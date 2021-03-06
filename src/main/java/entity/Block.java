package entity;

import main.Main;
import service.CryptoUtil;
import service.WalletUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {
    private String   hash;           // 현재 블럭의 해시값
    private final String   previousHash ;  // 이전 블럭의 해시값
    private String   data;           // 블럭에 있는 데이터
    private int      nonce;          // 블럭을 생성하는데 소요된 횟수(해시값을 찾는데 총 확인한 횟수)
    private final long     timeStamp;      // 블럭 생성일시
    private String   merkleRoot;     // 머클루트 (TODO: 확인필요)

    // 블록에 담긴 트랜잭션의 리스트 (추후 머클루트 생성에 필요)
    private final ArrayList<Transaction> transactions;

    public Block(ArrayList<Transaction> transactions) {
        this.hash = calculateBlockHash();   // 기본적으로 생성되는 블록 해시
        this.previousHash = Main.blockchain.get(Main.blockchain.size()-1).getHash();
        this.timeStamp = new Date().getTime(); // 20190210121212
        this.transactions = transactions;
        setData();
        mineBlock(Main.difficulty);
    }

    // for Genesis Block
    public Block(ArrayList<Transaction> transactions, String previousHash) {
        this.hash = calculateBlockHash();
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.transactions = transactions;
        setData();
        mineBlock(0);
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public int getNonce() {
        return nonce;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public String getData() {
        return data;
    }

    public void setData() {
        /*
         * 데이터를 무엇으로 정의할 것인가에 대해 고민이 많았음
         * ArrayList<Transaction>을 String으로 변환한 후,
         * 해당 내용을 hash값 한 것을 데이터로 정의하기로 함(개인적인 의견)
         */
        String allTransaction = "";
        for(Transaction transaction : transactions) {
            allTransaction += transaction.toString();
        }
        data = (new CryptoUtil()).applySha256(allTransaction);
    }

    // 블록의 해시값 계산
    private String calculateBlockHash() {
        return (new CryptoUtil()).applySha256(previousHash + timeStamp + nonce + merkleRoot);
    }

    // 머클루트 생성 후 변수(merkleroot)에 할당
    private void setMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();

        List<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionId());
        }
        List<String> treeLayer = previousTreeLayer;

        while(count > 1) {
            treeLayer = new ArrayList<String>();
            for(int i=1; i < previousTreeLayer.size(); i+=2) {
                treeLayer.add((new CryptoUtil()).applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

    // Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"
    private static String setDifficulty(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    // Mining block
    private void mineBlock(int difficulty) {
        setMerkleRoot(transactions);
        String target = setDifficulty(difficulty);
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateBlockHash();
        }
    }

    public String getTransaction() {
        String result = "";
        if(!transactions.isEmpty()) {
            for(Transaction transaction : transactions) {
                result += transaction.getTransactionInfo(
                        transactions.indexOf(transaction), WalletUtil.userDatabase);
            }
        }
        return result;
    }
}
