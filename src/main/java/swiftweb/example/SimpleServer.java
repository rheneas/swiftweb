package swiftweb.example;

import static swiftweb.dsl.ServerDSL.*;

public class SimpleServer {

    public String sayHello() {
        return "hello";
    }

    public static void main(String[] args) {
        run(SimpleServer.class);
    }
}
