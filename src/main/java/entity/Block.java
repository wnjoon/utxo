package entity;

import service.CryptoUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {
    private String   hash;           // 현재 블럭의 해시값
    private String   previousHash ;  // 이전 블럭의 해시값
    private String   data;           // 블럭에 있는 데이터
    private int      nonce;          // 블럭을 생성하는데 소요된 횟수(해시값을 찾는데 총 확인한 횟수)
    private long     timeStamp;      // 블럭 생성일시
    private String   merkleRoot;     // 머클루트 (TODO: 확인필요)

    // 블록에 담긴 트랜잭션의 리스트 (추후 머클루트 생성에 필요)
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public Block(String previousHash) {
        this.hash = calculateBlockHash();
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime(); // 20190210121212
    }

    // 블록의 해시값 계산
    private String calculateBlockHash() {
        return (new CryptoUtil()).applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
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
    public static String setDifficulty(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }
}
