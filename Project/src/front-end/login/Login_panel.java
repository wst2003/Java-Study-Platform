package login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


import learning.Learning_page;
import mainFrame.Main_frame;


/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : login
 * @ClassName : login.Login_panel.java
 * @createTime : 2022/11/16 21:32
 * @Description : 登录页面
 */
public class Login_panel extends JPanel implements ActionListener{
    JTextField name_input;
    JPasswordField password_input;
    JButton input_confirm=new JButton("确认输入");
    JButton switch_register=new JButton("注册新用户");
    JLabel alert=new JLabel();

    String name="";//用户名
    String password="";
    String login_string ="";
    JPanel overall_panel=new JPanel();
    JPanel alert_panel;
    Font f=new Font("宋体",Font.PLAIN,20);
    /**
    *   @Description: 构造函数，设置UI
    *   @Param: 
    *   @ParamTypes: 
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public Login_panel(){
        JLabel name_label = new JLabel("请输入用户名");
        JLabel password_label =new JLabel("请输入密码");
        name_label.setFont(f);
        password_label.setFont(f);
        input_confirm.setFont(f);
        switch_register.setFont(f);
        alert.setFont(f);
        name_input =new JTextField(20);
        password_input=new JPasswordField(20);
        setLayout(null);

        overall_panel.setBounds(450,300,600,400);
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
        JPanel button_panel=new JPanel();
        button_panel.add(input_confirm);
        button_panel.add(switch_register);
        overall_panel.add(button_panel);
        alert_panel=new JPanel();
        alert_panel.add(alert);
        alert_panel.setPreferredSize(new Dimension(300,10));
        overall_panel.add(alert_panel);

        add(overall_panel);
        input_confirm.addActionListener(this);
        switch_register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main_frame.SwitchPanel("register");
                name="";
                password="";
                name_input.setText("");
                password_input.setText("");
            }
        });
        updateUI();
        repaint();
    }


    /**
     *  登录成功，切换到学习页面
     *
     */
    void switchPage(){
        Main_frame.userID=name;
        Learning_page.setDirVec(Main_frame.socketManage.getChapterNames());
        Main_frame.SwitchPanel("learning");
    }

    /**
     * 绑定在确认登录上的方法
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        name=name_input.getText();
        password=new String(password_input.getPassword());
        login_string ="login"+"-"+name+"-"+password;
        if(name.length()>0 && password.length()>0){
            //用户名和密码均不为空
            try {
                System.out.println("login" + "-" + name + "-" + password);

                String return_string=Main_frame.socketManage.upload(login_string);
                System.out.println("服务器返回："+return_string);

                switch (return_string){
                    case"1":
                        alert.setText("登录成功！");
                        switchPage();
                        break;
                    case "0":
                        alert.setText("密码不匹配，请重新输入！");
                        break;
                    case "-1":
                        alert.setText("该用户不存在，请注册！");
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
        else{
            alert.setText("用户名与密码均不可为空！");
        }
    }
}
