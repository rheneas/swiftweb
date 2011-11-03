package swiftweb.example;

import swiftweb.dsl.HttpMethod;
import swiftweb.dsl.Route;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static swiftweb.dsl.ServerDSL.*;

public class ExampleServer {

    @Route
    public String helloWorld() {
        return "helloWorld";
    }

    @Route(path = "nothingToSeeHere", status = 404)
    public String nothingToSeeHere() {
        return "nothing";
    }

    @Route(path = "post", method = HttpMethod.POST, contentType = "application/xml")
    public String post() {
        return "post";
    }

    @Route(path="requestAndResponse")
    public String passedInARequestAndReponse(HttpServletRequest request, HttpServletResponse response) {
        return request.getRequestURI();
    }

    @Route(path = "response")
    public void passedInAResponse(HttpServletResponse response) throws IOException {
        response.setHeader("header", "me me me");
        response.getWriter().write("I'm handling the response myself");
    }

    @Route(path="/number/{:id}")
    public String number(String id) {
        return id;
    }

    @Route(path="/more/number/{:id}/and/{:stuff}")
    public String number(String id, String stuff, HttpServletRequest request, HttpServletResponse response) {
        return "id=" + id + ",stuff=" + stuff;
    }

    public static void main(String[] args) {
        run(ExampleServer.class);
    }
}
