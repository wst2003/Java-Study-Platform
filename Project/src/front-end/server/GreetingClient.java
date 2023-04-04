package server;


import java.net.*;
import java.io.*;
import java.util.Vector;

public class GreetingClient
{
    DataOutputStream out;//socket输出流
    DataInputStream in;//socket输入流
    Socket client;//连接到服务器的接口类
    /**
    *   @Description: 在构造函数中，试图连接服务器。尝试服务器的多个端口
    *   @Param:
    *   @ParamTypes:
    *   @return:
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public GreetingClient(String [] args){
        //String serverName = "123.249.36.173";//服务器IP地址，未来进行修改
        int port = 6000;//服务器端口,未来改成6000
        String serverName = "localhost";//本机测试地址
        while(true) {
            try {
                client = new Socket(serverName, port);
                System.out.println("远程网络地址" + serverName + " 端口" + port);
                System.out.println("地址" + client.getRemoteSocketAddress());
                //建立Socket
                OutputStream outToServer = client.getOutputStream();
                out = new DataOutputStream(outToServer);
                //socket输出流
                InputStream inFromServer = client.getInputStream();
                in = new DataInputStream(inFromServer);


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (in.available() == 0) {
                    port += 1;
                }
                else {
                    System.out.println(in.readUTF());
                    break;
                }
            } catch (IOException e) {
                port+=1;
            }
        }
    }

    /**
     * @description: 上传字符串到服务器，得到返回的字符串
     * @param upload_string
     * @return String
     * @author: lisnail
     * @date: 2022.11.18
     */
    public String upload(String upload_string) throws IOException {
        out.writeUTF(upload_string);
        String return_string=in.readUTF();
        System.out.println(return_string);
        return return_string;
    }

