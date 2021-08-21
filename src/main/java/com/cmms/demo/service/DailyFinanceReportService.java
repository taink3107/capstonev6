package com.cmms.demo.service;

import com.cmms.demo.domain.DailyFinanceReport;
import com.cmms.demo.dto.DailyFinanceReportDto;
import com.cmms.demo.dto.DailyFinanceReportOutput;

import java.util.Date;
import java.util.List;

public interface DailyFinanceReportService {
    DailyFinanceReportDto addDailyReport(DailyFinanceReportDto dto);

    DailyFinanceReportOutput filter(Integer pageIndex, Integer pageSize, Integer paymentType, String fromDate, String toDate, String title);

    DailyFinanceReportDto getOne(Long id);

    DailyFinanceReportDto updateDailyFinanceRp(DailyFinanceReportDto item);

    String deleteDailyFinanceRp(Long id);
    List<DailyFinanceReport> getReportByStaff(String staff_name, Date date);

    void saveAll(List<DailyFinanceReport> financeReports);

    void save(DailyFinanceReport report);
}
