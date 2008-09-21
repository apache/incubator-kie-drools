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
	VK_ACTION;
	VK_REVERSE;
	VK_RESULT;
}

@parser::header {
	package org.drools.lang;
	
	import java.util.List;
	import java.util.LinkedList;
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
	
	/**
	 * This methos is a copy from ANTLR base class (BaseRecognizer). 
	 * We had to copy it just to remove a System.err.println() 
	 * 
	 */
	public void recoverFromMismatchedToken(IntStream input,
			RecognitionException e, int ttype, BitSet follow)
			throws RecognitionException {
		// if next token is what we are looking for then "delete" this token
		if (input.LA(2) == ttype) {
			reportError(e);
			/*
			 * System.err.println("recoverFromMismatchedToken deleting
			 * "+input.LT(1)+ " since "+input.LT(2)+" is what we want");
			 */
			beginResync();
			input.consume(); // simply delete extra token
			endResync();
			input.consume(); // move past ttype token as if all were ok
			return;
		}
		if (!recoverFromMismatchedElement(input, e, follow)) {
			throw e;
		}
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
			adaptor.addChild(root_1, stream_package_statement.next());
		}
		while (stream_statement.hasNext()) {
			adaptor.addChild(root_1, stream_statement.next());
		}
		adaptor.addChild(root_0, root_1);
		retval.stop = input.LT(-1);
		retval.tree = (Object) adaptor.rulePostProcessing(root_0);
		adaptor.setTokenBoundaries(retval.tree, retval.start,
				retval.stop);
	}
	if (isEditorInterfaceEnabled && hasErrors()) {
		DroolsTree rootNode = (DroolsTree) retval.tree;
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			DroolsTree childNode = (DroolsTree) rootNode.getChild(i);
			if (childNode.getStartCharOffset() >= errors.get(0).getOffset()) {
				rootNode.deleteChild(i);
			}
		}
	}
}

package_statement
@init  { pushParaphrases(DroolsParaphraseTypes.PACKAGE); }
@after { paraphrases.pop(); }
	:
	{	beginSentence(DroolsSentenceType.PACKAGE);	}
		package_key
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
}	:	
	{	beginSentence(DroolsSentenceType.RULE_ATTRIBUTE);	}
		rule_attribute
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
@init  { pushParaphrases(DroolsParaphraseTypes.IMPORT); }
@after { paraphrases.pop(); }
	:
	{	beginSentence(DroolsSentenceType.IMPORT_STATEMENT);	}
		import_key import_name[DroolsParaphraseTypes.IMPORT] SEMICOLON?
	{	emit($SEMICOLON, DroolsEditorType.SYMBOL);	}
		-> ^(import_key import_name)
	;

function_import_statement
@init  { pushParaphrases(DroolsParaphraseTypes.FUNCTION_IMPORT); }
@after { paraphrases.pop(); }
	:
	{	beginSentence(DroolsSentenceType.FUNCTION_IMPORT_STATEMENT);	}
		imp=import_key function_key import_name[DroolsParaphraseTypes.FUNCTION_IMPORT] SEMICOLON?
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
@init  { pushParaphrases(DroolsParaphraseTypes.GLOBAL); }
@after { paraphrases.pop(); }
	:
	{	beginSentence(DroolsSentenceType.GLOBAL);	}
		global_key data_type global_id SEMICOLON?
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
@init  { pushParaphrases(DroolsParaphraseTypes.FUNCTION); }
@after { paraphrases.pop(); }
	:
	{	beginSentence(DroolsSentenceType.FUNCTION);	}
		function_key data_type? function_id parameters curly_chunk
		-> ^(function_key data_type? function_id parameters curly_chunk)
	;

function_id
	:	id=ID
	{	emit($id, DroolsEditorType.IDENTIFIER);
		setParaphrasesValue(DroolsParaphraseTypes.FUNCTION, $id.text);	}
		-> VT_FUNCTION_ID[$id]
	;

query
@init  { pushParaphrases(DroolsParaphraseTypes.QUERY); }
@after { paraphrases.pop(); }
	:
	{	beginSentence(DroolsSentenceType.QUERY);	}
		query_key query_id 
	{	emit(Location.LOCATION_RULE_HEADER);	}
		parameters? 
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
		normal_lhs_block 
		END SEMICOLON?
	{	emit($END, DroolsEditorType.KEYWORD);
		emit($SEMICOLON, DroolsEditorType.SYMBOL);	}
		-> ^(query_key query_id parameters? normal_lhs_block END)
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
@init  { pushParaphrases(DroolsParaphraseTypes.TYPE_DECLARE); }
@after { paraphrases.pop(); }
	:
	{	beginSentence(DroolsSentenceType.TYPE_DECLARATION);	}
		declare_key  type_declare_id
		decl_metadata*
		decl_field*
		END
	{	emit($END, DroolsEditorType.KEYWORD);	}
		-> ^(declare_key type_declare_id decl_metadata* decl_field* END)
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
		paren_chunk
		-> ^(AT ID paren_chunk)
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
		END semi2=SEMICOLON?
	{	emit($END, DroolsEditorType.KEYWORD);
		emit($semi2, DroolsEditorType.SYMBOL);	}
		-> ^(template_key template_id template_slot+ END)
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
@init  { pushParaphrases(DroolsParaphraseTypes.RULE); }
@after { paraphrases.pop(); }
	:
	{	beginSentence(DroolsSentenceType.RULE);	}
		rule_key rule_id 
	{	emit(Location.LOCATION_RULE_HEADER);	}
		rule_attributes? when_part? rhs_chunk
		-> ^(rule_key rule_id rule_attributes? when_part? rhs_chunk)
	;

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
@after { paraphrases.pop(); isFailed = false; emit(Location.LOCATION_RULE_HEADER); }
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

