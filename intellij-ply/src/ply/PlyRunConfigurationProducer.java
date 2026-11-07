package ply;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
public final class PlyRunConfigurationProducer extends LazyRunConfigurationProducer<PlyRunConfiguration> {
 @Override public @NotNull ConfigurationFactory getConfigurationFactory(){return com.intellij.execution.configurations.ConfigurationTypeUtil.findConfigurationType(PlyRunConfigurationType.class).getConfigurationFactories()[0];}
 private static String mode(PsiElement e){return PlyRunMarker.kind(e)==PlyRunMarker.Kind.TEST?"test":"run";}
 @Override protected boolean setupConfigurationFromContext(@NotNull PlyRunConfiguration c,@NotNull ConfigurationContext ctx,@NotNull Ref<PsiElement> source){
  PsiElement e=ctx.getPsiLocation(); if(e==null)return false;
  PsiFile f=e.getContainingFile(); if(!(f instanceof PlyFile)||f.getVirtualFile()==null)return false;
  c.o().setFile(f.getVirtualFile().getPath()); c.o().setMode(mode(e)); c.setName(c.suggestedName()==null?"Mycel":c.suggestedName());
  return true;
 }
 @Override public boolean isConfigurationFromContext(@NotNull PlyRunConfiguration c,@NotNull ConfigurationContext ctx){
  PsiElement e=ctx.getPsiLocation(); if(e==null)return false;
  PsiFile f=e.getContainingFile(); if(!(f instanceof PlyFile)||f.getVirtualFile()==null)return false;
  return f.getVirtualFile().getPath().equals(c.o().getFile())&&mode(e).equals(c.o().getMode());
 }
}
