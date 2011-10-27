package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.web.HtmlUtils;

public class BatchPage extends AbstractPage {
	private static final String ENCODING = "UTF-8";

	public static void render(AppStatus status, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		setup(resp);
		ServletOutputStream os = resp.getOutputStream();
		begin(os);

		os.write("<h1>Batchs</h1>".getBytes(ENCODING));
		IBatchManager manager = status.getBatchManager();

		os.write("<h2>Running</h2>".getBytes(ENCODING));

		List<IBatch> runningBatches = manager.getRunningBatches();

		if (HtmlUtils.generateBeginTable(os, runningBatches.size())) {

			HtmlUtils.generateHeaders(os, "", "Category", "Name", "Start",
					"Progress", "End (est.)", "Status", "Task", "Last Msg",
					"Items", "Rejected", "Last Update");

			for (IBatch batch : runningBatches) {
				HtmlUtils.generateRow(os, Icons.STATUS_JOB, batch.getGroup(),
						batch.getName(), batch.getStartDate(),
						batch.getProgressStatus() + "%", batch.getEndDate(),
						batch.getStatus(), batch.getCurrentTask(),
						batch.getLastMessage(), batch.getItemCount(),
						HtmlUtils.countAndDetail(batch.getRejectedItemsId()),
						batch.getLastUpdate());
			}

			HtmlUtils.generateEndTable(os, runningBatches.size());
		}

		os.write("<h2>Finished</h2>".getBytes(ENCODING));

		List<IBatch> finishedBatches = manager.getFinishedBatches();

		if (HtmlUtils.generateBeginTable(os, finishedBatches.size())) {

			HtmlUtils.generateHeaders(os, "", "Category", "Name", "Start",
					"Progress", "End (est.)", "Status", "Task", "Last Msg",
					"Items", "Rejected", "Last Update");
			for (IBatch batch : finishedBatches) {
				HtmlUtils.generateRow(os, Icons.STATUS_JOB, batch.getGroup(),
						batch.getName(), batch.getStartDate(),
						batch.getProgressStatus() + "%", batch.getEndDate(),
						batch.getStatus(), batch.getCurrentTask(),
						batch.getLastMessage(), batch.getItemCount(),
						HtmlUtils.countAndDetail(batch.getRejectedItemsId()),
						batch.getLastUpdate());
			}

			HtmlUtils.generateEndTable(os, finishedBatches.size());
		}
		os.write("<h2>With errors</h2>".getBytes(ENCODING));

		List<IBatch> errorBatches = manager.getErrorBatches();

		if (HtmlUtils.generateBeginTable(os, errorBatches.size())) {

			HtmlUtils.generateHeaders(os, "", "Category", "Name", "Start",
					"Progress", "End (est.)", "Status", "Task", "Last Msg",
					"Items", "Rejected", "Last Update");

			for (IBatch batch : errorBatches) {
				HtmlUtils.generateRow(os, Icons.STATUS_JOB, batch.getGroup(),
						batch.getName(), batch.getStartDate(),
						batch.getProgressStatus() + "%", batch.getEndDate(),
						batch.getStatus(), batch.getCurrentTask(),
						batch.getLastMessage(), batch.getItemCount(),
						HtmlUtils.countAndDetail(batch.getRejectedItemsId()),
						batch.getLastUpdate());
			}
			HtmlUtils.generateEndTable(os, errorBatches.size());
		}
		end(os);
	}
}
