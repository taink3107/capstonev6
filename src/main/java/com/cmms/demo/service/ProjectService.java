package com.cmms.demo.service;

import com.cmms.demo.domain.ProjectPOJO;
import com.cmms.demo.dto.ProjectDTO;
import com.cmms.demo.dto.ProjectOutput;

public interface ProjectService {
    ProjectPOJO addProject(ProjectDTO dto);

    ProjectPOJO getOne(String code);

    ProjectOutput filter(Integer pageIndex, Integer pageSize, String customerName
            , Long projectType, Integer projectStatus);

    void updateStartAndEndDateForProject();

    ProjectPOJO update(ProjectDTO dto);

    String cancelProject(String projectCode);
}
