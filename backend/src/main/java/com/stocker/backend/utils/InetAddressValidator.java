package com.stocker.backend.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class InetAddressValidator {
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(
            "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                    "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|" + // IPv4
                    "([a-fA-F0-9]{1,4}:){7}[a-fA-F0-9]{1,4}$"      // IPv6
    );
    public boolean isValidInetAddress(String ipAddress){
        try{
            boolean result = IP_ADDRESS_PATTERN.matcher(ipAddress).matches();

            return result;
        }catch (Exception e ){
            return false;
        }
    }
}
