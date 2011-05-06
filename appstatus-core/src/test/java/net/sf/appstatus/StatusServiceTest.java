package net.sf.appstatus;

import static org.junit.Assert.assertTrue;
import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.check.impl.StatusResultImpl;

import org.junit.Test;

public class StatusServiceTest extends AppStatus {

  public StatusResultImpl result;

  @Test
  public void testInitialization() throws Exception {
    this.init();
    assertTrue(probes.size() > 0);
  }
}
