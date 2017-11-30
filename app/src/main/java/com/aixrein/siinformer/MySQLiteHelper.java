package com.aixrein.siinformer;

/**
 * Created by aixre on 21.11.2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class MySQLiteHelper extends SQLiteOpenHelper {

    // Описание таблиц
    public static final String TABLE_AUTHORS = "authors";
    public static final String TABLE_WORK = "work";
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_WORK_TYPE = "work_type";
    private static final String TABLE_SEARCH_LNKS = "search_links";

    // Описание таблицы авторов
    public static final String COLUMN_AUTHOR_ID = "_id";
    public static final String COLUMN_AUTHOR_NAME = "author_name";
    public static final String COLUMN_AUTHOR_LINK = "author_link";
    public static final String COLUMN_AUTHOR_DESCRIPRTION = "author_description";
    public static final String COLUMN_AUTHOR_CATEGORY = "author_category";
    public static final String COLUMN_AUTHOR_UPD_DATE = "upd_date";
    public static final String COLUMN_AUTHOR_UPD_TIME = "upd_time";
    public static final String COLUMN_AUTHOR_IS_UPDATED = "is_updated";

    // Описание таблицы произведений
    public static final String COLUMN_WORK_ID = "_id";
    public static final String COLUMN_WORK_NAME = "work_name";
    public static final String COLUMN_WORK_LINK = "work_link";
    public static final String COLUMN_WORK_TYPE = "work_type";
    public static final String COLUMN_WORK_DESCRIPRTION = "work_description";
    public static final String COLUMN_WORK_AUTHOR_ID = "author_id";
    public static final String COLUMN_WORK_DIGEST = "work_digest";
    public static final String COLUMN_WORK_UPD_DATE = "upd_date";
    public static final String COLUMN_WORK_UPD_TIME = "upd_time";
    public static final String COLUMN_WORK_IS_UPDATED = "is_updated";


    // Описание справочника категорий авторов
    private static final String COLUMN_CATEGORY_ID = "_id";
    private static final String COLUMN_CATEGORY_NAME = "category";

    // Описание справочника типов произведений
    private static final String COLUMN_WORK_TYPE_ID = "_id";
    private static final String COLUMN_WORK_TYPE_NAME = "type";

    // Описание таблицы ссылок
    private static final String COLUMN_SEARCH_LINK_ID = "_id";
    private static final String COLUMN_SEARCH_LINK_LETTER = "letter";
    private static final String COLUMN_SEARCH_LINK_LINK = "link";

    private static final String DATABASE_NAME = "siinformer.db";
    private static final int DATABASE_VERSION = 1;



    // Database creation sql statement
    private static final String DATABASE_CREATE_TABLE_AUTHORS = "create table "
            + TABLE_AUTHORS + "(" + COLUMN_AUTHOR_ID
            + " integer primary key autoincrement, " + COLUMN_AUTHOR_NAME
            + " text not null, " + COLUMN_AUTHOR_LINK + " text not null, "
            + COLUMN_AUTHOR_DESCRIPRTION + " text not null, "
            + COLUMN_AUTHOR_CATEGORY + " integer, " + COLUMN_AUTHOR_UPD_DATE
            + " text not null, " + COLUMN_AUTHOR_UPD_TIME + " text not null, " + COLUMN_AUTHOR_IS_UPDATED + " integer); ";

    private static final String DATABASE_CREATE_TABLE_WORK = "create table "
            + TABLE_WORK + "(" + COLUMN_WORK_ID
            + " integer primary key autoincrement, " + COLUMN_WORK_NAME
            + " text not null, " + COLUMN_WORK_LINK + " text not null, "
            + COLUMN_WORK_TYPE + " integer, " + COLUMN_WORK_DESCRIPRTION
            + " text not null, " + COLUMN_WORK_AUTHOR_ID + " integer, "
            + COLUMN_WORK_DIGEST + " text not null, " + COLUMN_WORK_UPD_DATE
            + " text not null, " + COLUMN_WORK_UPD_TIME + " text not null, " + COLUMN_WORK_IS_UPDATED + " integer); ";

    private static final String DATABASE_CREATE_TABLE_CATEGORY = "create table "
            + TABLE_CATEGORY
            + "("
            + COLUMN_CATEGORY_ID
            + " integer primary key autoincrement, "
            + COLUMN_CATEGORY_NAME
            + " text not null); ";

    private static final String DATABASE_CREATE_TABLE_WORK_TYPE = "create table "
            + TABLE_WORK_TYPE
            + "("
            + COLUMN_WORK_TYPE_ID
            + " integer primary key autoincrement, "
            + COLUMN_WORK_TYPE_NAME
            + " text not null); ";

    private static final String DATABASE_CREATE_TABLE_SEARCH_LINKS = "create table "
            + TABLE_SEARCH_LNKS
            + "("
            + COLUMN_SEARCH_LINK_ID
            + " integer primary key autoincrement, "
            + COLUMN_SEARCH_LINK_LETTER
            + " text not null, "
            + COLUMN_SEARCH_LINK_LINK
            + " text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TABLE_AUTHORS);
        database.execSQL(DATABASE_CREATE_TABLE_WORK);
        database.execSQL(DATABASE_CREATE_TABLE_CATEGORY);
        database.execSQL(DATABASE_CREATE_TABLE_WORK_TYPE);
        database.execSQL(DATABASE_CREATE_TABLE_SEARCH_LINKS);

        // ImportSampleData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORK_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_LNKS);
        onCreate(db);
    }


}