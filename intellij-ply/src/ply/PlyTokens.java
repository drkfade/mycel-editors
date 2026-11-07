package ply;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
/** Token types shared by the lexer, highlighter, brace matcher and run-marker contributor. */
public final class PlyTokens {
 private static IElementType t(String n){return new IElementType(n,PlyLanguage.INSTANCE);}
 public static final IElementType KEYWORD=t("KEYWORD"), TYPE=t("TYPE"), BUILTIN_TYPE=t("BUILTIN_TYPE"), IDENT=t("IDENT"), FUNC_DECL=t("FUNC_DECL"), FUNC_CALL=t("FUNC_CALL"),
  STRING=t("STRING"), CHAR=t("CHAR"), INTERP_START=t("INTERP_START"), INTERP_END=t("INTERP_END"),
  NUMBER=t("NUMBER"), LINE_COMMENT=t("LINE_COMMENT"), DOC_COMMENT=t("DOC_COMMENT"), BLOCK_COMMENT=t("BLOCK_COMMENT"),
  OPERATOR=t("OPERATOR"), LPAREN=t("LPAREN"), RPAREN=t("RPAREN"), LBRACKET=t("LBRACKET"), RBRACKET=t("RBRACKET"), LBRACE=t("LBRACE"), RBRACE=t("RBRACE"),
  COMMA=t("COMMA"), SEMICOLON=t("SEMICOLON"), DOT=t("DOT"), COLON=t("COLON"), BAD=t("BAD");
 public static final TokenSet COMMENTS=TokenSet.create(LINE_COMMENT,DOC_COMMENT,BLOCK_COMMENT);
 public static final TokenSet STRINGS=TokenSet.create(STRING,CHAR);
 private PlyTokens(){}
}
