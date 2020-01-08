package com.bittech.vo;

import lombok.Data;

/**
 * @Author: BaiMiao
 * @Date: 2020/1/7 17:57
 * @Description:服务端与客户端通信载体
 */
@Data
public class MessageVO {
    /**
     * 告知服务端要进行的操作，eg：1.表示新用户注册2.表示私聊3.表示群聊等
     */

    private Integer type;
    /**
     * 服务端与客户端聊天具体内容
     */
    private String content;
    /**
     * 聊天信息发送的目标客户端名称
     */
    private String to;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        
        this.type = type;
    }

    public String getContent() {
        
        return content;
    }

    public void setContent(String content) {
        
        this.content = content;
    }

    public String getTo() {
        
        return to;
    }

    public void setTo(String to) {
        
        this.to = to;
    }
}
