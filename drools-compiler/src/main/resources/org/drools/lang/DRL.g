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
	VK_RULE;
	VK_EXTEND;
	VK_IMPORT;
	VK_PACKAGE;
	VK_TEMPLATE;
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
	VK_ACTION;
	VK_REVERSE;
	VK_RESULT;
	VK_OPERATOR;
	VK_END;
	VK_INIT;
}

@parser::header {
	package org.drools.lang;
	
	import java.util.List;
	import java.util.LinkedList;
	import org.drools.compiler.DroolsParserException;
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

@parser::members {
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

package_statement
@init  { pushParaphrases(DroolsParaphraseTypes.PACKAGE); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.PACKAGE); }
@after { paraphrases.pop(); }
	:	package_key
		package_id SEMICOLON?
	{	emit($SEMICOLON, DroolsEditorType.SYMBOL);	}
		-> ^(package_key package_id)
	;

package_id
	:	id+=ID ( id+=DOT id+=ID )*
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.PACKAGE, buildStringFromTokens($id));	}
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
@init  { pushParaphrases(DroolsParaphraseTypes.IMPORT); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.IMPORT_STATEMENT);  }
@after { paraphrases.pop(); }
	:	import_key import_name[DroolsParaphraseTypes.IMPORT] SEMICOLON?
	{	emit($SEMICOLON, DroolsEditorType.SYMBOL);	}
		-> ^(import_key import_name)
	;

function_import_statement
@init  { pushParaphrases(DroolsParaphraseTypes.FUNCTION_IMPORT); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.FUNCTION_IMPORT_STATEMENT); }
@after { paraphrases.pop(); }
	:	imp=import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] SEMICOLON?
	{	emit($SEMICOLON, DroolsEditorType.SYMBOL);	}		
		-> ^(VT_FUNCTION_IMPORT[$imp.start] function_key import_name)
	;

import_name [DroolsParaphraseTypes importType]
	:	id+=ID ( id+=DOT id+=ID )* id+=DOT_STAR?
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue($importType, buildStringFromTokens($id));	}
		-> ^(VT_IMPORT_ID ID+ DOT_STAR?)
	;

global
@init  { pushParaphrases(DroolsParaphraseTypes.GLOBAL);  if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.GLOBAL); }
@after { paraphrases.pop(); }
	:	global_key data_type global_id SEMICOLON?
	{	emit($SEMICOLON, DroolsEditorType.SYMBOL);	}
		-> ^(global_key data_type global_id)
	;

global_id
	:	id=ID
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.GLOBAL, $id.text);	}
		-> VT_GLOBAL_ID[$id]
	;

function
@init  { pushParaphrases(DroolsParaphraseTypes.FUNCTION); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.FUNCTION);  }
@after { paraphrases.pop(); }
	:	function_key data_type? function_id parameters curly_chunk
		-> ^(function_key data_type? function_id parameters curly_chunk)
	;

function_id
	:	id=ID
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.FUNCTION, $id.text);	}
		-> VT_FUNCTION_ID[$id]
	;

query
@init  { pushParaphrases(DroolsParaphraseTypes.QUERY); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.QUERY); }
@after { paraphrases.pop(); }
	:	query_key query_id 
	{	emit(Location.LOCATION_RULE_HEADER);	}
		parameters? 
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
		normal_lhs_block 
		end=end_key SEMICOLON?
	{	emit($SEMICOLON, DroolsEditorType.SYMBOL);	}
		-> ^(query_key query_id parameters? normal_lhs_block end_key)
	;

query_id
	: 	id=ID
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.QUERY, $id.text);	} -> VT_QUERY_ID[$id]
	| 	id=STRING
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.QUERY, $id.text);	} -> VT_QUERY_ID[$id]
	;

parameters
	:	LEFT_PAREN {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}
			( param_definition (COMMA {	emit($COMMA, DroolsEditorType.SYMBOL);	} param_definition)* )?
		RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
		-> ^(VT_PARAM_LIST param_definition* RIGHT_PAREN)
	;

param_definition
	:	data_type? argument
	;

argument
	:	ID {	emit($ID, DroolsEditorType.IDENTIFIER);	}
		dimension_definition*
	;

type_declaration
@init  { pushParaphrases(DroolsParaphraseTypes.TYPE_DECLARE); if ( state.backtracking==0 ) beginSentence(DroolsSentenceType.TYPE_DECLARATION); }
@after { paraphrases.pop(); }
	:	declare_key  type_declare_id
		decl_metadata*
		decl_field*
		end_key
		-> ^(declare_key type_declare_id decl_metadata* decl_field* end_key)
	;

