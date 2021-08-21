package com.cmms.demo.endpoint;

import com.cmms.demo.dto.DebtReportOutput;
import com.cmms.demo.dto.DriverReportDTO;
import com.cmms.demo.dto.DriverReportOutput;
import com.cmms.demo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/report", name = "Báo cáo")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    private ReportService service;

    @GetMapping(value = "/driverReport", name = "Xem báo cáo thời gian làm của lái xe")
    // @PreAuthorize("{@appAuthorizer.authorize(authentication, '/driverReport', 'GET', this)}")
    public ResponseEntity<DriverReportOutput> getDriverReport(@RequestParam(value = "page", required = false) Integer pageIndex,
                                                              @RequestParam(value = "limit", required = false) Integer pageSize,
                                                              @RequestParam(value = "driverName", required = false) String driverName,
                                                              @RequestParam("from") String from,
                                                              @RequestParam("to") String to) {
        return new ResponseEntity<>(service.getDriverReport(pageIndex, pageSize, driverName, from, to), HttpStatus.OK);
    }

    @GetMapping(value = "/debtReport", name = "Xem báo cáo công nợ")
    // @PreAuthorize("{@appAuthorizer.authorize(authentication, '/debtReport', 'GET', this)}")
    public ResponseEntity<DebtReportOutput> getDebtReport(@RequestParam(value = "page", required = false) Integer pageIndex,
                                                          @RequestParam(value = "limit", required = false) Integer pageSize,
                                                          @RequestParam(value = "customerName", required = false) String customerName,
                                                          @RequestParam(value = "statusId", required = false) Long statusId) {
        return new ResponseEntity<>(service.getDebtReport(pageIndex, pageSize, customerName, statusId), HttpStatus.OK);
    }
}
