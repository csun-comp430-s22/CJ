package CJLexer;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Parser {
	
	//Static map variables
	private static final Map<Token, Op> AddSubOPMap = new HashMap<Token, Op>() {
		{
			put(new PlusSignToken(), new PlusSignOp());
			put(new MinusSignToken(), new MinusSignOp());
		}
	};
	
	private static final Map<Token, Op> MultDivOPMap = new HashMap<Token, Op>() {
		{
			put(new MultSignToken(), new MultSignOp());
			put(new DivSignToken(), new DivSignOp());
		}
	};
	
	private final Token[] tokens;
	
	//initialize array of tokens
	public Parser(final Token[] tokens) {
		this.tokens = tokens;
	}
	
	private Token getTokenPos(final int position) throws ParserException {
		assert (position >= 0);
		if (position < tokens.length) {
			return tokens[position];
		} 
		else {
			throw new ParserException("No token at position: " + position);
		}
	}
	
	public class ParseResult<A> {
		public final A result;
		public final int tokenPos;

		public ParseResult(final A result, final int tokenPos) {
			this.result = result;
			this.tokenPos = tokenPos;
		}
	}
	
	private abstract class ParseBinop {
		
		private final Map<Token, Op> opMap;
		
		//initialize the operation map (opMap)
		public ParseBinop(final Map<Token, Op> opMap) {
			this.opMap = opMap;
		}
		
		//Similar to Op and Exp; to be overrided/implemented by other methods
		public abstract ParseResult<Exp> parseSomething(final int startPos) throws ParserException;
		
		public ParseResult<Exp> parse(final int startPos) throws ParserException {
			
			int pos = startPos;
			ParseResult<Exp> finalResult = parseSomething(pos);
			
			if (finalResult == null) {
				return null;
			}

			ParseResult<Exp> currentResult = null;
			
			while (finalResult.tokenPos < tokens.length) {
				
				final Op op = opMap.get(getTokenPos(finalResult.tokenPos));
				
				if (op != null) {
					
					// op exists then need a right
					final ParseResult<Exp> right = parseSomething(finalResult.tokenPos + 1);
					
					finalResult = new ParseResult<Exp>(new OpExp(finalResult.result, op, right.result),
							right.tokenPos);
				} else {
					// we don't have an op. return whatever we have
					return finalResult;
				}
			}

			return finalResult;
		}
	}

}
