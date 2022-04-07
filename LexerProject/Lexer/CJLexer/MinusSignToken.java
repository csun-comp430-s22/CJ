package CJLexer;

public class MinusSignToken implements Token{
    public int hashCode() {
		return 61;
	}
	
	public boolean equals(final Object other) {
		return other instanceof MinusSignToken;
	}
	
	public String toString() {
		return "-";
	}
}
