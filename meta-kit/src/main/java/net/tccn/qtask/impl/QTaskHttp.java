package net.tccn.qtask.impl;

import net.tccn.qtask.QTask;
import net.tccn.qtask.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: liangxianyou at 2019/1/20 11:07.
 */
public class QTaskHttp extends QTaskAbs implements QTask {

    public QTaskHttp(Task task) {
        super(task);
    }

    private static Map<String, HttpURLConnection> connetions = new HashMap();

    private URLConnection getConnection() {
        try {
            URI uri = URI.create(getTask().getDbAccount().getUrl());
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            StringBuilder content = new StringBuilder();
            getPara().forEach((k, v) -> {
                content.append(k).append("=").append(v).append("&");
            });
            if (content.length() > 0) {
                content.deleteCharAt(content.length() - 1);
            }

            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            //connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String encode = content.toString().replaceAll(" ", "%20");
            System.out.println(encode);
            //connection.setRequestProperty("Content-Length", String.valueOf(content.length()));
            //connection.getOutputStream().write(encode.getBytes());

            connetions.put(getTask().getDbAccount().getUrl(), connection);
            return connection;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Object execute() {
        URLConnection connection = getConnection();
        try (
                InputStream is = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(reader);

        ){
            String content = "";
            String line;
            while ((line = br.readLine()) != null) {
                content += line + System.lineSeparator();
            }
            return content;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }
}
