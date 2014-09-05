package net.sf.appstatus.batch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.sf.appstatus.core.AppStatusStatic;
import net.sf.appstatus.core.batch.AbstractBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * https://sourceforge.net/apps/mantisbt/appstatus/view.php?id=36
 * 
 * @author Guillaume Mary
 * @author Nicolas Richeton
 */
public class ProgressOverOneHundredPercentTest {

	/**
	 * Custom logger to catch progress in logs and check if lte 100%.
	 * <p>
	 * Expect that progress is logged using info( format, objects)
	 * <p>
	 * Set progressCheckedWhenLogged to true, if progress has be found and
	 * checked at least once.
	 * 
	 */
	private final Logger logger = new Logger() {

		public void debug(Marker marker, String msg) {
			// TODO Auto-generated method stub

		}

		public void debug(Marker marker, String format, Object arg) {
			// TODO Auto-generated method stub

		}

		public void debug(Marker marker, String format, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

		public void debug(Marker marker, String format, Object[] argArray) {
			// TODO Auto-generated method stub

		}

		public void debug(Marker marker, String msg, Throwable t) {
			// TODO Auto-generated method stub

		}

		public void debug(String msg) {
			// TODO Auto-generated method stub

		}

		public void debug(String format, Object arg) {
			// TODO Auto-generated method stub

		}

		public void debug(String format, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

		public void debug(String format, Object[] argArray) {
			// TODO Auto-generated method stub

		}

		public void debug(String msg, Throwable t) {
			// TODO Auto-generated method stub

		}

		public void error(Marker marker, String msg) {
			// TODO Auto-generated method stub

		}

		public void error(Marker marker, String format, Object arg) {
			// TODO Auto-generated method stub

		}

		public void error(Marker marker, String format, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

		public void error(Marker marker, String format, Object[] argArray) {
			// TODO Auto-generated method stub

		}

		public void error(Marker marker, String msg, Throwable t) {
			// TODO Auto-generated method stub

		}

		public void error(String msg) {
			// TODO Auto-generated method stub

		}

		public void error(String format, Object arg) {
			// TODO Auto-generated method stub

		}

		public void error(String format, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

		public void error(String format, Object[] argArray) {
			// TODO Auto-generated method stub

		}

		public void error(String msg, Throwable t) {
			// TODO Auto-generated method stub

		}

		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		public void info(Marker marker, String msg) {
			// TODO Auto-generated method stub

		}

		public void info(Marker marker, String format, Object arg) {
			// TODO Auto-generated method stub

		}

		public void info(Marker marker, String format, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

		public void info(Marker marker, String format, Object[] argArray) {
			// TODO Auto-generated method stub

		}

		public void info(Marker marker, String msg, Throwable t) {
			// TODO Auto-generated method stub

		}

		public void info(String msg) {

		}

		public void info(String format, Object arg) {
			// TODO Auto-generated method stub

		}

		public void info(String format, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

		public void info(String format, Object[] argArray) {

			if (format.contains("progress")) {
				progressCheckedWhenLogged = true;
				Float progress = (Float) argArray[2];

				if (progress > 100) {
					throw new RuntimeException("Progress over 100%");
				}
			}
		}

		public void info(String msg, Throwable t) {
			// TODO Auto-generated method stub

		}

		public boolean isDebugEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isDebugEnabled(Marker marker) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isErrorEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isErrorEnabled(Marker marker) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isInfoEnabled() {
			return true;
		}

		public boolean isInfoEnabled(Marker marker) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isTraceEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isTraceEnabled(Marker marker) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isWarnEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isWarnEnabled(Marker marker) {
			// TODO Auto-generated method stub
			return false;
		}

		public void trace(Marker marker, String msg) {
			// TODO Auto-generated method stub

		}

		public void trace(Marker marker, String format, Object arg) {
			// TODO Auto-generated method stub

		}

		public void trace(Marker marker, String format, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

		public void trace(Marker marker, String format, Object[] argArray) {
			// TODO Auto-generated method stub

		}

		public void trace(Marker marker, String msg, Throwable t) {
			// TODO Auto-generated method stub

		}

		public void trace(String msg) {
			// TODO Auto-generated method stub

		}

		public void trace(String format, Object arg) {
			// TODO Auto-generated method stub

		}

		public void trace(String format, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

		public void trace(String format, Object[] argArray) {
			// TODO Auto-generated method stub

		}

		public void trace(String msg, Throwable t) {
			// TODO Auto-generated method stub

		}

		public void warn(Marker marker, String msg) {
			// TODO Auto-generated method stub

		}

		public void warn(Marker marker, String format, Object arg) {
			// TODO Auto-generated method stub

		}

		public void warn(Marker marker, String format, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

		public void warn(Marker marker, String format, Object[] argArray) {
			// TODO Auto-generated method stub

		}

		public void warn(Marker marker, String msg, Throwable t) {
			// TODO Auto-generated method stub

		}

		public void warn(String msg) {
			// TODO Auto-generated method stub

		}

		public void warn(String format, Object arg) {
			// TODO Auto-generated method stub

		}

		public void warn(String format, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

		public void warn(String format, Object[] argArray) {
			// TODO Auto-generated method stub

		}

		public void warn(String msg, Throwable t) {
			// TODO Auto-generated method stub

		}

	};
	private AbstractBatchProgressMonitor monitor;

	private boolean progressCheckedWhenLogged = false;

	private float getProgress() {
		float progress = this.monitor.getProgress() * 100f
				/ this.monitor.getTotalWork();
		return progress;
	}

	private void step1(IBatchProgressMonitor stepMonitor) throws Exception {
		stepMonitor.setLogger(this.logger);
		stepMonitor.beginTask("step1", "Do the step 1", 1);
		Thread.sleep(1000);
		stepMonitor.done();
	}

	private void step2(IBatchProgressMonitor stepMonitor) throws Exception {
		stepMonitor.setLogger(this.logger);
		stepMonitor.beginTask("step2", "Do the step 2", 2);

		step2_1(stepMonitor.createSubTask(1));

		step2_2(stepMonitor.createSubTask(1));

		stepMonitor.done();

		float progress = getProgress();
		assertThat(progress <= 100f, is(true));
	}

	private void step2_1(IBatchProgressMonitor subStepMonitor) throws Exception {
		subStepMonitor.setLogger(this.logger);
		subStepMonitor.beginTask("step2.1", "Do the step 2.1", 1);
		Thread.sleep(1000);
		subStepMonitor.done();
		float progress = getProgress();
		assertThat(progress <= 100f, is(true));
	}

	private void step2_2(IBatchProgressMonitor subStepMonitor) throws Exception {
		subStepMonitor.setLogger(this.logger);
		subStepMonitor.beginTask("step2.2", "Do the step 2.2", 3);
		for (int i = 0; i < 2; i++) {
			Thread.sleep(1000);
			subStepMonitor.worked(1);
		}
		subStepMonitor.done();
		float progress = getProgress();
		assertThat(progress <= 100f, is(true));
	}

	@Test
	public void testProgressStayUnder100Percent() throws Exception {
		IBatchProgressMonitor jobMonitor = AppStatusStatic.getInstance()
				.getBatchProgressMonitor("test", "test", "1");

		this.monitor = (AbstractBatchProgressMonitor) jobMonitor;

		jobMonitor.setLogger(this.logger);
		jobMonitor.beginTask("job", "Do the job", 2);

		step1(jobMonitor.createSubTask(1));

		step2(jobMonitor.createSubTask(1));

		jobMonitor.done();

		// get the batch progress
		List<IBatch> finishedBatches = AppStatusStatic.getInstance()
				.getBatchManager().getFinishedBatches();

		IBatch myBatch = null;
		for (IBatch finishedBatch : finishedBatches) {
			if (finishedBatch.getUuid().equals("1")) {
				myBatch = finishedBatch;
			}
		}

		assertThat(myBatch.getProgressStatus(), equalTo(100f));

		float progress = getProgress();
		assertThat(progress <= 100f, is(true));

		// Ensure log check has happened.
		assertTrue(progressCheckedWhenLogged);

	}
}
