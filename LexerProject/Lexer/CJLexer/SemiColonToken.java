package CJLexer;
public class SemiColonToken implements Token {
	public int hashCode() {
		return 100;
	}
	
	public boolean equals(final Object other) {
		return other instanceof SemiColonToken;
	}
	
	public String toString() {
		return ";";
	}
}
