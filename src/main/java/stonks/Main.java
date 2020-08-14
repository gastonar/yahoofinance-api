package stonks;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.*;
import java.text.SimpleDateFormat;

public class Main {

    public static void main(String[] args) {

        // get list of companies
        // get stock data for each company
        //

        Stock test;
        try {
            // should get list of strings
            test = YahooFinance.get("AAPL");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String sCurrentLine = "";
        String lastLine = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("test.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (true)
        {
            try {
                if (!((sCurrentLine = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            lastLine = sCurrentLine;
        }
        FileWriter fw;
        try {
            fw = new FileWriter("test.csv", true);
            String symbol = test.getSymbol();
            String[] lastStonk = lastLine.split(",");
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
            String strDate= formatter.format(test.getQuote().getLastTradeTime().getTime());
            if (lastStonk[0].equals(symbol) && lastStonk[2].equals(strDate)) {
                System.out.println("Data is already updated");
                return;
            }
            fw.append(symbol + "," + test.getQuote().getPrice().toString() + "," + strDate);
            fw.append("\n");
            fw.flush();
            System.out.println("Data was flushed");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println(test.getQuote().getPrice().toString());
    }

}