type_declare_id
	: 	id=ID
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.TYPE_DECLARE, $id.text);	} -> VT_TYPE_DECLARE_ID[$id]
	;

decl_metadata
	:	AT 
	{	emit($AT, DroolsEditorType.SYMBOL);	}
		ID
	{	emit($ID, DroolsEditorType.IDENTIFIER);	}
		paren_chunk?
		-> ^(AT ID paren_chunk?)
	;

decl_field
	:	ID	{	emit($ID, DroolsEditorType.IDENTIFIER);	}
		decl_field_initialization? 
		COLON	{	emit($COLON, DroolsEditorType.SYMBOL);	}
		data_type
		decl_metadata*
		-> ^(ID decl_field_initialization? data_type decl_metadata*)
	;

decl_field_initialization
	:	EQUALS	{	emit($EQUALS, DroolsEditorType.SYMBOL);	}
		paren_chunk
	-> ^(EQUALS paren_chunk)
	;

template
@init  { pushParaphrases(DroolsParaphraseTypes.TEMPLATE); }
@after { paraphrases.pop(); }
	:	
	{	beginSentence(DroolsSentenceType.TEMPLATE);	}
		template_key template_id 
		semi1=SEMICOLON?
	{	emit($semi1, DroolsEditorType.SYMBOL);	}
		template_slot+
		end=end_key semi2=SEMICOLON?
	{	emit($semi2, DroolsEditorType.SYMBOL);	}
		-> ^(template_key template_id template_slot+ end_key)
	;

template_id
	: 	id=ID
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.TEMPLATE, $id.text);	} -> VT_TEMPLATE_ID[$id]
	| 	id=STRING
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.TEMPLATE, $id.text);	} -> VT_TEMPLATE_ID[$id]
	;

template_slot
	:	 data_type slot_id SEMICOLON?
	{	emit($SEMICOLON, DroolsEditorType.SYMBOL);	}
		-> ^(VT_SLOT data_type slot_id)
	;

slot_id
	:	id=ID
	{	emit($id, DroolsEditorType.IDENTIFIER);	}
		-> VT_SLOT_ID[$id]
	;

rule
@init  { boolean isFailed = true; pushParaphrases(DroolsParaphraseTypes.RULE); }
@after { paraphrases.pop(); isFailed = false; }
	:
	{	beginSentence(DroolsSentenceType.RULE);	}
		rule_key rule_id 
	{	emit(Location.LOCATION_RULE_HEADER);	}
		(extend_key rule_id)? decl_metadata* rule_attributes? when_part? rhs_chunk
		-> ^(rule_key rule_id ^(extend_key rule_id)? decl_metadata* rule_attributes? when_part? rhs_chunk)
	;
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

when_part
	: 	WHEN {	emit($WHEN, DroolsEditorType.KEYWORD);	}
		COLON? {	emit($COLON, DroolsEditorType.SYMBOL);	}
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
		normal_lhs_block
	->	WHEN normal_lhs_block
	;

rule_id
	: 	id=ID
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.RULE, $id.text);	} -> VT_RULE_ID[$id]
	| 	id=STRING
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.RULE, $id.text);	} -> VT_RULE_ID[$id]
	;

rule_attributes
	:	( attributes_key COLON {	emit($COLON, DroolsEditorType.SYMBOL);	} )? 
		rule_attribute ( COMMA? {	emit($COMMA, DroolsEditorType.SYMBOL);	} attr=rule_attribute )*
		-> ^(VT_RULE_ATTRIBUTES attributes_key? rule_attribute+)
	;

rule_attribute
@init  { boolean isFailed = true; pushParaphrases(DroolsParaphraseTypes.RULE_ATTRIBUTE); }
@after { paraphrases.pop(); isFailed = false; if (!(retval.tree instanceof CommonErrorNode)) emit(Location.LOCATION_RULE_HEADER); }
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
finally {
	if (isEditorInterfaceEnabled && isFailed) {
		if (input.LA(2) == EOF && input.LA(1) == ID){
			emit(input.LT(1), DroolsEditorType.IDENTIFIER);
			input.consume();
		}
	}
}
date_effective
	:	date_effective_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	emit($STRING, DroolsEditorType.STRING_CONST );	}
	;

date_expires
	:	date_expires_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	emit($STRING, DroolsEditorType.STRING_CONST );	}
	;
	
