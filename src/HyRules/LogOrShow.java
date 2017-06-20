package HyRules;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;

public class LogOrShow 
{
	//只有日志级别大于此值才打印
	private static InforType infoLevel = InforType.Debug;
	//是否写日志
	private static boolean bWriteLog = false;
	//日志名字
	private static String strFileName = null;
	//日志文件
	private static FileWriter fw = null;
	//时间格式
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//日志最大保留大小单位M
	private static int iMaxM = 100;
	//只有debug下展示
	private static boolean bDebug = false;

	public LogOrShow() 
	{
	}
	
	public static boolean getDebug() 
	{
		return LogOrShow.bDebug;
	}
	//获取日志级别
	public static void setDebug(boolean bDebug) 
	{
		LogOrShow.bDebug = bDebug;
	}
	//获取日志级别
	public static int getMaxLogLength() 
	{
		return iMaxM;
	}
	//设置日志级别
	public static void setMaxLogLength(int length) 
	{
		if(length >= 10)
			LogOrShow.iMaxM = length;
	}
	//获取日志级别
	public static InforType getInfoLevel() 
	{
		return infoLevel;
	}
	//设置日志级别
	public static void setInfoLevel(InforType infoLevel) 
	{
		LogOrShow.infoLevel = infoLevel;
	}
	//设置是否同时写日志
	public static boolean isbWriteLog() 
	{
		return bWriteLog;
	}
	//获取是否同时写日志标志
	public static void setbWriteLog(boolean bWriteLog) 
	{
		LogOrShow.bWriteLog = bWriteLog;
	}
	//获取文件名
	public static String getStrFileName() 
	{
		return strFileName;
	}
	//设置文件名
	public static void setStrFileName(String strFileName) 
	{
		LogOrShow.strFileName = strFileName;
	}
	//判断是否过长
	static void CheckFile()
	{
		try
		{
			File logFile = new File(strFileName);
			if(null != logFile && logFile.isFile())
			{
				if(logFile.length() > iMaxM*1024*1024)
				{
					File logFilebak = new File(strFileName + ".bak");
					if(null != logFilebak)
						logFilebak.delete();
					logFile.renameTo(new File(strFileName + ".bak"));
				}
			}
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
	}
	
	//展示时间
	public void ShowTime(InforType type)
	{
		StringBuilder loginfor = new StringBuilder();
		loginfor.append("<------HyRule start at: ");
		loginfor.append(timeFormat.format(new Date()));
		loginfor.append("------>");
		AppendShow(loginfor.toString(),type);
	}
	//展示信息
	public void AppendShow(String strInfor, InforType type)
	{
		AppendShow(strInfor,type,true);
	}
	public void AppendShow(String strInfor, InforType type,boolean carreturn)
	{
		CheckFile();
		
		if((type.compareTo(infoLevel) >= 0 && bDebug) ||
			type.equals(InforType.Error) || type.equals(InforType.Warn))
		{
			if(carreturn)
				System.out.println(strInfor);
			else
				System.out.print(strInfor);
			
			if(bWriteLog && null != strFileName && !strFileName.isEmpty())
			{
				try 
				{
					if(null == fw)
						fw = new FileWriter(strFileName,true);
					fw.append(strInfor);
					if(carreturn)
						fw.append("\n");
					fw.flush();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}
//错误级别定义
enum InforType
{
	Debug, Infor, Warn,Error;
}

//日志缓存
class InforLogBuilder 
{
	private StringBuilder infor = null;
	private boolean bImportant = false;
	
	public void setImportant(boolean bImportant)
	{
		this.bImportant = bImportant;
	}
	private boolean Canappend()
	{
		if(LogOrShow.getInfoLevel().equals(InforType.Debug) ||
		   bImportant || LogOrShow.getDebug())
			return true;
		else 
			return false;
	}
	public InforLogBuilder() 
	{
		if(Canappend())
			infor = new StringBuilder();
	}
	
	public void append(String str)
	{
		if(Canappend())
			infor.append(str);
	}
	
	public void append(char str)
	{
		if(Canappend())
			infor.append(str);
	}
	
	public void append(boolean str)
	{
		if(Canappend())
			infor.append(str);
	}
	
	public void append(int str)
	{
		if(Canappend())
			infor.append(str);
	}
	
	public void append(double str)
	{
		if(Canappend())
			infor.append(str);
	}
	
	public String toString()
	{
		if(Canappend())
			return infor.toString();
		else
			return null;
	}
}
