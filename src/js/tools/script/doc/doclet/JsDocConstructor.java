package js.tools.script.doc.doclet;

import java.lang.reflect.Modifier;

import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Type;

public class JsDocConstructor extends JsDocExecutableMember implements ConstructorDoc
{
  /**
   * Create public js-doc constructor.
   *
   * @param className class unqualified name
   */
  public JsDocConstructor(String className)
  {
    this(className, Modifier.PUBLIC);
  }

  /**
   * Create js-doc constructor with specified modifiers.
   *
   * @param className class unqualified name
   * @param modifiers constructor modifiers
   */
  public JsDocConstructor(String className, int modifiers)
  {
    this.modifiers = modifiers;
    this.nodeName = className;
  }

  @Override
  public boolean isConstructor()
  {
    return true;
  }

  @Override
  public Type receiverType()
  {
    return null;
  }
}
