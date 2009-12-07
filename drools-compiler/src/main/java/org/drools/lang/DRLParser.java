// $ANTLR 3.1.1 src/main/resources/org/drools/lang/DRL.g 2009-12-07 14:22:58

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TEMPLATE_ID", "VT_TYPE_DECLARE_ID", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_SLOT_ID", "VT_SLOT", "VT_RULE_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_TIMER", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_RULE", "VK_EXTEND", "VK_IMPORT", "VK_PACKAGE", "VK_TEMPLATE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "VK_OPERATOR", "VK_END", "VK_INIT", "SEMICOLON", "ID", "DOT", "DOT_STAR", "STRING", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "AT", "COLON", "EQUALS", "WHEN", "BOOL", "INT", "DOUBLE_PIPE", "DOUBLE_AMPER", "FROM", "OVER", "ACCUMULATE", "COLLECT", "ARROW", "EQUAL", "GREATER", "GREATER_EQUAL", "LESS", "LESS_EQUAL", "NOT_EQUAL", "FLOAT", "NULL", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "MISC", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart"
    };
    public static final int COMMA=87;
    public static final int VT_PATTERN_TYPE=39;
    public static final int VT_ACCUMULATE_ID_CLAUSE=28;
    public static final int VK_DIALECT=54;
    public static final int VK_FUNCTION=65;
    public static final int HexDigit=119;
    public static final int VK_ATTRIBUTES=57;
    public static final int VT_EXPRESSION_CHAIN=30;
    public static final int MISC=115;
    public static final int VT_AND_PREFIX=23;
    public static final int VK_QUERY=63;
    public static final int THEN=112;
    public static final int VK_AUTO_FOCUS=49;
    public static final int DOT=83;
    public static final int VK_IMPORT=60;
    public static final int VT_SLOT=15;
    public static final int VT_PACKAGE_ID=40;
    public static final int LEFT_SQUARE=110;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=122;
    public static final int VK_TIMER=53;
    public static final int VT_DATA_TYPE=38;
    public static final int VT_FACT=6;
    public static final int LEFT_CURLY=113;
    public static final int AT=89;
    public static final int DOUBLE_AMPER=96;
    public static final int LEFT_PAREN=86;
    public static final int IdentifierPart=126;
    public static final int VT_QUERY_ID=9;
    public static final int VT_ACCESSOR_PATH=36;
    public static final int VT_LABEL=8;
    public static final int WHEN=92;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int WS=117;
    public static final int VT_FIELD=35;
    public static final int VK_SALIENCE=55;
    public static final int OVER=98;
    public static final int VK_AND=72;
    public static final int STRING=85;
    public static final int VT_ACCESSOR_ELEMENT=37;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=27;
    public static final int VK_GLOBAL=66;
    public static final int VK_REVERSE=76;
    public static final int VT_BEHAVIOR=21;
    public static final int VT_SQUARE_CHUNK=19;
    public static final int VK_FORALL=74;
    public static final int VT_PAREN_CHUNK=20;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int COLLECT=100;
    public static final int VK_ENABLED=56;
    public static final int VK_RESULT=77;
    public static final int EQUALS=91;
    public static final int UnicodeEscape=120;
    public static final int VK_PACKAGE=61;
    public static final int VT_RULE_ID=12;
    public static final int EQUAL=102;
    public static final int VK_NO_LOOP=48;
    public static final int IdentifierStart=125;
    public static final int SEMICOLON=81;
    public static final int VK_TEMPLATE=62;
    public static final int VT_AND_IMPLICIT=22;
    public static final int NULL=109;
    public static final int COLON=90;
    public static final int MULTI_LINE_COMMENT=124;
    public static final int VT_RULE_ATTRIBUTES=16;
    public static final int RIGHT_SQUARE=111;
    public static final int VK_AGENDA_GROUP=51;
    public static final int VT_FACT_OR=33;
    public static final int VK_NOT=69;
    public static final int VK_DATE_EXPIRES=46;
    public static final int ARROW=101;
    public static final int FLOAT=108;
    public static final int VK_EXTEND=59;
    public static final int VT_SLOT_ID=14;
    public static final int VT_CURLY_CHUNK=18;
    public static final int VT_OR_PREFIX=24;
    public static final int DOUBLE_PIPE=95;
    public static final int VK_END=79;
    public static final int LESS=105;
    public static final int VT_TYPE_DECLARE_ID=11;
    public static final int VT_PATTERN=31;
    public static final int VK_DATE_EFFECTIVE=45;
    public static final int EscapeSequence=118;
    public static final int VK_EXISTS=73;
    public static final int INT=94;
    public static final int VT_BIND_FIELD=34;
    public static final int VK_RULE=58;
    public static final int VK_EVAL=67;
    public static final int GREATER=103;
    public static final int VT_FACT_BINDING=32;
    public static final int FROM=97;
    public static final int ID=82;
    public static final int NOT_EQUAL=107;
    public static final int RIGHT_CURLY=114;
    public static final int VK_OPERATOR=78;
    public static final int BOOL=93;
    public static final int VT_AND_INFIX=25;
    public static final int VT_PARAM_LIST=44;
    public static final int VK_ENTRY_POINT=68;
    public static final int VT_FROM_SOURCE=29;
    public static final int VK_LOCK_ON_ACTIVE=47;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VK_IN=70;
    public static final int VT_RHS_CHUNK=17;
    public static final int GREATER_EQUAL=104;
    public static final int VT_OR_INFIX=26;
    public static final int DOT_STAR=84;
    public static final int VK_OR=71;
    public static final int VT_GLOBAL_ID=42;
    public static final int LESS_EQUAL=106;
    public static final int ACCUMULATE=99;
    public static final int VK_RULEFLOW_GROUP=52;
    public static final int VT_FUNCTION_ID=43;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int EOL=116;
    public static final int VT_IMPORT_ID=41;
    public static final int VK_ACTIVATION_GROUP=50;
    public static final int VK_INIT=80;
    public static final int OctalEscape=121;
    public static final int VK_ACTION=75;
    public static final int RIGHT_PAREN=88;
    public static final int VT_TEMPLATE_ID=10;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=123;
    public static final int VK_DECLARE=64;

    // delegates
    // delegators


        public DRLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DRLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return DRLParser.tokenNames; }
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/DRL.g"; }


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
    // src/main/resources/org/drools/lang/DRL.g:395:1: compilation_unit : ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) ;
    public final DRLParser.compilation_unit_return compilation_unit() throws RecognitionException {
        DRLParser.compilation_unit_return retval = new DRLParser.compilation_unit_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF3=null;
        DRLParser.package_statement_return package_statement1 = null;

        DRLParser.statement_return statement2 = null;


        Object EOF3_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_package_statement=new RewriteRuleSubtreeStream(adaptor,"rule package_statement");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // src/main/resources/org/drools/lang/DRL.g:396:2: ( ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) )
            // src/main/resources/org/drools/lang/DRL.g:396:4: ( package_statement )? ( statement )* EOF
            {
            // src/main/resources/org/drools/lang/DRL.g:396:4: ( package_statement )?
            int alt1=2;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:396:4: package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_compilation_unit384);
                    package_statement1=package_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_package_statement.add(package_statement1.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL.g:397:3: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:397:3: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit389);
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

            EOF3=(Token)match(input,EOF,FOLLOW_EOF_in_compilation_unit394); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF3);



            // AST REWRITE
            // elements: statement, package_statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 399:3: -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
            {
                // src/main/resources/org/drools/lang/DRL.g:399:6: ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_COMPILATION_UNIT, "VT_COMPILATION_UNIT"), root_1);

                // src/main/resources/org/drools/lang/DRL.g:399:28: ( package_statement )?
                if ( stream_package_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_package_statement.nextTree());

                }
                stream_package_statement.reset();
                // src/main/resources/org/drools/lang/DRL.g:399:47: ( statement )*
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
    // src/main/resources/org/drools/lang/DRL.g:438:1: package_statement : package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) ;
    public final DRLParser.package_statement_return package_statement() throws RecognitionException {
        DRLParser.package_statement_return retval = new DRLParser.package_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON6=null;
        DRLParser.package_key_return package_key4 = null;

        DRLParser.package_id_return package_id5 = null;


        Object SEMICOLON6_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_package_key=new RewriteRuleSubtreeStream(adaptor,"rule package_key");
        RewriteRuleSubtreeStream stream_package_id=new RewriteRuleSubtreeStream(adaptor,"rule package_id");
         pushParaphrases(DroolsParaphraseTypes.PACKAGE); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.PACKAGE); 
        try {
            // src/main/resources/org/drools/lang/DRL.g:441:2: ( package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) )
            // src/main/resources/org/drools/lang/DRL.g:441:4: package_key package_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_package_key_in_package_statement449);
            package_key4=package_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_key.add(package_key4.getTree());
            pushFollow(FOLLOW_package_id_in_package_statement453);
            package_id5=package_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_id.add(package_id5.getTree());
            // src/main/resources/org/drools/lang/DRL.g:442:14: ( SEMICOLON )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==SEMICOLON) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:442:14: SEMICOLON
                    {
                    SEMICOLON6=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_package_statement455); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON6);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON6, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: package_id, package_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 444:3: -> ^( package_key package_id )
            {
                // src/main/resources/org/drools/lang/DRL.g:444:6: ^( package_key package_id )
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
    // src/main/resources/org/drools/lang/DRL.g:447:1: package_id : id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) ;
    public final DRLParser.package_id_return package_id() throws RecognitionException {
        DRLParser.package_id_return retval = new DRLParser.package_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // src/main/resources/org/drools/lang/DRL.g:448:2: (id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) )
            // src/main/resources/org/drools/lang/DRL.g:448:4: id+= ID (id+= DOT id+= ID )*
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_package_id482); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // src/main/resources/org/drools/lang/DRL.g:448:11: (id+= DOT id+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:448:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_package_id488); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_package_id492); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 451:3: -> ^( VT_PACKAGE_ID ( ID )+ )
            {
                // src/main/resources/org/drools/lang/DRL.g:451:6: ^( VT_PACKAGE_ID ( ID )+ )
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
    // src/main/resources/org/drools/lang/DRL.g:454:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );
    public final DRLParser.statement_return statement() throws RecognitionException {
        DRLParser.statement_return retval = new DRLParser.statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.rule_attribute_return rule_attribute7 = null;

        DRLParser.function_import_statement_return function_import_statement8 = null;

        DRLParser.import_statement_return import_statement9 = null;

        DRLParser.global_return global10 = null;

        DRLParser.function_return function11 = null;

        DRLParser.template_return template12 = null;

        DRLParser.type_declaration_return type_declaration13 = null;

        DRLParser.rule_return rule14 = null;

        DRLParser.query_return query15 = null;



        try {
            // src/main/resources/org/drools/lang/DRL.g:457:3: ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query )
            int alt5=9;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:457:5: rule_attribute
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_attribute_in_statement530);
                    rule_attribute7=rule_attribute();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rule_attribute7.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:458:3: {...}? => function_import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !(((validateLT(1, "import") && validateLT(2, "function") ))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, \"import\") && validateLT(2, \"function\") )");
                    }
                    pushFollow(FOLLOW_function_import_statement_in_statement537);
                    function_import_statement8=function_import_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function_import_statement8.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:459:4: import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_import_statement_in_statement543);
                    import_statement9=import_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, import_statement9.getTree());

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRL.g:460:4: global
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_global_in_statement549);
                    global10=global();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, global10.getTree());

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRL.g:461:4: function
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_function_in_statement555);
                    function11=function();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function11.getTree());

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRL.g:462:4: {...}? => template
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !(((validateLT(1, DroolsSoftKeywords.TEMPLATE)))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, DroolsSoftKeywords.TEMPLATE))");
                    }
                    pushFollow(FOLLOW_template_in_statement563);
                    template12=template();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, template12.getTree());

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRL.g:463:4: {...}? => type_declaration
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !(((validateLT(1, DroolsSoftKeywords.DECLARE)))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, DroolsSoftKeywords.DECLARE))");
                    }
                    pushFollow(FOLLOW_type_declaration_in_statement571);
                    type_declaration13=type_declaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type_declaration13.getTree());

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DRL.g:464:4: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_statement576);
                    rule14=rule();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rule14.getTree());

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DRL.g:465:4: query
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_query_in_statement581);
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
    // src/main/resources/org/drools/lang/DRL.g:468:1: import_statement : import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) ;
    public final DRLParser.import_statement_return import_statement() throws RecognitionException {
        DRLParser.import_statement_return retval = new DRLParser.import_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON18=null;
        DRLParser.import_key_return import_key16 = null;

        DRLParser.import_name_return import_name17 = null;


        Object SEMICOLON18_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_import_key=new RewriteRuleSubtreeStream(adaptor,"rule import_key");
        RewriteRuleSubtreeStream stream_import_name=new RewriteRuleSubtreeStream(adaptor,"rule import_name");
         pushParaphrases(DroolsParaphraseTypes.IMPORT); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.IMPORT_STATEMENT);  
        try {
            // src/main/resources/org/drools/lang/DRL.g:471:2: ( import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) )
            // src/main/resources/org/drools/lang/DRL.g:471:4: import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )?
            {
            pushFollow(FOLLOW_import_key_in_import_statement603);
            import_key16=import_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_key.add(import_key16.getTree());
            pushFollow(FOLLOW_import_name_in_import_statement605);
            import_name17=import_name(DroolsParaphraseTypes.IMPORT);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_name.add(import_name17.getTree());
            // src/main/resources/org/drools/lang/DRL.g:471:57: ( SEMICOLON )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==SEMICOLON) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:471:57: SEMICOLON
                    {
                    SEMICOLON18=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_import_statement608); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON18);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON18, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: import_name, import_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 473:3: -> ^( import_key import_name )
            {
                // src/main/resources/org/drools/lang/DRL.g:473:6: ^( import_key import_name )
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
    // src/main/resources/org/drools/lang/DRL.g:476:1: function_import_statement : imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) ;
    public final DRLParser.function_import_statement_return function_import_statement() throws RecognitionException {
        DRLParser.function_import_statement_return retval = new DRLParser.function_import_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON21=null;
        DRLParser.import_key_return imp = null;

        DRLParser.function_key_return function_key19 = null;

        DRLParser.import_name_return import_name20 = null;


        Object SEMICOLON21_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_import_key=new RewriteRuleSubtreeStream(adaptor,"rule import_key");
        RewriteRuleSubtreeStream stream_function_key=new RewriteRuleSubtreeStream(adaptor,"rule function_key");
        RewriteRuleSubtreeStream stream_import_name=new RewriteRuleSubtreeStream(adaptor,"rule import_name");
         pushParaphrases(DroolsParaphraseTypes.FUNCTION_IMPORT); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.FUNCTION_IMPORT_STATEMENT); 
        try {
            // src/main/resources/org/drools/lang/DRL.g:479:2: (imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) )
            // src/main/resources/org/drools/lang/DRL.g:479:4: imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )?
            {
            pushFollow(FOLLOW_import_key_in_function_import_statement646);
            imp=import_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_key.add(imp.getTree());
            pushFollow(FOLLOW_function_key_in_function_import_statement648);
            function_key19=function_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_function_key.add(function_key19.getTree());
            pushFollow(FOLLOW_import_name_in_function_import_statement650);
            import_name20=import_name(DroolsParaphraseTypes.FUNCTION_IMPORT);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_name.add(import_name20.getTree());
            // src/main/resources/org/drools/lang/DRL.g:479:83: ( SEMICOLON )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==SEMICOLON) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:479:83: SEMICOLON
                    {
                    SEMICOLON21=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_function_import_statement653); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON21);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON21, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: import_name, function_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 481:3: -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
            {
                // src/main/resources/org/drools/lang/DRL.g:481:6: ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
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
    // src/main/resources/org/drools/lang/DRL.g:484:1: import_name[DroolsParaphraseTypes importType] : id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) ;
    public final DRLParser.import_name_return import_name(DroolsParaphraseTypes importType) throws RecognitionException {
        DRLParser.import_name_return retval = new DRLParser.import_name_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_DOT_STAR=new RewriteRuleTokenStream(adaptor,"token DOT_STAR");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // src/main/resources/org/drools/lang/DRL.g:485:2: (id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) )
            // src/main/resources/org/drools/lang/DRL.g:485:4: id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )?
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_import_name687); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // src/main/resources/org/drools/lang/DRL.g:485:11: (id+= DOT id+= ID )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==DOT) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:485:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_import_name693); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_import_name697); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DRL.g:485:33: (id+= DOT_STAR )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==DOT_STAR) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:485:33: id+= DOT_STAR
                    {
                    id=(Token)match(input,DOT_STAR,FOLLOW_DOT_STAR_in_import_name704); if (state.failed) return retval; 
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
            // elements: ID, DOT_STAR
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 488:3: -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
            {
                // src/main/resources/org/drools/lang/DRL.g:488:6: ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
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
                // src/main/resources/org/drools/lang/DRL.g:488:25: ( DOT_STAR )?
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
    // src/main/resources/org/drools/lang/DRL.g:491:1: global : global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) ;
    public final DRLParser.global_return global() throws RecognitionException {
        DRLParser.global_return retval = new DRLParser.global_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON25=null;
        DRLParser.global_key_return global_key22 = null;

        DRLParser.data_type_return data_type23 = null;

        DRLParser.global_id_return global_id24 = null;


        Object SEMICOLON25_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        RewriteRuleSubtreeStream stream_global_id=new RewriteRuleSubtreeStream(adaptor,"rule global_id");
        RewriteRuleSubtreeStream stream_global_key=new RewriteRuleSubtreeStream(adaptor,"rule global_key");
         pushParaphrases(DroolsParaphraseTypes.GLOBAL);  if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.GLOBAL); 
        try {
            // src/main/resources/org/drools/lang/DRL.g:494:2: ( global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) )
            // src/main/resources/org/drools/lang/DRL.g:494:4: global_key data_type global_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_global_key_in_global744);
            global_key22=global_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_global_key.add(global_key22.getTree());
            pushFollow(FOLLOW_data_type_in_global746);
            data_type23=data_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_data_type.add(data_type23.getTree());
            pushFollow(FOLLOW_global_id_in_global748);
            global_id24=global_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_global_id.add(global_id24.getTree());
            // src/main/resources/org/drools/lang/DRL.g:494:35: ( SEMICOLON )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==SEMICOLON) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:494:35: SEMICOLON
                    {
                    SEMICOLON25=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_global750); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON25);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON25, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: global_id, data_type, global_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 496:3: -> ^( global_key data_type global_id )
            {
                // src/main/resources/org/drools/lang/DRL.g:496:6: ^( global_key data_type global_id )
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
    // src/main/resources/org/drools/lang/DRL.g:499:1: global_id : id= ID -> VT_GLOBAL_ID[$id] ;
    public final DRLParser.global_id_return global_id() throws RecognitionException {
        DRLParser.global_id_return retval = new DRLParser.global_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:500:2: (id= ID -> VT_GLOBAL_ID[$id] )
            // src/main/resources/org/drools/lang/DRL.g:500:4: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_global_id779); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 503:3: -> VT_GLOBAL_ID[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:506:1: function : function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) ;
    public final DRLParser.function_return function() throws RecognitionException {
        DRLParser.function_return retval = new DRLParser.function_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.function_key_return function_key26 = null;

        DRLParser.data_type_return data_type27 = null;

        DRLParser.function_id_return function_id28 = null;

        DRLParser.parameters_return parameters29 = null;

        DRLParser.curly_chunk_return curly_chunk30 = null;


        RewriteRuleSubtreeStream stream_parameters=new RewriteRuleSubtreeStream(adaptor,"rule parameters");
        RewriteRuleSubtreeStream stream_function_key=new RewriteRuleSubtreeStream(adaptor,"rule function_key");
        RewriteRuleSubtreeStream stream_curly_chunk=new RewriteRuleSubtreeStream(adaptor,"rule curly_chunk");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        RewriteRuleSubtreeStream stream_function_id=new RewriteRuleSubtreeStream(adaptor,"rule function_id");
         pushParaphrases(DroolsParaphraseTypes.FUNCTION); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.FUNCTION);  
        try {
            // src/main/resources/org/drools/lang/DRL.g:509:2: ( function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) )
            // src/main/resources/org/drools/lang/DRL.g:509:4: function_key ( data_type )? function_id parameters curly_chunk
            {
            pushFollow(FOLLOW_function_key_in_function811);
            function_key26=function_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_function_key.add(function_key26.getTree());
            // src/main/resources/org/drools/lang/DRL.g:509:17: ( data_type )?
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
                    // src/main/resources/org/drools/lang/DRL.g:509:17: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function813);
                    data_type27=data_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_data_type.add(data_type27.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_function_id_in_function816);
            function_id28=function_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_function_id.add(function_id28.getTree());
            pushFollow(FOLLOW_parameters_in_function818);
            parameters29=parameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_parameters.add(parameters29.getTree());
            pushFollow(FOLLOW_curly_chunk_in_function820);
            curly_chunk30=curly_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_curly_chunk.add(curly_chunk30.getTree());


            // AST REWRITE
            // elements: parameters, data_type, function_key, curly_chunk, function_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 510:3: -> ^( function_key ( data_type )? function_id parameters curly_chunk )
            {
                // src/main/resources/org/drools/lang/DRL.g:510:6: ^( function_key ( data_type )? function_id parameters curly_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_function_key.nextNode(), root_1);

                // src/main/resources/org/drools/lang/DRL.g:510:21: ( data_type )?
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
    // src/main/resources/org/drools/lang/DRL.g:513:1: function_id : id= ID -> VT_FUNCTION_ID[$id] ;
    public final DRLParser.function_id_return function_id() throws RecognitionException {
        DRLParser.function_id_return retval = new DRLParser.function_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:514:2: (id= ID -> VT_FUNCTION_ID[$id] )
            // src/main/resources/org/drools/lang/DRL.g:514:4: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_function_id850); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 517:3: -> VT_FUNCTION_ID[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:520:1: query : query_key query_id ( parameters )? normal_lhs_block end= end_key ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block end_key ) ;
    public final DRLParser.query_return query() throws RecognitionException {
        DRLParser.query_return retval = new DRLParser.query_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON35=null;
        DRLParser.end_key_return end = null;

        DRLParser.query_key_return query_key31 = null;

        DRLParser.query_id_return query_id32 = null;

        DRLParser.parameters_return parameters33 = null;

        DRLParser.normal_lhs_block_return normal_lhs_block34 = null;


        Object SEMICOLON35_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_parameters=new RewriteRuleSubtreeStream(adaptor,"rule parameters");
        RewriteRuleSubtreeStream stream_query_key=new RewriteRuleSubtreeStream(adaptor,"rule query_key");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        RewriteRuleSubtreeStream stream_query_id=new RewriteRuleSubtreeStream(adaptor,"rule query_id");
        RewriteRuleSubtreeStream stream_end_key=new RewriteRuleSubtreeStream(adaptor,"rule end_key");
         pushParaphrases(DroolsParaphraseTypes.QUERY); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.QUERY); 
        try {
            // src/main/resources/org/drools/lang/DRL.g:523:2: ( query_key query_id ( parameters )? normal_lhs_block end= end_key ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block end_key ) )
            // src/main/resources/org/drools/lang/DRL.g:523:4: query_key query_id ( parameters )? normal_lhs_block end= end_key ( SEMICOLON )?
            {
            pushFollow(FOLLOW_query_key_in_query882);
            query_key31=query_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_query_key.add(query_key31.getTree());
            pushFollow(FOLLOW_query_id_in_query884);
            query_id32=query_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_query_id.add(query_id32.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER);	
            }
            // src/main/resources/org/drools/lang/DRL.g:525:3: ( parameters )?
            int alt12=2;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:525:3: parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query892);
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
            pushFollow(FOLLOW_normal_lhs_block_in_query901);
            normal_lhs_block34=normal_lhs_block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block34.getTree());
            pushFollow(FOLLOW_end_key_in_query908);
            end=end_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_end_key.add(end.getTree());
            // src/main/resources/org/drools/lang/DRL.g:528:15: ( SEMICOLON )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==SEMICOLON) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:528:15: SEMICOLON
                    {
                    SEMICOLON35=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_query910); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON35);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON35, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: normal_lhs_block, end_key, query_id, parameters, query_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 530:3: -> ^( query_key query_id ( parameters )? normal_lhs_block end_key )
            {
                // src/main/resources/org/drools/lang/DRL.g:530:6: ^( query_key query_id ( parameters )? normal_lhs_block end_key )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_query_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_query_id.nextTree());
                // src/main/resources/org/drools/lang/DRL.g:530:27: ( parameters )?
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
    // src/main/resources/org/drools/lang/DRL.g:533:1: query_id : (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] );
    public final DRLParser.query_id_return query_id() throws RecognitionException {
        DRLParser.query_id_return retval = new DRLParser.query_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:534:2: (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] )
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
                    // src/main/resources/org/drools/lang/DRL.g:534:5: id= ID
                    {
                    id=(Token)match(input,ID,FOLLOW_ID_in_query_id945); if (state.failed) return retval; 
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
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 536:65: -> VT_QUERY_ID[$id]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_QUERY_ID, id));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:537:5: id= STRING
                    {
                    id=(Token)match(input,STRING,FOLLOW_STRING_in_query_id961); if (state.failed) return retval; 
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
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 539:65: -> VT_QUERY_ID[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:542:1: parameters : LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) ;
    public final DRLParser.parameters_return parameters() throws RecognitionException {
        DRLParser.parameters_return retval = new DRLParser.parameters_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN36=null;
        Token COMMA38=null;
        Token RIGHT_PAREN40=null;
        DRLParser.param_definition_return param_definition37 = null;

        DRLParser.param_definition_return param_definition39 = null;


        Object LEFT_PAREN36_tree=null;
        Object COMMA38_tree=null;
        Object RIGHT_PAREN40_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_param_definition=new RewriteRuleSubtreeStream(adaptor,"rule param_definition");
        try {
            // src/main/resources/org/drools/lang/DRL.g:543:2: ( LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) )
            // src/main/resources/org/drools/lang/DRL.g:543:4: LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN
            {
            LEFT_PAREN36=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parameters980); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN36);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN36, DroolsEditorType.SYMBOL);	
            }
            // src/main/resources/org/drools/lang/DRL.g:544:4: ( param_definition ( COMMA param_definition )* )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==ID) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:544:6: param_definition ( COMMA param_definition )*
                    {
                    pushFollow(FOLLOW_param_definition_in_parameters989);
                    param_definition37=param_definition();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_param_definition.add(param_definition37.getTree());
                    // src/main/resources/org/drools/lang/DRL.g:544:23: ( COMMA param_definition )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==COMMA) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRL.g:544:24: COMMA param_definition
                    	    {
                    	    COMMA38=(Token)match(input,COMMA,FOLLOW_COMMA_in_parameters992); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA38);

                    	    if ( state.backtracking==0 ) {
                    	      	emit(COMMA38, DroolsEditorType.SYMBOL);	
                    	    }
                    	    pushFollow(FOLLOW_param_definition_in_parameters996);
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

            RIGHT_PAREN40=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parameters1005); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN40);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN40, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: param_definition, RIGHT_PAREN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 546:3: -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
            {
                // src/main/resources/org/drools/lang/DRL.g:546:6: ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_PARAM_LIST, "VT_PARAM_LIST"), root_1);

                // src/main/resources/org/drools/lang/DRL.g:546:22: ( param_definition )*
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
    // src/main/resources/org/drools/lang/DRL.g:549:1: param_definition : ( data_type )? argument ;
    public final DRLParser.param_definition_return param_definition() throws RecognitionException {
        DRLParser.param_definition_return retval = new DRLParser.param_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.data_type_return data_type41 = null;

        DRLParser.argument_return argument42 = null;



        try {
            // src/main/resources/org/drools/lang/DRL.g:550:2: ( ( data_type )? argument )
            // src/main/resources/org/drools/lang/DRL.g:550:4: ( data_type )? argument
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL.g:550:4: ( data_type )?
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:550:4: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition1031);
                    data_type41=data_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, data_type41.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition1034);
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
    // src/main/resources/org/drools/lang/DRL.g:553:1: argument : ID ( dimension_definition )* ;
    public final DRLParser.argument_return argument() throws RecognitionException {
        DRLParser.argument_return retval = new DRLParser.argument_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID43=null;
        DRLParser.dimension_definition_return dimension_definition44 = null;


        Object ID43_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:554:2: ( ID ( dimension_definition )* )
            // src/main/resources/org/drools/lang/DRL.g:554:4: ID ( dimension_definition )*
            {
            root_0 = (Object)adaptor.nil();

            ID43=(Token)match(input,ID,FOLLOW_ID_in_argument1045); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID43_tree = (Object)adaptor.create(ID43);
            adaptor.addChild(root_0, ID43_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(ID43, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL.g:555:3: ( dimension_definition )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==LEFT_SQUARE) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:555:3: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_argument1051);
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
    // src/main/resources/org/drools/lang/DRL.g:558:1: type_declaration : declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key ) ;
    public final DRLParser.type_declaration_return type_declaration() throws RecognitionException {
        DRLParser.type_declaration_return retval = new DRLParser.type_declaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.declare_key_return declare_key45 = null;

        DRLParser.type_declare_id_return type_declare_id46 = null;

        DRLParser.decl_metadata_return decl_metadata47 = null;

        DRLParser.decl_field_return decl_field48 = null;

        DRLParser.end_key_return end_key49 = null;


        RewriteRuleSubtreeStream stream_decl_field=new RewriteRuleSubtreeStream(adaptor,"rule decl_field");
        RewriteRuleSubtreeStream stream_declare_key=new RewriteRuleSubtreeStream(adaptor,"rule declare_key");
        RewriteRuleSubtreeStream stream_decl_metadata=new RewriteRuleSubtreeStream(adaptor,"rule decl_metadata");
        RewriteRuleSubtreeStream stream_end_key=new RewriteRuleSubtreeStream(adaptor,"rule end_key");
        RewriteRuleSubtreeStream stream_type_declare_id=new RewriteRuleSubtreeStream(adaptor,"rule type_declare_id");
         pushParaphrases(DroolsParaphraseTypes.TYPE_DECLARE); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.TYPE_DECLARATION); 
        try {
            // src/main/resources/org/drools/lang/DRL.g:561:2: ( declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key ) )
            // src/main/resources/org/drools/lang/DRL.g:561:4: declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key
            {
            pushFollow(FOLLOW_declare_key_in_type_declaration1074);
            declare_key45=declare_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_declare_key.add(declare_key45.getTree());
            pushFollow(FOLLOW_type_declare_id_in_type_declaration1077);
            type_declare_id46=type_declare_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_type_declare_id.add(type_declare_id46.getTree());
            // src/main/resources/org/drools/lang/DRL.g:562:3: ( decl_metadata )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AT) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:562:3: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_type_declaration1081);
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

            // src/main/resources/org/drools/lang/DRL.g:563:3: ( decl_field )*
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
            	    // src/main/resources/org/drools/lang/DRL.g:563:3: decl_field
            	    {
            	    pushFollow(FOLLOW_decl_field_in_type_declaration1086);
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

            pushFollow(FOLLOW_end_key_in_type_declaration1091);
            end_key49=end_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_end_key.add(end_key49.getTree());


            // AST REWRITE
            // elements: end_key, type_declare_id, declare_key, decl_metadata, decl_field
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 565:3: -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key )
            {
                // src/main/resources/org/drools/lang/DRL.g:565:6: ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* end_key )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_declare_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_type_declare_id.nextTree());
                // src/main/resources/org/drools/lang/DRL.g:565:36: ( decl_metadata )*
                while ( stream_decl_metadata.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_metadata.nextTree());

                }
                stream_decl_metadata.reset();
                // src/main/resources/org/drools/lang/DRL.g:565:51: ( decl_field )*
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
    // src/main/resources/org/drools/lang/DRL.g:568:1: type_declare_id : id= ID -> VT_TYPE_DECLARE_ID[$id] ;
    public final DRLParser.type_declare_id_return type_declare_id() throws RecognitionException {
        DRLParser.type_declare_id_return retval = new DRLParser.type_declare_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:569:2: (id= ID -> VT_TYPE_DECLARE_ID[$id] )
            // src/main/resources/org/drools/lang/DRL.g:569:5: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_type_declare_id1123); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 571:72: -> VT_TYPE_DECLARE_ID[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:574:1: decl_metadata : AT ID ( paren_chunk )? -> ^( AT ID ( paren_chunk )? ) ;
    public final DRLParser.decl_metadata_return decl_metadata() throws RecognitionException {
        DRLParser.decl_metadata_return retval = new DRLParser.decl_metadata_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AT50=null;
        Token ID51=null;
        DRLParser.paren_chunk_return paren_chunk52 = null;


        Object AT50_tree=null;
        Object ID51_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL.g:575:2: ( AT ID ( paren_chunk )? -> ^( AT ID ( paren_chunk )? ) )
            // src/main/resources/org/drools/lang/DRL.g:575:4: AT ID ( paren_chunk )?
            {
            AT50=(Token)match(input,AT,FOLLOW_AT_in_decl_metadata1142); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AT.add(AT50);

            if ( state.backtracking==0 ) {
              	emit(AT50, DroolsEditorType.SYMBOL);	
            }
            ID51=(Token)match(input,ID,FOLLOW_ID_in_decl_metadata1150); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID51);

            if ( state.backtracking==0 ) {
              	emit(ID51, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL.g:579:3: ( paren_chunk )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==LEFT_PAREN) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:579:3: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_decl_metadata1157);
                    paren_chunk52=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk52.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: paren_chunk, AT, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 580:3: -> ^( AT ID ( paren_chunk )? )
            {
                // src/main/resources/org/drools/lang/DRL.g:580:6: ^( AT ID ( paren_chunk )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_AT.nextNode(), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // src/main/resources/org/drools/lang/DRL.g:580:14: ( paren_chunk )?
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
    // src/main/resources/org/drools/lang/DRL.g:583:1: decl_field : ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) ;
    public final DRLParser.decl_field_return decl_field() throws RecognitionException {
        DRLParser.decl_field_return retval = new DRLParser.decl_field_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID53=null;
        Token COLON55=null;
        DRLParser.decl_field_initialization_return decl_field_initialization54 = null;

        DRLParser.data_type_return data_type56 = null;

        DRLParser.decl_metadata_return decl_metadata57 = null;


        Object ID53_tree=null;
        Object COLON55_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_decl_metadata=new RewriteRuleSubtreeStream(adaptor,"rule decl_metadata");
        RewriteRuleSubtreeStream stream_decl_field_initialization=new RewriteRuleSubtreeStream(adaptor,"rule decl_field_initialization");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        try {
            // src/main/resources/org/drools/lang/DRL.g:584:2: ( ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) )
            // src/main/resources/org/drools/lang/DRL.g:584:4: ID ( decl_field_initialization )? COLON data_type ( decl_metadata )*
            {
            ID53=(Token)match(input,ID,FOLLOW_ID_in_decl_field1182); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID53);

            if ( state.backtracking==0 ) {
              	emit(ID53, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL.g:585:3: ( decl_field_initialization )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==EQUALS) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:585:3: decl_field_initialization
                    {
                    pushFollow(FOLLOW_decl_field_initialization_in_decl_field1188);
                    decl_field_initialization54=decl_field_initialization();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_decl_field_initialization.add(decl_field_initialization54.getTree());

                    }
                    break;

            }

            COLON55=(Token)match(input,COLON,FOLLOW_COLON_in_decl_field1194); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON55);

            if ( state.backtracking==0 ) {
              	emit(COLON55, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_data_type_in_decl_field1200);
            data_type56=data_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_data_type.add(data_type56.getTree());
            // src/main/resources/org/drools/lang/DRL.g:588:3: ( decl_metadata )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==AT) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:588:3: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_decl_field1204);
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
            // elements: decl_field_initialization, ID, data_type, decl_metadata
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 589:3: -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
            {
                // src/main/resources/org/drools/lang/DRL.g:589:6: ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ID.nextNode(), root_1);

                // src/main/resources/org/drools/lang/DRL.g:589:11: ( decl_field_initialization )?
                if ( stream_decl_field_initialization.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_field_initialization.nextTree());

                }
                stream_decl_field_initialization.reset();
                adaptor.addChild(root_1, stream_data_type.nextTree());
                // src/main/resources/org/drools/lang/DRL.g:589:48: ( decl_metadata )*
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
    // src/main/resources/org/drools/lang/DRL.g:592:1: decl_field_initialization : EQUALS paren_chunk -> ^( EQUALS paren_chunk ) ;
    public final DRLParser.decl_field_initialization_return decl_field_initialization() throws RecognitionException {
        DRLParser.decl_field_initialization_return retval = new DRLParser.decl_field_initialization_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS58=null;
        DRLParser.paren_chunk_return paren_chunk59 = null;


        Object EQUALS58_tree=null;
        RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL.g:593:2: ( EQUALS paren_chunk -> ^( EQUALS paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL.g:593:4: EQUALS paren_chunk
            {
            EQUALS58=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_decl_field_initialization1232); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS58);

            if ( state.backtracking==0 ) {
              	emit(EQUALS58, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_paren_chunk_in_decl_field_initialization1238);
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 595:2: -> ^( EQUALS paren_chunk )
            {
                // src/main/resources/org/drools/lang/DRL.g:595:5: ^( EQUALS paren_chunk )
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
    // src/main/resources/org/drools/lang/DRL.g:598:1: template : template_key template_id (semi1= SEMICOLON )? ( template_slot )+ end= end_key (semi2= SEMICOLON )? -> ^( template_key template_id ( template_slot )+ end_key ) ;
    public final DRLParser.template_return template() throws RecognitionException {
        DRLParser.template_return retval = new DRLParser.template_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token semi1=null;
        Token semi2=null;
        DRLParser.end_key_return end = null;

        DRLParser.template_key_return template_key60 = null;

        DRLParser.template_id_return template_id61 = null;

        DRLParser.template_slot_return template_slot62 = null;


        Object semi1_tree=null;
        Object semi2_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_template_id=new RewriteRuleSubtreeStream(adaptor,"rule template_id");
        RewriteRuleSubtreeStream stream_template_slot=new RewriteRuleSubtreeStream(adaptor,"rule template_slot");
        RewriteRuleSubtreeStream stream_template_key=new RewriteRuleSubtreeStream(adaptor,"rule template_key");
        RewriteRuleSubtreeStream stream_end_key=new RewriteRuleSubtreeStream(adaptor,"rule end_key");
         pushParaphrases(DroolsParaphraseTypes.TEMPLATE); 
        try {
            // src/main/resources/org/drools/lang/DRL.g:601:2: ( template_key template_id (semi1= SEMICOLON )? ( template_slot )+ end= end_key (semi2= SEMICOLON )? -> ^( template_key template_id ( template_slot )+ end_key ) )
            // src/main/resources/org/drools/lang/DRL.g:602:2: template_key template_id (semi1= SEMICOLON )? ( template_slot )+ end= end_key (semi2= SEMICOLON )?
            {
            if ( state.backtracking==0 ) {
              	beginSentence(DroolsSentenceType.TEMPLATE);	
            }
            pushFollow(FOLLOW_template_key_in_template1275);
            template_key60=template_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_template_key.add(template_key60.getTree());
            pushFollow(FOLLOW_template_id_in_template1277);
            template_id61=template_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_template_id.add(template_id61.getTree());
            // src/main/resources/org/drools/lang/DRL.g:604:8: (semi1= SEMICOLON )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==SEMICOLON) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:604:8: semi1= SEMICOLON
                    {
                    semi1=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1284); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(semi1);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(semi1, DroolsEditorType.SYMBOL);	
            }
            // src/main/resources/org/drools/lang/DRL.g:606:3: ( template_slot )+
            int cnt25=0;
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==ID) ) {
                    int LA25_1 = input.LA(2);

                    if ( (LA25_1==DOT||LA25_1==LEFT_SQUARE) ) {
                        alt25=1;
                    }
                    else if ( (LA25_1==ID) ) {
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


                }


                switch (alt25) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:606:3: template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template1292);
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

            pushFollow(FOLLOW_end_key_in_template1299);
            end=end_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_end_key.add(end.getTree());
            // src/main/resources/org/drools/lang/DRL.g:607:20: (semi2= SEMICOLON )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==SEMICOLON) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:607:20: semi2= SEMICOLON
                    {
                    semi2=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1303); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(semi2);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(semi2, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: template_slot, end_key, template_key, template_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 609:3: -> ^( template_key template_id ( template_slot )+ end_key )
            {
                // src/main/resources/org/drools/lang/DRL.g:609:6: ^( template_key template_id ( template_slot )+ end_key )
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
    // src/main/resources/org/drools/lang/DRL.g:612:1: template_id : (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] );
    public final DRLParser.template_id_return template_id() throws RecognitionException {
        DRLParser.template_id_return retval = new DRLParser.template_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:613:2: (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] )
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
                    // src/main/resources/org/drools/lang/DRL.g:613:5: id= ID
                    {
                    id=(Token)match(input,ID,FOLLOW_ID_in_template_id1336); if (state.failed) return retval; 
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
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 615:68: -> VT_TEMPLATE_ID[$id]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_TEMPLATE_ID, id));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:616:5: id= STRING
                    {
                    id=(Token)match(input,STRING,FOLLOW_STRING_in_template_id1352); if (state.failed) return retval; 
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
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 618:68: -> VT_TEMPLATE_ID[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:621:1: template_slot : data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) ;
    public final DRLParser.template_slot_return template_slot() throws RecognitionException {
        DRLParser.template_slot_return retval = new DRLParser.template_slot_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON65=null;
        DRLParser.data_type_return data_type63 = null;

        DRLParser.slot_id_return slot_id64 = null;


        Object SEMICOLON65_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_slot_id=new RewriteRuleSubtreeStream(adaptor,"rule slot_id");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        try {
            // src/main/resources/org/drools/lang/DRL.g:622:2: ( data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) )
            // src/main/resources/org/drools/lang/DRL.g:622:5: data_type slot_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_data_type_in_template_slot1372);
            data_type63=data_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_data_type.add(data_type63.getTree());
            pushFollow(FOLLOW_slot_id_in_template_slot1374);
            slot_id64=slot_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_slot_id.add(slot_id64.getTree());
            // src/main/resources/org/drools/lang/DRL.g:622:23: ( SEMICOLON )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==SEMICOLON) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:622:23: SEMICOLON
                    {
                    SEMICOLON65=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template_slot1376); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON65);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON65, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: slot_id, data_type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 624:3: -> ^( VT_SLOT data_type slot_id )
            {
                // src/main/resources/org/drools/lang/DRL.g:624:6: ^( VT_SLOT data_type slot_id )
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
    // src/main/resources/org/drools/lang/DRL.g:627:1: slot_id : id= ID -> VT_SLOT_ID[$id] ;
    public final DRLParser.slot_id_return slot_id() throws RecognitionException {
        DRLParser.slot_id_return retval = new DRLParser.slot_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:628:2: (id= ID -> VT_SLOT_ID[$id] )
            // src/main/resources/org/drools/lang/DRL.g:628:4: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_slot_id1405); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 630:3: -> VT_SLOT_ID[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:633:1: rule : rule_key rule_id ( extend_key rule_id )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk ) ;
    public final DRLParser.rule_return rule() throws RecognitionException {
        DRLParser.rule_return retval = new DRLParser.rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.rule_key_return rule_key66 = null;

        DRLParser.rule_id_return rule_id67 = null;

        DRLParser.extend_key_return extend_key68 = null;

        DRLParser.rule_id_return rule_id69 = null;

        DRLParser.decl_metadata_return decl_metadata70 = null;

        DRLParser.rule_attributes_return rule_attributes71 = null;

        DRLParser.when_part_return when_part72 = null;

        DRLParser.rhs_chunk_return rhs_chunk73 = null;


        RewriteRuleSubtreeStream stream_rule_key=new RewriteRuleSubtreeStream(adaptor,"rule rule_key");
        RewriteRuleSubtreeStream stream_rule_id=new RewriteRuleSubtreeStream(adaptor,"rule rule_id");
        RewriteRuleSubtreeStream stream_when_part=new RewriteRuleSubtreeStream(adaptor,"rule when_part");
        RewriteRuleSubtreeStream stream_rule_attributes=new RewriteRuleSubtreeStream(adaptor,"rule rule_attributes");
        RewriteRuleSubtreeStream stream_rhs_chunk=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk");
        RewriteRuleSubtreeStream stream_decl_metadata=new RewriteRuleSubtreeStream(adaptor,"rule decl_metadata");
        RewriteRuleSubtreeStream stream_extend_key=new RewriteRuleSubtreeStream(adaptor,"rule extend_key");
         boolean isFailed = true; pushParaphrases(DroolsParaphraseTypes.RULE); 
        try {
            // src/main/resources/org/drools/lang/DRL.g:636:2: ( rule_key rule_id ( extend_key rule_id )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk ) )
            // src/main/resources/org/drools/lang/DRL.g:637:2: rule_key rule_id ( extend_key rule_id )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk
            {
            if ( state.backtracking==0 ) {
              	beginSentence(DroolsSentenceType.RULE);	
            }
            pushFollow(FOLLOW_rule_key_in_rule1442);
            rule_key66=rule_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_key.add(rule_key66.getTree());
            pushFollow(FOLLOW_rule_id_in_rule1444);
            rule_id67=rule_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_id.add(rule_id67.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER);	
            }
            // src/main/resources/org/drools/lang/DRL.g:640:3: ( extend_key rule_id )?
            int alt29=2;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:640:4: extend_key rule_id
                    {
                    pushFollow(FOLLOW_extend_key_in_rule1453);
                    extend_key68=extend_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_extend_key.add(extend_key68.getTree());
                    pushFollow(FOLLOW_rule_id_in_rule1455);
                    rule_id69=rule_id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rule_id.add(rule_id69.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL.g:640:25: ( decl_metadata )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==AT) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:640:25: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_rule1459);
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

            // src/main/resources/org/drools/lang/DRL.g:640:40: ( rule_attributes )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))))) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:640:40: rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1462);
                    rule_attributes71=rule_attributes();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rule_attributes.add(rule_attributes71.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL.g:640:57: ( when_part )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==WHEN) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:640:57: when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule1465);
                    when_part72=when_part();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_when_part.add(when_part72.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1468);
            rhs_chunk73=rhs_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rhs_chunk.add(rhs_chunk73.getTree());


            // AST REWRITE
            // elements: rule_key, rule_attributes, rhs_chunk, rule_id, extend_key, when_part, decl_metadata, rule_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 641:3: -> ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk )
            {
                // src/main/resources/org/drools/lang/DRL.g:641:6: ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_rule_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rule_id.nextTree());
                // src/main/resources/org/drools/lang/DRL.g:641:25: ( ^( extend_key rule_id ) )?
                if ( stream_extend_key.hasNext()||stream_rule_id.hasNext() ) {
                    // src/main/resources/org/drools/lang/DRL.g:641:25: ^( extend_key rule_id )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_extend_key.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_rule_id.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_extend_key.reset();
                stream_rule_id.reset();
                // src/main/resources/org/drools/lang/DRL.g:641:48: ( decl_metadata )*
                while ( stream_decl_metadata.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_metadata.nextTree());

                }
                stream_decl_metadata.reset();
                // src/main/resources/org/drools/lang/DRL.g:641:63: ( rule_attributes )?
                if ( stream_rule_attributes.hasNext() ) {
                    adaptor.addChild(root_1, stream_rule_attributes.nextTree());

                }
                stream_rule_attributes.reset();
                // src/main/resources/org/drools/lang/DRL.g:641:80: ( when_part )?
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
    // src/main/resources/org/drools/lang/DRL.g:687:1: when_part : WHEN ( COLON )? normal_lhs_block -> WHEN normal_lhs_block ;
    public final DRLParser.when_part_return when_part() throws RecognitionException {
        DRLParser.when_part_return retval = new DRLParser.when_part_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token WHEN74=null;
        Token COLON75=null;
        DRLParser.normal_lhs_block_return normal_lhs_block76 = null;


        Object WHEN74_tree=null;
        Object COLON75_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_WHEN=new RewriteRuleTokenStream(adaptor,"token WHEN");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        try {
            // src/main/resources/org/drools/lang/DRL.g:688:2: ( WHEN ( COLON )? normal_lhs_block -> WHEN normal_lhs_block )
            // src/main/resources/org/drools/lang/DRL.g:688:5: WHEN ( COLON )? normal_lhs_block
            {
            WHEN74=(Token)match(input,WHEN,FOLLOW_WHEN_in_when_part1512); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_WHEN.add(WHEN74);

            if ( state.backtracking==0 ) {
              	emit(WHEN74, DroolsEditorType.KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL.g:689:3: ( COLON )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==COLON) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:689:3: COLON
                    {
                    COLON75=(Token)match(input,COLON,FOLLOW_COLON_in_when_part1518); if (state.failed) return retval; 
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
            pushFollow(FOLLOW_normal_lhs_block_in_when_part1528);
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 692:2: -> WHEN normal_lhs_block
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
    // src/main/resources/org/drools/lang/DRL.g:695:1: rule_id : (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] );
    public final DRLParser.rule_id_return rule_id() throws RecognitionException {
        DRLParser.rule_id_return retval = new DRLParser.rule_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:696:2: (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] )
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
                    // src/main/resources/org/drools/lang/DRL.g:696:5: id= ID
                    {
                    id=(Token)match(input,ID,FOLLOW_ID_in_rule_id1549); if (state.failed) return retval; 
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
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 698:64: -> VT_RULE_ID[$id]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_RULE_ID, id));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:699:5: id= STRING
                    {
                    id=(Token)match(input,STRING,FOLLOW_STRING_in_rule_id1565); if (state.failed) return retval; 
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
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 701:64: -> VT_RULE_ID[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:704:1: rule_attributes : ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) ;
    public final DRLParser.rule_attributes_return rule_attributes() throws RecognitionException {
        DRLParser.rule_attributes_return retval = new DRLParser.rule_attributes_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON78=null;
        Token COMMA80=null;
        DRLParser.rule_attribute_return attr = null;

        DRLParser.attributes_key_return attributes_key77 = null;

        DRLParser.rule_attribute_return rule_attribute79 = null;


        Object COLON78_tree=null;
        Object COMMA80_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_attributes_key=new RewriteRuleSubtreeStream(adaptor,"rule attributes_key");
        RewriteRuleSubtreeStream stream_rule_attribute=new RewriteRuleSubtreeStream(adaptor,"rule rule_attribute");
        try {
            // src/main/resources/org/drools/lang/DRL.g:705:2: ( ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) )
            // src/main/resources/org/drools/lang/DRL.g:705:4: ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )*
            {
            // src/main/resources/org/drools/lang/DRL.g:705:4: ( attributes_key COLON )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))))) {
                int LA35_1 = input.LA(2);

                if ( (LA35_1==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {
                    alt35=1;
                }
            }
            switch (alt35) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:705:6: attributes_key COLON
                    {
                    pushFollow(FOLLOW_attributes_key_in_rule_attributes1586);
                    attributes_key77=attributes_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_attributes_key.add(attributes_key77.getTree());
                    COLON78=(Token)match(input,COLON,FOLLOW_COLON_in_rule_attributes1588); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON78);

                    if ( state.backtracking==0 ) {
                      	emit(COLON78, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1598);
            rule_attribute79=rule_attribute();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_attribute.add(rule_attribute79.getTree());
            // src/main/resources/org/drools/lang/DRL.g:706:18: ( ( COMMA )? attr= rule_attribute )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( (LA37_0==ID||LA37_0==COMMA) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:706:20: ( COMMA )? attr= rule_attribute
            	    {
            	    // src/main/resources/org/drools/lang/DRL.g:706:20: ( COMMA )?
            	    int alt36=2;
            	    int LA36_0 = input.LA(1);

            	    if ( (LA36_0==COMMA) ) {
            	        alt36=1;
            	    }
            	    switch (alt36) {
            	        case 1 :
            	            // src/main/resources/org/drools/lang/DRL.g:706:20: COMMA
            	            {
            	            COMMA80=(Token)match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1602); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_COMMA.add(COMMA80);


            	            }
            	            break;

            	    }

            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA80, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1609);
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
            // elements: attributes_key, rule_attribute
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 707:3: -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
            {
                // src/main/resources/org/drools/lang/DRL.g:707:6: ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_RULE_ATTRIBUTES, "VT_RULE_ATTRIBUTES"), root_1);

                // src/main/resources/org/drools/lang/DRL.g:707:27: ( attributes_key )?
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
    // src/main/resources/org/drools/lang/DRL.g:710:1: rule_attribute : ( salience | no_loop | agenda_group | timer | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );
    public final DRLParser.rule_attribute_return rule_attribute() throws RecognitionException {
        DRLParser.rule_attribute_return retval = new DRLParser.rule_attribute_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.salience_return salience81 = null;

        DRLParser.no_loop_return no_loop82 = null;

        DRLParser.agenda_group_return agenda_group83 = null;

        DRLParser.timer_return timer84 = null;

        DRLParser.activation_group_return activation_group85 = null;

        DRLParser.auto_focus_return auto_focus86 = null;

        DRLParser.date_effective_return date_effective87 = null;

        DRLParser.date_expires_return date_expires88 = null;

        DRLParser.enabled_return enabled89 = null;

        DRLParser.ruleflow_group_return ruleflow_group90 = null;

        DRLParser.lock_on_active_return lock_on_active91 = null;

        DRLParser.dialect_return dialect92 = null;



         boolean isFailed = true; pushParaphrases(DroolsParaphraseTypes.RULE_ATTRIBUTE); 
        try {
            // src/main/resources/org/drools/lang/DRL.g:713:2: ( salience | no_loop | agenda_group | timer | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect )
            int alt38=12;
            alt38 = dfa38.predict(input);
            switch (alt38) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:713:4: salience
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_salience_in_rule_attribute1648);
                    salience81=salience();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, salience81.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:714:4: no_loop
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_no_loop_in_rule_attribute1654);
                    no_loop82=no_loop();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, no_loop82.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:715:4: agenda_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1659);
                    agenda_group83=agenda_group();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, agenda_group83.getTree());

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRL.g:716:4: timer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_timer_in_rule_attribute1666);
                    timer84=timer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, timer84.getTree());

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRL.g:717:4: activation_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_activation_group_in_rule_attribute1673);
                    activation_group85=activation_group();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, activation_group85.getTree());

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRL.g:718:4: auto_focus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1679);
                    auto_focus86=auto_focus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, auto_focus86.getTree());

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRL.g:719:4: date_effective
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_effective_in_rule_attribute1685);
                    date_effective87=date_effective();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, date_effective87.getTree());

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DRL.g:720:4: date_expires
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_expires_in_rule_attribute1691);
                    date_expires88=date_expires();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, date_expires88.getTree());

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DRL.g:721:4: enabled
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enabled_in_rule_attribute1697);
                    enabled89=enabled();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enabled89.getTree());

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DRL.g:722:4: ruleflow_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1703);
                    ruleflow_group90=ruleflow_group();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleflow_group90.getTree());

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/DRL.g:723:4: lock_on_active
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1709);
                    lock_on_active91=lock_on_active();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lock_on_active91.getTree());

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/lang/DRL.g:724:4: dialect
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_dialect_in_rule_attribute1714);
                    dialect92=dialect();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, dialect92.getTree());

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
    // src/main/resources/org/drools/lang/DRL.g:734:1: date_effective : date_effective_key STRING ;
    public final DRLParser.date_effective_return date_effective() throws RecognitionException {
        DRLParser.date_effective_return retval = new DRLParser.date_effective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING94=null;
        DRLParser.date_effective_key_return date_effective_key93 = null;


        Object STRING94_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:735:2: ( date_effective_key STRING )
            // src/main/resources/org/drools/lang/DRL.g:735:4: date_effective_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_effective_key_in_date_effective1729);
            date_effective_key93=date_effective_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_effective_key93.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING94=(Token)match(input,STRING,FOLLOW_STRING_in_date_effective1734); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING94_tree = (Object)adaptor.create(STRING94);
            adaptor.addChild(root_0, STRING94_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING94, DroolsEditorType.STRING_CONST );	
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
    // src/main/resources/org/drools/lang/DRL.g:739:1: date_expires : date_expires_key STRING ;
    public final DRLParser.date_expires_return date_expires() throws RecognitionException {
        DRLParser.date_expires_return retval = new DRLParser.date_expires_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING96=null;
        DRLParser.date_expires_key_return date_expires_key95 = null;


        Object STRING96_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:740:2: ( date_expires_key STRING )
            // src/main/resources/org/drools/lang/DRL.g:740:4: date_expires_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_expires_key_in_date_expires1748);
            date_expires_key95=date_expires_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_expires_key95.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING96=(Token)match(input,STRING,FOLLOW_STRING_in_date_expires1753); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING96_tree = (Object)adaptor.create(STRING96);
            adaptor.addChild(root_0, STRING96_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING96, DroolsEditorType.STRING_CONST );	
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
    // src/main/resources/org/drools/lang/DRL.g:744:1: enabled : enabled_key ( BOOL | paren_chunk ) ;
    public final DRLParser.enabled_return enabled() throws RecognitionException {
        DRLParser.enabled_return retval = new DRLParser.enabled_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL98=null;
        DRLParser.enabled_key_return enabled_key97 = null;

        DRLParser.paren_chunk_return paren_chunk99 = null;


        Object BOOL98_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:745:2: ( enabled_key ( BOOL | paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL.g:745:4: enabled_key ( BOOL | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_enabled_key_in_enabled1768);
            enabled_key97=enabled_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(enabled_key97.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL.g:746:6: ( BOOL | paren_chunk )
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
                    // src/main/resources/org/drools/lang/DRL.g:746:8: BOOL
                    {
                    BOOL98=(Token)match(input,BOOL,FOLLOW_BOOL_in_enabled1781); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL98_tree = (Object)adaptor.create(BOOL98);
                    adaptor.addChild(root_0, BOOL98_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(BOOL98, DroolsEditorType.BOOLEAN_CONST );	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:747:8: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_enabled1792);
                    paren_chunk99=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk99.getTree());

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
    // src/main/resources/org/drools/lang/DRL.g:751:1: salience : salience_key ( INT | paren_chunk ) ;
    public final DRLParser.salience_return salience() throws RecognitionException {
        DRLParser.salience_return retval = new DRLParser.salience_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT101=null;
        DRLParser.salience_key_return salience_key100 = null;

        DRLParser.paren_chunk_return paren_chunk102 = null;


        Object INT101_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:752:2: ( salience_key ( INT | paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL.g:752:4: salience_key ( INT | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_salience_key_in_salience1812);
            salience_key100=salience_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(salience_key100.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL.g:753:3: ( INT | paren_chunk )
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
                    // src/main/resources/org/drools/lang/DRL.g:753:5: INT
                    {
                    INT101=(Token)match(input,INT,FOLLOW_INT_in_salience1821); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT101_tree = (Object)adaptor.create(INT101);
                    adaptor.addChild(root_0, INT101_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(INT101, DroolsEditorType.NUMERIC_CONST );	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:754:5: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1830);
                    paren_chunk102=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk102.getTree());

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
    // src/main/resources/org/drools/lang/DRL.g:758:1: no_loop : no_loop_key ( BOOL )? ;
    public final DRLParser.no_loop_return no_loop() throws RecognitionException {
        DRLParser.no_loop_return retval = new DRLParser.no_loop_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL104=null;
        DRLParser.no_loop_key_return no_loop_key103 = null;


        Object BOOL104_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:759:2: ( no_loop_key ( BOOL )? )
            // src/main/resources/org/drools/lang/DRL.g:759:4: no_loop_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_no_loop_key_in_no_loop1845);
            no_loop_key103=no_loop_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(no_loop_key103.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL.g:759:66: ( BOOL )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==BOOL) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:759:66: BOOL
                    {
                    BOOL104=(Token)match(input,BOOL,FOLLOW_BOOL_in_no_loop1850); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL104_tree = (Object)adaptor.create(BOOL104);
                    adaptor.addChild(root_0, BOOL104_tree);
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(BOOL104, DroolsEditorType.BOOLEAN_CONST );	
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
    // src/main/resources/org/drools/lang/DRL.g:763:1: auto_focus : auto_focus_key ( BOOL )? ;
    public final DRLParser.auto_focus_return auto_focus() throws RecognitionException {
        DRLParser.auto_focus_return retval = new DRLParser.auto_focus_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL106=null;
        DRLParser.auto_focus_key_return auto_focus_key105 = null;


        Object BOOL106_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:764:2: ( auto_focus_key ( BOOL )? )
            // src/main/resources/org/drools/lang/DRL.g:764:4: auto_focus_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_auto_focus_key_in_auto_focus1865);
            auto_focus_key105=auto_focus_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(auto_focus_key105.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL.g:764:69: ( BOOL )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==BOOL) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:764:69: BOOL
                    {
                    BOOL106=(Token)match(input,BOOL,FOLLOW_BOOL_in_auto_focus1870); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL106_tree = (Object)adaptor.create(BOOL106);
                    adaptor.addChild(root_0, BOOL106_tree);
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(BOOL106, DroolsEditorType.BOOLEAN_CONST );	
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
    // src/main/resources/org/drools/lang/DRL.g:768:1: activation_group : activation_group_key STRING ;
    public final DRLParser.activation_group_return activation_group() throws RecognitionException {
        DRLParser.activation_group_return retval = new DRLParser.activation_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING108=null;
        DRLParser.activation_group_key_return activation_group_key107 = null;


        Object STRING108_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:769:2: ( activation_group_key STRING )
            // src/main/resources/org/drools/lang/DRL.g:769:4: activation_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_activation_group_key_in_activation_group1887);
            activation_group_key107=activation_group_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(activation_group_key107.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING108=(Token)match(input,STRING,FOLLOW_STRING_in_activation_group1892); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING108_tree = (Object)adaptor.create(STRING108);
            adaptor.addChild(root_0, STRING108_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING108, DroolsEditorType.STRING_CONST );	
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
    // src/main/resources/org/drools/lang/DRL.g:773:1: ruleflow_group : ruleflow_group_key STRING ;
    public final DRLParser.ruleflow_group_return ruleflow_group() throws RecognitionException {
        DRLParser.ruleflow_group_return retval = new DRLParser.ruleflow_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING110=null;
        DRLParser.ruleflow_group_key_return ruleflow_group_key109 = null;


        Object STRING110_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:774:2: ( ruleflow_group_key STRING )
            // src/main/resources/org/drools/lang/DRL.g:774:4: ruleflow_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_ruleflow_group_key_in_ruleflow_group1906);
            ruleflow_group_key109=ruleflow_group_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(ruleflow_group_key109.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING110=(Token)match(input,STRING,FOLLOW_STRING_in_ruleflow_group1911); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING110_tree = (Object)adaptor.create(STRING110);
            adaptor.addChild(root_0, STRING110_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING110, DroolsEditorType.STRING_CONST );	
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
    // src/main/resources/org/drools/lang/DRL.g:778:1: agenda_group : agenda_group_key STRING ;
    public final DRLParser.agenda_group_return agenda_group() throws RecognitionException {
        DRLParser.agenda_group_return retval = new DRLParser.agenda_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING112=null;
        DRLParser.agenda_group_key_return agenda_group_key111 = null;


        Object STRING112_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:779:2: ( agenda_group_key STRING )
            // src/main/resources/org/drools/lang/DRL.g:779:4: agenda_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_agenda_group_key_in_agenda_group1925);
            agenda_group_key111=agenda_group_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(agenda_group_key111.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING112=(Token)match(input,STRING,FOLLOW_STRING_in_agenda_group1930); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING112_tree = (Object)adaptor.create(STRING112);
            adaptor.addChild(root_0, STRING112_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING112, DroolsEditorType.STRING_CONST );	
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
    // src/main/resources/org/drools/lang/DRL.g:783:1: timer : ( duration_key | timer_key ) ( INT | paren_chunk ) ;
    public final DRLParser.timer_return timer() throws RecognitionException {
        DRLParser.timer_return retval = new DRLParser.timer_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT115=null;
        DRLParser.duration_key_return duration_key113 = null;

        DRLParser.timer_key_return timer_key114 = null;

        DRLParser.paren_chunk_return paren_chunk116 = null;


        Object INT115_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:784:2: ( ( duration_key | timer_key ) ( INT | paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL.g:784:4: ( duration_key | timer_key ) ( INT | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL.g:784:4: ( duration_key | timer_key )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))))) {
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
                    // src/main/resources/org/drools/lang/DRL.g:784:5: duration_key
                    {
                    pushFollow(FOLLOW_duration_key_in_timer1945);
                    duration_key113=duration_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(duration_key113.getTree(), root_0);

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:784:19: timer_key
                    {
                    pushFollow(FOLLOW_timer_key_in_timer1948);
                    timer_key114=timer_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(timer_key114.getTree(), root_0);

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL.g:785:6: ( INT | paren_chunk )
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
                    // src/main/resources/org/drools/lang/DRL.g:785:8: INT
                    {
                    INT115=(Token)match(input,INT,FOLLOW_INT_in_timer1962); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT115_tree = (Object)adaptor.create(INT115);
                    adaptor.addChild(root_0, INT115_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(INT115, DroolsEditorType.NUMERIC_CONST );	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:786:8: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_timer1973);
                    paren_chunk116=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk116.getTree());

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

    public static class dialect_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dialect"
    // src/main/resources/org/drools/lang/DRL.g:790:1: dialect : dialect_key STRING ;
    public final DRLParser.dialect_return dialect() throws RecognitionException {
        DRLParser.dialect_return retval = new DRLParser.dialect_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING118=null;
        DRLParser.dialect_key_return dialect_key117 = null;


        Object STRING118_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:791:2: ( dialect_key STRING )
            // src/main/resources/org/drools/lang/DRL.g:791:4: dialect_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_dialect_key_in_dialect1993);
            dialect_key117=dialect_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(dialect_key117.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING118=(Token)match(input,STRING,FOLLOW_STRING_in_dialect1998); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING118_tree = (Object)adaptor.create(STRING118);
            adaptor.addChild(root_0, STRING118_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING118, DroolsEditorType.STRING_CONST );	
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
    // src/main/resources/org/drools/lang/DRL.g:795:1: lock_on_active : lock_on_active_key ( BOOL )? ;
    public final DRLParser.lock_on_active_return lock_on_active() throws RecognitionException {
        DRLParser.lock_on_active_return retval = new DRLParser.lock_on_active_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL120=null;
        DRLParser.lock_on_active_key_return lock_on_active_key119 = null;


        Object BOOL120_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:796:2: ( lock_on_active_key ( BOOL )? )
            // src/main/resources/org/drools/lang/DRL.g:796:4: lock_on_active_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lock_on_active_key_in_lock_on_active2016);
            lock_on_active_key119=lock_on_active_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(lock_on_active_key119.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // src/main/resources/org/drools/lang/DRL.g:796:73: ( BOOL )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==BOOL) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:796:73: BOOL
                    {
                    BOOL120=(Token)match(input,BOOL,FOLLOW_BOOL_in_lock_on_active2021); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL120_tree = (Object)adaptor.create(BOOL120);
                    adaptor.addChild(root_0, BOOL120_tree);
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(BOOL120, DroolsEditorType.BOOLEAN_CONST );	
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
    // src/main/resources/org/drools/lang/DRL.g:800:1: normal_lhs_block : ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) ;
    public final DRLParser.normal_lhs_block_return normal_lhs_block() throws RecognitionException {
        DRLParser.normal_lhs_block_return retval = new DRLParser.normal_lhs_block_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.lhs_return lhs121 = null;


        RewriteRuleSubtreeStream stream_lhs=new RewriteRuleSubtreeStream(adaptor,"rule lhs");
        try {
            // src/main/resources/org/drools/lang/DRL.g:801:2: ( ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) )
            // src/main/resources/org/drools/lang/DRL.g:801:4: ( lhs )*
            {
            // src/main/resources/org/drools/lang/DRL.g:801:4: ( lhs )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( (LA46_0==ID) ) {
                    int LA46_1 = input.LA(2);

                    if ( (!((((validateIdentifierKey(DroolsSoftKeywords.END)))))) ) {
                        alt46=1;
                    }


                }
                else if ( (LA46_0==LEFT_PAREN) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:801:4: lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block2036);
            	    lhs121=lhs();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_lhs.add(lhs121.getTree());

            	    }
            	    break;

            	default :
            	    break loop46;
                }
            } while (true);



            // AST REWRITE
            // elements: lhs
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 802:2: -> ^( VT_AND_IMPLICIT ( lhs )* )
            {
                // src/main/resources/org/drools/lang/DRL.g:802:5: ^( VT_AND_IMPLICIT ( lhs )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_AND_IMPLICIT, "VT_AND_IMPLICIT"), root_1);

                // src/main/resources/org/drools/lang/DRL.g:802:23: ( lhs )*
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
    // src/main/resources/org/drools/lang/DRL.g:805:1: lhs : lhs_or ;
    public final DRLParser.lhs_return lhs() throws RecognitionException {
        DRLParser.lhs_return retval = new DRLParser.lhs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.lhs_or_return lhs_or122 = null;



        try {
            // src/main/resources/org/drools/lang/DRL.g:805:5: ( lhs_or )
            // src/main/resources/org/drools/lang/DRL.g:805:7: lhs_or
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_or_in_lhs2057);
            lhs_or122=lhs_or();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_or122.getTree());

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
    // src/main/resources/org/drools/lang/DRL.g:808:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );
    public final DRLParser.lhs_or_return lhs_or() throws RecognitionException {
        DRLParser.lhs_or_return retval = new DRLParser.lhs_or_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        Token LEFT_PAREN123=null;
        Token RIGHT_PAREN125=null;
        DRLParser.or_key_return or = null;

        DRLParser.or_key_return value = null;

        DRLParser.lhs_and_return lhs_and124 = null;

        DRLParser.lhs_and_return lhs_and126 = null;

        DRLParser.lhs_and_return lhs_and127 = null;


        Object pipe_tree=null;
        Object LEFT_PAREN123_tree=null;
        Object RIGHT_PAREN125_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");
        RewriteRuleSubtreeStream stream_lhs_and=new RewriteRuleSubtreeStream(adaptor,"rule lhs_and");

        	Token orToken = null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:811:3: ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* )
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==LEFT_PAREN) ) {
                int LA50_1 = input.LA(2);

                if ( (synpred1_DRL()) ) {
                    alt50=1;
                }
                else if ( (true) ) {
                    alt50=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA50_0==ID) ) {
                alt50=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:811:5: ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN
                    {
                    LEFT_PAREN123=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or2081); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN123);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN123, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_or_key_in_lhs_or2091);
                    or=or_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_or_key.add(or.getTree());
                    if ( state.backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    }
                    // src/main/resources/org/drools/lang/DRL.g:815:4: ( lhs_and )+
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
                    	    // src/main/resources/org/drools/lang/DRL.g:815:4: lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2099);
                    	    lhs_and124=lhs_and();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_lhs_and.add(lhs_and124.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt47 >= 1 ) break loop47;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(47, input);
                                throw eee;
                        }
                        cnt47++;
                    } while (true);

                    RIGHT_PAREN125=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or2105); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN125);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN125, DroolsEditorType.SYMBOL);	
                    }


                    // AST REWRITE
                    // elements: lhs_and, RIGHT_PAREN
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 817:3: -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
                    {
                        // src/main/resources/org/drools/lang/DRL.g:817:6: ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
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
                    // src/main/resources/org/drools/lang/DRL.g:818:4: ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    {
                    // src/main/resources/org/drools/lang/DRL.g:818:4: ( lhs_and -> lhs_and )
                    // src/main/resources/org/drools/lang/DRL.g:818:5: lhs_and
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or2128);
                    lhs_and126=lhs_and();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_and.add(lhs_and126.getTree());


                    // AST REWRITE
                    // elements: lhs_and
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 818:13: -> lhs_and
                    {
                        adaptor.addChild(root_0, stream_lhs_and.nextTree());

                    }

                    retval.tree = root_0;}
                    }

                    // src/main/resources/org/drools/lang/DRL.g:819:3: ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    loop49:
                    do {
                        int alt49=2;
                        int LA49_0 = input.LA(1);

                        if ( (LA49_0==ID) ) {
                            int LA49_1 = input.LA(2);

                            if ( ((synpred2_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.OR))))) ) {
                                alt49=1;
                            }


                        }
                        else if ( (LA49_0==DOUBLE_PIPE) ) {
                            int LA49_3 = input.LA(2);

                            if ( (synpred2_DRL()) ) {
                                alt49=1;
                            }


                        }


                        switch (alt49) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRL.g:819:5: ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and
                    	    {
                    	    // src/main/resources/org/drools/lang/DRL.g:819:28: (value= or_key | pipe= DOUBLE_PIPE )
                    	    int alt48=2;
                    	    int LA48_0 = input.LA(1);

                    	    if ( (LA48_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                    	        alt48=1;
                    	    }
                    	    else if ( (LA48_0==DOUBLE_PIPE) ) {
                    	        alt48=2;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("", 48, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt48) {
                    	        case 1 :
                    	            // src/main/resources/org/drools/lang/DRL.g:819:29: value= or_key
                    	            {
                    	            pushFollow(FOLLOW_or_key_in_lhs_or2150);
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
                    	            // src/main/resources/org/drools/lang/DRL.g:819:69: pipe= DOUBLE_PIPE
                    	            {
                    	            pipe=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_lhs_or2157); if (state.failed) return retval; 
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
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2168);
                    	    lhs_and127=lhs_and();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_lhs_and.add(lhs_and127.getTree());


                    	    // AST REWRITE
                    	    // elements: lhs_or, lhs_and
                    	    // token labels: 
                    	    // rule labels: retval
                    	    // token list labels: 
                    	    // rule list labels: 
                    	    if ( state.backtracking==0 ) {
                    	    retval.tree = root_0;
                    	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    	    root_0 = (Object)adaptor.nil();
                    	    // 822:3: -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
                    	    {
                    	        // src/main/resources/org/drools/lang/DRL.g:822:6: ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
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
                    	    break loop49;
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
    // src/main/resources/org/drools/lang/DRL.g:825:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );
    public final DRLParser.lhs_and_return lhs_and() throws RecognitionException {
        DRLParser.lhs_and_return retval = new DRLParser.lhs_and_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token amper=null;
        Token LEFT_PAREN128=null;
        Token RIGHT_PAREN130=null;
        DRLParser.and_key_return and = null;

        DRLParser.and_key_return value = null;

        DRLParser.lhs_unary_return lhs_unary129 = null;

        DRLParser.lhs_unary_return lhs_unary131 = null;

        DRLParser.lhs_unary_return lhs_unary132 = null;


        Object amper_tree=null;
        Object LEFT_PAREN128_tree=null;
        Object RIGHT_PAREN130_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_AMPER=new RewriteRuleTokenStream(adaptor,"token DOUBLE_AMPER");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_and_key=new RewriteRuleSubtreeStream(adaptor,"rule and_key");
        RewriteRuleSubtreeStream stream_lhs_unary=new RewriteRuleSubtreeStream(adaptor,"rule lhs_unary");

        	Token andToken = null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:828:3: ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* )
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==LEFT_PAREN) ) {
                int LA54_1 = input.LA(2);

                if ( (synpred3_DRL()) ) {
                    alt54=1;
                }
                else if ( (true) ) {
                    alt54=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 54, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA54_0==ID) ) {
                alt54=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:828:5: ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN
                    {
                    LEFT_PAREN128=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and2209); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN128);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN128, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_and_key_in_lhs_and2219);
                    and=and_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_and_key.add(and.getTree());
                    if ( state.backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    }
                    // src/main/resources/org/drools/lang/DRL.g:832:4: ( lhs_unary )+
                    int cnt51=0;
                    loop51:
                    do {
                        int alt51=2;
                        int LA51_0 = input.LA(1);

                        if ( (LA51_0==ID||LA51_0==LEFT_PAREN) ) {
                            alt51=1;
                        }


                        switch (alt51) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRL.g:832:4: lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2227);
                    	    lhs_unary129=lhs_unary();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_lhs_unary.add(lhs_unary129.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt51 >= 1 ) break loop51;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(51, input);
                                throw eee;
                        }
                        cnt51++;
                    } while (true);

                    RIGHT_PAREN130=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and2233); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN130);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN130, DroolsEditorType.SYMBOL);	
                    }


                    // AST REWRITE
                    // elements: lhs_unary, RIGHT_PAREN
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 834:3: -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
                    {
                        // src/main/resources/org/drools/lang/DRL.g:834:6: ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
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
                    // src/main/resources/org/drools/lang/DRL.g:835:4: ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    {
                    // src/main/resources/org/drools/lang/DRL.g:835:4: ( lhs_unary -> lhs_unary )
                    // src/main/resources/org/drools/lang/DRL.g:835:5: lhs_unary
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and2257);
                    lhs_unary131=lhs_unary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_unary.add(lhs_unary131.getTree());


                    // AST REWRITE
                    // elements: lhs_unary
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 835:15: -> lhs_unary
                    {
                        adaptor.addChild(root_0, stream_lhs_unary.nextTree());

                    }

                    retval.tree = root_0;}
                    }

                    // src/main/resources/org/drools/lang/DRL.g:836:3: ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    loop53:
                    do {
                        int alt53=2;
                        int LA53_0 = input.LA(1);

                        if ( (LA53_0==ID) ) {
                            int LA53_2 = input.LA(2);

                            if ( ((synpred4_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.AND))))) ) {
                                alt53=1;
                            }


                        }
                        else if ( (LA53_0==DOUBLE_AMPER) ) {
                            int LA53_3 = input.LA(2);

                            if ( (synpred4_DRL()) ) {
                                alt53=1;
                            }


                        }


                        switch (alt53) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRL.g:836:5: ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary
                    	    {
                    	    // src/main/resources/org/drools/lang/DRL.g:836:30: (value= and_key | amper= DOUBLE_AMPER )
                    	    int alt52=2;
                    	    int LA52_0 = input.LA(1);

                    	    if ( (LA52_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))))) {
                    	        alt52=1;
                    	    }
                    	    else if ( (LA52_0==DOUBLE_AMPER) ) {
                    	        alt52=2;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("", 52, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt52) {
                    	        case 1 :
                    	            // src/main/resources/org/drools/lang/DRL.g:836:31: value= and_key
                    	            {
                    	            pushFollow(FOLLOW_and_key_in_lhs_and2279);
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
                    	            // src/main/resources/org/drools/lang/DRL.g:836:73: amper= DOUBLE_AMPER
                    	            {
                    	            amper=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_lhs_and2286); if (state.failed) return retval; 
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
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2297);
                    	    lhs_unary132=lhs_unary();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_lhs_unary.add(lhs_unary132.getTree());


                    	    // AST REWRITE
                    	    // elements: lhs_unary, lhs_and
                    	    // token labels: 
                    	    // rule labels: retval
                    	    // token list labels: 
                    	    // rule list labels: 
                    	    if ( state.backtracking==0 ) {
                    	    retval.tree = root_0;
                    	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    	    root_0 = (Object)adaptor.nil();
                    	    // 839:3: -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
                    	    {
                    	        // src/main/resources/org/drools/lang/DRL.g:839:6: ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
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
                    	    break loop53;
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
    // src/main/resources/org/drools/lang/DRL.g:842:1: lhs_unary : ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? ;
    public final DRLParser.lhs_unary_return lhs_unary() throws RecognitionException {
        DRLParser.lhs_unary_return retval = new DRLParser.lhs_unary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN138=null;
        Token RIGHT_PAREN140=null;
        Token SEMICOLON142=null;
        DRLParser.lhs_exist_return lhs_exist133 = null;

        DRLParser.lhs_not_binding_return lhs_not_binding134 = null;

        DRLParser.lhs_not_return lhs_not135 = null;

        DRLParser.lhs_eval_return lhs_eval136 = null;

        DRLParser.lhs_forall_return lhs_forall137 = null;

        DRLParser.lhs_or_return lhs_or139 = null;

        DRLParser.pattern_source_return pattern_source141 = null;


        Object LEFT_PAREN138_tree=null;
        Object RIGHT_PAREN140_tree=null;
        Object SEMICOLON142_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:843:2: ( ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? )
            // src/main/resources/org/drools/lang/DRL.g:843:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL.g:843:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )
            int alt55=7;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==ID) ) {
                int LA55_1 = input.LA(2);

                if ( (((validateIdentifierKey(DroolsSoftKeywords.EXISTS)))) ) {
                    alt55=1;
                }
                else if ( (((validateNotWithBinding())&&((validateIdentifierKey(DroolsSoftKeywords.NOT))))) ) {
                    alt55=2;
                }
                else if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                    alt55=3;
                }
                else if ( (((validateIdentifierKey(DroolsSoftKeywords.EVAL)))) ) {
                    alt55=4;
                }
                else if ( (((validateIdentifierKey(DroolsSoftKeywords.FORALL)))) ) {
                    alt55=5;
                }
                else if ( (true) ) {
                    alt55=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 55, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA55_0==LEFT_PAREN) ) {
                alt55=6;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:843:6: lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary2328);
                    lhs_exist133=lhs_exist();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_exist133.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:844:4: {...}? => lhs_not_binding
                    {
                    if ( !((validateNotWithBinding())) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "lhs_unary", "validateNotWithBinding()");
                    }
                    pushFollow(FOLLOW_lhs_not_binding_in_lhs_unary2336);
                    lhs_not_binding134=lhs_not_binding();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_not_binding134.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:845:5: lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary2342);
                    lhs_not135=lhs_not();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_not135.getTree());

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRL.g:846:5: lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2348);
                    lhs_eval136=lhs_eval();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_eval136.getTree());

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRL.g:847:5: lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary2354);
                    lhs_forall137=lhs_forall();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_forall137.getTree());

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRL.g:848:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN138=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2360); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN138, DroolsEditorType.SYMBOL); emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary2371);
                    lhs_or139=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_or139.getTree());
                    RIGHT_PAREN140=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2377); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_PAREN140_tree = (Object)adaptor.create(RIGHT_PAREN140);
                    adaptor.addChild(root_0, RIGHT_PAREN140_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN140, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRL.g:851:5: pattern_source
                    {
                    pushFollow(FOLLOW_pattern_source_in_lhs_unary2385);
                    pattern_source141=pattern_source();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, pattern_source141.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL.g:853:3: ( ( SEMICOLON )=> SEMICOLON )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==SEMICOLON) ) {
                int LA56_1 = input.LA(2);

                if ( (synpred5_DRL()) ) {
                    alt56=1;
                }
            }
            switch (alt56) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:853:4: ( SEMICOLON )=> SEMICOLON
                    {
                    SEMICOLON142=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_lhs_unary2399); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	emit(SEMICOLON142, DroolsEditorType.SYMBOL);	
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
    // src/main/resources/org/drools/lang/DRL.g:856:1: lhs_exist : exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final DRLParser.lhs_exist_return lhs_exist() throws RecognitionException {
        DRLParser.lhs_exist_return retval = new DRLParser.lhs_exist_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN145=null;
        Token RIGHT_PAREN147=null;
        DRLParser.exists_key_return exists_key143 = null;

        DRLParser.lhs_or_return lhs_or144 = null;

        DRLParser.lhs_or_return lhs_or146 = null;

        DRLParser.lhs_pattern_return lhs_pattern148 = null;


        Object LEFT_PAREN145_tree=null;
        Object RIGHT_PAREN147_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_exists_key=new RewriteRuleSubtreeStream(adaptor,"rule exists_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // src/main/resources/org/drools/lang/DRL.g:857:2: ( exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // src/main/resources/org/drools/lang/DRL.g:857:4: exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_exists_key_in_lhs_exist2415);
            exists_key143=exists_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_exists_key.add(exists_key143.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS);	
            }
            // src/main/resources/org/drools/lang/DRL.g:859:10: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt57=3;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==LEFT_PAREN) ) {
                int LA57_1 = input.LA(2);

                if ( (synpred6_DRL()) ) {
                    alt57=1;
                }
                else if ( (true) ) {
                    alt57=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 57, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA57_0==ID) ) {
                int LA57_2 = input.LA(2);

                if ( ((((synpred6_DRL()&&(validateNotWithBinding()))&&((validateIdentifierKey(DroolsSoftKeywords.NOT))))||(synpred6_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.EXISTS))))||(synpred6_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.EVAL))))||synpred6_DRL()||(synpred6_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.NOT))))||(synpred6_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.FORALL)))))) ) {
                    alt57=1;
                }
                else if ( (true) ) {
                    alt57=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 57, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;
            }
            switch (alt57) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:859:12: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2442);
                    lhs_or144=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or144.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:860:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN145=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist2449); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN145);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN145, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2457);
                    lhs_or146=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or146.getTree());
                    RIGHT_PAREN147=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist2464); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN147);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN147, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:863:12: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist2479);
                    lhs_pattern148=lhs_pattern();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_pattern.add(lhs_pattern148.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: RIGHT_PAREN, lhs_or, lhs_pattern, exists_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 865:10: -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // src/main/resources/org/drools/lang/DRL.g:865:13: ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_exists_key.nextNode(), root_1);

                // src/main/resources/org/drools/lang/DRL.g:865:26: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.nextTree());

                }
                stream_lhs_or.reset();
                // src/main/resources/org/drools/lang/DRL.g:865:34: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.nextTree());

                }
                stream_lhs_pattern.reset();
                // src/main/resources/org/drools/lang/DRL.g:865:47: ( RIGHT_PAREN )?
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
    // src/main/resources/org/drools/lang/DRL.g:868:1: lhs_not_binding : not_key fact_binding -> ^( not_key ^( VT_PATTERN fact_binding ) ) ;
    public final DRLParser.lhs_not_binding_return lhs_not_binding() throws RecognitionException {
        DRLParser.lhs_not_binding_return retval = new DRLParser.lhs_not_binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.not_key_return not_key149 = null;

        DRLParser.fact_binding_return fact_binding150 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        try {
            // src/main/resources/org/drools/lang/DRL.g:869:2: ( not_key fact_binding -> ^( not_key ^( VT_PATTERN fact_binding ) ) )
            // src/main/resources/org/drools/lang/DRL.g:869:4: not_key fact_binding
            {
            pushFollow(FOLLOW_not_key_in_lhs_not_binding2525);
            not_key149=not_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_not_key.add(not_key149.getTree());
            pushFollow(FOLLOW_fact_binding_in_lhs_not_binding2527);
            fact_binding150=fact_binding();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_fact_binding.add(fact_binding150.getTree());


            // AST REWRITE
            // elements: fact_binding, not_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 870:2: -> ^( not_key ^( VT_PATTERN fact_binding ) )
            {
                // src/main/resources/org/drools/lang/DRL.g:870:5: ^( not_key ^( VT_PATTERN fact_binding ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // src/main/resources/org/drools/lang/DRL.g:870:15: ^( VT_PATTERN fact_binding )
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
    // src/main/resources/org/drools/lang/DRL.g:873:1: lhs_not : not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final DRLParser.lhs_not_return lhs_not() throws RecognitionException {
        DRLParser.lhs_not_return retval = new DRLParser.lhs_not_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN153=null;
        Token RIGHT_PAREN155=null;
        DRLParser.not_key_return not_key151 = null;

        DRLParser.lhs_or_return lhs_or152 = null;

        DRLParser.lhs_or_return lhs_or154 = null;

        DRLParser.lhs_pattern_return lhs_pattern156 = null;


        Object LEFT_PAREN153_tree=null;
        Object RIGHT_PAREN155_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // src/main/resources/org/drools/lang/DRL.g:873:9: ( not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // src/main/resources/org/drools/lang/DRL.g:873:11: not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_not_key_in_lhs_not2550);
            not_key151=not_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_not_key.add(not_key151.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT);	
            }
            // src/main/resources/org/drools/lang/DRL.g:875:3: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt58=3;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==LEFT_PAREN) ) {
                int LA58_1 = input.LA(2);

                if ( (synpred7_DRL()) ) {
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

                if ( (synpred7_DRL()) ) {
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
                    // src/main/resources/org/drools/lang/DRL.g:875:5: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    if ( state.backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2572);
                    lhs_or152=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or152.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:876:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN153=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2579); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN153);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN153, DroolsEditorType.SYMBOL); emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2588);
                    lhs_or154=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or154.getTree());
                    RIGHT_PAREN155=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2594); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN155);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN155, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:879:6: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2604);
                    lhs_pattern156=lhs_pattern();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_pattern.add(lhs_pattern156.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: RIGHT_PAREN, lhs_pattern, lhs_or, not_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 880:10: -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // src/main/resources/org/drools/lang/DRL.g:880:13: ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // src/main/resources/org/drools/lang/DRL.g:880:23: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.nextTree());

                }
                stream_lhs_or.reset();
                // src/main/resources/org/drools/lang/DRL.g:880:31: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.nextTree());

                }
                stream_lhs_pattern.reset();
                // src/main/resources/org/drools/lang/DRL.g:880:44: ( RIGHT_PAREN )?
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
    // src/main/resources/org/drools/lang/DRL.g:883:1: lhs_eval : ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) ;
    public final DRLParser.lhs_eval_return lhs_eval() throws RecognitionException {
        DRLParser.lhs_eval_return retval = new DRLParser.lhs_eval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.eval_key_return ev = null;

        DRLParser.paren_chunk_return pc = null;


        RewriteRuleSubtreeStream stream_eval_key=new RewriteRuleSubtreeStream(adaptor,"rule eval_key");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL.g:884:2: (ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL.g:884:4: ev= eval_key pc= paren_chunk
            {
            pushFollow(FOLLOW_eval_key_in_lhs_eval2643);
            ev=eval_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_eval_key.add(ev.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_EVAL);	
            }
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2652);
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 893:3: -> ^( eval_key paren_chunk )
            {
                // src/main/resources/org/drools/lang/DRL.g:893:6: ^( eval_key paren_chunk )
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
    // src/main/resources/org/drools/lang/DRL.g:896:1: lhs_forall : forall_key LEFT_PAREN ( pattern_source )+ RIGHT_PAREN -> ^( forall_key ( pattern_source )+ RIGHT_PAREN ) ;
    public final DRLParser.lhs_forall_return lhs_forall() throws RecognitionException {
        DRLParser.lhs_forall_return retval = new DRLParser.lhs_forall_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN158=null;
        Token RIGHT_PAREN160=null;
        DRLParser.forall_key_return forall_key157 = null;

        DRLParser.pattern_source_return pattern_source159 = null;


        Object LEFT_PAREN158_tree=null;
        Object RIGHT_PAREN160_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_pattern_source=new RewriteRuleSubtreeStream(adaptor,"rule pattern_source");
        RewriteRuleSubtreeStream stream_forall_key=new RewriteRuleSubtreeStream(adaptor,"rule forall_key");
        try {
            // src/main/resources/org/drools/lang/DRL.g:897:2: ( forall_key LEFT_PAREN ( pattern_source )+ RIGHT_PAREN -> ^( forall_key ( pattern_source )+ RIGHT_PAREN ) )
            // src/main/resources/org/drools/lang/DRL.g:897:4: forall_key LEFT_PAREN ( pattern_source )+ RIGHT_PAREN
            {
            pushFollow(FOLLOW_forall_key_in_lhs_forall2679);
            forall_key157=forall_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_forall_key.add(forall_key157.getTree());
            LEFT_PAREN158=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2684); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN158);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN158, DroolsEditorType.SYMBOL);	
            }
            // src/main/resources/org/drools/lang/DRL.g:899:4: ( pattern_source )+
            int cnt59=0;
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==ID) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:899:4: pattern_source
            	    {
            	    pushFollow(FOLLOW_pattern_source_in_lhs_forall2692);
            	    pattern_source159=pattern_source();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_pattern_source.add(pattern_source159.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt59 >= 1 ) break loop59;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(59, input);
                        throw eee;
                }
                cnt59++;
            } while (true);

            RIGHT_PAREN160=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall2698); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN160);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN160, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: pattern_source, RIGHT_PAREN, forall_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 901:3: -> ^( forall_key ( pattern_source )+ RIGHT_PAREN )
            {
                // src/main/resources/org/drools/lang/DRL.g:901:6: ^( forall_key ( pattern_source )+ RIGHT_PAREN )
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
    // src/main/resources/org/drools/lang/DRL.g:904:1: pattern_source : lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? ;
    public final DRLParser.pattern_source_return pattern_source() throws RecognitionException {
        DRLParser.pattern_source_return retval = new DRLParser.pattern_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FROM163=null;
        DRLParser.lhs_pattern_return lhs_pattern161 = null;

        DRLParser.over_clause_return over_clause162 = null;

        DRLParser.accumulate_statement_return accumulate_statement164 = null;

        DRLParser.collect_statement_return collect_statement165 = null;

        DRLParser.entrypoint_statement_return entrypoint_statement166 = null;

        DRLParser.from_source_return from_source167 = null;


        Object FROM163_tree=null;

         boolean isFailed = true;	
        try {
            // src/main/resources/org/drools/lang/DRL.g:907:2: ( lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? )
            // src/main/resources/org/drools/lang/DRL.g:907:4: lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_pattern_in_pattern_source2734);
            lhs_pattern161=lhs_pattern();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_pattern161.getTree());
            // src/main/resources/org/drools/lang/DRL.g:908:3: ( over_clause )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==OVER) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:908:3: over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_pattern_source2738);
                    over_clause162=over_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, over_clause162.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL.g:909:3: ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==FROM) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:910:4: FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    {
                    FROM163=(Token)match(input,FROM,FOLLOW_FROM_in_pattern_source2748); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FROM163_tree = (Object)adaptor.create(FROM163);
                    root_0 = (Object)adaptor.becomeRoot(FROM163_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(FROM163, DroolsEditorType.KEYWORD);
                      			emit(Location.LOCATION_LHS_FROM);	
                    }
                    // src/main/resources/org/drools/lang/DRL.g:913:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    int alt61=4;
                    switch ( input.LA(1) ) {
                    case ACCUMULATE:
                        {
                        alt61=1;
                        }
                        break;
                    case COLLECT:
                        {
                        alt61=2;
                        }
                        break;
                    case ID:
                        {
                        int LA61_3 = input.LA(2);

                        if ( (LA61_3==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT))))) {
                            alt61=3;
                        }
                        else if ( ((LA61_3>=SEMICOLON && LA61_3<=DOT)||(LA61_3>=LEFT_PAREN && LA61_3<=RIGHT_PAREN)||(LA61_3>=DOUBLE_PIPE && LA61_3<=DOUBLE_AMPER)||LA61_3==THEN) ) {
                            alt61=4;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 61, 3, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 61, 0, input);

                        throw nvae;
                    }

                    switch (alt61) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRL.g:913:14: accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_pattern_source2768);
                            accumulate_statement164=accumulate_statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, accumulate_statement164.getTree());

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DRL.g:914:15: collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_pattern_source2784);
                            collect_statement165=collect_statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, collect_statement165.getTree());

                            }
                            break;
                        case 3 :
                            // src/main/resources/org/drools/lang/DRL.g:915:15: entrypoint_statement
                            {
                            pushFollow(FOLLOW_entrypoint_statement_in_pattern_source2801);
                            entrypoint_statement166=entrypoint_statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, entrypoint_statement166.getTree());

                            }
                            break;
                        case 4 :
                            // src/main/resources/org/drools/lang/DRL.g:916:15: from_source
                            {
                            pushFollow(FOLLOW_from_source_in_pattern_source2817);
                            from_source167=from_source();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, from_source167.getTree());

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
    // src/main/resources/org/drools/lang/DRL.g:934:1: over_clause : OVER over_elements ( COMMA over_elements )* ;
    public final DRLParser.over_clause_return over_clause() throws RecognitionException {
        DRLParser.over_clause_return retval = new DRLParser.over_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OVER168=null;
        Token COMMA170=null;
        DRLParser.over_elements_return over_elements169 = null;

        DRLParser.over_elements_return over_elements171 = null;


        Object OVER168_tree=null;
        Object COMMA170_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:935:2: ( OVER over_elements ( COMMA over_elements )* )
            // src/main/resources/org/drools/lang/DRL.g:935:4: OVER over_elements ( COMMA over_elements )*
            {
            root_0 = (Object)adaptor.nil();

            OVER168=(Token)match(input,OVER,FOLLOW_OVER_in_over_clause2849); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            OVER168_tree = (Object)adaptor.create(OVER168);
            root_0 = (Object)adaptor.becomeRoot(OVER168_tree, root_0);
            }
            if ( state.backtracking==0 ) {
              	emit(OVER168, DroolsEditorType.KEYWORD);	
            }
            pushFollow(FOLLOW_over_elements_in_over_clause2854);
            over_elements169=over_elements();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, over_elements169.getTree());
            // src/main/resources/org/drools/lang/DRL.g:936:4: ( COMMA over_elements )*
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==COMMA) ) {
                    int LA63_2 = input.LA(2);

                    if ( (LA63_2==ID) ) {
                        int LA63_3 = input.LA(3);

                        if ( (LA63_3==COLON) ) {
                            alt63=1;
                        }


                    }


                }


                switch (alt63) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:936:5: COMMA over_elements
            	    {
            	    COMMA170=(Token)match(input,COMMA,FOLLOW_COMMA_in_over_clause2861); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA170, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_over_elements_in_over_clause2866);
            	    over_elements171=over_elements();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, over_elements171.getTree());

            	    }
            	    break;

            	default :
            	    break loop63;
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
    // src/main/resources/org/drools/lang/DRL.g:939:1: over_elements : id1= ID COLON id2= ID paren_chunk -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk ) ;
    public final DRLParser.over_elements_return over_elements() throws RecognitionException {
        DRLParser.over_elements_return retval = new DRLParser.over_elements_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token id2=null;
        Token COLON172=null;
        DRLParser.paren_chunk_return paren_chunk173 = null;


        Object id1_tree=null;
        Object id2_tree=null;
        Object COLON172_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL.g:940:2: (id1= ID COLON id2= ID paren_chunk -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL.g:940:4: id1= ID COLON id2= ID paren_chunk
            {
            id1=(Token)match(input,ID,FOLLOW_ID_in_over_elements2881); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            if ( state.backtracking==0 ) {
              	emit(id1, DroolsEditorType.IDENTIFIER);	
            }
            COLON172=(Token)match(input,COLON,FOLLOW_COLON_in_over_elements2888); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON172);

            if ( state.backtracking==0 ) {
              	emit(COLON172, DroolsEditorType.SYMBOL);	
            }
            id2=(Token)match(input,ID,FOLLOW_ID_in_over_elements2897); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            if ( state.backtracking==0 ) {
              	emit(id2, DroolsEditorType.IDENTIFIER);	
            }
            pushFollow(FOLLOW_paren_chunk_in_over_elements2904);
            paren_chunk173=paren_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk173.getTree());


            // AST REWRITE
            // elements: id2, paren_chunk, id1
            // token labels: id1, id2
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_id1=new RewriteRuleTokenStream(adaptor,"token id1",id1);
            RewriteRuleTokenStream stream_id2=new RewriteRuleTokenStream(adaptor,"token id2",id2);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 944:2: -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk )
            {
                // src/main/resources/org/drools/lang/DRL.g:944:5: ^( VT_BEHAVIOR $id1 $id2 paren_chunk )
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
    // src/main/resources/org/drools/lang/DRL.g:947:1: accumulate_statement : ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) ;
    public final DRLParser.accumulate_statement_return accumulate_statement() throws RecognitionException {
        DRLParser.accumulate_statement_return retval = new DRLParser.accumulate_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ACCUMULATE174=null;
        Token LEFT_PAREN175=null;
        Token COMMA177=null;
        Token RIGHT_PAREN180=null;
        DRLParser.lhs_or_return lhs_or176 = null;

        DRLParser.accumulate_init_clause_return accumulate_init_clause178 = null;

        DRLParser.accumulate_id_clause_return accumulate_id_clause179 = null;


        Object ACCUMULATE174_tree=null;
        Object LEFT_PAREN175_tree=null;
        Object COMMA177_tree=null;
        Object RIGHT_PAREN180_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_ACCUMULATE=new RewriteRuleTokenStream(adaptor,"token ACCUMULATE");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_accumulate_init_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_init_clause");
        RewriteRuleSubtreeStream stream_accumulate_id_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_id_clause");
        try {
            // src/main/resources/org/drools/lang/DRL.g:948:2: ( ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) )
            // src/main/resources/org/drools/lang/DRL.g:948:4: ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN
            {
            ACCUMULATE174=(Token)match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement2930); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACCUMULATE.add(ACCUMULATE174);

            if ( state.backtracking==0 ) {
              	emit(ACCUMULATE174, DroolsEditorType.KEYWORD);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_ACCUMULATE);	
            }
            LEFT_PAREN175=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2939); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN175);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN175, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_lhs_or_in_accumulate_statement2947);
            lhs_or176=lhs_or();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or176.getTree());
            // src/main/resources/org/drools/lang/DRL.g:952:3: ( COMMA )?
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==COMMA) ) {
                alt64=1;
            }
            switch (alt64) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:952:3: COMMA
                    {
                    COMMA177=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2952); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COMMA.add(COMMA177);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(COMMA177, DroolsEditorType.SYMBOL);	
            }
            // src/main/resources/org/drools/lang/DRL.g:953:3: ( accumulate_init_clause | accumulate_id_clause )
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==ID) ) {
                int LA65_1 = input.LA(2);

                if ( (((validateIdentifierKey(DroolsSoftKeywords.INIT)))) ) {
                    alt65=1;
                }
                else if ( (true) ) {
                    alt65=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 65, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 65, 0, input);

                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:953:5: accumulate_init_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_statement2962);
                    accumulate_init_clause178=accumulate_init_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accumulate_init_clause.add(accumulate_init_clause178.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:954:5: accumulate_id_clause
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_statement2968);
                    accumulate_id_clause179=accumulate_id_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accumulate_id_clause.add(accumulate_id_clause179.getTree());

                    }
                    break;

            }

            RIGHT_PAREN180=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2976); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN180);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN180, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }


            // AST REWRITE
            // elements: accumulate_id_clause, lhs_or, accumulate_init_clause, ACCUMULATE, RIGHT_PAREN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 958:3: -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
            {
                // src/main/resources/org/drools/lang/DRL.g:958:6: ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ACCUMULATE.nextNode(), root_1);

                adaptor.addChild(root_1, stream_lhs_or.nextTree());
                // src/main/resources/org/drools/lang/DRL.g:958:26: ( accumulate_init_clause )?
                if ( stream_accumulate_init_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_accumulate_init_clause.nextTree());

                }
                stream_accumulate_init_clause.reset();
                // src/main/resources/org/drools/lang/DRL.g:958:50: ( accumulate_id_clause )?
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
    // src/main/resources/org/drools/lang/DRL.g:962:1: accumulate_init_clause : init_key pc1= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] (cm1= COMMA )? action_key pc2= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] (cm2= COMMA )? ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )? res1= result_key pc4= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE] -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) ;
    public final DRLParser.accumulate_init_clause_return accumulate_init_clause() throws RecognitionException {
        DRLParser.accumulate_init_clause_return retval = new DRLParser.accumulate_init_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token cm1=null;
        Token cm2=null;
        Token cm3=null;
        DRLParser.accumulate_paren_chunk_return pc1 = null;

        DRLParser.accumulate_paren_chunk_return pc2 = null;

        DRLParser.accumulate_paren_chunk_return pc3 = null;

        DRLParser.result_key_return res1 = null;

        DRLParser.accumulate_paren_chunk_return pc4 = null;

        DRLParser.init_key_return init_key181 = null;

        DRLParser.action_key_return action_key182 = null;

        DRLParser.reverse_key_return reverse_key183 = null;


        Object cm1_tree=null;
        Object cm2_tree=null;
        Object cm3_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_accumulate_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_paren_chunk");
        RewriteRuleSubtreeStream stream_reverse_key=new RewriteRuleSubtreeStream(adaptor,"rule reverse_key");
        RewriteRuleSubtreeStream stream_result_key=new RewriteRuleSubtreeStream(adaptor,"rule result_key");
        RewriteRuleSubtreeStream stream_init_key=new RewriteRuleSubtreeStream(adaptor,"rule init_key");
        RewriteRuleSubtreeStream stream_action_key=new RewriteRuleSubtreeStream(adaptor,"rule action_key");
         boolean isFailed = true;	
        try {
            // src/main/resources/org/drools/lang/DRL.g:965:2: ( init_key pc1= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] (cm1= COMMA )? action_key pc2= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] (cm2= COMMA )? ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )? res1= result_key pc4= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE] -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) )
            // src/main/resources/org/drools/lang/DRL.g:965:4: init_key pc1= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] (cm1= COMMA )? action_key pc2= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] (cm2= COMMA )? ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )? res1= result_key pc4= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE]
            {
            pushFollow(FOLLOW_init_key_in_accumulate_init_clause3022);
            init_key181=init_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_init_key.add(init_key181.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT);	
            }
            pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3032);
            pc1=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc1.getTree());
            // src/main/resources/org/drools/lang/DRL.g:967:84: (cm1= COMMA )?
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==COMMA) ) {
                alt66=1;
            }
            switch (alt66) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:967:84: cm1= COMMA
                    {
                    cm1=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause3037); if (state.failed) return retval; 
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
            pushFollow(FOLLOW_action_key_in_accumulate_init_clause3048);
            action_key182=action_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_action_key.add(action_key182.getTree());
            pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3052);
            pc2=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc2.getTree());
            // src/main/resources/org/drools/lang/DRL.g:969:97: (cm2= COMMA )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==COMMA) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:969:97: cm2= COMMA
                    {
                    cm2=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause3057); if (state.failed) return retval; 
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
            // src/main/resources/org/drools/lang/DRL.g:971:2: ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==ID) ) {
                int LA69_1 = input.LA(2);

                if ( (((validateIdentifierKey(DroolsSoftKeywords.REVERSE)))) ) {
                    alt69=1;
                }
            }
            switch (alt69) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:971:4: reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )?
                    {
                    pushFollow(FOLLOW_reverse_key_in_accumulate_init_clause3069);
                    reverse_key183=reverse_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_reverse_key.add(reverse_key183.getTree());
                    pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3073);
                    pc3=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc3.getTree());
                    // src/main/resources/org/drools/lang/DRL.g:971:100: (cm3= COMMA )?
                    int alt68=2;
                    int LA68_0 = input.LA(1);

                    if ( (LA68_0==COMMA) ) {
                        alt68=1;
                    }
                    switch (alt68) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRL.g:971:100: cm3= COMMA
                            {
                            cm3=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause3078); if (state.failed) return retval; 
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
            pushFollow(FOLLOW_result_key_in_accumulate_init_clause3094);
            res1=result_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_result_key.add(res1.getTree());
            if ( state.backtracking==0 ) {
              	emit((res1!=null?((Token)res1.start):null), DroolsEditorType.KEYWORD);	
            }
            pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3100);
            pc4=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc4.getTree());


            // AST REWRITE
            // elements: pc2, pc4, init_key, result_key, reverse_key, action_key, pc3, pc1
            // token labels: 
            // rule labels: pc2, pc3, pc4, pc1, retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_pc2=new RewriteRuleSubtreeStream(adaptor,"token pc2",pc2!=null?pc2.tree:null);
            RewriteRuleSubtreeStream stream_pc3=new RewriteRuleSubtreeStream(adaptor,"token pc3",pc3!=null?pc3.tree:null);
            RewriteRuleSubtreeStream stream_pc4=new RewriteRuleSubtreeStream(adaptor,"token pc4",pc4!=null?pc4.tree:null);
            RewriteRuleSubtreeStream stream_pc1=new RewriteRuleSubtreeStream(adaptor,"token pc1",pc1!=null?pc1.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 980:2: -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
            {
                // src/main/resources/org/drools/lang/DRL.g:980:5: ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ACCUMULATE_INIT_CLAUSE, "VT_ACCUMULATE_INIT_CLAUSE"), root_1);

                // src/main/resources/org/drools/lang/DRL.g:980:33: ^( init_key $pc1)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_init_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc1.nextTree());

                adaptor.addChild(root_1, root_2);
                }
                // src/main/resources/org/drools/lang/DRL.g:980:50: ^( action_key $pc2)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_action_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc2.nextTree());

                adaptor.addChild(root_1, root_2);
                }
                // src/main/resources/org/drools/lang/DRL.g:980:69: ( ^( reverse_key $pc3) )?
                if ( stream_reverse_key.hasNext()||stream_pc3.hasNext() ) {
                    // src/main/resources/org/drools/lang/DRL.g:980:69: ^( reverse_key $pc3)
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_reverse_key.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_pc3.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_reverse_key.reset();
                stream_pc3.reset();
                // src/main/resources/org/drools/lang/DRL.g:980:90: ^( result_key $pc4)
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
    // src/main/resources/org/drools/lang/DRL.g:993:1: accumulate_paren_chunk[int locationType] : pc= accumulate_paren_chunk_data[false,$locationType] -> VT_PAREN_CHUNK[$pc.start,text] ;
    public final DRLParser.accumulate_paren_chunk_return accumulate_paren_chunk(int locationType) throws RecognitionException {
        DRLParser.accumulate_paren_chunk_return retval = new DRLParser.accumulate_paren_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.accumulate_paren_chunk_data_return pc = null;


        RewriteRuleSubtreeStream stream_accumulate_paren_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_paren_chunk_data");

        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL.g:996:3: (pc= accumulate_paren_chunk_data[false,$locationType] -> VT_PAREN_CHUNK[$pc.start,text] )
            // src/main/resources/org/drools/lang/DRL.g:996:5: pc= accumulate_paren_chunk_data[false,$locationType]
            {
            pushFollow(FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk3158);
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 997:2: -> VT_PAREN_CHUNK[$pc.start,text]
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
    // src/main/resources/org/drools/lang/DRL.g:1000:1: accumulate_paren_chunk_data[boolean isRecursive, int locationType] : lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )* rp1= RIGHT_PAREN ;
    public final DRLParser.accumulate_paren_chunk_data_return accumulate_paren_chunk_data(boolean isRecursive, int locationType) throws RecognitionException {
        DRLParser.accumulate_paren_chunk_data_return retval = new DRLParser.accumulate_paren_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lp1=null;
        Token any=null;
        Token rp1=null;
        DRLParser.accumulate_paren_chunk_data_return accumulate_paren_chunk_data184 = null;


        Object lp1_tree=null;
        Object any_tree=null;
        Object rp1_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1001:2: (lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )* rp1= RIGHT_PAREN )
            // src/main/resources/org/drools/lang/DRL.g:1001:4: lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )* rp1= RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            lp1=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_paren_chunk_data3182); if (state.failed) return retval;
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
            // src/main/resources/org/drools/lang/DRL.g:1009:4: (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )*
            loop70:
            do {
                int alt70=3;
                int LA70_0 = input.LA(1);

                if ( ((LA70_0>=VT_COMPILATION_UNIT && LA70_0<=STRING)||LA70_0==COMMA||(LA70_0>=AT && LA70_0<=IdentifierPart)) ) {
                    alt70=1;
                }
                else if ( (LA70_0==LEFT_PAREN) ) {
                    alt70=2;
                }


                switch (alt70) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1009:5: any=~ ( LEFT_PAREN | RIGHT_PAREN )
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
            	    // src/main/resources/org/drools/lang/DRL.g:1009:87: accumulate_paren_chunk_data[true,-1]
            	    {
            	    pushFollow(FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk_data3210);
            	    accumulate_paren_chunk_data184=accumulate_paren_chunk_data(true, -1);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, accumulate_paren_chunk_data184.getTree());

            	    }
            	    break;

            	default :
            	    break loop70;
                }
            } while (true);

            rp1=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_paren_chunk_data3221); if (state.failed) return retval;
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
    // src/main/resources/org/drools/lang/DRL.g:1019:1: accumulate_id_clause : ID paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) ;
    public final DRLParser.accumulate_id_clause_return accumulate_id_clause() throws RecognitionException {
        DRLParser.accumulate_id_clause_return retval = new DRLParser.accumulate_id_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID185=null;
        DRLParser.paren_chunk_return paren_chunk186 = null;


        Object ID185_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL.g:1020:2: ( ID paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL.g:1020:4: ID paren_chunk
            {
            ID185=(Token)match(input,ID,FOLLOW_ID_in_accumulate_id_clause3237); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID185);

            if ( state.backtracking==0 ) {
              	emit(ID185, DroolsEditorType.IDENTIFIER);	
            }
            pushFollow(FOLLOW_paren_chunk_in_accumulate_id_clause3243);
            paren_chunk186=paren_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk186.getTree());


            // AST REWRITE
            // elements: paren_chunk, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1022:2: -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
            {
                // src/main/resources/org/drools/lang/DRL.g:1022:5: ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
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
    // src/main/resources/org/drools/lang/DRL.g:1025:1: collect_statement : COLLECT LEFT_PAREN pattern_source RIGHT_PAREN -> ^( COLLECT pattern_source RIGHT_PAREN ) ;
    public final DRLParser.collect_statement_return collect_statement() throws RecognitionException {
        DRLParser.collect_statement_return retval = new DRLParser.collect_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLLECT187=null;
        Token LEFT_PAREN188=null;
        Token RIGHT_PAREN190=null;
        DRLParser.pattern_source_return pattern_source189 = null;


        Object COLLECT187_tree=null;
        Object LEFT_PAREN188_tree=null;
        Object RIGHT_PAREN190_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleTokenStream stream_COLLECT=new RewriteRuleTokenStream(adaptor,"token COLLECT");
        RewriteRuleSubtreeStream stream_pattern_source=new RewriteRuleSubtreeStream(adaptor,"rule pattern_source");
        try {
            // src/main/resources/org/drools/lang/DRL.g:1026:2: ( COLLECT LEFT_PAREN pattern_source RIGHT_PAREN -> ^( COLLECT pattern_source RIGHT_PAREN ) )
            // src/main/resources/org/drools/lang/DRL.g:1026:4: COLLECT LEFT_PAREN pattern_source RIGHT_PAREN
            {
            COLLECT187=(Token)match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement3265); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLLECT.add(COLLECT187);

            if ( state.backtracking==0 ) {
              	emit(COLLECT187, DroolsEditorType.KEYWORD);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_COLLECT);	
            }
            LEFT_PAREN188=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement3274); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN188);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN188, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_pattern_source_in_collect_statement3281);
            pattern_source189=pattern_source();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_pattern_source.add(pattern_source189.getTree());
            RIGHT_PAREN190=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement3286); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN190);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN190, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }


            // AST REWRITE
            // elements: pattern_source, COLLECT, RIGHT_PAREN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1032:2: -> ^( COLLECT pattern_source RIGHT_PAREN )
            {
                // src/main/resources/org/drools/lang/DRL.g:1032:5: ^( COLLECT pattern_source RIGHT_PAREN )
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
    // src/main/resources/org/drools/lang/DRL.g:1035:1: entrypoint_statement : entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) ;
    public final DRLParser.entrypoint_statement_return entrypoint_statement() throws RecognitionException {
        DRLParser.entrypoint_statement_return retval = new DRLParser.entrypoint_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.entry_point_key_return entry_point_key191 = null;

        DRLParser.entrypoint_id_return entrypoint_id192 = null;


        RewriteRuleSubtreeStream stream_entrypoint_id=new RewriteRuleSubtreeStream(adaptor,"rule entrypoint_id");
        RewriteRuleSubtreeStream stream_entry_point_key=new RewriteRuleSubtreeStream(adaptor,"rule entry_point_key");
        try {
            // src/main/resources/org/drools/lang/DRL.g:1036:2: ( entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) )
            // src/main/resources/org/drools/lang/DRL.g:1036:4: entry_point_key entrypoint_id
            {
            pushFollow(FOLLOW_entry_point_key_in_entrypoint_statement3313);
            entry_point_key191=entry_point_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_entry_point_key.add(entry_point_key191.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_COLLECT);	
            }
            pushFollow(FOLLOW_entrypoint_id_in_entrypoint_statement3321);
            entrypoint_id192=entrypoint_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_entrypoint_id.add(entrypoint_id192.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }


            // AST REWRITE
            // elements: entrypoint_id, entry_point_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1040:2: -> ^( entry_point_key entrypoint_id )
            {
                // src/main/resources/org/drools/lang/DRL.g:1040:5: ^( entry_point_key entrypoint_id )
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
    // src/main/resources/org/drools/lang/DRL.g:1043:1: entrypoint_id : (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] );
    public final DRLParser.entrypoint_id_return entrypoint_id() throws RecognitionException {
        DRLParser.entrypoint_id_return retval = new DRLParser.entrypoint_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1044:2: (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] )
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==ID) ) {
                alt71=1;
            }
            else if ( (LA71_0==STRING) ) {
                alt71=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1044:5: value= ID
                    {
                    value=(Token)match(input,ID,FOLLOW_ID_in_entrypoint_id3347); if (state.failed) return retval; 
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
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1045:3: -> VT_ENTRYPOINT_ID[$value]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_ENTRYPOINT_ID, value));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1046:5: value= STRING
                    {
                    value=(Token)match(input,STRING,FOLLOW_STRING_in_entrypoint_id3364); if (state.failed) return retval; 
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
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1047:3: -> VT_ENTRYPOINT_ID[$value]
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
    // src/main/resources/org/drools/lang/DRL.g:1050:1: from_source : ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) ;
    public final DRLParser.from_source_return from_source() throws RecognitionException {
        DRLParser.from_source_return retval = new DRLParser.from_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID193=null;
        DRLParser.paren_chunk_return args = null;

        DRLParser.expression_chain_return expression_chain194 = null;


        Object ID193_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL.g:1051:2: ( ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) )
            // src/main/resources/org/drools/lang/DRL.g:1051:4: ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )?
            {
            ID193=(Token)match(input,ID,FOLLOW_ID_in_from_source3384); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID193);

            if ( state.backtracking==0 ) {
              	emit(ID193, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL.g:1052:3: ( ( LEFT_PAREN )=>args= paren_chunk )?
            int alt72=2;
            alt72 = dfa72.predict(input);
            switch (alt72) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1052:5: ( LEFT_PAREN )=>args= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source3399);
                    args=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_paren_chunk.add(args.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL.g:1053:3: ( expression_chain )?
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==DOT) ) {
                alt73=1;
            }
            switch (alt73) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1053:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source3406);
                    expression_chain194=expression_chain();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression_chain.add(expression_chain194.getTree());

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
            // elements: expression_chain, paren_chunk, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1059:2: -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
            {
                // src/main/resources/org/drools/lang/DRL.g:1059:5: ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FROM_SOURCE, "VT_FROM_SOURCE"), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // src/main/resources/org/drools/lang/DRL.g:1059:25: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                }
                stream_paren_chunk.reset();
                // src/main/resources/org/drools/lang/DRL.g:1059:38: ( expression_chain )?
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
    // src/main/resources/org/drools/lang/DRL.g:1062:1: expression_chain : DOT ID ({...}? paren_chunk | square_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) ;
    public final DRLParser.expression_chain_return expression_chain() throws RecognitionException {
        DRLParser.expression_chain_return retval = new DRLParser.expression_chain_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT195=null;
        Token ID196=null;
        DRLParser.paren_chunk_return paren_chunk197 = null;

        DRLParser.square_chunk_return square_chunk198 = null;

        DRLParser.expression_chain_return expression_chain199 = null;


        Object DOT195_tree=null;
        Object ID196_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL.g:1063:2: ( DOT ID ({...}? paren_chunk | square_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) )
            // src/main/resources/org/drools/lang/DRL.g:1064:3: DOT ID ({...}? paren_chunk | square_chunk )? ( expression_chain )?
            {
            DOT195=(Token)match(input,DOT,FOLLOW_DOT_in_expression_chain3439); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_DOT.add(DOT195);

            if ( state.backtracking==0 ) {
              	emit(DOT195, DroolsEditorType.IDENTIFIER);	
            }
            ID196=(Token)match(input,ID,FOLLOW_ID_in_expression_chain3446); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID196);

            if ( state.backtracking==0 ) {
              	emit(ID196, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL.g:1066:4: ({...}? paren_chunk | square_chunk )?
            int alt74=3;
            alt74 = dfa74.predict(input);
            switch (alt74) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1067:6: {...}? paren_chunk
                    {
                    if ( !((input.LA(1) == LEFT_PAREN)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "expression_chain", "input.LA(1) == LEFT_PAREN");
                    }
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain3462);
                    paren_chunk197=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk197.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1069:6: square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain3476);
                    square_chunk198=square_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_square_chunk.add(square_chunk198.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL.g:1071:4: ( expression_chain )?
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==DOT) ) {
                alt75=1;
            }
            switch (alt75) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1071:4: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain3487);
                    expression_chain199=expression_chain();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression_chain.add(expression_chain199.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: ID, paren_chunk, expression_chain, square_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1072:4: -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
            {
                // src/main/resources/org/drools/lang/DRL.g:1072:7: ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_EXPRESSION_CHAIN, DOT195), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // src/main/resources/org/drools/lang/DRL.g:1072:38: ( square_chunk )?
                if ( stream_square_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_square_chunk.nextTree());

                }
                stream_square_chunk.reset();
                // src/main/resources/org/drools/lang/DRL.g:1072:52: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                }
                stream_paren_chunk.reset();
                // src/main/resources/org/drools/lang/DRL.g:1072:65: ( expression_chain )?
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
    // src/main/resources/org/drools/lang/DRL.g:1075:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );
    public final DRLParser.lhs_pattern_return lhs_pattern() throws RecognitionException {
        DRLParser.lhs_pattern_return retval = new DRLParser.lhs_pattern_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.fact_binding_return fact_binding200 = null;

        DRLParser.fact_return fact201 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // src/main/resources/org/drools/lang/DRL.g:1076:2: ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==ID) ) {
                int LA76_1 = input.LA(2);

                if ( (LA76_1==COLON) ) {
                    alt76=1;
                }
                else if ( (LA76_1==DOT||LA76_1==LEFT_PAREN||LA76_1==LEFT_SQUARE) ) {
                    alt76=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 76, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1076:4: fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern3520);
                    fact_binding200=fact_binding();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fact_binding.add(fact_binding200.getTree());


                    // AST REWRITE
                    // elements: fact_binding
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1076:17: -> ^( VT_PATTERN fact_binding )
                    {
                        // src/main/resources/org/drools/lang/DRL.g:1076:20: ^( VT_PATTERN fact_binding )
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
                    // src/main/resources/org/drools/lang/DRL.g:1077:4: fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern3533);
                    fact201=fact();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fact.add(fact201.getTree());


                    // AST REWRITE
                    // elements: fact
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1077:9: -> ^( VT_PATTERN fact )
                    {
                        // src/main/resources/org/drools/lang/DRL.g:1077:12: ^( VT_PATTERN fact )
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
    // src/main/resources/org/drools/lang/DRL.g:1080:1: fact_binding : label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) ;
    public final DRLParser.fact_binding_return fact_binding() throws RecognitionException {
        DRLParser.fact_binding_return retval = new DRLParser.fact_binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN204=null;
        Token RIGHT_PAREN206=null;
        DRLParser.label_return label202 = null;

        DRLParser.fact_return fact203 = null;

        DRLParser.fact_binding_expression_return fact_binding_expression205 = null;


        Object LEFT_PAREN204_tree=null;
        Object RIGHT_PAREN206_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        RewriteRuleSubtreeStream stream_fact_binding_expression=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding_expression");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // src/main/resources/org/drools/lang/DRL.g:1081:3: ( label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) )
            // src/main/resources/org/drools/lang/DRL.g:1081:5: label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            {
            pushFollow(FOLLOW_label_in_fact_binding3553);
            label202=label();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_label.add(label202.getTree());
            // src/main/resources/org/drools/lang/DRL.g:1082:3: ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==ID) ) {
                alt77=1;
            }
            else if ( (LA77_0==LEFT_PAREN) ) {
                alt77=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1082:5: fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding3559);
                    fact203=fact();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fact.add(fact203.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1083:6: LEFT_PAREN fact_binding_expression RIGHT_PAREN
                    {
                    LEFT_PAREN204=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding3566); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN204);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN204, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_fact_binding_expression_in_fact_binding3574);
                    fact_binding_expression205=fact_binding_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fact_binding_expression.add(fact_binding_expression205.getTree());
                    RIGHT_PAREN206=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding3582); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN206);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN206, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }



            // AST REWRITE
            // elements: RIGHT_PAREN, fact, label, fact_binding_expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1087:3: -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
            {
                // src/main/resources/org/drools/lang/DRL.g:1087:6: ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FACT_BINDING, "VT_FACT_BINDING"), root_1);

                adaptor.addChild(root_1, stream_label.nextTree());
                // src/main/resources/org/drools/lang/DRL.g:1087:30: ( fact )?
                if ( stream_fact.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact.nextTree());

                }
                stream_fact.reset();
                // src/main/resources/org/drools/lang/DRL.g:1087:36: ( fact_binding_expression )?
                if ( stream_fact_binding_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact_binding_expression.nextTree());

                }
                stream_fact_binding_expression.reset();
                // src/main/resources/org/drools/lang/DRL.g:1087:61: ( RIGHT_PAREN )?
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
    // src/main/resources/org/drools/lang/DRL.g:1090:1: fact_binding_expression : ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* ;
    public final DRLParser.fact_binding_expression_return fact_binding_expression() throws RecognitionException {
        DRLParser.fact_binding_expression_return retval = new DRLParser.fact_binding_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        DRLParser.or_key_return value = null;

        DRLParser.fact_return fact207 = null;

        DRLParser.fact_return fact208 = null;


        Object pipe_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");

        	Token orToken = null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1093:3: ( ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* )
            // src/main/resources/org/drools/lang/DRL.g:1093:5: ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            {
            // src/main/resources/org/drools/lang/DRL.g:1093:5: ( fact -> fact )
            // src/main/resources/org/drools/lang/DRL.g:1093:6: fact
            {
            pushFollow(FOLLOW_fact_in_fact_binding_expression3623);
            fact207=fact();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_fact.add(fact207.getTree());


            // AST REWRITE
            // elements: fact
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1093:11: -> fact
            {
                adaptor.addChild(root_0, stream_fact.nextTree());

            }

            retval.tree = root_0;}
            }

            // src/main/resources/org/drools/lang/DRL.g:1093:20: ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                    alt79=1;
                }
                else if ( (LA79_0==DOUBLE_PIPE) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1093:22: (value= or_key | pipe= DOUBLE_PIPE ) fact
            	    {
            	    // src/main/resources/org/drools/lang/DRL.g:1093:22: (value= or_key | pipe= DOUBLE_PIPE )
            	    int alt78=2;
            	    int LA78_0 = input.LA(1);

            	    if ( (LA78_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            	        alt78=1;
            	    }
            	    else if ( (LA78_0==DOUBLE_PIPE) ) {
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
            	            // src/main/resources/org/drools/lang/DRL.g:1093:23: value= or_key
            	            {
            	            pushFollow(FOLLOW_or_key_in_fact_binding_expression3635);
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
            	            // src/main/resources/org/drools/lang/DRL.g:1093:62: pipe= DOUBLE_PIPE
            	            {
            	            pipe=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3641); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

            	            if ( state.backtracking==0 ) {
            	              orToken = pipe;
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_fact_in_fact_binding_expression3646);
            	    fact208=fact();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_fact.add(fact208.getTree());


            	    // AST REWRITE
            	    // elements: fact_binding_expression, fact
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 1094:3: -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
            	    {
            	        // src/main/resources/org/drools/lang/DRL.g:1094:6: ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
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
            	    break loop79;
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
    // src/main/resources/org/drools/lang/DRL.g:1097:1: fact : pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) ;
    public final DRLParser.fact_return fact() throws RecognitionException {
        DRLParser.fact_return retval = new DRLParser.fact_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN210=null;
        Token RIGHT_PAREN212=null;
        DRLParser.pattern_type_return pattern_type209 = null;

        DRLParser.constraints_return constraints211 = null;


        Object LEFT_PAREN210_tree=null;
        Object RIGHT_PAREN212_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_pattern_type=new RewriteRuleSubtreeStream(adaptor,"rule pattern_type");
        RewriteRuleSubtreeStream stream_constraints=new RewriteRuleSubtreeStream(adaptor,"rule constraints");
         boolean isFailedOnConstraints = true; pushParaphrases(DroolsParaphraseTypes.PATTERN); 
        try {
            // src/main/resources/org/drools/lang/DRL.g:1100:2: ( pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) )
            // src/main/resources/org/drools/lang/DRL.g:1100:4: pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN
            {
            pushFollow(FOLLOW_pattern_type_in_fact3686);
            pattern_type209=pattern_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_pattern_type.add(pattern_type209.getTree());
            LEFT_PAREN210=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact3691); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN210);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN210, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	
            }
            // src/main/resources/org/drools/lang/DRL.g:1103:4: ( constraints )?
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==ID||LA80_0==LEFT_PAREN) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1103:4: constraints
                    {
                    pushFollow(FOLLOW_constraints_in_fact3702);
                    constraints211=constraints();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_constraints.add(constraints211.getTree());

                    }
                    break;

            }

            RIGHT_PAREN212=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact3708); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN212);

            if ( state.backtracking==0 ) {
              	isFailedOnConstraints = false;	
            }
            if ( state.backtracking==0 ) {
              	if ((RIGHT_PAREN212!=null?RIGHT_PAREN212.getText():null).equals(")") ){ //WORKAROUND FOR ANTLR BUG!
              			emit(RIGHT_PAREN212, DroolsEditorType.SYMBOL);
              			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);
              		}	
            }


            // AST REWRITE
            // elements: constraints, RIGHT_PAREN, pattern_type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1109:2: -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
            {
                // src/main/resources/org/drools/lang/DRL.g:1109:5: ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FACT, "VT_FACT"), root_1);

                adaptor.addChild(root_1, stream_pattern_type.nextTree());
                // src/main/resources/org/drools/lang/DRL.g:1109:28: ( constraints )?
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
    // src/main/resources/org/drools/lang/DRL.g:1119:1: constraints : constraint ( COMMA constraint )* ;
    public final DRLParser.constraints_return constraints() throws RecognitionException {
        DRLParser.constraints_return retval = new DRLParser.constraints_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA214=null;
        DRLParser.constraint_return constraint213 = null;

        DRLParser.constraint_return constraint215 = null;


        Object COMMA214_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1120:2: ( constraint ( COMMA constraint )* )
            // src/main/resources/org/drools/lang/DRL.g:1120:4: constraint ( COMMA constraint )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_in_constraints3742);
            constraint213=constraint();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint213.getTree());
            // src/main/resources/org/drools/lang/DRL.g:1120:15: ( COMMA constraint )*
            loop81:
            do {
                int alt81=2;
                int LA81_0 = input.LA(1);

                if ( (LA81_0==COMMA) ) {
                    alt81=1;
                }


                switch (alt81) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1120:17: COMMA constraint
            	    {
            	    COMMA214=(Token)match(input,COMMA,FOLLOW_COMMA_in_constraints3746); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA214, DroolsEditorType.SYMBOL);
            	      		emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	
            	    }
            	    pushFollow(FOLLOW_constraint_in_constraints3753);
            	    constraint215=constraint();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint215.getTree());

            	    }
            	    break;

            	default :
            	    break loop81;
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
    // src/main/resources/org/drools/lang/DRL.g:1125:1: constraint : or_constr ;
    public final DRLParser.constraint_return constraint() throws RecognitionException {
        DRLParser.constraint_return retval = new DRLParser.constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.or_constr_return or_constr216 = null;



        try {
            // src/main/resources/org/drools/lang/DRL.g:1126:2: ( or_constr )
            // src/main/resources/org/drools/lang/DRL.g:1126:4: or_constr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_or_constr_in_constraint3767);
            or_constr216=or_constr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, or_constr216.getTree());

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
    // src/main/resources/org/drools/lang/DRL.g:1129:1: or_constr : and_constr ( DOUBLE_PIPE and_constr )* ;
    public final DRLParser.or_constr_return or_constr() throws RecognitionException {
        DRLParser.or_constr_return retval = new DRLParser.or_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE218=null;
        DRLParser.and_constr_return and_constr217 = null;

        DRLParser.and_constr_return and_constr219 = null;


        Object DOUBLE_PIPE218_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1130:2: ( and_constr ( DOUBLE_PIPE and_constr )* )
            // src/main/resources/org/drools/lang/DRL.g:1130:4: and_constr ( DOUBLE_PIPE and_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_constr_in_or_constr3778);
            and_constr217=and_constr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, and_constr217.getTree());
            // src/main/resources/org/drools/lang/DRL.g:1130:15: ( DOUBLE_PIPE and_constr )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);

                if ( (LA82_0==DOUBLE_PIPE) ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1130:17: DOUBLE_PIPE and_constr
            	    {
            	    DOUBLE_PIPE218=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr3782); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_PIPE218_tree = (Object)adaptor.create(DOUBLE_PIPE218);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE218_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_PIPE218, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr3789);
            	    and_constr219=and_constr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, and_constr219.getTree());

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
    // $ANTLR end "or_constr"

    public static class and_constr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "and_constr"
    // src/main/resources/org/drools/lang/DRL.g:1134:1: and_constr : unary_constr ( DOUBLE_AMPER unary_constr )* ;
    public final DRLParser.and_constr_return and_constr() throws RecognitionException {
        DRLParser.and_constr_return retval = new DRLParser.and_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER221=null;
        DRLParser.unary_constr_return unary_constr220 = null;

        DRLParser.unary_constr_return unary_constr222 = null;


        Object DOUBLE_AMPER221_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1135:2: ( unary_constr ( DOUBLE_AMPER unary_constr )* )
            // src/main/resources/org/drools/lang/DRL.g:1135:4: unary_constr ( DOUBLE_AMPER unary_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_constr_in_and_constr3804);
            unary_constr220=unary_constr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unary_constr220.getTree());
            // src/main/resources/org/drools/lang/DRL.g:1135:17: ( DOUBLE_AMPER unary_constr )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);

                if ( (LA83_0==DOUBLE_AMPER) ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1135:19: DOUBLE_AMPER unary_constr
            	    {
            	    DOUBLE_AMPER221=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr3808); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_AMPER221_tree = (Object)adaptor.create(DOUBLE_AMPER221);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER221_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_AMPER221, DroolsEditorType.SYMBOL);;	
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr3815);
            	    unary_constr222=unary_constr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unary_constr222.getTree());

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
    // $ANTLR end "and_constr"

    public static class unary_constr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unary_constr"
    // src/main/resources/org/drools/lang/DRL.g:1139:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );
    public final DRLParser.unary_constr_return unary_constr() throws RecognitionException {
        DRLParser.unary_constr_return retval = new DRLParser.unary_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN226=null;
        Token RIGHT_PAREN228=null;
        DRLParser.eval_key_return eval_key223 = null;

        DRLParser.paren_chunk_return paren_chunk224 = null;

        DRLParser.field_constraint_return field_constraint225 = null;

        DRLParser.or_constr_return or_constr227 = null;


        Object LEFT_PAREN226_tree=null;
        Object RIGHT_PAREN228_tree=null;

         boolean isFailed = true;	
        try {
            // src/main/resources/org/drools/lang/DRL.g:1143:2: ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN )
            int alt84=3;
            alt84 = dfa84.predict(input);
            switch (alt84) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1143:4: eval_key paren_chunk
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_eval_key_in_unary_constr3848);
                    eval_key223=eval_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(eval_key223.getTree(), root_0);
                    pushFollow(FOLLOW_paren_chunk_in_unary_constr3851);
                    paren_chunk224=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk224.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1144:4: field_constraint
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_field_constraint_in_unary_constr3856);
                    field_constraint225=field_constraint();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, field_constraint225.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:1145:5: LEFT_PAREN or_constr RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN226=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr3862); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN226, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_or_constr_in_unary_constr3872);
                    or_constr227=or_constr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, or_constr227.getTree());
                    RIGHT_PAREN228=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr3877); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_PAREN228_tree = (Object)adaptor.create(RIGHT_PAREN228);
                    adaptor.addChild(root_0, RIGHT_PAREN228_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN228, DroolsEditorType.SYMBOL);	
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
    // src/main/resources/org/drools/lang/DRL.g:1158:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );
    public final DRLParser.field_constraint_return field_constraint() throws RecognitionException {
        DRLParser.field_constraint_return retval = new DRLParser.field_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token arw=null;
        DRLParser.label_return label229 = null;

        DRLParser.accessor_path_return accessor_path230 = null;

        DRLParser.or_restr_connective_return or_restr_connective231 = null;

        DRLParser.paren_chunk_return paren_chunk232 = null;

        DRLParser.accessor_path_return accessor_path233 = null;

        DRLParser.or_restr_connective_return or_restr_connective234 = null;


        Object arw_tree=null;
        RewriteRuleTokenStream stream_ARROW=new RewriteRuleTokenStream(adaptor,"token ARROW");
        RewriteRuleSubtreeStream stream_accessor_path=new RewriteRuleSubtreeStream(adaptor,"rule accessor_path");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        RewriteRuleSubtreeStream stream_or_restr_connective=new RewriteRuleSubtreeStream(adaptor,"rule or_restr_connective");

        	boolean isArrow = false;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1161:3: ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) )
            int alt86=2;
            int LA86_0 = input.LA(1);

            if ( (LA86_0==ID) ) {
                int LA86_1 = input.LA(2);

                if ( (LA86_1==COLON) ) {
                    alt86=1;
                }
                else if ( ((LA86_1>=ID && LA86_1<=DOT)||LA86_1==LEFT_PAREN||(LA86_1>=EQUAL && LA86_1<=NOT_EQUAL)||LA86_1==LEFT_SQUARE) ) {
                    alt86=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 86, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 86, 0, input);

                throw nvae;
            }
            switch (alt86) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1161:5: label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )?
                    {
                    pushFollow(FOLLOW_label_in_field_constraint3897);
                    label229=label();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_label.add(label229.getTree());
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3899);
                    accessor_path230=accessor_path();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accessor_path.add(accessor_path230.getTree());
                    // src/main/resources/org/drools/lang/DRL.g:1162:3: ( or_restr_connective | arw= ARROW paren_chunk )?
                    int alt85=3;
                    int LA85_0 = input.LA(1);

                    if ( (LA85_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {
                        alt85=1;
                    }
                    else if ( (LA85_0==LEFT_PAREN||(LA85_0>=EQUAL && LA85_0<=NOT_EQUAL)) ) {
                        alt85=1;
                    }
                    else if ( (LA85_0==ARROW) ) {
                        alt85=2;
                    }
                    switch (alt85) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRL.g:1162:5: or_restr_connective
                            {
                            pushFollow(FOLLOW_or_restr_connective_in_field_constraint3906);
                            or_restr_connective231=or_restr_connective();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_or_restr_connective.add(or_restr_connective231.getTree());

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DRL.g:1162:27: arw= ARROW paren_chunk
                            {
                            arw=(Token)match(input,ARROW,FOLLOW_ARROW_in_field_constraint3912); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ARROW.add(arw);

                            if ( state.backtracking==0 ) {
                              	emit(arw, DroolsEditorType.SYMBOL);	
                            }
                            pushFollow(FOLLOW_paren_chunk_in_field_constraint3916);
                            paren_chunk232=paren_chunk();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk232.getTree());
                            if ( state.backtracking==0 ) {
                              isArrow = true;
                            }

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: label, accessor_path, accessor_path, label, or_restr_connective, paren_chunk
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1163:3: -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )?
                    if (isArrow) {
                        // src/main/resources/org/drools/lang/DRL.g:1163:17: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.nextTree());
                        // src/main/resources/org/drools/lang/DRL.g:1163:39: ^( VT_FIELD accessor_path )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }
                        // src/main/resources/org/drools/lang/DRL.g:1163:66: ( ^( VK_EVAL[$arw] paren_chunk ) )?
                        if ( stream_paren_chunk.hasNext() ) {
                            // src/main/resources/org/drools/lang/DRL.g:1163:66: ^( VK_EVAL[$arw] paren_chunk )
                            {
                            Object root_1 = (Object)adaptor.nil();
                            root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VK_EVAL, arw), root_1);

                            adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                            adaptor.addChild(root_0, root_1);
                            }

                        }
                        stream_paren_chunk.reset();

                    }
                    else // 1164:3: -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                    {
                        // src/main/resources/org/drools/lang/DRL.g:1164:6: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.nextTree());
                        // src/main/resources/org/drools/lang/DRL.g:1164:28: ^( VT_FIELD accessor_path ( or_restr_connective )? )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.nextTree());
                        // src/main/resources/org/drools/lang/DRL.g:1164:53: ( or_restr_connective )?
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
                    // src/main/resources/org/drools/lang/DRL.g:1165:4: accessor_path or_restr_connective
                    {
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3970);
                    accessor_path233=accessor_path();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accessor_path.add(accessor_path233.getTree());
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint3972);
                    or_restr_connective234=or_restr_connective();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_or_restr_connective.add(or_restr_connective234.getTree());


                    // AST REWRITE
                    // elements: accessor_path, or_restr_connective
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1166:3: -> ^( VT_FIELD accessor_path or_restr_connective )
                    {
                        // src/main/resources/org/drools/lang/DRL.g:1166:6: ^( VT_FIELD accessor_path or_restr_connective )
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
    // src/main/resources/org/drools/lang/DRL.g:1169:1: label : value= ID COLON -> VT_LABEL[$value] ;
    public final DRLParser.label_return label() throws RecognitionException {
        DRLParser.label_return retval = new DRLParser.label_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;
        Token COLON235=null;

        Object value_tree=null;
        Object COLON235_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1170:2: (value= ID COLON -> VT_LABEL[$value] )
            // src/main/resources/org/drools/lang/DRL.g:1170:4: value= ID COLON
            {
            value=(Token)match(input,ID,FOLLOW_ID_in_label3997); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(value);

            if ( state.backtracking==0 ) {
              	emit(value, DroolsEditorType.IDENTIFIER_VARIABLE);	
            }
            COLON235=(Token)match(input,COLON,FOLLOW_COLON_in_label4004); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON235);

            if ( state.backtracking==0 ) {
              	emit(COLON235, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1172:3: -> VT_LABEL[$value]
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
    // src/main/resources/org/drools/lang/DRL.g:1175:1: or_restr_connective : and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* ;
    public final DRLParser.or_restr_connective_return or_restr_connective() throws RecognitionException {
        DRLParser.or_restr_connective_return retval = new DRLParser.or_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE237=null;
        DRLParser.and_restr_connective_return and_restr_connective236 = null;

        DRLParser.and_restr_connective_return and_restr_connective238 = null;


        Object DOUBLE_PIPE237_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1176:2: ( and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* )
            // src/main/resources/org/drools/lang/DRL.g:1176:4: and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective4025);
            and_restr_connective236=and_restr_connective();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, and_restr_connective236.getTree());
            // src/main/resources/org/drools/lang/DRL.g:1176:25: ({...}? => DOUBLE_PIPE and_restr_connective )*
            loop87:
            do {
                int alt87=2;
                int LA87_0 = input.LA(1);

                if ( (LA87_0==DOUBLE_PIPE) ) {
                    int LA87_2 = input.LA(2);

                    if ( (((validateRestr()))) ) {
                        alt87=1;
                    }


                }


                switch (alt87) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1176:26: {...}? => DOUBLE_PIPE and_restr_connective
            	    {
            	    if ( !(((validateRestr()))) ) {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        throw new FailedPredicateException(input, "or_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_PIPE237=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective4031); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_PIPE237_tree = (Object)adaptor.create(DOUBLE_PIPE237);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE237_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_PIPE237, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective4039);
            	    and_restr_connective238=and_restr_connective();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, and_restr_connective238.getTree());

            	    }
            	    break;

            	default :
            	    break loop87;
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
    // src/main/resources/org/drools/lang/DRL.g:1189:1: and_restr_connective : constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* ;
    public final DRLParser.and_restr_connective_return and_restr_connective() throws RecognitionException {
        DRLParser.and_restr_connective_return retval = new DRLParser.and_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER240=null;
        DRLParser.constraint_expression_return constraint_expression239 = null;

        DRLParser.constraint_expression_return constraint_expression241 = null;


        Object DOUBLE_AMPER240_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1190:2: ( constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* )
            // src/main/resources/org/drools/lang/DRL.g:1190:4: constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective4060);
            constraint_expression239=constraint_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint_expression239.getTree());
            // src/main/resources/org/drools/lang/DRL.g:1190:26: ({...}? => DOUBLE_AMPER constraint_expression )*
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);

                if ( (LA88_0==DOUBLE_AMPER) ) {
                    int LA88_2 = input.LA(2);

                    if ( (((validateRestr()))) ) {
                        alt88=1;
                    }


                }


                switch (alt88) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1190:27: {...}? => DOUBLE_AMPER constraint_expression
            	    {
            	    if ( !(((validateRestr()))) ) {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        throw new FailedPredicateException(input, "and_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_AMPER240=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective4066); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_AMPER240_tree = (Object)adaptor.create(DOUBLE_AMPER240);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER240_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_AMPER240, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective4073);
            	    constraint_expression241=constraint_expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint_expression241.getTree());

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
    // $ANTLR end "and_restr_connective"

    public static class constraint_expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constraint_expression"
    // src/main/resources/org/drools/lang/DRL.g:1203:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );
    public final DRLParser.constraint_expression_return constraint_expression() throws RecognitionException {
        DRLParser.constraint_expression_return retval = new DRLParser.constraint_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN244=null;
        Token RIGHT_PAREN246=null;
        DRLParser.compound_operator_return compound_operator242 = null;

        DRLParser.simple_operator_return simple_operator243 = null;

        DRLParser.or_restr_connective_return or_restr_connective245 = null;


        Object LEFT_PAREN244_tree=null;
        Object RIGHT_PAREN246_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1206:3: ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN )
            int alt89=3;
            alt89 = dfa89.predict(input);
            switch (alt89) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1206:5: compound_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_compound_operator_in_constraint_expression4101);
                    compound_operator242=compound_operator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, compound_operator242.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1207:4: simple_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_simple_operator_in_constraint_expression4106);
                    simple_operator243=simple_operator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_operator243.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:1208:4: LEFT_PAREN or_restr_connective RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN244=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression4111); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN244, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression4120);
                    or_restr_connective245=or_restr_connective();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, or_restr_connective245.getTree());
                    RIGHT_PAREN246=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression4125); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_PAREN246_tree = (Object)adaptor.create(RIGHT_PAREN246);
                    adaptor.addChild(root_0, RIGHT_PAREN246_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN246, DroolsEditorType.SYMBOL);	
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
    // src/main/resources/org/drools/lang/DRL.g:1254:1: simple_operator : ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) ) expression_value ;
    public final DRLParser.simple_operator_return simple_operator() throws RecognitionException {
        DRLParser.simple_operator_return retval = new DRLParser.simple_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUAL247=null;
        Token GREATER248=null;
        Token GREATER_EQUAL249=null;
        Token LESS250=null;
        Token LESS_EQUAL251=null;
        Token NOT_EQUAL252=null;
        DRLParser.not_key_return not_key253 = null;

        DRLParser.operator_key_return operator_key254 = null;

        DRLParser.square_chunk_return square_chunk255 = null;

        DRLParser.expression_value_return expression_value256 = null;


        Object EQUAL247_tree=null;
        Object GREATER248_tree=null;
        Object GREATER_EQUAL249_tree=null;
        Object LESS250_tree=null;
        Object LESS_EQUAL251_tree=null;
        Object NOT_EQUAL252_tree=null;

        if ( state.backtracking==0 ) emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
        try {
            // src/main/resources/org/drools/lang/DRL.g:1256:2: ( ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) ) expression_value )
            // src/main/resources/org/drools/lang/DRL.g:1257:2: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) ) expression_value
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL.g:1257:2: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) )
            int alt92=7;
            int LA92_0 = input.LA(1);

            if ( (LA92_0==EQUAL) ) {
                alt92=1;
            }
            else if ( (LA92_0==GREATER) ) {
                alt92=2;
            }
            else if ( (LA92_0==GREATER_EQUAL) ) {
                alt92=3;
            }
            else if ( (LA92_0==LESS) ) {
                alt92=4;
            }
            else if ( (LA92_0==LESS_EQUAL) ) {
                alt92=5;
            }
            else if ( (LA92_0==NOT_EQUAL) ) {
                alt92=6;
            }
            else if ( (LA92_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {
                alt92=7;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 92, 0, input);

                throw nvae;
            }
            switch (alt92) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1258:3: EQUAL
                    {
                    EQUAL247=(Token)match(input,EQUAL,FOLLOW_EQUAL_in_simple_operator4160); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQUAL247_tree = (Object)adaptor.create(EQUAL247);
                    root_0 = (Object)adaptor.becomeRoot(EQUAL247_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(EQUAL247, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1259:4: GREATER
                    {
                    GREATER248=(Token)match(input,GREATER,FOLLOW_GREATER_in_simple_operator4168); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER248_tree = (Object)adaptor.create(GREATER248);
                    root_0 = (Object)adaptor.becomeRoot(GREATER248_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(GREATER248, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:1260:4: GREATER_EQUAL
                    {
                    GREATER_EQUAL249=(Token)match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_simple_operator4176); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER_EQUAL249_tree = (Object)adaptor.create(GREATER_EQUAL249);
                    root_0 = (Object)adaptor.becomeRoot(GREATER_EQUAL249_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(GREATER_EQUAL249, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRL.g:1261:4: LESS
                    {
                    LESS250=(Token)match(input,LESS,FOLLOW_LESS_in_simple_operator4184); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS250_tree = (Object)adaptor.create(LESS250);
                    root_0 = (Object)adaptor.becomeRoot(LESS250_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(LESS250, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRL.g:1262:4: LESS_EQUAL
                    {
                    LESS_EQUAL251=(Token)match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_simple_operator4192); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS_EQUAL251_tree = (Object)adaptor.create(LESS_EQUAL251);
                    root_0 = (Object)adaptor.becomeRoot(LESS_EQUAL251_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(LESS_EQUAL251, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRL.g:1263:4: NOT_EQUAL
                    {
                    NOT_EQUAL252=(Token)match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_simple_operator4200); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NOT_EQUAL252_tree = (Object)adaptor.create(NOT_EQUAL252);
                    root_0 = (Object)adaptor.becomeRoot(NOT_EQUAL252_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(NOT_EQUAL252, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRL.g:1264:4: ( not_key )? ( operator_key ( square_chunk )? )
                    {
                    // src/main/resources/org/drools/lang/DRL.g:1264:4: ( not_key )?
                    int alt90=2;
                    int LA90_0 = input.LA(1);

                    if ( (LA90_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {
                        int LA90_1 = input.LA(2);

                        if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                            alt90=1;
                        }
                    }
                    switch (alt90) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRL.g:1264:4: not_key
                            {
                            pushFollow(FOLLOW_not_key_in_simple_operator4208);
                            not_key253=not_key();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, not_key253.getTree());

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRL.g:1265:3: ( operator_key ( square_chunk )? )
                    // src/main/resources/org/drools/lang/DRL.g:1265:5: operator_key ( square_chunk )?
                    {
                    pushFollow(FOLLOW_operator_key_in_simple_operator4215);
                    operator_key254=operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(operator_key254.getTree(), root_0);
                    // src/main/resources/org/drools/lang/DRL.g:1265:19: ( square_chunk )?
                    int alt91=2;
                    int LA91_0 = input.LA(1);

                    if ( (LA91_0==LEFT_SQUARE) ) {
                        alt91=1;
                    }
                    switch (alt91) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRL.g:1265:19: square_chunk
                            {
                            pushFollow(FOLLOW_square_chunk_in_simple_operator4218);
                            square_chunk255=square_chunk();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, square_chunk255.getTree());

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
            pushFollow(FOLLOW_expression_value_in_simple_operator4230);
            expression_value256=expression_value();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression_value256.getTree());

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
    // src/main/resources/org/drools/lang/DRL.g:1272:1: compound_operator : ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN ;
    public final DRLParser.compound_operator_return compound_operator() throws RecognitionException {
        DRLParser.compound_operator_return retval = new DRLParser.compound_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN260=null;
        Token COMMA262=null;
        Token RIGHT_PAREN264=null;
        DRLParser.in_key_return in_key257 = null;

        DRLParser.not_key_return not_key258 = null;

        DRLParser.in_key_return in_key259 = null;

        DRLParser.expression_value_return expression_value261 = null;

        DRLParser.expression_value_return expression_value263 = null;


        Object LEFT_PAREN260_tree=null;
        Object COMMA262_tree=null;
        Object RIGHT_PAREN264_tree=null;

         if ( state.backtracking==0 ) emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR); 
        try {
            // src/main/resources/org/drools/lang/DRL.g:1274:2: ( ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN )
            // src/main/resources/org/drools/lang/DRL.g:1275:2: ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL.g:1275:2: ( in_key | not_key in_key )
            int alt93=2;
            int LA93_0 = input.LA(1);

            if ( (LA93_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((validateIdentifierKey(DroolsSoftKeywords.IN)))))) {
                int LA93_1 = input.LA(2);

                if ( (LA93_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IN))))) {
                    alt93=1;
                }
                else if ( (LA93_1==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))))) {
                    alt93=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 93, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }
            switch (alt93) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1275:4: in_key
                    {
                    pushFollow(FOLLOW_in_key_in_compound_operator4252);
                    in_key257=in_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key257.getTree(), root_0);

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1275:14: not_key in_key
                    {
                    pushFollow(FOLLOW_not_key_in_compound_operator4257);
                    not_key258=not_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, not_key258.getTree());
                    pushFollow(FOLLOW_in_key_in_compound_operator4259);
                    in_key259=in_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key259.getTree(), root_0);

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);	
            }
            LEFT_PAREN260=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator4270); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN260, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_expression_value_in_compound_operator4278);
            expression_value261=expression_value();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression_value261.getTree());
            // src/main/resources/org/drools/lang/DRL.g:1278:21: ( COMMA expression_value )*
            loop94:
            do {
                int alt94=2;
                int LA94_0 = input.LA(1);

                if ( (LA94_0==COMMA) ) {
                    alt94=1;
                }


                switch (alt94) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1278:23: COMMA expression_value
            	    {
            	    COMMA262=(Token)match(input,COMMA,FOLLOW_COMMA_in_compound_operator4282); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA262, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_expression_value_in_compound_operator4287);
            	    expression_value263=expression_value();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression_value263.getTree());

            	    }
            	    break;

            	default :
            	    break loop94;
                }
            } while (true);

            RIGHT_PAREN264=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator4295); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_PAREN264_tree = (Object)adaptor.create(RIGHT_PAREN264);
            adaptor.addChild(root_0, RIGHT_PAREN264_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN264, DroolsEditorType.SYMBOL);	
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
    // src/main/resources/org/drools/lang/DRL.g:1289:1: operator_key : {...}? =>id= ID -> VK_OPERATOR[$id] ;
    public final DRLParser.operator_key_return operator_key() throws RecognitionException {
        DRLParser.operator_key_return retval = new DRLParser.operator_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1290:2: ({...}? =>id= ID -> VK_OPERATOR[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1290:9: {...}? =>id= ID
            {
            if ( !(((isPluggableEvaluator(false)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "operator_key", "(isPluggableEvaluator(false))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_operator_key4326); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1292:9: -> VK_OPERATOR[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1295:1: neg_operator_key : {...}? =>id= ID -> VK_OPERATOR[$id] ;
    public final DRLParser.neg_operator_key_return neg_operator_key() throws RecognitionException {
        DRLParser.neg_operator_key_return retval = new DRLParser.neg_operator_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1296:2: ({...}? =>id= ID -> VK_OPERATOR[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1296:9: {...}? =>id= ID
            {
            if ( !(((isPluggableEvaluator(true)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "neg_operator_key", "(isPluggableEvaluator(true))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key4371); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1298:9: -> VK_OPERATOR[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1301:1: expression_value : ( accessor_path | literal_constraint | paren_chunk ) ;
    public final DRLParser.expression_value_return expression_value() throws RecognitionException {
        DRLParser.expression_value_return retval = new DRLParser.expression_value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.accessor_path_return accessor_path265 = null;

        DRLParser.literal_constraint_return literal_constraint266 = null;

        DRLParser.paren_chunk_return paren_chunk267 = null;



        try {
            // src/main/resources/org/drools/lang/DRL.g:1302:2: ( ( accessor_path | literal_constraint | paren_chunk ) )
            // src/main/resources/org/drools/lang/DRL.g:1302:4: ( accessor_path | literal_constraint | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/DRL.g:1302:4: ( accessor_path | literal_constraint | paren_chunk )
            int alt95=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt95=1;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt95=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt95=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 95, 0, input);

                throw nvae;
            }

            switch (alt95) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1302:5: accessor_path
                    {
                    pushFollow(FOLLOW_accessor_path_in_expression_value4408);
                    accessor_path265=accessor_path();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, accessor_path265.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1303:4: literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_expression_value4413);
                    literal_constraint266=literal_constraint();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal_constraint266.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:1304:4: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_value4419);
                    paren_chunk267=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk267.getTree());

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
    // src/main/resources/org/drools/lang/DRL.g:1318:1: literal_constraint : ( STRING | INT | FLOAT | BOOL | NULL );
    public final DRLParser.literal_constraint_return literal_constraint() throws RecognitionException {
        DRLParser.literal_constraint_return retval = new DRLParser.literal_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING268=null;
        Token INT269=null;
        Token FLOAT270=null;
        Token BOOL271=null;
        Token NULL272=null;

        Object STRING268_tree=null;
        Object INT269_tree=null;
        Object FLOAT270_tree=null;
        Object BOOL271_tree=null;
        Object NULL272_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1319:2: ( STRING | INT | FLOAT | BOOL | NULL )
            int alt96=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt96=1;
                }
                break;
            case INT:
                {
                alt96=2;
                }
                break;
            case FLOAT:
                {
                alt96=3;
                }
                break;
            case BOOL:
                {
                alt96=4;
                }
                break;
            case NULL:
                {
                alt96=5;
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
                    // src/main/resources/org/drools/lang/DRL.g:1319:4: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING268=(Token)match(input,STRING,FOLLOW_STRING_in_literal_constraint4438); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING268_tree = (Object)adaptor.create(STRING268);
                    adaptor.addChild(root_0, STRING268_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(STRING268, DroolsEditorType.STRING_CONST);	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1320:4: INT
                    {
                    root_0 = (Object)adaptor.nil();

                    INT269=(Token)match(input,INT,FOLLOW_INT_in_literal_constraint4445); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT269_tree = (Object)adaptor.create(INT269);
                    adaptor.addChild(root_0, INT269_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(INT269, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:1321:4: FLOAT
                    {
                    root_0 = (Object)adaptor.nil();

                    FLOAT270=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint4452); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FLOAT270_tree = (Object)adaptor.create(FLOAT270);
                    adaptor.addChild(root_0, FLOAT270_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(FLOAT270, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRL.g:1322:4: BOOL
                    {
                    root_0 = (Object)adaptor.nil();

                    BOOL271=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal_constraint4459); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL271_tree = (Object)adaptor.create(BOOL271);
                    adaptor.addChild(root_0, BOOL271_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(BOOL271, DroolsEditorType.BOOLEAN_CONST);	
                    }

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRL.g:1323:4: NULL
                    {
                    root_0 = (Object)adaptor.nil();

                    NULL272=(Token)match(input,NULL,FOLLOW_NULL_in_literal_constraint4466); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NULL272_tree = (Object)adaptor.create(NULL272);
                    adaptor.addChild(root_0, NULL272_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(NULL272, DroolsEditorType.NULL_CONST);	
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
    // src/main/resources/org/drools/lang/DRL.g:1326:1: pattern_type : id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final DRLParser.pattern_type_return pattern_type() throws RecognitionException {
        DRLParser.pattern_type_return retval = new DRLParser.pattern_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;
        DRLParser.dimension_definition_return dimension_definition273 = null;


        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // src/main/resources/org/drools/lang/DRL.g:1327:2: (id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) )
            // src/main/resources/org/drools/lang/DRL.g:1327:4: id+= ID (id+= DOT id+= ID )* ( dimension_definition )*
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_pattern_type4481); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // src/main/resources/org/drools/lang/DRL.g:1327:11: (id+= DOT id+= ID )*
            loop97:
            do {
                int alt97=2;
                int LA97_0 = input.LA(1);

                if ( (LA97_0==DOT) ) {
                    alt97=1;
                }


                switch (alt97) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1327:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_pattern_type4487); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_pattern_type4491); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop97;
                }
            } while (true);

            if ( state.backtracking==0 ) {
              	emit(list_id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.PATTERN, buildStringFromTokens(list_id));	
            }
            // src/main/resources/org/drools/lang/DRL.g:1330:6: ( dimension_definition )*
            loop98:
            do {
                int alt98=2;
                int LA98_0 = input.LA(1);

                if ( (LA98_0==LEFT_SQUARE) ) {
                    alt98=1;
                }


                switch (alt98) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1330:6: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_pattern_type4506);
            	    dimension_definition273=dimension_definition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_dimension_definition.add(dimension_definition273.getTree());

            	    }
            	    break;

            	default :
            	    break loop98;
                }
            } while (true);



            // AST REWRITE
            // elements: ID, dimension_definition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1331:3: -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
            {
                // src/main/resources/org/drools/lang/DRL.g:1331:6: ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
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
                // src/main/resources/org/drools/lang/DRL.g:1331:28: ( dimension_definition )*
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
    // src/main/resources/org/drools/lang/DRL.g:1334:1: data_type : id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final DRLParser.data_type_return data_type() throws RecognitionException {
        DRLParser.data_type_return retval = new DRLParser.data_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;
        DRLParser.dimension_definition_return dimension_definition274 = null;


        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // src/main/resources/org/drools/lang/DRL.g:1335:2: (id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) )
            // src/main/resources/org/drools/lang/DRL.g:1335:4: id+= ID (id+= DOT id+= ID )* ( dimension_definition )*
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_data_type4534); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // src/main/resources/org/drools/lang/DRL.g:1335:11: (id+= DOT id+= ID )*
            loop99:
            do {
                int alt99=2;
                int LA99_0 = input.LA(1);

                if ( (LA99_0==DOT) ) {
                    alt99=1;
                }


                switch (alt99) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1335:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_data_type4540); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_data_type4544); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop99;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DRL.g:1335:31: ( dimension_definition )*
            loop100:
            do {
                int alt100=2;
                int LA100_0 = input.LA(1);

                if ( (LA100_0==LEFT_SQUARE) ) {
                    alt100=1;
                }


                switch (alt100) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1335:31: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_data_type4549);
            	    dimension_definition274=dimension_definition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_dimension_definition.add(dimension_definition274.getTree());

            	    }
            	    break;

            	default :
            	    break loop100;
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1337:3: -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
            {
                // src/main/resources/org/drools/lang/DRL.g:1337:6: ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
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
                // src/main/resources/org/drools/lang/DRL.g:1337:25: ( dimension_definition )*
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
    // src/main/resources/org/drools/lang/DRL.g:1340:1: dimension_definition : LEFT_SQUARE RIGHT_SQUARE ;
    public final DRLParser.dimension_definition_return dimension_definition() throws RecognitionException {
        DRLParser.dimension_definition_return retval = new DRLParser.dimension_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE275=null;
        Token RIGHT_SQUARE276=null;

        Object LEFT_SQUARE275_tree=null;
        Object RIGHT_SQUARE276_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1341:2: ( LEFT_SQUARE RIGHT_SQUARE )
            // src/main/resources/org/drools/lang/DRL.g:1341:4: LEFT_SQUARE RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE275=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dimension_definition4578); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_SQUARE275_tree = (Object)adaptor.create(LEFT_SQUARE275);
            adaptor.addChild(root_0, LEFT_SQUARE275_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(LEFT_SQUARE275, DroolsEditorType.SYMBOL);	
            }
            RIGHT_SQUARE276=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dimension_definition4585); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_SQUARE276_tree = (Object)adaptor.create(RIGHT_SQUARE276);
            adaptor.addChild(root_0, RIGHT_SQUARE276_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(RIGHT_SQUARE276, DroolsEditorType.SYMBOL);	
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
    // src/main/resources/org/drools/lang/DRL.g:1345:1: accessor_path : accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) ;
    public final DRLParser.accessor_path_return accessor_path() throws RecognitionException {
        DRLParser.accessor_path_return retval = new DRLParser.accessor_path_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT278=null;
        DRLParser.accessor_element_return accessor_element277 = null;

        DRLParser.accessor_element_return accessor_element279 = null;


        Object DOT278_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_accessor_element=new RewriteRuleSubtreeStream(adaptor,"rule accessor_element");
        try {
            // src/main/resources/org/drools/lang/DRL.g:1346:2: ( accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) )
            // src/main/resources/org/drools/lang/DRL.g:1346:4: accessor_element ( DOT accessor_element )*
            {
            pushFollow(FOLLOW_accessor_element_in_accessor_path4599);
            accessor_element277=accessor_element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accessor_element.add(accessor_element277.getTree());
            // src/main/resources/org/drools/lang/DRL.g:1346:21: ( DOT accessor_element )*
            loop101:
            do {
                int alt101=2;
                int LA101_0 = input.LA(1);

                if ( (LA101_0==DOT) ) {
                    alt101=1;
                }


                switch (alt101) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1346:23: DOT accessor_element
            	    {
            	    DOT278=(Token)match(input,DOT,FOLLOW_DOT_in_accessor_path4603); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT278);

            	    if ( state.backtracking==0 ) {
            	      	emit(DOT278, DroolsEditorType.IDENTIFIER);	
            	    }
            	    pushFollow(FOLLOW_accessor_element_in_accessor_path4607);
            	    accessor_element279=accessor_element();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_accessor_element.add(accessor_element279.getTree());

            	    }
            	    break;

            	default :
            	    break loop101;
                }
            } while (true);



            // AST REWRITE
            // elements: accessor_element
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1347:2: -> ^( VT_ACCESSOR_PATH ( accessor_element )+ )
            {
                // src/main/resources/org/drools/lang/DRL.g:1347:5: ^( VT_ACCESSOR_PATH ( accessor_element )+ )
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
    // src/main/resources/org/drools/lang/DRL.g:1350:1: accessor_element : ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) ;
    public final DRLParser.accessor_element_return accessor_element() throws RecognitionException {
        DRLParser.accessor_element_return retval = new DRLParser.accessor_element_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID280=null;
        DRLParser.square_chunk_return square_chunk281 = null;


        Object ID280_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        try {
            // src/main/resources/org/drools/lang/DRL.g:1351:2: ( ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) )
            // src/main/resources/org/drools/lang/DRL.g:1351:4: ID ( square_chunk )*
            {
            ID280=(Token)match(input,ID,FOLLOW_ID_in_accessor_element4631); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID280);

            if ( state.backtracking==0 ) {
              	emit(ID280, DroolsEditorType.IDENTIFIER);	
            }
            // src/main/resources/org/drools/lang/DRL.g:1352:3: ( square_chunk )*
            loop102:
            do {
                int alt102=2;
                int LA102_0 = input.LA(1);

                if ( (LA102_0==LEFT_SQUARE) ) {
                    alt102=1;
                }


                switch (alt102) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1352:3: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_accessor_element4637);
            	    square_chunk281=square_chunk();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_square_chunk.add(square_chunk281.getTree());

            	    }
            	    break;

            	default :
            	    break loop102;
                }
            } while (true);



            // AST REWRITE
            // elements: square_chunk, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1353:2: -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
            {
                // src/main/resources/org/drools/lang/DRL.g:1353:5: ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ACCESSOR_ELEMENT, "VT_ACCESSOR_ELEMENT"), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // src/main/resources/org/drools/lang/DRL.g:1353:30: ( square_chunk )*
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
    // src/main/resources/org/drools/lang/DRL.g:1356:1: rhs_chunk : rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] ;
    public final DRLParser.rhs_chunk_return rhs_chunk() throws RecognitionException {
        DRLParser.rhs_chunk_return retval = new DRLParser.rhs_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.rhs_chunk_data_return rc = null;


        RewriteRuleSubtreeStream stream_rhs_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk_data");

        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL.g:1359:3: (rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] )
            // src/main/resources/org/drools/lang/DRL.g:1359:5: rc= rhs_chunk_data
            {
            pushFollow(FOLLOW_rhs_chunk_data_in_rhs_chunk4666);
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1360:2: -> VT_RHS_CHUNK[$rc.start,text]
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
    // src/main/resources/org/drools/lang/DRL.g:1363:1: rhs_chunk_data : THEN ( not_end_key )* end_key ( SEMICOLON )? ;
    public final DRLParser.rhs_chunk_data_return rhs_chunk_data() throws RecognitionException {
        DRLParser.rhs_chunk_data_return retval = new DRLParser.rhs_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token THEN282=null;
        Token SEMICOLON285=null;
        DRLParser.not_end_key_return not_end_key283 = null;

        DRLParser.end_key_return end_key284 = null;


        Object THEN282_tree=null;
        Object SEMICOLON285_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1364:2: ( THEN ( not_end_key )* end_key ( SEMICOLON )? )
            // src/main/resources/org/drools/lang/DRL.g:1364:4: THEN ( not_end_key )* end_key ( SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            THEN282=(Token)match(input,THEN,FOLLOW_THEN_in_rhs_chunk_data4685); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            THEN282_tree = (Object)adaptor.create(THEN282);
            adaptor.addChild(root_0, THEN282_tree);
            }
            if ( state.backtracking==0 ) {
              	if ((THEN282!=null?THEN282.getText():null).equalsIgnoreCase("then")){
              			emit(THEN282, DroolsEditorType.KEYWORD);
              			emit(Location.LOCATION_RHS);
              		}	
            }
            // src/main/resources/org/drools/lang/DRL.g:1369:4: ( not_end_key )*
            loop103:
            do {
                int alt103=2;
                int LA103_0 = input.LA(1);

                if ( (LA103_0==ID) && (((!(validateIdentifierKey(DroolsSoftKeywords.END)))||((validateIdentifierKey(DroolsSoftKeywords.END)))))) {
                    int LA103_1 = input.LA(2);

                    if ( ((!(validateIdentifierKey(DroolsSoftKeywords.END)))) ) {
                        alt103=1;
                    }


                }
                else if ( ((LA103_0>=VT_COMPILATION_UNIT && LA103_0<=SEMICOLON)||(LA103_0>=DOT && LA103_0<=IdentifierPart)) && ((!(validateIdentifierKey(DroolsSoftKeywords.END))))) {
                    alt103=1;
                }


                switch (alt103) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1369:4: not_end_key
            	    {
            	    pushFollow(FOLLOW_not_end_key_in_rhs_chunk_data4694);
            	    not_end_key283=not_end_key();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, not_end_key283.getTree());

            	    }
            	    break;

            	default :
            	    break loop103;
                }
            } while (true);

            pushFollow(FOLLOW_end_key_in_rhs_chunk_data4700);
            end_key284=end_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, end_key284.getTree());
            // src/main/resources/org/drools/lang/DRL.g:1371:3: ( SEMICOLON )?
            int alt104=2;
            int LA104_0 = input.LA(1);

            if ( (LA104_0==SEMICOLON) ) {
                alt104=1;
            }
            switch (alt104) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1371:3: SEMICOLON
                    {
                    SEMICOLON285=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_rhs_chunk_data4705); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMICOLON285_tree = (Object)adaptor.create(SEMICOLON285);
                    adaptor.addChild(root_0, SEMICOLON285_tree);
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON285, DroolsEditorType.KEYWORD);	
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
    // src/main/resources/org/drools/lang/DRL.g:1374:1: curly_chunk : cc= curly_chunk_data[false] -> VT_CURLY_CHUNK[$cc.start,text] ;
    public final DRLParser.curly_chunk_return curly_chunk() throws RecognitionException {
        DRLParser.curly_chunk_return retval = new DRLParser.curly_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.curly_chunk_data_return cc = null;


        RewriteRuleSubtreeStream stream_curly_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule curly_chunk_data");

        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL.g:1377:3: (cc= curly_chunk_data[false] -> VT_CURLY_CHUNK[$cc.start,text] )
            // src/main/resources/org/drools/lang/DRL.g:1377:5: cc= curly_chunk_data[false]
            {
            pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk4724);
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1378:2: -> VT_CURLY_CHUNK[$cc.start,text]
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
    // src/main/resources/org/drools/lang/DRL.g:1381:1: curly_chunk_data[boolean isRecursive] : lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY ;
    public final DRLParser.curly_chunk_data_return curly_chunk_data(boolean isRecursive) throws RecognitionException {
        DRLParser.curly_chunk_data_return retval = new DRLParser.curly_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lc1=null;
        Token any=null;
        Token rc1=null;
        DRLParser.curly_chunk_data_return curly_chunk_data286 = null;


        Object lc1_tree=null;
        Object any_tree=null;
        Object rc1_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1382:2: (lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY )
            // src/main/resources/org/drools/lang/DRL.g:1382:4: lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY
            {
            root_0 = (Object)adaptor.nil();

            lc1=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk_data4747); if (state.failed) return retval;
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
            // src/main/resources/org/drools/lang/DRL.g:1389:4: (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )*
            loop105:
            do {
                int alt105=3;
                int LA105_0 = input.LA(1);

                if ( ((LA105_0>=VT_COMPILATION_UNIT && LA105_0<=THEN)||(LA105_0>=MISC && LA105_0<=IdentifierPart)) ) {
                    alt105=1;
                }
                else if ( (LA105_0==LEFT_CURLY) ) {
                    alt105=2;
                }


                switch (alt105) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1389:5: any=~ ( LEFT_CURLY | RIGHT_CURLY )
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
            	    // src/main/resources/org/drools/lang/DRL.g:1389:87: curly_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk_data4775);
            	    curly_chunk_data286=curly_chunk_data(true);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, curly_chunk_data286.getTree());

            	    }
            	    break;

            	default :
            	    break loop105;
                }
            } while (true);

            rc1=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk_data4786); if (state.failed) return retval;
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
    // src/main/resources/org/drools/lang/DRL.g:1399:1: paren_chunk : pc= paren_chunk_data[false] -> VT_PAREN_CHUNK[$pc.start,text] ;
    public final DRLParser.paren_chunk_return paren_chunk() throws RecognitionException {
        DRLParser.paren_chunk_return retval = new DRLParser.paren_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.paren_chunk_data_return pc = null;


        RewriteRuleSubtreeStream stream_paren_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk_data");

        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL.g:1402:3: (pc= paren_chunk_data[false] -> VT_PAREN_CHUNK[$pc.start,text] )
            // src/main/resources/org/drools/lang/DRL.g:1402:5: pc= paren_chunk_data[false]
            {
            pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk4807);
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1403:2: -> VT_PAREN_CHUNK[$pc.start,text]
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
    // src/main/resources/org/drools/lang/DRL.g:1406:1: paren_chunk_data[boolean isRecursive] : lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN ;
    public final DRLParser.paren_chunk_data_return paren_chunk_data(boolean isRecursive) throws RecognitionException {
        DRLParser.paren_chunk_data_return retval = new DRLParser.paren_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lp1=null;
        Token any=null;
        Token rp1=null;
        DRLParser.paren_chunk_data_return paren_chunk_data287 = null;


        Object lp1_tree=null;
        Object any_tree=null;
        Object rp1_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1407:2: (lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN )
            // src/main/resources/org/drools/lang/DRL.g:1407:4: lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            lp1=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk_data4831); if (state.failed) return retval;
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
            // src/main/resources/org/drools/lang/DRL.g:1414:4: (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )*
            loop106:
            do {
                int alt106=3;
                int LA106_0 = input.LA(1);

                if ( ((LA106_0>=VT_COMPILATION_UNIT && LA106_0<=STRING)||LA106_0==COMMA||(LA106_0>=AT && LA106_0<=IdentifierPart)) ) {
                    alt106=1;
                }
                else if ( (LA106_0==LEFT_PAREN) ) {
                    alt106=2;
                }


                switch (alt106) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1414:5: any=~ ( LEFT_PAREN | RIGHT_PAREN )
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
            	    // src/main/resources/org/drools/lang/DRL.g:1414:87: paren_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk_data4859);
            	    paren_chunk_data287=paren_chunk_data(true);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk_data287.getTree());

            	    }
            	    break;

            	default :
            	    break loop106;
                }
            } while (true);

            rp1=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk_data4870); if (state.failed) return retval;
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
    // src/main/resources/org/drools/lang/DRL.g:1424:1: square_chunk : sc= square_chunk_data[false] -> VT_SQUARE_CHUNK[$sc.start,text] ;
    public final DRLParser.square_chunk_return square_chunk() throws RecognitionException {
        DRLParser.square_chunk_return retval = new DRLParser.square_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.square_chunk_data_return sc = null;


        RewriteRuleSubtreeStream stream_square_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk_data");

        	String text = "";

        try {
            // src/main/resources/org/drools/lang/DRL.g:1427:3: (sc= square_chunk_data[false] -> VT_SQUARE_CHUNK[$sc.start,text] )
            // src/main/resources/org/drools/lang/DRL.g:1427:5: sc= square_chunk_data[false]
            {
            pushFollow(FOLLOW_square_chunk_data_in_square_chunk4891);
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1428:2: -> VT_SQUARE_CHUNK[$sc.start,text]
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
    // src/main/resources/org/drools/lang/DRL.g:1431:1: square_chunk_data[boolean isRecursive] : ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE ;
    public final DRLParser.square_chunk_data_return square_chunk_data(boolean isRecursive) throws RecognitionException {
        DRLParser.square_chunk_data_return retval = new DRLParser.square_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ls1=null;
        Token any=null;
        Token rs1=null;
        DRLParser.square_chunk_data_return square_chunk_data288 = null;


        Object ls1_tree=null;
        Object any_tree=null;
        Object rs1_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1432:2: (ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE )
            // src/main/resources/org/drools/lang/DRL.g:1432:4: ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            ls1=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk_data4914); if (state.failed) return retval;
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
            // src/main/resources/org/drools/lang/DRL.g:1439:4: (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )*
            loop107:
            do {
                int alt107=3;
                int LA107_0 = input.LA(1);

                if ( ((LA107_0>=VT_COMPILATION_UNIT && LA107_0<=NULL)||(LA107_0>=THEN && LA107_0<=IdentifierPart)) ) {
                    alt107=1;
                }
                else if ( (LA107_0==LEFT_SQUARE) ) {
                    alt107=2;
                }


                switch (alt107) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1439:5: any=~ ( LEFT_SQUARE | RIGHT_SQUARE )
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
            	    // src/main/resources/org/drools/lang/DRL.g:1439:88: square_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_square_chunk_data_in_square_chunk_data4941);
            	    square_chunk_data288=square_chunk_data(true);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, square_chunk_data288.getTree());

            	    }
            	    break;

            	default :
            	    break loop107;
                }
            } while (true);

            rs1=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk_data4952); if (state.failed) return retval;
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
    // src/main/resources/org/drools/lang/DRL.g:1449:1: lock_on_active_key : {...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID -> VK_LOCK_ON_ACTIVE[$start, text] ;
    public final DRLParser.lock_on_active_key_return lock_on_active_key() throws RecognitionException {
        DRLParser.lock_on_active_key_return retval = new DRLParser.lock_on_active_key_return();
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
            // src/main/resources/org/drools/lang/DRL.g:1452:3: ({...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID -> VK_LOCK_ON_ACTIVE[$start, text] )
            // src/main/resources/org/drools/lang/DRL.g:1452:5: {...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "lock_on_active_key", "(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, \"-\") && validateLT(5, DroolsSoftKeywords.ACTIVE))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_lock_on_active_key4976); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_lock_on_active_key4980); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_lock_on_active_key4984); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            mis2=(Token)match(input,MISC,FOLLOW_MISC_in_lock_on_active_key4988); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis2);

            id3=(Token)match(input,ID,FOLLOW_ID_in_lock_on_active_key4992); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1458:3: -> VK_LOCK_ON_ACTIVE[$start, text]
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
    // src/main/resources/org/drools/lang/DRL.g:1461:1: date_effective_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EFFECTIVE[$start, text] ;
    public final DRLParser.date_effective_key_return date_effective_key() throws RecognitionException {
        DRLParser.date_effective_key_return retval = new DRLParser.date_effective_key_return();
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
            // src/main/resources/org/drools/lang/DRL.g:1464:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EFFECTIVE[$start, text] )
            // src/main/resources/org/drools/lang/DRL.g:1464:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "date_effective_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_date_effective_key5024); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_date_effective_key5028); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_date_effective_key5032); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1468:3: -> VK_DATE_EFFECTIVE[$start, text]
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
    // src/main/resources/org/drools/lang/DRL.g:1471:1: date_expires_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EXPIRES[$start, text] ;
    public final DRLParser.date_expires_key_return date_expires_key() throws RecognitionException {
        DRLParser.date_expires_key_return retval = new DRLParser.date_expires_key_return();
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
            // src/main/resources/org/drools/lang/DRL.g:1474:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EXPIRES[$start, text] )
            // src/main/resources/org/drools/lang/DRL.g:1474:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "date_expires_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EXPIRES))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_date_expires_key5064); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_date_expires_key5068); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_date_expires_key5072); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1478:3: -> VK_DATE_EXPIRES[$start, text]
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
    // src/main/resources/org/drools/lang/DRL.g:1481:1: no_loop_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_NO_LOOP[$start, text] ;
    public final DRLParser.no_loop_key_return no_loop_key() throws RecognitionException {
        DRLParser.no_loop_key_return retval = new DRLParser.no_loop_key_return();
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
            // src/main/resources/org/drools/lang/DRL.g:1484:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_NO_LOOP[$start, text] )
            // src/main/resources/org/drools/lang/DRL.g:1484:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "no_loop_key", "(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.LOOP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_no_loop_key5104); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_no_loop_key5108); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_no_loop_key5112); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1488:3: -> VK_NO_LOOP[$start, text]
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
    // src/main/resources/org/drools/lang/DRL.g:1491:1: auto_focus_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_AUTO_FOCUS[$start, text] ;
    public final DRLParser.auto_focus_key_return auto_focus_key() throws RecognitionException {
        DRLParser.auto_focus_key_return retval = new DRLParser.auto_focus_key_return();
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
            // src/main/resources/org/drools/lang/DRL.g:1494:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_AUTO_FOCUS[$start, text] )
            // src/main/resources/org/drools/lang/DRL.g:1494:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "auto_focus_key", "(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.FOCUS))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_auto_focus_key5144); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_auto_focus_key5148); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_auto_focus_key5152); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1498:3: -> VK_AUTO_FOCUS[$start, text]
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
    // src/main/resources/org/drools/lang/DRL.g:1501:1: activation_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_ACTIVATION_GROUP[$start, text] ;
    public final DRLParser.activation_group_key_return activation_group_key() throws RecognitionException {
        DRLParser.activation_group_key_return retval = new DRLParser.activation_group_key_return();
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
            // src/main/resources/org/drools/lang/DRL.g:1504:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_ACTIVATION_GROUP[$start, text] )
            // src/main/resources/org/drools/lang/DRL.g:1504:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "activation_group_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_activation_group_key5184); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_activation_group_key5188); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_activation_group_key5192); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1508:3: -> VK_ACTIVATION_GROUP[$start, text]
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
    // src/main/resources/org/drools/lang/DRL.g:1511:1: agenda_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_AGENDA_GROUP[$start, text] ;
    public final DRLParser.agenda_group_key_return agenda_group_key() throws RecognitionException {
        DRLParser.agenda_group_key_return retval = new DRLParser.agenda_group_key_return();
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
            // src/main/resources/org/drools/lang/DRL.g:1514:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_AGENDA_GROUP[$start, text] )
            // src/main/resources/org/drools/lang/DRL.g:1514:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "agenda_group_key", "(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_agenda_group_key5224); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_agenda_group_key5228); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_agenda_group_key5232); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1518:3: -> VK_AGENDA_GROUP[$start, text]
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
    // src/main/resources/org/drools/lang/DRL.g:1521:1: ruleflow_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_RULEFLOW_GROUP[$start, text] ;
    public final DRLParser.ruleflow_group_key_return ruleflow_group_key() throws RecognitionException {
        DRLParser.ruleflow_group_key_return retval = new DRLParser.ruleflow_group_key_return();
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
            // src/main/resources/org/drools/lang/DRL.g:1524:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_RULEFLOW_GROUP[$start, text] )
            // src/main/resources/org/drools/lang/DRL.g:1524:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "ruleflow_group_key", "(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_ruleflow_group_key5264); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_ruleflow_group_key5268); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_ruleflow_group_key5272); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1528:3: -> VK_RULEFLOW_GROUP[$start, text]
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
    // src/main/resources/org/drools/lang/DRL.g:1531:1: entry_point_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_ENTRY_POINT[$start, text] ;
    public final DRLParser.entry_point_key_return entry_point_key() throws RecognitionException {
        DRLParser.entry_point_key_return retval = new DRLParser.entry_point_key_return();
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
            // src/main/resources/org/drools/lang/DRL.g:1534:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_ENTRY_POINT[$start, text] )
            // src/main/resources/org/drools/lang/DRL.g:1534:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "entry_point_key", "(validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.POINT))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_entry_point_key5304); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_entry_point_key5308); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_entry_point_key5312); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1538:3: -> VK_ENTRY_POINT[$start, text]
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
    // src/main/resources/org/drools/lang/DRL.g:1541:1: timer_key : {...}? =>id= ID -> VK_TIMER[$id] ;
    public final DRLParser.timer_key_return timer_key() throws RecognitionException {
        DRLParser.timer_key_return retval = new DRLParser.timer_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1542:2: ({...}? =>id= ID -> VK_TIMER[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1542:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.TIMER)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "timer_key", "(validateIdentifierKey(DroolsSoftKeywords.TIMER))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_timer_key5341); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1544:3: -> VK_TIMER[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1548:1: duration_key : {...}? =>id= ID -> VK_TIMER[$id] ;
    public final DRLParser.duration_key_return duration_key() throws RecognitionException {
        DRLParser.duration_key_return retval = new DRLParser.duration_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1549:2: ({...}? =>id= ID -> VK_TIMER[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1549:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DURATION)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "duration_key", "(validateIdentifierKey(DroolsSoftKeywords.DURATION))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_duration_key5369); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1551:3: -> VK_TIMER[$id]
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

    public static class package_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "package_key"
    // src/main/resources/org/drools/lang/DRL.g:1554:1: package_key : {...}? =>id= ID -> VK_PACKAGE[$id] ;
    public final DRLParser.package_key_return package_key() throws RecognitionException {
        DRLParser.package_key_return retval = new DRLParser.package_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1555:2: ({...}? =>id= ID -> VK_PACKAGE[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1555:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.PACKAGE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "package_key", "(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_package_key5396); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1557:3: -> VK_PACKAGE[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1560:1: import_key : {...}? =>id= ID -> VK_IMPORT[$id] ;
    public final DRLParser.import_key_return import_key() throws RecognitionException {
        DRLParser.import_key_return retval = new DRLParser.import_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1561:2: ({...}? =>id= ID -> VK_IMPORT[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1561:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "import_key", "(validateIdentifierKey(DroolsSoftKeywords.IMPORT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_import_key5423); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1563:3: -> VK_IMPORT[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1566:1: dialect_key : {...}? =>id= ID -> VK_DIALECT[$id] ;
    public final DRLParser.dialect_key_return dialect_key() throws RecognitionException {
        DRLParser.dialect_key_return retval = new DRLParser.dialect_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1567:2: ({...}? =>id= ID -> VK_DIALECT[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1567:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "dialect_key", "(validateIdentifierKey(DroolsSoftKeywords.DIALECT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_dialect_key5450); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1569:3: -> VK_DIALECT[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1572:1: salience_key : {...}? =>id= ID -> VK_SALIENCE[$id] ;
    public final DRLParser.salience_key_return salience_key() throws RecognitionException {
        DRLParser.salience_key_return retval = new DRLParser.salience_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1573:2: ({...}? =>id= ID -> VK_SALIENCE[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1573:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "salience_key", "(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_salience_key5477); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1575:3: -> VK_SALIENCE[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1578:1: enabled_key : {...}? =>id= ID -> VK_ENABLED[$id] ;
    public final DRLParser.enabled_key_return enabled_key() throws RecognitionException {
        DRLParser.enabled_key_return retval = new DRLParser.enabled_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1579:2: ({...}? =>id= ID -> VK_ENABLED[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1579:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "enabled_key", "(validateIdentifierKey(DroolsSoftKeywords.ENABLED))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_enabled_key5504); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1581:3: -> VK_ENABLED[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1584:1: attributes_key : {...}? =>id= ID -> VK_ATTRIBUTES[$id] ;
    public final DRLParser.attributes_key_return attributes_key() throws RecognitionException {
        DRLParser.attributes_key_return retval = new DRLParser.attributes_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1585:2: ({...}? =>id= ID -> VK_ATTRIBUTES[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1585:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "attributes_key", "(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_attributes_key5531); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1587:3: -> VK_ATTRIBUTES[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1590:1: rule_key : {...}? =>id= ID -> VK_RULE[$id] ;
    public final DRLParser.rule_key_return rule_key() throws RecognitionException {
        DRLParser.rule_key_return retval = new DRLParser.rule_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1591:2: ({...}? =>id= ID -> VK_RULE[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1591:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.RULE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "rule_key", "(validateIdentifierKey(DroolsSoftKeywords.RULE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_rule_key5558); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1593:3: -> VK_RULE[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1596:1: extend_key : {...}? =>id= ID -> VK_EXTEND[$id] ;
    public final DRLParser.extend_key_return extend_key() throws RecognitionException {
        DRLParser.extend_key_return retval = new DRLParser.extend_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1597:2: ({...}? =>id= ID -> VK_EXTEND[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1597:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "extend_key", "(validateIdentifierKey(DroolsSoftKeywords.EXTEND))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_extend_key5585); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1599:3: -> VK_EXTEND[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1602:1: template_key : {...}? =>id= ID -> VK_TEMPLATE[$id] ;
    public final DRLParser.template_key_return template_key() throws RecognitionException {
        DRLParser.template_key_return retval = new DRLParser.template_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1603:2: ({...}? =>id= ID -> VK_TEMPLATE[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1603:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "template_key", "(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_template_key5612); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1605:3: -> VK_TEMPLATE[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1608:1: query_key : {...}? =>id= ID -> VK_QUERY[$id] ;
    public final DRLParser.query_key_return query_key() throws RecognitionException {
        DRLParser.query_key_return retval = new DRLParser.query_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1609:2: ({...}? =>id= ID -> VK_QUERY[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1609:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "query_key", "(validateIdentifierKey(DroolsSoftKeywords.QUERY))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_query_key5639); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1611:3: -> VK_QUERY[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1614:1: declare_key : {...}? =>id= ID -> VK_DECLARE[$id] ;
    public final DRLParser.declare_key_return declare_key() throws RecognitionException {
        DRLParser.declare_key_return retval = new DRLParser.declare_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1615:2: ({...}? =>id= ID -> VK_DECLARE[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1615:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "declare_key", "(validateIdentifierKey(DroolsSoftKeywords.DECLARE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_declare_key5666); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1617:3: -> VK_DECLARE[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1620:1: function_key : {...}? =>id= ID -> VK_FUNCTION[$id] ;
    public final DRLParser.function_key_return function_key() throws RecognitionException {
        DRLParser.function_key_return retval = new DRLParser.function_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1621:2: ({...}? =>id= ID -> VK_FUNCTION[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1621:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "function_key", "(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_function_key5693); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1623:3: -> VK_FUNCTION[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1626:1: global_key : {...}? =>id= ID -> VK_GLOBAL[$id] ;
    public final DRLParser.global_key_return global_key() throws RecognitionException {
        DRLParser.global_key_return retval = new DRLParser.global_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1627:2: ({...}? =>id= ID -> VK_GLOBAL[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1627:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "global_key", "(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_global_key5720); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1629:3: -> VK_GLOBAL[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1632:1: eval_key : {...}? =>id= ID -> VK_EVAL[$id] ;
    public final DRLParser.eval_key_return eval_key() throws RecognitionException {
        DRLParser.eval_key_return retval = new DRLParser.eval_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1633:2: ({...}? =>id= ID -> VK_EVAL[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1633:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.EVAL)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "eval_key", "(validateIdentifierKey(DroolsSoftKeywords.EVAL))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_eval_key5747); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1635:3: -> VK_EVAL[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1638:1: not_key : {...}? =>id= ID -> VK_NOT[$id] ;
    public final DRLParser.not_key_return not_key() throws RecognitionException {
        DRLParser.not_key_return retval = new DRLParser.not_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1639:2: ({...}? =>id= ID -> VK_NOT[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1639:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "not_key", "(validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_not_key5774); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1641:3: -> VK_NOT[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1644:1: in_key : {...}? =>id= ID -> VK_IN[$id] ;
    public final DRLParser.in_key_return in_key() throws RecognitionException {
        DRLParser.in_key_return retval = new DRLParser.in_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1645:2: ({...}? =>id= ID -> VK_IN[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1645:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "in_key", "(validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_in_key5801); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1647:3: -> VK_IN[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1650:1: or_key : {...}? =>id= ID -> VK_OR[$id] ;
    public final DRLParser.or_key_return or_key() throws RecognitionException {
        DRLParser.or_key_return retval = new DRLParser.or_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1651:2: ({...}? =>id= ID -> VK_OR[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1651:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.OR)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "or_key", "(validateIdentifierKey(DroolsSoftKeywords.OR))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_or_key5828); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1653:3: -> VK_OR[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1656:1: and_key : {...}? =>id= ID -> VK_AND[$id] ;
    public final DRLParser.and_key_return and_key() throws RecognitionException {
        DRLParser.and_key_return retval = new DRLParser.and_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1657:2: ({...}? =>id= ID -> VK_AND[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1657:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.AND)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "and_key", "(validateIdentifierKey(DroolsSoftKeywords.AND))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_and_key5855); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1659:3: -> VK_AND[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1662:1: exists_key : {...}? =>id= ID -> VK_EXISTS[$id] ;
    public final DRLParser.exists_key_return exists_key() throws RecognitionException {
        DRLParser.exists_key_return retval = new DRLParser.exists_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1663:2: ({...}? =>id= ID -> VK_EXISTS[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1663:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.EXISTS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "exists_key", "(validateIdentifierKey(DroolsSoftKeywords.EXISTS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_exists_key5882); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1665:3: -> VK_EXISTS[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1668:1: forall_key : {...}? =>id= ID -> VK_FORALL[$id] ;
    public final DRLParser.forall_key_return forall_key() throws RecognitionException {
        DRLParser.forall_key_return retval = new DRLParser.forall_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1669:2: ({...}? =>id= ID -> VK_FORALL[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1669:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.FORALL)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "forall_key", "(validateIdentifierKey(DroolsSoftKeywords.FORALL))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_forall_key5909); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1671:3: -> VK_FORALL[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1674:1: action_key : {...}? =>id= ID -> VK_ACTION[$id] ;
    public final DRLParser.action_key_return action_key() throws RecognitionException {
        DRLParser.action_key_return retval = new DRLParser.action_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1675:2: ({...}? =>id= ID -> VK_ACTION[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1675:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ACTION)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "action_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTION))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_action_key5936); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1677:3: -> VK_ACTION[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1680:1: reverse_key : {...}? =>id= ID -> VK_REVERSE[$id] ;
    public final DRLParser.reverse_key_return reverse_key() throws RecognitionException {
        DRLParser.reverse_key_return retval = new DRLParser.reverse_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1681:2: ({...}? =>id= ID -> VK_REVERSE[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1681:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.REVERSE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "reverse_key", "(validateIdentifierKey(DroolsSoftKeywords.REVERSE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_reverse_key5963); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1683:3: -> VK_REVERSE[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1686:1: result_key : {...}? =>id= ID -> VK_RESULT[$id] ;
    public final DRLParser.result_key_return result_key() throws RecognitionException {
        DRLParser.result_key_return retval = new DRLParser.result_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1687:2: ({...}? =>id= ID -> VK_RESULT[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1687:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.RESULT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "result_key", "(validateIdentifierKey(DroolsSoftKeywords.RESULT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_result_key5990); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1689:3: -> VK_RESULT[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1692:1: end_key : {...}? =>id= ID -> VK_END[$id] ;
    public final DRLParser.end_key_return end_key() throws RecognitionException {
        DRLParser.end_key_return retval = new DRLParser.end_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1693:2: ({...}? =>id= ID -> VK_END[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1693:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.END)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "end_key", "(validateIdentifierKey(DroolsSoftKeywords.END))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_end_key6017); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1695:3: -> VK_END[$id]
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
    // src/main/resources/org/drools/lang/DRL.g:1698:1: not_end_key : {...}? =>any= . ;
    public final DRLParser.not_end_key_return not_end_key() throws RecognitionException {
        DRLParser.not_end_key_return retval = new DRLParser.not_end_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token any=null;

        Object any_tree=null;

        try {
            // src/main/resources/org/drools/lang/DRL.g:1699:2: ({...}? =>any= . )
            // src/main/resources/org/drools/lang/DRL.g:1699:4: {...}? =>any= .
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
    // src/main/resources/org/drools/lang/DRL.g:1703:1: init_key : {...}? =>id= ID -> VK_INIT[$id] ;
    public final DRLParser.init_key_return init_key() throws RecognitionException {
        DRLParser.init_key_return retval = new DRLParser.init_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // src/main/resources/org/drools/lang/DRL.g:1704:2: ({...}? =>id= ID -> VK_INIT[$id] )
            // src/main/resources/org/drools/lang/DRL.g:1704:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.INIT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "init_key", "(validateIdentifierKey(DroolsSoftKeywords.INIT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_init_key6064); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1706:3: -> VK_INIT[$id]
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

    // $ANTLR start synpred1_DRL
    public final void synpred1_DRL_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL.g:811:5: ( LEFT_PAREN or_key )
        // src/main/resources/org/drools/lang/DRL.g:811:6: LEFT_PAREN or_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred1_DRL2072); if (state.failed) return ;
        pushFollow(FOLLOW_or_key_in_synpred1_DRL2074);
        or_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_DRL

    // $ANTLR start synpred2_DRL
    public final void synpred2_DRL_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL.g:819:5: ( or_key | DOUBLE_PIPE )
        int alt108=2;
        int LA108_0 = input.LA(1);

        if ( (LA108_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            alt108=1;
        }
        else if ( (LA108_0==DOUBLE_PIPE) ) {
            alt108=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 108, 0, input);

            throw nvae;
        }
        switch (alt108) {
            case 1 :
                // src/main/resources/org/drools/lang/DRL.g:819:6: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred2_DRL2141);
                or_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRL.g:819:13: DOUBLE_PIPE
                {
                match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred2_DRL2143); if (state.failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred2_DRL

    // $ANTLR start synpred3_DRL
    public final void synpred3_DRL_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL.g:828:5: ( LEFT_PAREN and_key )
        // src/main/resources/org/drools/lang/DRL.g:828:6: LEFT_PAREN and_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred3_DRL2200); if (state.failed) return ;
        pushFollow(FOLLOW_and_key_in_synpred3_DRL2202);
        and_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_DRL

    // $ANTLR start synpred4_DRL
    public final void synpred4_DRL_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL.g:836:5: ( and_key | DOUBLE_AMPER )
        int alt109=2;
        int LA109_0 = input.LA(1);

        if ( (LA109_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))))) {
            alt109=1;
        }
        else if ( (LA109_0==DOUBLE_AMPER) ) {
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
                // src/main/resources/org/drools/lang/DRL.g:836:6: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred4_DRL2270);
                and_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRL.g:836:14: DOUBLE_AMPER
                {
                match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred4_DRL2272); if (state.failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred4_DRL

    // $ANTLR start synpred5_DRL
    public final void synpred5_DRL_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL.g:853:4: ( SEMICOLON )
        // src/main/resources/org/drools/lang/DRL.g:853:5: SEMICOLON
        {
        match(input,SEMICOLON,FOLLOW_SEMICOLON_in_synpred5_DRL2395); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_DRL

    // $ANTLR start synpred6_DRL
    public final void synpred6_DRL_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL.g:859:12: ( LEFT_PAREN ( or_key | and_key ) )
        // src/main/resources/org/drools/lang/DRL.g:859:13: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred6_DRL2432); if (state.failed) return ;
        // src/main/resources/org/drools/lang/DRL.g:859:24: ( or_key | and_key )
        int alt110=2;
        int LA110_0 = input.LA(1);

        if ( (LA110_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.AND)))||((validateIdentifierKey(DroolsSoftKeywords.OR)))))) {
            int LA110_1 = input.LA(2);

            if ( (((validateIdentifierKey(DroolsSoftKeywords.OR)))) ) {
                alt110=1;
            }
            else if ( (((validateIdentifierKey(DroolsSoftKeywords.AND)))) ) {
                alt110=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 110, 1, input);

                throw nvae;
            }
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 110, 0, input);

            throw nvae;
        }
        switch (alt110) {
            case 1 :
                // src/main/resources/org/drools/lang/DRL.g:859:25: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred6_DRL2435);
                or_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRL.g:859:32: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred6_DRL2437);
                and_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred6_DRL

    // $ANTLR start synpred7_DRL
    public final void synpred7_DRL_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL.g:875:5: ( LEFT_PAREN ( or_key | and_key ) )
        // src/main/resources/org/drools/lang/DRL.g:875:6: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred7_DRL2560); if (state.failed) return ;
        // src/main/resources/org/drools/lang/DRL.g:875:17: ( or_key | and_key )
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
                // src/main/resources/org/drools/lang/DRL.g:875:18: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred7_DRL2563);
                or_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRL.g:875:25: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred7_DRL2565);
                and_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred7_DRL

    // $ANTLR start synpred8_DRL
    public final void synpred8_DRL_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL.g:1052:5: ( LEFT_PAREN )
        // src/main/resources/org/drools/lang/DRL.g:1052:6: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred8_DRL3393); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_DRL

    // Delegated rules

    public final boolean synpred3_DRL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_DRL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_DRL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_DRL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_DRL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_DRL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_DRL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_DRL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_DRL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_DRL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_DRL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_DRL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_DRL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_DRL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_DRL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_DRL_fragment(); // can never throw exception
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
    protected DFA72 dfa72 = new DFA72(this);
    protected DFA74 dfa74 = new DFA74(this);
    protected DFA84 dfa84 = new DFA84(this);
    protected DFA89 dfa89 = new DFA89(this);
    static final String DFA1_eotS =
        "\12\uffff";
    static final String DFA1_eofS =
        "\1\2\11\uffff";
    static final String DFA1_minS =
        "\2\122\4\uffff\1\0\3\uffff";
    static final String DFA1_maxS =
        "\1\122\1\163\4\uffff\1\0\3\uffff";
    static final String DFA1_acceptS =
        "\2\uffff\4\2\1\uffff\2\2\1\1";
    static final String DFA1_specialS =
        "\1\uffff\1\0\4\uffff\1\1\3\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\1",
            "\1\6\2\uffff\1\7\1\5\6\uffff\1\10\1\4\24\uffff\1\3",
            "",
            "",
            "",
            "",
            "\1\uffff",
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
            return "396:4: ( package_statement )?";
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
                        if ( (LA1_1==MISC) && ((((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))))) {s = 3;}

                        else if ( (LA1_1==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))))) {s = 4;}

                        else if ( (LA1_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))))) {s = 5;}

                        else if ( (LA1_1==ID) ) {s = 6;}

                        else if ( (LA1_1==STRING) ) {s = 7;}

                        else if ( (LA1_1==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.ENABLED))))) {s = 8;}

                         
                        input.seek(index1_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA1_6 = input.LA(1);

                         
                        int index1_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.PACKAGE)))) ) {s = 9;}

                        else if ( (true) ) {s = 8;}

                         
                        input.seek(index1_6);
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
        "\2\122\1\uffff\1\0\2\uffff\1\0\11\uffff";
    static final String DFA5_maxS =
        "\1\122\1\163\1\uffff\1\0\2\uffff\1\0\11\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\1\1\1\uffff\2\1\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1"+
        "\10\1\11";
    static final String DFA5_specialS =
        "\1\uffff\1\0\1\uffff\1\1\2\uffff\1\2\11\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1",
            "\1\3\2\uffff\1\6\1\5\6\uffff\1\7\1\4\24\uffff\1\2",
            "",
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
            return "454:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );";
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
                        if ( (LA5_1==MISC) && ((((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))))) {s = 2;}

                        else if ( (LA5_1==ID) && (((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))))) {s = 3;}

                        else if ( (LA5_1==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))))) {s = 4;}

                        else if ( (LA5_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))))) {s = 5;}

                        else if ( (LA5_1==STRING) && ((!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))))) {s = 6;}

                        else if ( (LA5_1==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.ENABLED))))) {s = 7;}

                         
                        input.seek(index5_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA5_3 = input.LA(1);

                         
                        int index5_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) ) {s = 8;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) ) {s = 9;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))) ) {s = 10;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))) ) {s = 11;}

                        else if ( (((validateLT(1, DroolsSoftKeywords.TEMPLATE)))) ) {s = 12;}

                        else if ( ((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))) ) {s = 13;}

                        else if ( (!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))) ) {s = 14;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) ) {s = 15;}

                         
                        input.seek(index5_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA5_6 = input.LA(1);

                         
                        int index5_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) ) {s = 7;}

                        else if ( (((validateLT(1, DroolsSoftKeywords.TEMPLATE)))) ) {s = 12;}

                        else if ( (!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))) ) {s = 14;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) ) {s = 15;}

                         
                        input.seek(index5_6);
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
        "\2\122\1\uffff\1\122\1\uffff\2\122\2\157\2\122\1\126\1\157\1\122";
    static final String DFA12_maxS =
        "\1\126\1\130\1\uffff\1\156\1\uffff\1\156\1\122\2\157\3\156\1\157"+
        "\1\156";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\11\uffff";
    static final String DFA12_specialS =
        "\16\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\2\3\uffff\1\1",
            "\1\3\3\uffff\1\2\1\uffff\1\4",
            "",
            "\1\5\1\6\2\uffff\1\2\2\4\1\uffff\1\2\23\uffff\1\7",
            "",
            "\2\2\2\uffff\1\2\2\4\1\uffff\1\2\23\uffff\1\10",
            "\1\11",
            "\1\12",
            "\1\13",
            "\1\4\1\6\2\uffff\1\2\27\uffff\1\14",
            "\1\4\3\uffff\1\2\2\4\25\uffff\1\7",
            "\1\2\2\4\25\uffff\1\10",
            "\1\15",
            "\1\4\3\uffff\1\2\27\uffff\1\14"
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
            return "525:3: ( parameters )?";
        }
    }
    static final String DFA17_eotS =
        "\6\uffff";
    static final String DFA17_eofS =
        "\6\uffff";
    static final String DFA17_minS =
        "\2\122\1\uffff\1\157\1\uffff\1\122";
    static final String DFA17_maxS =
        "\1\122\1\156\1\uffff\1\157\1\uffff\1\156";
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
            return "550:4: ( data_type )?";
        }
    }
    static final String DFA29_eotS =
        "\12\uffff";
    static final String DFA29_eofS =
        "\12\uffff";
    static final String DFA29_minS =
        "\2\122\2\uffff\1\0\5\uffff";
    static final String DFA29_maxS =
        "\1\160\1\163\2\uffff\1\0\5\uffff";
    static final String DFA29_acceptS =
        "\2\uffff\2\2\1\uffff\4\2\1\1";
    static final String DFA29_specialS =
        "\1\2\1\1\2\uffff\1\0\5\uffff}>";
    static final String[] DFA29_transitionS = {
            "\1\1\6\uffff\1\2\2\uffff\1\2\23\uffff\1\2",
            "\1\11\2\uffff\1\4\1\7\3\uffff\1\5\2\uffff\1\10\1\6\24\uffff"+
            "\1\3",
            "",
            "",
            "\1\uffff",
            "",
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
            return "640:3: ( extend_key rule_id )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA29_4 = input.LA(1);

                         
                        int index29_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))) ) {s = 9;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) ) {s = 8;}

                         
                        input.seek(index29_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA29_1 = input.LA(1);

                         
                        int index29_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA29_1==MISC) && ((((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))))) {s = 3;}

                        else if ( (LA29_1==STRING) && ((((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))))) {s = 4;}

                        else if ( (LA29_1==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {s = 5;}

                        else if ( (LA29_1==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))))) {s = 6;}

                        else if ( (LA29_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))))) {s = 7;}

                        else if ( (LA29_1==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.ENABLED))))) {s = 8;}

                        else if ( (LA29_1==ID) && (((validateIdentifierKey(DroolsSoftKeywords.EXTEND))))) {s = 9;}

                         
                        input.seek(index29_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA29_0 = input.LA(1);

                         
                        int index29_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA29_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))))) {s = 1;}

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
        "\16\uffff";
    static final String DFA38_eofS =
        "\16\uffff";
    static final String DFA38_minS =
        "\1\122\1\0\14\uffff";
    static final String DFA38_maxS =
        "\1\122\1\0\14\uffff";
    static final String DFA38_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14";
    static final String DFA38_specialS =
        "\1\0\1\1\14\uffff}>";
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
            return "710:1: rule_attribute : ( salience | no_loop | agenda_group | timer | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );";
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
                        if ( (LA38_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))))) {s = 1;}

                         
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

                        else if ( ((((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.TIMER))))) ) {s = 5;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {s = 6;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))) ) {s = 7;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))) ) {s = 8;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))) ) {s = 9;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) ) {s = 10;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {s = 11;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) ) {s = 13;}

                         
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
    static final String DFA72_eotS =
        "\13\uffff";
    static final String DFA72_eofS =
        "\13\uffff";
    static final String DFA72_minS =
        "\1\121\1\0\11\uffff";
    static final String DFA72_maxS =
        "\1\160\1\0\11\uffff";
    static final String DFA72_acceptS =
        "\2\uffff\1\2\7\uffff\1\1";
    static final String DFA72_specialS =
        "\1\uffff\1\0\11\uffff}>";
    static final String[] DFA72_transitionS = {
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

    static final short[] DFA72_eot = DFA.unpackEncodedString(DFA72_eotS);
    static final short[] DFA72_eof = DFA.unpackEncodedString(DFA72_eofS);
    static final char[] DFA72_min = DFA.unpackEncodedStringToUnsignedChars(DFA72_minS);
    static final char[] DFA72_max = DFA.unpackEncodedStringToUnsignedChars(DFA72_maxS);
    static final short[] DFA72_accept = DFA.unpackEncodedString(DFA72_acceptS);
    static final short[] DFA72_special = DFA.unpackEncodedString(DFA72_specialS);
    static final short[][] DFA72_transition;

    static {
        int numStates = DFA72_transitionS.length;
        DFA72_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA72_transition[i] = DFA.unpackEncodedString(DFA72_transitionS[i]);
        }
    }

    class DFA72 extends DFA {

        public DFA72(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 72;
            this.eot = DFA72_eot;
            this.eof = DFA72_eof;
            this.min = DFA72_min;
            this.max = DFA72_max;
            this.accept = DFA72_accept;
            this.special = DFA72_special;
            this.transition = DFA72_transition;
        }
        public String getDescription() {
            return "1052:3: ( ( LEFT_PAREN )=>args= paren_chunk )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA72_1 = input.LA(1);

                         
                        int index72_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRL()) ) {s = 10;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index72_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 72, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA74_eotS =
        "\14\uffff";
    static final String DFA74_eofS =
        "\14\uffff";
    static final String DFA74_minS =
        "\1\121\1\0\12\uffff";
    static final String DFA74_maxS =
        "\1\160\1\0\12\uffff";
    static final String DFA74_acceptS =
        "\2\uffff\1\2\1\3\7\uffff\1\1";
    static final String DFA74_specialS =
        "\1\uffff\1\0\12\uffff}>";
    static final String[] DFA74_transitionS = {
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

    static final short[] DFA74_eot = DFA.unpackEncodedString(DFA74_eotS);
    static final short[] DFA74_eof = DFA.unpackEncodedString(DFA74_eofS);
    static final char[] DFA74_min = DFA.unpackEncodedStringToUnsignedChars(DFA74_minS);
    static final char[] DFA74_max = DFA.unpackEncodedStringToUnsignedChars(DFA74_maxS);
    static final short[] DFA74_accept = DFA.unpackEncodedString(DFA74_acceptS);
    static final short[] DFA74_special = DFA.unpackEncodedString(DFA74_specialS);
    static final short[][] DFA74_transition;

    static {
        int numStates = DFA74_transitionS.length;
        DFA74_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA74_transition[i] = DFA.unpackEncodedString(DFA74_transitionS[i]);
        }
    }

    class DFA74 extends DFA {

        public DFA74(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 74;
            this.eot = DFA74_eot;
            this.eof = DFA74_eof;
            this.min = DFA74_min;
            this.max = DFA74_max;
            this.accept = DFA74_accept;
            this.special = DFA74_special;
            this.transition = DFA74_transition;
        }
        public String getDescription() {
            return "1066:4: ({...}? paren_chunk | square_chunk )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA74_1 = input.LA(1);

                         
                        int index74_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((input.LA(1) == LEFT_PAREN)) ) {s = 11;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index74_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 74, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA84_eotS =
        "\17\uffff";
    static final String DFA84_eofS =
        "\17\uffff";
    static final String DFA84_minS =
        "\2\122\13\uffff\1\0\1\uffff";
    static final String DFA84_maxS =
        "\1\126\1\156\13\uffff\1\0\1\uffff";
    static final String DFA84_acceptS =
        "\2\uffff\1\3\1\2\12\uffff\1\1";
    static final String DFA84_specialS =
        "\15\uffff\1\0\1\uffff}>";
    static final String[] DFA84_transitionS = {
            "\1\1\3\uffff\1\2",
            "\2\3\2\uffff\1\15\3\uffff\1\3\13\uffff\6\3\2\uffff\1\3",
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
            "\1\uffff",
            ""
    };

    static final short[] DFA84_eot = DFA.unpackEncodedString(DFA84_eotS);
    static final short[] DFA84_eof = DFA.unpackEncodedString(DFA84_eofS);
    static final char[] DFA84_min = DFA.unpackEncodedStringToUnsignedChars(DFA84_minS);
    static final char[] DFA84_max = DFA.unpackEncodedStringToUnsignedChars(DFA84_maxS);
    static final short[] DFA84_accept = DFA.unpackEncodedString(DFA84_acceptS);
    static final short[] DFA84_special = DFA.unpackEncodedString(DFA84_specialS);
    static final short[][] DFA84_transition;

    static {
        int numStates = DFA84_transitionS.length;
        DFA84_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA84_transition[i] = DFA.unpackEncodedString(DFA84_transitionS[i]);
        }
    }

    class DFA84 extends DFA {

        public DFA84(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 84;
            this.eot = DFA84_eot;
            this.eof = DFA84_eof;
            this.min = DFA84_min;
            this.max = DFA84_max;
            this.accept = DFA84_accept;
            this.special = DFA84_special;
            this.transition = DFA84_transition;
        }
        public String getDescription() {
            return "1139:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA84_13 = input.LA(1);

                         
                        int index84_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.EVAL)))) ) {s = 14;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index84_13);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 84, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA89_eotS =
        "\50\uffff";
    static final String DFA89_eofS =
        "\50\uffff";
    static final String DFA89_minS =
        "\2\122\10\uffff\1\122\5\uffff\1\4\1\0\14\uffff\7\0\3\uffff";
    static final String DFA89_maxS =
        "\1\153\1\156\10\uffff\1\156\5\uffff\1\176\1\0\14\uffff\7\0\3\uffff";
    static final String DFA89_acceptS =
        "\2\uffff\1\2\5\uffff\1\3\1\2\10\uffff\2\2\5\uffff\1\2\15\uffff"+
        "\1\1";
    static final String DFA89_specialS =
        "\1\0\1\1\10\uffff\1\2\5\uffff\1\3\1\4\14\uffff\1\5\1\6\1\7\1\10"+
        "\1\11\1\12\1\13\3\uffff}>";
    static final String[] DFA89_transitionS = {
            "\1\1\3\uffff\1\10\17\uffff\6\2",
            "\1\12\2\uffff\1\11\1\20\6\uffff\2\11\15\uffff\3\11",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\23\1\31\1\uffff\1\23\1\21\2\31\4\uffff\2\23\2\31\13\uffff"+
            "\2\23\1\22",
            "",
            "",
            "",
            "",
            "",
            "\116\31\1\36\2\31\1\37\1\44\6\31\1\42\1\40\15\31\1\41\1\43"+
            "\21\31",
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
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA89_eot = DFA.unpackEncodedString(DFA89_eotS);
    static final short[] DFA89_eof = DFA.unpackEncodedString(DFA89_eofS);
    static final char[] DFA89_min = DFA.unpackEncodedStringToUnsignedChars(DFA89_minS);
    static final char[] DFA89_max = DFA.unpackEncodedStringToUnsignedChars(DFA89_maxS);
    static final short[] DFA89_accept = DFA.unpackEncodedString(DFA89_acceptS);
    static final short[] DFA89_special = DFA.unpackEncodedString(DFA89_specialS);
    static final short[][] DFA89_transition;

    static {
        int numStates = DFA89_transitionS.length;
        DFA89_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA89_transition[i] = DFA.unpackEncodedString(DFA89_transitionS[i]);
        }
    }

    class DFA89 extends DFA {

        public DFA89(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 89;
            this.eot = DFA89_eot;
            this.eof = DFA89_eof;
            this.min = DFA89_min;
            this.max = DFA89_max;
            this.accept = DFA89_accept;
            this.special = DFA89_special;
            this.transition = DFA89_transition;
        }
        public String getDescription() {
            return "1203:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA89_0 = input.LA(1);

                         
                        int index89_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA89_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {s = 1;}

                        else if ( ((LA89_0>=EQUAL && LA89_0<=NOT_EQUAL)) ) {s = 2;}

                        else if ( (LA89_0==LEFT_PAREN) ) {s = 8;}

                         
                        input.seek(index89_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA89_1 = input.LA(1);

                         
                        int index89_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA89_1==STRING||(LA89_1>=BOOL && LA89_1<=INT)||(LA89_1>=FLOAT && LA89_1<=LEFT_SQUARE)) && (((isPluggableEvaluator(false))))) {s = 9;}

                        else if ( (LA89_1==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {s = 10;}

                        else if ( (LA89_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 16;}

                         
                        input.seek(index89_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA89_10 = input.LA(1);

                         
                        int index89_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA89_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))))) {s = 17;}

                        else if ( (LA89_10==LEFT_SQUARE) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {s = 18;}

                        else if ( (LA89_10==ID||LA89_10==STRING||(LA89_10>=BOOL && LA89_10<=INT)||(LA89_10>=FLOAT && LA89_10<=NULL)) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))))) {s = 19;}

                        else if ( (LA89_10==DOT||(LA89_10>=COMMA && LA89_10<=RIGHT_PAREN)||(LA89_10>=DOUBLE_PIPE && LA89_10<=DOUBLE_AMPER)) && (((isPluggableEvaluator(false))))) {s = 25;}

                         
                        input.seek(index89_10);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA89_16 = input.LA(1);

                         
                        int index89_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA89_16==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 30;}

                        else if ( (LA89_16==STRING) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 31;}

                        else if ( (LA89_16==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 32;}

                        else if ( (LA89_16==FLOAT) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 33;}

                        else if ( (LA89_16==BOOL) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 34;}

                        else if ( (LA89_16==NULL) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 35;}

                        else if ( (LA89_16==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 36;}

                        else if ( ((LA89_16>=VT_COMPILATION_UNIT && LA89_16<=SEMICOLON)||(LA89_16>=DOT && LA89_16<=DOT_STAR)||(LA89_16>=COMMA && LA89_16<=WHEN)||(LA89_16>=DOUBLE_PIPE && LA89_16<=NOT_EQUAL)||(LA89_16>=LEFT_SQUARE && LA89_16<=IdentifierPart)) && (((isPluggableEvaluator(false))))) {s = 25;}

                         
                        input.seek(index89_16);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA89_17 = input.LA(1);

                         
                        int index89_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {s = 39;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {s = 25;}

                         
                        input.seek(index89_17);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA89_30 = input.LA(1);

                         
                        int index89_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index89_30);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA89_31 = input.LA(1);

                         
                        int index89_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index89_31);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA89_32 = input.LA(1);

                         
                        int index89_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index89_32);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA89_33 = input.LA(1);

                         
                        int index89_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index89_33);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA89_34 = input.LA(1);

                         
                        int index89_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index89_34);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA89_35 = input.LA(1);

                         
                        int index89_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index89_35);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA89_36 = input.LA(1);

                         
                        int index89_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index89_36);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 89, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_package_statement_in_compilation_unit384 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_statement_in_compilation_unit389 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_EOF_in_compilation_unit394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_package_key_in_package_statement449 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_package_id_in_package_statement453 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_package_statement455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_id482 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_package_id488 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_package_id492 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_rule_attribute_in_statement530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_declaration_in_statement571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_import_statement603 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_import_name_in_import_statement605 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_import_statement608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_function_import_statement646 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_function_key_in_function_import_statement648 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement650 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_function_import_statement653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name687 = new BitSet(new long[]{0x0000000000000002L,0x0000000000180000L});
    public static final BitSet FOLLOW_DOT_in_import_name693 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_import_name697 = new BitSet(new long[]{0x0000000000000002L,0x0000000000180000L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_key_in_global744 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_data_type_in_global746 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_global_id_in_global748 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_global750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_id779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_key_in_function811 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_data_type_in_function813 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_function_id_in_function816 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_parameters_in_function818 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_id850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_key_in_query882 = new BitSet(new long[]{0x0000000000000000L,0x0000000000240000L});
    public static final BitSet FOLLOW_query_id_in_query884 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_parameters_in_query892 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query901 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_end_key_in_query908 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_query910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_id945 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_query_id961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parameters980 = new BitSet(new long[]{0x0000000000000000L,0x0000000001040000L});
    public static final BitSet FOLLOW_param_definition_in_parameters989 = new BitSet(new long[]{0x0000000000000000L,0x0000000001800000L});
    public static final BitSet FOLLOW_COMMA_in_parameters992 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_param_definition_in_parameters996 = new BitSet(new long[]{0x0000000000000000L,0x0000000001800000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parameters1005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_param_definition1031 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_argument_in_param_definition1034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument1045 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_dimension_definition_in_argument1051 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_declare_key_in_type_declaration1074 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_type_declare_id_in_type_declaration1077 = new BitSet(new long[]{0x0000000000000000L,0x0000000002440000L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration1081 = new BitSet(new long[]{0x0000000000000000L,0x0000000002440000L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration1086 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_end_key_in_type_declaration1091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_type_declare_id1123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_decl_metadata1142 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_decl_metadata1150 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_decl_metadata1157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_decl_field1182 = new BitSet(new long[]{0x0000000000000000L,0x000000000C000000L});
    public static final BitSet FOLLOW_decl_field_initialization_in_decl_field1188 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_decl_field1194 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_data_type_in_decl_field1200 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field1204 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_EQUALS_in_decl_field_initialization1232 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_decl_field_initialization1238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_key_in_template1275 = new BitSet(new long[]{0x0000000000000000L,0x0000000000240000L});
    public static final BitSet FOLLOW_template_id_in_template1277 = new BitSet(new long[]{0x0000000000000000L,0x0000000000060000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1284 = new BitSet(new long[]{0x0000000000000000L,0x0000000000060000L});
    public static final BitSet FOLLOW_template_slot_in_template1292 = new BitSet(new long[]{0x0000000000000000L,0x0000000000460000L});
    public static final BitSet FOLLOW_end_key_in_template1299 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_id1336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_template_id1352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_template_slot1372 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_slot_id_in_template_slot1374 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template_slot1376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_slot_id1405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_key_in_rule1442 = new BitSet(new long[]{0x0000000000000000L,0x0000000000240000L});
    public static final BitSet FOLLOW_rule_id_in_rule1444 = new BitSet(new long[]{0x0000000000000000L,0x0001000012040000L});
    public static final BitSet FOLLOW_extend_key_in_rule1453 = new BitSet(new long[]{0x0000000000000000L,0x0000000000240000L});
    public static final BitSet FOLLOW_rule_id_in_rule1455 = new BitSet(new long[]{0x0000000000000000L,0x0001000012040000L});
    public static final BitSet FOLLOW_decl_metadata_in_rule1459 = new BitSet(new long[]{0x0000000000000000L,0x0001000012040000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule1462 = new BitSet(new long[]{0x0000000000000000L,0x0001000012040000L});
    public static final BitSet FOLLOW_when_part_in_rule1465 = new BitSet(new long[]{0x0000000000000000L,0x0001000012040000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_when_part1512 = new BitSet(new long[]{0x0000000000000000L,0x0000000004440000L});
    public static final BitSet FOLLOW_COLON_in_when_part1518 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_when_part1528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_id1549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_rule_id1565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attributes_key_in_rule_attributes1586 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_rule_attributes1588 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1598 = new BitSet(new long[]{0x0000000000000002L,0x0000000000840000L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1602 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1609 = new BitSet(new long[]{0x0000000000000002L,0x0000000000840000L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_timer_in_rule_attribute1666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_key_in_date_effective1729 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_key_in_date_expires1748 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_key_in_enabled1768 = new BitSet(new long[]{0x0000000000000000L,0x0000000020400000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_enabled1792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_salience_key_in_salience1812 = new BitSet(new long[]{0x0000000000000000L,0x0000000040400000L});
    public static final BitSet FOLLOW_INT_in_salience1821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_key_in_no_loop1845 = new BitSet(new long[]{0x0000000000000002L,0x0000000020000000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_key_in_auto_focus1865 = new BitSet(new long[]{0x0000000000000002L,0x0000000020000000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_key_in_activation_group1887 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_key_in_ruleflow_group1906 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_key_in_agenda_group1925 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_key_in_timer1945 = new BitSet(new long[]{0x0000000000000000L,0x0000000040400000L});
    public static final BitSet FOLLOW_timer_key_in_timer1948 = new BitSet(new long[]{0x0000000000000000L,0x0000000040400000L});
    public static final BitSet FOLLOW_INT_in_timer1962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_timer1973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_key_in_dialect1993 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_dialect1998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_key_in_lock_on_active2016 = new BitSet(new long[]{0x0000000000000002L,0x0000000020000000L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active2021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block2036 = new BitSet(new long[]{0x0000000000000002L,0x0000000000440000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs2057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or2081 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or2091 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2099 = new BitSet(new long[]{0x0000000000000000L,0x0000000001440000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or2105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2128 = new BitSet(new long[]{0x0000000000000002L,0x0000000080040000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or2150 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_lhs_or2157 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2168 = new BitSet(new long[]{0x0000000000000002L,0x0000000080040000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and2209 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and2219 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2227 = new BitSet(new long[]{0x0000000000000000L,0x0000000001440000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and2233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2257 = new BitSet(new long[]{0x0000000000000002L,0x0000000100040000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and2279 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_lhs_and2286 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2297 = new BitSet(new long[]{0x0000000000000002L,0x0000000100040000L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2328 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_lhs_not_binding_in_lhs_unary2336 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2342 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2348 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary2354 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2360 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary2371 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2377 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_unary2385 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_lhs_unary2399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_key_in_lhs_exist2415 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist2449 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2457 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist2464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist2479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not_binding2525 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_not_binding2527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not2550 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2579 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2588 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_key_in_lhs_eval2643 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forall_key_in_lhs_forall2679 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2684 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_forall2692 = new BitSet(new long[]{0x0000000000000000L,0x0000000001440000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall2698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_pattern_source2734 = new BitSet(new long[]{0x0000000000000002L,0x0000000600000000L});
    public static final BitSet FOLLOW_over_clause_in_pattern_source2738 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_FROM_in_pattern_source2748 = new BitSet(new long[]{0x0000000000000000L,0x0000001800040000L});
    public static final BitSet FOLLOW_accumulate_statement_in_pattern_source2768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_pattern_source2784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entrypoint_statement_in_pattern_source2801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_pattern_source2817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause2849 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2854 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_COMMA_in_over_clause2861 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2866 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_over_elements2881 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_over_elements2888 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_over_elements2897 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_over_elements2904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement2930 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2939 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_lhs_or_in_accumulate_statement2947 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2952 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_statement2962 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_statement2968 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_init_key_in_accumulate_init_clause3022 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3032 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause3037 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_action_key_in_accumulate_init_clause3048 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3052 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause3057 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_reverse_key_in_accumulate_init_clause3069 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3073 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause3078 = new BitSet(new long[]{0x0000000000000000L,0x0000000000840000L});
    public static final BitSet FOLLOW_result_key_in_accumulate_init_clause3094 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk3158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_paren_chunk_data3182 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_accumulate_paren_chunk_data3194 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk_data3210 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_paren_chunk_data3221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause3237 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_id_clause3243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement3265 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement3274 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_pattern_source_in_collect_statement3281 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement3286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entry_point_key_in_entrypoint_statement3313 = new BitSet(new long[]{0x0000000000000000L,0x0000000000240000L});
    public static final BitSet FOLLOW_entrypoint_id_in_entrypoint_statement3321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entrypoint_id3347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_entrypoint_id3364 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source3384 = new BitSet(new long[]{0x0000000000000002L,0x0000000000480000L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source3399 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_expression_chain_in_from_source3406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain3439 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_expression_chain3446 = new BitSet(new long[]{0x0000000000000002L,0x0000400000480000L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain3462 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain3476 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain3487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern3520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern3533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_fact_binding3553 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_fact_in_fact_binding3559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding3566 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_fact_binding_expression_in_fact_binding3574 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding3582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression3623 = new BitSet(new long[]{0x0000000000000002L,0x0000000080040000L});
    public static final BitSet FOLLOW_or_key_in_fact_binding_expression3635 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3641 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression3646 = new BitSet(new long[]{0x0000000000000002L,0x0000000080040000L});
    public static final BitSet FOLLOW_pattern_type_in_fact3686 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact3691 = new BitSet(new long[]{0x0000000000000000L,0x0000000001440000L});
    public static final BitSet FOLLOW_constraints_in_fact3702 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact3708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints3742 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_COMMA_in_constraints3746 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_constraint_in_constraints3753 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_or_constr_in_constraint3767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3778 = new BitSet(new long[]{0x0000000000000002L,0x0000000080000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr3782 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3789 = new BitSet(new long[]{0x0000000000000002L,0x0000000080000000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3804 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr3808 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3815 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_eval_key_in_unary_constr3848 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_unary_constr3851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr3856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr3862 = new BitSet(new long[]{0x0000000000000000L,0x0000000000440000L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr3872 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr3877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_field_constraint3897 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3899 = new BitSet(new long[]{0x0000000000000002L,0x00000FE000440000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_field_constraint3912 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_field_constraint3916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3970 = new BitSet(new long[]{0x0000000000000000L,0x00000FC000440000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_label3997 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_label4004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective4025 = new BitSet(new long[]{0x0000000000000002L,0x0000000080000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective4031 = new BitSet(new long[]{0x0000000000000000L,0x00000FC000440000L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective4039 = new BitSet(new long[]{0x0000000000000002L,0x0000000080000000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective4060 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective4066 = new BitSet(new long[]{0x0000000000000000L,0x00000FC000440000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective4073 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression4101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression4106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression4111 = new BitSet(new long[]{0x0000000000000000L,0x00000FC000440000L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression4120 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression4125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUAL_in_simple_operator4160 = new BitSet(new long[]{0x0000000000000000L,0x0000300060640000L});
    public static final BitSet FOLLOW_GREATER_in_simple_operator4168 = new BitSet(new long[]{0x0000000000000000L,0x0000300060640000L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_simple_operator4176 = new BitSet(new long[]{0x0000000000000000L,0x0000300060640000L});
    public static final BitSet FOLLOW_LESS_in_simple_operator4184 = new BitSet(new long[]{0x0000000000000000L,0x0000300060640000L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_simple_operator4192 = new BitSet(new long[]{0x0000000000000000L,0x0000300060640000L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_simple_operator4200 = new BitSet(new long[]{0x0000000000000000L,0x0000300060640000L});
    public static final BitSet FOLLOW_not_key_in_simple_operator4208 = new BitSet(new long[]{0x0000000000000000L,0x00000FC000040000L});
    public static final BitSet FOLLOW_operator_key_in_simple_operator4215 = new BitSet(new long[]{0x0000000000000000L,0x0000700060640000L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator4218 = new BitSet(new long[]{0x0000000000000000L,0x0000300060640000L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator4230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_compound_operator4252 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_not_key_in_compound_operator4257 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_in_key_in_compound_operator4259 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator4270 = new BitSet(new long[]{0x0000000000000000L,0x0000300060640000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4278 = new BitSet(new long[]{0x0000000000000000L,0x0000000001800000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator4282 = new BitSet(new long[]{0x0000000000000000L,0x0000300060640000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4287 = new BitSet(new long[]{0x0000000000000000L,0x0000000001800000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator4295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_operator_key4326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_neg_operator_key4371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_expression_value4408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value4413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_value4419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint4438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint4445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint4452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint4459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint4466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pattern_type4481 = new BitSet(new long[]{0x0000000000000002L,0x0000400000080000L});
    public static final BitSet FOLLOW_DOT_in_pattern_type4487 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_pattern_type4491 = new BitSet(new long[]{0x0000000000000002L,0x0000400000080000L});
    public static final BitSet FOLLOW_dimension_definition_in_pattern_type4506 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_ID_in_data_type4534 = new BitSet(new long[]{0x0000000000000002L,0x0000400000080000L});
    public static final BitSet FOLLOW_DOT_in_data_type4540 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_data_type4544 = new BitSet(new long[]{0x0000000000000002L,0x0000400000080000L});
    public static final BitSet FOLLOW_dimension_definition_in_data_type4549 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dimension_definition4578 = new BitSet(new long[]{0x0000000000000000L,0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dimension_definition4585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4599 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_accessor_path4603 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4607 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_accessor_element4631 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_square_chunk_in_accessor_element4637 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_rhs_chunk_data_in_rhs_chunk4666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk_data4685 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_not_end_key_in_rhs_chunk_data4694 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_end_key_in_rhs_chunk_data4700 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_SEMICOLON_in_rhs_chunk_data4705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk4724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk_data4747 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk_data4759 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk_data4775 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk_data4786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk4807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk_data4831 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk_data4843 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk_data4859 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk_data4870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk4891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk_data4914 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk_data4926 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk_data4941 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x7FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk_data4952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4976 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key4980 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4984 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key4988 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_effective_key5024 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_effective_key5028 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_date_effective_key5032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_expires_key5064 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_expires_key5068 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_date_expires_key5072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_no_loop_key5104 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_no_loop_key5108 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_no_loop_key5112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key5144 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_auto_focus_key5148 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key5152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_activation_group_key5184 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_activation_group_key5188 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_activation_group_key5192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key5224 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_agenda_group_key5228 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key5232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key5264 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_ruleflow_group_key5268 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key5272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entry_point_key5304 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_entry_point_key5308 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_entry_point_key5312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_timer_key5341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_duration_key5369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_key5396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_key5423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dialect_key5450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_salience_key5477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enabled_key5504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_attributes_key5531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_key5558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_extend_key5585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_key5612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_key5639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_declare_key5666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_key5693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_key5720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_eval_key5747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key5774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key5801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_or_key5828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_and_key5855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_exists_key5882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_forall_key5909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_action_key5936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_reverse_key5963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_result_key5990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_end_key6017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_init_key6064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred1_DRL2072 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_or_key_in_synpred1_DRL2074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_key_in_synpred2_DRL2141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred2_DRL2143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred3_DRL2200 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_and_key_in_synpred3_DRL2202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred4_DRL2270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred4_DRL2272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_synpred5_DRL2395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred6_DRL2432 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_or_key_in_synpred6_DRL2435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred6_DRL2437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred7_DRL2560 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_or_key_in_synpred7_DRL2563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred7_DRL2565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred8_DRL3393 = new BitSet(new long[]{0x0000000000000002L});

}