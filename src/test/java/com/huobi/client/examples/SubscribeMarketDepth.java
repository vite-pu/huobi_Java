package com.huobi.client.examples;

import com.huobi.client.SubscriptionClient;
import com.huobi.client.SubscriptionOptions;
import com.huobi.client.model.enums.CandlestickInterval;

public class SubscribeMarketDepth {
<<<<<<< HEAD
    public static void main(String[] args) {
        SubscriptionOptions options = new SubscriptionOptions();
        options.setUri("ws://18.136.19.192:8080/huobi/ws");
        SubscriptionClient subscriptionClient = SubscriptionClient.create("", "", options);
//        SubscriptionClient subscriptionClient = SubscriptionClient.create();
        subscriptionClient.subscribePriceDepthEvent("btcusdt",  (priceDepthEvent) -> {
            System.out.println();
            System.out.println("bids 0 price: " + priceDepthEvent.getData().getBids().get(0).getPrice());
            System.out.println("bids 0 volume: " + priceDepthEvent.getData().getBids().get(0).getAmount());
=======
>>>>>>> 99bf9f72f27b9f6b2b051b1573472ce49be6d9f0

  public static void main(String[] args) {
    String symbol = "htusdt";
    SubscriptionClient subscriptionClient = SubscriptionClient.create();
    subscriptionClient.subscribePriceDepthEvent(symbol, (priceDepthEvent) -> {
      System.out.println("------------Subscribe Price Depth-----------------");
      System.out.println("bids 0 price: " + priceDepthEvent.getData().getBids().get(0).getPrice());
      System.out.println("bids 0 volume: " + priceDepthEvent.getData().getBids().get(0).getAmount());

    });


    subscriptionClient.requestPriceDepthEvent(symbol,(priceDepthEvent) -> {
      System.out.println("------------Request Price Depth-----------------");
      System.out.println("bids 0 price: " + priceDepthEvent.getData().getBids().get(0).getPrice() +"  size:"+priceDepthEvent.getData().getBids().size());
      System.out.println("bids 0 volume: " + priceDepthEvent.getData().getBids().get(0).getAmount()+ " size:"+priceDepthEvent.getData().getAsks().size());
    });

  }
}
