package CJLexer;

public class WhileToken implements Token {
    
	public int hashCode() {
		return 44;
	}
	
	public boolean equals(final Object other) {
		return other instanceof WhileToken;
	}
	
	public String toString() {
		return "while";
	}
}
