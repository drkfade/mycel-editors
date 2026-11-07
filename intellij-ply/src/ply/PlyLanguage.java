package ply;
import com.intellij.lang.Language;
public final class PlyLanguage extends Language {
 public static final PlyLanguage INSTANCE = new PlyLanguage();
 private PlyLanguage() { super("Ply"); }
}
