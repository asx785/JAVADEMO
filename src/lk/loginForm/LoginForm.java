package lk.loginForm;

import demoFunction.classDemoClient;
import lk.loginForm.method.BCryptFunction;
import lk.loginForm.method.ReadProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Auther: Lk
 * @Date: 2019/1/2 0002 13:31
 * @Description:
 */
public class LoginForm {
    public  JPanel loginJPanel;
    private JTextField usernametextField;
    private JTextField passwordTextField;
    private JButton Loginbutton;
    private JLabel BG;


    public static boolean loginFalg=false;//登录成功验证
    private JFrame windowsform; //程序主界面
    private MessageForm messageForm=new MessageForm();//消息弹窗的方法

    public LoginForm(JFrame frame) {
        windowsform=frame;
        initialize();
    }

    public void initialize(){
        /*登录界面初始化*/
        JFrame loginframe = new JFrame("LoginForm");
        loginframe.setContentPane(loginJPanel);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screensize.getWidth();
        int height = (int) screensize.getHeight();
        loginframe.setLocation(width / 2 - 300, height / 2 - 200);
        loginframe.setTitle(" ");
        loginframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginframe.setResizable(false);
        loginframe.pack();
        loginframe.setVisible(true);


//        BG.setSize(400,300);
//        ImageIcon imageIcon= new ImageIcon("C:\\Users\\Administrator\\Desktop\\JavaDemoCode(x64)\\src\\resources\\picture\\143882949157763955c2cbb3c2d49.jpg");
//        BG.setSize(loginJPanel.getSize());
//        BG.setIcon(imageIcon);
        BG.setFont(new Font("宋体", Font.LAYOUT_NO_LIMIT_CONTEXT, 70));
        /**/
        Properties prop= ReadProperties.getProp();

        Loginbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String username=usernametextField.getText();
                String password=passwordTextField.getText();
                BCryptFunction bCryptFunction=new BCryptFunction();
                if("".equals(username)){
                    messageForm.messageShow("Username can not be empty ");
                    return;
                }
                if("".equals(password)){
                    messageForm.messageShow("Password can not be empty");
                    return;
                }

                if(username.equals(prop.getProperty("username"))
                        &&bCryptFunction.BCryptPsw(password,prop.getProperty("password"))){
                    loginFalg=true;
                    //loginframe.setVisible(false);//隐藏登录界面
                    loginframe.dispose();//回收登录界面
                    windowsform.setVisible(true);//显示main界面
                }else{
                    // lktodo:测试免登陆
                    //messageForm.messageShow("UserName or Password may be wrong !");
                    loginframe.dispose();//回收登录界面
                    windowsform.setVisible(true);//显示main界面
                }
            }
        });
    }





}
