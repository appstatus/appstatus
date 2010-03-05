package net.sf.appstatus.dummy;

import net.sf.appstatus.StatusChecker;
import net.sf.appstatus.StatusResult;
import net.sf.appstatus.StatusResultImpl;

public class CheckDummy implements StatusChecker {

	public StatusResult checkStatus() {

		StatusResultImpl result = new StatusResultImpl();

		result.setProbeName(getName());
		result.setCode(Math.random() > 0.5 ? StatusResult.OK
				: StatusResult.ERROR);
		if (result.getCode() == StatusResult.OK) {
			result.setDescription("Random is good");
			result.setFatal(false);
		} else {
			result.setDescription("Random failed");
			result
					.setResolutionSteps("This probe fails randomly. Please reload the page for better luck.");
			result.setFatal(true);
		}
		return result;
	}

	public String getName() {
		return "Dummy check";
	}

}
