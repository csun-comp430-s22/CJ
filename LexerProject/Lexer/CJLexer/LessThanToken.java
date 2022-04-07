package CJLexer;

public class LessThanToken implements Token{
    public int hashCode() {
		return 53;
	}
	
	public boolean equals(final Object other) {
		return other instanceof LessThanToken;
	}
	
	public String toString() {
		return "<";
	}
}
