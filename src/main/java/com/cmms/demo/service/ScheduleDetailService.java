package com.cmms.demo.service;

import com.cmms.demo.domain.BookingScheduleDetail;
import com.cmms.demo.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ScheduleDetailService {

    List<BookingScheduleDetail> assignDriver(List<RequestBodyToAssignDriverDTO> lsDto);

    BookingScheduleDetail getOne(Long id);

    List<BookingScheduleDetail> update(List<ParamToUpdateScheduleDetailDTO> lsDto);

    List<DriverDTO> getListDriverNotAssigned(String beginDate
            , String finishDate, String beginTime, String finishTime);

    List<BookingScheduleDetail> getListByBookingId(Long id);

    List<DriverDTO> getListDriverAssigned(String projectCode);

    List<ScheduleDetailByDriverDTO> getScheduleDetailByDriver(String driverCode);

    String deleteById(Long id);

    List<ListTaskOfDriverDTO> getListTaskByDriverAndDate(String driverCode);

    String updateTimeByDriver(Long scheduleDetailId, String workedHours, String image);

    int executeCompleteBtn(String projectCode, String chosenDate);

    boolean checkConfirmInProject(String projectCode);

    int checkDate(String projectCode, String driverCode, String date);

    HistoryTaskOfDriverOutput getListNeedConfirm(int pageIndex, int pageSize,
                                                 Long typeId, String driverName, String from, String to,
                                                 String customerName, String customerPhone);

    int updateWorkingStatus(Long scheduleDetailId, Long typeId);

    HistoryTaskOfDriverOutput getListHistoryTaskOfDriver(int pageIndex, int pageSize
            , String driverCode, String date, String customerName);

    boolean checkDateComplete(Long bookingId);

    boolean checkHasWorkTime(String projectCode);
}
