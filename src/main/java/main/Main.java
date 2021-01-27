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
    public static int                   difficulty      = 3;

    public static WalletUtil            walletUtil      = new WalletUtil(); // 지갑쪽 유틸리티
    public static BlockUtil             blockUtil       = new BlockUtil(); // 블록 관련 유틸리티
    public static UtxoUtil              utxoUtil        = new UtxoUtil(); // UTXO 관련 유틸리티

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        /*
         * 1. 제네시스 지갑(일종의 화폐발행) 생성
         * - 모든 거래는 지갑을 통해서 진행됨
         * - 초기 1000.0만큼 부여
         */
        Wallet genesisWallet    = walletUtil.createGenesisWallet(1000f);

        /*
         * 2. GenesisWallet -> Peter에게 500.0만큼 송금
         * - 결과:
         *    ㄴ GenesisWallet: 1000.0 - 500.0 = 500.0
         *    ㄴ Peter : 500.0
         */
        Wallet peter            = walletUtil.makeWallet("peter");
        walletUtil.sendMoneyTo(genesisWallet, peter, 500f);    //genesis 500,

        /*
         * 3. Peter -> Alice에게 200.0만큼 송금
         * - 결과:
         *    ㄴ GenesisWallet: 500.0
         *    ㄴ Peter: 500.0 - 300.0 = 200.0
         *    ㄴ Alice: 300.0
         */
        Wallet alice            = walletUtil.makeWallet("alice"); // peter 200
        walletUtil.sendMoneyTo(peter, alice, 300f);

        /*
         * 4. Alice -> James에게 100.0만큼 송금
         * - 결과:
         *    ㄴ GenesisWallet: 500.0
         *    ㄴ Peter: 300.0
         *    ㄴ Alice: 300.0 - 100.0 = 200.0
         *    ㄴ James: 100.0
         */
        Wallet james            = walletUtil.makeWallet("james"); // james 100
        walletUtil.sendMoneyTo(alice, james, 100f);

        /*
         * 5. Alice -> Bob에게 150.0만큼 송금
         * - 결과:
         *    ㄴ GenesisWallet: 500.0
         *    ㄴ Peter: 300.0
         *    ㄴ Alice: 200.0 - 150.0 = 50.0
         *    ㄴ James: 100.0
         *    ㄴ Bob: 150.0
         */
        Wallet bob            = walletUtil.makeWallet("bob");   // alice 50 bob 150
        walletUtil.sendMoneyTo(alice, bob, 150f);


        System.out.println("============== Summary ==============");
        walletUtil.printUserList();
        utxoUtil.getAllUTXOs();
        blockUtil.printBlockchainInfo();
        BlockUtil.getAverage();
    }
}
