package com.aitor3ml.avocado.shared.networking;

import java.io.DataOutputStream;
import java.io.IOException;

public interface AvocadoSerializable extends Message {

	void encode(DataOutputStream os) throws IOException;

}
