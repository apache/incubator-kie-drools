grammar RuleParser;

@members {
	private String packageName="";
	private RuleDescr rule;
}


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
	:	'package' id=ID { packageName = id.getText(); } ( '.' id=ID { packageName += "." + id.getText(); } )* ';'?	
	;
	
import_statement
	:	'import' java_package_or_class ';'?	
	;

use_expander
	:	'use' 'expander' ID ';'?
	;


rule
	:
		'rule' ruleName=word 
		{ rule = new RuleDescr( packageName + "." + ruleName, null ); }
		rule_options?
		'when' ':'?
		{ AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); }
			(l=lhs { rootLhs.addConfiguration( l ); } )*
		'then' ':'?
		{ rule.setRhs( null ); }
		'end' 
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
	
	
lhs returns [PatternDescr d]
	: l=lhs_or { d = l; }
	;

	
lhs_column returns [ColumnDesc d]
	:	fact_binding
	|	fact
	;
 	
fact_binding returns [PatternDescr d]
 	:
 		ID '=>' f=fact { d=f; }
 	;
 
fact returns [PatternDescr d] 
 	:	ID '(' constraints? ')'
 	;
 	
	
constraints returns [List constraints]
	:	c=constraint  { constraints = new ArrayList(); constraints.add( c ); }
		( ',' c=constraint { constraints.add( c ); } )*
	;
	
constraint returns [PatternDescr d]
	:	f=ID	op=(	'=='
			|	'>'
			|	'>='
			|	'<'
			|	'<='
			|	'!='
			)	(	lc=literal_constraint { d = new LiteralDescr( f.getText(), null, lc ); }
				|	rvc=retval_constraint { d = new ReturnValueDescr( f.getText(), null, rvc ); } )
	;
	
literal_constraint returns [String text]
	:	(	t=STRING
		|	t=INT
		|	t=FLOAT	
		) { text = t.getText(); }
	;
	
retval_constraint returns [String text]
	:
	;
	
field_binding
	:
	;
	
lhs_or returns [PatternDescr d]
	:	
		{ OrDescr or = null; }
		left=lhs_and {d = left; }
		( 	('or'|'||') 
			right=lhs_and 
			{
				if ( or == null ) {
					or = new OrDescr();
					or.addConfiguration( left );
					d = or;
				}
				
				or.addConfiguration( right );
			}
		)*
	;
	
lhs_and returns [PatternDescr d]
	:
		{ AndDescr and = null; }
		left=lhs_unary { d = left; }
		(	('and'|'&&') 
			right=lhs_unary 
			{
				if ( and == null ) {
					and = new AndDescr();
					and.addConfiguration( left );
					d = and;
				}
				
				and.addConfiguration( right );
			}
		)* 
	;
	
lhs_unary returns [PatternDescr d]
	:	(	u=lhs_exist
		|	u=lhs_not
		|	u=lhs_eval
		|	u=lhs_column
		|	'(' u=lhs ')'
		) { d = u; }
	;
	
lhs_exist returns [PatternDescr d]
	:	'exists' column=lhs_column { d = new ExistsDescr( column ); }	
	;
	
lhs_not	returns [NotDescr d]
	:	'not' column=lhs_column { d = new NotDescr( column ); }
	;

lhs_eval returns [PatternDescr d]
	:	'eval' { d = new EvalDescr( "" ); }
	;
	
java_package_or_class
	:	
		ID ( '.' ID )*
	;
	
	
word returns [String word]
	:	id=ID      { word=id.getText(); }
	|	'import'   { word="import"; }
	|	'use'      { word="use"; }
	|	'rule'     { word="rule"; }
	|	'salience' { word="salience"; }
 	|	'no-loop'  { word="no-loop"; }
	|	'when'     { word="when"; }
	|	'then'     { word="then"; }
	|	'end'      { word="end"; }
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
