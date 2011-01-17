grammar DRL;
              
options { 
	output=AST;
	language = Java;
}
  
tokens {
	VT_COMPILATION_UNIT; 
	VT_FUNCTION_IMPORT;

	VT_FACT;
	VT_CONSTRAINTS;
	VT_LABEL;

	VT_QUERY_ID;
	VT_TYPE_DECLARE_ID;
	VT_TYPE_NAME;
	VT_RULE_ID;
	VT_ENTRYPOINT_ID;
	
	VT_RULE_ATTRIBUTES;
	VT_PKG_ATTRIBUTES;

	VT_RHS_CHUNK;
	VT_CURLY_CHUNK;
	VT_SQUARE_CHUNK;
	VT_PAREN_CHUNK;
	VT_BEHAVIOR;

	VT_AND_IMPLICIT;
	VT_AND_PREFIX;
	VT_OR_PREFIX;
	VT_AND_INFIX;
	VT_OR_INFIX;

	VT_ACCUMULATE_INIT_CLAUSE;
	VT_ACCUMULATE_ID_CLAUSE;
	VT_FROM_SOURCE;
	VT_EXPRESSION_CHAIN;
	
	VT_FOR_CE;
	VT_FOR_FUNCTIONS;

	VT_PATTERN;
	VT_FACT_BINDING;
	VT_FACT_OR;
	VT_BIND_FIELD;
	VT_FIELD;

	VT_ACCESSOR_PATH;
	VT_ACCESSOR_ELEMENT;
	
	VT_DATA_TYPE;
	VT_PATTERN_TYPE;
	VT_PACKAGE_ID;
	VT_IMPORT_ID;
	VT_GLOBAL_ID;
	VT_FUNCTION_ID;
	VT_PARAM_LIST;
	
	VT_ARGUMENTS;
	VT_EXPRESSION;

	VK_DATE_EFFECTIVE;
	VK_DATE_EXPIRES;
	VK_LOCK_ON_ACTIVE;
	VK_NO_LOOP;
	VK_AUTO_FOCUS;
	VK_ACTIVATION_GROUP;
	VK_AGENDA_GROUP;
	VK_RULEFLOW_GROUP;
	VK_TIMER;
	VK_CALENDARS;
	VK_DIALECT;
	VK_SALIENCE;
	VK_ENABLED;
	VK_ATTRIBUTES;
	VK_RULE;
	VK_EXTEND;
	VK_IMPLEMENTS; 
	VK_IMPORT;
	VK_PACKAGE;
	VK_QUERY;
	VK_DECLARE;
	VK_FUNCTION;
	VK_GLOBAL;
	VK_EVAL;
	VK_ENTRY_POINT;
	VK_NOT;
	VK_IN;
	VK_OR;
	VK_AND;
	VK_EXISTS;
	VK_FORALL;
	VK_FOR;
	VK_ACTION;
	VK_REVERSE;
	VK_RESULT;
	VK_OPERATOR;
	VK_END;
	VK_INIT;
	VK_INSTANCEOF;
	VK_EXTENDS;
	VK_SUPER;
	VK_PRIMITIVE_TYPE;
	VK_THIS;
	VK_VOID;
	VK_CLASS;
	VK_NEW;
	 
	VK_FINAL;
	VK_IF;
	VK_ELSE;
	VK_WHILE;
	VK_DO;
	VK_CASE;
	VK_DEFAULT;
	VK_TRY;
	VK_CATCH;
	VK_FINALLY;
	VK_SWITCH;
	VK_SYNCHRONIZED;
	VK_RETURN;
	VK_THROW;
	VK_BREAK;
	VK_CONTINUE;
	VK_ASSERT;
	VK_MODIFY;
	VK_STATIC;
	  
	VK_PUBLIC;
	VK_PROTECTED;
	VK_PRIVATE;
	VK_ABSTRACT;
	VK_NATIVE;
	VK_TRANSIENT;
	VK_VOLATILE;
	VK_STRICTFP;
	VK_THROWS;
	VK_INTERFACE;
	VK_ENUM;

	SIGNED_DECIMAL;
	SIGNED_HEX;
	SIGNED_FLOAT;
	
	VT_PROP_KEY;
	VT_PROP_VALUE;
}

@lexer::header {
	package org.drools.lang;

	import org.drools.compiler.DroolsParserException;
}
 
@lexer::members {
	private List<DroolsParserException> errors = new ArrayList<DroolsParserException>();
	private DroolsParserExceptionFactory errorMessageFactory = new DroolsParserExceptionFactory(null, null);

	/** The standard method called to automatically emit a token at the
	 *  outermost lexical rule.  The token object should point into the
	 *  char buffer start..stop.  If there is a text override in 'text',
	 *  use that to set the token's text.  Override this method to emit
	 *  custom Token objects.
	 */
	public Token emit() {
		Token t = new DroolsToken(input, state.type, state.channel, state.tokenStartCharIndex, getCharIndex()-1);
		t.setLine(state.tokenStartLine);
		t.setText(state.text);
		t.setCharPositionInLine(state.tokenStartCharPositionInLine);
		emit(t);
		return t;
	}

	public void reportError(RecognitionException ex) {
		errors.add(errorMessageFactory.createDroolsException(ex));
	}

	/** return the raw DroolsParserException errors */
	public List<DroolsParserException> getErrors() {
		return errors;
	}

	/** Overrided this method to not output mesages */
	public void emitErrorMessage(String msg) {
	}
}

// can't use @parser::header because antlrIDE does not understand it
@header {
	package org.drools.lang;
	
	import java.util.List;
	import java.util.LinkedList;
	import org.drools.compiler.DroolsParserException;
	import org.drools.lang.ParserHelper;
}

@parser::members {
    private ParserHelper helper = new ParserHelper( this,
                                                    tokenNames,
                                                    input,
                                                    state );
                                                    
    public ParserHelper getHelper()                           { return helper; }
    public boolean hasErrors()                                { return helper.hasErrors(); }
    public List<DroolsParserException> getErrors()            { return helper.getErrors(); }
    public List<String> getErrorMessages()                    { return helper.getErrorMessages(); }
    public void enableEditorInterface()                       {        helper.enableEditorInterface(); }
    public void disableEditorInterface()                      {        helper.disableEditorInterface(); }
    public LinkedList<DroolsSentence> getEditorInterface()    { return helper.getEditorInterface(); }
    public void reportError(RecognitionException ex)          {        helper.reportError( ex ); }
    /** Overrided this method to not output mesages */
    public void emitErrorMessage(String msg)                  {}

}

// --------------------------------------------------------
//                      MAIN RULE
// --------------------------------------------------------
compilation_unit
	:	package_statement?
                resync	
		( statement resync )*
		EOF
		-> ^(VT_COMPILATION_UNIT package_statement? statement*) 
	;
	catch [ RecognitionException e ] {
		helper.reportError( e );
	}
	catch [ RewriteEmptyStreamException e ] {
	}
finally {
	if (helper.isEditorInterfaceEnabled && retval.tree == null) {
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
	if (helper.isEditorInterfaceEnabled && helper.hasErrors()) {
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

// --------------------------------------------------------
//          HELPER RULES FOR ERROR RECOVERY
// --------------------------------------------------------
//  this is copied from http://www.antlr.org/wiki/display/ANTLR3/Custom+Syntax+Error+Recovery
resync
@init
{
    // Consume any garbled tokens that come before the next statement
    // or the end of the block. The only slight risk here is that the
    // block becomes MORE inclusive than it should but as the script is
    // in error, this is a better course than throwing out the block
    // when the error occurs and screwing up the whole meaning of
    // the rest of the token stream.
    //
    helper.syncToSet();}
    :   // Deliberately match nothing, causing this rule always to be 
        // entered.
    ;

// --------------------------------------------------------
//                      PACKAGE STATEMENT
// --------------------------------------------------------
package_statement
@init  { helper.pushParaphrases(DroolsParaphraseTypes.PACKAGE); if ( state.backtracking==0 ) helper.beginSentence(DroolsSentenceType.PACKAGE); }
@after { helper.popParaphrases(); }
	:	package_key
		packageOrTypeName SEMICOLON?
	{	helper.emit($SEMICOLON, DroolsEditorType.SYMBOL);	}
		-> ^(package_key packageOrTypeName)
	;



// --------------------------------------------------------
//                      GENERAL STATEMENT
// --------------------------------------------------------
statement
options{
k = 2;
}	:	rule_attribute
	|	function_import_statement  
	|       import_statement 
	|	global 
	|	function
	|	type_declaration
	|	rule
	|	query
	;
	
// --------------------------------------------------------
//                      IMPORT STATEMENTS
// --------------------------------------------------------
import_statement
@init  { helper.pushParaphrases(DroolsParaphraseTypes.IMPORT); if ( state.backtracking==0 ) helper.beginSentence(DroolsSentenceType.IMPORT_STATEMENT);  }
@after { helper.popParaphrases(); }
	:	import_key import_name[DroolsParaphraseTypes.IMPORT] SEMICOLON?
	{	helper.emit($SEMICOLON, DroolsEditorType.SYMBOL);	}
		-> ^(import_key import_name)
	;

function_import_statement
@init  { helper.pushParaphrases(DroolsParaphraseTypes.FUNCTION_IMPORT); if ( state.backtracking==0 ) helper.beginSentence(DroolsSentenceType.FUNCTION_IMPORT_STATEMENT); }
@after { helper.popParaphrases(); }
	:	{helper.validateLT(1, DroolsSoftKeywords.IMPORT) && helper.validateLT(2, DroolsSoftKeywords.FUNCTION)}?=>imp=import_key function_key 
	        import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] SEMICOLON?
	{	helper.emit($SEMICOLON, DroolsEditorType.SYMBOL);	}		
		-> ^(VT_FUNCTION_IMPORT[$imp.start] function_key import_name)
	;

import_name [DroolsParaphraseTypes importType]
	:	id+=ID ( id+=DOT id+=ID )* (id+=DOT id+=STAR)?
	{	helper.emit($id, DroolsEditorType.IDENTIFIER);
		helper.setParaphrasesValue($importType, helper.buildStringFromTokens($id));	}
		-> ^(VT_IMPORT_ID ID+ STAR?)
	;

// --------------------------------------------------------
//                      GLOBAL STATEMENT
// --------------------------------------------------------
global
@init  { helper.pushParaphrases(DroolsParaphraseTypes.GLOBAL);  if ( state.backtracking==0 ) helper.beginSentence(DroolsSentenceType.GLOBAL); }
@after { helper.popParaphrases(); }
	:	global_key data_type global_id SEMICOLON?
	{	helper.emit($SEMICOLON, DroolsEditorType.SYMBOL);	}
		-> ^(global_key data_type global_id)
	;

global_id
	:	id=ID
	{	helper.emit($id, DroolsEditorType.IDENTIFIER);
		helper.setParaphrasesValue(DroolsParaphraseTypes.GLOBAL, $id.text);	}
		-> VT_GLOBAL_ID[$id]
	;

// --------------------------------------------------------
//                      FUNCTION STATEMENT
// --------------------------------------------------------
function
@init  { helper.pushParaphrases(DroolsParaphraseTypes.FUNCTION); if ( state.backtracking==0 ) helper.beginSentence(DroolsSentenceType.FUNCTION);  }
@after { helper.popParaphrases(); }
	:	function_key data_type? function_id parameters curly_chunk
		-> ^(function_key data_type? function_id parameters curly_chunk)
	;

function_id
	:	id=ID
	{	helper.emit($id, DroolsEditorType.IDENTIFIER);
		helper.setParaphrasesValue(DroolsParaphraseTypes.FUNCTION, $id.text);	}
		-> VT_FUNCTION_ID[$id]
	;

// --------------------------------------------------------
//                      QUERY STATEMENT
// --------------------------------------------------------
query
@init  { helper.pushParaphrases(DroolsParaphraseTypes.QUERY); if ( state.backtracking==0 ) helper.beginSentence(DroolsSentenceType.QUERY); }
@after { helper.popParaphrases(); }
	:	query_key query_id 
	{	helper.emit(Location.LOCATION_RULE_HEADER);	}
		parameters? 
	{	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
		normal_lhs_block 
		end=end_key SEMICOLON?
	{	helper.emit($SEMICOLON, DroolsEditorType.SYMBOL);	}
		-> ^(query_key query_id parameters? normal_lhs_block end_key)
	;

query_id
	: 	id=ID
	{	helper.emit($id, DroolsEditorType.IDENTIFIER);
		helper.setParaphrasesValue(DroolsParaphraseTypes.QUERY, $id.text);	} -> VT_QUERY_ID[$id]
	| 	id=STRING
	{	helper.emit($id, DroolsEditorType.IDENTIFIER);
		helper.setParaphrasesValue(DroolsParaphraseTypes.QUERY, $id.text);	} -> VT_QUERY_ID[$id]
	;

// --------------------------------------------------------
//                      DECLARE STATEMENT
// --------------------------------------------------------
type_declaration
@init  { helper.pushParaphrases(DroolsParaphraseTypes.TYPE_DECLARE); if ( state.backtracking==0 ) helper.beginSentence(DroolsSentenceType.TYPE_DECLARATION); }
@after { helper.popParaphrases(); }
	:	declare_key  type_declare_id type_decl_extends? type_decl_implements?
		decl_metadata*
		decl_field*
//		decl_method*
		end_key
		-> ^(declare_key type_declare_id type_decl_extends? type_decl_implements? decl_metadata* decl_field* end_key)
	;

type_declare_id
	: 	id=ID
	{	helper.emit($id, DroolsEditorType.IDENTIFIER);
		helper.setParaphrasesValue(DroolsParaphraseTypes.TYPE_DECLARE, $id.text);	} -> VT_TYPE_DECLARE_ID[$id]
	;
	

	
type_decl_extends
	: extends_key^ typeName
	;
	
type_decl_implements
	:	implements_key^ typeNameList
	;




//sotty: TODO: replace with annotation
//decl_metadata
//	: annotation
//	;

decl_metadata
	:	AT 
	{	helper.emit($AT, DroolsEditorType.SYMBOL);	}
		ID
	{	helper.emit($ID, DroolsEditorType.IDENTIFIER);	}
		paren_chunk?
		-> ^(AT ID paren_chunk?)
	;

decl_field
	:	ID	{	helper.emit($ID, DroolsEditorType.IDENTIFIER);	}
		decl_field_initialization? 
		COLON	{	helper.emit($COLON, DroolsEditorType.SYMBOL);	}
		data_type
		decl_metadata*
		-> ^(ID decl_field_initialization? data_type decl_metadata*)
	;

decl_field_initialization
	:	EQUALS_ASSIGN	{	helper.emit($EQUALS_ASSIGN, DroolsEditorType.SYMBOL);	}
		paren_chunk
	-> ^(EQUALS_ASSIGN paren_chunk)
	;

/* decl_method
options{ backtrack=true; memoize=true; }
	:	methodDeclaration
	|	void_key ID voidMethodDeclaratorRest
	|	ID constructorDeclaratorRest
	;
*/
// --------------------------------------------------------
//                      RULE STATEMENT
// --------------------------------------------------------
rule
@init  { boolean isFailed = true; helper.pushParaphrases(DroolsParaphraseTypes.RULE); if ( state.backtracking==0 ) helper.beginSentence(DroolsSentenceType.RULE);}
@after { helper.popParaphrases(); isFailed = false; }
	:
		rule_key
                rule_id 
	{	helper.emit(Location.LOCATION_RULE_HEADER);	}
		(extend_key rule_id)? decl_metadata* rule_attributes? when_part? rhs_chunk
		-> ^(rule_key rule_id ^(extend_key rule_id)? decl_metadata* rule_attributes? when_part? rhs_chunk)
	;
finally {
	if (helper.isEditorInterfaceEnabled && isFailed) {
		if (input.LA(6) == EOF && input.LA(1) == ID && input.LA(2) == MINUS && input.LA(3) == ID && 
			input.LA(5) == MINUS && input.LA(6) == ID && 
			helper.validateLT(1, DroolsSoftKeywords.LOCK) && helper.validateLT(3, DroolsSoftKeywords.ON) &&
			helper.validateLT(5, DroolsSoftKeywords.ACTIVE)){
			helper.emit(input.LT(1), DroolsEditorType.KEYWORD);
			helper.emit(input.LT(2), DroolsEditorType.KEYWORD);
			helper.emit(input.LT(3), DroolsEditorType.KEYWORD);
			helper.emit(input.LT(4), DroolsEditorType.KEYWORD);
			helper.emit(input.LT(5), DroolsEditorType.KEYWORD);
			helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);
			input.consume();
			input.consume();
			input.consume();
			input.consume();
			input.consume();
		} else if (input.LA(4) == EOF && input.LA(1) == ID && input.LA(2) == MINUS && input.LA(3) == ID && 
			(	(helper.validateLT(1, DroolsSoftKeywords.ACTIVATION) && helper.validateLT(3, DroolsSoftKeywords.GROUP)) ||
				(helper.validateLT(1, DroolsSoftKeywords.DATE) && helper.validateLT(3, DroolsSoftKeywords.EXPIRES)) ||
				(helper.validateLT(1, DroolsSoftKeywords.NO) && helper.validateLT(3, DroolsSoftKeywords.LOOP)) ||
				(helper.validateLT(1, DroolsSoftKeywords.DATE) && helper.validateLT(3, DroolsSoftKeywords.EFFECTIVE)) ||
				(helper.validateLT(1, DroolsSoftKeywords.AUTO) && helper.validateLT(3, DroolsSoftKeywords.FOCUS)) ||
				(helper.validateLT(1, DroolsSoftKeywords.ACTIVATION) && helper.validateLT(3, DroolsSoftKeywords.GROUP)) ||
				(helper.validateLT(1, DroolsSoftKeywords.RULEFLOW) && helper.validateLT(3, DroolsSoftKeywords.GROUP)) ||
				(helper.validateLT(1, DroolsSoftKeywords.AGENDA) && helper.validateLT(3, DroolsSoftKeywords.GROUP))	)){
			helper.emit(input.LT(1), DroolsEditorType.KEYWORD);
			helper.emit(input.LT(2), DroolsEditorType.KEYWORD);
			helper.emit(input.LT(3), DroolsEditorType.KEYWORD);
			helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);
			input.consume();
			input.consume();
			input.consume();
		} else if (input.LA(2) == EOF && input.LA(1) == ID && 
				(helper.validateLT(1, DroolsSoftKeywords.DIALECT) || helper.validateLT(1, DroolsSoftKeywords.ENABLED) ||
				 helper.validateLT(1, DroolsSoftKeywords.SALIENCE) || helper.validateLT(1, DroolsSoftKeywords.DURATION) ||
				 helper.validateLT(1, DroolsSoftKeywords.TIMER))){
			helper.emit(input.LT(1), DroolsEditorType.KEYWORD);
			helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);
			input.consume();
		}
	}
}

