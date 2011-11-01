package swiftweb;

import swiftweb.dsl.HttpMethod;
import swiftweb.dsl.Route;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static swiftweb.dsl.ServerDSL.run;

public class ExampleServer {

    public int port() {
        return 3000;
    }

    @Route
    public String helloWorld() {
        return "<xml>some crappy xml structure</xml>";
    }

    @Route(path = "hello", status = 404, contentType = "application/xml")
    public String anotherHelloWorld() {
        return "not found";
    }

    @Route(path = "postMe", method = HttpMethod.POST, contentType = "application/xml")
    public String post() {
        return "post";
    }

    @Route(path = "handleResponse")
    public void handleResponse(HttpServletResponse response) throws IOException {
        response.setHeader("header", "me me me");
        response.getWriter().write("I'm handling the response myself");
    }

    public static void main(String[] args) throws Exception {
        run(ExampleServer.class);
    }
}
