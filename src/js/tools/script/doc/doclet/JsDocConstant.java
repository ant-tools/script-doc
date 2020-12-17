package js.tools.script.doc.doclet;

import java.lang.reflect.Modifier;

import com.sun.javadoc.Type;

public class JsDocConstant extends JsDocField
{
  private Object value;

  JsDocConstant(String name, int modifiers)
  {
    super(name, modifiers |= Modifier.FINAL);
  }

  JsDocConstant(String name, Type type, int modifiers)
  {
    super(name, type, modifiers |= Modifier.FINAL);
  }

  public void setValue(Object value)
  {
    this.value = value;
  }

  @Override
  public Object constantValue()
  {
    return this.value;
  }

  @Override
  public String constantValueExpression()
  {
    return this.value != null ? this.value.toString() : null;
  }

  public boolean isField()
  {
    return true;
  }

  @Override
  public boolean isEnum()
  {
    return false;
  }

  @Override
  public boolean isEnumConstant()
  {
    return false;
  }
}
