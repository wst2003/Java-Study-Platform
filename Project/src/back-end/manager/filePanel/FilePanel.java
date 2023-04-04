package filePanel;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import manager.Manager;
import server.DataByte;

/*
 * 类名：FilePanel 
 * 继承：JPanel
 * 功能：图像文件题操作界面，用来展示图片，可以对某小节的图片进行增删查改。
 * 作者：王诗腾
 */
public class FilePanel extends JPanel
{
	JComboBox<String>chapterBox;	   //章节名选择框
	JComboBox<String>subChapterBox;    //子章节名选择框
	JComboBox<Integer>imageNumBox;	   //图片序号选择框
	JButton queryButton;			   //查询按钮
	JButton updateButton;			   //更新按钮
	JButton addButton;				   //尾插按钮
	JButton deleteButton;			   //尾删按钮
	
	JScrollPane scrollPane;			   //滚轮面板
	DataInputStream in;
	DataOutputStream out;
	
	Vector<Vector<String>> vector=new Vector<Vector<String>>();
	/*
    * 构造器名：FilePanel
    * 参数： in 服务器的输入流读取器 out 服务器的输出流写入器
    * 功能：初始化图像集操作界面，添加章节号选择、查询插入删除按钮、图片显示面板等控件。
    * 作者：王诗腾
    */
	public FilePanel(DataInputStream in,DataOutputStream out) 
	{
		this.in = in;this.out=out;
		this.setSize(950,750);
        this.setLayout(null);
        
        JLabel chapterLabel = new JLabel("选择章节");
        chapterLabel.setBounds(20,0,70,30);
        this.add(chapterLabel);
        
        chapterBox = new JComboBox<String>();
        
        chapterBox.setBounds(90, 0, 150, 30);
        this.add(chapterBox);
        
        JLabel subChapterLabel = new JLabel("选择子章节");
        subChapterLabel.setBounds(250,0,70,30);
        this.add(subChapterLabel);
        
        subChapterBox =new JComboBox<String>();
        subChapterBox.setBounds(330, 0, 150, 30);
        this.add(subChapterBox);
       
        try {
			out.writeUTF("chapterName");
			byte bytes[] = new byte[in.readInt()];
			Thread.sleep(100);
		    in.read(bytes);
		    vector=((DataByte) Manager.deserialize(bytes)).vector;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
       
        String tmpString = "";
        for(Vector<String>v  : vector) {
        	if(!v.get(0).equals(tmpString)) {
        		tmpString = v.get(0);
        		chapterBox.addItem(tmpString);
        	}
        }
        setComboBox();
        chapterBox.setSelectedIndex(1);
        chapterBox.setSelectedIndex(0);
        
        imageNumBox = new JComboBox<Integer>();
        imageNumBox.setBounds(490, 0, 60, 30);
        subChapterBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				imageNumBox.removeAllItems();
				try {
					out.writeUTF(String.format("imageNum-%d-%d", chapterBox.getSelectedIndex()+1,subChapterBox.getSelectedIndex()+1));
					int s = in.readInt();
					for(int i = 1;i<=s;i++) {
						imageNumBox.addItem(i);
					}
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
        this.add(imageNumBox);
        
        queryButton = new JButton("查询");
        queryButton.setBounds(590,0,60,30);
        this.add(queryButton);
        setQueryListener();
        
        updateButton = new JButton("更新");
        updateButton.setBounds(660,0,60,30);
        this.add(updateButton);
        setUpdateListener();
        
        addButton = new JButton("尾插");
        addButton.setBounds(730,0,60,30);
        this.add(addButton);
        setAddListener();
        
        deleteButton = new JButton("尾删");
        deleteButton.setBounds(800,0,60,30);
        this.add(deleteButton);
        setDeleteListener();
        
        subChapterBox.setSelectedIndex(1);subChapterBox.setSelectedIndex(0);
      
        this.setVisible(true);
	}
	/*
    * 方法名：setComboBox 
    * 参数： 无
    * 功能： 为章节选择器设置监听器，根据章节名查询子章节名
    * 返回值：无
    * 作者：王诗腾
    */
	void setComboBox()
	{
		chapterBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				subChapterBox.removeAllItems();
				for(Vector<String>v  : vector) {
		        	if(((String)e.getItem()).equals(v.get(0))) {
		        		subChapterBox.addItem(v.get(1));
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
					System.out.println(String.format("imageFile-%d-%d-%d", 
							chapterBox.getSelectedIndex()+1,subChapterBox.getSelectedIndex()+1,imageNumBox.getSelectedIndex()+1));
					out.writeUTF(String.format("imageFile-%d-%d-%d", 
							chapterBox.getSelectedIndex()+1,subChapterBox.getSelectedIndex()+1,imageNumBox.getSelectedIndex()+1));
					int s = in.readInt();
					byte bytes[] = new byte[s];
					Thread.sleep(500);
					in.readFully(bytes);
					if(scrollPane!=null) remove(scrollPane);
					ImageIcon imageIcon = new ImageIcon(bytes);
					imageIcon.setImage(imageIcon.getImage().getScaledInstance(960, 1200, Image.SCALE_AREA_AVERAGING));
					JPanel imagePanel = new ImagePanel(imageIcon);
					scrollPane = new JScrollPane();
					scrollPane.setViewportView(imagePanel);
			        scrollPane.setBounds(0, 35, 900, 650);
			        
			        add(scrollPane);
			        updateUI();
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
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setFileFilter(new FileNameExtensionFilter(null, "png"));
				if (jFileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					File pngFile= jFileChooser.getSelectedFile();
					byte[] imageBytes = null;
					try (FileInputStream fileInputStream = new FileInputStream(pngFile);) {
			            imageBytes= new byte[fileInputStream.available()];
			            fileInputStream.read(imageBytes);	//写入图片
			            out.writeUTF(String.format("imageUpdate-%d-%d-%d",
			            		chapterBox.getSelectedIndex()+1,subChapterBox.getSelectedIndex()+1,imageNumBox.getSelectedIndex()+1));
			            out.writeInt(imageBytes.length);
			            out.write(imageBytes);
			        } catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} 
				}
			}
		});
	}
	/*
    * 方法名：setAddListener 
    * 参数： 无
    * 功能： 为尾插按钮设置监听器
    * 返回值：无
    * 作者：王诗腾
    */
	void setAddListener()
	{
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setFileFilter(new FileNameExtensionFilter(null, "png"));
				if (jFileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					File pngFile= jFileChooser.getSelectedFile();
					byte[] imageBytes = null;
					try (FileInputStream fileInputStream = new FileInputStream(pngFile);) {
			            imageBytes= new byte[fileInputStream.available()];
			            fileInputStream.read(imageBytes);	//写入图片
			            out.writeUTF(String.format("imageAdd-%d-%d-%d",
			            		chapterBox.getSelectedIndex()+1,subChapterBox.getSelectedIndex()+1,imageNumBox.getItemCount()+1));
			            out.writeInt(imageBytes.length);
			            out.write(imageBytes);
			            int tmp = subChapterBox.getSelectedIndex();
						subChapterBox.setSelectedIndex(0);
						subChapterBox.setSelectedIndex(tmp);
			        } catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} 
				}
			}
		});
	}
	/*
    * 方法名：setAddListener 
    * 参数： 无
    * 功能： 为尾删按钮设置监听器
    * 返回值：无
    * 作者：王诗腾
    */
	void setDeleteListener()
	{
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					out.writeUTF(String.format("imageDelete-%d-%d-%d",
							chapterBox.getSelectedIndex()+1,subChapterBox.getSelectedIndex()+1,imageNumBox.getItemCount()));
					JOptionPane.showMessageDialog(null, in.readUTF());
					int tmp = subChapterBox.getSelectedIndex();
					subChapterBox.setSelectedIndex(0);
					subChapterBox.setSelectedIndex(tmp);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
/*
 * 类名：ImagePanel 
 * 继承：JPanel
 * 功能：用于显示特定的图像文件
 * 作者：王诗腾
 */
class ImagePanel extends JPanel
{
	ImageIcon image;
	public ImagePanel(ImageIcon image)
	{
		this.image = image;
		setSize(900,650);
		JLabel jLabel = new JLabel(image);
		jLabel.setBounds(0, 0, 900, 650);
		add(jLabel);
		this.setVisible(true);
	}
	
}
