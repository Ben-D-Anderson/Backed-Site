package backed.site.util;

import backed.site.mysql.MySQL;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.spec.KeySpec;
import java.util.LinkedList;
import java.util.List;

public class FileHandler {

    public static void encryptAndSaveFile(String username, File input) throws Exception {
        File output = getOutputFile(username, input);
        output.createNewFile();

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

        FileInputStream fileInputStream = new FileInputStream(input);
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, cipher);

        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = fileInputStream.read(buffer)) >= 0) {
            cipherOutputStream.write(buffer, 0, length);
        }
        fileInputStream.close();
        cipherOutputStream.flush();
        cipherOutputStream.close();
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public static void decryptToOutputStream(String username, String fileName, OutputStream outputStream) throws Exception {
        File input = getEncryptedFileOfUser(username, fileName);

        String key = MySQL.getInstance().getEncryptionKeyFromUsername(username);
        if (key == null || key.isEmpty()) {
            throw new Exception("No Encryption Key Found For User " + username);
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(key.toCharArray(), new StringBuilder(key).reverse().toString().getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));

        FileInputStream fileInputStream = new FileInputStream(input);
        CipherInputStream cipherInputStream = new CipherInputStream(fileInputStream, cipher);

        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = cipherInputStream.read(buffer)) >= 0) {
            outputStream.write(buffer, 0, length);
        }
        cipherInputStream.close();
        fileInputStream.close();
    }

    public static List<String> getAllFileNamesOfUser(String username) {
        List<String> fileNames = new LinkedList<>();
        String[] fullFileNames = getOutputDirOfUser(username).list();
        for (int i = 0; i < fullFileNames.length; i++) {
            fileNames.add(fullFileNames[i].substring(0, fullFileNames[i].lastIndexOf(".")));
        }
        return fileNames;
    }

    private static File getOutputFile(String username, File input) {
        String outputName = input.getName() + ".bak";
        File outputDir = getOutputDirOfUser(username);
        File outputFile = new File(outputDir.getAbsolutePath() + File.separator + outputName);
        if (outputFile.exists()) outputFile.delete();
        return outputFile;
    }

    private static File getOutputDirOfUser(String username) {
        String storageLoc = (String) Settings.getInstance().getConfig().getFileStorageLocation();
        if (!storageLoc.endsWith(File.separator))
            storageLoc += File.separator;
        String storageID = MySQL.getInstance().getStorageIDFromUsername(username);
        if (storageID == null || storageID.isEmpty()) {
            storageID = MySQL.getInstance().generateStorageID();
            MySQL.getInstance().setStorageID(storageID, username);
        }
        File outputDir = new File(storageLoc + storageID);
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
