package com.labviros.is.msgs.common;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import com.labviros.is.Message;

public class Info extends Message {
	private String name;
	private String type;

		
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Info(){
		
	}
	
	public Info(Message copy) throws Exception{
		super(copy);
		this.unpack();
	}
	
	@Override
    public void pack() throws Exception {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(1);
        packer.packString(this.name);
        packer.packString(this.type);
        packer.close();
        setBody(packer.toByteArray());
    }

    @Override
    public void unpack() throws Exception {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(getBody());
        if (unpacker.unpackArrayHeader() != 1) {
            throw new RuntimeException("Bad Length");
        }
        this.name = unpacker.unpackString();
        this.type = unpacker.unpackString();        
    }
	
	

}
