package cn.itcast.test;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @Date 2019/7/28
 */
public class HttpTest {


    public static void main(String[] args) throws Exception {

        String address="http://localhost:8090/report/put";
        sendData(address,"test==info===");
    }

    public static void sendData(String address, String info) throws Exception {
        //获取连接
        URL url = new URL(address);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        //设置参数
        urlConnection.setAllowUserInteraction(true);
        urlConnection.setConnectTimeout(60000);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        //设置请求头
        urlConnection.setRequestProperty("Content-Type","application/json");
        //设置代理
        urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.108 Safari/537.36");

        //输出数据
        OutputStream outputStream = urlConnection.getOutputStream();
        BufferedOutputStream out = new BufferedOutputStream(outputStream);
        out.write(info.getBytes());
        out.flush();
        out.close();

        //接收响应数据
        InputStream inputStream = urlConnection.getInputStream();
        byte[] bytes = new byte[1024];
        String str = "";
        while (inputStream.read(bytes,0,1024)>-1){
            str = new String(bytes);
        }
        System.out.println("<<rsp value:"+str);
        System.out.println("rsp code:"+urlConnection.getResponseCode());

    }
}