when_part
	: 	WHEN {	helper.emit($WHEN, DroolsEditorType.KEYWORD);	}
		COLON? {	helper.emit($COLON, DroolsEditorType.SYMBOL);	}
	{	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
		normal_lhs_block
	->	WHEN normal_lhs_block
	;

rule_id
	: 	id=ID
	{	helper.emit($id, DroolsEditorType.IDENTIFIER);
		helper.setParaphrasesValue(DroolsParaphraseTypes.RULE, $id.text);	} -> VT_RULE_ID[$id]
	| 	id=STRING
	{	helper.emit($id, DroolsEditorType.IDENTIFIER);
		helper.setParaphrasesValue(DroolsParaphraseTypes.RULE, $id.text);	} -> VT_RULE_ID[$id]
	;

rule_attributes
	:	( attributes_key COLON {	helper.emit($COLON, DroolsEditorType.SYMBOL);	} )? 
		rule_attribute ( COMMA? {	helper.emit($COMMA, DroolsEditorType.SYMBOL);	} attr=rule_attribute )*
		-> ^(VT_RULE_ATTRIBUTES attributes_key? rule_attribute+)
	;

rule_attribute
@init  { boolean isFailed = true; helper.pushParaphrases(DroolsParaphraseTypes.RULE_ATTRIBUTE); }
@after { helper.popParaphrases(); isFailed = false; if (!(retval.tree instanceof CommonErrorNode)) helper.emit(Location.LOCATION_RULE_HEADER); }
	:	salience 
	|	no_loop
	|	agenda_group  
	|	timer  
	|	activation_group 
	|	auto_focus 
	|	date_effective 
	|	date_expires 
	|	enabled 
	|	ruleflow_group 
	|	lock_on_active
	|	dialect 
	|   calendars
	;
finally {
	if (helper.isEditorInterfaceEnabled && isFailed) {
		if (input.LA(2) == EOF && input.LA(1) == ID){
			helper.emit(input.LT(1), DroolsEditorType.IDENTIFIER);
			input.consume();
		}
	}
}
date_effective
	:	date_effective_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	helper.emit($STRING, DroolsEditorType.STRING_CONST );	}
	;

date_expires
	:	date_expires_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	helper.emit($STRING, DroolsEditorType.STRING_CONST );	}
	;
	
enabled
	:	enabled_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} 
	    ( BOOL {	helper.emit($BOOL, DroolsEditorType.BOOLEAN_CONST );	}
	    | paren_chunk 
	    )
	;	

salience
	:	salience_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	}
		( signed_decimal 
		| paren_chunk
		)
	;

no_loop
	:	no_loop_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} BOOL?
	{	helper.emit($BOOL, DroolsEditorType.BOOLEAN_CONST );	}
	;

auto_focus
	:	auto_focus_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} BOOL?
	{	helper.emit($BOOL, DroolsEditorType.BOOLEAN_CONST );	}
	;	
	
activation_group
	:	activation_group_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	helper.emit($STRING, DroolsEditorType.STRING_CONST );	}
	;

ruleflow_group
	:	ruleflow_group_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	helper.emit($STRING, DroolsEditorType.STRING_CONST );	}
	;

agenda_group
	:	agenda_group_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	helper.emit($STRING, DroolsEditorType.STRING_CONST );	}
	;

timer
	:	(duration_key^|timer_key^) {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} 
		( signed_decimal 
	    	| paren_chunk
	    	)
	;	
	
calendars
	:	calendars_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} string_list
	;

dialect
	:	dialect_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	helper.emit($STRING, DroolsEditorType.STRING_CONST );	}
	;			
	
lock_on_active
	:	lock_on_active_key^ {	helper.emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} BOOL?
	{	helper.emit($BOOL, DroolsEditorType.BOOLEAN_CONST );	}
	;

// --------------------------------------------------------
//                      LHS
// --------------------------------------------------------
normal_lhs_block
	:	lhs*
	->	^(VT_AND_IMPLICIT lhs*)
	;

lhs	:	lhs_or
	;

lhs_or
@init{
	Token orToken = null;
}	:	(LEFT_PAREN or_key)=> 
		LEFT_PAREN  {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}
			or=or_key
	{	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	}
			lhs_and+ 
		RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	} // PREFIX 
		-> ^(VT_OR_PREFIX[$or.start] lhs_and+ RIGHT_PAREN)
	|	(lhs_and -> lhs_and) 
		( (or_key)=> (value=or_key {orToken = $value.start;} ) 
	{	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	}
		lhs_and 
		-> ^(VT_OR_INFIX[orToken] $lhs_or lhs_and))*
	;

lhs_and
@init{
	Token andToken = null;
}	:	(LEFT_PAREN and_key)=> 
		LEFT_PAREN {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			and=and_key
	{	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	}
			lhs_unary+ 
		RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}  // PREFIX
		-> ^(VT_AND_PREFIX[$and.start] lhs_unary+ RIGHT_PAREN)
	|	(lhs_unary -> lhs_unary) 
		( (and_key)=> (value=and_key {andToken = $value.start;} ) 
	{	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	}
		lhs_unary 
		-> ^(VT_AND_INFIX[andToken] $lhs_and lhs_unary) )*
	;

lhs_unary
	:	(	lhs_exist
		|{helper.validateNotWithBinding()}?=>	lhs_not_binding
		|	lhs_not
		|	lhs_eval
		|	lhs_forall
		| 	lhs_for
		|	LEFT_PAREN! {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL); helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	}  
				lhs_or 
			RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
		|	pattern_source
		)
	;

lhs_exist
	:	exists_key
	{	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS);	}
	        ( (LEFT_PAREN (or_key|and_key))=> lhs_or //prevent "((" 
		| LEFT_PAREN {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			lhs_or 
		  RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
	        | lhs_pattern
	        )
	        -> ^(exists_key lhs_or? lhs_pattern? RIGHT_PAREN?)
	;

lhs_not_binding
	:	not_key fact_binding
	-> ^(not_key ^(VT_PATTERN fact_binding))
	;

lhs_not	:	not_key
	{	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT);	}
		( (LEFT_PAREN (or_key|and_key))=> {	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	} lhs_or //prevent "((" 
		|	LEFT_PAREN {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL); helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	} 
				lhs_or 
			RIGHT_PAREN  {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
		| 	lhs_pattern )
	        -> ^(not_key lhs_or? lhs_pattern? RIGHT_PAREN?)
	;

lhs_eval
	:	ev=eval_key
	{	helper.emit(Location.LOCATION_LHS_INSIDE_EVAL);	}
		pc=paren_chunk
	{	if (((DroolsTree) $pc.tree).getText() != null){
			helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	            		
		}
	}
	{	String body = helper.safeSubstring( $pc.text, 1, $pc.text.length()-1 );
		helper.checkTrailingSemicolon( body, $ev.start );	}
		-> ^(eval_key paren_chunk)
	;

