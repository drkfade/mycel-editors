package ply;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
public final class PlyFile extends PsiFileBase {
 public PlyFile(@NotNull FileViewProvider vp){super(vp,PlyLanguage.INSTANCE);}
 @Override public @NotNull FileType getFileType(){return PlyFileType.INSTANCE;}
}
