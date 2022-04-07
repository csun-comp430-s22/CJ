package CJLexer;
public class NewToken implements Token {
	public int hashCode() {
		return 98;
	}
	
	public boolean equals(final Object other) {
		return other instanceof NewToken;
	}
	
	public String toString() {
		return "new";
	}
}
