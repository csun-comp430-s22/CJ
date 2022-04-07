package CJLexer;

public class MinusSignOp implements Op {
	public int hashCode() {
		return 2;
	}
	
	public boolean equals(final Object other) {
		return other instanceof MinusSignOp;
	}
	
	public String toString() {
		return "-";
	}
}
