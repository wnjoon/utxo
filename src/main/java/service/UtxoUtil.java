package service;

import entity.UTXO;
import main.Main;

import java.util.Map;

public class UtxoUtil {

    public static void getAllUTXOs() {
        for(Map.Entry<String, UTXO> entry : Main.utxos.entrySet()) {
            System.out.println("==== All UTXOs ====");
            System.out.println(entry.toString());
        }
    }
}
