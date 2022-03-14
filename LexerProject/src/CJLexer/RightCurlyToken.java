package CJLexer;
public class RightCurlyToken implements Token {
    public int hashCode() {
        return 6;
    }
	
    public boolean equals(final Object other) {
        return other instanceof RightCurlyToken;
    }

    public String toString() {
        return "}";
    }
}