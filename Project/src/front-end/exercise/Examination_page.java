package exercise;

import kownledgeStruction.RecommendationSolution;
import mainFrame.Main_frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : exercise
 * @ClassName : examination_page.java
 * @createTime : 2022/11/30 21:42
 * @Description : 做题页面以及学习评价页面的集合
 */
public class Examination_page extends JPanel {
    public static String chapter;//如果是一章习题，章节号
    public static Vector<Vector<Vector<String>>> questions=new Vector<Vector<Vector<String>>>();//从服务器拉取的所有题目信息
    public static int total_num=3;//题目总数
    static long startTime=0L;//答题开始的时间，以毫秒计
    static long endTime=0L;//答题完成的时间，以毫秒计
    static String fmTime="";//共计用时
    static long totalTime=0L;//答题总时间，以毫秒计
    static JLabel title=new JLabel("章节名");
    JButton back_b=new JButton("返回知识目录");
    static Font f=new Font("宋体",Font.PLAIN,20);//公用字体
    static JScrollPane scrollPane;
    static JPanel test_panel=new JPanel();//scrollPane中的JPanel
    static Vector<Topic> Topics=new Vector<>();//使用多态，统一管理所有题目
    static JLabel topic_s;//选择题标签
    static JLabel topic_j;//判断题标签
    static JLabel topic_b;//填空题标签
    static JLabel topic_c;//编程题标签

    static JButton  submit=new JButton("提交答案");
    static JButton  final_submit=new JButton("提交做题记录");
    static JPanel bottom_panel=new JPanel();//在整个页面最下方的Panel

