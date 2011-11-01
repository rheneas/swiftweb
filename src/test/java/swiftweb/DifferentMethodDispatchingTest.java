package swiftweb;

import swiftweb.dsl.Route;
import swiftweb.dsl.ServerDSL;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static swiftweb.dsl.ServerDSL.run;
import static org.junit.Assert.assertEquals;

public class DifferentMethodDispatchingTest {
    private ServerDSL.DSL dsl;
    private HttpClient httpClient;

    public static class DifferentMethodDispatchingServer {
        @Route
        public String noArgs() {
            return "noArgs";
        }

        @Route
        public String requestAndResponseArgsReturningString(HttpServletRequest request, HttpServletResponse response) {
            return "string";
        }

        @Route
        public void requestAndResponseReturningNothing(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.getWriter().write("nothing returned");
        }

        @Route
        public String requestArgReturningString(HttpServletRequest request) {
            return "string";
        }

        @Route
        public void responseArgReturningNothing(HttpServletResponse response) throws IOException {
            response.getWriter().write("nothing returned");
        }
    }

    @Before
    public void before() throws Exception {
        dsl = run(DifferentMethodDispatchingServer.class);
        httpClient = new DefaultHttpClient();
    }

    @After
    public void after() throws Exception {
        httpClient.getConnectionManager().shutdown();
        dsl.stop();
    }

    @Test
    public void requestToNoArgsShouldSucceed() throws IOException {
        urlShouldReturnAsExpected("noArgs", "noArgs");
    }

    @Test
    public void requestToRequestAndResponseArgsShouldSucceed() throws IOException {
        urlShouldReturnAsExpected("requestAndResponseArgsReturningString", "string");
    }

    @Test
    public void requestAndResponseReturningNothingShouldSucceed() throws IOException {
        urlShouldReturnAsExpected("requestAndResponseReturningNothing", "nothing returned");
    }

    @Test
    public void requestArgReturningString() throws IOException {
        urlShouldReturnAsExpected("requestArgReturningString", "string");
    }

    @Test
    public void responseArgReturningNothingShouldSucceed() throws IOException {
        urlShouldReturnAsExpected("responseArgReturningNothing", "nothing returned");
    }

    private void urlShouldReturnAsExpected(String path, String response) throws IOException {
        HttpResponse httpResponse = httpClient.execute(new HttpGet("http://localhost:8080/" + path));
        assertEquals(response, EntityUtils.toString(httpResponse.getEntity()));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }
}
