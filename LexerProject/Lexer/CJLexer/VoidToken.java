package CJLexer;
public class VoidToken implements Token{
	public int hashCode() {
		return 95;
	}
	
	public boolean equals(final Object other) {
		return other instanceof VoidToken;
	}
	
	public String toString() {
		return "void";
	}
}
