package CJLexer;
public class ClassToken implements Token{
	public int hashCode() {
		return 90;
	}
	
	public boolean equals(final Object other) {
		return other instanceof ClassToken;
	}
	
	public String toString() {
		return "Class";
	}
}
