package CJLexer;

public class EqualsToToken implements Token{
    public int hashCode() {
		return 50;
	}
	
	public boolean equals(final Object other) {
		return other instanceof EqualsToToken;
	}
	
	public String toString() {
		return "==";
	}
}
