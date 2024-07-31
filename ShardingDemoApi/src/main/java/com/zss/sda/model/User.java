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
 * @date 2021/2/20 11:01
 * @desc 用户实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "sjd_user")
@Table(appliesTo = "sjd_user")
public class User implements Serializable {

    /**
     * id
     */
    @Id
    @Column(nullable = false, name = "user_id")
    @GeneratedValue(generator = "id")
    @GenericGenerator(name = "id", strategy = "com.zss.sda.generator.RedisIdGenerator",
        parameters = @org.hibernate.annotations.Parameter(name = "table_name", value = "sjd_user"))
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "varchar(128)")
    private String userCode;

    @Column(nullable = false, unique = true, columnDefinition = "varchar(20)")
    private String username;

    @Column(columnDefinition = "integer")
    private Integer age;

    /**
     * 所属企业Code
     */
    @Column(columnDefinition = "varchar(128)")
    private String enterpriseCode;

    /**
     * 个人描述
     */
    @Column(name = "`desc`", columnDefinition = "text")
    private String desc;
}
