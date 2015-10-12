package com.aitor3ml.avocado.shared.networking.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import com.aitor3ml.avocado.shared.networking.Message;

public class TextCoder {

	public static String encode(Message message) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(message);

			String msg = Base64.getEncoder().encodeToString(baos.toByteArray());
			return msg;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Message decode(String msg) {
		try {
			byte[] data = Base64.getDecoder().decode(msg);

			ByteArrayInputStream in = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(in);
			return (Message) is.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
