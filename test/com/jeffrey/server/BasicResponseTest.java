package com.jeffrey.server;

import com.jeffrey.server.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created by jeffrey on 3/7/15.
 */
public class BasicResponseTest {

    @Test
    public void codeCompiles(){
        try {
            JServer s = new JServer(9000);
            s.register("/asdf", new JHandler() {
                @Override
                public Response handle(Request r) {
                    if(r.getMethod().equals("GET")) {
                        Scanner scanner = new Scanner(r.getBody());
                        scanner.useDelimiter("\\A");
                        if(!scanner.hasNext())
                            return new Response(200);
                        else
                            return new Response(200, scanner.next());
                    }
                    if(r.getMethod().equals("POST")){
                        try {
                            return new Response(500).pipe(r.getBody());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return new Response(405);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
            return;
        }
    }

    @Test
    public void byteArrayTest() throws IOException {
        ByteArray ba = new ByteArray();
        byte[] bytes = new byte[32];
        ByteArrayInputStream bais = new ByteArrayInputStream("I'm a dorky dork dork. I like to".getBytes());
        bais.read(bytes);
        ba.add(bytes);
        byte[] checkpoint = ba.trim();
        Assert.assertArrayEquals(checkpoint, bytes);
        Assert.assertEquals(new String(checkpoint), "I'm a dorky dork dork. I like to");
        ba.add(" program!".getBytes());
        Assert.assertEquals(new String(ba.trim()), "I'm a dorky dork dork. I like to program!");
        Assert.assertEquals(ba.trim().length, 32 + 9);
    }

    @Test
    public void serverBasicsWorkTest(){
        JServer s;
        try {
            s = new JServer(8080);
            s.start();
            s.register("/asdf", new JHandler() {
                @Override
                public Response handle(Request r) {
                    if(r.getMethod().equals("POST")){
                        try {
                            return new Response(200).pipe(r.getBody());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return new Response(405);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try{
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/asdf").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            String string = "This is a test of the echo server.";

            connection.getOutputStream().write(string.getBytes());
            connection.getOutputStream().close();
            InputStream is = connection.getInputStream();
            ByteArray ba = new ByteArray();
            while(is.available() > 0){
                byte[] t = new byte[is.available()];
                is.read(t);
                ba.add(t);
            }
            Assert.assertEquals(string, new String(ba.trim()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(s != null)
            s.stop();
    }

    /*@Test
    public void callSelfTest(){
        try {
            JServer server = new JServer(7000, 3);

            server.register("/a", new JHandler() {
                @Override
                public Response handle(Request r) {
                    try{
                        System.out.println("a: Calling self");
                        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:7000/b").openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoInput(true);
                        connection.setDoOutput(true);

                        InputStream is = r.getBody();
                        ByteArray ba = new ByteArray();
                        while(is.available() > 0){
                            byte[] t = new byte[is.available()];
                            is.read(t);
                            ba.add(t);
                        }
                        System.out.println("a: Sending through!");

                        connection.getOutputStream().write(ba.trim());
                        connection.getOutputStream().close();

                        System.out.println("a: Building response");
                        is = connection.getInputStream();
                        ba = new ByteArray();
                        while(is.available() > 0){
                            System.out.println("doing thing.");
                            byte[] t = new byte[is.available()];
                            is.read(t);
                            ba.add(t);
                        }
                        System.out.println("a: Sending response!");
                        return new Response(200, ba.trim());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            });
            server.register("/b", new JHandler() {
                @Override
                public Response handle(Request r) {
                    try {
                        System.out.println("b: Responding to request");
                        return new Response(200).pipe(r.getBody());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });
            server.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:7000/a").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            String string = "This is a test of the echo self-call server.";

            connection.getOutputStream().write(string.getBytes());
            connection.getOutputStream().close();
            InputStream is = connection.getInputStream();
            ByteArray ba = new ByteArray();
            while(is.available() > 0){
                byte[] t = new byte[is.available()];
                is.read(t);
                ba.add(t);
            }
            Assert.assertEquals(string, new String(ba.trim()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}