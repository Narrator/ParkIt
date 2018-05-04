package com.getparkit.parkit.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.getparkit.parkit.Classes.UserAccess;

import org.threeten.bp.OffsetDateTime;

import java.util.Arrays;


public class Helper extends SQLiteOpenHelper {

    // String constants that contain our SQLite DB info
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "ParkIt.db";
    private static final String TABLE_NAME = "user_access";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_ACCESS_TOKEN = "access_token";
    private static final String COLUMN_REFRESH_TOKEN = "refresh_token";
    private static final String COLUMN_TTL = "ttl";
    private static final String COLUMN_AT_CREATED = "at_created";
    private static final String COLUMN_ROLES = "roles";
    private static final String COLUMN_CURRENT_ROLE = "current_role";

    SQLiteDatabase db;

    // The following is the create Table query that'll be run when
    // the app is first run and on subsequent updates
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME +
            " (" + COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, " + COLUMN_USER_ID +
            " DOUBLE NOT NULL, " + COLUMN_ACCESS_TOKEN + " TEXT NOT NULL, " + COLUMN_REFRESH_TOKEN +
            " TEXT NOT NULL, " + COLUMN_TTL + " DOUBLE NOT NULL, " + COLUMN_AT_CREATED + " DATETIME NOT NULL, " +
            COLUMN_ROLES + " TEXT NOT NULL, " + COLUMN_CURRENT_ROLE + " TEXT NOT NULL );";

    // this is the helper's constructor
    public Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // insert query method
    public void insertUserAccess (UserAccess u) {
        db = this.getWritableDatabase();
        // store all the insert values into a container
        ContentValues values = new ContentValues();

        // below: define setters and getters
        // Set
        String query = "SELECT * FROM " + TABLE_NAME;

        // cursor will contain the rows that are queried
        Cursor cursor = db.rawQuery(query, null);

        // The following will give us a count of the number of rows
        int count = cursor.getCount();

        if (count < 1) {
            values.put(COLUMN_ID, 1);
        } else {
            values.put(COLUMN_ID, count +1);
        }

        // We then use the count as the ID for the next entry into the DB

        values.put(COLUMN_USER_ID, u.getUserId());
        values.put(COLUMN_ACCESS_TOKEN, u.getAccessToken());
        values.put(COLUMN_REFRESH_TOKEN, u.getRefreshToken());
        values.put(COLUMN_TTL, u.getTtl());
        values.put(COLUMN_AT_CREATED, u.getCreated().toString());
        if (u.getRoles() != null) {
            values.put(COLUMN_ROLES, TextUtils.join(",", u.getRoles()));
        } else {
            values.put(COLUMN_ROLES, "");
        }
        values.put(COLUMN_CURRENT_ROLE, u.getCurrentRole());

        // The .insert helper method will execute our container
        db.insert(TABLE_NAME, null, values);
        // Close the connection after all queries
        db.close();
    }

    // update query method
    public UserAccess updateUserAccess (UserAccess u) {
        db = this.getWritableDatabase();
        // store all the insert values into a container
        ContentValues values = new ContentValues();

        // We then use the count as the ID for the next entry into the DB
        values.put(COLUMN_USER_ID, u.getUserId());
        values.put(COLUMN_ACCESS_TOKEN, u.getAccessToken());
        values.put(COLUMN_REFRESH_TOKEN, u.getRefreshToken());
        values.put(COLUMN_TTL, u.getTtl());
        values.put(COLUMN_AT_CREATED, u.getCreated().toString());
        if (u.getRoles() != null) {
            values.put(COLUMN_ROLES, TextUtils.join(",", u.getRoles()));
        } else {
            values.put(COLUMN_ROLES, "");
        }
        values.put(COLUMN_CURRENT_ROLE, u.getCurrentRole());

        // The .insert helper method will execute our container
        db.update(TABLE_NAME, values,"id=" + u.getId(), null);
        // Close the connection after all queries
        db.close();
        return u;
    }

    // Select query method to validate user login and find the password
    public UserAccess searchUserAccess () {
        db = this.getReadableDatabase();

        // The following query will pull all usernames and passwords from DB
        String query = "SELECT * FROM " + TABLE_NAME;
        // cursor will hold the results
        Cursor cursor = db.rawQuery(query, null);
        UserAccess b = new UserAccess();

        // If there is atleast one row in the cursor
        if (cursor.moveToFirst()) {
            // Execute do..while loop to iterate through the results
            do {
                // Capture the userId
                b.setUserId(cursor.getDouble(1));
                b.setAccessToken(cursor.getString(2));
                b.setRefreshToken(cursor.getString(3));
                b.setTtl(cursor.getDouble(4));
                b.setCreated(OffsetDateTime.parse(cursor.getString(5)));
                b.setRoles(Arrays.asList(TextUtils.split(cursor.getString(6),",")));

            } while (cursor.moveToNext());
        }
        db.close();
        return b;
    }

    public void deleteAll () {
        db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        db.close();
    }

    // Override method to initialize our database table
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute the table creation query
        db.execSQL(TABLE_CREATE);
        this.db = db;
    }

    // Override the onUpgrade method to update our tables when there
    // is an upgrade to the schema
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // drop old table
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        //  Call onCreate to recreate new table.
        this.onCreate(db);

    }
}
