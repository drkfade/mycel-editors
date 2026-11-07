package ply;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.Icon;
import java.util.Map;

/** Exposes the complete Ply palette in Settings > Editor > Color Scheme. */
public final class PlyColorSettingsPage implements ColorSettingsPage {
 private static final AttributesDescriptor[] ATTRIBUTES = {
  new AttributesDescriptor("Keyword", PlyHighlighter.K_KEYWORD),
  new AttributesDescriptor("Variable and identifier", PlyHighlighter.K_VARIABLE),
  new AttributesDescriptor("Type", PlyHighlighter.K_TYPE),
  new AttributesDescriptor("Built-in type", PlyHighlighter.K_BUILTIN),
  new AttributesDescriptor("Function declaration", PlyHighlighter.K_FUNC_DECL),
  new AttributesDescriptor("Function call", PlyHighlighter.K_FUNC_CALL),
  new AttributesDescriptor("String and character", PlyHighlighter.K_STRING),
  new AttributesDescriptor("Number", PlyHighlighter.K_NUMBER),
  new AttributesDescriptor("Line comment", PlyHighlighter.K_LINE),
  new AttributesDescriptor("Documentation comment", PlyHighlighter.K_DOC),
  new AttributesDescriptor("Block comment", PlyHighlighter.K_BLOCK),
  new AttributesDescriptor("Operator", PlyHighlighter.K_OP),
  new AttributesDescriptor("Parentheses", PlyHighlighter.K_PAREN),
  new AttributesDescriptor("Brackets", PlyHighlighter.K_BRACKET),
  new AttributesDescriptor("Braces", PlyHighlighter.K_BRACE),
  new AttributesDescriptor("Comma", PlyHighlighter.K_COMMA),
  new AttributesDescriptor("Semicolon", PlyHighlighter.K_SEMI),
  new AttributesDescriptor("Dot", PlyHighlighter.K_DOT),
  new AttributesDescriptor("Interpolation", PlyHighlighter.K_INTERP),
  new AttributesDescriptor("Invalid character", PlyHighlighter.K_BAD)
 };
 @Override public @Nullable Icon getIcon(){return PlyIcons.FILE;}
 @Override public @NotNull SyntaxHighlighter getHighlighter(){return new PlyHighlighter();}
 @Override public @NotNull String getDemoText(){return "// A small Mycel example\nfn greet(name: String) {\n    let message = f\"Hello {name}!\"\n    print(message)\n}\n\nfn main() {\n    let count = 42\n    if count > 0 { greet(\"Mycel\") }\n}\n";}
 @Override public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap(){return null;}
 @Override public AttributesDescriptor @NotNull [] getAttributeDescriptors(){return ATTRIBUTES;}
 @Override public ColorDescriptor @NotNull [] getColorDescriptors(){return ColorDescriptor.EMPTY_ARRAY;}
 @Override public @NotNull String getDisplayName(){return "Mycel";}
}
