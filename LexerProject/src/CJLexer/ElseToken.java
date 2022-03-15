package CJLexer;
public class ElseToken implements Token{
    public int hashCode() {
        return 41;
    }
	
    public boolean equals(final Object other) {
        return other instanceof ElseToken;
    }

    public String toString() {
        return "else";
    }
}