date_effective
	:	date_effective_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	emit($STRING, DroolsEditorType.STRING_CONST );	}
	;

date_expires
	:	date_expires_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} STRING
	{	emit($STRING, DroolsEditorType.STRING_CONST );	}
	;
	
enabled
	:	enabled_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} BOOL
	{	emit($BOOL, DroolsEditorType.BOOLEAN_CONST );	}
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
	:	duration_key^ {	emit(Location.LOCATION_RULE_HEADER_KEYWORD);	} INT
	{	emit($INT, DroolsEditorType.NUMERIC_CONST );	}
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
	{	emit(Location.LOCATION_LHS_BEGIN_OF_CONDITION);	}
	{	String body = safeSubstring( $pc.text, 1, $pc.text.length()-1 );
		checkTrailingSemicolon( body, $ev.start );	}
		-> ^(eval_key paren_chunk)
	;

lhs_forall
	:	forall_key 
		LEFT_PAREN {	emit($LEFT_PAREN, DroolsEditorType.SYMBOL);	} 
			lhs_pattern+ 
		RIGHT_PAREN {	emit($RIGHT_PAREN, DroolsEditorType.SYMBOL);	}
		-> ^(forall_key lhs_pattern+ RIGHT_PAREN)
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
	:	INIT {	emit($INIT, DroolsEditorType.KEYWORD);	}
	{	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_INIT);	}
		pc1=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE] cm1=COMMA? {	emit($cm1, DroolsEditorType.SYMBOL);	} 
	{	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION);	}
		action_key pc2=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE] cm2=COMMA? {	emit($cm2, DroolsEditorType.SYMBOL);	} 
	{	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE);	}

	( reverse_key pc3=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE] cm3=COMMA? {	emit($cm3, DroolsEditorType.SYMBOL);	} )?
	{	emit(Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT);	}
		res1=result_key {	emit($res1.start, DroolsEditorType.KEYWORD);	} pc4=accumulate_paren_chunk[Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE]
	-> ^(VT_ACCUMULATE_INIT_CLAUSE ^(INIT $pc1) ^(action_key $pc2) ^(reverse_key $pc3)? ^(result_key $pc4))
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

and_restr_connective
	:	constraint_expression ({(validateRestr())}?=> DOUBLE_AMPER^ 
	{	emit($DOUBLE_AMPER, DroolsEditorType.SYMBOL);	} constraint_expression )*
	;

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
		recover(input, re);
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
	:	{	emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);	}
		(EQUAL^ {	emit($EQUAL, DroolsEditorType.SYMBOL);	}
	|	GREATER^ {	emit($GREATER, DroolsEditorType.SYMBOL);	}
	|	GREATER_EQUAL^ {	emit($GREATER_EQUAL, DroolsEditorType.SYMBOL);	}
	|	LESS^ {	emit($LESS, DroolsEditorType.SYMBOL);	}
	|	LESS_EQUAL^ {	emit($LESS_EQUAL, DroolsEditorType.SYMBOL);	}
	|	NOT_EQUAL^ {	emit($NOT_EQUAL, DroolsEditorType.SYMBOL);	}
	|	not_key 
		(	contains_key^
		|	soundslike_key^
		|	matches_key^
		|	memberof_key^
		|	id1=ID^ {	emit($id1, DroolsEditorType.IDENTIFIER);	}
		|	ga1=GRAVE_ACCENT!  {	emit($ga1, DroolsEditorType.SYMBOL);	} id2=ID^  {	emit($id2, DroolsEditorType.IDENTIFIER);	} square_chunk)
	|	contains_key^
	|	excludes_key^
	|	matches_key^
	|	soundslike_key^
	|	memberof_key^
	|	id3=ID^ {	emit($id3, DroolsEditorType.IDENTIFIER);	}
	|	ga2=GRAVE_ACCENT!  {	emit($ga2, DroolsEditorType.SYMBOL);	} id4=ID^  {	emit($id4, DroolsEditorType.IDENTIFIER);	} square_chunk)
	{	emit(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);	}
	expression_value
	;

//Simple Syntax Sugar
compound_operator 
	:	{	emit(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);	}
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
			( any=~END { emit($any, DroolsEditorType.CODE_CHUNK); } )* 
		end1=END {	emit($end1, DroolsEditorType.KEYWORD);	}
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

contains_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.CONTAINS))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_CONTAINS[$id]
	;

matches_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.MATCHES))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_MATCHES[$id]
	;

excludes_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.EXCLUDES))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_EXCLUDES[$id]
	;

soundslike_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.SOUNDSLIKE))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_SOUNDSLIKE[$id]
	;

memberof_key
	:	{(validateIdentifierKey(DroolsSoftKeywords.MEMBEROF))}?=>  id=ID
	{	emit($id, DroolsEditorType.KEYWORD);	}
		->	VK_MEMBEROF[$id]
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

ACCUMULATE
	:	'accumulate'
	;

COLLECT
	:	'collect'
	;

END	:	'end'
	;

FROM
	:	'from'
	;

INIT
	:	'init'
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
	|	'%' ('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')+ '%'
	{	text = $text.substring(1, $text.length() - 1);	}
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