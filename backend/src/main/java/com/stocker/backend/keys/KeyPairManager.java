package com.stocker.backend.keys;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

//saveKeyPairToPem() method 를 사용하여 임시적으로 key를 생성할 수 있다.
@Component
public class KeyPairManager {

    private KeyPair keyPair;

    public KeyPairManager() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        this.keyPair = keyPairGenerator.generateKeyPair();
    }

    // Method to save the key pair to PEM files
    public void saveKeyPairToPem(String privateKeyFile, String publicKeyFile) throws IOException {
        // Save private key
        try (PemWriter pemWriter = new PemWriter(new FileWriter(privateKeyFile))) {
            PemObject pemObject = new PemObject("PRIVATE KEY", keyPair.getPrivate().getEncoded());
            pemWriter.writeObject(pemObject);
        }

        // Save public key
        try (PemWriter pemWriter = new PemWriter(new FileWriter(publicKeyFile))) {
            PemObject pemObject = new PemObject("PUBLIC KEY", keyPair.getPublic().getEncoded());
            pemWriter.writeObject(pemObject);
        }
    }

    // Method to load key pair from PEM files --version
    public KeyPair loadKeyPairFromPem(String privateKeyFile, String publicKeyFile) throws Exception {
        // Load private key
        byte[] privateKeyBytes;
        try (PemReader pemReader = new PemReader(new FileReader(privateKeyFile))) {
            PemObject pemObject = pemReader.readPemObject();
            privateKeyBytes = pemObject.getContent();
        }
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        // Load public key
        byte[] publicKeyBytes;
        try (PemReader pemReader = new PemReader(new FileReader(publicKeyFile))) {
            PemObject pemObject = pemReader.readPemObject();
            publicKeyBytes = pemObject.getContent();
        }
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    // Getters for the private and public keys
    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

//    public static void main(String[] args) {
//        try {
//            KeyPairManager keyPairManager = new KeyPairManager();
//            // Save the generated key pair to PEM files
//            keyPairManager.saveKeyPairToPem("privateKey.pem", "publicKey.pem");
//
//            // Load the key pair back from the PEM files
////            KeyPair loadedKeyPair = keyPairManager.loadKeyPairFromPem("privateKey.pem", "publicKey.pem");
//
//            // Display the loaded keys
////            System.out.println("Loaded Private Key: " + loadedKeyPair.getPrivate());
////            System.out.println("Loaded Public Key: " + loadedKeyPair.getPublic());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}