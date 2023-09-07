package com.prologics.tools.nzip.util;

import java.util.Base64;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class EncDecUtil {
	
	private EncDecUtil() {
		throw new IllegalStateException("Utility class");
	}
	
    private static final int BLOCK_SIZE = 16; // AES block size is 128 bits (16 bytes)

    public static String encrypt(String content) throws Exception {
        String key = System.getProperty("key");
        String iv = System.getProperty("iv");
        
    	byte[] keyBytes = key.getBytes();
        byte[] ivBytes = iv.getBytes();
        byte[] plaintext = content.getBytes();

        BufferedBlockCipher cipher = new BufferedBlockCipher(new CFBBlockCipher(new AESEngine(), BLOCK_SIZE * 8));
        CipherParameters params = new ParametersWithIV(new KeyParameter(keyBytes), ivBytes);
        cipher.init(true, params);

        byte[] ciphertext = new byte[cipher.getOutputSize(plaintext.length)];
        int len = cipher.processBytes(plaintext, 0, plaintext.length, ciphertext, 0);
        cipher.doFinal(ciphertext, len);

        return Base64.getEncoder().encodeToString(ciphertext);
    }

    public static String decrypt(String encryptedContent) throws Exception {
    	String key = System.getProperty("key");
        String iv = System.getProperty("iv");
        
        byte[] keyBytes = key.getBytes();
        byte[] ivBytes = iv.getBytes();
        byte[] ciphertext = Base64.getDecoder().decode(encryptedContent);

        BufferedBlockCipher cipher = new BufferedBlockCipher(new CFBBlockCipher(new AESEngine(), BLOCK_SIZE * 8));
        CipherParameters params = new ParametersWithIV(new KeyParameter(keyBytes), ivBytes);
        cipher.init(false, params);

        byte[] plaintext = new byte[cipher.getOutputSize(ciphertext.length)];
        int len = cipher.processBytes(ciphertext, 0, ciphertext.length, plaintext, 0);
        cipher.doFinal(plaintext, len);

        return new String(plaintext);
    }
}