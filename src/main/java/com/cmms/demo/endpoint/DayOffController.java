package com.cmms.demo.endpoint;

import com.cmms.demo.domain.*;
import com.cmms.demo.dto.DayOffRequestDTO;
import com.cmms.demo.dto.DriverDayOffTrackingDTO;
import com.cmms.demo.dto.DriverDayOffTrackingDetailDTO;
import com.cmms.demo.dto.RequestDayOffStatusDTO;
import com.cmms.demo.service.DriverDayOffTrackingSerivce;
import com.cmms.demo.serviceImpl.DriverDayOffTrackingSerivceImpl;
import com.cmms.demo.serviceImpl.ScheduleDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/dayoff",name = "Quản lý đơn nghỉ")
@CrossOrigin("*")
public class DayOffController {


    @Autowired
    DriverDayOffTrackingSerivce trackingSerivce;

    @Autowired
    private ScheduleDetailServiceImpl detailService;

    @GetMapping(value = "/your-requests",name = "Đơn nghỉ phía nhân viên")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/your-requests','GET', this)}")
    public ResponseEntity<DayOffOutput> allYourRequest(@RequestParam("page") int pageIndex,
                                                       @RequestParam("limit") int pageSize,
                                                       @RequestParam(value = "status", required = false) Long status,
                                                       @RequestParam(value = "name", required = false) String driver_name,
                                                       @RequestParam(value = "start", required = false) String start,
                                                       @RequestParam(value = "end", required = false) String end) {
        Page<DriverDayOffTracking> list = trackingSerivce.getAllRequest(pageIndex, pageSize, driver_name, status, start, end);
        List<DriverDayOffTrackingDTO> dtos = list.stream().map(DriverDayOffTrackingDTO::from).collect(Collectors.toList());
        String currentUser = trackingSerivce.getCurrentUser();
        DayOffOutput output = new DayOffOutput();
        output.setTotalPages(list.getTotalPages());
        output.setDriverList(dtos.stream().filter(driverDayOffTrackingDTO -> driverDayOffTrackingDTO.getDriver_id().getAccount().equals(currentUser)).collect(Collectors.toList()));
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @GetMapping("/request-dayoff")
    public ResponseEntity<List<DayOffRequestDTO>> listKindOfReqDayOff() {
        List<DayOffRequest> requests = trackingSerivce.getAllKindOfRequest();
        return new ResponseEntity<>(requests.stream().map(DayOffRequestDTO::from).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PostMapping(value = "/getOne/{id}",name = "Thông tin 1 đơn nghỉ")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/getOne','POST', this)}")
    public ResponseEntity<DriverDayOffTrackingDTO> getOne(@PathVariable Long id) {
        DriverDayOffTracking tracking = trackingSerivce.findOneById(id);
        return new ResponseEntity<>(DriverDayOffTrackingDTO.from(tracking), HttpStatus.OK);
    }

    @GetMapping(value = "/all",name = "Xem toàn bộ đơn nghỉ")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/all','GET', this)}")
    public ResponseEntity<DayOffOutput> allRequest(@RequestParam("page") int pageIndex,
                                                   @RequestParam("limit") int pageSize,
                                                   @RequestParam(value = "status", required = false) Long status,
                                                   @RequestParam(value = "name", required = false) String driver_name,
                                                   @RequestParam(value = "start", required = false) String start,
                                                   @RequestParam(value = "end", required = false) String end) {
        Page<DriverDayOffTracking> list = trackingSerivce.getAllRequest(pageIndex, pageSize, driver_name, status, start, end);
        List<DriverDayOffTrackingDTO> dtos = list.stream().map(DriverDayOffTrackingDTO::from).collect(Collectors.toList());
        DayOffOutput output = new DayOffOutput();
        output.setTotalPages(list.getTotalPages());
        output.setDriverList(dtos);
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @GetMapping("/all-status")
    public ResponseEntity<List<RequestDayOffStatusDTO>> allStatus() {
        return new ResponseEntity<>(trackingSerivce.allStatus().stream().map(RequestDayOffStatusDTO::from).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PostMapping(value = "/create",name = "Tạo đơn nghỉ")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/create','POST', this)}")
    //@Secured({"ROLE_DRIVER"})
    public ResponseEntity<DriverDayOffTrackingDTO> createRequest(@RequestBody DriverDayOffTrackingDTO requestDTO) {
        DriverDayOffTracking tracking = trackingSerivce.convertToEntity(requestDTO);
        return new ResponseEntity<>(trackingSerivce.convertToDTO(trackingSerivce.createRequest(tracking)), HttpStatus.OK);
    }

    @PostMapping(value = "/update/{id}",name = "Cập nhật đơn nghỉ")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/update','POST', this)}")
//    @Secured({"ROLE_DRIVER"})
    public ResponseEntity<DriverDayOffTrackingDTO> updateRequest(@PathVariable Long id, @RequestBody DriverDayOffTrackingDTO dto) {
        dto.setId(id);
        DriverDayOffTracking offTracking = trackingSerivce.updateRequest(dto);
        if (offTracking.getId() != null) {
            return new ResponseEntity<>(trackingSerivce.convertToDTO(offTracking), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/confirm/{id}",name = "Xác nhận đơn nghỉ")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/confirm','POST', this)}")
    //@Secured({"ROLE_OWNER", "ROLE_ADMIN"})
    public ResponseEntity<DriverDayOffTrackingDTO> confirmRequest(@PathVariable Long id) {
        DriverDayOffTracking tracking = trackingSerivce.findOneById(id);
        int deleteBooking = detailService.deleteScheduleByDayOff(tracking.getDriver_code().getDrive_code()
                , tracking.getStart_date(), tracking.getEnd_date());
        if (tracking.getId() != null) {
            return new ResponseEntity<>(trackingSerivce.convertToDTO(trackingSerivce.confirmRequest(tracking)), HttpStatus.OK);

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @GetMapping(value = "/reject/{id}",name = "Từ chối đơn nghỉ")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/reject','POST', this)}")
    //@Secured({"ROLE_OWNER", "ROLE_ADMIN"})
    public ResponseEntity<DriverDayOffTrackingDTO> rejectRequest(@PathVariable Long id) {
        DriverDayOffTracking tracking = trackingSerivce.findOneById(id);
        if (tracking.getId() != null) {
            return new ResponseEntity<>(trackingSerivce.convertToDTO(trackingSerivce.rejectRequest(tracking)), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/get-detail-by-driver/{code}")
    public ResponseEntity<List<DriverDayOffTrackingDetailDTO>> detailByDriverId(@PathVariable String code, @RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year) {
        LocalDate localDate = LocalDate.now();
        List<DriverDayOffTrackingDetailPOJO> detail = trackingSerivce.allDetailByDriverId(code, Optional.ofNullable(month).orElse(localDate.getMonthValue()), Optional.ofNullable(localDate.getYear()).orElse(2021));
        return new ResponseEntity<>(detail.stream().map(detailPOJO -> DriverDayOffTrackingDetailDTO.from(detailPOJO)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PostMapping(value = "/add-dayoff-by-admin/{code}",name = "Chủ quản thêm ngày nghỉ.")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/add-dayoff-by-admin','POST', this)}")
    public ResponseEntity<DriverDayOffTrackingDetailDTO> addDayOffByAdmin(@PathVariable String code, @RequestBody DriverDayOffTrackingDetailDTO dto) {
        DriverDayOffTrackingDetailPOJO detail = trackingSerivce.createDetail(code, dto);
        return new ResponseEntity<>(DriverDayOffTrackingDetailDTO.from(detail), HttpStatus.OK);
    }

    @GetMapping(value = "/cancel/{id}")
    public ResponseEntity<String> cancelRequest(@PathVariable Long id) {
        DriverDayOffTracking tracking = trackingSerivce.findOneById(id);
        if (tracking.getId() != null) {
            trackingSerivce.cancelRequest(tracking);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        return new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND);
    }

}
