package swiftweb;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import swiftweb.dsl.Route;
import swiftweb.dsl.ServerDSL;

import java.io.IOException;

import static swiftweb.HttpTestUtils.getSuccess;
import static swiftweb.dsl.ServerDSL.run;

public class WildCardPathTest {

    private ServerDSL.DSL dsl;
    private HttpClient httpClient;

    public static class WildCardPathServer {
        @Route(path = "xml/*", status = 200, contentType = "application/xml")
        public String xml() {
            return "<xml>some</xml>";
        }

        @Route(path = "more/*/tests", status = 200, contentType = "application/xml")
        public String test() {
            return "<xml>some</xml>";
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
    public void shouldGetWildCardUri() throws IOException {
        getSuccess("http://localhost:8080/xml/1", httpClient);
        getSuccess("http://localhost:8080/xml/2/3/4", httpClient);
        getSuccess("http://localhost:8080/xml/something/this/is/a/test", httpClient);
    }

}
