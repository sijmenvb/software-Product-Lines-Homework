package main;

import enums.Algorithms;

public interface EncryptionInterface {
	String encrypt(final String message);
	String decrypt(final String message);
	public Algorithms getEncryptionType();
}
