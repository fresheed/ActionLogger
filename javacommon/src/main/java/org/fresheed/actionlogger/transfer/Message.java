package org.fresheed.actionlogger.transfer;

/**
 * Created by fresheed on 07.02.17.
 */

public class Message {
    public final String name;
    public final byte[] payload;

    public Message(String name, byte[] payload){
        this.name=name;
        this.payload=payload;
    }

    public Message(String name){
        this(name, new byte[0]);
    }
}
