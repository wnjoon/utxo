package main;

import entity.Block;
import entity.UTXO;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static ArrayList<Block> blockchain   = new ArrayList<>(); // 블록체인
    public static HashMap<String, UTXO> utxos   = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("TEST");
    }
}
