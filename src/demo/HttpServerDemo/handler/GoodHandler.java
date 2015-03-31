package demo.HttpServerDemo.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by jeffrey on 3/31/15.
 */
public class GoodHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Scanner scanner = new Scanner(httpExchange.getRequestBody());
        scanner.useDelimiter("\\A");
        String body = scanner.hasNext() ? scanner.next() : "Good!";
        httpExchange.sendResponseHeaders(200, body.getBytes().length);
        httpExchange.getResponseBody().write(body.getBytes());
        httpExchange.getResponseBody().close();
    }
}
