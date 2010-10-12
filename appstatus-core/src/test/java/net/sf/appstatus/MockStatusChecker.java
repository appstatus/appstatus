package net.sf.appstatus;

import static org.mockito.Mockito.mock;

public class MockStatusChecker implements IStatusChecker {

	private final IStatusChecker mockStatusChecker = mock(IStatusChecker.class);

	public IStatusResult checkStatus() {
		return mockStatusChecker.checkStatus();
	}

	public String getName() {
		return mockStatusChecker.getName();
	}

}
