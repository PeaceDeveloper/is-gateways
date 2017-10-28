/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labviros.is.msgs.camera;

import com.labviros.is.Message;
import java.io.IOException;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

/**
 *
 * @author clebeson
 */
public class TheoraPacket extends Message {

    private boolean newHeader = false;
    private byte[] data;

    public TheoraPacket(Message copy) throws Exception {
        super(copy);
        this.unpack();
    }

    public TheoraPacket() {
    }

    public TheoraPacket(byte[] data) {
        this.data = data;
    }

    public TheoraPacket(byte[] data, boolean newHeader) {
        this.newHeader = newHeader;
        this.data = data;
    }

    public boolean isNewHeader() {
        return newHeader;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();

        if (data == null || data.length == 0) {
            packer.packArrayHeader(1);
            packer.packBoolean(newHeader);
            packer.close();
            setBody(packer.toByteArray());

            return;
        }

        packer.packArrayHeader(2);
        packer.packBoolean(newHeader);

        packer.packBinaryHeader(data.length);
        for (int i = 0; i < data.length; i++) {
            packer.packByte(data[i]);
        }

        packer.close();
        setBody(packer.toByteArray());
    }

    @Override
    public void unpack() throws Exception {

        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        if (unpacker.unpackArrayHeader() != 2) {
            throw new RuntimeException("Bad Length");
        }

        this.newHeader = unpacker.unpackBoolean();
        try {
            data = unpacker.unpackValue().asBinaryValue().asByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNewHeader(boolean newHeader) {
        this.newHeader = newHeader;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
