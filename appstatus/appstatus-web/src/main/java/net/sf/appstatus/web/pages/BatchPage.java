package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.web.HtmlUtils;
import net.sf.appstatus.web.StatusWebHandler;

public class BatchPage extends AbstractPage {
	private static final String CLEAR_ITEM = "clear-item";
	private static final String CLEAR_OLD = "clear-old";
	private static final String CLEAR_SUCCESS = "clear-success";
	private static final String ENCODING = "UTF-8";
	private static final String ITEM_UUID = "item-uuid";

	@Override
	public void doGet(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		setup(resp, "text/html");
		ServletOutputStream os = resp.getOutputStream();
		begin(webHandler, os);

		os.write("<h2>Batchs</h2>".getBytes(ENCODING));
		IBatchManager manager = webHandler.getAppStatus().getBatchManager();

		os.write("<h3>Running</h3>".getBytes(ENCODING));

		List<IBatch> runningBatches = manager.getRunningBatches();

		if (HtmlUtils.generateBeginTable(os, runningBatches.size())) {

			HtmlUtils.generateHeaders(os, "", "Id", "Group", "Name", "Start", "Progress", "End (est.)", "Status",
					"Task", "Last Msg", "Items", "Rejected", "Last Update");

			for (IBatch batch : runningBatches) {
				HtmlUtils.generateRow(os, getIcon(batch), generateId(resp, batch.getUuid()), batch.getGroup(),
						batch.getName(), batch.getStartDate(), getProgressBar(Math.round(batch.getProgressStatus())),
						batch.getEndDate(), batch.getStatus(), batch.getCurrentTask(), batch.getLastMessage(),
						batch.getItemCount(), HtmlUtils.countAndDetail(batch.getRejectedItemsId()),
						batch.getLastUpdate());
			}

			HtmlUtils.generateEndTable(os, runningBatches.size());
		}

		os.write("<h3>Finished</h3>".getBytes(ENCODING));

		List<IBatch> finishedBatches = manager.getFinishedBatches();

		if (HtmlUtils.generateBeginTable(os, finishedBatches.size())) {

			HtmlUtils.generateHeaders(os, "", "Id", "Group", "Name", "Start", "Progress", "End", "Status", "Task",
					"Last Msg", "Items", "Rejected", "Last Update", "");
			for (IBatch batch : finishedBatches) {
				HtmlUtils.generateRow(os, getIcon(batch), generateId(resp, batch.getUuid()), batch.getGroup(),
						batch.getName(), batch.getStartDate(), getProgressBar(Math.round(batch.getProgressStatus())),
						batch.getEndDate(), batch.getStatus(), batch.getCurrentTask(), batch.getLastMessage(),
						batch.getItemCount(), HtmlUtils.countAndDetail(batch.getRejectedItemsId()),
						batch.getLastUpdate(), "<form action='?p=batch' method='post'><input type='submit' name='"
								+ CLEAR_ITEM + "' value='Delete' /><input type=hidden name='" + ITEM_UUID + "' value='"
								+ batch.getUuid() + "'/></form>");
			}

			HtmlUtils.generateEndTable(os, finishedBatches.size());
		}
		os.write("<h3>With errors</h3>".getBytes(ENCODING));

		List<IBatch> errorBatches = manager.getErrorBatches();

		if (HtmlUtils.generateBeginTable(os, errorBatches.size())) {

			HtmlUtils.generateHeaders(os, "", "Id", "Group", "Name", "Start", "Progress", "End", "Status", "Task",
					"Last Msg", "Items", "Rejected", "Last Update", "");

			for (IBatch batch : errorBatches) {
				HtmlUtils.generateRow(os, getIcon(batch), generateId(resp, batch.getUuid()), batch.getGroup(),
						batch.getName(), batch.getStartDate(), getProgressBar(Math.round(batch.getProgressStatus())),
						batch.getEndDate(), batch.getStatus(), batch.getCurrentTask(), batch.getLastMessage(),
						batch.getItemCount(), HtmlUtils.countAndDetail(batch.getRejectedItemsId()),
						batch.getLastUpdate(), "<form action='?p=batch' method='post'><input type='submit' name='"
								+ CLEAR_ITEM + "' value='Delete' /><input type=hidden name='" + ITEM_UUID + "' value='"
								+ batch.getUuid() + "'/></form>");
			}
			HtmlUtils.generateEndTable(os, errorBatches.size());
		}

		generateClearActions(resp);
		end(os);
	}

	@Override
	public void doPost(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp) {
		if (req.getParameter(CLEAR_OLD) != null) {
			webHandler.getAppStatus().getBatchManager().removeAllBatches(IBatchManager.REMOVE_OLD);
		} else if (req.getParameter(CLEAR_SUCCESS) != null) {
			webHandler.getAppStatus().getBatchManager().removeAllBatches(IBatchManager.REMOVE_SUCCESS);
		} else if (req.getParameter(CLEAR_ITEM) != null) {
			webHandler.getAppStatus().getBatchManager().removeBatch(req.getParameter(ITEM_UUID));
		}

	}

	private void generateClearActions(HttpServletResponse resp) throws IOException {
		resp.getOutputStream()
				.write(("<p>Actions :</p><form action='?p=batch' method='post'><input type='submit' name='" + CLEAR_OLD
						+ "' value='Delete old (6 months)' class='btn'/> <input type='submit' name='" + CLEAR_SUCCESS + "' value='Delete Success w/o rejected' class='btn'/></form>")
						.getBytes());
	}

	private String generateId(HttpServletResponse resp, String id) throws IOException {

		if (id == null) {
			return "";
		}

		if (id.length() < 15) {
			return id;
		} else {
			return "<span title='" + id + "'>" + id.substring(0, 10) + "...</span";
		}

	}

	private String getIcon(IBatch b) {

		if (IBatch.STATUS_FAILURE.equals(b.getStatus())) {
			return Resources.STATUS_JOB_ERROR;
		}

		if (IBatch.STATUS_SUCCESS.equals(b.getStatus()) && b.getRejectedItemsId() != null
				&& b.getRejectedItemsId().size() > 0) {
			return Resources.STATUS_JOB_WARNING;
		}

		return Resources.STATUS_JOB;

	}

	@Override
	public String getId() {
		return "batch";
	}

	@Override
	public String getName() {
		return "Batch";
	}

	String getProgressBar(int percent) {
		String status = "";
		if (percent < 100) {
			status = "active";
		}

		if (percent == 100) {
			status = "progress-success";
		}

		return "<div class=\"progress progress-striped " + status + "\">" + "<div class=\"bar\" style=\"width: "
				+ percent + "%;\"></div>" + "</div>";
	}
}
