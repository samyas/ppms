package com.advancedit.ppms.models.project;

import com.advancedit.ppms.exceptions.PPMSException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum ProjectStatus {



	PROPOSAL("Proposal"),
	ASSIGNED("Assigned"),
	REGISTRATION("Registration"),
	START("Start"),
	PROGRESS("Progress"),
	WITHDRAW("Withdraw"),
	SUSPEND("Suspend"),
	REALLOCATED("Re-Allocated"),
	COMPLETED("Completed"),

	NEW("Draft");

	private String label;

	ProjectStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public boolean canAssignUnassign(){
		return this.equals(PROPOSAL) || this.equals(REALLOCATED);
	}

	public static ProjectStatus fromLabel(String value){
		return Stream.of(ProjectStatus.values()).filter(l -> l.getLabel().equalsIgnoreCase(value) || l.name().equalsIgnoreCase(value)).findFirst()
		.orElseThrow((() -> new PPMSException("Invalid Project status:" + value)));
	}
}
