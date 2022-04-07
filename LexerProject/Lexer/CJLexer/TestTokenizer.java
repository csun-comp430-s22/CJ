package CJLexer;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

public class TestTokenizer {
	
    public void assertTokenizes(final String input,
                                final Token[] expected) {
        final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        try {
            final List<Token> received = tokenizer.tokenize();
            assertTrue("Expected tokenization failure, got: " + received,
                       expected != null);
            assertArrayEquals(expected,
                              received.toArray(new Token[received.size()]));
        } catch (final TokenizerException e) {
            assertTrue(("Unexpected tokenization failure for \"" +
                        input + "\": " + e.getMessage()),
                       expected == null);
        }
    }

    @Test
    public void testTokenizeSingleDigitInteger() {
        assertTokenizes("0",
                        new Token[]{ new NumberToken(0) });
    }
    
    @Test
    public void testTokenizeInteger() {
        assertTokenizes("123",
                        new Token[]{ new NumberToken(123) });
    }

    @Test
    public void testTokenizeIntegerLeadingWhitespace() {
        assertTokenizes("  123",
                        new Token[]{ new NumberToken(123) });
    }

    @Test
    public void testTokenizeIntegerTrailingWhitespace() {
        assertTokenizes("123   ",
                        new Token[]{ new NumberToken(123) });
    }

    @Test
    public void testTokenizeIntegerLeadingAndTrailingWhitespace() {
        assertTokenizes("  123  ",
                        new Token[]{ new NumberToken(123) });
    }

    @Test
    public void testTokenizeVariableSingleLetter() {
        assertTokenizes("x",
                        new Token[]{ new NameToken("x") });
    }

    @Test
    public void testTokenizeVariableMultiLetter() {
        assertTokenizes("foo",
                        new Token[]{ new NameToken("foo") });
    }

    @Test
    public void testTokenizeVariableStartsWithIf() {
        assertTokenizes("ifx",
                        new Token[]{ new NameToken("ifx") });
    }

    @Test
    public void testTokenizeIf() {
        assertTokenizes("if",
                        new Token[]{ new IfToken() });
    }


    @Test
    public void testTokenizeSingleChars() {
        assertTokenizes("+-*/(){}",
                        new Token[]{ new PlusSignToken(),
                                     new MinusSignToken(),
                                     new MultSignToken(),
                                     new DivSignToken(),
                                     new LeftParenToken(),
                                     new RightParenToken(),
                                     new LeftCurlyToken(),
                                     new RightCurlyToken() });
    }

    @Test
    public void testTokenizeIntermixed() {
        assertTokenizes("*if+foo-",
                        new Token[]{ new MultSignToken(),
                                     new IfToken(),
                                     new PlusSignToken(),
                                     new NameToken("foo"),
                                     new MinusSignToken() });
    }

    @Test
    public void testTokenizeElse() {
        assertTokenizes("else",
                        new Token[]{ new ElseToken() });
    }

    @Test
    public void testTokenizeIfExpression() {
        assertTokenizes("if (1) { x } else { y }",
                        new Token[]{ new IfToken(),
                                     new LeftParenToken(),
                                     new NumberToken(1),
                                     new RightParenToken(),
                                     new LeftCurlyToken(),
                                     new NameToken("x"),
                                     new RightCurlyToken(),
                                     new ElseToken(),
                                     new LeftCurlyToken(),
                                     new NameToken("y"),
                                     new RightCurlyToken() });
    }
    
    //Types/variable declarations
    
    @Test
    public void testTokenizeIntDeclaration() {
    	assertTokenizes("int foo;",
    					new Token[] { new IntToken(),
    								  new NameToken("foo"),
    								  new SemiColonToken()});
    }
    
    @Test
    public void testTokenizeIntDeclarationWithAssignment() {
    	assertTokenizes("int foo = 1;",
    					new Token[] { new IntToken(),
    								  new NameToken("foo"),
    								  new EqualsToken(),
    								  new NumberToken(1),
    								  new SemiColonToken()});
    }
    
    @Test
    public void testTokenizeStringDeclaration() {
    	assertTokenizes("String foo;",
    					new Token[] { new StringToken(),
    								  new NameToken("foo"),
    								  new SemiColonToken()});
    }
    
    @Test
    public void testTokenizeIntAssignment() {
    	assertTokenizes("foo = 1;",
    					new Token[] { new NameToken("foo"),
    								  new EqualsToken(),
    								  new NumberToken(1),
    								  new SemiColonToken()});
    }
    
    @Test
    public void testTokenizeVariableAssignment() {
    	assertTokenizes("foo = bar;",
    					new Token[] { new NameToken("foo"),
    							      new EqualsToken(),
    							      new NameToken("bar"),
    								  new SemiColonToken()});
    }
    @Test
    public void testObjectDeclaration() {
    	assertTokenizes("Object foo;", 
    					new Token[] { new ObjectToken(),
    								  new NameToken("foo"), 
    								  new SemiColonToken()});
    }
    @Test
    public void testTokenizeNewClass() {
    	assertTokenizes("new Foo();",
    					new Token[] { new NewToken(),
    								  new NameToken("Foo"),
    								  new LeftParenToken(),
    								  new RightParenToken(),
    								  new SemiColonToken() });
    }
    
    @Test
    public void testTokenizePlus() {
    	assertTokenizes("2+2",
    					new Token[] { new NumberToken(2),
    								  new PlusSignToken(),
    								  new NumberToken(2)});
    }
    
