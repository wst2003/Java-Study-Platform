package learning;

import exercise.Examination_page;
import kownledgeStruction.RecommendationSolution;
import mainFrame.Main_frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : learning
 * @ClassName : learning.Learning_page.java
 * @createTime : 2022/11/22 10:43
 * @Description : 知识学习页面
 */
public class Learning_page extends JPanel {
    static JPanel dir_panel=new JPanel();//目录面板
    JButton review_b;//综合复习按钮
    static JButton exercise_b;//每章的刷题按钮
    static ImageIcon[] imageIcons;//当前小节的所有图片，用于在getImage()
    static Vector<byte[]> imageBytes;//当前小节的所有图片的二进制流
    static JScrollPane dir_scroll;//左侧的目录树
    static JScrollPane pic_scroll;//右侧的图片栏
    static JButton next_page;//右侧面板的下一页
    static JButton prev_page;//左侧面板的下一页
    static PicPanel pic_panel=new PicPanel();//知识面板，装载一个章节的所有图片
    static String section_name="";//当前知识面板显示的小节名，形式为大章名-小章名
    public static Vector<Vector<String>>dir_name_vec=new Vector<>();//目录数组的vector形式
    static Vector<Byte>flag_vec=new Vector<>();//记录每个目录是否展开
    static Vector<JButton> dirTree=new Vector<>();//使用vector表示目录树，当前所有需要显示的章节按钮
    static Vector<JButton> chapter_buttons=new Vector<>();//大章按钮的数组，用于获得序号
    static Vector<Vector<JButton>> jButtons_vec=new Vector<>();//预设置所有小节的按钮
    static Vector<Integer> page_flags;
    /**
    *   @Description: 构造函数，设置UI
    *   @Param: 
    *   @ParamTypes: 
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public Learning_page(){
        setLayout(null);
        review_b=new JButton("综合复习");
        dir_scroll=new JScrollPane(dir_panel);
        pic_scroll=new JScrollPane(pic_panel);
        pic_scroll.setBounds(400,0,Main_frame.size_d.width-410,Main_frame.size_d.height-60);
        pic_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dir_scroll.setBounds(0,0,400, Main_frame.size_d.height-120);
        dir_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);//禁用左右滑块
        dir_panel.setPreferredSize(new Dimension(400, 5000));
        pic_panel.setPreferredSize(new Dimension(Main_frame.size_d.width-410, 1400));

        review_b.setBounds(0,Main_frame.size_d.height-120,400,60);
        review_b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //综合复习界面
                Vector<Vector<Vector<String>>> questions=RecommendationSolution.getComReviewTopics();
                System.out.println("获取综合复习习题成功");
                if(questions.size()==0){
                    review_b.setText("请先学至少一章，再综合复习！");
                }
                else {
                    review_b.setText("综合复习");
                    Examination_page.setPage(questions, "");
                    Main_frame.SwitchPanel("examination");
                }
            }
        });
        add(dir_scroll);
        add(review_b);
        add(pic_scroll);
        validate();
        repaint();
        dir_panel.setLayout(new FlowLayout());
        pic_panel.setLayout(null);
    }
/**
*   @Description: 根据服务器拉下来的章节数组，确定有多少章节，并设置为类成员dir_name_vec，之后调用其他方法设置目录树
*   @Param: raw
*   @ParamTypes: Vector<Vector<String>>
*   @return: 
*   @Arthur: lisnail
*   @Date: 2022/12/10
**/
    public static void setDirVec(Vector<Vector<String>> raw){
        String chapter="";
        Vector<Vector<String>> dir_vec=new Vector<>();
        Vector<String> chapter_vec=new Vector<>();
        for(int i=0;i<raw.size();++i){
            if(chapter.equals(raw.get(i).get(0))==false && i!=0) {//新的一大章
                System.out.println(raw.get(i).get(0)+"对比"+chapter);
                dir_vec.add(chapter_vec);//之前的大章数组进入章节数组
                chapter_vec=new Vector<>();//重置大章数组
                chapter = raw.get(i).get(0);
                chapter_vec.add(chapter);//新的大章数组，第0个元素为大章名
                chapter_vec.add(raw.get(i).get(1));//之后为各小章名
            }
            else if(i==0){//若是第一个小章，则相当于大章改变
                    chapter=raw.get(i).get(0);
                    chapter_vec.add(chapter);
                    chapter_vec.add(raw.get(i).get(1));
            }
            else{//大章没有发生改变，在大章数组中添加小章名
                chapter_vec.add(raw.get(i).get(1));
            }
        }
        dir_vec.add(chapter_vec);

        dir_name_vec=dir_vec;
        for(int i=0;i<raw.size();++i){
            System.out.println(raw.get(i).get(0)+"章"+raw.get(i).get(1));
        }
        for(int i=0;i<dir_name_vec.size();++i){
            for(int j=0;j<dir_name_vec.get(i).size();++j){
                System.out.println(dir_name_vec.get(i).get(j));
            }
        }
        setDirTree();
    }
    /**
    *   @Description: 设置目录树，并提前存储好所有小节的切换按钮，方便以后随意对目录树进行折叠与展开
    *   @Param: 
    *   @ParamTypes: 
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    private static void setDirTree(){
        Vector<Byte>flag_vec=new Vector<Byte>();//重置flag数组
        Vector<Vector<JButton>> jButtons_vec=new Vector<>();//重置小节数组
        for(int i=0;i < dir_name_vec.size();++i) {//i表示第几章节
            JButton chapter_i=new JButton(String.valueOf(i+1) +"."+dir_name_vec.get(i).get(0));
            chapter_i.setPreferredSize(new Dimension(dir_scroll.getWidth()-50,50));
            dir_panel.add(chapter_i);
            dirTree.add(chapter_i);//将按钮记录进目录树

            chapter_buttons.add(chapter_i);//记录按钮位置
            flag_vec.add((byte)0);//置为未展开

            chapter_i.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    refreshDirTree(e);
                }
            });
            Vector<JButton> jButtons=new Vector<>();//该大章的所有小章按钮
            for(int j=1;j<dir_name_vec.get(i).size();++j){//j表示第几小节
                JButton section_j=new JButton(String.valueOf(i+1)+"-"+j+" "+dir_name_vec.get(i).get(j));
                section_j.setPreferredSize(new Dimension(dir_scroll.getWidth()-100,50));
                jButtons.add(section_j);
                //预先将定制好的小节按钮放置在数组中
                int finalI = i+1;
                int finalJ = j;
                section_j.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sectionSwitch(finalI +"-"+ finalJ);
                    }
                });
            }
            jButtons_vec.add(jButtons);
        }
        Learning_page.flag_vec=flag_vec;
        Learning_page.jButtons_vec=jButtons_vec;
    }
    /**
    *   @Description: 绑定在大章按钮之上，展开或折叠该大章的所有小节
    *   @Param: e
    *   @ParamTypes: ActionEvent
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    private static void refreshDirTree(ActionEvent e){
        int index_indirTree=dirTree.indexOf(e.getSource());//找到按钮在目录树中的坐标值
        int index_chapter=chapter_buttons.indexOf(e.getSource());//找到序号
        if(index_indirTree!=-1){//在目录树内
            System.out.println("按钮在目录树内");
            if(flag_vec.get(index_chapter)==0){
                System.out.println("章节展开");
                for(int i=0;i<jButtons_vec.get(index_chapter).size();++i){
                    dirTree.add(++index_indirTree,jButtons_vec.get(index_chapter).get(i));
                }
                flag_vec.set(index_chapter, (byte) 1);//重置
            }
            else {
                System.out.println("章节收缩");
                for(int i=0;i<jButtons_vec.get(index_chapter).size();++i){
                    dirTree.remove(index_indirTree+1);
                }
                flag_vec.set(index_chapter, (byte) 0);//重置
            }
            repaintDirTree();
        }
        else{
            System.out.println("按钮不在目录树内");
        }
    }
    /**
    *   @Description: 重绘目录树，在refreshDirTree方法中调用
    *   @Param: 
    *   @ParamTypes: 
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    static void repaintDirTree(){
        dir_panel.removeAll();
        for(int i=0;i<dirTree.size();++i){
            dir_panel.add(dirTree.get(i));
        }
        dir_panel.validate();
        dir_panel.repaint();
    }
    /**
    *   @Description: 按下小节按钮，切换右侧的图片界面。在显示第一个图片的同时，另开一个线程加载其他图片，提高使用体验
    *   @Param: section_name
    *   @ParamTypes:  String
     *  @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    private static void sectionSwitch(String section_name){//   section_name的格式为“大章-小章”
        pic_scroll.getVerticalScrollBar().setValue(0);
        System.out.println("选择了当前小节");
        pic_panel.removeAll();
        Learning_page.section_name=section_name;

        imageBytes=Main_frame.socketManage.getImage("chapterfile-"+section_name);
        imageIcons=new ImageIcon[imageBytes.size()];
        page_flags = new Vector<Integer>(Collections.nCopies(imageBytes.size(), 0));

        exercise_b=new JButton("开始练习本章习题");
        next_page=new JButton("下一页");
        prev_page=new JButton("上一页");
        exercise_b.setBounds(480,1280,150,30);
        next_page.setBounds(1000,1280,80,30);
        prev_page.setBounds(0,1280,80,30);
        pic_panel.add(exercise_b);
        pic_panel.add(next_page);
        pic_panel.add(prev_page);
        long start=System.currentTimeMillis();

        imageIcons[0]=new ImageIcon(imageBytes.get(0));
        imageIcons[0].setImage(imageIcons[0].getImage().getScaledInstance(960,1200,Image.SCALE_AREA_AVERAGING));
        long end=System.currentTimeMillis();
        System.out.println("一张图片用时："+(end-start));
        page_flags.set(0,1);
        pic_panel.resetPic(imageIcons[0],0);
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for(int i=1;i<imageIcons.length;++i) {
                    long start = System.currentTimeMillis();
                    imageIcons[i]=new ImageIcon(imageBytes.get(i));
                    imageIcons[i].setImage(imageIcons[i].getImage().getScaledInstance(960,1200,Image.SCALE_AREA_AVERAGING));
                    long end = System.currentTimeMillis();
                    System.out.println("一张图片用时：" + (end - start));
                    page_flags.set(i,1);
                }
            }
        }, 0);


        prev_page.setEnabled(false);

        pic_panel.repaint();
        System.out.println("第0个图片加载完成");

        exercise_b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Examination_page.setPage(Main_frame.socketManage.getTopics("test-"+ section_name.split("-")[0]),section_name.split("-")[0]);
                Main_frame.SwitchPanel("examination");
                //点击每章的刷题按钮，首先设置刷题界面的题目数组，其次设置章节,然后设置题目数据
            }
        });

        next_page.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               switch_page(e);
            }
        });
        prev_page.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch_page(e);
            }
        });
    }
    /**
    *   @Description: 绑定在下一页或上一页按钮上的方法，用于切换同一小节下的不同图片。
    *   @Param: ActionEvent
    *   @ParamTypes: e
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public static void switch_page(ActionEvent e)  {
        pic_scroll.getVerticalScrollBar().setValue(0);
        System.out.print("切换页面");
        int present_no=pic_panel.getNo();
        if(e.getSource()==prev_page) {
            if (present_no > 0) {
                present_no--;
                next_page.setEnabled(true);
                if(present_no==0)
                    prev_page.setEnabled(false);
            }
            System.out.println("上一张图片");
        }
        else if(e.getSource()==next_page) {
            if (present_no < imageIcons.length - 1) {
                present_no++;
                prev_page.setEnabled(true);
                if(present_no==imageIcons.length - 1) {
                    next_page.setEnabled(false);
                    System.out.println("设置next按钮无效");
                }
            }
            System.out.println("下一张图片");
        }
        while(page_flags.get(present_no)==0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        pic_panel.resetPic(imageIcons[present_no],present_no);
        System.out.println("第" + present_no + "个图片加载完成");
        pic_panel.repaint();
    }
}

/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : learning
 * @ClassName : learning.PicPanel
 * @createTime : 2022/11/22 10:43
 * @Description : 用于在知识页面中绘图，属于pic_scorll，其中包含绘图域以及三个按钮
 */
class PicPanel extends JPanel  {
    private Image image;
    private int imgWidth=0;
    private int imgHeight=0;
    private int No=0;

    public void resetPic(ImageIcon new_pic,int No){
        image=new_pic.getImage();
        imgWidth=image.getWidth(this);
        imgHeight=image.getHeight(this);
        this.No=No;
    }
    public int getNo(){return No;}

    public int getImgWidth() {
        return imgWidth;
    }
    public int getImgHeight() {
        return imgHeight;
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image,50,0,getImgWidth() , getImgHeight() ,this);
    }
}

