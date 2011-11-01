package swiftweb;


import swiftweb.dsl.Route;
import swiftweb.dsl.ServerDSL;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static swiftweb.dsl.ServerDSL.run;
import static org.junit.Assert.assertEquals;

public class ConfigurablePortTest {

    private ServerDSL.DSL dsl;
    private HttpClient httpClient;

    public static class DummyServer {
        public int port() {
            return 3000;
        }

        @Route
        public String get() {
            return "get";
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
    public void configuredPortShouldBeUsed() throws IOException {
        HttpResponse httpResponse = httpClient.execute(new HttpGet("http://localhost:3000/get"));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

}
