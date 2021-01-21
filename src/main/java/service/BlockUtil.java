package service;

import entity.Block;
import entity.Transaction;
import main.Main;

import java.util.ArrayList;

public class BlockUtil {

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
