package net.sf.appstatus.core.batch;

import java.io.Serializable;
import java.util.Date;

/**
 * Object representation of a batch execution's configuration.
 *
 * @author Idriss Neumann
 *
 */
public class BatchConfiguration implements Serializable, IBatchConfiguration {
	private static final long serialVersionUID = 1L;

	private String group;

	private Date lastExecution;

	private final IBatchScheduleManager manager;

	private String name;

	private String schedule;

	public BatchConfiguration(IBatchScheduleManager manager) {
		this.manager = manager;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @return the lastExecution
	 */
	public Date getLastExecution() {
		return lastExecution;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the nextExecution
	 */
	public Date getNextExecution() {
		return manager.getNextDate(schedule, new Date());
	}

	/**
	 * @return the schedule
	 */
	public String getSchedule() {
		return schedule;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @param lastExecution
	 *            the lastExecution to set
	 */
	public void setLastExecution(Date lastExecution) {
		this.lastExecution = lastExecution;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param schedule
	 *            the schedule to set
	 */
	public void setSchedule(String executionExpr) {
		this.schedule = executionExpr;
	}
}
