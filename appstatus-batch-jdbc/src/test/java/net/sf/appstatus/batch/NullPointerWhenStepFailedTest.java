package net.sf.appstatus.batch;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.appstatus.batch.jdbc.CleanBatchHelper;
import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.batch.AbstractBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

/**
 * Test the failed feature.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/appstatus-jdbc-test-config.xml" })
public class NullPointerWhenStepFailedTest {

    @Autowired
    AppStatus appStatus;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(NullPointerWhenStepFailedTest.class);

    @Before
    public void setup() {
        jdbcTemplate.execute("TRUNCATE SCHEMA public AND COMMIT");
    }

    private void step1(IBatchProgressMonitor stepMonitor) throws Exception {
        stepMonitor.setLogger(this.logger);
        stepMonitor.beginTask("step1", "Do the step 1", 1);
        Thread.sleep(1000);
        stepMonitor.fail("Test fail feature");
    }

    @Test
    public void testBasicScenario() throws Exception {
        CleanBatchHelper.cleanBatches(appStatus);
        String uuid = UUID.randomUUID().toString();
        IBatchProgressMonitor m = appStatus.getBatchProgressMonitor("Batch name", "Batch group", uuid);
        m.setLogger(this.logger);

        // Before first task
        m.message("Test message");
        m.setCurrentItem("");

        assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(-1f));
        assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getProgressStatus(), is(-1f));

        // Task 1
        m.beginTask("Task 1 name", "Task 1 description ", 4);
        assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(0f));
        assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getProgressStatus(), is(0f));
        m.worked(1);
        assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(1f));

        m.message("Test message");

        m.reject("rejected1", "for testing");
        assertThat(((AbstractBatchProgressMonitor) m).getRejectedItems().size(), is(1));
        assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getRejectedItemsId().get(0), is("rejected1"));
        assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getProgressStatus(), is(25f));

        IBatchProgressMonitor m1 = m.createSubTask(3);

        m1.beginTask("Sub task 1", "Sub task 1 description", 1);
        m1.worked(1);

        m1.done();
        assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(4f));
        assertThat(appStatus.getBatchManager().getRunningBatches().get(0).getProgressStatus(), is(100f));

        m.done();

        assertThat(((AbstractBatchProgressMonitor) m).isSuccess(), is(true));
        assertThat(((AbstractBatchProgressMonitor) m).getProgress(), is(4f));
    }

    @Test
    public void testMonitorIsNotSuccessfulWhenAJobFailed() throws Exception {
        CleanBatchHelper.cleanBatches(appStatus);
        String uuid = UUID.randomUUID().toString();
        IBatchProgressMonitor monitor = appStatus.getBatchProgressMonitor("test", "test", uuid);

        monitor.setLogger(this.logger);
        monitor.beginTask("job", "Do the job", 1);

        monitor.fail("Test fail feature");

        assertThat(((AbstractBatchProgressMonitor) monitor).isSuccess(), is(false));
    }

    @Test
    public void testMonitorIsNotSuccessfulWhenAStepFailed() throws Exception {
        CleanBatchHelper.cleanBatches(appStatus);
        String uuid = UUID.randomUUID().toString();
        IBatchProgressMonitor jobMonitor = appStatus.getBatchProgressMonitor("test", "test", uuid);

        jobMonitor.setLogger(this.logger);
        jobMonitor.beginTask("job", "Do the job", 1);

        step1(jobMonitor.createSubTask(1));

        assertThat(((AbstractBatchProgressMonitor) jobMonitor).isSuccess(), is(false));
    }

}
