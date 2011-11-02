package swiftweb;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import swiftweb.dsl.Route;
import swiftweb.dsl.ServerDSL;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static swiftweb.HttpTestUtils.get;
import static swiftweb.dsl.ServerDSL.run;

public class UriParamsTest {
    private ServerDSL.DSL dsl;
    private HttpClient httpClient;

    public static class WildCardPathServer {
        @Route(path = "xml/{:id}", status = 200, contentType = "application/xml")
        public String xml(String id) {
            return id;
        }
    }

    @Before
    public void before() throws Exception {
        dsl = run(WildCardPathServer.class);
        httpClient = new DefaultHttpClient();
    }

    @After
    public void after() throws Exception {
        httpClient.getConnectionManager().shutdown();
        dsl.stop();
    }

    @Test
    public void shouldGetUriWithAParameter() throws IOException {
        assertEquals("23", get(httpClient, "http://localhost:8080/xml/23"));
    }
}
