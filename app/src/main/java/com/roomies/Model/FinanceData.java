package com.roomies.Model;

public class FinanceData {

    String billTypeName;
    String sum;
    String from;
    String to;
    String id;

    public FinanceData() {

    }

    public FinanceData(String billTypeName, String sum, String from, String to, String id) {
        this.billTypeName = billTypeName;
        this.sum = sum;
        this.from = from;
        this.to = to;
        this.id = id;
    }

    public String getBillTypeName() {
        return billTypeName;
    }

    public String getSum() {
        return sum;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getId() {
        return id;
    }

    public void setBillTypeName(String billTypeName) {
        this.billTypeName = billTypeName;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setId(String id) {
        this.id = id;
    }
}
