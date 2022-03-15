package CJLexer;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Tokenizer {
	private static Map<String, Token> tokenMap = new HashMap<String, Token>() {
		{
			put("public", new PublicToken());
			put("private", new PrivateToken());
			put("protected", new ProtectedToken());
			put("class", new ClassToken());
			put("{", new LeftCurlyToken());
			put("}", new RightCurlyToken());
			put("extends", new ExtendsToken());
			put("new", new NewToken());
			put("this", new ThisToken());
			put("int", new IntToken());
			put("void", new VoidToken());
			put("boolean", new BooleanToken());
			put("return", new ReturnToken());
			put("(", new LeftParenToken());
			put(")", new RightParenToken());
			put(";", new SemiColonToken());
			put("+", new PlusSignToken());
			put("-", new MinusSignToken());
			put("*", new MultSignToken());
			put("/", new DivSignToken());
			put("^", new ExpSignToken());
			put("%", new ModSignToken());
			put("==", new EqualsToToken());
			put("=", new EqualsToken());
			put("<=", new LessThanOrEqualsToken());
			put("<", new LessThanToken());
			put(">=", new GreaterThanOrEqualsToken());
			put(">", new GreaterThanToken());
			put("println", new PrintToken());
			put("if", new IfToken());
			put("else", new ElseToken());
			put("&&", new AndToken());
			put("||", new OrToken());
			put("!", new NotToken());
			put("while", new WhileToken());
		}

	};


}
