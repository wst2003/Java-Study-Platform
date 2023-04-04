package server;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/*
 * 类名：Manager 
 * 功能：服务器向管理员端进行交互，包括题目和图片的增删查改等操作
 * 作者：王诗腾
 */
public class Manager 
{
	/*
	* 方法名：questionsQuery 静态函数
	* 参数： chapter 章节名 type 题型
	* 功能： 查询某章节某题型的所有题目信息，包括章节名、题型、题号、题干描述信息
	* 返回值：questionsBytes 一维数组，包含上述信息转化而来的byte数组
	* 作者：王诗腾
	*/
	public static byte[] questionsQuery(String chapter,String type) throws Exception
	{
		byte[]questionsBytes;
		DataByte shaper= new DataByte();
		Vector<String> vector;
		
		String sql = "select number,questiondescribe from questions where chapter = "+chapter+" and type = '"+type+"';";
		

		GreetingServer.stmt = GreetingServer.conn.createStatement();
		ResultSet rs = GreetingServer.stmt.executeQuery(sql);  
		while(rs.next()) 
		{
			vector = new Vector<String>();              
			vector.add(rs.getString(1));
			vector.add(rs.getString(2));
			shaper.vector.add(vector);
		}

		GreetingServer.stmt.close();
		rs.close();
		questionsBytes=SendQuestions.getBytesFromObject(shaper);
		return questionsBytes;
	}
	/*
	* 方法名：deleteQuestion 静态函数
	* 参数： chapter 章节名 type 题型 number题号
	* 功能： 删除某给定的题目，在questions表和对应的题型表中都进行删除
	* 返回值：result>0 是否删除成功
	* 作者：王诗腾
	*/
	public static boolean deleteQuestion(String chapter,String type,String number ) throws SQLException
	{
		String sheetName="";
		if(type.equals("选择题"))sheetName = "selectquestions";
		if(type.equals("判断题"))sheetName = "judgequestions";
		if(type.equals("填空题"))sheetName = "fillquestions";
		if(type.equals("编程题"))sheetName = "programquestions";
		String sql = "delete from "+sheetName+" where CHAPTER = "+chapter+" and TYPE = '"+type+"' and NUMBER = "+number+";";
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		GreetingServer.stmt.executeUpdate(sql);
		sql = "delete from questions where CHAPTER = "+chapter+" and TYPE = '"+type+"' and NUMBER = "+number+";";
		int result = GreetingServer.stmt.executeUpdate(sql);
		System.out.println("delete"+result);
		GreetingServer.stmt.close();
		return result>0;
	}
	/*
	* 方法名：updateQuestions 静态函数
	* 参数： chapter 章节名 type 题型 number题号 describe题干描述
	* 功能： 更新某给定的题目（题干），在questions表进行更新
	* 返回值：result>0 是否更新成功
	* 作者：王诗腾
	*/
	public static boolean updateQuestions(String chapter,String type,String number,String describe)throws SQLException
	{
		String sql = "UPDATE questions set questiondescribe = '"+describe+"' where CHAPTER = "+chapter+" and TYPE = '"+type+"' and NUMBER = "+number+";";
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		int result = GreetingServer.stmt.executeUpdate(sql);
		System.out.println("update"+result);
		GreetingServer.stmt.close();
		return result>0;
	}
	/*
	* 方法名：addQuestions 静态函数
	* 参数： sql1 在某题型表中的插入sql语句 sql2 在questions表中的插入sql语句
	* 功能： 插入一道新题目，在对应的题型表和questions表中同时插入
	* 返回值：result>0 是否插入成功
	* 作者：王诗腾
	*/
	public static boolean addQuestions(String sql1,String sql2) throws SQLException
	{
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		int ok1,ok2;
		try {
			 ok1 = GreetingServer.stmt.executeUpdate(sql1);
			 ok2 = GreetingServer.stmt.executeUpdate(sql2);
		} catch (Exception e) {
			return false;
		}
		return (ok1>0)&&(ok2>0);
	}
	/*
	* 方法名：selectQuestionsQuery 静态函数
	* 参数： chapter 章节号
	* 功能： 查询某章选择题的信息，包括题号、四个选项、答案字段（题干等信息可用questionsQuery方法查询）
	* 返回值：questionsBytes 一维数组，包含上述信息转化而来的byte数组
	* 作者：王诗腾
	*/
	public static byte[] selectQuestionsQuery(String chapter) throws Exception
	{
		byte[]questionsBytes;
		DataByte shaper= new DataByte();
		Vector<String> vector;
		
		String sql = "select number,sa,sb,sc,sd,answer from selectquestions where chapter = "+chapter+";";
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		ResultSet rs = GreetingServer.stmt.executeQuery(sql);  
		while(rs.next())
		{
			vector = new Vector<String>();
			vector.add(rs.getString(1));
			vector.add(rs.getString(2));
			vector.add(rs.getString(3));
			vector.add(rs.getString(4));
			vector.add(rs.getString(5));
			vector.add(rs.getString(6));
			shaper.vector.add(vector);
		}
		GreetingServer.stmt.close();
		rs.close();
		questionsBytes=SendQuestions.getBytesFromObject(shaper);
		return questionsBytes;
	}
	/*
	* 方法名：fillQuestionsQuery 静态函数
	* 参数： chapter 章节号
	* 功能： 查询某章填空题的信息，包括题号、答案字段（题干等信息可用questionsQuery方法查询）
	* 返回值：questionsBytes 一维数组，包含上述信息转化而来的byte数组
	* 作者：王诗腾
	*/
	public static byte[] fillQuestionsQuery(String chapter) throws Exception
	{
		byte[]questionsBytes;
		DataByte shaper= new DataByte();
		Vector<String> vector;
		
		String sql = "select number,answer from fillquestions where chapter = "+chapter+";";
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		ResultSet rs = GreetingServer.stmt.executeQuery(sql);  
		while(rs.next())
		{
			vector = new Vector<String>();
			vector.add(rs.getString(1));
			vector.add(rs.getString(2));
			shaper.vector.add(vector);
		}
		GreetingServer.stmt.close();
		rs.close();
		questionsBytes=SendQuestions.getBytesFromObject(shaper);
		return questionsBytes;
	}
	/*
	* 方法名：judgeQuestionsQuery 静态函数
	* 参数： chapter 章节号
	* 功能： 查询某章判断题的信息，包括题号、答案字段（题干等信息可用questionsQuery方法查询）
	* 返回值：questionsBytes 一维数组，包含上述信息转化而来的byte数组
	* 作者：王诗腾
	*/
	public static byte[] judgeQuestionsQuery(String chapter) throws Exception
	{
		byte[]questionsBytes;
		DataByte shaper= new DataByte();
		Vector<String> vector;
		
		String sql = "select number,answer from judgequestions where chapter = "+chapter+";";
		System.out.println(sql);
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		ResultSet rs = GreetingServer.stmt.executeQuery(sql);  
		while(rs.next())
		{
			vector = new Vector<String>();
			vector.add(rs.getString(1));
			vector.add(rs.getString(2));
			shaper.vector.add(vector);
		}
		GreetingServer.stmt.close();
		rs.close();
		questionsBytes=SendQuestions.getBytesFromObject(shaper);
		return questionsBytes;
	}
	/*
	* 方法名：programQuestionQuery 静态函数
	* 参数： chapter 章节号
	* 功能： 查询某章某道编程题的信息，包括答案字段（题干等信息可用questionsQuery方法查询）
	* 返回值：result 查询到的答案字段
	* 作者：王诗腾
	*/
	public static String programQuestionQuery(String chapter,String number) throws SQLException 
	{
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		String sql = String.format("select answer from programquestions where CHAPTER = %s and number = %s;", chapter,number);
		ResultSet rs= GreetingServer.stmt.executeQuery(sql);
		rs.next();
		String result = rs.getString(1);
		GreetingServer.stmt.close();
		rs.close();
		return result;
	}
	/*
	* 方法名：updateSelectQuestions 静态函数
	* 参数： chapter 章节号 number 题号 columnNum字段号 contain 更新内容
	* 功能： 更新某章某道选择题的信息，包括四个选项、答案字段（题干信息更新可用updateQuestions方法更新）
	* 返回值：result>0 是否更新成功
	* 作者：王诗腾
	*/
	public static boolean updateSelectQuestions(String chapter,String number,String columnNum,String contain) throws SQLException
	{
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		String columnName="";
		if(columnNum.equals("1"))columnName = "sa";
		if(columnNum.equals("2"))columnName = "sb";
		if(columnNum.equals("3"))columnName = "sc";
		if(columnNum.equals("4"))columnName = "sd";
		if(columnNum.equals("5"))columnName = "answer";
		String sql = String.format("UPDATE selectquestions SET %s = '%s' where chapter = %s and number = %s;", columnName,contain,chapter,number);
		int result = GreetingServer.stmt.executeUpdate(sql);
		System.out.println("update"+result);
		GreetingServer.stmt.close();
		return result>0;
	}
	/*
	* 方法名：updateFillQuestions 静态函数
	* 参数： chapter 章节号 number 题号 contain 更新内容
	* 功能： 更新某章某道填空题的信息，包括答案字段（题干信息更新可用updateQuestions方法更新）
	* 返回值：result>0 是否更新成功
	* 作者：王诗腾
	*/
	public static boolean updateFillQuestions(String chapter,String number,String contain) throws SQLException
	{
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		String sql = String.format("UPDATE fillquestions SET answer = '%s' where chapter = %s and number = %s;",contain,chapter,number);
		int result = GreetingServer.stmt.executeUpdate(sql);
		System.out.println("update"+result);
		GreetingServer.stmt.close();
		return result>0;
	}
	/*
	* 方法名：updateJudgeQuestions 静态函数
	* 参数： chapter 章节号 number 题号 contain 更新内容
	* 功能： 更新某章某道判断题的信息，包括答案字段（题干信息更新可用updateQuestions方法更新）
	* 返回值：result>0 是否更新成功
	* 作者：王诗腾
	*/
	public static boolean updateJudgeQuestions(String chapter,String number,String contain) throws SQLException
	{
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		String sql = String.format("UPDATE judgequestions SET answer = '%s' where chapter = %s and number = %s;",contain,chapter,number);
		int result = GreetingServer.stmt.executeUpdate(sql);
		System.out.println("update"+result);
		GreetingServer.stmt.close();
		return result>0;
	}
	/*
	* 方法名：programQuestionsNumbers 静态函数
	* 参数： chapter 章节号 
	* 功能： 查询某章节有多少道编程题
	* 返回值：result 某章节编程题数量
	* 作者：王诗腾
	*/
	public static int programQuestionsNumbers(String chapter) throws SQLException
	{
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		String sql = String.format("select COUNT(*) from programquestions where CHAPTER = %s;", chapter);
		ResultSet rs= GreetingServer.stmt.executeQuery(sql);
		rs.next();
		int result = Integer.parseInt(rs.getString(1));
		GreetingServer.stmt.close();
		rs.close();
		return result;
	}
	/*
	* 方法名：updateProgramQuestions 静态函数
	* 参数： chapter 章节号 number 题号 contain 更新内容
	* 功能： 更新某章某道编程题的信息，包括答案字段（题干信息更新可用updateQuestions方法更新）
	* 返回值：result>0 是否更新成功
	* 作者：王诗腾
	*/
	public static boolean updateProgramQuestions(String chapter,String number,String contain) throws SQLException
	{
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		String sql = String.format("UPDATE programquestions SET answer = '%s' where chapter = %s and number = %s;",contain,chapter,number);
		int result = GreetingServer.stmt.executeUpdate(sql);
		System.out.println("update"+result);
		GreetingServer.stmt.close();
		return result>0;
	}
}
