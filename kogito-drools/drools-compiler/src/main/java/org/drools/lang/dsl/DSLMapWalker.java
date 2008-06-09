// $ANTLR 3.0.1 DSLMapWalker.g 2008-05-27 14:03:46

	package org.drools.lang.dsl;
	
	import java.util.Map;
	import java.util.HashMap;
	


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class DSLMapWalker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_DSL_GRAMMAR", "VT_COMMENT", "VT_ENTRY", "VT_SCOPE", "VT_CONDITION", "VT_CONSEQUENCE", "VT_KEYWORD", "VT_ANY", "VT_META", "VT_ENTRY_KEY", "VT_ENTRY_VAL", "VT_VAR_DEF", "VT_VAR_REF", "VT_LITERAL", "VT_PATTERN", "VT_SPACE", "EOL", "LINE_COMMENT", "EQUALS", "LEFT_SQUARE", "RIGHT_SQUARE", "LITERAL", "COLON", "LEFT_CURLY", "RIGHT_CURLY", "WS", "EscapeSequence", "DOT", "POUND", "MISC"
    };
    public static final int RIGHT_CURLY=28;
    public static final int VT_ENTRY_VAL=14;
    public static final int WS=29;
    public static final int MISC=33;
    public static final int VT_META=12;
    public static final int VT_CONSEQUENCE=9;
    public static final int VT_SPACE=19;
    public static final int LINE_COMMENT=21;
    public static final int VT_ANY=11;
    public static final int VT_LITERAL=17;
    public static final int DOT=31;
    public static final int EQUALS=22;
    public static final int VT_DSL_GRAMMAR=4;
    public static final int VT_CONDITION=8;
    public static final int VT_ENTRY=6;
    public static final int VT_VAR_DEF=15;
    public static final int VT_PATTERN=18;
    public static final int LITERAL=25;
    public static final int EscapeSequence=30;
    public static final int VT_COMMENT=5;
    public static final int EOF=-1;
    public static final int EOL=20;
    public static final int LEFT_SQUARE=23;
    public static final int VT_ENTRY_KEY=13;
    public static final int VT_SCOPE=7;
    public static final int COLON=26;
    public static final int VT_KEYWORD=10;
    public static final int VT_VAR_REF=16;
    public static final int LEFT_CURLY=27;
    public static final int POUND=32;
    public static final int RIGHT_SQUARE=24;

        public DSLMapWalker(TreeNodeStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "DSLMapWalker.g"; }


    protected static class mapping_file_scope {
        DSLMapping retval;
    }
    protected Stack mapping_file_stack = new Stack();


    // $ANTLR start mapping_file
    // DSLMapWalker.g:17:1: mapping_file returns [DSLMapping mapping] : ^( VT_DSL_GRAMMAR ( entry )* ) ;
    public final DSLMapping mapping_file() throws RecognitionException {
        mapping_file_stack.push(new mapping_file_scope());
        DSLMapping mapping = null;

        
        	((mapping_file_scope)mapping_file_stack.peek()).retval = new DefaultDSLMapping() ;

        try {
            // DSLMapWalker.g:24:2: ( ^( VT_DSL_GRAMMAR ( entry )* ) )
            // DSLMapWalker.g:24:4: ^( VT_DSL_GRAMMAR ( entry )* )
            {
            match(input,VT_DSL_GRAMMAR,FOLLOW_VT_DSL_GRAMMAR_in_mapping_file54); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // DSLMapWalker.g:24:21: ( entry )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==VT_ENTRY) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // DSLMapWalker.g:24:21: entry
                	    {
                	    pushFollow(FOLLOW_entry_in_mapping_file56);
                	    entry();
                	    _fsp--;


                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            
            		//System.out.println("done parsing file");
            		//System.out.println(((mapping_file_scope)mapping_file_stack.peek()).retval.dumpFile());
            		mapping = ((mapping_file_scope)mapping_file_stack.peek()).retval;
            		//java.io.StringWriter sw = new java.io.StringWriter();
            		//((mapping_file_scope)mapping_file_stack.peek()).retval.saveMapping(sw);
            		//System.out.println(sw.toString());
            	

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


    // $ANTLR start mapping_entry
    // DSLMapWalker.g:35:1: mapping_entry : ent= entry ;
    public final void mapping_entry() throws RecognitionException {
        DSLMappingEntry ent = null;


        try {
            // DSLMapWalker.g:36:2: (ent= entry )
            // DSLMapWalker.g:36:4: ent= entry
            {
            pushFollow(FOLLOW_entry_in_mapping_entry76);
            ent=entry();
            _fsp--;

            
            		((mapping_file_scope)mapping_file_stack.peek()).retval.addEntry(ent);
            		//System.out.println("mapping size is now " + ((mapping_file_scope)mapping_file_stack.peek()).retval.getEntries().size());
            	

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
    // $ANTLR end mapping_entry


    // $ANTLR start valid_entry
    // DSLMapWalker.g:43:1: valid_entry returns [DSLMappingEntry mappingEntry] : (ent= entry | VT_COMMENT );
    public final DSLMappingEntry valid_entry() throws RecognitionException {
        DSLMappingEntry mappingEntry = null;

        DSLMappingEntry ent = null;


        try {
            // DSLMapWalker.g:44:2: (ent= entry | VT_COMMENT )
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
                    new NoViableAltException("43:1: valid_entry returns [DSLMappingEntry mappingEntry] : (ent= entry | VT_COMMENT );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // DSLMapWalker.g:44:4: ent= entry
                    {
                    pushFollow(FOLLOW_entry_in_valid_entry97);
                    ent=entry();
                    _fsp--;

                    mappingEntry = ent;

                    }
                    break;
                case 2 :
                    // DSLMapWalker.g:45:4: VT_COMMENT
                    {
                    match(input,VT_COMMENT,FOLLOW_VT_COMMENT_in_valid_entry104); 
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
    }
    protected Stack entry_stack = new Stack();


    // $ANTLR start entry
    // DSLMapWalker.g:49:1: entry returns [DSLMappingEntry mappingEntry] : ^( VT_ENTRY scope_section ( meta_section )? key_section value_section ) ;
    public final DSLMappingEntry entry() throws RecognitionException {
        entry_stack.push(new entry_scope());
        DSLMappingEntry mappingEntry = null;

        
        	((entry_scope)entry_stack.peek()).retval = new AntlrDSLMappingEntry() ;
        	((entry_scope)entry_stack.peek()).variables = new HashMap();
        	((entry_scope)entry_stack.peek()).keybuffer = new StringBuffer();
        	((entry_scope)entry_stack.peek()).valuebuffer = new StringBuffer();

        try {
            // DSLMapWalker.g:63:2: ( ^( VT_ENTRY scope_section ( meta_section )? key_section value_section ) )
            // DSLMapWalker.g:63:4: ^( VT_ENTRY scope_section ( meta_section )? key_section value_section )
            {
            match(input,VT_ENTRY,FOLLOW_VT_ENTRY_in_entry132); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_scope_section_in_entry134);
            scope_section();
            _fsp--;

            // DSLMapWalker.g:63:29: ( meta_section )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==VT_META) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // DSLMapWalker.g:63:29: meta_section
                    {
                    pushFollow(FOLLOW_meta_section_in_entry136);
                    meta_section();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_key_section_in_entry139);
            key_section();
            _fsp--;

            ((entry_scope)entry_stack.peek()).retval.variables = ((entry_scope)entry_stack.peek()).variables; ((entry_scope)entry_stack.peek()).retval.setMappingKey(((entry_scope)entry_stack.peek()).keybuffer.toString());
            pushFollow(FOLLOW_value_section_in_entry145);
            value_section();
            _fsp--;


            match(input, Token.UP, null); 
            
            		//System.out.println("for this entry, metadata is " + ((entry_scope)entry_stack.peek()).retval.getMetaData().getMetaData());
            		//System.out.println("variables are " + ((entry_scope)entry_stack.peek()).variables);
            		
            		//System.out.println("keybuffer: " + ((entry_scope)entry_stack.peek()).keybuffer);
            		//System.out.println("valuebuffer: " + ((entry_scope)entry_stack.peek()).valuebuffer);
            //		((mapping_file_scope)mapping_file_stack.peek()).retval.addEntry(((entry_scope)entry_stack.peek()).retval);
            //		System.out.println("mapping size is now " + ((mapping_file_scope)mapping_file_stack.peek()).retval.getEntries().size());
            		//((entry_scope)entry_stack.peek()).retval.variables = ((entry_scope)entry_stack.peek()).variables;
            		//((entry_scope)entry_stack.peek()).retval.setMappingKey(((entry_scope)entry_stack.peek()).keybuffer.toString());
            		((entry_scope)entry_stack.peek()).retval.setMappingValue(((entry_scope)entry_stack.peek()).valuebuffer.toString());
            		//System.out.println("keypattern is " + ((entry_scope)entry_stack.peek()).retval.getKeyPattern());
            		//System.out.println("valuepattern is " + ((entry_scope)entry_stack.peek()).retval.getValuePattern());
            		mappingEntry = ((entry_scope)entry_stack.peek()).retval;
            	

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
    // DSLMapWalker.g:83:1: scope_section : ^(thescope= VT_SCOPE ( condition_key )? ( consequence_key )? ( keyword_key )? ( any_key )? ) ;
    public final void scope_section() throws RecognitionException {
        CommonTree thescope=null;

        try {
            // DSLMapWalker.g:84:2: ( ^(thescope= VT_SCOPE ( condition_key )? ( consequence_key )? ( keyword_key )? ( any_key )? ) )
            // DSLMapWalker.g:84:4: ^(thescope= VT_SCOPE ( condition_key )? ( consequence_key )? ( keyword_key )? ( any_key )? )
            {
            thescope=(CommonTree)input.LT(1);
            match(input,VT_SCOPE,FOLLOW_VT_SCOPE_in_scope_section165); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // DSLMapWalker.g:84:24: ( condition_key )?
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==VT_CONDITION) ) {
                    alt4=1;
                }
                switch (alt4) {
                    case 1 :
                        // DSLMapWalker.g:84:24: condition_key
                        {
                        pushFollow(FOLLOW_condition_key_in_scope_section167);
                        condition_key();
                        _fsp--;


                        }
                        break;

                }

                // DSLMapWalker.g:84:39: ( consequence_key )?
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==VT_CONSEQUENCE) ) {
                    alt5=1;
                }
                switch (alt5) {
                    case 1 :
                        // DSLMapWalker.g:84:39: consequence_key
                        {
                        pushFollow(FOLLOW_consequence_key_in_scope_section170);
                        consequence_key();
                        _fsp--;


                        }
                        break;

                }

                // DSLMapWalker.g:84:56: ( keyword_key )?
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==VT_KEYWORD) ) {
                    alt6=1;
                }
                switch (alt6) {
                    case 1 :
                        // DSLMapWalker.g:84:56: keyword_key
                        {
                        pushFollow(FOLLOW_keyword_key_in_scope_section173);
                        keyword_key();
                        _fsp--;


                        }
                        break;

                }

                // DSLMapWalker.g:84:69: ( any_key )?
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==VT_ANY) ) {
                    alt7=1;
                }
                switch (alt7) {
                    case 1 :
                        // DSLMapWalker.g:84:69: any_key
                        {
                        pushFollow(FOLLOW_any_key_in_scope_section176);
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
    // DSLMapWalker.g:89:1: meta_section : ^( VT_META (metalit= LITERAL )? ) ;
    public final void meta_section() throws RecognitionException {
        CommonTree metalit=null;

        try {
            // DSLMapWalker.g:90:2: ( ^( VT_META (metalit= LITERAL )? ) )
            // DSLMapWalker.g:90:4: ^( VT_META (metalit= LITERAL )? )
            {
            match(input,VT_META,FOLLOW_VT_META_in_meta_section193); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // DSLMapWalker.g:90:21: (metalit= LITERAL )?
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==LITERAL) ) {
                    alt8=1;
                }
                switch (alt8) {
                    case 1 :
                        // DSLMapWalker.g:90:21: metalit= LITERAL
                        {
                        metalit=(CommonTree)input.LT(1);
                        match(input,LITERAL,FOLLOW_LITERAL_in_meta_section197); 

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
    // DSLMapWalker.g:100:1: key_section : ^( VT_ENTRY_KEY ( key_sentence )+ ) ;
    public final void key_section() throws RecognitionException {
        try {
            // DSLMapWalker.g:101:2: ( ^( VT_ENTRY_KEY ( key_sentence )+ ) )
            // DSLMapWalker.g:101:4: ^( VT_ENTRY_KEY ( key_sentence )+ )
            {
            match(input,VT_ENTRY_KEY,FOLLOW_VT_ENTRY_KEY_in_key_section214); 

            match(input, Token.DOWN, null); 
            // DSLMapWalker.g:101:19: ( key_sentence )+
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
            	    // DSLMapWalker.g:101:19: key_sentence
            	    {
            	    pushFollow(FOLLOW_key_sentence_in_key_section216);
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
            
            		//((entry_scope)entry_stack.peek()).retval.setMappingKey(((entry_scope)entry_stack.peek()).keybuffer.toString());
            	

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
    // DSLMapWalker.g:107:1: key_sentence : ( variable_definition | vtl= VT_LITERAL | VT_SPACE );
    public final void key_sentence() throws RecognitionException {
        CommonTree vtl=null;

        try {
            // DSLMapWalker.g:108:2: ( variable_definition | vtl= VT_LITERAL | VT_SPACE )
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
                    new NoViableAltException("107:1: key_sentence : ( variable_definition | vtl= VT_LITERAL | VT_SPACE );", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // DSLMapWalker.g:108:4: variable_definition
                    {
                    pushFollow(FOLLOW_variable_definition_in_key_sentence234);
                    variable_definition();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // DSLMapWalker.g:109:4: vtl= VT_LITERAL
                    {
                    vtl=(CommonTree)input.LT(1);
                    match(input,VT_LITERAL,FOLLOW_VT_LITERAL_in_key_sentence241); 
                    
                    		//System.out.println("in key_sentence, literal is " + vtl.getText());
                    		((entry_scope)entry_stack.peek()).keybuffer.append(vtl.getText());
                    	

                    }
                    break;
                case 3 :
                    // DSLMapWalker.g:114:4: VT_SPACE
                    {
                    match(input,VT_SPACE,FOLLOW_VT_SPACE_in_key_sentence250); 
                    
                    		((entry_scope)entry_stack.peek()).keybuffer.append("\\s+");
                    	

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
    // DSLMapWalker.g:124:1: value_section : ^( VT_ENTRY_VAL ( value_sentence )+ ) ;
    public final void value_section() throws RecognitionException {
        try {
            // DSLMapWalker.g:128:2: ( ^( VT_ENTRY_VAL ( value_sentence )+ ) )
            // DSLMapWalker.g:128:4: ^( VT_ENTRY_VAL ( value_sentence )+ )
            {
            match(input,VT_ENTRY_VAL,FOLLOW_VT_ENTRY_VAL_in_value_section273); 

            match(input, Token.DOWN, null); 
            // DSLMapWalker.g:128:19: ( value_sentence )+
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
            	    // DSLMapWalker.g:128:19: value_sentence
            	    {
            	    pushFollow(FOLLOW_value_sentence_in_value_section275);
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
            
            		//((entry_scope)entry_stack.peek()).retval.setMappingValue(((entry_scope)entry_stack.peek()).valuebuffer.toString());
            	

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
    // DSLMapWalker.g:134:1: value_sentence : ( variable_reference | vtl= VT_LITERAL | VT_SPACE );
    public final void value_sentence() throws RecognitionException {
        CommonTree vtl=null;

        try {
            // DSLMapWalker.g:135:2: ( variable_reference | vtl= VT_LITERAL | VT_SPACE )
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
                    new NoViableAltException("134:1: value_sentence : ( variable_reference | vtl= VT_LITERAL | VT_SPACE );", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // DSLMapWalker.g:135:4: variable_reference
                    {
                    pushFollow(FOLLOW_variable_reference_in_value_sentence295);
                    variable_reference();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // DSLMapWalker.g:136:4: vtl= VT_LITERAL
                    {
                    vtl=(CommonTree)input.LT(1);
                    match(input,VT_LITERAL,FOLLOW_VT_LITERAL_in_value_sentence302); 
                    
                    		//System.out.println("in value_sentence, literal is " + vtl.getText());
                    		((entry_scope)entry_stack.peek()).valuebuffer.append(vtl.getText().replaceAll("\\$", "\\\\\\$"));
                    	

                    }
                    break;
                case 3 :
                    // DSLMapWalker.g:141:4: VT_SPACE
                    {
                    match(input,VT_SPACE,FOLLOW_VT_SPACE_in_value_sentence310); 
                    
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
    // DSLMapWalker.g:151:1: literal : theliteral= VT_LITERAL ;
    public final void literal() throws RecognitionException {
        CommonTree theliteral=null;

        try {
            // DSLMapWalker.g:152:2: (theliteral= VT_LITERAL )
            // DSLMapWalker.g:152:4: theliteral= VT_LITERAL
            {
            theliteral=(CommonTree)input.LT(1);
            match(input,VT_LITERAL,FOLLOW_VT_LITERAL_in_literal330); 
            
            	//System.out.println("theliteral is " + theliteral.getText());
            	

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
    // DSLMapWalker.g:159:1: variable_definition : ^( VT_VAR_DEF varname= LITERAL (pattern= VT_PATTERN )? ) ;
    public final void variable_definition() throws RecognitionException {
        CommonTree varname=null;
        CommonTree pattern=null;

        try {
            // DSLMapWalker.g:161:2: ( ^( VT_VAR_DEF varname= LITERAL (pattern= VT_PATTERN )? ) )
            // DSLMapWalker.g:161:6: ^( VT_VAR_DEF varname= LITERAL (pattern= VT_PATTERN )? )
            {
            match(input,VT_VAR_DEF,FOLLOW_VT_VAR_DEF_in_variable_definition351); 

            match(input, Token.DOWN, null); 
            varname=(CommonTree)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition355); 
            // DSLMapWalker.g:161:42: (pattern= VT_PATTERN )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==VT_PATTERN) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // DSLMapWalker.g:161:42: pattern= VT_PATTERN
                    {
                    pattern=(CommonTree)input.LT(1);
                    match(input,VT_PATTERN,FOLLOW_VT_PATTERN_in_variable_definition359); 

                    }
                    break;

            }


            match(input, Token.UP, null); 
            
            		//System.out.println("variable " + varname.getText() + " defined with pattern " + pattern);
            		((entry_scope)entry_stack.peek()).counter++;
            		((entry_scope)entry_stack.peek()).variables.put(varname.getText(), new Integer(((entry_scope)entry_stack.peek()).counter));
            		((entry_scope)entry_stack.peek()).keybuffer.append(pattern != null? "(" + pattern.getText() + ")" : "(.*?)");
            	

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
    // DSLMapWalker.g:171:1: variable_reference : ^(varref= VT_VAR_REF lit= LITERAL ) ;
    public final void variable_reference() throws RecognitionException {
        CommonTree varref=null;
        CommonTree lit=null;

        try {
            // DSLMapWalker.g:172:2: ( ^(varref= VT_VAR_REF lit= LITERAL ) )
            // DSLMapWalker.g:172:4: ^(varref= VT_VAR_REF lit= LITERAL )
            {
            varref=(CommonTree)input.LT(1);
            match(input,VT_VAR_REF,FOLLOW_VT_VAR_REF_in_variable_reference381); 

            match(input, Token.DOWN, null); 
            lit=(CommonTree)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_variable_reference385); 

            match(input, Token.UP, null); 
            
            		//System.out.println("varref is " + varref.getText() + " and points to " + lit.getText());
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
    // DSLMapWalker.g:179:1: condition_key : VT_CONDITION ;
    public final void condition_key() throws RecognitionException {
        try {
            // DSLMapWalker.g:180:2: ( VT_CONDITION )
            // DSLMapWalker.g:180:4: VT_CONDITION
            {
            match(input,VT_CONDITION,FOLLOW_VT_CONDITION_in_condition_key403); 
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
    // DSLMapWalker.g:184:1: consequence_key : VT_CONSEQUENCE ;
    public final void consequence_key() throws RecognitionException {
        try {
            // DSLMapWalker.g:185:2: ( VT_CONSEQUENCE )
            // DSLMapWalker.g:185:4: VT_CONSEQUENCE
            {
            match(input,VT_CONSEQUENCE,FOLLOW_VT_CONSEQUENCE_in_consequence_key418); 
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
    // DSLMapWalker.g:189:1: keyword_key : VT_KEYWORD ;
    public final void keyword_key() throws RecognitionException {
        try {
            // DSLMapWalker.g:190:2: ( VT_KEYWORD )
            // DSLMapWalker.g:190:4: VT_KEYWORD
            {
            match(input,VT_KEYWORD,FOLLOW_VT_KEYWORD_in_keyword_key433); 
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
    // DSLMapWalker.g:194:1: any_key : VT_ANY ;
    public final void any_key() throws RecognitionException {
        try {
            // DSLMapWalker.g:195:2: ( VT_ANY )
            // DSLMapWalker.g:195:4: VT_ANY
            {
            match(input,VT_ANY,FOLLOW_VT_ANY_in_any_key448); 
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
    public static final BitSet FOLLOW_entry_in_mapping_file56 = new BitSet(new long[]{0x0000000000000048L});
    public static final BitSet FOLLOW_entry_in_mapping_entry76 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entry_in_valid_entry97 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_COMMENT_in_valid_entry104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ENTRY_in_entry132 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_scope_section_in_entry134 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_meta_section_in_entry136 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_key_section_in_entry139 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_value_section_in_entry145 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_SCOPE_in_scope_section165 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_key_in_scope_section167 = new BitSet(new long[]{0x0000000000000E08L});
    public static final BitSet FOLLOW_consequence_key_in_scope_section170 = new BitSet(new long[]{0x0000000000000C08L});
    public static final BitSet FOLLOW_keyword_key_in_scope_section173 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_any_key_in_scope_section176 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_META_in_meta_section193 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LITERAL_in_meta_section197 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ENTRY_KEY_in_key_section214 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_key_sentence_in_key_section216 = new BitSet(new long[]{0x00000000000A8008L});
    public static final BitSet FOLLOW_variable_definition_in_key_sentence234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_LITERAL_in_key_sentence241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_SPACE_in_key_sentence250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ENTRY_VAL_in_value_section273 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_value_sentence_in_value_section275 = new BitSet(new long[]{0x00000000000B0008L});
    public static final BitSet FOLLOW_variable_reference_in_value_sentence295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_LITERAL_in_value_sentence302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_SPACE_in_value_sentence310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_LITERAL_in_literal330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_VAR_DEF_in_variable_definition351 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LITERAL_in_variable_definition355 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_PATTERN_in_variable_definition359 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_VAR_REF_in_variable_reference381 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LITERAL_in_variable_reference385 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_CONDITION_in_condition_key403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_CONSEQUENCE_in_consequence_key418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_KEYWORD_in_keyword_key433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ANY_in_any_key448 = new BitSet(new long[]{0x0000000000000002L});

}