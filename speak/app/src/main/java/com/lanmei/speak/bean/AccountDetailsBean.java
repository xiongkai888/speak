package com.lanmei.speak.bean;

/**
 * @author xkai 账户流水
 */
public class AccountDetailsBean {


    /**
     * payment_date : 2018-08-05 17:30:03
     * arena_code : 711806211909002
     * order_sn : 601808101741251741
     * total_amount_str : 763.61
     * settlement_total_str : 715.71
     * create_date_str : 2018-08-05 17:30:03
     * payment_status_str : 成功
     * pay_method : wx_scanpay
     * payment_type : 微信
     * order_date : 20180805
     * push_amount : 0.00
     * arena_name : APP开发测试0719
     */

    private String payment_date;
    private String arena_code;
    private String order_sn;
    private String total_amount_str;
    private String settlement_total_str;
    private String create_date_str;
    private String payment_status_str;
    private String pay_method;
    private String payment_type;
    private int order_date;
    private String push_amount;
    private String arena_name;
    private String cashier_name = "";//收银台

    public void setCashier_name(String cashier_name) {
        this.cashier_name = cashier_name;
    }

    public String getCashier_name() {
        return cashier_name;
    }

    public String getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public String getArena_code() {
        return arena_code;
    }

    public void setArena_code(String arena_code) {
        this.arena_code = arena_code;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getTotal_amount_str() {
        return total_amount_str;
    }

    public void setTotal_amount_str(String total_amount_str) {
        this.total_amount_str = total_amount_str;
    }

    public String getSettlement_total_str() {
        return settlement_total_str;
    }

    public void setSettlement_total_str(String settlement_total_str) {
        this.settlement_total_str = settlement_total_str;
    }

    public String getCreate_date_str() {
        return create_date_str;
    }

    public void setCreate_date_str(String create_date_str) {
        this.create_date_str = create_date_str;
    }

    public String getPayment_status_str() {
        return payment_status_str;
    }

    public void setPayment_status_str(String payment_status_str) {
        this.payment_status_str = payment_status_str;
    }

    public String getPay_method() {
        return pay_method;
    }

    public void setPay_method(String pay_method) {
        this.pay_method = pay_method;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public int getOrder_date() {
        return order_date;
    }

    public void setOrder_date(int order_date) {
        this.order_date = order_date;
    }

    public String getPush_amount() {
        return push_amount;
    }

    public void setPush_amount(String push_amount) {
        this.push_amount = push_amount;
    }

    public String getArena_name() {
        return arena_name;
    }

    public void setArena_name(String arena_name) {
        this.arena_name = arena_name;
    }
}