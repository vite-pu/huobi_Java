package com.huobi.client.impl;

import static com.huobi.client.impl.utils.InternalUtils.await;

import com.huobi.client.SubscriptionErrorHandler;
import com.huobi.client.SubscriptionListener;
import com.huobi.client.impl.utils.Channels;
import com.huobi.client.impl.utils.JsonWrapper;
import com.huobi.client.impl.utils.JsonWrapperArray;
import com.huobi.client.impl.utils.TimeService;
import com.huobi.client.model.*;
import com.huobi.client.model.enums.AccountChangeType;
import com.huobi.client.model.enums.BalanceMode;
import com.huobi.client.model.enums.BalanceType;
import com.huobi.client.model.enums.CandlestickInterval;
import com.huobi.client.model.enums.DealRole;
import com.huobi.client.model.enums.OrderSource;
import com.huobi.client.model.enums.OrderState;
import com.huobi.client.model.enums.OrderType;
import com.huobi.client.model.enums.TradeDirection;
import com.huobi.client.model.event.*;

import java.util.LinkedList;
import java.util.List;

class WebsocketRequestImpl {

  private final String apiKey;

  WebsocketRequestImpl(String apiKey) {
    this.apiKey = apiKey;
  }

  WebsocketRequest<CandlestickEvent> subscribeCandlestickEvent(
      List<String> symbols,
      CandlestickInterval interval,
      SubscriptionListener<CandlestickEvent> subscriptionListener,
      SubscriptionErrorHandler errorHandler) {
    InputChecker.checker()
        .checkSymbolList(symbols)
        .shouldNotNull(subscriptionListener, "listener")
        .shouldNotNull(interval, "CandlestickInterval");
    WebsocketRequest<CandlestickEvent> request =
        new WebsocketRequest<>(subscriptionListener, errorHandler);
    if (symbols.size() == 1) {
      request.name = "Candlestick for " + symbols;
    } else {
      request.name = "Candlestick for " + symbols + " ...";
    }
    request.connectionHandler = (connection) ->
        symbols.stream()
            .map((symbol) -> Channels.klineChannel(symbol, interval))
            .forEach(req -> {
              connection.send(req);
              await(1);
            });
    request.jsonParser = (jsonWrapper) -> {
      String ch = jsonWrapper.getString("ch");
      ChannelParser parser = new ChannelParser(ch);
      CandlestickEvent candlestickEvent = new CandlestickEvent();
      candlestickEvent.setSymbol(parser.getSymbol());
      candlestickEvent.setInterval(interval);
      candlestickEvent.setTimestamp(
          TimeService.convertCSTInMillisecondToUTC(jsonWrapper.getLong("ts")));
      JsonWrapper tick = jsonWrapper.getJsonObject("tick");
      Candlestick data = new Candlestick();
      data.setTimestamp(TimeService.convertCSTInSecondToUTC(tick.getLong("id")));
      data.setOpen(tick.getBigDecimal("open"));
      data.setClose(tick.getBigDecimal("close"));
      data.setLow(tick.getBigDecimal("low"));
      data.setHigh(tick.getBigDecimal("high"));
      data.setAmount(tick.getBigDecimal("amount"));
      data.setCount(tick.getLong("count"));
      data.setVolume(tick.getBigDecimal("vol"));
      candlestickEvent.setData(data);
      return candlestickEvent;
    };
    return request;
  }

  WebsocketRequest<TradeEvent> subscribeTradeEvent(
      List<String> symbols,
      SubscriptionListener<TradeEvent> subscriptionListener,
      SubscriptionErrorHandler errorHandler) {
    InputChecker.checker().checkSymbolList(symbols).shouldNotNull(subscriptionListener, "listener");
    WebsocketRequest<TradeEvent> request =
        new WebsocketRequest<>(subscriptionListener, errorHandler);
    if (symbols.size() == 1) {
      request.name = "Trade for " + symbols;
    } else {
      request.name = "Trade for " + symbols + " ...";
    }
    request.connectionHandler = (connection) ->
        symbols.stream()
            .map(Channels::tradeChannel)
            .forEach(req -> {
              connection.send(req);
              await(1);
            });
    request.jsonParser = (jsonWrapper) -> {
      String ch = jsonWrapper.getString("ch");
      ChannelParser parser = new ChannelParser(ch);
      TradeEvent tradeEvent = new TradeEvent();
      tradeEvent.setSymbol(parser.getSymbol());
      tradeEvent.setTimestamp(TimeService.convertCSTInMillisecondToUTC(jsonWrapper.getLong("ts")));
      JsonWrapper tick = jsonWrapper.getJsonObject("tick");
      JsonWrapperArray dataArray = tick.getJsonArray("data");
      List<Trade> trades = new LinkedList<>();
      dataArray.forEach((item) -> {
        Trade trade = new Trade();
        trade.setAmount(item.getBigDecimal("amount"));
        trade.setPrice(item.getBigDecimal("price"));
        trade.setTradeId(item.getString("id"));
        trade.setDirection(TradeDirection.lookup(item.getString("direction")));
        trade.setTimestamp(TimeService.convertCSTInMillisecondToUTC(item.getLong("ts")));
        trades.add(trade);
      });
      tradeEvent.setTradeList(trades);
      return tradeEvent;
    };
    return request;
  }

