package server;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 *
 * @version : 1.0
 * @Project : LeaningPlatform
 * @Package : SendQuestions
 * @ClassName : SocketManaging.DataByte.java
 * @createTime : 2022/12/3 20:53
 * @Description : 用于在服务器与客户端之间传递二维字符串数组
 */
public class DataByte implements Serializable{
    private static final long serialVersionUID = 6529685098267757690L;
    public Vector<Vector<String>> vector = new Vector<Vector<String>>();
}

