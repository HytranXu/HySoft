package HyRules;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

/*---------------------------------------*/
//数据定义语法树
/*---------------------------------------*/
public class HyRuleDataDef 
{
	public HyRuleDataDef() 
	{}
}
/*
定义：
ObjDef--->mem(Map)-->objData-->单个
                            -->数组

数据：
ObjData-->Value  -->Object
 			 Values -->vector<Object>
 			 
 			 ------以上是简单已定义-----
 			 
 			 -------以下是自定义-------
 			 
 			 Value	-->ObjDef
 			 Values  -->vector<ObjDef>一维数组
 			 	     -->vector<ObjDef>多维数组
 */
/*---------------------------------------*/
//数据定义语法树
/*---------------------------------------*/
//对象符号表
class DataTAB
{
	//对象符号表
	private Map<String,Obj> members = null;
	//属于哪个模块
	private String strName = null;
	//成功
	public static final int dt_OK = 0;
	//名字已经存在
	public static final int dt_NameExist = 1;
	//参数错误
	public static final int dt_ParamWrong = 2;
	//对象不存在
	public static final int dt_MemberNotExits = 3;
	protected LogOrShow logShow = null;
	
	public DataTAB(String Name,LogOrShow ls)
	{
		strName = Name;
		members = new HashMap<String,Obj>();
		if(null != ls)
			logShow = ls;
	}
	//--------------------------------------------------------------------
	//数据定义操作
	//保存ObjDef，值使用ObjData
	//单个数据增加已定义数据
	int addDataDef(String Dataname,String memName, String strType, Object Value)
	{
		int result = dt_OK;
		if(Dataname == null || null == memName || strType == null 
		  || Dataname.isEmpty() || strType.isEmpty() || memName.isEmpty())
			return dt_ParamWrong;
			
		ObjDef objdef = null;
		if(members.containsKey(Dataname))
		{
			objdef = (ObjDef) members.get(Dataname);
			if(objdef.mem.containsKey(memName))
			{
				return dt_NameExist;
			}
		}
		else
			objdef = new ObjDef(Dataname,logShow);
		
		ObjData objvalue = new ObjData(strType,Value,logShow);
		objvalue.bPartofSelf = true;
		objdef.mem.put(memName, objvalue);
		members.put(Dataname, objdef);
			
		return result;
	}
	//单个添加自定义数据
	int addDataDef(String Dataname,String memName, String strType, ObjDef Value)
	{
		int result = dt_OK;
		if(Dataname == null || null == memName || strType == null 
		  || Dataname.isEmpty() || strType.isEmpty() || memName.isEmpty())
			return dt_ParamWrong;
			
		ObjDef objdef = null;
		if(members.containsKey(Dataname))
		{
			objdef = (ObjDef) members.get(Dataname);
			if(objdef.mem.containsKey(memName))
			{
				return dt_NameExist;
			}
		}
		else
			objdef = new ObjDef(Dataname,logShow);
		
		ObjData objvalue = new ObjData(strType,Value,logShow);
		objvalue.bPartofSelf = true;
		objdef.mem.put(memName, objvalue);
		members.put(Dataname, objdef);
			
		return result;
	}
	//数组增加
	int addDataDefArray(String Dataname,String memName, String strType, Vector<Object> Values, boolean bSelfDef)
	{
		int result = dt_OK;
		if(Dataname == null || null == memName || strType == null || Values == null
		  || Dataname.isEmpty() || strType.isEmpty() || memName.isEmpty())
			return dt_ParamWrong;
			
		ObjDef objdef = null;
		if(members.containsKey(Dataname))
		{
			objdef = (ObjDef)members.get(Dataname);
			if(objdef.mem.containsKey(memName))
			{
				return dt_NameExist;
			}
		}
		else
			objdef = new ObjDef(Dataname,logShow);
			
		ObjData objvalue = new ObjData(strType,Values,bSelfDef,logShow);
		objvalue.bPartofSelf = true;
		objdef.mem.put(memName, objvalue);
		members.put(Dataname, objdef);
			
		return result;
	}
	//获取对象
	ObjDef lookupDataDef(String name)
	{
		if(null == name || name.isEmpty())
			return null;
		return (ObjDef)members.get(name);
	}
		
