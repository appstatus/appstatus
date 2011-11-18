package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.web.HtmlUtils;

public class BatchPage extends AbstractPage {
    private static final String ENCODING = "UTF-8";

    @Override
    public void doGet(AppStatus status, HttpServletRequest req, HttpServletResponse resp)
            throws UnsupportedEncodingException, IOException {

        setup(resp);
        ServletOutputStream os = resp.getOutputStream();
        begin(os);

        os.write("<h1>Batchs</h1>".getBytes(ENCODING));
        IBatchManager manager = status.getBatchManager();

        os.write("<h2>Running</h2>".getBytes(ENCODING));

        List<IBatch> runningBatches = manager.getRunningBatches();

        if (HtmlUtils.generateBeginTable(os, runningBatches.size())) {

            HtmlUtils.generateHeaders(os, "", "Id", "Category", "Name", "Start", "Progress", "End (est.)", "Status",
                    "Task", "Last Msg", "Items", "Rejected", "Last Update");

            for (IBatch batch : runningBatches) {
                HtmlUtils.generateRow(os, Icons.STATUS_JOB, batch.getUuid(), batch.getGroup(), batch.getName(),
                        batch.getStartDate(), Math.round(batch.getProgressStatus()) + "%", batch.getEndDate(),
                        batch.getStatus(), batch.getCurrentTask(), batch.getLastMessage(), batch.getItemCount(),
                        HtmlUtils.countAndDetail(batch.getRejectedItemsId()), batch.getLastUpdate());
            }

            HtmlUtils.generateEndTable(os, runningBatches.size());
        }

        os.write("<h2>Finished</h2>".getBytes(ENCODING));

        List<IBatch> finishedBatches = manager.getFinishedBatches();

        if (HtmlUtils.generateBeginTable(os, finishedBatches.size())) {

            HtmlUtils.generateHeaders(os, "", "Id", "Category", "Name", "Start", "Progress", "End (est.)", "Status",
                    "Task", "Last Msg", "Items", "Rejected", "Last Update", "");
            for (IBatch batch : finishedBatches) {
                HtmlUtils
                        .generateRow(
                                os,
                                Icons.STATUS_JOB,
                                batch.getUuid(),
                                batch.getGroup(),
                                batch.getName(),
                                batch.getStartDate(),
                                Math.round(batch.getProgressStatus()) + "%",
                                batch.getEndDate(),
                                batch.getStatus(),
                                batch.getCurrentTask(),
                                batch.getLastMessage(),
                                batch.getItemCount(),
                                HtmlUtils.countAndDetail(batch.getRejectedItemsId()),
                                batch.getLastUpdate(),
                                "<form action='?p=batch' method='post'><input type='submit' name='clear-item' value='Delete' /><input type=hidden name='item-uuid' value='"
                                        + batch.getUuid() + "'/></form>");
            }

            HtmlUtils.generateEndTable(os, finishedBatches.size());
        }
        os.write("<h2>With errors</h2>".getBytes(ENCODING));

        List<IBatch> errorBatches = manager.getErrorBatches();

        if (HtmlUtils.generateBeginTable(os, errorBatches.size())) {

            HtmlUtils.generateHeaders(os, "", "Id", "Category", "Name", "Start", "Progress", "End (est.)", "Status",
                    "Task", "Last Msg", "Items", "Rejected", "Last Update", "");

            for (IBatch batch : errorBatches) {
                HtmlUtils
                        .generateRow(
                                os,
                                Icons.STATUS_JOB,
                                batch.getUuid(),
                                batch.getGroup(),
                                batch.getName(),
                                batch.getStartDate(),
                                Math.round(batch.getProgressStatus()) + "%",
                                batch.getEndDate(),
                                batch.getStatus(),
                                batch.getCurrentTask(),
                                batch.getLastMessage(),
                                batch.getItemCount(),
                                HtmlUtils.countAndDetail(batch.getRejectedItemsId()),
                                batch.getLastUpdate(),
                                "<form action='?p=batch' method='post'><input type='submit' name='clear-item' value='Delete' /><input type=hidden name='item-uuid' value='"
                                        + batch.getUuid() + "'/></form>");
            }
            HtmlUtils.generateEndTable(os, errorBatches.size());
        }

        generateClearActions(resp);
        end(os);
    }

    @Override
    public void doPost(AppStatus status, HttpServletRequest req, HttpServletResponse resp) {
        if (req.getParameter("clear-old") != null) {
            status.getBatchManager().removeAllBatches(IBatchManager.REMOVE_OLD);
        } else if (req.getParameter("clear-success") != null) {
            status.getBatchManager().removeAllBatches(IBatchManager.REMOVE_SUCCESS);
        } else if (req.getParameter("clear-item") != null) {
            status.getBatchManager().removeBatch(req.getParameter("item-uuid"));
        }

    }

    private void generateClearActions(HttpServletResponse resp) throws IOException {
        resp.getOutputStream()
                .write("<p>Actions :</p><form action='?p=batch' method='post'><input type='submit' name='clear-old' value='Delete old (6 months)' /><input type='submit' name='clear-success' value='Delete Success w/o rejected'/></form>"
                        .getBytes());
    }

    @Override
    public String getId() {
        return "batch";
    }
}
