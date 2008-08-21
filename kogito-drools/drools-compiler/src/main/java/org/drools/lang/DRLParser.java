// $ANTLR 3.0.1 /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2008-08-21 09:26:02

	package org.drools.lang;
	
	import java.util.List;
	import java.util.LinkedList;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TEMPLATE_ID", "VT_TYPE_DECLARE_ID", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_SLOT_ID", "VT_SLOT", "VT_RULE_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_DURATION", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_RULE", "VK_IMPORT", "VK_PACKAGE", "VK_TEMPLATE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_CONTAINS", "VK_MATCHES", "VK_EXCLUDES", "VK_SOUNDSLIKE", "VK_MEMBEROF", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_ACTION", "VK_REVERSE", "SEMICOLON", "ID", "DOT", "DOT_STAR", "END", "STRING", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "AT", "COLON", "EQUALS", "WHEN", "BOOL", "INT", "DOUBLE_PIPE", "DOUBLE_AMPER", "FROM", "OVER", "ACCUMULATE", "INIT", "RESULT", "COLLECT", "ARROW", "EQUAL", "GREATER", "GREATER_EQUAL", "LESS", "LESS_EQUAL", "NOT_EQUAL", "GRAVE_ACCENT", "FLOAT", "NULL", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "MISC", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT"
    };
    public static final int COMMA=88;
    public static final int VT_PATTERN_TYPE=39;
    public static final int VT_ACCUMULATE_ID_CLAUSE=28;
    public static final int VK_DIALECT=54;
    public static final int VK_FUNCTION=64;
    public static final int END=85;
    public static final int HexDigit=123;
    public static final int VK_ATTRIBUTES=57;
    public static final int VT_EXPRESSION_CHAIN=30;
    public static final int MISC=119;
    public static final int VT_AND_PREFIX=23;
    public static final int VK_QUERY=62;
    public static final int THEN=116;
    public static final int VK_AUTO_FOCUS=49;
    public static final int DOT=83;
    public static final int VK_IMPORT=59;
    public static final int VT_SLOT=15;
    public static final int VT_PACKAGE_ID=40;
    public static final int LEFT_SQUARE=114;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=126;
    public static final int VT_DATA_TYPE=38;
    public static final int VT_FACT=6;
    public static final int VK_MATCHES=68;
    public static final int LEFT_CURLY=117;
    public static final int AT=90;
    public static final int DOUBLE_AMPER=97;
    public static final int LEFT_PAREN=87;
    public static final int VT_QUERY_ID=9;
    public static final int VT_ACCESSOR_PATH=36;
    public static final int VT_LABEL=8;
    public static final int WHEN=93;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int WS=121;
    public static final int VT_FIELD=35;
    public static final int VK_SALIENCE=55;
    public static final int VK_SOUNDSLIKE=70;
    public static final int OVER=99;
    public static final int VK_AND=76;
    public static final int STRING=86;
    public static final int VT_ACCESSOR_ELEMENT=37;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=27;
    public static final int VK_GLOBAL=65;
    public static final int VK_REVERSE=80;
    public static final int VT_BEHAVIOR=21;
    public static final int GRAVE_ACCENT=111;
    public static final int VK_DURATION=53;
    public static final int VT_SQUARE_CHUNK=19;
    public static final int VK_FORALL=78;
    public static final int VT_PAREN_CHUNK=20;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int COLLECT=103;
    public static final int VK_ENABLED=56;
    public static final int EQUALS=92;
    public static final int UnicodeEscape=124;
    public static final int VK_PACKAGE=60;
    public static final int VT_RULE_ID=12;
    public static final int EQUAL=105;
    public static final int VK_NO_LOOP=48;
    public static final int SEMICOLON=81;
    public static final int VK_TEMPLATE=61;
    public static final int VT_AND_IMPLICIT=22;
    public static final int NULL=113;
    public static final int COLON=91;
    public static final int MULTI_LINE_COMMENT=128;
    public static final int VT_RULE_ATTRIBUTES=16;
    public static final int RIGHT_SQUARE=115;
    public static final int VK_AGENDA_GROUP=51;
    public static final int VT_FACT_OR=33;
    public static final int VK_NOT=73;
    public static final int VK_DATE_EXPIRES=46;
    public static final int ARROW=104;
    public static final int FLOAT=112;
    public static final int INIT=101;
    public static final int VT_SLOT_ID=14;
    public static final int VT_CURLY_CHUNK=18;
    public static final int VT_OR_PREFIX=24;
    public static final int DOUBLE_PIPE=96;
    public static final int LESS=108;
    public static final int VT_TYPE_DECLARE_ID=11;
    public static final int VT_PATTERN=31;
    public static final int VK_DATE_EFFECTIVE=45;
    public static final int EscapeSequence=122;
    public static final int VK_EXISTS=77;
    public static final int INT=95;
    public static final int VT_BIND_FIELD=34;
    public static final int VK_RULE=58;
    public static final int VK_EVAL=66;
    public static final int GREATER=106;
    public static final int RESULT=102;
    public static final int VT_FACT_BINDING=32;
    public static final int FROM=98;
    public static final int ID=82;
    public static final int NOT_EQUAL=110;
    public static final int RIGHT_CURLY=118;
    public static final int BOOL=94;
    public static final int VT_AND_INFIX=25;
    public static final int VT_PARAM_LIST=44;
    public static final int VK_ENTRY_POINT=72;
    public static final int VT_FROM_SOURCE=29;
    public static final int VK_LOCK_ON_ACTIVE=47;
    public static final int VK_CONTAINS=67;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VK_IN=74;
    public static final int VT_RHS_CHUNK=17;
    public static final int GREATER_EQUAL=107;
    public static final int VK_MEMBEROF=71;
    public static final int VT_OR_INFIX=26;
    public static final int DOT_STAR=84;
    public static final int VK_OR=75;
    public static final int VT_GLOBAL_ID=42;
    public static final int LESS_EQUAL=109;
    public static final int ACCUMULATE=100;
    public static final int VK_RULEFLOW_GROUP=52;
    public static final int VT_FUNCTION_ID=43;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int EOL=120;
    public static final int VT_IMPORT_ID=41;
    public static final int VK_ACTIVATION_GROUP=50;
    public static final int OctalEscape=125;
    public static final int VK_ACTION=79;
    public static final int VK_EXCLUDES=69;
    public static final int RIGHT_PAREN=89;
    public static final int VT_TEMPLATE_ID=10;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=127;
    public static final int VK_DECLARE=63;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[140+1];
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

		private String source = "unknown";

		private Stack<Map<DroolsParaphraseTypes, String>> paraphrases = new Stack<Map<DroolsParaphraseTypes, String>>();
    	private List<DroolsParserException> errors = new ArrayList<DroolsParserException>();
    	private DroolsParserExceptionFactory errorMessageFactory = new DroolsParserExceptionFactory(tokenNames, paraphrases);
    	private boolean lookaheadTest = false;
    	private LinkedList<DroolsSentence> editorInterface = null;
    	private boolean isEditorInterfaceEnabled = false;

    	public LinkedList<DroolsSentence> getEditorInterface(){
    		return editorInterface;
    	}

    	public void enableEditorInterface(){
    		isEditorInterfaceEnabled = true;
    	}

    	public void disableEditorInterface(){
    		isEditorInterfaceEnabled = false;
    	}

    	private void beginSentence(DroolsSentenceType sentenceType){
    		if (isEditorInterfaceEnabled) {
    			if (null == editorInterface) {
    				editorInterface = new LinkedList<DroolsSentence>();
    			}
    			DroolsSentence sentence = new DroolsSentence();
    			sentence.setType(sentenceType);
    			editorInterface.add(sentence);
    		}
    	}

    	private DroolsSentence getActiveSentence(){
    		return editorInterface.getLast();
    	}

    	private void emit(List tokens, DroolsEditorType editorType){
    		if (isEditorInterfaceEnabled && tokens != null) {		
    			for (Object activeObject : tokens){
    				emit((Token) activeObject, editorType);
    			}
    		}
    	}

    	private void emit(Token token, DroolsEditorType editorType){
    		if (isEditorInterfaceEnabled && token != null) {
    			((DroolsToken)token).setEditorType(editorType);
    			getActiveSentence().addContent((DroolsToken) token);
    		}
    	}

    	private void emit(boolean forceEmit, int activeContext){
    		if (isEditorInterfaceEnabled) {
    			if (!forceEmit && activeContext == Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR){
    				if (input.LA(1) == EOF && input.get(input.index() - 1).getType() == WS){
    					getActiveSentence().addContent(activeContext);
    				}
    			} else if (!forceEmit && activeContext == Location.LOCATION_LHS_BEGIN_OF_CONDITION && getLastTokenOnList(getActiveSentence().getContent()).getType() == FROM) {
    				//
    			} else {
    				getActiveSentence().addContent(activeContext);
    			}
    		}
    	}
    	
    	private void emit(int activeContext){
    		if (isEditorInterfaceEnabled) {
    			emit(false, activeContext);
    		}
    	}

    	private DroolsToken getLastTokenOnList(LinkedList list){
    		DroolsToken lastToken = null;
    		for (Object object : list) {
    			if (object instanceof DroolsToken) {
    				lastToken = (DroolsToken) object;
    			}
    		}
    		return lastToken;
    	}

    	private int getLastIntegerValue(LinkedList list) {
    		int lastIntergerValue = -1;
    		for (Object object : list) {
    			if (object instanceof Integer) {
    				lastIntergerValue = (Integer) object;
    			}
    		}
    		return lastIntergerValue;
    	}

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
    			} else if (input.LA(lookahead) == EOF) {
    				break;
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
    	private void pushParaphrases(DroolsParaphraseTypes type) {
    		Map<DroolsParaphraseTypes, String> activeMap = new HashMap<DroolsParaphraseTypes, String>();
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
    	private void setParaphrasesValue(DroolsParaphraseTypes type, String value) {
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:368:1: compilation_unit : ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:369:2: ( ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:369:4: ( package_statement )? ( statement )* EOF
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:369:4: ( package_statement )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))) {
                int LA1_1 = input.LA(2);

                if ( (LA1_1==ID) && ((((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))) {
                    int LA1_4 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.PACKAGE))) ) {
                        alt1=1;
                    }
                }
            }
            switch (alt1) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:369:4: package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_compilation_unit384);
                    package_statement1=package_statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_package_statement.add(package_statement1.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:370:3: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:370:3: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit389);
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
            match(input,EOF,FOLLOW_EOF_in_compilation_unit394); if (failed) return retval;
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
            // 372:3: -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:372:6: ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_COMPILATION_UNIT, "VT_COMPILATION_UNIT"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:372:28: ( package_statement )?
                if ( stream_package_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_package_statement.next());

                }
                stream_package_statement.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:372:47: ( statement )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:380:1: package_statement : package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) ;
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
         pushParaphrases(DroolsParaphraseTypes.PACKAGE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:2: ( package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:384:2: package_key package_id ( SEMICOLON )?
            {
            if ( backtracking==0 ) {
              	beginSentence(DroolsSentenceType.PACKAGE);	
            }
            pushFollow(FOLLOW_package_key_in_package_statement450);
            package_key4=package_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_package_key.add(package_key4.getTree());
            pushFollow(FOLLOW_package_id_in_package_statement454);
            package_id5=package_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_package_id.add(package_id5.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:386:14: ( SEMICOLON )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==SEMICOLON) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:386:14: SEMICOLON
                    {
                    SEMICOLON6=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_package_statement456); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON6);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(SEMICOLON6, DroolsEditorType.SYMBOL);	
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
            // 388:3: -> ^( package_key package_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:388:6: ^( package_key package_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:391:1: package_id : id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:392:2: (id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:392:4: id+= ID (id+= DOT id+= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_id483); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:392:11: (id+= DOT id+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:392:13: id+= DOT id+= ID
            	    {
            	    id=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_package_id489); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_package_id493); if (failed) return retval;
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
              	emit(list_id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.PACKAGE, buildStringFromTokens(list_id));	
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
            // 395:3: -> ^( VT_PACKAGE_ID ( ID )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:395:6: ^( VT_PACKAGE_ID ( ID )+ )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:398:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:401:3: ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query )
            int alt5=9;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==ID) && ((((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                    alt5=1;
                }
                else if ( (LA5_1==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.DECLARE))&&(validateIdentifierKey(DroolsSoftKeywords.DECLARE)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))))) {
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
                            new NoViableAltException("398:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 3, input);

                        throw nvae;
                    }
                }
                else if ( (LA5_1==STRING) && ((((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))&&(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))))) {
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
                            new NoViableAltException("398:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 4, input);

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
                        new NoViableAltException("398:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("398:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:402:2: rule_attribute
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( backtracking==0 ) {
                      	beginSentence(DroolsSentenceType.RULE_ATTRIBUTE);	
                    }
                    pushFollow(FOLLOW_rule_attribute_in_statement537);
                    rule_attribute7=rule_attribute();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, rule_attribute7.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:404:3: {...}? => function_import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((validateLT(1, "import") && validateLT(2, "function") )) ) {
                        if (backtracking>0) {failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, \"import\") && validateLT(2, \"function\") )");
                    }
                    pushFollow(FOLLOW_function_import_statement_in_statement544);
                    function_import_statement8=function_import_statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, function_import_statement8.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:405:4: import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_import_statement_in_statement550);
                    import_statement9=import_statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, import_statement9.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:406:4: global
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_global_in_statement556);
                    global10=global();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, global10.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:407:4: function
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_function_in_statement562);
                    function11=function();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, function11.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:408:4: {...}? => template
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((validateLT(1, DroolsSoftKeywords.TEMPLATE))) ) {
                        if (backtracking>0) {failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, DroolsSoftKeywords.TEMPLATE))");
                    }
                    pushFollow(FOLLOW_template_in_statement570);
                    template12=template();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, template12.getTree());

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:409:4: {...}? => type_declaration
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((validateLT(1, DroolsSoftKeywords.DECLARE))) ) {
                        if (backtracking>0) {failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, DroolsSoftKeywords.DECLARE))");
                    }
                    pushFollow(FOLLOW_type_declaration_in_statement578);
                    type_declaration13=type_declaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, type_declaration13.getTree());

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:410:4: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_statement583);
                    rule14=rule();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, rule14.getTree());

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:411:4: query
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_query_in_statement588);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:1: import_statement : import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) ;
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
         pushParaphrases(DroolsParaphraseTypes.IMPORT); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:417:2: ( import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:418:2: import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )?
            {
            if ( backtracking==0 ) {
              	beginSentence(DroolsSentenceType.IMPORT_STATEMENT);	
            }
            pushFollow(FOLLOW_import_key_in_import_statement615);
            import_key16=import_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_key.add(import_key16.getTree());
            pushFollow(FOLLOW_import_name_in_import_statement617);
            import_name17=import_name(DroolsParaphraseTypes.IMPORT);
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_name.add(import_name17.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:419:56: ( SEMICOLON )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==SEMICOLON) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:419:56: SEMICOLON
                    {
                    SEMICOLON18=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_import_statement620); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON18);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(SEMICOLON18, DroolsEditorType.SYMBOL);	
            }

            // AST REWRITE
            // elements: import_name, import_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 421:3: -> ^( import_key import_name )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:421:6: ^( import_key import_name )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:424:1: function_import_statement : imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) ;
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
         pushParaphrases(DroolsParaphraseTypes.FUNCTION_IMPORT); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:427:2: (imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:428:2: imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )?
            {
            if ( backtracking==0 ) {
              	beginSentence(DroolsSentenceType.FUNCTION_IMPORT_STATEMENT);	
            }
            pushFollow(FOLLOW_import_key_in_function_import_statement663);
            imp=import_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_key.add(imp.getTree());
            pushFollow(FOLLOW_function_key_in_function_import_statement665);
            function_key19=function_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_function_key.add(function_key19.getTree());
            pushFollow(FOLLOW_import_name_in_function_import_statement667);
            import_name20=import_name(DroolsParaphraseTypes.FUNCTION_IMPORT);
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_name.add(import_name20.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:429:82: ( SEMICOLON )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==SEMICOLON) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:429:82: SEMICOLON
                    {
                    SEMICOLON21=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_function_import_statement670); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON21);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(SEMICOLON21, DroolsEditorType.SYMBOL);	
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
            // 431:3: -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:431:6: ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:434:1: import_name[DroolsParaphraseTypes importType] : id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) ;
    public final import_name_return import_name(DroolsParaphraseTypes importType) throws RecognitionException {
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:435:2: (id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:435:4: id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name704); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:435:11: (id+= DOT id+= ID )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==DOT) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:435:13: id+= DOT id+= ID
            	    {
            	    id=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_import_name710); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_import_name714); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:435:33: (id+= DOT_STAR )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==DOT_STAR) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:435:33: id+= DOT_STAR
                    {
                    id=(Token)input.LT(1);
                    match(input,DOT_STAR,FOLLOW_DOT_STAR_in_import_name721); if (failed) return retval;
                    if ( backtracking==0 ) stream_DOT_STAR.add(id);

                    if (list_id==null) list_id=new ArrayList();
                    list_id.add(id);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(list_id, DroolsEditorType.IDENTIFIER);
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
            // 438:3: -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:438:6: ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
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
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:438:25: ( DOT_STAR )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:441:1: global : global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) ;
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
         pushParaphrases(DroolsParaphraseTypes.GLOBAL); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:444:2: ( global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:445:2: global_key data_type global_id ( SEMICOLON )?
            {
            if ( backtracking==0 ) {
              	beginSentence(DroolsSentenceType.GLOBAL);	
            }
            pushFollow(FOLLOW_global_key_in_global766);
            global_key22=global_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_global_key.add(global_key22.getTree());
            pushFollow(FOLLOW_data_type_in_global768);
            data_type23=data_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_data_type.add(data_type23.getTree());
            pushFollow(FOLLOW_global_id_in_global770);
            global_id24=global_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_global_id.add(global_id24.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:446:34: ( SEMICOLON )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==SEMICOLON) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:446:34: SEMICOLON
                    {
                    SEMICOLON25=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_global772); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON25);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(SEMICOLON25, DroolsEditorType.SYMBOL);	
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
            // 448:3: -> ^( global_key data_type global_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:448:6: ^( global_key data_type global_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:451:1: global_id : id= ID -> VT_GLOBAL_ID[$id] ;
    public final global_id_return global_id() throws RecognitionException {
        global_id_return retval = new global_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:2: (id= ID -> VT_GLOBAL_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:4: id= ID
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global_id801); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.GLOBAL, id.getText());	
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
            // 455:3: -> VT_GLOBAL_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:458:1: function : function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) ;
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
         pushParaphrases(DroolsParaphraseTypes.FUNCTION); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:2: ( function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:462:2: function_key ( data_type )? function_id parameters curly_chunk
            {
            if ( backtracking==0 ) {
              	beginSentence(DroolsSentenceType.FUNCTION);	
            }
            pushFollow(FOLLOW_function_key_in_function838);
            function_key26=function_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_function_key.add(function_key26.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:463:16: ( data_type )?
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:463:16: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function840);
                    data_type27=data_type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_data_type.add(data_type27.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_function_id_in_function843);
            function_id28=function_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_function_id.add(function_id28.getTree());
            pushFollow(FOLLOW_parameters_in_function845);
            parameters29=parameters();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_parameters.add(parameters29.getTree());
            pushFollow(FOLLOW_curly_chunk_in_function847);
            curly_chunk30=curly_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_curly_chunk.add(curly_chunk30.getTree());

            // AST REWRITE
            // elements: data_type, function_id, function_key, parameters, curly_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 464:3: -> ^( function_key ( data_type )? function_id parameters curly_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:464:6: ^( function_key ( data_type )? function_id parameters curly_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_function_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:464:21: ( data_type )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:467:1: function_id : id= ID -> VT_FUNCTION_ID[$id] ;
    public final function_id_return function_id() throws RecognitionException {
        function_id_return retval = new function_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:2: (id= ID -> VT_FUNCTION_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:4: id= ID
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function_id877); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.FUNCTION, id.getText());	
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
            // 471:3: -> VT_FUNCTION_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:474:1: query : query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block END ) ;
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
         pushParaphrases(DroolsParaphraseTypes.QUERY); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:477:2: ( query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:478:2: query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )?
            {
            if ( backtracking==0 ) {
              	beginSentence(DroolsSentenceType.QUERY);	
            }
            pushFollow(FOLLOW_query_key_in_query914);
            query_key31=query_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_query_key.add(query_key31.getTree());
            pushFollow(FOLLOW_query_id_in_query916);
            query_id32=query_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_query_id.add(query_id32.getTree());
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:481:3: ( parameters )?
            int alt12=2;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:481:3: parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query924);
                    parameters33=parameters();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_parameters.add(parameters33.getTree());

                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }
            pushFollow(FOLLOW_normal_lhs_block_in_query933);
            normal_lhs_block34=normal_lhs_block();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block34.getTree());
            END35=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query938); if (failed) return retval;
            if ( backtracking==0 ) stream_END.add(END35);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:7: ( SEMICOLON )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==SEMICOLON) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:7: SEMICOLON
                    {
                    SEMICOLON36=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_query940); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON36);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(END35, DroolsEditorType.KEYWORD);
              		emit(SEMICOLON36, DroolsEditorType.SYMBOL);	
            }

            // AST REWRITE
            // elements: query_id, END, parameters, query_key, normal_lhs_block
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 487:3: -> ^( query_key query_id ( parameters )? normal_lhs_block END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:6: ^( query_key query_id ( parameters )? normal_lhs_block END )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_query_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_query_id.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:27: ( parameters )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:490:1: query_id : (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] );
    public final query_id_return query_id() throws RecognitionException {
        query_id_return retval = new query_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:491:2: (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] )
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
                    new NoViableAltException("490:1: query_id : (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] );", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:491:5: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_query_id975); if (failed) return retval;
                    if ( backtracking==0 ) stream_ID.add(id);

                    if ( backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.QUERY, id.getText());	
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
                    // 493:65: -> VT_QUERY_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_QUERY_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:5: id= STRING
                    {
                    id=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_query_id991); if (failed) return retval;
                    if ( backtracking==0 ) stream_STRING.add(id);

                    if ( backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.QUERY, id.getText());	
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
                    // 496:65: -> VT_QUERY_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:499:1: parameters : LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:500:2: ( LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:500:4: LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN
            {
            LEFT_PAREN37=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parameters1010); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN37);

            if ( backtracking==0 ) {
              	emit(LEFT_PAREN37, DroolsEditorType.SYMBOL);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:4: ( param_definition ( COMMA param_definition )* )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==ID) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:6: param_definition ( COMMA param_definition )*
                    {
                    pushFollow(FOLLOW_param_definition_in_parameters1019);
                    param_definition38=param_definition();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_param_definition.add(param_definition38.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:23: ( COMMA param_definition )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==COMMA) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:24: COMMA param_definition
                    	    {
                    	    COMMA39=(Token)input.LT(1);
                    	    match(input,COMMA,FOLLOW_COMMA_in_parameters1022); if (failed) return retval;
                    	    if ( backtracking==0 ) stream_COMMA.add(COMMA39);

                    	    if ( backtracking==0 ) {
                    	      	emit(COMMA39, DroolsEditorType.SYMBOL);	
                    	    }
                    	    pushFollow(FOLLOW_param_definition_in_parameters1026);
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
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parameters1035); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN41);

            if ( backtracking==0 ) {
              	emit(RIGHT_PAREN41, DroolsEditorType.SYMBOL);	
            }

            // AST REWRITE
            // elements: param_definition, RIGHT_PAREN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 503:3: -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:503:6: ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_PARAM_LIST, "VT_PARAM_LIST"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:503:22: ( param_definition )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:506:1: param_definition : ( data_type )? argument ;
    public final param_definition_return param_definition() throws RecognitionException {
        param_definition_return retval = new param_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        data_type_return data_type42 = null;

        argument_return argument43 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:507:2: ( ( data_type )? argument )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:507:4: ( data_type )? argument
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:507:4: ( data_type )?
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:507:4: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition1061);
                    data_type42=data_type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, data_type42.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition1064);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:510:1: argument : ID ( dimension_definition )* ;
    public final argument_return argument() throws RecognitionException {
        argument_return retval = new argument_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID44=null;
        dimension_definition_return dimension_definition45 = null;


        Object ID44_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:511:2: ( ID ( dimension_definition )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:511:4: ID ( dimension_definition )*
            {
            root_0 = (Object)adaptor.nil();

            ID44=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument1075); if (failed) return retval;
            if ( backtracking==0 ) {
            ID44_tree = (Object)adaptor.create(ID44);
            adaptor.addChild(root_0, ID44_tree);
            }
            if ( backtracking==0 ) {
              	emit(ID44, DroolsEditorType.IDENTIFIER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:512:3: ( dimension_definition )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==LEFT_SQUARE) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:512:3: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_argument1081);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:515:1: type_declaration : declare_key type_declare_id ( decl_metadata )* ( decl_field )* END -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END ) ;
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
         pushParaphrases(DroolsParaphraseTypes.TYPE_DECLARE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:518:2: ( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:519:2: declare_key type_declare_id ( decl_metadata )* ( decl_field )* END
            {
            if ( backtracking==0 ) {
              	beginSentence(DroolsSentenceType.TYPE_DECLARATION);	
            }
            pushFollow(FOLLOW_declare_key_in_type_declaration1109);
            declare_key46=declare_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_declare_key.add(declare_key46.getTree());
            pushFollow(FOLLOW_type_declare_id_in_type_declaration1112);
            type_declare_id47=type_declare_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_type_declare_id.add(type_declare_id47.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:3: ( decl_metadata )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AT) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:3: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_type_declaration1116);
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

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:522:3: ( decl_field )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==ID) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:522:3: decl_field
            	    {
            	    pushFollow(FOLLOW_decl_field_in_type_declaration1121);
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
            match(input,END,FOLLOW_END_in_type_declaration1126); if (failed) return retval;
            if ( backtracking==0 ) stream_END.add(END50);

            if ( backtracking==0 ) {
              	emit(END50, DroolsEditorType.KEYWORD);	
            }

            // AST REWRITE
            // elements: decl_metadata, type_declare_id, declare_key, END, decl_field
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 525:3: -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:525:6: ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_declare_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_type_declare_id.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:525:36: ( decl_metadata )*
                while ( stream_decl_metadata.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_metadata.next());

                }
                stream_decl_metadata.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:525:51: ( decl_field )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:528:1: type_declare_id : id= ID -> VT_TYPE_DECLARE_ID[$id] ;
    public final type_declare_id_return type_declare_id() throws RecognitionException {
        type_declare_id_return retval = new type_declare_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:529:2: (id= ID -> VT_TYPE_DECLARE_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:529:5: id= ID
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_type_declare_id1161); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.TYPE_DECLARE, id.getText());	
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
            // 531:72: -> VT_TYPE_DECLARE_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:534:1: decl_metadata : AT ID paren_chunk -> ^( AT ID paren_chunk ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:2: ( AT ID paren_chunk -> ^( AT ID paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:4: AT ID paren_chunk
            {
            AT51=(Token)input.LT(1);
            match(input,AT,FOLLOW_AT_in_decl_metadata1180); if (failed) return retval;
            if ( backtracking==0 ) stream_AT.add(AT51);

            if ( backtracking==0 ) {
              	emit(AT51, DroolsEditorType.SYMBOL);	
            }
            ID52=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_decl_metadata1188); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID52);

            if ( backtracking==0 ) {
              	emit(ID52, DroolsEditorType.IDENTIFIER);	
            }
            pushFollow(FOLLOW_paren_chunk_in_decl_metadata1195);
            paren_chunk53=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk53.getTree());

            // AST REWRITE
            // elements: AT, ID, paren_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 540:3: -> ^( AT ID paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:540:6: ^( AT ID paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:1: decl_field : ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:544:2: ( ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:544:4: ID ( decl_field_initialization )? COLON data_type ( decl_metadata )*
            {
            ID54=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_decl_field1218); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID54);

            if ( backtracking==0 ) {
              	emit(ID54, DroolsEditorType.IDENTIFIER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:545:3: ( decl_field_initialization )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==EQUALS) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:545:3: decl_field_initialization
                    {
                    pushFollow(FOLLOW_decl_field_initialization_in_decl_field1224);
                    decl_field_initialization55=decl_field_initialization();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_decl_field_initialization.add(decl_field_initialization55.getTree());

                    }
                    break;

            }

            COLON56=(Token)input.LT(1);
            match(input,COLON,FOLLOW_COLON_in_decl_field1230); if (failed) return retval;
            if ( backtracking==0 ) stream_COLON.add(COLON56);

            if ( backtracking==0 ) {
              	emit(COLON56, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_data_type_in_decl_field1236);
            data_type57=data_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_data_type.add(data_type57.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:548:3: ( decl_metadata )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==AT) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:548:3: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_decl_field1240);
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
            // elements: ID, data_type, decl_metadata, decl_field_initialization
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 549:3: -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:549:6: ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ID.next(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:549:11: ( decl_field_initialization )?
                if ( stream_decl_field_initialization.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_field_initialization.next());

                }
                stream_decl_field_initialization.reset();
                adaptor.addChild(root_1, stream_data_type.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:549:48: ( decl_metadata )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:1: decl_field_initialization : EQUALS paren_chunk -> ^( EQUALS paren_chunk ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:553:2: ( EQUALS paren_chunk -> ^( EQUALS paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:553:4: EQUALS paren_chunk
            {
            EQUALS59=(Token)input.LT(1);
            match(input,EQUALS,FOLLOW_EQUALS_in_decl_field_initialization1268); if (failed) return retval;
            if ( backtracking==0 ) stream_EQUALS.add(EQUALS59);

            if ( backtracking==0 ) {
              	emit(EQUALS59, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_paren_chunk_in_decl_field_initialization1274);
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
            // 555:2: -> ^( EQUALS paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:5: ^( EQUALS paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:558:1: template : template_key template_id (semi1= SEMICOLON )? ( template_slot )+ END (semi2= SEMICOLON )? -> ^( template_key template_id ( template_slot )+ END ) ;
    public final template_return template() throws RecognitionException {
        template_return retval = new template_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token semi1=null;
        Token semi2=null;
        Token END64=null;
        template_key_return template_key61 = null;

        template_id_return template_id62 = null;

        template_slot_return template_slot63 = null;


        Object semi1_tree=null;
        Object semi2_tree=null;
        Object END64_tree=null;
        RewriteRuleTokenStream stream_END=new RewriteRuleTokenStream(adaptor,"token END");
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_template_id=new RewriteRuleSubtreeStream(adaptor,"rule template_id");
        RewriteRuleSubtreeStream stream_template_slot=new RewriteRuleSubtreeStream(adaptor,"rule template_slot");
        RewriteRuleSubtreeStream stream_template_key=new RewriteRuleSubtreeStream(adaptor,"rule template_key");
         pushParaphrases(DroolsParaphraseTypes.TEMPLATE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:561:2: ( template_key template_id (semi1= SEMICOLON )? ( template_slot )+ END (semi2= SEMICOLON )? -> ^( template_key template_id ( template_slot )+ END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:2: template_key template_id (semi1= SEMICOLON )? ( template_slot )+ END (semi2= SEMICOLON )?
            {
            if ( backtracking==0 ) {
              	beginSentence(DroolsSentenceType.TEMPLATE);	
            }
            pushFollow(FOLLOW_template_key_in_template1311);
            template_key61=template_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_template_key.add(template_key61.getTree());
            pushFollow(FOLLOW_template_id_in_template1313);
            template_id62=template_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_template_id.add(template_id62.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:8: (semi1= SEMICOLON )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==SEMICOLON) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:8: semi1= SEMICOLON
                    {
                    semi1=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1320); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(semi1);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(semi1, DroolsEditorType.SYMBOL);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:566:3: ( template_slot )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:566:3: template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template1328);
            	    template_slot63=template_slot();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_template_slot.add(template_slot63.getTree());

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

            END64=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_template1333); if (failed) return retval;
            if ( backtracking==0 ) stream_END.add(END64);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:567:12: (semi2= SEMICOLON )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==SEMICOLON) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:567:12: semi2= SEMICOLON
                    {
                    semi2=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1337); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(semi2);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(END64, DroolsEditorType.KEYWORD);
              		emit(semi2, DroolsEditorType.SYMBOL);	
            }

            // AST REWRITE
            // elements: END, template_id, template_slot, template_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 570:3: -> ^( template_key template_id ( template_slot )+ END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:570:6: ^( template_key template_id ( template_slot )+ END )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:1: template_id : (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] );
    public final template_id_return template_id() throws RecognitionException {
        template_id_return retval = new template_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:574:2: (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] )
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
                    new NoViableAltException("573:1: template_id : (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] );", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:574:5: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_template_id1370); if (failed) return retval;
                    if ( backtracking==0 ) stream_ID.add(id);

                    if ( backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.TEMPLATE, id.getText());	
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
                    // 576:68: -> VT_TEMPLATE_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_TEMPLATE_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:577:5: id= STRING
                    {
                    id=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_template_id1386); if (failed) return retval;
                    if ( backtracking==0 ) stream_STRING.add(id);

                    if ( backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.TEMPLATE, id.getText());	
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
                    // 579:68: -> VT_TEMPLATE_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:582:1: template_slot : data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) ;
    public final template_slot_return template_slot() throws RecognitionException {
        template_slot_return retval = new template_slot_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON67=null;
        data_type_return data_type65 = null;

        slot_id_return slot_id66 = null;


        Object SEMICOLON67_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_slot_id=new RewriteRuleSubtreeStream(adaptor,"rule slot_id");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:2: ( data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:5: data_type slot_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_data_type_in_template_slot1406);
            data_type65=data_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_data_type.add(data_type65.getTree());
            pushFollow(FOLLOW_slot_id_in_template_slot1408);
            slot_id66=slot_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_slot_id.add(slot_id66.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:23: ( SEMICOLON )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==SEMICOLON) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:23: SEMICOLON
                    {
                    SEMICOLON67=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template_slot1410); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON67);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(SEMICOLON67, DroolsEditorType.SYMBOL);	
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
            // 585:3: -> ^( VT_SLOT data_type slot_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:585:6: ^( VT_SLOT data_type slot_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:588:1: slot_id : id= ID -> VT_SLOT_ID[$id] ;
    public final slot_id_return slot_id() throws RecognitionException {
        slot_id_return retval = new slot_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:589:2: (id= ID -> VT_SLOT_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:589:4: id= ID
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_slot_id1439); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.IDENTIFIER);	
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
            // 591:3: -> VT_SLOT_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:1: rule : rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk ) ;
    public final rule_return rule() throws RecognitionException {
        rule_return retval = new rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        rule_key_return rule_key68 = null;

        rule_id_return rule_id69 = null;

        rule_attributes_return rule_attributes70 = null;

        when_part_return when_part71 = null;

        rhs_chunk_return rhs_chunk72 = null;


        RewriteRuleSubtreeStream stream_rule_key=new RewriteRuleSubtreeStream(adaptor,"rule rule_key");
        RewriteRuleSubtreeStream stream_rule_id=new RewriteRuleSubtreeStream(adaptor,"rule rule_id");
        RewriteRuleSubtreeStream stream_when_part=new RewriteRuleSubtreeStream(adaptor,"rule when_part");
        RewriteRuleSubtreeStream stream_rule_attributes=new RewriteRuleSubtreeStream(adaptor,"rule rule_attributes");
        RewriteRuleSubtreeStream stream_rhs_chunk=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk");
         pushParaphrases(DroolsParaphraseTypes.RULE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:597:2: ( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:598:2: rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk
            {
            if ( backtracking==0 ) {
              	beginSentence(DroolsSentenceType.RULE);	
            }
            pushFollow(FOLLOW_rule_key_in_rule1476);
            rule_key68=rule_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rule_key.add(rule_key68.getTree());
            pushFollow(FOLLOW_rule_id_in_rule1478);
            rule_id69=rule_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rule_id.add(rule_id69.getTree());
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:601:3: ( rule_attributes )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:601:3: rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1488);
                    rule_attributes70=rule_attributes();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_rule_attributes.add(rule_attributes70.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:601:20: ( when_part )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==WHEN) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:601:20: when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule1491);
                    when_part71=when_part();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_when_part.add(when_part71.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1494);
            rhs_chunk72=rhs_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rhs_chunk.add(rhs_chunk72.getTree());

            // AST REWRITE
            // elements: rhs_chunk, rule_id, when_part, rule_attributes, rule_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 602:3: -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:6: ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_rule_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rule_id.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:25: ( rule_attributes )?
                if ( stream_rule_attributes.hasNext() ) {
                    adaptor.addChild(root_1, stream_rule_attributes.next());

                }
                stream_rule_attributes.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:42: ( when_part )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:605:1: when_part : WHEN ( COLON )? normal_lhs_block -> WHEN normal_lhs_block ;
    public final when_part_return when_part() throws RecognitionException {
        when_part_return retval = new when_part_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token WHEN73=null;
        Token COLON74=null;
        normal_lhs_block_return normal_lhs_block75 = null;


        Object WHEN73_tree=null;
        Object COLON74_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_WHEN=new RewriteRuleTokenStream(adaptor,"token WHEN");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:606:2: ( WHEN ( COLON )? normal_lhs_block -> WHEN normal_lhs_block )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:606:5: WHEN ( COLON )? normal_lhs_block
            {
            WHEN73=(Token)input.LT(1);
            match(input,WHEN,FOLLOW_WHEN_in_when_part1524); if (failed) return retval;
            if ( backtracking==0 ) stream_WHEN.add(WHEN73);

            if ( backtracking==0 ) {
              	emit(WHEN73, DroolsEditorType.KEYWORD);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:607:3: ( COLON )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==COLON) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:607:3: COLON
                    {
                    COLON74=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_when_part1530); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON74);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(COLON74, DroolsEditorType.SYMBOL);	
            }
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }
            pushFollow(FOLLOW_normal_lhs_block_in_when_part1540);
            normal_lhs_block75=normal_lhs_block();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block75.getTree());

            // AST REWRITE
            // elements: normal_lhs_block, WHEN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 610:2: -> WHEN normal_lhs_block
            {
                adaptor.addChild(root_0, stream_WHEN.next());
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:613:1: rule_id : (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] );
    public final rule_id_return rule_id() throws RecognitionException {
        rule_id_return retval = new rule_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:614:2: (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] )
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
                    new NoViableAltException("613:1: rule_id : (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] );", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:614:5: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_rule_id1561); if (failed) return retval;
                    if ( backtracking==0 ) stream_ID.add(id);

                    if ( backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.RULE, id.getText());	
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
                    // 616:64: -> VT_RULE_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_RULE_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:617:5: id= STRING
                    {
                    id=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_rule_id1577); if (failed) return retval;
                    if ( backtracking==0 ) stream_STRING.add(id);

                    if ( backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.RULE, id.getText());	
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
                    // 619:64: -> VT_RULE_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:622:1: rule_attributes : ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) ;
    public final rule_attributes_return rule_attributes() throws RecognitionException {
        rule_attributes_return retval = new rule_attributes_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON77=null;
        Token COMMA79=null;
        rule_attribute_return attr = null;

        attributes_key_return attributes_key76 = null;

        rule_attribute_return rule_attribute78 = null;


        Object COLON77_tree=null;
        Object COMMA79_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_attributes_key=new RewriteRuleSubtreeStream(adaptor,"rule attributes_key");
        RewriteRuleSubtreeStream stream_rule_attribute=new RewriteRuleSubtreeStream(adaptor,"rule rule_attribute");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:2: ( ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:4: ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:4: ( attributes_key COLON )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {
                int LA32_1 = input.LA(2);

                if ( (LA32_1==COLON) && ((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))) {
                    alt32=1;
                }
            }
            switch (alt32) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:6: attributes_key COLON
                    {
                    pushFollow(FOLLOW_attributes_key_in_rule_attributes1598);
                    attributes_key76=attributes_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_attributes_key.add(attributes_key76.getTree());
                    COLON77=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_rule_attributes1600); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON77);

                    if ( backtracking==0 ) {
                      	emit(COLON77, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1610);
            rule_attribute78=rule_attribute();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rule_attribute.add(rule_attribute78.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:624:18: ( ( COMMA )? attr= rule_attribute )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==COMMA) ) {
                    alt34=1;
                }
                else if ( (LA34_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:624:20: ( COMMA )? attr= rule_attribute
            	    {
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:624:20: ( COMMA )?
            	    int alt33=2;
            	    int LA33_0 = input.LA(1);

            	    if ( (LA33_0==COMMA) ) {
            	        alt33=1;
            	    }
            	    switch (alt33) {
            	        case 1 :
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:624:20: COMMA
            	            {
            	            COMMA79=(Token)input.LT(1);
            	            match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1614); if (failed) return retval;
            	            if ( backtracking==0 ) stream_COMMA.add(COMMA79);


            	            }
            	            break;

            	    }

            	    if ( backtracking==0 ) {
            	      	emit(COMMA79, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1621);
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
            // 625:3: -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:625:6: ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_RULE_ATTRIBUTES, "VT_RULE_ATTRIBUTES"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:625:27: ( attributes_key )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:628:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );
    public final rule_attribute_return rule_attribute() throws RecognitionException {
        rule_attribute_return retval = new rule_attribute_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        salience_return salience80 = null;

        no_loop_return no_loop81 = null;

        agenda_group_return agenda_group82 = null;

        duration_return duration83 = null;

        activation_group_return activation_group84 = null;

        auto_focus_return auto_focus85 = null;

        date_effective_return date_effective86 = null;

        date_expires_return date_expires87 = null;

        enabled_return enabled88 = null;

        ruleflow_group_return ruleflow_group89 = null;

        lock_on_active_return lock_on_active90 = null;

        dialect_return dialect91 = null;



         pushParaphrases(DroolsParaphraseTypes.RULE_ATTRIBUTE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:631:2: ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect )
            int alt35=12;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
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
                                    new NoViableAltException("628:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 10, input);

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
                                new NoViableAltException("628:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 7, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("628:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA35_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {
                    int LA35_3 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {
                        alt35=1;
                    }
                    else if ( ((validateIdentifierKey(DroolsSoftKeywords.DURATION))) ) {
                        alt35=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("628:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 3, input);

                        throw nvae;
                    }
                }
                else if ( (LA35_1==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {
                    alt35=1;
                }
                else if ( (LA35_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {
                    alt35=9;
                }
                else if ( (LA35_1==STRING) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {
                    alt35=12;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("628:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("628:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:631:4: salience
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_salience_in_rule_attribute1660);
                    salience80=salience();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, salience80.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:632:4: no_loop
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_no_loop_in_rule_attribute1666);
                    no_loop81=no_loop();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, no_loop81.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:633:4: agenda_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1673);
                    agenda_group82=agenda_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, agenda_group82.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:4: duration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_duration_in_rule_attribute1680);
                    duration83=duration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, duration83.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:4: activation_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_activation_group_in_rule_attribute1687);
                    activation_group84=activation_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, activation_group84.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:636:4: auto_focus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1693);
                    auto_focus85=auto_focus();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, auto_focus85.getTree());

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:637:4: date_effective
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_effective_in_rule_attribute1699);
                    date_effective86=date_effective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, date_effective86.getTree());

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:638:4: date_expires
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_expires_in_rule_attribute1705);
                    date_expires87=date_expires();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, date_expires87.getTree());

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:4: enabled
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enabled_in_rule_attribute1711);
                    enabled88=enabled();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, enabled88.getTree());

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:640:4: ruleflow_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1717);
                    ruleflow_group89=ruleflow_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, ruleflow_group89.getTree());

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:641:4: lock_on_active
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1723);
                    lock_on_active90=lock_on_active();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lock_on_active90.getTree());

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:4: dialect
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_dialect_in_rule_attribute1728);
                    dialect91=dialect();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, dialect91.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:645:1: date_effective : date_effective_key STRING ;
    public final date_effective_return date_effective() throws RecognitionException {
        date_effective_return retval = new date_effective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING93=null;
        date_effective_key_return date_effective_key92 = null;


        Object STRING93_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:646:2: ( date_effective_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:646:4: date_effective_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_effective_key_in_date_effective1740);
            date_effective_key92=date_effective_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_effective_key92.getTree(), root_0);
            STRING93=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1743); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING93_tree = (Object)adaptor.create(STRING93);
            adaptor.addChild(root_0, STRING93_tree);
            }
            if ( backtracking==0 ) {
              	emit(STRING93, DroolsEditorType.STRING_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:650:1: date_expires : date_expires_key STRING ;
    public final date_expires_return date_expires() throws RecognitionException {
        date_expires_return retval = new date_expires_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING95=null;
        date_expires_key_return date_expires_key94 = null;


        Object STRING95_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:651:2: ( date_expires_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:651:4: date_expires_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_expires_key_in_date_expires1757);
            date_expires_key94=date_expires_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_expires_key94.getTree(), root_0);
            STRING95=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1760); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING95_tree = (Object)adaptor.create(STRING95);
            adaptor.addChild(root_0, STRING95_tree);
            }
            if ( backtracking==0 ) {
              	emit(STRING95, DroolsEditorType.STRING_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:655:1: enabled : enabled_key BOOL ;
    public final enabled_return enabled() throws RecognitionException {
        enabled_return retval = new enabled_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL97=null;
        enabled_key_return enabled_key96 = null;


        Object BOOL97_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:656:2: ( enabled_key BOOL )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:656:4: enabled_key BOOL
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_enabled_key_in_enabled1775);
            enabled_key96=enabled_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(enabled_key96.getTree(), root_0);
            BOOL97=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1778); if (failed) return retval;
            if ( backtracking==0 ) {
            BOOL97_tree = (Object)adaptor.create(BOOL97);
            adaptor.addChild(root_0, BOOL97_tree);
            }
            if ( backtracking==0 ) {
              	emit(BOOL97, DroolsEditorType.BOOLEAN_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:660:1: salience : salience_key ( INT | paren_chunk ) ;
    public final salience_return salience() throws RecognitionException {
        salience_return retval = new salience_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT99=null;
        salience_key_return salience_key98 = null;

        paren_chunk_return paren_chunk100 = null;


        Object INT99_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:661:2: ( salience_key ( INT | paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:661:4: salience_key ( INT | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_salience_key_in_salience1793);
            salience_key98=salience_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(salience_key98.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:662:3: ( INT | paren_chunk )
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
                    new NoViableAltException("662:3: ( INT | paren_chunk )", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:662:5: INT
                    {
                    INT99=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_salience1800); if (failed) return retval;
                    if ( backtracking==0 ) {
                    INT99_tree = (Object)adaptor.create(INT99);
                    adaptor.addChild(root_0, INT99_tree);
                    }
                    if ( backtracking==0 ) {
                      	emit(INT99, DroolsEditorType.NUMERIC_CONST );	
                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:663:5: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1809);
                    paren_chunk100=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk100.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:667:1: no_loop : no_loop_key ( BOOL )? ;
    public final no_loop_return no_loop() throws RecognitionException {
        no_loop_return retval = new no_loop_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL102=null;
        no_loop_key_return no_loop_key101 = null;


        Object BOOL102_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:668:2: ( no_loop_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:668:4: no_loop_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_no_loop_key_in_no_loop1824);
            no_loop_key101=no_loop_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(no_loop_key101.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:668:17: ( BOOL )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==BOOL) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:668:17: BOOL
                    {
                    BOOL102=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1827); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL102_tree = (Object)adaptor.create(BOOL102);
                    adaptor.addChild(root_0, BOOL102_tree);
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(BOOL102, DroolsEditorType.BOOLEAN_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:672:1: auto_focus : auto_focus_key ( BOOL )? ;
    public final auto_focus_return auto_focus() throws RecognitionException {
        auto_focus_return retval = new auto_focus_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL104=null;
        auto_focus_key_return auto_focus_key103 = null;


        Object BOOL104_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:673:2: ( auto_focus_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:673:4: auto_focus_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_auto_focus_key_in_auto_focus1842);
            auto_focus_key103=auto_focus_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(auto_focus_key103.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:673:20: ( BOOL )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==BOOL) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:673:20: BOOL
                    {
                    BOOL104=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1845); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL104_tree = (Object)adaptor.create(BOOL104);
                    adaptor.addChild(root_0, BOOL104_tree);
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(BOOL104, DroolsEditorType.BOOLEAN_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:677:1: activation_group : activation_group_key STRING ;
    public final activation_group_return activation_group() throws RecognitionException {
        activation_group_return retval = new activation_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING106=null;
        activation_group_key_return activation_group_key105 = null;


        Object STRING106_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:678:2: ( activation_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:678:4: activation_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_activation_group_key_in_activation_group1862);
            activation_group_key105=activation_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(activation_group_key105.getTree(), root_0);
            STRING106=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1865); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING106_tree = (Object)adaptor.create(STRING106);
            adaptor.addChild(root_0, STRING106_tree);
            }
            if ( backtracking==0 ) {
              	emit(STRING106, DroolsEditorType.STRING_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:682:1: ruleflow_group : ruleflow_group_key STRING ;
    public final ruleflow_group_return ruleflow_group() throws RecognitionException {
        ruleflow_group_return retval = new ruleflow_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING108=null;
        ruleflow_group_key_return ruleflow_group_key107 = null;


        Object STRING108_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:683:2: ( ruleflow_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:683:4: ruleflow_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_ruleflow_group_key_in_ruleflow_group1879);
            ruleflow_group_key107=ruleflow_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(ruleflow_group_key107.getTree(), root_0);
            STRING108=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1882); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING108_tree = (Object)adaptor.create(STRING108);
            adaptor.addChild(root_0, STRING108_tree);
            }
            if ( backtracking==0 ) {
              	emit(STRING108, DroolsEditorType.STRING_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:687:1: agenda_group : agenda_group_key STRING ;
    public final agenda_group_return agenda_group() throws RecognitionException {
        agenda_group_return retval = new agenda_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING110=null;
        agenda_group_key_return agenda_group_key109 = null;


        Object STRING110_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:688:2: ( agenda_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:688:4: agenda_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_agenda_group_key_in_agenda_group1896);
            agenda_group_key109=agenda_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(agenda_group_key109.getTree(), root_0);
            STRING110=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1899); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING110_tree = (Object)adaptor.create(STRING110);
            adaptor.addChild(root_0, STRING110_tree);
            }
            if ( backtracking==0 ) {
              	emit(STRING110, DroolsEditorType.STRING_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:692:1: duration : duration_key INT ;
    public final duration_return duration() throws RecognitionException {
        duration_return retval = new duration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT112=null;
        duration_key_return duration_key111 = null;


        Object INT112_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:693:2: ( duration_key INT )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:693:4: duration_key INT
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_duration_key_in_duration1913);
            duration_key111=duration_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(duration_key111.getTree(), root_0);
            INT112=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1916); if (failed) return retval;
            if ( backtracking==0 ) {
            INT112_tree = (Object)adaptor.create(INT112);
            adaptor.addChild(root_0, INT112_tree);
            }
            if ( backtracking==0 ) {
              	emit(INT112, DroolsEditorType.NUMERIC_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:1: dialect : dialect_key STRING ;
    public final dialect_return dialect() throws RecognitionException {
        dialect_return retval = new dialect_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING114=null;
        dialect_key_return dialect_key113 = null;


        Object STRING114_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:2: ( dialect_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:4: dialect_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_dialect_key_in_dialect1932);
            dialect_key113=dialect_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(dialect_key113.getTree(), root_0);
            STRING114=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1935); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING114_tree = (Object)adaptor.create(STRING114);
            adaptor.addChild(root_0, STRING114_tree);
            }
            if ( backtracking==0 ) {
              	emit(STRING114, DroolsEditorType.STRING_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:702:1: lock_on_active : lock_on_active_key ( BOOL )? ;
    public final lock_on_active_return lock_on_active() throws RecognitionException {
        lock_on_active_return retval = new lock_on_active_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL116=null;
        lock_on_active_key_return lock_on_active_key115 = null;


        Object BOOL116_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:703:2: ( lock_on_active_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:703:4: lock_on_active_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lock_on_active_key_in_lock_on_active1953);
            lock_on_active_key115=lock_on_active_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(lock_on_active_key115.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:703:24: ( BOOL )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==BOOL) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:703:24: BOOL
                    {
                    BOOL116=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1956); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL116_tree = (Object)adaptor.create(BOOL116);
                    adaptor.addChild(root_0, BOOL116_tree);
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(BOOL116, DroolsEditorType.BOOLEAN_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:1: normal_lhs_block : ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) ;
    public final normal_lhs_block_return normal_lhs_block() throws RecognitionException {
        normal_lhs_block_return retval = new normal_lhs_block_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        lhs_return lhs117 = null;


        RewriteRuleSubtreeStream stream_lhs=new RewriteRuleSubtreeStream(adaptor,"rule lhs");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:708:2: ( ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:708:4: ( lhs )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:708:4: ( lhs )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==ID||LA40_0==LEFT_PAREN) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:708:4: lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1971);
            	    lhs117=lhs();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_lhs.add(lhs117.getTree());

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
            // 709:2: -> ^( VT_AND_IMPLICIT ( lhs )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:709:5: ^( VT_AND_IMPLICIT ( lhs )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_AND_IMPLICIT, "VT_AND_IMPLICIT"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:709:23: ( lhs )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:1: lhs : lhs_or ;
    public final lhs_return lhs() throws RecognitionException {
        lhs_return retval = new lhs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        lhs_or_return lhs_or118 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:5: ( lhs_or )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:7: lhs_or
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_or_in_lhs1992);
            lhs_or118=lhs_or();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, lhs_or118.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:715:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );
    public final lhs_or_return lhs_or() throws RecognitionException {
        lhs_or_return retval = new lhs_or_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        Token LEFT_PAREN119=null;
        Token RIGHT_PAREN121=null;
        or_key_return or = null;

        or_key_return value = null;

        lhs_and_return lhs_and120 = null;

        lhs_and_return lhs_and122 = null;

        lhs_and_return lhs_and123 = null;


        Object pipe_tree=null;
        Object LEFT_PAREN119_tree=null;
        Object RIGHT_PAREN121_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");
        RewriteRuleSubtreeStream stream_lhs_and=new RewriteRuleSubtreeStream(adaptor,"rule lhs_and");

        	Token orToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:718:3: ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==LEFT_PAREN) ) {
                int LA44_1 = input.LA(2);

                if ( (LA44_1==LEFT_PAREN) ) {
                    alt44=2;
                }
                else if ( (LA44_1==ID) ) {
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
                                new NoViableAltException("715:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 44, 4, input);

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
                                new NoViableAltException("715:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 44, 5, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("715:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 44, 3, input);

                        throw nvae;
                    }

                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("715:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 44, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA44_0==ID) ) {
                alt44=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("715:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:718:5: ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN
                    {
                    LEFT_PAREN119=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or2016); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN119);

                    if ( backtracking==0 ) {
                      	emit(LEFT_PAREN119, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_or_key_in_lhs_or2026);
                    or=or_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_or_key.add(or.getTree());
                    if ( backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    }
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:722:4: ( lhs_and )+
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
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:722:4: lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2034);
                    	    lhs_and120=lhs_and();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_and.add(lhs_and120.getTree());

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

                    RIGHT_PAREN121=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or2040); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN121);

                    if ( backtracking==0 ) {
                      	emit(RIGHT_PAREN121, DroolsEditorType.SYMBOL);	
                    }

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
                    // 724:3: -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:724:6: ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:725:4: ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:725:4: ( lhs_and -> lhs_and )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:725:5: lhs_and
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or2063);
                    lhs_and122=lhs_and();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_and.add(lhs_and122.getTree());

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
                    // 725:13: -> lhs_and
                    {
                        adaptor.addChild(root_0, stream_lhs_and.next());

                    }

                    }

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:3: ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
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
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:5: ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and
                    	    {
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:28: (value= or_key | pipe= DOUBLE_PIPE )
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
                    	            new NoViableAltException("726:28: (value= or_key | pipe= DOUBLE_PIPE )", 42, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt42) {
                    	        case 1 :
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:29: value= or_key
                    	            {
                    	            pushFollow(FOLLOW_or_key_in_lhs_or2085);
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
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:69: pipe= DOUBLE_PIPE
                    	            {
                    	            pipe=(Token)input.LT(1);
                    	            match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_lhs_or2092); if (failed) return retval;
                    	            if ( backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

                    	            if ( backtracking==0 ) {
                    	              orToken = pipe; emit(pipe, DroolsEditorType.SYMBOL);
                    	            }

                    	            }
                    	            break;

                    	    }

                    	    if ( backtracking==0 ) {
                    	      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    	    }
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2103);
                    	    lhs_and123=lhs_and();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_and.add(lhs_and123.getTree());

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
                    	    // 729:3: -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
                    	    {
                    	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:729:6: ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:732:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );
    public final lhs_and_return lhs_and() throws RecognitionException {
        lhs_and_return retval = new lhs_and_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token amper=null;
        Token LEFT_PAREN124=null;
        Token RIGHT_PAREN126=null;
        and_key_return and = null;

        and_key_return value = null;

        lhs_unary_return lhs_unary125 = null;

        lhs_unary_return lhs_unary127 = null;

        lhs_unary_return lhs_unary128 = null;


        Object amper_tree=null;
        Object LEFT_PAREN124_tree=null;
        Object RIGHT_PAREN126_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_AMPER=new RewriteRuleTokenStream(adaptor,"token DOUBLE_AMPER");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_and_key=new RewriteRuleSubtreeStream(adaptor,"rule and_key");
        RewriteRuleSubtreeStream stream_lhs_unary=new RewriteRuleSubtreeStream(adaptor,"rule lhs_unary");

        	Token andToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:3: ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* )
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
                                    new NoViableAltException("732:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 6, input);

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
                                    new NoViableAltException("732:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 7, input);

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
                        case VK_ACTION:
                        case VK_REVERSE:
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
                        case WHEN:
                        case BOOL:
                        case INT:
                        case DOUBLE_PIPE:
                        case DOUBLE_AMPER:
                        case FROM:
                        case OVER:
                        case ACCUMULATE:
                        case INIT:
                        case RESULT:
                        case COLLECT:
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
                                new NoViableAltException("732:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 4, input);

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
                                new NoViableAltException("732:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 5, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("732:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 3, input);

                        throw nvae;
                    }

                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("732:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA48_0==ID) ) {
                alt48=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("732:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:5: ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN
                    {
                    LEFT_PAREN124=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and2144); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN124);

                    if ( backtracking==0 ) {
                      	emit(LEFT_PAREN124, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_and_key_in_lhs_and2154);
                    and=and_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_and_key.add(and.getTree());
                    if ( backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    }
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:739:4: ( lhs_unary )+
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
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:739:4: lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2162);
                    	    lhs_unary125=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary125.getTree());

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

                    RIGHT_PAREN126=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and2168); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN126);

                    if ( backtracking==0 ) {
                      	emit(RIGHT_PAREN126, DroolsEditorType.SYMBOL);	
                    }

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
                    // 741:3: -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:741:6: ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:742:4: ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:742:4: ( lhs_unary -> lhs_unary )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:742:5: lhs_unary
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and2192);
                    lhs_unary127=lhs_unary();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary127.getTree());

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
                    // 742:15: -> lhs_unary
                    {
                        adaptor.addChild(root_0, stream_lhs_unary.next());

                    }

                    }

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:3: ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
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
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:5: ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary
                    	    {
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:30: (value= and_key | amper= DOUBLE_AMPER )
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
                    	            new NoViableAltException("743:30: (value= and_key | amper= DOUBLE_AMPER )", 46, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt46) {
                    	        case 1 :
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:31: value= and_key
                    	            {
                    	            pushFollow(FOLLOW_and_key_in_lhs_and2214);
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
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:73: amper= DOUBLE_AMPER
                    	            {
                    	            amper=(Token)input.LT(1);
                    	            match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_lhs_and2221); if (failed) return retval;
                    	            if ( backtracking==0 ) stream_DOUBLE_AMPER.add(amper);

                    	            if ( backtracking==0 ) {
                    	              andToken = amper; emit(amper, DroolsEditorType.SYMBOL);
                    	            }

                    	            }
                    	            break;

                    	    }

                    	    if ( backtracking==0 ) {
                    	      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    	    }
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2232);
                    	    lhs_unary128=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary128.getTree());

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
                    	    // 746:3: -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
                    	    {
                    	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:746:6: ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:749:1: lhs_unary : ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? ;
    public final lhs_unary_return lhs_unary() throws RecognitionException {
        lhs_unary_return retval = new lhs_unary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN134=null;
        Token RIGHT_PAREN136=null;
        Token SEMICOLON138=null;
        lhs_exist_return lhs_exist129 = null;

        lhs_not_binding_return lhs_not_binding130 = null;

        lhs_not_return lhs_not131 = null;

        lhs_eval_return lhs_eval132 = null;

        lhs_forall_return lhs_forall133 = null;

        lhs_or_return lhs_or135 = null;

        pattern_source_return pattern_source137 = null;


        Object LEFT_PAREN134_tree=null;
        Object RIGHT_PAREN136_tree=null;
        Object SEMICOLON138_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:750:2: ( ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:750:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:750:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )
            int alt49=7;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==ID) ) {
                int LA49_1 = input.LA(2);

                if ( ((validateIdentifierKey(DroolsSoftKeywords.EXISTS))) ) {
                    alt49=1;
                }
                else if ( ((validateNotWithBinding()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                    alt49=2;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                    alt49=3;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                    alt49=4;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.FORALL))) ) {
                    alt49=5;
                }
                else if ( (true) ) {
                    alt49=7;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("750:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )", 49, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA49_0==LEFT_PAREN) ) {
                alt49=6;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("750:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )", 49, 0, input);

                throw nvae;
            }
            switch (alt49) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:750:6: lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary2263);
                    lhs_exist129=lhs_exist();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_exist129.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:751:4: {...}? => lhs_not_binding
                    {
                    if ( !(validateNotWithBinding()) ) {
                        if (backtracking>0) {failed=true; return retval;}
                        throw new FailedPredicateException(input, "lhs_unary", "validateNotWithBinding()");
                    }
                    pushFollow(FOLLOW_lhs_not_binding_in_lhs_unary2271);
                    lhs_not_binding130=lhs_not_binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_not_binding130.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:752:5: lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary2277);
                    lhs_not131=lhs_not();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_not131.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:753:5: lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2283);
                    lhs_eval132=lhs_eval();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_eval132.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:754:5: lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary2289);
                    lhs_forall133=lhs_forall();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_forall133.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:755:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN134=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2295); if (failed) return retval;
                    if ( backtracking==0 ) {
                      	emit(LEFT_PAREN134, DroolsEditorType.SYMBOL); emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary2306);
                    lhs_or135=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_or135.getTree());
                    RIGHT_PAREN136=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2312); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN136_tree = (Object)adaptor.create(RIGHT_PAREN136);
                    adaptor.addChild(root_0, RIGHT_PAREN136_tree);
                    }
                    if ( backtracking==0 ) {
                      	emit(RIGHT_PAREN136, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:5: pattern_source
                    {
                    pushFollow(FOLLOW_pattern_source_in_lhs_unary2320);
                    pattern_source137=pattern_source();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, pattern_source137.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:760:3: ( ( SEMICOLON )=> SEMICOLON )?
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:760:4: ( SEMICOLON )=> SEMICOLON
                    {
                    SEMICOLON138=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_lhs_unary2334); if (failed) return retval;
                    if ( backtracking==0 ) {
                      	emit(SEMICOLON138, DroolsEditorType.SYMBOL);	
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
    // $ANTLR end lhs_unary

    public static class lhs_exist_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_exist
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:763:1: lhs_exist : exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final lhs_exist_return lhs_exist() throws RecognitionException {
        lhs_exist_return retval = new lhs_exist_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN141=null;
        Token RIGHT_PAREN143=null;
        exists_key_return exists_key139 = null;

        lhs_or_return lhs_or140 = null;

        lhs_or_return lhs_or142 = null;

        lhs_pattern_return lhs_pattern144 = null;


        Object LEFT_PAREN141_tree=null;
        Object RIGHT_PAREN143_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_exists_key=new RewriteRuleSubtreeStream(adaptor,"rule exists_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:2: ( exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:4: exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_exists_key_in_lhs_exist2350);
            exists_key139=exists_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_exists_key.add(exists_key139.getTree());
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:766:10: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt51=3;
            alt51 = dfa51.predict(input);
            switch (alt51) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:766:12: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2377);
                    lhs_or140=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or140.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:767:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN141=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist2384); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN141);

                    if ( backtracking==0 ) {
                      	emit(LEFT_PAREN141, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2392);
                    lhs_or142=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or142.getTree());
                    RIGHT_PAREN143=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist2399); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN143);

                    if ( backtracking==0 ) {
                      	emit(RIGHT_PAREN143, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:770:12: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist2414);
                    lhs_pattern144=lhs_pattern();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern144.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: RIGHT_PAREN, lhs_or, lhs_pattern, exists_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 772:10: -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:13: ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_exists_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:26: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.next());

                }
                stream_lhs_or.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:34: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.next());

                }
                stream_lhs_pattern.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:47: ( RIGHT_PAREN )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:775:1: lhs_not_binding : not_key fact_binding -> ^( not_key ^( VT_PATTERN fact_binding ) ) ;
    public final lhs_not_binding_return lhs_not_binding() throws RecognitionException {
        lhs_not_binding_return retval = new lhs_not_binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        not_key_return not_key145 = null;

        fact_binding_return fact_binding146 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:776:2: ( not_key fact_binding -> ^( not_key ^( VT_PATTERN fact_binding ) ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:776:4: not_key fact_binding
            {
            pushFollow(FOLLOW_not_key_in_lhs_not_binding2460);
            not_key145=not_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_not_key.add(not_key145.getTree());
            pushFollow(FOLLOW_fact_binding_in_lhs_not_binding2462);
            fact_binding146=fact_binding();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_fact_binding.add(fact_binding146.getTree());

            // AST REWRITE
            // elements: fact_binding, not_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 777:2: -> ^( not_key ^( VT_PATTERN fact_binding ) )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:777:5: ^( not_key ^( VT_PATTERN fact_binding ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:777:15: ^( VT_PATTERN fact_binding )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:780:1: lhs_not : not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final lhs_not_return lhs_not() throws RecognitionException {
        lhs_not_return retval = new lhs_not_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN149=null;
        Token RIGHT_PAREN151=null;
        not_key_return not_key147 = null;

        lhs_or_return lhs_or148 = null;

        lhs_or_return lhs_or150 = null;

        lhs_pattern_return lhs_pattern152 = null;


        Object LEFT_PAREN149_tree=null;
        Object RIGHT_PAREN151_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:780:9: ( not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:780:11: not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_not_key_in_lhs_not2485);
            not_key147=not_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_not_key.add(not_key147.getTree());
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:782:3: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt52=3;
            alt52 = dfa52.predict(input);
            switch (alt52) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:782:5: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    if ( backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2507);
                    lhs_or148=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or148.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN149=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2514); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN149);

                    if ( backtracking==0 ) {
                      	emit(LEFT_PAREN149, DroolsEditorType.SYMBOL); emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2523);
                    lhs_or150=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or150.getTree());
                    RIGHT_PAREN151=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2529); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN151);

                    if ( backtracking==0 ) {
                      	emit(RIGHT_PAREN151, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:786:6: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2539);
                    lhs_pattern152=lhs_pattern();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern152.getTree());

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
            // 787:10: -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:787:13: ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:787:23: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.next());

                }
                stream_lhs_or.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:787:31: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.next());

                }
                stream_lhs_pattern.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:787:44: ( RIGHT_PAREN )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:790:1: lhs_eval : ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) ;
    public final lhs_eval_return lhs_eval() throws RecognitionException {
        lhs_eval_return retval = new lhs_eval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        eval_key_return ev = null;

        paren_chunk_return pc = null;


        RewriteRuleSubtreeStream stream_eval_key=new RewriteRuleSubtreeStream(adaptor,"rule eval_key");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:791:2: (ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:791:4: ev= eval_key pc= paren_chunk
            {
            pushFollow(FOLLOW_eval_key_in_lhs_eval2578);
            ev=eval_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_eval_key.add(ev.getTree());
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_EVAL);	
            }
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2587);
            pc=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc.getTree());
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }
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
            // 797:3: -> ^( eval_key paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:797:6: ^( eval_key paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:800:1: lhs_forall : forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN ) ;
    public final lhs_forall_return lhs_forall() throws RecognitionException {
        lhs_forall_return retval = new lhs_forall_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN154=null;
        Token RIGHT_PAREN156=null;
        forall_key_return forall_key153 = null;

        lhs_pattern_return lhs_pattern155 = null;


        Object LEFT_PAREN154_tree=null;
        Object RIGHT_PAREN156_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_forall_key=new RewriteRuleSubtreeStream(adaptor,"rule forall_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:801:2: ( forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:801:4: forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN
            {
            pushFollow(FOLLOW_forall_key_in_lhs_forall2614);
            forall_key153=forall_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_forall_key.add(forall_key153.getTree());
            LEFT_PAREN154=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2619); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN154);

            if ( backtracking==0 ) {
              	emit(LEFT_PAREN154, DroolsEditorType.SYMBOL);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:4: ( lhs_pattern )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:4: lhs_pattern
            	    {
            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2627);
            	    lhs_pattern155=lhs_pattern();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern155.getTree());

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

            RIGHT_PAREN156=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall2633); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN156);

            if ( backtracking==0 ) {
              	emit(RIGHT_PAREN156, DroolsEditorType.SYMBOL);	
            }

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
            // 805:3: -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:805:6: ^( forall_key ( lhs_pattern )+ RIGHT_PAREN )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:808:1: pattern_source : lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? ;
    public final pattern_source_return pattern_source() throws RecognitionException {
        pattern_source_return retval = new pattern_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FROM159=null;
        lhs_pattern_return lhs_pattern157 = null;

        over_clause_return over_clause158 = null;

        accumulate_statement_return accumulate_statement160 = null;

        collect_statement_return collect_statement161 = null;

        entrypoint_statement_return entrypoint_statement162 = null;

        from_source_return from_source163 = null;


        Object FROM159_tree=null;

         boolean isFailed = true;	
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:811:2: ( lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:811:4: lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_pattern_in_pattern_source2669);
            lhs_pattern157=lhs_pattern();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, lhs_pattern157.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:812:3: ( over_clause )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==OVER) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:812:3: over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_pattern_source2673);
                    over_clause158=over_clause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, over_clause158.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:813:3: ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==FROM) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:814:4: FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    {
                    FROM159=(Token)input.LT(1);
                    match(input,FROM,FOLLOW_FROM_in_pattern_source2683); if (failed) return retval;
                    if ( backtracking==0 ) {
                    FROM159_tree = (Object)adaptor.create(FROM159);
                    root_0 = (Object)adaptor.becomeRoot(FROM159_tree, root_0);
                    }
                    if ( backtracking==0 ) {
                      	emit(FROM159, DroolsEditorType.KEYWORD);
                      			emit(Location.LOCATION_LHS_FROM);	
                    }
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:817:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    int alt55=4;
                    switch ( input.LA(1) ) {
                    case ACCUMULATE:
                        {
                        alt55=1;
                        }
                        break;
                    case COLLECT:
                        {
                        alt55=2;
                        }
                        break;
                    case ID:
                        {
                        int LA55_3 = input.LA(2);

                        if ( (LA55_3==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT)))) {
                            alt55=3;
                        }
                        else if ( ((LA55_3>=SEMICOLON && LA55_3<=DOT)||LA55_3==END||(LA55_3>=LEFT_PAREN && LA55_3<=RIGHT_PAREN)||(LA55_3>=DOUBLE_PIPE && LA55_3<=DOUBLE_AMPER)||LA55_3==INIT||LA55_3==THEN) ) {
                            alt55=4;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("817:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 55, 3, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("817:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 55, 0, input);

                        throw nvae;
                    }

                    switch (alt55) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:817:14: accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_pattern_source2703);
                            accumulate_statement160=accumulate_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, accumulate_statement160.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:818:15: collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_pattern_source2719);
                            collect_statement161=collect_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, collect_statement161.getTree());

                            }
                            break;
                        case 3 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:819:15: entrypoint_statement
                            {
                            pushFollow(FOLLOW_entrypoint_statement_in_pattern_source2736);
                            entrypoint_statement162=entrypoint_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, entrypoint_statement162.getTree());

                            }
                            break;
                        case 4 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:820:15: from_source
                            {
                            pushFollow(FOLLOW_from_source_in_pattern_source2752);
                            from_source163=from_source();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, from_source163.getTree());

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
            if ( backtracking==0 ) {
               isFailed = false;	
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	if (isEditorInterfaceEnabled && input.LA(3) == EOF && input.LA(1) == ACCUMULATE) {
            			emit(input.LT(1), DroolsEditorType.KEYWORD);
            			emit(input.LT(2), DroolsEditorType.SYMBOL);
            			input.consume();
            			emit(true, Location.LOCATION_LHS_FROM_ACCUMULATE);
            	} else if (isEditorInterfaceEnabled && input.LA(3) == EOF && input.LA(1) == COLLECT) {
            			emit(input.LT(1), DroolsEditorType.KEYWORD);
            			emit(input.LT(2), DroolsEditorType.SYMBOL);
            			input.consume();
            			emit(true, Location.LOCATION_LHS_FROM_COLLECT);
            	}

        }
        return retval;
    }
    // $ANTLR end pattern_source

    public static class over_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start over_clause
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:838:1: over_clause : OVER over_elements ( COMMA over_elements )* ;
    public final over_clause_return over_clause() throws RecognitionException {
        over_clause_return retval = new over_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OVER164=null;
        Token COMMA166=null;
        over_elements_return over_elements165 = null;

        over_elements_return over_elements167 = null;


        Object OVER164_tree=null;
        Object COMMA166_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:839:2: ( OVER over_elements ( COMMA over_elements )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:839:4: OVER over_elements ( COMMA over_elements )*
            {
            root_0 = (Object)adaptor.nil();

            OVER164=(Token)input.LT(1);
            match(input,OVER,FOLLOW_OVER_in_over_clause2784); if (failed) return retval;
            if ( backtracking==0 ) {
            OVER164_tree = (Object)adaptor.create(OVER164);
            root_0 = (Object)adaptor.becomeRoot(OVER164_tree, root_0);
            }
            if ( backtracking==0 ) {
              	emit(OVER164, DroolsEditorType.KEYWORD);	
            }
            pushFollow(FOLLOW_over_elements_in_over_clause2789);
            over_elements165=over_elements();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, over_elements165.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:840:4: ( COMMA over_elements )*
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:840:5: COMMA over_elements
            	    {
            	    COMMA166=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_over_clause2796); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	      	emit(COMMA166, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_over_elements_in_over_clause2801);
            	    over_elements167=over_elements();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, over_elements167.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:843:1: over_elements : id1= ID COLON id2= ID paren_chunk -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk ) ;
    public final over_elements_return over_elements() throws RecognitionException {
        over_elements_return retval = new over_elements_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token id2=null;
        Token COLON168=null;
        paren_chunk_return paren_chunk169 = null;


        Object id1_tree=null;
        Object id2_tree=null;
        Object COLON168_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:844:2: (id1= ID COLON id2= ID paren_chunk -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:844:4: id1= ID COLON id2= ID paren_chunk
            {
            id1=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_over_elements2816); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id1);

            if ( backtracking==0 ) {
              	emit(id1, DroolsEditorType.IDENTIFIER);	
            }
            COLON168=(Token)input.LT(1);
            match(input,COLON,FOLLOW_COLON_in_over_elements2823); if (failed) return retval;
            if ( backtracking==0 ) stream_COLON.add(COLON168);

            if ( backtracking==0 ) {
              	emit(COLON168, DroolsEditorType.SYMBOL);	
            }
            id2=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_over_elements2832); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id2);

            if ( backtracking==0 ) {
              	emit(id2, DroolsEditorType.IDENTIFIER);	
            }
            pushFollow(FOLLOW_paren_chunk_in_over_elements2839);
            paren_chunk169=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk169.getTree());

            // AST REWRITE
            // elements: id1, paren_chunk, id2
            // token labels: id1, id2
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_id1=new RewriteRuleTokenStream(adaptor,"token id1",id1);
            RewriteRuleTokenStream stream_id2=new RewriteRuleTokenStream(adaptor,"token id2",id2);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 848:2: -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:848:5: ^( VT_BEHAVIOR $id1 $id2 paren_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_BEHAVIOR, "VT_BEHAVIOR"), root_1);

                adaptor.addChild(root_1, stream_id1.next());
                adaptor.addChild(root_1, stream_id2.next());
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:851:1: accumulate_statement : ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) ;
    public final accumulate_statement_return accumulate_statement() throws RecognitionException {
        accumulate_statement_return retval = new accumulate_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ACCUMULATE170=null;
        Token LEFT_PAREN171=null;
        Token COMMA173=null;
        Token RIGHT_PAREN176=null;
        lhs_or_return lhs_or172 = null;

        accumulate_init_clause_return accumulate_init_clause174 = null;

        accumulate_id_clause_return accumulate_id_clause175 = null;


        Object ACCUMULATE170_tree=null;
        Object LEFT_PAREN171_tree=null;
        Object COMMA173_tree=null;
        Object RIGHT_PAREN176_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_ACCUMULATE=new RewriteRuleTokenStream(adaptor,"token ACCUMULATE");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_accumulate_init_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_init_clause");
        RewriteRuleSubtreeStream stream_accumulate_id_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_id_clause");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:852:2: ( ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:852:4: ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN
            {
            ACCUMULATE170=(Token)input.LT(1);
            match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement2865); if (failed) return retval;
            if ( backtracking==0 ) stream_ACCUMULATE.add(ACCUMULATE170);

            if ( backtracking==0 ) {
              	emit(ACCUMULATE170, DroolsEditorType.KEYWORD);	
            }
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_ACCUMULATE);	
            }
            LEFT_PAREN171=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2874); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN171);

            if ( backtracking==0 ) {
              	emit(LEFT_PAREN171, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_lhs_or_in_accumulate_statement2882);
            lhs_or172=lhs_or();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_lhs_or.add(lhs_or172.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:856:3: ( COMMA )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==COMMA) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:856:3: COMMA
                    {
                    COMMA173=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2887); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(COMMA173);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(COMMA173, DroolsEditorType.SYMBOL);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:3: ( accumulate_init_clause | accumulate_id_clause )
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==INIT) ) {
                alt59=1;
            }
            else if ( (LA59_0==ID) ) {
                alt59=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("857:3: ( accumulate_init_clause | accumulate_id_clause )", 59, 0, input);

                throw nvae;
            }
            switch (alt59) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:5: accumulate_init_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_statement2897);
                    accumulate_init_clause174=accumulate_init_clause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accumulate_init_clause.add(accumulate_init_clause174.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:858:5: accumulate_id_clause
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_statement2903);
                    accumulate_id_clause175=accumulate_id_clause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accumulate_id_clause.add(accumulate_id_clause175.getTree());

                    }
                    break;

            }

            RIGHT_PAREN176=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2911); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN176);

            if ( backtracking==0 ) {
              	emit(RIGHT_PAREN176, DroolsEditorType.SYMBOL);	
            }
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }

            // AST REWRITE
            // elements: lhs_or, accumulate_init_clause, RIGHT_PAREN, ACCUMULATE, accumulate_id_clause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 862:3: -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:862:6: ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ACCUMULATE.next(), root_1);

                adaptor.addChild(root_1, stream_lhs_or.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:862:26: ( accumulate_init_clause )?
                if ( stream_accumulate_init_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_accumulate_init_clause.next());

                }
                stream_accumulate_init_clause.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:862:50: ( accumulate_id_clause )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:866:1: accumulate_init_clause : INIT pc1= paren_chunk (cm1= COMMA )? action_key pc2= paren_chunk (cm2= COMMA )? ( reverse_key pc3= paren_chunk (cm3= COMMA )? )? RESULT pc4= paren_chunk -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( INIT $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( RESULT $pc4) ) ;
    public final accumulate_init_clause_return accumulate_init_clause() throws RecognitionException {
        accumulate_init_clause_return retval = new accumulate_init_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token cm1=null;
        Token cm2=null;
        Token cm3=null;
        Token INIT177=null;
        Token RESULT180=null;
        paren_chunk_return pc1 = null;

        paren_chunk_return pc2 = null;

        paren_chunk_return pc3 = null;

        paren_chunk_return pc4 = null;

        action_key_return action_key178 = null;

        reverse_key_return reverse_key179 = null;


        Object cm1_tree=null;
        Object cm2_tree=null;
        Object cm3_tree=null;
        Object INIT177_tree=null;
        Object RESULT180_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_INIT=new RewriteRuleTokenStream(adaptor,"token INIT");
        RewriteRuleTokenStream stream_RESULT=new RewriteRuleTokenStream(adaptor,"token RESULT");
        RewriteRuleSubtreeStream stream_reverse_key=new RewriteRuleSubtreeStream(adaptor,"rule reverse_key");
        RewriteRuleSubtreeStream stream_action_key=new RewriteRuleSubtreeStream(adaptor,"rule action_key");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:867:2: ( INIT pc1= paren_chunk (cm1= COMMA )? action_key pc2= paren_chunk (cm2= COMMA )? ( reverse_key pc3= paren_chunk (cm3= COMMA )? )? RESULT pc4= paren_chunk -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( INIT $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( RESULT $pc4) ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:867:4: INIT pc1= paren_chunk (cm1= COMMA )? action_key pc2= paren_chunk (cm2= COMMA )? ( reverse_key pc3= paren_chunk (cm3= COMMA )? )? RESULT pc4= paren_chunk
            {
            INIT177=(Token)input.LT(1);
            match(input,INIT,FOLLOW_INIT_in_accumulate_init_clause2946); if (failed) return retval;
            if ( backtracking==0 ) stream_INIT.add(INIT177);

            if ( backtracking==0 ) {
              	emit(INIT177, DroolsEditorType.KEYWORD);	
            }
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT);	
            }
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2957);
            pc1=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc1.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:869:22: (cm1= COMMA )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==COMMA) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:869:22: cm1= COMMA
                    {
                    cm1=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2961); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(cm1);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(cm1, DroolsEditorType.SYMBOL);	
            }
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION);	
            }
            pushFollow(FOLLOW_action_key_in_accumulate_init_clause2972);
            action_key178=action_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action_key.add(action_key178.getTree());
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2976);
            pc2=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc2.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:33: (cm2= COMMA )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==COMMA) ) {
                alt61=1;
            }
            switch (alt61) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:33: cm2= COMMA
                    {
                    cm2=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2980); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(cm2);


                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(cm2, DroolsEditorType.SYMBOL);	
            }
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:2: ( reverse_key pc3= paren_chunk (cm3= COMMA )? )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.REVERSE)))) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:4: reverse_key pc3= paren_chunk (cm3= COMMA )?
                    {
                    pushFollow(FOLLOW_reverse_key_in_accumulate_init_clause2993);
                    reverse_key179=reverse_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_reverse_key.add(reverse_key179.getTree());
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2997);
                    pc3=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(pc3.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:35: (cm3= COMMA )?
                    int alt62=2;
                    int LA62_0 = input.LA(1);

                    if ( (LA62_0==COMMA) ) {
                        alt62=1;
                    }
                    switch (alt62) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:35: cm3= COMMA
                            {
                            cm3=(Token)input.LT(1);
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause3001); if (failed) return retval;
                            if ( backtracking==0 ) stream_COMMA.add(cm3);


                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                      	emit(cm3, DroolsEditorType.SYMBOL);	
                    }
                    if ( backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT);	
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT);	
            }
            RESULT180=(Token)input.LT(1);
            match(input,RESULT,FOLLOW_RESULT_in_accumulate_init_clause3017); if (failed) return retval;
            if ( backtracking==0 ) stream_RESULT.add(RESULT180);

            if ( backtracking==0 ) {
              	emit(RESULT180, DroolsEditorType.KEYWORD);	
            }
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause3023);
            pc4=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc4.getTree());

            // AST REWRITE
            // elements: pc1, pc4, action_key, pc2, INIT, RESULT, pc3, reverse_key
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
            // 878:2: -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( INIT $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( RESULT $pc4) )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:5: ^( VT_ACCUMULATE_INIT_CLAUSE ^( INIT $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( RESULT $pc4) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ACCUMULATE_INIT_CLAUSE, "VT_ACCUMULATE_INIT_CLAUSE"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:33: ^( INIT $pc1)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_INIT.next(), root_2);

                adaptor.addChild(root_2, stream_pc1.next());

                adaptor.addChild(root_1, root_2);
                }
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:46: ^( action_key $pc2)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_action_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc2.next());

                adaptor.addChild(root_1, root_2);
                }
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:65: ( ^( reverse_key $pc3) )?
                if ( stream_pc3.hasNext()||stream_reverse_key.hasNext() ) {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:65: ^( reverse_key $pc3)
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_reverse_key.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_pc3.next());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_pc3.reset();
                stream_reverse_key.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:86: ^( RESULT $pc4)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_RESULT.next(), root_2);

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

            	if (isEditorInterfaceEnabled && input.LA(1) == EOF) {
            		int lastPosition = getLastIntegerValue(getActiveSentence().getContent());
            		if (lastPosition == Location.LOCATION_LHS_FROM_ACCUMULATE_INIT) {
            			emit(true, Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE);
            		} else if (lastPosition == Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION) {
            			emit(true, Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE);
            		} else if (lastPosition == Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE) {
            			emit(true, Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE);
            		} else if (lastPosition == Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT) {
            			emit(true, Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE);
            		}
            	}

        }
        return retval;
    }
    // $ANTLR end accumulate_init_clause

    public static class accumulate_id_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start accumulate_id_clause
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:895:1: accumulate_id_clause : ID paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) ;
    public final accumulate_id_clause_return accumulate_id_clause() throws RecognitionException {
        accumulate_id_clause_return retval = new accumulate_id_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID181=null;
        paren_chunk_return paren_chunk182 = null;


        Object ID181_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:896:2: ( ID paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:896:4: ID paren_chunk
            {
            ID181=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accumulate_id_clause3074); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID181);

            if ( backtracking==0 ) {
              	emit(ID181, DroolsEditorType.IDENTIFIER);	
            }
            pushFollow(FOLLOW_paren_chunk_in_accumulate_id_clause3080);
            paren_chunk182=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk182.getTree());

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
            // 898:2: -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:898:5: ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:901:1: collect_statement : COLLECT LEFT_PAREN pattern_source RIGHT_PAREN -> ^( COLLECT pattern_source RIGHT_PAREN ) ;
    public final collect_statement_return collect_statement() throws RecognitionException {
        collect_statement_return retval = new collect_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLLECT183=null;
        Token LEFT_PAREN184=null;
        Token RIGHT_PAREN186=null;
        pattern_source_return pattern_source185 = null;


        Object COLLECT183_tree=null;
        Object LEFT_PAREN184_tree=null;
        Object RIGHT_PAREN186_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleTokenStream stream_COLLECT=new RewriteRuleTokenStream(adaptor,"token COLLECT");
        RewriteRuleSubtreeStream stream_pattern_source=new RewriteRuleSubtreeStream(adaptor,"rule pattern_source");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:902:2: ( COLLECT LEFT_PAREN pattern_source RIGHT_PAREN -> ^( COLLECT pattern_source RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:902:4: COLLECT LEFT_PAREN pattern_source RIGHT_PAREN
            {
            COLLECT183=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement3102); if (failed) return retval;
            if ( backtracking==0 ) stream_COLLECT.add(COLLECT183);

            if ( backtracking==0 ) {
              	emit(COLLECT183, DroolsEditorType.KEYWORD);	
            }
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_COLLECT);	
            }
            LEFT_PAREN184=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement3111); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN184);

            if ( backtracking==0 ) {
              	emit(LEFT_PAREN184, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_pattern_source_in_collect_statement3118);
            pattern_source185=pattern_source();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_pattern_source.add(pattern_source185.getTree());
            RIGHT_PAREN186=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement3123); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN186);

            if ( backtracking==0 ) {
              	emit(RIGHT_PAREN186, DroolsEditorType.SYMBOL);	
            }
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }

            // AST REWRITE
            // elements: RIGHT_PAREN, pattern_source, COLLECT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 908:2: -> ^( COLLECT pattern_source RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:908:5: ^( COLLECT pattern_source RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_COLLECT.next(), root_1);

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:911:1: entrypoint_statement : entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) ;
    public final entrypoint_statement_return entrypoint_statement() throws RecognitionException {
        entrypoint_statement_return retval = new entrypoint_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        entry_point_key_return entry_point_key187 = null;

        entrypoint_id_return entrypoint_id188 = null;


        RewriteRuleSubtreeStream stream_entrypoint_id=new RewriteRuleSubtreeStream(adaptor,"rule entrypoint_id");
        RewriteRuleSubtreeStream stream_entry_point_key=new RewriteRuleSubtreeStream(adaptor,"rule entry_point_key");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:912:2: ( entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:912:4: entry_point_key entrypoint_id
            {
            pushFollow(FOLLOW_entry_point_key_in_entrypoint_statement3150);
            entry_point_key187=entry_point_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_entry_point_key.add(entry_point_key187.getTree());
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_COLLECT);	
            }
            pushFollow(FOLLOW_entrypoint_id_in_entrypoint_statement3158);
            entrypoint_id188=entrypoint_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_entrypoint_id.add(entrypoint_id188.getTree());
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }

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
            // 916:2: -> ^( entry_point_key entrypoint_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:916:5: ^( entry_point_key entrypoint_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:919:1: entrypoint_id : (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] );
    public final entrypoint_id_return entrypoint_id() throws RecognitionException {
        entrypoint_id_return retval = new entrypoint_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:920:2: (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] )
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
                    new NoViableAltException("919:1: entrypoint_id : (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] );", 64, 0, input);

                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:920:5: value= ID
                    {
                    value=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_entrypoint_id3184); if (failed) return retval;
                    if ( backtracking==0 ) stream_ID.add(value);

                    if ( backtracking==0 ) {
                      	emit(value, DroolsEditorType.IDENTIFIER);	
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
                    // 921:3: -> VT_ENTRYPOINT_ID[$value]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_ENTRYPOINT_ID, value));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:922:5: value= STRING
                    {
                    value=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_entrypoint_id3201); if (failed) return retval;
                    if ( backtracking==0 ) stream_STRING.add(value);

                    if ( backtracking==0 ) {
                      	emit(value, DroolsEditorType.IDENTIFIER);	
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
                    // 923:3: -> VT_ENTRYPOINT_ID[$value]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:926:1: from_source : ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) ;
    public final from_source_return from_source() throws RecognitionException {
        from_source_return retval = new from_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID189=null;
        paren_chunk_return args = null;

        expression_chain_return expression_chain190 = null;


        Object ID189_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:927:2: ( ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:927:4: ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )?
            {
            ID189=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_from_source3221); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID189);

            if ( backtracking==0 ) {
              	emit(ID189, DroolsEditorType.IDENTIFIER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:928:3: ( ( LEFT_PAREN )=>args= paren_chunk )?
            int alt65=2;
            alt65 = dfa65.predict(input);
            switch (alt65) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:928:5: ( LEFT_PAREN )=>args= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source3236);
                    args=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(args.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:929:3: ( expression_chain )?
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==DOT) ) {
                alt66=1;
            }
            switch (alt66) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:929:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source3243);
                    expression_chain190=expression_chain();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expression_chain.add(expression_chain190.getTree());

                    }
                    break;

            }

            if ( backtracking==0 ) {
              	if ( input.LA(1) == EOF && input.get(input.index() - 1).getType() == WS) {
              			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);
              		} else if ( input.LA(1) != EOF ) {
              			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);
              		}	
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
            // 935:2: -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:935:5: ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FROM_SOURCE, "VT_FROM_SOURCE"), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:935:25: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.next());

                }
                stream_paren_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:935:38: ( expression_chain )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:938:1: expression_chain : DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) ;
    public final expression_chain_return expression_chain() throws RecognitionException {
        expression_chain_return retval = new expression_chain_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT191=null;
        Token ID192=null;
        square_chunk_return square_chunk193 = null;

        paren_chunk_return paren_chunk194 = null;

        expression_chain_return expression_chain195 = null;


        Object DOT191_tree=null;
        Object ID192_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:939:2: ( DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:940:3: DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )?
            {
            DOT191=(Token)input.LT(1);
            match(input,DOT,FOLLOW_DOT_in_expression_chain3276); if (failed) return retval;
            if ( backtracking==0 ) stream_DOT.add(DOT191);

            if ( backtracking==0 ) {
              	emit(DOT191, DroolsEditorType.IDENTIFIER);	
            }
            ID192=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_expression_chain3283); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID192);

            if ( backtracking==0 ) {
              	emit(ID192, DroolsEditorType.IDENTIFIER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:942:4: ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )?
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:943:6: ( LEFT_SQUARE )=> square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain3305);
                    square_chunk193=square_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_square_chunk.add(square_chunk193.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:945:6: ( LEFT_PAREN )=> paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain3327);
                    paren_chunk194=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk194.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:947:4: ( expression_chain )?
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==DOT) ) {
                alt68=1;
            }
            switch (alt68) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:947:4: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain3338);
                    expression_chain195=expression_chain();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expression_chain.add(expression_chain195.getTree());

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
            // 948:4: -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:948:7: ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_EXPRESSION_CHAIN, DOT191), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:948:38: ( square_chunk )?
                if ( stream_square_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_square_chunk.next());

                }
                stream_square_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:948:52: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.next());

                }
                stream_paren_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:948:65: ( expression_chain )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:951:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );
    public final lhs_pattern_return lhs_pattern() throws RecognitionException {
        lhs_pattern_return retval = new lhs_pattern_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        fact_binding_return fact_binding196 = null;

        fact_return fact197 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:952:2: ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) )
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
                        new NoViableAltException("951:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );", 69, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("951:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );", 69, 0, input);

                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:952:4: fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern3371);
                    fact_binding196=fact_binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact_binding.add(fact_binding196.getTree());

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
                    // 952:17: -> ^( VT_PATTERN fact_binding )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:952:20: ^( VT_PATTERN fact_binding )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:953:4: fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern3384);
                    fact197=fact();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact.add(fact197.getTree());

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
                    // 953:9: -> ^( VT_PATTERN fact )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:953:12: ^( VT_PATTERN fact )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:956:1: fact_binding : label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) ;
    public final fact_binding_return fact_binding() throws RecognitionException {
        fact_binding_return retval = new fact_binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN200=null;
        Token RIGHT_PAREN202=null;
        label_return label198 = null;

        fact_return fact199 = null;

        fact_binding_expression_return fact_binding_expression201 = null;


        Object LEFT_PAREN200_tree=null;
        Object RIGHT_PAREN202_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        RewriteRuleSubtreeStream stream_fact_binding_expression=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding_expression");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:957:3: ( label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:957:5: label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            {
            pushFollow(FOLLOW_label_in_fact_binding3404);
            label198=label();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_label.add(label198.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:958:3: ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
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
                    new NoViableAltException("958:3: ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:958:5: fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding3410);
                    fact199=fact();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact.add(fact199.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:959:6: LEFT_PAREN fact_binding_expression RIGHT_PAREN
                    {
                    LEFT_PAREN200=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding3417); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN200);

                    if ( backtracking==0 ) {
                      	emit(LEFT_PAREN200, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_fact_binding_expression_in_fact_binding3425);
                    fact_binding_expression201=fact_binding_expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact_binding_expression.add(fact_binding_expression201.getTree());
                    RIGHT_PAREN202=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding3433); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN202);

                    if ( backtracking==0 ) {
                      	emit(RIGHT_PAREN202, DroolsEditorType.SYMBOL);	
                    }

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
            // 963:3: -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:6: ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FACT_BINDING, "VT_FACT_BINDING"), root_1);

                adaptor.addChild(root_1, stream_label.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:30: ( fact )?
                if ( stream_fact.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact.next());

                }
                stream_fact.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:36: ( fact_binding_expression )?
                if ( stream_fact_binding_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact_binding_expression.next());

                }
                stream_fact_binding_expression.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:61: ( RIGHT_PAREN )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:966:1: fact_binding_expression : ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* ;
    public final fact_binding_expression_return fact_binding_expression() throws RecognitionException {
        fact_binding_expression_return retval = new fact_binding_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        or_key_return value = null;

        fact_return fact203 = null;

        fact_return fact204 = null;


        Object pipe_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");

        	Token orToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:3: ( ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:5: ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:5: ( fact -> fact )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:6: fact
            {
            pushFollow(FOLLOW_fact_in_fact_binding_expression3474);
            fact203=fact();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_fact.add(fact203.getTree());

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
            // 969:11: -> fact
            {
                adaptor.addChild(root_0, stream_fact.next());

            }

            }

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:20: ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:22: (value= or_key | pipe= DOUBLE_PIPE ) fact
            	    {
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:22: (value= or_key | pipe= DOUBLE_PIPE )
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
            	            new NoViableAltException("969:22: (value= or_key | pipe= DOUBLE_PIPE )", 71, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt71) {
            	        case 1 :
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:23: value= or_key
            	            {
            	            pushFollow(FOLLOW_or_key_in_fact_binding_expression3486);
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
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:62: pipe= DOUBLE_PIPE
            	            {
            	            pipe=(Token)input.LT(1);
            	            match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3492); if (failed) return retval;
            	            if ( backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

            	            if ( backtracking==0 ) {
            	              orToken = pipe;
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_fact_in_fact_binding_expression3497);
            	    fact204=fact();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_fact.add(fact204.getTree());

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
            	    // 970:3: -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
            	    {
            	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:970:6: ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:973:1: fact : pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) ;
    public final fact_return fact() throws RecognitionException {
        fact_return retval = new fact_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN206=null;
        Token RIGHT_PAREN208=null;
        pattern_type_return pattern_type205 = null;

        constraints_return constraints207 = null;


        Object LEFT_PAREN206_tree=null;
        Object RIGHT_PAREN208_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_pattern_type=new RewriteRuleSubtreeStream(adaptor,"rule pattern_type");
        RewriteRuleSubtreeStream stream_constraints=new RewriteRuleSubtreeStream(adaptor,"rule constraints");
         pushParaphrases(DroolsParaphraseTypes.PATTERN); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:976:2: ( pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:976:4: pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN
            {
            pushFollow(FOLLOW_pattern_type_in_fact3537);
            pattern_type205=pattern_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_pattern_type.add(pattern_type205.getTree());
            LEFT_PAREN206=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact3542); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN206);

            if ( backtracking==0 ) {
              	emit(LEFT_PAREN206, DroolsEditorType.SYMBOL);	
            }
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:979:4: ( constraints )?
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==ID||LA73_0==LEFT_PAREN) ) {
                alt73=1;
            }
            switch (alt73) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:979:4: constraints
                    {
                    pushFollow(FOLLOW_constraints_in_fact3553);
                    constraints207=constraints();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_constraints.add(constraints207.getTree());

                    }
                    break;

            }

            RIGHT_PAREN208=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact3559); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN208);

            if ( backtracking==0 ) {
              		
            }
            if ( backtracking==0 ) {
              	if (RIGHT_PAREN208.getText().equals(")") ){ //WORKAROUND FOR ANTLR BUG!
              			emit(RIGHT_PAREN208, DroolsEditorType.SYMBOL);
              			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);
              		}	
            }

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
            // 985:2: -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:985:5: ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FACT, "VT_FACT"), root_1);

                adaptor.addChild(root_1, stream_pattern_type.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:985:28: ( constraints )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:988:1: constraints : constraint ( COMMA constraint )* ;
    public final constraints_return constraints() throws RecognitionException {
        constraints_return retval = new constraints_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA210=null;
        constraint_return constraint209 = null;

        constraint_return constraint211 = null;


        Object COMMA210_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:989:2: ( constraint ( COMMA constraint )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:989:4: constraint ( COMMA constraint )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_in_constraints3589);
            constraint209=constraint();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, constraint209.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:989:15: ( COMMA constraint )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==COMMA) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:989:17: COMMA constraint
            	    {
            	    COMMA210=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_constraints3593); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	      	emit(COMMA210, DroolsEditorType.SYMBOL);
            	      		emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	
            	    }
            	    pushFollow(FOLLOW_constraint_in_constraints3600);
            	    constraint211=constraint();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, constraint211.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:994:1: constraint : or_constr ;
    public final constraint_return constraint() throws RecognitionException {
        constraint_return retval = new constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        or_constr_return or_constr212 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:995:2: ( or_constr )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:995:4: or_constr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_or_constr_in_constraint3614);
            or_constr212=or_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, or_constr212.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:998:1: or_constr : and_constr ( DOUBLE_PIPE and_constr )* ;
    public final or_constr_return or_constr() throws RecognitionException {
        or_constr_return retval = new or_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE214=null;
        and_constr_return and_constr213 = null;

        and_constr_return and_constr215 = null;


        Object DOUBLE_PIPE214_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:2: ( and_constr ( DOUBLE_PIPE and_constr )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:4: and_constr ( DOUBLE_PIPE and_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_constr_in_or_constr3625);
            and_constr213=and_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, and_constr213.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:15: ( DOUBLE_PIPE and_constr )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==DOUBLE_PIPE) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:17: DOUBLE_PIPE and_constr
            	    {
            	    DOUBLE_PIPE214=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr3629); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_PIPE214_tree = (Object)adaptor.create(DOUBLE_PIPE214);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE214_tree, root_0);
            	    }
            	    if ( backtracking==0 ) {
            	      	emit(DOUBLE_PIPE214, DroolsEditorType.SYMBOL);
            	      		emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);	
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr3636);
            	    and_constr215=and_constr();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, and_constr215.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1004:1: and_constr : unary_constr ( DOUBLE_AMPER unary_constr )* ;
    public final and_constr_return and_constr() throws RecognitionException {
        and_constr_return retval = new and_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER217=null;
        unary_constr_return unary_constr216 = null;

        unary_constr_return unary_constr218 = null;


        Object DOUBLE_AMPER217_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1005:2: ( unary_constr ( DOUBLE_AMPER unary_constr )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1005:4: unary_constr ( DOUBLE_AMPER unary_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_constr_in_and_constr3651);
            unary_constr216=unary_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, unary_constr216.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1005:17: ( DOUBLE_AMPER unary_constr )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==DOUBLE_AMPER) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1005:19: DOUBLE_AMPER unary_constr
            	    {
            	    DOUBLE_AMPER217=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr3655); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_AMPER217_tree = (Object)adaptor.create(DOUBLE_AMPER217);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER217_tree, root_0);
            	    }
            	    if ( backtracking==0 ) {
            	      	emit(DOUBLE_AMPER217, DroolsEditorType.SYMBOL);
            	      		emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);	
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr3662);
            	    unary_constr218=unary_constr();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, unary_constr218.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1010:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );
    public final unary_constr_return unary_constr() throws RecognitionException {
        unary_constr_return retval = new unary_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN222=null;
        Token RIGHT_PAREN224=null;
        eval_key_return eval_key219 = null;

        paren_chunk_return paren_chunk220 = null;

        field_constraint_return field_constraint221 = null;

        or_constr_return or_constr223 = null;


        Object LEFT_PAREN222_tree=null;
        Object RIGHT_PAREN224_tree=null;

         boolean isFailed = true;	
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1014:2: ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN )
            int alt77=3;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==ID) ) {
                int LA77_1 = input.LA(2);

                if ( ((LA77_1>=ID && LA77_1<=DOT)||LA77_1==COLON||(LA77_1>=EQUAL && LA77_1<=GRAVE_ACCENT)||LA77_1==LEFT_SQUARE) ) {
                    alt77=2;
                }
                else if ( (LA77_1==LEFT_PAREN) ) {
                    int LA77_14 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                        alt77=1;
                    }
                    else if ( (true) ) {
                        alt77=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("1010:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 77, 14, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("1010:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 77, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA77_0==LEFT_PAREN) ) {
                alt77=3;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1010:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 77, 0, input);

                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1014:4: eval_key paren_chunk
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_eval_key_in_unary_constr3695);
                    eval_key219=eval_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(eval_key219.getTree(), root_0);
                    pushFollow(FOLLOW_paren_chunk_in_unary_constr3698);
                    paren_chunk220=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk220.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1015:4: field_constraint
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_field_constraint_in_unary_constr3703);
                    field_constraint221=field_constraint();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, field_constraint221.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1016:5: LEFT_PAREN or_constr RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN222=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr3709); if (failed) return retval;
                    if ( backtracking==0 ) {
                      	emit(LEFT_PAREN222, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_or_constr_in_unary_constr3719);
                    or_constr223=or_constr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, or_constr223.getTree());
                    RIGHT_PAREN224=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr3724); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN224_tree = (Object)adaptor.create(RIGHT_PAREN224);
                    adaptor.addChild(root_0, RIGHT_PAREN224_tree);
                    }
                    if ( backtracking==0 ) {
                      	emit(RIGHT_PAREN224, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( backtracking==0 ) {
               isFailed = false;	
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
             
            	if (isEditorInterfaceEnabled && isFailed && input.LA(2) == EOF && input.LA(1) == ID) {
            		emit(input.LT(1), DroolsEditorType.IDENTIFIER);
            		input.consume();
            		emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	}	
        }
        return retval;
    }
    // $ANTLR end unary_constr

    public static class field_constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start field_constraint
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1027:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );
    public final field_constraint_return field_constraint() throws RecognitionException {
        field_constraint_return retval = new field_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token arw=null;
        label_return label225 = null;

        accessor_path_return accessor_path226 = null;

        or_restr_connective_return or_restr_connective227 = null;

        paren_chunk_return paren_chunk228 = null;

        accessor_path_return accessor_path229 = null;

        or_restr_connective_return or_restr_connective230 = null;


        Object arw_tree=null;
        RewriteRuleTokenStream stream_ARROW=new RewriteRuleTokenStream(adaptor,"token ARROW");
        RewriteRuleSubtreeStream stream_accessor_path=new RewriteRuleSubtreeStream(adaptor,"rule accessor_path");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        RewriteRuleSubtreeStream stream_or_restr_connective=new RewriteRuleSubtreeStream(adaptor,"rule or_restr_connective");

        	boolean isArrow = false;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1030:3: ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) )
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
                        new NoViableAltException("1027:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );", 79, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1027:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );", 79, 0, input);

                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1030:5: label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )?
                    {
                    pushFollow(FOLLOW_label_in_field_constraint3744);
                    label225=label();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_label.add(label225.getTree());
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3746);
                    accessor_path226=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accessor_path.add(accessor_path226.getTree());
                    if ( backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);	
                    }
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1032:3: ( or_restr_connective | arw= ARROW paren_chunk )?
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
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1032:5: or_restr_connective
                            {
                            pushFollow(FOLLOW_or_restr_connective_in_field_constraint3756);
                            or_restr_connective227=or_restr_connective();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_or_restr_connective.add(or_restr_connective227.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1032:27: arw= ARROW paren_chunk
                            {
                            arw=(Token)input.LT(1);
                            match(input,ARROW,FOLLOW_ARROW_in_field_constraint3762); if (failed) return retval;
                            if ( backtracking==0 ) stream_ARROW.add(arw);

                            if ( backtracking==0 ) {
                              	emit(arw, DroolsEditorType.SYMBOL);	
                            }
                            pushFollow(FOLLOW_paren_chunk_in_field_constraint3766);
                            paren_chunk228=paren_chunk();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk228.getTree());
                            if ( backtracking==0 ) {
                              isArrow = true;
                            }

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: accessor_path, accessor_path, label, or_restr_connective, label, paren_chunk
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1033:3: -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )?
                    if (isArrow) {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1033:17: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.next());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1033:39: ^( VT_FIELD accessor_path )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.next());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1033:66: ( ^( VK_EVAL[$arw] paren_chunk ) )?
                        if ( stream_paren_chunk.hasNext() ) {
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1033:66: ^( VK_EVAL[$arw] paren_chunk )
                            {
                            Object root_1 = (Object)adaptor.nil();
                            root_1 = (Object)adaptor.becomeRoot(adaptor.create(VK_EVAL, arw), root_1);

                            adaptor.addChild(root_1, stream_paren_chunk.next());

                            adaptor.addChild(root_0, root_1);
                            }

                        }
                        stream_paren_chunk.reset();

                    }
                    else // 1034:3: -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1034:6: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.next());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1034:28: ^( VT_FIELD accessor_path ( or_restr_connective )? )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.next());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1034:53: ( or_restr_connective )?
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1035:4: accessor_path or_restr_connective
                    {
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3820);
                    accessor_path229=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accessor_path.add(accessor_path229.getTree());
                    if ( backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);	
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint3824);
                    or_restr_connective230=or_restr_connective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_or_restr_connective.add(or_restr_connective230.getTree());

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
                    // 1036:3: -> ^( VT_FIELD accessor_path or_restr_connective )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1036:6: ^( VT_FIELD accessor_path or_restr_connective )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1039:1: label : value= ID COLON -> VT_LABEL[$value] ;
    public final label_return label() throws RecognitionException {
        label_return retval = new label_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;
        Token COLON231=null;

        Object value_tree=null;
        Object COLON231_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:2: (value= ID COLON -> VT_LABEL[$value] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:4: value= ID COLON
            {
            value=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_label3849); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(value);

            if ( backtracking==0 ) {
              	emit(value, DroolsEditorType.IDENTIFIER);	
            }
            COLON231=(Token)input.LT(1);
            match(input,COLON,FOLLOW_COLON_in_label3856); if (failed) return retval;
            if ( backtracking==0 ) stream_COLON.add(COLON231);

            if ( backtracking==0 ) {
              	emit(COLON231, DroolsEditorType.SYMBOL);	
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
            // 1042:3: -> VT_LABEL[$value]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1045:1: or_restr_connective : and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* ;
    public final or_restr_connective_return or_restr_connective() throws RecognitionException {
        or_restr_connective_return retval = new or_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE233=null;
        and_restr_connective_return and_restr_connective232 = null;

        and_restr_connective_return and_restr_connective234 = null;


        Object DOUBLE_PIPE233_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1046:2: ( and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1046:4: and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3877);
            and_restr_connective232=and_restr_connective();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, and_restr_connective232.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1046:25: ({...}? => DOUBLE_PIPE and_restr_connective )*
            loop80:
            do {
                int alt80=2;
                alt80 = dfa80.predict(input);
                switch (alt80) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1046:26: {...}? => DOUBLE_PIPE and_restr_connective
            	    {
            	    if ( !((validateRestr())) ) {
            	        if (backtracking>0) {failed=true; return retval;}
            	        throw new FailedPredicateException(input, "or_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_PIPE233=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective3883); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_PIPE233_tree = (Object)adaptor.create(DOUBLE_PIPE233);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE233_tree, root_0);
            	    }
            	    if ( backtracking==0 ) {
            	      	emit(DOUBLE_PIPE233, DroolsEditorType.SYMBOL);
            	      		emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);	
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3891);
            	    and_restr_connective234=and_restr_connective();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, and_restr_connective234.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1051:1: and_restr_connective : constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* ;
    public final and_restr_connective_return and_restr_connective() throws RecognitionException {
        and_restr_connective_return retval = new and_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER236=null;
        constraint_expression_return constraint_expression235 = null;

        constraint_expression_return constraint_expression237 = null;


        Object DOUBLE_AMPER236_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1052:2: ( constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1052:4: constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3906);
            constraint_expression235=constraint_expression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, constraint_expression235.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1052:26: ({...}? => DOUBLE_AMPER constraint_expression )*
            loop81:
            do {
                int alt81=2;
                alt81 = dfa81.predict(input);
                switch (alt81) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1052:27: {...}? => DOUBLE_AMPER constraint_expression
            	    {
            	    if ( !((validateRestr())) ) {
            	        if (backtracking>0) {failed=true; return retval;}
            	        throw new FailedPredicateException(input, "and_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_AMPER236=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective3912); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_AMPER236_tree = (Object)adaptor.create(DOUBLE_AMPER236);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER236_tree, root_0);
            	    }
            	    if ( backtracking==0 ) {
            	      	emit(DOUBLE_AMPER236, DroolsEditorType.SYMBOL);
            	      		emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);	
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3919);
            	    constraint_expression237=constraint_expression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, constraint_expression237.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );
    public final constraint_expression_return constraint_expression() throws RecognitionException {
        constraint_expression_return retval = new constraint_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN240=null;
        Token RIGHT_PAREN242=null;
        compound_operator_return compound_operator238 = null;

        simple_operator_return simple_operator239 = null;

        or_restr_connective_return or_restr_connective241 = null;


        Object LEFT_PAREN240_tree=null;
        Object RIGHT_PAREN242_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1060:3: ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN )
            int alt82=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA82_1 = input.LA(2);

                if ( (LA82_1==ID) ) {
                    int LA82_10 = input.LA(3);

                    if ( (LA82_10==DOT||(LA82_10>=COMMA && LA82_10<=RIGHT_PAREN)||(LA82_10>=DOUBLE_PIPE && LA82_10<=DOUBLE_AMPER)||LA82_10==LEFT_SQUARE) ) {
                        alt82=2;
                    }
                    else if ( (LA82_10==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                        int LA82_24 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                            alt82=1;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 24, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA82_10==ID||LA82_10==STRING||(LA82_10>=BOOL && LA82_10<=INT)||(LA82_10>=FLOAT && LA82_10<=NULL)) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                        alt82=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 10, input);

                        throw nvae;
                    }
                }
                else if ( (LA82_1==STRING||(LA82_1>=BOOL && LA82_1<=INT)||(LA82_1>=FLOAT && LA82_1<=NULL)) ) {
                    alt82=2;
                }
                else if ( (LA82_1==LEFT_PAREN) ) {
                    switch ( input.LA(3) ) {
                    case ID:
                        {
                        int LA82_31 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt82=1;
                        }
                        else if ( (true) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 31, input);

                            throw nvae;
                        }
                        }
                        break;
                    case STRING:
                        {
                        int LA82_32 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt82=1;
                        }
                        else if ( (true) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 32, input);

                            throw nvae;
                        }
                        }
                        break;
                    case INT:
                        {
                        int LA82_33 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt82=1;
                        }
                        else if ( (true) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 33, input);

                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT:
                        {
                        int LA82_34 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt82=1;
                        }
                        else if ( (true) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 34, input);

                            throw nvae;
                        }
                        }
                        break;
                    case BOOL:
                        {
                        int LA82_35 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt82=1;
                        }
                        else if ( (true) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 35, input);

                            throw nvae;
                        }
                        }
                        break;
                    case NULL:
                        {
                        int LA82_36 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt82=1;
                        }
                        else if ( (true) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 36, input);

                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_PAREN:
                        {
                        int LA82_37 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt82=1;
                        }
                        else if ( (true) ) {
                            alt82=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 37, input);

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
                    case VK_ACTION:
                    case VK_REVERSE:
                    case SEMICOLON:
                    case DOT:
                    case DOT_STAR:
                    case END:
                    case COMMA:
                    case RIGHT_PAREN:
                    case AT:
                    case COLON:
                    case EQUALS:
                    case WHEN:
                    case DOUBLE_PIPE:
                    case DOUBLE_AMPER:
                    case FROM:
                    case OVER:
                    case ACCUMULATE:
                    case INIT:
                    case RESULT:
                    case COLLECT:
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
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 16, input);

                        throw nvae;
                    }

                }
                else if ( (LA82_1==GRAVE_ACCENT) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                    alt82=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 1, input);

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
                    new NoViableAltException("1057:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 82, 0, input);

                throw nvae;
            }

            switch (alt82) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1060:5: compound_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_compound_operator_in_constraint_expression3941);
                    compound_operator238=compound_operator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, compound_operator238.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1061:4: simple_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_simple_operator_in_constraint_expression3946);
                    simple_operator239=simple_operator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, simple_operator239.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1062:4: LEFT_PAREN or_restr_connective RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN240=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression3951); if (failed) return retval;
                    if ( backtracking==0 ) {
                      	emit(LEFT_PAREN240, DroolsEditorType.SYMBOL);	
                    }
                    if ( backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);	
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression3963);
                    or_restr_connective241=or_restr_connective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, or_restr_connective241.getTree());
                    RIGHT_PAREN242=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression3968); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN242_tree = (Object)adaptor.create(RIGHT_PAREN242);
                    adaptor.addChild(root_0, RIGHT_PAREN242_tree);
                    }
                    if ( backtracking==0 ) {
                      	emit(RIGHT_PAREN242, DroolsEditorType.SYMBOL);	
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

            	if (isEditorInterfaceEnabled && input.LA(2) == EOF) {
            		if (input.LA(1) == ID) {
            			emit(input.LT(1), DroolsEditorType.KEYWORD);
            			input.consume();
            			emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
            		}
            	} else if (isEditorInterfaceEnabled && input.LA(3) == EOF) {
            		if (input.LA(1) == ID && input.LA(2) == ID && validateLT(1, DroolsSoftKeywords.NOT)) {
            			emit(input.LT(1), DroolsEditorType.KEYWORD);
            			emit(input.LT(2), DroolsEditorType.KEYWORD);
            			input.consume();
            			input.consume();
            			emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
            		}
            	}

        }
        return retval;
    }
    // $ANTLR end constraint_expression

    public static class simple_operator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start simple_operator
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1093:1: simple_operator : ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | id1= ID | ga1= GRAVE_ACCENT id2= ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | id3= ID | ga2= GRAVE_ACCENT id4= ID square_chunk ) expression_value ;
    public final simple_operator_return simple_operator() throws RecognitionException {
        simple_operator_return retval = new simple_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token ga1=null;
        Token id2=null;
        Token id3=null;
        Token ga2=null;
        Token id4=null;
        Token EQUAL243=null;
        Token GREATER244=null;
        Token GREATER_EQUAL245=null;
        Token LESS246=null;
        Token LESS_EQUAL247=null;
        Token NOT_EQUAL248=null;
        not_key_return not_key249 = null;

        contains_key_return contains_key250 = null;

        soundslike_key_return soundslike_key251 = null;

        matches_key_return matches_key252 = null;

        memberof_key_return memberof_key253 = null;

        square_chunk_return square_chunk254 = null;

        contains_key_return contains_key255 = null;

        excludes_key_return excludes_key256 = null;

        matches_key_return matches_key257 = null;

        soundslike_key_return soundslike_key258 = null;

        memberof_key_return memberof_key259 = null;

        square_chunk_return square_chunk260 = null;

        expression_value_return expression_value261 = null;


        Object id1_tree=null;
        Object ga1_tree=null;
        Object id2_tree=null;
        Object id3_tree=null;
        Object ga2_tree=null;
        Object id4_tree=null;
        Object EQUAL243_tree=null;
        Object GREATER244_tree=null;
        Object GREATER_EQUAL245_tree=null;
        Object LESS246_tree=null;
        Object LESS_EQUAL247_tree=null;
        Object NOT_EQUAL248_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1094:2: ( ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | id1= ID | ga1= GRAVE_ACCENT id2= ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | id3= ID | ga2= GRAVE_ACCENT id4= ID square_chunk ) expression_value )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1094:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | id1= ID | ga1= GRAVE_ACCENT id2= ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | id3= ID | ga2= GRAVE_ACCENT id4= ID square_chunk ) expression_value
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1094:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | id1= ID | ga1= GRAVE_ACCENT id2= ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | id3= ID | ga2= GRAVE_ACCENT id4= ID square_chunk )
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
                        new NoViableAltException("1094:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | id1= ID | ga1= GRAVE_ACCENT id2= ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | id3= ID | ga2= GRAVE_ACCENT id4= ID square_chunk )", 84, 7, input);

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
                    new NoViableAltException("1094:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | id1= ID | ga1= GRAVE_ACCENT id2= ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | id3= ID | ga2= GRAVE_ACCENT id4= ID square_chunk )", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1094:5: EQUAL
                    {
                    EQUAL243=(Token)input.LT(1);
                    match(input,EQUAL,FOLLOW_EQUAL_in_simple_operator3993); if (failed) return retval;
                    if ( backtracking==0 ) {
                    EQUAL243_tree = (Object)adaptor.create(EQUAL243);
                    root_0 = (Object)adaptor.becomeRoot(EQUAL243_tree, root_0);
                    }
                    if ( backtracking==0 ) {
                      	emit(EQUAL243, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1095:4: GREATER
                    {
                    GREATER244=(Token)input.LT(1);
                    match(input,GREATER,FOLLOW_GREATER_in_simple_operator4001); if (failed) return retval;
                    if ( backtracking==0 ) {
                    GREATER244_tree = (Object)adaptor.create(GREATER244);
                    root_0 = (Object)adaptor.becomeRoot(GREATER244_tree, root_0);
                    }
                    if ( backtracking==0 ) {
                      	emit(GREATER244, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1096:4: GREATER_EQUAL
                    {
                    GREATER_EQUAL245=(Token)input.LT(1);
                    match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_simple_operator4009); if (failed) return retval;
                    if ( backtracking==0 ) {
                    GREATER_EQUAL245_tree = (Object)adaptor.create(GREATER_EQUAL245);
                    root_0 = (Object)adaptor.becomeRoot(GREATER_EQUAL245_tree, root_0);
                    }
                    if ( backtracking==0 ) {
                      	emit(GREATER_EQUAL245, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1097:4: LESS
                    {
                    LESS246=(Token)input.LT(1);
                    match(input,LESS,FOLLOW_LESS_in_simple_operator4017); if (failed) return retval;
                    if ( backtracking==0 ) {
                    LESS246_tree = (Object)adaptor.create(LESS246);
                    root_0 = (Object)adaptor.becomeRoot(LESS246_tree, root_0);
                    }
                    if ( backtracking==0 ) {
                      	emit(LESS246, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1098:4: LESS_EQUAL
                    {
                    LESS_EQUAL247=(Token)input.LT(1);
                    match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_simple_operator4025); if (failed) return retval;
                    if ( backtracking==0 ) {
                    LESS_EQUAL247_tree = (Object)adaptor.create(LESS_EQUAL247);
                    root_0 = (Object)adaptor.becomeRoot(LESS_EQUAL247_tree, root_0);
                    }
                    if ( backtracking==0 ) {
                      	emit(LESS_EQUAL247, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1099:4: NOT_EQUAL
                    {
                    NOT_EQUAL248=(Token)input.LT(1);
                    match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_simple_operator4033); if (failed) return retval;
                    if ( backtracking==0 ) {
                    NOT_EQUAL248_tree = (Object)adaptor.create(NOT_EQUAL248);
                    root_0 = (Object)adaptor.becomeRoot(NOT_EQUAL248_tree, root_0);
                    }
                    if ( backtracking==0 ) {
                      	emit(NOT_EQUAL248, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1100:4: not_key ( contains_key | soundslike_key | matches_key | memberof_key | id1= ID | ga1= GRAVE_ACCENT id2= ID square_chunk )
                    {
                    pushFollow(FOLLOW_not_key_in_simple_operator4041);
                    not_key249=not_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, not_key249.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1101:3: ( contains_key | soundslike_key | matches_key | memberof_key | id1= ID | ga1= GRAVE_ACCENT id2= ID square_chunk )
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
                                new NoViableAltException("1101:3: ( contains_key | soundslike_key | matches_key | memberof_key | id1= ID | ga1= GRAVE_ACCENT id2= ID square_chunk )", 83, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA83_0==GRAVE_ACCENT) ) {
                        alt83=6;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("1101:3: ( contains_key | soundslike_key | matches_key | memberof_key | id1= ID | ga1= GRAVE_ACCENT id2= ID square_chunk )", 83, 0, input);

                        throw nvae;
                    }
                    switch (alt83) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1101:5: contains_key
                            {
                            pushFollow(FOLLOW_contains_key_in_simple_operator4048);
                            contains_key250=contains_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(contains_key250.getTree(), root_0);

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1102:5: soundslike_key
                            {
                            pushFollow(FOLLOW_soundslike_key_in_simple_operator4055);
                            soundslike_key251=soundslike_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(soundslike_key251.getTree(), root_0);

                            }
                            break;
                        case 3 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1103:5: matches_key
                            {
                            pushFollow(FOLLOW_matches_key_in_simple_operator4062);
                            matches_key252=matches_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(matches_key252.getTree(), root_0);

                            }
                            break;
                        case 4 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1104:5: memberof_key
                            {
                            pushFollow(FOLLOW_memberof_key_in_simple_operator4069);
                            memberof_key253=memberof_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(memberof_key253.getTree(), root_0);

                            }
                            break;
                        case 5 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1105:5: id1= ID
                            {
                            id1=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_simple_operator4078); if (failed) return retval;
                            if ( backtracking==0 ) {
                            id1_tree = (Object)adaptor.create(id1);
                            root_0 = (Object)adaptor.becomeRoot(id1_tree, root_0);
                            }
                            if ( backtracking==0 ) {
                              	emit(id1, DroolsEditorType.IDENTIFIER);	
                            }

                            }
                            break;
                        case 6 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1106:5: ga1= GRAVE_ACCENT id2= ID square_chunk
                            {
                            ga1=(Token)input.LT(1);
                            match(input,GRAVE_ACCENT,FOLLOW_GRAVE_ACCENT_in_simple_operator4089); if (failed) return retval;
                            if ( backtracking==0 ) {
                              	emit(ga1, DroolsEditorType.SYMBOL);	
                            }
                            id2=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_simple_operator4097); if (failed) return retval;
                            if ( backtracking==0 ) {
                            id2_tree = (Object)adaptor.create(id2);
                            root_0 = (Object)adaptor.becomeRoot(id2_tree, root_0);
                            }
                            if ( backtracking==0 ) {
                              	emit(id2, DroolsEditorType.IDENTIFIER);	
                            }
                            pushFollow(FOLLOW_square_chunk_in_simple_operator4103);
                            square_chunk254=square_chunk();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk254.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1107:4: contains_key
                    {
                    pushFollow(FOLLOW_contains_key_in_simple_operator4109);
                    contains_key255=contains_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(contains_key255.getTree(), root_0);

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1108:4: excludes_key
                    {
                    pushFollow(FOLLOW_excludes_key_in_simple_operator4115);
                    excludes_key256=excludes_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(excludes_key256.getTree(), root_0);

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1109:4: matches_key
                    {
                    pushFollow(FOLLOW_matches_key_in_simple_operator4121);
                    matches_key257=matches_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(matches_key257.getTree(), root_0);

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1110:4: soundslike_key
                    {
                    pushFollow(FOLLOW_soundslike_key_in_simple_operator4127);
                    soundslike_key258=soundslike_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(soundslike_key258.getTree(), root_0);

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1111:4: memberof_key
                    {
                    pushFollow(FOLLOW_memberof_key_in_simple_operator4133);
                    memberof_key259=memberof_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(memberof_key259.getTree(), root_0);

                    }
                    break;
                case 13 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1112:4: id3= ID
                    {
                    id3=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator4141); if (failed) return retval;
                    if ( backtracking==0 ) {
                    id3_tree = (Object)adaptor.create(id3);
                    root_0 = (Object)adaptor.becomeRoot(id3_tree, root_0);
                    }
                    if ( backtracking==0 ) {
                      	emit(id3, DroolsEditorType.IDENTIFIER);	
                    }

                    }
                    break;
                case 14 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1113:4: ga2= GRAVE_ACCENT id4= ID square_chunk
                    {
                    ga2=(Token)input.LT(1);
                    match(input,GRAVE_ACCENT,FOLLOW_GRAVE_ACCENT_in_simple_operator4151); if (failed) return retval;
                    if ( backtracking==0 ) {
                      	emit(ga2, DroolsEditorType.SYMBOL);	
                    }
                    id4=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator4159); if (failed) return retval;
                    if ( backtracking==0 ) {
                    id4_tree = (Object)adaptor.create(id4);
                    root_0 = (Object)adaptor.becomeRoot(id4_tree, root_0);
                    }
                    if ( backtracking==0 ) {
                      	emit(id4, DroolsEditorType.IDENTIFIER);	
                    }
                    pushFollow(FOLLOW_square_chunk_in_simple_operator4165);
                    square_chunk260=square_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk260.getTree());

                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);	
            }
            pushFollow(FOLLOW_expression_value_in_simple_operator4172);
            expression_value261=expression_value();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression_value261.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1119:1: compound_operator : ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN ;
    public final compound_operator_return compound_operator() throws RecognitionException {
        compound_operator_return retval = new compound_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN265=null;
        Token COMMA267=null;
        Token RIGHT_PAREN269=null;
        in_key_return in_key262 = null;

        not_key_return not_key263 = null;

        in_key_return in_key264 = null;

        expression_value_return expression_value266 = null;

        expression_value_return expression_value268 = null;


        Object LEFT_PAREN265_tree=null;
        Object COMMA267_tree=null;
        Object RIGHT_PAREN269_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1120:2: ( ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1120:4: ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1120:4: ( in_key | not_key in_key )
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( (LA85_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.IN))))) {
                int LA85_1 = input.LA(2);

                if ( (LA85_1==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.IN)))) {
                    alt85=1;
                }
                else if ( (LA85_1==ID) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                    alt85=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("1120:4: ( in_key | not_key in_key )", 85, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1120:4: ( in_key | not_key in_key )", 85, 0, input);

                throw nvae;
            }
            switch (alt85) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1120:6: in_key
                    {
                    pushFollow(FOLLOW_in_key_in_compound_operator4187);
                    in_key262=in_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key262.getTree(), root_0);

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1120:16: not_key in_key
                    {
                    pushFollow(FOLLOW_not_key_in_compound_operator4192);
                    not_key263=not_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, not_key263.getTree());
                    pushFollow(FOLLOW_in_key_in_compound_operator4194);
                    in_key264=in_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key264.getTree(), root_0);

                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);	
            }
            LEFT_PAREN265=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator4205); if (failed) return retval;
            if ( backtracking==0 ) {
              	emit(LEFT_PAREN265, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_expression_value_in_compound_operator4213);
            expression_value266=expression_value();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression_value266.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1123:21: ( COMMA expression_value )*
            loop86:
            do {
                int alt86=2;
                int LA86_0 = input.LA(1);

                if ( (LA86_0==COMMA) ) {
                    alt86=1;
                }


                switch (alt86) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1123:23: COMMA expression_value
            	    {
            	    COMMA267=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_compound_operator4217); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	      	emit(COMMA267, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_expression_value_in_compound_operator4222);
            	    expression_value268=expression_value();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, expression_value268.getTree());

            	    }
            	    break;

            	default :
            	    break loop86;
                }
            } while (true);

            RIGHT_PAREN269=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator4230); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_PAREN269_tree = (Object)adaptor.create(RIGHT_PAREN269);
            adaptor.addChild(root_0, RIGHT_PAREN269_tree);
            }
            if ( backtracking==0 ) {
              	emit(RIGHT_PAREN269, DroolsEditorType.SYMBOL);	
            }
            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_END);	
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
             
            	if (isEditorInterfaceEnabled && input.LA(2) == EOF && input.LA(1) == DOUBLE_PIPE) {
            		emit(input.LT(1), DroolsEditorType.SYMBOL);
            		input.consume();
            		emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	}	
        }
        return retval;
    }
    // $ANTLR end compound_operator

    public static class expression_value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression_value
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1134:1: expression_value : ( accessor_path | literal_constraint | paren_chunk ) ;
    public final expression_value_return expression_value() throws RecognitionException {
        expression_value_return retval = new expression_value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        accessor_path_return accessor_path270 = null;

        literal_constraint_return literal_constraint271 = null;

        paren_chunk_return paren_chunk272 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1135:2: ( ( accessor_path | literal_constraint | paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1135:4: ( accessor_path | literal_constraint | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1135:4: ( accessor_path | literal_constraint | paren_chunk )
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
                    new NoViableAltException("1135:4: ( accessor_path | literal_constraint | paren_chunk )", 87, 0, input);

                throw nvae;
            }

            switch (alt87) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1135:5: accessor_path
                    {
                    pushFollow(FOLLOW_accessor_path_in_expression_value4251);
                    accessor_path270=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, accessor_path270.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1136:4: literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_expression_value4256);
                    literal_constraint271=literal_constraint();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, literal_constraint271.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1137:4: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_value4262);
                    paren_chunk272=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk272.getTree());

                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_END);	
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
             
            	if (isEditorInterfaceEnabled && input.LA(2) == EOF) {
            		if (input.LA(1) == DOUBLE_PIPE) {
            			emit(input.LT(1), DroolsEditorType.SYMBOL);
            			input.consume();
            			emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            		} else if (input.LA(1) == EOF && input.get(input.index() - 1).getType() != WS) {
            			emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
            		}
            	}

        }
        return retval;
    }
    // $ANTLR end expression_value

    public static class literal_constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start literal_constraint
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1152:1: literal_constraint : ( STRING | INT | FLOAT | BOOL | NULL );
    public final literal_constraint_return literal_constraint() throws RecognitionException {
        literal_constraint_return retval = new literal_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING273=null;
        Token INT274=null;
        Token FLOAT275=null;
        Token BOOL276=null;
        Token NULL277=null;

        Object STRING273_tree=null;
        Object INT274_tree=null;
        Object FLOAT275_tree=null;
        Object BOOL276_tree=null;
        Object NULL277_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1153:2: ( STRING | INT | FLOAT | BOOL | NULL )
            int alt88=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt88=1;
                }
                break;
            case INT:
                {
                alt88=2;
                }
                break;
            case FLOAT:
                {
                alt88=3;
                }
                break;
            case BOOL:
                {
                alt88=4;
                }
                break;
            case NULL:
                {
                alt88=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1152:1: literal_constraint : ( STRING | INT | FLOAT | BOOL | NULL );", 88, 0, input);

                throw nvae;
            }

            switch (alt88) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1153:4: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING273=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint4281); if (failed) return retval;
                    if ( backtracking==0 ) {
                    STRING273_tree = (Object)adaptor.create(STRING273);
                    adaptor.addChild(root_0, STRING273_tree);
                    }
                    if ( backtracking==0 ) {
                      	emit(STRING273, DroolsEditorType.STRING_CONST);	
                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1154:4: INT
                    {
                    root_0 = (Object)adaptor.nil();

                    INT274=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint4288); if (failed) return retval;
                    if ( backtracking==0 ) {
                    INT274_tree = (Object)adaptor.create(INT274);
                    adaptor.addChild(root_0, INT274_tree);
                    }
                    if ( backtracking==0 ) {
                      	emit(INT274, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1155:4: FLOAT
                    {
                    root_0 = (Object)adaptor.nil();

                    FLOAT275=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint4295); if (failed) return retval;
                    if ( backtracking==0 ) {
                    FLOAT275_tree = (Object)adaptor.create(FLOAT275);
                    adaptor.addChild(root_0, FLOAT275_tree);
                    }
                    if ( backtracking==0 ) {
                      	emit(FLOAT275, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1156:4: BOOL
                    {
                    root_0 = (Object)adaptor.nil();

                    BOOL276=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint4302); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL276_tree = (Object)adaptor.create(BOOL276);
                    adaptor.addChild(root_0, BOOL276_tree);
                    }
                    if ( backtracking==0 ) {
                      	emit(BOOL276, DroolsEditorType.BOOLEAN_CONST);	
                    }

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1157:4: NULL
                    {
                    root_0 = (Object)adaptor.nil();

                    NULL277=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint4309); if (failed) return retval;
                    if ( backtracking==0 ) {
                    NULL277_tree = (Object)adaptor.create(NULL277);
                    adaptor.addChild(root_0, NULL277_tree);
                    }
                    if ( backtracking==0 ) {
                      	emit(NULL277, DroolsEditorType.NULL_CONST);	
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
    // $ANTLR end literal_constraint

    public static class pattern_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start pattern_type
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1160:1: pattern_type : id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final pattern_type_return pattern_type() throws RecognitionException {
        pattern_type_return retval = new pattern_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;
        dimension_definition_return dimension_definition278 = null;


        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1161:2: (id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1161:4: id+= ID (id+= DOT id+= ID )* ( dimension_definition )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_pattern_type4324); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1161:11: (id+= DOT id+= ID )*
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( (LA89_0==DOT) ) {
                    alt89=1;
                }


                switch (alt89) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1161:13: id+= DOT id+= ID
            	    {
            	    id=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pattern_type4330); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_pattern_type4334); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop89;
                }
            } while (true);

            if ( backtracking==0 ) {
              	emit(list_id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.PATTERN, buildStringFromTokens(list_id));	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1164:6: ( dimension_definition )*
            loop90:
            do {
                int alt90=2;
                int LA90_0 = input.LA(1);

                if ( (LA90_0==LEFT_SQUARE) ) {
                    alt90=1;
                }


                switch (alt90) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1164:6: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_pattern_type4349);
            	    dimension_definition278=dimension_definition();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_dimension_definition.add(dimension_definition278.getTree());

            	    }
            	    break;

            	default :
            	    break loop90;
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
            // 1165:3: -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1165:6: ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
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
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1165:28: ( dimension_definition )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1168:1: data_type : id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final data_type_return data_type() throws RecognitionException {
        data_type_return retval = new data_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;
        dimension_definition_return dimension_definition279 = null;


        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1169:2: (id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1169:4: id+= ID (id+= DOT id+= ID )* ( dimension_definition )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_data_type4377); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1169:11: (id+= DOT id+= ID )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==DOT) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1169:13: id+= DOT id+= ID
            	    {
            	    id=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_data_type4383); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_data_type4387); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop91;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1169:31: ( dimension_definition )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==LEFT_SQUARE) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1169:31: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_data_type4392);
            	    dimension_definition279=dimension_definition();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_dimension_definition.add(dimension_definition279.getTree());

            	    }
            	    break;

            	default :
            	    break loop92;
                }
            } while (true);

            if ( backtracking==0 ) {
              	emit(list_id, DroolsEditorType.IDENTIFIER);	
            }

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
            // 1171:3: -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1171:6: ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
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
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1171:25: ( dimension_definition )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1174:1: dimension_definition : LEFT_SQUARE RIGHT_SQUARE ;
    public final dimension_definition_return dimension_definition() throws RecognitionException {
        dimension_definition_return retval = new dimension_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE280=null;
        Token RIGHT_SQUARE281=null;

        Object LEFT_SQUARE280_tree=null;
        Object RIGHT_SQUARE281_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1175:2: ( LEFT_SQUARE RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1175:4: LEFT_SQUARE RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE280=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dimension_definition4421); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_SQUARE280_tree = (Object)adaptor.create(LEFT_SQUARE280);
            adaptor.addChild(root_0, LEFT_SQUARE280_tree);
            }
            if ( backtracking==0 ) {
              	emit(LEFT_SQUARE280, DroolsEditorType.SYMBOL);	
            }
            RIGHT_SQUARE281=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dimension_definition4428); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_SQUARE281_tree = (Object)adaptor.create(RIGHT_SQUARE281);
            adaptor.addChild(root_0, RIGHT_SQUARE281_tree);
            }
            if ( backtracking==0 ) {
              	emit(RIGHT_SQUARE281, DroolsEditorType.SYMBOL);	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1179:1: accessor_path : accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) ;
    public final accessor_path_return accessor_path() throws RecognitionException {
        accessor_path_return retval = new accessor_path_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT283=null;
        accessor_element_return accessor_element282 = null;

        accessor_element_return accessor_element284 = null;


        Object DOT283_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_accessor_element=new RewriteRuleSubtreeStream(adaptor,"rule accessor_element");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1180:2: ( accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1180:4: accessor_element ( DOT accessor_element )*
            {
            pushFollow(FOLLOW_accessor_element_in_accessor_path4442);
            accessor_element282=accessor_element();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_accessor_element.add(accessor_element282.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1180:21: ( DOT accessor_element )*
            loop93:
            do {
                int alt93=2;
                int LA93_0 = input.LA(1);

                if ( (LA93_0==DOT) ) {
                    alt93=1;
                }


                switch (alt93) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1180:23: DOT accessor_element
            	    {
            	    DOT283=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_accessor_path4446); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(DOT283);

            	    if ( backtracking==0 ) {
            	      	emit(DOT283, DroolsEditorType.IDENTIFIER);	
            	    }
            	    pushFollow(FOLLOW_accessor_element_in_accessor_path4450);
            	    accessor_element284=accessor_element();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_accessor_element.add(accessor_element284.getTree());

            	    }
            	    break;

            	default :
            	    break loop93;
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
            // 1181:2: -> ^( VT_ACCESSOR_PATH ( accessor_element )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1181:5: ^( VT_ACCESSOR_PATH ( accessor_element )+ )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1184:1: accessor_element : ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) ;
    public final accessor_element_return accessor_element() throws RecognitionException {
        accessor_element_return retval = new accessor_element_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID285=null;
        square_chunk_return square_chunk286 = null;


        Object ID285_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1185:2: ( ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1185:4: ID ( square_chunk )*
            {
            ID285=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accessor_element4474); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID285);

            if ( backtracking==0 ) {
              	emit(ID285, DroolsEditorType.IDENTIFIER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1186:3: ( square_chunk )*
            loop94:
            do {
                int alt94=2;
                int LA94_0 = input.LA(1);

                if ( (LA94_0==LEFT_SQUARE) ) {
                    alt94=1;
                }


                switch (alt94) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1186:3: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_accessor_element4480);
            	    square_chunk286=square_chunk();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_square_chunk.add(square_chunk286.getTree());

            	    }
            	    break;

            	default :
            	    break loop94;
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
            // 1187:2: -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1187:5: ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ACCESSOR_ELEMENT, "VT_ACCESSOR_ELEMENT"), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1187:30: ( square_chunk )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1190:1: rhs_chunk : rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] ;
    public final rhs_chunk_return rhs_chunk() throws RecognitionException {
        rhs_chunk_return retval = new rhs_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        rhs_chunk_data_return rc = null;


        RewriteRuleSubtreeStream stream_rhs_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1193:3: (rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1193:5: rc= rhs_chunk_data
            {
            pushFollow(FOLLOW_rhs_chunk_data_in_rhs_chunk4509);
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
            // 1194:2: -> VT_RHS_CHUNK[$rc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1197:1: rhs_chunk_data : THEN (any=~ END )* end1= END ( SEMICOLON )? ;
    public final rhs_chunk_data_return rhs_chunk_data() throws RecognitionException {
        rhs_chunk_data_return retval = new rhs_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token any=null;
        Token end1=null;
        Token THEN287=null;
        Token SEMICOLON288=null;

        Object any_tree=null;
        Object end1_tree=null;
        Object THEN287_tree=null;
        Object SEMICOLON288_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1198:2: ( THEN (any=~ END )* end1= END ( SEMICOLON )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1198:4: THEN (any=~ END )* end1= END ( SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            THEN287=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk_data4528); if (failed) return retval;
            if ( backtracking==0 ) {
            THEN287_tree = (Object)adaptor.create(THEN287);
            adaptor.addChild(root_0, THEN287_tree);
            }
            if ( backtracking==0 ) {
              	if (THEN287.getText().equalsIgnoreCase("then")){
              			emit(THEN287, DroolsEditorType.KEYWORD);
              			emit(Location.LOCATION_RHS);
              		}	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1203:4: (any=~ END )*
            loop95:
            do {
                int alt95=2;
                int LA95_0 = input.LA(1);

                if ( ((LA95_0>=VT_COMPILATION_UNIT && LA95_0<=DOT_STAR)||(LA95_0>=STRING && LA95_0<=MULTI_LINE_COMMENT)) ) {
                    alt95=1;
                }


                switch (alt95) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1203:6: any=~ END
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=DOT_STAR)||(input.LA(1)>=STRING && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(any));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk_data4541);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	       emit(any, DroolsEditorType.CODE_CHUNK); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop95;
                }
            } while (true);

            end1=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_rhs_chunk_data4554); if (failed) return retval;
            if ( backtracking==0 ) {
            end1_tree = (Object)adaptor.create(end1);
            adaptor.addChild(root_0, end1_tree);
            }
            if ( backtracking==0 ) {
              	emit(end1, DroolsEditorType.KEYWORD);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1205:3: ( SEMICOLON )?
            int alt96=2;
            int LA96_0 = input.LA(1);

            if ( (LA96_0==SEMICOLON) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1205:3: SEMICOLON
                    {
                    SEMICOLON288=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_rhs_chunk_data4560); if (failed) return retval;
                    if ( backtracking==0 ) {
                    SEMICOLON288_tree = (Object)adaptor.create(SEMICOLON288);
                    adaptor.addChild(root_0, SEMICOLON288_tree);
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
              	emit(SEMICOLON288, DroolsEditorType.KEYWORD);	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1208:1: curly_chunk : cc= curly_chunk_data[false] -> VT_CURLY_CHUNK[$cc.start,text] ;
    public final curly_chunk_return curly_chunk() throws RecognitionException {
        curly_chunk_return retval = new curly_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        curly_chunk_data_return cc = null;


        RewriteRuleSubtreeStream stream_curly_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule curly_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1211:3: (cc= curly_chunk_data[false] -> VT_CURLY_CHUNK[$cc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1211:5: cc= curly_chunk_data[false]
            {
            pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk4579);
            cc=curly_chunk_data(false);
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
            // 1212:2: -> VT_CURLY_CHUNK[$cc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1215:1: curly_chunk_data[boolean isRecursive] : lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY ;
    public final curly_chunk_data_return curly_chunk_data(boolean isRecursive) throws RecognitionException {
        curly_chunk_data_return retval = new curly_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lc1=null;
        Token any=null;
        Token rc1=null;
        curly_chunk_data_return curly_chunk_data289 = null;


        Object lc1_tree=null;
        Object any_tree=null;
        Object rc1_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1216:2: (lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1216:4: lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY
            {
            root_0 = (Object)adaptor.nil();

            lc1=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk_data4602); if (failed) return retval;
            if ( backtracking==0 ) {
            lc1_tree = (Object)adaptor.create(lc1);
            adaptor.addChild(root_0, lc1_tree);
            }
            if ( backtracking==0 ) {
              	if (!isRecursive) {
              				emit(lc1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(lc1, DroolsEditorType.CODE_CHUNK);
              			}	
              		
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1223:4: (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )*
            loop97:
            do {
                int alt97=3;
                int LA97_0 = input.LA(1);

                if ( ((LA97_0>=VT_COMPILATION_UNIT && LA97_0<=THEN)||(LA97_0>=MISC && LA97_0<=MULTI_LINE_COMMENT)) ) {
                    alt97=1;
                }
                else if ( (LA97_0==LEFT_CURLY) ) {
                    alt97=2;
                }


                switch (alt97) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1223:5: any=~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=THEN)||(input.LA(1)>=MISC && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(any));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk_data4614);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	       emit(any, DroolsEditorType.CODE_CHUNK); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1223:87: curly_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk_data4630);
            	    curly_chunk_data289=curly_chunk_data(true);
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, curly_chunk_data289.getTree());

            	    }
            	    break;

            	default :
            	    break loop97;
                }
            } while (true);

            rc1=(Token)input.LT(1);
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk_data4641); if (failed) return retval;
            if ( backtracking==0 ) {
            rc1_tree = (Object)adaptor.create(rc1);
            adaptor.addChild(root_0, rc1_tree);
            }
            if ( backtracking==0 ) {
              	if (!isRecursive) {
              				emit(rc1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(rc1, DroolsEditorType.CODE_CHUNK);
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
    // $ANTLR end curly_chunk_data

    public static class paren_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start paren_chunk
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1233:1: paren_chunk : pc= paren_chunk_data[false] -> VT_PAREN_CHUNK[$pc.start,text] ;
    public final paren_chunk_return paren_chunk() throws RecognitionException {
        paren_chunk_return retval = new paren_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        paren_chunk_data_return pc = null;


        RewriteRuleSubtreeStream stream_paren_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1236:3: (pc= paren_chunk_data[false] -> VT_PAREN_CHUNK[$pc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1236:5: pc= paren_chunk_data[false]
            {
            pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk4662);
            pc=paren_chunk_data(false);
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
            // 1237:2: -> VT_PAREN_CHUNK[$pc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1240:1: paren_chunk_data[boolean isRecursive] : lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN ;
    public final paren_chunk_data_return paren_chunk_data(boolean isRecursive) throws RecognitionException {
        paren_chunk_data_return retval = new paren_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lp1=null;
        Token any=null;
        Token rp1=null;
        paren_chunk_data_return paren_chunk_data290 = null;


        Object lp1_tree=null;
        Object any_tree=null;
        Object rp1_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1241:2: (lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1241:4: lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            lp1=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk_data4686); if (failed) return retval;
            if ( backtracking==0 ) {
            lp1_tree = (Object)adaptor.create(lp1);
            adaptor.addChild(root_0, lp1_tree);
            }
            if ( backtracking==0 ) {
              	if (!isRecursive) {
              				emit(lp1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(lp1, DroolsEditorType.CODE_CHUNK);
              			}	
              		
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1248:4: (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )*
            loop98:
            do {
                int alt98=3;
                int LA98_0 = input.LA(1);

                if ( ((LA98_0>=VT_COMPILATION_UNIT && LA98_0<=STRING)||LA98_0==COMMA||(LA98_0>=AT && LA98_0<=MULTI_LINE_COMMENT)) ) {
                    alt98=1;
                }
                else if ( (LA98_0==LEFT_PAREN) ) {
                    alt98=2;
                }


                switch (alt98) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1248:5: any=~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=STRING)||input.LA(1)==COMMA||(input.LA(1)>=AT && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(any));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk_data4698);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	       emit(any, DroolsEditorType.CODE_CHUNK); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1248:87: paren_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk_data4714);
            	    paren_chunk_data290=paren_chunk_data(true);
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk_data290.getTree());

            	    }
            	    break;

            	default :
            	    break loop98;
                }
            } while (true);

            rp1=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk_data4725); if (failed) return retval;
            if ( backtracking==0 ) {
            rp1_tree = (Object)adaptor.create(rp1);
            adaptor.addChild(root_0, rp1_tree);
            }
            if ( backtracking==0 ) {
              	if (!isRecursive) {
              				emit(rp1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(rp1, DroolsEditorType.CODE_CHUNK);
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
    // $ANTLR end paren_chunk_data

    public static class square_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start square_chunk
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1258:1: square_chunk : sc= square_chunk_data[false] -> VT_SQUARE_CHUNK[$sc.start,text] ;
    public final square_chunk_return square_chunk() throws RecognitionException {
        square_chunk_return retval = new square_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        square_chunk_data_return sc = null;


        RewriteRuleSubtreeStream stream_square_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1261:3: (sc= square_chunk_data[false] -> VT_SQUARE_CHUNK[$sc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1261:5: sc= square_chunk_data[false]
            {
            pushFollow(FOLLOW_square_chunk_data_in_square_chunk4746);
            sc=square_chunk_data(false);
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
            // 1262:2: -> VT_SQUARE_CHUNK[$sc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1265:1: square_chunk_data[boolean isRecursive] : ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE ;
    public final square_chunk_data_return square_chunk_data(boolean isRecursive) throws RecognitionException {
        square_chunk_data_return retval = new square_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ls1=null;
        Token any=null;
        Token rs1=null;
        square_chunk_data_return square_chunk_data291 = null;


        Object ls1_tree=null;
        Object any_tree=null;
        Object rs1_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1266:2: (ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1266:4: ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            ls1=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk_data4769); if (failed) return retval;
            if ( backtracking==0 ) {
            ls1_tree = (Object)adaptor.create(ls1);
            adaptor.addChild(root_0, ls1_tree);
            }
            if ( backtracking==0 ) {
              	if (!isRecursive) {
              				emit(ls1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(ls1, DroolsEditorType.CODE_CHUNK);
              			}	
              		
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1273:4: (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )*
            loop99:
            do {
                int alt99=3;
                int LA99_0 = input.LA(1);

                if ( ((LA99_0>=VT_COMPILATION_UNIT && LA99_0<=NULL)||(LA99_0>=THEN && LA99_0<=MULTI_LINE_COMMENT)) ) {
                    alt99=1;
                }
                else if ( (LA99_0==LEFT_SQUARE) ) {
                    alt99=2;
                }


                switch (alt99) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1273:5: any=~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=NULL)||(input.LA(1)>=THEN && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(any));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk_data4781);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	       emit(any, DroolsEditorType.CODE_CHUNK); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1273:88: square_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_square_chunk_data_in_square_chunk_data4796);
            	    square_chunk_data291=square_chunk_data(true);
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk_data291.getTree());

            	    }
            	    break;

            	default :
            	    break loop99;
                }
            } while (true);

            rs1=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk_data4807); if (failed) return retval;
            if ( backtracking==0 ) {
            rs1_tree = (Object)adaptor.create(rs1);
            adaptor.addChild(root_0, rs1_tree);
            }
            if ( backtracking==0 ) {
              	if (!isRecursive) {
              				emit(rs1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(rs1, DroolsEditorType.CODE_CHUNK);
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
    // $ANTLR end square_chunk_data

    public static class lock_on_active_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lock_on_active_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1283:1: lock_on_active_key : {...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID -> VK_LOCK_ON_ACTIVE[$start, text] ;
    public final lock_on_active_key_return lock_on_active_key() throws RecognitionException {
        lock_on_active_key_return retval = new lock_on_active_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;
        Token mis2=null;
        Token id3=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        Object mis2_tree=null;
        Object id3_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1286:3: ({...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID -> VK_LOCK_ON_ACTIVE[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1286:5: {...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "lock_on_active_key", "(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, \"-\") && validateLT(5, DroolsSoftKeywords.ACTIVE))");
            }
            id1=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key4831); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_lock_on_active_key4835); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key4839); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id2);

            mis2=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_lock_on_active_key4843); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(mis2);

            id3=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key4847); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id3);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( backtracking==0 ) {
              	emit(id1, DroolsEditorType.KEYWORD);
              		emit(mis1, DroolsEditorType.KEYWORD);
              		emit(id2, DroolsEditorType.KEYWORD);
              		emit(mis2, DroolsEditorType.KEYWORD);
              		emit(id3, DroolsEditorType.KEYWORD);	
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
            // 1292:3: -> VK_LOCK_ON_ACTIVE[$start, text]
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

    public static class date_effective_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start date_effective_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1295:1: date_effective_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EFFECTIVE[$start, text] ;
    public final date_effective_key_return date_effective_key() throws RecognitionException {
        date_effective_key_return retval = new date_effective_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1298:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EFFECTIVE[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1298:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "date_effective_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))");
            }
            id1=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_effective_key4879); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_date_effective_key4883); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_effective_key4887); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id2);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( backtracking==0 ) {
              	emit(id1, DroolsEditorType.KEYWORD);
              		emit(mis1, DroolsEditorType.KEYWORD);
              		emit(id2, DroolsEditorType.KEYWORD);	
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
            // 1302:3: -> VK_DATE_EFFECTIVE[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1305:1: date_expires_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EXPIRES[$start, text] ;
    public final date_expires_key_return date_expires_key() throws RecognitionException {
        date_expires_key_return retval = new date_expires_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1308:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EXPIRES[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1308:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "date_expires_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EXPIRES))");
            }
            id1=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_expires_key4919); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_date_expires_key4923); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_expires_key4927); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id2);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( backtracking==0 ) {
              	emit(id1, DroolsEditorType.KEYWORD);
              		emit(mis1, DroolsEditorType.KEYWORD);
              		emit(id2, DroolsEditorType.KEYWORD);	
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
            // 1312:3: -> VK_DATE_EXPIRES[$start, text]
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

    public static class no_loop_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start no_loop_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1315:1: no_loop_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_NO_LOOP[$start, text] ;
    public final no_loop_key_return no_loop_key() throws RecognitionException {
        no_loop_key_return retval = new no_loop_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1318:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_NO_LOOP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1318:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "no_loop_key", "(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.LOOP))");
            }
            id1=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_no_loop_key4959); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_no_loop_key4963); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_no_loop_key4967); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id2);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( backtracking==0 ) {
              	emit(id1, DroolsEditorType.KEYWORD);
              		emit(mis1, DroolsEditorType.KEYWORD);
              		emit(id2, DroolsEditorType.KEYWORD);	
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
            // 1322:3: -> VK_NO_LOOP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1325:1: auto_focus_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_AUTO_FOCUS[$start, text] ;
    public final auto_focus_key_return auto_focus_key() throws RecognitionException {
        auto_focus_key_return retval = new auto_focus_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1328:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_AUTO_FOCUS[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1328:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "auto_focus_key", "(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.FOCUS))");
            }
            id1=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_auto_focus_key4999); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_auto_focus_key5003); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_auto_focus_key5007); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id2);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( backtracking==0 ) {
              	emit(id1, DroolsEditorType.KEYWORD);
              		emit(mis1, DroolsEditorType.KEYWORD);
              		emit(id2, DroolsEditorType.KEYWORD);	
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
            // 1332:3: -> VK_AUTO_FOCUS[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1335:1: activation_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_ACTIVATION_GROUP[$start, text] ;
    public final activation_group_key_return activation_group_key() throws RecognitionException {
        activation_group_key_return retval = new activation_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1338:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_ACTIVATION_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1338:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "activation_group_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_activation_group_key5039); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_activation_group_key5043); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_activation_group_key5047); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id2);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( backtracking==0 ) {
              	emit(id1, DroolsEditorType.KEYWORD);
              		emit(mis1, DroolsEditorType.KEYWORD);
              		emit(id2, DroolsEditorType.KEYWORD);	
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
            // 1342:3: -> VK_ACTIVATION_GROUP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1345:1: agenda_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_AGENDA_GROUP[$start, text] ;
    public final agenda_group_key_return agenda_group_key() throws RecognitionException {
        agenda_group_key_return retval = new agenda_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1348:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_AGENDA_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1348:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "agenda_group_key", "(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_agenda_group_key5079); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_agenda_group_key5083); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_agenda_group_key5087); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id2);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( backtracking==0 ) {
              	emit(id1, DroolsEditorType.KEYWORD);
              		emit(mis1, DroolsEditorType.KEYWORD);
              		emit(id2, DroolsEditorType.KEYWORD);	
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
            // 1352:3: -> VK_AGENDA_GROUP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1355:1: ruleflow_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_RULEFLOW_GROUP[$start, text] ;
    public final ruleflow_group_key_return ruleflow_group_key() throws RecognitionException {
        ruleflow_group_key_return retval = new ruleflow_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1358:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_RULEFLOW_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1358:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "ruleflow_group_key", "(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_ruleflow_group_key5119); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_ruleflow_group_key5123); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_ruleflow_group_key5127); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id2);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( backtracking==0 ) {
              	emit(id1, DroolsEditorType.KEYWORD);
              		emit(mis1, DroolsEditorType.KEYWORD);
              		emit(id2, DroolsEditorType.KEYWORD);	
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
            // 1362:3: -> VK_RULEFLOW_GROUP[$start, text]
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

    public static class entry_point_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start entry_point_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1365:1: entry_point_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_ENTRY_POINT[$start, text] ;
    public final entry_point_key_return entry_point_key() throws RecognitionException {
        entry_point_key_return retval = new entry_point_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1368:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_ENTRY_POINT[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1368:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "entry_point_key", "(validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.POINT))");
            }
            id1=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_entry_point_key5159); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_entry_point_key5163); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_entry_point_key5167); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id2);

            if ( backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( backtracking==0 ) {
              	emit(id1, DroolsEditorType.KEYWORD);
              		emit(mis1, DroolsEditorType.KEYWORD);
              		emit(id2, DroolsEditorType.KEYWORD);	
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
            // 1372:3: -> VK_ENTRY_POINT[$start, text]
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

    public static class duration_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start duration_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1375:1: duration_key : {...}? =>id= ID -> VK_DURATION[$id] ;
    public final duration_key_return duration_key() throws RecognitionException {
        duration_key_return retval = new duration_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1376:2: ({...}? =>id= ID -> VK_DURATION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1376:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DURATION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "duration_key", "(validateIdentifierKey(DroolsSoftKeywords.DURATION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_duration_key5196); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1378:3: -> VK_DURATION[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1381:1: package_key : {...}? =>id= ID -> VK_PACKAGE[$id] ;
    public final package_key_return package_key() throws RecognitionException {
        package_key_return retval = new package_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1382:2: ({...}? =>id= ID -> VK_PACKAGE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1382:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.PACKAGE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "package_key", "(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_key5223); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1384:3: -> VK_PACKAGE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1387:1: import_key : {...}? =>id= ID -> VK_IMPORT[$id] ;
    public final import_key_return import_key() throws RecognitionException {
        import_key_return retval = new import_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1388:2: ({...}? =>id= ID -> VK_IMPORT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1388:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "import_key", "(validateIdentifierKey(DroolsSoftKeywords.IMPORT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_key5250); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1390:3: -> VK_IMPORT[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1393:1: dialect_key : {...}? =>id= ID -> VK_DIALECT[$id] ;
    public final dialect_key_return dialect_key() throws RecognitionException {
        dialect_key_return retval = new dialect_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1394:2: ({...}? =>id= ID -> VK_DIALECT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1394:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DIALECT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "dialect_key", "(validateIdentifierKey(DroolsSoftKeywords.DIALECT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dialect_key5277); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1396:3: -> VK_DIALECT[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1399:1: salience_key : {...}? =>id= ID -> VK_SALIENCE[$id] ;
    public final salience_key_return salience_key() throws RecognitionException {
        salience_key_return retval = new salience_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1400:2: ({...}? =>id= ID -> VK_SALIENCE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1400:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "salience_key", "(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_salience_key5304); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1402:3: -> VK_SALIENCE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1405:1: enabled_key : {...}? =>id= ID -> VK_ENABLED[$id] ;
    public final enabled_key_return enabled_key() throws RecognitionException {
        enabled_key_return retval = new enabled_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1406:2: ({...}? =>id= ID -> VK_ENABLED[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1406:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ENABLED))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "enabled_key", "(validateIdentifierKey(DroolsSoftKeywords.ENABLED))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enabled_key5331); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1408:3: -> VK_ENABLED[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1411:1: attributes_key : {...}? =>id= ID -> VK_ATTRIBUTES[$id] ;
    public final attributes_key_return attributes_key() throws RecognitionException {
        attributes_key_return retval = new attributes_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1412:2: ({...}? =>id= ID -> VK_ATTRIBUTES[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1412:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "attributes_key", "(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_attributes_key5358); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1414:3: -> VK_ATTRIBUTES[$id]
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

    public static class rule_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rule_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1417:1: rule_key : {...}? =>id= ID -> VK_RULE[$id] ;
    public final rule_key_return rule_key() throws RecognitionException {
        rule_key_return retval = new rule_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1418:2: ({...}? =>id= ID -> VK_RULE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1418:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "rule_key", "(validateIdentifierKey(DroolsSoftKeywords.RULE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_rule_key5385); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1420:3: -> VK_RULE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1423:1: template_key : {...}? =>id= ID -> VK_TEMPLATE[$id] ;
    public final template_key_return template_key() throws RecognitionException {
        template_key_return retval = new template_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1424:2: ({...}? =>id= ID -> VK_TEMPLATE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1424:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "template_key", "(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template_key5412); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1426:3: -> VK_TEMPLATE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1429:1: query_key : {...}? =>id= ID -> VK_QUERY[$id] ;
    public final query_key_return query_key() throws RecognitionException {
        query_key_return retval = new query_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1430:2: ({...}? =>id= ID -> VK_QUERY[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1430:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "query_key", "(validateIdentifierKey(DroolsSoftKeywords.QUERY))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_query_key5439); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1432:3: -> VK_QUERY[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1435:1: declare_key : {...}? =>id= ID -> VK_DECLARE[$id] ;
    public final declare_key_return declare_key() throws RecognitionException {
        declare_key_return retval = new declare_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1436:2: ({...}? =>id= ID -> VK_DECLARE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1436:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DECLARE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "declare_key", "(validateIdentifierKey(DroolsSoftKeywords.DECLARE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_declare_key5466); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1438:3: -> VK_DECLARE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1441:1: function_key : {...}? =>id= ID -> VK_FUNCTION[$id] ;
    public final function_key_return function_key() throws RecognitionException {
        function_key_return retval = new function_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1442:2: ({...}? =>id= ID -> VK_FUNCTION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1442:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "function_key", "(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function_key5493); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1444:3: -> VK_FUNCTION[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1447:1: global_key : {...}? =>id= ID -> VK_GLOBAL[$id] ;
    public final global_key_return global_key() throws RecognitionException {
        global_key_return retval = new global_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1448:2: ({...}? =>id= ID -> VK_GLOBAL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1448:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "global_key", "(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global_key5520); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1450:3: -> VK_GLOBAL[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1453:1: eval_key : {...}? =>id= ID -> VK_EVAL[$id] ;
    public final eval_key_return eval_key() throws RecognitionException {
        eval_key_return retval = new eval_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1454:2: ({...}? =>id= ID -> VK_EVAL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1454:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "eval_key", "(validateIdentifierKey(DroolsSoftKeywords.EVAL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_eval_key5547); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1456:3: -> VK_EVAL[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1459:1: contains_key : {...}? =>id= ID -> VK_CONTAINS[$id] ;
    public final contains_key_return contains_key() throws RecognitionException {
        contains_key_return retval = new contains_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1460:2: ({...}? =>id= ID -> VK_CONTAINS[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1460:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.CONTAINS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "contains_key", "(validateIdentifierKey(DroolsSoftKeywords.CONTAINS))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_contains_key5574); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1462:3: -> VK_CONTAINS[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1465:1: matches_key : {...}? =>id= ID -> VK_MATCHES[$id] ;
    public final matches_key_return matches_key() throws RecognitionException {
        matches_key_return retval = new matches_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1466:2: ({...}? =>id= ID -> VK_MATCHES[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1466:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.MATCHES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "matches_key", "(validateIdentifierKey(DroolsSoftKeywords.MATCHES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_matches_key5601); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1468:3: -> VK_MATCHES[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1471:1: excludes_key : {...}? =>id= ID -> VK_EXCLUDES[$id] ;
    public final excludes_key_return excludes_key() throws RecognitionException {
        excludes_key_return retval = new excludes_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1472:2: ({...}? =>id= ID -> VK_EXCLUDES[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1472:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "excludes_key", "(validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_excludes_key5628); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1474:3: -> VK_EXCLUDES[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1477:1: soundslike_key : {...}? =>id= ID -> VK_SOUNDSLIKE[$id] ;
    public final soundslike_key_return soundslike_key() throws RecognitionException {
        soundslike_key_return retval = new soundslike_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1478:2: ({...}? =>id= ID -> VK_SOUNDSLIKE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1478:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "soundslike_key", "(validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_soundslike_key5655); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1480:3: -> VK_SOUNDSLIKE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1483:1: memberof_key : {...}? =>id= ID -> VK_MEMBEROF[$id] ;
    public final memberof_key_return memberof_key() throws RecognitionException {
        memberof_key_return retval = new memberof_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1484:2: ({...}? =>id= ID -> VK_MEMBEROF[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1484:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "memberof_key", "(validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_memberof_key5682); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1486:3: -> VK_MEMBEROF[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1489:1: not_key : {...}? =>id= ID -> VK_NOT[$id] ;
    public final not_key_return not_key() throws RecognitionException {
        not_key_return retval = new not_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1490:2: ({...}? =>id= ID -> VK_NOT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1490:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "not_key", "(validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_not_key5709); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1492:3: -> VK_NOT[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1495:1: in_key : {...}? =>id= ID -> VK_IN[$id] ;
    public final in_key_return in_key() throws RecognitionException {
        in_key_return retval = new in_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1496:2: ({...}? =>id= ID -> VK_IN[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1496:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "in_key", "(validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_in_key5736); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1498:3: -> VK_IN[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:1: or_key : {...}? =>id= ID -> VK_OR[$id] ;
    public final or_key_return or_key() throws RecognitionException {
        or_key_return retval = new or_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1502:2: ({...}? =>id= ID -> VK_OR[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1502:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.OR))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "or_key", "(validateIdentifierKey(DroolsSoftKeywords.OR))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_or_key5763); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1504:3: -> VK_OR[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1507:1: and_key : {...}? =>id= ID -> VK_AND[$id] ;
    public final and_key_return and_key() throws RecognitionException {
        and_key_return retval = new and_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1508:2: ({...}? =>id= ID -> VK_AND[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1508:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AND))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "and_key", "(validateIdentifierKey(DroolsSoftKeywords.AND))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_and_key5790); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1510:3: -> VK_AND[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1513:1: exists_key : {...}? =>id= ID -> VK_EXISTS[$id] ;
    public final exists_key_return exists_key() throws RecognitionException {
        exists_key_return retval = new exists_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1514:2: ({...}? =>id= ID -> VK_EXISTS[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1514:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EXISTS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "exists_key", "(validateIdentifierKey(DroolsSoftKeywords.EXISTS))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_exists_key5817); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1516:3: -> VK_EXISTS[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1519:1: forall_key : {...}? =>id= ID -> VK_FORALL[$id] ;
    public final forall_key_return forall_key() throws RecognitionException {
        forall_key_return retval = new forall_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1520:2: ({...}? =>id= ID -> VK_FORALL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1520:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.FORALL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "forall_key", "(validateIdentifierKey(DroolsSoftKeywords.FORALL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_forall_key5844); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1522:3: -> VK_FORALL[$id]
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

    public static class action_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start action_key
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1525:1: action_key : {...}? =>id= ID -> VK_ACTION[$id] ;
    public final action_key_return action_key() throws RecognitionException {
        action_key_return retval = new action_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1526:2: ({...}? =>id= ID -> VK_ACTION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1526:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ACTION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "action_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_action_key5871); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1528:3: -> VK_ACTION[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1531:1: reverse_key : {...}? =>id= ID -> VK_REVERSE[$id] ;
    public final reverse_key_return reverse_key() throws RecognitionException {
        reverse_key_return retval = new reverse_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1532:2: ({...}? =>id= ID -> VK_REVERSE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1532:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.REVERSE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "reverse_key", "(validateIdentifierKey(DroolsSoftKeywords.REVERSE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_reverse_key5898); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if ( backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
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
            // 1534:3: -> VK_REVERSE[$id]
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

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:718:5: ( LEFT_PAREN or_key )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:718:6: LEFT_PAREN or_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred12007); if (failed) return ;
        pushFollow(FOLLOW_or_key_in_synpred12009);
        or_key();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:5: ( or_key | DOUBLE_PIPE )
        int alt100=2;
        int LA100_0 = input.LA(1);

        if ( (LA100_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
            alt100=1;
        }
        else if ( (LA100_0==DOUBLE_PIPE) ) {
            alt100=2;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("726:5: synpred2 : ( or_key | DOUBLE_PIPE );", 100, 0, input);

            throw nvae;
        }
        switch (alt100) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:6: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred22076);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:13: DOUBLE_PIPE
                {
                match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred22078); if (failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred2

    // $ANTLR start synpred3
    public final void synpred3_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:5: ( LEFT_PAREN and_key )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:6: LEFT_PAREN and_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred32135); if (failed) return ;
        pushFollow(FOLLOW_and_key_in_synpred32137);
        and_key();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred3

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:5: ( and_key | DOUBLE_AMPER )
        int alt101=2;
        int LA101_0 = input.LA(1);

        if ( (LA101_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.AND)))) {
            alt101=1;
        }
        else if ( (LA101_0==DOUBLE_AMPER) ) {
            alt101=2;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("743:5: synpred4 : ( and_key | DOUBLE_AMPER );", 101, 0, input);

            throw nvae;
        }
        switch (alt101) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:6: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred42205);
                and_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:14: DOUBLE_AMPER
                {
                match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred42207); if (failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:760:4: ( SEMICOLON )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:760:5: SEMICOLON
        {
        match(input,SEMICOLON,FOLLOW_SEMICOLON_in_synpred52330); if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:766:12: ( LEFT_PAREN ( or_key | and_key ) )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:766:13: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred62367); if (failed) return ;
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:766:24: ( or_key | and_key )
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
                    new NoViableAltException("766:24: ( or_key | and_key )", 102, 1, input);

                throw nvae;
            }
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("766:24: ( or_key | and_key )", 102, 0, input);

            throw nvae;
        }
        switch (alt102) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:766:25: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred62370);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:766:32: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred62372);
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
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:782:5: ( LEFT_PAREN ( or_key | and_key ) )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:782:6: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred72495); if (failed) return ;
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:782:17: ( or_key | and_key )
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
                    new NoViableAltException("782:17: ( or_key | and_key )", 103, 1, input);

                throw nvae;
            }
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("782:17: ( or_key | and_key )", 103, 0, input);

            throw nvae;
        }
        switch (alt103) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:782:18: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred72498);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:782:25: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred72500);
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
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:928:5: ( LEFT_PAREN )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:928:6: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred83230); if (failed) return ;

        }
    }
    // $ANTLR end synpred8

    // $ANTLR start synpred9
    public final void synpred9_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:943:6: ( LEFT_SQUARE )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:943:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred93299); if (failed) return ;

        }
    }
    // $ANTLR end synpred9

    // $ANTLR start synpred10
    public final void synpred10_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:945:6: ( LEFT_PAREN )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:945:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred103321); if (failed) return ;

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
    protected DFA51 dfa51 = new DFA51(this);
    protected DFA52 dfa52 = new DFA52(this);
    protected DFA65 dfa65 = new DFA65(this);
    protected DFA80 dfa80 = new DFA80(this);
    protected DFA81 dfa81 = new DFA81(this);
    static final String DFA12_eotS =
        "\16\uffff";
    static final String DFA12_eofS =
        "\16\uffff";
    static final String DFA12_minS =
        "\2\122\1\uffff\1\122\1\uffff\2\122\2\163\2\122\1\127\1\163\1\122";
    static final String DFA12_maxS =
        "\1\127\1\131\1\uffff\1\162\1\uffff\1\162\1\122\2\163\3\162\1\163"+
        "\1\162";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\11\uffff";
    static final String DFA12_specialS =
        "\16\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\2\2\uffff\1\2\1\uffff\1\1",
            "\1\3\4\uffff\1\2\1\uffff\1\4",
            "",
            "\1\5\1\6\3\uffff\1\2\2\4\1\uffff\1\2\26\uffff\1\7",
            "",
            "\2\2\3\uffff\1\2\2\4\1\uffff\1\2\26\uffff\1\10",
            "\1\11",
            "\1\12",
            "\1\13",
            "\1\4\1\6\3\uffff\1\2\32\uffff\1\14",
            "\1\4\4\uffff\1\2\2\4\30\uffff\1\7",
            "\1\2\2\4\30\uffff\1\10",
            "\1\15",
            "\1\4\4\uffff\1\2\32\uffff\1\14"
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
            return "481:3: ( parameters )?";
        }
    }
    static final String DFA17_eotS =
        "\6\uffff";
    static final String DFA17_eofS =
        "\6\uffff";
    static final String DFA17_minS =
        "\2\122\1\163\2\uffff\1\122";
    static final String DFA17_maxS =
        "\1\122\1\162\1\163\2\uffff\1\162";
    static final String DFA17_acceptS =
        "\3\uffff\1\2\1\1\1\uffff";
    static final String DFA17_specialS =
        "\6\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\1",
            "\2\4\4\uffff\2\3\30\uffff\1\2",
            "\1\5",
            "",
            "",
            "\1\4\5\uffff\2\3\30\uffff\1\2"
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
            return "507:4: ( data_type )?";
        }
    }
    static final String DFA51_eotS =
        "\u0082\uffff";
    static final String DFA51_eofS =
        "\u0082\uffff";
    static final String DFA51_minS =
        "\3\122\2\0\2\122\1\163\1\0\3\uffff\1\123\1\122\1\123\1\127\1\uffff"+
        "\1\122\1\163\1\122\1\123\1\122\1\123\1\127\1\122\2\0\1\122\1\163"+
        "\2\122\2\0\1\122\1\4\11\122\1\0\2\uffff\1\123\1\127\1\122\1\0\2"+
        "\122\1\4\11\122\1\0\1\uffff\1\122\1\4\16\0\1\uffff\14\0\1\uffff"+
        "\23\0\1\uffff\17\0";
    static final String DFA51_maxS =
        "\2\127\1\162\2\0\1\127\1\122\1\163\1\0\3\uffff\1\162\1\122\2\162"+
        "\1\uffff\1\122\1\163\1\131\1\162\1\131\3\162\2\0\1\122\1\163\1\131"+
        "\1\162\2\0\1\122\1\u0080\1\122\7\161\1\122\1\0\2\uffff\3\162\1\0"+
        "\1\140\1\122\1\u0080\1\122\7\161\1\122\1\0\1\uffff\1\162\1\u0080"+
        "\16\0\1\uffff\14\0\1\uffff\23\0\1\uffff\17\0";
    static final String DFA51_acceptS =
        "\11\uffff\1\1\2\2\4\uffff\1\3\34\uffff\2\3\21\uffff\1\3\20\uffff"+
        "\1\3\14\uffff\1\3\23\uffff\1\3\17\uffff";
    static final String DFA51_specialS =
        "\2\uffff\1\7\1\6\1\3\3\uffff\1\1\20\uffff\1\10\1\12\4\uffff\1\2"+
        "\1\0\13\uffff\1\5\5\uffff\1\11\14\uffff\1\4\102\uffff}>";
    static final String[] DFA51_transitionS = {
            "\1\2\4\uffff\1\1",
            "\1\3\4\uffff\1\4",
            "\1\11\1\6\3\uffff\1\10\3\uffff\1\5\26\uffff\1\7",
            "\1\uffff",
            "\1\uffff",
            "\1\14\4\uffff\1\15",
            "\1\16",
            "\1\17",
            "\1\uffff",
            "",
            "",
            "",
            "\1\21\3\uffff\1\23\32\uffff\1\22",
            "\1\24",
            "\1\6\3\uffff\1\25\32\uffff\1\7",
            "\1\25\32\uffff\1\7",
            "",
            "\1\26",
            "\1\27",
            "\1\30\4\uffff\1\31\1\uffff\1\32",
            "\1\33\3\uffff\1\35\32\uffff\1\34",
            "\1\36\4\uffff\1\37\1\uffff\1\40",
            "\1\21\3\uffff\1\23\32\uffff\1\22",
            "\1\23\32\uffff\1\22",
            "\1\44\1\43\3\uffff\1\54\3\uffff\1\41\15\uffff\1\45\1\46\1\47"+
            "\1\50\1\51\1\52\1\53\2\uffff\1\42",
            "\1\uffff",
            "\1\uffff",
            "\1\57",
            "\1\60",
            "\1\61\4\uffff\1\62\1\uffff\1\63",
            "\1\67\1\66\3\uffff\1\77\3\uffff\1\64\15\uffff\1\70\1\71\1\72"+
            "\1\73\1\74\1\75\1\76\2\uffff\1\65",
            "\1\uffff",
            "\1\uffff",
            "\1\101",
            "\156\102\1\103\1\104\15\102",
            "\1\105",
            "\1\107\3\uffff\1\110\1\106\6\uffff\1\113\1\111\17\uffff\1\115"+
            "\1\112\1\114",
            "\1\116\3\uffff\1\110\1\117\6\uffff\1\113\1\111\20\uffff\1\112"+
            "\1\114",
            "\1\116\3\uffff\1\110\1\117\6\uffff\1\113\1\111\20\uffff\1\112"+
            "\1\114",
            "\1\116\3\uffff\1\110\1\117\6\uffff\1\113\1\111\20\uffff\1\112"+
            "\1\114",
            "\1\116\3\uffff\1\110\1\117\6\uffff\1\113\1\111\20\uffff\1\112"+
            "\1\114",
            "\1\116\3\uffff\1\110\1\117\6\uffff\1\113\1\111\20\uffff\1\112"+
            "\1\114",
            "\1\116\3\uffff\1\110\1\117\6\uffff\1\113\1\111\20\uffff\1\112"+
            "\1\114",
            "\1\120",
            "\1\uffff",
            "",
            "",
            "\1\33\3\uffff\1\35\32\uffff\1\34",
            "\1\35\32\uffff\1\34",
            "\1\125\1\124\3\uffff\1\135\3\uffff\1\122\15\uffff\1\126\1\127"+
            "\1\130\1\131\1\132\1\133\1\134\2\uffff\1\123",
            "\1\uffff",
            "\1\137\6\uffff\1\141\6\uffff\1\140",
            "\1\142",
            "\156\143\1\144\1\145\15\143",
            "\1\146",
            "\1\147\3\uffff\1\150\1\155\6\uffff\1\153\1\151\17\uffff\1\156"+
            "\1\152\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\161",
            "\1\uffff",
            "",
            "\1\165\1\164\3\uffff\1\175\1\u0081\1\32\6\uffff\1\u0080\1\177"+
            "\6\uffff\1\176\1\166\1\167\1\170\1\171\1\172\1\173\1\174\2\uffff"+
            "\1\163",
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
            return "766:10: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA51_32 = input.LA(1);

                         
                        int index51_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index51_32);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA51_8 = input.LA(1);

                         
                        int index51_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred6()||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.FORALL)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EVAL))))) ) {s = 9;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index51_8);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA51_31 = input.LA(1);

                         
                        int index51_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 64;}

                         
                        input.seek(index51_31);
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
                        int LA51_63 = input.LA(1);

                         
                        int index51_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 114;}

                         
                        input.seek(index51_63);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA51_44 = input.LA(1);

                         
                        int index51_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 81;}

                         
                        input.seek(index51_44);
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
                        int LA51_2 = input.LA(1);

                         
                        int index51_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA51_2==COLON) ) {s = 5;}

                        else if ( (LA51_2==DOT) ) {s = 6;}

                        else if ( (LA51_2==LEFT_SQUARE) ) {s = 7;}

                        else if ( (LA51_2==LEFT_PAREN) ) {s = 8;}

                        else if ( (LA51_2==ID) && (((synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||((synpred6()&&validateNotWithBinding())&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {s = 9;}

                         
                        input.seek(index51_2);
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

                        else if ( (true) ) {s = 94;}

                         
                        input.seek(index51_50);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA51_26 = input.LA(1);

                         
                        int index51_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 9;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index51_26);
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
        "\u0082\uffff";
    static final String DFA52_eofS =
        "\u0082\uffff";
    static final String DFA52_minS =
        "\3\122\2\0\1\122\1\0\1\uffff\1\122\1\163\2\uffff\1\123\1\122\1\uffff"+
        "\1\123\1\127\1\122\1\163\1\122\1\123\1\122\1\123\1\127\1\122\2\0"+
        "\1\122\1\163\2\122\2\0\1\122\1\4\11\122\1\0\2\uffff\1\123\1\127"+
        "\1\122\1\0\2\122\1\4\11\122\1\0\1\uffff\1\122\1\4\16\0\1\uffff\14"+
        "\0\1\uffff\23\0\1\uffff\17\0";
    static final String DFA52_maxS =
        "\2\127\1\162\2\0\1\127\1\0\1\uffff\1\122\1\163\2\uffff\1\162\1\122"+
        "\1\uffff\2\162\1\122\1\163\1\131\1\162\1\131\3\162\2\0\1\122\1\163"+
        "\1\131\1\162\2\0\1\122\1\u0080\1\122\7\161\1\122\1\0\2\uffff\3\162"+
        "\1\0\1\140\1\122\1\u0080\1\122\7\161\1\122\1\0\1\uffff\1\162\1\u0080"+
        "\16\0\1\uffff\14\0\1\uffff\23\0\1\uffff\17\0";
    static final String DFA52_acceptS =
        "\7\uffff\1\1\2\uffff\2\2\2\uffff\1\3\36\uffff\2\3\21\uffff\1\3\20"+
        "\uffff\1\3\14\uffff\1\3\23\uffff\1\3\17\uffff";
    static final String DFA52_specialS =
        "\2\uffff\1\4\1\12\1\1\1\uffff\1\10\22\uffff\1\6\1\11\4\uffff\1\2"+
        "\1\5\13\uffff\1\3\5\uffff\1\7\14\uffff\1\0\102\uffff}>";
    static final String[] DFA52_transitionS = {
            "\1\2\4\uffff\1\1",
            "\1\4\4\uffff\1\3",
            "\1\7\1\10\3\uffff\1\6\3\uffff\1\5\26\uffff\1\11",
            "\1\uffff",
            "\1\uffff",
            "\1\14\4\uffff\1\15",
            "\1\uffff",
            "",
            "\1\17",
            "\1\20",
            "",
            "",
            "\1\21\3\uffff\1\23\32\uffff\1\22",
            "\1\24",
            "",
            "\1\10\3\uffff\1\25\32\uffff\1\11",
            "\1\25\32\uffff\1\11",
            "\1\26",
            "\1\27",
            "\1\30\4\uffff\1\31\1\uffff\1\32",
            "\1\33\3\uffff\1\35\32\uffff\1\34",
            "\1\36\4\uffff\1\37\1\uffff\1\40",
            "\1\21\3\uffff\1\23\32\uffff\1\22",
            "\1\23\32\uffff\1\22",
            "\1\44\1\43\3\uffff\1\54\3\uffff\1\41\15\uffff\1\45\1\46\1\47"+
            "\1\50\1\51\1\52\1\53\2\uffff\1\42",
            "\1\uffff",
            "\1\uffff",
            "\1\57",
            "\1\60",
            "\1\61\4\uffff\1\62\1\uffff\1\63",
            "\1\67\1\66\3\uffff\1\77\3\uffff\1\64\15\uffff\1\70\1\71\1\72"+
            "\1\73\1\74\1\75\1\76\2\uffff\1\65",
            "\1\uffff",
            "\1\uffff",
            "\1\101",
            "\156\102\1\103\1\104\15\102",
            "\1\105",
            "\1\106\3\uffff\1\107\1\114\6\uffff\1\112\1\110\17\uffff\1\115"+
            "\1\111\1\113",
            "\1\116\3\uffff\1\107\1\117\6\uffff\1\112\1\110\20\uffff\1\111"+
            "\1\113",
            "\1\116\3\uffff\1\107\1\117\6\uffff\1\112\1\110\20\uffff\1\111"+
            "\1\113",
            "\1\116\3\uffff\1\107\1\117\6\uffff\1\112\1\110\20\uffff\1\111"+
            "\1\113",
            "\1\116\3\uffff\1\107\1\117\6\uffff\1\112\1\110\20\uffff\1\111"+
            "\1\113",
            "\1\116\3\uffff\1\107\1\117\6\uffff\1\112\1\110\20\uffff\1\111"+
            "\1\113",
            "\1\116\3\uffff\1\107\1\117\6\uffff\1\112\1\110\20\uffff\1\111"+
            "\1\113",
            "\1\120",
            "\1\uffff",
            "",
            "",
            "\1\33\3\uffff\1\35\32\uffff\1\34",
            "\1\35\32\uffff\1\34",
            "\1\126\1\125\3\uffff\1\123\3\uffff\1\122\15\uffff\1\127\1\130"+
            "\1\131\1\132\1\133\1\134\1\135\2\uffff\1\124",
            "\1\uffff",
            "\1\137\6\uffff\1\141\6\uffff\1\140",
            "\1\142",
            "\156\143\1\144\1\145\15\143",
            "\1\146",
            "\1\147\3\uffff\1\150\1\155\6\uffff\1\153\1\151\17\uffff\1\156"+
            "\1\152\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\157\3\uffff\1\150\1\160\6\uffff\1\153\1\151\20\uffff\1\152"+
            "\1\154",
            "\1\161",
            "\1\uffff",
            "",
            "\1\165\1\164\3\uffff\1\175\1\u0081\1\32\6\uffff\1\u0080\1\177"+
            "\6\uffff\1\176\1\166\1\167\1\170\1\171\1\172\1\173\1\174\2\uffff"+
            "\1\163",
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
            return "782:3: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA52_63 = input.LA(1);

                         
                        int index52_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 114;}

                         
                        input.seek(index52_63);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA52_4 = input.LA(1);

                         
                        int index52_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index52_4);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA52_31 = input.LA(1);

                         
                        int index52_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 64;}

                         
                        input.seek(index52_31);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA52_44 = input.LA(1);

                         
                        int index52_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 81;}

                         
                        input.seek(index52_44);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA52_2 = input.LA(1);

                         
                        int index52_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_2==COLON) ) {s = 5;}

                        else if ( (LA52_2==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA52_2==ID) && (((synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||((synpred7()&&validateNotWithBinding())&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))))) {s = 7;}

                        else if ( (LA52_2==DOT) ) {s = 8;}

                        else if ( (LA52_2==LEFT_SQUARE) ) {s = 9;}

                         
                        input.seek(index52_2);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA52_32 = input.LA(1);

                         
                        int index52_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index52_32);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA52_25 = input.LA(1);

                         
                        int index52_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 45;}

                         
                        input.seek(index52_25);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA52_50 = input.LA(1);

                         
                        int index52_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 94;}

                         
                        input.seek(index52_50);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA52_6 = input.LA(1);

                         
                        int index52_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.FORALL)))||synpred7()||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EVAL)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) ) {s = 7;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index52_6);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA52_26 = input.LA(1);

                         
                        int index52_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index52_26);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA52_3 = input.LA(1);

                         
                        int index52_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 10;}

                         
                        input.seek(index52_3);
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
    static final String DFA65_eotS =
        "\25\uffff";
    static final String DFA65_eofS =
        "\25\uffff";
    static final String DFA65_minS =
        "\1\121\1\4\1\uffff\1\4\1\uffff\1\0\1\uffff\1\4\2\0\3\4\1\0\1\4\1"+
        "\uffff\1\4\4\0";
    static final String DFA65_maxS =
        "\1\164\1\u0080\1\uffff\1\u0080\1\uffff\1\0\1\uffff\1\u0080\2\0\3"+
        "\u0080\1\0\1\u0080\1\uffff\1\u0080\4\0";
    static final String DFA65_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\1\uffff\1\1\10\uffff\1\1\5\uffff";
    static final String DFA65_specialS =
        "\1\uffff\1\10\1\uffff\1\3\1\uffff\1\7\1\uffff\1\1\1\5\1\2\1\6\1"+
        "\13\1\0\1\4\1\12\1\uffff\1\11\4\uffff}>";
    static final String[] DFA65_transitionS = {
            "\3\2\1\uffff\1\2\1\uffff\1\1\2\2\6\uffff\2\2\3\uffff\1\2\16"+
            "\uffff\1\2",
            "\116\4\1\3\4\4\1\5\1\4\1\6\47\4",
            "",
            "\116\4\1\11\1\12\3\4\1\10\1\4\1\6\1\4\1\7\26\4\1\13\16\4",
            "",
            "\1\uffff",
            "",
            "\116\4\1\14\4\4\1\15\1\4\1\6\47\4",
            "\1\uffff",
            "\1\uffff",
            "\116\4\1\16\4\4\1\17\1\4\1\6\47\4",
            "\123\4\1\17\1\4\1\6\31\4\1\20\15\4",
            "\117\4\1\21\3\4\1\23\1\4\1\6\30\4\1\22\16\4",
            "\1\uffff",
            "\117\4\1\12\3\4\1\24\1\4\1\6\30\4\1\13\16\4",
            "",
            "\123\4\1\24\1\4\1\6\30\4\1\13\16\4",
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
            return "928:3: ( ( LEFT_PAREN )=>args= paren_chunk )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA65_12 = input.LA(1);

                         
                        int index65_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_12==DOT) ) {s = 17;}

                        else if ( (LA65_12==LEFT_SQUARE) ) {s = 18;}

                        else if ( (LA65_12==LEFT_PAREN) ) {s = 19;}

                        else if ( (LA65_12==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( ((LA65_12>=VT_COMPILATION_UNIT && LA65_12<=ID)||(LA65_12>=DOT_STAR && LA65_12<=STRING)||LA65_12==COMMA||(LA65_12>=AT && LA65_12<=NULL)||(LA65_12>=RIGHT_SQUARE && LA65_12<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 4;}

                         
                        input.seek(index65_12);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA65_7 = input.LA(1);

                         
                        int index65_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_7==ID) ) {s = 12;}

                        else if ( (LA65_7==LEFT_PAREN) ) {s = 13;}

                        else if ( (LA65_7==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( ((LA65_7>=VT_COMPILATION_UNIT && LA65_7<=SEMICOLON)||(LA65_7>=DOT && LA65_7<=STRING)||LA65_7==COMMA||(LA65_7>=AT && LA65_7<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 4;}

                         
                        input.seek(index65_7);
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
                        int LA65_3 = input.LA(1);

                         
                        int index65_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_3==COLON) ) {s = 7;}

                        else if ( (LA65_3==LEFT_PAREN) ) {s = 8;}

                        else if ( (LA65_3==ID) ) {s = 9;}

                        else if ( (LA65_3==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( (LA65_3==DOT) ) {s = 10;}

                        else if ( (LA65_3==LEFT_SQUARE) ) {s = 11;}

                        else if ( ((LA65_3>=VT_COMPILATION_UNIT && LA65_3<=SEMICOLON)||(LA65_3>=DOT_STAR && LA65_3<=STRING)||LA65_3==COMMA||LA65_3==AT||(LA65_3>=EQUALS && LA65_3<=NULL)||(LA65_3>=RIGHT_SQUARE && LA65_3<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 4;}

                         
                        input.seek(index65_3);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA65_13 = input.LA(1);

                         
                        int index65_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8()) ) {s = 15;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index65_13);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA65_8 = input.LA(1);

                         
                        int index65_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8()) ) {s = 6;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index65_8);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA65_10 = input.LA(1);

                         
                        int index65_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_10==ID) ) {s = 14;}

                        else if ( (LA65_10==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( ((LA65_10>=VT_COMPILATION_UNIT && LA65_10<=SEMICOLON)||(LA65_10>=DOT && LA65_10<=STRING)||LA65_10==COMMA||(LA65_10>=AT && LA65_10<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 4;}

                        else if ( (LA65_10==LEFT_PAREN) && (synpred8())) {s = 15;}

                         
                        input.seek(index65_10);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA65_5 = input.LA(1);

                         
                        int index65_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8()) ) {s = 6;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index65_5);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA65_1 = input.LA(1);

                         
                        int index65_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_1==ID) ) {s = 3;}

                        else if ( ((LA65_1>=VT_COMPILATION_UNIT && LA65_1<=SEMICOLON)||(LA65_1>=DOT && LA65_1<=STRING)||LA65_1==COMMA||(LA65_1>=AT && LA65_1<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 4;}

                        else if ( (LA65_1==LEFT_PAREN) ) {s = 5;}

                        else if ( (LA65_1==RIGHT_PAREN) && (synpred8())) {s = 6;}

                         
                        input.seek(index65_1);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA65_16 = input.LA(1);

                         
                        int index65_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_16==LEFT_PAREN) ) {s = 20;}

                        else if ( (LA65_16==LEFT_SQUARE) ) {s = 11;}

                        else if ( (LA65_16==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( ((LA65_16>=VT_COMPILATION_UNIT && LA65_16<=STRING)||LA65_16==COMMA||(LA65_16>=AT && LA65_16<=NULL)||(LA65_16>=RIGHT_SQUARE && LA65_16<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 4;}

                         
                        input.seek(index65_16);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA65_14 = input.LA(1);

                         
                        int index65_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_14==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( (LA65_14==LEFT_SQUARE) ) {s = 11;}

                        else if ( (LA65_14==LEFT_PAREN) ) {s = 20;}

                        else if ( (LA65_14==DOT) ) {s = 10;}

                        else if ( ((LA65_14>=VT_COMPILATION_UNIT && LA65_14<=ID)||(LA65_14>=DOT_STAR && LA65_14<=STRING)||LA65_14==COMMA||(LA65_14>=AT && LA65_14<=NULL)||(LA65_14>=RIGHT_SQUARE && LA65_14<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 4;}

                         
                        input.seek(index65_14);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA65_11 = input.LA(1);

                         
                        int index65_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_11==RIGHT_SQUARE) ) {s = 16;}

                        else if ( (LA65_11==RIGHT_PAREN) && (synpred8())) {s = 6;}

                        else if ( ((LA65_11>=VT_COMPILATION_UNIT && LA65_11<=STRING)||LA65_11==COMMA||(LA65_11>=AT && LA65_11<=LEFT_SQUARE)||(LA65_11>=THEN && LA65_11<=MULTI_LINE_COMMENT)) && (synpred8())) {s = 4;}

                        else if ( (LA65_11==LEFT_PAREN) && (synpred8())) {s = 15;}

                         
                        input.seek(index65_11);
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
        "\40\uffff";
    static final String DFA80_eofS =
        "\40\uffff";
    static final String DFA80_minS =
        "\1\130\1\uffff\2\122\1\uffff\1\0\2\122\1\0\1\4\6\0\1\162\1\4\2\0"+
        "\1\4\13\0";
    static final String DFA80_maxS =
        "\1\141\1\uffff\1\157\1\162\1\uffff\1\0\1\162\1\122\1\0\1\u0080\6"+
        "\0\1\162\1\u0080\2\0\1\u0080\13\0";
    static final String DFA80_acceptS =
        "\1\uffff\1\2\2\uffff\1\1\33\uffff";
    static final String DFA80_specialS =
        "\2\uffff\1\6\1\0\1\uffff\1\10\1\2\1\uffff\1\14\1\uffff\1\5\1\3\1"+
        "\12\1\4\1\11\1\1\2\uffff\1\13\1\7\14\uffff}>";
    static final String[] DFA80_transitionS = {
            "\2\1\6\uffff\1\2\1\1",
            "",
            "\1\3\4\uffff\1\5\21\uffff\7\4",
            "\1\6\1\1\2\uffff\1\4\1\10\3\uffff\1\1\2\uffff\2\4\11\uffff\6"+
            "\1\1\7\2\4\1\1",
            "",
            "\1\uffff",
            "\1\12\1\4\2\uffff\1\13\1\11\2\4\4\uffff\1\16\1\14\2\4\15\uffff"+
            "\1\1\1\15\1\17\1\4",
            "\1\20",
            "\1\uffff",
            "\116\31\1\21\3\31\1\24\1\22\1\31\1\23\4\31\1\27\1\25\20\31\1"+
            "\26\1\30\17\31",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\32",
            "\117\31\1\34\3\31\1\37\1\35\1\36\30\31\1\33\16\31",
            "\1\uffff",
            "\1\uffff",
            "\123\31\1\37\1\35\1\36\47\31",
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
            return "()* loopback of 1046:25: ({...}? => DOUBLE_PIPE and_restr_connective )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA80_3 = input.LA(1);

                         
                        int index80_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA80_3==DOT||LA80_3==COLON||(LA80_3>=EQUAL && LA80_3<=NOT_EQUAL)||LA80_3==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA80_3==ID) ) {s = 6;}

                        else if ( (LA80_3==GRAVE_ACCENT) ) {s = 7;}

                        else if ( (LA80_3==STRING||(LA80_3>=BOOL && LA80_3<=INT)||(LA80_3>=FLOAT && LA80_3<=NULL)) && ((validateRestr()))) {s = 4;}

                        else if ( (LA80_3==LEFT_PAREN) ) {s = 8;}

                         
                        input.seek(index80_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA80_15 = input.LA(1);

                         
                        int index80_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_15);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA80_6 = input.LA(1);

                         
                        int index80_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA80_6==LEFT_PAREN) ) {s = 9;}

                        else if ( (LA80_6==ID) ) {s = 10;}

                        else if ( (LA80_6==STRING) ) {s = 11;}

                        else if ( (LA80_6==INT) ) {s = 12;}

                        else if ( (LA80_6==FLOAT) ) {s = 13;}

                        else if ( (LA80_6==BOOL) ) {s = 14;}

                        else if ( (LA80_6==NULL) ) {s = 15;}

                        else if ( (LA80_6==GRAVE_ACCENT) ) {s = 1;}

                        else if ( (LA80_6==DOT||(LA80_6>=COMMA && LA80_6<=RIGHT_PAREN)||(LA80_6>=DOUBLE_PIPE && LA80_6<=DOUBLE_AMPER)||LA80_6==LEFT_SQUARE) && ((validateRestr()))) {s = 4;}

                         
                        input.seek(index80_6);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA80_11 = input.LA(1);

                         
                        int index80_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_11);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA80_13 = input.LA(1);

                         
                        int index80_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_13);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA80_10 = input.LA(1);

                         
                        int index80_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_10);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA80_2 = input.LA(1);

                         
                        int index80_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA80_2==ID) ) {s = 3;}

                        else if ( ((LA80_2>=EQUAL && LA80_2<=GRAVE_ACCENT)) && ((validateRestr()))) {s = 4;}

                        else if ( (LA80_2==LEFT_PAREN) ) {s = 5;}

                         
                        input.seek(index80_2);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA80_19 = input.LA(1);

                         
                        int index80_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_19);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA80_5 = input.LA(1);

                         
                        int index80_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_5);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA80_14 = input.LA(1);

                         
                        int index80_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_14);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA80_12 = input.LA(1);

                         
                        int index80_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_12);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA80_18 = input.LA(1);

                         
                        int index80_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_18);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA80_8 = input.LA(1);

                         
                        int index80_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index80_8);
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
        "\63\uffff";
    static final String DFA81_eofS =
        "\63\uffff";
    static final String DFA81_minS =
        "\1\130\1\uffff\2\122\1\uffff\1\122\1\4\3\122\1\0\1\4\2\0\2\4\43"+
        "\0";
    static final String DFA81_maxS =
        "\1\141\1\uffff\1\157\1\162\1\uffff\1\157\1\u0080\1\162\1\122\1\162"+
        "\1\0\1\u0080\2\0\2\u0080\43\0";
    static final String DFA81_acceptS =
        "\1\uffff\1\2\2\uffff\1\1\56\uffff";
    static final String DFA81_specialS =
        "\2\uffff\1\2\1\0\1\uffff\1\5\1\uffff\1\4\1\uffff\1\1\1\7\1\uffff"+
        "\1\3\1\6\45\uffff}>";
    static final String[] DFA81_transitionS = {
            "\2\1\6\uffff\1\1\1\2",
            "",
            "\1\3\4\uffff\1\5\21\uffff\7\4",
            "\1\7\1\1\2\uffff\1\4\1\6\3\uffff\1\1\2\uffff\2\4\11\uffff\6"+
            "\1\1\10\2\4\1\1",
            "",
            "\1\11\4\uffff\1\12\21\uffff\7\4",
            "\116\32\1\13\3\32\1\16\1\14\1\32\1\15\4\32\1\21\1\17\11\32\1"+
            "\23\1\24\1\25\1\26\1\27\1\30\1\31\1\20\1\22\17\32",
            "\1\33\1\4\2\uffff\1\34\1\41\2\4\4\uffff\1\37\1\35\2\4\15\uffff"+
            "\1\1\1\36\1\40\1\4",
            "\1\42",
            "\1\43\1\1\2\uffff\1\4\1\45\3\uffff\1\1\2\uffff\2\4\11\uffff"+
            "\6\1\1\44\2\4\1\1",
            "\1\uffff",
            "\116\32\1\46\1\57\2\32\1\47\1\54\1\60\1\61\4\32\1\52\1\50\17"+
            "\32\1\55\1\51\1\53\1\56\16\32",
            "\1\uffff",
            "\1\uffff",
            "\123\32\1\62\1\60\1\61\47\32",
            "\123\32\1\62\1\60\1\61\47\32",
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
            return "()* loopback of 1052:26: ({...}? => DOUBLE_AMPER constraint_expression )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
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
                    case 1 : 
                        int LA81_9 = input.LA(1);

                         
                        int index81_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA81_9==DOT||LA81_9==COLON||(LA81_9>=EQUAL && LA81_9<=NOT_EQUAL)||LA81_9==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA81_9==ID) ) {s = 35;}

                        else if ( (LA81_9==GRAVE_ACCENT) ) {s = 36;}

                        else if ( (LA81_9==LEFT_PAREN) ) {s = 37;}

                        else if ( (LA81_9==STRING||(LA81_9>=BOOL && LA81_9<=INT)||(LA81_9>=FLOAT && LA81_9<=NULL)) && ((validateRestr()))) {s = 4;}

                         
                        input.seek(index81_9);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
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
                    case 3 : 
                        int LA81_12 = input.LA(1);

                         
                        int index81_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index81_12);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA81_7 = input.LA(1);

                         
                        int index81_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA81_7==DOT||(LA81_7>=COMMA && LA81_7<=RIGHT_PAREN)||(LA81_7>=DOUBLE_PIPE && LA81_7<=DOUBLE_AMPER)||LA81_7==LEFT_SQUARE) && ((validateRestr()))) {s = 4;}

                        else if ( (LA81_7==ID) ) {s = 27;}

                        else if ( (LA81_7==STRING) ) {s = 28;}

                        else if ( (LA81_7==INT) ) {s = 29;}

                        else if ( (LA81_7==FLOAT) ) {s = 30;}

                        else if ( (LA81_7==BOOL) ) {s = 31;}

                        else if ( (LA81_7==NULL) ) {s = 32;}

                        else if ( (LA81_7==LEFT_PAREN) ) {s = 33;}

                        else if ( (LA81_7==GRAVE_ACCENT) ) {s = 1;}

                         
                        input.seek(index81_7);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA81_5 = input.LA(1);

                         
                        int index81_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA81_5==ID) ) {s = 9;}

                        else if ( (LA81_5==LEFT_PAREN) ) {s = 10;}

                        else if ( ((LA81_5>=EQUAL && LA81_5<=GRAVE_ACCENT)) && ((validateRestr()))) {s = 4;}

                         
                        input.seek(index81_5);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA81_13 = input.LA(1);

                         
                        int index81_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index81_13);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA81_10 = input.LA(1);

                         
                        int index81_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateRestr())) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index81_10);
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
 

    public static final BitSet FOLLOW_package_statement_in_compilation_unit384 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_statement_in_compilation_unit389 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_EOF_in_compilation_unit394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_package_key_in_package_statement450 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_package_id_in_package_statement454 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_package_statement456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_id483 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_package_id489 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_package_id493 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_rule_attribute_in_statement537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_declaration_in_statement578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_import_statement615 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_import_name_in_import_statement617 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_import_statement620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_function_import_statement663 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_function_key_in_function_import_statement665 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement667 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_function_import_statement670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name704 = new BitSet(new long[]{0x0000000000000002L,0x0000000000180000L});
    public static final BitSet FOLLOW_DOT_in_import_name710 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_import_name714 = new BitSet(new long[]{0x0000000000000002L,0x0000000000180000L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_key_in_global766 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_data_type_in_global768 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_global_id_in_global770 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_global772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_id801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_key_in_function838 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_data_type_in_function840 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_function_id_in_function843 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_parameters_in_function845 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_id877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_key_in_query914 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_query_id_in_query916 = new BitSet(new long[]{0x0000000000000000L,0x0000000000A40000L});
    public static final BitSet FOLLOW_parameters_in_query924 = new BitSet(new long[]{0x0000000000000000L,0x0000000000A40000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query933 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_END_in_query938 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_query940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_id975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_query_id991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parameters1010 = new BitSet(new long[]{0x0000000000000000L,0x0000000002040000L});
    public static final BitSet FOLLOW_param_definition_in_parameters1019 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000000L});
    public static final BitSet FOLLOW_COMMA_in_parameters1022 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_param_definition_in_parameters1026 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parameters1035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_param_definition1061 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_argument_in_param_definition1064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument1075 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_dimension_definition_in_argument1081 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_declare_key_in_type_declaration1109 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_type_declare_id_in_type_declaration1112 = new BitSet(new long[]{0x0000000000000000L,0x0000000004240000L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration1116 = new BitSet(new long[]{0x0000000000000000L,0x0000000004240000L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration1121 = new BitSet(new long[]{0x0000000000000000L,0x0000000000240000L});
    public static final BitSet FOLLOW_END_in_type_declaration1126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_type_declare_id1161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_decl_metadata1180 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_decl_metadata1188 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_decl_metadata1195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_decl_field1218 = new BitSet(new long[]{0x0000000000000000L,0x0000000018000000L});
    public static final BitSet FOLLOW_decl_field_initialization_in_decl_field1224 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_decl_field1230 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_data_type_in_decl_field1236 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000000L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field1240 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000000L});
    public static final BitSet FOLLOW_EQUALS_in_decl_field_initialization1268 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_decl_field_initialization1274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_key_in_template1311 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_template_id_in_template1313 = new BitSet(new long[]{0x0000000000000000L,0x0000000000060000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1320 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_template_slot_in_template1328 = new BitSet(new long[]{0x0000000000000000L,0x0000000000240000L});
    public static final BitSet FOLLOW_END_in_template1333 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_id1370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_template_id1386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_template_slot1406 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_slot_id_in_template_slot1408 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template_slot1410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_slot_id1439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_key_in_rule1476 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_rule_id_in_rule1478 = new BitSet(new long[]{0x0000000000000000L,0x0010000020040000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule1488 = new BitSet(new long[]{0x0000000000000000L,0x0010000020000000L});
    public static final BitSet FOLLOW_when_part_in_rule1491 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_when_part1524 = new BitSet(new long[]{0x0000000000000002L,0x0000000008840000L});
    public static final BitSet FOLLOW_COLON_in_when_part1530 = new BitSet(new long[]{0x0000000000000002L,0x0000000000840000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_when_part1540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_id1561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_rule_id1577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attributes_key_in_rule_attributes1598 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_rule_attributes1600 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1610 = new BitSet(new long[]{0x0000000000000002L,0x0000000001040000L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1614 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1621 = new BitSet(new long[]{0x0000000000000002L,0x0000000001040000L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_key_in_date_effective1740 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_key_in_date_expires1757 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_key_in_enabled1775 = new BitSet(new long[]{0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_salience_key_in_salience1793 = new BitSet(new long[]{0x0000000000000000L,0x0000000080800000L});
    public static final BitSet FOLLOW_INT_in_salience1800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_key_in_no_loop1824 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_key_in_auto_focus1842 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_key_in_activation_group1862 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_key_in_ruleflow_group1879 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_key_in_agenda_group1896 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_key_in_duration1913 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_INT_in_duration1916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_key_in_dialect1932 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_dialect1935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_key_in_lock_on_active1953 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1971 = new BitSet(new long[]{0x0000000000000002L,0x0000000000840000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or2016 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or2026 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2034 = new BitSet(new long[]{0x0000000000000000L,0x0000000002840000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or2040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2063 = new BitSet(new long[]{0x0000000000000002L,0x0000000100040000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or2085 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_lhs_or2092 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2103 = new BitSet(new long[]{0x0000000000000002L,0x0000000100040000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and2144 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and2154 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2162 = new BitSet(new long[]{0x0000000000000000L,0x0000000002840000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and2168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2192 = new BitSet(new long[]{0x0000000000000002L,0x0000000200040000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and2214 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_lhs_and2221 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2232 = new BitSet(new long[]{0x0000000000000002L,0x0000000200040000L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2263 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_lhs_not_binding_in_lhs_unary2271 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2277 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2283 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary2289 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2295 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary2306 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2312 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_unary2320 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_lhs_unary2334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_key_in_lhs_exist2350 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist2384 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2392 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist2399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist2414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not_binding2460 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_not_binding2462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not2485 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2514 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2523 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_key_in_lhs_eval2578 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forall_key_in_lhs_forall2614 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2619 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2627 = new BitSet(new long[]{0x0000000000000000L,0x0000000002040000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall2633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_pattern_source2669 = new BitSet(new long[]{0x0000000000000002L,0x0000000C00000000L});
    public static final BitSet FOLLOW_over_clause_in_pattern_source2673 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_FROM_in_pattern_source2683 = new BitSet(new long[]{0x0000000000000000L,0x0000009000040000L});
    public static final BitSet FOLLOW_accumulate_statement_in_pattern_source2703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_pattern_source2719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entrypoint_statement_in_pattern_source2736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_pattern_source2752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause2784 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2789 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_COMMA_in_over_clause2796 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2801 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_over_elements2816 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_over_elements2823 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_over_elements2832 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_over_elements2839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement2865 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2874 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_lhs_or_in_accumulate_statement2882 = new BitSet(new long[]{0x0000000000000000L,0x0000002001040000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2887 = new BitSet(new long[]{0x0000000000000000L,0x0000002000040000L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_statement2897 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_statement2903 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INIT_in_accumulate_init_clause2946 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2957 = new BitSet(new long[]{0x0000000000000000L,0x0000000001040000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2961 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_action_key_in_accumulate_init_clause2972 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2976 = new BitSet(new long[]{0x0000000000000000L,0x0000004001040000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2980 = new BitSet(new long[]{0x0000000000000000L,0x0000004000040000L});
    public static final BitSet FOLLOW_reverse_key_in_accumulate_init_clause2993 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2997 = new BitSet(new long[]{0x0000000000000000L,0x0000004001000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause3001 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_init_clause3017 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause3023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause3074 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_id_clause3080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement3102 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement3111 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_pattern_source_in_collect_statement3118 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement3123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entry_point_key_in_entrypoint_statement3150 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_entrypoint_id_in_entrypoint_statement3158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entrypoint_id3184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_entrypoint_id3201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source3221 = new BitSet(new long[]{0x0000000000000002L,0x0000000000880000L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source3236 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_expression_chain_in_from_source3243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain3276 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_expression_chain3283 = new BitSet(new long[]{0x0000000000000002L,0x0004000000880000L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain3305 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain3327 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain3338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern3371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern3384 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_fact_binding3404 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_fact_in_fact_binding3410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding3417 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_fact_binding_expression_in_fact_binding3425 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding3433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression3474 = new BitSet(new long[]{0x0000000000000002L,0x0000000100040000L});
    public static final BitSet FOLLOW_or_key_in_fact_binding_expression3486 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3492 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression3497 = new BitSet(new long[]{0x0000000000000002L,0x0000000100040000L});
    public static final BitSet FOLLOW_pattern_type_in_fact3537 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact3542 = new BitSet(new long[]{0x0000000000000000L,0x0000000002840000L});
    public static final BitSet FOLLOW_constraints_in_fact3553 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact3559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints3589 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_COMMA_in_constraints3593 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_constraint_in_constraints3600 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_or_constr_in_constraint3614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3625 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr3629 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3636 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3651 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr3655 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3662 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_eval_key_in_unary_constr3695 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_unary_constr3698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr3703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr3709 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr3719 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr3724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_field_constraint3744 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3746 = new BitSet(new long[]{0x0000000000000002L,0x0000FF0000840000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_field_constraint3762 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_field_constraint3766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3820 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0000840000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_label3849 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_label3856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3877 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective3883 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0000840000L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3891 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3906 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective3912 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0000840000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3919 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression3941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression3946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression3951 = new BitSet(new long[]{0x0000000000000000L,0x0000FE0000840000L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression3963 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression3968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUAL_in_simple_operator3993 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_GREATER_in_simple_operator4001 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_simple_operator4009 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_LESS_in_simple_operator4017 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_simple_operator4025 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_simple_operator4033 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_not_key_in_simple_operator4041 = new BitSet(new long[]{0x0000000000000000L,0x0000800000040000L});
    public static final BitSet FOLLOW_contains_key_in_simple_operator4048 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_soundslike_key_in_simple_operator4055 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_matches_key_in_simple_operator4062 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_memberof_key_in_simple_operator4069 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_ID_in_simple_operator4078 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_GRAVE_ACCENT_in_simple_operator4089 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_simple_operator4097 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator4103 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_contains_key_in_simple_operator4109 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_excludes_key_in_simple_operator4115 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_matches_key_in_simple_operator4121 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_soundslike_key_in_simple_operator4127 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_memberof_key_in_simple_operator4133 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_ID_in_simple_operator4141 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_GRAVE_ACCENT_in_simple_operator4151 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_simple_operator4159 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator4165 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator4172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_compound_operator4187 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_not_key_in_compound_operator4192 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_in_key_in_compound_operator4194 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator4205 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4213 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator4217 = new BitSet(new long[]{0x0000000000000000L,0x00030000C0C40000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4222 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator4230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_expression_value4251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value4256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_value4262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint4281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint4288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint4295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint4302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint4309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pattern_type4324 = new BitSet(new long[]{0x0000000000000002L,0x0004000000080000L});
    public static final BitSet FOLLOW_DOT_in_pattern_type4330 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_pattern_type4334 = new BitSet(new long[]{0x0000000000000002L,0x0004000000080000L});
    public static final BitSet FOLLOW_dimension_definition_in_pattern_type4349 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_ID_in_data_type4377 = new BitSet(new long[]{0x0000000000000002L,0x0004000000080000L});
    public static final BitSet FOLLOW_DOT_in_data_type4383 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_data_type4387 = new BitSet(new long[]{0x0000000000000002L,0x0004000000080000L});
    public static final BitSet FOLLOW_dimension_definition_in_data_type4392 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dimension_definition4421 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dimension_definition4428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4442 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_accessor_path4446 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4450 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_accessor_element4474 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_square_chunk_in_accessor_element4480 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_data_in_rhs_chunk4509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk_data4528 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_rhs_chunk_data4541 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_END_in_rhs_chunk_data4554 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_rhs_chunk_data4560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk4579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk_data4602 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_curly_chunk_data4614 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk_data4630 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk_data4641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk4662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk_data4686 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_paren_chunk_data4698 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk_data4714 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk_data4725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk4746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk_data4769 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_square_chunk_data4781 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk_data4796 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL,0x0000000000000001L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk_data4807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4831 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key4835 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4839 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key4843 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_effective_key4879 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_effective_key4883 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_date_effective_key4887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_expires_key4919 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_expires_key4923 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_date_expires_key4927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_no_loop_key4959 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_no_loop_key4963 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_no_loop_key4967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key4999 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_auto_focus_key5003 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key5007 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_activation_group_key5039 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_activation_group_key5043 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_activation_group_key5047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key5079 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_agenda_group_key5083 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key5087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key5119 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_ruleflow_group_key5123 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key5127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entry_point_key5159 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_MISC_in_entry_point_key5163 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_entry_point_key5167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_duration_key5196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_key5223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_key5250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dialect_key5277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_salience_key5304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enabled_key5331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_attributes_key5358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_key5385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_key5412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_key5439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_declare_key5466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_key5493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_key5520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_eval_key5547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_contains_key5574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_matches_key5601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_excludes_key5628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_soundslike_key5655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_memberof_key5682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key5709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key5736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_or_key5763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_and_key5790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_exists_key5817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_forall_key5844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_action_key5871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_reverse_key5898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred12007 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_or_key_in_synpred12009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_key_in_synpred22076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred22078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred32135 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_and_key_in_synpred32137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred42205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred42207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_synpred52330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred62367 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_or_key_in_synpred62370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred62372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred72495 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_or_key_in_synpred72498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred72500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred83230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred93299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred103321 = new BitSet(new long[]{0x0000000000000002L});

}