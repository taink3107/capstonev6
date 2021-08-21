package com.cmms.demo.service;

import com.cmms.demo.domain.RolePOJO;
import com.cmms.demo.domain.UserPOJO;
import com.cmms.demo.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    UserPOJO getUserById(Long id);

    List<RolePOJO> getAllRByUserName(String name);

    RolePOJO findOneRoleById(int id);

    UserPOJO save(UserDTO user);

    List<UserPOJO> findAll();

    Page<UserPOJO> getList(int pageIndex, int pageSize , String account, Long roleId, Long statusId);

    boolean updateOneUser(UserDTO userDTO);

    boolean changePassword(String oldPassword, String newPassword);

    String currentAccount();

    UserPOJO getUserByName(String currentUserName);

    boolean resetPassword(Long id);

    boolean changeStatusUser(Long id, String statusName);

    boolean isStaff(UserPOJO userPOJO);

    boolean isAdmin(UserPOJO userPOJO);
}