  WebsocketRequest<PriceDepthEvent> subscribePriceDepthEvent(
      List<String> symbols,
      SubscriptionListener<PriceDepthEvent> subscriptionListener,
      SubscriptionErrorHandler errorHandler) {
    InputChecker.checker().checkSymbolList(symbols).shouldNotNull(subscriptionListener, "listener");
    WebsocketRequest<PriceDepthEvent> request =
        new WebsocketRequest<>(subscriptionListener, errorHandler);
    if (symbols.size() == 1) {
      request.name = "PriceDepth for " + symbols;
    } else {
      request.name = "PriceDepth for " + symbols + " ...";
    }
    request.connectionHandler = (connection) ->
        symbols.stream()
            .map(Channels::priceDepthChannel)
            .forEach(req -> {
              connection.send(req);
              await(1);
            });
    request.jsonParser = (jsonWrapper) -> {
      String ch = jsonWrapper.getString("ch");
      ChannelParser parser = new ChannelParser(ch);
      PriceDepthEvent priceDepthEvent = new PriceDepthEvent();
      priceDepthEvent.setTimestamp(
          TimeService.convertCSTInMillisecondToUTC(jsonWrapper.getLong("ts")));
      priceDepthEvent.setSymbol(parser.getSymbol());
      PriceDepth priceDepth = new PriceDepth();
      JsonWrapper tick = jsonWrapper.getJsonObject("tick");
      priceDepth.setTimestamp(TimeService.convertCSTInMillisecondToUTC(tick.getLong("ts")));
      List<DepthEntry> bidList = new LinkedList<>();
      JsonWrapperArray bids = tick.getJsonArray("bids");
      bids.forEachAsArray((item) -> {
        DepthEntry depthEntry = new DepthEntry();
        depthEntry.setPrice(item.getBigDecimalAt(0));
        depthEntry.setAmount(item.getBigDecimalAt(1));
        bidList.add(depthEntry);
      });
      List<DepthEntry> askList = new LinkedList<>();
      JsonWrapperArray asks = tick.getJsonArray("asks");
      asks.forEachAsArray((item) -> {
        DepthEntry depthEntry = new DepthEntry();
        depthEntry.setPrice(item.getBigDecimalAt(0));
        depthEntry.setAmount(item.getBigDecimalAt(1));
        askList.add(depthEntry);
      });
      priceDepth.setAsks(askList);
      priceDepth.setBids(bidList);
      priceDepthEvent.setData(priceDepth);
      return priceDepthEvent;
    };
    return request;
  }

  WebsocketRequest<TickerEvent> subscribeTickerEvent(
          List<String> symbols,
          SubscriptionListener<TickerEvent> subscriptionListener,
          SubscriptionErrorHandler errorHandler) {
    InputChecker.checker().checkSymbolList(symbols).shouldNotNull(subscriptionListener, "listener");
    WebsocketRequest<TickerEvent> request =
            new WebsocketRequest<>(subscriptionListener, errorHandler);
    if (symbols.size() == 1) {
      request.name = "Ticker for " + symbols;
    } else {
      request.name = "Ticker for " + symbols + " ...";
    }
    request.connectionHandler = (connection) ->
            symbols.stream()
                    .map(Channels::tickerChannel)
                    .forEach(req -> {
                      connection.send(req);
                      await(1);
                    });
    request.jsonParser = (jsonWrapper) -> {
      String ch = jsonWrapper.getString("ch");
      ChannelParser parser = new ChannelParser(ch);
      TickerEvent tickerEvent = new TickerEvent();
      tickerEvent.setTimestamp(
              TimeService.convertCSTInMillisecondToUTC(jsonWrapper.getLong("ts")));
      tickerEvent.setSymbol(parser.getSymbol());
      Ticker ticker = new Ticker();
      JsonWrapper tick = jsonWrapper.getJsonObject("tick");
      ticker.setTimestamp(TimeService.convertCSTInMillisecondToUTC(tick.getLong("quoteTime")));
      ticker.setAsk(tick.getBigDecimal("ask"));
      ticker.setAskSize(tick.getBigDecimal("askSize"));
      ticker.setBid(tick.getBigDecimal("bid"));
      ticker.setBidSize(tick.getBigDecimal("bidSize"));
      ticker.setSeqId(tick.getLong("seqId"));
      ticker.setSymbol(tick.getString("symbol"));

      tickerEvent.setTicker(ticker);

      return tickerEvent;
    };
    return request;
  }

