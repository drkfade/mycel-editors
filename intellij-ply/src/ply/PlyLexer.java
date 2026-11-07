package ply;
import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import java.util.ArrayDeque;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import static ply.PlyTokens.*;

/**
 * Lexer from lang/docs/SPEC.md §1. f-strings (f"..{x}..") are split around {...} so the interpolated code is lexed as code
 * ({{ and }} are literal braces; a plain "..." string never interpolates)
 * (nested strings and braces included). The lexer only reports state 0 between top-level tokens: the editor restarts
 * only at state-0 offsets, where the interpolation stack is empty, so a hash of the stack is not needed.
 */
public final class PlyLexer extends LexerBase {
 public static final Set<String> KEYWORDS=Set.of("let","var","fn","class","struct","interface","enum","type","extend","import","pub","private","protected","static","if","else","while","for","in","match","return","break","continue","throw","try","catch","finally","true","false","nil","is","as","async","await","spawn","gen","yield","init","self","super","override","operator","extern","then","and","or","not");
 public static final Set<String> BUILTIN_TYPES=Set.of("Int","Float","Bool","String","Char","Void","Never","Any","Set","Result","Task","Gen","Range","Error","Iterator","Comparable","Optional","Map","Array");
 private static final String[] OPS={"**=","<<=",">>=","..=","??=","|>","??","?.","=>","->","==","!=","<=",">=","&&","||","..","<<",">>","+=","-=","*=","/=","%=","**","+","-","*","/","%","=","<",">","!","~","&","|","^","?","@","#"};
 private CharSequence b; private int end,start,pos; private IElementType token;
 /** Interpolation frames: {stringKind, braceDepth}. */
 private final ArrayDeque<int[]> frames=new ArrayDeque<>();
 private int inStr; // 0 = code, 1 = in "…", 2 = in """…""", 3 = in f"…", 4 = in f"""…"""
 @Override public void start(@NotNull CharSequence buffer,int startOffset,int endOffset,int initialState){b=buffer;end=endOffset;pos=startOffset;frames.clear();inStr=0;advance();}
 @Override public int getState(){return frames.isEmpty()&&inStr==0?0:1;}
 @Override public IElementType getTokenType(){return token;}
 @Override public int getTokenStart(){return start;}
 @Override public int getTokenEnd(){return pos;}
 @Override public @NotNull CharSequence getBufferSequence(){return b;}
 @Override public int getBufferEnd(){return end;}
 private char at(int i){return i<end?b.charAt(i):'\0';}
 @Override public void advance(){
  start=pos;
  if(pos>=end){token=null;return;}
  if(inStr!=0){stringBody();return;}
  char c=b.charAt(pos++);
  if(Character.isWhitespace(c)){while(pos<end&&Character.isWhitespace(b.charAt(pos)))pos++;token=TokenType.WHITE_SPACE;return;}
  if(c=='/'&&at(pos)=='/'){boolean doc=at(pos+1)=='/'&&at(pos+2)!='/';while(pos<end&&b.charAt(pos)!='\n')pos++;token=doc?DOC_COMMENT:LINE_COMMENT;return;}
  if(c=='/'&&at(pos)=='*'){pos++;while(pos<end){if(b.charAt(pos++)=='*'&&at(pos)=='/'){pos++;break;}}token=BLOCK_COMMENT;return;}
  boolean fs=c=='f'&&at(pos)=='"'; if(fs)c=b.charAt(pos++);
  if(c=='"'){if(at(pos)=='"'&&at(pos+1)=='"'){pos+=2;inStr=2;}else inStr=1;if(fs)inStr+=2;stringBody();return;}
  if(c=='\''){while(pos<end){char d=b.charAt(pos++);if(d=='\\'&&pos<end){pos++;continue;}if(d=='\''||d=='\n'){if(d=='\n')pos--;break;}}token=CHAR;return;}
  if(c>='0'&&c<='9'){number(c);return;}
  if(Character.isUnicodeIdentifierStart(c)||c=='_'){ident(c);return;}
  switch(c){
   case '(':token=LPAREN;return; case ')':token=RPAREN;return; case '[':token=LBRACKET;return; case ']':token=RBRACKET;return;
   case '{': if(at(pos)==':'&&at(pos+1)=='}'){pos+=2;token=BAD;return;} if(!frames.isEmpty())frames.peek()[1]++; token=LBRACE;return;
   case '}': if(!frames.isEmpty()){int[] f=frames.peek(); if(f[1]==0){frames.pop();inStr=f[0];token=INTERP_END;return;} f[1]--;} token=RBRACE;return;
   case ',':token=COMMA;return; case ';':token=SEMICOLON;return; case ':':token=COLON;return;
   case '.': if(at(pos)!='.'){token=DOT;return;} break;
   case '#': if(at(pos)=='['){pos++;token=BAD;return;} break;
  }
  pos--;
  for(String op:OPS){if(startsWith(op)){pos+=op.length();token=OPERATOR;return;}}
  pos++;token=BAD;
 }
 private boolean startsWith(String s){if(pos+s.length()>end)return false;for(int i=0;i<s.length();i++)if(b.charAt(pos+i)!=s.charAt(i))return false;return true;}
 private void stringBody(){
  boolean triple=inStr==2||inStr==4, fmt=inStr>=3;
  while(pos<end){
   char d=b.charAt(pos);
   if(d=='\\'){pos=Math.min(end,pos+2);continue;}
   if(fmt&&(d=='{'&&at(pos+1)=='{'||d=='}'&&at(pos+1)=='}')){pos+=2;continue;}
   if(fmt&&d=='{'){
    if(pos>start){token=STRING;return;}
    pos+=1;frames.push(new int[]{inStr,0});inStr=0;token=INTERP_START;return;
   }
   if(d=='\n'&&!triple){inStr=0;token=STRING;return;} // unterminated "…": recover at end of line
   pos++;
   if(d=='"'&&(!triple||(at(pos)=='"'&&at(pos+1)=='"'))){if(triple)pos+=2;inStr=0;break;}
  }
  if(pos>=end)inStr=0;
  token=STRING;
 }
 private void number(char first){
  if(first=='0'&&(at(pos)=='x'||at(pos)=='X'||at(pos)=='b'||at(pos)=='B'||at(pos)=='o'||at(pos)=='O')){pos++;while(pos<end&&(Character.isLetterOrDigit(b.charAt(pos))||b.charAt(pos)=='_'))pos++;token=NUMBER;return;}
  digits();
  if(at(pos)=='.'&&Character.isDigit(at(pos+1))){pos++;digits();}
  if((at(pos)=='e'||at(pos)=='E')&&(Character.isDigit(at(pos+1))||((at(pos+1)=='+'||at(pos+1)=='-')&&Character.isDigit(at(pos+2))))){pos+=2;digits();}
  token=NUMBER;
 }
 private void digits(){while(pos<end&&(Character.isDigit(b.charAt(pos))||b.charAt(pos)=='_'))pos++;}
 private void ident(char c){
  while(pos<end&&(Character.isUnicodeIdentifierPart(b.charAt(pos))&&!Character.isIdentifierIgnorable(b.charAt(pos))||b.charAt(pos)=='_'))pos++;
  String s=b.subSequence(start,pos).toString();
  int j=pos;while(j<end&&(b.charAt(j)==' '||b.charAt(j)=='\t'))j++;
  char next=at(j);
  if(KEYWORDS.contains(s)){token=KEYWORD;return;}
  // contextual keywords
  if(s.equals("test")&&next=='"'&&precededByLineStart()){token=KEYWORD;return;}
  if((s.equals("get")||s.equals("set"))&&(Character.isLetter(next)||next=='_')&&precededByLineStart()){token=KEYWORD;return;}
  if(BUILTIN_TYPES.contains(s)){token=BUILTIN_TYPE;return;}
  if(Character.isUpperCase(c)){token=TYPE;return;}
  if(previousWord().equals("fn")){token=FUNC_DECL;return;}
  token=next=='('?FUNC_CALL:IDENT;
 }
 private boolean precededByLineStart(){int i=start-1;while(i>=0&&(b.charAt(i)==' '||b.charAt(i)=='\t'))i--;return i<0||b.charAt(i)=='\n'||b.charAt(i)=='{'||b.charAt(i)==';'||b.charAt(i)=='}'||i<0;}
 private String previousWord(){int i=start-1;while(i>=0&&(b.charAt(i)==' '||b.charAt(i)=='\t'))i--;int e=i+1;while(i>=0&&Character.isLetterOrDigit(b.charAt(i)))i--;return b.subSequence(i+1,e).toString();}
}
