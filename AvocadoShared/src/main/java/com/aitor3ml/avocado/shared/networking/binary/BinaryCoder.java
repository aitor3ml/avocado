package com.aitor3ml.avocado.shared.networking.binary;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.aitor3ml.avocado.shared.networking.AvocadoDeserializer;
import com.aitor3ml.avocado.shared.networking.AvocadoSerializable;
import com.aitor3ml.avocado.shared.networking.Message;
import com.aitor3ml.avocado.shared.networking.websocket.WSProtocolConstants;

public class BinaryCoder implements WSProtocolConstants {

	public static ByteBuffer encode(Message message) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		AvocadoSerializable as = (AvocadoSerializable) message;
		as.encode(new DataOutputStream(baos));
		return ByteBuffer.wrap(baos.toByteArray());
	}

	public static Message decode(InputStream stream, AvocadoDeserializer deserializer)
			throws IOException, ClassNotFoundException {
		if (deserializer == null)
			throw new RuntimeException("no AvocadoDeserializer provided");
		return deserializer.deserialize(new DataInputStream(stream));
	}

}
