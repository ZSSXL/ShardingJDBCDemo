package com.zss.sda.properties;

import com.zss.sda.properties.redis.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author zhoushs@dist.com.cn
 * @date 2020/8/11 10:51
 * @desc Lettuce - 属性
 */
@Component
@SuppressWarnings("unused")
@ConfigurationProperties(prefix = "lettuce")
@EnableConfigurationProperties(LettuceProperties.class)
public class LettuceProperties {

    private LettuceSingleProperties single;
    private LettucePoolProperties pool;

    // ==================== Getter & Setter ==================== //

    public LettuceSingleProperties getSingle() {
        return single;
    }

    public void setSingle(LettuceSingleProperties single) {
        this.single = single;
    }

    public LettucePoolProperties getPool() {
        return pool;
    }

    public void setPool(LettucePoolProperties pool) {
        this.pool = pool;
    }

    // ==================== Hash & Equals ==================== //


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LettuceProperties that = (LettuceProperties) o;
        return Objects.equals(single, that.single) &&
                Objects.equals(pool, that.pool);
    }

    @Override
    public int hashCode() {
        return Objects.hash(single, pool);
    }

    // ==================== ToString ==================== //

    @Override
    public String toString() {
        return "LettuceProperties{" +
                "single=" + single +
                ", pool=" + pool +
                '}';
    }
}
