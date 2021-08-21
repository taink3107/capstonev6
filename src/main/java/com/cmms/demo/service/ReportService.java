package com.cmms.demo.service;

import com.cmms.demo.dto.DebtReportOutput;
import com.cmms.demo.dto.DriverReportDTO;
import com.cmms.demo.dto.DriverReportOutput;

import java.util.List;

public interface ReportService {
    DriverReportOutput getDriverReport(Integer pageIndex, Integer pageSize, String driverName, String fromDate, String endDate);

    DebtReportOutput getDebtReport(Integer pageIndex, Integer pageSize, String customerName, Long statusId);
}
