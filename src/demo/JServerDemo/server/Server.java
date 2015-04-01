package demo.JServerDemo.server;

import com.jeffrey.server.JServer;
import com.sun.net.httpserver.HttpContext;
import demo.HttpServerDemo.filter.LoggingFilter;
import demo.HttpServerDemo.filter.MethodFilter;
import demo.JServerDemo.handler.BadHandler;
import demo.JServerDemo.handler.ElseHandler;
import demo.JServerDemo.handler.GoodHandler;
import demo.JServerDemo.handler.RandomHandler;

import java.io.IOException;

/**
 * Created by jeffrey on 3/31/15.
 */
public class Server {
    public static void main(String[] args) {
        try {
            JServer server = new JServer(8081);
            server.register("/good", new GoodHandler());
            server.register("/bad", new BadHandler());
            server.register("/random", new RandomHandler());
            HttpContext context = server.register("/", new ElseHandler());
            context.getFilters().add(new MethodFilter("GET", "POST", "asdf"));
            context.getFilters().add(new LoggingFilter());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
