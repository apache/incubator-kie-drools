// $ANTLR 3.0.1 /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2008-06-05 23:49:53

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TEMPLATE_ID", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_SLOT_ID", "VT_SLOT", "VT_RULE_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_DURATION", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_WHEN", "VK_RULE", "VK_IMPORT", "VK_PACKAGE", "VK_TEMPLATE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_CONTAINS", "VK_MATCHES", "VK_EXCLUDES", "VK_SOUNDSLIKE", "VK_MEMBEROF", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_FROM", "VK_ACCUMULATE", "VK_INIT", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "VK_COLLECT", "SEMICOLON", "ID", "DOT", "DOT_STAR", "END", "STRING", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "COLON", "BOOL", "INT", "DOUBLE_PIPE", "DOUBLE_AMPER", "ARROW", "EQUAL", "GREATER", "GREATER_EQUAL", "LESS", "LESS_EQUAL", "NOT_EQUAL", "GRAVE_ACCENT", "FLOAT", "NULL", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "MISC", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT"
    };
    public static final int COMMA=92;
    public static final int VT_PATTERN_TYPE=37;
    public static final int VT_ACCUMULATE_ID_CLAUSE=26;
    public static final int VK_DIALECT=52;
    public static final int VK_FUNCTION=63;
    public static final int END=89;
    public static final int HexDigit=118;
    public static final int VK_ATTRIBUTES=55;
    public static final int VT_EXPRESSION_CHAIN=28;
    public static final int VK_ACCUMULATE=79;
    public static final int MISC=114;
    public static final int VT_AND_PREFIX=21;
    public static final int VK_QUERY=61;
    public static final int THEN=111;
    public static final int VK_AUTO_FOCUS=47;
    public static final int DOT=87;
    public static final int VK_IMPORT=58;
    public static final int VT_SLOT=14;
    public static final int VT_PACKAGE_ID=38;
    public static final int LEFT_SQUARE=109;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=121;
    public static final int VT_DATA_TYPE=36;
    public static final int VT_FACT=6;
    public static final int VK_MATCHES=67;
    public static final int LEFT_CURLY=112;
    public static final int DOUBLE_AMPER=98;
    public static final int LEFT_PAREN=91;
    public static final int VT_QUERY_ID=9;
    public static final int VT_ACCESSOR_PATH=34;
    public static final int VT_LABEL=8;
    public static final int VT_ENTRYPOINT_ID=12;
    public static final int WS=116;
    public static final int VT_FIELD=33;
    public static final int VK_SALIENCE=53;
    public static final int VK_SOUNDSLIKE=69;
    public static final int VK_AND=75;
    public static final int STRING=90;
    public static final int VT_ACCESSOR_ELEMENT=35;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=25;
    public static final int VK_GLOBAL=64;
    public static final int VK_REVERSE=82;
    public static final int GRAVE_ACCENT=106;
    public static final int VK_DURATION=51;
    public static final int VT_SQUARE_CHUNK=18;
    public static final int VK_FORALL=77;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int VT_PAREN_CHUNK=19;
    public static final int VK_ENABLED=54;
    public static final int VK_RESULT=83;
    public static final int UnicodeEscape=119;
    public static final int VK_PACKAGE=59;
    public static final int VT_RULE_ID=11;
    public static final int EQUAL=100;
    public static final int VK_NO_LOOP=46;
    public static final int SEMICOLON=85;
    public static final int VK_TEMPLATE=60;
    public static final int VT_AND_IMPLICIT=20;
    public static final int NULL=108;
    public static final int COLON=94;
    public static final int MULTI_LINE_COMMENT=123;
    public static final int VT_RULE_ATTRIBUTES=15;
    public static final int RIGHT_SQUARE=110;
    public static final int VK_AGENDA_GROUP=49;
    public static final int VT_FACT_OR=31;
    public static final int VK_NOT=72;
    public static final int VK_DATE_EXPIRES=44;
    public static final int ARROW=99;
    public static final int FLOAT=107;
    public static final int VT_SLOT_ID=13;
    public static final int VT_CURLY_CHUNK=17;
    public static final int VT_OR_PREFIX=22;
    public static final int DOUBLE_PIPE=97;
    public static final int LESS=103;
    public static final int VT_PATTERN=29;
    public static final int VK_DATE_EFFECTIVE=43;
    public static final int EscapeSequence=117;
    public static final int VK_EXISTS=76;
    public static final int INT=96;
    public static final int VT_BIND_FIELD=32;
    public static final int VK_RULE=57;
    public static final int VK_EVAL=65;
    public static final int VK_COLLECT=84;
    public static final int GREATER=101;
    public static final int VT_FACT_BINDING=30;
    public static final int ID=86;
    public static final int NOT_EQUAL=105;
    public static final int RIGHT_CURLY=113;
    public static final int BOOL=95;
    public static final int VT_PARAM_LIST=42;
    public static final int VT_AND_INFIX=23;
    public static final int VK_ENTRY_POINT=71;
    public static final int VT_FROM_SOURCE=27;
    public static final int VK_LOCK_ON_ACTIVE=45;
    public static final int VK_CONTAINS=66;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VK_IN=73;
    public static final int VT_RHS_CHUNK=16;
    public static final int GREATER_EQUAL=102;
    public static final int VK_MEMBEROF=70;
    public static final int VT_OR_INFIX=24;
    public static final int DOT_STAR=88;
    public static final int VK_OR=74;
    public static final int VT_GLOBAL_ID=40;
    public static final int LESS_EQUAL=104;
    public static final int VK_WHEN=56;
    public static final int VK_RULEFLOW_GROUP=50;
    public static final int VT_FUNCTION_ID=41;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int EOL=115;
    public static final int VT_IMPORT_ID=39;
    public static final int VK_ACTIVATION_GROUP=48;
    public static final int VK_INIT=80;
    public static final int OctalEscape=120;
    public static final int VK_ACTION=81;
    public static final int VK_EXCLUDES=68;
    public static final int VK_FROM=78;
    public static final int RIGHT_PAREN=93;
    public static final int VT_TEMPLATE_ID=10;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=122;
    public static final int VK_DECLARE=62;

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
    public String getGrammarFileName() { return "/Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g"; }


    	private Stack<Map<Integer, String>> paraphrases = new Stack<Map<Integer, String>>();
    	private List<DroolsParserException> errors = new ArrayList<DroolsParserException>();
    	private DroolsParserExceptionFactory errorMessageFactory = new DroolsParserExceptionFactory(tokenNames, paraphrases);
    	private String source = "unknown";

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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:242:1: compilation_unit : ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) ;
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
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:243:2: ( ( package_statement )? ( statement )* EOF -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:243:4: ( package_statement )? ( statement )* EOF
            {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:243:4: ( package_statement )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))) {
                int LA1_1 = input.LA(2);

                if ( (LA1_1==ID) && ((((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))))) {
                    int LA1_6 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.PACKAGE))) ) {
                        alt1=1;
                    }
                }
            }
            switch (alt1) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:243:4: package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_compilation_unit400);
                    package_statement1=package_statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_package_statement.add(package_statement1.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:244:3: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:244:3: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit405);
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
            match(input,EOF,FOLLOW_EOF_in_compilation_unit410); if (failed) return retval;
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
            // 246:3: -> ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:246:6: ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_COMPILATION_UNIT, "VT_COMPILATION_UNIT"), root_1);

                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:246:28: ( package_statement )?
                if ( stream_package_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_package_statement.next());

                }
                stream_package_statement.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:246:47: ( statement )*
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:254:1: package_statement : package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) ;
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
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:257:2: ( package_key package_id ( SEMICOLON )? -> ^( package_key package_id ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:257:4: package_key package_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_package_key_in_package_statement461);
            package_key4=package_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_package_key.add(package_key4.getTree());
            pushFollow(FOLLOW_package_id_in_package_statement463);
            package_id5=package_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_package_id.add(package_id5.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:257:27: ( SEMICOLON )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==SEMICOLON) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:257:27: SEMICOLON
                    {
                    SEMICOLON6=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_package_statement465); if (failed) return retval;
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
            // 258:3: -> ^( package_key package_id )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:258:6: ^( package_key package_id )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:261:1: package_id : id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) ;
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
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:262:2: (id+= ID (id+= DOT id+= ID )* -> ^( VT_PACKAGE_ID ( ID )+ ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:262:4: id+= ID (id+= DOT id+= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_id489); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:262:11: (id+= DOT id+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:262:13: id+= DOT id+= ID
            	    {
            	    id=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_package_id495); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_package_id499); if (failed) return retval;
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
            // 264:3: -> ^( VT_PACKAGE_ID ( ID )+ )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:264:6: ^( VT_PACKAGE_ID ( ID )+ )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:267:1: statement : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | template | rule | query );
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

        rule_return rule13 = null;

        query_return query14 = null;



        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:268:2: ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | template | rule | query )
            int alt5=8;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:268:4: rule_attribute
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_attribute_in_statement527);
                    rule_attribute7=rule_attribute();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, rule_attribute7.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:269:3: {...}? => function_import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((validateLT(1, "import") && validateLT(2, "function") )) ) {
                        if (backtracking>0) {failed=true; return retval;}
                        throw new FailedPredicateException(input, "statement", "(validateLT(1, \"import\") && validateLT(2, \"function\") )");
                    }
                    pushFollow(FOLLOW_function_import_statement_in_statement534);
                    function_import_statement8=function_import_statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, function_import_statement8.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:270:4: import_statement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_import_statement_in_statement540);
                    import_statement9=import_statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, import_statement9.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:271:4: global
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_global_in_statement546);
                    global10=global();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, global10.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:272:4: function
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_function_in_statement552);
                    function11=function();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, function11.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:273:4: template
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_template_in_statement557);
                    template12=template();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, template12.getTree());

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:274:4: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_statement562);
                    rule13=rule();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, rule13.getTree());

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:275:4: query
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_query_in_statement567);
                    query14=query();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, query14.getTree());

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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:278:1: import_statement : import_key import_name[DroolsParaphareseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) ;
    public final import_statement_return import_statement() throws RecognitionException {
        import_statement_return retval = new import_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON17=null;
        import_key_return import_key15 = null;

        import_name_return import_name16 = null;


        Object SEMICOLON17_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_import_key=new RewriteRuleSubtreeStream(adaptor,"rule import_key");
        RewriteRuleSubtreeStream stream_import_name=new RewriteRuleSubtreeStream(adaptor,"rule import_name");
         pushParaphrases(DroolsParaphareseTypes.IMPORT); 
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:281:2: ( import_key import_name[DroolsParaphareseTypes.IMPORT] ( SEMICOLON )? -> ^( import_key import_name ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:281:4: import_key import_name[DroolsParaphareseTypes.IMPORT] ( SEMICOLON )?
            {
            pushFollow(FOLLOW_import_key_in_import_statement589);
            import_key15=import_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_key.add(import_key15.getTree());
            pushFollow(FOLLOW_import_name_in_import_statement591);
            import_name16=import_name(DroolsParaphareseTypes.IMPORT);
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_name.add(import_name16.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:281:58: ( SEMICOLON )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==SEMICOLON) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:281:58: SEMICOLON
                    {
                    SEMICOLON17=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_import_statement594); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON17);


                    }
                    break;

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
            // 282:3: -> ^( import_key import_name )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:282:6: ^( import_key import_name )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:285:1: function_import_statement : imp= import_key function_key import_name[DroolsParaphareseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) ;
    public final function_import_statement_return function_import_statement() throws RecognitionException {
        function_import_statement_return retval = new function_import_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON20=null;
        import_key_return imp = null;

        function_key_return function_key18 = null;

        import_name_return import_name19 = null;


        Object SEMICOLON20_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_import_key=new RewriteRuleSubtreeStream(adaptor,"rule import_key");
        RewriteRuleSubtreeStream stream_function_key=new RewriteRuleSubtreeStream(adaptor,"rule function_key");
        RewriteRuleSubtreeStream stream_import_name=new RewriteRuleSubtreeStream(adaptor,"rule import_name");
         pushParaphrases(DroolsParaphareseTypes.FUNCTION_IMPORT); 
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:288:2: (imp= import_key function_key import_name[DroolsParaphareseTypes.FUNCTION_IMPORT] ( SEMICOLON )? -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:288:4: imp= import_key function_key import_name[DroolsParaphareseTypes.FUNCTION_IMPORT] ( SEMICOLON )?
            {
            pushFollow(FOLLOW_import_key_in_function_import_statement629);
            imp=import_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_key.add(imp.getTree());
            pushFollow(FOLLOW_function_key_in_function_import_statement631);
            function_key18=function_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_function_key.add(function_key18.getTree());
            pushFollow(FOLLOW_import_name_in_function_import_statement633);
            import_name19=import_name(DroolsParaphareseTypes.FUNCTION_IMPORT);
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_import_name.add(import_name19.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:288:84: ( SEMICOLON )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==SEMICOLON) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:288:84: SEMICOLON
                    {
                    SEMICOLON20=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_function_import_statement636); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON20);


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
            // 289:3: -> ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:289:6: ^( VT_FUNCTION_IMPORT[$imp.start] function_key import_name )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:292:1: import_name[int importType] : id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) ;
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
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:293:2: (id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )? -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:293:4: id+= ID (id+= DOT id+= ID )* (id+= DOT_STAR )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name665); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:293:11: (id+= DOT id+= ID )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==DOT) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:293:13: id+= DOT id+= ID
            	    {
            	    id=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_import_name671); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_import_name675); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:293:33: (id+= DOT_STAR )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==DOT_STAR) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:293:33: id+= DOT_STAR
                    {
                    id=(Token)input.LT(1);
                    match(input,DOT_STAR,FOLLOW_DOT_STAR_in_import_name682); if (failed) return retval;
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
            // 295:3: -> ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:295:6: ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
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
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:295:25: ( DOT_STAR )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:298:1: global : global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) ;
    public final global_return global() throws RecognitionException {
        global_return retval = new global_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON24=null;
        global_key_return global_key21 = null;

        data_type_return data_type22 = null;

        global_id_return global_id23 = null;


        Object SEMICOLON24_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        RewriteRuleSubtreeStream stream_global_id=new RewriteRuleSubtreeStream(adaptor,"rule global_id");
        RewriteRuleSubtreeStream stream_global_key=new RewriteRuleSubtreeStream(adaptor,"rule global_key");
         pushParaphrases(DroolsParaphareseTypes.GLOBAL); 
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:301:2: ( global_key data_type global_id ( SEMICOLON )? -> ^( global_key data_type global_id ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:301:4: global_key data_type global_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_global_key_in_global722);
            global_key21=global_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_global_key.add(global_key21.getTree());
            pushFollow(FOLLOW_data_type_in_global724);
            data_type22=data_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_data_type.add(data_type22.getTree());
            pushFollow(FOLLOW_global_id_in_global726);
            global_id23=global_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_global_id.add(global_id23.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:301:35: ( SEMICOLON )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==SEMICOLON) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:301:35: SEMICOLON
                    {
                    SEMICOLON24=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_global728); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON24);


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
            // 302:3: -> ^( global_key data_type global_id )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:302:6: ^( global_key data_type global_id )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:305:1: global_id : id= ID -> VT_GLOBAL_ID[$id] ;
    public final global_id_return global_id() throws RecognitionException {
        global_id_return retval = new global_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:306:2: (id= ID -> VT_GLOBAL_ID[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:306:4: id= ID
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global_id754); if (failed) return retval;
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
            // 308:3: -> VT_GLOBAL_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:311:1: function : function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) ;
    public final function_return function() throws RecognitionException {
        function_return retval = new function_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        function_key_return function_key25 = null;

        data_type_return data_type26 = null;

        function_id_return function_id27 = null;

        parameters_return parameters28 = null;

        curly_chunk_return curly_chunk29 = null;


        RewriteRuleSubtreeStream stream_parameters=new RewriteRuleSubtreeStream(adaptor,"rule parameters");
        RewriteRuleSubtreeStream stream_function_key=new RewriteRuleSubtreeStream(adaptor,"rule function_key");
        RewriteRuleSubtreeStream stream_curly_chunk=new RewriteRuleSubtreeStream(adaptor,"rule curly_chunk");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        RewriteRuleSubtreeStream stream_function_id=new RewriteRuleSubtreeStream(adaptor,"rule function_id");
         pushParaphrases(DroolsParaphareseTypes.FUNCTION); 
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:314:2: ( function_key ( data_type )? function_id parameters curly_chunk -> ^( function_key ( data_type )? function_id parameters curly_chunk ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:314:4: function_key ( data_type )? function_id parameters curly_chunk
            {
            pushFollow(FOLLOW_function_key_in_function786);
            function_key25=function_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_function_key.add(function_key25.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:314:17: ( data_type )?
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
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:314:17: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function788);
                    data_type26=data_type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_data_type.add(data_type26.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_function_id_in_function791);
            function_id27=function_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_function_id.add(function_id27.getTree());
            pushFollow(FOLLOW_parameters_in_function793);
            parameters28=parameters();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_parameters.add(parameters28.getTree());
            pushFollow(FOLLOW_curly_chunk_in_function795);
            curly_chunk29=curly_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_curly_chunk.add(curly_chunk29.getTree());

            // AST REWRITE
            // elements: curly_chunk, parameters, function_id, data_type, function_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 315:3: -> ^( function_key ( data_type )? function_id parameters curly_chunk )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:315:6: ^( function_key ( data_type )? function_id parameters curly_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_function_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:315:21: ( data_type )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:318:1: function_id : id= ID -> VT_FUNCTION_ID[$id] ;
    public final function_id_return function_id() throws RecognitionException {
        function_id_return retval = new function_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:319:2: (id= ID -> VT_FUNCTION_ID[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:319:4: id= ID
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function_id825); if (failed) return retval;
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
            // 321:3: -> VT_FUNCTION_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:324:1: query : query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block END ) ;
    public final query_return query() throws RecognitionException {
        query_return retval = new query_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token END34=null;
        Token SEMICOLON35=null;
        query_key_return query_key30 = null;

        query_id_return query_id31 = null;

        parameters_return parameters32 = null;

        normal_lhs_block_return normal_lhs_block33 = null;


        Object END34_tree=null;
        Object SEMICOLON35_tree=null;
        RewriteRuleTokenStream stream_END=new RewriteRuleTokenStream(adaptor,"token END");
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_parameters=new RewriteRuleSubtreeStream(adaptor,"rule parameters");
        RewriteRuleSubtreeStream stream_query_key=new RewriteRuleSubtreeStream(adaptor,"rule query_key");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        RewriteRuleSubtreeStream stream_query_id=new RewriteRuleSubtreeStream(adaptor,"rule query_id");
         pushParaphrases(DroolsParaphareseTypes.QUERY); 
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:2: ( query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )? -> ^( query_key query_id ( parameters )? normal_lhs_block END ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:4: query_key query_id ( parameters )? normal_lhs_block END ( SEMICOLON )?
            {
            pushFollow(FOLLOW_query_key_in_query857);
            query_key30=query_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_query_key.add(query_key30.getTree());
            pushFollow(FOLLOW_query_id_in_query859);
            query_id31=query_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_query_id.add(query_id31.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:23: ( parameters )?
            int alt12=2;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:23: parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query861);
                    parameters32=parameters();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_parameters.add(parameters32.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_normal_lhs_block_in_query864);
            normal_lhs_block33=normal_lhs_block();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block33.getTree());
            END34=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query866); if (failed) return retval;
            if ( backtracking==0 ) stream_END.add(END34);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:56: ( SEMICOLON )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==SEMICOLON) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:56: SEMICOLON
                    {
                    SEMICOLON35=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_query868); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON35);


                    }
                    break;

            }


            // AST REWRITE
            // elements: normal_lhs_block, query_id, END, query_key, parameters
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 328:3: -> ^( query_key query_id ( parameters )? normal_lhs_block END )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:328:6: ^( query_key query_id ( parameters )? normal_lhs_block END )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_query_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_query_id.next());
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:328:27: ( parameters )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:331:1: query_id : (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] );
    public final query_id_return query_id() throws RecognitionException {
        query_id_return retval = new query_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:332:2: (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] )
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
                    new NoViableAltException("331:1: query_id : (id= ID -> VT_QUERY_ID[$id] | id= STRING -> VT_QUERY_ID[$id] );", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:332:5: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_query_id900); if (failed) return retval;
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
                    // 333:67: -> VT_QUERY_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_QUERY_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:334:5: id= STRING
                    {
                    id=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_query_id916); if (failed) return retval;
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
                    // 335:67: -> VT_QUERY_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:338:1: parameters : LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) ;
    public final parameters_return parameters() throws RecognitionException {
        parameters_return retval = new parameters_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN36=null;
        Token COMMA38=null;
        Token RIGHT_PAREN40=null;
        param_definition_return param_definition37 = null;

        param_definition_return param_definition39 = null;


        Object LEFT_PAREN36_tree=null;
        Object COMMA38_tree=null;
        Object RIGHT_PAREN40_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_param_definition=new RewriteRuleSubtreeStream(adaptor,"rule param_definition");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:339:2: ( LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:339:4: LEFT_PAREN ( param_definition ( COMMA param_definition )* )? RIGHT_PAREN
            {
            LEFT_PAREN36=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parameters935); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN36);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:340:4: ( param_definition ( COMMA param_definition )* )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==ID) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:340:6: param_definition ( COMMA param_definition )*
                    {
                    pushFollow(FOLLOW_param_definition_in_parameters942);
                    param_definition37=param_definition();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_param_definition.add(param_definition37.getTree());
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:340:23: ( COMMA param_definition )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==COMMA) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:340:24: COMMA param_definition
                    	    {
                    	    COMMA38=(Token)input.LT(1);
                    	    match(input,COMMA,FOLLOW_COMMA_in_parameters945); if (failed) return retval;
                    	    if ( backtracking==0 ) stream_COMMA.add(COMMA38);

                    	    pushFollow(FOLLOW_param_definition_in_parameters947);
                    	    param_definition39=param_definition();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_param_definition.add(param_definition39.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    }
                    break;

            }

            RIGHT_PAREN40=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parameters956); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN40);


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
            // 342:3: -> ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:342:6: ^( VT_PARAM_LIST ( param_definition )* RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_PARAM_LIST, "VT_PARAM_LIST"), root_1);

                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:342:22: ( param_definition )*
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:345:1: param_definition : ( data_type )? argument ;
    public final param_definition_return param_definition() throws RecognitionException {
        param_definition_return retval = new param_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        data_type_return data_type41 = null;

        argument_return argument42 = null;



        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:346:2: ( ( data_type )? argument )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:346:4: ( data_type )? argument
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:346:4: ( data_type )?
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:346:4: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition980);
                    data_type41=data_type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, data_type41.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition983);
            argument42=argument();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, argument42.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:349:1: argument : ID ( dimension_definition )* ;
    public final argument_return argument() throws RecognitionException {
        argument_return retval = new argument_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID43=null;
        dimension_definition_return dimension_definition44 = null;


        Object ID43_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:350:2: ( ID ( dimension_definition )* )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:350:4: ID ( dimension_definition )*
            {
            root_0 = (Object)adaptor.nil();

            ID43=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument994); if (failed) return retval;
            if ( backtracking==0 ) {
            ID43_tree = (Object)adaptor.create(ID43);
            adaptor.addChild(root_0, ID43_tree);
            }
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:350:7: ( dimension_definition )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==LEFT_SQUARE) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:350:7: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_argument996);
            	    dimension_definition44=dimension_definition();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, dimension_definition44.getTree());

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

    public static class template_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start template
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:354:1: template : template_key template_id ( SEMICOLON )? ( template_slot )+ END ( SEMICOLON )? -> ^( template_key template_id ( template_slot )+ END ) ;
    public final template_return template() throws RecognitionException {
        template_return retval = new template_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON47=null;
        Token END49=null;
        Token SEMICOLON50=null;
        template_key_return template_key45 = null;

        template_id_return template_id46 = null;

        template_slot_return template_slot48 = null;


        Object SEMICOLON47_tree=null;
        Object END49_tree=null;
        Object SEMICOLON50_tree=null;
        RewriteRuleTokenStream stream_END=new RewriteRuleTokenStream(adaptor,"token END");
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_template_id=new RewriteRuleSubtreeStream(adaptor,"rule template_id");
        RewriteRuleSubtreeStream stream_template_slot=new RewriteRuleSubtreeStream(adaptor,"rule template_slot");
        RewriteRuleSubtreeStream stream_template_key=new RewriteRuleSubtreeStream(adaptor,"rule template_key");
         pushParaphrases(DroolsParaphareseTypes.TEMPLATE); 
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:357:2: ( template_key template_id ( SEMICOLON )? ( template_slot )+ END ( SEMICOLON )? -> ^( template_key template_id ( template_slot )+ END ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:357:4: template_key template_id ( SEMICOLON )? ( template_slot )+ END ( SEMICOLON )?
            {
            pushFollow(FOLLOW_template_key_in_template1020);
            template_key45=template_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_template_key.add(template_key45.getTree());
            pushFollow(FOLLOW_template_id_in_template1022);
            template_id46=template_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_template_id.add(template_id46.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:357:29: ( SEMICOLON )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==SEMICOLON) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:357:29: SEMICOLON
                    {
                    SEMICOLON47=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1024); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON47);


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:358:3: ( template_slot )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==ID) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:358:3: template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template1029);
            	    template_slot48=template_slot();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_template_slot.add(template_slot48.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
            	    if (backtracking>0) {failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
            } while (true);

            END49=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_template1034); if (failed) return retval;
            if ( backtracking==0 ) stream_END.add(END49);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:359:7: ( SEMICOLON )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==SEMICOLON) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:359:7: SEMICOLON
                    {
                    SEMICOLON50=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template1036); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON50);


                    }
                    break;

            }


            // AST REWRITE
            // elements: template_key, template_slot, template_id, END
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 360:3: -> ^( template_key template_id ( template_slot )+ END )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:360:6: ^( template_key template_id ( template_slot )+ END )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:363:1: template_id : (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] );
    public final template_id_return template_id() throws RecognitionException {
        template_id_return retval = new template_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:364:2: (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==ID) ) {
                alt22=1;
            }
            else if ( (LA22_0==STRING) ) {
                alt22=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("363:1: template_id : (id= ID -> VT_TEMPLATE_ID[$id] | id= STRING -> VT_TEMPLATE_ID[$id] );", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:364:5: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_template_id1066); if (failed) return retval;
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
                    // 365:70: -> VT_TEMPLATE_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_TEMPLATE_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:366:5: id= STRING
                    {
                    id=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_template_id1082); if (failed) return retval;
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
                    // 367:70: -> VT_TEMPLATE_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:370:1: template_slot : data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) ;
    public final template_slot_return template_slot() throws RecognitionException {
        template_slot_return retval = new template_slot_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMICOLON53=null;
        data_type_return data_type51 = null;

        slot_id_return slot_id52 = null;


        Object SEMICOLON53_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleSubtreeStream stream_slot_id=new RewriteRuleSubtreeStream(adaptor,"rule slot_id");
        RewriteRuleSubtreeStream stream_data_type=new RewriteRuleSubtreeStream(adaptor,"rule data_type");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:2: ( data_type slot_id ( SEMICOLON )? -> ^( VT_SLOT data_type slot_id ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:5: data_type slot_id ( SEMICOLON )?
            {
            pushFollow(FOLLOW_data_type_in_template_slot1102);
            data_type51=data_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_data_type.add(data_type51.getTree());
            pushFollow(FOLLOW_slot_id_in_template_slot1104);
            slot_id52=slot_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_slot_id.add(slot_id52.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:23: ( SEMICOLON )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==SEMICOLON) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:23: SEMICOLON
                    {
                    SEMICOLON53=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_template_slot1106); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEMICOLON.add(SEMICOLON53);


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
            // 372:3: -> ^( VT_SLOT data_type slot_id )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:372:6: ^( VT_SLOT data_type slot_id )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:375:1: slot_id : id= ID -> VT_SLOT_ID[$id] ;
    public final slot_id_return slot_id() throws RecognitionException {
        slot_id_return retval = new slot_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:375:9: (id= ID -> VT_SLOT_ID[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:375:11: id= ID
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_slot_id1131); if (failed) return retval;
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
            // 376:3: -> VT_SLOT_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:379:1: rule : rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk ) ;
    public final rule_return rule() throws RecognitionException {
        rule_return retval = new rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        rule_key_return rule_key54 = null;

        rule_id_return rule_id55 = null;

        rule_attributes_return rule_attributes56 = null;

        when_part_return when_part57 = null;

        rhs_chunk_return rhs_chunk58 = null;


        RewriteRuleSubtreeStream stream_rule_key=new RewriteRuleSubtreeStream(adaptor,"rule rule_key");
        RewriteRuleSubtreeStream stream_rule_id=new RewriteRuleSubtreeStream(adaptor,"rule rule_id");
        RewriteRuleSubtreeStream stream_when_part=new RewriteRuleSubtreeStream(adaptor,"rule when_part");
        RewriteRuleSubtreeStream stream_rule_attributes=new RewriteRuleSubtreeStream(adaptor,"rule rule_attributes");
        RewriteRuleSubtreeStream stream_rhs_chunk=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk");
         pushParaphrases(DroolsParaphareseTypes.RULE); 
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:2: ( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:4: rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk
            {
            pushFollow(FOLLOW_rule_key_in_rule1160);
            rule_key54=rule_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rule_key.add(rule_key54.getTree());
            pushFollow(FOLLOW_rule_id_in_rule1162);
            rule_id55=rule_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rule_id.add(rule_id55.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:21: ( rule_attributes )?
            int alt24=2;
            alt24 = dfa24.predict(input);
            switch (alt24) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:21: rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1164);
                    rule_attributes56=rule_attributes();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_rule_attributes.add(rule_attributes56.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:38: ( when_part )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:38: when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule1167);
                    when_part57=when_part();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_when_part.add(when_part57.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1170);
            rhs_chunk58=rhs_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rhs_chunk.add(rhs_chunk58.getTree());

            // AST REWRITE
            // elements: when_part, rhs_chunk, rule_id, rule_key, rule_attributes
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 383:3: -> ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:6: ^( rule_key rule_id ( rule_attributes )? ( when_part )? rhs_chunk )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_rule_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rule_id.next());
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:25: ( rule_attributes )?
                if ( stream_rule_attributes.hasNext() ) {
                    adaptor.addChild(root_1, stream_rule_attributes.next());

                }
                stream_rule_attributes.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:42: ( when_part )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:386:1: when_part : when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block ;
    public final when_part_return when_part() throws RecognitionException {
        when_part_return retval = new when_part_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON60=null;
        when_key_return when_key59 = null;

        normal_lhs_block_return normal_lhs_block61 = null;


        Object COLON60_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_normal_lhs_block=new RewriteRuleSubtreeStream(adaptor,"rule normal_lhs_block");
        RewriteRuleSubtreeStream stream_when_key=new RewriteRuleSubtreeStream(adaptor,"rule when_key");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:387:2: ( when_key ( COLON )? normal_lhs_block -> when_key normal_lhs_block )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:387:4: when_key ( COLON )? normal_lhs_block
            {
            pushFollow(FOLLOW_when_key_in_when_part1199);
            when_key59=when_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_when_key.add(when_key59.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:387:13: ( COLON )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==COLON) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:387:13: COLON
                    {
                    COLON60=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_when_part1201); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON60);


                    }
                    break;

            }

            pushFollow(FOLLOW_normal_lhs_block_in_when_part1204);
            normal_lhs_block61=normal_lhs_block();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_normal_lhs_block.add(normal_lhs_block61.getTree());

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
            // 388:2: -> when_key normal_lhs_block
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:391:1: rule_id : (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] );
    public final rule_id_return rule_id() throws RecognitionException {
        rule_id_return retval = new rule_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:392:2: (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==ID) ) {
                alt27=1;
            }
            else if ( (LA27_0==STRING) ) {
                alt27=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("391:1: rule_id : (id= ID -> VT_RULE_ID[$id] | id= STRING -> VT_RULE_ID[$id] );", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:392:5: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_rule_id1225); if (failed) return retval;
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
                    // 393:66: -> VT_RULE_ID[$id]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_RULE_ID, id));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:394:5: id= STRING
                    {
                    id=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_rule_id1241); if (failed) return retval;
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
                    // 395:66: -> VT_RULE_ID[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:398:1: rule_attributes : ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) ;
    public final rule_attributes_return rule_attributes() throws RecognitionException {
        rule_attributes_return retval = new rule_attributes_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON63=null;
        Token COMMA65=null;
        rule_attribute_return attr = null;

        attributes_key_return attributes_key62 = null;

        rule_attribute_return rule_attribute64 = null;


        Object COLON63_tree=null;
        Object COMMA65_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_attributes_key=new RewriteRuleSubtreeStream(adaptor,"rule attributes_key");
        RewriteRuleSubtreeStream stream_rule_attribute=new RewriteRuleSubtreeStream(adaptor,"rule rule_attribute");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:399:2: ( ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )* -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:399:4: ( attributes_key COLON )? rule_attribute ( ( COMMA )? attr= rule_attribute )*
            {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:399:4: ( attributes_key COLON )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {
                int LA28_1 = input.LA(2);

                if ( (LA28_1==COLON) && ((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES)))) {
                    alt28=1;
                }
            }
            switch (alt28) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:399:6: attributes_key COLON
                    {
                    pushFollow(FOLLOW_attributes_key_in_rule_attributes1262);
                    attributes_key62=attributes_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_attributes_key.add(attributes_key62.getTree());
                    COLON63=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_rule_attributes1264); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON63);


                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1269);
            rule_attribute64=rule_attribute();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_rule_attribute.add(rule_attribute64.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:399:45: ( ( COMMA )? attr= rule_attribute )*
            loop30:
            do {
                int alt30=2;
                alt30 = dfa30.predict(input);
                switch (alt30) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:399:47: ( COMMA )? attr= rule_attribute
            	    {
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:399:47: ( COMMA )?
            	    int alt29=2;
            	    int LA29_0 = input.LA(1);

            	    if ( (LA29_0==COMMA) ) {
            	        alt29=1;
            	    }
            	    switch (alt29) {
            	        case 1 :
            	            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:399:47: COMMA
            	            {
            	            COMMA65=(Token)input.LT(1);
            	            match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1273); if (failed) return retval;
            	            if ( backtracking==0 ) stream_COMMA.add(COMMA65);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1278);
            	    attr=rule_attribute();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_rule_attribute.add(attr.getTree());

            	    }
            	    break;

            	default :
            	    break loop30;
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
            // 400:3: -> ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:400:6: ^( VT_RULE_ATTRIBUTES ( attributes_key )? ( rule_attribute )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_RULE_ATTRIBUTES, "VT_RULE_ATTRIBUTES"), root_1);

                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:400:27: ( attributes_key )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:403:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );
    public final rule_attribute_return rule_attribute() throws RecognitionException {
        rule_attribute_return retval = new rule_attribute_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        salience_return salience66 = null;

        no_loop_return no_loop67 = null;

        agenda_group_return agenda_group68 = null;

        duration_return duration69 = null;

        activation_group_return activation_group70 = null;

        auto_focus_return auto_focus71 = null;

        date_effective_return date_effective72 = null;

        date_expires_return date_expires73 = null;

        enabled_return enabled74 = null;

        ruleflow_group_return ruleflow_group75 = null;

        lock_on_active_return lock_on_active76 = null;

        dialect_return dialect77 = null;



         pushParaphrases(DroolsParaphareseTypes.RULE_ATTRIBUTE); 
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:406:2: ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect )
            int alt31=12;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                int LA31_1 = input.LA(2);

                if ( (LA31_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                    int LA31_2 = input.LA(3);

                    if ( (LA31_2==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                        int LA31_7 = input.LA(4);

                        if ( (LA31_7==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE)))) {
                            alt31=11;
                        }
                        else if ( (LA31_7==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {
                            int LA31_10 = input.LA(5);

                            if ( ((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                                alt31=3;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                                alt31=5;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))) ) {
                                alt31=7;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))) ) {
                                alt31=8;
                            }
                            else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                                alt31=10;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("403:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 31, 10, input);

                                throw nvae;
                            }
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))) ) {
                            alt31=2;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))) ) {
                            alt31=6;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("403:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 31, 7, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("403:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 31, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA31_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {
                    int LA31_3 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {
                        alt31=1;
                    }
                    else if ( ((validateIdentifierKey(DroolsSoftKeywords.DURATION))) ) {
                        alt31=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("403:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 31, 3, input);

                        throw nvae;
                    }
                }
                else if ( (LA31_1==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {
                    alt31=1;
                }
                else if ( (LA31_1==STRING) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {
                    alt31=12;
                }
                else if ( (LA31_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {
                    alt31=9;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("403:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 31, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("403:1: rule_attribute : ( salience | no_loop | agenda_group | duration | activation_group | auto_focus | date_effective | date_expires | enabled | ruleflow_group | lock_on_active | dialect );", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:406:4: salience
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_salience_in_rule_attribute1317);
                    salience66=salience();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, salience66.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:407:4: no_loop
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_no_loop_in_rule_attribute1323);
                    no_loop67=no_loop();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, no_loop67.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:408:4: agenda_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1330);
                    agenda_group68=agenda_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, agenda_group68.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:409:4: duration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_duration_in_rule_attribute1337);
                    duration69=duration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, duration69.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:410:4: activation_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_activation_group_in_rule_attribute1344);
                    activation_group70=activation_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, activation_group70.getTree());

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:411:4: auto_focus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1350);
                    auto_focus71=auto_focus();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, auto_focus71.getTree());

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:412:4: date_effective
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_effective_in_rule_attribute1356);
                    date_effective72=date_effective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, date_effective72.getTree());

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:4: date_expires
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_expires_in_rule_attribute1362);
                    date_expires73=date_expires();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, date_expires73.getTree());

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:4: enabled
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enabled_in_rule_attribute1368);
                    enabled74=enabled();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, enabled74.getTree());

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:415:4: ruleflow_group
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1374);
                    ruleflow_group75=ruleflow_group();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, ruleflow_group75.getTree());

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:416:4: lock_on_active
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1380);
                    lock_on_active76=lock_on_active();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lock_on_active76.getTree());

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:417:4: dialect
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_dialect_in_rule_attribute1385);
                    dialect77=dialect();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, dialect77.getTree());

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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:420:1: date_effective : date_effective_key STRING ;
    public final date_effective_return date_effective() throws RecognitionException {
        date_effective_return retval = new date_effective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING79=null;
        date_effective_key_return date_effective_key78 = null;


        Object STRING79_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:421:2: ( date_effective_key STRING )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:421:4: date_effective_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_effective_key_in_date_effective1397);
            date_effective_key78=date_effective_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_effective_key78.getTree(), root_0);
            STRING79=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1400); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING79_tree = (Object)adaptor.create(STRING79);
            adaptor.addChild(root_0, STRING79_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:424:1: date_expires : date_expires_key STRING ;
    public final date_expires_return date_expires() throws RecognitionException {
        date_expires_return retval = new date_expires_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING81=null;
        date_expires_key_return date_expires_key80 = null;


        Object STRING81_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:425:2: ( date_expires_key STRING )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:425:4: date_expires_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_date_expires_key_in_date_expires1411);
            date_expires_key80=date_expires_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(date_expires_key80.getTree(), root_0);
            STRING81=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1414); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING81_tree = (Object)adaptor.create(STRING81);
            adaptor.addChild(root_0, STRING81_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:428:1: enabled : enabled_key BOOL ;
    public final enabled_return enabled() throws RecognitionException {
        enabled_return retval = new enabled_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL83=null;
        enabled_key_return enabled_key82 = null;


        Object BOOL83_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:429:2: ( enabled_key BOOL )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:429:4: enabled_key BOOL
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_enabled_key_in_enabled1428);
            enabled_key82=enabled_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(enabled_key82.getTree(), root_0);
            BOOL83=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1431); if (failed) return retval;
            if ( backtracking==0 ) {
            BOOL83_tree = (Object)adaptor.create(BOOL83);
            adaptor.addChild(root_0, BOOL83_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:432:1: salience : salience_key ( INT | paren_chunk ) ;
    public final salience_return salience() throws RecognitionException {
        salience_return retval = new salience_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT85=null;
        salience_key_return salience_key84 = null;

        paren_chunk_return paren_chunk86 = null;


        Object INT85_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:433:2: ( salience_key ( INT | paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:433:4: salience_key ( INT | paren_chunk )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_salience_key_in_salience1443);
            salience_key84=salience_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(salience_key84.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:434:3: ( INT | paren_chunk )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==INT) ) {
                alt32=1;
            }
            else if ( (LA32_0==LEFT_PAREN) ) {
                alt32=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("434:3: ( INT | paren_chunk )", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:434:5: INT
                    {
                    INT85=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_salience1450); if (failed) return retval;
                    if ( backtracking==0 ) {
                    INT85_tree = (Object)adaptor.create(INT85);
                    adaptor.addChild(root_0, INT85_tree);
                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:435:5: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1459);
                    paren_chunk86=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk86.getTree());

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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:439:1: no_loop : no_loop_key ( BOOL )? ;
    public final no_loop_return no_loop() throws RecognitionException {
        no_loop_return retval = new no_loop_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL88=null;
        no_loop_key_return no_loop_key87 = null;


        Object BOOL88_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:2: ( no_loop_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:4: no_loop_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_no_loop_key_in_no_loop1475);
            no_loop_key87=no_loop_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(no_loop_key87.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:17: ( BOOL )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==BOOL) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:17: BOOL
                    {
                    BOOL88=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1478); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL88_tree = (Object)adaptor.create(BOOL88);
                    adaptor.addChild(root_0, BOOL88_tree);
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:443:1: auto_focus : auto_focus_key ( BOOL )? ;
    public final auto_focus_return auto_focus() throws RecognitionException {
        auto_focus_return retval = new auto_focus_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL90=null;
        auto_focus_key_return auto_focus_key89 = null;


        Object BOOL90_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:444:2: ( auto_focus_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:444:4: auto_focus_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_auto_focus_key_in_auto_focus1490);
            auto_focus_key89=auto_focus_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(auto_focus_key89.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:444:20: ( BOOL )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==BOOL) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:444:20: BOOL
                    {
                    BOOL90=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1493); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL90_tree = (Object)adaptor.create(BOOL90);
                    adaptor.addChild(root_0, BOOL90_tree);
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:447:1: activation_group : activation_group_key STRING ;
    public final activation_group_return activation_group() throws RecognitionException {
        activation_group_return retval = new activation_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING92=null;
        activation_group_key_return activation_group_key91 = null;


        Object STRING92_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:448:2: ( activation_group_key STRING )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:448:4: activation_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_activation_group_key_in_activation_group1507);
            activation_group_key91=activation_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(activation_group_key91.getTree(), root_0);
            STRING92=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1510); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING92_tree = (Object)adaptor.create(STRING92);
            adaptor.addChild(root_0, STRING92_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:451:1: ruleflow_group : ruleflow_group_key STRING ;
    public final ruleflow_group_return ruleflow_group() throws RecognitionException {
        ruleflow_group_return retval = new ruleflow_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING94=null;
        ruleflow_group_key_return ruleflow_group_key93 = null;


        Object STRING94_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:2: ( ruleflow_group_key STRING )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:4: ruleflow_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_ruleflow_group_key_in_ruleflow_group1521);
            ruleflow_group_key93=ruleflow_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(ruleflow_group_key93.getTree(), root_0);
            STRING94=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1524); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING94_tree = (Object)adaptor.create(STRING94);
            adaptor.addChild(root_0, STRING94_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:455:1: agenda_group : agenda_group_key STRING ;
    public final agenda_group_return agenda_group() throws RecognitionException {
        agenda_group_return retval = new agenda_group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING96=null;
        agenda_group_key_return agenda_group_key95 = null;


        Object STRING96_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:456:2: ( agenda_group_key STRING )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:456:4: agenda_group_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_agenda_group_key_in_agenda_group1535);
            agenda_group_key95=agenda_group_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(agenda_group_key95.getTree(), root_0);
            STRING96=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1538); if (failed) return retval;
            if ( backtracking==0 ) {
            STRING96_tree = (Object)adaptor.create(STRING96);
            adaptor.addChild(root_0, STRING96_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:459:1: duration : duration_key INT ;
    public final duration_return duration() throws RecognitionException {
        duration_return retval = new duration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT98=null;
        duration_key_return duration_key97 = null;


        Object INT98_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:460:2: ( duration_key INT )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:460:4: duration_key INT
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_duration_key_in_duration1549);
            duration_key97=duration_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(duration_key97.getTree(), root_0);
            INT98=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1552); if (failed) return retval;
            if ( backtracking==0 ) {
            INT98_tree = (Object)adaptor.create(INT98);
            adaptor.addChild(root_0, INT98_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:463:1: dialect : dialect_key STRING ;
    public final dialect_return dialect() throws RecognitionException {
        dialect_return retval = new dialect_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING100=null;
        dialect_key_return dialect_key99 = null;


        Object STRING100_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:464:2: ( dialect_key STRING )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:464:4: dialect_key STRING
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_dialect_key_in_dialect1566);
            dialect_key99=dialect_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(dialect_key99.getTree(), root_0);
            STRING100=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1569); if (failed) return retval;
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
    // $ANTLR end dialect

    public static class lock_on_active_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lock_on_active
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:467:1: lock_on_active : lock_on_active_key ( BOOL )? ;
    public final lock_on_active_return lock_on_active() throws RecognitionException {
        lock_on_active_return retval = new lock_on_active_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BOOL102=null;
        lock_on_active_key_return lock_on_active_key101 = null;


        Object BOOL102_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:2: ( lock_on_active_key ( BOOL )? )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:4: lock_on_active_key ( BOOL )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lock_on_active_key_in_lock_on_active1587);
            lock_on_active_key101=lock_on_active_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(lock_on_active_key101.getTree(), root_0);
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:24: ( BOOL )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==BOOL) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:24: BOOL
                    {
                    BOOL102=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1590); if (failed) return retval;
                    if ( backtracking==0 ) {
                    BOOL102_tree = (Object)adaptor.create(BOOL102);
                    adaptor.addChild(root_0, BOOL102_tree);
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:471:1: normal_lhs_block : ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) ;
    public final normal_lhs_block_return normal_lhs_block() throws RecognitionException {
        normal_lhs_block_return retval = new normal_lhs_block_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        lhs_return lhs103 = null;


        RewriteRuleSubtreeStream stream_lhs=new RewriteRuleSubtreeStream(adaptor,"rule lhs");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:472:2: ( ( lhs )* -> ^( VT_AND_IMPLICIT ( lhs )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:472:4: ( lhs )*
            {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:472:4: ( lhs )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==ID||LA36_0==LEFT_PAREN) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:472:4: lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1602);
            	    lhs103=lhs();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_lhs.add(lhs103.getTree());

            	    }
            	    break;

            	default :
            	    break loop36;
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
            // 473:2: -> ^( VT_AND_IMPLICIT ( lhs )* )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:473:5: ^( VT_AND_IMPLICIT ( lhs )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_AND_IMPLICIT, "VT_AND_IMPLICIT"), root_1);

                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:473:23: ( lhs )*
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:476:1: lhs : lhs_or ;
    public final lhs_return lhs() throws RecognitionException {
        lhs_return retval = new lhs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        lhs_or_return lhs_or104 = null;



        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:476:5: ( lhs_or )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:476:7: lhs_or
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_or_in_lhs1623);
            lhs_or104=lhs_or();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, lhs_or104.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:479:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );
    public final lhs_or_return lhs_or() throws RecognitionException {
        lhs_or_return retval = new lhs_or_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        Token LEFT_PAREN105=null;
        Token RIGHT_PAREN107=null;
        or_key_return or = null;

        or_key_return value = null;

        lhs_and_return lhs_and106 = null;

        lhs_and_return lhs_and108 = null;

        lhs_and_return lhs_and109 = null;


        Object pipe_tree=null;
        Object LEFT_PAREN105_tree=null;
        Object RIGHT_PAREN107_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");
        RewriteRuleSubtreeStream stream_lhs_and=new RewriteRuleSubtreeStream(adaptor,"rule lhs_and");

        	Token orToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:3: ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* )
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==LEFT_PAREN) ) {
                int LA40_1 = input.LA(2);

                if ( (LA40_1==LEFT_PAREN) ) {
                    alt40=2;
                }
                else if ( (LA40_1==ID) ) {
                    switch ( input.LA(3) ) {
                    case DOT:
                    case COLON:
                    case LEFT_SQUARE:
                        {
                        alt40=2;
                        }
                        break;
                    case ID:
                        {
                        int LA40_4 = input.LA(4);

                        if ( (synpred1()) ) {
                            alt40=1;
                        }
                        else if ( (true) ) {
                            alt40=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("479:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 40, 4, input);

                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_PAREN:
                        {
                        int LA40_5 = input.LA(4);

                        if ( (synpred1()) ) {
                            alt40=1;
                        }
                        else if ( (true) ) {
                            alt40=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("479:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 40, 5, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("479:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 40, 3, input);

                        throw nvae;
                    }

                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("479:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 40, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA40_0==ID) ) {
                alt40=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("479:1: lhs_or : ( ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN ) | ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )* );", 40, 0, input);

                throw nvae;
            }
            switch (alt40) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:5: ( LEFT_PAREN or_key )=> LEFT_PAREN or= or_key ( lhs_and )+ RIGHT_PAREN
                    {
                    LEFT_PAREN105=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or1644); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN105);

                    pushFollow(FOLLOW_or_key_in_lhs_or1648);
                    or=or_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_or_key.add(or.getTree());
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:48: ( lhs_and )+
                    int cnt37=0;
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( (LA37_0==ID||LA37_0==LEFT_PAREN) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:48: lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1650);
                    	    lhs_and106=lhs_and();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_and.add(lhs_and106.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt37 >= 1 ) break loop37;
                    	    if (backtracking>0) {failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(37, input);
                                throw eee;
                        }
                        cnt37++;
                    } while (true);

                    RIGHT_PAREN107=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or1653); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN107);


                    // AST REWRITE
                    // elements: lhs_and, RIGHT_PAREN
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 483:3: -> ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
                    {
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:483:6: ^( VT_OR_PREFIX[$or.start] ( lhs_and )+ RIGHT_PAREN )
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
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:4: ( lhs_and -> lhs_and ) ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    {
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:4: ( lhs_and -> lhs_and )
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:5: lhs_and
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or1674);
                    lhs_and108=lhs_and();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_and.add(lhs_and108.getTree());

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
                    // 484:13: -> lhs_and
                    {
                        adaptor.addChild(root_0, stream_lhs_and.next());

                    }

                    }

                    }

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:3: ( ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and ) )*
                    loop39:
                    do {
                        int alt39=2;
                        int LA39_0 = input.LA(1);

                        if ( (LA39_0==ID) ) {
                            int LA39_2 = input.LA(2);

                            if ( ((synpred2()&&(validateIdentifierKey(DroolsSoftKeywords.OR)))) ) {
                                alt39=1;
                            }


                        }
                        else if ( (LA39_0==DOUBLE_PIPE) ) {
                            int LA39_3 = input.LA(2);

                            if ( (synpred2()) ) {
                                alt39=1;
                            }


                        }


                        switch (alt39) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:5: ( or_key | DOUBLE_PIPE )=> (value= or_key | pipe= DOUBLE_PIPE ) lhs_and
                    	    {
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:28: (value= or_key | pipe= DOUBLE_PIPE )
                    	    int alt38=2;
                    	    int LA38_0 = input.LA(1);

                    	    if ( (LA38_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
                    	        alt38=1;
                    	    }
                    	    else if ( (LA38_0==DOUBLE_PIPE) ) {
                    	        alt38=2;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("485:28: (value= or_key | pipe= DOUBLE_PIPE )", 38, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt38) {
                    	        case 1 :
                    	            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:29: value= or_key
                    	            {
                    	            pushFollow(FOLLOW_or_key_in_lhs_or1696);
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
                    	            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:69: pipe= DOUBLE_PIPE
                    	            {
                    	            pipe=(Token)input.LT(1);
                    	            match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_lhs_or1703); if (failed) return retval;
                    	            if ( backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

                    	            if ( backtracking==0 ) {
                    	              orToken = pipe;
                    	            }

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1708);
                    	    lhs_and109=lhs_and();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_and.add(lhs_and109.getTree());

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
                    	    // 486:3: -> ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
                    	    {
                    	        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:6: ^( VT_OR_INFIX[orToken] $lhs_or lhs_and )
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
                    	    break loop39;
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:489:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );
    public final lhs_and_return lhs_and() throws RecognitionException {
        lhs_and_return retval = new lhs_and_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token amper=null;
        Token LEFT_PAREN110=null;
        Token RIGHT_PAREN112=null;
        and_key_return and = null;

        and_key_return value = null;

        lhs_unary_return lhs_unary111 = null;

        lhs_unary_return lhs_unary113 = null;

        lhs_unary_return lhs_unary114 = null;


        Object amper_tree=null;
        Object LEFT_PAREN110_tree=null;
        Object RIGHT_PAREN112_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_AMPER=new RewriteRuleTokenStream(adaptor,"token DOUBLE_AMPER");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_and_key=new RewriteRuleSubtreeStream(adaptor,"rule and_key");
        RewriteRuleSubtreeStream stream_lhs_unary=new RewriteRuleSubtreeStream(adaptor,"rule lhs_unary");

        	Token andToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:3: ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* )
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
                        switch ( input.LA(4) ) {
                        case ID:
                            {
                            int LA44_6 = input.LA(5);

                            if ( (synpred3()) ) {
                                alt44=1;
                            }
                            else if ( (true) ) {
                                alt44=2;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("489:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 44, 6, input);

                                throw nvae;
                            }
                            }
                            break;
                        case LEFT_PAREN:
                            {
                            int LA44_7 = input.LA(5);

                            if ( (synpred3()) ) {
                                alt44=1;
                            }
                            else if ( (true) ) {
                                alt44=2;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("489:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 44, 7, input);

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
                        case VT_RULE_ID:
                        case VT_ENTRYPOINT_ID:
                        case VT_SLOT_ID:
                        case VT_SLOT:
                        case VT_RULE_ATTRIBUTES:
                        case VT_RHS_CHUNK:
                        case VT_CURLY_CHUNK:
                        case VT_SQUARE_CHUNK:
                        case VT_PAREN_CHUNK:
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
                        case COLON:
                        case BOOL:
                        case INT:
                        case DOUBLE_PIPE:
                        case DOUBLE_AMPER:
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
                            alt44=2;
                            }
                            break;
                        default:
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("489:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 44, 4, input);

                            throw nvae;
                        }

                        }
                        break;
                    case ID:
                        {
                        int LA44_5 = input.LA(4);

                        if ( (synpred3()) ) {
                            alt44=1;
                        }
                        else if ( (true) ) {
                            alt44=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("489:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 44, 5, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("489:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 44, 3, input);

                        throw nvae;
                    }

                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("489:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 44, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA44_0==ID) ) {
                alt44=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("489:1: lhs_and : ( ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN ) | ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )* );", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:5: ( LEFT_PAREN and_key )=> LEFT_PAREN and= and_key ( lhs_unary )+ RIGHT_PAREN
                    {
                    LEFT_PAREN110=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and1746); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN110);

                    pushFollow(FOLLOW_and_key_in_lhs_and1750);
                    and=and_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_and_key.add(and.getTree());
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:51: ( lhs_unary )+
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
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:51: lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and1752);
                    	    lhs_unary111=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary111.getTree());

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

                    RIGHT_PAREN112=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and1755); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN112);


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
                    // 493:3: -> ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
                    {
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:493:6: ^( VT_AND_PREFIX[$and.start] ( lhs_unary )+ RIGHT_PAREN )
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
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:4: ( lhs_unary -> lhs_unary ) ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    {
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:4: ( lhs_unary -> lhs_unary )
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:5: lhs_unary
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and1776);
                    lhs_unary113=lhs_unary();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary113.getTree());

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
                    // 494:15: -> lhs_unary
                    {
                        adaptor.addChild(root_0, stream_lhs_unary.next());

                    }

                    }

                    }

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:3: ( ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary ) )*
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==ID) ) {
                            int LA43_2 = input.LA(2);

                            if ( ((synpred4()&&(validateIdentifierKey(DroolsSoftKeywords.AND)))) ) {
                                alt43=1;
                            }


                        }
                        else if ( (LA43_0==DOUBLE_AMPER) ) {
                            int LA43_3 = input.LA(2);

                            if ( (synpred4()) ) {
                                alt43=1;
                            }


                        }


                        switch (alt43) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:5: ( and_key | DOUBLE_AMPER )=> (value= and_key | amper= DOUBLE_AMPER ) lhs_unary
                    	    {
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:30: (value= and_key | amper= DOUBLE_AMPER )
                    	    int alt42=2;
                    	    int LA42_0 = input.LA(1);

                    	    if ( (LA42_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.AND)))) {
                    	        alt42=1;
                    	    }
                    	    else if ( (LA42_0==DOUBLE_AMPER) ) {
                    	        alt42=2;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("495:30: (value= and_key | amper= DOUBLE_AMPER )", 42, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt42) {
                    	        case 1 :
                    	            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:31: value= and_key
                    	            {
                    	            pushFollow(FOLLOW_and_key_in_lhs_and1798);
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
                    	            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:73: amper= DOUBLE_AMPER
                    	            {
                    	            amper=(Token)input.LT(1);
                    	            match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_lhs_and1805); if (failed) return retval;
                    	            if ( backtracking==0 ) stream_DOUBLE_AMPER.add(amper);

                    	            if ( backtracking==0 ) {
                    	              andToken = amper;
                    	            }

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and1810);
                    	    lhs_unary114=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_lhs_unary.add(lhs_unary114.getTree());

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
                    	    // 496:3: -> ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
                    	    {
                    	        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:496:6: ^( VT_AND_INFIX[andToken] $lhs_and lhs_unary )
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
    // $ANTLR end lhs_and

    public static class lhs_unary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start lhs_unary
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:499:1: lhs_unary options {backtrack=true; } : ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? ;
    public final lhs_unary_return lhs_unary() throws RecognitionException {
        lhs_unary_return retval = new lhs_unary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN119=null;
        Token RIGHT_PAREN121=null;
        Token SEMICOLON123=null;
        lhs_exist_return lhs_exist115 = null;

        lhs_not_return lhs_not116 = null;

        lhs_eval_return lhs_eval117 = null;

        lhs_forall_return lhs_forall118 = null;

        lhs_or_return lhs_or120 = null;

        pattern_source_return pattern_source122 = null;


        Object LEFT_PAREN119_tree=null;
        Object RIGHT_PAREN121_tree=null;
        Object SEMICOLON123_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:2: ( ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )? )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:4: ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source ) ( ( SEMICOLON )=> SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:4: ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )
            int alt45=6;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==ID) ) {
                int LA45_1 = input.LA(2);

                if ( ((validateIdentifierKey(DroolsSoftKeywords.EXISTS))) ) {
                    alt45=1;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                    alt45=2;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                    alt45=3;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.FORALL))) ) {
                    alt45=4;
                }
                else if ( (true) ) {
                    alt45=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("501:4: ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )", 45, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA45_0==LEFT_PAREN) ) {
                alt45=5;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("501:4: ( lhs_exist | lhs_not | lhs_eval | lhs_forall | LEFT_PAREN lhs_or RIGHT_PAREN | pattern_source )", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:6: lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary1848);
                    lhs_exist115=lhs_exist();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_exist115.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:502:5: lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary1854);
                    lhs_not116=lhs_not();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_not116.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:503:5: lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary1860);
                    lhs_eval117=lhs_eval();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_eval117.getTree());

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:504:5: lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary1866);
                    lhs_forall118=lhs_forall();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_forall118.getTree());

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:505:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN119=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary1872); if (failed) return retval;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary1875);
                    lhs_or120=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, lhs_or120.getTree());
                    RIGHT_PAREN121=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary1877); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN121_tree = (Object)adaptor.create(RIGHT_PAREN121);
                    adaptor.addChild(root_0, RIGHT_PAREN121_tree);
                    }

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:506:5: pattern_source
                    {
                    pushFollow(FOLLOW_pattern_source_in_lhs_unary1883);
                    pattern_source122=pattern_source();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, pattern_source122.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:508:3: ( ( SEMICOLON )=> SEMICOLON )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==SEMICOLON) ) {
                int LA46_1 = input.LA(2);

                if ( (synpred5()) ) {
                    alt46=1;
                }
            }
            switch (alt46) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:508:4: ( SEMICOLON )=> SEMICOLON
                    {
                    SEMICOLON123=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_lhs_unary1897); if (failed) return retval;

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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:511:1: lhs_exist : exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final lhs_exist_return lhs_exist() throws RecognitionException {
        lhs_exist_return retval = new lhs_exist_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN126=null;
        Token RIGHT_PAREN128=null;
        exists_key_return exists_key124 = null;

        lhs_or_return lhs_or125 = null;

        lhs_or_return lhs_or127 = null;

        lhs_pattern_return lhs_pattern129 = null;


        Object LEFT_PAREN126_tree=null;
        Object RIGHT_PAREN128_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_exists_key=new RewriteRuleSubtreeStream(adaptor,"rule exists_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:512:2: ( exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:512:4: exists_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_exists_key_in_lhs_exist1911);
            exists_key124=exists_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_exists_key.add(exists_key124.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:10: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt47=3;
            alt47 = dfa47.predict(input);
            switch (alt47) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:12: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist1935);
                    lhs_or125=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or125.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:514:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN126=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist1942); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN126);

                    pushFollow(FOLLOW_lhs_or_in_lhs_exist1944);
                    lhs_or127=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or127.getTree());
                    RIGHT_PAREN128=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist1946); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN128);


                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:515:12: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist1959);
                    lhs_pattern129=lhs_pattern();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern129.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: RIGHT_PAREN, lhs_pattern, lhs_or, exists_key
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 517:10: -> ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:517:13: ^( exists_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_exists_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:517:26: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.next());

                }
                stream_lhs_or.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:517:34: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.next());

                }
                stream_lhs_pattern.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:517:47: ( RIGHT_PAREN )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:520:1: lhs_not : not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) ;
    public final lhs_not_return lhs_not() throws RecognitionException {
        lhs_not_return retval = new lhs_not_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN132=null;
        Token RIGHT_PAREN134=null;
        not_key_return not_key130 = null;

        lhs_or_return lhs_or131 = null;

        lhs_or_return lhs_or133 = null;

        lhs_pattern_return lhs_pattern135 = null;


        Object LEFT_PAREN132_tree=null;
        Object RIGHT_PAREN134_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:520:9: ( not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern ) -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:520:11: not_key ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            {
            pushFollow(FOLLOW_not_key_in_lhs_not2005);
            not_key130=not_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_not_key.add(not_key130.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:3: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )
            int alt48=3;
            alt48 = dfa48.predict(input);
            switch (alt48) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:5: ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or
                    {
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2022);
                    lhs_or131=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or131.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:522:5: LEFT_PAREN lhs_or RIGHT_PAREN
                    {
                    LEFT_PAREN132=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2029); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN132);

                    pushFollow(FOLLOW_lhs_or_in_lhs_not2031);
                    lhs_or133=lhs_or();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_or.add(lhs_or133.getTree());
                    RIGHT_PAREN134=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2033); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN134);


                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:523:6: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2041);
                    lhs_pattern135=lhs_pattern();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern135.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: lhs_or, RIGHT_PAREN, not_key, lhs_pattern
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 524:10: -> ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:524:13: ^( not_key ( lhs_or )? ( lhs_pattern )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_not_key.nextNode(), root_1);

                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:524:23: ( lhs_or )?
                if ( stream_lhs_or.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_or.next());

                }
                stream_lhs_or.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:524:31: ( lhs_pattern )?
                if ( stream_lhs_pattern.hasNext() ) {
                    adaptor.addChild(root_1, stream_lhs_pattern.next());

                }
                stream_lhs_pattern.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:524:44: ( RIGHT_PAREN )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:527:1: lhs_eval : ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) ;
    public final lhs_eval_return lhs_eval() throws RecognitionException {
        lhs_eval_return retval = new lhs_eval_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        eval_key_return ev = null;

        paren_chunk_return pc = null;


        RewriteRuleSubtreeStream stream_eval_key=new RewriteRuleSubtreeStream(adaptor,"rule eval_key");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:528:2: (ev= eval_key pc= paren_chunk -> ^( eval_key paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:528:4: ev= eval_key pc= paren_chunk
            {
            pushFollow(FOLLOW_eval_key_in_lhs_eval2080);
            ev=eval_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_eval_key.add(ev.getTree());
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2084);
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
            // 531:3: -> ^( eval_key paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:531:6: ^( eval_key paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:534:1: lhs_forall : forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN ) ;
    public final lhs_forall_return lhs_forall() throws RecognitionException {
        lhs_forall_return retval = new lhs_forall_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN137=null;
        Token RIGHT_PAREN139=null;
        forall_key_return forall_key136 = null;

        lhs_pattern_return lhs_pattern138 = null;


        Object LEFT_PAREN137_tree=null;
        Object RIGHT_PAREN139_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_forall_key=new RewriteRuleSubtreeStream(adaptor,"rule forall_key");
        RewriteRuleSubtreeStream stream_lhs_pattern=new RewriteRuleSubtreeStream(adaptor,"rule lhs_pattern");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:2: ( forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:4: forall_key LEFT_PAREN ( lhs_pattern )+ RIGHT_PAREN
            {
            pushFollow(FOLLOW_forall_key_in_lhs_forall2108);
            forall_key136=forall_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_forall_key.add(forall_key136.getTree());
            LEFT_PAREN137=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2110); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN137);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:26: ( lhs_pattern )+
            int cnt49=0;
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==ID) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:26: lhs_pattern
            	    {
            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2112);
            	    lhs_pattern138=lhs_pattern();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_lhs_pattern.add(lhs_pattern138.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt49 >= 1 ) break loop49;
            	    if (backtracking>0) {failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(49, input);
                        throw eee;
                }
                cnt49++;
            } while (true);

            RIGHT_PAREN139=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall2115); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN139);


            // AST REWRITE
            // elements: forall_key, lhs_pattern, RIGHT_PAREN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 536:3: -> ^( forall_key ( lhs_pattern )+ RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:536:6: ^( forall_key ( lhs_pattern )+ RIGHT_PAREN )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:539:1: pattern_source options {k=3; } : lhs_pattern ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? ;
    public final pattern_source_return pattern_source() throws RecognitionException {
        pattern_source_return retval = new pattern_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        lhs_pattern_return lhs_pattern140 = null;

        from_key_return from_key141 = null;

        accumulate_statement_return accumulate_statement142 = null;

        collect_statement_return collect_statement143 = null;

        entrypoint_statement_return entrypoint_statement144 = null;

        from_source_return from_source145 = null;



        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:541:2: ( lhs_pattern ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )? )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:541:4: lhs_pattern ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_lhs_pattern_in_pattern_source2147);
            lhs_pattern140=lhs_pattern();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, lhs_pattern140.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:542:3: ( from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source ) )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==ID) ) {
                int LA51_1 = input.LA(2);

                if ( (LA51_1==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                    int LA51_3 = input.LA(3);

                    if ( (LA51_3==SEMICOLON||LA51_3==END||(LA51_3>=COMMA && LA51_3<=RIGHT_PAREN)||(LA51_3>=DOUBLE_PIPE && LA51_3<=DOUBLE_AMPER)||LA51_3==THEN||LA51_3==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                        alt51=1;
                    }
                    else if ( (LA51_3==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                        int LA51_6 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                            alt51=1;
                        }
                    }
                    else if ( (LA51_3==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                        int LA51_7 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                            alt51=1;
                        }
                    }
                    else if ( (LA51_3==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                        int LA51_8 = input.LA(4);

                        if ( (LA51_8==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                            int LA51_10 = input.LA(5);

                            if ( (LA51_10==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                int LA51_11 = input.LA(6);

                                if ( (LA51_11==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                    int LA51_14 = input.LA(7);

                                    if ( (LA51_14==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                                        int LA51_16 = input.LA(8);

                                        if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                                            alt51=1;
                                        }
                                    }
                                    else if ( ((LA51_14>=SEMICOLON && LA51_14<=DOT)||LA51_14==END||(LA51_14>=COMMA && LA51_14<=RIGHT_PAREN)||(LA51_14>=DOUBLE_PIPE && LA51_14<=DOUBLE_AMPER)||LA51_14==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                                        alt51=1;
                                    }
                                }
                                else if ( ((LA51_11>=VT_COMPILATION_UNIT && LA51_11<=LEFT_SQUARE)||(LA51_11>=THEN && LA51_11<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                                    alt51=1;
                                }
                            }
                            else if ( (LA51_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                                int LA51_12 = input.LA(6);

                                if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                                    alt51=1;
                                }
                            }
                            else if ( (LA51_10==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.OR))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))))) {
                                int LA51_13 = input.LA(6);

                                if ( (LA51_13==ID) && (((validateIdentifierKey(DroolsSoftKeywords.NOT))||(validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.FROM))||(validateIdentifierKey(DroolsSoftKeywords.EXISTS))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
                                    int LA51_15 = input.LA(7);

                                    if ( ((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                                        alt51=1;
                                    }
                                }
                            }
                            else if ( ((LA51_10>=SEMICOLON && LA51_10<=ID)||LA51_10==END||(LA51_10>=COMMA && LA51_10<=RIGHT_PAREN)||(LA51_10>=DOUBLE_PIPE && LA51_10<=DOUBLE_AMPER)||LA51_10==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.FROM)))) {
                                alt51=1;
                            }
                        }
                    }
                }
            }
            switch (alt51) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:4: from_key ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    {
                    pushFollow(FOLLOW_from_key_in_pattern_source2156);
                    from_key141=from_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(from_key141.getTree(), root_0);
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:544:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )
                    int alt50=4;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==ID) ) {
                        int LA50_1 = input.LA(2);

                        if ( (LA50_1==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT)))) {
                            alt50=3;
                        }
                        else if ( (LA50_1==LEFT_PAREN) ) {
                            switch ( input.LA(3) ) {
                            case LEFT_PAREN:
                                {
                                int LA50_5 = input.LA(4);

                                if ( ((validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))) ) {
                                    alt50=1;
                                }
                                else if ( (true) ) {
                                    alt50=4;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return retval;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("544:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 50, 5, input);

                                    throw nvae;
                                }
                                }
                                break;
                            case ID:
                                {
                                int LA50_6 = input.LA(4);

                                if ( ((validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))) ) {
                                    alt50=1;
                                }
                                else if ( ((validateIdentifierKey(DroolsSoftKeywords.COLLECT))) ) {
                                    alt50=2;
                                }
                                else if ( (true) ) {
                                    alt50=4;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return retval;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("544:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 50, 6, input);

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
                            case VT_RULE_ID:
                            case VT_ENTRYPOINT_ID:
                            case VT_SLOT_ID:
                            case VT_SLOT:
                            case VT_RULE_ATTRIBUTES:
                            case VT_RHS_CHUNK:
                            case VT_CURLY_CHUNK:
                            case VT_SQUARE_CHUNK:
                            case VT_PAREN_CHUNK:
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
                            case COLON:
                            case BOOL:
                            case INT:
                            case DOUBLE_PIPE:
                            case DOUBLE_AMPER:
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
                                alt50=4;
                                }
                                break;
                            default:
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("544:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 50, 3, input);

                                throw nvae;
                            }

                        }
                        else if ( ((LA50_1>=SEMICOLON && LA50_1<=DOT)||LA50_1==END||(LA50_1>=COMMA && LA50_1<=RIGHT_PAREN)||(LA50_1>=DOUBLE_PIPE && LA50_1<=DOUBLE_AMPER)||LA50_1==THEN) ) {
                            alt50=4;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("544:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 50, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("544:11: ( accumulate_statement | collect_statement | entrypoint_statement | from_source )", 50, 0, input);

                        throw nvae;
                    }
                    switch (alt50) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:544:14: accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_pattern_source2172);
                            accumulate_statement142=accumulate_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, accumulate_statement142.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:545:15: collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_pattern_source2188);
                            collect_statement143=collect_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, collect_statement143.getTree());

                            }
                            break;
                        case 3 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:546:15: entrypoint_statement
                            {
                            pushFollow(FOLLOW_entrypoint_statement_in_pattern_source2205);
                            entrypoint_statement144=entrypoint_statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, entrypoint_statement144.getTree());

                            }
                            break;
                        case 4 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:547:15: from_source
                            {
                            pushFollow(FOLLOW_from_source_in_pattern_source2221);
                            from_source145=from_source();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, from_source145.getTree());

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

    public static class accumulate_statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start accumulate_statement
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:1: accumulate_statement : accumulate_key LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) ;
    public final accumulate_statement_return accumulate_statement() throws RecognitionException {
        accumulate_statement_return retval = new accumulate_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN147=null;
        Token COMMA149=null;
        Token RIGHT_PAREN152=null;
        accumulate_key_return accumulate_key146 = null;

        lhs_or_return lhs_or148 = null;

        accumulate_init_clause_return accumulate_init_clause150 = null;

        accumulate_id_clause_return accumulate_id_clause151 = null;


        Object LEFT_PAREN147_tree=null;
        Object COMMA149_tree=null;
        Object RIGHT_PAREN152_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_lhs_or=new RewriteRuleSubtreeStream(adaptor,"rule lhs_or");
        RewriteRuleSubtreeStream stream_accumulate_init_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_init_clause");
        RewriteRuleSubtreeStream stream_accumulate_id_clause=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_id_clause");
        RewriteRuleSubtreeStream stream_accumulate_key=new RewriteRuleSubtreeStream(adaptor,"rule accumulate_key");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:553:2: ( accumulate_key LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN -> ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:553:4: accumulate_key LEFT_PAREN lhs_or ( COMMA )? ( accumulate_init_clause | accumulate_id_clause ) RIGHT_PAREN
            {
            pushFollow(FOLLOW_accumulate_key_in_accumulate_statement2249);
            accumulate_key146=accumulate_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_accumulate_key.add(accumulate_key146.getTree());
            LEFT_PAREN147=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2253); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN147);

            pushFollow(FOLLOW_lhs_or_in_accumulate_statement2255);
            lhs_or148=lhs_or();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_lhs_or.add(lhs_or148.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:554:21: ( COMMA )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==COMMA) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:554:21: COMMA
                    {
                    COMMA149=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2257); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(COMMA149);


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:3: ( accumulate_init_clause | accumulate_id_clause )
            int alt53=2;
            alt53 = dfa53.predict(input);
            switch (alt53) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:5: accumulate_init_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_statement2265);
                    accumulate_init_clause150=accumulate_init_clause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accumulate_init_clause.add(accumulate_init_clause150.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:556:5: accumulate_id_clause
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_statement2271);
                    accumulate_id_clause151=accumulate_id_clause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accumulate_id_clause.add(accumulate_id_clause151.getTree());

                    }
                    break;

            }

            RIGHT_PAREN152=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2279); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN152);


            // AST REWRITE
            // elements: accumulate_id_clause, lhs_or, accumulate_key, RIGHT_PAREN, accumulate_init_clause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 559:3: -> ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:559:6: ^( accumulate_key lhs_or ( accumulate_init_clause )? ( accumulate_id_clause )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_accumulate_key.nextNode(), root_1);

                adaptor.addChild(root_1, stream_lhs_or.next());
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:559:30: ( accumulate_init_clause )?
                if ( stream_accumulate_init_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_accumulate_init_clause.next());

                }
                stream_accumulate_init_clause.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:559:54: ( accumulate_id_clause )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:1: accumulate_init_clause : init_key pc1= paren_chunk ( COMMA )? action_key pc2= paren_chunk ( COMMA )? ( reverse_key pc3= paren_chunk ( COMMA )? )? result_key pc4= paren_chunk -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) ;
    public final accumulate_init_clause_return accumulate_init_clause() throws RecognitionException {
        accumulate_init_clause_return retval = new accumulate_init_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA154=null;
        Token COMMA156=null;
        Token COMMA158=null;
        paren_chunk_return pc1 = null;

        paren_chunk_return pc2 = null;

        paren_chunk_return pc3 = null;

        paren_chunk_return pc4 = null;

        init_key_return init_key153 = null;

        action_key_return action_key155 = null;

        reverse_key_return reverse_key157 = null;

        result_key_return result_key159 = null;


        Object COMMA154_tree=null;
        Object COMMA156_tree=null;
        Object COMMA158_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_reverse_key=new RewriteRuleSubtreeStream(adaptor,"rule reverse_key");
        RewriteRuleSubtreeStream stream_result_key=new RewriteRuleSubtreeStream(adaptor,"rule result_key");
        RewriteRuleSubtreeStream stream_init_key=new RewriteRuleSubtreeStream(adaptor,"rule init_key");
        RewriteRuleSubtreeStream stream_action_key=new RewriteRuleSubtreeStream(adaptor,"rule action_key");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:563:2: ( init_key pc1= paren_chunk ( COMMA )? action_key pc2= paren_chunk ( COMMA )? ( reverse_key pc3= paren_chunk ( COMMA )? )? result_key pc4= paren_chunk -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:563:4: init_key pc1= paren_chunk ( COMMA )? action_key pc2= paren_chunk ( COMMA )? ( reverse_key pc3= paren_chunk ( COMMA )? )? result_key pc4= paren_chunk
            {
            pushFollow(FOLLOW_init_key_in_accumulate_init_clause2308);
            init_key153=init_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_init_key.add(init_key153.getTree());
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2313);
            pc1=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc1.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:18: ( COMMA )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==COMMA) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:18: COMMA
                    {
                    COMMA154=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2315); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(COMMA154);


                    }
                    break;

            }

            pushFollow(FOLLOW_action_key_in_accumulate_init_clause2319);
            action_key155=action_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action_key.add(action_key155.getTree());
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2323);
            pc2=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc2.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:29: ( COMMA )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==COMMA) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:565:29: COMMA
                    {
                    COMMA156=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2325); if (failed) return retval;
                    if ( backtracking==0 ) stream_COMMA.add(COMMA156);


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:566:2: ( reverse_key pc3= paren_chunk ( COMMA )? )?
            int alt57=2;
            alt57 = dfa57.predict(input);
            switch (alt57) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:566:4: reverse_key pc3= paren_chunk ( COMMA )?
                    {
                    pushFollow(FOLLOW_reverse_key_in_accumulate_init_clause2331);
                    reverse_key157=reverse_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_reverse_key.add(reverse_key157.getTree());
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2335);
                    pc3=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(pc3.getTree());
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:566:32: ( COMMA )?
                    int alt56=2;
                    int LA56_0 = input.LA(1);

                    if ( (LA56_0==COMMA) ) {
                        alt56=1;
                    }
                    switch (alt56) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:566:32: COMMA
                            {
                            COMMA158=(Token)input.LT(1);
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_init_clause2337); if (failed) return retval;
                            if ( backtracking==0 ) stream_COMMA.add(COMMA158);


                            }
                            break;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_result_key_in_accumulate_init_clause2343);
            result_key159=result_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_result_key.add(result_key159.getTree());
            pushFollow(FOLLOW_paren_chunk_in_accumulate_init_clause2347);
            pc4=paren_chunk();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_paren_chunk.add(pc4.getTree());

            // AST REWRITE
            // elements: pc3, pc2, action_key, pc1, pc4, init_key, result_key, reverse_key
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
            // 568:2: -> ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:568:5: ^( VT_ACCUMULATE_INIT_CLAUSE ^( init_key $pc1) ^( action_key $pc2) ( ^( reverse_key $pc3) )? ^( result_key $pc4) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ACCUMULATE_INIT_CLAUSE, "VT_ACCUMULATE_INIT_CLAUSE"), root_1);

                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:568:33: ^( init_key $pc1)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_init_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc1.next());

                adaptor.addChild(root_1, root_2);
                }
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:568:50: ^( action_key $pc2)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_action_key.nextNode(), root_2);

                adaptor.addChild(root_2, stream_pc2.next());

                adaptor.addChild(root_1, root_2);
                }
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:568:69: ( ^( reverse_key $pc3) )?
                if ( stream_pc3.hasNext()||stream_reverse_key.hasNext() ) {
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:568:69: ^( reverse_key $pc3)
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_reverse_key.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_pc3.next());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_pc3.reset();
                stream_reverse_key.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:568:90: ^( result_key $pc4)
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:1: accumulate_id_clause : id= ID text= paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) ;
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
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:572:2: (id= ID text= paren_chunk -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:572:4: id= ID text= paren_chunk
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accumulate_id_clause2396); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            pushFollow(FOLLOW_paren_chunk_in_accumulate_id_clause2400);
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
            // 573:2: -> ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:5: ^( VT_ACCUMULATE_ID_CLAUSE ID paren_chunk )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:576:1: collect_statement : collect_key LEFT_PAREN pattern_source RIGHT_PAREN -> ^( collect_key pattern_source RIGHT_PAREN ) ;
    public final collect_statement_return collect_statement() throws RecognitionException {
        collect_statement_return retval = new collect_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN161=null;
        Token RIGHT_PAREN163=null;
        collect_key_return collect_key160 = null;

        pattern_source_return pattern_source162 = null;


        Object LEFT_PAREN161_tree=null;
        Object RIGHT_PAREN163_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_collect_key=new RewriteRuleSubtreeStream(adaptor,"rule collect_key");
        RewriteRuleSubtreeStream stream_pattern_source=new RewriteRuleSubtreeStream(adaptor,"rule pattern_source");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:577:2: ( collect_key LEFT_PAREN pattern_source RIGHT_PAREN -> ^( collect_key pattern_source RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:577:4: collect_key LEFT_PAREN pattern_source RIGHT_PAREN
            {
            pushFollow(FOLLOW_collect_key_in_collect_statement2422);
            collect_key160=collect_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_collect_key.add(collect_key160.getTree());
            LEFT_PAREN161=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement2426); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN161);

            pushFollow(FOLLOW_pattern_source_in_collect_statement2428);
            pattern_source162=pattern_source();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_pattern_source.add(pattern_source162.getTree());
            RIGHT_PAREN163=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement2430); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN163);


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
            // 579:2: -> ^( collect_key pattern_source RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:579:5: ^( collect_key pattern_source RIGHT_PAREN )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:582:1: entrypoint_statement : entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) ;
    public final entrypoint_statement_return entrypoint_statement() throws RecognitionException {
        entrypoint_statement_return retval = new entrypoint_statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        entry_point_key_return entry_point_key164 = null;

        entrypoint_id_return entrypoint_id165 = null;


        RewriteRuleSubtreeStream stream_entrypoint_id=new RewriteRuleSubtreeStream(adaptor,"rule entrypoint_id");
        RewriteRuleSubtreeStream stream_entry_point_key=new RewriteRuleSubtreeStream(adaptor,"rule entry_point_key");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:2: ( entry_point_key entrypoint_id -> ^( entry_point_key entrypoint_id ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:4: entry_point_key entrypoint_id
            {
            pushFollow(FOLLOW_entry_point_key_in_entrypoint_statement2452);
            entry_point_key164=entry_point_key();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_entry_point_key.add(entry_point_key164.getTree());
            pushFollow(FOLLOW_entrypoint_id_in_entrypoint_statement2454);
            entrypoint_id165=entrypoint_id();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_entrypoint_id.add(entrypoint_id165.getTree());

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
            // 584:2: -> ^( entry_point_key entrypoint_id )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:584:5: ^( entry_point_key entrypoint_id )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:587:1: entrypoint_id : (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] );
    public final entrypoint_id_return entrypoint_id() throws RecognitionException {
        entrypoint_id_return retval = new entrypoint_id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:588:2: (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] )
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==ID) ) {
                alt58=1;
            }
            else if ( (LA58_0==STRING) ) {
                alt58=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("587:1: entrypoint_id : (value= ID -> VT_ENTRYPOINT_ID[$value] | value= STRING -> VT_ENTRYPOINT_ID[$value] );", 58, 0, input);

                throw nvae;
            }
            switch (alt58) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:588:5: value= ID
                    {
                    value=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_entrypoint_id2477); if (failed) return retval;
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
                    // 588:14: -> VT_ENTRYPOINT_ID[$value]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_ENTRYPOINT_ID, value));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:589:5: value= STRING
                    {
                    value=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_entrypoint_id2490); if (failed) return retval;
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
                    // 589:18: -> VT_ENTRYPOINT_ID[$value]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:592:1: from_source : ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) ;
    public final from_source_return from_source() throws RecognitionException {
        from_source_return retval = new from_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID166=null;
        paren_chunk_return args = null;

        expression_chain_return expression_chain167 = null;


        Object ID166_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:593:2: ( ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )? -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:593:4: ID ( ( LEFT_PAREN )=>args= paren_chunk )? ( expression_chain )?
            {
            ID166=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_from_source2506); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID166);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:3: ( ( LEFT_PAREN )=>args= paren_chunk )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==LEFT_PAREN) ) {
                int LA59_1 = input.LA(2);

                if ( (LA59_1==LEFT_PAREN) ) {
                    int LA59_3 = input.LA(3);

                    if ( (synpred8()) ) {
                        alt59=1;
                    }
                }
                else if ( (LA59_1==ID) ) {
                    int LA59_4 = input.LA(3);

                    if ( (synpred8()) ) {
                        alt59=1;
                    }
                }
                else if ( ((LA59_1>=VT_COMPILATION_UNIT && LA59_1<=SEMICOLON)||(LA59_1>=DOT && LA59_1<=STRING)||LA59_1==COMMA||(LA59_1>=COLON && LA59_1<=MULTI_LINE_COMMENT)) && (synpred8())) {
                    alt59=1;
                }
                else if ( (LA59_1==RIGHT_PAREN) && (synpred8())) {
                    alt59=1;
                }
            }
            switch (alt59) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:5: ( LEFT_PAREN )=>args= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source2519);
                    args=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(args.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:595:3: ( expression_chain )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==DOT) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:595:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source2526);
                    expression_chain167=expression_chain();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expression_chain.add(expression_chain167.getTree());

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
            // 596:2: -> ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:596:5: ^( VT_FROM_SOURCE ID ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FROM_SOURCE, "VT_FROM_SOURCE"), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:596:25: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.next());

                }
                stream_paren_chunk.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:596:38: ( expression_chain )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:599:1: expression_chain : startToken= DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) ;
    public final expression_chain_return expression_chain() throws RecognitionException {
        expression_chain_return retval = new expression_chain_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token startToken=null;
        Token ID168=null;
        square_chunk_return square_chunk169 = null;

        paren_chunk_return paren_chunk170 = null;

        expression_chain_return expression_chain171 = null;


        Object startToken_tree=null;
        Object ID168_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        RewriteRuleSubtreeStream stream_expression_chain=new RewriteRuleSubtreeStream(adaptor,"rule expression_chain");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:600:2: (startToken= DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )? -> ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:601:3: startToken= DOT ID ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )? ( expression_chain )?
            {
            startToken=(Token)input.LT(1);
            match(input,DOT,FOLLOW_DOT_in_expression_chain2558); if (failed) return retval;
            if ( backtracking==0 ) stream_DOT.add(startToken);

            ID168=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_expression_chain2560); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID168);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:602:4: ( ( LEFT_SQUARE )=> square_chunk | ( LEFT_PAREN )=> paren_chunk )?
            int alt61=3;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==LEFT_SQUARE) && (synpred9())) {
                alt61=1;
            }
            else if ( (LA61_0==LEFT_PAREN) ) {
                int LA61_2 = input.LA(2);

                if ( (LA61_2==LEFT_PAREN) ) {
                    int LA61_4 = input.LA(3);

                    if ( (synpred10()) ) {
                        alt61=2;
                    }
                }
                else if ( (LA61_2==ID) ) {
                    int LA61_5 = input.LA(3);

                    if ( (synpred10()) ) {
                        alt61=2;
                    }
                }
                else if ( ((LA61_2>=VT_COMPILATION_UNIT && LA61_2<=SEMICOLON)||(LA61_2>=DOT && LA61_2<=STRING)||LA61_2==COMMA||(LA61_2>=COLON && LA61_2<=MULTI_LINE_COMMENT)) && (synpred10())) {
                    alt61=2;
                }
                else if ( (LA61_2==RIGHT_PAREN) && (synpred10())) {
                    alt61=2;
                }
            }
            switch (alt61) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:603:6: ( LEFT_SQUARE )=> square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain2580);
                    square_chunk169=square_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_square_chunk.add(square_chunk169.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:605:6: ( LEFT_PAREN )=> paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain2602);
                    paren_chunk170=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk170.getTree());

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:607:4: ( expression_chain )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==DOT) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:607:4: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain2613);
                    expression_chain171=expression_chain();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expression_chain.add(expression_chain171.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: square_chunk, paren_chunk, ID, expression_chain
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 608:4: -> ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:608:7: ^( VT_EXPRESSION_CHAIN[$startToken] ID ( square_chunk )? ( paren_chunk )? ( expression_chain )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_EXPRESSION_CHAIN, startToken), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:608:45: ( square_chunk )?
                if ( stream_square_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_square_chunk.next());

                }
                stream_square_chunk.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:608:59: ( paren_chunk )?
                if ( stream_paren_chunk.hasNext() ) {
                    adaptor.addChild(root_1, stream_paren_chunk.next());

                }
                stream_paren_chunk.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:608:72: ( expression_chain )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:611:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );
    public final lhs_pattern_return lhs_pattern() throws RecognitionException {
        lhs_pattern_return retval = new lhs_pattern_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        fact_binding_return fact_binding172 = null;

        fact_return fact173 = null;


        RewriteRuleSubtreeStream stream_fact_binding=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:612:2: ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) )
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==ID) ) {
                int LA63_1 = input.LA(2);

                if ( (LA63_1==COLON) ) {
                    alt63=1;
                }
                else if ( (LA63_1==DOT||LA63_1==LEFT_PAREN||LA63_1==LEFT_SQUARE) ) {
                    alt63=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("611:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );", 63, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("611:1: lhs_pattern : ( fact_binding -> ^( VT_PATTERN fact_binding ) | fact -> ^( VT_PATTERN fact ) );", 63, 0, input);

                throw nvae;
            }
            switch (alt63) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:612:4: fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern2646);
                    fact_binding172=fact_binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact_binding.add(fact_binding172.getTree());

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
                    // 612:17: -> ^( VT_PATTERN fact_binding )
                    {
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:612:20: ^( VT_PATTERN fact_binding )
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
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:613:4: fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern2659);
                    fact173=fact();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact.add(fact173.getTree());

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
                    // 613:9: -> ^( VT_PATTERN fact )
                    {
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:613:12: ^( VT_PATTERN fact )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:616:1: fact_binding : label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) ;
    public final fact_binding_return fact_binding() throws RecognitionException {
        fact_binding_return retval = new fact_binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN176=null;
        Token RIGHT_PAREN178=null;
        label_return label174 = null;

        fact_return fact175 = null;

        fact_binding_expression_return fact_binding_expression177 = null;


        Object LEFT_PAREN176_tree=null;
        Object RIGHT_PAREN178_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        RewriteRuleSubtreeStream stream_fact_binding_expression=new RewriteRuleSubtreeStream(adaptor,"rule fact_binding_expression");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:617:3: ( label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN ) -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:617:5: label ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            {
            pushFollow(FOLLOW_label_in_fact_binding2679);
            label174=label();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_label.add(label174.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:618:3: ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==ID) ) {
                alt64=1;
            }
            else if ( (LA64_0==LEFT_PAREN) ) {
                alt64=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("618:3: ( fact | LEFT_PAREN fact_binding_expression RIGHT_PAREN )", 64, 0, input);

                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:618:5: fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding2685);
                    fact175=fact();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact.add(fact175.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:619:6: LEFT_PAREN fact_binding_expression RIGHT_PAREN
                    {
                    LEFT_PAREN176=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding2692); if (failed) return retval;
                    if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN176);

                    pushFollow(FOLLOW_fact_binding_expression_in_fact_binding2694);
                    fact_binding_expression177=fact_binding_expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fact_binding_expression.add(fact_binding_expression177.getTree());
                    RIGHT_PAREN178=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding2696); if (failed) return retval;
                    if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN178);


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
            // 621:3: -> ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:621:6: ^( VT_FACT_BINDING label ( fact )? ( fact_binding_expression )? ( RIGHT_PAREN )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FACT_BINDING, "VT_FACT_BINDING"), root_1);

                adaptor.addChild(root_1, stream_label.next());
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:621:30: ( fact )?
                if ( stream_fact.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact.next());

                }
                stream_fact.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:621:36: ( fact_binding_expression )?
                if ( stream_fact_binding_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_fact_binding_expression.next());

                }
                stream_fact_binding_expression.reset();
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:621:61: ( RIGHT_PAREN )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:624:1: fact_binding_expression : ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* ;
    public final fact_binding_expression_return fact_binding_expression() throws RecognitionException {
        fact_binding_expression_return retval = new fact_binding_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pipe=null;
        or_key_return value = null;

        fact_return fact179 = null;

        fact_return fact180 = null;


        Object pipe_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleSubtreeStream stream_or_key=new RewriteRuleSubtreeStream(adaptor,"rule or_key");
        RewriteRuleSubtreeStream stream_fact=new RewriteRuleSubtreeStream(adaptor,"rule fact");

        	Token orToken = null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:3: ( ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )* )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:5: ( fact -> fact ) ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:5: ( fact -> fact )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:6: fact
            {
            pushFollow(FOLLOW_fact_in_fact_binding_expression2735);
            fact179=fact();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_fact.add(fact179.getTree());

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
            // 627:11: -> fact
            {
                adaptor.addChild(root_0, stream_fact.next());

            }

            }

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:20: ( (value= or_key | pipe= DOUBLE_PIPE ) fact -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact ) )*
            loop66:
            do {
                int alt66=2;
                int LA66_0 = input.LA(1);

                if ( (LA66_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
                    alt66=1;
                }
                else if ( (LA66_0==DOUBLE_PIPE) ) {
                    alt66=1;
                }


                switch (alt66) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:22: (value= or_key | pipe= DOUBLE_PIPE ) fact
            	    {
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:22: (value= or_key | pipe= DOUBLE_PIPE )
            	    int alt65=2;
            	    int LA65_0 = input.LA(1);

            	    if ( (LA65_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
            	        alt65=1;
            	    }
            	    else if ( (LA65_0==DOUBLE_PIPE) ) {
            	        alt65=2;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("627:22: (value= or_key | pipe= DOUBLE_PIPE )", 65, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt65) {
            	        case 1 :
            	            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:23: value= or_key
            	            {
            	            pushFollow(FOLLOW_or_key_in_fact_binding_expression2747);
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
            	            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:62: pipe= DOUBLE_PIPE
            	            {
            	            pipe=(Token)input.LT(1);
            	            match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_binding_expression2753); if (failed) return retval;
            	            if ( backtracking==0 ) stream_DOUBLE_PIPE.add(pipe);

            	            if ( backtracking==0 ) {
            	              orToken = pipe;
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_fact_in_fact_binding_expression2758);
            	    fact180=fact();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_fact.add(fact180.getTree());

            	    // AST REWRITE
            	    // elements: fact_binding_expression, fact
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    if ( backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 628:3: -> ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
            	    {
            	        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:628:6: ^( VT_FACT_OR[orToken] $fact_binding_expression fact )
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
            	    break loop66;
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:631:1: fact : pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) ;
    public final fact_return fact() throws RecognitionException {
        fact_return retval = new fact_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN182=null;
        Token RIGHT_PAREN184=null;
        pattern_type_return pattern_type181 = null;

        constraints_return constraints183 = null;


        Object LEFT_PAREN182_tree=null;
        Object RIGHT_PAREN184_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleSubtreeStream stream_pattern_type=new RewriteRuleSubtreeStream(adaptor,"rule pattern_type");
        RewriteRuleSubtreeStream stream_constraints=new RewriteRuleSubtreeStream(adaptor,"rule constraints");
         pushParaphrases(DroolsParaphareseTypes.PATTERN); 
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:2: ( pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:4: pattern_type LEFT_PAREN ( constraints )? RIGHT_PAREN
            {
            pushFollow(FOLLOW_pattern_type_in_fact2798);
            pattern_type181=pattern_type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_pattern_type.add(pattern_type181.getTree());
            LEFT_PAREN182=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact2800); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN182);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:28: ( constraints )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==ID||LA67_0==LEFT_PAREN) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:28: constraints
                    {
                    pushFollow(FOLLOW_constraints_in_fact2802);
                    constraints183=constraints();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_constraints.add(constraints183.getTree());

                    }
                    break;

            }

            RIGHT_PAREN184=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact2805); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN184);


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
            // 635:2: -> ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:5: ^( VT_FACT pattern_type ( constraints )? RIGHT_PAREN )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_FACT, "VT_FACT"), root_1);

                adaptor.addChild(root_1, stream_pattern_type.next());
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:28: ( constraints )?
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:638:1: constraints : constraint ( COMMA constraint )* ;
    public final constraints_return constraints() throws RecognitionException {
        constraints_return retval = new constraints_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA186=null;
        constraint_return constraint185 = null;

        constraint_return constraint187 = null;


        Object COMMA186_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:2: ( constraint ( COMMA constraint )* )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:4: constraint ( COMMA constraint )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_in_constraints2830);
            constraint185=constraint();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, constraint185.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:15: ( COMMA constraint )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==COMMA) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:17: COMMA constraint
            	    {
            	    COMMA186=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_constraints2834); if (failed) return retval;
            	    pushFollow(FOLLOW_constraint_in_constraints2837);
            	    constraint187=constraint();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, constraint187.getTree());

            	    }
            	    break;

            	default :
            	    break loop68;
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:1: constraint : or_constr ;
    public final constraint_return constraint() throws RecognitionException {
        constraint_return retval = new constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        or_constr_return or_constr188 = null;



        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:643:2: ( or_constr )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:643:4: or_constr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_or_constr_in_constraint2851);
            or_constr188=or_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, or_constr188.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:646:1: or_constr : and_constr ( DOUBLE_PIPE and_constr )* ;
    public final or_constr_return or_constr() throws RecognitionException {
        or_constr_return retval = new or_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE190=null;
        and_constr_return and_constr189 = null;

        and_constr_return and_constr191 = null;


        Object DOUBLE_PIPE190_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:647:2: ( and_constr ( DOUBLE_PIPE and_constr )* )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:647:4: and_constr ( DOUBLE_PIPE and_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_constr_in_or_constr2862);
            and_constr189=and_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, and_constr189.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:647:15: ( DOUBLE_PIPE and_constr )*
            loop69:
            do {
                int alt69=2;
                int LA69_0 = input.LA(1);

                if ( (LA69_0==DOUBLE_PIPE) ) {
                    alt69=1;
                }


                switch (alt69) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:647:17: DOUBLE_PIPE and_constr
            	    {
            	    DOUBLE_PIPE190=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr2866); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_PIPE190_tree = (Object)adaptor.create(DOUBLE_PIPE190);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE190_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr2869);
            	    and_constr191=and_constr();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, and_constr191.getTree());

            	    }
            	    break;

            	default :
            	    break loop69;
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:650:1: and_constr : unary_constr ( DOUBLE_AMPER unary_constr )* ;
    public final and_constr_return and_constr() throws RecognitionException {
        and_constr_return retval = new and_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER193=null;
        unary_constr_return unary_constr192 = null;

        unary_constr_return unary_constr194 = null;


        Object DOUBLE_AMPER193_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:651:2: ( unary_constr ( DOUBLE_AMPER unary_constr )* )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:651:4: unary_constr ( DOUBLE_AMPER unary_constr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_constr_in_and_constr2884);
            unary_constr192=unary_constr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, unary_constr192.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:651:17: ( DOUBLE_AMPER unary_constr )*
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);

                if ( (LA70_0==DOUBLE_AMPER) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:651:19: DOUBLE_AMPER unary_constr
            	    {
            	    DOUBLE_AMPER193=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr2888); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_AMPER193_tree = (Object)adaptor.create(DOUBLE_AMPER193);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER193_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr2891);
            	    unary_constr194=unary_constr();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, unary_constr194.getTree());

            	    }
            	    break;

            	default :
            	    break loop70;
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:654:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );
    public final unary_constr_return unary_constr() throws RecognitionException {
        unary_constr_return retval = new unary_constr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN198=null;
        Token RIGHT_PAREN200=null;
        eval_key_return eval_key195 = null;

        paren_chunk_return paren_chunk196 = null;

        field_constraint_return field_constraint197 = null;

        or_constr_return or_constr199 = null;


        Object LEFT_PAREN198_tree=null;
        Object RIGHT_PAREN200_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:656:2: ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN )
            int alt71=3;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==ID) ) {
                int LA71_1 = input.LA(2);

                if ( ((LA71_1>=ID && LA71_1<=DOT)||LA71_1==COLON||(LA71_1>=EQUAL && LA71_1<=GRAVE_ACCENT)||LA71_1==LEFT_SQUARE) ) {
                    alt71=2;
                }
                else if ( (LA71_1==LEFT_PAREN) ) {
                    int LA71_14 = input.LA(3);

                    if ( ((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                        alt71=1;
                    }
                    else if ( (true) ) {
                        alt71=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("654:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 71, 14, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("654:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 71, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA71_0==LEFT_PAREN) ) {
                alt71=3;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("654:1: unary_constr options {k=2; } : ( eval_key paren_chunk | field_constraint | LEFT_PAREN or_constr RIGHT_PAREN );", 71, 0, input);

                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:656:4: eval_key paren_chunk
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_eval_key_in_unary_constr2912);
                    eval_key195=eval_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(eval_key195.getTree(), root_0);
                    pushFollow(FOLLOW_paren_chunk_in_unary_constr2915);
                    paren_chunk196=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk196.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:657:4: field_constraint
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_field_constraint_in_unary_constr2920);
                    field_constraint197=field_constraint();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, field_constraint197.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:658:4: LEFT_PAREN or_constr RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN198=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr2925); if (failed) return retval;
                    pushFollow(FOLLOW_or_constr_in_unary_constr2928);
                    or_constr199=or_constr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, or_constr199.getTree());
                    RIGHT_PAREN200=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr2930); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN200_tree = (Object)adaptor.create(RIGHT_PAREN200);
                    adaptor.addChild(root_0, RIGHT_PAREN200_tree);
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:661:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );
    public final field_constraint_return field_constraint() throws RecognitionException {
        field_constraint_return retval = new field_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token arw=null;
        label_return label201 = null;

        accessor_path_return accessor_path202 = null;

        or_restr_connective_return or_restr_connective203 = null;

        paren_chunk_return paren_chunk204 = null;

        accessor_path_return accessor_path205 = null;

        or_restr_connective_return or_restr_connective206 = null;


        Object arw_tree=null;
        RewriteRuleTokenStream stream_ARROW=new RewriteRuleTokenStream(adaptor,"token ARROW");
        RewriteRuleSubtreeStream stream_accessor_path=new RewriteRuleSubtreeStream(adaptor,"rule accessor_path");
        RewriteRuleSubtreeStream stream_label=new RewriteRuleSubtreeStream(adaptor,"rule label");
        RewriteRuleSubtreeStream stream_paren_chunk=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk");
        RewriteRuleSubtreeStream stream_or_restr_connective=new RewriteRuleSubtreeStream(adaptor,"rule or_restr_connective");

        	boolean isArrow = false;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:664:3: ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==ID) ) {
                int LA73_1 = input.LA(2);

                if ( (LA73_1==COLON) ) {
                    alt73=1;
                }
                else if ( ((LA73_1>=ID && LA73_1<=DOT)||LA73_1==LEFT_PAREN||(LA73_1>=EQUAL && LA73_1<=GRAVE_ACCENT)||LA73_1==LEFT_SQUARE) ) {
                    alt73=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("661:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );", 73, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("661:1: field_constraint : ( label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )? -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )? -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) ) | accessor_path or_restr_connective -> ^( VT_FIELD accessor_path or_restr_connective ) );", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:664:5: label accessor_path ( or_restr_connective | arw= ARROW paren_chunk )?
                    {
                    pushFollow(FOLLOW_label_in_field_constraint2944);
                    label201=label();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_label.add(label201.getTree());
                    pushFollow(FOLLOW_accessor_path_in_field_constraint2946);
                    accessor_path202=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accessor_path.add(accessor_path202.getTree());
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:664:25: ( or_restr_connective | arw= ARROW paren_chunk )?
                    int alt72=3;
                    int LA72_0 = input.LA(1);

                    if ( (LA72_0==ID||LA72_0==LEFT_PAREN||(LA72_0>=EQUAL && LA72_0<=GRAVE_ACCENT)) ) {
                        alt72=1;
                    }
                    else if ( (LA72_0==ARROW) ) {
                        alt72=2;
                    }
                    switch (alt72) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:664:27: or_restr_connective
                            {
                            pushFollow(FOLLOW_or_restr_connective_in_field_constraint2950);
                            or_restr_connective203=or_restr_connective();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_or_restr_connective.add(or_restr_connective203.getTree());

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:664:49: arw= ARROW paren_chunk
                            {
                            arw=(Token)input.LT(1);
                            match(input,ARROW,FOLLOW_ARROW_in_field_constraint2956); if (failed) return retval;
                            if ( backtracking==0 ) stream_ARROW.add(arw);

                            pushFollow(FOLLOW_paren_chunk_in_field_constraint2958);
                            paren_chunk204=paren_chunk();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_paren_chunk.add(paren_chunk204.getTree());
                            if ( backtracking==0 ) {
                              isArrow = true;
                            }

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: accessor_path, label, paren_chunk, label, or_restr_connective, accessor_path
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 665:3: -> {isArrow}? ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) ) ( ^( VK_EVAL[$arw] paren_chunk ) )?
                    if (isArrow) {
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:665:17: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.next());
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:665:39: ^( VT_FIELD accessor_path )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.next());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:665:66: ( ^( VK_EVAL[$arw] paren_chunk ) )?
                        if ( stream_paren_chunk.hasNext() ) {
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:665:66: ^( VK_EVAL[$arw] paren_chunk )
                            {
                            Object root_1 = (Object)adaptor.nil();
                            root_1 = (Object)adaptor.becomeRoot(adaptor.create(VK_EVAL, arw), root_1);

                            adaptor.addChild(root_1, stream_paren_chunk.next());

                            adaptor.addChild(root_0, root_1);
                            }

                        }
                        stream_paren_chunk.reset();

                    }
                    else // 666:3: -> ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                    {
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:666:6: ^( VT_BIND_FIELD label ^( VT_FIELD accessor_path ( or_restr_connective )? ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_BIND_FIELD, "VT_BIND_FIELD"), root_1);

                        adaptor.addChild(root_1, stream_label.next());
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:666:28: ^( VT_FIELD accessor_path ( or_restr_connective )? )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(adaptor.create(VT_FIELD, "VT_FIELD"), root_2);

                        adaptor.addChild(root_2, stream_accessor_path.next());
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:666:53: ( or_restr_connective )?
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
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:667:4: accessor_path or_restr_connective
                    {
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3012);
                    accessor_path205=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_accessor_path.add(accessor_path205.getTree());
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint3014);
                    or_restr_connective206=or_restr_connective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_or_restr_connective.add(or_restr_connective206.getTree());

                    // AST REWRITE
                    // elements: accessor_path, or_restr_connective
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 668:3: -> ^( VT_FIELD accessor_path or_restr_connective )
                    {
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:668:6: ^( VT_FIELD accessor_path or_restr_connective )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:671:1: label : value= ID COLON -> VT_LABEL[$value] ;
    public final label_return label() throws RecognitionException {
        label_return retval = new label_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;
        Token COLON207=null;

        Object value_tree=null;
        Object COLON207_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:671:7: (value= ID COLON -> VT_LABEL[$value] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:671:9: value= ID COLON
            {
            value=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_label3038); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(value);

            COLON207=(Token)input.LT(1);
            match(input,COLON,FOLLOW_COLON_in_label3040); if (failed) return retval;
            if ( backtracking==0 ) stream_COLON.add(COLON207);


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
            // 671:24: -> VT_LABEL[$value]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:674:1: or_restr_connective : and_restr_connective ( ( DOUBLE_PIPE )=> DOUBLE_PIPE and_restr_connective )* ;
    public final or_restr_connective_return or_restr_connective() throws RecognitionException {
        or_restr_connective_return retval = new or_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE209=null;
        and_restr_connective_return and_restr_connective208 = null;

        and_restr_connective_return and_restr_connective210 = null;


        Object DOUBLE_PIPE209_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:675:2: ( and_restr_connective ( ( DOUBLE_PIPE )=> DOUBLE_PIPE and_restr_connective )* )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:675:4: and_restr_connective ( ( DOUBLE_PIPE )=> DOUBLE_PIPE and_restr_connective )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3056);
            and_restr_connective208=and_restr_connective();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, and_restr_connective208.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:675:25: ( ( DOUBLE_PIPE )=> DOUBLE_PIPE and_restr_connective )*
            loop74:
            do {
                int alt74=2;
                alt74 = dfa74.predict(input);
                switch (alt74) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:675:26: ( DOUBLE_PIPE )=> DOUBLE_PIPE and_restr_connective
            	    {
            	    DOUBLE_PIPE209=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective3064); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_PIPE209_tree = (Object)adaptor.create(DOUBLE_PIPE209);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE209_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3067);
            	    and_restr_connective210=and_restr_connective();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, and_restr_connective210.getTree());

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
    // $ANTLR end or_restr_connective

    public static class and_restr_connective_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start and_restr_connective
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:678:1: and_restr_connective : constraint_expression ( ( DOUBLE_AMPER )=> DOUBLE_AMPER constraint_expression )* ;
    public final and_restr_connective_return and_restr_connective() throws RecognitionException {
        and_restr_connective_return retval = new and_restr_connective_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER212=null;
        constraint_expression_return constraint_expression211 = null;

        constraint_expression_return constraint_expression213 = null;


        Object DOUBLE_AMPER212_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:679:2: ( constraint_expression ( ( DOUBLE_AMPER )=> DOUBLE_AMPER constraint_expression )* )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:679:4: constraint_expression ( ( DOUBLE_AMPER )=> DOUBLE_AMPER constraint_expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3082);
            constraint_expression211=constraint_expression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, constraint_expression211.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:679:26: ( ( DOUBLE_AMPER )=> DOUBLE_AMPER constraint_expression )*
            loop75:
            do {
                int alt75=2;
                alt75 = dfa75.predict(input);
                switch (alt75) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:679:27: ( DOUBLE_AMPER )=> DOUBLE_AMPER constraint_expression
            	    {
            	    DOUBLE_AMPER212=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective3090); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOUBLE_AMPER212_tree = (Object)adaptor.create(DOUBLE_AMPER212);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER212_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3093);
            	    constraint_expression213=constraint_expression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, constraint_expression213.getTree());

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
    // $ANTLR end and_restr_connective

    public static class constraint_expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start constraint_expression
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:682:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );
    public final constraint_expression_return constraint_expression() throws RecognitionException {
        constraint_expression_return retval = new constraint_expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN216=null;
        Token RIGHT_PAREN218=null;
        compound_operator_return compound_operator214 = null;

        simple_operator_return simple_operator215 = null;

        or_restr_connective_return or_restr_connective217 = null;


        Object LEFT_PAREN216_tree=null;
        Object RIGHT_PAREN218_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:685:3: ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN )
            int alt76=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA76_1 = input.LA(2);

                if ( (LA76_1==ID) ) {
                    int LA76_10 = input.LA(3);

                    if ( (LA76_10==ID||LA76_10==STRING||(LA76_10>=BOOL && LA76_10<=INT)||(LA76_10>=FLOAT && LA76_10<=NULL)) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                        alt76=2;
                    }
                    else if ( (LA76_10==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                        int LA76_16 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                            alt76=1;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                            alt76=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("682:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 76, 16, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA76_10==DOT||(LA76_10>=COMMA && LA76_10<=RIGHT_PAREN)||(LA76_10>=DOUBLE_PIPE && LA76_10<=DOUBLE_AMPER)||LA76_10==LEFT_SQUARE) ) {
                        alt76=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("682:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 76, 10, input);

                        throw nvae;
                    }
                }
                else if ( (LA76_1==STRING||(LA76_1>=BOOL && LA76_1<=INT)||(LA76_1>=FLOAT && LA76_1<=NULL)) ) {
                    alt76=2;
                }
                else if ( (LA76_1==LEFT_PAREN) ) {
                    switch ( input.LA(3) ) {
                    case ID:
                        {
                        int LA76_23 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt76=1;
                        }
                        else if ( (true) ) {
                            alt76=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("682:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 76, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    case STRING:
                    case BOOL:
                    case INT:
                    case FLOAT:
                    case NULL:
                        {
                        int LA76_24 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt76=1;
                        }
                        else if ( (true) ) {
                            alt76=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("682:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 76, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_PAREN:
                        {
                        int LA76_25 = input.LA(4);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                            alt76=1;
                        }
                        else if ( (true) ) {
                            alt76=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("682:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 76, 25, input);

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
                    case VT_RULE_ID:
                    case VT_ENTRYPOINT_ID:
                    case VT_SLOT_ID:
                    case VT_SLOT:
                    case VT_RULE_ATTRIBUTES:
                    case VT_RHS_CHUNK:
                    case VT_CURLY_CHUNK:
                    case VT_SQUARE_CHUNK:
                    case VT_PAREN_CHUNK:
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
                    case COLON:
                    case DOUBLE_PIPE:
                    case DOUBLE_AMPER:
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
                        alt76=2;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("682:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 76, 12, input);

                        throw nvae;
                    }

                }
                else if ( (LA76_1==GRAVE_ACCENT) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                    alt76=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("682:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 76, 1, input);

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
                alt76=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt76=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("682:1: constraint_expression options {k=3; } : ( compound_operator | simple_operator | LEFT_PAREN or_restr_connective RIGHT_PAREN );", 76, 0, input);

                throw nvae;
            }

            switch (alt76) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:685:5: compound_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_compound_operator_in_constraint_expression3115);
                    compound_operator214=compound_operator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, compound_operator214.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:686:4: simple_operator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_simple_operator_in_constraint_expression3120);
                    simple_operator215=simple_operator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, simple_operator215.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:687:4: LEFT_PAREN or_restr_connective RIGHT_PAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN216=(Token)input.LT(1);
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression3125); if (failed) return retval;
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression3128);
                    or_restr_connective217=or_restr_connective();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, or_restr_connective217.getTree());
                    RIGHT_PAREN218=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression3130); if (failed) return retval;
                    if ( backtracking==0 ) {
                    RIGHT_PAREN218_tree = (Object)adaptor.create(RIGHT_PAREN218);
                    adaptor.addChild(root_0, RIGHT_PAREN218_tree);
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
    // $ANTLR end constraint_expression

    public static class simple_operator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start simple_operator
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:690:1: simple_operator : ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) expression_value ;
    public final simple_operator_return simple_operator() throws RecognitionException {
        simple_operator_return retval = new simple_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUAL219=null;
        Token GREATER220=null;
        Token GREATER_EQUAL221=null;
        Token LESS222=null;
        Token LESS_EQUAL223=null;
        Token NOT_EQUAL224=null;
        Token ID230=null;
        Token GRAVE_ACCENT231=null;
        Token ID232=null;
        Token ID239=null;
        Token GRAVE_ACCENT240=null;
        Token ID241=null;
        not_key_return not_key225 = null;

        contains_key_return contains_key226 = null;

        soundslike_key_return soundslike_key227 = null;

        matches_key_return matches_key228 = null;

        memberof_key_return memberof_key229 = null;

        square_chunk_return square_chunk233 = null;

        contains_key_return contains_key234 = null;

        excludes_key_return excludes_key235 = null;

        matches_key_return matches_key236 = null;

        soundslike_key_return soundslike_key237 = null;

        memberof_key_return memberof_key238 = null;

        square_chunk_return square_chunk242 = null;

        expression_value_return expression_value243 = null;


        Object EQUAL219_tree=null;
        Object GREATER220_tree=null;
        Object GREATER_EQUAL221_tree=null;
        Object LESS222_tree=null;
        Object LESS_EQUAL223_tree=null;
        Object NOT_EQUAL224_tree=null;
        Object ID230_tree=null;
        Object GRAVE_ACCENT231_tree=null;
        Object ID232_tree=null;
        Object ID239_tree=null;
        Object GRAVE_ACCENT240_tree=null;
        Object ID241_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:2: ( ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) expression_value )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) expression_value
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )
            int alt78=14;
            switch ( input.LA(1) ) {
            case EQUAL:
                {
                alt78=1;
                }
                break;
            case GREATER:
                {
                alt78=2;
                }
                break;
            case GREATER_EQUAL:
                {
                alt78=3;
                }
                break;
            case LESS:
                {
                alt78=4;
                }
                break;
            case LESS_EQUAL:
                {
                alt78=5;
                }
                break;
            case NOT_EQUAL:
                {
                alt78=6;
                }
                break;
            case ID:
                {
                int LA78_7 = input.LA(2);

                if ( (LA78_7==ID||LA78_7==GRAVE_ACCENT) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                    alt78=7;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.CONTAINS))) ) {
                    alt78=8;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))) ) {
                    alt78=9;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.MATCHES))) ) {
                    alt78=10;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))) ) {
                    alt78=11;
                }
                else if ( ((validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))) ) {
                    alt78=12;
                }
                else if ( (true) ) {
                    alt78=13;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("691:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 78, 7, input);

                    throw nvae;
                }
                }
                break;
            case GRAVE_ACCENT:
                {
                alt78=14;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("691:4: ( EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | NOT_EQUAL | not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk ) | contains_key | excludes_key | matches_key | soundslike_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 78, 0, input);

                throw nvae;
            }

            switch (alt78) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:5: EQUAL
                    {
                    EQUAL219=(Token)input.LT(1);
                    match(input,EQUAL,FOLLOW_EQUAL_in_simple_operator3142); if (failed) return retval;
                    if ( backtracking==0 ) {
                    EQUAL219_tree = (Object)adaptor.create(EQUAL219);
                    root_0 = (Object)adaptor.becomeRoot(EQUAL219_tree, root_0);
                    }

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:692:4: GREATER
                    {
                    GREATER220=(Token)input.LT(1);
                    match(input,GREATER,FOLLOW_GREATER_in_simple_operator3148); if (failed) return retval;
                    if ( backtracking==0 ) {
                    GREATER220_tree = (Object)adaptor.create(GREATER220);
                    root_0 = (Object)adaptor.becomeRoot(GREATER220_tree, root_0);
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:693:4: GREATER_EQUAL
                    {
                    GREATER_EQUAL221=(Token)input.LT(1);
                    match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_simple_operator3154); if (failed) return retval;
                    if ( backtracking==0 ) {
                    GREATER_EQUAL221_tree = (Object)adaptor.create(GREATER_EQUAL221);
                    root_0 = (Object)adaptor.becomeRoot(GREATER_EQUAL221_tree, root_0);
                    }

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:694:4: LESS
                    {
                    LESS222=(Token)input.LT(1);
                    match(input,LESS,FOLLOW_LESS_in_simple_operator3160); if (failed) return retval;
                    if ( backtracking==0 ) {
                    LESS222_tree = (Object)adaptor.create(LESS222);
                    root_0 = (Object)adaptor.becomeRoot(LESS222_tree, root_0);
                    }

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:695:4: LESS_EQUAL
                    {
                    LESS_EQUAL223=(Token)input.LT(1);
                    match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_simple_operator3166); if (failed) return retval;
                    if ( backtracking==0 ) {
                    LESS_EQUAL223_tree = (Object)adaptor.create(LESS_EQUAL223);
                    root_0 = (Object)adaptor.becomeRoot(LESS_EQUAL223_tree, root_0);
                    }

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:696:4: NOT_EQUAL
                    {
                    NOT_EQUAL224=(Token)input.LT(1);
                    match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_simple_operator3172); if (failed) return retval;
                    if ( backtracking==0 ) {
                    NOT_EQUAL224_tree = (Object)adaptor.create(NOT_EQUAL224);
                    root_0 = (Object)adaptor.becomeRoot(NOT_EQUAL224_tree, root_0);
                    }

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:4: not_key ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )
                    {
                    pushFollow(FOLLOW_not_key_in_simple_operator3178);
                    not_key225=not_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, not_key225.getTree());
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:12: ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )
                    int alt77=6;
                    int LA77_0 = input.LA(1);

                    if ( (LA77_0==ID) ) {
                        int LA77_1 = input.LA(2);

                        if ( ((validateIdentifierKey(DroolsSoftKeywords.CONTAINS))) ) {
                            alt77=1;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))) ) {
                            alt77=2;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.MATCHES))) ) {
                            alt77=3;
                        }
                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))) ) {
                            alt77=4;
                        }
                        else if ( (true) ) {
                            alt77=5;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("697:12: ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 77, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA77_0==GRAVE_ACCENT) ) {
                        alt77=6;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("697:12: ( contains_key | soundslike_key | matches_key | memberof_key | ID | GRAVE_ACCENT ID square_chunk )", 77, 0, input);

                        throw nvae;
                    }
                    switch (alt77) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:13: contains_key
                            {
                            pushFollow(FOLLOW_contains_key_in_simple_operator3181);
                            contains_key226=contains_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(contains_key226.getTree(), root_0);

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:27: soundslike_key
                            {
                            pushFollow(FOLLOW_soundslike_key_in_simple_operator3184);
                            soundslike_key227=soundslike_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(soundslike_key227.getTree(), root_0);

                            }
                            break;
                        case 3 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:43: matches_key
                            {
                            pushFollow(FOLLOW_matches_key_in_simple_operator3187);
                            matches_key228=matches_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(matches_key228.getTree(), root_0);

                            }
                            break;
                        case 4 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:56: memberof_key
                            {
                            pushFollow(FOLLOW_memberof_key_in_simple_operator3190);
                            memberof_key229=memberof_key();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(memberof_key229.getTree(), root_0);

                            }
                            break;
                        case 5 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:71: ID
                            {
                            ID230=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_simple_operator3194); if (failed) return retval;
                            if ( backtracking==0 ) {
                            ID230_tree = (Object)adaptor.create(ID230);
                            root_0 = (Object)adaptor.becomeRoot(ID230_tree, root_0);
                            }

                            }
                            break;
                        case 6 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:77: GRAVE_ACCENT ID square_chunk
                            {
                            GRAVE_ACCENT231=(Token)input.LT(1);
                            match(input,GRAVE_ACCENT,FOLLOW_GRAVE_ACCENT_in_simple_operator3199); if (failed) return retval;
                            ID232=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_simple_operator3202); if (failed) return retval;
                            if ( backtracking==0 ) {
                            ID232_tree = (Object)adaptor.create(ID232);
                            root_0 = (Object)adaptor.becomeRoot(ID232_tree, root_0);
                            }
                            pushFollow(FOLLOW_square_chunk_in_simple_operator3205);
                            square_chunk233=square_chunk();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk233.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:4: contains_key
                    {
                    pushFollow(FOLLOW_contains_key_in_simple_operator3211);
                    contains_key234=contains_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(contains_key234.getTree(), root_0);

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:699:4: excludes_key
                    {
                    pushFollow(FOLLOW_excludes_key_in_simple_operator3217);
                    excludes_key235=excludes_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(excludes_key235.getTree(), root_0);

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:700:4: matches_key
                    {
                    pushFollow(FOLLOW_matches_key_in_simple_operator3223);
                    matches_key236=matches_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(matches_key236.getTree(), root_0);

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:701:4: soundslike_key
                    {
                    pushFollow(FOLLOW_soundslike_key_in_simple_operator3229);
                    soundslike_key237=soundslike_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(soundslike_key237.getTree(), root_0);

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:702:4: memberof_key
                    {
                    pushFollow(FOLLOW_memberof_key_in_simple_operator3235);
                    memberof_key238=memberof_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(memberof_key238.getTree(), root_0);

                    }
                    break;
                case 13 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:703:4: ID
                    {
                    ID239=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator3241); if (failed) return retval;
                    if ( backtracking==0 ) {
                    ID239_tree = (Object)adaptor.create(ID239);
                    root_0 = (Object)adaptor.becomeRoot(ID239_tree, root_0);
                    }

                    }
                    break;
                case 14 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:704:4: GRAVE_ACCENT ID square_chunk
                    {
                    GRAVE_ACCENT240=(Token)input.LT(1);
                    match(input,GRAVE_ACCENT,FOLLOW_GRAVE_ACCENT_in_simple_operator3247); if (failed) return retval;
                    ID241=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator3250); if (failed) return retval;
                    if ( backtracking==0 ) {
                    ID241_tree = (Object)adaptor.create(ID241);
                    root_0 = (Object)adaptor.becomeRoot(ID241_tree, root_0);
                    }
                    pushFollow(FOLLOW_square_chunk_in_simple_operator3253);
                    square_chunk242=square_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk242.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_expression_value_in_simple_operator3257);
            expression_value243=expression_value();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression_value243.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:709:1: compound_operator : ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN ;
    public final compound_operator_return compound_operator() throws RecognitionException {
        compound_operator_return retval = new compound_operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN247=null;
        Token COMMA249=null;
        Token RIGHT_PAREN251=null;
        in_key_return in_key244 = null;

        not_key_return not_key245 = null;

        in_key_return in_key246 = null;

        expression_value_return expression_value248 = null;

        expression_value_return expression_value250 = null;


        Object LEFT_PAREN247_tree=null;
        Object COMMA249_tree=null;
        Object RIGHT_PAREN251_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:710:2: ( ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:710:4: ( in_key | not_key in_key ) LEFT_PAREN expression_value ( COMMA expression_value )* RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:710:4: ( in_key | not_key in_key )
            int alt79=2;
            int LA79_0 = input.LA(1);

            if ( (LA79_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IN))||(validateIdentifierKey(DroolsSoftKeywords.NOT))))) {
                int LA79_1 = input.LA(2);

                if ( (LA79_1==ID) && ((validateIdentifierKey(DroolsSoftKeywords.NOT)))) {
                    alt79=2;
                }
                else if ( (LA79_1==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.IN)))) {
                    alt79=1;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("710:4: ( in_key | not_key in_key )", 79, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("710:4: ( in_key | not_key in_key )", 79, 0, input);

                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:710:6: in_key
                    {
                    pushFollow(FOLLOW_in_key_in_compound_operator3272);
                    in_key244=in_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key244.getTree(), root_0);

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:710:16: not_key in_key
                    {
                    pushFollow(FOLLOW_not_key_in_compound_operator3277);
                    not_key245=not_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, not_key245.getTree());
                    pushFollow(FOLLOW_in_key_in_compound_operator3279);
                    in_key246=in_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(in_key246.getTree(), root_0);

                    }
                    break;

            }

            LEFT_PAREN247=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator3284); if (failed) return retval;
            pushFollow(FOLLOW_expression_value_in_compound_operator3287);
            expression_value248=expression_value();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression_value248.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:710:63: ( COMMA expression_value )*
            loop80:
            do {
                int alt80=2;
                int LA80_0 = input.LA(1);

                if ( (LA80_0==COMMA) ) {
                    alt80=1;
                }


                switch (alt80) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:710:65: COMMA expression_value
            	    {
            	    COMMA249=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_compound_operator3291); if (failed) return retval;
            	    pushFollow(FOLLOW_expression_value_in_compound_operator3294);
            	    expression_value250=expression_value();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, expression_value250.getTree());

            	    }
            	    break;

            	default :
            	    break loop80;
                }
            } while (true);

            RIGHT_PAREN251=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator3299); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_PAREN251_tree = (Object)adaptor.create(RIGHT_PAREN251);
            adaptor.addChild(root_0, RIGHT_PAREN251_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:713:1: expression_value : ( accessor_path | literal_constraint | paren_chunk );
    public final expression_value_return expression_value() throws RecognitionException {
        expression_value_return retval = new expression_value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        accessor_path_return accessor_path252 = null;

        literal_constraint_return literal_constraint253 = null;

        paren_chunk_return paren_chunk254 = null;



        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:714:2: ( accessor_path | literal_constraint | paren_chunk )
            int alt81=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt81=1;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt81=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt81=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("713:1: expression_value : ( accessor_path | literal_constraint | paren_chunk );", 81, 0, input);

                throw nvae;
            }

            switch (alt81) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:714:4: accessor_path
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_accessor_path_in_expression_value3310);
                    accessor_path252=accessor_path();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, accessor_path252.getTree());

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:715:4: literal_constraint
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_constraint_in_expression_value3315);
                    literal_constraint253=literal_constraint();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, literal_constraint253.getTree());

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:716:4: paren_chunk
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_paren_chunk_in_expression_value3321);
                    paren_chunk254=paren_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk254.getTree());

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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:719:1: literal_constraint : ( STRING | INT | FLOAT | BOOL | NULL );
    public final literal_constraint_return literal_constraint() throws RecognitionException {
        literal_constraint_return retval = new literal_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set255=null;

        Object set255_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:720:2: ( STRING | INT | FLOAT | BOOL | NULL )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
            {
            root_0 = (Object)adaptor.nil();

            set255=(Token)input.LT(1);
            if ( input.LA(1)==STRING||(input.LA(1)>=BOOL && input.LA(1)<=INT)||(input.LA(1)>=FLOAT && input.LA(1)<=NULL) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set255));
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:727:1: pattern_type : id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final pattern_type_return pattern_type() throws RecognitionException {
        pattern_type_return retval = new pattern_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        List list_id=null;
        dimension_definition_return dimension_definition256 = null;


        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:728:2: (id+= ID (id+= DOT id+= ID )* ( dimension_definition )* -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:728:4: id+= ID (id+= DOT id+= ID )* ( dimension_definition )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_pattern_type3365); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(id);

            if (list_id==null) list_id=new ArrayList();
            list_id.add(id);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:728:11: (id+= DOT id+= ID )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);

                if ( (LA82_0==DOT) ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:728:13: id+= DOT id+= ID
            	    {
            	    id=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pattern_type3371); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_pattern_type3375); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(id);

            	    if (list_id==null) list_id=new ArrayList();
            	    list_id.add(id);


            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);

            if ( backtracking==0 ) {
              	setParaphrasesValue(DroolsParaphareseTypes.PATTERN, buildStringFromTokens(list_id));	
            }
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:730:6: ( dimension_definition )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);

                if ( (LA83_0==LEFT_SQUARE) ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:730:6: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_pattern_type3390);
            	    dimension_definition256=dimension_definition();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_dimension_definition.add(dimension_definition256.getTree());

            	    }
            	    break;

            	default :
            	    break loop83;
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
            // 731:3: -> ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:6: ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
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
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:28: ( dimension_definition )*
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:734:1: data_type : ID ( DOT ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final data_type_return data_type() throws RecognitionException {
        data_type_return retval = new data_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID257=null;
        Token DOT258=null;
        Token ID259=null;
        dimension_definition_return dimension_definition260 = null;


        Object ID257_tree=null;
        Object DOT258_tree=null;
        Object ID259_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_dimension_definition=new RewriteRuleSubtreeStream(adaptor,"rule dimension_definition");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:2: ( ID ( DOT ID )* ( dimension_definition )* -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:4: ID ( DOT ID )* ( dimension_definition )*
            {
            ID257=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_data_type3416); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID257);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:7: ( DOT ID )*
            loop84:
            do {
                int alt84=2;
                int LA84_0 = input.LA(1);

                if ( (LA84_0==DOT) ) {
                    alt84=1;
                }


                switch (alt84) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:9: DOT ID
            	    {
            	    DOT258=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_data_type3420); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(DOT258);

            	    ID259=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_data_type3422); if (failed) return retval;
            	    if ( backtracking==0 ) stream_ID.add(ID259);


            	    }
            	    break;

            	default :
            	    break loop84;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:19: ( dimension_definition )*
            loop85:
            do {
                int alt85=2;
                int LA85_0 = input.LA(1);

                if ( (LA85_0==LEFT_SQUARE) ) {
                    alt85=1;
                }


                switch (alt85) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:735:19: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_data_type3427);
            	    dimension_definition260=dimension_definition();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_dimension_definition.add(dimension_definition260.getTree());

            	    }
            	    break;

            	default :
            	    break loop85;
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
            // 736:3: -> ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:736:6: ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
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
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:736:25: ( dimension_definition )*
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:739:1: dimension_definition : LEFT_SQUARE RIGHT_SQUARE ;
    public final dimension_definition_return dimension_definition() throws RecognitionException {
        dimension_definition_return retval = new dimension_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE261=null;
        Token RIGHT_SQUARE262=null;

        Object LEFT_SQUARE261_tree=null;
        Object RIGHT_SQUARE262_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:740:2: ( LEFT_SQUARE RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:740:4: LEFT_SQUARE RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE261=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dimension_definition3453); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_SQUARE261_tree = (Object)adaptor.create(LEFT_SQUARE261);
            adaptor.addChild(root_0, LEFT_SQUARE261_tree);
            }
            RIGHT_SQUARE262=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dimension_definition3455); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_SQUARE262_tree = (Object)adaptor.create(RIGHT_SQUARE262);
            adaptor.addChild(root_0, RIGHT_SQUARE262_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:1: accessor_path : accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) ;
    public final accessor_path_return accessor_path() throws RecognitionException {
        accessor_path_return retval = new accessor_path_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT264=null;
        accessor_element_return accessor_element263 = null;

        accessor_element_return accessor_element265 = null;


        Object DOT264_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_accessor_element=new RewriteRuleSubtreeStream(adaptor,"rule accessor_element");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:2: ( accessor_element ( DOT accessor_element )* -> ^( VT_ACCESSOR_PATH ( accessor_element )+ ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:4: accessor_element ( DOT accessor_element )*
            {
            pushFollow(FOLLOW_accessor_element_in_accessor_path3466);
            accessor_element263=accessor_element();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_accessor_element.add(accessor_element263.getTree());
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:21: ( DOT accessor_element )*
            loop86:
            do {
                int alt86=2;
                int LA86_0 = input.LA(1);

                if ( (LA86_0==DOT) ) {
                    alt86=1;
                }


                switch (alt86) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:23: DOT accessor_element
            	    {
            	    DOT264=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_accessor_path3470); if (failed) return retval;
            	    if ( backtracking==0 ) stream_DOT.add(DOT264);

            	    pushFollow(FOLLOW_accessor_element_in_accessor_path3472);
            	    accessor_element265=accessor_element();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_accessor_element.add(accessor_element265.getTree());

            	    }
            	    break;

            	default :
            	    break loop86;
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
            // 745:2: -> ^( VT_ACCESSOR_PATH ( accessor_element )+ )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:5: ^( VT_ACCESSOR_PATH ( accessor_element )+ )
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:748:1: accessor_element : ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) ;
    public final accessor_element_return accessor_element() throws RecognitionException {
        accessor_element_return retval = new accessor_element_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID266=null;
        square_chunk_return square_chunk267 = null;


        Object ID266_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_square_chunk=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk");
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:749:2: ( ID ( square_chunk )* -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:749:4: ID ( square_chunk )*
            {
            ID266=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accessor_element3496); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID266);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:749:7: ( square_chunk )*
            loop87:
            do {
                int alt87=2;
                int LA87_0 = input.LA(1);

                if ( (LA87_0==LEFT_SQUARE) ) {
                    alt87=1;
                }


                switch (alt87) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:749:7: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_accessor_element3498);
            	    square_chunk267=square_chunk();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_square_chunk.add(square_chunk267.getTree());

            	    }
            	    break;

            	default :
            	    break loop87;
                }
            } while (true);


            // AST REWRITE
            // elements: square_chunk, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 750:2: -> ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
            {
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:750:5: ^( VT_ACCESSOR_ELEMENT ID ( square_chunk )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ACCESSOR_ELEMENT, "VT_ACCESSOR_ELEMENT"), root_1);

                adaptor.addChild(root_1, stream_ID.next());
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:750:30: ( square_chunk )*
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:753:1: rhs_chunk : rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] ;
    public final rhs_chunk_return rhs_chunk() throws RecognitionException {
        rhs_chunk_return retval = new rhs_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        rhs_chunk_data_return rc = null;


        RewriteRuleSubtreeStream stream_rhs_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule rhs_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:756:3: (rc= rhs_chunk_data -> VT_RHS_CHUNK[$rc.start,text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:756:5: rc= rhs_chunk_data
            {
            pushFollow(FOLLOW_rhs_chunk_data_in_rhs_chunk3527);
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
            // 757:2: -> VT_RHS_CHUNK[$rc.start,text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:760:1: rhs_chunk_data : THEN (~ END )* END ( SEMICOLON )? ;
    public final rhs_chunk_data_return rhs_chunk_data() throws RecognitionException {
        rhs_chunk_data_return retval = new rhs_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token THEN268=null;
        Token set269=null;
        Token END270=null;
        Token SEMICOLON271=null;

        Object THEN268_tree=null;
        Object set269_tree=null;
        Object END270_tree=null;
        Object SEMICOLON271_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:761:2: ( THEN (~ END )* END ( SEMICOLON )? )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:761:4: THEN (~ END )* END ( SEMICOLON )?
            {
            root_0 = (Object)adaptor.nil();

            THEN268=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk_data3546); if (failed) return retval;
            if ( backtracking==0 ) {
            THEN268_tree = (Object)adaptor.create(THEN268);
            adaptor.addChild(root_0, THEN268_tree);
            }
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:761:9: (~ END )*
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);

                if ( ((LA88_0>=VT_COMPILATION_UNIT && LA88_0<=DOT_STAR)||(LA88_0>=STRING && LA88_0<=MULTI_LINE_COMMENT)) ) {
                    alt88=1;
                }


                switch (alt88) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:761:11: ~ END
            	    {
            	    set269=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=DOT_STAR)||(input.LA(1)>=STRING && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set269));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk_data3550);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop88;
                }
            } while (true);

            END270=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_rhs_chunk_data3556); if (failed) return retval;
            if ( backtracking==0 ) {
            END270_tree = (Object)adaptor.create(END270);
            adaptor.addChild(root_0, END270_tree);
            }
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:761:23: ( SEMICOLON )?
            int alt89=2;
            int LA89_0 = input.LA(1);

            if ( (LA89_0==SEMICOLON) ) {
                alt89=1;
            }
            switch (alt89) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:761:23: SEMICOLON
                    {
                    SEMICOLON271=(Token)input.LT(1);
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_rhs_chunk_data3558); if (failed) return retval;
                    if ( backtracking==0 ) {
                    SEMICOLON271_tree = (Object)adaptor.create(SEMICOLON271);
                    adaptor.addChild(root_0, SEMICOLON271_tree);
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:1: curly_chunk : cc= curly_chunk_data -> VT_CURLY_CHUNK[$cc.start,text] ;
    public final curly_chunk_return curly_chunk() throws RecognitionException {
        curly_chunk_return retval = new curly_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        curly_chunk_data_return cc = null;


        RewriteRuleSubtreeStream stream_curly_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule curly_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:767:3: (cc= curly_chunk_data -> VT_CURLY_CHUNK[$cc.start,text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:767:5: cc= curly_chunk_data
            {
            pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk3575);
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
            // 768:2: -> VT_CURLY_CHUNK[$cc.start,text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:771:1: curly_chunk_data : LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )* RIGHT_CURLY ;
    public final curly_chunk_data_return curly_chunk_data() throws RecognitionException {
        curly_chunk_data_return retval = new curly_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_CURLY272=null;
        Token set273=null;
        Token RIGHT_CURLY275=null;
        curly_chunk_data_return curly_chunk_data274 = null;


        Object LEFT_CURLY272_tree=null;
        Object set273_tree=null;
        Object RIGHT_CURLY275_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:2: ( LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )* RIGHT_CURLY )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:4: LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )* RIGHT_CURLY
            {
            root_0 = (Object)adaptor.nil();

            LEFT_CURLY272=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk_data3594); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_CURLY272_tree = (Object)adaptor.create(LEFT_CURLY272);
            adaptor.addChild(root_0, LEFT_CURLY272_tree);
            }
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:15: (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )*
            loop90:
            do {
                int alt90=3;
                int LA90_0 = input.LA(1);

                if ( ((LA90_0>=VT_COMPILATION_UNIT && LA90_0<=THEN)||(LA90_0>=MISC && LA90_0<=MULTI_LINE_COMMENT)) ) {
                    alt90=1;
                }
                else if ( (LA90_0==LEFT_CURLY) ) {
                    alt90=2;
                }


                switch (alt90) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:16: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    set273=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=THEN)||(input.LA(1)>=MISC && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set273));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk_data3597);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:49: curly_chunk_data
            	    {
            	    pushFollow(FOLLOW_curly_chunk_data_in_curly_chunk_data3611);
            	    curly_chunk_data274=curly_chunk_data();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, curly_chunk_data274.getTree());

            	    }
            	    break;

            	default :
            	    break loop90;
                }
            } while (true);

            RIGHT_CURLY275=(Token)input.LT(1);
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk_data3616); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_CURLY275_tree = (Object)adaptor.create(RIGHT_CURLY275);
            adaptor.addChild(root_0, RIGHT_CURLY275_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:775:1: paren_chunk : pc= paren_chunk_data -> VT_PAREN_CHUNK[$pc.start,text] ;
    public final paren_chunk_return paren_chunk() throws RecognitionException {
        paren_chunk_return retval = new paren_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        paren_chunk_data_return pc = null;


        RewriteRuleSubtreeStream stream_paren_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule paren_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:778:3: (pc= paren_chunk_data -> VT_PAREN_CHUNK[$pc.start,text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:778:5: pc= paren_chunk_data
            {
            pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk3632);
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
            // 779:2: -> VT_PAREN_CHUNK[$pc.start,text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:782:1: paren_chunk_data : LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )* RIGHT_PAREN ;
    public final paren_chunk_data_return paren_chunk_data() throws RecognitionException {
        paren_chunk_data_return retval = new paren_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN276=null;
        Token set277=null;
        Token RIGHT_PAREN279=null;
        paren_chunk_data_return paren_chunk_data278 = null;


        Object LEFT_PAREN276_tree=null;
        Object set277_tree=null;
        Object RIGHT_PAREN279_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:2: ( LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )* RIGHT_PAREN )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:4: LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )* RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            LEFT_PAREN276=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk_data3652); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_PAREN276_tree = (Object)adaptor.create(LEFT_PAREN276);
            adaptor.addChild(root_0, LEFT_PAREN276_tree);
            }
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:15: (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )*
            loop91:
            do {
                int alt91=3;
                int LA91_0 = input.LA(1);

                if ( ((LA91_0>=VT_COMPILATION_UNIT && LA91_0<=STRING)||LA91_0==COMMA||(LA91_0>=COLON && LA91_0<=MULTI_LINE_COMMENT)) ) {
                    alt91=1;
                }
                else if ( (LA91_0==LEFT_PAREN) ) {
                    alt91=2;
                }


                switch (alt91) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:16: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    set277=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=STRING)||input.LA(1)==COMMA||(input.LA(1)>=COLON && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set277));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk_data3655);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:49: paren_chunk_data
            	    {
            	    pushFollow(FOLLOW_paren_chunk_data_in_paren_chunk_data3669);
            	    paren_chunk_data278=paren_chunk_data();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, paren_chunk_data278.getTree());

            	    }
            	    break;

            	default :
            	    break loop91;
                }
            } while (true);

            RIGHT_PAREN279=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk_data3674); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_PAREN279_tree = (Object)adaptor.create(RIGHT_PAREN279);
            adaptor.addChild(root_0, RIGHT_PAREN279_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:786:1: square_chunk : sc= square_chunk_data -> VT_SQUARE_CHUNK[$sc.start,text] ;
    public final square_chunk_return square_chunk() throws RecognitionException {
        square_chunk_return retval = new square_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        square_chunk_data_return sc = null;


        RewriteRuleSubtreeStream stream_square_chunk_data=new RewriteRuleSubtreeStream(adaptor,"rule square_chunk_data");

        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:3: (sc= square_chunk_data -> VT_SQUARE_CHUNK[$sc.start,text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:5: sc= square_chunk_data
            {
            pushFollow(FOLLOW_square_chunk_data_in_square_chunk3691);
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
            // 790:2: -> VT_SQUARE_CHUNK[$sc.start,text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:793:1: square_chunk_data : LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )* RIGHT_SQUARE ;
    public final square_chunk_data_return square_chunk_data() throws RecognitionException {
        square_chunk_data_return retval = new square_chunk_data_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE280=null;
        Token set281=null;
        Token RIGHT_SQUARE283=null;
        square_chunk_data_return square_chunk_data282 = null;


        Object LEFT_SQUARE280_tree=null;
        Object set281_tree=null;
        Object RIGHT_SQUARE283_tree=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:794:2: ( LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )* RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:794:4: LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )* RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE280=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk_data3710); if (failed) return retval;
            if ( backtracking==0 ) {
            LEFT_SQUARE280_tree = (Object)adaptor.create(LEFT_SQUARE280);
            adaptor.addChild(root_0, LEFT_SQUARE280_tree);
            }
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:794:16: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )*
            loop92:
            do {
                int alt92=3;
                int LA92_0 = input.LA(1);

                if ( ((LA92_0>=VT_COMPILATION_UNIT && LA92_0<=NULL)||(LA92_0>=THEN && LA92_0<=MULTI_LINE_COMMENT)) ) {
                    alt92=1;
                }
                else if ( (LA92_0==LEFT_SQUARE) ) {
                    alt92=2;
                }


                switch (alt92) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:794:17: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    set281=(Token)input.LT(1);
            	    if ( (input.LA(1)>=VT_COMPILATION_UNIT && input.LA(1)<=NULL)||(input.LA(1)>=THEN && input.LA(1)<=MULTI_LINE_COMMENT) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set281));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk_data3713);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:794:52: square_chunk_data
            	    {
            	    pushFollow(FOLLOW_square_chunk_data_in_square_chunk_data3727);
            	    square_chunk_data282=square_chunk_data();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, square_chunk_data282.getTree());

            	    }
            	    break;

            	default :
            	    break loop92;
                }
            } while (true);

            RIGHT_SQUARE283=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk_data3732); if (failed) return retval;
            if ( backtracking==0 ) {
            RIGHT_SQUARE283_tree = (Object)adaptor.create(RIGHT_SQUARE283);
            adaptor.addChild(root_0, RIGHT_SQUARE283_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:1: date_effective_key : {...}? => ID MISC ID -> VK_DATE_EFFECTIVE[$start, text] ;
    public final date_effective_key_return date_effective_key() throws RecognitionException {
        date_effective_key_return retval = new date_effective_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID284=null;
        Token MISC285=null;
        Token ID286=null;

        Object ID284_tree=null;
        Object MISC285_tree=null;
        Object ID286_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:801:3: ({...}? => ID MISC ID -> VK_DATE_EFFECTIVE[$start, text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:801:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "date_effective_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))");
            }
            ID284=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_effective_key3751); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID284);

            MISC285=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_date_effective_key3753); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC285);

            ID286=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_effective_key3755); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID286);

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
            // 802:2: -> VK_DATE_EFFECTIVE[$start, text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:805:1: date_expires_key : {...}? => ID MISC ID -> VK_DATE_EXPIRES[$start, text] ;
    public final date_expires_key_return date_expires_key() throws RecognitionException {
        date_expires_key_return retval = new date_expires_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID287=null;
        Token MISC288=null;
        Token ID289=null;

        Object ID287_tree=null;
        Object MISC288_tree=null;
        Object ID289_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:808:3: ({...}? => ID MISC ID -> VK_DATE_EXPIRES[$start, text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:808:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "date_expires_key", "(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.EXPIRES))");
            }
            ID287=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_expires_key3781); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID287);

            MISC288=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_date_expires_key3783); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC288);

            ID289=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_date_expires_key3785); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID289);

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
            // 809:2: -> VK_DATE_EXPIRES[$start, text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:812:1: lock_on_active_key : {...}? => ID MISC ID MISC ID -> VK_LOCK_ON_ACTIVE[$start, text] ;
    public final lock_on_active_key_return lock_on_active_key() throws RecognitionException {
        lock_on_active_key_return retval = new lock_on_active_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID290=null;
        Token MISC291=null;
        Token ID292=null;
        Token MISC293=null;
        Token ID294=null;

        Object ID290_tree=null;
        Object MISC291_tree=null;
        Object ID292_tree=null;
        Object MISC293_tree=null;
        Object ID294_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:815:3: ({...}? => ID MISC ID MISC ID -> VK_LOCK_ON_ACTIVE[$start, text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:815:5: {...}? => ID MISC ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "lock_on_active_key", "(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, \"-\") && validateLT(5, DroolsSoftKeywords.ACTIVE))");
            }
            ID290=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key3811); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID290);

            MISC291=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_lock_on_active_key3813); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC291);

            ID292=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key3815); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID292);

            MISC293=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_lock_on_active_key3817); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC293);

            ID294=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_lock_on_active_key3819); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID294);

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
            // 816:2: -> VK_LOCK_ON_ACTIVE[$start, text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:819:1: no_loop_key : {...}? => ID MISC ID -> VK_NO_LOOP[$start, text] ;
    public final no_loop_key_return no_loop_key() throws RecognitionException {
        no_loop_key_return retval = new no_loop_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID295=null;
        Token MISC296=null;
        Token ID297=null;

        Object ID295_tree=null;
        Object MISC296_tree=null;
        Object ID297_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:3: ({...}? => ID MISC ID -> VK_NO_LOOP[$start, text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "no_loop_key", "(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.LOOP))");
            }
            ID295=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_no_loop_key3845); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID295);

            MISC296=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_no_loop_key3847); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC296);

            ID297=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_no_loop_key3849); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID297);

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
            // 823:2: -> VK_NO_LOOP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:826:1: auto_focus_key : {...}? => ID MISC ID -> VK_AUTO_FOCUS[$start, text] ;
    public final auto_focus_key_return auto_focus_key() throws RecognitionException {
        auto_focus_key_return retval = new auto_focus_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID298=null;
        Token MISC299=null;
        Token ID300=null;

        Object ID298_tree=null;
        Object MISC299_tree=null;
        Object ID300_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:829:3: ({...}? => ID MISC ID -> VK_AUTO_FOCUS[$start, text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:829:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "auto_focus_key", "(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.FOCUS))");
            }
            ID298=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_auto_focus_key3875); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID298);

            MISC299=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_auto_focus_key3877); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC299);

            ID300=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_auto_focus_key3879); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID300);

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
            // 830:2: -> VK_AUTO_FOCUS[$start, text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:833:1: activation_group_key : {...}? => ID MISC ID -> VK_ACTIVATION_GROUP[$start, text] ;
    public final activation_group_key_return activation_group_key() throws RecognitionException {
        activation_group_key_return retval = new activation_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID301=null;
        Token MISC302=null;
        Token ID303=null;

        Object ID301_tree=null;
        Object MISC302_tree=null;
        Object ID303_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:836:3: ({...}? => ID MISC ID -> VK_ACTIVATION_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:836:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "activation_group_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            ID301=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_activation_group_key3905); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID301);

            MISC302=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_activation_group_key3907); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC302);

            ID303=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_activation_group_key3909); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID303);

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
            // 837:2: -> VK_ACTIVATION_GROUP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:840:1: agenda_group_key : {...}? => ID MISC ID -> VK_AGENDA_GROUP[$start, text] ;
    public final agenda_group_key_return agenda_group_key() throws RecognitionException {
        agenda_group_key_return retval = new agenda_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID304=null;
        Token MISC305=null;
        Token ID306=null;

        Object ID304_tree=null;
        Object MISC305_tree=null;
        Object ID306_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:843:3: ({...}? => ID MISC ID -> VK_AGENDA_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:843:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "agenda_group_key", "(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            ID304=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_agenda_group_key3935); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID304);

            MISC305=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_agenda_group_key3937); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC305);

            ID306=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_agenda_group_key3939); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID306);

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
            // 844:2: -> VK_AGENDA_GROUP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:1: ruleflow_group_key : {...}? => ID MISC ID -> VK_RULEFLOW_GROUP[$start, text] ;
    public final ruleflow_group_key_return ruleflow_group_key() throws RecognitionException {
        ruleflow_group_key_return retval = new ruleflow_group_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID307=null;
        Token MISC308=null;
        Token ID309=null;

        Object ID307_tree=null;
        Object MISC308_tree=null;
        Object ID309_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:850:3: ({...}? => ID MISC ID -> VK_RULEFLOW_GROUP[$start, text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:850:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "ruleflow_group_key", "(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.GROUP))");
            }
            ID307=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_ruleflow_group_key3965); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID307);

            MISC308=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_ruleflow_group_key3967); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC308);

            ID309=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_ruleflow_group_key3969); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID309);

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
            // 851:2: -> VK_RULEFLOW_GROUP[$start, text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:854:1: duration_key : {...}? =>id= ID -> VK_DURATION[$id] ;
    public final duration_key_return duration_key() throws RecognitionException {
        duration_key_return retval = new duration_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:855:2: ({...}? =>id= ID -> VK_DURATION[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:855:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DURATION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "duration_key", "(validateIdentifierKey(DroolsSoftKeywords.DURATION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_duration_key3994); if (failed) return retval;
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
            // 855:69: -> VK_DURATION[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:858:1: package_key : {...}? =>id= ID -> VK_PACKAGE[$id] ;
    public final package_key_return package_key() throws RecognitionException {
        package_key_return retval = new package_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:859:2: ({...}? =>id= ID -> VK_PACKAGE[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:859:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.PACKAGE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "package_key", "(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_key4016); if (failed) return retval;
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
            // 859:68: -> VK_PACKAGE[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:862:1: import_key : {...}? =>id= ID -> VK_IMPORT[$id] ;
    public final import_key_return import_key() throws RecognitionException {
        import_key_return retval = new import_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:863:2: ({...}? =>id= ID -> VK_IMPORT[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:863:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "import_key", "(validateIdentifierKey(DroolsSoftKeywords.IMPORT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_key4038); if (failed) return retval;
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
            // 863:67: -> VK_IMPORT[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:866:1: dialect_key : {...}? =>id= ID -> VK_DIALECT[$id] ;
    public final dialect_key_return dialect_key() throws RecognitionException {
        dialect_key_return retval = new dialect_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:867:2: ({...}? =>id= ID -> VK_DIALECT[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:867:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DIALECT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "dialect_key", "(validateIdentifierKey(DroolsSoftKeywords.DIALECT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dialect_key4060); if (failed) return retval;
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
            // 867:68: -> VK_DIALECT[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:870:1: salience_key : {...}? =>id= ID -> VK_SALIENCE[$id] ;
    public final salience_key_return salience_key() throws RecognitionException {
        salience_key_return retval = new salience_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:2: ({...}? =>id= ID -> VK_SALIENCE[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:871:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "salience_key", "(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_salience_key4082); if (failed) return retval;
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
            // 871:69: -> VK_SALIENCE[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:1: enabled_key : {...}? =>id= ID -> VK_ENABLED[$id] ;
    public final enabled_key_return enabled_key() throws RecognitionException {
        enabled_key_return retval = new enabled_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:875:2: ({...}? =>id= ID -> VK_ENABLED[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:875:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ENABLED))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "enabled_key", "(validateIdentifierKey(DroolsSoftKeywords.ENABLED))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enabled_key4104); if (failed) return retval;
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
            // 875:68: -> VK_ENABLED[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:1: attributes_key : {...}? =>id= ID -> VK_ATTRIBUTES[$id] ;
    public final attributes_key_return attributes_key() throws RecognitionException {
        attributes_key_return retval = new attributes_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:879:2: ({...}? =>id= ID -> VK_ATTRIBUTES[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:879:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "attributes_key", "(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_attributes_key4126); if (failed) return retval;
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
            // 879:71: -> VK_ATTRIBUTES[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:882:1: when_key : {...}? =>id= ID -> VK_WHEN[$id] ;
    public final when_key_return when_key() throws RecognitionException {
        when_key_return retval = new when_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:883:2: ({...}? =>id= ID -> VK_WHEN[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:883:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "when_key", "(validateIdentifierKey(DroolsSoftKeywords.WHEN))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_when_key4148); if (failed) return retval;
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
            // 883:65: -> VK_WHEN[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:886:1: rule_key : {...}? =>id= ID -> VK_RULE[$id] ;
    public final rule_key_return rule_key() throws RecognitionException {
        rule_key_return retval = new rule_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:887:2: ({...}? =>id= ID -> VK_RULE[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:887:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "rule_key", "(validateIdentifierKey(DroolsSoftKeywords.RULE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_rule_key4170); if (failed) return retval;
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
            // 887:65: -> VK_RULE[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:890:1: template_key : {...}? =>id= ID -> VK_TEMPLATE[$id] ;
    public final template_key_return template_key() throws RecognitionException {
        template_key_return retval = new template_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:891:2: ({...}? =>id= ID -> VK_TEMPLATE[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:891:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "template_key", "(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template_key4192); if (failed) return retval;
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
            // 891:69: -> VK_TEMPLATE[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:894:1: query_key : {...}? =>id= ID -> VK_QUERY[$id] ;
    public final query_key_return query_key() throws RecognitionException {
        query_key_return retval = new query_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:895:2: ({...}? =>id= ID -> VK_QUERY[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:895:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "query_key", "(validateIdentifierKey(DroolsSoftKeywords.QUERY))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_query_key4214); if (failed) return retval;
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
            // 895:66: -> VK_QUERY[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:898:1: declare_key : {...}? =>id= ID -> VK_DECLARE[$id] ;
    public final declare_key_return declare_key() throws RecognitionException {
        declare_key_return retval = new declare_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:899:2: ({...}? =>id= ID -> VK_DECLARE[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:899:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.DECLARE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "declare_key", "(validateIdentifierKey(DroolsSoftKeywords.DECLARE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_declare_key4236); if (failed) return retval;
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
            // 899:68: -> VK_DECLARE[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:902:1: function_key : {...}? =>id= ID -> VK_FUNCTION[$id] ;
    public final function_key_return function_key() throws RecognitionException {
        function_key_return retval = new function_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:903:2: ({...}? =>id= ID -> VK_FUNCTION[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:903:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "function_key", "(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function_key4258); if (failed) return retval;
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
            // 903:69: -> VK_FUNCTION[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:906:1: global_key : {...}? =>id= ID -> VK_GLOBAL[$id] ;
    public final global_key_return global_key() throws RecognitionException {
        global_key_return retval = new global_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:907:2: ({...}? =>id= ID -> VK_GLOBAL[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:907:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "global_key", "(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global_key4280); if (failed) return retval;
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
            // 907:67: -> VK_GLOBAL[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:910:1: eval_key : {...}? =>id= ID -> VK_EVAL[$id] ;
    public final eval_key_return eval_key() throws RecognitionException {
        eval_key_return retval = new eval_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:911:2: ({...}? =>id= ID -> VK_EVAL[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:911:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EVAL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "eval_key", "(validateIdentifierKey(DroolsSoftKeywords.EVAL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_eval_key4302); if (failed) return retval;
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
            // 911:65: -> VK_EVAL[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:914:1: contains_key : {...}? =>id= ID -> VK_CONTAINS[$id] ;
    public final contains_key_return contains_key() throws RecognitionException {
        contains_key_return retval = new contains_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:915:2: ({...}? =>id= ID -> VK_CONTAINS[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:915:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.CONTAINS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "contains_key", "(validateIdentifierKey(DroolsSoftKeywords.CONTAINS))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_contains_key4324); if (failed) return retval;
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
            // 915:69: -> VK_CONTAINS[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:918:1: matches_key : {...}? =>id= ID -> VK_MATCHES[$id] ;
    public final matches_key_return matches_key() throws RecognitionException {
        matches_key_return retval = new matches_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:919:2: ({...}? =>id= ID -> VK_MATCHES[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:919:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.MATCHES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "matches_key", "(validateIdentifierKey(DroolsSoftKeywords.MATCHES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_matches_key4346); if (failed) return retval;
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
            // 919:68: -> VK_MATCHES[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:922:1: excludes_key : {...}? =>id= ID -> VK_EXCLUDES[$id] ;
    public final excludes_key_return excludes_key() throws RecognitionException {
        excludes_key_return retval = new excludes_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:923:2: ({...}? =>id= ID -> VK_EXCLUDES[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:923:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "excludes_key", "(validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_excludes_key4368); if (failed) return retval;
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
            // 923:69: -> VK_EXCLUDES[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:926:1: soundslike_key : {...}? =>id= ID -> VK_SOUNDSLIKE[$id] ;
    public final soundslike_key_return soundslike_key() throws RecognitionException {
        soundslike_key_return retval = new soundslike_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:927:2: ({...}? =>id= ID -> VK_SOUNDSLIKE[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:927:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "soundslike_key", "(validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_soundslike_key4390); if (failed) return retval;
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
            // 927:71: -> VK_SOUNDSLIKE[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:930:1: memberof_key : {...}? =>id= ID -> VK_MEMBEROF[$id] ;
    public final memberof_key_return memberof_key() throws RecognitionException {
        memberof_key_return retval = new memberof_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:931:2: ({...}? =>id= ID -> VK_MEMBEROF[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:931:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "memberof_key", "(validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_memberof_key4412); if (failed) return retval;
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
            // 931:69: -> VK_MEMBEROF[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:934:1: not_key : {...}? =>id= ID -> VK_NOT[$id] ;
    public final not_key_return not_key() throws RecognitionException {
        not_key_return retval = new not_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:935:2: ({...}? =>id= ID -> VK_NOT[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:935:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.NOT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "not_key", "(validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_not_key4434); if (failed) return retval;
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
            // 935:64: -> VK_NOT[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:938:1: in_key : {...}? =>id= ID -> VK_IN[$id] ;
    public final in_key_return in_key() throws RecognitionException {
        in_key_return retval = new in_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:939:2: ({...}? =>id= ID -> VK_IN[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:939:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.IN))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "in_key", "(validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_in_key4456); if (failed) return retval;
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
            // 939:63: -> VK_IN[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:942:1: or_key : {...}? =>id= ID -> VK_OR[$id] ;
    public final or_key_return or_key() throws RecognitionException {
        or_key_return retval = new or_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:943:2: ({...}? =>id= ID -> VK_OR[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:943:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.OR))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "or_key", "(validateIdentifierKey(DroolsSoftKeywords.OR))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_or_key4478); if (failed) return retval;
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
            // 943:63: -> VK_OR[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:946:1: and_key : {...}? =>id= ID -> VK_AND[$id] ;
    public final and_key_return and_key() throws RecognitionException {
        and_key_return retval = new and_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:947:2: ({...}? =>id= ID -> VK_AND[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:947:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.AND))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "and_key", "(validateIdentifierKey(DroolsSoftKeywords.AND))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_and_key4500); if (failed) return retval;
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
            // 947:64: -> VK_AND[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:950:1: exists_key : {...}? =>id= ID -> VK_EXISTS[$id] ;
    public final exists_key_return exists_key() throws RecognitionException {
        exists_key_return retval = new exists_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:951:2: ({...}? =>id= ID -> VK_EXISTS[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:951:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.EXISTS))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "exists_key", "(validateIdentifierKey(DroolsSoftKeywords.EXISTS))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_exists_key4522); if (failed) return retval;
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
            // 951:67: -> VK_EXISTS[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:954:1: forall_key : {...}? =>id= ID -> VK_FORALL[$id] ;
    public final forall_key_return forall_key() throws RecognitionException {
        forall_key_return retval = new forall_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:955:2: ({...}? =>id= ID -> VK_FORALL[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:955:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.FORALL))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "forall_key", "(validateIdentifierKey(DroolsSoftKeywords.FORALL))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_forall_key4544); if (failed) return retval;
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
            // 955:67: -> VK_FORALL[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:958:1: from_key : {...}? =>id= ID -> VK_FROM[$id] ;
    public final from_key_return from_key() throws RecognitionException {
        from_key_return retval = new from_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:959:2: ({...}? =>id= ID -> VK_FROM[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:959:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.FROM))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "from_key", "(validateIdentifierKey(DroolsSoftKeywords.FROM))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_from_key4566); if (failed) return retval;
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
            // 959:65: -> VK_FROM[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:962:1: entry_point_key : {...}? => ID MISC ID -> VK_ENTRY_POINT[$start, text] ;
    public final entry_point_key_return entry_point_key() throws RecognitionException {
        entry_point_key_return retval = new entry_point_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID310=null;
        Token MISC311=null;
        Token ID312=null;

        Object ID310_tree=null;
        Object MISC311_tree=null;
        Object ID312_tree=null;
        RewriteRuleTokenStream stream_MISC=new RewriteRuleTokenStream(adaptor,"token MISC");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");


        	String text = "";

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:965:3: ({...}? => ID MISC ID -> VK_ENTRY_POINT[$start, text] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:965:5: {...}? => ID MISC ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "entry_point_key", "(validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, \"-\") && validateLT(3, DroolsSoftKeywords.POINT))");
            }
            ID310=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_entry_point_key4589); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID310);

            MISC311=(Token)input.LT(1);
            match(input,MISC,FOLLOW_MISC_in_entry_point_key4591); if (failed) return retval;
            if ( backtracking==0 ) stream_MISC.add(MISC311);

            ID312=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_entry_point_key4593); if (failed) return retval;
            if ( backtracking==0 ) stream_ID.add(ID312);

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
            // 966:2: -> VK_ENTRY_POINT[$start, text]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:1: accumulate_key : {...}? =>id= ID -> VK_ACCUMULATE[$id] ;
    public final accumulate_key_return accumulate_key() throws RecognitionException {
        accumulate_key_return retval = new accumulate_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:970:2: ({...}? =>id= ID -> VK_ACCUMULATE[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:970:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "accumulate_key", "(validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accumulate_key4618); if (failed) return retval;
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
            // 970:71: -> VK_ACCUMULATE[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:973:1: init_key : {...}? =>id= ID -> VK_INIT[$id] ;
    public final init_key_return init_key() throws RecognitionException {
        init_key_return retval = new init_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:974:2: ({...}? =>id= ID -> VK_INIT[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:974:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.INIT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "init_key", "(validateIdentifierKey(DroolsSoftKeywords.INIT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_init_key4640); if (failed) return retval;
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
            // 974:65: -> VK_INIT[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:977:1: action_key : {...}? =>id= ID -> VK_ACTION[$id] ;
    public final action_key_return action_key() throws RecognitionException {
        action_key_return retval = new action_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:2: ({...}? =>id= ID -> VK_ACTION[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.ACTION))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "action_key", "(validateIdentifierKey(DroolsSoftKeywords.ACTION))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_action_key4662); if (failed) return retval;
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
            // 978:67: -> VK_ACTION[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:981:1: reverse_key : {...}? =>id= ID -> VK_REVERSE[$id] ;
    public final reverse_key_return reverse_key() throws RecognitionException {
        reverse_key_return retval = new reverse_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:982:2: ({...}? =>id= ID -> VK_REVERSE[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:982:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.REVERSE))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "reverse_key", "(validateIdentifierKey(DroolsSoftKeywords.REVERSE))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_reverse_key4684); if (failed) return retval;
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
            // 982:68: -> VK_REVERSE[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:985:1: result_key : {...}? =>id= ID -> VK_RESULT[$id] ;
    public final result_key_return result_key() throws RecognitionException {
        result_key_return retval = new result_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:986:2: ({...}? =>id= ID -> VK_RESULT[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:986:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.RESULT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "result_key", "(validateIdentifierKey(DroolsSoftKeywords.RESULT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_result_key4706); if (failed) return retval;
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
            // 986:67: -> VK_RESULT[$id]
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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:989:1: collect_key : {...}? =>id= ID -> VK_COLLECT[$id] ;
    public final collect_key_return collect_key() throws RecognitionException {
        collect_key_return retval = new collect_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:990:2: ({...}? =>id= ID -> VK_COLLECT[$id] )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:990:4: {...}? =>id= ID
            {
            if ( !((validateIdentifierKey(DroolsSoftKeywords.COLLECT))) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "collect_key", "(validateIdentifierKey(DroolsSoftKeywords.COLLECT))");
            }
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_collect_key4728); if (failed) return retval;
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
            // 990:68: -> VK_COLLECT[$id]
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
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:5: ( LEFT_PAREN or_key )
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:6: LEFT_PAREN or_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred11638); if (failed) return ;
        pushFollow(FOLLOW_or_key_in_synpred11640);
        or_key();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:5: ( or_key | DOUBLE_PIPE )
        int alt93=2;
        int LA93_0 = input.LA(1);

        if ( (LA93_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.OR)))) {
            alt93=1;
        }
        else if ( (LA93_0==DOUBLE_PIPE) ) {
            alt93=2;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("485:5: synpred2 : ( or_key | DOUBLE_PIPE );", 93, 0, input);

            throw nvae;
        }
        switch (alt93) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:6: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred21687);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:13: DOUBLE_PIPE
                {
                match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred21689); if (failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred2

    // $ANTLR start synpred3
    public final void synpred3_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:5: ( LEFT_PAREN and_key )
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:6: LEFT_PAREN and_key
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred31740); if (failed) return ;
        pushFollow(FOLLOW_and_key_in_synpred31742);
        and_key();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred3

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:5: ( and_key | DOUBLE_AMPER )
        int alt94=2;
        int LA94_0 = input.LA(1);

        if ( (LA94_0==ID) && ((validateIdentifierKey(DroolsSoftKeywords.AND)))) {
            alt94=1;
        }
        else if ( (LA94_0==DOUBLE_AMPER) ) {
            alt94=2;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("495:5: synpred4 : ( and_key | DOUBLE_AMPER );", 94, 0, input);

            throw nvae;
        }
        switch (alt94) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:6: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred41789);
                and_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:14: DOUBLE_AMPER
                {
                match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred41791); if (failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:508:4: ( SEMICOLON )
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:508:5: SEMICOLON
        {
        match(input,SEMICOLON,FOLLOW_SEMICOLON_in_synpred51893); if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:12: ( LEFT_PAREN ( or_key | and_key ) )
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:13: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred61925); if (failed) return ;
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:24: ( or_key | and_key )
        int alt95=2;
        int LA95_0 = input.LA(1);

        if ( (LA95_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            int LA95_1 = input.LA(2);

            if ( ((validateIdentifierKey(DroolsSoftKeywords.OR))) ) {
                alt95=1;
            }
            else if ( ((validateIdentifierKey(DroolsSoftKeywords.AND))) ) {
                alt95=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("513:24: ( or_key | and_key )", 95, 1, input);

                throw nvae;
            }
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("513:24: ( or_key | and_key )", 95, 0, input);

            throw nvae;
        }
        switch (alt95) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:25: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred61928);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:32: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred61930);
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
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:5: ( LEFT_PAREN ( or_key | and_key ) )
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:6: LEFT_PAREN ( or_key | and_key )
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred72012); if (failed) return ;
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:17: ( or_key | and_key )
        int alt96=2;
        int LA96_0 = input.LA(1);

        if ( (LA96_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.AND))||(validateIdentifierKey(DroolsSoftKeywords.OR))))) {
            int LA96_1 = input.LA(2);

            if ( ((validateIdentifierKey(DroolsSoftKeywords.OR))) ) {
                alt96=1;
            }
            else if ( ((validateIdentifierKey(DroolsSoftKeywords.AND))) ) {
                alt96=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("521:17: ( or_key | and_key )", 96, 1, input);

                throw nvae;
            }
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("521:17: ( or_key | and_key )", 96, 0, input);

            throw nvae;
        }
        switch (alt96) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:18: or_key
                {
                pushFollow(FOLLOW_or_key_in_synpred72015);
                or_key();
                _fsp--;
                if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:25: and_key
                {
                pushFollow(FOLLOW_and_key_in_synpred72017);
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
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:5: ( LEFT_PAREN )
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:6: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred82513); if (failed) return ;

        }
    }
    // $ANTLR end synpred8

    // $ANTLR start synpred9
    public final void synpred9_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:603:6: ( LEFT_SQUARE )
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:603:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred92574); if (failed) return ;

        }
    }
    // $ANTLR end synpred9

    // $ANTLR start synpred10
    public final void synpred10_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:605:6: ( LEFT_PAREN )
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:605:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred102596); if (failed) return ;

        }
    }
    // $ANTLR end synpred10

    // $ANTLR start synpred11
    public final void synpred11_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:675:26: ( DOUBLE_PIPE )
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:675:27: DOUBLE_PIPE
        {
        match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred113060); if (failed) return ;

        }
    }
    // $ANTLR end synpred11

    // $ANTLR start synpred12
    public final void synpred12_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:679:27: ( DOUBLE_AMPER )
        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DRL.g:679:28: DOUBLE_AMPER
        {
        match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred123086); if (failed) return ;

        }
    }
    // $ANTLR end synpred12

    public final boolean synpred12() {
        backtracking++;
        int start = input.mark();
        try {
            synpred12_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
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
    public final boolean synpred11() {
        backtracking++;
        int start = input.mark();
        try {
            synpred11_fragment(); // can never throw exception
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


    protected DFA5 dfa5 = new DFA5(this);
    protected DFA12 dfa12 = new DFA12(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA24 dfa24 = new DFA24(this);
    protected DFA30 dfa30 = new DFA30(this);
    protected DFA47 dfa47 = new DFA47(this);
    protected DFA48 dfa48 = new DFA48(this);
    protected DFA53 dfa53 = new DFA53(this);
    protected DFA57 dfa57 = new DFA57(this);
    protected DFA74 dfa74 = new DFA74(this);
    protected DFA75 dfa75 = new DFA75(this);
    static final String DFA5_eotS =
        "\u0085\uffff";
    static final String DFA5_eofS =
        "\3\uffff\1\17\1\22\11\uffff\1\17\10\uffff\1\17\5\uffff\1\17\2\uffff"+
        "\1\17\5\uffff\1\22\1\uffff\1\22\5\uffff\1\17\5\uffff\2\107\1\uffff"+
        "\1\22\5\uffff\2\17\2\uffff\1\17\2\uffff\1\17\4\uffff\2\22\3\uffff"+
        "\1\22\1\uffff\1\22\5\uffff\2\17\1\uffff\1\17\2\22\5\uffff\1\17\1"+
        "\uffff\2\17\4\uffff\1\22\1\uffff\2\22\3\uffff\1\17\4\uffff\1\22"+
        "\1\uffff\1\17\5\uffff\1\22\7\uffff";
    static final String DFA5_minS =
        "\2\126\1\uffff\2\125\3\uffff\2\126\2\uffff\1\126\1\156\1\126\1\uffff"+
        "\1\126\2\uffff\5\126\2\0\1\126\1\156\1\0\1\126\2\uffff\1\125\3\126"+
        "\2\0\1\126\1\0\1\126\1\156\1\126\1\0\1\126\1\uffff\1\126\2\0\3\126"+
        "\2\125\1\0\11\126\1\0\1\126\2\0\1\126\1\0\1\4\1\uffff\4\126\2\0"+
        "\1\126\1\0\1\126\1\134\1\126\1\156\1\134\1\156\2\126\1\4\3\126\1"+
        "\156\2\126\1\156\5\126\4\4\4\126\1\134\1\156\1\134\1\126\4\4\3\126"+
        "\5\4\1\126\7\4";
    static final String DFA5_maxS =
        "\1\126\1\162\1\uffff\2\157\3\uffff\1\135\1\162\2\uffff\1\126\1\156"+
        "\1\126\1\uffff\1\162\2\uffff\1\155\1\160\1\126\2\157\2\0\1\126\1"+
        "\156\1\0\1\157\2\uffff\2\155\1\162\1\126\2\0\1\157\1\0\1\157\1\156"+
        "\1\126\1\0\1\126\1\uffff\1\162\2\0\1\162\2\155\1\162\1\133\1\0\2"+
        "\162\3\155\1\126\2\157\1\126\1\0\1\157\2\0\1\157\1\0\1\173\1\uffff"+
        "\1\126\2\157\1\126\2\0\1\157\1\0\1\157\1\155\1\126\1\156\1\155\1"+
        "\156\1\157\1\162\1\173\1\160\1\157\1\162\1\156\2\155\1\156\1\155"+
        "\1\157\1\126\2\157\4\173\1\157\1\126\2\157\1\155\1\156\1\155\1\157"+
        "\4\173\1\157\1\155\1\157\5\173\1\157\7\173";
    static final String DFA5_acceptS =
        "\2\uffff\1\1\2\uffff\3\1\2\uffff\1\10\1\7\3\uffff\1\3\1\uffff\1"+
        "\6\1\1\13\uffff\1\2\1\4\15\uffff\1\5\31\uffff\1\4\75\uffff";
    static final String DFA5_specialS =
        "\1\14\1\62\1\uffff\1\130\1\103\3\uffff\1\34\1\127\2\uffff\1\163"+
        "\1\154\1\5\1\uffff\1\107\2\uffff\1\122\1\104\1\24\1\121\1\46\1\111"+
        "\1\13\1\37\1\33\1\162\1\74\2\uffff\1\1\1\141\1\132\1\76\1\40\1\112"+
        "\1\144\1\102\1\32\1\136\1\115\1\26\1\10\1\uffff\1\2\1\52\1\145\1"+
        "\156\1\43\1\41\1\65\1\42\1\150\1\143\1\56\1\161\1\60\1\16\1\55\1"+
        "\75\1\7\1\25\1\151\1\11\1\146\1\64\1\110\1\157\1\167\1\uffff\1\153"+
        "\1\22\1\47\1\77\1\17\1\44\1\6\1\133\1\106\1\15\1\66\1\61\1\147\1"+
        "\67\1\114\1\12\1\53\1\72\1\120\1\142\1\30\1\117\1\63\1\31\1\23\1"+
        "\131\1\54\1\0\1\51\1\101\1\36\1\71\1\4\1\134\1\152\1\113\1\57\1"+
        "\45\1\27\1\35\1\50\1\135\1\73\1\140\1\126\1\165\1\164\1\160\1\70"+
        "\1\100\1\166\1\125\1\116\1\105\1\137\1\155\1\124\1\21\1\123\1\3"+
        "\1\20}>";
    static final String[] DFA5_transitionS = {
            "\1\1",
            "\1\3\3\uffff\1\4\1\7\3\uffff\1\6\1\5\21\uffff\1\2",
            "",
            "\1\16\1\11\1\14\1\17\1\12\1\uffff\1\10\21\uffff\1\15\1\uffff"+
            "\1\13",
            "\1\21\1\20\2\uffff\1\12\1\uffff\1\12\23\uffff\1\13",
            "",
            "",
            "",
            "\1\23\4\uffff\1\12\1\uffff\1\24",
            "\1\31\1\32\2\uffff\1\34\1\30\2\uffff\1\26\1\35\1\27\14\uffff"+
            "\1\33\1\uffff\1\13\2\uffff\1\25",
            "",
            "",
            "\1\40",
            "\1\41",
            "\1\42",
            "",
            "\1\44\1\32\2\uffff\1\45\1\47\2\uffff\1\26\1\50\1\46\14\uffff"+
            "\1\33\1\uffff\1\13\2\uffff\1\43",
            "",
            "",
            "\1\53\1\54\3\uffff\1\12\1\52\1\24\1\12\16\uffff\1\51",
            "\1\12\2\uffff\1\12\1\uffff\1\12\24\uffff\1\55",
            "\1\56",
            "\1\57\4\uffff\1\60\23\uffff\1\13",
            "\1\61\5\uffff\1\13\22\uffff\1\13",
            "\1\uffff",
            "\1\uffff",
            "\1\62",
            "\1\63",
            "\1\uffff",
            "\1\61\5\uffff\1\13\22\uffff\1\13",
            "",
            "",
            "\1\17\1\64\1\14\1\17\24\uffff\1\15",
            "\1\65\26\uffff\1\15",
            "\1\66\1\21\2\uffff\2\17\3\uffff\2\17\14\uffff\1\21\4\uffff\1"+
            "\17",
            "\1\67",
            "\1\uffff",
            "\1\uffff",
            "\1\70\5\uffff\1\13\22\uffff\1\13",
            "\1\uffff",
            "\1\70\5\uffff\1\13\22\uffff\1\13",
            "\1\71",
            "\1\72",
            "\1\uffff",
            "\1\73",
            "",
            "\1\61\3\uffff\1\76\1\uffff\1\13\2\uffff\1\75\17\uffff\1\13\2"+
            "\uffff\1\74",
            "\1\uffff",
            "\1\uffff",
            "\1\100\3\uffff\1\103\1\102\2\uffff\1\13\1\104\1\101\16\uffff"+
            "\1\13\2\uffff\1\77",
            "\1\21\1\32\3\uffff\1\12\21\uffff\1\33",
            "\1\21\4\uffff\1\12\21\uffff\1\33",
            "\1\107\1\105\3\uffff\1\17\1\106\3\uffff\2\17\21\uffff\1\17",
            "\2\107\4\uffff\1\55",
            "\1\uffff",
            "\1\70\3\uffff\1\111\1\uffff\1\13\2\uffff\1\112\17\uffff\1\13"+
            "\2\uffff\1\110",
            "\1\114\3\uffff\1\115\1\117\2\uffff\1\13\1\120\1\116\16\uffff"+
            "\1\13\2\uffff\1\113",
            "\1\121\4\uffff\1\12\1\52\1\24\17\uffff\1\51",
            "\1\124\1\122\4\uffff\1\52\1\24\17\uffff\1\123",
            "\1\121\1\54\3\uffff\1\12\21\uffff\1\125",
            "\1\126",
            "\1\61\5\uffff\1\13\22\uffff\1\13",
            "\1\61\5\uffff\1\13\22\uffff\1\13",
            "\1\127",
            "\1\uffff",
            "\1\61\5\uffff\1\13\22\uffff\1\13",
            "\1\uffff",
            "\1\uffff",
            "\1\61\5\uffff\1\13\22\uffff\1\13",
            "\1\uffff",
            "\122\17\1\130\6\17\1\131\36\17",
            "",
            "\1\132",
            "\1\70\5\uffff\1\13\22\uffff\1\13",
            "\1\70\5\uffff\1\13\22\uffff\1\13",
            "\1\133",
            "\1\uffff",
            "\1\uffff",
            "\1\70\5\uffff\1\13\22\uffff\1\13",
            "\1\uffff",
            "\1\70\5\uffff\1\13\22\uffff\1\13",
            "\1\52\1\24\17\uffff\1\134",
            "\1\135",
            "\1\136",
            "\1\52\1\24\17\uffff\1\137",
            "\1\140",
            "\1\61\5\uffff\1\13\2\uffff\1\141\17\uffff\1\13",
            "\1\61\3\uffff\1\144\1\uffff\1\13\2\uffff\1\143\17\uffff\1\13"+
            "\2\uffff\1\142",
            "\122\17\1\150\1\147\4\17\1\146\1\131\17\17\1\145\16\17",
            "\1\17\31\uffff\1\55",
            "\1\70\5\uffff\1\13\2\uffff\1\151\17\uffff\1\13",
            "\1\70\3\uffff\1\153\1\uffff\1\13\2\uffff\1\154\17\uffff\1\13"+
            "\2\uffff\1\152",
            "\1\155",
            "\1\124\1\122\25\uffff\1\156",
            "\1\124\5\uffff\1\52\1\24\17\uffff\1\123",
            "\1\157",
            "\1\121\4\uffff\1\12\21\uffff\1\125",
            "\1\61\5\uffff\1\13\22\uffff\1\13",
            "\1\160",
            "\1\61\5\uffff\1\13\22\uffff\1\13",
            "\1\61\5\uffff\1\13\22\uffff\1\13",
            "\152\17\1\161\15\17",
            "\122\17\1\162\45\17",
            "\122\17\1\163\45\17",
            "\130\17\1\146\1\131\17\17\1\164\16\17",
            "\1\70\5\uffff\1\13\22\uffff\1\13",
            "\1\165",
            "\1\70\5\uffff\1\13\22\uffff\1\13",
            "\1\70\5\uffff\1\13\22\uffff\1\13",
            "\1\52\1\24\17\uffff\1\134",
            "\1\166",
            "\1\52\1\24\17\uffff\1\137",
            "\1\61\5\uffff\1\13\2\uffff\1\167\17\uffff\1\13",
            "\122\17\1\150\5\17\1\146\1\131\17\17\1\145\16\17",
            "\122\17\1\172\1\170\4\17\1\146\1\131\17\17\1\171\16\17",
            "\122\17\1\150\1\147\25\17\1\173\16\17",
            "\152\17\1\174\15\17",
            "\1\70\5\uffff\1\13\2\uffff\1\175\17\uffff\1\13",
            "\1\124\26\uffff\1\156",
            "\1\61\5\uffff\1\13\22\uffff\1\13",
            "\122\17\1\176\45\17",
            "\152\17\1\177\15\17",
            "\130\17\1\146\1\131\17\17\1\u0080\16\17",
            "\152\17\1\u0081\15\17",
            "\130\17\1\146\1\131\17\17\1\164\16\17",
            "\1\70\5\uffff\1\13\22\uffff\1\13",
            "\122\17\1\172\1\170\25\17\1\u0082\16\17",
            "\122\17\1\172\5\17\1\146\1\131\17\17\1\171\16\17",
            "\152\17\1\u0083\15\17",
            "\122\17\1\150\26\17\1\173\16\17",
            "\152\17\1\u0084\15\17",
            "\130\17\1\146\1\131\17\17\1\u0080\16\17",
            "\122\17\1\172\26\17\1\u0082\16\17"
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
            return "267:1: statement : ( rule_attribute | {...}? => function_import_statement | import_statement | global | function | template | rule | query );";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_99 = input.LA(1);

                         
                        int index5_99 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_99==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_99==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_99==COMMA||LA5_99==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_99);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA5_32 = input.LA(1);

                         
                        int index5_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_32==EOF||LA5_32==SEMICOLON||LA5_32==DOT_STAR) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_32==ID) && (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 52;}

                        else if ( (LA5_32==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 12;}

                        else if ( (LA5_32==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 13;}

                         
                        input.seek(index5_32);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA5_46 = input.LA(1);

                         
                        int index5_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_46==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 60;}

                        else if ( (LA5_46==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 61;}

                        else if ( (LA5_46==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_46==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_46==COMMA||LA5_46==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_46==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 62;}

                         
                        input.seek(index5_46);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA5_131 = input.LA(1);

                         
                        int index5_131 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_131==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 89;}

                        else if ( (LA5_131==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 102;}

                        else if ( ((LA5_131>=VT_COMPILATION_UNIT && LA5_131<=LEFT_PAREN)||(LA5_131>=COLON && LA5_131<=NULL)||(LA5_131>=RIGHT_SQUARE && LA5_131<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_131==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 128;}

                         
                        input.seek(index5_131);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA5_104 = input.LA(1);

                         
                        int index5_104 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_104==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 116;}

                        else if ( (LA5_104==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 102;}

                        else if ( (LA5_104==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 89;}

                        else if ( ((LA5_104>=VT_COMPILATION_UNIT && LA5_104<=LEFT_PAREN)||(LA5_104>=COLON && LA5_104<=NULL)||(LA5_104>=RIGHT_SQUARE && LA5_104<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_104);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA5_14 = input.LA(1);

                         
                        int index5_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_14==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 34;}

                        else if ( (LA5_14==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_14);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA5_78 = input.LA(1);

                         
                        int index5_78 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_78==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_78==COMMA||LA5_78==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_78==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                         
                        input.seek(index5_78);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA5_62 = input.LA(1);

                         
                        int index5_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_62==COMMA||LA5_62==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_62==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_62==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_62);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA5_44 = input.LA(1);

                         
                        int index5_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_44==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 59;}

                         
                        input.seek(index5_44);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA5_65 = input.LA(1);

                         
                        int index5_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_65==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_65==COMMA||LA5_65==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_65==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_65);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA5_87 = input.LA(1);

                         
                        int index5_87 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_87==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 98;}

                        else if ( (LA5_87==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 99;}

                        else if ( (LA5_87==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_87==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_87==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 100;}

                        else if ( (LA5_87==COMMA||LA5_87==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_87);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA5_25 = input.LA(1);

                         
                        int index5_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {s = 15;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))) ) {s = 17;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {s = 10;}

                         
                        input.seek(index5_25);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA5_0 = input.LA(1);

                         
                        int index5_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))) {s = 1;}

                         
                        input.seek(index5_0);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA5_81 = input.LA(1);

                         
                        int index5_81 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_81==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 92;}

                        else if ( (LA5_81==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 42;}

                        else if ( (LA5_81==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 20;}

                         
                        input.seek(index5_81);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA5_59 = input.LA(1);

                         
                        int index5_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_59==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 85;}

                        else if ( (LA5_59==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 81;}

                        else if ( (LA5_59==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 44;}

                        else if ( (LA5_59==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) {s = 10;}

                         
                        input.seek(index5_59);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA5_76 = input.LA(1);

                         
                        int index5_76 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.DIALECT))) ) {s = 18;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                         
                        input.seek(index5_76);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA5_132 = input.LA(1);

                         
                        int index5_132 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_132==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 122;}

                        else if ( (LA5_132==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 130;}

                        else if ( ((LA5_132>=VT_COMPILATION_UNIT && LA5_132<=SEMICOLON)||(LA5_132>=DOT && LA5_132<=NULL)||(LA5_132>=RIGHT_SQUARE && LA5_132<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_132);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA5_129 = input.LA(1);

                         
                        int index5_129 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA5_129>=VT_COMPILATION_UNIT && LA5_129<=SEMICOLON)||(LA5_129>=DOT && LA5_129<=NULL)||(LA5_129>=RIGHT_SQUARE && LA5_129<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_129==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 104;}

                        else if ( (LA5_129==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 123;}

                         
                        input.seek(index5_129);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA5_73 = input.LA(1);

                         
                        int index5_73 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_73==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                        else if ( (LA5_73==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_73==COMMA||LA5_73==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_73);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA5_96 = input.LA(1);

                         
                        int index5_96 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_96==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) {s = 10;}

                        else if ( (LA5_96==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 85;}

                        else if ( (LA5_96==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 81;}

                         
                        input.seek(index5_96);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA5_21 = input.LA(1);

                         
                        int index5_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_21==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 46;}

                         
                        input.seek(index5_21);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA5_63 = input.LA(1);

                         
                        int index5_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_63==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 87;}

                         
                        input.seek(index5_63);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA5_43 = input.LA(1);

                         
                        int index5_43 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))) ) {s = 45;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {s = 10;}

                         
                        input.seek(index5_43);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA5_110 = input.LA(1);

                         
                        int index5_110 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_110==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 118;}

                         
                        input.seek(index5_110);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA5_92 = input.LA(1);

                         
                        int index5_92 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_92==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 109;}

                         
                        input.seek(index5_92);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA5_95 = input.LA(1);

                         
                        int index5_95 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_95==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 111;}

                         
                        input.seek(index5_95);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA5_40 = input.LA(1);

                         
                        int index5_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_40==COMMA||LA5_40==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_40==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_40==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                         
                        input.seek(index5_40);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA5_27 = input.LA(1);

                         
                        int index5_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_27==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 51;}

                         
                        input.seek(index5_27);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA5_8 = input.LA(1);

                         
                        int index5_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_8==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 19;}

                        else if ( (LA5_8==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 20;}

                        else if ( (LA5_8==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) {s = 10;}

                         
                        input.seek(index5_8);
                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA5_111 = input.LA(1);

                         
                        int index5_111 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_111==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 20;}

                        else if ( (LA5_111==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 42;}

                        else if ( (LA5_111==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 95;}

                         
                        input.seek(index5_111);
                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA5_102 = input.LA(1);

                         
                        int index5_102 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_102==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 114;}

                        else if ( ((LA5_102>=VT_COMPILATION_UNIT && LA5_102<=SEMICOLON)||(LA5_102>=DOT && LA5_102<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_102);
                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA5_26 = input.LA(1);

                         
                        int index5_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_26==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 50;}

                         
                        input.seek(index5_26);
                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA5_36 = input.LA(1);

                         
                        int index5_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.DIALECT))) ) {s = 18;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))) ) {s = 17;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {s = 10;}

                         
                        input.seek(index5_36);
                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA5_51 = input.LA(1);

                         
                        int index5_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_51==ID) && ((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))) {s = 17;}

                        else if ( (LA5_51==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 27;}

                        else if ( (LA5_51==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) {s = 10;}

                         
                        input.seek(index5_51);
                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA5_53 = input.LA(1);

                         
                        int index5_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_53==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))) {s = 45;}

                        else if ( (LA5_53==EOF||(LA5_53>=SEMICOLON && LA5_53<=ID)) && ((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))) {s = 71;}

                         
                        input.seek(index5_53);
                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA5_50 = input.LA(1);

                         
                        int index5_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_50==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 27;}

                        else if ( (LA5_50==ID) && ((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))) {s = 17;}

                        else if ( (LA5_50==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 26;}

                        else if ( (LA5_50==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) {s = 10;}

                         
                        input.seek(index5_50);
                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA5_77 = input.LA(1);

                         
                        int index5_77 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.DIALECT))) ) {s = 18;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                         
                        input.seek(index5_77);
                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA5_109 = input.LA(1);

                         
                        int index5_109 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_109==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 42;}

                        else if ( (LA5_109==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 20;}

                        else if ( (LA5_109==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 92;}

                         
                        input.seek(index5_109);
                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA5_23 = input.LA(1);

                         
                        int index5_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_23==COMMA||LA5_23==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_23==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_23==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_23);
                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA5_74 = input.LA(1);

                         
                        int index5_74 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_74==COMMA||LA5_74==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_74==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_74==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                         
                        input.seek(index5_74);
                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA5_112 = input.LA(1);

                         
                        int index5_112 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_112==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 119;}

                        else if ( (LA5_112==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_112==COMMA||LA5_112==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_112==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_112);
                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA5_100 = input.LA(1);

                         
                        int index5_100 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_100==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_100==COMMA||LA5_100==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_100==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_100);
                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA5_47 = input.LA(1);

                         
                        int index5_47 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {s = 10;}

                         
                        input.seek(index5_47);
                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA5_88 = input.LA(1);

                         
                        int index5_88 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_88==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 101;}

                        else if ( (LA5_88==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 102;}

                        else if ( (LA5_88==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 89;}

                        else if ( (LA5_88==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 103;}

                        else if ( ((LA5_88>=VT_COMPILATION_UNIT && LA5_88<=SEMICOLON)||(LA5_88>=DOT_STAR && LA5_88<=LEFT_PAREN)||(LA5_88>=COLON && LA5_88<=NULL)||(LA5_88>=RIGHT_SQUARE && LA5_88<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_88==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 104;}

                         
                        input.seek(index5_88);
                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA5_98 = input.LA(1);

                         
                        int index5_98 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_98==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 112;}

                         
                        input.seek(index5_98);
                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA5_60 = input.LA(1);

                         
                        int index5_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_60==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 86;}

                         
                        input.seek(index5_60);
                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA5_56 = input.LA(1);

                         
                        int index5_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_56==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 75;}

                        else if ( (LA5_56==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 76;}

                        else if ( (LA5_56==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 77;}

                        else if ( (LA5_56==INT) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 78;}

                        else if ( (LA5_56==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 79;}

                        else if ( (LA5_56==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 80;}

                        else if ( (LA5_56==COLON||LA5_56==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_56);
                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA5_108 = input.LA(1);

                         
                        int index5_108 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_108==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                        else if ( (LA5_108==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_108==COMMA||LA5_108==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_108);
                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA5_58 = input.LA(1);

                         
                        int index5_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_58==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 82;}

                        else if ( (LA5_58==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 83;}

                        else if ( (LA5_58==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 84;}

                        else if ( (LA5_58==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 20;}

                        else if ( (LA5_58==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 42;}

                         
                        input.seek(index5_58);
                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA5_83 = input.LA(1);

                         
                        int index5_83 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_83==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 94;}

                         
                        input.seek(index5_83);
                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA5_1 = input.LA(1);

                         
                        int index5_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {s = 2;}

                        else if ( (LA5_1==ID) && ((((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 3;}

                        else if ( (LA5_1==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 4;}

                        else if ( (LA5_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {s = 5;}

                        else if ( (LA5_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {s = 6;}

                        else if ( (LA5_1==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 7;}

                         
                        input.seek(index5_1);
                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA5_94 = input.LA(1);

                         
                        int index5_94 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_94==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 84;}

                        else if ( (LA5_94==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 83;}

                        else if ( (LA5_94==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 20;}

                        else if ( (LA5_94==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 42;}

                         
                        input.seek(index5_94);
                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA5_67 = input.LA(1);

                         
                        int index5_67 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {s = 15;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                         
                        input.seek(index5_67);
                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA5_52 = input.LA(1);

                         
                        int index5_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_52==STRING||(LA5_52>=BOOL && LA5_52<=INT)||LA5_52==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_52==ID) && (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 69;}

                        else if ( (LA5_52==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 70;}

                        else if ( (LA5_52==EOF||LA5_52==SEMICOLON) && ((validateIdentifierKey(DroolsSoftKeywords.GLOBAL)))) {s = 71;}

                         
                        input.seek(index5_52);
                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA5_82 = input.LA(1);

                         
                        int index5_82 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_82==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 93;}

                         
                        input.seek(index5_82);
                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA5_85 = input.LA(1);

                         
                        int index5_85 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_85==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 96;}

                         
                        input.seek(index5_85);
                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA5_120 = input.LA(1);

                         
                        int index5_120 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_120==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 126;}

                        else if ( ((LA5_120>=VT_COMPILATION_UNIT && LA5_120<=SEMICOLON)||(LA5_120>=DOT && LA5_120<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_120);
                        if ( s>=0 ) return s;
                        break;
                    case 57 : 
                        int LA5_103 = input.LA(1);

                         
                        int index5_103 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_103==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 115;}

                        else if ( ((LA5_103>=VT_COMPILATION_UNIT && LA5_103<=SEMICOLON)||(LA5_103>=DOT && LA5_103<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_103);
                        if ( s>=0 ) return s;
                        break;
                    case 58 : 
                        int LA5_89 = input.LA(1);

                         
                        int index5_89 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_89==LEFT_CURLY) && ((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))) {s = 45;}

                        else if ( (LA5_89==EOF||LA5_89==ID) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_89);
                        if ( s>=0 ) return s;
                        break;
                    case 59 : 
                        int LA5_114 = input.LA(1);

                         
                        int index5_114 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_114==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 120;}

                        else if ( (LA5_114==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 121;}

                        else if ( (LA5_114==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 122;}

                        else if ( (LA5_114==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 89;}

                        else if ( (LA5_114==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 102;}

                        else if ( ((LA5_114>=VT_COMPILATION_UNIT && LA5_114<=SEMICOLON)||(LA5_114>=DOT_STAR && LA5_114<=LEFT_PAREN)||(LA5_114>=COLON && LA5_114<=NULL)||(LA5_114>=RIGHT_SQUARE && LA5_114<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_114);
                        if ( s>=0 ) return s;
                        break;
                    case 60 : 
                        int LA5_29 = input.LA(1);

                         
                        int index5_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_29==COMMA||LA5_29==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_29==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_29==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_29);
                        if ( s>=0 ) return s;
                        break;
                    case 61 : 
                        int LA5_61 = input.LA(1);

                         
                        int index5_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_61==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_61==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_61==COMMA||LA5_61==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_61);
                        if ( s>=0 ) return s;
                        break;
                    case 62 : 
                        int LA5_35 = input.LA(1);

                         
                        int index5_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_35==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 55;}

                         
                        input.seek(index5_35);
                        if ( s>=0 ) return s;
                        break;
                    case 63 : 
                        int LA5_75 = input.LA(1);

                         
                        int index5_75 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_75==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 91;}

                         
                        input.seek(index5_75);
                        if ( s>=0 ) return s;
                        break;
                    case 64 : 
                        int LA5_121 = input.LA(1);

                         
                        int index5_121 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_121==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 127;}

                        else if ( ((LA5_121>=VT_COMPILATION_UNIT && LA5_121<=LEFT_SQUARE)||(LA5_121>=THEN && LA5_121<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_121);
                        if ( s>=0 ) return s;
                        break;
                    case 65 : 
                        int LA5_101 = input.LA(1);

                         
                        int index5_101 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_101==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 113;}

                        else if ( ((LA5_101>=VT_COMPILATION_UNIT && LA5_101<=LEFT_SQUARE)||(LA5_101>=THEN && LA5_101<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_101);
                        if ( s>=0 ) return s;
                        break;
                    case 66 : 
                        int LA5_39 = input.LA(1);

                         
                        int index5_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.DIALECT))) ) {s = 18;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {s = 10;}

                         
                        input.seek(index5_39);
                        if ( s>=0 ) return s;
                        break;
                    case 67 : 
                        int LA5_4 = input.LA(1);

                         
                        int index5_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_4==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 16;}

                        else if ( (LA5_4==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_4==END||LA5_4==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) {s = 10;}

                        else if ( (LA5_4==SEMICOLON) && ((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))) {s = 17;}

                        else if ( (LA5_4==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                         
                        input.seek(index5_4);
                        if ( s>=0 ) return s;
                        break;
                    case 68 : 
                        int LA5_20 = input.LA(1);

                         
                        int index5_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_20==ID||LA5_20==END||LA5_20==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) {s = 10;}

                        else if ( (LA5_20==LEFT_CURLY) && ((validateIdentifierKey(DroolsSoftKeywords.FUNCTION)))) {s = 45;}

                         
                        input.seek(index5_20);
                        if ( s>=0 ) return s;
                        break;
                    case 69 : 
                        int LA5_125 = input.LA(1);

                         
                        int index5_125 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_125==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                        else if ( (LA5_125==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_125==COMMA||LA5_125==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_125);
                        if ( s>=0 ) return s;
                        break;
                    case 70 : 
                        int LA5_80 = input.LA(1);

                         
                        int index5_80 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_80==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                        else if ( (LA5_80==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_80==COMMA||LA5_80==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_80);
                        if ( s>=0 ) return s;
                        break;
                    case 71 : 
                        int LA5_16 = input.LA(1);

                         
                        int index5_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_16==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 35;}

                        else if ( (LA5_16==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))))) {s = 22;}

                        else if ( (LA5_16==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 36;}

                        else if ( (LA5_16==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 37;}

                        else if ( (LA5_16==INT) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 38;}

                        else if ( (LA5_16==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 39;}

                        else if ( (LA5_16==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 26;}

                        else if ( (LA5_16==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 27;}

                        else if ( (LA5_16==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 40;}

                        else if ( (LA5_16==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_16);
                        if ( s>=0 ) return s;
                        break;
                    case 72 : 
                        int LA5_68 = input.LA(1);

                         
                        int index5_68 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_68==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_68==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_68==COMMA||LA5_68==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_68);
                        if ( s>=0 ) return s;
                        break;
                    case 73 : 
                        int LA5_24 = input.LA(1);

                         
                        int index5_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {s = 15;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.FUNCTION))) ) {s = 45;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {s = 10;}

                         
                        input.seek(index5_24);
                        if ( s>=0 ) return s;
                        break;
                    case 74 : 
                        int LA5_37 = input.LA(1);

                         
                        int index5_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.DIALECT))) ) {s = 18;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                         
                        input.seek(index5_37);
                        if ( s>=0 ) return s;
                        break;
                    case 75 : 
                        int LA5_107 = input.LA(1);

                         
                        int index5_107 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_107==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                        else if ( (LA5_107==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_107==COMMA||LA5_107==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_107);
                        if ( s>=0 ) return s;
                        break;
                    case 76 : 
                        int LA5_86 = input.LA(1);

                         
                        int index5_86 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_86==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 97;}

                        else if ( (LA5_86==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_86==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_86==COMMA||LA5_86==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_86);
                        if ( s>=0 ) return s;
                        break;
                    case 77 : 
                        int LA5_42 = input.LA(1);

                         
                        int index5_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_42==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 58;}

                         
                        input.seek(index5_42);
                        if ( s>=0 ) return s;
                        break;
                    case 78 : 
                        int LA5_124 = input.LA(1);

                         
                        int index5_124 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_124==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 89;}

                        else if ( (LA5_124==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 102;}

                        else if ( ((LA5_124>=VT_COMPILATION_UNIT && LA5_124<=LEFT_PAREN)||(LA5_124>=COLON && LA5_124<=NULL)||(LA5_124>=RIGHT_SQUARE && LA5_124<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_124==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 116;}

                         
                        input.seek(index5_124);
                        if ( s>=0 ) return s;
                        break;
                    case 79 : 
                        int LA5_93 = input.LA(1);

                         
                        int index5_93 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_93==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 110;}

                        else if ( (LA5_93==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 84;}

                        else if ( (LA5_93==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 82;}

                         
                        input.seek(index5_93);
                        if ( s>=0 ) return s;
                        break;
                    case 80 : 
                        int LA5_90 = input.LA(1);

                         
                        int index5_90 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_90==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 105;}

                        else if ( (LA5_90==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                        else if ( (LA5_90==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_90==COMMA||LA5_90==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_90);
                        if ( s>=0 ) return s;
                        break;
                    case 81 : 
                        int LA5_22 = input.LA(1);

                         
                        int index5_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_22==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))))) {s = 47;}

                        else if ( (LA5_22==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.RULE))))) {s = 48;}

                        else if ( (LA5_22==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_22);
                        if ( s>=0 ) return s;
                        break;
                    case 82 : 
                        int LA5_19 = input.LA(1);

                         
                        int index5_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_19==LEFT_PAREN||LA5_19==COLON) && ((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) {s = 10;}

                        else if ( (LA5_19==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 41;}

                        else if ( (LA5_19==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 42;}

                        else if ( (LA5_19==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 20;}

                        else if ( (LA5_19==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 43;}

                        else if ( (LA5_19==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 44;}

                         
                        input.seek(index5_19);
                        if ( s>=0 ) return s;
                        break;
                    case 83 : 
                        int LA5_130 = input.LA(1);

                         
                        int index5_130 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_130==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 132;}

                        else if ( ((LA5_130>=VT_COMPILATION_UNIT && LA5_130<=LEFT_SQUARE)||(LA5_130>=THEN && LA5_130<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_130);
                        if ( s>=0 ) return s;
                        break;
                    case 84 : 
                        int LA5_128 = input.LA(1);

                         
                        int index5_128 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_128==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 131;}

                        else if ( ((LA5_128>=VT_COMPILATION_UNIT && LA5_128<=LEFT_SQUARE)||(LA5_128>=THEN && LA5_128<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_128);
                        if ( s>=0 ) return s;
                        break;
                    case 85 : 
                        int LA5_123 = input.LA(1);

                         
                        int index5_123 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_123==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 129;}

                        else if ( ((LA5_123>=VT_COMPILATION_UNIT && LA5_123<=LEFT_SQUARE)||(LA5_123>=THEN && LA5_123<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_123);
                        if ( s>=0 ) return s;
                        break;
                    case 86 : 
                        int LA5_116 = input.LA(1);

                         
                        int index5_116 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_116==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 124;}

                        else if ( ((LA5_116>=VT_COMPILATION_UNIT && LA5_116<=LEFT_SQUARE)||(LA5_116>=THEN && LA5_116<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_116);
                        if ( s>=0 ) return s;
                        break;
                    case 87 : 
                        int LA5_9 = input.LA(1);

                         
                        int index5_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_9==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 21;}

                        else if ( (LA5_9==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))))) {s = 22;}

                        else if ( (LA5_9==INT) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 23;}

                        else if ( (LA5_9==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 24;}

                        else if ( (LA5_9==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 25;}

                        else if ( (LA5_9==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 26;}

                        else if ( (LA5_9==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 27;}

                        else if ( (LA5_9==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 28;}

                        else if ( (LA5_9==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 29;}

                        else if ( (LA5_9==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) ) {s = 30;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))) ) {s = 31;}

                         
                        input.seek(index5_9);
                        if ( s>=0 ) return s;
                        break;
                    case 88 : 
                        int LA5_3 = input.LA(1);

                         
                        int index5_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_3==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 8;}

                        else if ( (LA5_3==ID) && ((((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.RULE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.QUERY))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))||((validateLT(1, "import") && validateLT(2, "function") )&&(validateIdentifierKey(DroolsSoftKeywords.IMPORT)))))) {s = 9;}

                        else if ( (LA5_3==END) && ((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) {s = 10;}

                        else if ( (LA5_3==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_3==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 12;}

                        else if ( (LA5_3==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 13;}

                        else if ( (LA5_3==SEMICOLON) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 14;}

                        else if ( (LA5_3==EOF||LA5_3==DOT_STAR) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_3);
                        if ( s>=0 ) return s;
                        break;
                    case 89 : 
                        int LA5_97 = input.LA(1);

                         
                        int index5_97 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_97==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_97==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_97==COMMA||LA5_97==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_97);
                        if ( s>=0 ) return s;
                        break;
                    case 90 : 
                        int LA5_34 = input.LA(1);

                         
                        int index5_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA5_34>=STRING && LA5_34<=LEFT_PAREN)||(LA5_34>=BOOL && LA5_34<=INT)||LA5_34==MISC) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_34==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))))) {s = 54;}

                        else if ( (LA5_34==DOT||LA5_34==LEFT_SQUARE) && ((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE)))) {s = 17;}

                         
                        input.seek(index5_34);
                        if ( s>=0 ) return s;
                        break;
                    case 91 : 
                        int LA5_79 = input.LA(1);

                         
                        int index5_79 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.DIALECT))) ) {s = 18;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                         
                        input.seek(index5_79);
                        if ( s>=0 ) return s;
                        break;
                    case 92 : 
                        int LA5_105 = input.LA(1);

                         
                        int index5_105 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_105==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                        else if ( (LA5_105==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_105==COMMA||LA5_105==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_105);
                        if ( s>=0 ) return s;
                        break;
                    case 93 : 
                        int LA5_113 = input.LA(1);

                         
                        int index5_113 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_113==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 89;}

                        else if ( (LA5_113==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 102;}

                        else if ( ((LA5_113>=VT_COMPILATION_UNIT && LA5_113<=SEMICOLON)||(LA5_113>=DOT && LA5_113<=LEFT_PAREN)||(LA5_113>=COLON && LA5_113<=NULL)||(LA5_113>=RIGHT_SQUARE && LA5_113<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_113==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 101;}

                        else if ( (LA5_113==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 104;}

                         
                        input.seek(index5_113);
                        if ( s>=0 ) return s;
                        break;
                    case 94 : 
                        int LA5_41 = input.LA(1);

                         
                        int index5_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_41==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 57;}

                         
                        input.seek(index5_41);
                        if ( s>=0 ) return s;
                        break;
                    case 95 : 
                        int LA5_126 = input.LA(1);

                         
                        int index5_126 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA5_126>=VT_COMPILATION_UNIT && LA5_126<=SEMICOLON)||(LA5_126>=DOT_STAR && LA5_126<=NULL)||(LA5_126>=RIGHT_SQUARE && LA5_126<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_126==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 130;}

                        else if ( (LA5_126==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 122;}

                        else if ( (LA5_126==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 120;}

                         
                        input.seek(index5_126);
                        if ( s>=0 ) return s;
                        break;
                    case 96 : 
                        int LA5_115 = input.LA(1);

                         
                        int index5_115 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA5_115>=VT_COMPILATION_UNIT && LA5_115<=SEMICOLON)||(LA5_115>=DOT_STAR && LA5_115<=NULL)||(LA5_115>=RIGHT_SQUARE && LA5_115<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_115==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 123;}

                        else if ( (LA5_115==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 104;}

                        else if ( (LA5_115==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 103;}

                         
                        input.seek(index5_115);
                        if ( s>=0 ) return s;
                        break;
                    case 97 : 
                        int LA5_33 = input.LA(1);

                         
                        int index5_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_33==ID) && (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 53;}

                        else if ( (LA5_33==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 13;}

                         
                        input.seek(index5_33);
                        if ( s>=0 ) return s;
                        break;
                    case 98 : 
                        int LA5_91 = input.LA(1);

                         
                        int index5_91 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_91==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 106;}

                        else if ( (LA5_91==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 107;}

                        else if ( (LA5_91==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 108;}

                        else if ( (LA5_91==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                        else if ( (LA5_91==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_91==COMMA||LA5_91==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_91);
                        if ( s>=0 ) return s;
                        break;
                    case 99 : 
                        int LA5_55 = input.LA(1);

                         
                        int index5_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_55==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 72;}

                        else if ( (LA5_55==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 73;}

                        else if ( (LA5_55==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 74;}

                        else if ( (LA5_55==COMMA||LA5_55==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_55==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_55==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                         
                        input.seek(index5_55);
                        if ( s>=0 ) return s;
                        break;
                    case 100 : 
                        int LA5_38 = input.LA(1);

                         
                        int index5_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_38==COMMA||LA5_38==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_38==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_38==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                         
                        input.seek(index5_38);
                        if ( s>=0 ) return s;
                        break;
                    case 101 : 
                        int LA5_48 = input.LA(1);

                         
                        int index5_48 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.QUERY))) ) {s = 10;}

                         
                        input.seek(index5_48);
                        if ( s>=0 ) return s;
                        break;
                    case 102 : 
                        int LA5_66 = input.LA(1);

                         
                        int index5_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {s = 15;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                         
                        input.seek(index5_66);
                        if ( s>=0 ) return s;
                        break;
                    case 103 : 
                        int LA5_84 = input.LA(1);

                         
                        int index5_84 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_84==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 95;}

                        else if ( (LA5_84==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 20;}

                        else if ( (LA5_84==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 42;}

                         
                        input.seek(index5_84);
                        if ( s>=0 ) return s;
                        break;
                    case 104 : 
                        int LA5_54 = input.LA(1);

                         
                        int index5_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {s = 15;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))) ) {s = 17;}

                         
                        input.seek(index5_54);
                        if ( s>=0 ) return s;
                        break;
                    case 105 : 
                        int LA5_64 = input.LA(1);

                         
                        int index5_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {s = 15;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                         
                        input.seek(index5_64);
                        if ( s>=0 ) return s;
                        break;
                    case 106 : 
                        int LA5_106 = input.LA(1);

                         
                        int index5_106 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_106==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 117;}

                         
                        input.seek(index5_106);
                        if ( s>=0 ) return s;
                        break;
                    case 107 : 
                        int LA5_72 = input.LA(1);

                         
                        int index5_72 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_72==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 90;}

                         
                        input.seek(index5_72);
                        if ( s>=0 ) return s;
                        break;
                    case 108 : 
                        int LA5_13 = input.LA(1);

                         
                        int index5_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_13==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 33;}

                         
                        input.seek(index5_13);
                        if ( s>=0 ) return s;
                        break;
                    case 109 : 
                        int LA5_127 = input.LA(1);

                         
                        int index5_127 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_127==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 122;}

                        else if ( (LA5_127==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 121;}

                        else if ( (LA5_127==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 89;}

                        else if ( (LA5_127==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 102;}

                        else if ( ((LA5_127>=VT_COMPILATION_UNIT && LA5_127<=SEMICOLON)||(LA5_127>=DOT && LA5_127<=LEFT_PAREN)||(LA5_127>=COLON && LA5_127<=NULL)||(LA5_127>=RIGHT_SQUARE && LA5_127<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                         
                        input.seek(index5_127);
                        if ( s>=0 ) return s;
                        break;
                    case 110 : 
                        int LA5_49 = input.LA(1);

                         
                        int index5_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_49==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 63;}

                        else if ( (LA5_49==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 64;}

                        else if ( (LA5_49==INT) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 65;}

                        else if ( (LA5_49==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 66;}

                        else if ( (LA5_49==STRING) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 67;}

                        else if ( (LA5_49==COLON||LA5_49==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_49==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 68;}

                         
                        input.seek(index5_49);
                        if ( s>=0 ) return s;
                        break;
                    case 111 : 
                        int LA5_69 = input.LA(1);

                         
                        int index5_69 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {s = 15;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))) ) {s = 71;}

                         
                        input.seek(index5_69);
                        if ( s>=0 ) return s;
                        break;
                    case 112 : 
                        int LA5_119 = input.LA(1);

                         
                        int index5_119 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_119==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_119==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))))) {s = 49;}

                        else if ( (LA5_119==COMMA||LA5_119==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                         
                        input.seek(index5_119);
                        if ( s>=0 ) return s;
                        break;
                    case 113 : 
                        int LA5_57 = input.LA(1);

                         
                        int index5_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_57==LEFT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.QUERY)))) {s = 10;}

                        else if ( (LA5_57==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 41;}

                        else if ( (LA5_57==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 42;}

                        else if ( (LA5_57==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 20;}

                        else if ( (LA5_57==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 81;}

                         
                        input.seek(index5_57);
                        if ( s>=0 ) return s;
                        break;
                    case 114 : 
                        int LA5_28 = input.LA(1);

                         
                        int index5_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.IMPORT))) ) {s = 15;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RULE))) ) {s = 11;}

                         
                        input.seek(index5_28);
                        if ( s>=0 ) return s;
                        break;
                    case 115 : 
                        int LA5_12 = input.LA(1);

                         
                        int index5_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_12==ID) && (((validateIdentifierKey(DroolsSoftKeywords.GLOBAL))||(validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 32;}

                         
                        input.seek(index5_12);
                        if ( s>=0 ) return s;
                        break;
                    case 116 : 
                        int LA5_118 = input.LA(1);

                         
                        int index5_118 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_118==ID) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 84;}

                        else if ( (LA5_118==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.QUERY))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 110;}

                         
                        input.seek(index5_118);
                        if ( s>=0 ) return s;
                        break;
                    case 117 : 
                        int LA5_117 = input.LA(1);

                         
                        int index5_117 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_117==BOOL) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 125;}

                        else if ( (LA5_117==ID) && (((validateIdentifierKey(DroolsSoftKeywords.RULE))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))))) {s = 56;}

                        else if ( (LA5_117==COMMA||LA5_117==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.RULE)))) {s = 11;}

                        else if ( (LA5_117==EOF) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 18;}

                         
                        input.seek(index5_117);
                        if ( s>=0 ) return s;
                        break;
                    case 118 : 
                        int LA5_122 = input.LA(1);

                         
                        int index5_122 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_122==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 89;}

                        else if ( (LA5_122==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 128;}

                        else if ( ((LA5_122>=VT_COMPILATION_UNIT && LA5_122<=LEFT_PAREN)||(LA5_122>=COLON && LA5_122<=NULL)||(LA5_122>=RIGHT_SQUARE && LA5_122<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_122==COMMA) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 102;}

                         
                        input.seek(index5_122);
                        if ( s>=0 ) return s;
                        break;
                    case 119 : 
                        int LA5_70 = input.LA(1);

                         
                        int index5_70 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_70==ID) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 88;}

                        else if ( ((LA5_70>=VT_COMPILATION_UNIT && LA5_70<=SEMICOLON)||(LA5_70>=DOT && LA5_70<=COMMA)||(LA5_70>=COLON && LA5_70<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.IMPORT)))) {s = 15;}

                        else if ( (LA5_70==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.IMPORT))||(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))))) {s = 89;}

                         
                        input.seek(index5_70);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
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
        "\2\126\1\uffff\1\126\1\uffff\1\126\1\156\3\126\2\156\1\133\1\126";
    static final String DFA12_maxS =
        "\1\133\1\135\1\uffff\1\155\1\uffff\1\126\1\156\3\155\2\156\2\155";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\11\uffff";
    static final String DFA12_specialS =
        "\16\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\2\2\uffff\1\2\1\uffff\1\1",
            "\1\3\4\uffff\1\2\1\uffff\1\4",
            "",
            "\1\7\1\5\3\uffff\1\2\2\4\1\2\16\uffff\1\6",
            "",
            "\1\10",
            "\1\11",
            "\2\2\3\uffff\1\2\2\4\1\2\16\uffff\1\12",
            "\1\4\1\5\3\uffff\1\2\21\uffff\1\13",
            "\1\4\4\uffff\1\2\2\4\17\uffff\1\6",
            "\1\14",
            "\1\15",
            "\1\2\2\4\17\uffff\1\12",
            "\1\4\4\uffff\1\2\21\uffff\1\13"
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
            return "327:23: ( parameters )?";
        }
    }
    static final String DFA17_eotS =
        "\6\uffff";
    static final String DFA17_eofS =
        "\6\uffff";
    static final String DFA17_minS =
        "\2\126\1\uffff\1\156\1\uffff\1\126";
    static final String DFA17_maxS =
        "\1\126\1\155\1\uffff\1\156\1\uffff\1\155";
    static final String DFA17_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    static final String DFA17_specialS =
        "\6\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\1",
            "\2\2\4\uffff\2\4\17\uffff\1\3",
            "",
            "\1\5",
            "",
            "\1\2\5\uffff\2\4\17\uffff\1\3"
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
            return "346:4: ( data_type )?";
        }
    }
    static final String DFA24_eotS =
        "\33\uffff";
    static final String DFA24_eofS =
        "\33\uffff";
    static final String DFA24_minS =
        "\2\126\3\uffff\1\4\1\126\3\uffff\1\4\1\uffff\2\0\1\4\2\0\3\4\1\0"+
        "\2\4\4\0";
    static final String DFA24_maxS =
        "\1\157\1\162\3\uffff\1\173\1\157\3\uffff\1\173\1\uffff\2\0\1\173"+
        "\2\0\3\173\1\0\2\173\4\0";
    static final String DFA24_acceptS =
        "\2\uffff\1\2\2\1\2\uffff\1\2\2\1\1\uffff\1\1\17\uffff";
    static final String DFA24_specialS =
        "\1\4\1\12\3\uffff\1\7\1\1\3\uffff\1\0\1\uffff\1\17\1\6\1\13\1\15"+
        "\1\3\1\16\1\11\1\14\1\2\1\5\1\10\4\uffff}>";
    static final String[] DFA24_transitionS = {
            "\1\1\30\uffff\1\2",
            "\1\7\3\uffff\1\10\1\5\2\uffff\1\6\1\11\1\4\16\uffff\1\7\2\uffff"+
            "\1\3",
            "",
            "",
            "",
            "\122\13\1\12\4\13\1\14\40\13",
            "\1\15\4\uffff\1\7\23\uffff\1\7",
            "",
            "",
            "",
            "\122\13\1\20\1\21\3\13\1\17\2\13\1\16\16\13\1\22\16\13",
            "",
            "\1\uffff",
            "\1\uffff",
            "\122\13\1\23\4\13\1\24\40\13",
            "\1\uffff",
            "\1\uffff",
            "\122\13\1\25\45\13",
            "\152\13\1\26\15\13",
            "\123\13\1\27\3\13\1\31\21\13\1\30\16\13",
            "\1\uffff",
            "\123\13\1\21\3\13\1\32\21\13\1\22\16\13",
            "\127\13\1\32\21\13\1\22\16\13",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA24_eot = DFA.unpackEncodedString(DFA24_eotS);
    static final short[] DFA24_eof = DFA.unpackEncodedString(DFA24_eofS);
    static final char[] DFA24_min = DFA.unpackEncodedStringToUnsignedChars(DFA24_minS);
    static final char[] DFA24_max = DFA.unpackEncodedStringToUnsignedChars(DFA24_maxS);
    static final short[] DFA24_accept = DFA.unpackEncodedString(DFA24_acceptS);
    static final short[] DFA24_special = DFA.unpackEncodedString(DFA24_specialS);
    static final short[][] DFA24_transition;

    static {
        int numStates = DFA24_transitionS.length;
        DFA24_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA24_transition[i] = DFA.unpackEncodedString(DFA24_transitionS[i]);
        }
    }

    class DFA24 extends DFA {

        public DFA24(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 24;
            this.eot = DFA24_eot;
            this.eof = DFA24_eof;
            this.min = DFA24_min;
            this.max = DFA24_max;
            this.accept = DFA24_accept;
            this.special = DFA24_special;
            this.transition = DFA24_transition;
        }
        public String getDescription() {
            return "382:21: ( rule_attributes )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA24_10 = input.LA(1);

                         
                        int index24_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_10==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 14;}

                        else if ( (LA24_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 15;}

                        else if ( (LA24_10==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 16;}

                        else if ( ((LA24_10>=VT_COMPILATION_UNIT && LA24_10<=SEMICOLON)||(LA24_10>=DOT_STAR && LA24_10<=STRING)||(LA24_10>=COMMA && LA24_10<=RIGHT_PAREN)||(LA24_10>=BOOL && LA24_10<=NULL)||(LA24_10>=RIGHT_SQUARE && LA24_10<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 11;}

                        else if ( (LA24_10==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                        else if ( (LA24_10==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 18;}

                         
                        input.seek(index24_10);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA24_6 = input.LA(1);

                         
                        int index24_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_6==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {s = 13;}

                        else if ( (LA24_6==LEFT_PAREN||LA24_6==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {s = 7;}

                         
                        input.seek(index24_6);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA24_20 = input.LA(1);

                         
                        int index24_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 11;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 7;}

                         
                        input.seek(index24_20);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA24_16 = input.LA(1);

                         
                        int index24_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 11;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 7;}

                         
                        input.seek(index24_16);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA24_0 = input.LA(1);

                         
                        int index24_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {s = 1;}

                        else if ( (LA24_0==THEN) ) {s = 2;}

                         
                        input.seek(index24_0);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA24_21 = input.LA(1);

                         
                        int index24_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA24_21>=VT_COMPILATION_UNIT && LA24_21<=ID)||(LA24_21>=DOT_STAR && LA24_21<=STRING)||(LA24_21>=COMMA && LA24_21<=NULL)||(LA24_21>=RIGHT_SQUARE && LA24_21<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 11;}

                        else if ( (LA24_21==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 18;}

                        else if ( (LA24_21==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 26;}

                        else if ( (LA24_21==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                         
                        input.seek(index24_21);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA24_13 = input.LA(1);

                         
                        int index24_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))) ) {s = 11;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 7;}

                         
                        input.seek(index24_13);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA24_5 = input.LA(1);

                         
                        int index24_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_5==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 10;}

                        else if ( ((LA24_5>=VT_COMPILATION_UNIT && LA24_5<=SEMICOLON)||(LA24_5>=DOT && LA24_5<=STRING)||(LA24_5>=COMMA && LA24_5<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 11;}

                        else if ( (LA24_5==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 12;}

                         
                        input.seek(index24_5);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA24_22 = input.LA(1);

                         
                        int index24_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA24_22>=VT_COMPILATION_UNIT && LA24_22<=STRING)||(LA24_22>=COMMA && LA24_22<=NULL)||(LA24_22>=RIGHT_SQUARE && LA24_22<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 11;}

                        else if ( (LA24_22==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 18;}

                        else if ( (LA24_22==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 26;}

                         
                        input.seek(index24_22);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA24_18 = input.LA(1);

                         
                        int index24_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_18==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 22;}

                        else if ( ((LA24_18>=VT_COMPILATION_UNIT && LA24_18<=LEFT_SQUARE)||(LA24_18>=THEN && LA24_18<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 11;}

                         
                        input.seek(index24_18);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA24_1 = input.LA(1);

                         
                        int index24_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {s = 3;}

                        else if ( (LA24_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {s = 4;}

                        else if ( (LA24_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 5;}

                        else if ( (LA24_1==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))))) {s = 6;}

                        else if ( (LA24_1==ID||LA24_1==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {s = 7;}

                        else if ( (LA24_1==STRING) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 8;}

                        else if ( (LA24_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {s = 9;}

                         
                        input.seek(index24_1);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA24_14 = input.LA(1);

                         
                        int index24_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA24_14>=VT_COMPILATION_UNIT && LA24_14<=SEMICOLON)||(LA24_14>=DOT && LA24_14<=STRING)||(LA24_14>=COMMA && LA24_14<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 11;}

                        else if ( (LA24_14==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 19;}

                        else if ( (LA24_14==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 20;}

                         
                        input.seek(index24_14);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA24_19 = input.LA(1);

                         
                        int index24_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_19==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 23;}

                        else if ( (LA24_19==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 24;}

                        else if ( (LA24_19==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 25;}

                        else if ( ((LA24_19>=VT_COMPILATION_UNIT && LA24_19<=ID)||(LA24_19>=DOT_STAR && LA24_19<=STRING)||(LA24_19>=COMMA && LA24_19<=NULL)||(LA24_19>=RIGHT_SQUARE && LA24_19<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 11;}

                         
                        input.seek(index24_19);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA24_15 = input.LA(1);

                         
                        int index24_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 11;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 7;}

                         
                        input.seek(index24_15);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA24_17 = input.LA(1);

                         
                        int index24_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA24_17==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 21;}

                        else if ( ((LA24_17>=VT_COMPILATION_UNIT && LA24_17<=SEMICOLON)||(LA24_17>=DOT && LA24_17<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 11;}

                         
                        input.seek(index24_17);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA24_12 = input.LA(1);

                         
                        int index24_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 11;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 7;}

                         
                        input.seek(index24_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 24, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA30_eotS =
        "\32\uffff";
    static final String DFA30_eofS =
        "\32\uffff";
    static final String DFA30_minS =
        "\2\126\4\uffff\1\4\3\uffff\1\4\1\0\1\uffff\1\4\2\0\3\4\1\0\2\4\4"+
        "\0";
    static final String DFA30_maxS =
        "\1\157\1\162\4\uffff\1\173\3\uffff\1\173\1\0\1\uffff\1\173\2\0\3"+
        "\173\1\0\2\173\4\0";
    static final String DFA30_acceptS =
        "\2\uffff\1\2\3\1\1\uffff\2\1\1\2\2\uffff\1\1\15\uffff";
    static final String DFA30_specialS =
        "\1\15\1\6\4\uffff\1\4\3\uffff\1\0\1\10\1\uffff\1\11\1\13\1\3\1\14"+
        "\1\7\1\12\1\1\1\2\1\5\4\uffff}>";
    static final String[] DFA30_transitionS = {
            "\1\1\5\uffff\1\3\22\uffff\1\2",
            "\1\11\3\uffff\1\10\1\6\2\uffff\1\11\1\7\1\5\16\uffff\1\11\2"+
            "\uffff\1\4",
            "",
            "",
            "",
            "",
            "\122\14\1\12\4\14\1\13\40\14",
            "",
            "",
            "",
            "\122\14\1\17\1\20\3\14\1\16\2\14\1\15\16\14\1\21\16\14",
            "\1\uffff",
            "",
            "\122\14\1\22\4\14\1\23\40\14",
            "\1\uffff",
            "\1\uffff",
            "\122\14\1\24\45\14",
            "\152\14\1\25\15\14",
            "\123\14\1\26\3\14\1\30\21\14\1\27\16\14",
            "\1\uffff",
            "\123\14\1\20\3\14\1\31\21\14\1\21\16\14",
            "\127\14\1\31\21\14\1\21\16\14",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
    static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
    static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
    static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
    static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
    static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
        }
    }

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }
        public String getDescription() {
            return "()* loopback of 399:45: ( ( COMMA )? attr= rule_attribute )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA30_10 = input.LA(1);

                         
                        int index30_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA30_10==COLON) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 13;}

                        else if ( (LA30_10==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 14;}

                        else if ( ((LA30_10>=VT_COMPILATION_UNIT && LA30_10<=SEMICOLON)||(LA30_10>=DOT_STAR && LA30_10<=STRING)||(LA30_10>=COMMA && LA30_10<=RIGHT_PAREN)||(LA30_10>=BOOL && LA30_10<=NULL)||(LA30_10>=RIGHT_SQUARE && LA30_10<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA30_10==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 15;}

                        else if ( (LA30_10==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 16;}

                        else if ( (LA30_10==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                         
                        input.seek(index30_10);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA30_19 = input.LA(1);

                         
                        int index30_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index30_19);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA30_20 = input.LA(1);

                         
                        int index30_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA30_20>=VT_COMPILATION_UNIT && LA30_20<=ID)||(LA30_20>=DOT_STAR && LA30_20<=STRING)||(LA30_20>=COMMA && LA30_20<=NULL)||(LA30_20>=RIGHT_SQUARE && LA30_20<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA30_20==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                        else if ( (LA30_20==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 25;}

                        else if ( (LA30_20==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 16;}

                         
                        input.seek(index30_20);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA30_15 = input.LA(1);

                         
                        int index30_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index30_15);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA30_6 = input.LA(1);

                         
                        int index30_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA30_6==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 10;}

                        else if ( (LA30_6==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 11;}

                        else if ( ((LA30_6>=VT_COMPILATION_UNIT && LA30_6<=SEMICOLON)||(LA30_6>=DOT && LA30_6<=STRING)||(LA30_6>=COMMA && LA30_6<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index30_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA30_21 = input.LA(1);

                         
                        int index30_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA30_21>=VT_COMPILATION_UNIT && LA30_21<=STRING)||(LA30_21>=COMMA && LA30_21<=NULL)||(LA30_21>=RIGHT_SQUARE && LA30_21<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA30_21==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 17;}

                        else if ( (LA30_21==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 25;}

                         
                        input.seek(index30_21);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA30_1 = input.LA(1);

                         
                        int index30_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA30_1==MISC) && (((validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))))) {s = 4;}

                        else if ( (LA30_1==INT) && (((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))))) {s = 5;}

                        else if ( (LA30_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 6;}

                        else if ( (LA30_1==BOOL) && ((validateIdentifierKey(DroolsSoftKeywords.ENABLED)))) {s = 7;}

                        else if ( (LA30_1==STRING) && ((validateIdentifierKey(DroolsSoftKeywords.DIALECT)))) {s = 8;}

                        else if ( (LA30_1==ID||LA30_1==COLON||LA30_1==THEN) && ((validateIdentifierKey(DroolsSoftKeywords.WHEN)))) {s = 9;}

                         
                        input.seek(index30_1);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA30_17 = input.LA(1);

                         
                        int index30_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA30_17==RIGHT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 21;}

                        else if ( ((LA30_17>=VT_COMPILATION_UNIT && LA30_17<=LEFT_SQUARE)||(LA30_17>=THEN && LA30_17<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index30_17);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA30_11 = input.LA(1);

                         
                        int index30_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index30_11);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA30_13 = input.LA(1);

                         
                        int index30_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA30_13>=VT_COMPILATION_UNIT && LA30_13<=SEMICOLON)||(LA30_13>=DOT && LA30_13<=STRING)||(LA30_13>=COMMA && LA30_13<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                        else if ( (LA30_13==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 18;}

                        else if ( (LA30_13==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 19;}

                         
                        input.seek(index30_13);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA30_18 = input.LA(1);

                         
                        int index30_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA30_18==DOT) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 22;}

                        else if ( (LA30_18==LEFT_SQUARE) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 23;}

                        else if ( (LA30_18==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 24;}

                        else if ( ((LA30_18>=VT_COMPILATION_UNIT && LA30_18<=ID)||(LA30_18>=DOT_STAR && LA30_18<=STRING)||(LA30_18>=COMMA && LA30_18<=NULL)||(LA30_18>=RIGHT_SQUARE && LA30_18<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index30_18);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA30_14 = input.LA(1);

                         
                        int index30_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE))) ) {s = 12;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.WHEN))) ) {s = 9;}

                         
                        input.seek(index30_14);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA30_16 = input.LA(1);

                         
                        int index30_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA30_16==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))))) {s = 20;}

                        else if ( ((LA30_16>=VT_COMPILATION_UNIT && LA30_16<=SEMICOLON)||(LA30_16>=DOT && LA30_16<=MULTI_LINE_COMMENT)) && ((validateIdentifierKey(DroolsSoftKeywords.SALIENCE)))) {s = 12;}

                         
                        input.seek(index30_16);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA30_0 = input.LA(1);

                         
                        int index30_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA30_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.WHEN))||(validateIdentifierKey(DroolsSoftKeywords.DIALECT))||(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))||(validateIdentifierKey(DroolsSoftKeywords.DURATION))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))||(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))||(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))||(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))||(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))||(validateIdentifierKey(DroolsSoftKeywords.ENABLED))||(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))))) {s = 1;}

                        else if ( (LA30_0==THEN) ) {s = 2;}

                        else if ( (LA30_0==COMMA) ) {s = 3;}

                         
                        input.seek(index30_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 30, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA47_eotS =
        "\172\uffff";
    static final String DFA47_eofS =
        "\172\uffff";
    static final String DFA47_minS =
        "\3\126\2\0\1\126\1\0\1\uffff\1\126\1\156\2\uffff\1\127\1\126\1\uffff"+
        "\1\127\1\133\1\126\1\156\1\126\1\127\1\126\1\127\1\133\1\126\2\0"+
        "\1\126\1\156\2\126\2\0\1\126\1\0\1\4\11\126\2\uffff\1\127\1\133"+
        "\1\126\1\0\2\126\1\4\11\126\1\0\1\uffff\1\126\1\uffff\1\4\26\0\1"+
        "\uffff\17\0\1\uffff\17\0";
    static final String DFA47_maxS =
        "\2\133\1\155\2\0\1\133\1\0\1\uffff\1\126\1\156\2\uffff\1\155\1\126"+
        "\1\uffff\2\155\1\126\1\156\1\135\1\155\1\135\3\155\2\0\1\126\1\156"+
        "\1\135\1\155\2\0\1\126\1\0\1\173\1\126\7\154\1\126\2\uffff\3\155"+
        "\1\0\1\141\1\126\1\173\1\126\7\154\1\126\1\0\1\uffff\1\155\1\uffff"+
        "\1\173\26\0\1\uffff\17\0\1\uffff\17\0";
    static final String DFA47_acceptS =
        "\7\uffff\1\1\2\uffff\2\2\2\uffff\1\3\36\uffff\2\3\21\uffff\1\3\1"+
        "\uffff\1\3\27\uffff\1\3\17\uffff\1\3\17\uffff";
    static final String DFA47_specialS =
        "\2\uffff\1\5\1\0\1\7\1\uffff\1\11\22\uffff\1\3\1\12\4\uffff\1\2"+
        "\1\10\1\uffff\1\6\17\uffff\1\4\14\uffff\1\1\72\uffff}>";
    static final String[] DFA47_transitionS = {
            "\1\2\4\uffff\1\1",
            "\1\3\4\uffff\1\4",
            "\1\7\1\10\3\uffff\1\6\2\uffff\1\5\16\uffff\1\11",
            "\1\uffff",
            "\1\uffff",
            "\1\14\4\uffff\1\15",
            "\1\uffff",
            "",
            "\1\17",
            "\1\20",
            "",
            "",
            "\1\21\3\uffff\1\23\21\uffff\1\22",
            "\1\24",
            "",
            "\1\10\3\uffff\1\25\21\uffff\1\11",
            "\1\25\21\uffff\1\11",
            "\1\26",
            "\1\27",
            "\1\30\4\uffff\1\31\1\uffff\1\32",
            "\1\33\3\uffff\1\35\21\uffff\1\34",
            "\1\36\4\uffff\1\37\1\uffff\1\40",
            "\1\21\3\uffff\1\23\21\uffff\1\22",
            "\1\23\21\uffff\1\22",
            "\1\45\1\44\3\uffff\1\42\2\uffff\1\41\5\uffff\1\46\1\47\1\50"+
            "\1\51\1\52\1\53\1\54\2\uffff\1\43",
            "\1\uffff",
            "\1\uffff",
            "\1\57",
            "\1\60",
            "\1\61\4\uffff\1\62\1\uffff\1\63",
            "\1\67\1\66\3\uffff\1\77\2\uffff\1\64\5\uffff\1\70\1\71\1\72"+
            "\1\73\1\74\1\75\1\76\2\uffff\1\65",
            "\1\uffff",
            "\1\uffff",
            "\1\101",
            "\1\uffff",
            "\151\103\1\104\1\105\15\103",
            "\1\106",
            "\1\107\3\uffff\1\111\1\112\3\uffff\2\111\11\uffff\1\110\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\115",
            "",
            "",
            "\1\33\3\uffff\1\35\21\uffff\1\34",
            "\1\35\21\uffff\1\34",
            "\1\121\1\120\3\uffff\1\131\2\uffff\1\116\5\uffff\1\122\1\123"+
            "\1\124\1\125\1\126\1\127\1\130\2\uffff\1\117",
            "\1\uffff",
            "\1\133\6\uffff\1\135\3\uffff\1\134",
            "\1\136",
            "\151\137\1\140\1\141\15\137",
            "\1\142",
            "\1\143\3\uffff\1\144\1\145\3\uffff\2\144\11\uffff\1\146\2\144",
            "\1\147\3\uffff\1\144\1\150\3\uffff\2\144\12\uffff\2\144",
            "\1\147\3\uffff\1\144\1\150\3\uffff\2\144\12\uffff\2\144",
            "\1\147\3\uffff\1\144\1\150\3\uffff\2\144\12\uffff\2\144",
            "\1\147\3\uffff\1\144\1\150\3\uffff\2\144\12\uffff\2\144",
            "\1\147\3\uffff\1\144\1\150\3\uffff\2\144\12\uffff\2\144",
            "\1\147\3\uffff\1\144\1\150\3\uffff\2\144\12\uffff\2\144",
            "\1\151",
            "\1\uffff",
            "",
            "\1\155\1\154\3\uffff\1\165\1\171\1\32\3\uffff\1\170\1\167\1"+
            "\166\1\156\1\157\1\160\1\161\1\162\1\163\1\164\2\uffff\1\153",
            "",
            "\151\103\1\104\1\105\15\103",
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

    static final short[] DFA47_eot = DFA.unpackEncodedString(DFA47_eotS);
    static final short[] DFA47_eof = DFA.unpackEncodedString(DFA47_eofS);
    static final char[] DFA47_min = DFA.unpackEncodedStringToUnsignedChars(DFA47_minS);
    static final char[] DFA47_max = DFA.unpackEncodedStringToUnsignedChars(DFA47_maxS);
    static final short[] DFA47_accept = DFA.unpackEncodedString(DFA47_acceptS);
    static final short[] DFA47_special = DFA.unpackEncodedString(DFA47_specialS);
    static final short[][] DFA47_transition;

    static {
        int numStates = DFA47_transitionS.length;
        DFA47_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA47_transition[i] = DFA.unpackEncodedString(DFA47_transitionS[i]);
        }
    }

    class DFA47 extends DFA {

        public DFA47(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 47;
            this.eot = DFA47_eot;
            this.eof = DFA47_eof;
            this.min = DFA47_min;
            this.max = DFA47_max;
            this.accept = DFA47_accept;
            this.special = DFA47_special;
            this.transition = DFA47_transition;
        }
        public String getDescription() {
            return "513:10: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA47_3 = input.LA(1);

                         
                        int index47_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 10;}

                         
                        input.seek(index47_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA47_63 = input.LA(1);

                         
                        int index47_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 106;}

                         
                        input.seek(index47_63);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA47_31 = input.LA(1);

                         
                        int index47_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 64;}

                         
                        input.seek(index47_31);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA47_25 = input.LA(1);

                         
                        int index47_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 45;}

                         
                        input.seek(index47_25);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA47_50 = input.LA(1);

                         
                        int index47_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 90;}

                         
                        input.seek(index47_50);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA47_2 = input.LA(1);

                         
                        int index47_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_2==COLON) ) {s = 5;}

                        else if ( (LA47_2==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA47_2==ID) && (((synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {s = 7;}

                        else if ( (LA47_2==DOT) ) {s = 8;}

                        else if ( (LA47_2==LEFT_SQUARE) ) {s = 9;}

                         
                        input.seek(index47_2);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA47_34 = input.LA(1);

                         
                        int index47_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 66;}

                         
                        input.seek(index47_34);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA47_4 = input.LA(1);

                         
                        int index47_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index47_4);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA47_32 = input.LA(1);

                         
                        int index47_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index47_32);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA47_6 = input.LA(1);

                         
                        int index47_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.EVAL)))||synpred6()||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.FORALL)))||(synpred6()&&(validateIdentifierKey(DroolsSoftKeywords.NOT))))) ) {s = 7;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index47_6);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA47_26 = input.LA(1);

                         
                        int index47_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6()) ) {s = 7;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index47_26);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 47, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA48_eotS =
        "\173\uffff";
    static final String DFA48_eofS =
        "\173\uffff";
    static final String DFA48_minS =
        "\3\126\2\0\1\126\1\0\1\uffff\1\126\1\156\2\uffff\1\127\1\126\1\uffff"+
        "\1\127\1\133\1\126\1\156\1\126\1\127\1\126\1\127\1\133\1\126\2\0"+
        "\1\126\1\156\2\126\2\0\1\126\1\4\11\126\1\0\2\uffff\1\127\1\133"+
        "\1\126\1\0\2\126\1\4\11\126\1\0\2\uffff\1\126\1\4\12\0\1\uffff\14"+
        "\0\1\uffff\17\0\1\uffff\17\0";
    static final String DFA48_maxS =
        "\2\133\1\155\2\0\1\133\1\0\1\uffff\1\126\1\156\2\uffff\1\155\1\126"+
        "\1\uffff\2\155\1\126\1\156\1\135\1\155\1\135\3\155\2\0\1\126\1\156"+
        "\1\135\1\155\2\0\1\126\1\173\1\126\7\154\1\126\1\0\2\uffff\3\155"+
        "\1\0\1\141\1\126\1\173\1\126\7\154\1\126\1\0\2\uffff\1\155\1\173"+
        "\12\0\1\uffff\14\0\1\uffff\17\0\1\uffff\17\0";
    static final String DFA48_acceptS =
        "\7\uffff\1\1\2\uffff\2\2\2\uffff\1\3\36\uffff\2\3\21\uffff\2\3\14"+
        "\uffff\1\3\14\uffff\1\3\17\uffff\1\3\17\uffff";
    static final String DFA48_specialS =
        "\2\uffff\1\5\1\2\1\1\1\uffff\1\7\22\uffff\1\0\1\6\4\uffff\1\3\1"+
        "\11\13\uffff\1\12\5\uffff\1\4\14\uffff\1\10\73\uffff}>";
    static final String[] DFA48_transitionS = {
            "\1\2\4\uffff\1\1",
            "\1\4\4\uffff\1\3",
            "\1\7\1\10\3\uffff\1\6\2\uffff\1\5\16\uffff\1\11",
            "\1\uffff",
            "\1\uffff",
            "\1\14\4\uffff\1\15",
            "\1\uffff",
            "",
            "\1\17",
            "\1\20",
            "",
            "",
            "\1\21\3\uffff\1\23\21\uffff\1\22",
            "\1\24",
            "",
            "\1\10\3\uffff\1\25\21\uffff\1\11",
            "\1\25\21\uffff\1\11",
            "\1\26",
            "\1\27",
            "\1\30\4\uffff\1\31\1\uffff\1\32",
            "\1\33\3\uffff\1\35\21\uffff\1\34",
            "\1\36\4\uffff\1\37\1\uffff\1\40",
            "\1\21\3\uffff\1\23\21\uffff\1\22",
            "\1\23\21\uffff\1\22",
            "\1\44\1\43\3\uffff\1\54\2\uffff\1\41\5\uffff\1\45\1\46\1\47"+
            "\1\50\1\51\1\52\1\53\2\uffff\1\42",
            "\1\uffff",
            "\1\uffff",
            "\1\57",
            "\1\60",
            "\1\61\4\uffff\1\62\1\uffff\1\63",
            "\1\67\1\66\3\uffff\1\77\2\uffff\1\64\5\uffff\1\70\1\71\1\72"+
            "\1\73\1\74\1\75\1\76\2\uffff\1\65",
            "\1\uffff",
            "\1\uffff",
            "\1\102",
            "\151\103\1\104\1\105\15\103",
            "\1\106",
            "\1\107\3\uffff\1\111\1\112\3\uffff\2\111\11\uffff\1\110\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\113\3\uffff\1\111\1\114\3\uffff\2\111\12\uffff\2\111",
            "\1\115",
            "\1\uffff",
            "",
            "",
            "\1\33\3\uffff\1\35\21\uffff\1\34",
            "\1\35\21\uffff\1\34",
            "\1\123\1\122\3\uffff\1\120\2\uffff\1\117\5\uffff\1\124\1\125"+
            "\1\126\1\127\1\130\1\131\1\132\2\uffff\1\121",
            "\1\uffff",
            "\1\134\6\uffff\1\136\3\uffff\1\135",
            "\1\137",
            "\151\140\1\141\1\142\15\140",
            "\1\143",
            "\1\144\3\uffff\1\145\1\146\3\uffff\2\145\11\uffff\1\147\2\145",
            "\1\150\3\uffff\1\145\1\151\3\uffff\2\145\12\uffff\2\145",
            "\1\150\3\uffff\1\145\1\151\3\uffff\2\145\12\uffff\2\145",
            "\1\150\3\uffff\1\145\1\151\3\uffff\2\145\12\uffff\2\145",
            "\1\150\3\uffff\1\145\1\151\3\uffff\2\145\12\uffff\2\145",
            "\1\150\3\uffff\1\145\1\151\3\uffff\2\145\12\uffff\2\145",
            "\1\150\3\uffff\1\145\1\151\3\uffff\2\145\12\uffff\2\145",
            "\1\152",
            "\1\uffff",
            "",
            "",
            "\1\156\1\155\3\uffff\1\166\1\172\1\32\3\uffff\1\171\1\170\1"+
            "\167\1\157\1\160\1\161\1\162\1\163\1\164\1\165\2\uffff\1\154",
            "\151\103\1\104\1\105\15\103",
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

    static final short[] DFA48_eot = DFA.unpackEncodedString(DFA48_eotS);
    static final short[] DFA48_eof = DFA.unpackEncodedString(DFA48_eofS);
    static final char[] DFA48_min = DFA.unpackEncodedStringToUnsignedChars(DFA48_minS);
    static final char[] DFA48_max = DFA.unpackEncodedStringToUnsignedChars(DFA48_maxS);
    static final short[] DFA48_accept = DFA.unpackEncodedString(DFA48_acceptS);
    static final short[] DFA48_special = DFA.unpackEncodedString(DFA48_specialS);
    static final short[][] DFA48_transition;

    static {
        int numStates = DFA48_transitionS.length;
        DFA48_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA48_transition[i] = DFA.unpackEncodedString(DFA48_transitionS[i]);
        }
    }

    class DFA48 extends DFA {

        public DFA48(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 48;
            this.eot = DFA48_eot;
            this.eof = DFA48_eof;
            this.min = DFA48_min;
            this.max = DFA48_max;
            this.accept = DFA48_accept;
            this.special = DFA48_special;
            this.transition = DFA48_transition;
        }
        public String getDescription() {
            return "521:3: ( ( LEFT_PAREN ( or_key | and_key ) )=> lhs_or | LEFT_PAREN lhs_or RIGHT_PAREN | lhs_pattern )";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA48_25 = input.LA(1);

                         
                        int index48_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 45;}

                         
                        input.seek(index48_25);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA48_4 = input.LA(1);

                         
                        int index48_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index48_4);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA48_3 = input.LA(1);

                         
                        int index48_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 10;}

                         
                        input.seek(index48_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA48_31 = input.LA(1);

                         
                        int index48_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 64;}

                         
                        input.seek(index48_31);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA48_50 = input.LA(1);

                         
                        int index48_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 91;}

                         
                        input.seek(index48_50);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA48_2 = input.LA(1);

                         
                        int index48_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA48_2==COLON) ) {s = 5;}

                        else if ( (LA48_2==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA48_2==ID) && (((synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {s = 7;}

                        else if ( (LA48_2==DOT) ) {s = 8;}

                        else if ( (LA48_2==LEFT_SQUARE) ) {s = 9;}

                         
                        input.seek(index48_2);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA48_26 = input.LA(1);

                         
                        int index48_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 46;}

                         
                        input.seek(index48_26);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA48_6 = input.LA(1);

                         
                        int index48_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred7()||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EVAL)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.EXISTS)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.NOT)))||(synpred7()&&(validateIdentifierKey(DroolsSoftKeywords.FORALL))))) ) {s = 7;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index48_6);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA48_63 = input.LA(1);

                         
                        int index48_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 107;}

                         
                        input.seek(index48_63);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA48_32 = input.LA(1);

                         
                        int index48_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 65;}

                         
                        input.seek(index48_32);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA48_44 = input.LA(1);

                         
                        int index48_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 7;}

                        else if ( (true) ) {s = 78;}

                         
                        input.seek(index48_44);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 48, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA53_eotS =
        "\13\uffff";
    static final String DFA53_eofS =
        "\13\uffff";
    static final String DFA53_minS =
        "\1\126\1\133\2\4\1\0\1\126\1\0\4\uffff";
    static final String DFA53_maxS =
        "\1\126\1\133\2\173\1\0\1\135\1\0\4\uffff";
    static final String DFA53_acceptS =
        "\7\uffff\1\1\2\2\1\1";
    static final String DFA53_specialS =
        "\4\uffff\1\1\1\0\1\2\4\uffff}>";
    static final String[] DFA53_transitionS = {
            "\1\1",
            "\1\2",
            "\127\3\1\4\1\3\1\5\36\3",
            "\127\3\1\6\1\3\1\5\36\3",
            "\1\uffff",
            "\1\12\5\uffff\1\12\1\11",
            "\1\uffff",
            "",
            "",
            "",
            ""
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
            return "555:3: ( accumulate_init_clause | accumulate_id_clause )";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA53_5 = input.LA(1);

                         
                        int index53_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA53_5==RIGHT_PAREN) ) {s = 9;}

                        else if ( (LA53_5==ID||LA53_5==COMMA) && ((validateIdentifierKey(DroolsSoftKeywords.INIT)))) {s = 10;}

                         
                        input.seek(index53_5);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA53_4 = input.LA(1);

                         
                        int index53_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.INIT))) ) {s = 7;}

                        else if ( (true) ) {s = 8;}

                         
                        input.seek(index53_4);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA53_6 = input.LA(1);

                         
                        int index53_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.INIT))) ) {s = 10;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index53_6);
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
    static final String DFA57_eotS =
        "\13\uffff";
    static final String DFA57_eofS =
        "\13\uffff";
    static final String DFA57_minS =
        "\1\126\1\133\2\4\1\0\1\126\1\0\4\uffff";
    static final String DFA57_maxS =
        "\1\126\1\133\2\173\1\0\1\135\1\0\4\uffff";
    static final String DFA57_acceptS =
        "\7\uffff\1\1\1\2\1\1\1\2";
    static final String DFA57_specialS =
        "\1\4\1\2\1\1\1\0\1\5\1\6\1\3\4\uffff}>";
    static final String[] DFA57_transitionS = {
            "\1\1",
            "\1\2",
            "\127\3\1\4\1\3\1\5\36\3",
            "\127\3\1\6\1\3\1\5\36\3",
            "\1\uffff",
            "\1\11\5\uffff\1\11\1\12",
            "\1\uffff",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA57_eot = DFA.unpackEncodedString(DFA57_eotS);
    static final short[] DFA57_eof = DFA.unpackEncodedString(DFA57_eofS);
    static final char[] DFA57_min = DFA.unpackEncodedStringToUnsignedChars(DFA57_minS);
    static final char[] DFA57_max = DFA.unpackEncodedStringToUnsignedChars(DFA57_maxS);
    static final short[] DFA57_accept = DFA.unpackEncodedString(DFA57_acceptS);
    static final short[] DFA57_special = DFA.unpackEncodedString(DFA57_specialS);
    static final short[][] DFA57_transition;

    static {
        int numStates = DFA57_transitionS.length;
        DFA57_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA57_transition[i] = DFA.unpackEncodedString(DFA57_transitionS[i]);
        }
    }

    class DFA57 extends DFA {

        public DFA57(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 57;
            this.eot = DFA57_eot;
            this.eof = DFA57_eof;
            this.min = DFA57_min;
            this.max = DFA57_max;
            this.accept = DFA57_accept;
            this.special = DFA57_special;
            this.transition = DFA57_transition;
        }
        public String getDescription() {
            return "566:2: ( reverse_key pc3= paren_chunk ( COMMA )? )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA57_3 = input.LA(1);

                         
                        int index57_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_3==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 5;}

                        else if ( ((LA57_3>=VT_COMPILATION_UNIT && LA57_3<=STRING)||LA57_3==COMMA||(LA57_3>=COLON && LA57_3<=MULTI_LINE_COMMENT)) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 3;}

                        else if ( (LA57_3==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 6;}

                         
                        input.seek(index57_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA57_2 = input.LA(1);

                         
                        int index57_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA57_2>=VT_COMPILATION_UNIT && LA57_2<=STRING)||LA57_2==COMMA||(LA57_2>=COLON && LA57_2<=MULTI_LINE_COMMENT)) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 3;}

                        else if ( (LA57_2==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 4;}

                        else if ( (LA57_2==RIGHT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 5;}

                         
                        input.seek(index57_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA57_1 = input.LA(1);

                         
                        int index57_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_1==LEFT_PAREN) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 2;}

                         
                        input.seek(index57_1);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA57_6 = input.LA(1);

                         
                        int index57_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.REVERSE))) ) {s = 9;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RESULT))) ) {s = 10;}

                         
                        input.seek(index57_6);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA57_0 = input.LA(1);

                         
                        int index57_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_0==ID) && (((validateIdentifierKey(DroolsSoftKeywords.REVERSE))||(validateIdentifierKey(DroolsSoftKeywords.RESULT))))) {s = 1;}

                         
                        input.seek(index57_0);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA57_4 = input.LA(1);

                         
                        int index57_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((validateIdentifierKey(DroolsSoftKeywords.REVERSE))) ) {s = 7;}

                        else if ( ((validateIdentifierKey(DroolsSoftKeywords.RESULT))) ) {s = 8;}

                         
                        input.seek(index57_4);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA57_5 = input.LA(1);

                         
                        int index57_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_5==ID||LA57_5==COMMA) && ((validateIdentifierKey(DroolsSoftKeywords.REVERSE)))) {s = 9;}

                        else if ( (LA57_5==RIGHT_PAREN) && ((validateIdentifierKey(DroolsSoftKeywords.RESULT)))) {s = 10;}

                         
                        input.seek(index57_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 57, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA74_eotS =
        "\45\uffff";
    static final String DFA74_eofS =
        "\45\uffff";
    static final String DFA74_minS =
        "\1\134\1\uffff\2\126\7\uffff\1\0\1\126\1\uffff\1\0\1\126\2\0\1\4"+
        "\6\uffff\1\155\1\4\2\0\1\4\7\0";
    static final String DFA74_maxS =
        "\1\142\1\uffff\1\152\1\155\7\uffff\1\0\1\155\1\uffff\1\0\1\126\2"+
        "\0\1\173\6\uffff\1\155\1\173\2\0\1\173\7\0";
    static final String DFA74_acceptS =
        "\1\uffff\1\2\2\uffff\7\1\2\uffff\1\1\5\uffff\6\1\14\uffff";
    static final String DFA74_specialS =
        "\2\uffff\1\7\1\6\7\uffff\1\1\1\2\1\uffff\1\3\1\uffff\1\0\1\4\11"+
        "\uffff\1\10\1\5\10\uffff}>";
    static final String[] DFA74_transitionS = {
            "\2\1\3\uffff\1\2\1\1",
            "",
            "\1\3\4\uffff\1\13\10\uffff\1\4\1\5\1\6\1\7\1\10\1\11\1\12",
            "\1\14\1\1\2\uffff\1\15\1\16\2\uffff\1\1\2\15\3\uffff\6\1\1\17"+
            "\2\15\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\20\1\24\2\uffff\1\21\1\22\1\27\1\30\1\uffff\2\21\1\26\1\25"+
            "\7\uffff\1\1\2\21\1\23",
            "",
            "\1\uffff",
            "\1\31",
            "\1\uffff",
            "\1\uffff",
            "\122\36\1\32\3\36\1\35\1\33\1\36\1\34\1\36\2\35\12\36\2\35\17"+
            "\36",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\37",
            "\123\36\1\41\3\36\1\44\1\42\1\43\17\36\1\40\16\36",
            "\1\uffff",
            "\1\uffff",
            "\127\36\1\44\1\42\1\43\36\36",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
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
            return "()* loopback of 675:25: ( ( DOUBLE_PIPE )=> DOUBLE_PIPE and_restr_connective )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA74_16 = input.LA(1);

                         
                        int index74_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11()) ) {s = 24;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index74_16);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA74_11 = input.LA(1);

                         
                        int index74_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11()) ) {s = 13;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index74_11);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA74_12 = input.LA(1);

                         
                        int index74_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA74_12==ID) ) {s = 16;}

                        else if ( (LA74_12==STRING||(LA74_12>=BOOL && LA74_12<=INT)||(LA74_12>=FLOAT && LA74_12<=NULL)) ) {s = 17;}

                        else if ( (LA74_12==LEFT_PAREN) ) {s = 18;}

                        else if ( (LA74_12==LEFT_SQUARE) && (synpred11())) {s = 19;}

                        else if ( (LA74_12==DOT) && (synpred11())) {s = 20;}

                        else if ( (LA74_12==DOUBLE_AMPER) && (synpred11())) {s = 21;}

                        else if ( (LA74_12==DOUBLE_PIPE) && (synpred11())) {s = 22;}

                        else if ( (LA74_12==COMMA) && (synpred11())) {s = 23;}

                        else if ( (LA74_12==RIGHT_PAREN) && (synpred11())) {s = 24;}

                        else if ( (LA74_12==GRAVE_ACCENT) ) {s = 1;}

                         
                        input.seek(index74_12);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA74_14 = input.LA(1);

                         
                        int index74_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11()) ) {s = 24;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index74_14);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA74_17 = input.LA(1);

                         
                        int index74_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11()) ) {s = 24;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index74_17);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA74_28 = input.LA(1);

                         
                        int index74_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11()) ) {s = 24;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index74_28);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA74_3 = input.LA(1);

                         
                        int index74_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA74_3==DOT||LA74_3==COLON||(LA74_3>=EQUAL && LA74_3<=NOT_EQUAL)||LA74_3==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA74_3==ID) ) {s = 12;}

                        else if ( (LA74_3==STRING||(LA74_3>=BOOL && LA74_3<=INT)||(LA74_3>=FLOAT && LA74_3<=NULL)) && (synpred11())) {s = 13;}

                        else if ( (LA74_3==LEFT_PAREN) ) {s = 14;}

                        else if ( (LA74_3==GRAVE_ACCENT) ) {s = 15;}

                         
                        input.seek(index74_3);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA74_2 = input.LA(1);

                         
                        int index74_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA74_2==ID) ) {s = 3;}

                        else if ( (LA74_2==EQUAL) && (synpred11())) {s = 4;}

                        else if ( (LA74_2==GREATER) && (synpred11())) {s = 5;}

                        else if ( (LA74_2==GREATER_EQUAL) && (synpred11())) {s = 6;}

                        else if ( (LA74_2==LESS) && (synpred11())) {s = 7;}

                        else if ( (LA74_2==LESS_EQUAL) && (synpred11())) {s = 8;}

                        else if ( (LA74_2==NOT_EQUAL) && (synpred11())) {s = 9;}

                        else if ( (LA74_2==GRAVE_ACCENT) && (synpred11())) {s = 10;}

                        else if ( (LA74_2==LEFT_PAREN) ) {s = 11;}

                         
                        input.seek(index74_2);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA74_27 = input.LA(1);

                         
                        int index74_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11()) ) {s = 24;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index74_27);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 74, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA75_eotS =
        "\102\uffff";
    static final String DFA75_eofS =
        "\102\uffff";
    static final String DFA75_minS =
        "\1\134\1\uffff\2\126\7\uffff\2\126\1\uffff\1\4\2\126\1\0\7\uffff"+
        "\2\0\1\4\6\uffff\1\4\2\0\2\4\13\0\1\uffff\17\0";
    static final String DFA75_maxS =
        "\1\142\1\uffff\1\152\1\155\7\uffff\1\152\1\155\1\uffff\1\173\1\126"+
        "\1\155\1\0\7\uffff\2\0\1\173\6\uffff\1\173\2\0\2\173\13\0\1\uffff"+
        "\17\0";
    static final String DFA75_acceptS =
        "\1\uffff\1\2\2\uffff\7\1\2\uffff\1\1\4\uffff\7\1\3\uffff\6\1\20"+
        "\uffff\1\1\17\uffff";
    static final String DFA75_specialS =
        "\2\uffff\1\0\1\5\7\uffff\1\10\1\11\3\uffff\1\4\1\1\7\uffff\1\7\1"+
        "\2\10\uffff\1\6\1\3\35\uffff}>";
    static final String[] DFA75_transitionS = {
            "\2\1\3\uffff\1\1\1\2",
            "",
            "\1\3\4\uffff\1\13\10\uffff\1\4\1\5\1\6\1\7\1\10\1\11\1\12",
            "\1\14\1\1\2\uffff\1\15\1\16\2\uffff\1\1\2\15\3\uffff\6\1\1\17"+
            "\2\15\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\20\4\uffff\1\21\10\uffff\1\22\1\23\1\24\1\25\1\26\1\27\1"+
            "\30",
            "\1\31\1\35\2\uffff\1\32\1\33\1\40\1\41\1\uffff\2\32\1\36\1\37"+
            "\7\uffff\1\1\2\32\1\34",
            "",
            "\122\55\1\42\3\55\1\54\1\43\1\55\1\44\1\55\2\54\3\55\1\45\1"+
            "\46\1\47\1\50\1\51\1\52\1\53\2\54\17\55",
            "\1\56",
            "\1\57\1\1\2\uffff\1\62\1\61\2\uffff\1\1\2\62\3\uffff\6\1\1\60"+
            "\2\62\1\1",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\122\67\1\63\3\67\1\66\1\64\1\67\1\65\1\67\2\66\12\67\2\66\17"+
            "\67",
            "",
            "",
            "",
            "",
            "",
            "",
            "\122\55\1\70\1\74\2\55\1\71\1\72\1\75\1\76\1\55\2\71\11\55\1"+
            "\77\2\71\1\73\16\55",
            "\1\uffff",
            "\1\uffff",
            "\122\55\1\100\3\55\1\71\1\101\1\55\1\44\1\55\2\71\12\55\2\71"+
            "\17\55",
            "\122\55\1\100\3\55\1\71\1\101\1\55\1\44\1\55\2\71\12\55\2\71"+
            "\17\55",
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
            return "()* loopback of 679:26: ( ( DOUBLE_AMPER )=> DOUBLE_AMPER constraint_expression )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA75_2 = input.LA(1);

                         
                        int index75_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA75_2==ID) ) {s = 3;}

                        else if ( (LA75_2==EQUAL) && (synpred12())) {s = 4;}

                        else if ( (LA75_2==GREATER) && (synpred12())) {s = 5;}

                        else if ( (LA75_2==GREATER_EQUAL) && (synpred12())) {s = 6;}

                        else if ( (LA75_2==LESS) && (synpred12())) {s = 7;}

                        else if ( (LA75_2==LESS_EQUAL) && (synpred12())) {s = 8;}

                        else if ( (LA75_2==NOT_EQUAL) && (synpred12())) {s = 9;}

                        else if ( (LA75_2==GRAVE_ACCENT) && (synpred12())) {s = 10;}

                        else if ( (LA75_2==LEFT_PAREN) ) {s = 11;}

                         
                        input.seek(index75_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA75_17 = input.LA(1);

                         
                        int index75_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12()) ) {s = 50;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index75_17);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA75_26 = input.LA(1);

                         
                        int index75_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12()) ) {s = 50;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index75_26);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA75_36 = input.LA(1);

                         
                        int index75_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12()) ) {s = 50;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index75_36);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA75_16 = input.LA(1);

                         
                        int index75_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA75_16==DOT||LA75_16==COLON||(LA75_16>=EQUAL && LA75_16<=NOT_EQUAL)||LA75_16==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA75_16==ID) ) {s = 47;}

                        else if ( (LA75_16==GRAVE_ACCENT) ) {s = 48;}

                        else if ( (LA75_16==LEFT_PAREN) ) {s = 49;}

                        else if ( (LA75_16==STRING||(LA75_16>=BOOL && LA75_16<=INT)||(LA75_16>=FLOAT && LA75_16<=NULL)) && (synpred12())) {s = 50;}

                         
                        input.seek(index75_16);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA75_3 = input.LA(1);

                         
                        int index75_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA75_3==DOT||LA75_3==COLON||(LA75_3>=EQUAL && LA75_3<=NOT_EQUAL)||LA75_3==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA75_3==ID) ) {s = 12;}

                        else if ( (LA75_3==STRING||(LA75_3>=BOOL && LA75_3<=INT)||(LA75_3>=FLOAT && LA75_3<=NULL)) && (synpred12())) {s = 13;}

                        else if ( (LA75_3==LEFT_PAREN) ) {s = 14;}

                        else if ( (LA75_3==GRAVE_ACCENT) ) {s = 15;}

                         
                        input.seek(index75_3);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA75_35 = input.LA(1);

                         
                        int index75_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12()) ) {s = 50;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index75_35);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA75_25 = input.LA(1);

                         
                        int index75_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12()) ) {s = 50;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index75_25);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA75_11 = input.LA(1);

                         
                        int index75_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA75_11==ID) ) {s = 16;}

                        else if ( (LA75_11==LEFT_PAREN) ) {s = 17;}

                        else if ( (LA75_11==EQUAL) && (synpred12())) {s = 18;}

                        else if ( (LA75_11==GREATER) && (synpred12())) {s = 19;}

                        else if ( (LA75_11==GREATER_EQUAL) && (synpred12())) {s = 20;}

                        else if ( (LA75_11==LESS) && (synpred12())) {s = 21;}

                        else if ( (LA75_11==LESS_EQUAL) && (synpred12())) {s = 22;}

                        else if ( (LA75_11==NOT_EQUAL) && (synpred12())) {s = 23;}

                        else if ( (LA75_11==GRAVE_ACCENT) && (synpred12())) {s = 24;}

                         
                        input.seek(index75_11);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA75_12 = input.LA(1);

                         
                        int index75_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA75_12==ID) ) {s = 25;}

                        else if ( (LA75_12==STRING||(LA75_12>=BOOL && LA75_12<=INT)||(LA75_12>=FLOAT && LA75_12<=NULL)) ) {s = 26;}

                        else if ( (LA75_12==LEFT_PAREN) ) {s = 27;}

                        else if ( (LA75_12==GRAVE_ACCENT) ) {s = 1;}

                        else if ( (LA75_12==LEFT_SQUARE) && (synpred12())) {s = 28;}

                        else if ( (LA75_12==DOT) && (synpred12())) {s = 29;}

                        else if ( (LA75_12==DOUBLE_PIPE) && (synpred12())) {s = 30;}

                        else if ( (LA75_12==DOUBLE_AMPER) && (synpred12())) {s = 31;}

                        else if ( (LA75_12==COMMA) && (synpred12())) {s = 32;}

                        else if ( (LA75_12==RIGHT_PAREN) && (synpred12())) {s = 33;}

                         
                        input.seek(index75_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 75, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_package_statement_in_compilation_unit400 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_statement_in_compilation_unit405 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_EOF_in_compilation_unit410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_package_key_in_package_statement461 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_package_id_in_package_statement463 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_SEMICOLON_in_package_statement465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_id489 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_DOT_in_package_id495 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_package_id499 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_rule_attribute_in_statement527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_import_statement589 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_import_name_in_import_statement591 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_SEMICOLON_in_import_statement594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_key_in_function_import_statement629 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_function_key_in_function_import_statement631 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement633 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_SEMICOLON_in_function_import_statement636 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name665 = new BitSet(new long[]{0x0000000000000002L,0x0000000001800000L});
    public static final BitSet FOLLOW_DOT_in_import_name671 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_import_name675 = new BitSet(new long[]{0x0000000000000002L,0x0000000001800000L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_key_in_global722 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_data_type_in_global724 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_global_id_in_global726 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_SEMICOLON_in_global728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_id754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_key_in_function786 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_data_type_in_function788 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_function_id_in_function791 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_parameters_in_function793 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_id825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_key_in_query857 = new BitSet(new long[]{0x0000000000000000L,0x0000000004400000L});
    public static final BitSet FOLLOW_query_id_in_query859 = new BitSet(new long[]{0x0000000000000000L,0x000000000A400000L});
    public static final BitSet FOLLOW_parameters_in_query861 = new BitSet(new long[]{0x0000000000000000L,0x000000000A400000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query864 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_END_in_query866 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_SEMICOLON_in_query868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_id900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_query_id916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parameters935 = new BitSet(new long[]{0x0000000000000000L,0x0000000020400000L});
    public static final BitSet FOLLOW_param_definition_in_parameters942 = new BitSet(new long[]{0x0000000000000000L,0x0000000030000000L});
    public static final BitSet FOLLOW_COMMA_in_parameters945 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_param_definition_in_parameters947 = new BitSet(new long[]{0x0000000000000000L,0x0000000030000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parameters956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_param_definition980 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_argument_in_param_definition983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument994 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_dimension_definition_in_argument996 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_template_key_in_template1020 = new BitSet(new long[]{0x0000000000000000L,0x0000000004400000L});
    public static final BitSet FOLLOW_template_id_in_template1022 = new BitSet(new long[]{0x0000000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1024 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_template_slot_in_template1029 = new BitSet(new long[]{0x0000000000000000L,0x0000000002400000L});
    public static final BitSet FOLLOW_END_in_template1034 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template1036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_id1066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_template_id1082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_type_in_template_slot1102 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_slot_id_in_template_slot1104 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_SEMICOLON_in_template_slot1106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_slot_id1131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_key_in_rule1160 = new BitSet(new long[]{0x0000000000000000L,0x0000000004400000L});
    public static final BitSet FOLLOW_rule_id_in_rule1162 = new BitSet(new long[]{0x0000000000000000L,0x0000800000400000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule1164 = new BitSet(new long[]{0x0000000000000000L,0x0000800000400000L});
    public static final BitSet FOLLOW_when_part_in_rule1167 = new BitSet(new long[]{0x0000000000000000L,0x0000800000000000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_when_key_in_when_part1199 = new BitSet(new long[]{0x0000000000000002L,0x0000000048400000L});
    public static final BitSet FOLLOW_COLON_in_when_part1201 = new BitSet(new long[]{0x0000000000000002L,0x0000000008400000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_when_part1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_id1225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_rule_id1241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attributes_key_in_rule_attributes1262 = new BitSet(new long[]{0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_rule_attributes1264 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1269 = new BitSet(new long[]{0x0000000000000002L,0x0000000010400000L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1273 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1278 = new BitSet(new long[]{0x0000000000000002L,0x0000000010400000L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_key_in_date_effective1397 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_key_in_date_expires1411 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_key_in_enabled1428 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_salience_key_in_salience1443 = new BitSet(new long[]{0x0000000000000000L,0x0000000108000000L});
    public static final BitSet FOLLOW_INT_in_salience1450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_key_in_no_loop1475 = new BitSet(new long[]{0x0000000000000002L,0x0000000080000000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_key_in_auto_focus1490 = new BitSet(new long[]{0x0000000000000002L,0x0000000080000000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_key_in_activation_group1507 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_key_in_ruleflow_group1521 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_key_in_agenda_group1535 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_key_in_duration1549 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_INT_in_duration1552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_key_in_dialect1566 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_STRING_in_dialect1569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_key_in_lock_on_active1587 = new BitSet(new long[]{0x0000000000000002L,0x0000000080000000L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1602 = new BitSet(new long[]{0x0000000000000002L,0x0000000008400000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or1644 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or1648 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1650 = new BitSet(new long[]{0x0000000000000000L,0x0000000028400000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or1653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1674 = new BitSet(new long[]{0x0000000000000002L,0x0000000200400000L});
    public static final BitSet FOLLOW_or_key_in_lhs_or1696 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_lhs_or1703 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1708 = new BitSet(new long[]{0x0000000000000002L,0x0000000200400000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and1746 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and1750 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1752 = new BitSet(new long[]{0x0000000000000000L,0x0000000028400000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and1755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1776 = new BitSet(new long[]{0x0000000000000002L,0x0000000400400000L});
    public static final BitSet FOLLOW_and_key_in_lhs_and1798 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_lhs_and1805 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1810 = new BitSet(new long[]{0x0000000000000002L,0x0000000400400000L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1848 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1854 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary1860 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary1866 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary1872 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary1875 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary1877 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_unary1883 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_SEMICOLON_in_lhs_unary1897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_key_in_lhs_exist1911 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist1935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist1942 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist1944 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist1946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist1959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_lhs_not2005 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2029 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2031 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_key_in_lhs_eval2080 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forall_key_in_lhs_forall2108 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2110 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2112 = new BitSet(new long[]{0x0000000000000000L,0x0000000020400000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall2115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_pattern_source2147 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
    public static final BitSet FOLLOW_from_key_in_pattern_source2156 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_accumulate_statement_in_pattern_source2172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_pattern_source2188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entrypoint_statement_in_pattern_source2205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_pattern_source2221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_key_in_accumulate_statement2249 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2253 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_lhs_or_in_accumulate_statement2255 = new BitSet(new long[]{0x0000000000000000L,0x0000000010400000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2257 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_statement2265 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_statement2271 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_init_key_in_accumulate_init_clause2308 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2313 = new BitSet(new long[]{0x0000000000000000L,0x0000000010400000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2315 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_action_key_in_accumulate_init_clause2319 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2323 = new BitSet(new long[]{0x0000000000000000L,0x0000000010400000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2325 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_reverse_key_in_accumulate_init_clause2331 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2335 = new BitSet(new long[]{0x0000000000000000L,0x0000000010400000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_init_clause2337 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_result_key_in_accumulate_init_clause2343 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_init_clause2347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause2396 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_id_clause2400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_key_in_collect_statement2422 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement2426 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_pattern_source_in_collect_statement2428 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement2430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entry_point_key_in_entrypoint_statement2452 = new BitSet(new long[]{0x0000000000000000L,0x0000000004400000L});
    public static final BitSet FOLLOW_entrypoint_id_in_entrypoint_statement2454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entrypoint_id2477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_entrypoint_id2490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source2506 = new BitSet(new long[]{0x0000000000000002L,0x0000000008800000L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source2519 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_expression_chain_in_from_source2526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain2558 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_expression_chain2560 = new BitSet(new long[]{0x0000000000000002L,0x0000200008800000L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain2580 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain2602 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain2613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern2646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern2659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_fact_binding2679 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_fact_in_fact_binding2685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding2692 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_fact_binding_expression_in_fact_binding2694 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding2696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression2735 = new BitSet(new long[]{0x0000000000000002L,0x0000000200400000L});
    public static final BitSet FOLLOW_or_key_in_fact_binding_expression2747 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_binding_expression2753 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_fact_in_fact_binding_expression2758 = new BitSet(new long[]{0x0000000000000002L,0x0000000200400000L});
    public static final BitSet FOLLOW_pattern_type_in_fact2798 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact2800 = new BitSet(new long[]{0x0000000000000000L,0x0000000028400000L});
    public static final BitSet FOLLOW_constraints_in_fact2802 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact2805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints2830 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_COMMA_in_constraints2834 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_constraint_in_constraints2837 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_or_constr_in_constraint2851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr2862 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr2866 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_and_constr_in_or_constr2869 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr2884 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr2888 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr2891 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_eval_key_in_unary_constr2912 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_paren_chunk_in_unary_constr2915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr2920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr2925 = new BitSet(new long[]{0x0000000000000000L,0x0000000008400000L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr2928 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr2930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_field_constraint2944 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint2946 = new BitSet(new long[]{0x0000000000000002L,0x000007F808400000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint2950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_field_constraint2956 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_paren_chunk_in_field_constraint2958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3012 = new BitSet(new long[]{0x0000000000000000L,0x000007F008400000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_label3038 = new BitSet(new long[]{0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_label3040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3056 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective3064 = new BitSet(new long[]{0x0000000000000000L,0x000007F008400000L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3067 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3082 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective3090 = new BitSet(new long[]{0x0000000000000000L,0x000007F008400000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3093 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression3115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression3120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression3125 = new BitSet(new long[]{0x0000000000000000L,0x000007F008400000L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression3128 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression3130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUAL_in_simple_operator3142 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_GREATER_in_simple_operator3148 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_simple_operator3154 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_LESS_in_simple_operator3160 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_simple_operator3166 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_simple_operator3172 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_not_key_in_simple_operator3178 = new BitSet(new long[]{0x0000000000000000L,0x0000040000400000L});
    public static final BitSet FOLLOW_contains_key_in_simple_operator3181 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_soundslike_key_in_simple_operator3184 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_matches_key_in_simple_operator3187 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_memberof_key_in_simple_operator3190 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3194 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_GRAVE_ACCENT_in_simple_operator3199 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3202 = new BitSet(new long[]{0x0000000000000000L,0x0000200000000000L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator3205 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_contains_key_in_simple_operator3211 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_excludes_key_in_simple_operator3217 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_matches_key_in_simple_operator3223 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_soundslike_key_in_simple_operator3229 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_memberof_key_in_simple_operator3235 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3241 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_GRAVE_ACCENT_in_simple_operator3247 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_simple_operator3250 = new BitSet(new long[]{0x0000000000000000L,0x0000200000000000L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator3253 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator3257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_compound_operator3272 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_not_key_in_compound_operator3277 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_in_key_in_compound_operator3279 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator3284 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3287 = new BitSet(new long[]{0x0000000000000000L,0x0000000030000000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator3291 = new BitSet(new long[]{0x0000000000000000L,0x000018018C400000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3294 = new BitSet(new long[]{0x0000000000000000L,0x0000000030000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator3299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_expression_value3310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value3315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_value3321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_literal_constraint0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pattern_type3365 = new BitSet(new long[]{0x0000000000000002L,0x0000200000800000L});
    public static final BitSet FOLLOW_DOT_in_pattern_type3371 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_pattern_type3375 = new BitSet(new long[]{0x0000000000000002L,0x0000200000800000L});
    public static final BitSet FOLLOW_dimension_definition_in_pattern_type3390 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_ID_in_data_type3416 = new BitSet(new long[]{0x0000000000000002L,0x0000200000800000L});
    public static final BitSet FOLLOW_DOT_in_data_type3420 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_data_type3422 = new BitSet(new long[]{0x0000000000000002L,0x0000200000800000L});
    public static final BitSet FOLLOW_dimension_definition_in_data_type3427 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dimension_definition3453 = new BitSet(new long[]{0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dimension_definition3455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path3466 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_DOT_in_accessor_path3470 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path3472 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_accessor_element3496 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_square_chunk_in_accessor_element3498 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_rhs_chunk_data_in_rhs_chunk3527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk_data3546 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk_data3550 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk_data3556 = new BitSet(new long[]{0x0000000000000002L,0x0000000000200000L});
    public static final BitSet FOLLOW_SEMICOLON_in_rhs_chunk_data3558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk3575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk_data3594 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk_data3597 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_curly_chunk_data_in_curly_chunk_data3611 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk_data3616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk3632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk_data3652 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk_data3655 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_paren_chunk_data_in_paren_chunk_data3669 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk_data3674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk3691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk_data3710 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk_data3713 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_square_chunk_data_in_square_chunk_data3727 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0FFFFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk_data3732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_effective_key3751 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_effective_key3753 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_date_effective_key3755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_date_expires_key3781 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_MISC_in_date_expires_key3783 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_date_expires_key3785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key3811 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key3813 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key3815 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_MISC_in_lock_on_active_key3817 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_lock_on_active_key3819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_no_loop_key3845 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_MISC_in_no_loop_key3847 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_no_loop_key3849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key3875 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_MISC_in_auto_focus_key3877 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_auto_focus_key3879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_activation_group_key3905 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_MISC_in_activation_group_key3907 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_activation_group_key3909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key3935 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_MISC_in_agenda_group_key3937 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_agenda_group_key3939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key3965 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_MISC_in_ruleflow_group_key3967 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_ruleflow_group_key3969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_duration_key3994 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_package_key4016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_key4038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dialect_key4060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_salience_key4082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enabled_key4104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_attributes_key4126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_when_key4148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_key4170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_template_key4192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_query_key4214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_declare_key4236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_key4258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_global_key4280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_eval_key4302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_contains_key4324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_matches_key4346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_excludes_key4368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_soundslike_key4390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_memberof_key4412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key4434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key4456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_or_key4478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_and_key4500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_exists_key4522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_forall_key4544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_key4566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entry_point_key4589 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_MISC_in_entry_point_key4591 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_entry_point_key4593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_accumulate_key4618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_init_key4640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_action_key4662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_reverse_key4684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_result_key4706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_collect_key4728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred11638 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_or_key_in_synpred11640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_key_in_synpred21687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred21689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred31740 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_and_key_in_synpred31742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred41789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred41791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_synpred51893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred61925 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_or_key_in_synpred61928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred61930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred72012 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_or_key_in_synpred72015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_key_in_synpred72017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred82513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred92574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred102596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred113060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred123086 = new BitSet(new long[]{0x0000000000000002L});

}