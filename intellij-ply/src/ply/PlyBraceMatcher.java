package ply;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public final class PlyBraceMatcher implements PairedBraceMatcher {
 private static final BracePair[] PAIRS={new BracePair(PlyTokens.LPAREN,PlyTokens.RPAREN,false),new BracePair(PlyTokens.LBRACKET,PlyTokens.RBRACKET,false),new BracePair(PlyTokens.LBRACE,PlyTokens.RBRACE,true),new BracePair(PlyTokens.INTERP_START,PlyTokens.INTERP_END,false)};
 @Override public BracePair @NotNull [] getPairs(){return PAIRS;}
 @Override public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType l,@Nullable IElementType next){return true;}
 @Override public int getCodeConstructStart(PsiFile f,int openingBraceOffset){return openingBraceOffset;}
}
