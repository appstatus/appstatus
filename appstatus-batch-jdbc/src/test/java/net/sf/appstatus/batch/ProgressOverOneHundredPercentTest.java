package net.sf.appstatus.batch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.appstatus.batch.jdbc.CleanBatchHelper;
import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.batch.AbstractBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

/**
 * https://sourceforge.net/apps/mantisbt/appstatus/view.php?id=36
 *
 * @author Guillaume Mary
 * @author Nicolas Richeton
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/appstatus-jdbc-test-config.xml" })
public class ProgressOverOneHundredPercentTest {

    @Autowired
    AppStatus appStatus;

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

        }

        public void debug(Marker marker, String format, Object arg) {

        }

        public void debug(Marker marker, String format, Object arg1, Object arg2) {

        }

        public void debug(Marker marker, String format, Object[] argArray) {

        }

        public void debug(Marker marker, String msg, Throwable t) {

        }

        public void debug(String msg) {

        }

        public void debug(String format, Object arg) {

        }

        public void debug(String format, Object arg1, Object arg2) {

        }

        public void debug(String format, Object[] argArray) {

        }

        public void debug(String msg, Throwable t) {

        }

        public void error(Marker marker, String msg) {

        }

        public void error(Marker marker, String format, Object arg) {

        }

        public void error(Marker marker, String format, Object arg1, Object arg2) {

        }

        public void error(Marker marker, String format, Object[] argArray) {

        }

        public void error(Marker marker, String msg, Throwable t) {

        }

        public void error(String msg) {

        }

        public void error(String format, Object arg) {

        }

        public void error(String format, Object arg1, Object arg2) {

        }

        public void error(String format, Object[] argArray) {

        }

        public void error(String msg, Throwable t) {

        }

        public String getName() {
            return null;
        }

        public void info(Marker marker, String msg) {

        }

        public void info(Marker marker, String format, Object arg) {

        }

        public void info(Marker marker, String format, Object arg1, Object arg2) {

        }

        public void info(Marker marker, String format, Object[] argArray) {

        }

        public void info(Marker marker, String msg, Throwable t) {

        }

        public void info(String msg) {

        }

        public void info(String format, Object arg) {

        }

        public void info(String format, Object arg1, Object arg2) {

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

        }

        public boolean isDebugEnabled() {

            return false;
        }

        public boolean isDebugEnabled(Marker marker) {

            return false;
        }

        public boolean isErrorEnabled() {

            return false;
        }

        public boolean isErrorEnabled(Marker marker) {

            return false;
        }

        public boolean isInfoEnabled() {
            return true;
        }

        public boolean isInfoEnabled(Marker marker) {

            return false;
        }

        public boolean isTraceEnabled() {

            return false;
        }

        public boolean isTraceEnabled(Marker marker) {

            return false;
        }

        public boolean isWarnEnabled() {

            return false;
        }

        public boolean isWarnEnabled(Marker marker) {

            return false;
        }

        public void trace(Marker marker, String msg) {

        }

        public void trace(Marker marker, String format, Object arg) {

        }

        public void trace(Marker marker, String format, Object arg1, Object arg2) {

        }

        public void trace(Marker marker, String format, Object[] argArray) {

        }

        public void trace(Marker marker, String msg, Throwable t) {

        }

        public void trace(String msg) {

        }

        public void trace(String format, Object arg) {

        }

        public void trace(String format, Object arg1, Object arg2) {

        }

        public void trace(String format, Object[] argArray) {

        }

        public void trace(String msg, Throwable t) {

        }

        public void warn(Marker marker, String msg) {

        }

        public void warn(Marker marker, String format, Object arg) {

        }

        public void warn(Marker marker, String format, Object arg1, Object arg2) {

        }

        public void warn(Marker marker, String format, Object[] argArray) {

        }

        public void warn(Marker marker, String msg, Throwable t) {

        }

        public void warn(String msg) {

        }

        public void warn(String format, Object arg) {

        }

        public void warn(String format, Object arg1, Object arg2) {

        }

        public void warn(String format, Object[] argArray) {

        }

        public void warn(String msg, Throwable t) {

        }

    };
    private AbstractBatchProgressMonitor monitor;

    private boolean progressCheckedWhenLogged = false;

    private float getProgress() {
        float progress = this.monitor.getProgress() * 100f / this.monitor.getTotalWork();
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
        CleanBatchHelper.cleanBatches(appStatus);
        String uuid = UUID.randomUUID().toString();
        IBatchProgressMonitor jobMonitor = appStatus.getBatchProgressMonitor("test", "test", uuid);

        this.monitor = (AbstractBatchProgressMonitor) jobMonitor;

        jobMonitor.setLogger(this.logger);
        jobMonitor.beginTask("job", "Do the job", 2);

        step1(jobMonitor.createSubTask(1));

        step2(jobMonitor.createSubTask(1));

        jobMonitor.done();

        // get the batch progress
        List<IBatch> finishedBatches = appStatus.getBatchManager().getFinishedBatches();

        IBatch myBatch = null;
        for (IBatch finishedBatch : finishedBatches) {
            if (finishedBatch.getUuid().equals(uuid)) {
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
