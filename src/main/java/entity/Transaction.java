package entity;

import main.Main;
import service.CryptoUtil;
import service.WalletUtil;

import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Transaction {
    private         String      transactionId;
    private final   PublicKey   sender;
    private final   PublicKey   receiver;
    private final   float       amount;
    private byte[]              signature;
    private int                 sequence;
    private final   ArrayList<UTXO>     inputs;    
    private static  final float         MINIMUM_AMOUNT  = 0.1f; // 최소 금액

    private final   CryptoUtil          util            = new CryptoUtil();

    public Transaction(PublicKey sender, PublicKey receiver, float amount, ArrayList<UTXO> inputs) {
        this.sender     = sender;
        this.receiver   = receiver;
        this.amount     = amount;
        this.inputs     = inputs;
        this.sequence   = 0;
    }

    // Getter
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
    
    // 서명값 생성
    public void generateSignature(PrivateKey privateKey) {
        String data = util.getStringFromKey(sender)
                + util.getStringFromKey(receiver)
                + amount;
        signature = util.applyECDSASig(privateKey, data);
    }

    // 서명값 증명
    public boolean verifyTransactionSignature() {
        String data = util.getStringFromKey(sender)
                + util.getStringFromKey(receiver)
                + amount;
        return util.verifyECDSASig(sender, data, signature);
    }

    // 트랜잭션의 hash값 생성
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
            if(balance > 0f) {
                outputs.add(new UTXO(sender, balance, transactionId));
            }
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

    // 실제 Transaction의 정보를 반환하는데 사용
    public String getTransactionInfo(int index, HashMap<PublicKey, String> userDatabase) {
        return "\t\t- [" + index + "]\n"
                + "\t\t\t- transactionId=" + transactionId + "\n"
                + "\t\t\t- sender=" + userDatabase.get(sender) + "\n"
                + "\t\t\t- receiver=" + userDatabase.get(receiver) + "\n"
                + "\t\t\t- amount=" + amount + "\n";
    }

    // 추후 Block의 data를 계산할 때 사용
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", amount=" + amount +
                '}';
    }
}