enabled
	:	enabled_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} 
	    ( BOOL {	emit($BOOL, DroolsEditorType.BOOLEAN_CONST );	}
	    | paren_chunk 
	    )
	;	

salience
	:	salience_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	}
		( INT 	{	emit($INT, DroolsEditorType.NUMERIC_CONST );	}
		| paren_chunk
		)
	;

no_loop
	:	no_loop_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} BOOL?
	{	emit($BOOL, DroolsEditorType.BOOLEAN_CONST );	}
	;

auto_focus
	:	auto_focus_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} BOOL?
	{	emit($BOOL, DroolsEditorType.BOOLEAN_CONST );	}
	;	
	
activation_group
	:	activation_group_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	emit($STRING, DroolsEditorType.STRING_CONST );	}
	;

ruleflow_group
	:	ruleflow_group_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	emit($STRING, DroolsEditorType.STRING_CONST );	}
	;

agenda_group
	:	agenda_group_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	emit($STRING, DroolsEditorType.STRING_CONST );	}
	;

duration
	:	duration_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} 
	    ( INT {	emit($INT, DroolsEditorType.NUMERIC_CONST );	}
	    | paren_chunk
	    )
	;	
	
dialect
	:	dialect_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	emit($STRING, DroolsEditorType.STRING_CONST );	}
	;			
	
lock_on_active
	:	lock_on_active_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} BOOL?
	{	emit($BOOL, DroolsEditorType.BOOLEAN_CONST );	}
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
}	:	(LEFT_PAREN or_key)=> 
		LEFT_PAREN  {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}
			or=or_key
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	}
			lhs_and+ 
		RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	} // PREFIX 
		-> ^(VT_OR_PREFIX[$or.start] lhs_and+ RIGHT_PAREN)
	|	(lhs_and -> lhs_and) 
		( (or_key|DOUBLE_PIPE)=> (value=or_key {orToken = $value.start;} |pipe=DOUBLE_PIPE {orToken = $pipe; emit($DOUBLE_PIPE, DroolsEditorType.SYMBOL);}) 
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	}
		lhs_and 
		-> ^(VT_OR_INFIX[orToken] $lhs_or lhs_and))*
	;

lhs_and
@init{
	Token andToken = null;
}	:	(LEFT_PAREN and_key)=> 
		LEFT_PAREN {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			and=and_key
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	}
			lhs_unary+ 
		RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}  // PREFIX
		-> ^(VT_AND_PREFIX[$and.start] lhs_unary+ RIGHT_PAREN)
	|	(lhs_unary -> lhs_unary) 
		( (and_key|DOUBLE_AMPER)=> (value=and_key {andToken = $value.start;} |amper=DOUBLE_AMPER {andToken = $amper; emit($DOUBLE_AMPER, DroolsEditorType.SYMBOL);}) 
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR);	}
		lhs_unary 
		-> ^(VT_AND_INFIX[andToken] $lhs_and lhs_unary) )*
	;

lhs_unary
	:	(	lhs_exist
		|{validateNotWithBinding()}?=>	lhs_not_binding
		|	lhs_not
		|	lhs_eval
		|	lhs_forall
		|	LEFT_PAREN! {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL); emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	}  
				lhs_or 
			RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
		|	pattern_source
		)
		((SEMICOLON)=> SEMICOLON! {	emit($SEMICOLON, DroolsEditorType.SYMBOL);	})?
	;

lhs_exist
	:	exists_key
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS);	}
	        ( (LEFT_PAREN (or_key|and_key))=> lhs_or //prevent "((" 
		| LEFT_PAREN {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			lhs_or 
		  RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
	        | lhs_pattern
	        )
	        -> ^(exists_key lhs_or? lhs_pattern? RIGHT_PAREN?)
	;

lhs_not_binding
	:	not_key fact_binding
	-> ^(not_key ^(VT_PATTERN fact_binding))
	;

lhs_not	:	not_key
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT);	}
		( (LEFT_PAREN (or_key|and_key))=> {	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	} lhs_or //prevent "((" 
		|	LEFT_PAREN {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL); emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION );	} 
				lhs_or 
			RIGHT_PAREN  {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
		| 	lhs_pattern )
	        -> ^(not_key lhs_or? lhs_pattern? RIGHT_PAREN?)
	;

lhs_eval
	:	ev=eval_key
	{	emit(Location.LOCATION_LHS_INSIDE_EVAL);	}
		pc=paren_chunk
	{	if (((DroolsTree) $pc.tree).getText() != null){
			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	            		
		}
	}
	{	String body = safeSubstring( $pc.text, 1, $pc.text.length()-1 );
		checkTrailingSemicolon( body, $ev.start );	}
		-> ^(eval_key paren_chunk)
	;

