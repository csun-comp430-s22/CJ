package CJLexer;

public class QuotedStringToken implements Token {
    public final String string;

    public QuotedStringToken(String string) {
        this.string = string;
    }

    public int hashCode() { 
    	return 56; 
    }
    
    public boolean equals(final Object other) {
        return (other instanceof QuotedStringToken && 
        		((QuotedStringToken)other).string.equals(string));
    }
    
    public String toString() {
        return string;
    }

}