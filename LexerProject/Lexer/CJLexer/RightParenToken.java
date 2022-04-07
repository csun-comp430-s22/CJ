package CJLexer;
public class RightParenToken implements Token {
    public int hashCode() {
        return 4;
    }
	
    public boolean equals(final Object other) {
        return other instanceof RightParenToken;
    }

    public String toString() {
        return ")";
    }
}