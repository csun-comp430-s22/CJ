package CJLexer;

public class GreaterThanOrEqualsToken implements Token{
    public int hashCode() {
		return 54;
	}
	
	public boolean equals(final Object other) {
		return other instanceof GreaterThanOrEqualsToken;
	}
	
	public String toString() {
		return ">=";
	}
}
