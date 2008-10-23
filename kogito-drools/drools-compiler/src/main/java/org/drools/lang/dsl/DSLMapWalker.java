// $ANTLR 3.0 src/main/resources/org/drools/lang/dsl/DSLMapWalker.g 2008-10-22 23:57:51

	package org.drools.lang.dsl;
	
	import java.util.Map;
	import java.util.HashMap;
	


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class DSLMapWalker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_DSL_GRAMMAR", "VT_COMMENT", "VT_ENTRY", "VT_SCOPE", "VT_CONDITION", "VT_CONSEQUENCE", "VT_KEYWORD", "VT_ANY", "VT_META", "VT_ENTRY_KEY", "VT_ENTRY_VAL", "VT_VAR_DEF", "VT_VAR_REF", "VT_LITERAL", "VT_PATTERN", "VT_QUAL", "VT_SPACE", "EOL", "LINE_COMMENT", "EQUALS", "LEFT_SQUARE", "RIGHT_SQUARE", "LITERAL", "COMMA", "COLON", "LEFT_CURLY", "RIGHT_CURLY", "WS", "EscapeSequence", "DOT", "POUND", "MISC"
    };
    public static final int COMMA=27;
    public static final int RIGHT_CURLY=30;
    public static final int VT_ENTRY_VAL=14;
    public static final int WS=31;
    public static final int MISC=35;
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
    public static final int VT_VAR_DEF=15;
    public static final int VT_ENTRY=6;
    public static final int VT_PATTERN=18;
    public static final int LITERAL=26;
    public static final int EscapeSequence=32;
    public static final int VT_COMMENT=5;
    public static final int EOF=-1;
    public static final int EOL=21;
    public static final int LEFT_SQUARE=24;
    public static final int VT_ENTRY_KEY=13;
    public static final int VT_SCOPE=7;
    public static final int COLON=28;
    public static final int VT_KEYWORD=10;
    public static final int VT_QUAL=19;
    public static final int VT_VAR_REF=16;
    public static final int LEFT_CURLY=29;
    public static final int POUND=34;
    public static final int RIGHT_SQUARE=25;

        public DSLMapWalker(TreeNodeStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/dsl/DSLMapWalker.g"; }


    protected static class mapping_file_scope {
        DSLMapping retval;
    }
    protected Stack mapping_file_stack = new Stack();


    // $ANTLR start mapping_file
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:17:1: mapping_file returns [DSLMapping mapping] : ^( VT_DSL_GRAMMAR ( valid_entry )* ) ;
    public final DSLMapping mapping_file() throws RecognitionException {
        mapping_file_stack.push(new mapping_file_scope());
        DSLMapping mapping = null;


        	((mapping_file_scope)mapping_file_stack.peek()).retval = new DefaultDSLMapping() ;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:24:4: ( ^( VT_DSL_GRAMMAR ( valid_entry )* ) )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:24:4: ^( VT_DSL_GRAMMAR ( valid_entry )* )
            {
            match(input,VT_DSL_GRAMMAR,FOLLOW_VT_DSL_GRAMMAR_in_mapping_file54); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:24:21: ( valid_entry )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( ((LA1_0>=VT_COMMENT && LA1_0<=VT_ENTRY)) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:24:21: valid_entry
                	    {
                	    pushFollow(FOLLOW_valid_entry_in_mapping_file56);
                	    valid_entry();
                	    _fsp--;


                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            		mapping = ((mapping_file_scope)mapping_file_stack.peek()).retval;
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            mapping_file_stack.pop();
        }
        return mapping;
    }
    // $ANTLR end mapping_file


    // $ANTLR start valid_entry
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:30:1: valid_entry returns [DSLMappingEntry mappingEntry] : (ent= entry | ^( VT_COMMENT lc= LINE_COMMENT ) );
    public final DSLMappingEntry valid_entry() throws RecognitionException {
        DSLMappingEntry mappingEntry = null;

        CommonTree lc=null;
        DSLMappingEntry ent = null;


        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:31:4: (ent= entry | ^( VT_COMMENT lc= LINE_COMMENT ) )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==VT_ENTRY) ) {
                alt2=1;
            }
            else if ( (LA2_0==VT_COMMENT) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("30:1: valid_entry returns [DSLMappingEntry mappingEntry] : (ent= entry | ^( VT_COMMENT lc= LINE_COMMENT ) );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:31:4: ent= entry
                    {
                    pushFollow(FOLLOW_entry_in_valid_entry78);
                    ent=entry();
                    _fsp--;

                    mappingEntry = ent; 

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:32:4: ^( VT_COMMENT lc= LINE_COMMENT )
                    {
                    match(input,VT_COMMENT,FOLLOW_VT_COMMENT_in_valid_entry86); 

                    match(input, Token.DOWN, null); 
                    lc=(CommonTree)input.LT(1);
                    match(input,LINE_COMMENT,FOLLOW_LINE_COMMENT_in_valid_entry90); 

                    match(input, Token.UP, null); 
                    mappingEntry = null;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return mappingEntry;
    }
    // $ANTLR end valid_entry

    protected static class entry_scope {
        Map variables;
        AntlrDSLMappingEntry retval;
        int counter;
        StringBuffer keybuffer;
        StringBuffer valuebuffer;
        StringBuffer sentencebuffer;
    }
    protected Stack entry_stack = new Stack();


    // $ANTLR start entry
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:36:1: entry returns [DSLMappingEntry mappingEntry] : ^( VT_ENTRY scope_section ( meta_section )? key_section value_section ) ;
    public final DSLMappingEntry entry() throws RecognitionException {
        entry_stack.push(new entry_scope());
        DSLMappingEntry mappingEntry = null;


        	((entry_scope)entry_stack.peek()).retval = new AntlrDSLMappingEntry() ;
        	((entry_scope)entry_stack.peek()).variables = new HashMap();
        	((entry_scope)entry_stack.peek()).keybuffer = new StringBuffer();
        	((entry_scope)entry_stack.peek()).valuebuffer = new StringBuffer();
        	((entry_scope)entry_stack.peek()).sentencebuffer = new StringBuffer();

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:52:4: ( ^( VT_ENTRY scope_section ( meta_section )? key_section value_section ) )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:52:4: ^( VT_ENTRY scope_section ( meta_section )? key_section value_section )
            {
            match(input,VT_ENTRY,FOLLOW_VT_ENTRY_in_entry119); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_scope_section_in_entry121);
            scope_section();
            _fsp--;

            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:52:29: ( meta_section )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==VT_META) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:52:29: meta_section
                    {
                    pushFollow(FOLLOW_meta_section_in_entry123);
                    meta_section();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_key_section_in_entry126);
            key_section();
            _fsp--;

            ((entry_scope)entry_stack.peek()).retval.variables = ((entry_scope)entry_stack.peek()).variables; ((entry_scope)entry_stack.peek()).retval.setMappingKey(((entry_scope)entry_stack.peek()).keybuffer.toString());((entry_scope)entry_stack.peek()).retval.setSentence(((entry_scope)entry_stack.peek()).sentencebuffer.toString());
            pushFollow(FOLLOW_value_section_in_entry132);
            value_section();
            _fsp--;


            match(input, Token.UP, null); 

            		((entry_scope)entry_stack.peek()).retval.setMappingValue(((entry_scope)entry_stack.peek()).valuebuffer.toString());
            		mappingEntry = ((entry_scope)entry_stack.peek()).retval;
            		((mapping_file_scope)mapping_file_stack.peek()).retval.addEntry(mappingEntry);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            entry_stack.pop();
        }
        return mappingEntry;
    }
    // $ANTLR end entry


    // $ANTLR start scope_section
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:62:1: scope_section : ^(thescope= VT_SCOPE ( condition_key )? ( consequence_key )? ( keyword_key )? ( any_key )? ) ;
    public final void scope_section() throws RecognitionException {
        CommonTree thescope=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:63:4: ( ^(thescope= VT_SCOPE ( condition_key )? ( consequence_key )? ( keyword_key )? ( any_key )? ) )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:63:4: ^(thescope= VT_SCOPE ( condition_key )? ( consequence_key )? ( keyword_key )? ( any_key )? )
            {
            thescope=(CommonTree)input.LT(1);
            match(input,VT_SCOPE,FOLLOW_VT_SCOPE_in_scope_section152); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:63:24: ( condition_key )?
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==VT_CONDITION) ) {
                    alt4=1;
                }
                switch (alt4) {
                    case 1 :
                        // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:63:24: condition_key
                        {
                        pushFollow(FOLLOW_condition_key_in_scope_section154);
                        condition_key();
                        _fsp--;


                        }
                        break;

                }

                // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:63:39: ( consequence_key )?
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==VT_CONSEQUENCE) ) {
                    alt5=1;
                }
                switch (alt5) {
                    case 1 :
                        // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:63:39: consequence_key
                        {
                        pushFollow(FOLLOW_consequence_key_in_scope_section157);
                        consequence_key();
                        _fsp--;


                        }
                        break;

                }

                // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:63:56: ( keyword_key )?
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==VT_KEYWORD) ) {
                    alt6=1;
                }
                switch (alt6) {
                    case 1 :
                        // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:63:56: keyword_key
                        {
                        pushFollow(FOLLOW_keyword_key_in_scope_section160);
                        keyword_key();
                        _fsp--;


                        }
                        break;

                }

                // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:63:69: ( any_key )?
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==VT_ANY) ) {
                    alt7=1;
                }
                switch (alt7) {
                    case 1 :
                        // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:63:69: any_key
                        {
                        pushFollow(FOLLOW_any_key_in_scope_section163);
                        any_key();
                        _fsp--;


                        }
                        break;

                }


                match(input, Token.UP, null); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end scope_section


    // $ANTLR start meta_section
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:68:1: meta_section : ^( VT_META (metalit= LITERAL )? ) ;
    public final void meta_section() throws RecognitionException {
        CommonTree metalit=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:69:4: ( ^( VT_META (metalit= LITERAL )? ) )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:69:4: ^( VT_META (metalit= LITERAL )? )
            {
            match(input,VT_META,FOLLOW_VT_META_in_meta_section180); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:69:21: (metalit= LITERAL )?
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==LITERAL) ) {
                    alt8=1;
                }
                switch (alt8) {
                    case 1 :
                        // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:69:21: metalit= LITERAL
                        {
                        metalit=(CommonTree)input.LT(1);
                        match(input,LITERAL,FOLLOW_LITERAL_in_meta_section184); 

                        }
                        break;

                }


                match(input, Token.UP, null); 
            }

            		if ( metalit == null || metalit.getText() == null || metalit.getText().length() == 0 ) {
            			((entry_scope)entry_stack.peek()).retval.setMetaData(DSLMappingEntry.EMPTY_METADATA);
            		} else {
                    		((entry_scope)entry_stack.peek()).retval.setMetaData(new DSLMappingEntry.DefaultDSLEntryMetaData( metalit.getText() ));
            	        }
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end meta_section


    // $ANTLR start key_section
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:79:1: key_section : ^( VT_ENTRY_KEY ( key_sentence )+ ) ;
    public final void key_section() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:80:4: ( ^( VT_ENTRY_KEY ( key_sentence )+ ) )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:80:4: ^( VT_ENTRY_KEY ( key_sentence )+ )
            {
            match(input,VT_ENTRY_KEY,FOLLOW_VT_ENTRY_KEY_in_key_section201); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:80:19: ( key_sentence )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==VT_VAR_DEF||LA9_0==VT_LITERAL||LA9_0==VT_SPACE) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:80:19: key_sentence
            	    {
            	    pushFollow(FOLLOW_key_sentence_in_key_section203);
            	    key_sentence();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end key_section


    // $ANTLR start key_sentence
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:83:1: key_sentence : ( variable_definition | vtl= VT_LITERAL | VT_SPACE );
    public final void key_sentence() throws RecognitionException {
        CommonTree vtl=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:84:4: ( variable_definition | vtl= VT_LITERAL | VT_SPACE )
            int alt10=3;
            switch ( input.LA(1) ) {
            case VT_VAR_DEF:
                {
                alt10=1;
                }
                break;
            case VT_LITERAL:
                {
                alt10=2;
                }
                break;
            case VT_SPACE:
                {
                alt10=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("83:1: key_sentence : ( variable_definition | vtl= VT_LITERAL | VT_SPACE );", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:84:4: variable_definition
                    {
                    pushFollow(FOLLOW_variable_definition_in_key_sentence218);
                    variable_definition();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:85:4: vtl= VT_LITERAL
                    {
                    vtl=(CommonTree)input.LT(1);
                    match(input,VT_LITERAL,FOLLOW_VT_LITERAL_in_key_sentence225); 

                    		((entry_scope)entry_stack.peek()).keybuffer.append(vtl.getText());
                    		((entry_scope)entry_stack.peek()).sentencebuffer.append(vtl.getText());
                    	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:90:4: VT_SPACE
                    {
                    match(input,VT_SPACE,FOLLOW_VT_SPACE_in_key_sentence234); 

                    		((entry_scope)entry_stack.peek()).keybuffer.append("\\s+");
                    		((entry_scope)entry_stack.peek()).sentencebuffer.append(" ");
                    	

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end key_sentence


    // $ANTLR start value_section
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:97:1: value_section : ^( VT_ENTRY_VAL ( value_sentence )+ ) ;
    public final void value_section() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:101:4: ( ^( VT_ENTRY_VAL ( value_sentence )+ ) )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:101:4: ^( VT_ENTRY_VAL ( value_sentence )+ )
            {
            match(input,VT_ENTRY_VAL,FOLLOW_VT_ENTRY_VAL_in_value_section255); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:101:19: ( value_sentence )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>=VT_VAR_REF && LA11_0<=VT_LITERAL)||LA11_0==VT_SPACE) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:101:19: value_sentence
            	    {
            	    pushFollow(FOLLOW_value_sentence_in_value_section257);
            	    value_sentence();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);


            match(input, Token.UP, null); 

            }


            	((entry_scope)entry_stack.peek()).valuebuffer.append(" ");

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end value_section


    // $ANTLR start value_sentence
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:104:1: value_sentence : ( variable_reference | vtl= VT_LITERAL | VT_SPACE );
    public final void value_sentence() throws RecognitionException {
        CommonTree vtl=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:105:4: ( variable_reference | vtl= VT_LITERAL | VT_SPACE )
            int alt12=3;
            switch ( input.LA(1) ) {
            case VT_VAR_REF:
                {
                alt12=1;
                }
                break;
            case VT_LITERAL:
                {
                alt12=2;
                }
                break;
            case VT_SPACE:
                {
                alt12=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("104:1: value_sentence : ( variable_reference | vtl= VT_LITERAL | VT_SPACE );", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:105:4: variable_reference
                    {
                    pushFollow(FOLLOW_variable_reference_in_value_sentence274);
                    variable_reference();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:106:4: vtl= VT_LITERAL
                    {
                    vtl=(CommonTree)input.LT(1);
                    match(input,VT_LITERAL,FOLLOW_VT_LITERAL_in_value_sentence281); 

                    		((entry_scope)entry_stack.peek()).valuebuffer.append(vtl.getText().replaceAll("\\$", "\\\\\\$"));
                    	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:110:4: VT_SPACE
                    {
                    match(input,VT_SPACE,FOLLOW_VT_SPACE_in_value_sentence289); 

                    		((entry_scope)entry_stack.peek()).valuebuffer.append(" ");
                    	

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end value_sentence


    // $ANTLR start literal
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:116:1: literal : theliteral= VT_LITERAL ;
    public final void literal() throws RecognitionException {
        CommonTree theliteral=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:117:4: (theliteral= VT_LITERAL )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:117:4: theliteral= VT_LITERAL
            {
            theliteral=(CommonTree)input.LT(1);
            match(input,VT_LITERAL,FOLLOW_VT_LITERAL_in_literal307); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end literal


    // $ANTLR start variable_definition
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:120:1: variable_definition : ^( VT_VAR_DEF varname= LITERAL ^( VT_QUAL (q= LITERAL )? ) (pattern= VT_PATTERN )? ) ;
    public final void variable_definition() throws RecognitionException {
        CommonTree varname=null;
        CommonTree q=null;
        CommonTree pattern=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:121:6: ( ^( VT_VAR_DEF varname= LITERAL ^( VT_QUAL (q= LITERAL )? ) (pattern= VT_PATTERN )? ) )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:121:6: ^( VT_VAR_DEF varname= LITERAL ^( VT_QUAL (q= LITERAL )? ) (pattern= VT_PATTERN )? )
            {
            match(input,VT_VAR_DEF,FOLLOW_VT_VAR_DEF_in_variable_definition323); 

            match(input, Token.DOWN, null); 
            varname=(CommonTree)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition327); 
            match(input,VT_QUAL,FOLLOW_VT_QUAL_in_variable_definition331); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:121:47: (q= LITERAL )?
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==LITERAL) ) {
                    alt13=1;
                }
                switch (alt13) {
                    case 1 :
                        // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:121:47: q= LITERAL
                        {
                        q=(CommonTree)input.LT(1);
                        match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition335); 

                        }
                        break;

                }


                match(input, Token.UP, null); 
            }
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:121:65: (pattern= VT_PATTERN )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==VT_PATTERN) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:121:65: pattern= VT_PATTERN
                    {
                    pattern=(CommonTree)input.LT(1);
                    match(input,VT_PATTERN,FOLLOW_VT_PATTERN_in_variable_definition341); 

                    }
                    break;

            }


            match(input, Token.UP, null); 

            		((entry_scope)entry_stack.peek()).counter++;
            		((entry_scope)entry_stack.peek()).variables.put(varname.getText(), new Integer(((entry_scope)entry_stack.peek()).counter));
            		
            		if(q!=null && pattern!=null){
            			((entry_scope)entry_stack.peek()).sentencebuffer.append("{"+varname.getText()+":"+q.getText()+":"+pattern.getText()+"}");
            		}else if(q==null && pattern!=null){
            			((entry_scope)entry_stack.peek()).sentencebuffer.append("{"+varname.getText()+":"+pattern.getText()+"}");
            		}else{
            			((entry_scope)entry_stack.peek()).sentencebuffer.append("{"+varname.getText()+"}");
            		}
            		
            		if(q == null || (!q.getText().equals("ENUM") && !q.getText().equals("DATE") && !q.getText().equals("BOOLEAN"))){
            			((entry_scope)entry_stack.peek()).keybuffer.append(pattern != null? "(" + pattern.getText() + ")" : "(.*?)");
            		}else{
            			((entry_scope)entry_stack.peek()).keybuffer.append("(.*?)");
            			
            		}
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end variable_definition


    // $ANTLR start variable_reference
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:144:1: variable_reference : ^(varref= VT_VAR_REF lit= LITERAL ) ;
    public final void variable_reference() throws RecognitionException {
        CommonTree varref=null;
        CommonTree lit=null;

        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:145:4: ( ^(varref= VT_VAR_REF lit= LITERAL ) )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:145:4: ^(varref= VT_VAR_REF lit= LITERAL )
            {
            varref=(CommonTree)input.LT(1);
            match(input,VT_VAR_REF,FOLLOW_VT_VAR_REF_in_variable_reference363); 

            match(input, Token.DOWN, null); 
            lit=(CommonTree)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_variable_reference367); 

            match(input, Token.UP, null); 

            		((entry_scope)entry_stack.peek()).valuebuffer.append("$" + ((entry_scope)entry_stack.peek()).variables.get(lit.getText()));
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end variable_reference


    // $ANTLR start condition_key
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:151:1: condition_key : VT_CONDITION ;
    public final void condition_key() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:152:4: ( VT_CONDITION )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:152:4: VT_CONDITION
            {
            match(input,VT_CONDITION,FOLLOW_VT_CONDITION_in_condition_key385); 
            ((entry_scope)entry_stack.peek()).retval.setSection(DSLMappingEntry.CONDITION);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end condition_key


    // $ANTLR start consequence_key
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:156:1: consequence_key : VT_CONSEQUENCE ;
    public final void consequence_key() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:157:4: ( VT_CONSEQUENCE )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:157:4: VT_CONSEQUENCE
            {
            match(input,VT_CONSEQUENCE,FOLLOW_VT_CONSEQUENCE_in_consequence_key400); 
            ((entry_scope)entry_stack.peek()).retval.setSection(DSLMappingEntry.CONSEQUENCE);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end consequence_key


    // $ANTLR start keyword_key
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:161:1: keyword_key : VT_KEYWORD ;
    public final void keyword_key() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:162:4: ( VT_KEYWORD )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:162:4: VT_KEYWORD
            {
            match(input,VT_KEYWORD,FOLLOW_VT_KEYWORD_in_keyword_key415); 
            ((entry_scope)entry_stack.peek()).retval.setSection(DSLMappingEntry.KEYWORD);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end keyword_key


    // $ANTLR start any_key
    // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:166:1: any_key : VT_ANY ;
    public final void any_key() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:167:4: ( VT_ANY )
            // src/main/resources/org/drools/lang/dsl/DSLMapWalker.g:167:4: VT_ANY
            {
            match(input,VT_ANY,FOLLOW_VT_ANY_in_any_key430); 
            ((entry_scope)entry_stack.peek()).retval.setSection(DSLMappingEntry.ANY);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end any_key


 

    public static final BitSet FOLLOW_VT_DSL_GRAMMAR_in_mapping_file54 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_valid_entry_in_mapping_file56 = new BitSet(new long[]{0x0000000000000068L});
    public static final BitSet FOLLOW_entry_in_valid_entry78 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_COMMENT_in_valid_entry86 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LINE_COMMENT_in_valid_entry90 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ENTRY_in_entry119 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_scope_section_in_entry121 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_meta_section_in_entry123 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_key_section_in_entry126 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_value_section_in_entry132 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_SCOPE_in_scope_section152 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_key_in_scope_section154 = new BitSet(new long[]{0x0000000000000E08L});
    public static final BitSet FOLLOW_consequence_key_in_scope_section157 = new BitSet(new long[]{0x0000000000000C08L});
    public static final BitSet FOLLOW_keyword_key_in_scope_section160 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_any_key_in_scope_section163 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_META_in_meta_section180 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LITERAL_in_meta_section184 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ENTRY_KEY_in_key_section201 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_key_sentence_in_key_section203 = new BitSet(new long[]{0x0000000000128008L});
    public static final BitSet FOLLOW_variable_definition_in_key_sentence218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_LITERAL_in_key_sentence225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_SPACE_in_key_sentence234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ENTRY_VAL_in_value_section255 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_value_sentence_in_value_section257 = new BitSet(new long[]{0x0000000000130008L});
    public static final BitSet FOLLOW_variable_reference_in_value_sentence274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_LITERAL_in_value_sentence281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_SPACE_in_value_sentence289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_LITERAL_in_literal307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_VAR_DEF_in_variable_definition323 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LITERAL_in_variable_definition327 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_VT_QUAL_in_variable_definition331 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LITERAL_in_variable_definition335 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PATTERN_in_variable_definition341 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_VAR_REF_in_variable_reference363 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LITERAL_in_variable_reference367 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_CONDITION_in_condition_key385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_CONSEQUENCE_in_consequence_key400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_KEYWORD_in_keyword_key415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ANY_in_any_key430 = new BitSet(new long[]{0x0000000000000002L});

}