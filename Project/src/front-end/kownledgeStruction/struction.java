package kownledgeStruction;


import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : kownledgeStruction
 * @ClassName : struction.java
 * @createTime : 2022/12/6 15:45
 * @Description : 防止章节之间拓扑结构无向图的类
 */
public class struction {
    private HashSet<Vertex> nodeSet;

    public struction() {
        nodeSet = new HashSet<>();
    }

    public void addVertex(int chapter, int weight) {
        nodeSet.add(new Vertex(chapter, weight));
    }

    public void addEdge(int ch1, int ch2) {
        Vertex v1 = null;
        Vertex v2 = null;
        for (Vertex vertex : nodeSet) {//先找到对应章节的点，再构建相应的边
            if (vertex.chapter == ch1)
                v1 = vertex;
            else if (vertex.chapter == ch2) {
                v2 = vertex;
            }
        }
        Edge e = new Edge(v1, v2);
        v1.adj.add(e);
        v2.vs_adj.add(e);
    }

    public void print() {
        for (Vertex vertex : nodeSet) {
            for (Edge edge : vertex.vs_adj) {
                System.out.println(edge.v1.chapter + "--" + edge.v2.chapter);
            }
        }
    }

    /**
     * @Description: 获得指定章节的顶点 若未找到，返回null
     * @Param: [this_chapter]
     * @ParamTypes: [int]
     * @return: kownledgeStruction.Vertex
     * @Arthur: lisnail
     * @Date: 2022/12/6
     **/
    public Vertex getChapter(int this_chapter) {
        Vertex v = null;
        for (Vertex vertex : nodeSet) {
            if (vertex.chapter == this_chapter) {
                v = vertex;
                break;
            }
        }
        return v;
    }

    /**
     * @Description: 得到该章节的下一章节数组
     * 如果该章节没有后续章节，直接返回空数组
     * 若没有找到该章节，返回null
     * @Param: [this_chapter]
     * @ParamTypes: [int]
     * @return: ArrayList<Vertex>
     * @Arthur: lisnail
     * @Date: 2022/12/6
     **/
    public ArrayList<Vertex> getNextChapter(int this_chapter) {
        ArrayList<Vertex> next = new ArrayList<>();
        Vertex v = getChapter(this_chapter);
        if (v == null)//未找到该章节
            return null;
        for (Edge e : v.adj) {
            next.add(e.v2);
        }
        return next;
    }


    /**
     * @Description: 得到该章节的上一章节数组
     * 如果该章节没有之前章节，直接返回空数组
     * 若没有找到该章节，返回null
     * @Param: this_chapter
     * @ParamTypes: int
     * @return: ArrayList<Vertex>
     * @Arthur: lisnail
     * @Date: 2022/12/6
     **/
    public ArrayList<Vertex> getReviewChapter(int this_chapter) {
        ArrayList<Vertex> next = new ArrayList<>();
        Vertex v = getChapter(this_chapter);
        if (v == null)//未找到该章节
            return null;
        for (Edge e : v.vs_adj) {
            next.add(e.v1);
        }
        return next;
    }

    /**
    *   @Description: 返回各章节的权重
    *   @Param: 
    *   @ParamTypes: 
    *   @return: ArrayList<Integer>
    *   @Arthur: lisnail
    *   @Date: 2022/12/7
    **/
    public ArrayList<Integer> getWeights() {
        ArrayList<Integer> weights=new ArrayList<>();
        for(Vertex v:nodeSet){
            weights.add(v.weight);
        }
        return weights;
    }
}

class Vertex {

    int chapter;
    int weight;
    HashSet<Edge> adj;//邻接表,使用set防止重复边，散列提高搜索效率;为此，必须重写Edge类的equal与hashcode函数
    HashSet<Edge> vs_adj;//逆邻接表

    /**
     * @Description: 构造函数，必须指定章节以及权值
     * @Param: [ch]
     * @ParamTypes: [char]
     * @return:
     * @Arthur: lisnail
     * @Date: 2022/12/6
     **/
    Vertex(int chapter, int weight) {
        this.chapter = chapter;
        this.weight = weight;
        this.adj=new HashSet<>();
        this.vs_adj=new HashSet<>();
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)return false;//自己
        if(obj==null || getClass()!=obj.getClass())return false;//参数为空，或类名不同
        Vertex vertex=(Vertex) obj;
        return chapter== vertex.chapter;//类型转换后，比较两值
    }

    @Override
    public int hashCode() {
        return Objects.hash(chapter);//章节名为顶点的唯一标识符
    }


}

class Edge{
    Vertex v1;
    Vertex v2;
    /**
    *   @Description: 构造函数，必须指定起点与终点
    *   @Param: v1 , v2
    *   @ParamTypes: Vertex , Vertex
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    Edge(Vertex v1,Vertex v2){
        this.v1=v1;
        this.v2=v2;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)return false;//自己
        if(obj==null || getClass()!=obj.getClass())return false;//参数为空，或类名不同
        Edge edge=(Edge) obj;
        return Objects.equals(v1,edge.v1)&& Objects.equals(v2,edge.v2);//类型转换后，比较两值
    }

    @Override
    public int hashCode() {
        return Objects.hash(v1,v2);
    }
}

