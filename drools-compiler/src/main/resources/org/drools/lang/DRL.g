grammar DRL;

options {
	output=AST;
}

tokens {
	VT_COMPILATION_UNIT;
	VT_FUNCTION_IMPORT;

	VT_FACT;
	VT_CONSTRAINTS;
	VT_LABEL;

	VT_QUERY_ID;
	VT_TEMPLATE_ID;
	VT_TYPE_DECLARE_ID;
	VT_RULE_ID;
	VT_ENTRYPOINT_ID;
	VT_SLOT_ID;
	
	VT_SLOT;
	VT_RULE_ATTRIBUTES;

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

	VK_DATE_EFFECTIVE;
	VK_DATE_EXPIRES;
	VK_LOCK_ON_ACTIVE;
	VK_NO_LOOP;
	VK_AUTO_FOCUS;
	VK_ACTIVATION_GROUP;
	VK_AGENDA_GROUP;
	VK_RULEFLOW_GROUP;
	VK_DURATION;
	VK_DIALECT;
	VK_SALIENCE;
	VK_ENABLED;
	VK_ATTRIBUTES;
	VK_WHEN;
	VK_RULE;
	VK_IMPORT;
	VK_PACKAGE;
	VK_TEMPLATE;
	VK_QUERY;
	VK_DECLARE;
	VK_FUNCTION;
	VK_GLOBAL;
	VK_EVAL;
	VK_CONTAINS;
	VK_MATCHES;
	VK_EXCLUDES;
	VK_SOUNDSLIKE;
	VK_MEMBEROF;
	VK_ENTRY_POINT;
	VK_NOT;
	VK_IN;
	VK_OR;
	VK_AND;
	VK_EXISTS;
	VK_FORALL;
	VK_FROM;
	VK_ACCUMULATE;
	VK_INIT;
	VK_ACTION;
	VK_REVERSE;
	VK_RESULT;
	VK_COLLECT;
}

@parser::header {
	package org.drools.lang;
	
	import org.drools.compiler.DroolsParserException;
}

@lexer::header {
	package org.drools.lang;
}


@lexer::members {
	/** The standard method called to automatically emit a token at the
	 *  outermost lexical rule.  The token object should point into the
	 *  char buffer start..stop.  If there is a text override in 'text',
	 *  use that to set the token's text.  Override this method to emit
	 *  custom Token objects.
	 */
	public Token emit() {
		Token t = new DroolsToken(input, type, channel, tokenStartCharIndex, getCharIndex()-1);
		t.setLine(tokenStartLine);
		t.setText(text);
		t.setCharPositionInLine(tokenStartCharPositionInLine);
		emit(t);
		return t;
	}
}

@parser::members {
	private Stack<Map<Integer, String>> paraphrases = new Stack<Map<Integer, String>>();
	private List<DroolsParserException> errors = new ArrayList<DroolsParserException>();
	private DroolsParserExceptionFactory errorMessageFactory = new DroolsParserExceptionFactory(tokenNames, paraphrases);
	private String source = "unknown";
	private boolean lookaheadTest = false;

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
}

compilation_unit
	:	package_statement?
		statement*
		EOF
		-> ^(VT_COMPILATION_UNIT package_statement? statement*) 
	;
	catch [ RecognitionException e ] {
		reportError( e );
	}
	catch [ RewriteEmptyStreamException e ] {
	}

package_statement
@init  { pushParaphrases(DroolsParaphareseTypes.PACKAGE); }
@after { paraphrases.pop(); }
	:	package_key package_id SEMICOLON?
		-> ^(package_key package_id)
	;

package_id
	:	id+=ID ( id+=DOT id+=ID )*
	{	setParaphrasesValue(DroolsParaphareseTypes.PACKAGE, buildStringFromTokens($id));	}
		-> ^(VT_PACKAGE_ID ID+)
	;

statement
options{
k = 2;
}	:	rule_attribute
	|{(validateLT(1, "import") && validateLT(2, "function") )}?=> function_import_statement 
	|	import_statement 
	|	global 
	|	function
	|	{(validateLT(1, DroolsSoftKeywords.TEMPLATE))}?=> template
	|	{(validateLT(1, DroolsSoftKeywords.DECLARE))}?=> type_declaration
	|	rule
	|	query
	;

import_statement
@init  { pushParaphrases(DroolsParaphareseTypes.IMPORT); }
@after { paraphrases.pop(); }
	:	import_key import_name[DroolsParaphareseTypes.IMPORT] SEMICOLON?
		-> ^(import_key import_name)
	;

