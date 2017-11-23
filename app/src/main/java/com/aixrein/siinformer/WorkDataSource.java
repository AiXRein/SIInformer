package com.aixrein.siinformer;

/**
 * Created by aixre on 21.11.2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

class WorkDataSource {
    private SQLiteDatabase database;
    private final MySQLiteHelper dbHelper;
    private final String[] allColumns = {MySQLiteHelper.COLUMN_WORK_ID,
            MySQLiteHelper.COLUMN_WORK_NAME,
            MySQLiteHelper.COLUMN_WORK_LINK,
            MySQLiteHelper.COLUMN_WORK_TYPE,
            MySQLiteHelper.COLUMN_WORK_DESCRIPRTION,
            MySQLiteHelper.COLUMN_WORK_AUTHOR_ID,
            MySQLiteHelper.COLUMN_WORK_DIGEST,
            MySQLiteHelper.COLUMN_WORK_UPD_DATE,
            MySQLiteHelper.COLUMN_WORK_UPD_TIME,
            MySQLiteHelper.COLUMN_WORK_IS_UPDATED};

    public WorkDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Work createWork(String WorkName, String WorkLink, int WorkType,
                           String WorkDescription, String AuthorID, String WorkDigest,
                           String UpdDate, String UpdTime, int isUpdated) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_WORK_NAME, WorkName);
        values.put(MySQLiteHelper.COLUMN_WORK_LINK, WorkLink);
        values.put(MySQLiteHelper.COLUMN_WORK_TYPE, WorkType);
        values.put(MySQLiteHelper.COLUMN_WORK_DESCRIPRTION, WorkDescription);
        values.put(MySQLiteHelper.COLUMN_WORK_AUTHOR_ID, AuthorID);
        values.put(MySQLiteHelper.COLUMN_WORK_DIGEST, WorkDigest);
        values.put(MySQLiteHelper.COLUMN_WORK_UPD_DATE, UpdDate);
        values.put(MySQLiteHelper.COLUMN_WORK_UPD_TIME, UpdTime);
        values.put(MySQLiteHelper.COLUMN_WORK_IS_UPDATED, isUpdated);

        long insertId = database
                .insert(MySQLiteHelper.TABLE_WORK, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_WORK, allColumns,
                MySQLiteHelper.COLUMN_WORK_ID + " = " + insertId, null, null,
                null, null);

        cursor.moveToFirst();
        Work newWork = cursorToWork(cursor);
        cursor.close();
        return newWork;
    }

    public void updateWork(Long WorkID, String WorkName, String WorkLink,
                           int WorkType, String WorkDescription, String AuthorID,
                           String WorkDigest, String UpdDate, String UpdTime, int isUpdated) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_WORK_NAME, WorkName);
        values.put(MySQLiteHelper.COLUMN_WORK_LINK, WorkLink);
        values.put(MySQLiteHelper.COLUMN_WORK_TYPE, WorkType);
        values.put(MySQLiteHelper.COLUMN_WORK_DESCRIPRTION, WorkDescription);
        values.put(MySQLiteHelper.COLUMN_WORK_AUTHOR_ID, AuthorID);
        values.put(MySQLiteHelper.COLUMN_WORK_DIGEST, WorkDigest);
        values.put(MySQLiteHelper.COLUMN_WORK_UPD_DATE, UpdDate);
        values.put(MySQLiteHelper.COLUMN_WORK_UPD_TIME, UpdTime);
        values.put(MySQLiteHelper.COLUMN_WORK_IS_UPDATED, isUpdated);

        String[] WhereClause = {WorkID.toString()};
        database.update(MySQLiteHelper.TABLE_WORK, values,
                MySQLiteHelper.COLUMN_WORK_ID + " = ?", WhereClause);
    }

    public void deleteWork(Work Work) {
        long id = Work.getID();
        String WorkName = Work.getWorkName();
        System.out.println("Work:" + WorkName + "deleted ");
        database.delete(MySQLiteHelper.TABLE_WORK,
                MySQLiteHelper.COLUMN_WORK_ID + " = " + id, null);
    }

    public List<Work> getAllWork(String AuthorID) {
        List<Work> Work = new ArrayList<Work>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_WORK, allColumns,
                MySQLiteHelper.COLUMN_WORK_AUTHOR_ID + " = " + AuthorID, null,
                null, null, MySQLiteHelper.COLUMN_WORK_IS_UPDATED + " DESC, " + MySQLiteHelper.COLUMN_WORK_UPD_DATE + " DESC," + MySQLiteHelper.COLUMN_WORK_UPD_TIME + " DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Work Author = cursorToWork(cursor);
            Work.add(Author);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return Work;
    }

    public String getWorkLink(String Name) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_WORK, allColumns,
                MySQLiteHelper.COLUMN_WORK_NAME + " = " + Name, null, null,
                null, null);
        cursor.moveToFirst();
        Work newWork = cursorToWork(cursor);
        cursor.close();

        return newWork.getWorkLink();
    }

    private Work cursorToWork(Cursor cursor) {
        Work Work = new Work();
        Work.setID(cursor.getLong(0));
        Work.setWorkName(cursor.getString(1));
        Work.setWorkLink(cursor.getString(2));
        Work.setWorkType(cursor.getInt(3));
        Work.setWorkDescription(cursor.getString(4));
        Work.setAuthorID(cursor.getString(5));
        Work.setWorkDigest(cursor.getString(6));
        Work.setUpdDate(cursor.getString(7));
        Work.setUpdTime(cursor.getString(8));
        Work.setIsUpdated(cursor.getInt(9));

        return Work;
    }

    public void setWorkUpdateStatus(String link) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_WORK, allColumns,
                MySQLiteHelper.COLUMN_WORK_LINK + " = " + "'" + link + "'", null, null,
                null, null);
        cursor.moveToFirst();
        Work Work = cursorToWork(cursor);
        cursor.close();
        Work.setIsUpdated(0);
        updateWork(Work.getID(), Work.getWorkName(),
                Work.getWorkLink(), Work.getWorkType(),
                Work.getWorkDescription(),
                Work.getAuthorID(), Work.getWorkDigest(),
                Work.getUpdDate(), Work.getUpdTime(), Work.getIsUpdated());

    }
}

