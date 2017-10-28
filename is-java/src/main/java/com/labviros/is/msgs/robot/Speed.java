/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labviros.is.msgs.robot;

import com.labviros.is.Message;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

/**
 *
 * @author clebeson
 */
public class Speed extends Message {

    private double linear;
    private double angular;

    public Speed(Message copy) throws Exception {
        super(copy);
        this.unpack();
    }

    public Speed() {
    }

    public Speed(double linear, double angular) {
        this.linear = linear;
        this.angular = angular;
    }

    @Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(2);
        packer.packDouble(linear);
        packer.packDouble(angular);
        packer.close();
        setBody(packer.toByteArray());
    }

    @Override
    public void unpack() throws Exception {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        if (unpacker.unpackArrayHeader() != 2) {
            throw new RuntimeException("Bad Length");
        }
        linear = unpacker.unpackDouble();
        angular = unpacker.unpackDouble();

    }

    public double getLinear() {
        return linear;
    }

    public double getAngular() {
        return angular;
    }

    public void setLinear(double linear) {
        this.linear = linear;
    }

    public void setAngular(double angular) {
        this.angular = angular;
    }

}
