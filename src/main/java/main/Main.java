package main;

import entity.Block;
import entity.UTXO;
import entity.Wallet;
import service.UtxoUtil;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static ArrayList<Block> blockchain   = new ArrayList<>(); // 블록체인
    public static HashMap<String, UTXO> utxos   = new HashMap<>();

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // 지갑 생성
        Wallet satoshi = createGenesisUTXO();
        Wallet peter = new Wallet();
        Wallet alice = new Wallet();

        UtxoUtil.getAllUTXOs();
    }

    public static Wallet createGenesisUTXO() {
        Wallet  genesisWallet   = new Wallet();
        Wallet  satoshi         = new Wallet();
        UTXO    genesisUTXO     = new UTXO(genesisWallet.getPublicKey(), 1000f, "0");
        utxos.put("0", new UTXO(genesisWallet.getPublicKey(), 1000f, "0"));
        genesisWallet.send(satoshi.getPublicKey(), 1000f);
        return satoshi;
    }


}
