package CJLexer;
public class LeftCurlyToken implements Token {
    public int hashCode() {
        return 5;
    }
	
    public boolean equals(final Object other) {
        return other instanceof LeftCurlyToken;
    }

    public String toString() {
        return "{";
    }
}