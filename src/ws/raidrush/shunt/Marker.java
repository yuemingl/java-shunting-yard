package ws.raidrush.shunt;

/**
 * A marker token
 * First added to mark the beginning of a function
 *
 */
public class Marker extends Token {
	public Token ref;
	public Context ctx; //Context between this marker and this.ref

	public Marker(short type, Token ref) {
		this.type = type;
		this.ref = ref;
		ctx = new Context();
	}
	
	public String toString() {
		return "Marker(Ty="+type+", Ref="+ref.toString()+")";
	}
}
