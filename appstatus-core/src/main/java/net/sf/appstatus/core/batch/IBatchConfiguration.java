package net.sf.appstatus.core.batch;

import java.util.Date;

public interface IBatchConfiguration {

	/**
	 * @return the group
	 */
	public String getGroup();

	/**
	 * @return the lastExecution
	 */
	public Date getLastExecution();

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * @return the nextExecution
	 */
	public Date getNextExecution();

	/**
	 * @return the schedule
	 */
	public String getSchedule();

	/**
	 * @param nextExecution
	 *            the nextExecution to set
	 */
	public void setLastExecution(Date startDate);
}
