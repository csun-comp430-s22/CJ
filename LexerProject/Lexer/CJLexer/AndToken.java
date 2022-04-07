package CJLexer;

public class AndToken implements Token {
    
	public int hashCode() {
		return 42;
	}
	
	public boolean equals(final Object other) {
		return other instanceof AndToken;
	}
	
	public String toString() {
		return "&&";
	}
}