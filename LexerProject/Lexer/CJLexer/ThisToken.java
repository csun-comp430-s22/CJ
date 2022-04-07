package CJLexer;
public class ThisToken implements Token {
	public int hashCode() {
		return 97;
	}
	
	public boolean equals(final Object other) {
		return other instanceof ThisToken;
	}
	
	public String toString() {
		return "this";
	}
}
