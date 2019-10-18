package com.nowcoder.demo.service;

import org.springframework.stereotype.Service;

/**
 * @author pxk
 * @date 2019/10/6 - 15:32
 */
@Service//系统容器起来自动创建这个对象，放到AutoWerid上,
public class ToutiaoService {

    public String say() {
        return "I am say  ToutiaoService";
    }
}