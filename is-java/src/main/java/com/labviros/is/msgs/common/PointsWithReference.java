/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labviros.is.msgs.common;

import com.labviros.is.Message;
import com.labviros.is.msgs.geometry.Point;
import java.util.ArrayList;
import java.util.List;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

/**
 *
 * @author clebeson
 */
public class PointsWithReference extends Message {

    private String reference = "";
    private List<Point> points;

    public PointsWithReference() {
    }

    public PointsWithReference(Message copy) throws Exception {
        super(copy);
        this.unpack();
    }

    public PointsWithReference(String reference, List<Point> points) {
        this.reference = reference;
        this.points = points;
    }

    @Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        if (points == null || points.isEmpty()) {
            packer.packArrayHeader(1);
            packer.packString(reference);
            packer.close();
            setBody(packer.toByteArray());
            return;
        }

        packer.packArrayHeader(2);
        packer.packString(reference);

        packer.packArrayHeader(points.size());
        for (Point point : points) {
            point.pack();
            packer.addPayload(point.getBody());
        }

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
        points = new ArrayList<>();
        reference = unpacker.unpackString();

        Value value = unpacker.unpackValue();
        List<Value> values = value.asArrayValue().list();

        for (Value v : values) {

            ArrayValue arrayValue = v.asArrayValue();
            Point p = new Point(arrayValue.get(0).isNilValue() ? 0
                    : (arrayValue.get(0).asNumberValue().isFloatValue() ? arrayValue.get(0).asNumberValue().toFloat() : arrayValue.get(0).asNumberValue().toDouble()),
                    arrayValue.get(1).isNilValue() ? 0
                            : (arrayValue.get(1).asNumberValue().isFloatValue() ? arrayValue.get(1).asNumberValue().toFloat() : arrayValue.get(1).asNumberValue().toDouble()),
                    arrayValue.get(2).isNilValue() ? 0
                            : (arrayValue.get(2).asNumberValue().isFloatValue() ? arrayValue.get(2).asNumberValue().toFloat() : arrayValue.get(2).asNumberValue().toDouble()));

            points.add(p);
        }

    }

    public List<Point> getPoints() {
        return points;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

}
