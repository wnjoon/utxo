package main;

import entity.Block;
import entity.UTXO;
import entity.Wallet;
import service.BlockUtil;
import service.UtxoUtil;
import service.WalletUtil;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static ArrayList<Block>      blockchain      = new ArrayList<>(); // 블록체인
    public static HashMap<String, UTXO> utxos           = new HashMap<>(); // UTXO
    public static int                   difficulty      = 3; // 채굴 난이도 (공통분모로 생각됨)

    public static WalletUtil            walletUtil      = new WalletUtil(); // 지갑쪽 유틸리티
    public static BlockUtil             blockUtil       = new BlockUtil(); // 블록 관련 유틸리티

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // 지갑 생성
        Wallet genesisWallet    = walletUtil.createGenesisWallet(1000f);
        Wallet peter            = walletUtil.makeWallet("peter");

        walletUtil.sendMoneyTo(genesisWallet, peter, 500f);

//        Wallet alice            = walletUtil.makeWallet("alice");
//        walletUtil.sendMoneyTo(peter, alice, 300f);
//
//        Wallet james            = walletUtil.makeWallet("james");
//        walletUtil.sendMoneyTo(genesisWallet, james, 100f);
//
//        Wallet bob            = walletUtil.makeWallet("bob");
//        walletUtil.sendMoneyTo(genesisWallet, bob, 100f);


        System.out.println("============== Summary ==============");
        walletUtil.printUserList();
        UtxoUtil.getAllUTXOs();
        blockUtil.printBlockchainInfo();

    }
}
