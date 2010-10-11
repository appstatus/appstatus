package net.sf.appstatus;

import static org.junit.Assert.assertTrue;
import net.sf.appstatus.check.impl.StatusResultImpl;

import org.junit.Test;

public class StatusServiceTest extends StatusService {

  public StatusResultImpl result;

  @Test
  public void testInitialization() throws Exception {
    this.init();
    assertTrue(probes.size() > 0);
  }
}
