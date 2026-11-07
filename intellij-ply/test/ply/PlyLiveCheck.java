package ply;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
/** Runs the real lang/bin/ply through PlyChecker, and loads every class named in plugin.xml. */
public final class PlyLiveCheck {
 public static void main(String[] a) throws Exception {
  File f=File.createTempFile("plylive",".olx"); f.deleteOnExit();
  String src="fn main() {\n  let x: Int = \"a\"\n  print(y)\n}\n";
  Files.writeString(f.toPath(),src);
  for(boolean buf:new boolean[]{false,true}){
   List<PlyDiagnostic> d=PlyChecker.check(f.getPath(),src,buf);
   if(d==null)throw new AssertionError("check returned null (ply not runnable) buf="+buf);
   System.out.println("buffer="+buf+" -> "+d.size()+" diagnostics");
   for(PlyDiagnostic x:d)System.out.println("  "+x.severity+" "+x.code+" "+x.line+":"+x.col+"-"+x.endLine+":"+x.endCol+" "+x.shortText());
   if(d.size()<2||!d.get(0).isError())throw new AssertionError("expected >=2 diagnostics");
  }
  String xml=new String(PlyLiveCheck.class.getResourceAsStream("/META-INF/plugin.xml").readAllBytes());
  java.util.regex.Matcher m=java.util.regex.Pattern.compile("(?:implementationClass|implementation|className)=\"([^\"]+)\"").matcher(xml); int n=0;
  while(m.find()){ Class<?> c=Class.forName(m.group(1)); if(!m.group(1).equals("ply.PlyFileType"))c.getDeclaredConstructor().newInstance(); n++; }
  System.out.println("instantiated "+n+" plugin.xml classes");
  System.exit(0);
 }
}
