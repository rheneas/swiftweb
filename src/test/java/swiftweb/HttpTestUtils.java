package swiftweb;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpTestUtils {

    public static String get(HttpClient httpClient, String uri) throws IOException {
        HttpResponse httpResponse = httpClient.execute(new HttpGet(uri));
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new RuntimeException("Status code unexpected, expected 200, got " + statusCode);
        }

        return EntityUtils.toString(httpResponse.getEntity());
    }

    public static String post(HttpClient httpClient, String uri) throws IOException {
        HttpResponse httpResponse = httpClient.execute(new HttpPost(uri));
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new RuntimeException("Status code unexpected, expected 200, got " + statusCode);
        }

        return EntityUtils.toString(httpResponse.getEntity());
    }
}
