package server;
import java.io.Serializable;
import java.util.Vector;

/*
 * 类名：DataByte 拓展Serializable接口
 * 功能：内含一个二维vector数组，用于保存信息，可以被转化为byte[]数组进行传输
 * 作者：王诗腾
 */
public class DataByte implements Serializable
{
	private static final long serialVersionUID = 6529685098267757690L;
	public Vector<Vector<String>> vector=new Vector<Vector<String>>();
}
