package CJLexer;
public class FalseToken implements Token {
    public boolean equals(final Object other) {
        return other instanceof FalseToken;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "false";
    }
}
