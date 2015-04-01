package demo.JServerDemo.handler;

import com.jeffrey.server.JHandler;
import com.jeffrey.server.Request;
import com.jeffrey.server.Response;

import java.io.IOException;

/**
 * Created by jeffrey on 3/31/15.
 */
public class GoodHandler implements JHandler {
    @Override
    public Response handle(Request r) {
        return new Response(200).pipe(r.getBody(), "Good!");
    }
}
