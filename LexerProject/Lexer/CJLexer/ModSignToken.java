package CJLexer;

public class ModSignToken implements Token{
    public int hashCode() {
		return 65;
	}
	
	public boolean equals(final Object other) {
		return other instanceof ModSignToken;
	}
	
	public String toString() {
		return "%";
	}
}