function_import_statement
@init  { pushParaphrases(DroolsParaphareseTypes.FUNCTION_IMPORT); }
@after { paraphrases.pop(); }
	:	imp=import_key function_key import_name[DroolsParaphareseTypes.FUNCTION_IMPORT] SEMICOLON?
		-> ^(VT_FUNCTION_IMPORT[$imp.start] function_key import_name)
	;

import_name [int importType]
	:	id+=ID ( id+=DOT id+=ID )* id+=DOT_STAR?
	{	setParaphrasesValue($importType, buildStringFromTokens($id));	}
		-> ^(VT_IMPORT_ID ID+ DOT_STAR?)
	;

global
@init  { pushParaphrases(DroolsParaphareseTypes.GLOBAL); }
@after { paraphrases.pop(); }
	:	global_key data_type global_id SEMICOLON?
		-> ^(global_key data_type global_id)
	;

global_id
	:	id=ID
	{	setParaphrasesValue(DroolsParaphareseTypes.GLOBAL, $id.text);	}
		-> VT_GLOBAL_ID[$id]
	;

function
@init  { pushParaphrases(DroolsParaphareseTypes.FUNCTION); }
@after { paraphrases.pop(); }
	:	function_key data_type? function_id parameters curly_chunk
		-> ^(function_key data_type? function_id parameters curly_chunk)
	;

function_id
	:	id=ID
	{	setParaphrasesValue(DroolsParaphareseTypes.FUNCTION, $id.text);	}
		-> VT_FUNCTION_ID[$id]
	;

query
@init  { pushParaphrases(DroolsParaphareseTypes.QUERY); }
@after { paraphrases.pop(); }
	:	query_key query_id parameters? normal_lhs_block END SEMICOLON?
		-> ^(query_key query_id parameters? normal_lhs_block END)
	;

query_id
	: 	id=ID
	{	setParaphrasesValue(DroolsParaphareseTypes.QUERY, $id.text);	} -> VT_QUERY_ID[$id]
	| 	id=STRING
	{	setParaphrasesValue(DroolsParaphareseTypes.QUERY, $id.text);	} -> VT_QUERY_ID[$id]
	;

parameters
	:	LEFT_PAREN
			( param_definition (COMMA param_definition)* )?
		RIGHT_PAREN
		-> ^(VT_PARAM_LIST param_definition* RIGHT_PAREN)
	;

param_definition
	:	data_type? argument
	;

argument
	:	ID dimension_definition*
	;

type_declaration
@init  { pushParaphrases(DroolsParaphareseTypes.TYPE_DECLARE); }
@after { paraphrases.pop(); }
	:	declare_key  type_declare_id
		decl_metadata*
		decl_field*
		END
		-> ^(declare_key type_declare_id decl_metadata* decl_field* END)
	;

type_declare_id
	: 	id=ID
	{	setParaphrasesValue(DroolsParaphareseTypes.TYPE_DECLARE, $id.text);	} -> VT_TYPE_DECLARE_ID[$id]
	;

decl_metadata
	:	AT ID paren_chunk
		-> ^(AT ID paren_chunk)
	;

decl_field
	:	ID decl_field_initialization? COLON data_type
		decl_metadata*
		-> ^(ID decl_field_initialization? data_type decl_metadata*)
	;

decl_field_initialization
	:	EQUALS paren_chunk
	-> ^(EQUALS paren_chunk)
	;

template
@init  { pushParaphrases(DroolsParaphareseTypes.TEMPLATE); }
@after { paraphrases.pop(); }
	:	template_key template_id SEMICOLON?
		template_slot+
		END SEMICOLON?
		-> ^(template_key template_id template_slot+ END)
	;

template_id
	: 	id=ID
	{	setParaphrasesValue(DroolsParaphareseTypes.TEMPLATE, $id.text);	} -> VT_TEMPLATE_ID[$id]
	| 	id=STRING
	{	setParaphrasesValue(DroolsParaphareseTypes.TEMPLATE, $id.text);	} -> VT_TEMPLATE_ID[$id]
	;

template_slot
	:	 data_type slot_id SEMICOLON?
		-> ^(VT_SLOT data_type slot_id)
	;

slot_id	:	id=ID
		-> VT_SLOT_ID[$id]
	;

