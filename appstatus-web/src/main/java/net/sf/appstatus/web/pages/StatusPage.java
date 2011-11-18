package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.web.HtmlUtils;

public class StatusPage extends AbstractPage {
    private static final String ENCODING = "UTF-8";

    /**
     * Returns status icon id.
     * 
     * @param result
     * @return
     */
    private static String getStatus(ICheckResult result) {

        if (result.isFatal()) {
            return Icons.STATUS_ERROR;
        }

        if (result.getCode() == ICheckResult.OK) {
            return Icons.STATUS_OK;
        }

        return Icons.STATUS_WARN;
    }

    @Override
    public void doGet(AppStatus status, HttpServletRequest req, HttpServletResponse resp)
            throws UnsupportedEncodingException, IOException {

        setup(resp);
        ServletOutputStream os = resp.getOutputStream();
        begin(os);

        List<ICheckResult> results = status.checkAll();
        boolean statusOk = true;
        int statusCode = 200;
        for (ICheckResult r : results) {
            if (r.isFatal()) {
                resp.setStatus(500);
                statusCode = 500;
                statusOk = false;
                break;
            }
        }

        os.write("<h1>Status Page</h1>".getBytes(ENCODING));
        os.write(("<p>Online:" + statusOk + "</p>").getBytes(ENCODING));
        os.write(("<p>Code:" + statusCode + "</p>").getBytes(ENCODING));

        os.write("<h2 class=\"status\">Status</h2>".getBytes(ENCODING));
        if (HtmlUtils.generateBeginTable(os, results.size())) {

            HtmlUtils.generateHeaders(os, "", "Name", "Description", "Code", "Resolution");

            for (ICheckResult r : results) {
                HtmlUtils.generateRow(os, getStatus(r), r.getProbeName(), r.getDescription(),
                        String.valueOf(r.getCode()), r.getResolutionSteps());
            }
            HtmlUtils.generateEndTable(os, results.size());
        }

        os.write("<h2 class=\"properties\">Properties</h2>".getBytes(ENCODING));
        Map<String, Map<String, String>> properties = status.getProperties();
        if (HtmlUtils.generateBeginTable(os, properties.size())) {

            HtmlUtils.generateHeaders(os, "", "Category", "Name", "Value");

            for (Entry<String, Map<String, String>> cat : properties.entrySet()) {
                String category = cat.getKey();

                for (Entry<String, String> r : cat.getValue().entrySet()) {
                    HtmlUtils.generateRow(os, Icons.STATUS_PROP, category, r.getKey(), r.getValue());
                }

            }
            HtmlUtils.generateEndTable(os, properties.size());
        }

        end(os);
    }

    @Override
    public void doPost(AppStatus status, HttpServletRequest req, HttpServletResponse resp) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getId() {
        return "status";
    }
}