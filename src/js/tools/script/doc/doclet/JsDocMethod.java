package js.tools.script.doc.doclet;

import java.lang.reflect.Modifier;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

public class JsDocMethod extends JsDocExecutableMember implements MethodDoc
{
  private Type returnType = JsDocType.VOID;
  private MethodDoc overriddenMethod;

  /**
   * Construct js-doc method.
   * 
   * @param modifiers method modifiers
   * @param name method unqualified name
   */
  public JsDocMethod(String name, int modifiers)
  {
    this.modifiers = modifiers;
    this.nodeName = name;
  }

  public void setReturnType(String typeName)
  {
    this.returnType = typeName != null ? new JsDocType(typeName) : JsDocType.VOID;
  }

  public void setOverriddenMethod(MethodDoc overriddenMethod)
  {
    this.overriddenMethod = overriddenMethod;
  }

  @Override
  public boolean isAbstract()
  {
    return Modifier.isAbstract(this.modifiers);
  }

  @Override
  public ClassDoc overriddenClass()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public MethodDoc overriddenMethod()
  {
    return this.overriddenMethod;
  }

  @Override
  public Type overriddenType()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean overrides(MethodDoc method)
  {
    return false;
  }

  @Override
  public Type returnType()
  {
    return this.returnType;
  }

  @Override
  public boolean isMethod()
  {
    return true;
  }

  @Override
  public Type receiverType()
  {
    return null;
  }

  @Override
  public boolean isDefault()
  {
    return false;
  }
}
