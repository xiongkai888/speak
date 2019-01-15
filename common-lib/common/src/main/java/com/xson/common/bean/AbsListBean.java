package com.xson.common.bean;

import java.util.List;

/**
 * Created by xson on 2016/12/15.
 */

public abstract class AbsListBean<T> extends  BaseBean{

    /**
     * page : {"pageSize":10,"totalrows":103,"pageNum":11,"curPage":1,"start":0,"limit":0}
     */

    private PageBean page;

//    private SummaryBean summary;
//
//    public void setSummary(SummaryBean summary) {
//        this.summary = summary;
//    }
//
//    public SummaryBean getSummary() {
//        return summary;
//    }
//
    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public abstract List<T> getDataList();

    public static class PageBean {
        /**
         * pageSize : 10
         * totalrows : 103
         * pageNum : 11
         * curPage : 1
         * start : 0
         * limit : 0
         */

        private int pageSize;
        private int totalrows;
        private int pageNum;
        private int curPage;
        private int start;
        private int limit;

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalrows() {
            return totalrows;
        }

        public void setTotalrows(int totalrows) {
            this.totalrows = totalrows;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getCurPage() {
            return curPage;
        }

        public void setCurPage(int curPage) {
            this.curPage = curPage;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

    }

//    public static class SummaryBean {
//
//        /**
//         * settlement_total : 348276.16
//         * often_count : 25
//         * count : 715
//         * deduction_fee : 2105.82
//         * luck_draw_amount : 350.00
//         * new_customer_count : 20
//         * total_amount : 350382.00
//         * coupon_fee : 11200.00
//         * coupon_fee_no_cash : 996.00
//         * refund_amount : 16585.60
//         * total_amount_s : 332800.38
//         * refund_count : 560
//         * luck_draw_count : 10
//         */
//
//        private String settlement_total;
//        private int often_count;
//        private int count;
//        private String deduction_fee;
//        private String luck_draw_amount;
//        private int new_customer_count;
//        private String total_amount;
//        private String coupon_fee;
//        private String coupon_fee_no_cash;
//        private String refund_amount;
//        private String total_amount_s;
//        private int refund_count;
//        private int luck_draw_count;
//
//        public String getSettlement_total() {
//            return settlement_total;
//        }
//
//        public void setSettlement_total(String settlement_total) {
//            this.settlement_total = settlement_total;
//        }
//
//        public int getOften_count() {
//            return often_count;
//        }
//
//        public void setOften_count(int often_count) {
//            this.often_count = often_count;
//        }
//
//        public int getCount() {
//            return count;
//        }
//
//        public void setCount(int count) {
//            this.count = count;
//        }
//
//        public String getDeduction_fee() {
//            return deduction_fee;
//        }
//
//        public void setDeduction_fee(String deduction_fee) {
//            this.deduction_fee = deduction_fee;
//        }
//
//        public String getLuck_draw_amount() {
//            return luck_draw_amount;
//        }
//
//        public void setLuck_draw_amount(String luck_draw_amount) {
//            this.luck_draw_amount = luck_draw_amount;
//        }
//
//        public int getNew_customer_count() {
//            return new_customer_count;
//        }
//
//        public void setNew_customer_count(int new_customer_count) {
//            this.new_customer_count = new_customer_count;
//        }
//
//        public String getTotal_amount() {
//            return total_amount;
//        }
//
//        public void setTotal_amount(String total_amount) {
//            this.total_amount = total_amount;
//        }
//
//        public String getCoupon_fee() {
//            return coupon_fee;
//        }
//
//        public void setCoupon_fee(String coupon_fee) {
//            this.coupon_fee = coupon_fee;
//        }
//
//        public String getCoupon_fee_no_cash() {
//            return coupon_fee_no_cash;
//        }
//
//        public void setCoupon_fee_no_cash(String coupon_fee_no_cash) {
//            this.coupon_fee_no_cash = coupon_fee_no_cash;
//        }
//
//        public String getRefund_amount() {
//            return refund_amount;
//        }
//
//        public void setRefund_amount(String refund_amount) {
//            this.refund_amount = refund_amount;
//        }
//
//        public String getTotal_amount_s() {
//            return total_amount_s;
//        }
//
//        public void setTotal_amount_s(String total_amount_s) {
//            this.total_amount_s = total_amount_s;
//        }
//
//        public int getRefund_count() {
//            return refund_count;
//        }
//
//        public void setRefund_count(int refund_count) {
//            this.refund_count = refund_count;
//        }
//
//        public int getLuck_draw_count() {
//            return luck_draw_count;
//        }
//
//        public void setLuck_draw_count(int luck_draw_count) {
//            this.luck_draw_count = luck_draw_count;
//        }
//    }
}
