package CJLexer;
public class PublicToken implements Token{
	public int hashCode() {
		return 91;
	}
	
	public boolean equals(final Object other) {
		return other instanceof PublicToken;
	}
	
	public String toString() {
		return "public";
	}
}
