package com.nowcoder.demo.controller;

import com.nowcoder.demo.async.EventModel;
import com.nowcoder.demo.async.EventProducer;
import com.nowcoder.demo.async.EventType;
import com.nowcoder.demo.service.UserService;
import com.nowcoder.demo.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author pxk
 * @date 2019/10/18 - 15:10
 */

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    EventProducer eventProducer;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})//path或者value
    @ResponseBody
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                      HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0) {
                    cookie.setMaxAge(3600 * 24 * 5);//5天, 默认浏览器关闭就没了
                }
                response.addCookie(cookie);//这样后端才能收到信息
                return ToutiaoUtil.getJSONString(0, "注册成功！！！！");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        } catch (Exception e) {

            logger.error("注册异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "注册异常");
        }
    }

    @RequestMapping(path = {"/login/"}, method = {RequestMethod.GET, RequestMethod.POST})//path或者value
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0) {
                    cookie.setMaxAge(3600 * 24 * 5);//5天, 默认浏览器关闭就没了
                }
                response.addCookie(cookie);//这样后端才能收到信息

                eventProducer.fireEvent(new EventModel(EventType.LOGIN).setActorId((int) map.get("userId"))
                        .setExt("username", username).setExt("email","pxk4464@qq.com"));//UserService里面的map没有put这个字段
                return ToutiaoUtil.getJSONString(0, "登录成功");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        } catch (Exception e) {

            logger.error("登录异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "登录异常");
        }
    }


    @RequestMapping(path = {"/logout/"}, method = {RequestMethod.GET, RequestMethod.POST})//path或者value
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

}