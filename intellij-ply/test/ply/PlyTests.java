package ply;
import com.intellij.psi.tree.IElementType;
import java.util.ArrayList;
import java.util.List;
import static ply.PlyTokens.*;
/** Plain-main tests (no JUnit on the offline classpath). Run: scripts/build.sh test */
public final class PlyTests {
 static int fails=0, count=0;
 static String dump(String src){
  PlyLexer lx=new PlyLexer(); lx.start(src,0,src.length(),0); StringBuilder b=new StringBuilder();
  for(;lx.getTokenType()!=null;lx.advance()){ IElementType t=lx.getTokenType(); if(t==com.intellij.psi.TokenType.WHITE_SPACE)continue; b.append(t).append('[').append(src,lx.getTokenStart(),lx.getTokenEnd()).append("] "); }
  return b.toString().trim();
 }
 static void eq(String name,String want,String got){count++; if(!want.equals(got)){fails++;System.out.println("FAIL "+name+"\n  want: "+want+"\n  got:  "+got);}}
 static void ok(String name,boolean c,String got){count++; if(!c){fails++;System.out.println("FAIL "+name+": "+got);}}
 public static void main(String[] a) throws Exception {
  eq("keywords","KEYWORD[let] IDENT[x] OPERATOR[=] NUMBER[1]",dump("let x = 1"));
  eq("fn decl/call","KEYWORD[fn] FUNC_DECL[main] LPAREN[(] RPAREN[)] LBRACE[{] FUNC_CALL[print] LPAREN[(] STRING[\"hi\"] RPAREN[)] RBRACE[}]",dump("fn main() { print(\"hi\") }"));
  eq("types","KEYWORD[let] IDENT[a] COLON[:] BUILTIN_TYPE[Int] OPERATOR[?] OPERATOR[=] KEYWORD[nil] KEYWORD[let] IDENT[s] COLON[:] TYPE[Shape]",dump("let a: Int? = nil\nlet s: Shape"));
  eq("numbers","NUMBER[42] NUMBER[1_000] NUMBER[0xFF] NUMBER[0b101] NUMBER[0o17] NUMBER[3.14] NUMBER[1e9] NUMBER[2.5e-3]",dump("42 1_000 0xFF 0b101 0o17 3.14 1e9 2.5e-3"));
  eq("range not float","NUMBER[0] OPERATOR[..] NUMBER[3] NUMBER[1] OPERATOR[..=] NUMBER[2]",dump("0..3 1..=2"));
  eq("comments","LINE_COMMENT[// a] DOC_COMMENT[/// d] BLOCK_COMMENT[/* b\n c */] KEYWORD[let]",dump("// a\n/// d\n/* b\n c */ let"));
  eq("interpolation","STRING[f\"a ] INTERP_START[{] FUNC_CALL[f] LPAREN[(] IDENT[x] RPAREN[)] INTERP_END[}] STRING[ b\"]",dump("f\"a {f(x)} b\""));
  eq("nested braces+string in interp","STRING[f\"] INTERP_START[{] LBRACE[{] STRING[\"k\"] COLON[:] NUMBER[1] RBRACE[}] LBRACKET[[] STRING[\"k\"] RBRACKET[]] INTERP_END[}] STRING[\"] KEYWORD[let]",dump("f\"{ {\"k\": 1}[\"k\"]}\" let"));
 eq("escapes","STRING[\"a\\\"b\\$c\"]",dump("\"a\\\"b\\$c\""));
  eq("plain string and {{ }} never interpolate","STRING[\"{x} ${x}\"] STRING[f\"{{x}}\"]",dump("\"{x} ${x}\"f\"{{x}}\""));
  eq("triple","STRING[f\"\"\"a \"q\"\n ] INTERP_START[{] IDENT[x] INTERP_END[}] STRING[\n\"\"\"] KEYWORD[let]",dump("f\"\"\"a \"q\"\n {x}\n\"\"\" let"));
  eq("char","CHAR['c'] CHAR['\\n']",dump("'c' '\\n'"));
  eq("operators","OPERATOR[|>] OPERATOR[??] OPERATOR[?.] OPERATOR[=>] OPERATOR[->] OPERATOR[**=] OPERATOR[&&] OPERATOR[!=]",dump("|> ?? ?. => -> **= && !="));
  eq("contextual test/get","KEYWORD[test] STRING[\"n\"] LBRACE[{] RBRACE[}] KEYWORD[get] IDENT[area] COLON[:] BUILTIN_TYPE[Float]",dump("test \"n\" {}\nget area: Float"));
  eq("test as ident","IDENT[test] OPERATOR[=] NUMBER[1]",dump("test = 1"));
  eq("unterminated string recovers","STRING[\"abc] KEYWORD[let]",dump("\"abc\nlet"));
 eq("unicode ident","IDENT[naïve] FUNC_CALL[größe] LPAREN[(]",dump("naïve größe("));
  eq("destructuring swap","IDENT[a] COMMA[,] IDENT[b] OPERATOR[=] IDENT[b] COMMA[,] IDENT[a]",dump("a, b = b, a"));
  eq("in and not in","IDENT[x] KEYWORD[in] IDENT[items] IDENT[y] KEYWORD[not] KEYWORD[in] IDENT[items]",dump("x in items y not in items"));
  eq("comprehension","LBRACKET[[] IDENT[x] KEYWORD[for] IDENT[x] KEYWORD[in] IDENT[xs] KEYWORD[if] IDENT[c] RBRACKET[]]",dump("[x for x in xs if c]"));
  eq("if as value","KEYWORD[if] IDENT[c] LBRACE[{] IDENT[a] RBRACE[}] KEYWORD[else] LBRACE[{] IDENT[b] RBRACE[}]",dump("if c { a } else { b }"));
  eq("python ternary","IDENT[a] KEYWORD[if] IDENT[c] KEYWORD[else] IDENT[b]",dump("a if c else b"));
  eq("anon fn","KEYWORD[fn] LPAREN[(] IDENT[x] RPAREN[)] OPERATOR[=>] IDENT[x] OPERATOR[*] NUMBER[2]",dump("fn(x) => x * 2"));
  eq("set literal","BUILTIN_TYPE[Set] LPAREN[(] LBRACKET[[] NUMBER[1] COMMA[,] NUMBER[2] RBRACKET[]] RPAREN[)]",dump("Set([1, 2])"));
  eq("std web","KEYWORD[import] IDENT[std] DOT[.] IDENT[web]",dump("import std.web"));
  eq("bad hash set","BAD[#[] NUMBER[1] COMMA[,] NUMBER[2] RBRACKET[]]",dump("#[1, 2]"));
  eq("bad empty map","BAD[{:}]",dump("{:}"));
  // every spec keyword
  for(String k:PlyLexer.KEYWORDS) eq("kw "+k,"KEYWORD["+k+"]",dump(k));
  // lexer covers the buffer without gaps (restart safety)
  String big="fn main() {\n let s = f\"x {1 + 2} y\" // c\n for i in 0..3 { print(f\"{i}\") }\n}\n";
  PlyLexer lx=new PlyLexer(); lx.start(big,0,big.length(),0); int pos=0; boolean gap=false;
  for(;lx.getTokenType()!=null;lx.advance()){ if(lx.getTokenStart()!=pos)gap=true; pos=lx.getTokenEnd(); }
  ok("no gaps",!gap&&pos==big.length(),"pos="+pos);
  ok("variables have a dedicated colour", PlyHighlighter.keyFor(IDENT)==PlyHighlighter.K_VARIABLE, "IDENT has no variable colour");
  ok("all categories are configurable", new PlyColorSettingsPage().getAttributeDescriptors().length==20, "missing colour settings");
  // an installed `mycel` on PATH is found (the case for every Marketplace user)
  java.io.File tmp=java.nio.file.Files.createTempDirectory("mycelbin").toFile(); java.io.File fake=new java.io.File(tmp,"mycel"); java.nio.file.Files.writeString(fake.toPath(),"#!/bin/sh\n"); fake.setExecutable(true);
  ok("finds mycel on PATH", fake.getPath().equals(PlyPaths.onPath("/nonexistent"+java.io.File.pathSeparator+tmp.getPath())), "got "+PlyPaths.onPath("/nonexistent"+java.io.File.pathSeparator+tmp.getPath()));
  // diagnostics
  String json="[{\"severity\":\"error\",\"code\":\"E0352\",\"message\":\"cannot find `y` in this scope\",\"file\":\"/t.olx\",\"line\":4,\"col\":9,\"endLine\":4,\"endCol\":10,\"why\":\"no variable\",\"suggestion\":\"did you mean `x`?\"},"
   +"{\"severity\":\"warning\",\"code\":\"W0101\",\"message\":\"unused \\\"q\\\" \\u00e9\",\"file\":\"/t.olx\",\"line\":2,\"col\":7,\"endLine\":2,\"endCol\":8}]";
  List<PlyDiagnostic> d=PlyDiagnostic.parse(json);
  ok("diag count",d.size()==2,""+d.size());
  ok("diag fields",d.get(0).isError()&&d.get(0).line==4&&d.get(0).col==9&&d.get(0).endCol==10&&"did you mean `x`?".equals(d.get(0).suggestion)&&"no variable".equals(d.get(0).why),d.get(0).tooltip());
  ok("warning",!d.get(1).isError()&&d.get(1).message.equals("unused \"q\" é")&&d.get(1).suggestion==null,d.get(1).message);
  ok("tooltip",d.get(0).tooltip().contains("Fix:")&&d.get(0).tooltip().contains("E0352"),d.get(0).tooltip());
  eq("empty array","0",""+PlyDiagnostic.parse("[]\n").size());
  boolean threw=false; try{PlyDiagnostic.parse("ply: no file given");}catch(IllegalArgumentException e){threw=true;} ok("non-json rejected",threw,"no exception");
  threw=false; try{PlyDiagnostic.parse("{\"a\":1}");}catch(IllegalArgumentException e){threw=true;} ok("object rejected",threw,"no exception");
  eq("args split","[a, b c, d]",""+PlyRunConfiguration.split("a \"b c\"  d"));
  System.out.println((fails==0?"OK ":"FAILED ")+count+" checks, "+fails+" failed");
  System.exit(fails==0?0:1);
 }
}
