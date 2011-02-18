tree grammar Tree2TestDRL;

options{
    tokenVocab=DRL;
    ASTLabelType=DroolsTree;
    TokenLabelType=DroolsToken;
}

@header {
    package org.drools.lang;
}

compilation_unit
    :	^(VT_COMPILATION_UNIT package_statement? statement*)
    ;

package_statement
    :	^(VK_PACKAGE package_id)
    ;

package_id
    :	^(VT_PACKAGE_ID ID+)
    ;

statement
    :	rule_attribute
    |	function_import_statement
    |	import_statement
    |	global
    |	function
    |	template
    |	rule
    |	query
    |	type_declaration
    ;

import_statement
    :	^(VK_IMPORT import_name)
    ;

function_import_statement
    :	^(VT_FUNCTION_IMPORT VK_FUNCTION import_name)
    ;

import_name
    :	^(VT_IMPORT_ID ID+ DOT_STAR?)
    ;

global
    :	^(VK_GLOBAL data_type VT_GLOBAL_ID)
    ;

function
    :	^(VK_FUNCTION data_type? VT_FUNCTION_ID parameters curly_chunk)
    ;

query
    :	^(VK_QUERY VT_QUERY_ID parameters? lhs_block VK_END)
    ;

parameters
    :	^(VT_PARAM_LIST param_definition*)
    ;

param_definition
    :	data_type? argument
    ;

argument
    :	ID dimension_definition*
    ;

type_declaration
    :	^(VK_DECLARE VT_TYPE_DECLARE_ID decl_metadata* decl_field* VK_END)
    ;

decl_metadata
    :	^(AT ID VT_PAREN_CHUNK?)
    ;

decl_field
    :	^(ID decl_field_initialization? data_type decl_metadata*)
    ;

decl_field_initialization
    :	^(EQUALS VT_PAREN_CHUNK)
    ;

template
    :	^(VK_TEMPLATE VT_TEMPLATE_ID template_slot+ VK_END)
    ;

template_slot
    :	^(VT_SLOT data_type VT_SLOT_ID)
    ;

rule
    :	^(VK_RULE VT_RULE_ID rule_attributes? when_part? VT_RHS_CHUNK)
    ;

when_part
    :	WHEN lhs_block
    ;

rule_attributes
    :	^(VT_RULE_ATTRIBUTES VK_ATTRIBUTES? rule_attribute+)
    ;

rule_attribute
    :	^(VK_SALIENCE (INT|VT_PAREN_CHUNK))
    |	^(VK_NO_LOOP BOOL?)
    |	^(VK_AGENDA_GROUP STRING)
    |	^(VK_DURATION INT)
    |	^(VK_ACTIVATION_GROUP STRING)
    |	^(VK_AUTO_FOCUS BOOL?)
    |	^(VK_DATE_EFFECTIVE STRING)
    |	^(VK_DATE_EXPIRES STRING)
    |	^(VK_ENABLED BOOL)
    |	^(VK_RULEFLOW_GROUP STRING)
    |	^(VK_LOCK_ON_ACTIVE BOOL?)
    |	^(VK_DIALECT STRING)
    ;

lhs_block
    :	^(VT_AND_IMPLICIT lhs*)
    ;

lhs	:	^(VT_OR_PREFIX lhs+)
    |	^(VT_OR_INFIX lhs lhs)
    |	^(VT_AND_PREFIX lhs+)
    |	^(VT_AND_INFIX lhs lhs)
    |	^(VK_EXISTS lhs)
    |	^(VK_NOT lhs)
    |	^(VK_EVAL VT_PAREN_CHUNK)
    |	^(VK_FORALL lhs+)
    |	^(FROM lhs_pattern from_elements)
    |	lhs_pattern
    ;

from_elements
    :	^(ACCUMULATE lhs (accumulate_init_clause|accumulate_id_clause))
    |	^(COLLECT lhs)
    |	^(VK_ENTRY_POINT VT_ENTRYPOINT_ID)
    |	^(VT_FROM_SOURCE ID VT_PAREN_CHUNK? expression_chain?)
    ;

accumulate_init_clause
    :	^(VT_ACCUMULATE_INIT_CLAUSE
            ^(VK_INIT VT_PAREN_CHUNK)
            ^(VK_ACTION VT_PAREN_CHUNK)
            accumulate_init_reverse_clause?
            ^(VK_RESULT VT_PAREN_CHUNK))
    ;

accumulate_init_reverse_clause
    :	^(VK_REVERSE VT_PAREN_CHUNK)
    ;


accumulate_id_clause
    :	^(VT_ACCUMULATE_ID_CLAUSE ID VT_PAREN_CHUNK)
    ;

lhs_pattern
    :	^(VT_PATTERN fact_expression) over_clause?
    ;

over_clause
    :	^(OVER over_element+)
    ;

over_element
    :	^(VT_BEHAVIOR ID ID VT_PAREN_CHUNK)
    ;

fact_expression
    :	^(DOUBLE_PIPE fact_expression fact_expression)
    |	^(DOUBLE_AMPER fact_expression fact_expression)
    |	^(VT_FACT_BINDING VT_LABEL fact_expression)
    |	^(VT_FACT pattern_type fact_expression*)
    |	^(VT_FACT_OR fact_expression fact_expression)
    |	^(VK_EVAL VT_PAREN_CHUNK)
    |	^(VK_IN VK_NOT? fact_expression+)
    |	^(EQUAL fact_expression)
    |	^(GREATER fact_expression)
    |	^(GREATER_EQUAL fact_expression)
    |	^(LESS fact_expression)
    |	^(LESS_EQUAL fact_expression)
    |	^(NOT_EQUAL fact_expression)
    |	^(VK_OPERATOR VK_NOT? VT_SQUARE_CHUNK? fact_expression)
    |	^(ID VK_NOT? VT_SQUARE_CHUNK? fact_expression)
    |	^(VT_BIND_FIELD VT_LABEL fact_expression)
    |	^(VT_FIELD fact_expression fact_expression?)
    |	^(VT_ACCESSOR_PATH accessor_element+)
    |	STRING
    |	INT
    |	FLOAT
    |	BOOL
    |	NULL
    |	VT_PAREN_CHUNK
    ;

pattern_type
    :	^(VT_PATTERN_TYPE ID+ dimension_definition*)
    ;

data_type
    :	^(VT_DATA_TYPE ID+ dimension_definition*)
    ;

dimension_definition
    :	LEFT_SQUARE RIGHT_SQUARE
    ;

accessor_element
    :	^(VT_ACCESSOR_ELEMENT ID VT_SQUARE_CHUNK*)
    ;

expression_chain
    :	^(VT_EXPRESSION_CHAIN ID VT_SQUARE_CHUNK? VT_PAREN_CHUNK? expression_chain?)
    ;

curly_chunk
    :	VT_CURLY_CHUNK
    ;
