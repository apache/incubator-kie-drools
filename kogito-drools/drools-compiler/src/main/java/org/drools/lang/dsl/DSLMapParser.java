// $ANTLR 3.0.1 DSLMap.g 2008-05-27 14:03:44

	package org.drools.lang.dsl;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class DSLMapParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_DSL_GRAMMAR", "VT_COMMENT", "VT_ENTRY", "VT_SCOPE", "VT_CONDITION", "VT_CONSEQUENCE", "VT_KEYWORD", "VT_ANY", "VT_META", "VT_ENTRY_KEY", "VT_ENTRY_VAL", "VT_VAR_DEF", "VT_VAR_REF", "VT_LITERAL", "VT_PATTERN", "VT_SPACE", "EOL", "LINE_COMMENT", "EQUALS", "LEFT_SQUARE", "RIGHT_SQUARE", "LITERAL", "COMMA", "COLON", "LEFT_CURLY", "RIGHT_CURLY", "WS", "EscapeSequence", "DOT", "POUND", "MISC"
    };
    public static final int COMMA=26;
    public static final int RIGHT_CURLY=29;
    public static final int VT_ENTRY_VAL=14;
    public static final int WS=30;
    public static final int MISC=34;
    public static final int VT_META=12;
    public static final int VT_CONSEQUENCE=9;
    public static final int VT_SPACE=19;
    public static final int LINE_COMMENT=21;
    public static final int VT_ANY=11;
    public static final int VT_LITERAL=17;
    public static final int DOT=32;
    public static final int EQUALS=22;
    public static final int VT_DSL_GRAMMAR=4;
    public static final int VT_CONDITION=8;
    public static final int VT_ENTRY=6;
    public static final int VT_VAR_DEF=15;
    public static final int LITERAL=25;
    public static final int VT_PATTERN=18;
    public static final int EscapeSequence=31;
    public static final int VT_COMMENT=5;
    public static final int EOF=-1;
    public static final int EOL=20;
    public static final int LEFT_SQUARE=23;
    public static final int VT_ENTRY_KEY=13;
    public static final int COLON=27;
    public static final int VT_SCOPE=7;
    public static final int VT_KEYWORD=10;
    public static final int POUND=33;
    public static final int LEFT_CURLY=28;
    public static final int VT_VAR_REF=16;
    public static final int RIGHT_SQUARE=24;

        public DSLMapParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[47+1];
         }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "DSLMap.g"; }

    
    //we may not need the check on [], as the LITERAL token being examined 
    //should not have them.
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
    	
    	protected void mismatch(IntStream input, int ttype, BitSet follow)
    		throws RecognitionException{
    		throw new MismatchedTokenException(ttype, input);
    	}
    
    	public void recoverFromMismatchedSet(IntStream input,
    		RecognitionException e, BitSet follow) throws RecognitionException{
    		throw e;
    	}
    	


    public static class mapping_file_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start mapping_file
    // DSLMap.g:76:1: mapping_file : ( statement )* -> ^( VT_DSL_GRAMMAR ( statement )* ) ;
    public final mapping_file_return mapping_file() throws RecognitionException {
        mapping_file_return retval = new mapping_file_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        statement_return statement1 = null;


        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // DSLMap.g:77:2: ( ( statement )* -> ^( VT_DSL_GRAMMAR ( statement )* ) )
            // DSLMap.g:77:4: ( statement )*
            {
            // DSLMap.g:77:4: ( statement )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=EOL && LA1_0<=LINE_COMMENT)||LA1_0==LEFT_SQUARE) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // DSLMap.g:0:0: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_mapping_file262);
            	    statement1=statement();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_statement.add(statement1.getTree());

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
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 78:2: -> ^( VT_DSL_GRAMMAR ( statement )* )
            {
                // DSLMap.g:78:5: ^( VT_DSL_GRAMMAR ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_DSL_GRAMMAR, "VT_DSL_GRAMMAR"), root_1);

                // DSLMap.g:78:22: ( statement )*
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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end mapping_file

    public static class statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start statement
    // DSLMap.g:81:1: statement : ( entry | comment | EOL );
    public final statement_return statement() throws RecognitionException {
        statement_return retval = new statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOL4=null;
        entry_return entry2 = null;

        comment_return comment3 = null;


        Object EOL4_tree=null;

        try {
            // DSLMap.g:82:2: ( entry | comment | EOL )
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
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("81:1: statement : ( entry | comment | EOL );", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // DSLMap.g:82:4: entry
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_entry_in_statement285);
                    entry2=entry();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, entry2.getTree());

                    }
                    break;
                case 2 :
                    // DSLMap.g:83:4: comment
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_comment_in_statement292);
                    comment3=comment();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, comment3.getTree());

                    }
                    break;
                case 3 :
                    // DSLMap.g:84:4: EOL
                    {
                    root_0 = (Object)adaptor.nil();

                    EOL4=(Token)input.LT(1);
                    match(input,EOL,FOLLOW_EOL_in_statement298); if (failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end statement

    public static class comment_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start comment
    // DSLMap.g:89:1: comment : LINE_COMMENT -> ^( VT_COMMENT[$LINE_COMMENT, \"COMMENT\"] LINE_COMMENT ) ;
    public final comment_return comment() throws RecognitionException {
        comment_return retval = new comment_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LINE_COMMENT5=null;

        Object LINE_COMMENT5_tree=null;
        RewriteRuleTokenStream stream_LINE_COMMENT=new RewriteRuleTokenStream(adaptor,"token LINE_COMMENT");

        try {
            // DSLMap.g:89:9: ( LINE_COMMENT -> ^( VT_COMMENT[$LINE_COMMENT, \"COMMENT\"] LINE_COMMENT ) )
            // DSLMap.g:89:11: LINE_COMMENT
            {
            LINE_COMMENT5=(Token)input.LT(1);
            match(input,LINE_COMMENT,FOLLOW_LINE_COMMENT_in_comment314); if (failed) return retval;
            if ( backtracking==0 ) stream_LINE_COMMENT.add(LINE_COMMENT5);


            // AST REWRITE
            // elements: LINE_COMMENT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 90:2: -> ^( VT_COMMENT[$LINE_COMMENT, \"COMMENT\"] LINE_COMMENT )
            {
                // DSLMap.g:90:5: ^( VT_COMMENT[$LINE_COMMENT, \"COMMENT\"] LINE_COMMENT )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_COMMENT, LINE_COMMENT5,  "COMMENT"), root_1);

                adaptor.addChild(root_1, stream_LINE_COMMENT.next());

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end comment

    public static class entry_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start entry
    // DSLMap.g:94:1: entry : scope_section ( meta_section )? key_section EQUALS value_section ( EOL | EOF ) -> ^( VT_ENTRY scope_section ( meta_section )? key_section value_section ) ;
    public final entry_return entry() throws RecognitionException {
        entry_return retval = new entry_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS9=null;
        Token EOL11=null;
        Token EOF12=null;
        scope_section_return scope_section6 = null;

        meta_section_return meta_section7 = null;

        key_section_return key_section8 = null;

        value_section_return value_section10 = null;


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
            // DSLMap.g:94:8: ( scope_section ( meta_section )? key_section EQUALS value_section ( EOL | EOF ) -> ^( VT_ENTRY scope_section ( meta_section )? key_section value_section ) )
            // DSLMap.g:94:10: scope_section ( meta_section )? key_section EQUALS value_section ( EOL | EOF )
            {
            pushFollow(FOLLOW_scope_section_in_entry339);
            scope_section6=scope_section();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_scope_section.add(scope_section6.getTree());
            // DSLMap.g:94:24: ( meta_section )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==LEFT_SQUARE) ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1==LITERAL) ) {
                    int LA3_3 = input.LA(3);

                    if ( (LA3_3==RIGHT_SQUARE) ) {
                        int LA3_4 = input.LA(4);

                        if ( (synpred4()) ) {
                            alt3=1;
                        }
                    }
                }
                else if ( (LA3_1==RIGHT_SQUARE) ) {
                    int LA3_4 = input.LA(3);

                    if ( (synpred4()) ) {
                        alt3=1;
                    }
                }
            }
            switch (alt3) {
                case 1 :
                    // DSLMap.g:0:0: meta_section
                    {
                    pushFollow(FOLLOW_meta_section_in_entry341);
                    meta_section7=meta_section();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_meta_section.add(meta_section7.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_key_section_in_entry344);
            key_section8=key_section();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_key_section.add(key_section8.getTree());
            EQUALS9=(Token)input.LT(1);
            match(input,EQUALS,FOLLOW_EQUALS_in_entry346); if (failed) return retval;
            if ( backtracking==0 ) stream_EQUALS.add(EQUALS9);

            pushFollow(FOLLOW_value_section_in_entry348);
            value_section10=value_section();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_value_section.add(value_section10.getTree());
            // DSLMap.g:94:71: ( EOL | EOF )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==EOL) ) {
                alt4=1;
            }
            else if ( (LA4_0==EOF) ) {
                alt4=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("94:71: ( EOL | EOF )", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // DSLMap.g:94:72: EOL
                    {
                    EOL11=(Token)input.LT(1);
                    match(input,EOL,FOLLOW_EOL_in_entry351); if (failed) return retval;
                    if ( backtracking==0 ) stream_EOL.add(EOL11);


                    }
                    break;
                case 2 :
                    // DSLMap.g:94:76: EOF
                    {
                    EOF12=(Token)input.LT(1);
                    match(input,EOF,FOLLOW_EOF_in_entry353); if (failed) return retval;
                    if ( backtracking==0 ) stream_EOF.add(EOF12);


                    }
                    break;

            }


            // AST REWRITE
            // elements: key_section, value_section, scope_section, meta_section
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 95:2: -> ^( VT_ENTRY scope_section ( meta_section )? key_section value_section )
            {
                // DSLMap.g:95:5: ^( VT_ENTRY scope_section ( meta_section )? key_section value_section )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ENTRY, "VT_ENTRY"), root_1);

                adaptor.addChild(root_1, stream_scope_section.next());
                // DSLMap.g:95:30: ( meta_section )?
                if ( stream_meta_section.hasNext() ) {
                    adaptor.addChild(root_1, stream_meta_section.next());

                }
                stream_meta_section.reset();
                adaptor.addChild(root_1, stream_key_section.next());
                adaptor.addChild(root_1, stream_value_section.next());

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end entry

    public static class scope_section_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start scope_section
    // DSLMap.g:100:1: scope_section : LEFT_SQUARE (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key ) RIGHT_SQUARE -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? ) ;
    public final scope_section_return scope_section() throws RecognitionException {
        scope_section_return retval = new scope_section_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE13=null;
        Token RIGHT_SQUARE14=null;
        condition_key_return value1 = null;

        consequence_key_return value2 = null;

        keyword_key_return value3 = null;

        any_key_return value4 = null;


        Object LEFT_SQUARE13_tree=null;
        Object RIGHT_SQUARE14_tree=null;
        RewriteRuleTokenStream stream_LEFT_SQUARE=new RewriteRuleTokenStream(adaptor,"token LEFT_SQUARE");
        RewriteRuleTokenStream stream_RIGHT_SQUARE=new RewriteRuleTokenStream(adaptor,"token RIGHT_SQUARE");
        RewriteRuleSubtreeStream stream_condition_key=new RewriteRuleSubtreeStream(adaptor,"rule condition_key");
        RewriteRuleSubtreeStream stream_any_key=new RewriteRuleSubtreeStream(adaptor,"rule any_key");
        RewriteRuleSubtreeStream stream_keyword_key=new RewriteRuleSubtreeStream(adaptor,"rule keyword_key");
        RewriteRuleSubtreeStream stream_consequence_key=new RewriteRuleSubtreeStream(adaptor,"rule consequence_key");
        try {
            // DSLMap.g:101:2: ( LEFT_SQUARE (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key ) RIGHT_SQUARE -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? ) )
            // DSLMap.g:101:4: LEFT_SQUARE (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key ) RIGHT_SQUARE
            {
            LEFT_SQUARE13=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_scope_section384); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_SQUARE.add(LEFT_SQUARE13);

            // DSLMap.g:102:3: (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key )
            int alt5=4;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==LITERAL) ) {
                int LA5_1 = input.LA(2);

                if ( ((synpred6()&&validateIdentifierKey("condition")||validateIdentifierKey("when"))) ) {
                    alt5=1;
                }
                else if ( ((synpred7()&&validateIdentifierKey("consequence")||validateIdentifierKey("then"))) ) {
                    alt5=2;
                }
                else if ( ((synpred8()&&validateIdentifierKey("keyword"))) ) {
                    alt5=3;
                }
                else if ( (validateIdentifierKey("*")) ) {
                    alt5=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("102:3: (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key )", 5, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("102:3: (value1= condition_key | value2= consequence_key | value3= keyword_key | value4= any_key )", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // DSLMap.g:102:4: value1= condition_key
                    {
                    pushFollow(FOLLOW_condition_key_in_scope_section392);
                    value1=condition_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_condition_key.add(value1.getTree());

                    }
                    break;
                case 2 :
                    // DSLMap.g:103:5: value2= consequence_key
                    {
                    pushFollow(FOLLOW_consequence_key_in_scope_section401);
                    value2=consequence_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_consequence_key.add(value2.getTree());

                    }
                    break;
                case 3 :
                    // DSLMap.g:104:5: value3= keyword_key
                    {
                    pushFollow(FOLLOW_keyword_key_in_scope_section409);
                    value3=keyword_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_keyword_key.add(value3.getTree());

                    }
                    break;
                case 4 :
                    // DSLMap.g:105:5: value4= any_key
                    {
                    pushFollow(FOLLOW_any_key_in_scope_section417);
                    value4=any_key();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_any_key.add(value4.getTree());

                    }
                    break;

            }

            RIGHT_SQUARE14=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_scope_section425); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_SQUARE.add(RIGHT_SQUARE14);


            // AST REWRITE
            // elements: value1, value2, value4, value3
            // token labels: 
            // rule labels: value1, value4, value2, retval, value3
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_value1=new RewriteRuleSubtreeStream(adaptor,"token value1",value1!=null?value1.tree:null);
            RewriteRuleSubtreeStream stream_value4=new RewriteRuleSubtreeStream(adaptor,"token value4",value4!=null?value4.tree:null);
            RewriteRuleSubtreeStream stream_value2=new RewriteRuleSubtreeStream(adaptor,"token value2",value2!=null?value2.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_value3=new RewriteRuleSubtreeStream(adaptor,"token value3",value3!=null?value3.tree:null);

            root_0 = (Object)adaptor.nil();
            // 108:2: -> ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? )
            {
                // DSLMap.g:108:5: ^( VT_SCOPE[$LEFT_SQUARE, \"SCOPE SECTION\"] ( $value1)? ( $value2)? ( $value3)? ( $value4)? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_SCOPE, LEFT_SQUARE13,  "SCOPE SECTION"), root_1);

                // DSLMap.g:108:47: ( $value1)?
                if ( stream_value1.hasNext() ) {
                    adaptor.addChild(root_1, stream_value1.next());

                }
                stream_value1.reset();
                // DSLMap.g:108:56: ( $value2)?
                if ( stream_value2.hasNext() ) {
                    adaptor.addChild(root_1, stream_value2.next());

                }
                stream_value2.reset();
                // DSLMap.g:108:65: ( $value3)?
                if ( stream_value3.hasNext() ) {
                    adaptor.addChild(root_1, stream_value3.next());

                }
                stream_value3.reset();
                // DSLMap.g:108:74: ( $value4)?
                if ( stream_value4.hasNext() ) {
                    adaptor.addChild(root_1, stream_value4.next());

                }
                stream_value4.reset();

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end scope_section

    public static class meta_section_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start meta_section
    // DSLMap.g:113:1: meta_section : LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? ) ;
    public final meta_section_return meta_section() throws RecognitionException {
        meta_section_return retval = new meta_section_return();
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
            // DSLMap.g:114:2: ( LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? ) )
            // DSLMap.g:114:4: LEFT_SQUARE ( LITERAL )? RIGHT_SQUARE
            {
            LEFT_SQUARE15=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_meta_section463); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_SQUARE.add(LEFT_SQUARE15);

            // DSLMap.g:114:16: ( LITERAL )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==LITERAL) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // DSLMap.g:0:0: LITERAL
                    {
                    LITERAL16=(Token)input.LT(1);
                    match(input,LITERAL,FOLLOW_LITERAL_in_meta_section465); if (failed) return retval;
                    if ( backtracking==0 ) stream_LITERAL.add(LITERAL16);


                    }
                    break;

            }

            RIGHT_SQUARE17=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_meta_section468); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_SQUARE.add(RIGHT_SQUARE17);


            // AST REWRITE
            // elements: LITERAL
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 115:2: -> ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? )
            {
                // DSLMap.g:115:5: ^( VT_META[$LEFT_SQUARE, \"META SECTION\"] ( LITERAL )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_META, LEFT_SQUARE15,  "META SECTION"), root_1);

                // DSLMap.g:115:45: ( LITERAL )?
                if ( stream_LITERAL.hasNext() ) {
                    adaptor.addChild(root_1, stream_LITERAL.next());

                }
                stream_LITERAL.reset();

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end meta_section

    public static class key_section_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start key_section
    // DSLMap.g:118:1: key_section : (ks= key_sentence )+ -> ^( VT_ENTRY_KEY ( key_sentence )+ ) ;
    public final key_section_return key_section() throws RecognitionException {
        key_section_return retval = new key_section_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        key_sentence_return ks = null;


        RewriteRuleSubtreeStream stream_key_sentence=new RewriteRuleSubtreeStream(adaptor,"rule key_sentence");
        try {
            // DSLMap.g:119:2: ( (ks= key_sentence )+ -> ^( VT_ENTRY_KEY ( key_sentence )+ ) )
            // DSLMap.g:119:4: (ks= key_sentence )+
            {
            // DSLMap.g:119:6: (ks= key_sentence )+
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
            	    // DSLMap.g:0:0: ks= key_sentence
            	    {
            	    pushFollow(FOLLOW_key_sentence_in_key_section492);
            	    ks=key_sentence();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_key_sentence.add(ks.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
            	    if (backtracking>0) {failed=true; return retval;}
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
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 120:2: -> ^( VT_ENTRY_KEY ( key_sentence )+ )
            {
                // DSLMap.g:120:5: ^( VT_ENTRY_KEY ( key_sentence )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ENTRY_KEY, "VT_ENTRY_KEY"), root_1);

                if ( !(stream_key_sentence.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_key_sentence.hasNext() ) {
                    adaptor.addChild(root_1, stream_key_sentence.next());

                }
                stream_key_sentence.reset();

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end key_section

    public static class key_sentence_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start key_sentence
    // DSLMap.g:123:1: key_sentence : ( variable_definition | cb= key_chunk -> VT_LITERAL[$cb.start, text] );
    public final key_sentence_return key_sentence() throws RecognitionException {
        key_sentence_return retval = new key_sentence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        key_chunk_return cb = null;

        variable_definition_return variable_definition18 = null;


        RewriteRuleSubtreeStream stream_key_chunk=new RewriteRuleSubtreeStream(adaptor,"rule key_chunk");
        
                String text = "";

        try {
            // DSLMap.g:127:2: ( variable_definition | cb= key_chunk -> VT_LITERAL[$cb.start, text] )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==LEFT_CURLY) ) {
                alt8=1;
            }
            else if ( ((LA8_0>=LEFT_SQUARE && LA8_0<=LITERAL)||LA8_0==COLON) ) {
                alt8=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("123:1: key_sentence : ( variable_definition | cb= key_chunk -> VT_LITERAL[$cb.start, text] );", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // DSLMap.g:127:4: variable_definition
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_variable_definition_in_key_sentence523);
                    variable_definition18=variable_definition();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, variable_definition18.getTree());

                    }
                    break;
                case 2 :
                    // DSLMap.g:128:4: cb= key_chunk
                    {
                    pushFollow(FOLLOW_key_chunk_in_key_sentence530);
                    cb=key_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_key_chunk.add(cb.getTree());
                    if ( backtracking==0 ) {
                       text = input.toString(cb.start,cb.stop);
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
                    // 129:2: -> VT_LITERAL[$cb.start, text]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_LITERAL, ((Token)cb.start),  text));

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end key_sentence

    public static class key_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start key_chunk
    // DSLMap.g:132:1: key_chunk : ( literal )+ ;
    public final key_chunk_return key_chunk() throws RecognitionException {
        key_chunk_return retval = new key_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        literal_return literal19 = null;



        try {
            // DSLMap.g:133:2: ( ( literal )+ )
            // DSLMap.g:133:4: ( literal )+
            {
            root_0 = (Object)adaptor.nil();

            // DSLMap.g:133:4: ( literal )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>=LEFT_SQUARE && LA9_0<=LITERAL)||LA9_0==COLON) ) {
                    int LA9_2 = input.LA(2);

                    if ( (synpred12()) ) {
                        alt9=1;
                    }


                }


                switch (alt9) {
            	case 1 :
            	    // DSLMap.g:0:0: literal
            	    {
            	    pushFollow(FOLLOW_literal_in_key_chunk551);
            	    literal19=literal();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, literal19.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
            	    if (backtracking>0) {failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end key_chunk

    public static class value_section_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start value_section
    // DSLMap.g:136:1: value_section : ( value_sentence )+ -> ^( VT_ENTRY_VAL ( value_sentence )+ ) ;
    public final value_section_return value_section() throws RecognitionException {
        value_section_return retval = new value_section_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        value_sentence_return value_sentence20 = null;


        RewriteRuleSubtreeStream stream_value_sentence=new RewriteRuleSubtreeStream(adaptor,"rule value_sentence");
        try {
            // DSLMap.g:137:2: ( ( value_sentence )+ -> ^( VT_ENTRY_VAL ( value_sentence )+ ) )
            // DSLMap.g:137:4: ( value_sentence )+
            {
            // DSLMap.g:137:4: ( value_sentence )+
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
            	    // DSLMap.g:0:0: value_sentence
            	    {
            	    pushFollow(FOLLOW_value_sentence_in_value_section566);
            	    value_sentence20=value_sentence();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_value_sentence.add(value_sentence20.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
            	    if (backtracking>0) {failed=true; return retval;}
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
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 138:2: -> ^( VT_ENTRY_VAL ( value_sentence )+ )
            {
                // DSLMap.g:138:5: ^( VT_ENTRY_VAL ( value_sentence )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_ENTRY_VAL, "VT_ENTRY_VAL"), root_1);

                if ( !(stream_value_sentence.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_value_sentence.hasNext() ) {
                    adaptor.addChild(root_1, stream_value_sentence.next());

                }
                stream_value_sentence.reset();

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end value_section

    public static class value_sentence_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start value_sentence
    // DSLMap.g:141:1: value_sentence : ( variable_reference | vc= value_chunk -> VT_LITERAL[$vc.start, text] );
    public final value_sentence_return value_sentence() throws RecognitionException {
        value_sentence_return retval = new value_sentence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        value_chunk_return vc = null;

        variable_reference_return variable_reference21 = null;


        RewriteRuleSubtreeStream stream_value_chunk=new RewriteRuleSubtreeStream(adaptor,"rule value_chunk");
        
                String text = "";

        try {
            // DSLMap.g:145:2: ( variable_reference | vc= value_chunk -> VT_LITERAL[$vc.start, text] )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==LEFT_CURLY) ) {
                alt11=1;
            }
            else if ( ((LA11_0>=EQUALS && LA11_0<=COLON)) ) {
                alt11=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("141:1: value_sentence : ( variable_reference | vc= value_chunk -> VT_LITERAL[$vc.start, text] );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // DSLMap.g:145:4: variable_reference
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_variable_reference_in_value_sentence597);
                    variable_reference21=variable_reference();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, variable_reference21.getTree());

                    }
                    break;
                case 2 :
                    // DSLMap.g:146:4: vc= value_chunk
                    {
                    pushFollow(FOLLOW_value_chunk_in_value_sentence604);
                    vc=value_chunk();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_value_chunk.add(vc.getTree());
                    if ( backtracking==0 ) {
                       text = input.toString(vc.start,vc.stop); 
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
                    // 147:2: -> VT_LITERAL[$vc.start, text]
                    {
                        adaptor.addChild(root_0, adaptor.create(VT_LITERAL, ((Token)vc.start),  text));

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end value_sentence

    public static class value_chunk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start value_chunk
    // DSLMap.g:150:1: value_chunk : ( literal | EQUALS | COMMA )+ ;
    public final value_chunk_return value_chunk() throws RecognitionException {
        value_chunk_return retval = new value_chunk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS23=null;
        Token COMMA24=null;
        literal_return literal22 = null;


        Object EQUALS23_tree=null;
        Object COMMA24_tree=null;

        try {
            // DSLMap.g:151:2: ( ( literal | EQUALS | COMMA )+ )
            // DSLMap.g:151:4: ( literal | EQUALS | COMMA )+
            {
            root_0 = (Object)adaptor.nil();

            // DSLMap.g:151:4: ( literal | EQUALS | COMMA )+
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

                    if ( (synpred15()) ) {
                        alt12=1;
                    }


                    }
                    break;
                case EQUALS:
                    {
                    int LA12_3 = input.LA(2);

                    if ( (synpred16()) ) {
                        alt12=2;
                    }


                    }
                    break;
                case COMMA:
                    {
                    int LA12_4 = input.LA(2);

                    if ( (synpred17()) ) {
                        alt12=3;
                    }


                    }
                    break;

                }

                switch (alt12) {
            	case 1 :
            	    // DSLMap.g:151:5: literal
            	    {
            	    pushFollow(FOLLOW_literal_in_value_chunk626);
            	    literal22=literal();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, literal22.getTree());

            	    }
            	    break;
            	case 2 :
            	    // DSLMap.g:151:13: EQUALS
            	    {
            	    EQUALS23=(Token)input.LT(1);
            	    match(input,EQUALS,FOLLOW_EQUALS_in_value_chunk628); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    EQUALS23_tree = (Object)adaptor.create(EQUALS23);
            	    adaptor.addChild(root_0, EQUALS23_tree);
            	    }

            	    }
            	    break;
            	case 3 :
            	    // DSLMap.g:151:20: COMMA
            	    {
            	    COMMA24=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_value_chunk630); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    COMMA24_tree = (Object)adaptor.create(COMMA24);
            	    adaptor.addChild(root_0, COMMA24_tree);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
            	    if (backtracking>0) {failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end value_chunk

    public static class literal_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start literal
    // DSLMap.g:154:1: literal : ( LITERAL | COLON | LEFT_SQUARE | RIGHT_SQUARE ) ;
    public final literal_return literal() throws RecognitionException {
        literal_return retval = new literal_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set25=null;

        Object set25_tree=null;

        try {
            // DSLMap.g:156:2: ( ( LITERAL | COLON | LEFT_SQUARE | RIGHT_SQUARE ) )
            // DSLMap.g:156:4: ( LITERAL | COLON | LEFT_SQUARE | RIGHT_SQUARE )
            {
            root_0 = (Object)adaptor.nil();

            set25=(Token)input.LT(1);
            if ( (input.LA(1)>=LEFT_SQUARE && input.LA(1)<=LITERAL)||input.LA(1)==COLON ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set25));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_literal648);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end literal

    public static class variable_definition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start variable_definition
    // DSLMap.g:160:1: variable_definition : lc= LEFT_CURLY name= LITERAL ( COLON pat= pattern )? rc= RIGHT_CURLY -> {hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name) -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name) -> {hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) VT_SPACE -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) VT_SPACE -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name) VT_SPACE -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name) VT_SPACE -> ^( VT_VAR_DEF $name) ;
    public final variable_definition_return variable_definition() throws RecognitionException {
        variable_definition_return retval = new variable_definition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lc=null;
        Token name=null;
        Token rc=null;
        Token COLON26=null;
        pattern_return pat = null;


        Object lc_tree=null;
        Object name_tree=null;
        Object rc_tree=null;
        Object COLON26_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_RIGHT_CURLY=new RewriteRuleTokenStream(adaptor,"token RIGHT_CURLY");
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
        RewriteRuleTokenStream stream_LEFT_CURLY=new RewriteRuleTokenStream(adaptor,"token LEFT_CURLY");
        RewriteRuleSubtreeStream stream_pattern=new RewriteRuleSubtreeStream(adaptor,"rule pattern");
        
                String text = "";
                boolean hasSpaceBefore = false;
                boolean hasSpaceAfter = false;

        try {
            // DSLMap.g:166:2: (lc= LEFT_CURLY name= LITERAL ( COLON pat= pattern )? rc= RIGHT_CURLY -> {hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name) -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name) -> {hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) VT_SPACE -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) VT_SPACE -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name) VT_SPACE -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name) VT_SPACE -> ^( VT_VAR_DEF $name) )
            // DSLMap.g:166:4: lc= LEFT_CURLY name= LITERAL ( COLON pat= pattern )? rc= RIGHT_CURLY
            {
            lc=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_definition684); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_CURLY.add(lc);

            if ( backtracking==0 ) {
               
              		CommonToken back2 =  (CommonToken)input.LT(-2);
              		if( back2!=null && back2.getStopIndex() < ((CommonToken)lc).getStartIndex() -1 ) hasSpaceBefore = true; 
              		
            }
            name=(Token)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition695); if (failed) return retval;
            if ( backtracking==0 ) stream_LITERAL.add(name);

            // DSLMap.g:171:15: ( COLON pat= pattern )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==COLON) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // DSLMap.g:171:17: COLON pat= pattern
                    {
                    COLON26=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_variable_definition699); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON26);

                    pushFollow(FOLLOW_pattern_in_variable_definition703);
                    pat=pattern();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_pattern.add(pat.getTree());
                    if ( backtracking==0 ) {
                      text = input.toString(pat.start,pat.stop);
                    }

                    }
                    break;

            }

            rc=(Token)input.LT(1);
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_definition712); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_CURLY.add(rc);

            if ( backtracking==0 ) {
              
              	CommonToken rc1 = (CommonToken)input.LT(1);
              	if(!"=".equals(rc1.getText()) && ((CommonToken)rc).getStopIndex() < rc1.getStartIndex() - 1) hasSpaceAfter = true;
              	
            }

            // AST REWRITE
            // elements: name, name, name, name, name, name, name, name, name
            // token labels: name
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 176:2: -> {hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] )
            if (hasSpaceBefore && !"".equals(text) && !hasSpaceAfter) {
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));
                // DSLMap.g:176:70: ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.next());
                adaptor.addChild(root_1, adaptor.create(VT_PATTERN, ((Token)pat.start),  text));

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 177:2: -> {!hasSpaceBefore && !\"\".equals(text) && !hasSpaceAfter}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] )
            if (!hasSpaceBefore && !"".equals(text)  && !hasSpaceAfter) {
                // DSLMap.g:177:63: ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.next());
                adaptor.addChild(root_1, adaptor.create(VT_PATTERN, ((Token)pat.start),  text));

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 178:2: -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name)
            if (hasSpaceBefore  && !hasSpaceAfter) {
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));
                // DSLMap.g:178:51: ^( VT_VAR_DEF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.next());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 179:2: -> {!hasSpaceBefore && !hasSpaceAfter}? ^( VT_VAR_DEF $name)
            if (!hasSpaceBefore  && !hasSpaceAfter) {
                // DSLMap.g:179:44: ^( VT_VAR_DEF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.next());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 181:2: -> {hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) VT_SPACE
            if (hasSpaceBefore && !"".equals(text) && hasSpaceAfter) {
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));
                // DSLMap.g:181:69: ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.next());
                adaptor.addChild(root_1, adaptor.create(VT_PATTERN, ((Token)pat.start),  text));

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 182:2: -> {!hasSpaceBefore && !\"\".equals(text) && hasSpaceAfter}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) VT_SPACE
            if (!hasSpaceBefore && !"".equals(text)  && hasSpaceAfter) {
                // DSLMap.g:182:62: ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.next());
                adaptor.addChild(root_1, adaptor.create(VT_PATTERN, ((Token)pat.start),  text));

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 183:2: -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_DEF $name) VT_SPACE
            if (hasSpaceBefore  && hasSpaceAfter) {
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));
                // DSLMap.g:183:50: ^( VT_VAR_DEF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.next());

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 184:2: -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_DEF $name) VT_SPACE
            if (!hasSpaceBefore  && hasSpaceAfter) {
                // DSLMap.g:184:43: ^( VT_VAR_DEF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.next());

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 185:2: -> ^( VT_VAR_DEF $name)
            {
                // DSLMap.g:185:5: ^( VT_VAR_DEF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.next());

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end variable_definition

    public static class variable_definition2_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start variable_definition2
    // DSLMap.g:188:1: variable_definition2 : LEFT_CURLY name= LITERAL ( COLON pat= pattern )? RIGHT_CURLY -> {!\"\".equals(text)}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) -> ^( VT_VAR_DEF $name) ;
    public final variable_definition2_return variable_definition2() throws RecognitionException {
        variable_definition2_return retval = new variable_definition2_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token LEFT_CURLY27=null;
        Token COLON28=null;
        Token RIGHT_CURLY29=null;
        pattern_return pat = null;


        Object name_tree=null;
        Object LEFT_CURLY27_tree=null;
        Object COLON28_tree=null;
        Object RIGHT_CURLY29_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_RIGHT_CURLY=new RewriteRuleTokenStream(adaptor,"token RIGHT_CURLY");
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
        RewriteRuleTokenStream stream_LEFT_CURLY=new RewriteRuleTokenStream(adaptor,"token LEFT_CURLY");
        RewriteRuleSubtreeStream stream_pattern=new RewriteRuleSubtreeStream(adaptor,"rule pattern");
        
                String text = "";

        try {
            // DSLMap.g:192:2: ( LEFT_CURLY name= LITERAL ( COLON pat= pattern )? RIGHT_CURLY -> {!\"\".equals(text)}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] ) -> ^( VT_VAR_DEF $name) )
            // DSLMap.g:192:4: LEFT_CURLY name= LITERAL ( COLON pat= pattern )? RIGHT_CURLY
            {
            LEFT_CURLY27=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_definition2888); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_CURLY.add(LEFT_CURLY27);

            name=(Token)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition2892); if (failed) return retval;
            if ( backtracking==0 ) stream_LITERAL.add(name);

            // DSLMap.g:192:28: ( COLON pat= pattern )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==COLON) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // DSLMap.g:192:30: COLON pat= pattern
                    {
                    COLON28=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_variable_definition2896); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON28);

                    pushFollow(FOLLOW_pattern_in_variable_definition2900);
                    pat=pattern();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_pattern.add(pat.getTree());
                    if ( backtracking==0 ) {
                      text = input.toString(pat.start,pat.stop);
                    }

                    }
                    break;

            }

            RIGHT_CURLY29=(Token)input.LT(1);
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_definition2907); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_CURLY.add(RIGHT_CURLY29);


            // AST REWRITE
            // elements: name, name
            // token labels: name
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 193:2: -> {!\"\".equals(text)}? ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] )
            if (!"".equals(text)) {
                // DSLMap.g:193:25: ^( VT_VAR_DEF $name VT_PATTERN[$pat.start, text] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.next());
                adaptor.addChild(root_1, adaptor.create(VT_PATTERN, ((Token)pat.start),  text));

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 194:2: -> ^( VT_VAR_DEF $name)
            {
                // DSLMap.g:194:5: ^( VT_VAR_DEF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_DEF, "VT_VAR_DEF"), root_1);

                adaptor.addChild(root_1, stream_name.next());

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end variable_definition2

    public static class pattern_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start pattern
    // DSLMap.g:198:1: pattern : ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+ ;
    public final pattern_return pattern() throws RecognitionException {
        pattern_return retval = new pattern_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_CURLY31=null;
        Token RIGHT_CURLY33=null;
        Token LEFT_SQUARE34=null;
        Token RIGHT_SQUARE36=null;
        literal_return literal30 = null;

        literal_return literal32 = null;

        pattern_return pattern35 = null;


        Object LEFT_CURLY31_tree=null;
        Object RIGHT_CURLY33_tree=null;
        Object LEFT_SQUARE34_tree=null;
        Object RIGHT_SQUARE36_tree=null;

        try {
            // DSLMap.g:199:9: ( ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+ )
            // DSLMap.g:199:11: ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+
            {
            root_0 = (Object)adaptor.nil();

            // DSLMap.g:199:11: ( literal | LEFT_CURLY literal RIGHT_CURLY | LEFT_SQUARE pattern RIGHT_SQUARE )+
            int cnt15=0;
            loop15:
            do {
                int alt15=4;
                switch ( input.LA(1) ) {
                case RIGHT_SQUARE:
                    {
                    int LA15_2 = input.LA(2);

                    if ( (synpred23()) ) {
                        alt15=1;
                    }


                    }
                    break;
                case LEFT_SQUARE:
                    {
                    int LA15_3 = input.LA(2);

                    if ( (synpred23()) ) {
                        alt15=1;
                    }
                    else if ( (synpred25()) ) {
                        alt15=3;
                    }


                    }
                    break;
                case LEFT_CURLY:
                    {
                    alt15=2;
                    }
                    break;
                case LITERAL:
                case COLON:
                    {
                    alt15=1;
                    }
                    break;

                }

                switch (alt15) {
            	case 1 :
            	    // DSLMap.g:199:13: literal
            	    {
            	    pushFollow(FOLLOW_literal_in_pattern958);
            	    literal30=literal();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, literal30.getTree());

            	    }
            	    break;
            	case 2 :
            	    // DSLMap.g:200:13: LEFT_CURLY literal RIGHT_CURLY
            	    {
            	    LEFT_CURLY31=(Token)input.LT(1);
            	    match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_pattern972); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    LEFT_CURLY31_tree = (Object)adaptor.create(LEFT_CURLY31);
            	    adaptor.addChild(root_0, LEFT_CURLY31_tree);
            	    }
            	    pushFollow(FOLLOW_literal_in_pattern974);
            	    literal32=literal();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, literal32.getTree());
            	    RIGHT_CURLY33=(Token)input.LT(1);
            	    match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_pattern976); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    RIGHT_CURLY33_tree = (Object)adaptor.create(RIGHT_CURLY33);
            	    adaptor.addChild(root_0, RIGHT_CURLY33_tree);
            	    }

            	    }
            	    break;
            	case 3 :
            	    // DSLMap.g:201:13: LEFT_SQUARE pattern RIGHT_SQUARE
            	    {
            	    LEFT_SQUARE34=(Token)input.LT(1);
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_pattern990); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    LEFT_SQUARE34_tree = (Object)adaptor.create(LEFT_SQUARE34);
            	    adaptor.addChild(root_0, LEFT_SQUARE34_tree);
            	    }
            	    pushFollow(FOLLOW_pattern_in_pattern992);
            	    pattern35=pattern();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, pattern35.getTree());
            	    RIGHT_SQUARE36=(Token)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_pattern994); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    RIGHT_SQUARE36_tree = (Object)adaptor.create(RIGHT_SQUARE36);
            	    adaptor.addChild(root_0, RIGHT_SQUARE36_tree);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
            	    if (backtracking>0) {failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end pattern

    public static class variable_reference_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start variable_reference
    // DSLMap.g:206:1: variable_reference : lc= LEFT_CURLY name= LITERAL rc= RIGHT_CURLY -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) VT_SPACE -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF $name) VT_SPACE -> ^( VT_VAR_REF $name) ;
    public final variable_reference_return variable_reference() throws RecognitionException {
        variable_reference_return retval = new variable_reference_return();
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
            // DSLMap.g:211:2: (lc= LEFT_CURLY name= LITERAL rc= RIGHT_CURLY -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) VT_SPACE -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF $name) VT_SPACE -> ^( VT_VAR_REF $name) )
            // DSLMap.g:211:4: lc= LEFT_CURLY name= LITERAL rc= RIGHT_CURLY
            {
            lc=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_reference1029); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_CURLY.add(lc);

            if ( backtracking==0 ) {
              //try too figure out how to get " {name} " to realize there's a space infron of the { char
              		CommonToken back2 =  (CommonToken)input.LT(-2);
              		if( back2!=null && back2.getStopIndex() < ((CommonToken)lc).getStartIndex() -1 ) hasSpaceBefore = true; 
              		
            }
            name=(Token)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_variable_reference1040); if (failed) return retval;
            if ( backtracking==0 ) stream_LITERAL.add(name);

            rc=(Token)input.LT(1);
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_reference1044); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_CURLY.add(rc);

            if ( backtracking==0 ) {
              if(((CommonToken)rc).getStopIndex() < ((CommonToken)input.LT(1)).getStartIndex() - 1) hasSpaceAfter = true;
            }

            // AST REWRITE
            // elements: name, name, name, name
            // token labels: name
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 218:2: -> {hasSpaceBefore && hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name) VT_SPACE
            if (hasSpaceBefore && hasSpaceAfter) {
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));
                // DSLMap.g:218:49: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.next());

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 219:2: -> {hasSpaceBefore && !hasSpaceAfter}? VT_SPACE ^( VT_VAR_REF $name)
            if (hasSpaceBefore && !hasSpaceAfter) {
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));
                // DSLMap.g:219:50: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.next());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 220:2: -> {!hasSpaceBefore && hasSpaceAfter}? ^( VT_VAR_REF $name) VT_SPACE
            if (!hasSpaceBefore && hasSpaceAfter) {
                // DSLMap.g:220:42: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.next());

                adaptor.addChild(root_0, root_1);
                }
                adaptor.addChild(root_0, adaptor.create(VT_SPACE, "VT_SPACE"));

            }
            else // 221:2: -> ^( VT_VAR_REF $name)
            {
                // DSLMap.g:221:6: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.next());

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end variable_reference

    public static class variable_reference2_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start variable_reference2
    // DSLMap.g:225:1: variable_reference2 : LEFT_CURLY name= LITERAL RIGHT_CURLY -> ^( VT_VAR_REF $name) ;
    public final variable_reference2_return variable_reference2() throws RecognitionException {
        variable_reference2_return retval = new variable_reference2_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token LEFT_CURLY37=null;
        Token RIGHT_CURLY38=null;

        Object name_tree=null;
        Object LEFT_CURLY37_tree=null;
        Object RIGHT_CURLY38_tree=null;
        RewriteRuleTokenStream stream_RIGHT_CURLY=new RewriteRuleTokenStream(adaptor,"token RIGHT_CURLY");
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
        RewriteRuleTokenStream stream_LEFT_CURLY=new RewriteRuleTokenStream(adaptor,"token LEFT_CURLY");

        try {
            // DSLMap.g:226:2: ( LEFT_CURLY name= LITERAL RIGHT_CURLY -> ^( VT_VAR_REF $name) )
            // DSLMap.g:226:4: LEFT_CURLY name= LITERAL RIGHT_CURLY
            {
            LEFT_CURLY37=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_variable_reference21122); if (failed) return retval;
            if ( backtracking==0 ) stream_LEFT_CURLY.add(LEFT_CURLY37);

            name=(Token)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_variable_reference21126); if (failed) return retval;
            if ( backtracking==0 ) stream_LITERAL.add(name);

            RIGHT_CURLY38=(Token)input.LT(1);
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_variable_reference21128); if (failed) return retval;
            if ( backtracking==0 ) stream_RIGHT_CURLY.add(RIGHT_CURLY38);


            // AST REWRITE
            // elements: name
            // token labels: name
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 227:2: -> ^( VT_VAR_REF $name)
            {
                // DSLMap.g:227:5: ^( VT_VAR_REF $name)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(VT_VAR_REF, "VT_VAR_REF"), root_1);

                adaptor.addChild(root_1, stream_name.next());

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
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end variable_reference2

    public static class condition_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start condition_key
    // DSLMap.g:231:1: condition_key : {...}?value= LITERAL -> VT_CONDITION[$value] ;
    public final condition_key_return condition_key() throws RecognitionException {
        condition_key_return retval = new condition_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // DSLMap.g:232:2: ({...}?value= LITERAL -> VT_CONDITION[$value] )
            // DSLMap.g:232:4: {...}?value= LITERAL
            {
            if ( !(validateIdentifierKey("condition")||validateIdentifierKey("when")) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "condition_key", "validateIdentifierKey(\"condition\")||validateIdentifierKey(\"when\")");
            }
            value=(Token)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_condition_key1157); if (failed) return retval;
            if ( backtracking==0 ) stream_LITERAL.add(value);


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
            // 233:2: -> VT_CONDITION[$value]
            {
                adaptor.addChild(root_0, adaptor.create(VT_CONDITION, value));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end condition_key

    public static class consequence_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start consequence_key
    // DSLMap.g:236:1: consequence_key : {...}?value= LITERAL -> VT_CONSEQUENCE[$value] ;
    public final consequence_key_return consequence_key() throws RecognitionException {
        consequence_key_return retval = new consequence_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // DSLMap.g:237:2: ({...}?value= LITERAL -> VT_CONSEQUENCE[$value] )
            // DSLMap.g:237:4: {...}?value= LITERAL
            {
            if ( !(validateIdentifierKey("consequence")||validateIdentifierKey("then")) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "consequence_key", "validateIdentifierKey(\"consequence\")||validateIdentifierKey(\"then\")");
            }
            value=(Token)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_consequence_key1180); if (failed) return retval;
            if ( backtracking==0 ) stream_LITERAL.add(value);


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
            // 238:2: -> VT_CONSEQUENCE[$value]
            {
                adaptor.addChild(root_0, adaptor.create(VT_CONSEQUENCE, value));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end consequence_key

    public static class keyword_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start keyword_key
    // DSLMap.g:241:1: keyword_key : {...}?value= LITERAL -> VT_KEYWORD[$value] ;
    public final keyword_key_return keyword_key() throws RecognitionException {
        keyword_key_return retval = new keyword_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // DSLMap.g:242:2: ({...}?value= LITERAL -> VT_KEYWORD[$value] )
            // DSLMap.g:242:4: {...}?value= LITERAL
            {
            if ( !(validateIdentifierKey("keyword")) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "keyword_key", "validateIdentifierKey(\"keyword\")");
            }
            value=(Token)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_keyword_key1203); if (failed) return retval;
            if ( backtracking==0 ) stream_LITERAL.add(value);


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
            // 243:2: -> VT_KEYWORD[$value]
            {
                adaptor.addChild(root_0, adaptor.create(VT_KEYWORD, value));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end keyword_key

    public static class any_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start any_key
    // DSLMap.g:246:1: any_key : {...}?value= LITERAL -> VT_ANY[$value] ;
    public final any_key_return any_key() throws RecognitionException {
        any_key_return retval = new any_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token value=null;

        Object value_tree=null;
        RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");

        try {
            // DSLMap.g:247:2: ({...}?value= LITERAL -> VT_ANY[$value] )
            // DSLMap.g:247:4: {...}?value= LITERAL
            {
            if ( !(validateIdentifierKey("*")) ) {
                if (backtracking>0) {failed=true; return retval;}
                throw new FailedPredicateException(input, "any_key", "validateIdentifierKey(\"*\")");
            }
            value=(Token)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_any_key1226); if (failed) return retval;
            if ( backtracking==0 ) stream_LITERAL.add(value);


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
            // 248:2: -> VT_ANY[$value]
            {
                adaptor.addChild(root_0, adaptor.create(VT_ANY, value));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        
        	catch (RecognitionException e) {
        		throw e;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end any_key

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // DSLMap.g:94:24: ( meta_section )
        // DSLMap.g:94:24: meta_section
        {
        pushFollow(FOLLOW_meta_section_in_synpred4341);
        meta_section();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred4

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // DSLMap.g:102:4: ( condition_key )
        // DSLMap.g:102:4: condition_key
        {
        pushFollow(FOLLOW_condition_key_in_synpred6392);
        condition_key();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred6

    // $ANTLR start synpred7
    public final void synpred7_fragment() throws RecognitionException {   
        // DSLMap.g:103:5: ( consequence_key )
        // DSLMap.g:103:5: consequence_key
        {
        pushFollow(FOLLOW_consequence_key_in_synpred7401);
        consequence_key();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred7

    // $ANTLR start synpred8
    public final void synpred8_fragment() throws RecognitionException {   
        // DSLMap.g:104:5: ( keyword_key )
        // DSLMap.g:104:5: keyword_key
        {
        pushFollow(FOLLOW_keyword_key_in_synpred8409);
        keyword_key();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred8

    // $ANTLR start synpred12
    public final void synpred12_fragment() throws RecognitionException {   
        // DSLMap.g:133:4: ( literal )
        // DSLMap.g:133:4: literal
        {
        pushFollow(FOLLOW_literal_in_synpred12551);
        literal();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred12

    // $ANTLR start synpred15
    public final void synpred15_fragment() throws RecognitionException {   
        // DSLMap.g:151:5: ( literal )
        // DSLMap.g:151:5: literal
        {
        pushFollow(FOLLOW_literal_in_synpred15626);
        literal();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred15

    // $ANTLR start synpred16
    public final void synpred16_fragment() throws RecognitionException {   
        // DSLMap.g:151:13: ( EQUALS )
        // DSLMap.g:151:13: EQUALS
        {
        match(input,EQUALS,FOLLOW_EQUALS_in_synpred16628); if (failed) return ;

        }
    }
    // $ANTLR end synpred16

    // $ANTLR start synpred17
    public final void synpred17_fragment() throws RecognitionException {   
        // DSLMap.g:151:20: ( COMMA )
        // DSLMap.g:151:20: COMMA
        {
        match(input,COMMA,FOLLOW_COMMA_in_synpred17630); if (failed) return ;

        }
    }
    // $ANTLR end synpred17

    // $ANTLR start synpred23
    public final void synpred23_fragment() throws RecognitionException {   
        // DSLMap.g:199:13: ( literal )
        // DSLMap.g:199:13: literal
        {
        pushFollow(FOLLOW_literal_in_synpred23958);
        literal();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred23

    // $ANTLR start synpred25
    public final void synpred25_fragment() throws RecognitionException {   
        // DSLMap.g:201:13: ( LEFT_SQUARE pattern RIGHT_SQUARE )
        // DSLMap.g:201:13: LEFT_SQUARE pattern RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred25990); if (failed) return ;
        pushFollow(FOLLOW_pattern_in_synpred25992);
        pattern();
        _fsp--;
        if (failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred25994); if (failed) return ;

        }
    }
    // $ANTLR end synpred25

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
    public final boolean synpred25() {
        backtracking++;
        int start = input.mark();
        try {
            synpred25_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred23() {
        backtracking++;
        int start = input.mark();
        try {
            synpred23_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred16() {
        backtracking++;
        int start = input.mark();
        try {
            synpred16_fragment(); // can never throw exception
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
    public final boolean synpred17() {
        backtracking++;
        int start = input.mark();
        try {
            synpred17_fragment(); // can never throw exception
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
    public final boolean synpred15() {
        backtracking++;
        int start = input.mark();
        try {
            synpred15_fragment(); // can never throw exception
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


 

    public static final BitSet FOLLOW_statement_in_mapping_file262 = new BitSet(new long[]{0x0000000000B00002L});
    public static final BitSet FOLLOW_entry_in_statement285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comment_in_statement292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOL_in_statement298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LINE_COMMENT_in_comment314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scope_section_in_entry339 = new BitSet(new long[]{0x000000001B800000L});
    public static final BitSet FOLLOW_meta_section_in_entry341 = new BitSet(new long[]{0x000000001B800000L});
    public static final BitSet FOLLOW_key_section_in_entry344 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_EQUALS_in_entry346 = new BitSet(new long[]{0x000000001FC00000L});
    public static final BitSet FOLLOW_value_section_in_entry348 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_EOL_in_entry351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOF_in_entry353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_scope_section384 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_condition_key_in_scope_section392 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_consequence_key_in_scope_section401 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_keyword_key_in_scope_section409 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_any_key_in_scope_section417 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_scope_section425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_meta_section463 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_LITERAL_in_meta_section465 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_meta_section468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_key_sentence_in_key_section492 = new BitSet(new long[]{0x000000001B800002L});
    public static final BitSet FOLLOW_variable_definition_in_key_sentence523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_key_chunk_in_key_sentence530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_key_chunk551 = new BitSet(new long[]{0x000000000B800002L});
    public static final BitSet FOLLOW_value_sentence_in_value_section566 = new BitSet(new long[]{0x000000001FC00002L});
    public static final BitSet FOLLOW_variable_reference_in_value_sentence597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_chunk_in_value_sentence604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_value_chunk626 = new BitSet(new long[]{0x000000000FC00002L});
    public static final BitSet FOLLOW_EQUALS_in_value_chunk628 = new BitSet(new long[]{0x000000000FC00002L});
    public static final BitSet FOLLOW_COMMA_in_value_chunk630 = new BitSet(new long[]{0x000000000FC00002L});
    public static final BitSet FOLLOW_set_in_literal648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_variable_definition684 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_definition695 = new BitSet(new long[]{0x0000000028000000L});
    public static final BitSet FOLLOW_COLON_in_variable_definition699 = new BitSet(new long[]{0x000000001B800000L});
    public static final BitSet FOLLOW_pattern_in_variable_definition703 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_definition712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_variable_definition2888 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_definition2892 = new BitSet(new long[]{0x0000000028000000L});
    public static final BitSet FOLLOW_COLON_in_variable_definition2896 = new BitSet(new long[]{0x000000001B800000L});
    public static final BitSet FOLLOW_pattern_in_variable_definition2900 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_definition2907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_pattern958 = new BitSet(new long[]{0x000000001B800002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_pattern972 = new BitSet(new long[]{0x000000000B800000L});
    public static final BitSet FOLLOW_literal_in_pattern974 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_pattern976 = new BitSet(new long[]{0x000000001B800002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_pattern990 = new BitSet(new long[]{0x000000001B800000L});
    public static final BitSet FOLLOW_pattern_in_pattern992 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_pattern994 = new BitSet(new long[]{0x000000001B800002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_variable_reference1029 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_reference1040 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_reference1044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_variable_reference21122 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_LITERAL_in_variable_reference21126 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_variable_reference21128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_condition_key1157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_consequence_key1180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_keyword_key1203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_any_key1226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_meta_section_in_synpred4341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_condition_key_in_synpred6392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_consequence_key_in_synpred7401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_key_in_synpred8409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred12551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred15626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_synpred16628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_synpred17630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred23958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred25990 = new BitSet(new long[]{0x000000001B800000L});
    public static final BitSet FOLLOW_pattern_in_synpred25992 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred25994 = new BitSet(new long[]{0x0000000000000002L});

}