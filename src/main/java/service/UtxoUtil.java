package service;

import entity.UTXO;
import main.Main;

import java.util.Map;

public class UtxoUtil {

    public static void getAllUTXOs() {
        System.out.println("[UTXO information]");
        int index = 0;
        for(Map.Entry<String, UTXO> entry : Main.utxos.entrySet()) {
            System.out.println("[" + index++ + "] " + entry.getValue().toString());
        }
        System.out.println("\n\n");
    }
}
