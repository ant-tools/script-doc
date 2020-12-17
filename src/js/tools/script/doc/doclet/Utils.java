package js.tools.script.doc.doclet;

import java.lang.reflect.Modifier;

final class Utils
{
  /**
   * Divide text at first white space. Split given string into two components at first white space encountered. If no white
   * space found all given text is stored in first item of returned array.
   * 
   * @param text text to be divided
   * @return string array with 2 items.
   */
  static String[] divideAtWhitespace(String text)
  {
    String[] a = new String[2];
    for(int i = 0, l = text.length(); i < l; i++) {
      char c = text.charAt(i);
      if(Character.isWhitespace(c)) {
        a[0] = text.substring(0, i);
        for(; i < l; ++i) {
          c = text.charAt(i);
          if(!Character.isWhitespace(c)) {
            a[1] = text.substring(i, l);
            return a;
          }
        }
        break;
      }
    }
    if(a[0] == null) a[0] = text;
    return a;
  }

  public static String getPackageName(String qualifiedClassName)
  {
    if(qualifiedClassName == null) return null;
    int i = qualifiedClassName.lastIndexOf('.');
    if(i == -1) return null;
    return qualifiedClassName.substring(0, i);
  }

  public static String getClassName(String qualifiedClassName)
  {
    if(qualifiedClassName == null) return null;
    return qualifiedClassName.substring(qualifiedClassName.lastIndexOf('.') + 1);
  }

  public static int modifiers(String name)
  {
    return name.charAt(0) == '_' ? Modifier.PRIVATE : Modifier.PUBLIC;
  }
}
