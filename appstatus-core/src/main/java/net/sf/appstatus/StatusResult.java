package net.sf.appstatus;

public interface StatusResult {

	int OK = 0;
	int ERROR = -1;

	int getCode();

	String getProbeName();
	
	String getDescription();

	String getResolutionSteps();

	boolean isFatal();

}
