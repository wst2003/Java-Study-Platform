package server;
import java.sql.*;

/*
 * 类名：LoginRegister
 * 功能：处理客户端的注册登录请求
 * 作者：王诗腾
 */
public class LoginRegister {
	/*
    * 方法名：Login 静态函数
    * 参数： name 用户名 password 密码
    * 功能： 处理客户端的登录请求
    * 返回值： -1 查无此人 -2 数据库错误 0 密码不匹配 1 登陆成功
    * 作者：王诗腾
    */
	public static int Login(String name,String password)
	{
		try {
		GreetingServer.stmt = GreetingServer.conn.createStatement();
		
        String sql;
        sql = "SELECT * FROM users where USERNAME = '"+name+"'";
        ResultSet rs = GreetingServer.stmt.executeQuery(sql);
        if(!rs.next()) {
        	return -1;  //查无此人
        }
    
        String clientName = rs.getString("USERNAME");
        String clientPassword = rs.getString("USERPASSWORD");
        System.out.print("名称: " + clientName+"密码："+clientPassword);
        rs.close();
        GreetingServer.stmt.close();
        if(!password.equals(clientPassword))	return 0;	//密码不匹配
        return 1;	//登陆成功
		}
		catch(SQLException se) {
			se.printStackTrace();
			return -2;   //数据库错误
		}
	}
	/*
    * 方法名：Register 静态函数
    * 参数： name 用户名 password 密码
    * 功能： 处理客户端的注册请求
    * 返回值： -1 用户名重复 -2 数据库错误 0 未成功注册 1 登陆成功
    * 作者：王诗腾
    */
	public static int Register(String name,String password)
	{
		try {
			GreetingServer.stmt = GreetingServer.conn.createStatement();
			String sqlString = "select * from users where USERNAME = '"+name+"'";
			ResultSet rs = GreetingServer.stmt.executeQuery(sqlString);
			if(rs.next())	return -1;	//用户名重复
			else {
				sqlString = "insert into users (USERNAME,USERPASSWORD) values('"+name+"','"+password+"')";
				int resultStat = GreetingServer.stmt.executeUpdate(sqlString);
				if(resultStat>0)	return 1;	//注册成功
				else return 0;	//未成功注册 
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return -2;	//数据库错误
		}
		
	}
}
