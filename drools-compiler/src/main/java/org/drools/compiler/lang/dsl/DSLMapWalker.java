// $ANTLR 3.5 src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g 2014-02-04 17:04:53

    package org.drools.compiler.lang.dsl;

    import java.util.Map;
    import java.util.HashMap;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class DSLMapWalker extends TreeParser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "COLON", "COMMA", "DOT", "EOL", 
		"EQUALS", "EscapeSequence", "IdentifierPart", "LEFT_CURLY", "LEFT_SQUARE", 
		"LITERAL", "MISC", "RIGHT_CURLY", "RIGHT_SQUARE", "VT_ANY", "VT_CONDITION", 
		"VT_CONSEQUENCE", "VT_DSL_GRAMMAR", "VT_ENTRY", "VT_ENTRY_KEY", "VT_ENTRY_VAL", 
		"VT_KEYWORD", "VT_LITERAL", "VT_META", "VT_PATTERN", "VT_QUAL", "VT_SCOPE", 
		"VT_SPACE", "VT_VAR_DEF", "VT_VAR_REF", "WS"
	};
	public static final int EOF=-1;
	public static final int COLON=4;
	public static final int COMMA=5;
	public static final int DOT=6;
	public static final int EOL=7;
	public static final int EQUALS=8;
	public static final int EscapeSequence=9;
	public static final int IdentifierPart=10;
	public static final int LEFT_CURLY=11;
	public static final int LEFT_SQUARE=12;
	public static final int LITERAL=13;
	public static final int MISC=14;
	public static final int RIGHT_CURLY=15;
	public static final int RIGHT_SQUARE=16;
	public static final int VT_ANY=17;
	public static final int VT_CONDITION=18;
	public static final int VT_CONSEQUENCE=19;
	public static final int VT_DSL_GRAMMAR=20;
	public static final int VT_ENTRY=21;
	public static final int VT_ENTRY_KEY=22;
	public static final int VT_ENTRY_VAL=23;
	public static final int VT_KEYWORD=24;
	public static final int VT_LITERAL=25;
	public static final int VT_META=26;
	public static final int VT_PATTERN=27;
	public static final int VT_QUAL=28;
	public static final int VT_SCOPE=29;
	public static final int VT_SPACE=30;
	public static final int VT_VAR_DEF=31;
	public static final int VT_VAR_REF=32;
	public static final int WS=33;

	// delegates
	public TreeParser[] getDelegates() {
		return new TreeParser[] {};
	}

	// delegators


	public DSLMapWalker(TreeNodeStream input) {
		this(input, new RecognizerSharedState());
	}
	public DSLMapWalker(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return DSLMapWalker.tokenNames; }
	@Override public String getGrammarFileName() { return "src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g"; }


	protected static class mapping_file_scope {
		DSLMapping retval;
	}
	protected Stack<mapping_file_scope> mapping_file_stack = new Stack<mapping_file_scope>();


	// $ANTLR start "mapping_file"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:16:1: mapping_file returns [DSLMapping mapping] : ^( VT_DSL_GRAMMAR ( valid_entry )* ) ;
	public final DSLMapping mapping_file() throws RecognitionException {
		mapping_file_stack.push(new mapping_file_scope());
		DSLMapping mapping = null;



		    mapping_file_stack.peek().retval = new DefaultDSLMapping() ;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:23:5: ( ^( VT_DSL_GRAMMAR ( valid_entry )* ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:23:7: ^( VT_DSL_GRAMMAR ( valid_entry )* )
			{
			match(input,VT_DSL_GRAMMAR,FOLLOW_VT_DSL_GRAMMAR_in_mapping_file63); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:23:24: ( valid_entry )*
				loop1:
				while (true) {
					int alt1=2;
					int LA1_0 = input.LA(1);
					if ( (LA1_0==VT_ENTRY) ) {
						alt1=1;
					}

					switch (alt1) {
					case 1 :
						// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:23:24: valid_entry
						{
						pushFollow(FOLLOW_valid_entry_in_mapping_file65);
						valid_entry();
						state._fsp--;

						}
						break;

					default :
						break loop1;
					}
				}

				match(input, Token.UP, null); 
			}


			        mapping = mapping_file_stack.peek().retval;
			    
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			mapping_file_stack.pop();
		}
		return mapping;
	}
	// $ANTLR end "mapping_file"



	// $ANTLR start "valid_entry"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:29:1: valid_entry returns [DSLMappingEntry mappingEntry] : ent= entry ;
	public final DSLMappingEntry valid_entry() throws RecognitionException {
		DSLMappingEntry mappingEntry = null;


		DSLMappingEntry ent =null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:30:5: (ent= entry )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:30:7: ent= entry
			{
			pushFollow(FOLLOW_entry_in_valid_entry96);
			ent=entry();
			state._fsp--;

			mappingEntry = ent; 
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return mappingEntry;
	}
	// $ANTLR end "valid_entry"


	protected static class entry_scope {
		Map<String,Integer> variables;
		AntlrDSLMappingEntry retval;
		StringBuilder keybuffer;
		StringBuilder valuebuffer;
		StringBuilder sentenceKeyBuffer;
		StringBuilder sentenceValueBuffer;
	}
	protected Stack<entry_scope> entry_stack = new Stack<entry_scope>();


	// $ANTLR start "entry"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:34:1: entry returns [DSLMappingEntry mappingEntry] : ^( VT_ENTRY scope_section ( meta_section )? key_section ( value_section )? ) ;
	public final DSLMappingEntry entry() throws RecognitionException {
		entry_stack.push(new entry_scope());
		DSLMappingEntry mappingEntry = null;



		    entry_stack.peek().retval = new AntlrDSLMappingEntry() ;
		    entry_stack.peek().variables = new HashMap<String,Integer>();
		    entry_stack.peek().keybuffer = new StringBuilder();
		    entry_stack.peek().valuebuffer = new StringBuilder();
		    entry_stack.peek().sentenceKeyBuffer = new StringBuilder();
		    entry_stack.peek().sentenceValueBuffer = new StringBuilder();

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:51:5: ( ^( VT_ENTRY scope_section ( meta_section )? key_section ( value_section )? ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:51:7: ^( VT_ENTRY scope_section ( meta_section )? key_section ( value_section )? )
			{
			match(input,VT_ENTRY,FOLLOW_VT_ENTRY_in_entry130); 
			match(input, Token.DOWN, null); 
			pushFollow(FOLLOW_scope_section_in_entry132);
			scope_section();
			state._fsp--;

			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:51:32: ( meta_section )?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==VT_META) ) {
				alt2=1;
			}
			switch (alt2) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:51:32: meta_section
					{
					pushFollow(FOLLOW_meta_section_in_entry134);
					meta_section();
					state._fsp--;

					}
					break;

			}

			pushFollow(FOLLOW_key_section_in_entry137);
			key_section();
			state._fsp--;

			    entry_stack.peek().retval.setVariables( entry_stack.peek().variables );
			                 entry_stack.peek().retval.setMappingKey(entry_stack.peek().sentenceKeyBuffer.toString());
			                 entry_stack.peek().retval.setKeyPattern(entry_stack.peek().keybuffer.toString());
			            
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:56:9: ( value_section )?
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==VT_ENTRY_VAL) ) {
				alt3=1;
			}
			switch (alt3) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:56:9: value_section
					{
					pushFollow(FOLLOW_value_section_in_entry161);
					value_section();
					state._fsp--;

					}
					break;

			}

			match(input, Token.UP, null); 


			        entry_stack.peek().retval.setMappingValue(entry_stack.peek().sentenceValueBuffer.toString());
			        entry_stack.peek().retval.setValuePattern(entry_stack.peek().valuebuffer.toString());
			        mappingEntry = entry_stack.peek().retval;
			        mapping_file_stack.peek().retval.addEntry(mappingEntry);
			    
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			entry_stack.pop();
		}
		return mappingEntry;
	}
	// $ANTLR end "entry"



	// $ANTLR start "scope_section"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:66:1: scope_section : ^(thescope= VT_SCOPE ( condition_key )? ( consequence_key )? ( keyword_key )? ( any_key )? ) ;
	public final void scope_section() throws RecognitionException {
		CommonTree thescope=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:67:5: ( ^(thescope= VT_SCOPE ( condition_key )? ( consequence_key )? ( keyword_key )? ( any_key )? ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:67:7: ^(thescope= VT_SCOPE ( condition_key )? ( consequence_key )? ( keyword_key )? ( any_key )? )
			{
			thescope=(CommonTree)match(input,VT_SCOPE,FOLLOW_VT_SCOPE_in_scope_section191); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:67:27: ( condition_key )?
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( (LA4_0==VT_CONDITION) ) {
					alt4=1;
				}
				switch (alt4) {
					case 1 :
						// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:67:27: condition_key
						{
						pushFollow(FOLLOW_condition_key_in_scope_section193);
						condition_key();
						state._fsp--;

						}
						break;

				}

				// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:67:42: ( consequence_key )?
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( (LA5_0==VT_CONSEQUENCE) ) {
					alt5=1;
				}
				switch (alt5) {
					case 1 :
						// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:67:42: consequence_key
						{
						pushFollow(FOLLOW_consequence_key_in_scope_section196);
						consequence_key();
						state._fsp--;

						}
						break;

				}

				// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:67:59: ( keyword_key )?
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( (LA6_0==VT_KEYWORD) ) {
					alt6=1;
				}
				switch (alt6) {
					case 1 :
						// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:67:59: keyword_key
						{
						pushFollow(FOLLOW_keyword_key_in_scope_section199);
						keyword_key();
						state._fsp--;

						}
						break;

				}

				// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:67:72: ( any_key )?
				int alt7=2;
				int LA7_0 = input.LA(1);
				if ( (LA7_0==VT_ANY) ) {
					alt7=1;
				}
				switch (alt7) {
					case 1 :
						// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:67:72: any_key
						{
						pushFollow(FOLLOW_any_key_in_scope_section202);
						any_key();
						state._fsp--;

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
			// do for sure before leaving
		}
	}
	// $ANTLR end "scope_section"



	// $ANTLR start "meta_section"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:72:1: meta_section : ^( VT_META (metalit= LITERAL )? ) ;
	public final void meta_section() throws RecognitionException {
		CommonTree metalit=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:73:5: ( ^( VT_META (metalit= LITERAL )? ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:73:7: ^( VT_META (metalit= LITERAL )? )
			{
			match(input,VT_META,FOLLOW_VT_META_in_meta_section224); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:73:24: (metalit= LITERAL )?
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( (LA8_0==LITERAL) ) {
					alt8=1;
				}
				switch (alt8) {
					case 1 :
						// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:73:24: metalit= LITERAL
						{
						metalit=(CommonTree)match(input,LITERAL,FOLLOW_LITERAL_in_meta_section228); 
						}
						break;

				}

				match(input, Token.UP, null); 
			}


			        if ( metalit == null || (metalit!=null?metalit.getText():null) == null || (metalit!=null?metalit.getText():null).length() == 0 ) {
			            entry_stack.peek().retval.setMetaData(DSLMappingEntry.EMPTY_METADATA);
			        } else {
			                entry_stack.peek().retval.setMetaData(new DSLMappingEntry.DefaultDSLEntryMetaData( (metalit!=null?metalit.getText():null) ));
			            }
			    
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "meta_section"



	// $ANTLR start "key_section"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:83:1: key_section : ^( VT_ENTRY_KEY ( key_sentence )+ ) ;
	public final void key_section() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:84:5: ( ^( VT_ENTRY_KEY ( key_sentence )+ ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:84:7: ^( VT_ENTRY_KEY ( key_sentence )+ )
			{
			match(input,VT_ENTRY_KEY,FOLLOW_VT_ENTRY_KEY_in_key_section254); 
			match(input, Token.DOWN, null); 
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:84:22: ( key_sentence )+
			int cnt9=0;
			loop9:
			while (true) {
				int alt9=2;
				int LA9_0 = input.LA(1);
				if ( (LA9_0==VT_LITERAL||(LA9_0 >= VT_SPACE && LA9_0 <= VT_VAR_DEF)) ) {
					alt9=1;
				}

				switch (alt9) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:84:22: key_sentence
					{
					pushFollow(FOLLOW_key_sentence_in_key_section256);
					key_sentence();
					state._fsp--;

					}
					break;

				default :
					if ( cnt9 >= 1 ) break loop9;
					EarlyExitException eee = new EarlyExitException(9, input);
					throw eee;
				}
				cnt9++;
			}

			match(input, Token.UP, null); 

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "key_section"



	// $ANTLR start "key_sentence"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:87:1: key_sentence : ( variable_definition |vtl= VT_LITERAL | VT_SPACE );
	public final void key_sentence() throws RecognitionException {
		CommonTree vtl=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:88:5: ( variable_definition |vtl= VT_LITERAL | VT_SPACE )
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
					new NoViableAltException("", 10, 0, input);
				throw nvae;
			}
			switch (alt10) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:88:7: variable_definition
					{
					pushFollow(FOLLOW_variable_definition_in_key_sentence277);
					variable_definition();
					state._fsp--;

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:89:7: vtl= VT_LITERAL
					{
					vtl=(CommonTree)match(input,VT_LITERAL,FOLLOW_VT_LITERAL_in_key_sentence287); 

					        entry_stack.peek().keybuffer.append((vtl!=null?vtl.getText():null));
					        entry_stack.peek().sentenceKeyBuffer.append((vtl!=null?vtl.getText():null));
					    
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:94:7: VT_SPACE
					{
					match(input,VT_SPACE,FOLLOW_VT_SPACE_in_key_sentence301); 

					        entry_stack.peek().keybuffer.append("\\s+");
					        entry_stack.peek().sentenceKeyBuffer.append(" ");
					    
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "key_sentence"



	// $ANTLR start "value_section"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:101:1: value_section : ^( VT_ENTRY_VAL ( value_sentence )+ ) ;
	public final void value_section() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:105:5: ( ^( VT_ENTRY_VAL ( value_sentence )+ ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:105:7: ^( VT_ENTRY_VAL ( value_sentence )+ )
			{
			match(input,VT_ENTRY_VAL,FOLLOW_VT_ENTRY_VAL_in_value_section329); 
			match(input, Token.DOWN, null); 
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:105:22: ( value_sentence )+
			int cnt11=0;
			loop11:
			while (true) {
				int alt11=2;
				int LA11_0 = input.LA(1);
				if ( (LA11_0==VT_LITERAL||LA11_0==VT_SPACE||LA11_0==VT_VAR_REF) ) {
					alt11=1;
				}

				switch (alt11) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:105:22: value_sentence
					{
					pushFollow(FOLLOW_value_sentence_in_value_section331);
					value_sentence();
					state._fsp--;

					}
					break;

				default :
					if ( cnt11 >= 1 ) break loop11;
					EarlyExitException eee = new EarlyExitException(11, input);
					throw eee;
				}
				cnt11++;
			}

			match(input, Token.UP, null); 

			}


			    entry_stack.peek().valuebuffer.append(" ");

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "value_section"



	// $ANTLR start "value_sentence"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:108:1: value_sentence : ( variable_reference |vtl= VT_LITERAL | VT_SPACE );
	public final void value_sentence() throws RecognitionException {
		CommonTree vtl=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:109:5: ( variable_reference |vtl= VT_LITERAL | VT_SPACE )
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
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}
			switch (alt12) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:109:7: variable_reference
					{
					pushFollow(FOLLOW_variable_reference_in_value_sentence353);
					variable_reference();
					state._fsp--;

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:110:7: vtl= VT_LITERAL
					{
					vtl=(CommonTree)match(input,VT_LITERAL,FOLLOW_VT_LITERAL_in_value_sentence363); 

					        entry_stack.peek().valuebuffer.append((vtl!=null?vtl.getText():null));
					        entry_stack.peek().sentenceValueBuffer.append((vtl!=null?vtl.getText():null));
					    
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:115:7: VT_SPACE
					{
					match(input,VT_SPACE,FOLLOW_VT_SPACE_in_value_sentence377); 

					        entry_stack.peek().valuebuffer.append(" ");
					        entry_stack.peek().sentenceValueBuffer.append(" ");
					    
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "value_sentence"



	// $ANTLR start "literal"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:122:1: literal : theliteral= VT_LITERAL ;
	public final void literal() throws RecognitionException {
		CommonTree theliteral=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:123:5: (theliteral= VT_LITERAL )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:123:7: theliteral= VT_LITERAL
			{
			theliteral=(CommonTree)match(input,VT_LITERAL,FOLLOW_VT_LITERAL_in_literal403); 
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "literal"



	// $ANTLR start "variable_definition"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:126:1: variable_definition : ^( VT_VAR_DEF varname= LITERAL ^( VT_QUAL (q= LITERAL )? ) (pattern= VT_PATTERN )? ) ;
	public final void variable_definition() throws RecognitionException {
		CommonTree varname=null;
		CommonTree q=null;
		CommonTree pattern=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:127:5: ( ^( VT_VAR_DEF varname= LITERAL ^( VT_QUAL (q= LITERAL )? ) (pattern= VT_PATTERN )? ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:127:9: ^( VT_VAR_DEF varname= LITERAL ^( VT_QUAL (q= LITERAL )? ) (pattern= VT_PATTERN )? )
			{
			match(input,VT_VAR_DEF,FOLLOW_VT_VAR_DEF_in_variable_definition423); 
			match(input, Token.DOWN, null); 
			varname=(CommonTree)match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition427); 
			match(input,VT_QUAL,FOLLOW_VT_QUAL_in_variable_definition430); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:127:49: (q= LITERAL )?
				int alt13=2;
				int LA13_0 = input.LA(1);
				if ( (LA13_0==LITERAL) ) {
					alt13=1;
				}
				switch (alt13) {
					case 1 :
						// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:127:49: q= LITERAL
						{
						q=(CommonTree)match(input,LITERAL,FOLLOW_LITERAL_in_variable_definition434); 
						}
						break;

				}

				match(input, Token.UP, null); 
			}

			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:127:67: (pattern= VT_PATTERN )?
			int alt14=2;
			int LA14_0 = input.LA(1);
			if ( (LA14_0==VT_PATTERN) ) {
				alt14=1;
			}
			switch (alt14) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:127:67: pattern= VT_PATTERN
					{
					pattern=(CommonTree)match(input,VT_PATTERN,FOLLOW_VT_PATTERN_in_variable_definition440); 
					}
					break;

			}

			match(input, Token.UP, null); 


			        entry_stack.peek().variables.put((varname!=null?varname.getText():null), Integer.valueOf(0));

			        if(q!=null && pattern!=null){
			            entry_stack.peek().sentenceKeyBuffer.append("{"+(varname!=null?varname.getText():null)+":"+(q!=null?q.getText():null)+":"+(pattern!=null?pattern.getText():null)+"}");
			        }else if(q==null && pattern!=null){
			            entry_stack.peek().sentenceKeyBuffer.append("{"+(varname!=null?varname.getText():null)+":"+(pattern!=null?pattern.getText():null)+"}");
			        }else{
			            entry_stack.peek().sentenceKeyBuffer.append("{"+(varname!=null?varname.getText():null)+"}");
			        }

			        if(q == null || (!q.getText().equals("ENUM") && !q.getText().equals("CF") && 
			                          !q.getText().equals("DATE") && !q.getText().equals("BOOLEAN"))){
			            entry_stack.peek().keybuffer.append(pattern != null? "(" + (pattern!=null?pattern.getText():null) + ")" : "(.*?)");
			        }else{
			            entry_stack.peek().keybuffer.append("(.*?)");
			        }
			    
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "variable_definition"



	// $ANTLR start "variable_reference"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:149:1: variable_reference : ^(varref= VT_VAR_REF lit= LITERAL ) ;
	public final void variable_reference() throws RecognitionException {
		CommonTree varref=null;
		CommonTree lit=null;

		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:150:5: ( ^(varref= VT_VAR_REF lit= LITERAL ) )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:150:7: ^(varref= VT_VAR_REF lit= LITERAL )
			{
			varref=(CommonTree)match(input,VT_VAR_REF,FOLLOW_VT_VAR_REF_in_variable_reference471); 
			match(input, Token.DOWN, null); 
			lit=(CommonTree)match(input,LITERAL,FOLLOW_LITERAL_in_variable_reference475); 
			match(input, Token.UP, null); 


			        entry_stack.peek().valuebuffer.append("{" + (lit!=null?lit.getText():null) + "}" );
			         entry_stack.peek().sentenceValueBuffer.append("{"+(lit!=null?lit.getText():null)+"}");
			    
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "variable_reference"



	// $ANTLR start "condition_key"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:157:1: condition_key : VT_CONDITION ;
	public final void condition_key() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:158:5: ( VT_CONDITION )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:158:7: VT_CONDITION
			{
			match(input,VT_CONDITION,FOLLOW_VT_CONDITION_in_condition_key500); 
			entry_stack.peek().retval.setSection(DSLMappingEntry.CONDITION);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "condition_key"



	// $ANTLR start "consequence_key"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:162:1: consequence_key : VT_CONSEQUENCE ;
	public final void consequence_key() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:163:5: ( VT_CONSEQUENCE )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:163:7: VT_CONSEQUENCE
			{
			match(input,VT_CONSEQUENCE,FOLLOW_VT_CONSEQUENCE_in_consequence_key524); 
			entry_stack.peek().retval.setSection(DSLMappingEntry.CONSEQUENCE);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "consequence_key"



	// $ANTLR start "keyword_key"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:167:1: keyword_key : VT_KEYWORD ;
	public final void keyword_key() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:168:5: ( VT_KEYWORD )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:168:7: VT_KEYWORD
			{
			match(input,VT_KEYWORD,FOLLOW_VT_KEYWORD_in_keyword_key548); 
			entry_stack.peek().retval.setSection(DSLMappingEntry.KEYWORD);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "keyword_key"



	// $ANTLR start "any_key"
	// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:172:1: any_key : VT_ANY ;
	public final void any_key() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:173:5: ( VT_ANY )
			// src/main/resources/org/drools/compiler/lang/dsl/DSLMapWalker.g:173:7: VT_ANY
			{
			match(input,VT_ANY,FOLLOW_VT_ANY_in_any_key572); 
			entry_stack.peek().retval.setSection(DSLMappingEntry.ANY);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "any_key"

	// Delegated rules



	public static final BitSet FOLLOW_VT_DSL_GRAMMAR_in_mapping_file63 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_valid_entry_in_mapping_file65 = new BitSet(new long[]{0x0000000000200008L});
	public static final BitSet FOLLOW_entry_in_valid_entry96 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VT_ENTRY_in_entry130 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_scope_section_in_entry132 = new BitSet(new long[]{0x0000000004400000L});
	public static final BitSet FOLLOW_meta_section_in_entry134 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_key_section_in_entry137 = new BitSet(new long[]{0x0000000000800008L});
	public static final BitSet FOLLOW_value_section_in_entry161 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_VT_SCOPE_in_scope_section191 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_condition_key_in_scope_section193 = new BitSet(new long[]{0x00000000010A0008L});
	public static final BitSet FOLLOW_consequence_key_in_scope_section196 = new BitSet(new long[]{0x0000000001020008L});
	public static final BitSet FOLLOW_keyword_key_in_scope_section199 = new BitSet(new long[]{0x0000000000020008L});
	public static final BitSet FOLLOW_any_key_in_scope_section202 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_VT_META_in_meta_section224 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_LITERAL_in_meta_section228 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_VT_ENTRY_KEY_in_key_section254 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_key_sentence_in_key_section256 = new BitSet(new long[]{0x00000000C2000008L});
	public static final BitSet FOLLOW_variable_definition_in_key_sentence277 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VT_LITERAL_in_key_sentence287 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VT_SPACE_in_key_sentence301 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VT_ENTRY_VAL_in_value_section329 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_value_sentence_in_value_section331 = new BitSet(new long[]{0x0000000142000008L});
	public static final BitSet FOLLOW_variable_reference_in_value_sentence353 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VT_LITERAL_in_value_sentence363 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VT_SPACE_in_value_sentence377 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VT_LITERAL_in_literal403 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VT_VAR_DEF_in_variable_definition423 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_LITERAL_in_variable_definition427 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_VT_QUAL_in_variable_definition430 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_LITERAL_in_variable_definition434 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_VT_PATTERN_in_variable_definition440 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_VT_VAR_REF_in_variable_reference471 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_LITERAL_in_variable_reference475 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_VT_CONDITION_in_condition_key500 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VT_CONSEQUENCE_in_consequence_key524 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VT_KEYWORD_in_keyword_key548 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VT_ANY_in_any_key572 = new BitSet(new long[]{0x0000000000000002L});
}
