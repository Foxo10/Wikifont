package com.example.wikifountains.api;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Clase de utilidad para solicitudes HTTP.
 */
public class UserApi {
    private static final String BASE_URL = "http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/odiez016/WEB/";

    public static JSONObject login(String name, String password) throws Exception {
        return post("login.php", Map.of("name", name, "password", password));
    }

    public static JSONObject register(String name, String email, String password) throws Exception {
        return post("register.php", Map.of("name", name, "email", email, "password", password));
    }

    public static JSONObject updatePhoto(String email, String image) throws Exception {
        return post("update_photo.php", Map.of("email", email, "image", image));
    }

    private static JSONObject post(String endpoint, Map<String, String> params) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, String> e : params.entrySet()) {
            joiner.add(URLEncoder.encode(e.getKey(), "UTF-8") + "=" + URLEncoder.encode(e.getValue(), "UTF-8"));
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
        writer.write(joiner.toString());
        writer.flush();
        writer.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();
        conn.disconnect();
        return new JSONObject(result.toString());
    }
}