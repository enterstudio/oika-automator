package com.pragmasphere.oika.automator.security;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOError;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Base64;

@Service
public class SecurityServiceImpl implements SecurityService {

    private static final String CHARSET = "UTF-8";
    final Key key = new SecretKeySpec(getBytes("BA18C3583F052EB6"), "Blowfish");

    private static byte[] getBytes(final String keyString) {
        try {
            return keyString.getBytes(CHARSET);
        } catch (final UnsupportedEncodingException e) {
            throw new IOError(e);
        }
    }

    @Override
    public String encrypt(final String data) {
        try {
            final byte[] bData = data.getBytes(CHARSET);
            final Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final byte[] bEncryptedData = cipher.doFinal(bData);
            return new String(Base64.getEncoder().encode(bEncryptedData), CHARSET);
        } catch (final Exception e) {
            throw new RuntimeException("Can't encrypt data", e);
        }
    }

    @Override
    public String decrypt(final String encodedData) {
        try {
            final byte[] bEncryptedData = Base64.getDecoder().decode(encodedData.getBytes(CHARSET));
            final Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, key);
            final byte[] bData = cipher.doFinal(bEncryptedData);
            return new String(bData, CHARSET);
        } catch (final Exception e) {
            throw new RuntimeException("Can't decrypt data", e);
        }
    }
}
