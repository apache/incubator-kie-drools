// $ANTLR 3.1.1 src/main/resources/org/drools/lang/dsl/DSLMap.g 2009-05-01 12:52:50

	package org.drools.lang.dsl;
	import java.util.List;
	import java.util.ArrayList;
//	import org.drools.lang.dsl.DSLMappingParseException;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class DSLMapParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_DSL_GRAMMAR", "VT_COMMENT", "VT_ENTRY", "VT_SCOPE", "VT_CONDITION", "VT_CONSEQUENCE", "VT_KEYWORD", "VT_ANY", "VT_META", "VT_ENTRY_KEY", "VT_ENTRY_VAL", "VT_VAR_DEF", "VT_VAR_REF", "VT_LITERAL", "VT_PATTERN", "VT_QUAL", "VT_SPACE", "EOL", "LINE_COMMENT", "EQUALS", "LEFT_SQUARE", "RIGHT_SQUARE", "LITERAL", "COMMA", "COLON", "LEFT_CURLY", "RIGHT_CURLY", "WS", "EscapeSequence", "DOT", "POUND", "IdentifierPart", "MISC"
    };
    public static final int COMMA=27;
    public static final int IdentifierPart=35;
    public static final int RIGHT_CURLY=30;
    public static final int VT_ENTRY_VAL=14;
    public static final int WS=31;
    public static final int MISC=36;
    public static final int VT_META=12;
    public static final int VT_CONSEQUENCE=9;
    public static final int VT_SPACE=20;
    public static final int LINE_COMMENT=22;
    public static final int VT_ANY=11;
    public static final int VT_LITERAL=17;
    public static final int DOT=33;
    public static final int EQUALS=23;
    public static final int VT_DSL_GRAMMAR=4;
    public static final int VT_CONDITION=8;
    public static final int VT_ENTRY=6;
    public static final int VT_VAR_DEF=15;
    public static final int LITERAL=26;
    public static final int VT_PATTERN=18;
    public static final int EscapeSequence=32;
    public static final int VT_COMMENT=5;
    public static final int EOF=-1;
    public static final int EOL=21;
    public static final int LEFT_SQUARE=24;
    public static final int VT_ENTRY_KEY=13;
    public static final int COLON=28;
    public static final int VT_SCOPE=7;
    public static final int VT_KEYWORD=10;
    public static final int POUND=34;
    public static final int LEFT_CURLY=29;
    public static final int VT_VAR_REF=16;
    public static final int VT_QUAL=19;
    public static final int RIGHT_SQUARE=25;

    // delegates
    // delegators


        public DSLMapParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DSLMapParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return DSLMapParser.tokenNames; }
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/dsl/DSLMap.g"; }


    //we may not need the check on [], as the LITERAL token being examined 
    //should not have them.
    	
    	private List errorList = new ArrayList();
    	public List getErrorList(){
    		return errorList;
    	}

    	private boolean validateLT(int LTNumber, String text){
    		if (null == input) return false;
    		if (null == input.LT(LTNumber)) return false;
    		if (null == input.LT(LTNumber).getText()) return false;
    		
    		String text2Validate = input.LT(LTNumber).getText();
    		if (text2Validate.startsWith("[") && text2Validate.endsWith("]")){
    			text2Validate = text2Validate.substring(1, text2Validate.length() - 1); 
    		}

    		return text2Validate.equalsIgnoreCase(text);
    	}

    	private boolean validateIdentifierKey(String text){
    		return validateLT(1, text);
    	}
    	
    	//public void reportError(RecognitionException re) {
    		// if we've already reported an error and have not matched a token
    		// yet successfully, don't report any errors.
    	//	if (errorRecovery) {
    	//		return;
    	//	}
    	//	errorRecovery = true;
    	//
    	//	String error = "Error parsing mapping entry: " + getErrorMessage(re, tokenNames);
    	//	DSLMappingParseException exception = new DSLMappingParseException (error, re.line);
    	//	errorList.add(exception);
    	//}
    	


    public static class mapping_file_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mapping_file"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:86:1: mapping_file : ( statement )* -> ^( VT_DSL_GRAMMAR ( statement )* ) ;
    public final DSLMapParser.mapping_file_return mapping_file() throws RecognitionException {
        DSLMapParser.mapping_file_return retval = new DSLMapParser.mapping_file_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.statement_return statement1 = null;


        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:87:2: ( ( statement )* -> ^( VT_DSL_GRAMMAR ( statement )* ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:87:4: ( statement )*
            {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:87:4: ( statement )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=EOL && LA1_0<=LINE_COMMENT)||LA1_0==LEFT_SQUARE) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_mapping_file273);
            	    statement1=statement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_statement.add(statement1.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);



            // AST REWRITE
            // elements: statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 88:2: -> ^( VT_DSL_GRAMMAR ( statement )* )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:88:5: ^( VT_DSL_GRAMMAR ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_DSL_GRAMMAR, "VT_DSL_GRAMMAR"), root_1);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:88:22: ( statement )*
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
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mapping_file"

    public static class statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "statement"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:91:1: statement : ( entry | comment | EOL );
    public final DSLMapParser.statement_return statement() throws RecognitionException {
        DSLMapParser.statement_return retval = new DSLMapParser.statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOL4=null;
        DSLMapParser.entry_return entry2 = null;

        DSLMapParser.comment_return comment3 = null;


        Object EOL4_tree=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:92:2: ( entry | comment | EOL )
            int alt2=3;
            switch ( input.LA(1) ) {
            case LEFT_SQUARE:
                {
                alt2=1;
                }
                break;
            case LINE_COMMENT:
                {
                alt2=2;
                }
                break;
            case EOL:
                {
                alt2=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:92:4: entry
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_entry_in_statement296);
                    entry2=entry();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, entry2.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:93:4: comment
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_comment_in_statement303);
                    comment3=comment();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, comment3.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:94:4: EOL
                    {
                    root_0 = (Object)adaptor.nil();

                    EOL4=(Token)match(input,EOL,FOLLOW_EOL_in_statement309); if (state.failed) return retval;

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

    public static class comment_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "comment"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:99:1: comment : LINE_COMMENT -> ^( VT_COMMENT[$LINE_COMMENT, \"COMMENT\"] LINE_COMMENT ) ;
    public final DSLMapParser.comment_return comment() throws RecognitionException {
        DSLMapParser.comment_return retval = new DSLMapParser.comment_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LINE_COMMENT5=null;

        Object LINE_COMMENT5_tree=null;
        RewriteRuleTokenStream stream_LINE_COMMENT=new RewriteRuleTokenStream(adaptor,"token LINE_COMMENT");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:99:9: ( LINE_COMMENT -> ^( VT_COMMENT[$LINE_COMMENT, \"COMMENT\"] LINE_COMMENT ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:99:11: LINE_COMMENT
            {
            LINE_COMMENT5=(Token)match(input,LINE_COMMENT,FOLLOW_LINE_COMMENT_in_comment325); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LINE_COMMENT.add(LINE_COMMENT5);



            // AST REWRITE
            // elements: LINE_COMMENT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 100:2: -> ^( VT_COMMENT[$LINE_COMMENT, \"COMMENT\"] LINE_COMMENT )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:100:5: ^( VT_COMMENT[$LINE_COMMENT, \"COMMENT\"] LINE_COMMENT )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_COMMENT, LINE_COMMENT5, "COMMENT"), root_1);

                adaptor.addChild(root_1, stream_LINE_COMMENT.nextNode());

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
    // $ANTLR end "comment"

    public static class entry_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entry"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:104:1: entry : scope_section ( meta_section )? key_section EQUALS value_section ( EOL | EOF ) -> ^( VT_ENTRY scope_section ( meta_section )? key_section value_section ) ;
    public final DSLMapParser.entry_return entry() throws RecognitionException {
        DSLMapParser.entry_return retval = new DSLMapParser.entry_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS9=null;
        Token EOL11=null;
        Token EOF12=null;
        DSLMapParser.scope_section_return scope_section6 = null;

        DSLMapParser.meta_section_return meta_section7 = null;

        DSLMapParser.key_section_return key_section8 = null;

        DSLMapParser.value_section_return value_section10 = null;


        Object EQUALS9_tree=null;
        Object EOL11_tree=null;
        Object EOF12_tree=null;
        RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleTokenStream stream_EOL=new RewriteRuleTokenStream(adaptor,"token EOL");
        RewriteRuleSubtreeStream stream_key_section=new RewriteRuleSubtreeStream(adaptor,"rule key_section");
        RewriteRuleSubtreeStream stream_value_section=new RewriteRuleSubtreeStream(adaptor,"rule value_section");
        RewriteRuleSubtreeStream stream_scope_section=new RewriteRuleSubtreeStream(adaptor,"rule scope_section");
        RewriteRuleSubtreeStream stream_meta_section=new RewriteRuleSubtreeStream(adaptor,"rule meta_section");
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:104:8: ( scope_section ( meta_section )? key_section EQUALS value_section ( EOL | EOF ) -> ^( VT_ENTRY scope_section ( meta_section )? key_section value_section ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:104:10: scope_section ( meta_section )? key_section EQUALS value_section ( EOL | EOF )
            {
            pushFollow(FOLLOW_scope_section_in_entry350);
            scope_section6=scope_section();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_scope_section.add(scope_section6.getTree());
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:104:24: ( meta_section )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==LEFT_SQUARE) ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1==LITERAL) ) {
                    int LA3_3 = input.LA(3);

                    if ( (LA3_3==RIGHT_SQUARE) ) {
                        int LA3_4 = input.LA(4);

                        if ( (synpred4_DSLMap()) ) {
                            alt3=1;
                        }
                    }
                }
                else if ( (LA3_1==RIGHT_SQUARE) ) {
                    int LA3_4 = input.LA(3);

                    if ( (synpred4_DSLMap()) ) {
                        alt3=1;
                    }
                }
            }
            switch (alt3) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: meta_section
                    {
                    pushFollow(FOLLOW_meta_section_in_entry352);
                    meta_section7=meta_section();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_meta_section.add(meta_section7.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_key_section_in_entry355);
            key_section8=key_section();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_key_section.add(key_section8.getTree());
            EQUALS9=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_entry357); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS9);

            pushFollow(FOLLOW_value_section_in_entry359);
            value_section10=value_section();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_value_section.add(value_section10.getTree());
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:104:71: ( EOL | EOF )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==EOL) ) {
                alt4=1;
            }
            else if ( (LA4_0==EOF) ) {
                alt4=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:104:72: EOL
                    {
                    EOL11=(Token)match(input,EOL,FOLLOW_EOL_in_entry362); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_EOL.add(EOL11);


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:104:76: EOF
                    {
                    EOF12=(Token)match(input,EOF,FOLLOW_EOF_in_entry364); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_EOF.add(EOF12);


                    }
                    break;

            }



            // AST REWRITE
            // elements: scope_section, key_section, meta_section, value_section
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 105:2: -> ^( VT_ENTRY scope_section ( meta_section )? key_section value_section )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:105:5: ^( VT_ENTRY scope_section ( meta_section )? key_section value_section )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ENTRY, "VT_ENTRY"), root_1);

                adaptor.addChild(root_1, stream_scope_section.nextTree());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:105:30: ( meta_section )?
                if ( stream_meta_section.hasNext() ) {
                    adaptor.addChild(root_1, stream_meta_section.nextTree());

                }
                stream_meta_section.reset();
                adaptor.addChild(root_1, stream_key_section.nextTree());
                adaptor.addChild(root_1, stream_value_section.nextTree());

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
        }
        return retval;
    }
    // $ANTLR end "entry"

    public static class scope_section_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "scope_section"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:115:1: scope_section : LEFT_SQUARE (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key ) RIGHT_SQUARE -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? ) ;
    public final DSLMapParser.scope_section_return scope_section() throws RecognitionException {
        DSLMapParser.scope_section_return retval = new DSLMapParser.scope_section_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE13=null;
        Token RIGHT_SQUARE14=null;
        DSLMapParser.condition_key_return value1 = null;

        DSLMapParser.consequence_key_return value2 = null;

        DSLMapParser.keyword_key_return value3 = null;

        DSLMapParser.any_key_return value4 = null;


        Object LEFT_SQUARE13_tree=null;
        Object RIGHT_SQUARE14_tree=null;
        RewriteRuleTokenStream stream_LEFT_SQUARE=new RewriteRuleTokenStream(adaptor,"token LEFT_SQUARE");
        RewriteRuleTokenStream stream_RIGHT_SQUARE=new RewriteRuleTokenStream(adaptor,"token RIGHT_SQUARE");
        RewriteRuleSubtreeStream stream_condition_key=new RewriteRuleSubtreeStream(adaptor,"rule condition_key");
        RewriteRuleSubtreeStream stream_any_key=new RewriteRuleSubtreeStream(adaptor,"rule any_key");
        RewriteRuleSubtreeStream stream_keyword_key=new RewriteRuleSubtreeStream(adaptor,"rule keyword_key");
        RewriteRuleSubtreeStream stream_consequence_key=new RewriteRuleSubtreeStream(adaptor,"rule consequence_key");
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:116:2: ( LEFT_SQUARE (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key ) RIGHT_SQUARE -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:116:4: LEFT_SQUARE (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key ) RIGHT_SQUARE
            {
            LEFT_SQUARE13=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_scope_section410); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_SQUARE.add(LEFT_SQUARE13);

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:117:3: (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key )
            int alt5=4;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==LITERAL) ) {
                int LA5_1 = input.LA(2);

                if ( ((synpred6_DSLMap()&&(validateIdentifierKey("condition")||validateIdentifierKey("when")))) ) {
                    alt5=1;
                }
                else if ( ((synpred7_DSLMap()&&(validateIdentifierKey("consequence")||validateIdentifierKey("then")))) ) {
                    alt5=2;
                }
                else if ( ((synpred8_DSLMap()&&(validateIdentifierKey("keyword")))) ) {
                    alt5=3;
                }
                else if ( ((validateIdentifierKey("*"))) ) {
                    alt5=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:117:4: value1= condition_key
                    {
                    pushFollow(FOLLOW_condition_key_in_scope_section418);
                    value1=condition_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_condition_key.add(value1.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:118:5: value2= consequence_key
                    {
                    pushFollow(FOLLOW_consequence_key_in_scope_section427);
                    value2=consequence_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_consequence_key.add(value2.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:5: value3= keyword_key
                    {
                    pushFollow(FOLLOW_keyword_key_in_scope_section435);
                    value3=keyword_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_keyword_key.add(value3.getTree());

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:120:5: value4= any_key
                    {
                    pushFollow(FOLLOW_any_key_in_scope_section443);
                    value4=any_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_any_key.add(value4.getTree());

                    }
                    break;

            }

            RIGHT_SQUARE14=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_scope_section451); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_SQUARE.add(RIGHT_SQUARE14);



            // AST REWRITE
            // elements: value4, value3, value1, value2
            // token labels: 
            // rule labels: value1, value4, value2, retval, value3
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_value1=new RewriteRuleSubtreeStream(adaptor,"token value1",value1!=null?value1.tree:null);
            RewriteRuleSubtreeStream stream_value4=new RewriteRuleSubtreeStream(adaptor,"token value4",value4!=null?value4.tree:null);
            RewriteRuleSubtreeStream stream_value2=new RewriteRuleSubtreeStream(adaptor,"token value2",value2!=null?value2.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_value3=new RewriteRuleSubtreeStream(adaptor,"token value3",value3!=null?value3.tree:null);

            root_0 = (Object)adaptor.nil();
            // 123:2: -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:123:5: ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_SCOPE, LEFT_SQUARE13, "SCOPE SECTION"), root_1);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:123:47: ( $value1)?
                if ( stream_value1.hasNext() ) {
                    adaptor.addChild(root_1, stream_value1.nextTree());

                }
                stream_value1.reset();
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:123:56: ( $value2)?
                if ( stream_value2.hasNext() ) {
                    adaptor.addChild(root_1, stream_value2.nextTree());

                }
                stream_value2.reset();
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:123:65: ( $value3)?
                if ( stream_value3.hasNext() ) {
                    adaptor.addChild(root_1, stream_value3.nextTree());

                }
                stream_value3.reset();
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:123:74: ( $value4)?
                if ( stream_value4.hasNext() ) {
                    adaptor.addChild(root_1, stream_value4.nextTree());

                }
                stream_value4.reset();

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
    // $ANTLR end "scope_section"

    public static class meta_section_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "meta_section"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:129:1: meta_section : LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? ) ;
    public final DSLMapParser.meta_section_return meta_section() throws RecognitionException {
        DSLMapParser.meta_section_return retval = new DSLMapParser.meta_section_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE15=null;
        Token LITERAL16=null;
        Token RIGHT_SQUARE17=null;

        Object LEFT_SQUARE15_tree=null;
        Object LITERAL16_tree=null;
        Object RIGHT_SQUARE17_tree=null;
        RewriteRuleTokenStream stream_LEFT_SQUARE=new RewriteRuleTokenStream(adaptor,"token LEFT_SQUARE");
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
        RewriteRuleTokenStream stream_RIGHT_SQUARE=new RewriteRuleTokenStream(adaptor,"token RIGHT_SQUARE");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:130:2: ( LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:130:4: LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE
            {
            LEFT_SQUARE15=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_meta_section491); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_SQUARE.add(LEFT_SQUARE15);

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:130:16: ( LITERAL )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==LITERAL) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: LITERAL
                    {
                    LITERAL16=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_meta_section493); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LITERAL.add(LITERAL16);


                    }
                    break;

            }

            RIGHT_SQUARE17=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_meta_section496); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_SQUARE.add(RIGHT_SQUARE17);



            // AST REWRITE
            // elements: LITERAL
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 131:2: -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:131:5: ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_META, LEFT_SQUARE15, "META SECTION"), root_1);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:131:45: ( LITERAL )?
                if ( stream_LITERAL.hasNext() ) {
                    adaptor.addChild(root_1, stream_LITERAL.nextNode());

                }
                stream_LITERAL.reset();

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
    // $ANTLR end "meta_section"

    public static class key_section_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "key_section"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:134:1: key_section : (ks= key_sentence )+ -> ^( VT_ENTRY_KEY ( key_sentence )+ ) ;
    public final DSLMapParser.key_section_return key_section() throws RecognitionException {
        DSLMapParser.key_section_return retval = new DSLMapParser.key_section_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.key_sentence_return ks = null;


        RewriteRuleSubtreeStream stream_key_sentence=new RewriteRuleSubtreeStream(adaptor,"rule key_sentence");
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:135:2: ( (ks= key_sentence )+ -> ^( VT_ENTRY_KEY ( key_sentence )+ ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:135:4: (ks= key_sentence )+
            {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:135:6: (ks= key_sentence )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>=LEFT_SQUARE && LA7_0<=LITERAL)||(LA7_0>=COLON && LA7_0<=LEFT_CURLY)) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: ks= key_sentence
            	    {
            	    pushFollow(FOLLOW_key_sentence_in_key_section520);
            	    ks=key_sentence();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_key_sentence.add(ks.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);



            // AST REWRITE
            // elements: key_sentence
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 136:2: -> ^( VT_ENTRY_KEY ( key_sentence )+ )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:136:5: ^( VT_ENTRY_KEY ( key_sentence )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ENTRY_KEY, "VT_ENTRY_KEY"), root_1);

                if ( !(stream_key_sentence.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_key_sentence.hasNext() ) {
                    adaptor.addChild(root_1, stream_key_sentence.nextTree());

                }
                stream_key_sentence.reset();

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
    // $ANTLR end "key_section"

    public static class key_sentence_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "key_sentence"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:139:1: key_sentence : ( variable_definition | cb= key_chunk -> VT_LITERAL[$cb.start, text] );
    public final DSLMapParser.key_sentence_return key_sentence() throws RecognitionException {
        DSLMapParser.key_sentence_return retval = new DSLMapParser.key_sentence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.key_chunk_return cb = null;

        DSLMapParser.variable_definition_return variable_definition18 = null;


        RewriteRuleSubtreeStream stream_key_chunk=new RewriteRuleSubtreeStream(adaptor,"rule key_chunk");

                String text = "";

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:143:2: ( variable_definition | cb= key_chunk -> VT_LITERAL[$cb.start, text] )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==LEFT_CURLY) ) {
                alt8=1;
            }
            else if ( ((LA8_0>=LEFT_SQUARE && LA8_0<=LITERAL)||LA8_0==COLON) ) {
                alt8=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:143:4: variable_definition
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_variable_definition_in_key_sentence551);
                    variable_definition18=variable_definition();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variable_definition18.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:144:4: cb= key_chunk
                    {
                    pushFollow(FOLLOW_key_chunk_in_key_sentence558);
                    cb=key_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_key_chunk.add(cb.getTree());
                    if ( state.backtracking==0 ) {
                       text = (cb!=null?input.toString(cb.start,cb.stop):null);
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
                    // 145:2: -> VT_LITERAL[$cb.start, text]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_LITERAL, (cb!=null?((Token)cb.start):null), text));

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
    // $ANTLR end "key_sentence"

    public static class key_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "key_chunk"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:148:1: key_chunk : ( literal )+ ;
    public final DSLMapParser.key_chunk_return key_chunk() throws RecognitionException {
        DSLMapParser.key_chunk_return retval = new DSLMapParser.key_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.literal_return literal19 = null;



        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:149:2: ( ( literal )+ )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:149:4: ( literal )+
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:149:4: ( literal )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>=LEFT_SQUARE && LA9_0<=LITERAL)||LA9_0==COLON) ) {
                    int LA9_2 = input.LA(2);

                    if ( (synpred12_DSLMap()) ) {
                        alt9=1;
                    }


                }


                switch (alt9) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: literal
            	    {
            	    pushFollow(FOLLOW_literal_in_key_chunk579);
            	    literal19=literal();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal19.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
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
    // $ANTLR end "key_chunk"

    public static class value_section_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "value_section"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:152:1: value_section : ( value_sentence )+ -> ^( VT_ENTRY_VAL ( value_sentence )+ ) ;
    public final DSLMapParser.value_section_return value_section() throws RecognitionException {
        DSLMapParser.value_section_return retval = new DSLMapParser.value_section_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.value_sentence_return value_sentence20 = null;


        RewriteRuleSubtreeStream stream_value_sentence=new RewriteRuleSubtreeStream(adaptor,"rule value_sentence");
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:153:2: ( ( value_sentence )+ -> ^( VT_ENTRY_VAL ( value_sentence )+ ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:153:4: ( value_sentence )+
            {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:153:4: ( value_sentence )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=EQUALS && LA10_0<=LEFT_CURLY)) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: value_sentence
            	    {
            	    pushFollow(FOLLOW_value_sentence_in_value_section594);
            	    value_sentence20=value_sentence();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_value_sentence.add(value_sentence20.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);



            // AST REWRITE
            // elements: value_sentence
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 154:2: -> ^( VT_ENTRY_VAL ( value_sentence )+ )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:154:5: ^( VT_ENTRY_VAL ( value_sentence )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ENTRY_VAL, "VT_ENTRY_VAL"), root_1);

                if ( !(stream_value_sentence.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_value_sentence.hasNext() ) {
                    adaptor.addChild(root_1, stream_value_sentence.nextTree());

                }
                stream_value_sentence.reset();

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
    // $ANTLR end "value_section"

    public static class value_sentence_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "value_sentence"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:157:1: value_sentence : ( variable_reference | vc= value_chunk -> VT_LITERAL[$vc.start, text] );
    public final DSLMapParser.value_sentence_return value_sentence() throws RecognitionException {
        DSLMapParser.value_sentence_return retval = new DSLMapParser.value_sentence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.value_chunk_return vc = null;

        DSLMapParser.variable_reference_return variable_reference21 = null;


        RewriteRuleSubtreeStream stream_value_chunk=new RewriteRuleSubtreeStream(adaptor,"rule value_chunk");

                String text = "";

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:161:2: ( variable_reference | vc= value_chunk -> VT_LITERAL[$vc.start, text] )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==LEFT_CURLY) ) {
                alt11=1;
            }
            else if ( ((LA11_0>=EQUALS && LA11_0<=COLON)) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:161:4: variable_reference
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_variable_reference_in_value_sentence625);
                    variable_reference21=variable_reference();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variable_reference21.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:162:4: vc= value_chunk
                    {
                    pushFollow(FOLLOW_value_chunk_in_value_sentence632);
                    vc=value_chunk();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_value_chunk.add(vc.getTree());
                    if ( state.backtracking==0 ) {
                       text = (vc!=null?input.toString(vc.start,vc.stop):null); 
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
                    // 163:2: -> VT_LITERAL[$vc.start, text]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(VT_LITERAL, (vc!=null?((Token)vc.start):null), text));

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
    // $ANTLR end "value_sentence"

    public static class value_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "value_chunk"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:166:1: value_chunk : ( literal | EQUALS | COMMA )+ ;
    public final DSLMapParser.value_chunk_return value_chunk() throws RecognitionException {
        DSLMapParser.value_chunk_return retval = new DSLMapParser.value_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS23=null;
        Token COMMA24=null;
        DSLMapParser.literal_return literal22 = null;


        Object EQUALS23_tree=null;
        Object COMMA24_tree=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:2: ( ( literal | EQUALS | COMMA )+ )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:4: ( literal | EQUALS | COMMA )+
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:4: ( literal | EQUALS | COMMA )+
            int cnt12=0;
            loop12:
            do {
                int alt12=4;
                switch ( input.LA(1) ) {
                case LEFT_SQUARE:
                case RIGHT_SQUARE:
                case LITERAL:
                case COLON:
                    {
                    int LA12_2 = input.LA(2);

                    if ( (synpred15_DSLMap()) ) {
                        alt12=1;
                    }


                    }
                    break;
                case EQUALS:
                    {
                    int LA12_3 = input.LA(2);

                    if ( (synpred16_DSLMap()) ) {
                        alt12=2;
                    }


                    }
                    break;
                case COMMA:
                    {
                    int LA12_4 = input.LA(2);

                    if ( (synpred17_DSLMap()) ) {
                        alt12=3;
                    }


                    }
                    break;

                }

                switch (alt12) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:5: literal
            	    {
            	    pushFollow(FOLLOW_literal_in_value_chunk654);
            	    literal22=literal();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal22.getTree());

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:13: EQUALS
            	    {
            	    EQUALS23=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_value_chunk656); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    EQUALS23_tree = (Object)adaptor.create(EQUALS23);
            	    adaptor.addChild(root_0, EQUALS23_tree);
            	    }

            	    }
            	    break;
            	case 3 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:20: COMMA
            	    {
            	    COMMA24=(Token)match(input,COMMA,FOLLOW_COMMA_in_value_chunk658); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    COMMA24_tree = (Object)adaptor.create(COMMA24);
            	    adaptor.addChild(root_0, COMMA24_tree);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
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
    // $ANTLR end "value_chunk"

    public static class literal_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:170:1: literal : ( LITERAL | COLON | LEFT_SQUARE | RIGHT_SQUARE ) ;
    public final DSLMapParser.literal_return literal() throws RecognitionException {
        DSLMapParser.literal_return retval = new DSLMapParser.literal_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set25=null;

        Object set25_tree=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:171:2: ( ( LITERAL | COLON | LEFT_SQUARE | RIGHT_SQUARE ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:171:4: ( LITERAL | COLON | LEFT_SQUARE | RIGHT_SQUARE )
            {
            root_0 = (Object)adaptor.nil();

            set25=(Token)input.LT(1);
            if ( (input.LA(1)>=LEFT_SQUARE && input.LA(1)<=LITERAL)||input.LA(1)==COLON ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set25));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
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
    // $ANTLR end "literal"

    public static class variable_definition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variable_definition"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:175:1: variable_definition : lc= LEFT_CURLY name= LITERAL ( ( COLON q= LITERAL )? COLON pat= pattern )? rc= RIGHT_CURLY -> {hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> {hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) ;
    public final DSLMapParser.variable_definition_return variable_definition() throws RecognitionException {
        DSLMapParser.variable_definition_return retval = new DSLMapParser.variable_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lc=null;
        Token name=null;
        Token q=null;
        Token rc=null;
        Token COLON26=null;
        Token COLON27=null;
        DSLMapParser.pattern_return pat = null;


        Object lc_tree=null;
        Object name_tree=null;
        Object q_tree=null;
        Object rc_tree=null;
        Object COLON26_tree=null;
        Object COLON27_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_RIGHT_CURLY=new RewriteRuleTokenStream(adaptor,"token RIGHT_CURLY");
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
        RewriteRuleTokenStream stream_LEFT_CURLY=new RewriteRuleTokenStream(adaptor,"token LEFT_CURLY");
        RewriteRuleSubtreeStream stream_pattern=new RewriteRuleSubtreeStream(adaptor,"rule pattern");

                String text = "";
                boolean hasSpaceBefore = false;
                boolean hasSpaceAfter = false;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:181:2: (lc= LEFT_CURLY name= LITERAL ( ( COLON q= LITERAL )? COLON pat= pattern )? rc= RIGHT_CURLY -> {hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> {hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:181:4: lc= LEFT_CURLY name= LITERAL ( ( COLON q= LITERAL )? COLON pat= pattern )? rc= RIGHT_CURLY
            {
            lc=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_definition710); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_CURLY.add(lc);

            if ( state.backtracking==0 ) {
               
              		CommonToken back2 =  (CommonToken)input.LT(-2);
              		if( back2!=null && back2.getStopIndex() < ((CommonToken)lc).getStartIndex() -1 ) hasSpaceBefore = true; 
              		
            }
            name=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition721); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LITERAL.add(name);

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:186:15: ( ( COLON q= LITERAL )? COLON pat= pattern )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==COLON) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:186:17: ( COLON q= LITERAL )? COLON pat= pattern
                    {
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:186:17: ( COLON q= LITERAL )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==COLON) ) {
                        int LA13_1 = input.LA(2);

                        if ( (LA13_1==LITERAL) ) {
                            int LA13_2 = input.LA(3);

                            if ( (LA13_2==COLON) ) {
                                int LA13_4 = input.LA(4);

                                if ( (synpred21_DSLMap()) ) {
                                    alt13=1;
                                }
                            }
                        }
                    }
                    switch (alt13) {
                        case 1 :
                            // src/main/resources/org/drools/lang/dsl/DSLMap.g:186:18: COLON q= LITERAL
                            {
                            COLON26=(Token)match(input,COLON,FOLLOW_COLON_in_variable_definition726); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_COLON.add(COLON26);

                            q=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition730); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LITERAL.add(q);


                            }
                            break;

                    }

                    COLON27=(Token)match(input,COLON,FOLLOW_COLON_in_variable_definition734); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON27);

                    pushFollow(FOLLOW_pattern_in_variable_definition738);
                    pat=pattern();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_pattern.add(pat.getTree());
                    if ( state.backtracking==0 ) {
                      text = (pat!=null?input.toString(pat.start,pat.stop):null);
                    }

                    }
                    break;

            }

            rc=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_definition747); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_CURLY.add(rc);

            if ( state.backtracking==0 ) {

              	CommonToken rc1 = (CommonToken)input.LT(1);
              	if(!"=".equals(rc1.getText()) && ((CommonToken)rc).getStopIndex() < rc1.getStartIndex() - 1) hasSpaceAfter = true;
              	
            }


            // AST REWRITE
            // elements: name, q, name, name, q, name, q, name, name, q, q, q, q, q, name, name, name, q
            // token labels: q, name
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_q=new RewriteRuleTokenStream(adaptor,"token q",q);
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 191:2: -> {hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
            if (hasSpaceBefore && !"".equals(text) && !hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:191:70: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:191:89: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:191:99: ( $q)?
                if ( stream_q.hasNext() ) {
                    adaptor.addChild(root_2, stream_q.nextNode());

                }
                stream_q.reset();

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, (Object)adaptor.create(VT_PATTERN, (pat!=null?((Token)pat.start):null), text));

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 192:2: -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
            if (!hasSpaceBefore && !"".equals(text)  && !hasSpaceAfter) {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:192:63: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:192:82: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:192:92: ( $q)?
                if ( stream_q.hasNext() ) {
                    adaptor.addChild(root_2, stream_q.nextNode());

                }
                stream_q.reset();

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, (Object)adaptor.create(VT_PATTERN, (pat!=null?((Token)pat.start):null), text));

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 193:2: -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
            if (hasSpaceBefore  && !hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:193:51: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:193:70: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:193:80: ( $q)?
                if ( stream_q.hasNext() ) {
                    adaptor.addChild(root_2, stream_q.nextNode());

                }
                stream_q.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 194:2: -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
            if (!hasSpaceBefore  && !hasSpaceAfter) {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:194:44: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:194:63: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:194:73: ( $q)?
                if ( stream_q.hasNext() ) {
                    adaptor.addChild(root_2, stream_q.nextNode());

                }
                stream_q.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 196:2: -> {hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE
            if (hasSpaceBefore && !"".equals(text) && hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:196:69: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:196:88: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:196:98: ( $q)?
                if ( stream_q.hasNext() ) {
                    adaptor.addChild(root_2, stream_q.nextNode());

                }
                stream_q.reset();

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, (Object)adaptor.create(VT_PATTERN, (pat!=null?((Token)pat.start):null), text));

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 197:2: -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE
            if (!hasSpaceBefore && !"".equals(text)  && hasSpaceAfter) {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:197:62: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:197:81: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:197:91: ( $q)?
                if ( stream_q.hasNext() ) {
                    adaptor.addChild(root_2, stream_q.nextNode());

                }
                stream_q.reset();

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, (Object)adaptor.create(VT_PATTERN, (pat!=null?((Token)pat.start):null), text));

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 198:2: -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE
            if (hasSpaceBefore  && hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:198:50: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:198:69: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:198:79: ( $q)?
                if ( stream_q.hasNext() ) {
                    adaptor.addChild(root_2, stream_q.nextNode());

                }
                stream_q.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 199:2: -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE
            if (!hasSpaceBefore  && hasSpaceAfter) {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:199:43: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:199:62: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:199:72: ( $q)?
                if ( stream_q.hasNext() ) {
                    adaptor.addChild(root_2, stream_q.nextNode());

                }
                stream_q.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 200:2: -> ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:200:5: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:200:24: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:200:34: ( $q)?
                if ( stream_q.hasNext() ) {
                    adaptor.addChild(root_2, stream_q.nextNode());

                }
                stream_q.reset();

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
    // $ANTLR end "variable_definition"

    public static class variable_definition2_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variable_definition2"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:203:1: variable_definition2 : LEFT_CURLY name= LITERAL ( COLON pat= pattern )? RIGHT_CURLY -> {!\"\".equals(text)}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) -> ^( VT_VAR_DEF $name) ;
    public final DSLMapParser.variable_definition2_return variable_definition2() throws RecognitionException {
        DSLMapParser.variable_definition2_return retval = new DSLMapParser.variable_definition2_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token LEFT_CURLY28=null;
        Token COLON29=null;
        Token RIGHT_CURLY30=null;
        DSLMapParser.pattern_return pat = null;


        Object name_tree=null;
        Object LEFT_CURLY28_tree=null;
        Object COLON29_tree=null;
        Object RIGHT_CURLY30_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_RIGHT_CURLY=new RewriteRuleTokenStream(adaptor,"token RIGHT_CURLY");
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
        RewriteRuleTokenStream stream_LEFT_CURLY=new RewriteRuleTokenStream(adaptor,"token LEFT_CURLY");
        RewriteRuleSubtreeStream stream_pattern=new RewriteRuleSubtreeStream(adaptor,"rule pattern");

                String text = "";

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:207:2: ( LEFT_CURLY name= LITERAL ( COLON pat= pattern )? RIGHT_CURLY -> {!\"\".equals(text)}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) -> ^( VT_VAR_DEF $name) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:207:4: LEFT_CURLY name= LITERAL ( COLON pat= pattern )? RIGHT_CURLY
            {
            LEFT_CURLY28=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_definition2990); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_CURLY.add(LEFT_CURLY28);

            name=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition2994); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LITERAL.add(name);

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:207:28: ( COLON pat= pattern )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==COLON) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:207:30: COLON pat= pattern
                    {
                    COLON29=(Token)match(input,COLON,FOLLOW_COLON_in_variable_definition2998); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON29);

                    pushFollow(FOLLOW_pattern_in_variable_definition21002);
                    pat=pattern();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_pattern.add(pat.getTree());
                    if ( state.backtracking==0 ) {
                      text = (pat!=null?input.toString(pat.start,pat.stop):null);
                    }

                    }
                    break;

            }

            RIGHT_CURLY30=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_definition21009); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_CURLY.add(RIGHT_CURLY30);



            // AST REWRITE
            // elements: name, name
            // token labels: name
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 208:2: -> {!\"\".equals(text)}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] )
            if (!"".equals(text)) {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:208:25: ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                adaptor.addChild(root_1, (Object)adaptor.create(VT_PATTERN, (pat!=null?((Token)pat.start):null), text));

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 209:2: -> ^( VT_VAR_DEF $name)
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:209:5: ^( VT_VAR_DEF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());

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
    // $ANTLR end "variable_definition2"

    public static class pattern_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "pattern"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:213:1: pattern : ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+ ;
    public final DSLMapParser.pattern_return pattern() throws RecognitionException {
        DSLMapParser.pattern_return retval = new DSLMapParser.pattern_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_CURLY32=null;
        Token RIGHT_CURLY34=null;
        Token LEFT_SQUARE35=null;
        Token RIGHT_SQUARE37=null;
        DSLMapParser.literal_return literal31 = null;

        DSLMapParser.literal_return literal33 = null;

        DSLMapParser.pattern_return pattern36 = null;


        Object LEFT_CURLY32_tree=null;
        Object RIGHT_CURLY34_tree=null;
        Object LEFT_SQUARE35_tree=null;
        Object RIGHT_SQUARE37_tree=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:214:9: ( ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+ )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:214:11: ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:214:11: ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+
            int cnt16=0;
            loop16:
            do {
                int alt16=4;
                switch ( input.LA(1) ) {
                case RIGHT_SQUARE:
                    {
                    int LA16_2 = input.LA(2);

                    if ( (synpred24_DSLMap()) ) {
                        alt16=1;
                    }


                    }
                    break;
                case LEFT_SQUARE:
                    {
                    int LA16_3 = input.LA(2);

                    if ( (synpred24_DSLMap()) ) {
                        alt16=1;
                    }
                    else if ( (synpred26_DSLMap()) ) {
                        alt16=3;
                    }


                    }
                    break;
                case LEFT_CURLY:
                    {
                    alt16=2;
                    }
                    break;
                case LITERAL:
                case COLON:
                    {
                    alt16=1;
                    }
                    break;

                }

                switch (alt16) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:214:13: literal
            	    {
            	    pushFollow(FOLLOW_literal_in_pattern1060);
            	    literal31=literal();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal31.getTree());

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:215:13: LEFT_CURLY literal RIGHT_CURLY
            	    {
            	    LEFT_CURLY32=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_pattern1074); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    LEFT_CURLY32_tree = (Object)adaptor.create(LEFT_CURLY32);
            	    adaptor.addChild(root_0, LEFT_CURLY32_tree);
            	    }
            	    pushFollow(FOLLOW_literal_in_pattern1076);
            	    literal33=literal();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal33.getTree());
            	    RIGHT_CURLY34=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_pattern1078); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    RIGHT_CURLY34_tree = (Object)adaptor.create(RIGHT_CURLY34);
            	    adaptor.addChild(root_0, RIGHT_CURLY34_tree);
            	    }

            	    }
            	    break;
            	case 3 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:216:13: LEFT_SQUARE pattern RIGHT_SQUARE
            	    {
            	    LEFT_SQUARE35=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_pattern1092); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    LEFT_SQUARE35_tree = (Object)adaptor.create(LEFT_SQUARE35);
            	    adaptor.addChild(root_0, LEFT_SQUARE35_tree);
            	    }
            	    pushFollow(FOLLOW_pattern_in_pattern1094);
            	    pattern36=pattern();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, pattern36.getTree());
            	    RIGHT_SQUARE37=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_pattern1096); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    RIGHT_SQUARE37_tree = (Object)adaptor.create(RIGHT_SQUARE37);
            	    adaptor.addChild(root_0, RIGHT_SQUARE37_tree);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
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
    // $ANTLR end "pattern"

    public static class variable_reference_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variable_reference"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:221:1: variable_reference : lc= LEFT_CURLY name= LITERAL rc= RIGHT_CURLY -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) VT_SPACE -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF $name) VT_SPACE -> ^( VT_VAR_REF $name) ;
    public final DSLMapParser.variable_reference_return variable_reference() throws RecognitionException {
        DSLMapParser.variable_reference_return retval = new DSLMapParser.variable_reference_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lc=null;
        Token name=null;
        Token rc=null;

        Object lc_tree=null;
        Object name_tree=null;
        Object rc_tree=null;
        RewriteRuleTokenStream stream_RIGHT_CURLY=new RewriteRuleTokenStream(adaptor,"token RIGHT_CURLY");
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
        RewriteRuleTokenStream stream_LEFT_CURLY=new RewriteRuleTokenStream(adaptor,"token LEFT_CURLY");


                boolean hasSpaceBefore = false;
                boolean hasSpaceAfter = false;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:226:2: (lc= LEFT_CURLY name= LITERAL rc= RIGHT_CURLY -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) VT_SPACE -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF $name) VT_SPACE -> ^( VT_VAR_REF $name) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:226:4: lc= LEFT_CURLY name= LITERAL rc= RIGHT_CURLY
            {
            lc=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_reference1131); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_CURLY.add(lc);

            if ( state.backtracking==0 ) {

              		CommonToken back2 =  (CommonToken)input.LT(-2);
              		if( back2!=null && back2.getStopIndex() < ((CommonToken)lc).getStartIndex() -1 ) hasSpaceBefore = true; 
              		
            }
            name=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_variable_reference1142); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LITERAL.add(name);

            rc=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_reference1146); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_CURLY.add(rc);

            if ( state.backtracking==0 ) {
              if(((CommonToken)rc).getStopIndex() < ((CommonToken)input.LT(1)).getStartIndex() - 1) hasSpaceAfter = true;
            }


            // AST REWRITE
            // elements: name, name, name, name
            // token labels: name
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 233:2: -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) VT_SPACE
            if (hasSpaceBefore && hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:233:49: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 234:2: -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name)
            if (hasSpaceBefore && !hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:234:50: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 235:2: -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF $name) VT_SPACE
            if (!hasSpaceBefore && hasSpaceAfter) {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:235:42: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 236:2: -> ^( VT_VAR_REF $name)
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:236:6: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());

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
    // $ANTLR end "variable_reference"

    public static class variable_reference2_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variable_reference2"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:240:1: variable_reference2 : LEFT_CURLY name= LITERAL RIGHT_CURLY -> ^( VT_VAR_REF $name) ;
    public final DSLMapParser.variable_reference2_return variable_reference2() throws RecognitionException {
        DSLMapParser.variable_reference2_return retval = new DSLMapParser.variable_reference2_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token LEFT_CURLY38=null;
        Token RIGHT_CURLY39=null;

        Object name_tree=null;
        Object LEFT_CURLY38_tree=null;
        Object RIGHT_CURLY39_tree=null;
        RewriteRuleTokenStream stream_RIGHT_CURLY=new RewriteRuleTokenStream(adaptor,"token RIGHT_CURLY");
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
        RewriteRuleTokenStream stream_LEFT_CURLY=new RewriteRuleTokenStream(adaptor,"token LEFT_CURLY");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:241:2: ( LEFT_CURLY name= LITERAL RIGHT_CURLY -> ^( VT_VAR_REF $name) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:241:4: LEFT_CURLY name= LITERAL RIGHT_CURLY
            {
            LEFT_CURLY38=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_reference21224); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEFT_CURLY.add(LEFT_CURLY38);

            name=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_variable_reference21228); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LITERAL.add(name);

            RIGHT_CURLY39=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_reference21230); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RIGHT_CURLY.add(RIGHT_CURLY39);



            // AST REWRITE
            // elements: name
            // token labels: name
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 242:2: -> ^( VT_VAR_REF $name)
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:242:5: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());

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
    // $ANTLR end "variable_reference2"

    public static class condition_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "condition_key"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:246:1: condition_key : {...}?value= LITERAL -> VT_CONDITION[$value] ;
    public final DSLMapParser.condition_key_return condition_key() throws RecognitionException {
        DSLMapParser.condition_key_return retval = new DSLMapParser.condition_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:247:2: ({...}?value= LITERAL -> VT_CONDITION[$value] )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:247:4: {...}?value= LITERAL
            {
            if ( !((validateIdentifierKey("condition")||validateIdentifierKey("when"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "condition_key", "validateIdentifierKey(\"condition\")||validateIdentifierKey(\"when\")");
            }
            value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_condition_key1259); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LITERAL.add(value);



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
            // 248:2: -> VT_CONDITION[$value]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_CONDITION, value));

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
    // $ANTLR end "condition_key"

    public static class consequence_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "consequence_key"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:251:1: consequence_key : {...}?value= LITERAL -> VT_CONSEQUENCE[$value] ;
    public final DSLMapParser.consequence_key_return consequence_key() throws RecognitionException {
        DSLMapParser.consequence_key_return retval = new DSLMapParser.consequence_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:252:2: ({...}?value= LITERAL -> VT_CONSEQUENCE[$value] )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:252:4: {...}?value= LITERAL
            {
            if ( !((validateIdentifierKey("consequence")||validateIdentifierKey("then"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "consequence_key", "validateIdentifierKey(\"consequence\")||validateIdentifierKey(\"then\")");
            }
            value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_consequence_key1282); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LITERAL.add(value);



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
            // 253:2: -> VT_CONSEQUENCE[$value]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_CONSEQUENCE, value));

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
    // $ANTLR end "consequence_key"

    public static class keyword_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "keyword_key"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:256:1: keyword_key : {...}?value= LITERAL -> VT_KEYWORD[$value] ;
    public final DSLMapParser.keyword_key_return keyword_key() throws RecognitionException {
        DSLMapParser.keyword_key_return retval = new DSLMapParser.keyword_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:257:2: ({...}?value= LITERAL -> VT_KEYWORD[$value] )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:257:4: {...}?value= LITERAL
            {
            if ( !((validateIdentifierKey("keyword"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "keyword_key", "validateIdentifierKey(\"keyword\")");
            }
            value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_keyword_key1305); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LITERAL.add(value);



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
            // 258:2: -> VT_KEYWORD[$value]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_KEYWORD, value));

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
    // $ANTLR end "keyword_key"

    public static class any_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "any_key"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:261:1: any_key : {...}?value= LITERAL -> VT_ANY[$value] ;
    public final DSLMapParser.any_key_return any_key() throws RecognitionException {
        DSLMapParser.any_key_return retval = new DSLMapParser.any_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:262:2: ({...}?value= LITERAL -> VT_ANY[$value] )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:262:4: {...}?value= LITERAL
            {
            if ( !((validateIdentifierKey("*"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "any_key", "validateIdentifierKey(\"*\")");
            }
            value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_any_key1328); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LITERAL.add(value);



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
            // 263:2: -> VT_ANY[$value]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_ANY, value));

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
    // $ANTLR end "any_key"

    // $ANTLR start synpred4_DSLMap
    public final void synpred4_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:104:24: ( meta_section )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:104:24: meta_section
        {
        pushFollow(FOLLOW_meta_section_in_synpred4_DSLMap352);
        meta_section();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_DSLMap

    // $ANTLR start synpred6_DSLMap
    public final void synpred6_DSLMap_fragment() throws RecognitionException {   
        DSLMapParser.condition_key_return value1 = null;


        // src/main/resources/org/drools/lang/dsl/DSLMap.g:117:4: (value1= condition_key )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:117:4: value1= condition_key
        {
        pushFollow(FOLLOW_condition_key_in_synpred6_DSLMap418);
        value1=condition_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_DSLMap

    // $ANTLR start synpred7_DSLMap
    public final void synpred7_DSLMap_fragment() throws RecognitionException {   
        DSLMapParser.consequence_key_return value2 = null;


        // src/main/resources/org/drools/lang/dsl/DSLMap.g:118:5: (value2= consequence_key )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:118:5: value2= consequence_key
        {
        pushFollow(FOLLOW_consequence_key_in_synpred7_DSLMap427);
        value2=consequence_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_DSLMap

    // $ANTLR start synpred8_DSLMap
    public final void synpred8_DSLMap_fragment() throws RecognitionException {   
        DSLMapParser.keyword_key_return value3 = null;


        // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:5: (value3= keyword_key )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:5: value3= keyword_key
        {
        pushFollow(FOLLOW_keyword_key_in_synpred8_DSLMap435);
        value3=keyword_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_DSLMap

    // $ANTLR start synpred12_DSLMap
    public final void synpred12_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:149:4: ( literal )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:149:4: literal
        {
        pushFollow(FOLLOW_literal_in_synpred12_DSLMap579);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_DSLMap

    // $ANTLR start synpred15_DSLMap
    public final void synpred15_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:5: ( literal )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:5: literal
        {
        pushFollow(FOLLOW_literal_in_synpred15_DSLMap654);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_DSLMap

    // $ANTLR start synpred16_DSLMap
    public final void synpred16_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:13: ( EQUALS )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:13: EQUALS
        {
        match(input,EQUALS,FOLLOW_EQUALS_in_synpred16_DSLMap656); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred16_DSLMap

    // $ANTLR start synpred17_DSLMap
    public final void synpred17_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:20: ( COMMA )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:20: COMMA
        {
        match(input,COMMA,FOLLOW_COMMA_in_synpred17_DSLMap658); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred17_DSLMap

    // $ANTLR start synpred21_DSLMap
    public final void synpred21_DSLMap_fragment() throws RecognitionException {   
        Token q=null;

        // src/main/resources/org/drools/lang/dsl/DSLMap.g:186:18: ( COLON q= LITERAL )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:186:18: COLON q= LITERAL
        {
        match(input,COLON,FOLLOW_COLON_in_synpred21_DSLMap726); if (state.failed) return ;
        q=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_synpred21_DSLMap730); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred21_DSLMap

    // $ANTLR start synpred24_DSLMap
    public final void synpred24_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:214:13: ( literal )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:214:13: literal
        {
        pushFollow(FOLLOW_literal_in_synpred24_DSLMap1060);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred24_DSLMap

    // $ANTLR start synpred26_DSLMap
    public final void synpred26_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:216:13: ( LEFT_SQUARE pattern RIGHT_SQUARE )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:216:13: LEFT_SQUARE pattern RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred26_DSLMap1092); if (state.failed) return ;
        pushFollow(FOLLOW_pattern_in_synpred26_DSLMap1094);
        pattern();

        state._fsp--;
        if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred26_DSLMap1096); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred26_DSLMap

    // Delegated rules

    public final boolean synpred21_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred21_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred15_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred24_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred24_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred17_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred17_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred26_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred26_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred16_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred16_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_statement_in_mapping_file273 = new BitSet(new long[]{0x0000000001600002L});
    public static final BitSet FOLLOW_entry_in_statement296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comment_in_statement303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOL_in_statement309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LINE_COMMENT_in_comment325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scope_section_in_entry350 = new BitSet(new long[]{0x0000000037000000L});
    public static final BitSet FOLLOW_meta_section_in_entry352 = new BitSet(new long[]{0x0000000037000000L});
    public static final BitSet FOLLOW_key_section_in_entry355 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_EQUALS_in_entry357 = new BitSet(new long[]{0x000000003F800000L});
    public static final BitSet FOLLOW_value_section_in_entry359 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_EOL_in_entry362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOF_in_entry364 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_scope_section410 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_condition_key_in_scope_section418 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_consequence_key_in_scope_section427 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_keyword_key_in_scope_section435 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_any_key_in_scope_section443 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_scope_section451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_meta_section491 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_LITERAL_in_meta_section493 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_meta_section496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_key_sentence_in_key_section520 = new BitSet(new long[]{0x0000000037000002L});
    public static final BitSet FOLLOW_variable_definition_in_key_sentence551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_key_chunk_in_key_sentence558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_key_chunk579 = new BitSet(new long[]{0x0000000037000002L});
    public static final BitSet FOLLOW_value_sentence_in_value_section594 = new BitSet(new long[]{0x000000003F800002L});
    public static final BitSet FOLLOW_variable_reference_in_value_sentence625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_chunk_in_value_sentence632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_value_chunk654 = new BitSet(new long[]{0x000000003F800002L});
    public static final BitSet FOLLOW_EQUALS_in_value_chunk656 = new BitSet(new long[]{0x000000003F800002L});
    public static final BitSet FOLLOW_COMMA_in_value_chunk658 = new BitSet(new long[]{0x000000003F800002L});
    public static final BitSet FOLLOW_set_in_literal674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_variable_definition710 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_definition721 = new BitSet(new long[]{0x0000000050000000L});
    public static final BitSet FOLLOW_COLON_in_variable_definition726 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_definition730 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_variable_definition734 = new BitSet(new long[]{0x0000000037000000L});
    public static final BitSet FOLLOW_pattern_in_variable_definition738 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_definition747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_variable_definition2990 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_definition2994 = new BitSet(new long[]{0x0000000050000000L});
    public static final BitSet FOLLOW_COLON_in_variable_definition2998 = new BitSet(new long[]{0x0000000037000000L});
    public static final BitSet FOLLOW_pattern_in_variable_definition21002 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_definition21009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_pattern1060 = new BitSet(new long[]{0x0000000037000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_pattern1074 = new BitSet(new long[]{0x0000000037000000L});
    public static final BitSet FOLLOW_literal_in_pattern1076 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_pattern1078 = new BitSet(new long[]{0x0000000037000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_pattern1092 = new BitSet(new long[]{0x0000000037000000L});
    public static final BitSet FOLLOW_pattern_in_pattern1094 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_pattern1096 = new BitSet(new long[]{0x0000000037000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_variable_reference1131 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_reference1142 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_reference1146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_variable_reference21224 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_reference21228 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_reference21230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_condition_key1259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_consequence_key1282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_keyword_key1305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_any_key1328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_meta_section_in_synpred4_DSLMap352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_condition_key_in_synpred6_DSLMap418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_consequence_key_in_synpred7_DSLMap427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_key_in_synpred8_DSLMap435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred12_DSLMap579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred15_DSLMap654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_synpred16_DSLMap656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_synpred17_DSLMap658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_synpred21_DSLMap726 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LITERAL_in_synpred21_DSLMap730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred24_DSLMap1060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred26_DSLMap1092 = new BitSet(new long[]{0x0000000037000000L});
    public static final BitSet FOLLOW_pattern_in_synpred26_DSLMap1094 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred26_DSLMap1096 = new BitSet(new long[]{0x0000000000000002L});

}