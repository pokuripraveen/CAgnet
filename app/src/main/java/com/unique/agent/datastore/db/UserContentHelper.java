package com.unique.agent.datastore.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.unique.agent.datastore.model.DataScheme;
/**
 * Created by praveenpokuri on 04/07/17.
 */

public class UserContentHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "users.db";

    private static UserContentHelper sSingleton;


    private UserContentHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    private UserContentHelper(final Context context, final String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    public static UserContentHelper getInstance(final Context context) {
        if (sSingleton == null) {
            synchronized (UserContentHelper.class) {
                if (sSingleton == null) {
                    sSingleton = new UserContentHelper(context, DATABASE_NAME);
                }
            }
        }
        return sSingleton;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DataScheme.DDL_CREATE_TBL_USER_LABOR);
        sqLiteDatabase.execSQL(DataScheme.DDL_CREATE_TBL_USER_OWNER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

