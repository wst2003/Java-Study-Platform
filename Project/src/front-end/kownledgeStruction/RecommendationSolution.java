package kownledgeStruction;

import mainFrame.Main_frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;


/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : kownledgeStruction
 * @ClassName : RecommendationSolution.java
 * @createTime : 2022/12/6 21:05
 * @Description : 推荐算法总类，所有成员以及方法皆为static
 */
public class RecommendationSolution {

    public static ArrayList<Integer> nextChapter=new ArrayList<>();
    public static ArrayList<Integer> reviewChapter=new ArrayList<>();
    public static  Vector<Vector<String>> topicinfo=new Vector<>();



    /**
    *   @Description: 统一调用推荐算法
    *   @Param: currency this_chapter
    *   @ParamTypes: flaot int
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/6
    **/
    public static void setRecommend(float currency,int this_chapter){
        getTopicInfo();
        nextChapter.clear();
        reviewChapter.clear();
        ReviewRecommend(this_chapter);
        NextChapterRecommend(currency,this_chapter);
    }

    /**
    *   @Description: 统一调用综合复习算法
    *   @Param: 
    *   @ParamTypes:
    *   @return: Vector<Vector<Vector<String>>> 用于生成Examination页的题目
    *   @Arthur: lisnail
    *   @Date: 2022/12/9
    **/
    public static Vector<Vector<Vector<String>>> getComReviewTopics(){
        getTopicInfo();
        Vector<Vector<String>> topic_nums=comprehensiveReview();
        if(topic_nums.size()==0)return new Vector<Vector<Vector<String>>>();
        return Main_frame.socketManage.uploadReviewTopicNums(topic_nums);
    }
    /**
    *   @Description: 推荐下一章节的算法 若正确率不高于90%，则继续学习本章节
     *   若正确率高于90%但没有下一章节，则返回空数组
     *   若正确率高于90%，且下一章节只有一个，且下一章节学习的前提条件，除了学好当前章外还需要学习其他章节，
     *   则推荐没有被学习的其他章节。若其他章节都已被学习，不管学习好坏，直接推荐下一章节，
     *   并将其中需要推荐复习的章节添加到复习数组里。
    *   @Param: [currency, this_chapter]
    *   @ParamTypes: [float, int]
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/6
    **/
    public static void NextChapterRecommend(float currency, int this_chapter){
        ArrayList<Vertex> next_chapters ;
        ArrayList<Integer> res=new ArrayList<>();
        if(currency<0.90f){
            res.add(this_chapter);
            nextChapter=res;
            return;
        }
        next_chapters= Main_frame.chapter_map.getNextChapter(this_chapter);
        if(next_chapters.size()==1&&next_chapters.get(0).vs_adj.size()>1){
            //若邻接点只有一个，且该邻接点的逆邻接表中含有另外的章节
            //拉正确率，若另外的章节全部都已学习，则直接推荐该邻接点
            //若有未学习的章节，则推荐这些未学习的章节
            for(Edge e:next_chapters.get(0).vs_adj){//遍历逆邻接表
                if(e.v1.chapter!=this_chapter){//找到与当前章节平行的章节
                    if(Float.valueOf(topicinfo.get(e.v1.chapter - 1).get(0))==-1.0f)//如果该章节没有被学习，则推荐学习该章节
                        res.add(e.v1.chapter);
                    else {
                        if(reviewWeight(e.v1.chapter)<0.9f){//如果需要复习，加入推荐复习数组
                            reviewChapter.add(e.v1.chapter);
                        }
                    }

                }

            }
            if(res.size()==0)res.add(next_chapters.get(0).chapter);//如果遍历过逆邻接表，res数组中仍没有章节，说明可以学习下一章节。
        }
        nextChapter=res;
    }

