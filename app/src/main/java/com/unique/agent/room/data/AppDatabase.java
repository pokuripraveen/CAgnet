package com.unique.agent.room.data;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.unique.agent.datastore.db.UserContentHelper;
import com.unique.agent.room.dao.UserDao;
import com.unique.agent.room.model.User;

import static com.unique.agent.room.TableUtils.USER_DB;

/**
 * Created by praveenpokuri on 06/09/17.
 */
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, USER_DB)
                                    .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao getUserDao();
}
