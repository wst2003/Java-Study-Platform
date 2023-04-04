package questionsPanels;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import manager.Manager;

import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import server.DataByte;

/*
 * 类名：QuestionsPanel 
 * 继承：JPanel
 * 功能：习题集操作界面，用来展示题号、题型、章节号、题干描述。
 * 		 功能包括对四种题型的增删查改，其中能被更新的只有题干描述。
 * 作者：王诗腾
 */
public class QuestionsPanel extends JPanel 
{
	JComboBox<Integer>chapterBox;
	JComboBox<String>typeBox;
	JButton queryButton;
	JButton deleteButton;
	JButton addButton;
	JScrollPane scrollPane;
	JTable table;
	
	DataInputStream in;
	DataOutputStream out;
	
	String []columnNames = {"章节号","题型","题号","题目描述"};
	/*
    * 构造器名：QuestionsPanel
    * 参数： in 服务器的输入流读取器 out 服务器的输出流写入器
    * 功能：初始化习题集操作界面，添加章节号选择、题型选择、查询插入删除按钮、数据表等控件。
    * 作者：王诗腾
    */
	public QuestionsPanel(DataInputStream in,DataOutputStream out)
	{
		/*构造函数，设置各控件位置*/
		this.in = in;
		this.out=out;
		
		this.setSize(950,750);
        this.setLayout(null);
        //设置增删查改控件
        JLabel chapterLabel = new JLabel("选择章节");
        chapterLabel.setBounds(10,0,70,30);
        this.add(chapterLabel);
        
        chapterBox = new JComboBox<Integer>();
        for(int i = 1;i<=11;i++)chapterBox.addItem(i);
        chapterBox.setBounds(90, 0, 50, 30);
        
        JLabel typeLabel = new JLabel("选择题型");
        typeLabel.setBounds(150,0,90,30);
        this.add(typeLabel);
        
        typeBox = new JComboBox<String>();
        typeBox.addItem("选择题");typeBox.addItem("判断题");typeBox.addItem("填空题");typeBox.addItem("编程题");
        
        typeBox.setBounds(230,0,90,30);
        this.add(chapterBox);
        this.add(typeBox);
        
        queryButton = new JButton("查询");
        queryButton.setBounds(350,0,60,30);
        this.add(queryButton);
        setQueryListener();		//设置查询监听器
        
        deleteButton = new JButton("删除");
        deleteButton.setBounds(420, 0, 60, 30);
        this.add(deleteButton);
        setDeleteListener();	//设置删除监听器
        
        addButton = new JButton("添加");
        addButton.setBounds(490,0,60,30);
        this.add(addButton);
        setAddListener();
        
        this.setVisible(true);
	}
	