    /**
    *   @Description: 构造函数，预先设置UI
    *   @Param:
    *   @ParamTypes:
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/

    public Examination_page(){
        //UI设置
        setLayout(null);
        back_b.setBounds(0,0,150,50);
        title.setBounds(150,0, Main_frame.size_d.width-150,50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setText("第"+chapter+"章习题");
        title.setFont(f);
        scrollPane=new JScrollPane(test_panel);

        scrollPane.setBounds(0,50, Main_frame.size_d.width-10, Main_frame.size_d.height-110);
        back_b.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               Main_frame.SwitchPanel("learning");
               totalTime=endTime-startTime;
           }
        });
        add(back_b);
        add(title);
        add(scrollPane);

        topic_s =new JLabel("一、选择题");
        topic_s.setFont(f.deriveFont(Font.BOLD,30));
        topic_j=new JLabel("二、判断题");
        topic_j.setFont(f.deriveFont(Font.BOLD,30));
        topic_b=new JLabel("三、填空题");
        topic_b.setFont(f.deriveFont(Font.BOLD,30));
        topic_c=new JLabel("四、编程题");
        topic_c.setFont(f.deriveFont(Font.BOLD,30));
        //提交按钮
        bottom_panel.add(submit);
        submit.setFont(f);
        submit.setPreferredSize(new Dimension(200,50));
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchJudging();
            }
        });
        final_submit.setFont(f);
        final_submit.setPreferredSize(new Dimension(200,50));
        final_submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endPage();
            }
        });
    }

    /**
    *   @Description: 在学习评价页面，"提交学习记录"按钮绑定该方法；点击后，返回知识页面，同时调出总结页面
    *   @Param:
    *   @ParamTypes:
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    void endPage(){
        Vector<Vector<String>> info=new Vector<>();
        SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        date.setTime(startTime);
        String up_beginTime=fmt.format(date);
        date.setTime(endTime);
        String up_endTime=fmt.format(date);
        System.out.println(up_beginTime+"  "+up_endTime);
        float currency=0.0f;
        for(int i=0;i<Topics.size();++i){
            Vector<String> one_info=new Vector<>();
            one_info.add(up_beginTime);
            one_info.add(up_endTime);
            one_info.add(Topics.get(i).answer);
            String s_correct="";
            if(Topics.get(i).correct==true) {
                currency+=1f;
                s_correct = "T";
            }
            else {
                s_correct = "F";
            }
            one_info.add(s_correct);
            one_info.add(Topics.get(i).chapter);
            one_info.add(Topics.get(i).type);
            one_info.add(Topics.get(i).No);
            one_info.add(Main_frame.userID);//用户名
            info.add(one_info);
        }
        System.out.println("上传题目数量为："+info.size());
        Main_frame.socketManage.uploadTopicInfo(info);
        currency/=Topics.size();

        if(chapter.equals("")==false){//不是综合复习的情况下
            RecommendationSolution.setRecommend(currency,Integer.parseInt(chapter));
            new Summary_frame(fmTime,currency,RecommendationSolution.nextChapter,RecommendationSolution.reviewChapter);
        }
        else {
            System.out.println("综合复习总结页面");
            new Summary_frame(fmTime, currency);
        }

        Main_frame.SwitchPanel("learning");
    }
    /**
    *   @Description: 在类外调用该方法，就能够重置页面的题库（questions数组），以及页面头的章节名
    *   @Param: ques, chap_label
    *   @ParamTypes: Vector<Vector<Vector<String>>>, String
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public static void setPage(Vector<Vector<Vector<String>>> ques,String chap_label){
        questions=ques;
        chapter=chap_label;

        scrollPane.getVerticalScrollBar().setValue(0);
        startTime=System.currentTimeMillis();

        test_panel.removeAll();
        if(chapter=="")
            title.setText("综合复习");
        else
            title.setText("第"+chapter+"章习题");
        BoxLayout boxLayout=new BoxLayout(test_panel,BoxLayout.Y_AXIS);
        bottom_panel.remove(final_submit);
        bottom_panel.add(submit);
        test_panel.setLayout(boxLayout);
        topic_b.setText("三、填空题");
        topic_c.setText("四、编程题");
        setNewQuestions();
    }
    /**
    *   @Description: 刷题页面的”提交答案“按钮上的方法，点击后且切换到学习评价页面
    *   @Param:
    *   @ParamTypes:
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    static void switchJudging(){
        endTime=System.currentTimeMillis();
        for(int i=0;i<Topics.size();++i){
            Topics.get(i).switchJudging();
        }
        totalTime=endTime-startTime;
        totalTime/=1000;
        long second=totalTime%60;
        totalTime/=60;
        long minute=totalTime%60;
        totalTime/=60;
        long hour=totalTime;
        fmTime=hour+"时"+minute+"分"+second+"秒";
        System.out.println("完成做题,共计用时："+fmTime);

        bottom_panel.remove(submit);
        bottom_panel.add(final_submit);
        topic_b.setText( "三、填空题"+
                "(请对照标准答案，自行批改)" );
        topic_c.setText( "四、编程题"+
                "(请对照标准答案，自行批改)");
        scrollPane.getVerticalScrollBar().setValue(0);
    }
    /**
    *   @Description: 在setPage方法中调用，用于根据题库生成各题目，产生最终的刷题页面
    *   @Param:
    *   @ParamTypes:
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    static void setNewQuestions(){
        Topics.clear();
        JPanel topic_sp=new JPanel();
        JPanel topic_jp=new JPanel();
        JPanel topic_bp=new JPanel();
        JPanel topic_cp=new JPanel();
        topic_sp.setLayout(new FlowLayout(FlowLayout.CENTER));
        topic_jp.setLayout(new FlowLayout(FlowLayout.CENTER));
        topic_bp.setLayout(new FlowLayout(FlowLayout.CENTER));
        topic_cp.setLayout(new FlowLayout(FlowLayout.CENTER));
        topic_sp.add(topic_s);
        topic_jp.add(topic_j);
        topic_bp.add(topic_b);
        topic_cp.add(topic_c);
    //选择题
        Vector<Vector<String>>questions_part=questions.get(0);
        test_panel.add(topic_sp);
        for(int i=0;i<questions_part.size();++i){
            Vector<String> one_ques=questions_part.get(i);
            String[]choices=new String[4];
            for(int j=0;j<4;++j){
                choices[j]=one_ques.get(j+4);
            }
            selectTopic one_select=new selectTopic(one_ques.get(2),one_ques.get(0),one_ques.get(1),one_ques.get(3),one_ques.get(8),choices);

            Topics.add(one_select);
            test_panel.add(one_select);
        }
        //判断题
        questions_part=questions.get(1);
        test_panel.add(topic_jp);
        for(int i=0;i<questions_part.size();++i){
            Vector<String> one_ques=questions_part.get(i);
            judgeTopic one_judge=new judgeTopic(one_ques.get(2),one_ques.get(0),one_ques.get(1),one_ques.get(3),one_ques.get(4));

            Topics.add(one_judge);
            test_panel.add(one_judge);
        }
        //填空题
        questions_part=questions.get(2);
        test_panel.add(topic_bp);
        for(int i=0;i<questions_part.size();++i){
            Vector<String> one_ques=questions_part.get(i);
            blankTopic one_blank=new blankTopic(one_ques.get(2),one_ques.get(0),one_ques.get(1),one_ques.get(3),one_ques.get(4));
            Topics.add(one_blank);
            test_panel.add(one_blank);
        }

        //编程题
        questions_part = questions.get(3);
        System.out.println(questions_part.size());
        if (!questions_part.get(0).isEmpty()) {
            test_panel.add(topic_cp);
            for (int i = 0; i < questions_part.size(); ++i) {

                Vector<String> one_ques = questions_part.get(i);
                codeTopic one_code = new codeTopic(one_ques.get(2), one_ques.get(0), one_ques.get(1), one_ques.get(3), one_ques.get(4));
                Topics.add(one_code);
                test_panel.add(one_code);
            }
        }



        //设置每道题的展示题号
        for(int i=0;i<Topics.size();++i){
            Topics.get(i).setShow_No(i+1);
        }
        //提交按钮
        test_panel.add(bottom_panel);
    }
    /*
     * 测试用函数，预设置一些题目
    */


}
