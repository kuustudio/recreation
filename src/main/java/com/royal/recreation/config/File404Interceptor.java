package com.royal.recreation.config;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class File404Interceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (response.getStatus() == 404) {
            if (notIgnore(request.getRequestURI())) {
                action404URI(request.getRequestURI());
            }
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

    private static void action404URI(String uri) {
        try {
            String url = ORIGIN_URL_PREFIX + uri;
            HttpGet get = new HttpGet(url);
            HttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String[] strArr = url.replaceFirst(ORIGIN_URL_PREFIX, "").split("/");
                String[] dirs = Arrays.copyOfRange(strArr, 1, strArr.length - 1);
                File dir = Paths.get(TARGET_DIR, dirs).toFile();
                dir.mkdirs();
                FileOutputStream os = new FileOutputStream(new File(dir, strArr[strArr.length - 1]));
                entity.writeTo(os);
                os.close();
                System.out.println("自动下载文件:" + uri);
            } else {
                System.out.println(url + ":" + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String ORIGIN_URL_PREFIX = "http://www.oc66666.com";
    private static final String TARGET_DIR = "D:\\git\\gitee\\recreation\\src\\main\\resources\\static";

    private static Set<String> ignoreURISet = new HashSet<String>() {{
        add("/error");
    }};

    private static boolean notIgnore(String requestURI) {
//        return !ignoreURISet.contains(requestURI);\
        return !requestURI.startsWith("/index.php");
    }

}