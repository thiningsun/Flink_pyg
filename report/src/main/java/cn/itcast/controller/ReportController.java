package cn.itcast.controller;

import cn.itcast.bean.Message;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @Date 2019/7/28
 */
@Controller
@RequestMapping("report")
public class ReportController {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @RequestMapping(value="put",method= RequestMethod.POST)
    public void sendLogData(@RequestBody String jsonStr, HttpServletResponse rsp) throws IOException {


        Message message = new Message();
        message.setMessage(jsonStr);
        message.setCount(1);
        message.setTimestamp(new Date().getTime());

        System.out.println("<<:"+message.toString());

        //发送kafka
        kafkaTemplate.send("test-28","test", JSON.toJSONString(message));

        PrintWriter printWriter= write(rsp);
        printWriter.flush();
        printWriter.close();
    }

    private PrintWriter write(HttpServletResponse rsp) throws IOException {
        //设置编码方式
        rsp.setCharacterEncoding("UTF-8");
        //设置响应头
        rsp.setContentType("application/json");
        //设置响应返回状态
        rsp.setStatus(HttpStatus.OK.value());
        ServletOutputStream outputStream = rsp.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("send sucess");
        return printWriter;
    }


}
