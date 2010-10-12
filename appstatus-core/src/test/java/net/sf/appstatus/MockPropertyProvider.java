package net.sf.appstatus;

import static org.mockito.Mockito.mock;

import java.util.Map;

public class MockPropertyProvider implements IPropertyProvider {

	private final IPropertyProvider mockPropertyProvider = mock(IPropertyProvider.class);

	public String getCategory() {
		return mockPropertyProvider.getCategory();
	}

	public Map<String, String> getProperties() {
		return mockPropertyProvider.getProperties();
	}

}
