package com.klarite.backend;

public class Constants {
    public static final int ZERO = 0;
    public static final int ONE = 1;

    // table names
    public static final String TABLE_SKILLS = " skills ";
    public static final String TABLE_TRAININGS = " trainings ";
    public static final String TABLE_USERS = " users ";
    public static final String TABLE_COST_CENTER = " cost_center ";
    public static final String TABLE_BUSINESS_UNIT = " business_unit ";
    public static final String TABLE_CONTINUED_EDUCATION = " ce ";
    public static final String TABLE_EPISODES = " episodes ";
    public static final String TABLE_SKILL_EPISODES = " skill_episodes ";
    public static final String TABLE_CONTACT_HOURS = " contact_hours ";
    public static final String TABLE_S_ASSIGNMENTS = " s_assignments ";
    public static final String TABLE_SKILL_ASSIGNMENTS = " skill_assignments ";
    public static final String TABLE_T_ASSIGNMENTS = " t_assignments ";
    public static final String TABLE_TRAINING_ASSIGNMENTS = " training_assignments ";
    public static final String TABLE_TOKENS = " tokens ";

    // return msgs
    public static final String MSG_MARK_ATTENDANCE_SUCCESS = "Attendance marked successfully";
    public static final String MSG_MARK_ATTENDANCE_INVALID_UUID = "Failed to mark attendance. Reason: 'Invalid indetifier specified'.";
    public static final String MSG_MARK_ATTENDANCE_INVALID_TIME = "Failed to mark attendance. Reason: 'Inavlid time'.";
    public static final String MSG_UPDATED_SUCCESSFULLY = "Data updated successfully.";
    public static final String MSG_UPDATE_FAILED = "Failed to update data.";

    // directories
    public static final String DIRECTORY_UPLOADS = "uploads";
    public static final String DIRECTORY_PROFILE = DIRECTORY_UPLOADS + "/" + "profile";
    public static final String DIRECTORY_DOCUMENTS = DIRECTORY_UPLOADS + "/" + "documents";
}
