package com.aixrein.siinformer;

import android.os.AsyncTask;

/**
 * Created by aixre on 27.11.2017.
 */

public class AutoCompleteSearch extends AsyncTask <Object, Void, Object[]> {

    Object[] searchResult;
    AddAuthorActivity addAuthorActivity;

    public void search(AddAuthorActivity addAuthorActivity) {
        this.addAuthorActivity = addAuthorActivity;
    }

    protected Object[] doInBackground(Object[] objects) {

        return searchResult;

    }
}
