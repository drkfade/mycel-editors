package ply;
import com.intellij.execution.configurations.GeneralCommandLine;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
/** Locates lang/bin/ply and a project root, and makes the command line work from a Dock-launched IDE (thin PATH). */
public final class PlyPaths {
 private PlyPaths(){}
 /** Search order: explicit override, $PLY_BIN, lang/bin/ply (or bin/ply) above the file, an installed `mycel` on PATH, the build-time repo path (dev builds only). */
 public static String findPly(String override,String fromPath){
  if(override!=null&&!override.trim().isEmpty())return override.trim();
  String env=System.getenv("PLY_BIN"); if(env!=null&&new File(env).canExecute())return env;
  for(File d=fromPath==null?null:new File(fromPath).getAbsoluteFile().getParentFile();d!=null;d=d.getParentFile()){
   for(String n:new String[]{"mycel","ply"}){ // `mycel` runs programs; `ply` is the same CLI under the old name
    File a=new File(d,"lang/bin/"+n); if(a.canExecute())return a.getPath();
    File b=new File(d,"bin/"+n); if(b.canExecute()&&new File(d,"dist").isDirectory())return b.getPath();
   }
  }
  String onPath=onPath(System.getenv("PATH")); if(onPath!=null)return onPath;
  String baked=baked(); if(baked!=null&&new File(baked).canExecute())return baked;
  return null;
 }
 /** An installed `mycel` (or the older `ply` name): the IDE's own PATH is thin when launched from the Dock, so the usual install dirs are searched too. */
 static String onPath(String path){
  java.util.List<String> dirs=new java.util.ArrayList<>(java.util.List.of("/opt/homebrew/bin","/usr/local/bin",System.getProperty("user.home","")+"/.local/bin",System.getProperty("user.home","")+"/.mycel/bin"));
  if(path!=null)for(String d:path.split(File.pathSeparator))if(!d.isEmpty())dirs.add(d);
  for(String n:new String[]{"mycel","ply"})for(String d:dirs){ File f=new File(d,n); if(f.isFile()&&f.canExecute())return f.getPath(); }
  return null;
 }
 private static String baked(){
  try(InputStream in=PlyPaths.class.getResourceAsStream("/ply-default-path.txt")){ if(in==null)return null; return new String(in.readAllBytes(),StandardCharsets.UTF_8).trim(); }catch(Exception e){return null;}
 }
 /** Nearest directory at or above `path` holding ply.json, else the file's own directory. */
 public static String workingDir(String path){
  File f=new File(path).getAbsoluteFile(); File dir=f.isDirectory()?f:f.getParentFile();
  for(File d=dir;d!=null;d=d.getParentFile())if(new File(d,"ply.json").isFile())return d.getPath();
  return dir.getPath();
 }
 /** `ply` is `#!/bin/sh … exec node`; the IDE's own PATH usually lacks Homebrew / /usr/local. */
 public static void fixEnv(GeneralCommandLine cmd){
  String path=System.getenv("PATH"); if(path==null)path="/usr/bin:/bin";
  cmd.withEnvironment("PATH","/opt/homebrew/bin:/usr/local/bin:"+path);
 }
}