    /**
     * @description: 上传指令字符串到服务器，得到返回的二进制流图片数组
     * @param getImage_string
     * @return Vector<byte[]>
     * @author: lisnail
     * @date: 2022.11.18
     */
    public Vector<byte[]> getImage(String getImage_string) {
        Vector<byte[]>imageBytes=new Vector<>();
        try {
            out.writeUTF(getImage_string);
            int len;//图片大小
            while ((len = in.readInt()) != -1)    //当输入流里还有图片
            {
                byte[] imageByte = new byte[len];      //创建图片的byte数组
                in.readFully(imageByte);        //写入，默认大小为len
                imageBytes.add(imageByte);
            }
            System.out.println(getImage_string+"章节录入成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBytes;
    }
    /**
     * @description: 上传指令字符串到服务器，得到返回的题目信息
     * @param getTopics_string
     * @return Vector<Vector<Vector<String>>>
     *     第一维指题型，0为选择题，1为判断题，2为填空题
     *     第二维指题目，它的size表示该类型有多少题目
     *     第三维表示题目具体信息：
     *              选择题：章节 章节名 题号 题干 A B C D 答案
     *              判断题：章节 章节名 题号 题干 答案
     *              填空题：章节 章节名 题号 题干 答案
     *              编程题：章节 章节名 题号 题干 答案
     *      若返回值为null，则拉取题目信息失败
     * @author: lisnail
     * @date: 2022.12.2
     */
    public Vector<Vector<Vector<String>>> getTopics(String getTopics_string)  {
        Vector<Vector<Vector<String>>> topics = new Vector<Vector<Vector<String>>>();
        try {
            out.writeUTF(getTopics_string);
            for(int i=0;i<4;++i) {
                int size = in.readInt();    //大小
                byte QuestionsBytes[] = new byte[size];
                int bytesRead = 0;
                int bytesToRead = size;
                while (bytesRead < bytesToRead) {
                    bytesRead += in.read(QuestionsBytes, bytesRead, bytesToRead - bytesRead);
                }
                //获取题目数据
                System.out.println("获取了一类题目");
                DataByte shaper = (DataByte) deserialize(QuestionsBytes);
                for(int j = 0;j<shaper.vector.size();j++) {
                    for(int k = 0;k<shaper.vector.elementAt(j).size();k++) {
                        System.out.print(shaper.vector.elementAt(j).elementAt(k)+" ");
                    }
                    System.out.println();
                }
                topics.add(shaper.vector);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return topics;
    }


    /**
     * @description: 获取数据库中的所有章节名组成的数组
     * @param
     * @return Vector<Vector<String>>
     *      章节名数组，一维大小为小章节总数，
     *      二维包含两个元素，第一个为大章节名，第二个为小章节名
     * @author: lisnail
     * @date: 2022.12.5
     */
    public Vector<Vector<String>> getChapterNames(){
        Vector<Vector<String>> chapterNames=null;
        try {
            out.writeUTF("chapterName");
            int size = in.readInt();    //大小
            System.out.println(size);
            byte QuestionsBytes[] = new byte[size];
            int bytesRead = 0;
            int bytesToRead = size;
            while (bytesRead < bytesToRead) {
                bytesRead += in.read(QuestionsBytes, bytesRead, bytesToRead - bytesRead);
            }
            System.out.println("获取了章节名数组");
            DataByte shaper = (DataByte) deserialize(QuestionsBytes);
            chapterNames=shaper.vector;
        }catch (IOException e){
            e.printStackTrace();
        }
        return chapterNames;
    }

    /**
    *   @Description: 返回全部做题记录
    *   @Param: 
    *   @ParamTypes: 
    *   @return: Vector<Vector<String>>
     *       一维是章节总数
     *       二维包含四个元素，是该章节最近三次的刷题正确率，以及该章节的总正确率，均用float转String。
     *       第一个元素是最近一次，第二个元素稍远，以此类推
     *       若没有刷题记录，返回-1.0f转String
    *   @Arthur: lisnail
    *   @Date: 2022/12/6
    **/
    public Vector<Vector<String>> getTopicInfo(String username){
        Vector<Vector<String>> topicinfo=new Vector<>();
        try {
            out.writeUTF("gettopicinfo-"+username);
            int size = in.readInt();    //大小
            byte QuestionsBytes[] = new byte[size];
            int bytesRead = 0;
            int bytesToRead = size;
            Thread.sleep(500);
            while (bytesRead < bytesToRead) {
                bytesRead += in.read(QuestionsBytes, bytesRead, bytesToRead - bytesRead);
            }

            //获取题目数据
            System.out.println("获取了以往做题记录");
            DataByte shaper = (DataByte) deserialize(QuestionsBytes);
            topicinfo=shaper.vector;
        }catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return topicinfo;
    }
    /**
     * @description: 上传题目信息的数组
     * @param info
     * @return void
     * @author: lisnail
     * @date: 2022.12.4
     */
    public void uploadTopicInfo(Vector<Vector<String>> info)  {
        DataByte data=new DataByte();
        data.vector=info;
        byte[] bytes=getBytesFromObject(data);
        try {
            out.writeUTF("topicinfo");
            out.writeInt(bytes.length);
            out.write(bytes);
        } catch (IOException e) {
           e.printStackTrace();
        }
    }


    /**
    *   @Description: 综合复习模块，上传每章节应该获取多少道题，服务器返回题目信息数组，数组架构同getTopics
    *   @Param: nums
    *   @ParamTypes: ArrayList<Integer>
    *   @return: Vector<Vector<Vector<String>>>
    *   @Arthur: lisnail
    *   @Date: 2022/12/7
    **/
    public Vector<Vector<Vector<String>>> uploadReviewTopicNums(Vector<Vector<String>> info){
        DataByte data=new DataByte();
        data.vector=info;
        byte[] bytes=getBytesFromObject(data);
        try {
            out.writeUTF("topicnums");
            out.writeInt(bytes.length);
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Vector<Vector<Vector<String>>> topics = new Vector<Vector<Vector<String>>>();
        Vector<Vector<String>> selecttopics = new Vector<Vector<String>>();
        Vector<Vector<String>> judgetopics = new Vector<Vector<String>>();
        Vector<Vector<String>> blanktopics = new Vector<Vector<String>>();
        Vector<Vector<String>> codetopics = new Vector<Vector<String>>();

        try {
            System.out.println("打开题目输入流"+in.available());
            int size = in.readInt();    //大小
            System.out.println("打开题目输入流"+in.available());
            System.out.println(size);
            byte QuestionsBytes[] = new byte[size];
            int bytesRead = 0;
            int bytesToRead = size;

            while (bytesRead < bytesToRead) {
                bytesRead += in.read(QuestionsBytes, bytesRead, bytesToRead - bytesRead);
            }
                  //获取题目数据
            System.out.println("获取了复习题目");
            DataByte shaper = (DataByte) deserialize(QuestionsBytes);
            for(int j = 0;j<shaper.vector.size();j++) {
                String type= shaper.vector.get(j).remove(0);
                if(type.equals("选择题"))
                    selecttopics.add(shaper.vector.get(j));
                else if(type.equals("填空题"))
                    blanktopics.add(shaper.vector.get(j));
                else if(type.equals("判断题"))
                    judgetopics.add(shaper.vector.get(j));
                else if(type.equals("编程题"))
                    codetopics.add(shaper.vector.get(j));
            }
            for(int j = 0;j<selecttopics.size();j++) {
                for(int k = 0;k<selecttopics.elementAt(j).size();k++) {
                    System.out.print(selecttopics.elementAt(j).elementAt(k)+" ");
                }
                System.out.println();
            }
            topics.add(selecttopics);
            topics.add(judgetopics);
            topics.add(blanktopics);
            if(codetopics.size()==0){
                codetopics.add(new Vector<String>());
            }
            topics.add(codetopics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return topics;
    }

    /**
     * @description: 断开与服务器的连接
     * @param
     * @return void
     * @author: lisnail
     * @date: 2022.11.18
     */
    public void quit(){
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
    *   @Description: serialization对序列化对象的解码函数
    *   @Param: bytes
    *   @ParamTypes: byte[]
    *   @return: Object
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public Object deserialize(byte[] bytes) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;

        Object object = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            object = ois.readObject();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }finally {//保证对象输入流关闭
            try {
                if (bis != null) {
                    bis.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
        return object;
    }
    /**
    *   @Description: serialization序列化对象函数
    *   @Param: obj
    *   @ParamTypes: Serializable
    *   @return: byte[]
    *   @Arthur: lisnail
    *   @Date: 2022/12/10
    **/
    public byte[] getBytesFromObject(Serializable obj){
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream bo = null;
        ObjectOutputStream oos = null;
        try {
            bo = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bo);
            oos.writeObject(obj);
            oos.flush();
            return bo.toByteArray();
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bo != null) {
                    bo.close();
                }
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

