package swiftweb;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import swiftweb.dsl.Route;
import swiftweb.dsl.ServerDSL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.*;
import static swiftweb.HttpTestUtils.*;
import static swiftweb.dsl.ServerDSL.*;

public class DifferentMethodDispatchingTest extends AbstractServerTest {

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

    @Override
    protected Class getServerClass() {
        return DifferentMethodDispatchingServer.class;
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
        assertEquals(response, get(httpClient, "http://localhost:8080/" + path));
    }
}
