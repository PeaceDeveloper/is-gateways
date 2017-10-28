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
public class ImageType extends Message {

    private String rgb = "rgb";
    private String gray = "gray";

    public ImageType(Message copy) throws Exception {
        super(copy);
        this.unpack();
    }

    public ImageType() {
    }

    @Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(2);
        packer.packString(rgb);
        packer.packString(gray);
        packer.close();
        setBody(packer.toByteArray());
    }

    @Override
    public void unpack() throws Exception {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        if (unpacker.unpackArrayHeader() != 2) {
            throw new RuntimeException("Bad Length");
        }
        rgb = unpacker.unpackString();
        gray = unpacker.unpackString();
    }

    public String getRgb() {
        return rgb;
    }

    public String getGray() {
        return gray;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    public void setGray(String gray) {
        this.gray = gray;
    }

}
