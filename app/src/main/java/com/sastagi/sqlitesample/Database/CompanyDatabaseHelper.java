package com.sastagi.sqlitesample.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.sastagi.sqlitesample.model.CompanyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sastagi on 3/12/16.
 */
public class CompanyDatabaseHelper extends SQLiteOpenHelper {
    private static CompanyDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "companyDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_COMPANIES = "posts";

    // Company Table Columns
    private static final String COMPANY_ID = "id";
    private static final String COMPANY_TITLE = "title";
    private static final String COMPANY_SUBTITLE = "subtitle";
    private static final String COMPANY_EARNINGS = "earnings";
    private static final String COMPANY_IMAGE = "image";

    private CompanyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    public static synchronized CompanyDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new CompanyDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    @WorkerThread
    // Insert a post into the database
    public void addPost(CompanyItem companyItem) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            //long userId = addOrUpdateUser(companyItem.user);

            ContentValues values = new ContentValues();
            values.put(COMPANY_ID, companyItem.getId());
            values.put(COMPANY_TITLE, companyItem.getTitle());
            values.put(COMPANY_SUBTITLE, companyItem.getSubtitle());
            values.put(COMPANY_EARNINGS, companyItem.getEarnings());
            values.put(COMPANY_IMAGE, companyItem.getImage());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_COMPANIES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("TEST", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public List<CompanyItem> getAllCompanies() {
        List<CompanyItem> companies = new ArrayList<>();

        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
        String COMPANY_SELECT_QUERY ="SELECT * FROM "+
                        TABLE_COMPANIES;

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(COMPANY_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    CompanyItem newCompany = new CompanyItem();
                    newCompany.setTitle(cursor.getString(cursor.getColumnIndex(COMPANY_TITLE)));
                    newCompany.setSubtitle(cursor.getString(cursor.getColumnIndex(COMPANY_SUBTITLE)));
                    newCompany.setEarnings(cursor.getString(cursor.getColumnIndex(COMPANY_EARNINGS)));
                    companies.add(newCompany);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("TEST", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return companies;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COMPANIES_TABLE = "CREATE TABLE " + TABLE_COMPANIES +
                "(" +
                COMPANY_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                COMPANY_TITLE + "," + COMPANY_SUBTITLE + "," + COMPANY_IMAGE + "," +// Define a foreign key
                COMPANY_EARNINGS + " INTEGER" +
                ")";

        db.execSQL(CREATE_COMPANIES_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANIES);
            onCreate(db);
        }
    }
}
