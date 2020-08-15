package stonks;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Double.parseDouble;

public class Main {

    private static String stockSymbolsFilename = "sp500companies.csv";
    private static String stockDataFilename = "stock_data.csv";

    public static void main(String[] args) {
        // create file if necessary
        // get list of companies
        // get stock data for each company
        // read stock data stored locally
        // compare previous data with new data
        // write updated data to csv
        List<String> stockSymbols = getStockSymbolsFromCsv(stockSymbolsFilename);
        String[] stockSymbolsArray = new String[stockSymbols.size()];
        Map<String, Stock> stockData = getYahooFinanceData(stockSymbols.toArray(stockSymbolsArray));

        List<StockCsvData> previousStockData = getStoredStockDataFromCsv(stockDataFilename);
        List<StockCsvData> currentStockData = getStockListFromMap(stockData);
        List<StockCsvData> updatedStockData = comparePreviousAndCurrentStockData(previousStockData, currentStockData);
        sortStockByPercentChange(updatedStockData);
        writeStockDataToCsv(updatedStockData);
    }

    private static List<StockCsvData> comparePreviousAndCurrentStockData(List<StockCsvData> previousStockData, List<StockCsvData> currentStockData) {
        Map<String, StockCsvData> previousStockDataMap = getMapFromStockList(previousStockData);
        Map<String, StockCsvData> currentStockDataMap = getMapFromStockList(currentStockData);
        List<StockCsvData> updatedStockData = new ArrayList<StockCsvData>();

        for (String symbol : currentStockDataMap.keySet()) {
            StockCsvData previousStock = previousStockDataMap.get(symbol);
            if (previousStock != null) {
                updatePreviousFieldsFromStoredData(currentStockDataMap.get(symbol), previousStock);
            }
            updatedStockData.add(currentStockDataMap.get(symbol));
        }

        return updatedStockData;
    }

    private static void updatePreviousFieldsFromStoredData(StockCsvData stockCsvData, StockCsvData previousStock) {
        String previousPrice = previousStock.getLastPrice();
        String previousPriceDate = previousStock.getLastPriceDate();
        if (previousPriceDate.equals(stockCsvData.getLastPriceDate())) {
            return;
        }
        double nominalChange = parseDouble(stockCsvData.getLastPrice()) - parseDouble(previousPrice);
        double percentChange = (parseDouble(stockCsvData.getLastPrice()) - parseDouble(previousPrice)) / parseDouble(previousPrice);
        stockCsvData.setPreviousPrice(previousPrice);
        stockCsvData.setPreviousDate(previousPriceDate);
        stockCsvData.setNominalChange(String.valueOf(nominalChange));
        stockCsvData.setPercentChange(String.valueOf(percentChange));
    }

    private static Map<String, StockCsvData> getMapFromStockList(List<StockCsvData> stockCsvDataList) {
        Map<String, StockCsvData> stockMap = new HashMap<String, StockCsvData>();
        for (StockCsvData csvData : stockCsvDataList) {
            stockMap.put(csvData.getSymbol(), csvData);
        }
        return stockMap;
    }

    private static List<StockCsvData> getStoredStockDataFromCsv(String filename) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<StockCsvData>();
        }
        List<StockCsvData> stockCsvDataList = new ArrayList<StockCsvData>();
        try {
            String currentLine = br.readLine();
            while (currentLine != null) {
                StockCsvData stockCsvData = new StockCsvData(currentLine);
                stockCsvDataList.add(stockCsvData);
                currentLine = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<StockCsvData>();
        }
        return stockCsvDataList;
    }

    private static void writeStockDataToCsv(List<StockCsvData> stockList) {
        FileWriter fw;
        try {
            fw = new FileWriter(stockDataFilename);
            for (StockCsvData stockData : stockList) {
                fw.append(stockData.toString());
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private static List<StockCsvData> getStockListFromMap(Map<String, Stock> stockData) {
        List<StockCsvData> stocks = new ArrayList<StockCsvData>();
        Stock stock;
        for (String symbol : stockData.keySet()) {
            try {
                stock = stockData.get(symbol);
                if (stock.getName() == null || stock.getQuote() == null || stock.getQuote().getPrice() == null || stock.getQuote().getPrice().equals(new BigDecimal(0))) {
                    continue;
                }
                stocks.add(new StockCsvData(stock));
            } catch (NullPointerException e) {
                System.out.println(symbol);
            }
        }
        return stocks;
    }

    private static void sortStockByPercentChange(List<StockCsvData> stockCsvData) {
        Collections.sort(stockCsvData, new Comparator<StockCsvData>() {
            @Override
            public int compare(StockCsvData s1, StockCsvData s2) {
                int difference = Double.compare(parseDouble(s2.getPercentChange()), parseDouble(s1.getPercentChange()));
                if (difference == 0) {
                    return s1.getSymbol().compareTo(s2.getSymbol());
                } else {
                    return difference;
                }
            }
        });
    }

    private static Map<String, Stock> getYahooFinanceData(String[] stockSymbols) {
        Map<String, Stock> stockData;
        try {
            stockData = YahooFinance.get(stockSymbols);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<String, Stock>();
        }
        return stockData;
    }

    private static List<String> getStockSymbolsFromCsv(String filename) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        List<String> stockSymbols = new ArrayList<String>();
        try {
            br.readLine(); // first line contains the headers (Symbol, Name, Industry), so we do not want to keep it
            String currentStock = br.readLine();
            while (currentStock != null) {
                stockSymbols.add(currentStock.substring(0, currentStock.indexOf(",")).trim());
                currentStock = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stockSymbols;
    }

}

