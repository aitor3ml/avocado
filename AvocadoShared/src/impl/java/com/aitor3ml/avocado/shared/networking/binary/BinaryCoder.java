package com.aitor3ml.avocado.shared.networking.binary;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import com.aitor3ml.avocado.shared.networking.AvocadoDeserializer;
import com.aitor3ml.avocado.shared.networking.AvocadoSerializable;
import com.aitor3ml.avocado.shared.networking.Message;
import com.aitor3ml.avocado.shared.networking.websocket.WSProtocolConstants;

public class BinaryCoder implements WSProtocolConstants {

	public static ByteBuffer encode(Message message) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (message instanceof AvocadoSerializable) {
			AvocadoSerializable as = (AvocadoSerializable) message;
			baos.write(AVOCADO_SERIALIZABLE);
			as.encode(new DataOutputStream(baos));
		} else if (message instanceof Serializable) {
			baos.write(JAVA_SERIALIZED);
			try {
				writeMessage(baos, message);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else
			throw new RuntimeException("don't know to encode " + message.toString());
		return ByteBuffer.wrap(baos.toByteArray());
	}

	public static Message decode(InputStream stream, AvocadoDeserializer deserializer)
			throws IOException, ClassNotFoundException {
		int code = stream.read();
		switch (code) {
		case AVOCADO_SERIALIZABLE:
			if (deserializer == null)
				throw new RuntimeException("no AvocadoDeserializer provided");
			return deserializer.deserialize(new DataInputStream(stream));
		case JAVA_SERIALIZED:
			return readMessage(stream);
		default:
			throw new RuntimeException("don't know to decode " + code);
		}
	}

	public static Message readMessage(InputStream stream) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(stream);
		Message m = (Message) in.readObject();
		in.close();
		return m;
	}

	public static void writeMessage(ByteArrayOutputStream baos, Message message) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(message);
		out.close();
	}

}
