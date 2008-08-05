// $ANTLR 3.0.1 /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2008-08-04 21:40:30

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
            ruleMemo = new HashMap[146+1];
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
    	
    	private boolean validateNotWithBinding(){
    		if (input.LA(1) == ID && input.LA(2) == ID && input.LA(3) == COLON){
    			return true;
    		}
    		return false;
    	}

    	private boolean validateWhenWithParenthesis(){
    		if (input.LA(1) == ID && validateIdentifierKey(DroolsSoftKeywords.WHEN) && input.LA(2) == COLON && input.LA(3) == LEFT_PAREN && !(validateLT(4, DroolsSoftKeywords.OR) || validateLT(4, DroolsSoftKeywords.AND))){
    			return true;
    		} else if (input.LA(1) == ID && validateIdentifierKey(DroolsSoftKeywords.WHEN) && input.LA(2) == LEFT_PAREN && !(validateLT(3, DroolsSoftKeywords.OR) || validateLT(3, DroolsSoftKeywords.AND))){
    			return true;
    		}
    		return false;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:295:1: compilation_unit : ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:296:2: ( ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:296:4: ( package_statement )? ( statement )* EOF
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:296:4: ( package_statement )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))))) {
                int LA1_1 = input.LA(2);

                if ( (LA1_1==ID) && ((((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))))) {
                    int LA1_4 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.PACKAGE))) ) {
                        alt1=1;
                    }
                }
            }
            switch (alt1) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:296:4: package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_compilation_unit408);
                    package_statement1=package_statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_package_statement.add(package_statement1.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:297:3: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:297:3: statement
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
            // 299:3: -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:299:6: ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_COMPILATION_UNIT, "VT_COMPILATION_UNIT"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:299:28: ( package_statement )?
                if ( stream_package_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_package_statement.next());

                }
                stream_package_statement.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:299:47: ( statement )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:307:1: package_statement : package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:310:2: ( package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:310:4: package_key package_id ( SEMICOLON )?
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:310:27: ( SEMICOLON )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==SEMICOLON) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:310:27: SEMICOLON
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
            // 311:3: -> ^( package_key package_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:311:6: ^( package_key package_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:314:1: package_id : id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:315:2: (id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:315:4: id+= ID (id+= DOT id+= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_id497); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:315:11: (id+= DOT id+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:315:13: id+= DOT id+= ID
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
            // 317:3: -> ^( VT_PACKAGE_ID ( ID )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:317:6: ^( VT_PACKAGE_ID ( ID )+ )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:320:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:323:3: ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query )
            int alt5=9;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                    alt5=1;
                }
                else if ( (LA5_1==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))))) {
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
                            new NoViableAltException("320:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 3, input);

                        throw nvae;
                    }
                }
                else if ( (LA5_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {
                    alt5=1;
                }
                else if ( (LA5_1==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {
                    alt5=1;
                }
                else if ( (LA5_1==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))))) {
                    int LA5_6 = input.LA(3);

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
                            new NoViableAltException("320:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 6, input);

                        throw nvae;
                    }
                }
                else if ( (LA5_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {
                    alt5=1;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("320:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("320:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:323:5: rule_attribute
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:324:3: {...}? => function_import_statement
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:325:4: import_statement
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:326:4: global
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:4: function
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:328:4: {...}? => template
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:329:4: {...}? => type_declaration
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:330:4: rule
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:331:4: query
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:334:1: import_statement : import_key import_name[DroolsParaphareseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:337:2: ( import_key import_name[DroolsParaphareseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:337:4: import_key import_name[DroolsParaphareseTypes.IMPORT] ( SEMICOLON )?
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:337:58: ( SEMICOLON )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==SEMICOLON) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:337:58: SEMICOLON
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
            // 338:3: -> ^( import_key import_name )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:338:6: ^( import_key import_name )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:341:1: function_import_statement : imp= import_key function_key import_name[DroolsParaphareseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:344:2: (imp= import_key function_key import_name[DroolsParaphareseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:344:4: imp= import_key function_key import_name[DroolsParaphareseTypes.FUNCTION_IMPORT] ( SEMICOLON )?
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:344:84: ( SEMICOLON )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==SEMICOLON) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:344:84: SEMICOLON
                    {
                    SEMICOLON21=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_function_import_statement665); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON21);


                    }
                    break;

            }


            // AST REWRITE
            // elements: import_name, function_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 345:3: -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:345:6: ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:348:1: import_name[int importType] : id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:349:2: (id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:349:4: id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name694); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:349:11: (id+= DOT id+= ID )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==DOT) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:349:13: id+= DOT id+= ID
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

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:349:33: (id+= DOT_STAR )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==DOT_STAR) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:349:33: id+= DOT_STAR
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
            // elements: ID, DOT_STAR
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 351:3: -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:351:6: ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
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
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:351:25: ( DOT_STAR )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:354:1: global : global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:357:2: ( global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:357:4: global_key data_type global_id ( SEMICOLON )?
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:357:35: ( SEMICOLON )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==SEMICOLON) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:357:35: SEMICOLON
                    {
                    SEMICOLON25=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_global757); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON25);


                    }
                    break;

            }


            // AST REWRITE
            // elements: data_type, global_id, global_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 358:3: -> ^( global_key data_type global_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:358:6: ^( global_key data_type global_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:361:1: global_id : id= ID -> VT_GLOBAL_ID[$id] ;
    public final global_id_return global_id() throws RecognitionException {
        global_id_return retval = new global_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:362:2: (id= ID -> VT_GLOBAL_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:362:4: id= ID
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
            // 364:3: -> VT_GLOBAL_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:1: function : function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:370:2: ( function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:370:4: function_key ( data_type )? function_id parameters curly_chunk
            {
            pushFollow(FOLLOW_function_key_in_function815);
            function_key26=function_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_function_key.add(function_key26.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:370:17: ( data_type )?
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:370:17: data_type
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
            // elements: curly_chunk, data_type, function_key, function_id, parameters
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 371:3: -> ^( function_key ( data_type )? function_id parameters curly_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:6: ^( function_key ( data_type )? function_id parameters curly_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_function_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:21: ( data_type )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:374:1: function_id : id= ID -> VT_FUNCTION_ID[$id] ;
    public final function_id_return function_id() throws RecognitionException {
        function_id_return retval = new function_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:375:2: (id= ID -> VT_FUNCTION_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:375:4: id= ID
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
            // 377:3: -> VT_FUNCTION_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:380:1: query : query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block END ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:2: ( query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:4: query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )?
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:23: ( parameters )?
            int alt12=2;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:23: parameters
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

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:56: ( SEMICOLON )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==SEMICOLON) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:56: SEMICOLON
                    {
                    SEMICOLON36=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_query897); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON36);


                    }
                    break;

            }


            // AST REWRITE
            // elements: normal_lhs_block, query_key, query_id, END, parameters
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 384:3: -> ^( query_key query_id ( parameters )? normal_lhs_block END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:384:6: ^( query_key query_id ( parameters )? normal_lhs_block END )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_query_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_query_id.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:384:27: ( parameters )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:387:1: query_id : (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] );
    public final query_id_return query_id() throws RecognitionException {
        query_id_return retval = new query_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:388:2: (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] )
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
                    new NoViableAltException("387:1: query_id : (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] );", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:388:5: id= ID
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
                    // 389:67: -> VT_QUERY_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_QUERY_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:390:5: id= STRING
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
                    // 391:67: -> VT_QUERY_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:394:1: parameters : LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:395:2: ( LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:395:4: LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN
            {
            LEFT_PAREN37=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parameters964); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN37);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:396:4: ( param_definition ( COMMA param_definition )* )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==ID) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:396:6: param_definition ( COMMA param_definition )*
                    {
                    pushFollow(FOLLOW_param_definition_in_parameters971);
                    param_definition38=param_definition();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_param_definition.add(param_definition38.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:396:23: ( COMMA param_definition )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==COMMA) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:396:24: COMMA param_definition
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
            // 398:3: -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:398:6: ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_PARAM_LIST, "VT_PARAM_LIST"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:398:22: ( param_definition )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:401:1: param_definition : ( data_type )? argument ;
    public final param_definition_return param_definition() throws RecognitionException {
        param_definition_return retval = new param_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        data_type_return data_type42 = null;

        argument_return argument43 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:402:2: ( ( data_type )? argument )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:402:4: ( data_type )? argument
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:402:4: ( data_type )?
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:402:4: data_type
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:405:1: argument : ID ( dimension_definition )* ;
    public final argument_return argument() throws RecognitionException {
        argument_return retval = new argument_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID44=null;
        dimension_definition_return dimension_definition45 = null;


        Object ID44_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:406:2: ( ID ( dimension_definition )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:406:4: ID ( dimension_definition )*
            {
            root_0 = (Object)adaptor.nil();

            ID44=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument1023); if (failed) return retval;
            if ( backtracking==0 ) {
            ID44_tree = (Object)adaptor.create(ID44);
            adaptor.addChild(root_0, ID44_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:406:7: ( dimension_definition )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==LEFT_SQUARE) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:406:7: dimension_definition
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:409:1: type_declaration : declare_key type_declare_id ( decl_metadata )* ( decl_field )* END -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:412:2: ( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:412:4: declare_key type_declare_id ( decl_metadata )* ( decl_field )* END
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:3: ( decl_metadata )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AT) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:3: decl_metadata
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

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:3: ( decl_field )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==ID) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:3: decl_field
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
            // elements: decl_metadata, decl_field, END, declare_key, type_declare_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 416:3: -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:416:6: ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_declare_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_type_declare_id.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:416:36: ( decl_metadata )*
                while ( stream_decl_metadata.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_metadata.next());

                }
                stream_decl_metadata.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:416:51: ( decl_field )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:419:1: type_declare_id : id= ID -> VT_TYPE_DECLARE_ID[$id] ;
    public final type_declare_id_return type_declare_id() throws RecognitionException {
        type_declare_id_return retval = new type_declare_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:420:2: (id= ID -> VT_TYPE_DECLARE_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:420:5: id= ID
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
            // 421:74: -> VT_TYPE_DECLARE_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:424:1: decl_metadata : AT ID paren_chunk -> ^( AT ID paren_chunk ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:425:2: ( AT ID paren_chunk -> ^( AT ID paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:425:4: AT ID paren_chunk
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
            // elements: ID, AT, paren_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 426:3: -> ^( AT ID paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:426:6: ^( AT ID paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:429:1: decl_field : ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:430:2: ( ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:430:4: ID ( decl_field_initialization )? COLON data_type ( decl_metadata )*
            {
            ID54=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_decl_field1143); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID54);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:430:7: ( decl_field_initialization )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==EQUALS) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:430:7: decl_field_initialization
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:431:3: ( decl_metadata )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==AT) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:431:3: decl_metadata
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
            // elements: decl_field_initialization, data_type, ID, decl_metadata
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 432:3: -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:432:6: ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ID.next(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:432:11: ( decl_field_initialization )?
                if ( stream_decl_field_initialization.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_field_initialization.next());

                }
                stream_decl_field_initialization.reset();
                adaptor.addChild(root_1, stream_data_type.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:432:48: ( decl_metadata )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:435:1: decl_field_initialization : EQUALS paren_chunk -> ^( EQUALS paren_chunk ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:436:2: ( EQUALS paren_chunk -> ^( EQUALS paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:436:4: EQUALS paren_chunk
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
            // 437:2: -> ^( EQUALS paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:437:5: ^( EQUALS paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:1: template : template_key template_id ( SEMICOLON )? ( template_slot )+ END ( SEMICOLON )? -> ^( template_key template_id ( template_slot )+ END ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:443:2: ( template_key template_id ( SEMICOLON )? ( template_slot )+ END ( SEMICOLON )? -> ^( template_key template_id ( template_slot )+ END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:443:4: template_key template_id ( SEMICOLON )? ( template_slot )+ END ( SEMICOLON )?
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:443:29: ( SEMICOLON )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==SEMICOLON) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:443:29: SEMICOLON
                    {
                    SEMICOLON63=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1219); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON63);


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:444:3: ( template_slot )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:444:3: template_slot
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

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:445:7: ( SEMICOLON )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==SEMICOLON) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:445:7: SEMICOLON
                    {
                    SEMICOLON66=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1231); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON66);


                    }
                    break;

            }


            // AST REWRITE
            // elements: template_slot, template_id, END, template_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 446:3: -> ^( template_key template_id ( template_slot )+ END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:446:6: ^( template_key template_id ( template_slot )+ END )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:449:1: template_id : (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] );
    public final template_id_return template_id() throws RecognitionException {
        template_id_return retval = new template_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:450:2: (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] )
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
                    new NoViableAltException("449:1: template_id : (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] );", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:450:5: id= ID
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
                    // 451:70: -> VT_TEMPLATE_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_TEMPLATE_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:5: id= STRING
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
                    // 453:70: -> VT_TEMPLATE_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:456:1: template_slot : data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:457:2: ( data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:457:5: data_type slot_id ( SEMICOLON )?
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:457:23: ( SEMICOLON )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==SEMICOLON) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:457:23: SEMICOLON
                    {
                    SEMICOLON69=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template_slot1301); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON69);


                    }
                    break;

            }


            // AST REWRITE
            // elements: data_type, slot_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 458:3: -> ^( VT_SLOT data_type slot_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:458:6: ^( VT_SLOT data_type slot_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:1: slot_id : id= ID -> VT_SLOT_ID[$id] ;
    public final slot_id_return slot_id() throws RecognitionException {
        slot_id_return retval = new slot_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:9: (id= ID -> VT_SLOT_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:11: id= ID
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
            // 462:3: -> VT_SLOT_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:465:1: rule : rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:2: ( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:4: rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:21: ( rule_attributes )?
            int alt28=2;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:21: rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1359);
                    rule_attributes72=rule_attributes();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_rule_attributes.add(rule_attributes72.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:38: ( when_part )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:38: when_part
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
            // elements: rule_key, rule_attributes, when_part, rhs_chunk, rule_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 469:3: -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:6: ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_rule_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rule_id.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:25: ( rule_attributes )?
                if ( stream_rule_attributes.hasNext() ) {
                    adaptor.addChild(root_1, stream_rule_attributes.next());

                }
                stream_rule_attributes.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:42: ( when_part )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:472:1: when_part : ({...}? when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block | when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block );
    public final when_part_return when_part() throws RecognitionException {
        when_part_return retval = new when_part_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON76=null;
        Token COLON79=null;
        when_key_return when_key75 = null;

        normal_lhs_block_return normal_lhs_block77 = null;

        when_key_return when_key78 = null;

        normal_lhs_block_return normal_lhs_block80 = null;


        Object COLON76_tree=null;
        Object COLON79_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        RewriteRuleSubtreeStream stream_when_key=new RewriteRuleSubtreeStream(adaptor,"rule when_key");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:473:2: ({...}? when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block | when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {
                int LA32_1 = input.LA(2);

                if ( ((validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN)))) ) {
                    alt32=1;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {
                    alt32=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("472:1: when_part : ({...}? when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block | when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block );", 32, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("472:1: when_part : ({...}? when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block | when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block );", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:473:4: {...}? when_key ( COLON )? normal_lhs_block
                    {
                    if ( !(validateWhenWithParenthesis()) ) {
                        if (backtracking>0) {failed=true; return retval;}
                        throw new FailedPredicateException(input, "when_part", "validateWhenWithParenthesis()");
                    }
                    pushFollow(FOLLOW_when_key_in_when_part1396);
                    when_key75=when_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_when_key.add(when_key75.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:473:46: ( COLON )?
                    int alt30=2;
                    int LA30_0 = input.LA(1);

                    if ( (LA30_0==COLON) ) {
                        alt30=1;
                    }
                    switch (alt30) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:473:46: COLON
                            {
                            COLON76=(Token)input.LT(1);
                            match(input,COLON,FOLLOW_COLON_in_when_part1398); if (failed) return retval;
                            if ( backtracking==0 ) stream_COLON.add(COLON76);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_normal_lhs_block_in_when_part1401);
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
                    // 474:2: -> when_key normal_lhs_block
                    {
                        adaptor.addChild(root_0, stream_when_key.next());
                        adaptor.addChild(root_0, stream_normal_lhs_block.next());

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:475:4: when_key ( COLON )? normal_lhs_block
                    {
                    pushFollow(FOLLOW_when_key_in_when_part1413);
                    when_key78=when_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_when_key.add(when_key78.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:475:13: ( COLON )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==COLON) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:475:13: COLON
                            {
                            COLON79=(Token)input.LT(1);
                            match(input,COLON,FOLLOW_COLON_in_when_part1415); if (failed) return retval;
                            if ( backtracking==0 ) stream_COLON.add(COLON79);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_normal_lhs_block_in_when_part1418);
                    normal_lhs_block80=normal_lhs_block();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block80.getTree());

                    // AST REWRITE
                    // elements: when_key, normal_lhs_block
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 476:2: -> when_key normal_lhs_block
                    {
                        adaptor.addChild(root_0, stream_when_key.next());
                        adaptor.addChild(root_0, stream_normal_lhs_block.next());

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
    // $ANTLR end when_part

    public static class rule_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rule_id
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:479:1: rule_id : (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] );
    public final rule_id_return rule_id() throws RecognitionException {
        rule_id_return retval = new rule_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:480:2: (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==ID) ) {
                alt33=1;
            }
            else if ( (LA33_0==STRING) ) {
                alt33=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("479:1: rule_id : (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] );", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:480:5: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_rule_id1439); if (failed) return retval;
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
                    // 481:66: -> VT_RULE_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_RULE_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:5: id= STRING
                    {
                    id=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_rule_id1455); if (failed) return retval;
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
                    // 483:66: -> VT_RULE_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:1: rule_attributes : ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) ;
    public final rule_attributes_return rule_attributes() throws RecognitionException {
        rule_attributes_return retval = new rule_attributes_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON82=null;
        Token COMMA84=null;
        rule_attribute_return attr = null;

        attributes_key_return attributes_key81 = null;

        rule_attribute_return rule_attribute83 = null;


        Object COLON82_tree=null;
        Object COMMA84_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_attributes_key=new RewriteRuleSubtreeStream(adaptor,"rule attributes_key");
        RewriteRuleSubtreeStream stream_rule_attribute=new RewriteRuleSubtreeStream(adaptor,"rule rule_attribute");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:2: ( ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:4: ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:4: ( attributes_key COLON )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {
                int LA34_1 = input.LA(2);

                if ( (LA34_1==COLON) && ((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))) {
                    alt34=1;
                }
            }
            switch (alt34) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:6: attributes_key COLON
                    {
                    pushFollow(FOLLOW_attributes_key_in_rule_attributes1476);
                    attributes_key81=attributes_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_attributes_key.add(attributes_key81.getTree());
                    COLON82=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_rule_attributes1478); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON82);


                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1483);
            rule_attribute83=rule_attribute();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rule_attribute.add(rule_attribute83.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:45: ( ( COMMA )? attr= rule_attribute )*
            loop36:
            do {
                int alt36=2;
                alt36 = dfa36.predict(input);
                switch (alt36) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:47: ( COMMA )? attr= rule_attribute
            	    {
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:47: ( COMMA )?
            	    int alt35=2;
            	    int LA35_0 = input.LA(1);

            	    if ( (LA35_0==COMMA) ) {
            	        alt35=1;
            	    }
            	    switch (alt35) {
            	        case 1 :
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:47: COMMA
            	            {
            	            COMMA84=(Token)input.LT(1);
            	            match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1487); if (failed) return retval;
            	            if ( backtracking==0 ) stream_COMMA.add(COMMA84);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1492);
            	    attr=rule_attribute();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_rule_attribute.add(attr.getTree());

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);


            // AST REWRITE
            // elements: rule_attribute, attributes_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 488:3: -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:488:6: ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_RULE_ATTRIBUTES, "VT_RULE_ATTRIBUTES"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:488:27: ( attributes_key )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:491:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );
    public final rule_attribute_return rule_attribute() throws RecognitionException {
        rule_attribute_return retval = new rule_attribute_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        salience_return salience85 = null;

        no_loop_return no_loop86 = null;

        agenda_group_return agenda_group87 = null;

        duration_return duration88 = null;

        activation_group_return activation_group89 = null;

        auto_focus_return auto_focus90 = null;

        date_effective_return date_effective91 = null;

        date_expires_return date_expires92 = null;

        enabled_return enabled93 = null;

        ruleflow_group_return ruleflow_group94 = null;

        lock_on_active_return lock_on_active95 = null;

        dialect_return dialect96 = null;



         pushParaphrases(DroolsParaphareseTypes.RULE_ATTRIBUTE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:2: ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect )
            int alt37=12;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                int LA37_1 = input.LA(2);

                if ( (LA37_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                    int LA37_2 = input.LA(3);

                    if ( (LA37_2==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                        int LA37_7 = input.LA(4);

                        if ( (LA37_7==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))) {
                            alt37=11;
                        }
                        else if ( (LA37_7==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                            int LA37_10 = input.LA(5);

                            if ( ((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                                alt37=3;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                                alt37=5;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))) ) {
                                alt37=7;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))) ) {
                                alt37=8;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                                alt37=10;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("491:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 37, 10, input);

                                throw nvae;
                            }
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))) ) {
                            alt37=2;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))) ) {
                            alt37=6;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("491:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 37, 7, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("491:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 37, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA37_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {
                    int LA37_3 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {
                        alt37=1;
                    }
                    else if ( ((validateIdentifierKey(DroolsSoftKeywords.DURATION))) ) {
                        alt37=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("491:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 37, 3, input);

                        throw nvae;
                    }
                }
                else if ( (LA37_1==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {
                    alt37=1;
                }
                else if ( (LA37_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {
                    alt37=9;
                }
                else if ( (LA37_1==STRING) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {
                    alt37=12;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("491:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 37, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("491:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:4: salience
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_salience_in_rule_attribute1531);
                    salience85=salience();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, salience85.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:4: no_loop
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_no_loop_in_rule_attribute1537);
                    no_loop86=no_loop();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, no_loop86.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:496:4: agenda_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1544);
                    agenda_group87=agenda_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, agenda_group87.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:497:4: duration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_duration_in_rule_attribute1551);
                    duration88=duration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, duration88.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:498:4: activation_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_activation_group_in_rule_attribute1558);
                    activation_group89=activation_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, activation_group89.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:499:4: auto_focus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1564);
                    auto_focus90=auto_focus();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, auto_focus90.getTree());

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:500:4: date_effective
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_effective_in_rule_attribute1570);
                    date_effective91=date_effective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, date_effective91.getTree());

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:4: date_expires
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_expires_in_rule_attribute1576);
                    date_expires92=date_expires();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, date_expires92.getTree());

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:502:4: enabled
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enabled_in_rule_attribute1582);
                    enabled93=enabled();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, enabled93.getTree());

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:503:4: ruleflow_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1588);
                    ruleflow_group94=ruleflow_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, ruleflow_group94.getTree());

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:504:4: lock_on_active
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1594);
                    lock_on_active95=lock_on_active();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lock_on_active95.getTree());

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:505:4: dialect
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_dialect_in_rule_attribute1599);
                    dialect96=dialect();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, dialect96.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:508:1: date_effective : date_effective_key STRING ;
    public final date_effective_return date_effective() throws RecognitionException {
        date_effective_return retval = new date_effective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING98=null;
        date_effective_key_return date_effective_key97 = null;


        Object STRING98_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:509:2: ( date_effective_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:509:4: date_effective_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_effective_key_in_date_effective1611);
            date_effective_key97=date_effective_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_effective_key97.getTree(), root_0);
            STRING98=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1614); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING98_tree = (Object)adaptor.create(STRING98);
            adaptor.addChild(root_0, STRING98_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:512:1: date_expires : date_expires_key STRING ;
    public final date_expires_return date_expires() throws RecognitionException {
        date_expires_return retval = new date_expires_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING100=null;
        date_expires_key_return date_expires_key99 = null;


        Object STRING100_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:2: ( date_expires_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:4: date_expires_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_expires_key_in_date_expires1625);
            date_expires_key99=date_expires_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_expires_key99.getTree(), root_0);
            STRING100=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1628); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING100_tree = (Object)adaptor.create(STRING100);
            adaptor.addChild(root_0, STRING100_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:516:1: enabled : enabled_key BOOL ;
    public final enabled_return enabled() throws RecognitionException {
        enabled_return retval = new enabled_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL102=null;
        enabled_key_return enabled_key101 = null;


        Object BOOL102_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:517:2: ( enabled_key BOOL )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:517:4: enabled_key BOOL
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_enabled_key_in_enabled1642);
            enabled_key101=enabled_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(enabled_key101.getTree(), root_0);
            BOOL102=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1645); if (failed) return retval;
            if ( backtracking==0 ) {
            BOOL102_tree = (Object)adaptor.create(BOOL102);
            adaptor.addChild(root_0, BOOL102_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:520:1: salience : salience_key ( INT | paren_chunk ) ;
    public final salience_return salience() throws RecognitionException {
        salience_return retval = new salience_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT104=null;
        salience_key_return salience_key103 = null;

        paren_chunk_return paren_chunk105 = null;


        Object INT104_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:2: ( salience_key ( INT | paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:4: salience_key ( INT | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_salience_key_in_salience1657);
            salience_key103=salience_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(salience_key103.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:522:3: ( INT | paren_chunk )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==INT) ) {
                alt38=1;
            }
            else if ( (LA38_0==LEFT_PAREN) ) {
                alt38=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("522:3: ( INT | paren_chunk )", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:522:5: INT
                    {
                    INT104=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_salience1664); if (failed) return retval;
                    if ( backtracking==0 ) {
                    INT104_tree = (Object)adaptor.create(INT104);
                    adaptor.addChild(root_0, INT104_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:523:5: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1673);
                    paren_chunk105=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk105.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:527:1: no_loop : no_loop_key ( BOOL )? ;
    public final no_loop_return no_loop() throws RecognitionException {
        no_loop_return retval = new no_loop_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL107=null;
        no_loop_key_return no_loop_key106 = null;


        Object BOOL107_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:528:2: ( no_loop_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:528:4: no_loop_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_no_loop_key_in_no_loop1689);
            no_loop_key106=no_loop_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(no_loop_key106.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:528:17: ( BOOL )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==BOOL) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:528:17: BOOL
                    {
                    BOOL107=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1692); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL107_tree = (Object)adaptor.create(BOOL107);
                    adaptor.addChild(root_0, BOOL107_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:531:1: auto_focus : auto_focus_key ( BOOL )? ;
    public final auto_focus_return auto_focus() throws RecognitionException {
        auto_focus_return retval = new auto_focus_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL109=null;
        auto_focus_key_return auto_focus_key108 = null;


        Object BOOL109_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:532:2: ( auto_focus_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:532:4: auto_focus_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_auto_focus_key_in_auto_focus1704);
            auto_focus_key108=auto_focus_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(auto_focus_key108.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:532:20: ( BOOL )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==BOOL) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:532:20: BOOL
                    {
                    BOOL109=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1707); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL109_tree = (Object)adaptor.create(BOOL109);
                    adaptor.addChild(root_0, BOOL109_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:1: activation_group : activation_group_key STRING ;
    public final activation_group_return activation_group() throws RecognitionException {
        activation_group_return retval = new activation_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING111=null;
        activation_group_key_return activation_group_key110 = null;


        Object STRING111_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:536:2: ( activation_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:536:4: activation_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_activation_group_key_in_activation_group1721);
            activation_group_key110=activation_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(activation_group_key110.getTree(), root_0);
            STRING111=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1724); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING111_tree = (Object)adaptor.create(STRING111);
            adaptor.addChild(root_0, STRING111_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:539:1: ruleflow_group : ruleflow_group_key STRING ;
    public final ruleflow_group_return ruleflow_group() throws RecognitionException {
        ruleflow_group_return retval = new ruleflow_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING113=null;
        ruleflow_group_key_return ruleflow_group_key112 = null;


        Object STRING113_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:540:2: ( ruleflow_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:540:4: ruleflow_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_ruleflow_group_key_in_ruleflow_group1735);
            ruleflow_group_key112=ruleflow_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(ruleflow_group_key112.getTree(), root_0);
            STRING113=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1738); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING113_tree = (Object)adaptor.create(STRING113);
            adaptor.addChild(root_0, STRING113_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:1: agenda_group : agenda_group_key STRING ;
    public final agenda_group_return agenda_group() throws RecognitionException {
        agenda_group_return retval = new agenda_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING115=null;
        agenda_group_key_return agenda_group_key114 = null;


        Object STRING115_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:544:2: ( agenda_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:544:4: agenda_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_agenda_group_key_in_agenda_group1749);
            agenda_group_key114=agenda_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(agenda_group_key114.getTree(), root_0);
            STRING115=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1752); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING115_tree = (Object)adaptor.create(STRING115);
            adaptor.addChild(root_0, STRING115_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:547:1: duration : duration_key INT ;
    public final duration_return duration() throws RecognitionException {
        duration_return retval = new duration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT117=null;
        duration_key_return duration_key116 = null;


        Object INT117_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:548:2: ( duration_key INT )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:548:4: duration_key INT
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_duration_key_in_duration1763);
            duration_key116=duration_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(duration_key116.getTree(), root_0);
            INT117=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1766); if (failed) return retval;
            if ( backtracking==0 ) {
            INT117_tree = (Object)adaptor.create(INT117);
            adaptor.addChild(root_0, INT117_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:551:1: dialect : dialect_key STRING ;
    public final dialect_return dialect() throws RecognitionException {
        dialect_return retval = new dialect_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING119=null;
        dialect_key_return dialect_key118 = null;


        Object STRING119_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:2: ( dialect_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:4: dialect_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_dialect_key_in_dialect1780);
            dialect_key118=dialect_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(dialect_key118.getTree(), root_0);
            STRING119=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1783); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING119_tree = (Object)adaptor.create(STRING119);
            adaptor.addChild(root_0, STRING119_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:1: lock_on_active : lock_on_active_key ( BOOL )? ;
    public final lock_on_active_return lock_on_active() throws RecognitionException {
        lock_on_active_return retval = new lock_on_active_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL121=null;
        lock_on_active_key_return lock_on_active_key120 = null;


        Object BOOL121_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:556:2: ( lock_on_active_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:556:4: lock_on_active_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lock_on_active_key_in_lock_on_active1801);
            lock_on_active_key120=lock_on_active_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(lock_on_active_key120.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:556:24: ( BOOL )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==BOOL) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:556:24: BOOL
                    {
                    BOOL121=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1804); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL121_tree = (Object)adaptor.create(BOOL121);
                    adaptor.addChild(root_0, BOOL121_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:559:1: normal_lhs_block : ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) ;
    public final normal_lhs_block_return normal_lhs_block() throws RecognitionException {
        normal_lhs_block_return retval = new normal_lhs_block_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        lhs_return lhs122 = null;


        RewriteRuleSubtreeStream stream_lhs=new RewriteRuleSubtreeStream(adaptor,"rule lhs");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:560:2: ( ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:560:4: ( lhs )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:560:4: ( lhs )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==ID||LA42_0==LEFT_PAREN) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:560:4: lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1816);
            	    lhs122=lhs();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_lhs.add(lhs122.getTree());

            	    }
            	    break;

            	default :
            	    break loop42;
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
            // 561:2: -> ^( VT_AND_IMPLICIT ( lhs )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:561:5: ^( VT_AND_IMPLICIT ( lhs )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_AND_IMPLICIT, "VT_AND_IMPLICIT"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:561:23: ( lhs )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:1: lhs : lhs_or ;
    public final lhs_return lhs() throws RecognitionException {
        lhs_return retval = new lhs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        lhs_or_return lhs_or123 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:5: ( lhs_or )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:7: lhs_or
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_or_in_lhs1837);
            lhs_or123=lhs_or();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, lhs_or123.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:567:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );
    public final lhs_or_return lhs_or() throws RecognitionException {
        lhs_or_return retval = new lhs_or_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        Token LEFT_PAREN124=null;
        Token RIGHT_PAREN126=null;
        or_key_return or = null;

        or_key_return value = null;

        lhs_and_return lhs_and125 = null;

        lhs_and_return lhs_and127 = null;

        lhs_and_return lhs_and128 = null;


        Object pipe_tree=null;
        Object LEFT_PAREN124_tree=null;
        Object RIGHT_PAREN126_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");
        RewriteRuleSubtreeStream stream_lhs_and=new RewriteRuleSubtreeStream(adaptor,"rule lhs_and");

        	Token orToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:570:3: ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* )
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==LEFT_PAREN) ) {
                int LA46_1 = input.LA(2);

                if ( (LA46_1==ID) ) {
                    switch ( input.LA(3) ) {
                    case DOT:
                    case COLON:
                    case LEFT_SQUARE:
                        {
                        alt46=2;
                        }
                        break;
                    case LEFT_PAREN:
                        {
                        int LA46_4 = input.LA(4);

                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("567:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 46, 4, input);

                            throw nvae;
                        }
                        }
                        break;
                    case ID:
                        {
                        int LA46_5 = input.LA(4);

                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("567:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 46, 5, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("567:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 46, 3, input);

                        throw nvae;
                    }

                }
                else if ( (LA46_1==LEFT_PAREN) ) {
                    alt46=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("567:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 46, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA46_0==ID) ) {
                alt46=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("567:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:570:5: ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN
                    {
                    LEFT_PAREN124=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or1858); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN124);

                    pushFollow(FOLLOW_or_key_in_lhs_or1862);
                    or=or_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_or_key.add(or.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:570:48: ( lhs_and )+
                    int cnt43=0;
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==ID||LA43_0==LEFT_PAREN) ) {
                            alt43=1;
                        }


                        switch (alt43) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:570:48: lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1864);
                    	    lhs_and125=lhs_and();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_and.add(lhs_and125.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt43 >= 1 ) break loop43;
                    	    if (backtracking>0) {failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(43, input);
                                throw eee;
                        }
                        cnt43++;
                    } while (true);

                    RIGHT_PAREN126=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or1867); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN126);


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
                    // 571:3: -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:6: ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:572:4: ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:572:4: ( lhs_and -> lhs_and )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:572:5: lhs_and
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or1888);
                    lhs_and127=lhs_and();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_and.add(lhs_and127.getTree());

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
                    // 572:13: -> lhs_and
                    {
                        adaptor.addChild(root_0, stream_lhs_and.next());

                    }

                    }

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:3: ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    loop45:
                    do {
                        int alt45=2;
                        int LA45_0 = input.LA(1);

                        if ( (LA45_0==ID) ) {
                            int LA45_2 = input.LA(2);

                            if ( ((synpred2()&&(validateIdentifierKey(DroolsSoftKeywords.OR)))) ) {
                                alt45=1;
                            }


                        }
                        else if ( (LA45_0==DOUBLE_PIPE) ) {
                            int LA45_3 = input.LA(2);

                            if ( (synpred2()) ) {
                                alt45=1;
                            }


                        }


                        switch (alt45) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:5: ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and
                    	    {
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:28: (value= or_key | pipe= DOUBLE_PIPE )
                    	    int alt44=2;
                    	    int LA44_0 = input.LA(1);

                    	    if ( (LA44_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
                    	        alt44=1;
                    	    }
                    	    else if ( (LA44_0==DOUBLE_PIPE) ) {
                    	        alt44=2;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("573:28: (value= or_key | pipe= DOUBLE_PIPE )", 44, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt44) {
                    	        case 1 :
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:29: value= or_key
                    	            {
                    	            pushFollow(FOLLOW_or_key_in_lhs_or1910);
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
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:69: pipe= DOUBLE_PIPE
                    	            {
                    	            pipe=(Token)input.LT(1);
                    	            match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_lhs_or1917); if (failed) return retval;
                    	            if ( backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

                    	            if ( backtracking==0 ) {
                    	              orToken = pipe;
                    	            }

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1922);
                    	    lhs_and128=lhs_and();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_and.add(lhs_and128.getTree());

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
                    	    // 574:3: -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
                    	    {
                    	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:574:6: ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
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
                    	    break loop45;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:577:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );
    public final lhs_and_return lhs_and() throws RecognitionException {
        lhs_and_return retval = new lhs_and_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token amper=null;
        Token LEFT_PAREN129=null;
        Token RIGHT_PAREN131=null;
        and_key_return and = null;

        and_key_return value = null;

        lhs_unary_return lhs_unary130 = null;

        lhs_unary_return lhs_unary132 = null;

        lhs_unary_return lhs_unary133 = null;


        Object amper_tree=null;
        Object LEFT_PAREN129_tree=null;
        Object RIGHT_PAREN131_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_AMPER=new RewriteRuleTokenStream(adaptor,"token DOUBLE_AMPER");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_and_key=new RewriteRuleSubtreeStream(adaptor,"rule and_key");
        RewriteRuleSubtreeStream stream_lhs_unary=new RewriteRuleSubtreeStream(adaptor,"rule lhs_unary");

        	Token andToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:580:3: ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* )
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==LEFT_PAREN) ) {
                int LA50_1 = input.LA(2);

                if ( (LA50_1==ID) ) {
                    switch ( input.LA(3) ) {
                    case DOT:
                    case COLON:
                    case LEFT_SQUARE:
                        {
                        alt50=2;
                        }
                        break;
                    case LEFT_PAREN:
                        {
                        switch ( input.LA(4) ) {
                        case ID:
                            {
                            int LA50_6 = input.LA(5);

                            if ( (synpred3()) ) {
                                alt50=1;
                            }
                            else if ( (true) ) {
                                alt50=2;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("577:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 50, 6, input);

                                throw nvae;
                            }
                            }
                            break;
                        case LEFT_PAREN:
                            {
                            int LA50_7 = input.LA(5);

                            if ( (synpred3()) ) {
                                alt50=1;
                            }
                            else if ( (true) ) {
                                alt50=2;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("577:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 50, 7, input);

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
                            alt50=2;
                            }
                            break;
                        default:
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("577:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 50, 4, input);

                            throw nvae;
                        }

                        }
                        break;
                    case ID:
                        {
                        int LA50_5 = input.LA(4);

                        if ( (synpred3()) ) {
                            alt50=1;
                        }
                        else if ( (true) ) {
                            alt50=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("577:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 50, 5, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("577:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 50, 3, input);

                        throw nvae;
                    }

                }
                else if ( (LA50_1==LEFT_PAREN) ) {
                    alt50=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("577:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 50, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA50_0==ID) ) {
                alt50=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("577:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:580:5: ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN
                    {
                    LEFT_PAREN129=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and1960); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN129);

                    pushFollow(FOLLOW_and_key_in_lhs_and1964);
                    and=and_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_and_key.add(and.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:580:51: ( lhs_unary )+
                    int cnt47=0;
                    loop47:
                    do {
                        int alt47=2;
                        int LA47_0 = input.LA(1);

                        if ( (LA47_0==ID||LA47_0==LEFT_PAREN) ) {
                            alt47=1;
                        }


                        switch (alt47) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:580:51: lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and1966);
                    	    lhs_unary130=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary130.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt47 >= 1 ) break loop47;
                    	    if (backtracking>0) {failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(47, input);
                                throw eee;
                        }
                        cnt47++;
                    } while (true);

                    RIGHT_PAREN131=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and1969); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN131);


                    // AST REWRITE
                    // elements: lhs_unary, RIGHT_PAREN
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 581:3: -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:581:6: ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:582:4: ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:582:4: ( lhs_unary -> lhs_unary )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:582:5: lhs_unary
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and1990);
                    lhs_unary132=lhs_unary();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary132.getTree());

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
                    // 582:15: -> lhs_unary
                    {
                        adaptor.addChild(root_0, stream_lhs_unary.next());

                    }

                    }

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:3: ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    loop49:
                    do {
                        int alt49=2;
                        int LA49_0 = input.LA(1);

                        if ( (LA49_0==ID) ) {
                            int LA49_2 = input.LA(2);

                            if ( ((synpred4()&&(validateIdentifierKey(DroolsSoftKeywords.AND)))) ) {
                                alt49=1;
                            }


                        }
                        else if ( (LA49_0==DOUBLE_AMPER) ) {
                            int LA49_3 = input.LA(2);

                            if ( (synpred4()) ) {
                                alt49=1;
                            }


                        }


                        switch (alt49) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:5: ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary
                    	    {
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:30: (value= and_key | amper= DOUBLE_AMPER )
                    	    int alt48=2;
                    	    int LA48_0 = input.LA(1);

                    	    if ( (LA48_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.AND)))) {
                    	        alt48=1;
                    	    }
                    	    else if ( (LA48_0==DOUBLE_AMPER) ) {
                    	        alt48=2;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("583:30: (value= and_key | amper= DOUBLE_AMPER )", 48, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt48) {
                    	        case 1 :
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:31: value= and_key
                    	            {
                    	            pushFollow(FOLLOW_and_key_in_lhs_and2012);
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
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:73: amper= DOUBLE_AMPER
                    	            {
                    	            amper=(Token)input.LT(1);
                    	            match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_lhs_and2019); if (failed) return retval;
                    	            if ( backtracking==0 ) stream_DOUBLE_AMPER.add(amper);

                    	            if ( backtracking==0 ) {
                    	              andToken = amper;
                    	            }

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2024);
                    	    lhs_unary133=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary133.getTree());

                    	    // AST REWRITE
                    	    // elements: lhs_unary, lhs_and
                    	    // token labels: 
                    	    // rule labels: retval
                    	    // token list labels: 
                    	    // rule list labels: 
                    	    if ( backtracking==0 ) {
                    	    retval.tree = root_0;
                    	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    	    root_0 = (Object)adaptor.nil();
                    	    // 584:3: -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
                    	    {
                    	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:584:6: ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
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
                    	    break loop49;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:587:1: lhs_unary options {backtrack=true; } : ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? ;
    public final lhs_unary_return lhs_unary() throws RecognitionException {
        lhs_unary_return retval = new lhs_unary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN139=null;
        Token RIGHT_PAREN141=null;
        Token SEMICOLON143=null;
        lhs_exist_return lhs_exist134 = null;

        lhs_not_binding_return lhs_not_binding135 = null;

        lhs_not_return lhs_not136 = null;

        lhs_eval_return lhs_eval137 = null;

        lhs_forall_return lhs_forall138 = null;

        lhs_or_return lhs_or140 = null;

        pattern_source_return pattern_source142 = null;


        Object LEFT_PAREN139_tree=null;
        Object RIGHT_PAREN141_tree=null;
        Object SEMICOLON143_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:589:2: ( ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:589:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:589:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )
            int alt51=7;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==ID) ) {
                int LA51_1 = input.LA(2);

                if ( ((validateIdentifierKey(DroolsSoftKeywords.EXISTS))) ) {
                    alt51=1;
                }
                else if ( ((validateNotWithBinding()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                    alt51=2;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                    alt51=3;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                    alt51=4;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.FORALL))) ) {
                    alt51=5;
                }
                else if ( (true) ) {
                    alt51=7;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("589:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )", 51, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA51_0==LEFT_PAREN) ) {
                alt51=6;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("589:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:589:6: lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary2062);
                    lhs_exist134=lhs_exist();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_exist134.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:590:4: {...}? => lhs_not_binding
                    {
                    if ( !(validateNotWithBinding()) ) {
                        if (backtracking>0) {failed=true; return retval;}
                        throw new FailedPredicateException(input, "lhs_unary", "validateNotWithBinding()");
                    }
                    pushFollow(FOLLOW_lhs_not_binding_in_lhs_unary2070);
                    lhs_not_binding135=lhs_not_binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_not_binding135.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:591:5: lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary2076);
                    lhs_not136=lhs_not();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_not136.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:592:5: lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2082);
                    lhs_eval137=lhs_eval();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_eval137.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:593:5: lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary2088);
                    lhs_forall138=lhs_forall();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_forall138.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN139=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2094); if (failed) return retval;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary2097);
                    lhs_or140=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_or140.getTree());
                    RIGHT_PAREN141=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2099); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN141_tree = (Object)adaptor.create(RIGHT_PAREN141);
                    adaptor.addChild(root_0, RIGHT_PAREN141_tree);
                    }

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:595:5: pattern_source
                    {
                    pushFollow(FOLLOW_pattern_source_in_lhs_unary2105);
                    pattern_source142=pattern_source();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, pattern_source142.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:597:3: ( ( SEMICOLON )=> SEMICOLON )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==SEMICOLON) ) {
                int LA52_1 = input.LA(2);

                if ( (synpred5()) ) {
                    alt52=1;
                }
            }
            switch (alt52) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:597:4: ( SEMICOLON )=> SEMICOLON
                    {
                    SEMICOLON143=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_lhs_unary2119); if (failed) return retval;

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:600:1: lhs_exist : exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final lhs_exist_return lhs_exist() throws RecognitionException {
        lhs_exist_return retval = new lhs_exist_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN146=null;
        Token RIGHT_PAREN148=null;
        exists_key_return exists_key144 = null;

        lhs_or_return lhs_or145 = null;

        lhs_or_return lhs_or147 = null;

        lhs_pattern_return lhs_pattern149 = null;


        Object LEFT_PAREN146_tree=null;
        Object RIGHT_PAREN148_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_exists_key=new RewriteRuleSubtreeStream(adaptor,"rule exists_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:601:2: ( exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:601:4: exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_exists_key_in_lhs_exist2133);
            exists_key144=exists_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_exists_key.add(exists_key144.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:10: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt53=3;
            alt53 = dfa53.predict(input);
            switch (alt53) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:12: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2157);
                    lhs_or145=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or145.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:603:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN146=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist2164); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN146);

                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2166);
                    lhs_or147=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or147.getTree());
                    RIGHT_PAREN148=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist2168); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN148);


                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:604:12: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist2181);
                    lhs_pattern149=lhs_pattern();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern149.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: lhs_pattern, exists_key, RIGHT_PAREN, lhs_or
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 606:10: -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:606:13: ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_exists_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:606:26: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.next());

                }
                stream_lhs_or.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:606:34: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.next());

                }
                stream_lhs_pattern.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:606:47: ( RIGHT_PAREN )?
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

    public static class lhs_not_binding_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_not_binding
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:609:1: lhs_not_binding : not_key fact_binding -> ^( not_key ^( VT_PATTERN fact_binding ) ) ;
    public final lhs_not_binding_return lhs_not_binding() throws RecognitionException {
        lhs_not_binding_return retval = new lhs_not_binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        not_key_return not_key150 = null;

        fact_binding_return fact_binding151 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:610:2: ( not_key fact_binding -> ^( not_key ^( VT_PATTERN fact_binding ) ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:610:4: not_key fact_binding
            {
            pushFollow(FOLLOW_not_key_in_lhs_not_binding2227);
            not_key150=not_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_not_key.add(not_key150.getTree());
            pushFollow(FOLLOW_fact_binding_in_lhs_not_binding2229);
            fact_binding151=fact_binding();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_fact_binding.add(fact_binding151.getTree());

            // AST REWRITE
            // elements: not_key, fact_binding
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 611:2: -> ^( not_key ^( VT_PATTERN fact_binding ) )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:611:5: ^( not_key ^( VT_PATTERN fact_binding ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:611:15: ^( VT_PATTERN fact_binding )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(adaptor.create(VT_PATTERN, "VT_PATTERN"), root_2);

                adaptor.addChild(root_2, stream_fact_binding.next());

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
    // $ANTLR end lhs_not_binding

    public static class lhs_not_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_not
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:614:1: lhs_not : not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final lhs_not_return lhs_not() throws RecognitionException {
        lhs_not_return retval = new lhs_not_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN154=null;
        Token RIGHT_PAREN156=null;
        not_key_return not_key152 = null;

        lhs_or_return lhs_or153 = null;

        lhs_or_return lhs_or155 = null;

        lhs_pattern_return lhs_pattern157 = null;


        Object LEFT_PAREN154_tree=null;
        Object RIGHT_PAREN156_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:614:9: ( not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:614:11: not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_not_key_in_lhs_not2252);
            not_key152=not_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_not_key.add(not_key152.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:615:3: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt54=3;
            alt54 = dfa54.predict(input);
            switch (alt54) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:615:5: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2269);
                    lhs_or153=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or153.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:616:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN154=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2276); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN154);

                    pushFollow(FOLLOW_lhs_or_in_lhs_not2278);
                    lhs_or155=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or155.getTree());
                    RIGHT_PAREN156=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2280); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN156);


                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:617:6: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2288);
                    lhs_pattern157=lhs_pattern();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern157.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: not_key, RIGHT_PAREN, lhs_pattern, lhs_or
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 618:10: -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:618:13: ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:618:23: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.next());

                }
                stream_lhs_or.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:618:31: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.next());

                }
                stream_lhs_pattern.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:618:44: ( RIGHT_PAREN )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:621:1: lhs_eval : ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) ;
    public final lhs_eval_return lhs_eval() throws RecognitionException {
        lhs_eval_return retval = new lhs_eval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        eval_key_return ev = null;

        paren_chunk_return pc = null;


        RewriteRuleSubtreeStream stream_eval_key=new RewriteRuleSubtreeStream(adaptor,"rule eval_key");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:622:2: (ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:622:4: ev= eval_key pc= paren_chunk
            {
            pushFollow(FOLLOW_eval_key_in_lhs_eval2327);
            ev=eval_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_eval_key.add(ev.getTree());
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2331);
            pc=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc.getTree());
            if ( backtracking==0 ) {
              	String body = safeSubstring( input.toString(pc.start,pc.stop), 1, input.toString(pc.start,pc.stop).length()-1 );
              		checkTrailingSemicolon( body, ((Token)ev.start) );	
            }

            // AST REWRITE
            // elements: eval_key, paren_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 625:3: -> ^( eval_key paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:625:6: ^( eval_key paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:628:1: lhs_forall : forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN ) ;
    public final lhs_forall_return lhs_forall() throws RecognitionException {
        lhs_forall_return retval = new lhs_forall_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN159=null;
        Token RIGHT_PAREN161=null;
        forall_key_return forall_key158 = null;

        lhs_pattern_return lhs_pattern160 = null;


        Object LEFT_PAREN159_tree=null;
        Object RIGHT_PAREN161_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_forall_key=new RewriteRuleSubtreeStream(adaptor,"rule forall_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:629:2: ( forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:629:4: forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN
            {
            pushFollow(FOLLOW_forall_key_in_lhs_forall2355);
            forall_key158=forall_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_forall_key.add(forall_key158.getTree());
            LEFT_PAREN159=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2357); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN159);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:629:26: ( lhs_pattern )+
            int cnt55=0;
            loop55:
            do {
                int alt55=2;
                int LA55_0 = input.LA(1);

                if ( (LA55_0==ID) ) {
                    alt55=1;
                }


                switch (alt55) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:629:26: lhs_pattern
            	    {
            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2359);
            	    lhs_pattern160=lhs_pattern();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern160.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt55 >= 1 ) break loop55;
            	    if (backtracking>0) {failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(55, input);
                        throw eee;
                }
                cnt55++;
            } while (true);

            RIGHT_PAREN161=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall2362); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN161);


            // AST REWRITE
            // elements: RIGHT_PAREN, forall_key, lhs_pattern
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 630:3: -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:630:6: ^( forall_key ( lhs_pattern )+ RIGHT_PAREN )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:633:1: pattern_source options {backtrack=true; } : lhs_pattern ( over_clause )? ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? ;
    public final pattern_source_return pattern_source() throws RecognitionException {
        pattern_source_return retval = new pattern_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        lhs_pattern_return lhs_pattern162 = null;

        over_clause_return over_clause163 = null;

        from_key_return from_key164 = null;

        accumulate_statement_return accumulate_statement165 = null;

        collect_statement_return collect_statement166 = null;

        entrypoint_statement_return entrypoint_statement167 = null;

        from_source_return from_source168 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:2: ( lhs_pattern ( over_clause )? ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:4: lhs_pattern ( over_clause )? ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_pattern_in_pattern_source2394);
            lhs_pattern162=lhs_pattern();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, lhs_pattern162.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:636:3: ( over_clause )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==OVER) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:636:3: over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_pattern_source2398);
                    over_clause163=over_clause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, over_clause163.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:637:3: ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==ID) ) {
                int LA58_1 = input.LA(2);

                if ( (LA58_1==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateNotWithBinding()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateNotWithBinding()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(validateNotWithBinding()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                    int LA58_3 = input.LA(3);

                    if ( (LA58_3==SEMICOLON||LA58_3==END||(LA58_3>=COMMA && LA58_3<=RIGHT_PAREN)||(LA58_3>=DOUBLE_PIPE && LA58_3<=DOUBLE_AMPER)||LA58_3==THEN||LA58_3==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                        alt58=1;
                    }
                    else if ( (LA58_3==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                        int LA58_6 = input.LA(4);

                        if ( (LA58_6==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                            int LA58_10 = input.LA(5);

                            if ( (LA58_10==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                int LA58_11 = input.LA(6);

                                if ( (LA58_11==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                                    int LA58_14 = input.LA(7);

                                    if ( (LA58_14==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                        int LA58_16 = input.LA(8);

                                        if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                                            alt58=1;
                                        }
                                    }
                                    else if ( ((LA58_14>=SEMICOLON && LA58_14<=DOT)||LA58_14==END||(LA58_14>=COMMA && LA58_14<=RIGHT_PAREN)||(LA58_14>=DOUBLE_PIPE && LA58_14<=DOUBLE_AMPER)||LA58_14==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                                        alt58=1;
                                    }
                                }
                                else if ( ((LA58_11>=VT_COMPILATION_UNIT && LA58_11<=LEFT_SQUARE)||(LA58_11>=THEN && LA58_11<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                                    alt58=1;
                                }
                            }
                            else if ( (LA58_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                int LA58_12 = input.LA(6);

                                if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                                    alt58=1;
                                }
                            }
                            else if ( (LA58_10==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                int LA58_13 = input.LA(6);

                                if ( (LA58_13==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                    int LA58_15 = input.LA(7);

                                    if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                                        alt58=1;
                                    }
                                }
                            }
                            else if ( ((LA58_10>=SEMICOLON && LA58_10<=ID)||LA58_10==END||(LA58_10>=COMMA && LA58_10<=RIGHT_PAREN)||(LA58_10>=DOUBLE_PIPE && LA58_10<=DOUBLE_AMPER)||LA58_10==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                                alt58=1;
                            }
                        }
                    }
                    else if ( (LA58_3==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                        int LA58_8 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                            alt58=1;
                        }
                    }
                    else if ( (LA58_3==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                        int LA58_9 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                            alt58=1;
                        }
                    }
                }
            }
            switch (alt58) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:638:4: from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    {
                    pushFollow(FOLLOW_from_key_in_pattern_source2408);
                    from_key164=from_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(from_key164.getTree(), root_0);
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    int alt57=4;
                    int LA57_0 = input.LA(1);

                    if ( (LA57_0==ID) ) {
                        int LA57_1 = input.LA(2);

                        if ( (LA57_1==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT)))) {
                            alt57=3;
                        }
                        else if ( (LA57_1==LEFT_PAREN) ) {
                            switch ( input.LA(3) ) {
                            case ID:
                                {
                                int LA57_5 = input.LA(4);

                                if ( ((validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))) ) {
                                    alt57=1;
                                }
                                else if ( ((validateIdentifierKey(DroolsSoftKeywords.COLLECT))) ) {
                                    alt57=2;
                                }
                                else if ( (true) ) {
                                    alt57=4;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return retval;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("639:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 57, 5, input);

                                    throw nvae;
                                }
                                }
                                break;
                            case LEFT_PAREN:
                                {
                                int LA57_6 = input.LA(4);

                                if ( ((validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))) ) {
                                    alt57=1;
                                }
                                else if ( (true) ) {
                                    alt57=4;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return retval;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("639:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 57, 6, input);

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
                                alt57=4;
                                }
                                break;
                            default:
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("639:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 57, 3, input);

                                throw nvae;
                            }

                        }
                        else if ( ((LA57_1>=SEMICOLON && LA57_1<=DOT)||LA57_1==END||(LA57_1>=COMMA && LA57_1<=RIGHT_PAREN)||(LA57_1>=DOUBLE_PIPE && LA57_1<=DOUBLE_AMPER)||LA57_1==THEN) ) {
                            alt57=4;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("639:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 57, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("639:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 57, 0, input);

                        throw nvae;
                    }
                    switch (alt57) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:14: accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_pattern_source2424);
                            accumulate_statement165=accumulate_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, accumulate_statement165.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:640:15: collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_pattern_source2440);
                            collect_statement166=collect_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, collect_statement166.getTree());

                            }
                            break;
                        case 3 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:641:15: entrypoint_statement
                            {
                            pushFollow(FOLLOW_entrypoint_statement_in_pattern_source2457);
                            entrypoint_statement167=entrypoint_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, entrypoint_statement167.getTree());

                            }
                            break;
                        case 4 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:15: from_source
                            {
                            pushFollow(FOLLOW_from_source_in_pattern_source2473);
                            from_source168=from_source();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, from_source168.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:647:1: over_clause : OVER over_elements ( COMMA over_elements )* ;
    public final over_clause_return over_clause() throws RecognitionException {
        over_clause_return retval = new over_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OVER169=null;
        Token COMMA171=null;
        over_elements_return over_elements170 = null;

        over_elements_return over_elements172 = null;


        Object OVER169_tree=null;
        Object COMMA171_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:648:2: ( OVER over_elements ( COMMA over_elements )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:648:4: OVER over_elements ( COMMA over_elements )*
            {
            root_0 = (Object)adaptor.nil();

            OVER169=(Token)input.LT(1);
            match(input,OVER,FOLLOW_OVER_in_over_clause2501); if (failed) return retval;
            if ( backtracking==0 ) {
            OVER169_tree = (Object)adaptor.create(OVER169);
            root_0 = (Object)adaptor.becomeRoot(OVER169_tree, root_0);
            }
            pushFollow(FOLLOW_over_elements_in_over_clause2504);
            over_elements170=over_elements();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, over_elements170.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:648:24: ( COMMA over_elements )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==COMMA) ) {
                    int LA59_2 = input.LA(2);

                    if ( (LA59_2==ID) ) {
                        int LA59_3 = input.LA(3);

                        if ( (LA59_3==COLON) ) {
                            alt59=1;
                        }


                    }


                }


                switch (alt59) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:648:25: COMMA over_elements
            	    {
            	    COMMA171=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_over_clause2507); if (failed) return retval;
            	    pushFollow(FOLLOW_over_elements_in_over_clause2510);
            	    over_elements172=over_elements();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, over_elements172.getTree());

            	    }
            	    break;

            	default :
            	    break loop59;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:651:1: over_elements : ID COLON ID paren_chunk -> ^( VT_BEHAVIOR ID ID paren_chunk ) ;
    public final over_elements_return over_elements() throws RecognitionException {
        over_elements_return retval = new over_elements_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID173=null;
        Token COLON174=null;
        Token ID175=null;
        paren_chunk_return paren_chunk176 = null;


        Object ID173_tree=null;
        Object COLON174_tree=null;
        Object ID175_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:652:2: ( ID COLON ID paren_chunk -> ^( VT_BEHAVIOR ID ID paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:652:4: ID COLON ID paren_chunk
            {
            ID173=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_over_elements2523); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID173);

            COLON174=(Token)input.LT(1);
            match(input,COLON,FOLLOW_COLON_in_over_elements2525); if (failed) return retval;
            if ( backtracking==0 ) stream_COLON.add(COLON174);

            ID175=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_over_elements2527); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID175);

            pushFollow(FOLLOW_paren_chunk_in_over_elements2529);
            paren_chunk176=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk176.getTree());

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
            // 653:2: -> ^( VT_BEHAVIOR ID ID paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:653:5: ^( VT_BEHAVIOR ID ID paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:656:1: accumulate_statement : accumulate_key LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) ;
    public final accumulate_statement_return accumulate_statement() throws RecognitionException {
        accumulate_statement_return retval = new accumulate_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN178=null;
        Token COMMA180=null;
        Token RIGHT_PAREN183=null;
        accumulate_key_return accumulate_key177 = null;

        lhs_or_return lhs_or179 = null;

        accumulate_init_clause_return accumulate_init_clause181 = null;

        accumulate_id_clause_return accumulate_id_clause182 = null;


        Object LEFT_PAREN178_tree=null;
        Object COMMA180_tree=null;
        Object RIGHT_PAREN183_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_accumulate_init_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_init_clause");
        RewriteRuleSubtreeStream stream_accumulate_id_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_id_clause");
        RewriteRuleSubtreeStream stream_accumulate_key=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_key");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:657:2: ( accumulate_key LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:657:4: accumulate_key LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN
            {
            pushFollow(FOLLOW_accumulate_key_in_accumulate_statement2553);
            accumulate_key177=accumulate_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_accumulate_key.add(accumulate_key177.getTree());
            LEFT_PAREN178=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2557); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN178);

            pushFollow(FOLLOW_lhs_or_in_accumulate_statement2559);
            lhs_or179=lhs_or();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_lhs_or.add(lhs_or179.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:658:21: ( COMMA )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==COMMA) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:658:21: COMMA
                    {
                    COMMA180=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2561); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(COMMA180);


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:659:3: ( accumulate_init_clause | accumulate_id_clause )
            int alt61=2;
            alt61 = dfa61.predict(input);
            switch (alt61) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:659:5: accumulate_init_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_statement2569);
                    accumulate_init_clause181=accumulate_init_clause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accumulate_init_clause.add(accumulate_init_clause181.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:660:5: accumulate_id_clause
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_statement2575);
                    accumulate_id_clause182=accumulate_id_clause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accumulate_id_clause.add(accumulate_id_clause182.getTree());

                    }
                    break;

            }

            RIGHT_PAREN183=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2583); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN183);


            // AST REWRITE
            // elements: accumulate_id_clause, accumulate_init_clause, RIGHT_PAREN, lhs_or, accumulate_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 663:3: -> ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:663:6: ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_accumulate_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_lhs_or.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:663:30: ( accumulate_init_clause )?
                if ( stream_accumulate_init_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_accumulate_init_clause.next());

                }
                stream_accumulate_init_clause.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:663:54: ( accumulate_id_clause )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:666:1: accumulate_init_clause : init_key pc1= paren_chunk ( COMMA )? action_key pc2= paren_chunk ( COMMA )? ( reverse_key pc3= paren_chunk ( COMMA )? )? result_key pc4= paren_chunk -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) ;
    public final accumulate_init_clause_return accumulate_init_clause() throws RecognitionException {
        accumulate_init_clause_return retval = new accumulate_init_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA185=null;
        Token COMMA187=null;
        Token COMMA189=null;
        paren_chunk_return pc1 = null;

        paren_chunk_return pc2 = null;

        paren_chunk_return pc3 = null;

        paren_chunk_return pc4 = null;

        init_key_return init_key184 = null;

        action_key_return action_key186 = null;

        reverse_key_return reverse_key188 = null;

        result_key_return result_key190 = null;


        Object COMMA185_tree=null;
        Object COMMA187_tree=null;
        Object COMMA189_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_reverse_key=new RewriteRuleSubtreeStream(adaptor,"rule reverse_key");
        RewriteRuleSubtreeStream stream_result_key=new RewriteRuleSubtreeStream(adaptor,"rule result_key");
        RewriteRuleSubtreeStream stream_init_key=new RewriteRuleSubtreeStream(adaptor,"rule init_key");
        RewriteRuleSubtreeStream stream_action_key=new RewriteRuleSubtreeStream(adaptor,"rule action_key");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:667:2: ( init_key pc1= paren_chunk ( COMMA )? action_key pc2= paren_chunk ( COMMA )? ( reverse_key pc3= paren_chunk ( COMMA )? )? result_key pc4= paren_chunk -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:667:4: init_key pc1= paren_chunk ( COMMA )? action_key pc2= paren_chunk ( COMMA )? ( reverse_key pc3= paren_chunk ( COMMA )? )? result_key pc4= paren_chunk
            {
            pushFollow(FOLLOW_init_key_in_accumulate_init_clause2612);
            init_key184=init_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_init_key.add(init_key184.getTree());
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2617);
            pc1=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc1.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:668:18: ( COMMA )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==COMMA) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:668:18: COMMA
                    {
                    COMMA185=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2619); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(COMMA185);


                    }
                    break;

            }

            pushFollow(FOLLOW_action_key_in_accumulate_init_clause2623);
            action_key186=action_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action_key.add(action_key186.getTree());
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2627);
            pc2=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc2.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:669:29: ( COMMA )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==COMMA) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:669:29: COMMA
                    {
                    COMMA187=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2629); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(COMMA187);


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:670:2: ( reverse_key pc3= paren_chunk ( COMMA )? )?
            int alt65=2;
            alt65 = dfa65.predict(input);
            switch (alt65) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:670:4: reverse_key pc3= paren_chunk ( COMMA )?
                    {
                    pushFollow(FOLLOW_reverse_key_in_accumulate_init_clause2635);
                    reverse_key188=reverse_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_reverse_key.add(reverse_key188.getTree());
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2639);
                    pc3=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(pc3.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:670:32: ( COMMA )?
                    int alt64=2;
                    int LA64_0 = input.LA(1);

                    if ( (LA64_0==COMMA) ) {
                        alt64=1;
                    }
                    switch (alt64) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:670:32: COMMA
                            {
                            COMMA189=(Token)input.LT(1);
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2641); if (failed) return retval;
                            if ( backtracking==0 ) stream_COMMA.add(COMMA189);


                            }
                            break;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_result_key_in_accumulate_init_clause2647);
            result_key190=result_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_result_key.add(result_key190.getTree());
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2651);
            pc4=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc4.getTree());

            // AST REWRITE
            // elements: pc2, result_key, pc3, action_key, pc4, init_key, pc1, reverse_key
            // token labels: 
            // rule labels: pc2, pc4, pc3, pc1, retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_pc2=new RewriteRuleSubtreeStream(adaptor,"token pc2",pc2!=null?pc2.tree:null);
            RewriteRuleSubtreeStream stream_pc4=new RewriteRuleSubtreeStream(adaptor,"token pc4",pc4!=null?pc4.tree:null);
            RewriteRuleSubtreeStream stream_pc3=new RewriteRuleSubtreeStream(adaptor,"token pc3",pc3!=null?pc3.tree:null);
            RewriteRuleSubtreeStream stream_pc1=new RewriteRuleSubtreeStream(adaptor,"token pc1",pc1!=null?pc1.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 672:2: -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:672:5: ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ACCUMULATE_INIT_CLAUSE, "VT_ACCUMULATE_INIT_CLAUSE"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:672:33: ^( init_key $pc1)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_init_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc1.next());

                adaptor.addChild(root_1, root_2);
                }
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:672:50: ^( action_key $pc2)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_action_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc2.next());

                adaptor.addChild(root_1, root_2);
                }
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:672:69: ( ^( reverse_key $pc3) )?
                if ( stream_pc3.hasNext()||stream_reverse_key.hasNext() ) {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:672:69: ^( reverse_key $pc3)
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_reverse_key.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_pc3.next());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_pc3.reset();
                stream_reverse_key.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:672:90: ^( result_key $pc4)
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:675:1: accumulate_id_clause : id= ID text= paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:676:2: (id= ID text= paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:676:4: id= ID text= paren_chunk
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accumulate_id_clause2700); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            pushFollow(FOLLOW_paren_chunk_in_accumulate_id_clause2704);
            text=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(text.getTree());

            // AST REWRITE
            // elements: paren_chunk, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 677:2: -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:677:5: ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:680:1: collect_statement : collect_key LEFT_PAREN pattern_source RIGHT_PAREN -> ^( collect_key pattern_source RIGHT_PAREN ) ;
    public final collect_statement_return collect_statement() throws RecognitionException {
        collect_statement_return retval = new collect_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN192=null;
        Token RIGHT_PAREN194=null;
        collect_key_return collect_key191 = null;

        pattern_source_return pattern_source193 = null;


        Object LEFT_PAREN192_tree=null;
        Object RIGHT_PAREN194_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_collect_key=new RewriteRuleSubtreeStream(adaptor,"rule collect_key");
        RewriteRuleSubtreeStream stream_pattern_source=new RewriteRuleSubtreeStream(adaptor,"rule pattern_source");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:681:2: ( collect_key LEFT_PAREN pattern_source RIGHT_PAREN -> ^( collect_key pattern_source RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:681:4: collect_key LEFT_PAREN pattern_source RIGHT_PAREN
            {
            pushFollow(FOLLOW_collect_key_in_collect_statement2726);
            collect_key191=collect_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_collect_key.add(collect_key191.getTree());
            LEFT_PAREN192=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement2730); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN192);

            pushFollow(FOLLOW_pattern_source_in_collect_statement2732);
            pattern_source193=pattern_source();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_pattern_source.add(pattern_source193.getTree());
            RIGHT_PAREN194=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement2734); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN194);


            // AST REWRITE
            // elements: collect_key, pattern_source, RIGHT_PAREN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 683:2: -> ^( collect_key pattern_source RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:683:5: ^( collect_key pattern_source RIGHT_PAREN )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:686:1: entrypoint_statement : entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) ;
    public final entrypoint_statement_return entrypoint_statement() throws RecognitionException {
        entrypoint_statement_return retval = new entrypoint_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        entry_point_key_return entry_point_key195 = null;

        entrypoint_id_return entrypoint_id196 = null;


        RewriteRuleSubtreeStream stream_entrypoint_id=new RewriteRuleSubtreeStream(adaptor,"rule entrypoint_id");
        RewriteRuleSubtreeStream stream_entry_point_key=new RewriteRuleSubtreeStream(adaptor,"rule entry_point_key");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:687:2: ( entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:687:4: entry_point_key entrypoint_id
            {
            pushFollow(FOLLOW_entry_point_key_in_entrypoint_statement2756);
            entry_point_key195=entry_point_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_entry_point_key.add(entry_point_key195.getTree());
            pushFollow(FOLLOW_entrypoint_id_in_entrypoint_statement2758);
            entrypoint_id196=entrypoint_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_entrypoint_id.add(entrypoint_id196.getTree());

            // AST REWRITE
            // elements: entrypoint_id, entry_point_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 688:2: -> ^( entry_point_key entrypoint_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:688:5: ^( entry_point_key entrypoint_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:1: entrypoint_id : (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] );
    public final entrypoint_id_return entrypoint_id() throws RecognitionException {
        entrypoint_id_return retval = new entrypoint_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:692:2: (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] )
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==ID) ) {
                alt66=1;
            }
            else if ( (LA66_0==STRING) ) {
                alt66=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("691:1: entrypoint_id : (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] );", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:692:5: value= ID
                    {
                    value=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_entrypoint_id2781); if (failed) return retval;
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
                    // 692:14: -> VT_ENTRYPOINT_ID[$value]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_ENTRYPOINT_ID, value));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:693:5: value= STRING
                    {
                    value=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_entrypoint_id2794); if (failed) return retval;
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
                    // 693:18: -> VT_ENTRYPOINT_ID[$value]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:696:1: from_source : ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) ;
    public final from_source_return from_source() throws RecognitionException {
        from_source_return retval = new from_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID197=null;
        paren_chunk_return args = null;

        expression_chain_return expression_chain198 = null;


        Object ID197_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:2: ( ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:4: ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )?
            {
            ID197=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_from_source2810); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID197);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:3: ( ( LEFT_PAREN )=>args= paren_chunk )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==LEFT_PAREN) ) {
                int LA67_1 = input.LA(2);

                if ( (LA67_1==LEFT_PAREN) ) {
                    int LA67_3 = input.LA(3);

                    if ( (synpred8()) ) {
                        alt67=1;
                    }
                }
                else if ( (LA67_1==ID) ) {
                    int LA67_4 = input.LA(3);

                    if ( (synpred8()) ) {
                        alt67=1;
                    }
                }
                else if ( ((LA67_1>=VT_COMPILATION_UNIT && LA67_1<=SEMICOLON)||(LA67_1>=DOT && LA67_1<=STRING)||LA67_1==COMMA||(LA67_1>=AT && LA67_1<=MULTI_LINE_COMMENT)) && (synpred8())) {
                    alt67=1;
                }
                else if ( (LA67_1==RIGHT_PAREN) && (synpred8())) {
                    alt67=1;
                }
            }
            switch (alt67) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:5: ( LEFT_PAREN )=>args= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source2823);
                    args=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(args.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:699:3: ( expression_chain )?
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==DOT) ) {
                alt68=1;
            }
            switch (alt68) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:699:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source2830);
                    expression_chain198=expression_chain();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expression_chain.add(expression_chain198.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: paren_chunk, ID, expression_chain
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 700:2: -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:700:5: ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FROM_SOURCE, "VT_FROM_SOURCE"), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:700:25: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.next());

                }
                stream_paren_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:700:38: ( expression_chain )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:703:1: expression_chain : startToken= DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) ;
    public final expression_chain_return expression_chain() throws RecognitionException {
        expression_chain_return retval = new expression_chain_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token startToken=null;
        Token ID199=null;
        square_chunk_return square_chunk200 = null;

        paren_chunk_return paren_chunk201 = null;

        expression_chain_return expression_chain202 = null;


        Object startToken_tree=null;
        Object ID199_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:704:2: (startToken= DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:705:3: startToken= DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )?
            {
            startToken=(Token)input.LT(1);
            match(input,DOT,FOLLOW_DOT_in_expression_chain2862); if (failed) return retval;
            if ( backtracking==0 ) stream_DOT.add(startToken);

            ID199=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_expression_chain2864); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID199);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:706:4: ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )?
            int alt69=3;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==LEFT_SQUARE) && (synpred9())) {
                alt69=1;
            }
            else if ( (LA69_0==LEFT_PAREN) ) {
                int LA69_2 = input.LA(2);

                if ( (LA69_2==LEFT_PAREN) ) {
                    int LA69_4 = input.LA(3);

                    if ( (synpred10()) ) {
                        alt69=2;
                    }
                }
                else if ( (LA69_2==ID) ) {
                    int LA69_5 = input.LA(3);

                    if ( (synpred10()) ) {
                        alt69=2;
                    }
                }
                else if ( ((LA69_2>=VT_COMPILATION_UNIT && LA69_2<=SEMICOLON)||(LA69_2>=DOT && LA69_2<=STRING)||LA69_2==COMMA||(LA69_2>=AT && LA69_2<=MULTI_LINE_COMMENT)) && (synpred10())) {
                    alt69=2;
                }
                else if ( (LA69_2==RIGHT_PAREN) && (synpred10())) {
                    alt69=2;
                }
            }
            switch (alt69) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:6: ( LEFT_SQUARE )=> square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain2884);
                    square_chunk200=square_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_square_chunk.add(square_chunk200.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:709:6: ( LEFT_PAREN )=> paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain2906);
                    paren_chunk201=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk201.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:711:4: ( expression_chain )?
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==DOT) ) {
                alt70=1;
            }
            switch (alt70) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:711:4: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain2917);
                    expression_chain202=expression_chain();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expression_chain.add(expression_chain202.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: ID, expression_chain, paren_chunk, square_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 712:4: -> ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:7: ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_EXPRESSION_CHAIN, startToken), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:45: ( square_chunk )?
                if ( stream_square_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_square_chunk.next());

                }
                stream_square_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:59: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.next());

                }
                stream_paren_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:72: ( expression_chain )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:715:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );
    public final lhs_pattern_return lhs_pattern() throws RecognitionException {
        lhs_pattern_return retval = new lhs_pattern_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        fact_binding_return fact_binding203 = null;

        fact_return fact204 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:716:2: ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) )
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==ID) ) {
                int LA71_1 = input.LA(2);

                if ( (LA71_1==COLON) ) {
                    alt71=1;
                }
                else if ( (LA71_1==DOT||LA71_1==LEFT_PAREN||LA71_1==LEFT_SQUARE) ) {
                    alt71=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("715:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );", 71, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("715:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );", 71, 0, input);

                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:716:4: fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern2950);
                    fact_binding203=fact_binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact_binding.add(fact_binding203.getTree());

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
                    // 716:17: -> ^( VT_PATTERN fact_binding )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:716:20: ^( VT_PATTERN fact_binding )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:717:4: fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern2963);
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
                    // 717:9: -> ^( VT_PATTERN fact )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:717:12: ^( VT_PATTERN fact )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:720:1: fact_binding : label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) ;
    public final fact_binding_return fact_binding() throws RecognitionException {
        fact_binding_return retval = new fact_binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN207=null;
        Token RIGHT_PAREN209=null;
        label_return label205 = null;

        fact_return fact206 = null;

        fact_binding_expression_return fact_binding_expression208 = null;


        Object LEFT_PAREN207_tree=null;
        Object RIGHT_PAREN209_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        RewriteRuleSubtreeStream stream_fact_binding_expression=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding_expression");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:721:3: ( label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:721:5: label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            {
            pushFollow(FOLLOW_label_in_fact_binding2983);
            label205=label();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_label.add(label205.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:722:3: ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==ID) ) {
                alt72=1;
            }
            else if ( (LA72_0==LEFT_PAREN) ) {
                alt72=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("722:3: ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:722:5: fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding2989);
                    fact206=fact();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact.add(fact206.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:723:6: LEFT_PAREN fact_binding_expression RIGHT_PAREN
                    {
                    LEFT_PAREN207=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding2996); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN207);

                    pushFollow(FOLLOW_fact_binding_expression_in_fact_binding2998);
                    fact_binding_expression208=fact_binding_expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact_binding_expression.add(fact_binding_expression208.getTree());
                    RIGHT_PAREN209=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding3000); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN209);


                    }
                    break;

            }


            // AST REWRITE
            // elements: RIGHT_PAREN, fact_binding_expression, label, fact
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 725:3: -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:725:6: ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FACT_BINDING, "VT_FACT_BINDING"), root_1);

                adaptor.addChild(root_1, stream_label.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:725:30: ( fact )?
                if ( stream_fact.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact.next());

                }
                stream_fact.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:725:36: ( fact_binding_expression )?
                if ( stream_fact_binding_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact_binding_expression.next());

                }
                stream_fact_binding_expression.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:725:61: ( RIGHT_PAREN )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:728:1: fact_binding_expression : ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* ;
    public final fact_binding_expression_return fact_binding_expression() throws RecognitionException {
        fact_binding_expression_return retval = new fact_binding_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        or_key_return value = null;

        fact_return fact210 = null;

        fact_return fact211 = null;


        Object pipe_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");

        	Token orToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:3: ( ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:5: ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:5: ( fact -> fact )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:6: fact
            {
            pushFollow(FOLLOW_fact_in_fact_binding_expression3039);
            fact210=fact();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_fact.add(fact210.getTree());

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
            // 731:11: -> fact
            {
                adaptor.addChild(root_0, stream_fact.next());

            }

            }

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:20: ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
                    alt74=1;
                }
                else if ( (LA74_0==DOUBLE_PIPE) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:22: (value= or_key | pipe= DOUBLE_PIPE ) fact
            	    {
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:22: (value= or_key | pipe= DOUBLE_PIPE )
            	    int alt73=2;
            	    int LA73_0 = input.LA(1);

            	    if ( (LA73_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
            	        alt73=1;
            	    }
            	    else if ( (LA73_0==DOUBLE_PIPE) ) {
            	        alt73=2;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("731:22: (value= or_key | pipe= DOUBLE_PIPE )", 73, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt73) {
            	        case 1 :
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:23: value= or_key
            	            {
            	            pushFollow(FOLLOW_or_key_in_fact_binding_expression3051);
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
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:62: pipe= DOUBLE_PIPE
            	            {
            	            pipe=(Token)input.LT(1);
            	            match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3057); if (failed) return retval;
            	            if ( backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

            	            if ( backtracking==0 ) {
            	              orToken = pipe;
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_fact_in_fact_binding_expression3062);
            	    fact211=fact();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_fact.add(fact211.getTree());

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
            	    // 732:3: -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
            	    {
            	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:732:6: ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
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
    // $ANTLR end fact_binding_expression

    public static class fact_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start fact
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:1: fact : pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) ;
    public final fact_return fact() throws RecognitionException {
        fact_return retval = new fact_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN213=null;
        Token RIGHT_PAREN215=null;
        pattern_type_return pattern_type212 = null;

        constraints_return constraints214 = null;


        Object LEFT_PAREN213_tree=null;
        Object RIGHT_PAREN215_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_pattern_type=new RewriteRuleSubtreeStream(adaptor,"rule pattern_type");
        RewriteRuleSubtreeStream stream_constraints=new RewriteRuleSubtreeStream(adaptor,"rule constraints");
         pushParaphrases(DroolsParaphareseTypes.PATTERN); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:738:2: ( pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:738:4: pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN
            {
            pushFollow(FOLLOW_pattern_type_in_fact3102);
            pattern_type212=pattern_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_pattern_type.add(pattern_type212.getTree());
            LEFT_PAREN213=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact3104); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN213);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:738:28: ( constraints )?
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==ID||LA75_0==LEFT_PAREN) ) {
                alt75=1;
            }
            switch (alt75) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:738:28: constraints
                    {
                    pushFollow(FOLLOW_constraints_in_fact3106);
                    constraints214=constraints();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_constraints.add(constraints214.getTree());

                    }
                    break;

            }

            RIGHT_PAREN215=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact3109); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN215);


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
            // 739:2: -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:739:5: ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FACT, "VT_FACT"), root_1);

                adaptor.addChild(root_1, stream_pattern_type.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:739:28: ( constraints )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:742:1: constraints : constraint ( COMMA constraint )* ;
    public final constraints_return constraints() throws RecognitionException {
        constraints_return retval = new constraints_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA217=null;
        constraint_return constraint216 = null;

        constraint_return constraint218 = null;


        Object COMMA217_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:2: ( constraint ( COMMA constraint )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:4: constraint ( COMMA constraint )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_in_constraints3134);
            constraint216=constraint();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, constraint216.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:15: ( COMMA constraint )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==COMMA) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:17: COMMA constraint
            	    {
            	    COMMA217=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_constraints3138); if (failed) return retval;
            	    pushFollow(FOLLOW_constraint_in_constraints3141);
            	    constraint218=constraint();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, constraint218.getTree());

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
    // $ANTLR end constraints

    public static class constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start constraint
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:746:1: constraint : or_constr ;
    public final constraint_return constraint() throws RecognitionException {
        constraint_return retval = new constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        or_constr_return or_constr219 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:747:2: ( or_constr )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:747:4: or_constr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_or_constr_in_constraint3155);
            or_constr219=or_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, or_constr219.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:750:1: or_constr : and_constr ( DOUBLE_PIPE and_constr )* ;
    public final or_constr_return or_constr() throws RecognitionException {
        or_constr_return retval = new or_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE221=null;
        and_constr_return and_constr220 = null;

        and_constr_return and_constr222 = null;


        Object DOUBLE_PIPE221_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:751:2: ( and_constr ( DOUBLE_PIPE and_constr )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:751:4: and_constr ( DOUBLE_PIPE and_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_constr_in_or_constr3166);
            and_constr220=and_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, and_constr220.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:751:15: ( DOUBLE_PIPE and_constr )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==DOUBLE_PIPE) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:751:17: DOUBLE_PIPE and_constr
            	    {
            	    DOUBLE_PIPE221=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr3170); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_PIPE221_tree = (Object)adaptor.create(DOUBLE_PIPE221);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE221_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr3173);
            	    and_constr222=and_constr();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, and_constr222.getTree());

            	    }
            	    break;

            	default :
            	    break loop77;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:754:1: and_constr : unary_constr ( DOUBLE_AMPER unary_constr )* ;
    public final and_constr_return and_constr() throws RecognitionException {
        and_constr_return retval = new and_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER224=null;
        unary_constr_return unary_constr223 = null;

        unary_constr_return unary_constr225 = null;


        Object DOUBLE_AMPER224_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:755:2: ( unary_constr ( DOUBLE_AMPER unary_constr )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:755:4: unary_constr ( DOUBLE_AMPER unary_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_constr_in_and_constr3188);
            unary_constr223=unary_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, unary_constr223.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:755:17: ( DOUBLE_AMPER unary_constr )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==DOUBLE_AMPER) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:755:19: DOUBLE_AMPER unary_constr
            	    {
            	    DOUBLE_AMPER224=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr3192); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_AMPER224_tree = (Object)adaptor.create(DOUBLE_AMPER224);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER224_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr3195);
            	    unary_constr225=unary_constr();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, unary_constr225.getTree());

            	    }
            	    break;

            	default :
            	    break loop78;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );
    public final unary_constr_return unary_constr() throws RecognitionException {
        unary_constr_return retval = new unary_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN229=null;
        Token RIGHT_PAREN231=null;
        eval_key_return eval_key226 = null;

        paren_chunk_return paren_chunk227 = null;

        field_constraint_return field_constraint228 = null;

        or_constr_return or_constr230 = null;


        Object LEFT_PAREN229_tree=null;
        Object RIGHT_PAREN231_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:760:2: ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN )
            int alt79=3;
            int LA79_0 = input.LA(1);

            if ( (LA79_0==ID) ) {
                int LA79_1 = input.LA(2);

                if ( ((LA79_1>=ID && LA79_1<=DOT)||LA79_1==COLON||(LA79_1>=EQUAL && LA79_1<=GRAVE_ACCENT)||LA79_1==LEFT_SQUARE) ) {
                    alt79=2;
                }
                else if ( (LA79_1==LEFT_PAREN) ) {
                    int LA79_4 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                        alt79=1;
                    }
                    else if ( (true) ) {
                        alt79=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("758:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 79, 4, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("758:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 79, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA79_0==LEFT_PAREN) ) {
                alt79=3;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("758:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 79, 0, input);

                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:760:4: eval_key paren_chunk
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_eval_key_in_unary_constr3216);
                    eval_key226=eval_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(eval_key226.getTree(), root_0);
                    pushFollow(FOLLOW_paren_chunk_in_unary_constr3219);
                    paren_chunk227=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk227.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:761:4: field_constraint
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_field_constraint_in_unary_constr3224);
                    field_constraint228=field_constraint();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, field_constraint228.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:762:4: LEFT_PAREN or_constr RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN229=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr3229); if (failed) return retval;
                    pushFollow(FOLLOW_or_constr_in_unary_constr3232);
                    or_constr230=or_constr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, or_constr230.getTree());
                    RIGHT_PAREN231=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr3234); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN231_tree = (Object)adaptor.create(RIGHT_PAREN231);
                    adaptor.addChild(root_0, RIGHT_PAREN231_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:765:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );
    public final field_constraint_return field_constraint() throws RecognitionException {
        field_constraint_return retval = new field_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token arw=null;
        label_return label232 = null;

        accessor_path_return accessor_path233 = null;

        or_restr_connective_return or_restr_connective234 = null;

        paren_chunk_return paren_chunk235 = null;

        accessor_path_return accessor_path236 = null;

        or_restr_connective_return or_restr_connective237 = null;


        Object arw_tree=null;
        RewriteRuleTokenStream stream_ARROW=new RewriteRuleTokenStream(adaptor,"token ARROW");
        RewriteRuleSubtreeStream stream_accessor_path=new RewriteRuleSubtreeStream(adaptor,"rule accessor_path");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        RewriteRuleSubtreeStream stream_or_restr_connective=new RewriteRuleSubtreeStream(adaptor,"rule or_restr_connective");

        	boolean isArrow = false;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:768:3: ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) )
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( (LA81_0==ID) ) {
                int LA81_1 = input.LA(2);

                if ( (LA81_1==COLON) ) {
                    alt81=1;
                }
                else if ( ((LA81_1>=ID && LA81_1<=DOT)||LA81_1==LEFT_PAREN||(LA81_1>=EQUAL && LA81_1<=GRAVE_ACCENT)||LA81_1==LEFT_SQUARE) ) {
                    alt81=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("765:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );", 81, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("765:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );", 81, 0, input);

                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:768:5: label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )?
                    {
                    pushFollow(FOLLOW_label_in_field_constraint3248);
                    label232=label();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_label.add(label232.getTree());
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3250);
                    accessor_path233=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accessor_path.add(accessor_path233.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:768:25: ( or_restr_connective | arw= ARROW paren_chunk )?
                    int alt80=3;
                    int LA80_0 = input.LA(1);

                    if ( (LA80_0==ID||LA80_0==LEFT_PAREN||(LA80_0>=EQUAL && LA80_0<=GRAVE_ACCENT)) ) {
                        alt80=1;
                    }
                    else if ( (LA80_0==ARROW) ) {
                        alt80=2;
                    }
                    switch (alt80) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:768:27: or_restr_connective
                            {
                            pushFollow(FOLLOW_or_restr_connective_in_field_constraint3254);
                            or_restr_connective234=or_restr_connective();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_or_restr_connective.add(or_restr_connective234.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:768:49: arw= ARROW paren_chunk
                            {
                            arw=(Token)input.LT(1);
                            match(input,ARROW,FOLLOW_ARROW_in_field_constraint3260); if (failed) return retval;
                            if ( backtracking==0 ) stream_ARROW.add(arw);

                            pushFollow(FOLLOW_paren_chunk_in_field_constraint3262);
                            paren_chunk235=paren_chunk();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk235.getTree());
                            if ( backtracking==0 ) {
                              isArrow = true;
                            }

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: label, accessor_path, or_restr_connective, paren_chunk, label, accessor_path
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 769:3: -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )?
                    if (isArrow) {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:769:17: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.next());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:769:39: ^( VT_FIELD accessor_path )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.next());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:769:66: ( ^( VK_EVAL[$arw] paren_chunk ) )?
                        if ( stream_paren_chunk.hasNext() ) {
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:769:66: ^( VK_EVAL[$arw] paren_chunk )
                            {
                            Object root_1 = (Object)adaptor.nil();
                            root_1 = (Object)adaptor.becomeRoot(adaptor.create(VK_EVAL, arw), root_1);

                            adaptor.addChild(root_1, stream_paren_chunk.next());

                            adaptor.addChild(root_0, root_1);
                            }

                        }
                        stream_paren_chunk.reset();

                    }
                    else // 770:3: -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:770:6: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.next());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:770:28: ^( VT_FIELD accessor_path ( or_restr_connective )? )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.next());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:770:53: ( or_restr_connective )?
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:771:4: accessor_path or_restr_connective
                    {
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3316);
                    accessor_path236=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accessor_path.add(accessor_path236.getTree());
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint3318);
                    or_restr_connective237=or_restr_connective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_or_restr_connective.add(or_restr_connective237.getTree());

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
                    // 772:3: -> ^( VT_FIELD accessor_path or_restr_connective )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:6: ^( VT_FIELD accessor_path or_restr_connective )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:775:1: label : value= ID COLON -> VT_LABEL[$value] ;
    public final label_return label() throws RecognitionException {
        label_return retval = new label_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;
        Token COLON238=null;

        Object value_tree=null;
        Object COLON238_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:775:7: (value= ID COLON -> VT_LABEL[$value] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:775:9: value= ID COLON
            {
            value=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_label3342); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(value);

            COLON238=(Token)input.LT(1);
            match(input,COLON,FOLLOW_COLON_in_label3344); if (failed) return retval;
            if ( backtracking==0 ) stream_COLON.add(COLON238);


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
            // 775:24: -> VT_LABEL[$value]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:778:1: or_restr_connective : and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* ;
    public final or_restr_connective_return or_restr_connective() throws RecognitionException {
        or_restr_connective_return retval = new or_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE240=null;
        and_restr_connective_return and_restr_connective239 = null;

        and_restr_connective_return and_restr_connective241 = null;


        Object DOUBLE_PIPE240_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:779:2: ( and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:779:4: and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3360);
            and_restr_connective239=and_restr_connective();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, and_restr_connective239.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:779:25: ({...}? => DOUBLE_PIPE and_restr_connective )*
            loop82:
            do {
                int alt82=2;
                alt82 = dfa82.predict(input);
                switch (alt82) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:779:26: {...}? => DOUBLE_PIPE and_restr_connective
            	    {
            	    if ( !((validateRestr())) ) {
            	        if (backtracking>0) {failed=true; return retval;}
            	        throw new FailedPredicateException(input, "or_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_PIPE240=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective3366); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_PIPE240_tree = (Object)adaptor.create(DOUBLE_PIPE240);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE240_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3369);
            	    and_restr_connective241=and_restr_connective();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, and_restr_connective241.getTree());

            	    }
            	    break;

            	default :
            	    break loop82;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:782:1: and_restr_connective : constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* ;
    public final and_restr_connective_return and_restr_connective() throws RecognitionException {
        and_restr_connective_return retval = new and_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER243=null;
        constraint_expression_return constraint_expression242 = null;

        constraint_expression_return constraint_expression244 = null;


        Object DOUBLE_AMPER243_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:2: ( constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:4: constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3384);
            constraint_expression242=constraint_expression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, constraint_expression242.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:26: ({...}? => DOUBLE_AMPER constraint_expression )*
            loop83:
            do {
                int alt83=2;
                alt83 = dfa83.predict(input);
                switch (alt83) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:27: {...}? => DOUBLE_AMPER constraint_expression
            	    {
            	    if ( !((validateRestr())) ) {
            	        if (backtracking>0) {failed=true; return retval;}
            	        throw new FailedPredicateException(input, "and_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_AMPER243=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective3390); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_AMPER243_tree = (Object)adaptor.create(DOUBLE_AMPER243);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER243_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3393);
            	    constraint_expression244=constraint_expression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, constraint_expression244.getTree());

            	    }
            	    break;

            	default :
            	    break loop83;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:786:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );
    public final constraint_expression_return constraint_expression() throws RecognitionException {
        constraint_expression_return retval = new constraint_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN247=null;
        Token RIGHT_PAREN249=null;
        compound_operator_return compound_operator245 = null;

        simple_operator_return simple_operator246 = null;

        or_restr_connective_return or_restr_connective248 = null;


        Object LEFT_PAREN247_tree=null;
        Object RIGHT_PAREN249_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:3: ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN )
            int alt84=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA84_1 = input.LA(2);

                if ( (LA84_1==ID) ) {
                    int LA84_10 = input.LA(3);

                    if ( (LA84_10==ID||LA84_10==STRING||(LA84_10>=BOOL && LA84_10<=INT)||(LA84_10>=FLOAT && LA84_10<=NULL)) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                        alt84=2;
                    }
                    else if ( (LA84_10==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                        int LA84_16 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                            alt84=1;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                            alt84=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("786:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 84, 16, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA84_10==DOT||(LA84_10>=COMMA && LA84_10<=RIGHT_PAREN)||(LA84_10>=DOUBLE_PIPE && LA84_10<=DOUBLE_AMPER)||LA84_10==LEFT_SQUARE) ) {
                        alt84=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("786:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 84, 10, input);

                        throw nvae;
                    }
                }
                else if ( (LA84_1==GRAVE_ACCENT) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                    alt84=2;
                }
                else if ( (LA84_1==STRING||(LA84_1>=BOOL && LA84_1<=INT)||(LA84_1>=FLOAT && LA84_1<=NULL)) ) {
                    alt84=2;
                }
                else if ( (LA84_1==LEFT_PAREN) ) {
                    switch ( input.LA(3) ) {
                    case ID:
                        {
                        int LA84_23 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt84=1;
                        }
                        else if ( (true) ) {
                            alt84=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("786:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 84, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_PAREN:
                        {
                        int LA84_24 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt84=1;
                        }
                        else if ( (true) ) {
                            alt84=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("786:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 84, 24, input);

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
                        alt84=2;
                        }
                        break;
                    case STRING:
                    case BOOL:
                    case INT:
                    case FLOAT:
                    case NULL:
                        {
                        int LA84_26 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt84=1;
                        }
                        else if ( (true) ) {
                            alt84=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("786:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 84, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("786:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 84, 13, input);

                        throw nvae;
                    }

                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("786:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 84, 1, input);

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
                alt84=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt84=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("786:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:5: compound_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_compound_operator_in_constraint_expression3415);
                    compound_operator245=compound_operator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, compound_operator245.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:790:4: simple_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_simple_operator_in_constraint_expression3420);
                    simple_operator246=simple_operator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, simple_operator246.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:791:4: LEFT_PAREN or_restr_connective RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN247=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression3425); if (failed) return retval;
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression3428);
                    or_restr_connective248=or_restr_connective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, or_restr_connective248.getTree());
                    RIGHT_PAREN249=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression3430); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN249_tree = (Object)adaptor.create(RIGHT_PAREN249);
                    adaptor.addChild(root_0, RIGHT_PAREN249_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:802:1: simple_operator : ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) expression_value ;
    public final simple_operator_return simple_operator() throws RecognitionException {
        simple_operator_return retval = new simple_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUAL250=null;
        Token GREATER251=null;
        Token GREATER_EQUAL252=null;
        Token LESS253=null;
        Token LESS_EQUAL254=null;
        Token NOT_EQUAL255=null;
        Token ID261=null;
        Token GRAVE_ACCENT262=null;
        Token ID263=null;
        Token ID270=null;
        Token GRAVE_ACCENT271=null;
        Token ID272=null;
        not_key_return not_key256 = null;

        contains_key_return contains_key257 = null;

        soundslike_key_return soundslike_key258 = null;

        matches_key_return matches_key259 = null;

        memberof_key_return memberof_key260 = null;

        square_chunk_return square_chunk264 = null;

        contains_key_return contains_key265 = null;

        excludes_key_return excludes_key266 = null;

        matches_key_return matches_key267 = null;

        soundslike_key_return soundslike_key268 = null;

        memberof_key_return memberof_key269 = null;

        square_chunk_return square_chunk273 = null;

        expression_value_return expression_value274 = null;


        Object EQUAL250_tree=null;
        Object GREATER251_tree=null;
        Object GREATER_EQUAL252_tree=null;
        Object LESS253_tree=null;
        Object LESS_EQUAL254_tree=null;
        Object NOT_EQUAL255_tree=null;
        Object ID261_tree=null;
        Object GRAVE_ACCENT262_tree=null;
        Object ID263_tree=null;
        Object ID270_tree=null;
        Object GRAVE_ACCENT271_tree=null;
        Object ID272_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:2: ( ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) expression_value )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) expression_value
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )
            int alt86=14;
            switch ( input.LA(1) ) {
            case EQUAL:
                {
                alt86=1;
                }
                break;
            case GREATER:
                {
                alt86=2;
                }
                break;
            case GREATER_EQUAL:
                {
                alt86=3;
                }
                break;
            case LESS:
                {
                alt86=4;
                }
                break;
            case LESS_EQUAL:
                {
                alt86=5;
                }
                break;
            case NOT_EQUAL:
                {
                alt86=6;
                }
                break;
            case ID:
                {
                int LA86_7 = input.LA(2);

                if ( (LA86_7==ID||LA86_7==GRAVE_ACCENT) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                    alt86=7;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.CONTAINS))) ) {
                    alt86=8;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))) ) {
                    alt86=9;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.MATCHES))) ) {
                    alt86=10;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))) ) {
                    alt86=11;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))) ) {
                    alt86=12;
                }
                else if ( (true) ) {
                    alt86=13;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("803:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 86, 7, input);

                    throw nvae;
                }
                }
                break;
            case GRAVE_ACCENT:
                {
                alt86=14;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("803:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 86, 0, input);

                throw nvae;
            }

            switch (alt86) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:5: EQUAL
                    {
                    EQUAL250=(Token)input.LT(1);
                    match(input,EQUAL,FOLLOW_EQUAL_in_simple_operator3449); if (failed) return retval;
                    if ( backtracking==0 ) {
                    EQUAL250_tree = (Object)adaptor.create(EQUAL250);
                    root_0 = (Object)adaptor.becomeRoot(EQUAL250_tree, root_0);
                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:804:4: GREATER
                    {
                    GREATER251=(Token)input.LT(1);
                    match(input,GREATER,FOLLOW_GREATER_in_simple_operator3455); if (failed) return retval;
                    if ( backtracking==0 ) {
                    GREATER251_tree = (Object)adaptor.create(GREATER251);
                    root_0 = (Object)adaptor.becomeRoot(GREATER251_tree, root_0);
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:805:4: GREATER_EQUAL
                    {
                    GREATER_EQUAL252=(Token)input.LT(1);
                    match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_simple_operator3461); if (failed) return retval;
                    if ( backtracking==0 ) {
                    GREATER_EQUAL252_tree = (Object)adaptor.create(GREATER_EQUAL252);
                    root_0 = (Object)adaptor.becomeRoot(GREATER_EQUAL252_tree, root_0);
                    }

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:806:4: LESS
                    {
                    LESS253=(Token)input.LT(1);
                    match(input,LESS,FOLLOW_LESS_in_simple_operator3467); if (failed) return retval;
                    if ( backtracking==0 ) {
                    LESS253_tree = (Object)adaptor.create(LESS253);
                    root_0 = (Object)adaptor.becomeRoot(LESS253_tree, root_0);
                    }

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:807:4: LESS_EQUAL
                    {
                    LESS_EQUAL254=(Token)input.LT(1);
                    match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_simple_operator3473); if (failed) return retval;
                    if ( backtracking==0 ) {
                    LESS_EQUAL254_tree = (Object)adaptor.create(LESS_EQUAL254);
                    root_0 = (Object)adaptor.becomeRoot(LESS_EQUAL254_tree, root_0);
                    }

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:808:4: NOT_EQUAL
                    {
                    NOT_EQUAL255=(Token)input.LT(1);
                    match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_simple_operator3479); if (failed) return retval;
                    if ( backtracking==0 ) {
                    NOT_EQUAL255_tree = (Object)adaptor.create(NOT_EQUAL255);
                    root_0 = (Object)adaptor.becomeRoot(NOT_EQUAL255_tree, root_0);
                    }

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:4: not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )
                    {
                    pushFollow(FOLLOW_not_key_in_simple_operator3485);
                    not_key256=not_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, not_key256.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:12: ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )
                    int alt85=6;
                    int LA85_0 = input.LA(1);

                    if ( (LA85_0==ID) ) {
                        int LA85_1 = input.LA(2);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.CONTAINS))) ) {
                            alt85=1;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))) ) {
                            alt85=2;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.MATCHES))) ) {
                            alt85=3;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))) ) {
                            alt85=4;
                        }
                        else if ( (true) ) {
                            alt85=5;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("809:12: ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 85, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA85_0==GRAVE_ACCENT) ) {
                        alt85=6;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("809:12: ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 85, 0, input);

                        throw nvae;
                    }
                    switch (alt85) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:13: contains_key
                            {
                            pushFollow(FOLLOW_contains_key_in_simple_operator3488);
                            contains_key257=contains_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(contains_key257.getTree(), root_0);

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:27: soundslike_key
                            {
                            pushFollow(FOLLOW_soundslike_key_in_simple_operator3491);
                            soundslike_key258=soundslike_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(soundslike_key258.getTree(), root_0);

                            }
                            break;
                        case 3 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:43: matches_key
                            {
                            pushFollow(FOLLOW_matches_key_in_simple_operator3494);
                            matches_key259=matches_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(matches_key259.getTree(), root_0);

                            }
                            break;
                        case 4 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:56: memberof_key
                            {
                            pushFollow(FOLLOW_memberof_key_in_simple_operator3497);
                            memberof_key260=memberof_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(memberof_key260.getTree(), root_0);

                            }
                            break;
                        case 5 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:71: ID
                            {
                            ID261=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_simple_operator3501); if (failed) return retval;
                            if ( backtracking==0 ) {
                            ID261_tree = (Object)adaptor.create(ID261);
                            root_0 = (Object)adaptor.becomeRoot(ID261_tree, root_0);
                            }

                            }
                            break;
                        case 6 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:77: GRAVE_ACCENT ID square_chunk
                            {
                            GRAVE_ACCENT262=(Token)input.LT(1);
                            match(input,GRAVE_ACCENT,FOLLOW_GRAVE_ACCENT_in_simple_operator3506); if (failed) return retval;
                            ID263=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_simple_operator3509); if (failed) return retval;
                            if ( backtracking==0 ) {
                            ID263_tree = (Object)adaptor.create(ID263);
                            root_0 = (Object)adaptor.becomeRoot(ID263_tree, root_0);
                            }
                            pushFollow(FOLLOW_square_chunk_in_simple_operator3512);
                            square_chunk264=square_chunk();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk264.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:810:4: contains_key
                    {
                    pushFollow(FOLLOW_contains_key_in_simple_operator3518);
                    contains_key265=contains_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(contains_key265.getTree(), root_0);

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:811:4: excludes_key
                    {
                    pushFollow(FOLLOW_excludes_key_in_simple_operator3524);
                    excludes_key266=excludes_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(excludes_key266.getTree(), root_0);

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:812:4: matches_key
                    {
                    pushFollow(FOLLOW_matches_key_in_simple_operator3530);
                    matches_key267=matches_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(matches_key267.getTree(), root_0);

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:813:4: soundslike_key
                    {
                    pushFollow(FOLLOW_soundslike_key_in_simple_operator3536);
                    soundslike_key268=soundslike_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(soundslike_key268.getTree(), root_0);

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:814:4: memberof_key
                    {
                    pushFollow(FOLLOW_memberof_key_in_simple_operator3542);
                    memberof_key269=memberof_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(memberof_key269.getTree(), root_0);

                    }
                    break;
                case 13 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:815:4: ID
                    {
                    ID270=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator3548); if (failed) return retval;
                    if ( backtracking==0 ) {
                    ID270_tree = (Object)adaptor.create(ID270);
                    root_0 = (Object)adaptor.becomeRoot(ID270_tree, root_0);
                    }

                    }
                    break;
                case 14 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:816:4: GRAVE_ACCENT ID square_chunk
                    {
                    GRAVE_ACCENT271=(Token)input.LT(1);
                    match(input,GRAVE_ACCENT,FOLLOW_GRAVE_ACCENT_in_simple_operator3554); if (failed) return retval;
                    ID272=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator3557); if (failed) return retval;
                    if ( backtracking==0 ) {
                    ID272_tree = (Object)adaptor.create(ID272);
                    root_0 = (Object)adaptor.becomeRoot(ID272_tree, root_0);
                    }
                    pushFollow(FOLLOW_square_chunk_in_simple_operator3560);
                    square_chunk273=square_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk273.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_expression_value_in_simple_operator3564);
            expression_value274=expression_value();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression_value274.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:821:1: compound_operator : ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN ;
    public final compound_operator_return compound_operator() throws RecognitionException {
        compound_operator_return retval = new compound_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN278=null;
        Token COMMA280=null;
        Token RIGHT_PAREN282=null;
        in_key_return in_key275 = null;

        not_key_return not_key276 = null;

        in_key_return in_key277 = null;

        expression_value_return expression_value279 = null;

        expression_value_return expression_value281 = null;


        Object LEFT_PAREN278_tree=null;
        Object COMMA280_tree=null;
        Object RIGHT_PAREN282_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:2: ( ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:4: ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:4: ( in_key | not_key in_key )
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IN))||(validateIdentifierKey(DroolsSoftKeywords.NOT))))) {
                int LA87_1 = input.LA(2);

                if ( (LA87_1==ID) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                    alt87=2;
                }
                else if ( (LA87_1==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.IN)))) {
                    alt87=1;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("822:4: ( in_key | not_key in_key )", 87, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("822:4: ( in_key | not_key in_key )", 87, 0, input);

                throw nvae;
            }
            switch (alt87) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:6: in_key
                    {
                    pushFollow(FOLLOW_in_key_in_compound_operator3579);
                    in_key275=in_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key275.getTree(), root_0);

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:16: not_key in_key
                    {
                    pushFollow(FOLLOW_not_key_in_compound_operator3584);
                    not_key276=not_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, not_key276.getTree());
                    pushFollow(FOLLOW_in_key_in_compound_operator3586);
                    in_key277=in_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key277.getTree(), root_0);

                    }
                    break;

            }

            LEFT_PAREN278=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator3591); if (failed) return retval;
            pushFollow(FOLLOW_expression_value_in_compound_operator3594);
            expression_value279=expression_value();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression_value279.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:63: ( COMMA expression_value )*
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);

                if ( (LA88_0==COMMA) ) {
                    alt88=1;
                }


                switch (alt88) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:65: COMMA expression_value
            	    {
            	    COMMA280=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_compound_operator3598); if (failed) return retval;
            	    pushFollow(FOLLOW_expression_value_in_compound_operator3601);
            	    expression_value281=expression_value();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, expression_value281.getTree());

            	    }
            	    break;

            	default :
            	    break loop88;
                }
            } while (true);

            RIGHT_PAREN282=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator3606); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_PAREN282_tree = (Object)adaptor.create(RIGHT_PAREN282);
            adaptor.addChild(root_0, RIGHT_PAREN282_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:825:1: expression_value : ( accessor_path | literal_constraint | paren_chunk );
    public final expression_value_return expression_value() throws RecognitionException {
        expression_value_return retval = new expression_value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        accessor_path_return accessor_path283 = null;

        literal_constraint_return literal_constraint284 = null;

        paren_chunk_return paren_chunk285 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:826:2: ( accessor_path | literal_constraint | paren_chunk )
            int alt89=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt89=1;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt89=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt89=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("825:1: expression_value : ( accessor_path | literal_constraint | paren_chunk );", 89, 0, input);

                throw nvae;
            }

            switch (alt89) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:826:4: accessor_path
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_accessor_path_in_expression_value3617);
                    accessor_path283=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, accessor_path283.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:4: literal_constraint
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_constraint_in_expression_value3622);
                    literal_constraint284=literal_constraint();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, literal_constraint284.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:828:4: paren_chunk
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_paren_chunk_in_expression_value3628);
                    paren_chunk285=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk285.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:831:1: literal_constraint : ( STRING | INT | FLOAT | BOOL | NULL );
    public final literal_constraint_return literal_constraint() throws RecognitionException {
        literal_constraint_return retval = new literal_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set286=null;

        Object set286_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:832:2: ( STRING | INT | FLOAT | BOOL | NULL )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
            {
            root_0 = (Object)adaptor.nil();

            set286=(Token)input.LT(1);
            if ( input.LA(1)==STRING||(input.LA(1)>=BOOL && input.LA(1)<=INT)||(input.LA(1)>=FLOAT && input.LA(1)<=NULL) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set286));
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:839:1: pattern_type : id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final pattern_type_return pattern_type() throws RecognitionException {
        pattern_type_return retval = new pattern_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;
        dimension_definition_return dimension_definition287 = null;


        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:840:2: (id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:840:4: id+= ID (id+= DOT id+= ID )* ( dimension_definition )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_pattern_type3672); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:840:11: (id+= DOT id+= ID )*
            loop90:
            do {
                int alt90=2;
                int LA90_0 = input.LA(1);

                if ( (LA90_0==DOT) ) {
                    alt90=1;
                }


                switch (alt90) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:840:13: id+= DOT id+= ID
            	    {
            	    id=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pattern_type3678); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_pattern_type3682); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop90;
                }
            } while (true);

            if ( backtracking==0 ) {
              	setParaphrasesValue(DroolsParaphareseTypes.PATTERN, buildStringFromTokens(list_id));	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:842:6: ( dimension_definition )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==LEFT_SQUARE) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:842:6: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_pattern_type3697);
            	    dimension_definition287=dimension_definition();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_dimension_definition.add(dimension_definition287.getTree());

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
            // 843:3: -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:843:6: ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
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
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:843:28: ( dimension_definition )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:1: data_type : ID ( DOT ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final data_type_return data_type() throws RecognitionException {
        data_type_return retval = new data_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID288=null;
        Token DOT289=null;
        Token ID290=null;
        dimension_definition_return dimension_definition291 = null;


        Object ID288_tree=null;
        Object DOT289_tree=null;
        Object ID290_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:2: ( ID ( DOT ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:4: ID ( DOT ID )* ( dimension_definition )*
            {
            ID288=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_data_type3723); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID288);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:7: ( DOT ID )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==DOT) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:9: DOT ID
            	    {
            	    DOT289=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_data_type3727); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(DOT289);

            	    ID290=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_data_type3729); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(ID290);


            	    }
            	    break;

            	default :
            	    break loop92;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:19: ( dimension_definition )*
            loop93:
            do {
                int alt93=2;
                int LA93_0 = input.LA(1);

                if ( (LA93_0==LEFT_SQUARE) ) {
                    alt93=1;
                }


                switch (alt93) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:19: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_data_type3734);
            	    dimension_definition291=dimension_definition();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_dimension_definition.add(dimension_definition291.getTree());

            	    }
            	    break;

            	default :
            	    break loop93;
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
            // 848:3: -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:848:6: ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
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
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:848:25: ( dimension_definition )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:851:1: dimension_definition : LEFT_SQUARE RIGHT_SQUARE ;
    public final dimension_definition_return dimension_definition() throws RecognitionException {
        dimension_definition_return retval = new dimension_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE292=null;
        Token RIGHT_SQUARE293=null;

        Object LEFT_SQUARE292_tree=null;
        Object RIGHT_SQUARE293_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:852:2: ( LEFT_SQUARE RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:852:4: LEFT_SQUARE RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE292=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dimension_definition3760); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_SQUARE292_tree = (Object)adaptor.create(LEFT_SQUARE292);
            adaptor.addChild(root_0, LEFT_SQUARE292_tree);
            }
            RIGHT_SQUARE293=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dimension_definition3762); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_SQUARE293_tree = (Object)adaptor.create(RIGHT_SQUARE293);
            adaptor.addChild(root_0, RIGHT_SQUARE293_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:855:1: accessor_path : accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) ;
    public final accessor_path_return accessor_path() throws RecognitionException {
        accessor_path_return retval = new accessor_path_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT295=null;
        accessor_element_return accessor_element294 = null;

        accessor_element_return accessor_element296 = null;


        Object DOT295_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_accessor_element=new RewriteRuleSubtreeStream(adaptor,"rule accessor_element");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:856:2: ( accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:856:4: accessor_element ( DOT accessor_element )*
            {
            pushFollow(FOLLOW_accessor_element_in_accessor_path3773);
            accessor_element294=accessor_element();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_accessor_element.add(accessor_element294.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:856:21: ( DOT accessor_element )*
            loop94:
            do {
                int alt94=2;
                int LA94_0 = input.LA(1);

                if ( (LA94_0==DOT) ) {
                    alt94=1;
                }


                switch (alt94) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:856:23: DOT accessor_element
            	    {
            	    DOT295=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_accessor_path3777); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(DOT295);

            	    pushFollow(FOLLOW_accessor_element_in_accessor_path3779);
            	    accessor_element296=accessor_element();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_accessor_element.add(accessor_element296.getTree());

            	    }
            	    break;

            	default :
            	    break loop94;
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
            // 857:2: -> ^( VT_ACCESSOR_PATH ( accessor_element )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:5: ^( VT_ACCESSOR_PATH ( accessor_element )+ )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:860:1: accessor_element : ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) ;
    public final accessor_element_return accessor_element() throws RecognitionException {
        accessor_element_return retval = new accessor_element_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID297=null;
        square_chunk_return square_chunk298 = null;


        Object ID297_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:861:2: ( ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:861:4: ID ( square_chunk )*
            {
            ID297=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accessor_element3803); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID297);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:861:7: ( square_chunk )*
            loop95:
            do {
                int alt95=2;
                int LA95_0 = input.LA(1);

                if ( (LA95_0==LEFT_SQUARE) ) {
                    alt95=1;
                }


                switch (alt95) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:861:7: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_accessor_element3805);
            	    square_chunk298=square_chunk();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_square_chunk.add(square_chunk298.getTree());

            	    }
            	    break;

            	default :
            	    break loop95;
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
            // 862:2: -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:862:5: ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ACCESSOR_ELEMENT, "VT_ACCESSOR_ELEMENT"), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:862:30: ( square_chunk )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:865:1: rhs_chunk : rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] ;
    public final rhs_chunk_return rhs_chunk() throws RecognitionException {
        rhs_chunk_return retval = new rhs_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        rhs_chunk_data_return rc = null;


        RewriteRuleSubtreeStream stream_rhs_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:868:3: (rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:868:5: rc= rhs_chunk_data
            {
            pushFollow(FOLLOW_rhs_chunk_data_in_rhs_chunk3834);
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
            // 869:2: -> VT_RHS_CHUNK[$rc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:872:1: rhs_chunk_data : THEN (~ END )* END ( SEMICOLON )? ;
    public final rhs_chunk_data_return rhs_chunk_data() throws RecognitionException {
        rhs_chunk_data_return retval = new rhs_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token THEN299=null;
        Token set300=null;
        Token END301=null;
        Token SEMICOLON302=null;

        Object THEN299_tree=null;
        Object set300_tree=null;
        Object END301_tree=null;
        Object SEMICOLON302_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:2: ( THEN (~ END )* END ( SEMICOLON )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:4: THEN (~ END )* END ( SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            THEN299=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk_data3853); if (failed) return retval;
            if ( backtracking==0 ) {
            THEN299_tree = (Object)adaptor.create(THEN299);
            adaptor.addChild(root_0, THEN299_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:9: (~ END )*
            loop96:
            do {
                int alt96=2;
                int LA96_0 = input.LA(1);

                if ( ((LA96_0>=VT_COMPILATION_UNIT && LA96_0<=DOT_STAR)||(LA96_0>=STRING && LA96_0<=MULTI_LINE_COMMENT)) ) {
                    alt96=1;
                }


                switch (alt96) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:11: ~ END
            	    {
            	    set300=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=DOT_STAR)||(input.LA(1)>=STRING && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set300));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk_data3857);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop96;
                }
            } while (true);

            END301=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_rhs_chunk_data3863); if (failed) return retval;
            if ( backtracking==0 ) {
            END301_tree = (Object)adaptor.create(END301);
            adaptor.addChild(root_0, END301_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:23: ( SEMICOLON )?
            int alt97=2;
            int LA97_0 = input.LA(1);

            if ( (LA97_0==SEMICOLON) ) {
                alt97=1;
            }
            switch (alt97) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:23: SEMICOLON
                    {
                    SEMICOLON302=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_rhs_chunk_data3865); if (failed) return retval;
                    if ( backtracking==0 ) {
                    SEMICOLON302_tree = (Object)adaptor.create(SEMICOLON302);
                    adaptor.addChild(root_0, SEMICOLON302_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:876:1: curly_chunk : cc= curly_chunk_data -> VT_CURLY_CHUNK[$cc.start,text] ;
    public final curly_chunk_return curly_chunk() throws RecognitionException {
        curly_chunk_return retval = new curly_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        curly_chunk_data_return cc = null;


        RewriteRuleSubtreeStream stream_curly_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule curly_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:879:3: (cc= curly_chunk_data -> VT_CURLY_CHUNK[$cc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:879:5: cc= curly_chunk_data
            {
            pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk3882);
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
            // 880:2: -> VT_CURLY_CHUNK[$cc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:883:1: curly_chunk_data : LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )* RIGHT_CURLY ;
    public final curly_chunk_data_return curly_chunk_data() throws RecognitionException {
        curly_chunk_data_return retval = new curly_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_CURLY303=null;
        Token set304=null;
        Token RIGHT_CURLY306=null;
        curly_chunk_data_return curly_chunk_data305 = null;


        Object LEFT_CURLY303_tree=null;
        Object set304_tree=null;
        Object RIGHT_CURLY306_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:884:2: ( LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )* RIGHT_CURLY )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:884:4: LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )* RIGHT_CURLY
            {
            root_0 = (Object)adaptor.nil();

            LEFT_CURLY303=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk_data3901); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_CURLY303_tree = (Object)adaptor.create(LEFT_CURLY303);
            adaptor.addChild(root_0, LEFT_CURLY303_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:884:15: (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )*
            loop98:
            do {
                int alt98=3;
                int LA98_0 = input.LA(1);

                if ( ((LA98_0>=VT_COMPILATION_UNIT && LA98_0<=THEN)||(LA98_0>=MISC && LA98_0<=MULTI_LINE_COMMENT)) ) {
                    alt98=1;
                }
                else if ( (LA98_0==LEFT_CURLY) ) {
                    alt98=2;
                }


                switch (alt98) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:884:16: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    set304=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=THEN)||(input.LA(1)>=MISC && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set304));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk_data3904);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:884:49: curly_chunk_data
            	    {
            	    pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk_data3918);
            	    curly_chunk_data305=curly_chunk_data();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, curly_chunk_data305.getTree());

            	    }
            	    break;

            	default :
            	    break loop98;
                }
            } while (true);

            RIGHT_CURLY306=(Token)input.LT(1);
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk_data3923); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_CURLY306_tree = (Object)adaptor.create(RIGHT_CURLY306);
            adaptor.addChild(root_0, RIGHT_CURLY306_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:887:1: paren_chunk : pc= paren_chunk_data -> VT_PAREN_CHUNK[$pc.start,text] ;
    public final paren_chunk_return paren_chunk() throws RecognitionException {
        paren_chunk_return retval = new paren_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        paren_chunk_data_return pc = null;


        RewriteRuleSubtreeStream stream_paren_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:890:3: (pc= paren_chunk_data -> VT_PAREN_CHUNK[$pc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:890:5: pc= paren_chunk_data
            {
            pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk3939);
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
            // 891:2: -> VT_PAREN_CHUNK[$pc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:894:1: paren_chunk_data : LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )* RIGHT_PAREN ;
    public final paren_chunk_data_return paren_chunk_data() throws RecognitionException {
        paren_chunk_data_return retval = new paren_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN307=null;
        Token set308=null;
        Token RIGHT_PAREN310=null;
        paren_chunk_data_return paren_chunk_data309 = null;


        Object LEFT_PAREN307_tree=null;
        Object set308_tree=null;
        Object RIGHT_PAREN310_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:895:2: ( LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )* RIGHT_PAREN )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:895:4: LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )* RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            LEFT_PAREN307=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk_data3959); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_PAREN307_tree = (Object)adaptor.create(LEFT_PAREN307);
            adaptor.addChild(root_0, LEFT_PAREN307_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:895:15: (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )*
            loop99:
            do {
                int alt99=3;
                int LA99_0 = input.LA(1);

                if ( ((LA99_0>=VT_COMPILATION_UNIT && LA99_0<=STRING)||LA99_0==COMMA||(LA99_0>=AT && LA99_0<=MULTI_LINE_COMMENT)) ) {
                    alt99=1;
                }
                else if ( (LA99_0==LEFT_PAREN) ) {
                    alt99=2;
                }


                switch (alt99) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:895:16: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    set308=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=STRING)||input.LA(1)==COMMA||(input.LA(1)>=AT && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set308));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk_data3962);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:895:49: paren_chunk_data
            	    {
            	    pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk_data3976);
            	    paren_chunk_data309=paren_chunk_data();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk_data309.getTree());

            	    }
            	    break;

            	default :
            	    break loop99;
                }
            } while (true);

            RIGHT_PAREN310=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk_data3981); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_PAREN310_tree = (Object)adaptor.create(RIGHT_PAREN310);
            adaptor.addChild(root_0, RIGHT_PAREN310_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:898:1: square_chunk : sc= square_chunk_data -> VT_SQUARE_CHUNK[$sc.start,text] ;
    public final square_chunk_return square_chunk() throws RecognitionException {
        square_chunk_return retval = new square_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        square_chunk_data_return sc = null;


        RewriteRuleSubtreeStream stream_square_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:901:3: (sc= square_chunk_data -> VT_SQUARE_CHUNK[$sc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:901:5: sc= square_chunk_data
            {
            pushFollow(FOLLOW_square_chunk_data_in_square_chunk3998);
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
            // 902:2: -> VT_SQUARE_CHUNK[$sc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:905:1: square_chunk_data : LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )* RIGHT_SQUARE ;
    public final square_chunk_data_return square_chunk_data() throws RecognitionException {
        square_chunk_data_return retval = new square_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE311=null;
        Token set312=null;
        Token RIGHT_SQUARE314=null;
        square_chunk_data_return square_chunk_data313 = null;


        Object LEFT_SQUARE311_tree=null;
        Object set312_tree=null;
        Object RIGHT_SQUARE314_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:906:2: ( LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )* RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:906:4: LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )* RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE311=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk_data4017); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_SQUARE311_tree = (Object)adaptor.create(LEFT_SQUARE311);
            adaptor.addChild(root_0, LEFT_SQUARE311_tree);
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:906:16: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )*
            loop100:
            do {
                int alt100=3;
                int LA100_0 = input.LA(1);

                if ( ((LA100_0>=VT_COMPILATION_UNIT && LA100_0<=NULL)||(LA100_0>=THEN && LA100_0<=MULTI_LINE_COMMENT)) ) {
                    alt100=1;
                }
                else if ( (LA100_0==LEFT_SQUARE) ) {
                    alt100=2;
                }


                switch (alt100) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:906:17: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    set312=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=NULL)||(input.LA(1)>=THEN && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set312));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk_data4020);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:906:52: square_chunk_data
            	    {
            	    pushFollow(FOLLOW_square_chunk_data_in_square_chunk_data4034);
            	    square_chunk_data313=square_chunk_data();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk_data313.getTree());

            	    }
            	    break;

            	default :
            	    break loop100;
                }
            } while (true);

            RIGHT_SQUARE314=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk_data4039); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_SQUARE314_tree = (Object)adaptor.create(RIGHT_SQUARE314);
            adaptor.addChild(root_0, RIGHT_SQUARE314_tree);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:910:1: date_effective_key : {...}? => ID MISC ID -> VK_DATE_EFFECTIVE[$start, text] ;
    public final date_effective_key_return date_effective_key() throws RecognitionException {
        date_effective_key_return retval = new date_effective_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID315=null;
        Token MISC316=null;
        Token ID317=null;

        Object ID315_tree=null;
        Object MISC316_tree=null;
        Object ID317_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:913:3: ({...}? => ID MISC ID -> VK_DATE_EFFECTIVE[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:913:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "date_effective_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))");
            }
            ID315=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_effective_key4058); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID315);

            MISC316=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_date_effective_key4060); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC316);

            ID317=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_effective_key4062); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID317);

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
            // 914:2: -> VK_DATE_EFFECTIVE[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:917:1: date_expires_key : {...}? => ID MISC ID -> VK_DATE_EXPIRES[$start, text] ;
    public final date_expires_key_return date_expires_key() throws RecognitionException {
        date_expires_key_return retval = new date_expires_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID318=null;
        Token MISC319=null;
        Token ID320=null;

        Object ID318_tree=null;
        Object MISC319_tree=null;
        Object ID320_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:920:3: ({...}? => ID MISC ID -> VK_DATE_EXPIRES[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:920:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "date_expires_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EXPIRES))");
            }
            ID318=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_expires_key4088); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID318);

            MISC319=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_date_expires_key4090); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC319);

            ID320=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_expires_key4092); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID320);

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
            // 921:2: -> VK_DATE_EXPIRES[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:924:1: lock_on_active_key : {...}? => ID MISC ID MISC ID -> VK_LOCK_ON_ACTIVE[$start, text] ;
    public final lock_on_active_key_return lock_on_active_key() throws RecognitionException {
        lock_on_active_key_return retval = new lock_on_active_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID321=null;
        Token MISC322=null;
        Token ID323=null;
        Token MISC324=null;
        Token ID325=null;

        Object ID321_tree=null;
        Object MISC322_tree=null;
        Object ID323_tree=null;
        Object MISC324_tree=null;
        Object ID325_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:927:3: ({...}? => ID MISC ID MISC ID -> VK_LOCK_ON_ACTIVE[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:927:5: {...}? => ID MISC ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "lock_on_active_key", "(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, \"-\") && validateLT(5, DroolsSoftKeywords.ACTIVE))");
            }
            ID321=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key4118); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID321);

            MISC322=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_lock_on_active_key4120); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC322);

            ID323=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key4122); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID323);

            MISC324=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_lock_on_active_key4124); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC324);

            ID325=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key4126); if (failed) return retval;
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
            // 928:2: -> VK_LOCK_ON_ACTIVE[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:931:1: no_loop_key : {...}? => ID MISC ID -> VK_NO_LOOP[$start, text] ;
    public final no_loop_key_return no_loop_key() throws RecognitionException {
        no_loop_key_return retval = new no_loop_key_return();
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:934:3: ({...}? => ID MISC ID -> VK_NO_LOOP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:934:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "no_loop_key", "(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.LOOP))");
            }
            ID326=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_no_loop_key4152); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID326);

            MISC327=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_no_loop_key4154); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC327);

            ID328=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_no_loop_key4156); if (failed) return retval;
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
            // 935:2: -> VK_NO_LOOP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:938:1: auto_focus_key : {...}? => ID MISC ID -> VK_AUTO_FOCUS[$start, text] ;
    public final auto_focus_key_return auto_focus_key() throws RecognitionException {
        auto_focus_key_return retval = new auto_focus_key_return();
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:941:3: ({...}? => ID MISC ID -> VK_AUTO_FOCUS[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:941:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "auto_focus_key", "(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.FOCUS))");
            }
            ID329=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_auto_focus_key4182); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID329);

            MISC330=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_auto_focus_key4184); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC330);

            ID331=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_auto_focus_key4186); if (failed) return retval;
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
            // 942:2: -> VK_AUTO_FOCUS[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:945:1: activation_group_key : {...}? => ID MISC ID -> VK_ACTIVATION_GROUP[$start, text] ;
    public final activation_group_key_return activation_group_key() throws RecognitionException {
        activation_group_key_return retval = new activation_group_key_return();
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:948:3: ({...}? => ID MISC ID -> VK_ACTIVATION_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:948:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "activation_group_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            ID332=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_activation_group_key4212); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID332);

            MISC333=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_activation_group_key4214); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC333);

            ID334=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_activation_group_key4216); if (failed) return retval;
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
            // 949:2: -> VK_ACTIVATION_GROUP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:952:1: agenda_group_key : {...}? => ID MISC ID -> VK_AGENDA_GROUP[$start, text] ;
    public final agenda_group_key_return agenda_group_key() throws RecognitionException {
        agenda_group_key_return retval = new agenda_group_key_return();
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:955:3: ({...}? => ID MISC ID -> VK_AGENDA_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:955:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "agenda_group_key", "(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            ID335=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_agenda_group_key4242); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID335);

            MISC336=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_agenda_group_key4244); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC336);

            ID337=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_agenda_group_key4246); if (failed) return retval;
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
            // 956:2: -> VK_AGENDA_GROUP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:959:1: ruleflow_group_key : {...}? => ID MISC ID -> VK_RULEFLOW_GROUP[$start, text] ;
    public final ruleflow_group_key_return ruleflow_group_key() throws RecognitionException {
        ruleflow_group_key_return retval = new ruleflow_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID338=null;
        Token MISC339=null;
        Token ID340=null;

        Object ID338_tree=null;
        Object MISC339_tree=null;
        Object ID340_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:962:3: ({...}? => ID MISC ID -> VK_RULEFLOW_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:962:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "ruleflow_group_key", "(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            ID338=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_ruleflow_group_key4272); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID338);

            MISC339=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_ruleflow_group_key4274); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC339);

            ID340=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_ruleflow_group_key4276); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID340);

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
            // 963:2: -> VK_RULEFLOW_GROUP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:966:1: duration_key : {...}? =>id= ID -> VK_DURATION[$id] ;
    public final duration_key_return duration_key() throws RecognitionException {
        duration_key_return retval = new duration_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:967:2: ({...}? =>id= ID -> VK_DURATION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:967:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DURATION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "duration_key", "(validateIdentifierKey(DroolsSoftKeywords.DURATION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_duration_key4301); if (failed) return retval;
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
            // 967:69: -> VK_DURATION[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:970:1: package_key : {...}? =>id= ID -> VK_PACKAGE[$id] ;
    public final package_key_return package_key() throws RecognitionException {
        package_key_return retval = new package_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:971:2: ({...}? =>id= ID -> VK_PACKAGE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:971:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.PACKAGE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "package_key", "(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_key4323); if (failed) return retval;
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
            // 971:68: -> VK_PACKAGE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:974:1: import_key : {...}? =>id= ID -> VK_IMPORT[$id] ;
    public final import_key_return import_key() throws RecognitionException {
        import_key_return retval = new import_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:975:2: ({...}? =>id= ID -> VK_IMPORT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:975:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "import_key", "(validateIdentifierKey(DroolsSoftKeywords.IMPORT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_key4345); if (failed) return retval;
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
            // 975:67: -> VK_IMPORT[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:1: dialect_key : {...}? =>id= ID -> VK_DIALECT[$id] ;
    public final dialect_key_return dialect_key() throws RecognitionException {
        dialect_key_return retval = new dialect_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:979:2: ({...}? =>id= ID -> VK_DIALECT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:979:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DIALECT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "dialect_key", "(validateIdentifierKey(DroolsSoftKeywords.DIALECT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dialect_key4367); if (failed) return retval;
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
            // 979:68: -> VK_DIALECT[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:982:1: salience_key : {...}? =>id= ID -> VK_SALIENCE[$id] ;
    public final salience_key_return salience_key() throws RecognitionException {
        salience_key_return retval = new salience_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:983:2: ({...}? =>id= ID -> VK_SALIENCE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:983:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "salience_key", "(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_salience_key4389); if (failed) return retval;
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
            // 983:69: -> VK_SALIENCE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:986:1: enabled_key : {...}? =>id= ID -> VK_ENABLED[$id] ;
    public final enabled_key_return enabled_key() throws RecognitionException {
        enabled_key_return retval = new enabled_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:987:2: ({...}? =>id= ID -> VK_ENABLED[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:987:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ENABLED))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "enabled_key", "(validateIdentifierKey(DroolsSoftKeywords.ENABLED))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enabled_key4411); if (failed) return retval;
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
            // 987:68: -> VK_ENABLED[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:990:1: attributes_key : {...}? =>id= ID -> VK_ATTRIBUTES[$id] ;
    public final attributes_key_return attributes_key() throws RecognitionException {
        attributes_key_return retval = new attributes_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:991:2: ({...}? =>id= ID -> VK_ATTRIBUTES[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:991:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "attributes_key", "(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_attributes_key4433); if (failed) return retval;
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
            // 991:71: -> VK_ATTRIBUTES[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:994:1: when_key : {...}? =>id= ID -> VK_WHEN[$id] ;
    public final when_key_return when_key() throws RecognitionException {
        when_key_return retval = new when_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:995:2: ({...}? =>id= ID -> VK_WHEN[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:995:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "when_key", "(validateIdentifierKey(DroolsSoftKeywords.WHEN))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_when_key4455); if (failed) return retval;
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
            // 995:65: -> VK_WHEN[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:998:1: rule_key : {...}? =>id= ID -> VK_RULE[$id] ;
    public final rule_key_return rule_key() throws RecognitionException {
        rule_key_return retval = new rule_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:2: ({...}? =>id= ID -> VK_RULE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "rule_key", "(validateIdentifierKey(DroolsSoftKeywords.RULE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_rule_key4477); if (failed) return retval;
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
            // 999:65: -> VK_RULE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1002:1: template_key : {...}? =>id= ID -> VK_TEMPLATE[$id] ;
    public final template_key_return template_key() throws RecognitionException {
        template_key_return retval = new template_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1003:2: ({...}? =>id= ID -> VK_TEMPLATE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1003:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "template_key", "(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template_key4499); if (failed) return retval;
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
            // 1003:69: -> VK_TEMPLATE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1006:1: query_key : {...}? =>id= ID -> VK_QUERY[$id] ;
    public final query_key_return query_key() throws RecognitionException {
        query_key_return retval = new query_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1007:2: ({...}? =>id= ID -> VK_QUERY[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1007:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "query_key", "(validateIdentifierKey(DroolsSoftKeywords.QUERY))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_query_key4521); if (failed) return retval;
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
            // 1007:66: -> VK_QUERY[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1010:1: declare_key : {...}? =>id= ID -> VK_DECLARE[$id] ;
    public final declare_key_return declare_key() throws RecognitionException {
        declare_key_return retval = new declare_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1011:2: ({...}? =>id= ID -> VK_DECLARE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1011:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DECLARE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "declare_key", "(validateIdentifierKey(DroolsSoftKeywords.DECLARE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_declare_key4543); if (failed) return retval;
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
            // 1011:68: -> VK_DECLARE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1014:1: function_key : {...}? =>id= ID -> VK_FUNCTION[$id] ;
    public final function_key_return function_key() throws RecognitionException {
        function_key_return retval = new function_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1015:2: ({...}? =>id= ID -> VK_FUNCTION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1015:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "function_key", "(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function_key4565); if (failed) return retval;
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
            // 1015:69: -> VK_FUNCTION[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1018:1: global_key : {...}? =>id= ID -> VK_GLOBAL[$id] ;
    public final global_key_return global_key() throws RecognitionException {
        global_key_return retval = new global_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1019:2: ({...}? =>id= ID -> VK_GLOBAL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1019:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "global_key", "(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global_key4587); if (failed) return retval;
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
            // 1019:67: -> VK_GLOBAL[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1022:1: eval_key : {...}? =>id= ID -> VK_EVAL[$id] ;
    public final eval_key_return eval_key() throws RecognitionException {
        eval_key_return retval = new eval_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1023:2: ({...}? =>id= ID -> VK_EVAL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1023:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "eval_key", "(validateIdentifierKey(DroolsSoftKeywords.EVAL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_eval_key4609); if (failed) return retval;
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
            // 1023:65: -> VK_EVAL[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1026:1: contains_key : {...}? =>id= ID -> VK_CONTAINS[$id] ;
    public final contains_key_return contains_key() throws RecognitionException {
        contains_key_return retval = new contains_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1027:2: ({...}? =>id= ID -> VK_CONTAINS[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1027:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.CONTAINS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "contains_key", "(validateIdentifierKey(DroolsSoftKeywords.CONTAINS))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_contains_key4631); if (failed) return retval;
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
            // 1027:69: -> VK_CONTAINS[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1030:1: matches_key : {...}? =>id= ID -> VK_MATCHES[$id] ;
    public final matches_key_return matches_key() throws RecognitionException {
        matches_key_return retval = new matches_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1031:2: ({...}? =>id= ID -> VK_MATCHES[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1031:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.MATCHES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "matches_key", "(validateIdentifierKey(DroolsSoftKeywords.MATCHES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_matches_key4653); if (failed) return retval;
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
            // 1031:68: -> VK_MATCHES[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1034:1: excludes_key : {...}? =>id= ID -> VK_EXCLUDES[$id] ;
    public final excludes_key_return excludes_key() throws RecognitionException {
        excludes_key_return retval = new excludes_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1035:2: ({...}? =>id= ID -> VK_EXCLUDES[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1035:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "excludes_key", "(validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_excludes_key4675); if (failed) return retval;
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
            // 1035:69: -> VK_EXCLUDES[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1038:1: soundslike_key : {...}? =>id= ID -> VK_SOUNDSLIKE[$id] ;
    public final soundslike_key_return soundslike_key() throws RecognitionException {
        soundslike_key_return retval = new soundslike_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1039:2: ({...}? =>id= ID -> VK_SOUNDSLIKE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1039:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "soundslike_key", "(validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_soundslike_key4697); if (failed) return retval;
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
            // 1039:71: -> VK_SOUNDSLIKE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1042:1: memberof_key : {...}? =>id= ID -> VK_MEMBEROF[$id] ;
    public final memberof_key_return memberof_key() throws RecognitionException {
        memberof_key_return retval = new memberof_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1043:2: ({...}? =>id= ID -> VK_MEMBEROF[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1043:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "memberof_key", "(validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_memberof_key4719); if (failed) return retval;
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
            // 1043:69: -> VK_MEMBEROF[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1046:1: not_key : {...}? =>id= ID -> VK_NOT[$id] ;
    public final not_key_return not_key() throws RecognitionException {
        not_key_return retval = new not_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1047:2: ({...}? =>id= ID -> VK_NOT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1047:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "not_key", "(validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_not_key4741); if (failed) return retval;
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
            // 1047:64: -> VK_NOT[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1050:1: in_key : {...}? =>id= ID -> VK_IN[$id] ;
    public final in_key_return in_key() throws RecognitionException {
        in_key_return retval = new in_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1051:2: ({...}? =>id= ID -> VK_IN[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1051:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "in_key", "(validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_in_key4763); if (failed) return retval;
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
            // 1051:63: -> VK_IN[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1054:1: or_key : {...}? =>id= ID -> VK_OR[$id] ;
    public final or_key_return or_key() throws RecognitionException {
        or_key_return retval = new or_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1055:2: ({...}? =>id= ID -> VK_OR[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1055:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.OR))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "or_key", "(validateIdentifierKey(DroolsSoftKeywords.OR))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_or_key4785); if (failed) return retval;
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
            // 1055:63: -> VK_OR[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1058:1: and_key : {...}? =>id= ID -> VK_AND[$id] ;
    public final and_key_return and_key() throws RecognitionException {
        and_key_return retval = new and_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1059:2: ({...}? =>id= ID -> VK_AND[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1059:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AND))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "and_key", "(validateIdentifierKey(DroolsSoftKeywords.AND))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_and_key4807); if (failed) return retval;
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
            // 1059:64: -> VK_AND[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1062:1: exists_key : {...}? =>id= ID -> VK_EXISTS[$id] ;
    public final exists_key_return exists_key() throws RecognitionException {
        exists_key_return retval = new exists_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1063:2: ({...}? =>id= ID -> VK_EXISTS[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1063:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EXISTS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "exists_key", "(validateIdentifierKey(DroolsSoftKeywords.EXISTS))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_exists_key4829); if (failed) return retval;
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
            // 1063:67: -> VK_EXISTS[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1066:1: forall_key : {...}? =>id= ID -> VK_FORALL[$id] ;
    public final forall_key_return forall_key() throws RecognitionException {
        forall_key_return retval = new forall_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1067:2: ({...}? =>id= ID -> VK_FORALL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1067:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.FORALL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "forall_key", "(validateIdentifierKey(DroolsSoftKeywords.FORALL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_forall_key4851); if (failed) return retval;
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
            // 1067:67: -> VK_FORALL[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1070:1: from_key : {...}? =>id= ID -> VK_FROM[$id] ;
    public final from_key_return from_key() throws RecognitionException {
        from_key_return retval = new from_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1071:2: ({...}? =>id= ID -> VK_FROM[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1071:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "from_key", "(validateIdentifierKey(DroolsSoftKeywords.FROM))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_from_key4873); if (failed) return retval;
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
            // 1071:65: -> VK_FROM[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1074:1: entry_point_key : {...}? => ID MISC ID -> VK_ENTRY_POINT[$start, text] ;
    public final entry_point_key_return entry_point_key() throws RecognitionException {
        entry_point_key_return retval = new entry_point_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID341=null;
        Token MISC342=null;
        Token ID343=null;

        Object ID341_tree=null;
        Object MISC342_tree=null;
        Object ID343_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1077:3: ({...}? => ID MISC ID -> VK_ENTRY_POINT[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1077:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "entry_point_key", "(validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.POINT))");
            }
            ID341=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_entry_point_key4896); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID341);

            MISC342=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_entry_point_key4898); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC342);

            ID343=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_entry_point_key4900); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID343);

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
            // 1078:2: -> VK_ENTRY_POINT[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1081:1: accumulate_key : {...}? =>id= ID -> VK_ACCUMULATE[$id] ;
    public final accumulate_key_return accumulate_key() throws RecognitionException {
        accumulate_key_return retval = new accumulate_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1082:2: ({...}? =>id= ID -> VK_ACCUMULATE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1082:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "accumulate_key", "(validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accumulate_key4925); if (failed) return retval;
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
            // 1082:71: -> VK_ACCUMULATE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1085:1: init_key : {...}? =>id= ID -> VK_INIT[$id] ;
    public final init_key_return init_key() throws RecognitionException {
        init_key_return retval = new init_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1086:2: ({...}? =>id= ID -> VK_INIT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1086:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.INIT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "init_key", "(validateIdentifierKey(DroolsSoftKeywords.INIT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_init_key4947); if (failed) return retval;
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
            // 1086:65: -> VK_INIT[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1089:1: action_key : {...}? =>id= ID -> VK_ACTION[$id] ;
    public final action_key_return action_key() throws RecognitionException {
        action_key_return retval = new action_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1090:2: ({...}? =>id= ID -> VK_ACTION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1090:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ACTION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "action_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_action_key4969); if (failed) return retval;
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
            // 1090:67: -> VK_ACTION[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1093:1: reverse_key : {...}? =>id= ID -> VK_REVERSE[$id] ;
    public final reverse_key_return reverse_key() throws RecognitionException {
        reverse_key_return retval = new reverse_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1094:2: ({...}? =>id= ID -> VK_REVERSE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1094:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.REVERSE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "reverse_key", "(validateIdentifierKey(DroolsSoftKeywords.REVERSE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_reverse_key4991); if (failed) return retval;
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
            // 1094:68: -> VK_REVERSE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1097:1: result_key : {...}? =>id= ID -> VK_RESULT[$id] ;
    public final result_key_return result_key() throws RecognitionException {
        result_key_return retval = new result_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1098:2: ({...}? =>id= ID -> VK_RESULT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1098:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.RESULT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "result_key", "(validateIdentifierKey(DroolsSoftKeywords.RESULT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_result_key5013); if (failed) return retval;
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
            // 1098:67: -> VK_RESULT[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1101:1: collect_key : {...}? =>id= ID -> VK_COLLECT[$id] ;
    public final collect_key_return collect_key() throws RecognitionException {
        collect_key_return retval = new collect_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1102:2: ({...}? =>id= ID -> VK_COLLECT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1102:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.COLLECT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "collect_key", "(validateIdentifierKey(DroolsSoftKeywords.COLLECT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_collect_key5035); if (failed) return retval;
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
            // 1102:68: -> VK_COLLECT[$id]
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
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:570:5: ( LEFT_PAREN or_key )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:570:6: LEFT_PAREN or_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred11852); if (failed) return ;
        pushFollow(FOLLOW_or_key_in_synpred11854);
        or_key();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:5: ( or_key | DOUBLE_PIPE )
        int alt101=2;
        int LA101_0 = input.LA(1);

        if ( (LA101_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
            alt101=1;
        }
        else if ( (LA101_0==DOUBLE_PIPE) ) {
            alt101=2;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("573:5: synpred2 : ( or_key | DOUBLE_PIPE );", 101, 0, input);

            throw nvae;
        }
        switch (alt101) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:6: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred21901);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:13: DOUBLE_PIPE
                {
                match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred21903); if (failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred2

    // $ANTLR start synpred3
    public final void synpred3_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:580:5: ( LEFT_PAREN and_key )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:580:6: LEFT_PAREN and_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred31954); if (failed) return ;
        pushFollow(FOLLOW_and_key_in_synpred31956);
        and_key();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred3

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:5: ( and_key | DOUBLE_AMPER )
        int alt102=2;
        int LA102_0 = input.LA(1);

        if ( (LA102_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.AND)))) {
            alt102=1;
        }
        else if ( (LA102_0==DOUBLE_AMPER) ) {
            alt102=2;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("583:5: synpred4 : ( and_key | DOUBLE_AMPER );", 102, 0, input);

            throw nvae;
        }
        switch (alt102) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:6: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred42003);
                and_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:14: DOUBLE_AMPER
                {
                match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred42005); if (failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:597:4: ( SEMICOLON )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:597:5: SEMICOLON
        {
        match(input,SEMICOLON,FOLLOW_SEMICOLON_in_synpred52115); if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:12: ( LEFT_PAREN ( or_key | and_key ) )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:13: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred62147); if (failed) return ;
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:24: ( or_key | and_key )
        int alt103=2;
        int LA103_0 = input.LA(1);

        if ( (LA103_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            int LA103_1 = input.LA(2);

            if ( ((validateIdentifierKey(DroolsSoftKeywords.OR))) ) {
                alt103=1;
            }
            else if ( ((validateIdentifierKey(DroolsSoftKeywords.AND))) ) {
                alt103=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("602:24: ( or_key | and_key )", 103, 1, input);

                throw nvae;
            }
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("602:24: ( or_key | and_key )", 103, 0, input);

            throw nvae;
        }
        switch (alt103) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:25: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred62150);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:32: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred62152);
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
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:615:5: ( LEFT_PAREN ( or_key | and_key ) )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:615:6: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred72259); if (failed) return ;
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:615:17: ( or_key | and_key )
        int alt104=2;
        int LA104_0 = input.LA(1);

        if ( (LA104_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            int LA104_1 = input.LA(2);

            if ( ((validateIdentifierKey(DroolsSoftKeywords.OR))) ) {
                alt104=1;
            }
            else if ( ((validateIdentifierKey(DroolsSoftKeywords.AND))) ) {
                alt104=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("615:17: ( or_key | and_key )", 104, 1, input);

                throw nvae;
            }
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("615:17: ( or_key | and_key )", 104, 0, input);

            throw nvae;
        }
        switch (alt104) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:615:18: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred72262);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:615:25: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred72264);
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
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:5: ( LEFT_PAREN )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:6: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred82817); if (failed) return ;

        }
    }
    // $ANTLR end synpred8

    // $ANTLR start synpred9
    public final void synpred9_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:6: ( LEFT_SQUARE )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred92878); if (failed) return ;

        }
    }
    // $ANTLR end synpred9

    // $ANTLR start synpred10
    public final void synpred10_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:709:6: ( LEFT_PAREN )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:709:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred102900); if (failed) return ;

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
    protected DFA36 dfa36 = new DFA36(this);
    protected DFA53 dfa53 = new DFA53(this);
    protected DFA54 dfa54 = new DFA54(this);
    protected DFA61 dfa61 = new DFA61(this);
    protected DFA65 dfa65 = new DFA65(this);
    protected DFA82 dfa82 = new DFA82(this);
    protected DFA83 dfa83 = new DFA83(this);
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
            return "383:23: ( parameters )?";
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
            return "402:4: ( data_type )?";
        }
    }
    static final String DFA28_eotS =
        "\40\uffff";
    static final String DFA28_eofS =
        "\40\uffff";
    static final String DFA28_minS =
        "\2\130\4\uffff\1\4\1\130\2\uffff\1\4\1\0\1\uffff\1\0\1\4\1\0\2\4"+
        "\1\0\1\4\1\0\3\4\1\0\1\4\2\0\2\4\2\0";
    static final String DFA28_maxS =
        "\1\164\1\167\4\uffff\1\u0080\1\164\2\uffff\1\u0080\1\0\1\uffff\1"+
        "\0\1\u0080\1\0\2\u0080\1\0\1\u0080\1\0\3\u0080\1\0\1\u0080\2\0\2"+
        "\u0080\2\0";
    static final String DFA28_acceptS =
        "\2\uffff\1\2\3\1\2\uffff\1\2\1\1\2\uffff\1\1\23\uffff";
    static final String DFA28_specialS =
        "\1\10\1\24\4\uffff\1\30\1\23\2\uffff\1\20\1\16\1\uffff\1\11\1\25"+
        "\1\15\1\21\1\7\1\1\1\5\1\17\1\4\1\2\1\22\1\14\1\6\1\27\1\26\1\3"+
        "\1\0\1\13\1\12}>";
    static final String[] DFA28_transitionS = {
            "\1\1\33\uffff\1\2",
            "\1\10\3\uffff\1\11\1\6\3\uffff\1\7\1\uffff\1\4\1\5\17\uffff"+
            "\1\10\2\uffff\1\3",
            "",
            "",
            "",
            "",
            "\124\14\1\12\4\14\1\13\43\14",
            "\1\15\4\uffff\1\10\26\uffff\1\10",
            "",
            "",
            "\124\14\1\17\1\20\3\14\1\22\3\14\1\16\20\14\1\21\16\14",
            "\1\uffff",
            "",
            "\1\uffff",
            "\124\14\1\23\4\14\1\24\43\14",
            "\1\uffff",
            "\124\14\1\25\50\14",
            "\157\14\1\26\15\14",
            "\1\uffff",
            "\125\14\1\27\3\14\1\30\24\14\1\31\16\14",
            "\1\uffff",
            "\125\14\1\20\3\14\1\32\24\14\1\21\16\14",
            "\131\14\1\33\24\14\1\21\16\14",
            "\124\14\1\34\50\14",
            "\1\uffff",
            "\157\14\1\35\15\14",
            "\1\uffff",
            "\1\uffff",
            "\125\14\1\27\3\14\1\36\24\14\1\31\16\14",
            "\131\14\1\37\24\14\1\31\16\14",
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
            return "468:21: ( rule_attributes )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA28_29 = input.LA(1);

                         
                        int index28_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_29==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 31;}

                        else if ( (LA28_29==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 25;}

                        else if ( ((LA28_29>=VT_COMPILATION_UNIT && LA28_29<=STRING)||(LA28_29>=COMMA && LA28_29<=NULL)||(LA28_29>=RIGHT_SQUARE && LA28_29<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_29);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA28_18 = input.LA(1);

                         
                        int index28_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 8;}

                         
                        input.seek(index28_18);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA28_22 = input.LA(1);

                         
                        int index28_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_22==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 27;}

                        else if ( (LA28_22==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                        else if ( ((LA28_22>=VT_COMPILATION_UNIT && LA28_22<=STRING)||(LA28_22>=COMMA && LA28_22<=NULL)||(LA28_22>=RIGHT_SQUARE && LA28_22<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_22);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA28_28 = input.LA(1);

                         
                        int index28_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_28==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 25;}

                        else if ( (LA28_28==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 30;}

                        else if ( (LA28_28==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 23;}

                        else if ( ((LA28_28>=VT_COMPILATION_UNIT && LA28_28<=ID)||(LA28_28>=DOT_STAR && LA28_28<=STRING)||(LA28_28>=COMMA && LA28_28<=NULL)||(LA28_28>=RIGHT_SQUARE && LA28_28<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_28);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA28_21 = input.LA(1);

                         
                        int index28_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA28_21>=VT_COMPILATION_UNIT && LA28_21<=ID)||(LA28_21>=DOT_STAR && LA28_21<=STRING)||(LA28_21>=COMMA && LA28_21<=NULL)||(LA28_21>=RIGHT_SQUARE && LA28_21<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA28_21==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                        else if ( (LA28_21==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 26;}

                        else if ( (LA28_21==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 16;}

                         
                        input.seek(index28_21);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA28_19 = input.LA(1);

                         
                        int index28_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA28_19>=VT_COMPILATION_UNIT && LA28_19<=ID)||(LA28_19>=DOT_STAR && LA28_19<=STRING)||(LA28_19>=COMMA && LA28_19<=NULL)||(LA28_19>=RIGHT_SQUARE && LA28_19<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA28_19==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 23;}

                        else if ( (LA28_19==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 24;}

                        else if ( (LA28_19==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 25;}

                         
                        input.seek(index28_19);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA28_25 = input.LA(1);

                         
                        int index28_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_25==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 29;}

                        else if ( ((LA28_25>=VT_COMPILATION_UNIT && LA28_25<=LEFT_SQUARE)||(LA28_25>=THEN && LA28_25<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_25);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA28_17 = input.LA(1);

                         
                        int index28_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_17==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 22;}

                        else if ( ((LA28_17>=VT_COMPILATION_UNIT && LA28_17<=LEFT_SQUARE)||(LA28_17>=THEN && LA28_17<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_17);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA28_0 = input.LA(1);

                         
                        int index28_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {s = 1;}

                        else if ( (LA28_0==THEN) ) {s = 2;}

                         
                        input.seek(index28_0);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA28_13 = input.LA(1);

                         
                        int index28_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 8;}

                         
                        input.seek(index28_13);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA28_31 = input.LA(1);

                         
                        int index28_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 8;}

                         
                        input.seek(index28_31);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA28_30 = input.LA(1);

                         
                        int index28_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 8;}

                         
                        input.seek(index28_30);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA28_24 = input.LA(1);

                         
                        int index28_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 8;}

                         
                        input.seek(index28_24);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA28_15 = input.LA(1);

                         
                        int index28_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 8;}

                         
                        input.seek(index28_15);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA28_11 = input.LA(1);

                         
                        int index28_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 8;}

                         
                        input.seek(index28_11);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA28_20 = input.LA(1);

                         
                        int index28_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 8;}

                         
                        input.seek(index28_20);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA28_10 = input.LA(1);

                         
                        int index28_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_10==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 14;}

                        else if ( (LA28_10==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 15;}

                        else if ( (LA28_10==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 16;}

                        else if ( (LA28_10==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                        else if ( (LA28_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 18;}

                        else if ( ((LA28_10>=VT_COMPILATION_UNIT && LA28_10<=SEMICOLON)||(LA28_10>=DOT_STAR && LA28_10<=STRING)||(LA28_10>=COMMA && LA28_10<=AT)||(LA28_10>=EQUALS && LA28_10<=NULL)||(LA28_10>=RIGHT_SQUARE && LA28_10<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_10);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA28_16 = input.LA(1);

                         
                        int index28_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_16==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 21;}

                        else if ( ((LA28_16>=VT_COMPILATION_UNIT && LA28_16<=SEMICOLON)||(LA28_16>=DOT && LA28_16<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_16);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA28_23 = input.LA(1);

                         
                        int index28_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_23==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 28;}

                        else if ( ((LA28_23>=VT_COMPILATION_UNIT && LA28_23<=SEMICOLON)||(LA28_23>=DOT && LA28_23<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_23);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA28_7 = input.LA(1);

                         
                        int index28_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_7==LEFT_PAREN||LA28_7==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {s = 8;}

                        else if ( (LA28_7==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {s = 13;}

                         
                        input.seek(index28_7);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA28_1 = input.LA(1);

                         
                        int index28_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {s = 3;}

                        else if ( (LA28_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {s = 4;}

                        else if ( (LA28_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {s = 5;}

                        else if ( (LA28_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 6;}

                        else if ( (LA28_1==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {s = 7;}

                        else if ( (LA28_1==ID||LA28_1==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {s = 8;}

                        else if ( (LA28_1==STRING) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 9;}

                         
                        input.seek(index28_1);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
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
                    case 22 : 
                        int LA28_27 = input.LA(1);

                         
                        int index28_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 8;}

                         
                        input.seek(index28_27);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA28_26 = input.LA(1);

                         
                        int index28_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 8;}

                         
                        input.seek(index28_26);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA28_6 = input.LA(1);

                         
                        int index28_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_6==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 10;}

                        else if ( (LA28_6==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 11;}

                        else if ( ((LA28_6>=VT_COMPILATION_UNIT && LA28_6<=SEMICOLON)||(LA28_6>=DOT && LA28_6<=STRING)||(LA28_6>=COMMA && LA28_6<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index28_6);
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
    static final String DFA36_eotS =
        "\37\uffff";
    static final String DFA36_eofS =
        "\37\uffff";
    static final String DFA36_minS =
        "\2\130\6\uffff\1\4\1\uffff\1\4\1\0\1\uffff\1\4\1\0\2\4\1\0\1\4\1"+
        "\0\4\4\3\0\2\4\2\0";
    static final String DFA36_maxS =
        "\1\164\1\167\6\uffff\1\u0080\1\uffff\1\u0080\1\0\1\uffff\1\u0080"+
        "\1\0\2\u0080\1\0\1\u0080\1\0\4\u0080\3\0\2\u0080\2\0";
    static final String DFA36_acceptS =
        "\2\uffff\1\2\4\1\1\2\1\uffff\1\1\2\uffff\1\1\22\uffff";
    static final String DFA36_specialS =
        "\1\15\1\17\6\uffff\1\26\1\uffff\1\20\1\10\1\uffff\1\23\1\14\1\21"+
        "\1\6\1\1\1\5\1\16\1\4\1\2\1\22\1\7\1\13\1\25\1\24\1\3\1\0\1\12\1"+
        "\11}>";
    static final String[] DFA36_transitionS = {
            "\1\1\5\uffff\1\3\25\uffff\1\2",
            "\1\7\3\uffff\1\5\1\10\3\uffff\1\7\1\uffff\1\6\1\11\17\uffff"+
            "\1\7\2\uffff\1\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "\124\14\1\12\4\14\1\13\43\14",
            "",
            "\124\14\1\16\1\17\3\14\1\21\3\14\1\15\20\14\1\20\16\14",
            "\1\uffff",
            "",
            "\124\14\1\22\4\14\1\23\43\14",
            "\1\uffff",
            "\124\14\1\24\50\14",
            "\157\14\1\25\15\14",
            "\1\uffff",
            "\125\14\1\26\3\14\1\30\24\14\1\27\16\14",
            "\1\uffff",
            "\125\14\1\17\3\14\1\31\24\14\1\20\16\14",
            "\131\14\1\32\24\14\1\20\16\14",
            "\124\14\1\33\50\14",
            "\157\14\1\34\15\14",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\125\14\1\26\3\14\1\35\24\14\1\27\16\14",
            "\131\14\1\36\24\14\1\27\16\14",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA36_eot = DFA.unpackEncodedString(DFA36_eotS);
    static final short[] DFA36_eof = DFA.unpackEncodedString(DFA36_eofS);
    static final char[] DFA36_min = DFA.unpackEncodedStringToUnsignedChars(DFA36_minS);
    static final char[] DFA36_max = DFA.unpackEncodedStringToUnsignedChars(DFA36_maxS);
    static final short[] DFA36_accept = DFA.unpackEncodedString(DFA36_acceptS);
    static final short[] DFA36_special = DFA.unpackEncodedString(DFA36_specialS);
    static final short[][] DFA36_transition;

    static {
        int numStates = DFA36_transitionS.length;
        DFA36_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA36_transition[i] = DFA.unpackEncodedString(DFA36_transitionS[i]);
        }
    }

    class DFA36 extends DFA {

        public DFA36(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 36;
            this.eot = DFA36_eot;
            this.eof = DFA36_eof;
            this.min = DFA36_min;
            this.max = DFA36_max;
            this.accept = DFA36_accept;
            this.special = DFA36_special;
            this.transition = DFA36_transition;
        }
        public String getDescription() {
            return "()* loopback of 487:45: ( ( COMMA )? attr= rule_attribute )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA36_28 = input.LA(1);

                         
                        int index36_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_28==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 30;}

                        else if ( (LA36_28==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 23;}

                        else if ( ((LA36_28>=VT_COMPILATION_UNIT && LA36_28<=STRING)||(LA36_28>=COMMA && LA36_28<=NULL)||(LA36_28>=RIGHT_SQUARE && LA36_28<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_28);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA36_17 = input.LA(1);

                         
                        int index36_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 7;}

                         
                        input.seek(index36_17);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA36_21 = input.LA(1);

                         
                        int index36_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_21==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 26;}

                        else if ( (LA36_21==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 16;}

                        else if ( ((LA36_21>=VT_COMPILATION_UNIT && LA36_21<=STRING)||(LA36_21>=COMMA && LA36_21<=NULL)||(LA36_21>=RIGHT_SQUARE && LA36_21<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_21);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA36_27 = input.LA(1);

                         
                        int index36_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_27==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 23;}

                        else if ( (LA36_27==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 29;}

                        else if ( (LA36_27==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 22;}

                        else if ( ((LA36_27>=VT_COMPILATION_UNIT && LA36_27<=ID)||(LA36_27>=DOT_STAR && LA36_27<=STRING)||(LA36_27>=COMMA && LA36_27<=NULL)||(LA36_27>=RIGHT_SQUARE && LA36_27<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_27);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA36_20 = input.LA(1);

                         
                        int index36_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_20==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 16;}

                        else if ( (LA36_20==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 25;}

                        else if ( (LA36_20==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 15;}

                        else if ( ((LA36_20>=VT_COMPILATION_UNIT && LA36_20<=ID)||(LA36_20>=DOT_STAR && LA36_20<=STRING)||(LA36_20>=COMMA && LA36_20<=NULL)||(LA36_20>=RIGHT_SQUARE && LA36_20<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_20);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA36_18 = input.LA(1);

                         
                        int index36_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_18==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 22;}

                        else if ( (LA36_18==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 23;}

                        else if ( (LA36_18==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 24;}

                        else if ( ((LA36_18>=VT_COMPILATION_UNIT && LA36_18<=ID)||(LA36_18>=DOT_STAR && LA36_18<=STRING)||(LA36_18>=COMMA && LA36_18<=NULL)||(LA36_18>=RIGHT_SQUARE && LA36_18<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_18);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA36_16 = input.LA(1);

                         
                        int index36_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_16==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 21;}

                        else if ( ((LA36_16>=VT_COMPILATION_UNIT && LA36_16<=LEFT_SQUARE)||(LA36_16>=THEN && LA36_16<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_16);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA36_23 = input.LA(1);

                         
                        int index36_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_23==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 28;}

                        else if ( ((LA36_23>=VT_COMPILATION_UNIT && LA36_23<=LEFT_SQUARE)||(LA36_23>=THEN && LA36_23<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_23);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA36_11 = input.LA(1);

                         
                        int index36_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 7;}

                         
                        input.seek(index36_11);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA36_30 = input.LA(1);

                         
                        int index36_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 7;}

                         
                        input.seek(index36_30);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA36_29 = input.LA(1);

                         
                        int index36_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 7;}

                         
                        input.seek(index36_29);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA36_24 = input.LA(1);

                         
                        int index36_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 7;}

                         
                        input.seek(index36_24);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA36_14 = input.LA(1);

                         
                        int index36_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 7;}

                         
                        input.seek(index36_14);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA36_0 = input.LA(1);

                         
                        int index36_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {s = 1;}

                        else if ( (LA36_0==THEN) ) {s = 2;}

                        else if ( (LA36_0==COMMA) ) {s = 3;}

                         
                        input.seek(index36_0);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA36_19 = input.LA(1);

                         
                        int index36_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 7;}

                         
                        input.seek(index36_19);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA36_1 = input.LA(1);

                         
                        int index36_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {s = 4;}

                        else if ( (LA36_1==STRING) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 5;}

                        else if ( (LA36_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {s = 6;}

                        else if ( (LA36_1==ID||LA36_1==COLON||LA36_1==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {s = 7;}

                        else if ( (LA36_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 8;}

                        else if ( (LA36_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {s = 9;}

                         
                        input.seek(index36_1);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA36_10 = input.LA(1);

                         
                        int index36_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_10==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 13;}

                        else if ( (LA36_10==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 14;}

                        else if ( (LA36_10==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 15;}

                        else if ( (LA36_10==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 16;}

                        else if ( (LA36_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                        else if ( ((LA36_10>=VT_COMPILATION_UNIT && LA36_10<=SEMICOLON)||(LA36_10>=DOT_STAR && LA36_10<=STRING)||(LA36_10>=COMMA && LA36_10<=AT)||(LA36_10>=EQUALS && LA36_10<=NULL)||(LA36_10>=RIGHT_SQUARE && LA36_10<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_10);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA36_15 = input.LA(1);

                         
                        int index36_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_15==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 20;}

                        else if ( ((LA36_15>=VT_COMPILATION_UNIT && LA36_15<=SEMICOLON)||(LA36_15>=DOT && LA36_15<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_15);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA36_22 = input.LA(1);

                         
                        int index36_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_22==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 27;}

                        else if ( ((LA36_22>=VT_COMPILATION_UNIT && LA36_22<=SEMICOLON)||(LA36_22>=DOT && LA36_22<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_22);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA36_13 = input.LA(1);

                         
                        int index36_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_13==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 18;}

                        else if ( (LA36_13==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 19;}

                        else if ( ((LA36_13>=VT_COMPILATION_UNIT && LA36_13<=SEMICOLON)||(LA36_13>=DOT && LA36_13<=STRING)||(LA36_13>=COMMA && LA36_13<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_13);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA36_26 = input.LA(1);

                         
                        int index36_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 7;}

                         
                        input.seek(index36_26);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA36_25 = input.LA(1);

                         
                        int index36_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateWhenWithParenthesis()&&(validateIdentifierKey(DroolsSoftKeywords.WHEN))))) ) {s = 7;}

                         
                        input.seek(index36_25);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA36_8 = input.LA(1);

                         
                        int index36_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_8==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 10;}

                        else if ( (LA36_8==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 11;}

                        else if ( ((LA36_8>=VT_COMPILATION_UNIT && LA36_8<=SEMICOLON)||(LA36_8>=DOT && LA36_8<=STRING)||(LA36_8>=COMMA && LA36_8<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index36_8);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 36, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA53_eotS =
        "\172\uffff";
    static final String DFA53_eofS =
        "\172\uffff";
    static final String DFA53_minS =
        "\3\130\2\0\1\130\1\0\1\uffff\1\130\1\163\2\uffff\1\131\1\130\1\uffff"+
        "\1\131\1\135\1\130\1\163\1\130\1\131\1\130\1\131\1\135\1\130\2\0"+
        "\1\130\1\163\2\130\2\0\1\130\1\0\1\4\11\130\2\uffff\1\131\1\135"+
        "\1\130\1\0\2\130\1\4\11\130\1\0\1\uffff\1\130\1\uffff\1\4\26\0\1"+
        "\uffff\17\0\1\uffff\17\0";
    static final String DFA53_maxS =
        "\2\135\1\162\2\0\1\135\1\0\1\uffff\1\130\1\163\2\uffff\1\162\1\130"+
        "\1\uffff\2\162\1\130\1\163\1\137\1\162\1\137\3\162\2\0\1\130\1\163"+
        "\1\137\1\162\2\0\1\130\1\0\1\u0080\1\130\7\161\1\130\2\uffff\3\162"+
        "\1\0\1\145\1\130\1\u0080\1\130\7\161\1\130\1\0\1\uffff\1\162\1\uffff"+
        "\1\u0080\26\0\1\uffff\17\0\1\uffff\17\0";
    static final String DFA53_acceptS =
        "\7\uffff\1\1\2\uffff\2\2\2\uffff\1\3\36\uffff\2\3\21\uffff\1\3\1"+
        "\uffff\1\3\27\uffff\1\3\17\uffff\1\3\17\uffff";
    static final String DFA53_specialS =
        "\2\uffff\1\7\1\0\1\3\1\uffff\1\10\22\uffff\1\2\1\1\4\uffff\1\5\1"+
        "\11\1\uffff\1\4\17\uffff\1\6\14\uffff\1\12\72\uffff}>";
    static final String[] DFA53_transitionS = {
            "\1\2\4\uffff\1\1",
            "\1\3\4\uffff\1\4",
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
            "\1\67\1\66\3\uffff\1\77\3\uffff\1\64\7\uffff\1\70\1\71\1\72"+
            "\1\73\1\74\1\75\1\76\2\uffff\1\65",
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
            "\156\137\1\140\1\141\15\137",
            "\1\142",
            "\1\144\3\uffff\1\145\1\143\5\uffff\2\145\12\uffff\1\146\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\151",
            "\1\uffff",
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

    static final short[] DFA53_eot = DFA.unpackEncodedString(DFA53_eotS);
    static final short[] DFA53_eof = DFA.unpackEncodedString(DFA53_eofS);
    static final char[] DFA53_min = DFA.unpackEncodedStringToUnsignedChars(DFA53_minS);
    static final char[] DFA53_max = DFA.unpackEncodedStringToUnsignedChars(DFA53_maxS);
    static final short[] DFA53_accept = DFA.unpackEncodedString(DFA53_acceptS);
    static final short[] DFA53_special = DFA.unpackEncodedString(DFA53_specialS);
    static final short[][] DFA53_transition;

    static {
        int numStates = DFA53_transitionS.length;
        DFA53_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA53_transition[i] = DFA.unpackEncodedString(DFA53_transitionS[i]);
        }
    }

    class DFA53 extends DFA {

        public DFA53(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 53;
            this.eot = DFA53_eot;
            this.eof = DFA53_eof;
            this.min = DFA53_min;
            this.max = DFA53_max;
            this.accept = DFA53_accept;
            this.special = DFA53_special;
            this.transition = DFA53_transition;
        }
        public String getDescription() {
            return "602:10: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA53_3 = input.LA(1);

                         
                        int index53_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 10;}

                         
                        input.seek(index53_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA53_26 = input.LA(1);

                         
                        int index53_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index53_26);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA53_25 = input.LA(1);

                         
                        int index53_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 45;}

                         
                        input.seek(index53_25);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA53_4 = input.LA(1);

                         
                        int index53_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index53_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA53_34 = input.LA(1);

                         
                        int index53_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 66;}

                         
                        input.seek(index53_34);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA53_31 = input.LA(1);

                         
                        int index53_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 64;}

                         
                        input.seek(index53_31);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA53_50 = input.LA(1);

                         
                        int index53_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 90;}

                         
                        input.seek(index53_50);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA53_2 = input.LA(1);

                         
                        int index53_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA53_2==COLON) ) {s = 5;}

                        else if ( (LA53_2==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA53_2==ID) && (((synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||((synpred6()&&validateNotWithBinding())&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {s = 7;}

                        else if ( (LA53_2==DOT) ) {s = 8;}

                        else if ( (LA53_2==LEFT_SQUARE) ) {s = 9;}

                         
                        input.seek(index53_2);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA53_6 = input.LA(1);

                         
                        int index53_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EVAL)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||synpred6()||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.FORALL))))) ) {s = 7;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index53_6);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA53_32 = input.LA(1);

                         
                        int index53_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index53_32);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA53_63 = input.LA(1);

                         
                        int index53_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 106;}

                         
                        input.seek(index53_63);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 53, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA54_eotS =
        "\172\uffff";
    static final String DFA54_eofS =
        "\172\uffff";
    static final String DFA54_minS =
        "\3\130\2\0\1\130\1\0\1\uffff\1\130\1\163\2\uffff\1\131\1\130\1\uffff"+
        "\1\131\1\135\1\130\1\163\1\130\1\131\1\130\1\131\1\135\1\130\2\0"+
        "\1\130\1\163\2\130\2\0\1\130\1\0\1\4\11\130\2\uffff\1\131\1\135"+
        "\1\130\1\0\2\130\1\4\11\130\1\0\1\uffff\1\130\1\uffff\1\4\26\0\1"+
        "\uffff\17\0\1\uffff\17\0";
    static final String DFA54_maxS =
        "\2\135\1\162\2\0\1\135\1\0\1\uffff\1\130\1\163\2\uffff\1\162\1\130"+
        "\1\uffff\2\162\1\130\1\163\1\137\1\162\1\137\3\162\2\0\1\130\1\163"+
        "\1\137\1\162\2\0\1\130\1\0\1\u0080\1\130\7\161\1\130\2\uffff\3\162"+
        "\1\0\1\145\1\130\1\u0080\1\130\7\161\1\130\1\0\1\uffff\1\162\1\uffff"+
        "\1\u0080\26\0\1\uffff\17\0\1\uffff\17\0";
    static final String DFA54_acceptS =
        "\7\uffff\1\1\2\uffff\2\2\2\uffff\1\3\36\uffff\2\3\21\uffff\1\3\1"+
        "\uffff\1\3\27\uffff\1\3\17\uffff\1\3\17\uffff";
    static final String DFA54_specialS =
        "\2\uffff\1\10\1\11\1\1\1\uffff\1\2\22\uffff\1\4\1\0\4\uffff\1\6"+
        "\1\7\1\uffff\1\3\17\uffff\1\5\14\uffff\1\12\72\uffff}>";
    static final String[] DFA54_transitionS = {
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
            "\1\67\1\66\3\uffff\1\77\3\uffff\1\64\7\uffff\1\70\1\71\1\72"+
            "\1\73\1\74\1\75\1\76\2\uffff\1\65",
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
            "\156\137\1\140\1\141\15\137",
            "\1\142",
            "\1\144\3\uffff\1\145\1\143\5\uffff\2\145\12\uffff\1\146\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\147\3\uffff\1\145\1\150\5\uffff\2\145\13\uffff\2\145",
            "\1\151",
            "\1\uffff",
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

    static final short[] DFA54_eot = DFA.unpackEncodedString(DFA54_eotS);
    static final short[] DFA54_eof = DFA.unpackEncodedString(DFA54_eofS);
    static final char[] DFA54_min = DFA.unpackEncodedStringToUnsignedChars(DFA54_minS);
    static final char[] DFA54_max = DFA.unpackEncodedStringToUnsignedChars(DFA54_maxS);
    static final short[] DFA54_accept = DFA.unpackEncodedString(DFA54_acceptS);
    static final short[] DFA54_special = DFA.unpackEncodedString(DFA54_specialS);
    static final short[][] DFA54_transition;

    static {
        int numStates = DFA54_transitionS.length;
        DFA54_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA54_transition[i] = DFA.unpackEncodedString(DFA54_transitionS[i]);
        }
    }

    class DFA54 extends DFA {

        public DFA54(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 54;
            this.eot = DFA54_eot;
            this.eof = DFA54_eof;
            this.min = DFA54_min;
            this.max = DFA54_max;
            this.accept = DFA54_accept;
            this.special = DFA54_special;
            this.transition = DFA54_transition;
        }
        public String getDescription() {
            return "615:3: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA54_26 = input.LA(1);

                         
                        int index54_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index54_26);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA54_4 = input.LA(1);

                         
                        int index54_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index54_4);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA54_6 = input.LA(1);

                         
                        int index54_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.FORALL)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||synpred7()||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EVAL))))) ) {s = 7;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index54_6);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA54_34 = input.LA(1);

                         
                        int index54_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 66;}

                         
                        input.seek(index54_34);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA54_25 = input.LA(1);

                         
                        int index54_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 45;}

                         
                        input.seek(index54_25);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA54_50 = input.LA(1);

                         
                        int index54_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 90;}

                         
                        input.seek(index54_50);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA54_31 = input.LA(1);

                         
                        int index54_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 64;}

                         
                        input.seek(index54_31);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA54_32 = input.LA(1);

                         
                        int index54_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index54_32);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA54_2 = input.LA(1);

                         
                        int index54_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_2==COLON) ) {s = 5;}

                        else if ( (LA54_2==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA54_2==ID) && (((synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||((synpred7()&&validateNotWithBinding())&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))))) {s = 7;}

                        else if ( (LA54_2==DOT) ) {s = 8;}

                        else if ( (LA54_2==LEFT_SQUARE) ) {s = 9;}

                         
                        input.seek(index54_2);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA54_3 = input.LA(1);

                         
                        int index54_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 10;}

                         
                        input.seek(index54_3);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA54_63 = input.LA(1);

                         
                        int index54_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 106;}

                         
                        input.seek(index54_63);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 54, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA61_eotS =
        "\13\uffff";
    static final String DFA61_eofS =
        "\13\uffff";
    static final String DFA61_minS =
        "\1\130\1\135\2\4\1\0\1\130\1\0\4\uffff";
    static final String DFA61_maxS =
        "\1\130\1\135\2\u0080\1\0\1\137\1\0\4\uffff";
    static final String DFA61_acceptS =
        "\7\uffff\1\1\2\2\1\1";
    static final String DFA61_specialS =
        "\4\uffff\1\2\1\0\1\1\4\uffff}>";
    static final String[] DFA61_transitionS = {
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

    static final short[] DFA61_eot = DFA.unpackEncodedString(DFA61_eotS);
    static final short[] DFA61_eof = DFA.unpackEncodedString(DFA61_eofS);
    static final char[] DFA61_min = DFA.unpackEncodedStringToUnsignedChars(DFA61_minS);
    static final char[] DFA61_max = DFA.unpackEncodedStringToUnsignedChars(DFA61_maxS);
    static final short[] DFA61_accept = DFA.unpackEncodedString(DFA61_acceptS);
    static final short[] DFA61_special = DFA.unpackEncodedString(DFA61_specialS);
    static final short[][] DFA61_transition;

    static {
        int numStates = DFA61_transitionS.length;
        DFA61_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA61_transition[i] = DFA.unpackEncodedString(DFA61_transitionS[i]);
        }
    }

    class DFA61 extends DFA {

        public DFA61(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 61;
            this.eot = DFA61_eot;
            this.eof = DFA61_eof;
            this.min = DFA61_min;
            this.max = DFA61_max;
            this.accept = DFA61_accept;
            this.special = DFA61_special;
            this.transition = DFA61_transition;
        }
        public String getDescription() {
            return "659:3: ( accumulate_init_clause | accumulate_id_clause )";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA61_5 = input.LA(1);

                         
                        int index61_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA61_5==RIGHT_PAREN) ) {s = 9;}

                        else if ( (LA61_5==ID||LA61_5==COMMA) && ((validateIdentifierKey(DroolsSoftKeywords.INIT)))) {s = 10;}

                         
                        input.seek(index61_5);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA61_6 = input.LA(1);

                         
                        int index61_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.INIT))) ) {s = 10;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index61_6);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA61_4 = input.LA(1);

                         
                        int index61_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.INIT))) ) {s = 7;}

                        else if ( (true) ) {s = 8;}

                         
                        input.seek(index61_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 61, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA65_eotS =
        "\13\uffff";
    static final String DFA65_eofS =
        "\13\uffff";
    static final String DFA65_minS =
        "\1\130\1\135\2\4\1\0\1\130\1\0\4\uffff";
    static final String DFA65_maxS =
        "\1\130\1\135\2\u0080\1\0\1\137\1\0\4\uffff";
    static final String DFA65_acceptS =
        "\7\uffff\1\1\2\2\1\1";
    static final String DFA65_specialS =
        "\1\5\1\4\1\2\1\6\1\0\1\1\1\3\4\uffff}>";
    static final String[] DFA65_transitionS = {
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
            return "670:2: ( reverse_key pc3= paren_chunk ( COMMA )? )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA65_4 = input.LA(1);

                         
                        int index65_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.REVERSE))) ) {s = 7;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RESULT))) ) {s = 8;}

                         
                        input.seek(index65_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA65_5 = input.LA(1);

                         
                        int index65_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_5==RIGHT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.RESULT)))) {s = 9;}

                        else if ( (LA65_5==ID||LA65_5==COMMA) && ((validateIdentifierKey(DroolsSoftKeywords.REVERSE)))) {s = 10;}

                         
                        input.seek(index65_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA65_2 = input.LA(1);

                         
                        int index65_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA65_2>=VT_COMPILATION_UNIT && LA65_2<=STRING)||LA65_2==COMMA||(LA65_2>=AT && LA65_2<=MULTI_LINE_COMMENT)) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 3;}

                        else if ( (LA65_2==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 4;}

                        else if ( (LA65_2==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 5;}

                         
                        input.seek(index65_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA65_6 = input.LA(1);

                         
                        int index65_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.REVERSE))) ) {s = 10;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RESULT))) ) {s = 9;}

                         
                        input.seek(index65_6);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA65_1 = input.LA(1);

                         
                        int index65_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 2;}

                         
                        input.seek(index65_1);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA65_0 = input.LA(1);

                         
                        int index65_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 1;}

                         
                        input.seek(index65_0);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA65_3 = input.LA(1);

                         
                        int index65_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_3==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 5;}

                        else if ( ((LA65_3>=VT_COMPILATION_UNIT && LA65_3<=STRING)||LA65_3==COMMA||(LA65_3>=AT && LA65_3<=MULTI_LINE_COMMENT)) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 3;}

                        else if ( (LA65_3==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 6;}

                         
                        input.seek(index65_3);
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
    static final String DFA82_eotS =
        "\30\uffff";
    static final String DFA82_eofS =
        "\30\uffff";
    static final String DFA82_minS =
        "\1\136\1\uffff\2\130\1\uffff\2\0\2\130\2\0\1\4\1\162\2\4\11\0";
    static final String DFA82_maxS =
        "\1\146\1\uffff\1\157\1\162\1\uffff\2\0\1\162\1\130\2\0\1\u0080\1"+
        "\162\2\u0080\11\0";
    static final String DFA82_acceptS =
        "\1\uffff\1\2\2\uffff\1\1\23\uffff";
    static final String DFA82_specialS =
        "\2\uffff\1\5\1\6\1\uffff\1\3\1\4\1\0\1\uffff\1\1\1\2\15\uffff}>";
    static final String[] DFA82_transitionS = {
            "\2\1\5\uffff\1\2\1\1",
            "",
            "\1\3\4\uffff\1\5\13\uffff\7\4",
            "\1\7\1\1\2\uffff\1\4\1\6\3\uffff\1\1\1\uffff\2\4\4\uffff\6\1"+
            "\1\10\2\4\1\1",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\11\1\4\2\uffff\1\12\1\13\2\4\3\uffff\2\12\2\4\10\uffff\1"+
            "\1\2\12\1\4",
            "\1\14",
            "\1\uffff",
            "\1\uffff",
            "\124\20\1\15\3\20\1\16\1\17\1\20\1\21\3\20\2\16\13\20\2\16\17"+
            "\20",
            "\1\22",
            "\125\20\1\26\3\20\1\25\1\27\1\23\22\20\1\24\16\20",
            "\131\20\1\25\1\27\1\23\41\20",
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

    static final short[] DFA82_eot = DFA.unpackEncodedString(DFA82_eotS);
    static final short[] DFA82_eof = DFA.unpackEncodedString(DFA82_eofS);
    static final char[] DFA82_min = DFA.unpackEncodedStringToUnsignedChars(DFA82_minS);
    static final char[] DFA82_max = DFA.unpackEncodedStringToUnsignedChars(DFA82_maxS);
    static final short[] DFA82_accept = DFA.unpackEncodedString(DFA82_acceptS);
    static final short[] DFA82_special = DFA.unpackEncodedString(DFA82_specialS);
    static final short[][] DFA82_transition;

    static {
        int numStates = DFA82_transitionS.length;
        DFA82_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA82_transition[i] = DFA.unpackEncodedString(DFA82_transitionS[i]);
        }
    }

    class DFA82 extends DFA {

        public DFA82(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 82;
            this.eot = DFA82_eot;
            this.eof = DFA82_eof;
            this.min = DFA82_min;
            this.max = DFA82_max;
            this.accept = DFA82_accept;
            this.special = DFA82_special;
            this.transition = DFA82_transition;
        }
        public String getDescription() {
            return "()* loopback of 779:25: ({...}? => DOUBLE_PIPE and_restr_connective )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA82_7 = input.LA(1);

                         
                        int index82_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA82_7==ID) ) {s = 9;}

                        else if ( (LA82_7==STRING||(LA82_7>=BOOL && LA82_7<=INT)||(LA82_7>=FLOAT && LA82_7<=NULL)) ) {s = 10;}

                        else if ( (LA82_7==LEFT_PAREN) ) {s = 11;}

                        else if ( (LA82_7==GRAVE_ACCENT) ) {s = 1;}

                        else if ( (LA82_7==DOT||(LA82_7>=COMMA && LA82_7<=RIGHT_PAREN)||(LA82_7>=DOUBLE_PIPE && LA82_7<=DOUBLE_AMPER)||LA82_7==LEFT_SQUARE) && ((validateRestr()))) {s = 4;}

                         
                        input.seek(index82_7);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA82_9 = input.LA(1);

                         
                        int index82_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index82_9);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA82_10 = input.LA(1);

                         
                        int index82_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index82_10);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA82_5 = input.LA(1);

                         
                        int index82_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index82_5);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA82_6 = input.LA(1);

                         
                        int index82_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index82_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA82_2 = input.LA(1);

                         
                        int index82_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA82_2==ID) ) {s = 3;}

                        else if ( ((LA82_2>=EQUAL && LA82_2<=GRAVE_ACCENT)) && ((validateRestr()))) {s = 4;}

                        else if ( (LA82_2==LEFT_PAREN) ) {s = 5;}

                         
                        input.seek(index82_2);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA82_3 = input.LA(1);

                         
                        int index82_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA82_3==DOT||LA82_3==COLON||(LA82_3>=EQUAL && LA82_3<=NOT_EQUAL)||LA82_3==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA82_3==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA82_3==ID) ) {s = 7;}

                        else if ( (LA82_3==STRING||(LA82_3>=BOOL && LA82_3<=INT)||(LA82_3>=FLOAT && LA82_3<=NULL)) && ((validateRestr()))) {s = 4;}

                        else if ( (LA82_3==GRAVE_ACCENT) ) {s = 8;}

                         
                        input.seek(index82_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 82, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA83_eotS =
        "\57\uffff";
    static final String DFA83_eofS =
        "\57\uffff";
    static final String DFA83_minS =
        "\1\136\1\uffff\2\130\1\uffff\3\130\1\4\1\130\1\0\1\4\2\0\1\162\1"+
        "\4\2\0\3\4\32\0";
    static final String DFA83_maxS =
        "\1\146\1\uffff\1\157\1\162\1\uffff\1\157\1\162\1\130\1\u0080\1\162"+
        "\1\0\1\u0080\2\0\1\162\1\u0080\2\0\3\u0080\32\0";
    static final String DFA83_acceptS =
        "\1\uffff\1\2\2\uffff\1\1\52\uffff";
    static final String DFA83_specialS =
        "\2\uffff\1\2\1\4\1\uffff\1\6\1\7\2\uffff\1\3\1\5\1\uffff\1\0\1\1"+
        "\2\uffff\1\10\1\11\35\uffff}>";
    static final String[] DFA83_transitionS = {
            "\2\1\5\uffff\1\1\1\2",
            "",
            "\1\3\4\uffff\1\5\13\uffff\7\4",
            "\1\6\1\1\2\uffff\1\4\1\10\3\uffff\1\1\1\uffff\2\4\4\uffff\6"+
            "\1\1\7\2\4\1\1",
            "",
            "\1\11\4\uffff\1\12\13\uffff\7\4",
            "\1\14\1\4\2\uffff\1\15\1\13\2\4\3\uffff\2\15\2\4\10\uffff\1"+
            "\1\2\15\1\4",
            "\1\16",
            "\124\32\1\17\3\32\1\22\1\20\1\32\1\21\3\32\2\22\4\32\1\23\1"+
            "\24\1\25\1\26\1\27\1\30\1\31\2\22\17\32",
            "\1\33\1\1\2\uffff\1\4\1\35\3\uffff\1\1\1\uffff\2\4\4\uffff\6"+
            "\1\1\34\2\4\1\1",
            "\1\uffff",
            "\124\42\1\36\3\42\1\41\1\37\1\42\1\40\3\42\2\41\13\42\2\41\17"+
            "\42",
            "\1\uffff",
            "\1\uffff",
            "\1\43",
            "\124\32\1\50\1\45\2\32\1\52\1\51\1\46\1\47\3\32\2\52\12\32\1"+
            "\53\2\52\1\44\16\32",
            "\1\uffff",
            "\1\uffff",
            "\131\32\1\54\1\46\1\47\41\32",
            "\124\32\1\55\3\32\1\52\1\56\1\32\1\21\3\32\2\52\13\32\2\52\17"+
            "\32",
            "\124\32\1\55\3\32\1\52\1\56\1\32\1\21\3\32\2\52\13\32\2\52\17"+
            "\32",
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

    static final short[] DFA83_eot = DFA.unpackEncodedString(DFA83_eotS);
    static final short[] DFA83_eof = DFA.unpackEncodedString(DFA83_eofS);
    static final char[] DFA83_min = DFA.unpackEncodedStringToUnsignedChars(DFA83_minS);
    static final char[] DFA83_max = DFA.unpackEncodedStringToUnsignedChars(DFA83_maxS);
    static final short[] DFA83_accept = DFA.unpackEncodedString(DFA83_acceptS);
    static final short[] DFA83_special = DFA.unpackEncodedString(DFA83_specialS);
    static final short[][] DFA83_transition;

    static {
        int numStates = DFA83_transitionS.length;
        DFA83_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA83_transition[i] = DFA.unpackEncodedString(DFA83_transitionS[i]);
        }
    }

    class DFA83 extends DFA {

        public DFA83(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 83;
            this.eot = DFA83_eot;
            this.eof = DFA83_eof;
            this.min = DFA83_min;
            this.max = DFA83_max;
            this.accept = DFA83_accept;
            this.special = DFA83_special;
            this.transition = DFA83_transition;
        }
        public String getDescription() {
            return "()* loopback of 783:26: ({...}? => DOUBLE_AMPER constraint_expression )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA83_12 = input.LA(1);

                         
                        int index83_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index83_12);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA83_13 = input.LA(1);

                         
                        int index83_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index83_13);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA83_2 = input.LA(1);

                         
                        int index83_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA83_2==ID) ) {s = 3;}

                        else if ( ((LA83_2>=EQUAL && LA83_2<=GRAVE_ACCENT)) && ((validateRestr()))) {s = 4;}

                        else if ( (LA83_2==LEFT_PAREN) ) {s = 5;}

                         
                        input.seek(index83_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA83_9 = input.LA(1);

                         
                        int index83_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA83_9==DOT||LA83_9==COLON||(LA83_9>=EQUAL && LA83_9<=NOT_EQUAL)||LA83_9==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA83_9==ID) ) {s = 27;}

                        else if ( (LA83_9==GRAVE_ACCENT) ) {s = 28;}

                        else if ( (LA83_9==LEFT_PAREN) ) {s = 29;}

                        else if ( (LA83_9==STRING||(LA83_9>=BOOL && LA83_9<=INT)||(LA83_9>=FLOAT && LA83_9<=NULL)) && ((validateRestr()))) {s = 4;}

                         
                        input.seek(index83_9);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA83_3 = input.LA(1);

                         
                        int index83_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA83_3==DOT||LA83_3==COLON||(LA83_3>=EQUAL && LA83_3<=NOT_EQUAL)||LA83_3==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA83_3==ID) ) {s = 6;}

                        else if ( (LA83_3==GRAVE_ACCENT) ) {s = 7;}

                        else if ( (LA83_3==LEFT_PAREN) ) {s = 8;}

                        else if ( (LA83_3==STRING||(LA83_3>=BOOL && LA83_3<=INT)||(LA83_3>=FLOAT && LA83_3<=NULL)) && ((validateRestr()))) {s = 4;}

                         
                        input.seek(index83_3);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA83_10 = input.LA(1);

                         
                        int index83_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index83_10);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA83_5 = input.LA(1);

                         
                        int index83_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA83_5==ID) ) {s = 9;}

                        else if ( (LA83_5==LEFT_PAREN) ) {s = 10;}

                        else if ( ((LA83_5>=EQUAL && LA83_5<=GRAVE_ACCENT)) && ((validateRestr()))) {s = 4;}

                         
                        input.seek(index83_5);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA83_6 = input.LA(1);

                         
                        int index83_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA83_6==LEFT_PAREN) ) {s = 11;}

                        else if ( (LA83_6==ID) ) {s = 12;}

                        else if ( (LA83_6==STRING||(LA83_6>=BOOL && LA83_6<=INT)||(LA83_6>=FLOAT && LA83_6<=NULL)) ) {s = 13;}

                        else if ( (LA83_6==GRAVE_ACCENT) ) {s = 1;}

                        else if ( (LA83_6==DOT||(LA83_6>=COMMA && LA83_6<=RIGHT_PAREN)||(LA83_6>=DOUBLE_PIPE && LA83_6<=DOUBLE_AMPER)||LA83_6==LEFT_SQUARE) && ((validateRestr()))) {s = 4;}

                         
                        input.seek(index83_6);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA83_16 = input.LA(1);

                         
                        int index83_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index83_16);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA83_17 = input.LA(1);

                         
                        int index83_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index83_17);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 83, _s, input);
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
    public static final BitSet FOLLOW_when_key_in_when_part1396 = new BitSet(new long[]{0x0000000000000002L,0x0000000221000000L});
    public static final BitSet FOLLOW_COLON_in_when_part1398 = new BitSet(new long[]{0x0000000000000002L,0x0000000021000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_when_part1401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_when_key_in_when_part1413 = new BitSet(new long[]{0x0000000000000002L,0x0000000221000000L});
    public static final BitSet FOLLOW_COLON_in_when_part1415 = new BitSet(new long[]{0x0000000000000002L,0x0000000021000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_when_part1418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_id1439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_rule_id1455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attributes_key_in_rule_attributes1476 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_rule_attributes1478 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1483 = new BitSet(new long[]{0x0000000000000002L,0x0000000041000000L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1487 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1492 = new BitSet(new long[]{0x0000000000000002L,0x0000000041000000L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_key_in_date_effective1611 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_key_in_date_expires1625 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_key_in_enabled1642 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_salience_key_in_salience1657 = new BitSet(new long[]{0x0000000000000000L,0x0000001020000000L});
    public static final BitSet FOLLOW_INT_in_salience1664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_key_in_no_loop1689 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_key_in_auto_focus1704 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_key_in_activation_group1721 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_key_in_ruleflow_group1735 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_key_in_agenda_group1749 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_key_in_duration1763 = new BitSet(new long[]{0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_INT_in_duration1766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_key_in_dialect1780 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_STRING_in_dialect1783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_key_in_lock_on_active1801 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1816 = new BitSet(new long[]{0x0000000000000002L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or1858 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or1862 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1864 = new BitSet(new long[]{0x0000000000000000L,0x00000000A1000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or1867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1888 = new BitSet(new long[]{0x0000000000000002L,0x0000002001000000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or1910 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_lhs_or1917 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1922 = new BitSet(new long[]{0x0000000000000002L,0x0000002001000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and1960 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and1964 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1966 = new BitSet(new long[]{0x0000000000000000L,0x00000000A1000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and1969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1990 = new BitSet(new long[]{0x0000000000000002L,0x0000004001000000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and2012 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_lhs_and2019 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2024 = new BitSet(new long[]{0x0000000000000002L,0x0000004001000000L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2062 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_lhs_not_binding_in_lhs_unary2070 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2076 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2082 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary2088 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2094 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary2097 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2099 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_unary2105 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_lhs_unary2119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_key_in_lhs_exist2133 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist2164 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2166 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist2168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist2181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not_binding2227 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_not_binding2229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not2252 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2276 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2278 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_key_in_lhs_eval2327 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forall_key_in_lhs_forall2355 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2357 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2359 = new BitSet(new long[]{0x0000000000000000L,0x0000000081000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall2362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_pattern_source2394 = new BitSet(new long[]{0x0000000000000002L,0x0000008001000000L});
    public static final BitSet FOLLOW_over_clause_in_pattern_source2398 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_from_key_in_pattern_source2408 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_accumulate_statement_in_pattern_source2424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_pattern_source2440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entrypoint_statement_in_pattern_source2457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_pattern_source2473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause2501 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2504 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_COMMA_in_over_clause2507 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2510 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_ID_in_over_elements2523 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_over_elements2525 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_over_elements2527 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_over_elements2529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_key_in_accumulate_statement2553 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2557 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_lhs_or_in_accumulate_statement2559 = new BitSet(new long[]{0x0000000000000000L,0x0000000041000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2561 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_statement2569 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_statement2575 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_init_key_in_accumulate_init_clause2612 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2617 = new BitSet(new long[]{0x0000000000000000L,0x0000000041000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2619 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_action_key_in_accumulate_init_clause2623 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2627 = new BitSet(new long[]{0x0000000000000000L,0x0000000041000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2629 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_reverse_key_in_accumulate_init_clause2635 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2639 = new BitSet(new long[]{0x0000000000000000L,0x0000000041000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2641 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_result_key_in_accumulate_init_clause2647 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause2700 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_id_clause2704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_key_in_collect_statement2726 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement2730 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_pattern_source_in_collect_statement2732 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement2734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entry_point_key_in_entrypoint_statement2756 = new BitSet(new long[]{0x0000000000000000L,0x0000000011000000L});
    public static final BitSet FOLLOW_entrypoint_id_in_entrypoint_statement2758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entrypoint_id2781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_entrypoint_id2794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source2810 = new BitSet(new long[]{0x0000000000000002L,0x0000000022000000L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source2823 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_expression_chain_in_from_source2830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain2862 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_expression_chain2864 = new BitSet(new long[]{0x0000000000000002L,0x0004000022000000L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain2884 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain2906 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain2917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern2950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern2963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_fact_binding2983 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_fact_in_fact_binding2989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding2996 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_fact_binding_expression_in_fact_binding2998 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding3000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression3039 = new BitSet(new long[]{0x0000000000000002L,0x0000002001000000L});
    public static final BitSet FOLLOW_or_key_in_fact_binding_expression3051 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3057 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression3062 = new BitSet(new long[]{0x0000000000000002L,0x0000002001000000L});
    public static final BitSet FOLLOW_pattern_type_in_fact3102 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact3104 = new BitSet(new long[]{0x0000000000000000L,0x00000000A1000000L});
    public static final BitSet FOLLOW_constraints_in_fact3106 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact3109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints3134 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_COMMA_in_constraints3138 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_constraint_in_constraints3141 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_or_constr_in_constraint3155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3166 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr3170 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3173 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3188 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr3192 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3195 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_eval_key_in_unary_constr3216 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_unary_constr3219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr3224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr3229 = new BitSet(new long[]{0x0000000000000000L,0x0000000021000000L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr3232 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr3234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_field_constraint3248 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3250 = new BitSet(new long[]{0x0000000000000002L,0x0000FF0021000000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_field_constraint3260 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_paren_chunk_in_field_constraint3262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3316 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0021000000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_label3342 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_label3344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3360 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective3366 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0021000000L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3369 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3384 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective3390 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0021000000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3393 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression3415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression3420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression3425 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0021000000L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression3428 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression3430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUAL_in_simple_operator3449 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_GREATER_in_simple_operator3455 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_simple_operator3461 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_LESS_in_simple_operator3467 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_simple_operator3473 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_simple_operator3479 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_not_key_in_simple_operator3485 = new BitSet(new long[]{0x0000000000000000L,0x0000800001000000L});
    public static final BitSet FOLLOW_contains_key_in_simple_operator3488 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_soundslike_key_in_simple_operator3491 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_matches_key_in_simple_operator3494 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_memberof_key_in_simple_operator3497 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3501 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_GRAVE_ACCENT_in_simple_operator3506 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3509 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator3512 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_contains_key_in_simple_operator3518 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_excludes_key_in_simple_operator3524 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_matches_key_in_simple_operator3530 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_soundslike_key_in_simple_operator3536 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_memberof_key_in_simple_operator3542 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3548 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_GRAVE_ACCENT_in_simple_operator3554 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3557 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator3560 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator3564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_compound_operator3579 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_not_key_in_compound_operator3584 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_in_key_in_compound_operator3586 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator3591 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3594 = new BitSet(new long[]{0x0000000000000000L,0x00000000C0000000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator3598 = new BitSet(new long[]{0x0000000000000000L,0x0003001831000000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3601 = new BitSet(new long[]{0x0000000000000000L,0x00000000C0000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator3606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_expression_value3617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value3622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_value3628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_literal_constraint0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pattern_type3672 = new BitSet(new long[]{0x0000000000000002L,0x0004000002000000L});
    public static final BitSet FOLLOW_DOT_in_pattern_type3678 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_pattern_type3682 = new BitSet(new long[]{0x0000000000000002L,0x0004000002000000L});
    public static final BitSet FOLLOW_dimension_definition_in_pattern_type3697 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_ID_in_data_type3723 = new BitSet(new long[]{0x0000000000000002L,0x0004000002000000L});
    public static final BitSet FOLLOW_DOT_in_data_type3727 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_data_type3729 = new BitSet(new long[]{0x0000000000000002L,0x0004000002000000L});
    public static final BitSet FOLLOW_dimension_definition_in_data_type3734 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dimension_definition3760 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dimension_definition3762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path3773 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_DOT_in_accessor_path3777 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path3779 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_accessor_element3803 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_square_chunk_in_accessor_element3805 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_data_in_rhs_chunk3834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk_data3853 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_rhs_chunk_data3857 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_END_in_rhs_chunk_data3863 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_SEMICOLON_in_rhs_chunk_data3865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk3882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk_data3901 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_curly_chunk_data3904 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk_data3918 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk_data3923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk3939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk_data3959 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_paren_chunk_data3962 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk_data3976 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk_data3981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk3998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk_data4017 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_square_chunk_data4020 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk_data4034 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk_data4039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_effective_key4058 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_effective_key4060 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_date_effective_key4062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_expires_key4088 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_expires_key4090 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_date_expires_key4092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4118 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key4120 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4122 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key4124 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_no_loop_key4152 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_no_loop_key4154 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_no_loop_key4156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key4182 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_auto_focus_key4184 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key4186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_activation_group_key4212 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_activation_group_key4214 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_activation_group_key4216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key4242 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_agenda_group_key4244 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key4246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key4272 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_ruleflow_group_key4274 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key4276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_duration_key4301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_key4323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_key4345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dialect_key4367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_salience_key4389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enabled_key4411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_attributes_key4433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_when_key4455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_key4477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_key4499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_key4521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_declare_key4543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_key4565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_key4587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_eval_key4609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_contains_key4631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_matches_key4653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_excludes_key4675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_soundslike_key4697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_memberof_key4719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key4741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key4763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_or_key4785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_and_key4807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_exists_key4829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_forall_key4851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_key4873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entry_point_key4896 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_entry_point_key4898 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_entry_point_key4900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_accumulate_key4925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_init_key4947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_action_key4969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_reverse_key4991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_result_key5013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_collect_key5035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred11852 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_or_key_in_synpred11854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_key_in_synpred21901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred21903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred31954 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_and_key_in_synpred31956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred42003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred42005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_synpred52115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred62147 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_or_key_in_synpred62150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred62152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred72259 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_or_key_in_synpred72262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred72264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred82817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred92878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred102900 = new BitSet(new long[]{0x0000000000000002L});

}