package demo.JServerDemo.handler;

import com.jeffrey.server.JHandler;
import com.jeffrey.server.Request;
import com.jeffrey.server.Response;

/**
 * Created by jeffrey on 3/31/15.
 */
public class BadHandler implements JHandler {
    @Override
    public Response handle(Request r) {
        return new Response(500, "Something went wrong");
    }
}
