package com.cmms.demo.serviceImpl;

import com.cmms.demo.domain.ApiItem;
import com.cmms.demo.domain.RoleApi;
import com.cmms.demo.domain.RolePOJO;
import com.cmms.demo.domain.UserPOJO;
import com.cmms.demo.dto.RoleDTO;
import com.cmms.demo.reponsitory.RoleApiRepository;
import com.cmms.demo.reponsitory.RoleRepository;
import com.cmms.demo.service.AuthorityService;
import com.cmms.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    AuthorityService service;
    @Autowired
    RoleRepository repository;
    @Autowired
    RoleApiRepository roleApiRepository;

    @Override
    public RolePOJO getOneById(Integer id) {
        return repository.getById(id);
    }

    @Autowired
    UserServiceImpl userService;

    public List<RolePOJO> getAll(Long id) {
        return repository.findAll();
    }

    @Override
    public List<RolePOJO> findAll() {
        return repository.findAll();
    }

    @Override
    public RolePOJO update(RoleDTO dto) {
        RolePOJO role = getOneById(dto.getRole_id());
        List<Long> isSelected = dto.getFunctions().stream().filter(roleApiDTO -> roleApiDTO.isAllow()).map(roleApiDTO -> roleApiDTO.getId()).collect(Collectors.toList());
        List<RoleApi> roleApis = getRoleApis(role.getRole_id());
        roleApis.forEach(api -> api.setAllow(false));

        for (RoleApi roleApi : roleApis) {
            if (isSelected.contains(roleApi.getId())) {
                roleApi.setAllow(true);
            }
        }
        role.setRoleApis(roleApis);
        role.setRole_name(dto.getRole_name());
        role.setDescription(dto.getDescription());
        role.setRole_id(dto.getRole_id());
        return repository.save(role);
    }

    @Override
    public List<RoleApi> getRoleApis(Integer id) {
        return roleApiRepository.findAllByRoleId(id);
    }

    @Override
    public RoleApi getOneByKeyName(String role, String item, String api, String method) {
        return roleApiRepository.findRoleApi(role, api, item, method);
    }

    @Override
    public RolePOJO create(RoleDTO dto) {
        RolePOJO pojo = new RolePOJO();
        pojo.setDescription(dto.getDescription());
        pojo.setName(dto.getName());
        pojo.setRole_name("ROLE_" + getTextBegin(dto.getName()));
        List<ApiItem> items = service.getAllApiItem();
        RolePOJO pojo1 = repository.save(pojo);
        createRoleApi(items, pojo1);
        return pojo1;
    }

    private String getTextBegin(String text) {
        String[] arrays = text.split(" ");
        final String[] result = {""};
        Arrays.stream(arrays).forEach(s -> result[0] += s.charAt(0));
        return result[0];
    }

    private void createRoleApi(List<ApiItem> items, RolePOJO role) {

        List<RoleApi> roleApis = service.getAllRoleApi();
        for (ApiItem item : items) {
            RoleApi roleApi = new RoleApi();
            roleApi.setApiItem(item);
            roleApi.setRole(role);
            RoleApi x = roleApis.stream().filter(userApi1 -> userApi1.getApiItem().getApi().getUrl().equals(roleApi.getApiItem().getApi().getUrl()) && userApi1.getApiItem().getUrl().equals(roleApi.getApiItem().getUrl()) && userApi1.getRole().getRole_name().equals(roleApi.getRole().getRole_name())).findFirst().orElse(null);
            if (x == null) {
                service.saveRoleApi(roleApi);
            }
        }


    }

    @Override
    public List<RolePOJO> roleByUser() {
        UserPOJO pojo = userService.getUserByName(userService.currentAccount());
        List<RolePOJO> role = repository.findAll();
        if (userService.isAdmin(pojo)) {
            return role.stream().filter(roleX -> roleX.getRole_id() == 2).collect(Collectors.toList());
        }
        return role.stream().filter(roleX -> roleX.getRole_id() != 2 && roleX.getRole_id() != 1).collect(Collectors.toList());
    }

    @Override
    public void delete(Integer integer) {
        RolePOJO pojo = repository.getById(integer);
        if (pojo != null) {
            repository.delete(pojo);
        }
    }
}
