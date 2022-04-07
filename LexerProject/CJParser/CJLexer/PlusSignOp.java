package CJLexer;

public class PlusSignOp implements Op {
	public int hashCode() {
		return 1;
	}
	
	public boolean equals(final Object other) {
		return other instanceof PlusSignOp;
	}
	
	public String toString() {
		return "+";
	}
}