lhs_forall
	:	forall_key 
		LEFT_PAREN {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			pattern_source+ 
		RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
		-> ^(forall_key pattern_source+ RIGHT_PAREN)
	;

pattern_source
@init { boolean isFailed = true;	}
@after { isFailed = false;	}
	:	lhs_pattern
		over_clause?
		(
			FROM^
		{	emit($FROM, DroolsEditorType.KEYWORD);
			emit(Location.LOCATION_LHS_FROM);	}
		        (  accumulate_statement
		          | collect_statement 
		          | entrypoint_statement
		          | from_source
		        )
		)?
	;
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

over_clause
	:	OVER^ {	emit($OVER, DroolsEditorType.KEYWORD);	} over_elements 
			(COMMA! {	emit($COMMA, DroolsEditorType.SYMBOL);	} over_elements)*
	;

over_elements
	:	id1=ID {	emit($id1, DroolsEditorType.IDENTIFIER);	} 
		COLON {	emit($COLON, DroolsEditorType.SYMBOL);	} 
		id2=ID {	emit($id2, DroolsEditorType.IDENTIFIER);	} 
		paren_chunk
	-> ^(VT_BEHAVIOR $id1 $id2 paren_chunk)
	;

accumulate_statement
	:	ACCUMULATE {	emit($ACCUMULATE, DroolsEditorType.KEYWORD);	}
	{	emit(Location.LOCATION_LHS_FROM_ACCUMULATE);	}
		LEFT_PAREN {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			lhs_or 
		COMMA? {	emit($COMMA, DroolsEditorType.SYMBOL);	} 
		(	accumulate_init_clause
		|	accumulate_id_clause
		)
		RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
		-> ^(ACCUMULATE lhs_or accumulate_init_clause? accumulate_id_clause? RIGHT_PAREN)
	;


accumulate_init_clause
@init  { boolean isFailed = true;	}
@after { isFailed = false;	}
	:	init_key 
	{	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT);	}
		pc1=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] cm1=COMMA? {	emit($cm1, DroolsEditorType.SYMBOL);	} 
	{	if (pc1 != null && ((DroolsTree) pc1.getTree()).getText() != null) emit(Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION);	}
		action_key pc2=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] cm2=COMMA? {	emit($cm2, DroolsEditorType.SYMBOL);	} 
	{	if (pc1 != null && ((DroolsTree) pc1.getTree()).getText() != null && pc2 != null && ((DroolsTree) pc2.getTree()).getText() != null ) emit(Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE);	}
	(	reverse_key pc3=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] cm3=COMMA? {	emit($cm3, DroolsEditorType.SYMBOL);	} )?

	{	if ((pc1 != null && ((DroolsTree) pc1.tree).getText() != null) &&
            			(pc2 != null && ((DroolsTree) pc2.tree).getText() != null) &&
            			(pc3 != null && ((DroolsTree) pc3.tree).getText() != null)) {
			emit(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT);
		}	
	}
		res1=result_key {	emit($res1.start, DroolsEditorType.KEYWORD);	} pc4=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE]
	-> ^(VT_ACCUMULATE_INIT_CLAUSE ^(init_key $pc1) ^(action_key $pc2) ^(reverse_key $pc3)? ^(result_key $pc4))
	;
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

accumulate_paren_chunk[int locationType]
@init{
	String text = "";
}	:	pc=accumulate_paren_chunk_data[false,$locationType] {text = $pc.text;} 
	-> VT_PAREN_CHUNK[$pc.start,text]
	;

accumulate_paren_chunk_data[boolean isRecursive, int locationType]
	:	lp1=LEFT_PAREN
		{	if (!isRecursive) {
				emit($lp1, DroolsEditorType.SYMBOL);
				emit($locationType);
			} else {
				emit($lp1, DroolsEditorType.CODE_CHUNK);
			}	
		}
			(any=~ ( LEFT_PAREN | RIGHT_PAREN ) { emit($any, DroolsEditorType.CODE_CHUNK); } | accumulate_paren_chunk_data[true,-1] )* 
		rp1=RIGHT_PAREN
		{	if (!isRecursive) {
				emit($rp1, DroolsEditorType.SYMBOL);
			} else {
				emit($rp1, DroolsEditorType.CODE_CHUNK);
			}	
		}	
	;

accumulate_id_clause
	:	ID {	emit($ID, DroolsEditorType.IDENTIFIER);	}
		paren_chunk
	-> ^(VT_ACCUMULATE_ID_CLAUSE ID paren_chunk)
	;

