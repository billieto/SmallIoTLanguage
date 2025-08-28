grammar Sil;

program
    : statement+
    ;

statement
    : deviceDecl
    | varDecl
    | ifStmt
    | command
    ;

deviceDecl
    : 'device' ID 'at' STRING // flash uses port 80, thats the default so when i will change to MQTT i need to add port options, i think
    ;

varDecl
    : ID '=' (number | ask) // for sensors and tempetures/mezurements
    ;

ifStmt
    : 'if' condition block ('else' block)? // i should add else if in the future
    ;

condition
    : (number | ask | ID) comparator (number | ask | ID)
    ;

number
    : INT
    | FLOAT
    ;

comparator 
    : '>'
    | '<'
    | '=='
    ;

block
    : '{' command+ '}' ;

command
    : turn
    | print
    | ask
    ;

ask // like a function that returns a value
    : 'ask' ID RETURN_VALUE
    ;

print
    : 'print' ID STRING
    ;

turn
    : 'turn' ID SWITCHING GPIO
    ;

// LEXER

RETURN_VALUE
    : 'temperature'
    | 'pressure'
    | 'humidity'
    | 'orientation' // last
    ;

GPIO // I could add the string as the documentation of gpiozero has
    : '2'  | '3'  | '4'
    | '17' | '27' | '22'
    | '10' | '9'  | '11'
    | '5'  | '6'  | '13'
    | '19' | '26' | '14'
    | '15' | '18' | '23'
    | '24' | '25' | '8'
    | '7'  | '12' | '16'
    | '20' | '21'
    ; // i could also treat it as INT but have to know if the rasbperry support the pin

SWITCHING
: 'on'
| 'off'
;

ID
    : [a-zA-Z][a-zA-Z0-9]*
    ;

STRING 
    : '"' .*? '"'
    ;

FLOAT
    : [0-9]+ '.' [0-9]+
    | '.' [0-9]
    ;

INT
    : [0-9]+
    ;

WS
    : [ \t\r\n]+ -> skip
    ;

COMMENT
    : ';' ~[\r\n]* -> skip
    ;
