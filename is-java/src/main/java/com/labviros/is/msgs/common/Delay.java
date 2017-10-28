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
public class Delay extends Message {

    private int milliseconds;

    public Delay() {
    }

    public Delay(Message copy) throws Exception {
        super(copy);
        this.unpack();
    }

    public Delay(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(1);
        packer.packInt(milliseconds);
        packer.close();
        setBody(packer.toByteArray());
    }

    @Override
    public void unpack() throws Exception {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        if (unpacker.unpackArrayHeader() != 1) {
            throw new RuntimeException("Bad Length");
        }
        milliseconds = unpacker.unpackInt();

    }

    public int getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(int milliseconds) {
        this.milliseconds = milliseconds;
    }

}