collect_statement
	:	COLLECT {	emit($COLLECT, DroolsEditorType.KEYWORD);	}
	{	emit(Location.LOCATION_LHS_FROM_COLLECT);	}
		LEFT_PAREN {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}
			pattern_source 
		RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
	-> ^(COLLECT pattern_source RIGHT_PAREN)
	;

entrypoint_statement
	:	entry_point_key 
	{	emit(Location.LOCATION_LHS_FROM_COLLECT);	}
		entrypoint_id
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
	-> ^(entry_point_key entrypoint_id)
	;

entrypoint_id
	: 	value=ID {	emit($value, DroolsEditorType.IDENTIFIER);	}
		-> VT_ENTRYPOINT_ID[$value]
	| 	value=STRING {	emit($value, DroolsEditorType.IDENTIFIER);	}
		-> VT_ENTRYPOINT_ID[$value]
	;

from_source
	:	ID {	emit($ID, DroolsEditorType.IDENTIFIER);	}
		( (LEFT_PAREN)=> args=paren_chunk )?
		expression_chain?
	{	if ( input.LA(1) == EOF && input.get(input.index() - 1).getType() == WS) {
			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);
		} else if ( input.LA(1) != EOF ) {
			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);
		}	}
	->	^(VT_FROM_SOURCE ID paren_chunk? expression_chain?)
	;
	
expression_chain
	:
	 DOT {	emit($DOT, DroolsEditorType.IDENTIFIER);	} 
	 ID {	emit($ID, DroolsEditorType.IDENTIFIER);	}
	  (
	    {input.LA(1) == LEFT_PAREN}? paren_chunk
	    |
	    square_chunk
	  )?
	  expression_chain?
	  -> ^(VT_EXPRESSION_CHAIN[$DOT] ID square_chunk? paren_chunk? expression_chain?)
	;

lhs_pattern
	:	fact_binding -> ^(VT_PATTERN fact_binding)
	|	fact -> ^(VT_PATTERN fact)
	;

fact_binding
 	:	label
		( fact
 		| LEFT_PAREN {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}
 			fact_binding_expression 
 		  RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
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
@init  { boolean isFailedOnConstraints = true; pushParaphrases(DroolsParaphraseTypes.PATTERN); }
@after { paraphrases.pop();	}
	:	pattern_type 
		LEFT_PAREN {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
	{	emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	}
			constraints? 
		RIGHT_PAREN {	isFailedOnConstraints = false;	}
	{	if ($RIGHT_PAREN.text.equals(")") ){ //WORKAROUND FOR ANTLR BUG!
			emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);
			emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);
		}	}
	->	^(VT_FACT pattern_type constraints? RIGHT_PAREN)
	;
finally {
	if (isEditorInterfaceEnabled && isFailedOnConstraints && input.LA(1) == EOF && input.get(input.index() - 1).getType() == WS){
		if (!(getActiveSentence().getContent().getLast() instanceof Integer) && input.LA(-1) != COLON) {
			emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
		}
	}
}

constraints
	:	constraint ( COMMA! 
	{	emit($COMMA, DroolsEditorType.SYMBOL);
		emit(Location.LOCATION_LHS_INSIDE_CONDITION_START);	} constraint )*
	;

constraint
	:	or_constr
	;

or_constr
	:	and_constr ( DOUBLE_PIPE^ 
	{	emit($DOUBLE_PIPE, DroolsEditorType.SYMBOL);	} and_constr )* 
	;

and_constr
	:	unary_constr ( DOUBLE_AMPER^ 
	{	emit($DOUBLE_AMPER, DroolsEditorType.SYMBOL);;	} unary_constr )*
	;

unary_constr
options { k=2; }
@init { boolean isFailed = true;	}
@after { isFailed = false;	}
	:	eval_key^ paren_chunk
	|	field_constraint
	| 	LEFT_PAREN! {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}  
			or_constr 
		RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
	;
finally { 
	if (isEditorInterfaceEnabled && isFailed && input.LA(2) == EOF && input.LA(1) == ID) {
		emit(input.LT(1), DroolsEditorType.IDENTIFIER);
		input.consume();
		if (input.get(input.index() - 1).getType() == WS)
			emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
	}
}

