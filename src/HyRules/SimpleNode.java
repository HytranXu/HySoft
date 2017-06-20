package HyRules;

import java.util.HashMap;
import java.util.Map;

public class SimpleNode extends HyRuleNode implements Node 
{
  protected Node parent;
  protected Node[] children;
  protected int id;
  protected Object value;
  protected HySoft parser;
  protected Token firstToken;
  protected Token lastToken;
  protected Map<String,String> Arraymap = new HashMap<String,String>();
  
  public SimpleNode(int i) 
  {
    id = i;
  }

  public SimpleNode(HySoft p, int i) 
  {
    this(i);
    parser = p;
  }

  void setParser(HySoft p)
  {
	  parser = p;
  }
  
  public void jjtOpen() 
  {
  }

  public void jjtClose()
  {
  }

  public void jjtSetParent(Node n) 
  { 
	  parent = n; 
  }
  public Node jjtGetParent() 
  { 
	  return parent; 
  }

  public void jjtAddChild(Node n, int i)
  {
    if (children == null) 
    {
      children = new Node[i + 1];
    } 
    else if (i >= children.length) 
    {
      Node c[] = new Node[i + 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = n;
  }

  public Node jjtGetChild(int i) 
  {
    return children[i];
  }

  public int jjtGetNumChildren() 
  {
    return (children == null) ? 0 : children.length;
  }

  public void jjtSetValue(Object value) 
  { 
	  this.value = value; 
  }
  public Object jjtGetValue() 
  { 
	  return value;
  }

  public Token jjtGetFirstToken() 
  { 
	  return firstToken; 
  }
  public void jjtSetFirstToken(Token token) 
  { 
	  this.firstToken = token; 
  }
  public Token jjtGetLastToken() 
  { 
	  return lastToken; 
  }
  public void jjtSetLastToken(Token token) 
  { 
	  this.lastToken = token; 
  }

  /* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

  public String toString() 
  {
    return HySoftTreeConstants.jjtNodeName[id];
  }
  public String toString(String prefix) 
  { 
	  return prefix + toString(); 
  }

  /* Override this method if you want to customize how the node dumps
     out its children. */
  public void dump(String prefix) 
  {
	logShow.AppendShow(prefix,InforType.Infor);
    if (children != null) 
    {
      for (int i = 0; i < children.length; ++i) 
      {
        SimpleNode n = (SimpleNode)children[i];
        if (n != null)
          n.dump(prefix + "  ");
      }
    }
  }
  
  public void interpret(InterpreType mode)
  {
	  if (children != null) 
	  {
	      for (int i = 0; i < children.length; ++i) 
	      {
	        SimpleNode n = (SimpleNode)children[i];
	        if (n != null)
	          n.interpret(mode);
	      }
	  }
  }
  
  private int iCountsHasbeenFinished = 0;
  public void interpretNext(InterpreType mode)
  {
	  if (children != null) 
	  {
	      for (int i = iCountsHasbeenFinished; i < children.length; ++i) 
	      {
	        SimpleNode n = (SimpleNode)children[i];
	        if (n != null)
	        {
	          n.interpret(mode);
	          iCountsHasbeenFinished++;
	          break;
	        }
	      }
	  }
  }
  
  public void skipNextNode()
  {
	  iCountsHasbeenFinished++;
  }

  public void dumpMember(String prefix) 
  {
	  if (children != null) 
	  {
	    for (int i = 0; i < children.length; ++i) 
	    {
	      SimpleNode n = (SimpleNode)children[i];
	      if (n != null) 
	        n.dumpContent(prefix);
	    }
	  }
  }
  
  public int getId() 
  {
    return id;
  }
  
  void AddIntItem(String name, String value) throws ParseException
  {
	  if(Arraymap.containsKey(name))
	  {
		  InforLogBuilder infor = new InforLogBuilder();
		  infor.setImportant(true);
		  infor.append("!!!<----Waring : Variable named ");
		  infor.append(name);
		  infor.append(" already exists please change the name---->!!!");
		  logShow.AppendShow(infor.toString(),InforType.Error);
	  }
	  else
		  Arraymap.put(name, value);
  }
  
  public void dumpContent(String prefix) 
  {
	InforLogBuilder infor = new InforLogBuilder();
	infor.append(prefix);
	infor.append("\n{");
	for(String key : Arraymap.keySet())
	{
		String value = Arraymap.get(key);
		infor.append("\tName : ");
		infor.append(key);
		infor.append("\tvalues : ");
		infor.append(value);
	}
	infor.append("\n}");
	logShow.AppendShow(infor.toString(),InforType.Debug);
  }
}
