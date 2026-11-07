package ply;
import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;
/** Gutter run icon on `fn main` (the `main` identifier) and on `test "name"` (the `test` keyword). */
public final class PlyRunMarker extends RunLineMarkerContributor {
 enum Kind { NONE, MAIN, TEST }
 static Kind kind(PsiElement e){
  com.intellij.lang.ASTNode n=e.getNode(); if(n==null)return Kind.NONE;
  IElementType t=n.getElementType();
  if(t==PlyTokens.FUNC_DECL&&e.getText().equals("main"))return Kind.MAIN;
  if(t==PlyTokens.KEYWORD&&e.getText().equals("test"))return Kind.TEST;
  return Kind.NONE;
 }
 @Override public @Nullable Info getInfo(PsiElement e){
  Kind k=kind(e); if(k==Kind.NONE)return null;
  return new Info(k==Kind.TEST?AllIcons.RunConfigurations.TestState.Run:AllIcons.RunConfigurations.TestState.Run_run,ExecutorAction.getActions(0),x->k==Kind.TEST?"Run tests in this file (ply test)":"Run main (ply run)");
 }
}
