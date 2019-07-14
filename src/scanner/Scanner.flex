package scanner;

import java_cup.runtime.*;
import parser.sym;

%%

%class Lexer
%unicode
%type Symbol
%char
%line
%column
%public

%{
    StringBuilder string;
    StringBuilder character;

    private Symbol symbol(int type) {
		return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
    	return new Symbol(type, yyline, yycolumn, value);
    }
%}

%function scanFunction

Alph = [A-Za-z]
Digit = [0-9]

Id = ({Alph} | _) ({Alph} | {Digit} | _)*

DecInt = [-+]? ({Digit})+
// HEX IS REMOVED
//HexAlph = [A-Fa-f]
//Hex = 0 ("x" | "X") ({Digit} | {HexAlph})+ | 0
Float = {DecInt}.{DecInt}
SciFloat = (({DecInt} | {Float}) ("e" | "E") ("+" | "-")? {DecInt})

%state STRING
%state CHAR

InlineComment = "##" [^\r\n]*
MultilineComment = "/#" [^]* "#/"
Comment = {InlineComment} | {MultilineComment}

EOL = \n | \r | \r\n
WhiteSpace = {EOL} | [ \t\f]

%% // END OF OPTIONS AND DECLERATIONS

<YYINITIAL> {

    // Reserved keywords
    "bool"               { return symbol(sym.BOOL); }
    "const"              { return symbol(sym.CONST); }
    "break"              { return symbol(sym.BREAK); }
    "auto"               { return symbol(sym.AUTO); }
    "double"             { return symbol(sym.DOUBLE); }
    "int"                { return symbol(sym.INT); }
    //"struct"             { return symbol(parser.sym.STRUCT); }
    "float"              { return symbol(sym.FLOAT); }
    "else"               { return symbol(sym.ELSE); }
    "long"               { return symbol(sym.LONG); }
    "switch"             { return symbol(sym.SWITCH); }
    "continue"           { return symbol(sym.CONTINUE); }
    "for"                { return symbol(sym.FOR); }
    "void"               { return symbol(sym.VOID); }
    "case"               { return symbol(sym.CASE); }
    "default"            { return symbol(sym.DEFAULT); }
    "sizeof"             { return symbol(sym.SIZEOF); }
    "char"               { return symbol(sym.CHAR); }
    "extern"             { return symbol(sym.EXTERN); }
    "return"             { return symbol(sym.RETURN); }
    "if"                 { return symbol(sym.IF); }
    "repeat"             { return symbol(sym.REPEAT); }
    "until"              { return symbol(sym.UNTIL); }
    "of"                 { return symbol(sym.OF); }

    // NEW SHIT
    "begin"				 { return symbol(sym.BEGIN); }
    "false"				 { return symbol(sym.FALSE); }
    "true"				 { return symbol(sym.TRUE); }
    "function"			 { return symbol(sym.FUNCTION); }
    "record"			 { return symbol(sym.RECORD); }
    "string"			 { return symbol(sym.STRING); }
    "end"				 { return symbol(sym.END); }
    "null"               { return symbol(sym.NULL); }
    "NULL"               { return symbol(sym.NULL); }

    "=="				 { return symbol(sym.EQ); }
    "!="				 { return symbol(sym.NOTEQ); }
    "<="				 { return symbol(sym.LESSEQ); }
    "<"					 { return symbol(sym.LESS); }
    ">"					 { return symbol(sym.GR); }
    ">="				 { return symbol(sym.GREQ); }
	"="					 { return symbol(sym.ASSIGN); }
	"not"				 { return symbol(sym.NOT); }
	"~"					 { return symbol(sym.BITNEG); }
	"&"					 { return symbol(sym.ARITHAND); }
	"and"				 { return symbol(sym.LOGICAND); }
	"|"					 { return symbol(sym.ARITHOR); }
	"or"				 { return symbol(sym.LOGICOR); }
	"^"					 { return symbol(sym.XOR); }
	"*"					 { return symbol(sym.PROD); }
	"+"					 { return symbol(sym.ADD); }
	"+="				 { return symbol(sym.ADDASS); }
	"-"					 { return symbol(sym.MINUS); }
	"++"				 { return symbol(sym.INC); }
	"--"				 { return symbol(sym.DEC); }
	"-="				 { return symbol(sym.SUBASS); }
	"*="				 { return symbol(sym.MULTASS); }
	"/="				 { return symbol(sym.DIVASS); }
	"/"					 { return symbol(sym.DIV); }
	"%"					 { return symbol(sym.MOD); }
	"{"					 { return symbol(sym.LCURLY); }
	"}"					 { return symbol(sym.RCURLY); }
	"("					 { return symbol(sym.LPAREN); }
	")"					 { return symbol(sym.RPAREN); }
	"."					 { return symbol(sym.DOT); }
	","					 { return symbol(sym.COMMA); }
	":"					 { return symbol(sym.COLON); }
	";"					 { return symbol(sym.SEMICOLON); }
	"["					 { return symbol(sym.LBRACK); }
	"]"					 { return symbol(sym.RBRACK); }

    // Identifier
    {Id} { return symbol(sym.ID, new String(yytext())); }

    // Decimal Integer
    {DecInt} { return symbol(sym.INTCONST, new Integer(yytext())); }

    // Hexadecimal
    // HEX IS REMOVED
    //{Hex} { return symbol(parser.sym.REALCONST, new Double(yytext())); }

    // Float
    {Float} { return symbol(sym.REALCONST, new Double(yytext())); }
    // Scientific Float
    {SciFloat} { return symbol(sym.REALCONST, new Double(yytext())); }

    // Strings
    \" { string = new StringBuilder("\""); yybegin(STRING); }

    // Characters
    \' { character = new StringBuilder("\'"); yybegin(CHAR); }

    // Comment
    {Comment} {  }

    {WhiteSpace} { }
}

<YYINITIAL> {
    <<EOF>> { return symbol(sym.EOF); }
}

<STRING> {
    \" {
	    string.append("\"");
	    yybegin(YYINITIAL);
	    return symbol(sym.STRINGCONST, new String(string.toString()));
    }

    // Special Characters
    \\\' { string.append("\\\'"); }
    \\t { string.append("\\t"); }
    \\n { string.append("\\n"); }
    \\r { string.append("\\r"); }
    \\\" { string.append("\\\""); }
    \\ { string.append("\\"); }
    \\b { string.append("\\b"); }
    \\v { string.append("\\v"); }
    \\f { string.append("\\f"); }
    \\0 { string.append("\\0"); }

    [^\n\r\"\\]* { string.append( yytext() ); }
}

<CHAR> {
    ("\\\'" | "\\\"" | "\\t" | "\\n" | "\\r" | "\\b" | "\\v" | "\\f" | "\\0" | "\\" | "\\\\" | [^]?) \'  {
	    character.append(yytext());
	    yybegin(YYINITIAL);
	    return symbol(sym.CHARCONST, new Character(character.toString().charAt(1)));
	}
}

/* error fallback */
[^] { throw new Error("Illegal character <" + yytext() + ">"); }