rule
@init  { pushParaphrases(DroolsParaphareseTypes.RULE); }
@after { paraphrases.pop(); }
	:	rule_key rule_id rule_attributes? when_part? rhs_chunk
		-> ^(rule_key rule_id rule_attributes? when_part? rhs_chunk)
	;

when_part
	:	when_key COLON? normal_lhs_block
	->	when_key normal_lhs_block
	;

rule_id
	: 	id=ID
	{	setParaphrasesValue(DroolsParaphareseTypes.RULE, $id.text);	} -> VT_RULE_ID[$id]
	| 	id=STRING
	{	setParaphrasesValue(DroolsParaphareseTypes.RULE, $id.text);	} -> VT_RULE_ID[$id]
	;

rule_attributes
	:	( attributes_key COLON )? rule_attribute ( COMMA? attr=rule_attribute )*
		-> ^(VT_RULE_ATTRIBUTES attributes_key? rule_attribute+)
	;

rule_attribute
@init  { pushParaphrases(DroolsParaphareseTypes.RULE_ATTRIBUTE); }
@after { paraphrases.pop(); }
	:	salience 
	|	no_loop  
	|	agenda_group  
	|	duration  
	|	activation_group 
	|	auto_focus 
	|	date_effective 
	|	date_expires 
	|	enabled 
	|	ruleflow_group 
	|	lock_on_active
	|	dialect 
	;

date_effective
	:	date_effective_key^ STRING
	;

date_expires
	:	date_expires_key^ STRING  
	;
	
enabled
	:	enabled_key^ BOOL
	;	

salience
	:	salience_key^
		( INT   
		| paren_chunk
		)
	;
	
no_loop
	:	no_loop_key^ BOOL?
	;

auto_focus
	:	auto_focus_key^ BOOL?
	;	
	
activation_group
	:	activation_group_key^ STRING
	;

ruleflow_group
	:	ruleflow_group_key^ STRING
	;

agenda_group
	:	agenda_group_key^ STRING
	;

duration
	:	duration_key^ INT 
	;	
	
dialect
	:	dialect_key^ STRING   
	;			
	
lock_on_active
	:	lock_on_active_key^ BOOL?
	;

normal_lhs_block
	:	lhs*
	->	^(VT_AND_IMPLICIT lhs*)
	;

lhs	:	lhs_or
	;

lhs_or
@init{
	Token orToken = null;
}	:	(LEFT_PAREN or_key)=> LEFT_PAREN or=or_key lhs_and+ RIGHT_PAREN // PREFIX
		-> ^(VT_OR_PREFIX[$or.start] lhs_and+ RIGHT_PAREN)
	|	(lhs_and -> lhs_and) 
		( (or_key|DOUBLE_PIPE)=> (value=or_key {orToken = $value.start;} |pipe=DOUBLE_PIPE {orToken = $pipe;}) lhs_and 
		-> ^(VT_OR_INFIX[orToken] $lhs_or lhs_and))*
	;

lhs_and
@init{
	Token andToken = null;
}	:	(LEFT_PAREN and_key)=> LEFT_PAREN and=and_key lhs_unary+ RIGHT_PAREN // PREFIX
		-> ^(VT_AND_PREFIX[$and.start] lhs_unary+ RIGHT_PAREN)
	|	(lhs_unary -> lhs_unary) 
		( (and_key|DOUBLE_AMPER)=> (value=and_key {andToken = $value.start;} |amper=DOUBLE_AMPER {andToken = $amper;}) lhs_unary 
		-> ^(VT_AND_INFIX[andToken] $lhs_and lhs_unary) )*
	;

lhs_unary
options{backtrack=true;}
	:	(	lhs_exist
		|	lhs_not
		|	lhs_eval
		|	lhs_forall
		|	LEFT_PAREN! lhs_or RIGHT_PAREN
		|	pattern_source
		)
		((SEMICOLON)=> SEMICOLON!)?
	;

lhs_exist
	:	exists_key
	        ( (LEFT_PAREN (or_key|and_key))=> lhs_or //prevent "((" 
		| LEFT_PAREN lhs_or RIGHT_PAREN
	        | lhs_pattern
	        )
	        -> ^(exists_key lhs_or? lhs_pattern? RIGHT_PAREN?)
	;
	
lhs_not	:	not_key
		( (LEFT_PAREN (or_key|and_key))=> lhs_or //prevent "((" 
		|	LEFT_PAREN lhs_or RIGHT_PAREN 
		| 	lhs_pattern )
	        -> ^(not_key lhs_or? lhs_pattern? RIGHT_PAREN?)
	;

