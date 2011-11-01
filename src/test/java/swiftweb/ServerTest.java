package swiftweb;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import swiftweb.dsl.HttpMethod;
import swiftweb.dsl.Route;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static swiftweb.dsl.ServerDSL.DSL;
import static swiftweb.dsl.ServerDSL.run;


public class ServerTest {

    private DSL dsl;
    private HttpClient httpClient;

    public static class DummyServer {
        @Route(path = "xml", status = 200, contentType = "application/xml")
        public String xml() {
            return "<xml>some</xml>";
        }

        @Route(path = "postMe", method = HttpMethod.POST)
        public String post() {
            return "post";
        }

        @Route(status = 404)
        public String fileNotFound() {
            return "file not found";
        }

        @Route
        public String plainJaneGet() {
            return "plainJaneGet";
        }
    }

    @Before
    public void before() throws Exception {
        dsl = run(DummyServer.class);
        httpClient = new DefaultHttpClient();
    }

    @After
    public void after() throws Exception {
        httpClient.getConnectionManager().shutdown();
        dsl.stop();
    }

    @Test
    public void shouldConfigureXmlGet() throws IOException {
        HttpResponse httpResponse = httpClient.execute(new HttpGet("http://localhost:8080/xml"));
        assertEquals("<xml>some</xml>", EntityUtils.toString(httpResponse.getEntity()));
        assertTrue(httpResponse.getEntity().getContentType().toString().contains("application/xml"));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldConfigurePostMethod() throws IOException {
        HttpResponse httpResponse = httpClient.execute(new HttpPost("http://localhost:8080/postMe"));
        assertEquals("post", EntityUtils.toString(httpResponse.getEntity()));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldConfigureFileNotFound() throws IOException {
        HttpResponse httpResponse = httpClient.execute(new HttpGet("http://localhost:8080/fileNotFound"));
        assertEquals("file not found", EntityUtils.toString(httpResponse.getEntity()));
        assertEquals(404, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldConfigurePlainJaneGet() throws IOException {
        HttpResponse httpResponse = httpClient.execute(new HttpGet("http://localhost:8080/plainJaneGet"));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }
}
