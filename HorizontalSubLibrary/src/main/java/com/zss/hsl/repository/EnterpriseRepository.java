package com.zss.hsl.repository;

import com.zss.sda.model.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/20 14:16
 * @desc Enterprise持久化
 */
@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
}
