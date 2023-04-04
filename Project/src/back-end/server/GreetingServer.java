package server;
import java.net.*;
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;

/*
 * 类名：GreetingServer 
 * 继承：Thread
 * 功能：连接数据库、开放管理员和客户端端口，根据发送给服务器的请求调用
 * 		 相应方法做出响应。
 * 作者：王诗腾
 */
public class GreetingServer extends Thread
{
   private ServerSocket serverSocket;
   // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
   static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";


   // 数据库的用户名与密码，需要根据自己的设置 
   static final String USER = "debian-sys-maint";
   static final String PASS = "ONPEVtZlkY6cA8V0";
   //数据库连接器和句柄对象
   static Connection conn = null;
   static Statement stmt = null;
   
   /*
    * 构造器名：GreetingServer
    * 参数： port 要开放的端口号
    * 功能：连接数据库、创建端口号为port的ServerSocket
    * 作者：王诗腾
    */
   public GreetingServer(int port) throws IOException
   {
	   
      serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(1000000);
      
      //连接数据库
      try{
          // 注册 JDBC 驱动
          Class.forName(JDBC_DRIVER);
      
          // 打开链接
          System.out.println("连接数据库...");
          conn = DriverManager.getConnection(DB_URL,USER,PASS);
          System.out.println("端口"+port+"连接数据库成功!");
      }
      catch(SQLException se){
          // 处理 JDBC 错误
          se.printStackTrace();
      }
      catch(Exception e){
          // 处理 Class.forName 错误
          e.printStackTrace();
      }
   }
 
   /*
    * 方法名：run 重载自Thread
    * 参数： 无
    * 功能：运行本线程。等待管理员或客户端的连接，
    * 	    并根据它们的请求调用相应方法做出响应。
    * 返回值：无
    * 作者：王诗腾
    */
   public void run()
   {
	  Socket server = null;
      while(true)
      {
         try
         {
        	//等待客户端连接
            System.out.println("等待远程连接，端口号为：" + serverSocket.getLocalPort() + "...");
            server = serverSocket.accept();
            System.out.println("远程主机地址：" + server.getRemoteSocketAddress());
            String serverIn;
            //in:服务器的输入流读取器
            DataInputStream in = new DataInputStream(server.getInputStream());
            //out:服务器的输出流写入器
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            out.writeUTF("OK");
            //读入Client请求
            while((serverIn = in.readUTF()).equals("exit")==false)
            {
            	//处理请求
            	dealClient(serverIn, in,out);
            }
            
            out.writeUTF("谢谢连接我：" + server.getLocalSocketAddress() + "\nGoodbye!");
            server.close();
            continue;
         }
         catch(SocketTimeoutException s)
         {
            System.out.println("Socket timed out!");
            break;
         }
         catch(IOException e)
         {
            try {
				server.close();
			} 
            catch (IOException e1) {
				e1.printStackTrace();
			}
            continue;
         } 
         catch (Exception e) {
			e.printStackTrace();
		}
      }
   }
   
