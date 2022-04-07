package CJLexer;

//Might be useless b/c of NameToken.java lol

public class VariableToken implements Token {
    public final String name;

    public VariableToken(final String name) {
        this.name = name;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return "Variable(" + name + ")";
    }

    public boolean equals(final Object other) {
        if (other instanceof VariableToken) {
            final VariableToken vardec = (VariableToken)other;
            return name.equals(vardec.name);
        } else {
            return false;
        }
    }
}