package demo.HttpServerDemo.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import demo.HttpServerDemo.filter.MethodFilter;
import demo.HttpServerDemo.handler.BadHandler;
import demo.HttpServerDemo.handler.ElseHandler;
import demo.HttpServerDemo.handler.GoodHandler;
import demo.HttpServerDemo.filter.LoggingFilter;
import demo.HttpServerDemo.handler.RandomHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by jeffrey on 3/31/15.
 */
public class Server {
    public static void main(String[] args){
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/good", new GoodHandler());
            server.createContext("/bad", new BadHandler());
            server.createContext("/random", new RandomHandler());
            HttpContext context = server.createContext("/", new ElseHandler());
            context.getFilters().add(new MethodFilter("GET", "POST", "asdf"));
            context.getFilters().add(new LoggingFilter());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
