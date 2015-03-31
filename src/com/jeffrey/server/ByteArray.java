package com.jeffrey.server;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jeffrey on 3/7/15.
 */
public class ByteArray {
    private byte[] bytes;
    private int length = 512;
    private int read;

    public ByteArray(){
        bytes = new byte[length];
        read = 0;
    }

    public ByteArray(InputStream is) throws IOException {
        this();
        while(is.available() > 0){
            byte[] t = new byte[is.available()];
            is.read(t);
            this.add(t);
        }
    }


    public void add(byte[] bytes){
        while(bytes.length + read > length){
            length *= 2;
            byte[] newBytes = new byte[length];
            for(int i = 0; i < read; i++){
                newBytes[i] = this.bytes[i];
            }
            this.bytes = newBytes;
        }
        for(int i = 0; i < bytes.length; i++){
            this.bytes[read++] = bytes[i];
        }
    }

    public byte[] trim(){
        byte[] bytes = new byte[read];
        for(int i = 0; i < read; i++)
            bytes[i] = this.bytes[i];
        return bytes;
    }
}
