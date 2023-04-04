package exercise;

import mainFrame.Main_frame;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : exercise
 * @ClassName : exercise.Topic.java
 * @createTime : 2022/11/30 22:01
 * @Description :Topic是所有题目的抽象父类，继承自JPanel。
 *      已有成员：
 *      String No;//题号
 *      String chapter;//所属章节标号
 *      String chapter_name;//所属章节名称
 *      String question;//题干
 *      String standard_ans;//标准答案
 *      String answer;//用户的答案
 *      boolean correct=false;//该题正误
 *      String type="";//题目种类
 *      int show_No;//做题页面应该显示的题号
 */
abstract public class Topic extends JPanel {
    String No;//题号
    String chapter;//所属章节标号
    Font f=new Font("宋体",Font.PLAIN,20);//默认字体
    String chapter_name;//所属章节名称
    String question;//题干
    String standard_ans;//标准答案
    String answer;//用户的答案
    boolean correct=false;//该题正误
    JLabel ques=new JLabel();
    JPanel ques_panel=new JPanel();
    JPanel input_panel=new JPanel();
    String type="";//题目种类
    int show_No;//做题页面应该显示的题号
    /**
    *   @Description: 构造函数，整合一些需要在所有题目UI子类需要做的预备工作
     *  @Param: No, chapter, chapter_name, question, s_answer 
     *  @ParamTypes: String, String, String, String, String
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/

    public Topic(String No, String chapter, String chapter_name, String question, String s_answer){
        this.question=question;
        this.No=No;
        standard_ans=s_answer;
        this.chapter=chapter;
        this.chapter_name=chapter_name;
        ques.setFont( f.deriveFont(Font.BOLD));//在获取字符串之前，设置字体，因为字体需要在JlabelSetText中使用
        input_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    /**
     * abstract
     * 点击提交按钮后，题目需要调用该函数，实现：
     * 1.切换显示界面
     * 2.批改，即修改correct字段
     */
    abstract public void switchJudging();//切换到学习评价页面，必须实现
    /**
    *   @Description: 设置题目在刷题页面应该显示的题号
    *   @Param: show_No
    *   @ParamTypes: int
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public void setShow_No(int show_No) {
        this.show_No = show_No;
        String new_str=JlabelSetText(show_No+"  "+question);
        int br_num=new_str.split("<br/>").length;//有多少行
        ques.setText(new_str);
        ques_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //根据题干有多少行来设置高度
        ques_panel.setPreferredSize(new Dimension(Main_frame.size_d.width-40,ques.getFontMetrics(f).getHeight()*br_num));
        ques_panel.add(ques);
    }
    /**
    *   @Description: 换行函数，使输入的字符串转换为可以在JLable中自动换行的字符串。在任何子类中都可以调用
    *   @Param: longString
    *   @ParamTypes: String
    *   @return: String
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public String JlabelSetText(String longString){
        StringBuilder builder = new StringBuilder("<html>");
        builder.append("<br/>");//与上一题拉开间距
        FontMetrics fontMetrics = ques.getFontMetrics(ques.getFont());
        String [] res_split=longString.split("\n");
        for(int i=0;i<res_split.length;++i) {
            char[] chars = res_split[i].toCharArray();
            int start = 0;
            int len = 0;
            while (start + len < res_split[i].length()) {
                while (true) {
                    len++;
                    if (start + len > res_split[i].length())
                        break;//遍历字符串完成
                    if (fontMetrics.charsWidth(chars, start, len) > Main_frame.size_d.width - 40)
                        break;//超过指定宽度
                }
                builder.append(chars, start, len - 1).append("<br/>");
                start = start + len - 1;
                len = 0;
            }
            builder.append(chars, start, res_split[i].length() - start);
        }
        builder.append("</html>");
        return builder.toString();
    }
}
