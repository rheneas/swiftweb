package swiftweb;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;
import swiftweb.dsl.Route;

import java.io.IOException;

import static org.junit.Assert.*;

public class ConfigurablePortTest extends AbstractServerTest {

    public static class DummyServer {
        public int port() {
            return 3000;
        }

        @Route
        public String get() {
            return "get";
        }
    }

    @Override
    protected Class getServerClass() {
        return DummyServer.class;
    }

    @Test
    public void configuredPortShouldBeUsed() throws IOException {
        HttpResponse httpResponse = httpClient.execute(new HttpGet("http://localhost:3000/get"));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }
}
