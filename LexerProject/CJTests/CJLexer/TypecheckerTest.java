package CJLexer;

import java.util.List; 
import java.util.ArrayList; 
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TypecheckerTest {
	
	ArrayList<Statement> statements = new ArrayList<Statement>(0); 
	ArrayList<ClassDefExp> classdefs = new ArrayList<ClassDefExp>(0);
	
	//method for testing some expressions (exps)
	public void assertExpType(final Type expected, final Exp exp) {
		
		Program program = new Program(statements, classdefs);
		
		try {
			
			Typechecker typecheck = new Typechecker(program);  
			
			final Type received = typecheck.typeofExp(exp); //missing method in typechecker
			
			assertTrue("Expected type error; got: "+ received.toString(), expected!=null); 
			assertEquals(expected,received); 
			
		}
		catch(final TypeErrorException e) {
			assertTrue("Unexpected type error: "+e.getMessage(), expected == null); 
		}
	}
	
	@Test
	public void testIntExp() {
        assertExpType(new IntType(), new NumberExp(42));
	}
	
	@Test
	public void testStringExp() {
		assertExpType(new StringType(), new StringExp("hello")); 
	}
	
	@Test
	public void testBinopPlusInts() {
        assertExpType(new IntType(), new OpExp(new NumberExp(1), new PlusSignOp(), 
        new NumberExp(2)));
	}
	
	@Test 
    public void testBinopPlusNonIntOrPointer() {
        assertExpType(null, new OpExp(new StringExp("oof"), new PlusSignOp(), new NumberExp(1)));
	}
	
    @Test
    public void testMinusInts() {
        assertExpType(new IntType(), new OpExp(new NumberExp(1), new MinusSignOp(), 
        new NumberExp(2)));
    }
    
    @Test
    public void testMinusNonInts() {
        assertExpType(null, new OpExp(new NumberExp(1), new MinusSignOp(), new StringExp("oof")));
    }
    
    @Test
    public void testMultInts() {
        assertExpType(new IntType(), new OpExp(new NumberExp(1), new MultSignOp(), 
        new NumberExp(2)));
    }

    @Test
    public void testMultNonInts() {
        assertExpType(null, new OpExp(new NumberExp(1), new MultSignOp(), new StringExp("oof")));
    }
    
    @Test
    public void testDivInts() {
        assertExpType(new IntType(), new OpExp(new NumberExp(1), new DivSignOp(), 
        new NumberExp(2)));
    }

    @Test
    public void testDivNonInts() {
        assertExpType(null, new OpExp(new NumberExp(1), new DivSignOp(), new StringExp("oof")));
    }
    
    @Test 
    public void testVariableCheckWithoutClass() {
    	
    	statements.add(new VariableDecExp(new IntType(), new VariableExp("oof")));
    	assertExpType(new IntType(), new VariableExp("oof"));
    }
    
  //using java.lang.AssertionError.class since the method assertExpType throws this when 
  //finding TypeErrorException
    @Test(expected = java.lang.AssertionError.class)
    public void failsTestVariableCheckWithoutClass() throws TypeErrorException{
    	
    	statements.add(new VariableDecExp(new IntType(), new VariableExp("foo")));
    	assertExpType(new IntType(), new VariableExp("oof"));
    }
    
    //Tests for full programs now fully functional with tokenizer, parser, and typechecker 

    @Test
    public void testEntireClassWithStatementsOutside() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Standard class declaration with variety of statements outside
    	final String input = "public class Student{" 
    	+ "private int age; " 
    			
    	+ "public Student(int a) {" 
    	+ "	this.age = a; " 
    	+ "}" 
    	+ "public int getAge() {" 
    	+ "return this.age; " 
    	+ "}" 
    	+"public void setAge(int n) {" 
    	+ "this.age=n; " 
    	+ "}" 
    	+ "}" 
    	+ "int age; " 
    	+ "age = 21;" 
    	+ "Student student;" 
    	+ "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram();
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsNoInstanceTestEntireClassWithStatementsOutside() throws TypeErrorException, 
    TokenizerException, ParserException{
    	
    	//Instance variable is named notage instead but age is used throughout
    	final String input = "public class Student{"
    						+"private int notage; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsWrongMethodTypeTestEntireClassWithStatementsOutside() throws 
    TypeErrorException, TokenizerException, ParserException {
    	
    	//getAge is supposed to return a string but instead it returns an int 
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public String getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsWrongParameterEntireClassWithStatementsOutside() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Assignment in constructor uses undeclared variable bleh 
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = bleh; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsDuplicateMethodEntireClassWithStatementsOutside() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Two methods are named getAge
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void getAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsDuplicateInstanceEntireClassWithStatementsOutside() throws TypeErrorException,
    TokenizerException, ParserException {
    	
    	// Two instances of age: one string and one int
    	final String input = "public class Student{"
    						+"private int age; "
    						+"private String age;"
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram();
    	
    	Typechecker.Programcheck(program); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsDuplicateClassEntireClassWithStatementsOutside() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Duplicate classes of the same name: Student
    	//The 2nd duplicate is blank for testing
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "public class Student{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testExtends() throws TypeErrorException, TokenizerException, ParserException{
    	
    	//Testing extends keyword (inheritance)
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "public class Junior extends Student{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestExtends() throws TypeErrorException, TokenizerException, ParserException {
    	
    	// testing extends failing when class extending does not exist (Person)
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "public class Junior extends Person{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void testNewClassVariableObjectThatDoesntExist() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	// testing Person student = new Student() when the Person class does not exist
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "public class Junior extends Student{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Person student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testNewClassInheritance() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	// Testing new class object declaration with inheritance
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "public class Junior extends Student{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Junior junior;"
    				  + "junior = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestNewClassInheritance() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Testing new class inheritance failing when going backwards in inheritance
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "public class Junior extends Student{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Junior(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void testPrivateMethodAccess() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Testing fails when trying to access private methods
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"private int getAge() {"
    							+"return this.age; "
    						+"}"
    				  + "}"
    				  + "int age; "
    			  	  + "Student student;"
    				  + "age = student.getAge();";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testFunctionCallOutsideClass() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Testing function calls that are outside of the class
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    				  + "}"
    				  + "int age; "
    			  	  + "Student student;"
    				  + "age = student.getAge();";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestFunctionCallOutsideClassWrongParameters() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//testing wrong parameters on function call 
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "int n; "
    			  	  + "Student student;"
    				  + "age = student.getAge(n);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram();
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestFunctionCallOutsideClassWrongTypeParameters() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	// testing the wrong type of parameters on a function call 
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge(int n) {"
    							+"return this.age; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "String n; "
    			  	  + "Student student;"
    				  + "age = student.getAge(n);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testIndependentMethodCall() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	// Independent method call test
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student.setAge(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected=TypeErrorException.class)
    public void failsTestIndependentMethodCallPrivate() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//testing fail when calling private method with independent method call
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"private void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student.setAge(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected=TypeErrorException.class)
    public void failsTestIndependentMethodCallWrongMethodName() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//The test fails when the method name used for the method call is invalid
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student.setAges(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected=TypeErrorException.class)
    public void failsTestIndependentMethodCallWrongParameterNumber() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//The test fails when there is the wrong number of parameters in the
    	//independent method call
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student.setAge();";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected=TypeErrorException.class)
    public void failsTestIndependentMethodCallWrongParameterType() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//The test fails when the wrong type of parameter is passed in the method call
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "String age; "
    			  	  + "Student student;"
    				  + "student.setAge(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestMultipleSameParameterInConstructor() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	// More than 1 parameter with the same name in the constructor test
    	final String input = "public class Student{"
    						+"private int age;"
    						+"public Student(int a, int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestMultipleSameParameterInMethod() throws TypeErrorException, 
    TokenizerException, ParserException {
    	// More than 1 parameter with the same name in a method test
    	final String input = "public class Student{"
    						+"private int age;"
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n, int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestEntireClassWithStatementsOutsideMoreCoverage() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Test that fails due to class declaration with variety of statements outside (for more coverage) 
    	//some code is redundant/useless but is included for testing coverage only
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"int s;"
    							+"int n;"
    							+"s=2;"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testEntireClassWithStatementsOutsideMoreCoverage() throws TypeErrorException, 
    TokenizerException, ParserException {

    	//Similar to the test above, this test fails due to class declaration with various statements
    	//outside for more coverage. Some code is redundant/useless but is there for test coverage
    	
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestMultipleVariablesOutsideClass() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Test fails b/c of the multiple variables with the same name outside of the class
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize();
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram();
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestNewClassDoesntExist() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//This test fails b/c Person does not exist
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Person(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram();
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestCallMethodThatDoesntExist() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Test fails b/c add doesn't exist for ints
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    							+"n=n.add(n);"
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testCallMethodWithinClass() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	// Test for method call in a class method
    	final String input = "public class Student {" 
    	+"private int age; "
    	+"public Student(int a) {"
    	+"	this.age = a; "
    	+"}"
    	
    	+"public int getAge() {"
    		+"return this.age; "
    	+"}"
    	
    	+"public void setAge(int n) {"
    		+"this.age = n; "
    		+"Student s;"
    		+"n=s.getAge();"
    		+"}"
    	+ "}"
    	
    	+ "int age; "
    	+ "age = 21;"
    	+ "Student student;"
    	+ "student = new.Student(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestMultipleSameInstance() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Test fails b/c of replicate of an instance variable
    	final String input = "public class Student{"
    						+"private int age; "
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    						+"}"
    				  + "}";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsDeclaredAlreadyInConstructor() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Test fails b/c variable 'a' is already declared in the constructor
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"  int a; "
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    						+"}"
    				  + "}";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testMultipleConstructors() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Class declaration with multiple constructors
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public Student(int a, int b) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsCallingInstanceWithoutThis() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//fails since methods do  not call this.age for age
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public Student(int a, int b) {"
    						+"	age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"age=n; "
    						+"}"
    				  + "}";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testGenericNewExp() throws TypeErrorException, TokenizerException, ParserException{
    	
    	final String input = "public class Student<A, B>{"
    					   +	"public Student(){"
    					   + 	"}"
    					   + "}"
    					   + "Student<int, int> s;"
    					   + "s = new.Student<int, int>();";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestGenericNewExp() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Test fails since wrong number of generic parameters
    	final String input = "public class Student<A>{"
    						+	"public Student(){"
    						+ 	"}"
    					   + "}"
    					   + "Student<int, int> s;"
    					   + "s = new.Student<int, int>();";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestGenericNewExp2() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Test fails since wrong name of generic class
    	final String input = "public class Person<A,B>{"
    						+	"public Person(){"
    						+ 	"}"
    					   + "}"
    					   + "Student<int, int> s;"
    					   + "s = new.Student<int, int>();";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestGenericVarDec() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Fails since wrong name of generic class
    	final String input = "public class Person<A,B>{"
    						+	"public Person(){"
    						+ 	"}"
    					   + "}"
    					   + "Student<int,String> s;";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestGenericVarDec2() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Fails since wrong parameters in generic class in var declaration
    	final String input = "public class Student<A,B>{"
    						+	"public Student(){"
    						+ 	"}"
    					   + "}"
    					   + "Student<int> s;";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsGenericAssignment() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Fails since assignment is comparing <int,String> and <int, int> 
    	final String input = "public class Student<A,B>{"
    						+	"public Student(){"
    						+ 	"}"
    					   + "}"
    					   + "Student<int, String> s;"
    					   + "s = new.Student<int,int>();";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsGenericAssignment2() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Fails since assignment is comparing <int, int> with just <int> (different number of parameters)
    	final String input = "public class Student<A,B>{"
    						+	"public Student(){"
    						+ 	"}"
    					   + "}"
    					   + "Student<int, int> s;"
    					   + "s = new.Student<int>();";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testGenericFullClassWithMethods() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Generic class with getters and setters
    	final String input = "public class Student<A>{"
    						+	"public A variable;"
    						+	"public Student(A var){"
    						+		"this.variable = var;"
    						+ 	"}"
    						+	"public A getVariable(){"
    						+		"return this.variable;"
    						+	"}"
    						+	"public void setVariable(A var){"
    						+		"this.variable = var; "
    						+ 	"}"
    					   + "}";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected=TypeErrorException.class)
    public void failsTestGenericFullClassWithMethodsWrongReturnType() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Fails since return type is int when variable is of generic type A in getVariable
    	final String input = "public class Student<A>{"
    						+	"public A variable;"
    						+	"public Student(A var){"
    						+		"this.variable = var;"
    						+ 	"}"
    						+	"public int getVariable(){"
    						+		"return this.variable;"
    						+	"}"
    						+	"public void setVariable(A var){"
    						+		"this.variable = var; "
    						+ 	"}"
    					   + "}";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected=TypeErrorException.class)
    public void failsTestGenericFullClassWithMethodsWrongAssignmentType() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Fails since assignment is comparing int with generic type A in setVariable
    	final String input = "public class Student<A>{"
    						+	"public A variable;"
    						+	"public Student(A var){"
    						+		"this.variable = var;"
    						+ 	"}"
    						+	"public A getVariable(){"
    						+		"return this.variable;"
    						+	"}"
    						+	"public void setVariable(int var){"
    						+		"this.variable = var; "
    						+ 	"}"
    					   + "}";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected=TypeErrorException.class)
    public void failsTestGenericFullClassWrongGenericInstance() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Fails since using non declared generic in instance variable 
    	final String input = "public class Student<A>{"
    						+	"public B variable;"
    						+	"public Student(A var){"
    						+		"this.variable = var;"
    						+ 	"}"
    						+	"public A getVariable(){"
    						+		"return this.variable;"
    						+	"}"
    						+	"public void setVariable(A var){"
    						+		"this.variable = var; "
    						+ 	"}"
    					   + "}";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected=TypeErrorException.class)
    public void failsTestGenericFullClassWrongGenericMethod() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Fails since using non declared generic in instance variable 
    	final String input = "public class Student<A>{"
    						+	"public A variable;"
    						+	"public Student(A var){"
    						+		"this.variable = var;"
    						+ 	"}"
    						+	"public B getVariable(){"
    						+		"return this.variable;"
    						+	"}"
    						+	"public void setVariable(A var){"
    						+		"this.variable = var; "
    						+ 	"}"
    					   + "}";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testGenericFullClassWithMethodsAndStatements() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Generic class with getters and setters with statements outside
    	final String input = "public class Student<A>{"
    						+	"public A variable;"
    						+	"public Student(A var){"
    						+		"this.variable = var;"
    						+ 	"}"
    						+	"public A getVariable(){"
    						+		"return this.variable;"
    						+	"}"
    						+	"public void setVariable(A var){"
    						+		"this.variable = var; "
    						+ 	"}"
    					   + "}"
    					   + "int age;"
    					   + "Student<int> s;"
    					   + "s = new.Student<int>(age);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestGenericFullClassWithMethodsAndStatements() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Fails since A is set to int and name is String
    	final String input = "public class Student<A>{"
    						+	"public A variable;"
    						+	"public Student(A var){"
    						+		"this.variable = var;"
    						+ 	"}"
    						+	"public A getVariable(){"
    						+		"return this.variable;"
    						+	"}"
    						+	"public void setVariable(A var){"
    						+		"this.variable = var; "
    						+ 	"}"
    					   + "}"
    					   + "String name;"
    					   + "Student<int> s;"
    					   + "s = new.Student<int>(name);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestWrongNewExpParameterType() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Fails since name is string and constructor for student takes in int
    	final String input = "public class Student{"
    						+	"public int age;"
    						+	"public Student(int a){"
    						+		"this.age = a;"
    						+ 	"}"
    						+	"public int getVariable(){"
    						+		"return this.age;"
    						+	"}"
    						+	"public void setVariable(int a){"
    						+		"this.age = a; "
    						+ 	"}"
    					   + "}"
    					   + "String name;"
    					   + "Student s;"
    					   + "s = new.Student(name);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestWrongNewExpParameterNumber() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Fails since there are two inputs for new student but student only has a constructor with 
    	//one input
    	final String input = "public class Student{"
    						+	"public int age;"
    						+	"public Student(int a){"
    						+		"this.age = a;"
    						+ 	"}"
    						+	"public int getVariable(){"
    						+		"return this.age;"
    						+	"}"
    						+	"public void setVariable(int a){"
    						+		"this.age = a; "
    						+ 	"}"
    					   + "}"
    					   + "String name;"
    					   + "Student s;"
    					   + "s = new.Student(name,name);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void TestMultipleGenericFullClassWithMethodsAndStatements() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Multiple generic class with new class object call outside
    	final String input = "public class Student<A,B>{"
    						+	"public B variable;"
    						+	"public Student(B var){"
    						+		"this.variable = var;"
    						+ 	"}"
    						+	"public B getVariable(){"
    						+		"return this.variable;"
    						+	"}"
    						+	"public void setVariable(B var){"
    						+		"this.variable = var; "
    						+ 	"}"
    					   + "}"
    					   + "String name;"
    					   + "Student<int, String> s;"
    					   + "s = new.Student<int, String>(name);";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test(expected = TypeErrorException.class)
    public void failsTestMultipleGenericFullClassWithMethodsAndStatements() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Fails since B is set to int and new student with type B is called with a string 
    	final String input = "public class Student<A,B>{"
    						+	"public B variable;"
    						+	"public Student(B var){"
    						+		"this.variable = var;"
    						+ 	"}"
    						+	"public B getVariable(){"
    						+		"return this.variable;"
    						+	"}"
    						+	"public void setVariable(B var){"
    						+		"this.variable = var; "
    						+ 	"}"
    					   + "}"
    					   + "String name;"
    					   + "Student<String, int> s;"
    					   + "s = new.Student<String, int>(name);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testInstanceVariablesFromParentClass() throws TypeErrorException, TokenizerException, 
    ParserException {
    	
    	//Testing if calling instance variables from the parent class works 
    	final String input = "public class Person{"
    						+	"public int age;"
    						+	"public Person(int a){"
    						+		"this.age = a;"
    						+ 	"}"
    					    +"}"
    						+"public class Student extends Person{"
    					    +	"public int year;"
    						+	"public Student(int a, int b){"
    					    +		"this.age = a;"
    					    +		"this.year = b;"
    					    +	"}"
    					    +	"public int getAge(){"
    					    + 		"return this.age;"
    					    +	"}"
    					    +"}";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram();
    	
    	Typechecker.Programcheck(program); 
    }
    
    @Test
    public void testInstanceVariablesFromParentClassNested() throws TypeErrorException, 
    TokenizerException, ParserException {
    	
    	//Testing if calling instance variables from the parent class works 
    	final String input = "public class Person{"
    						+	"public int age;"
    						+	"public Person(int a){"
    						+		"this.age = a;"
    						+ 	"}"
    					    +"}"
    						+"public class Student extends Person{"
    					    +	"public int grade;"
    						+	"public Student(int a, int b){"
    					    +		"this.age = a;"
    					    +		"this.grade = b;"
    					    +	"}"
    					    +"}"
    						+"public class Junior extends Student{"
    					    +	"public String name;"
    						+	"public Junior(int a, int b, String c){"
    					    +		"this.age = a;"
    						+		"this.grade = b;"
    					    +		"this.name = c;"
    					    +	"}"
    					    +"}"
							+"public class Foo extends Junior{"
							+	"public String foo1;"
							+	"public Foo(int a, int b, String c, String d){"
							+		"this.age = a;"
							+		"this.grade = b;"
							+		"this.name = c;"
							+		"this.foo1 = d;"
							+	"}"
							+	"public int getGrade(){"
							+		"return this.grade;"
							+	"}"
							+"}";
    	
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	
    	Token[] tokenArray = new Token[tokenList.size()];
    	
    	tokenArray = tokenList.toArray(tokenArray); 
    	
    	final Parser parser = new Parser(tokenArray); 
    	final Program program = parser.parseProgram(); 
    	
    	Typechecker.Programcheck(program); 
    }
}