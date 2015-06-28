package net.sf.appstatus.batch;

import java.util.UUID;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.AppStatusStatic;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the failed feature.
 */

public class GHIssue1Test {

	/**
	 * Ensure running jobs are not deleted by clean operations.
	 *
	 * @throws Exception
	 */
	@Test
	public void testIssue1() throws Exception {
		AppStatus appStatus = AppStatusStatic.getInstance();

		// Create two finished job.
		appStatus.getBatchProgressMonitor("Batch name", "Batch group", UUID.randomUUID().toString()).done();
		IBatchProgressMonitor pm = appStatus.getBatchProgressMonitor("Batch name", "Batch group", UUID.randomUUID()
				.toString());
		pm.reject("1", "reject");
		pm.done();

		// Create one running job.
		appStatus.getBatchProgressMonitor("Batch name", "Batch group", UUID.randomUUID().toString());

		Assert.assertThat(appStatus.getBatchManager().getRunningBatches().size(), Matchers.is(1));
		Assert.assertThat(appStatus.getBatchManager().getFinishedBatches().size(), Matchers.is(2));

		appStatus.getBatchManager().removeAllBatches(IBatchManager.REMOVE_SUCCESS);

		Assert.assertThat(appStatus.getBatchManager().getRunningBatches().size(), Matchers.is(1));
		Assert.assertThat(appStatus.getBatchManager().getFinishedBatches().size(), Matchers.is(1));

	}

}
