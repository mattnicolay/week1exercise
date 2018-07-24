package nicolay.wk1;

import java.sql.Timestamp;

/**
 * Class to represent a single stock quote. Holds data for the stock symbol, price, volume, and date
 */
public class Quote {

  private String symbol;
  private double price;
  private int volume;
  private Timestamp date;

  public Quote (){
    symbol = null;
    price = 0;
    volume = 0;
    date = null;
  }

  public Quote (String symbol, double price, int volume, Timestamp date) {
    this.symbol = symbol;
    this.price = price;
    this.volume = volume;
    this.date = date;
  }

  public Timestamp getDate() {
    return date;
  }

  public void setDate(Timestamp date) {
    this.date = date;
  }

  public int getVolume() {

    return volume;
  }

  public void setVolume(int volume) {
    this.volume = volume;
  }

  public double getPrice() {

    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getSymbol() {

    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }
}