    /**
    *   @Description: 获取做题记录，在推荐算法之前必须调用
    *   @Param: 
    *   @ParamTypes: 
    *   @return: 
    *   @Arthur: lisnail
    *   @Date: 2022/12/6
    **/
    public static void getTopicInfo(){
        topicinfo=Main_frame.socketManage.getTopicInfo(Main_frame.userID);
    }

    /**
    *   @Description: 获取推荐复习的章节
    *   @Param: this_chapter
    *   @ParamTypes: int
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/6
    **/
    public static void ReviewRecommend(int this_chapter){
        ArrayList<Vertex> review_chapters= Main_frame.chapter_map.getReviewChapter(this_chapter);
       for(int i=0;i<review_chapters.size();++i){
           if(reviewWeight(review_chapters.get(i).chapter)<0.9f){
                reviewChapter.add(review_chapters.get(i).chapter);
           }
       }
    }

    /**
    *   @Description: 返回该章节加权后的复习权重
    *   @Param: this_chapter
    *   @ParamTypes: int
    *   @return: float
    *   @Arthur: lisnail
    *   @Date: 2022/12/6
    **/
    public static float reviewWeight(int this_chapter){
        Vector<String>info= topicinfo.get(this_chapter-1);
        float res_weight;
        float[] info_f=new float[3];
        int num=0;
        for(int i=0;i<3;++i){
            info_f[i]=Float.valueOf(info.get(i));
            if(info_f[i]!=-1.0f)
                ++num;
        }
        if(num==3){
            res_weight=0.5f*info_f[0]+0.3f*info_f[1]+0.2f*info_f[2];
        }
        else if(num==2){
            res_weight=0.7f*info_f[0]+0.3f*info_f[1];
        }
        else if(num==1){
            res_weight=info_f[0];
        }
        else res_weight=0f;
        return res_weight;
    }

    /**
    *   @Description: 根据所有章节题目的正确率，获取将要发送到服务器的拉取题目用数组
     *   根据每章节的权重以及该章节之前所有做题的正确率，得到每章节究竟应该获得多少题目
    *   @Param: 
    *   @ParamTypes: 
    *   @return: Vector<Vector<String>>
    *   @Arthur: lisnail
    *   @Date: 2022/12/9
    **/
    public static Vector<Vector<String>>  comprehensiveReview(){
        Vector<Vector<String>> res=new Vector<>();//最终上传服务器的，用于拉取题目的数组
        ArrayList<Float> floats=new ArrayList<>();//每一章的总正确率
        for(int i=0;i< topicinfo.size();++i){
            floats.add(Float.valueOf(topicinfo.get(i).get(3)));
        }
        ArrayList<Integer> weights=Main_frame.chapter_map.getWeights();//每一章的权重
        ArrayList<Float> num_weights=new ArrayList<>();//错误率乘以权重
        float total_weight=0f;//总权值
        int chapter_nums=0;//综合复习应该包含的章节数
        for(int i=0;i<floats.size();++i){
            if(floats.get(i)>=0){
                num_weights.add((1f-floats.get(i))*(float)weights.get(i));
                total_weight+=(1f-floats.get(i))*(float)weights.get(i);
                chapter_nums+=1;
            }
            else{
                num_weights.add(-1f);
            }
        }
        int pre_topic_nums=0;//每章应该有多少道题目
        if(chapter_nums==0)return res;//若还没有学习，直接返回空数组
        else if(chapter_nums==1)pre_topic_nums=10;
        else if(chapter_nums<4)pre_topic_nums=20;
        else if (chapter_nums<7)pre_topic_nums=30;
        else pre_topic_nums=40;
        ArrayList<ArrayList<Integer>> nums=new ArrayList<>();
        for(int i=0;i<num_weights.size();++i){
            if(num_weights.get(i)>0) {
                ArrayList<Integer> new_chaptopic = new ArrayList<>();
                new_chaptopic.add(i + 1);
                new_chaptopic.add(Math.round(num_weights.get(i) / total_weight * (float) pre_topic_nums));
                System.out.println(new_chaptopic.get(0) + "章" + new_chaptopic.get(1) + "题");
                //根据权重占总权重的比，乘以预计总题数再向上取整，得到该章节的题目个数
                nums.add(new_chaptopic);
            }
        }
        res= setTopicVector(nums);
        return res;
    }

