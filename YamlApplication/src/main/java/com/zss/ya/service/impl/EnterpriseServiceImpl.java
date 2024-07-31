package com.zss.ya.service.impl;

import com.zss.ya.repository.EnterpriseRepository;
import com.zss.sda.model.Enterprise;
import com.zss.sda.service.EnterpriseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/20 14:14
 * @desc enterprise服务层接口方法实现
 */
@Slf4j
@Service
public class EnterpriseServiceImpl implements EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    @Autowired
    public EnterpriseServiceImpl(EnterpriseRepository enterpriseRepository) {
        this.enterpriseRepository = enterpriseRepository;
    }

    @Override
    public Enterprise createEnterprise(Enterprise enterprise) {
        try{
           return enterpriseRepository.save(enterprise);
        } catch (Exception e){
            log.error("创建企业失败: [{}]", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Enterprise> createEnterpriseMulti(List<Enterprise> enterpriseList) {

        try{
            return enterpriseRepository.saveAll(enterpriseList);
        } catch (Exception e){
            log.error("创建企业失败: [{}]", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Enterprise> getAllEnterprise() {
        return enterpriseRepository.findAll();
    }

    @Override
    public Enterprise getEnterpriseById(Long enterpriseId) {
        return null;
    }
}
