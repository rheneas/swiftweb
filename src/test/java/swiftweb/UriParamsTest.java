package swiftweb;

import org.junit.Ignore;
import org.junit.Test;
import swiftweb.dsl.Route;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.*;
import static swiftweb.HttpTestUtils.*;

public class UriParamsTest extends AbstractServerTest {

    public static class UriParamsServer {
        @Route(path = "similar/{:id}/hello")
        public String similarWithHello(String id) {
            return "with hello";
        }

        @Route(path = "similar/{:id}")
        public String similar(String id) {
            return "without hello";
        }


        @Route(path = "xml/{:id}")
        public String xml(String id) {
            return id;
        }

        @Route(path = "/groups/{:id}/students/{:moreids}")
        public String groupsAndStudents(String id, String moreIds) {
            return id + moreIds;
        }

        @Route(path = "/request/{:id}")
        public String request(String id, HttpServletRequest request) {
            return id;
        }

        @Route(path = "/response/{:id}")
        public void response(String id, HttpServletResponse response) throws IOException {
            response.getWriter().write(id);
        }

        @Route(path = "/both/{:id}")
        public String bothRequestResponse(String id, HttpServletRequest request, HttpServletResponse response) {
            return id;
        }
    }

    @Override
    protected Class getServerClass() {
        return UriParamsServer.class;
    }

    @Test
    public void shouldGetUriWithParameters() throws IOException {
        assertEquals("23", get(httpClient, "http://localhost:8080/xml/23"));
        assertEquals("23100", get(httpClient, "http://localhost:8080/groups/23/students/100"));
        assertEquals("23", get(httpClient, "http://localhost:8080/request/23"));
        assertEquals("somestring", get(httpClient, "http://localhost:8080/both/somestring"));
        assertEquals("someresponse", get(httpClient, "http://localhost:8080/response/someresponse"));
    }

    @Test
    @Ignore
    public void shouldGetWithSimilarUris() throws IOException {
        assertEquals("without hello", get(httpClient, "http://localhost:8080/similar/11"));
        assertEquals("with hello", get(httpClient, "http://localhost:8080/similar/11/hello"));
    }
}
