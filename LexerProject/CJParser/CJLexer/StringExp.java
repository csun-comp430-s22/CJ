package CJLexer;

public class StringExp implements Exp {
	
	public final String string; 
	
	public StringExp(final String string) {
		this.string = string; 
	}
	
	public int hashCode() { 
		return string.hashCode(); 
	}
	
	public boolean equals(final Object other) {
		return (other instanceof StringExp && ((StringExp)other).string.equals(string)); 
	}
	
	 public String toString() {
	        return string;
	}
}
