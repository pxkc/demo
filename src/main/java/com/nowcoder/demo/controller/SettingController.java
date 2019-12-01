package com.nowcoder.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author pxk
 * @date 2019/10/6 - 15:50
 */
@Controller
public class SettingController {

    @RequestMapping("/setting")
    @ResponseBody
    public String setting() {

        return "setting: ok";
    }
}