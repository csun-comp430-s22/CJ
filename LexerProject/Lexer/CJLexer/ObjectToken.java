package CJLexer;

public class ObjectToken implements Token {
	public int hashCode()
	{
		return 111;
	}
	
	public boolean equals(final Object other) {
		return other instanceof ObjectToken;
	}
	
	public String toString() {
		return "Object";
	}
}
