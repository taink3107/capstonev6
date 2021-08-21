package com.cmms.demo.serviceImpl;

import com.cmms.demo.domain.*;
import com.cmms.demo.dto.DriverDTO;
import com.cmms.demo.dto.DriverOutput;
import com.cmms.demo.dto.ParamToUpdateDriverInfoDto;
import com.cmms.demo.dto.ProjectDTO;
import com.cmms.demo.reponsitory.*;
import com.cmms.demo.service.*;
import com.cmms.demo.specification.DriverSpecs;
import com.cmms.demo.utils.GenerateAccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverServiceImpl implements DriverService {
    private Path root = Paths.get("images");
    @Autowired
    private DriverRepository repository;
    @Autowired
    private UserStatusRepository statusRepository;
    @Autowired
    private MachineRepository machineRepository;
    @Autowired
    private PasswordEncoder bcryptEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProvinceService provinceServiceImpl;
    @Autowired
    private DistrictService districtServiceImpl;
    @Autowired
    private CommuneService communeServiceImpl;
    @Autowired
    private ScheduleDetailRepository detailRepository;
    @Autowired
    private BookingScheduleRepository bookingRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public DriverOutput filter(Integer pageIndex, Integer pageSize, String name, Long bottom, Long top, String address, boolean isActive, String phone) {
        DriverOutput output = new DriverOutput();
        if (pageIndex == null && pageSize == null) {
            List<DriverPOJO> ls = repository.findAll(Specification.where(DriverSpecs.filter(name, bottom, top, address, isActive, phone)));
            List<DriverDTO> lstDriverDto = ls.stream().map(item -> DriverDTO.from(item, null)).collect(Collectors.toList());
            output.setTotalPages(0);
            output.setDriverList(lstDriverDto);
        } else {
            Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
            Page<DriverPOJO> page = repository.findAll(Specification.where(DriverSpecs.filter(name, bottom, top, address, isActive, phone)), pageable);
            List<DriverDTO> lstDriverDto = page.stream().map(item -> DriverDTO.from(item, null)).collect(Collectors.toList());
            output.setTotalPages(page.getTotalPages());
            output.setDriverList(lstDriverDto);
        }
        return output;
    }

    @Override
    public DriverPOJO getOne(String code) {
        return repository.getOne(code);
    }

    @Override
    public DriverDTO getOneDto(String code){
        DriverPOJO d = repository.getOne(code);
        return DriverDTO.from(d, this);
    }

    public List<DriverPOJO> getAll() {
        return repository.findAll().stream().filter(pojo -> pojo.isActive()).collect(Collectors.toList());
    }

    @Override
    public DriverPOJO getByUserID(Long userId) {
        return repository.getDriverByUserId(userId);
    }

    @Override
    public String generateUsername(String name) {
        List<DriverPOJO> nameLst = repository.findAll();
        return new GenerateAccountUtils().convertToUsernameOutput(name, nameLst);
    }

    @Override
    public DriverPOJO addDriver(DriverDTO item) {
        DriverPOJO driver = new DriverPOJO();
        if (item.getDrive_code() != null) {
            driver.setDrive_code(item.getDrive_code());
        } else {
            int count = getMaxDriverCodeNumber();
            driver.setDrive_code("DR-" + (count + 1));
        }
        driver.setName(item.getName());
        driver.setDetail_address(item.getDetail_address());
        driver.setProvince(provinceServiceImpl.getOne(item.getProvince_id()));
        driver.setDistrict(districtServiceImpl.getOne(item.getDistrict_id()));
        driver.setCommune(communeServiceImpl.getOne(item.getCommune_id()));
        driver.setPhone(item.getPhone());
        driver.setBase_salary(item.getBase_salary());
        if (item.getDriver_image() != null) {
            try {
                driver.setDriver_image(item.getDriver_image());
            } catch (Exception e) {
                throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
            }
        }

        MachinePOJO machine = machineRepository.findByMachine_code(item.getMachine_code());
        driver.setMachine(machine);

        UserPOJO user = new UserPOJO();
        user.setAccount(item.getAccount());
        String password = "123456";
        user.setPassword(bcryptEncoder.encode(password));

        UserStatus status = statusRepository.findByStatusName("ACTIVE");
        user.setAccount_status(status);

        List<RolePOJO> roles = new ArrayList<>();
        RolePOJO role = roleRepository.findByRole_name("ROLE_DRIVER");
        roles.add(role);
        user.setRolePOJOSet(roles);
        driver.setUser(user);
        driver.setId_number(item.getId_number());
        if (item.getDate_of_birth() != null) {
            java.util.Date dateOfBirth = null;
            try {
                dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(item.getDate_of_birth());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            driver.setDate_of_birth(new Date(dateOfBirth.getTime()));
        }
        if (item.getGender() != null) {
            driver.setGender(Integer.parseInt(item.getGender()));
        } else {
            driver.setGender(null);
        }
        driver.setActive(true);
        String detailAddress = "";
        if (item.getDetail_address() != null && !item.getDetail_address().trim().equals("")) {
            detailAddress = item.getDetail_address() + ", ";
        }
        driver.setAddress(detailAddress + driver.getCommune().getName() +
                ", " + driver.getDistrict().getName() + ", " + driver.getProvince().getName());
        driver.setCreate_date(new Date(System.currentTimeMillis()));
        return repository.save(driver);
    }

    public int getMaxDriverCodeNumber() {
        List<DriverPOJO> ls = repository.findAll();
        List<Integer> integerLs = new ArrayList<>();
        for (int i = 0; i < ls.size(); i++) {
            String[] array = ls.get(i).getDrive_code().split("-");
            integerLs.add(Integer.parseInt(array[1].toString()));
        }
        return getMaxNumber(integerLs);
    }

    public int getMaxNumber(List<Integer> ls) {
        if (ls.size() == 0) {
            return 0;
        }
        int max = ls.get(0);
        for (int i = 1; i < ls.size(); i++) {
            if (max < ls.get(i)) {
                max = ls.get(i);
            }
        }
        return max;
    }

    @Override
    public List<DriverPOJO> getDriverListAssignMachine() {
        return repository.getDriverListAssignMachine();
    }

    @Override
    public List<MachinePOJO> getMachineListNotAssign() {
        List<MachinePOJO> machineLs = machineRepository.findAll();
        List<DriverPOJO> machineAssignedLst = getDriverListAssignMachine();
        List<MachinePOJO> outputLst = new ArrayList<>();
        for (int i = 0; i < machineLs.size(); i++) {
            String machineCode = machineLs.get(i).getMachine_code();
            if (!checkMachine(machineCode, machineAssignedLst)) {
                outputLst.add(machineLs.get(i));
            }
        }
        return outputLst;
    }


    public boolean checkMachine(String machineCode, List<DriverPOJO> ls) {
        for (int i = 0; i < ls.size(); i++) {
            if (ls.get(i).getMachine().getMachine_code().equals(machineCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DriverPOJO checkPhoneUnique(String phone) {
        List<DriverPOJO> ls = repository.findAll();
        for (int i = 0; i < ls.size(); i++) {
            DriverPOJO d = ls.get(i);
            if (d.getPhone().equals(phone)) {
                return d;
            }
        }
        return null;
    }

    @Override
    public DriverPOJO update(ParamToUpdateDriverInfoDto dto) {
        DriverPOJO d = getOne(dto.getDrive_code());
        if (d != null) {
            if (dto.getName() != null) {
                d.setName(dto.getName());
            }
            if (dto.getDetail_address() != null) {
                d.setDetail_address(dto.getDetail_address());
            }
            if (dto.getProvince_id() != null) {
                d.setProvince(provinceServiceImpl.getOne(dto.getProvince_id()));
            }
            if (dto.getDistrict_id() != null) {
                d.setDistrict(districtServiceImpl.getOne(dto.getDistrict_id()));
            }
            if (dto.getCommune_id() != null) {
                d.setCommune(communeServiceImpl.getOne(dto.getCommune_id()));
            }
            if (dto.getPhone() != null) {
                d.setPhone(dto.getPhone());
            }
            if (dto.getBase_salary() != null) {
                d.setBase_salary(dto.getBase_salary());
            }
            if (dto.getMachine_code() != null) {
                d.setMachine(machineRepository.findByMachine_code(dto.getMachine_code()));
            }
            if (dto.getDriver_image() != null) {
                try {
                    d.setDriver_image(dto.getDriver_image());
                } catch (Exception e) {
                    throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
                }
            }
            if (dto.getId_number() != null) {
                d.setId_number(dto.getId_number());
            }
            if (dto.getGender() != null) {
                d.setGender(Integer.parseInt(dto.getGender()));
            }
            if (dto.getDate_of_birth() != null) {
                java.util.Date dateOfBirth = null;
                try {
                    dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(dto.getDate_of_birth());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                d.setDate_of_birth(new Date(dateOfBirth.getTime()));
            }
            return repository.save(d);
        }
        return null;
    }

    @Override
    public DriverDTO delete(String code) {
        DriverPOJO d = getOne(code);
        DriverDTO dto = DriverDTO.from(d, null);
        if (d != null) {
            d.setActive(false);
            DriverPOJO d1 = repository.save(d);
            UserPOJO user = userService.getUserById(d.getUser().getUser_id());
            if (user != null) {
                user.setAccount_status(statusRepository.findByStatusName("INACTIVE"));
                userRepository.save(user);
            }
            LocalDate date = LocalDate.now();
            List<BookingScheduleDetail> ls = detailRepository.getByDriver(code, Date.valueOf(date));
            for (int i = 0; i < ls.size(); i++) {
                BookingScheduleDetail sd = ls.get(i);
                if (sd != null) {
                    int delete = detailRepository.deleteByDetailId(sd.getId());
                    List<BookingScheduleDetail> lsDriverByDate = detailRepository.getListByBookingId(sd.getBookingSchedule().getId());
                    if (lsDriverByDate.size() == 0) {
                        int deleteBooking = bookingRepository.deleteByBookingId(sd.getBookingSchedule().getId());
                    }
                }
            }
            if (d1 != null) {
                return dto;
            }
        }
        return null;
    }

    public List<ProjectDTO> getListProjectByDriver(String driverCode){
        List<BookingScheduleDetail> ls = detailRepository.getListProjectByDriver(driverCode);
        List<ProjectDTO> lsDto = new ArrayList<>();
        for(int i=0; i< ls.size(); i++){
            BookingScheduleDetail sd = ls.get(i);
            if(!checkProjectIsExisted(sd.getBookingSchedule().getProject().getProject_code(), lsDto)){
                lsDto.add(ProjectDTO.from(sd.getBookingSchedule().getProject()));
            }
        }
        return lsDto;
    }

    public boolean checkProjectIsExisted(String projectCode, List<ProjectDTO> ls){
        for(int i=0; i<ls.size(); i++){
            if(ls.get(i).getProject_code().equals(projectCode)){
                return true;
            }
        }
        return false;
    }
}
