// $ANTLR 3.3 Nov 30, 2010 12:46:29 src/main/resources/org/drools/lang/DRL5x.g 2011-01-17 16:59:38

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

public class DRL5xParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TEMPLATE_ID", "VT_TYPE_DECLARE_ID", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_SLOT_ID", "VT_SLOT", "VT_RULE_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_TIMER", "VK_CALENDARS", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_RULE", "VK_EXTEND", "VK_IMPORT", "VK_PACKAGE", "VK_TEMPLATE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "VK_OPERATOR", "VK_END", "VK_INIT", "SEMICOLON", "ID", "DOT", "DOT_STAR", "STRING", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "AT", "COLON", "EQUALS", "WHEN", "BOOL", "INT", "DOUBLE_PIPE", "DOUBLE_AMPER", "FROM", "OVER", "ACCUMULATE", "COLLECT", "ARROW", "EQUAL", "GREATER", "GREATER_EQUAL", "LESS", "LESS_EQUAL", "NOT_EQUAL", "FLOAT", "NULL", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "MISC", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart"
    };
    public static final int EOF=-1;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VT_FACT=6;
    public static final int VT_CONSTRAINTS=7;
    public static final int VT_LABEL=8;
    public static final int VT_QUERY_ID=9;
    public static final int VT_TEMPLATE_ID=10;
    public static final int VT_TYPE_DECLARE_ID=11;
    public static final int VT_RULE_ID=12;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int VT_SLOT_ID=14;
    public static final int VT_SLOT=15;
    public static final int VT_RULE_ATTRIBUTES=16;
    public static final int VT_RHS_CHUNK=17;
    public static final int VT_CURLY_CHUNK=18;
    public static final int VT_SQUARE_CHUNK=19;
    public static final int VT_PAREN_CHUNK=20;
    public static final int VT_BEHAVIOR=21;
    public static final int VT_AND_IMPLICIT=22;
    public static final int VT_AND_PREFIX=23;
    public static final int VT_OR_PREFIX=24;
    public static final int VT_AND_INFIX=25;
    public static final int VT_OR_INFIX=26;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=27;
    public static final int VT_ACCUMULATE_ID_CLAUSE=28;
    public static final int VT_FROM_SOURCE=29;
    public static final int VT_EXPRESSION_CHAIN=30;
    public static final int VT_PATTERN=31;
    public static final int VT_FACT_BINDING=32;
    public static final int VT_FACT_OR=33;
    public static final int VT_BIND_FIELD=34;
    public static final int VT_FIELD=35;
    public static final int VT_ACCESSOR_PATH=36;
    public static final int VT_ACCESSOR_ELEMENT=37;
    public static final int VT_DATA_TYPE=38;
    public static final int VT_PATTERN_TYPE=39;
    public static final int VT_PACKAGE_ID=40;
    public static final int VT_IMPORT_ID=41;
    public static final int VT_GLOBAL_ID=42;
    public static final int VT_FUNCTION_ID=43;
    public static final int VT_PARAM_LIST=44;
    public static final int VK_DATE_EFFECTIVE=45;
    public static final int VK_DATE_EXPIRES=46;
    public static final int VK_LOCK_ON_ACTIVE=47;
    public static final int VK_NO_LOOP=48;
    public static final int VK_AUTO_FOCUS=49;
    public static final int VK_ACTIVATION_GROUP=50;
    public static final int VK_AGENDA_GROUP=51;
    public static final int VK_RULEFLOW_GROUP=52;
    public static final int VK_TIMER=53;
    public static final int VK_CALENDARS=54;
    public static final int VK_DIALECT=55;
    public static final int VK_SALIENCE=56;
    public static final int VK_ENABLED=57;
    public static final int VK_ATTRIBUTES=58;
    public static final int VK_RULE=59;
    public static final int VK_EXTEND=60;
    public static final int VK_IMPORT=61;
    public static final int VK_PACKAGE=62;
    public static final int VK_TEMPLATE=63;
    public static final int VK_QUERY=64;
    public static final int VK_DECLARE=65;
    public static final int VK_FUNCTION=66;
    public static final int VK_GLOBAL=67;
    public static final int VK_EVAL=68;
    public static final int VK_ENTRY_POINT=69;
    public static final int VK_NOT=70;
    public static final int VK_IN=71;
    public static final int VK_OR=72;
    public static final int VK_AND=73;
    public static final int VK_EXISTS=74;
    public static final int VK_FORALL=75;
    public static final int VK_ACTION=76;
    public static final int VK_REVERSE=77;
    public static final int VK_RESULT=78;
    public static final int VK_OPERATOR=79;
    public static final int VK_END=80;
    public static final int VK_INIT=81;
    public static final int SEMICOLON=82;
    public static final int ID=83;
    public static final int DOT=84;
    public static final int DOT_STAR=85;
    public static final int STRING=86;
    public static final int LEFT_PAREN=87;
    public static final int COMMA=88;
    public static final int RIGHT_PAREN=89;
    public static final int AT=90;
    public static final int COLON=91;
    public static final int EQUALS=92;
    public static final int WHEN=93;
    public static final int BOOL=94;
    public static final int INT=95;
    public static final int DOUBLE_PIPE=96;
    public static final int DOUBLE_AMPER=97;
    public static final int FROM=98;
    public static final int OVER=99;
    public static final int ACCUMULATE=100;
    public static final int COLLECT=101;
    public static final int ARROW=102;
    public static final int EQUAL=103;
    public static final int GREATER=104;
    public static final int GREATER_EQUAL=105;
    public static final int LESS=106;
    public static final int LESS_EQUAL=107;
    public static final int NOT_EQUAL=108;
    public static final int FLOAT=109;
    public static final int NULL=110;
    public static final int LEFT_SQUARE=111;
    public static final int RIGHT_SQUARE=112;
    public static final int THEN=113;
    public static final int LEFT_CURLY=114;
    public static final int RIGHT_CURLY=115;
    public static final int MISC=116;
    public static final int EOL=117;
    public static final int WS=118;
    public static final int EscapeSequence=119;
    public static final int HexDigit=120;
    public static final int UnicodeEscape=121;
    public static final int OctalEscape=122;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=123;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=124;
    public static final int MULTI_LINE_COMMENT=125;
    public static final int IdentifierStart=126;
    public static final int IdentifierPart=127;

    // delegates
    // delegators


        public DRL5xParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DRL5xParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return DRL5xParser.tokenNames; }
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/DRL5x.g"; }


    	private Stack<Map<DroolsParaphraseTypes, String>> paraphrases = new Stack<Map<DroolsParaphraseTypes, String>>();
    	private List<DroolsParserException> errors = new ArrayList<DroolsParserException>();
    	private DroolsParserExceptionFactory errorMessageFactory = new DroolsParserExceptionFactory(tokenNames, paraphrases);
    	private String source = "unknown";
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
    				getActiveSentence().addContent(activeContext);
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

    	private String retrieveLT(int LTNumber) {
          		if (null == input)
    			return null;
    		if (null == input.LT(LTNumber))
    			return null;
    		if (null == input.LT(LTNumber).getText())
    			return null;
    	
    		return input.LT(LTNumber).getText();
    	}

    	private boolean validateLT(int LTNumber, String text) {
    		String text2Validate = retrieveLT( LTNumber );
    		return text2Validate == null ? false : text2Validate.equalsIgnoreCase(text);
    	}
    	
    	private boolean isPluggableEvaluator( int offset, boolean negated ) {
    		String text2Validate = retrieveLT( offset );
    	        return text2Validate == null ? false : DroolsSoftKeywords.isOperator( text2Validate, negated );
    	}
    	
    	private boolean isPluggableEvaluator( boolean negated ) {
    	        return isPluggableEvaluator( 1, negated );
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
    		if (state.errorRecovery) {
    			return;
    		}
    		state.errorRecovery = true;
    	
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
    	
    	/** Overrided this method to not output mesages */
    	public void emitErrorMessage(String msg) {
    	}


    public static class compilation_unit_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "compilation_unit"
    // src/main/resources/org/drools/lang/DRL5x.g:396:1: compilation_unit : ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) ;
    public final DRL5xParser.compilation_unit_return compilation_unit() throws RecognitionException {
        DRL5xParser.compilation_unit_return retval = new DRL5xParser.compilation_unit_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF3=null;
        DRL5xParser.package_statement_return package_statement1 = null;

        DRL5xParser.statement_return statement2 = null;


        Object EOF3_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        RewriteRuleSubtreeStream stream_package_statement=new RewriteRuleSubtreeStream(adaptor,"rule package_statement");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:397:2: ( ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) )
            // src/main/resources/org/drools/lang/DRL5x.g:397:4: ( package_statement )? ( statement )* EOF
            {
            // src/main/resources/org/drools/lang/DRL5x.g:397:4: ( package_statement )?
            int alt1=2;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:397:4: package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_compilation_unit388);
                    package_statement1=package_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_package_statement.add(package_statement1.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL5x.g:398:3: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:398:3: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit393);
            	    statement2=statement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_statement.add(statement2.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            EOF3=(Token)match(input,EOF,FOLLOW_EOF_in_compilation_unit398); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF3);



            // AST REWRITE
            // elements: statement, package_statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 400:3: -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:400:6: ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_COMPILATION_UNIT, "VT_COMPILATION_UNIT"), root_1);

                // src/main/resources/org/drools/lang/DRL5x.g:400:28: ( package_statement )?
                if ( stream_package_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_package_statement.nextTree());

                }
                stream_package_statement.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:400:47: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_statement.nextTree());

                }
                stream_statement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

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

            	if (isEditorInterfaceEnabled && retval.tree == null) {
            		retval.tree = root_0;
            		root_0 = (Object) adaptor.nil();
            		Object root_1 = (Object) adaptor.nil();
            		root_1 = (Object) adaptor.becomeRoot(adaptor.create(
            				VT_COMPILATION_UNIT, "VT_COMPILATION_UNIT"), root_1);
            		if (stream_package_statement.hasNext()) {
            			adaptor.addChild(root_1, stream_package_statement.nextTree());
            		}
            		while (stream_statement.hasNext()) {
            			adaptor.addChild(root_1, stream_statement.nextTree());
            		}
            		adaptor.addChild(root_0, root_1);
            		retval.stop = input.LT(-1);
            		retval.tree = (Object) adaptor.rulePostProcessing(root_0);
            		adaptor.setTokenBoundaries(retval.tree, retval.start,
            				retval.stop);
            	}
            	if (isEditorInterfaceEnabled && hasErrors()) {
            		Tree rootNode = (Tree) adaptor.becomeRoot(adaptor.create(
            				VT_COMPILATION_UNIT, "VT_COMPILATION_UNIT"), adaptor.nil());
            		for (int i = 0; i < ((Tree)retval.tree).getChildCount(); i++) {
            			Tree childNode = (Tree) ((Tree)retval.tree).getChild(i);
            			if (!(childNode instanceof CommonErrorNode)) {
            				rootNode.addChild(childNode);
            			}
            		}
            		retval.tree = rootNode; 
            	}

        }
        return retval;
    }
    // $ANTLR end "compilation_unit"

    public static class package_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "package_statement"
    // src/main/resources/org/drools/lang/DRL5x.g:439:1: package_statement : package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) ;
    public final DRL5xParser.package_statement_return package_statement() throws RecognitionException {
        DRL5xParser.package_statement_return retval = new DRL5xParser.package_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON6=null;
        DRL5xParser.package_key_return package_key4 = null;

        DRL5xParser.package_id_return package_id5 = null;


        Object SEMICOLON6_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_package_key=new RewriteRuleSubtreeStream(adaptor,"rule package_key");
        RewriteRuleSubtreeStream stream_package_id=new RewriteRuleSubtreeStream(adaptor,"rule package_id");
         pushParaphrases(DroolsParaphraseTypes.PACKAGE); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.PACKAGE); 
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:442:2: ( package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) )
            // src/main/resources/org/drools/lang/DRL5x.g:442:4: package_key package_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_package_key_in_package_statement453);
            package_key4=package_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_key.add(package_key4.getTree());
            pushFollow(FOLLOW_package_id_in_package_statement457);
            package_id5=package_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_id.add(package_id5.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:443:14: ( SEMICOLON )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==SEMICOLON) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:443:14: SEMICOLON
                    {
                    SEMICOLON6=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_package_statement459); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON6);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON6, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: package_key, package_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 445:3: -> ^( package_key package_id )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:445:6: ^( package_key package_id )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_package_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_package_id.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "package_statement"

    public static class package_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "package_id"
    // src/main/resources/org/drools/lang/DRL5x.g:448:1: package_id : id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) ;
    public final DRL5xParser.package_id_return package_id() throws RecognitionException {
        DRL5xParser.package_id_return retval = new DRL5xParser.package_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:449:2: (id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) )
            // src/main/resources/org/drools/lang/DRL5x.g:449:4: id+= ID (id+= DOT id+= ID )*
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_package_id486); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // src/main/resources/org/drools/lang/DRL5x.g:449:11: (id+= DOT id+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:449:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_package_id492); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_package_id496); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            if ( state.backtracking==0 ) {
              	emit(list_id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.PACKAGE, buildStringFromTokens(list_id));	
            }


            // AST REWRITE
            // elements: ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 452:3: -> ^( VT_PACKAGE_ID ( ID )+ )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:452:6: ^( VT_PACKAGE_ID ( ID )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_PACKAGE_ID, "VT_PACKAGE_ID"), root_1);

                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, stream_ID.nextNode());

                }
                stream_ID.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "package_id"

    public static class statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "statement"
    // src/main/resources/org/drools/lang/DRL5x.g:455:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );
    public final DRL5xParser.statement_return statement() throws RecognitionException {
        DRL5xParser.statement_return retval = new DRL5xParser.statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.rule_attribute_return rule_attribute7 = null;

        DRL5xParser.function_import_statement_return function_import_statement8 = null;

        DRL5xParser.import_statement_return import_statement9 = null;

        DRL5xParser.global_return global10 = null;

        DRL5xParser.function_return function11 = null;

        DRL5xParser.template_return template12 = null;

        DRL5xParser.type_declaration_return type_declaration13 = null;

        DRL5xParser.rule_return rule14 = null;

        DRL5xParser.query_return query15 = null;



        try {
            // src/main/resources/org/drools/lang/DRL5x.g:458:3: ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query )
            int alt5=9;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:458:5: rule_attribute
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_attribute_in_statement534);
                    rule_attribute7=rule_attribute();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rule_attribute7.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:459:3: {...}? => function_import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !(((validateLT(1, "import") && validateLT(2, "function") ))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, \"import\") && validateLT(2, \"function\") )");
                    }
                    pushFollow(FOLLOW_function_import_statement_in_statement541);
                    function_import_statement8=function_import_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function_import_statement8.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL5x.g:460:4: import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_import_statement_in_statement547);
                    import_statement9=import_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, import_statement9.getTree());

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRL5x.g:461:4: global
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_global_in_statement553);
                    global10=global();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, global10.getTree());

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRL5x.g:462:4: function
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_function_in_statement559);
                    function11=function();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function11.getTree());

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRL5x.g:463:4: {...}? => template
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !(((validateLT(1, DroolsSoftKeywords.TEMPLATE)))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, DroolsSoftKeywords.TEMPLATE))");
                    }
                    pushFollow(FOLLOW_template_in_statement567);
                    template12=template();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, template12.getTree());

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRL5x.g:464:4: {...}? => type_declaration
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !(((validateLT(1, DroolsSoftKeywords.DECLARE)))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, DroolsSoftKeywords.DECLARE))");
                    }
                    pushFollow(FOLLOW_type_declaration_in_statement575);
                    type_declaration13=type_declaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type_declaration13.getTree());

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DRL5x.g:465:4: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_statement580);
                    rule14=rule();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rule14.getTree());

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DRL5x.g:466:4: query
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_query_in_statement585);
                    query15=query();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, query15.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "statement"

    public static class import_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "import_statement"
    // src/main/resources/org/drools/lang/DRL5x.g:469:1: import_statement : import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) ;
    public final DRL5xParser.import_statement_return import_statement() throws RecognitionException {
        DRL5xParser.import_statement_return retval = new DRL5xParser.import_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON18=null;
        DRL5xParser.import_key_return import_key16 = null;

        DRL5xParser.import_name_return import_name17 = null;


        Object SEMICOLON18_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_import_key=new RewriteRuleSubtreeStream(adaptor,"rule import_key");
        RewriteRuleSubtreeStream stream_import_name=new RewriteRuleSubtreeStream(adaptor,"rule import_name");
         pushParaphrases(DroolsParaphraseTypes.IMPORT); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.IMPORT_STATEMENT);  
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:472:2: ( import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) )
            // src/main/resources/org/drools/lang/DRL5x.g:472:4: import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )?
            {
            pushFollow(FOLLOW_import_key_in_import_statement607);
            import_key16=import_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_key.add(import_key16.getTree());
            pushFollow(FOLLOW_import_name_in_import_statement609);
            import_name17=import_name(DroolsParaphraseTypes.IMPORT);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_name.add(import_name17.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:472:57: ( SEMICOLON )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==SEMICOLON) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:472:57: SEMICOLON
                    {
                    SEMICOLON18=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_import_statement612); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON18);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON18, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: import_key, import_name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 474:3: -> ^( import_key import_name )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:474:6: ^( import_key import_name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_import_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_import_name.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "import_statement"

    public static class function_import_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "function_import_statement"
    // src/main/resources/org/drools/lang/DRL5x.g:477:1: function_import_statement : imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) ;
    public final DRL5xParser.function_import_statement_return function_import_statement() throws RecognitionException {
        DRL5xParser.function_import_statement_return retval = new DRL5xParser.function_import_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON21=null;
        DRL5xParser.import_key_return imp = null;

        DRL5xParser.function_key_return function_key19 = null;

        DRL5xParser.import_name_return import_name20 = null;


        Object SEMICOLON21_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_function_key=new RewriteRuleSubtreeStream(adaptor,"rule function_key");
        RewriteRuleSubtreeStream stream_import_key=new RewriteRuleSubtreeStream(adaptor,"rule import_key");
        RewriteRuleSubtreeStream stream_import_name=new RewriteRuleSubtreeStream(adaptor,"rule import_name");
         pushParaphrases(DroolsParaphraseTypes.FUNCTION_IMPORT); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.FUNCTION_IMPORT_STATEMENT); 
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:480:2: (imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) )
            // src/main/resources/org/drools/lang/DRL5x.g:480:4: imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )?
            {
            pushFollow(FOLLOW_import_key_in_function_import_statement650);
            imp=import_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_key.add(imp.getTree());
            pushFollow(FOLLOW_function_key_in_function_import_statement652);
            function_key19=function_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_function_key.add(function_key19.getTree());
            pushFollow(FOLLOW_import_name_in_function_import_statement654);
            import_name20=import_name(DroolsParaphraseTypes.FUNCTION_IMPORT);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_name.add(import_name20.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:480:83: ( SEMICOLON )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==SEMICOLON) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:480:83: SEMICOLON
                    {
                    SEMICOLON21=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_function_import_statement657); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON21);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON21, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: function_key, import_name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 482:3: -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:482:6: ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FUNCTION_IMPORT, (imp!=null?((Token)imp.start):null)), root_1);

                adaptor.addChild(root_1, stream_function_key.nextTree());
                adaptor.addChild(root_1, stream_import_name.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "function_import_statement"

    public static class import_name_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "import_name"
    // src/main/resources/org/drools/lang/DRL5x.g:485:1: import_name[DroolsParaphraseTypes importType] : id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) ;
    public final DRL5xParser.import_name_return import_name(DroolsParaphraseTypes importType) throws RecognitionException {
        DRL5xParser.import_name_return retval = new DRL5xParser.import_name_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_DOT_STAR=new RewriteRuleTokenStream(adaptor,"token DOT_STAR");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:486:2: (id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) )
            // src/main/resources/org/drools/lang/DRL5x.g:486:4: id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )?
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_import_name691); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // src/main/resources/org/drools/lang/DRL5x.g:486:11: (id+= DOT id+= ID )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==DOT) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:486:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_import_name697); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_import_name701); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DRL5x.g:486:33: (id+= DOT_STAR )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==DOT_STAR) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:486:33: id+= DOT_STAR
                    {
                    id=(Token)match(input,DOT_STAR,FOLLOW_DOT_STAR_in_import_name708); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT_STAR.add(id);

                    if (list_id==null) list_id=new ArrayList();
                    list_id.add(id);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(list_id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(importType, buildStringFromTokens(list_id));	
            }


            // AST REWRITE
            // elements: DOT_STAR, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 489:3: -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:489:6: ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_IMPORT_ID, "VT_IMPORT_ID"), root_1);

                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, stream_ID.nextNode());

                }
                stream_ID.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:489:25: ( DOT_STAR )?
                if ( stream_DOT_STAR.hasNext() ) {
                    adaptor.addChild(root_1, stream_DOT_STAR.nextNode());

                }
                stream_DOT_STAR.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "import_name"

    public static class global_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "global"
    // src/main/resources/org/drools/lang/DRL5x.g:492:1: global : global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) ;
    public final DRL5xParser.global_return global() throws RecognitionException {
        DRL5xParser.global_return retval = new DRL5xParser.global_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON25=null;
        DRL5xParser.global_key_return global_key22 = null;

        DRL5xParser.data_type_return data_type23 = null;

        DRL5xParser.global_id_return global_id24 = null;


        Object SEMICOLON25_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_global_key=new RewriteRuleSubtreeStream(adaptor,"rule global_key");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        RewriteRuleSubtreeStream stream_global_id=new RewriteRuleSubtreeStream(adaptor,"rule global_id");
         pushParaphrases(DroolsParaphraseTypes.GLOBAL);  if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.GLOBAL); 
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:495:2: ( global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) )
            // src/main/resources/org/drools/lang/DRL5x.g:495:4: global_key data_type global_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_global_key_in_global748);
            global_key22=global_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_global_key.add(global_key22.getTree());
            pushFollow(FOLLOW_data_type_in_global750);
            data_type23=data_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_data_type.add(data_type23.getTree());
            pushFollow(FOLLOW_global_id_in_global752);
            global_id24=global_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_global_id.add(global_id24.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:495:35: ( SEMICOLON )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==SEMICOLON) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:495:35: SEMICOLON
                    {
                    SEMICOLON25=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_global754); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON25);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON25, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: global_key, global_id, data_type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 497:3: -> ^( global_key data_type global_id )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:497:6: ^( global_key data_type global_id )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_global_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_data_type.nextTree());
                adaptor.addChild(root_1, stream_global_id.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "global"

    public static class global_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "global_id"
    // src/main/resources/org/drools/lang/DRL5x.g:500:1: global_id : id= ID -> VT_GLOBAL_ID[$id] ;
    public final DRL5xParser.global_id_return global_id() throws RecognitionException {
        DRL5xParser.global_id_return retval = new DRL5xParser.global_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:501:2: (id= ID -> VT_GLOBAL_ID[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:501:4: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_global_id783); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.GLOBAL, (id!=null?id.getText():null));	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 504:3: -> VT_GLOBAL_ID[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_GLOBAL_ID, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "global_id"

    public static class function_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "function"
    // src/main/resources/org/drools/lang/DRL5x.g:507:1: function : function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) ;
    public final DRL5xParser.function_return function() throws RecognitionException {
        DRL5xParser.function_return retval = new DRL5xParser.function_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.function_key_return function_key26 = null;

        DRL5xParser.data_type_return data_type27 = null;

        DRL5xParser.function_id_return function_id28 = null;

        DRL5xParser.parameters_return parameters29 = null;

        DRL5xParser.curly_chunk_return curly_chunk30 = null;


        RewriteRuleSubtreeStream stream_function_key=new RewriteRuleSubtreeStream(adaptor,"rule function_key");
        RewriteRuleSubtreeStream stream_function_id=new RewriteRuleSubtreeStream(adaptor,"rule function_id");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        RewriteRuleSubtreeStream stream_parameters=new RewriteRuleSubtreeStream(adaptor,"rule parameters");
        RewriteRuleSubtreeStream stream_curly_chunk=new RewriteRuleSubtreeStream(adaptor,"rule curly_chunk");
         pushParaphrases(DroolsParaphraseTypes.FUNCTION); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.FUNCTION);  
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:510:2: ( function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) )
            // src/main/resources/org/drools/lang/DRL5x.g:510:4: function_key ( data_type )? function_id parameters curly_chunk
            {
            pushFollow(FOLLOW_function_key_in_function815);
            function_key26=function_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_function_key.add(function_key26.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:510:17: ( data_type )?
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
                    // src/main/resources/org/drools/lang/DRL5x.g:510:17: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function817);
                    data_type27=data_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_data_type.add(data_type27.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_function_id_in_function820);
            function_id28=function_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_function_id.add(function_id28.getTree());
            pushFollow(FOLLOW_parameters_in_function822);
            parameters29=parameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_parameters.add(parameters29.getTree());
            pushFollow(FOLLOW_curly_chunk_in_function824);
            curly_chunk30=curly_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_curly_chunk.add(curly_chunk30.getTree());


            // AST REWRITE
            // elements: function_id, curly_chunk, function_key, data_type, parameters
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 511:3: -> ^( function_key ( data_type )? function_id parameters curly_chunk )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:511:6: ^( function_key ( data_type )? function_id parameters curly_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_function_key.nextNode(), root_1);

                // src/main/resources/org/drools/lang/DRL5x.g:511:21: ( data_type )?
                if ( stream_data_type.hasNext() ) {
                    adaptor.addChild(root_1, stream_data_type.nextTree());

                }
                stream_data_type.reset();
                adaptor.addChild(root_1, stream_function_id.nextTree());
                adaptor.addChild(root_1, stream_parameters.nextTree());
                adaptor.addChild(root_1, stream_curly_chunk.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "function"

    public static class function_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "function_id"
    // src/main/resources/org/drools/lang/DRL5x.g:514:1: function_id : id= ID -> VT_FUNCTION_ID[$id] ;
    public final DRL5xParser.function_id_return function_id() throws RecognitionException {
        DRL5xParser.function_id_return retval = new DRL5xParser.function_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:515:2: (id= ID -> VT_FUNCTION_ID[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:515:4: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_function_id854); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.FUNCTION, (id!=null?id.getText():null));	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 518:3: -> VT_FUNCTION_ID[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_FUNCTION_ID, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "function_id"

    public static class query_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "query"
    // src/main/resources/org/drools/lang/DRL5x.g:521:1: query : query_key query_id ( parameters )? normal_lhs_block end= end_key ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block end_key ) ;
    public final DRL5xParser.query_return query() throws RecognitionException {
        DRL5xParser.query_return retval = new DRL5xParser.query_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON35=null;
        DRL5xParser.end_key_return end = null;

        DRL5xParser.query_key_return query_key31 = null;

        DRL5xParser.query_id_return query_id32 = null;

        DRL5xParser.parameters_return parameters33 = null;

        DRL5xParser.normal_lhs_block_return normal_lhs_block34 = null;


        Object SEMICOLON35_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_query_key=new RewriteRuleSubtreeStream(adaptor,"rule query_key");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        RewriteRuleSubtreeStream stream_end_key=new RewriteRuleSubtreeStream(adaptor,"rule end_key");
        RewriteRuleSubtreeStream stream_parameters=new RewriteRuleSubtreeStream(adaptor,"rule parameters");
        RewriteRuleSubtreeStream stream_query_id=new RewriteRuleSubtreeStream(adaptor,"rule query_id");
         pushParaphrases(DroolsParaphraseTypes.QUERY); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.QUERY); 
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:524:2: ( query_key query_id ( parameters )? normal_lhs_block end= end_key ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block end_key ) )
            // src/main/resources/org/drools/lang/DRL5x.g:524:4: query_key query_id ( parameters )? normal_lhs_block end= end_key ( SEMICOLON )?
            {
            pushFollow(FOLLOW_query_key_in_query886);
            query_key31=query_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_query_key.add(query_key31.getTree());
            pushFollow(FOLLOW_query_id_in_query888);
            query_id32=query_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_query_id.add(query_id32.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:526:3: ( parameters )?
            int alt12=2;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:526:3: parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query896);
                    parameters33=parameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_parameters.add(parameters33.getTree());

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }
            pushFollow(FOLLOW_normal_lhs_block_in_query905);
            normal_lhs_block34=normal_lhs_block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block34.getTree());
            pushFollow(FOLLOW_end_key_in_query912);
            end=end_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_end_key.add(end.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:529:15: ( SEMICOLON )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==SEMICOLON) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:529:15: SEMICOLON
                    {
                    SEMICOLON35=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_query914); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON35);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON35, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: query_id, normal_lhs_block, end_key, query_key, parameters
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 531:3: -> ^( query_key query_id ( parameters )? normal_lhs_block end_key )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:531:6: ^( query_key query_id ( parameters )? normal_lhs_block end_key )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_query_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_query_id.nextTree());
                // src/main/resources/org/drools/lang/DRL5x.g:531:27: ( parameters )?
                if ( stream_parameters.hasNext() ) {
                    adaptor.addChild(root_1, stream_parameters.nextTree());

                }
                stream_parameters.reset();
                adaptor.addChild(root_1, stream_normal_lhs_block.nextTree());
                adaptor.addChild(root_1, stream_end_key.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "query"

    public static class query_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "query_id"
    // src/main/resources/org/drools/lang/DRL5x.g:534:1: query_id : (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] );
    public final DRL5xParser.query_id_return query_id() throws RecognitionException {
        DRL5xParser.query_id_return retval = new DRL5xParser.query_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:535:2: (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==ID) ) {
                alt14=1;
            }
            else if ( (LA14_0==STRING) ) {
                alt14=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:535:5: id= ID
                    {
                    id=(Token)match(input,ID,FOLLOW_ID_in_query_id949); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(id);

                    if ( state.backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.QUERY, (id!=null?id.getText():null));	
                    }


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 537:65: -> VT_QUERY_ID[$id]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_QUERY_ID, id));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:538:5: id= STRING
                    {
                    id=(Token)match(input,STRING,FOLLOW_STRING_in_query_id965); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING.add(id);

                    if ( state.backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.QUERY, (id!=null?id.getText():null));	
                    }


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 540:65: -> VT_QUERY_ID[$id]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_QUERY_ID, id));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "query_id"

    public static class parameters_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "parameters"
    // src/main/resources/org/drools/lang/DRL5x.g:543:1: parameters : LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) ;
    public final DRL5xParser.parameters_return parameters() throws RecognitionException {
        DRL5xParser.parameters_return retval = new DRL5xParser.parameters_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN36=null;
        Token COMMA38=null;
        Token RIGHT_PAREN40=null;
        DRL5xParser.param_definition_return param_definition37 = null;

        DRL5xParser.param_definition_return param_definition39 = null;


        Object LEFT_PAREN36_tree=null;
        Object COMMA38_tree=null;
        Object RIGHT_PAREN40_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_param_definition=new RewriteRuleSubtreeStream(adaptor,"rule param_definition");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:544:2: ( LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) )
            // src/main/resources/org/drools/lang/DRL5x.g:544:4: LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN
            {
            LEFT_PAREN36=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parameters984); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN36);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN36, DroolsEditorType.SYMBOL);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:545:4: ( param_definition ( COMMA param_definition )* )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==ID) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:545:6: param_definition ( COMMA param_definition )*
                    {
                    pushFollow(FOLLOW_param_definition_in_parameters993);
                    param_definition37=param_definition();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_param_definition.add(param_definition37.getTree());
                    // src/main/resources/org/drools/lang/DRL5x.g:545:23: ( COMMA param_definition )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==COMMA) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRL5x.g:545:24: COMMA param_definition
                    	    {
                    	    COMMA38=(Token)match(input,COMMA,FOLLOW_COMMA_in_parameters996); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA38);

                    	    if ( state.backtracking==0 ) {
                    	      	emit(COMMA38, DroolsEditorType.SYMBOL);	
                    	    }
                    	    pushFollow(FOLLOW_param_definition_in_parameters1000);
                    	    param_definition39=param_definition();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_param_definition.add(param_definition39.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    }
                    break;

            }

            RIGHT_PAREN40=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parameters1009); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN40);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN40, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: RIGHT_PAREN, param_definition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 547:3: -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:547:6: ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_PARAM_LIST, "VT_PARAM_LIST"), root_1);

                // src/main/resources/org/drools/lang/DRL5x.g:547:22: ( param_definition )*
                while ( stream_param_definition.hasNext() ) {
                    adaptor.addChild(root_1, stream_param_definition.nextTree());

                }
                stream_param_definition.reset();
                adaptor.addChild(root_1, stream_RIGHT_PAREN.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "parameters"

    public static class param_definition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "param_definition"
    // src/main/resources/org/drools/lang/DRL5x.g:550:1: param_definition : ( data_type )? argument ;
    public final DRL5xParser.param_definition_return param_definition() throws RecognitionException {
        DRL5xParser.param_definition_return retval = new DRL5xParser.param_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.data_type_return data_type41 = null;

        DRL5xParser.argument_return argument42 = null;



        try {
            // src/main/resources/org/drools/lang/DRL5x.g:551:2: ( ( data_type )? argument )
            // src/main/resources/org/drools/lang/DRL5x.g:551:4: ( data_type )? argument
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL5x.g:551:4: ( data_type )?
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:551:4: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition1035);
                    data_type41=data_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, data_type41.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition1038);
            argument42=argument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, argument42.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "param_definition"

    public static class argument_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "argument"
    // src/main/resources/org/drools/lang/DRL5x.g:554:1: argument : ID ( dimension_definition )* ;
    public final DRL5xParser.argument_return argument() throws RecognitionException {
        DRL5xParser.argument_return retval = new DRL5xParser.argument_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID43=null;
        DRL5xParser.dimension_definition_return dimension_definition44 = null;


        Object ID43_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:555:2: ( ID ( dimension_definition )* )
            // src/main/resources/org/drools/lang/DRL5x.g:555:4: ID ( dimension_definition )*
            {
            root_0 = (Object)adaptor.nil();

            ID43=(Token)match(input,ID,FOLLOW_ID_in_argument1049); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID43_tree = (Object)adaptor.create(ID43);
            adaptor.addChild(root_0, ID43_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(ID43, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:556:3: ( dimension_definition )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==LEFT_SQUARE) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:556:3: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_argument1055);
            	    dimension_definition44=dimension_definition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, dimension_definition44.getTree());

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "argument"

    public static class type_declaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type_declaration"
    // src/main/resources/org/drools/lang/DRL5x.g:559:1: type_declaration : declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key ) ;
    public final DRL5xParser.type_declaration_return type_declaration() throws RecognitionException {
        DRL5xParser.type_declaration_return retval = new DRL5xParser.type_declaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.declare_key_return declare_key45 = null;

        DRL5xParser.type_declare_id_return type_declare_id46 = null;

        DRL5xParser.decl_metadata_return decl_metadata47 = null;

        DRL5xParser.decl_field_return decl_field48 = null;

        DRL5xParser.end_key_return end_key49 = null;


        RewriteRuleSubtreeStream stream_decl_field=new RewriteRuleSubtreeStream(adaptor,"rule decl_field");
        RewriteRuleSubtreeStream stream_type_declare_id=new RewriteRuleSubtreeStream(adaptor,"rule type_declare_id");
        RewriteRuleSubtreeStream stream_end_key=new RewriteRuleSubtreeStream(adaptor,"rule end_key");
        RewriteRuleSubtreeStream stream_decl_metadata=new RewriteRuleSubtreeStream(adaptor,"rule decl_metadata");
        RewriteRuleSubtreeStream stream_declare_key=new RewriteRuleSubtreeStream(adaptor,"rule declare_key");
         pushParaphrases(DroolsParaphraseTypes.TYPE_DECLARE); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.TYPE_DECLARATION); 
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:562:2: ( declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key ) )
            // src/main/resources/org/drools/lang/DRL5x.g:562:4: declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key
            {
            pushFollow(FOLLOW_declare_key_in_type_declaration1078);
            declare_key45=declare_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_declare_key.add(declare_key45.getTree());
            pushFollow(FOLLOW_type_declare_id_in_type_declaration1081);
            type_declare_id46=type_declare_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_type_declare_id.add(type_declare_id46.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:563:3: ( decl_metadata )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AT) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:563:3: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_type_declaration1085);
            	    decl_metadata47=decl_metadata();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_decl_metadata.add(decl_metadata47.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DRL5x.g:564:3: ( decl_field )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==ID) ) {
                    int LA20_1 = input.LA(2);

                    if ( ((LA20_1>=COLON && LA20_1<=EQUALS)) ) {
                        alt20=1;
                    }


                }


                switch (alt20) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:564:3: decl_field
            	    {
            	    pushFollow(FOLLOW_decl_field_in_type_declaration1090);
            	    decl_field48=decl_field();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_decl_field.add(decl_field48.getTree());

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            pushFollow(FOLLOW_end_key_in_type_declaration1095);
            end_key49=end_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_end_key.add(end_key49.getTree());


            // AST REWRITE
            // elements: end_key, declare_key, type_declare_id, decl_metadata, decl_field
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 566:3: -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:566:6: ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_declare_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_type_declare_id.nextTree());
                // src/main/resources/org/drools/lang/DRL5x.g:566:36: ( decl_metadata )*
                while ( stream_decl_metadata.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_metadata.nextTree());

                }
                stream_decl_metadata.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:566:51: ( decl_field )*
                while ( stream_decl_field.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_field.nextTree());

                }
                stream_decl_field.reset();
                adaptor.addChild(root_1, stream_end_key.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "type_declaration"

    public static class type_declare_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type_declare_id"
    // src/main/resources/org/drools/lang/DRL5x.g:569:1: type_declare_id : id= ID -> VT_TYPE_DECLARE_ID[$id] ;
    public final DRL5xParser.type_declare_id_return type_declare_id() throws RecognitionException {
        DRL5xParser.type_declare_id_return retval = new DRL5xParser.type_declare_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:570:2: (id= ID -> VT_TYPE_DECLARE_ID[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:570:5: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_type_declare_id1127); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.TYPE_DECLARE, (id!=null?id.getText():null));	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 572:72: -> VT_TYPE_DECLARE_ID[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_TYPE_DECLARE_ID, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "type_declare_id"

    public static class decl_metadata_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "decl_metadata"
    // src/main/resources/org/drools/lang/DRL5x.g:575:1: decl_metadata : AT ID ( paren_chunk )? -> ^( AT ID ( paren_chunk )? ) ;
    public final DRL5xParser.decl_metadata_return decl_metadata() throws RecognitionException {
        DRL5xParser.decl_metadata_return retval = new DRL5xParser.decl_metadata_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AT50=null;
        Token ID51=null;
        DRL5xParser.paren_chunk_return paren_chunk52 = null;


        Object AT50_tree=null;
        Object ID51_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:576:2: ( AT ID ( paren_chunk )? -> ^( AT ID ( paren_chunk )? ) )
            // src/main/resources/org/drools/lang/DRL5x.g:576:4: AT ID ( paren_chunk )?
            {
            AT50=(Token)match(input,AT,FOLLOW_AT_in_decl_metadata1146); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AT.add(AT50);

            if ( state.backtracking==0 ) {
              	emit(AT50, DroolsEditorType.SYMBOL);	
            }
            ID51=(Token)match(input,ID,FOLLOW_ID_in_decl_metadata1154); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID51);

            if ( state.backtracking==0 ) {
              	emit(ID51, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:580:3: ( paren_chunk )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==LEFT_PAREN) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:580:3: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_decl_metadata1161);
                    paren_chunk52=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk52.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: ID, AT, paren_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 581:3: -> ^( AT ID ( paren_chunk )? )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:581:6: ^( AT ID ( paren_chunk )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_AT.nextNode(), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // src/main/resources/org/drools/lang/DRL5x.g:581:14: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                }
                stream_paren_chunk.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "decl_metadata"

    public static class decl_field_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "decl_field"
    // src/main/resources/org/drools/lang/DRL5x.g:584:1: decl_field : ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) ;
    public final DRL5xParser.decl_field_return decl_field() throws RecognitionException {
        DRL5xParser.decl_field_return retval = new DRL5xParser.decl_field_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID53=null;
        Token COLON55=null;
        DRL5xParser.decl_field_initialization_return decl_field_initialization54 = null;

        DRL5xParser.data_type_return data_type56 = null;

        DRL5xParser.decl_metadata_return decl_metadata57 = null;


        Object ID53_tree=null;
        Object COLON55_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_decl_field_initialization=new RewriteRuleSubtreeStream(adaptor,"rule decl_field_initialization");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        RewriteRuleSubtreeStream stream_decl_metadata=new RewriteRuleSubtreeStream(adaptor,"rule decl_metadata");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:585:2: ( ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) )
            // src/main/resources/org/drools/lang/DRL5x.g:585:4: ID ( decl_field_initialization )? COLON data_type ( decl_metadata )*
            {
            ID53=(Token)match(input,ID,FOLLOW_ID_in_decl_field1186); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID53);

            if ( state.backtracking==0 ) {
              	emit(ID53, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:586:3: ( decl_field_initialization )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==EQUALS) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:586:3: decl_field_initialization
                    {
                    pushFollow(FOLLOW_decl_field_initialization_in_decl_field1192);
                    decl_field_initialization54=decl_field_initialization();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_decl_field_initialization.add(decl_field_initialization54.getTree());

                    }
                    break;

            }

            COLON55=(Token)match(input,COLON,FOLLOW_COLON_in_decl_field1198); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON55);

            if ( state.backtracking==0 ) {
              	emit(COLON55, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_data_type_in_decl_field1204);
            data_type56=data_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_data_type.add(data_type56.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:589:3: ( decl_metadata )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==AT) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:589:3: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_decl_field1208);
            	    decl_metadata57=decl_metadata();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_decl_metadata.add(decl_metadata57.getTree());

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);



            // AST REWRITE
            // elements: decl_metadata, data_type, decl_field_initialization, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 590:3: -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:590:6: ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ID.nextNode(), root_1);

                // src/main/resources/org/drools/lang/DRL5x.g:590:11: ( decl_field_initialization )?
                if ( stream_decl_field_initialization.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_field_initialization.nextTree());

                }
                stream_decl_field_initialization.reset();
                adaptor.addChild(root_1, stream_data_type.nextTree());
                // src/main/resources/org/drools/lang/DRL5x.g:590:48: ( decl_metadata )*
                while ( stream_decl_metadata.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_metadata.nextTree());

                }
                stream_decl_metadata.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "decl_field"

    public static class decl_field_initialization_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "decl_field_initialization"
    // src/main/resources/org/drools/lang/DRL5x.g:593:1: decl_field_initialization : EQUALS paren_chunk -> ^( EQUALS paren_chunk ) ;
    public final DRL5xParser.decl_field_initialization_return decl_field_initialization() throws RecognitionException {
        DRL5xParser.decl_field_initialization_return retval = new DRL5xParser.decl_field_initialization_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS58=null;
        DRL5xParser.paren_chunk_return paren_chunk59 = null;


        Object EQUALS58_tree=null;
        RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:594:2: ( EQUALS paren_chunk -> ^( EQUALS paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL5x.g:594:4: EQUALS paren_chunk
            {
            EQUALS58=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_decl_field_initialization1236); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS58);

            if ( state.backtracking==0 ) {
              	emit(EQUALS58, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_paren_chunk_in_decl_field_initialization1242);
            paren_chunk59=paren_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk59.getTree());


            // AST REWRITE
            // elements: paren_chunk, EQUALS
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 596:2: -> ^( EQUALS paren_chunk )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:596:5: ^( EQUALS paren_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_EQUALS.nextNode(), root_1);

                adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "decl_field_initialization"

    public static class template_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "template"
    // src/main/resources/org/drools/lang/DRL5x.g:599:1: template : template_key template_id (semi1= SEMICOLON )? ( template_slot )+ end= end_key (semi2= SEMICOLON )? -> ^( template_key template_id ( template_slot )+ end_key ) ;
    public final DRL5xParser.template_return template() throws RecognitionException {
        DRL5xParser.template_return retval = new DRL5xParser.template_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token semi1=null;
        Token semi2=null;
        DRL5xParser.end_key_return end = null;

        DRL5xParser.template_key_return template_key60 = null;

        DRL5xParser.template_id_return template_id61 = null;

        DRL5xParser.template_slot_return template_slot62 = null;


        Object semi1_tree=null;
        Object semi2_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_template_slot=new RewriteRuleSubtreeStream(adaptor,"rule template_slot");
        RewriteRuleSubtreeStream stream_template_id=new RewriteRuleSubtreeStream(adaptor,"rule template_id");
        RewriteRuleSubtreeStream stream_end_key=new RewriteRuleSubtreeStream(adaptor,"rule end_key");
        RewriteRuleSubtreeStream stream_template_key=new RewriteRuleSubtreeStream(adaptor,"rule template_key");
         pushParaphrases(DroolsParaphraseTypes.TEMPLATE); 
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:602:2: ( template_key template_id (semi1= SEMICOLON )? ( template_slot )+ end= end_key (semi2= SEMICOLON )? -> ^( template_key template_id ( template_slot )+ end_key ) )
            // src/main/resources/org/drools/lang/DRL5x.g:603:2: template_key template_id (semi1= SEMICOLON )? ( template_slot )+ end= end_key (semi2= SEMICOLON )?
            {
            if ( state.backtracking==0 ) {
              	beginSentence(DroolsSentenceType.TEMPLATE);	
            }
            pushFollow(FOLLOW_template_key_in_template1279);
            template_key60=template_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_template_key.add(template_key60.getTree());
            pushFollow(FOLLOW_template_id_in_template1281);
            template_id61=template_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_template_id.add(template_id61.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:605:8: (semi1= SEMICOLON )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==SEMICOLON) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:605:8: semi1= SEMICOLON
                    {
                    semi1=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1288); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(semi1);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(semi1, DroolsEditorType.SYMBOL);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:607:3: ( template_slot )+
            int cnt25=0;
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==ID) ) {
                    int LA25_1 = input.LA(2);

                    if ( (LA25_1==ID) ) {
                        int LA25_3 = input.LA(3);

                        if ( (LA25_3==ID) ) {
                            int LA25_5 = input.LA(4);

                            if ( (!((((validateIdentifierKey(DroolsSoftKeywords.END)))))) ) {
                                alt25=1;
                            }


                        }
                        else if ( (LA25_3==SEMICOLON) ) {
                            alt25=1;
                        }


                    }
                    else if ( (LA25_1==DOT||LA25_1==LEFT_SQUARE) ) {
                        alt25=1;
                    }


                }


                switch (alt25) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:607:3: template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template1296);
            	    template_slot62=template_slot();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_template_slot.add(template_slot62.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt25 >= 1 ) break loop25;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(25, input);
                        throw eee;
                }
                cnt25++;
            } while (true);

            pushFollow(FOLLOW_end_key_in_template1303);
            end=end_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_end_key.add(end.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:608:20: (semi2= SEMICOLON )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==SEMICOLON) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:608:20: semi2= SEMICOLON
                    {
                    semi2=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1307); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(semi2);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(semi2, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: end_key, template_id, template_key, template_slot
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 610:3: -> ^( template_key template_id ( template_slot )+ end_key )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:610:6: ^( template_key template_id ( template_slot )+ end_key )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_template_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_template_id.nextTree());
                if ( !(stream_template_slot.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_template_slot.hasNext() ) {
                    adaptor.addChild(root_1, stream_template_slot.nextTree());

                }
                stream_template_slot.reset();
                adaptor.addChild(root_1, stream_end_key.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "template"

    public static class template_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "template_id"
    // src/main/resources/org/drools/lang/DRL5x.g:613:1: template_id : (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] );
    public final DRL5xParser.template_id_return template_id() throws RecognitionException {
        DRL5xParser.template_id_return retval = new DRL5xParser.template_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:614:2: (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==ID) ) {
                alt27=1;
            }
            else if ( (LA27_0==STRING) ) {
                alt27=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:614:5: id= ID
                    {
                    id=(Token)match(input,ID,FOLLOW_ID_in_template_id1340); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(id);

                    if ( state.backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.TEMPLATE, (id!=null?id.getText():null));	
                    }


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 616:68: -> VT_TEMPLATE_ID[$id]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_TEMPLATE_ID, id));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:617:5: id= STRING
                    {
                    id=(Token)match(input,STRING,FOLLOW_STRING_in_template_id1356); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING.add(id);

                    if ( state.backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.TEMPLATE, (id!=null?id.getText():null));	
                    }


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 619:68: -> VT_TEMPLATE_ID[$id]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_TEMPLATE_ID, id));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "template_id"

    public static class template_slot_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "template_slot"
    // src/main/resources/org/drools/lang/DRL5x.g:622:1: template_slot : data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) ;
    public final DRL5xParser.template_slot_return template_slot() throws RecognitionException {
        DRL5xParser.template_slot_return retval = new DRL5xParser.template_slot_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON65=null;
        DRL5xParser.data_type_return data_type63 = null;

        DRL5xParser.slot_id_return slot_id64 = null;


        Object SEMICOLON65_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_slot_id=new RewriteRuleSubtreeStream(adaptor,"rule slot_id");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:623:2: ( data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) )
            // src/main/resources/org/drools/lang/DRL5x.g:623:5: data_type slot_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_data_type_in_template_slot1376);
            data_type63=data_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_data_type.add(data_type63.getTree());
            pushFollow(FOLLOW_slot_id_in_template_slot1378);
            slot_id64=slot_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_slot_id.add(slot_id64.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:623:23: ( SEMICOLON )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==SEMICOLON) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:623:23: SEMICOLON
                    {
                    SEMICOLON65=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template_slot1380); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON65);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON65, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: data_type, slot_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 625:3: -> ^( VT_SLOT data_type slot_id )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:625:6: ^( VT_SLOT data_type slot_id )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_SLOT, "VT_SLOT"), root_1);

                adaptor.addChild(root_1, stream_data_type.nextTree());
                adaptor.addChild(root_1, stream_slot_id.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "template_slot"

    public static class slot_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "slot_id"
    // src/main/resources/org/drools/lang/DRL5x.g:628:1: slot_id : id= ID -> VT_SLOT_ID[$id] ;
    public final DRL5xParser.slot_id_return slot_id() throws RecognitionException {
        DRL5xParser.slot_id_return retval = new DRL5xParser.slot_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:629:2: (id= ID -> VT_SLOT_ID[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:629:4: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_slot_id1409); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.IDENTIFIER);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 631:3: -> VT_SLOT_ID[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SLOT_ID, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "slot_id"

    public static class rule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule"
    // src/main/resources/org/drools/lang/DRL5x.g:634:1: rule : rule_key rule_id ( extend_key rule_id )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk ) ;
    public final DRL5xParser.rule_return rule() throws RecognitionException {
        DRL5xParser.rule_return retval = new DRL5xParser.rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.rule_key_return rule_key66 = null;

        DRL5xParser.rule_id_return rule_id67 = null;

        DRL5xParser.extend_key_return extend_key68 = null;

        DRL5xParser.rule_id_return rule_id69 = null;

        DRL5xParser.decl_metadata_return decl_metadata70 = null;

        DRL5xParser.rule_attributes_return rule_attributes71 = null;

        DRL5xParser.when_part_return when_part72 = null;

        DRL5xParser.rhs_chunk_return rhs_chunk73 = null;


        RewriteRuleSubtreeStream stream_rule_id=new RewriteRuleSubtreeStream(adaptor,"rule rule_id");
        RewriteRuleSubtreeStream stream_rhs_chunk=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk");
        RewriteRuleSubtreeStream stream_rule_attributes=new RewriteRuleSubtreeStream(adaptor,"rule rule_attributes");
        RewriteRuleSubtreeStream stream_rule_key=new RewriteRuleSubtreeStream(adaptor,"rule rule_key");
        RewriteRuleSubtreeStream stream_extend_key=new RewriteRuleSubtreeStream(adaptor,"rule extend_key");
        RewriteRuleSubtreeStream stream_decl_metadata=new RewriteRuleSubtreeStream(adaptor,"rule decl_metadata");
        RewriteRuleSubtreeStream stream_when_part=new RewriteRuleSubtreeStream(adaptor,"rule when_part");
         boolean isFailed = true; pushParaphrases(DroolsParaphraseTypes.RULE); 
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:637:2: ( rule_key rule_id ( extend_key rule_id )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk ) )
            // src/main/resources/org/drools/lang/DRL5x.g:638:2: rule_key rule_id ( extend_key rule_id )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk
            {
            if ( state.backtracking==0 ) {
              	beginSentence(DroolsSentenceType.RULE);	
            }
            pushFollow(FOLLOW_rule_key_in_rule1446);
            rule_key66=rule_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_key.add(rule_key66.getTree());
            pushFollow(FOLLOW_rule_id_in_rule1448);
            rule_id67=rule_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_id.add(rule_id67.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:641:3: ( extend_key rule_id )?
            int alt29=2;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:641:4: extend_key rule_id
                    {
                    pushFollow(FOLLOW_extend_key_in_rule1457);
                    extend_key68=extend_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_extend_key.add(extend_key68.getTree());
                    pushFollow(FOLLOW_rule_id_in_rule1459);
                    rule_id69=rule_id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rule_id.add(rule_id69.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL5x.g:641:25: ( decl_metadata )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==AT) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:641:25: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_rule1463);
            	    decl_metadata70=decl_metadata();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_decl_metadata.add(decl_metadata70.getTree());

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DRL5x.g:641:40: ( rule_attributes )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:641:40: rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1466);
                    rule_attributes71=rule_attributes();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rule_attributes.add(rule_attributes71.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL5x.g:641:57: ( when_part )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==WHEN) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:641:57: when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule1469);
                    when_part72=when_part();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_when_part.add(when_part72.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1472);
            rhs_chunk73=rhs_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rhs_chunk.add(rhs_chunk73.getTree());


            // AST REWRITE
            // elements: when_part, rhs_chunk, rule_attributes, rule_id, rule_key, extend_key, rule_id, decl_metadata
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 642:3: -> ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:642:6: ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_rule_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rule_id.nextTree());
                // src/main/resources/org/drools/lang/DRL5x.g:642:25: ( ^( extend_key rule_id ) )?
                if ( stream_rule_id.hasNext()||stream_extend_key.hasNext() ) {
                    // src/main/resources/org/drools/lang/DRL5x.g:642:25: ^( extend_key rule_id )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_extend_key.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_rule_id.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_rule_id.reset();
                stream_extend_key.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:642:48: ( decl_metadata )*
                while ( stream_decl_metadata.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_metadata.nextTree());

                }
                stream_decl_metadata.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:642:63: ( rule_attributes )?
                if ( stream_rule_attributes.hasNext() ) {
                    adaptor.addChild(root_1, stream_rule_attributes.nextTree());

                }
                stream_rule_attributes.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:642:80: ( when_part )?
                if ( stream_when_part.hasNext() ) {
                    adaptor.addChild(root_1, stream_when_part.nextTree());

                }
                stream_when_part.reset();
                adaptor.addChild(root_1, stream_rhs_chunk.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); isFailed = false; 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {

            	if (isEditorInterfaceEnabled && isFailed) {
            		if (input.LA(6) == EOF && input.LA(1) == ID && input.LA(2) == MISC && input.LA(3) == ID && 
            			input.LA(5) == MISC && input.LA(6) == ID && 
            			validateLT(1, DroolsSoftKeywords.LOCK) && validateLT(3, DroolsSoftKeywords.ON) &&
            			validateLT(5, DroolsSoftKeywords.ACTIVE)){
            			emit(input.LT(1), DroolsEditorType.KEYWORD);
            			emit(input.LT(2), DroolsEditorType.KEYWORD);
            			emit(input.LT(3), DroolsEditorType.KEYWORD);
            			emit(input.LT(4), DroolsEditorType.KEYWORD);
            			emit(input.LT(5), DroolsEditorType.KEYWORD);
            			emit(Location.LOCATION_RULE_HEADER_KEYWORD);
            			input.consume();
            			input.consume();
            			input.consume();
            			input.consume();
            			input.consume();
            		} else if (input.LA(4) == EOF && input.LA(1) == ID && input.LA(2) == MISC && input.LA(3) == ID && 
            			(	(validateLT(1, DroolsSoftKeywords.ACTIVATION) && validateLT(3, DroolsSoftKeywords.GROUP)) ||
            				(validateLT(1, DroolsSoftKeywords.DATE) && validateLT(3, DroolsSoftKeywords.EXPIRES)) ||
            				(validateLT(1, DroolsSoftKeywords.NO) && validateLT(3, DroolsSoftKeywords.LOOP)) ||
            				(validateLT(1, DroolsSoftKeywords.DATE) && validateLT(3, DroolsSoftKeywords.EFFECTIVE)) ||
            				(validateLT(1, DroolsSoftKeywords.AUTO) && validateLT(3, DroolsSoftKeywords.FOCUS)) ||
            				(validateLT(1, DroolsSoftKeywords.ACTIVATION) && validateLT(3, DroolsSoftKeywords.GROUP)) ||
            				(validateLT(1, DroolsSoftKeywords.RULEFLOW) && validateLT(3, DroolsSoftKeywords.GROUP)) ||
            				(validateLT(1, DroolsSoftKeywords.AGENDA) && validateLT(3, DroolsSoftKeywords.GROUP))	)){
            			emit(input.LT(1), DroolsEditorType.KEYWORD);
            			emit(input.LT(2), DroolsEditorType.KEYWORD);
            			emit(input.LT(3), DroolsEditorType.KEYWORD);
            			emit(Location.LOCATION_RULE_HEADER_KEYWORD);
            			input.consume();
            			input.consume();
            			input.consume();
            		} else if (input.LA(2) == EOF && input.LA(1) == ID && 
            				(validateLT(1, DroolsSoftKeywords.DIALECT) || validateLT(1, DroolsSoftKeywords.ENABLED) ||
            				 validateLT(1, DroolsSoftKeywords.SALIENCE) || validateLT(1, DroolsSoftKeywords.DURATION) ||
            				 validateLT(1, DroolsSoftKeywords.TIMER))){
            			emit(input.LT(1), DroolsEditorType.KEYWORD);
            			emit(Location.LOCATION_RULE_HEADER_KEYWORD);
            			input.consume();
            		}
            	}

        }
        return retval;
    }
    // $ANTLR end "rule"

    public static class when_part_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "when_part"
    // src/main/resources/org/drools/lang/DRL5x.g:688:1: when_part : WHEN ( COLON )? normal_lhs_block -> WHEN normal_lhs_block ;
    public final DRL5xParser.when_part_return when_part() throws RecognitionException {
        DRL5xParser.when_part_return retval = new DRL5xParser.when_part_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token WHEN74=null;
        Token COLON75=null;
        DRL5xParser.normal_lhs_block_return normal_lhs_block76 = null;


        Object WHEN74_tree=null;
        Object COLON75_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_WHEN=new RewriteRuleTokenStream(adaptor,"token WHEN");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:689:2: ( WHEN ( COLON )? normal_lhs_block -> WHEN normal_lhs_block )
            // src/main/resources/org/drools/lang/DRL5x.g:689:5: WHEN ( COLON )? normal_lhs_block
            {
            WHEN74=(Token)match(input,WHEN,FOLLOW_WHEN_in_when_part1516); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_WHEN.add(WHEN74);

            if ( state.backtracking==0 ) {
              	emit(WHEN74, DroolsEditorType.KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:690:3: ( COLON )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==COLON) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:690:3: COLON
                    {
                    COLON75=(Token)match(input,COLON,FOLLOW_COLON_in_when_part1522); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON75);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(COLON75, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }
            pushFollow(FOLLOW_normal_lhs_block_in_when_part1532);
            normal_lhs_block76=normal_lhs_block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block76.getTree());


            // AST REWRITE
            // elements: normal_lhs_block, WHEN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 693:2: -> WHEN normal_lhs_block
            {
                adaptor.addChild(root_0, stream_WHEN.nextNode());
                adaptor.addChild(root_0, stream_normal_lhs_block.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "when_part"

    public static class rule_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule_id"
    // src/main/resources/org/drools/lang/DRL5x.g:696:1: rule_id : (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] );
    public final DRL5xParser.rule_id_return rule_id() throws RecognitionException {
        DRL5xParser.rule_id_return retval = new DRL5xParser.rule_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:697:2: (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==ID) ) {
                alt34=1;
            }
            else if ( (LA34_0==STRING) ) {
                alt34=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:697:5: id= ID
                    {
                    id=(Token)match(input,ID,FOLLOW_ID_in_rule_id1553); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(id);

                    if ( state.backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.RULE, (id!=null?id.getText():null));	
                    }


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 699:64: -> VT_RULE_ID[$id]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_RULE_ID, id));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:700:5: id= STRING
                    {
                    id=(Token)match(input,STRING,FOLLOW_STRING_in_rule_id1569); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING.add(id);

                    if ( state.backtracking==0 ) {
                      	emit(id, DroolsEditorType.IDENTIFIER);
                      		setParaphrasesValue(DroolsParaphraseTypes.RULE, (id!=null?id.getText():null));	
                    }


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 702:64: -> VT_RULE_ID[$id]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_RULE_ID, id));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rule_id"

    public static class rule_attributes_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule_attributes"
    // src/main/resources/org/drools/lang/DRL5x.g:705:1: rule_attributes : ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) ;
    public final DRL5xParser.rule_attributes_return rule_attributes() throws RecognitionException {
        DRL5xParser.rule_attributes_return retval = new DRL5xParser.rule_attributes_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON78=null;
        Token COMMA80=null;
        DRL5xParser.rule_attribute_return attr = null;

        DRL5xParser.attributes_key_return attributes_key77 = null;

        DRL5xParser.rule_attribute_return rule_attribute79 = null;


        Object COLON78_tree=null;
        Object COMMA80_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_rule_attribute=new RewriteRuleSubtreeStream(adaptor,"rule rule_attribute");
        RewriteRuleSubtreeStream stream_attributes_key=new RewriteRuleSubtreeStream(adaptor,"rule attributes_key");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:706:2: ( ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) )
            // src/main/resources/org/drools/lang/DRL5x.g:706:4: ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )*
            {
            // src/main/resources/org/drools/lang/DRL5x.g:706:4: ( attributes_key COLON )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {
                int LA35_1 = input.LA(2);

                if ( (LA35_1==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {
                    alt35=1;
                }
            }
            switch (alt35) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:706:6: attributes_key COLON
                    {
                    pushFollow(FOLLOW_attributes_key_in_rule_attributes1590);
                    attributes_key77=attributes_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_attributes_key.add(attributes_key77.getTree());
                    COLON78=(Token)match(input,COLON,FOLLOW_COLON_in_rule_attributes1592); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON78);

                    if ( state.backtracking==0 ) {
                      	emit(COLON78, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1602);
            rule_attribute79=rule_attribute();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_attribute.add(rule_attribute79.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:707:18: ( ( COMMA )? attr= rule_attribute )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( (LA37_0==ID||LA37_0==COMMA) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:707:20: ( COMMA )? attr= rule_attribute
            	    {
            	    // src/main/resources/org/drools/lang/DRL5x.g:707:20: ( COMMA )?
            	    int alt36=2;
            	    int LA36_0 = input.LA(1);

            	    if ( (LA36_0==COMMA) ) {
            	        alt36=1;
            	    }
            	    switch (alt36) {
            	        case 1 :
            	            // src/main/resources/org/drools/lang/DRL5x.g:707:20: COMMA
            	            {
            	            COMMA80=(Token)match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1606); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_COMMA.add(COMMA80);


            	            }
            	            break;

            	    }

            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA80, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1613);
            	    attr=rule_attribute();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rule_attribute.add(attr.getTree());

            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);



            // AST REWRITE
            // elements: rule_attribute, attributes_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 708:3: -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:708:6: ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_RULE_ATTRIBUTES, "VT_RULE_ATTRIBUTES"), root_1);

                // src/main/resources/org/drools/lang/DRL5x.g:708:27: ( attributes_key )?
                if ( stream_attributes_key.hasNext() ) {
                    adaptor.addChild(root_1, stream_attributes_key.nextTree());

                }
                stream_attributes_key.reset();
                if ( !(stream_rule_attribute.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_rule_attribute.hasNext() ) {
                    adaptor.addChild(root_1, stream_rule_attribute.nextTree());

                }
                stream_rule_attribute.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rule_attributes"

    public static class rule_attribute_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule_attribute"
    // src/main/resources/org/drools/lang/DRL5x.g:711:1: rule_attribute : ( salience | no_loop | agenda_group | timer | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect | calendars );
    public final DRL5xParser.rule_attribute_return rule_attribute() throws RecognitionException {
        DRL5xParser.rule_attribute_return retval = new DRL5xParser.rule_attribute_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.salience_return salience81 = null;

        DRL5xParser.no_loop_return no_loop82 = null;

        DRL5xParser.agenda_group_return agenda_group83 = null;

        DRL5xParser.timer_return timer84 = null;

        DRL5xParser.activation_group_return activation_group85 = null;

        DRL5xParser.auto_focus_return auto_focus86 = null;

        DRL5xParser.date_effective_return date_effective87 = null;

        DRL5xParser.date_expires_return date_expires88 = null;

        DRL5xParser.enabled_return enabled89 = null;

        DRL5xParser.ruleflow_group_return ruleflow_group90 = null;

        DRL5xParser.lock_on_active_return lock_on_active91 = null;

        DRL5xParser.dialect_return dialect92 = null;

        DRL5xParser.calendars_return calendars93 = null;



         boolean isFailed = true; pushParaphrases(DroolsParaphraseTypes.RULE_ATTRIBUTE); 
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:714:2: ( salience | no_loop | agenda_group | timer | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect | calendars )
            int alt38=13;
            alt38 = dfa38.predict(input);
            switch (alt38) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:714:4: salience
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_salience_in_rule_attribute1652);
                    salience81=salience();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, salience81.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:715:4: no_loop
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_no_loop_in_rule_attribute1658);
                    no_loop82=no_loop();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, no_loop82.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL5x.g:716:4: agenda_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1663);
                    agenda_group83=agenda_group();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, agenda_group83.getTree());

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRL5x.g:717:4: timer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_timer_in_rule_attribute1670);
                    timer84=timer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, timer84.getTree());

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRL5x.g:718:4: activation_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_activation_group_in_rule_attribute1677);
                    activation_group85=activation_group();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, activation_group85.getTree());

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRL5x.g:719:4: auto_focus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1683);
                    auto_focus86=auto_focus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, auto_focus86.getTree());

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRL5x.g:720:4: date_effective
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_effective_in_rule_attribute1689);
                    date_effective87=date_effective();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, date_effective87.getTree());

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DRL5x.g:721:4: date_expires
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_expires_in_rule_attribute1695);
                    date_expires88=date_expires();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, date_expires88.getTree());

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DRL5x.g:722:4: enabled
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enabled_in_rule_attribute1701);
                    enabled89=enabled();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enabled89.getTree());

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DRL5x.g:723:4: ruleflow_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1707);
                    ruleflow_group90=ruleflow_group();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleflow_group90.getTree());

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/DRL5x.g:724:4: lock_on_active
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1713);
                    lock_on_active91=lock_on_active();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lock_on_active91.getTree());

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/lang/DRL5x.g:725:4: dialect
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_dialect_in_rule_attribute1718);
                    dialect92=dialect();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, dialect92.getTree());

                    }
                    break;
                case 13 :
                    // src/main/resources/org/drools/lang/DRL5x.g:726:6: calendars
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_calendars_in_rule_attribute1726);
                    calendars93=calendars();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, calendars93.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); isFailed = false; if (!(retval.tree instanceof CommonErrorNode)) emit(Location.LOCATION_RULE_HEADER); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {

            	if (isEditorInterfaceEnabled && isFailed) {
            		if (input.LA(2) == EOF && input.LA(1) == ID){
            			emit(input.LT(1), DroolsEditorType.IDENTIFIER);
            			input.consume();
            		}
            	}

        }
        return retval;
    }
    // $ANTLR end "rule_attribute"

    public static class date_effective_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "date_effective"
    // src/main/resources/org/drools/lang/DRL5x.g:736:1: date_effective : date_effective_key STRING ;
    public final DRL5xParser.date_effective_return date_effective() throws RecognitionException {
        DRL5xParser.date_effective_return retval = new DRL5xParser.date_effective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING95=null;
        DRL5xParser.date_effective_key_return date_effective_key94 = null;


        Object STRING95_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:737:2: ( date_effective_key STRING )
            // src/main/resources/org/drools/lang/DRL5x.g:737:4: date_effective_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_effective_key_in_date_effective1740);
            date_effective_key94=date_effective_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_effective_key94.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING95=(Token)match(input,STRING,FOLLOW_STRING_in_date_effective1745); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING95_tree = (Object)adaptor.create(STRING95);
            adaptor.addChild(root_0, STRING95_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING95, DroolsEditorType.STRING_CONST );	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "date_effective"

    public static class date_expires_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "date_expires"
    // src/main/resources/org/drools/lang/DRL5x.g:741:1: date_expires : date_expires_key STRING ;
    public final DRL5xParser.date_expires_return date_expires() throws RecognitionException {
        DRL5xParser.date_expires_return retval = new DRL5xParser.date_expires_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING97=null;
        DRL5xParser.date_expires_key_return date_expires_key96 = null;


        Object STRING97_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:742:2: ( date_expires_key STRING )
            // src/main/resources/org/drools/lang/DRL5x.g:742:4: date_expires_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_expires_key_in_date_expires1759);
            date_expires_key96=date_expires_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_expires_key96.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING97=(Token)match(input,STRING,FOLLOW_STRING_in_date_expires1764); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING97_tree = (Object)adaptor.create(STRING97);
            adaptor.addChild(root_0, STRING97_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING97, DroolsEditorType.STRING_CONST );	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "date_expires"

    public static class enabled_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enabled"
    // src/main/resources/org/drools/lang/DRL5x.g:746:1: enabled : enabled_key ( BOOL | paren_chunk ) ;
    public final DRL5xParser.enabled_return enabled() throws RecognitionException {
        DRL5xParser.enabled_return retval = new DRL5xParser.enabled_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL99=null;
        DRL5xParser.enabled_key_return enabled_key98 = null;

        DRL5xParser.paren_chunk_return paren_chunk100 = null;


        Object BOOL99_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:747:2: ( enabled_key ( BOOL | paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL5x.g:747:4: enabled_key ( BOOL | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_enabled_key_in_enabled1779);
            enabled_key98=enabled_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(enabled_key98.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:748:6: ( BOOL | paren_chunk )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==BOOL) ) {
                alt39=1;
            }
            else if ( (LA39_0==LEFT_PAREN) ) {
                alt39=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:748:8: BOOL
                    {
                    BOOL99=(Token)match(input,BOOL,FOLLOW_BOOL_in_enabled1792); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL99_tree = (Object)adaptor.create(BOOL99);
                    adaptor.addChild(root_0, BOOL99_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(BOOL99, DroolsEditorType.BOOLEAN_CONST );	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:749:8: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_enabled1803);
                    paren_chunk100=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk100.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "enabled"

    public static class salience_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "salience"
    // src/main/resources/org/drools/lang/DRL5x.g:753:1: salience : salience_key ( INT | paren_chunk ) ;
    public final DRL5xParser.salience_return salience() throws RecognitionException {
        DRL5xParser.salience_return retval = new DRL5xParser.salience_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT102=null;
        DRL5xParser.salience_key_return salience_key101 = null;

        DRL5xParser.paren_chunk_return paren_chunk103 = null;


        Object INT102_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:754:2: ( salience_key ( INT | paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL5x.g:754:4: salience_key ( INT | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_salience_key_in_salience1823);
            salience_key101=salience_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(salience_key101.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:755:3: ( INT | paren_chunk )
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==INT) ) {
                alt40=1;
            }
            else if ( (LA40_0==LEFT_PAREN) ) {
                alt40=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;
            }
            switch (alt40) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:755:5: INT
                    {
                    INT102=(Token)match(input,INT,FOLLOW_INT_in_salience1832); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT102_tree = (Object)adaptor.create(INT102);
                    adaptor.addChild(root_0, INT102_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(INT102, DroolsEditorType.NUMERIC_CONST );	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:756:5: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1841);
                    paren_chunk103=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk103.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "salience"

    public static class no_loop_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "no_loop"
    // src/main/resources/org/drools/lang/DRL5x.g:760:1: no_loop : no_loop_key ( BOOL )? ;
    public final DRL5xParser.no_loop_return no_loop() throws RecognitionException {
        DRL5xParser.no_loop_return retval = new DRL5xParser.no_loop_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL105=null;
        DRL5xParser.no_loop_key_return no_loop_key104 = null;


        Object BOOL105_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:761:2: ( no_loop_key ( BOOL )? )
            // src/main/resources/org/drools/lang/DRL5x.g:761:4: no_loop_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_no_loop_key_in_no_loop1856);
            no_loop_key104=no_loop_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(no_loop_key104.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:761:66: ( BOOL )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==BOOL) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:761:66: BOOL
                    {
                    BOOL105=(Token)match(input,BOOL,FOLLOW_BOOL_in_no_loop1861); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL105_tree = (Object)adaptor.create(BOOL105);
                    adaptor.addChild(root_0, BOOL105_tree);
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(BOOL105, DroolsEditorType.BOOLEAN_CONST );	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "no_loop"

    public static class auto_focus_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "auto_focus"
    // src/main/resources/org/drools/lang/DRL5x.g:765:1: auto_focus : auto_focus_key ( BOOL )? ;
    public final DRL5xParser.auto_focus_return auto_focus() throws RecognitionException {
        DRL5xParser.auto_focus_return retval = new DRL5xParser.auto_focus_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL107=null;
        DRL5xParser.auto_focus_key_return auto_focus_key106 = null;


        Object BOOL107_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:766:2: ( auto_focus_key ( BOOL )? )
            // src/main/resources/org/drools/lang/DRL5x.g:766:4: auto_focus_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_auto_focus_key_in_auto_focus1876);
            auto_focus_key106=auto_focus_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(auto_focus_key106.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:766:69: ( BOOL )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==BOOL) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:766:69: BOOL
                    {
                    BOOL107=(Token)match(input,BOOL,FOLLOW_BOOL_in_auto_focus1881); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL107_tree = (Object)adaptor.create(BOOL107);
                    adaptor.addChild(root_0, BOOL107_tree);
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(BOOL107, DroolsEditorType.BOOLEAN_CONST );	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "auto_focus"

    public static class activation_group_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "activation_group"
    // src/main/resources/org/drools/lang/DRL5x.g:770:1: activation_group : activation_group_key STRING ;
    public final DRL5xParser.activation_group_return activation_group() throws RecognitionException {
        DRL5xParser.activation_group_return retval = new DRL5xParser.activation_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING109=null;
        DRL5xParser.activation_group_key_return activation_group_key108 = null;


        Object STRING109_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:771:2: ( activation_group_key STRING )
            // src/main/resources/org/drools/lang/DRL5x.g:771:4: activation_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_activation_group_key_in_activation_group1898);
            activation_group_key108=activation_group_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(activation_group_key108.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING109=(Token)match(input,STRING,FOLLOW_STRING_in_activation_group1903); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING109_tree = (Object)adaptor.create(STRING109);
            adaptor.addChild(root_0, STRING109_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING109, DroolsEditorType.STRING_CONST );	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "activation_group"

    public static class ruleflow_group_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleflow_group"
    // src/main/resources/org/drools/lang/DRL5x.g:775:1: ruleflow_group : ruleflow_group_key STRING ;
    public final DRL5xParser.ruleflow_group_return ruleflow_group() throws RecognitionException {
        DRL5xParser.ruleflow_group_return retval = new DRL5xParser.ruleflow_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING111=null;
        DRL5xParser.ruleflow_group_key_return ruleflow_group_key110 = null;


        Object STRING111_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:776:2: ( ruleflow_group_key STRING )
            // src/main/resources/org/drools/lang/DRL5x.g:776:4: ruleflow_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_ruleflow_group_key_in_ruleflow_group1917);
            ruleflow_group_key110=ruleflow_group_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(ruleflow_group_key110.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING111=(Token)match(input,STRING,FOLLOW_STRING_in_ruleflow_group1922); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING111_tree = (Object)adaptor.create(STRING111);
            adaptor.addChild(root_0, STRING111_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING111, DroolsEditorType.STRING_CONST );	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ruleflow_group"

    public static class agenda_group_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "agenda_group"
    // src/main/resources/org/drools/lang/DRL5x.g:780:1: agenda_group : agenda_group_key STRING ;
    public final DRL5xParser.agenda_group_return agenda_group() throws RecognitionException {
        DRL5xParser.agenda_group_return retval = new DRL5xParser.agenda_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING113=null;
        DRL5xParser.agenda_group_key_return agenda_group_key112 = null;


        Object STRING113_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:781:2: ( agenda_group_key STRING )
            // src/main/resources/org/drools/lang/DRL5x.g:781:4: agenda_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_agenda_group_key_in_agenda_group1936);
            agenda_group_key112=agenda_group_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(agenda_group_key112.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING113=(Token)match(input,STRING,FOLLOW_STRING_in_agenda_group1941); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING113_tree = (Object)adaptor.create(STRING113);
            adaptor.addChild(root_0, STRING113_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING113, DroolsEditorType.STRING_CONST );	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "agenda_group"

    public static class timer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "timer"
    // src/main/resources/org/drools/lang/DRL5x.g:785:1: timer : ( duration_key | timer_key ) ( INT | paren_chunk ) ;
    public final DRL5xParser.timer_return timer() throws RecognitionException {
        DRL5xParser.timer_return retval = new DRL5xParser.timer_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT116=null;
        DRL5xParser.duration_key_return duration_key114 = null;

        DRL5xParser.timer_key_return timer_key115 = null;

        DRL5xParser.paren_chunk_return paren_chunk117 = null;


        Object INT116_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:786:2: ( ( duration_key | timer_key ) ( INT | paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL5x.g:786:4: ( duration_key | timer_key ) ( INT | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL5x.g:786:4: ( duration_key | timer_key )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {
                int LA43_1 = input.LA(2);

                if ( (((validateIdentifierKey(DroolsSoftKeywords.DURATION)))) ) {
                    alt43=1;
                }
                else if ( (((validateIdentifierKey(DroolsSoftKeywords.TIMER)))) ) {
                    alt43=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 43, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:786:5: duration_key
                    {
                    pushFollow(FOLLOW_duration_key_in_timer1956);
                    duration_key114=duration_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(duration_key114.getTree(), root_0);

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:786:19: timer_key
                    {
                    pushFollow(FOLLOW_timer_key_in_timer1959);
                    timer_key115=timer_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(timer_key115.getTree(), root_0);

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:787:6: ( INT | paren_chunk )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==INT) ) {
                alt44=1;
            }
            else if ( (LA44_0==LEFT_PAREN) ) {
                alt44=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:787:8: INT
                    {
                    INT116=(Token)match(input,INT,FOLLOW_INT_in_timer1973); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT116_tree = (Object)adaptor.create(INT116);
                    adaptor.addChild(root_0, INT116_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(INT116, DroolsEditorType.NUMERIC_CONST );	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:788:8: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_timer1984);
                    paren_chunk117=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk117.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "timer"

    public static class calendars_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "calendars"
    // src/main/resources/org/drools/lang/DRL5x.g:792:1: calendars : calendars_key string_list ;
    public final DRL5xParser.calendars_return calendars() throws RecognitionException {
        DRL5xParser.calendars_return retval = new DRL5xParser.calendars_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.calendars_key_return calendars_key118 = null;

        DRL5xParser.string_list_return string_list119 = null;



        try {
            // src/main/resources/org/drools/lang/DRL5x.g:793:2: ( calendars_key string_list )
            // src/main/resources/org/drools/lang/DRL5x.g:793:4: calendars_key string_list
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_calendars_key_in_calendars2004);
            calendars_key118=calendars_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(calendars_key118.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            pushFollow(FOLLOW_string_list_in_calendars2009);
            string_list119=string_list();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, string_list119.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "calendars"

    public static class string_list_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "string_list"
    // src/main/resources/org/drools/lang/DRL5x.g:796:1: string_list : first= STRING ( ',' next= STRING )* -> STRING[$first,buf.toString()+\" ]\"] ;
    public final DRL5xParser.string_list_return string_list() throws RecognitionException {
        DRL5xParser.string_list_return retval = new DRL5xParser.string_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token first=null;
        Token next=null;
        Token char_literal120=null;

        Object first_tree=null;
        Object next_tree=null;
        Object char_literal120_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");


            StringBuilder buf = new StringBuilder();

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:800:2: (first= STRING ( ',' next= STRING )* -> STRING[$first,buf.toString()+\" ]\"] )
            // src/main/resources/org/drools/lang/DRL5x.g:800:4: first= STRING ( ',' next= STRING )*
            {
            first=(Token)match(input,STRING,FOLLOW_STRING_in_string_list2027); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_STRING.add(first);

            if ( state.backtracking==0 ) {
               buf.append( "[ "+ (first!=null?first.getText():null) ); 
            }
            // src/main/resources/org/drools/lang/DRL5x.g:801:5: ( ',' next= STRING )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( (LA45_0==COMMA) ) {
                    int LA45_2 = input.LA(2);

                    if ( (LA45_2==STRING) ) {
                        alt45=1;
                    }


                }


                switch (alt45) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:801:6: ',' next= STRING
            	    {
            	    char_literal120=(Token)match(input,COMMA,FOLLOW_COMMA_in_string_list2036); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(char_literal120);

            	    next=(Token)match(input,STRING,FOLLOW_STRING_in_string_list2040); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_STRING.add(next);

            	    if ( state.backtracking==0 ) {
            	       buf.append( ", " + (next!=null?next.getText():null) ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop45;
                }
            } while (true);



            // AST REWRITE
            // elements: STRING
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 802:2: -> STRING[$first,buf.toString()+\" ]\"]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(STRING, first, buf.toString()+" ]"));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "string_list"

    public static class dialect_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dialect"
    // src/main/resources/org/drools/lang/DRL5x.g:806:1: dialect : dialect_key STRING ;
    public final DRL5xParser.dialect_return dialect() throws RecognitionException {
        DRL5xParser.dialect_return retval = new DRL5xParser.dialect_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING122=null;
        DRL5xParser.dialect_key_return dialect_key121 = null;


        Object STRING122_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:807:2: ( dialect_key STRING )
            // src/main/resources/org/drools/lang/DRL5x.g:807:4: dialect_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_dialect_key_in_dialect2064);
            dialect_key121=dialect_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(dialect_key121.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING122=(Token)match(input,STRING,FOLLOW_STRING_in_dialect2069); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING122_tree = (Object)adaptor.create(STRING122);
            adaptor.addChild(root_0, STRING122_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING122, DroolsEditorType.STRING_CONST );	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "dialect"

    public static class lock_on_active_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lock_on_active"
    // src/main/resources/org/drools/lang/DRL5x.g:811:1: lock_on_active : lock_on_active_key ( BOOL )? ;
    public final DRL5xParser.lock_on_active_return lock_on_active() throws RecognitionException {
        DRL5xParser.lock_on_active_return retval = new DRL5xParser.lock_on_active_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL124=null;
        DRL5xParser.lock_on_active_key_return lock_on_active_key123 = null;


        Object BOOL124_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:812:2: ( lock_on_active_key ( BOOL )? )
            // src/main/resources/org/drools/lang/DRL5x.g:812:4: lock_on_active_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lock_on_active_key_in_lock_on_active2087);
            lock_on_active_key123=lock_on_active_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(lock_on_active_key123.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:812:73: ( BOOL )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==BOOL) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:812:73: BOOL
                    {
                    BOOL124=(Token)match(input,BOOL,FOLLOW_BOOL_in_lock_on_active2092); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL124_tree = (Object)adaptor.create(BOOL124);
                    adaptor.addChild(root_0, BOOL124_tree);
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(BOOL124, DroolsEditorType.BOOLEAN_CONST );	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lock_on_active"

    public static class normal_lhs_block_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "normal_lhs_block"
    // src/main/resources/org/drools/lang/DRL5x.g:816:1: normal_lhs_block : ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) ;
    public final DRL5xParser.normal_lhs_block_return normal_lhs_block() throws RecognitionException {
        DRL5xParser.normal_lhs_block_return retval = new DRL5xParser.normal_lhs_block_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.lhs_return lhs125 = null;


        RewriteRuleSubtreeStream stream_lhs=new RewriteRuleSubtreeStream(adaptor,"rule lhs");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:817:2: ( ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) )
            // src/main/resources/org/drools/lang/DRL5x.g:817:4: ( lhs )*
            {
            // src/main/resources/org/drools/lang/DRL5x.g:817:4: ( lhs )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==ID) ) {
                    int LA47_1 = input.LA(2);

                    if ( (!((((validateIdentifierKey(DroolsSoftKeywords.END)))))) ) {
                        alt47=1;
                    }


                }
                else if ( (LA47_0==LEFT_PAREN) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:817:4: lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block2107);
            	    lhs125=lhs();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_lhs.add(lhs125.getTree());

            	    }
            	    break;

            	default :
            	    break loop47;
                }
            } while (true);



            // AST REWRITE
            // elements: lhs
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 818:2: -> ^( VT_AND_IMPLICIT ( lhs )* )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:818:5: ^( VT_AND_IMPLICIT ( lhs )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_AND_IMPLICIT, "VT_AND_IMPLICIT"), root_1);

                // src/main/resources/org/drools/lang/DRL5x.g:818:23: ( lhs )*
                while ( stream_lhs.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs.nextTree());

                }
                stream_lhs.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "normal_lhs_block"

    public static class lhs_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lhs"
    // src/main/resources/org/drools/lang/DRL5x.g:821:1: lhs : lhs_or ;
    public final DRL5xParser.lhs_return lhs() throws RecognitionException {
        DRL5xParser.lhs_return retval = new DRL5xParser.lhs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.lhs_or_return lhs_or126 = null;



        try {
            // src/main/resources/org/drools/lang/DRL5x.g:821:5: ( lhs_or )
            // src/main/resources/org/drools/lang/DRL5x.g:821:7: lhs_or
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_or_in_lhs2128);
            lhs_or126=lhs_or();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_or126.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lhs"

    public static class lhs_or_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lhs_or"
    // src/main/resources/org/drools/lang/DRL5x.g:824:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );
    public final DRL5xParser.lhs_or_return lhs_or() throws RecognitionException {
        DRL5xParser.lhs_or_return retval = new DRL5xParser.lhs_or_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        Token LEFT_PAREN127=null;
        Token RIGHT_PAREN129=null;
        DRL5xParser.or_key_return or = null;

        DRL5xParser.or_key_return value = null;

        DRL5xParser.lhs_and_return lhs_and128 = null;

        DRL5xParser.lhs_and_return lhs_and130 = null;

        DRL5xParser.lhs_and_return lhs_and131 = null;


        Object pipe_tree=null;
        Object LEFT_PAREN127_tree=null;
        Object RIGHT_PAREN129_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");
        RewriteRuleSubtreeStream stream_lhs_and=new RewriteRuleSubtreeStream(adaptor,"rule lhs_and");

        	Token orToken = null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:827:3: ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* )
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==LEFT_PAREN) ) {
                int LA51_1 = input.LA(2);

                if ( (synpred1_DRL5x()) ) {
                    alt51=1;
                }
                else if ( (true) ) {
                    alt51=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 51, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA51_0==ID) ) {
                alt51=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:827:5: ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN
                    {
                    LEFT_PAREN127=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or2152); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN127);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN127, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_or_key_in_lhs_or2162);
                    or=or_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_or_key.add(or.getTree());
                    if ( state.backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    }
                    // src/main/resources/org/drools/lang/DRL5x.g:831:4: ( lhs_and )+
                    int cnt48=0;
                    loop48:
                    do {
                        int alt48=2;
                        int LA48_0 = input.LA(1);

                        if ( (LA48_0==ID||LA48_0==LEFT_PAREN) ) {
                            alt48=1;
                        }


                        switch (alt48) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRL5x.g:831:4: lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2170);
                    	    lhs_and128=lhs_and();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_lhs_and.add(lhs_and128.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt48 >= 1 ) break loop48;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(48, input);
                                throw eee;
                        }
                        cnt48++;
                    } while (true);

                    RIGHT_PAREN129=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or2176); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN129);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN129, DroolsEditorType.SYMBOL);	
                    }


                    // AST REWRITE
                    // elements: lhs_and, RIGHT_PAREN
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 833:3: -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
                    {
                        // src/main/resources/org/drools/lang/DRL5x.g:833:6: ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_OR_PREFIX, (or!=null?((Token)or.start):null)), root_1);

                        if ( !(stream_lhs_and.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_lhs_and.hasNext() ) {
                            adaptor.addChild(root_1, stream_lhs_and.nextTree());

                        }
                        stream_lhs_and.reset();
                        adaptor.addChild(root_1, stream_RIGHT_PAREN.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:834:4: ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    {
                    // src/main/resources/org/drools/lang/DRL5x.g:834:4: ( lhs_and -> lhs_and )
                    // src/main/resources/org/drools/lang/DRL5x.g:834:5: lhs_and
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or2199);
                    lhs_and130=lhs_and();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_and.add(lhs_and130.getTree());


                    // AST REWRITE
                    // elements: lhs_and
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 834:13: -> lhs_and
                    {
                        adaptor.addChild(root_0, stream_lhs_and.nextTree());

                    }

                    retval.tree = root_0;}
                    }

                    // src/main/resources/org/drools/lang/DRL5x.g:835:3: ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    loop50:
                    do {
                        int alt50=2;
                        int LA50_0 = input.LA(1);

                        if ( (LA50_0==ID) ) {
                            int LA50_1 = input.LA(2);

                            if ( ((synpred2_DRL5x()&&((validateIdentifierKey(DroolsSoftKeywords.OR))))) ) {
                                alt50=1;
                            }


                        }
                        else if ( (LA50_0==DOUBLE_PIPE) ) {
                            int LA50_3 = input.LA(2);

                            if ( (synpred2_DRL5x()) ) {
                                alt50=1;
                            }


                        }


                        switch (alt50) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRL5x.g:835:5: ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and
                    	    {
                    	    // src/main/resources/org/drools/lang/DRL5x.g:835:28: (value= or_key | pipe= DOUBLE_PIPE )
                    	    int alt49=2;
                    	    int LA49_0 = input.LA(1);

                    	    if ( (LA49_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                    	        alt49=1;
                    	    }
                    	    else if ( (LA49_0==DOUBLE_PIPE) ) {
                    	        alt49=2;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("", 49, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt49) {
                    	        case 1 :
                    	            // src/main/resources/org/drools/lang/DRL5x.g:835:29: value= or_key
                    	            {
                    	            pushFollow(FOLLOW_or_key_in_lhs_or2221);
                    	            value=or_key();

                    	            state._fsp--;
                    	            if (state.failed) return retval;
                    	            if ( state.backtracking==0 ) stream_or_key.add(value.getTree());
                    	            if ( state.backtracking==0 ) {
                    	              orToken = (value!=null?((Token)value.start):null);
                    	            }

                    	            }
                    	            break;
                    	        case 2 :
                    	            // src/main/resources/org/drools/lang/DRL5x.g:835:69: pipe= DOUBLE_PIPE
                    	            {
                    	            pipe=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_lhs_or2228); if (state.failed) return retval; 
                    	            if ( state.backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

                    	            if ( state.backtracking==0 ) {
                    	              orToken = pipe; emit(pipe, DroolsEditorType.SYMBOL);
                    	            }

                    	            }
                    	            break;

                    	    }

                    	    if ( state.backtracking==0 ) {
                    	      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    	    }
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2239);
                    	    lhs_and131=lhs_and();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_lhs_and.add(lhs_and131.getTree());


                    	    // AST REWRITE
                    	    // elements: lhs_or, lhs_and
                    	    // token labels: 
                    	    // rule labels: retval
                    	    // token list labels: 
                    	    // rule list labels: 
                    	    // wildcard labels: 
                    	    if ( state.backtracking==0 ) {
                    	    retval.tree = root_0;
                    	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    	    root_0 = (Object)adaptor.nil();
                    	    // 838:3: -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
                    	    {
                    	        // src/main/resources/org/drools/lang/DRL5x.g:838:6: ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
                    	        {
                    	        Object root_1 = (Object)adaptor.nil();
                    	        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_OR_INFIX, orToken), root_1);

                    	        adaptor.addChild(root_1, stream_retval.nextTree());
                    	        adaptor.addChild(root_1, stream_lhs_and.nextTree());

                    	        adaptor.addChild(root_0, root_1);
                    	        }

                    	    }

                    	    retval.tree = root_0;}
                    	    }
                    	    break;

                    	default :
                    	    break loop50;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lhs_or"

    public static class lhs_and_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lhs_and"
    // src/main/resources/org/drools/lang/DRL5x.g:841:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );
    public final DRL5xParser.lhs_and_return lhs_and() throws RecognitionException {
        DRL5xParser.lhs_and_return retval = new DRL5xParser.lhs_and_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token amper=null;
        Token LEFT_PAREN132=null;
        Token RIGHT_PAREN134=null;
        DRL5xParser.and_key_return and = null;

        DRL5xParser.and_key_return value = null;

        DRL5xParser.lhs_unary_return lhs_unary133 = null;

        DRL5xParser.lhs_unary_return lhs_unary135 = null;

        DRL5xParser.lhs_unary_return lhs_unary136 = null;


        Object amper_tree=null;
        Object LEFT_PAREN132_tree=null;
        Object RIGHT_PAREN134_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleTokenStream stream_DOUBLE_AMPER=new RewriteRuleTokenStream(adaptor,"token DOUBLE_AMPER");
        RewriteRuleSubtreeStream stream_lhs_unary=new RewriteRuleSubtreeStream(adaptor,"rule lhs_unary");
        RewriteRuleSubtreeStream stream_and_key=new RewriteRuleSubtreeStream(adaptor,"rule and_key");

        	Token andToken = null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:844:3: ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==LEFT_PAREN) ) {
                int LA55_1 = input.LA(2);

                if ( (synpred3_DRL5x()) ) {
                    alt55=1;
                }
                else if ( (true) ) {
                    alt55=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 55, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA55_0==ID) ) {
                alt55=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:844:5: ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN
                    {
                    LEFT_PAREN132=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and2280); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN132);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN132, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_and_key_in_lhs_and2290);
                    and=and_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_and_key.add(and.getTree());
                    if ( state.backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    }
                    // src/main/resources/org/drools/lang/DRL5x.g:848:4: ( lhs_unary )+
                    int cnt52=0;
                    loop52:
                    do {
                        int alt52=2;
                        int LA52_0 = input.LA(1);

                        if ( (LA52_0==ID||LA52_0==LEFT_PAREN) ) {
                            alt52=1;
                        }


                        switch (alt52) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRL5x.g:848:4: lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2298);
                    	    lhs_unary133=lhs_unary();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_lhs_unary.add(lhs_unary133.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt52 >= 1 ) break loop52;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(52, input);
                                throw eee;
                        }
                        cnt52++;
                    } while (true);

                    RIGHT_PAREN134=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and2304); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN134);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN134, DroolsEditorType.SYMBOL);	
                    }


                    // AST REWRITE
                    // elements: RIGHT_PAREN, lhs_unary
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 850:3: -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
                    {
                        // src/main/resources/org/drools/lang/DRL5x.g:850:6: ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_AND_PREFIX, (and!=null?((Token)and.start):null)), root_1);

                        if ( !(stream_lhs_unary.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_lhs_unary.hasNext() ) {
                            adaptor.addChild(root_1, stream_lhs_unary.nextTree());

                        }
                        stream_lhs_unary.reset();
                        adaptor.addChild(root_1, stream_RIGHT_PAREN.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:851:4: ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    {
                    // src/main/resources/org/drools/lang/DRL5x.g:851:4: ( lhs_unary -> lhs_unary )
                    // src/main/resources/org/drools/lang/DRL5x.g:851:5: lhs_unary
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and2328);
                    lhs_unary135=lhs_unary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_unary.add(lhs_unary135.getTree());


                    // AST REWRITE
                    // elements: lhs_unary
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 851:15: -> lhs_unary
                    {
                        adaptor.addChild(root_0, stream_lhs_unary.nextTree());

                    }

                    retval.tree = root_0;}
                    }

                    // src/main/resources/org/drools/lang/DRL5x.g:852:3: ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    loop54:
                    do {
                        int alt54=2;
                        int LA54_0 = input.LA(1);

                        if ( (LA54_0==ID) ) {
                            int LA54_2 = input.LA(2);

                            if ( ((synpred4_DRL5x()&&((validateIdentifierKey(DroolsSoftKeywords.AND))))) ) {
                                alt54=1;
                            }


                        }
                        else if ( (LA54_0==DOUBLE_AMPER) ) {
                            int LA54_3 = input.LA(2);

                            if ( (synpred4_DRL5x()) ) {
                                alt54=1;
                            }


                        }


                        switch (alt54) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRL5x.g:852:5: ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary
                    	    {
                    	    // src/main/resources/org/drools/lang/DRL5x.g:852:30: (value= and_key | amper= DOUBLE_AMPER )
                    	    int alt53=2;
                    	    int LA53_0 = input.LA(1);

                    	    if ( (LA53_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))))) {
                    	        alt53=1;
                    	    }
                    	    else if ( (LA53_0==DOUBLE_AMPER) ) {
                    	        alt53=2;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("", 53, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt53) {
                    	        case 1 :
                    	            // src/main/resources/org/drools/lang/DRL5x.g:852:31: value= and_key
                    	            {
                    	            pushFollow(FOLLOW_and_key_in_lhs_and2350);
                    	            value=and_key();

                    	            state._fsp--;
                    	            if (state.failed) return retval;
                    	            if ( state.backtracking==0 ) stream_and_key.add(value.getTree());
                    	            if ( state.backtracking==0 ) {
                    	              andToken = (value!=null?((Token)value.start):null);
                    	            }

                    	            }
                    	            break;
                    	        case 2 :
                    	            // src/main/resources/org/drools/lang/DRL5x.g:852:73: amper= DOUBLE_AMPER
                    	            {
                    	            amper=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_lhs_and2357); if (state.failed) return retval; 
                    	            if ( state.backtracking==0 ) stream_DOUBLE_AMPER.add(amper);

                    	            if ( state.backtracking==0 ) {
                    	              andToken = amper; emit(amper, DroolsEditorType.SYMBOL);
                    	            }

                    	            }
                    	            break;

                    	    }

                    	    if ( state.backtracking==0 ) {
                    	      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    	    }
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2368);
                    	    lhs_unary136=lhs_unary();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_lhs_unary.add(lhs_unary136.getTree());


                    	    // AST REWRITE
                    	    // elements: lhs_unary, lhs_and
                    	    // token labels: 
                    	    // rule labels: retval
                    	    // token list labels: 
                    	    // rule list labels: 
                    	    // wildcard labels: 
                    	    if ( state.backtracking==0 ) {
                    	    retval.tree = root_0;
                    	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    	    root_0 = (Object)adaptor.nil();
                    	    // 855:3: -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
                    	    {
                    	        // src/main/resources/org/drools/lang/DRL5x.g:855:6: ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
                    	        {
                    	        Object root_1 = (Object)adaptor.nil();
                    	        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_AND_INFIX, andToken), root_1);

                    	        adaptor.addChild(root_1, stream_retval.nextTree());
                    	        adaptor.addChild(root_1, stream_lhs_unary.nextTree());

                    	        adaptor.addChild(root_0, root_1);
                    	        }

                    	    }

                    	    retval.tree = root_0;}
                    	    }
                    	    break;

                    	default :
                    	    break loop54;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lhs_and"

    public static class lhs_unary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lhs_unary"
    // src/main/resources/org/drools/lang/DRL5x.g:858:1: lhs_unary : ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? ;
    public final DRL5xParser.lhs_unary_return lhs_unary() throws RecognitionException {
        DRL5xParser.lhs_unary_return retval = new DRL5xParser.lhs_unary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN142=null;
        Token RIGHT_PAREN144=null;
        Token SEMICOLON146=null;
        DRL5xParser.lhs_exist_return lhs_exist137 = null;

        DRL5xParser.lhs_not_binding_return lhs_not_binding138 = null;

        DRL5xParser.lhs_not_return lhs_not139 = null;

        DRL5xParser.lhs_eval_return lhs_eval140 = null;

        DRL5xParser.lhs_forall_return lhs_forall141 = null;

        DRL5xParser.lhs_or_return lhs_or143 = null;

        DRL5xParser.pattern_source_return pattern_source145 = null;


        Object LEFT_PAREN142_tree=null;
        Object RIGHT_PAREN144_tree=null;
        Object SEMICOLON146_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:859:2: ( ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? )
            // src/main/resources/org/drools/lang/DRL5x.g:859:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL5x.g:859:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )
            int alt56=7;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==ID) ) {
                int LA56_1 = input.LA(2);

                if ( (((validateIdentifierKey(DroolsSoftKeywords.EXISTS)))) ) {
                    alt56=1;
                }
                else if ( (((validateNotWithBinding())&&((validateIdentifierKey(DroolsSoftKeywords.NOT))))) ) {
                    alt56=2;
                }
                else if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                    alt56=3;
                }
                else if ( (((validateIdentifierKey(DroolsSoftKeywords.EVAL)))) ) {
                    alt56=4;
                }
                else if ( (((validateIdentifierKey(DroolsSoftKeywords.FORALL)))) ) {
                    alt56=5;
                }
                else if ( (true) ) {
                    alt56=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 56, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA56_0==LEFT_PAREN) ) {
                alt56=6;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:859:6: lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary2399);
                    lhs_exist137=lhs_exist();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_exist137.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:860:4: {...}? => lhs_not_binding
                    {
                    if ( !((validateNotWithBinding())) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "lhs_unary", "validateNotWithBinding()");
                    }
                    pushFollow(FOLLOW_lhs_not_binding_in_lhs_unary2407);
                    lhs_not_binding138=lhs_not_binding();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_not_binding138.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL5x.g:861:5: lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary2413);
                    lhs_not139=lhs_not();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_not139.getTree());

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRL5x.g:862:5: lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2419);
                    lhs_eval140=lhs_eval();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_eval140.getTree());

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRL5x.g:863:5: lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary2425);
                    lhs_forall141=lhs_forall();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_forall141.getTree());

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRL5x.g:864:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN142=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2431); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN142, DroolsEditorType.SYMBOL); emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary2442);
                    lhs_or143=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_or143.getTree());
                    RIGHT_PAREN144=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2448); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_PAREN144_tree = (Object)adaptor.create(RIGHT_PAREN144);
                    adaptor.addChild(root_0, RIGHT_PAREN144_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN144, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRL5x.g:867:5: pattern_source
                    {
                    pushFollow(FOLLOW_pattern_source_in_lhs_unary2456);
                    pattern_source145=pattern_source();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, pattern_source145.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL5x.g:869:3: ( ( SEMICOLON )=> SEMICOLON )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==SEMICOLON) ) {
                int LA57_1 = input.LA(2);

                if ( (synpred5_DRL5x()) ) {
                    alt57=1;
                }
            }
            switch (alt57) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:869:4: ( SEMICOLON )=> SEMICOLON
                    {
                    SEMICOLON146=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_lhs_unary2470); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	emit(SEMICOLON146, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lhs_unary"

    public static class lhs_exist_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lhs_exist"
    // src/main/resources/org/drools/lang/DRL5x.g:872:1: lhs_exist : exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final DRL5xParser.lhs_exist_return lhs_exist() throws RecognitionException {
        DRL5xParser.lhs_exist_return retval = new DRL5xParser.lhs_exist_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN149=null;
        Token RIGHT_PAREN151=null;
        DRL5xParser.exists_key_return exists_key147 = null;

        DRL5xParser.lhs_or_return lhs_or148 = null;

        DRL5xParser.lhs_or_return lhs_or150 = null;

        DRL5xParser.lhs_pattern_return lhs_pattern152 = null;


        Object LEFT_PAREN149_tree=null;
        Object RIGHT_PAREN151_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_exists_key=new RewriteRuleSubtreeStream(adaptor,"rule exists_key");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:873:2: ( exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // src/main/resources/org/drools/lang/DRL5x.g:873:4: exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_exists_key_in_lhs_exist2486);
            exists_key147=exists_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_exists_key.add(exists_key147.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:875:10: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt58=3;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==LEFT_PAREN) ) {
                int LA58_1 = input.LA(2);

                if ( (synpred6_DRL5x()) ) {
                    alt58=1;
                }
                else if ( (true) ) {
                    alt58=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 58, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA58_0==ID) ) {
                int LA58_2 = input.LA(2);

                if ( (((synpred6_DRL5x()&&((validateIdentifierKey(DroolsSoftKeywords.EVAL))))||(synpred6_DRL5x()&&((validateIdentifierKey(DroolsSoftKeywords.NOT))))||((synpred6_DRL5x()&&(validateNotWithBinding()))&&((validateIdentifierKey(DroolsSoftKeywords.NOT))))||synpred6_DRL5x()||(synpred6_DRL5x()&&((validateIdentifierKey(DroolsSoftKeywords.FORALL))))||(synpred6_DRL5x()&&((validateIdentifierKey(DroolsSoftKeywords.EXISTS)))))) ) {
                    alt58=1;
                }
                else if ( (true) ) {
                    alt58=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 58, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 58, 0, input);

                throw nvae;
            }
            switch (alt58) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:875:12: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2513);
                    lhs_or148=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or148.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:876:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN149=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist2520); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN149);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN149, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2528);
                    lhs_or150=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or150.getTree());
                    RIGHT_PAREN151=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist2535); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN151);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN151, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL5x.g:879:12: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist2550);
                    lhs_pattern152=lhs_pattern();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_pattern.add(lhs_pattern152.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: RIGHT_PAREN, lhs_pattern, lhs_or, exists_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 881:10: -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:881:13: ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_exists_key.nextNode(), root_1);

                // src/main/resources/org/drools/lang/DRL5x.g:881:26: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.nextTree());

                }
                stream_lhs_or.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:881:34: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.nextTree());

                }
                stream_lhs_pattern.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:881:47: ( RIGHT_PAREN )?
                if ( stream_RIGHT_PAREN.hasNext() ) {
                    adaptor.addChild(root_1, stream_RIGHT_PAREN.nextNode());

                }
                stream_RIGHT_PAREN.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lhs_exist"

    public static class lhs_not_binding_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lhs_not_binding"
    // src/main/resources/org/drools/lang/DRL5x.g:884:1: lhs_not_binding : not_key fact_binding -> ^( not_key ^( VT_PATTERN fact_binding ) ) ;
    public final DRL5xParser.lhs_not_binding_return lhs_not_binding() throws RecognitionException {
        DRL5xParser.lhs_not_binding_return retval = new DRL5xParser.lhs_not_binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.not_key_return not_key153 = null;

        DRL5xParser.fact_binding_return fact_binding154 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:885:2: ( not_key fact_binding -> ^( not_key ^( VT_PATTERN fact_binding ) ) )
            // src/main/resources/org/drools/lang/DRL5x.g:885:4: not_key fact_binding
            {
            pushFollow(FOLLOW_not_key_in_lhs_not_binding2596);
            not_key153=not_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_not_key.add(not_key153.getTree());
            pushFollow(FOLLOW_fact_binding_in_lhs_not_binding2598);
            fact_binding154=fact_binding();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_fact_binding.add(fact_binding154.getTree());


            // AST REWRITE
            // elements: not_key, fact_binding
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 886:2: -> ^( not_key ^( VT_PATTERN fact_binding ) )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:886:5: ^( not_key ^( VT_PATTERN fact_binding ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // src/main/resources/org/drools/lang/DRL5x.g:886:15: ^( VT_PATTERN fact_binding )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_PATTERN, "VT_PATTERN"), root_2);

                adaptor.addChild(root_2, stream_fact_binding.nextTree());

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lhs_not_binding"

    public static class lhs_not_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lhs_not"
    // src/main/resources/org/drools/lang/DRL5x.g:889:1: lhs_not : not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final DRL5xParser.lhs_not_return lhs_not() throws RecognitionException {
        DRL5xParser.lhs_not_return retval = new DRL5xParser.lhs_not_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN157=null;
        Token RIGHT_PAREN159=null;
        DRL5xParser.not_key_return not_key155 = null;

        DRL5xParser.lhs_or_return lhs_or156 = null;

        DRL5xParser.lhs_or_return lhs_or158 = null;

        DRL5xParser.lhs_pattern_return lhs_pattern160 = null;


        Object LEFT_PAREN157_tree=null;
        Object RIGHT_PAREN159_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:889:9: ( not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // src/main/resources/org/drools/lang/DRL5x.g:889:11: not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_not_key_in_lhs_not2621);
            not_key155=not_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_not_key.add(not_key155.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:891:3: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt59=3;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==LEFT_PAREN) ) {
                int LA59_1 = input.LA(2);

                if ( (synpred7_DRL5x()) ) {
                    alt59=1;
                }
                else if ( (true) ) {
                    alt59=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 59, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA59_0==ID) ) {
                int LA59_2 = input.LA(2);

                if ( (synpred7_DRL5x()) ) {
                    alt59=1;
                }
                else if ( (true) ) {
                    alt59=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 59, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }
            switch (alt59) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:891:5: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    if ( state.backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2643);
                    lhs_or156=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or156.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:892:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN157=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2650); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN157);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN157, DroolsEditorType.SYMBOL); emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2659);
                    lhs_or158=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or158.getTree());
                    RIGHT_PAREN159=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2665); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN159);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN159, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL5x.g:895:6: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2675);
                    lhs_pattern160=lhs_pattern();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_pattern.add(lhs_pattern160.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: lhs_pattern, not_key, lhs_or, RIGHT_PAREN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 896:10: -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:896:13: ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // src/main/resources/org/drools/lang/DRL5x.g:896:23: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.nextTree());

                }
                stream_lhs_or.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:896:31: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.nextTree());

                }
                stream_lhs_pattern.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:896:44: ( RIGHT_PAREN )?
                if ( stream_RIGHT_PAREN.hasNext() ) {
                    adaptor.addChild(root_1, stream_RIGHT_PAREN.nextNode());

                }
                stream_RIGHT_PAREN.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lhs_not"

    public static class lhs_eval_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lhs_eval"
    // src/main/resources/org/drools/lang/DRL5x.g:899:1: lhs_eval : ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) ;
    public final DRL5xParser.lhs_eval_return lhs_eval() throws RecognitionException {
        DRL5xParser.lhs_eval_return retval = new DRL5xParser.lhs_eval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.eval_key_return ev = null;

        DRL5xParser.paren_chunk_return pc = null;


        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        RewriteRuleSubtreeStream stream_eval_key=new RewriteRuleSubtreeStream(adaptor,"rule eval_key");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:900:2: (ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL5x.g:900:4: ev= eval_key pc= paren_chunk
            {
            pushFollow(FOLLOW_eval_key_in_lhs_eval2714);
            ev=eval_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_eval_key.add(ev.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_EVAL);	
            }
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2723);
            pc=paren_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_paren_chunk.add(pc.getTree());
            if ( state.backtracking==0 ) {
              	if (((DroolsTree) (pc!=null?((Object)pc.tree):null)).getText() != null){
              			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	            		
              		}
              	
            }
            if ( state.backtracking==0 ) {
              	String body = safeSubstring( (pc!=null?input.toString(pc.start,pc.stop):null), 1, (pc!=null?input.toString(pc.start,pc.stop):null).length()-1 );
              		checkTrailingSemicolon( body, (ev!=null?((Token)ev.start):null) );	
            }


            // AST REWRITE
            // elements: eval_key, paren_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 909:3: -> ^( eval_key paren_chunk )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:909:6: ^( eval_key paren_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_eval_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lhs_eval"

    public static class lhs_forall_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lhs_forall"
    // src/main/resources/org/drools/lang/DRL5x.g:912:1: lhs_forall : forall_key LEFT_PAREN ( pattern_source )+ RIGHT_PAREN -> ^( forall_key ( pattern_source )+ RIGHT_PAREN ) ;
    public final DRL5xParser.lhs_forall_return lhs_forall() throws RecognitionException {
        DRL5xParser.lhs_forall_return retval = new DRL5xParser.lhs_forall_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN162=null;
        Token RIGHT_PAREN164=null;
        DRL5xParser.forall_key_return forall_key161 = null;

        DRL5xParser.pattern_source_return pattern_source163 = null;


        Object LEFT_PAREN162_tree=null;
        Object RIGHT_PAREN164_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_pattern_source=new RewriteRuleSubtreeStream(adaptor,"rule pattern_source");
        RewriteRuleSubtreeStream stream_forall_key=new RewriteRuleSubtreeStream(adaptor,"rule forall_key");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:913:2: ( forall_key LEFT_PAREN ( pattern_source )+ RIGHT_PAREN -> ^( forall_key ( pattern_source )+ RIGHT_PAREN ) )
            // src/main/resources/org/drools/lang/DRL5x.g:913:4: forall_key LEFT_PAREN ( pattern_source )+ RIGHT_PAREN
            {
            pushFollow(FOLLOW_forall_key_in_lhs_forall2750);
            forall_key161=forall_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_forall_key.add(forall_key161.getTree());
            LEFT_PAREN162=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2755); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN162);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN162, DroolsEditorType.SYMBOL);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:915:4: ( pattern_source )+
            int cnt60=0;
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==ID) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:915:4: pattern_source
            	    {
            	    pushFollow(FOLLOW_pattern_source_in_lhs_forall2763);
            	    pattern_source163=pattern_source();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_pattern_source.add(pattern_source163.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt60 >= 1 ) break loop60;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(60, input);
                        throw eee;
                }
                cnt60++;
            } while (true);

            RIGHT_PAREN164=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall2769); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN164);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN164, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: pattern_source, RIGHT_PAREN, forall_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 917:3: -> ^( forall_key ( pattern_source )+ RIGHT_PAREN )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:917:6: ^( forall_key ( pattern_source )+ RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_forall_key.nextNode(), root_1);

                if ( !(stream_pattern_source.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_pattern_source.hasNext() ) {
                    adaptor.addChild(root_1, stream_pattern_source.nextTree());

                }
                stream_pattern_source.reset();
                adaptor.addChild(root_1, stream_RIGHT_PAREN.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lhs_forall"

    public static class pattern_source_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "pattern_source"
    // src/main/resources/org/drools/lang/DRL5x.g:920:1: pattern_source : lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? ;
    public final DRL5xParser.pattern_source_return pattern_source() throws RecognitionException {
        DRL5xParser.pattern_source_return retval = new DRL5xParser.pattern_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FROM167=null;
        DRL5xParser.lhs_pattern_return lhs_pattern165 = null;

        DRL5xParser.over_clause_return over_clause166 = null;

        DRL5xParser.accumulate_statement_return accumulate_statement168 = null;

        DRL5xParser.collect_statement_return collect_statement169 = null;

        DRL5xParser.entrypoint_statement_return entrypoint_statement170 = null;

        DRL5xParser.from_source_return from_source171 = null;


        Object FROM167_tree=null;

         boolean isFailed = true;	
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:923:2: ( lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? )
            // src/main/resources/org/drools/lang/DRL5x.g:923:4: lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_pattern_in_pattern_source2805);
            lhs_pattern165=lhs_pattern();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_pattern165.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:924:3: ( over_clause )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==OVER) ) {
                alt61=1;
            }
            switch (alt61) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:924:3: over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_pattern_source2809);
                    over_clause166=over_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, over_clause166.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL5x.g:925:3: ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==FROM) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:926:4: FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    {
                    FROM167=(Token)match(input,FROM,FOLLOW_FROM_in_pattern_source2819); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FROM167_tree = (Object)adaptor.create(FROM167);
                    root_0 = (Object)adaptor.becomeRoot(FROM167_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(FROM167, DroolsEditorType.KEYWORD);
                      			emit(Location.LOCATION_LHS_FROM);	
                    }
                    // src/main/resources/org/drools/lang/DRL5x.g:929:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    int alt62=4;
                    switch ( input.LA(1) ) {
                    case ACCUMULATE:
                        {
                        alt62=1;
                        }
                        break;
                    case COLLECT:
                        {
                        alt62=2;
                        }
                        break;
                    case ID:
                        {
                        int LA62_3 = input.LA(2);

                        if ( (LA62_3==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT))))) {
                            alt62=3;
                        }
                        else if ( ((LA62_3>=SEMICOLON && LA62_3<=DOT)||(LA62_3>=LEFT_PAREN && LA62_3<=RIGHT_PAREN)||(LA62_3>=DOUBLE_PIPE && LA62_3<=DOUBLE_AMPER)||LA62_3==THEN) ) {
                            alt62=4;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 62, 3, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 62, 0, input);

                        throw nvae;
                    }

                    switch (alt62) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRL5x.g:929:14: accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_pattern_source2839);
                            accumulate_statement168=accumulate_statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, accumulate_statement168.getTree());

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DRL5x.g:930:15: collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_pattern_source2855);
                            collect_statement169=collect_statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, collect_statement169.getTree());

                            }
                            break;
                        case 3 :
                            // src/main/resources/org/drools/lang/DRL5x.g:931:15: entrypoint_statement
                            {
                            pushFollow(FOLLOW_entrypoint_statement_in_pattern_source2872);
                            entrypoint_statement170=entrypoint_statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, entrypoint_statement170.getTree());

                            }
                            break;
                        case 4 :
                            // src/main/resources/org/drools/lang/DRL5x.g:932:15: from_source
                            {
                            pushFollow(FOLLOW_from_source_in_pattern_source2888);
                            from_source171=from_source();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, from_source171.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               isFailed = false;	
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
    // $ANTLR end "pattern_source"

    public static class over_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "over_clause"
    // src/main/resources/org/drools/lang/DRL5x.g:950:1: over_clause : OVER over_elements ( COMMA over_elements )* ;
    public final DRL5xParser.over_clause_return over_clause() throws RecognitionException {
        DRL5xParser.over_clause_return retval = new DRL5xParser.over_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OVER172=null;
        Token COMMA174=null;
        DRL5xParser.over_elements_return over_elements173 = null;

        DRL5xParser.over_elements_return over_elements175 = null;


        Object OVER172_tree=null;
        Object COMMA174_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:951:2: ( OVER over_elements ( COMMA over_elements )* )
            // src/main/resources/org/drools/lang/DRL5x.g:951:4: OVER over_elements ( COMMA over_elements )*
            {
            root_0 = (Object)adaptor.nil();

            OVER172=(Token)match(input,OVER,FOLLOW_OVER_in_over_clause2920); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            OVER172_tree = (Object)adaptor.create(OVER172);
            root_0 = (Object)adaptor.becomeRoot(OVER172_tree, root_0);
            }
            if ( state.backtracking==0 ) {
              	emit(OVER172, DroolsEditorType.KEYWORD);	
            }
            pushFollow(FOLLOW_over_elements_in_over_clause2925);
            over_elements173=over_elements();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, over_elements173.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:952:4: ( COMMA over_elements )*
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==COMMA) ) {
                    int LA64_2 = input.LA(2);

                    if ( (LA64_2==ID) ) {
                        int LA64_3 = input.LA(3);

                        if ( (LA64_3==COLON) ) {
                            alt64=1;
                        }


                    }


                }


                switch (alt64) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:952:5: COMMA over_elements
            	    {
            	    COMMA174=(Token)match(input,COMMA,FOLLOW_COMMA_in_over_clause2932); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA174, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_over_elements_in_over_clause2937);
            	    over_elements175=over_elements();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, over_elements175.getTree());

            	    }
            	    break;

            	default :
            	    break loop64;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "over_clause"

    public static class over_elements_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "over_elements"
    // src/main/resources/org/drools/lang/DRL5x.g:955:1: over_elements : id1= ID COLON id2= ID paren_chunk -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk ) ;
    public final DRL5xParser.over_elements_return over_elements() throws RecognitionException {
        DRL5xParser.over_elements_return retval = new DRL5xParser.over_elements_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token id2=null;
        Token COLON176=null;
        DRL5xParser.paren_chunk_return paren_chunk177 = null;


        Object id1_tree=null;
        Object id2_tree=null;
        Object COLON176_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:956:2: (id1= ID COLON id2= ID paren_chunk -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL5x.g:956:4: id1= ID COLON id2= ID paren_chunk
            {
            id1=(Token)match(input,ID,FOLLOW_ID_in_over_elements2952); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            if ( state.backtracking==0 ) {
              	emit(id1, DroolsEditorType.IDENTIFIER);	
            }
            COLON176=(Token)match(input,COLON,FOLLOW_COLON_in_over_elements2959); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON176);

            if ( state.backtracking==0 ) {
              	emit(COLON176, DroolsEditorType.SYMBOL);	
            }
            id2=(Token)match(input,ID,FOLLOW_ID_in_over_elements2968); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            if ( state.backtracking==0 ) {
              	emit(id2, DroolsEditorType.IDENTIFIER);	
            }
            pushFollow(FOLLOW_paren_chunk_in_over_elements2975);
            paren_chunk177=paren_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk177.getTree());


            // AST REWRITE
            // elements: paren_chunk, id2, id1
            // token labels: id2, id1
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_id2=new RewriteRuleTokenStream(adaptor,"token id2",id2);
            RewriteRuleTokenStream stream_id1=new RewriteRuleTokenStream(adaptor,"token id1",id1);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 960:2: -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:960:5: ^( VT_BEHAVIOR $id1 $id2 paren_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_BEHAVIOR, "VT_BEHAVIOR"), root_1);

                adaptor.addChild(root_1, stream_id1.nextNode());
                adaptor.addChild(root_1, stream_id2.nextNode());
                adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "over_elements"

    public static class accumulate_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "accumulate_statement"
    // src/main/resources/org/drools/lang/DRL5x.g:963:1: accumulate_statement : ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) ;
    public final DRL5xParser.accumulate_statement_return accumulate_statement() throws RecognitionException {
        DRL5xParser.accumulate_statement_return retval = new DRL5xParser.accumulate_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ACCUMULATE178=null;
        Token LEFT_PAREN179=null;
        Token COMMA181=null;
        Token RIGHT_PAREN184=null;
        DRL5xParser.lhs_or_return lhs_or180 = null;

        DRL5xParser.accumulate_init_clause_return accumulate_init_clause182 = null;

        DRL5xParser.accumulate_id_clause_return accumulate_id_clause183 = null;


        Object ACCUMULATE178_tree=null;
        Object LEFT_PAREN179_tree=null;
        Object COMMA181_tree=null;
        Object RIGHT_PAREN184_tree=null;
        RewriteRuleTokenStream stream_ACCUMULATE=new RewriteRuleTokenStream(adaptor,"token ACCUMULATE");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_accumulate_init_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_init_clause");
        RewriteRuleSubtreeStream stream_accumulate_id_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_id_clause");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:964:2: ( ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) )
            // src/main/resources/org/drools/lang/DRL5x.g:964:4: ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN
            {
            ACCUMULATE178=(Token)match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement3001); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACCUMULATE.add(ACCUMULATE178);

            if ( state.backtracking==0 ) {
              	emit(ACCUMULATE178, DroolsEditorType.KEYWORD);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_ACCUMULATE);	
            }
            LEFT_PAREN179=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement3010); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN179);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN179, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_lhs_or_in_accumulate_statement3018);
            lhs_or180=lhs_or();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or180.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:968:3: ( COMMA )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==COMMA) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:968:3: COMMA
                    {
                    COMMA181=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement3023); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COMMA.add(COMMA181);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(COMMA181, DroolsEditorType.SYMBOL);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:969:3: ( accumulate_init_clause | accumulate_id_clause )
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==ID) ) {
                int LA66_1 = input.LA(2);

                if ( (((validateIdentifierKey(DroolsSoftKeywords.INIT)))) ) {
                    alt66=1;
                }
                else if ( (true) ) {
                    alt66=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 66, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:969:5: accumulate_init_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_statement3033);
                    accumulate_init_clause182=accumulate_init_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accumulate_init_clause.add(accumulate_init_clause182.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:970:5: accumulate_id_clause
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_statement3039);
                    accumulate_id_clause183=accumulate_id_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accumulate_id_clause.add(accumulate_id_clause183.getTree());

                    }
                    break;

            }

            RIGHT_PAREN184=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement3047); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN184);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN184, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }


            // AST REWRITE
            // elements: accumulate_id_clause, accumulate_init_clause, lhs_or, RIGHT_PAREN, ACCUMULATE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 974:3: -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:974:6: ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ACCUMULATE.nextNode(), root_1);

                adaptor.addChild(root_1, stream_lhs_or.nextTree());
                // src/main/resources/org/drools/lang/DRL5x.g:974:26: ( accumulate_init_clause )?
                if ( stream_accumulate_init_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_accumulate_init_clause.nextTree());

                }
                stream_accumulate_init_clause.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:974:50: ( accumulate_id_clause )?
                if ( stream_accumulate_id_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_accumulate_id_clause.nextTree());

                }
                stream_accumulate_id_clause.reset();
                adaptor.addChild(root_1, stream_RIGHT_PAREN.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "accumulate_statement"

    public static class accumulate_init_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "accumulate_init_clause"
    // src/main/resources/org/drools/lang/DRL5x.g:978:1: accumulate_init_clause : init_key pc1= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] (cm1= COMMA )? action_key pc2= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] (cm2= COMMA )? ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )? res1= result_key pc4= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE] -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) ;
    public final DRL5xParser.accumulate_init_clause_return accumulate_init_clause() throws RecognitionException {
        DRL5xParser.accumulate_init_clause_return retval = new DRL5xParser.accumulate_init_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token cm1=null;
        Token cm2=null;
        Token cm3=null;
        DRL5xParser.accumulate_paren_chunk_return pc1 = null;

        DRL5xParser.accumulate_paren_chunk_return pc2 = null;

        DRL5xParser.accumulate_paren_chunk_return pc3 = null;

        DRL5xParser.result_key_return res1 = null;

        DRL5xParser.accumulate_paren_chunk_return pc4 = null;

        DRL5xParser.init_key_return init_key185 = null;

        DRL5xParser.action_key_return action_key186 = null;

        DRL5xParser.reverse_key_return reverse_key187 = null;


        Object cm1_tree=null;
        Object cm2_tree=null;
        Object cm3_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_action_key=new RewriteRuleSubtreeStream(adaptor,"rule action_key");
        RewriteRuleSubtreeStream stream_accumulate_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_paren_chunk");
        RewriteRuleSubtreeStream stream_reverse_key=new RewriteRuleSubtreeStream(adaptor,"rule reverse_key");
        RewriteRuleSubtreeStream stream_result_key=new RewriteRuleSubtreeStream(adaptor,"rule result_key");
        RewriteRuleSubtreeStream stream_init_key=new RewriteRuleSubtreeStream(adaptor,"rule init_key");
         boolean isFailed = true;	
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:981:2: ( init_key pc1= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] (cm1= COMMA )? action_key pc2= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] (cm2= COMMA )? ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )? res1= result_key pc4= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE] -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) )
            // src/main/resources/org/drools/lang/DRL5x.g:981:4: init_key pc1= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] (cm1= COMMA )? action_key pc2= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] (cm2= COMMA )? ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )? res1= result_key pc4= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE]
            {
            pushFollow(FOLLOW_init_key_in_accumulate_init_clause3093);
            init_key185=init_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_init_key.add(init_key185.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT);	
            }
            pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3103);
            pc1=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc1.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:983:84: (cm1= COMMA )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==COMMA) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:983:84: cm1= COMMA
                    {
                    cm1=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause3108); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COMMA.add(cm1);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(cm1, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	if (pc1 != null && ((DroolsTree) pc1.getTree()).getText() != null) emit(Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION);	
            }
            pushFollow(FOLLOW_action_key_in_accumulate_init_clause3119);
            action_key186=action_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_action_key.add(action_key186.getTree());
            pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3123);
            pc2=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc2.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:985:97: (cm2= COMMA )?
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==COMMA) ) {
                alt68=1;
            }
            switch (alt68) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:985:97: cm2= COMMA
                    {
                    cm2=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause3128); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COMMA.add(cm2);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(cm2, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	if (pc1 != null && ((DroolsTree) pc1.getTree()).getText() != null && pc2 != null && ((DroolsTree) pc2.getTree()).getText() != null ) emit(Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:987:2: ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )?
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==ID) ) {
                int LA70_1 = input.LA(2);

                if ( (((validateIdentifierKey(DroolsSoftKeywords.REVERSE)))) ) {
                    alt70=1;
                }
            }
            switch (alt70) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:987:4: reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )?
                    {
                    pushFollow(FOLLOW_reverse_key_in_accumulate_init_clause3140);
                    reverse_key187=reverse_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_reverse_key.add(reverse_key187.getTree());
                    pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3144);
                    pc3=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc3.getTree());
                    // src/main/resources/org/drools/lang/DRL5x.g:987:100: (cm3= COMMA )?
                    int alt69=2;
                    int LA69_0 = input.LA(1);

                    if ( (LA69_0==COMMA) ) {
                        alt69=1;
                    }
                    switch (alt69) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRL5x.g:987:100: cm3= COMMA
                            {
                            cm3=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause3149); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_COMMA.add(cm3);


                            }
                            break;

                    }

                    if ( state.backtracking==0 ) {
                      	emit(cm3, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	if ((pc1 != null && ((DroolsTree) pc1.tree).getText() != null) &&
                          			(pc2 != null && ((DroolsTree) pc2.tree).getText() != null) &&
                          			(pc3 != null && ((DroolsTree) pc3.tree).getText() != null)) {
              			emit(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT);
              		}	
              	
            }
            pushFollow(FOLLOW_result_key_in_accumulate_init_clause3165);
            res1=result_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_result_key.add(res1.getTree());
            if ( state.backtracking==0 ) {
              	emit((res1!=null?((Token)res1.start):null), DroolsEditorType.KEYWORD);	
            }
            pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3171);
            pc4=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc4.getTree());


            // AST REWRITE
            // elements: pc2, result_key, action_key, pc4, pc3, reverse_key, pc1, init_key
            // token labels: 
            // rule labels: pc4, pc3, retval, pc1, pc2
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_pc4=new RewriteRuleSubtreeStream(adaptor,"rule pc4",pc4!=null?pc4.tree:null);
            RewriteRuleSubtreeStream stream_pc3=new RewriteRuleSubtreeStream(adaptor,"rule pc3",pc3!=null?pc3.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_pc1=new RewriteRuleSubtreeStream(adaptor,"rule pc1",pc1!=null?pc1.tree:null);
            RewriteRuleSubtreeStream stream_pc2=new RewriteRuleSubtreeStream(adaptor,"rule pc2",pc2!=null?pc2.tree:null);

            root_0 = (Object)adaptor.nil();
            // 996:2: -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:996:5: ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ACCUMULATE_INIT_CLAUSE, "VT_ACCUMULATE_INIT_CLAUSE"), root_1);

                // src/main/resources/org/drools/lang/DRL5x.g:996:33: ^( init_key $pc1)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_init_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc1.nextTree());

                adaptor.addChild(root_1, root_2);
                }
                // src/main/resources/org/drools/lang/DRL5x.g:996:50: ^( action_key $pc2)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_action_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc2.nextTree());

                adaptor.addChild(root_1, root_2);
                }
                // src/main/resources/org/drools/lang/DRL5x.g:996:69: ( ^( reverse_key $pc3) )?
                if ( stream_pc3.hasNext()||stream_reverse_key.hasNext() ) {
                    // src/main/resources/org/drools/lang/DRL5x.g:996:69: ^( reverse_key $pc3)
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_reverse_key.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_pc3.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_pc3.reset();
                stream_reverse_key.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:996:90: ^( result_key $pc4)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_result_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc4.nextTree());

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               isFailed = false;	
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
             
            	if (isEditorInterfaceEnabled && isFailed && input.LA(1) == ID && validateLT(1, DroolsSoftKeywords.RESULT)) {
            		emit(input.LT(1), DroolsEditorType.KEYWORD);
            		input.consume();
            		if (input.LA(1) == LEFT_PAREN){
            			input.consume();
            			emit(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE);
            		}
            	}

        }
        return retval;
    }
    // $ANTLR end "accumulate_init_clause"

    public static class accumulate_paren_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "accumulate_paren_chunk"
    // src/main/resources/org/drools/lang/DRL5x.g:1009:1: accumulate_paren_chunk[int locationType] : pc= accumulate_paren_chunk_data[false,$locationType] -> VT_PAREN_CHUNK[$pc.start,text] ;
    public final DRL5xParser.accumulate_paren_chunk_return accumulate_paren_chunk(int locationType) throws RecognitionException {
        DRL5xParser.accumulate_paren_chunk_return retval = new DRL5xParser.accumulate_paren_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.accumulate_paren_chunk_data_return pc = null;


        RewriteRuleSubtreeStream stream_accumulate_paren_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_paren_chunk_data");

        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1012:3: (pc= accumulate_paren_chunk_data[false,$locationType] -> VT_PAREN_CHUNK[$pc.start,text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1012:5: pc= accumulate_paren_chunk_data[false,$locationType]
            {
            pushFollow(FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk3229);
            pc=accumulate_paren_chunk_data(false, locationType);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accumulate_paren_chunk_data.add(pc.getTree());
            if ( state.backtracking==0 ) {
              text = (pc!=null?input.toString(pc.start,pc.stop):null);
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1013:2: -> VT_PAREN_CHUNK[$pc.start,text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_PAREN_CHUNK, (pc!=null?((Token)pc.start):null), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "accumulate_paren_chunk"

    public static class accumulate_paren_chunk_data_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "accumulate_paren_chunk_data"
    // src/main/resources/org/drools/lang/DRL5x.g:1016:1: accumulate_paren_chunk_data[boolean isRecursive, int locationType] : lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )* rp1= RIGHT_PAREN ;
    public final DRL5xParser.accumulate_paren_chunk_data_return accumulate_paren_chunk_data(boolean isRecursive, int locationType) throws RecognitionException {
        DRL5xParser.accumulate_paren_chunk_data_return retval = new DRL5xParser.accumulate_paren_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lp1=null;
        Token any=null;
        Token rp1=null;
        DRL5xParser.accumulate_paren_chunk_data_return accumulate_paren_chunk_data188 = null;


        Object lp1_tree=null;
        Object any_tree=null;
        Object rp1_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1017:2: (lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )* rp1= RIGHT_PAREN )
            // src/main/resources/org/drools/lang/DRL5x.g:1017:4: lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )* rp1= RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            lp1=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_paren_chunk_data3253); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            lp1_tree = (Object)adaptor.create(lp1);
            adaptor.addChild(root_0, lp1_tree);
            }
            if ( state.backtracking==0 ) {
              	if (!isRecursive) {
              				emit(lp1, DroolsEditorType.SYMBOL);
              				emit(locationType);
              			} else {
              				emit(lp1, DroolsEditorType.CODE_CHUNK);
              			}	
              		
            }
            // src/main/resources/org/drools/lang/DRL5x.g:1025:4: (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )*
            loop71:
            do {
                int alt71=3;
                int LA71_0 = input.LA(1);

                if ( ((LA71_0>=VT_COMPILATION_UNIT && LA71_0<=STRING)||LA71_0==COMMA||(LA71_0>=AT && LA71_0<=IdentifierPart)) ) {
                    alt71=1;
                }
                else if ( (LA71_0==LEFT_PAREN) ) {
                    alt71=2;
                }


                switch (alt71) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1025:5: any=~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=STRING)||input.LA(1)==COMMA||(input.LA(1)>=AT && input.LA(1)<=IdentifierPart) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(any));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    if ( state.backtracking==0 ) {
            	       emit(any, DroolsEditorType.CODE_CHUNK); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1025:87: accumulate_paren_chunk_data[true,-1]
            	    {
            	    pushFollow(FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk_data3281);
            	    accumulate_paren_chunk_data188=accumulate_paren_chunk_data(true, -1);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, accumulate_paren_chunk_data188.getTree());

            	    }
            	    break;

            	default :
            	    break loop71;
                }
            } while (true);

            rp1=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_paren_chunk_data3292); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            rp1_tree = (Object)adaptor.create(rp1);
            adaptor.addChild(root_0, rp1_tree);
            }
            if ( state.backtracking==0 ) {
              	if (!isRecursive) {
              				emit(rp1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(rp1, DroolsEditorType.CODE_CHUNK);
              			}	
              		
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "accumulate_paren_chunk_data"

    public static class accumulate_id_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "accumulate_id_clause"
    // src/main/resources/org/drools/lang/DRL5x.g:1035:1: accumulate_id_clause : ID paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) ;
    public final DRL5xParser.accumulate_id_clause_return accumulate_id_clause() throws RecognitionException {
        DRL5xParser.accumulate_id_clause_return retval = new DRL5xParser.accumulate_id_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID189=null;
        DRL5xParser.paren_chunk_return paren_chunk190 = null;


        Object ID189_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1036:2: ( ID paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1036:4: ID paren_chunk
            {
            ID189=(Token)match(input,ID,FOLLOW_ID_in_accumulate_id_clause3308); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID189);

            if ( state.backtracking==0 ) {
              	emit(ID189, DroolsEditorType.IDENTIFIER);	
            }
            pushFollow(FOLLOW_paren_chunk_in_accumulate_id_clause3314);
            paren_chunk190=paren_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk190.getTree());


            // AST REWRITE
            // elements: paren_chunk, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1038:2: -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:1038:5: ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ACCUMULATE_ID_CLAUSE, "VT_ACCUMULATE_ID_CLAUSE"), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "accumulate_id_clause"

    public static class collect_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "collect_statement"
    // src/main/resources/org/drools/lang/DRL5x.g:1041:1: collect_statement : COLLECT LEFT_PAREN pattern_source RIGHT_PAREN -> ^( COLLECT pattern_source RIGHT_PAREN ) ;
    public final DRL5xParser.collect_statement_return collect_statement() throws RecognitionException {
        DRL5xParser.collect_statement_return retval = new DRL5xParser.collect_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLLECT191=null;
        Token LEFT_PAREN192=null;
        Token RIGHT_PAREN194=null;
        DRL5xParser.pattern_source_return pattern_source193 = null;


        Object COLLECT191_tree=null;
        Object LEFT_PAREN192_tree=null;
        Object RIGHT_PAREN194_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleTokenStream stream_COLLECT=new RewriteRuleTokenStream(adaptor,"token COLLECT");
        RewriteRuleSubtreeStream stream_pattern_source=new RewriteRuleSubtreeStream(adaptor,"rule pattern_source");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1042:2: ( COLLECT LEFT_PAREN pattern_source RIGHT_PAREN -> ^( COLLECT pattern_source RIGHT_PAREN ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1042:4: COLLECT LEFT_PAREN pattern_source RIGHT_PAREN
            {
            COLLECT191=(Token)match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement3336); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLLECT.add(COLLECT191);

            if ( state.backtracking==0 ) {
              	emit(COLLECT191, DroolsEditorType.KEYWORD);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_COLLECT);	
            }
            LEFT_PAREN192=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement3345); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN192);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN192, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_pattern_source_in_collect_statement3352);
            pattern_source193=pattern_source();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_pattern_source.add(pattern_source193.getTree());
            RIGHT_PAREN194=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement3357); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN194);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN194, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }


            // AST REWRITE
            // elements: RIGHT_PAREN, pattern_source, COLLECT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1048:2: -> ^( COLLECT pattern_source RIGHT_PAREN )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:1048:5: ^( COLLECT pattern_source RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_COLLECT.nextNode(), root_1);

                adaptor.addChild(root_1, stream_pattern_source.nextTree());
                adaptor.addChild(root_1, stream_RIGHT_PAREN.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "collect_statement"

    public static class entrypoint_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entrypoint_statement"
    // src/main/resources/org/drools/lang/DRL5x.g:1051:1: entrypoint_statement : entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) ;
    public final DRL5xParser.entrypoint_statement_return entrypoint_statement() throws RecognitionException {
        DRL5xParser.entrypoint_statement_return retval = new DRL5xParser.entrypoint_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.entry_point_key_return entry_point_key195 = null;

        DRL5xParser.entrypoint_id_return entrypoint_id196 = null;


        RewriteRuleSubtreeStream stream_entrypoint_id=new RewriteRuleSubtreeStream(adaptor,"rule entrypoint_id");
        RewriteRuleSubtreeStream stream_entry_point_key=new RewriteRuleSubtreeStream(adaptor,"rule entry_point_key");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1052:2: ( entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1052:4: entry_point_key entrypoint_id
            {
            pushFollow(FOLLOW_entry_point_key_in_entrypoint_statement3384);
            entry_point_key195=entry_point_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_entry_point_key.add(entry_point_key195.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_COLLECT);	
            }
            pushFollow(FOLLOW_entrypoint_id_in_entrypoint_statement3392);
            entrypoint_id196=entrypoint_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_entrypoint_id.add(entrypoint_id196.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }


            // AST REWRITE
            // elements: entry_point_key, entrypoint_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1056:2: -> ^( entry_point_key entrypoint_id )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:1056:5: ^( entry_point_key entrypoint_id )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_entry_point_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_entrypoint_id.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "entrypoint_statement"

    public static class entrypoint_id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entrypoint_id"
    // src/main/resources/org/drools/lang/DRL5x.g:1059:1: entrypoint_id : (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] );
    public final DRL5xParser.entrypoint_id_return entrypoint_id() throws RecognitionException {
        DRL5xParser.entrypoint_id_return retval = new DRL5xParser.entrypoint_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1060:2: (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==ID) ) {
                alt72=1;
            }
            else if ( (LA72_0==STRING) ) {
                alt72=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1060:5: value= ID
                    {
                    value=(Token)match(input,ID,FOLLOW_ID_in_entrypoint_id3418); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(value);

                    if ( state.backtracking==0 ) {
                      	emit(value, DroolsEditorType.IDENTIFIER);	
                    }


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1061:3: -> VT_ENTRYPOINT_ID[$value]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_ENTRYPOINT_ID, value));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1062:5: value= STRING
                    {
                    value=(Token)match(input,STRING,FOLLOW_STRING_in_entrypoint_id3435); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING.add(value);

                    if ( state.backtracking==0 ) {
                      	emit(value, DroolsEditorType.IDENTIFIER);	
                    }


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1063:3: -> VT_ENTRYPOINT_ID[$value]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_ENTRYPOINT_ID, value));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "entrypoint_id"

    public static class from_source_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "from_source"
    // src/main/resources/org/drools/lang/DRL5x.g:1066:1: from_source : ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) ;
    public final DRL5xParser.from_source_return from_source() throws RecognitionException {
        DRL5xParser.from_source_return retval = new DRL5xParser.from_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID197=null;
        DRL5xParser.paren_chunk_return args = null;

        DRL5xParser.expression_chain_return expression_chain198 = null;


        Object ID197_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1067:2: ( ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1067:4: ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )?
            {
            ID197=(Token)match(input,ID,FOLLOW_ID_in_from_source3455); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID197);

            if ( state.backtracking==0 ) {
              	emit(ID197, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:1068:3: ( ( LEFT_PAREN )=>args= paren_chunk )?
            int alt73=2;
            alt73 = dfa73.predict(input);
            switch (alt73) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1068:5: ( LEFT_PAREN )=>args= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source3470);
                    args=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_paren_chunk.add(args.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL5x.g:1069:3: ( expression_chain )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==DOT) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1069:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source3477);
                    expression_chain198=expression_chain();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression_chain.add(expression_chain198.getTree());

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	if ( input.LA(1) == EOF && input.get(input.index() - 1).getType() == WS) {
              			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);
              		} else if ( input.LA(1) != EOF ) {
              			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);
              		}	
            }


            // AST REWRITE
            // elements: ID, expression_chain, paren_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1075:2: -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:1075:5: ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FROM_SOURCE, "VT_FROM_SOURCE"), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // src/main/resources/org/drools/lang/DRL5x.g:1075:25: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                }
                stream_paren_chunk.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:1075:38: ( expression_chain )?
                if ( stream_expression_chain.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression_chain.nextTree());

                }
                stream_expression_chain.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "from_source"

    public static class expression_chain_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression_chain"
    // src/main/resources/org/drools/lang/DRL5x.g:1078:1: expression_chain : DOT ID ({...}? paren_chunk | square_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) ;
    public final DRL5xParser.expression_chain_return expression_chain() throws RecognitionException {
        DRL5xParser.expression_chain_return retval = new DRL5xParser.expression_chain_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT199=null;
        Token ID200=null;
        DRL5xParser.paren_chunk_return paren_chunk201 = null;

        DRL5xParser.square_chunk_return square_chunk202 = null;

        DRL5xParser.expression_chain_return expression_chain203 = null;


        Object DOT199_tree=null;
        Object ID200_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1079:2: ( DOT ID ({...}? paren_chunk | square_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1080:3: DOT ID ({...}? paren_chunk | square_chunk )? ( expression_chain )?
            {
            DOT199=(Token)match(input,DOT,FOLLOW_DOT_in_expression_chain3510); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_DOT.add(DOT199);

            if ( state.backtracking==0 ) {
              	emit(DOT199, DroolsEditorType.IDENTIFIER);	
            }
            ID200=(Token)match(input,ID,FOLLOW_ID_in_expression_chain3517); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID200);

            if ( state.backtracking==0 ) {
              	emit(ID200, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:1082:4: ({...}? paren_chunk | square_chunk )?
            int alt75=3;
            alt75 = dfa75.predict(input);
            switch (alt75) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1083:6: {...}? paren_chunk
                    {
                    if ( !((input.LA(1) == LEFT_PAREN)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "expression_chain", "input.LA(1) == LEFT_PAREN");
                    }
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain3533);
                    paren_chunk201=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk201.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1085:6: square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain3547);
                    square_chunk202=square_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_square_chunk.add(square_chunk202.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL5x.g:1087:4: ( expression_chain )?
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==DOT) ) {
                alt76=1;
            }
            switch (alt76) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1087:4: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain3558);
                    expression_chain203=expression_chain();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression_chain.add(expression_chain203.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: square_chunk, expression_chain, paren_chunk, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1088:4: -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:1088:7: ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_EXPRESSION_CHAIN, DOT199), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // src/main/resources/org/drools/lang/DRL5x.g:1088:38: ( square_chunk )?
                if ( stream_square_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_square_chunk.nextTree());

                }
                stream_square_chunk.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:1088:52: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                }
                stream_paren_chunk.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:1088:65: ( expression_chain )?
                if ( stream_expression_chain.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression_chain.nextTree());

                }
                stream_expression_chain.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expression_chain"

    public static class lhs_pattern_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lhs_pattern"
    // src/main/resources/org/drools/lang/DRL5x.g:1091:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );
    public final DRL5xParser.lhs_pattern_return lhs_pattern() throws RecognitionException {
        DRL5xParser.lhs_pattern_return retval = new DRL5xParser.lhs_pattern_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.fact_binding_return fact_binding204 = null;

        DRL5xParser.fact_return fact205 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1092:2: ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) )
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==ID) ) {
                int LA77_1 = input.LA(2);

                if ( (LA77_1==COLON) ) {
                    alt77=1;
                }
                else if ( (LA77_1==DOT||LA77_1==LEFT_PAREN||LA77_1==LEFT_SQUARE) ) {
                    alt77=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 77, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1092:4: fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern3591);
                    fact_binding204=fact_binding();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fact_binding.add(fact_binding204.getTree());


                    // AST REWRITE
                    // elements: fact_binding
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1092:17: -> ^( VT_PATTERN fact_binding )
                    {
                        // src/main/resources/org/drools/lang/DRL5x.g:1092:20: ^( VT_PATTERN fact_binding )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_PATTERN, "VT_PATTERN"), root_1);

                        adaptor.addChild(root_1, stream_fact_binding.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1093:4: fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern3604);
                    fact205=fact();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fact.add(fact205.getTree());


                    // AST REWRITE
                    // elements: fact
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1093:9: -> ^( VT_PATTERN fact )
                    {
                        // src/main/resources/org/drools/lang/DRL5x.g:1093:12: ^( VT_PATTERN fact )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_PATTERN, "VT_PATTERN"), root_1);

                        adaptor.addChild(root_1, stream_fact.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lhs_pattern"

    public static class fact_binding_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fact_binding"
    // src/main/resources/org/drools/lang/DRL5x.g:1096:1: fact_binding : label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) ;
    public final DRL5xParser.fact_binding_return fact_binding() throws RecognitionException {
        DRL5xParser.fact_binding_return retval = new DRL5xParser.fact_binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN208=null;
        Token RIGHT_PAREN210=null;
        DRL5xParser.label_return label206 = null;

        DRL5xParser.fact_return fact207 = null;

        DRL5xParser.fact_binding_expression_return fact_binding_expression209 = null;


        Object LEFT_PAREN208_tree=null;
        Object RIGHT_PAREN210_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        RewriteRuleSubtreeStream stream_fact_binding_expression=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding_expression");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1097:3: ( label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1097:5: label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            {
            pushFollow(FOLLOW_label_in_fact_binding3624);
            label206=label();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_label.add(label206.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:1098:3: ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==ID) ) {
                alt78=1;
            }
            else if ( (LA78_0==LEFT_PAREN) ) {
                alt78=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 78, 0, input);

                throw nvae;
            }
            switch (alt78) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1098:5: fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding3630);
                    fact207=fact();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fact.add(fact207.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1099:6: LEFT_PAREN fact_binding_expression RIGHT_PAREN
                    {
                    LEFT_PAREN208=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding3637); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN208);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN208, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_fact_binding_expression_in_fact_binding3645);
                    fact_binding_expression209=fact_binding_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fact_binding_expression.add(fact_binding_expression209.getTree());
                    RIGHT_PAREN210=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding3653); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN210);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN210, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }



            // AST REWRITE
            // elements: fact, fact_binding_expression, RIGHT_PAREN, label
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1103:3: -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:1103:6: ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FACT_BINDING, "VT_FACT_BINDING"), root_1);

                adaptor.addChild(root_1, stream_label.nextTree());
                // src/main/resources/org/drools/lang/DRL5x.g:1103:30: ( fact )?
                if ( stream_fact.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact.nextTree());

                }
                stream_fact.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:1103:36: ( fact_binding_expression )?
                if ( stream_fact_binding_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact_binding_expression.nextTree());

                }
                stream_fact_binding_expression.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:1103:61: ( RIGHT_PAREN )?
                if ( stream_RIGHT_PAREN.hasNext() ) {
                    adaptor.addChild(root_1, stream_RIGHT_PAREN.nextNode());

                }
                stream_RIGHT_PAREN.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "fact_binding"

    public static class fact_binding_expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fact_binding_expression"
    // src/main/resources/org/drools/lang/DRL5x.g:1106:1: fact_binding_expression : ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* ;
    public final DRL5xParser.fact_binding_expression_return fact_binding_expression() throws RecognitionException {
        DRL5xParser.fact_binding_expression_return retval = new DRL5xParser.fact_binding_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        DRL5xParser.or_key_return value = null;

        DRL5xParser.fact_return fact211 = null;

        DRL5xParser.fact_return fact212 = null;


        Object pipe_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");

        	Token orToken = null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1109:3: ( ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* )
            // src/main/resources/org/drools/lang/DRL5x.g:1109:5: ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            {
            // src/main/resources/org/drools/lang/DRL5x.g:1109:5: ( fact -> fact )
            // src/main/resources/org/drools/lang/DRL5x.g:1109:6: fact
            {
            pushFollow(FOLLOW_fact_in_fact_binding_expression3694);
            fact211=fact();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_fact.add(fact211.getTree());


            // AST REWRITE
            // elements: fact
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1109:11: -> fact
            {
                adaptor.addChild(root_0, stream_fact.nextTree());

            }

            retval.tree = root_0;}
            }

            // src/main/resources/org/drools/lang/DRL5x.g:1109:20: ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            loop80:
            do {
                int alt80=2;
                int LA80_0 = input.LA(1);

                if ( (LA80_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                    alt80=1;
                }
                else if ( (LA80_0==DOUBLE_PIPE) ) {
                    alt80=1;
                }


                switch (alt80) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1109:22: (value= or_key | pipe= DOUBLE_PIPE ) fact
            	    {
            	    // src/main/resources/org/drools/lang/DRL5x.g:1109:22: (value= or_key | pipe= DOUBLE_PIPE )
            	    int alt79=2;
            	    int LA79_0 = input.LA(1);

            	    if ( (LA79_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            	        alt79=1;
            	    }
            	    else if ( (LA79_0==DOUBLE_PIPE) ) {
            	        alt79=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 79, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt79) {
            	        case 1 :
            	            // src/main/resources/org/drools/lang/DRL5x.g:1109:23: value= or_key
            	            {
            	            pushFollow(FOLLOW_or_key_in_fact_binding_expression3706);
            	            value=or_key();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) stream_or_key.add(value.getTree());
            	            if ( state.backtracking==0 ) {
            	              orToken = (value!=null?((Token)value.start):null);
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // src/main/resources/org/drools/lang/DRL5x.g:1109:62: pipe= DOUBLE_PIPE
            	            {
            	            pipe=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3712); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

            	            if ( state.backtracking==0 ) {
            	              orToken = pipe;
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_fact_in_fact_binding_expression3717);
            	    fact212=fact();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_fact.add(fact212.getTree());


            	    // AST REWRITE
            	    // elements: fact, fact_binding_expression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 1110:3: -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
            	    {
            	        // src/main/resources/org/drools/lang/DRL5x.g:1110:6: ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
            	        {
            	        Object root_1 = (Object)adaptor.nil();
            	        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FACT_OR, orToken), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_fact.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop80;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "fact_binding_expression"

    public static class fact_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fact"
    // src/main/resources/org/drools/lang/DRL5x.g:1113:1: fact : pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) ;
    public final DRL5xParser.fact_return fact() throws RecognitionException {
        DRL5xParser.fact_return retval = new DRL5xParser.fact_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN214=null;
        Token RIGHT_PAREN216=null;
        DRL5xParser.pattern_type_return pattern_type213 = null;

        DRL5xParser.constraints_return constraints215 = null;


        Object LEFT_PAREN214_tree=null;
        Object RIGHT_PAREN216_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_constraints=new RewriteRuleSubtreeStream(adaptor,"rule constraints");
        RewriteRuleSubtreeStream stream_pattern_type=new RewriteRuleSubtreeStream(adaptor,"rule pattern_type");
         boolean isFailedOnConstraints = true; pushParaphrases(DroolsParaphraseTypes.PATTERN); 
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1116:2: ( pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1116:4: pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN
            {
            pushFollow(FOLLOW_pattern_type_in_fact3757);
            pattern_type213=pattern_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_pattern_type.add(pattern_type213.getTree());
            LEFT_PAREN214=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact3762); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN214);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN214, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:1119:4: ( constraints )?
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( (LA81_0==ID||LA81_0==LEFT_PAREN) ) {
                alt81=1;
            }
            switch (alt81) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1119:4: constraints
                    {
                    pushFollow(FOLLOW_constraints_in_fact3773);
                    constraints215=constraints();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_constraints.add(constraints215.getTree());

                    }
                    break;

            }

            RIGHT_PAREN216=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact3779); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN216);

            if ( state.backtracking==0 ) {
              	isFailedOnConstraints = false;	
            }
            if ( state.backtracking==0 ) {
              	if ((RIGHT_PAREN216!=null?RIGHT_PAREN216.getText():null).equals(")") ){ //WORKAROUND FOR ANTLR BUG!
              			emit(RIGHT_PAREN216, DroolsEditorType.SYMBOL);
              			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);
              		}	
            }


            // AST REWRITE
            // elements: pattern_type, RIGHT_PAREN, constraints
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1125:2: -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:1125:5: ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FACT, "VT_FACT"), root_1);

                adaptor.addChild(root_1, stream_pattern_type.nextTree());
                // src/main/resources/org/drools/lang/DRL5x.g:1125:28: ( constraints )?
                if ( stream_constraints.hasNext() ) {
                    adaptor.addChild(root_1, stream_constraints.nextTree());

                }
                stream_constraints.reset();
                adaptor.addChild(root_1, stream_RIGHT_PAREN.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop();	
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {

            	if (isEditorInterfaceEnabled && isFailedOnConstraints && input.LA(1) == EOF && input.get(input.index() - 1).getType() == WS){
            		if (!(getActiveSentence().getContent().getLast() instanceof Integer) && input.LA(-1) != COLON) {
            			emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            		}
            	}

        }
        return retval;
    }
    // $ANTLR end "fact"

    public static class constraints_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constraints"
    // src/main/resources/org/drools/lang/DRL5x.g:1135:1: constraints : constraint ( COMMA constraint )* ;
    public final DRL5xParser.constraints_return constraints() throws RecognitionException {
        DRL5xParser.constraints_return retval = new DRL5xParser.constraints_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA218=null;
        DRL5xParser.constraint_return constraint217 = null;

        DRL5xParser.constraint_return constraint219 = null;


        Object COMMA218_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1136:2: ( constraint ( COMMA constraint )* )
            // src/main/resources/org/drools/lang/DRL5x.g:1136:4: constraint ( COMMA constraint )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_in_constraints3813);
            constraint217=constraint();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint217.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:1136:15: ( COMMA constraint )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);

                if ( (LA82_0==COMMA) ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1136:17: COMMA constraint
            	    {
            	    COMMA218=(Token)match(input,COMMA,FOLLOW_COMMA_in_constraints3817); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA218, DroolsEditorType.SYMBOL);
            	      		emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	
            	    }
            	    pushFollow(FOLLOW_constraint_in_constraints3824);
            	    constraint219=constraint();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint219.getTree());

            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "constraints"

    public static class constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constraint"
    // src/main/resources/org/drools/lang/DRL5x.g:1141:1: constraint : or_constr ;
    public final DRL5xParser.constraint_return constraint() throws RecognitionException {
        DRL5xParser.constraint_return retval = new DRL5xParser.constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.or_constr_return or_constr220 = null;



        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1142:2: ( or_constr )
            // src/main/resources/org/drools/lang/DRL5x.g:1142:4: or_constr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_or_constr_in_constraint3838);
            or_constr220=or_constr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, or_constr220.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "constraint"

    public static class or_constr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "or_constr"
    // src/main/resources/org/drools/lang/DRL5x.g:1145:1: or_constr : and_constr ( DOUBLE_PIPE and_constr )* ;
    public final DRL5xParser.or_constr_return or_constr() throws RecognitionException {
        DRL5xParser.or_constr_return retval = new DRL5xParser.or_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE222=null;
        DRL5xParser.and_constr_return and_constr221 = null;

        DRL5xParser.and_constr_return and_constr223 = null;


        Object DOUBLE_PIPE222_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1146:2: ( and_constr ( DOUBLE_PIPE and_constr )* )
            // src/main/resources/org/drools/lang/DRL5x.g:1146:4: and_constr ( DOUBLE_PIPE and_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_constr_in_or_constr3849);
            and_constr221=and_constr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, and_constr221.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:1146:15: ( DOUBLE_PIPE and_constr )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);

                if ( (LA83_0==DOUBLE_PIPE) ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1146:17: DOUBLE_PIPE and_constr
            	    {
            	    DOUBLE_PIPE222=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr3853); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_PIPE222_tree = (Object)adaptor.create(DOUBLE_PIPE222);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE222_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_PIPE222, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr3860);
            	    and_constr223=and_constr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, and_constr223.getTree());

            	    }
            	    break;

            	default :
            	    break loop83;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "or_constr"

    public static class and_constr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "and_constr"
    // src/main/resources/org/drools/lang/DRL5x.g:1150:1: and_constr : unary_constr ( DOUBLE_AMPER unary_constr )* ;
    public final DRL5xParser.and_constr_return and_constr() throws RecognitionException {
        DRL5xParser.and_constr_return retval = new DRL5xParser.and_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER225=null;
        DRL5xParser.unary_constr_return unary_constr224 = null;

        DRL5xParser.unary_constr_return unary_constr226 = null;


        Object DOUBLE_AMPER225_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1151:2: ( unary_constr ( DOUBLE_AMPER unary_constr )* )
            // src/main/resources/org/drools/lang/DRL5x.g:1151:4: unary_constr ( DOUBLE_AMPER unary_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_constr_in_and_constr3875);
            unary_constr224=unary_constr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unary_constr224.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:1151:17: ( DOUBLE_AMPER unary_constr )*
            loop84:
            do {
                int alt84=2;
                int LA84_0 = input.LA(1);

                if ( (LA84_0==DOUBLE_AMPER) ) {
                    alt84=1;
                }


                switch (alt84) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1151:19: DOUBLE_AMPER unary_constr
            	    {
            	    DOUBLE_AMPER225=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr3879); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_AMPER225_tree = (Object)adaptor.create(DOUBLE_AMPER225);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER225_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_AMPER225, DroolsEditorType.SYMBOL);;	
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr3886);
            	    unary_constr226=unary_constr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unary_constr226.getTree());

            	    }
            	    break;

            	default :
            	    break loop84;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "and_constr"

    public static class unary_constr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unary_constr"
    // src/main/resources/org/drools/lang/DRL5x.g:1155:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );
    public final DRL5xParser.unary_constr_return unary_constr() throws RecognitionException {
        DRL5xParser.unary_constr_return retval = new DRL5xParser.unary_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN230=null;
        Token RIGHT_PAREN232=null;
        DRL5xParser.eval_key_return eval_key227 = null;

        DRL5xParser.paren_chunk_return paren_chunk228 = null;

        DRL5xParser.field_constraint_return field_constraint229 = null;

        DRL5xParser.or_constr_return or_constr231 = null;


        Object LEFT_PAREN230_tree=null;
        Object RIGHT_PAREN232_tree=null;

         boolean isFailed = true;	
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1159:2: ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN )
            int alt85=3;
            alt85 = dfa85.predict(input);
            switch (alt85) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1159:4: eval_key paren_chunk
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_eval_key_in_unary_constr3919);
                    eval_key227=eval_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(eval_key227.getTree(), root_0);
                    pushFollow(FOLLOW_paren_chunk_in_unary_constr3922);
                    paren_chunk228=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk228.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1160:4: field_constraint
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_field_constraint_in_unary_constr3927);
                    field_constraint229=field_constraint();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, field_constraint229.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1161:5: LEFT_PAREN or_constr RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN230=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr3933); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN230, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_or_constr_in_unary_constr3943);
                    or_constr231=or_constr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, or_constr231.getTree());
                    RIGHT_PAREN232=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr3948); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_PAREN232_tree = (Object)adaptor.create(RIGHT_PAREN232);
                    adaptor.addChild(root_0, RIGHT_PAREN232_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN232, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               isFailed = false;	
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
             
            	if (isEditorInterfaceEnabled && isFailed && input.LA(2) == EOF && input.LA(1) == ID) {
            		emit(input.LT(1), DroolsEditorType.IDENTIFIER);
            		input.consume();
            		if (input.get(input.index() - 1).getType() == WS)
            			emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	}

        }
        return retval;
    }
    // $ANTLR end "unary_constr"

    public static class field_constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "field_constraint"
    // src/main/resources/org/drools/lang/DRL5x.g:1174:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );
    public final DRL5xParser.field_constraint_return field_constraint() throws RecognitionException {
        DRL5xParser.field_constraint_return retval = new DRL5xParser.field_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token arw=null;
        DRL5xParser.label_return label233 = null;

        DRL5xParser.accessor_path_return accessor_path234 = null;

        DRL5xParser.or_restr_connective_return or_restr_connective235 = null;

        DRL5xParser.paren_chunk_return paren_chunk236 = null;

        DRL5xParser.accessor_path_return accessor_path237 = null;

        DRL5xParser.or_restr_connective_return or_restr_connective238 = null;


        Object arw_tree=null;
        RewriteRuleTokenStream stream_ARROW=new RewriteRuleTokenStream(adaptor,"token ARROW");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        RewriteRuleSubtreeStream stream_or_restr_connective=new RewriteRuleSubtreeStream(adaptor,"rule or_restr_connective");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        RewriteRuleSubtreeStream stream_accessor_path=new RewriteRuleSubtreeStream(adaptor,"rule accessor_path");

        	boolean isArrow = false;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1177:3: ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) )
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==ID) ) {
                int LA87_1 = input.LA(2);

                if ( (LA87_1==COLON) ) {
                    alt87=1;
                }
                else if ( ((LA87_1>=ID && LA87_1<=DOT)||LA87_1==LEFT_PAREN||(LA87_1>=EQUAL && LA87_1<=NOT_EQUAL)||LA87_1==LEFT_SQUARE) ) {
                    alt87=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 87, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 87, 0, input);

                throw nvae;
            }
            switch (alt87) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1177:5: label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )?
                    {
                    pushFollow(FOLLOW_label_in_field_constraint3968);
                    label233=label();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_label.add(label233.getTree());
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3970);
                    accessor_path234=accessor_path();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accessor_path.add(accessor_path234.getTree());
                    // src/main/resources/org/drools/lang/DRL5x.g:1178:3: ( or_restr_connective | arw= ARROW paren_chunk )?
                    int alt86=3;
                    int LA86_0 = input.LA(1);

                    if ( (LA86_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))||((validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {
                        alt86=1;
                    }
                    else if ( (LA86_0==LEFT_PAREN||(LA86_0>=EQUAL && LA86_0<=NOT_EQUAL)) ) {
                        alt86=1;
                    }
                    else if ( (LA86_0==ARROW) ) {
                        alt86=2;
                    }
                    switch (alt86) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRL5x.g:1178:5: or_restr_connective
                            {
                            pushFollow(FOLLOW_or_restr_connective_in_field_constraint3977);
                            or_restr_connective235=or_restr_connective();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_or_restr_connective.add(or_restr_connective235.getTree());

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DRL5x.g:1178:27: arw= ARROW paren_chunk
                            {
                            arw=(Token)match(input,ARROW,FOLLOW_ARROW_in_field_constraint3983); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ARROW.add(arw);

                            if ( state.backtracking==0 ) {
                              	emit(arw, DroolsEditorType.SYMBOL);	
                            }
                            pushFollow(FOLLOW_paren_chunk_in_field_constraint3987);
                            paren_chunk236=paren_chunk();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk236.getTree());
                            if ( state.backtracking==0 ) {
                              isArrow = true;
                            }

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: paren_chunk, accessor_path, label, accessor_path, or_restr_connective, label
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1179:3: -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )?
                    if (isArrow) {
                        // src/main/resources/org/drools/lang/DRL5x.g:1179:17: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.nextTree());
                        // src/main/resources/org/drools/lang/DRL5x.g:1179:39: ^( VT_FIELD accessor_path )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }
                        // src/main/resources/org/drools/lang/DRL5x.g:1179:66: ( ^( VK_EVAL[$arw] paren_chunk ) )?
                        if ( stream_paren_chunk.hasNext() ) {
                            // src/main/resources/org/drools/lang/DRL5x.g:1179:66: ^( VK_EVAL[$arw] paren_chunk )
                            {
                            Object root_1 = (Object)adaptor.nil();
                            root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VK_EVAL, arw), root_1);

                            adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                            adaptor.addChild(root_0, root_1);
                            }

                        }
                        stream_paren_chunk.reset();

                    }
                    else // 1180:3: -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                    {
                        // src/main/resources/org/drools/lang/DRL5x.g:1180:6: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.nextTree());
                        // src/main/resources/org/drools/lang/DRL5x.g:1180:28: ^( VT_FIELD accessor_path ( or_restr_connective )? )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.nextTree());
                        // src/main/resources/org/drools/lang/DRL5x.g:1180:53: ( or_restr_connective )?
                        if ( stream_or_restr_connective.hasNext() ) {
                            adaptor.addChild(root_2, stream_or_restr_connective.nextTree());

                        }
                        stream_or_restr_connective.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1181:4: accessor_path or_restr_connective
                    {
                    pushFollow(FOLLOW_accessor_path_in_field_constraint4041);
                    accessor_path237=accessor_path();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accessor_path.add(accessor_path237.getTree());
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint4043);
                    or_restr_connective238=or_restr_connective();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_or_restr_connective.add(or_restr_connective238.getTree());


                    // AST REWRITE
                    // elements: accessor_path, or_restr_connective
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1182:3: -> ^( VT_FIELD accessor_path or_restr_connective )
                    {
                        // src/main/resources/org/drools/lang/DRL5x.g:1182:6: ^( VT_FIELD accessor_path or_restr_connective )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FIELD, "VT_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_accessor_path.nextTree());
                        adaptor.addChild(root_1, stream_or_restr_connective.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "field_constraint"

    public static class label_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "label"
    // src/main/resources/org/drools/lang/DRL5x.g:1185:1: label : value= ID COLON -> VT_LABEL[$value] ;
    public final DRL5xParser.label_return label() throws RecognitionException {
        DRL5xParser.label_return retval = new DRL5xParser.label_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;
        Token COLON239=null;

        Object value_tree=null;
        Object COLON239_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1186:2: (value= ID COLON -> VT_LABEL[$value] )
            // src/main/resources/org/drools/lang/DRL5x.g:1186:4: value= ID COLON
            {
            value=(Token)match(input,ID,FOLLOW_ID_in_label4068); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(value);

            if ( state.backtracking==0 ) {
              	emit(value, DroolsEditorType.IDENTIFIER_VARIABLE);	
            }
            COLON239=(Token)match(input,COLON,FOLLOW_COLON_in_label4075); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON239);

            if ( state.backtracking==0 ) {
              	emit(COLON239, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1188:3: -> VT_LABEL[$value]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_LABEL, value));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "label"

    public static class or_restr_connective_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "or_restr_connective"
    // src/main/resources/org/drools/lang/DRL5x.g:1191:1: or_restr_connective : and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* ;
    public final DRL5xParser.or_restr_connective_return or_restr_connective() throws RecognitionException {
        DRL5xParser.or_restr_connective_return retval = new DRL5xParser.or_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE241=null;
        DRL5xParser.and_restr_connective_return and_restr_connective240 = null;

        DRL5xParser.and_restr_connective_return and_restr_connective242 = null;


        Object DOUBLE_PIPE241_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1192:2: ( and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* )
            // src/main/resources/org/drools/lang/DRL5x.g:1192:4: and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective4096);
            and_restr_connective240=and_restr_connective();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, and_restr_connective240.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:1192:25: ({...}? => DOUBLE_PIPE and_restr_connective )*
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);

                if ( (LA88_0==DOUBLE_PIPE) ) {
                    int LA88_2 = input.LA(2);

                    if ( (((validateRestr()))) ) {
                        alt88=1;
                    }


                }


                switch (alt88) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1192:26: {...}? => DOUBLE_PIPE and_restr_connective
            	    {
            	    if ( !(((validateRestr()))) ) {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        throw new FailedPredicateException(input, "or_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_PIPE241=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective4102); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_PIPE241_tree = (Object)adaptor.create(DOUBLE_PIPE241);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE241_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_PIPE241, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective4110);
            	    and_restr_connective242=and_restr_connective();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, and_restr_connective242.getTree());

            	    }
            	    break;

            	default :
            	    break loop88;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch ( RecognitionException re ) {

            	if (!lookaheadTest){
                    reportError(re);
                    recover(input,re);
                	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
            	} else {
            		throw re;
            	}

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "or_restr_connective"

    public static class and_restr_connective_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "and_restr_connective"
    // src/main/resources/org/drools/lang/DRL5x.g:1205:1: and_restr_connective : constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* ;
    public final DRL5xParser.and_restr_connective_return and_restr_connective() throws RecognitionException {
        DRL5xParser.and_restr_connective_return retval = new DRL5xParser.and_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER244=null;
        DRL5xParser.constraint_expression_return constraint_expression243 = null;

        DRL5xParser.constraint_expression_return constraint_expression245 = null;


        Object DOUBLE_AMPER244_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1206:2: ( constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* )
            // src/main/resources/org/drools/lang/DRL5x.g:1206:4: constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective4131);
            constraint_expression243=constraint_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint_expression243.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:1206:26: ({...}? => DOUBLE_AMPER constraint_expression )*
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( (LA89_0==DOUBLE_AMPER) ) {
                    int LA89_2 = input.LA(2);

                    if ( (((validateRestr()))) ) {
                        alt89=1;
                    }


                }


                switch (alt89) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1206:27: {...}? => DOUBLE_AMPER constraint_expression
            	    {
            	    if ( !(((validateRestr()))) ) {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        throw new FailedPredicateException(input, "and_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_AMPER244=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective4137); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_AMPER244_tree = (Object)adaptor.create(DOUBLE_AMPER244);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER244_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_AMPER244, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective4144);
            	    constraint_expression245=constraint_expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint_expression245.getTree());

            	    }
            	    break;

            	default :
            	    break loop89;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch ( RecognitionException re ) {

            	if (!lookaheadTest){
                    reportError(re);
                    recover(input,re);
                	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
            	} else {
            		throw re;
            	}

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "and_restr_connective"

    public static class constraint_expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constraint_expression"
    // src/main/resources/org/drools/lang/DRL5x.g:1219:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );
    public final DRL5xParser.constraint_expression_return constraint_expression() throws RecognitionException {
        DRL5xParser.constraint_expression_return retval = new DRL5xParser.constraint_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN248=null;
        Token RIGHT_PAREN250=null;
        DRL5xParser.compound_operator_return compound_operator246 = null;

        DRL5xParser.simple_operator_return simple_operator247 = null;

        DRL5xParser.or_restr_connective_return or_restr_connective249 = null;


        Object LEFT_PAREN248_tree=null;
        Object RIGHT_PAREN250_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1222:3: ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN )
            int alt90=3;
            alt90 = dfa90.predict(input);
            switch (alt90) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1222:5: compound_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_compound_operator_in_constraint_expression4172);
                    compound_operator246=compound_operator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, compound_operator246.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1223:4: simple_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_simple_operator_in_constraint_expression4177);
                    simple_operator247=simple_operator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_operator247.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1224:4: LEFT_PAREN or_restr_connective RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN248=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression4182); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN248, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression4191);
                    or_restr_connective249=or_restr_connective();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, or_restr_connective249.getTree());
                    RIGHT_PAREN250=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression4196); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_PAREN250_tree = (Object)adaptor.create(RIGHT_PAREN250);
                    adaptor.addChild(root_0, RIGHT_PAREN250_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN250, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch ( RecognitionException re ) {

            	if (!lookaheadTest){
                    reportError(re);
                    recover(input,re);
                	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
            	} else {
            		throw re;
            	}

        }
        finally {

            	if (isEditorInterfaceEnabled && input.LA(2) == EOF && input.LA(1) == ID) {
            		emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            		emit(input.LT(1), DroolsEditorType.KEYWORD);
            		input.consume();
            		emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
            	} else if (isEditorInterfaceEnabled && input.LA(3) == EOF && input.LA(1) == ID && 
            				input.LA(2) == ID && validateLT(1, DroolsSoftKeywords.NOT)) {
            		emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            		emit(input.LT(1), DroolsEditorType.KEYWORD);
            		emit(input.LT(2), DroolsEditorType.KEYWORD);
            		input.consume();
            		input.consume();
            		emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
            	} else if (isEditorInterfaceEnabled && input.LA(3) == EOF  && input.LA(1) == ID && validateLT(1, DroolsSoftKeywords.IN)) {
            		emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            		emit(input.LT(1), DroolsEditorType.KEYWORD);
            		emit(input.LT(2), DroolsEditorType.SYMBOL);
            		input.consume();
            		input.consume();
            		emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
            	} else if (isEditorInterfaceEnabled && input.LA(3) == EOF && input.LA(1) == ID) {
            		emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            		emit(input.LT(1), DroolsEditorType.KEYWORD);
            		emit(input.LT(2), DroolsEditorType.IDENTIFIER);
            		input.consume();
            		input.consume();
            		if (input.get(input.index() - 1).getType() == WS){
            			emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_END);
            		}
            	}

        }
        return retval;
    }
    // $ANTLR end "constraint_expression"

    public static class simple_operator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "simple_operator"
    // src/main/resources/org/drools/lang/DRL5x.g:1270:1: simple_operator : ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) ) expression_value ;
    public final DRL5xParser.simple_operator_return simple_operator() throws RecognitionException {
        DRL5xParser.simple_operator_return retval = new DRL5xParser.simple_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUAL251=null;
        Token GREATER252=null;
        Token GREATER_EQUAL253=null;
        Token LESS254=null;
        Token LESS_EQUAL255=null;
        Token NOT_EQUAL256=null;
        DRL5xParser.not_key_return not_key257 = null;

        DRL5xParser.operator_key_return operator_key258 = null;

        DRL5xParser.square_chunk_return square_chunk259 = null;

        DRL5xParser.expression_value_return expression_value260 = null;


        Object EQUAL251_tree=null;
        Object GREATER252_tree=null;
        Object GREATER_EQUAL253_tree=null;
        Object LESS254_tree=null;
        Object LESS_EQUAL255_tree=null;
        Object NOT_EQUAL256_tree=null;

        if ( state.backtracking==0 ) emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1272:2: ( ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) ) expression_value )
            // src/main/resources/org/drools/lang/DRL5x.g:1273:2: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) ) expression_value
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL5x.g:1273:2: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) )
            int alt93=7;
            int LA93_0 = input.LA(1);

            if ( (LA93_0==EQUAL) ) {
                alt93=1;
            }
            else if ( (LA93_0==GREATER) ) {
                alt93=2;
            }
            else if ( (LA93_0==GREATER_EQUAL) ) {
                alt93=3;
            }
            else if ( (LA93_0==LESS) ) {
                alt93=4;
            }
            else if ( (LA93_0==LESS_EQUAL) ) {
                alt93=5;
            }
            else if ( (LA93_0==NOT_EQUAL) ) {
                alt93=6;
            }
            else if ( (LA93_0==ID) && ((((isPluggableEvaluator(false)))||((validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {
                alt93=7;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }
            switch (alt93) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1274:3: EQUAL
                    {
                    EQUAL251=(Token)match(input,EQUAL,FOLLOW_EQUAL_in_simple_operator4231); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQUAL251_tree = (Object)adaptor.create(EQUAL251);
                    root_0 = (Object)adaptor.becomeRoot(EQUAL251_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(EQUAL251, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1275:4: GREATER
                    {
                    GREATER252=(Token)match(input,GREATER,FOLLOW_GREATER_in_simple_operator4239); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER252_tree = (Object)adaptor.create(GREATER252);
                    root_0 = (Object)adaptor.becomeRoot(GREATER252_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(GREATER252, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1276:4: GREATER_EQUAL
                    {
                    GREATER_EQUAL253=(Token)match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_simple_operator4247); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER_EQUAL253_tree = (Object)adaptor.create(GREATER_EQUAL253);
                    root_0 = (Object)adaptor.becomeRoot(GREATER_EQUAL253_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(GREATER_EQUAL253, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1277:4: LESS
                    {
                    LESS254=(Token)match(input,LESS,FOLLOW_LESS_in_simple_operator4255); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS254_tree = (Object)adaptor.create(LESS254);
                    root_0 = (Object)adaptor.becomeRoot(LESS254_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(LESS254, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1278:4: LESS_EQUAL
                    {
                    LESS_EQUAL255=(Token)match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_simple_operator4263); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS_EQUAL255_tree = (Object)adaptor.create(LESS_EQUAL255);
                    root_0 = (Object)adaptor.becomeRoot(LESS_EQUAL255_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(LESS_EQUAL255, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1279:4: NOT_EQUAL
                    {
                    NOT_EQUAL256=(Token)match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_simple_operator4271); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NOT_EQUAL256_tree = (Object)adaptor.create(NOT_EQUAL256);
                    root_0 = (Object)adaptor.becomeRoot(NOT_EQUAL256_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(NOT_EQUAL256, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1280:4: ( not_key )? ( operator_key ( square_chunk )? )
                    {
                    // src/main/resources/org/drools/lang/DRL5x.g:1280:4: ( not_key )?
                    int alt91=2;
                    int LA91_0 = input.LA(1);

                    if ( (LA91_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {
                        int LA91_1 = input.LA(2);

                        if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                            alt91=1;
                        }
                    }
                    switch (alt91) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRL5x.g:1280:4: not_key
                            {
                            pushFollow(FOLLOW_not_key_in_simple_operator4279);
                            not_key257=not_key();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, not_key257.getTree());

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRL5x.g:1281:3: ( operator_key ( square_chunk )? )
                    // src/main/resources/org/drools/lang/DRL5x.g:1281:5: operator_key ( square_chunk )?
                    {
                    pushFollow(FOLLOW_operator_key_in_simple_operator4286);
                    operator_key258=operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(operator_key258.getTree(), root_0);
                    // src/main/resources/org/drools/lang/DRL5x.g:1281:19: ( square_chunk )?
                    int alt92=2;
                    int LA92_0 = input.LA(1);

                    if ( (LA92_0==LEFT_SQUARE) ) {
                        alt92=1;
                    }
                    switch (alt92) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRL5x.g:1281:19: square_chunk
                            {
                            pushFollow(FOLLOW_square_chunk_in_simple_operator4289);
                            square_chunk259=square_chunk();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, square_chunk259.getTree());

                            }
                            break;

                    }


                    }


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);	
            }
            pushFollow(FOLLOW_expression_value_in_simple_operator4301);
            expression_value260=expression_value();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression_value260.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "simple_operator"

    public static class compound_operator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "compound_operator"
    // src/main/resources/org/drools/lang/DRL5x.g:1288:1: compound_operator : ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN ;
    public final DRL5xParser.compound_operator_return compound_operator() throws RecognitionException {
        DRL5xParser.compound_operator_return retval = new DRL5xParser.compound_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN264=null;
        Token COMMA266=null;
        Token RIGHT_PAREN268=null;
        DRL5xParser.in_key_return in_key261 = null;

        DRL5xParser.not_key_return not_key262 = null;

        DRL5xParser.in_key_return in_key263 = null;

        DRL5xParser.expression_value_return expression_value265 = null;

        DRL5xParser.expression_value_return expression_value267 = null;


        Object LEFT_PAREN264_tree=null;
        Object COMMA266_tree=null;
        Object RIGHT_PAREN268_tree=null;

         if ( state.backtracking==0 ) emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR); 
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1290:2: ( ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN )
            // src/main/resources/org/drools/lang/DRL5x.g:1291:2: ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL5x.g:1291:2: ( in_key | not_key in_key )
            int alt94=2;
            int LA94_0 = input.LA(1);

            if ( (LA94_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {
                int LA94_1 = input.LA(2);

                if ( (LA94_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IN))))) {
                    alt94=1;
                }
                else if ( (LA94_1==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))))) {
                    alt94=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 94, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 94, 0, input);

                throw nvae;
            }
            switch (alt94) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1291:4: in_key
                    {
                    pushFollow(FOLLOW_in_key_in_compound_operator4323);
                    in_key261=in_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key261.getTree(), root_0);

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1291:14: not_key in_key
                    {
                    pushFollow(FOLLOW_not_key_in_compound_operator4328);
                    not_key262=not_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, not_key262.getTree());
                    pushFollow(FOLLOW_in_key_in_compound_operator4330);
                    in_key263=in_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key263.getTree(), root_0);

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);	
            }
            LEFT_PAREN264=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator4341); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN264, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_expression_value_in_compound_operator4349);
            expression_value265=expression_value();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression_value265.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:1294:21: ( COMMA expression_value )*
            loop95:
            do {
                int alt95=2;
                int LA95_0 = input.LA(1);

                if ( (LA95_0==COMMA) ) {
                    alt95=1;
                }


                switch (alt95) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1294:23: COMMA expression_value
            	    {
            	    COMMA266=(Token)match(input,COMMA,FOLLOW_COMMA_in_compound_operator4353); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA266, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_expression_value_in_compound_operator4358);
            	    expression_value267=expression_value();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression_value267.getTree());

            	    }
            	    break;

            	default :
            	    break loop95;
                }
            } while (true);

            RIGHT_PAREN268=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator4366); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_PAREN268_tree = (Object)adaptor.create(RIGHT_PAREN268);
            adaptor.addChild(root_0, RIGHT_PAREN268_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN268, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_END);	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
    // $ANTLR end "compound_operator"

    public static class operator_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "operator_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1305:1: operator_key : {...}? =>id= ID -> VK_OPERATOR[$id] ;
    public final DRL5xParser.operator_key_return operator_key() throws RecognitionException {
        DRL5xParser.operator_key_return retval = new DRL5xParser.operator_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1306:2: ({...}? =>id= ID -> VK_OPERATOR[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1306:9: {...}? =>id= ID
            {
            if ( !(((isPluggableEvaluator(false)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "operator_key", "(isPluggableEvaluator(false))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_operator_key4397); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
               emit(id, DroolsEditorType.IDENTIFIER); 
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1308:9: -> VK_OPERATOR[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_OPERATOR, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "operator_key"

    public static class neg_operator_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "neg_operator_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1311:1: neg_operator_key : {...}? =>id= ID -> VK_OPERATOR[$id] ;
    public final DRL5xParser.neg_operator_key_return neg_operator_key() throws RecognitionException {
        DRL5xParser.neg_operator_key_return retval = new DRL5xParser.neg_operator_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1312:2: ({...}? =>id= ID -> VK_OPERATOR[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1312:9: {...}? =>id= ID
            {
            if ( !(((isPluggableEvaluator(true)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "neg_operator_key", "(isPluggableEvaluator(true))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key4442); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
               emit(id, DroolsEditorType.IDENTIFIER); 
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1314:9: -> VK_OPERATOR[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_OPERATOR, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "neg_operator_key"

    public static class expression_value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression_value"
    // src/main/resources/org/drools/lang/DRL5x.g:1317:1: expression_value : ( accessor_path | literal_constraint | paren_chunk ) ;
    public final DRL5xParser.expression_value_return expression_value() throws RecognitionException {
        DRL5xParser.expression_value_return retval = new DRL5xParser.expression_value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.accessor_path_return accessor_path269 = null;

        DRL5xParser.literal_constraint_return literal_constraint270 = null;

        DRL5xParser.paren_chunk_return paren_chunk271 = null;



        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1318:2: ( ( accessor_path | literal_constraint | paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1318:4: ( accessor_path | literal_constraint | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL5x.g:1318:4: ( accessor_path | literal_constraint | paren_chunk )
            int alt96=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt96=1;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt96=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt96=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 96, 0, input);

                throw nvae;
            }

            switch (alt96) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1318:5: accessor_path
                    {
                    pushFollow(FOLLOW_accessor_path_in_expression_value4479);
                    accessor_path269=accessor_path();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, accessor_path269.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1319:4: literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_expression_value4484);
                    literal_constraint270=literal_constraint();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal_constraint270.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1320:4: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_value4490);
                    paren_chunk271=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk271.getTree());

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	if (isEditorInterfaceEnabled && !(input.LA(1) == EOF && input.get(input.index() - 1).getType() != WS))
              			emit(Location.LOCATION_LHS_INSIDE_CONDITION_END);	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
             
            	if (isEditorInterfaceEnabled && input.LA(2) == EOF) {
            		if (input.LA(1) == DOUBLE_PIPE) {
            			emit(input.LT(1), DroolsEditorType.SYMBOL);
            			input.consume();
            			emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            		}
            	}

        }
        return retval;
    }
    // $ANTLR end "expression_value"

    public static class literal_constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal_constraint"
    // src/main/resources/org/drools/lang/DRL5x.g:1334:1: literal_constraint : ( STRING | INT | FLOAT | BOOL | NULL );
    public final DRL5xParser.literal_constraint_return literal_constraint() throws RecognitionException {
        DRL5xParser.literal_constraint_return retval = new DRL5xParser.literal_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING272=null;
        Token INT273=null;
        Token FLOAT274=null;
        Token BOOL275=null;
        Token NULL276=null;

        Object STRING272_tree=null;
        Object INT273_tree=null;
        Object FLOAT274_tree=null;
        Object BOOL275_tree=null;
        Object NULL276_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1335:2: ( STRING | INT | FLOAT | BOOL | NULL )
            int alt97=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt97=1;
                }
                break;
            case INT:
                {
                alt97=2;
                }
                break;
            case FLOAT:
                {
                alt97=3;
                }
                break;
            case BOOL:
                {
                alt97=4;
                }
                break;
            case NULL:
                {
                alt97=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 97, 0, input);

                throw nvae;
            }

            switch (alt97) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1335:4: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING272=(Token)match(input,STRING,FOLLOW_STRING_in_literal_constraint4509); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING272_tree = (Object)adaptor.create(STRING272);
                    adaptor.addChild(root_0, STRING272_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(STRING272, DroolsEditorType.STRING_CONST);	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1336:4: INT
                    {
                    root_0 = (Object)adaptor.nil();

                    INT273=(Token)match(input,INT,FOLLOW_INT_in_literal_constraint4516); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT273_tree = (Object)adaptor.create(INT273);
                    adaptor.addChild(root_0, INT273_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(INT273, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1337:4: FLOAT
                    {
                    root_0 = (Object)adaptor.nil();

                    FLOAT274=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint4523); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FLOAT274_tree = (Object)adaptor.create(FLOAT274);
                    adaptor.addChild(root_0, FLOAT274_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(FLOAT274, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1338:4: BOOL
                    {
                    root_0 = (Object)adaptor.nil();

                    BOOL275=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal_constraint4530); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL275_tree = (Object)adaptor.create(BOOL275);
                    adaptor.addChild(root_0, BOOL275_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(BOOL275, DroolsEditorType.BOOLEAN_CONST);	
                    }

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1339:4: NULL
                    {
                    root_0 = (Object)adaptor.nil();

                    NULL276=(Token)match(input,NULL,FOLLOW_NULL_in_literal_constraint4537); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NULL276_tree = (Object)adaptor.create(NULL276);
                    adaptor.addChild(root_0, NULL276_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(NULL276, DroolsEditorType.NULL_CONST);	
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "literal_constraint"

    public static class pattern_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "pattern_type"
    // src/main/resources/org/drools/lang/DRL5x.g:1342:1: pattern_type : id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final DRL5xParser.pattern_type_return pattern_type() throws RecognitionException {
        DRL5xParser.pattern_type_return retval = new DRL5xParser.pattern_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;
        DRL5xParser.dimension_definition_return dimension_definition277 = null;


        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1343:2: (id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1343:4: id+= ID (id+= DOT id+= ID )* ( dimension_definition )*
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_pattern_type4552); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // src/main/resources/org/drools/lang/DRL5x.g:1343:11: (id+= DOT id+= ID )*
            loop98:
            do {
                int alt98=2;
                int LA98_0 = input.LA(1);

                if ( (LA98_0==DOT) ) {
                    alt98=1;
                }


                switch (alt98) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1343:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_pattern_type4558); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_pattern_type4562); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop98;
                }
            } while (true);

            if ( state.backtracking==0 ) {
              	emit(list_id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.PATTERN, buildStringFromTokens(list_id));	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:1346:6: ( dimension_definition )*
            loop99:
            do {
                int alt99=2;
                int LA99_0 = input.LA(1);

                if ( (LA99_0==LEFT_SQUARE) ) {
                    alt99=1;
                }


                switch (alt99) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1346:6: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_pattern_type4577);
            	    dimension_definition277=dimension_definition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_dimension_definition.add(dimension_definition277.getTree());

            	    }
            	    break;

            	default :
            	    break loop99;
                }
            } while (true);



            // AST REWRITE
            // elements: ID, dimension_definition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1347:3: -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:1347:6: ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_PATTERN_TYPE, "VT_PATTERN_TYPE"), root_1);

                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, stream_ID.nextNode());

                }
                stream_ID.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:1347:28: ( dimension_definition )*
                while ( stream_dimension_definition.hasNext() ) {
                    adaptor.addChild(root_1, stream_dimension_definition.nextTree());

                }
                stream_dimension_definition.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "pattern_type"

    public static class data_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "data_type"
    // src/main/resources/org/drools/lang/DRL5x.g:1350:1: data_type : id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final DRL5xParser.data_type_return data_type() throws RecognitionException {
        DRL5xParser.data_type_return retval = new DRL5xParser.data_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;
        DRL5xParser.dimension_definition_return dimension_definition278 = null;


        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1351:2: (id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1351:4: id+= ID (id+= DOT id+= ID )* ( dimension_definition )*
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_data_type4605); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // src/main/resources/org/drools/lang/DRL5x.g:1351:11: (id+= DOT id+= ID )*
            loop100:
            do {
                int alt100=2;
                int LA100_0 = input.LA(1);

                if ( (LA100_0==DOT) ) {
                    alt100=1;
                }


                switch (alt100) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1351:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_data_type4611); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_data_type4615); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop100;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DRL5x.g:1351:31: ( dimension_definition )*
            loop101:
            do {
                int alt101=2;
                int LA101_0 = input.LA(1);

                if ( (LA101_0==LEFT_SQUARE) ) {
                    alt101=1;
                }


                switch (alt101) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1351:31: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_data_type4620);
            	    dimension_definition278=dimension_definition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_dimension_definition.add(dimension_definition278.getTree());

            	    }
            	    break;

            	default :
            	    break loop101;
                }
            } while (true);

            if ( state.backtracking==0 ) {
              	emit(list_id, DroolsEditorType.IDENTIFIER);	
            }


            // AST REWRITE
            // elements: ID, dimension_definition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1353:3: -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:1353:6: ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_DATA_TYPE, "VT_DATA_TYPE"), root_1);

                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, stream_ID.nextNode());

                }
                stream_ID.reset();
                // src/main/resources/org/drools/lang/DRL5x.g:1353:25: ( dimension_definition )*
                while ( stream_dimension_definition.hasNext() ) {
                    adaptor.addChild(root_1, stream_dimension_definition.nextTree());

                }
                stream_dimension_definition.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "data_type"

    public static class dimension_definition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dimension_definition"
    // src/main/resources/org/drools/lang/DRL5x.g:1356:1: dimension_definition : LEFT_SQUARE RIGHT_SQUARE ;
    public final DRL5xParser.dimension_definition_return dimension_definition() throws RecognitionException {
        DRL5xParser.dimension_definition_return retval = new DRL5xParser.dimension_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE279=null;
        Token RIGHT_SQUARE280=null;

        Object LEFT_SQUARE279_tree=null;
        Object RIGHT_SQUARE280_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1357:2: ( LEFT_SQUARE RIGHT_SQUARE )
            // src/main/resources/org/drools/lang/DRL5x.g:1357:4: LEFT_SQUARE RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE279=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dimension_definition4649); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_SQUARE279_tree = (Object)adaptor.create(LEFT_SQUARE279);
            adaptor.addChild(root_0, LEFT_SQUARE279_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(LEFT_SQUARE279, DroolsEditorType.SYMBOL);	
            }
            RIGHT_SQUARE280=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dimension_definition4656); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_SQUARE280_tree = (Object)adaptor.create(RIGHT_SQUARE280);
            adaptor.addChild(root_0, RIGHT_SQUARE280_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(RIGHT_SQUARE280, DroolsEditorType.SYMBOL);	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "dimension_definition"

    public static class accessor_path_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "accessor_path"
    // src/main/resources/org/drools/lang/DRL5x.g:1361:1: accessor_path : accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) ;
    public final DRL5xParser.accessor_path_return accessor_path() throws RecognitionException {
        DRL5xParser.accessor_path_return retval = new DRL5xParser.accessor_path_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT282=null;
        DRL5xParser.accessor_element_return accessor_element281 = null;

        DRL5xParser.accessor_element_return accessor_element283 = null;


        Object DOT282_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_accessor_element=new RewriteRuleSubtreeStream(adaptor,"rule accessor_element");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1362:2: ( accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1362:4: accessor_element ( DOT accessor_element )*
            {
            pushFollow(FOLLOW_accessor_element_in_accessor_path4670);
            accessor_element281=accessor_element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accessor_element.add(accessor_element281.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:1362:21: ( DOT accessor_element )*
            loop102:
            do {
                int alt102=2;
                int LA102_0 = input.LA(1);

                if ( (LA102_0==DOT) ) {
                    alt102=1;
                }


                switch (alt102) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1362:23: DOT accessor_element
            	    {
            	    DOT282=(Token)match(input,DOT,FOLLOW_DOT_in_accessor_path4674); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT282);

            	    if ( state.backtracking==0 ) {
            	      	emit(DOT282, DroolsEditorType.IDENTIFIER);	
            	    }
            	    pushFollow(FOLLOW_accessor_element_in_accessor_path4678);
            	    accessor_element283=accessor_element();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_accessor_element.add(accessor_element283.getTree());

            	    }
            	    break;

            	default :
            	    break loop102;
                }
            } while (true);



            // AST REWRITE
            // elements: accessor_element
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1363:2: -> ^( VT_ACCESSOR_PATH ( accessor_element )+ )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:1363:5: ^( VT_ACCESSOR_PATH ( accessor_element )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ACCESSOR_PATH, "VT_ACCESSOR_PATH"), root_1);

                if ( !(stream_accessor_element.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_accessor_element.hasNext() ) {
                    adaptor.addChild(root_1, stream_accessor_element.nextTree());

                }
                stream_accessor_element.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "accessor_path"

    public static class accessor_element_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "accessor_element"
    // src/main/resources/org/drools/lang/DRL5x.g:1366:1: accessor_element : ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) ;
    public final DRL5xParser.accessor_element_return accessor_element() throws RecognitionException {
        DRL5xParser.accessor_element_return retval = new DRL5xParser.accessor_element_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID284=null;
        DRL5xParser.square_chunk_return square_chunk285 = null;


        Object ID284_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1367:2: ( ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) )
            // src/main/resources/org/drools/lang/DRL5x.g:1367:4: ID ( square_chunk )*
            {
            ID284=(Token)match(input,ID,FOLLOW_ID_in_accessor_element4702); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID284);

            if ( state.backtracking==0 ) {
              	emit(ID284, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:1368:3: ( square_chunk )*
            loop103:
            do {
                int alt103=2;
                int LA103_0 = input.LA(1);

                if ( (LA103_0==LEFT_SQUARE) ) {
                    alt103=1;
                }


                switch (alt103) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1368:3: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_accessor_element4708);
            	    square_chunk285=square_chunk();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_square_chunk.add(square_chunk285.getTree());

            	    }
            	    break;

            	default :
            	    break loop103;
                }
            } while (true);



            // AST REWRITE
            // elements: square_chunk, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1369:2: -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
            {
                // src/main/resources/org/drools/lang/DRL5x.g:1369:5: ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ACCESSOR_ELEMENT, "VT_ACCESSOR_ELEMENT"), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // src/main/resources/org/drools/lang/DRL5x.g:1369:30: ( square_chunk )*
                while ( stream_square_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_square_chunk.nextTree());

                }
                stream_square_chunk.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "accessor_element"

    public static class rhs_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rhs_chunk"
    // src/main/resources/org/drools/lang/DRL5x.g:1372:1: rhs_chunk : rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] ;
    public final DRL5xParser.rhs_chunk_return rhs_chunk() throws RecognitionException {
        DRL5xParser.rhs_chunk_return retval = new DRL5xParser.rhs_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.rhs_chunk_data_return rc = null;


        RewriteRuleSubtreeStream stream_rhs_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk_data");

        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1375:3: (rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1375:5: rc= rhs_chunk_data
            {
            pushFollow(FOLLOW_rhs_chunk_data_in_rhs_chunk4737);
            rc=rhs_chunk_data();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rhs_chunk_data.add(rc.getTree());
            if ( state.backtracking==0 ) {
              text = (rc!=null?input.toString(rc.start,rc.stop):null);
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1376:2: -> VT_RHS_CHUNK[$rc.start,text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_RHS_CHUNK, (rc!=null?((Token)rc.start):null), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rhs_chunk"

    public static class rhs_chunk_data_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rhs_chunk_data"
    // src/main/resources/org/drools/lang/DRL5x.g:1379:1: rhs_chunk_data : THEN ( not_end_key )* end_key ( SEMICOLON )? ;
    public final DRL5xParser.rhs_chunk_data_return rhs_chunk_data() throws RecognitionException {
        DRL5xParser.rhs_chunk_data_return retval = new DRL5xParser.rhs_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token THEN286=null;
        Token SEMICOLON289=null;
        DRL5xParser.not_end_key_return not_end_key287 = null;

        DRL5xParser.end_key_return end_key288 = null;


        Object THEN286_tree=null;
        Object SEMICOLON289_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1380:2: ( THEN ( not_end_key )* end_key ( SEMICOLON )? )
            // src/main/resources/org/drools/lang/DRL5x.g:1380:4: THEN ( not_end_key )* end_key ( SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            THEN286=(Token)match(input,THEN,FOLLOW_THEN_in_rhs_chunk_data4756); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            THEN286_tree = (Object)adaptor.create(THEN286);
            adaptor.addChild(root_0, THEN286_tree);
            }
            if ( state.backtracking==0 ) {
              	if ((THEN286!=null?THEN286.getText():null).equalsIgnoreCase("then")){
              			emit(THEN286, DroolsEditorType.KEYWORD);
              			emit(Location.LOCATION_RHS);
              		}	
            }
            // src/main/resources/org/drools/lang/DRL5x.g:1385:4: ( not_end_key )*
            loop104:
            do {
                int alt104=2;
                int LA104_0 = input.LA(1);

                if ( (LA104_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.END)))||(!(validateIdentifierKey(DroolsSoftKeywords.END)))))) {
                    int LA104_1 = input.LA(2);

                    if ( ((!(validateIdentifierKey(DroolsSoftKeywords.END)))) ) {
                        alt104=1;
                    }


                }
                else if ( ((LA104_0>=VT_COMPILATION_UNIT && LA104_0<=SEMICOLON)||(LA104_0>=DOT && LA104_0<=IdentifierPart)) && ((!(validateIdentifierKey(DroolsSoftKeywords.END))))) {
                    alt104=1;
                }


                switch (alt104) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1385:4: not_end_key
            	    {
            	    pushFollow(FOLLOW_not_end_key_in_rhs_chunk_data4765);
            	    not_end_key287=not_end_key();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, not_end_key287.getTree());

            	    }
            	    break;

            	default :
            	    break loop104;
                }
            } while (true);

            pushFollow(FOLLOW_end_key_in_rhs_chunk_data4771);
            end_key288=end_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, end_key288.getTree());
            // src/main/resources/org/drools/lang/DRL5x.g:1387:3: ( SEMICOLON )?
            int alt105=2;
            int LA105_0 = input.LA(1);

            if ( (LA105_0==SEMICOLON) ) {
                alt105=1;
            }
            switch (alt105) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL5x.g:1387:3: SEMICOLON
                    {
                    SEMICOLON289=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_rhs_chunk_data4776); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMICOLON289_tree = (Object)adaptor.create(SEMICOLON289);
                    adaptor.addChild(root_0, SEMICOLON289_tree);
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON289, DroolsEditorType.KEYWORD);	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rhs_chunk_data"

    public static class curly_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "curly_chunk"
    // src/main/resources/org/drools/lang/DRL5x.g:1390:1: curly_chunk : cc= curly_chunk_data[false] -> VT_CURLY_CHUNK[$cc.start,text] ;
    public final DRL5xParser.curly_chunk_return curly_chunk() throws RecognitionException {
        DRL5xParser.curly_chunk_return retval = new DRL5xParser.curly_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.curly_chunk_data_return cc = null;


        RewriteRuleSubtreeStream stream_curly_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule curly_chunk_data");

        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1393:3: (cc= curly_chunk_data[false] -> VT_CURLY_CHUNK[$cc.start,text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1393:5: cc= curly_chunk_data[false]
            {
            pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk4795);
            cc=curly_chunk_data(false);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_curly_chunk_data.add(cc.getTree());
            if ( state.backtracking==0 ) {
              text = (cc!=null?input.toString(cc.start,cc.stop):null);
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1394:2: -> VT_CURLY_CHUNK[$cc.start,text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_CURLY_CHUNK, (cc!=null?((Token)cc.start):null), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "curly_chunk"

    public static class curly_chunk_data_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "curly_chunk_data"
    // src/main/resources/org/drools/lang/DRL5x.g:1397:1: curly_chunk_data[boolean isRecursive] : lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY ;
    public final DRL5xParser.curly_chunk_data_return curly_chunk_data(boolean isRecursive) throws RecognitionException {
        DRL5xParser.curly_chunk_data_return retval = new DRL5xParser.curly_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lc1=null;
        Token any=null;
        Token rc1=null;
        DRL5xParser.curly_chunk_data_return curly_chunk_data290 = null;


        Object lc1_tree=null;
        Object any_tree=null;
        Object rc1_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1398:2: (lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY )
            // src/main/resources/org/drools/lang/DRL5x.g:1398:4: lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY
            {
            root_0 = (Object)adaptor.nil();

            lc1=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk_data4818); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            lc1_tree = (Object)adaptor.create(lc1);
            adaptor.addChild(root_0, lc1_tree);
            }
            if ( state.backtracking==0 ) {
              	if (!isRecursive) {
              				emit(lc1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(lc1, DroolsEditorType.CODE_CHUNK);
              			}	
              		
            }
            // src/main/resources/org/drools/lang/DRL5x.g:1405:4: (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )*
            loop106:
            do {
                int alt106=3;
                int LA106_0 = input.LA(1);

                if ( ((LA106_0>=VT_COMPILATION_UNIT && LA106_0<=THEN)||(LA106_0>=MISC && LA106_0<=IdentifierPart)) ) {
                    alt106=1;
                }
                else if ( (LA106_0==LEFT_CURLY) ) {
                    alt106=2;
                }


                switch (alt106) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1405:5: any=~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=THEN)||(input.LA(1)>=MISC && input.LA(1)<=IdentifierPart) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(any));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    if ( state.backtracking==0 ) {
            	       emit(any, DroolsEditorType.CODE_CHUNK); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1405:87: curly_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk_data4846);
            	    curly_chunk_data290=curly_chunk_data(true);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, curly_chunk_data290.getTree());

            	    }
            	    break;

            	default :
            	    break loop106;
                }
            } while (true);

            rc1=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk_data4857); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            rc1_tree = (Object)adaptor.create(rc1);
            adaptor.addChild(root_0, rc1_tree);
            }
            if ( state.backtracking==0 ) {
              	if (!isRecursive) {
              				emit(rc1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(rc1, DroolsEditorType.CODE_CHUNK);
              			}	
              		
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "curly_chunk_data"

    public static class paren_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "paren_chunk"
    // src/main/resources/org/drools/lang/DRL5x.g:1415:1: paren_chunk : pc= paren_chunk_data[false] -> VT_PAREN_CHUNK[$pc.start,text] ;
    public final DRL5xParser.paren_chunk_return paren_chunk() throws RecognitionException {
        DRL5xParser.paren_chunk_return retval = new DRL5xParser.paren_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.paren_chunk_data_return pc = null;


        RewriteRuleSubtreeStream stream_paren_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk_data");

        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1418:3: (pc= paren_chunk_data[false] -> VT_PAREN_CHUNK[$pc.start,text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1418:5: pc= paren_chunk_data[false]
            {
            pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk4878);
            pc=paren_chunk_data(false);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_paren_chunk_data.add(pc.getTree());
            if ( state.backtracking==0 ) {
              text = (pc!=null?input.toString(pc.start,pc.stop):null);
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1419:2: -> VT_PAREN_CHUNK[$pc.start,text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_PAREN_CHUNK, (pc!=null?((Token)pc.start):null), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "paren_chunk"

    public static class paren_chunk_data_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "paren_chunk_data"
    // src/main/resources/org/drools/lang/DRL5x.g:1422:1: paren_chunk_data[boolean isRecursive] : lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN ;
    public final DRL5xParser.paren_chunk_data_return paren_chunk_data(boolean isRecursive) throws RecognitionException {
        DRL5xParser.paren_chunk_data_return retval = new DRL5xParser.paren_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lp1=null;
        Token any=null;
        Token rp1=null;
        DRL5xParser.paren_chunk_data_return paren_chunk_data291 = null;


        Object lp1_tree=null;
        Object any_tree=null;
        Object rp1_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1423:2: (lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN )
            // src/main/resources/org/drools/lang/DRL5x.g:1423:4: lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            lp1=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk_data4902); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            lp1_tree = (Object)adaptor.create(lp1);
            adaptor.addChild(root_0, lp1_tree);
            }
            if ( state.backtracking==0 ) {
              	if (!isRecursive) {
              				emit(lp1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(lp1, DroolsEditorType.CODE_CHUNK);
              			}	
              		
            }
            // src/main/resources/org/drools/lang/DRL5x.g:1430:4: (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )*
            loop107:
            do {
                int alt107=3;
                int LA107_0 = input.LA(1);

                if ( ((LA107_0>=VT_COMPILATION_UNIT && LA107_0<=STRING)||LA107_0==COMMA||(LA107_0>=AT && LA107_0<=IdentifierPart)) ) {
                    alt107=1;
                }
                else if ( (LA107_0==LEFT_PAREN) ) {
                    alt107=2;
                }


                switch (alt107) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1430:5: any=~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=STRING)||input.LA(1)==COMMA||(input.LA(1)>=AT && input.LA(1)<=IdentifierPart) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(any));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    if ( state.backtracking==0 ) {
            	       emit(any, DroolsEditorType.CODE_CHUNK); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1430:87: paren_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk_data4930);
            	    paren_chunk_data291=paren_chunk_data(true);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk_data291.getTree());

            	    }
            	    break;

            	default :
            	    break loop107;
                }
            } while (true);

            rp1=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk_data4941); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            rp1_tree = (Object)adaptor.create(rp1);
            adaptor.addChild(root_0, rp1_tree);
            }
            if ( state.backtracking==0 ) {
              	if (!isRecursive) {
              				emit(rp1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(rp1, DroolsEditorType.CODE_CHUNK);
              			}	
              		
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "paren_chunk_data"

    public static class square_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "square_chunk"
    // src/main/resources/org/drools/lang/DRL5x.g:1440:1: square_chunk : sc= square_chunk_data[false] -> VT_SQUARE_CHUNK[$sc.start,text] ;
    public final DRL5xParser.square_chunk_return square_chunk() throws RecognitionException {
        DRL5xParser.square_chunk_return retval = new DRL5xParser.square_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRL5xParser.square_chunk_data_return sc = null;


        RewriteRuleSubtreeStream stream_square_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk_data");

        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1443:3: (sc= square_chunk_data[false] -> VT_SQUARE_CHUNK[$sc.start,text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1443:5: sc= square_chunk_data[false]
            {
            pushFollow(FOLLOW_square_chunk_data_in_square_chunk4962);
            sc=square_chunk_data(false);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_square_chunk_data.add(sc.getTree());
            if ( state.backtracking==0 ) {
              text = (sc!=null?input.toString(sc.start,sc.stop):null);
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1444:2: -> VT_SQUARE_CHUNK[$sc.start,text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SQUARE_CHUNK, (sc!=null?((Token)sc.start):null), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "square_chunk"

    public static class square_chunk_data_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "square_chunk_data"
    // src/main/resources/org/drools/lang/DRL5x.g:1447:1: square_chunk_data[boolean isRecursive] : ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE ;
    public final DRL5xParser.square_chunk_data_return square_chunk_data(boolean isRecursive) throws RecognitionException {
        DRL5xParser.square_chunk_data_return retval = new DRL5xParser.square_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ls1=null;
        Token any=null;
        Token rs1=null;
        DRL5xParser.square_chunk_data_return square_chunk_data292 = null;


        Object ls1_tree=null;
        Object any_tree=null;
        Object rs1_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1448:2: (ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE )
            // src/main/resources/org/drools/lang/DRL5x.g:1448:4: ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            ls1=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk_data4985); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ls1_tree = (Object)adaptor.create(ls1);
            adaptor.addChild(root_0, ls1_tree);
            }
            if ( state.backtracking==0 ) {
              	if (!isRecursive) {
              				emit(ls1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(ls1, DroolsEditorType.CODE_CHUNK);
              			}	
              		
            }
            // src/main/resources/org/drools/lang/DRL5x.g:1455:4: (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )*
            loop108:
            do {
                int alt108=3;
                int LA108_0 = input.LA(1);

                if ( ((LA108_0>=VT_COMPILATION_UNIT && LA108_0<=NULL)||(LA108_0>=THEN && LA108_0<=IdentifierPart)) ) {
                    alt108=1;
                }
                else if ( (LA108_0==LEFT_SQUARE) ) {
                    alt108=2;
                }


                switch (alt108) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1455:5: any=~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=NULL)||(input.LA(1)>=THEN && input.LA(1)<=IdentifierPart) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(any));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    if ( state.backtracking==0 ) {
            	       emit(any, DroolsEditorType.CODE_CHUNK); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/DRL5x.g:1455:88: square_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_square_chunk_data_in_square_chunk_data5012);
            	    square_chunk_data292=square_chunk_data(true);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, square_chunk_data292.getTree());

            	    }
            	    break;

            	default :
            	    break loop108;
                }
            } while (true);

            rs1=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk_data5023); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            rs1_tree = (Object)adaptor.create(rs1);
            adaptor.addChild(root_0, rs1_tree);
            }
            if ( state.backtracking==0 ) {
              	if (!isRecursive) {
              				emit(rs1, DroolsEditorType.SYMBOL);
              			} else {
              				emit(rs1, DroolsEditorType.CODE_CHUNK);
              			}	
              		
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "square_chunk_data"

    public static class lock_on_active_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lock_on_active_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1465:1: lock_on_active_key : {...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID -> VK_LOCK_ON_ACTIVE[$start, text] ;
    public final DRL5xParser.lock_on_active_key_return lock_on_active_key() throws RecognitionException {
        DRL5xParser.lock_on_active_key_return retval = new DRL5xParser.lock_on_active_key_return();
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
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");


        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1468:3: ({...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID -> VK_LOCK_ON_ACTIVE[$start, text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1468:5: {...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "lock_on_active_key", "(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, \"-\") && validateLT(5, DroolsSoftKeywords.ACTIVE))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_lock_on_active_key5047); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_lock_on_active_key5051); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_lock_on_active_key5055); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            mis2=(Token)match(input,MISC,FOLLOW_MISC_in_lock_on_active_key5059); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis2);

            id3=(Token)match(input,ID,FOLLOW_ID_in_lock_on_active_key5063); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id3);

            if ( state.backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( state.backtracking==0 ) {
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
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1474:3: -> VK_LOCK_ON_ACTIVE[$start, text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_LOCK_ON_ACTIVE, ((Token)retval.start), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lock_on_active_key"

    public static class date_effective_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "date_effective_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1477:1: date_effective_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EFFECTIVE[$start, text] ;
    public final DRL5xParser.date_effective_key_return date_effective_key() throws RecognitionException {
        DRL5xParser.date_effective_key_return retval = new DRL5xParser.date_effective_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");


        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1480:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EFFECTIVE[$start, text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1480:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "date_effective_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_date_effective_key5095); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_date_effective_key5099); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_date_effective_key5103); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            if ( state.backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( state.backtracking==0 ) {
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
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1484:3: -> VK_DATE_EFFECTIVE[$start, text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_DATE_EFFECTIVE, ((Token)retval.start), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "date_effective_key"

    public static class date_expires_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "date_expires_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1487:1: date_expires_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EXPIRES[$start, text] ;
    public final DRL5xParser.date_expires_key_return date_expires_key() throws RecognitionException {
        DRL5xParser.date_expires_key_return retval = new DRL5xParser.date_expires_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");


        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1490:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EXPIRES[$start, text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1490:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "date_expires_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EXPIRES))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_date_expires_key5135); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_date_expires_key5139); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_date_expires_key5143); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            if ( state.backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( state.backtracking==0 ) {
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
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1494:3: -> VK_DATE_EXPIRES[$start, text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_DATE_EXPIRES, ((Token)retval.start), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "date_expires_key"

    public static class no_loop_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "no_loop_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1497:1: no_loop_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_NO_LOOP[$start, text] ;
    public final DRL5xParser.no_loop_key_return no_loop_key() throws RecognitionException {
        DRL5xParser.no_loop_key_return retval = new DRL5xParser.no_loop_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");


        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1500:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_NO_LOOP[$start, text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1500:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "no_loop_key", "(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.LOOP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_no_loop_key5175); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_no_loop_key5179); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_no_loop_key5183); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            if ( state.backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( state.backtracking==0 ) {
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
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1504:3: -> VK_NO_LOOP[$start, text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_NO_LOOP, ((Token)retval.start), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "no_loop_key"

    public static class auto_focus_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "auto_focus_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1507:1: auto_focus_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_AUTO_FOCUS[$start, text] ;
    public final DRL5xParser.auto_focus_key_return auto_focus_key() throws RecognitionException {
        DRL5xParser.auto_focus_key_return retval = new DRL5xParser.auto_focus_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");


        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1510:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_AUTO_FOCUS[$start, text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1510:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "auto_focus_key", "(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.FOCUS))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_auto_focus_key5215); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_auto_focus_key5219); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_auto_focus_key5223); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            if ( state.backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( state.backtracking==0 ) {
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
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1514:3: -> VK_AUTO_FOCUS[$start, text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_AUTO_FOCUS, ((Token)retval.start), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "auto_focus_key"

    public static class activation_group_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "activation_group_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1517:1: activation_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_ACTIVATION_GROUP[$start, text] ;
    public final DRL5xParser.activation_group_key_return activation_group_key() throws RecognitionException {
        DRL5xParser.activation_group_key_return retval = new DRL5xParser.activation_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");


        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1520:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_ACTIVATION_GROUP[$start, text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1520:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "activation_group_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_activation_group_key5255); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_activation_group_key5259); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_activation_group_key5263); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            if ( state.backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( state.backtracking==0 ) {
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
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1524:3: -> VK_ACTIVATION_GROUP[$start, text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_ACTIVATION_GROUP, ((Token)retval.start), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "activation_group_key"

    public static class agenda_group_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "agenda_group_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1527:1: agenda_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_AGENDA_GROUP[$start, text] ;
    public final DRL5xParser.agenda_group_key_return agenda_group_key() throws RecognitionException {
        DRL5xParser.agenda_group_key_return retval = new DRL5xParser.agenda_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");


        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1530:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_AGENDA_GROUP[$start, text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1530:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "agenda_group_key", "(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_agenda_group_key5295); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_agenda_group_key5299); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_agenda_group_key5303); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            if ( state.backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( state.backtracking==0 ) {
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
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1534:3: -> VK_AGENDA_GROUP[$start, text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_AGENDA_GROUP, ((Token)retval.start), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "agenda_group_key"

    public static class ruleflow_group_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleflow_group_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1537:1: ruleflow_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_RULEFLOW_GROUP[$start, text] ;
    public final DRL5xParser.ruleflow_group_key_return ruleflow_group_key() throws RecognitionException {
        DRL5xParser.ruleflow_group_key_return retval = new DRL5xParser.ruleflow_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");


        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1540:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_RULEFLOW_GROUP[$start, text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1540:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "ruleflow_group_key", "(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_ruleflow_group_key5335); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_ruleflow_group_key5339); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_ruleflow_group_key5343); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            if ( state.backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( state.backtracking==0 ) {
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
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1544:3: -> VK_RULEFLOW_GROUP[$start, text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_RULEFLOW_GROUP, ((Token)retval.start), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ruleflow_group_key"

    public static class entry_point_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entry_point_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1547:1: entry_point_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_ENTRY_POINT[$start, text] ;
    public final DRL5xParser.entry_point_key_return entry_point_key() throws RecognitionException {
        DRL5xParser.entry_point_key_return retval = new DRL5xParser.entry_point_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token mis1=null;
        Token id2=null;

        Object id1_tree=null;
        Object mis1_tree=null;
        Object id2_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");


        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1550:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_ENTRY_POINT[$start, text] )
            // src/main/resources/org/drools/lang/DRL5x.g:1550:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "entry_point_key", "(validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.POINT))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_entry_point_key5375); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_entry_point_key5379); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_entry_point_key5383); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            if ( state.backtracking==0 ) {
              text = input.toString(retval.start,input.LT(-1));
            }
            if ( state.backtracking==0 ) {
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
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1554:3: -> VK_ENTRY_POINT[$start, text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_ENTRY_POINT, ((Token)retval.start), text));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "entry_point_key"

    public static class timer_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "timer_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1557:1: timer_key : {...}? =>id= ID -> VK_TIMER[$id] ;
    public final DRL5xParser.timer_key_return timer_key() throws RecognitionException {
        DRL5xParser.timer_key_return retval = new DRL5xParser.timer_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1558:2: ({...}? =>id= ID -> VK_TIMER[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1558:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.TIMER)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "timer_key", "(validateIdentifierKey(DroolsSoftKeywords.TIMER))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_timer_key5412); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1560:3: -> VK_TIMER[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_TIMER, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "timer_key"

    public static class duration_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "duration_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1564:1: duration_key : {...}? =>id= ID -> VK_TIMER[$id] ;
    public final DRL5xParser.duration_key_return duration_key() throws RecognitionException {
        DRL5xParser.duration_key_return retval = new DRL5xParser.duration_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1565:2: ({...}? =>id= ID -> VK_TIMER[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1565:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DURATION)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "duration_key", "(validateIdentifierKey(DroolsSoftKeywords.DURATION))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_duration_key5440); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1567:3: -> VK_TIMER[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_TIMER, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "duration_key"

    public static class calendars_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "calendars_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1570:1: calendars_key : {...}? =>id= ID -> VK_CALENDARS[$id] ;
    public final DRL5xParser.calendars_key_return calendars_key() throws RecognitionException {
        DRL5xParser.calendars_key_return retval = new DRL5xParser.calendars_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1571:2: ({...}? =>id= ID -> VK_CALENDARS[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1571:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "calendars_key", "(validateIdentifierKey(DroolsSoftKeywords.CALENDARS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_calendars_key5467); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1573:3: -> VK_CALENDARS[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_CALENDARS, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "calendars_key"

    public static class package_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "package_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1576:1: package_key : {...}? =>id= ID -> VK_PACKAGE[$id] ;
    public final DRL5xParser.package_key_return package_key() throws RecognitionException {
        DRL5xParser.package_key_return retval = new DRL5xParser.package_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1577:2: ({...}? =>id= ID -> VK_PACKAGE[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1577:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.PACKAGE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "package_key", "(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_package_key5494); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1579:3: -> VK_PACKAGE[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_PACKAGE, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "package_key"

    public static class import_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "import_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1582:1: import_key : {...}? =>id= ID -> VK_IMPORT[$id] ;
    public final DRL5xParser.import_key_return import_key() throws RecognitionException {
        DRL5xParser.import_key_return retval = new DRL5xParser.import_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1583:2: ({...}? =>id= ID -> VK_IMPORT[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1583:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "import_key", "(validateIdentifierKey(DroolsSoftKeywords.IMPORT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_import_key5521); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1585:3: -> VK_IMPORT[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_IMPORT, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "import_key"

    public static class dialect_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dialect_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1588:1: dialect_key : {...}? =>id= ID -> VK_DIALECT[$id] ;
    public final DRL5xParser.dialect_key_return dialect_key() throws RecognitionException {
        DRL5xParser.dialect_key_return retval = new DRL5xParser.dialect_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1589:2: ({...}? =>id= ID -> VK_DIALECT[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1589:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "dialect_key", "(validateIdentifierKey(DroolsSoftKeywords.DIALECT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_dialect_key5548); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1591:3: -> VK_DIALECT[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_DIALECT, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "dialect_key"

    public static class salience_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "salience_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1594:1: salience_key : {...}? =>id= ID -> VK_SALIENCE[$id] ;
    public final DRL5xParser.salience_key_return salience_key() throws RecognitionException {
        DRL5xParser.salience_key_return retval = new DRL5xParser.salience_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1595:2: ({...}? =>id= ID -> VK_SALIENCE[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1595:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "salience_key", "(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_salience_key5575); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1597:3: -> VK_SALIENCE[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_SALIENCE, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "salience_key"

    public static class enabled_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enabled_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1600:1: enabled_key : {...}? =>id= ID -> VK_ENABLED[$id] ;
    public final DRL5xParser.enabled_key_return enabled_key() throws RecognitionException {
        DRL5xParser.enabled_key_return retval = new DRL5xParser.enabled_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1601:2: ({...}? =>id= ID -> VK_ENABLED[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1601:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "enabled_key", "(validateIdentifierKey(DroolsSoftKeywords.ENABLED))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_enabled_key5602); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1603:3: -> VK_ENABLED[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_ENABLED, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "enabled_key"

    public static class attributes_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attributes_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1606:1: attributes_key : {...}? =>id= ID -> VK_ATTRIBUTES[$id] ;
    public final DRL5xParser.attributes_key_return attributes_key() throws RecognitionException {
        DRL5xParser.attributes_key_return retval = new DRL5xParser.attributes_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1607:2: ({...}? =>id= ID -> VK_ATTRIBUTES[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1607:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "attributes_key", "(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_attributes_key5629); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1609:3: -> VK_ATTRIBUTES[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_ATTRIBUTES, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "attributes_key"

    public static class rule_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1612:1: rule_key : {...}? =>id= ID -> VK_RULE[$id] ;
    public final DRL5xParser.rule_key_return rule_key() throws RecognitionException {
        DRL5xParser.rule_key_return retval = new DRL5xParser.rule_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1613:2: ({...}? =>id= ID -> VK_RULE[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1613:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.RULE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "rule_key", "(validateIdentifierKey(DroolsSoftKeywords.RULE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_rule_key5656); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1615:3: -> VK_RULE[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_RULE, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rule_key"

    public static class extend_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "extend_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1618:1: extend_key : {...}? =>id= ID -> VK_EXTEND[$id] ;
    public final DRL5xParser.extend_key_return extend_key() throws RecognitionException {
        DRL5xParser.extend_key_return retval = new DRL5xParser.extend_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1619:2: ({...}? =>id= ID -> VK_EXTEND[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1619:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "extend_key", "(validateIdentifierKey(DroolsSoftKeywords.EXTEND))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_extend_key5683); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1621:3: -> VK_EXTEND[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_EXTEND, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "extend_key"

    public static class template_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "template_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1624:1: template_key : {...}? =>id= ID -> VK_TEMPLATE[$id] ;
    public final DRL5xParser.template_key_return template_key() throws RecognitionException {
        DRL5xParser.template_key_return retval = new DRL5xParser.template_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1625:2: ({...}? =>id= ID -> VK_TEMPLATE[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1625:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "template_key", "(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_template_key5710); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1627:3: -> VK_TEMPLATE[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_TEMPLATE, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "template_key"

    public static class query_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "query_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1630:1: query_key : {...}? =>id= ID -> VK_QUERY[$id] ;
    public final DRL5xParser.query_key_return query_key() throws RecognitionException {
        DRL5xParser.query_key_return retval = new DRL5xParser.query_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1631:2: ({...}? =>id= ID -> VK_QUERY[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1631:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "query_key", "(validateIdentifierKey(DroolsSoftKeywords.QUERY))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_query_key5737); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1633:3: -> VK_QUERY[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_QUERY, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "query_key"

    public static class declare_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "declare_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1636:1: declare_key : {...}? =>id= ID -> VK_DECLARE[$id] ;
    public final DRL5xParser.declare_key_return declare_key() throws RecognitionException {
        DRL5xParser.declare_key_return retval = new DRL5xParser.declare_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1637:2: ({...}? =>id= ID -> VK_DECLARE[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1637:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "declare_key", "(validateIdentifierKey(DroolsSoftKeywords.DECLARE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_declare_key5764); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1639:3: -> VK_DECLARE[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_DECLARE, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "declare_key"

    public static class function_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "function_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1642:1: function_key : {...}? =>id= ID -> VK_FUNCTION[$id] ;
    public final DRL5xParser.function_key_return function_key() throws RecognitionException {
        DRL5xParser.function_key_return retval = new DRL5xParser.function_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1643:2: ({...}? =>id= ID -> VK_FUNCTION[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1643:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "function_key", "(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_function_key5791); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1645:3: -> VK_FUNCTION[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_FUNCTION, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "function_key"

    public static class global_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "global_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1648:1: global_key : {...}? =>id= ID -> VK_GLOBAL[$id] ;
    public final DRL5xParser.global_key_return global_key() throws RecognitionException {
        DRL5xParser.global_key_return retval = new DRL5xParser.global_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1649:2: ({...}? =>id= ID -> VK_GLOBAL[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1649:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "global_key", "(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_global_key5818); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1651:3: -> VK_GLOBAL[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_GLOBAL, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "global_key"

    public static class eval_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "eval_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1654:1: eval_key : {...}? =>id= ID -> VK_EVAL[$id] ;
    public final DRL5xParser.eval_key_return eval_key() throws RecognitionException {
        DRL5xParser.eval_key_return retval = new DRL5xParser.eval_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1655:2: ({...}? =>id= ID -> VK_EVAL[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1655:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.EVAL)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "eval_key", "(validateIdentifierKey(DroolsSoftKeywords.EVAL))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_eval_key5845); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1657:3: -> VK_EVAL[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_EVAL, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "eval_key"

    public static class not_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "not_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1660:1: not_key : {...}? =>id= ID -> VK_NOT[$id] ;
    public final DRL5xParser.not_key_return not_key() throws RecognitionException {
        DRL5xParser.not_key_return retval = new DRL5xParser.not_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1661:2: ({...}? =>id= ID -> VK_NOT[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1661:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "not_key", "(validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_not_key5872); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1663:3: -> VK_NOT[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_NOT, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "not_key"

    public static class in_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "in_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1666:1: in_key : {...}? =>id= ID -> VK_IN[$id] ;
    public final DRL5xParser.in_key_return in_key() throws RecognitionException {
        DRL5xParser.in_key_return retval = new DRL5xParser.in_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1667:2: ({...}? =>id= ID -> VK_IN[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1667:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "in_key", "(validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_in_key5899); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1669:3: -> VK_IN[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_IN, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "in_key"

    public static class or_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "or_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1672:1: or_key : {...}? =>id= ID -> VK_OR[$id] ;
    public final DRL5xParser.or_key_return or_key() throws RecognitionException {
        DRL5xParser.or_key_return retval = new DRL5xParser.or_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1673:2: ({...}? =>id= ID -> VK_OR[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1673:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.OR)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "or_key", "(validateIdentifierKey(DroolsSoftKeywords.OR))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_or_key5926); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1675:3: -> VK_OR[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_OR, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "or_key"

    public static class and_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "and_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1678:1: and_key : {...}? =>id= ID -> VK_AND[$id] ;
    public final DRL5xParser.and_key_return and_key() throws RecognitionException {
        DRL5xParser.and_key_return retval = new DRL5xParser.and_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1679:2: ({...}? =>id= ID -> VK_AND[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1679:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.AND)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "and_key", "(validateIdentifierKey(DroolsSoftKeywords.AND))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_and_key5953); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1681:3: -> VK_AND[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_AND, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "and_key"

    public static class exists_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "exists_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1684:1: exists_key : {...}? =>id= ID -> VK_EXISTS[$id] ;
    public final DRL5xParser.exists_key_return exists_key() throws RecognitionException {
        DRL5xParser.exists_key_return retval = new DRL5xParser.exists_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1685:2: ({...}? =>id= ID -> VK_EXISTS[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1685:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.EXISTS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "exists_key", "(validateIdentifierKey(DroolsSoftKeywords.EXISTS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_exists_key5980); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1687:3: -> VK_EXISTS[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_EXISTS, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "exists_key"

    public static class forall_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "forall_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1690:1: forall_key : {...}? =>id= ID -> VK_FORALL[$id] ;
    public final DRL5xParser.forall_key_return forall_key() throws RecognitionException {
        DRL5xParser.forall_key_return retval = new DRL5xParser.forall_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1691:2: ({...}? =>id= ID -> VK_FORALL[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1691:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.FORALL)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "forall_key", "(validateIdentifierKey(DroolsSoftKeywords.FORALL))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_forall_key6007); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1693:3: -> VK_FORALL[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_FORALL, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "forall_key"

    public static class action_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "action_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1696:1: action_key : {...}? =>id= ID -> VK_ACTION[$id] ;
    public final DRL5xParser.action_key_return action_key() throws RecognitionException {
        DRL5xParser.action_key_return retval = new DRL5xParser.action_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1697:2: ({...}? =>id= ID -> VK_ACTION[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1697:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ACTION)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "action_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTION))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_action_key6034); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1699:3: -> VK_ACTION[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_ACTION, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "action_key"

    public static class reverse_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "reverse_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1702:1: reverse_key : {...}? =>id= ID -> VK_REVERSE[$id] ;
    public final DRL5xParser.reverse_key_return reverse_key() throws RecognitionException {
        DRL5xParser.reverse_key_return retval = new DRL5xParser.reverse_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1703:2: ({...}? =>id= ID -> VK_REVERSE[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1703:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.REVERSE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "reverse_key", "(validateIdentifierKey(DroolsSoftKeywords.REVERSE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_reverse_key6061); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1705:3: -> VK_REVERSE[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_REVERSE, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "reverse_key"

    public static class result_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "result_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1708:1: result_key : {...}? =>id= ID -> VK_RESULT[$id] ;
    public final DRL5xParser.result_key_return result_key() throws RecognitionException {
        DRL5xParser.result_key_return retval = new DRL5xParser.result_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1709:2: ({...}? =>id= ID -> VK_RESULT[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1709:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.RESULT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "result_key", "(validateIdentifierKey(DroolsSoftKeywords.RESULT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_result_key6088); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1711:3: -> VK_RESULT[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_RESULT, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "result_key"

    public static class end_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "end_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1714:1: end_key : {...}? =>id= ID -> VK_END[$id] ;
    public final DRL5xParser.end_key_return end_key() throws RecognitionException {
        DRL5xParser.end_key_return retval = new DRL5xParser.end_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1715:2: ({...}? =>id= ID -> VK_END[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1715:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.END)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "end_key", "(validateIdentifierKey(DroolsSoftKeywords.END))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_end_key6115); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1717:3: -> VK_END[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_END, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "end_key"

    public static class not_end_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "not_end_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1720:1: not_end_key : {...}? =>any= . ;
    public final DRL5xParser.not_end_key_return not_end_key() throws RecognitionException {
        DRL5xParser.not_end_key_return retval = new DRL5xParser.not_end_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token any=null;

        Object any_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1721:2: ({...}? =>any= . )
            // src/main/resources/org/drools/lang/DRL5x.g:1721:4: {...}? =>any= .
            {
            root_0 = (Object)adaptor.nil();

            if ( !((!(validateIdentifierKey(DroolsSoftKeywords.END)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "not_end_key", "!(validateIdentifierKey(DroolsSoftKeywords.END))");
            }
            any=(Token)input.LT(1);
            matchAny(input); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            any_tree = (Object)adaptor.create(any);
            adaptor.addChild(root_0, any_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(any, DroolsEditorType.CODE_CHUNK);	
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "not_end_key"

    public static class init_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "init_key"
    // src/main/resources/org/drools/lang/DRL5x.g:1725:1: init_key : {...}? =>id= ID -> VK_INIT[$id] ;
    public final DRL5xParser.init_key_return init_key() throws RecognitionException {
        DRL5xParser.init_key_return retval = new DRL5xParser.init_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL5x.g:1726:2: ({...}? =>id= ID -> VK_INIT[$id] )
            // src/main/resources/org/drools/lang/DRL5x.g:1726:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.INIT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "init_key", "(validateIdentifierKey(DroolsSoftKeywords.INIT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_init_key6162); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if ( state.backtracking==0 ) {
              	emit(id, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1728:3: -> VK_INIT[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_INIT, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "init_key"

    // $ANTLR start synpred1_DRL5x
    public final void synpred1_DRL5x_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL5x.g:827:5: ( LEFT_PAREN or_key )
        // src/main/resources/org/drools/lang/DRL5x.g:827:6: LEFT_PAREN or_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred1_DRL5x2143); if (state.failed) return ;
        pushFollow(FOLLOW_or_key_in_synpred1_DRL5x2145);
        or_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_DRL5x

    // $ANTLR start synpred2_DRL5x
    public final void synpred2_DRL5x_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL5x.g:835:5: ( or_key | DOUBLE_PIPE )
        int alt109=2;
        int LA109_0 = input.LA(1);

        if ( (LA109_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            alt109=1;
        }
        else if ( (LA109_0==DOUBLE_PIPE) ) {
            alt109=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 109, 0, input);

            throw nvae;
        }
        switch (alt109) {
            case 1 :
                // src/main/resources/org/drools/lang/DRL5x.g:835:6: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred2_DRL5x2212);
                or_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRL5x.g:835:13: DOUBLE_PIPE
                {
                match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred2_DRL5x2214); if (state.failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred2_DRL5x

    // $ANTLR start synpred3_DRL5x
    public final void synpred3_DRL5x_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL5x.g:844:5: ( LEFT_PAREN and_key )
        // src/main/resources/org/drools/lang/DRL5x.g:844:6: LEFT_PAREN and_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred3_DRL5x2271); if (state.failed) return ;
        pushFollow(FOLLOW_and_key_in_synpred3_DRL5x2273);
        and_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_DRL5x

    // $ANTLR start synpred4_DRL5x
    public final void synpred4_DRL5x_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL5x.g:852:5: ( and_key | DOUBLE_AMPER )
        int alt110=2;
        int LA110_0 = input.LA(1);

        if ( (LA110_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))))) {
            alt110=1;
        }
        else if ( (LA110_0==DOUBLE_AMPER) ) {
            alt110=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 110, 0, input);

            throw nvae;
        }
        switch (alt110) {
            case 1 :
                // src/main/resources/org/drools/lang/DRL5x.g:852:6: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred4_DRL5x2341);
                and_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRL5x.g:852:14: DOUBLE_AMPER
                {
                match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred4_DRL5x2343); if (state.failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred4_DRL5x

    // $ANTLR start synpred5_DRL5x
    public final void synpred5_DRL5x_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL5x.g:869:4: ( SEMICOLON )
        // src/main/resources/org/drools/lang/DRL5x.g:869:5: SEMICOLON
        {
        match(input,SEMICOLON,FOLLOW_SEMICOLON_in_synpred5_DRL5x2466); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_DRL5x

    // $ANTLR start synpred6_DRL5x
    public final void synpred6_DRL5x_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL5x.g:875:12: ( LEFT_PAREN ( or_key | and_key ) )
        // src/main/resources/org/drools/lang/DRL5x.g:875:13: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred6_DRL5x2503); if (state.failed) return ;
        // src/main/resources/org/drools/lang/DRL5x.g:875:24: ( or_key | and_key )
        int alt111=2;
        int LA111_0 = input.LA(1);

        if ( (LA111_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.AND)))||((validateIdentifierKey(DroolsSoftKeywords.OR)))))) {
            int LA111_1 = input.LA(2);

            if ( (((validateIdentifierKey(DroolsSoftKeywords.OR)))) ) {
                alt111=1;
            }
            else if ( (((validateIdentifierKey(DroolsSoftKeywords.AND)))) ) {
                alt111=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 111, 1, input);

                throw nvae;
            }
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 111, 0, input);

            throw nvae;
        }
        switch (alt111) {
            case 1 :
                // src/main/resources/org/drools/lang/DRL5x.g:875:25: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred6_DRL5x2506);
                or_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRL5x.g:875:32: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred6_DRL5x2508);
                and_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred6_DRL5x

    // $ANTLR start synpred7_DRL5x
    public final void synpred7_DRL5x_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL5x.g:891:5: ( LEFT_PAREN ( or_key | and_key ) )
        // src/main/resources/org/drools/lang/DRL5x.g:891:6: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred7_DRL5x2631); if (state.failed) return ;
        // src/main/resources/org/drools/lang/DRL5x.g:891:17: ( or_key | and_key )
        int alt112=2;
        int LA112_0 = input.LA(1);

        if ( (LA112_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.AND)))||((validateIdentifierKey(DroolsSoftKeywords.OR)))))) {
            int LA112_1 = input.LA(2);

            if ( (((validateIdentifierKey(DroolsSoftKeywords.OR)))) ) {
                alt112=1;
            }
            else if ( (((validateIdentifierKey(DroolsSoftKeywords.AND)))) ) {
                alt112=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 112, 1, input);

                throw nvae;
            }
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 112, 0, input);

            throw nvae;
        }
        switch (alt112) {
            case 1 :
                // src/main/resources/org/drools/lang/DRL5x.g:891:18: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred7_DRL5x2634);
                or_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRL5x.g:891:25: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred7_DRL5x2636);
                and_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred7_DRL5x

    // $ANTLR start synpred8_DRL5x
    public final void synpred8_DRL5x_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL5x.g:1068:5: ( LEFT_PAREN )
        // src/main/resources/org/drools/lang/DRL5x.g:1068:6: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred8_DRL5x3464); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_DRL5x

    // Delegated rules

    public final boolean synpred5_DRL5x() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_DRL5x_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_DRL5x() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_DRL5x_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_DRL5x() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_DRL5x_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_DRL5x() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_DRL5x_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_DRL5x() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_DRL5x_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_DRL5x() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_DRL5x_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_DRL5x() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_DRL5x_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_DRL5x() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_DRL5x_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA12 dfa12 = new DFA12(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA29 dfa29 = new DFA29(this);
    protected DFA38 dfa38 = new DFA38(this);
    protected DFA73 dfa73 = new DFA73(this);
    protected DFA75 dfa75 = new DFA75(this);
    protected DFA85 dfa85 = new DFA85(this);
    protected DFA90 dfa90 = new DFA90(this);
    static final String DFA1_eotS =
        "\12\uffff";
    static final String DFA1_eofS =
        "\1\2\11\uffff";
    static final String DFA1_minS =
        "\2\123\2\uffff\1\0\5\uffff";
    static final String DFA1_maxS =
        "\1\123\1\164\2\uffff\1\0\5\uffff";
    static final String DFA1_acceptS =
        "\2\uffff\2\2\1\uffff\4\2\1\1";
    static final String DFA1_specialS =
        "\1\uffff\1\0\2\uffff\1\1\5\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\1",
            "\1\4\2\uffff\1\10\1\6\6\uffff\1\7\1\5\24\uffff\1\3",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "397:4: ( package_statement )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA1_1 = input.LA(1);

                         
                        int index1_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_1==MISC) && ((((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))))) {s = 3;}

                        else if ( (LA1_1==ID) ) {s = 4;}

                        else if ( (LA1_1==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {s = 5;}

                        else if ( (LA1_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {s = 6;}

                        else if ( (LA1_1==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.ENABLED))))) {s = 7;}

                        else if ( (LA1_1==STRING) ) {s = 8;}

                         
                        input.seek(index1_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA1_4 = input.LA(1);

                         
                        int index1_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.PACKAGE)))) ) {s = 9;}

                        else if ( (true) ) {s = 8;}

                         
                        input.seek(index1_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 1, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA5_eotS =
        "\20\uffff";
    static final String DFA5_eofS =
        "\20\uffff";
    static final String DFA5_minS =
        "\2\123\4\uffff\2\0\10\uffff";
    static final String DFA5_maxS =
        "\1\123\1\164\4\uffff\2\0\10\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\4\1\2\uffff\1\6\1\10\1\11\1\2\1\3\1\4\1\5\1\7";
    static final String DFA5_specialS =
        "\1\uffff\1\0\4\uffff\1\1\1\2\10\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1",
            "\1\7\2\uffff\1\6\1\4\6\uffff\1\5\1\3\24\uffff\1\2",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "455:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_1 = input.LA(1);

                         
                        int index5_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_1==MISC) && ((((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))))) {s = 2;}

                        else if ( (LA5_1==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {s = 3;}

                        else if ( (LA5_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {s = 4;}

                        else if ( (LA5_1==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.ENABLED))))) {s = 5;}

                        else if ( (LA5_1==STRING) && ((!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY))))))))) {s = 6;}

                        else if ( (LA5_1==ID) && (((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))))) {s = 7;}

                         
                        input.seek(index5_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA5_6 = input.LA(1);

                         
                        int index5_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) ) {s = 5;}

                        else if ( (((validateLT(1, DroolsSoftKeywords.TEMPLATE)))) ) {s = 8;}

                        else if ( (!(((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT))))))) ) {s = 9;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) ) {s = 10;}

                         
                        input.seek(index5_6);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA5_7 = input.LA(1);

                         
                        int index5_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) ) {s = 11;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))) ) {s = 13;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))) ) {s = 14;}

                        else if ( (((validateLT(1, DroolsSoftKeywords.TEMPLATE)))) ) {s = 8;}

                        else if ( ((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))) ) {s = 15;}

                        else if ( (!(((((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))))) ) {s = 9;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) ) {s = 10;}

                         
                        input.seek(index5_7);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 5, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA12_eotS =
        "\16\uffff";
    static final String DFA12_eofS =
        "\16\uffff";
    static final String DFA12_minS =
        "\2\123\1\uffff\1\123\1\uffff\1\123\1\160\3\123\2\160\1\127\1\123";
    static final String DFA12_maxS =
        "\1\127\1\131\1\uffff\1\157\1\uffff\1\123\1\160\3\157\2\160\2\157";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\11\uffff";
    static final String DFA12_specialS =
        "\16\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\2\3\uffff\1\1",
            "\1\3\3\uffff\1\2\1\uffff\1\4",
            "",
            "\1\7\1\5\2\uffff\1\2\2\4\1\uffff\1\2\23\uffff\1\6",
            "",
            "\1\10",
            "\1\11",
            "\2\2\2\uffff\1\2\2\4\1\uffff\1\2\23\uffff\1\12",
            "\1\4\1\5\2\uffff\1\2\27\uffff\1\13",
            "\1\4\3\uffff\1\2\2\4\25\uffff\1\6",
            "\1\14",
            "\1\15",
            "\1\2\2\4\25\uffff\1\12",
            "\1\4\3\uffff\1\2\27\uffff\1\13"
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
            return "526:3: ( parameters )?";
        }
    }
    static final String DFA17_eotS =
        "\6\uffff";
    static final String DFA17_eofS =
        "\6\uffff";
    static final String DFA17_minS =
        "\2\123\1\uffff\1\160\1\uffff\1\123";
    static final String DFA17_maxS =
        "\1\123\1\157\1\uffff\1\160\1\uffff\1\157";
    static final String DFA17_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    static final String DFA17_specialS =
        "\6\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\1",
            "\2\2\3\uffff\2\4\25\uffff\1\3",
            "",
            "\1\5",
            "",
            "\1\2\4\uffff\2\4\25\uffff\1\3"
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
            return "551:4: ( data_type )?";
        }
    }
    static final String DFA29_eotS =
        "\12\uffff";
    static final String DFA29_eofS =
        "\12\uffff";
    static final String DFA29_minS =
        "\2\123\3\uffff\1\0\4\uffff";
    static final String DFA29_maxS =
        "\1\161\1\164\3\uffff\1\0\4\uffff";
    static final String DFA29_acceptS =
        "\2\uffff\2\2\1\1\1\uffff\4\2";
    static final String DFA29_specialS =
        "\1\2\1\0\3\uffff\1\1\4\uffff}>";
    static final String[] DFA29_transitionS = {
            "\1\1\6\uffff\1\2\2\uffff\1\2\23\uffff\1\2",
            "\1\4\2\uffff\1\5\1\10\3\uffff\1\6\2\uffff\1\11\1\7\24\uffff"+
            "\1\3",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        public String getDescription() {
            return "641:3: ( extend_key rule_id )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA29_1 = input.LA(1);

                         
                        int index29_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA29_1==MISC) && ((((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))))) {s = 3;}

                        else if ( (LA29_1==ID) && (((validateIdentifierKey(DroolsSoftKeywords.EXTEND))))) {s = 4;}

                        else if ( (LA29_1==STRING) && ((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))))) {s = 5;}

                        else if ( (LA29_1==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {s = 6;}

                        else if ( (LA29_1==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {s = 7;}

                        else if ( (LA29_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {s = 8;}

                        else if ( (LA29_1==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.ENABLED))))) {s = 9;}

                         
                        input.seek(index29_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA29_5 = input.LA(1);

                         
                        int index29_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))) ) {s = 4;}

                        else if ( ((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) ) {s = 9;}

                         
                        input.seek(index29_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA29_0 = input.LA(1);

                         
                        int index29_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA29_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {s = 1;}

                        else if ( (LA29_0==AT||LA29_0==WHEN||LA29_0==THEN) ) {s = 2;}

                         
                        input.seek(index29_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 29, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA38_eotS =
        "\17\uffff";
    static final String DFA38_eofS =
        "\17\uffff";
    static final String DFA38_minS =
        "\1\123\1\0\15\uffff";
    static final String DFA38_maxS =
        "\1\123\1\0\15\uffff";
    static final String DFA38_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1"+
        "\15";
    static final String DFA38_specialS =
        "\1\0\1\1\15\uffff}>";
    static final String[] DFA38_transitionS = {
            "\1\1",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA38_eot = DFA.unpackEncodedString(DFA38_eotS);
    static final short[] DFA38_eof = DFA.unpackEncodedString(DFA38_eofS);
    static final char[] DFA38_min = DFA.unpackEncodedStringToUnsignedChars(DFA38_minS);
    static final char[] DFA38_max = DFA.unpackEncodedStringToUnsignedChars(DFA38_maxS);
    static final short[] DFA38_accept = DFA.unpackEncodedString(DFA38_acceptS);
    static final short[] DFA38_special = DFA.unpackEncodedString(DFA38_specialS);
    static final short[][] DFA38_transition;

    static {
        int numStates = DFA38_transitionS.length;
        DFA38_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA38_transition[i] = DFA.unpackEncodedString(DFA38_transitionS[i]);
        }
    }

    class DFA38 extends DFA {

        public DFA38(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 38;
            this.eot = DFA38_eot;
            this.eof = DFA38_eof;
            this.min = DFA38_min;
            this.max = DFA38_max;
            this.accept = DFA38_accept;
            this.special = DFA38_special;
            this.transition = DFA38_transition;
        }
        public String getDescription() {
            return "711:1: rule_attribute : ( salience | no_loop | agenda_group | timer | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect | calendars );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA38_0 = input.LA(1);

                         
                        int index38_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA38_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {s = 1;}

                         
                        input.seek(index38_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA38_1 = input.LA(1);

                         
                        int index38_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) ) {s = 2;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))) ) {s = 3;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {s = 4;}

                        else if ( ((((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION))))) ) {s = 5;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {s = 6;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))) ) {s = 7;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))) ) {s = 8;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))) ) {s = 9;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) ) {s = 10;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {s = 11;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) ) {s = 13;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.CALENDARS)))) ) {s = 14;}

                         
                        input.seek(index38_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 38, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA73_eotS =
        "\13\uffff";
    static final String DFA73_eofS =
        "\13\uffff";
    static final String DFA73_minS =
        "\1\122\1\0\11\uffff";
    static final String DFA73_maxS =
        "\1\161\1\0\11\uffff";
    static final String DFA73_acceptS =
        "\2\uffff\1\2\7\uffff\1\1";
    static final String DFA73_specialS =
        "\1\uffff\1\0\11\uffff}>";
    static final String[] DFA73_transitionS = {
            "\3\2\2\uffff\1\1\2\2\6\uffff\2\2\17\uffff\1\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA73_eot = DFA.unpackEncodedString(DFA73_eotS);
    static final short[] DFA73_eof = DFA.unpackEncodedString(DFA73_eofS);
    static final char[] DFA73_min = DFA.unpackEncodedStringToUnsignedChars(DFA73_minS);
    static final char[] DFA73_max = DFA.unpackEncodedStringToUnsignedChars(DFA73_maxS);
    static final short[] DFA73_accept = DFA.unpackEncodedString(DFA73_acceptS);
    static final short[] DFA73_special = DFA.unpackEncodedString(DFA73_specialS);
    static final short[][] DFA73_transition;

    static {
        int numStates = DFA73_transitionS.length;
        DFA73_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA73_transition[i] = DFA.unpackEncodedString(DFA73_transitionS[i]);
        }
    }

    class DFA73 extends DFA {

        public DFA73(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 73;
            this.eot = DFA73_eot;
            this.eof = DFA73_eof;
            this.min = DFA73_min;
            this.max = DFA73_max;
            this.accept = DFA73_accept;
            this.special = DFA73_special;
            this.transition = DFA73_transition;
        }
        public String getDescription() {
            return "1068:3: ( ( LEFT_PAREN )=>args= paren_chunk )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA73_1 = input.LA(1);

                         
                        int index73_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRL5x()) ) {s = 10;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index73_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 73, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA75_eotS =
        "\14\uffff";
    static final String DFA75_eofS =
        "\14\uffff";
    static final String DFA75_minS =
        "\1\122\1\0\12\uffff";
    static final String DFA75_maxS =
        "\1\161\1\0\12\uffff";
    static final String DFA75_acceptS =
        "\2\uffff\1\2\1\3\7\uffff\1\1";
    static final String DFA75_specialS =
        "\1\uffff\1\0\12\uffff}>";
    static final String[] DFA75_transitionS = {
            "\3\3\2\uffff\1\1\2\3\6\uffff\2\3\15\uffff\1\2\1\uffff\1\3",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA75_eot = DFA.unpackEncodedString(DFA75_eotS);
    static final short[] DFA75_eof = DFA.unpackEncodedString(DFA75_eofS);
    static final char[] DFA75_min = DFA.unpackEncodedStringToUnsignedChars(DFA75_minS);
    static final char[] DFA75_max = DFA.unpackEncodedStringToUnsignedChars(DFA75_maxS);
    static final short[] DFA75_accept = DFA.unpackEncodedString(DFA75_acceptS);
    static final short[] DFA75_special = DFA.unpackEncodedString(DFA75_specialS);
    static final short[][] DFA75_transition;

    static {
        int numStates = DFA75_transitionS.length;
        DFA75_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA75_transition[i] = DFA.unpackEncodedString(DFA75_transitionS[i]);
        }
    }

    class DFA75 extends DFA {

        public DFA75(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 75;
            this.eot = DFA75_eot;
            this.eof = DFA75_eof;
            this.min = DFA75_min;
            this.max = DFA75_max;
            this.accept = DFA75_accept;
            this.special = DFA75_special;
            this.transition = DFA75_transition;
        }
        public String getDescription() {
            return "1082:4: ({...}? paren_chunk | square_chunk )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA75_1 = input.LA(1);

                         
                        int index75_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((input.LA(1) == LEFT_PAREN)) ) {s = 11;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index75_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 75, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA85_eotS =
        "\17\uffff";
    static final String DFA85_eofS =
        "\17\uffff";
    static final String DFA85_minS =
        "\2\123\1\uffff\1\0\13\uffff";
    static final String DFA85_maxS =
        "\1\127\1\157\1\uffff\1\0\13\uffff";
    static final String DFA85_acceptS =
        "\2\uffff\1\3\1\uffff\1\2\11\uffff\1\1";
    static final String DFA85_specialS =
        "\3\uffff\1\0\13\uffff}>";
    static final String[] DFA85_transitionS = {
            "\1\1\3\uffff\1\2",
            "\2\4\2\uffff\1\3\3\uffff\1\4\13\uffff\6\4\2\uffff\1\4",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA85_eot = DFA.unpackEncodedString(DFA85_eotS);
    static final short[] DFA85_eof = DFA.unpackEncodedString(DFA85_eofS);
    static final char[] DFA85_min = DFA.unpackEncodedStringToUnsignedChars(DFA85_minS);
    static final char[] DFA85_max = DFA.unpackEncodedStringToUnsignedChars(DFA85_maxS);
    static final short[] DFA85_accept = DFA.unpackEncodedString(DFA85_acceptS);
    static final short[] DFA85_special = DFA.unpackEncodedString(DFA85_specialS);
    static final short[][] DFA85_transition;

    static {
        int numStates = DFA85_transitionS.length;
        DFA85_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA85_transition[i] = DFA.unpackEncodedString(DFA85_transitionS[i]);
        }
    }

    class DFA85 extends DFA {

        public DFA85(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 85;
            this.eot = DFA85_eot;
            this.eof = DFA85_eof;
            this.min = DFA85_min;
            this.max = DFA85_max;
            this.accept = DFA85_accept;
            this.special = DFA85_special;
            this.transition = DFA85_transition;
        }
        public String getDescription() {
            return "1155:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA85_3 = input.LA(1);

                         
                        int index85_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.EVAL)))) ) {s = 14;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index85_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 85, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA90_eotS =
        "\50\uffff";
    static final String DFA90_eofS =
        "\50\uffff";
    static final String DFA90_minS =
        "\2\123\7\uffff\1\4\1\123\6\uffff\7\0\2\uffff\1\0\15\uffff";
    static final String DFA90_maxS =
        "\1\154\1\157\7\uffff\1\177\1\157\6\uffff\7\0\2\uffff\1\0\15\uffff";
    static final String DFA90_acceptS =
        "\2\uffff\1\2\5\uffff\1\3\2\uffff\1\2\17\uffff\2\2\5\uffff\1\2\4"+
        "\uffff\1\1";
    static final String DFA90_specialS =
        "\1\0\1\1\7\uffff\1\2\1\3\6\uffff\1\4\1\5\1\6\1\7\1\10\1\11\1\12"+
        "\2\uffff\1\13\15\uffff}>";
    static final String[] DFA90_transitionS = {
            "\1\1\3\uffff\1\10\17\uffff\6\2",
            "\1\12\2\uffff\1\13\1\11\6\uffff\2\13\15\uffff\3\13",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\117\13\1\21\2\13\1\22\1\27\6\13\1\25\1\23\15\13\1\24\1\26"+
            "\21\13",
            "\1\34\1\42\1\uffff\1\34\1\32\2\42\4\uffff\2\34\2\42\13\uffff"+
            "\2\34\1\33",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA90_eot = DFA.unpackEncodedString(DFA90_eotS);
    static final short[] DFA90_eof = DFA.unpackEncodedString(DFA90_eofS);
    static final char[] DFA90_min = DFA.unpackEncodedStringToUnsignedChars(DFA90_minS);
    static final char[] DFA90_max = DFA.unpackEncodedStringToUnsignedChars(DFA90_maxS);
    static final short[] DFA90_accept = DFA.unpackEncodedString(DFA90_acceptS);
    static final short[] DFA90_special = DFA.unpackEncodedString(DFA90_specialS);
    static final short[][] DFA90_transition;

    static {
        int numStates = DFA90_transitionS.length;
        DFA90_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA90_transition[i] = DFA.unpackEncodedString(DFA90_transitionS[i]);
        }
    }

    class DFA90 extends DFA {

        public DFA90(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 90;
            this.eot = DFA90_eot;
            this.eof = DFA90_eof;
            this.min = DFA90_min;
            this.max = DFA90_max;
            this.accept = DFA90_accept;
            this.special = DFA90_special;
            this.transition = DFA90_transition;
        }
        public String getDescription() {
            return "1219:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA90_0 = input.LA(1);

                         
                        int index90_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA90_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {s = 1;}

                        else if ( ((LA90_0>=EQUAL && LA90_0<=NOT_EQUAL)) ) {s = 2;}

                        else if ( (LA90_0==LEFT_PAREN) ) {s = 8;}

                         
                        input.seek(index90_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA90_1 = input.LA(1);

                         
                        int index90_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA90_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 9;}

                        else if ( (LA90_1==ID) && ((((isPluggableEvaluator(false)))||((validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {s = 10;}

                        else if ( (LA90_1==STRING||(LA90_1>=BOOL && LA90_1<=INT)||(LA90_1>=FLOAT && LA90_1<=LEFT_SQUARE)) && (((isPluggableEvaluator(false))))) {s = 11;}

                         
                        input.seek(index90_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA90_9 = input.LA(1);

                         
                        int index90_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA90_9==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 17;}

                        else if ( (LA90_9==STRING) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 18;}

                        else if ( (LA90_9==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 19;}

                        else if ( (LA90_9==FLOAT) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 20;}

                        else if ( (LA90_9==BOOL) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 21;}

                        else if ( (LA90_9==NULL) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 22;}

                        else if ( (LA90_9==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 23;}

                        else if ( ((LA90_9>=VT_COMPILATION_UNIT && LA90_9<=SEMICOLON)||(LA90_9>=DOT && LA90_9<=DOT_STAR)||(LA90_9>=COMMA && LA90_9<=WHEN)||(LA90_9>=DOUBLE_PIPE && LA90_9<=NOT_EQUAL)||(LA90_9>=LEFT_SQUARE && LA90_9<=IdentifierPart)) && (((isPluggableEvaluator(false))))) {s = 11;}

                         
                        input.seek(index90_9);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA90_10 = input.LA(1);

                         
                        int index90_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA90_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))))) {s = 26;}

                        else if ( (LA90_10==LEFT_SQUARE) && ((((isPluggableEvaluator(false)))||((validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {s = 27;}

                        else if ( (LA90_10==ID||LA90_10==STRING||(LA90_10>=BOOL && LA90_10<=INT)||(LA90_10>=FLOAT && LA90_10<=NULL)) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))))) {s = 28;}

                        else if ( (LA90_10==DOT||(LA90_10>=COMMA && LA90_10<=RIGHT_PAREN)||(LA90_10>=DOUBLE_PIPE && LA90_10<=DOUBLE_AMPER)) && (((isPluggableEvaluator(false))))) {s = 34;}

                         
                        input.seek(index90_10);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA90_17 = input.LA(1);

                         
                        int index90_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 34;}

                         
                        input.seek(index90_17);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA90_18 = input.LA(1);

                         
                        int index90_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 34;}

                         
                        input.seek(index90_18);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA90_19 = input.LA(1);

                         
                        int index90_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 34;}

                         
                        input.seek(index90_19);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA90_20 = input.LA(1);

                         
                        int index90_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 34;}

                         
                        input.seek(index90_20);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA90_21 = input.LA(1);

                         
                        int index90_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 34;}

                         
                        input.seek(index90_21);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA90_22 = input.LA(1);

                         
                        int index90_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 34;}

                         
                        input.seek(index90_22);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA90_23 = input.LA(1);

                         
                        int index90_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 34;}

                         
                        input.seek(index90_23);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA90_26 = input.LA(1);

                         
                        int index90_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {s = 39;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {s = 34;}

                         
                        input.seek(index90_26);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 90, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_package_statement_in_compilation_unit388 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_statement_in_compilation_unit393 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_EOF_in_compilation_unit398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_package_key_in_package_statement453 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_package_id_in_package_statement457 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMICOLON_in_package_statement459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_id486 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_package_id492 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_package_id496 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_rule_attribute_in_statement534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_declaration_in_statement575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_import_statement607 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_import_name_in_import_statement609 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMICOLON_in_import_statement612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_function_import_statement650 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_function_key_in_function_import_statement652 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement654 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMICOLON_in_function_import_statement657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name691 = new BitSet(new long[]{0x0000000000000002L,0x0000000000300000L});
    public static final BitSet FOLLOW_DOT_in_import_name697 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_import_name701 = new BitSet(new long[]{0x0000000000000002L,0x0000000000300000L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_key_in_global748 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_data_type_in_global750 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_global_id_in_global752 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMICOLON_in_global754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_id783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_key_in_function815 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_data_type_in_function817 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_function_id_in_function820 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_parameters_in_function822 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_id854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_key_in_query886 = new BitSet(new long[]{0x0000000000000000L,0x0000000000480000L});
    public static final BitSet FOLLOW_query_id_in_query888 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_parameters_in_query896 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query905 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_end_key_in_query912 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMICOLON_in_query914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_id949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_query_id965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parameters984 = new BitSet(new long[]{0x0000000000000000L,0x0000000002080000L});
    public static final BitSet FOLLOW_param_definition_in_parameters993 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000000L});
    public static final BitSet FOLLOW_COMMA_in_parameters996 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_param_definition_in_parameters1000 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parameters1009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_param_definition1035 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_argument_in_param_definition1038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument1049 = new BitSet(new long[]{0x0000000000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_dimension_definition_in_argument1055 = new BitSet(new long[]{0x0000000000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_declare_key_in_type_declaration1078 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_type_declare_id_in_type_declaration1081 = new BitSet(new long[]{0x0000000000000000L,0x0000000004880000L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration1085 = new BitSet(new long[]{0x0000000000000000L,0x0000000004880000L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration1090 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_end_key_in_type_declaration1095 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_type_declare_id1127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_decl_metadata1146 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_decl_metadata1154 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_decl_metadata1161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_decl_field1186 = new BitSet(new long[]{0x0000000000000000L,0x0000000018000000L});
    public static final BitSet FOLLOW_decl_field_initialization_in_decl_field1192 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_decl_field1198 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_data_type_in_decl_field1204 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000000L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field1208 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000000L});
    public static final BitSet FOLLOW_EQUALS_in_decl_field_initialization1236 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_decl_field_initialization1242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_key_in_template1279 = new BitSet(new long[]{0x0000000000000000L,0x0000000000480000L});
    public static final BitSet FOLLOW_template_id_in_template1281 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1288 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0000L});
    public static final BitSet FOLLOW_template_slot_in_template1296 = new BitSet(new long[]{0x0000000000000000L,0x00000000008C0000L});
    public static final BitSet FOLLOW_end_key_in_template1303 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_id1340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_template_id1356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_template_slot1376 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_slot_id_in_template_slot1378 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template_slot1380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_slot_id1409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_key_in_rule1446 = new BitSet(new long[]{0x0000000000000000L,0x0000000000480000L});
    public static final BitSet FOLLOW_rule_id_in_rule1448 = new BitSet(new long[]{0x0000000000000000L,0x0002000024080000L});
    public static final BitSet FOLLOW_extend_key_in_rule1457 = new BitSet(new long[]{0x0000000000000000L,0x0000000000480000L});
    public static final BitSet FOLLOW_rule_id_in_rule1459 = new BitSet(new long[]{0x0000000000000000L,0x0002000024080000L});
    public static final BitSet FOLLOW_decl_metadata_in_rule1463 = new BitSet(new long[]{0x0000000000000000L,0x0002000024080000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule1466 = new BitSet(new long[]{0x0000000000000000L,0x0002000024080000L});
    public static final BitSet FOLLOW_when_part_in_rule1469 = new BitSet(new long[]{0x0000000000000000L,0x0002000024080000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_when_part1516 = new BitSet(new long[]{0x0000000000000000L,0x0000000008880000L});
    public static final BitSet FOLLOW_COLON_in_when_part1522 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_when_part1532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_id1553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_rule_id1569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attributes_key_in_rule_attributes1590 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_rule_attributes1592 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1602 = new BitSet(new long[]{0x0000000000000002L,0x0000000001080000L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1606 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1613 = new BitSet(new long[]{0x0000000000000002L,0x0000000001080000L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_timer_in_rule_attribute1670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_calendars_in_rule_attribute1726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_key_in_date_effective1740 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_key_in_date_expires1759 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_key_in_enabled1779 = new BitSet(new long[]{0x0000000000000000L,0x0000000040800000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_enabled1803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_salience_key_in_salience1823 = new BitSet(new long[]{0x0000000000000000L,0x0000000080800000L});
    public static final BitSet FOLLOW_INT_in_salience1832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_key_in_no_loop1856 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_key_in_auto_focus1876 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_key_in_activation_group1898 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_key_in_ruleflow_group1917 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_key_in_agenda_group1936 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_key_in_timer1956 = new BitSet(new long[]{0x0000000000000000L,0x0000000080800000L});
    public static final BitSet FOLLOW_timer_key_in_timer1959 = new BitSet(new long[]{0x0000000000000000L,0x0000000080800000L});
    public static final BitSet FOLLOW_INT_in_timer1973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_timer1984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_calendars_key_in_calendars2004 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_string_list_in_calendars2009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_string_list2027 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_COMMA_in_string_list2036 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_string_list2040 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_dialect_key_in_dialect2064 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_STRING_in_dialect2069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_key_in_lock_on_active2087 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active2092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block2107 = new BitSet(new long[]{0x0000000000000002L,0x0000000000880000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs2128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or2152 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or2162 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2170 = new BitSet(new long[]{0x0000000000000000L,0x0000000002880000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or2176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2199 = new BitSet(new long[]{0x0000000000000002L,0x0000000100080000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or2221 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_lhs_or2228 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2239 = new BitSet(new long[]{0x0000000000000002L,0x0000000100080000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and2280 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and2290 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2298 = new BitSet(new long[]{0x0000000000000000L,0x0000000002880000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and2304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2328 = new BitSet(new long[]{0x0000000000000002L,0x0000000200080000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and2350 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_lhs_and2357 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2368 = new BitSet(new long[]{0x0000000000000002L,0x0000000200080000L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2399 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_lhs_not_binding_in_lhs_unary2407 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2413 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2419 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary2425 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2431 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary2442 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2448 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_unary2456 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMICOLON_in_lhs_unary2470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_key_in_lhs_exist2486 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist2520 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2528 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist2535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist2550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not_binding2596 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_not_binding2598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not2621 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2650 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2659 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_key_in_lhs_eval2714 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forall_key_in_lhs_forall2750 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2755 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_forall2763 = new BitSet(new long[]{0x0000000000000000L,0x0000000002880000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall2769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_pattern_source2805 = new BitSet(new long[]{0x0000000000000002L,0x0000000C00000000L});
    public static final BitSet FOLLOW_over_clause_in_pattern_source2809 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_FROM_in_pattern_source2819 = new BitSet(new long[]{0x0000000000000000L,0x0000003000080000L});
    public static final BitSet FOLLOW_accumulate_statement_in_pattern_source2839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_pattern_source2855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entrypoint_statement_in_pattern_source2872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_pattern_source2888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause2920 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2925 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_COMMA_in_over_clause2932 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2937 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_over_elements2952 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_over_elements2959 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_over_elements2968 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_over_elements2975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement3001 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement3010 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_lhs_or_in_accumulate_statement3018 = new BitSet(new long[]{0x0000000000000000L,0x0000000001080000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement3023 = new BitSet(new long[]{0x0000000000000000L,0x0000000001080000L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_statement3033 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_statement3039 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement3047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_init_key_in_accumulate_init_clause3093 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3103 = new BitSet(new long[]{0x0000000000000000L,0x0000000001080000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause3108 = new BitSet(new long[]{0x0000000000000000L,0x0000000001080000L});
    public static final BitSet FOLLOW_action_key_in_accumulate_init_clause3119 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3123 = new BitSet(new long[]{0x0000000000000000L,0x0000000001080000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause3128 = new BitSet(new long[]{0x0000000000000000L,0x0000000001080000L});
    public static final BitSet FOLLOW_reverse_key_in_accumulate_init_clause3140 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3144 = new BitSet(new long[]{0x0000000000000000L,0x0000000001080000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause3149 = new BitSet(new long[]{0x0000000000000000L,0x0000000001080000L});
    public static final BitSet FOLLOW_result_key_in_accumulate_init_clause3165 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk3229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_paren_chunk_data3253 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_accumulate_paren_chunk_data3265 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk_data3281 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_paren_chunk_data3292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause3308 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_id_clause3314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement3336 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement3345 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_pattern_source_in_collect_statement3352 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement3357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entry_point_key_in_entrypoint_statement3384 = new BitSet(new long[]{0x0000000000000000L,0x0000000000480000L});
    public static final BitSet FOLLOW_entrypoint_id_in_entrypoint_statement3392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entrypoint_id3418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_entrypoint_id3435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source3455 = new BitSet(new long[]{0x0000000000000002L,0x0000000000900000L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source3470 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_expression_chain_in_from_source3477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain3510 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_expression_chain3517 = new BitSet(new long[]{0x0000000000000002L,0x0000800000900000L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain3533 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain3547 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain3558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern3591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern3604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_fact_binding3624 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_fact_in_fact_binding3630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding3637 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_fact_binding_expression_in_fact_binding3645 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding3653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression3694 = new BitSet(new long[]{0x0000000000000002L,0x0000000100080000L});
    public static final BitSet FOLLOW_or_key_in_fact_binding_expression3706 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3712 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression3717 = new BitSet(new long[]{0x0000000000000002L,0x0000000100080000L});
    public static final BitSet FOLLOW_pattern_type_in_fact3757 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact3762 = new BitSet(new long[]{0x0000000000000000L,0x0000000002880000L});
    public static final BitSet FOLLOW_constraints_in_fact3773 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact3779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints3813 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_COMMA_in_constraints3817 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_constraint_in_constraints3824 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_or_constr_in_constraint3838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3849 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr3853 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3860 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3875 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr3879 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3886 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_eval_key_in_unary_constr3919 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_unary_constr3922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr3927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr3933 = new BitSet(new long[]{0x0000000000000000L,0x0000000000880000L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr3943 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr3948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_field_constraint3968 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3970 = new BitSet(new long[]{0x0000000000000002L,0x00001FC000880000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_field_constraint3983 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_field_constraint3987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint4041 = new BitSet(new long[]{0x0000000000000000L,0x00001F8000880000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint4043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_label4068 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_label4075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective4096 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective4102 = new BitSet(new long[]{0x0000000000000000L,0x00001F8000880000L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective4110 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective4131 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective4137 = new BitSet(new long[]{0x0000000000000000L,0x00001F8000880000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective4144 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression4172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression4177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression4182 = new BitSet(new long[]{0x0000000000000000L,0x00001F8000880000L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression4191 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression4196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUAL_in_simple_operator4231 = new BitSet(new long[]{0x0000000000000000L,0x00006000C0C80000L});
    public static final BitSet FOLLOW_GREATER_in_simple_operator4239 = new BitSet(new long[]{0x0000000000000000L,0x00006000C0C80000L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_simple_operator4247 = new BitSet(new long[]{0x0000000000000000L,0x00006000C0C80000L});
    public static final BitSet FOLLOW_LESS_in_simple_operator4255 = new BitSet(new long[]{0x0000000000000000L,0x00006000C0C80000L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_simple_operator4263 = new BitSet(new long[]{0x0000000000000000L,0x00006000C0C80000L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_simple_operator4271 = new BitSet(new long[]{0x0000000000000000L,0x00006000C0C80000L});
    public static final BitSet FOLLOW_not_key_in_simple_operator4279 = new BitSet(new long[]{0x0000000000000000L,0x00001F8000080000L});
    public static final BitSet FOLLOW_operator_key_in_simple_operator4286 = new BitSet(new long[]{0x0000000000000000L,0x0000E000C0C80000L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator4289 = new BitSet(new long[]{0x0000000000000000L,0x00006000C0C80000L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator4301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_compound_operator4323 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_not_key_in_compound_operator4328 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_in_key_in_compound_operator4330 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator4341 = new BitSet(new long[]{0x0000000000000000L,0x00006000C0C80000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4349 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator4353 = new BitSet(new long[]{0x0000000000000000L,0x00006000C0C80000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4358 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator4366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_operator_key4397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_neg_operator_key4442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_expression_value4479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value4484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_value4490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint4509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint4516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint4523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint4530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint4537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pattern_type4552 = new BitSet(new long[]{0x0000000000000002L,0x0000800000100000L});
    public static final BitSet FOLLOW_DOT_in_pattern_type4558 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_pattern_type4562 = new BitSet(new long[]{0x0000000000000002L,0x0000800000100000L});
    public static final BitSet FOLLOW_dimension_definition_in_pattern_type4577 = new BitSet(new long[]{0x0000000000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_ID_in_data_type4605 = new BitSet(new long[]{0x0000000000000002L,0x0000800000100000L});
    public static final BitSet FOLLOW_DOT_in_data_type4611 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_data_type4615 = new BitSet(new long[]{0x0000000000000002L,0x0000800000100000L});
    public static final BitSet FOLLOW_dimension_definition_in_data_type4620 = new BitSet(new long[]{0x0000000000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dimension_definition4649 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dimension_definition4656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4670 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_accessor_path4674 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4678 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_ID_in_accessor_element4702 = new BitSet(new long[]{0x0000000000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_square_chunk_in_accessor_element4708 = new BitSet(new long[]{0x0000000000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_rhs_chunk_data_in_rhs_chunk4737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk_data4756 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_not_end_key_in_rhs_chunk_data4765 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_end_key_in_rhs_chunk_data4771 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMICOLON_in_rhs_chunk_data4776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk4795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk_data4818 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk_data4830 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk_data4846 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk_data4857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk4878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk_data4902 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk_data4914 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk_data4930 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk_data4941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk4962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk_data4985 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk_data4997 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk_data5012 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0xFFFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk_data5023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key5047 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key5051 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key5055 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key5059 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key5063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_effective_key5095 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_effective_key5099 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_date_effective_key5103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_expires_key5135 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_expires_key5139 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_date_expires_key5143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_no_loop_key5175 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_MISC_in_no_loop_key5179 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_no_loop_key5183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key5215 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_MISC_in_auto_focus_key5219 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key5223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_activation_group_key5255 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_MISC_in_activation_group_key5259 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_activation_group_key5263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key5295 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_MISC_in_agenda_group_key5299 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key5303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key5335 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_MISC_in_ruleflow_group_key5339 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key5343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entry_point_key5375 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_MISC_in_entry_point_key5379 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_entry_point_key5383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_timer_key5412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_duration_key5440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_calendars_key5467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_key5494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_key5521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dialect_key5548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_salience_key5575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enabled_key5602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_attributes_key5629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_key5656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_extend_key5683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_key5710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_key5737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_declare_key5764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_key5791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_key5818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_eval_key5845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key5872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key5899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_or_key5926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_and_key5953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_exists_key5980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_forall_key6007 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_action_key6034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_reverse_key6061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_result_key6088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_end_key6115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_init_key6162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred1_DRL5x2143 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_or_key_in_synpred1_DRL5x2145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_key_in_synpred2_DRL5x2212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred2_DRL5x2214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred3_DRL5x2271 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_and_key_in_synpred3_DRL5x2273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred4_DRL5x2341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred4_DRL5x2343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_synpred5_DRL5x2466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred6_DRL5x2503 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_or_key_in_synpred6_DRL5x2506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred6_DRL5x2508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred7_DRL5x2631 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_or_key_in_synpred7_DRL5x2634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred7_DRL5x2636 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred8_DRL5x3464 = new BitSet(new long[]{0x0000000000000002L});

}