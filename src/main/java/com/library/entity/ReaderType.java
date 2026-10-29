package com.library.entity;

import java.math.BigDecimal;

/**
 * 读者类型（借阅规则载体）：可借数量、借期、续借次数、滞纳金标准。
 */
public class ReaderType {

    private Long id;
    private String name;
    private Integer maxBooks;
    private Integer loanDays;
    private Integer renewLimit;
    private BigDecimal finePerDay;
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxBooks() {
        return maxBooks;
    }

    public void setMaxBooks(Integer maxBooks) {
        this.maxBooks = maxBooks;
    }

    public Integer getLoanDays() {
        return loanDays;
    }

    public void setLoanDays(Integer loanDays) {
        this.loanDays = loanDays;
    }

    public Integer getRenewLimit() {
        return renewLimit;
    }

    public void setRenewLimit(Integer renewLimit) {
        this.renewLimit = renewLimit;
    }

    public BigDecimal getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(BigDecimal finePerDay) {
        this.finePerDay = finePerDay;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
