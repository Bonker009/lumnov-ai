package com.renthouse.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeReportDto {
    private LocalDate period;
    private BigDecimal totalIncome;
    private String periodType; // MONTHLY or YEARLY

    public IncomeReportDto() {}

    public IncomeReportDto(LocalDate period, BigDecimal totalIncome, String periodType) {
        this.period = period;
        this.totalIncome = totalIncome;
        this.periodType = periodType;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }
}