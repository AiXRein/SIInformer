package com.aixrein.siinformer;

/**
 * Created by aixre on 21.11.2017.
 */

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MyHttpHelper {
    private final MD5Tools md5 = new MD5Tools();


    public boolean parseLink(String AuthorLink, Work Work, Authors Author, WorkDataSource WorkDS, AuthorsDataSource AuthorsDS, Context context, String UpdatedAuthors) {
        try {
            URL url = new URL(AuthorLink);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            if (AuthorLink.indexOf("index_") > 0)
                setWork(con.getInputStream(), AuthorLink, Work, Author, WorkDS, AuthorsDS, context, UpdatedAuthors);
            else
                readStream(con.getInputStream(), AuthorLink, Work, Author, WorkDS, AuthorsDS, context, UpdatedAuthors);
        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), "Соединение не прошло ",
            // Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return true;
    }

    void setWork(InputStream in, String AuthorLink, Work Work, Authors Author, WorkDataSource WorkDS, AuthorsDataSource AuthorsDS, Context context, String UpdatedAuthors) {
        BufferedReader reader = null;

        try {
            WorkDS = new WorkDataSource(context);
            WorkDS.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            reader = new BufferedReader(new InputStreamReader(in, "windows-1251"));

            String line = "";

            Work.setWorkType(0);

            // Разбор входящего файла
            while ((line = reader.readLine()) != null) {
                Calendar c = Calendar.getInstance();

                // Добавление данных о произведении
                if (line.indexOf("<DL><DT><li>") >= 0) {
                    Work.setAuthorID(Long.toString(Author.getID()));
                    Work.setWorkLink(Author.getAuthorLink()
                            + line.substring(
                            line.indexOf("<A HREF=") + 8,
                            line.indexOf(".shtml><b>",
                                    line.indexOf("<A HREF=") + 8) + 6));
                    Work.setWorkName(line.substring(
                            line.indexOf(".shtml><b>") + 10,
                            line.indexOf("</b></A> &nbsp; <b>",
                                    line.indexOf(".shtml><b>") + 10)));
                    if (line.indexOf("<", line.lastIndexOf("</small><br>") + 38) > 0) {
                        Work.setWorkDescription(line.substring(line
                                .indexOf("</small><br>") + 38, line.indexOf(
                                "<", line.indexOf("</small><br>") + 39)));
                        if (Work.getWorkDescription().indexOf(".shtml") > 0)
                            Work.setWorkDescription("");
                    } else Work.setWorkDescription("");


                    List<Work> ListWork = WorkDS.getAllWork(Work.getAuthorID());
                    boolean isIsset = false;

                    if (ListWork.size() > 0) {
                        for (int i = 0; i < ListWork.size(); i++) {
                            if (Work.getWorkName().equalsIgnoreCase(
                                    ListWork.get(i).getWorkName())) {
                                Work = ListWork.get(i);
                                isIsset = true;
                            }

                        }
                    }

                    if (!isIsset) {
                        Work.setWorkDigest(md5.calculateMD5(Work.getWorkLink()));
                        Work.setIsUpdated(1);
                        WorkDS.createWork(Work.getWorkName(),
                                Work.getWorkLink(), Work.getWorkType(),
                                Work.getWorkDescription(), Work.getAuthorID(),
                                Work.getWorkDigest(), getCurrentDate(c),
                                getCurrentTime(c), Work.getIsUpdated());
                        AuthorsDS.updateAuthor(Author.getAuthorName(),
                                Author.getAuthorLink(),
                                Author.getAuthorsDescription(),
                                Author.getAuthorCategory(),
                                getCurrentDate(c), getCurrentTime(c),
                                Author.getID(), Author.getIsUpdated());
                        if (UpdatedAuthors.length() > 0) UpdatedAuthors = Author.getAuthorName();
                        else {
                            if (UpdatedAuthors.indexOf(Author.getAuthorName()) <= 0 && !UpdatedAuthors.equalsIgnoreCase(Author.getAuthorName()))
                                UpdatedAuthors = UpdatedAuthors + ", " + Author.getAuthorName();
                        }
                    } else {
                        if (!md5.checkMD5(Work.getWorkDigest(), Work.getWorkLink())) {
                            Author.setIsUpdated(1);
                            Work.setIsUpdated(1);
                            Work.setWorkDigest(md5.calculateMD5(Work.getWorkLink()));
                            WorkDS.updateWork(Work.getID(), Work.getWorkName(),
                                    Work.getWorkLink(), Work.getWorkType(),
                                    Work.getWorkDescription(),
                                    Work.getAuthorID(), Work.getWorkDigest(),
                                    getCurrentDate(c), getCurrentTime(c), Work.getIsUpdated());
                            AuthorsDS.updateAuthor(Author.getAuthorName(),
                                    Author.getAuthorLink(),
                                    Author.getAuthorsDescription(),
                                    Author.getAuthorCategory(),
                                    getCurrentDate(c), getCurrentTime(c),
                                    Author.getID(), Author.getIsUpdated());
                            if (UpdatedAuthors.length() > 0)
                                UpdatedAuthors = Author.getAuthorName();
                            else {
                                if (UpdatedAuthors.indexOf(Author.getAuthorName()) <= 0 && !UpdatedAuthors.equalsIgnoreCase(Author.getAuthorName()))
                                    UpdatedAuthors = UpdatedAuthors + ", " + Author.getAuthorName();
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void readStream(InputStream in, String AuthorLink, Work Work, Authors Author, WorkDataSource WorkDS, AuthorsDataSource AuthorsDS, Context context, String UpdatedAuthors) {
        if (AuthorLink.lastIndexOf(".shtml") > 0) {
            //AuthorLink = AuthorLink.substring(0, AuthorLink.lastIndexOf(".shtml"));
            AuthorLink = AuthorLink.substring(0, AuthorLink.lastIndexOf("/") + 1);
        } else {
            if (AuthorLink.lastIndexOf("/") + 1 < AuthorLink.length())
                AuthorLink = AuthorLink + "/";
            else AuthorLink = AuthorLink.substring(0, AuthorLink.lastIndexOf("/") + 1);
        }

        BufferedReader reader = null;

        try {
            AuthorsDS = new AuthorsDataSource(context);
            AuthorsDS.open();

            WorkDS = new WorkDataSource(context);
            WorkDS.open();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            reader = new BufferedReader(new InputStreamReader(in,
                    "windows-1251"));


            String line = "";
            Author.setAuthorCategory(0);
            Work.setWorkType(0);
            List<Authors> values = AuthorsDS.getAllAuthors();

            // Разбор входящего файла
            while ((line = reader.readLine()) != null) {
                Calendar c = Calendar.getInstance();

                if (line.indexOf("><a href=index_") > 0 && line.indexOf("<b><a name=gr") >= 0) {
                    if (AuthorLink.lastIndexOf("/") > 8)
                        parseLink(AuthorLink + line.substring(line.indexOf("<a href=index_") + 8, line.indexOf(".shtml>")) + ".shtml", Work, Author, WorkDS, AuthorsDS, context, AuthorLink);
                    else
                        parseLink(AuthorLink + "/" + line.substring(line.indexOf("<a href=index_") + 8, line.indexOf(".shtml>")) + ".shtml", Work, Author, WorkDS, AuthorsDS, context, AuthorLink);
                }

                // Добавление данных об Авторе
                if (line.indexOf("<h3>") >= 0) {
                    Author.setAuthorLink(AuthorLink);
                    Author.setAuthorName(line.substring(
                            line.indexOf("<h3>") + 4, line.indexOf(":<br>")));
                    Author.setUpdDate(getCurrentDate(c));
                    Author.setUpdTime(getCurrentTime(c));

                }
                if (line.indexOf("<font color=") == 0
                        && line.indexOf("</font></h3>") > 0) {
                    Author.setAuthorsDescription(line.substring(
                            line.indexOf("font color=") + 21,
                            line.indexOf("</font></h3>")));

                    boolean isIssetAuthor = false;
                    for (int i = 0; i < values.size(); i++) {
                        if (Author.getAuthorName().equalsIgnoreCase(
                                values.get(i).getAuthorName())) {
                            isIssetAuthor = true;
                            Author.setID(values.get(i).getID());
                        }
                    }
                    if (!isIssetAuthor)
                        Author = AuthorsDS.createAuthor(
                                Author.getAuthorName(), Author.getAuthorLink(),
                                Author.getAuthorsDescription(),
                                Author.getAuthorCategory(),
                                Author.getUpdDate(), Author.getUpdTime(), Author.getIsUpdated());
                }

                // Добавление данных о произведении
                if (line.indexOf("<DL><DT><li>") >= 0) {
                    Work.setAuthorID(Long.toString(Author.getID()));
                    Work.setWorkLink(Author.getAuthorLink()
                            + line.substring(
                            line.indexOf("<A HREF=") + 8,
                            line.indexOf(".shtml><b>",
                                    line.indexOf("<A HREF=") + 8) + 6));
                    Work.setWorkName(line.substring(
                            line.indexOf(".shtml><b>") + 10,
                            line.indexOf("</b></A> &nbsp; <b>",
                                    line.indexOf(".shtml><b>") + 10)));
                    if (line.indexOf("<", line.lastIndexOf("</small><br>") + 38) > 0) {
                        Work.setWorkDescription(line.substring(line
                                .indexOf("</small><br>") + 38, line.indexOf(
                                "<", line.indexOf("</small><br>") + 39)));
                        if (Work.getWorkDescription().indexOf(".shtml") > 0)
                            Work.setWorkDescription("");
                    } else Work.setWorkDescription("");


                    List<Work> ListWork = WorkDS.getAllWork(Work.getAuthorID());
                    boolean isIsset = false;

                    if (ListWork.size() > 0) {
                        for (int i = 0; i < ListWork.size(); i++) {
                            if (Work.getWorkName().equalsIgnoreCase(
                                    ListWork.get(i).getWorkName())) {
                                Work = ListWork.get(i);
                                isIsset = true;
                            }

                        }
                    }

                    if (!isIsset) {
                        Work.setWorkDigest(md5.calculateMD5(Work.getWorkLink()));
//						Work.setWorkDigest("123");
                        Author.setIsUpdated(1);
                        Work.setIsUpdated(1);
                        WorkDS.createWork(Work.getWorkName(),
                                Work.getWorkLink(), Work.getWorkType(),
                                Work.getWorkDescription(), Work.getAuthorID(),
                                Work.getWorkDigest(), getCurrentDate(c),
                                getCurrentTime(c), Work.getIsUpdated());
                        // Toast.makeText(context, "Произведение " + '"' +
                        // newWork.getWorkName() + '"' + " добавлено.",
                        // Toast.LENGTH_SHORT).show();
                        AuthorsDS.updateAuthor(Author.getAuthorName(),
                                Author.getAuthorLink(),
                                Author.getAuthorsDescription(),
                                Author.getAuthorCategory(),
                                getCurrentDate(c), getCurrentTime(c),
                                Author.getID(), Author.getIsUpdated());
                    } else {
                        if (!md5.checkMD5(Work.getWorkDigest(), Work.getWorkLink())) {
                            Author.setIsUpdated(1);
                            Work.setIsUpdated(1);
                            Work.setWorkDigest(md5.calculateMD5(Work.getWorkLink()));
//							Work.setWorkDigest("123");
                            WorkDS.updateWork(Work.getID(), Work.getWorkName(),
                                    Work.getWorkLink(), Work.getWorkType(),
                                    Work.getWorkDescription(),
                                    Work.getAuthorID(), Work.getWorkDigest(),
                                    getCurrentDate(c), getCurrentTime(c), Work.getIsUpdated());
                            AuthorsDS.updateAuthor(Author.getAuthorName(),
                                    Author.getAuthorLink(),
                                    Author.getAuthorsDescription(),
                                    Author.getAuthorCategory(),
                                    getCurrentDate(c), getCurrentTime(c),
                                    Author.getID(), Author.getIsUpdated());
                        }
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    AuthorsDS.close();
                    WorkDS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String getCurrentDate(Calendar c) {
        String day, month, currentDate;
        if (c.get(Calendar.DAY_OF_MONTH) < 10)
            day = "0" + c.get(Calendar.DAY_OF_MONTH);
        else
            day = "" + c.get(Calendar.DAY_OF_MONTH);

        if (c.get(Calendar.MONTH) < 10)
            month = "0" + c.get(Calendar.MONTH);
        else
            month = "" + c.get(Calendar.MONTH);

        currentDate = day + "." + month + "." + c.get(Calendar.YEAR);

        return currentDate;
    }

    String getCurrentTime(Calendar c) {
        String hour, minute, second, currentTime;

        if (c.get(Calendar.HOUR_OF_DAY) < 10)
            hour = "0" + c.get(Calendar.HOUR_OF_DAY);
        else
            hour = "" + c.get(Calendar.HOUR_OF_DAY);

        if (c.get(Calendar.MINUTE) < 10)
            minute = "0" + c.get(Calendar.MINUTE);
        else
            minute = "" + c.get(Calendar.MINUTE);

        if (c.get(Calendar.SECOND) < 10)
            second = "0" + c.get(Calendar.SECOND);
        else
            second = "" + c.get(Calendar.SECOND);

        currentTime = hour + ":" + minute + ":" + second;

        return currentTime;
    }

    public String postData(String searchStr) {
        // Create a new HttpClient and Post Header
        String response = "";
        int i=0;
        try {
            URL url = new URL("http://samlib.ru/cgi-bin/seek");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            HashMap<String, String> nameValuePairs = new HashMap<String, String>();
            nameValuePairs.put("DIR", "");
            nameValuePairs.put("FIND", searchStr);
            nameValuePairs.put("PLACE", "index");
            nameValuePairs.put("JANR", "0");
            nameValuePairs.put("TYPE", "0");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(nameValuePairs));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                boolean bool = false;
                String line;

                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(), "windows-1251"));
                while ((line=br.readLine()) != null) {
                    response+=line;
                    i++;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Количество строк",Integer.toString(i));
        response=response.substring(response.indexOf("<center>"),response.lastIndexOf("</center>"));

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public ArrayList<HashMap<String,String>> SearchResultList (String responce){
        ArrayList<HashMap<String,String>> searchResultList = new ArrayList<HashMap<String,String>>();

        return searchResultList;

    }
    public static HashMap <String, String> getAlphabetList(){
        String responce = "";

        try {
            URL url = new URL("http://samlib.ru");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);


            int responseCode=conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                boolean bool = false;
                String line;

                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(), "windows-1251"));
                while ((line=br.readLine()) != null) {
                    responce+=line;
                }
                br.close();
                conn.disconnect();
            }
            else {
                responce="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> linkMap = new HashMap<String, String>();
        responce = responce.substring(responce.indexOf("<!------------------ Тело индекса -------------------->")+"<!------------------ Тело индекса --------------------><hr noshade>".length(),responce.indexOf("<a href=/z/index_z.shtml>Z</a>")+ "<a href=/z/index_z.shtml>Z</a>".length());
        Document doc = Jsoup.parse(responce);



        Elements links = doc.select("a[href]");
        for (Element link : links) {

            //put elements in the map
            linkMap.put(link.text(),link.attr("href"));
        }

        return linkMap;

    }

    public static ArrayList<HashMap<String, String>> searchAuthor(String query) {
        ArrayList<HashMap<String, String>> searchResult = new ArrayList<HashMap<String, String>>();



        return searchResult;
    }


    public static ArrayList<HashMap<String, String>> searchAuthorSuggestion(String link) {
        ArrayList<HashMap<String, String>> searchResult = new ArrayList<HashMap<String, String>>();

        String responce = "";

        try {
            URL url = new URL(link);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);


            int responseCode=conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                boolean bool = false;
                String line;

                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(), "windows-1251"));
                while ((line=br.readLine()) != null) {
                    responce+=line;
                }
                br.close();
                conn.disconnect();
            }
            else {
                responce="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        responce = responce.substring(responce.indexOf("<DL>"), responce.lastIndexOf("</DL>")+ "</DL>".length());

        Document doc = Jsoup.parse(responce);
        HashMap <String,String> resultRow = new HashMap <String,String>();


        //Elements info = doc.select("DD");
        Elements links = doc.select("a[href]");
        Elements title = doc.select("DL");
        /*for (Element author_link : links) {

            //put elements in the map
           // linkMap.put(link.text(),link.attr("href"));
        }*/

        int max = links.size();
        //if (max < info.size()) max = info.size();
        //if (max < title.size()) max = title.size();
        String info = "";
        for (int i=0;i<100;i++){
            resultRow.put("Name", links.get(i).text());
            resultRow.put("Link", links.get(i).attr("href"));
            if(title.get(i).text().indexOf(") ")>0)
                if (title.get(i).text().substring(title.get(i).text().indexOf(") ")).length()>2) info = title.get(i).text().substring(title.get(i).text().indexOf(")")+2);
            else info = "";
            resultRow.put("Info",info);
            resultRow.put("Title",title.get(i).text().substring(links.get(i).text().length()+1,title.get(i).text().indexOf("\"(")+1));
            Log.i("Результаты поиска: " + Integer.toString(i),resultRow.toString());
            searchResult.add(resultRow);
        }


        return searchResult;
    }

}