	//--------------------------------------------------------------------
	//符号表操作
	//添加已定义单项
	int setParam(String name,String strType, Object Value,boolean bAdd)
	{
		int result = dt_OK;
		if(name == null || strType == null || name.isEmpty() || strType.isEmpty())
			return dt_ParamWrong;
			
		ObjData mem = null;
		if(members.containsKey(name))
		{
			mem = (ObjData)members.get(name);
			if(bAdd && null != mem)
			{
				result = dt_NameExist;
				return result;
			}
			if(!mem.strType.equals(strType))
			{
				result = dt_NameExist;
				return result;
			}
			mem.Value = Value;
			mem.dataType = DataType.Defined;
		}
		else
			mem = new ObjData(strType,Value,logShow);
			
		members.put(name, mem);
		return result;
	}
	//添加自定义单项
	int setParam(String name,String strType, ObjDef Value,boolean bAdd)
	{
		int result = dt_OK;
		if(name == null || strType == null || name.isEmpty() || strType.isEmpty())
			return dt_ParamWrong;
			
		ObjData mem = null;
		if(members.containsKey(name))
		{
			Value.bDefine = false;
			mem = (ObjData)members.get(name);
			if(bAdd && null != mem)
			{
				result = dt_NameExist;
				return result;
			}
			if(!mem.strType.equals(strType))
			{
				result = dt_NameExist;
				return result;
			}
			mem.Value = Value;
			mem.dataType = DataType.SelfDef;
		}
		else
			mem = new ObjData(strType,Value,logShow);
			
		members.put(name, mem);
		return result;
	}
	//简单对象数组设置
	int setParamArray(String name,String strType,Vector<Object> Values,boolean bSelfDef,boolean bAdd)
	{
		int result = dt_OK;
		if(Values == null || name == null || strType == null 
		  || name.isEmpty() || strType.isEmpty())
			return dt_ParamWrong;
			
		ObjData mem = null;
		if(members.containsKey(name))
		{
			mem = (ObjData)members.get(name);
			if(bAdd && null != mem)
			{
				result = dt_NameExist;
				return result;
			}
			if(!mem.strType.equals(strType))
			{
				result = dt_NameExist;
				return result;
			}
			mem.iCounts = Values.size();
			mem.Values = Values;
			mem.bArray = true;
			if(bSelfDef)
				mem.dataType = DataType.SelfDef;
			else
				mem.dataType = DataType.Defined;
		}
		else
			mem = new ObjData(strType,Values,bSelfDef,logShow);
		
		members.put(name, mem);			
		return result;
	}
	//获取单个简单对象
	ObjData lookup(String name)
	{
		if(null == name || name.isEmpty())
			return null;
		ObjData sobj = (ObjData)members.get(name);
		
		if(null != sobj)
			return sobj;
		else
			return null;
	}	
	//删除某个对象
	int Delete(String name)
	{
		if(null == name)
			return dt_ParamWrong;
		else
			members.remove(name);
		
		return dt_OK;
	}
	//显示符号表
	void DumpVariables()
	{
		//Dump name
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("Variables: ");
		infor.append(strName);
		infor.append("\n{");
		logShow.AppendShow(infor.toString(),InforType.Infor);
		ObjData objd = null;
		for(Entry<String,Obj> mem : members.entrySet())
		{
			infor = new InforLogBuilder();
			objd = (ObjData)mem.getValue();
			infor.append("\tName: ");
			infor.append(mem.getKey());
			infor.append("\n\t{");
			logShow.AppendShow(infor.toString(),InforType.Infor);
			objd.Dump("\t",false);
			logShow.AppendShow("\t}",InforType.Infor);
		}
		logShow.AppendShow("}",InforType.Infor);
	}
	//展示定义表
	void DumpDefines()
	{
		//Dump name
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("Define: ");
		infor.append(strName);
		infor.append("\n{");
		logShow.AppendShow(infor.toString(),InforType.Infor);
		ObjDef objd = null;
		for(Entry<String,Obj> mem : members.entrySet())
		{
			infor = new InforLogBuilder();
			infor.append("\tName: ");
			infor.append(mem.getKey());
			infor.append("\n\t{");
			logShow.AppendShow(infor.toString(),InforType.Infor);
			objd = (ObjDef)mem.getValue();
			objd.Dump("\t",true);
			logShow.AppendShow("\t}",InforType.Infor);
		}
		logShow.AppendShow("}",InforType.Infor);
	}
}
/*---------------------------------------*/
//对象定义
/*---------------------------------------*/
/*
 * 自定义对象定义
 * 每个项可以使简单已定义，也可以是自定义
 */
class ObjDef extends Obj
{
	public Map<String,ObjData> mem = new HashMap<String,ObjData>();
	
	public ObjDef(String Type,LogOrShow ls)
	{
		dataType = DataType.SelfDef;
		bDefine = true;
		strType = Type;
		logShow = ls;
	}
	
