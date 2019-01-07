package lk.loginForm;

import org.mindrot.bcrypt.BCrypt;

/**
 * @Auther: Lk
 * @Date: 2019/1/3 0003 09:15
 * @Description:    加密 解密
 */
public class BCryptFunction {
    public BCryptFunction(){}

    public boolean BCryptPsw(String pass,String truepass){
        //String re= BCrypt.hashpw(pass+"cisdi",BCrypt.gensalt());//加密
        pass=pass+"cisdi";
        boolean pswFlag = BCrypt.checkpw(pass,truepass);//解密
        return pswFlag;

    }
}
