package CJLexer;
//Use for class names, method names, and maybe variable names?
//Might make VariableToken unnecessary
public class NameToken implements Token{
	public final String name;
	
	public NameToken(String name) {
		this.name = name;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(final Object other) {
		return (other instanceof NameToken && 
				((NameToken)other).name.equals(name));
	}
	
	public String toString() {
		return name;
	}
}