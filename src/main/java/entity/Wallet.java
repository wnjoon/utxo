package entity;

import main.Main;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    private PrivateKey  privateKey;
    private PublicKey   publicKey;

    public Wallet() {
        generateKeyPair();
    }

    // 지갑에 해당하는 UTXO 리스트
    private HashMap<String, UTXO> my_utxos = new HashMap<>();

    // 지갑에 해당하는 UTXO들 중, 사용 될(Transaction input) 상태의 UTXO
    private HashMap<String, UTXO> input = new HashMap<>();

    // 지갑의 PrivateKey & PublicKey 쌍 생성
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 본인의 계좌번호(PublicKey) 반환
    public PublicKey getPublicKey() {
        return publicKey;
    }

    // 본인 계좌에 해당하는 UTXO 리스트 반환
    public HashMap<String, UTXO> getWalletUTXO() {
        return my_utxos;
    }

    // 본인의 잔액을 확인해주는 부분
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, UTXO> entry: Main.utxos.entrySet()){
            UTXO utxo = entry.getValue();
            if(utxo.getReceiver() == publicKey) {
                my_utxos.put(utxo.getId(), utxo);
                total += utxo.getAmount();
            }
        }
        return total;
    }

    // 나의 지갑에서 상대방에게 송금하는 트랜잭션을 생성
    public Transaction sendTransaction(PublicKey receiver, float amount) {
        float amountToSend = 0;
        ArrayList<String> inputIds = new ArrayList<>();
        HashMap<String, UTXO> inputs = new HashMap<>();
        for (Map.Entry<String, UTXO> entry : my_utxos.entrySet()){
            UTXO utxo = entry.getValue();
            amountToSend += utxo.getAmount();
            inputIds.add(utxo.getId());
            if(amountToSend >= amount) break;
        }

        Transaction transaction = new Transaction(publicKey, receiver, amount, inputIds);
        transaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            UTXO_Wallet.remove(input.transactionOutputId);
        }

        return newTransaction;
}
