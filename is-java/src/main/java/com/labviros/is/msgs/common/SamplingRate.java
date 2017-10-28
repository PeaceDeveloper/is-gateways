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
public class SamplingRate extends Message {

    private double rate; // [Hz]
    private int period; // [ms]

    public SamplingRate() {
    }

    public SamplingRate(Message copy) throws Exception {
        super(copy);
        this.unpack();
    }

    public SamplingRate(double rate, int period) {
        this.rate = rate;
        this.period = period;
    }

    @Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(2);
        packer.packDouble(rate);
        packer.packInt(period);
        packer.close();
        setBody(packer.toByteArray());
    }

    @Override
    public void unpack() throws Exception {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        if (unpacker.unpackArrayHeader() != 2) {
            throw new RuntimeException("Bad Length");
        }
        rate = unpacker.unpackDouble();
        period = unpacker.unpackInt();
    }

    public int getPeriod() {
        return period;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

}
