package com.huobi.client.examples;

import com.huobi.client.SubscriptionClient;
import com.huobi.client.SubscriptionOptions;
import com.huobi.client.model.enums.CandlestickInterval;

public class SubscribeMarketDepth {
    public static void main(String[] args) {
        SubscriptionOptions options = new SubscriptionOptions();
        options.setUri("ws://18.136.19.192:8080/huobi/ws");
        SubscriptionClient subscriptionClient = SubscriptionClient.create("", "", options);
//        SubscriptionClient subscriptionClient = SubscriptionClient.create();
        subscriptionClient.subscribePriceDepthEvent("btcusdt",  (priceDepthEvent) -> {
            System.out.println();
            System.out.println("bids 0 price: " + priceDepthEvent.getData().getBids().get(0).getPrice());
            System.out.println("bids 0 volume: " + priceDepthEvent.getData().getBids().get(0).getAmount());

        });
    }
}
