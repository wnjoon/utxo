package entity;

import util.CryptoUtil;

import java.security.PublicKey;

public class UTXO {
    private String       id;
    private PublicKey    receiver;
    private float        amount;
    private String       transactionId;

    public UTXO(PublicKey receiver, float amount, String transactionId) {
        this.receiver = receiver;
        this.amount = amount;
        this.transactionId = transactionId;
        this.id = (new CryptoUtil()).applySha256((new CryptoUtil()).getStringFromKey(receiver)
                + Float.toString(amount)
                + transactionId);
    }

    public String getId() {
        return id;
    }

    public PublicKey getReceiver() {
        return receiver;
    }

    public float getAmount() {
        return amount;
    }
}
