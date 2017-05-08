package net.sf.appstatus;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.check.impl.StatusResultImpl;

public class StatusServiceTest extends AppStatus {

    public StatusResultImpl result;

    @Test
    public void testInitialization() throws Exception {
        this.init();
        assertTrue(checkers.size() > 0);
        this.close();
        assertTrue(executorService == null || (executorService.isTerminated() && executorService.isShutdown()));
    }
}
