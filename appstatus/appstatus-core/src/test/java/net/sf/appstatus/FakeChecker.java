package net.sf.appstatus;

import net.sf.appstatus.check.impl.StatusResultImpl;

public class FakeChecker implements IStatusChecker {

  public IStatusResult checkStatus() {
    StatusResultImpl result = new StatusResultImpl();
    result.setCode(IStatusResult.OK);
    result.setFatal(false);
    result.setProbeName("Unit testing probe");
    result.setDescription("fake check");
    result.setResolutionSteps("nothing to do");
    return result;
  }

  public String getName() {
    return "FakeChecker";
  }

}