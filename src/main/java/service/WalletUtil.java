package service;

import entity.Block;
import entity.Transaction;
import entity.UTXO;
import entity.Wallet;
import main.Main;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WalletUtil {

    // 지갑 소유자들의 DB를 갖고있다고 가정함
    public static HashMap<PublicKey, String> userDatabase = new HashMap<>();

    public Wallet createGenesisWallet(float amount) {
        Wallet  genesisWallet   = makeWallet("genesis");
        UTXO    genesisUTXO     = new UTXO(genesisWallet.getPublicKey(), amount, "0");
        Main.utxos.put(genesisUTXO.getId(), genesisUTXO);

        Block block = new Block(new ArrayList<Transaction>(), "0");
        Main.blockchain.add(block);
        return genesisWallet;
    }

    public Wallet makeWallet(String username) {
        Wallet wallet = new Wallet();
        userDatabase.put(wallet.getPublicKey(), username);
        return wallet;
    }

    public void sendMoneyTo(Wallet sender, Wallet receiver, float amount) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        Transaction transaction = sender.send(receiver.getPublicKey(), amount);
        if(transaction != null) {
            // Check block creation time
            transactions.add(transaction);
            long start = System.currentTimeMillis();
            Block block = new Block(transactions);
            Main.blockchain.add(block);
            long end = System.currentTimeMillis();
            BlockUtil.blockCreateTime.add(end-start);
        }
    }

    // 유저 리스트 전체 반환
    public void printUserList() {
        System.out.println("[User list information]");
        int index = 0;
        for(Map.Entry<PublicKey, String> entry : userDatabase.entrySet()) {
            System.out.println("[" + index++ + "] Username : "
                    + entry.getValue() + ", PublicKey : "
                    + entry.getKey().toString());
        }
        System.out.println("\n\n");
    }
}
