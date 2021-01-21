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
    // 지갑에 해당하는 UTXO 리스트
    //private HashMap<String, UTXO> my_utxos;
    private ArrayList<UTXO> my_utxos;

    public Wallet() {
        generateKeyPair();
    }

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
    // 본인의 비밀번호(PrivateKey) 반환
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    // 본인의 잔액을 확인해주는 부분
    public float getBalance() {
        float total = 0;
        my_utxos = new ArrayList<>();
        for (Map.Entry<String, UTXO> entry: Main.utxos.entrySet()){
            UTXO utxo = entry.getValue();
            if(utxo.getReceiver() == publicKey) {
                total += utxo.getAmount();
                my_utxos.add(utxo);
            }
        }
        return total;
    }

    // 나의 지갑에서 상대방에게 송금하는 트랜잭션을 생성
    public boolean send(PublicKey receiver, float amount) {

        // balance check
        if(getBalance() < amount) {
            System.out.println("[ERROR] Not enough money in your utxos");
            return false;
        }

        float amountToSend = 0;
        ArrayList<UTXO> inputs = new ArrayList<>();
        for(UTXO utxo : my_utxos) {
            amountToSend += utxo.getAmount();
            inputs.add(utxo);
            if(amountToSend >= amount) break;
        }
//        for (Map.Entry<String, UTXO> entry : my_utxos.entrySet()) {
//            UTXO utxo = entry.getValue();
//            amountToSend += utxo.getAmount();
//            inputs.add(utxo);
//            if (amountToSend >= amount) break;
//        }

        Transaction transaction = new Transaction(publicKey, receiver, amount, inputs);
        transaction.generateSignature(privateKey);

        if(transaction.process()) {
            return true;
        }
        return false;
    }

    // [TODO]전체 UTXO 중에서 지갑에 해당하는 목록을 동기화하는 부분 (추후 개발)
    private void synchronizeWallet() {
    }
}