	public void Dump(String preInfo, boolean bCompile)
	{
		if(null != mem)
		{
			ObjData objd = null;
			String key = null;
			for(Entry<String,ObjData> df : mem.entrySet())
			{
				key = df.getKey();
				objd = df.getValue();
				
				if(objd.bArray)
				{
					InforLogBuilder infor = new InforLogBuilder();
					infor.append(preInfo);
					infor.append("\tName: ");
					infor.append(key);
					logShow.AppendShow(infor.toString(),InforType.Infor);
				}
				else
				{
					InforLogBuilder infor = new InforLogBuilder();
					infor.append(preInfo);
					infor.append("\tName: ");
					infor.append(key);
					if(objd.dataType.equals(DataType.Defined))
						logShow.AppendShow(infor.toString(),InforType.Infor,false);
					else
						logShow.AppendShow(infor.toString(),InforType.Infor);
				}
				objd.Dump(preInfo,bCompile);
			}
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException 
	{
		ObjDef result = new ObjDef(this.strType,this.logShow);
		result.bArray = this.bArray;
		result.iCounts = this.iCounts;
		result.dataType = this.dataType;
		if(this.mem != null)
		{
			Map<String,ObjData> md = new HashMap<String,ObjData>();
			HashMap<String,ObjData> source = (HashMap<String,ObjData>)mem;
			ObjData tmpObj = null;
			for(String key : source.keySet())
			{
				tmpObj = source.get(key);
				md.put(key, tmpObj.copy());
			}
			result.mem = md;
		}
		return result;
	}
	
	ObjDef copy()
	{
		try
		{
			return (ObjDef)clone();
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
/*
 * 对象数据
 * 如果是简单对象，Object可以是Integer、Double、String、Character等
 * 如果是自定义对象，Object是ObjDef类型
 */
class ObjData extends Obj
{
	//单个值
	public Object Value = null;
	//数组
	public Vector<Object> Values = null;
	//是否是自定义数据部分，展示使用
	public boolean bPartofSelf = false;
	
	public ObjData(String sType, Object obj,LogOrShow ls)
	{
		strType = sType;
		dataType = DataType.Defined;
		Value = obj;
		logShow = ls;
	}
	public ObjData(String sType, ObjDef obj,LogOrShow ls)
	{
		strType = sType;
		dataType = DataType.SelfDef;
		Value = obj;
		logShow = ls;
	}
	public ObjData(String sType, Vector<Object> obj,boolean bSelfDefine,LogOrShow ls)
	{
		strType = sType;
		if(bSelfDefine)
			dataType = DataType.SelfDef;
		else
			dataType = DataType.Defined;
		Values = obj;
		iCounts = obj.size();
		bArray = true;
		logShow = ls;
	}
	
	//展示信息
	public void Dump(String preInfo, boolean bCompile)
	{
		if(dataType == DataType.SelfDef || bArray)
			super.Dump(preInfo,bCompile);
		
		if(dataType == DataType.SelfDef)
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append(preInfo);
			infor.append("\t{");
			logShow.AppendShow(infor.toString(),InforType.Infor);
		}
		
		//数组
		if(bArray)
			DumpArray(Values,"\t" + preInfo,bCompile);
		//单个自定义
		else if(dataType == DataType.SelfDef && null != Value)
		{
			((ObjDef)Value).Dump("\t" + preInfo,bCompile);
		}
		//单个已定义
		else if(dataType == DataType.Defined)
		{
			InforLogBuilder infor = new InforLogBuilder();
			if(!bCompile && !bPartofSelf)
			{
				infor.append("\t");
				infor.append(preInfo);
			}
			else
				infor.append(" ");	
			infor.append("Type: ");
			infor.append(strType);
			infor.append(" Value: ");
			if(null != Value)
				infor.append(Value.toString());
			else
				infor.append("null");
			logShow.AppendShow(infor.toString(),InforType.Infor);
		}
		else
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append(preInfo);
			infor.append("!!!!Wrong happened!!!!!");
			logShow.AppendShow(infor.toString(),InforType.Infor);
		}
		
		if(dataType == DataType.SelfDef)
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append(preInfo);
			infor.append("\t}");
			logShow.AppendShow(infor.toString(),InforType.Infor);
		}
	}
	//显示数组及多重数组
	@SuppressWarnings("unchecked")
	private void DumpArray(Vector<Object> array, String preInfor, boolean bCompile)
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append(preInfor);
		infor.append("[");
		logShow.AppendShow(infor.toString(),InforType.Infor);
		for(Object obj : array)
		{
			if(null != obj)
			{
				if(obj instanceof Vector<?>)
				{
					DumpArray((Vector<Object>)obj,"\t" + preInfor,bCompile);
				}
				else if(obj instanceof ObjDef)
				{
					infor = new InforLogBuilder();
					infor.append(preInfor);
					infor.append("\t{");
					logShow.AppendShow(infor.toString(),InforType.Infor);
					
					((ObjDef)obj).Dump("\t" + preInfor,bCompile);
					
					infor = new InforLogBuilder();
					infor.append(preInfor);
					infor.append("\t}");
					logShow.AppendShow(infor.toString(),InforType.Infor);
				}
				else if(obj instanceof Object)
				{
					infor = new InforLogBuilder();
					infor.append("\t");
					infor.append(preInfor);
					infor.append("Value:");
					infor.append(obj.toString());
					logShow.AppendShow(infor.toString(),InforType.Infor);
				}
			}
			else
			{
				infor = new InforLogBuilder();
				infor.append(preInfor);
				infor.append("\t{\n");
				infor.append("\t\t");
				infor.append(preInfor);
				infor.append("Value : null");
				infor.append("\n");
				infor.append(preInfor);
				infor.append("\t}");
				logShow.AppendShow(infor.toString(),InforType.Infor);
			}
		}
		infor = new InforLogBuilder();
		infor.append(preInfor);
		infor.append("]");
		logShow.AppendShow(infor.toString(),InforType.Infor);
	}
	
	//对象复制深复制
	ObjData copy() throws CloneNotSupportedException 
	{
		ObjData result = new ObjData(this.strType,(Object)null,logShow);
		if(this.Value != null && this.dataType.equals(DataType.SelfDef))
			result.Value = ((ObjDef)this.Value).copy();
		else if(null != this.Value)
			result.Value = this.Value;
		result.bArray = this.bArray;
		result.iCounts = this.iCounts;
		result.dataType = this.dataType;
		result.bPartofSelf = this.bPartofSelf;
		if(this.Values != null)
		{
			if(this.dataType.equals(DataType.SelfDef))
			{
				ObjDef objtmp = null;
				Vector<Object> tmpResult = new Vector<Object>();
				for(Object objd : Values)
				{
					objtmp = (ObjDef)objd;
					tmpResult.add(objtmp.copy());
				}
				
				result.Values = tmpResult;
			}
			else
			{
				result.Values = HyRuleNode.copyVector(this.Values.size(),Values);
			}
		}
		
		return result;
	}
	void copy(ObjData result) throws CloneNotSupportedException 
	{
		if(this.Value != null && this.dataType.equals(DataType.SelfDef))
			result.Value = ((ObjDef)this.Value).copy();
		else if(null != this.Value)
			result.Value = this.Value;
		result.bArray = this.bArray;
		result.iCounts = this.iCounts;
		result.dataType = this.dataType;
		result.bPartofSelf = this.bPartofSelf;
		result.strType = this.strType;
		result.logShow = this.logShow;
		if(this.Values != null)
		{
			if(this.dataType.equals(DataType.SelfDef))
			{
				ObjDef objtmp = null;
				Vector<Object> tmpResult = new Vector<Object>();
				for(Object objd : Values)
				{
					objtmp = (ObjDef)objd;
					tmpResult.add(objtmp.copy());
				}
				
				result.Values = tmpResult;
			}
			else
				result.Values = HyRuleNode.copyVector(this.Values.size(),Values);
		}
	}
	//复制Value
	Object copyValue()
	{
		if(this.dataType.equals(DataType.SelfDef))
			return null;
		else
			return this.Value;
	}
	//复制Value
	ObjDef copyValue(boolean bSelfDef)
	{
		if(this.dataType.equals(DataType.SelfDef))
		{
			if(this.Value != null)
				return ((ObjDef)this.Value).copy();
			else
				return null;
		}
		else
			return null;
	}
	//复制Values
	Vector<Object> copyValues()
	{
		if(null != this.Values)
		{
			if(this.dataType.equals(DataType.SelfDef))
			{
				ObjDef objtmp = null;
				Vector<Object> tmpResult = new Vector<Object>();
				for(Object objd : Values)
				{
					objtmp = (ObjDef)objd;
					tmpResult.add(objtmp.copy());
				}
				
				return tmpResult;
			}
			else
				return HyRuleNode.copyVector(this.Values.size(),Values);
		}
		else
			return null;
	}
}

//类型定义
enum DataType 
{
    Defined, //类似Integer、Double、String、Characte
    SelfDef; //ObjDef
}
//对象基类
class Obj
{
	//类型名
	public String strType = null;
	//是否是数组，缺省不是
	public boolean bArray = false;
	//是否简单对象
	public DataType dataType = DataType.Defined;
	//数组本层元素数
	public int iCounts = 0;
	//是定义还是变量
	public boolean bDefine = false;
	protected LogOrShow logShow = null;
	
	public void Dump(String preInfo, boolean bCompile)
	{
		logShow.AppendShow(preInfo + "\tType: " + strType,InforType.Infor);
	}
}