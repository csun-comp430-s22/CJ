package CJLexer;

//Might not use this access modifier
public class ProtectedModifier implements Modifier {
	
	public int hashCode() { 
		return 2; 
	}
	
	public boolean equals(final Object other) {
		return other instanceof ProtectedModifier; 
	}
	
	public String toString() {
		return "protected";
	}
}
