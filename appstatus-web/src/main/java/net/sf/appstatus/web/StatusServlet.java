/*
 * Copyright 2010 Capgemini
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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.IServletContextProvider;
import net.sf.appstatus.StatusService;
import net.sf.appstatus.check.impl.StatusResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusServlet extends HttpServlet {

  private static final String ENCODING = "UTF-8";
  private static Logger logger = LoggerFactory.getLogger(StatusService.class);
  private static final long serialVersionUID = 3912325072098291029L;
  private static StatusService status = null;
  private static final String STATUS_ERROR = "error";
  private static final String STATUS_OK = "ok";
  private static final String STATUS_PROP = "prop";
  private static final String STATUS_WARN = "warn";
  private String allow = null;
  private final String styleSheet = "<style type=\"text/css\" media=\"screen\">" + "table { font-size: 80%; }"
      + "table ,th, td {  border: 1px solid black; border-collapse:collapse;}" + "th { background-color: #DDDDDD; }"
      + "</style>";

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
   * , javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    if (allow != null) {
      if (!req.getRemoteAddr().equals(allow)) {
        resp.sendError(401, "IP not authorized");
        return;
      }
    }

    if (req.getParameter("icon") != null) {
      doGetResource(req.getParameter("icon"), req, resp);
      return;
    }

    long start = System.currentTimeMillis();
    System.err.println("start " + System.currentTimeMillis());
    List<StatusResult> results = status.checkAll();
    System.err.println("end " + (System.currentTimeMillis() - start));

    boolean statusOk = true;
    int statusCode = 200;
    for (StatusResult r : results) {
      if (r.isFatal()) {
        resp.setStatus(500);
        statusCode = 500;
        statusOk = false;
        break;
      }
    }

    resp.setContentType("text/html");
    resp.setCharacterEncoding(ENCODING);

    PrintWriter writer = resp.getWriter();
    writer.println("<html><head>");
    writer.println(styleSheet);
    writer.println("<body>");

    // THE STATUS :
    writer.println("<h1>Status Page</h1>");
    writer.print("<p>Online:");
    writer.print(statusOk);
    writer.println("</p>");
    writer.println("<p>Code:" + statusCode + "</p>");

    writer.println("<h2>Status</h2>");
    writer.println("<table>");
    writer.println("<tr><th></th><th>Name</th><th>Description</th><th>Code</th><th>Resolution</th></tr>");

    for (StatusResult r : results) {
      generateRow(writer, getStatus(r), r.getProbeName(), r.getDescription(), String.valueOf(r.getCode()),
          r.getResolutionSteps());
    }
    writer.println("</table>");

    // THE PROPERTIES :
    writer.println("<h2>Properties</h2>");
    writer.println("<table>");
    writer.println("<tr><th></th><th>Category</th><th>Name</th><th>Value</th></tr>");

    Map<String, Map<String, String>> properties = status.getProperties();
    for (Entry<String, Map<String, String>> cat : properties.entrySet()) {
      String category = cat.getKey();

      for (Entry<String, String> r : cat.getValue().entrySet()) {
        generateRow(writer, STATUS_PROP, category, r.getKey(), r.getValue());
      }

    }

    writer.print("</table>");
    writer.print("</body></html>");
  }

  /**
   * Serve icons
   * 
   * @param id
   * @param req
   * @param resp
   * @throws IOException
   */
  protected void doGetResource(String id, HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String location = null;
    if (STATUS_OK.equals(id)) {
      location = "/org/freedesktop/tango/22x22/status/weather-clear.png";
    } else if (STATUS_WARN.equals(id)) {
      location = "/org/freedesktop/tango/22x22/status/weather-overcast.png";
    } else if (STATUS_ERROR.equals(id)) {
      location = "/org/freedesktop/tango/22x22/status/weather-severe-alert.png";
    } else if (STATUS_PROP.equals(id)) {
      location = "/org/freedesktop/tango/22x22/actions/format-justify-fill.png";
    }

    InputStream is = this.getClass().getResourceAsStream(location);
    OutputStream os = resp.getOutputStream();
    IOUtils.copy(is, os);
  }

  /**
   * Outputs one table row
   * 
   * @param os
   * @param status
   * @param cols
   * @throws IOException
   */
  private void generateRow(PrintWriter writer, String status, Object... cols) throws IOException {
    writer.print("<tr>");

    writer.print(("<td><img src='?icon=" + status + "'></td>"));

    for (Object obj : cols) {

      if (obj != null) {
        writer.print("<td>");
        writer.print(obj.toString());
        writer.print("</td>");
      } else {
        writer.print("<td></td>");
      }

    }
    writer.print("</tr>");
  }

  /**
   * Returns status icon id.
   * 
   * @param result
   * @return
   */
  private String getStatus(StatusResult result) {

    if (result.isFatal()) {
      return STATUS_ERROR;
    }

    if (result.getCode() == StatusResult.OK) {
      return STATUS_OK;
    }

    return STATUS_WARN;
  }

  @Override
  public void init() throws ServletException {
    super.init();
    boolean useSpring = false;
    try {
      InputStream is = StatusServlet.class.getResourceAsStream("/status-web-conf.properties");

      if (is == null) {
        logger.warn("/status-web-conf.properties not found in classpath. Using default configuration");
      } else {
        Properties p = new Properties();
        p.load(is);
        is.close();
        allow = (String) p.get("ip.allow");
        useSpring = Boolean.parseBoolean((String) p.get("useSpring"));
      }
    } catch (Exception e) {
      logger.error("Error loading configuration from /status-web-conf.properties.", e);
    }

    status = new StatusService();

    if (useSpring) {
      status.setObjectInstanciationListener(new SpringObjectInstantiationListener(this.getServletContext()));
    }

    status.setServletContextProvider(new IServletContextProvider() {
      public ServletContext getServletContext() {
        return StatusServlet.this.getServletContext();
      }
    });
    status.init();
  }
}
