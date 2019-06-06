/*
Here you can do your imports and codes that need to be added out side of your Scanner Class
*/

enum Type { Nothing, WhiteSpace, Reserved, Identifier, Integer, Real, String, Character, SpecialChar, Comment }

class Element {
	private String element;
    private String encodedElement;
    private String color;
    private Type type;

    public Element (String element, Type type) {
    	this.element = element;
    	this.type = type;
    }

    public String getEncodedElement() {
    	if (type == Type.WhiteSpace)
    		return element;

        encodedElement = "";
        encodedElement += "<span style=\"";
    	switch (type){
            case Reserved: {
                encodedElement += "color: blue; font-weight: bold;";
                break;
            }
            case Identifier: {
            	encodedElement += "color: orange;";
            	break;
            }
            case Integer:{
            	encodedElement += "color: violet;";
            	break;
            }
            case Real: {
            	encodedElement += "color: violet; font-style: italic;";
            	break;
            }
            case String:{
            	encodedElement += "color: red;";
            	break;
            }
            case Character:{
            	encodedElement += "color: green;";
            	break;
            }
            case SpecialChar:{
            	encodedElement += "color: green; font-style: italic;";
            	break;
            }
            case Comment:{
            	encodedElement += "color: gray;";
            	break;
            }
            case Nothing:{
            	encodedElement += "color: black;";
            	break;
            }
        }
        encodedElement += "\">";
    	if (type == Type.String) {
    		if (element.length() >= 3) {
                for (int i = 0; i < element.length(); i++) {
                    if (element.charAt(i) == '\\') {
                    	Element temp = new Element(String.valueOf(element.charAt(i)) + String.valueOf(element.charAt(i + 1)),
                    	                            Type.SpecialChar);
                    	encodedElement += temp.getEncodedElement();
                    	i++;
                    } else {
                    	encodedElement += String.valueOf(element.charAt(i));
                    }
                }
    		}
    	} else if (type == Type.Character) {
    		encodedElement += "\'";
    		if (element.charAt(1) == '\\') {
    			Element temp = new Element(String.valueOf(element.charAt(1)) + String.valueOf(element.charAt(2)),
                                    	    Type.SpecialChar);
    			encodedElement += temp.getEncodedElement();
    		} else {
    			encodedElement += String.valueOf(element.charAt(1));
    		}
    		encodedElement += "\'";
    	} else {
            encodedElement += element;
    	}
        encodedElement += "</span>";
        return encodedElement;
    }

    public String toString() {
        return element + ": " + type;
    }
}

%% // END OF USER CODE

%class CompilerScanner
%unicode
%char
%line
%column
%public

%{
//    Here you write the code that you want to include in your scanner class
    StringBuilder string;
    StringBuilder character;
%}

%function scanFunction

Alph = [A-Za-z]
Digit = [0-9]
HexAlph = [A-Fa-f]

Id = ({Alph} | _) ({Alph} | {Digit} | _)*

DecInt = ("-" | "+" | "") ({Digit})+
Hex = 0 ("x" | "X") ({Digit} | {HexAlph})+ | 0
Float = {DecInt}.{DecInt}
SciFloat = (({DecInt} | {Float}) ("e" | "E") ("+" | "-")? {DecInt})

%state STRING

%state CHAR

InlineComment = "//" [^\r\n]*
MultilineComment = "/*" [^]* "*/"
Comment = {InlineComment} | {MultilineComment}

EOL = \n | \r | \r\n
WhiteSpace = {EOL} | [ \t\f]

ArithmaticOperators = "+" | "-" | "*" | "/" | "++" | "--" | "%"
LogicalOperators = "<" | "<=" | ">=" | ">" | "==" | "!=" | "&&" | "||"
BitwiseOperators = "&" | "|" | "^" | "<<" | ">>" | "~"
Assignments = "=" | "+=" | "-=" | "*=" | "/=" | "%=" | "^=" | "&=" | "|=" | "<<=" | ">>="
SomeOtherElements = "{" | "}" | "(" | ")" | "," | ";"
Accessors = ".>" | "." | "[" | "]"
//Ternary =
Nothing = {ArithmaticOperators} | {LogicalOperators} | {BitwiseOperators} | {Assignments} | {Accessors} | {SomeOtherElements}

