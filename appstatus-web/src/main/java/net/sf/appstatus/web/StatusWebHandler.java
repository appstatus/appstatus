/*
 * Copyright 2010-2012 Capgemini
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.sf.appstatus.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.AppStatusStatic;
import net.sf.appstatus.web.pages.Resources;

/**
 * Handle the Web UI of AppStatus.
 *
 * @author Olivier Lafon
 * @author Nicolas Richeton
 */
public class StatusWebHandler {
    private static Logger logger = LoggerFactory.getLogger(StatusWebHandler.class);
    private static final String STATUS_WEB_CONF_PROPERTIES = "/status-web-conf.properties";
    private String allowIp = null;
    private String applicationName = StringUtils.EMPTY;
    private AppStatus appStatus = null;
    private String cssLocation = null;
    private Map<String, IPage> pages = null;

    /**
     * Handle a GET request.
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if (allowIp != null) {
            if (!req.getRemoteAddr().equals(allowIp)) {
                resp.sendError(401, "IP not authorized");
                return;
            }
        }

        if (req.getParameter("icon") != null || req.getParameter("resource") != null) {
            Resources.doGet(this, req, resp);
            return;
        }

        if (req.getParameter("p") != null && pages.containsKey(req.getParameter("p"))) {
            pages.get(req.getParameter("p")).doGet(this, req, resp);

        } else {
            pages.get("status").doGet(this, req, resp);
        }
    }

    /**
     * Handle a POST request.
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (allowIp != null) {
            if (!req.getRemoteAddr().equals(allowIp)) {
                resp.sendError(401, "IP not authorized");
                return;
            }
        }

        if (req.getParameter("p") != null && pages.containsKey(req.getParameter("p"))) {
            pages.get(req.getParameter("p")).doPost(this, req, resp);

        } else {
            pages.get("status").doPost(this, req, resp);
        }

        doGet(req, resp);
    }

    public String getApplicationName() {
        return applicationName;
    }

    public AppStatus getAppStatus() {
        return appStatus;
    }

    public String getCssLocation() {
        return cssLocation;
    }

    public Map<String, IPage> getPages() {
        return pages;
    }

    /**
     * Does the initialization work.
     * <p>
     * Read configuration from /status-web-conf.properties
     * <p>
     * If you need to inject custom objects using these methods, please do it
     * before calling init.
     * <ul>
     * <li>{@link #setPages(Map)}</li>
     * <li>{@link #setAppStatus(AppStatus)}</li>
     * <li>{@link #setCssLocation(String)}</li>
     * </ul>
     */
    public void init() {

        // init AppStatus
        if (appStatus == null) {
            // Use default instance if not set
            appStatus = AppStatusStatic.getInstance();
        }
        appStatus.init();
        InputStream is = null;

        // Load specific configuration
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(STATUS_WEB_CONF_PROPERTIES);

            if (is == null) {
                logger.warn("/status-web-conf.properties not found in classpath. Using default configuration");
            } else {
                Properties p = new Properties();
                p.load(is);

                if (allowIp == null) {
                    allowIp = (String) p.get("ip.allow");
                }
            }
        } catch (Exception e) {
            logger.error("Error loading configuration from /status-web-conf.properties.", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.warn(String.format("Error in close resource [%s]", STATUS_WEB_CONF_PROPERTIES), e);
            }
        }

        // Init css & js
        if (cssLocation == null) {
            cssLocation = "?resource=appstatus.css";
            Resources.addResource("appstatus.css", "/assets/css/appstatus.css", "text/css");
            Resources.addResource("bootstrap.js", "/assets/js/bootstrap.js", "application/javascript");
            Resources.addResource("jquery.js", "/assets/js/jquery-2.0.1.min.js", "application/javascript");
            Resources.addResource("glyphicons-halflings.png", "/assets/img/glyphicons-halflings.png", "image/png");
            Resources.addResource("glyphicons-halflings-white.png", "/assets/img/glyphicons-halflings-white.png",
                    "image/png");
        }
    }

    /**
     * Restrict access to a single IP.
     *
     * @param allowIp
     */
    public void setAllowIp(String allowIp) {
        this.allowIp = allowIp;
    }

    public void setApplicationName(String servletContextName) {
        this.applicationName = servletContextName;
    }

    /**
     * Set the AppStatus object to use in the web interface.
     *
     * @param appStatus
     */
    public void setAppStatus(AppStatus appStatus) {
        this.appStatus = appStatus;
    }

    /**
     * Set the location of the css to use.
     *
     * @param cssLocation
     */
    public void setCssLocation(String cssLocation) {
        this.cssLocation = cssLocation;
    }

    /**
     * Set the available pages in the web interface.
     *
     * @param pages
     */
    public void setPages(Map<String, IPage> pages) {
        this.pages = pages;
    }
}
