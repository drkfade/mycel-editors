package ply;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
/** Runs `ply check --json` for one file. Returns null (no annotations, one log line) when the CLI could not be run. */
public final class PlyChecker {
 private static final Logger LOG=Logger.getInstance(PlyChecker.class);
 private PlyChecker(){}
 public static List<PlyDiagnostic> check(String realPath,String text,boolean useBuffer){
  String ply=PlyPaths.findPly(null,realPath);
  if(ply==null){LOG.info("Ply: lang/bin/ply not found for "+realPath);return null;}
  File real=new File(realPath),tmp=null;
  try{
   String target=realPath;
   if(useBuffer){ // unsaved edits: check a sibling copy so relative module paths still resolve
    tmp=new File(real.getParentFile(),".ply-ide-"+Long.toHexString(System.nanoTime())+".olx");
    Files.write(tmp.toPath(),text.getBytes(StandardCharsets.UTF_8)); target=tmp.getPath();
   }
   GeneralCommandLine cmd=new GeneralCommandLine(ply,"check","--json",target).withWorkDirectory(PlyPaths.workingDir(realPath)).withCharset(StandardCharsets.UTF_8);
   PlyPaths.fixEnv(cmd);
   ProcessOutput out=new CapturingProcessHandler(cmd).runProcess(20_000);
   if(out.isTimeout()){LOG.info("Ply: check timed out");return null;}
   String so=out.getStdout().trim();
   if(so.isEmpty())return out.getExitCode()==0?Collections.emptyList():null;
   try{
    List<PlyDiagnostic> all=PlyDiagnostic.parse(so);
    final String tp=target;
    // diagnostics in other files (imports) belong to those files
    return all.stream().filter(d->d.file.isEmpty()||d.file.equals(tp)||d.file.equals(realPath)).collect(Collectors.toList());
   }catch(IllegalArgumentException e){LOG.info("Ply: unreadable check output: "+e.getMessage()+" :: "+so.substring(0,Math.min(200,so.length())));return null;}
  }catch(Exception e){LOG.info("Ply: check failed",e);return null;}
  finally{ if(tmp!=null)tmp.delete(); }
 }
}
