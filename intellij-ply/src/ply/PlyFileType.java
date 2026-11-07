package ply;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
public final class PlyFileType extends LanguageFileType {
 public static final PlyFileType INSTANCE = new PlyFileType();
 private PlyFileType() { super(PlyLanguage.INSTANCE); }
 @Override public @NotNull String getName(){return "Ply";}
 @Override public @NotNull String getDescription(){return "Mycel language";}
 /** The id stays "Ply" (plugin.xml + saved settings refer to it); people see "Mycel". */
 @Override public @NotNull String getDisplayName(){return "Mycel";}
 @Override public @NotNull String getDefaultExtension(){return "myc";}
 @Override public Icon getIcon(){return PlyIcons.FILE;}
}