	/*
    * 方法名：setQueryListener 
    * 参数： 无
    * 功能： 为查询按钮设置监听器
    * 返回值：无
    * 作者：王诗腾
    */
	void setQueryListener()
	{
		/*查询事件，查询给定章节、给定题型的题目（创建表格）*/
		queryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(scrollPane!=null) remove(scrollPane);
				Integer chapter = (Integer) chapterBox.getSelectedItem();	//章节号
				String type = (String) typeBox.getSelectedItem();   //题型
				try {
					//获取题目的二维vector
					out.writeUTF("ManagerQuestions-"+chapter+"-"+type);
					int dataSize = in.readInt();
					byte[]questionsBytes = new byte[dataSize];
					Thread.sleep(500);
					in.read(questionsBytes);
					DataByte shaper = (DataByte) Manager.deserialize(questionsBytes);
					Vector<Vector<String>> vector = shaper.vector;
					//表格的样式对象
					QPModel modelDemo = new QPModel(columnNames,chapter,type,vector);
					//为表格（样式）增加修改事件监听器
					modelDemo.addTableModelListener(new TableModelListener() {
						public void tableChanged(TableModelEvent e) {
							int chapter = (int)chapterBox.getSelectedItem();
							String type = (String) typeBox.getSelectedItem();
							String number = (String)table.getValueAt(table.getSelectedRow(), 2);
							String describe = (String)table.getValueAt(table.getSelectedRow(), 3);
							//用户更新题目描述并确定更新
							int option=JOptionPane.showConfirmDialog(null, "您确定要更新吗？");
							if(option==0) {
								try {
									out.writeUTF("ManagerQuestionsUpdate-"+chapter+"-"+type+"-"+number+"-"+describe);
									System.out.println(in.readBoolean());
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					});
					table = new JTable(modelDemo);	//根据样式创建表格
					
					//表格各列设置宽度
					table.getColumnModel().getColumn(0).setPreferredWidth(50);
					table.getColumnModel().getColumn(1).setPreferredWidth(80);
					table.getColumnModel().getColumn(2).setPreferredWidth(50);
					table.getColumnModel().getColumn(3).setPreferredWidth(890-50-50-80);
					//表格加入滚轮
					scrollPane = new JScrollPane(table);
			        scrollPane.setBounds(0, 35, 900, 650);
			        
			        add(scrollPane);
					System.out.println("queryButton");
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	/*
    * 方法名：setDeleteListener 
    * 参数： 无
    * 功能： 为删除按钮设置监听器
    * 返回值：无
    * 作者：王诗腾
    */
	void setDeleteListener()
	{
		/*删除事件，删除给定章节、给定题型、给定题号的题目*/
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(table==null)return;
				System.out.println(table.getSelectedRow());
				try {
					int chapter = (int)chapterBox.getSelectedItem();
					String type = (String) typeBox.getSelectedItem();
					String number = (String)table.getValueAt(table.getSelectedRow(), 2);
					System.out.println("ManagerDelete-"+chapter+"-"+type+"-"+number);
					int option=JOptionPane.showConfirmDialog(null, "您确定要删除吗？");
					if(option==0) {
					out.writeUTF("ManagerDelete-"+chapter+"-"+type+"-"+number);
					if(in.readBoolean())queryButton.doClick();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	/*
    * 方法名：setAddListener 
    * 参数： 无
    * 功能： 为添加按钮设置监听器
    * 返回值：无
    * 作者：王诗腾
    */
	void setAddListener()
	{
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "";
				String inputString;
				String chapter = ((Integer) chapterBox.getSelectedItem()).toString();
				String type = (String) typeBox.getSelectedItem();
				if(type.equals("选择题"))message = "请输入题号、题目描述、四个选项、标准答案字段，用-隔开";
				if(type.equals("判断题"))message = "请输入题号、题目描述、标准答案字段，用-隔开";
				if(type.equals("填空题"))message = "请输入题号、题目描述、参考答案字段，用-隔开";
				inputString=JOptionPane.showInputDialog(null, message);
				String sql1,sql2;
				String messages[] = inputString.split("-");
				try {
					Integer.parseInt(messages[0]);
				} catch (NumberFormatException e2) {
					JOptionPane.showMessageDialog(null, "题号必须是正整数！");
					return;
				}
				if(type.equals("选择题")) {
					if(!messages[6].equals("A")&&!messages[6].equals("B")&&!messages[6].equals("C")&&!messages[6].equals("D")) {
						JOptionPane.showMessageDialog(null, "答案只能是 A B C D 其中之一！");
						return;
					}
					sql1 = String.format("insert into questions values(%s,'%s',%s,'%s');", chapter,type,messages[0],messages[1]);
					sql2 = String.format("insert into selectquestions values(%s,'%s',%s,'%s','%s','%s','%s','%s');",
							chapter,type,messages[0],messages[2],messages[3],messages[4],messages[5],messages[6]);
					try {
						out.writeUTF("ManagerAdd-"+sql1+"-"+sql2);
						if(!in.readBoolean())JOptionPane.showMessageDialog(null, "添加题目失败！");
						else queryButton.doClick();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				if(type.equals("判断题")) {
					sql1 = String.format("insert into questions values(%s,'%s',%s,'%s');", chapter,type,messages[0],messages[1]);
					sql2 = String.format("insert into judgequestions values(%s,'%s',%s,'%s');",
							chapter,type,messages[0],messages[2]);
					try {
						out.writeUTF("ManagerAdd-"+sql1+"-"+sql2);
						if(!in.readBoolean())JOptionPane.showMessageDialog(null, "添加题目失败！");
						else queryButton.doClick();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				if(type.equals("填空题")) {
					sql1 = String.format("insert into questions values(%s,'%s',%s,'%s');", chapter,type,messages[0],messages[1]);
					sql2 = String.format("insert into fillquestions values(%s,'%s',%s,'%s');",
							chapter,type,messages[0],messages[2]);
					try {
						out.writeUTF("ManagerAdd-"+sql1+"-"+sql2);
						if(!in.readBoolean())JOptionPane.showMessageDialog(null, "添加题目失败！");
						else queryButton.doClick();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
}



/*
 * 类名：QPModel 
 * 继承：AbstractTableModel
 * 功能：为QuestionsPanel界面的数据表定制样式
 * 作者：王诗腾
 */
class QPModel extends AbstractTableModel
{    
   private String[][] TableData;//用来存放表格数据的线性表
   private String[] TableTitle;//表格的 列标题

   public QPModel(String []columnNames,int chapter,String type,Vector<Vector<String>> vector)
   {
	   /*构造函数，设置表中数据、表头*/
	   TableData = new String[vector.size()][];
        for(int i=0;i<vector.size();i++) {
        	String tmp[] = new String[4];
        	tmp[0]= ""+chapter;tmp[1]= type;tmp[2]= vector.get(i).get(0);tmp[3]= vector.get(i).get(1);
			TableData[i]= tmp; 
		}
        TableTitle = new String[4];
        TableTitle[0] = "章节号";
        TableTitle[1] = "题型";
        TableTitle[2] = "题号";
        TableTitle[3] = "题目描述";
   }
   		
   
        @Override
        public int getRowCount() {
            return TableData.length;
        }
 
        @Override
        public int getColumnCount() {
            return TableTitle.length;
        }
 
        @Override
        public Object getValueAt(int row, int col) {
            return TableData[row][col];
        }
 
        @Override
        public String getColumnName(int col) {
            return TableTitle[col];
        }
 
        @Override
        public Class<?> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
 
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // 判断单元格是否可以编辑，只有第三列可以编辑
            if(columnIndex==3)return true;
            else return false;
        }
 
        @Override
        public void setValueAt(Object value, int row, int col) {
        	TableData[row][col] = (String) value;
            fireTableCellUpdated(row, col);
        }
 
        public void mySetValueAt(Object value, int row, int col) {
        	TableData[row][col] = (String) value;
        }
}
