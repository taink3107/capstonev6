package com.cmms.demo.dto;

import com.cmms.demo.domain.RoleApi;
import lombok.Data;

import javax.management.relation.Role;

@Data
public class RoleApiDTO {
    private Long id;
    private Long child_id;
    private String child_name;
    private Long func_id;
    private String func_name;
    private boolean isAllow;
    private String description;
    private String api_url;
    private String apiItem_url;
    private String method;

    public static RoleApiDTO from(RoleApi api) {
        RoleApiDTO dto = new RoleApiDTO();
        dto.setId(api.getId());
        dto.setAllow(api.isAllow());
        dto.setDescription(api.getApiItem().getDescription());
        dto.setChild_id(api.getApiItem().getId());
        dto.setFunc_name(api.getApiItem().getApi().getName());
        dto.setFunc_id(api.getApiItem().getApi().getId());
        dto.setChild_name(api.getApiItem().getName());
        dto.setApi_url(api.getApiItem().getApi().getUrl());
        dto.setApiItem_url(api.getApiItem().getUrl());
        dto.setMethod(api.getApiItem().getMethod());
        return dto;
    }

}
