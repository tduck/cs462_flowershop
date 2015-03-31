package demo.HttpServerDemo.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by jeffrey on 3/31/15.
 */
public class ElseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = format(httpExchange);
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        httpExchange.getResponseBody().write(response.getBytes());
        httpExchange.getResponseBody().close();
    }

    public String format(HttpExchange httpExchange){
        StringBuilder s = new StringBuilder();
        Scanner scanner = new Scanner(httpExchange.getRequestBody());
        scanner.useDelimiter("\\A");
        s.append("Method: ").append(httpExchange.getRequestMethod()).append("\n");
        if(scanner.hasNext())
            s.append("Body: ").append(scanner.next()).append("\n");
        s.append("Headers: ").append(httpExchange.getRequestHeaders()).append("\n");
        s.append("Remote address: ").append(httpExchange.getRemoteAddress()).append("\n");
        return s.toString();
    }
}
