package CJLexer;

public class OrToken implements Token {
    
	public int hashCode() {
		return 43;
	}
	
	public boolean equals(final Object other) {
		return other instanceof OrToken;
	}
	
	public String toString() {
		return "||";
	}
}