package CJLexer;

public class GreaterThanToken implements Token{
    public int hashCode() {
		return 55;
	}
	
	public boolean equals(final Object other) {
		return other instanceof GreaterThanToken;
	}
	
	public String toString() {
		return ">";
	}
}
