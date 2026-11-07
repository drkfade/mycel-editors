package ply;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
public final class PlyRunConfigurationType extends ConfigurationTypeBase {
 public static final String ID="PlyRunConfiguration";
 public PlyRunConfigurationType(){
  super(ID,"Mycel","Run or test a Mycel (.myc / .olx) file with lang/bin/mycel",PlyIcons.FILE);
  addFactory(new ConfigurationFactory(this){
   @Override public @NotNull String getId(){return "Ply";}
   @Override public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project p){return new PlyRunConfiguration(p,this,"Mycel");}
   @Override public Class<? extends BaseState> getOptionsClass(){return PlyRunOptions.class;}
  });
 }
}
