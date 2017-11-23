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

class AuthorsDataSource {
    private SQLiteDatabase database;
    private final MySQLiteHelper dbHelper;
    private final String[] allColumns = {MySQLiteHelper.COLUMN_AUTHOR_ID,
            MySQLiteHelper.COLUMN_AUTHOR_NAME,
            MySQLiteHelper.COLUMN_AUTHOR_LINK,
            MySQLiteHelper.COLUMN_AUTHOR_DESCRIPRTION,
            MySQLiteHelper.COLUMN_AUTHOR_CATEGORY,
            MySQLiteHelper.COLUMN_AUTHOR_UPD_DATE,
            MySQLiteHelper.COLUMN_AUTHOR_UPD_TIME,
            MySQLiteHelper.COLUMN_AUTHOR_IS_UPDATED};

    private final String[] allColumnsWork = {MySQLiteHelper.COLUMN_WORK_ID,
            MySQLiteHelper.COLUMN_WORK_NAME,
            MySQLiteHelper.COLUMN_WORK_LINK,
            MySQLiteHelper.COLUMN_WORK_TYPE,
            MySQLiteHelper.COLUMN_WORK_DESCRIPRTION,
            MySQLiteHelper.COLUMN_WORK_AUTHOR_ID,
            MySQLiteHelper.COLUMN_WORK_DIGEST,
            MySQLiteHelper.COLUMN_WORK_UPD_DATE,
            MySQLiteHelper.COLUMN_WORK_UPD_TIME,
            MySQLiteHelper.COLUMN_WORK_IS_UPDATED};

    public AuthorsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void updateAuthor(String AuthorName, String AuthorLink,
                             String AuthorDescription, int AuthorCategory, String UpdDate,
                             String UpdTime, Long AuthorID, int isUpdated) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_AUTHOR_ID, AuthorID);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_NAME, AuthorName);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_LINK, AuthorLink);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_DESCRIPRTION, AuthorDescription);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_CATEGORY, AuthorCategory);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_UPD_DATE, UpdDate);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_UPD_TIME, UpdTime);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_UPD_TIME, UpdTime);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_IS_UPDATED, isUpdated);
        String[] WhereClause = {AuthorID.toString()};
        database.update(MySQLiteHelper.TABLE_AUTHORS, values,
                MySQLiteHelper.COLUMN_AUTHOR_ID + " = ?", WhereClause);

    }

    public Authors createAuthor(String AuthorName, String AuthorLink,
                                String AuthorDescription, int AuthorCategory, String UpdDate,
                                String UpdTime, int isUpdated) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_AUTHOR_NAME, AuthorName);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_LINK, AuthorLink);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_DESCRIPRTION, AuthorDescription);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_CATEGORY, AuthorCategory);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_UPD_DATE, UpdDate);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_UPD_TIME, UpdTime);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_IS_UPDATED, isUpdated);
        long insertId = database.insert(MySQLiteHelper.TABLE_AUTHORS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_AUTHORS,
                allColumns, MySQLiteHelper.COLUMN_AUTHOR_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Authors newAuthor = cursorToAuthor(cursor);
        cursor.close();
        return newAuthor;
    }

    public void deleteAuthor(Authors Author) {
        long id = Author.getID();
        String AuthorName = Author.getAuthorName();
        System.out.println("Author:" + AuthorName + "deleted ");
        database.delete(MySQLiteHelper.TABLE_WORK,
                MySQLiteHelper.COLUMN_WORK_AUTHOR_ID + " = " + id, null);
        database.delete(MySQLiteHelper.TABLE_AUTHORS,
                MySQLiteHelper.COLUMN_AUTHOR_ID + " = " + id, null);

    }

    public List<Authors> getAllAuthors() {
        List<Authors> Authors = new ArrayList<Authors>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_AUTHORS,
                allColumns, null, null, null, null,
                MySQLiteHelper.COLUMN_AUTHOR_UPD_DATE + " DESC ,"
                        + MySQLiteHelper.COLUMN_AUTHOR_UPD_TIME + " DESC ," + MySQLiteHelper.COLUMN_AUTHOR_IS_UPDATED + " DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Authors Author = cursorToAuthor(cursor);
            Authors.add(Author);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return Authors;
    }

    private Authors cursorToAuthor(Cursor cursor) {
        Authors Author = new Authors();
        Author.setID(cursor.getLong(0));
        Author.setAuthorName(cursor.getString(1));
        Author.setAuthorLink(cursor.getString(2));
        Author.setAuthorsDescription(cursor.getString(3));
        Author.setAuthorCategory(cursor.getInt(4));
        Author.setUpdDate(cursor.getString(5));
        Author.setUpdTime(cursor.getString(6));
        Author.setIsUpdated(cursor.getInt(7));
        return Author;
    }

    public boolean getWorkUpdateStatus(long id) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_WORK, allColumnsWork,
                MySQLiteHelper.COLUMN_WORK_IS_UPDATED + " > 0 and " + MySQLiteHelper.COLUMN_WORK_AUTHOR_ID + "=" + id, null, null,
                null, null);
        return cursor.getCount() > 0;

    }
}
