package CJLexer;

public class DivSignToken  implements Token{
    public int hashCode() {
		return 63;
	}
	
	public boolean equals(final Object other) {
		return other instanceof DivSignToken;
	}
	
	public String toString() {
		return "/";
	}
}
