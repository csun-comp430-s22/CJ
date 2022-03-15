
public class IfToken implements Token {
    public int hashCode() {
        return 40;
    }
	
    public boolean equals(final Object other) {
        return other instanceof IfToken;
    }

    public String toString() {
        return "if";
    }
}
