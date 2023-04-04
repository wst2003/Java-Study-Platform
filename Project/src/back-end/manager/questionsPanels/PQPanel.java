package questionsPanels;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import server.DataByte;

/*
 * 类名：PQPanel 
 * 继承：JPanel
 * 功能：编程题操作界面，用来展示题号、章节号、答案字段。功能包括对答案的增删查改。
 * 作者：王诗腾
 */
public class PQPanel extends JPanel{
	JComboBox<Integer>chapterBox;
	JComboBox<Integer>numberBox;
	JButton queryButton;
	JButton updateButton;
	
	JTextArea answerTextArea;
	JScrollPane scrollPane;
	DataInputStream in;
	DataOutputStream out;
	/*
    * 构造器名：PQPanel
    * 参数： in 服务器的输入流读取器 out 服务器的输出流写入器
    * 功能：初始化填空题集操作界面，添加章节号选择、查询插入删除按钮、数据表等控件。
    * 作者：王诗腾
    */
	public PQPanel(DataInputStream in,DataOutputStream out)
	{
		this.in = in;this.out=out;
		this.setSize(950,750);
        this.setLayout(null);
        
        JLabel chapterLabel = new JLabel("选择章节");
        chapterLabel.setBounds(20,0,70,30);
        this.add(chapterLabel);
        
        chapterBox = new JComboBox<Integer>();
        for(int i = 2;i<=11;i++)chapterBox.addItem(i);
        chapterBox.removeItemAt(6);chapterBox.removeItemAt(7);
        chapterBox.setBounds(90, 0, 50, 30);
        this.add(chapterBox);
        
        JLabel numberLabel = new JLabel("选择题号");
        numberLabel.setBounds(160,0,70,30);
        this.add(numberLabel);
        
        numberBox =new JComboBox<Integer>();
        numberBox.setBounds(225, 0, 50, 30);
        this.add(numberBox);
        
        setChapterBoxListener();
        chapterBox.setSelectedIndex(1);
        chapterBox.setSelectedIndex(0);
        
        queryButton = new JButton("查询");
        queryButton.setBounds(290,0,60,30);
        this.add(queryButton);
        
        updateButton = new JButton("更新");
        updateButton.setBounds(590,0,60,30);
        this.add(updateButton);
        
        answerTextArea = new JTextArea();
        answerTextArea.setTabSize(2);
        answerTextArea.setFont(new Font(answerTextArea.getFont().getName(), answerTextArea.getFont().getStyle(), answerTextArea.getFont().getSize()+8));
        scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 35, 900, 650);
        scrollPane.setViewportView(answerTextArea);
        add(scrollPane);
        
        setQueryListener();		//设置查询监听器
        setUpdateListener();
        this.setVisible(true);
	}
	/*
    * 方法名：setChapterBoxListener 
    * 参数： 无
    * 功能： 为章节选择器设置监听器，根据章节名查询子章节名
    * 返回值：无
    * 作者：王诗腾
    */
	void setChapterBoxListener()
	{
		chapterBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				numberBox.removeAllItems();
				if(e.getStateChange()==e.SELECTED){
					int chapter = (Integer) e.getItem();
					try {
						out.writeUTF(String.format("PQuestionsNum-%d", chapter));
						int numberCount = in.readInt();
						for(int i = 1;i<=numberCount;i++) {
							numberBox.addItem(i);
						}
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
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
				try {
					out.writeUTF(String.format("PQuestion-%d-%d",(Integer)chapterBox.getSelectedItem(),(Integer)numberBox.getSelectedItem()));
					Thread.sleep(100);
					answerTextArea.setText(in.readUTF());
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	/*
    * 方法名：setUpdateListener 
    * 参数： 无
    * 功能： 为更新按钮设置监听器
    * 返回值：无
    * 作者：王诗腾
    */
	void setUpdateListener()
	{
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(answerTextArea.getText().length()==0)return;
				try {
					out.writeUTF(String.format("PQUpdate分%d分%d分%s", (Integer)chapterBox.getSelectedItem(),(Integer)numberBox.getSelectedItem(),
							answerTextArea.getText().replaceAll("'", "\\\\'")));
					if(in.readBoolean())JOptionPane.showMessageDialog(null, "您已经成功更新！");
					else JOptionPane.showMessageDialog(null, "更新失败！");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

}