  WebsocketRequest<OrderUpdateEvent> subscribeOrderUpdateEvent(
      List<String> symbols,
      SubscriptionListener<OrderUpdateEvent> subscriptionListener,
      SubscriptionErrorHandler errorHandler) {
    InputChecker.checker().checkSymbolList(symbols).shouldNotNull(subscriptionListener, "listener");
    WebsocketRequest<OrderUpdateEvent> request =
        new WebsocketRequest<>(subscriptionListener, errorHandler);
    if (symbols.size() == 1) {
      request.name = "OrderUpdate for " + symbols;
    } else {
      request.name = "OrderUpdate for " + symbols + " ...";
    }
    request.authHandler = (connection) ->
        symbols.stream()
            .map(Channels::ordersChannel)
            .forEach(req -> {
              connection.send(req);
              await(1);
            });
    request.jsonParser = (jsonWrapper) -> {
      String ch = jsonWrapper.getString("topic");
      ChannelParser parser = new ChannelParser(ch);
      OrderUpdateEvent orderUpdateEvent = new OrderUpdateEvent();
      orderUpdateEvent.setSymbol(parser.getSymbol());
      orderUpdateEvent.setTimestamp(
          TimeService.convertCSTInMillisecondToUTC(jsonWrapper.getLong("ts")));
      JsonWrapper data = jsonWrapper.getJsonObject("data");
      Order order = new Order();
      order.setOrderId(data.getLong("order-id"));
      order.setSymbol(parser.getSymbol());
      order
          .setAccountType(AccountsInfoMap.getAccount(apiKey, data.getLong("account-id")).getType());
      order.setAmount(data.getBigDecimal("order-amount"));
      order.setPrice(data.getBigDecimal("order-price"));
      order.setCreatedTimestamp(
          TimeService.convertCSTInMillisecondToUTC(data.getLong("created-at")));
      order.setType(OrderType.lookup(data.getString("order-type")));
      order.setFilledAmount(data.getBigDecimal("filled-amount"));
      order.setFilledCashAmount(data.getBigDecimal("filled-cash-amount"));
      order.setFilledFees(data.getBigDecimal("filled-fees"));
      order.setState(OrderState.lookup(data.getString("order-state")));
      order.setSource(OrderSource.lookup(data.getString("order-source")));
      orderUpdateEvent.setData(order);
      return orderUpdateEvent;
    };
    return request;
  }

  WebsocketRequest<OrderUpdateNewEvent> subscribeOrderUpdateNewEvent(
      List<String> symbols,
      SubscriptionListener<OrderUpdateNewEvent> subscriptionListener,
      SubscriptionErrorHandler errorHandler) {
    InputChecker.checker().checkSymbolList(symbols).shouldNotNull(subscriptionListener, "listener");
    WebsocketRequest<OrderUpdateNewEvent> request =
        new WebsocketRequest<>(subscriptionListener, errorHandler);
    if (symbols.size() == 1) {
      request.name = "OrderUpdateNew for " + symbols;
    } else {
      request.name = "OrderUpdateNew for " + symbols + " ...";
    }
    request.authHandler = (connection) ->
        symbols.stream()
            .map(Channels::ordersChannelNew)
            .forEach(req -> {
              connection.send(req);
              await(1);
            });
    request.jsonParser = (jsonWrapper) -> {
      OrderUpdateNewEvent orderUpdateEvent = new OrderUpdateNewEvent();
      JsonWrapper data = jsonWrapper.getJsonObject("data");
      String symbol = data.getString("symbol");
      orderUpdateEvent.setSymbol(symbol);
      orderUpdateEvent.setTimestamp(TimeService.convertCSTInMillisecondToUTC(jsonWrapper.getLong("ts")));

      orderUpdateEvent.setData(OrderUpdate.builder()
          .matchId(data.getLong("match-id"))
          .orderId(data.getLong("order-id"))
          .symbol(symbol)
          .state(OrderState.lookup(data.getString("order-state")))
          .type(OrderType.lookup(data.getString("order-type")))
          .role(DealRole.find(data.getString("role")))
          .price(data.getBigDecimal("price"))
          .filledAmount(data.getBigDecimal("filled-amount"))
          .filledCashAmount(data.getBigDecimal("filled-cash-amount"))
          .unfilledAmount(data.getBigDecimal("unfilled-amount"))
          .clientOrderId(data.getStringOrDefault("client-order-id", null))
          .build());
      return orderUpdateEvent;
    };
    return request;
  }

