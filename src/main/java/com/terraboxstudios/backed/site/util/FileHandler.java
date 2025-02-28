package com.terraboxstudios.backed.site.util;

import com.terraboxstudios.backed.site.api.exceptions.NoValidEncryptionKeyException;
import com.terraboxstudios.backed.site.mysql.MySQL;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.spec.KeySpec;

public class FileHandler {

    public static void encryptAndSaveFile(String username, InputStream inputStream, String inputName) throws IOException, GeneralSecurityException {
        File output = getOutputFile(username, inputName);
        if (output.exists())
            output.delete();
        createFile(output);

        String key = MySQL.getInstance().getEncryptionKeyFromUsername(username);
        if (key == null || key.isEmpty()) {
            key = MySQL.getInstance().generateEncryptionKey();
            MySQL.getInstance().setEncryptionKey(key, username);
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(key.toCharArray(), new StringBuilder(key).reverse().toString().getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));

        try (
            FileOutputStream fileOutputStream = new FileOutputStream(output);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, cipher);
        ) {
            byte[] buffer = new byte[1024 * 1024];
            int length;
            while ((length = inputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            cipherOutputStream.flush();
            fileOutputStream.flush();
        }

    }

    private static void createFile(File output) throws IOException {
        if (!output.getParentFile().exists())
            output.getParentFile().mkdirs();
        output.createNewFile();
    }

    public static void decryptToOutputStream(String username, String fileName, OutputStream outputStream) throws FileNotFoundException, IOException, NoValidEncryptionKeyException, GeneralSecurityException {
        File input = getEncryptedFileOfUser(username, fileName);

        String key = MySQL.getInstance().getEncryptionKeyFromUsername(username);
        if (key == null || key.isEmpty()) {
            throw new NoValidEncryptionKeyException("No Encryption Key Found For User " + username);
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(key.toCharArray(), new StringBuilder(key).reverse().toString().getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));

        try (
            FileInputStream fileInputStream = new FileInputStream(input);
            CipherInputStream cipherInputStream = new CipherInputStream(fileInputStream, cipher);
        ) {
            byte[] buffer = new byte[1024 * 1024];
            int length;
            while ((length = cipherInputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, length);
            }
        }

    }

    public static String getDecryptedFileHash(String username, String fileName, MessageDigest digest) throws IOException, GeneralSecurityException, NoValidEncryptionKeyException {
        File input = getEncryptedFileOfUser(username, fileName);

        String key = MySQL.getInstance().getEncryptionKeyFromUsername(username);
        if (key == null || key.isEmpty()) {
            throw new NoValidEncryptionKeyException("No Encryption Key Found For User " + username);
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(key.toCharArray(), new StringBuilder(key).reverse().toString().getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));

        FileInputStream fileInputStream = new FileInputStream(input);
        CipherInputStream cipherInputStream = new CipherInputStream(fileInputStream, cipher);

        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        while ((bytesCount = cipherInputStream.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        cipherInputStream.close();

        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static File getOutputFile(String username, String inputName) {
        String outputName = inputName;
        if (!outputName.endsWith("\\") && !outputName.endsWith("/"))
            outputName += ".bak";
        File outputDir = getOutputDirOfUser(username);
        return new File(outputDir.getPath() + File.separator + outputName);
    }

    public static File getOutputDirOfUser(String username) {
        String storageLoc = Settings.getInstance().getConfig().getFileStorageLocation().toString().replace("\"", "");
        String storageID = MySQL.getInstance().getStorageIDFromUsername(username);
        if (storageID == null || storageID.isEmpty()) {
            storageID = MySQL.getInstance().generateStorageID();
            MySQL.getInstance().setStorageID(storageID, username);
        }
        File outputDir = new File(storageLoc + File.separator + storageID);
        if (!outputDir.exists()) outputDir.mkdirs();
        return outputDir;
    }

    private static File getEncryptedFileOfUser(String username, String fileName) throws FileNotFoundException {
        File userDir = getOutputDirOfUser(username);
        File file = new File(userDir.getAbsolutePath() + File.separator + fileName + ".bak");
        if (!file.exists()) throw new FileNotFoundException();
        return file;
    }

}
