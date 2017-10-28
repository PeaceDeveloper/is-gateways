/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labviros.is.msgs.geometry;

import com.labviros.is.Message;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

/**
 *
 * @author clebeson
 */
public class Point extends Message {

    private double x;
    private double y;
    private double z = 0;

    public Point() {
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(Message msg) throws Exception {
        super(msg);
        this.unpack();
    }

    @Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(3);
        packer.packDouble(x);
        packer.packDouble(y);
        packer.packDouble(z);
        packer.close();
        setBody(packer.toByteArray());
    }

    @Override
    public void unpack() throws Exception {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        int length = unpacker.unpackArrayHeader();
        switch (length) {
            case 2:
                x = unpacker.unpackDouble();
                y = unpacker.unpackDouble();
                break;
            case 3:
                x = unpacker.unpackDouble();
                y = unpacker.unpackDouble();
                z = unpacker.unpackDouble();
                break;
            default:
                throw new RuntimeException("Bad Length");
        }

    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

}
