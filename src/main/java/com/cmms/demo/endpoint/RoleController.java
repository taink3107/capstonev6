package com.cmms.demo.endpoint;

import com.cmms.demo.domain.MyMessage;
import com.cmms.demo.domain.RolePOJO;
import com.cmms.demo.dto.RoleDTO;
import com.cmms.demo.service.RoleService;
import com.cmms.demo.serviceImpl.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/role",name = "Quản lý phân quyền")
@CrossOrigin("*")
public class RoleController {
    @Autowired
    RoleService roleService;

    @GetMapping(value = "/getOne/{id}", name = "Xem thông tin 1 quyền")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/getOne','GET', this)}")
    public ResponseEntity<RoleDTO> getOneRoleById(@PathVariable Integer id) {
        RolePOJO role = roleService.getOneById(id);
        return new ResponseEntity<>(RoleDTO.from(role), HttpStatus.OK);
    }

    @GetMapping(name = "Xem tất cả quyền")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '','GET', this)}")
    public ResponseEntity<List<RoleDTO>> getAllRole() {
        List<RolePOJO> role = roleService.findAll();
        return new ResponseEntity<>(role.stream().map(RoleDTO::from).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping(value = "/roles-by-user")
    public ResponseEntity<List<RoleDTO>> getAllRoleByUser() {
        List<RolePOJO> role = roleService.roleByUser();
        return new ResponseEntity<>(role.stream().map(RoleDTO::from).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PostMapping(value = "/update", name = "Cập nhật quyền")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/update','POST', this)}")
    public ResponseEntity<RoleDTO> updateInfoRole(@RequestBody RoleDTO dto) {
        RolePOJO role = roleService.update(dto);
        return new ResponseEntity<>(RoleDTO.from(role), HttpStatus.OK);
    }

    @PostMapping(value = "/create", name = "Tạo quyền truy cập mới")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/create','POST', this)}")
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO dto) {
        RolePOJO role = roleService.create(dto);
        return new ResponseEntity<>(RoleDTO.from(role), HttpStatus.OK);
    }

    @PostMapping(value = "/delete/{role}", name = "Xóa quyền")
    @PreAuthorize("{@appAuthorizer.authorize(authentication, '/delete','POST', this)}")
    public ResponseEntity<MyMessage> deleteRole(@PathVariable Integer role) {
        MyMessage myMessage = new MyMessage();
        if (role != null) {
            try {
                roleService.delete(role);
                myMessage.setMessage("Xóa thành công");
            } catch (Exception e) {
                myMessage.setMessage(e.getMessage());
                return new ResponseEntity<>(myMessage, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(myMessage, HttpStatus.OK);
    }
}
