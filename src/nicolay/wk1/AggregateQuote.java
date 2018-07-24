package nicolay.wk1;

import java.sql.Timestamp;

/**
 * Class to represent an aggregated view of a quote, storing a symbol and time period, and the
 * maximum price, minimum price, closing price, and total volume for that symbol and time period.
 * This time period is either one day or one month, with the starting date being the startDate
 * field.
 */
public class AggregateQuote {
  private String symbol;
  private double maxPrice;
  private double minPrice;
  private double closingPrice;
  private int totalVolume;
  private Timestamp startDate;
  // at the moment the timeSetting field has no use other than storing information
  private TimeSetting timeSetting;

  public AggregateQuote(String symbol, double maxPrice, double minPrice, double closingPrice,
      int totalVolume, Timestamp startDate, TimeSetting timeSetting) {
    this.symbol = symbol;
    this.maxPrice = maxPrice;
    this.minPrice = minPrice;
    this.closingPrice = closingPrice;
    this.totalVolume = totalVolume;
    this.startDate = startDate;
    this.timeSetting = timeSetting;
  }

  public TimeSetting getTimeSetting() {
    return timeSetting;
  }

  public void setTimeSetting(TimeSetting timeSetting) {
    this.timeSetting = timeSetting;
  }

  public double getClosingPrice() {
    return closingPrice;
  }

  public void setClosingPrice(double closingPrice) {
    this.closingPrice = closingPrice;
  }

  public Timestamp getStartDate() {
    return startDate;
  }

  public void setStartDate(Timestamp startDate) {
    this.startDate = startDate;
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
    return "Symbol: "
        + symbol
        + "\nMaximum Price: "
        + maxPrice
        + "\nMinimum Price: "
        + minPrice
        + "\nClosing Price: "
        + closingPrice
        + "\nTotal Volume: "
        + totalVolume
        + "\nDate: "
        + startDate
        + "\nTime Period: "
        + timeSetting;
  }
}
