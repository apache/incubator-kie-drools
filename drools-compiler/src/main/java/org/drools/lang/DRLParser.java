// $ANTLR 3.0.1 /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2008-07-23 13:47:24

	package org.drools.lang;
	
	import org.drools.compiler.DroolsParserException;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class DRLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TEMPLATE_ID", "VT_TYPE_DECLARE_ID", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_SLOT_ID", "VT_SLOT", "VT_RULE_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_DURATION", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_WHEN", "VK_RULE", "VK_IMPORT", "VK_PACKAGE", "VK_TEMPLATE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_CONTAINS", "VK_MATCHES", "VK_EXCLUDES", "VK_SOUNDSLIKE", "VK_MEMBEROF", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_FROM", "VK_ACCUMULATE", "VK_INIT", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "VK_COLLECT", "SEMICOLON", "ID", "DOT", "DOT_STAR", "END", "STRING", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "AT", "COLON", "EQUALS", "BOOL", "INT", "DOUBLE_PIPE", "DOUBLE_AMPER", "OVER", "ARROW", "EQUAL", "GREATER", "GREATER_EQUAL", "LESS", "LESS_EQUAL", "NOT_EQUAL", "GRAVE_ACCENT", "FLOAT", "NULL", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "MISC", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT"
    };
    public static final int COMMA=94;
    public static final int VT_PATTERN_TYPE=39;
    public static final int VT_ACCUMULATE_ID_CLAUSE=28;
    public static final int VK_DIALECT=54;
    public static final int VK_FUNCTION=65;
    public static final int END=91;
    public static final int HexDigit=123;
    public static final int VK_ATTRIBUTES=57;
    public static final int VT_EXPRESSION_CHAIN=30;
    public static final int VK_ACCUMULATE=81;
    public static final int MISC=119;
    public static final int VT_AND_PREFIX=23;
    public static final int VK_QUERY=63;
    public static final int THEN=116;
    public static final int VK_AUTO_FOCUS=49;
    public static final int DOT=89;
    public static final int VK_IMPORT=60;
    public static final int VT_SLOT=15;
    public static final int VT_PACKAGE_ID=40;
    public static final int LEFT_SQUARE=114;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=126;
    public static final int VT_DATA_TYPE=38;
    public static final int VT_FACT=6;
    public static final int VK_MATCHES=69;
    public static final int LEFT_CURLY=117;
    public static final int AT=96;
    public static final int DOUBLE_AMPER=102;
    public static final int LEFT_PAREN=93;
    public static final int VT_QUERY_ID=9;
    public static final int VT_ACCESSOR_PATH=36;
    public static final int VT_LABEL=8;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int WS=121;
    public static final int VT_FIELD=35;
    public static final int VK_SALIENCE=55;
    public static final int VK_SOUNDSLIKE=71;
    public static final int OVER=103;
    public static final int STRING=92;
    public static final int VK_AND=77;
    public static final int VT_ACCESSOR_ELEMENT=37;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=27;
    public static final int VK_GLOBAL=66;
    public static final int VK_REVERSE=84;
    public static final int VT_BEHAVIOR=21;
    public static final int GRAVE_ACCENT=111;
    public static final int VK_DURATION=53;
    public static final int VT_SQUARE_CHUNK=19;
    public static final int VK_FORALL=79;
    public static final int VT_PAREN_CHUNK=20;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int VK_ENABLED=56;
    public static final int EQUALS=98;
    public static final int VK_RESULT=85;
    public static final int UnicodeEscape=124;
    public static final int VK_PACKAGE=61;
    public static final int VT_RULE_ID=12;
    public static final int EQUAL=105;
    public static final int VK_NO_LOOP=48;
    public static final int SEMICOLON=87;
    public static final int VK_TEMPLATE=62;
    public static final int VT_AND_IMPLICIT=22;
    public static final int NULL=113;
    public static final int COLON=97;
    public static final int MULTI_LINE_COMMENT=128;
    public static final int VT_RULE_ATTRIBUTES=16;
    public static final int RIGHT_SQUARE=115;
    public static final int VK_AGENDA_GROUP=51;
    public static final int VT_FACT_OR=33;
    public static final int VK_NOT=74;
    public static final int VK_DATE_EXPIRES=46;
    public static final int ARROW=104;
    public static final int FLOAT=112;
    public static final int VT_SLOT_ID=14;
    public static final int VT_CURLY_CHUNK=18;
    public static final int VT_OR_PREFIX=24;
    public static final int DOUBLE_PIPE=101;
    public static final int LESS=108;
    public static final int VT_TYPE_DECLARE_ID=11;
    public static final int VT_PATTERN=31;
    public static final int VK_DATE_EFFECTIVE=45;
    public static final int EscapeSequence=122;
    public static final int VK_EXISTS=78;
    public static final int INT=100;
    public static final int VT_BIND_FIELD=34;
    public static final int VK_RULE=59;
    public static final int VK_EVAL=67;
    public static final int VK_COLLECT=86;
    public static final int GREATER=106;
    public static final int VT_FACT_BINDING=32;
    public static final int ID=88;
    public static final int NOT_EQUAL=110;
    public static final int RIGHT_CURLY=118;
    public static final int BOOL=99;
    public static final int VT_AND_INFIX=25;
    public static final int VT_PARAM_LIST=44;
    public static final int VK_ENTRY_POINT=73;
    public static final int VT_FROM_SOURCE=29;
    public static final int VK_LOCK_ON_ACTIVE=47;
    public static final int VK_CONTAINS=68;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VK_IN=75;
    public static final int VT_RHS_CHUNK=17;
    public static final int GREATER_EQUAL=107;
    public static final int VK_MEMBEROF=72;
    public static final int VT_OR_INFIX=26;
    public static final int DOT_STAR=90;
    public static final int VK_OR=76;
    public static final int VT_GLOBAL_ID=42;
    public static final int LESS_EQUAL=109;
    public static final int VK_WHEN=58;
    public static final int VK_RULEFLOW_GROUP=52;
    public static final int VT_FUNCTION_ID=43;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int EOL=120;
    public static final int VT_IMPORT_ID=41;
    public static final int VK_ACTIVATION_GROUP=50;
    public static final int VK_INIT=82;
    public static final int OctalEscape=125;
    public static final int VK_ACTION=83;
    public static final int VK_EXCLUDES=70;
    public static final int VK_FROM=80;
    public static final int RIGHT_PAREN=95;
    public static final int VT_TEMPLATE_ID=10;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=127;
    public static final int VK_DECLARE=64;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[145+1];
         }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g"; }


    	private Stack<Map<Integer, String>> paraphrases = new Stack<Map<Integer, String>>();
    	private List<DroolsParserException> errors = new ArrayList<DroolsParserException>();
    	private DroolsParserExceptionFactory errorMessageFactory = new DroolsParserExceptionFactory(tokenNames, paraphrases);
    	private String source = "unknown";
    	private boolean lookaheadTest = false;

    	private boolean validateLT(int LTNumber, String text) {
    		if (null == input)
    			return false;
    		if (null == input.LT(LTNumber))
    			return false;
    		if (null == input.LT(LTNumber).getText())
    			return false;
    	
    		String text2Validate = input.LT(LTNumber).getText();
    		return text2Validate.equalsIgnoreCase(text);
    	}
    	
    	private boolean validateIdentifierKey(String text) {
    		return validateLT(1, text);
    	}
    	
    	void checkTrailingSemicolon(String text, Token token) {
    		if (text.trim().endsWith(";")) {
    			errors.add(errorMessageFactory
    					.createTrailingSemicolonException(((DroolsToken) token)
    							.getLine(), ((DroolsToken) token)
    							.getCharPositionInLine(), ((DroolsToken) token)
    							.getStopIndex()));
    		}
    	}
    	
    	private boolean validateRestr() {
    		int lookahead = 2;
    		int countParen = 1;

    		while (true) {
    			if (input.LA(lookahead) == COMMA) {
    				break;
    			} else if (input.LA(lookahead) == LEFT_PAREN) {
    				countParen++;
    			} else if (input.LA(lookahead) == RIGHT_PAREN) {
    				countParen--;
    			}
    			if (countParen == 0){
    				break;
    			}
    			lookahead++;
    		}
    		
    		boolean returnValue = false;
    		int activeIndex = input.index();
    		lookaheadTest = true;
    		try {
    			input.seek(input.LT(2).getTokenIndex());
    			constraint_expression();
    			returnValue = true;
    		} catch (RecognitionException e) {
    		} finally{
    			input.seek(activeIndex);
    		}
    		lookaheadTest = false;

    		return returnValue;
    	}
    	
    	private String safeSubstring(String text, int start, int end) {
    		return text.substring(Math.min(start, text.length()), Math.min(Math
    				.max(start, end), text.length()));
    	}
    	
    	public void reportError(RecognitionException ex) {
    		// if we've already reported an error and have not matched a token
    		// yet successfully, don't report any errors.
    		if (errorRecovery) {
    			return;
    		}
    		errorRecovery = true;
    	
    		errors.add(errorMessageFactory.createDroolsException(ex));
    	}
    	
    	/** return the raw DroolsParserException errors */
    	public List<DroolsParserException> getErrors() {
    		return errors;
    	}
    	
    	/** Return a list of pretty strings summarising the errors */
    	public List<String> getErrorMessages() {
    		List<String> messages = new ArrayList<String>(errors.size());
    	
    		for (DroolsParserException activeException : errors) {
    			messages.add(activeException.getMessage());
    		}
    	
    		return messages;
    	}
    	
    	/** return true if any parser errors were accumulated */
    	public boolean hasErrors() {
    		return !errors.isEmpty();
    	}

    	/**
    	 * Method that adds a paraphrase type into paraphrases stack.
    	 * 
    	 * @param type
    	 *            paraphrase type
    	 */
    	private void pushParaphrases(int type) {
    		Map<Integer, String> activeMap = new HashMap<Integer, String>();
    		activeMap.put(type, "");
    		paraphrases.push(activeMap);
    	}

    	/**
    	 * Method that sets paraphrase value for a type into paraphrases stack.
    	 * 
    	 * @param type
    	 *            paraphrase type
    	 * @param value
    	 *            paraphrase value
    	 */
    	private void setParaphrasesValue(int type, String value) {
    		paraphrases.peek().put(type, value);
    	}

    	/**
    	 * Helper method that creates a string from a token list.
    	 * 
    	 * @param tokenList
    	 *            token list
    	 * @return string
    	 */
    	private String buildStringFromTokens(List<Token> tokenList) {
    		StringBuilder sb = new StringBuilder();
    		if (null != tokenList) {
    			for (Token activeToken : tokenList) {
    				if (null != activeToken) {
    					sb.append(activeToken.getText());
    				}
    			}
    		}
    		return sb.toString();
    	}


    public static class compilation_unit_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start compilation_unit
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:279:1: compilation_unit : ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) ;
    public final compilation_unit_return compilation_unit() throws RecognitionException {
        compilation_unit_return retval = new compilation_unit_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF3=null;
        package_statement_return package_statement1 = null;

        statement_return statement2 = null;


        Object EOF3_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_package_statement=new RewriteRuleSubtreeStream(adaptor,"rule package_statement");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:280:2: ( ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:280:4: ( package_statement )? ( statement )* EOF
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:280:4: ( package_statement )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))) {
                int LA1_1 = input.LA(2);

                if ( (LA1_1==ID) && ((((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))) {
                    int LA1_4 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.PACKAGE))) ) {
                        alt1=1;
                    }
                }
            }
            switch (alt1) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:280:4: package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_compilation_unit408);
                    package_statement1=package_statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_package_statement.add(package_statement1.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:281:3: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:281:3: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit413);
            	    statement2=statement();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_statement.add(statement2.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            EOF3=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_compilation_unit418); if (failed) return retval;
            if ( backtracking==0 ) stream_EOF.add(EOF3);


            // AST REWRITE
            // elements: statement, package_statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 283:3: -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:283:6: ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_COMPILATION_UNIT, "VT_COMPILATION_UNIT"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:283:28: ( package_statement )?
                if ( stream_package_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_package_statement.next());

                }
                stream_package_statement.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:283:47: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_statement.next());

                }
                stream_statement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch ( RecognitionException e ) {

            		reportError( e );
            	
        }
        catch ( RewriteEmptyStreamException e ) {

            	
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end compilation_unit

    public static class package_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start package_statement
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:291:1: package_statement : package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) ;
    public final package_statement_return package_statement() throws RecognitionException {
        package_statement_return retval = new package_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON6=null;
        package_key_return package_key4 = null;

        package_id_return package_id5 = null;


        Object SEMICOLON6_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_package_key=new RewriteRuleSubtreeStream(adaptor,"rule package_key");
        RewriteRuleSubtreeStream stream_package_id=new RewriteRuleSubtreeStream(adaptor,"rule package_id");
         pushParaphrases(DroolsParaphareseTypes.PACKAGE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:294:2: ( package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:294:4: package_key package_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_package_key_in_package_statement469);
            package_key4=package_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_package_key.add(package_key4.getTree());
            pushFollow(FOLLOW_package_id_in_package_statement471);
            package_id5=package_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_package_id.add(package_id5.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:294:27: ( SEMICOLON )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==SEMICOLON) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:294:27: SEMICOLON
                    {
                    SEMICOLON6=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_package_statement473); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON6);


                    }
                    break;

            }


            // AST REWRITE
            // elements: package_id, package_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 295:3: -> ^( package_key package_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:295:6: ^( package_key package_id )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_package_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_package_id.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end package_statement

    public static class package_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start package_id
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:298:1: package_id : id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) ;
    public final package_id_return package_id() throws RecognitionException {
        package_id_return retval = new package_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:299:2: (id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:299:4: id+= ID (id+= DOT id+= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_id497); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:299:11: (id+= DOT id+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:299:13: id+= DOT id+= ID
            	    {
            	    id=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_package_id503); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_package_id507); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            if ( backtracking==0 ) {
              	setParaphrasesValue(DroolsParaphareseTypes.PACKAGE, buildStringFromTokens(list_id));	
            }

            // AST REWRITE
            // elements: ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 301:3: -> ^( VT_PACKAGE_ID ( ID )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:301:6: ^( VT_PACKAGE_ID ( ID )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_PACKAGE_ID, "VT_PACKAGE_ID"), root_1);

                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, stream_ID.next());

                }
                stream_ID.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end package_id

    public static class statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start statement
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:304:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );
    public final statement_return statement() throws RecognitionException {
        statement_return retval = new statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        rule_attribute_return rule_attribute7 = null;

        function_import_statement_return function_import_statement8 = null;

        import_statement_return import_statement9 = null;

        global_return global10 = null;

        function_return function11 = null;

        template_return template12 = null;

        type_declaration_return type_declaration13 = null;

        rule_return rule14 = null;

        query_return query15 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:307:3: ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query )
            int alt5=9;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))))) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                    alt5=1;
                }
                else if ( (LA5_1==ID) && ((((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))))) {
                    int LA5_3 = input.LA(3);

                    if ( (((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) ) {
                        alt5=2;
                    }
                    else if ( ((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {
                        alt5=3;
                    }
                    else if ( ((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))) ) {
                        alt5=4;
                    }
                    else if ( ((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))) ) {
                        alt5=5;
                    }
                    else if ( (((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))) ) {
                        alt5=6;
                    }
                    else if ( (((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))) ) {
                        alt5=7;
                    }
                    else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {
                        alt5=8;
                    }
                    else if ( ((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {
                        alt5=9;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("304:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 3, input);

                        throw nvae;
                    }
                }
                else if ( (LA5_1==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))))) {
                    int LA5_4 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.DIALECT))) ) {
                        alt5=1;
                    }
                    else if ( (((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))) ) {
                        alt5=6;
                    }
                    else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {
                        alt5=8;
                    }
                    else if ( ((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {
                        alt5=9;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("304:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 4, input);

                        throw nvae;
                    }
                }
                else if ( (LA5_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {
                    alt5=1;
                }
                else if ( (LA5_1==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {
                    alt5=1;
                }
                else if ( (LA5_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {
                    alt5=1;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("304:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("304:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:307:5: rule_attribute
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_attribute_in_statement545);
                    rule_attribute7=rule_attribute();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, rule_attribute7.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:308:3: {...}? => function_import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((validateLT(1, "import") && validateLT(2, "function") )) ) {
                        if (backtracking>0) {failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, \"import\") && validateLT(2, \"function\") )");
                    }
                    pushFollow(FOLLOW_function_import_statement_in_statement552);
                    function_import_statement8=function_import_statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, function_import_statement8.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:309:4: import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_import_statement_in_statement558);
                    import_statement9=import_statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, import_statement9.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:310:4: global
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_global_in_statement564);
                    global10=global();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, global10.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:311:4: function
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_function_in_statement570);
                    function11=function();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, function11.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:312:4: {...}? => template
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((validateLT(1, DroolsSoftKeywords.TEMPLATE))) ) {
                        if (backtracking>0) {failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, DroolsSoftKeywords.TEMPLATE))");
                    }
                    pushFollow(FOLLOW_template_in_statement578);
                    template12=template();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, template12.getTree());

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:313:4: {...}? => type_declaration
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((validateLT(1, DroolsSoftKeywords.DECLARE))) ) {
                        if (backtracking>0) {failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, DroolsSoftKeywords.DECLARE))");
                    }
                    pushFollow(FOLLOW_type_declaration_in_statement586);
                    type_declaration13=type_declaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, type_declaration13.getTree());

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:314:4: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_statement591);
                    rule14=rule();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, rule14.getTree());

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:315:4: query
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_query_in_statement596);
                    query15=query();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, query15.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end statement

    public static class import_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start import_statement
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:318:1: import_statement : import_key import_name[DroolsParaphareseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) ;
    public final import_statement_return import_statement() throws RecognitionException {
        import_statement_return retval = new import_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON18=null;
        import_key_return import_key16 = null;

        import_name_return import_name17 = null;


        Object SEMICOLON18_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_import_key=new RewriteRuleSubtreeStream(adaptor,"rule import_key");
        RewriteRuleSubtreeStream stream_import_name=new RewriteRuleSubtreeStream(adaptor,"rule import_name");
         pushParaphrases(DroolsParaphareseTypes.IMPORT); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:321:2: ( import_key import_name[DroolsParaphareseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:321:4: import_key import_name[DroolsParaphareseTypes.IMPORT] ( SEMICOLON )?
            {
            pushFollow(FOLLOW_import_key_in_import_statement618);
            import_key16=import_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_key.add(import_key16.getTree());
            pushFollow(FOLLOW_import_name_in_import_statement620);
            import_name17=import_name(DroolsParaphareseTypes.IMPORT);
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_name.add(import_name17.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:321:58: ( SEMICOLON )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==SEMICOLON) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:321:58: SEMICOLON
                    {
                    SEMICOLON18=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_import_statement623); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON18);


                    }
                    break;

            }


            // AST REWRITE
            // elements: import_key, import_name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 322:3: -> ^( import_key import_name )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:322:6: ^( import_key import_name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_import_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_import_name.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end import_statement

    public static class function_import_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start function_import_statement
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:325:1: function_import_statement : imp= import_key function_key import_name[DroolsParaphareseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) ;
    public final function_import_statement_return function_import_statement() throws RecognitionException {
        function_import_statement_return retval = new function_import_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON21=null;
        import_key_return imp = null;

        function_key_return function_key19 = null;

        import_name_return import_name20 = null;


        Object SEMICOLON21_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_import_key=new RewriteRuleSubtreeStream(adaptor,"rule import_key");
        RewriteRuleSubtreeStream stream_function_key=new RewriteRuleSubtreeStream(adaptor,"rule function_key");
        RewriteRuleSubtreeStream stream_import_name=new RewriteRuleSubtreeStream(adaptor,"rule import_name");
         pushParaphrases(DroolsParaphareseTypes.FUNCTION_IMPORT); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:328:2: (imp= import_key function_key import_name[DroolsParaphareseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:328:4: imp= import_key function_key import_name[DroolsParaphareseTypes.FUNCTION_IMPORT] ( SEMICOLON )?
            {
            pushFollow(FOLLOW_import_key_in_function_import_statement658);
            imp=import_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_key.add(imp.getTree());
            pushFollow(FOLLOW_function_key_in_function_import_statement660);
            function_key19=function_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_function_key.add(function_key19.getTree());
            pushFollow(FOLLOW_import_name_in_function_import_statement662);
            import_name20=import_name(DroolsParaphareseTypes.FUNCTION_IMPORT);
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_name.add(import_name20.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:328:84: ( SEMICOLON )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==SEMICOLON) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:328:84: SEMICOLON
                    {
                    SEMICOLON21=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_function_import_statement665); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON21);


                    }
                    break;

            }


            // AST REWRITE
            // elements: function_key, import_name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 329:3: -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:329:6: ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FUNCTION_IMPORT, ((Token)imp.start)), root_1);

                adaptor.addChild(root_1, stream_function_key.next());
                adaptor.addChild(root_1, stream_import_name.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end function_import_statement

    public static class import_name_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start import_name
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:332:1: import_name[int importType] : id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) ;
    public final import_name_return import_name(int importType) throws RecognitionException {
        import_name_return retval = new import_name_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_DOT_STAR=new RewriteRuleTokenStream(adaptor,"token DOT_STAR");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:333:2: (id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:333:4: id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name694); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:333:11: (id+= DOT id+= ID )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==DOT) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:333:13: id+= DOT id+= ID
            	    {
            	    id=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_import_name700); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_import_name704); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:333:33: (id+= DOT_STAR )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==DOT_STAR) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:333:33: id+= DOT_STAR
                    {
                    id=(Token)input.LT(1);
                    match(input,DOT_STAR,FOLLOW_DOT_STAR_in_import_name711); if (failed) return retval;
                    if ( backtracking==0 ) stream_DOT_STAR.add(id);

                    if (list_id==null) list_id=new ArrayList();
                    list_id.add(id);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	setParaphrasesValue(importType, buildStringFromTokens(list_id));	
            }

            // AST REWRITE
            // elements: DOT_STAR, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 335:3: -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:335:6: ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_IMPORT_ID, "VT_IMPORT_ID"), root_1);

                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, stream_ID.next());

                }
                stream_ID.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:335:25: ( DOT_STAR )?
                if ( stream_DOT_STAR.hasNext() ) {
                    adaptor.addChild(root_1, stream_DOT_STAR.next());

                }
                stream_DOT_STAR.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end import_name

    public static class global_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start global
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:338:1: global : global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) ;
    public final global_return global() throws RecognitionException {
        global_return retval = new global_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON25=null;
        global_key_return global_key22 = null;

        data_type_return data_type23 = null;

        global_id_return global_id24 = null;


        Object SEMICOLON25_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        RewriteRuleSubtreeStream stream_global_id=new RewriteRuleSubtreeStream(adaptor,"rule global_id");
        RewriteRuleSubtreeStream stream_global_key=new RewriteRuleSubtreeStream(adaptor,"rule global_key");
         pushParaphrases(DroolsParaphareseTypes.GLOBAL); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:341:2: ( global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:341:4: global_key data_type global_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_global_key_in_global751);
            global_key22=global_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_global_key.add(global_key22.getTree());
            pushFollow(FOLLOW_data_type_in_global753);
            data_type23=data_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_data_type.add(data_type23.getTree());
            pushFollow(FOLLOW_global_id_in_global755);
            global_id24=global_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_global_id.add(global_id24.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:341:35: ( SEMICOLON )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==SEMICOLON) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:341:35: SEMICOLON
                    {
                    SEMICOLON25=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_global757); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON25);


                    }
                    break;

            }


            // AST REWRITE
            // elements: global_key, data_type, global_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 342:3: -> ^( global_key data_type global_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:342:6: ^( global_key data_type global_id )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_global_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_data_type.next());
                adaptor.addChild(root_1, stream_global_id.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end global

    public static class global_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start global_id
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:345:1: global_id : id= ID -> VT_GLOBAL_ID[$id] ;
    public final global_id_return global_id() throws RecognitionException {
        global_id_return retval = new global_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:346:2: (id= ID -> VT_GLOBAL_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:346:4: id= ID
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global_id783); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	setParaphrasesValue(DroolsParaphareseTypes.GLOBAL, id.getText());	
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 348:3: -> VT_GLOBAL_ID[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VT_GLOBAL_ID, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end global_id

    public static class function_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start function
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:351:1: function : function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) ;
    public final function_return function() throws RecognitionException {
        function_return retval = new function_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        function_key_return function_key26 = null;

        data_type_return data_type27 = null;

        function_id_return function_id28 = null;

        parameters_return parameters29 = null;

        curly_chunk_return curly_chunk30 = null;


        RewriteRuleSubtreeStream stream_parameters=new RewriteRuleSubtreeStream(adaptor,"rule parameters");
        RewriteRuleSubtreeStream stream_function_key=new RewriteRuleSubtreeStream(adaptor,"rule function_key");
        RewriteRuleSubtreeStream stream_curly_chunk=new RewriteRuleSubtreeStream(adaptor,"rule curly_chunk");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        RewriteRuleSubtreeStream stream_function_id=new RewriteRuleSubtreeStream(adaptor,"rule function_id");
         pushParaphrases(DroolsParaphareseTypes.FUNCTION); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:354:2: ( function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:354:4: function_key ( data_type )? function_id parameters curly_chunk
            {
            pushFollow(FOLLOW_function_key_in_function815);
            function_key26=function_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_function_key.add(function_key26.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:354:17: ( data_type )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ID) ) {
                int LA11_1 = input.LA(2);

                if ( ((LA11_1>=ID && LA11_1<=DOT)||LA11_1==LEFT_SQUARE) ) {
                    alt11=1;
                }
            }
            switch (alt11) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:354:17: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function817);
                    data_type27=data_type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_data_type.add(data_type27.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_function_id_in_function820);
            function_id28=function_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_function_id.add(function_id28.getTree());
            pushFollow(FOLLOW_parameters_in_function822);
            parameters29=parameters();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_parameters.add(parameters29.getTree());
            pushFollow(FOLLOW_curly_chunk_in_function824);
            curly_chunk30=curly_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_curly_chunk.add(curly_chunk30.getTree());

            // AST REWRITE
            // elements: parameters, function_id, data_type, curly_chunk, function_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 355:3: -> ^( function_key ( data_type )? function_id parameters curly_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:355:6: ^( function_key ( data_type )? function_id parameters curly_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_function_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:355:21: ( data_type )?
                if ( stream_data_type.hasNext() ) {
                    adaptor.addChild(root_1, stream_data_type.next());

                }
                stream_data_type.reset();
                adaptor.addChild(root_1, stream_function_id.next());
                adaptor.addChild(root_1, stream_parameters.next());
                adaptor.addChild(root_1, stream_curly_chunk.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end function

    public static class function_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start function_id
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:358:1: function_id : id= ID -> VT_FUNCTION_ID[$id] ;
    public final function_id_return function_id() throws RecognitionException {
        function_id_return retval = new function_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:359:2: (id= ID -> VT_FUNCTION_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:359:4: id= ID
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function_id854); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	setParaphrasesValue(DroolsParaphareseTypes.FUNCTION, id.getText());	
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 361:3: -> VT_FUNCTION_ID[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VT_FUNCTION_ID, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end function_id

    public static class query_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start query
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:364:1: query : query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block END ) ;
    public final query_return query() throws RecognitionException {
        query_return retval = new query_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token END35=null;
        Token SEMICOLON36=null;
        query_key_return query_key31 = null;

        query_id_return query_id32 = null;

        parameters_return parameters33 = null;

        normal_lhs_block_return normal_lhs_block34 = null;


        Object END35_tree=null;
        Object SEMICOLON36_tree=null;
        RewriteRuleTokenStream stream_END=new RewriteRuleTokenStream(adaptor,"token END");
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_parameters=new RewriteRuleSubtreeStream(adaptor,"rule parameters");
        RewriteRuleSubtreeStream stream_query_key=new RewriteRuleSubtreeStream(adaptor,"rule query_key");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        RewriteRuleSubtreeStream stream_query_id=new RewriteRuleSubtreeStream(adaptor,"rule query_id");
         pushParaphrases(DroolsParaphareseTypes.QUERY); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:2: ( query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:4: query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )?
            {
            pushFollow(FOLLOW_query_key_in_query886);
            query_key31=query_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_query_key.add(query_key31.getTree());
            pushFollow(FOLLOW_query_id_in_query888);
            query_id32=query_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_query_id.add(query_id32.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:23: ( parameters )?
            int alt12=2;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:23: parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query890);
                    parameters33=parameters();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_parameters.add(parameters33.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_normal_lhs_block_in_query893);
            normal_lhs_block34=normal_lhs_block();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block34.getTree());
            END35=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query895); if (failed) return retval;
            if ( backtracking==0 ) stream_END.add(END35);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:56: ( SEMICOLON )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==SEMICOLON) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:56: SEMICOLON
                    {
                    SEMICOLON36=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_query897); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON36);


                    }
                    break;

            }


            // AST REWRITE
            // elements: parameters, query_key, normal_lhs_block, query_id, END
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 368:3: -> ^( query_key query_id ( parameters )? normal_lhs_block END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:368:6: ^( query_key query_id ( parameters )? normal_lhs_block END )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_query_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_query_id.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:368:27: ( parameters )?
                if ( stream_parameters.hasNext() ) {
                    adaptor.addChild(root_1, stream_parameters.next());

                }
                stream_parameters.reset();
                adaptor.addChild(root_1, stream_normal_lhs_block.next());
                adaptor.addChild(root_1, stream_END.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end query

    public static class query_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start query_id
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:1: query_id : (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] );
    public final query_id_return query_id() throws RecognitionException {
        query_id_return retval = new query_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:372:2: (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==ID) ) {
                alt14=1;
            }
            else if ( (LA14_0==STRING) ) {
                alt14=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("371:1: query_id : (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] );", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:372:5: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_query_id929); if (failed) return retval;
                    if ( backtracking==0 ) stream_ID.add(id);

                    if ( backtracking==0 ) {
                      	setParaphrasesValue(DroolsParaphareseTypes.QUERY, id.getText());	
                    }

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 373:67: -> VT_QUERY_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_QUERY_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:374:5: id= STRING
                    {
                    id=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_query_id945); if (failed) return retval;
                    if ( backtracking==0 ) stream_STRING.add(id);

                    if ( backtracking==0 ) {
                      	setParaphrasesValue(DroolsParaphareseTypes.QUERY, id.getText());	
                    }

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 375:67: -> VT_QUERY_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_QUERY_ID, id));

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end query_id

    public static class parameters_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start parameters
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:378:1: parameters : LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) ;
    public final parameters_return parameters() throws RecognitionException {
        parameters_return retval = new parameters_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN37=null;
        Token COMMA39=null;
        Token RIGHT_PAREN41=null;
        param_definition_return param_definition38 = null;

        param_definition_return param_definition40 = null;


        Object LEFT_PAREN37_tree=null;
        Object COMMA39_tree=null;
        Object RIGHT_PAREN41_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_param_definition=new RewriteRuleSubtreeStream(adaptor,"rule param_definition");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:379:2: ( LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:379:4: LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN
            {
            LEFT_PAREN37=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parameters964); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN37);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:380:4: ( param_definition ( COMMA param_definition )* )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==ID) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:380:6: param_definition ( COMMA param_definition )*
                    {
                    pushFollow(FOLLOW_param_definition_in_parameters971);
                    param_definition38=param_definition();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_param_definition.add(param_definition38.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:380:23: ( COMMA param_definition )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==COMMA) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:380:24: COMMA param_definition
                    	    {
                    	    COMMA39=(Token)input.LT(1);
                    	    match(input,COMMA,FOLLOW_COMMA_in_parameters974); if (failed) return retval;
                    	    if ( backtracking==0 ) stream_COMMA.add(COMMA39);

                    	    pushFollow(FOLLOW_param_definition_in_parameters976);
                    	    param_definition40=param_definition();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_param_definition.add(param_definition40.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    }
                    break;

            }

            RIGHT_PAREN41=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parameters985); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN41);


            // AST REWRITE
            // elements: RIGHT_PAREN, param_definition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 382:3: -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:6: ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_PARAM_LIST, "VT_PARAM_LIST"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:22: ( param_definition )*
                while ( stream_param_definition.hasNext() ) {
                    adaptor.addChild(root_1, stream_param_definition.next());

                }
                stream_param_definition.reset();
                adaptor.addChild(root_1, stream_RIGHT_PAREN.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end parameters

    public static class param_definition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start param_definition
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:385:1: param_definition : ( data_type )? argument ;
    public final param_definition_return param_definition() throws RecognitionException {
        param_definition_return retval = new param_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        data_type_return data_type42 = null;

        argument_return argument43 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:386:2: ( ( data_type )? argument )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:386:4: ( data_type )? argument
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:386:4: ( data_type )?
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:386:4: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition1009);
                    data_type42=data_type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, data_type42.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition1012);
            argument43=argument();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, argument43.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end param_definition

    public static class argument_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start argument
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:389:1: argument : ID ( dimension_definition )* ;
    public final argument_return argument() throws RecognitionException {
        argument_return retval = new argument_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID44=null;
        dimension_definition_return dimension_definition45 = null;


        Object ID44_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:390:2: ( ID ( dimension_definition )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:390:4: ID ( dimension_definition )*
            {
            root_0 = (Object)adaptor.nil();

            ID44=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument1023); if (failed) return retval;
            if ( backtracking==0 ) {
            ID44_tree = (Object)adaptor.create(ID44);
            adaptor.addChild(root_0, ID44_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:390:7: ( dimension_definition )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==LEFT_SQUARE) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:390:7: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_argument1025);
            	    dimension_definition45=dimension_definition();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, dimension_definition45.getTree());

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end argument

    public static class type_declaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start type_declaration
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:393:1: type_declaration : declare_key type_declare_id ( decl_metadata )* ( decl_field )* END -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END ) ;
    public final type_declaration_return type_declaration() throws RecognitionException {
        type_declaration_return retval = new type_declaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token END50=null;
        declare_key_return declare_key46 = null;

        type_declare_id_return type_declare_id47 = null;

        decl_metadata_return decl_metadata48 = null;

        decl_field_return decl_field49 = null;


        Object END50_tree=null;
        RewriteRuleTokenStream stream_END=new RewriteRuleTokenStream(adaptor,"token END");
        RewriteRuleSubtreeStream stream_decl_field=new RewriteRuleSubtreeStream(adaptor,"rule decl_field");
        RewriteRuleSubtreeStream stream_declare_key=new RewriteRuleSubtreeStream(adaptor,"rule declare_key");
        RewriteRuleSubtreeStream stream_decl_metadata=new RewriteRuleSubtreeStream(adaptor,"rule decl_metadata");
        RewriteRuleSubtreeStream stream_type_declare_id=new RewriteRuleSubtreeStream(adaptor,"rule type_declare_id");
         pushParaphrases(DroolsParaphareseTypes.TYPE_DECLARE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:396:2: ( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:396:4: declare_key type_declare_id ( decl_metadata )* ( decl_field )* END
            {
            pushFollow(FOLLOW_declare_key_in_type_declaration1048);
            declare_key46=declare_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_declare_key.add(declare_key46.getTree());
            pushFollow(FOLLOW_type_declare_id_in_type_declaration1051);
            type_declare_id47=type_declare_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_type_declare_id.add(type_declare_id47.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:397:3: ( decl_metadata )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AT) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:397:3: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_type_declaration1055);
            	    decl_metadata48=decl_metadata();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_decl_metadata.add(decl_metadata48.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:398:3: ( decl_field )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==ID) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:398:3: decl_field
            	    {
            	    pushFollow(FOLLOW_decl_field_in_type_declaration1060);
            	    decl_field49=decl_field();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_decl_field.add(decl_field49.getTree());

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            END50=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_type_declaration1065); if (failed) return retval;
            if ( backtracking==0 ) stream_END.add(END50);


            // AST REWRITE
            // elements: END, declare_key, decl_metadata, type_declare_id, decl_field
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 400:3: -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:400:6: ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_declare_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_type_declare_id.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:400:36: ( decl_metadata )*
                while ( stream_decl_metadata.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_metadata.next());

                }
                stream_decl_metadata.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:400:51: ( decl_field )*
                while ( stream_decl_field.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_field.next());

                }
                stream_decl_field.reset();
                adaptor.addChild(root_1, stream_END.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end type_declaration

    public static class type_declare_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start type_declare_id
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:403:1: type_declare_id : id= ID -> VT_TYPE_DECLARE_ID[$id] ;
    public final type_declare_id_return type_declare_id() throws RecognitionException {
        type_declare_id_return retval = new type_declare_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:404:2: (id= ID -> VT_TYPE_DECLARE_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:404:5: id= ID
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_type_declare_id1097); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	setParaphrasesValue(DroolsParaphareseTypes.TYPE_DECLARE, id.getText());	
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 405:74: -> VT_TYPE_DECLARE_ID[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VT_TYPE_DECLARE_ID, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end type_declare_id

    public static class decl_metadata_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start decl_metadata
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:408:1: decl_metadata : AT ID paren_chunk -> ^( AT ID paren_chunk ) ;
    public final decl_metadata_return decl_metadata() throws RecognitionException {
        decl_metadata_return retval = new decl_metadata_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AT51=null;
        Token ID52=null;
        paren_chunk_return paren_chunk53 = null;


        Object AT51_tree=null;
        Object ID52_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:409:2: ( AT ID paren_chunk -> ^( AT ID paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:409:4: AT ID paren_chunk
            {
            AT51=(Token)input.LT(1);
            match(input,AT,FOLLOW_AT_in_decl_metadata1116); if (failed) return retval;
            if ( backtracking==0 ) stream_AT.add(AT51);

            ID52=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_decl_metadata1118); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID52);

            pushFollow(FOLLOW_paren_chunk_in_decl_metadata1120);
            paren_chunk53=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk53.getTree());

            // AST REWRITE
            // elements: paren_chunk, ID, AT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 410:3: -> ^( AT ID paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:410:6: ^( AT ID paren_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_AT.next(), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                adaptor.addChild(root_1, stream_paren_chunk.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end decl_metadata

    public static class decl_field_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start decl_field
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:1: decl_field : ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) ;
    public final decl_field_return decl_field() throws RecognitionException {
        decl_field_return retval = new decl_field_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID54=null;
        Token COLON56=null;
        decl_field_initialization_return decl_field_initialization55 = null;

        data_type_return data_type57 = null;

        decl_metadata_return decl_metadata58 = null;


        Object ID54_tree=null;
        Object COLON56_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_decl_metadata=new RewriteRuleSubtreeStream(adaptor,"rule decl_metadata");
        RewriteRuleSubtreeStream stream_decl_field_initialization=new RewriteRuleSubtreeStream(adaptor,"rule decl_field_initialization");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:2: ( ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:4: ID ( decl_field_initialization )? COLON data_type ( decl_metadata )*
            {
            ID54=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_decl_field1143); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID54);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:7: ( decl_field_initialization )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==EQUALS) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:7: decl_field_initialization
                    {
                    pushFollow(FOLLOW_decl_field_initialization_in_decl_field1145);
                    decl_field_initialization55=decl_field_initialization();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_decl_field_initialization.add(decl_field_initialization55.getTree());

                    }
                    break;

            }

            COLON56=(Token)input.LT(1);
            match(input,COLON,FOLLOW_COLON_in_decl_field1148); if (failed) return retval;
            if ( backtracking==0 ) stream_COLON.add(COLON56);

            pushFollow(FOLLOW_data_type_in_decl_field1150);
            data_type57=data_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_data_type.add(data_type57.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:415:3: ( decl_metadata )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==AT) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:415:3: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_decl_field1154);
            	    decl_metadata58=decl_metadata();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_decl_metadata.add(decl_metadata58.getTree());

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);


            // AST REWRITE
            // elements: decl_metadata, decl_field_initialization, ID, data_type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 416:3: -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:416:6: ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ID.next(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:416:11: ( decl_field_initialization )?
                if ( stream_decl_field_initialization.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_field_initialization.next());

                }
                stream_decl_field_initialization.reset();
                adaptor.addChild(root_1, stream_data_type.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:416:48: ( decl_metadata )*
                while ( stream_decl_metadata.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_metadata.next());

                }
                stream_decl_metadata.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end decl_field

    public static class decl_field_initialization_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start decl_field_initialization
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:419:1: decl_field_initialization : EQUALS paren_chunk -> ^( EQUALS paren_chunk ) ;
    public final decl_field_initialization_return decl_field_initialization() throws RecognitionException {
        decl_field_initialization_return retval = new decl_field_initialization_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS59=null;
        paren_chunk_return paren_chunk60 = null;


        Object EQUALS59_tree=null;
        RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:420:2: ( EQUALS paren_chunk -> ^( EQUALS paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:420:4: EQUALS paren_chunk
            {
            EQUALS59=(Token)input.LT(1);
            match(input,EQUALS,FOLLOW_EQUALS_in_decl_field_initialization1182); if (failed) return retval;
            if ( backtracking==0 ) stream_EQUALS.add(EQUALS59);

            pushFollow(FOLLOW_paren_chunk_in_decl_field_initialization1184);
            paren_chunk60=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk60.getTree());

            // AST REWRITE
            // elements: EQUALS, paren_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 421:2: -> ^( EQUALS paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:421:5: ^( EQUALS paren_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_EQUALS.next(), root_1);

                adaptor.addChild(root_1, stream_paren_chunk.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end decl_field_initialization

    public static class template_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start template
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:424:1: template : template_key template_id ( SEMICOLON )? ( template_slot )+ END ( SEMICOLON )? -> ^( template_key template_id ( template_slot )+ END ) ;
    public final template_return template() throws RecognitionException {
        template_return retval = new template_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON63=null;
        Token END65=null;
        Token SEMICOLON66=null;
        template_key_return template_key61 = null;

        template_id_return template_id62 = null;

        template_slot_return template_slot64 = null;


        Object SEMICOLON63_tree=null;
        Object END65_tree=null;
        Object SEMICOLON66_tree=null;
        RewriteRuleTokenStream stream_END=new RewriteRuleTokenStream(adaptor,"token END");
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_template_id=new RewriteRuleSubtreeStream(adaptor,"rule template_id");
        RewriteRuleSubtreeStream stream_template_slot=new RewriteRuleSubtreeStream(adaptor,"rule template_slot");
        RewriteRuleSubtreeStream stream_template_key=new RewriteRuleSubtreeStream(adaptor,"rule template_key");
         pushParaphrases(DroolsParaphareseTypes.TEMPLATE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:427:2: ( template_key template_id ( SEMICOLON )? ( template_slot )+ END ( SEMICOLON )? -> ^( template_key template_id ( template_slot )+ END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:427:4: template_key template_id ( SEMICOLON )? ( template_slot )+ END ( SEMICOLON )?
            {
            pushFollow(FOLLOW_template_key_in_template1215);
            template_key61=template_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_template_key.add(template_key61.getTree());
            pushFollow(FOLLOW_template_id_in_template1217);
            template_id62=template_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_template_id.add(template_id62.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:427:29: ( SEMICOLON )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==SEMICOLON) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:427:29: SEMICOLON
                    {
                    SEMICOLON63=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1219); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON63);


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:428:3: ( template_slot )+
            int cnt24=0;
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==ID) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:428:3: template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template1224);
            	    template_slot64=template_slot();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_template_slot.add(template_slot64.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt24 >= 1 ) break loop24;
            	    if (backtracking>0) {failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(24, input);
                        throw eee;
                }
                cnt24++;
            } while (true);

            END65=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_template1229); if (failed) return retval;
            if ( backtracking==0 ) stream_END.add(END65);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:429:7: ( SEMICOLON )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==SEMICOLON) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:429:7: SEMICOLON
                    {
                    SEMICOLON66=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1231); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON66);


                    }
                    break;

            }


            // AST REWRITE
            // elements: END, template_id, template_key, template_slot
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 430:3: -> ^( template_key template_id ( template_slot )+ END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:430:6: ^( template_key template_id ( template_slot )+ END )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_template_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_template_id.next());
                if ( !(stream_template_slot.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_template_slot.hasNext() ) {
                    adaptor.addChild(root_1, stream_template_slot.next());

                }
                stream_template_slot.reset();
                adaptor.addChild(root_1, stream_END.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end template

    public static class template_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start template_id
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:433:1: template_id : (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] );
    public final template_id_return template_id() throws RecognitionException {
        template_id_return retval = new template_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:434:2: (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==ID) ) {
                alt26=1;
            }
            else if ( (LA26_0==STRING) ) {
                alt26=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("433:1: template_id : (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] );", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:434:5: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_template_id1261); if (failed) return retval;
                    if ( backtracking==0 ) stream_ID.add(id);

                    if ( backtracking==0 ) {
                      	setParaphrasesValue(DroolsParaphareseTypes.TEMPLATE, id.getText());	
                    }

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 435:70: -> VT_TEMPLATE_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_TEMPLATE_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:436:5: id= STRING
                    {
                    id=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_template_id1277); if (failed) return retval;
                    if ( backtracking==0 ) stream_STRING.add(id);

                    if ( backtracking==0 ) {
                      	setParaphrasesValue(DroolsParaphareseTypes.TEMPLATE, id.getText());	
                    }

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 437:70: -> VT_TEMPLATE_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_TEMPLATE_ID, id));

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end template_id

    public static class template_slot_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start template_slot
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:1: template_slot : data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) ;
    public final template_slot_return template_slot() throws RecognitionException {
        template_slot_return retval = new template_slot_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON69=null;
        data_type_return data_type67 = null;

        slot_id_return slot_id68 = null;


        Object SEMICOLON69_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_slot_id=new RewriteRuleSubtreeStream(adaptor,"rule slot_id");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:441:2: ( data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:441:5: data_type slot_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_data_type_in_template_slot1297);
            data_type67=data_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_data_type.add(data_type67.getTree());
            pushFollow(FOLLOW_slot_id_in_template_slot1299);
            slot_id68=slot_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_slot_id.add(slot_id68.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:441:23: ( SEMICOLON )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==SEMICOLON) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:441:23: SEMICOLON
                    {
                    SEMICOLON69=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template_slot1301); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON69);


                    }
                    break;

            }


            // AST REWRITE
            // elements: slot_id, data_type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 442:3: -> ^( VT_SLOT data_type slot_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:442:6: ^( VT_SLOT data_type slot_id )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_SLOT, "VT_SLOT"), root_1);

                adaptor.addChild(root_1, stream_data_type.next());
                adaptor.addChild(root_1, stream_slot_id.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end template_slot

    public static class slot_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start slot_id
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:445:1: slot_id : id= ID -> VT_SLOT_ID[$id] ;
    public final slot_id_return slot_id() throws RecognitionException {
        slot_id_return retval = new slot_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:445:9: (id= ID -> VT_SLOT_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:445:11: id= ID
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_slot_id1326); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 446:3: -> VT_SLOT_ID[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VT_SLOT_ID, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end slot_id

    public static class rule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rule
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:449:1: rule : rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk ) ;
    public final rule_return rule() throws RecognitionException {
        rule_return retval = new rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        rule_key_return rule_key70 = null;

        rule_id_return rule_id71 = null;

        rule_attributes_return rule_attributes72 = null;

        when_part_return when_part73 = null;

        rhs_chunk_return rhs_chunk74 = null;


        RewriteRuleSubtreeStream stream_rule_key=new RewriteRuleSubtreeStream(adaptor,"rule rule_key");
        RewriteRuleSubtreeStream stream_rule_id=new RewriteRuleSubtreeStream(adaptor,"rule rule_id");
        RewriteRuleSubtreeStream stream_when_part=new RewriteRuleSubtreeStream(adaptor,"rule when_part");
        RewriteRuleSubtreeStream stream_rule_attributes=new RewriteRuleSubtreeStream(adaptor,"rule rule_attributes");
        RewriteRuleSubtreeStream stream_rhs_chunk=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk");
         pushParaphrases(DroolsParaphareseTypes.RULE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:2: ( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:4: rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk
            {
            pushFollow(FOLLOW_rule_key_in_rule1355);
            rule_key70=rule_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rule_key.add(rule_key70.getTree());
            pushFollow(FOLLOW_rule_id_in_rule1357);
            rule_id71=rule_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rule_id.add(rule_id71.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:21: ( rule_attributes )?
            int alt28=2;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:21: rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1359);
                    rule_attributes72=rule_attributes();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_rule_attributes.add(rule_attributes72.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:38: ( when_part )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:38: when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule1362);
                    when_part73=when_part();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_when_part.add(when_part73.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1365);
            rhs_chunk74=rhs_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rhs_chunk.add(rhs_chunk74.getTree());

            // AST REWRITE
            // elements: rule_attributes, rhs_chunk, rule_key, when_part, rule_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 453:3: -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:453:6: ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_rule_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rule_id.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:453:25: ( rule_attributes )?
                if ( stream_rule_attributes.hasNext() ) {
                    adaptor.addChild(root_1, stream_rule_attributes.next());

                }
                stream_rule_attributes.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:453:42: ( when_part )?
                if ( stream_when_part.hasNext() ) {
                    adaptor.addChild(root_1, stream_when_part.next());

                }
                stream_when_part.reset();
                adaptor.addChild(root_1, stream_rhs_chunk.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end rule

    public static class when_part_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start when_part
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:456:1: when_part : when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block ;
    public final when_part_return when_part() throws RecognitionException {
        when_part_return retval = new when_part_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON76=null;
        when_key_return when_key75 = null;

        normal_lhs_block_return normal_lhs_block77 = null;


        Object COLON76_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        RewriteRuleSubtreeStream stream_when_key=new RewriteRuleSubtreeStream(adaptor,"rule when_key");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:457:2: ( when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:457:4: when_key ( COLON )? normal_lhs_block
            {
            pushFollow(FOLLOW_when_key_in_when_part1394);
            when_key75=when_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_when_key.add(when_key75.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:457:13: ( COLON )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==COLON) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:457:13: COLON
                    {
                    COLON76=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_when_part1396); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON76);


                    }
                    break;

            }

            pushFollow(FOLLOW_normal_lhs_block_in_when_part1399);
            normal_lhs_block77=normal_lhs_block();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block77.getTree());

            // AST REWRITE
            // elements: normal_lhs_block, when_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 458:2: -> when_key normal_lhs_block
            {
                adaptor.addChild(root_0, stream_when_key.next());
                adaptor.addChild(root_0, stream_normal_lhs_block.next());

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end when_part

    public static class rule_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rule_id
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:1: rule_id : (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] );
    public final rule_id_return rule_id() throws RecognitionException {
        rule_id_return retval = new rule_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:462:2: (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==ID) ) {
                alt31=1;
            }
            else if ( (LA31_0==STRING) ) {
                alt31=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("461:1: rule_id : (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] );", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:462:5: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_rule_id1420); if (failed) return retval;
                    if ( backtracking==0 ) stream_ID.add(id);

                    if ( backtracking==0 ) {
                      	setParaphrasesValue(DroolsParaphareseTypes.RULE, id.getText());	
                    }

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 463:66: -> VT_RULE_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_RULE_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:464:5: id= STRING
                    {
                    id=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_rule_id1436); if (failed) return retval;
                    if ( backtracking==0 ) stream_STRING.add(id);

                    if ( backtracking==0 ) {
                      	setParaphrasesValue(DroolsParaphareseTypes.RULE, id.getText());	
                    }

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 465:66: -> VT_RULE_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_RULE_ID, id));

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end rule_id

    public static class rule_attributes_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rule_attributes
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:1: rule_attributes : ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) ;
    public final rule_attributes_return rule_attributes() throws RecognitionException {
        rule_attributes_return retval = new rule_attributes_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON79=null;
        Token COMMA81=null;
        rule_attribute_return attr = null;

        attributes_key_return attributes_key78 = null;

        rule_attribute_return rule_attribute80 = null;


        Object COLON79_tree=null;
        Object COMMA81_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_attributes_key=new RewriteRuleSubtreeStream(adaptor,"rule attributes_key");
        RewriteRuleSubtreeStream stream_rule_attribute=new RewriteRuleSubtreeStream(adaptor,"rule rule_attribute");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:2: ( ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:4: ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:4: ( attributes_key COLON )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {
                int LA32_1 = input.LA(2);

                if ( (LA32_1==COLON) && ((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))) {
                    alt32=1;
                }
            }
            switch (alt32) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:6: attributes_key COLON
                    {
                    pushFollow(FOLLOW_attributes_key_in_rule_attributes1457);
                    attributes_key78=attributes_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_attributes_key.add(attributes_key78.getTree());
                    COLON79=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_rule_attributes1459); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON79);


                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1464);
            rule_attribute80=rule_attribute();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rule_attribute.add(rule_attribute80.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:45: ( ( COMMA )? attr= rule_attribute )*
            loop34:
            do {
                int alt34=2;
                alt34 = dfa34.predict(input);
                switch (alt34) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:47: ( COMMA )? attr= rule_attribute
            	    {
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:47: ( COMMA )?
            	    int alt33=2;
            	    int LA33_0 = input.LA(1);

            	    if ( (LA33_0==COMMA) ) {
            	        alt33=1;
            	    }
            	    switch (alt33) {
            	        case 1 :
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:47: COMMA
            	            {
            	            COMMA81=(Token)input.LT(1);
            	            match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1468); if (failed) return retval;
            	            if ( backtracking==0 ) stream_COMMA.add(COMMA81);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1473);
            	    attr=rule_attribute();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_rule_attribute.add(attr.getTree());

            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);


            // AST REWRITE
            // elements: attributes_key, rule_attribute
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 470:3: -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:470:6: ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_RULE_ATTRIBUTES, "VT_RULE_ATTRIBUTES"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:470:27: ( attributes_key )?
                if ( stream_attributes_key.hasNext() ) {
                    adaptor.addChild(root_1, stream_attributes_key.next());

                }
                stream_attributes_key.reset();
                if ( !(stream_rule_attribute.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_rule_attribute.hasNext() ) {
                    adaptor.addChild(root_1, stream_rule_attribute.next());

                }
                stream_rule_attribute.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end rule_attributes

    public static class rule_attribute_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rule_attribute
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:473:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );
    public final rule_attribute_return rule_attribute() throws RecognitionException {
        rule_attribute_return retval = new rule_attribute_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        salience_return salience82 = null;

        no_loop_return no_loop83 = null;

        agenda_group_return agenda_group84 = null;

        duration_return duration85 = null;

        activation_group_return activation_group86 = null;

        auto_focus_return auto_focus87 = null;

        date_effective_return date_effective88 = null;

        date_expires_return date_expires89 = null;

        enabled_return enabled90 = null;

        ruleflow_group_return ruleflow_group91 = null;

        lock_on_active_return lock_on_active92 = null;

        dialect_return dialect93 = null;



         pushParaphrases(DroolsParaphareseTypes.RULE_ATTRIBUTE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:476:2: ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect )
            int alt35=12;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                int LA35_1 = input.LA(2);

                if ( (LA35_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                    int LA35_2 = input.LA(3);

                    if ( (LA35_2==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                        int LA35_7 = input.LA(4);

                        if ( (LA35_7==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))) {
                            alt35=11;
                        }
                        else if ( (LA35_7==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                            int LA35_10 = input.LA(5);

                            if ( ((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                                alt35=3;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                                alt35=5;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))) ) {
                                alt35=7;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))) ) {
                                alt35=8;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                                alt35=10;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("473:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 10, input);

                                throw nvae;
                            }
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))) ) {
                            alt35=2;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))) ) {
                            alt35=6;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("473:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 7, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("473:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA35_1==STRING) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {
                    alt35=12;
                }
                else if ( (LA35_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {
                    int LA35_4 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {
                        alt35=1;
                    }
                    else if ( ((validateIdentifierKey(DroolsSoftKeywords.DURATION))) ) {
                        alt35=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("473:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 4, input);

                        throw nvae;
                    }
                }
                else if ( (LA35_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {
                    alt35=9;
                }
                else if ( (LA35_1==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {
                    alt35=1;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("473:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("473:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:476:4: salience
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_salience_in_rule_attribute1512);
                    salience82=salience();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, salience82.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:477:4: no_loop
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_no_loop_in_rule_attribute1518);
                    no_loop83=no_loop();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, no_loop83.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:478:4: agenda_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1525);
                    agenda_group84=agenda_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, agenda_group84.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:479:4: duration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_duration_in_rule_attribute1532);
                    duration85=duration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, duration85.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:480:4: activation_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_activation_group_in_rule_attribute1539);
                    activation_group86=activation_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, activation_group86.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:481:4: auto_focus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1545);
                    auto_focus87=auto_focus();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, auto_focus87.getTree());

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:4: date_effective
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_effective_in_rule_attribute1551);
                    date_effective88=date_effective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, date_effective88.getTree());

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:483:4: date_expires
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_expires_in_rule_attribute1557);
                    date_expires89=date_expires();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, date_expires89.getTree());

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:4: enabled
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enabled_in_rule_attribute1563);
                    enabled90=enabled();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, enabled90.getTree());

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:4: ruleflow_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1569);
                    ruleflow_group91=ruleflow_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, ruleflow_group91.getTree());

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:4: lock_on_active
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1575);
                    lock_on_active92=lock_on_active();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lock_on_active92.getTree());

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:4: dialect
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_dialect_in_rule_attribute1580);
                    dialect93=dialect();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, dialect93.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end rule_attribute

    public static class date_effective_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start date_effective
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:490:1: date_effective : date_effective_key STRING ;
    public final date_effective_return date_effective() throws RecognitionException {
        date_effective_return retval = new date_effective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING95=null;
        date_effective_key_return date_effective_key94 = null;


        Object STRING95_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:491:2: ( date_effective_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:491:4: date_effective_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_effective_key_in_date_effective1592);
            date_effective_key94=date_effective_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_effective_key94.getTree(), root_0);
            STRING95=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1595); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING95_tree = (Object)adaptor.create(STRING95);
            adaptor.addChild(root_0, STRING95_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end date_effective

    public static class date_expires_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start date_expires
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:1: date_expires : date_expires_key STRING ;
    public final date_expires_return date_expires() throws RecognitionException {
        date_expires_return retval = new date_expires_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING97=null;
        date_expires_key_return date_expires_key96 = null;


        Object STRING97_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:2: ( date_expires_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:4: date_expires_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_expires_key_in_date_expires1606);
            date_expires_key96=date_expires_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_expires_key96.getTree(), root_0);
            STRING97=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1609); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING97_tree = (Object)adaptor.create(STRING97);
            adaptor.addChild(root_0, STRING97_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end date_expires

    public static class enabled_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start enabled
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:498:1: enabled : enabled_key BOOL ;
    public final enabled_return enabled() throws RecognitionException {
        enabled_return retval = new enabled_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL99=null;
        enabled_key_return enabled_key98 = null;


        Object BOOL99_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:499:2: ( enabled_key BOOL )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:499:4: enabled_key BOOL
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_enabled_key_in_enabled1623);
            enabled_key98=enabled_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(enabled_key98.getTree(), root_0);
            BOOL99=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1626); if (failed) return retval;
            if ( backtracking==0 ) {
            BOOL99_tree = (Object)adaptor.create(BOOL99);
            adaptor.addChild(root_0, BOOL99_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end enabled

    public static class salience_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start salience
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:502:1: salience : salience_key ( INT | paren_chunk ) ;
    public final salience_return salience() throws RecognitionException {
        salience_return retval = new salience_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT101=null;
        salience_key_return salience_key100 = null;

        paren_chunk_return paren_chunk102 = null;


        Object INT101_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:503:2: ( salience_key ( INT | paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:503:4: salience_key ( INT | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_salience_key_in_salience1638);
            salience_key100=salience_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(salience_key100.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:504:3: ( INT | paren_chunk )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==INT) ) {
                alt36=1;
            }
            else if ( (LA36_0==LEFT_PAREN) ) {
                alt36=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("504:3: ( INT | paren_chunk )", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:504:5: INT
                    {
                    INT101=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_salience1645); if (failed) return retval;
                    if ( backtracking==0 ) {
                    INT101_tree = (Object)adaptor.create(INT101);
                    adaptor.addChild(root_0, INT101_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:505:5: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1654);
                    paren_chunk102=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk102.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end salience

    public static class no_loop_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start no_loop
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:509:1: no_loop : no_loop_key ( BOOL )? ;
    public final no_loop_return no_loop() throws RecognitionException {
        no_loop_return retval = new no_loop_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL104=null;
        no_loop_key_return no_loop_key103 = null;


        Object BOOL104_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:510:2: ( no_loop_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:510:4: no_loop_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_no_loop_key_in_no_loop1670);
            no_loop_key103=no_loop_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(no_loop_key103.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:510:17: ( BOOL )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==BOOL) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:510:17: BOOL
                    {
                    BOOL104=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1673); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL104_tree = (Object)adaptor.create(BOOL104);
                    adaptor.addChild(root_0, BOOL104_tree);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end no_loop

    public static class auto_focus_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start auto_focus
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:1: auto_focus : auto_focus_key ( BOOL )? ;
    public final auto_focus_return auto_focus() throws RecognitionException {
        auto_focus_return retval = new auto_focus_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL106=null;
        auto_focus_key_return auto_focus_key105 = null;


        Object BOOL106_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:514:2: ( auto_focus_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:514:4: auto_focus_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_auto_focus_key_in_auto_focus1685);
            auto_focus_key105=auto_focus_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(auto_focus_key105.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:514:20: ( BOOL )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==BOOL) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:514:20: BOOL
                    {
                    BOOL106=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1688); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL106_tree = (Object)adaptor.create(BOOL106);
                    adaptor.addChild(root_0, BOOL106_tree);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end auto_focus

    public static class activation_group_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start activation_group
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:517:1: activation_group : activation_group_key STRING ;
    public final activation_group_return activation_group() throws RecognitionException {
        activation_group_return retval = new activation_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING108=null;
        activation_group_key_return activation_group_key107 = null;


        Object STRING108_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:518:2: ( activation_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:518:4: activation_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_activation_group_key_in_activation_group1702);
            activation_group_key107=activation_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(activation_group_key107.getTree(), root_0);
            STRING108=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1705); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING108_tree = (Object)adaptor.create(STRING108);
            adaptor.addChild(root_0, STRING108_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end activation_group

    public static class ruleflow_group_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start ruleflow_group
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:1: ruleflow_group : ruleflow_group_key STRING ;
    public final ruleflow_group_return ruleflow_group() throws RecognitionException {
        ruleflow_group_return retval = new ruleflow_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING110=null;
        ruleflow_group_key_return ruleflow_group_key109 = null;


        Object STRING110_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:522:2: ( ruleflow_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:522:4: ruleflow_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_ruleflow_group_key_in_ruleflow_group1716);
            ruleflow_group_key109=ruleflow_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(ruleflow_group_key109.getTree(), root_0);
            STRING110=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1719); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING110_tree = (Object)adaptor.create(STRING110);
            adaptor.addChild(root_0, STRING110_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end ruleflow_group

    public static class agenda_group_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start agenda_group
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:525:1: agenda_group : agenda_group_key STRING ;
    public final agenda_group_return agenda_group() throws RecognitionException {
        agenda_group_return retval = new agenda_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING112=null;
        agenda_group_key_return agenda_group_key111 = null;


        Object STRING112_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:526:2: ( agenda_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:526:4: agenda_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_agenda_group_key_in_agenda_group1730);
            agenda_group_key111=agenda_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(agenda_group_key111.getTree(), root_0);
            STRING112=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1733); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING112_tree = (Object)adaptor.create(STRING112);
            adaptor.addChild(root_0, STRING112_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end agenda_group

    public static class duration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start duration
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:529:1: duration : duration_key INT ;
    public final duration_return duration() throws RecognitionException {
        duration_return retval = new duration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT114=null;
        duration_key_return duration_key113 = null;


        Object INT114_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:530:2: ( duration_key INT )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:530:4: duration_key INT
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_duration_key_in_duration1744);
            duration_key113=duration_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(duration_key113.getTree(), root_0);
            INT114=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1747); if (failed) return retval;
            if ( backtracking==0 ) {
            INT114_tree = (Object)adaptor.create(INT114);
            adaptor.addChild(root_0, INT114_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end duration

    public static class dialect_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start dialect
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:533:1: dialect : dialect_key STRING ;
    public final dialect_return dialect() throws RecognitionException {
        dialect_return retval = new dialect_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING116=null;
        dialect_key_return dialect_key115 = null;


        Object STRING116_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:534:2: ( dialect_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:534:4: dialect_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_dialect_key_in_dialect1761);
            dialect_key115=dialect_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(dialect_key115.getTree(), root_0);
            STRING116=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1764); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING116_tree = (Object)adaptor.create(STRING116);
            adaptor.addChild(root_0, STRING116_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end dialect

    public static class lock_on_active_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lock_on_active
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:537:1: lock_on_active : lock_on_active_key ( BOOL )? ;
    public final lock_on_active_return lock_on_active() throws RecognitionException {
        lock_on_active_return retval = new lock_on_active_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL118=null;
        lock_on_active_key_return lock_on_active_key117 = null;


        Object BOOL118_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:538:2: ( lock_on_active_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:538:4: lock_on_active_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lock_on_active_key_in_lock_on_active1782);
            lock_on_active_key117=lock_on_active_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(lock_on_active_key117.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:538:24: ( BOOL )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==BOOL) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:538:24: BOOL
                    {
                    BOOL118=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1785); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL118_tree = (Object)adaptor.create(BOOL118);
                    adaptor.addChild(root_0, BOOL118_tree);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end lock_on_active

    public static class normal_lhs_block_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start normal_lhs_block
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:541:1: normal_lhs_block : ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) ;
    public final normal_lhs_block_return normal_lhs_block() throws RecognitionException {
        normal_lhs_block_return retval = new normal_lhs_block_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        lhs_return lhs119 = null;


        RewriteRuleSubtreeStream stream_lhs=new RewriteRuleSubtreeStream(adaptor,"rule lhs");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:542:2: ( ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:542:4: ( lhs )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:542:4: ( lhs )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==ID||LA40_0==LEFT_PAREN) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:542:4: lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1797);
            	    lhs119=lhs();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_lhs.add(lhs119.getTree());

            	    }
            	    break;

            	default :
            	    break loop40;
                }
            } while (true);


            // AST REWRITE
            // elements: lhs
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 543:2: -> ^( VT_AND_IMPLICIT ( lhs )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:5: ^( VT_AND_IMPLICIT ( lhs )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_AND_IMPLICIT, "VT_AND_IMPLICIT"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:23: ( lhs )*
                while ( stream_lhs.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs.next());

                }
                stream_lhs.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end normal_lhs_block

    public static class lhs_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:546:1: lhs : lhs_or ;
    public final lhs_return lhs() throws RecognitionException {
        lhs_return retval = new lhs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        lhs_or_return lhs_or120 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:546:5: ( lhs_or )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:546:7: lhs_or
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_or_in_lhs1818);
            lhs_or120=lhs_or();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, lhs_or120.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end lhs

    public static class lhs_or_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_or
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:549:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );
    public final lhs_or_return lhs_or() throws RecognitionException {
        lhs_or_return retval = new lhs_or_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        Token LEFT_PAREN121=null;
        Token RIGHT_PAREN123=null;
        or_key_return or = null;

        or_key_return value = null;

        lhs_and_return lhs_and122 = null;

        lhs_and_return lhs_and124 = null;

        lhs_and_return lhs_and125 = null;


        Object pipe_tree=null;
        Object LEFT_PAREN121_tree=null;
        Object RIGHT_PAREN123_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");
        RewriteRuleSubtreeStream stream_lhs_and=new RewriteRuleSubtreeStream(adaptor,"rule lhs_and");

        	Token orToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:3: ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==LEFT_PAREN) ) {
                int LA44_1 = input.LA(2);

                if ( (LA44_1==ID) ) {
                    switch ( input.LA(3) ) {
                    case DOT:
                    case COLON:
                    case LEFT_SQUARE:
                        {
                        alt44=2;
                        }
                        break;
                    case LEFT_PAREN:
                        {
                        int LA44_4 = input.LA(4);

                        if ( (synpred1()) ) {
                            alt44=1;
                        }
                        else if ( (true) ) {
                            alt44=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("549:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 44, 4, input);

                            throw nvae;
                        }
                        }
                        break;
                    case ID:
                        {
                        int LA44_5 = input.LA(4);

                        if ( (synpred1()) ) {
                            alt44=1;
                        }
                        else if ( (true) ) {
                            alt44=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("549:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 44, 5, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("549:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 44, 3, input);

                        throw nvae;
                    }

                }
                else if ( (LA44_1==LEFT_PAREN) ) {
                    alt44=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("549:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 44, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA44_0==ID) ) {
                alt44=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("549:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:5: ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN
                    {
                    LEFT_PAREN121=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or1839); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN121);

                    pushFollow(FOLLOW_or_key_in_lhs_or1843);
                    or=or_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_or_key.add(or.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:48: ( lhs_and )+
                    int cnt41=0;
                    loop41:
                    do {
                        int alt41=2;
                        int LA41_0 = input.LA(1);

                        if ( (LA41_0==ID||LA41_0==LEFT_PAREN) ) {
                            alt41=1;
                        }


                        switch (alt41) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:48: lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1845);
                    	    lhs_and122=lhs_and();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_and.add(lhs_and122.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt41 >= 1 ) break loop41;
                    	    if (backtracking>0) {failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(41, input);
                                throw eee;
                        }
                        cnt41++;
                    } while (true);

                    RIGHT_PAREN123=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or1848); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN123);


                    // AST REWRITE
                    // elements: RIGHT_PAREN, lhs_and
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 553:3: -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:553:6: ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_OR_PREFIX, ((Token)or.start)), root_1);

                        if ( !(stream_lhs_and.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_lhs_and.hasNext() ) {
                            adaptor.addChild(root_1, stream_lhs_and.next());

                        }
                        stream_lhs_and.reset();
                        adaptor.addChild(root_1, stream_RIGHT_PAREN.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:554:4: ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:554:4: ( lhs_and -> lhs_and )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:554:5: lhs_and
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or1869);
                    lhs_and124=lhs_and();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_and.add(lhs_and124.getTree());

                    // AST REWRITE
                    // elements: lhs_and
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 554:13: -> lhs_and
                    {
                        adaptor.addChild(root_0, stream_lhs_and.next());

                    }

                    }

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:3: ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==ID) ) {
                            int LA43_2 = input.LA(2);

                            if ( ((synpred2()&&(validateIdentifierKey(DroolsSoftKeywords.OR)))) ) {
                                alt43=1;
                            }


                        }
                        else if ( (LA43_0==DOUBLE_PIPE) ) {
                            int LA43_3 = input.LA(2);

                            if ( (synpred2()) ) {
                                alt43=1;
                            }


                        }


                        switch (alt43) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:5: ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and
                    	    {
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:28: (value= or_key | pipe= DOUBLE_PIPE )
                    	    int alt42=2;
                    	    int LA42_0 = input.LA(1);

                    	    if ( (LA42_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
                    	        alt42=1;
                    	    }
                    	    else if ( (LA42_0==DOUBLE_PIPE) ) {
                    	        alt42=2;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("555:28: (value= or_key | pipe= DOUBLE_PIPE )", 42, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt42) {
                    	        case 1 :
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:29: value= or_key
                    	            {
                    	            pushFollow(FOLLOW_or_key_in_lhs_or1891);
                    	            value=or_key();
                    	            _fsp--;
                    	            if (failed) return retval;
                    	            if ( backtracking==0 ) stream_or_key.add(value.getTree());
                    	            if ( backtracking==0 ) {
                    	              orToken = ((Token)value.start);
                    	            }

                    	            }
                    	            break;
                    	        case 2 :
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:69: pipe= DOUBLE_PIPE
                    	            {
                    	            pipe=(Token)input.LT(1);
                    	            match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_lhs_or1898); if (failed) return retval;
                    	            if ( backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

                    	            if ( backtracking==0 ) {
                    	              orToken = pipe;
                    	            }

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1903);
                    	    lhs_and125=lhs_and();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_and.add(lhs_and125.getTree());

                    	    // AST REWRITE
                    	    // elements: lhs_and, lhs_or
                    	    // token labels: 
                    	    // rule labels: retval
                    	    // token list labels: 
                    	    // rule list labels: 
                    	    if ( backtracking==0 ) {
                    	    retval.tree = root_0;
                    	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    	    root_0 = (Object)adaptor.nil();
                    	    // 556:3: -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
                    	    {
                    	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:556:6: ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
                    	        {
                    	        Object root_1 = (Object)adaptor.nil();
                    	        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_OR_INFIX, orToken), root_1);

                    	        adaptor.addChild(root_1, stream_retval.next());
                    	        adaptor.addChild(root_1, stream_lhs_and.next());

                    	        adaptor.addChild(root_0, root_1);
                    	        }

                    	    }

                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop43;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end lhs_or

    public static class lhs_and_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_and
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:559:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );
    public final lhs_and_return lhs_and() throws RecognitionException {
        lhs_and_return retval = new lhs_and_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token amper=null;
        Token LEFT_PAREN126=null;
        Token RIGHT_PAREN128=null;
        and_key_return and = null;

        and_key_return value = null;

        lhs_unary_return lhs_unary127 = null;

        lhs_unary_return lhs_unary129 = null;

        lhs_unary_return lhs_unary130 = null;


        Object amper_tree=null;
        Object LEFT_PAREN126_tree=null;
        Object RIGHT_PAREN128_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_AMPER=new RewriteRuleTokenStream(adaptor,"token DOUBLE_AMPER");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_and_key=new RewriteRuleSubtreeStream(adaptor,"rule and_key");
        RewriteRuleSubtreeStream stream_lhs_unary=new RewriteRuleSubtreeStream(adaptor,"rule lhs_unary");

        	Token andToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:3: ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* )
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==LEFT_PAREN) ) {
                int LA48_1 = input.LA(2);

                if ( (LA48_1==LEFT_PAREN) ) {
                    alt48=2;
                }
                else if ( (LA48_1==ID) ) {
                    switch ( input.LA(3) ) {
                    case DOT:
                    case COLON:
                    case LEFT_SQUARE:
                        {
                        alt48=2;
                        }
                        break;
                    case LEFT_PAREN:
                        {
                        switch ( input.LA(4) ) {
                        case ID:
                            {
                            int LA48_6 = input.LA(5);

                            if ( (synpred3()) ) {
                                alt48=1;
                            }
                            else if ( (true) ) {
                                alt48=2;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("559:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 6, input);

                                throw nvae;
                            }
                            }
                            break;
                        case LEFT_PAREN:
                            {
                            int LA48_7 = input.LA(5);

                            if ( (synpred3()) ) {
                                alt48=1;
                            }
                            else if ( (true) ) {
                                alt48=2;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("559:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 7, input);

                                throw nvae;
                            }
                            }
                            break;
                        case VT_COMPILATION_UNIT:
                        case VT_FUNCTION_IMPORT:
                        case VT_FACT:
                        case VT_CONSTRAINTS:
                        case VT_LABEL:
                        case VT_QUERY_ID:
                        case VT_TEMPLATE_ID:
                        case VT_TYPE_DECLARE_ID:
                        case VT_RULE_ID:
                        case VT_ENTRYPOINT_ID:
                        case VT_SLOT_ID:
                        case VT_SLOT:
                        case VT_RULE_ATTRIBUTES:
                        case VT_RHS_CHUNK:
                        case VT_CURLY_CHUNK:
                        case VT_SQUARE_CHUNK:
                        case VT_PAREN_CHUNK:
                        case VT_BEHAVIOR:
                        case VT_AND_IMPLICIT:
                        case VT_AND_PREFIX:
                        case VT_OR_PREFIX:
                        case VT_AND_INFIX:
                        case VT_OR_INFIX:
                        case VT_ACCUMULATE_INIT_CLAUSE:
                        case VT_ACCUMULATE_ID_CLAUSE:
                        case VT_FROM_SOURCE:
                        case VT_EXPRESSION_CHAIN:
                        case VT_PATTERN:
                        case VT_FACT_BINDING:
                        case VT_FACT_OR:
                        case VT_BIND_FIELD:
                        case VT_FIELD:
                        case VT_ACCESSOR_PATH:
                        case VT_ACCESSOR_ELEMENT:
                        case VT_DATA_TYPE:
                        case VT_PATTERN_TYPE:
                        case VT_PACKAGE_ID:
                        case VT_IMPORT_ID:
                        case VT_GLOBAL_ID:
                        case VT_FUNCTION_ID:
                        case VT_PARAM_LIST:
                        case VK_DATE_EFFECTIVE:
                        case VK_DATE_EXPIRES:
                        case VK_LOCK_ON_ACTIVE:
                        case VK_NO_LOOP:
                        case VK_AUTO_FOCUS:
                        case VK_ACTIVATION_GROUP:
                        case VK_AGENDA_GROUP:
                        case VK_RULEFLOW_GROUP:
                        case VK_DURATION:
                        case VK_DIALECT:
                        case VK_SALIENCE:
                        case VK_ENABLED:
                        case VK_ATTRIBUTES:
                        case VK_WHEN:
                        case VK_RULE:
                        case VK_IMPORT:
                        case VK_PACKAGE:
                        case VK_TEMPLATE:
                        case VK_QUERY:
                        case VK_DECLARE:
                        case VK_FUNCTION:
                        case VK_GLOBAL:
                        case VK_EVAL:
                        case VK_CONTAINS:
                        case VK_MATCHES:
                        case VK_EXCLUDES:
                        case VK_SOUNDSLIKE:
                        case VK_MEMBEROF:
                        case VK_ENTRY_POINT:
                        case VK_NOT:
                        case VK_IN:
                        case VK_OR:
                        case VK_AND:
                        case VK_EXISTS:
                        case VK_FORALL:
                        case VK_FROM:
                        case VK_ACCUMULATE:
                        case VK_INIT:
                        case VK_ACTION:
                        case VK_REVERSE:
                        case VK_RESULT:
                        case VK_COLLECT:
                        case SEMICOLON:
                        case DOT:
                        case DOT_STAR:
                        case END:
                        case STRING:
                        case COMMA:
                        case RIGHT_PAREN:
                        case AT:
                        case COLON:
                        case EQUALS:
                        case BOOL:
                        case INT:
                        case DOUBLE_PIPE:
                        case DOUBLE_AMPER:
                        case OVER:
                        case ARROW:
                        case EQUAL:
                        case GREATER:
                        case GREATER_EQUAL:
                        case LESS:
                        case LESS_EQUAL:
                        case NOT_EQUAL:
                        case GRAVE_ACCENT:
                        case FLOAT:
                        case NULL:
                        case LEFT_SQUARE:
                        case RIGHT_SQUARE:
                        case THEN:
                        case LEFT_CURLY:
                        case RIGHT_CURLY:
                        case MISC:
                        case EOL:
                        case WS:
                        case EscapeSequence:
                        case HexDigit:
                        case UnicodeEscape:
                        case OctalEscape:
                        case SH_STYLE_SINGLE_LINE_COMMENT:
                        case C_STYLE_SINGLE_LINE_COMMENT:
                        case MULTI_LINE_COMMENT:
                            {
                            alt48=2;
                            }
                            break;
                        default:
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("559:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 4, input);

                            throw nvae;
                        }

                        }
                        break;
                    case ID:
                        {
                        int LA48_5 = input.LA(4);

                        if ( (synpred3()) ) {
                            alt48=1;
                        }
                        else if ( (true) ) {
                            alt48=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("559:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 5, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("559:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 3, input);

                        throw nvae;
                    }

                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("559:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA48_0==ID) ) {
                alt48=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("559:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:5: ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN
                    {
                    LEFT_PAREN126=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and1941); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN126);

                    pushFollow(FOLLOW_and_key_in_lhs_and1945);
                    and=and_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_and_key.add(and.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:51: ( lhs_unary )+
                    int cnt45=0;
                    loop45:
                    do {
                        int alt45=2;
                        int LA45_0 = input.LA(1);

                        if ( (LA45_0==ID||LA45_0==LEFT_PAREN) ) {
                            alt45=1;
                        }


                        switch (alt45) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:51: lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and1947);
                    	    lhs_unary127=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary127.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt45 >= 1 ) break loop45;
                    	    if (backtracking>0) {failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(45, input);
                                throw eee;
                        }
                        cnt45++;
                    } while (true);

                    RIGHT_PAREN128=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and1950); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN128);


                    // AST REWRITE
                    // elements: RIGHT_PAREN, lhs_unary
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 563:3: -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:563:6: ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_AND_PREFIX, ((Token)and.start)), root_1);

                        if ( !(stream_lhs_unary.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_lhs_unary.hasNext() ) {
                            adaptor.addChild(root_1, stream_lhs_unary.next());

                        }
                        stream_lhs_unary.reset();
                        adaptor.addChild(root_1, stream_RIGHT_PAREN.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:4: ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:4: ( lhs_unary -> lhs_unary )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:5: lhs_unary
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and1971);
                    lhs_unary129=lhs_unary();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary129.getTree());

                    // AST REWRITE
                    // elements: lhs_unary
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 564:15: -> lhs_unary
                    {
                        adaptor.addChild(root_0, stream_lhs_unary.next());

                    }

                    }

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:3: ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    loop47:
                    do {
                        int alt47=2;
                        int LA47_0 = input.LA(1);

                        if ( (LA47_0==ID) ) {
                            int LA47_2 = input.LA(2);

                            if ( ((synpred4()&&(validateIdentifierKey(DroolsSoftKeywords.AND)))) ) {
                                alt47=1;
                            }


                        }
                        else if ( (LA47_0==DOUBLE_AMPER) ) {
                            int LA47_3 = input.LA(2);

                            if ( (synpred4()) ) {
                                alt47=1;
                            }


                        }


                        switch (alt47) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:5: ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary
                    	    {
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:30: (value= and_key | amper= DOUBLE_AMPER )
                    	    int alt46=2;
                    	    int LA46_0 = input.LA(1);

                    	    if ( (LA46_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.AND)))) {
                    	        alt46=1;
                    	    }
                    	    else if ( (LA46_0==DOUBLE_AMPER) ) {
                    	        alt46=2;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("565:30: (value= and_key | amper= DOUBLE_AMPER )", 46, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt46) {
                    	        case 1 :
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:31: value= and_key
                    	            {
                    	            pushFollow(FOLLOW_and_key_in_lhs_and1993);
                    	            value=and_key();
                    	            _fsp--;
                    	            if (failed) return retval;
                    	            if ( backtracking==0 ) stream_and_key.add(value.getTree());
                    	            if ( backtracking==0 ) {
                    	              andToken = ((Token)value.start);
                    	            }

                    	            }
                    	            break;
                    	        case 2 :
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:73: amper= DOUBLE_AMPER
                    	            {
                    	            amper=(Token)input.LT(1);
                    	            match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_lhs_and2000); if (failed) return retval;
                    	            if ( backtracking==0 ) stream_DOUBLE_AMPER.add(amper);

                    	            if ( backtracking==0 ) {
                    	              andToken = amper;
                    	            }

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2005);
                    	    lhs_unary130=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary130.getTree());

                    	    // AST REWRITE
                    	    // elements: lhs_and, lhs_unary
                    	    // token labels: 
                    	    // rule labels: retval
                    	    // token list labels: 
                    	    // rule list labels: 
                    	    if ( backtracking==0 ) {
                    	    retval.tree = root_0;
                    	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    	    root_0 = (Object)adaptor.nil();
                    	    // 566:3: -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
                    	    {
                    	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:566:6: ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
                    	        {
                    	        Object root_1 = (Object)adaptor.nil();
                    	        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_AND_INFIX, andToken), root_1);

                    	        adaptor.addChild(root_1, stream_retval.next());
                    	        adaptor.addChild(root_1, stream_lhs_unary.next());

                    	        adaptor.addChild(root_0, root_1);
                    	        }

                    	    }

                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop47;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end lhs_and

    public static class lhs_unary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_unary
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:569:1: lhs_unary options {backtrack=true; } : ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? ;
    public final lhs_unary_return lhs_unary() throws RecognitionException {
        lhs_unary_return retval = new lhs_unary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN135=null;
        Token RIGHT_PAREN137=null;
        Token SEMICOLON139=null;
        lhs_exist_return lhs_exist131 = null;

        lhs_not_return lhs_not132 = null;

        lhs_eval_return lhs_eval133 = null;

        lhs_forall_return lhs_forall134 = null;

        lhs_or_return lhs_or136 = null;

        pattern_source_return pattern_source138 = null;


        Object LEFT_PAREN135_tree=null;
        Object RIGHT_PAREN137_tree=null;
        Object SEMICOLON139_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:2: ( ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:4: ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:4: ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )
            int alt49=6;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==ID) ) {
                int LA49_1 = input.LA(2);

                if ( ((validateIdentifierKey(DroolsSoftKeywords.EXISTS))) ) {
                    alt49=1;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                    alt49=2;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                    alt49=3;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.FORALL))) ) {
                    alt49=4;
                }
                else if ( (true) ) {
                    alt49=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("571:4: ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )", 49, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA49_0==LEFT_PAREN) ) {
                alt49=5;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("571:4: ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )", 49, 0, input);

                throw nvae;
            }
            switch (alt49) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:6: lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary2043);
                    lhs_exist131=lhs_exist();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_exist131.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:572:5: lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary2049);
                    lhs_not132=lhs_not();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_not132.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:5: lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2055);
                    lhs_eval133=lhs_eval();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_eval133.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:574:5: lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary2061);
                    lhs_forall134=lhs_forall();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_forall134.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:575:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN135=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2067); if (failed) return retval;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary2070);
                    lhs_or136=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_or136.getTree());
                    RIGHT_PAREN137=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2072); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN137_tree = (Object)adaptor.create(RIGHT_PAREN137);
                    adaptor.addChild(root_0, RIGHT_PAREN137_tree);
                    }

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:576:5: pattern_source
                    {
                    pushFollow(FOLLOW_pattern_source_in_lhs_unary2078);
                    pattern_source138=pattern_source();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, pattern_source138.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:578:3: ( ( SEMICOLON )=> SEMICOLON )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==SEMICOLON) ) {
                int LA50_1 = input.LA(2);

                if ( (synpred5()) ) {
                    alt50=1;
                }
            }
            switch (alt50) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:578:4: ( SEMICOLON )=> SEMICOLON
                    {
                    SEMICOLON139=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_lhs_unary2092); if (failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end lhs_unary

    public static class lhs_exist_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_exist
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:581:1: lhs_exist : exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final lhs_exist_return lhs_exist() throws RecognitionException {
        lhs_exist_return retval = new lhs_exist_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN142=null;
        Token RIGHT_PAREN144=null;
        exists_key_return exists_key140 = null;

        lhs_or_return lhs_or141 = null;

        lhs_or_return lhs_or143 = null;

        lhs_pattern_return lhs_pattern145 = null;


        Object LEFT_PAREN142_tree=null;
        Object RIGHT_PAREN144_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_exists_key=new RewriteRuleSubtreeStream(adaptor,"rule exists_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:582:2: ( exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:582:4: exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_exists_key_in_lhs_exist2106);
            exists_key140=exists_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_exists_key.add(exists_key140.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:10: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt51=3;
            alt51 = dfa51.predict(input);
            switch (alt51) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:12: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2130);
                    lhs_or141=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or141.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:584:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN142=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist2137); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN142);

                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2139);
                    lhs_or143=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or143.getTree());
                    RIGHT_PAREN144=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist2141); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN144);


                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:585:12: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist2154);
                    lhs_pattern145=lhs_pattern();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern145.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: lhs_or, RIGHT_PAREN, exists_key, lhs_pattern
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 587:10: -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:587:13: ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_exists_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:587:26: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.next());

                }
                stream_lhs_or.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:587:34: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.next());

                }
                stream_lhs_pattern.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:587:47: ( RIGHT_PAREN )?
                if ( stream_RIGHT_PAREN.hasNext() ) {
                    adaptor.addChild(root_1, stream_RIGHT_PAREN.next());

                }
                stream_RIGHT_PAREN.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end lhs_exist

    public static class lhs_not_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_not
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:590:1: lhs_not : not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final lhs_not_return lhs_not() throws RecognitionException {
        lhs_not_return retval = new lhs_not_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN148=null;
        Token RIGHT_PAREN150=null;
        not_key_return not_key146 = null;

        lhs_or_return lhs_or147 = null;

        lhs_or_return lhs_or149 = null;

        lhs_pattern_return lhs_pattern151 = null;


        Object LEFT_PAREN148_tree=null;
        Object RIGHT_PAREN150_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:590:9: ( not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:590:11: not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_not_key_in_lhs_not2200);
            not_key146=not_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_not_key.add(not_key146.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:591:3: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt52=3;
            alt52 = dfa52.predict(input);
            switch (alt52) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:591:5: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2217);
                    lhs_or147=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or147.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:592:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN148=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2224); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN148);

                    pushFollow(FOLLOW_lhs_or_in_lhs_not2226);
                    lhs_or149=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or149.getTree());
                    RIGHT_PAREN150=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2228); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN150);


                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:593:6: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2236);
                    lhs_pattern151=lhs_pattern();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern151.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: RIGHT_PAREN, lhs_pattern, not_key, lhs_or
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 594:10: -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:13: ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:23: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.next());

                }
                stream_lhs_or.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:31: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.next());

                }
                stream_lhs_pattern.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:44: ( RIGHT_PAREN )?
                if ( stream_RIGHT_PAREN.hasNext() ) {
                    adaptor.addChild(root_1, stream_RIGHT_PAREN.next());

                }
                stream_RIGHT_PAREN.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end lhs_not

    public static class lhs_eval_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_eval
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:597:1: lhs_eval : ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) ;
    public final lhs_eval_return lhs_eval() throws RecognitionException {
        lhs_eval_return retval = new lhs_eval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        eval_key_return ev = null;

        paren_chunk_return pc = null;


        RewriteRuleSubtreeStream stream_eval_key=new RewriteRuleSubtreeStream(adaptor,"rule eval_key");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:598:2: (ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:598:4: ev= eval_key pc= paren_chunk
            {
            pushFollow(FOLLOW_eval_key_in_lhs_eval2275);
            ev=eval_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_eval_key.add(ev.getTree());
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2279);
            pc=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc.getTree());
            if ( backtracking==0 ) {
              	String body = safeSubstring( input.toString(pc.start,pc.stop), 1, input.toString(pc.start,pc.stop).length()-1 );
              		checkTrailingSemicolon( body, ((Token)ev.start) );	
            }

            // AST REWRITE
            // elements: paren_chunk, eval_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 601:3: -> ^( eval_key paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:601:6: ^( eval_key paren_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_eval_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_paren_chunk.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end lhs_eval

    public static class lhs_forall_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_forall
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:604:1: lhs_forall : forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN ) ;
    public final lhs_forall_return lhs_forall() throws RecognitionException {
        lhs_forall_return retval = new lhs_forall_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN153=null;
        Token RIGHT_PAREN155=null;
        forall_key_return forall_key152 = null;

        lhs_pattern_return lhs_pattern154 = null;


        Object LEFT_PAREN153_tree=null;
        Object RIGHT_PAREN155_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_forall_key=new RewriteRuleSubtreeStream(adaptor,"rule forall_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:605:2: ( forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:605:4: forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN
            {
            pushFollow(FOLLOW_forall_key_in_lhs_forall2303);
            forall_key152=forall_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_forall_key.add(forall_key152.getTree());
            LEFT_PAREN153=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2305); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN153);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:605:26: ( lhs_pattern )+
            int cnt53=0;
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==ID) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:605:26: lhs_pattern
            	    {
            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2307);
            	    lhs_pattern154=lhs_pattern();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern154.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt53 >= 1 ) break loop53;
            	    if (backtracking>0) {failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(53, input);
                        throw eee;
                }
                cnt53++;
            } while (true);

            RIGHT_PAREN155=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall2310); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN155);


            // AST REWRITE
            // elements: forall_key, RIGHT_PAREN, lhs_pattern
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 606:3: -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:606:6: ^( forall_key ( lhs_pattern )+ RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_forall_key.nextNode(), root_1);

                if ( !(stream_lhs_pattern.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.next());

                }
                stream_lhs_pattern.reset();
                adaptor.addChild(root_1, stream_RIGHT_PAREN.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end lhs_forall

    public static class pattern_source_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start pattern_source
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:609:1: pattern_source options {backtrack=true; } : lhs_pattern ( over_clause )? ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? ;
    public final pattern_source_return pattern_source() throws RecognitionException {
        pattern_source_return retval = new pattern_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        lhs_pattern_return lhs_pattern156 = null;

        over_clause_return over_clause157 = null;

        from_key_return from_key158 = null;

        accumulate_statement_return accumulate_statement159 = null;

        collect_statement_return collect_statement160 = null;

        entrypoint_statement_return entrypoint_statement161 = null;

        from_source_return from_source162 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:611:2: ( lhs_pattern ( over_clause )? ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:611:4: lhs_pattern ( over_clause )? ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_pattern_in_pattern_source2342);
            lhs_pattern156=lhs_pattern();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, lhs_pattern156.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:612:3: ( over_clause )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==OVER) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:612:3: over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_pattern_source2346);
                    over_clause157=over_clause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, over_clause157.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:613:3: ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==ID) ) {
                int LA56_1 = input.LA(2);

                if ( (LA56_1==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                    int LA56_3 = input.LA(3);

                    if ( (LA56_3==SEMICOLON||LA56_3==END||(LA56_3>=COMMA && LA56_3<=RIGHT_PAREN)||(LA56_3>=DOUBLE_PIPE && LA56_3<=DOUBLE_AMPER)||LA56_3==THEN||LA56_3==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                        alt56=1;
                    }
                    else if ( (LA56_3==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                        int LA56_6 = input.LA(4);

                        if ( (LA56_6==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                            int LA56_10 = input.LA(5);

                            if ( (LA56_10==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                int LA56_11 = input.LA(6);

                                if ( (LA56_11==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                    int LA56_14 = input.LA(7);

                                    if ( (LA56_14==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                        int LA56_16 = input.LA(8);

                                        if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                                            alt56=1;
                                        }
                                    }
                                    else if ( ((LA56_14>=SEMICOLON && LA56_14<=DOT)||LA56_14==END||(LA56_14>=COMMA && LA56_14<=RIGHT_PAREN)||(LA56_14>=DOUBLE_PIPE && LA56_14<=DOUBLE_AMPER)||LA56_14==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                                        alt56=1;
                                    }
                                }
                                else if ( ((LA56_11>=VT_COMPILATION_UNIT && LA56_11<=LEFT_SQUARE)||(LA56_11>=THEN && LA56_11<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                                    alt56=1;
                                }
                            }
                            else if ( (LA56_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                                int LA56_12 = input.LA(6);

                                if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                                    alt56=1;
                                }
                            }
                            else if ( (LA56_10==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                int LA56_13 = input.LA(6);

                                if ( (LA56_13==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                    int LA56_15 = input.LA(7);

                                    if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                                        alt56=1;
                                    }
                                }
                            }
                            else if ( ((LA56_10>=SEMICOLON && LA56_10<=ID)||LA56_10==END||(LA56_10>=COMMA && LA56_10<=RIGHT_PAREN)||(LA56_10>=DOUBLE_PIPE && LA56_10<=DOUBLE_AMPER)||LA56_10==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                                alt56=1;
                            }
                        }
                    }
                    else if ( (LA56_3==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                        int LA56_8 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                            alt56=1;
                        }
                    }
                    else if ( (LA56_3==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                        int LA56_9 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                            alt56=1;
                        }
                    }
                }
            }
            switch (alt56) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:614:4: from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    {
                    pushFollow(FOLLOW_from_key_in_pattern_source2356);
                    from_key158=from_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(from_key158.getTree(), root_0);
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:615:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    int alt55=4;
                    int LA55_0 = input.LA(1);

                    if ( (LA55_0==ID) ) {
                        int LA55_1 = input.LA(2);

                        if ( (LA55_1==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT)))) {
                            alt55=3;
                        }
                        else if ( (LA55_1==LEFT_PAREN) ) {
                            switch ( input.LA(3) ) {
                            case LEFT_PAREN:
                                {
                                int LA55_5 = input.LA(4);

                                if ( ((validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))) ) {
                                    alt55=1;
                                }
                                else if ( (true) ) {
                                    alt55=4;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return retval;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("615:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 55, 5, input);

                                    throw nvae;
                                }
                                }
                                break;
                            case ID:
                                {
                                int LA55_6 = input.LA(4);

                                if ( ((validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))) ) {
                                    alt55=1;
                                }
                                else if ( ((validateIdentifierKey(DroolsSoftKeywords.COLLECT))) ) {
                                    alt55=2;
                                }
                                else if ( (true) ) {
                                    alt55=4;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return retval;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("615:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 55, 6, input);

                                    throw nvae;
                                }
                                }
                                break;
                            case VT_COMPILATION_UNIT:
                            case VT_FUNCTION_IMPORT:
                            case VT_FACT:
                            case VT_CONSTRAINTS:
                            case VT_LABEL:
                            case VT_QUERY_ID:
                            case VT_TEMPLATE_ID:
                            case VT_TYPE_DECLARE_ID:
                            case VT_RULE_ID:
                            case VT_ENTRYPOINT_ID:
                            case VT_SLOT_ID:
                            case VT_SLOT:
                            case VT_RULE_ATTRIBUTES:
                            case VT_RHS_CHUNK:
                            case VT_CURLY_CHUNK:
                            case VT_SQUARE_CHUNK:
                            case VT_PAREN_CHUNK:
                            case VT_BEHAVIOR:
                            case VT_AND_IMPLICIT:
                            case VT_AND_PREFIX:
                            case VT_OR_PREFIX:
                            case VT_AND_INFIX:
                            case VT_OR_INFIX:
                            case VT_ACCUMULATE_INIT_CLAUSE:
                            case VT_ACCUMULATE_ID_CLAUSE:
                            case VT_FROM_SOURCE:
                            case VT_EXPRESSION_CHAIN:
                            case VT_PATTERN:
                            case VT_FACT_BINDING:
                            case VT_FACT_OR:
                            case VT_BIND_FIELD:
                            case VT_FIELD:
                            case VT_ACCESSOR_PATH:
                            case VT_ACCESSOR_ELEMENT:
                            case VT_DATA_TYPE:
                            case VT_PATTERN_TYPE:
                            case VT_PACKAGE_ID:
                            case VT_IMPORT_ID:
                            case VT_GLOBAL_ID:
                            case VT_FUNCTION_ID:
                            case VT_PARAM_LIST:
                            case VK_DATE_EFFECTIVE:
                            case VK_DATE_EXPIRES:
                            case VK_LOCK_ON_ACTIVE:
                            case VK_NO_LOOP:
                            case VK_AUTO_FOCUS:
                            case VK_ACTIVATION_GROUP:
                            case VK_AGENDA_GROUP:
                            case VK_RULEFLOW_GROUP:
                            case VK_DURATION:
                            case VK_DIALECT:
                            case VK_SALIENCE:
                            case VK_ENABLED:
                            case VK_ATTRIBUTES:
                            case VK_WHEN:
                            case VK_RULE:
                            case VK_IMPORT:
                            case VK_PACKAGE:
                            case VK_TEMPLATE:
                            case VK_QUERY:
                            case VK_DECLARE:
                            case VK_FUNCTION:
                            case VK_GLOBAL:
                            case VK_EVAL:
                            case VK_CONTAINS:
                            case VK_MATCHES:
                            case VK_EXCLUDES:
                            case VK_SOUNDSLIKE:
                            case VK_MEMBEROF:
                            case VK_ENTRY_POINT:
                            case VK_NOT:
                            case VK_IN:
                            case VK_OR:
                            case VK_AND:
                            case VK_EXISTS:
                            case VK_FORALL:
                            case VK_FROM:
                            case VK_ACCUMULATE:
                            case VK_INIT:
                            case VK_ACTION:
                            case VK_REVERSE:
                            case VK_RESULT:
                            case VK_COLLECT:
                            case SEMICOLON:
                            case DOT:
                            case DOT_STAR:
                            case END:
                            case STRING:
                            case COMMA:
                            case RIGHT_PAREN:
                            case AT:
                            case COLON:
                            case EQUALS:
                            case BOOL:
                            case INT:
                            case DOUBLE_PIPE:
                            case DOUBLE_AMPER:
                            case OVER:
                            case ARROW:
                            case EQUAL:
                            case GREATER:
                            case GREATER_EQUAL:
                            case LESS:
                            case LESS_EQUAL:
                            case NOT_EQUAL:
                            case GRAVE_ACCENT:
                            case FLOAT:
                            case NULL:
                            case LEFT_SQUARE:
                            case RIGHT_SQUARE:
                            case THEN:
                            case LEFT_CURLY:
                            case RIGHT_CURLY:
                            case MISC:
                            case EOL:
                            case WS:
                            case EscapeSequence:
                            case HexDigit:
                            case UnicodeEscape:
                            case OctalEscape:
                            case SH_STYLE_SINGLE_LINE_COMMENT:
                            case C_STYLE_SINGLE_LINE_COMMENT:
                            case MULTI_LINE_COMMENT:
                                {
                                alt55=4;
                                }
                                break;
                            default:
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("615:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 55, 3, input);

                                throw nvae;
                            }

                        }
                        else if ( ((LA55_1>=SEMICOLON && LA55_1<=DOT)||LA55_1==END||(LA55_1>=COMMA && LA55_1<=RIGHT_PAREN)||(LA55_1>=DOUBLE_PIPE && LA55_1<=DOUBLE_AMPER)||LA55_1==THEN) ) {
                            alt55=4;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("615:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 55, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("615:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 55, 0, input);

                        throw nvae;
                    }
                    switch (alt55) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:615:14: accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_pattern_source2372);
                            accumulate_statement159=accumulate_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, accumulate_statement159.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:616:15: collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_pattern_source2388);
                            collect_statement160=collect_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, collect_statement160.getTree());

                            }
                            break;
                        case 3 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:617:15: entrypoint_statement
                            {
                            pushFollow(FOLLOW_entrypoint_statement_in_pattern_source2405);
                            entrypoint_statement161=entrypoint_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, entrypoint_statement161.getTree());

                            }
                            break;
                        case 4 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:618:15: from_source
                            {
                            pushFollow(FOLLOW_from_source_in_pattern_source2421);
                            from_source162=from_source();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, from_source162.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end pattern_source

    public static class over_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start over_clause
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:1: over_clause : OVER over_elements ( COMMA over_elements )* ;
    public final over_clause_return over_clause() throws RecognitionException {
        over_clause_return retval = new over_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OVER163=null;
        Token COMMA165=null;
        over_elements_return over_elements164 = null;

        over_elements_return over_elements166 = null;


        Object OVER163_tree=null;
        Object COMMA165_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:624:2: ( OVER over_elements ( COMMA over_elements )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:624:4: OVER over_elements ( COMMA over_elements )*
            {
            root_0 = (Object)adaptor.nil();

            OVER163=(Token)input.LT(1);
            match(input,OVER,FOLLOW_OVER_in_over_clause2449); if (failed) return retval;
            if ( backtracking==0 ) {
            OVER163_tree = (Object)adaptor.create(OVER163);
            root_0 = (Object)adaptor.becomeRoot(OVER163_tree, root_0);
            }
            pushFollow(FOLLOW_over_elements_in_over_clause2452);
            over_elements164=over_elements();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, over_elements164.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:624:24: ( COMMA over_elements )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==COMMA) ) {
                    int LA57_2 = input.LA(2);

                    if ( (LA57_2==ID) ) {
                        int LA57_3 = input.LA(3);

                        if ( (LA57_3==COLON) ) {
                            alt57=1;
                        }


                    }


                }


                switch (alt57) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:624:25: COMMA over_elements
            	    {
            	    COMMA165=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_over_clause2455); if (failed) return retval;
            	    pushFollow(FOLLOW_over_elements_in_over_clause2458);
            	    over_elements166=over_elements();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, over_elements166.getTree());

            	    }
            	    break;

            	default :
            	    break loop57;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end over_clause

    public static class over_elements_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start over_elements
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:1: over_elements : ID COLON ID paren_chunk -> ^( VT_BEHAVIOR ID ID paren_chunk ) ;
    public final over_elements_return over_elements() throws RecognitionException {
        over_elements_return retval = new over_elements_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID167=null;
        Token COLON168=null;
        Token ID169=null;
        paren_chunk_return paren_chunk170 = null;


        Object ID167_tree=null;
        Object COLON168_tree=null;
        Object ID169_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:628:2: ( ID COLON ID paren_chunk -> ^( VT_BEHAVIOR ID ID paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:628:4: ID COLON ID paren_chunk
            {
            ID167=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_over_elements2471); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID167);

            COLON168=(Token)input.LT(1);
            match(input,COLON,FOLLOW_COLON_in_over_elements2473); if (failed) return retval;
            if ( backtracking==0 ) stream_COLON.add(COLON168);

            ID169=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_over_elements2475); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID169);

            pushFollow(FOLLOW_paren_chunk_in_over_elements2477);
            paren_chunk170=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk170.getTree());

            // AST REWRITE
            // elements: ID, paren_chunk, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 629:2: -> ^( VT_BEHAVIOR ID ID paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:629:5: ^( VT_BEHAVIOR ID ID paren_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_BEHAVIOR, "VT_BEHAVIOR"), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                adaptor.addChild(root_1, stream_ID.next());
                adaptor.addChild(root_1, stream_paren_chunk.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end over_elements

    public static class accumulate_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start accumulate_statement
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:632:1: accumulate_statement : accumulate_key LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) ;
    public final accumulate_statement_return accumulate_statement() throws RecognitionException {
        accumulate_statement_return retval = new accumulate_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN172=null;
        Token COMMA174=null;
        Token RIGHT_PAREN177=null;
        accumulate_key_return accumulate_key171 = null;

        lhs_or_return lhs_or173 = null;

        accumulate_init_clause_return accumulate_init_clause175 = null;

        accumulate_id_clause_return accumulate_id_clause176 = null;


        Object LEFT_PAREN172_tree=null;
        Object COMMA174_tree=null;
        Object RIGHT_PAREN177_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_accumulate_init_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_init_clause");
        RewriteRuleSubtreeStream stream_accumulate_id_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_id_clause");
        RewriteRuleSubtreeStream stream_accumulate_key=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_key");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:633:2: ( accumulate_key LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:633:4: accumulate_key LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN
            {
            pushFollow(FOLLOW_accumulate_key_in_accumulate_statement2501);
            accumulate_key171=accumulate_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_accumulate_key.add(accumulate_key171.getTree());
            LEFT_PAREN172=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2505); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN172);

            pushFollow(FOLLOW_lhs_or_in_accumulate_statement2507);
            lhs_or173=lhs_or();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_lhs_or.add(lhs_or173.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:21: ( COMMA )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==COMMA) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:21: COMMA
                    {
                    COMMA174=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2509); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(COMMA174);


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:3: ( accumulate_init_clause | accumulate_id_clause )
            int alt59=2;
            alt59 = dfa59.predict(input);
            switch (alt59) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:5: accumulate_init_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_statement2517);
                    accumulate_init_clause175=accumulate_init_clause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accumulate_init_clause.add(accumulate_init_clause175.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:636:5: accumulate_id_clause
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_statement2523);
                    accumulate_id_clause176=accumulate_id_clause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accumulate_id_clause.add(accumulate_id_clause176.getTree());

                    }
                    break;

            }

            RIGHT_PAREN177=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2531); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN177);


            // AST REWRITE
            // elements: lhs_or, accumulate_init_clause, RIGHT_PAREN, accumulate_key, accumulate_id_clause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 639:3: -> ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:6: ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_accumulate_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_lhs_or.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:30: ( accumulate_init_clause )?
                if ( stream_accumulate_init_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_accumulate_init_clause.next());

                }
                stream_accumulate_init_clause.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:54: ( accumulate_id_clause )?
                if ( stream_accumulate_id_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_accumulate_id_clause.next());

                }
                stream_accumulate_id_clause.reset();
                adaptor.addChild(root_1, stream_RIGHT_PAREN.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end accumulate_statement

    public static class accumulate_init_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start accumulate_init_clause
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:1: accumulate_init_clause : init_key pc1= paren_chunk ( COMMA )? action_key pc2= paren_chunk ( COMMA )? ( reverse_key pc3= paren_chunk ( COMMA )? )? result_key pc4= paren_chunk -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) ;
    public final accumulate_init_clause_return accumulate_init_clause() throws RecognitionException {
        accumulate_init_clause_return retval = new accumulate_init_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA179=null;
        Token COMMA181=null;
        Token COMMA183=null;
        paren_chunk_return pc1 = null;

        paren_chunk_return pc2 = null;

        paren_chunk_return pc3 = null;

        paren_chunk_return pc4 = null;

        init_key_return init_key178 = null;

        action_key_return action_key180 = null;

        reverse_key_return reverse_key182 = null;

        result_key_return result_key184 = null;


        Object COMMA179_tree=null;
        Object COMMA181_tree=null;
        Object COMMA183_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_reverse_key=new RewriteRuleSubtreeStream(adaptor,"rule reverse_key");
        RewriteRuleSubtreeStream stream_result_key=new RewriteRuleSubtreeStream(adaptor,"rule result_key");
        RewriteRuleSubtreeStream stream_init_key=new RewriteRuleSubtreeStream(adaptor,"rule init_key");
        RewriteRuleSubtreeStream stream_action_key=new RewriteRuleSubtreeStream(adaptor,"rule action_key");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:643:2: ( init_key pc1= paren_chunk ( COMMA )? action_key pc2= paren_chunk ( COMMA )? ( reverse_key pc3= paren_chunk ( COMMA )? )? result_key pc4= paren_chunk -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:643:4: init_key pc1= paren_chunk ( COMMA )? action_key pc2= paren_chunk ( COMMA )? ( reverse_key pc3= paren_chunk ( COMMA )? )? result_key pc4= paren_chunk
            {
            pushFollow(FOLLOW_init_key_in_accumulate_init_clause2560);
            init_key178=init_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_init_key.add(init_key178.getTree());
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2565);
            pc1=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc1.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:644:18: ( COMMA )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==COMMA) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:644:18: COMMA
                    {
                    COMMA179=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2567); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(COMMA179);


                    }
                    break;

            }

            pushFollow(FOLLOW_action_key_in_accumulate_init_clause2571);
            action_key180=action_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action_key.add(action_key180.getTree());
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2575);
            pc2=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc2.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:645:29: ( COMMA )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==COMMA) ) {
                alt61=1;
            }
            switch (alt61) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:645:29: COMMA
                    {
                    COMMA181=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2577); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(COMMA181);


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:646:2: ( reverse_key pc3= paren_chunk ( COMMA )? )?
            int alt63=2;
            alt63 = dfa63.predict(input);
            switch (alt63) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:646:4: reverse_key pc3= paren_chunk ( COMMA )?
                    {
                    pushFollow(FOLLOW_reverse_key_in_accumulate_init_clause2583);
                    reverse_key182=reverse_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_reverse_key.add(reverse_key182.getTree());
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2587);
                    pc3=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(pc3.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:646:32: ( COMMA )?
                    int alt62=2;
                    int LA62_0 = input.LA(1);

                    if ( (LA62_0==COMMA) ) {
                        alt62=1;
                    }
                    switch (alt62) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:646:32: COMMA
                            {
                            COMMA183=(Token)input.LT(1);
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2589); if (failed) return retval;
                            if ( backtracking==0 ) stream_COMMA.add(COMMA183);


                            }
                            break;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_result_key_in_accumulate_init_clause2595);
            result_key184=result_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_result_key.add(result_key184.getTree());
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2599);
            pc4=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc4.getTree());

            // AST REWRITE
            // elements: pc1, pc4, action_key, pc2, init_key, result_key, pc3, reverse_key
            // token labels: 
            // rule labels: pc2, pc3, pc4, pc1, retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_pc2=new RewriteRuleSubtreeStream(adaptor,"token pc2",pc2!=null?pc2.tree:null);
            RewriteRuleSubtreeStream stream_pc3=new RewriteRuleSubtreeStream(adaptor,"token pc3",pc3!=null?pc3.tree:null);
            RewriteRuleSubtreeStream stream_pc4=new RewriteRuleSubtreeStream(adaptor,"token pc4",pc4!=null?pc4.tree:null);
            RewriteRuleSubtreeStream stream_pc1=new RewriteRuleSubtreeStream(adaptor,"token pc1",pc1!=null?pc1.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 648:2: -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:648:5: ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ACCUMULATE_INIT_CLAUSE, "VT_ACCUMULATE_INIT_CLAUSE"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:648:33: ^( init_key $pc1)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_init_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc1.next());

                adaptor.addChild(root_1, root_2);
                }
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:648:50: ^( action_key $pc2)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_action_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc2.next());

                adaptor.addChild(root_1, root_2);
                }
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:648:69: ( ^( reverse_key $pc3) )?
                if ( stream_pc3.hasNext()||stream_reverse_key.hasNext() ) {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:648:69: ^( reverse_key $pc3)
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_reverse_key.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_pc3.next());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_pc3.reset();
                stream_reverse_key.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:648:90: ^( result_key $pc4)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_result_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc4.next());

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end accumulate_init_clause

    public static class accumulate_id_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start accumulate_id_clause
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:651:1: accumulate_id_clause : id= ID text= paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) ;
    public final accumulate_id_clause_return accumulate_id_clause() throws RecognitionException {
        accumulate_id_clause_return retval = new accumulate_id_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        paren_chunk_return text = null;


        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:652:2: (id= ID text= paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:652:4: id= ID text= paren_chunk
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accumulate_id_clause2648); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            pushFollow(FOLLOW_paren_chunk_in_accumulate_id_clause2652);
            text=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(text.getTree());

            // AST REWRITE
            // elements: ID, paren_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 653:2: -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:653:5: ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ACCUMULATE_ID_CLAUSE, "VT_ACCUMULATE_ID_CLAUSE"), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                adaptor.addChild(root_1, stream_paren_chunk.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end accumulate_id_clause

    public static class collect_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start collect_statement
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:656:1: collect_statement : collect_key LEFT_PAREN pattern_source RIGHT_PAREN -> ^( collect_key pattern_source RIGHT_PAREN ) ;
    public final collect_statement_return collect_statement() throws RecognitionException {
        collect_statement_return retval = new collect_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN186=null;
        Token RIGHT_PAREN188=null;
        collect_key_return collect_key185 = null;

        pattern_source_return pattern_source187 = null;


        Object LEFT_PAREN186_tree=null;
        Object RIGHT_PAREN188_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_collect_key=new RewriteRuleSubtreeStream(adaptor,"rule collect_key");
        RewriteRuleSubtreeStream stream_pattern_source=new RewriteRuleSubtreeStream(adaptor,"rule pattern_source");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:657:2: ( collect_key LEFT_PAREN pattern_source RIGHT_PAREN -> ^( collect_key pattern_source RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:657:4: collect_key LEFT_PAREN pattern_source RIGHT_PAREN
            {
            pushFollow(FOLLOW_collect_key_in_collect_statement2674);
            collect_key185=collect_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_collect_key.add(collect_key185.getTree());
            LEFT_PAREN186=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement2678); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN186);

            pushFollow(FOLLOW_pattern_source_in_collect_statement2680);
            pattern_source187=pattern_source();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_pattern_source.add(pattern_source187.getTree());
            RIGHT_PAREN188=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement2682); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN188);


            // AST REWRITE
            // elements: RIGHT_PAREN, pattern_source, collect_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 659:2: -> ^( collect_key pattern_source RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:659:5: ^( collect_key pattern_source RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_collect_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_pattern_source.next());
                adaptor.addChild(root_1, stream_RIGHT_PAREN.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end collect_statement

    public static class entrypoint_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start entrypoint_statement
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:662:1: entrypoint_statement : entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) ;
    public final entrypoint_statement_return entrypoint_statement() throws RecognitionException {
        entrypoint_statement_return retval = new entrypoint_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        entry_point_key_return entry_point_key189 = null;

        entrypoint_id_return entrypoint_id190 = null;


        RewriteRuleSubtreeStream stream_entrypoint_id=new RewriteRuleSubtreeStream(adaptor,"rule entrypoint_id");
        RewriteRuleSubtreeStream stream_entry_point_key=new RewriteRuleSubtreeStream(adaptor,"rule entry_point_key");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:663:2: ( entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:663:4: entry_point_key entrypoint_id
            {
            pushFollow(FOLLOW_entry_point_key_in_entrypoint_statement2704);
            entry_point_key189=entry_point_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_entry_point_key.add(entry_point_key189.getTree());
            pushFollow(FOLLOW_entrypoint_id_in_entrypoint_statement2706);
            entrypoint_id190=entrypoint_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_entrypoint_id.add(entrypoint_id190.getTree());

            // AST REWRITE
            // elements: entry_point_key, entrypoint_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 664:2: -> ^( entry_point_key entrypoint_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:664:5: ^( entry_point_key entrypoint_id )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_entry_point_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_entrypoint_id.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end entrypoint_statement

    public static class entrypoint_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start entrypoint_id
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:667:1: entrypoint_id : (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] );
    public final entrypoint_id_return entrypoint_id() throws RecognitionException {
        entrypoint_id_return retval = new entrypoint_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:668:2: (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] )
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==ID) ) {
                alt64=1;
            }
            else if ( (LA64_0==STRING) ) {
                alt64=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("667:1: entrypoint_id : (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] );", 64, 0, input);

                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:668:5: value= ID
                    {
                    value=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_entrypoint_id2729); if (failed) return retval;
                    if ( backtracking==0 ) stream_ID.add(value);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 668:14: -> VT_ENTRYPOINT_ID[$value]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_ENTRYPOINT_ID, value));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:669:5: value= STRING
                    {
                    value=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_entrypoint_id2742); if (failed) return retval;
                    if ( backtracking==0 ) stream_STRING.add(value);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 669:18: -> VT_ENTRYPOINT_ID[$value]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_ENTRYPOINT_ID, value));

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end entrypoint_id

    public static class from_source_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start from_source
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:672:1: from_source : ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) ;
    public final from_source_return from_source() throws RecognitionException {
        from_source_return retval = new from_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID191=null;
        paren_chunk_return args = null;

        expression_chain_return expression_chain192 = null;


        Object ID191_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:673:2: ( ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:673:4: ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )?
            {
            ID191=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_from_source2758); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID191);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:674:3: ( ( LEFT_PAREN )=>args= paren_chunk )?
            int alt65=2;
            alt65 = dfa65.predict(input);
            switch (alt65) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:674:5: ( LEFT_PAREN )=>args= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source2771);
                    args=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(args.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:675:3: ( expression_chain )?
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==DOT) ) {
                alt66=1;
            }
            switch (alt66) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:675:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source2778);
                    expression_chain192=expression_chain();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expression_chain.add(expression_chain192.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: ID, paren_chunk, expression_chain
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 676:2: -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:676:5: ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FROM_SOURCE, "VT_FROM_SOURCE"), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:676:25: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.next());

                }
                stream_paren_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:676:38: ( expression_chain )?
                if ( stream_expression_chain.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression_chain.next());

                }
                stream_expression_chain.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end from_source

    public static class expression_chain_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression_chain
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:679:1: expression_chain : startToken= DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) ;
    public final expression_chain_return expression_chain() throws RecognitionException {
        expression_chain_return retval = new expression_chain_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token startToken=null;
        Token ID193=null;
        square_chunk_return square_chunk194 = null;

        paren_chunk_return paren_chunk195 = null;

        expression_chain_return expression_chain196 = null;


        Object startToken_tree=null;
        Object ID193_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:680:2: (startToken= DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:681:3: startToken= DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )?
            {
            startToken=(Token)input.LT(1);
            match(input,DOT,FOLLOW_DOT_in_expression_chain2810); if (failed) return retval;
            if ( backtracking==0 ) stream_DOT.add(startToken);

            ID193=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_expression_chain2812); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID193);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:682:4: ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )?
            int alt67=3;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==LEFT_SQUARE) && (synpred9())) {
                alt67=1;
            }
            else if ( (LA67_0==LEFT_PAREN) ) {
                int LA67_2 = input.LA(2);

                if ( (LA67_2==LEFT_PAREN) ) {
                    int LA67_4 = input.LA(3);

                    if ( (synpred10()) ) {
                        alt67=2;
                    }
                }
                else if ( (LA67_2==ID) ) {
                    int LA67_5 = input.LA(3);

                    if ( (synpred10()) ) {
                        alt67=2;
                    }
                }
                else if ( ((LA67_2>=VT_COMPILATION_UNIT && LA67_2<=SEMICOLON)||(LA67_2>=DOT && LA67_2<=STRING)||LA67_2==COMMA||(LA67_2>=AT && LA67_2<=MULTI_LINE_COMMENT)) && (synpred10())) {
                    alt67=2;
                }
                else if ( (LA67_2==RIGHT_PAREN) && (synpred10())) {
                    alt67=2;
                }
            }
            switch (alt67) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:683:6: ( LEFT_SQUARE )=> square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain2832);
                    square_chunk194=square_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_square_chunk.add(square_chunk194.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:685:6: ( LEFT_PAREN )=> paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain2854);
                    paren_chunk195=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk195.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:687:4: ( expression_chain )?
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==DOT) ) {
                alt68=1;
            }
            switch (alt68) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:687:4: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain2865);
                    expression_chain196=expression_chain();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expression_chain.add(expression_chain196.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: expression_chain, ID, paren_chunk, square_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 688:4: -> ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:688:7: ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_EXPRESSION_CHAIN, startToken), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:688:45: ( square_chunk )?
                if ( stream_square_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_square_chunk.next());

                }
                stream_square_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:688:59: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.next());

                }
                stream_paren_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:688:72: ( expression_chain )?
                if ( stream_expression_chain.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression_chain.next());

                }
                stream_expression_chain.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression_chain

    public static class lhs_pattern_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_pattern
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );
    public final lhs_pattern_return lhs_pattern() throws RecognitionException {
        lhs_pattern_return retval = new lhs_pattern_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        fact_binding_return fact_binding197 = null;

        fact_return fact198 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:692:2: ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) )
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==ID) ) {
                int LA69_1 = input.LA(2);

                if ( (LA69_1==COLON) ) {
                    alt69=1;
                }
                else if ( (LA69_1==DOT||LA69_1==LEFT_PAREN||LA69_1==LEFT_SQUARE) ) {
                    alt69=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("691:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );", 69, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("691:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );", 69, 0, input);

                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:692:4: fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern2898);
                    fact_binding197=fact_binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact_binding.add(fact_binding197.getTree());

                    // AST REWRITE
                    // elements: fact_binding
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 692:17: -> ^( VT_PATTERN fact_binding )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:692:20: ^( VT_PATTERN fact_binding )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_PATTERN, "VT_PATTERN"), root_1);

                        adaptor.addChild(root_1, stream_fact_binding.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:693:4: fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern2911);
                    fact198=fact();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact.add(fact198.getTree());

                    // AST REWRITE
                    // elements: fact
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 693:9: -> ^( VT_PATTERN fact )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:693:12: ^( VT_PATTERN fact )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_PATTERN, "VT_PATTERN"), root_1);

                        adaptor.addChild(root_1, stream_fact.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end lhs_pattern

    public static class fact_binding_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start fact_binding
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:696:1: fact_binding : label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) ;
    public final fact_binding_return fact_binding() throws RecognitionException {
        fact_binding_return retval = new fact_binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN201=null;
        Token RIGHT_PAREN203=null;
        label_return label199 = null;

        fact_return fact200 = null;

        fact_binding_expression_return fact_binding_expression202 = null;


        Object LEFT_PAREN201_tree=null;
        Object RIGHT_PAREN203_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        RewriteRuleSubtreeStream stream_fact_binding_expression=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding_expression");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:3: ( label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:5: label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            {
            pushFollow(FOLLOW_label_in_fact_binding2931);
            label199=label();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_label.add(label199.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:3: ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==ID) ) {
                alt70=1;
            }
            else if ( (LA70_0==LEFT_PAREN) ) {
                alt70=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("698:3: ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:5: fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding2937);
                    fact200=fact();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact.add(fact200.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:699:6: LEFT_PAREN fact_binding_expression RIGHT_PAREN
                    {
                    LEFT_PAREN201=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding2944); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN201);

                    pushFollow(FOLLOW_fact_binding_expression_in_fact_binding2946);
                    fact_binding_expression202=fact_binding_expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact_binding_expression.add(fact_binding_expression202.getTree());
                    RIGHT_PAREN203=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding2948); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN203);


                    }
                    break;

            }


            // AST REWRITE
            // elements: RIGHT_PAREN, fact_binding_expression, fact, label
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 701:3: -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:701:6: ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FACT_BINDING, "VT_FACT_BINDING"), root_1);

                adaptor.addChild(root_1, stream_label.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:701:30: ( fact )?
                if ( stream_fact.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact.next());

                }
                stream_fact.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:701:36: ( fact_binding_expression )?
                if ( stream_fact_binding_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact_binding_expression.next());

                }
                stream_fact_binding_expression.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:701:61: ( RIGHT_PAREN )?
                if ( stream_RIGHT_PAREN.hasNext() ) {
                    adaptor.addChild(root_1, stream_RIGHT_PAREN.next());

                }
                stream_RIGHT_PAREN.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end fact_binding

    public static class fact_binding_expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start fact_binding_expression
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:704:1: fact_binding_expression : ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* ;
    public final fact_binding_expression_return fact_binding_expression() throws RecognitionException {
        fact_binding_expression_return retval = new fact_binding_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        or_key_return value = null;

        fact_return fact204 = null;

        fact_return fact205 = null;


        Object pipe_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");

        	Token orToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:3: ( ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:5: ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:5: ( fact -> fact )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:6: fact
            {
            pushFollow(FOLLOW_fact_in_fact_binding_expression2987);
            fact204=fact();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_fact.add(fact204.getTree());

            // AST REWRITE
            // elements: fact
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 707:11: -> fact
            {
                adaptor.addChild(root_0, stream_fact.next());

            }

            }

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:20: ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
                    alt72=1;
                }
                else if ( (LA72_0==DOUBLE_PIPE) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:22: (value= or_key | pipe= DOUBLE_PIPE ) fact
            	    {
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:22: (value= or_key | pipe= DOUBLE_PIPE )
            	    int alt71=2;
            	    int LA71_0 = input.LA(1);

            	    if ( (LA71_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
            	        alt71=1;
            	    }
            	    else if ( (LA71_0==DOUBLE_PIPE) ) {
            	        alt71=2;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("707:22: (value= or_key | pipe= DOUBLE_PIPE )", 71, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt71) {
            	        case 1 :
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:23: value= or_key
            	            {
            	            pushFollow(FOLLOW_or_key_in_fact_binding_expression2999);
            	            value=or_key();
            	            _fsp--;
            	            if (failed) return retval;
            	            if ( backtracking==0 ) stream_or_key.add(value.getTree());
            	            if ( backtracking==0 ) {
            	              orToken = ((Token)value.start);
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:62: pipe= DOUBLE_PIPE
            	            {
            	            pipe=(Token)input.LT(1);
            	            match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3005); if (failed) return retval;
            	            if ( backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

            	            if ( backtracking==0 ) {
            	              orToken = pipe;
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_fact_in_fact_binding_expression3010);
            	    fact205=fact();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_fact.add(fact205.getTree());

            	    // AST REWRITE
            	    // elements: fact, fact_binding_expression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    if ( backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 708:3: -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
            	    {
            	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:708:6: ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
            	        {
            	        Object root_1 = (Object)adaptor.nil();
            	        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FACT_OR, orToken), root_1);

            	        adaptor.addChild(root_1, stream_retval.next());
            	        adaptor.addChild(root_1, stream_fact.next());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    }

            	    }
            	    break;

            	default :
            	    break loop72;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end fact_binding_expression

    public static class fact_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start fact
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:711:1: fact : pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) ;
    public final fact_return fact() throws RecognitionException {
        fact_return retval = new fact_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN207=null;
        Token RIGHT_PAREN209=null;
        pattern_type_return pattern_type206 = null;

        constraints_return constraints208 = null;


        Object LEFT_PAREN207_tree=null;
        Object RIGHT_PAREN209_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_pattern_type=new RewriteRuleSubtreeStream(adaptor,"rule pattern_type");
        RewriteRuleSubtreeStream stream_constraints=new RewriteRuleSubtreeStream(adaptor,"rule constraints");
         pushParaphrases(DroolsParaphareseTypes.PATTERN); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:714:2: ( pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:714:4: pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN
            {
            pushFollow(FOLLOW_pattern_type_in_fact3050);
            pattern_type206=pattern_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_pattern_type.add(pattern_type206.getTree());
            LEFT_PAREN207=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact3052); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN207);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:714:28: ( constraints )?
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==ID||LA73_0==LEFT_PAREN) ) {
                alt73=1;
            }
            switch (alt73) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:714:28: constraints
                    {
                    pushFollow(FOLLOW_constraints_in_fact3054);
                    constraints208=constraints();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_constraints.add(constraints208.getTree());

                    }
                    break;

            }

            RIGHT_PAREN209=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact3057); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN209);


            // AST REWRITE
            // elements: RIGHT_PAREN, pattern_type, constraints
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 715:2: -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:715:5: ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FACT, "VT_FACT"), root_1);

                adaptor.addChild(root_1, stream_pattern_type.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:715:28: ( constraints )?
                if ( stream_constraints.hasNext() ) {
                    adaptor.addChild(root_1, stream_constraints.next());

                }
                stream_constraints.reset();
                adaptor.addChild(root_1, stream_RIGHT_PAREN.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end fact

    public static class constraints_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start constraints
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:718:1: constraints : constraint ( COMMA constraint )* ;
    public final constraints_return constraints() throws RecognitionException {
        constraints_return retval = new constraints_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA211=null;
        constraint_return constraint210 = null;

        constraint_return constraint212 = null;


        Object COMMA211_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:719:2: ( constraint ( COMMA constraint )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:719:4: constraint ( COMMA constraint )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_in_constraints3082);
            constraint210=constraint();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, constraint210.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:719:15: ( COMMA constraint )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==COMMA) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:719:17: COMMA constraint
            	    {
            	    COMMA211=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_constraints3086); if (failed) return retval;
            	    pushFollow(FOLLOW_constraint_in_constraints3089);
            	    constraint212=constraint();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, constraint212.getTree());

            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end constraints

    public static class constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start constraint
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:722:1: constraint : or_constr ;
    public final constraint_return constraint() throws RecognitionException {
        constraint_return retval = new constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        or_constr_return or_constr213 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:723:2: ( or_constr )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:723:4: or_constr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_or_constr_in_constraint3103);
            or_constr213=or_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, or_constr213.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end constraint

    public static class or_constr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start or_constr
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:1: or_constr : and_constr ( DOUBLE_PIPE and_constr )* ;
    public final or_constr_return or_constr() throws RecognitionException {
        or_constr_return retval = new or_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE215=null;
        and_constr_return and_constr214 = null;

        and_constr_return and_constr216 = null;


        Object DOUBLE_PIPE215_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:727:2: ( and_constr ( DOUBLE_PIPE and_constr )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:727:4: and_constr ( DOUBLE_PIPE and_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_constr_in_or_constr3114);
            and_constr214=and_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, and_constr214.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:727:15: ( DOUBLE_PIPE and_constr )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==DOUBLE_PIPE) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:727:17: DOUBLE_PIPE and_constr
            	    {
            	    DOUBLE_PIPE215=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr3118); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_PIPE215_tree = (Object)adaptor.create(DOUBLE_PIPE215);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE215_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr3121);
            	    and_constr216=and_constr();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, and_constr216.getTree());

            	    }
            	    break;

            	default :
            	    break loop75;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end or_constr

    public static class and_constr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start and_constr
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:730:1: and_constr : unary_constr ( DOUBLE_AMPER unary_constr )* ;
    public final and_constr_return and_constr() throws RecognitionException {
        and_constr_return retval = new and_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER218=null;
        unary_constr_return unary_constr217 = null;

        unary_constr_return unary_constr219 = null;


        Object DOUBLE_AMPER218_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:2: ( unary_constr ( DOUBLE_AMPER unary_constr )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:4: unary_constr ( DOUBLE_AMPER unary_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_constr_in_and_constr3136);
            unary_constr217=unary_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, unary_constr217.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:17: ( DOUBLE_AMPER unary_constr )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==DOUBLE_AMPER) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:19: DOUBLE_AMPER unary_constr
            	    {
            	    DOUBLE_AMPER218=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr3140); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_AMPER218_tree = (Object)adaptor.create(DOUBLE_AMPER218);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER218_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr3143);
            	    unary_constr219=unary_constr();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, unary_constr219.getTree());

            	    }
            	    break;

            	default :
            	    break loop76;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end and_constr

    public static class unary_constr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start unary_constr
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:734:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );
    public final unary_constr_return unary_constr() throws RecognitionException {
        unary_constr_return retval = new unary_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN223=null;
        Token RIGHT_PAREN225=null;
        eval_key_return eval_key220 = null;

        paren_chunk_return paren_chunk221 = null;

        field_constraint_return field_constraint222 = null;

        or_constr_return or_constr224 = null;


        Object LEFT_PAREN223_tree=null;
        Object RIGHT_PAREN225_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:736:2: ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN )
            int alt77=3;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==ID) ) {
                int LA77_1 = input.LA(2);

                if ( ((LA77_1>=ID && LA77_1<=DOT)||LA77_1==COLON||(LA77_1>=EQUAL && LA77_1<=GRAVE_ACCENT)||LA77_1==LEFT_SQUARE) ) {
                    alt77=2;
                }
                else if ( (LA77_1==LEFT_PAREN) ) {
                    int LA77_4 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                        alt77=1;
                    }
                    else if ( (true) ) {
                        alt77=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("734:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 77, 4, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("734:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 77, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA77_0==LEFT_PAREN) ) {
                alt77=3;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("734:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 77, 0, input);

                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:736:4: eval_key paren_chunk
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_eval_key_in_unary_constr3164);
                    eval_key220=eval_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(eval_key220.getTree(), root_0);
                    pushFollow(FOLLOW_paren_chunk_in_unary_constr3167);
                    paren_chunk221=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk221.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:737:4: field_constraint
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_field_constraint_in_unary_constr3172);
                    field_constraint222=field_constraint();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, field_constraint222.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:738:4: LEFT_PAREN or_constr RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN223=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr3177); if (failed) return retval;
                    pushFollow(FOLLOW_or_constr_in_unary_constr3180);
                    or_constr224=or_constr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, or_constr224.getTree());
                    RIGHT_PAREN225=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr3182); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN225_tree = (Object)adaptor.create(RIGHT_PAREN225);
                    adaptor.addChild(root_0, RIGHT_PAREN225_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end unary_constr

    public static class field_constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start field_constraint
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:741:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );
    public final field_constraint_return field_constraint() throws RecognitionException {
        field_constraint_return retval = new field_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token arw=null;
        label_return label226 = null;

        accessor_path_return accessor_path227 = null;

        or_restr_connective_return or_restr_connective228 = null;

        paren_chunk_return paren_chunk229 = null;

        accessor_path_return accessor_path230 = null;

        or_restr_connective_return or_restr_connective231 = null;


        Object arw_tree=null;
        RewriteRuleTokenStream stream_ARROW=new RewriteRuleTokenStream(adaptor,"token ARROW");
        RewriteRuleSubtreeStream stream_accessor_path=new RewriteRuleSubtreeStream(adaptor,"rule accessor_path");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        RewriteRuleSubtreeStream stream_or_restr_connective=new RewriteRuleSubtreeStream(adaptor,"rule or_restr_connective");

        	boolean isArrow = false;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:3: ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) )
            int alt79=2;
            int LA79_0 = input.LA(1);

            if ( (LA79_0==ID) ) {
                int LA79_1 = input.LA(2);

                if ( (LA79_1==COLON) ) {
                    alt79=1;
                }
                else if ( ((LA79_1>=ID && LA79_1<=DOT)||LA79_1==LEFT_PAREN||(LA79_1>=EQUAL && LA79_1<=GRAVE_ACCENT)||LA79_1==LEFT_SQUARE) ) {
                    alt79=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("741:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );", 79, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("741:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );", 79, 0, input);

                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:5: label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )?
                    {
                    pushFollow(FOLLOW_label_in_field_constraint3196);
                    label226=label();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_label.add(label226.getTree());
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3198);
                    accessor_path227=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accessor_path.add(accessor_path227.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:25: ( or_restr_connective | arw= ARROW paren_chunk )?
                    int alt78=3;
                    int LA78_0 = input.LA(1);

                    if ( (LA78_0==ID||LA78_0==LEFT_PAREN||(LA78_0>=EQUAL && LA78_0<=GRAVE_ACCENT)) ) {
                        alt78=1;
                    }
                    else if ( (LA78_0==ARROW) ) {
                        alt78=2;
                    }
                    switch (alt78) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:27: or_restr_connective
                            {
                            pushFollow(FOLLOW_or_restr_connective_in_field_constraint3202);
                            or_restr_connective228=or_restr_connective();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_or_restr_connective.add(or_restr_connective228.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:49: arw= ARROW paren_chunk
                            {
                            arw=(Token)input.LT(1);
                            match(input,ARROW,FOLLOW_ARROW_in_field_constraint3208); if (failed) return retval;
                            if ( backtracking==0 ) stream_ARROW.add(arw);

                            pushFollow(FOLLOW_paren_chunk_in_field_constraint3210);
                            paren_chunk229=paren_chunk();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk229.getTree());
                            if ( backtracking==0 ) {
                              isArrow = true;
                            }

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: paren_chunk, or_restr_connective, accessor_path, accessor_path, label, label
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 745:3: -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )?
                    if (isArrow) {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:17: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.next());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:39: ^( VT_FIELD accessor_path )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.next());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:66: ( ^( VK_EVAL[$arw] paren_chunk ) )?
                        if ( stream_paren_chunk.hasNext() ) {
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:66: ^( VK_EVAL[$arw] paren_chunk )
                            {
                            Object root_1 = (Object)adaptor.nil();
                            root_1 = (Object)adaptor.becomeRoot(adaptor.create(VK_EVAL, arw), root_1);

                            adaptor.addChild(root_1, stream_paren_chunk.next());

                            adaptor.addChild(root_0, root_1);
                            }

                        }
                        stream_paren_chunk.reset();

                    }
                    else // 746:3: -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:746:6: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.next());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:746:28: ^( VT_FIELD accessor_path ( or_restr_connective )? )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.next());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:746:53: ( or_restr_connective )?
                        if ( stream_or_restr_connective.hasNext() ) {
                            adaptor.addChild(root_2, stream_or_restr_connective.next());

                        }
                        stream_or_restr_connective.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:747:4: accessor_path or_restr_connective
                    {
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3264);
                    accessor_path230=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accessor_path.add(accessor_path230.getTree());
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint3266);
                    or_restr_connective231=or_restr_connective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_or_restr_connective.add(or_restr_connective231.getTree());

                    // AST REWRITE
                    // elements: or_restr_connective, accessor_path
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 748:3: -> ^( VT_FIELD accessor_path or_restr_connective )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:748:6: ^( VT_FIELD accessor_path or_restr_connective )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FIELD, "VT_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_accessor_path.next());
                        adaptor.addChild(root_1, stream_or_restr_connective.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end field_constraint

    public static class label_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start label
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:751:1: label : value= ID COLON -> VT_LABEL[$value] ;
    public final label_return label() throws RecognitionException {
        label_return retval = new label_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;
        Token COLON232=null;

        Object value_tree=null;
        Object COLON232_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:751:7: (value= ID COLON -> VT_LABEL[$value] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:751:9: value= ID COLON
            {
            value=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_label3290); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(value);

            COLON232=(Token)input.LT(1);
            match(input,COLON,FOLLOW_COLON_in_label3292); if (failed) return retval;
            if ( backtracking==0 ) stream_COLON.add(COLON232);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 751:24: -> VT_LABEL[$value]
            {
                adaptor.addChild(root_0, adaptor.create(VT_LABEL, value));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end label

    public static class or_restr_connective_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start or_restr_connective
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:754:1: or_restr_connective : and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* ;
    public final or_restr_connective_return or_restr_connective() throws RecognitionException {
        or_restr_connective_return retval = new or_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE234=null;
        and_restr_connective_return and_restr_connective233 = null;

        and_restr_connective_return and_restr_connective235 = null;


        Object DOUBLE_PIPE234_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:755:2: ( and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:755:4: and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3308);
            and_restr_connective233=and_restr_connective();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, and_restr_connective233.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:755:25: ({...}? => DOUBLE_PIPE and_restr_connective )*
            loop80:
            do {
                int alt80=2;
                alt80 = dfa80.predict(input);
                switch (alt80) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:755:26: {...}? => DOUBLE_PIPE and_restr_connective
            	    {
            	    if ( !((validateRestr())) ) {
            	        if (backtracking>0) {failed=true; return retval;}
            	        throw new FailedPredicateException(input, "or_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_PIPE234=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective3314); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_PIPE234_tree = (Object)adaptor.create(DOUBLE_PIPE234);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE234_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3317);
            	    and_restr_connective235=and_restr_connective();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, and_restr_connective235.getTree());

            	    }
            	    break;

            	default :
            	    break loop80;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end or_restr_connective

    public static class and_restr_connective_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start and_restr_connective
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:1: and_restr_connective : constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* ;
    public final and_restr_connective_return and_restr_connective() throws RecognitionException {
        and_restr_connective_return retval = new and_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER237=null;
        constraint_expression_return constraint_expression236 = null;

        constraint_expression_return constraint_expression238 = null;


        Object DOUBLE_AMPER237_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:759:2: ( constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:759:4: constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3332);
            constraint_expression236=constraint_expression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, constraint_expression236.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:759:26: ({...}? => DOUBLE_AMPER constraint_expression )*
            loop81:
            do {
                int alt81=2;
                alt81 = dfa81.predict(input);
                switch (alt81) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:759:27: {...}? => DOUBLE_AMPER constraint_expression
            	    {
            	    if ( !((validateRestr())) ) {
            	        if (backtracking>0) {failed=true; return retval;}
            	        throw new FailedPredicateException(input, "and_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_AMPER237=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective3338); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_AMPER237_tree = (Object)adaptor.create(DOUBLE_AMPER237);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER237_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3341);
            	    constraint_expression238=constraint_expression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, constraint_expression238.getTree());

            	    }
            	    break;

            	default :
            	    break loop81;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end and_restr_connective

    public static class constraint_expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start constraint_expression
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:762:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );
    public final constraint_expression_return constraint_expression() throws RecognitionException {
        constraint_expression_return retval = new constraint_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN241=null;
        Token RIGHT_PAREN243=null;
        compound_operator_return compound_operator239 = null;

        simple_operator_return simple_operator240 = null;

        or_restr_connective_return or_restr_connective242 = null;


        Object LEFT_PAREN241_tree=null;
        Object RIGHT_PAREN243_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:765:3: ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN )
            int alt82=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA82_1 = input.LA(2);

                if ( (LA82_1==LEFT_PAREN) ) {
                    switch ( input.LA(3) ) {
                    case ID:
                        {
                        int LA82_14 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt82=1;
                        }
                        else if ( (true) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("762:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 14, input);

                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_PAREN:
                        {
                        int LA82_15 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt82=1;
                        }
                        else if ( (true) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("762:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case VT_COMPILATION_UNIT:
                    case VT_FUNCTION_IMPORT:
                    case VT_FACT:
                    case VT_CONSTRAINTS:
                    case VT_LABEL:
                    case VT_QUERY_ID:
                    case VT_TEMPLATE_ID:
                    case VT_TYPE_DECLARE_ID:
                    case VT_RULE_ID:
                    case VT_ENTRYPOINT_ID:
                    case VT_SLOT_ID:
                    case VT_SLOT:
                    case VT_RULE_ATTRIBUTES:
                    case VT_RHS_CHUNK:
                    case VT_CURLY_CHUNK:
                    case VT_SQUARE_CHUNK:
                    case VT_PAREN_CHUNK:
                    case VT_BEHAVIOR:
                    case VT_AND_IMPLICIT:
                    case VT_AND_PREFIX:
                    case VT_OR_PREFIX:
                    case VT_AND_INFIX:
                    case VT_OR_INFIX:
                    case VT_ACCUMULATE_INIT_CLAUSE:
                    case VT_ACCUMULATE_ID_CLAUSE:
                    case VT_FROM_SOURCE:
                    case VT_EXPRESSION_CHAIN:
                    case VT_PATTERN:
                    case VT_FACT_BINDING:
                    case VT_FACT_OR:
                    case VT_BIND_FIELD:
                    case VT_FIELD:
                    case VT_ACCESSOR_PATH:
                    case VT_ACCESSOR_ELEMENT:
                    case VT_DATA_TYPE:
                    case VT_PATTERN_TYPE:
                    case VT_PACKAGE_ID:
                    case VT_IMPORT_ID:
                    case VT_GLOBAL_ID:
                    case VT_FUNCTION_ID:
                    case VT_PARAM_LIST:
                    case VK_DATE_EFFECTIVE:
                    case VK_DATE_EXPIRES:
                    case VK_LOCK_ON_ACTIVE:
                    case VK_NO_LOOP:
                    case VK_AUTO_FOCUS:
                    case VK_ACTIVATION_GROUP:
                    case VK_AGENDA_GROUP:
                    case VK_RULEFLOW_GROUP:
                    case VK_DURATION:
                    case VK_DIALECT:
                    case VK_SALIENCE:
                    case VK_ENABLED:
                    case VK_ATTRIBUTES:
                    case VK_WHEN:
                    case VK_RULE:
                    case VK_IMPORT:
                    case VK_PACKAGE:
                    case VK_TEMPLATE:
                    case VK_QUERY:
                    case VK_DECLARE:
                    case VK_FUNCTION:
                    case VK_GLOBAL:
                    case VK_EVAL:
                    case VK_CONTAINS:
                    case VK_MATCHES:
                    case VK_EXCLUDES:
                    case VK_SOUNDSLIKE:
                    case VK_MEMBEROF:
                    case VK_ENTRY_POINT:
                    case VK_NOT:
                    case VK_IN:
                    case VK_OR:
                    case VK_AND:
                    case VK_EXISTS:
                    case VK_FORALL:
                    case VK_FROM:
                    case VK_ACCUMULATE:
                    case VK_INIT:
                    case VK_ACTION:
                    case VK_REVERSE:
                    case VK_RESULT:
                    case VK_COLLECT:
                    case SEMICOLON:
                    case DOT:
                    case DOT_STAR:
                    case END:
                    case COMMA:
                    case RIGHT_PAREN:
                    case AT:
                    case COLON:
                    case EQUALS:
                    case DOUBLE_PIPE:
                    case DOUBLE_AMPER:
                    case OVER:
                    case ARROW:
                    case EQUAL:
                    case GREATER:
                    case GREATER_EQUAL:
                    case LESS:
                    case LESS_EQUAL:
                    case NOT_EQUAL:
                    case GRAVE_ACCENT:
                    case LEFT_SQUARE:
                    case RIGHT_SQUARE:
                    case THEN:
                    case LEFT_CURLY:
                    case RIGHT_CURLY:
                    case MISC:
                    case EOL:
                    case WS:
                    case EscapeSequence:
                    case HexDigit:
                    case UnicodeEscape:
                    case OctalEscape:
                    case SH_STYLE_SINGLE_LINE_COMMENT:
                    case C_STYLE_SINGLE_LINE_COMMENT:
                    case MULTI_LINE_COMMENT:
                        {
                        alt82=2;
                        }
                        break;
                    case STRING:
                    case BOOL:
                    case INT:
                    case FLOAT:
                    case NULL:
                        {
                        int LA82_17 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt82=1;
                        }
                        else if ( (true) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("762:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("762:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA82_1==ID) ) {
                    int LA82_11 = input.LA(3);

                    if ( (LA82_11==ID||LA82_11==STRING||(LA82_11>=BOOL && LA82_11<=INT)||(LA82_11>=FLOAT && LA82_11<=NULL)) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                        alt82=2;
                    }
                    else if ( (LA82_11==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                        int LA82_21 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                            alt82=1;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("762:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 21, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA82_11==DOT||(LA82_11>=COMMA && LA82_11<=RIGHT_PAREN)||(LA82_11>=DOUBLE_PIPE && LA82_11<=DOUBLE_AMPER)||LA82_11==LEFT_SQUARE) ) {
                        alt82=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("762:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 11, input);

                        throw nvae;
                    }
                }
                else if ( (LA82_1==STRING||(LA82_1>=BOOL && LA82_1<=INT)||(LA82_1>=FLOAT && LA82_1<=NULL)) ) {
                    alt82=2;
                }
                else if ( (LA82_1==GRAVE_ACCENT) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                    alt82=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("762:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 1, input);

                    throw nvae;
                }
                }
                break;
            case EQUAL:
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case NOT_EQUAL:
            case GRAVE_ACCENT:
                {
                alt82=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt82=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("762:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 0, input);

                throw nvae;
            }

            switch (alt82) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:765:5: compound_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_compound_operator_in_constraint_expression3363);
                    compound_operator239=compound_operator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, compound_operator239.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:766:4: simple_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_simple_operator_in_constraint_expression3368);
                    simple_operator240=simple_operator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, simple_operator240.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:767:4: LEFT_PAREN or_restr_connective RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN241=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression3373); if (failed) return retval;
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression3376);
                    or_restr_connective242=or_restr_connective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, or_restr_connective242.getTree());
                    RIGHT_PAREN243=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression3378); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN243_tree = (Object)adaptor.create(RIGHT_PAREN243);
                    adaptor.addChild(root_0, RIGHT_PAREN243_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch ( RecognitionException re ) {

            		if (!lookaheadTest){
            			reportError(re);
            			recover(input, re);
            		} else {
            			throw re;
            		}
            	
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end constraint_expression

    public static class simple_operator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start simple_operator
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:778:1: simple_operator : ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) expression_value ;
    public final simple_operator_return simple_operator() throws RecognitionException {
        simple_operator_return retval = new simple_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUAL244=null;
        Token GREATER245=null;
        Token GREATER_EQUAL246=null;
        Token LESS247=null;
        Token LESS_EQUAL248=null;
        Token NOT_EQUAL249=null;
        Token ID255=null;
        Token GRAVE_ACCENT256=null;
        Token ID257=null;
        Token ID264=null;
        Token GRAVE_ACCENT265=null;
        Token ID266=null;
        not_key_return not_key250 = null;

        contains_key_return contains_key251 = null;

        soundslike_key_return soundslike_key252 = null;

        matches_key_return matches_key253 = null;

        memberof_key_return memberof_key254 = null;

        square_chunk_return square_chunk258 = null;

        contains_key_return contains_key259 = null;

        excludes_key_return excludes_key260 = null;

        matches_key_return matches_key261 = null;

        soundslike_key_return soundslike_key262 = null;

        memberof_key_return memberof_key263 = null;

        square_chunk_return square_chunk267 = null;

        expression_value_return expression_value268 = null;


        Object EQUAL244_tree=null;
        Object GREATER245_tree=null;
        Object GREATER_EQUAL246_tree=null;
        Object LESS247_tree=null;
        Object LESS_EQUAL248_tree=null;
        Object NOT_EQUAL249_tree=null;
        Object ID255_tree=null;
        Object GRAVE_ACCENT256_tree=null;
        Object ID257_tree=null;
        Object ID264_tree=null;
        Object GRAVE_ACCENT265_tree=null;
        Object ID266_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:779:2: ( ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) expression_value )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:779:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) expression_value
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:779:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )
            int alt84=14;
            switch ( input.LA(1) ) {
            case EQUAL:
                {
                alt84=1;
                }
                break;
            case GREATER:
                {
                alt84=2;
                }
                break;
            case GREATER_EQUAL:
                {
                alt84=3;
                }
                break;
            case LESS:
                {
                alt84=4;
                }
                break;
            case LESS_EQUAL:
                {
                alt84=5;
                }
                break;
            case NOT_EQUAL:
                {
                alt84=6;
                }
                break;
            case ID:
                {
                int LA84_7 = input.LA(2);

                if ( (LA84_7==ID||LA84_7==GRAVE_ACCENT) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                    alt84=7;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.CONTAINS))) ) {
                    alt84=8;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))) ) {
                    alt84=9;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.MATCHES))) ) {
                    alt84=10;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))) ) {
                    alt84=11;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))) ) {
                    alt84=12;
                }
                else if ( (true) ) {
                    alt84=13;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("779:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 84, 7, input);

                    throw nvae;
                }
                }
                break;
            case GRAVE_ACCENT:
                {
                alt84=14;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("779:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:779:5: EQUAL
                    {
                    EQUAL244=(Token)input.LT(1);
                    match(input,EQUAL,FOLLOW_EQUAL_in_simple_operator3397); if (failed) return retval;
                    if ( backtracking==0 ) {
                    EQUAL244_tree = (Object)adaptor.create(EQUAL244);
                    root_0 = (Object)adaptor.becomeRoot(EQUAL244_tree, root_0);
                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:780:4: GREATER
                    {
                    GREATER245=(Token)input.LT(1);
                    match(input,GREATER,FOLLOW_GREATER_in_simple_operator3403); if (failed) return retval;
                    if ( backtracking==0 ) {
                    GREATER245_tree = (Object)adaptor.create(GREATER245);
                    root_0 = (Object)adaptor.becomeRoot(GREATER245_tree, root_0);
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:781:4: GREATER_EQUAL
                    {
                    GREATER_EQUAL246=(Token)input.LT(1);
                    match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_simple_operator3409); if (failed) return retval;
                    if ( backtracking==0 ) {
                    GREATER_EQUAL246_tree = (Object)adaptor.create(GREATER_EQUAL246);
                    root_0 = (Object)adaptor.becomeRoot(GREATER_EQUAL246_tree, root_0);
                    }

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:782:4: LESS
                    {
                    LESS247=(Token)input.LT(1);
                    match(input,LESS,FOLLOW_LESS_in_simple_operator3415); if (failed) return retval;
                    if ( backtracking==0 ) {
                    LESS247_tree = (Object)adaptor.create(LESS247);
                    root_0 = (Object)adaptor.becomeRoot(LESS247_tree, root_0);
                    }

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:4: LESS_EQUAL
                    {
                    LESS_EQUAL248=(Token)input.LT(1);
                    match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_simple_operator3421); if (failed) return retval;
                    if ( backtracking==0 ) {
                    LESS_EQUAL248_tree = (Object)adaptor.create(LESS_EQUAL248);
                    root_0 = (Object)adaptor.becomeRoot(LESS_EQUAL248_tree, root_0);
                    }

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:784:4: NOT_EQUAL
                    {
                    NOT_EQUAL249=(Token)input.LT(1);
                    match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_simple_operator3427); if (failed) return retval;
                    if ( backtracking==0 ) {
                    NOT_EQUAL249_tree = (Object)adaptor.create(NOT_EQUAL249);
                    root_0 = (Object)adaptor.becomeRoot(NOT_EQUAL249_tree, root_0);
                    }

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:4: not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )
                    {
                    pushFollow(FOLLOW_not_key_in_simple_operator3433);
                    not_key250=not_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, not_key250.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:12: ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )
                    int alt83=6;
                    int LA83_0 = input.LA(1);

                    if ( (LA83_0==ID) ) {
                        int LA83_1 = input.LA(2);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.CONTAINS))) ) {
                            alt83=1;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))) ) {
                            alt83=2;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.MATCHES))) ) {
                            alt83=3;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))) ) {
                            alt83=4;
                        }
                        else if ( (true) ) {
                            alt83=5;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("785:12: ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 83, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA83_0==GRAVE_ACCENT) ) {
                        alt83=6;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("785:12: ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 83, 0, input);

                        throw nvae;
                    }
                    switch (alt83) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:13: contains_key
                            {
                            pushFollow(FOLLOW_contains_key_in_simple_operator3436);
                            contains_key251=contains_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(contains_key251.getTree(), root_0);

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:27: soundslike_key
                            {
                            pushFollow(FOLLOW_soundslike_key_in_simple_operator3439);
                            soundslike_key252=soundslike_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(soundslike_key252.getTree(), root_0);

                            }
                            break;
                        case 3 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:43: matches_key
                            {
                            pushFollow(FOLLOW_matches_key_in_simple_operator3442);
                            matches_key253=matches_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(matches_key253.getTree(), root_0);

                            }
                            break;
                        case 4 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:56: memberof_key
                            {
                            pushFollow(FOLLOW_memberof_key_in_simple_operator3445);
                            memberof_key254=memberof_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(memberof_key254.getTree(), root_0);

                            }
                            break;
                        case 5 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:71: ID
                            {
                            ID255=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_simple_operator3449); if (failed) return retval;
                            if ( backtracking==0 ) {
                            ID255_tree = (Object)adaptor.create(ID255);
                            root_0 = (Object)adaptor.becomeRoot(ID255_tree, root_0);
                            }

                            }
                            break;
                        case 6 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:77: GRAVE_ACCENT ID square_chunk
                            {
                            GRAVE_ACCENT256=(Token)input.LT(1);
                            match(input,GRAVE_ACCENT,FOLLOW_GRAVE_ACCENT_in_simple_operator3454); if (failed) return retval;
                            ID257=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_simple_operator3457); if (failed) return retval;
                            if ( backtracking==0 ) {
                            ID257_tree = (Object)adaptor.create(ID257);
                            root_0 = (Object)adaptor.becomeRoot(ID257_tree, root_0);
                            }
                            pushFollow(FOLLOW_square_chunk_in_simple_operator3460);
                            square_chunk258=square_chunk();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk258.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:786:4: contains_key
                    {
                    pushFollow(FOLLOW_contains_key_in_simple_operator3466);
                    contains_key259=contains_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(contains_key259.getTree(), root_0);

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:787:4: excludes_key
                    {
                    pushFollow(FOLLOW_excludes_key_in_simple_operator3472);
                    excludes_key260=excludes_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(excludes_key260.getTree(), root_0);

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:788:4: matches_key
                    {
                    pushFollow(FOLLOW_matches_key_in_simple_operator3478);
                    matches_key261=matches_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(matches_key261.getTree(), root_0);

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:4: soundslike_key
                    {
                    pushFollow(FOLLOW_soundslike_key_in_simple_operator3484);
                    soundslike_key262=soundslike_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(soundslike_key262.getTree(), root_0);

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:790:4: memberof_key
                    {
                    pushFollow(FOLLOW_memberof_key_in_simple_operator3490);
                    memberof_key263=memberof_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(memberof_key263.getTree(), root_0);

                    }
                    break;
                case 13 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:791:4: ID
                    {
                    ID264=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator3496); if (failed) return retval;
                    if ( backtracking==0 ) {
                    ID264_tree = (Object)adaptor.create(ID264);
                    root_0 = (Object)adaptor.becomeRoot(ID264_tree, root_0);
                    }

                    }
                    break;
                case 14 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:792:4: GRAVE_ACCENT ID square_chunk
                    {
                    GRAVE_ACCENT265=(Token)input.LT(1);
                    match(input,GRAVE_ACCENT,FOLLOW_GRAVE_ACCENT_in_simple_operator3502); if (failed) return retval;
                    ID266=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator3505); if (failed) return retval;
                    if ( backtracking==0 ) {
                    ID266_tree = (Object)adaptor.create(ID266);
                    root_0 = (Object)adaptor.becomeRoot(ID266_tree, root_0);
                    }
                    pushFollow(FOLLOW_square_chunk_in_simple_operator3508);
                    square_chunk267=square_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk267.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_expression_value_in_simple_operator3512);
            expression_value268=expression_value();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression_value268.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end simple_operator

    public static class compound_operator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start compound_operator
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:797:1: compound_operator : ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN ;
    public final compound_operator_return compound_operator() throws RecognitionException {
        compound_operator_return retval = new compound_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN272=null;
        Token COMMA274=null;
        Token RIGHT_PAREN276=null;
        in_key_return in_key269 = null;

        not_key_return not_key270 = null;

        in_key_return in_key271 = null;

        expression_value_return expression_value273 = null;

        expression_value_return expression_value275 = null;


        Object LEFT_PAREN272_tree=null;
        Object COMMA274_tree=null;
        Object RIGHT_PAREN276_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:2: ( ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:4: ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:4: ( in_key | not_key in_key )
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( (LA85_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IN))||(validateIdentifierKey(DroolsSoftKeywords.NOT))))) {
                int LA85_1 = input.LA(2);

                if ( (LA85_1==ID) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                    alt85=2;
                }
                else if ( (LA85_1==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.IN)))) {
                    alt85=1;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("798:4: ( in_key | not_key in_key )", 85, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("798:4: ( in_key | not_key in_key )", 85, 0, input);

                throw nvae;
            }
            switch (alt85) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:6: in_key
                    {
                    pushFollow(FOLLOW_in_key_in_compound_operator3527);
                    in_key269=in_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key269.getTree(), root_0);

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:16: not_key in_key
                    {
                    pushFollow(FOLLOW_not_key_in_compound_operator3532);
                    not_key270=not_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, not_key270.getTree());
                    pushFollow(FOLLOW_in_key_in_compound_operator3534);
                    in_key271=in_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key271.getTree(), root_0);

                    }
                    break;

            }

            LEFT_PAREN272=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator3539); if (failed) return retval;
            pushFollow(FOLLOW_expression_value_in_compound_operator3542);
            expression_value273=expression_value();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression_value273.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:63: ( COMMA expression_value )*
            loop86:
            do {
                int alt86=2;
                int LA86_0 = input.LA(1);

                if ( (LA86_0==COMMA) ) {
                    alt86=1;
                }


                switch (alt86) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:65: COMMA expression_value
            	    {
            	    COMMA274=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_compound_operator3546); if (failed) return retval;
            	    pushFollow(FOLLOW_expression_value_in_compound_operator3549);
            	    expression_value275=expression_value();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, expression_value275.getTree());

            	    }
            	    break;

            	default :
            	    break loop86;
                }
            } while (true);

            RIGHT_PAREN276=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator3554); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_PAREN276_tree = (Object)adaptor.create(RIGHT_PAREN276);
            adaptor.addChild(root_0, RIGHT_PAREN276_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end compound_operator

    public static class expression_value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression_value
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:801:1: expression_value : ( accessor_path | literal_constraint | paren_chunk );
    public final expression_value_return expression_value() throws RecognitionException {
        expression_value_return retval = new expression_value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        accessor_path_return accessor_path277 = null;

        literal_constraint_return literal_constraint278 = null;

        paren_chunk_return paren_chunk279 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:802:2: ( accessor_path | literal_constraint | paren_chunk )
            int alt87=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt87=1;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt87=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt87=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("801:1: expression_value : ( accessor_path | literal_constraint | paren_chunk );", 87, 0, input);

                throw nvae;
            }

            switch (alt87) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:802:4: accessor_path
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_accessor_path_in_expression_value3565);
                    accessor_path277=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, accessor_path277.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:4: literal_constraint
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_constraint_in_expression_value3570);
                    literal_constraint278=literal_constraint();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, literal_constraint278.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:804:4: paren_chunk
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_paren_chunk_in_expression_value3576);
                    paren_chunk279=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk279.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression_value

    public static class literal_constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start literal_constraint
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:807:1: literal_constraint : ( STRING | INT | FLOAT | BOOL | NULL );
    public final literal_constraint_return literal_constraint() throws RecognitionException {
        literal_constraint_return retval = new literal_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set280=null;

        Object set280_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:808:2: ( STRING | INT | FLOAT | BOOL | NULL )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
            {
            root_0 = (Object)adaptor.nil();

            set280=(Token)input.LT(1);
            if ( input.LA(1)==STRING||(input.LA(1)>=BOOL && input.LA(1)<=INT)||(input.LA(1)>=FLOAT && input.LA(1)<=NULL) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set280));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_literal_constraint0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end literal_constraint

    public static class pattern_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start pattern_type
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:815:1: pattern_type : id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final pattern_type_return pattern_type() throws RecognitionException {
        pattern_type_return retval = new pattern_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;
        dimension_definition_return dimension_definition281 = null;


        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:816:2: (id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:816:4: id+= ID (id+= DOT id+= ID )* ( dimension_definition )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_pattern_type3620); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:816:11: (id+= DOT id+= ID )*
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);

                if ( (LA88_0==DOT) ) {
                    alt88=1;
                }


                switch (alt88) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:816:13: id+= DOT id+= ID
            	    {
            	    id=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pattern_type3626); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_pattern_type3630); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop88;
                }
            } while (true);

            if ( backtracking==0 ) {
              	setParaphrasesValue(DroolsParaphareseTypes.PATTERN, buildStringFromTokens(list_id));	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:818:6: ( dimension_definition )*
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( (LA89_0==LEFT_SQUARE) ) {
                    alt89=1;
                }


                switch (alt89) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:818:6: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_pattern_type3645);
            	    dimension_definition281=dimension_definition();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_dimension_definition.add(dimension_definition281.getTree());

            	    }
            	    break;

            	default :
            	    break loop89;
                }
            } while (true);


            // AST REWRITE
            // elements: ID, dimension_definition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 819:3: -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:819:6: ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_PATTERN_TYPE, "VT_PATTERN_TYPE"), root_1);

                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, stream_ID.next());

                }
                stream_ID.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:819:28: ( dimension_definition )*
                while ( stream_dimension_definition.hasNext() ) {
                    adaptor.addChild(root_1, stream_dimension_definition.next());

                }
                stream_dimension_definition.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end pattern_type

    public static class data_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start data_type
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:1: data_type : ID ( DOT ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final data_type_return data_type() throws RecognitionException {
        data_type_return retval = new data_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID282=null;
        Token DOT283=null;
        Token ID284=null;
        dimension_definition_return dimension_definition285 = null;


        Object ID282_tree=null;
        Object DOT283_tree=null;
        Object ID284_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:2: ( ID ( DOT ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:4: ID ( DOT ID )* ( dimension_definition )*
            {
            ID282=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_data_type3671); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID282);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:7: ( DOT ID )*
            loop90:
            do {
                int alt90=2;
                int LA90_0 = input.LA(1);

                if ( (LA90_0==DOT) ) {
                    alt90=1;
                }


                switch (alt90) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:9: DOT ID
            	    {
            	    DOT283=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_data_type3675); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(DOT283);

            	    ID284=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_data_type3677); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(ID284);


            	    }
            	    break;

            	default :
            	    break loop90;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:19: ( dimension_definition )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==LEFT_SQUARE) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:19: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_data_type3682);
            	    dimension_definition285=dimension_definition();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_dimension_definition.add(dimension_definition285.getTree());

            	    }
            	    break;

            	default :
            	    break loop91;
                }
            } while (true);


            // AST REWRITE
            // elements: dimension_definition, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 824:3: -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:824:6: ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_DATA_TYPE, "VT_DATA_TYPE"), root_1);

                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, stream_ID.next());

                }
                stream_ID.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:824:25: ( dimension_definition )*
                while ( stream_dimension_definition.hasNext() ) {
                    adaptor.addChild(root_1, stream_dimension_definition.next());

                }
                stream_dimension_definition.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end data_type

    public static class dimension_definition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start dimension_definition
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:1: dimension_definition : LEFT_SQUARE RIGHT_SQUARE ;
    public final dimension_definition_return dimension_definition() throws RecognitionException {
        dimension_definition_return retval = new dimension_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE286=null;
        Token RIGHT_SQUARE287=null;

        Object LEFT_SQUARE286_tree=null;
        Object RIGHT_SQUARE287_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:828:2: ( LEFT_SQUARE RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:828:4: LEFT_SQUARE RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE286=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dimension_definition3708); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_SQUARE286_tree = (Object)adaptor.create(LEFT_SQUARE286);
            adaptor.addChild(root_0, LEFT_SQUARE286_tree);
            }
            RIGHT_SQUARE287=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dimension_definition3710); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_SQUARE287_tree = (Object)adaptor.create(RIGHT_SQUARE287);
            adaptor.addChild(root_0, RIGHT_SQUARE287_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end dimension_definition

    public static class accessor_path_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start accessor_path
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:831:1: accessor_path : accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) ;
    public final accessor_path_return accessor_path() throws RecognitionException {
        accessor_path_return retval = new accessor_path_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT289=null;
        accessor_element_return accessor_element288 = null;

        accessor_element_return accessor_element290 = null;


        Object DOT289_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_accessor_element=new RewriteRuleSubtreeStream(adaptor,"rule accessor_element");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:832:2: ( accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:832:4: accessor_element ( DOT accessor_element )*
            {
            pushFollow(FOLLOW_accessor_element_in_accessor_path3721);
            accessor_element288=accessor_element();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_accessor_element.add(accessor_element288.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:832:21: ( DOT accessor_element )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==DOT) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:832:23: DOT accessor_element
            	    {
            	    DOT289=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_accessor_path3725); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(DOT289);

            	    pushFollow(FOLLOW_accessor_element_in_accessor_path3727);
            	    accessor_element290=accessor_element();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_accessor_element.add(accessor_element290.getTree());

            	    }
            	    break;

            	default :
            	    break loop92;
                }
            } while (true);


            // AST REWRITE
            // elements: accessor_element
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 833:2: -> ^( VT_ACCESSOR_PATH ( accessor_element )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:833:5: ^( VT_ACCESSOR_PATH ( accessor_element )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ACCESSOR_PATH, "VT_ACCESSOR_PATH"), root_1);

                if ( !(stream_accessor_element.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_accessor_element.hasNext() ) {
                    adaptor.addChild(root_1, stream_accessor_element.next());

                }
                stream_accessor_element.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end accessor_path

    public static class accessor_element_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start accessor_element
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:836:1: accessor_element : ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) ;
    public final accessor_element_return accessor_element() throws RecognitionException {
        accessor_element_return retval = new accessor_element_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID291=null;
        square_chunk_return square_chunk292 = null;


        Object ID291_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:837:2: ( ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:837:4: ID ( square_chunk )*
            {
            ID291=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accessor_element3751); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID291);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:837:7: ( square_chunk )*
            loop93:
            do {
                int alt93=2;
                int LA93_0 = input.LA(1);

                if ( (LA93_0==LEFT_SQUARE) ) {
                    alt93=1;
                }


                switch (alt93) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:837:7: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_accessor_element3753);
            	    square_chunk292=square_chunk();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_square_chunk.add(square_chunk292.getTree());

            	    }
            	    break;

            	default :
            	    break loop93;
                }
            } while (true);


            // AST REWRITE
            // elements: ID, square_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 838:2: -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:838:5: ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ACCESSOR_ELEMENT, "VT_ACCESSOR_ELEMENT"), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:838:30: ( square_chunk )*
                while ( stream_square_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_square_chunk.next());

                }
                stream_square_chunk.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end accessor_element

    public static class rhs_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rhs_chunk
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:841:1: rhs_chunk : rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] ;
    public final rhs_chunk_return rhs_chunk() throws RecognitionException {
        rhs_chunk_return retval = new rhs_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        rhs_chunk_data_return rc = null;


        RewriteRuleSubtreeStream stream_rhs_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:844:3: (rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:844:5: rc= rhs_chunk_data
            {
            pushFollow(FOLLOW_rhs_chunk_data_in_rhs_chunk3782);
            rc=rhs_chunk_data();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rhs_chunk_data.add(rc.getTree());
            if ( backtracking==0 ) {
              text = input.toString(rc.start,rc.stop);
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 845:2: -> VT_RHS_CHUNK[$rc.start,text]
            {
                adaptor.addChild(root_0, adaptor.create(VT_RHS_CHUNK, ((Token)rc.start), text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end rhs_chunk

    public static class rhs_chunk_data_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rhs_chunk_data
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:848:1: rhs_chunk_data : THEN (~ END )* END ( SEMICOLON )? ;
    public final rhs_chunk_data_return rhs_chunk_data() throws RecognitionException {
        rhs_chunk_data_return retval = new rhs_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token THEN293=null;
        Token set294=null;
        Token END295=null;
        Token SEMICOLON296=null;

        Object THEN293_tree=null;
        Object set294_tree=null;
        Object END295_tree=null;
        Object SEMICOLON296_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:2: ( THEN (~ END )* END ( SEMICOLON )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:4: THEN (~ END )* END ( SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            THEN293=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk_data3801); if (failed) return retval;
            if ( backtracking==0 ) {
            THEN293_tree = (Object)adaptor.create(THEN293);
            adaptor.addChild(root_0, THEN293_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:9: (~ END )*
            loop94:
            do {
                int alt94=2;
                int LA94_0 = input.LA(1);

                if ( ((LA94_0>=VT_COMPILATION_UNIT && LA94_0<=DOT_STAR)||(LA94_0>=STRING && LA94_0<=MULTI_LINE_COMMENT)) ) {
                    alt94=1;
                }


                switch (alt94) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:11: ~ END
            	    {
            	    set294=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=DOT_STAR)||(input.LA(1)>=STRING && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set294));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk_data3805);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop94;
                }
            } while (true);

            END295=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_rhs_chunk_data3811); if (failed) return retval;
            if ( backtracking==0 ) {
            END295_tree = (Object)adaptor.create(END295);
            adaptor.addChild(root_0, END295_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:23: ( SEMICOLON )?
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==SEMICOLON) ) {
                alt95=1;
            }
            switch (alt95) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:23: SEMICOLON
                    {
                    SEMICOLON296=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_rhs_chunk_data3813); if (failed) return retval;
                    if ( backtracking==0 ) {
                    SEMICOLON296_tree = (Object)adaptor.create(SEMICOLON296);
                    adaptor.addChild(root_0, SEMICOLON296_tree);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end rhs_chunk_data

    public static class curly_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start curly_chunk
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:852:1: curly_chunk : cc= curly_chunk_data -> VT_CURLY_CHUNK[$cc.start,text] ;
    public final curly_chunk_return curly_chunk() throws RecognitionException {
        curly_chunk_return retval = new curly_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        curly_chunk_data_return cc = null;


        RewriteRuleSubtreeStream stream_curly_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule curly_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:855:3: (cc= curly_chunk_data -> VT_CURLY_CHUNK[$cc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:855:5: cc= curly_chunk_data
            {
            pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk3830);
            cc=curly_chunk_data();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_curly_chunk_data.add(cc.getTree());
            if ( backtracking==0 ) {
              text = input.toString(cc.start,cc.stop);
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 856:2: -> VT_CURLY_CHUNK[$cc.start,text]
            {
                adaptor.addChild(root_0, adaptor.create(VT_CURLY_CHUNK, ((Token)cc.start), text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end curly_chunk

    public static class curly_chunk_data_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start curly_chunk_data
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:859:1: curly_chunk_data : LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )* RIGHT_CURLY ;
    public final curly_chunk_data_return curly_chunk_data() throws RecognitionException {
        curly_chunk_data_return retval = new curly_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_CURLY297=null;
        Token set298=null;
        Token RIGHT_CURLY300=null;
        curly_chunk_data_return curly_chunk_data299 = null;


        Object LEFT_CURLY297_tree=null;
        Object set298_tree=null;
        Object RIGHT_CURLY300_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:860:2: ( LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )* RIGHT_CURLY )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:860:4: LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )* RIGHT_CURLY
            {
            root_0 = (Object)adaptor.nil();

            LEFT_CURLY297=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk_data3849); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_CURLY297_tree = (Object)adaptor.create(LEFT_CURLY297);
            adaptor.addChild(root_0, LEFT_CURLY297_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:860:15: (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )*
            loop96:
            do {
                int alt96=3;
                int LA96_0 = input.LA(1);

                if ( ((LA96_0>=VT_COMPILATION_UNIT && LA96_0<=THEN)||(LA96_0>=MISC && LA96_0<=MULTI_LINE_COMMENT)) ) {
                    alt96=1;
                }
                else if ( (LA96_0==LEFT_CURLY) ) {
                    alt96=2;
                }


                switch (alt96) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:860:16: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    set298=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=THEN)||(input.LA(1)>=MISC && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set298));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk_data3852);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:860:49: curly_chunk_data
            	    {
            	    pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk_data3866);
            	    curly_chunk_data299=curly_chunk_data();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, curly_chunk_data299.getTree());

            	    }
            	    break;

            	default :
            	    break loop96;
                }
            } while (true);

            RIGHT_CURLY300=(Token)input.LT(1);
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk_data3871); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_CURLY300_tree = (Object)adaptor.create(RIGHT_CURLY300);
            adaptor.addChild(root_0, RIGHT_CURLY300_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end curly_chunk_data

    public static class paren_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start paren_chunk
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:863:1: paren_chunk : pc= paren_chunk_data -> VT_PAREN_CHUNK[$pc.start,text] ;
    public final paren_chunk_return paren_chunk() throws RecognitionException {
        paren_chunk_return retval = new paren_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        paren_chunk_data_return pc = null;


        RewriteRuleSubtreeStream stream_paren_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:866:3: (pc= paren_chunk_data -> VT_PAREN_CHUNK[$pc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:866:5: pc= paren_chunk_data
            {
            pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk3887);
            pc=paren_chunk_data();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk_data.add(pc.getTree());
            if ( backtracking==0 ) {
              text = input.toString(pc.start,pc.stop);
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 867:2: -> VT_PAREN_CHUNK[$pc.start,text]
            {
                adaptor.addChild(root_0, adaptor.create(VT_PAREN_CHUNK, ((Token)pc.start), text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end paren_chunk

    public static class paren_chunk_data_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start paren_chunk_data
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:870:1: paren_chunk_data : LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )* RIGHT_PAREN ;
    public final paren_chunk_data_return paren_chunk_data() throws RecognitionException {
        paren_chunk_data_return retval = new paren_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN301=null;
        Token set302=null;
        Token RIGHT_PAREN304=null;
        paren_chunk_data_return paren_chunk_data303 = null;


        Object LEFT_PAREN301_tree=null;
        Object set302_tree=null;
        Object RIGHT_PAREN304_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:2: ( LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )* RIGHT_PAREN )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:4: LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )* RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            LEFT_PAREN301=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk_data3907); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_PAREN301_tree = (Object)adaptor.create(LEFT_PAREN301);
            adaptor.addChild(root_0, LEFT_PAREN301_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:15: (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )*
            loop97:
            do {
                int alt97=3;
                int LA97_0 = input.LA(1);

                if ( ((LA97_0>=VT_COMPILATION_UNIT && LA97_0<=STRING)||LA97_0==COMMA||(LA97_0>=AT && LA97_0<=MULTI_LINE_COMMENT)) ) {
                    alt97=1;
                }
                else if ( (LA97_0==LEFT_PAREN) ) {
                    alt97=2;
                }


                switch (alt97) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:16: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    set302=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=STRING)||input.LA(1)==COMMA||(input.LA(1)>=AT && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set302));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk_data3910);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:49: paren_chunk_data
            	    {
            	    pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk_data3924);
            	    paren_chunk_data303=paren_chunk_data();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk_data303.getTree());

            	    }
            	    break;

            	default :
            	    break loop97;
                }
            } while (true);

            RIGHT_PAREN304=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk_data3929); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_PAREN304_tree = (Object)adaptor.create(RIGHT_PAREN304);
            adaptor.addChild(root_0, RIGHT_PAREN304_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end paren_chunk_data

    public static class square_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start square_chunk
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:1: square_chunk : sc= square_chunk_data -> VT_SQUARE_CHUNK[$sc.start,text] ;
    public final square_chunk_return square_chunk() throws RecognitionException {
        square_chunk_return retval = new square_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        square_chunk_data_return sc = null;


        RewriteRuleSubtreeStream stream_square_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:877:3: (sc= square_chunk_data -> VT_SQUARE_CHUNK[$sc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:877:5: sc= square_chunk_data
            {
            pushFollow(FOLLOW_square_chunk_data_in_square_chunk3946);
            sc=square_chunk_data();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_square_chunk_data.add(sc.getTree());
            if ( backtracking==0 ) {
              text = input.toString(sc.start,sc.stop);
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 878:2: -> VT_SQUARE_CHUNK[$sc.start,text]
            {
                adaptor.addChild(root_0, adaptor.create(VT_SQUARE_CHUNK, ((Token)sc.start), text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end square_chunk

    public static class square_chunk_data_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start square_chunk_data
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:881:1: square_chunk_data : LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )* RIGHT_SQUARE ;
    public final square_chunk_data_return square_chunk_data() throws RecognitionException {
        square_chunk_data_return retval = new square_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE305=null;
        Token set306=null;
        Token RIGHT_SQUARE308=null;
        square_chunk_data_return square_chunk_data307 = null;


        Object LEFT_SQUARE305_tree=null;
        Object set306_tree=null;
        Object RIGHT_SQUARE308_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:882:2: ( LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )* RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:882:4: LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )* RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE305=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk_data3965); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_SQUARE305_tree = (Object)adaptor.create(LEFT_SQUARE305);
            adaptor.addChild(root_0, LEFT_SQUARE305_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:882:16: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )*
            loop98:
            do {
                int alt98=3;
                int LA98_0 = input.LA(1);

                if ( ((LA98_0>=VT_COMPILATION_UNIT && LA98_0<=NULL)||(LA98_0>=THEN && LA98_0<=MULTI_LINE_COMMENT)) ) {
                    alt98=1;
                }
                else if ( (LA98_0==LEFT_SQUARE) ) {
                    alt98=2;
                }


                switch (alt98) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:882:17: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    set306=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=NULL)||(input.LA(1)>=THEN && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set306));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk_data3968);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:882:52: square_chunk_data
            	    {
            	    pushFollow(FOLLOW_square_chunk_data_in_square_chunk_data3982);
            	    square_chunk_data307=square_chunk_data();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk_data307.getTree());

            	    }
            	    break;

            	default :
            	    break loop98;
                }
            } while (true);

            RIGHT_SQUARE308=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk_data3987); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_SQUARE308_tree = (Object)adaptor.create(RIGHT_SQUARE308);
            adaptor.addChild(root_0, RIGHT_SQUARE308_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end square_chunk_data

    public static class date_effective_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start date_effective_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:886:1: date_effective_key : {...}? => ID MISC ID -> VK_DATE_EFFECTIVE[$start, text] ;
    public final date_effective_key_return date_effective_key() throws RecognitionException {
        date_effective_key_return retval = new date_effective_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID309=null;
        Token MISC310=null;
        Token ID311=null;

        Object ID309_tree=null;
        Object MISC310_tree=null;
        Object ID311_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:889:3: ({...}? => ID MISC ID -> VK_DATE_EFFECTIVE[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:889:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "date_effective_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))");
            }
            ID309=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_effective_key4006); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID309);

            MISC310=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_date_effective_key4008); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC310);

            ID311=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_effective_key4010); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID311);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 890:2: -> VK_DATE_EFFECTIVE[$start, text]
            {
                adaptor.addChild(root_0, adaptor.create(VK_DATE_EFFECTIVE, ((Token)retval.start),  text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end date_effective_key

    public static class date_expires_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start date_expires_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:893:1: date_expires_key : {...}? => ID MISC ID -> VK_DATE_EXPIRES[$start, text] ;
    public final date_expires_key_return date_expires_key() throws RecognitionException {
        date_expires_key_return retval = new date_expires_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID312=null;
        Token MISC313=null;
        Token ID314=null;

        Object ID312_tree=null;
        Object MISC313_tree=null;
        Object ID314_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:896:3: ({...}? => ID MISC ID -> VK_DATE_EXPIRES[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:896:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "date_expires_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EXPIRES))");
            }
            ID312=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_expires_key4036); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID312);

            MISC313=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_date_expires_key4038); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC313);

            ID314=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_expires_key4040); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID314);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 897:2: -> VK_DATE_EXPIRES[$start, text]
            {
                adaptor.addChild(root_0, adaptor.create(VK_DATE_EXPIRES, ((Token)retval.start),  text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end date_expires_key

    public static class lock_on_active_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lock_on_active_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:900:1: lock_on_active_key : {...}? => ID MISC ID MISC ID -> VK_LOCK_ON_ACTIVE[$start, text] ;
    public final lock_on_active_key_return lock_on_active_key() throws RecognitionException {
        lock_on_active_key_return retval = new lock_on_active_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID315=null;
        Token MISC316=null;
        Token ID317=null;
        Token MISC318=null;
        Token ID319=null;

        Object ID315_tree=null;
        Object MISC316_tree=null;
        Object ID317_tree=null;
        Object MISC318_tree=null;
        Object ID319_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:903:3: ({...}? => ID MISC ID MISC ID -> VK_LOCK_ON_ACTIVE[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:903:5: {...}? => ID MISC ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "lock_on_active_key", "(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, \"-\") && validateLT(5, DroolsSoftKeywords.ACTIVE))");
            }
            ID315=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key4066); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID315);

            MISC316=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_lock_on_active_key4068); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC316);

            ID317=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key4070); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID317);

            MISC318=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_lock_on_active_key4072); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC318);

            ID319=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key4074); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID319);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 904:2: -> VK_LOCK_ON_ACTIVE[$start, text]
            {
                adaptor.addChild(root_0, adaptor.create(VK_LOCK_ON_ACTIVE, ((Token)retval.start),  text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end lock_on_active_key

    public static class no_loop_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start no_loop_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:907:1: no_loop_key : {...}? => ID MISC ID -> VK_NO_LOOP[$start, text] ;
    public final no_loop_key_return no_loop_key() throws RecognitionException {
        no_loop_key_return retval = new no_loop_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID320=null;
        Token MISC321=null;
        Token ID322=null;

        Object ID320_tree=null;
        Object MISC321_tree=null;
        Object ID322_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:910:3: ({...}? => ID MISC ID -> VK_NO_LOOP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:910:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "no_loop_key", "(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.LOOP))");
            }
            ID320=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_no_loop_key4100); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID320);

            MISC321=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_no_loop_key4102); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC321);

            ID322=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_no_loop_key4104); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID322);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 911:2: -> VK_NO_LOOP[$start, text]
            {
                adaptor.addChild(root_0, adaptor.create(VK_NO_LOOP, ((Token)retval.start),  text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end no_loop_key

    public static class auto_focus_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start auto_focus_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:914:1: auto_focus_key : {...}? => ID MISC ID -> VK_AUTO_FOCUS[$start, text] ;
    public final auto_focus_key_return auto_focus_key() throws RecognitionException {
        auto_focus_key_return retval = new auto_focus_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID323=null;
        Token MISC324=null;
        Token ID325=null;

        Object ID323_tree=null;
        Object MISC324_tree=null;
        Object ID325_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:917:3: ({...}? => ID MISC ID -> VK_AUTO_FOCUS[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:917:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "auto_focus_key", "(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.FOCUS))");
            }
            ID323=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_auto_focus_key4130); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID323);

            MISC324=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_auto_focus_key4132); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC324);

            ID325=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_auto_focus_key4134); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID325);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 918:2: -> VK_AUTO_FOCUS[$start, text]
            {
                adaptor.addChild(root_0, adaptor.create(VK_AUTO_FOCUS, ((Token)retval.start),  text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end auto_focus_key

    public static class activation_group_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start activation_group_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:921:1: activation_group_key : {...}? => ID MISC ID -> VK_ACTIVATION_GROUP[$start, text] ;
    public final activation_group_key_return activation_group_key() throws RecognitionException {
        activation_group_key_return retval = new activation_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID326=null;
        Token MISC327=null;
        Token ID328=null;

        Object ID326_tree=null;
        Object MISC327_tree=null;
        Object ID328_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:924:3: ({...}? => ID MISC ID -> VK_ACTIVATION_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:924:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "activation_group_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            ID326=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_activation_group_key4160); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID326);

            MISC327=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_activation_group_key4162); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC327);

            ID328=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_activation_group_key4164); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID328);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 925:2: -> VK_ACTIVATION_GROUP[$start, text]
            {
                adaptor.addChild(root_0, adaptor.create(VK_ACTIVATION_GROUP, ((Token)retval.start),  text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end activation_group_key

    public static class agenda_group_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start agenda_group_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:928:1: agenda_group_key : {...}? => ID MISC ID -> VK_AGENDA_GROUP[$start, text] ;
    public final agenda_group_key_return agenda_group_key() throws RecognitionException {
        agenda_group_key_return retval = new agenda_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID329=null;
        Token MISC330=null;
        Token ID331=null;

        Object ID329_tree=null;
        Object MISC330_tree=null;
        Object ID331_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:931:3: ({...}? => ID MISC ID -> VK_AGENDA_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:931:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "agenda_group_key", "(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            ID329=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_agenda_group_key4190); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID329);

            MISC330=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_agenda_group_key4192); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC330);

            ID331=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_agenda_group_key4194); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID331);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 932:2: -> VK_AGENDA_GROUP[$start, text]
            {
                adaptor.addChild(root_0, adaptor.create(VK_AGENDA_GROUP, ((Token)retval.start),  text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end agenda_group_key

    public static class ruleflow_group_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start ruleflow_group_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:935:1: ruleflow_group_key : {...}? => ID MISC ID -> VK_RULEFLOW_GROUP[$start, text] ;
    public final ruleflow_group_key_return ruleflow_group_key() throws RecognitionException {
        ruleflow_group_key_return retval = new ruleflow_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID332=null;
        Token MISC333=null;
        Token ID334=null;

        Object ID332_tree=null;
        Object MISC333_tree=null;
        Object ID334_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:938:3: ({...}? => ID MISC ID -> VK_RULEFLOW_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:938:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "ruleflow_group_key", "(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            ID332=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_ruleflow_group_key4220); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID332);

            MISC333=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_ruleflow_group_key4222); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC333);

            ID334=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_ruleflow_group_key4224); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID334);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 939:2: -> VK_RULEFLOW_GROUP[$start, text]
            {
                adaptor.addChild(root_0, adaptor.create(VK_RULEFLOW_GROUP, ((Token)retval.start),  text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end ruleflow_group_key

    public static class duration_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start duration_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:942:1: duration_key : {...}? =>id= ID -> VK_DURATION[$id] ;
    public final duration_key_return duration_key() throws RecognitionException {
        duration_key_return retval = new duration_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:943:2: ({...}? =>id= ID -> VK_DURATION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:943:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DURATION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "duration_key", "(validateIdentifierKey(DroolsSoftKeywords.DURATION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_duration_key4249); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 943:69: -> VK_DURATION[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_DURATION, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end duration_key

    public static class package_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start package_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:946:1: package_key : {...}? =>id= ID -> VK_PACKAGE[$id] ;
    public final package_key_return package_key() throws RecognitionException {
        package_key_return retval = new package_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:947:2: ({...}? =>id= ID -> VK_PACKAGE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:947:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.PACKAGE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "package_key", "(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_key4271); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 947:68: -> VK_PACKAGE[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_PACKAGE, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end package_key

    public static class import_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start import_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:950:1: import_key : {...}? =>id= ID -> VK_IMPORT[$id] ;
    public final import_key_return import_key() throws RecognitionException {
        import_key_return retval = new import_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:951:2: ({...}? =>id= ID -> VK_IMPORT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:951:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "import_key", "(validateIdentifierKey(DroolsSoftKeywords.IMPORT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_key4293); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 951:67: -> VK_IMPORT[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_IMPORT, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end import_key

    public static class dialect_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start dialect_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:954:1: dialect_key : {...}? =>id= ID -> VK_DIALECT[$id] ;
    public final dialect_key_return dialect_key() throws RecognitionException {
        dialect_key_return retval = new dialect_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:955:2: ({...}? =>id= ID -> VK_DIALECT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:955:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DIALECT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "dialect_key", "(validateIdentifierKey(DroolsSoftKeywords.DIALECT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dialect_key4315); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 955:68: -> VK_DIALECT[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_DIALECT, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end dialect_key

    public static class salience_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start salience_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:958:1: salience_key : {...}? =>id= ID -> VK_SALIENCE[$id] ;
    public final salience_key_return salience_key() throws RecognitionException {
        salience_key_return retval = new salience_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:959:2: ({...}? =>id= ID -> VK_SALIENCE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:959:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "salience_key", "(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_salience_key4337); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 959:69: -> VK_SALIENCE[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_SALIENCE, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end salience_key

    public static class enabled_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start enabled_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:962:1: enabled_key : {...}? =>id= ID -> VK_ENABLED[$id] ;
    public final enabled_key_return enabled_key() throws RecognitionException {
        enabled_key_return retval = new enabled_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:2: ({...}? =>id= ID -> VK_ENABLED[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ENABLED))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "enabled_key", "(validateIdentifierKey(DroolsSoftKeywords.ENABLED))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enabled_key4359); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 963:68: -> VK_ENABLED[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_ENABLED, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end enabled_key

    public static class attributes_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start attributes_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:966:1: attributes_key : {...}? =>id= ID -> VK_ATTRIBUTES[$id] ;
    public final attributes_key_return attributes_key() throws RecognitionException {
        attributes_key_return retval = new attributes_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:967:2: ({...}? =>id= ID -> VK_ATTRIBUTES[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:967:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "attributes_key", "(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_attributes_key4381); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 967:71: -> VK_ATTRIBUTES[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_ATTRIBUTES, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end attributes_key

    public static class when_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start when_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:970:1: when_key : {...}? =>id= ID -> VK_WHEN[$id] ;
    public final when_key_return when_key() throws RecognitionException {
        when_key_return retval = new when_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:971:2: ({...}? =>id= ID -> VK_WHEN[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:971:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "when_key", "(validateIdentifierKey(DroolsSoftKeywords.WHEN))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_when_key4403); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 971:65: -> VK_WHEN[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_WHEN, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end when_key

    public static class rule_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rule_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:974:1: rule_key : {...}? =>id= ID -> VK_RULE[$id] ;
    public final rule_key_return rule_key() throws RecognitionException {
        rule_key_return retval = new rule_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:975:2: ({...}? =>id= ID -> VK_RULE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:975:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "rule_key", "(validateIdentifierKey(DroolsSoftKeywords.RULE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_rule_key4425); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 975:65: -> VK_RULE[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_RULE, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end rule_key

    public static class template_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start template_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:1: template_key : {...}? =>id= ID -> VK_TEMPLATE[$id] ;
    public final template_key_return template_key() throws RecognitionException {
        template_key_return retval = new template_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:979:2: ({...}? =>id= ID -> VK_TEMPLATE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:979:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "template_key", "(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template_key4447); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 979:69: -> VK_TEMPLATE[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_TEMPLATE, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end template_key

    public static class query_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start query_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:982:1: query_key : {...}? =>id= ID -> VK_QUERY[$id] ;
    public final query_key_return query_key() throws RecognitionException {
        query_key_return retval = new query_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:983:2: ({...}? =>id= ID -> VK_QUERY[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:983:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "query_key", "(validateIdentifierKey(DroolsSoftKeywords.QUERY))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_query_key4469); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 983:66: -> VK_QUERY[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_QUERY, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end query_key

    public static class declare_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start declare_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:986:1: declare_key : {...}? =>id= ID -> VK_DECLARE[$id] ;
    public final declare_key_return declare_key() throws RecognitionException {
        declare_key_return retval = new declare_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:987:2: ({...}? =>id= ID -> VK_DECLARE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:987:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DECLARE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "declare_key", "(validateIdentifierKey(DroolsSoftKeywords.DECLARE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_declare_key4491); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 987:68: -> VK_DECLARE[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_DECLARE, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end declare_key

    public static class function_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start function_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:990:1: function_key : {...}? =>id= ID -> VK_FUNCTION[$id] ;
    public final function_key_return function_key() throws RecognitionException {
        function_key_return retval = new function_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:991:2: ({...}? =>id= ID -> VK_FUNCTION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:991:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "function_key", "(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function_key4513); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 991:69: -> VK_FUNCTION[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_FUNCTION, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end function_key

    public static class global_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start global_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:994:1: global_key : {...}? =>id= ID -> VK_GLOBAL[$id] ;
    public final global_key_return global_key() throws RecognitionException {
        global_key_return retval = new global_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:995:2: ({...}? =>id= ID -> VK_GLOBAL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:995:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "global_key", "(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global_key4535); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 995:67: -> VK_GLOBAL[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_GLOBAL, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end global_key

    public static class eval_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start eval_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:998:1: eval_key : {...}? =>id= ID -> VK_EVAL[$id] ;
    public final eval_key_return eval_key() throws RecognitionException {
        eval_key_return retval = new eval_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:2: ({...}? =>id= ID -> VK_EVAL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "eval_key", "(validateIdentifierKey(DroolsSoftKeywords.EVAL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_eval_key4557); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 999:65: -> VK_EVAL[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_EVAL, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end eval_key

    public static class contains_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start contains_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1002:1: contains_key : {...}? =>id= ID -> VK_CONTAINS[$id] ;
    public final contains_key_return contains_key() throws RecognitionException {
        contains_key_return retval = new contains_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1003:2: ({...}? =>id= ID -> VK_CONTAINS[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1003:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.CONTAINS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "contains_key", "(validateIdentifierKey(DroolsSoftKeywords.CONTAINS))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_contains_key4579); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1003:69: -> VK_CONTAINS[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_CONTAINS, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end contains_key

    public static class matches_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start matches_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1006:1: matches_key : {...}? =>id= ID -> VK_MATCHES[$id] ;
    public final matches_key_return matches_key() throws RecognitionException {
        matches_key_return retval = new matches_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1007:2: ({...}? =>id= ID -> VK_MATCHES[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1007:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.MATCHES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "matches_key", "(validateIdentifierKey(DroolsSoftKeywords.MATCHES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_matches_key4601); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1007:68: -> VK_MATCHES[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_MATCHES, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end matches_key

    public static class excludes_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start excludes_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1010:1: excludes_key : {...}? =>id= ID -> VK_EXCLUDES[$id] ;
    public final excludes_key_return excludes_key() throws RecognitionException {
        excludes_key_return retval = new excludes_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1011:2: ({...}? =>id= ID -> VK_EXCLUDES[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1011:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "excludes_key", "(validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_excludes_key4623); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1011:69: -> VK_EXCLUDES[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_EXCLUDES, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end excludes_key

    public static class soundslike_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start soundslike_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1014:1: soundslike_key : {...}? =>id= ID -> VK_SOUNDSLIKE[$id] ;
    public final soundslike_key_return soundslike_key() throws RecognitionException {
        soundslike_key_return retval = new soundslike_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1015:2: ({...}? =>id= ID -> VK_SOUNDSLIKE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1015:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "soundslike_key", "(validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_soundslike_key4645); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1015:71: -> VK_SOUNDSLIKE[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_SOUNDSLIKE, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end soundslike_key

    public static class memberof_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start memberof_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1018:1: memberof_key : {...}? =>id= ID -> VK_MEMBEROF[$id] ;
    public final memberof_key_return memberof_key() throws RecognitionException {
        memberof_key_return retval = new memberof_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1019:2: ({...}? =>id= ID -> VK_MEMBEROF[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1019:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "memberof_key", "(validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_memberof_key4667); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1019:69: -> VK_MEMBEROF[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_MEMBEROF, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end memberof_key

    public static class not_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start not_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1022:1: not_key : {...}? =>id= ID -> VK_NOT[$id] ;
    public final not_key_return not_key() throws RecognitionException {
        not_key_return retval = new not_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1023:2: ({...}? =>id= ID -> VK_NOT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1023:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "not_key", "(validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_not_key4689); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1023:64: -> VK_NOT[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_NOT, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end not_key

    public static class in_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start in_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1026:1: in_key : {...}? =>id= ID -> VK_IN[$id] ;
    public final in_key_return in_key() throws RecognitionException {
        in_key_return retval = new in_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1027:2: ({...}? =>id= ID -> VK_IN[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1027:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "in_key", "(validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_in_key4711); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1027:63: -> VK_IN[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_IN, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end in_key

    public static class or_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start or_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1030:1: or_key : {...}? =>id= ID -> VK_OR[$id] ;
    public final or_key_return or_key() throws RecognitionException {
        or_key_return retval = new or_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1031:2: ({...}? =>id= ID -> VK_OR[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1031:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.OR))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "or_key", "(validateIdentifierKey(DroolsSoftKeywords.OR))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_or_key4733); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1031:63: -> VK_OR[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_OR, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end or_key

    public static class and_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start and_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1034:1: and_key : {...}? =>id= ID -> VK_AND[$id] ;
    public final and_key_return and_key() throws RecognitionException {
        and_key_return retval = new and_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1035:2: ({...}? =>id= ID -> VK_AND[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1035:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AND))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "and_key", "(validateIdentifierKey(DroolsSoftKeywords.AND))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_and_key4755); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1035:64: -> VK_AND[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_AND, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end and_key

    public static class exists_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start exists_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1038:1: exists_key : {...}? =>id= ID -> VK_EXISTS[$id] ;
    public final exists_key_return exists_key() throws RecognitionException {
        exists_key_return retval = new exists_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1039:2: ({...}? =>id= ID -> VK_EXISTS[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1039:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EXISTS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "exists_key", "(validateIdentifierKey(DroolsSoftKeywords.EXISTS))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_exists_key4777); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1039:67: -> VK_EXISTS[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_EXISTS, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end exists_key

    public static class forall_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start forall_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1042:1: forall_key : {...}? =>id= ID -> VK_FORALL[$id] ;
    public final forall_key_return forall_key() throws RecognitionException {
        forall_key_return retval = new forall_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1043:2: ({...}? =>id= ID -> VK_FORALL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1043:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.FORALL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "forall_key", "(validateIdentifierKey(DroolsSoftKeywords.FORALL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_forall_key4799); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1043:67: -> VK_FORALL[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_FORALL, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end forall_key

    public static class from_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start from_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1046:1: from_key : {...}? =>id= ID -> VK_FROM[$id] ;
    public final from_key_return from_key() throws RecognitionException {
        from_key_return retval = new from_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1047:2: ({...}? =>id= ID -> VK_FROM[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1047:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "from_key", "(validateIdentifierKey(DroolsSoftKeywords.FROM))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_from_key4821); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1047:65: -> VK_FROM[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_FROM, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end from_key

    public static class entry_point_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start entry_point_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1050:1: entry_point_key : {...}? => ID MISC ID -> VK_ENTRY_POINT[$start, text] ;
    public final entry_point_key_return entry_point_key() throws RecognitionException {
        entry_point_key_return retval = new entry_point_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID335=null;
        Token MISC336=null;
        Token ID337=null;

        Object ID335_tree=null;
        Object MISC336_tree=null;
        Object ID337_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1053:3: ({...}? => ID MISC ID -> VK_ENTRY_POINT[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1053:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "entry_point_key", "(validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.POINT))");
            }
            ID335=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_entry_point_key4844); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID335);

            MISC336=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_entry_point_key4846); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC336);

            ID337=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_entry_point_key4848); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID337);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1054:2: -> VK_ENTRY_POINT[$start, text]
            {
                adaptor.addChild(root_0, adaptor.create(VK_ENTRY_POINT, ((Token)retval.start),  text));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end entry_point_key

    public static class accumulate_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start accumulate_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1057:1: accumulate_key : {...}? =>id= ID -> VK_ACCUMULATE[$id] ;
    public final accumulate_key_return accumulate_key() throws RecognitionException {
        accumulate_key_return retval = new accumulate_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1058:2: ({...}? =>id= ID -> VK_ACCUMULATE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1058:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "accumulate_key", "(validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accumulate_key4873); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1058:71: -> VK_ACCUMULATE[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_ACCUMULATE, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end accumulate_key

    public static class init_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start init_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1061:1: init_key : {...}? =>id= ID -> VK_INIT[$id] ;
    public final init_key_return init_key() throws RecognitionException {
        init_key_return retval = new init_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1062:2: ({...}? =>id= ID -> VK_INIT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1062:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.INIT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "init_key", "(validateIdentifierKey(DroolsSoftKeywords.INIT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_init_key4895); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1062:65: -> VK_INIT[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_INIT, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end init_key

    public static class action_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start action_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1065:1: action_key : {...}? =>id= ID -> VK_ACTION[$id] ;
    public final action_key_return action_key() throws RecognitionException {
        action_key_return retval = new action_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1066:2: ({...}? =>id= ID -> VK_ACTION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1066:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ACTION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "action_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_action_key4917); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1066:67: -> VK_ACTION[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_ACTION, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end action_key

    public static class reverse_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start reverse_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1069:1: reverse_key : {...}? =>id= ID -> VK_REVERSE[$id] ;
    public final reverse_key_return reverse_key() throws RecognitionException {
        reverse_key_return retval = new reverse_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1070:2: ({...}? =>id= ID -> VK_REVERSE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1070:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.REVERSE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "reverse_key", "(validateIdentifierKey(DroolsSoftKeywords.REVERSE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_reverse_key4939); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1070:68: -> VK_REVERSE[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_REVERSE, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end reverse_key

    public static class result_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start result_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1073:1: result_key : {...}? =>id= ID -> VK_RESULT[$id] ;
    public final result_key_return result_key() throws RecognitionException {
        result_key_return retval = new result_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1074:2: ({...}? =>id= ID -> VK_RESULT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1074:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.RESULT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "result_key", "(validateIdentifierKey(DroolsSoftKeywords.RESULT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_result_key4961); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1074:67: -> VK_RESULT[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_RESULT, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end result_key

    public static class collect_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start collect_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1077:1: collect_key : {...}? =>id= ID -> VK_COLLECT[$id] ;
    public final collect_key_return collect_key() throws RecognitionException {
        collect_key_return retval = new collect_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1078:2: ({...}? =>id= ID -> VK_COLLECT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1078:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.COLLECT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "collect_key", "(validateIdentifierKey(DroolsSoftKeywords.COLLECT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_collect_key4983); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1078:68: -> VK_COLLECT[$id]
            {
                adaptor.addChild(root_0, adaptor.create(VK_COLLECT, id));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end collect_key

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:5: ( LEFT_PAREN or_key )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:6: LEFT_PAREN or_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred11833); if (failed) return ;
        pushFollow(FOLLOW_or_key_in_synpred11835);
        or_key();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:5: ( or_key | DOUBLE_PIPE )
        int alt99=2;
        int LA99_0 = input.LA(1);

        if ( (LA99_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
            alt99=1;
        }
        else if ( (LA99_0==DOUBLE_PIPE) ) {
            alt99=2;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("555:5: synpred2 : ( or_key | DOUBLE_PIPE );", 99, 0, input);

            throw nvae;
        }
        switch (alt99) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:6: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred21882);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:13: DOUBLE_PIPE
                {
                match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred21884); if (failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred2

    // $ANTLR start synpred3
    public final void synpred3_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:5: ( LEFT_PAREN and_key )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:6: LEFT_PAREN and_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred31935); if (failed) return ;
        pushFollow(FOLLOW_and_key_in_synpred31937);
        and_key();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred3

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:5: ( and_key | DOUBLE_AMPER )
        int alt100=2;
        int LA100_0 = input.LA(1);

        if ( (LA100_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.AND)))) {
            alt100=1;
        }
        else if ( (LA100_0==DOUBLE_AMPER) ) {
            alt100=2;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("565:5: synpred4 : ( and_key | DOUBLE_AMPER );", 100, 0, input);

            throw nvae;
        }
        switch (alt100) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:6: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred41984);
                and_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:14: DOUBLE_AMPER
                {
                match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred41986); if (failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:578:4: ( SEMICOLON )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:578:5: SEMICOLON
        {
        match(input,SEMICOLON,FOLLOW_SEMICOLON_in_synpred52088); if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:12: ( LEFT_PAREN ( or_key | and_key ) )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:13: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred62120); if (failed) return ;
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:24: ( or_key | and_key )
        int alt101=2;
        int LA101_0 = input.LA(1);

        if ( (LA101_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            int LA101_1 = input.LA(2);

            if ( ((validateIdentifierKey(DroolsSoftKeywords.OR))) ) {
                alt101=1;
            }
            else if ( ((validateIdentifierKey(DroolsSoftKeywords.AND))) ) {
                alt101=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("583:24: ( or_key | and_key )", 101, 1, input);

                throw nvae;
            }
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("583:24: ( or_key | and_key )", 101, 0, input);

            throw nvae;
        }
        switch (alt101) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:25: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred62123);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:32: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred62125);
                and_key();
                _fsp--;
                if (failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred6

    // $ANTLR start synpred7
    public final void synpred7_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:591:5: ( LEFT_PAREN ( or_key | and_key ) )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:591:6: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred72207); if (failed) return ;
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:591:17: ( or_key | and_key )
        int alt102=2;
        int LA102_0 = input.LA(1);

        if ( (LA102_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            int LA102_1 = input.LA(2);

            if ( ((validateIdentifierKey(DroolsSoftKeywords.OR))) ) {
                alt102=1;
            }
            else if ( ((validateIdentifierKey(DroolsSoftKeywords.AND))) ) {
                alt102=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("591:17: ( or_key | and_key )", 102, 1, input);

                throw nvae;
            }
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("591:17: ( or_key | and_key )", 102, 0, input);

            throw nvae;
        }
        switch (alt102) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:591:18: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred72210);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:591:25: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred72212);
                and_key();
                _fsp--;
                if (failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred7

    // $ANTLR start synpred8
    public final void synpred8_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:674:5: ( LEFT_PAREN )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:674:6: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred82765); if (failed) return ;

        }
    }
    // $ANTLR end synpred8

    // $ANTLR start synpred9
    public final void synpred9_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:683:6: ( LEFT_SQUARE )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:683:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred92826); if (failed) return ;

        }
    }
    // $ANTLR end synpred9

    // $ANTLR start synpred10
    public final void synpred10_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:685:6: ( LEFT_PAREN )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:685:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred102848); if (failed) return ;

        }
    }
    // $ANTLR end synpred10

    public final boolean synpred4() {
        backtracking++;
        int start = input.mark();
        try {
            synpred4_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred9() {
        backtracking++;
        int start = input.mark();
        try {
            synpred9_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred7() {
        backtracking++;
        int start = input.mark();
        try {
            synpred7_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred2() {
        backtracking++;
        int start = input.mark();
        try {
            synpred2_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred3() {
        backtracking++;
        int start = input.mark();
        try {
            synpred3_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred1() {
        backtracking++;
        int start = input.mark();
        try {
            synpred1_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred5() {
        backtracking++;
        int start = input.mark();
        try {
            synpred5_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred6() {
        backtracking++;
        int start = input.mark();
        try {
            synpred6_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred8() {
        backtracking++;
        int start = input.mark();
        try {
            synpred8_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred10() {
        backtracking++;
        int start = input.mark();
        try {
            synpred10_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


    protected DFA12 dfa12 = new DFA12(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA28 dfa28 = new DFA28(this);
    protected DFA34 dfa34 = new DFA34(this);
    protected DFA51 dfa51 = new DFA51(this);
    protected DFA52 dfa52 = new DFA52(this);
    protected DFA59 dfa59 = new DFA59(this);
    protected DFA63 dfa63 = new DFA63(this);
    protected DFA65 dfa65 = new DFA65(this);
    protected DFA80 dfa80 = new DFA80(this);
    protected DFA81 dfa81 = new DFA81(this);
    static final String DFA12_eotS =
        "\16\uffff";
    static final String DFA12_eofS =
        "\16\uffff";
    static final String DFA12_minS =
        "\2\130\1\uffff\1\130\1\uffff\1\130\1\163\3\130\2\163\1\135\1\130";
    static final String DFA12_maxS =
        "\1\135\1\137\1\uffff\1\162\1\uffff\1\130\1\163\3\162\2\163\2\162";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\11\uffff";
    static final String DFA12_specialS =
        "\16\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\2\2\uffff\1\2\1\uffff\1\1",
            "\1\3\4\uffff\1\2\1\uffff\1\4",
            "",
            "\1\7\1\5\3\uffff\1\2\2\4\1\uffff\1\2\20\uffff\1\6",
            "",
            "\1\10",
            "\1\11",
            "\2\2\3\uffff\1\2\2\4\1\uffff\1\2\20\uffff\1\12",
            "\1\4\1\5\3\uffff\1\2\24\uffff\1\13",
            "\1\4\4\uffff\1\2\2\4\22\uffff\1\6",
            "\1\14",
            "\1\15",
            "\1\2\2\4\22\uffff\1\12",
            "\1\4\4\uffff\1\2\24\uffff\1\13"
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "367:23: ( parameters )?";
        }
    }
    static final String DFA17_eotS =
        "\6\uffff";
    static final String DFA17_eofS =
        "\6\uffff";
    static final String DFA17_minS =
        "\2\130\1\uffff\1\163\1\uffff\1\130";
    static final String DFA17_maxS =
        "\1\130\1\162\1\uffff\1\163\1\uffff\1\162";
    static final String DFA17_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    static final String DFA17_specialS =
        "\6\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\1",
            "\2\2\4\uffff\2\4\22\uffff\1\3",
            "",
            "\1\5",
            "",
            "\1\2\5\uffff\2\4\22\uffff\1\3"
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "386:4: ( data_type )?";
        }
    }
    static final String DFA28_eotS =
        "\33\uffff";
    static final String DFA28_eofS =
        "\33\uffff";
    static final String DFA28_minS =
        "\2\130\3\uffff\1\4\1\130\3\uffff\1\4\1\0\1\uffff\1\0\1\4\2\0\3\4"+
        "\1\0\2\4\4\0";
    static final String DFA28_maxS =
        "\1\164\1\167\3\uffff\1\u0080\1\164\3\uffff\1\u0080\1\0\1\uffff\1"+
        "\0\1\u0080\2\0\3\u0080\1\0\2\u0080\4\0";
    static final String DFA28_acceptS =
        "\2\uffff\1\2\2\1\2\uffff\2\1\1\2\2\uffff\1\1\16\uffff";
    static final String DFA28_specialS =
        "\1\6\1\11\3\uffff\1\1\1\7\3\uffff\1\14\1\10\1\uffff\1\16\1\12\1"+
        "\4\1\0\1\13\1\15\1\2\1\17\1\5\1\3\4\uffff}>";
    static final String[] DFA28_transitionS = {
            "\1\1\33\uffff\1\2",
            "\1\11\3\uffff\1\7\1\5\3\uffff\1\6\1\uffff\1\10\1\4\17\uffff"+
            "\1\11\2\uffff\1\3",
            "",
            "",
            "",
            "\124\14\1\12\4\14\1\13\43\14",
            "\1\15\4\uffff\1\11\26\uffff\1\11",
            "",
            "",
            "",
            "\124\14\1\20\1\21\3\14\1\17\3\14\1\16\20\14\1\22\16\14",
            "\1\uffff",
            "",
            "\1\uffff",
            "\124\14\1\23\4\14\1\24\43\14",
            "\1\uffff",
            "\1\uffff",
            "\124\14\1\25\50\14",
            "\157\14\1\26\15\14",
            "\125\14\1\27\3\14\1\31\24\14\1\30\16\14",
            "\1\uffff",
            "\125\14\1\21\3\14\1\32\24\14\1\22\16\14",
            "\131\14\1\32\24\14\1\22\16\14",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "452:21: ( rule_attributes )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA28_16 = input.LA(1);

                         
                        int index28_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index28_16);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA28_5 = input.LA(1);

                         
                        int index28_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_5==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 10;}

                        else if ( (LA28_5==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 11;}

                        else if ( ((LA28_5>=VT_COMPILATION_UNIT && LA28_5<=SEMICOLON)||(LA28_5>=DOT && LA28_5<=STRING)||(LA28_5>=COMMA && LA28_5<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA28_19 = input.LA(1);

                         
                        int index28_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_19==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 23;}

                        else if ( (LA28_19==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 24;}

                        else if ( (LA28_19==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 25;}

                        else if ( ((LA28_19>=VT_COMPILATION_UNIT && LA28_19<=ID)||(LA28_19>=DOT_STAR && LA28_19<=STRING)||(LA28_19>=COMMA && LA28_19<=NULL)||(LA28_19>=RIGHT_SQUARE && LA28_19<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_19);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA28_22 = input.LA(1);

                         
                        int index28_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_22==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 26;}

                        else if ( (LA28_22==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 18;}

                        else if ( ((LA28_22>=VT_COMPILATION_UNIT && LA28_22<=STRING)||(LA28_22>=COMMA && LA28_22<=NULL)||(LA28_22>=RIGHT_SQUARE && LA28_22<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_22);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA28_15 = input.LA(1);

                         
                        int index28_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index28_15);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA28_21 = input.LA(1);

                         
                        int index28_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_21==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 18;}

                        else if ( (LA28_21==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 26;}

                        else if ( (LA28_21==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                        else if ( ((LA28_21>=VT_COMPILATION_UNIT && LA28_21<=ID)||(LA28_21>=DOT_STAR && LA28_21<=STRING)||(LA28_21>=COMMA && LA28_21<=NULL)||(LA28_21>=RIGHT_SQUARE && LA28_21<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_21);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA28_0 = input.LA(1);

                         
                        int index28_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {s = 1;}

                        else if ( (LA28_0==THEN) ) {s = 2;}

                         
                        input.seek(index28_0);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA28_6 = input.LA(1);

                         
                        int index28_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_6==LEFT_PAREN||LA28_6==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {s = 9;}

                        else if ( (LA28_6==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {s = 13;}

                         
                        input.seek(index28_6);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA28_11 = input.LA(1);

                         
                        int index28_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index28_11);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA28_1 = input.LA(1);

                         
                        int index28_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {s = 3;}

                        else if ( (LA28_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {s = 4;}

                        else if ( (LA28_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 5;}

                        else if ( (LA28_1==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {s = 6;}

                        else if ( (LA28_1==STRING) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 7;}

                        else if ( (LA28_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {s = 8;}

                        else if ( (LA28_1==ID||LA28_1==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {s = 9;}

                         
                        input.seek(index28_1);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA28_14 = input.LA(1);

                         
                        int index28_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_14==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 19;}

                        else if ( (LA28_14==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 20;}

                        else if ( ((LA28_14>=VT_COMPILATION_UNIT && LA28_14<=SEMICOLON)||(LA28_14>=DOT && LA28_14<=STRING)||(LA28_14>=COMMA && LA28_14<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_14);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA28_17 = input.LA(1);

                         
                        int index28_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_17==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 21;}

                        else if ( ((LA28_17>=VT_COMPILATION_UNIT && LA28_17<=SEMICOLON)||(LA28_17>=DOT && LA28_17<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_17);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA28_10 = input.LA(1);

                         
                        int index28_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_10==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 14;}

                        else if ( (LA28_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 15;}

                        else if ( (LA28_10==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 16;}

                        else if ( ((LA28_10>=VT_COMPILATION_UNIT && LA28_10<=SEMICOLON)||(LA28_10>=DOT_STAR && LA28_10<=STRING)||(LA28_10>=COMMA && LA28_10<=AT)||(LA28_10>=EQUALS && LA28_10<=NULL)||(LA28_10>=RIGHT_SQUARE && LA28_10<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA28_10==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                        else if ( (LA28_10==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 18;}

                         
                        input.seek(index28_10);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA28_18 = input.LA(1);

                         
                        int index28_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_18==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 22;}

                        else if ( ((LA28_18>=VT_COMPILATION_UNIT && LA28_18<=LEFT_SQUARE)||(LA28_18>=THEN && LA28_18<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_18);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA28_13 = input.LA(1);

                         
                        int index28_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index28_13);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA28_20 = input.LA(1);

                         
                        int index28_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index28_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 28, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA34_eotS =
        "\32\uffff";
    static final String DFA34_eofS =
        "\32\uffff";
    static final String DFA34_minS =
        "\2\130\5\uffff\1\4\2\uffff\1\4\1\0\1\uffff\1\4\2\0\3\4\1\0\2\4\4"+
        "\0";
    static final String DFA34_maxS =
        "\1\164\1\167\5\uffff\1\u0080\2\uffff\1\u0080\1\0\1\uffff\1\u0080"+
        "\2\0\3\u0080\1\0\2\u0080\4\0";
    static final String DFA34_acceptS =
        "\2\uffff\1\2\4\1\1\uffff\1\1\1\2\2\uffff\1\1\15\uffff";
    static final String DFA34_specialS =
        "\1\5\1\2\5\uffff\1\1\2\uffff\1\13\1\10\1\uffff\1\11\1\6\1\0\1\12"+
        "\1\14\1\3\1\15\1\7\1\4\4\uffff}>";
    static final String[] DFA34_transitionS = {
            "\1\1\5\uffff\1\3\25\uffff\1\2",
            "\1\11\3\uffff\1\10\1\7\3\uffff\1\11\1\uffff\1\5\1\6\17\uffff"+
            "\1\11\2\uffff\1\4",
            "",
            "",
            "",
            "",
            "",
            "\124\14\1\12\4\14\1\13\43\14",
            "",
            "",
            "\124\14\1\17\1\20\3\14\1\16\3\14\1\15\20\14\1\21\16\14",
            "\1\uffff",
            "",
            "\124\14\1\22\4\14\1\23\43\14",
            "\1\uffff",
            "\1\uffff",
            "\124\14\1\24\50\14",
            "\157\14\1\25\15\14",
            "\125\14\1\26\3\14\1\27\24\14\1\30\16\14",
            "\1\uffff",
            "\125\14\1\20\3\14\1\31\24\14\1\21\16\14",
            "\131\14\1\31\24\14\1\21\16\14",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA34_eot = DFA.unpackEncodedString(DFA34_eotS);
    static final short[] DFA34_eof = DFA.unpackEncodedString(DFA34_eofS);
    static final char[] DFA34_min = DFA.unpackEncodedStringToUnsignedChars(DFA34_minS);
    static final char[] DFA34_max = DFA.unpackEncodedStringToUnsignedChars(DFA34_maxS);
    static final short[] DFA34_accept = DFA.unpackEncodedString(DFA34_acceptS);
    static final short[] DFA34_special = DFA.unpackEncodedString(DFA34_specialS);
    static final short[][] DFA34_transition;

    static {
        int numStates = DFA34_transitionS.length;
        DFA34_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA34_transition[i] = DFA.unpackEncodedString(DFA34_transitionS[i]);
        }
    }

    class DFA34 extends DFA {

        public DFA34(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 34;
            this.eot = DFA34_eot;
            this.eof = DFA34_eof;
            this.min = DFA34_min;
            this.max = DFA34_max;
            this.accept = DFA34_accept;
            this.special = DFA34_special;
            this.transition = DFA34_transition;
        }
        public String getDescription() {
            return "()* loopback of 469:45: ( ( COMMA )? attr= rule_attribute )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA34_15 = input.LA(1);

                         
                        int index34_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index34_15);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA34_7 = input.LA(1);

                         
                        int index34_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA34_7==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 10;}

                        else if ( (LA34_7==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 11;}

                        else if ( ((LA34_7>=VT_COMPILATION_UNIT && LA34_7<=SEMICOLON)||(LA34_7>=DOT && LA34_7<=STRING)||(LA34_7>=COMMA && LA34_7<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index34_7);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA34_1 = input.LA(1);

                         
                        int index34_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA34_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {s = 4;}

                        else if ( (LA34_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {s = 5;}

                        else if ( (LA34_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {s = 6;}

                        else if ( (LA34_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 7;}

                        else if ( (LA34_1==STRING) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 8;}

                        else if ( (LA34_1==ID||LA34_1==COLON||LA34_1==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {s = 9;}

                         
                        input.seek(index34_1);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA34_18 = input.LA(1);

                         
                        int index34_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA34_18>=VT_COMPILATION_UNIT && LA34_18<=ID)||(LA34_18>=DOT_STAR && LA34_18<=STRING)||(LA34_18>=COMMA && LA34_18<=NULL)||(LA34_18>=RIGHT_SQUARE && LA34_18<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA34_18==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 22;}

                        else if ( (LA34_18==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 23;}

                        else if ( (LA34_18==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 24;}

                         
                        input.seek(index34_18);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA34_21 = input.LA(1);

                         
                        int index34_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA34_21>=VT_COMPILATION_UNIT && LA34_21<=STRING)||(LA34_21>=COMMA && LA34_21<=NULL)||(LA34_21>=RIGHT_SQUARE && LA34_21<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA34_21==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                        else if ( (LA34_21==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 25;}

                         
                        input.seek(index34_21);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA34_0 = input.LA(1);

                         
                        int index34_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA34_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {s = 1;}

                        else if ( (LA34_0==THEN) ) {s = 2;}

                        else if ( (LA34_0==COMMA) ) {s = 3;}

                         
                        input.seek(index34_0);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA34_14 = input.LA(1);

                         
                        int index34_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index34_14);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA34_20 = input.LA(1);

                         
                        int index34_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA34_20>=VT_COMPILATION_UNIT && LA34_20<=ID)||(LA34_20>=DOT_STAR && LA34_20<=STRING)||(LA34_20>=COMMA && LA34_20<=NULL)||(LA34_20>=RIGHT_SQUARE && LA34_20<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA34_20==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                        else if ( (LA34_20==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 25;}

                        else if ( (LA34_20==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 16;}

                         
                        input.seek(index34_20);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA34_11 = input.LA(1);

                         
                        int index34_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index34_11);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA34_13 = input.LA(1);

                         
                        int index34_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA34_13>=VT_COMPILATION_UNIT && LA34_13<=SEMICOLON)||(LA34_13>=DOT && LA34_13<=STRING)||(LA34_13>=COMMA && LA34_13<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA34_13==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 18;}

                        else if ( (LA34_13==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 19;}

                         
                        input.seek(index34_13);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA34_16 = input.LA(1);

                         
                        int index34_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA34_16==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 20;}

                        else if ( ((LA34_16>=VT_COMPILATION_UNIT && LA34_16<=SEMICOLON)||(LA34_16>=DOT && LA34_16<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index34_16);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA34_10 = input.LA(1);

                         
                        int index34_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA34_10==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 13;}

                        else if ( (LA34_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 14;}

                        else if ( (LA34_10==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 15;}

                        else if ( ((LA34_10>=VT_COMPILATION_UNIT && LA34_10<=SEMICOLON)||(LA34_10>=DOT_STAR && LA34_10<=STRING)||(LA34_10>=COMMA && LA34_10<=AT)||(LA34_10>=EQUALS && LA34_10<=NULL)||(LA34_10>=RIGHT_SQUARE && LA34_10<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA34_10==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 16;}

                        else if ( (LA34_10==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                         
                        input.seek(index34_10);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA34_17 = input.LA(1);

                         
                        int index34_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA34_17==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 21;}

                        else if ( ((LA34_17>=VT_COMPILATION_UNIT && LA34_17<=LEFT_SQUARE)||(LA34_17>=THEN && LA34_17<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index34_17);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA34_19 = input.LA(1);

                         
                        int index34_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index34_19);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 34, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA51_eotS =
        "\172\uffff";
    static final String DFA51_eofS =
        "\172\uffff";
    static final String DFA51_minS =
        "\3\130\2\0\2\130\1\163\1\0\3\uffff\1\131\1\130\1\131\1\135\1\uffff"+
        "\1\130\1\163\1\130\1\131\1\130\1\131\1\135\1\130\2\0\1\130\1\163"+
        "\2\130\2\0\1\130\1\4\11\130\1\0\2\uffff\1\131\1\135\1\130\1\0\2"+
        "\130\1\0\1\4\11\130\1\uffff\1\130\1\4\12\0\1\uffff\14\0\1\uffff"+
        "\4\0\1\uffff\32\0";
    static final String DFA51_maxS =
        "\2\135\1\162\2\0\1\135\1\130\1\163\1\0\3\uffff\1\162\1\130\2\162"+
        "\1\uffff\1\130\1\163\1\137\1\162\1\137\3\162\2\0\1\130\1\163\1\137"+
        "\1\162\2\0\1\130\1\u0080\1\130\7\161\1\130\1\0\2\uffff\3\162\1\0"+
        "\1\145\1\130\1\0\1\u0080\1\130\7\161\1\130\1\uffff\1\162\1\u0080"+
        "\12\0\1\uffff\14\0\1\uffff\4\0\1\uffff\32\0";
    static final String DFA51_acceptS =
        "\11\uffff\1\1\2\2\4\uffff\1\3\34\uffff\2\3\21\uffff\1\3\14\uffff"+
        "\1\3\14\uffff\1\3\4\uffff\1\3\32\uffff";
    static final String DFA51_specialS =
        "\2\uffff\1\2\1\6\1\3\3\uffff\1\4\20\uffff\1\10\1\5\4\uffff\1\7\1"+
        "\12\13\uffff\1\0\5\uffff\1\11\2\uffff\1\1\104\uffff}>";
    static final String[] DFA51_transitionS = {
            "\1\2\4\uffff\1\1",
            "\1\4\4\uffff\1\3",
            "\1\11\1\6\3\uffff\1\10\3\uffff\1\5\20\uffff\1\7",
            "\1\uffff",
            "\1\uffff",
            "\1\14\4\uffff\1\15",
            "\1\16",
            "\1\17",
            "\1\uffff",
            "",
            "",
            "",
            "\1\21\3\uffff\1\23\24\uffff\1\22",
            "\1\24",
            "\1\6\3\uffff\1\25\24\uffff\1\7",
            "\1\25\24\uffff\1\7",
            "",
            "\1\26",
            "\1\27",
            "\1\30\4\uffff\1\31\1\uffff\1\32",
            "\1\33\3\uffff\1\35\24\uffff\1\34",
            "\1\36\4\uffff\1\37\1\uffff\1\40",
            "\1\21\3\uffff\1\23\24\uffff\1\22",
            "\1\23\24\uffff\1\22",
            "\1\44\1\43\3\uffff\1\54\3\uffff\1\41\7\uffff\1\45\1\46\1\47"+
            "\1\50\1\51\1\52\1\53\2\uffff\1\42",
            "\1\uffff",
            "\1\uffff",
            "\1\57",
            "\1\60",
            "\1\61\4\uffff\1\62\1\uffff\1\63",
            "\1\70\1\67\3\uffff\1\65\3\uffff\1\64\7\uffff\1\71\1\72\1\73"+
            "\1\74\1\75\1\76\1\77\2\uffff\1\66",
            "\1\uffff",
            "\1\uffff",
            "\1\101",
            "\156\102\1\103\1\104\15\102",
            "\1\105",
            "\1\106\3\uffff\1\107\1\110\5\uffff\2\107\12\uffff\1\111\2\107",
            "\1\112\3\uffff\1\107\1\113\5\uffff\2\107\13\uffff\2\107",
            "\1\112\3\uffff\1\107\1\113\5\uffff\2\107\13\uffff\2\107",
            "\1\112\3\uffff\1\107\1\113\5\uffff\2\107\13\uffff\2\107",
            "\1\112\3\uffff\1\107\1\113\5\uffff\2\107\13\uffff\2\107",
            "\1\112\3\uffff\1\107\1\113\5\uffff\2\107\13\uffff\2\107",
            "\1\112\3\uffff\1\107\1\113\5\uffff\2\107\13\uffff\2\107",
            "\1\114",
            "\1\uffff",
            "",
            "",
            "\1\33\3\uffff\1\35\24\uffff\1\34",
            "\1\35\24\uffff\1\34",
            "\1\121\1\120\3\uffff\1\131\3\uffff\1\116\7\uffff\1\122\1\123"+
            "\1\124\1\125\1\126\1\127\1\130\2\uffff\1\117",
            "\1\uffff",
            "\1\133\6\uffff\1\135\5\uffff\1\134",
            "\1\136",
            "\1\uffff",
            "\156\140\1\141\1\142\15\140",
            "\1\143",
            "\1\144\3\uffff\1\145\1\146\5\uffff\2\145\12\uffff\1\147\2\145",
            "\1\150\3\uffff\1\145\1\151\5\uffff\2\145\13\uffff\2\145",
            "\1\150\3\uffff\1\145\1\151\5\uffff\2\145\13\uffff\2\145",
            "\1\150\3\uffff\1\145\1\151\5\uffff\2\145\13\uffff\2\145",
            "\1\150\3\uffff\1\145\1\151\5\uffff\2\145\13\uffff\2\145",
            "\1\150\3\uffff\1\145\1\151\5\uffff\2\145\13\uffff\2\145",
            "\1\150\3\uffff\1\145\1\151\5\uffff\2\145\13\uffff\2\145",
            "\1\152",
            "",
            "\1\155\1\154\3\uffff\1\165\1\171\1\32\5\uffff\1\170\1\167\1"+
            "\uffff\1\166\1\156\1\157\1\160\1\161\1\162\1\163\1\164\2\uffff"+
            "\1\153",
            "\156\102\1\103\1\104\15\102",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA51_eot = DFA.unpackEncodedString(DFA51_eotS);
    static final short[] DFA51_eof = DFA.unpackEncodedString(DFA51_eofS);
    static final char[] DFA51_min = DFA.unpackEncodedStringToUnsignedChars(DFA51_minS);
    static final char[] DFA51_max = DFA.unpackEncodedStringToUnsignedChars(DFA51_maxS);
    static final short[] DFA51_accept = DFA.unpackEncodedString(DFA51_acceptS);
    static final short[] DFA51_special = DFA.unpackEncodedString(DFA51_specialS);
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA51_transition[i] = DFA.unpackEncodedString(DFA51_transitionS[i]);
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }
        public String getDescription() {
            return "583:10: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA51_44 = input.LA(1);

                         
                        int index51_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 77;}

                         
                        input.seek(index51_44);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA51_53 = input.LA(1);

                         
                        int index51_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 95;}

                         
                        input.seek(index51_53);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA51_2 = input.LA(1);

                         
                        int index51_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA51_2==COLON) ) {s = 5;}

                        else if ( (LA51_2==DOT) ) {s = 6;}

                        else if ( (LA51_2==LEFT_SQUARE) ) {s = 7;}

                        else if ( (LA51_2==LEFT_PAREN) ) {s = 8;}

                        else if ( (LA51_2==ID) && (((synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {s = 9;}

                         
                        input.seek(index51_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA51_4 = input.LA(1);

                         
                        int index51_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index51_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA51_8 = input.LA(1);

                         
                        int index51_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred6()||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EVAL)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.FORALL))))) ) {s = 9;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index51_8);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA51_26 = input.LA(1);

                         
                        int index51_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index51_26);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA51_3 = input.LA(1);

                         
                        int index51_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 10;}

                         
                        input.seek(index51_3);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA51_31 = input.LA(1);

                         
                        int index51_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 64;}

                         
                        input.seek(index51_31);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA51_25 = input.LA(1);

                         
                        int index51_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 45;}

                         
                        input.seek(index51_25);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA51_50 = input.LA(1);

                         
                        int index51_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 90;}

                         
                        input.seek(index51_50);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA51_32 = input.LA(1);

                         
                        int index51_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index51_32);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 51, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA52_eotS =
        "\172\uffff";
    static final String DFA52_eofS =
        "\172\uffff";
    static final String DFA52_minS =
        "\3\130\2\0\1\130\1\0\1\uffff\1\130\1\163\2\uffff\1\131\1\130\1\uffff"+
        "\1\131\1\135\1\130\1\163\1\130\1\131\1\130\1\131\1\135\1\130\2\0"+
        "\1\130\1\163\2\130\2\0\1\130\1\0\1\4\11\130\2\uffff\1\131\1\135"+
        "\1\130\1\0\2\130\1\0\1\4\11\130\1\uffff\1\130\1\uffff\1\4\26\0\1"+
        "\uffff\4\0\1\uffff\32\0";
    static final String DFA52_maxS =
        "\2\135\1\162\2\0\1\135\1\0\1\uffff\1\130\1\163\2\uffff\1\162\1\130"+
        "\1\uffff\2\162\1\130\1\163\1\137\1\162\1\137\3\162\2\0\1\130\1\163"+
        "\1\137\1\162\2\0\1\130\1\0\1\u0080\1\130\7\161\1\130\2\uffff\3\162"+
        "\1\0\1\145\1\130\1\0\1\u0080\1\130\7\161\1\130\1\uffff\1\162\1\uffff"+
        "\1\u0080\26\0\1\uffff\4\0\1\uffff\32\0";
    static final String DFA52_acceptS =
        "\7\uffff\1\1\2\uffff\2\2\2\uffff\1\3\36\uffff\2\3\21\uffff\1\3\1"+
        "\uffff\1\3\27\uffff\1\3\4\uffff\1\3\32\uffff";
    static final String DFA52_specialS =
        "\2\uffff\1\3\1\1\1\2\1\uffff\1\12\22\uffff\1\10\1\5\4\uffff\1\6"+
        "\1\11\1\uffff\1\0\17\uffff\1\7\2\uffff\1\4\104\uffff}>";
    static final String[] DFA52_transitionS = {
            "\1\2\4\uffff\1\1",
            "\1\4\4\uffff\1\3",
            "\1\7\1\10\3\uffff\1\6\3\uffff\1\5\20\uffff\1\11",
            "\1\uffff",
            "\1\uffff",
            "\1\14\4\uffff\1\15",
            "\1\uffff",
            "",
            "\1\17",
            "\1\20",
            "",
            "",
            "\1\21\3\uffff\1\23\24\uffff\1\22",
            "\1\24",
            "",
            "\1\10\3\uffff\1\25\24\uffff\1\11",
            "\1\25\24\uffff\1\11",
            "\1\26",
            "\1\27",
            "\1\30\4\uffff\1\31\1\uffff\1\32",
            "\1\33\3\uffff\1\35\24\uffff\1\34",
            "\1\36\4\uffff\1\37\1\uffff\1\40",
            "\1\21\3\uffff\1\23\24\uffff\1\22",
            "\1\23\24\uffff\1\22",
            "\1\45\1\44\3\uffff\1\42\3\uffff\1\41\7\uffff\1\46\1\47\1\50"+
            "\1\51\1\52\1\53\1\54\2\uffff\1\43",
            "\1\uffff",
            "\1\uffff",
            "\1\57",
            "\1\60",
            "\1\61\4\uffff\1\62\1\uffff\1\63",
            "\1\70\1\67\3\uffff\1\65\3\uffff\1\64\7\uffff\1\71\1\72\1\73"+
            "\1\74\1\75\1\76\1\77\2\uffff\1\66",
            "\1\uffff",
            "\1\uffff",
            "\1\101",
            "\1\uffff",
            "\156\103\1\104\1\105\15\103",
            "\1\106",
            "\1\107\3\uffff\1\110\1\111\5\uffff\2\110\12\uffff\1\112\2\110",
            "\1\113\3\uffff\1\110\1\114\5\uffff\2\110\13\uffff\2\110",
            "\1\113\3\uffff\1\110\1\114\5\uffff\2\110\13\uffff\2\110",
            "\1\113\3\uffff\1\110\1\114\5\uffff\2\110\13\uffff\2\110",
            "\1\113\3\uffff\1\110\1\114\5\uffff\2\110\13\uffff\2\110",
            "\1\113\3\uffff\1\110\1\114\5\uffff\2\110\13\uffff\2\110",
            "\1\113\3\uffff\1\110\1\114\5\uffff\2\110\13\uffff\2\110",
            "\1\115",
            "",
            "",
            "\1\33\3\uffff\1\35\24\uffff\1\34",
            "\1\35\24\uffff\1\34",
            "\1\121\1\120\3\uffff\1\131\3\uffff\1\116\7\uffff\1\122\1\123"+
            "\1\124\1\125\1\126\1\127\1\130\2\uffff\1\117",
            "\1\uffff",
            "\1\133\6\uffff\1\135\5\uffff\1\134",
            "\1\136",
            "\1\uffff",
            "\156\140\1\141\1\142\15\140",
            "\1\143",
            "\1\144\3\uffff\1\146\1\147\5\uffff\2\146\12\uffff\1\145\2\146",
            "\1\150\3\uffff\1\146\1\151\5\uffff\2\146\13\uffff\2\146",
            "\1\150\3\uffff\1\146\1\151\5\uffff\2\146\13\uffff\2\146",
            "\1\150\3\uffff\1\146\1\151\5\uffff\2\146\13\uffff\2\146",
            "\1\150\3\uffff\1\146\1\151\5\uffff\2\146\13\uffff\2\146",
            "\1\150\3\uffff\1\146\1\151\5\uffff\2\146\13\uffff\2\146",
            "\1\150\3\uffff\1\146\1\151\5\uffff\2\146\13\uffff\2\146",
            "\1\152",
            "",
            "\1\155\1\154\3\uffff\1\165\1\171\1\32\5\uffff\1\170\1\167\1"+
            "\uffff\1\166\1\156\1\157\1\160\1\161\1\162\1\163\1\164\2\uffff"+
            "\1\153",
            "",
            "\156\103\1\104\1\105\15\103",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA52_eot = DFA.unpackEncodedString(DFA52_eotS);
    static final short[] DFA52_eof = DFA.unpackEncodedString(DFA52_eofS);
    static final char[] DFA52_min = DFA.unpackEncodedStringToUnsignedChars(DFA52_minS);
    static final char[] DFA52_max = DFA.unpackEncodedStringToUnsignedChars(DFA52_maxS);
    static final short[] DFA52_accept = DFA.unpackEncodedString(DFA52_acceptS);
    static final short[] DFA52_special = DFA.unpackEncodedString(DFA52_specialS);
    static final short[][] DFA52_transition;

    static {
        int numStates = DFA52_transitionS.length;
        DFA52_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA52_transition[i] = DFA.unpackEncodedString(DFA52_transitionS[i]);
        }
    }

    class DFA52 extends DFA {

        public DFA52(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 52;
            this.eot = DFA52_eot;
            this.eof = DFA52_eof;
            this.min = DFA52_min;
            this.max = DFA52_max;
            this.accept = DFA52_accept;
            this.special = DFA52_special;
            this.transition = DFA52_transition;
        }
        public String getDescription() {
            return "591:3: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA52_34 = input.LA(1);

                         
                        int index52_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 66;}

                         
                        input.seek(index52_34);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA52_3 = input.LA(1);

                         
                        int index52_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 10;}

                         
                        input.seek(index52_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA52_4 = input.LA(1);

                         
                        int index52_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index52_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA52_2 = input.LA(1);

                         
                        int index52_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_2==COLON) ) {s = 5;}

                        else if ( (LA52_2==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA52_2==ID) && (((synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {s = 7;}

                        else if ( (LA52_2==DOT) ) {s = 8;}

                        else if ( (LA52_2==LEFT_SQUARE) ) {s = 9;}

                         
                        input.seek(index52_2);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA52_53 = input.LA(1);

                         
                        int index52_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 95;}

                         
                        input.seek(index52_53);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA52_26 = input.LA(1);

                         
                        int index52_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index52_26);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA52_31 = input.LA(1);

                         
                        int index52_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 64;}

                         
                        input.seek(index52_31);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA52_50 = input.LA(1);

                         
                        int index52_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 90;}

                         
                        input.seek(index52_50);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA52_25 = input.LA(1);

                         
                        int index52_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 45;}

                         
                        input.seek(index52_25);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA52_32 = input.LA(1);

                         
                        int index52_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index52_32);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA52_6 = input.LA(1);

                         
                        int index52_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||synpred7()||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.FORALL)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EVAL))))) ) {s = 7;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index52_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 52, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA59_eotS =
        "\13\uffff";
    static final String DFA59_eofS =
        "\13\uffff";
    static final String DFA59_minS =
        "\1\130\1\135\2\4\1\0\1\130\1\0\4\uffff";
    static final String DFA59_maxS =
        "\1\130\1\135\2\u0080\1\0\1\137\1\0\4\uffff";
    static final String DFA59_acceptS =
        "\7\uffff\1\1\2\2\1\1";
    static final String DFA59_specialS =
        "\4\uffff\1\0\1\1\1\2\4\uffff}>";
    static final String[] DFA59_transitionS = {
            "\1\1",
            "\1\2",
            "\131\3\1\4\1\3\1\5\41\3",
            "\131\3\1\6\1\3\1\5\41\3",
            "\1\uffff",
            "\1\12\5\uffff\1\12\1\11",
            "\1\uffff",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA59_eot = DFA.unpackEncodedString(DFA59_eotS);
    static final short[] DFA59_eof = DFA.unpackEncodedString(DFA59_eofS);
    static final char[] DFA59_min = DFA.unpackEncodedStringToUnsignedChars(DFA59_minS);
    static final char[] DFA59_max = DFA.unpackEncodedStringToUnsignedChars(DFA59_maxS);
    static final short[] DFA59_accept = DFA.unpackEncodedString(DFA59_acceptS);
    static final short[] DFA59_special = DFA.unpackEncodedString(DFA59_specialS);
    static final short[][] DFA59_transition;

    static {
        int numStates = DFA59_transitionS.length;
        DFA59_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA59_transition[i] = DFA.unpackEncodedString(DFA59_transitionS[i]);
        }
    }

    class DFA59 extends DFA {

        public DFA59(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 59;
            this.eot = DFA59_eot;
            this.eof = DFA59_eof;
            this.min = DFA59_min;
            this.max = DFA59_max;
            this.accept = DFA59_accept;
            this.special = DFA59_special;
            this.transition = DFA59_transition;
        }
        public String getDescription() {
            return "635:3: ( accumulate_init_clause | accumulate_id_clause )";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA59_4 = input.LA(1);

                         
                        int index59_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.INIT))) ) {s = 7;}

                        else if ( (true) ) {s = 8;}

                         
                        input.seek(index59_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA59_5 = input.LA(1);

                         
                        int index59_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA59_5==RIGHT_PAREN) ) {s = 9;}

                        else if ( (LA59_5==ID||LA59_5==COMMA) && ((validateIdentifierKey(DroolsSoftKeywords.INIT)))) {s = 10;}

                         
                        input.seek(index59_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA59_6 = input.LA(1);

                         
                        int index59_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.INIT))) ) {s = 10;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index59_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 59, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA63_eotS =
        "\13\uffff";
    static final String DFA63_eofS =
        "\13\uffff";
    static final String DFA63_minS =
        "\1\130\1\135\2\4\1\0\1\130\1\0\4\uffff";
    static final String DFA63_maxS =
        "\1\130\1\135\2\u0080\1\0\1\137\1\0\4\uffff";
    static final String DFA63_acceptS =
        "\7\uffff\1\1\1\2\1\1\1\2";
    static final String DFA63_specialS =
        "\1\5\1\3\1\2\1\4\1\0\1\1\1\6\4\uffff}>";
    static final String[] DFA63_transitionS = {
            "\1\1",
            "\1\2",
            "\131\3\1\4\1\3\1\5\41\3",
            "\131\3\1\6\1\3\1\5\41\3",
            "\1\uffff",
            "\1\11\5\uffff\1\11\1\12",
            "\1\uffff",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA63_eot = DFA.unpackEncodedString(DFA63_eotS);
    static final short[] DFA63_eof = DFA.unpackEncodedString(DFA63_eofS);
    static final char[] DFA63_min = DFA.unpackEncodedStringToUnsignedChars(DFA63_minS);
    static final char[] DFA63_max = DFA.unpackEncodedStringToUnsignedChars(DFA63_maxS);
    static final short[] DFA63_accept = DFA.unpackEncodedString(DFA63_acceptS);
    static final short[] DFA63_special = DFA.unpackEncodedString(DFA63_specialS);
    static final short[][] DFA63_transition;

    static {
        int numStates = DFA63_transitionS.length;
        DFA63_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA63_transition[i] = DFA.unpackEncodedString(DFA63_transitionS[i]);
        }
    }

    class DFA63 extends DFA {

        public DFA63(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 63;
            this.eot = DFA63_eot;
            this.eof = DFA63_eof;
            this.min = DFA63_min;
            this.max = DFA63_max;
            this.accept = DFA63_accept;
            this.special = DFA63_special;
            this.transition = DFA63_transition;
        }
        public String getDescription() {
            return "646:2: ( reverse_key pc3= paren_chunk ( COMMA )? )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA63_4 = input.LA(1);

                         
                        int index63_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.REVERSE))) ) {s = 7;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RESULT))) ) {s = 8;}

                         
                        input.seek(index63_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA63_5 = input.LA(1);

                         
                        int index63_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA63_5==ID||LA63_5==COMMA) && ((validateIdentifierKey(DroolsSoftKeywords.REVERSE)))) {s = 9;}

                        else if ( (LA63_5==RIGHT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.RESULT)))) {s = 10;}

                         
                        input.seek(index63_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA63_2 = input.LA(1);

                         
                        int index63_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA63_2>=VT_COMPILATION_UNIT && LA63_2<=STRING)||LA63_2==COMMA||(LA63_2>=AT && LA63_2<=MULTI_LINE_COMMENT)) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 3;}

                        else if ( (LA63_2==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 4;}

                        else if ( (LA63_2==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 5;}

                         
                        input.seek(index63_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA63_1 = input.LA(1);

                         
                        int index63_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA63_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 2;}

                         
                        input.seek(index63_1);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA63_3 = input.LA(1);

                         
                        int index63_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA63_3==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 5;}

                        else if ( ((LA63_3>=VT_COMPILATION_UNIT && LA63_3<=STRING)||LA63_3==COMMA||(LA63_3>=AT && LA63_3<=MULTI_LINE_COMMENT)) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 3;}

                        else if ( (LA63_3==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 6;}

                         
                        input.seek(index63_3);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA63_0 = input.LA(1);

                         
                        int index63_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA63_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 1;}

                         
                        input.seek(index63_0);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA63_6 = input.LA(1);

                         
                        int index63_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.REVERSE))) ) {s = 9;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RESULT))) ) {s = 10;}

                         
                        input.seek(index63_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 63, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA65_eotS =
        "\25\uffff";
    static final String DFA65_eofS =
        "\25\uffff";
    static final String DFA65_minS =
        "\1\127\1\4\1\uffff\1\4\1\0\2\uffff\1\4\2\0\3\4\1\0\1\4\1\uffff\1"+
        "\4\4\0";
    static final String DFA65_maxS =
        "\1\164\1\u0080\1\uffff\1\u0080\1\0\2\uffff\1\u0080\2\0\3\u0080\1"+
        "\0\1\u0080\1\uffff\1\u0080\4\0";
    static final String DFA65_acceptS =
        "\2\uffff\1\2\2\uffff\2\1\10\uffff\1\1\5\uffff";
    static final String DFA65_specialS =
        "\1\uffff\1\5\1\uffff\1\12\1\1\2\uffff\1\10\1\11\1\2\1\13\1\3\1\6"+
        "\1\0\1\7\1\uffff\1\4\4\uffff}>";
    static final String[] DFA65_transitionS = {
            "\3\2\1\uffff\1\2\1\uffff\1\1\2\2\5\uffff\2\2\15\uffff\1\2",
            "\124\5\1\3\4\5\1\4\1\5\1\6\41\5",
            "",
            "\124\5\1\11\1\12\3\5\1\10\1\5\1\6\1\5\1\7\20\5\1\13\16\5",
            "\1\uffff",
            "",
            "",
            "\124\5\1\14\4\5\1\15\1\5\1\6\41\5",
            "\1\uffff",
            "\1\uffff",
            "\124\5\1\16\4\5\1\17\1\5\1\6\41\5",
            "\131\5\1\17\1\5\1\6\23\5\1\20\15\5",
            "\125\5\1\21\3\5\1\22\1\5\1\6\22\5\1\23\16\5",
            "\1\uffff",
            "\125\5\1\12\3\5\1\24\1\5\1\6\22\5\1\13\16\5",
            "",
            "\131\5\1\24\1\5\1\6\22\5\1\13\16\5",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA65_eot = DFA.unpackEncodedString(DFA65_eotS);
    static final short[] DFA65_eof = DFA.unpackEncodedString(DFA65_eofS);
    static final char[] DFA65_min = DFA.unpackEncodedStringToUnsignedChars(DFA65_minS);
    static final char[] DFA65_max = DFA.unpackEncodedStringToUnsignedChars(DFA65_maxS);
    static final short[] DFA65_accept = DFA.unpackEncodedString(DFA65_acceptS);
    static final short[] DFA65_special = DFA.unpackEncodedString(DFA65_specialS);
    static final short[][] DFA65_transition;

    static {
        int numStates = DFA65_transitionS.length;
        DFA65_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA65_transition[i] = DFA.unpackEncodedString(DFA65_transitionS[i]);
        }
    }

    class DFA65 extends DFA {

        public DFA65(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 65;
            this.eot = DFA65_eot;
            this.eof = DFA65_eof;
            this.min = DFA65_min;
            this.max = DFA65_max;
            this.accept = DFA65_accept;
            this.special = DFA65_special;
            this.transition = DFA65_transition;
        }
        public String getDescription() {
            return "674:3: ( ( LEFT_PAREN )=>args= paren_chunk )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA65_13 = input.LA(1);

                         
                        int index65_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8()) ) {s = 15;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index65_13);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA65_4 = input.LA(1);

                         
                        int index65_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8()) ) {s = 6;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index65_4);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA65_9 = input.LA(1);

                         
                        int index65_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8()) ) {s = 6;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index65_9);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA65_11 = input.LA(1);

                         
                        int index65_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_11==RIGHT_SQUARE) ) {s = 16;}

                        else if ( (LA65_11==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( ((LA65_11>=VT_COMPILATION_UNIT && LA65_11<=STRING)||LA65_11==COMMA||(LA65_11>=AT && LA65_11<=LEFT_SQUARE)||(LA65_11>=THEN && LA65_11<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 5;}

                        else if ( (LA65_11==LEFT_PAREN) && (synpred8())) {s = 15;}

                         
                        input.seek(index65_11);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA65_16 = input.LA(1);

                         
                        int index65_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_16==LEFT_PAREN) ) {s = 20;}

                        else if ( (LA65_16==LEFT_SQUARE) ) {s = 11;}

                        else if ( (LA65_16==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( ((LA65_16>=VT_COMPILATION_UNIT && LA65_16<=STRING)||LA65_16==COMMA||(LA65_16>=AT && LA65_16<=NULL)||(LA65_16>=RIGHT_SQUARE && LA65_16<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 5;}

                         
                        input.seek(index65_16);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA65_1 = input.LA(1);

                         
                        int index65_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_1==ID) ) {s = 3;}

                        else if ( (LA65_1==LEFT_PAREN) ) {s = 4;}

                        else if ( ((LA65_1>=VT_COMPILATION_UNIT && LA65_1<=SEMICOLON)||(LA65_1>=DOT && LA65_1<=STRING)||LA65_1==COMMA||(LA65_1>=AT && LA65_1<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 5;}

                        else if ( (LA65_1==RIGHT_PAREN) && (synpred8())) {s = 6;}

                         
                        input.seek(index65_1);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA65_12 = input.LA(1);

                         
                        int index65_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_12==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( (LA65_12==DOT) ) {s = 17;}

                        else if ( (LA65_12==LEFT_PAREN) ) {s = 18;}

                        else if ( (LA65_12==LEFT_SQUARE) ) {s = 19;}

                        else if ( ((LA65_12>=VT_COMPILATION_UNIT && LA65_12<=ID)||(LA65_12>=DOT_STAR && LA65_12<=STRING)||LA65_12==COMMA||(LA65_12>=AT && LA65_12<=NULL)||(LA65_12>=RIGHT_SQUARE && LA65_12<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 5;}

                         
                        input.seek(index65_12);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA65_14 = input.LA(1);

                         
                        int index65_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_14==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( (LA65_14==LEFT_SQUARE) ) {s = 11;}

                        else if ( (LA65_14==LEFT_PAREN) ) {s = 20;}

                        else if ( (LA65_14==DOT) ) {s = 10;}

                        else if ( ((LA65_14>=VT_COMPILATION_UNIT && LA65_14<=ID)||(LA65_14>=DOT_STAR && LA65_14<=STRING)||LA65_14==COMMA||(LA65_14>=AT && LA65_14<=NULL)||(LA65_14>=RIGHT_SQUARE && LA65_14<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 5;}

                         
                        input.seek(index65_14);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA65_7 = input.LA(1);

                         
                        int index65_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_7==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( (LA65_7==ID) ) {s = 12;}

                        else if ( (LA65_7==LEFT_PAREN) ) {s = 13;}

                        else if ( ((LA65_7>=VT_COMPILATION_UNIT && LA65_7<=SEMICOLON)||(LA65_7>=DOT && LA65_7<=STRING)||LA65_7==COMMA||(LA65_7>=AT && LA65_7<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 5;}

                         
                        input.seek(index65_7);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA65_8 = input.LA(1);

                         
                        int index65_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8()) ) {s = 6;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index65_8);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA65_3 = input.LA(1);

                         
                        int index65_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_3==COLON) ) {s = 7;}

                        else if ( (LA65_3==LEFT_PAREN) ) {s = 8;}

                        else if ( (LA65_3==ID) ) {s = 9;}

                        else if ( (LA65_3==DOT) ) {s = 10;}

                        else if ( (LA65_3==LEFT_SQUARE) ) {s = 11;}

                        else if ( (LA65_3==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( ((LA65_3>=VT_COMPILATION_UNIT && LA65_3<=SEMICOLON)||(LA65_3>=DOT_STAR && LA65_3<=STRING)||LA65_3==COMMA||LA65_3==AT||(LA65_3>=EQUALS && LA65_3<=NULL)||(LA65_3>=RIGHT_SQUARE && LA65_3<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 5;}

                         
                        input.seek(index65_3);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA65_10 = input.LA(1);

                         
                        int index65_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_10==ID) ) {s = 14;}

                        else if ( (LA65_10==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( ((LA65_10>=VT_COMPILATION_UNIT && LA65_10<=SEMICOLON)||(LA65_10>=DOT && LA65_10<=STRING)||LA65_10==COMMA||(LA65_10>=AT && LA65_10<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 5;}

                        else if ( (LA65_10==LEFT_PAREN) && (synpred8())) {s = 15;}

                         
                        input.seek(index65_10);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 65, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA80_eotS =
        "\30\uffff";
    static final String DFA80_eofS =
        "\30\uffff";
    static final String DFA80_minS =
        "\1\136\1\uffff\2\130\1\0\1\uffff\1\130\1\0\1\130\2\0\1\4\1\162\2"+
        "\4\11\0";
    static final String DFA80_maxS =
        "\1\146\1\uffff\1\157\1\162\1\0\1\uffff\1\162\1\0\1\130\2\0\1\u0080"+
        "\1\162\2\u0080\11\0";
    static final String DFA80_acceptS =
        "\1\uffff\1\2\3\uffff\1\1\22\uffff";
    static final String DFA80_specialS =
        "\2\uffff\1\3\1\6\1\5\1\uffff\1\0\1\1\1\uffff\1\2\1\4\15\uffff}>";
    static final String[] DFA80_transitionS = {
            "\2\1\5\uffff\1\2\1\1",
            "",
            "\1\3\4\uffff\1\4\13\uffff\7\5",
            "\1\6\1\1\2\uffff\1\5\1\7\3\uffff\1\1\1\uffff\2\5\4\uffff\6\1"+
            "\1\10\2\5\1\1",
            "\1\uffff",
            "",
            "\1\11\1\5\2\uffff\1\12\1\13\2\5\3\uffff\2\12\2\5\10\uffff\1"+
            "\1\2\12\1\5",
            "\1\uffff",
            "\1\14",
            "\1\uffff",
            "\1\uffff",
            "\124\20\1\15\3\20\1\16\1\17\1\20\1\21\3\20\2\16\13\20\2\16\17"+
            "\20",
            "\1\22",
            "\125\20\1\24\3\20\1\27\1\25\1\26\22\20\1\23\16\20",
            "\131\20\1\27\1\25\1\26\41\20",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA80_eot = DFA.unpackEncodedString(DFA80_eotS);
    static final short[] DFA80_eof = DFA.unpackEncodedString(DFA80_eofS);
    static final char[] DFA80_min = DFA.unpackEncodedStringToUnsignedChars(DFA80_minS);
    static final char[] DFA80_max = DFA.unpackEncodedStringToUnsignedChars(DFA80_maxS);
    static final short[] DFA80_accept = DFA.unpackEncodedString(DFA80_acceptS);
    static final short[] DFA80_special = DFA.unpackEncodedString(DFA80_specialS);
    static final short[][] DFA80_transition;

    static {
        int numStates = DFA80_transitionS.length;
        DFA80_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA80_transition[i] = DFA.unpackEncodedString(DFA80_transitionS[i]);
        }
    }

    class DFA80 extends DFA {

        public DFA80(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 80;
            this.eot = DFA80_eot;
            this.eof = DFA80_eof;
            this.min = DFA80_min;
            this.max = DFA80_max;
            this.accept = DFA80_accept;
            this.special = DFA80_special;
            this.transition = DFA80_transition;
        }
        public String getDescription() {
            return "()* loopback of 755:25: ({...}? => DOUBLE_PIPE and_restr_connective )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA80_6 = input.LA(1);

                         
                        int index80_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA80_6==ID) ) {s = 9;}

                        else if ( (LA80_6==STRING||(LA80_6>=BOOL && LA80_6<=INT)||(LA80_6>=FLOAT && LA80_6<=NULL)) ) {s = 10;}

                        else if ( (LA80_6==LEFT_PAREN) ) {s = 11;}

                        else if ( (LA80_6==GRAVE_ACCENT) ) {s = 1;}

                        else if ( (LA80_6==DOT||(LA80_6>=COMMA && LA80_6<=RIGHT_PAREN)||(LA80_6>=DOUBLE_PIPE && LA80_6<=DOUBLE_AMPER)||LA80_6==LEFT_SQUARE) && ((validateRestr()))) {s = 5;}

                         
                        input.seek(index80_6);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA80_7 = input.LA(1);

                         
                        int index80_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 5;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_7);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA80_9 = input.LA(1);

                         
                        int index80_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 5;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_9);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA80_2 = input.LA(1);

                         
                        int index80_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA80_2==ID) ) {s = 3;}

                        else if ( (LA80_2==LEFT_PAREN) ) {s = 4;}

                        else if ( ((LA80_2>=EQUAL && LA80_2<=GRAVE_ACCENT)) && ((validateRestr()))) {s = 5;}

                         
                        input.seek(index80_2);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA80_10 = input.LA(1);

                         
                        int index80_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 5;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_10);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA80_4 = input.LA(1);

                         
                        int index80_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 5;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_4);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA80_3 = input.LA(1);

                         
                        int index80_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA80_3==DOT||LA80_3==COLON||(LA80_3>=EQUAL && LA80_3<=NOT_EQUAL)||LA80_3==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA80_3==ID) ) {s = 6;}

                        else if ( (LA80_3==STRING||(LA80_3>=BOOL && LA80_3<=INT)||(LA80_3>=FLOAT && LA80_3<=NULL)) && ((validateRestr()))) {s = 5;}

                        else if ( (LA80_3==LEFT_PAREN) ) {s = 7;}

                        else if ( (LA80_3==GRAVE_ACCENT) ) {s = 8;}

                         
                        input.seek(index80_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 80, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA81_eotS =
        "\50\uffff";
    static final String DFA81_eofS =
        "\50\uffff";
    static final String DFA81_minS =
        "\1\136\1\uffff\2\130\1\uffff\1\130\1\4\3\130\1\0\1\4\2\0\2\4\30"+
        "\0";
    static final String DFA81_maxS =
        "\1\146\1\uffff\1\157\1\162\1\uffff\1\157\1\u0080\1\162\1\130\1\162"+
        "\1\0\1\u0080\2\0\2\u0080\30\0";
    static final String DFA81_acceptS =
        "\1\uffff\1\2\2\uffff\1\1\43\uffff";
    static final String DFA81_specialS =
        "\2\uffff\1\1\1\3\1\uffff\1\7\1\uffff\1\5\1\uffff\1\2\1\4\1\uffff"+
        "\1\6\1\0\32\uffff}>";
    static final String[] DFA81_transitionS = {
            "\2\1\5\uffff\1\1\1\2",
            "",
            "\1\3\4\uffff\1\5\13\uffff\7\4",
            "\1\7\1\1\2\uffff\1\4\1\6\3\uffff\1\1\1\uffff\2\4\4\uffff\6\1"+
            "\1\10\2\4\1\1",
            "",
            "\1\11\4\uffff\1\12\13\uffff\7\4",
            "\124\26\1\13\3\26\1\25\1\14\1\26\1\15\3\26\2\25\4\26\1\16\1"+
            "\17\1\20\1\21\1\22\1\23\1\24\2\25\17\26",
            "\1\27\1\4\2\uffff\1\30\1\31\2\4\3\uffff\2\30\2\4\10\uffff\1"+
            "\1\2\30\1\4",
            "\1\32",
            "\1\34\1\1\2\uffff\1\4\1\33\3\uffff\1\1\1\uffff\2\4\4\uffff\6"+
            "\1\1\35\2\4\1\1",
            "\1\uffff",
            "\124\26\1\36\1\43\2\26\1\37\1\40\1\44\1\45\3\26\2\37\12\26\1"+
            "\41\2\37\1\42\16\26",
            "\1\uffff",
            "\1\uffff",
            "\124\26\1\46\3\26\1\37\1\47\1\26\1\15\3\26\2\37\13\26\2\37\17"+
            "\26",
            "\124\26\1\46\3\26\1\37\1\47\1\26\1\15\3\26\2\37\13\26\2\37\17"+
            "\26",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA81_eot = DFA.unpackEncodedString(DFA81_eotS);
    static final short[] DFA81_eof = DFA.unpackEncodedString(DFA81_eofS);
    static final char[] DFA81_min = DFA.unpackEncodedStringToUnsignedChars(DFA81_minS);
    static final char[] DFA81_max = DFA.unpackEncodedStringToUnsignedChars(DFA81_maxS);
    static final short[] DFA81_accept = DFA.unpackEncodedString(DFA81_acceptS);
    static final short[] DFA81_special = DFA.unpackEncodedString(DFA81_specialS);
    static final short[][] DFA81_transition;

    static {
        int numStates = DFA81_transitionS.length;
        DFA81_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA81_transition[i] = DFA.unpackEncodedString(DFA81_transitionS[i]);
        }
    }

    class DFA81 extends DFA {

        public DFA81(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 81;
            this.eot = DFA81_eot;
            this.eof = DFA81_eof;
            this.min = DFA81_min;
            this.max = DFA81_max;
            this.accept = DFA81_accept;
            this.special = DFA81_special;
            this.transition = DFA81_transition;
        }
        public String getDescription() {
            return "()* loopback of 759:26: ({...}? => DOUBLE_AMPER constraint_expression )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA81_13 = input.LA(1);

                         
                        int index81_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index81_13);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA81_2 = input.LA(1);

                         
                        int index81_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA81_2==ID) ) {s = 3;}

                        else if ( ((LA81_2>=EQUAL && LA81_2<=GRAVE_ACCENT)) && ((validateRestr()))) {s = 4;}

                        else if ( (LA81_2==LEFT_PAREN) ) {s = 5;}

                         
                        input.seek(index81_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA81_9 = input.LA(1);

                         
                        int index81_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA81_9==DOT||LA81_9==COLON||(LA81_9>=EQUAL && LA81_9<=NOT_EQUAL)||LA81_9==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA81_9==LEFT_PAREN) ) {s = 27;}

                        else if ( (LA81_9==ID) ) {s = 28;}

                        else if ( (LA81_9==STRING||(LA81_9>=BOOL && LA81_9<=INT)||(LA81_9>=FLOAT && LA81_9<=NULL)) && ((validateRestr()))) {s = 4;}

                        else if ( (LA81_9==GRAVE_ACCENT) ) {s = 29;}

                         
                        input.seek(index81_9);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA81_3 = input.LA(1);

                         
                        int index81_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA81_3==DOT||LA81_3==COLON||(LA81_3>=EQUAL && LA81_3<=NOT_EQUAL)||LA81_3==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA81_3==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA81_3==ID) ) {s = 7;}

                        else if ( (LA81_3==GRAVE_ACCENT) ) {s = 8;}

                        else if ( (LA81_3==STRING||(LA81_3>=BOOL && LA81_3<=INT)||(LA81_3>=FLOAT && LA81_3<=NULL)) && ((validateRestr()))) {s = 4;}

                         
                        input.seek(index81_3);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA81_10 = input.LA(1);

                         
                        int index81_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index81_10);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA81_7 = input.LA(1);

                         
                        int index81_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA81_7==ID) ) {s = 23;}

                        else if ( (LA81_7==STRING||(LA81_7>=BOOL && LA81_7<=INT)||(LA81_7>=FLOAT && LA81_7<=NULL)) ) {s = 24;}

                        else if ( (LA81_7==LEFT_PAREN) ) {s = 25;}

                        else if ( (LA81_7==DOT||(LA81_7>=COMMA && LA81_7<=RIGHT_PAREN)||(LA81_7>=DOUBLE_PIPE && LA81_7<=DOUBLE_AMPER)||LA81_7==LEFT_SQUARE) && ((validateRestr()))) {s = 4;}

                        else if ( (LA81_7==GRAVE_ACCENT) ) {s = 1;}

                         
                        input.seek(index81_7);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA81_12 = input.LA(1);

                         
                        int index81_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index81_12);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA81_5 = input.LA(1);

                         
                        int index81_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA81_5==ID) ) {s = 9;}

                        else if ( ((LA81_5>=EQUAL && LA81_5<=GRAVE_ACCENT)) && ((validateRestr()))) {s = 4;}

                        else if ( (LA81_5==LEFT_PAREN) ) {s = 10;}

                         
                        input.seek(index81_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 81, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_package_statement_in_compilation_unit408 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_statement_in_compilation_unit413 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_EOF_in_compilation_unit418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_package_key_in_package_statement469 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_package_id_in_package_statement471 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_package_statement473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_id497 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_DOT_in_package_id503 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_package_id507 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_rule_attribute_in_statement545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_declaration_in_statement586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_import_statement618 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_import_name_in_import_statement620 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_import_statement623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_function_import_statement658 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_function_key_in_function_import_statement660 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement662 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_function_import_statement665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name694 = new BitSet(new long[]{0x0000000000000002L,0x0000000006000000L});
    public static final BitSet FOLLOW_DOT_in_import_name700 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_import_name704 = new BitSet(new long[]{0x0000000000000002L,0x0000000006000000L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_key_in_global751 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_data_type_in_global753 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_global_id_in_global755 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_global757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_id783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_key_in_function815 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_data_type_in_function817 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_function_id_in_function820 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_parameters_in_function822 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_id854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_key_in_query886 = new BitSet(new long[]{0x0000000000000000L,0x0000000011000000L});
    public static final BitSet FOLLOW_query_id_in_query888 = new BitSet(new long[]{0x0000000000000000L,0x0000000029000000L});
    public static final BitSet FOLLOW_parameters_in_query890 = new BitSet(new long[]{0x0000000000000000L,0x0000000029000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query893 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_END_in_query895 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_query897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_id929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_query_id945 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parameters964 = new BitSet(new long[]{0x0000000000000000L,0x0000000081000000L});
    public static final BitSet FOLLOW_param_definition_in_parameters971 = new BitSet(new long[]{0x0000000000000000L,0x00000000C0000000L});
    public static final BitSet FOLLOW_COMMA_in_parameters974 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_param_definition_in_parameters976 = new BitSet(new long[]{0x0000000000000000L,0x00000000C0000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parameters985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_param_definition1009 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_argument_in_param_definition1012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument1023 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_dimension_definition_in_argument1025 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_declare_key_in_type_declaration1048 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_type_declare_id_in_type_declaration1051 = new BitSet(new long[]{0x0000000000000000L,0x0000000109000000L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration1055 = new BitSet(new long[]{0x0000000000000000L,0x0000000109000000L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration1060 = new BitSet(new long[]{0x0000000000000000L,0x0000000009000000L});
    public static final BitSet FOLLOW_END_in_type_declaration1065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_type_declare_id1097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_decl_metadata1116 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_decl_metadata1118 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_decl_metadata1120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_decl_field1143 = new BitSet(new long[]{0x0000000000000000L,0x0000000600000000L});
    public static final BitSet FOLLOW_decl_field_initialization_in_decl_field1145 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_decl_field1148 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_data_type_in_decl_field1150 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field1154 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_EQUALS_in_decl_field_initialization1182 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_decl_field_initialization1184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_key_in_template1215 = new BitSet(new long[]{0x0000000000000000L,0x0000000011000000L});
    public static final BitSet FOLLOW_template_id_in_template1217 = new BitSet(new long[]{0x0000000000000000L,0x0000000001800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1219 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_template_slot_in_template1224 = new BitSet(new long[]{0x0000000000000000L,0x0000000009000000L});
    public static final BitSet FOLLOW_END_in_template1229 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_id1261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_template_id1277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_template_slot1297 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_slot_id_in_template_slot1299 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template_slot1301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_slot_id1326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_key_in_rule1355 = new BitSet(new long[]{0x0000000000000000L,0x0000000011000000L});
    public static final BitSet FOLLOW_rule_id_in_rule1357 = new BitSet(new long[]{0x0000000000000000L,0x0010000001000000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule1359 = new BitSet(new long[]{0x0000000000000000L,0x0010000001000000L});
    public static final BitSet FOLLOW_when_part_in_rule1362 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_when_key_in_when_part1394 = new BitSet(new long[]{0x0000000000000002L,0x0000000221000000L});
    public static final BitSet FOLLOW_COLON_in_when_part1396 = new BitSet(new long[]{0x0000000000000002L,0x0000000021000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_when_part1399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_id1420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_rule_id1436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attributes_key_in_rule_attributes1457 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_rule_attributes1459 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1464 = new BitSet(new long[]{0x0000000000000002L,0x0000000041000000L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1468 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1473 = new BitSet(new long[]{0x0000000000000002L,0x0000000041000000L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_key_in_date_effective1592 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_key_in_date_expires1606 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_key_in_enabled1623 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_salience_key_in_salience1638 = new BitSet(new long[]{0x0000000000000000L,0x0000001020000000L});
    public static final BitSet FOLLOW_INT_in_salience1645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_key_in_no_loop1670 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_key_in_auto_focus1685 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_key_in_activation_group1702 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_key_in_ruleflow_group1716 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_key_in_agenda_group1730 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_key_in_duration1744 = new BitSet(new long[]{0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_INT_in_duration1747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_key_in_dialect1761 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_dialect1764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_key_in_lock_on_active1782 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1797 = new BitSet(new long[]{0x0000000000000002L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or1839 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or1843 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1845 = new BitSet(new long[]{0x0000000000000000L,0x00000000A1000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or1848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1869 = new BitSet(new long[]{0x0000000000000002L,0x0000002001000000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or1891 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_lhs_or1898 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1903 = new BitSet(new long[]{0x0000000000000002L,0x0000002001000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and1941 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and1945 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1947 = new BitSet(new long[]{0x0000000000000000L,0x00000000A1000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and1950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1971 = new BitSet(new long[]{0x0000000000000002L,0x0000004001000000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and1993 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_lhs_and2000 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2005 = new BitSet(new long[]{0x0000000000000002L,0x0000004001000000L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2043 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2049 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2055 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary2061 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2067 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary2070 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2072 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_unary2078 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_lhs_unary2092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_key_in_lhs_exist2106 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist2137 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2139 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist2141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist2154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not2200 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2224 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2226 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_key_in_lhs_eval2275 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forall_key_in_lhs_forall2303 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2305 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2307 = new BitSet(new long[]{0x0000000000000000L,0x0000000081000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall2310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_pattern_source2342 = new BitSet(new long[]{0x0000000000000002L,0x0000008001000000L});
    public static final BitSet FOLLOW_over_clause_in_pattern_source2346 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_from_key_in_pattern_source2356 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_accumulate_statement_in_pattern_source2372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_pattern_source2388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entrypoint_statement_in_pattern_source2405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_pattern_source2421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause2449 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2452 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_COMMA_in_over_clause2455 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2458 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_ID_in_over_elements2471 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_over_elements2473 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_over_elements2475 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_over_elements2477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_key_in_accumulate_statement2501 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2505 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_accumulate_statement2507 = new BitSet(new long[]{0x0000000000000000L,0x0000000041000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2509 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_statement2517 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_statement2523 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_init_key_in_accumulate_init_clause2560 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2565 = new BitSet(new long[]{0x0000000000000000L,0x0000000041000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2567 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_action_key_in_accumulate_init_clause2571 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2575 = new BitSet(new long[]{0x0000000000000000L,0x0000000041000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2577 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_reverse_key_in_accumulate_init_clause2583 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2587 = new BitSet(new long[]{0x0000000000000000L,0x0000000041000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2589 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_result_key_in_accumulate_init_clause2595 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause2648 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_id_clause2652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_key_in_collect_statement2674 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement2678 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_pattern_source_in_collect_statement2680 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement2682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entry_point_key_in_entrypoint_statement2704 = new BitSet(new long[]{0x0000000000000000L,0x0000000011000000L});
    public static final BitSet FOLLOW_entrypoint_id_in_entrypoint_statement2706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entrypoint_id2729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_entrypoint_id2742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source2758 = new BitSet(new long[]{0x0000000000000002L,0x0000000022000000L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source2771 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_expression_chain_in_from_source2778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain2810 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_expression_chain2812 = new BitSet(new long[]{0x0000000000000002L,0x0004000022000000L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain2832 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain2854 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain2865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern2898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern2911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_fact_binding2931 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_fact_in_fact_binding2937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding2944 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_fact_binding_expression_in_fact_binding2946 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding2948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression2987 = new BitSet(new long[]{0x0000000000000002L,0x0000002001000000L});
    public static final BitSet FOLLOW_or_key_in_fact_binding_expression2999 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3005 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression3010 = new BitSet(new long[]{0x0000000000000002L,0x0000002001000000L});
    public static final BitSet FOLLOW_pattern_type_in_fact3050 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact3052 = new BitSet(new long[]{0x0000000000000000L,0x00000000A1000000L});
    public static final BitSet FOLLOW_constraints_in_fact3054 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact3057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints3082 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_COMMA_in_constraints3086 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_constraint_in_constraints3089 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_or_constr_in_constraint3103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3114 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr3118 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3121 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3136 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr3140 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3143 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_eval_key_in_unary_constr3164 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_unary_constr3167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr3172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr3177 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr3180 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr3182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_field_constraint3196 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3198 = new BitSet(new long[]{0x0000000000000002L,0x0000FF0021000000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_field_constraint3208 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_field_constraint3210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3264 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0021000000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_label3290 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_label3292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3308 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective3314 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0021000000L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3317 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3332 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective3338 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0021000000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3341 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression3363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression3368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression3373 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0021000000L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression3376 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression3378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUAL_in_simple_operator3397 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_GREATER_in_simple_operator3403 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_simple_operator3409 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_LESS_in_simple_operator3415 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_simple_operator3421 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_simple_operator3427 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_not_key_in_simple_operator3433 = new BitSet(new long[]{0x0000000000000000L,0x0000800001000000L});
    public static final BitSet FOLLOW_contains_key_in_simple_operator3436 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_soundslike_key_in_simple_operator3439 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_matches_key_in_simple_operator3442 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_memberof_key_in_simple_operator3445 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3449 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_GRAVE_ACCENT_in_simple_operator3454 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3457 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator3460 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_contains_key_in_simple_operator3466 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_excludes_key_in_simple_operator3472 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_matches_key_in_simple_operator3478 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_soundslike_key_in_simple_operator3484 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_memberof_key_in_simple_operator3490 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3496 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_GRAVE_ACCENT_in_simple_operator3502 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3505 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator3508 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator3512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_compound_operator3527 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_not_key_in_compound_operator3532 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_in_key_in_compound_operator3534 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator3539 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3542 = new BitSet(new long[]{0x0000000000000000L,0x00000000C0000000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator3546 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3549 = new BitSet(new long[]{0x0000000000000000L,0x00000000C0000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator3554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_expression_value3565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value3570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_value3576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_literal_constraint0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pattern_type3620 = new BitSet(new long[]{0x0000000000000002L,0x0004000002000000L});
    public static final BitSet FOLLOW_DOT_in_pattern_type3626 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_pattern_type3630 = new BitSet(new long[]{0x0000000000000002L,0x0004000002000000L});
    public static final BitSet FOLLOW_dimension_definition_in_pattern_type3645 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_ID_in_data_type3671 = new BitSet(new long[]{0x0000000000000002L,0x0004000002000000L});
    public static final BitSet FOLLOW_DOT_in_data_type3675 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_data_type3677 = new BitSet(new long[]{0x0000000000000002L,0x0004000002000000L});
    public static final BitSet FOLLOW_dimension_definition_in_data_type3682 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dimension_definition3708 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dimension_definition3710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path3721 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_DOT_in_accessor_path3725 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path3727 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_accessor_element3751 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_square_chunk_in_accessor_element3753 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_data_in_rhs_chunk3782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk_data3801 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_rhs_chunk_data3805 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_END_in_rhs_chunk_data3811 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_rhs_chunk_data3813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk3830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk_data3849 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_curly_chunk_data3852 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk_data3866 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk_data3871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk3887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk_data3907 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_paren_chunk_data3910 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk_data3924 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk_data3929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk3946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk_data3965 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_square_chunk_data3968 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk_data3982 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk_data3987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_effective_key4006 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_effective_key4008 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_date_effective_key4010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_expires_key4036 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_expires_key4038 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_date_expires_key4040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4066 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key4068 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4070 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key4072 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_no_loop_key4100 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_no_loop_key4102 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_no_loop_key4104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key4130 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_auto_focus_key4132 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key4134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_activation_group_key4160 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_activation_group_key4162 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_activation_group_key4164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key4190 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_agenda_group_key4192 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key4194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key4220 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_ruleflow_group_key4222 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key4224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_duration_key4249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_key4271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_key4293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dialect_key4315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_salience_key4337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enabled_key4359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_attributes_key4381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_when_key4403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_key4425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_key4447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_key4469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_declare_key4491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_key4513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_key4535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_eval_key4557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_contains_key4579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_matches_key4601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_excludes_key4623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_soundslike_key4645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_memberof_key4667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key4689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key4711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_or_key4733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_and_key4755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_exists_key4777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_forall_key4799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_key4821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entry_point_key4844 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_entry_point_key4846 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_entry_point_key4848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_accumulate_key4873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_init_key4895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_action_key4917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_reverse_key4939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_result_key4961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_collect_key4983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred11833 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_or_key_in_synpred11835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_key_in_synpred21882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred21884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred31935 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_and_key_in_synpred31937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred41984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred41986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_synpred52088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred62120 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_or_key_in_synpred62123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred62125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred72207 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_or_key_in_synpred72210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred72212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred82765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred92826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred102848 = new BitSet(new long[]{0x0000000000000002L});

}