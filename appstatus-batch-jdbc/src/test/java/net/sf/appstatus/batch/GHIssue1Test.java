package net.sf.appstatus.batch;

import java.util.UUID;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test the failed feature.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/appstatus-jdbc-test-config.xml" })
public class GHIssue1Test {

	@Autowired
	AppStatus appStatus;

	@Autowired 
	JdbcTemplate jdbcTemplate; 
	
	@Before
	public void setup() {
		jdbcTemplate.execute("TRUNCATE SCHEMA public AND COMMIT");
	}
	/**
	 * Ensure running jobs are not deleted by clean operations.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIssue1() throws Exception {

		// Create two finished job.
		appStatus.getBatchProgressMonitor("Batch name", "Batch group", UUID.randomUUID().toString()).done();
		IBatchProgressMonitor pm  = appStatus.getBatchProgressMonitor("Batch name", "Batch group", UUID.randomUUID().toString());
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
