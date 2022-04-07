package CJLexer;
public class ReturnToken implements Token {
	public int hashCode() {
		return 99;
	}
	
	public boolean equals(final Object other) {
		return other instanceof ReturnToken;
	}
	
	public String toString() {
		return "return";
	}
}
