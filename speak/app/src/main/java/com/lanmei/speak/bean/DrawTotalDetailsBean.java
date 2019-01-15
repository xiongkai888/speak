package com.lanmei.speak.bean;

/**
 * Created by xkai on 2018/11/30.
 */

public class DrawTotalDetailsBean {

    /**
     * order_sn : 80181017121826021
     * quantity : 1
     * total_price : 500
     * create_time : 2018-10-17 12:18:26
     * goods_name : 番茄牛腩蛋包饭套餐
     * cash_back_format : 0.00
     * discount_price_format : 0.00
     * deliver_format : -
     * total_price_format : 5.00
     * prize_grade_format : -
     * purchase_status_format : -
     */

    private String order_sn;
    private int quantity;
    private double total_price;
    private String create_time;
    private String goods_name;
    private String cash_back_format;
    private String discount_price_format;
    private String deliver_format;
    private String total_price_format;
    private String prize_grade_format;
    private String purchase_status_format;

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getCash_back_format() {
        return cash_back_format;
    }

    public void setCash_back_format(String cash_back_format) {
        this.cash_back_format = cash_back_format;
    }

    public String getDiscount_price_format() {
        return discount_price_format;
    }

    public void setDiscount_price_format(String discount_price_format) {
        this.discount_price_format = discount_price_format;
    }

    public String getDeliver_format() {
        return deliver_format;
    }

    public void setDeliver_format(String deliver_format) {
        this.deliver_format = deliver_format;
    }

    public String getTotal_price_format() {
        return total_price_format;
    }

    public void setTotal_price_format(String total_price_format) {
        this.total_price_format = total_price_format;
    }

    public String getPrize_grade_format() {
        return prize_grade_format;
    }

    public void setPrize_grade_format(String prize_grade_format) {
        this.prize_grade_format = prize_grade_format;
    }

    public String getPurchase_status_format() {
        return purchase_status_format;
    }

    public void setPurchase_status_format(String purchase_status_format) {
        this.purchase_status_format = purchase_status_format;
    }
}