lhs_eval
	:	ev=eval_key pc=paren_chunk
	{	String body = safeSubstring( $pc.text, 1, $pc.text.length()-1 );
		checkTrailingSemicolon( body, $ev.start );	}
		-> ^(eval_key paren_chunk)
	;

lhs_forall
	:	forall_key LEFT_PAREN lhs_pattern+ RIGHT_PAREN
		-> ^(forall_key lhs_pattern+ RIGHT_PAREN)
	;

pattern_source
options { backtrack=true;}
	:	lhs_pattern
		over_clause?
		(
			from_key^
		        (  accumulate_statement
		          | collect_statement 
		          | entrypoint_statement
		          | from_source
		        )
		)?
	;

over_clause
	:	OVER^ over_elements (COMMA! over_elements)*
	;

over_elements
	:	ID COLON ID paren_chunk
	-> ^(VT_BEHAVIOR ID ID paren_chunk)
	;

accumulate_statement
	:	accumulate_key
		LEFT_PAREN lhs_or COMMA? 
		(	accumulate_init_clause
		|	accumulate_id_clause
		)
		RIGHT_PAREN
		-> ^(accumulate_key lhs_or accumulate_init_clause? accumulate_id_clause? RIGHT_PAREN)
	;

accumulate_init_clause
	:	init_key
	pc1=paren_chunk COMMA?
	action_key pc2=paren_chunk COMMA?
	( reverse_key pc3=paren_chunk COMMA?)?
	result_key pc4=paren_chunk
	-> ^(VT_ACCUMULATE_INIT_CLAUSE ^(init_key $pc1) ^(action_key $pc2) ^(reverse_key $pc3)? ^(result_key $pc4))
	;

accumulate_id_clause
	:	id=ID text=paren_chunk
	-> ^(VT_ACCUMULATE_ID_CLAUSE ID paren_chunk)
	;

collect_statement
	:	collect_key
		LEFT_PAREN pattern_source RIGHT_PAREN
	-> ^(collect_key pattern_source RIGHT_PAREN)
	;

entrypoint_statement
	:	entry_point_key entrypoint_id
	-> ^(entry_point_key entrypoint_id)
	;

entrypoint_id
	: 	value=ID	-> VT_ENTRYPOINT_ID[$value]
	| 	value=STRING	-> VT_ENTRYPOINT_ID[$value]
	;

from_source
	:	ID
		( (LEFT_PAREN)=> args=paren_chunk )?
		expression_chain?
	->	^(VT_FROM_SOURCE ID paren_chunk? expression_chain?)
	;
	
expression_chain
	:
	 startToken=DOT ID
	  (
	    ( LEFT_SQUARE ) => square_chunk
	    |
	    ( LEFT_PAREN ) => paren_chunk
	  )?
	  expression_chain?
	  -> ^(VT_EXPRESSION_CHAIN[$startToken] ID square_chunk? paren_chunk? expression_chain?)
	;

lhs_pattern
	:	fact_binding -> ^(VT_PATTERN fact_binding)
	|	fact -> ^(VT_PATTERN fact)
	;

