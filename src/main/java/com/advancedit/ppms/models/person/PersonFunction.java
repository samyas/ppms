package com.advancedit.ppms.models.person;

public enum PersonFunction {
		//CANDIDATE, CONSULTANT, SECRETARIAT
	ADMIN_CREATOR, STUDENT, STAFF, MODEL_LEADER,

;
	public static boolean isStaff(PersonFunction personFunction){
		return (personFunction.equals(STAFF) || personFunction.equals(MODEL_LEADER));
	}
}
