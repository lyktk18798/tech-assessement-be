package com.lyktk.urlshortener.utils;
import com.lyktk.urlshortener.exception.InvalidDataException;
import org.springframework.util.ObjectUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UrlUtils {
    public static String generateShortUrl(String originalUrl, int length) {
        try {
            if (ObjectUtils.isEmpty(originalUrl)) {
                throw new InvalidDataException("Original URL cannot be null or empty");
            }
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // digest() method is called to calculate message digest of an input digest() return array of byte
            byte[] messageDigest = md.digest(originalUrl.getBytes());
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            return hashtext.substring(0,length);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
