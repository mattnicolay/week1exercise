package nicolay.wk1;

import java.sql.Timestamp;

public class AggregateQuote {
  private String symbol;
  private double maxPrice;
  private double minPrice;
  private int totalVolume;
  private Timestamp date;


  public Timestamp getDate() {

    return date;
  }

  public void setDate(Timestamp date) {
    this.date = date;
  }

  public int getTotalVolume() {

    return totalVolume;
  }

  public void setTotalVolume(int totalVolume) {
    this.totalVolume = totalVolume;
  }

  public double getMinPrice() {

    return minPrice;
  }

  public void setMinPrice(double minPrice) {
    this.minPrice = minPrice;
  }

  public double getMaxPrice() {

    return maxPrice;
  }

  public void setMaxPrice(double maxPrice) {
    this.maxPrice = maxPrice;
  }

  public String getSymbol() {

    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String toString() {
    return "symbol: "
        + symbol
        + "\nmaxPrice: "
        + maxPrice
        + "\nminPrice: "
        + minPrice
        + "\ntotalVolume: "
        + totalVolume
        + "\ndate: "
        + date;
  }
}
