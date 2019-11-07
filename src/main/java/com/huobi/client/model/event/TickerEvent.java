package com.huobi.client.model.event;

import com.huobi.client.model.Ticker;

public class TickerEvent {

    private String ch;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol( String symbol ) {
        this.symbol = symbol;
    }

    private String symbol;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }

    private long timestamp;

    public String getCh() {
        return ch;
    }

    public void setCh( String ch ) {
        this.ch = ch;
    }



    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker( Ticker ticker ) {
        this.ticker = ticker;
    }

    private Ticker ticker;

}
