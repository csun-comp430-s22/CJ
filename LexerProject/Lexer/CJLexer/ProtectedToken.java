package CJLexer;
public class ProtectedToken implements Token {
	public int hashCode() {
		return 93;
	}
	
	public boolean equals(final Object other) {
		return other instanceof ProtectedToken;
	}
	
	public String toString() {
		return "protected";
	}
}
