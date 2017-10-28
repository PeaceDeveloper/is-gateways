/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labviros.is.msgs.robot;

import com.labviros.is.Message;
import com.labviros.is.msgs.geometry.Point;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

/**
 *
 * @author clebeson
 */
public class Pose extends Message {

    private Point position;  // [mm]
    private double heading;

    public Pose() {
    }

    public Pose(Message copy) throws Exception {
        super(copy);
        this.unpack();
    }

    public Pose(Point position, double heading) {
        this.position = position;
        this.heading = heading;
    }

    @Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        if (position == null) {
            packer.packArrayHeader(1);
            packer.packDouble(heading);
            packer.close();
            setBody(packer.toByteArray());
            return;
        }

        packer.packArrayHeader(2);
        position.pack();
        packer.addPayload(position.getBody());
        packer.packDouble(heading);
        packer.close();
        setBody(packer.toByteArray());

    }

    @Override
    public void unpack() throws Exception {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        int length = unpacker.unpackArrayHeader();
        if (length != 2) {
            throw new RuntimeException("Bad Length");
        }

        Value value = unpacker.unpackValue().asArrayValue();

        ArrayValue arrayValue = value.asArrayValue();
        position = new Point(arrayValue.get(0).isNilValue() ? 0
                : (arrayValue.get(0).asNumberValue().isFloatValue() ? arrayValue.get(0).asNumberValue().toFloat() : arrayValue.get(0).asNumberValue().toDouble()),
                arrayValue.get(1).isNilValue() ? 0
                        : (arrayValue.get(1).asNumberValue().isFloatValue() ? arrayValue.get(1).asNumberValue().toFloat() : arrayValue.get(1).asNumberValue().toDouble()),
                arrayValue.get(2).isNilValue() ? 0
                        : (arrayValue.get(2).asNumberValue().isFloatValue() ? arrayValue.get(2).asNumberValue().toFloat() : arrayValue.get(2).asNumberValue().toDouble()));

        heading = unpacker.unpackDouble();

    }

    public Point getPosition() {
        return position;
    }

    public double getHeading() {
        return heading;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

}
