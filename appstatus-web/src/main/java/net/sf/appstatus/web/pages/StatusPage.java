/*
 * Copyright SCN Guichet Entreprises, Capgemini and contributors (2016-2017)
 *
 * This software is a computer program whose purpose is to maintain and
 * administrate standalone forms.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.web.HtmlUtils;
import net.sf.appstatus.web.StatusWebHandler;

public class StatusPage extends AbstractPage {

    private static Logger LOGGER = LoggerFactory.getLogger(StatusPage.class);

    private static final String ENCODING = "UTF-8";

    private static final String PAGECONTENTLAYOUT = "statusContentLayout.html";

    /**
     * Returns status icon id.
     *
     * @param result
     * @return
     */
    private static String getStatus(final ICheckResult result) {

        if (result.getCode() == ICheckResult.OK) {
            return Resources.STATUS_OK;
        }

        if (result.isFatal()) {
            return Resources.STATUS_ERROR;
        }

        return Resources.STATUS_WARN;
    }

    @Override
    public void doGet(final StatusWebHandler webHandler, final HttpServletRequest req, final HttpServletResponse resp) throws UnsupportedEncodingException, IOException {

        if (req.getParameter("json") != null) {
            doGetJSON(webHandler, req, resp);
        } else {
            doGetHTML(webHandler, req, resp);
        }

    }

    public void doGetHTML(final StatusWebHandler webHandler, final HttpServletRequest req, final HttpServletResponse resp) throws UnsupportedEncodingException, IOException {

        setup(resp, "text/html");
        final ServletOutputStream os = resp.getOutputStream();

        final AppStatus appStatus = webHandler.getAppStatus();

        final Map<String, String> valuesMap = new HashMap<String, String>();
        final List<ICheckResult> results = appStatus.checkAll(req.getLocale());
        Collections.sort(results);
        boolean statusOk = true;
        int statusCode = 200;
        for (final ICheckResult r : results) {
            if (r.getCode() != ICheckResult.OK && r.isFatal()) {
                resp.setStatus(500);
                statusCode = 500;
                statusOk = false;
                break;
            }
        }

        if (appStatus.isMaintenanceMode()) {
            resp.setStatus(503);
            statusCode = 503;
            valuesMap.put("maintenanceModeActive", "on");
            valuesMap.put("nextMode", "available");
        } else {
            valuesMap.put("maintenanceModeActive", "off");
            valuesMap.put("nextMode", "maintenance");
        }

        valuesMap.put("statusOk", String.valueOf(statusOk));
        valuesMap.put("statusCode", String.valueOf(statusCode));

        // STATUS TABLE
        final StrBuilder sbStatusTable = new StrBuilder();
        if (HtmlUtils.generateBeginTable(sbStatusTable, results.size())) {

            HtmlUtils.generateHeaders(sbStatusTable, "", "Group", "Name", "Description", "Code", "Resolution");

            for (final ICheckResult r : results) {
                HtmlUtils.generateRow(sbStatusTable, getStatus(r), r.getGroup(), r.getProbeName(), r.getDescription(), String.valueOf(r.getCode()), r.getResolutionSteps());
            }
            HtmlUtils.generateEndTable(sbStatusTable, results.size());
        }
        valuesMap.put("statusTable", sbStatusTable.toString());

        // PROPERTIES TABLE
        final StrBuilder sbPropertiesTable = new StrBuilder();
        final Map<String, Map<String, String>> properties = appStatus.getProperties();
        if (HtmlUtils.generateBeginTable(sbPropertiesTable, properties.size())) {

            HtmlUtils.generateHeaders(sbPropertiesTable, "", "Group", "Name", "Value");

            for (final Entry<String, Map<String, String>> cat : properties.entrySet()) {
                final String category = cat.getKey();

                for (final Entry<String, String> r : cat.getValue().entrySet()) {
                    HtmlUtils.generateRow(sbPropertiesTable, Resources.STATUS_PROP, category, r.getKey(), r.getValue());
                }

            }
            HtmlUtils.generateEndTable(sbPropertiesTable, properties.size());
        }
        valuesMap.put("propertiesTable", sbPropertiesTable.toString());
        final String content = HtmlUtils.applyLayout(valuesMap, PAGECONTENTLAYOUT);

        valuesMap.clear();
        valuesMap.put("content", content);
        os.write(getPage(webHandler, valuesMap).getBytes(ENCODING));
    }

    public void doGetJSON(final StatusWebHandler webHandler, final HttpServletRequest req, final HttpServletResponse resp) throws UnsupportedEncodingException, IOException {

        setup(resp, "application/json");

        final AppStatus appStatus = webHandler.getAppStatus();

        final ServletOutputStream os = resp.getOutputStream();
        int statusCode = 200;
        final List<ICheckResult> results = appStatus.checkAll(req.getLocale());
        for (final ICheckResult r : results) {
            if (r.isFatal()) {
                resp.setStatus(500);
                statusCode = 500;
                break;
            }
        }

        if (appStatus.isMaintenanceMode()) {
            resp.setStatus(503);
            statusCode = 503;
        }

        os.write("{".getBytes(ENCODING));

        os.write(("\"code\" : " + statusCode + ",").getBytes(ENCODING));
        os.write(("\"status\" : {").getBytes(ENCODING));

        boolean first = true;
        for (final ICheckResult r : results) {
            if (!first) {
                os.write((",").getBytes(ENCODING));
            }

            os.write(("\"" + r.getProbeName() + "\" : " + r.getCode()).getBytes(ENCODING));

            if (first) {
                first = false;
            }
        }

        os.write("}".getBytes(ENCODING));
        os.write("}".getBytes(ENCODING));

    }

    @Override
    public void doPost(final StatusWebHandler webHandler, final HttpServletRequest req, final HttpServletResponse resp) {
        updateMode(webHandler, req.getParameter("mode"));
        try {
            resp.sendRedirect(req.getRequestURI());
        } catch (final IOException ex) {
            LOGGER.warn("Unable to redirect to main status page after updating mode", ex);
        }
    }

    private void updateMode(final StatusWebHandler webHandler, final String mode) {
        if (null != mode) {
            webHandler.getAppStatus().setMaintenanceMode("maintenance".equalsIgnoreCase(mode));
        }
    }

    @Override
    public String getId() {
        return "status";
    }

    @Override
    public String getName() {
        return "Status";
    }
}
