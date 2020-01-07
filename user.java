package com.bittech.client.entity;

import lombok.Data;

/**
 * @Author: BaiMiao
 * @Date: 2020/1/7 11:03
 * @Description:user表的实体类
 */
@Data
public class User {
    private Integer id;//实体类型都用包装类
    private String username;
    private String password;
    private String brief;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
}