field_constraint
@init{
	boolean isArrow = false;
}	:	label accessor_path 
		( or_restr_connective | arw=ARROW {	emit($ARROW, DroolsEditorType.SYMBOL);	} paren_chunk {isArrow = true;})?
		-> {isArrow}? ^(VT_BIND_FIELD label ^(VT_FIELD accessor_path)) ^(VK_EVAL[$arw] paren_chunk)?
		-> ^(VT_BIND_FIELD label ^(VT_FIELD accessor_path or_restr_connective?))
	|	accessor_path or_restr_connective
		-> ^(VT_FIELD accessor_path or_restr_connective)
	;

label
	:	value=ID {	emit($ID, DroolsEditorType.IDENTIFIER_VARIABLE);	} 
		COLON {	emit($COLON, DroolsEditorType.SYMBOL);	} 
		-> VT_LABEL[$value]
	;

or_restr_connective
	:	and_restr_connective ({(validateRestr())}?=> DOUBLE_PIPE^ 
	{	emit($DOUBLE_PIPE, DroolsEditorType.SYMBOL);	}  and_restr_connective )* 
	;
catch [ RecognitionException re ] {
	if (!lookaheadTest){
        reportError(re);
        recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
	} else {
		throw re;
	}
}

and_restr_connective
	:	constraint_expression ({(validateRestr())}?=> DOUBLE_AMPER^ 
	{	emit($DOUBLE_AMPER, DroolsEditorType.SYMBOL);	} constraint_expression )*
	;
catch [ RecognitionException re ] {
	if (!lookaheadTest){
        reportError(re);
        recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
	} else {
		throw re;
	}
}

constraint_expression
options{
k=3;
}	:	compound_operator
	|	simple_operator
	|	LEFT_PAREN! {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			or_restr_connective 
		RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	} 
	;
catch [ RecognitionException re ] {
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

simple_operator
@init {if ( state.backtracking==0 ) emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);}
	:	
	(
		EQUAL^ {	emit($EQUAL, DroolsEditorType.SYMBOL);	}
	|	GREATER^ {	emit($GREATER, DroolsEditorType.SYMBOL);	}
	|	GREATER_EQUAL^ {	emit($GREATER_EQUAL, DroolsEditorType.SYMBOL);	}
	|	LESS^ {	emit($LESS, DroolsEditorType.SYMBOL);	}
	|	LESS_EQUAL^ {	emit($LESS_EQUAL, DroolsEditorType.SYMBOL);	}
	|	NOT_EQUAL^ {	emit($NOT_EQUAL, DroolsEditorType.SYMBOL);	}
	|	not_key?
		(	operator_key^ square_chunk?	)
	)
	{	emit(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);	}
	expression_value
	;

//Simple Syntax Sugar
compound_operator 
@init { if ( state.backtracking==0 ) emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR); }
	:	
	( in_key^ | not_key in_key^ ) 
	{	emit(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);	}
		LEFT_PAREN! {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	}
			expression_value ( COMMA! {	emit($COMMA, DroolsEditorType.SYMBOL);	} expression_value )* 
		RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
	{	emit(Location.LOCATION_LHS_INSIDE_CONDITION_END);	}
	;
finally { 
	if (isEditorInterfaceEnabled && input.LA(2) == EOF && input.LA(1) == DOUBLE_PIPE) {
		emit(input.LT(1), DroolsEditorType.SYMBOL);
		input.consume();
		emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
	}	}

operator_key
	:      {(isPluggableEvaluator(false))}? => id=ID
	       { emit($id, DroolsEditorType.IDENTIFIER); }
	       -> VK_OPERATOR[$id]
	;

neg_operator_key
	:      {(isPluggableEvaluator(true))}? => id=ID 
	       { emit($id, DroolsEditorType.IDENTIFIER); } 
	       -> VK_OPERATOR[$id]
	;

expression_value
	:	(accessor_path
	|	literal_constraint 
	|	paren_chunk)
	{	if (isEditorInterfaceEnabled && !(input.LA(1) == EOF && input.get(input.index() - 1).getType() != WS))
			emit(Location.LOCATION_LHS_INSIDE_CONDITION_END);	}
	;
finally { 
	if (isEditorInterfaceEnabled && input.LA(2) == EOF) {
		if (input.LA(1) == DOUBLE_PIPE) {
			emit(input.LT(1), DroolsEditorType.SYMBOL);
			input.consume();
			emit(true, Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
		}
	}
}

literal_constraint
	:	STRING {	emit($STRING, DroolsEditorType.STRING_CONST);	}
	|	INT {	emit($INT, DroolsEditorType.NUMERIC_CONST);	}
	|	FLOAT {	emit($FLOAT, DroolsEditorType.NUMERIC_CONST);	}
	|	BOOL {	emit($BOOL, DroolsEditorType.BOOLEAN_CONST);	}
	|	NULL {	emit($NULL, DroolsEditorType.NULL_CONST);	}
	;

pattern_type
	:	id+=ID ( id+=DOT id+=ID )* 
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.PATTERN, buildStringFromTokens($id));	} 
	    dimension_definition*
		-> ^(VT_PATTERN_TYPE ID+ dimension_definition*)
	;

