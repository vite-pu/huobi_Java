package com.huobi.client.model;

import java.math.BigDecimal;

public class Ticker {
    @Override
    public String toString() {
        return "Ticker{" +
                "symbol='" + symbol + '\'' +
                ", timestamp=" + timestamp +
                ", ask=" + ask +
                ", askSize=" + askSize +
                ", bid=" + bid +
                ", bidSize=" + bidSize +
                ", seqId=" + seqId +
                '}';
    }

    private String symbol;
    private long timestamp;
    private BigDecimal ask;
    private BigDecimal askSize;
    private BigDecimal bid;
    private BigDecimal bidSize;


    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk( BigDecimal ask ) {
        this.ask = ask;
    }

    public BigDecimal getAskSize() {
        return askSize;
    }

    public void setAskSize( BigDecimal askSize ) {
        this.askSize = askSize;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid( BigDecimal bid ) {
        this.bid = bid;
    }

    public BigDecimal getBidSize() {
        return bidSize;
    }

    public void setBidSize( BigDecimal bidSize ) {
        this.bidSize = bidSize;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol( String symbol ) {
        this.symbol = symbol;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }

    public long getSeqId() {
        return seqId;
    }

    public void setSeqId( long seqId ) {
        this.seqId = seqId;
    }

    private long seqId;

}