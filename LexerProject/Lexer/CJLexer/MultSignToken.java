package CJLexer;

public class MultSignToken implements Token{
    public int hashCode() {
		return 62;
	}
	
	public boolean equals(final Object other) {
		return other instanceof MultSignToken;
	}
	
	public String toString() {
		return "*";
	}
}
