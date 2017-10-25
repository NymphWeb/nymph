package com.nymph.link.codec;

import com.nymph.link.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
/**
 * 编码器 (对象-->字节)
 * @author Nymph
 *
 */
public class Encoder extends MessageToByteEncoder<Object> {
	 private Class<?> genericClass;

	    public Encoder(Class<?> genericClass) {
	        this.genericClass = genericClass;
	    }
	    @Override
	    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
	        if (genericClass.isInstance(in)) {
	            byte[] data = SerializationUtil.serialize(in);
	            out.writeInt(data.length);
	            out.writeBytes(data);
	        }
	    }
}