fact_binding
 	:	label
		( fact
 		| LEFT_PAREN fact_binding_expression RIGHT_PAREN
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
@init  { pushParaphrases(DroolsParaphareseTypes.PATTERN); }
@after { paraphrases.pop(); }
	:	pattern_type LEFT_PAREN constraints? RIGHT_PAREN
	->	^(VT_FACT pattern_type constraints? RIGHT_PAREN)
	;

constraints
	:	constraint ( COMMA! constraint )*
	;

constraint
	:	or_constr
	;

or_constr
	:	and_constr ( DOUBLE_PIPE^ and_constr )* 
	;

and_constr
	:	unary_constr ( DOUBLE_AMPER^ unary_constr )*
	;

unary_constr
options {k=2;}
	:	eval_key^ paren_chunk
	|	field_constraint
	|	LEFT_PAREN! or_constr RIGHT_PAREN
	;

field_constraint
@init{
	boolean isArrow = false;
}	:	label accessor_path ( or_restr_connective | arw=ARROW paren_chunk {isArrow = true;})?
		-> {isArrow}? ^(VT_BIND_FIELD label ^(VT_FIELD accessor_path)) ^(VK_EVAL[$arw] paren_chunk)?
		-> ^(VT_BIND_FIELD label ^(VT_FIELD accessor_path or_restr_connective?))
	|	accessor_path or_restr_connective
		-> ^(VT_FIELD accessor_path or_restr_connective)
	;

label	:	value=ID COLON -> VT_LABEL[$value]
	;

or_restr_connective
	:	and_restr_connective ({(validateRestr())}?=> DOUBLE_PIPE^ and_restr_connective )* 
	;

and_restr_connective
	:	constraint_expression ({(validateRestr())}?=> DOUBLE_AMPER^ constraint_expression )*
	;

constraint_expression
options{
k=3;
}	:	compound_operator
	|	simple_operator
	|	LEFT_PAREN! or_restr_connective RIGHT_PAREN
	;
	catch [ RecognitionException re ] {
		if (!lookaheadTest){
			reportError(re);
			recover(input, re);
		} else {
			throw re;
		}
	}

simple_operator
	:	(EQUAL^
	|	GREATER^
	|	GREATER_EQUAL^
	|	LESS^
	|	LESS_EQUAL^
	|	NOT_EQUAL^
	|	not_key (contains_key^|soundslike_key^|matches_key^|memberof_key^| ID^ | GRAVE_ACCENT! ID^ square_chunk)
	|	contains_key^
	|	excludes_key^
	|	matches_key^
	|	soundslike_key^
	|	memberof_key^
	|	ID^
	|	GRAVE_ACCENT! ID^ square_chunk)
	expression_value
	;

//Simple Syntax Sugar
compound_operator 
	:	( in_key^ | not_key in_key^ ) LEFT_PAREN! expression_value ( COMMA! expression_value )* RIGHT_PAREN
	;

expression_value
	:	accessor_path
	|	literal_constraint 
	|	paren_chunk
	;

literal_constraint
	:	STRING
	|	INT
	|	FLOAT
	|	BOOL
	|	NULL
	;

pattern_type
	:	id+=ID ( id+=DOT id+=ID )* 
	{	setParaphrasesValue(DroolsParaphareseTypes.PATTERN, buildStringFromTokens($id));	} 
	    dimension_definition*
		-> ^(VT_PATTERN_TYPE ID+ dimension_definition*)
	;

data_type
	:	ID ( DOT ID )* dimension_definition*
		-> ^(VT_DATA_TYPE ID+ dimension_definition*)
	;

dimension_definition
	:	LEFT_SQUARE RIGHT_SQUARE
	;

accessor_path
	:	accessor_element ( DOT accessor_element )*
	-> ^(VT_ACCESSOR_PATH accessor_element+)
	;

accessor_element
	:	ID square_chunk*
	-> ^(VT_ACCESSOR_ELEMENT ID square_chunk*)
	;

rhs_chunk
@init{
	String text = "";
}	:	rc=rhs_chunk_data {text = $rc.text;}
	-> VT_RHS_CHUNK[$rc.start,text]
	;

rhs_chunk_data
	:	THEN ( ~END )* END SEMICOLON?
	;

curly_chunk
@init{
	String text = "";
}	:	cc=curly_chunk_data {text = $cc.text;}
	-> VT_CURLY_CHUNK[$cc.start,text]
	;

curly_chunk_data
	:	LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk_data )* RIGHT_CURLY
	;

paren_chunk
@init{
	String text = "";
}	:	pc=paren_chunk_data {text = $pc.text;} 
	-> VT_PAREN_CHUNK[$pc.start,text]
	;

paren_chunk_data
	:	LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk_data )* RIGHT_PAREN 
	;

square_chunk
@init{
	String text = "";
}	:	sc=square_chunk_data {text = $sc.text;}
	-> VT_SQUARE_CHUNK[$sc.start,text]
	;

square_chunk_data
	:	LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk_data )* RIGHT_SQUARE
	;


date_effective_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))}?=>  ID MISC ID {text = $text;}
	->	VK_DATE_EFFECTIVE[$start, text]
	;

date_expires_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))}?=>  ID MISC ID {text = $text;}
	->	VK_DATE_EXPIRES[$start, text]
	;

lock_on_active_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))}?=>  ID MISC ID MISC ID {text = $text;}
	->	VK_LOCK_ON_ACTIVE[$start, text]
	;

no_loop_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))}?=>  ID MISC ID {text = $text;}
	->	VK_NO_LOOP[$start, text]
	;

auto_focus_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))}?=>  ID MISC ID {text = $text;}
	->	VK_AUTO_FOCUS[$start, text]
	;

activation_group_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))}?=>  ID MISC ID {text = $text;}
	->	VK_ACTIVATION_GROUP[$start, text]
	;