   /*
    * 方法名：dealClient 
    * 异常：NumberFormatException, Exception
    * 参数1：serverIn 给服务器的请求字符串
    * 参数2：in 服务器的输入流读取器
    * 参数3：out 服务器的输出流写入器
    * 功能：将请求字符串以"-"分割，根据第一个子串内容，调用对应方法写进输出流。
    * 返回值：无
    * 作者：王诗腾
    */
   public void dealClient(String serverIn, DataInputStream in,DataOutputStream out) throws NumberFormatException, Exception
   {
	   //根据客户端请求，进行相应响应
	   String[]commandStrings;		//包含若干请求字段
	   if(serverIn.length()>7&& serverIn.substring(0, 8).equals("PQUpdate")) {
		   //更新编程题，由于"-"是实义字符，这里约定以"分"作为分隔符
		   commandStrings = serverIn.split("分");
	   }
	   else {commandStrings = serverIn.split("-");}		//约定以"-"作为分隔符
	   
	   System.out.println("s"+commandStrings[0]);
	   
	   if(commandStrings[0].equals("login")) {	//客户端登录请求
		   out.writeUTF( ""+LoginRegister.Login(commandStrings[1], commandStrings[2]));
	   }
	   if(commandStrings[0].equals("register")) {	//客户端注册请求
		   out.writeUTF( ""+LoginRegister.Register(commandStrings[1], commandStrings[2]));
	   }
	   if(commandStrings[0].equals("chapterName")) {	//客户端章节名请求
		   byte bytes[] = FileIO.sendChapterNames();
		   out.writeInt(bytes.length);
		   out.write(bytes);
	   }
	   if(commandStrings[0].equals("chapterfile")) {   
		   //客户端某章某小节的知识图片请求
		   //imageBytes二维数组，一维：图片数量 二维：图片的bytes
		   byte[][]imageBytes = FileIO.sendImage
				   (Integer.parseInt(commandStrings[1]),Integer.parseInt(commandStrings[2]));
		   
		   for(int i = 0;i<imageBytes.length;i++) {
			   out.writeInt(imageBytes[i].length);	//向客户端发送图片的大小
			   out.write(imageBytes[i]);	//向客户端发送图片的二进制数组
		   }
		   out.writeInt(-1);
	   }
	   if(commandStrings[0].equals("imageNum"))
	   {
		   //某一小节图片数量请求
		   out.writeInt(FileIO.imageNum(commandStrings[1], commandStrings[2]));
	   }
	   if(commandStrings[0].equals("imageFile")) {
		   //管理员某图片文件请求
		   byte bytes[] = FileIO.sendSelectImage
				   (Integer.parseInt(commandStrings[1]), Integer.parseInt(commandStrings[2]), Integer.parseInt(commandStrings[3]));
		   
		   out.writeInt(bytes.length);
		   out.write(bytes);
	   }
	   if(commandStrings[0].equals("imageUpdate")) {
		   //管理员图片更新请求
		   byte bytes[] = new byte[in.readInt()];
		   in.readFully(bytes);
		   FileIO.updateImage(commandStrings[1], commandStrings[2], commandStrings[3], bytes);
	   }
	   if(commandStrings[0].equals("imageAdd")) {
		   byte bytes[] = new byte[in.readInt()];
		   in.readFully(bytes);
		   FileIO.addImage(commandStrings[1], commandStrings[2], commandStrings[3], bytes);
	   }
	   if(commandStrings[0].equals("imageDelete")) {
		   //管理员图片删除请求
		   if(FileIO.deleteImage(commandStrings[1], commandStrings[2], commandStrings[3])) {
			   out.writeUTF("删除成功！");
		   }
		   else out.writeUTF("删除失败！");
	   }
	   if(commandStrings[0].equals("test")) {	//客户端请求某一章的题目
		   byte[][] questionsBytes;
		   
		   questionsBytes = SendQuestions.sendQuestionsByChapter(Integer.parseInt(commandStrings[1]));
		   for(int i = 0;i<questionsBytes.length;i++) {
			   out.writeInt(questionsBytes[i].length);
			   out.write(questionsBytes[i]);
		   }
	   }
	   if(commandStrings[0].equals("ManagerQuestions")) {
		   //管理员的题库查询请求
		   byte[]questionsBytes=Manager.questionsQuery(commandStrings[1], commandStrings[2]);
		   out.writeInt(questionsBytes.length);
		   out.write(questionsBytes);
		   
	   }
	   if(commandStrings[0].equals("ManagerDelete")) {
		   //管理员的题目删除请求
		   out.writeBoolean(Manager.deleteQuestion(commandStrings[1], commandStrings[2], commandStrings[3]));
	   }
	   if(commandStrings[0].equals("ManagerQuestionsUpdate")) {
		   //管理员的题目更新请求
		   out.writeBoolean(Manager.updateQuestions(commandStrings[1], commandStrings[2], commandStrings[3],commandStrings[4]));
	   }
	   if(commandStrings[0].equals("ManagerAdd")) {
		   //管理员的题目增加请求
		   out.writeBoolean(Manager.addQuestions(commandStrings[1], commandStrings[2]));
	   }
	   if(commandStrings[0].equals("ManagerSelectQuestions")) {
		   //管理员的选择题查询请求
		   byte[]selectQuestionsBytes=Manager.selectQuestionsQuery(commandStrings[1]);
		   out.writeInt(selectQuestionsBytes.length);
		   out.write(selectQuestionsBytes);
	   }
	   if(commandStrings[0].equals("ManagerSQUpdate")) {
		 //管理员的选择题更新请求
		   out.writeBoolean(Manager.updateSelectQuestions(commandStrings[1], commandStrings[2], commandStrings[3], commandStrings[4]));
	   }
	   if(commandStrings[0].equals("ManagerFillQuestions")) {
		 //管理员的填空题查询请求
		   byte[]fillQuestionsBytes=Manager.fillQuestionsQuery(commandStrings[1]);
		   out.writeInt(fillQuestionsBytes.length);
		   out.write(fillQuestionsBytes);
	   }
	   if(commandStrings[0].equals("ManagerFQUpdate")) {
		 //管理员的填空题更新请求
		   out.writeBoolean(Manager.updateFillQuestions(commandStrings[1], commandStrings[2], commandStrings[3]));
	   }
	   if(commandStrings[0].equals("ManagerJudgeQuestions")) {
		   //管理员的判断题查询请求
		   byte[]judgeQuestionsBytes=Manager.judgeQuestionsQuery(commandStrings[1]);		
		   out.writeInt(judgeQuestionsBytes.length);
		   out.write(judgeQuestionsBytes);

	   }
	   if(commandStrings[0].equals("ManagerJQUpdate")) {
		 //管理员的判断题更新请求
		   out.writeBoolean(Manager.updateJudgeQuestions(commandStrings[1], commandStrings[2], commandStrings[3]));
	   }
	   if(commandStrings[0].equals("PQuestionsNum")) {
		 //管理员的某小节编程题数目查询请求
		   out.writeInt(Manager.programQuestionsNumbers(commandStrings[1]));
	   }
	   if(commandStrings[0].equals("PQuestion")) {
		   //管理员的编程题查询请求
		   out.writeUTF(Manager.programQuestionQuery(commandStrings[1], commandStrings[2]));
	   }
	   if(commandStrings[0].equals("PQUpdate")) {
		   //管理员的编程题更新请求
		   out.writeBoolean(Manager.updateProgramQuestions(commandStrings[1], commandStrings[2], commandStrings[3]));
	   }
	   if(commandStrings[0].equals("gettopicinfo")) {
		   //管理员的编程题查询请求
		   byte[] bytes = AnswersIO.sendTopicInfo(commandStrings[1]);
		   out.writeInt(bytes.length);
		   out.write(bytes);
	   }
	   if(commandStrings[0].equals("topicinfo")) {
		   //用户端做题记录录入请求
		   byte bytes[] = new byte[in.readInt()];
		   in.read(bytes);
		   DataByte dataByte = (DataByte)SendQuestions.deserialize(bytes);
		   AnswersIO.addAnswers(dataByte.vector);
	   }
	   if(commandStrings[0].equals("topicnums")) {
		   //用户端综合复习拉题请求
		   byte bytes[] = new byte[in.readInt()];
		   in.read(bytes);
		   DataByte dataByte = (DataByte)SendQuestions.deserialize(bytes);
		   byte questionsBytes[]=SendQuestions.sendReviewQuestions(dataByte.vector);
		   out.writeInt(questionsBytes.length);
		   out.write(questionsBytes);
	   }
   }
   /*
    * 方法名：main 
    * 参数： args
    * 功能：主函数，构造7000端口（管理员）和6000-6019端口（客户端）的服务器。
    * 返回值：无
    * 作者：王诗腾
    */
   public static void main(String [] args)
   {
      try
      {
    	  Thread manager = new GreetingServer(7000);
   	      manager.start();
    	 //多线程
   	      for(int i = 6000;i<=6019;i++) {
   	         Thread t = new GreetingServer(i);
   	         t.start();
   	      }
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}