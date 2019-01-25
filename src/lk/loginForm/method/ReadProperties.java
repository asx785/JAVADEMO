package lk.loginForm.method;

import demoFunction.classDemoClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Auther: Lk
 * @Date: 2019/1/7 0007 16:18
 * @Description:
 */
public class ReadProperties {
    //读取配置
    private static Properties prop;

    static {
        InputStream in = classDemoClient.class.getClassLoader().getResourceAsStream("resource/properties.properties");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        prop= new Properties();
        try {
            prop.load(br);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Properties getProp(){
        return prop;
    }
}
