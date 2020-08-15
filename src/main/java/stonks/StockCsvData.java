package stonks;

import yahoofinance.Stock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.String.format;

public class StockCsvData {

    private String symbol;
    private String lastPrice;
    private String lastPriceDate;
    private String previousPrice;
    private String previousDate;
    private String nominalChange;
    private String percentChange;

    public StockCsvData(String csvData) {
        String[] csvDataArray = csvData.split(",");
        this.symbol = csvDataArray[0];
        this.lastPrice = csvDataArray[1];
        this.lastPriceDate = csvDataArray[2];
        this.previousPrice = csvDataArray[3];
        this.previousDate = csvDataArray[4];
        this.nominalChange = csvDataArray[5];
        this.percentChange = csvDataArray[6];
    }

    public StockCsvData(Stock stock) {
        this.symbol = stock.getSymbol();
        this.lastPrice = String.valueOf(stock.getQuote().getPrice());
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
        this.lastPriceDate = formatter.format(stock.getQuote().getLastTradeTime().getTime());
        this.previousPrice = String.valueOf(stock.getQuote().getOpen());
        this.previousDate = formatter.format(removeTime());
        this.nominalChange = String.valueOf(stock.getQuote().getChange());
        this.percentChange = String.valueOf(stock.getQuote().getChangeInPercent());
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getLastPriceDate() {
        return lastPriceDate;
    }

    public void setLastPriceDate(String lastPriceDate) {
        this.lastPriceDate = lastPriceDate;
    }

    public String getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(String previousPrice) {
        this.previousPrice = previousPrice;
    }

    public String getPreviousDate() {
        return previousDate;
    }

    public void setPreviousDate(String previousDate) {
        this.previousDate = previousDate;
    }

    public String getNominalChange() {
        return nominalChange;
    }

    public void setNominalChange(String nominalChange) {
        this.nominalChange = nominalChange;
    }

    public String getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(String percentChange) {
        this.percentChange = percentChange;
    }

    public static Date removeTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    public String toString() {
        return format("%s,%s,%s,%s,%s,%s,%s\n", symbol, lastPrice, lastPriceDate, previousPrice, previousDate, nominalChange, percentChange);
    }
}
