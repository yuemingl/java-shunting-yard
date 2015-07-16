package ws.raidrush.shunt;

public class RInteger extends Token {
	public int value;
	
	public RInteger(int value, short type)
	{
		this.value = value;
		this.type  = type;
	}
	
	public String toString() {
		return String.valueOf(value);
	}
}
