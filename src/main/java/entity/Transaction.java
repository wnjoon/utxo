package entity;

import main.Main;
import service.CryptoUtil;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
    private String          transactionId;
    private final PublicKey sender;
    private final PublicKey receiver;
    private final float     amount;
    private byte[]          signature;
    private int             sequence;
    private final ArrayList<UTXO>   inputs;
    private final CryptoUtil        util            = new CryptoUtil();
    private static final float      MINIMUM_AMOUNT  = 0.1f;

    public Transaction(PublicKey sender, PublicKey receiver, float amount, ArrayList<UTXO> inputs) {
        this.sender     = sender;
        this.receiver   = receiver;
        this.amount     = amount;
        this.inputs     = inputs;
        this.sequence   = 0;
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
        String data = util.getStringFromKey(sender)
                + util.getStringFromKey(receiver)
                + amount;
        signature = util.applyECDSASig(privateKey, data);
    }

    public boolean verifyTransactionSignature() {
        String data = util.getStringFromKey(sender)
                + util.getStringFromKey(receiver)
                + amount;
        return util.verifyECDSASig(sender, data, signature);
    }

    // 트랜잭션의 hash값 반환
    private String calculateTxHash() {
        sequence++;
        return util.applySha256(util.getStringFromKey(sender)
                + util.getStringFromKey(receiver)
                + amount + sequence);
    }

    // Input으로 들어온 UTXO의 amount 총 합을 계산 -> 추후 실제 가격을 뺀 나머지를 구하기 위함
    private float getTotalInputAmount() {
        float totalInputAmount = 0;
        for(UTXO utxo : inputs) {
            totalInputAmount += utxo.getAmount();
        }
        return totalInputAmount;
    }

    public boolean process() {
        if(verifyTransactionSignature() && (amount > MINIMUM_AMOUNT)) {
            float balance = getTotalInputAmount() - amount;
            transactionId = calculateTxHash();

            ArrayList<UTXO> outputs = new ArrayList<>();
            // 실제 송금하는 트랜잭션
            outputs.add(new UTXO(receiver, amount, transactionId));
            // 남은 금액을 나를 향해 송금(?)하는 트랜잭션 : UTXO는 최종 결과가 output이어야 한다.
            outputs.add(new UTXO(sender, balance, transactionId));

            // UTXO 리스트에 해당 output들을 입력
            for(UTXO output : outputs) {
                Main.utxos.put(output.getId(), output);
            }

            // 사용한 Input들은 UTXO 리스트에서 삭제
            // [TODO] Input 삭제와 output 입력의 순서를 어떻게 해야하나 고민
            for(UTXO input : inputs) {
                if(input != null) {
                    Main.utxos.remove(input.getId());
                }
            }
            return true;
        }
        return false;
    }
}
