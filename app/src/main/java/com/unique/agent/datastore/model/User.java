package com.unique.agent.datastore.model;

import com.unique.agent.model.UserLabor;

/**
 * Created by praveenpokuri on 04/07/17.
 */

public class User {

    private String mName;
    private String mPassword;
    private String mEmail;
    private String mPhone;
    private String mAddress;
    private String mAvatar;
    private String mRating;
    private String mSkills;
    private LoginType mLoginType;
    private UserType mUserType;




    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        this.mPhone = phone;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }
    public String getRating() {
        return mRating;
    }

    public void setRating(String rating) {
        this.mRating = rating;
    }

    public String getSkills() {
        return mSkills;
    }

    public void setSkills(String skills) {
        this.mSkills = skills;
    }

    public UserType getUserType() {
        return mUserType;
    }

    public void setUserType(UserType userType) {
        this.mUserType = userType;
    }

    public LoginType getLoginType(){
        return mLoginType;
    }

    public void setLoginType(LoginType loginType){
        this.mLoginType = loginType;
    }

    public static enum LoginType {
        MOBILE(1,"mobile"),
        EMAIL(2,"email"),
        USERNAME(3,"username");

        int mType;
        String mName;



        LoginType(int type, String name) {
            mType = type;
            mName = name;
        }

        public int getType(){
            return mType;
        }

        public String getName(){
            return mName;
        }
    }

    public static enum UserType {
        LABOR,
        OWNER;
    }
}
