package ply;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/** One entry of `ply check --json` (columns and lines are 1-based; the end is exclusive). */
public final class PlyDiagnostic {
 public final String severity, code, message, file, why, suggestion;
 public final int line, col, endLine, endCol;
 PlyDiagnostic(String severity,String code,String message,String file,int line,int col,int endLine,int endCol,String why,String suggestion){
  this.severity=severity;this.code=code;this.message=message;this.file=file;this.line=line;this.col=col;this.endLine=endLine;this.endCol=endCol;this.why=why;this.suggestion=suggestion;}
 public boolean isError(){return !"warning".equals(severity);}
 /** Parses the CLI output; throws IllegalArgumentException if it is not the JSON array (so the caller can say so rather than guess). */
 public static List<PlyDiagnostic> parse(String json){
  Object v=PlyJson.parse(json.trim());
  if(!(v instanceof List))throw new IllegalArgumentException("expected a JSON array of diagnostics");
  List<PlyDiagnostic> out=new ArrayList<>();
  for(Object o:(List<?>)v){
   if(!(o instanceof Map))continue;
   Map<?,?> m=(Map<?,?>)o;
   int line=num(m.get("line"),1), col=num(m.get("col"),1);
   out.add(new PlyDiagnostic(str(m.get("severity"),"error"),str(m.get("code"),""),str(m.get("message"),""),str(m.get("file"),""),line,col,num(m.get("endLine"),line),num(m.get("endCol"),col),str(m.get("why"),null),str(m.get("suggestion"),null)));
  }
  return out;
 }
 private static String str(Object o,String d){return o instanceof String?(String)o:d;}
 private static int num(Object o,int d){return o instanceof Double?(int)Math.round((Double)o):d;}
 /** Text for the editor tooltip: message, then why, then the fix. */
 public String tooltip(){
  StringBuilder b=new StringBuilder(esc(message));
  if(!code.isEmpty())b.append(" <span style='color:gray'>[").append(esc(code)).append("]</span>");
  if(why!=null)b.append("<br/>").append(esc(why));
  if(suggestion!=null)b.append("<br/><b>Fix:</b> ").append(esc(suggestion));
  return b.toString();
 }
 /** Plain one-line form for the Problems view / status bar. */
 public String shortText(){return suggestion==null?message:message+" — "+suggestion;}
 private static String esc(String s){return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");}
}
