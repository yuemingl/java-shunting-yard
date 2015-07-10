package ws.raidrush.shunt;

public class RSymbol
{
  public static final short
    IS_STRING = 1,
    IS_NUMBER = 2;
  
  public String str;
  public double num;
  
  public short type;
  public boolean readonly = false, ident = false;
  
  public RSymbol() {}
  
  public RSymbol(String str)
  {
    this(str, false);
  }
  
  public RSymbol(String str, boolean ident)
  {
    this.str = str;
    this.type = IS_STRING;
    this.ident = ident;
  }
  
  public RSymbol(String str, boolean ident, boolean readonly)
  {
    this(str, ident);
    this.readonly = readonly;
  }
  
  public RSymbol(double num)
  {
    this(num, false);
  }
  
  public RSymbol(double num, boolean ident)
  {
    this.num = num;
    this.type = IS_NUMBER;
    this.ident = ident;
  }
  
  public RSymbol(double num, boolean ident, boolean readonly)
  {
    this(num, ident);
    this.readonly = readonly;
  }
  
  public String toString() {
	  if(this.type == IS_STRING) {
		  return this.str;
	  } else {
		  return String.valueOf(this.num);
	  }
  }
}
