grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---
//Stylesheet
stylesheet: (variableAssignment| styleRule)* EOF;

//Variables
variableAssignment: variableReference ASSIGNMENT_OPERATOR expression SEMICOLON;
variableReference: CAPITAL_IDENT;

//Stylerule
styleRule: selector OPEN_BRACE (declaration | ifClause | variableAssignment)* CLOSE_BRACE;

//Selectors
selector: (tagSelector | classSelector | idSelector);
tagSelector: LOWER_IDENT;
classSelector: CLASS_IDENT;
idSelector: ID_IDENT;

//Property Name
propertyName: LOWER_IDENT;

//Declaration
declaration: propertyName COLON expression SEMICOLON;

//Expression
expression: literal | expression MUL expression | expression PLUS expression | expression MIN expression;

//Literals
literal: (pixelliteral | colorliteral | boolLiteral | scalarLiteral | percentageLiteral | variableReference);
colorliteral: COLOR;
pixelliteral: PIXELSIZE;
boolLiteral: TRUE | FALSE;
scalarLiteral: SCALAR;
percentageLiteral: PERCENTAGE;

//If Else
ifClause: IF BOX_BRACKET_OPEN (variableReference) BOX_BRACKET_CLOSE OPEN_BRACE
            (declaration | ifClause)*
            CLOSE_BRACE elseClause*;
elseClause: ELSE OPEN_BRACE (declaration | ifClause)* CLOSE_BRACE;

