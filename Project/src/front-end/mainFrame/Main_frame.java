package mainFrame;

import exercise.Examination_page;
import kownledgeStruction.struction;
import learning.Learning_page;
import login.Login_panel;
import login.Register_panel;
import server.*;

import javax.swing.*;
import java.awt.*;


/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package :
 * @ClassName : mainFrame.Main_frame.java
 * @createTime :
 * @Description : 主界面，直接初始化所有可能用到的页面，并后续使用这些页面的static函数改变它们的数据，
 *  使用cardLayout统一管理页面切换
 */
public class Main_frame extends JFrame {
    public static String userID="";
    public static GreetingClient socketManage;//通信管理类，设置为static，任意页面可调用
    //首先初始化所有页面，然后将它们加入到主panel当中
    Login_panel loginPanel=new Login_panel();//登录页面
    public static struction chapter_map=new struction();
    Register_panel registerPanel=new Register_panel();//注册页面
    Learning_page learningPanel=new Learning_page();//学习页面
    Examination_page examinationPage=new Examination_page();//考试页面
    public static Dimension size_d=new Dimension(1500,900);//存储页面标准大小
    public static JPanel mainPanel=new JPanel(); //主Panel,所有的panel都包含在其中
    public static CardLayout c=new CardLayout(); //主panel使用cardlayout作为排版方式，方便切换页面
    /**
    *   @Description: 构造函数，使用cardLayout
    *   @Param:
    *   @ParamTypes:
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    Main_frame(){
        super("java学习平台");
        setSize(size_d);
        setVisible(true);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //平台初始化

        setChapter_map();//构建章节结构

        mainPanel.setSize(getSize());//使main_panel自动填充frame大小
        mainPanel.setLayout(c);


        mainPanel.add(loginPanel,"login");
        mainPanel.add(registerPanel,"register");
        mainPanel.add(learningPanel,"learning");
        mainPanel.add(examinationPage,"examination");
        //在主面板中依次添加页面，后续可扩展
        add(mainPanel);
    }
    /**
    *   @Description: 设置章节无向图架构，以及各章节的权值
     *   权值包括3中：4,5,6
    *   @Param:
    *   @ParamTypes:
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    void setChapter_map(){
        //添加每一章节以及权值
        chapter_map.addVertex(1,4);
        chapter_map.addVertex(2,4);
        chapter_map.addVertex(3,4);
        chapter_map.addVertex(4,4);
        chapter_map.addVertex(5,4);
        chapter_map.addVertex(6,4);
        chapter_map.addVertex(7,2);
        chapter_map.addVertex(8,3);
        chapter_map.addVertex(9,3);
        chapter_map.addVertex(10,2);
        chapter_map.addVertex(11,2);

        chapter_map.addEdge(1,2);
        chapter_map.addEdge(2,3);
        chapter_map.addEdge(2,4);
        chapter_map.addEdge(3,5);
        chapter_map.addEdge(4,5);
        chapter_map.addEdge(5,6);
        chapter_map.addEdge(6,7);
        chapter_map.addEdge(6,8);
        chapter_map.addEdge(6,10);
        chapter_map.addEdge(8,9);
        chapter_map.addEdge(7,11);
        chapter_map.addEdge(9,11);
        chapter_map.addEdge(10,11);

        chapter_map.print();
    }
    /**
    *   @Description: 切换页面
    *   @Param: destPanel
     *   可能的参数：
     *   "login"
     *   "register"
     *   "learning"
     *   "examination"
    *   @ParamTypes: String
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public static void SwitchPanel(String destPanel){
         c.show(mainPanel,destPanel);//任意页面，可以通过传入页面名称的方式切换到目标页面
    }

    public static void main(String[] args) {
        Main_frame app=new Main_frame();
        socketManage=new GreetingClient(args);

    }
}