package com.unique.agent.datastore.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.unique.agent.datastore.db.UserContentHelper;
import com.unique.agent.utils.AppUtil;

/**
 * Created by praveenpokuri on 04/07/17.
 */

public class UserContentDao {

    private static final String TAG = UserContentDao.class.getSimpleName();
    private UserContentHelper mDbHelper;


    public UserContentDao(Context context) {
        mDbHelper = UserContentHelper.getInstance(context);
    }

    public void createUser(User user) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (db != null && !isUserExisted(user)) {
            if (user.getUserType() == User.UserType.LABOR) {
                db.insert(DataScheme.TBL_USER_LABOR, null, getUserLaborContentValues(user));
            } else if (user.getUserType() == User.UserType.OWNER) {
                db.insert(DataScheme.TBL_USER_OWNER, null, getUserOwnerContentValues(user));
            }
        }
    }

    private boolean isUserExisted(User user) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (user.getUserType() == User.UserType.OWNER) {
            String[] columns = {DataScheme.USER_OWNER.KEY_ID};
            String where = DataScheme.USER_OWNER.KEY_PH_NO + " = " + user.getPhone();
            String orderBy = DataScheme.USER_OWNER.KEY_ID + " ASC";
            Cursor userCursor = db.query(DataScheme.TBL_USER_OWNER, columns, where, null, null, null, orderBy);
            if (userCursor != null && userCursor.getCount() > 0) {
                return true;
            }
        } else if (user.getUserType() == User.UserType.LABOR) {
            String[] columns = {DataScheme.USER_LABOR.KEY_ID};
            String where = DataScheme.USER_LABOR.KEY_PH_NO + " = " + user.getPhone();
            String orderBy = DataScheme.USER_LABOR.KEY_ID + " ASC";
            Cursor userCursor = db.query(DataScheme.TBL_USER_LABOR, columns, where, null, null, null, orderBy);
            if (userCursor != null && userCursor.getCount() > 0) {
                return true;
            }
        }

        Log.i(TAG, "Cursor may NULL or with zero columns SO user Not Existed....!!!!! ");
        return false;
    }

    private ContentValues getUserLaborContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(DataScheme.USER_LABOR.KEY_NAME, user.getName());
        values.put(DataScheme.USER_LABOR.KEY_PASSWORD, AppUtil.encode(user.getPassword()));
        values.put(DataScheme.USER_LABOR.KEY_ADDRESS, user.getAddress());
        values.put(DataScheme.USER_LABOR.KEY_PH_NO, user.getPhone());
        values.put(DataScheme.USER_LABOR.KEY_EMAIL, user.getEmail());
        values.put(DataScheme.USER_LABOR.KEY_IMAGE, user.getAvatar());
        return values;
    }

    private ContentValues getUserOwnerContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(DataScheme.USER_OWNER.KEY_NAME, user.getName());
        values.put(DataScheme.USER_OWNER.KEY_PASSWORD, AppUtil.encode(user.getPassword()));
        values.put(DataScheme.USER_OWNER.KEY_ADDRESS, user.getAddress());
        values.put(DataScheme.USER_OWNER.KEY_PH_NO, user.getPhone());
        values.put(DataScheme.USER_OWNER.KEY_EMAIL, user.getEmail());
        values.put(DataScheme.USER_OWNER.KEY_IMAGE, user.getAvatar());
        return values;
    }
}
