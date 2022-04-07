package CJLexer;

public class MultSignOp implements Op {
	public int hashCode() {
		return 3;
	}
	
	public boolean equals(final Object other) {
		return other instanceof MultSignOp;
	}
	
	public String toString() {
		return "*";
	}
}
