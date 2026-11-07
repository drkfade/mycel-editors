package ply;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/** Minimal JSON reader (no dependency on a bundled gson): objects → Map, arrays → List, numbers → Double. */
public final class PlyJson {
 private final String s; private int i;
 private PlyJson(String s){this.s=s;}
 public static Object parse(String text){PlyJson p=new PlyJson(text);p.ws();Object v=p.value();p.ws();if(p.i!=text.length())throw p.err("trailing data");return v;}
 private IllegalArgumentException err(String m){return new IllegalArgumentException(m+" at "+i);}
 private void ws(){while(i<s.length()&&Character.isWhitespace(s.charAt(i)))i++;}
 private Object value(){
  if(i>=s.length())throw err("unexpected end");
  char c=s.charAt(i);
  if(c=='{'){i++;Map<String,Object> m=new LinkedHashMap<>();ws();if(peek('}')){i++;return m;}
   while(true){ws();String k=str();ws();expect(':');ws();m.put(k,value());ws();if(peek(',')){i++;continue;}expect('}');return m;}}
  if(c=='['){i++;List<Object> l=new ArrayList<>();ws();if(peek(']')){i++;return l;}
   while(true){ws();l.add(value());ws();if(peek(',')){i++;continue;}expect(']');return l;}}
  if(c=='"')return str();
  if(s.startsWith("true",i)){i+=4;return Boolean.TRUE;} if(s.startsWith("false",i)){i+=5;return Boolean.FALSE;} if(s.startsWith("null",i)){i+=4;return null;}
  int st=i;while(i<s.length()&&"+-0123456789.eE".indexOf(s.charAt(i))>=0)i++;
  if(st==i)throw err("unexpected '"+c+"'");
  return Double.parseDouble(s.substring(st,i));
 }
 private boolean peek(char c){return i<s.length()&&s.charAt(i)==c;}
 private void expect(char c){if(!peek(c))throw err("expected '"+c+"'");i++;}
 private String str(){
  expect('"');StringBuilder b=new StringBuilder();
  while(true){
   if(i>=s.length())throw err("unterminated string");
   char c=s.charAt(i++);
   if(c=='"')return b.toString();
   if(c!='\\'){b.append(c);continue;}
   char e=s.charAt(i++);
   switch(e){case 'n':b.append('\n');break;case 't':b.append('\t');break;case 'r':b.append('\r');break;case 'b':b.append('\b');break;case 'f':b.append('\f');break;
    case 'u':b.append((char)Integer.parseInt(s.substring(i,i+4),16));i+=4;break;default:b.append(e);}
  }
 }
}
