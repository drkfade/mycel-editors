package ply;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public final class PlyRunConfiguration extends RunConfigurationBase<PlyRunOptions> {
 PlyRunConfiguration(Project p,ConfigurationFactory f,String name){super(p,f,name);}
 @Override protected @NotNull PlyRunOptions getOptions(){return (PlyRunOptions)super.getOptions();}
 public PlyRunOptions o(){return getOptions();}
 public boolean isTest(){return "test".equals(o().getMode());}
 @Override public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor(){return new PlyRunConfigurationEditor();}
 @Override public void checkConfiguration() throws RuntimeConfigurationException {
  String f=o().getFile();
  if(f==null||f.trim().isEmpty())throw new RuntimeConfigurationError("Choose a .myc or .olx file");
  if(!new java.io.File(f).isFile())throw new RuntimeConfigurationError("File not found: "+f);
  if(PlyPaths.findPly(o().getPlyBin(),f)==null)throw new RuntimeConfigurationError("lang/bin/mycel not found: set the Mycel executable");
 }
 public @Nullable String suggestedName(){String f=o().getFile();String n=f==null?"":new java.io.File(f).getName();return n.isEmpty()?null:(isTest()?"test ":"")+n;}
 /** Command line without a shell, so nothing like $FilePath$ is ever passed literally. */
 public GeneralCommandLine commandLine() throws ExecutionException {
  PlyRunOptions o=o(); String file=o.getFile();
  String ply=PlyPaths.findPly(o.getPlyBin(),file);
  if(ply==null)throw new ExecutionException("lang/bin/mycel not found");
  List<String> a=new ArrayList<>(); a.add(ply); a.add(isTest()?"test":"run"); a.add(file);
  a.addAll(split(o.getPlyFlags()));
  List<String> pa=split(o.getProgramArgs());
  if(!pa.isEmpty()&&!isTest()){a.add("--");a.addAll(pa);}
  String wd=o.getWorkDir()==null||o.getWorkDir().trim().isEmpty()?PlyPaths.workingDir(file):o.getWorkDir().trim();
  GeneralCommandLine cmd=new GeneralCommandLine(a).withWorkDirectory(wd).withCharset(StandardCharsets.UTF_8);
  PlyPaths.fixEnv(cmd);
  return cmd;
 }
 /** Whitespace split honouring "double quotes". */
 static List<String> split(String s){
  List<String> out=new ArrayList<>(); if(s==null)return out;
  StringBuilder b=new StringBuilder(); boolean q=false,has=false;
  for(char c:s.toCharArray()){
   if(c=='"'){q=!q;has=true;} else if(Character.isWhitespace(c)&&!q){if(has||b.length()>0){out.add(b.toString());b.setLength(0);has=false;}} else b.append(c);
  }
  if(has||b.length()>0)out.add(b.toString());
  return out;
 }
 @Override public @Nullable RunProfileState getState(@NotNull Executor ex,@NotNull ExecutionEnvironment env){
  return new CommandLineState(env){
   @Override protected @NotNull ProcessHandler startProcess() throws ExecutionException {
    ProcessHandler h=ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine());
    ProcessTerminatedListener.attach(h);
    return h;
   }
  };
 }
}
