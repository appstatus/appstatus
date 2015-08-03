package net.sf.appstatus;

import java.util.Locale;

import net.sf.appstatus.core.check.ICheck;
import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.core.check.impl.StatusResultImpl;

public class FakeChecker implements ICheck {

	public ICheckResult checkStatus(Locale locale) {
		StatusResultImpl result = new StatusResultImpl();
		result.setCode(ICheckResult.OK);
		result.setFatal(false);
		result.setProbeName("Unit testing probe");
		result.setDescription("fake check");
		result.setResolutionSteps("nothing to do");
		return result;
	}

	public String getGroup() {

		return "fake";
	}

	public String getName() {
		return "FakeChecker";
	}

}