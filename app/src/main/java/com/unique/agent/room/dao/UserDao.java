package com.unique.agent.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.unique.agent.room.model.User;

import java.util.List;

import static com.unique.agent.room.TableUtils.TBL_USERS;

/**
 * Created by praveenpokuri on 06/09/17.
 */
@Dao
public interface UserDao {

    @Query("SELECT * FROM "+TBL_USERS)
    List<User> getAll();

    @Insert
    void insertAll(User... users);

    @Insert
    void insertAll(List<User> users);

    @Delete
    void delete(User user);
}

