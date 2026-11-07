package ply;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
public final class PlyRunConfigurationEditor extends SettingsEditor<PlyRunConfiguration> {
 private final ComboBox<String> mode=new ComboBox<>(new String[]{"run","test"});
 private final TextFieldWithBrowseButton file=new TextFieldWithBrowseButton(), workDir=new TextFieldWithBrowseButton(), bin=new TextFieldWithBrowseButton();
 private final JBTextField flags=new JBTextField(), args=new JBTextField();
 public PlyRunConfigurationEditor(){
  file.addBrowseFolderListener("Mycel File",null,null,FileChooserDescriptorFactory.createSingleFileDescriptor().withFileFilter(v->"myc".equals(v.getExtension())||"olx".equals(v.getExtension())));
  workDir.addBrowseFolderListener("Working Directory",null,null,FileChooserDescriptorFactory.createSingleFolderDescriptor());
  bin.addBrowseFolderListener("Mycel Executable",null,null,FileChooserDescriptorFactory.createSingleFileDescriptor());
  flags.getEmptyText().setText("e.g. -A  or  --allow-read=data --max-seconds=10");
  args.getEmptyText().setText("passed after --  (run only)");
 }
 @Override protected void resetEditorFrom(@NotNull PlyRunConfiguration c){PlyRunOptions o=c.o();mode.setSelectedItem(o.getMode());file.setText(o.getFile());flags.setText(o.getPlyFlags());args.setText(o.getProgramArgs());workDir.setText(o.getWorkDir());bin.setText(o.getPlyBin());}
 @Override protected void applyEditorTo(@NotNull PlyRunConfiguration c) throws ConfigurationException {PlyRunOptions o=c.o();o.setMode((String)mode.getSelectedItem());o.setFile(file.getText().trim());o.setPlyFlags(flags.getText());o.setProgramArgs(args.getText());o.setWorkDir(workDir.getText());o.setPlyBin(bin.getText());}
 @Override protected @NotNull JComponent createEditor(){
  return FormBuilder.createFormBuilder().addLabeledComponent("Command:",mode).addLabeledComponent("File:",file).addLabeledComponent("Mycel options:",flags).addLabeledComponent("Program arguments:",args)
   .addLabeledComponent("Working directory:",workDir).addLabeledComponent("Mycel executable:",bin).addComponentFillVertically(new javax.swing.JPanel(),0).getPanel();
 }
}
