package com.nowcoder.demo.util;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

/**
 * @author pxk
 * @date 2019/11/30 - 20:06
 */
@Service
public class MailSender implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    public boolean sendWithHTMLTemplate(String to, String subject, String template, Map<String, Object> model){
        try {
            String nick = MimeUtility.encodeText("牛客中级课");
            InternetAddress from = new InternetAddress(nick + "<1873703540@163.com>");

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            //通过velocity的引擎渲染出去
            String result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result, true);//result构造的模板
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("发送失败" + e.getMessage());
            return false;
        }
    }


//    //初始化，密码，端口配置
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        mailSender = new JavaMailSenderImpl();
//
//        // 请输入自己的邮箱和密码，用于发送邮件
//        mailSender.setUsername("pxk4464@163.com");
//        mailSender.setPassword("");
//        mailSender.setHost("smtp.163.com");
//        mailSender.setPort(465);
//        mailSender.setProtocol("smtps");
//        mailSender.setDefaultEncoding("utf8");
//        Properties javaMailProperties = new Properties();
//        javaMailProperties.put("mail.smtp.auth", "true");
//        javaMailProperties.put("mail.smtp.ssl.enable", "true");
//        javaMailProperties.put("mail.smtp.socketFactory.fallback", "true");
//        mailSender.setJavaMailProperties(javaMailProperties);
//    }


    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();

        // 请输入自己的邮箱和密码，用于发送邮件
        mailSender.setUsername("1873703540@qq.com");
        mailSender.setPassword("ijktowisorrgcggc");
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}