package CJLexer;

public class PeriodToken implements Token {
	public int hashCode() {
		return 113;
	}
	
	public boolean equals(final Object other) {
		return other instanceof PeriodToken;
	}
	
	public String toString() {
		return ".";
	}
}
