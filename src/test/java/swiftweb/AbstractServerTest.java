package swiftweb;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import swiftweb.dsl.ServerDSL;

import static swiftweb.dsl.ServerDSL.*;

public abstract class AbstractServerTest {

    protected ServerDSL.DSL dsl;
    protected DefaultHttpClient httpClient;

    @Before
    public void before() throws Exception {
        dsl = run(getServerClass());
        httpClient = new DefaultHttpClient();
    }

    @After
    public void after() throws Exception {
        httpClient.getConnectionManager().shutdown();
        dsl.stop();
    }

    protected abstract Class getServerClass();
}
