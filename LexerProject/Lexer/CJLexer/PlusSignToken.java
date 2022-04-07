package CJLexer;

public class PlusSignToken implements Token {
    
	public int hashCode() {
		return 60;
	}
	
	public boolean equals(final Object other) {
		return other instanceof PlusSignToken;
	}
	
	public String toString() {
		return "+";
	}
}

