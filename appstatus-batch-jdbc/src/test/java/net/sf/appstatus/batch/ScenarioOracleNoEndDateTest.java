package net.sf.appstatus.batch;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
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
public class ScenarioOracleNoEndDateTest {

	@Autowired
	AppStatus appStatus;

	private final Logger logger = LoggerFactory.getLogger(ScenarioOracleNoEndDateTest.class);

	@Test
	public void testNoEndDateScenario() throws Exception {
		String uuid = UUID.randomUUID().toString();
		IBatchProgressMonitor m = appStatus.getBatchProgressMonitor("Batch name", "Batch group", uuid);
		m.setLogger(this.logger);

		// Before first task
		m.message("Test message");
		m.setCurrentItem("");

		assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getProgressStatus(), is(-1f));

		// Task 1
		m.beginTask("Task 1 name", "Task 1 description ", 4);
		assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(0f));
		assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getProgressStatus(), is(0f));

		assertNull(appStatus.getBatchManager().getRunningBatches().get(0).getEndDate());
	}

}
