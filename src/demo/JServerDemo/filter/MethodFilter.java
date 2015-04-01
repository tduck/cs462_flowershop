package demo.JServerDemo.filter;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Created by jeffrey on 3/31/15.
 */
public class MethodFilter extends Filter {
    String[] methods;
    public MethodFilter(String... methods){
        this.methods = methods;
    }

    public boolean contains(String[] arr, String str){
        for(String s: arr){
            if(s.equals(str))
                return true;
        }
        return false;
    }

    @Override
    public void doFilter(HttpExchange httpExchange, Chain chain) throws IOException {
        if(contains(methods, httpExchange.getRequestMethod())){
            chain.doFilter(httpExchange);
        } else{
            String response = "Method not allowed";
            httpExchange.sendResponseHeaders(405, response.getBytes().length);
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.getResponseBody().close();
        }
    }

    @Override
    public String description() {
        return null;
    }
}
