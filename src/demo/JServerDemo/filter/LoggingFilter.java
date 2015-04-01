package demo.JServerDemo.filter;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Created by jeffrey on 3/31/15.
 */
public class LoggingFilter extends Filter {
    @Override
    public void doFilter(HttpExchange httpExchange, Chain chain) throws IOException {
        System.out.println("Logging request...");
        chain.doFilter(httpExchange);
        System.out.println("Request finished!");
    }

    @Override
    public String description() {
        return null;
    }
}
