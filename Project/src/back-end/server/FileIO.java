package server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Vector;

/*
 * 类名：FileIO 
 * 功能：进行服务器与客户端、管理员端的图片交互，包括图片的增删查改等操作
 * 作者：王诗腾
 */
public class FileIO {
	/*
    * 方法名：sendImage 静态函数
    * 参数： chapter章节号，subChapter子章节号
    * 功能： 将某小节的所有图片转化为二维数组返回
    * 返回值：imageBytes 包含了某小节所有图片的byte二维数组 
    * 作者：王诗腾
    */
	public static byte[][] sendImage(int chapter,int subChapter)
		{
			String pathString = "/root/JavaProject/";    //图片总文件夹
			byte[][] imageBytes = null; 
			try {
				GreetingServer.stmt = GreetingServer.conn.createStatement();
		        String sql;
		        sql = "select FILEPATH from chapterfiles where CHAPTER = "+chapter+" AND SUBCHAPTER = "+subChapter;
		        
		        ResultSet rs = GreetingServer.stmt.executeQuery(sql);    //图片路径名称
		        rs.last();
		        int relen = rs.getRow();	//图片数量
		        rs.beforeFirst();
		        imageBytes = new byte[relen][];    //该小节的图片集合
		        int index = 0;
		        while(rs.next())    //循环写入图片
		        {
		        	String pngString = pathString+rs.getString("FILEPATH");
			        System.out.println(pngString);
			        
			        try (FileInputStream fileInputStream = new FileInputStream(new File(pngString));) {
			            imageBytes[index] = new byte[fileInputStream.available()];
			            fileInputStream.read(imageBytes[index]);	//写入图片
			        } 
			        catch (IOException e) {
			            e.printStackTrace();
			            return null;
			        }
		        	index+=1;
		        }
	            rs.close();
		        GreetingServer.stmt.close();
			}
	        catch (SQLException e1) {
				e1.printStackTrace();
			}
	        return imageBytes;    //返回二维数组
		}
	/*
    * 方法名：sendChapterNames 静态函数
    * 参数： 无
    * 功能： 查询数据库，将所有章节子章节名转化为byte数组返回
    * 返回值：bytes 包含了所有章节子章节名的byte数组 
    * 作者：王诗腾
    */
	public static byte[] sendChapterNames() throws Exception
	{
		GreetingServer.stmt = GreetingServer.conn.createStatement();
        String sql;
        sql = "select CHAPTERNAME,SUBCHAPTERNAME from knowledge";
        ResultSet rs = GreetingServer.stmt.executeQuery(sql); 
        byte[] bytes;
        Vector<String> vector;
        DataByte shaper= new DataByte();
        while(rs.next())
		{
			vector = new Vector<String>();
			vector.add(rs.getString(1));
			vector.add(rs.getString(2));
			shaper.vector.add(vector);
		}
        
        bytes =SendQuestions.getBytesFromObject(shaper);
        
        rs.close();
        GreetingServer.stmt.close();
        return bytes;
	}
	/*
    * 方法名：sendSelectImage 静态函数
    * 参数： chapter章节号，subChapter子章节号，number图片号
    * 功能： 将某张指定的图片转化为byte数组返回
    * 返回值：imageBytes 包含了所有章节子章节名的byte数组 
    * 作者：王诗腾
    */
	public static byte[]sendSelectImage(int chapter,int subChapter,int number)
	{
		String pathString = "/root/JavaProject/";    //图片总文件夹
		byte[] imageBytes = null; 
		try {
			GreetingServer.stmt = GreetingServer.conn.createStatement();
			String sql;
		    sql = "select FILEPATH from chapterfiles where CHAPTER = "+chapter+" AND SUBCHAPTER = "+subChapter;
		    ResultSet rs = GreetingServer.stmt.executeQuery(sql);    //图片路径名称
		    for(int i = 1;i<=number;i++)rs.next();
		    String pngString = pathString+rs.getString("FILEPATH");
	        System.out.println(pngString);
	        
	        try (FileInputStream fileInputStream = new FileInputStream(new File(pngString));) {
	            imageBytes= new byte[fileInputStream.available()];
	            
	            fileInputStream.read(imageBytes);	//写入图片
	        } 
	        catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        }
	        rs.close();
	        GreetingServer.stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
       return imageBytes;
	}
	/*
    * 方法名：imageNum 静态函数
    * 参数： chapter章节号，subChapter子章节号
    * 功能： 计算某小节含有的图片总数
    * 返回值：len 某小节含有的图片总数
    * 作者：王诗腾
    */
	public static int imageNum(String chapter, String subChapter)
	{
		int len = 0;
			try {
				GreetingServer.stmt = GreetingServer.conn.createStatement();
				String sql;
			    sql = "select FILEPATH from chapterfiles where CHAPTER = "+chapter+" AND SUBCHAPTER = "+subChapter;
			    ResultSet rs = GreetingServer.stmt.executeQuery(sql);    //图片路径名称
			    rs.last();
		        len = rs.getRow();
		        rs.close();
		        GreetingServer.stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return len;	
	}
	/*
    * 方法名：updateImage 静态函数
    * 参数： chapter章节号，subChapter子章节号，number图片号，bytes新图像的byte数组
    * 功能： 更新某张特定的图像
    * 返回值：无
    * 作者：王诗腾
    */
	public static void updateImage(String chapter,String subChapter,String num,byte[]bytes)
	{
		String pathString = "/root/JavaProject/";    //图片总文件夹
		File pngFile = new File(String.format("%schapterfiles/%s/%s.%s/%s_%s_%s.png", 
				pathString,chapter,chapter,subChapter,chapter,subChapter,num));
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(pngFile);
			fileOutputStream.write(bytes);    //往图片文件里写数据
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/*
    * 方法名：addImage 静态函数
    * 参数： chapter章节号，subChapter子章节号，number图片号，bytes新图像的byte数组
    * 功能： 向某一子章节后尾插图片。
    * 返回值：无
    * 作者：王诗腾
    */
	public static void addImage(String chapter,String subChapter,String num,byte[]bytes)
	{
		String pathString = "/root/JavaProject/";    //图片总文件夹
		try {
			GreetingServer.stmt = GreetingServer.conn.createStatement();
			String sqlString = String.format("insert into chapterfiles values(%s,%s,"
					+ "'chapterfiles/%s/%s.%s/%s_%s_%s.png');", 
					chapter,subChapter,chapter,chapter,subChapter,chapter,subChapter,num);
			GreetingServer.stmt.executeUpdate(sqlString);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		File pngFile = new File(String.format("%schapterfiles/%s/%s.%s/%s_%s_%s.png", 
				pathString,chapter,chapter,subChapter,chapter,subChapter,num));
		try {
			pngFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(pngFile);
			fileOutputStream.write(bytes);    //往图片文件里写数据
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/*
    * 方法名：deleteImage 静态函数
    * 参数： chapter章节号，subChapter子章节号，number图片号
    * 功能： 删除某子章节最后一张图像
    * 返回值：ok1&&ok2 是否删除成功
    * 作者：王诗腾
    */
	public static boolean deleteImage(String chapter,String subChapter,String num)
	{
		String pathString = "/root/JavaProject/";    //图片总文件夹
		String filePath = String.format("%schapterfiles/%s/%s.%s/%s_%s_%s.png", 
				pathString,chapter,chapter,subChapter,chapter,subChapter,num);
		boolean ok1 = (new File(filePath)).delete();
		boolean ok2 = false;
		try {
			GreetingServer.stmt = GreetingServer.conn.createStatement();
			String sqlString = String.format("delete from chapterfiles where filepath = "
					+ "'chapterfiles/%s/%s.%s/%s_%s_%s.png';", 
					chapter,chapter,subChapter,chapter,subChapter,num);
			System.out.println(sqlString);
			ok2 =  GreetingServer.stmt.executeUpdate(sqlString)>0;
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		return ok1&&ok2;
	}
}
