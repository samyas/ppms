package com.advancedit.ppms.models.project;

public enum TaskStatus {

	NEW("Initial"),
	START("Started"),
	REVIEW("Review"),
	DECLINED("Declined"),
	COMPLETED("Completed"),
	;

	private String label;

	TaskStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
