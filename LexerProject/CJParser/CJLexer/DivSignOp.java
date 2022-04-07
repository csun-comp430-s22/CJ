package CJLexer;

public class DivSignOp implements Op {
	public int hashCode() {
		return 4;
	}
	
	public boolean equals(final Object other) {
		return other instanceof DivSignOp;
	}
	
	public String toString() {
		return "/";
	}
}