    /**
    *   @Description: 根据每章节应该设置几道题目，返回将要上传到服务器的题目数组
    *   @Param:  nums
    *   @ParamTypes: ArrayList<Integer>
    *   @return: Vector<Vector<String>>
     *       一维的数量表示题目的数量
     *       二维包括2个元素，第一个为章节序号，第二个为题目类型
    *   @Arthur: lisnail
    *   @Date: 2022/12/8
    **/
    private static Vector<Vector<String>> setTopicVector(ArrayList<ArrayList<Integer>> topic_nums) {
        ArrayList<ArrayList<Integer>> topic_diffnums=new ArrayList<>();
        //存放不同章节不同类型的题目需要多少道
        //第二维的第一个元素是章节号，之后四个元素分别是选择题、判断题、填空题、编程题的数量
        Vector<Vector<String>> upload_vec=new Vector<>();
        //System.out.println("将添加"+topic_nums.size()+"章节的题目");
        for(int i=0;i<topic_nums.size();++i){
            ArrayList<Integer> this_diffnums=new ArrayList<>(Collections.nCopies(5,0));
            this_diffnums.set(0,topic_nums.get(i).get(0));//章节号
            int total_num=topic_nums.get(i).get(1);
            if(total_num>=5){//若该章节题目总数超过5道，则拉取一道编程题
                this_diffnums.set(4,1);
                total_num-=1;
            }
            else
                this_diffnums.set(4,0);
            //根据题目数量，尽量均分选择题、判断题、填空题的数量
            if(total_num%3==0){
                this_diffnums.set(1,total_num/3);
                this_diffnums.set(2,total_num/3);
                this_diffnums.set(3,total_num/3);
            }
            else if(total_num%3==1){
                this_diffnums.set(1,total_num/3+1);
                this_diffnums.set(2,total_num/3);
                this_diffnums.set(3,total_num/3);
            }
            else if(total_num%3==2){
                this_diffnums.set(1,total_num/3+1);
                this_diffnums.set(2,total_num/3+2);
                this_diffnums.set(3,total_num/3);
            }
            topic_diffnums.add(this_diffnums);
        }
        //System.out.println("将添加"+topic_diffnums.size()+"章节的题目");
        //转换为上传服务器用的数组
        for(int i=0;i<topic_diffnums.size();++i){
            for(int j=0;j<topic_diffnums.get(i).get(1);++j){
                Vector<String>topic_selects=new Vector<>();
                topic_selects.add(String.valueOf(topic_diffnums.get(i).get(0)));
                topic_selects.add("选择题");
                upload_vec.add(topic_selects);
            }
            for(int j=0;j<topic_diffnums.get(i).get(2);++j){
                Vector<String>topic_judges=new Vector<>();
                topic_judges.add(String.valueOf(topic_diffnums.get(i).get(0)));
                topic_judges.add("判断题");
                upload_vec.add(topic_judges);
            }
            for(int j=0;j<topic_diffnums.get(i).get(3);++j){
                Vector<String>topic_blanks=new Vector<>();
                topic_blanks.add(String.valueOf(topic_diffnums.get(i).get(0)));
                topic_blanks.add("填空题");
                upload_vec.add(topic_blanks);
            }
            for(int j=0;j<topic_diffnums.get(i).get(4);++j){
                Vector<String>topic_codes=new Vector<>();
                topic_codes.add(String.valueOf(topic_diffnums.get(i).get(0)));
                topic_codes.add("编程题");
                upload_vec.add(topic_codes);
            }
        }
        return upload_vec;
    }
}
