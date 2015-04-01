package com.jeffrey.server;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
* Created by jeffrey on 3/7/15.
*/
public class Response {
    int status;
    byte[] response;


    public Response(int i){
        status = i;
        response = null;
    }

    public Response(int i, String s) {
        status = i;
        response = s.getBytes();
    }

    public Response(int i, byte[] bytes){
        status = i;
        response = bytes;
    }

    public Response send(Object o){
        response = new Gson().toJson(o).getBytes();
        return this;
    }


    public Response pipe(InputStream is){
        return this.pipe(is, "");
    }

    public Response pipe(InputStream is, String s){
        ByteArray ba = null;
        try {
            ba = new ByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
            ba = new ByteArray();
        }

        response = ba.trim();
        if(response.length == 0)
            response = s.getBytes();
        return this;
    }


    public int getStatus() {
        return status;
    }

    public long getSize() {
        if(response == null){
            response = "No response".getBytes();
        }
        long length = response.length;
        return length;
    }

    public byte[] getBody() {
        return response;
    }
}
