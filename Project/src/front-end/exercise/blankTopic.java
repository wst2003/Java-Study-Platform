package exercise;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import mainFrame.Main_frame;

/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : exercise
 * @ClassName : exercise.blankTopic.java
 * @createTime : 2022/12/1 0:34
 * @Description : 填空题类，继承自Topic总类
 */
public class blankTopic extends Topic {
    JScrollPane js=new JScrollPane();
    JTextArea jt=new JTextArea();//答案输入框
    JPanel chapter_info_p=new JPanel();//评价页面的章节信息
    JPanel standard_answer_p=new JPanel();//放置标准答案jlabel
    JPanel client_answer_p=new JPanel();//放置用户答案jlabel
    JPanel judging_p=new JPanel();//放置用户答案jlabel
    ButtonGroup choices_g=new ButtonGroup();//管理所有单选框的按钮组
    JRadioButton[] choices_b=new JRadioButton[2];//包含正确和错误两种选项

    /**
    *   @Description: 构造函数，传输信息
    *   @Param: No, chapter, chapter_name, question, s_answer 
    *   @ParamTypes: String, String, String, String, String
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public  blankTopic(String No ,String chapter,String chapter_name,String question,String s_answer){
        super(No, chapter,chapter_name,question, s_answer);
        type="填空题";

        BoxLayout boxLayout=new BoxLayout(this,BoxLayout.Y_AXIS);
        setLayout(boxLayout);

        js.setViewportView(jt);
        js.setPreferredSize(new Dimension(Main_frame.size_d.width-600,100));
        jt.setColumns(100);
        jt.setLineWrap(true);
        jt.setFont(f);
        jt.setBorder(BorderFactory.createTitledBorder("请在此输入"));

        add(ques_panel);
        add(js);
    }

    /**
    *   @Description: 切换到学习评价页面
    *   @Param: 
    *   @ParamTypes: 
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    @Override
    public void switchJudging() {
        answer=jt.getText();
        chapter_info_p.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel chapter_info=new JLabel();
        chapter_info.setFont(f);
        chapter_info.setText("该题目属于第"+chapter+"章："+chapter_name);
        chapter_info_p.add(chapter_info);

        JLabel answer_l=new JLabel();
        JLabel s_answer_l=new JLabel();
        answer_l.setText(JlabelSetText("你的答案："+JlabelSetText(answer)));
        answer_l.setFont(f);
        s_answer_l.setText(JlabelSetText("标准答案："+JlabelSetText(standard_ans)));
        s_answer_l.setFont(f);
        client_answer_p.setLayout(new FlowLayout(FlowLayout.LEFT));
        standard_answer_p.setLayout(new FlowLayout(FlowLayout.LEFT));
        client_answer_p.add(answer_l);
        standard_answer_p.add(s_answer_l);
        int br_num_client=JlabelSetText(answer).split("<br/>").length;
        int br_num_standard=JlabelSetText(standard_ans).split("<br/>").length;
        client_answer_p.setPreferredSize(new Dimension(Main_frame.size_d.width-40,answer_l.getFontMetrics(answer_l.getFont()).getHeight()*br_num_client+10));
        standard_answer_p.setPreferredSize(new Dimension(Main_frame.size_d.width-40,s_answer_l.getFontMetrics(s_answer_l.getFont()).getHeight()*br_num_standard+10));

        judging_p.setLayout(new FlowLayout(FlowLayout.LEFT));
        setChoices();
        judging_p.add(choices_b[0]);
        judging_p.add(choices_b[1]);


        removeAll();
        add(ques_panel);
        add(chapter_info_p);
        add(client_answer_p);
        add(standard_answer_p);
        add(judging_p);


    }
    /**
    *   @Description: 学习评价页面，统一设置正误单选
    *   @Param: 
    *   @ParamTypes: 
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    void setChoices(){
        answer="";
        choices_b[0]=new JRadioButton("正确");
        choices_b[1]=new JRadioButton("错误");
        choices_b[0].setFont(f);
        choices_b[1].setFont(f);
        choices_b[1].setSelected(true);
        choices_g.add(choices_b[0]);
        choices_g.add(choices_b[1]);
        choices_b[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                correct=true;
            }
        });
        choices_b[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {correct=false;}
        });
    }


}
