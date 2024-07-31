package com.zss.sda.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/20 13:23
 * @desc 企业实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "sjd_enterprise")
@Table(appliesTo = "sjd_enterprise")
public class Enterprise implements Serializable {

    @Id
    @Column(name = "enterprise_id")
    @GeneratedValue(generator = "id")
    @GenericGenerator(name = "id", strategy = "com.zss.sda.generator.RedisIdGenerator",
            parameters = @org.hibernate.annotations.Parameter(name = "table_name", value = "sjd_enterprise"))
    private Long id;

    @Column(name = "enterprise_code", columnDefinition = "varchar(128)")
    private String enterpriseCode;

    @Column(name = "enterprise_name", columnDefinition = "varchar(100)")
    private String enterpriseName;

    @Column(columnDefinition = "varchar(50)")
    private String email;

    /**
     * 公司地址
     */
    @Column(columnDefinition = "varchar(128)")
    private String address;

    /**
     * 企业描述
     */
    @Column(name = "`desc`", columnDefinition = "text")
    private String desc;
}