agenda_group_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))}?=>  ID MISC ID {text = $text;}
	->	VK_AGENDA_GROUP[$start, text]
	;

ruleflow_group_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))}?=>  ID MISC ID {text = $text;}
	->	VK_RULEFLOW_GROUP[$start, text]
	;

duration_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.DURATION))}?=>  id=ID	->	VK_DURATION[$id]
	;

package_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))}?=>  id=ID	->	VK_PACKAGE[$id]
	;

import_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.IMPORT))}?=>  id=ID	->	VK_IMPORT[$id]
	;

dialect_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.DIALECT))}?=>  id=ID	->	VK_DIALECT[$id]
	;

salience_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))}?=>  id=ID	->	VK_SALIENCE[$id]
	;

enabled_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.ENABLED))}?=>  id=ID	->	VK_ENABLED[$id]
	;

attributes_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))}?=>  id=ID	->	VK_ATTRIBUTES[$id]
	;

when_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.WHEN))}?=>  id=ID	->	VK_WHEN[$id]
	;

rule_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.RULE))}?=>  id=ID	->	VK_RULE[$id]
	;

template_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))}?=>  id=ID	->	VK_TEMPLATE[$id]
	;

query_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.QUERY))}?=>  id=ID	->	VK_QUERY[$id]
	;

declare_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.DECLARE))}?=>  id=ID	->	VK_DECLARE[$id]
	;

function_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))}?=>  id=ID	->	VK_FUNCTION[$id]
	;

global_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))}?=>  id=ID	->	VK_GLOBAL[$id]
	;

eval_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.EVAL))}?=>  id=ID	->	VK_EVAL[$id]
	;

contains_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.CONTAINS))}?=>  id=ID	->	VK_CONTAINS[$id]
	;

matches_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.MATCHES))}?=>  id=ID	->	VK_MATCHES[$id]
	;

excludes_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))}?=>  id=ID	->	VK_EXCLUDES[$id]
	;

soundslike_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))}?=>  id=ID	->	VK_SOUNDSLIKE[$id]
	;

memberof_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))}?=>  id=ID	->	VK_MEMBEROF[$id]
	;

not_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.NOT))}?=>  id=ID	->	VK_NOT[$id]
	;

in_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.IN))}?=>  id=ID	->	VK_IN[$id]
	;

or_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.OR))}?=>  id=ID	->	VK_OR[$id]
	;

and_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.AND))}?=>  id=ID	->	VK_AND[$id]
	;

exists_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.EXISTS))}?=>  id=ID	->	VK_EXISTS[$id]
	;

forall_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.FORALL))}?=>  id=ID	->	VK_FORALL[$id]
	;

from_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.FROM))}?=>  id=ID	->	VK_FROM[$id]
	;

entry_point_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT))}?=>  ID MISC ID {text = $text;}
	->	VK_ENTRY_POINT[$start, text]
	;

accumulate_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.ACCUMULATE))}?=>  id=ID	->	VK_ACCUMULATE[$id]
	;

init_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.INIT))}?=>  id=ID	->	VK_INIT[$id]
	;

action_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.ACTION))}?=>  id=ID	->	VK_ACTION[$id]
	;

reverse_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.REVERSE))}?=>  id=ID	->	VK_REVERSE[$id]
	;

result_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.RESULT))}?=>  id=ID	->	VK_RESULT[$id]
	;

collect_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.COLLECT))}?=>  id=ID	->	VK_COLLECT[$id]
	;

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
        
INT	
	:	('-')?('0'..'9')+
		;

FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
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

NULL	:	'null';

OVER
	:	'over'
	;

THEN
	:	'then'
	;

END	:	'end'
	;

GRAVE_ACCENT
	:	'`'
	;

AT	:	'@'
	;

EQUALS
	:	'='
	;

SEMICOLON
	:	';'
	;

DOT_STAR
	:	'.*'
	;

COLON
	:	':'
	;

EQUAL
	:	'=='
	;

NOT_EQUAL
	:	'!='
	;

GREATER
	:	'>'
	;

GREATER_EQUAL
	:	'>='
	;

LESS
	:	'<'
	;

LESS_EQUAL
	:	'<='
	;

ARROW
	:	'->'
	;

ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
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

SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; setText("//"+getText().substring(1));}
	;
        
        
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;

MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

MISC 	:
		'!' | '$' | '%' | '^' | '*' | '_' | '-' | '+'  | '?' | '/' | '\'' | '\\' | '|' | '&'
	;