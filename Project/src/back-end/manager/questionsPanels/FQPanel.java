package questionsPanels;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import manager.Manager;
import server.DataByte;

/*
 * 类名：FQPanel 
 * 继承：JPanel
 * 功能：填空题操作界面，用来展示题号、章节号、答案字段。功能包括对答案的增删查改。
 * 作者：王诗腾
 */
public class FQPanel extends JPanel 
{
	JComboBox<Integer>chapterBox;
	JButton queryButton;
	JScrollPane scrollPane;
	JTable table;
	
	DataInputStream in;
	DataOutputStream out;
	/*
    * 构造器名：FQPanel
    * 参数： in 服务器的输入流读取器 out 服务器的输出流写入器
    * 功能：初始化填空题集操作界面，添加章节号选择、查询插入删除按钮、数据表等控件。
    * 作者：王诗腾
    */
	public FQPanel(DataInputStream in,DataOutputStream out)
	{
		this.in = in;this.out=out;
		this.setSize(950,750);
        this.setLayout(null);
        
        JLabel chapterLabel = new JLabel("选择章节");
        chapterLabel.setBounds(10,0,70,30);
        this.add(chapterLabel);
        
        chapterBox = new JComboBox<Integer>();
        for(int i = 1;i<=11;i++)chapterBox.addItem(i);
        chapterBox.setBounds(90, 0, 50, 30);
        this.add(chapterBox);
        
        queryButton = new JButton("查询");
        queryButton.setBounds(150,0,60,30);
        this.add(queryButton);
        setQueryListener();		//设置查询监听器
        
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
		queryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(scrollPane!=null) remove(scrollPane);
				Integer chapter = (Integer) chapterBox.getSelectedItem();	//章节号
				try {
					out.writeUTF("ManagerFillQuestions-"+chapter);
					int dataSize = in.readInt();
					byte[]questionsBytes = new byte[dataSize];
					Thread.sleep(500);
					in.read(questionsBytes);
					DataByte shaper = (DataByte) Manager.deserialize(questionsBytes);
					Vector<Vector<String>> vector = shaper.vector;
					//表格的样式对象
					FQModel modelDemo = new FQModel(vector);
					//为表格（样式）增加修改事件监听器
					modelDemo.addTableModelListener(new TableModelListener() {
						public void tableChanged(TableModelEvent e) {
							int chapter = (int)chapterBox.getSelectedItem();
							String contain = (String)table.getValueAt(table.getSelectedRow(), 1);
							//用户更新题目并确定更新
							int option=JOptionPane.showConfirmDialog(null, "您确定要更新吗？");
							if(option==0) {
								try {
									out.writeUTF(String.format("ManagerFQUpdate-%d-%s-%s",
											chapter,(String)table.getValueAt(table.getSelectedRow(), 0),contain));
									System.out.println(in.readBoolean());
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					});
					table = new JTable(modelDemo);	//根据样式创建表格
					
					//表格各列设置宽度
					table.getColumnModel().getColumn(0).setPreferredWidth(40);
					table.getColumnModel().getColumn(1).setPreferredWidth(840);
					//表格加入滚轮
					scrollPane = new JScrollPane(table);
			        scrollPane.setBounds(0, 35, 900, 650);
			        
			        add(scrollPane);
					System.out.println("queryButton");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
	}
}
/*
 * 类名：FQModel 
 * 继承：AbstractTableModel
 * 功能：为FQPanel界面的数据表定制样式
 * 作者：王诗腾
 */
class FQModel extends AbstractTableModel
{
	/*表格样式类*/
	private String[][] TableData;//用来存放表格数据的线性表
	private String[] TableTitle;//表格的 列标题
	   
	public FQModel(Vector<Vector<String>> vector) 
	{
		TableData=new String[vector.size()][];
		for(int i=0;i<vector.size();i++) {
			String tmp[] = new String[2];
			tmp[0] = vector.get(i).get(0);
			tmp[1] = vector.get(i).get(1);
			TableData[i]= tmp; 
		}
		TableTitle = new String[2];
        TableTitle[0] = "题号";
        TableTitle[1] = "标准答案";
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
        // 判断单元格是否可以编辑，除了第一列都可以编辑
        if(columnIndex==0)return false;
        else return true;
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