  WebsocketRequest<AccountEvent> subscribeAccountEvent(
      BalanceMode mode,
      SubscriptionListener<AccountEvent> subscriptionListener,
      SubscriptionErrorHandler errorHandler) {
    InputChecker.checker().shouldNotNull(subscriptionListener, "listener");
    WebsocketRequest<AccountEvent> request =
        new WebsocketRequest<>(subscriptionListener, errorHandler);
    request.name = "Account";
    request.authHandler = (connection) ->
        connection.send(Channels.accountChannel(mode));
    request.jsonParser = (jsonWrapper) -> {
      AccountEvent accountEvent = new AccountEvent();
      accountEvent.setTimestamp(
          TimeService.convertCSTInMillisecondToUTC(jsonWrapper.getLong("ts")));
      JsonWrapper data = jsonWrapper.getJsonObject("data");
      accountEvent.setChangeType(AccountChangeType.lookup(data.getString("event")));
      JsonWrapperArray listArray = data.getJsonArray("list");
      List<AccountChange> accountChangeList = new LinkedList<>();
      listArray.forEach((itemInList) -> {
        AccountChange change = new AccountChange();
        change.setAccountType(
            AccountsInfoMap.getAccount(apiKey, itemInList.getLong("account-id")).getType());
        change.setCurrency(itemInList.getString("currency"));
        change.setBalance(itemInList.getBigDecimal("balance"));
        change.setBalanceType(BalanceType.lookup(itemInList.getString("type")));
        accountChangeList.add(change);
      });
      accountEvent.setData(accountChangeList);
      return accountEvent;
    };
    return request;
  }

  WebsocketRequest<TradeStatisticsEvent> subscribe24HTradeStatisticsEvent(
      List<String> symbols,
      SubscriptionListener<TradeStatisticsEvent> subscriptionListener,
      SubscriptionErrorHandler errorHandler) {
    InputChecker.checker().checkSymbolList(symbols).shouldNotNull(subscriptionListener, "listener");
    WebsocketRequest<TradeStatisticsEvent> request =
        new WebsocketRequest<>(subscriptionListener, errorHandler);
    if (symbols.size() == 1) {
      request.name = "24HTradeStatistics for " + symbols;
    } else {
      request.name = "24HTradeStatistics for " + symbols + " ...";
    }
    request.connectionHandler = (connection) -> {
      for (String symbol : symbols) {
        symbol = Channels.tradeStatisticsChannel(symbol);
        connection.send(symbol);
      }
    };
    request.jsonParser = (jsonWrapper) -> {
      String ch = jsonWrapper.getString("ch");
      ChannelParser parser = new ChannelParser(ch);
      TradeStatisticsEvent tradeStatisticsEvent = new TradeStatisticsEvent();
      tradeStatisticsEvent.setSymbol(parser.getSymbol());
      JsonWrapper tick = jsonWrapper.getJsonObject("tick");
      long ts = TimeService.convertCSTInMillisecondToUTC(jsonWrapper.getLong("ts"));
      tradeStatisticsEvent.setTimeStamp(ts);
      TradeStatistics statistics = new TradeStatistics();
      statistics.setAmount(tick.getBigDecimal("amount"));
      statistics.setOpen(tick.getBigDecimal("open"));
      statistics.setClose(tick.getBigDecimal("close"));
      statistics.setHigh(tick.getBigDecimal("high"));
      statistics.setTimestamp(ts);
      //statistics.setId(tick.getLong("id"));
      statistics.setCount(tick.getLong("count"));
      statistics.setLow(tick.getBigDecimal("low"));
      statistics.setVolume(tick.getBigDecimal("vol"));
      tradeStatisticsEvent.setData(statistics);
      return tradeStatisticsEvent;
    };
    return request;
  }
}
