package com.github.avarabyeu.restendpoint.serializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.google.common.net.MediaType;

/**
 * Byte array message converter. Actually, just placeholder to be able to work
 * with byte arrays through {@link Serializer} interface
 * 
 * @author Andrei Varabyeu
 * 
 */
public class ByteArraySerializer implements Serializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * Serializer#serialize(java.lang.Object)
	 */
	@Override
	public <T> byte[] serialize(T t) throws SerializerException {
		return (byte[]) t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Serializer#deserialize(byte[],
	 * java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException {
		if (byte[].class.equals(clazz)) {
			return (T) content;
		}
		throw new SerializerException("Unable to deserialize to type '" + clazz.getName() + "'");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Serializer#deserialize(byte[],
	 * java.lang.reflect.Type)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] content, Type type) throws SerializerException {
		if (byte[].class.equals(type)) {
			return (T) content;
		}
		throw new SerializerException("Unable to deserialize to type '" + type + "'");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Serializer#getMimeType()
	 */
	@Override
	public String getMimeType() {
		return MediaType.OCTET_STREAM.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Serializer#canRead(java.lang.String)
	 */
	@Override
	public boolean canRead(String mimeType) {
        return MediaType.ANY_TYPE.is(MediaType.parse(mimeType));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * Serializer#canWrite(java.lang.Object)
	 */
	@Override
	public boolean canWrite(Object o) {
		return byte[].class.equals(o.getClass());
	}

}