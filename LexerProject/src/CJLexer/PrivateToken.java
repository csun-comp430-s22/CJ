package CJLexer;
public class PrivateToken implements Token {
	public int hashCode() {
		return 92;
	}
	
	public boolean equals(final Object other) {
		return other instanceof PrivateToken;
	}
	
	public String toString() {
		return "private";
	}
}
