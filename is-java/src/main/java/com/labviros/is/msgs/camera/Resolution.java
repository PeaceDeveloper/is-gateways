/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labviros.is.msgs.camera;

import com.labviros.is.Message;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

/**
 *
 * @author clebeson
 */
public class Resolution extends Message {

    private int height;
    private int width;

    public Resolution() {
    }

    public Resolution(Message copy) throws Exception {
        super(copy);
        this.unpack();
    }

    public Resolution(int height, int width) {
        this.height = height;
        this.width = width;
    }

    @Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(2);
        packer.packInt(height);
        packer.packInt(width);
        packer.close();
        setBody(packer.toByteArray());
    }

    @Override
    public void unpack() throws Exception {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        if (unpacker.unpackArrayHeader() != 2) {
            throw new RuntimeException("Bad Length");
        }
        height = unpacker.unpackInt();
        width = unpacker.unpackInt();

    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
