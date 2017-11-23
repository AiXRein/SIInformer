package com.aixrein.siinformer;

/**
 * Created by aixre on 21.11.2017.
 */

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

class MD5Tools {
    private HttpURLConnection con;

    public boolean checkMD5(String md5, String link) {
        if (md5 == null || md5.equals("") || link == null) {
            return false;
        }
        else
        {
            String calculatedDigest = calculateMD5(link);
            return calculatedDigest != null && calculatedDigest.equalsIgnoreCase(md5);
        }
    }

    public String calculateMD5(String link) {
        BufferedReader reader = null;
        MessageDigest digest = null;
        //   byte[] buffer = new byte[8192];
        //    int read = 0;
        boolean work = false;
        //  ByteArrayInputStream stream;
        try {
            URL url = new URL(link);
            con = (HttpURLConnection) url.openConnection();

            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {
                reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "windows-1251"));
                String line = "";
                List<String> WorkContent = new ArrayList<String>();
                // Разбор входящего файла
                while ((line = reader.readLine()) != null) {
                    if (line.contains("<!----------- Собственно произведение --------------->"))
                        work = true;
                    if (line.contains("<!--------------------------------------------------->") && work)
                        work = false;
                    if (work) WorkContent.add(line);
                }

                StringBuilder sb = new StringBuilder();
                for (String s : WorkContent) {
                    sb.append(s);
                }
                line = sb.toString();
                digest.update(line.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process link for MD5", e);
        } finally {
            try {
                con.getInputStream().close();
            } catch (IOException e) {
                throw new RuntimeException(
                        "Unable to close input stream for MD5 calculation", e);
            }
        }
    }
}

