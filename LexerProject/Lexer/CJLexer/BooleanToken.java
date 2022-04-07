package CJLexer;
//Might also be useless lol
public class BooleanToken implements Token {
	public int hashCode() {
		return 96;
	}
	
	public boolean equals(final Object other) {
		return other instanceof BooleanToken;
	}
	
	public String toString() {
		return "boolean";
	}
}