data_type
	:	id+=ID ( id+=DOT id+=ID )* dimension_definition*
	{	emit($id, DroolsEditorType.IDENTIFIER);	}
		-> ^(VT_DATA_TYPE ID+ dimension_definition*)
	;

dimension_definition
	:	LEFT_SQUARE {	emit($LEFT_SQUARE, DroolsEditorType.SYMBOL);	} 
		RIGHT_SQUARE {	emit($RIGHT_SQUARE, DroolsEditorType.SYMBOL);	} 
	;

accessor_path
	:	accessor_element ( DOT {	emit($DOT, DroolsEditorType.IDENTIFIER);	} accessor_element )*
	-> ^(VT_ACCESSOR_PATH accessor_element+)
	;

accessor_element
	:	ID {	emit($ID, DroolsEditorType.IDENTIFIER);	}
		square_chunk*
	-> ^(VT_ACCESSOR_ELEMENT ID square_chunk*)
	;

rhs_chunk
@init{
	String text = "";
}	:	rc=rhs_chunk_data {text = $rc.text;}
	-> VT_RHS_CHUNK[$rc.start,text]
	;

rhs_chunk_data
	:	THEN 
	{	if ($THEN.text.equalsIgnoreCase("then")){
			emit($THEN, DroolsEditorType.KEYWORD);
			emit(Location.LOCATION_RHS);
		}	}
			not_end_key* 
		end_key 
		SEMICOLON? {	emit($SEMICOLON, DroolsEditorType.KEYWORD);	}
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
				emit($lc1, DroolsEditorType.SYMBOL);
			} else {
				emit($lc1, DroolsEditorType.CODE_CHUNK);
			}	
		}
			(any=~ ( LEFT_CURLY | RIGHT_CURLY ) { emit($any, DroolsEditorType.CODE_CHUNK); } | curly_chunk_data[true] )* 
		rc1=RIGHT_CURLY
		{	if (!isRecursive) {
				emit($rc1, DroolsEditorType.SYMBOL);
			} else {
				emit($rc1, DroolsEditorType.CODE_CHUNK);
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
				emit($lp1, DroolsEditorType.SYMBOL);
			} else {
				emit($lp1, DroolsEditorType.CODE_CHUNK);
			}	
		}
			(any=~ ( LEFT_PAREN | RIGHT_PAREN ) { emit($any, DroolsEditorType.CODE_CHUNK); } | paren_chunk_data[true] )* 
		rp1=RIGHT_PAREN
		{	if (!isRecursive) {
				emit($rp1, DroolsEditorType.SYMBOL);
			} else {
				emit($rp1, DroolsEditorType.CODE_CHUNK);
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
				emit($ls1, DroolsEditorType.SYMBOL);
			} else {
				emit($ls1, DroolsEditorType.CODE_CHUNK);
			}	
		}
			(any=~ ( LEFT_SQUARE | RIGHT_SQUARE ) { emit($any, DroolsEditorType.CODE_CHUNK); }| square_chunk_data[true] )* 
		rs1=RIGHT_SQUARE
		{	if (!isRecursive) {
				emit($rs1, DroolsEditorType.SYMBOL);
			} else {
				emit($rs1, DroolsEditorType.CODE_CHUNK);
			}	
		}
	;

lock_on_active_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.LOCK) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.ON) && validateLT(4, "-") && validateLT(5, DroolsSoftKeywords.ACTIVE))}?=>  id1=ID mis1=MISC id2=ID mis2=MISC id3=ID {text = $text;}
	{	emit($id1, DroolsEditorType.KEYWORD);
		emit($mis1, DroolsEditorType.KEYWORD);
		emit($id2, DroolsEditorType.KEYWORD);
		emit($mis2, DroolsEditorType.KEYWORD);
		emit($id3, DroolsEditorType.KEYWORD);	}
		->	VK_LOCK_ON_ACTIVE[$start, text]
	;

