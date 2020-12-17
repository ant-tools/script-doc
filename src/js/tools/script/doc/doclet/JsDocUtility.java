package js.tools.script.doc.doclet;

import java.lang.reflect.Modifier;

/**
 * Utility class.
 *
 * @author Iulian Rotaru
 */
public class JsDocUtility extends JsDocClass
{
  public JsDocUtility(String className, int modifiers)
  {
    super(className, (modifiers |= Modifier.FINAL));
  }

  @Override
  public boolean isClass()
  {
    return false;
  }

  @Override
  public boolean isOrdinaryClass()
  {
    return true;
  }
}
