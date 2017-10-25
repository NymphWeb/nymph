package com.nymph.link.codec;

import java.util.List;

import com.nymph.link.utils.SerializationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 解码器
 * @author Nymph
 *
 */
public class Decoder extends ByteToMessageDecoder {

	private Class<?> genericClass;

	public Decoder(){}

	public Decoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}
	public void setGenericClass(Class<?> genericClass) {
		this.genericClass = genericClass;
	}
	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if(in.readableBytes()<4){
			return ;
		}
		in.markReaderIndex();
		int length = in.readInt();
		if(in.readableBytes() < length) {
			in.resetReaderIndex();
			return;
		}
		byte[] data = new byte[length];
		in.readBytes(data);
		out.add(SerializationUtil.deserialize(data, genericClass));
	}

}