date_effective_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EFFECTIVE))}?=>  id1=ID mis1=MISC id2=ID {text = $text;}
	{	emit($id1, DroolsEditorType.KEYWORD);
		emit($mis1, DroolsEditorType.KEYWORD);
		emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_DATE_EFFECTIVE[$start, text]
	;

date_expires_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.DATE) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.EXPIRES))}?=>  id1=ID mis1=MISC id2=ID {text = $text;}
	{	emit($id1, DroolsEditorType.KEYWORD);
		emit($mis1, DroolsEditorType.KEYWORD);
		emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_DATE_EXPIRES[$start, text]
	;

no_loop_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.NO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.LOOP))}?=>  id1=ID mis1=MISC id2=ID {text = $text;}
	{	emit($id1, DroolsEditorType.KEYWORD);
		emit($mis1, DroolsEditorType.KEYWORD);
		emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_NO_LOOP[$start, text]
	;

auto_focus_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.AUTO) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.FOCUS))}?=>  id1=ID mis1=MISC id2=ID {text = $text;}
	{	emit($id1, DroolsEditorType.KEYWORD);
		emit($mis1, DroolsEditorType.KEYWORD);
		emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_AUTO_FOCUS[$start, text]
	;

activation_group_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.ACTIVATION) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))}?=>  id1=ID mis1=MISC id2=ID {text = $text;}
	{	emit($id1, DroolsEditorType.KEYWORD);
		emit($mis1, DroolsEditorType.KEYWORD);
		emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_ACTIVATION_GROUP[$start, text]
	;

agenda_group_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.AGENDA) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))}?=>  id1=ID mis1=MISC id2=ID {text = $text;}
	{	emit($id1, DroolsEditorType.KEYWORD);
		emit($mis1, DroolsEditorType.KEYWORD);
		emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_AGENDA_GROUP[$start, text]
	;

ruleflow_group_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.RULEFLOW) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.GROUP))}?=>  id1=ID mis1=MISC id2=ID {text = $text;}
	{	emit($id1, DroolsEditorType.KEYWORD);
		emit($mis1, DroolsEditorType.KEYWORD);
		emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_RULEFLOW_GROUP[$start, text]
	;

entry_point_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(DroolsSoftKeywords.ENTRY) && validateLT(2, "-") && validateLT(3, DroolsSoftKeywords.POINT))}?=>  id1=ID mis1=MISC id2=ID {text = $text;}
	{	emit($id1, DroolsEditorType.KEYWORD);
		emit($mis1, DroolsEditorType.KEYWORD);
		emit($id2, DroolsEditorType.KEYWORD);	}
		->	VK_ENTRY_POINT[$start, text]
	;

duration_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.DURATION))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_DURATION[$id]
	;

package_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.PACKAGE))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_PACKAGE[$id]
	;

import_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.IMPORT))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_IMPORT[$id]
	;

dialect_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.DIALECT))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_DIALECT[$id]
	;

salience_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.SALIENCE))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_SALIENCE[$id]
	;

enabled_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.ENABLED))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_ENABLED[$id]
	;

attributes_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.ATTRIBUTES))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_ATTRIBUTES[$id]
	;

rule_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.RULE))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_RULE[$id]
	;

extend_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.EXTEND))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_EXTEND[$id]
	;

template_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.TEMPLATE))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_TEMPLATE[$id]
	;

query_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.QUERY))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_QUERY[$id]
	;

declare_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.DECLARE))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_DECLARE[$id]
	;

function_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.FUNCTION))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_FUNCTION[$id]
	;

global_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.GLOBAL))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_GLOBAL[$id]
	;

eval_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.EVAL))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_EVAL[$id]
	;

not_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.NOT))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_NOT[$id]
	;

in_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.IN))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_IN[$id]
	;

or_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.OR))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_OR[$id]
	;

and_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.AND))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_AND[$id]
	;

exists_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.EXISTS))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_EXISTS[$id]
	;

forall_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.FORALL))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_FORALL[$id]
	;

action_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.ACTION))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_ACTION[$id]
	;

reverse_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.REVERSE))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_REVERSE[$id]
	;

result_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.RESULT))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_RESULT[$id]
	;

end_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.END))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_END[$id]
	;

not_end_key
	:	{!(validateIdentifierKey(DroolsSoftKeywords.END))}?=>  any=.
	{	emit($any, DroolsEditorType.CODE_CHUNK);	}
	;

init_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.INIT))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_INIT[$id]
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
        
FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

INT	
	:	('-')?('0'..'9')+
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
	
MISC 	:
		'!' | '%' | '^' | '*' | '-' | '+'  | '?' | '/' | '\'' | '\\' | '|' | '&' | '$'
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