lhs_forall
	:	forall_key 
		LEFT_PAREN {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			pattern_source+ 
		RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
		-> ^(forall_key pattern_source+ RIGHT_PAREN)
	;

lhs_for
	:	for_key 
		LEFT_PAREN {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			lhs_or SEMICOLON
			for_functions
			(SEMICOLON constraints)?
		RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
		-> ^(VT_FOR_CE lhs_or for_functions constraints?)
	;
	
for_functions
	:	for_function ( COMMA for_function )*
		-> ^(VT_FOR_FUNCTIONS for_function+ )
	;
	
for_function 
	: 	label ID arguments_as_string
	-> ^(ID label arguments_as_string )
	;	
	
arguments_as_string
options { backtrack=true; memoize=true; }
  : LEFT_PAREN (expression_as_string (COMMA expression_as_string)*)? RIGHT_PAREN
  -> ^(VT_ARGUMENTS expression_as_string*)
  ;
  
expression_as_string
  : ex=expression
  -> VT_EXPRESSION[$ex.start, $ex.text]
  ;  

pattern_source
@init { boolean isFailed = true;	}
@after { isFailed = false;	}
	:	lhs_pattern
		over_clause?
		(
			FROM^
		{	helper.emit($FROM, DroolsEditorType.KEYWORD);
			helper.emit(Location.LOCATION_LHS_FROM);	}
		        (  accumulate_statement
		          | collect_statement 
		          | entrypoint_statement
		          | from_source
		        )
		)?
	;
finally {
	if (helper.isEditorInterfaceEnabled && input.LA(3) == EOF && input.LA(1) == ACCUMULATE) {
			helper.emit(input.LT(1), DroolsEditorType.KEYWORD);
			helper.emit(input.LT(2), DroolsEditorType.SYMBOL);
			input.consume();
			helper.emit(true, Location.LOCATION_LHS_FROM_ACCUMULATE);
	} else if (helper.isEditorInterfaceEnabled && input.LA(3) == EOF && input.LA(1) == COLLECT) {
			helper.emit(input.LT(1), DroolsEditorType.KEYWORD);
			helper.emit(input.LT(2), DroolsEditorType.SYMBOL);
			input.consume();
			helper.emit(true, Location.LOCATION_LHS_FROM_COLLECT);
	}
}

over_clause
	:	OVER^ {	helper.emit($OVER, DroolsEditorType.KEYWORD);	} over_elements 
			(COMMA! {	helper.emit($COMMA, DroolsEditorType.SYMBOL);	} over_elements)*
	;

over_elements
	:	id1=ID {	helper.emit($id1, DroolsEditorType.IDENTIFIER);	} 
		COLON {	helper.emit($COLON, DroolsEditorType.SYMBOL);	} 
		id2=ID {	helper.emit($id2, DroolsEditorType.IDENTIFIER);	} 
		LEFT_PAREN 
			( 
				t=TimePeriod | t=DECIMAL
			) 
		RIGHT_PAREN
	-> ^(VT_BEHAVIOR $id1 $id2 VT_PAREN_CHUNK[$t])
	;

accumulate_statement
	:	ACCUMULATE {	helper.emit($ACCUMULATE, DroolsEditorType.KEYWORD);	}
	{	helper.emit(Location.LOCATION_LHS_FROM_ACCUMULATE);	}
		LEFT_PAREN {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			lhs_or 
		COMMA? {	helper.emit($COMMA, DroolsEditorType.SYMBOL);	} 
		(	accumulate_init_clause
		|	accumulate_id_clause
		)
		RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
	{	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
		-> ^(ACCUMULATE lhs_or accumulate_init_clause? accumulate_id_clause? RIGHT_PAREN)
	;


accumulate_init_clause
@init  { boolean isFailed = true;	}
@after { isFailed = false;	}
	:	init_key 
	{	helper.emit(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT);	}
		pc1=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] cm1=COMMA? {	helper.emit($cm1, DroolsEditorType.SYMBOL);	} 
	{	if (pc1 != null && ((DroolsTree) pc1.getTree()).getText() != null) helper.emit(Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION);	}
		action_key pc2=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] cm2=COMMA? {	helper.emit($cm2, DroolsEditorType.SYMBOL);	} 
	{	if (pc1 != null && ((DroolsTree) pc1.getTree()).getText() != null && pc2 != null && ((DroolsTree) pc2.getTree()).getText() != null ) helper.emit(Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE);	}
	(	reverse_key pc3=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] cm3=COMMA? {	helper.emit($cm3, DroolsEditorType.SYMBOL);	} )?

	{	if ((pc1 != null && ((DroolsTree) pc1.tree).getText() != null) &&
            			(pc2 != null && ((DroolsTree) pc2.tree).getText() != null) &&
            			(pc3 != null && ((DroolsTree) pc3.tree).getText() != null)) {
			helper.emit(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT);
		}	
	}
		res1=result_key {	helper.emit($res1.start, DroolsEditorType.KEYWORD);	} pc4=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE]
	-> ^(VT_ACCUMULATE_INIT_CLAUSE ^(init_key $pc1) ^(action_key $pc2) ^(reverse_key $pc3)? ^(result_key $pc4))
	;
finally { 
	if (helper.isEditorInterfaceEnabled && isFailed && input.LA(1) == ID && helper.validateLT(1, DroolsSoftKeywords.RESULT)) {
		helper.emit(input.LT(1), DroolsEditorType.KEYWORD);
		input.consume();
		if (input.LA(1) == LEFT_PAREN){
			input.consume();
			helper.emit(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE);
		}
	}
}

accumulate_paren_chunk[int locationType]
@init{
	String text = "";
}	:	pc=accumulate_paren_chunk_data[false,$locationType] {text = $pc.text;} 
	-> VT_PAREN_CHUNK[$pc.start,text]
	;

accumulate_paren_chunk_data[boolean isRecursive, int locationType]
	:	lp1=LEFT_PAREN
		{	if (!isRecursive) {
				helper.emit($lp1, DroolsEditorType.SYMBOL);
				helper.emit($locationType);
			} else {
				helper.emit($lp1, DroolsEditorType.CODE_CHUNK);
			}	
		}
			(any=~ ( LEFT_PAREN | RIGHT_PAREN ) { helper.emit($any, DroolsEditorType.CODE_CHUNK); } | accumulate_paren_chunk_data[true,-1] )* 
		rp1=RIGHT_PAREN
		{	if (!isRecursive) {
				helper.emit($rp1, DroolsEditorType.SYMBOL);
			} else {
				helper.emit($rp1, DroolsEditorType.CODE_CHUNK);
			}	
		}	
	;

accumulate_id_clause
	:	ID {	helper.emit($ID, DroolsEditorType.IDENTIFIER);	}
		paren_chunk
	-> ^(VT_ACCUMULATE_ID_CLAUSE ID paren_chunk)
	;

collect_statement
	:	COLLECT {	helper.emit($COLLECT, DroolsEditorType.KEYWORD);	}
	{	helper.emit(Location.LOCATION_LHS_FROM_COLLECT);	}
		LEFT_PAREN {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}
			pattern_source 
		RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
	{	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
	-> ^(COLLECT pattern_source RIGHT_PAREN)
	;

entrypoint_statement
	:	entry_point_key 
	{	helper.emit(Location.LOCATION_LHS_FROM_COLLECT);	}
		entrypoint_id
	{	helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
	-> ^(entry_point_key entrypoint_id)
	;

entrypoint_id
	: 	value=ID {	helper.emit($value, DroolsEditorType.IDENTIFIER);	}
		-> VT_ENTRYPOINT_ID[$value]
	| 	value=STRING {	helper.emit($value, DroolsEditorType.IDENTIFIER);	}
		-> VT_ENTRYPOINT_ID[$value]
	;


from_source
options { backtrack=true; memoize=true; }
	:	fs=expression  { if ( input.LA(1) != EOF || input.get(input.index() - 1).getType() == WS ) { helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION); } }
	        -> VT_FROM_SOURCE[$fs.text]
	;

// --------------------------------------------------------
//                      PATTERN
// --------------------------------------------------------
lhs_pattern
	:	fact_binding -> ^(VT_PATTERN fact_binding)
	|	fact -> ^(VT_PATTERN fact)
	;

fact_binding
 	:	label
		( fact
 		| LEFT_PAREN {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}
 			fact_binding_expression 
 		  RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
 		)
 	-> ^(VT_FACT_BINDING label fact? fact_binding_expression? RIGHT_PAREN?)
	;

fact_binding_expression
@init{
	Token orToken = null;
}	:	(fact -> fact) ( (value=or_key {orToken = $value.start;}|pipe=DOUBLE_PIPE {orToken = $pipe;}) fact 
		-> ^(VT_FACT_OR[orToken] $fact_binding_expression fact) )*
	;

fact
@init  { boolean isFailedOnConstraints = true; helper.pushParaphrases(DroolsParaphraseTypes.PATTERN); }
@after { helper.popParaphrases();	}
	:	pattern_type 
		LEFT_PAREN {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
	{	helper.emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	}
			constraints? 
		RIGHT_PAREN {	isFailedOnConstraints = false;	}
	{	if ($RIGHT_PAREN.text.equals(")") ){ //WORKAROUND FOR ANTLR BUG!
			helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);
			helper.emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);
		}	}
	->	^(VT_FACT pattern_type constraints?)
	;
finally {
	if (helper.isEditorInterfaceEnabled && isFailedOnConstraints && input.LA(1) == EOF && input.get(input.index() - 1).getType() == WS){
		if (!(helper.getActiveSentence().getContent().getLast() instanceof Integer) && input.LA(-1) != COLON) {
			helper.emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
		}
	}
}

constraints
	:	constraint ( COMMA! 
	{	helper.emit($COMMA, DroolsEditorType.SYMBOL);
		helper.emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	} constraint )*
	;

constraint
	:	or_constr
	;

or_constr
	:	and_constr ( DOUBLE_PIPE^ 
	{	helper.emit($DOUBLE_PIPE, DroolsEditorType.SYMBOL);	} and_constr )* 
	;

and_constr
	:	unary_constr ( DOUBLE_AMPER^ 
	{	helper.emit($DOUBLE_AMPER, DroolsEditorType.SYMBOL);;	} unary_constr )*
	;

unary_constr
options { k=2; }
@init { boolean isFailed = true;	}
@after { isFailed = false;	}
	:	eval_key^ paren_chunk
	|	field_constraint
	| 	LEFT_PAREN! {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}  
			or_constr 
		RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
	;
finally { 
	if (helper.isEditorInterfaceEnabled && isFailed && input.LA(2) == EOF && input.LA(1) == ID) {
		helper.emit(input.LT(1), DroolsEditorType.IDENTIFIER);
		input.consume();
		if (input.get(input.index() - 1).getType() == WS)
			helper.emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
	}
}

field_constraint
@init{
	boolean isArrow = false;
}	:	label accessor_path 
		( or_restr_connective | arw=ARROW {	helper.emit($ARROW, DroolsEditorType.SYMBOL);	} paren_chunk {isArrow = true;})?
		-> {isArrow}? ^(VT_BIND_FIELD label ^(VT_FIELD accessor_path)) ^(VK_EVAL[$arw] paren_chunk)?
		-> ^(VT_BIND_FIELD label ^(VT_FIELD accessor_path or_restr_connective?))
	|	accessor_path or_restr_connective
		-> ^(VT_FIELD accessor_path or_restr_connective)
	;

label
	:	value=ID {	helper.emit($ID, DroolsEditorType.IDENTIFIER_VARIABLE);	} 
		COLON {	helper.emit($COLON, DroolsEditorType.SYMBOL);	} 
		-> VT_LABEL[$value]
	;

or_restr_connective
	:	and_restr_connective ({(helper.validateRestr())}?=> DOUBLE_PIPE^ 
	{	helper.emit($DOUBLE_PIPE, DroolsEditorType.SYMBOL);	}  and_restr_connective )* 
	;
catch [ RecognitionException re ] {
	if (!helper.lookaheadTest){
        helper.reportError(re);
        recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
	} else {
		throw re;
	}
}

and_restr_connective
	:	constraint_expression ({(helper.validateRestr())}?=> DOUBLE_AMPER^ 
	{	helper.emit($DOUBLE_AMPER, DroolsEditorType.SYMBOL);	} constraint_expression )*
	;
catch [ RecognitionException re ] {
	if (!helper.lookaheadTest){
        helper.reportError(re);
        recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
	} else {
		throw re;
	}
}

constraint_expression
options{ k=3; }	
	:	compound_operator
	|	simple_operator
	|	LEFT_PAREN! {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			or_restr_connective 
		RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	} 
	;
catch [ RecognitionException re ] {
	if (!helper.lookaheadTest){
        helper.reportError(re);
        recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
	} else {
		throw re;
	}
}
finally {
	if (helper.isEditorInterfaceEnabled && input.LA(2) == EOF && input.LA(1) == ID) {
		helper.emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
		helper.emit(input.LT(1), DroolsEditorType.KEYWORD);
		input.consume();
		helper.emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
	} else if (helper.isEditorInterfaceEnabled && input.LA(3) == EOF && input.LA(1) == ID && 
				input.LA(2) == ID && helper.validateLT(1, DroolsSoftKeywords.NOT)) {
		helper.emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
		helper.emit(input.LT(1), DroolsEditorType.KEYWORD);
		helper.emit(input.LT(2), DroolsEditorType.KEYWORD);
		input.consume();
		input.consume();
		helper.emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
	} else if (helper.isEditorInterfaceEnabled && input.LA(3) == EOF  && input.LA(1) == ID && helper.validateLT(1, DroolsSoftKeywords.IN)) {
		helper.emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
		helper.emit(input.LT(1), DroolsEditorType.KEYWORD);
		helper.emit(input.LT(2), DroolsEditorType.SYMBOL);
		input.consume();
		input.consume();
		helper.emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
	} else if (helper.isEditorInterfaceEnabled && input.LA(3) == EOF && input.LA(1) == ID) {
		helper.emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
		helper.emit(input.LT(1), DroolsEditorType.KEYWORD);
		helper.emit(input.LT(2), DroolsEditorType.IDENTIFIER);
		input.consume();
		input.consume();
		if (input.get(input.index() - 1).getType() == WS){
			helper.emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_END);
		}
	}
}

simple_operator
@init {if ( state.backtracking==0 ) helper.emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);}
	:	
	(
		EQUALS^ {	helper.emit($EQUALS, DroolsEditorType.SYMBOL);	}
	|	GREATER^ {	helper.emit($GREATER, DroolsEditorType.SYMBOL);	}
	|	GREATER_EQUALS^ {	helper.emit($GREATER_EQUALS, DroolsEditorType.SYMBOL);	}
	|	LESS^ {	helper.emit($LESS, DroolsEditorType.SYMBOL);	}
	|	LESS_EQUALS^ {	helper.emit($LESS_EQUALS, DroolsEditorType.SYMBOL);	}
	|	NOT_EQUALS^ {	helper.emit($NOT_EQUALS, DroolsEditorType.SYMBOL);	}
	|	not_key?
		(	operator_key^ (operator_params)?	)
	)
	{	helper.emit(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);	}
	expression_value
	;

operator_params
	:	(operator_args) => ops=operator_args 
	-> VT_SQUARE_CHUNK[$ops.start, $ops.text]
	| square_chunk
	;

operator_args
	: LEFT_SQUARE! operator_arg (COMMA! operator_arg)* RIGHT_SQUARE!
	;
	
operator_arg
	: TimePeriod
	;	

//Simple Syntax Sugar
compound_operator 
@init { if ( state.backtracking==0 ) helper.emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR); }
	:	
	( in_key^ | not_key in_key^ ) 
	{	helper.emit(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);	}
		LEFT_PAREN! {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}
			expression_value ( COMMA! {	helper.emit($COMMA, DroolsEditorType.SYMBOL);	} expression_value )* 
		RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
	{	helper.emit(Location.LOCATION_LHS_INSIDE_CONDITION_END);	}
	;
finally { 
	if (helper.isEditorInterfaceEnabled && input.LA(2) == EOF && input.LA(1) == DOUBLE_PIPE) {
		helper.emit(input.LT(1), DroolsEditorType.SYMBOL);
		input.consume();
		helper.emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
	}	}

expression_value
	:	(accessor_path
	|	signed_literal 
	|	paren_chunk)
	{	if (helper.isEditorInterfaceEnabled && !(input.LA(1) == EOF && input.get(input.index() - 1).getType() != WS))
			helper.emit(Location.LOCATION_LHS_INSIDE_CONDITION_END);	}
	;
