package com.labviros.is.msgs.camera;

import com.labviros.is.Message;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

/**
 * Created by picoreti on 08/04/17.
 */
public class CompressedImage extends Message {

    String format = "";
    byte[] data;

    public CompressedImage() {
    }

    public CompressedImage(Message message) throws IOException {
        super(message);
        this.unpack();
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void pack() throws IOException {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(2);
        if (format != null) {
            packer.packString(format);
        } else {
            packer.packString("");
        }

        if (data != null) {
            packer.packBinaryHeader(data.length);
            for (int i = 0; i < data.length; i++) {
                packer.packByte(data[i]);
            }
        } else {
            packer.packBinaryHeader(0);
        }

        packer.close();
        setBody(packer.toByteArray());
    }

    @Override
    public void unpack() throws IOException {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        if (unpacker.unpackArrayHeader() != 2) {
            throw new RuntimeException("Bad Length");
        }

        this.format = unpacker.unpackString();

        try {
            data = unpacker.unpackValue().asBinaryValue().asByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
