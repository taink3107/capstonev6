package com.cmms.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtReportDTO {
    private CustomerDTO customer;
    private String invoice_code;
    private String project_code;
    private Long total_amount;
    private Long advance_amount;
    private Long remain_amount;
    private PaymentStatusDTO payment_status;
}
