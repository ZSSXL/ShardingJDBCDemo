package com.zss.sda.service;


import com.zss.sda.model.Enterprise;

import java.util.List;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/20 13:28
 * @desc 企业服务层接口
 */
public interface EnterpriseService {

    /**
     * 创建企业
     *
     * @param enterprise 企业实体
     * @return Enterprise
     */
    Enterprise createEnterprise(Enterprise enterprise);

    /**
     * 批量创建企业
     *
     * @param enterpriseList 企业实体列表
     * @return enterpriseList
     */
    List<Enterprise> createEnterpriseMulti(List<Enterprise> enterpriseList);

    /**
     * 获取所有的企业数据
     *
     * @return enterpriseList
     */
    List<Enterprise> getAllEnterprise();

    /**
     * 通过企业Id获取企业信息
     *
     * @param enterpriseId 企业Id
     * @return enterprise
     */
    Enterprise getEnterpriseById(Long enterpriseId);
}
