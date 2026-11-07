package ply;
import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;
public final class PlyRunOptions extends RunConfigurationOptions {
 private final StoredProperty<String> file=string("").provideDelegate(this,"file");
 private final StoredProperty<String> mode=string("run").provideDelegate(this,"mode"); // run | test
 private final StoredProperty<String> plyFlags=string("").provideDelegate(this,"plyFlags");
 private final StoredProperty<String> programArgs=string("").provideDelegate(this,"programArgs");
 private final StoredProperty<String> workDir=string("").provideDelegate(this,"workDir");
 private final StoredProperty<String> plyBin=string("").provideDelegate(this,"plyBin");
 public String getFile(){return file.getValue(this);} public void setFile(String v){file.setValue(this,v);}
 public String getMode(){return mode.getValue(this);} public void setMode(String v){mode.setValue(this,v);}
 public String getPlyFlags(){return plyFlags.getValue(this);} public void setPlyFlags(String v){plyFlags.setValue(this,v);}
 public String getProgramArgs(){return programArgs.getValue(this);} public void setProgramArgs(String v){programArgs.setValue(this,v);}
 public String getWorkDir(){return workDir.getValue(this);} public void setWorkDir(String v){workDir.setValue(this,v);}
 public String getPlyBin(){return plyBin.getValue(this);} public void setPlyBin(String v){plyBin.setValue(this,v);}
}