%type Element

%% // END OF OPTIONS AND DECLERATIONS

<YYINITIAL> {

    // Reserved keywords
    "boolean"            { return new Element("boolean", Type.Reserved); }
    "const"            { return new Element("const", Type.Reserved); }
    "break"              { return new Element("break", Type.Reserved); }
    "auto"               { return new Element("auto", Type.Reserved); }
    "double"             { return new Element("auto", Type.Reserved); }
    "int"                { return new Element("int", Type.Reserved); }
    "struct"             { return new Element("struct", Type.Reserved); }
    "float"              { return new Element("float", Type.Reserved); }
    "short"              { return new Element("short", Type.Reserved); }
    "unsigned"           { return new Element("unsigned", Type.Reserved); }
    "else"               { return new Element("else", Type.Reserved); }
    "long"               { return new Element("long", Type.Reserved); }
    "switch"             { return new Element("switch", Type.Reserved); }
    "continue"           { return new Element("continue", Type.Reserved); }
    "for"                { return new Element("for", Type.Reserved); }
    "signed"             { return new Element("signed", Type.Reserved); }
    "void"               { return new Element("void", Type.Reserved); }
    "case"               { return new Element("case", Type.Reserved); }
    "enum"               { return new Element("enum", Type.Reserved); }
    "register"           { return new Element("register", Type.Reserved); }
    "typedef"            { return new Element("typedef", Type.Reserved); }
    "default"            { return new Element("default", Type.Reserved); }
    "goto"               { return new Element("goto", Type.Reserved); }
    "sizeof"             { return new Element("sizeof", Type.Reserved); }
    "volatile"           { return new Element("volatile", Type.Reserved); }
    "char"               { return new Element("char", Type.Reserved); }
    "extern"             { return new Element("extern", Type.Reserved); }
    "return"             { return new Element("return", Type.Reserved); }
    "union"              { return new Element("union", Type.Reserved); }
    "do"                 { return new Element("do", Type.Reserved); }
    "if"                 { return new Element("if", Type.Reserved); }
    "static"             { return new Element("static", Type.Reserved); }
    "while"              { return new Element("while", Type.Reserved); }

    // Identifier
    {Id} { return new Element(yytext(), Type.Identifier); }

    // Decimal Integer
    {DecInt} { return new Element(yytext(), Type.Integer); }
    // Hexadecimal
    {Hex} { return new Element(yytext(), Type.Real); }
    // Float
    {Float} { return new Element(yytext(), Type.Real); }
    // Scientific Float
    {SciFloat} { return new Element(yytext(), Type.Real); }

    // Strings
    \" { string = new StringBuilder("\""); yybegin(STRING); }

    // Characters
    \' { character = new StringBuilder("\'"); yybegin(CHAR); }

    // Comment
    {Comment} { return new Element(yytext(), Type.Comment); }

    // Everything else that is important
    {Nothing} { return new Element(yytext(), Type.Nothing); }

    {WhiteSpace} { return new Element(yytext(), Type.WhiteSpace); }
}

<YYINITIAL> {

    // Special Characters
    \\\' { return new Element("\\\'", Type.SpecialChar); }
    \\t { return new Element("\\t", Type.SpecialChar); }
    \\n { return new Element("\\n", Type.SpecialChar); }
    \\r { return new Element("\\r", Type.SpecialChar); }
    \\\" { return new Element("\\\"", Type.SpecialChar); }
    \\\\ { return new Element("\\\\", Type.SpecialChar); }
    \\b { return new Element("\\b", Type.SpecialChar); }
    \\v { return new Element("\\v", Type.SpecialChar); }
    \\f { return new Element("\\f", Type.SpecialChar); }
//    \\? { return new Element("\\?", Type.SpecialChar); }
    \\0 { return new Element("\\0", Type.SpecialChar); }
}

<STRING> {
    \" {
	    string.append("\"");
	    yybegin(YYINITIAL);
	    return new Element(string.toString(), Type.String);
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
	    return new Element(character.toString(), Type.Character);
	}
}

/* error fallback */
[^] { throw new Error("Illegal character <" + yytext() + ">"); }