package source;

import java_cup.runtime.Symbol;

%%

%{

	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn);
	}
	
	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn, value);
	}
	
%}

%cup
%line
%column

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" "		{}
"\b"	{}
"\t"	{}
"\r"	{}
"\n"	{}
"\r\n"	{}
"\f"	{}

"program" { return new_symbol(sym.PROGRAM, yytext()); }
"break" { return new_symbol(sym.BREAK, yytext()); }
"class" { return new_symbol(sym.CLASS, yytext()); }
"else" { return new_symbol(sym.ELSE, yytext()); }
"const" { return new_symbol(sym.CONST, yytext()); }
"if" { return new_symbol(sym.IF, yytext()); }
"new" { return new_symbol(sym.NEW, yytext()); }
"print" { return new_symbol(sym.PRINT, yytext()); }
"read" { return new_symbol(sym.READ, yytext()); }
"return" { return new_symbol(sym.RETURN, yytext()); }
"void" { return new_symbol(sym.VOID, yytext()); }
"for" { return new_symbol(sym.FOR, yytext()); }
"extends" { return new_symbol(sym.EXTENDS, yytext()); }
"continue" { return new_symbol(sym.CONTINUE, yytext()); }
"static" { return new_symbol(sym.STATIC, yytext()); }

"+" { return new_symbol(sym.PLUS, yytext()); }
"-" { return new_symbol(sym.MINUS, yytext()); }
"*" { return new_symbol(sym.MUL, yytext()); }
"/" { return new_symbol(sym.DIV, yytext()); }
"%" { return new_symbol(sym.MOD, yytext()); }
"==" { return new_symbol(sym.EQU, yytext()); }
"!=" { return new_symbol(sym.NOTEQU, yytext()); }
">" { return new_symbol(sym.GT, yytext()); }
">=" { return new_symbol(sym.GTEQU, yytext()); }
"<" { return new_symbol(sym.LE, yytext()); }
"<=" { return new_symbol(sym.LEQU, yytext()); }
"&&" { return new_symbol(sym.AND, yytext()); }
"||" { return new_symbol(sym.OR, yytext()); }
"=" { return new_symbol(sym.ASSIGN, yytext()); }
"+=" { return new_symbol(sym.PLUSASSIGN, yytext()); }
"-=" { return new_symbol(sym.MINUSASSIGN, yytext()); }
"*=" { return new_symbol(sym.MULASSIGN, yytext()); }
"/=" { return new_symbol(sym.DIVASSIGN, yytext()); }
"%=" { return new_symbol(sym.MODASSIGN, yytext()); }
"++" { return new_symbol(sym.INCREMENT, yytext()); }
"--" { return new_symbol(sym.DECREMENT, yytext()); }
";" { return new_symbol(sym.SEMICOLON, yytext()); }
"," { return new_symbol(sym.COMMA, yytext()); }
"." { return new_symbol(sym.DOT, yytext()); }
"(" { return new_symbol(sym.LPAREN, yytext()); }
")" { return new_symbol(sym.RPAREN, yytext()); }
"[" { return new_symbol(sym.LINDEX, yytext()); }
"]" { return new_symbol(sym.RINDEX, yytext()); }
"{" { return new_symbol(sym.LCURLYBRACE, yytext()); }
"}" { return new_symbol(sym.RCURLYBRACE, yytext()); }

"//"				{ yybegin(COMMENT); }
<COMMENT> .			{ yybegin(COMMENT); }
<COMMENT> "\n"		{ yybegin(YYINITIAL); }
<COMMENT> "\r\n"	{ yybegin(YYINITIAL); }

[0-9]+ { return new_symbol(sym.NUMCONST, new Integer(yytext())); }
("true" | "false") { return new_symbol(sym.BOOLCONST, Boolean.valueOf(yytext())); }
"'"."'" {return new_symbol(sym.CHARCONST, new Character(yytext().charAt(1))); }
([a-z]|[A-Z])[a-z|A-Z|0-9|_]* {return new_symbol(sym.IDENT, yytext()); }

. { System.err.println("Leksicka greska ("+yytext()+") u liniji "+(yyline+1)); }
