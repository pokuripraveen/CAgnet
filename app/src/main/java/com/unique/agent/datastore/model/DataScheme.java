package com.unique.agent.datastore.model;

/**
 * Created by praveenpokuri on 04/07/17.
 */

public class DataScheme {

    public static final String TBL_USER_LABOR = "UserLabor";
    public static final String TBL_USER_OWNER = "UserOwner";

    /* TBL_USER Table */
    public static class USER_LABOR {

        public static final String KEY_ID = "_id";
        public static final String KEY_NAME = "name";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_PH_NO = "phone";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_ADDRESS = "address";
        public static final String KEY_IMAGE = "image";
        public static final String KEY_AGE = "age";
        public static final String KEY_MARRIED = "married";
        public static final String KEY_SEX = "sex";
        public static final String KEY_CATEGORY = "category";
        public static final String KEY_RATING = "rating";
        public static final String KEY_SKILLS = "skills";
        public static final String KEY_EXPERIENCE = "experience";
    }

    public static final String DDL_CREATE_TBL_USER_LABOR = "CREATE TABLE IF NOT EXISTS " + TBL_USER_LABOR + "("
            + USER_LABOR.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_LABOR.KEY_NAME  + " TEXT, " +
            USER_LABOR.KEY_PASSWORD  + " TEXT, " +
            USER_LABOR.KEY_PH_NO + " TEXT, " +
            USER_LABOR.KEY_EMAIL + " TEXT, " +
            USER_LABOR.KEY_ADDRESS + " TEXT"+
            USER_LABOR.KEY_AGE + " TEXT"+
            USER_LABOR.KEY_MARRIED + " TEXT"+
            USER_LABOR.KEY_SEX + " TEXT"+
            USER_LABOR.KEY_CATEGORY + " TEXT"+
            USER_LABOR.KEY_RATING + " TEXT"+
            USER_LABOR.KEY_SKILLS + " TEXT"+
            USER_LABOR.KEY_EXPERIENCE + " TEXT"+
            USER_LABOR.KEY_IMAGE + " BLOB);";

    /* TBL_USER Table */
    public static class USER_OWNER {

        public static final String KEY_ID = "_id";
        public static final String KEY_NAME = "name";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_PH_NO = "phone";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_ADDRESS = "address";
        public static final String KEY_IMAGE = "image";
        public static final String KEY_AGE = "age";
        public static final String KEY_MARRIED = "married";
        public static final String KEY_SEX = "sex";
        public static final String KEY_RATING = "rating";
    }

    public static final String DDL_CREATE_TBL_USER_OWNER = "CREATE TABLE IF NOT EXISTS " + TBL_USER_OWNER + "("
            + USER_OWNER.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_OWNER.KEY_NAME  + " TEXT, " +
            USER_OWNER.KEY_PASSWORD  + " TEXT, " +
            USER_OWNER.KEY_PH_NO + " TEXT, " +
            USER_OWNER.KEY_EMAIL + " TEXT, " +
            USER_OWNER.KEY_ADDRESS + " TEXT"+
            USER_OWNER.KEY_AGE + " TEXT"+
            USER_OWNER.KEY_MARRIED + " TEXT"+
            USER_OWNER.KEY_SEX + " TEXT"+
            USER_OWNER.KEY_RATING + " TEXT"+
            USER_OWNER.KEY_IMAGE + " BLOB);";

}
