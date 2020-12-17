package js.tools.script.doc;

import js.tools.commons.ast.AstHandler;

abstract class AbstractAstHandler extends AstHandler
{
  protected Context context;

  protected AbstractAstHandler(Context context)
  {
    this.context = context;
  }

  // couple helpers for testing naming conventions

  protected static boolean isClassName(String className)
  {
    int i = className.charAt(0) == '_' ? 1 : 0;
    return Character.isUpperCase(className.charAt(i));
  }

  protected static boolean isMemberName(String name)
  {
    char prefix = name.charAt(0);
    // allow pseudo-operators as member; a pseudo-operator starts with dollar
    int i = (prefix == '_' || prefix == '$') ? 1 : 0;
    return Character.isLowerCase(name.charAt(i));
  }

  protected static boolean isConstantName(String name)
  {
    if(name.isEmpty()) return false;
    for(int i = 0, l = name.length(); i < l; i++) {
      char c = name.charAt(i);
      if(c == '_') {
        continue;
      }
      if(Character.isUpperCase(c)) {
        continue;
      }
      if(Character.isDigit(c)) {
        continue;
      }
      return false;
    }
    return true;
  }

  private static final String ERROR = "Error";
  private static final String EXCEPTION = "Exception";

  protected static boolean isErrorClass(String className)
  {
    return className.endsWith(ERROR) || className.endsWith(EXCEPTION);
  }
}
