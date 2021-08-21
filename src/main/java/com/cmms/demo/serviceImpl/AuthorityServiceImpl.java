package com.cmms.demo.serviceImpl;

import com.cmms.demo.domain.*;
import com.cmms.demo.dto.ApiItemDTO;
import com.cmms.demo.dto.ApiDTO;
import com.cmms.demo.reponsitory.*;
import com.cmms.demo.service.AuthorityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    Logger LOGGER = LoggerFactory.getLogger(EndpointListener.class);
    @Autowired
    UserApiRepository repository;

    @Autowired
    ApiRepository apiRepository;

    @Autowired
    ApiItemRepository apiItemRepository;
    @Autowired
    RoleApiRepository roleApiRepository;

    @Autowired
    UserRepository userRepository;


    @Autowired
    ObjectMapper objectMapperl;
    @Autowired
    UserServiceImpl service;

    @Autowired
    RoleRepository roleRepository;

    public UserApi getOne(String action, String api, Long user_id) {
        return repository.findOneAuthority(action, api, user_id);
    }

    public List<Api> getAll() {
        return apiRepository.findAll();
    }


    public List<UserApi> update(Long id, @NotNull List<Long> actions) {

        List<UserApi> decentralizations = repository.getAllByUserId(id);
        decentralizations.stream().forEach(decentralization -> repository.delete(decentralization));
        decentralizations = new ArrayList<>();
        UserPOJO pojo = userRepository.getById(id);
        for (Long x : actions) {
            UserApi decentralization = new UserApi();
            decentralization.setUser(pojo);
            decentralization.setApiItem(apiItemRepository.getById(x));
            decentralizations.add(repository.save(decentralization));
        }
        return decentralizations;
    }

    private void deleteAction(ApiItem action) {
        if (apiItemRepository.isExist(action.getId()) > 0) {
            apiItemRepository.deleteRoleActionByActionId(action.getId());
        }
        if (repository.isExist(action.getId()) > 0) {
            repository.deleteByActionId(action.getId());
        }
        apiItemRepository.delete(action);
    }

    public Api addActionForApi(Long id_api, List<String> actions) {
        Api api = apiRepository.findById(id_api).orElse(null);
        List<ApiItem> acts = apiItemRepository.getAllActionByApiId(id_api);
        List<ApiItem> finalActs = new ArrayList<>();
        List<ApiItem> deleteActs = new ArrayList<>();
        for (ApiItem action : acts) {
            boolean isEqual = false;
            for (String x : actions) {
                if (action.getName().equals(x)) {
                    if (!checkExist(finalActs, action)) {
                        finalActs.add(action);
                        isEqual = true;
                    }
                }
            }
            if (!isEqual) {
                deleteActs.add(action);
            }
        }
        deleteActs.forEach(this::deleteAction);

        for (ApiItem action : finalActs) {
            if (actions.contains(action.getName())) {
                actions.remove(action.getName());
            }
        }
        actions.stream().forEach(name -> {
            ApiItem action = new ApiItem();
            action.setApi(api);
            action.setName(name);
            finalActs.add(action);
        });
        acts = finalActs;
        api.setItems(acts);
        return apiRepository.save(api);
    }

    private boolean checkExist(List<ApiItem> actions, ApiItem action) {
        for (ApiItem action1 : actions) {
            if (action1.getId().equals(action.getId())) {
                return true;
            }
        }
        return false;
    }
    public List<Long> getAllActionOfRole(int id) {
        List<ApiItem> actions = apiItemRepository.getAllActionByRoleId(id);
        return actions.stream().map(action -> action.getId()).collect(Collectors.toList());
    }

    public Api addNewApi(ApiDTO dto) {
        Api api = new Api();
        api.setUrl(dto.getUrl());
        api.setName(dto.getName());
        return apiRepository.save(api);
    }

    public List<Api> allApi() {
        List<Api> apis = apiRepository.findAll();
        return apis;
    }

    @Override
    public List<ApiItem> getAllApiItem() {
        return apiItemRepository.findAll();
    }

    public ApiItem saveApiItem(ApiItem apiItem) {
        return apiItemRepository.save(apiItem);
    }

    public List<UserApi> getAllUserApi() {
        return repository.findAll();
    }

    @Override
    public List<RoleApi> getAllRoleApi() {
        return roleApiRepository.findAll();
    }
    public void saveUserApi(UserApi userApi){
        repository.save(userApi);
    }

    @Override
    public void saveRoleApi(RoleApi roleApi) {
        roleApiRepository.save(roleApi);
    }

}
