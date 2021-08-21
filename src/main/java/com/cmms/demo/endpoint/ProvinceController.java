package com.cmms.demo.endpoint;

import com.cmms.demo.domain.Province;
import com.cmms.demo.dto.ProvinceDTO;
import com.cmms.demo.service.ProvinceService;
import com.cmms.demo.serviceImpl.ProvinceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/province")
@CrossOrigin("*")
public class ProvinceController {
    @Autowired
    private ProvinceService service;

    @GetMapping
    public ResponseEntity<List<ProvinceDTO>> getAll(){
        List<Province> ls = service.getAll();
        List<ProvinceDTO> output = ls.stream().map(ProvinceDTO::from).collect(Collectors.toList());
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvinceDTO> getOne(@PathVariable Long id){
        return new ResponseEntity<>(ProvinceDTO.from(service.getOne(id)), HttpStatus.OK);
    }
}
