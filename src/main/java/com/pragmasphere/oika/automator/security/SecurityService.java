package com.pragmasphere.oika.automator.security;

public interface SecurityService {
    String encrypt(String data);

    String decrypt(String data);
}