    @Test
    public void testTokenizeMinus() {
    	assertTokenizes("2-2",
    					new Token[] { new NumberToken(2),
    								  new MinusSignToken(),
    								  new NumberToken(2)});
    }
    
    @Test
    public void testTokenizeMult() {
    	assertTokenizes("2*2",
    					new Token[] { new NumberToken(2),
    								  new MultSignToken(),
    								  new NumberToken(2)});
    }
    
    @Test
    public void testTokenizeDiv() {
    	assertTokenizes("2/2",
    					new Token[] { new NumberToken(2),
    								  new DivSignToken(),
    								  new NumberToken(2)});
    }
    
    @Test
    public void testTokenizeThisVar() {
    	assertTokenizes("this.foo",
    					new Token[] { new ThisToken(),
    								  new PeriodToken(),
    								  new NameToken("foo")});
    }
    
    @Test
    public void testTokenizeVarFunc() {
    	assertTokenizes("foo.func(x,y);",
    					new Token[] { new NameToken("foo"),
    								  new PeriodToken(),
    								  new NameToken("func"),
    								  new LeftParenToken(),
    								  new NameToken("x"),
    								  new CommaToken(),
    								  new NameToken("y"),
    								  new RightParenToken(),
    								  new SemiColonToken()});
    }
    
    @Test
    public void testTokenizeFunctionDeclaration() {
    	assertTokenizes("public int addFoo(int x) { this.x+x; return x; }",
    					new Token[] { new PublicToken(),
    								  new IntToken(),
    								  new NameToken("addFoo"),
    								  new LeftParenToken(),
    								  new IntToken(),
    								  new NameToken("x"),
    								  new RightParenToken(),
    								  new LeftCurlyToken(),
    								  new ThisToken(),
    								  new PeriodToken(),
    								  new NameToken("x"),
    								  new PlusSignToken(),
    								  new NameToken("x"),
    								  new SemiColonToken(),
    								  new ReturnToken(),
    								  new NameToken("x"),
    								  new SemiColonToken(),
    								  new RightCurlyToken()});
    }
    
    @Test
    public void testTokenizeClassDeclaration() {
    	assertTokenizes("public class Student extends Person{" + 
    			"	private int id;" + 
    			"	public Student(int id){" + 
    			"		this.id=id;" + 
    			"	}" + 
    			"	public int getID(){" + 
    			"		return this.id;" + 
    			"}" + 
    			"}",
    					new Token[] { new PublicToken(),
    								  new ClassToken(),
    								  new NameToken("Student"),
    								  new ExtendsToken(),
    								  new NameToken("Person"),
    								  new LeftCurlyToken(),
    								  new PrivateToken(),
    								  new IntToken(),
    								  new NameToken("id"),
    								  new SemiColonToken(),
    								  new PublicToken(),
    								  new NameToken("Student"),
    								  new LeftParenToken(),
    								  new IntToken(),
    								  new NameToken("id"),
    								  new RightParenToken(),
    								  new LeftCurlyToken(),
    								  new ThisToken(),
    								  new PeriodToken(),
    								  new NameToken("id"),
    								  new EqualsToken(),
    								  new NameToken("id"),
    								  new SemiColonToken(),
    								  new RightCurlyToken(),
    								  new PublicToken(),
    								  new IntToken(),
    								  new NameToken("getID"),
    								  new LeftParenToken(),
    								  new RightParenToken(),
    								  new LeftCurlyToken(),
    								  new ReturnToken(),
    								  new ThisToken(),
    								  new PeriodToken(),
    								  new NameToken("id"),
    								  new SemiColonToken(),
    								  new RightCurlyToken(),
    								  new RightCurlyToken()});
    }
    @Test
    public void testTokenizeQuotedString() {
        assertTokenizes("\"123\"",
                        new Token[]{ new QuotedStringToken("123") });
    }
    @Test
    public void testVoidToken() {
        assertTokenizes("return void;",
                        new Token[]{ new ReturnToken(), new VoidToken(), new SemiColonToken() });
    }
    @Test
    public void testArrows() {
        assertTokenizes("<>",
                        new Token[]{ new LessThanToken(), new GreaterThanToken() });
    }
    @Test
    public void testPrint() {
        assertTokenizes("println();",
                        new Token[]{ new PrintToken(), new LeftParenToken(), new RightParenToken(), new SemiColonToken() });
    }
    
    @Test(expected = AssertionError.class)
    public void failsTokenizer1() {
        assertTokenizes("public class Student",
                        new Token[]{ new PrivateToken(), new ClassToken(), new NameToken("Student") });
    }
    
    @Test(expected = AssertionError.class)
    public void failsTokenizer2() {
        assertTokenizes("1+1",
                        new Token[]{ new NumberToken(2), new PlusSignToken(), new NumberToken(1) });
    }
    @Test(expected = AssertionError.class)
    public void failsTokenizer3() {
        assertTokenizes("int foo1",
                        new Token[]{ new IntToken(), new NameToken("foo") });
    }
    @Test(expected = AssertionError.class)
    public void failsTokenizer4() {
        assertTokenizes("[",
                        new Token[]{ new IntToken(), new NameToken("foo") });
    }
    @Test
    public void testUnclosedQuotedStringToken() {
        assertTokenizes("\"wow",
                        new Token[]{ new QuoteToken(), new NameToken("wow") });
    }

}