// $ANTLR 3.1.1 /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2008-11-24 22:04:04

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TEMPLATE_ID", "VT_TYPE_DECLARE_ID", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_SLOT_ID", "VT_SLOT", "VT_RULE_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_DURATION", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_RULE", "VK_EXTEND", "VK_IMPORT", "VK_PACKAGE", "VK_TEMPLATE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "VK_OPERATOR", "SEMICOLON", "ID", "DOT", "DOT_STAR", "END", "STRING", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "AT", "COLON", "EQUALS", "WHEN", "BOOL", "INT", "DOUBLE_PIPE", "DOUBLE_AMPER", "FROM", "OVER", "ACCUMULATE", "INIT", "COLLECT", "ARROW", "EQUAL", "GREATER", "GREATER_EQUAL", "LESS", "LESS_EQUAL", "NOT_EQUAL", "FLOAT", "NULL", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "MISC", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "GRAVE_ACCENT", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT"
    };
    public static final int COMMA=86;
    public static final int VT_PATTERN_TYPE=39;
    public static final int VT_ACCUMULATE_ID_CLAUSE=28;
    public static final int VK_DIALECT=54;
    public static final int VK_FUNCTION=65;
    public static final int END=83;
    public static final int HexDigit=119;
    public static final int VK_ATTRIBUTES=57;
    public static final int VT_EXPRESSION_CHAIN=30;
    public static final int MISC=115;
    public static final int VT_AND_PREFIX=23;
    public static final int VK_QUERY=63;
    public static final int THEN=112;
    public static final int VK_AUTO_FOCUS=49;
    public static final int DOT=81;
    public static final int VK_IMPORT=60;
    public static final int VT_SLOT=15;
    public static final int VT_PACKAGE_ID=40;
    public static final int LEFT_SQUARE=110;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=123;
    public static final int VT_DATA_TYPE=38;
    public static final int VT_FACT=6;
    public static final int LEFT_CURLY=113;
    public static final int AT=88;
    public static final int DOUBLE_AMPER=95;
    public static final int LEFT_PAREN=85;
    public static final int VT_QUERY_ID=9;
    public static final int VT_ACCESSOR_PATH=36;
    public static final int VT_LABEL=8;
    public static final int WHEN=91;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int WS=117;
    public static final int VT_FIELD=35;
    public static final int VK_SALIENCE=55;
    public static final int OVER=97;
    public static final int VK_AND=72;
    public static final int STRING=84;
    public static final int VT_ACCESSOR_ELEMENT=37;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=27;
    public static final int VK_GLOBAL=66;
    public static final int VK_REVERSE=76;
    public static final int VT_BEHAVIOR=21;
    public static final int GRAVE_ACCENT=122;
    public static final int VK_DURATION=53;
    public static final int VT_SQUARE_CHUNK=19;
    public static final int VK_FORALL=74;
    public static final int VT_PAREN_CHUNK=20;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int COLLECT=100;
    public static final int VK_ENABLED=56;
    public static final int VK_RESULT=77;
    public static final int EQUALS=90;
    public static final int UnicodeEscape=120;
    public static final int VK_PACKAGE=61;
    public static final int VT_RULE_ID=12;
    public static final int EQUAL=102;
    public static final int VK_NO_LOOP=48;
    public static final int SEMICOLON=79;
    public static final int VK_TEMPLATE=62;
    public static final int VT_AND_IMPLICIT=22;
    public static final int NULL=109;
    public static final int COLON=89;
    public static final int MULTI_LINE_COMMENT=125;
    public static final int VT_RULE_ATTRIBUTES=16;
    public static final int RIGHT_SQUARE=111;
    public static final int VK_AGENDA_GROUP=51;
    public static final int VT_FACT_OR=33;
    public static final int VK_NOT=69;
    public static final int VK_DATE_EXPIRES=46;
    public static final int ARROW=101;
    public static final int FLOAT=108;
    public static final int INIT=99;
    public static final int VK_EXTEND=59;
    public static final int VT_SLOT_ID=14;
    public static final int VT_CURLY_CHUNK=18;
    public static final int VT_OR_PREFIX=24;
    public static final int DOUBLE_PIPE=94;
    public static final int LESS=105;
    public static final int VT_TYPE_DECLARE_ID=11;
    public static final int VT_PATTERN=31;
    public static final int VK_DATE_EFFECTIVE=45;
    public static final int EscapeSequence=118;
    public static final int VK_EXISTS=73;
    public static final int INT=93;
    public static final int VT_BIND_FIELD=34;
    public static final int VK_RULE=58;
    public static final int VK_EVAL=67;
    public static final int GREATER=103;
    public static final int VT_FACT_BINDING=32;
    public static final int FROM=96;
    public static final int ID=80;
    public static final int NOT_EQUAL=107;
    public static final int RIGHT_CURLY=114;
    public static final int VK_OPERATOR=78;
    public static final int BOOL=92;
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
    public static final int DOT_STAR=82;
    public static final int VK_OR=71;
    public static final int VT_GLOBAL_ID=42;
    public static final int LESS_EQUAL=106;
    public static final int ACCUMULATE=98;
    public static final int VK_RULEFLOW_GROUP=52;
    public static final int VT_FUNCTION_ID=43;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int EOL=116;
    public static final int VT_IMPORT_ID=41;
    public static final int VK_ACTIVATION_GROUP=50;
    public static final int OctalEscape=121;
    public static final int VK_ACTION=75;
    public static final int RIGHT_PAREN=87;
    public static final int VT_TEMPLATE_ID=10;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=124;
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
    public String getGrammarFileName() { return "/Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g"; }


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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:393:1: compilation_unit : ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:394:2: ( ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:394:4: ( package_statement )? ( statement )* EOF
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:394:4: ( package_statement )?
            int alt1=2;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:394:4: package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_compilation_unit376);
                    package_statement1=package_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_package_statement.add(package_statement1.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:395:3: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:395:3: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit381);
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

            EOF3=(Token)match(input,EOF,FOLLOW_EOF_in_compilation_unit386); if (state.failed) return retval; 
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
            // 397:3: -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:397:6: ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_COMPILATION_UNIT, "VT_COMPILATION_UNIT"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:397:28: ( package_statement )?
                if ( stream_package_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_package_statement.nextTree());

                }
                stream_package_statement.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:397:47: ( statement )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:436:1: package_statement : package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:439:2: ( package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:439:4: package_key package_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_package_key_in_package_statement441);
            package_key4=package_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_key.add(package_key4.getTree());
            pushFollow(FOLLOW_package_id_in_package_statement445);
            package_id5=package_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_id.add(package_id5.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:14: ( SEMICOLON )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==SEMICOLON) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:14: SEMICOLON
                    {
                    SEMICOLON6=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_package_statement447); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 442:3: -> ^( package_key package_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:442:6: ^( package_key package_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:445:1: package_id : id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:446:2: (id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:446:4: id+= ID (id+= DOT id+= ID )*
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_package_id474); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:446:11: (id+= DOT id+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:446:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_package_id480); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_package_id484); if (state.failed) return retval; 
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
            // 449:3: -> ^( VT_PACKAGE_ID ( ID )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:449:6: ^( VT_PACKAGE_ID ( ID )+ )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:455:3: ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query )
            int alt5=9;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:455:5: rule_attribute
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_attribute_in_statement522);
                    rule_attribute7=rule_attribute();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rule_attribute7.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:456:3: {...}? => function_import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !(((validateLT(1, "import") && validateLT(2, "function") ))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, \"import\") && validateLT(2, \"function\") )");
                    }
                    pushFollow(FOLLOW_function_import_statement_in_statement529);
                    function_import_statement8=function_import_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function_import_statement8.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:457:4: import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_import_statement_in_statement535);
                    import_statement9=import_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, import_statement9.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:458:4: global
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_global_in_statement541);
                    global10=global();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, global10.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:459:4: function
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_function_in_statement547);
                    function11=function();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function11.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:460:4: {...}? => template
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !(((validateLT(1, DroolsSoftKeywords.TEMPLATE)))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, DroolsSoftKeywords.TEMPLATE))");
                    }
                    pushFollow(FOLLOW_template_in_statement555);
                    template12=template();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, template12.getTree());

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:4: {...}? => type_declaration
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !(((validateLT(1, DroolsSoftKeywords.DECLARE)))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, DroolsSoftKeywords.DECLARE))");
                    }
                    pushFollow(FOLLOW_type_declaration_in_statement563);
                    type_declaration13=type_declaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type_declaration13.getTree());

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:462:4: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_statement568);
                    rule14=rule();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rule14.getTree());

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:463:4: query
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_query_in_statement573);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:466:1: import_statement : import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:2: ( import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:4: import_key import_name[DroolsParaphraseTypes.IMPORT] ( SEMICOLON )?
            {
            pushFollow(FOLLOW_import_key_in_import_statement595);
            import_key16=import_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_key.add(import_key16.getTree());
            pushFollow(FOLLOW_import_name_in_import_statement597);
            import_name17=import_name(DroolsParaphraseTypes.IMPORT);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_name.add(import_name17.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:57: ( SEMICOLON )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==SEMICOLON) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:57: SEMICOLON
                    {
                    SEMICOLON18=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_import_statement600); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 471:3: -> ^( import_key import_name )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:471:6: ^( import_key import_name )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:474:1: function_import_statement : imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:477:2: (imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:477:4: imp= import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] ( SEMICOLON )?
            {
            pushFollow(FOLLOW_import_key_in_function_import_statement638);
            imp=import_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_key.add(imp.getTree());
            pushFollow(FOLLOW_function_key_in_function_import_statement640);
            function_key19=function_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_function_key.add(function_key19.getTree());
            pushFollow(FOLLOW_import_name_in_function_import_statement642);
            import_name20=import_name(DroolsParaphraseTypes.FUNCTION_IMPORT);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_import_name.add(import_name20.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:477:83: ( SEMICOLON )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==SEMICOLON) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:477:83: SEMICOLON
                    {
                    SEMICOLON21=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_function_import_statement645); if (state.failed) return retval; 
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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 479:3: -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:479:6: ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:1: import_name[DroolsParaphraseTypes importType] : id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:483:2: (id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:483:4: id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )?
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_import_name679); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:483:11: (id+= DOT id+= ID )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==DOT) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:483:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_import_name685); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_import_name689); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:483:33: (id+= DOT_STAR )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==DOT_STAR) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:483:33: id+= DOT_STAR
                    {
                    id=(Token)match(input,DOT_STAR,FOLLOW_DOT_STAR_in_import_name696); if (state.failed) return retval; 
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
            // 486:3: -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:6: ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
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
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:25: ( DOT_STAR )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:489:1: global : global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:2: ( global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:4: global_key data_type global_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_global_key_in_global736);
            global_key22=global_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_global_key.add(global_key22.getTree());
            pushFollow(FOLLOW_data_type_in_global738);
            data_type23=data_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_data_type.add(data_type23.getTree());
            pushFollow(FOLLOW_global_id_in_global740);
            global_id24=global_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_global_id.add(global_id24.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:35: ( SEMICOLON )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==SEMICOLON) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:35: SEMICOLON
                    {
                    SEMICOLON25=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_global742); if (state.failed) return retval; 
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
            // 494:3: -> ^( global_key data_type global_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:6: ^( global_key data_type global_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:497:1: global_id : id= ID -> VT_GLOBAL_ID[$id] ;
    public final DRLParser.global_id_return global_id() throws RecognitionException {
        DRLParser.global_id_return retval = new DRLParser.global_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:498:2: (id= ID -> VT_GLOBAL_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:498:4: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_global_id771); if (state.failed) return retval; 
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
            // 501:3: -> VT_GLOBAL_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:504:1: function : function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:507:2: ( function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:507:4: function_key ( data_type )? function_id parameters curly_chunk
            {
            pushFollow(FOLLOW_function_key_in_function803);
            function_key26=function_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_function_key.add(function_key26.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:507:17: ( data_type )?
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:507:17: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function805);
                    data_type27=data_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_data_type.add(data_type27.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_function_id_in_function808);
            function_id28=function_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_function_id.add(function_id28.getTree());
            pushFollow(FOLLOW_parameters_in_function810);
            parameters29=parameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_parameters.add(parameters29.getTree());
            pushFollow(FOLLOW_curly_chunk_in_function812);
            curly_chunk30=curly_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_curly_chunk.add(curly_chunk30.getTree());


            // AST REWRITE
            // elements: curly_chunk, function_id, data_type, parameters, function_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 508:3: -> ^( function_key ( data_type )? function_id parameters curly_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:508:6: ^( function_key ( data_type )? function_id parameters curly_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_function_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:508:21: ( data_type )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:511:1: function_id : id= ID -> VT_FUNCTION_ID[$id] ;
    public final DRLParser.function_id_return function_id() throws RecognitionException {
        DRLParser.function_id_return retval = new DRLParser.function_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:512:2: (id= ID -> VT_FUNCTION_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:512:4: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_function_id842); if (state.failed) return retval; 
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
            // 515:3: -> VT_FUNCTION_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:518:1: query : query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block END ) ;
    public final DRLParser.query_return query() throws RecognitionException {
        DRLParser.query_return retval = new DRLParser.query_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token END35=null;
        Token SEMICOLON36=null;
        DRLParser.query_key_return query_key31 = null;

        DRLParser.query_id_return query_id32 = null;

        DRLParser.parameters_return parameters33 = null;

        DRLParser.normal_lhs_block_return normal_lhs_block34 = null;


        Object END35_tree=null;
        Object SEMICOLON36_tree=null;
        RewriteRuleTokenStream stream_END=new RewriteRuleTokenStream(adaptor,"token END");
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_parameters=new RewriteRuleSubtreeStream(adaptor,"rule parameters");
        RewriteRuleSubtreeStream stream_query_key=new RewriteRuleSubtreeStream(adaptor,"rule query_key");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        RewriteRuleSubtreeStream stream_query_id=new RewriteRuleSubtreeStream(adaptor,"rule query_id");
         pushParaphrases(DroolsParaphraseTypes.QUERY); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.QUERY); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:2: ( query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:4: query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )?
            {
            pushFollow(FOLLOW_query_key_in_query874);
            query_key31=query_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_query_key.add(query_key31.getTree());
            pushFollow(FOLLOW_query_id_in_query876);
            query_id32=query_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_query_id.add(query_id32.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:523:3: ( parameters )?
            int alt12=2;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:523:3: parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query884);
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
            pushFollow(FOLLOW_normal_lhs_block_in_query893);
            normal_lhs_block34=normal_lhs_block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block34.getTree());
            END35=(Token)match(input,END,FOLLOW_END_in_query898); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_END.add(END35);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:526:7: ( SEMICOLON )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==SEMICOLON) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:526:7: SEMICOLON
                    {
                    SEMICOLON36=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_query900); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON36);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(END35, DroolsEditorType.KEYWORD);
              		emit(SEMICOLON36, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: query_key, normal_lhs_block, query_id, END, parameters
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 529:3: -> ^( query_key query_id ( parameters )? normal_lhs_block END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:529:6: ^( query_key query_id ( parameters )? normal_lhs_block END )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_query_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_query_id.nextTree());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:529:27: ( parameters )?
                if ( stream_parameters.hasNext() ) {
                    adaptor.addChild(root_1, stream_parameters.nextTree());

                }
                stream_parameters.reset();
                adaptor.addChild(root_1, stream_normal_lhs_block.nextTree());
                adaptor.addChild(root_1, stream_END.nextNode());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:532:1: query_id : (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] );
    public final DRLParser.query_id_return query_id() throws RecognitionException {
        DRLParser.query_id_return retval = new DRLParser.query_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:533:2: (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:533:5: id= ID
                    {
                    id=(Token)match(input,ID,FOLLOW_ID_in_query_id935); if (state.failed) return retval; 
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
                    // 535:65: -> VT_QUERY_ID[$id]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_QUERY_ID, id));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:536:5: id= STRING
                    {
                    id=(Token)match(input,STRING,FOLLOW_STRING_in_query_id951); if (state.failed) return retval; 
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
                    // 538:65: -> VT_QUERY_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:541:1: parameters : LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) ;
    public final DRLParser.parameters_return parameters() throws RecognitionException {
        DRLParser.parameters_return retval = new DRLParser.parameters_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN37=null;
        Token COMMA39=null;
        Token RIGHT_PAREN41=null;
        DRLParser.param_definition_return param_definition38 = null;

        DRLParser.param_definition_return param_definition40 = null;


        Object LEFT_PAREN37_tree=null;
        Object COMMA39_tree=null;
        Object RIGHT_PAREN41_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_param_definition=new RewriteRuleSubtreeStream(adaptor,"rule param_definition");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:542:2: ( LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:542:4: LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN
            {
            LEFT_PAREN37=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parameters970); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN37);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN37, DroolsEditorType.SYMBOL);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:4: ( param_definition ( COMMA param_definition )* )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==ID) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:6: param_definition ( COMMA param_definition )*
                    {
                    pushFollow(FOLLOW_param_definition_in_parameters979);
                    param_definition38=param_definition();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_param_definition.add(param_definition38.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:23: ( COMMA param_definition )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==COMMA) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:24: COMMA param_definition
                    	    {
                    	    COMMA39=(Token)match(input,COMMA,FOLLOW_COMMA_in_parameters982); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA39);

                    	    if ( state.backtracking==0 ) {
                    	      	emit(COMMA39, DroolsEditorType.SYMBOL);	
                    	    }
                    	    pushFollow(FOLLOW_param_definition_in_parameters986);
                    	    param_definition40=param_definition();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_param_definition.add(param_definition40.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    }
                    break;

            }

            RIGHT_PAREN41=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parameters995); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN41);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN41, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: RIGHT_PAREN, param_definition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 545:3: -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:545:6: ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_PARAM_LIST, "VT_PARAM_LIST"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:545:22: ( param_definition )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:548:1: param_definition : ( data_type )? argument ;
    public final DRLParser.param_definition_return param_definition() throws RecognitionException {
        DRLParser.param_definition_return retval = new DRLParser.param_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.data_type_return data_type42 = null;

        DRLParser.argument_return argument43 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:549:2: ( ( data_type )? argument )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:549:4: ( data_type )? argument
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:549:4: ( data_type )?
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:549:4: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition1021);
                    data_type42=data_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, data_type42.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition1024);
            argument43=argument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, argument43.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:1: argument : ID ( dimension_definition )* ;
    public final DRLParser.argument_return argument() throws RecognitionException {
        DRLParser.argument_return retval = new DRLParser.argument_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID44=null;
        DRLParser.dimension_definition_return dimension_definition45 = null;


        Object ID44_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:553:2: ( ID ( dimension_definition )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:553:4: ID ( dimension_definition )*
            {
            root_0 = (Object)adaptor.nil();

            ID44=(Token)match(input,ID,FOLLOW_ID_in_argument1035); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID44_tree = (Object)adaptor.create(ID44);
            adaptor.addChild(root_0, ID44_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(ID44, DroolsEditorType.IDENTIFIER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:554:3: ( dimension_definition )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==LEFT_SQUARE) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:554:3: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_argument1041);
            	    dimension_definition45=dimension_definition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, dimension_definition45.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:557:1: type_declaration : declare_key type_declare_id ( decl_metadata )* ( decl_field )* END -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END ) ;
    public final DRLParser.type_declaration_return type_declaration() throws RecognitionException {
        DRLParser.type_declaration_return retval = new DRLParser.type_declaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token END50=null;
        DRLParser.declare_key_return declare_key46 = null;

        DRLParser.type_declare_id_return type_declare_id47 = null;

        DRLParser.decl_metadata_return decl_metadata48 = null;

        DRLParser.decl_field_return decl_field49 = null;


        Object END50_tree=null;
        RewriteRuleTokenStream stream_END=new RewriteRuleTokenStream(adaptor,"token END");
        RewriteRuleSubtreeStream stream_decl_field=new RewriteRuleSubtreeStream(adaptor,"rule decl_field");
        RewriteRuleSubtreeStream stream_declare_key=new RewriteRuleSubtreeStream(adaptor,"rule declare_key");
        RewriteRuleSubtreeStream stream_decl_metadata=new RewriteRuleSubtreeStream(adaptor,"rule decl_metadata");
        RewriteRuleSubtreeStream stream_type_declare_id=new RewriteRuleSubtreeStream(adaptor,"rule type_declare_id");
         pushParaphrases(DroolsParaphraseTypes.TYPE_DECLARE); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.TYPE_DECLARATION); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:560:2: ( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:560:4: declare_key type_declare_id ( decl_metadata )* ( decl_field )* END
            {
            pushFollow(FOLLOW_declare_key_in_type_declaration1064);
            declare_key46=declare_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_declare_key.add(declare_key46.getTree());
            pushFollow(FOLLOW_type_declare_id_in_type_declaration1067);
            type_declare_id47=type_declare_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_type_declare_id.add(type_declare_id47.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:561:3: ( decl_metadata )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AT) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:561:3: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_type_declaration1071);
            	    decl_metadata48=decl_metadata();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_decl_metadata.add(decl_metadata48.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:3: ( decl_field )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==ID) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:3: decl_field
            	    {
            	    pushFollow(FOLLOW_decl_field_in_type_declaration1076);
            	    decl_field49=decl_field();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_decl_field.add(decl_field49.getTree());

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            END50=(Token)match(input,END,FOLLOW_END_in_type_declaration1081); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_END.add(END50);

            if ( state.backtracking==0 ) {
              	emit(END50, DroolsEditorType.KEYWORD);	
            }


            // AST REWRITE
            // elements: decl_metadata, END, type_declare_id, decl_field, declare_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 565:3: -> ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:6: ^( declare_key type_declare_id ( decl_metadata )* ( decl_field )* END )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_declare_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_type_declare_id.nextTree());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:36: ( decl_metadata )*
                while ( stream_decl_metadata.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_metadata.nextTree());

                }
                stream_decl_metadata.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:51: ( decl_field )*
                while ( stream_decl_field.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_field.nextTree());

                }
                stream_decl_field.reset();
                adaptor.addChild(root_1, stream_END.nextNode());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:568:1: type_declare_id : id= ID -> VT_TYPE_DECLARE_ID[$id] ;
    public final DRLParser.type_declare_id_return type_declare_id() throws RecognitionException {
        DRLParser.type_declare_id_return retval = new DRLParser.type_declare_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:569:2: (id= ID -> VT_TYPE_DECLARE_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:569:5: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_type_declare_id1116); if (state.failed) return retval; 
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:574:1: decl_metadata : AT ID paren_chunk -> ^( AT ID paren_chunk ) ;
    public final DRLParser.decl_metadata_return decl_metadata() throws RecognitionException {
        DRLParser.decl_metadata_return retval = new DRLParser.decl_metadata_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AT51=null;
        Token ID52=null;
        DRLParser.paren_chunk_return paren_chunk53 = null;


        Object AT51_tree=null;
        Object ID52_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:575:2: ( AT ID paren_chunk -> ^( AT ID paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:575:4: AT ID paren_chunk
            {
            AT51=(Token)match(input,AT,FOLLOW_AT_in_decl_metadata1135); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AT.add(AT51);

            if ( state.backtracking==0 ) {
              	emit(AT51, DroolsEditorType.SYMBOL);	
            }
            ID52=(Token)match(input,ID,FOLLOW_ID_in_decl_metadata1143); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID52);

            if ( state.backtracking==0 ) {
              	emit(ID52, DroolsEditorType.IDENTIFIER);	
            }
            pushFollow(FOLLOW_paren_chunk_in_decl_metadata1150);
            paren_chunk53=paren_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk53.getTree());


            // AST REWRITE
            // elements: ID, paren_chunk, AT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 580:3: -> ^( AT ID paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:580:6: ^( AT ID paren_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_AT.nextNode(), root_1);

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
    // $ANTLR end "decl_metadata"

    public static class decl_field_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "decl_field"
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:1: decl_field : ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) ;
    public final DRLParser.decl_field_return decl_field() throws RecognitionException {
        DRLParser.decl_field_return retval = new DRLParser.decl_field_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID54=null;
        Token COLON56=null;
        DRLParser.decl_field_initialization_return decl_field_initialization55 = null;

        DRLParser.data_type_return data_type57 = null;

        DRLParser.decl_metadata_return decl_metadata58 = null;


        Object ID54_tree=null;
        Object COLON56_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_decl_metadata=new RewriteRuleSubtreeStream(adaptor,"rule decl_metadata");
        RewriteRuleSubtreeStream stream_decl_field_initialization=new RewriteRuleSubtreeStream(adaptor,"rule decl_field_initialization");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:584:2: ( ID ( decl_field_initialization )? COLON data_type ( decl_metadata )* -> ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:584:4: ID ( decl_field_initialization )? COLON data_type ( decl_metadata )*
            {
            ID54=(Token)match(input,ID,FOLLOW_ID_in_decl_field1173); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID54);

            if ( state.backtracking==0 ) {
              	emit(ID54, DroolsEditorType.IDENTIFIER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:585:3: ( decl_field_initialization )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==EQUALS) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:585:3: decl_field_initialization
                    {
                    pushFollow(FOLLOW_decl_field_initialization_in_decl_field1179);
                    decl_field_initialization55=decl_field_initialization();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_decl_field_initialization.add(decl_field_initialization55.getTree());

                    }
                    break;

            }

            COLON56=(Token)match(input,COLON,FOLLOW_COLON_in_decl_field1185); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON56);

            if ( state.backtracking==0 ) {
              	emit(COLON56, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_data_type_in_decl_field1191);
            data_type57=data_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_data_type.add(data_type57.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:588:3: ( decl_metadata )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==AT) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:588:3: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_decl_field1195);
            	    decl_metadata58=decl_metadata();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_decl_metadata.add(decl_metadata58.getTree());

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);



            // AST REWRITE
            // elements: decl_metadata, ID, decl_field_initialization, data_type
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
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:589:6: ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ID.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:589:11: ( decl_field_initialization )?
                if ( stream_decl_field_initialization.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_field_initialization.nextTree());

                }
                stream_decl_field_initialization.reset();
                adaptor.addChild(root_1, stream_data_type.nextTree());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:589:48: ( decl_metadata )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:592:1: decl_field_initialization : EQUALS paren_chunk -> ^( EQUALS paren_chunk ) ;
    public final DRLParser.decl_field_initialization_return decl_field_initialization() throws RecognitionException {
        DRLParser.decl_field_initialization_return retval = new DRLParser.decl_field_initialization_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS59=null;
        DRLParser.paren_chunk_return paren_chunk60 = null;


        Object EQUALS59_tree=null;
        RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:593:2: ( EQUALS paren_chunk -> ^( EQUALS paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:593:4: EQUALS paren_chunk
            {
            EQUALS59=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_decl_field_initialization1223); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS59);

            if ( state.backtracking==0 ) {
              	emit(EQUALS59, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_paren_chunk_in_decl_field_initialization1229);
            paren_chunk60=paren_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk60.getTree());


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
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:595:5: ^( EQUALS paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:598:1: template : template_key template_id (semi1= SEMICOLON )? ( template_slot )+ END (semi2= SEMICOLON )? -> ^( template_key template_id ( template_slot )+ END ) ;
    public final DRLParser.template_return template() throws RecognitionException {
        DRLParser.template_return retval = new DRLParser.template_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token semi1=null;
        Token semi2=null;
        Token END64=null;
        DRLParser.template_key_return template_key61 = null;

        DRLParser.template_id_return template_id62 = null;

        DRLParser.template_slot_return template_slot63 = null;


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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:601:2: ( template_key template_id (semi1= SEMICOLON )? ( template_slot )+ END (semi2= SEMICOLON )? -> ^( template_key template_id ( template_slot )+ END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:2: template_key template_id (semi1= SEMICOLON )? ( template_slot )+ END (semi2= SEMICOLON )?
            {
            if ( state.backtracking==0 ) {
              	beginSentence(DroolsSentenceType.TEMPLATE);	
            }
            pushFollow(FOLLOW_template_key_in_template1266);
            template_key61=template_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_template_key.add(template_key61.getTree());
            pushFollow(FOLLOW_template_id_in_template1268);
            template_id62=template_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_template_id.add(template_id62.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:604:8: (semi1= SEMICOLON )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==SEMICOLON) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:604:8: semi1= SEMICOLON
                    {
                    semi1=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1275); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(semi1);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(semi1, DroolsEditorType.SYMBOL);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:606:3: ( template_slot )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:606:3: template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template1283);
            	    template_slot63=template_slot();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_template_slot.add(template_slot63.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt24 >= 1 ) break loop24;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(24, input);
                        throw eee;
                }
                cnt24++;
            } while (true);

            END64=(Token)match(input,END,FOLLOW_END_in_template1288); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_END.add(END64);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:607:12: (semi2= SEMICOLON )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==SEMICOLON) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:607:12: semi2= SEMICOLON
                    {
                    semi2=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1292); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(semi2);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(END64, DroolsEditorType.KEYWORD);
              		emit(semi2, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: END, template_slot, template_key, template_id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 610:3: -> ^( template_key template_id ( template_slot )+ END )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:610:6: ^( template_key template_id ( template_slot )+ END )
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
                adaptor.addChild(root_1, stream_END.nextNode());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:613:1: template_id : (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] );
    public final DRLParser.template_id_return template_id() throws RecognitionException {
        DRLParser.template_id_return retval = new DRLParser.template_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:614:2: (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==ID) ) {
                alt26=1;
            }
            else if ( (LA26_0==STRING) ) {
                alt26=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:614:5: id= ID
                    {
                    id=(Token)match(input,ID,FOLLOW_ID_in_template_id1325); if (state.failed) return retval; 
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
                    // 616:68: -> VT_TEMPLATE_ID[$id]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_TEMPLATE_ID, id));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:617:5: id= STRING
                    {
                    id=(Token)match(input,STRING,FOLLOW_STRING_in_template_id1341); if (state.failed) return retval; 
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:622:1: template_slot : data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) ;
    public final DRLParser.template_slot_return template_slot() throws RecognitionException {
        DRLParser.template_slot_return retval = new DRLParser.template_slot_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON67=null;
        DRLParser.data_type_return data_type65 = null;

        DRLParser.slot_id_return slot_id66 = null;


        Object SEMICOLON67_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_slot_id=new RewriteRuleSubtreeStream(adaptor,"rule slot_id");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:2: ( data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:5: data_type slot_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_data_type_in_template_slot1361);
            data_type65=data_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_data_type.add(data_type65.getTree());
            pushFollow(FOLLOW_slot_id_in_template_slot1363);
            slot_id66=slot_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_slot_id.add(slot_id66.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:23: ( SEMICOLON )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==SEMICOLON) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:23: SEMICOLON
                    {
                    SEMICOLON67=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template_slot1365); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMICOLON.add(SEMICOLON67);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON67, DroolsEditorType.SYMBOL);	
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
            // 625:3: -> ^( VT_SLOT data_type slot_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:625:6: ^( VT_SLOT data_type slot_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:628:1: slot_id : id= ID -> VT_SLOT_ID[$id] ;
    public final DRLParser.slot_id_return slot_id() throws RecognitionException {
        DRLParser.slot_id_return retval = new DRLParser.slot_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:629:2: (id= ID -> VT_SLOT_ID[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:629:4: id= ID
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_slot_id1394); if (state.failed) return retval; 
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:1: rule : rule_key rule_id ( extend_key rule_id )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk ) ;
    public final DRLParser.rule_return rule() throws RecognitionException {
        DRLParser.rule_return retval = new DRLParser.rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.rule_key_return rule_key68 = null;

        DRLParser.rule_id_return rule_id69 = null;

        DRLParser.extend_key_return extend_key70 = null;

        DRLParser.rule_id_return rule_id71 = null;

        DRLParser.decl_metadata_return decl_metadata72 = null;

        DRLParser.rule_attributes_return rule_attributes73 = null;

        DRLParser.when_part_return when_part74 = null;

        DRLParser.rhs_chunk_return rhs_chunk75 = null;


        RewriteRuleSubtreeStream stream_rule_key=new RewriteRuleSubtreeStream(adaptor,"rule rule_key");
        RewriteRuleSubtreeStream stream_rule_id=new RewriteRuleSubtreeStream(adaptor,"rule rule_id");
        RewriteRuleSubtreeStream stream_when_part=new RewriteRuleSubtreeStream(adaptor,"rule when_part");
        RewriteRuleSubtreeStream stream_rule_attributes=new RewriteRuleSubtreeStream(adaptor,"rule rule_attributes");
        RewriteRuleSubtreeStream stream_rhs_chunk=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk");
        RewriteRuleSubtreeStream stream_decl_metadata=new RewriteRuleSubtreeStream(adaptor,"rule decl_metadata");
        RewriteRuleSubtreeStream stream_extend_key=new RewriteRuleSubtreeStream(adaptor,"rule extend_key");
         boolean isFailed = true; pushParaphrases(DroolsParaphraseTypes.RULE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:637:2: ( rule_key rule_id ( extend_key rule_id )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:638:2: rule_key rule_id ( extend_key rule_id )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk
            {
            if ( state.backtracking==0 ) {
              	beginSentence(DroolsSentenceType.RULE);	
            }
            pushFollow(FOLLOW_rule_key_in_rule1431);
            rule_key68=rule_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_key.add(rule_key68.getTree());
            pushFollow(FOLLOW_rule_id_in_rule1433);
            rule_id69=rule_id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_id.add(rule_id69.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:641:3: ( extend_key rule_id )?
            int alt28=2;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:641:4: extend_key rule_id
                    {
                    pushFollow(FOLLOW_extend_key_in_rule1442);
                    extend_key70=extend_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_extend_key.add(extend_key70.getTree());
                    pushFollow(FOLLOW_rule_id_in_rule1444);
                    rule_id71=rule_id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rule_id.add(rule_id71.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:641:25: ( decl_metadata )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==AT) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:641:25: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_rule1448);
            	    decl_metadata72=decl_metadata();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_decl_metadata.add(decl_metadata72.getTree());

            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:641:40: ( rule_attributes )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))))) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:641:40: rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1451);
                    rule_attributes73=rule_attributes();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rule_attributes.add(rule_attributes73.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:641:57: ( when_part )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==WHEN) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:641:57: when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule1454);
                    when_part74=when_part();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_when_part.add(when_part74.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1457);
            rhs_chunk75=rhs_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rhs_chunk.add(rhs_chunk75.getTree());


            // AST REWRITE
            // elements: rule_id, rule_id, rhs_chunk, rule_attributes, when_part, extend_key, rule_key, decl_metadata
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 642:3: -> ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:6: ^( rule_key rule_id ( ^( extend_key rule_id ) )? ( decl_metadata )* ( rule_attributes )? ( when_part )? rhs_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_rule_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rule_id.nextTree());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:25: ( ^( extend_key rule_id ) )?
                if ( stream_rule_id.hasNext()||stream_extend_key.hasNext() ) {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:25: ^( extend_key rule_id )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_extend_key.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_rule_id.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_rule_id.reset();
                stream_extend_key.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:48: ( decl_metadata )*
                while ( stream_decl_metadata.hasNext() ) {
                    adaptor.addChild(root_1, stream_decl_metadata.nextTree());

                }
                stream_decl_metadata.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:63: ( rule_attributes )?
                if ( stream_rule_attributes.hasNext() ) {
                    adaptor.addChild(root_1, stream_rule_attributes.nextTree());

                }
                stream_rule_attributes.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:80: ( when_part )?
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
            				 validateLT(1, DroolsSoftKeywords.SALIENCE) || validateLT(1, DroolsSoftKeywords.DURATION))){
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:687:1: when_part : WHEN ( COLON )? normal_lhs_block -> WHEN normal_lhs_block ;
    public final DRLParser.when_part_return when_part() throws RecognitionException {
        DRLParser.when_part_return retval = new DRLParser.when_part_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token WHEN76=null;
        Token COLON77=null;
        DRLParser.normal_lhs_block_return normal_lhs_block78 = null;


        Object WHEN76_tree=null;
        Object COLON77_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_WHEN=new RewriteRuleTokenStream(adaptor,"token WHEN");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:688:2: ( WHEN ( COLON )? normal_lhs_block -> WHEN normal_lhs_block )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:688:5: WHEN ( COLON )? normal_lhs_block
            {
            WHEN76=(Token)match(input,WHEN,FOLLOW_WHEN_in_when_part1501); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_WHEN.add(WHEN76);

            if ( state.backtracking==0 ) {
              	emit(WHEN76, DroolsEditorType.KEYWORD);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:689:3: ( COLON )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==COLON) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:689:3: COLON
                    {
                    COLON77=(Token)match(input,COLON,FOLLOW_COLON_in_when_part1507); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON77);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(COLON77, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }
            pushFollow(FOLLOW_normal_lhs_block_in_when_part1517);
            normal_lhs_block78=normal_lhs_block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block78.getTree());


            // AST REWRITE
            // elements: WHEN, normal_lhs_block
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:695:1: rule_id : (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] );
    public final DRLParser.rule_id_return rule_id() throws RecognitionException {
        DRLParser.rule_id_return retval = new DRLParser.rule_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:696:2: (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==ID) ) {
                alt33=1;
            }
            else if ( (LA33_0==STRING) ) {
                alt33=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:696:5: id= ID
                    {
                    id=(Token)match(input,ID,FOLLOW_ID_in_rule_id1538); if (state.failed) return retval; 
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:699:5: id= STRING
                    {
                    id=(Token)match(input,STRING,FOLLOW_STRING_in_rule_id1554); if (state.failed) return retval; 
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:704:1: rule_attributes : ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) ;
    public final DRLParser.rule_attributes_return rule_attributes() throws RecognitionException {
        DRLParser.rule_attributes_return retval = new DRLParser.rule_attributes_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON80=null;
        Token COMMA82=null;
        DRLParser.rule_attribute_return attr = null;

        DRLParser.attributes_key_return attributes_key79 = null;

        DRLParser.rule_attribute_return rule_attribute81 = null;


        Object COLON80_tree=null;
        Object COMMA82_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_attributes_key=new RewriteRuleSubtreeStream(adaptor,"rule attributes_key");
        RewriteRuleSubtreeStream stream_rule_attribute=new RewriteRuleSubtreeStream(adaptor,"rule rule_attribute");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:705:2: ( ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:705:4: ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:705:4: ( attributes_key COLON )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))))) {
                int LA34_1 = input.LA(2);

                if ( (LA34_1==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {
                    alt34=1;
                }
            }
            switch (alt34) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:705:6: attributes_key COLON
                    {
                    pushFollow(FOLLOW_attributes_key_in_rule_attributes1575);
                    attributes_key79=attributes_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_attributes_key.add(attributes_key79.getTree());
                    COLON80=(Token)match(input,COLON,FOLLOW_COLON_in_rule_attributes1577); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON80);

                    if ( state.backtracking==0 ) {
                      	emit(COLON80, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1587);
            rule_attribute81=rule_attribute();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_attribute.add(rule_attribute81.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:706:18: ( ( COMMA )? attr= rule_attribute )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==ID||LA36_0==COMMA) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:706:20: ( COMMA )? attr= rule_attribute
            	    {
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:706:20: ( COMMA )?
            	    int alt35=2;
            	    int LA35_0 = input.LA(1);

            	    if ( (LA35_0==COMMA) ) {
            	        alt35=1;
            	    }
            	    switch (alt35) {
            	        case 1 :
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:706:20: COMMA
            	            {
            	            COMMA82=(Token)match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1591); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_COMMA.add(COMMA82);


            	            }
            	            break;

            	    }

            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA82, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1598);
            	    attr=rule_attribute();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rule_attribute.add(attr.getTree());

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
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 707:3: -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:6: ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_RULE_ATTRIBUTES, "VT_RULE_ATTRIBUTES"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:27: ( attributes_key )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:710:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );
    public final DRLParser.rule_attribute_return rule_attribute() throws RecognitionException {
        DRLParser.rule_attribute_return retval = new DRLParser.rule_attribute_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.salience_return salience83 = null;

        DRLParser.no_loop_return no_loop84 = null;

        DRLParser.agenda_group_return agenda_group85 = null;

        DRLParser.duration_return duration86 = null;

        DRLParser.activation_group_return activation_group87 = null;

        DRLParser.auto_focus_return auto_focus88 = null;

        DRLParser.date_effective_return date_effective89 = null;

        DRLParser.date_expires_return date_expires90 = null;

        DRLParser.enabled_return enabled91 = null;

        DRLParser.ruleflow_group_return ruleflow_group92 = null;

        DRLParser.lock_on_active_return lock_on_active93 = null;

        DRLParser.dialect_return dialect94 = null;



         boolean isFailed = true; pushParaphrases(DroolsParaphraseTypes.RULE_ATTRIBUTE); 
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:713:2: ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect )
            int alt37=12;
            alt37 = dfa37.predict(input);
            switch (alt37) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:713:4: salience
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_salience_in_rule_attribute1637);
                    salience83=salience();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, salience83.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:714:4: no_loop
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_no_loop_in_rule_attribute1643);
                    no_loop84=no_loop();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, no_loop84.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:715:4: agenda_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1648);
                    agenda_group85=agenda_group();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, agenda_group85.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:716:4: duration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_duration_in_rule_attribute1655);
                    duration86=duration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, duration86.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:717:4: activation_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_activation_group_in_rule_attribute1662);
                    activation_group87=activation_group();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, activation_group87.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:718:4: auto_focus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1668);
                    auto_focus88=auto_focus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, auto_focus88.getTree());

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:719:4: date_effective
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_effective_in_rule_attribute1674);
                    date_effective89=date_effective();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, date_effective89.getTree());

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:720:4: date_expires
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_expires_in_rule_attribute1680);
                    date_expires90=date_expires();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, date_expires90.getTree());

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:721:4: enabled
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enabled_in_rule_attribute1686);
                    enabled91=enabled();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enabled91.getTree());

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:722:4: ruleflow_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1692);
                    ruleflow_group92=ruleflow_group();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleflow_group92.getTree());

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:723:4: lock_on_active
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1698);
                    lock_on_active93=lock_on_active();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lock_on_active93.getTree());

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:724:4: dialect
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_dialect_in_rule_attribute1703);
                    dialect94=dialect();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, dialect94.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:734:1: date_effective : date_effective_key STRING ;
    public final DRLParser.date_effective_return date_effective() throws RecognitionException {
        DRLParser.date_effective_return retval = new DRLParser.date_effective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING96=null;
        DRLParser.date_effective_key_return date_effective_key95 = null;


        Object STRING96_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:2: ( date_effective_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:4: date_effective_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_effective_key_in_date_effective1718);
            date_effective_key95=date_effective_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_effective_key95.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING96=(Token)match(input,STRING,FOLLOW_STRING_in_date_effective1723); if (state.failed) return retval;
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
    // $ANTLR end "date_effective"

    public static class date_expires_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "date_expires"
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:739:1: date_expires : date_expires_key STRING ;
    public final DRLParser.date_expires_return date_expires() throws RecognitionException {
        DRLParser.date_expires_return retval = new DRLParser.date_expires_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING98=null;
        DRLParser.date_expires_key_return date_expires_key97 = null;


        Object STRING98_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:740:2: ( date_expires_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:740:4: date_expires_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_expires_key_in_date_expires1737);
            date_expires_key97=date_expires_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_expires_key97.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING98=(Token)match(input,STRING,FOLLOW_STRING_in_date_expires1742); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING98_tree = (Object)adaptor.create(STRING98);
            adaptor.addChild(root_0, STRING98_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING98, DroolsEditorType.STRING_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:1: enabled : enabled_key ( BOOL | paren_chunk ) ;
    public final DRLParser.enabled_return enabled() throws RecognitionException {
        DRLParser.enabled_return retval = new DRLParser.enabled_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL100=null;
        DRLParser.enabled_key_return enabled_key99 = null;

        DRLParser.paren_chunk_return paren_chunk101 = null;


        Object BOOL100_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:2: ( enabled_key ( BOOL | paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:4: enabled_key ( BOOL | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_enabled_key_in_enabled1757);
            enabled_key99=enabled_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(enabled_key99.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:746:6: ( BOOL | paren_chunk )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==BOOL) ) {
                alt38=1;
            }
            else if ( (LA38_0==LEFT_PAREN) ) {
                alt38=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:746:8: BOOL
                    {
                    BOOL100=(Token)match(input,BOOL,FOLLOW_BOOL_in_enabled1770); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL100_tree = (Object)adaptor.create(BOOL100);
                    adaptor.addChild(root_0, BOOL100_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(BOOL100, DroolsEditorType.BOOLEAN_CONST );	
                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:747:8: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_enabled1781);
                    paren_chunk101=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk101.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:751:1: salience : salience_key ( INT | paren_chunk ) ;
    public final DRLParser.salience_return salience() throws RecognitionException {
        DRLParser.salience_return retval = new DRLParser.salience_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT103=null;
        DRLParser.salience_key_return salience_key102 = null;

        DRLParser.paren_chunk_return paren_chunk104 = null;


        Object INT103_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:752:2: ( salience_key ( INT | paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:752:4: salience_key ( INT | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_salience_key_in_salience1801);
            salience_key102=salience_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(salience_key102.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:753:3: ( INT | paren_chunk )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==INT) ) {
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:753:5: INT
                    {
                    INT103=(Token)match(input,INT,FOLLOW_INT_in_salience1810); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT103_tree = (Object)adaptor.create(INT103);
                    adaptor.addChild(root_0, INT103_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(INT103, DroolsEditorType.NUMERIC_CONST );	
                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:754:5: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1819);
                    paren_chunk104=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk104.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:1: no_loop : no_loop_key ( BOOL )? ;
    public final DRLParser.no_loop_return no_loop() throws RecognitionException {
        DRLParser.no_loop_return retval = new DRLParser.no_loop_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL106=null;
        DRLParser.no_loop_key_return no_loop_key105 = null;


        Object BOOL106_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:759:2: ( no_loop_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:759:4: no_loop_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_no_loop_key_in_no_loop1834);
            no_loop_key105=no_loop_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(no_loop_key105.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:759:66: ( BOOL )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==BOOL) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:759:66: BOOL
                    {
                    BOOL106=(Token)match(input,BOOL,FOLLOW_BOOL_in_no_loop1839); if (state.failed) return retval;
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
    // $ANTLR end "no_loop"

    public static class auto_focus_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "auto_focus"
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:763:1: auto_focus : auto_focus_key ( BOOL )? ;
    public final DRLParser.auto_focus_return auto_focus() throws RecognitionException {
        DRLParser.auto_focus_return retval = new DRLParser.auto_focus_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL108=null;
        DRLParser.auto_focus_key_return auto_focus_key107 = null;


        Object BOOL108_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:2: ( auto_focus_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:4: auto_focus_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_auto_focus_key_in_auto_focus1854);
            auto_focus_key107=auto_focus_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(auto_focus_key107.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:69: ( BOOL )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==BOOL) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:69: BOOL
                    {
                    BOOL108=(Token)match(input,BOOL,FOLLOW_BOOL_in_auto_focus1859); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL108_tree = (Object)adaptor.create(BOOL108);
                    adaptor.addChild(root_0, BOOL108_tree);
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(BOOL108, DroolsEditorType.BOOLEAN_CONST );	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:768:1: activation_group : activation_group_key STRING ;
    public final DRLParser.activation_group_return activation_group() throws RecognitionException {
        DRLParser.activation_group_return retval = new DRLParser.activation_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING110=null;
        DRLParser.activation_group_key_return activation_group_key109 = null;


        Object STRING110_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:769:2: ( activation_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:769:4: activation_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_activation_group_key_in_activation_group1876);
            activation_group_key109=activation_group_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(activation_group_key109.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING110=(Token)match(input,STRING,FOLLOW_STRING_in_activation_group1881); if (state.failed) return retval;
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
    // $ANTLR end "activation_group"

    public static class ruleflow_group_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleflow_group"
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:773:1: ruleflow_group : ruleflow_group_key STRING ;
    public final DRLParser.ruleflow_group_return ruleflow_group() throws RecognitionException {
        DRLParser.ruleflow_group_return retval = new DRLParser.ruleflow_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING112=null;
        DRLParser.ruleflow_group_key_return ruleflow_group_key111 = null;


        Object STRING112_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:774:2: ( ruleflow_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:774:4: ruleflow_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_ruleflow_group_key_in_ruleflow_group1895);
            ruleflow_group_key111=ruleflow_group_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(ruleflow_group_key111.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING112=(Token)match(input,STRING,FOLLOW_STRING_in_ruleflow_group1900); if (state.failed) return retval;
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
    // $ANTLR end "ruleflow_group"

    public static class agenda_group_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "agenda_group"
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:778:1: agenda_group : agenda_group_key STRING ;
    public final DRLParser.agenda_group_return agenda_group() throws RecognitionException {
        DRLParser.agenda_group_return retval = new DRLParser.agenda_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING114=null;
        DRLParser.agenda_group_key_return agenda_group_key113 = null;


        Object STRING114_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:779:2: ( agenda_group_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:779:4: agenda_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_agenda_group_key_in_agenda_group1914);
            agenda_group_key113=agenda_group_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(agenda_group_key113.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING114=(Token)match(input,STRING,FOLLOW_STRING_in_agenda_group1919); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING114_tree = (Object)adaptor.create(STRING114);
            adaptor.addChild(root_0, STRING114_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(STRING114, DroolsEditorType.STRING_CONST );	
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

    public static class duration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "duration"
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:1: duration : duration_key INT ;
    public final DRLParser.duration_return duration() throws RecognitionException {
        DRLParser.duration_return retval = new DRLParser.duration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT116=null;
        DRLParser.duration_key_return duration_key115 = null;


        Object INT116_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:784:2: ( duration_key INT )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:784:4: duration_key INT
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_duration_key_in_duration1933);
            duration_key115=duration_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(duration_key115.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            INT116=(Token)match(input,INT,FOLLOW_INT_in_duration1938); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            INT116_tree = (Object)adaptor.create(INT116);
            adaptor.addChild(root_0, INT116_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(INT116, DroolsEditorType.NUMERIC_CONST );	
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
    // $ANTLR end "duration"

    public static class dialect_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dialect"
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:788:1: dialect : dialect_key STRING ;
    public final DRLParser.dialect_return dialect() throws RecognitionException {
        DRLParser.dialect_return retval = new DRLParser.dialect_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING118=null;
        DRLParser.dialect_key_return dialect_key117 = null;


        Object STRING118_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:2: ( dialect_key STRING )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:4: dialect_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_dialect_key_in_dialect1954);
            dialect_key117=dialect_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(dialect_key117.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            STRING118=(Token)match(input,STRING,FOLLOW_STRING_in_dialect1959); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:793:1: lock_on_active : lock_on_active_key ( BOOL )? ;
    public final DRLParser.lock_on_active_return lock_on_active() throws RecognitionException {
        DRLParser.lock_on_active_return retval = new DRLParser.lock_on_active_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL120=null;
        DRLParser.lock_on_active_key_return lock_on_active_key119 = null;


        Object BOOL120_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:794:2: ( lock_on_active_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:794:4: lock_on_active_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lock_on_active_key_in_lock_on_active1977);
            lock_on_active_key119=lock_on_active_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(lock_on_active_key119.getTree(), root_0);
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:794:73: ( BOOL )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==BOOL) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:794:73: BOOL
                    {
                    BOOL120=(Token)match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1982); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:1: normal_lhs_block : ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) ;
    public final DRLParser.normal_lhs_block_return normal_lhs_block() throws RecognitionException {
        DRLParser.normal_lhs_block_return retval = new DRLParser.normal_lhs_block_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.lhs_return lhs121 = null;


        RewriteRuleSubtreeStream stream_lhs=new RewriteRuleSubtreeStream(adaptor,"rule lhs");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:799:2: ( ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:799:4: ( lhs )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:799:4: ( lhs )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==ID||LA43_0==LEFT_PAREN) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:799:4: lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1997);
            	    lhs121=lhs();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_lhs.add(lhs121.getTree());

            	    }
            	    break;

            	default :
            	    break loop43;
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
            // 800:2: -> ^( VT_AND_IMPLICIT ( lhs )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:800:5: ^( VT_AND_IMPLICIT ( lhs )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_AND_IMPLICIT, "VT_AND_IMPLICIT"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:800:23: ( lhs )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:1: lhs : lhs_or ;
    public final DRLParser.lhs_return lhs() throws RecognitionException {
        DRLParser.lhs_return retval = new DRLParser.lhs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.lhs_or_return lhs_or122 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:5: ( lhs_or )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:7: lhs_or
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_or_in_lhs2018);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:806:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:3: ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==LEFT_PAREN) ) {
                int LA47_1 = input.LA(2);

                if ( (synpred1_DRL()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA47_0==ID) ) {
                alt47=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:5: ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN
                    {
                    LEFT_PAREN123=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or2042); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN123);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN123, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_or_key_in_lhs_or2052);
                    or=or_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_or_key.add(or.getTree());
                    if ( state.backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    }
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:813:4: ( lhs_and )+
                    int cnt44=0;
                    loop44:
                    do {
                        int alt44=2;
                        int LA44_0 = input.LA(1);

                        if ( (LA44_0==ID||LA44_0==LEFT_PAREN) ) {
                            alt44=1;
                        }


                        switch (alt44) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:813:4: lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2060);
                    	    lhs_and124=lhs_and();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_lhs_and.add(lhs_and124.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt44 >= 1 ) break loop44;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(44, input);
                                throw eee;
                        }
                        cnt44++;
                    } while (true);

                    RIGHT_PAREN125=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or2066); if (state.failed) return retval; 
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
                    // 815:3: -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:815:6: ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:816:4: ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:816:4: ( lhs_and -> lhs_and )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:816:5: lhs_and
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or2089);
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
                    // 816:13: -> lhs_and
                    {
                        adaptor.addChild(root_0, stream_lhs_and.nextTree());

                    }

                    retval.tree = root_0;}
                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:817:3: ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    loop46:
                    do {
                        int alt46=2;
                        int LA46_0 = input.LA(1);

                        if ( (LA46_0==ID) ) {
                            int LA46_2 = input.LA(2);

                            if ( ((synpred2_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.OR))))) ) {
                                alt46=1;
                            }


                        }
                        else if ( (LA46_0==DOUBLE_PIPE) ) {
                            int LA46_3 = input.LA(2);

                            if ( (synpred2_DRL()) ) {
                                alt46=1;
                            }


                        }


                        switch (alt46) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:817:5: ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and
                    	    {
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:817:28: (value= or_key | pipe= DOUBLE_PIPE )
                    	    int alt45=2;
                    	    int LA45_0 = input.LA(1);

                    	    if ( (LA45_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                    	        alt45=1;
                    	    }
                    	    else if ( (LA45_0==DOUBLE_PIPE) ) {
                    	        alt45=2;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("", 45, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt45) {
                    	        case 1 :
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:817:29: value= or_key
                    	            {
                    	            pushFollow(FOLLOW_or_key_in_lhs_or2111);
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
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:817:69: pipe= DOUBLE_PIPE
                    	            {
                    	            pipe=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_lhs_or2118); if (state.failed) return retval; 
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
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2129);
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
                    	    // 820:3: -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
                    	    {
                    	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:820:6: ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
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
                    	    break loop46;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:826:3: ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* )
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==LEFT_PAREN) ) {
                int LA51_1 = input.LA(2);

                if ( (synpred3_DRL()) ) {
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:826:5: ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN
                    {
                    LEFT_PAREN128=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and2170); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN128);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN128, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_and_key_in_lhs_and2180);
                    and=and_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_and_key.add(and.getTree());
                    if ( state.backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	
                    }
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:830:4: ( lhs_unary )+
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
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:830:4: lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2188);
                    	    lhs_unary129=lhs_unary();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_lhs_unary.add(lhs_unary129.getTree());

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

                    RIGHT_PAREN130=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and2194); if (state.failed) return retval; 
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
                    // 832:3: -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:832:6: ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:833:4: ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:833:4: ( lhs_unary -> lhs_unary )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:833:5: lhs_unary
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and2218);
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
                    // 833:15: -> lhs_unary
                    {
                        adaptor.addChild(root_0, stream_lhs_unary.nextTree());

                    }

                    retval.tree = root_0;}
                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:834:3: ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    loop50:
                    do {
                        int alt50=2;
                        int LA50_0 = input.LA(1);

                        if ( (LA50_0==ID) ) {
                            int LA50_2 = input.LA(2);

                            if ( ((synpred4_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.AND))))) ) {
                                alt50=1;
                            }


                        }
                        else if ( (LA50_0==DOUBLE_AMPER) ) {
                            int LA50_3 = input.LA(2);

                            if ( (synpred4_DRL()) ) {
                                alt50=1;
                            }


                        }


                        switch (alt50) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:834:5: ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary
                    	    {
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:834:30: (value= and_key | amper= DOUBLE_AMPER )
                    	    int alt49=2;
                    	    int LA49_0 = input.LA(1);

                    	    if ( (LA49_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))))) {
                    	        alt49=1;
                    	    }
                    	    else if ( (LA49_0==DOUBLE_AMPER) ) {
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
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:834:31: value= and_key
                    	            {
                    	            pushFollow(FOLLOW_and_key_in_lhs_and2240);
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
                    	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:834:73: amper= DOUBLE_AMPER
                    	            {
                    	            amper=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_lhs_and2247); if (state.failed) return retval; 
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
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2258);
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
                    	    // 837:3: -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
                    	    {
                    	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:837:6: ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
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
    // $ANTLR end "lhs_and"

    public static class lhs_unary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lhs_unary"
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:840:1: lhs_unary : ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:841:2: ( ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:841:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:841:4: ( lhs_exist | {...}? => lhs_not_binding | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )
            int alt52=7;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==ID) ) {
                int LA52_1 = input.LA(2);

                if ( (((validateIdentifierKey(DroolsSoftKeywords.EXISTS)))) ) {
                    alt52=1;
                }
                else if ( (((validateNotWithBinding())&&((validateIdentifierKey(DroolsSoftKeywords.NOT))))) ) {
                    alt52=2;
                }
                else if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                    alt52=3;
                }
                else if ( (((validateIdentifierKey(DroolsSoftKeywords.EVAL)))) ) {
                    alt52=4;
                }
                else if ( (((validateIdentifierKey(DroolsSoftKeywords.FORALL)))) ) {
                    alt52=5;
                }
                else if ( (true) ) {
                    alt52=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA52_0==LEFT_PAREN) ) {
                alt52=6;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:841:6: lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary2289);
                    lhs_exist133=lhs_exist();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_exist133.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:842:4: {...}? => lhs_not_binding
                    {
                    if ( !((validateNotWithBinding())) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "lhs_unary", "validateNotWithBinding()");
                    }
                    pushFollow(FOLLOW_lhs_not_binding_in_lhs_unary2297);
                    lhs_not_binding134=lhs_not_binding();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_not_binding134.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:843:5: lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary2303);
                    lhs_not135=lhs_not();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_not135.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:844:5: lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2309);
                    lhs_eval136=lhs_eval();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_eval136.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:845:5: lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary2315);
                    lhs_forall137=lhs_forall();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_forall137.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN138=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2321); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN138, DroolsEditorType.SYMBOL); emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary2332);
                    lhs_or139=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_or139.getTree());
                    RIGHT_PAREN140=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2338); if (state.failed) return retval;
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:5: pattern_source
                    {
                    pushFollow(FOLLOW_pattern_source_in_lhs_unary2346);
                    pattern_source141=pattern_source();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, pattern_source141.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:851:3: ( ( SEMICOLON )=> SEMICOLON )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==SEMICOLON) ) {
                int LA53_1 = input.LA(2);

                if ( (synpred5_DRL()) ) {
                    alt53=1;
                }
            }
            switch (alt53) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:851:4: ( SEMICOLON )=> SEMICOLON
                    {
                    SEMICOLON142=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_lhs_unary2360); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:854:1: lhs_exist : exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:855:2: ( exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:855:4: exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_exists_key_in_lhs_exist2376);
            exists_key143=exists_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_exists_key.add(exists_key143.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:10: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt54=3;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==LEFT_PAREN) ) {
                int LA54_1 = input.LA(2);

                if ( (synpred6_DRL()) ) {
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
                int LA54_2 = input.LA(2);

                if ( (((synpred6_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.EVAL))))||(synpred6_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.EXISTS))))||synpred6_DRL()||(synpred6_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.FORALL))))||(synpred6_DRL()&&((validateIdentifierKey(DroolsSoftKeywords.NOT))))||((synpred6_DRL()&&(validateNotWithBinding()))&&((validateIdentifierKey(DroolsSoftKeywords.NOT)))))) ) {
                    alt54=1;
                }
                else if ( (true) ) {
                    alt54=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 54, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:12: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2403);
                    lhs_or144=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or144.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:858:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN145=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist2410); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN145);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN145, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2418);
                    lhs_or146=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or146.getTree());
                    RIGHT_PAREN147=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist2425); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN147);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN147, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:861:12: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist2440);
                    lhs_pattern148=lhs_pattern();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_pattern.add(lhs_pattern148.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: exists_key, lhs_pattern, RIGHT_PAREN, lhs_or
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 863:10: -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:863:13: ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_exists_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:863:26: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.nextTree());

                }
                stream_lhs_or.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:863:34: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.nextTree());

                }
                stream_lhs_pattern.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:863:47: ( RIGHT_PAREN )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:866:1: lhs_not_binding : not_key fact_binding -> ^( not_key ^( VT_PATTERN fact_binding ) ) ;
    public final DRLParser.lhs_not_binding_return lhs_not_binding() throws RecognitionException {
        DRLParser.lhs_not_binding_return retval = new DRLParser.lhs_not_binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.not_key_return not_key149 = null;

        DRLParser.fact_binding_return fact_binding150 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:867:2: ( not_key fact_binding -> ^( not_key ^( VT_PATTERN fact_binding ) ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:867:4: not_key fact_binding
            {
            pushFollow(FOLLOW_not_key_in_lhs_not_binding2486);
            not_key149=not_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_not_key.add(not_key149.getTree());
            pushFollow(FOLLOW_fact_binding_in_lhs_not_binding2488);
            fact_binding150=fact_binding();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_fact_binding.add(fact_binding150.getTree());


            // AST REWRITE
            // elements: not_key, fact_binding
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 868:2: -> ^( not_key ^( VT_PATTERN fact_binding ) )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:868:5: ^( not_key ^( VT_PATTERN fact_binding ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:868:15: ^( VT_PATTERN fact_binding )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:1: lhs_not : not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:9: ( not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:11: not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_not_key_in_lhs_not2511);
            not_key151=not_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_not_key.add(not_key151.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:3: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt55=3;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==LEFT_PAREN) ) {
                int LA55_1 = input.LA(2);

                if ( (synpred7_DRL()) ) {
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
                int LA55_2 = input.LA(2);

                if ( (synpred7_DRL()) ) {
                    alt55=1;
                }
                else if ( (true) ) {
                    alt55=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 55, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:5: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    if ( state.backtracking==0 ) {
                      	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2533);
                    lhs_or152=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or152.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN153=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2540); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN153);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN153, DroolsEditorType.SYMBOL); emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	
                    }
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2549);
                    lhs_or154=lhs_or();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or154.getTree());
                    RIGHT_PAREN155=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2555); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN155);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN155, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:877:6: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2565);
                    lhs_pattern156=lhs_pattern();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_lhs_pattern.add(lhs_pattern156.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: not_key, RIGHT_PAREN, lhs_pattern, lhs_or
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 878:10: -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:13: ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:23: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.nextTree());

                }
                stream_lhs_or.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:31: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.nextTree());

                }
                stream_lhs_pattern.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:44: ( RIGHT_PAREN )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:881:1: lhs_eval : ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) ;
    public final DRLParser.lhs_eval_return lhs_eval() throws RecognitionException {
        DRLParser.lhs_eval_return retval = new DRLParser.lhs_eval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.eval_key_return ev = null;

        DRLParser.paren_chunk_return pc = null;


        RewriteRuleSubtreeStream stream_eval_key=new RewriteRuleSubtreeStream(adaptor,"rule eval_key");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:882:2: (ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:882:4: ev= eval_key pc= paren_chunk
            {
            pushFollow(FOLLOW_eval_key_in_lhs_eval2604);
            ev=eval_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_eval_key.add(ev.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_EVAL);	
            }
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2613);
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
            // elements: paren_chunk, eval_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 891:3: -> ^( eval_key paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:891:6: ^( eval_key paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:894:1: lhs_forall : forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN ) ;
    public final DRLParser.lhs_forall_return lhs_forall() throws RecognitionException {
        DRLParser.lhs_forall_return retval = new DRLParser.lhs_forall_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN158=null;
        Token RIGHT_PAREN160=null;
        DRLParser.forall_key_return forall_key157 = null;

        DRLParser.lhs_pattern_return lhs_pattern159 = null;


        Object LEFT_PAREN158_tree=null;
        Object RIGHT_PAREN160_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_forall_key=new RewriteRuleSubtreeStream(adaptor,"rule forall_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:895:2: ( forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:895:4: forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN
            {
            pushFollow(FOLLOW_forall_key_in_lhs_forall2640);
            forall_key157=forall_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_forall_key.add(forall_key157.getTree());
            LEFT_PAREN158=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2645); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN158);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN158, DroolsEditorType.SYMBOL);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:897:4: ( lhs_pattern )+
            int cnt56=0;
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( (LA56_0==ID) ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:897:4: lhs_pattern
            	    {
            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2653);
            	    lhs_pattern159=lhs_pattern();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_lhs_pattern.add(lhs_pattern159.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt56 >= 1 ) break loop56;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(56, input);
                        throw eee;
                }
                cnt56++;
            } while (true);

            RIGHT_PAREN160=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall2659); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN160);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN160, DroolsEditorType.SYMBOL);	
            }


            // AST REWRITE
            // elements: RIGHT_PAREN, forall_key, lhs_pattern
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 899:3: -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:899:6: ^( forall_key ( lhs_pattern )+ RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_forall_key.nextNode(), root_1);

                if ( !(stream_lhs_pattern.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.nextTree());

                }
                stream_lhs_pattern.reset();
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:902:1: pattern_source : lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:905:2: ( lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:905:4: lhs_pattern ( over_clause )? ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_pattern_in_pattern_source2695);
            lhs_pattern161=lhs_pattern();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, lhs_pattern161.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:906:3: ( over_clause )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==OVER) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:906:3: over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_pattern_source2699);
                    over_clause162=over_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, over_clause162.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:907:3: ( FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==FROM) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:908:4: FROM ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    {
                    FROM163=(Token)match(input,FROM,FOLLOW_FROM_in_pattern_source2709); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FROM163_tree = (Object)adaptor.create(FROM163);
                    root_0 = (Object)adaptor.becomeRoot(FROM163_tree, root_0);
                    }
                    if ( state.backtracking==0 ) {
                      	emit(FROM163, DroolsEditorType.KEYWORD);
                      			emit(Location.LOCATION_LHS_FROM);	
                    }
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:911:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    int alt58=4;
                    switch ( input.LA(1) ) {
                    case ACCUMULATE:
                        {
                        alt58=1;
                        }
                        break;
                    case COLLECT:
                        {
                        alt58=2;
                        }
                        break;
                    case ID:
                        {
                        int LA58_3 = input.LA(2);

                        if ( (LA58_3==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT))))) {
                            alt58=3;
                        }
                        else if ( ((LA58_3>=SEMICOLON && LA58_3<=DOT)||LA58_3==END||(LA58_3>=LEFT_PAREN && LA58_3<=RIGHT_PAREN)||(LA58_3>=DOUBLE_PIPE && LA58_3<=DOUBLE_AMPER)||LA58_3==INIT||LA58_3==THEN) ) {
                            alt58=4;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 58, 3, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 58, 0, input);

                        throw nvae;
                    }

                    switch (alt58) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:911:14: accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_pattern_source2729);
                            accumulate_statement164=accumulate_statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, accumulate_statement164.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:912:15: collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_pattern_source2745);
                            collect_statement165=collect_statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, collect_statement165.getTree());

                            }
                            break;
                        case 3 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:913:15: entrypoint_statement
                            {
                            pushFollow(FOLLOW_entrypoint_statement_in_pattern_source2762);
                            entrypoint_statement166=entrypoint_statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, entrypoint_statement166.getTree());

                            }
                            break;
                        case 4 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:914:15: from_source
                            {
                            pushFollow(FOLLOW_from_source_in_pattern_source2778);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:932:1: over_clause : OVER over_elements ( COMMA over_elements )* ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:933:2: ( OVER over_elements ( COMMA over_elements )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:933:4: OVER over_elements ( COMMA over_elements )*
            {
            root_0 = (Object)adaptor.nil();

            OVER168=(Token)match(input,OVER,FOLLOW_OVER_in_over_clause2810); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            OVER168_tree = (Object)adaptor.create(OVER168);
            root_0 = (Object)adaptor.becomeRoot(OVER168_tree, root_0);
            }
            if ( state.backtracking==0 ) {
              	emit(OVER168, DroolsEditorType.KEYWORD);	
            }
            pushFollow(FOLLOW_over_elements_in_over_clause2815);
            over_elements169=over_elements();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, over_elements169.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:934:4: ( COMMA over_elements )*
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==COMMA) ) {
                    int LA60_2 = input.LA(2);

                    if ( (LA60_2==ID) ) {
                        int LA60_3 = input.LA(3);

                        if ( (LA60_3==COLON) ) {
                            alt60=1;
                        }


                    }


                }


                switch (alt60) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:934:5: COMMA over_elements
            	    {
            	    COMMA170=(Token)match(input,COMMA,FOLLOW_COMMA_in_over_clause2822); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA170, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_over_elements_in_over_clause2827);
            	    over_elements171=over_elements();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, over_elements171.getTree());

            	    }
            	    break;

            	default :
            	    break loop60;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:937:1: over_elements : id1= ID COLON id2= ID paren_chunk -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:938:2: (id1= ID COLON id2= ID paren_chunk -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:938:4: id1= ID COLON id2= ID paren_chunk
            {
            id1=(Token)match(input,ID,FOLLOW_ID_in_over_elements2842); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            if ( state.backtracking==0 ) {
              	emit(id1, DroolsEditorType.IDENTIFIER);	
            }
            COLON172=(Token)match(input,COLON,FOLLOW_COLON_in_over_elements2849); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON172);

            if ( state.backtracking==0 ) {
              	emit(COLON172, DroolsEditorType.SYMBOL);	
            }
            id2=(Token)match(input,ID,FOLLOW_ID_in_over_elements2858); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            if ( state.backtracking==0 ) {
              	emit(id2, DroolsEditorType.IDENTIFIER);	
            }
            pushFollow(FOLLOW_paren_chunk_in_over_elements2865);
            paren_chunk173=paren_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk173.getTree());


            // AST REWRITE
            // elements: id1, id2, paren_chunk
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
            // 942:2: -> ^( VT_BEHAVIOR $id1 $id2 paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:942:5: ^( VT_BEHAVIOR $id1 $id2 paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:945:1: accumulate_statement : ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:946:2: ( ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:946:4: ACCUMULATE LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN
            {
            ACCUMULATE174=(Token)match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement2891); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACCUMULATE.add(ACCUMULATE174);

            if ( state.backtracking==0 ) {
              	emit(ACCUMULATE174, DroolsEditorType.KEYWORD);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_ACCUMULATE);	
            }
            LEFT_PAREN175=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2900); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN175);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN175, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_lhs_or_in_accumulate_statement2908);
            lhs_or176=lhs_or();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_lhs_or.add(lhs_or176.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:950:3: ( COMMA )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==COMMA) ) {
                alt61=1;
            }
            switch (alt61) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:950:3: COMMA
                    {
                    COMMA177=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2913); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COMMA.add(COMMA177);


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(COMMA177, DroolsEditorType.SYMBOL);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:951:3: ( accumulate_init_clause | accumulate_id_clause )
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==INIT) ) {
                alt62=1;
            }
            else if ( (LA62_0==ID) ) {
                alt62=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 62, 0, input);

                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:951:5: accumulate_init_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_statement2923);
                    accumulate_init_clause178=accumulate_init_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accumulate_init_clause.add(accumulate_init_clause178.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:952:5: accumulate_id_clause
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_statement2929);
                    accumulate_id_clause179=accumulate_id_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accumulate_id_clause.add(accumulate_id_clause179.getTree());

                    }
                    break;

            }

            RIGHT_PAREN180=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2937); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN180);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN180, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }


            // AST REWRITE
            // elements: lhs_or, ACCUMULATE, accumulate_init_clause, RIGHT_PAREN, accumulate_id_clause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 956:3: -> ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:956:6: ^( ACCUMULATE lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ACCUMULATE.nextNode(), root_1);

                adaptor.addChild(root_1, stream_lhs_or.nextTree());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:956:26: ( accumulate_init_clause )?
                if ( stream_accumulate_init_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_accumulate_init_clause.nextTree());

                }
                stream_accumulate_init_clause.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:956:50: ( accumulate_id_clause )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:960:1: accumulate_init_clause : INIT pc1= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] (cm1= COMMA )? action_key pc2= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] (cm2= COMMA )? ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )? res1= result_key pc4= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE] -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( INIT $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) ;
    public final DRLParser.accumulate_init_clause_return accumulate_init_clause() throws RecognitionException {
        DRLParser.accumulate_init_clause_return retval = new DRLParser.accumulate_init_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token cm1=null;
        Token cm2=null;
        Token cm3=null;
        Token INIT181=null;
        DRLParser.accumulate_paren_chunk_return pc1 = null;

        DRLParser.accumulate_paren_chunk_return pc2 = null;

        DRLParser.accumulate_paren_chunk_return pc3 = null;

        DRLParser.result_key_return res1 = null;

        DRLParser.accumulate_paren_chunk_return pc4 = null;

        DRLParser.action_key_return action_key182 = null;

        DRLParser.reverse_key_return reverse_key183 = null;


        Object cm1_tree=null;
        Object cm2_tree=null;
        Object cm3_tree=null;
        Object INIT181_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_INIT=new RewriteRuleTokenStream(adaptor,"token INIT");
        RewriteRuleSubtreeStream stream_accumulate_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_paren_chunk");
        RewriteRuleSubtreeStream stream_reverse_key=new RewriteRuleSubtreeStream(adaptor,"rule reverse_key");
        RewriteRuleSubtreeStream stream_result_key=new RewriteRuleSubtreeStream(adaptor,"rule result_key");
        RewriteRuleSubtreeStream stream_action_key=new RewriteRuleSubtreeStream(adaptor,"rule action_key");
         boolean isFailed = true;	
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:2: ( INIT pc1= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] (cm1= COMMA )? action_key pc2= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] (cm2= COMMA )? ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )? res1= result_key pc4= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE] -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( INIT $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:4: INIT pc1= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] (cm1= COMMA )? action_key pc2= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] (cm2= COMMA )? ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )? res1= result_key pc4= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE]
            {
            INIT181=(Token)match(input,INIT,FOLLOW_INIT_in_accumulate_init_clause2983); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_INIT.add(INIT181);

            if ( state.backtracking==0 ) {
              	emit(INIT181, DroolsEditorType.KEYWORD);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT);	
            }
            pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause2994);
            pc1=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc1.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:965:84: (cm1= COMMA )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==COMMA) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:965:84: cm1= COMMA
                    {
                    cm1=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2999); if (state.failed) return retval; 
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
            pushFollow(FOLLOW_action_key_in_accumulate_init_clause3010);
            action_key182=action_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_action_key.add(action_key182.getTree());
            pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3014);
            pc2=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc2.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:967:97: (cm2= COMMA )?
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==COMMA) ) {
                alt64=1;
            }
            switch (alt64) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:967:97: cm2= COMMA
                    {
                    cm2=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause3019); if (state.failed) return retval; 
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:2: ( reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )? )?
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==ID) ) {
                int LA66_1 = input.LA(2);

                if ( (((validateIdentifierKey(DroolsSoftKeywords.REVERSE)))) ) {
                    alt66=1;
                }
            }
            switch (alt66) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:4: reverse_key pc3= accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] (cm3= COMMA )?
                    {
                    pushFollow(FOLLOW_reverse_key_in_accumulate_init_clause3031);
                    reverse_key183=reverse_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_reverse_key.add(reverse_key183.getTree());
                    pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3035);
                    pc3=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc3.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:100: (cm3= COMMA )?
                    int alt65=2;
                    int LA65_0 = input.LA(1);

                    if ( (LA65_0==COMMA) ) {
                        alt65=1;
                    }
                    switch (alt65) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:100: cm3= COMMA
                            {
                            cm3=(Token)match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause3040); if (state.failed) return retval; 
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
            pushFollow(FOLLOW_result_key_in_accumulate_init_clause3056);
            res1=result_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_result_key.add(res1.getTree());
            if ( state.backtracking==0 ) {
              	emit((res1!=null?((Token)res1.start):null), DroolsEditorType.KEYWORD);	
            }
            pushFollow(FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3062);
            pc4=accumulate_paren_chunk(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accumulate_paren_chunk.add(pc4.getTree());


            // AST REWRITE
            // elements: pc2, reverse_key, pc3, INIT, action_key, pc1, pc4, result_key
            // token labels: 
            // rule labels: pc2, pc4, pc3, pc1, retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_pc2=new RewriteRuleSubtreeStream(adaptor,"token pc2",pc2!=null?pc2.tree:null);
            RewriteRuleSubtreeStream stream_pc4=new RewriteRuleSubtreeStream(adaptor,"token pc4",pc4!=null?pc4.tree:null);
            RewriteRuleSubtreeStream stream_pc3=new RewriteRuleSubtreeStream(adaptor,"token pc3",pc3!=null?pc3.tree:null);
            RewriteRuleSubtreeStream stream_pc1=new RewriteRuleSubtreeStream(adaptor,"token pc1",pc1!=null?pc1.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 978:2: -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( INIT $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:5: ^( VT_ACCUMULATE_INIT_CLAUSE ^( INIT $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ACCUMULATE_INIT_CLAUSE, "VT_ACCUMULATE_INIT_CLAUSE"), root_1);

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:33: ^( INIT $pc1)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_INIT.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc1.nextTree());

                adaptor.addChild(root_1, root_2);
                }
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:46: ^( action_key $pc2)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_action_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc2.nextTree());

                adaptor.addChild(root_1, root_2);
                }
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:65: ( ^( reverse_key $pc3) )?
                if ( stream_reverse_key.hasNext()||stream_pc3.hasNext() ) {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:65: ^( reverse_key $pc3)
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_reverse_key.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_pc3.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_reverse_key.reset();
                stream_pc3.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:86: ^( result_key $pc4)
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:991:1: accumulate_paren_chunk[int locationType] : pc= accumulate_paren_chunk_data[false,$locationType] -> VT_PAREN_CHUNK[$pc.start,text] ;
    public final DRLParser.accumulate_paren_chunk_return accumulate_paren_chunk(int locationType) throws RecognitionException {
        DRLParser.accumulate_paren_chunk_return retval = new DRLParser.accumulate_paren_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.accumulate_paren_chunk_data_return pc = null;


        RewriteRuleSubtreeStream stream_accumulate_paren_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_paren_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:994:3: (pc= accumulate_paren_chunk_data[false,$locationType] -> VT_PAREN_CHUNK[$pc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:994:5: pc= accumulate_paren_chunk_data[false,$locationType]
            {
            pushFollow(FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk3120);
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
            // 995:2: -> VT_PAREN_CHUNK[$pc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:998:1: accumulate_paren_chunk_data[boolean isRecursive, int locationType] : lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )* rp1= RIGHT_PAREN ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:2: (lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )* rp1= RIGHT_PAREN )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:4: lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )* rp1= RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            lp1=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_paren_chunk_data3144); if (state.failed) return retval;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1007:4: (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | accumulate_paren_chunk_data[true,-1] )*
            loop67:
            do {
                int alt67=3;
                int LA67_0 = input.LA(1);

                if ( ((LA67_0>=VT_COMPILATION_UNIT && LA67_0<=STRING)||LA67_0==COMMA||(LA67_0>=AT && LA67_0<=MULTI_LINE_COMMENT)) ) {
                    alt67=1;
                }
                else if ( (LA67_0==LEFT_PAREN) ) {
                    alt67=2;
                }


                switch (alt67) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1007:5: any=~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=STRING)||input.LA(1)==COMMA||(input.LA(1)>=AT && input.LA(1)<=MULTI_LINE_COMMENT) ) {
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1007:87: accumulate_paren_chunk_data[true,-1]
            	    {
            	    pushFollow(FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk_data3172);
            	    accumulate_paren_chunk_data184=accumulate_paren_chunk_data(true, -1);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, accumulate_paren_chunk_data184.getTree());

            	    }
            	    break;

            	default :
            	    break loop67;
                }
            } while (true);

            rp1=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_paren_chunk_data3183); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1017:1: accumulate_id_clause : ID paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1018:2: ( ID paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1018:4: ID paren_chunk
            {
            ID185=(Token)match(input,ID,FOLLOW_ID_in_accumulate_id_clause3199); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID185);

            if ( state.backtracking==0 ) {
              	emit(ID185, DroolsEditorType.IDENTIFIER);	
            }
            pushFollow(FOLLOW_paren_chunk_in_accumulate_id_clause3205);
            paren_chunk186=paren_chunk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk186.getTree());


            // AST REWRITE
            // elements: ID, paren_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1020:2: -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1020:5: ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1023:1: collect_statement : COLLECT LEFT_PAREN pattern_source RIGHT_PAREN -> ^( COLLECT pattern_source RIGHT_PAREN ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1024:2: ( COLLECT LEFT_PAREN pattern_source RIGHT_PAREN -> ^( COLLECT pattern_source RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1024:4: COLLECT LEFT_PAREN pattern_source RIGHT_PAREN
            {
            COLLECT187=(Token)match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement3227); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLLECT.add(COLLECT187);

            if ( state.backtracking==0 ) {
              	emit(COLLECT187, DroolsEditorType.KEYWORD);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_COLLECT);	
            }
            LEFT_PAREN188=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement3236); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN188);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN188, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_pattern_source_in_collect_statement3243);
            pattern_source189=pattern_source();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_pattern_source.add(pattern_source189.getTree());
            RIGHT_PAREN190=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement3248); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN190);

            if ( state.backtracking==0 ) {
              	emit(RIGHT_PAREN190, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	
            }


            // AST REWRITE
            // elements: COLLECT, pattern_source, RIGHT_PAREN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1030:2: -> ^( COLLECT pattern_source RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1030:5: ^( COLLECT pattern_source RIGHT_PAREN )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1033:1: entrypoint_statement : entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) ;
    public final DRLParser.entrypoint_statement_return entrypoint_statement() throws RecognitionException {
        DRLParser.entrypoint_statement_return retval = new DRLParser.entrypoint_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.entry_point_key_return entry_point_key191 = null;

        DRLParser.entrypoint_id_return entrypoint_id192 = null;


        RewriteRuleSubtreeStream stream_entrypoint_id=new RewriteRuleSubtreeStream(adaptor,"rule entrypoint_id");
        RewriteRuleSubtreeStream stream_entry_point_key=new RewriteRuleSubtreeStream(adaptor,"rule entry_point_key");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1034:2: ( entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1034:4: entry_point_key entrypoint_id
            {
            pushFollow(FOLLOW_entry_point_key_in_entrypoint_statement3275);
            entry_point_key191=entry_point_key();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_entry_point_key.add(entry_point_key191.getTree());
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_FROM_COLLECT);	
            }
            pushFollow(FOLLOW_entrypoint_id_in_entrypoint_statement3283);
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
            // 1038:2: -> ^( entry_point_key entrypoint_id )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1038:5: ^( entry_point_key entrypoint_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1041:1: entrypoint_id : (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] );
    public final DRLParser.entrypoint_id_return entrypoint_id() throws RecognitionException {
        DRLParser.entrypoint_id_return retval = new DRLParser.entrypoint_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1042:2: (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] )
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==ID) ) {
                alt68=1;
            }
            else if ( (LA68_0==STRING) ) {
                alt68=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 68, 0, input);

                throw nvae;
            }
            switch (alt68) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1042:5: value= ID
                    {
                    value=(Token)match(input,ID,FOLLOW_ID_in_entrypoint_id3309); if (state.failed) return retval; 
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
                    // 1043:3: -> VT_ENTRYPOINT_ID[$value]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_ENTRYPOINT_ID, value));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1044:5: value= STRING
                    {
                    value=(Token)match(input,STRING,FOLLOW_STRING_in_entrypoint_id3326); if (state.failed) return retval; 
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
                    // 1045:3: -> VT_ENTRYPOINT_ID[$value]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1048:1: from_source : ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1049:2: ( ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1049:4: ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )?
            {
            ID193=(Token)match(input,ID,FOLLOW_ID_in_from_source3346); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID193);

            if ( state.backtracking==0 ) {
              	emit(ID193, DroolsEditorType.IDENTIFIER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1050:3: ( ( LEFT_PAREN )=>args= paren_chunk )?
            int alt69=2;
            alt69 = dfa69.predict(input);
            switch (alt69) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1050:5: ( LEFT_PAREN )=>args= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source3361);
                    args=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_paren_chunk.add(args.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1051:3: ( expression_chain )?
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==DOT) ) {
                alt70=1;
            }
            switch (alt70) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1051:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source3368);
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
            // 1057:2: -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1057:5: ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FROM_SOURCE, "VT_FROM_SOURCE"), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1057:25: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                }
                stream_paren_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1057:38: ( expression_chain )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1060:1: expression_chain : DOT ID ({...}? paren_chunk | square_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1061:2: ( DOT ID ({...}? paren_chunk | square_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1062:3: DOT ID ({...}? paren_chunk | square_chunk )? ( expression_chain )?
            {
            DOT195=(Token)match(input,DOT,FOLLOW_DOT_in_expression_chain3401); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_DOT.add(DOT195);

            if ( state.backtracking==0 ) {
              	emit(DOT195, DroolsEditorType.IDENTIFIER);	
            }
            ID196=(Token)match(input,ID,FOLLOW_ID_in_expression_chain3408); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID196);

            if ( state.backtracking==0 ) {
              	emit(ID196, DroolsEditorType.IDENTIFIER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1064:4: ({...}? paren_chunk | square_chunk )?
            int alt71=3;
            alt71 = dfa71.predict(input);
            switch (alt71) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1065:6: {...}? paren_chunk
                    {
                    if ( !((input.LA(1) == LEFT_PAREN)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "expression_chain", "input.LA(1) == LEFT_PAREN");
                    }
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain3424);
                    paren_chunk197=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_paren_chunk.add(paren_chunk197.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1067:6: square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain3438);
                    square_chunk198=square_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_square_chunk.add(square_chunk198.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1069:4: ( expression_chain )?
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==DOT) ) {
                alt72=1;
            }
            switch (alt72) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1069:4: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain3449);
                    expression_chain199=expression_chain();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression_chain.add(expression_chain199.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: paren_chunk, ID, expression_chain, square_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1070:4: -> ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1070:7: ^( VT_EXPRESSION_CHAIN[$DOT] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_EXPRESSION_CHAIN, DOT195), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1070:38: ( square_chunk )?
                if ( stream_square_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_square_chunk.nextTree());

                }
                stream_square_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1070:52: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                }
                stream_paren_chunk.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1070:65: ( expression_chain )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1073:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );
    public final DRLParser.lhs_pattern_return lhs_pattern() throws RecognitionException {
        DRLParser.lhs_pattern_return retval = new DRLParser.lhs_pattern_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.fact_binding_return fact_binding200 = null;

        DRLParser.fact_return fact201 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1074:2: ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==ID) ) {
                int LA73_1 = input.LA(2);

                if ( (LA73_1==DOT||LA73_1==LEFT_PAREN||LA73_1==LEFT_SQUARE) ) {
                    alt73=2;
                }
                else if ( (LA73_1==COLON) ) {
                    alt73=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 73, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1074:4: fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern3482);
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
                    // 1074:17: -> ^( VT_PATTERN fact_binding )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1074:20: ^( VT_PATTERN fact_binding )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1075:4: fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern3495);
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
                    // 1075:9: -> ^( VT_PATTERN fact )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1075:12: ^( VT_PATTERN fact )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1078:1: fact_binding : label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1079:3: ( label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1079:5: label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            {
            pushFollow(FOLLOW_label_in_fact_binding3515);
            label202=label();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_label.add(label202.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1080:3: ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==ID) ) {
                alt74=1;
            }
            else if ( (LA74_0==LEFT_PAREN) ) {
                alt74=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 74, 0, input);

                throw nvae;
            }
            switch (alt74) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1080:5: fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding3521);
                    fact203=fact();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fact.add(fact203.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1081:6: LEFT_PAREN fact_binding_expression RIGHT_PAREN
                    {
                    LEFT_PAREN204=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding3528); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN204);

                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN204, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_fact_binding_expression_in_fact_binding3536);
                    fact_binding_expression205=fact_binding_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fact_binding_expression.add(fact_binding_expression205.getTree());
                    RIGHT_PAREN206=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding3544); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN206);

                    if ( state.backtracking==0 ) {
                      	emit(RIGHT_PAREN206, DroolsEditorType.SYMBOL);	
                    }

                    }
                    break;

            }



            // AST REWRITE
            // elements: fact, label, fact_binding_expression, RIGHT_PAREN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1085:3: -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1085:6: ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FACT_BINDING, "VT_FACT_BINDING"), root_1);

                adaptor.addChild(root_1, stream_label.nextTree());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1085:30: ( fact )?
                if ( stream_fact.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact.nextTree());

                }
                stream_fact.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1085:36: ( fact_binding_expression )?
                if ( stream_fact_binding_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact_binding_expression.nextTree());

                }
                stream_fact_binding_expression.reset();
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1085:61: ( RIGHT_PAREN )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1088:1: fact_binding_expression : ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1091:3: ( ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1091:5: ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1091:5: ( fact -> fact )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1091:6: fact
            {
            pushFollow(FOLLOW_fact_in_fact_binding_expression3585);
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
            // 1091:11: -> fact
            {
                adaptor.addChild(root_0, stream_fact.nextTree());

            }

            retval.tree = root_0;}
            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1091:20: ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                    alt76=1;
                }
                else if ( (LA76_0==DOUBLE_PIPE) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1091:22: (value= or_key | pipe= DOUBLE_PIPE ) fact
            	    {
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1091:22: (value= or_key | pipe= DOUBLE_PIPE )
            	    int alt75=2;
            	    int LA75_0 = input.LA(1);

            	    if ( (LA75_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            	        alt75=1;
            	    }
            	    else if ( (LA75_0==DOUBLE_PIPE) ) {
            	        alt75=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 75, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt75) {
            	        case 1 :
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1091:23: value= or_key
            	            {
            	            pushFollow(FOLLOW_or_key_in_fact_binding_expression3597);
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
            	            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1091:62: pipe= DOUBLE_PIPE
            	            {
            	            pipe=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3603); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

            	            if ( state.backtracking==0 ) {
            	              orToken = pipe;
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_fact_in_fact_binding_expression3608);
            	    fact208=fact();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_fact.add(fact208.getTree());


            	    // AST REWRITE
            	    // elements: fact, fact_binding_expression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 1092:3: -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
            	    {
            	        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1092:6: ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
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
            	    break loop76;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1095:1: fact : pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1098:2: ( pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1098:4: pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN
            {
            pushFollow(FOLLOW_pattern_type_in_fact3648);
            pattern_type209=pattern_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_pattern_type.add(pattern_type209.getTree());
            LEFT_PAREN210=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact3653); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN210);

            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN210, DroolsEditorType.SYMBOL);	
            }
            if ( state.backtracking==0 ) {
              	emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1101:4: ( constraints )?
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==ID||LA77_0==LEFT_PAREN) ) {
                alt77=1;
            }
            switch (alt77) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1101:4: constraints
                    {
                    pushFollow(FOLLOW_constraints_in_fact3664);
                    constraints211=constraints();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_constraints.add(constraints211.getTree());

                    }
                    break;

            }

            RIGHT_PAREN212=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact3670); if (state.failed) return retval; 
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
            // elements: pattern_type, RIGHT_PAREN, constraints
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1107:2: -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1107:5: ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FACT, "VT_FACT"), root_1);

                adaptor.addChild(root_1, stream_pattern_type.nextTree());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1107:28: ( constraints )?
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1117:1: constraints : constraint ( COMMA constraint )* ;
    public final DRLParser.constraints_return constraints() throws RecognitionException {
        DRLParser.constraints_return retval = new DRLParser.constraints_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA214=null;
        DRLParser.constraint_return constraint213 = null;

        DRLParser.constraint_return constraint215 = null;


        Object COMMA214_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1118:2: ( constraint ( COMMA constraint )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1118:4: constraint ( COMMA constraint )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_in_constraints3704);
            constraint213=constraint();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint213.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1118:15: ( COMMA constraint )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==COMMA) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1118:17: COMMA constraint
            	    {
            	    COMMA214=(Token)match(input,COMMA,FOLLOW_COMMA_in_constraints3708); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA214, DroolsEditorType.SYMBOL);
            	      		emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	
            	    }
            	    pushFollow(FOLLOW_constraint_in_constraints3715);
            	    constraint215=constraint();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint215.getTree());

            	    }
            	    break;

            	default :
            	    break loop78;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1123:1: constraint : or_constr ;
    public final DRLParser.constraint_return constraint() throws RecognitionException {
        DRLParser.constraint_return retval = new DRLParser.constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.or_constr_return or_constr216 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1124:2: ( or_constr )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1124:4: or_constr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_or_constr_in_constraint3729);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1127:1: or_constr : and_constr ( DOUBLE_PIPE and_constr )* ;
    public final DRLParser.or_constr_return or_constr() throws RecognitionException {
        DRLParser.or_constr_return retval = new DRLParser.or_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE218=null;
        DRLParser.and_constr_return and_constr217 = null;

        DRLParser.and_constr_return and_constr219 = null;


        Object DOUBLE_PIPE218_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1128:2: ( and_constr ( DOUBLE_PIPE and_constr )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1128:4: and_constr ( DOUBLE_PIPE and_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_constr_in_or_constr3740);
            and_constr217=and_constr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, and_constr217.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1128:15: ( DOUBLE_PIPE and_constr )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==DOUBLE_PIPE) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1128:17: DOUBLE_PIPE and_constr
            	    {
            	    DOUBLE_PIPE218=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr3744); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_PIPE218_tree = (Object)adaptor.create(DOUBLE_PIPE218);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE218_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_PIPE218, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr3751);
            	    and_constr219=and_constr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, and_constr219.getTree());

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
    // $ANTLR end "or_constr"

    public static class and_constr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "and_constr"
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1132:1: and_constr : unary_constr ( DOUBLE_AMPER unary_constr )* ;
    public final DRLParser.and_constr_return and_constr() throws RecognitionException {
        DRLParser.and_constr_return retval = new DRLParser.and_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER221=null;
        DRLParser.unary_constr_return unary_constr220 = null;

        DRLParser.unary_constr_return unary_constr222 = null;


        Object DOUBLE_AMPER221_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1133:2: ( unary_constr ( DOUBLE_AMPER unary_constr )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1133:4: unary_constr ( DOUBLE_AMPER unary_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_constr_in_and_constr3766);
            unary_constr220=unary_constr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unary_constr220.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1133:17: ( DOUBLE_AMPER unary_constr )*
            loop80:
            do {
                int alt80=2;
                int LA80_0 = input.LA(1);

                if ( (LA80_0==DOUBLE_AMPER) ) {
                    alt80=1;
                }


                switch (alt80) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1133:19: DOUBLE_AMPER unary_constr
            	    {
            	    DOUBLE_AMPER221=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr3770); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_AMPER221_tree = (Object)adaptor.create(DOUBLE_AMPER221);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER221_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_AMPER221, DroolsEditorType.SYMBOL);;	
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr3777);
            	    unary_constr222=unary_constr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unary_constr222.getTree());

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
    // $ANTLR end "and_constr"

    public static class unary_constr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unary_constr"
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1137:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1141:2: ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN )
            int alt81=3;
            alt81 = dfa81.predict(input);
            switch (alt81) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1141:4: eval_key paren_chunk
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_eval_key_in_unary_constr3810);
                    eval_key223=eval_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(eval_key223.getTree(), root_0);
                    pushFollow(FOLLOW_paren_chunk_in_unary_constr3813);
                    paren_chunk224=paren_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk224.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1142:4: field_constraint
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_field_constraint_in_unary_constr3818);
                    field_constraint225=field_constraint();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, field_constraint225.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1143:5: LEFT_PAREN or_constr RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN226=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr3824); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN226, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_or_constr_in_unary_constr3834);
                    or_constr227=or_constr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, or_constr227.getTree());
                    RIGHT_PAREN228=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr3839); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1156:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1159:3: ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) )
            int alt83=2;
            int LA83_0 = input.LA(1);

            if ( (LA83_0==ID) ) {
                int LA83_1 = input.LA(2);

                if ( ((LA83_1>=ID && LA83_1<=DOT)||LA83_1==LEFT_PAREN||(LA83_1>=EQUAL && LA83_1<=NOT_EQUAL)||LA83_1==LEFT_SQUARE) ) {
                    alt83=2;
                }
                else if ( (LA83_1==COLON) ) {
                    alt83=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 83, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 83, 0, input);

                throw nvae;
            }
            switch (alt83) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1159:5: label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )?
                    {
                    pushFollow(FOLLOW_label_in_field_constraint3859);
                    label229=label();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_label.add(label229.getTree());
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3861);
                    accessor_path230=accessor_path();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accessor_path.add(accessor_path230.getTree());
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1160:3: ( or_restr_connective | arw= ARROW paren_chunk )?
                    int alt82=3;
                    int LA82_0 = input.LA(1);

                    if ( (LA82_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {
                        alt82=1;
                    }
                    else if ( (LA82_0==LEFT_PAREN||(LA82_0>=EQUAL && LA82_0<=NOT_EQUAL)) ) {
                        alt82=1;
                    }
                    else if ( (LA82_0==ARROW) ) {
                        alt82=2;
                    }
                    switch (alt82) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1160:5: or_restr_connective
                            {
                            pushFollow(FOLLOW_or_restr_connective_in_field_constraint3868);
                            or_restr_connective231=or_restr_connective();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_or_restr_connective.add(or_restr_connective231.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1160:27: arw= ARROW paren_chunk
                            {
                            arw=(Token)match(input,ARROW,FOLLOW_ARROW_in_field_constraint3874); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ARROW.add(arw);

                            if ( state.backtracking==0 ) {
                              	emit(arw, DroolsEditorType.SYMBOL);	
                            }
                            pushFollow(FOLLOW_paren_chunk_in_field_constraint3878);
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
                    // elements: paren_chunk, label, accessor_path, accessor_path, or_restr_connective, label
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 1161:3: -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )?
                    if (isArrow) {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1161:17: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.nextTree());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1161:39: ^( VT_FIELD accessor_path )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1161:66: ( ^( VK_EVAL[$arw] paren_chunk ) )?
                        if ( stream_paren_chunk.hasNext() ) {
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1161:66: ^( VK_EVAL[$arw] paren_chunk )
                            {
                            Object root_1 = (Object)adaptor.nil();
                            root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VK_EVAL, arw), root_1);

                            adaptor.addChild(root_1, stream_paren_chunk.nextTree());

                            adaptor.addChild(root_0, root_1);
                            }

                        }
                        stream_paren_chunk.reset();

                    }
                    else // 1162:3: -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1162:6: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.nextTree());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1162:28: ^( VT_FIELD accessor_path ( or_restr_connective )? )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.nextTree());
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1162:53: ( or_restr_connective )?
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1163:4: accessor_path or_restr_connective
                    {
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3932);
                    accessor_path233=accessor_path();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_accessor_path.add(accessor_path233.getTree());
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint3934);
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
                    // 1164:3: -> ^( VT_FIELD accessor_path or_restr_connective )
                    {
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1164:6: ^( VT_FIELD accessor_path or_restr_connective )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1167:1: label : value= ID COLON -> VT_LABEL[$value] ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1168:2: (value= ID COLON -> VT_LABEL[$value] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1168:4: value= ID COLON
            {
            value=(Token)match(input,ID,FOLLOW_ID_in_label3959); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(value);

            if ( state.backtracking==0 ) {
              	emit(value, DroolsEditorType.IDENTIFIER_VARIABLE);	
            }
            COLON235=(Token)match(input,COLON,FOLLOW_COLON_in_label3966); if (state.failed) return retval; 
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
            // 1170:3: -> VT_LABEL[$value]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1173:1: or_restr_connective : and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* ;
    public final DRLParser.or_restr_connective_return or_restr_connective() throws RecognitionException {
        DRLParser.or_restr_connective_return retval = new DRLParser.or_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE237=null;
        DRLParser.and_restr_connective_return and_restr_connective236 = null;

        DRLParser.and_restr_connective_return and_restr_connective238 = null;


        Object DOUBLE_PIPE237_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1174:2: ( and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1174:4: and_restr_connective ({...}? => DOUBLE_PIPE and_restr_connective )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3987);
            and_restr_connective236=and_restr_connective();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, and_restr_connective236.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1174:25: ({...}? => DOUBLE_PIPE and_restr_connective )*
            loop84:
            do {
                int alt84=2;
                int LA84_0 = input.LA(1);

                if ( (LA84_0==DOUBLE_PIPE) ) {
                    int LA84_2 = input.LA(2);

                    if ( (((validateRestr()))) ) {
                        alt84=1;
                    }


                }


                switch (alt84) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1174:26: {...}? => DOUBLE_PIPE and_restr_connective
            	    {
            	    if ( !(((validateRestr()))) ) {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        throw new FailedPredicateException(input, "or_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_PIPE237=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective3993); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_PIPE237_tree = (Object)adaptor.create(DOUBLE_PIPE237);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE237_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_PIPE237, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective4001);
            	    and_restr_connective238=and_restr_connective();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, and_restr_connective238.getTree());

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1187:1: and_restr_connective : constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* ;
    public final DRLParser.and_restr_connective_return and_restr_connective() throws RecognitionException {
        DRLParser.and_restr_connective_return retval = new DRLParser.and_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER240=null;
        DRLParser.constraint_expression_return constraint_expression239 = null;

        DRLParser.constraint_expression_return constraint_expression241 = null;


        Object DOUBLE_AMPER240_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1188:2: ( constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1188:4: constraint_expression ({...}? => DOUBLE_AMPER constraint_expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective4022);
            constraint_expression239=constraint_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint_expression239.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1188:26: ({...}? => DOUBLE_AMPER constraint_expression )*
            loop85:
            do {
                int alt85=2;
                int LA85_0 = input.LA(1);

                if ( (LA85_0==DOUBLE_AMPER) ) {
                    int LA85_2 = input.LA(2);

                    if ( (((validateRestr()))) ) {
                        alt85=1;
                    }


                }


                switch (alt85) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1188:27: {...}? => DOUBLE_AMPER constraint_expression
            	    {
            	    if ( !(((validateRestr()))) ) {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        throw new FailedPredicateException(input, "and_restr_connective", "(validateRestr())");
            	    }
            	    DOUBLE_AMPER240=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective4028); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_AMPER240_tree = (Object)adaptor.create(DOUBLE_AMPER240);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER240_tree, root_0);
            	    }
            	    if ( state.backtracking==0 ) {
            	      	emit(DOUBLE_AMPER240, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective4035);
            	    constraint_expression241=constraint_expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, constraint_expression241.getTree());

            	    }
            	    break;

            	default :
            	    break loop85;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1201:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1204:3: ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN )
            int alt86=3;
            alt86 = dfa86.predict(input);
            switch (alt86) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1204:5: compound_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_compound_operator_in_constraint_expression4063);
                    compound_operator242=compound_operator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, compound_operator242.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1205:4: simple_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_simple_operator_in_constraint_expression4068);
                    simple_operator243=simple_operator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_operator243.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1206:4: LEFT_PAREN or_restr_connective RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN244=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression4073); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	emit(LEFT_PAREN244, DroolsEditorType.SYMBOL);	
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression4082);
                    or_restr_connective245=or_restr_connective();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, or_restr_connective245.getTree());
                    RIGHT_PAREN246=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression4087); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1252:1: simple_operator : ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) ) expression_value ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1254:2: ( ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) ) expression_value )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1255:2: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) ) expression_value
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1255:2: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | ( not_key )? ( operator_key ( square_chunk )? ) )
            int alt89=7;
            int LA89_0 = input.LA(1);

            if ( (LA89_0==EQUAL) ) {
                alt89=1;
            }
            else if ( (LA89_0==GREATER) ) {
                alt89=2;
            }
            else if ( (LA89_0==GREATER_EQUAL) ) {
                alt89=3;
            }
            else if ( (LA89_0==LESS) ) {
                alt89=4;
            }
            else if ( (LA89_0==LESS_EQUAL) ) {
                alt89=5;
            }
            else if ( (LA89_0==NOT_EQUAL) ) {
                alt89=6;
            }
            else if ( (LA89_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {
                alt89=7;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 89, 0, input);

                throw nvae;
            }
            switch (alt89) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1256:3: EQUAL
                    {
                    EQUAL247=(Token)match(input,EQUAL,FOLLOW_EQUAL_in_simple_operator4122); if (state.failed) return retval;
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1257:4: GREATER
                    {
                    GREATER248=(Token)match(input,GREATER,FOLLOW_GREATER_in_simple_operator4130); if (state.failed) return retval;
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1258:4: GREATER_EQUAL
                    {
                    GREATER_EQUAL249=(Token)match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_simple_operator4138); if (state.failed) return retval;
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1259:4: LESS
                    {
                    LESS250=(Token)match(input,LESS,FOLLOW_LESS_in_simple_operator4146); if (state.failed) return retval;
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1260:4: LESS_EQUAL
                    {
                    LESS_EQUAL251=(Token)match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_simple_operator4154); if (state.failed) return retval;
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1261:4: NOT_EQUAL
                    {
                    NOT_EQUAL252=(Token)match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_simple_operator4162); if (state.failed) return retval;
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1262:4: ( not_key )? ( operator_key ( square_chunk )? )
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1262:4: ( not_key )?
                    int alt87=2;
                    int LA87_0 = input.LA(1);

                    if ( (LA87_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {
                        int LA87_1 = input.LA(2);

                        if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                            alt87=1;
                        }
                    }
                    switch (alt87) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1262:4: not_key
                            {
                            pushFollow(FOLLOW_not_key_in_simple_operator4170);
                            not_key253=not_key();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, not_key253.getTree());

                            }
                            break;

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:3: ( operator_key ( square_chunk )? )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:5: operator_key ( square_chunk )?
                    {
                    pushFollow(FOLLOW_operator_key_in_simple_operator4177);
                    operator_key254=operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(operator_key254.getTree(), root_0);
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:19: ( square_chunk )?
                    int alt88=2;
                    int LA88_0 = input.LA(1);

                    if ( (LA88_0==LEFT_SQUARE) ) {
                        alt88=1;
                    }
                    switch (alt88) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:19: square_chunk
                            {
                            pushFollow(FOLLOW_square_chunk_in_simple_operator4180);
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
            pushFollow(FOLLOW_expression_value_in_simple_operator4192);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1270:1: compound_operator : ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1272:2: ( ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1273:2: ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1273:2: ( in_key | not_key in_key )
            int alt90=2;
            int LA90_0 = input.LA(1);

            if ( (LA90_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((validateIdentifierKey(DroolsSoftKeywords.IN)))))) {
                int LA90_1 = input.LA(2);

                if ( (LA90_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IN))))) {
                    alt90=1;
                }
                else if ( (LA90_1==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))))) {
                    alt90=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 90, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 90, 0, input);

                throw nvae;
            }
            switch (alt90) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1273:4: in_key
                    {
                    pushFollow(FOLLOW_in_key_in_compound_operator4214);
                    in_key257=in_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key257.getTree(), root_0);

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1273:14: not_key in_key
                    {
                    pushFollow(FOLLOW_not_key_in_compound_operator4219);
                    not_key258=not_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, not_key258.getTree());
                    pushFollow(FOLLOW_in_key_in_compound_operator4221);
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
            LEFT_PAREN260=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator4232); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
              	emit(LEFT_PAREN260, DroolsEditorType.SYMBOL);	
            }
            pushFollow(FOLLOW_expression_value_in_compound_operator4240);
            expression_value261=expression_value();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression_value261.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1276:21: ( COMMA expression_value )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==COMMA) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1276:23: COMMA expression_value
            	    {
            	    COMMA262=(Token)match(input,COMMA,FOLLOW_COMMA_in_compound_operator4244); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	      	emit(COMMA262, DroolsEditorType.SYMBOL);	
            	    }
            	    pushFollow(FOLLOW_expression_value_in_compound_operator4249);
            	    expression_value263=expression_value();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression_value263.getTree());

            	    }
            	    break;

            	default :
            	    break loop91;
                }
            } while (true);

            RIGHT_PAREN264=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator4257); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1287:1: operator_key : {...}? =>id= ID -> VK_OPERATOR[$id] ;
    public final DRLParser.operator_key_return operator_key() throws RecognitionException {
        DRLParser.operator_key_return retval = new DRLParser.operator_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1288:2: ({...}? =>id= ID -> VK_OPERATOR[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1288:9: {...}? =>id= ID
            {
            if ( !(((isPluggableEvaluator(false)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "operator_key", "(isPluggableEvaluator(false))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_operator_key4288); if (state.failed) return retval; 
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
            // 1290:9: -> VK_OPERATOR[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1293:1: neg_operator_key : {...}? =>id= ID -> VK_OPERATOR[$id] ;
    public final DRLParser.neg_operator_key_return neg_operator_key() throws RecognitionException {
        DRLParser.neg_operator_key_return retval = new DRLParser.neg_operator_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1294:2: ({...}? =>id= ID -> VK_OPERATOR[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1294:9: {...}? =>id= ID
            {
            if ( !(((isPluggableEvaluator(true)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "neg_operator_key", "(isPluggableEvaluator(true))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key4333); if (state.failed) return retval; 
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
            // 1296:9: -> VK_OPERATOR[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1299:1: expression_value : ( accessor_path | literal_constraint | paren_chunk ) ;
    public final DRLParser.expression_value_return expression_value() throws RecognitionException {
        DRLParser.expression_value_return retval = new DRLParser.expression_value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.accessor_path_return accessor_path265 = null;

        DRLParser.literal_constraint_return literal_constraint266 = null;

        DRLParser.paren_chunk_return paren_chunk267 = null;



        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1300:2: ( ( accessor_path | literal_constraint | paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1300:4: ( accessor_path | literal_constraint | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1300:4: ( accessor_path | literal_constraint | paren_chunk )
            int alt92=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt92=1;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt92=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt92=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 92, 0, input);

                throw nvae;
            }

            switch (alt92) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1300:5: accessor_path
                    {
                    pushFollow(FOLLOW_accessor_path_in_expression_value4370);
                    accessor_path265=accessor_path();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, accessor_path265.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1301:4: literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_expression_value4375);
                    literal_constraint266=literal_constraint();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal_constraint266.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1302:4: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_value4381);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1316:1: literal_constraint : ( STRING | INT | FLOAT | BOOL | NULL );
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1317:2: ( STRING | INT | FLOAT | BOOL | NULL )
            int alt93=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt93=1;
                }
                break;
            case INT:
                {
                alt93=2;
                }
                break;
            case FLOAT:
                {
                alt93=3;
                }
                break;
            case BOOL:
                {
                alt93=4;
                }
                break;
            case NULL:
                {
                alt93=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }

            switch (alt93) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1317:4: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING268=(Token)match(input,STRING,FOLLOW_STRING_in_literal_constraint4400); if (state.failed) return retval;
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1318:4: INT
                    {
                    root_0 = (Object)adaptor.nil();

                    INT269=(Token)match(input,INT,FOLLOW_INT_in_literal_constraint4407); if (state.failed) return retval;
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1319:4: FLOAT
                    {
                    root_0 = (Object)adaptor.nil();

                    FLOAT270=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint4414); if (state.failed) return retval;
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1320:4: BOOL
                    {
                    root_0 = (Object)adaptor.nil();

                    BOOL271=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal_constraint4421); if (state.failed) return retval;
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1321:4: NULL
                    {
                    root_0 = (Object)adaptor.nil();

                    NULL272=(Token)match(input,NULL,FOLLOW_NULL_in_literal_constraint4428); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1324:1: pattern_type : id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1325:2: (id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1325:4: id+= ID (id+= DOT id+= ID )* ( dimension_definition )*
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_pattern_type4443); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1325:11: (id+= DOT id+= ID )*
            loop94:
            do {
                int alt94=2;
                int LA94_0 = input.LA(1);

                if ( (LA94_0==DOT) ) {
                    alt94=1;
                }


                switch (alt94) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1325:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_pattern_type4449); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_pattern_type4453); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop94;
                }
            } while (true);

            if ( state.backtracking==0 ) {
              	emit(list_id, DroolsEditorType.IDENTIFIER);
              		setParaphrasesValue(DroolsParaphraseTypes.PATTERN, buildStringFromTokens(list_id));	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1328:6: ( dimension_definition )*
            loop95:
            do {
                int alt95=2;
                int LA95_0 = input.LA(1);

                if ( (LA95_0==LEFT_SQUARE) ) {
                    alt95=1;
                }


                switch (alt95) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1328:6: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_pattern_type4468);
            	    dimension_definition273=dimension_definition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_dimension_definition.add(dimension_definition273.getTree());

            	    }
            	    break;

            	default :
            	    break loop95;
                }
            } while (true);



            // AST REWRITE
            // elements: dimension_definition, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1329:3: -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1329:6: ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
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
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1329:28: ( dimension_definition )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1332:1: data_type : id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1333:2: (id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1333:4: id+= ID (id+= DOT id+= ID )* ( dimension_definition )*
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_data_type4496); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1333:11: (id+= DOT id+= ID )*
            loop96:
            do {
                int alt96=2;
                int LA96_0 = input.LA(1);

                if ( (LA96_0==DOT) ) {
                    alt96=1;
                }


                switch (alt96) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1333:13: id+= DOT id+= ID
            	    {
            	    id=(Token)match(input,DOT,FOLLOW_DOT_in_data_type4502); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_data_type4506); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop96;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1333:31: ( dimension_definition )*
            loop97:
            do {
                int alt97=2;
                int LA97_0 = input.LA(1);

                if ( (LA97_0==LEFT_SQUARE) ) {
                    alt97=1;
                }


                switch (alt97) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1333:31: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_data_type4511);
            	    dimension_definition274=dimension_definition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_dimension_definition.add(dimension_definition274.getTree());

            	    }
            	    break;

            	default :
            	    break loop97;
                }
            } while (true);

            if ( state.backtracking==0 ) {
              	emit(list_id, DroolsEditorType.IDENTIFIER);	
            }


            // AST REWRITE
            // elements: dimension_definition, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1335:3: -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1335:6: ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
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
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1335:25: ( dimension_definition )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1338:1: dimension_definition : LEFT_SQUARE RIGHT_SQUARE ;
    public final DRLParser.dimension_definition_return dimension_definition() throws RecognitionException {
        DRLParser.dimension_definition_return retval = new DRLParser.dimension_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE275=null;
        Token RIGHT_SQUARE276=null;

        Object LEFT_SQUARE275_tree=null;
        Object RIGHT_SQUARE276_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1339:2: ( LEFT_SQUARE RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1339:4: LEFT_SQUARE RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE275=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dimension_definition4540); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_SQUARE275_tree = (Object)adaptor.create(LEFT_SQUARE275);
            adaptor.addChild(root_0, LEFT_SQUARE275_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(LEFT_SQUARE275, DroolsEditorType.SYMBOL);	
            }
            RIGHT_SQUARE276=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dimension_definition4547); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1343:1: accessor_path : accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1344:2: ( accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1344:4: accessor_element ( DOT accessor_element )*
            {
            pushFollow(FOLLOW_accessor_element_in_accessor_path4561);
            accessor_element277=accessor_element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_accessor_element.add(accessor_element277.getTree());
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1344:21: ( DOT accessor_element )*
            loop98:
            do {
                int alt98=2;
                int LA98_0 = input.LA(1);

                if ( (LA98_0==DOT) ) {
                    alt98=1;
                }


                switch (alt98) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1344:23: DOT accessor_element
            	    {
            	    DOT278=(Token)match(input,DOT,FOLLOW_DOT_in_accessor_path4565); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT278);

            	    if ( state.backtracking==0 ) {
            	      	emit(DOT278, DroolsEditorType.IDENTIFIER);	
            	    }
            	    pushFollow(FOLLOW_accessor_element_in_accessor_path4569);
            	    accessor_element279=accessor_element();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_accessor_element.add(accessor_element279.getTree());

            	    }
            	    break;

            	default :
            	    break loop98;
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
            // 1345:2: -> ^( VT_ACCESSOR_PATH ( accessor_element )+ )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1345:5: ^( VT_ACCESSOR_PATH ( accessor_element )+ )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1348:1: accessor_element : ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1349:2: ( ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1349:4: ID ( square_chunk )*
            {
            ID280=(Token)match(input,ID,FOLLOW_ID_in_accessor_element4593); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID280);

            if ( state.backtracking==0 ) {
              	emit(ID280, DroolsEditorType.IDENTIFIER);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1350:3: ( square_chunk )*
            loop99:
            do {
                int alt99=2;
                int LA99_0 = input.LA(1);

                if ( (LA99_0==LEFT_SQUARE) ) {
                    alt99=1;
                }


                switch (alt99) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1350:3: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_accessor_element4599);
            	    square_chunk281=square_chunk();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_square_chunk.add(square_chunk281.getTree());

            	    }
            	    break;

            	default :
            	    break loop99;
                }
            } while (true);



            // AST REWRITE
            // elements: ID, square_chunk
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 1351:2: -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
            {
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1351:5: ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ACCESSOR_ELEMENT, "VT_ACCESSOR_ELEMENT"), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1351:30: ( square_chunk )*
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1354:1: rhs_chunk : rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] ;
    public final DRLParser.rhs_chunk_return rhs_chunk() throws RecognitionException {
        DRLParser.rhs_chunk_return retval = new DRLParser.rhs_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.rhs_chunk_data_return rc = null;


        RewriteRuleSubtreeStream stream_rhs_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1357:3: (rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1357:5: rc= rhs_chunk_data
            {
            pushFollow(FOLLOW_rhs_chunk_data_in_rhs_chunk4628);
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
            // 1358:2: -> VT_RHS_CHUNK[$rc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1361:1: rhs_chunk_data : THEN (any=~ END )* end1= END ( SEMICOLON )? ;
    public final DRLParser.rhs_chunk_data_return rhs_chunk_data() throws RecognitionException {
        DRLParser.rhs_chunk_data_return retval = new DRLParser.rhs_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token any=null;
        Token end1=null;
        Token THEN282=null;
        Token SEMICOLON283=null;

        Object any_tree=null;
        Object end1_tree=null;
        Object THEN282_tree=null;
        Object SEMICOLON283_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1362:2: ( THEN (any=~ END )* end1= END ( SEMICOLON )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1362:4: THEN (any=~ END )* end1= END ( SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            THEN282=(Token)match(input,THEN,FOLLOW_THEN_in_rhs_chunk_data4647); if (state.failed) return retval;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1367:4: (any=~ END )*
            loop100:
            do {
                int alt100=2;
                int LA100_0 = input.LA(1);

                if ( ((LA100_0>=VT_COMPILATION_UNIT && LA100_0<=DOT_STAR)||(LA100_0>=STRING && LA100_0<=MULTI_LINE_COMMENT)) ) {
                    alt100=1;
                }


                switch (alt100) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1367:6: any=~ END
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=DOT_STAR)||(input.LA(1)>=STRING && input.LA(1)<=MULTI_LINE_COMMENT) ) {
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

            	default :
            	    break loop100;
                }
            } while (true);

            end1=(Token)match(input,END,FOLLOW_END_in_rhs_chunk_data4673); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            end1_tree = (Object)adaptor.create(end1);
            adaptor.addChild(root_0, end1_tree);
            }
            if ( state.backtracking==0 ) {
              	emit(end1, DroolsEditorType.KEYWORD);	
            }
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1369:3: ( SEMICOLON )?
            int alt101=2;
            int LA101_0 = input.LA(1);

            if ( (LA101_0==SEMICOLON) ) {
                alt101=1;
            }
            switch (alt101) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1369:3: SEMICOLON
                    {
                    SEMICOLON283=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_rhs_chunk_data4679); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMICOLON283_tree = (Object)adaptor.create(SEMICOLON283);
                    adaptor.addChild(root_0, SEMICOLON283_tree);
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              	emit(SEMICOLON283, DroolsEditorType.KEYWORD);	
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1372:1: curly_chunk : cc= curly_chunk_data[false] -> VT_CURLY_CHUNK[$cc.start,text] ;
    public final DRLParser.curly_chunk_return curly_chunk() throws RecognitionException {
        DRLParser.curly_chunk_return retval = new DRLParser.curly_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.curly_chunk_data_return cc = null;


        RewriteRuleSubtreeStream stream_curly_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule curly_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1375:3: (cc= curly_chunk_data[false] -> VT_CURLY_CHUNK[$cc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1375:5: cc= curly_chunk_data[false]
            {
            pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk4698);
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
            // 1376:2: -> VT_CURLY_CHUNK[$cc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1379:1: curly_chunk_data[boolean isRecursive] : lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY ;
    public final DRLParser.curly_chunk_data_return curly_chunk_data(boolean isRecursive) throws RecognitionException {
        DRLParser.curly_chunk_data_return retval = new DRLParser.curly_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lc1=null;
        Token any=null;
        Token rc1=null;
        DRLParser.curly_chunk_data_return curly_chunk_data284 = null;


        Object lc1_tree=null;
        Object any_tree=null;
        Object rc1_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1380:2: (lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1380:4: lc1= LEFT_CURLY (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )* rc1= RIGHT_CURLY
            {
            root_0 = (Object)adaptor.nil();

            lc1=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk_data4721); if (state.failed) return retval;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1387:4: (any=~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data[true] )*
            loop102:
            do {
                int alt102=3;
                int LA102_0 = input.LA(1);

                if ( ((LA102_0>=VT_COMPILATION_UNIT && LA102_0<=THEN)||(LA102_0>=MISC && LA102_0<=MULTI_LINE_COMMENT)) ) {
                    alt102=1;
                }
                else if ( (LA102_0==LEFT_CURLY) ) {
                    alt102=2;
                }


                switch (alt102) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1387:5: any=~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=THEN)||(input.LA(1)>=MISC && input.LA(1)<=MULTI_LINE_COMMENT) ) {
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1387:87: curly_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk_data4749);
            	    curly_chunk_data284=curly_chunk_data(true);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, curly_chunk_data284.getTree());

            	    }
            	    break;

            	default :
            	    break loop102;
                }
            } while (true);

            rc1=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk_data4760); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1397:1: paren_chunk : pc= paren_chunk_data[false] -> VT_PAREN_CHUNK[$pc.start,text] ;
    public final DRLParser.paren_chunk_return paren_chunk() throws RecognitionException {
        DRLParser.paren_chunk_return retval = new DRLParser.paren_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.paren_chunk_data_return pc = null;


        RewriteRuleSubtreeStream stream_paren_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1400:3: (pc= paren_chunk_data[false] -> VT_PAREN_CHUNK[$pc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1400:5: pc= paren_chunk_data[false]
            {
            pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk4781);
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
            // 1401:2: -> VT_PAREN_CHUNK[$pc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1404:1: paren_chunk_data[boolean isRecursive] : lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN ;
    public final DRLParser.paren_chunk_data_return paren_chunk_data(boolean isRecursive) throws RecognitionException {
        DRLParser.paren_chunk_data_return retval = new DRLParser.paren_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lp1=null;
        Token any=null;
        Token rp1=null;
        DRLParser.paren_chunk_data_return paren_chunk_data285 = null;


        Object lp1_tree=null;
        Object any_tree=null;
        Object rp1_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1405:2: (lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1405:4: lp1= LEFT_PAREN (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )* rp1= RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            lp1=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk_data4805); if (state.failed) return retval;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1412:4: (any=~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data[true] )*
            loop103:
            do {
                int alt103=3;
                int LA103_0 = input.LA(1);

                if ( ((LA103_0>=VT_COMPILATION_UNIT && LA103_0<=STRING)||LA103_0==COMMA||(LA103_0>=AT && LA103_0<=MULTI_LINE_COMMENT)) ) {
                    alt103=1;
                }
                else if ( (LA103_0==LEFT_PAREN) ) {
                    alt103=2;
                }


                switch (alt103) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1412:5: any=~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=STRING)||input.LA(1)==COMMA||(input.LA(1)>=AT && input.LA(1)<=MULTI_LINE_COMMENT) ) {
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1412:87: paren_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk_data4833);
            	    paren_chunk_data285=paren_chunk_data(true);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, paren_chunk_data285.getTree());

            	    }
            	    break;

            	default :
            	    break loop103;
                }
            } while (true);

            rp1=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk_data4844); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1422:1: square_chunk : sc= square_chunk_data[false] -> VT_SQUARE_CHUNK[$sc.start,text] ;
    public final DRLParser.square_chunk_return square_chunk() throws RecognitionException {
        DRLParser.square_chunk_return retval = new DRLParser.square_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLParser.square_chunk_data_return sc = null;


        RewriteRuleSubtreeStream stream_square_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1425:3: (sc= square_chunk_data[false] -> VT_SQUARE_CHUNK[$sc.start,text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1425:5: sc= square_chunk_data[false]
            {
            pushFollow(FOLLOW_square_chunk_data_in_square_chunk4865);
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
            // 1426:2: -> VT_SQUARE_CHUNK[$sc.start,text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1429:1: square_chunk_data[boolean isRecursive] : ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE ;
    public final DRLParser.square_chunk_data_return square_chunk_data(boolean isRecursive) throws RecognitionException {
        DRLParser.square_chunk_data_return retval = new DRLParser.square_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ls1=null;
        Token any=null;
        Token rs1=null;
        DRLParser.square_chunk_data_return square_chunk_data286 = null;


        Object ls1_tree=null;
        Object any_tree=null;
        Object rs1_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1430:2: (ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1430:4: ls1= LEFT_SQUARE (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )* rs1= RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            ls1=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk_data4888); if (state.failed) return retval;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1437:4: (any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data[true] )*
            loop104:
            do {
                int alt104=3;
                int LA104_0 = input.LA(1);

                if ( ((LA104_0>=VT_COMPILATION_UNIT && LA104_0<=NULL)||(LA104_0>=THEN && LA104_0<=MULTI_LINE_COMMENT)) ) {
                    alt104=1;
                }
                else if ( (LA104_0==LEFT_SQUARE) ) {
                    alt104=2;
                }


                switch (alt104) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1437:5: any=~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    any=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=NULL)||(input.LA(1)>=THEN && input.LA(1)<=MULTI_LINE_COMMENT) ) {
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1437:88: square_chunk_data[true]
            	    {
            	    pushFollow(FOLLOW_square_chunk_data_in_square_chunk_data4915);
            	    square_chunk_data286=square_chunk_data(true);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, square_chunk_data286.getTree());

            	    }
            	    break;

            	default :
            	    break loop104;
                }
            } while (true);

            rs1=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk_data4926); if (state.failed) return retval;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1447:1: lock_on_active_key : {...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID -> VK_LOCK_ON_ACTIVE[$start, text] ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1450:3: ({...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID -> VK_LOCK_ON_ACTIVE[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1450:5: {...}? =>id1= ID mis1= MISC id2= ID mis2= MISC id3= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "lock_on_active_key", "(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, \"-\") && validateLT(5, DroolsSoftKeywords.ACTIVE))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_lock_on_active_key4950); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_lock_on_active_key4954); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_lock_on_active_key4958); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id2);

            mis2=(Token)match(input,MISC,FOLLOW_MISC_in_lock_on_active_key4962); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis2);

            id3=(Token)match(input,ID,FOLLOW_ID_in_lock_on_active_key4966); if (state.failed) return retval; 
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
            // 1456:3: -> VK_LOCK_ON_ACTIVE[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1459:1: date_effective_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EFFECTIVE[$start, text] ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1462:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EFFECTIVE[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1462:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "date_effective_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_date_effective_key4998); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_date_effective_key5002); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_date_effective_key5006); if (state.failed) return retval; 
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
            // 1466:3: -> VK_DATE_EFFECTIVE[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1469:1: date_expires_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EXPIRES[$start, text] ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1472:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_DATE_EXPIRES[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1472:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "date_expires_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EXPIRES))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_date_expires_key5038); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_date_expires_key5042); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_date_expires_key5046); if (state.failed) return retval; 
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
            // 1476:3: -> VK_DATE_EXPIRES[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1479:1: no_loop_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_NO_LOOP[$start, text] ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1482:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_NO_LOOP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1482:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "no_loop_key", "(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.LOOP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_no_loop_key5078); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_no_loop_key5082); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_no_loop_key5086); if (state.failed) return retval; 
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
            // 1486:3: -> VK_NO_LOOP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1489:1: auto_focus_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_AUTO_FOCUS[$start, text] ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1492:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_AUTO_FOCUS[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1492:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "auto_focus_key", "(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.FOCUS))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_auto_focus_key5118); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_auto_focus_key5122); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_auto_focus_key5126); if (state.failed) return retval; 
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
            // 1496:3: -> VK_AUTO_FOCUS[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:1: activation_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_ACTIVATION_GROUP[$start, text] ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1502:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_ACTIVATION_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1502:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "activation_group_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_activation_group_key5158); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_activation_group_key5162); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_activation_group_key5166); if (state.failed) return retval; 
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
            // 1506:3: -> VK_ACTIVATION_GROUP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1509:1: agenda_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_AGENDA_GROUP[$start, text] ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1512:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_AGENDA_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1512:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "agenda_group_key", "(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_agenda_group_key5198); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_agenda_group_key5202); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_agenda_group_key5206); if (state.failed) return retval; 
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
            // 1516:3: -> VK_AGENDA_GROUP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1519:1: ruleflow_group_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_RULEFLOW_GROUP[$start, text] ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1522:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_RULEFLOW_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1522:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "ruleflow_group_key", "(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_ruleflow_group_key5238); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_ruleflow_group_key5242); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_ruleflow_group_key5246); if (state.failed) return retval; 
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
            // 1526:3: -> VK_RULEFLOW_GROUP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1529:1: entry_point_key : {...}? =>id1= ID mis1= MISC id2= ID -> VK_ENTRY_POINT[$start, text] ;
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1532:3: ({...}? =>id1= ID mis1= MISC id2= ID -> VK_ENTRY_POINT[$start, text] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1532:5: {...}? =>id1= ID mis1= MISC id2= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "entry_point_key", "(validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.POINT))");
            }
            id1=(Token)match(input,ID,FOLLOW_ID_in_entry_point_key5278); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id1);

            mis1=(Token)match(input,MISC,FOLLOW_MISC_in_entry_point_key5282); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MISC.add(mis1);

            id2=(Token)match(input,ID,FOLLOW_ID_in_entry_point_key5286); if (state.failed) return retval; 
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
            // 1536:3: -> VK_ENTRY_POINT[$start, text]
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

    public static class duration_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "duration_key"
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1539:1: duration_key : {...}? =>id= ID -> VK_DURATION[$id] ;
    public final DRLParser.duration_key_return duration_key() throws RecognitionException {
        DRLParser.duration_key_return retval = new DRLParser.duration_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1540:2: ({...}? =>id= ID -> VK_DURATION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1540:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DURATION)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "duration_key", "(validateIdentifierKey(DroolsSoftKeywords.DURATION))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_duration_key5315); if (state.failed) return retval; 
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
            // 1542:3: -> VK_DURATION[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VK_DURATION, id));

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1545:1: package_key : {...}? =>id= ID -> VK_PACKAGE[$id] ;
    public final DRLParser.package_key_return package_key() throws RecognitionException {
        DRLParser.package_key_return retval = new DRLParser.package_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1546:2: ({...}? =>id= ID -> VK_PACKAGE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1546:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.PACKAGE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "package_key", "(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_package_key5342); if (state.failed) return retval; 
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
            // 1548:3: -> VK_PACKAGE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1551:1: import_key : {...}? =>id= ID -> VK_IMPORT[$id] ;
    public final DRLParser.import_key_return import_key() throws RecognitionException {
        DRLParser.import_key_return retval = new DRLParser.import_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1552:2: ({...}? =>id= ID -> VK_IMPORT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1552:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "import_key", "(validateIdentifierKey(DroolsSoftKeywords.IMPORT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_import_key5369); if (state.failed) return retval; 
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
            // 1554:3: -> VK_IMPORT[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1557:1: dialect_key : {...}? =>id= ID -> VK_DIALECT[$id] ;
    public final DRLParser.dialect_key_return dialect_key() throws RecognitionException {
        DRLParser.dialect_key_return retval = new DRLParser.dialect_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1558:2: ({...}? =>id= ID -> VK_DIALECT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1558:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "dialect_key", "(validateIdentifierKey(DroolsSoftKeywords.DIALECT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_dialect_key5396); if (state.failed) return retval; 
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
            // 1560:3: -> VK_DIALECT[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1563:1: salience_key : {...}? =>id= ID -> VK_SALIENCE[$id] ;
    public final DRLParser.salience_key_return salience_key() throws RecognitionException {
        DRLParser.salience_key_return retval = new DRLParser.salience_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1564:2: ({...}? =>id= ID -> VK_SALIENCE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1564:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "salience_key", "(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_salience_key5423); if (state.failed) return retval; 
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
            // 1566:3: -> VK_SALIENCE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1569:1: enabled_key : {...}? =>id= ID -> VK_ENABLED[$id] ;
    public final DRLParser.enabled_key_return enabled_key() throws RecognitionException {
        DRLParser.enabled_key_return retval = new DRLParser.enabled_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1570:2: ({...}? =>id= ID -> VK_ENABLED[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1570:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "enabled_key", "(validateIdentifierKey(DroolsSoftKeywords.ENABLED))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_enabled_key5450); if (state.failed) return retval; 
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
            // 1572:3: -> VK_ENABLED[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1575:1: attributes_key : {...}? =>id= ID -> VK_ATTRIBUTES[$id] ;
    public final DRLParser.attributes_key_return attributes_key() throws RecognitionException {
        DRLParser.attributes_key_return retval = new DRLParser.attributes_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1576:2: ({...}? =>id= ID -> VK_ATTRIBUTES[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1576:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "attributes_key", "(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_attributes_key5477); if (state.failed) return retval; 
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
            // 1578:3: -> VK_ATTRIBUTES[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1581:1: rule_key : {...}? =>id= ID -> VK_RULE[$id] ;
    public final DRLParser.rule_key_return rule_key() throws RecognitionException {
        DRLParser.rule_key_return retval = new DRLParser.rule_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1582:2: ({...}? =>id= ID -> VK_RULE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1582:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.RULE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "rule_key", "(validateIdentifierKey(DroolsSoftKeywords.RULE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_rule_key5504); if (state.failed) return retval; 
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
            // 1584:3: -> VK_RULE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1587:1: extend_key : {...}? =>id= ID -> VK_EXTEND[$id] ;
    public final DRLParser.extend_key_return extend_key() throws RecognitionException {
        DRLParser.extend_key_return retval = new DRLParser.extend_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1588:2: ({...}? =>id= ID -> VK_EXTEND[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1588:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "extend_key", "(validateIdentifierKey(DroolsSoftKeywords.EXTEND))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_extend_key5531); if (state.failed) return retval; 
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
            // 1590:3: -> VK_EXTEND[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1593:1: template_key : {...}? =>id= ID -> VK_TEMPLATE[$id] ;
    public final DRLParser.template_key_return template_key() throws RecognitionException {
        DRLParser.template_key_return retval = new DRLParser.template_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1594:2: ({...}? =>id= ID -> VK_TEMPLATE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1594:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "template_key", "(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_template_key5558); if (state.failed) return retval; 
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
            // 1596:3: -> VK_TEMPLATE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1599:1: query_key : {...}? =>id= ID -> VK_QUERY[$id] ;
    public final DRLParser.query_key_return query_key() throws RecognitionException {
        DRLParser.query_key_return retval = new DRLParser.query_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1600:2: ({...}? =>id= ID -> VK_QUERY[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1600:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "query_key", "(validateIdentifierKey(DroolsSoftKeywords.QUERY))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_query_key5585); if (state.failed) return retval; 
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
            // 1602:3: -> VK_QUERY[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1605:1: declare_key : {...}? =>id= ID -> VK_DECLARE[$id] ;
    public final DRLParser.declare_key_return declare_key() throws RecognitionException {
        DRLParser.declare_key_return retval = new DRLParser.declare_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1606:2: ({...}? =>id= ID -> VK_DECLARE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1606:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "declare_key", "(validateIdentifierKey(DroolsSoftKeywords.DECLARE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_declare_key5612); if (state.failed) return retval; 
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
            // 1608:3: -> VK_DECLARE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1611:1: function_key : {...}? =>id= ID -> VK_FUNCTION[$id] ;
    public final DRLParser.function_key_return function_key() throws RecognitionException {
        DRLParser.function_key_return retval = new DRLParser.function_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1612:2: ({...}? =>id= ID -> VK_FUNCTION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1612:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "function_key", "(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_function_key5639); if (state.failed) return retval; 
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
            // 1614:3: -> VK_FUNCTION[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1617:1: global_key : {...}? =>id= ID -> VK_GLOBAL[$id] ;
    public final DRLParser.global_key_return global_key() throws RecognitionException {
        DRLParser.global_key_return retval = new DRLParser.global_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1618:2: ({...}? =>id= ID -> VK_GLOBAL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1618:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "global_key", "(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_global_key5666); if (state.failed) return retval; 
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
            // 1620:3: -> VK_GLOBAL[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1623:1: eval_key : {...}? =>id= ID -> VK_EVAL[$id] ;
    public final DRLParser.eval_key_return eval_key() throws RecognitionException {
        DRLParser.eval_key_return retval = new DRLParser.eval_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1624:2: ({...}? =>id= ID -> VK_EVAL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1624:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.EVAL)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "eval_key", "(validateIdentifierKey(DroolsSoftKeywords.EVAL))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_eval_key5693); if (state.failed) return retval; 
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
            // 1626:3: -> VK_EVAL[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1629:1: not_key : {...}? =>id= ID -> VK_NOT[$id] ;
    public final DRLParser.not_key_return not_key() throws RecognitionException {
        DRLParser.not_key_return retval = new DRLParser.not_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1630:2: ({...}? =>id= ID -> VK_NOT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1630:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "not_key", "(validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_not_key5720); if (state.failed) return retval; 
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
            // 1632:3: -> VK_NOT[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1635:1: in_key : {...}? =>id= ID -> VK_IN[$id] ;
    public final DRLParser.in_key_return in_key() throws RecognitionException {
        DRLParser.in_key_return retval = new DRLParser.in_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1636:2: ({...}? =>id= ID -> VK_IN[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1636:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "in_key", "(validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_in_key5747); if (state.failed) return retval; 
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
            // 1638:3: -> VK_IN[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1641:1: or_key : {...}? =>id= ID -> VK_OR[$id] ;
    public final DRLParser.or_key_return or_key() throws RecognitionException {
        DRLParser.or_key_return retval = new DRLParser.or_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1642:2: ({...}? =>id= ID -> VK_OR[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1642:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.OR)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "or_key", "(validateIdentifierKey(DroolsSoftKeywords.OR))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_or_key5774); if (state.failed) return retval; 
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
            // 1644:3: -> VK_OR[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:1: and_key : {...}? =>id= ID -> VK_AND[$id] ;
    public final DRLParser.and_key_return and_key() throws RecognitionException {
        DRLParser.and_key_return retval = new DRLParser.and_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1648:2: ({...}? =>id= ID -> VK_AND[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1648:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.AND)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "and_key", "(validateIdentifierKey(DroolsSoftKeywords.AND))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_and_key5801); if (state.failed) return retval; 
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
            // 1650:3: -> VK_AND[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1653:1: exists_key : {...}? =>id= ID -> VK_EXISTS[$id] ;
    public final DRLParser.exists_key_return exists_key() throws RecognitionException {
        DRLParser.exists_key_return retval = new DRLParser.exists_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1654:2: ({...}? =>id= ID -> VK_EXISTS[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1654:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.EXISTS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "exists_key", "(validateIdentifierKey(DroolsSoftKeywords.EXISTS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_exists_key5828); if (state.failed) return retval; 
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
            // 1656:3: -> VK_EXISTS[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1659:1: forall_key : {...}? =>id= ID -> VK_FORALL[$id] ;
    public final DRLParser.forall_key_return forall_key() throws RecognitionException {
        DRLParser.forall_key_return retval = new DRLParser.forall_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1660:2: ({...}? =>id= ID -> VK_FORALL[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1660:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.FORALL)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "forall_key", "(validateIdentifierKey(DroolsSoftKeywords.FORALL))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_forall_key5855); if (state.failed) return retval; 
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
            // 1662:3: -> VK_FORALL[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1665:1: action_key : {...}? =>id= ID -> VK_ACTION[$id] ;
    public final DRLParser.action_key_return action_key() throws RecognitionException {
        DRLParser.action_key_return retval = new DRLParser.action_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1666:2: ({...}? =>id= ID -> VK_ACTION[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1666:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.ACTION)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "action_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTION))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_action_key5882); if (state.failed) return retval; 
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
            // 1668:3: -> VK_ACTION[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1671:1: reverse_key : {...}? =>id= ID -> VK_REVERSE[$id] ;
    public final DRLParser.reverse_key_return reverse_key() throws RecognitionException {
        DRLParser.reverse_key_return retval = new DRLParser.reverse_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:2: ({...}? =>id= ID -> VK_REVERSE[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.REVERSE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "reverse_key", "(validateIdentifierKey(DroolsSoftKeywords.REVERSE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_reverse_key5909); if (state.failed) return retval; 
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
            // 1674:3: -> VK_REVERSE[$id]
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1677:1: result_key : {...}? =>id= ID -> VK_RESULT[$id] ;
    public final DRLParser.result_key_return result_key() throws RecognitionException {
        DRLParser.result_key_return retval = new DRLParser.result_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1678:2: ({...}? =>id= ID -> VK_RESULT[$id] )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1678:4: {...}? =>id= ID
            {
            if ( !(((validateIdentifierKey(DroolsSoftKeywords.RESULT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "result_key", "(validateIdentifierKey(DroolsSoftKeywords.RESULT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_result_key5936); if (state.failed) return retval; 
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
            // 1680:3: -> VK_RESULT[$id]
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

    // $ANTLR start synpred1_DRL
    public final void synpred1_DRL_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:5: ( LEFT_PAREN or_key )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:6: LEFT_PAREN or_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred1_DRL2033); if (state.failed) return ;
        pushFollow(FOLLOW_or_key_in_synpred1_DRL2035);
        or_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_DRL

    // $ANTLR start synpred2_DRL
    public final void synpred2_DRL_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:817:5: ( or_key | DOUBLE_PIPE )
        int alt105=2;
        int LA105_0 = input.LA(1);

        if ( (LA105_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            alt105=1;
        }
        else if ( (LA105_0==DOUBLE_PIPE) ) {
            alt105=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 105, 0, input);

            throw nvae;
        }
        switch (alt105) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:817:6: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred2_DRL2102);
                or_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:817:13: DOUBLE_PIPE
                {
                match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred2_DRL2104); if (state.failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred2_DRL

    // $ANTLR start synpred3_DRL
    public final void synpred3_DRL_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:826:5: ( LEFT_PAREN and_key )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:826:6: LEFT_PAREN and_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred3_DRL2161); if (state.failed) return ;
        pushFollow(FOLLOW_and_key_in_synpred3_DRL2163);
        and_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_DRL

    // $ANTLR start synpred4_DRL
    public final void synpred4_DRL_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:834:5: ( and_key | DOUBLE_AMPER )
        int alt106=2;
        int LA106_0 = input.LA(1);

        if ( (LA106_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))))) {
            alt106=1;
        }
        else if ( (LA106_0==DOUBLE_AMPER) ) {
            alt106=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 106, 0, input);

            throw nvae;
        }
        switch (alt106) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:834:6: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred4_DRL2231);
                and_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:834:14: DOUBLE_AMPER
                {
                match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred4_DRL2233); if (state.failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred4_DRL

    // $ANTLR start synpred5_DRL
    public final void synpred5_DRL_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:851:4: ( SEMICOLON )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:851:5: SEMICOLON
        {
        match(input,SEMICOLON,FOLLOW_SEMICOLON_in_synpred5_DRL2356); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_DRL

    // $ANTLR start synpred6_DRL
    public final void synpred6_DRL_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:12: ( LEFT_PAREN ( or_key | and_key ) )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:13: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred6_DRL2393); if (state.failed) return ;
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:24: ( or_key | and_key )
        int alt107=2;
        int LA107_0 = input.LA(1);

        if ( (LA107_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.AND)))||((validateIdentifierKey(DroolsSoftKeywords.OR)))))) {
            int LA107_1 = input.LA(2);

            if ( (((validateIdentifierKey(DroolsSoftKeywords.OR)))) ) {
                alt107=1;
            }
            else if ( (((validateIdentifierKey(DroolsSoftKeywords.AND)))) ) {
                alt107=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 107, 1, input);

                throw nvae;
            }
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 107, 0, input);

            throw nvae;
        }
        switch (alt107) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:25: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred6_DRL2396);
                or_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:32: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred6_DRL2398);
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
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:5: ( LEFT_PAREN ( or_key | and_key ) )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:6: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred7_DRL2521); if (state.failed) return ;
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:17: ( or_key | and_key )
        int alt108=2;
        int LA108_0 = input.LA(1);

        if ( (LA108_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.AND)))||((validateIdentifierKey(DroolsSoftKeywords.OR)))))) {
            int LA108_1 = input.LA(2);

            if ( (((validateIdentifierKey(DroolsSoftKeywords.OR)))) ) {
                alt108=1;
            }
            else if ( (((validateIdentifierKey(DroolsSoftKeywords.AND)))) ) {
                alt108=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 108, 1, input);

                throw nvae;
            }
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 108, 0, input);

            throw nvae;
        }
        switch (alt108) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:18: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred7_DRL2524);
                or_key();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:25: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred7_DRL2526);
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
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1050:5: ( LEFT_PAREN )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1050:6: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred8_DRL3355); if (state.failed) return ;

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
    protected DFA28 dfa28 = new DFA28(this);
    protected DFA37 dfa37 = new DFA37(this);
    protected DFA69 dfa69 = new DFA69(this);
    protected DFA71 dfa71 = new DFA71(this);
    protected DFA81 dfa81 = new DFA81(this);
    protected DFA86 dfa86 = new DFA86(this);
    static final String DFA1_eotS =
        "\12\uffff";
    static final String DFA1_eofS =
        "\1\2\11\uffff";
    static final String DFA1_minS =
        "\2\120\2\uffff\1\0\5\uffff";
    static final String DFA1_maxS =
        "\1\120\1\163\2\uffff\1\0\5\uffff";
    static final String DFA1_acceptS =
        "\2\uffff\2\2\1\uffff\4\2\1\1";
    static final String DFA1_specialS =
        "\1\uffff\1\1\2\uffff\1\0\5\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\1",
            "\1\4\3\uffff\1\5\1\7\6\uffff\1\10\1\6\25\uffff\1\3",
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
            return "394:4: ( package_statement )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA1_4 = input.LA(1);

                         
                        int index1_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.PACKAGE)))) ) {s = 9;}

                        else if ( (true) ) {s = 8;}

                         
                        input.seek(index1_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA1_1 = input.LA(1);

                         
                        int index1_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_1==MISC) && ((((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))))) {s = 3;}

                        else if ( (LA1_1==ID) ) {s = 4;}

                        else if ( (LA1_1==STRING) ) {s = 5;}

                        else if ( (LA1_1==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {s = 6;}

                        else if ( (LA1_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))))) {s = 7;}

                        else if ( (LA1_1==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.ENABLED))))) {s = 8;}

                         
                        input.seek(index1_1);
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
        "\2\120\1\uffff\1\0\2\uffff\1\0\11\uffff";
    static final String DFA5_maxS =
        "\1\120\1\163\1\uffff\1\0\2\uffff\1\0\11\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\1\1\1\uffff\2\1\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10"+
        "\1\11";
    static final String DFA5_specialS =
        "\1\uffff\1\0\1\uffff\1\1\2\uffff\1\2\11\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1",
            "\1\3\3\uffff\1\6\1\5\6\uffff\1\7\1\4\25\uffff\1\2",
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
            return "452:1: statement options {k=2; } : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | {...}? => template | {...}? => type_declaration | rule | query );";
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

                        else if ( (LA5_1==ID) && ((!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!((((((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!((((((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT))))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE)))))))))) {s = 3;}

                        else if ( (LA5_1==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {s = 4;}

                        else if ( (LA5_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))))) {s = 5;}

                        else if ( (LA5_1==STRING) && ((!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))||!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE))))))))) {s = 6;}

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

                        else if ( (!(((((validateIdentifierKey(DroolsSoftKeywords.QUERY)))||((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))||((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))||((validateLT(1, DroolsSoftKeywords.TEMPLATE)))||(((validateLT(1, DroolsSoftKeywords.DECLARE)))&&((validateIdentifierKey(DroolsSoftKeywords.DECLARE))))||(((validateLT(1, "import") && validateLT(2, "function") ))&&((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))))) ) {s = 14;}

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
        "\2\120\1\uffff\1\120\1\uffff\2\120\2\157\2\120\1\125\1\157\1\120";
    static final String DFA12_maxS =
        "\1\125\1\127\1\uffff\1\156\1\uffff\1\156\1\120\2\157\3\156\1\157"+
        "\1\156";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\11\uffff";
    static final String DFA12_specialS =
        "\16\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\2\2\uffff\1\2\1\uffff\1\1",
            "\1\3\4\uffff\1\2\1\uffff\1\4",
            "",
            "\1\5\1\6\3\uffff\1\2\2\4\1\uffff\1\2\24\uffff\1\7",
            "",
            "\2\2\3\uffff\1\2\2\4\1\uffff\1\2\24\uffff\1\10",
            "\1\11",
            "\1\12",
            "\1\13",
            "\1\4\1\6\3\uffff\1\2\30\uffff\1\14",
            "\1\4\4\uffff\1\2\2\4\26\uffff\1\7",
            "\1\2\2\4\26\uffff\1\10",
            "\1\15",
            "\1\4\4\uffff\1\2\30\uffff\1\14"
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
            return "523:3: ( parameters )?";
        }
    }
    static final String DFA17_eotS =
        "\6\uffff";
    static final String DFA17_eofS =
        "\6\uffff";
    static final String DFA17_minS =
        "\2\120\1\uffff\1\157\1\uffff\1\120";
    static final String DFA17_maxS =
        "\1\120\1\156\1\uffff\1\157\1\uffff\1\156";
    static final String DFA17_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    static final String DFA17_specialS =
        "\6\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\1",
            "\2\2\4\uffff\2\4\26\uffff\1\3",
            "",
            "\1\5",
            "",
            "\1\2\5\uffff\2\4\26\uffff\1\3"
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
            return "549:4: ( data_type )?";
        }
    }
    static final String DFA28_eotS =
        "\12\uffff";
    static final String DFA28_eofS =
        "\12\uffff";
    static final String DFA28_minS =
        "\2\120\5\uffff\1\0\2\uffff";
    static final String DFA28_maxS =
        "\1\160\1\163\5\uffff\1\0\2\uffff";
    static final String DFA28_acceptS =
        "\2\uffff\5\2\1\uffff\1\1\1\2";
    static final String DFA28_specialS =
        "\1\2\1\0\5\uffff\1\1\2\uffff}>";
    static final String[] DFA28_transitionS = {
            "\1\1\7\uffff\1\2\2\uffff\1\2\24\uffff\1\2",
            "\1\10\3\uffff\1\7\1\6\3\uffff\1\11\2\uffff\1\5\1\4\25\uffff"+
            "\1\3",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            ""
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
            return "641:3: ( extend_key rule_id )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA28_1 = input.LA(1);

                         
                        int index28_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_1==MISC) && ((((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))))) {s = 3;}

                        else if ( (LA28_1==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))))) {s = 4;}

                        else if ( (LA28_1==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.ENABLED))))) {s = 5;}

                        else if ( (LA28_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))))) {s = 6;}

                        else if ( (LA28_1==STRING) && ((((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))))) {s = 7;}

                        else if ( (LA28_1==ID) && (((validateIdentifierKey(DroolsSoftKeywords.EXTEND))))) {s = 8;}

                        else if ( (LA28_1==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {s = 9;}

                         
                        input.seek(index28_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA28_7 = input.LA(1);

                         
                        int index28_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))) ) {s = 8;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) ) {s = 9;}

                         
                        input.seek(index28_7);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA28_0 = input.LA(1);

                         
                        int index28_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.EXTEND)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))))) {s = 1;}

                        else if ( (LA28_0==AT||LA28_0==WHEN||LA28_0==THEN) ) {s = 2;}

                         
                        input.seek(index28_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 28, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA37_eotS =
        "\16\uffff";
    static final String DFA37_eofS =
        "\16\uffff";
    static final String DFA37_minS =
        "\1\120\1\0\14\uffff";
    static final String DFA37_maxS =
        "\1\120\1\0\14\uffff";
    static final String DFA37_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14";
    static final String DFA37_specialS =
        "\1\0\1\1\14\uffff}>";
    static final String[] DFA37_transitionS = {
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

    static final short[] DFA37_eot = DFA.unpackEncodedString(DFA37_eotS);
    static final short[] DFA37_eof = DFA.unpackEncodedString(DFA37_eofS);
    static final char[] DFA37_min = DFA.unpackEncodedStringToUnsignedChars(DFA37_minS);
    static final char[] DFA37_max = DFA.unpackEncodedStringToUnsignedChars(DFA37_maxS);
    static final short[] DFA37_accept = DFA.unpackEncodedString(DFA37_acceptS);
    static final short[] DFA37_special = DFA.unpackEncodedString(DFA37_specialS);
    static final short[][] DFA37_transition;

    static {
        int numStates = DFA37_transitionS.length;
        DFA37_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA37_transition[i] = DFA.unpackEncodedString(DFA37_transitionS[i]);
        }
    }

    class DFA37 extends DFA {

        public DFA37(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 37;
            this.eot = DFA37_eot;
            this.eof = DFA37_eof;
            this.min = DFA37_min;
            this.max = DFA37_max;
            this.accept = DFA37_accept;
            this.special = DFA37_special;
            this.transition = DFA37_transition;
        }
        public String getDescription() {
            return "710:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA37_0 = input.LA(1);

                         
                        int index37_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA37_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))||((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))||((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))||((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))||((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))||((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))||((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))||((validateIdentifierKey(DroolsSoftKeywords.DURATION)))||((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))||((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))))) {s = 1;}

                         
                        input.seek(index37_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA37_1 = input.LA(1);

                         
                        int index37_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) ) {s = 2;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP)))) ) {s = 3;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {s = 4;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DURATION)))) ) {s = 5;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {s = 6;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS)))) ) {s = 7;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE)))) ) {s = 8;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES)))) ) {s = 9;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) ) {s = 10;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP)))) ) {s = 11;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))) ) {s = 12;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) ) {s = 13;}

                         
                        input.seek(index37_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 37, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA69_eotS =
        "\15\uffff";
    static final String DFA69_eofS =
        "\15\uffff";
    static final String DFA69_minS =
        "\1\117\1\0\13\uffff";
    static final String DFA69_maxS =
        "\1\160\1\0\13\uffff";
    static final String DFA69_acceptS =
        "\2\uffff\1\2\11\uffff\1\1";
    static final String DFA69_specialS =
        "\1\uffff\1\0\13\uffff}>";
    static final String[] DFA69_transitionS = {
            "\3\2\1\uffff\1\2\1\uffff\1\1\2\2\6\uffff\2\2\3\uffff\1\2\14"+
            "\uffff\1\2",
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

    static final short[] DFA69_eot = DFA.unpackEncodedString(DFA69_eotS);
    static final short[] DFA69_eof = DFA.unpackEncodedString(DFA69_eofS);
    static final char[] DFA69_min = DFA.unpackEncodedStringToUnsignedChars(DFA69_minS);
    static final char[] DFA69_max = DFA.unpackEncodedStringToUnsignedChars(DFA69_maxS);
    static final short[] DFA69_accept = DFA.unpackEncodedString(DFA69_acceptS);
    static final short[] DFA69_special = DFA.unpackEncodedString(DFA69_specialS);
    static final short[][] DFA69_transition;

    static {
        int numStates = DFA69_transitionS.length;
        DFA69_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA69_transition[i] = DFA.unpackEncodedString(DFA69_transitionS[i]);
        }
    }

    class DFA69 extends DFA {

        public DFA69(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 69;
            this.eot = DFA69_eot;
            this.eof = DFA69_eof;
            this.min = DFA69_min;
            this.max = DFA69_max;
            this.accept = DFA69_accept;
            this.special = DFA69_special;
            this.transition = DFA69_transition;
        }
        public String getDescription() {
            return "1050:3: ( ( LEFT_PAREN )=>args= paren_chunk )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA69_1 = input.LA(1);

                         
                        int index69_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRL()) ) {s = 12;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index69_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 69, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA71_eotS =
        "\16\uffff";
    static final String DFA71_eofS =
        "\16\uffff";
    static final String DFA71_minS =
        "\1\117\1\0\14\uffff";
    static final String DFA71_maxS =
        "\1\160\1\0\14\uffff";
    static final String DFA71_acceptS =
        "\2\uffff\1\2\1\3\11\uffff\1\1";
    static final String DFA71_specialS =
        "\1\uffff\1\0\14\uffff}>";
    static final String[] DFA71_transitionS = {
            "\3\3\1\uffff\1\3\1\uffff\1\1\2\3\6\uffff\2\3\3\uffff\1\3\12"+
            "\uffff\1\2\1\uffff\1\3",
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

    static final short[] DFA71_eot = DFA.unpackEncodedString(DFA71_eotS);
    static final short[] DFA71_eof = DFA.unpackEncodedString(DFA71_eofS);
    static final char[] DFA71_min = DFA.unpackEncodedStringToUnsignedChars(DFA71_minS);
    static final char[] DFA71_max = DFA.unpackEncodedStringToUnsignedChars(DFA71_maxS);
    static final short[] DFA71_accept = DFA.unpackEncodedString(DFA71_acceptS);
    static final short[] DFA71_special = DFA.unpackEncodedString(DFA71_specialS);
    static final short[][] DFA71_transition;

    static {
        int numStates = DFA71_transitionS.length;
        DFA71_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA71_transition[i] = DFA.unpackEncodedString(DFA71_transitionS[i]);
        }
    }

    class DFA71 extends DFA {

        public DFA71(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 71;
            this.eot = DFA71_eot;
            this.eof = DFA71_eof;
            this.min = DFA71_min;
            this.max = DFA71_max;
            this.accept = DFA71_accept;
            this.special = DFA71_special;
            this.transition = DFA71_transition;
        }
        public String getDescription() {
            return "1064:4: ({...}? paren_chunk | square_chunk )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA71_1 = input.LA(1);

                         
                        int index71_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((input.LA(1) == LEFT_PAREN)) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index71_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 71, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA81_eotS =
        "\17\uffff";
    static final String DFA81_eofS =
        "\17\uffff";
    static final String DFA81_minS =
        "\2\120\13\uffff\1\0\1\uffff";
    static final String DFA81_maxS =
        "\1\125\1\156\13\uffff\1\0\1\uffff";
    static final String DFA81_acceptS =
        "\2\uffff\1\3\1\2\12\uffff\1\1";
    static final String DFA81_specialS =
        "\15\uffff\1\0\1\uffff}>";
    static final String[] DFA81_transitionS = {
            "\1\1\4\uffff\1\2",
            "\2\3\3\uffff\1\15\3\uffff\1\3\14\uffff\6\3\2\uffff\1\3",
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
            return "1137:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA81_13 = input.LA(1);

                         
                        int index81_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.EVAL)))) ) {s = 14;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index81_13);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 81, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA86_eotS =
        "\50\uffff";
    static final String DFA86_eofS =
        "\50\uffff";
    static final String DFA86_minS =
        "\2\120\10\uffff\1\120\5\uffff\1\4\7\uffff\1\0\5\uffff\7\0\3\uffff";
    static final String DFA86_maxS =
        "\1\153\1\156\10\uffff\1\156\5\uffff\1\175\7\uffff\1\0\5\uffff\7"+
        "\0\3\uffff";
    static final String DFA86_acceptS =
        "\2\uffff\1\2\5\uffff\1\3\1\2\7\uffff\2\2\6\uffff\1\2\15\uffff\1"+
        "\1";
    static final String DFA86_specialS =
        "\1\0\1\1\10\uffff\1\2\5\uffff\1\3\7\uffff\1\4\5\uffff\1\5\1\6\1"+
        "\7\1\10\1\11\1\12\1\13\3\uffff}>";
    static final String[] DFA86_transitionS = {
            "\1\1\4\uffff\1\10\20\uffff\6\2",
            "\1\12\3\uffff\1\11\1\20\6\uffff\2\11\16\uffff\3\11",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\22\1\31\2\uffff\1\22\1\30\2\31\4\uffff\2\22\2\31\14\uffff"+
            "\2\22\1\21",
            "",
            "",
            "",
            "",
            "",
            "\114\31\1\36\3\31\1\37\1\44\6\31\1\42\1\40\16\31\1\41\1\43"+
            "\20\31",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
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

    static final short[] DFA86_eot = DFA.unpackEncodedString(DFA86_eotS);
    static final short[] DFA86_eof = DFA.unpackEncodedString(DFA86_eofS);
    static final char[] DFA86_min = DFA.unpackEncodedStringToUnsignedChars(DFA86_minS);
    static final char[] DFA86_max = DFA.unpackEncodedStringToUnsignedChars(DFA86_maxS);
    static final short[] DFA86_accept = DFA.unpackEncodedString(DFA86_acceptS);
    static final short[] DFA86_special = DFA.unpackEncodedString(DFA86_specialS);
    static final short[][] DFA86_transition;

    static {
        int numStates = DFA86_transitionS.length;
        DFA86_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA86_transition[i] = DFA.unpackEncodedString(DFA86_transitionS[i]);
        }
    }

    class DFA86 extends DFA {

        public DFA86(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 86;
            this.eot = DFA86_eot;
            this.eof = DFA86_eof;
            this.min = DFA86_min;
            this.max = DFA86_max;
            this.accept = DFA86_accept;
            this.special = DFA86_special;
            this.transition = DFA86_transition;
        }
        public String getDescription() {
            return "1201:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA86_0 = input.LA(1);

                         
                        int index86_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA86_0==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {s = 1;}

                        else if ( ((LA86_0>=EQUAL && LA86_0<=NOT_EQUAL)) ) {s = 2;}

                        else if ( (LA86_0==LEFT_PAREN) ) {s = 8;}

                         
                        input.seek(index86_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA86_1 = input.LA(1);

                         
                        int index86_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA86_1==STRING||(LA86_1>=BOOL && LA86_1<=INT)||(LA86_1>=FLOAT && LA86_1<=LEFT_SQUARE)) && (((isPluggableEvaluator(false))))) {s = 9;}

                        else if ( (LA86_1==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {s = 10;}

                        else if ( (LA86_1==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 16;}

                         
                        input.seek(index86_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA86_10 = input.LA(1);

                         
                        int index86_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA86_10==LEFT_SQUARE) && ((((validateIdentifierKey(DroolsSoftKeywords.NOT)))||((isPluggableEvaluator(false)))))) {s = 17;}

                        else if ( (LA86_10==ID||LA86_10==STRING||(LA86_10>=BOOL && LA86_10<=INT)||(LA86_10>=FLOAT && LA86_10<=NULL)) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))))) {s = 18;}

                        else if ( (LA86_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))))) {s = 24;}

                        else if ( (LA86_10==DOT||(LA86_10>=COMMA && LA86_10<=RIGHT_PAREN)||(LA86_10>=DOUBLE_PIPE && LA86_10<=DOUBLE_AMPER)) && (((isPluggableEvaluator(false))))) {s = 25;}

                         
                        input.seek(index86_10);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA86_16 = input.LA(1);

                         
                        int index86_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA86_16==ID) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 30;}

                        else if ( (LA86_16==STRING) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 31;}

                        else if ( (LA86_16==INT) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 32;}

                        else if ( (LA86_16==FLOAT) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 33;}

                        else if ( (LA86_16==BOOL) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 34;}

                        else if ( (LA86_16==NULL) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 35;}

                        else if ( (LA86_16==LEFT_PAREN) && ((((validateIdentifierKey(DroolsSoftKeywords.IN)))||((isPluggableEvaluator(false)))))) {s = 36;}

                        else if ( ((LA86_16>=VT_COMPILATION_UNIT && LA86_16<=SEMICOLON)||(LA86_16>=DOT && LA86_16<=END)||(LA86_16>=COMMA && LA86_16<=WHEN)||(LA86_16>=DOUBLE_PIPE && LA86_16<=NOT_EQUAL)||(LA86_16>=LEFT_SQUARE && LA86_16<=MULTI_LINE_COMMENT)) && (((isPluggableEvaluator(false))))) {s = 25;}

                         
                        input.seek(index86_16);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA86_24 = input.LA(1);

                         
                        int index86_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {s = 39;}

                        else if ( (((validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {s = 25;}

                         
                        input.seek(index86_24);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA86_30 = input.LA(1);

                         
                        int index86_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index86_30);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA86_31 = input.LA(1);

                         
                        int index86_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index86_31);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA86_32 = input.LA(1);

                         
                        int index86_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index86_32);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA86_33 = input.LA(1);

                         
                        int index86_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index86_33);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA86_34 = input.LA(1);

                         
                        int index86_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index86_34);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA86_35 = input.LA(1);

                         
                        int index86_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index86_35);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA86_36 = input.LA(1);

                         
                        int index86_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 39;}

                        else if ( (((isPluggableEvaluator(false)))) ) {s = 25;}

                         
                        input.seek(index86_36);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 86, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_package_statement_in_compilation_unit376 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_statement_in_compilation_unit381 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_EOF_in_compilation_unit386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_package_key_in_package_statement441 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_package_id_in_package_statement445 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_SEMICOLON_in_package_statement447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_id474 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_DOT_in_package_id480 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_package_id484 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_rule_attribute_in_statement522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_declaration_in_statement563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_import_statement595 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_import_name_in_import_statement597 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_SEMICOLON_in_import_statement600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_function_import_statement638 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_function_key_in_function_import_statement640 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement642 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_SEMICOLON_in_function_import_statement645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name679 = new BitSet(new long[]{0x0000000000000002L,0x0000000000060000L});
    public static final BitSet FOLLOW_DOT_in_import_name685 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_import_name689 = new BitSet(new long[]{0x0000000000000002L,0x0000000000060000L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_key_in_global736 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_data_type_in_global738 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_global_id_in_global740 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_SEMICOLON_in_global742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_id771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_key_in_function803 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_data_type_in_function805 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_function_id_in_function808 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_parameters_in_function810 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_id842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_key_in_query874 = new BitSet(new long[]{0x0000000000000000L,0x0000000000110000L});
    public static final BitSet FOLLOW_query_id_in_query876 = new BitSet(new long[]{0x0000000000000000L,0x0000000000290000L});
    public static final BitSet FOLLOW_parameters_in_query884 = new BitSet(new long[]{0x0000000000000000L,0x0000000000290000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query893 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_END_in_query898 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_SEMICOLON_in_query900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_id935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_query_id951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parameters970 = new BitSet(new long[]{0x0000000000000000L,0x0000000000810000L});
    public static final BitSet FOLLOW_param_definition_in_parameters979 = new BitSet(new long[]{0x0000000000000000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_COMMA_in_parameters982 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_param_definition_in_parameters986 = new BitSet(new long[]{0x0000000000000000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parameters995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_param_definition1021 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_argument_in_param_definition1024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument1035 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_dimension_definition_in_argument1041 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_declare_key_in_type_declaration1064 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_type_declare_id_in_type_declaration1067 = new BitSet(new long[]{0x0000000000000000L,0x0000000001090000L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration1071 = new BitSet(new long[]{0x0000000000000000L,0x0000000001090000L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration1076 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_END_in_type_declaration1081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_type_declare_id1116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_decl_metadata1135 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_decl_metadata1143 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_paren_chunk_in_decl_metadata1150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_decl_field1173 = new BitSet(new long[]{0x0000000000000000L,0x0000000006000000L});
    public static final BitSet FOLLOW_decl_field_initialization_in_decl_field1179 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_COLON_in_decl_field1185 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_data_type_in_decl_field1191 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field1195 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_EQUALS_in_decl_field_initialization1223 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_paren_chunk_in_decl_field_initialization1229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_key_in_template1266 = new BitSet(new long[]{0x0000000000000000L,0x0000000000110000L});
    public static final BitSet FOLLOW_template_id_in_template1268 = new BitSet(new long[]{0x0000000000000000L,0x0000000000018000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1275 = new BitSet(new long[]{0x0000000000000000L,0x0000000000018000L});
    public static final BitSet FOLLOW_template_slot_in_template1283 = new BitSet(new long[]{0x0000000000000000L,0x0000000000098000L});
    public static final BitSet FOLLOW_END_in_template1288 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_id1325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_template_id1341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_template_slot1361 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_slot_id_in_template_slot1363 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template_slot1365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_slot_id1394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_key_in_rule1431 = new BitSet(new long[]{0x0000000000000000L,0x0000000000110000L});
    public static final BitSet FOLLOW_rule_id_in_rule1433 = new BitSet(new long[]{0x0000000000000000L,0x0001000009010000L});
    public static final BitSet FOLLOW_extend_key_in_rule1442 = new BitSet(new long[]{0x0000000000000000L,0x0000000000110000L});
    public static final BitSet FOLLOW_rule_id_in_rule1444 = new BitSet(new long[]{0x0000000000000000L,0x0001000009010000L});
    public static final BitSet FOLLOW_decl_metadata_in_rule1448 = new BitSet(new long[]{0x0000000000000000L,0x0001000009010000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule1451 = new BitSet(new long[]{0x0000000000000000L,0x0001000009010000L});
    public static final BitSet FOLLOW_when_part_in_rule1454 = new BitSet(new long[]{0x0000000000000000L,0x0001000009010000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_when_part1501 = new BitSet(new long[]{0x0000000000000000L,0x0000000002210000L});
    public static final BitSet FOLLOW_COLON_in_when_part1507 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_when_part1517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_id1538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_rule_id1554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attributes_key_in_rule_attributes1575 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_COLON_in_rule_attributes1577 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1587 = new BitSet(new long[]{0x0000000000000002L,0x0000000000410000L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1591 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1598 = new BitSet(new long[]{0x0000000000000002L,0x0000000000410000L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_key_in_date_effective1718 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_key_in_date_expires1737 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_key_in_enabled1757 = new BitSet(new long[]{0x0000000000000000L,0x0000000010200000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_enabled1781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_salience_key_in_salience1801 = new BitSet(new long[]{0x0000000000000000L,0x0000000020200000L});
    public static final BitSet FOLLOW_INT_in_salience1810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_key_in_no_loop1834 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_key_in_auto_focus1854 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_key_in_activation_group1876 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_key_in_ruleflow_group1895 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_key_in_agenda_group1914 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_key_in_duration1933 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_INT_in_duration1938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_key_in_dialect1954 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_dialect1959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_key_in_lock_on_active1977 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1997 = new BitSet(new long[]{0x0000000000000002L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs2018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or2042 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or2052 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2060 = new BitSet(new long[]{0x0000000000000000L,0x0000000000A10000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or2066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2089 = new BitSet(new long[]{0x0000000000000002L,0x0000000040010000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or2111 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_lhs_or2118 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2129 = new BitSet(new long[]{0x0000000000000002L,0x0000000040010000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and2170 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and2180 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2188 = new BitSet(new long[]{0x0000000000000000L,0x0000000000A10000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and2194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2218 = new BitSet(new long[]{0x0000000000000002L,0x0000000080010000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and2240 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_lhs_and2247 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2258 = new BitSet(new long[]{0x0000000000000002L,0x0000000080010000L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2289 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_lhs_not_binding_in_lhs_unary2297 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2303 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2309 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary2315 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2321 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary2332 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2338 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_unary2346 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_SEMICOLON_in_lhs_unary2360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_key_in_lhs_exist2376 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist2410 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2418 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist2425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist2440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not_binding2486 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_not_binding2488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not2511 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2540 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2549 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_key_in_lhs_eval2604 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forall_key_in_lhs_forall2640 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2645 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2653 = new BitSet(new long[]{0x0000000000000000L,0x0000000000A10000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall2659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_pattern_source2695 = new BitSet(new long[]{0x0000000000000002L,0x0000000300000000L});
    public static final BitSet FOLLOW_over_clause_in_pattern_source2699 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_FROM_in_pattern_source2709 = new BitSet(new long[]{0x0000000000000000L,0x0000001400010000L});
    public static final BitSet FOLLOW_accumulate_statement_in_pattern_source2729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_pattern_source2745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entrypoint_statement_in_pattern_source2762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_pattern_source2778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause2810 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2815 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
    public static final BitSet FOLLOW_COMMA_in_over_clause2822 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_over_elements_in_over_clause2827 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_over_elements2842 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_COLON_in_over_elements2849 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_over_elements2858 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_paren_chunk_in_over_elements2865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement2891 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2900 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_lhs_or_in_accumulate_statement2908 = new BitSet(new long[]{0x0000000000000000L,0x0000000800410000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2913 = new BitSet(new long[]{0x0000000000000000L,0x0000000800410000L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_statement2923 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_statement2929 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INIT_in_accumulate_init_clause2983 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause2994 = new BitSet(new long[]{0x0000000000000000L,0x0000000000410000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2999 = new BitSet(new long[]{0x0000000000000000L,0x0000000000410000L});
    public static final BitSet FOLLOW_action_key_in_accumulate_init_clause3010 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3014 = new BitSet(new long[]{0x0000000000000000L,0x0000000000410000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause3019 = new BitSet(new long[]{0x0000000000000000L,0x0000000000410000L});
    public static final BitSet FOLLOW_reverse_key_in_accumulate_init_clause3031 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3035 = new BitSet(new long[]{0x0000000000000000L,0x0000000000410000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause3040 = new BitSet(new long[]{0x0000000000000000L,0x0000000000410000L});
    public static final BitSet FOLLOW_result_key_in_accumulate_init_clause3056 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_in_accumulate_init_clause3062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk3120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_paren_chunk_data3144 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_accumulate_paren_chunk_data3156 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_accumulate_paren_chunk_data_in_accumulate_paren_chunk_data3172 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_paren_chunk_data3183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause3199 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_id_clause3205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement3227 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement3236 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_pattern_source_in_collect_statement3243 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement3248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entry_point_key_in_entrypoint_statement3275 = new BitSet(new long[]{0x0000000000000000L,0x0000000000110000L});
    public static final BitSet FOLLOW_entrypoint_id_in_entrypoint_statement3283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entrypoint_id3309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_entrypoint_id3326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source3346 = new BitSet(new long[]{0x0000000000000002L,0x0000000000220000L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source3361 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_expression_chain_in_from_source3368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain3401 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_expression_chain3408 = new BitSet(new long[]{0x0000000000000002L,0x0000400000220000L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain3424 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain3438 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain3449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern3482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern3495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_fact_binding3515 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_fact_in_fact_binding3521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding3528 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_fact_binding_expression_in_fact_binding3536 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding3544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression3585 = new BitSet(new long[]{0x0000000000000002L,0x0000000040010000L});
    public static final BitSet FOLLOW_or_key_in_fact_binding_expression3597 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_binding_expression3603 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression3608 = new BitSet(new long[]{0x0000000000000002L,0x0000000040010000L});
    public static final BitSet FOLLOW_pattern_type_in_fact3648 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact3653 = new BitSet(new long[]{0x0000000000000000L,0x0000000000A10000L});
    public static final BitSet FOLLOW_constraints_in_fact3664 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact3670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints3704 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
    public static final BitSet FOLLOW_COMMA_in_constraints3708 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_constraint_in_constraints3715 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
    public static final BitSet FOLLOW_or_constr_in_constraint3729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3740 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr3744 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3751 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3766 = new BitSet(new long[]{0x0000000000000002L,0x0000000080000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr3770 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3777 = new BitSet(new long[]{0x0000000000000002L,0x0000000080000000L});
    public static final BitSet FOLLOW_eval_key_in_unary_constr3810 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_paren_chunk_in_unary_constr3813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr3818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr3824 = new BitSet(new long[]{0x0000000000000000L,0x0000000000210000L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr3834 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr3839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_field_constraint3859 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3861 = new BitSet(new long[]{0x0000000000000002L,0x00000FE000210000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_field_constraint3874 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_paren_chunk_in_field_constraint3878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3932 = new BitSet(new long[]{0x0000000000000000L,0x00000FC000210000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_label3959 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_COLON_in_label3966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3987 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective3993 = new BitSet(new long[]{0x0000000000000000L,0x00000FC000210000L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective4001 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective4022 = new BitSet(new long[]{0x0000000000000002L,0x0000000080000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective4028 = new BitSet(new long[]{0x0000000000000000L,0x00000FC000210000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective4035 = new BitSet(new long[]{0x0000000000000002L,0x0000000080000000L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression4063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression4068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression4073 = new BitSet(new long[]{0x0000000000000000L,0x00000FC000210000L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression4082 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression4087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUAL_in_simple_operator4122 = new BitSet(new long[]{0x0000000000000000L,0x0000300030310000L});
    public static final BitSet FOLLOW_GREATER_in_simple_operator4130 = new BitSet(new long[]{0x0000000000000000L,0x0000300030310000L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_simple_operator4138 = new BitSet(new long[]{0x0000000000000000L,0x0000300030310000L});
    public static final BitSet FOLLOW_LESS_in_simple_operator4146 = new BitSet(new long[]{0x0000000000000000L,0x0000300030310000L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_simple_operator4154 = new BitSet(new long[]{0x0000000000000000L,0x0000300030310000L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_simple_operator4162 = new BitSet(new long[]{0x0000000000000000L,0x0000300030310000L});
    public static final BitSet FOLLOW_not_key_in_simple_operator4170 = new BitSet(new long[]{0x0000000000000000L,0x00000FC000010000L});
    public static final BitSet FOLLOW_operator_key_in_simple_operator4177 = new BitSet(new long[]{0x0000000000000000L,0x0000700030310000L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator4180 = new BitSet(new long[]{0x0000000000000000L,0x0000300030310000L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator4192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_compound_operator4214 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_not_key_in_compound_operator4219 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_in_key_in_compound_operator4221 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator4232 = new BitSet(new long[]{0x0000000000000000L,0x0000300030310000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4240 = new BitSet(new long[]{0x0000000000000000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator4244 = new BitSet(new long[]{0x0000000000000000L,0x0000300030310000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4249 = new BitSet(new long[]{0x0000000000000000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator4257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_operator_key4288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_neg_operator_key4333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_expression_value4370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value4375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_value4381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint4400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint4407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint4414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint4421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint4428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pattern_type4443 = new BitSet(new long[]{0x0000000000000002L,0x0000400000020000L});
    public static final BitSet FOLLOW_DOT_in_pattern_type4449 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_pattern_type4453 = new BitSet(new long[]{0x0000000000000002L,0x0000400000020000L});
    public static final BitSet FOLLOW_dimension_definition_in_pattern_type4468 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_ID_in_data_type4496 = new BitSet(new long[]{0x0000000000000002L,0x0000400000020000L});
    public static final BitSet FOLLOW_DOT_in_data_type4502 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_data_type4506 = new BitSet(new long[]{0x0000000000000002L,0x0000400000020000L});
    public static final BitSet FOLLOW_dimension_definition_in_data_type4511 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dimension_definition4540 = new BitSet(new long[]{0x0000000000000000L,0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dimension_definition4547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4561 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_DOT_in_accessor_path4565 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4569 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_ID_in_accessor_element4593 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_square_chunk_in_accessor_element4599 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_rhs_chunk_data_in_rhs_chunk4628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk_data4647 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk_data4660 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk_data4673 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_SEMICOLON_in_rhs_chunk_data4679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk4698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk_data4721 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk_data4733 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk_data4749 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk_data4760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk4781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk_data4805 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk_data4817 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk_data4833 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk_data4844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk4865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk_data4888 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk_data4900 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk_data4915 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x3FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk_data4926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4950 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key4954 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4958 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key4962 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key4966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_effective_key4998 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_effective_key5002 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_date_effective_key5006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_expires_key5038 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_expires_key5042 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_date_expires_key5046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_no_loop_key5078 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_no_loop_key5082 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_no_loop_key5086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key5118 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_auto_focus_key5122 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key5126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_activation_group_key5158 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_activation_group_key5162 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_activation_group_key5166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key5198 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_agenda_group_key5202 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key5206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key5238 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_ruleflow_group_key5242 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key5246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entry_point_key5278 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_MISC_in_entry_point_key5282 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_entry_point_key5286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_duration_key5315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_key5342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_key5369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dialect_key5396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_salience_key5423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enabled_key5450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_attributes_key5477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_key5504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_extend_key5531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_key5558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_key5585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_declare_key5612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_key5639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_key5666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_eval_key5693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key5720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key5747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_or_key5774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_and_key5801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_exists_key5828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_forall_key5855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_action_key5882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_reverse_key5909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_result_key5936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred1_DRL2033 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_or_key_in_synpred1_DRL2035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_key_in_synpred2_DRL2102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred2_DRL2104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred3_DRL2161 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_and_key_in_synpred3_DRL2163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred4_DRL2231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred4_DRL2233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_synpred5_DRL2356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred6_DRL2393 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_or_key_in_synpred6_DRL2396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred6_DRL2398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred7_DRL2521 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_or_key_in_synpred7_DRL2524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred7_DRL2526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred8_DRL3355 = new BitSet(new long[]{0x0000000000000002L});

}