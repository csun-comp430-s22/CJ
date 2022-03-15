package CJLexer;

public class QuoteToken implements Token {
	public int hashCode() {
		return 120;
	}
	
	public boolean equals(final Object other) {
		return other instanceof QuoteToken;
	}
	
	public String toString() {
		return "\"";
	}
}
