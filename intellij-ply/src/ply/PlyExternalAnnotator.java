package ply;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public final class PlyExternalAnnotator extends ExternalAnnotator<PlyExternalAnnotator.Info,PlyExternalAnnotator.Result> {
 public static final class Info { final String path,text; final boolean dirty; Info(String p,String t,boolean d){path=p;text=t;dirty=d;} }
 public static final class Result { final List<PlyDiagnostic> diags; final String text; Result(List<PlyDiagnostic> d,String t){diags=d;text=t;} }
 @Override public @Nullable Info collectInformation(@NotNull PsiFile file,@NotNull Editor editor,boolean hasErrors){return info(file);}
 @Override public @Nullable Info collectInformation(@NotNull PsiFile file){return info(file);}
 private static Info info(PsiFile file){
  VirtualFile vf=file.getVirtualFile(); if(vf==null||!vf.isInLocalFileSystem())return null;
  Document doc=FileDocumentManager.getInstance().getCachedDocument(vf);
  return new Info(vf.getPath(),file.getText(),doc!=null&&FileDocumentManager.getInstance().isDocumentUnsaved(doc));
 }
 @Override public @Nullable Result doAnnotate(Info info){
  List<PlyDiagnostic> d=PlyChecker.check(info.path,info.text,info.dirty);
  return d==null?null:new Result(d,info.text);
 }
 @Override public void apply(@NotNull PsiFile file,Result r,@NotNull AnnotationHolder holder){
  if(r==null||!r.text.equals(file.getText()))return; // stale: a newer pass is coming
  Document doc=file.getViewProvider().getDocument(); if(doc==null)return;
  for(PlyDiagnostic d:r.diags){
   int a=offset(doc,d.line,d.col), b=offset(doc,d.endLine,d.endCol);
   if(b<=a)b=Math.min(doc.getTextLength(),a+1);
   if(a>=doc.getTextLength()&&a>0){a=doc.getTextLength()-1;b=doc.getTextLength();}
   if(b<=a)continue;
   holder.newAnnotation(d.isError()?HighlightSeverity.ERROR:HighlightSeverity.WARNING,d.shortText())
    .range(new TextRange(a,b)).tooltip(d.tooltip()).create();
  }
 }
 static int offset(Document doc,int line,int col){
  int l=Math.max(0,Math.min(doc.getLineCount()-1,line-1));
  int start=doc.getLineStartOffset(l),end=doc.getLineEndOffset(l);
  return Math.min(end,start+Math.max(0,col-1));
 }
}
