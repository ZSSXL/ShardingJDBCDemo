package com.zss.sda.generator;

import com.zss.sda.common.Constant;
import com.zss.sda.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/23 14:18
 * @desc redis id v3
 */
@Slf4j
@SuppressWarnings("unused")
public class RedisIdGenerator implements IdentifierGenerator, Configurable {

    private String tableNameValue;

    @SuppressWarnings({"WeakerAccess"})
    protected String determineTableNameValue(Properties params) {
        return ConfigurationHelper.getString(Constant.TABLE_NAME, params, Constant.DEFAULT_TABLE_NAME);
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        tableNameValue = determineTableNameValue(params);
        log.info("Get TableName From Parameters: [{}]", tableNameValue);
    }

    private long getIncrId() {
        return RedisUtil.incr(tableNameValue);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object o) throws HibernateException {
        return getIncrId();
    }

}
