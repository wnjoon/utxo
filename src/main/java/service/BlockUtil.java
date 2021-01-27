package service;

import entity.Block;
import entity.Transaction;
import main.Main;

import java.util.ArrayList;
import java.util.Collections;

public class BlockUtil {

    public static ArrayList<Long> blockCreateTime = new ArrayList<>();

       public static void getAverage() {
        Collections.sort(blockCreateTime);

        String minTime = Long.toString(blockCreateTime.get(0));
        String maxTime = Long.toString(blockCreateTime.get(blockCreateTime.size()-1));

        Long totalTime = 0L;
        for(int i=0; i<blockCreateTime.size(); i++) {
            totalTime += blockCreateTime.get(i);
        }

        String avgTime = Long.toString(totalTime/blockCreateTime.size());
        System.out.println("[Block creation information]");
        System.out.println("- Minimum Time : " + minTime + "ms");
        System.out.println("- Maximum Time : " + maxTime + "ms");
        System.out.println("- Average Time : " + avgTime + "ms");

    }

    public void printBlockchainInfo() {
        System.out.println("[Blockchain information]");
        for(Block block : Main.blockchain) {
            System.out.println(printBlockInfo(Main.blockchain.indexOf(block), block));
        }
        System.out.println("\n\n");
    }

    public String printBlockInfo(int index, Block block) {
        return "Block[" + index + "] {" + "\n"
                + "\t- hash=" + block.getHash() + "\n"
                + "\t- previousHash=" + block.getPreviousHash()+ "\n"
                + "\t- nonce=" + block.getNonce() + "\n"
                + "\t- merkleRoot=" + block.getMerkleRoot() + "\n"
                + "\t- timeStamp=" + block.getTimeStamp() + "\n"
                + "\t- data=" + block.getData() + "\n"
                + "\t- transactions=\n" + block.getTransaction() + "\n"
                + "}";
    }

}
