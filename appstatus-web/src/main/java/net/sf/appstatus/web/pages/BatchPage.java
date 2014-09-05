/*
 * Copyright 2010-2013 Capgemini Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.web.HtmlUtils;
import net.sf.appstatus.web.StatusWebHandler;

import org.apache.commons.lang3.text.StrBuilder;

public class BatchPage extends AbstractPage {
	private static final String CLEAR_ITEM = "clear-item";
	private static final String CLEAR_OLD = "clear-old";
	private static final String CLEAR_SUCCESS = "clear-success";
	private static final String ENCODING = "UTF-8";
	private static final String ITEM_UUID = "item-uuid";
	private static final String PAGECONTENTLAYOUT = "batchesContentLayout.html";

	@Override
	public void doGet(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		setup(resp, "text/html");
		ServletOutputStream os = resp.getOutputStream();
		Map<String, String> valuesMap = new HashMap<String, String>();
		StrBuilder sbRunningBatchesBatchesTable = new StrBuilder();
		StrBuilder sbFinishedBatchesBatchesTable = new StrBuilder();
		StrBuilder sbErrorsBatchesBatchesTable = new StrBuilder();

		IBatchManager manager = webHandler.getAppStatus().getBatchManager();
		List<IBatch> runningBatches = manager.getRunningBatches();
		if (HtmlUtils.generateBeginTable(sbRunningBatchesBatchesTable, runningBatches.size())) {

			HtmlUtils.generateHeaders(sbRunningBatchesBatchesTable, "", "Id", "Group", "Name", "Start", "Progress",
					"End (est.)", "Status", "Task", "Last Msg", "Items", "Rejected", "Last Update");

			for (IBatch batch : runningBatches) {
				HtmlUtils.generateRow(sbRunningBatchesBatchesTable, getIcon(batch), generateId(resp, batch.getUuid()),
						batch.getGroup(), batch.getName(), batch.getStartDate(), getProgressBar(batch),
						batch.getEndDate(), batch.getStatus(), batch.getCurrentTask(), batch.getLastMessage(),
						batch.getItemCount(), HtmlUtils.countAndDetail(batch.getRejectedItemsId()),
						batch.getLastUpdate());
			}

			HtmlUtils.generateEndTable(sbRunningBatchesBatchesTable, runningBatches.size());
		}

		List<IBatch> finishedBatches = manager.getFinishedBatches();

		if (HtmlUtils.generateBeginTable(sbFinishedBatchesBatchesTable, finishedBatches.size())) {

			HtmlUtils.generateHeaders(sbFinishedBatchesBatchesTable, "", "Id", "Group", "Name", "Start", "Progress",
					"End", "Status", "Task", "Last Msg", "Items", "Rejected", "Last Update", "");
			for (IBatch batch : finishedBatches) {
				HtmlUtils.generateRow(sbFinishedBatchesBatchesTable, getIcon(batch), generateId(resp, batch.getUuid()),
						batch.getGroup(), batch.getName(), batch.getStartDate(), getProgressBar(batch),
						batch.getEndDate(), batch.getStatus(), batch.getCurrentTask(), batch.getLastMessage(),
						batch.getItemCount(), HtmlUtils.countAndDetail(batch.getRejectedItemsId()),
						batch.getLastUpdate(), "<form action='?p=batch' method='post'><input type='submit' name='"
								+ CLEAR_ITEM + "' value='Delete'  class='btn btn-small' /><input type=hidden name='"
								+ ITEM_UUID + "' value='" + batch.getUuid() + "'/></form>");
			}

			HtmlUtils.generateEndTable(sbFinishedBatchesBatchesTable, finishedBatches.size());
		}

		List<IBatch> errorBatches = manager.getErrorBatches();

		if (HtmlUtils.generateBeginTable(sbErrorsBatchesBatchesTable, errorBatches.size())) {

			HtmlUtils.generateHeaders(sbErrorsBatchesBatchesTable, "", "Id", "Group", "Name", "Start", "Progress",
					"End", "Status", "Task", "Last Msg", "Items", "Rejected", "Last Update", "");

			for (IBatch batch : errorBatches) {
				HtmlUtils.generateRow(sbErrorsBatchesBatchesTable, getIcon(batch), generateId(resp, batch.getUuid()),
						batch.getGroup(), batch.getName(), batch.getStartDate(), getProgressBar(batch),
						batch.getEndDate(), batch.getStatus(), batch.getCurrentTask(), batch.getLastMessage(),
						batch.getItemCount(), HtmlUtils.countAndDetail(batch.getRejectedItemsId()),
						batch.getLastUpdate(), "<form action='?p=batch' method='post'><input type='submit' name='"
								+ CLEAR_ITEM + "' value='Delete' class='btn btn-small'/><input type=hidden name='"
								+ ITEM_UUID + "' value='" + batch.getUuid() + "'/></form>");
			}
			HtmlUtils.generateEndTable(sbErrorsBatchesBatchesTable, errorBatches.size());
		}

		valuesMap.put("runningBatchesBatchesTable", sbRunningBatchesBatchesTable.toString());
		valuesMap.put("finishedBatchesBatchesTable", sbFinishedBatchesBatchesTable.toString());
		valuesMap.put("errorsBatchesBatchesTable", sbErrorsBatchesBatchesTable.toString());
		valuesMap.put("clearActions", generateClearActions());

		String content = HtmlUtils.applyLayout(valuesMap, PAGECONTENTLAYOUT);

		valuesMap.clear();
		valuesMap.put("content", content);

		os.write(getPage(webHandler, valuesMap).getBytes(ENCODING));
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

	private String generateClearActions() throws IOException {
		StrBuilder sb = new StrBuilder();
		sb.append("<p>Actions :</p><form action='?p=batch' method='post'><input type='submit' name='" + CLEAR_OLD
				+ "' value='Delete old (6 months)' class='btn'/> <input type='submit' name='" + CLEAR_SUCCESS
				+ "' value='Delete Success w/o rejected' class='btn'/></form>");
		return sb.toString();
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

	String getProgressBar(IBatch batch) {

		String color = "success";
		if (batch.getRejectedItemsId() != null && batch.getRejectedItemsId().size() > 0) {
			color = "warning";
		}
		if (IBatch.STATUS_FAILURE.equals(batch.getStatus())) {
			color = "danger";
		}

		int percent = Math.round(batch.getProgressStatus());

		String status = "";
		if (percent < 100) {
			status = "active progress-" + color;
		}

		if (percent == 100) {
			status = "progress-" + color;
		}

		return "<div class=\"progress progress-striped " + status + "\">" + "<div class=\"bar\" style=\"width: "
				+ percent + "%;\"></div>" + "</div>";
	}
}
