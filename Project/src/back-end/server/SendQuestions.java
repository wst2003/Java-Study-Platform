package server;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/*
 * 类名：SendQuestions 
 * 功能：服务器向客户端发送题目信息
 * 作者：王诗腾
 */
public class SendQuestions {
	/*
    * 方法名：getBytesFromObject 静态函数
    * 参数： obj 一个拓展了Serializable接口的对象
    * 功能： 将一个拓展了Serializable接口的对象转化为byte数组
    * 返回值：bo.toByteArray() 一维数组 
    * 作者：王诗腾
    */
	public static byte[] getBytesFromObject(Serializable obj) throws Exception {
	    if (obj == null) {
	      return null;
	    }
	    ByteArrayOutputStream bo = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(bo);
	    oos.writeObject(obj);
	    return bo.toByteArray();
	}
	/*
    * 方法名：deserialize 静态函数
    * 参数： bytes 一个将转化为拓展了Serializable接口对象的数组
    * 功能： 将一个byte数组转化为拓展了Serializable接口的对象
    * 返回值：object 拓展了Serializable接口的对象
    * 作者：王诗腾
    */
	public static Object deserialize(byte[] bytes) {
	    Object object = null;
	    try {
	      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);//
	      ObjectInputStream ois = new ObjectInputStream(bis);
	        object = ois.readObject();
	      ois.close();
	      bis.close();
	    } catch (IOException ex) {
	      ex.printStackTrace();
	    } catch (ClassNotFoundException ex) {
	      ex.printStackTrace();
	    }
	    return object;
	}
	/*
    * 方法名：sendQuestionsByChapter 静态函数
    * 参数： chapter 章节名
    * 功能： 将客户端要求的某章习题，
    * 		 先转化为Serializable对象再转化为byte数组，最后按题型组装为二维数组
    * 返回值：questionsBytes 包含四种题型的byte[4][]
    * 作者：王诗腾
    */
	public static byte[][] sendQuestionsByChapter(int chapter) throws Exception
	{
		byte[][]questionsBytes = new byte[4][];	//第一维表示题目类型，第二维表示六道题目构成的二维vector
		DataByte shaper;
		Vector<String> vector;
		//获取六道选择题
		shaper = new DataByte();
		String selectString = "select * from("+
				"select questions.CHAPTER ,knowledge.CHAPTERNAME,questions.NUMBER,questions.questiondescribe,"+
				"selectquestions.sa,selectquestions.sb,selectquestions.sc,selectquestions.sd,selectquestions.answer "+
			"from questions ,knowledge,selectquestions "+
			"where questions.CHAPTER = "+chapter+" and knowledge.CHAPTER = questions.CHAPTER and questions.TYPE = '选择题' "+
				"and selectquestions.CHAPTER = questions.CHAPTER and selectquestions.NUMBER = questions.NUMBER "+
			"order by rand() limit 6)as tmp ORDER BY tmp.NUMBER;";
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		ResultSet rs = GreetingServer.stmt.executeQuery(selectString);  
		while(rs.next())
		{	//获取每一道选择题（有六道）
			vector = new Vector<String>();
			
			for(int i = 1;i<=9;i++) {
				vector.add(rs.getString(i));
			}
			shaper.vector.add(vector);
		}
		questionsBytes[0]=getBytesFromObject(shaper);
		rs.close();
		//获取六道判断题
		shaper = new DataByte();
		String judgeString = "select * from("+
				"select questions.CHAPTER ,knowledge.CHAPTERNAME,questions.NUMBER,questions.questiondescribe,judgequestions.answer "+
				"from questions ,knowledge,judgequestions "+
				"where questions.CHAPTER = "+chapter+" and knowledge.CHAPTER = questions.CHAPTER and questions.TYPE = '判断题' "+ 
					"and judgequestions.CHAPTER = questions.CHAPTER and judgequestions.NUMBER = questions.NUMBER "+
				"order by rand() limit 6)as tmp ORDER BY tmp.NUMBER;";
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		rs = GreetingServer.stmt.executeQuery(judgeString);  
		while(rs.next())
		{	//获取每一道判断题（有六道）
			vector = new Vector<String>();
			for(int i = 1;i<=5;i++) {
				vector.add(rs.getString(i));
			}
			shaper.vector.add(vector);
		}
		questionsBytes[1]=getBytesFromObject(shaper);
		rs.close();
		//获取六道填空题
		shaper = new DataByte();
		String fillString = "select * from("+
				"select questions.CHAPTER ,knowledge.CHAPTERNAME,questions.NUMBER,questions.questiondescribe,fillquestions.answer "+
				"from questions ,knowledge,fillquestions "+
				"where questions.CHAPTER = "+chapter+" and knowledge.CHAPTER = questions.CHAPTER and questions.TYPE = '填空题' "+ 
					"and fillquestions.CHAPTER = questions.CHAPTER and fillquestions.NUMBER = questions.NUMBER "+
				"order by rand() limit 6)as tmp ORDER BY tmp.NUMBER;";
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		
		rs = GreetingServer.stmt.executeQuery(fillString);  
		while(rs.next())
		{	//获取每一道填空题（有六道）
			vector = new Vector<String>();
			for(int i = 1;i<=5;i++) {
				vector.add(rs.getString(i));
			}
			shaper.vector.add(vector);
		}
		questionsBytes[2]=getBytesFromObject(shaper);
		//获取二道编程题
				shaper = new DataByte();
				String programString = "select * from("+
						"select questions.CHAPTER ,knowledge.CHAPTERNAME,questions.NUMBER,questions.questiondescribe,programquestions.answer "+
						"from questions ,knowledge,programquestions "+
						"where questions.CHAPTER = "+chapter+" and knowledge.CHAPTER = questions.CHAPTER and questions.TYPE = '编程题' "+ 
							"and programquestions.CHAPTER = questions.CHAPTER and programquestions.NUMBER = questions.NUMBER "+
						"order by rand() limit 2)as tmp ORDER BY tmp.NUMBER;";
				GreetingServer.stmt = GreetingServer.conn.createStatement();
				
				rs = GreetingServer.stmt.executeQuery(programString); 
				if(!rs.last()) {
					vector = new Vector<String>();
					shaper.vector.add(vector);
					System.out.println(0);
				}
				else {
					rs.beforeFirst();
					while(rs.next())
					{	//获取每一道编程题（有二道）
						vector = new Vector<String>();
						for(int i = 1;i<=5;i++) {
							vector.add(rs.getString(i));
						}
						shaper.vector.add(vector);
					}
				}
				questionsBytes[3]=getBytesFromObject(shaper);
		
		GreetingServer.stmt.close();
		rs.close();
		return questionsBytes;
	}
	/*
    * 方法名：sendReviewQuestions 静态函数
    * 参数： v 一个11×2的vector数组，包含了章节号和题型
    * 功能：提供客户端所需的综合复习习题。按照客户端要求的章节号和题型，
    * 		先转化为Serializable对象，再转化为byte数组进行返回
    * 返回值：bytes 包含用户综合复习所需的若干题目信息
    * 作者：王诗腾
    */
	public static byte[]sendReviewQuestions(Vector<Vector<String>>v)
	{
		byte []bytes=null;
		DataByte dataByte = new DataByte();
		Vector<Vector<String>> tmpVector = new Vector<Vector<String>>();
		String sqlString = "";
		
		try {
			GreetingServer.stmt = GreetingServer.conn.createStatement();
			ResultSet rs = null;
			for(int i = 0;i<v.size();i++) {
				String chapter = v.get(i).get(0);
				String type = v.get(i).get(1);
				if(type.equals("编程题")&&(chapter.equals("1")||chapter.equals("8")||chapter.equals("10")))type = "选择题";
				if(type.equals("选择题")) {
					sqlString = "SELECT questions.CHAPTER ,knowledge.CHAPTERNAME,questions.NUMBER,questions.questiondescribe, "
							+ "selectquestions.sa,selectquestions.sb,selectquestions.sc,selectquestions.sd,selectquestions.answer "
							+ "from questions ,knowledge,selectquestions "
							+ "where questions.CHAPTER = "+chapter+" and knowledge.CHAPTER = questions.CHAPTER and questions.TYPE = '选择题'  "
							+ "and selectquestions.CHAPTER = questions.CHAPTER and selectquestions.NUMBER = questions.NUMBER  "
							+ "order by rand() limit 1;";
					
					rs = GreetingServer.stmt.executeQuery(sqlString);  
					rs.next();
					Vector<String> tmp= new Vector<String>();
					tmp.add(type);
					for(int j = 1;j<=9;j++) {
						tmp.add(rs.getString(j));
					}
					tmpVector.add(tmp);
				}
				else {
					String typeSheelName = "";
					if(type.equals("判断题"))typeSheelName = "judgequestions";
					else if(type.equals("填空题"))typeSheelName = "fillquestions";
					else if(type.equals("编程题"))typeSheelName = "programquestions";
					sqlString = "SELECT questions.CHAPTER ,knowledge.CHAPTERNAME,questions.NUMBER,questions.questiondescribe, "
							+typeSheelName+ ".answer "
							+ "from questions ,knowledge,"+typeSheelName
							+ " where questions.CHAPTER = "+chapter+" and knowledge.CHAPTER = questions.CHAPTER and questions.TYPE = '"+type+"' "
							+ "and "+typeSheelName+".CHAPTER = questions.CHAPTER and "+typeSheelName+".NUMBER = questions.NUMBER  "
							+ "order by rand() limit 1;";
					rs = GreetingServer.stmt.executeQuery(sqlString);  
					rs.next();
					Vector<String> tmp= new Vector<String>();
					tmp.add(type);
					for(int j = 1;j<=5;j++) {
						tmp.add(rs.getString(j));
					}
					tmpVector.add(tmp);
				}
			}
			for(int i = 0;i<tmpVector.size();i++) {
				System.out.println(tmpVector.get(i).get(0)+" "+tmpVector.get(i).get(1)+" "+tmpVector.get(i).get(2)+" "+tmpVector.get(i).get(3));
			}
			GreetingServer.stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		dataByte.vector = tmpVector;
		try {
			bytes = getBytesFromObject(dataByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}
}
