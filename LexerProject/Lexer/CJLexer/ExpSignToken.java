package CJLexer;

public class ExpSignToken implements Token{
    public int hashCode() {
		return 64;
	}
	
	public boolean equals(final Object other) {
		return other instanceof ExpSignToken;
	}
	
	public String toString() {
		return "^";
	}
}
