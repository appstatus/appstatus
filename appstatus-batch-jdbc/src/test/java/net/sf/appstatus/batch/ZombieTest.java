package net.sf.appstatus.batch;

import java.util.Properties;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.appstatus.batch.jdbc.CleanBatchHelper;
import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.batch.IBatch;

/**
 * Test the failed feature.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/appstatus-jdbc-test-config.xml" })
public class ZombieTest {
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
        CleanBatchHelper.cleanBatches(appStatus);
        Properties p = new Properties();
        p.setProperty("batch.zombieInterval", "1000");
        appStatus.getBatchManager().setConfiguration(p);

        // Create two finished job.
        appStatus.getBatchProgressMonitor("Batch name", "Batch group", UUID.randomUUID().toString());
        Assert.assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getStatus(),
                Matchers.is(IBatch.STATUS_RUNNING));

        Thread.sleep(1000);
        Assert.assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getStatus(),
                Matchers.is(IBatch.STATUS_ZOMBIE));

    }

}
