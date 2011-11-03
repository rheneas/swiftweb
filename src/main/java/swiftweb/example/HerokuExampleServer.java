package swiftweb.example;

import swiftweb.dsl.Route;

import static swiftweb.dsl.ServerDSL.*;

public class HerokuExampleServer {

    @Route
    public String sayHello() {
        return "hello";
    }

    public static void main(String[] args) {
        runInHeroku(HerokuExampleServer.class);
    }
}
