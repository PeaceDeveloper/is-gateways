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
public class RegionOfInterest extends Message {

    private int offsetX;  // Leftmost pixel of the ROI
    private int offsetY;  // Topmost pixel of the ROI
    private int height;    // Height of ROI
    private int width; // Width of ROI

    public RegionOfInterest() {
    }

    public RegionOfInterest(Message copy) throws Exception {
        super(copy);
        this.unpack();
    }

    public RegionOfInterest(int offsetX, int offsetY, int height, int width) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.height = height;
        this.width = width;
    }

    @Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(4);
        packer.packInt(offsetX);
        packer.packInt(offsetY);
        packer.packInt(height);
        packer.packInt(width);
        packer.close();
        setBody(packer.toByteArray());
    }

    @Override
    public void unpack() throws Exception {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        if (unpacker.unpackArrayHeader() != 4) {
            throw new RuntimeException("Bad Length");
        }
        offsetX = unpacker.unpackInt();
        offsetY = unpacker.unpackInt();
        height = unpacker.unpackInt();
        width = unpacker.unpackInt();
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
