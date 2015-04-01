package com.jeffrey.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;

/**
* Created by jeffrey on 3/7/15.
*/
public class Request {
    private InputStream is;
    private String method;
    private Headers h;
    private URI uri;
    private InetSocketAddress address;
    public Request(HttpExchange e){
        //System.out.println("Making request");
        is = e.getRequestBody();
        method = e.getRequestMethod();
        h = e.getRequestHeaders();
        uri = e.getRequestURI();
        address = e.getRemoteAddress();
        //System.out.println("Request made");
    }


    public InputStream getBody() {
        return is;
    }

    public String getMethod() {
        return method;
    }

    public Headers getHeaders() {
        return h;
    }

    public URI getURI() {
        return uri;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
