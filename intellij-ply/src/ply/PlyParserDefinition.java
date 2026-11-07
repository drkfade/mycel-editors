package ply;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
/** Flat PSI: the compiler (`ply check`) is the parser; the PSI only exists so leaf tokens can carry gutter markers. */
public final class PlyParserDefinition implements ParserDefinition {
 static final IFileElementType FILE=new IFileElementType(PlyLanguage.INSTANCE);
 @Override public @NotNull Lexer createLexer(Project p){return new PlyLexer();}
 @Override public @NotNull PsiParser createParser(Project p){return (IElementType root,PsiBuilder b)->{PsiBuilder.Marker m=b.mark();while(!b.eof())b.advanceLexer();m.done(root);return b.getTreeBuilt();};}
 @Override public @NotNull IFileElementType getFileNodeType(){return FILE;}
 @Override public @NotNull TokenSet getCommentTokens(){return PlyTokens.COMMENTS;}
 @Override public @NotNull TokenSet getStringLiteralElements(){return PlyTokens.STRINGS;}
 @Override public @NotNull TokenSet getWhitespaceTokens(){return TokenSet.create(TokenType.WHITE_SPACE);}
 @Override public @NotNull PsiElement createElement(ASTNode n){throw new UnsupportedOperationException(n.getElementType().toString());}
 @Override public @NotNull PsiFile createFile(@NotNull FileViewProvider vp){return new PlyFile(vp);}
}
