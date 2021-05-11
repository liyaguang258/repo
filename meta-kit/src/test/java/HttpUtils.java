
import org.redkale.convert.json.JsonConvert;
import org.redkale.net.http.MimeType;

import javax.net.ssl.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpUtils {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static File downloadFile(String url, File baseFile, Map<String, String> headers) {
        if (baseFile.isDirectory()) {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            baseFile = new File(baseFile, fileName);
        }

        baseFile.getParentFile().mkdirs();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        if (headers != null && !headers.isEmpty()) headers.forEach(requestBuilder::header);

        HttpRequest request = requestBuilder.GET().uri(URI.create(url)).build();

        try {
            HttpResponse<Path> client = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofFile(Path.of(baseFile.toURI())));
            return client.body().toFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpResponse<String> send(String url, Object post_params, HttpMethod method) {
        return send(url, post_params, method, (Map<String, String>) null);
    }

    public static HttpResponse<String> send(String url, Object post_params, HttpMethod method, SSLContext sslContext) {
        return send(url, post_params, method, null, sslContext);
    }

    public static HttpResponse<String> send(String url, Object post_params, HttpMethod method, Map<String, String> headers) {
        return send(url, post_params, null, method, headers, null);
    }

    public static HttpResponse<String> send(String url, Object post_params, HttpMethod method, Map<String, String> headers, SSLContext sslContext) {
        return send(url, post_params, null, method, headers, sslContext);
    }

    public static HttpResponse<String> send(String url, Object post_params, Map<String, File> files) {
        return send(url, post_params, files, HttpMethod.POST_FORM, null, null);
    }

    public static HttpResponse<String> send(String url, Object post_params, Map<String, File> files, Map<String, String> headers) {
        return send(url, post_params, files, HttpMethod.POST_FORM, headers, null);
    }

    private static HttpResponse<String> send(String url, Object post_params, Map<String, File> files, HttpMethod method, Map<String, String> headers, SSLContext sslContext) {
        String boundary = "NelsonCreateBoundary";
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        final HttpClient.Builder builder = HttpClient.newBuilder();
        if (sslContext != null) builder.sslContext(sslContext);
        HttpClient client = builder.build();

        if (!Utils.isEmpty(headers)) {
            headers.forEach(requestBuilder::header);
        }
        try {
            switch (method) {
                case GET -> {
                    url = generateUrlParams(url, post_params);
                    requestBuilder.GET();
                }
                case PUT -> {
                    requestBuilder.header("Content-Type", "application/json");
                    requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(convertObject(post_params)));
                }
                case POST_FORM -> {
                    requestBuilder.header("Content-Type", "multipart/form-data;boundary=" + boundary);
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofInputStream(() -> createFormDataBody(boundary, obj2Map(post_params), files)));
                }
                case POST_X -> {
                    String contentType = headers != null ? headers.get("Content-Type") : null;
                    if (contentType == null) {
                        contentType = "application/x-www-form-urlencoded";
                    }
                    String baseParams = post_params instanceof String ? (String) post_params : contentType.startsWith("application/json") ? convertObject(post_params) : generateBaseParams(obj2Map(post_params));
                    requestBuilder.header("Content-Type", contentType);
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(baseParams));
                }
                case DELETE -> {
                    url = generateUrlParams(url, post_params);
                    requestBuilder.DELETE();
                }
            }
            requestBuilder.uri(URI.create(url));
            return client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, Object> obj2Map(Object obj) {
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        } else {
            LinkedHashMap<String, Object> ret = new LinkedHashMap<>();
            Arrays.stream(obj.getClass().getMethods())
                    .filter(f -> f.getName().startsWith("get") && f.getParameterCount() == 0)
                    .forEach(method -> {
                        String k = Utils.toLowerCaseFirst(method.getName().substring(3));
                        Object v = null;
                        try {
                            method.setAccessible(true);
                            v = method.invoke(obj);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ret.put(k, v);
                    });

            return ret;


//            k -> Utils.toLowerCaseFirst(k.getName().substring(3)), v -> {
//                try {
//                    v.setAccessible(true);
//                    return v.invoke(obj);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
        }
    }

    private static ByteArrayInputStream createFormDataBody(String boundary, Map<String, Object> post_params, Map<String, File> files) {
        String paramTemplate = String.format("--%s\r\nContent-Disposition: form-data; name=\"%%s\"\r\nContent-Type: text/plain;\r\n\r\n%%s\r\n", boundary);
        String fileTemplate = String.format("--%s\r\nContent-Disposition: form-data; name=\"%%s\"; filename=\"%%s\"\r\nContent-Type: %%s\r\n\r\n", boundary);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        if (files != null) {
            files.forEach((k, v) -> {
                try {
                    byte[] fileByte = toByteArray(new FileInputStream(v));
                    String head = String.format(fileTemplate, k, v.getName(), MimeType.getByFilename(v.getName()));
                    outStream.writeBytes(head.getBytes());
                    outStream.writeBytes(fileByte);
                    outStream.writeBytes("\r\n".getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        if (post_params != null) {
            StringBuilder builder = new StringBuilder();
            post_params.forEach((k, v) -> builder.append(String.format(paramTemplate, k, v instanceof String ? v : convertObject(v))));
            outStream.writeBytes(builder.toString().getBytes());
        }


        outStream.writeBytes(String.format("--%s--\r\n", boundary).getBytes());
        try {
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(outStream.toByteArray());
    }

    private static String convertObject(Object object) {
        if (object instanceof String)
            return (String) object;
        return JsonConvert.root().convertTo(obj2Map(object));
    }

    private static String generateBaseParams(Map<String, Object> data) {
        StringBuilder builder = new StringBuilder();
        data.forEach((k, v) -> {
            builder.append(String.format("%s=%s&", k, URLEncoder.encode(JsonConvert.root().convertTo(v), StandardCharsets.UTF_8)));
        });
        return builder.length() > 0 ? builder.substring(0, builder.length() - 1) : builder.toString();
    }

    public static String generateUrlParams(String url, Object post_params) {
        if (post_params instanceof String) {
            return url.endsWith("?") ? url + post_params : String.format("%s?%s", url, post_params);
        }
        if (Utils.isEmpty(post_params) || post_params instanceof List)
            return url;


        if (!(post_params instanceof Map)) {
            post_params = obj2Map(post_params);
        }

        String postStr = ((Map<String, Object>) post_params).entrySet().stream()
                .map(m -> String.format("%s=%s", m.getKey(), URLEncoder.encode(convertObject(m.getValue()), StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&"));
        String template;
        if (url.indexOf("?") > 0) {
            template = "%s&%s";
        } else {
            template = "%s?%s";
        }
        return String.format(template, url, postStr);
    }

    public static byte[] toByteArray(final InputStream input) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        }
    }

    public static void saveImg(String urlStr, File local) throws Exception {
        URL url = new URL(urlStr);
        if (!local.getParentFile().exists()) local.getParentFile().mkdirs();

        try (DataInputStream dataInputStream = new DataInputStream(url.openStream());
             ByteArrayOutputStream output = new ByteArrayOutputStream();
             FileOutputStream fileOutputStream = new FileOutputStream(local)) {
            output.writeBytes(dataInputStream.readAllBytes());
            fileOutputStream.write(output.toByteArray());
        } catch (Exception e) {
            throw new Exception("not exists files");
        }
    }

    public static SSLContext getP12SSLContext(File file, String passWord) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            //            SSLContext sslContext = SSLContext.getInstance("TLS");

            SSLContext sslContext = SSLContext.getInstance("SSL");

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(inputStream, passWord.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);


            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            keyManagerFactory.init(keyStore, passWord.toCharArray());
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
            X509TrustManager tm = new SimpleX509TrustManager();

            sslContext.init(keyManagers, new TrustManager[]{tm}, null);
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public enum HttpMethod {
        POST_X, POST_FORM, GET, PUT, DELETE
    }
}

class SimpleX509TrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
