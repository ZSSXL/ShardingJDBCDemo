package com.zss.sda.properties.redis;

/**
 * @author zhoushs@dist.com.cn
 * @date 2020/8/11 10:35
 * @desc Lettuce单个 - 属性
 */
@SuppressWarnings("unused")
public class LettuceSingleProperties {

    private String host;

    private Integer port;

    private Integer database;

    private Integer timeout;

    // ==================== Getter & Setter ==================== //

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    // ==================== ToString ==================== //

    @Override
    public String toString() {
        return "LettuceSingleProperties{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", database=" + database +
                ", timeout=" + timeout +
                '}';
    }
}
