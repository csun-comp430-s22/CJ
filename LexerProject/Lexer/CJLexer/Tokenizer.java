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
			put("&&", new AndToken());
			put("||", new OrToken());
		}

	};

    // begin instance variables
    private final char[] input;
    private int inputPos; // position in the input
    // end instance variables
    
    public Tokenizer(final char[] input) {
        this.input = input;
        inputPos = 0;
    }

    public static boolean isTokenString(final String input) {
        return tokenMap.containsKey(input);
    }
    
    // assumes there is at least one character left
    // returns null if it couldn't parse a number
    private NumberToken tryTokenizeNumber() {
        final int initialInputPos = inputPos;
        String digits = "";

        while (inputPos < input.length &&
               Character.isDigit(input[inputPos])) {
            digits += input[inputPos];
            inputPos++;
        }

        if (digits.length() > 0) {
            return new NumberToken(Integer.parseInt(digits));
        } else {
            // reset position
            inputPos = initialInputPos;
            return null;
        }
    }

    // assumes there is at least one character left
    // returns null if it couldn't parse a variable
    private NameToken tryTokenizeName() {
        final int initialInputPos = inputPos;
        String name = "";

        if (Character.isLetter(input[inputPos])) {
            name += input[inputPos];
            inputPos++;            
            while (inputPos < input.length &&
                   Character.isLetterOrDigit(input[inputPos])) {
                name += input[inputPos];
                inputPos++;
            }
        } else {
            // reset position
            inputPos = initialInputPos;
            return null;
        }

        if (isTokenString(name)) {
            // reset position
            inputPos = initialInputPos;
            return null;
        } else {
            return new NameToken(name);
        }
    }
    
    private QuotedStringToken tryTokenizeQuotedString() {
    	final int initialInputPos = inputPos;
    	//check for quoted strings
    	if(input[inputPos] == '"') {
    		inputPos++;
    		int strStart = inputPos;
    		while(input[inputPos] != '"') {
    			inputPos++;
    			//if end of program is hit and quote is never closed
    			//then resets inputPos and returns null
    			if(inputPos >= input.length) {
    				inputPos = initialInputPos;
    				return null;
    			}
    		}
        	inputPos++;
        	return new QuotedStringToken(new String(input, strStart, inputPos-strStart-1));
    	}else {
        	//Quoted String must start with a quote
        	//then resets inputPos and returns null
    		inputPos = initialInputPos;
			return null;
    	}
    	
    }

    private boolean prefixCharsEqual(final String probe) {
        int targetPos = inputPos;
        int probePos = 0;

        while (targetPos < input.length &&
               probePos < probe.length() &&
               probe.charAt(probePos) == input[targetPos]) {
            probePos++;
            targetPos++;
        }

        return probePos == probe.length();
    }
            
    // returns null if it couldn't parse a token
    private Token tryTokenizeOther() {
    	//check for other tokens
        for (final Map.Entry<String, Token> entry : tokenMap.entrySet()) {
            final String key = entry.getKey();
            if (prefixCharsEqual(key)) {
                inputPos += key.length();
                return entry.getValue();
            }
        }
        return null;
    }
    
    private void skipWhitespace() {
        while (inputPos < input.length &&
               Character.isWhitespace(input[inputPos])) {
            inputPos++;
        }
    }

    // returns null if there are no more tokens
    public Token tokenizeSingle() throws TokenizerException {
        NameToken var = null;
        NumberToken num = null;
        QuotedStringToken str = null;
        Token otherToken = null;

        skipWhitespace();

        if (inputPos >= input.length) {
            return null;
        } else if ((str = tryTokenizeQuotedString()) != null) {
        	return str;
        } else if ((var = tryTokenizeName()) != null) {
            return var;
        } else if ((num = tryTokenizeNumber()) != null) {
            return num;
        } else if ((otherToken = tryTokenizeOther()) != null) {
            return otherToken;
        } else {
            throw new TokenizerException("Invalid character " +
                                         input[inputPos] +
                                         " at position " +
                                         inputPos);
        }
    }
    
    public List<Token> tokenize() throws TokenizerException {
        List<Token> list = new ArrayList<Token>();
        Token current = null;
            
        while ((current = tokenizeSingle()) != null) {
            list.add(current);
        }

        return list;
    }

}
