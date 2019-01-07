package lk.loginForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @Auther: Lk
 * @Date: 2019/1/3 0003 10:33
 * @Description:
 */
public class MessageForm {


    private JPanel panel1;
    private JLabel messageLabel;
    private JButton button1;

    private static JFrame messageframe = new JFrame("MessageForm");//弹窗form界面
    public MessageForm(){
        initialize();

    }


    public void messageShow(String str){
        this.messageLabel.setSize(10,10);
        this.messageLabel.setText(str);
        messageframe.setVisible(true);
    }



    public void initialize(){
        messageframe.setContentPane(panel1);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screensize.getWidth();
        int height = (int) screensize.getHeight();
        messageframe.setLocation(width / 2 - 175, height / 2 - 50);
        messageframe.setTitle("Message");
        messageframe.setResizable(false);
        messageframe.pack();
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                messageframe.setVisible(false);
            }
        });
    }

}