finally { 
	if (helper.isEditorInterfaceEnabled && input.LA(2) == EOF) {
		if (input.LA(1) == DOUBLE_PIPE) {
			helper.emit(input.LT(1), DroolsEditorType.SYMBOL);
			input.consume();
			helper.emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
		}
	}
}

signed_literal
        :       STRING                  { helper.emit($STRING, DroolsEditorType.STRING_CONST);  }
        |       signed_decimal
        |       signed_hex
        |       signed_float
        |       BOOL                    { helper.emit($BOOL, DroolsEditorType.BOOLEAN_CONST); }
        |       NULL                    { helper.emit($NULL, DroolsEditorType.NULL_CONST); }
        ;

signed_decimal
        :       sdm=signed_decimal_match -> SIGNED_DECIMAL[$sdm.text]
        ;

signed_decimal_match
        :       (PLUS { helper.emit($PLUS, DroolsEditorType.NUMERIC_CONST); } | MINUS { helper.emit($MINUS, DroolsEditorType.NUMERIC_CONST); })?
                DECIMAL { helper.emit($DECIMAL, DroolsEditorType.NUMERIC_CONST); }
        ;

signed_hex
        :       sdm=signed_hex_match -> SIGNED_HEX[$sdm.text]
        ;

signed_hex_match
        :       (PLUS { helper.emit($PLUS, DroolsEditorType.NUMERIC_CONST); } | MINUS { helper.emit($MINUS, DroolsEditorType.NUMERIC_CONST); })?
                HEX { helper.emit($HEX, DroolsEditorType.NUMERIC_CONST); }
        ;

signed_float
        :       sdm=signed_float_match -> SIGNED_FLOAT[$sdm.text]
        ;

signed_float_match
        :       (PLUS { helper.emit($PLUS, DroolsEditorType.NUMERIC_CONST); } | MINUS { helper.emit($MINUS, DroolsEditorType.NUMERIC_CONST); })?
                FLOAT { helper.emit($FLOAT, DroolsEditorType.NUMERIC_CONST); }
        ;
pattern_type
	:	id+=ID ( id+=DOT id+=ID )* 
	{	helper.emit($id, DroolsEditorType.IDENTIFIER);
		helper.setParaphrasesValue(DroolsParaphraseTypes.PATTERN, helper.buildStringFromTokens($id));	} 
	    dimension_definition*
		-> ^(VT_PATTERN_TYPE ID+ dimension_definition*)
	;

data_type
	:	id+=ID ( id+=DOT id+=ID )* dimension_definition*
	{	helper.emit($id, DroolsEditorType.IDENTIFIER);	}
		-> ^(VT_DATA_TYPE ID+ dimension_definition*)
	;

dimension_definition
	:	LEFT_SQUARE {	helper.emit($LEFT_SQUARE, DroolsEditorType.SYMBOL);	} 
		RIGHT_SQUARE {	helper.emit($RIGHT_SQUARE, DroolsEditorType.SYMBOL);	} 
	;

accessor_path
	:	accessor_element ( DOT {	helper.emit($DOT, DroolsEditorType.IDENTIFIER);	} accessor_element )*
	-> ^(VT_ACCESSOR_PATH accessor_element+)
	;

accessor_element
	:	ID {	helper.emit($ID, DroolsEditorType.IDENTIFIER);	}
		square_chunk*
	-> ^(VT_ACCESSOR_ELEMENT ID square_chunk*)
	;

// --------------------------------------------------------
//                      CHUNKS
// --------------------------------------------------------
rhs_chunk
@init{
	String text = "";
}	:	rc=rhs_chunk_data {text = $rc.text;}
	-> VT_RHS_CHUNK[$rc.start,text]
	;

rhs_chunk_data
	:	THEN 
	{	if ($THEN.text.equalsIgnoreCase("then")){
			helper.emit($THEN, DroolsEditorType.KEYWORD);
			helper.emit(Location.LOCATION_RHS);
		}	}
			not_end_key* 
		end_key 
		SEMICOLON? {	helper.emit($SEMICOLON, DroolsEditorType.KEYWORD);	}
	;

curly_chunk
@init{
	String text = "";
}	:	cc=curly_chunk_data[false] {text = $cc.text;}
	-> VT_CURLY_CHUNK[$cc.start,text]
	;

curly_chunk_data[boolean isRecursive]
	:	lc1=LEFT_CURLY
		{	if (!isRecursive) {
				helper.emit($lc1, DroolsEditorType.SYMBOL);
			} else {
				helper.emit($lc1, DroolsEditorType.CODE_CHUNK);
			}	
		}
			(any=~ ( LEFT_CURLY | RIGHT_CURLY ) { helper.emit($any, DroolsEditorType.CODE_CHUNK); } | curly_chunk_data[true] )* 
		rc1=RIGHT_CURLY
		{	if (!isRecursive) {
				helper.emit($rc1, DroolsEditorType.SYMBOL);
			} else {
				helper.emit($rc1, DroolsEditorType.CODE_CHUNK);
			}	
		}	
	;

paren_chunk
@init{
	String text = "";
}	:	pc=paren_chunk_data[false] {text = $pc.text;} 
	-> VT_PAREN_CHUNK[$pc.start,text]
	;

paren_chunk_data[boolean isRecursive]
	:	lp1=LEFT_PAREN
		{	if (!isRecursive) {
				helper.emit($lp1, DroolsEditorType.SYMBOL);
			} else {
				helper.emit($lp1, DroolsEditorType.CODE_CHUNK);
			}	
		}
			(any=~ ( LEFT_PAREN | RIGHT_PAREN ) { helper.emit($any, DroolsEditorType.CODE_CHUNK); } | paren_chunk_data[true] )* 
		rp1=RIGHT_PAREN
		{	if (!isRecursive) {
				helper.emit($rp1, DroolsEditorType.SYMBOL);
			} else {
				helper.emit($rp1, DroolsEditorType.CODE_CHUNK);
			}	
		}	
	;

square_chunk
@init{
	String text = "";
}	:	sc=square_chunk_data[false] {text = $sc.text;}
	-> VT_SQUARE_CHUNK[$sc.start,text]
	;

square_chunk_data[boolean isRecursive]
	:	ls1=LEFT_SQUARE
		{	if (!isRecursive) {
				helper.emit($ls1, DroolsEditorType.SYMBOL);
			} else {
				helper.emit($ls1, DroolsEditorType.CODE_CHUNK);
			}	
		}
			(any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) { helper.emit($any, DroolsEditorType.CODE_CHUNK); }| square_chunk_data[true] )* 
		rs1=RIGHT_SQUARE
		{	if (!isRecursive) {
				helper.emit($rs1, DroolsEditorType.SYMBOL);
			} else {
				helper.emit($rs1, DroolsEditorType.CODE_CHUNK);
			}	
		}
	;

// --------------------------------------------------------
//                      GENERAL RULES
// --------------------------------------------------------
literal
	:	STRING                	{	helper.emit($STRING, DroolsEditorType.STRING_CONST);	}
	|	DECIMAL 		{	helper.emit($DECIMAL, DroolsEditorType.NUMERIC_CONST);	}
	|	HEX     		{	helper.emit($HEX, DroolsEditorType.NUMERIC_CONST);	}
	|	FLOAT   		{	helper.emit($FLOAT, DroolsEditorType.NUMERIC_CONST);	}
	|	BOOL                  	{	helper.emit($BOOL, DroolsEditorType.BOOLEAN_CONST);	}
	|	NULL                  	{	helper.emit($NULL, DroolsEditorType.NULL_CONST);	}
	;

typeList
	:	type (COMMA type)*
	;
	
typeNameList
	:	typeName (COMMA! typeName)*
	;	
	
	//  helper.validateLT(2, "-")
type
options { backtrack=true; memoize=true; }
	: 	(primitiveType) => ( primitiveType ((LEFT_SQUARE RIGHT_SQUARE)=> LEFT_SQUARE RIGHT_SQUARE)* )
	|	( ID ((typeArguments)=>typeArguments)? (DOT ID ((typeArguments)=>typeArguments)? )* ((LEFT_SQUARE RIGHT_SQUARE)=> LEFT_SQUARE RIGHT_SQUARE)* )
	;

typeName
	:   ID (DOT ID)* -> VT_TYPE_NAME[$typeName.text]
    	//|   packageOrTypeName DOT ID		//sotty: ??
	;
	

packageOrTypeName
	:	id+=ID ( id+=DOT id+=ID )*
	{	helper.emit($id, DroolsEditorType.IDENTIFIER);
		helper.setParaphrasesValue(DroolsParaphraseTypes.PACKAGE, helper.buildStringFromTokens($id));	}
		-> ^(VT_PACKAGE_ID ID+)
	;

typeArguments
	:	LESS typeArgument (COMMA typeArgument)* GREATER
	;
	
typeArgument
	:	type
	|	QUESTION ((extends_key | super_key) type)?
	;

parameters
	:	LEFT_PAREN {	helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}
			( param_definition (COMMA {	helper.emit($COMMA, DroolsEditorType.SYMBOL);	} param_definition)* )?
		RIGHT_PAREN {	helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
		-> ^(VT_PARAM_LIST param_definition* RIGHT_PAREN)
	;

param_definition
	:	data_type? argument
	;

argument
	:	ID {	helper.emit($ID, DroolsEditorType.IDENTIFIER);	}
		dimension_definition*
	;

string_list
@init {
    StringBuilder buf = new StringBuilder();
}
	:	first=STRING { buf.append( "[ "+ $first.text ); }
	   (COMMA next=STRING { buf.append( ", " + $next.text ); } )* 
	-> STRING[$first,buf.toString()+" ]"]
	;

// --------------------------------------------------------
//                      STATEMENTS
// --------------------------------------------------------
/*

block   
	:	LEFT_CURLY blockStatement* RIGHT_CURLY
	;
	
blockStatement
	:	(final_key)=> localVariableDeclaration
		//|	classOrInterfaceDeclaration						//sotty: do not allow named classes. inline classes are primary expressions
  	|	rhs_statement
	;



	
localVariableDeclaration
	:	
	( variableModifier )* type variableDeclarators SEMICOLON
	;
	
variableModifier
	:	final_key
//   |   annotation
	;

variableDeclaratorId
	:	ID (LEFT_SQUARE RIGHT_SQUARE)*
	;

variableDeclarators
	:	variableDeclarator (COMMA variableDeclarator)*
	;

variableDeclarator
	:	id=ID rest=variableDeclaratorRest 
	;
	
variableDeclaratorRest
	:	(LEFT_SQUARE RIGHT_SQUARE)+ (EQUALS_ASSIGN variableInitializer)?
	|	EQUALS_ASSIGN variableInitializer
	|
	;	
	
	
	
		
	
	
rhs_statement 
options{ backtrack=true; memoize=true; }
	: block    
    | if_key parExpression rhs_statement (options {k=1;}: else_key rhs_statement)?
    | for_key LEFT_PAREN forControl RIGHT_PAREN rhs_statement
    | while_key parExpression rhs_statement
    | do_key rhs_statement while_key parExpression SEMICOLON
    | try_key block
      (	catches finally_key block
      | catches
      | finally_key block
      )
    | switch_key parExpression LEFT_CURLY switchBlockStatementGroups* RIGHT_CURLY
    | synchronized_key parExpression block
    | return_key expression? SEMICOLON
    | throw_key expression SEMICOLON
    | break_key ID? SEMICOLON
    | continue_key ID? SEMICOLON
    | SEMICOLON
//    | statementExpression SEMICOLON				// just an expression
		| expression SEMICOLON				
    | ID COLON rhs_statement
    
    // adding support to drools modify block        
    | modifyStatement   
    
    | assert_key expression (COLON expression)? SEMICOLON    
	;


forControl 
options { backtrack=true; memoize=true; }
	:	forVarControl
	|	forInit? SEMICOLON expression? SEMICOLON forUpdate?
	;

forInit    
options { backtrack=true; memoize=true; }
	:	variableModifier* type variableDeclarators
	|	expressionList
	;

forVarControl
	:	variableModifier* type ID COLON expression
	;

forUpdate
	:	expressionList
	;



catches
	:	catchClause (catchClause)*
	;
	
catchClause
	:	catch_key LEFT_PAREN formalParameter RIGHT_PAREN block
	;

formalParameter
	:	variableModifier* type variableDeclaratorId
	;



switchBlockStatementGroups
	:	switchLabel 
	| blockStatement
	;
	
switchLabel
//	:	'case' constantExpression ':'				//constantExpression is actually an expression
//  |   'case' enumConstantName ':'       //enumConstantName is actually an ID
	: case_key expression COLON
	| default_key COLON
	;
	
modifyStatement
	: s=modify_key parExpression 
	LEFT_CURLY ( e = expression (COMMA e=expression  )* )? RIGHT_CURLY
	;	
*/

// --------------------------------------------------------
//                      EXPRESSIONS
// --------------------------------------------------------
expression
options { backtrack=true; memoize=true; }
	:	conditionalExpression ((assignmentOperator) => assignmentOperator expression)?
	;

conditionalExpression
        :       conditionalOrExpression ( QUESTION expression COLON expression )?
	;
conditionalOrExpression
    :   conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )*
	;

conditionalAndExpression
    :   inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )*
	;

inclusiveOrExpression
    :   exclusiveOrExpression ( PIPE exclusiveOrExpression )*
	;

exclusiveOrExpression
    :   andExpression ( XOR andExpression )*
	;

andExpression
    :   equalityExpression ( AMPER equalityExpression )*
	;

equalityExpression
    :   instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )*
	;

instanceOfExpression
    :   relationalExpression (instanceof_key type)?
	;

relationalExpression
    :   shiftExpression ( (LESS)=> relationalOp shiftExpression )*
    ;
	
relationalOp
	:	(LESS_EQUALS| GREATER_EQUALS | LESS | GREATER)
	;

shiftExpression
    :   additiveExpression ( shiftOp additiveExpression )*
	;

shiftOp
	:	(SHIFT_LEFT | SHIFT_RIGHT_UNSIG | SHIFT_RIGHT )
	;

