package service;

import com.google.gson.GsonBuilder;
import entity.Transaction;

import java.security.*;
import java.util.Base64;

public class CryptoUtil {
    // Applies Sha256 to a string and returns the result.
    public String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Applies ECDSA Signature and returns the result ( as bytes ).
    public byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }



    // PrivateKey 또는 PublicKey -> String으로 반환
    public String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // Short hand helper to turn Object into a json string
    public String getJson(Object o) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(o);
    }
}
