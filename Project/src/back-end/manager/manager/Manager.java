package manager;
import java.net.*;
import javax.swing.*;


import questionsPanels.FQPanel;
import questionsPanels.JQPanel;
import questionsPanels.PQPanel;
import questionsPanels.QuestionsPanel;
import questionsPanels.SQPanel;

import java.awt.event.*;
import java.io.*;
import questionsPanels.*;
import filePanel.*;
/*
 * 类名：Manager 
 * 继承：JFrame
 * 功能：构建管理员窗体，其中包含了对图片和题目的增删查改等功能面板
 * 作者：王诗腾
 */
public class Manager extends JFrame
{	
	Socket manager;
	OutputStream outToServer;
	DataOutputStream out;
    InputStream inFromServer;
    DataInputStream in;
	
	JMenuBar sheetJMenuBar;
	JMenu sheetMenus[];
	
	JPanel panel;
	/*
    * 构造器名：Manager
    * 参数： serverName 服务器地址 port 要连接的端口号
    * 功能：连接服务器，初始化窗体，添加菜单栏，构建与服务器的输入输出流
    * 作者：王诗腾
    */
	public Manager(String serverName,int port) throws UnknownHostException, IOException
	{
		
		System.out.println("远程网络地址" + serverName + " 端口" + port);
		 manager = new Socket(serverName,port);
	    System.out.println("地址" + manager.getRemoteSocketAddress());
	    
	     outToServer = manager.getOutputStream();
	     out = new DataOutputStream(outToServer);
	    
	     inFromServer = manager.getInputStream();
	     in = new DataInputStream(inFromServer);
		System.out.println(in.readUTF());
		this.setSize(1000,800);
        this.setLayout(null);
        this.setTitle("管理员");
        
        sheetJMenuBar = new JMenuBar();
        sheetMenus = new JMenu[6];
        sheetMenus[0] = new JMenu("章节图片");
        sheetMenus[1] = new JMenu("习题集");
        sheetMenus[2] = new JMenu("选择题");
        sheetMenus[3] = new JMenu("判断题");
        sheetMenus[4] = new JMenu("填空题");
        sheetMenus[5] = new JMenu("编程题");
        		
        setActionListener();
        
        for(int i = 0;i<6;i++)sheetJMenuBar.add(sheetMenus[i]);
        this.setJMenuBar(sheetJMenuBar);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
       
	}
	/*
    * 方法名：setActionListener 
    * 参数： 无
    * 功能： 为窗体的各个菜单项添加监听器
    * 返回值：无
    * 作者：王诗腾
    */
	void setActionListener() 
	{
		sheetMenus[0].addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				if(panel!=null)remove(panel);
				FilePanel filePanel = new FilePanel(in,out);
				panel = filePanel;
				panel.setBounds(0, 0, 900, 700);
				add(panel);
				panel.updateUI();
			}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) { }
			public void mouseClicked(MouseEvent e) { }
		});
		sheetMenus[1].addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				if(panel!=null)remove(panel);
				QuestionsPanel questionsPanel = new QuestionsPanel(in,out);
				panel = questionsPanel;
				panel.setBounds(0, 0, 900, 700);
				add(panel);
				panel.updateUI();
			}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) { }
			public void mouseClicked(MouseEvent e) { }
		});
		
		sheetMenus[2].addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				if(panel!=null)remove(panel);
				SQPanel sqPanel = new SQPanel(in,out);
				panel = sqPanel;
				panel.setBounds(0, 0, 900, 700);
				add(panel);
				panel.updateUI();
			}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) { }
			public void mouseClicked(MouseEvent e) { }
		});
		sheetMenus[3].addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				if(panel!=null)remove(panel);
				JQPanel jqPanel = new JQPanel(in,out);
				panel = jqPanel;
				panel.setBounds(0, 0, 900, 700);
				add(panel);
				panel.updateUI();
			}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) { }
			public void mouseClicked(MouseEvent e) { }
		});
		sheetMenus[4].addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				if(panel!=null)remove(panel);
				FQPanel fqPanel = new FQPanel(in,out);
				panel = fqPanel;
				panel.setBounds(0, 0, 900, 700);
				add(panel);
				panel.updateUI();
			}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) { }
			public void mouseClicked(MouseEvent e) { }
		});
		sheetMenus[5].addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				if(panel!=null)remove(panel);
				PQPanel pqPanel = new PQPanel(in,out);
				panel = pqPanel;
				panel.setBounds(0, 0, 900, 700);
				add(panel);
				panel.updateUI();
			}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) { }
			public void mouseClicked(MouseEvent e) { }
		});
	}
	/*
    * 方法名：main 
    * 参数： 无
    * 功能： 构建窗体，连接IP123.249.36.173的7000端口
    * 返回值：无
    * 作者：王诗腾
    */
	public static void main(String [] args) throws IOException
	{
		 Manager frame = new Manager("123.249.36.173",7000); 
	}
	/*
    * 方法名：getBytesFromObject 静态函数
    * 参数： obj 一个拓展了Serializable接口的对象
    * 功能： 将一个拓展了Serializable接口的对象转化为byte数组
    * 返回值：bo.toByteArray() 一维数组 
    * 作者：王诗腾
    */
	public static byte[] getBytesFromObject(Serializable obj) throws Exception 
	{
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
    public static Object deserialize(byte[] bytes)
    {
	    Object object = null;
	    try {
	      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);//
	      ObjectInputStream ois = new ObjectInputStream(bis);
	        object = ois.readObject();
	      ois.close();
	      bis.close();
	    } 
	    catch (IOException ex) {
	      ex.printStackTrace();
	    } 
	    catch (ClassNotFoundException ex) {
	      ex.printStackTrace();
	    }
	    return object;
  }
}
