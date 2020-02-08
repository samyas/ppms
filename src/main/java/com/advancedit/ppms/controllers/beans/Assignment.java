/*
 * 
 */
package com.advancedit.ppms.controllers.beans;

import java.util.List;

public class Assignment {

	private Action action;
	private String personId;
	private Position position;
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}


	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	
	public enum Action {
		ADD,
		REMOVE
	}

	public enum Position {
		TEAM,
		EXAMINATOR,
		SUPERVISOR,
		REPORTER
	}
}


