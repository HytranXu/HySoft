package HyRules;

import java.util.Vector;

public class ASTFunctionDefine extends SimpleNode 
{
	private String strReturnType;
	private FuncParameter funParam;
	private String strFunName;
	
	public void setStrReturnType(String strReturnType) 
	{
		this.strReturnType = strReturnType;
	}

	public void setFunParam(FuncParameter funParam) 
	{
		this.funParam = funParam;
	}

	public void setStrFunName(String strFunName) 
	{
		this.strFunName = strFunName;
	}

	public ASTFunctionDefine(int id) 
	{
		super(id);
	}

	public ASTFunctionDefine(HySoft p, int id) 
	{
		super(p, id);
	}
	
	//函数运行结束清楚临时变量
	private void ClearAllVariable()
	{
		//目前不需要清楚
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("FunctionDefine\n{\n");
		infor.append("\tFunctionName : ");
		infor.append(strFunName);
		infor.append("\tReturnType : ");
		infor.append(strReturnType);
		infor.append("\tParam(");
		int paracount = funParam.getCount();
		if(paracount > 0)
		{
			Object[] param = funParam.toArray();
			Param tmpParm = null;
			for(int t=0; t<paracount; t++)
			{
				tmpParm = (Param)param[t];
				if(t > 0)
					infor.append(", ");
				infor.append(tmpParm.strType);
				infor.append(" ");
				infor.append(tmpParm.strName);
			}
		}
		infor.append(")");
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	//添加参数进变量表
	void addParamToVarTable()
	{
		int paracount = funParam.getCount();
		if(paracount > 0)
		{
			Object[] param = funParam.toArray();
			Param tmpParm = null;
			for(int t=0; t<paracount; t++)
			{
				tmpParm = (Param)param[t];
				setParam(tmpParm.strName,tmpParm.strType,null);
			}
		}
	}
	public void interpret(InterpreType mod)
	{	
		if(mod.equals(InterpreType.Build) | !this.bInitialed)
		{
			logShow.AppendShow("<------FunctionBodyDefine interpret for build or initial------>",InforType.Infor);
			
			//注册函数参数和语法树
			if(null != funDefineInfor)
				AddFunInfor(strFunName,funParam,strReturnType,this);
					
			addParamToVarTable();
			
			//展示
			if(parser.getShowTab())
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.append("<------FunctionBodyDefine systab: ");
				infor.append(strFunName);
				infor.append("------>");
				logShow.AppendShow(infor.toString(),InforType.Infor);
				this.function_Systab.DumpVariables();
				
				if(null != this.funDefineInfor)
				{
					infor = new InforLogBuilder();
					infor.append("<------FunctionBodyDefine functionInfor:------>");
					logShow.AppendShow(infor.toString(),InforType.Infor);
					funDefineInfor.dump("");
				}
			}
		}
		else if(mod.equals(InterpreType.Run))
		{
			logShow.AppendShow("<------FunctionBodyDefine interpret for run------>",InforType.Infor);
						
			//数据格式定义编译中其中编译模式，同时把数据定义名称入栈以便后续使用
			this.pushStack(strFunName);
			if (children != null) 
			{
				for (int i = 0; i < children.length; ++i)
				{
					SimpleNode n = (SimpleNode)children[i];
			        if (n != null)
			        	n.interpret(mod);
			        if(isReturned())
			        	break;
				}
			}
			Object retuflag = this.popStack();
			Object re_result = this.popStack();
			this.popStack();
			this.pushStack(re_result);
			this.pushStack(retuflag);
	
			//展示
			if(parser.getShowTab())
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.append("<------FunctionBodyDefine systab: ");
				infor.append(strFunName);
				infor.append("------>");
				logShow.AppendShow(infor.toString(),InforType.Infor);
				this.function_Systab.DumpVariables();
			}
			
			ClearAllVariable();
		}
			
		bInitialed = true;		
	}

	//设置函数变量
	public void setFunParameter(Vector<Object> funparas,String strFunctionName,int iParaCount)
	{	
		//参数不能空
		if(null == funparas || null == strFunctionName || !strFunctionName.equals(strFunName))
		{
			logShow.AppendShow("Function Parameter set wrong!!",InforType.Debug);
			return;
		}
		
		logShow.AppendShow("<------FunctionBodyDefine setFunParam------>",InforType.Infor);
		//根据名字找函数信息
		if(null == funParam)
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("FunctionLibrary \n{\n");
			infor.append("\tName : ");
			infor.append(strFunName);
			infor.append("\tFunction Name : ");
			infor.append(strFunName);
			infor.append("\tdoesn't exist!!!");
			logShow.AppendShow(infor.toString(),InforType.Warn);
			return;
		}
		
		//函数参数个数需要和设置的参数个数一致
		if(funParam.getCount() != funparas.size() && iParaCount <= 0)
		{
			logShow.AppendShow("Function Parameters and Parameters's size are different!!",InforType.Debug);
			return;
		}
		
		//根据参数名进行参数设置
		for(int t=0; t<funParam.getCount(); t++)
		{
			String name = funParam.getParam(t).strName;
			FindObjMemResult result = getObjAndMem(name,"","");
			if(null != result)
				setVariableValue(result,funparas.get(t));
		}
		
		//展示
		if(parser.getShowTab())
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("<------FunctionBodyDefine systab changed: ");
			infor.append(strFunName);
			infor.append("------>");
			logShow.AppendShow(infor.toString(),InforType.Infor);
			this.function_Systab.DumpVariables();
		}
	}
}
