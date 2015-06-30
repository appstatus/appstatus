package net.sf.appstatus.batch;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import net.sf.appstatus.core.AppStatusStatic;
import net.sf.appstatus.core.batch.AbstractBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test the failed feature.
 */
public class NullPointerWhenStepFailedTest {
	private final Logger logger = LoggerFactory.getLogger(NullPointerWhenStepFailedTest.class);

	private AbstractBatchProgressMonitor monitor;

	private void step1(IBatchProgressMonitor stepMonitor) throws Exception {
		stepMonitor.setLogger(this.logger);
		stepMonitor.beginTask("step1", "Do the step 1", 1);
		Thread.sleep(1000);
		stepMonitor.fail("Test fail feature");
	}

	@Test
	public void testBasicScenario() throws Exception {
		String uuid = UUID.randomUUID().toString();
		IBatchProgressMonitor m = AppStatusStatic.getInstance().getBatchProgressMonitor("Batch name", "Batch group",
				uuid);
		m.setLogger(this.logger);

		// Before first task
		m.message("Test message");
		m.setCurrentItem("");

		assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(-1f));
		assertThat(AppStatusStatic.getInstance().getBatchManager().getRunningBatches().get(0).getProgressStatus(),
				is(-1f));

		// Task 1
		m.beginTask("Task 1 name", "Task 1 description ", 4);
		assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(0f));
		assertThat(AppStatusStatic.getInstance().getBatchManager().getRunningBatches().get(0).getProgressStatus(),
				is(0f));
		m.worked(1);
		assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(1f));
		assertThat(AppStatusStatic.getInstance().getBatchManager().getRunningBatches().get(0).getProgressStatus(),
				is(25f));

		m.message("Test message");

		m.reject("rejected1", "for testing");
		assertThat(((AbstractBatchProgressMonitor) m).getRejectedItems().size(), is(1));
		assertThat(AppStatusStatic.getInstance().getBatchManager().getRunningBatches().get(0).getProgressStatus(),
				is(25f));

		IBatchProgressMonitor m1 = m.createSubTask(3);

		m1.beginTask("Sub task 1", "Sub task 1 description", 1);
		m1.worked(1);
		m1.done();
		assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(4f));
		assertThat(AppStatusStatic.getInstance().getBatchManager().getRunningBatches().get(0).getProgressStatus(),
				is(100f));

		m.done();

		assertThat(((AbstractBatchProgressMonitor) m).isSuccess(), is(true));
		assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(4f));
	}

	@Test
	public void testMonitorIsNotSuccessfulWhenAJobFailed() throws Exception {
		IBatchProgressMonitor jobMonitor = AppStatusStatic.getInstance().getBatchProgressMonitor("test", "test", "1");

		this.monitor = (AbstractBatchProgressMonitor) jobMonitor;

		jobMonitor.setLogger(this.logger);
		jobMonitor.beginTask("job", "Do the job", 1);

		jobMonitor.fail("Test fail feature");

		assertThat(this.monitor.isSuccess(), is(false));
	}

	@Test
	public void testMonitorIsNotSuccessfulWhenAStepFailed() throws Exception {
		IBatchProgressMonitor jobMonitor = AppStatusStatic.getInstance().getBatchProgressMonitor("test", "test", "1");

		this.monitor = (AbstractBatchProgressMonitor) jobMonitor;

		jobMonitor.setLogger(this.logger);
		jobMonitor.beginTask("job", "Do the job", 1);

		step1(jobMonitor.createSubTask(1));

		assertThat(this.monitor.isSuccess(), is(false));
	}
}
