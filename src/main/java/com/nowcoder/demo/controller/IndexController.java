package com.nowcoder.demo.controller;

import com.nowcoder.demo.aspect.LogAspect;
import com.nowcoder.demo.model.User;
import com.nowcoder.demo.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author pxk
 * @date 2019/10/5 - 21:10
 */

@Controller
public class IndexController {


    @Autowired//控制反转的思想，将实现写到了单独的地方，用到的地方使用最简单的方法注入进来，@autowired
    /*
    ●谁控制谁，控制什么：传统Java SE程序设计，我们直接在对象内部通过new进行创建对象，是程序主动去创建依赖对象；
    而IoC是有专门一个容器来创建这些对象，即由Ioc容器来控制对象的创建；谁控制谁？
    当然是IoC 容器控制了对象；控制什么？那就是主要控制了外部资源获取（不只是对象包括比如文件等）。

　　●为何是反转，哪些方面反转了：有反转就有正转，传统应用程序是由我们自己在对象中主动控制去直接获取依赖对象，也就是正转；
    而反转则是由容器来帮忙创建及注入依赖对象；为何是反转？因为由容器帮我们查找及注入依赖对象，
    对象只是被动的接受依赖对象，所以是反转；哪些方面反转了？依赖对象的获取被反转了。
     */
    private ToutiaoService toutiaoService;//spring解放了，做了控制反转，或者依赖注入
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
//    public IndexController(){//传统java 代码
//        toutiaoService = new ToutiaoService();
//    }

    /**
    @RequestMapping(path = {"/", "/index"})//path或者value
    @ResponseBody
    public String index(HttpSession session) {
        logger.info("Visit Index");
        return "helloworld, " +  session.getAttribute("msg") + "<br>" + toutiaoService.say();

    }
    **/


    //http://localhost:8080/profile/11/33?type=11&key=0
    @RequestMapping(path = {"/profile/{groupId}/{userId}"})//value是别名
    @ResponseBody//返回的是string，所以response 是个body,
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", defaultValue = "nowcoder") String key) {

        return String.format("{%s}, {%d}, %d, %s", groupId, userId, type, key);
    }

    @RequestMapping(value = {"/vm"})//没有resposebody,返回的不是helloworld,返回的是页面了，
    public String news(Model model) {
        model.addAttribute("value1", "vv1");

//        List<String> colors = new ArrayList<>(){"green","red"};上面的是不行，直接用
        List<String> colors = Arrays.asList("red", "green", "color");
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < 4; i++) {
            map.put(String.valueOf(i), String.valueOf(i * i));
        }

        model.addAttribute("colors", colors);
        model.addAttribute("map", map);
        model.addAttribute("user", new User("jim"));

        return "news";//找的是news.vm这个页面
    }


    @RequestMapping(value = {"/request"})//参数解析，cookie的读取，http请求，文件上传
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name + ':' + request.getHeader(name) + "<br>");
        }

        for(Cookie cookie: request.getCookies()){
            sb.append("cooking");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");
        }

        sb.append("getMedthod:" + request.getMethod() + "<br>");
        sb.append("getPathInfo:" + request.getPathInfo() + "<br>");
        sb.append("getQueryString" + request.getQueryString() + "<br>");
        return sb.toString();
    }

    @RequestMapping(value = {"/response"})//能将所有的数据写回去，cookie下发，http字段设置，header
    @ResponseBody
    public String response(@CookieValue(value = "nowcoderid", defaultValue = "a") String nowcoderid,
                           @RequestParam(value = "key", defaultValue = "key") String key,
                           @RequestParam(value = "value", defaultValue = "value") String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key, value));
        response.addHeader(key,value);
        return "NowCoderId from Cookie:" + nowcoderid;//从cookie中获得的id
    }

    //跳转
    @RequestMapping(value = "/redirect/{code}")//一次会话代表一次session
    public String redirect (@PathVariable("code") int code,
                            HttpSession session) {
//        RedirectView red = new RedirectView("/", true);
//        if (code == 301){
//            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
//        }
//        return red;
        session.setAttribute("msg", "jump from redirect");
        return "redirect:/";
    }

    //业务异常的处理
    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key", required = false) String key){
        if("admin".equals(key)){//传过来的key的字符串是"admin"
            return "hello admin";
        }
        throw new IllegalArgumentException("key错误");
    }

    @ExceptionHandler()//对统一的错误处理
    @ResponseBody
    public String error(Exception e){
        return "error:" + e.getMessage();
    }




}