package CJLexer;
//Public interface have abstract methods by default needed for
//other token classes to implement

public interface Token {
	public int hashCode(); //Used to get an integer that is the number for the 
						//token for access in the hashmap (aka returns the hash code)
	
	public boolean equals(Object other); //Returns object type of the 
											//particular token
	
	public String toString(); //Returns the string name of a token
	
}