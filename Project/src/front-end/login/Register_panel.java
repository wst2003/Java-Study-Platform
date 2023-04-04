package login;

import learning.Learning_page;
import mainFrame.Main_frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : register
 * @ClassName : login.Register_panel.java
 * @createTime : 2022/11/16 22:09
 * @Description : 注册页面
 */
public class Register_panel extends JPanel implements ActionListener{
    JTextField name_input;
    JPasswordField password_input;
    JPasswordField password_re_input;
    JButton input_confirm=new JButton("确认输入");
    JButton return_login=new JButton("返回登录页面");
    JLabel alert=new JLabel();
    String name="";
    String password="";
    String re_password="";
    String register_string ="";
    JPanel overall_panel=new JPanel();
    Font f=new Font("宋体",Font.PLAIN,20);
    JPanel alert_panel;
    /**
    *   @Description: 在构造函数中设置页面UI
    *   @Param: 
    *   @ParamTypes: 
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public Register_panel() {
        JLabel name_label = new JLabel("请输入用户名");
        JLabel password_label =new JLabel("请输入密码");
        JLabel password_confirm_label =new JLabel("请再次输入确认密码");
        name_label.setFont(f);
        password_label.setFont(f);
        password_confirm_label.setFont(f);
        return_login.setFont(f);
        input_confirm.setFont(f);
        alert.setFont(f);
        name_input =new JTextField(20);
        password_input=new JPasswordField(20);
        password_re_input=new JPasswordField(20);
        add(overall_panel);
        setLayout(null);

        overall_panel.setBounds(450,300,600,500);
        BoxLayout boxLayout=new BoxLayout(overall_panel,BoxLayout.Y_AXIS);
        overall_panel.setLayout(boxLayout);

        JPanel name_panel=new JPanel();
        name_panel.add(name_label);
        name_panel.add(name_input);
        name_panel.setPreferredSize(new Dimension(300,10));
        overall_panel.add(name_panel);

        JPanel password_panel=new JPanel();
        password_panel.add(password_label);
        password_panel.add(password_input);
        password_panel.setPreferredSize(new Dimension(300,10));
        overall_panel.add(password_panel);

        JPanel password_re_panel=new JPanel();
        password_re_panel.add(password_confirm_label);
        password_re_panel.add(password_re_input);
        password_re_panel.setPreferredSize(new Dimension(300,10));
        overall_panel.add(password_re_panel);

        JPanel button_panel=new JPanel();
        button_panel.add(return_login);
        button_panel.add(input_confirm);
        overall_panel.add(button_panel);

        alert_panel=new JPanel();
        alert_panel.add(alert);
        alert_panel.setPreferredSize(new Dimension(300,10));
        overall_panel.add(alert_panel);

        return_login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main_frame.SwitchPanel("login");
                name="";
                password="";
                re_password="";
                name_input.setText("");
                password_input.setText("");
                password_re_input.setText("");

            }
        });
        input_confirm.addActionListener(this);
    }

    /**
    *   @Description: 切换页面到学习页面
    *   @Param: 
    *   @ParamTypes: 
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    void switchPage(){
        Main_frame.userID=name;
        Learning_page.setDirVec(Main_frame.socketManage.getChapterNames());
        Main_frame.SwitchPanel("learning");
    }

    /**
    *   @Description: 判断注册输入是否正确
    *   @Param: e
    *   @ParamTypes: ActionEvent
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    @Override
    public void actionPerformed(ActionEvent e) {
        name=name_input.getText();
        password=new String(password_input.getPassword());
        re_password=new String(password_re_input.getPassword());

        if(name.length()==0 || password.length()==0 || re_password.length()==0){
            alert.setText("输入栏均不可为空！");
        }
        else if(password==re_password){
            alert.setText("两次密码输入不一致！");
        }
        else {
            try {
                register_string = "register" + "-" + name + "-" + password;
                System.out.println("register" + "-" + name + "-" + password);
                String return_string= Main_frame.socketManage.upload(register_string);
                System.out.println("服务器返回："+return_string);

                switch (return_string){
                    case"1":
                        alert.setText("注册成功！");
                        switchPage();
                        break;
                    case "0":
                        alert.setText("注册失败！请重新尝试");
                        break;
                    case "-1":
                        alert.setText("该用户名已存在，请重新设置！");
                        break;
                    case "-2":
                        alert.setText("数据库错误");
                        break;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                //错误处理有待进一步优化
            }
        }
    }
}
