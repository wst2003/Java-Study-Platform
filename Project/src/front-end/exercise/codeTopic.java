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
 * @ClassName : codeTopic.java
 * @createTime : 2022/12/4 21:02
 * @Description : 编程题类，继承自blankTopic填空题类
 */
public class codeTopic extends blankTopic{
    /**
    *   @Description: 构造函数，只需要改变输入框的高度
     *   @Param: No, chapter, chapter_name, question, s_answer
     *   @ParamTypes: String, String, String, String, String
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/

    public codeTopic(String No, String chapter, String chapter_name, String question, String s_answer) {
        super(No, chapter, chapter_name, question, s_answer);
        type="编程题";
        js.setPreferredSize(new Dimension(Main_frame.size_d.width-600,600));
    }

}
