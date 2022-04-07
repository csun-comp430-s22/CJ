package CJLexer;

public class LessThanOrEqualsToken implements Token{
    public int hashCode() {
		return 52;
	}
	
	public boolean equals(final Object other) {
		return other instanceof LessThanOrEqualsToken;
	}
	
	public String toString() {
		return "<=";
	}
}
