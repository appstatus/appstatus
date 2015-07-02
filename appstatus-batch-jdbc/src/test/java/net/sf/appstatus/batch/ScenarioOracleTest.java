package net.sf.appstatus.batch;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.batch.AbstractBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test the failed feature.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/appstatus-jdbc-ora-test-config.xml" })
public class ScenarioOracleTest {

	@Autowired
	AppStatus appStatus;

	private final Logger logger = LoggerFactory.getLogger(ScenarioOracleTest.class);

	@Test
	public void testBasicScenario() throws Exception {
		String uuid = UUID.randomUUID().toString();
		IBatchProgressMonitor m = appStatus.getBatchProgressMonitor("Batch name", "Batch group", uuid);
		m.setLogger(this.logger);

		// Before first task
		m.message("Test message");
		m.setCurrentItem("");

		
		assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(-1f));
		assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getProgressStatus(), is( -1f));

		// Task 1
		m.beginTask("Task 1 name", "Task 1 description ", 4);
		assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(0f));
		assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getProgressStatus(), is( 0f));
		m.worked(1);
		assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(1f));

		m.message("Test message");

		m.reject("rejected1", "for testing");
		assertThat(((AbstractBatchProgressMonitor) m).getRejectedItems().size(), is(1));
		assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getRejectedItemsId().get(0), is( "rejected1"));
		assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getProgressStatus(), is( 25f));

		IBatchProgressMonitor m1 = m.createSubTask(3);

		m1.beginTask("Sub task 1", "Sub task 1 description", 1);
		m1.worked(1);

		m1.done();
		assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(4f));
		assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getProgressStatus(), is( 100f));

		m.done();

		assertThat(((AbstractBatchProgressMonitor) m).isSuccess(), is(true));
		assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(4f));
	}
	
	

}
