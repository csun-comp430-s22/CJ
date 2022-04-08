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
					
					// If an operation exists, then must have a right expression
					final ParseResult<Exp> right = parseSomething(finalResult.tokenPos + 1);
					
					finalResult = new ParseResult<Exp>(new OpExp(finalResult.result, op, right.result),
							right.tokenPos);
				} else {
					// Else no operation, then return whatever there is
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
			
			resultExp = new VariableExp(((NameToken) current).name);
			resultPos = startPos + 1;
			
		} //Token could be a string
		else if (current instanceof QuotedStringToken) {
			
			resultExp = new StringExp(((QuotedStringToken) current).string);
			resultPos = startPos + 1;
			
		} 
		else if (current instanceof ThisToken) { 
			// Variable could be of this.variable
			assertTokenAtPos(new PeriodToken(), startPos + 1);
			
			final ParseResult<Exp> variable = parseExp(startPos + 2);
			
			resultExp = new ThisExp(variable.result);
			resultPos = startPos + 3;
			
		} 
//		else if (current instanceof PrintToken) { 
//		 // println(var);
//			assertTokenAtPos(new LeftParenToken(), startPos + 1);
//			final ParseResult<Exp> expression = parseExp(startPos + 2);
//			assertTokenAtPos(new RightParenToken(), startPos + 3);
//			resultExp = new PrintExp(expression.result);
//			resultPos = startPos + 4;
//		} 
		else if (current instanceof NameToken && getTokenPos(startPos + 1) 
				instanceof PeriodToken) { 
			
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
			// new expression non-generic classes
			
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
			// new expression for generic classes
			
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
					
					typeList.add(parseType(newPos).result);
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
	
	public Statement parseSingleStatement() throws ParserException {
		
		return parseSingleStatement(0).result;
	}
	
	
	
	public ConstructorDef parseConstructorDef(String classname) throws ParserException {
		return parseConstructorDef(0, classname).result;
	}
	
	
	
	public ClassDefExp parseClassDef() throws ParserException {
		return parseClassDef(0).result;
	}
	
	
	
	public ClassDefExp parseGenericClassDef() throws ParserException {
		return parseGenericClassDef(0).result;
	}
	

	
	public Program parseProgram() throws ParserException {
		return parseProgram(0).result;
	}
	
	
	//Start of parseClassDef
	
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
						
						ParseResult<MethodDefExp> methodDefExp = parseMethodDefExp(currentPos);
						
						currentPos = methodDefExp.tokenPos;
						methodlist.add(methodDefExp.result);
					} 
					else {
						
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
	//End of parseClassDef
	
	//Start of parseGenericClassDef
	private ParseResult<GenericClassDefinition> parseGenericClassDef(final int startPos) throws ParserException{
		
		final Token current = getTokenPos(startPos);
		
		ArrayList<ConstructorDef> constructorlist = new ArrayList<ConstructorDef>();
		ArrayList<MethodDefExp> methodlist = new ArrayList<MethodDefExp>();
		ArrayList<InstanceDecExp> memberlist = new ArrayList<InstanceDecExp>();
		ArrayList<VariableExp> genericList = new ArrayList<VariableExp>(); 
		
		boolean extending = false;
		String extendingName = "";
		
		if((current instanceof PublicToken) || (current instanceof PrivateToken)) {
			
			ParseResult<Modifier> modifierResult = parseModifier(startPos); 
			Modifier modifier = modifierResult.result; 
			assertTokenAtPos(new ClassToken(), startPos+1);
			
			if (!(getTokenPos(startPos + 2) instanceof NameToken)) {
				throw new ParserException("Expected Valid Name of Class at: " + startPos + 2);
			}
			
			String classname = ((NameToken)getTokenPos(startPos+2)).name; 
			assertTokenAtPos(new LessThanToken(), startPos+3);
			
			int tempPos = startPos+4;
			while(!(getTokenPos(tempPos) instanceof GreaterThanToken)) {
				
				if(getTokenPos(tempPos) instanceof NameToken) {
					genericList.add(((VariableExp)parseExp(tempPos).result)); 
					tempPos++; 
				} 
				else if(getTokenPos(tempPos) instanceof CommaToken && 
						getTokenPos(tempPos-1) instanceof NameToken) {
					
					tempPos++; 
				} 
				else {
					throw new ParserException("NameToken expected in generic class definition "
							+ "at pos: " +tempPos); 
				}
			}
			
			assertTokenAtPos(new GreaterThanToken(), tempPos); 
			Token currentToken; 
			int currentPos;
			
			if(getTokenPos(tempPos+1) instanceof ExtendsToken) {
				
				extending = true; 
				if(!(getTokenPos(tempPos+2) instanceof NameToken)) {
					throw new ParserException("Expected valid name of class at pos: "+  (tempPos+2)); 
				}
				
				extendingName = ((NameToken) getTokenPos(tempPos+2)).name; 
				assertTokenAtPos(new LeftCurlyToken(), tempPos+3);
				
				currentToken = getTokenPos(tempPos+4); 
				currentPos = tempPos+4; 
				
			} 
			else {
				assertTokenAtPos(new LeftCurlyToken(), tempPos+1); 
				currentToken= getTokenPos(tempPos+2); 
				currentPos = tempPos+2; 
			}
			
			while(!(currentToken instanceof RightCurlyToken)) {
				
				ParseResult<Modifier> mod = parseModifier(currentPos);
				
				if(getTokenPos(currentPos+1) instanceof NameToken && 
						getTokenPos(currentPos+2) instanceof LeftParenToken) {
					
					ParseResult<ConstructorDef> constructorDef = parseConstructorDef(currentPos, classname);
					
					currentPos = constructorDef.tokenPos; 
					constructorlist.add(constructorDef.result);
					
				} 
				else if(getTokenPos(currentPos+2) instanceof NameToken) {
					
					ParseResult<Type> type = parseType(currentPos+1);
					
					if(!(getTokenPos(currentPos+2) instanceof NameToken)) {
						throw new ParserException("Expected Valid Name of Member Variable or "
								+ "method at: "+(currentPos+2));
					}
					
					String name = getTokenPos(currentPos+2).toString();
					
					if(getTokenPos(currentPos+3) instanceof LeftParenToken) {
						
						ParseResult<MethodDefExp> methodDefExp = parseMethodDefExp(currentPos); 
						currentPos = methodDefExp.tokenPos; 
						methodlist.add(methodDefExp.result);
					} 
					else {
						
						assertTokenAtPos(new SemiColonToken(), currentPos+3); 
						memberlist.add(new InstanceDecExp(mod.result, new VariableDecExp(type.result, new VariableExp(name))));
						currentPos+=4; 
					} 
				} 
				else {
					throw new ParserException("Unexpected " + getTokenPos(currentPos+1).toString()
							+ " at: "+ (currentPos+1)); 
				}
				currentToken = getTokenPos(currentPos); 
			}
			return new ParseResult<GenericClassDefinition>(new GenericClassDefinition(modifier, 
					classname, constructorlist, memberlist, methodlist, extending, extendingName, 
					genericList), currentPos+1);
		} 
		else {
			throw new ParserException("Expected Modifier for Class Declaration at: "+ startPos); 
		}
	}
	//End of parseGenericClassDef
	
	
	private ParseResult<ConstructorDef> parseConstructorDef(int startPos, String className) throws ParserException {
		
		int pos = startPos;
		ParseResult<Modifier> mod = parseModifier(pos);
		pos = mod.tokenPos;
		
		if (!(getTokenPos(pos) instanceof NameToken)) {
			throw new ParserException("Expected Constructor Name at: " + pos);
		}
		
		String name = ((NameToken) getTokenPos(pos)).name;
		
		if (!name.equals(className)) {
			throw new ParserException("Constructor needs to match class name: " + pos);
		}
		
		pos++;
		assertTokenAtPos(new LeftParenToken(), pos);
		pos++;
		ArrayList<VariableDecExp> paramlist = new ArrayList<VariableDecExp>();
		
		while (!(getTokenPos(pos) instanceof RightParenToken)) {
			
			Token currentToken = getTokenPos(pos);
			ParseResult<Type> paramType = parseType(pos);
			pos = paramType.tokenPos;
			currentToken = getTokenPos(pos);
			
			if (!(currentToken instanceof NameToken)) {
				throw new ParserException("Expected Parameter Name at: " + pos);
			}
			
			VariableExp paramName = new VariableExp(currentToken.toString());
			pos++;
			paramlist.add(new VariableDecExp(paramType.result, paramName));
			
			if ((!(getTokenPos(pos) instanceof RightParenToken))) {
				
				if ((!(getTokenPos(pos) instanceof CommaToken))) {
					throw new ParserException("Expected comma at: " + pos);
				}
			}
			
			if (getTokenPos(pos) instanceof CommaToken) {
				pos++;
			}
		}
		
		assertTokenAtPos(new LeftCurlyToken(), pos + 1);
		pos += 2;
		ParseResult<ArrayList<Statement>> block = parseConstructorStatements(pos);
		pos = block.tokenPos;
		
		assertTokenAtPos(new RightCurlyToken(), pos);
		ConstructorDef method = new ConstructorDef(mod.result, name, paramlist, block.result);
		
		return new ParseResult<ConstructorDef>(method, pos + 1);
	}
	
	private ParseResult<MethodDefExp> parseMethodDefExp(int startPos) throws ParserException {
		
		int pos = startPos;
		
		ParseResult<Modifier> mod = parseModifier(pos);
		ParseResult<Type> type = parseType(mod.tokenPos);
		
		pos = type.tokenPos;
		
		if (!(getTokenPos(pos) instanceof NameToken)) {
			throw new ParserException("Expected Method Name at: " + pos);
		}
		
		String name = getTokenPos(type.tokenPos).toString();
		pos++;
		assertTokenAtPos(new LeftParenToken(), pos);
		pos++;
		
		ArrayList<VariableDecExp> paramlist = new ArrayList<VariableDecExp>();
		
		while (!(getTokenPos(pos) instanceof RightParenToken)) {
			
			Token currentToken = getTokenPos(pos);
			ParseResult<Type> paramType = parseType(pos);
			pos = paramType.tokenPos;
			currentToken = getTokenPos(pos);
			
			if (!(currentToken instanceof NameToken)) {
				throw new ParserException("Expected Parameter Name at: " + pos);
			}
			
			VariableExp paramName = new VariableExp(currentToken.toString());
			pos++;
			paramlist.add(new VariableDecExp(paramType.result, paramName));
			
			if ((!(getTokenPos(pos) instanceof RightParenToken))) {
				
				if ((!(getTokenPos(pos) instanceof CommaToken))) {
					throw new ParserException("Expected comma at: " + pos);
				}
			}
			
			if (getTokenPos(pos) instanceof CommaToken) {
				pos++;
			}
		}
		
		assertTokenAtPos(new LeftCurlyToken(), pos + 1);
		pos += 2;
		ParseResult<ArrayList<Statement>> block = parseStatements(pos);
		pos = block.tokenPos;
		
		assertTokenAtPos(new RightCurlyToken(), pos);
		MethodDefExp method = new MethodDefExp(mod.result, type.result, name, paramlist, block.result);
		
		return new ParseResult<MethodDefExp>(method, pos + 1);
	}
	
	private ParseResult<Program> parseProgram(final int startPos) throws ParserException {
		Token currentToken = getTokenPos(startPos);
		int pos = startPos;
		ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
		ArrayList<Statement> statementList = new ArrayList<Statement>();
		while (pos < tokens.length) {
			if ((currentToken instanceof PublicToken || currentToken instanceof PrivateToken)
					&& getTokenPos(pos + 1) instanceof ClassToken) {
				
				if(getTokenPos(pos+3) instanceof LessThanToken) {
					ParseResult<GenericClassDefinition> gClassDef = parseGenericClassDef(pos); 
					classDefList.add(gClassDef.result); 
					pos = gClassDef.tokenPos; 
				} else {
					ParseResult<ClassDefExp> classDef = parseClassDef(pos);
					classDefList.add(classDef.result);
					pos = classDef.tokenPos;
				}
			} else {
				
				ParseResult<Statement> statement = parseSingleStatement(pos);
				statementList.add(statement.result);
				pos = statement.tokenPos;
			}

		}
		return new ParseResult<Program>(new Program(statementList, classDefList), pos + 1);
	}
	
	private ParseResult<Modifier> parseModifier(int startPos) throws ParserException {
		
		Token m = getTokenPos(startPos);
		// returns null if the token is not a modifier
		if (m instanceof PublicToken) {
			return new ParseResult<Modifier>(new PublicModifier(), startPos + 1);
		} 
		else if (m instanceof PrivateToken) {
			return new ParseResult<Modifier>(new PrivateModifier(), startPos + 1);
		} 
		else {
			throw new ParserException("Expected Modifier at " + startPos);
		}
	}
	
	// returns null if the token is not a modifier
	private ParseResult<Type> parseType(int startPos) throws ParserException {
		Token m = getTokenPos(startPos);

		//Need boolean type
		if (m instanceof IntToken) {
			return new ParseResult<Type>(new IntType(), startPos + 1);
		}
		else if (m instanceof VoidToken) {
			return new ParseResult<Type>(new VoidType(), startPos + 1);
		} 
		else if (m instanceof StringToken) {
			return new ParseResult<Type>(new StringType(), startPos + 1);
		} 
		else if(m instanceof NameToken && getTokenPos(startPos+1) instanceof LessThanToken){
			
			ArrayList<Type> typeList = new ArrayList<Type>(); 
			int newPos= startPos+2; 
			
			while(!(getTokenPos(newPos) instanceof GreaterThanToken)) {
				
				if(getTokenPos(newPos) instanceof IntToken 
						|| getTokenPos(newPos) instanceof StringToken 
						||getTokenPos(newPos) instanceof VoidToken 
						|| getTokenPos(newPos) instanceof NameToken) {
					
					typeList.add(parseType(newPos).result);
					newPos++; 
					
				} 
				else if(getTokenPos(newPos) instanceof CommaToken 
						&& (getTokenPos(newPos-1) instanceof IntToken) 
						|| getTokenPos(newPos-1) instanceof StringToken 
						|| getTokenPos(newPos-1) instanceof VoidToken 
						|| getTokenPos(newPos-1) instanceof NameToken) {
					
					newPos++; 
				} 
				else {
					throw new ParserException("Invalid Token, expected TypeToken or CommaToken at pos: " + newPos);
				}
			}
			return new ParseResult<Type>(new GenericObjectType(((NameToken)m).name, typeList), newPos+1);
		} 
		else if (m instanceof NameToken) {
			return new ParseResult<Type>(new ObjectType(((NameToken) m).name), startPos + 1);
		} 
		else {
			throw new ParserException("Expected Type at " + startPos);
		}
	}
	
	private ParseResult<ArrayList<Statement>> parseStatements(int startPos) throws ParserException {
		
		ArrayList<Statement> block = new ArrayList<Statement>();
		int i = startPos;
		
		while (!(getTokenPos(i) instanceof RightCurlyToken)) {
			
			ParseResult<Statement> statementResult = parseSingleStatement(i);
			block.add(statementResult.result);
			i = statementResult.tokenPos;
		}
		return new ParseResult<ArrayList<Statement>>(block, i);
	}
	
	private ParseResult<ArrayList<Statement>> parseConstructorStatements(int startPos) throws ParserException {
		
		ArrayList<Statement> block = new ArrayList<Statement>();
		int i = startPos;
		
		while (!(getTokenPos(i) instanceof RightCurlyToken)) {
			
			ParseResult<Statement> statementResult = parseSingleConstructorStatement(i);
			block.add(statementResult.result);
			i = statementResult.tokenPos;
		}
		return new ParseResult<ArrayList<Statement>>(block, i);
	}
	
	private ParseResult<Statement> parseSingleStatement(int startPos) throws ParserException {
		
		int pos = startPos;
		ParseResult<Statement> result = null;
		
		if (!(getTokenPos(pos) instanceof SemiColonToken)) {
			
			if (getTokenPos(pos) instanceof ReturnToken) {
				
				if (getTokenPos(pos + 1) instanceof SemiColonToken) {
					
					result = new ParseResult<Statement>(new ReturnStmt(), pos + 1);
				}
				else {
					ParseResult<Exp> expResult = parseExp(pos + 1);
					result = new ParseResult<Statement>(new ReturnStmt((Exp) 
							parseExp(pos + 1).result), expResult.tokenPos);
				}
			} 
			else if ((getTokenPos(pos) instanceof NameToken) 
					&& (getTokenPos(pos + 1) instanceof EqualsToken)) {
				
				ParseResult<EqualsStmt> assignmentResult = parseAssignment(pos);
				result = new ParseResult<Statement>(assignmentResult.result, assignmentResult.tokenPos);
				
			} 
			else if((getTokenPos(pos) instanceof ThisToken)
					&& (getTokenPos(pos+1) instanceof PeriodToken) 
					&& (getTokenPos(pos+2) instanceof NameToken) 
					&& (getTokenPos(pos+3) instanceof EqualsToken)) {
				
				ParseResult<EqualsStmt> assignmentResult = parseAssignment(pos); 
				result = new ParseResult<Statement>(assignmentResult.result, assignmentResult.tokenPos);
				
			}
			else if ((getTokenPos(pos) instanceof IntToken 
					|| getTokenPos(pos) instanceof StringToken)
					&& (getTokenPos(pos + 1) instanceof NameToken)) {
				
				ParseResult<VariableDecExp> variableDecExpResult = parseVariableDecExp(pos);
				result = new ParseResult<Statement>(variableDecExpResult.result, variableDecExpResult.tokenPos);
				
			} 
			else if ((getTokenPos(pos) instanceof NameToken) 
					&& (getTokenPos(pos + 1) instanceof NameToken)
					&& (getTokenPos(pos + 2) instanceof SemiColonToken)) {
				
				ParseResult<VariableDecExp> variableDecExpResult = parseVariableDecExp(pos);
				result = new ParseResult<Statement>(variableDecExpResult.result, variableDecExpResult.tokenPos);
				
			} 
			else if((getTokenPos(pos) instanceof NameToken) 
					&& (getTokenPos(pos +1) instanceof PeriodToken) 
					&& (getTokenPos(pos+2) instanceof NameToken) 
					&& (getTokenPos(pos+3) instanceof LeftParenToken)) {
				
				ParseResult<IndependentMethodCallStmt> independentMethodCallStmtResult = parseIndependentMethodCallStmt(pos);
				result = new ParseResult<Statement>(independentMethodCallStmtResult.result, independentMethodCallStmtResult.tokenPos);
				
			} 
			else if((getTokenPos(pos) instanceof NameToken) 
					&& (getTokenPos(pos+1) instanceof LessThanToken)) {
				
				ParseResult<VariableDecExp> variableDecExpResult = parseVariableDecExp(pos);
				result = new ParseResult<Statement>(variableDecExpResult.result, variableDecExpResult.tokenPos);
			} 
			else if ((getTokenPos(pos) instanceof PrintToken)) { //For printing variables
				
				assertTokenAtPos(new LeftParenToken(), pos + 1);
				final ParseResult<Exp> expression = parseExp(pos + 2);
				assertTokenAtPos(new RightParenToken(), pos + 3);
				
				result = new ParseResult<Statement>(new PrintExp(expression.result), startPos + 4);
			}
			
			if (result == null) {
				throw new ParserException("Expected valid statement at: " + pos);
				
			} 
			else if (!(getTokenPos(result.tokenPos) instanceof SemiColonToken)) {
				throw new ParserException("Expected semicolon at: " + result.tokenPos);
			} 
			else {
				return new ParseResult<Statement>(result.result, result.tokenPos + 1);
			}
		} else {
			throw new ParserException("Empty Statement(double semicolon) at: " + pos);
		}
	}
	
	private ParseResult<Statement> parseSingleConstructorStatement(int startPos) throws ParserException {
		
		int pos = startPos;
		ParseResult<Statement> result = null;
		
		if (!(getTokenPos(pos) instanceof SemiColonToken)) {
			
			if ((getTokenPos(pos) instanceof NameToken) 
					&& (getTokenPos(pos + 1) instanceof EqualsToken)) {
				
				ParseResult<EqualsStmt> assignmentResult = parseAssignment(pos);
				result = new ParseResult<Statement>(assignmentResult.result, assignmentResult.tokenPos);
				
			} 
			else if((getTokenPos(pos) instanceof ThisToken)
					&& (getTokenPos(pos+1) instanceof PeriodToken)
					&& (getTokenPos(pos+2) instanceof NameToken)
					&&(getTokenPos(pos+3) instanceof EqualsToken)) {
				
				ParseResult<EqualsStmt> assignmentResult = parseAssignment(pos); 
				result = result = new ParseResult<Statement>(assignmentResult.result, assignmentResult.tokenPos);
				
			}
			else if ((getTokenPos(pos) instanceof IntToken 
					|| getTokenPos(pos) instanceof StringToken)
					&& (getTokenPos(pos + 1) instanceof NameToken)) {
				
				ParseResult<VariableDecExp> variableDecExpResult = parseVariableDecExp(pos);
				result = new ParseResult<Statement>(variableDecExpResult.result, variableDecExpResult.tokenPos);
				
			} 
			else if ((getTokenPos(pos) instanceof NameToken) && 
					(getTokenPos(pos + 1) instanceof NameToken)
					&& (getTokenPos(pos + 2) instanceof SemiColonToken)) {
				
				ParseResult<VariableDecExp> variableDecExpResult = parseVariableDecExp(pos);
				result = new ParseResult<Statement>(variableDecExpResult.result, variableDecExpResult.tokenPos);
				
			} 
			else if((getTokenPos(pos) instanceof NameToken) 
					&& (getTokenPos(pos +1) instanceof PeriodToken) 
					&& (getTokenPos(pos+2) instanceof NameToken) 
					&& (getTokenPos(pos+3) instanceof LeftParenToken)) {
				
			ParseResult<IndependentMethodCallStmt> independentMethodCallStmtResult = parseIndependentMethodCallStmt(pos);
			result = new ParseResult<Statement>(independentMethodCallStmtResult.result, independentMethodCallStmtResult.tokenPos);
			
			}

			if (result == null) {
				throw new ParserException("Expcted valid statement at: " + pos);
			} 
			else if (!(getTokenPos(result.tokenPos) instanceof SemiColonToken)) {
				throw new ParserException("Expected semicolon at: " + result.tokenPos);
			} 
			else {
				return new ParseResult<Statement>(result.result, result.tokenPos + 1);
			}
		} 
		else {
			throw new ParserException("Empty Statement(double semicolon) at: " + pos);
		}
	}
	
	private ParseResult<IndependentMethodCallStmt> parseIndependentMethodCallStmt(int startPos) throws ParserException {
		
		int pos = startPos;
		
		if((getTokenPos(pos) instanceof NameToken) 
				&& (getTokenPos(pos +1) instanceof PeriodToken) 
				&& (getTokenPos(pos+2) instanceof NameToken) 
				&& (getTokenPos(pos+3) instanceof LeftParenToken)) {
			
			VariableExp input = new VariableExp(((NameToken)getTokenPos(pos)).name);
			VariableExp methodname = new VariableExp(((NameToken)getTokenPos(pos+2)).name);
			
			pos = pos + 4;
			
			ArrayList<VariableExp> params = new ArrayList<VariableExp>();
			
			while(!(getTokenPos(pos) instanceof RightParenToken)){
				
				if(!(getTokenPos(pos) instanceof NameToken)){
					throw new ParserException("Expected Parameter Name at: " + pos);
				}
				
				params.add(new VariableExp(((NameToken) getTokenPos(pos)).name));
				
				if(!(getTokenPos(pos+1) instanceof RightParenToken)) {
					
					assertTokenAtPos(new CommaToken(), pos+1);
					pos += 2;
					
				}
				else {
					pos += 1;
				}
			}
			assertTokenAtPos(new RightParenToken(), pos);
			IndependentMethodCallStmt result = new IndependentMethodCallStmt(new CallMethodExp(input, methodname, params));
			return new ParseResult<IndependentMethodCallStmt>(result, pos+1);
			
		}
		else {
			throw new ParserException("Expected Method Name at: " + pos);
		}
			
	}
	
	private ParseResult<EqualsStmt> parseAssignment(int startPos) throws ParserException {
		
		int pos = startPos;
		
		if ((getTokenPos(pos) instanceof NameToken) 
				&& (getTokenPos(pos + 1) instanceof EqualsToken)) {
			
			ParseResult<Exp> expResult = parseExp(pos + 2);
			return new ParseResult<EqualsStmt>(new EqualsStmt(new VariableExp(getTokenPos(pos).toString()), 
					expResult.result, false), expResult.tokenPos);
			
		}
		else if((getTokenPos(pos) instanceof ThisToken)){
			
			ParseResult<Exp> expResult = parseExp(pos+4);
			
			return new ParseResult<EqualsStmt>(new EqualsStmt(new VariableExp(getTokenPos(pos+2).toString()), 
					expResult.result, true), expResult.tokenPos);
		}
		else {
			throw new ParserException("Expected variable and assignment at: " + pos);
		}
	}
	
	private ParseResult<VariableDecExp> parseVariableDecExp(int startPos) throws ParserException {
		
		int currentPos = startPos;
		ParseResult<Type> typeResult = parseType(currentPos);
		currentPos = typeResult.tokenPos;
		
		if (!(getTokenPos(currentPos) instanceof NameToken)) {
			throw new ParserException("Expected Variable Name at " + currentPos);
		} 
		else {
			return new ParseResult<VariableDecExp>(new VariableDecExp(typeResult.result, 
					new VariableExp(getTokenPos(currentPos).toString())), currentPos + 1);
		}
	}
	
}