additiveExpression
    :   multiplicativeExpression ( (PLUS|MINUS)=> (PLUS | MINUS) multiplicativeExpression )*
	;

multiplicativeExpression
    :   unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
	;
	
unaryExpression
    :   PLUS unaryExpression
    |	MINUS unaryExpression
    |   INCR primary
    |   DECR primary
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
options{ backtrack=true; memoize=true; }
    :   TILDE unaryExpression
    | 	NEGATION unaryExpression
    |   castExpression
    |   primary ((selector)=>selector)* ((INCR|DECR)=> (INCR|DECR))?
    ;
    
castExpression
options { backtrack=true; memoize=true; }
    :  (LEFT_PAREN primitiveType) => LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression
    |  (LEFT_PAREN type) => LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
    |  LEFT_PAREN expression RIGHT_PAREN unaryExpressionNotPlusMinus
    ;
    
primitiveType
options { backtrack=true; memoize=true; }
    :   boolean_key
    |	char_key
    |	byte_key
    |	short_key
    |	int_key
    |	long_key
    |	float_key
    |	double_key
    ;

primary
//options{ backtrack=true; memoize=true; }
    :	(parExpression)=> parExpression
    |   (nonWildcardTypeArguments)=> nonWildcardTypeArguments (explicitGenericInvocationSuffix | this_key arguments)
    |   (literal)=> literal
    //|   this_key ({!helper.validateSpecialID(2)}?=> DOT ID)* ({helper.validateIdentifierSufix()}?=> identifierSuffix)?
    |   (super_key)=> super_key superSuffix
    |   (new_key)=> new_key creator
    |   (primitiveType)=> primitiveType (LEFT_SQUARE RIGHT_SQUARE)* DOT class_key
    //|   void_key DOT class_key
    |   (inlineMapExpression)=> inlineMapExpression
    |   (inlineListExpression)=> inlineListExpression
    |   (ID)=>ID ((DOT ID)=>DOT ID)* ((identifierSuffix)=>identifierSuffix)?
    ;

inlineListExpression
    :   LEFT_SQUARE expressionList? RIGHT_SQUARE	
    ;
    
inlineMapExpression
    :	LEFT_SQUARE mapExpressionList+ RIGHT_SQUARE
    ;

mapExpressionList
    :	mapEntry (COMMA mapEntry)*
    ;
    
mapEntry
    :	expression COLON expression
    ;

parExpression
	:	LEFT_PAREN expression RIGHT_PAREN
	;
	
identifierSuffix
options { backtrack=true; memoize=true; }
    :	(LEFT_SQUARE RIGHT_SQUARE)+ DOT class_key
    |	((LEFT_SQUARE) => LEFT_SQUARE expression RIGHT_SQUARE)+ // can also be matched by selector, but do here
    |   arguments 
//    |   DOT class_key
//    |   DOT explicitGenericInvocation
//    |   DOT this_key
//    |   DOT super_key arguments
//    |   DOT new_key (nonWildcardTypeArguments)? innerCreator
	;
	
creator
	:	nonWildcardTypeArguments? createdName
        (arrayCreatorRest | classCreatorRest)
	;

createdName
	:	ID typeArguments?
        ( DOT ID typeArguments?)*
        |	primitiveType
	;
	
innerCreator
	:	{!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))}?=> ID classCreatorRest
	;

arrayCreatorRest
	:   LEFT_SQUARE 
	(   RIGHT_SQUARE (LEFT_SQUARE RIGHT_SQUARE)* arrayInitializer
        |   expression RIGHT_SQUARE ({!helper.validateLT(2,"]")}?=>LEFT_SQUARE expression RIGHT_SQUARE)* ((LEFT_SQUARE RIGHT_SQUARE)=> LEFT_SQUARE RIGHT_SQUARE)*
        )
	;

variableInitializer
	:	arrayInitializer
    	|   expression
	;
	
arrayInitializer
	:	LEFT_CURLY (variableInitializer (COMMA variableInitializer)* (COMMA)? )? RIGHT_CURLY
	;

classCreatorRest
	:	arguments //classBody?		//sotty:  restored classBody to allow for inline, anonymous classes
	;
	
explicitGenericInvocation
	:	nonWildcardTypeArguments arguments
	;
	
nonWildcardTypeArguments
	:	LESS typeList GREATER
	;
	
explicitGenericInvocationSuffix
	:	super_key superSuffix
	|   	ID arguments
	;

selector
options { backtrack=true; memoize=true; }
	:   DOT ID ((LEFT_PAREN) => arguments)?
	//|   DOT this_key
	|   DOT super_key superSuffix
	|   DOT new_key (nonWildcardTypeArguments)? innerCreator
	|   LEFT_SQUARE expression RIGHT_SQUARE
	;
	
superSuffix
	:	arguments
	|   	DOT ID ((LEFT_PAREN) => arguments)?
        ;

arguments
options { backtrack=true; memoize=true; }
	:	LEFT_PAREN expressionList? RIGHT_PAREN
	;

expressionList
    :   expression (COMMA! expression)*
    ;

assignmentOperator
options { k=1; }
	:   EQUALS_ASSIGN
        |   PLUS_ASSIGN
        |   MINUS_ASSIGN
        |   MULT_ASSIGN
        |   DIV_ASSIGN
        |   AND_ASSIGN
        |   OR_ASSIGN
        |   XOR_ASSIGN
        |   MOD_ASSIGN
        |   SHIFT_LEFT EQUALS_ASSIGN
        |   SHIFT_RIGHT EQUALS_ASSIGN
        |   SHIFT_RIGHT_UNSIG EQUALS_ASSIGN
	;

// --------------------------------------------------------
//                      (ANON) CLASS_BODY
// --------------------------------------------------------
/*	
classDeclaration
	:	normalClassDeclaration
    |   enumDeclaration
	;
	
normalClassDeclaration
	:	class_key ID (typeParameters)?
        (extends_key type)?
        (implements_key typeList)?
        classBody
	;

classBody
	:	LEFT_CURLY classBodyDeclaration* RIGHT_CURLY
	;

classBodyDeclaration			//sotty: only for anon classes
	:	SEMICOLON
	|	//static_key? 				//static is not allowed, but an action block is
		block		
	|	modifiedClassMember
	;


modifiedClassMember
options{ backtrack=true; }
	: modifier modifiedClassMember
	| memberDecl
	;	


modifier
    :   annotation
    |   public_key
    |   protected_key
    |   private_key
    |   static_key
    |   abstract_key
    |   final_key
    |   native_key
    |   synchronized_key
    |   transient_key
    |   volatile_key
    |   strictfp_key
    ;

memberDecl
options{ backtrack=true; }
	:	genericMethodOrConstructorDecl			//sotty: should we?
	
	|	methodDeclaration
	|	void_key ID voidMethodDeclaratorRest
	|	ID constructorDeclaratorRest
	
	|	fieldDeclaration
		
	|	interfaceDeclaration
	|	classDeclaration
	;
	
	
genericMethodOrConstructorDecl
	:	typeParameters genericMethodOrConstructorRest
	;
	
genericMethodOrConstructorRest
	:	(type | void_key) ID methodDeclaratorRest
	|	ID constructorDeclaratorRest
	;	
	
	
methodDeclaration
	:	type ID methodDeclaratorRest
	;
	
methodDeclaratorRest
	:	formalParameters (LEFT_SQUARE RIGHT_SQUARE)*
        (throws_key typeNameList)?
        (   block 
        |   SEMICOLON
        )
	;	

voidMethodDeclaratorRest
	:	formalParameters (throws_key typeNameList)?
        (   block
        |   SEMICOLON
        )
	;

constructorDeclaratorRest
	:	formalParameters (throws_key typeNameList)? block
	;


fieldDeclaration
	:	type variableDeclarators SEMICOLON
	;
	
formalParameters
	:	LEFT_PAREN formalParameterDecls? RIGHT_PAREN
	;
	
formalParameterDecls
	:	variableModifier* type formalParameterDeclsRest?
	;
	
formalParameterDeclsRest
	:	variableDeclaratorId (COMMA formalParameterDecls)?
	|   DOT DOT DOT variableDeclaratorId
	;

typeParameters
	:	LESS typeParameter (COMMA typeParameter)* GREATER
	;

typeParameter
	:	ID (extends_key bound)?
	;
	
bound
	:	type (AMPER type)*
	;
	
interfaceDeclaration
	:	normalInterfaceDeclaration
		| annotationTypeDeclaration		//sotty: not sure, would it allow to declare new metadata?
	;
	
normalInterfaceDeclaration
	:	interface_key ID typeParameters? (extends_key typeList)? interfaceBody
	;

interfaceBody
	:	LEFT_CURLY interfaceBodyDeclaration* RIGHT_CURLY
	;


interfaceBodyDeclaration
	:	modifiedInterfaceMember
	|   SEMICOLON
	;
	
modifiedInterfaceMember
options{ backtrack=true; }
	: modifier modifiedInterfaceMember
	| interfaceMemberDecl
	;	

interfaceMemberDecl
	:	interfaceMethodOrFieldDecl
	|   interfaceGenericMethodDecl
    |   void_key ID voidInterfaceMethodDeclaratorRest
    |   interfaceDeclaration
    |   classDeclaration
	;
	
interfaceMethodOrFieldDecl
	:	type ID interfaceMethodOrFieldRest
	;
	
interfaceMethodOrFieldRest
	:	constantDeclaratorsRest SEMICOLON
	|	interfaceMethodDeclaratorRest
	;


interfaceMethodDeclaratorRest
	:	formalParameters (LEFT_SQUARE RIGHT_SQUARE)* (throws_key typeNameList)? SEMICOLON
	;
	
interfaceGenericMethodDecl
	:	typeParameters (type | void_key) ID
        interfaceMethodDeclaratorRest
	;
	
voidInterfaceMethodDeclaratorRest
	:	formalParameters (throws_key typeNameList)? SEMICOLON
	;

constantDeclarator
	:	ID constantDeclaratorRest
	;

constantDeclaratorsRest
    :   constantDeclaratorRest (COMMA constantDeclarator)*
    ;

constantDeclaratorRest
	:	(LEFT_SQUARE RIGHT_SQUARE)* EQUALS variableInitializer
	;

enumDeclaration
	:	enum_key ID (implements_key typeList)? enumBody
	;
	
enumBody
	:	LEFT_CURLY enumConstants? COMMA? enumBodyDeclarations? RIGHT_CURLY
	;

enumConstants
	:	enumConstant (COMMA enumConstant)*
	;
	
enumConstant
	:	annotations? ID (arguments)? (classBody)?
	;
	
enumBodyDeclarations
	:	SEMICOLON (classBodyDeclaration)*
	;

// --------------------------------------------------------
//                      (JAVA) ANNOTATIONS
// --------------------------------------------------------

annotations
	:	annotation+
	;

annotation
	:	AT {	helper.emit($AT, DroolsEditorType.SYMBOL);	}
		ann=annotationName 
			(
				LEFT_PAREN RIGHT_PAREN
				| LEFT_PAREN elementValuePairs RIGHT_PAREN
				| 
			)
		-> ^(AT VT_TYPE_NAME[$ann.name] elementValuePairs?)
	;

	
annotationName returns [String name]
@init{ $name=""; }
	: id=ID 	{	$name += $id.text; helper.emit($id, DroolsEditorType.IDENTIFIER);	}
		(DOT mid=ID { $name += $mid.text; } )*
	;
	
elementValuePairs
	: elementValuePair (COMMA! elementValuePair)*
	;
	
elementValuePair
	: (ID EQUALS_ASSIGN)=> key=ID EQUALS_ASSIGN val=elementValue -> ^(VT_PROP_KEY[$key] VT_PROP_VALUE[$val.text])
	| value=elementValue -> ^(VT_PROP_KEY[$value.text])
	;
	
elementValue
	:	TimePeriod
	|	conditionalExpression
	|   annotation
	|   elementValueArrayInitializer
	;

elementValueArrayInitializer
	:	LEFT_CURLY (elementValue (COMMA elementValue )*)? RIGHT_CURLY
	;

	
annotationTypeDeclaration
	:	AT interface_key ID annotationTypeBody
	;
	
annotationTypeBody
	:	LEFT_CURLY (annotationTypeElementDeclarations)? RIGHT_CURLY
	;
	
annotationTypeElementDeclarations
	:	(annotationTypeElementDeclaration) (annotationTypeElementDeclaration)*
	;
	
annotationTypeElementDeclaration
	:	(modifier)* annotationTypeElementRest
	;
	
annotationTypeElementRest
	:	type annotationMethodOrConstantRest SEMICOLON
	|   classDeclaration SEMICOLON?
	|   interfaceDeclaration SEMICOLON?
//	|   enumDeclaration SEMICOLON?								//included in classDecl
//	|   annotationTypeDeclaration SEMICOLON?			//included in interfDecl
	;
	
annotationMethodOrConstantRest
	:	annotationMethodRest
	|   annotationConstantRest
	;
	
annotationMethodRest
 	:	ID LEFT_PAREN RIGHT_PAREN (defaultValue)?
 	;
 	
annotationConstantRest
 	:	variableDeclarators
 	;
 	
defaultValue
 	:	default_key elementValue
 	;
*/

// --------------------------------------------------------
//                      KEYWORDS
// --------------------------------------------------------
operator_key
	:      {(helper.isPluggableEvaluator(false))}?=> id=ID
	       { helper.emit($id, DroolsEditorType.IDENTIFIER); }
	       -> VK_OPERATOR[$id]
	;

neg_operator_key
	:      {(helper.isPluggableEvaluator(true))}?=> id=ID 
	       { helper.emit($id, DroolsEditorType.IDENTIFIER); } 
	       -> VK_OPERATOR[$id]
	;

