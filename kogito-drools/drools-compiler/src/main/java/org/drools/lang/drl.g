grammar RuleParser;

compilation_unit
	:	prolog 
		rule*
	;
	
prolog
	:	package_statement?
		import_statement*
		use_expander?
	;
	
package_statement
	:	'package' ID ( '.' ID )* ';'?	
	;
	
import_statement
	:	'import' java_package_or_class ';'?	
	;

use_expander
	:	'use' 'expander' ID ';'?
	;


rule
	:	'rule' word
		rule_options?
		'when' ':'?
			lhs+
		'then' ':'?
		'end' 
	;
	
rule_name
	:
		ID
	;

rule_options
	:	'options' ':'?
			( salience | no_loop ) ( ','? ( salience | no_loop ) )*
	;
	
salience
	:	
		'salience' INT ';'?
	;
	
no_loop
	:
		'no-loop' ';'?
	;
	
	
lhs	
	: lhs_or
	;

	
lhs_column
	:	fact_binding
	|	fact
	;
 	
fact_binding
 	:
 		ID '=>' fact
 	;
 
fact
 	:	ID '(' constraints? ')'
 	;
 	
	
constraints
	:	constraint ( ',' constraint )*
	;
	
constraint
	:	ID	(	'=='
			|	'>'
			|	'>='
			|	'<'
			|	'<='
			|	'!='
			)	(	literal_constraint
				|	retval_constraint )
	;
	
literal_constraint
	:	STRING
	|	INT
	|	FLOAT	
	;
	
retval_constraint
	:
	;
	
field_binding
	:
	;
	
lhs_or
	:	lhs_and ( ('or'|'||') lhs_and )*
	;
	
lhs_and
	:
		lhs_unary ( ('and'|'&&') lhs_unary )* 
	;
	
lhs_unary
	:	lhs_exist
	|	lhs_not
	|	lhs_eval
	|	lhs_column
	|	'(' lhs ')'
	;
	
lhs_exist
	:	'exists' lhs_column	
	;
	
lhs_not	
	:	'not' lhs_column
	;

lhs_eval
	:	'eval'
	;
	
java_package_or_class
	:	
		ID ( '.' ID )*
	;
	
	
word
	:	ID
	|	'import'
	|	'use'
	|	'rule'
	|	'salience'
	|	'no-loop'
	|	'when'
	|	'then'
	|	'end'
	;


WS      :       (       ' '
                |       '\t'
                |       '\f'
                        // handle newlines
                |       (       '\r\n'  // Evil DOS
                        |       '\r'    // Macintosh
                        |       '\n'    // Unix (the right way)
                        )
                )+
                { channel=99;}
                
        ;
            

        
INT	
	:	('0'..'9')+
	;

FLOAT
	:	('0'..'9')+ '.' ('0'..'9')+
	;
	
STRING
	:	'"' ( options{greedy=false;} : .)* '"'
	;
	
ID	
	:	('a'..'z'|'A'..'Z')+ 
	;

SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* ('\r')? '\n' 
	;
        
        
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* ('\r')? '\n' 
	;

MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
	;
