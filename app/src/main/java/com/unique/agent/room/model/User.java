package com.unique.agent.room.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

import static com.unique.agent.room.TableUtils.TBL_USERS;

/**
 * Created by praveenpokuri on 06/09/17.
 */
@Entity(tableName = TBL_USERS)
public class User {
    @PrimaryKey(autoGenerate = true)
    public int uId;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "lst_name")
    public String lastName;

    @ColumnInfo(name = "lst_update")
    public String lastUpdate;

    @Ignore
    Bitmap avatar;

    public User(){

    }

    public User(String firstName, String lastName, Bitmap avatar){
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
    }
}
