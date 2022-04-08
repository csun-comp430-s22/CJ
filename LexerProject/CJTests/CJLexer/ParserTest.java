package CJLexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ParserTest {
    // specify null for expected if it's not supposed to parse
    public void assertParses(final Token[] tokens, final Exp expected) {
    	
        final Parser parser = new Parser(tokens);
        
        try {
            final Exp received = parser.parseExp();
            assertTrue("Expected parse failure; got: " + received, expected != null);
            assertEquals(expected, received);
        } 
        catch (final ParserException e) {
            assertTrue(("Unexpected parse failure for " + Arrays.toString(tokens) + ": " 
            		+ e.getMessage()), expected == null);
        }
    }
    
    public void assertParsesSingleStatement(final Token[] tokens, final Statement expected) {
    	
    	final Parser parser = new Parser(tokens);
    	
    	try {
    		final Statement received = parser.parseSingleStatement();
    		
    		assertTrue("Expected parse failure; got: " + received, expected != null);
    		assertEquals(expected, received);
    	} 
    	catch (final ParserException e) {
    		assertTrue(("Unexpected parse failure for " + Arrays.toString(tokens) + ": " + 
    				e.getMessage()), expected == null);
    	}
    }
    
    public void assertParsesConstructorDef(final Token[] tokens, final ConstructorDef expected) {
    	
    	final Parser parser = new Parser(tokens);
    	
    	try {
    		
    		final ConstructorDef received = parser.parseConstructorDef(expected.name);
    		
    		assertTrue("Expected parse failure; got: " + received, expected != null);
    		assertEquals(expected, received);
    	} 
    	catch (final ParserException e) {
    		assertTrue(("Unexpected parse failure for " + Arrays.toString(tokens) + ": " + e.getMessage()), expected == null);
    	}
    }
    
    public void assertParsesClassDef(final Token[] tokens, final ClassDefExp expected) {
    	
    	final Parser parser = new Parser(tokens);
    	try {
    		
    		final ClassDefExp received = parser.parseClassDef();
    		
    		assertTrue("Expected parse failure; got: " + received, expected != null);
    		assertEquals(expected, received);
    	} 
    	catch (final ParserException e) {
    		assertTrue(("Unexpected parse failure for " + Arrays.toString(tokens) + ": " 
    				+ e.getMessage()), expected == null);
    	}
    }
    
    public void assertParsesGenericClassDef(final Token[] tokens, final ClassDefExp expected) {
    	
    	final Parser parser = new Parser(tokens);
    	
    	try {
    		
    		final GenericClassDefinition received = (GenericClassDefinition) parser.parseGenericClassDef();
    		
    		assertTrue("Expected parse failure; got: " + received, expected != null);
    		assertEquals(expected, received);
    	} 
    	catch (final ParserException e) {
    		assertTrue(("Unexpected parse failure for " + Arrays.toString(tokens) + ": " + e.getMessage()), expected == null);
    	}
    }
    
    public void assertParsesProgram(final Token[] tokens, final Program expected) {
    	
    	final Parser parser = new Parser(tokens);
    	
    	try {
    		
    		final Program received = parser.parseProgram();
    		
    		assertTrue("Expected parse failure; got: " + received, expected != null);
    		assertEquals(expected, received);
    		
    	} 
    	catch (final ParserException e) {
    		assertTrue(("Unexpected parse failure for " + Arrays.toString(tokens) + ": " + e.getMessage()), expected == null);
    	}
    }
    
    @Test
    public void testParsesInteger() {
    	
        assertParses(new Token[]{new NumberToken(7)}, new NumberExp(7));
    }
    
    @Test
    public void testParsesQuoted() {
    	assertParses(new Token[] {new QuotedStringToken("sup")}, new StringExp("sup"));
    }
    
    @Test
    public void testParsesVariable() {
    	
        assertParses(new Token[]{ new NameToken("yafoo") }, new VariableExp("yafoo"));
    }
    
    @Test
    public void testParsesParens() {
    	
        final Token[] tokens = {new LeftParenToken(), new NumberToken(1), new RightParenToken()};
        final Exp expected = new NumberExp(1);
        
        assertParses(tokens, expected);
    }
    
    @Test
    public void testParsesPlus() {
        final Token[] tokens = {new NumberToken(1), new PlusSignToken(), new NumberToken(2)};
        final Exp expected = new OpExp(new NumberExp(1), new PlusSignOp(), new NumberExp(2));
        
        assertParses(tokens, expected);
    }
    
    @Test
    public void testParsesMinus() {
        final Token[] tokens = {new NumberToken(1), new MinusSignToken(), new NumberToken(2)};
        final Exp expected = new OpExp(new NumberExp(1), new MinusSignOp(), new NumberExp(2));
        
        assertParses(tokens, expected);
    }
    
    @Test
    public void testParsesMult() {
        final Token[] tokens = {new NumberToken(1), new MultSignToken(), new NumberToken(2)};
        final Exp expected = new OpExp(new NumberExp(1), new MultSignOp(), new NumberExp(2));
        
        assertParses(tokens, expected);
    }
    
    @Test
    public void testParsesDiv() {
        final Token[] tokens = {new NumberToken(1), new DivSignToken(), new NumberToken(2)};
        final Exp expected = new OpExp(new NumberExp(1), new DivSignOp(), new NumberExp(2));
        
        assertParses(tokens, expected);
    }
    
    @Test
    public void testArithmeticLeftAssociative() {
        final Token[] tokens = { new NumberToken(1),
                                 new PlusSignToken(),
                                 new NumberToken(2),
                                 new MinusSignToken(),
                                 new NumberToken(3) };
        final Exp expected = new OpExp(new OpExp(new NumberExp(1),
                                                       new PlusSignOp(),
                                                       new NumberExp(2)),
                                          new MinusSignOp(),
                                          new NumberExp(3));
        assertParses(tokens, expected);
    }
    
    @Test
    public void testArithmeticPrecedence() {
        final Token[] tokens = { new NumberToken(1),
                                 new MinusSignToken(),
                                 new NumberToken(2),
                                 new DivSignToken(),
                                 new NumberToken(3) };
        final Exp expected = new OpExp(new NumberExp(1),
                                          new MinusSignOp(),
                                          new OpExp(new NumberExp(2),
                                                       new DivSignOp(),
                                                       new NumberExp(3)));
        assertParses(tokens, expected);
    }
    
    @Test
    public void testArithmeticPrecedenceWithParens() {
        final Token[] tokens = { new LeftParenToken(),
                                 new NumberToken(1),
                                 new MinusSignToken(),
                                 new NumberToken(2),
                                 new RightParenToken(),
                                 new DivSignToken(),
                                 new NumberToken(3) };
        final Exp expected = new OpExp(new OpExp(new NumberExp(1),
                                                       new MinusSignOp(),
                                                       new NumberExp(2)),
                                          new DivSignOp(),
                                          new NumberExp(3));
        assertParses(tokens, expected);
    }
    
    @Test
    public void testString() {
    	final Token[] tokens = { new QuotedStringToken("ugh")}; 
    	final Exp expected = new StringExp("ugh");
    	
    	assertParses(tokens, expected); 
    }
    
    @Test
    public void testThis() {
    	final Token[] tokens = { new ThisToken(), 
    							 new PeriodToken(), 
    							 new NameToken("bleh") }; 
    	final Exp expected = new ThisExp(new VariableExp("bleh")); 
    	assertParses(tokens, expected); 
    }
    
    @Test
    public void testPrint() {
    	final Token [] tokens = { new PrintToken(), 
    							  new LeftParenToken(), 
    							  new NameToken("Goodbye World"), 
    							  new RightParenToken(),
    							  new SemiColonToken()  }; 
    	final Statement expected = new PrintExp(new VariableExp("Goodbye World"));
    	assertParsesSingleStatement(tokens, expected); 
    }
    
    @Test 
    public void testCallMethod() {
    	final Token[] tokens = { new NameToken("dood"),
    							 new PeriodToken(), 
    							 new NameToken("add"),
    							 new LeftParenToken(), 
    							 new NameToken("dood2"),
    							 new RightParenToken() }; 
    	ArrayList<VariableExp> params = new ArrayList<VariableExp>();
    	params.add(new VariableExp("dood2"));
    	final Exp expected = new CallMethodExp(new VariableExp("dood"),
    										   new VariableExp("add"), params);
    	assertParses(tokens,expected); 
    }
    
    @Test
    public void testNewClassExp() {
    	final Token[] tokens = { new NewToken(), 
    							 new PeriodToken(), 
    							 new NameToken("Student"),
    							 new LeftParenToken(),
    							 new NameToken("grade"),
    							 new RightParenToken() };
    	ArrayList<VariableExp> varList = new ArrayList<VariableExp>(); 
    	varList.add(new VariableExp("grade"));
    	final Exp expected = new NewExp(new VariableExp("Student"),
    									varList);
    	assertParses(tokens, expected); 
    }
    
    @Test
    public void testNewClassExpWithMultipleParameters() {
    	final Token[] tokens = { new NewToken(), 
    							 new PeriodToken(), 
    							 new NameToken("Student"),
    							 new LeftParenToken(),
    							 new NameToken("grade"),
    							 new CommaToken(),
    							 new NameToken("age"),
    							 new RightParenToken() };
    	ArrayList<VariableExp> varList = new ArrayList<VariableExp>(); 
    	varList.add(new VariableExp("grade"));
    	varList.add(new VariableExp("age"));
    	final Exp expected = new NewExp(new VariableExp("Student"),
    									varList);
    	assertParses(tokens, expected); 
    }
    
    @Test
    public void testConstructor() {
    	final Token[] tokens = { new PublicToken(),
    							 new NameToken("Student"),
    							 new LeftParenToken(),
    							 new IntToken(),
    							 new NameToken("dood"),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new RightCurlyToken()};
    	ArrayList<VariableDecExp> param = new ArrayList<VariableDecExp>();
    	param.add(new VariableDecExp(new IntType(), new VariableExp("dood")));
    	final ConstructorDef expected = new ConstructorDef(new PublicModifier(), "Student",param , new ArrayList());
    	
    	assertParsesConstructorDef(tokens, expected); 
    }
    
    @Test
    public void testIndependentMethodCall() {
    	final Token[] tokens = { new NameToken("food"),
    							  new PeriodToken(),
    							  new NameToken("fuFunc"),
    							  new LeftParenToken(),
    							  new NameToken("fubar"),
    							  new RightParenToken(),
    							  new SemiColonToken()};
    	ArrayList<VariableExp> params = new ArrayList<VariableExp>();
    	params.add(new VariableExp("fubar"));
    	VariableExp input = new VariableExp("food");
    	VariableExp methodname = new VariableExp("fuFunc");
    	final IndependentMethodCallStmt expected = new IndependentMethodCallStmt(new CallMethodExp(input, methodname, params));
    	
    	assertParsesSingleStatement(tokens, expected);
    }
    
    @Test
    public void testClassDec() {
    	final Token[] tokens = { new PublicToken(), 
    							 new ClassToken(), 
    							 new NameToken("Student"),
    							 new LeftCurlyToken(),
    							 new PrivateToken(),
    							 new IntToken(),
    							 new NameToken("age"),
    							 new SemiColonToken(),
    							 new PublicToken(),
    							 new IntToken(),
    							 new NameToken("getAge"),
    							 new LeftParenToken(),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new ReturnToken(),
    							 new NameToken("age"),
    							 new SemiColonToken(),
    							 new RightCurlyToken(),
    							 new PublicToken(),
    							 new VoidToken(),
    							 new NameToken("setAge"),
    							 new LeftParenToken(),
    							 new IntToken(),
    							 new NameToken("n"),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new NameToken("age"),
    							 new EqualsToken(),
    							 new NameToken("n"),
    							 new SemiColonToken(),
    							 new RightCurlyToken(),
    							 new RightCurlyToken() };
    	
    	ArrayList<InstanceDecExp> memberVarList = new ArrayList<InstanceDecExp>();
    	memberVarList.add(new InstanceDecExp(new PrivateModifier(), new VariableDecExp(new IntType(), 
    			new VariableExp("age"))));
    	
    	ArrayList<MethodDefExp> methodList = new ArrayList<MethodDefExp>();
    	ArrayList<Statement> block = new ArrayList<Statement>();
    	ArrayList<Statement> setblock = new ArrayList<Statement>();
    	ArrayList<VariableDecExp> setparam = new ArrayList<VariableDecExp>();
    	
    	setparam.add(new VariableDecExp(new IntType(), new VariableExp("n")));
    	block.add(new ReturnStmt(new VariableExp("age")));
    	setblock.add(new EqualsStmt(new VariableExp("age"), new VariableExp("n"),false));
    	
    	methodList.add(new MethodDefExp(new PublicModifier(), new IntType(), "getAge", 
    			new ArrayList<VariableDecExp>(), block));
    	
    	methodList.add(new MethodDefExp(new PublicModifier(), new VoidType(), "setAge", setparam, 
    			setblock));
    	
    	final ClassDefExp expected = new ClassDefExp(new PublicModifier(), "Student", new ArrayList<ConstructorDef>(), memberVarList, methodList, false, "");
    	assertParsesClassDef(tokens, expected); 
    }
    
    @Test
    public void testProgram() {
    	final Token[] tokens = { new PublicToken(), 
    							 new ClassToken(), 
    							 new NameToken("Student"),
    							 new ExtendsToken(),
    							 new NameToken("Person"),
    							 new LeftCurlyToken(),
    							 new PrivateToken(),
    							 new IntToken(),
    							 new NameToken("age"),
    							 new SemiColonToken(),
    							 new PublicToken(),
    							 new NameToken("Student"),
    							 new LeftParenToken(),
    							 new IntToken(),
    							 new NameToken("a"),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new NameToken("age"),
    							 new EqualsToken(),
    							 new NameToken("a"),
    							 new SemiColonToken(),
    							 new RightCurlyToken(),
    							 new PublicToken(),
    							 new IntToken(),
    							 new NameToken("getAge"),
    							 new LeftParenToken(),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new ReturnToken(),
    							 new NameToken("age"),
    							 new SemiColonToken(),
    							 new RightCurlyToken(),
    							 new PublicToken(),
    							 new VoidToken(),
    							 new NameToken("setAge"),
    							 new LeftParenToken(),
    							 new IntToken(),
    							 new NameToken("n"),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new NameToken("age"),
    							 new EqualsToken(),
    							 new NameToken("n"),
    							 new SemiColonToken(),
    							 new RightCurlyToken(),
    							 new RightCurlyToken(),
    							 new IntToken(),
    							 new NameToken("age"),
    							 new SemiColonToken(),
    							 new NameToken("age"),
    							 new EqualsToken(),
    							 new NumberToken(21),
    							 new SemiColonToken(),
    							 new NameToken("Student"),
    							 new NameToken("student"),
    							 new SemiColonToken(),
    							 new NameToken("student"),
    							 new EqualsToken(),
    							 new NewToken(),
    							 new PeriodToken(),
    							 new NameToken("Student"),
    							 new LeftParenToken(),
    							 new NameToken("age"),
       							 new RightParenToken(),
    							 new SemiColonToken()};
    	ArrayList<InstanceDecExp> memberVarList = new ArrayList<InstanceDecExp>();
    	memberVarList.add(new InstanceDecExp(new PrivateModifier(), new VariableDecExp(new IntType(), 
    			new VariableExp("age"))));
    	
    	ArrayList<MethodDefExp> methodList = new ArrayList<MethodDefExp>();
    	ArrayList<ConstructorDef> constructorList = new ArrayList<ConstructorDef>();
    	ArrayList<Statement> block = new ArrayList<Statement>();
    	ArrayList<Statement> setblock = new ArrayList<Statement>();
    	ArrayList<VariableDecExp> setparam = new ArrayList<VariableDecExp>();
    	ArrayList<VariableDecExp> constructorParam = new ArrayList<VariableDecExp>();
    	ArrayList<Statement> constructorblock = new ArrayList<Statement>();
    	
    	constructorblock.add(new EqualsStmt(new VariableExp("age"), new VariableExp("a"),false));
    	constructorParam.add(new VariableDecExp(new IntType(), new VariableExp("a")));
    	
    	setparam.add(new VariableDecExp(new IntType(), new VariableExp("n")));
    	block.add(new ReturnStmt(new VariableExp("age")));
    	setblock.add(new EqualsStmt(new VariableExp("age"), new VariableExp("n"),false));
    	
    	methodList.add(new MethodDefExp(new PublicModifier(), new IntType(), "getAge", 
    			new ArrayList<VariableDecExp>(), block));
    	
    	methodList.add(new MethodDefExp(new PublicModifier(), new VoidType(), "setAge", setparam, 
    			setblock));
    	
    	constructorList.add(new ConstructorDef(new PublicModifier(), "Student", constructorParam, 
    			constructorblock));
    	
    	ClassDefExp classStudent = new ClassDefExp(new PublicModifier(), "Student", constructorList, 
    			memberVarList, methodList, true, "Person");
    	
    	ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
    	ArrayList<Statement> statementList = new ArrayList<Statement>();
    	
    	classDefList.add(classStudent);
    	
    	statementList.add(new VariableDecExp(new IntType(), new VariableExp("age")));
    	statementList.add(new EqualsStmt(new VariableExp("age"), new NumberExp(21),false));
    	statementList.add(new VariableDecExp(new ObjectType("Student"), new VariableExp("student")));
    	
    	ArrayList<VariableExp> varList = new ArrayList<VariableExp>(); 
    	varList.add(new VariableExp("age"));
    	
    	statementList.add(new EqualsStmt(new VariableExp("student"), 
    			new NewExp(new VariableExp("Student"),varList ),false));
    	
    	Program expected = new Program(statementList, classDefList);
    	assertParsesProgram(tokens, expected); 
    }
    
    @Test
    public void testThisExpAssignment() throws TypeErrorException, TokenizerException, ParserException{
    	
    	final String input = "this.age = 25;";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	assertParsesSingleStatement(tokenArray, new EqualsStmt(new VariableExp("age"), new NumberExp(25),true));
    }
    
}
