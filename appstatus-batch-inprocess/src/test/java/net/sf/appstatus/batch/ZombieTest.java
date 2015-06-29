package net.sf.appstatus.batch;

import java.util.Properties;
import java.util.UUID;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.batch.IBatch;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the failed feature.
 */

public class ZombieTest {

	/**
	 * Ensure running jobs are not deleted by clean operations.
	 *
	 * @throws Exception
	 */
	@Test
	public void testIssue1() throws Exception {
		Properties p = new Properties();
		p.setProperty("batch.zombieInterval", "1000");
		AppStatus appStatus = new AppStatus();
		appStatus.setConfiguration(p);
		appStatus.init();

		// Create two finished job.
		appStatus.getBatchProgressMonitor("Batch name", "Batch group", UUID.randomUUID().toString());
		Assert.assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getStatus(),
				Matchers.is(IBatch.STATUS_RUNNING));

		Thread.sleep(1000);
		Assert.assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getStatus(),
				Matchers.is(IBatch.STATUS_ZOMBIE));

	}

}
