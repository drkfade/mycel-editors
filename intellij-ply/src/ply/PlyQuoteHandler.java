package ply;
import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
public final class PlyQuoteHandler extends SimpleTokenSetQuoteHandler {
 public PlyQuoteHandler(){super(PlyTokens.STRING,PlyTokens.CHAR);}
}
