package server;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/*
 * 类名：AnswersIO 
 * 功能：服务器向客户端进行关于做题记录的交互，包括发送正确率和向数据库录入做题记录
 * 作者：王诗腾
 */
public class AnswersIO
{
	/*
	* 方法名：sendTopicInfo 静态函数
	* 参数： userName 用户名
	* 功能： 为客户端推荐学习路径和综合复习功能提供数据，
	* 	   包括用户在某一章节的近期第1-15、16-30、31-45和历史做过的所有题目的正确率。
	* 	   共有十一章，记录进11×4 的二维vector并转化为byte数组
	* 返回值：bytes 一维数组，包含上述信息转化而来的byte数组
	* 作者：王诗腾
	*/
	static byte[] sendTopicInfo(String userName)
	{
		byte bytes[] = null;
		DataByte shaper= new DataByte();
		Vector<String>vector;
		ResultSet rs = null;
		for(int i = 1;i<=11;i++) {
			vector = new Vector<String>();
			String sql = "select judge from answers  where username = \'"+userName+"\' and chapter = "+i+" ORDER BY begintime  LIMIT 45";
			try {
				int trueNum;
				GreetingServer.stmt = GreetingServer.conn.createStatement();
				rs = GreetingServer.stmt.executeQuery(sql);  
				rs.last();
				int size = rs.getRow(); System.out.print(size+" ");
				rs.beforeFirst();
				if(size==0) {
					vector.add(""+-1);vector.add(""+-1);vector.add(""+-1);
				}
				if(1<=size&&size<=15) {
					rs.next();
					trueNum = 0;
					for(int j = 1;j<=size;j++) {
						if(rs.getString(1).equals("T"))trueNum++;
					}
					vector.add(""+(trueNum/(size+0.0)));vector.add(""+-1);vector.add(""+-1);
				}
				if(16<=size&&size<=30) {
					trueNum = 0;
					for(int j = 1;j<=15;j++) {
						rs.next();
						if(rs.getString(1).equals("T"))trueNum++;
					}
					vector.add(""+(trueNum/15.0));
					trueNum = 0;
					for(int j = 16;j<=size;j++) {
						rs.next();
						if(rs.getString(1).equals("T"))trueNum++;
					}
					vector.add(""+(trueNum/(size-15.0)));vector.add(""+-1);
				}
				if(31<=size) {
					trueNum = 0;
					for(int j = 1;j<=15;j++) {
						rs.next();
						if(rs.getString(1).equals("T")) {
							trueNum++;
						}
					}
					vector.add(""+(trueNum/15.0));
					trueNum = 0;
					for(int j = 16;j<=30;j++) {
						rs.next();
						if(rs.getString(1).equals("T")) {
							trueNum++;
						}
					}
					vector.add(""+(trueNum/15.0));
					trueNum = 0;
					for(int j = 31;j<=size;j++) {
						rs.next();
						if(rs.getString(1).equals("T")) {
							trueNum++;
						}
					}
					vector.add(""+(trueNum/(size-30.0)));
				}
				sql = String.format("select t/s from (select COUNT(*) as s from answers  where username = '%s' and chapter = %d)as tmp1,"
						+ "(select COUNT(*) as t from answers  where username = '%s' and chapter = %d and judge = 'T')as tmp2", userName,i,userName,i);
				rs = GreetingServer.stmt.executeQuery(sql);  
				rs.next();
				if(rs.getString(1)!=null)vector.add(""+rs.getString(1));
				else vector.add(""+-1);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			shaper.vector.add(vector);
		}
		try {
			bytes= SendQuestions.getBytesFromObject(shaper);
			GreetingServer.stmt.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bytes;
	}
	/*
	* 方法名：addAnswers 静态函数
	* 参数： info 某用户某次做题后的做题记录数组，包括用户名、题型、题号、答案等信息
	* 功能： 将上述信息录入数据库
	* 返回值：无
	* 作者：王诗腾
	*/
	public static void addAnswers(Vector<Vector<String>>info)
	{
		String sql = "";
		try {
			GreetingServer.stmt = GreetingServer.conn.createStatement();
			for(int i = 0;i<info.size();i++) {
				Vector<String>v = info.get(i);
				sql = String.format("insert into answers values('%s','%s','%s','%s',%s,'%s',%s,'%s');", 
						v.get(0),v.get(1),v.get(2),v.get(3),v.get(4),v.get(5),v.get(6),v.get(7));
				GreetingServer.stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}