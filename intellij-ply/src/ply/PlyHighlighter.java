package ply;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import static ply.PlyTokens.*;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
public final class PlyHighlighter extends SyntaxHighlighterBase {
 static final TextAttributesKey K_KEYWORD=createTextAttributesKey("PLY_KEYWORD",DefaultLanguageHighlighterColors.KEYWORD),
  K_VARIABLE=createTextAttributesKey("PLY_VARIABLE",new com.intellij.openapi.editor.markup.TextAttributes(new java.awt.Color(0x9cdcfe),null,null,null,java.awt.Font.PLAIN)),
  K_TYPE=createTextAttributesKey("PLY_TYPE",DefaultLanguageHighlighterColors.CLASS_NAME),
  K_BUILTIN=createTextAttributesKey("PLY_BUILTIN_TYPE",DefaultLanguageHighlighterColors.CLASS_REFERENCE),
  K_FUNC_DECL=createTextAttributesKey("PLY_FUNCTION_DECLARATION",DefaultLanguageHighlighterColors.FUNCTION_DECLARATION),
  K_FUNC_CALL=createTextAttributesKey("PLY_FUNCTION_CALL",DefaultLanguageHighlighterColors.FUNCTION_CALL),
  K_STRING=createTextAttributesKey("PLY_STRING",DefaultLanguageHighlighterColors.STRING),
  K_NUMBER=createTextAttributesKey("PLY_NUMBER",DefaultLanguageHighlighterColors.NUMBER),
  K_LINE=createTextAttributesKey("PLY_LINE_COMMENT",DefaultLanguageHighlighterColors.LINE_COMMENT),
  K_DOC=createTextAttributesKey("PLY_DOC_COMMENT",DefaultLanguageHighlighterColors.DOC_COMMENT),
  K_BLOCK=createTextAttributesKey("PLY_BLOCK_COMMENT",DefaultLanguageHighlighterColors.BLOCK_COMMENT),
  K_OP=createTextAttributesKey("PLY_OPERATOR",DefaultLanguageHighlighterColors.OPERATION_SIGN),
  K_PAREN=createTextAttributesKey("PLY_PARENTHESES",DefaultLanguageHighlighterColors.PARENTHESES),
  K_BRACKET=createTextAttributesKey("PLY_BRACKETS",DefaultLanguageHighlighterColors.BRACKETS),
  K_BRACE=createTextAttributesKey("PLY_BRACES",DefaultLanguageHighlighterColors.BRACES),
  K_COMMA=createTextAttributesKey("PLY_COMMA",DefaultLanguageHighlighterColors.COMMA),
  K_SEMI=createTextAttributesKey("PLY_SEMICOLON",DefaultLanguageHighlighterColors.SEMICOLON),
  K_DOT=createTextAttributesKey("PLY_DOT",DefaultLanguageHighlighterColors.DOT),
  K_INTERP=createTextAttributesKey("PLY_INTERPOLATION",DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR),
  K_BAD=createTextAttributesKey("PLY_BAD_CHARACTER",com.intellij.openapi.editor.HighlighterColors.BAD_CHARACTER);
 @Override public @NotNull Lexer getHighlightingLexer(){return new PlyLexer();}
 @Override public TextAttributesKey @NotNull [] getTokenHighlights(IElementType t){
  TextAttributesKey k=keyFor(t);
  return k==null?TextAttributesKey.EMPTY_ARRAY:pack(k);
 }
 static TextAttributesKey keyFor(IElementType t){
  if(t==IDENT)return K_VARIABLE; if(t==KEYWORD)return K_KEYWORD; if(t==TYPE)return K_TYPE; if(t==BUILTIN_TYPE)return K_BUILTIN; if(t==FUNC_DECL)return K_FUNC_DECL; if(t==FUNC_CALL)return K_FUNC_CALL;
  if(t==STRING||t==CHAR)return K_STRING; if(t==NUMBER)return K_NUMBER; if(t==LINE_COMMENT)return K_LINE; if(t==DOC_COMMENT)return K_DOC; if(t==BLOCK_COMMENT)return K_BLOCK;
  if(t==OPERATOR||t==COLON)return K_OP; if(t==LPAREN||t==RPAREN)return K_PAREN; if(t==LBRACKET||t==RBRACKET)return K_BRACKET; if(t==LBRACE||t==RBRACE)return K_BRACE;
  if(t==INTERP_START||t==INTERP_END)return K_INTERP; if(t==COMMA)return K_COMMA; if(t==SEMICOLON)return K_SEMI; if(t==DOT)return K_DOT; if(t==BAD)return K_BAD;
  return null;
 }
}
