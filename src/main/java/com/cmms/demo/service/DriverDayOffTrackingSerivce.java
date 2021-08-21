package com.cmms.demo.service;

import com.cmms.demo.domain.DayOffRequest;
import com.cmms.demo.domain.DayOffRequestStatus;
import com.cmms.demo.domain.DriverDayOffTracking;
import com.cmms.demo.domain.DriverDayOffTrackingDetailPOJO;
import com.cmms.demo.dto.DriverDayOffTrackingDTO;
import com.cmms.demo.dto.DriverDayOffTrackingDetailDTO;
import org.springframework.data.domain.Page;

import java.sql.Date;
import java.util.List;

public interface DriverDayOffTrackingSerivce {
    Page<DriverDayOffTracking> getAllRequest(int pageIndex, int pageSize, String driver_name, Long status, String start, String end);

    DriverDayOffTracking createRequest(DriverDayOffTracking tracking);

    DriverDayOffTracking updateRequest(DriverDayOffTrackingDTO tracking);

    DriverDayOffTracking findOneById(Long id);

    DriverDayOffTracking confirmRequest(DriverDayOffTracking tracking);

    DriverDayOffTracking rejectRequest(DriverDayOffTracking tracking);

    void cancelRequest(DriverDayOffTracking tracking);

    DriverDayOffTracking setStatus(DriverDayOffTracking tracking, String status_name);

    long getDuration(Date start_date, Date end_date);

    void processApprovedRequest();

    DriverDayOffTrackingDTO convertToDTO(DriverDayOffTracking tracking);

    DriverDayOffTracking convertToEntity(DriverDayOffTrackingDTO dto);

    List<DayOffRequest> getAllKindOfRequest();

    List<DayOffRequestStatus> allStatus();

    String getCurrentUser();

    List<DriverDayOffTrackingDetailPOJO> allDetailByDriverId(String code, Integer month, Integer year);

    DriverDayOffTrackingDetailPOJO createDetail(String code, DriverDayOffTrackingDetailDTO dto);

    int getTotalDayOffByDriver(String drive_code, java.util.Date start_date, java.util.Date end_date);

    List<DriverDayOffTrackingDetailPOJO> getAllDayOffByDriver(String drive_code, java.util.Date start_date, java.util.Date end_date);

    void saveAllDetail(List<DriverDayOffTrackingDetailPOJO> dayOffDetails);
}
