package exercise;

import learning.Learning_page;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : exercise
 * @ClassName : Summary_frame.java
 * @createTime : 2022/12/4 21:49
 * @Description :总结页面 继承JFrame
 */
public class Summary_frame extends JFrame {
    String used_time="";//共计用时
    float currency=0;//正确率
    ArrayList<Integer> nextChapter;
    ArrayList<Integer> reviewChapter;
    Font f=new Font("宋体",Font.PLAIN,25);

    /**
    *   @Description: 构造方法，在任意章节的习题完成后调用，显示推荐学习路径以及推荐复习章节
    *   @Param: used_time, currency, nextChapter, reviewChapter
    *   @ParamTypes: String, float, ArrayList<Integer>, ArrayList<Integer>
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public Summary_frame(String used_time, float currency, ArrayList<Integer> nextChapter, ArrayList<Integer> reviewChapter) {
        this.used_time=used_time;
        this.currency=currency;
        this.nextChapter=nextChapter;
        this.reviewChapter=reviewChapter;
        BoxLayout boxLayout=new BoxLayout(getContentPane(),BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        setSize(600,400);
        setVisible(true);


        String next_chapter="<html>推荐学习：<br/>";
        System.out.println("推荐复习"+nextChapter.size()+"章");
        for(int i=0;i<nextChapter.size();++i){
            next_chapter+= "第"+nextChapter.get(i)+"章： "+Learning_page.dir_name_vec.get(nextChapter.get(i)-1).get(0)+"<br/>";
        }
        String review_chapter="<html>推荐复习：<br/>";
        System.out.println("推荐复习"+reviewChapter.size()+"章");
        for(int i=0;i<reviewChapter.size();++i){
            review_chapter+= "第"+reviewChapter.get(i)+"章： "+Learning_page.dir_name_vec.get(reviewChapter.get(i)-1).get(0)+"<br/>";
        }

        JLabel time=new JLabel("本次做题共计用时："+this.used_time);
        JLabel curr=new JLabel("本次做题正确率："+ String.format("%.1f", this.currency*100)+"%");
        JLabel next=new JLabel(next_chapter);
        JLabel review=new JLabel(review_chapter);

        time.setFont(f);
        curr.setFont(f);
        next.setFont(f);
        review.setFont(f);

        add(time);
        add(curr);
        add(next);
        add(review);
    }
    /**
     *   @Description: 构造方法，在综合复习完成之后调用
     *   @Param: used_time, currency
     *   @ParamTypes: String, float
     *   @return:
     *   @Arthur: lisnail
     *   @Date: 2022/12/10
     **/
    public Summary_frame(String used_time, float currency) {
        this.used_time=used_time;
        this.currency=currency;
        setLocationRelativeTo(null);
        BoxLayout boxLayout=new BoxLayout(getContentPane(),BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        setSize(600,400);
        setVisible(true);
        JLabel time=new JLabel("本次做题共计用时："+this.used_time);
        JLabel curr=new JLabel("本次做题正确率："+String.format("%.1f", this.currency*100)+"%");
        time.setFont(f);
        curr.setFont(f);
        add(time);
        add(curr);
    }
}
