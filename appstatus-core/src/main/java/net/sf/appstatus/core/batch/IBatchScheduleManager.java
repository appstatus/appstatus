package net.sf.appstatus.core.batch;

import java.util.Date;
import java.util.List;

/**
 * Interface which must be implemented to read a set of execution expr.
 *
 * @author Idriss Neumann
 *
 */
public interface IBatchScheduleManager {
	/**
	 * Getting the batch's configurations.
	 */
	List<IBatchConfiguration> getBatchConfigurations();

	/**
	 * Convert the execution schedule (cron for example).
	 *
	 * @param schedule
	 *            Schedule string
	 * @param lastExecution
	 * @return the next date which correspond to the schedule
	 */
	Date getNextDate(String schedule, Date lastExecution);
}
