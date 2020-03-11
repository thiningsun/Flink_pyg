package cn.itcast.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Date 2019/7/28
 */
@Controller
@RequestMapping("test")
public class TestController {


    @RequestMapping("send")
    public void sendTest(String str){
        System.out.println("<<<<:"+str);
    }

}
