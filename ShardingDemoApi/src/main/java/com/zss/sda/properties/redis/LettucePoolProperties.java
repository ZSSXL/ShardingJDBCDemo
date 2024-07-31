package com.zss.sda.properties.redis;

/**
 * @author zhoushs@dist.com.cn
 * @date 2020/8/11 14:56
 * @desc Lettuce连接池 - 属性
 */
@SuppressWarnings("unused")
public class LettucePoolProperties {

    /**
     * 对象池中最大的空闲对象个数，默认值是8。
     */
    private Integer maxIdle = 8;
    /**
     * 对象池中最小的空闲对象个数，默认值是0。
     */
    private Integer minIdle = 0;
    /**
     * 对象池中管理的最多对象个数默，认值是8。
     */
    private Integer maxTotal = 8;
    /**
     * 当连接池资源耗尽时，等待时间，超出则抛异常，默认为-1即永不超时, 单位ms
     */
    private Long maxWaitMills = -1L;
    /**
     * 在获取对象的时候检查有效性, 默认false
     */
    private Boolean testOnBorrow = false;
    /**
     * 在归还对象的时候检查有效性, 默认false
     */
    private Boolean testOnReturn = false;
    /**
     * 在创建对象时检测对象是否有效，默认值是false。
     */
    private Boolean testOnCreate = false;

    // ==================== Getter & Setter ==================== //


    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Long getMaxWaitMills() {
        return maxWaitMills;
    }

    public void setMaxWaitMills(Long maxWaitMills) {
        this.maxWaitMills = maxWaitMills;
    }

    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public Boolean getTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public Boolean getTestOnCreate() {
        return testOnCreate;
    }

    public void setTestOnCreate(Boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
    }

    // ==================== ToString ==================== //

    @Override
    public String toString() {
        return "LettucePoolProperties{" +
                "maxIdle=" + maxIdle +
                ", minIdle=" + minIdle +
                ", maxTotal=" + maxTotal +
                ", maxWaitMills=" + maxWaitMills +
                ", testOnBorrow=" + testOnBorrow +
                ", testOnReturn=" + testOnReturn +
                ", testOnCreate=" + testOnCreate +
                '}';
    }
}
