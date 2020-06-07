package com.advancedit.ppms.models.project;

public enum GoalStatus {

	NEW("Initial"),
	START("Started"),
	REVIEW("Review"),
	DECLINED("Declined"),
	COMPLETED("Completed"),
	;

	private String label;

	GoalStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
