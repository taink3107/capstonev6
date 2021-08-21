package com.cmms.demo.service;

import com.cmms.demo.domain.ApiItem;
import com.cmms.demo.domain.RoleApi;

import java.util.List;

public interface AuthorityService {
    List<ApiItem> getAllApiItem();

    List<RoleApi> getAllRoleApi();

    void saveRoleApi(RoleApi roleApi);
}
