package demo.HttpServerDemo.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by jeffrey on 3/31/15.
 */
public class BadHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "Something went wrong";
        httpExchange.sendResponseHeaders(500, response.getBytes().length);
        httpExchange.getResponseBody().write(response.getBytes());
        httpExchange.getResponseBody().close();
    }
}