lock_on_active_key
@init{
	String text = "";
}	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.LOCK) && helper.validateLT(2, "-") && helper.validateLT(3, DroolsSoftKeywords.ON) && helper.validateLT(4, "-") && helper.validateLT(5, DroolsSoftKeywords.ACTIVE))}?=>  id1=ID mis1=MINUS id2=ID mis2=MINUS id3=ID {text = $text;}
	{	helper.emit($id1, DroolsEditorType.KEYWORD);
		helper.emit($mis1, DroolsEditorType.KEYWORD);
		helper.emit($id2, DroolsEditorType.KEYWORD);
		helper.emit($mis2, DroolsEditorType.KEYWORD);
		helper.emit($id3, DroolsEditorType.KEYWORD);	}
		->	VK_LOCK_ON_ACTIVE[$start, text]
	;

date_effective_key
@init{
	String text = "";
}	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.DATE) && helper.validateLT(2, "-") && helper.validateLT(3, DroolsSoftKeywords.EFFECTIVE))}?=>  id1=ID mis1=MINUS id2=ID {text = $text;}
	{	helper.emit($id1, DroolsEditorType.KEYWORD);
		helper.emit($mis1, DroolsEditorType.KEYWORD);
		helper.emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_DATE_EFFECTIVE[$start, text]
	;

date_expires_key
@init{
	String text = "";
}	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.DATE) && helper.validateLT(2, "-") && helper.validateLT(3, DroolsSoftKeywords.EXPIRES))}?=>  id1=ID mis1=MINUS id2=ID {text = $text;}
	{	helper.emit($id1, DroolsEditorType.KEYWORD);
		helper.emit($mis1, DroolsEditorType.KEYWORD);
		helper.emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_DATE_EXPIRES[$start, text]
	;

no_loop_key
@init{
	String text = "";
}	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.NO) && helper.validateLT(2, "-") && helper.validateLT(3, DroolsSoftKeywords.LOOP))}?=>  id1=ID mis1=MINUS id2=ID {text = $text;}
	{	helper.emit($id1, DroolsEditorType.KEYWORD);
		helper.emit($mis1, DroolsEditorType.KEYWORD);
		helper.emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_NO_LOOP[$start, text]
	;

auto_focus_key
@init{
	String text = "";
}	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.AUTO) && helper.validateLT(2, "-") && helper.validateLT(3, DroolsSoftKeywords.FOCUS))}?=>  id1=ID mis1=MINUS id2=ID {text = $text;}
	{	helper.emit($id1, DroolsEditorType.KEYWORD);
		helper.emit($mis1, DroolsEditorType.KEYWORD);
		helper.emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_AUTO_FOCUS[$start, text]
	;

activation_group_key
@init{
	String text = "";
}	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && helper.validateLT(2, "-") && helper.validateLT(3, DroolsSoftKeywords.GROUP))}?=>  id1=ID mis1=MINUS id2=ID {text = $text;}
	{	helper.emit($id1, DroolsEditorType.KEYWORD);
		helper.emit($mis1, DroolsEditorType.KEYWORD);
		helper.emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_ACTIVATION_GROUP[$start, text]
	;

agenda_group_key
@init{
	String text = "";
}	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.AGENDA) && helper.validateLT(2, "-") && helper.validateLT(3, DroolsSoftKeywords.GROUP))}?=>  id1=ID mis1=MINUS id2=ID {text = $text;}
	{	helper.emit($id1, DroolsEditorType.KEYWORD);
		helper.emit($mis1, DroolsEditorType.KEYWORD);
		helper.emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_AGENDA_GROUP[$start, text]
	;

ruleflow_group_key
@init{
	String text = "";
}	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && helper.validateLT(2, "-") && helper.validateLT(3, DroolsSoftKeywords.GROUP))}?=>  id1=ID mis1=MINUS id2=ID {text = $text;}
	{	helper.emit($id1, DroolsEditorType.KEYWORD);
		helper.emit($mis1, DroolsEditorType.KEYWORD);
		helper.emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_RULEFLOW_GROUP[$start, text]
	;

entry_point_key
@init{
	String text = "";
}	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.ENTRY) && helper.validateLT(2, "-") && helper.validateLT(3, DroolsSoftKeywords.POINT))}?=>  id1=ID mis1=MINUS id2=ID {text = $text;}
	{	helper.emit($id1, DroolsEditorType.KEYWORD);
		helper.emit($mis1, DroolsEditorType.KEYWORD);
		helper.emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_ENTRY_POINT[$start, text]
	;

timer_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.TIMER))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_TIMER[$id]
	;

duration_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.DURATION))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_TIMER[$id]
	;

calendars_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.CALENDARS))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_CALENDARS[$id]
	;

package_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.PACKAGE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_PACKAGE[$id]
	;

import_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.IMPORT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_IMPORT[$id]
	;

dialect_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.DIALECT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_DIALECT[$id]
	;

salience_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.SALIENCE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_SALIENCE[$id]
	;

enabled_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.ENABLED))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_ENABLED[$id]
	;

attributes_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_ATTRIBUTES[$id]
	;

rule_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.RULE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_RULE[$id]
	;

extend_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.EXTEND))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_EXTEND[$id]
	;

query_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.QUERY))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_QUERY[$id]
	;

declare_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.DECLARE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_DECLARE[$id]
	;

function_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.FUNCTION))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_FUNCTION[$id]
	;

global_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.GLOBAL))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_GLOBAL[$id]
	;

eval_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.EVAL))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_EVAL[$id]
	;

not_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_NOT[$id]
	;

in_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.IN))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_IN[$id]
	;

or_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.OR))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_OR[$id]
	;

and_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.AND))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_AND[$id]
	;

exists_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.EXISTS))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_EXISTS[$id]
	;

forall_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.FORALL))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_FORALL[$id]
	;

action_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.ACTION))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_ACTION[$id]
	;

reverse_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.REVERSE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_REVERSE[$id]
	;

result_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.RESULT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_RESULT[$id]
	;

end_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.END))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_END[$id]
	;

not_end_key
	:	{!(helper.validateIdentifierKey(DroolsSoftKeywords.END))}?=>  any=.
	{	helper.emit($any, DroolsEditorType.CODE_CHUNK);	}
	;

init_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.INIT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_INIT[$id]
	;

instanceof_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_INSTANCEOF[$id]
	;

extends_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_EXTENDS[$id]
	;

implements_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.IMPLEMENTS))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_IMPLEMENTS[$id]
	;

super_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_SUPER[$id] 
	;

boolean_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_PRIMITIVE_TYPE[$id] 
	;

char_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_PRIMITIVE_TYPE[$id] 
	;

byte_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_PRIMITIVE_TYPE[$id] 
	;

short_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_PRIMITIVE_TYPE[$id] 
	;

int_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.INT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_PRIMITIVE_TYPE[$id] 
	;

long_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_PRIMITIVE_TYPE[$id] 
	;

float_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_PRIMITIVE_TYPE[$id] 
	;

double_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_PRIMITIVE_TYPE[$id] 
	;

this_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_THIS[$id] 
	;

void_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_VOID[$id] 
	;

class_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_CLASS[$id] 
	;

new_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_NEW[$id] 
	;


final_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.FINAL))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_FINAL[$id]
;


if_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.IF))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_IF[$id]
;


else_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.ELSE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_ELSE[$id]
;


for_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.FOR))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_FOR[$id]
;


while_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.WHILE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_WHILE[$id]
;


do_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.DO))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_DO[$id]
;


case_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.CASE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_CASE[$id]
;


default_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.DEFAULT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_DEFAULT[$id]
;


try_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.TRY))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_TRY[$id]
;


catch_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.CATCH))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_CATCH[$id]
;


finally_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.FINALLY))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_FINALLY[$id]
;


switch_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.SWITCH))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_SWITCH[$id]
;


synchronized_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.SYNCHRONIZED))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_SYNCHRONIZED[$id]
;


return_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.RETURN))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_RETURN[$id]
;


throw_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.THROW))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_THROW[$id]
;


break_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.BREAK))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_BREAK[$id]
;


continue_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.CONTINUE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_CONTINUE[$id]
;

assert_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.ASSERT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_ASSERT[$id]
;

static_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.STATIC))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_STATIC[$id]
;

modify_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.MODIFY))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_MODIFY[$id]
;
	
public_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.PUBLIC))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_PUBLIC[$id]
;


protected_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.PROTECTED))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_PROTECTED[$id]
;


private_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.PRIVATE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_PRIVATE[$id]
;


abstract_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.ABSTRACT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_ABSTRACT[$id]
;


native_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.NATIVE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_NATIVE[$id]
;


transient_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.TRANSIENT))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_TRANSIENT[$id]
;


volatile_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.VOLATILE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_VOLATILE[$id]
;


strictfp_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.STRICTFP))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_STRICTFP[$id]
;


throws_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.THROWS))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_THROWS[$id]
;


interface_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.INTERFACE))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_INTERFACE[$id]
;


enum_key
	:	{(helper.validateIdentifierKey(DroolsSoftKeywords.ENUM))}?=>  id=ID
	{	helper.emit($id, DroolsEditorType.KEYWORD);	}
	-> VK_ENUM[$id]
;
	



// --------------------------------------------------------
//                      LEXER
// --------------------------------------------------------
WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )+
                { $channel=HIDDEN; }
        ;

fragment
EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;
        
FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ FloatTypeSuffix
	;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;

HEX 	: '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;

DECIMAL	: ('0'..'9')+ IntegerTypeSuffix? ;

fragment
IntegerTypeSuffix : ('l'|'L') ;

STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;


TimePeriod
	: (('0'..'9')+ 'd') (('0'..'9')+ 'h')?(('0'..'9')+ 'm')?(('0'..'9')+ 's')?(('0'..'9')+ 'ms'?)?
	| (('0'..'9')+ 'h') (('0'..'9')+ 'm')?(('0'..'9')+ 's')?(('0'..'9')+ 'ms'?)?
	| (('0'..'9')+ 'm') (('0'..'9')+ 's')?(('0'..'9')+ 'ms'?)?
	| (('0'..'9')+ 's') (('0'..'9')+ 'ms'?)?
	| (('0'..'9')+ 'ms'?)
	;




fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
EscapeSequence
    :   '\\' ('b'|'B'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'|'.'|'o'|
              'x'|'a'|'e'|'c'|'d'|'D'|'s'|'S'|'w'|'W'|'p'|'A'|
              'G'|'Z'|'z'|'Q'|'E'|'*'|'['|']'|'('|')'|'$'|'^'|
              '{'|'}'|'?'|'+'|'-'|'&'|'|')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

BOOL
	:	('true'|'false') 
	;	

ACCUMULATE
	:	'accumulate'
	;

COLLECT
	:	'collect'
	;

FROM
	:	'from'
	;

NULL	
	:	'null'
	;

OVER
	:	'over'
	;

THEN
	:	'then'
	;

WHEN
	:	'when'
	;

AT	:	'@'
	;

SHIFT_RIGHT
	:	'>>'
	;

SHIFT_LEFT
	:	'<<'
	;

SHIFT_RIGHT_UNSIG
	:	'>>>'
	;

PLUS_ASSIGN
	:	'+='
	;

MINUS_ASSIGN
	:	'-='
	;

MULT_ASSIGN
	:	'*='
	;

DIV_ASSIGN
	:	'/='
	;

AND_ASSIGN
	:	'&='
	;

OR_ASSIGN
	:	'|='
	;

XOR_ASSIGN
	:	'^='
	;

MOD_ASSIGN
	:	'%='
	;

DECR	:	'--' 
	;
	
INCR	:	'++'
	;
	
ARROW
	:	'->'
	;

SEMICOLON
	:	';'
	;

COLON
	:	':'
	;

EQUALS
	:	'=='
	;

NOT_EQUALS
	:	'!='
	;

GREATER_EQUALS
	:	'>='
	;

LESS_EQUALS
	:	'<='
	;

GREATER
	:	'>'
	;

LESS
	:	'<'
	;

EQUALS_ASSIGN
	:	'='
	;

LEFT_PAREN
        :	'('
        ;

RIGHT_PAREN
        :	')'
        ;
        
LEFT_SQUARE
        :	'['
        ;

RIGHT_SQUARE
        :	']'
        ;        

LEFT_CURLY
        :	'{'
        ;

RIGHT_CURLY
        :	'}'
        ;
        
COMMA	:	','
	;
	
DOT	:	'.'
	;	
	
DOUBLE_AMPER
	:	'&&'
	;
	
DOUBLE_PIPE
	:	'||'
	;
	
QUESTION
	:	'?'
	;	
	
NEGATION
	:	'!'
	;	

TILDE
	:	'~'
	;
	
PIPE
	:	'|'
	;
	
AMPER	
	:	'&'	
	;
	
XOR
	:	'^'
	;	
	
MOD	
	:	'%'
	;
	
STAR	:	'*' 
	;
	
MINUS	:	'-' 
	;
	
PLUS	:	'+'
	;
	
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' (~('\r'|'\n'))* EOL?
                { $channel=HIDDEN; setText("//"+getText().substring(1));}
	;
        
        
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' (~('\r'|'\n'))* EOL?
                { $channel=HIDDEN; }
    ;

MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

ID	
	:	IdentifierStart IdentifierPart*
	|	'`' IdentifierStart IdentifierPart* '`'
	{	state.text = $text.substring(1, $text.length() - 1);	}
	;

// must come after the commentaries that use 2-character sequences with /	
DIV	:	'/'	
	;

MISC 	:
		'!' | '\'' | '\\' | '$'
	;

