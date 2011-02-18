// $ANTLR 3.3 Nov 30, 2010 12:46:29 src/main/resources/org/drools/lang/dsl/DSLMap.g 2011-01-18 19:45:17

	package org.drools.lang.dsl;
	import java.util.List;
	import java.util.ArrayList;
    import java.util.regex.Pattern;
    import org.drools.compiler.ParserError;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class DSLMapParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_DSL_GRAMMAR", "VT_ENTRY", "VT_SCOPE", "VT_CONDITION", "VT_CONSEQUENCE", "VT_KEYWORD", "VT_ANY", "VT_META", "VT_ENTRY_KEY", "VT_ENTRY_VAL", "VT_VAR_DEF", "VT_VAR_REF", "VT_LITERAL", "VT_PATTERN", "VT_QUAL", "VT_SPACE", "EOL", "EQUALS", "LEFT_SQUARE", "RIGHT_SQUARE", "LITERAL", "COMMA", "COLON", "LEFT_CURLY", "RIGHT_CURLY", "WS", "EscapeSequence", "DOT", "IdentifierPart", "MISC"
    };
    public static final int EOF=-1;
    public static final int VT_DSL_GRAMMAR=4;
    public static final int VT_ENTRY=5;
    public static final int VT_SCOPE=6;
    public static final int VT_CONDITION=7;
    public static final int VT_CONSEQUENCE=8;
    public static final int VT_KEYWORD=9;
    public static final int VT_ANY=10;
    public static final int VT_META=11;
    public static final int VT_ENTRY_KEY=12;
    public static final int VT_ENTRY_VAL=13;
    public static final int VT_VAR_DEF=14;
    public static final int VT_VAR_REF=15;
    public static final int VT_LITERAL=16;
    public static final int VT_PATTERN=17;
    public static final int VT_QUAL=18;
    public static final int VT_SPACE=19;
    public static final int EOL=20;
    public static final int EQUALS=21;
    public static final int LEFT_SQUARE=22;
    public static final int RIGHT_SQUARE=23;
    public static final int LITERAL=24;
    public static final int COMMA=25;
    public static final int COLON=26;
    public static final int LEFT_CURLY=27;
    public static final int RIGHT_CURLY=28;
    public static final int WS=29;
    public static final int EscapeSequence=30;
    public static final int DOT=31;
    public static final int IdentifierPart=32;
    public static final int MISC=33;

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


    	private List<ParserError> errors = new ArrayList<ParserError>();

    	public void reportError(RecognitionException ex) {
    		errors.add(new ParserError( "DSL parser error", ex.line, ex.charPositionInLine ) );
    	}

    	public List<ParserError> getErrors() {
    		return errors;
    	}

    	/** Override this method to not output mesages */
    	public void emitErrorMessage(String msg) {
    	}

        private static final Pattern namePat = Pattern.compile( "[\\p{L}_$][\\p{L}_$\\d]*" );

        private void isIdentifier( Token name ){
            String nameString = name.getText();
            if( ! namePat.matcher( nameString ).matches() ){
                errors.add(new ParserError( "invalid variable identifier " + nameString,
                                            name.getLine(), name.getCharPositionInLine() ) );
            }
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

    	


    public static class mapping_file_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mapping_file"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:106:1: mapping_file : ( statement )* -> ^( VT_DSL_GRAMMAR ( statement )* ) ;
    public final DSLMapParser.mapping_file_return mapping_file() throws RecognitionException {
        DSLMapParser.mapping_file_return retval = new DSLMapParser.mapping_file_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.statement_return statement1 = null;


        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:107:2: ( ( statement )* -> ^( VT_DSL_GRAMMAR ( statement )* ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:107:4: ( statement )*
            {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:107:4: ( statement )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==EOL||LA1_0==LEFT_SQUARE) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_mapping_file272);
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
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 108:2: -> ^( VT_DSL_GRAMMAR ( statement )* )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:108:5: ^( VT_DSL_GRAMMAR ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_DSL_GRAMMAR, "VT_DSL_GRAMMAR"), root_1);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:108:22: ( statement )*
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:111:1: statement : ( entry | EOL );
    public final DSLMapParser.statement_return statement() throws RecognitionException {
        DSLMapParser.statement_return retval = new DSLMapParser.statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOL3=null;
        DSLMapParser.entry_return entry2 = null;


        Object EOL3_tree=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:112:2: ( entry | EOL )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==LEFT_SQUARE) ) {
                alt2=1;
            }
            else if ( (LA2_0==EOL) ) {
                alt2=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:112:4: entry
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_entry_in_statement295);
                    entry2=entry();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, entry2.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:113:4: EOL
                    {
                    root_0 = (Object)adaptor.nil();

                    EOL3=(Token)match(input,EOL,FOLLOW_EOL_in_statement302); if (state.failed) return retval;

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

    public static class entry_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "entry"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:1: entry : scope_section ( meta_section )? key_section EQUALS ( value_section )? ( EOL | EOF ) -> ^( VT_ENTRY scope_section ( meta_section )? key_section ( value_section )? ) ;
    public final DSLMapParser.entry_return entry() throws RecognitionException {
        DSLMapParser.entry_return retval = new DSLMapParser.entry_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS7=null;
        Token EOL9=null;
        Token EOF10=null;
        DSLMapParser.scope_section_return scope_section4 = null;

        DSLMapParser.meta_section_return meta_section5 = null;

        DSLMapParser.key_section_return key_section6 = null;

        DSLMapParser.value_section_return value_section8 = null;


        Object EQUALS7_tree=null;
        Object EOL9_tree=null;
        Object EOF10_tree=null;
        RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
        RewriteRuleTokenStream stream_EOL=new RewriteRuleTokenStream(adaptor,"token EOL");
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_meta_section=new RewriteRuleSubtreeStream(adaptor,"rule meta_section");
        RewriteRuleSubtreeStream stream_key_section=new RewriteRuleSubtreeStream(adaptor,"rule key_section");
        RewriteRuleSubtreeStream stream_scope_section=new RewriteRuleSubtreeStream(adaptor,"rule scope_section");
        RewriteRuleSubtreeStream stream_value_section=new RewriteRuleSubtreeStream(adaptor,"rule value_section");
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:8: ( scope_section ( meta_section )? key_section EQUALS ( value_section )? ( EOL | EOF ) -> ^( VT_ENTRY scope_section ( meta_section )? key_section ( value_section )? ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:10: scope_section ( meta_section )? key_section EQUALS ( value_section )? ( EOL | EOF )
            {
            pushFollow(FOLLOW_scope_section_in_entry320);
            scope_section4=scope_section();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_scope_section.add(scope_section4.getTree());
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:24: ( meta_section )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==LEFT_SQUARE) ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1==LITERAL) ) {
                    int LA3_3 = input.LA(3);

                    if ( (LA3_3==RIGHT_SQUARE) ) {
                        int LA3_4 = input.LA(4);

                        if ( (synpred3_DSLMap()) ) {
                            alt3=1;
                        }
                    }
                }
                else if ( (LA3_1==RIGHT_SQUARE) ) {
                    int LA3_4 = input.LA(3);

                    if ( (synpred3_DSLMap()) ) {
                        alt3=1;
                    }
                }
            }
            switch (alt3) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: meta_section
                    {
                    pushFollow(FOLLOW_meta_section_in_entry322);
                    meta_section5=meta_section();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_meta_section.add(meta_section5.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_key_section_in_entry325);
            key_section6=key_section();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_key_section.add(key_section6.getTree());
            EQUALS7=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_entry327); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS7);

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:57: ( value_section )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>=EQUALS && LA4_0<=LEFT_CURLY)) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: value_section
                    {
                    pushFollow(FOLLOW_value_section_in_entry329);
                    value_section8=value_section();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_value_section.add(value_section8.getTree());

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:72: ( EOL | EOF )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==EOL) ) {
                alt5=1;
            }
            else if ( (LA5_0==EOF) ) {
                alt5=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:73: EOL
                    {
                    EOL9=(Token)match(input,EOL,FOLLOW_EOL_in_entry333); if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_EOL.add(EOL9);


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:77: EOF
                    {
                    EOF10=(Token)match(input,EOF,FOLLOW_EOF_in_entry335); if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_EOF.add(EOF10);


                    }
                    break;

            }



            // AST REWRITE
            // elements: value_section, key_section, meta_section, scope_section
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 120:2: -> ^( VT_ENTRY scope_section ( meta_section )? key_section ( value_section )? )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:120:5: ^( VT_ENTRY scope_section ( meta_section )? key_section ( value_section )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_ENTRY, "VT_ENTRY"), root_1);

                adaptor.addChild(root_1, stream_scope_section.nextTree());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:120:30: ( meta_section )?
                if ( stream_meta_section.hasNext() ) {
                    adaptor.addChild(root_1, stream_meta_section.nextTree());

                }
                stream_meta_section.reset();
                adaptor.addChild(root_1, stream_key_section.nextTree());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:120:56: ( value_section )?
                if ( stream_value_section.hasNext() ) {
                    adaptor.addChild(root_1, stream_value_section.nextTree());

                }
                stream_value_section.reset();

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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:130:1: scope_section : LEFT_SQUARE (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key ) RIGHT_SQUARE -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? ) ;
    public final DSLMapParser.scope_section_return scope_section() throws RecognitionException {
        DSLMapParser.scope_section_return retval = new DSLMapParser.scope_section_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE11=null;
        Token RIGHT_SQUARE12=null;
        DSLMapParser.condition_key_return value1 = null;

        DSLMapParser.consequence_key_return value2 = null;

        DSLMapParser.keyword_key_return value3 = null;

        DSLMapParser.any_key_return value4 = null;


        Object LEFT_SQUARE11_tree=null;
        Object RIGHT_SQUARE12_tree=null;
        RewriteRuleTokenStream stream_RIGHT_SQUARE=new RewriteRuleTokenStream(adaptor,"token RIGHT_SQUARE");
        RewriteRuleTokenStream stream_LEFT_SQUARE=new RewriteRuleTokenStream(adaptor,"token LEFT_SQUARE");
        RewriteRuleSubtreeStream stream_any_key=new RewriteRuleSubtreeStream(adaptor,"rule any_key");
        RewriteRuleSubtreeStream stream_condition_key=new RewriteRuleSubtreeStream(adaptor,"rule condition_key");
        RewriteRuleSubtreeStream stream_keyword_key=new RewriteRuleSubtreeStream(adaptor,"rule keyword_key");
        RewriteRuleSubtreeStream stream_consequence_key=new RewriteRuleSubtreeStream(adaptor,"rule consequence_key");
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:131:2: ( LEFT_SQUARE (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key ) RIGHT_SQUARE -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:131:4: LEFT_SQUARE (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key ) RIGHT_SQUARE
            {
            LEFT_SQUARE11=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_scope_section382); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_LEFT_SQUARE.add(LEFT_SQUARE11);

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:132:3: (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key )
            int alt6=4;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==LITERAL) ) {
                int LA6_1 = input.LA(2);

                if ( ((synpred6_DSLMap()&&(validateIdentifierKey("condition")||validateIdentifierKey("when")))) ) {
                    alt6=1;
                }
                else if ( ((synpred7_DSLMap()&&(validateIdentifierKey("consequence")||validateIdentifierKey("then")))) ) {
                    alt6=2;
                }
                else if ( ((synpred8_DSLMap()&&(validateIdentifierKey("keyword")))) ) {
                    alt6=3;
                }
                else if ( ((validateIdentifierKey("*"))) ) {
                    alt6=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:132:4: value1= condition_key
                    {
                    pushFollow(FOLLOW_condition_key_in_scope_section390);
                    value1=condition_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_condition_key.add(value1.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:133:5: value2= consequence_key
                    {
                    pushFollow(FOLLOW_consequence_key_in_scope_section399);
                    value2=consequence_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_consequence_key.add(value2.getTree());

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:134:5: value3= keyword_key
                    {
                    pushFollow(FOLLOW_keyword_key_in_scope_section407);
                    value3=keyword_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_keyword_key.add(value3.getTree());

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:135:5: value4= any_key
                    {
                    pushFollow(FOLLOW_any_key_in_scope_section415);
                    value4=any_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_any_key.add(value4.getTree());

                    }
                    break;

            }

            RIGHT_SQUARE12=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_scope_section423); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_RIGHT_SQUARE.add(RIGHT_SQUARE12);



            // AST REWRITE
            // elements: value4, value2, value1, value3
            // token labels: 
            // rule labels: retval, value3, value4, value1, value2
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_value3=new RewriteRuleSubtreeStream(adaptor,"rule value3",value3!=null?value3.tree:null);
            RewriteRuleSubtreeStream stream_value4=new RewriteRuleSubtreeStream(adaptor,"rule value4",value4!=null?value4.tree:null);
            RewriteRuleSubtreeStream stream_value1=new RewriteRuleSubtreeStream(adaptor,"rule value1",value1!=null?value1.tree:null);
            RewriteRuleSubtreeStream stream_value2=new RewriteRuleSubtreeStream(adaptor,"rule value2",value2!=null?value2.tree:null);

            root_0 = (Object)adaptor.nil();
            // 138:2: -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:138:5: ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_SCOPE, LEFT_SQUARE11, "SCOPE SECTION"), root_1);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:138:47: ( $value1)?
                if ( stream_value1.hasNext() ) {
                    adaptor.addChild(root_1, stream_value1.nextTree());

                }
                stream_value1.reset();
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:138:56: ( $value2)?
                if ( stream_value2.hasNext() ) {
                    adaptor.addChild(root_1, stream_value2.nextTree());

                }
                stream_value2.reset();
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:138:65: ( $value3)?
                if ( stream_value3.hasNext() ) {
                    adaptor.addChild(root_1, stream_value3.nextTree());

                }
                stream_value3.reset();
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:138:74: ( $value4)?
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:144:1: meta_section : LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? ) ;
    public final DSLMapParser.meta_section_return meta_section() throws RecognitionException {
        DSLMapParser.meta_section_return retval = new DSLMapParser.meta_section_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE13=null;
        Token LITERAL14=null;
        Token RIGHT_SQUARE15=null;

        Object LEFT_SQUARE13_tree=null;
        Object LITERAL14_tree=null;
        Object RIGHT_SQUARE15_tree=null;
        RewriteRuleTokenStream stream_RIGHT_SQUARE=new RewriteRuleTokenStream(adaptor,"token RIGHT_SQUARE");
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
        RewriteRuleTokenStream stream_LEFT_SQUARE=new RewriteRuleTokenStream(adaptor,"token LEFT_SQUARE");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:145:2: ( LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:145:4: LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE
            {
            LEFT_SQUARE13=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_meta_section463); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_LEFT_SQUARE.add(LEFT_SQUARE13);

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:145:16: ( LITERAL )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==LITERAL) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: LITERAL
                    {
                    LITERAL14=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_meta_section465); if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_LITERAL.add(LITERAL14);


                    }
                    break;

            }

            RIGHT_SQUARE15=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_meta_section468); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_RIGHT_SQUARE.add(RIGHT_SQUARE15);



            // AST REWRITE
            // elements: LITERAL
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 146:2: -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:146:5: ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_META, LEFT_SQUARE13, "META SECTION"), root_1);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:146:45: ( LITERAL )?
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:149:1: key_section : (ks= key_sentence )+ -> ^( VT_ENTRY_KEY ( key_sentence )+ ) ;
    public final DSLMapParser.key_section_return key_section() throws RecognitionException {
        DSLMapParser.key_section_return retval = new DSLMapParser.key_section_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.key_sentence_return ks = null;


        RewriteRuleSubtreeStream stream_key_sentence=new RewriteRuleSubtreeStream(adaptor,"rule key_sentence");
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:150:2: ( (ks= key_sentence )+ -> ^( VT_ENTRY_KEY ( key_sentence )+ ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:150:4: (ks= key_sentence )+
            {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:150:6: (ks= key_sentence )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0>=LEFT_SQUARE && LA8_0<=LITERAL)||(LA8_0>=COLON && LA8_0<=LEFT_CURLY)) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: ks= key_sentence
            	    {
            	    pushFollow(FOLLOW_key_sentence_in_key_section492);
            	    ks=key_sentence();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_key_sentence.add(ks.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);



            // AST REWRITE
            // elements: key_sentence
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 151:2: -> ^( VT_ENTRY_KEY ( key_sentence )+ )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:151:5: ^( VT_ENTRY_KEY ( key_sentence )+ )
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:154:1: key_sentence : ( variable_definition | cb= key_chunk -> VT_LITERAL[$cb.start, text] );
    public final DSLMapParser.key_sentence_return key_sentence() throws RecognitionException {
        DSLMapParser.key_sentence_return retval = new DSLMapParser.key_sentence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.key_chunk_return cb = null;

        DSLMapParser.variable_definition_return variable_definition16 = null;


        RewriteRuleSubtreeStream stream_key_chunk=new RewriteRuleSubtreeStream(adaptor,"rule key_chunk");

                String text = "";

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:158:2: ( variable_definition | cb= key_chunk -> VT_LITERAL[$cb.start, text] )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==LEFT_CURLY) ) {
                alt9=1;
            }
            else if ( ((LA9_0>=LEFT_SQUARE && LA9_0<=LITERAL)||LA9_0==COLON) ) {
                alt9=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:158:4: variable_definition
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_variable_definition_in_key_sentence523);
                    variable_definition16=variable_definition();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variable_definition16.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:159:4: cb= key_chunk
                    {
                    pushFollow(FOLLOW_key_chunk_in_key_sentence530);
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
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 160:2: -> VT_LITERAL[$cb.start, text]
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:163:1: key_chunk : ( literal )+ ;
    public final DSLMapParser.key_chunk_return key_chunk() throws RecognitionException {
        DSLMapParser.key_chunk_return retval = new DSLMapParser.key_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.literal_return literal17 = null;



        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:164:2: ( ( literal )+ )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:164:4: ( literal )+
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:164:4: ( literal )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=LEFT_SQUARE && LA10_0<=LITERAL)||LA10_0==COLON) ) {
                    int LA10_2 = input.LA(2);

                    if ( (synpred12_DSLMap()) ) {
                        alt10=1;
                    }


                }


                switch (alt10) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: literal
            	    {
            	    pushFollow(FOLLOW_literal_in_key_chunk551);
            	    literal17=literal();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal17.getTree());

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


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:167:1: value_section : ( value_sentence )+ -> ^( VT_ENTRY_VAL ( value_sentence )+ ) ;
    public final DSLMapParser.value_section_return value_section() throws RecognitionException {
        DSLMapParser.value_section_return retval = new DSLMapParser.value_section_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.value_sentence_return value_sentence18 = null;


        RewriteRuleSubtreeStream stream_value_sentence=new RewriteRuleSubtreeStream(adaptor,"rule value_sentence");
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:168:2: ( ( value_sentence )+ -> ^( VT_ENTRY_VAL ( value_sentence )+ ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:168:4: ( value_sentence )+
            {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:168:4: ( value_sentence )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>=EQUALS && LA11_0<=LEFT_CURLY)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:0:0: value_sentence
            	    {
            	    pushFollow(FOLLOW_value_sentence_in_value_section566);
            	    value_sentence18=value_sentence();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_value_sentence.add(value_sentence18.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);



            // AST REWRITE
            // elements: value_sentence
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 169:2: -> ^( VT_ENTRY_VAL ( value_sentence )+ )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:169:5: ^( VT_ENTRY_VAL ( value_sentence )+ )
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:172:1: value_sentence : ( variable_reference | vc= value_chunk -> VT_LITERAL[$vc.start, text] );
    public final DSLMapParser.value_sentence_return value_sentence() throws RecognitionException {
        DSLMapParser.value_sentence_return retval = new DSLMapParser.value_sentence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DSLMapParser.value_chunk_return vc = null;

        DSLMapParser.variable_reference_return variable_reference19 = null;


        RewriteRuleSubtreeStream stream_value_chunk=new RewriteRuleSubtreeStream(adaptor,"rule value_chunk");

                String text = "";

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:176:2: ( variable_reference | vc= value_chunk -> VT_LITERAL[$vc.start, text] )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==LEFT_CURLY) ) {
                alt12=1;
            }
            else if ( ((LA12_0>=EQUALS && LA12_0<=COLON)) ) {
                alt12=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:176:4: variable_reference
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_variable_reference_in_value_sentence597);
                    variable_reference19=variable_reference();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variable_reference19.getTree());

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:177:4: vc= value_chunk
                    {
                    pushFollow(FOLLOW_value_chunk_in_value_sentence604);
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
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 178:2: -> VT_LITERAL[$vc.start, text]
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:181:1: value_chunk : ( literal | EQUALS | COMMA )+ ;
    public final DSLMapParser.value_chunk_return value_chunk() throws RecognitionException {
        DSLMapParser.value_chunk_return retval = new DSLMapParser.value_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS21=null;
        Token COMMA22=null;
        DSLMapParser.literal_return literal20 = null;


        Object EQUALS21_tree=null;
        Object COMMA22_tree=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:2: ( ( literal | EQUALS | COMMA )+ )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:4: ( literal | EQUALS | COMMA )+
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:4: ( literal | EQUALS | COMMA )+
            int cnt13=0;
            loop13:
            do {
                int alt13=4;
                switch ( input.LA(1) ) {
                case LEFT_SQUARE:
                case RIGHT_SQUARE:
                case LITERAL:
                case COLON:
                    {
                    int LA13_2 = input.LA(2);

                    if ( (synpred15_DSLMap()) ) {
                        alt13=1;
                    }


                    }
                    break;
                case EQUALS:
                    {
                    int LA13_3 = input.LA(2);

                    if ( (synpred16_DSLMap()) ) {
                        alt13=2;
                    }


                    }
                    break;
                case COMMA:
                    {
                    int LA13_4 = input.LA(2);

                    if ( (synpred17_DSLMap()) ) {
                        alt13=3;
                    }


                    }
                    break;

                }

                switch (alt13) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:5: literal
            	    {
            	    pushFollow(FOLLOW_literal_in_value_chunk626);
            	    literal20=literal();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal20.getTree());

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:13: EQUALS
            	    {
            	    EQUALS21=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_value_chunk628); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    EQUALS21_tree = (Object)adaptor.create(EQUALS21);
            	    adaptor.addChild(root_0, EQUALS21_tree);
            	    }

            	    }
            	    break;
            	case 3 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:20: COMMA
            	    {
            	    COMMA22=(Token)match(input,COMMA,FOLLOW_COMMA_in_value_chunk630); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    COMMA22_tree = (Object)adaptor.create(COMMA22);
            	    adaptor.addChild(root_0, COMMA22_tree);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:185:1: literal : ( LITERAL | COLON | LEFT_SQUARE | RIGHT_SQUARE ) ;
    public final DSLMapParser.literal_return literal() throws RecognitionException {
        DSLMapParser.literal_return retval = new DSLMapParser.literal_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set23=null;

        Object set23_tree=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:186:2: ( ( LITERAL | COLON | LEFT_SQUARE | RIGHT_SQUARE ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:186:4: ( LITERAL | COLON | LEFT_SQUARE | RIGHT_SQUARE )
            {
            root_0 = (Object)adaptor.nil();

            set23=(Token)input.LT(1);
            if ( (input.LA(1)>=LEFT_SQUARE && input.LA(1)<=LITERAL)||input.LA(1)==COLON ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set23));
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:190:1: variable_definition : lc= LEFT_CURLY name= LITERAL ( ( COLON q= LITERAL )? COLON pat= pattern )? rc= RIGHT_CURLY -> { hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> { hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) ;
    public final DSLMapParser.variable_definition_return variable_definition() throws RecognitionException {
        DSLMapParser.variable_definition_return retval = new DSLMapParser.variable_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lc=null;
        Token name=null;
        Token q=null;
        Token rc=null;
        Token COLON24=null;
        Token COLON25=null;
        DSLMapParser.pattern_return pat = null;


        Object lc_tree=null;
        Object name_tree=null;
        Object q_tree=null;
        Object rc_tree=null;
        Object COLON24_tree=null;
        Object COLON25_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
        RewriteRuleTokenStream stream_RIGHT_CURLY=new RewriteRuleTokenStream(adaptor,"token RIGHT_CURLY");
        RewriteRuleTokenStream stream_LEFT_CURLY=new RewriteRuleTokenStream(adaptor,"token LEFT_CURLY");
        RewriteRuleSubtreeStream stream_pattern=new RewriteRuleSubtreeStream(adaptor,"rule pattern");

                String text = "";
                boolean hasSpaceBefore = false;
                boolean hasSpaceAfter = false;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:196:2: (lc= LEFT_CURLY name= LITERAL ( ( COLON q= LITERAL )? COLON pat= pattern )? rc= RIGHT_CURLY -> { hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) -> { hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE -> ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:196:4: lc= LEFT_CURLY name= LITERAL ( ( COLON q= LITERAL )? COLON pat= pattern )? rc= RIGHT_CURLY
            {
            lc=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_definition682); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_LEFT_CURLY.add(lc);

            if ( state.backtracking==0 ) {
               
              		CommonToken back2 =  (CommonToken)input.LT(-2);
              		if( back2!=null && back2.getStopIndex() < ((CommonToken)lc).getStartIndex() -1 ) hasSpaceBefore = true;
              		
            }
            name=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition693); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_LITERAL.add(name);

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:201:15: ( ( COLON q= LITERAL )? COLON pat= pattern )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==COLON) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:201:17: ( COLON q= LITERAL )? COLON pat= pattern
                    {
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:201:17: ( COLON q= LITERAL )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==COLON) ) {
                        int LA14_1 = input.LA(2);

                        if ( (LA14_1==LITERAL) ) {
                            int LA14_2 = input.LA(3);

                            if ( (LA14_2==COLON) ) {
                                int LA14_4 = input.LA(4);

                                if ( (synpred21_DSLMap()) ) {
                                    alt14=1;
                                }
                            }
                        }
                    }
                    switch (alt14) {
                        case 1 :
                            // src/main/resources/org/drools/lang/dsl/DSLMap.g:201:18: COLON q= LITERAL
                            {
                            COLON24=(Token)match(input,COLON,FOLLOW_COLON_in_variable_definition698); if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_COLON.add(COLON24);

                            q=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition702); if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_LITERAL.add(q);


                            }
                            break;

                    }

                    COLON25=(Token)match(input,COLON,FOLLOW_COLON_in_variable_definition706); if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_COLON.add(COLON25);

                    pushFollow(FOLLOW_pattern_in_variable_definition710);
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

            rc=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_definition719); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_RIGHT_CURLY.add(rc);

            if ( state.backtracking==0 ) {

                    CommonToken rc1 = (CommonToken)input.LT(1);
                    if(!"=".equals(rc1.getText()) && ((CommonToken)rc).getStopIndex() < rc1.getStartIndex() - 1) hasSpaceAfter = true;
                    isIdentifier( name );
              	
            }


            // AST REWRITE
            // elements: q, name, name, name, q, name, q, name, name, q, name, name, name, q, q, q, q, q
            // token labels: q, name
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_q=new RewriteRuleTokenStream(adaptor,"token q",q);
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 208:2: -> { hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
            if ( hasSpaceBefore && !"".equals(text) && !hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:208:71: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:208:90: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:208:100: ( $q)?
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
            else // 209:2: -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
            if (!hasSpaceBefore && !"".equals(text) && !hasSpaceAfter) {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:209:71: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:209:90: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:209:100: ( $q)?
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
            else // 210:2: -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
            if ( hasSpaceBefore                     && !hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:210:71: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:210:90: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:210:100: ( $q)?
                if ( stream_q.hasNext() ) {
                    adaptor.addChild(root_2, stream_q.nextNode());

                }
                stream_q.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 211:2: -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
            if (!hasSpaceBefore                     && !hasSpaceAfter) {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:211:71: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:211:90: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:211:100: ( $q)?
                if ( stream_q.hasNext() ) {
                    adaptor.addChild(root_2, stream_q.nextNode());

                }
                stream_q.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 212:2: -> { hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE
            if ( hasSpaceBefore && !"".equals(text) &&  hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:212:71: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:212:90: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:212:100: ( $q)?
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
            else // 213:2: -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] ) VT_SPACE
            if (!hasSpaceBefore && !"".equals(text) &&  hasSpaceAfter) {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:213:71: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:213:90: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:213:100: ( $q)?
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
            else // 214:2: -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE
            if ( hasSpaceBefore &&                      hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:214:71: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:214:90: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:214:100: ( $q)?
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
            else // 215:2: -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) ) VT_SPACE
            if (!hasSpaceBefore &&                      hasSpaceAfter) {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:215:71: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:215:90: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:215:100: ( $q)?
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
            else // 216:2: -> ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:216:71: ^( VT_VAR_DEF $name ^( VT_QUAL ( $q)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:216:90: ^( VT_QUAL ( $q)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_QUAL, "VT_QUAL"), root_2);

                // src/main/resources/org/drools/lang/dsl/DSLMap.g:216:100: ( $q)?
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

    public static class pattern_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "pattern"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:219:1: pattern : ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+ ;
    public final DSLMapParser.pattern_return pattern() throws RecognitionException {
        DSLMapParser.pattern_return retval = new DSLMapParser.pattern_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_CURLY27=null;
        Token RIGHT_CURLY29=null;
        Token LEFT_SQUARE30=null;
        Token RIGHT_SQUARE32=null;
        DSLMapParser.literal_return literal26 = null;

        DSLMapParser.literal_return literal28 = null;

        DSLMapParser.pattern_return pattern31 = null;


        Object LEFT_CURLY27_tree=null;
        Object RIGHT_CURLY29_tree=null;
        Object LEFT_SQUARE30_tree=null;
        Object RIGHT_SQUARE32_tree=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:220:9: ( ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+ )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:220:11: ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+
            {
            root_0 = (Object)adaptor.nil();

            // src/main/resources/org/drools/lang/dsl/DSLMap.g:220:11: ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+
            int cnt16=0;
            loop16:
            do {
                int alt16=4;
                switch ( input.LA(1) ) {
                case RIGHT_SQUARE:
                    {
                    int LA16_2 = input.LA(2);

                    if ( (synpred23_DSLMap()) ) {
                        alt16=1;
                    }


                    }
                    break;
                case LEFT_SQUARE:
                    {
                    int LA16_3 = input.LA(2);

                    if ( (synpred23_DSLMap()) ) {
                        alt16=1;
                    }
                    else if ( (synpred25_DSLMap()) ) {
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
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:220:13: literal
            	    {
            	    pushFollow(FOLLOW_literal_in_pattern1126);
            	    literal26=literal();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal26.getTree());

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:221:13: LEFT_CURLY literal RIGHT_CURLY
            	    {
            	    LEFT_CURLY27=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_pattern1140); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    LEFT_CURLY27_tree = (Object)adaptor.create(LEFT_CURLY27);
            	    adaptor.addChild(root_0, LEFT_CURLY27_tree);
            	    }
            	    pushFollow(FOLLOW_literal_in_pattern1142);
            	    literal28=literal();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal28.getTree());
            	    RIGHT_CURLY29=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_pattern1144); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    RIGHT_CURLY29_tree = (Object)adaptor.create(RIGHT_CURLY29);
            	    adaptor.addChild(root_0, RIGHT_CURLY29_tree);
            	    }

            	    }
            	    break;
            	case 3 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:222:13: LEFT_SQUARE pattern RIGHT_SQUARE
            	    {
            	    LEFT_SQUARE30=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_pattern1158); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    LEFT_SQUARE30_tree = (Object)adaptor.create(LEFT_SQUARE30);
            	    adaptor.addChild(root_0, LEFT_SQUARE30_tree);
            	    }
            	    pushFollow(FOLLOW_pattern_in_pattern1160);
            	    pattern31=pattern();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, pattern31.getTree());
            	    RIGHT_SQUARE32=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_pattern1162); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    RIGHT_SQUARE32_tree = (Object)adaptor.create(RIGHT_SQUARE32);
            	    adaptor.addChild(root_0, RIGHT_SQUARE32_tree);
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:226:1: variable_reference : lc= LEFT_CURLY name= LITERAL rc= RIGHT_CURLY -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) VT_SPACE -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF $name) VT_SPACE -> ^( VT_VAR_REF $name) ;
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
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
        RewriteRuleTokenStream stream_RIGHT_CURLY=new RewriteRuleTokenStream(adaptor,"token RIGHT_CURLY");
        RewriteRuleTokenStream stream_LEFT_CURLY=new RewriteRuleTokenStream(adaptor,"token LEFT_CURLY");


                boolean hasSpaceBefore = false;
                boolean hasSpaceAfter = false;
                String text = "";

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:232:2: (lc= LEFT_CURLY name= LITERAL rc= RIGHT_CURLY -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) VT_SPACE -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF $name) VT_SPACE -> ^( VT_VAR_REF $name) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:232:4: lc= LEFT_CURLY name= LITERAL rc= RIGHT_CURLY
            {
            lc=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_reference1194); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_LEFT_CURLY.add(lc);

            if ( state.backtracking==0 ) {

              		CommonToken back2 =  (CommonToken)input.LT(-2);
              		if( back2!=null && back2.getStopIndex() < ((CommonToken)lc).getStartIndex() -1 ) hasSpaceBefore = true;
              		
            }
            name=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_variable_reference1205); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_LITERAL.add(name);

            rc=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_reference1209); if (state.failed) return retval;
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
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 239:2: -> { hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) VT_SPACE
            if ( hasSpaceBefore &&  hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:239:51: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 240:2: -> { hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name)
            if ( hasSpaceBefore && !hasSpaceAfter) {
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:240:51: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 241:2: -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF $name) VT_SPACE
            if (!hasSpaceBefore &&  hasSpaceAfter) {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:241:51: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, (Object)adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 242:2: -> ^( VT_VAR_REF $name)
            {
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:242:51: ^( VT_VAR_REF $name)
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

    public static class condition_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "condition_key"
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:245:1: condition_key : {...}?value= LITERAL -> VT_CONDITION[$value] ;
    public final DSLMapParser.condition_key_return condition_key() throws RecognitionException {
        DSLMapParser.condition_key_return retval = new DSLMapParser.condition_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:246:2: ({...}?value= LITERAL -> VT_CONDITION[$value] )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:246:4: {...}?value= LITERAL
            {
            if ( !((validateIdentifierKey("condition")||validateIdentifierKey("when"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "condition_key", "validateIdentifierKey(\"condition\")||validateIdentifierKey(\"when\")");
            }
            value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_condition_key1342); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_LITERAL.add(value);



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
            // 247:2: -> VT_CONDITION[$value]
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:250:1: consequence_key : {...}?value= LITERAL -> VT_CONSEQUENCE[$value] ;
    public final DSLMapParser.consequence_key_return consequence_key() throws RecognitionException {
        DSLMapParser.consequence_key_return retval = new DSLMapParser.consequence_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:251:2: ({...}?value= LITERAL -> VT_CONSEQUENCE[$value] )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:251:4: {...}?value= LITERAL
            {
            if ( !((validateIdentifierKey("consequence")||validateIdentifierKey("then"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "consequence_key", "validateIdentifierKey(\"consequence\")||validateIdentifierKey(\"then\")");
            }
            value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_consequence_key1365); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_LITERAL.add(value);



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
            // 252:2: -> VT_CONSEQUENCE[$value]
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:255:1: keyword_key : {...}?value= LITERAL -> VT_KEYWORD[$value] ;
    public final DSLMapParser.keyword_key_return keyword_key() throws RecognitionException {
        DSLMapParser.keyword_key_return retval = new DSLMapParser.keyword_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:256:2: ({...}?value= LITERAL -> VT_KEYWORD[$value] )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:256:4: {...}?value= LITERAL
            {
            if ( !((validateIdentifierKey("keyword"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "keyword_key", "validateIdentifierKey(\"keyword\")");
            }
            value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_keyword_key1388); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_LITERAL.add(value);



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
            // 257:2: -> VT_KEYWORD[$value]
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
    // src/main/resources/org/drools/lang/dsl/DSLMap.g:260:1: any_key : {...}?value= LITERAL -> VT_ANY[$value] ;
    public final DSLMapParser.any_key_return any_key() throws RecognitionException {
        DSLMapParser.any_key_return retval = new DSLMapParser.any_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:261:2: ({...}?value= LITERAL -> VT_ANY[$value] )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:261:4: {...}?value= LITERAL
            {
            if ( !((validateIdentifierKey("*"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "any_key", "validateIdentifierKey(\"*\")");
            }
            value=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_any_key1411); if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_LITERAL.add(value);



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
            // 262:2: -> VT_ANY[$value]
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

    // $ANTLR start synpred3_DSLMap
    public final void synpred3_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:24: ( meta_section )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:119:24: meta_section
        {
        pushFollow(FOLLOW_meta_section_in_synpred3_DSLMap322);
        meta_section();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_DSLMap

    // $ANTLR start synpred6_DSLMap
    public final void synpred6_DSLMap_fragment() throws RecognitionException {   
        DSLMapParser.condition_key_return value1 = null;


        // src/main/resources/org/drools/lang/dsl/DSLMap.g:132:4: (value1= condition_key )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:132:4: value1= condition_key
        {
        pushFollow(FOLLOW_condition_key_in_synpred6_DSLMap390);
        value1=condition_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_DSLMap

    // $ANTLR start synpred7_DSLMap
    public final void synpred7_DSLMap_fragment() throws RecognitionException {   
        DSLMapParser.consequence_key_return value2 = null;


        // src/main/resources/org/drools/lang/dsl/DSLMap.g:133:5: (value2= consequence_key )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:133:5: value2= consequence_key
        {
        pushFollow(FOLLOW_consequence_key_in_synpred7_DSLMap399);
        value2=consequence_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_DSLMap

    // $ANTLR start synpred8_DSLMap
    public final void synpred8_DSLMap_fragment() throws RecognitionException {   
        DSLMapParser.keyword_key_return value3 = null;


        // src/main/resources/org/drools/lang/dsl/DSLMap.g:134:5: (value3= keyword_key )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:134:5: value3= keyword_key
        {
        pushFollow(FOLLOW_keyword_key_in_synpred8_DSLMap407);
        value3=keyword_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_DSLMap

    // $ANTLR start synpred12_DSLMap
    public final void synpred12_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:164:4: ( literal )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:164:4: literal
        {
        pushFollow(FOLLOW_literal_in_synpred12_DSLMap551);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_DSLMap

    // $ANTLR start synpred15_DSLMap
    public final void synpred15_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:5: ( literal )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:5: literal
        {
        pushFollow(FOLLOW_literal_in_synpred15_DSLMap626);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_DSLMap

    // $ANTLR start synpred16_DSLMap
    public final void synpred16_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:13: ( EQUALS )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:13: EQUALS
        {
        match(input,EQUALS,FOLLOW_EQUALS_in_synpred16_DSLMap628); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred16_DSLMap

    // $ANTLR start synpred17_DSLMap
    public final void synpred17_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:20: ( COMMA )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:182:20: COMMA
        {
        match(input,COMMA,FOLLOW_COMMA_in_synpred17_DSLMap630); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred17_DSLMap

    // $ANTLR start synpred21_DSLMap
    public final void synpred21_DSLMap_fragment() throws RecognitionException {   
        Token q=null;

        // src/main/resources/org/drools/lang/dsl/DSLMap.g:201:18: ( COLON q= LITERAL )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:201:18: COLON q= LITERAL
        {
        match(input,COLON,FOLLOW_COLON_in_synpred21_DSLMap698); if (state.failed) return ;
        q=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_synpred21_DSLMap702); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred21_DSLMap

    // $ANTLR start synpred23_DSLMap
    public final void synpred23_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:220:13: ( literal )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:220:13: literal
        {
        pushFollow(FOLLOW_literal_in_synpred23_DSLMap1126);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred23_DSLMap

    // $ANTLR start synpred25_DSLMap
    public final void synpred25_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:222:13: ( LEFT_SQUARE pattern RIGHT_SQUARE )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:222:13: LEFT_SQUARE pattern RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred25_DSLMap1158); if (state.failed) return ;
        pushFollow(FOLLOW_pattern_in_synpred25_DSLMap1160);
        pattern();

        state._fsp--;
        if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred25_DSLMap1162); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred25_DSLMap

    // Delegated rules

    public final boolean synpred3_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_DSLMap_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred25_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred25_DSLMap_fragment(); // can never throw exception
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
    public final boolean synpred23_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred23_DSLMap_fragment(); // can never throw exception
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


 

    public static final BitSet FOLLOW_statement_in_mapping_file272 = new BitSet(new long[]{0x0000000000500002L});
    public static final BitSet FOLLOW_entry_in_statement295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOL_in_statement302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scope_section_in_entry320 = new BitSet(new long[]{0x000000000DC00000L});
    public static final BitSet FOLLOW_meta_section_in_entry322 = new BitSet(new long[]{0x000000000DC00000L});
    public static final BitSet FOLLOW_key_section_in_entry325 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_EQUALS_in_entry327 = new BitSet(new long[]{0x000000000FF00000L});
    public static final BitSet FOLLOW_value_section_in_entry329 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_EOL_in_entry333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOF_in_entry335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_scope_section382 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_condition_key_in_scope_section390 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_consequence_key_in_scope_section399 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_keyword_key_in_scope_section407 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_any_key_in_scope_section415 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_scope_section423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_meta_section463 = new BitSet(new long[]{0x0000000001800000L});
    public static final BitSet FOLLOW_LITERAL_in_meta_section465 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_meta_section468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_key_sentence_in_key_section492 = new BitSet(new long[]{0x000000000DC00002L});
    public static final BitSet FOLLOW_variable_definition_in_key_sentence523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_key_chunk_in_key_sentence530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_key_chunk551 = new BitSet(new long[]{0x000000000DC00002L});
    public static final BitSet FOLLOW_value_sentence_in_value_section566 = new BitSet(new long[]{0x000000000FE00002L});
    public static final BitSet FOLLOW_variable_reference_in_value_sentence597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_chunk_in_value_sentence604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_value_chunk626 = new BitSet(new long[]{0x000000000FE00002L});
    public static final BitSet FOLLOW_EQUALS_in_value_chunk628 = new BitSet(new long[]{0x000000000FE00002L});
    public static final BitSet FOLLOW_COMMA_in_value_chunk630 = new BitSet(new long[]{0x000000000FE00002L});
    public static final BitSet FOLLOW_set_in_literal646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_variable_definition682 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_definition693 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_COLON_in_variable_definition698 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_definition702 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_variable_definition706 = new BitSet(new long[]{0x000000000DC00000L});
    public static final BitSet FOLLOW_pattern_in_variable_definition710 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_definition719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_pattern1126 = new BitSet(new long[]{0x000000000DC00002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_pattern1140 = new BitSet(new long[]{0x000000000DC00000L});
    public static final BitSet FOLLOW_literal_in_pattern1142 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_pattern1144 = new BitSet(new long[]{0x000000000DC00002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_pattern1158 = new BitSet(new long[]{0x000000000DC00000L});
    public static final BitSet FOLLOW_pattern_in_pattern1160 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_pattern1162 = new BitSet(new long[]{0x000000000DC00002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_variable_reference1194 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_reference1205 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_reference1209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_condition_key1342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_consequence_key1365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_keyword_key1388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_any_key1411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_meta_section_in_synpred3_DSLMap322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_condition_key_in_synpred6_DSLMap390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_consequence_key_in_synpred7_DSLMap399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_key_in_synpred8_DSLMap407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred12_DSLMap551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred15_DSLMap626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_synpred16_DSLMap628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_synpred17_DSLMap630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_synpred21_DSLMap698 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_LITERAL_in_synpred21_DSLMap702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred23_DSLMap1126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred25_DSLMap1158 = new BitSet(new long[]{0x000000000DC00000L});
    public static final BitSet FOLLOW_pattern_in_synpred25_DSLMap1160 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred25_DSLMap1162 = new BitSet(new long[]{0x0000000000000002L});

}
