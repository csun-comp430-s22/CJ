package CJLexer;
public class IntToken implements Token{
	public int hashCode() {
		return 94;
	}
	
	public boolean equals(final Object other) {
		return other instanceof IntToken;
	}
	
	public String toString() {
		return "int";
	}
}
