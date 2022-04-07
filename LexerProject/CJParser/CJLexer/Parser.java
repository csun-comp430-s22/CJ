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
	
	private class ParseAdditive extends ParseBinop {
		public ParseAdditive() {
			
			super(AddSubOPMap);
		}

		public ParseResult<Exp> parseSomething(final int startPos) throws ParserException {
			
			return parseMultiplicative(startPos);
		}
	}

	private class ParseMultiplicative extends ParseBinop {
		public ParseMultiplicative() {
			
			super(MultDivOPMap);
		}

		public ParseResult<Exp> parseSomething(final int startPos) throws ParserException {
			
			return parsePrimary(startPos);
		}
	}

	public Exp parseExp() throws ParserException {
		
		final ParseResult<Exp> result = parseExp(0);
		
		if (result.tokenPos == tokens.length) {
			return result.result;
		} 
		else {
			throw new ParserException("Extra tokens starting at " + result.tokenPos);
		}
	}

	private ParseResult<Exp> parseExp(final int startPos) throws ParserException {
		
		return parseAdditive(startPos);
	}

	private ParseResult<Exp> parseAdditive(final int startPos) throws ParserException {
		
		return new ParseAdditive().parse(startPos);
	}

	private ParseResult<Exp> parseMultiplicative(final int startPos) throws ParserException {
		
		return new ParseMultiplicative().parse(startPos);
	}

	private void assertTokenAtPos(final Token token, final int pos) throws ParserException {
		
		if (!getTokenPos(pos).equals(token)) {
			
			throw new ParserException("Expected " + token.toString() + " at pos " + pos);
		}
	}
	
	//Beginning of parsePrimary
	private ParseResult<Exp> parsePrimary(final int startPos) throws ParserException {
		
		final Token current = getTokenPos(startPos);
		
		Exp resultExp;
		int resultPos;
		
		//Check if token object is a number token 
		if (current instanceof NumberToken) {
			
			resultExp = new NumberExp(((NumberToken) current).number);
			resultPos = startPos + 1;
			
		} 
		else if (current instanceof NameToken && (tokens.length <= startPos + 1 
				|| !(getTokenPos(startPos + 1) instanceof PeriodToken))) { 
			//Else check if it's a variable by being a NameToken.................
			
			resultExp = new VariableExp(((NameToken) current).name);
			resultPos = startPos + 1;
			
		} //Token could be a string
		else if (current instanceof QuotedStringToken) {
			
			resultExp = new StringExp(((QuotedStringToken) current).string);
			resultPos = startPos + 1;
			
		} 
		else if (current instanceof ThisToken) { 
			// this.var
			assertTokenAtPos(new PeriodToken(), startPos + 1);
			
			final ParseResult<Exp> variable = parseExp(startPos + 2);
			
			resultExp = new ThisExp(variable.result);
			resultPos = startPos + 3;
			
		} 
		/*else if (current instanceof PrintToken) { 
		 * // println(var);
			assertTokenAtPos(new LeftParenToken(), startPos + 1);
			final ParseResult<Exp> expression = parseExp(startPos + 2);
			assertTokenAtPos(new RightParenToken(), startPos + 3);
			resultExp = new PrintExp(expression.result);
			resultPos = startPos + 4;
		}*/ 
		else if (current instanceof NameToken && getTokenPos(startPos + 1) 
				instanceof PeriodToken) { 
			// call method
			assertTokenAtPos(new PeriodToken(), startPos + 1);
			
			final ParseResult<Exp> methodname = parseExp(startPos + 2);
			
			assertTokenAtPos(new LeftParenToken(), startPos + 3);
			
			ArrayList<VariableExp> params = new ArrayList<VariableExp>();
			
			int i = 4;
			while(getTokenPos(startPos + i) instanceof NameToken) {
				
				params.add((VariableExp)parseExp(startPos + i).result);
				
				if(getTokenPos(startPos + i + 1) instanceof RightParenToken) {
					i+= 1;
					break;
				}
				else {
					assertTokenAtPos(new CommaToken(), startPos + i + 1);
					i += 2;
				}
			}
			
			assertTokenAtPos(new RightParenToken(), startPos + i);
			resultExp = new CallMethodExp(new VariableExp(((NameToken) current).name), 
					(VariableExp)methodname.result, params);
			resultPos = startPos + i + 1;
			
		} 
		else if (current instanceof NewToken && 
				getTokenPos(startPos+3) instanceof LeftParenToken) {    
			//NEW EXP FOR NON GENERIC CLASSES
			
			assertTokenAtPos(new PeriodToken(), startPos + 1);
			
			final ParseResult<Exp> classname = parseExp(startPos + 2);
			
			assertTokenAtPos(new LeftParenToken(), startPos + 3);
			int currentPos = startPos+4; 
			
			ArrayList<VariableExp> varList = new ArrayList<VariableExp>();
			
			while(!(getTokenPos(currentPos) instanceof RightParenToken)) {
				
				if(getTokenPos(currentPos) instanceof NameToken) {
					
					varList.add((VariableExp)(parseExp(currentPos).result));
					currentPos++; 
				} 
				else if(getTokenPos(currentPos) instanceof CommaToken && 
						getTokenPos(currentPos-1) instanceof NameToken){
					
					currentPos++; 
				} 
				else {
					
					throw new ParserException("Expected name or comma token at pos: "+ 
					currentPos); 
				}
			}
			
			assertTokenAtPos(new RightParenToken(), currentPos);
			
			resultExp = new NewExp((VariableExp)(classname.result), varList);
			resultPos = currentPos+1;
			
		} 
		else if(current instanceof NewToken && getTokenPos(startPos+3) 
				instanceof LessThanToken) {	
			// NEW EXP FOR GENERIC CLASSES
			
			assertTokenAtPos(new PeriodToken(), startPos + 1);
			
			final ParseResult<Exp> classname = parseExp(startPos+2); 
			assertTokenAtPos(new LessThanToken(), startPos+3); 
			int newPos = startPos + 4;
			
			ArrayList<Type> typeList = new ArrayList<Type>(); 
			ArrayList<VariableExp> varList = new ArrayList<VariableExp>();
			
			while(!(getTokenPos(newPos) instanceof GreaterThanToken)) {
				
				if(getTokenPos(newPos) instanceof IntToken 
						|| getTokenPos(newPos) instanceof StringToken 
						|| getTokenPos(newPos) instanceof VoidToken 
						|| getTokenPos(newPos) instanceof NameToken) {
					
					//RESUME UNCOMMENT
					//typeList.add(parseType(newPos).result);
					newPos++;
					
				} 
				else if(getTokenPos(newPos) instanceof CommaToken 
						&& (getTokenPos(newPos-1) instanceof IntToken 
						|| getTokenPos(newPos-1) instanceof StringToken 
						|| getTokenPos(newPos-1) instanceof VoidToken 
						||getTokenPos(newPos-1) instanceof NameToken)) {
					
					newPos++; 
				} 
				else {
					
					throw new ParserException("Expected CommaToken or any TypeToken at pos: "+ newPos); 
				}
			}
			
			assertTokenAtPos(new GreaterThanToken(), newPos); 
			assertTokenAtPos(new LeftParenToken(), newPos+1); 
			
			int newPos2 = newPos + 2;
			
			while(!(getTokenPos(newPos2) instanceof RightParenToken)) {
				
				if(getTokenPos(newPos2) instanceof NameToken) {
					
					varList.add((VariableExp)parseExp(newPos2).result);
					newPos2++; 
				} 
				else if(getTokenPos(newPos2) instanceof CommaToken 
						&& getTokenPos(newPos2-1) instanceof NameToken) {
					
					newPos2++; 
				} 
				else {
					
					throw new ParserException("Expected CommaToken or any NameToken at pos:"+ newPos2); 
				}
			}
			resultExp = new GenericNewExp(((VariableExp)classname.result), typeList, varList);
			resultPos = newPos2  +1; 
			
		} 
		else if (current instanceof LeftParenToken) { 
			// (EXP)
			
			final ParseResult<Exp> nested = parseExp(startPos + 1);
			assertTokenAtPos(new RightParenToken(), nested.tokenPos);
			
			resultExp = nested.result;
			resultPos = nested.tokenPos + 1;
			
		} 
		else {
			throw new ParserException("Expected primary at " + startPos);
		}

		return new ParseResult<Exp>(resultExp, resultPos);
	}
	//End of parsePrimary
	
	/*
	public Statement parseSingleStatement() throws ParserException {
		
		return parseSingleStatement(0).result;
	}
	*/
	
	/*
	public ConstructorDef parseConstructorDef(String classname) throws ParserException {
		return parseConstructorDef(0, classname).result;
	}
	*/
	
	/*
	public ClassDefExp parseClassDef() throws ParserException {
		return parseClassDef(0).result;
	}
	*/
	
	/*
	public ClassDefExp parseGenericClassDef() throws ParserException {
		return parseGenericClassDef(0).result;
	}
	*/

	/*
	public Program parseProgram() throws ParserException {
		return parseProgram(0).result;
	}
	*/
	
	//Start of parseClassDef
	/*
	private ParseResult<ClassDefExp> parseClassDef(final int startPos) throws ParserException {
		
		final Token current = getTokenPos(startPos);
		
		ArrayList<ConstructorDef> constructorlist = new ArrayList<ConstructorDef>();
		ArrayList<MethodDefExp> methodlist = new ArrayList<MethodDefExp>();
		ArrayList<InstanceDecExp> memberlist = new ArrayList<InstanceDecExp>();
		
		boolean extending = false;
		String extendingName = "";

		if ((current instanceof PublicToken) || (current instanceof PrivateToken)) {
			
			ParseResult<Modifier> modifierResult = parseModifier(startPos);
			Modifier modifier = modifierResult.result;
			assertTokenAtPos(new ClassToken(), startPos + 1);
			
			if (!(getTokenPos(startPos + 2) instanceof NameToken)) {
				throw new ParserException("Expected Valid Name of Class at: " + startPos + 2);
			}
			
			String classname = ((NameToken) getTokenPos(startPos + 2)).name;
			
			Token currentToken;
			int currentPos;
			
			if(getTokenPos(startPos + 3) instanceof ExtendsToken) {
				
				extending = true;
				
				if (!(getTokenPos(startPos + 4) instanceof NameToken)) {
					throw new ParserException("Expected Valid Name of Class at: " + 
					startPos + 4);
				}
				
				extendingName = ((NameToken) getTokenPos(startPos + 4)).name;
				assertTokenAtPos(new LeftCurlyToken(), startPos + 5);
				
				currentToken = getTokenPos(startPos + 6);
				currentPos = startPos + 6;
				
			}
			else {
				
				assertTokenAtPos(new LeftCurlyToken(), startPos + 3);
				
				currentToken = getTokenPos(startPos + 4);
				currentPos = startPos + 4;
			}
			
			while (!(currentToken instanceof RightCurlyToken)) {
				
				ParseResult<Modifier> mod = parseModifier(currentPos);
				
				if (getTokenPos(currentPos + 1) instanceof NameToken && 
						getTokenPos(currentPos + 2) instanceof LeftParenToken) {
					
					// constructor
					
					ParseResult<ConstructorDef> constructorDef = parseConstructorDef(currentPos, classname);
					
					currentPos = constructorDef.tokenPos;
					constructorlist.add(constructorDef.result);
					
				}
				else if (getTokenPos(currentPos+2) instanceof NameToken) {
					
					ParseResult<Type> type = parseType(currentPos + 1);
					
					if (!(getTokenPos(currentPos + 2) instanceof NameToken)) {
						
						throw new ParserException( "Expected Valid Name of Member variable "
								+ "or method at: " + currentPos + 2);
					}
					
					String name = getTokenPos(currentPos + 2).toString();
					
					if (getTokenPos(currentPos + 3) instanceof LeftParenToken) {
						
						// method dec
						ParseResult<MethodDefExp> methodDefExp = parseMethodDefExp(currentPos);
						
						currentPos = methodDefExp.tokenPos;
						methodlist.add(methodDefExp.result);
					} 
					else {
						// member var dec
						
						assertTokenAtPos(new SemiColonToken(), currentPos + 3);
						
						memberlist.add( new InstanceDecExp(mod.result, 
								new VariableDecExp(type.result, new VariableExp(name))));
						
						currentPos += 4;
					}
				} 
				else {
					throw new ParserException("Unexpected " + getTokenPos(currentPos + 1).toString() + " at: " + currentPos+1);
				}
				
				currentToken = getTokenPos(currentPos);
			}

			return new ParseResult<ClassDefExp>(new ClassDefExp(modifier, classname, 
					constructorlist, memberlist, methodlist, extending, extendingName), 
					currentPos + 1);

		} 
		else {
			throw new ParserException("Expected Modifier for Class Declaration at: " 
					+ startPos);
		}
	}
	*/
	
	//End of parseClassDef
}
