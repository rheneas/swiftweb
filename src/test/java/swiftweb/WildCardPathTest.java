package swiftweb;

import org.junit.Test;
import swiftweb.dsl.Route;

import java.io.IOException;

import static swiftweb.HttpTestUtils.*;

public class WildCardPathTest extends AbstractServerTest {

    public static class WildCardPathServer {
        @Route(path = "xml/*", status = 200, contentType = "application/xml")
        public String xml() {
            return "<xml>some</xml>";
        }
    }

    @Override
    protected Class getServerClass() {
        return WildCardPathServer.class;
    }

    @Test
    public void shouldGetWildCardUri() throws IOException {
        get(httpClient, "http://localhost:8080/xml/1");
        get(httpClient, "http://localhost:8080/xml/2/3/4");
        get(httpClient, "http://localhost:8080/xml/something/this/is/a/test");
    }
}
