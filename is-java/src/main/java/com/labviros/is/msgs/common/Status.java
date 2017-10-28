/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labviros.is.msgs.common;

import com.labviros.is.Message;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

/**
 *
 * @author clebeson
 */
public class Status extends Message {

    private String value;
    private String why;

    public Status() {
    }

    public Status(Message copy) throws Exception {
        super(copy);
        this.unpack();
    }

    @Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(2);
        packer.packString(value);
        packer.packString(why);
        packer.close();
        setBody(packer.toByteArray());
    }

    @Override
    public void unpack() throws Exception {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        if (unpacker.unpackArrayHeader() != 2) {
            throw new RuntimeException("Bad Length");
        }
        value = unpacker.unpackString();
        why = unpacker.unpackString();
    }

    public String getValue() {
        return value;
    }

    public String getWhy() {
        return why;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setWhy(String why) {
        this.why = why;
    }

}
