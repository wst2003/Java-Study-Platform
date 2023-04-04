package exercise;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : exercise
 * @ClassName : exercise.judgeTopic.java
 * @createTime : 2022/12/1 0:27
 * @Description : 判断题类，继承自Topic总类
 */
public class judgeTopic extends Topic {
    ButtonGroup choices_g=new ButtonGroup();//管理所有单选框的按钮组
    //JLabel ques=new JLabel();//放置题干信息
    JRadioButton[] choices_b=new JRadioButton[2];//包含正确和错误两种选项
    JPanel chapter_info_p=new JPanel();//评价页面的章节信息
    JPanel answer_contrast=new JPanel();//评价页面的答案信息
    /**
    *   @Description: 构造函数
     *   @Param: No, chapter, chapter_name, question, s_answer 
     *   @ParamTypes: String, String, String, String, String
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public judgeTopic(String No,String chapter,String chapter_name,String question,String s_answer){
        super(No, chapter,chapter_name,question, s_answer);
        type="判断题";
        setChoices();

        BoxLayout boxLayout=new BoxLayout(this,BoxLayout.Y_AXIS);
        setLayout(boxLayout);

        add(ques_panel);

        choices_b[0].setFont(f);
        choices_b[1].setFont(f);
        input_panel.add(choices_b[0]);
        input_panel.add(choices_b[1]);
        add(input_panel);
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
        if(answer.equals(standard_ans)==true)
            correct=true;
        else
            correct=false;
        for(int i=0;i<choices_b.length;++i){
            choices_b[i].setEnabled(false);
        }
        chapter_info_p.setLayout(new FlowLayout(FlowLayout.LEFT));
        answer_contrast.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel chapter_info=new JLabel();
        chapter_info.setFont(f);
        chapter_info.setText("该题目属于第"+chapter+"章  "+chapter_name);
        chapter_info_p.add(chapter_info);

        JLabel answer_l=new JLabel();
        answer_l.setFont(f);
        answer_l.setText("标准答案："+standard_ans+"  你的答案："+answer);
        answer_contrast.add(answer_l);

        removeAll();
        add(ques_panel);
        add(chapter_info_p);
        add(input_panel);
        add(answer_contrast);
    }
    /**
    *   @Description: 设置正确与错误按钮
    *   @Param: 
    *   @ParamTypes: 
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    void setChoices(){
        choices_b[0]=new JRadioButton("正确");
        choices_b[1]=new JRadioButton("错误");
        choices_b[1].setSelected(true);
        answer="F";//默认选择错误
        choices_g.add(choices_b[0]);
        choices_g.add(choices_b[1]);
        choices_b[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                answer="T";
            }
        });
        choices_b[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                answer="F";
            }
        });
    }
}