fragment
IdentifierStart
    :   '\u0024'
    |   '\u0041'..'\u005a'
    |   '\u005f'
    |   '\u0061'..'\u007a'
    |   '\u00a2'..'\u00a5'
    |   '\u00aa'
    |   '\u00b5'
    |   '\u00ba'
    |   '\u00c0'..'\u00d6'
    |   '\u00d8'..'\u00f6'
    |   '\u00f8'..'\u0236'
    |   '\u0250'..'\u02c1'
    |   '\u02c6'..'\u02d1'
    |   '\u02e0'..'\u02e4'
    |   '\u02ee'
    |   '\u037a'
    |   '\u0386'
    |   '\u0388'..'\u038a'
    |   '\u038c'
    |   '\u038e'..'\u03a1'
    |   '\u03a3'..'\u03ce'
    |   '\u03d0'..'\u03f5'
    |   '\u03f7'..'\u03fb'
    |   '\u0400'..'\u0481'
    |   '\u048a'..'\u04ce'
    |   '\u04d0'..'\u04f5'
    |   '\u04f8'..'\u04f9'
    |   '\u0500'..'\u050f'
    |   '\u0531'..'\u0556'
    |   '\u0559'
    |   '\u0561'..'\u0587'
    |   '\u05d0'..'\u05ea'
    |   '\u05f0'..'\u05f2'
    |   '\u0621'..'\u063a'
    |   '\u0640'..'\u064a'
    |   '\u066e'..'\u066f'
    |   '\u0671'..'\u06d3'
    |   '\u06d5'
    |   '\u06e5'..'\u06e6'
    |   '\u06ee'..'\u06ef'
    |   '\u06fa'..'\u06fc'
    |   '\u06ff'
    |   '\u0710'
    |   '\u0712'..'\u072f'
    |   '\u074d'..'\u074f'
    |   '\u0780'..'\u07a5'
    |   '\u07b1'
    |   '\u0904'..'\u0939'
    |   '\u093d'
    |   '\u0950'
    |   '\u0958'..'\u0961'
    |   '\u0985'..'\u098c'
    |   '\u098f'..'\u0990'
    |   '\u0993'..'\u09a8'
    |   '\u09aa'..'\u09b0'
    |   '\u09b2'
    |   '\u09b6'..'\u09b9'
    |   '\u09bd'
    |   '\u09dc'..'\u09dd'
    |   '\u09df'..'\u09e1'
    |   '\u09f0'..'\u09f3'
    |   '\u0a05'..'\u0a0a'
    |   '\u0a0f'..'\u0a10'
    |   '\u0a13'..'\u0a28'
    |   '\u0a2a'..'\u0a30'
    |   '\u0a32'..'\u0a33'
    |   '\u0a35'..'\u0a36'
    |   '\u0a38'..'\u0a39'
    |   '\u0a59'..'\u0a5c'
    |   '\u0a5e'
    |   '\u0a72'..'\u0a74'
    |   '\u0a85'..'\u0a8d'
    |   '\u0a8f'..'\u0a91'
    |   '\u0a93'..'\u0aa8'
    |   '\u0aaa'..'\u0ab0'
    |   '\u0ab2'..'\u0ab3'
    |   '\u0ab5'..'\u0ab9'
    |   '\u0abd'
    |   '\u0ad0'
    |   '\u0ae0'..'\u0ae1'
    |   '\u0af1'
    |   '\u0b05'..'\u0b0c'
    |   '\u0b0f'..'\u0b10'
    |   '\u0b13'..'\u0b28'
    |   '\u0b2a'..'\u0b30'
    |   '\u0b32'..'\u0b33'
    |   '\u0b35'..'\u0b39'
    |   '\u0b3d'
    |   '\u0b5c'..'\u0b5d'
    |   '\u0b5f'..'\u0b61'
    |   '\u0b71'
    |   '\u0b83'
    |   '\u0b85'..'\u0b8a'
    |   '\u0b8e'..'\u0b90'
    |   '\u0b92'..'\u0b95'
    |   '\u0b99'..'\u0b9a'
    |   '\u0b9c'
    |   '\u0b9e'..'\u0b9f'
    |   '\u0ba3'..'\u0ba4'
    |   '\u0ba8'..'\u0baa'
    |   '\u0bae'..'\u0bb5'
    |   '\u0bb7'..'\u0bb9'
    |   '\u0bf9'
    |   '\u0c05'..'\u0c0c'
    |   '\u0c0e'..'\u0c10'
    |   '\u0c12'..'\u0c28'
    |   '\u0c2a'..'\u0c33'
    |   '\u0c35'..'\u0c39'
    |   '\u0c60'..'\u0c61'
    |   '\u0c85'..'\u0c8c'
    |   '\u0c8e'..'\u0c90'
    |   '\u0c92'..'\u0ca8'
    |   '\u0caa'..'\u0cb3'
    |   '\u0cb5'..'\u0cb9'
    |   '\u0cbd'
    |   '\u0cde'
    |   '\u0ce0'..'\u0ce1'
    |   '\u0d05'..'\u0d0c'
    |   '\u0d0e'..'\u0d10'
    |   '\u0d12'..'\u0d28'
    |   '\u0d2a'..'\u0d39'
    |   '\u0d60'..'\u0d61'
    |   '\u0d85'..'\u0d96'
    |   '\u0d9a'..'\u0db1'
    |   '\u0db3'..'\u0dbb'
    |   '\u0dbd'
    |   '\u0dc0'..'\u0dc6'
    |   '\u0e01'..'\u0e30'
    |   '\u0e32'..'\u0e33'
    |   '\u0e3f'..'\u0e46'
    |   '\u0e81'..'\u0e82'
    |   '\u0e84'
    |   '\u0e87'..'\u0e88'
    |   '\u0e8a'
    |   '\u0e8d'
    |   '\u0e94'..'\u0e97'
    |   '\u0e99'..'\u0e9f'
    |   '\u0ea1'..'\u0ea3'
    |   '\u0ea5'
    |   '\u0ea7'
    |   '\u0eaa'..'\u0eab'
    |   '\u0ead'..'\u0eb0'
    |   '\u0eb2'..'\u0eb3'
    |   '\u0ebd'
    |   '\u0ec0'..'\u0ec4'
    |   '\u0ec6'
    |   '\u0edc'..'\u0edd'
    |   '\u0f00'
    |   '\u0f40'..'\u0f47'
    |   '\u0f49'..'\u0f6a'
    |   '\u0f88'..'\u0f8b'
    |   '\u1000'..'\u1021'
    |   '\u1023'..'\u1027'
    |   '\u1029'..'\u102a'
    |   '\u1050'..'\u1055'
    |   '\u10a0'..'\u10c5'
    |   '\u10d0'..'\u10f8'
    |   '\u1100'..'\u1159'
    |   '\u115f'..'\u11a2'
    |   '\u11a8'..'\u11f9'
    |   '\u1200'..'\u1206'
    |   '\u1208'..'\u1246'
    |   '\u1248'
    |   '\u124a'..'\u124d'
    |   '\u1250'..'\u1256'
    |   '\u1258'
    |   '\u125a'..'\u125d'
    |   '\u1260'..'\u1286'
    |   '\u1288'
    |   '\u128a'..'\u128d'
    |   '\u1290'..'\u12ae'
    |   '\u12b0'
    |   '\u12b2'..'\u12b5'
    |   '\u12b8'..'\u12be'
    |   '\u12c0'
    |   '\u12c2'..'\u12c5'
    |   '\u12c8'..'\u12ce'
    |   '\u12d0'..'\u12d6'
    |   '\u12d8'..'\u12ee'
    |   '\u12f0'..'\u130e'
    |   '\u1310'
    |   '\u1312'..'\u1315'
    |   '\u1318'..'\u131e'
    |   '\u1320'..'\u1346'
    |   '\u1348'..'\u135a'
    |   '\u13a0'..'\u13f4'
    |   '\u1401'..'\u166c'
    |   '\u166f'..'\u1676'
    |   '\u1681'..'\u169a'
    |   '\u16a0'..'\u16ea'
    |   '\u16ee'..'\u16f0'
    |   '\u1700'..'\u170c'
    |   '\u170e'..'\u1711'
    |   '\u1720'..'\u1731'
    |   '\u1740'..'\u1751'
    |   '\u1760'..'\u176c'
    |   '\u176e'..'\u1770'
    |   '\u1780'..'\u17b3'
    |   '\u17d7' 
    |   '\u17db'..'\u17dc'
    |   '\u1820'..'\u1877'
    |   '\u1880'..'\u18a8'
    |   '\u1900'..'\u191c'
    |   '\u1950'..'\u196d'
    |   '\u1970'..'\u1974'
    |   '\u1d00'..'\u1d6b'
    |   '\u1e00'..'\u1e9b'
    |   '\u1ea0'..'\u1ef9'
    |   '\u1f00'..'\u1f15'
    |   '\u1f18'..'\u1f1d'
    |   '\u1f20'..'\u1f45'
    |   '\u1f48'..'\u1f4d'
    |   '\u1f50'..'\u1f57'
    |   '\u1f59'
    |   '\u1f5b'
    |   '\u1f5d'
    |   '\u1f5f'..'\u1f7d'
    |   '\u1f80'..'\u1fb4'
    |   '\u1fb6'..'\u1fbc'
    |   '\u1fbe'
    |   '\u1fc2'..'\u1fc4'
    |   '\u1fc6'..'\u1fcc'
    |   '\u1fd0'..'\u1fd3'
    |   '\u1fd6'..'\u1fdb'
    |   '\u1fe0'..'\u1fec'
    |   '\u1ff2'..'\u1ff4'
    |   '\u1ff6'..'\u1ffc'
    |   '\u203f'..'\u2040'
    |   '\u2054'
    |   '\u2071'
    |   '\u207f'
    |   '\u20a0'..'\u20b1'
    |   '\u2102'
    |   '\u2107'
    |   '\u210a'..'\u2113'
    |   '\u2115'
    |   '\u2119'..'\u211d'
    |   '\u2124'
    |   '\u2126'
    |   '\u2128'
    |   '\u212a'..'\u212d'
    |   '\u212f'..'\u2131'
    |   '\u2133'..'\u2139'
    |   '\u213d'..'\u213f'
    |   '\u2145'..'\u2149'
    |   '\u2160'..'\u2183'
    |   '\u3005'..'\u3007'
    |   '\u3021'..'\u3029'
    |   '\u3031'..'\u3035'
    |   '\u3038'..'\u303c'
    |   '\u3041'..'\u3096'
    |   '\u309d'..'\u309f'
    |   '\u30a1'..'\u30ff'
    |   '\u3105'..'\u312c'
    |   '\u3131'..'\u318e'
    |   '\u31a0'..'\u31b7'
    |   '\u31f0'..'\u31ff'
    |   '\u3400'..'\u4db5'
    |   '\u4e00'..'\u9fa5'
    |   '\ua000'..'\ua48c'
    |   '\uac00'..'\ud7a3'
    |   '\uf900'..'\ufa2d'
    |   '\ufa30'..'\ufa6a'
    |   '\ufb00'..'\ufb06'
    |   '\ufb13'..'\ufb17'
    |   '\ufb1d'
    |   '\ufb1f'..'\ufb28'
    |   '\ufb2a'..'\ufb36'
    |   '\ufb38'..'\ufb3c'
    |   '\ufb3e'
    |   '\ufb40'..'\ufb41'
    |   '\ufb43'..'\ufb44'
    |   '\ufb46'..'\ufbb1'
    |   '\ufbd3'..'\ufd3d'
    |   '\ufd50'..'\ufd8f'
    |   '\ufd92'..'\ufdc7'
    |   '\ufdf0'..'\ufdfc'
    |   '\ufe33'..'\ufe34'
    |   '\ufe4d'..'\ufe4f'
    |   '\ufe69'
    |   '\ufe70'..'\ufe74'
    |   '\ufe76'..'\ufefc'
    |   '\uff04'
    |   '\uff21'..'\uff3a'
    |   '\uff3f'
    |   '\uff41'..'\uff5a'
    |   '\uff65'..'\uffbe'
    |   '\uffc2'..'\uffc7'
    |   '\uffca'..'\uffcf'
    |   '\uffd2'..'\uffd7'
    |   '\uffda'..'\uffdc'
    |   '\uffe0'..'\uffe1'
    |   '\uffe5'..'\uffe6'
// UTF-16:    |   ('\ud800'..'\udbff') ('\udc00'..'\udfff') 
    ;                
                       
