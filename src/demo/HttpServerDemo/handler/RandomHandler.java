package demo.HttpServerDemo.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Random;

/**
 * Created by jeffrey on 3/31/15.
 */
public class RandomHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Random r = new Random();
        if(r.nextDouble() < .5){
            new GoodHandler().handle(httpExchange);
        } else{
            new BadHandler().handle(httpExchange);
        }
    }
}
