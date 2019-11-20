package com.huobi.client.examples;

import com.huobi.client.SubscriptionClient;
import com.huobi.client.SubscriptionOptions;

public class Subscribe24HTradeStatistics {

  public static void main(String[] args) {
    String symbol = "btcusdt";
    // Subscribe 24HTradeStatistics with custom server
    SubscriptionOptions options = new SubscriptionOptions();
    SubscriptionClient subscriptionClient = SubscriptionClient.create("", "", options);
<<<<<<< HEAD
//    subscriptionClient.subscribe24HTradeStatisticsEvent("btcusdt", (statisticsEvent) -> {
//      System.out.println();
//      System.out.println("Timestamp: " + statisticsEvent.getData().getTimestamp());
//      System.out.println("High: " + statisticsEvent.getData().getHigh());
//      System.out.println("Low: " + statisticsEvent.getData().getLow());
//      System.out.println("Open: " + statisticsEvent.getData().getOpen());
//      System.out.println("Close: " + statisticsEvent.getData().getClose());
//      System.out.println("Volume: " + statisticsEvent.getData().getVolume());
//    });
    subscriptionClient.subscribeTickerEvent("btcusdt,ethusdt", (tickerEvent) -> {
        System.out.println(tickerEvent.getTicker().toString());
    }, e -> {});
=======
    subscriptionClient.subscribe24HTradeStatisticsEvent(symbol, (statisticsEvent) -> {
      System.out.println("-------------Subscribe 24H Trade Statistics-------------");
      System.out.println("Timestamp: " + statisticsEvent.getData().getTimestamp());
      System.out.println("High: " + statisticsEvent.getData().getHigh());
      System.out.println("Low: " + statisticsEvent.getData().getLow());
      System.out.println("Open: " + statisticsEvent.getData().getOpen());
      System.out.println("Close: " + statisticsEvent.getData().getClose());
      System.out.println("Volume: " + statisticsEvent.getData().getVolume());
    });

    subscriptionClient.request24HTradeStatisticsEvent(symbol, (statisticsEvent) -> {
      System.out.println("-------------Request 24H Trade Statistics-------------");
      System.out.println("Timestamp: " + statisticsEvent.getData().getTimestamp());
      System.out.println("High: " + statisticsEvent.getData().getHigh());
      System.out.println("Low: " + statisticsEvent.getData().getLow());
      System.out.println("Open: " + statisticsEvent.getData().getOpen());
      System.out.println("Close: " + statisticsEvent.getData().getClose());
      System.out.println("Volume: " + statisticsEvent.getData().getVolume());
    });
>>>>>>> 99bf9f72f27b9f6b2b051b1573472ce49be6d9f0
  }
}
