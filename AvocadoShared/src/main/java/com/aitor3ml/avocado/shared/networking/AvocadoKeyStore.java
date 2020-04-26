package com.aitor3ml.avocado.shared.networking;

import java.net.URL;

import com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
public class AvocadoKeyStore {

	private static final String PATH = "keystore.jks";
	private static final String STORE_PASSWORD = "odacova";
	private static final String KEY_PASSWORD = "odacova";

	public static URL getUrl() {
		return AvocadoKeyStore.class.getResource(PATH);
	}

	public static String getStorePassword() {
		return STORE_PASSWORD;
	}

	public static String getKeyPassword() {
		return KEY_PASSWORD;
	}

}
