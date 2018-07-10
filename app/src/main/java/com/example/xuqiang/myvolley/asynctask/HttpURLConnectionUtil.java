package com.example.xuqiang.myvolley.asynctask;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpURLConnectionUtil {

    private static String PREFIX = "--";
    private static String LINE_END = "\r\n";

    public static String httpURLConectionGET(String path, Map<String, String> params) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(path);

            if (url.getProtocol().toLowerCase().equals("https")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                connection = https;
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            setGetConnection(connection);

            //添加请求头
            for (Map.Entry<String, String> entry : params.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            connection.connect();

            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = inputStream.read(buffer))) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
                return baos.toString("utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private static void setGetConnection(HttpURLConnection connection) {
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(60 * 1000);
            connection.setReadTimeout(60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String httpURLConectionPost(String path, Map<String, String> params) {
        HttpURLConnection connection = null;
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try {
            URL url = new URL(path);

            if (url.getProtocol().toLowerCase().equals("https")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                connection = https;
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            String boundary = UUID.randomUUID().toString(); // 边界标识 随机生成

            setPostConnection(connection, boundary);
            connection.connect();

            if (params != null && params.size() > 0) {
                dos = new DataOutputStream(connection.getOutputStream());
                addFormField(params, dos, boundary);
            }

            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = inputStream.read(buffer))) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
                return baos.toString("utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (dos != null) {
                    dos.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void setPostConnection(HttpURLConnection connection, String boundary) {
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestProperty("Connection", "Keep-Alive");//长连接
            connection.setRequestProperty("Charset", "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addFormField(Map<String, String> params, DataOutputStream dataOutputStream, String boundary) throws IOException {
        if (params != null && params.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(PREFIX);
                sb.append(boundary);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"").append(LINE_END);
//                sb.append("Content-Type: text/plain; charset=").append("utf-8").append(LINE_END);
                sb.append(LINE_END);
                sb.append(entry.getValue());
                sb.append(LINE_END);
            }
            dataOutputStream.write(sb.toString().getBytes());//写入

//            dataOutputStream.writeBytes(PREFIX + boundary + PREFIX + LINE_END);//写入
//            dataOutputStream.flush();
        }
    }

    /**
     * 上传文件
     */
    public static String httpURLConectionPost(String path, Map<String, String> params, File file) {
        HttpURLConnection connection = null;
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try {
            URL url = new URL(path);

            if (url.getProtocol().toLowerCase().equals("https")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                connection = https;
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            String boundary = UUID.randomUUID().toString(); // 边界标识 随机生成

            setPostConnection(connection, boundary);
            connection.connect();

            if (params != null && params.size() > 0) {
                dos = new DataOutputStream(connection.getOutputStream());
                addFormField(params, dos, boundary);

                if (file != null) {
                    addImageContent(file, dos, boundary);
                }

                dos.writeBytes(PREFIX + boundary + PREFIX + LINE_END);//写入
                dos.flush();
            }


            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = inputStream.read(buffer))) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
                return baos.toString("utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (dos != null) {
                    dos.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void addImageContent(File file, DataOutputStream dataOutputStream,String boundary) throws IOException {
        //文件的上传配置：二进制流
        if (file != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append(boundary);
            sb.append(LINE_END);
            sb.append("Content-Disposition: form-data; name=\"file\";filename=\"head.jpg\"").append(LINE_END);
            sb.append("Content-Type: image/jpeg; charset=" + "utf-8").append(LINE_END);
            sb.append(LINE_END);
            dataOutputStream.write(sb.toString().getBytes());//发送图片数据

            FileInputStream is = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, len);
            }
            is.close();
            dataOutputStream.write(LINE_END.getBytes());//写入
        }
    }

    private static void trustAllHosts() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            @SuppressLint("TrustAllX509TrustManager")
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @SuppressLint("TrustAllX509TrustManager")
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HostnameVerifier DO_NOT_VERIFY = (hostname, session) -> true;

}
