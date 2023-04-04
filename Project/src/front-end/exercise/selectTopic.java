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
 * @ClassName : singleChoiceQ_4choice.java
 * @createTime : 2022/12/1 0:27
 * @Description : 选择题类，继承自Topic总类
 */
public class selectTopic extends Topic {
    String[]choices=new String[4];
    ButtonGroup choices_g=new ButtonGroup();
    JRadioButton[] choices_b=new JRadioButton[4];
    JPanel[] choices_p=new JPanel[4];
    JPanel chapter_info_p=new JPanel();//评价页面的章节信息
    JPanel answer_contrast=new JPanel();//评价页面的答案信息

    int choice=0;
    /**
     *   @Description: 构造函数
     *   @Param: No, chapter, chapter_name, question, s_answer, choices
     *   @ParamTypes: String, String, String, String, String, String[4]
     *   @return:
     *   @Arthur: lisnail
     *   @Date: 2022/12/10
     **/
    public selectTopic(String No, String chapter, String chapter_name, String question, String s_answer, String[]choices){
        super(No, chapter,chapter_name,question, s_answer);
        type="选择题";
        setChoices(choices);
        answer="A";//默认答案为A
        BoxLayout boxLayout=new BoxLayout(this,BoxLayout.Y_AXIS);
        setLayout(boxLayout);

        add(ques_panel);

        for(int i=0;i<4;++i){
            JPanel choice_panel=new JPanel();
            JLabel new_l;
            if(i==0)
                new_l=new JLabel("A");
            else if(i==1)
                new_l=new JLabel("B");
            else if(i==2)
                new_l=new JLabel("C");
            else
                new_l=new JLabel("D");
            new_l.setFont(f);
            choices_b[i].setFont(f);
            choice_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            choice_panel.add(new_l);
            choice_panel.add(choices_b[i]);
            choices_p[i]=choice_panel;
            add(choices_p[i]);
            //input_panel.add(choice_panel);
        }
    }
    /**
    *   @Description: 设置四个选择
    *   @Param: 
    *   @ParamTypes: 
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    void setChoices(String[]choices){
        for(int i=0;i<4;++i){
            this.choices[i]=choices[i];
            String choice_str=JlabelSetText(choices[i]);//处理选项的换行
            choices_b[i]=new JRadioButton(choice_str);
            int finalI = i;
            choices_b[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    choice= finalI;
                    if(finalI==0)
                        answer="A";
                    else if(finalI==1)
                        answer="B";
                    else if(finalI==2)
                        answer="C";
                    else
                        answer="D";
                    System.out.println("题目"+No+"选择了"+choice);
                }
            });
            choices_g.add(choices_b[i]);
        }
        choices_b[0].setSelected(true);//默认选中第一个选项
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
        //System.out.println("添加章节");

        JLabel answer_l=new JLabel();
        answer_l.setFont(f);
        answer_l.setText("标准答案："+standard_ans+"  你的答案："+answer);
        answer_contrast.add(answer_l);

        removeAll();
        add(ques_panel);
        add(chapter_info_p);
        for(int i=0;i<4;++i) {
            add(choices_p[i]);
        }

        add(answer_contrast);

    }
}
