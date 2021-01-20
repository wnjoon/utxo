package entity;

import util.CryptoUtil;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
    private String          transactionId;
    private PublicKey       sender;
    private PublicKey       receiver;
    private float           amount;
    private byte[]          signature;

    private ArrayList<String> inputIds = new ArrayList<>();

    public Transaction(PublicKey sender, PublicKey receiver, float amount, ArrayList<String> inputIds) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.inputIds = inputIds;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PublicKey getSender() {
        return sender;
    }

    public PublicKey getReceiver() {
        return receiver;
    }

    public float getAmount() {
        return amount;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void generateSignature(PrivateKey privateKey) {
        CryptoUtil util = new CryptoUtil();
        String data = util.getStringFromKey(sender)
                + util.getStringFromKey(receiver)
                + Float.toString(amount);
        signature = util.applyECDSASig(privateKey, data);
    }
}
