package CJLexer;
public class ExtendsToken implements Token {
	public int hashCode() {
		return 101;
	}
	
	public boolean equals(final Object other) {
		return other instanceof ExtendsToken;
	}
	
	public String toString() {
		return "extends";
	}
}
