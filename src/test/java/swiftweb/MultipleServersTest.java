package swiftweb;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import swiftweb.dsl.Route;
import swiftweb.dsl.ServerDSL;

import java.io.IOException;

import static swiftweb.dsl.ServerDSL.*;
import static swiftweb.HttpTestUtils.*;

public class MultipleServersTest {

    private ServerDSL.DSL dsl;
    private DefaultHttpClient httpClient;

    public static class DummyServer1 {
        @Route
        public String get1() {
            return "get";
        }
    }

    public static class DummyServer2 {
        @Route
        public String get2() {
            return "get";
        }
    }

    @Before
    public void before() throws Exception {
        dsl = run(DummyServer1.class, DummyServer2.class);
        httpClient = new DefaultHttpClient();
    }

    @After
    public void after() throws Exception {
        httpClient.getConnectionManager().shutdown();
        dsl.stop();
    }

    @Test
    public void shouldResponseToGets() throws IOException {
        get(httpClient, "http://localhost:8080/get1");
        get(httpClient, "http://localhost:8080/get2");
    }
}
