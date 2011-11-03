package swiftweb;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.junit.Test;
import swiftweb.dsl.Route;
import swiftweb.dsl.Security;

import java.io.IOException;

import static junit.framework.Assert.*;

public class BasicAuthServerTest extends AbstractServerTest {

    @Security(user = "user", password = "password")
    public static class DummyServer {
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
    public void shouldFailWhenCredentialsAreAbsent() throws IOException {
        HttpResponse httpResponse = httpClient.execute(new HttpGet("http://localhost:8080/get"));
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        assertEquals(401, statusCode);
    }

    @Test
    public void shouldFailWhenPasswordIsIncorrect() throws IOException {
        get("user", "password1", 401);
    }

    @Test
    public void shouldFailWhenUserIsIncorrect() throws IOException {
        get("user1", "password", 401);
    }

    @Test
    public void shouldRespondWhenCredentialsAreCorrect() throws IOException {
        get("user", "password", 200);
    }

    private void get(String user, String password, int expectedStatusCode) throws IOException {
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
        httpClient.setCredentialsProvider(basicCredentialsProvider);
        HttpResponse httpResponse = httpClient.execute(new HttpGet("http://localhost:8080/get"));
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        assertEquals(expectedStatusCode, statusCode);
    }
}
