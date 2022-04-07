package CJLexer;

public class CommaToken implements Token {
	public int hashCode() {
		return 112;
	}
	
	public boolean equals(final Object other) {
		return other instanceof CommaToken;
	}
	
	public String toString() {
		return ",";
	}
}
