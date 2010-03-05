package net.sf.appstatus;

public class StatusResultImpl implements StatusResult {
	private int code;
	private String description;
	private String resolutionSteps;
	private String probeName;

	public String getProbeName() {
		return probeName;
	}

	public void setProbeName(String probeName) {
		this.probeName = probeName;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResolutionSteps() {
		return resolutionSteps;
	}

	public void setResolutionSteps(String resolutionSteps) {
		this.resolutionSteps = resolutionSteps;
	}

	public boolean isFatal() {
		return fatal;
	}

	public void setFatal(boolean fatal) {
		this.fatal = fatal;
	}

	private boolean fatal;

}
