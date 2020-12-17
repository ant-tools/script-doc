package js.tools.script.doc.doclet;

import java.lang.reflect.Modifier;

import com.sun.javadoc.Type;

public class JsDocEnumConstant extends JsDocConstant
{
  JsDocEnumConstant(String name, Type type)
  {
    super(name, type, Modifier.PUBLIC);
  }

  public boolean isField()
  {
    return false;
  }

  @Override
  public boolean isEnum()
  {
    return true;
  }

  @Override
  public boolean isEnumConstant()
  {
    return true;
  }
}
