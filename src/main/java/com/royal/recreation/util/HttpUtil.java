package com.royal.recreation.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpUtil {

    public static String httpGet(String url) {
        HttpGet get = new HttpGet(url);
        get.setConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());
        return getResult(get);
    }

    private static String getResult(HttpGet get) {
        HttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
