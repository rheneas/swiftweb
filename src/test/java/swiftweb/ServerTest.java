package swiftweb;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import swiftweb.dsl.HttpMethod;
import swiftweb.dsl.Route;

import java.io.IOException;

import static org.junit.Assert.*;
import static swiftweb.HttpTestUtils.*;


public class ServerTest extends AbstractServerTest {

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

    protected Class getServerClass() {
        return DummyServer.class;
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
        assertEquals("post", post(httpClient, "http://localhost:8080/postMe"));
    }

    @Test
    public void shouldConfigureFileNotFound() throws IOException {
        HttpResponse httpResponse = httpClient.execute(new HttpGet("http://localhost:8080/fileNotFound"));
        assertEquals("file not found", EntityUtils.toString(httpResponse.getEntity()));
        assertEquals(404, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldConfigurePlainJaneGet() throws IOException {
        get(httpClient, "http://localhost:8080/plainJaneGet");
    }
}
