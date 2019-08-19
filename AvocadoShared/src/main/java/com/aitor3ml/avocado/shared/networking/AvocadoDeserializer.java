package com.aitor3ml.avocado.shared.networking;

import java.io.DataInputStream;
import java.io.IOException;

public interface AvocadoDeserializer {

	AvocadoSerializable deserialize(DataInputStream is) throws IOException;

}
