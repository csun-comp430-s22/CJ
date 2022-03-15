package CJLexer;

public class NotToken implements Token {
    
	public int hashCode() {
		return 43;
	}
	
	public boolean equals(final Object other) {
		return other instanceof NotToken;
	}
	
	public String toString() {
		return "!";
	}
}
