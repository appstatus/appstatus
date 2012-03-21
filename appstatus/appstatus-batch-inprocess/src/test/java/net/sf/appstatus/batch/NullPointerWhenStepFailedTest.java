package net.sf.appstatus.batch;

import net.sf.appstatus.core.AppStatusStatic;
import net.sf.appstatus.core.batch.AbstractBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test the failed feature.
 */
public class NullPointerWhenStepFailedTest {
    private AbstractBatchProgressMonitor monitor;

    private final Logger logger = LoggerFactory
            .getLogger(NullPointerWhenStepFailedTest.class);

    private void step1(IBatchProgressMonitor stepMonitor) throws Exception {
        stepMonitor.setLogger(this.logger);
        stepMonitor.beginTask("step1", "Do the step 1", 1);
        Thread.sleep(1000);
        stepMonitor.fail("Test fail feature");
    }

    @Test
    public void testMonitorIsNotSuccessfulWhenAJobFailed() throws Exception {
        IBatchProgressMonitor jobMonitor = AppStatusStatic.getInstance()
                .getBatchProgressMonitor("test", "test", "1");

        this.monitor = (AbstractBatchProgressMonitor) jobMonitor;

        jobMonitor.setLogger(this.logger);
        jobMonitor.beginTask("job", "Do the job", 1);

        jobMonitor.fail("Test fail feature");

        assertThat(this.monitor.isSuccess(), is(false));
    }

    @Test
    public void testMonitorIsNotSuccessfulWhenAStepFailed() throws Exception {
        IBatchProgressMonitor jobMonitor = AppStatusStatic.getInstance()
                .getBatchProgressMonitor("test", "test", "1");

        this.monitor = (AbstractBatchProgressMonitor) jobMonitor;

        jobMonitor.setLogger(this.logger);
        jobMonitor.beginTask("job", "Do the job", 1);

        step1(jobMonitor.createSubTask(1));

        assertThat(this.monitor.isSuccess(), is(false));
    }
}
