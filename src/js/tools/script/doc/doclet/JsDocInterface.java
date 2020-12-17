package js.tools.script.doc.doclet;

import java.lang.reflect.Modifier;

import com.sun.javadoc.ClassDoc;

public class JsDocInterface extends JsDocClass
{
  /**
   * Construct a js-doc interface with specified modifiers. Note that Modifier.INTERFACE is automatically added.
   * 
   * @param className interface name as returned by {@link #name()} method
   * @param modifiers requested modifier to be applied beside Modifier.INTERFACE
   */
  public JsDocInterface(String className, int modifiers)
  {
    super(className, (modifiers |= Modifier.INTERFACE));
  }

  @Override
  public boolean isInterface()
  {
    return true;
  }

  @Override
  public boolean isAbstract()
  {
    return true;
  }

  @Override
  public boolean isClass()
  {
    return false;
  }

  @Override
  public boolean isOrdinaryClass()
  {
    return false;
  }

  /**
   * Return the superclass of this class. Because this doc object is an interface always returns null.
   * 
   * @return always return null.
   */
  @Override
  public ClassDoc superclass()
  {
    return null;
  }
}
