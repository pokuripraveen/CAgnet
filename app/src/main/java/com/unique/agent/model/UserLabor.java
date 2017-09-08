package com.unique.agent.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class UserLabor {

    @SerializedName("id")
    public int    id;
    @SerializedName ("name")
    public String name;
    @SerializedName ("email")
    public String email;

    @SerializedName ("category")
    public String category;
    @SerializedName ("age")
    public String age;

    @SerializedName ("sex")
    public String sex;

    @SerializedName ("married")
    public String married;



}
