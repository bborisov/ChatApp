package com.ibm.activities;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class HashActivity {

	public static byte[] hashPassword(final String password, final byte[] salt) {
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 256, 384);
			SecretKey key = skf.generateSecret(spec);
			byte[] res = key.getEncoded();

			return res;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] getRandomSalt() {
		byte[] b = new byte[20];
		new Random().nextBytes(b);

		return b;
	}

}