fragment 
IdentifierPart
    :   '\u0000'..'\u0008'
    |   '\u000e'..'\u001b'
    |   '\u0024'
    |   '\u0030'..'\u0039'
    |   '\u0041'..'\u005a'
    |   '\u005f'
    |   '\u0061'..'\u007a'
    |   '\u007f'..'\u009f'
    |   '\u00a2'..'\u00a5'
    |   '\u00aa'
    |   '\u00ad'
    |   '\u00b5'
    |   '\u00ba'
    |   '\u00c0'..'\u00d6'
    |   '\u00d8'..'\u00f6'
    |   '\u00f8'..'\u0236'
    |   '\u0250'..'\u02c1'
    |   '\u02c6'..'\u02d1'
    |   '\u02e0'..'\u02e4'
    |   '\u02ee'
    |   '\u0300'..'\u0357'
    |   '\u035d'..'\u036f'
    |   '\u037a'
    |   '\u0386'
    |   '\u0388'..'\u038a'
    |   '\u038c'
    |   '\u038e'..'\u03a1'
    |   '\u03a3'..'\u03ce'
    |   '\u03d0'..'\u03f5'
    |   '\u03f7'..'\u03fb'
    |   '\u0400'..'\u0481'
    |   '\u0483'..'\u0486'
    |   '\u048a'..'\u04ce'
    |   '\u04d0'..'\u04f5'
    |   '\u04f8'..'\u04f9'
    |   '\u0500'..'\u050f'
    |   '\u0531'..'\u0556'
    |   '\u0559'
    |   '\u0561'..'\u0587'
    |   '\u0591'..'\u05a1'
    |   '\u05a3'..'\u05b9'
    |   '\u05bb'..'\u05bd'
    |   '\u05bf'
    |   '\u05c1'..'\u05c2'
    |   '\u05c4'
    |   '\u05d0'..'\u05ea'
    |   '\u05f0'..'\u05f2'
    |   '\u0600'..'\u0603'
    |   '\u0610'..'\u0615'
    |   '\u0621'..'\u063a'
    |   '\u0640'..'\u0658'
    |   '\u0660'..'\u0669'
    |   '\u066e'..'\u06d3'
    |   '\u06d5'..'\u06dd'
    |   '\u06df'..'\u06e8'
    |   '\u06ea'..'\u06fc'
    |   '\u06ff'
    |   '\u070f'..'\u074a'
    |   '\u074d'..'\u074f'
    |   '\u0780'..'\u07b1'
    |   '\u0901'..'\u0939'
    |   '\u093c'..'\u094d'
    |   '\u0950'..'\u0954'
    |   '\u0958'..'\u0963'
    |   '\u0966'..'\u096f'
    |   '\u0981'..'\u0983'
    |   '\u0985'..'\u098c'
    |   '\u098f'..'\u0990'
    |   '\u0993'..'\u09a8'
    |   '\u09aa'..'\u09b0'
    |   '\u09b2'
    |   '\u09b6'..'\u09b9'
    |   '\u09bc'..'\u09c4'
    |   '\u09c7'..'\u09c8'
    |   '\u09cb'..'\u09cd'
    |   '\u09d7'
    |   '\u09dc'..'\u09dd'
    |   '\u09df'..'\u09e3'
    |   '\u09e6'..'\u09f3'
    |   '\u0a01'..'\u0a03'
    |   '\u0a05'..'\u0a0a'
    |   '\u0a0f'..'\u0a10'
    |   '\u0a13'..'\u0a28'
    |   '\u0a2a'..'\u0a30'
    |   '\u0a32'..'\u0a33'
    |   '\u0a35'..'\u0a36'
    |   '\u0a38'..'\u0a39'
    |   '\u0a3c'
    |   '\u0a3e'..'\u0a42'
    |   '\u0a47'..'\u0a48'
    |   '\u0a4b'..'\u0a4d'
    |   '\u0a59'..'\u0a5c'
    |   '\u0a5e'
    |   '\u0a66'..'\u0a74'
    |   '\u0a81'..'\u0a83'
    |   '\u0a85'..'\u0a8d'
    |   '\u0a8f'..'\u0a91'
    |   '\u0a93'..'\u0aa8'
    |   '\u0aaa'..'\u0ab0'
    |   '\u0ab2'..'\u0ab3'
    |   '\u0ab5'..'\u0ab9'
    |   '\u0abc'..'\u0ac5'
    |   '\u0ac7'..'\u0ac9'
    |   '\u0acb'..'\u0acd'
    |   '\u0ad0'
    |   '\u0ae0'..'\u0ae3'
    |   '\u0ae6'..'\u0aef'
    |   '\u0af1'
    |   '\u0b01'..'\u0b03'
    |   '\u0b05'..'\u0b0c'        
    |   '\u0b0f'..'\u0b10'
    |   '\u0b13'..'\u0b28'
    |   '\u0b2a'..'\u0b30'
    |   '\u0b32'..'\u0b33'
    |   '\u0b35'..'\u0b39'
    |   '\u0b3c'..'\u0b43'
    |   '\u0b47'..'\u0b48'
    |   '\u0b4b'..'\u0b4d'
    |   '\u0b56'..'\u0b57'
    |   '\u0b5c'..'\u0b5d'
    |   '\u0b5f'..'\u0b61'
    |   '\u0b66'..'\u0b6f'
    |   '\u0b71'
    |   '\u0b82'..'\u0b83'
    |   '\u0b85'..'\u0b8a'
    |   '\u0b8e'..'\u0b90'
    |   '\u0b92'..'\u0b95'
    |   '\u0b99'..'\u0b9a'
    |   '\u0b9c'
    |   '\u0b9e'..'\u0b9f'
    |   '\u0ba3'..'\u0ba4'
    |   '\u0ba8'..'\u0baa'
    |   '\u0bae'..'\u0bb5'
    |   '\u0bb7'..'\u0bb9'
    |   '\u0bbe'..'\u0bc2'
    |   '\u0bc6'..'\u0bc8'
    |   '\u0bca'..'\u0bcd'
    |   '\u0bd7'
    |   '\u0be7'..'\u0bef'
    |   '\u0bf9'
    |   '\u0c01'..'\u0c03'
    |   '\u0c05'..'\u0c0c'
    |   '\u0c0e'..'\u0c10'
    |   '\u0c12'..'\u0c28'
    |   '\u0c2a'..'\u0c33'
    |   '\u0c35'..'\u0c39'
    |   '\u0c3e'..'\u0c44'
    |   '\u0c46'..'\u0c48'
    |   '\u0c4a'..'\u0c4d'
    |   '\u0c55'..'\u0c56'
    |   '\u0c60'..'\u0c61'
    |   '\u0c66'..'\u0c6f'        
    |   '\u0c82'..'\u0c83'
    |   '\u0c85'..'\u0c8c'
    |   '\u0c8e'..'\u0c90'
    |   '\u0c92'..'\u0ca8'
    |   '\u0caa'..'\u0cb3'
    |   '\u0cb5'..'\u0cb9'
    |   '\u0cbc'..'\u0cc4'
    |   '\u0cc6'..'\u0cc8'
    |   '\u0cca'..'\u0ccd'
    |   '\u0cd5'..'\u0cd6'
    |   '\u0cde'
    |   '\u0ce0'..'\u0ce1'
    |   '\u0ce6'..'\u0cef'
    |   '\u0d02'..'\u0d03'
    |   '\u0d05'..'\u0d0c'
    |   '\u0d0e'..'\u0d10'
    |   '\u0d12'..'\u0d28'
    |   '\u0d2a'..'\u0d39'
    |   '\u0d3e'..'\u0d43'
    |   '\u0d46'..'\u0d48'
    |   '\u0d4a'..'\u0d4d'
    |   '\u0d57'
    |   '\u0d60'..'\u0d61'
    |   '\u0d66'..'\u0d6f'
    |   '\u0d82'..'\u0d83'
    |   '\u0d85'..'\u0d96'
    |   '\u0d9a'..'\u0db1'
    |   '\u0db3'..'\u0dbb'
    |   '\u0dbd'
    |   '\u0dc0'..'\u0dc6'
    |   '\u0dca'
    |   '\u0dcf'..'\u0dd4'
    |   '\u0dd6'
    |   '\u0dd8'..'\u0ddf'
    |   '\u0df2'..'\u0df3'
    |   '\u0e01'..'\u0e3a'
    |   '\u0e3f'..'\u0e4e'
    |   '\u0e50'..'\u0e59'
    |   '\u0e81'..'\u0e82'
    |   '\u0e84'
    |   '\u0e87'..'\u0e88'        
    |   '\u0e8a'
    |   '\u0e8d'
    |   '\u0e94'..'\u0e97'
    |   '\u0e99'..'\u0e9f'
    |   '\u0ea1'..'\u0ea3'
    |   '\u0ea5'
    |   '\u0ea7'
    |   '\u0eaa'..'\u0eab'
    |   '\u0ead'..'\u0eb9'
    |   '\u0ebb'..'\u0ebd'
    |   '\u0ec0'..'\u0ec4'
    |   '\u0ec6'
    |   '\u0ec8'..'\u0ecd'
    |   '\u0ed0'..'\u0ed9'
    |   '\u0edc'..'\u0edd'
    |   '\u0f00'
    |   '\u0f18'..'\u0f19'
    |   '\u0f20'..'\u0f29'
    |   '\u0f35'
    |   '\u0f37'
    |   '\u0f39'
    |   '\u0f3e'..'\u0f47'
    |   '\u0f49'..'\u0f6a'
    |   '\u0f71'..'\u0f84'
    |   '\u0f86'..'\u0f8b'
    |   '\u0f90'..'\u0f97'
    |   '\u0f99'..'\u0fbc'
    |   '\u0fc6'
    |   '\u1000'..'\u1021'
    |   '\u1023'..'\u1027'
    |   '\u1029'..'\u102a'
    |   '\u102c'..'\u1032'
    |   '\u1036'..'\u1039'
    |   '\u1040'..'\u1049'
    |   '\u1050'..'\u1059'
    |   '\u10a0'..'\u10c5'
    |   '\u10d0'..'\u10f8'
    |   '\u1100'..'\u1159'
    |   '\u115f'..'\u11a2'
    |   '\u11a8'..'\u11f9'
    |   '\u1200'..'\u1206'        
    |   '\u1208'..'\u1246'
    |   '\u1248'
    |   '\u124a'..'\u124d'
    |   '\u1250'..'\u1256'
    |   '\u1258'
    |   '\u125a'..'\u125d'
    |   '\u1260'..'\u1286'
    |   '\u1288'        
    |   '\u128a'..'\u128d'
    |   '\u1290'..'\u12ae'
    |   '\u12b0'
    |   '\u12b2'..'\u12b5'
    |   '\u12b8'..'\u12be'
    |   '\u12c0'
    |   '\u12c2'..'\u12c5'
    |   '\u12c8'..'\u12ce'
    |   '\u12d0'..'\u12d6'
    |   '\u12d8'..'\u12ee'
    |   '\u12f0'..'\u130e'
    |   '\u1310'
    |   '\u1312'..'\u1315'
    |   '\u1318'..'\u131e'
    |   '\u1320'..'\u1346'
    |   '\u1348'..'\u135a'
    |   '\u1369'..'\u1371'
    |   '\u13a0'..'\u13f4'
    |   '\u1401'..'\u166c'
    |   '\u166f'..'\u1676'
    |   '\u1681'..'\u169a'
    |   '\u16a0'..'\u16ea'
    |   '\u16ee'..'\u16f0'
    |   '\u1700'..'\u170c'
    |   '\u170e'..'\u1714'
    |   '\u1720'..'\u1734'
    |   '\u1740'..'\u1753'
    |   '\u1760'..'\u176c'
    |   '\u176e'..'\u1770'
    |   '\u1772'..'\u1773'
    |   '\u1780'..'\u17d3'
    |   '\u17d7'
        |   '\u17db'..'\u17dd'
    |   '\u17e0'..'\u17e9'
    |   '\u180b'..'\u180d'
    |   '\u1810'..'\u1819'
    |   '\u1820'..'\u1877'
    |   '\u1880'..'\u18a9'
    |   '\u1900'..'\u191c'
    |   '\u1920'..'\u192b'
    |   '\u1930'..'\u193b'
    |   '\u1946'..'\u196d'
    |   '\u1970'..'\u1974'
    |   '\u1d00'..'\u1d6b'
    |   '\u1e00'..'\u1e9b'
    |   '\u1ea0'..'\u1ef9'
    |   '\u1f00'..'\u1f15'
    |   '\u1f18'..'\u1f1d'
    |   '\u1f20'..'\u1f45'
    |   '\u1f48'..'\u1f4d'
    |   '\u1f50'..'\u1f57'
    |   '\u1f59'
    |   '\u1f5b'
    |   '\u1f5d'
    |   '\u1f5f'..'\u1f7d'
    |   '\u1f80'..'\u1fb4'
    |   '\u1fb6'..'\u1fbc'        
    |   '\u1fbe'
    |   '\u1fc2'..'\u1fc4'
    |   '\u1fc6'..'\u1fcc'
    |   '\u1fd0'..'\u1fd3'
    |   '\u1fd6'..'\u1fdb'
    |   '\u1fe0'..'\u1fec'
    |   '\u1ff2'..'\u1ff4'
    |   '\u1ff6'..'\u1ffc'
    |   '\u200c'..'\u200f'
    |   '\u202a'..'\u202e'
    |   '\u203f'..'\u2040'
    |   '\u2054'
    |   '\u2060'..'\u2063'
    |   '\u206a'..'\u206f'
    |   '\u2071'
    |   '\u207f'
    |   '\u20a0'..'\u20b1'
    |   '\u20d0'..'\u20dc'
    |   '\u20e1'
    |   '\u20e5'..'\u20ea'
    |   '\u2102'
    |   '\u2107'
    |   '\u210a'..'\u2113'
    |   '\u2115'
    |   '\u2119'..'\u211d'
    |   '\u2124'
    |   '\u2126'
    |   '\u2128'
    |   '\u212a'..'\u212d'
    |   '\u212f'..'\u2131'
    |   '\u2133'..'\u2139'
    |   '\u213d'..'\u213f'
    |   '\u2145'..'\u2149'
    |   '\u2160'..'\u2183'
    |   '\u3005'..'\u3007'
    |   '\u3021'..'\u302f'        
    |   '\u3031'..'\u3035'
    |   '\u3038'..'\u303c'
    |   '\u3041'..'\u3096'
    |   '\u3099'..'\u309a'
    |   '\u309d'..'\u309f'
    |   '\u30a1'..'\u30ff'
    |   '\u3105'..'\u312c'
    |   '\u3131'..'\u318e'
    |   '\u31a0'..'\u31b7'
    |   '\u31f0'..'\u31ff'
    |   '\u3400'..'\u4db5'
    |   '\u4e00'..'\u9fa5'
    |   '\ua000'..'\ua48c'
    |   '\uac00'..'\ud7a3'
    |   '\uf900'..'\ufa2d'
    |   '\ufa30'..'\ufa6a'
    |   '\ufb00'..'\ufb06'
    |   '\ufb13'..'\ufb17'
    |   '\ufb1d'..'\ufb28'
    |   '\ufb2a'..'\ufb36'
    |   '\ufb38'..'\ufb3c'
    |   '\ufb3e'
    |   '\ufb40'..'\ufb41'
    |   '\ufb43'..'\ufb44'
    |   '\ufb46'..'\ufbb1'
    |   '\ufbd3'..'\ufd3d'
    |   '\ufd50'..'\ufd8f'
    |   '\ufd92'..'\ufdc7'
    |   '\ufdf0'..'\ufdfc'
    |   '\ufe00'..'\ufe0f'
    |   '\ufe20'..'\ufe23'
    |   '\ufe33'..'\ufe34'
    |   '\ufe4d'..'\ufe4f'
    |   '\ufe69'
    |   '\ufe70'..'\ufe74'
    |   '\ufe76'..'\ufefc'
    |   '\ufeff'
    |   '\uff04'
    |   '\uff10'..'\uff19'
    |   '\uff21'..'\uff3a'
    |   '\uff3f'
    |   '\uff41'..'\uff5a'
    |   '\uff65'..'\uffbe'
    |   '\uffc2'..'\uffc7'
    |   '\uffca'..'\uffcf'
    |   '\uffd2'..'\uffd7'
    |   '\uffda'..'\uffdc'
    |   '\uffe0'..'\uffe1'
    |   '\uffe5'..'\uffe6'
    |   '\ufff9'..'\ufffb' 
// UTF-16    |   ('\ud800'..'\udbff') ('\udc00'..'\udfff')
    ;


