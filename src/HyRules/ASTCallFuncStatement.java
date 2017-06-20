package HyRules;

import java.util.Vector;

public class ASTCallFuncStatement extends SimpleNode 
{
	//解决void和参数需要计算时参数表空的问题
	private int iParacount = 0;
	
	public void setParaCount(int iCount)
	{
		iParacount = iCount;
	}
	
	public void setStrFunName(String strFunName) 
	{
		this.strFunName = strFunName;
	}

	public void setStrParam(String strParam) 
	{
		this.strParam = strParam;
	}

	private String strFunName;
	private String strParam;
	
	public ASTCallFuncStatement(int id) 
	{
		super(id);
	}

	public ASTCallFuncStatement(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("CallFuncStatement\n{\n");
		infor.append("\tFuncName : ");
		infor.append(strFunName);
		infor.append("\tParams : ");
		infor.append(strParam);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		//输出帮助信息
		logShow.AppendShow("<------CallFuncStatement interpret------>",InforType.Infor);
		//输出调试信息
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("CallFuncStatement\n{\n");
		infor.append("\tFuncName : ");
		infor.append(strFunName);
		infor.append("\tParams : ");
		infor.append(strParam);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		
		//将参数分解成单个参数数组
		super.interpret(mod);
		Object[] paras = splitMemb(strParam,",",true);
		Vector<Object> funparas = new Vector<Object>();
		if(null != paras && paras.length > 0)
		{
			for(Object param : paras)
			{
				String paraValue = (String)param;
				if(null != paraValue && !paraValue.isEmpty())
					funparas.add(param);
				else
				{
					Object value = this.popStack();
					if(null != value)
						funparas.add(value);
				}
			}
		}
		//根据函数名和参数设置参数并运行
		setFunParamAndRun(funparas);
	}
	
	//设置函数变量并运行函数
	private void setFunParamAndRun(Vector<Object> funparas)
	{	
		//参数不能空
		if(null == funparas)
		{
			logShow.AppendShow("Function Parameter set wrong!!",InforType.Debug);
			return;
		}
		logShow.AppendShow("<------CallFuncStatement setParamAndRun------>",InforType.Infor);
		
		//查找函数信息
		FuncParameter funIn = null;
		String strFunLibName = null,sFunName = null;
		if(null == funDefineInfor)
		{
			if(null == parentFun)
				return;
			
			//根据名字找函数库,首先分解出库名和函数名
			Object[] paras = splitMemb(strFunName);
			if(null != paras && paras.length > 0)
			{
				for(Object param : paras)
				{
					String fun = (String)param;
					if(null != fun && !fun.isEmpty())
					{
						if(null == strFunLibName)
							strFunLibName = fun;
						else if(null == sFunName)
						{
							sFunName = fun;
							break;
						}
					}
				}
			}
			
			//根据名字找函数库
			funDefineInfor = parentFun.getFunLib(strFunLibName);
			if(null == funDefineInfor)
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.append("FunctionLibrary \n{\n");
				infor.append("\tName : ");
				infor.append(strFunLibName);
				infor.append("\tdoesn't exist!!!");
				logShow.AppendShow(infor.toString(),InforType.Warn);
				return;
			}
			//根据名字找函数信息
			funIn = funDefineInfor.getFunInfor(sFunName);
			if(null == funIn)
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.append("FunctionLibrary \n{\n");
				infor.append("\tName : ");
				infor.append(strFunLibName);
				infor.append("\tFunction Name : ");
				infor.append(sFunName);
				infor.append("\tdoesn't exist!!!");
				logShow.AppendShow(infor.toString(),InforType.Warn);
				return;
			}
		}
		
		//函数参数个数需要和设置的参数个数一致
		if(funIn.getCount() != funparas.size() && iParacount <= 0)
		{
			logShow.AppendShow("Function Parameters and Parameters's size are different!!",InforType.Debug);
			return;
		}
		
		//根据参数名进行参数设置
		((ASTFunctionDefine)funIn.awtFunc).setFunParameter(funparas,sFunName,iParacount);
		//执行
		((ASTFunctionDefine)funIn.awtFunc).interpret(InterpreType.Run);
	}
}
