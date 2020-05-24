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
    public static final String TABLE_NOTIFICATIONS = " notifications ";
    public static final String CERTIFICATIONS = "certifications";
    public static final String TABLE_LICENSE = "license";
    public static final String TABLE_USER_LICENSE = "user_license";

    // return msgs
    public static final String MSG_MARK_ATTENDANCE_SUCCESS = "Attendance marked successfully";
    public static final String MSG_MARK_ATTENDANCE_INVALID_UUID = "Failed to mark attendance.\\nReason: 'Invalid event code specified'.";
    public static final String MSG_MARK_ATTENDANCE_INVALID_TIME = "Failed to mark attendance.\\nReason: 'Meeting not started yet'.";
    public static final String MSG_UPDATED_SUCCESSFULLY = "Data updated successfully.";
    public static final String MSG_UPDATE_FAILED = "Failed to update data.";
    public static final String MSG_DELETE_SUCCESS = "Item deleted successfully";
    public static final String MSG_DELETE_FAILED = "Failed to delete item";
    public static final String MSG_UNAUTHORIZED = "User is not logged in";
    public static final String MSG_CURRENT_PSWD_INVLAID = "Your password was incorrect";
    public static final String MSG_PSWD_RULE_VOILATED = "New password should be atleast 6 characters long";

    // directories
    public static final String DIRECTORY_UPLOADS = "uploads";
    public static final String DIRECTORY_PROFILE = DIRECTORY_UPLOADS + "/" + "profile";
    public static final String DIRECTORY_DOCUMENTS = DIRECTORY_UPLOADS + "/" + "documents";
}
