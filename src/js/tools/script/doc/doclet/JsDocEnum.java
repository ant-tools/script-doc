package js.tools.script.doc.doclet;

import java.util.ArrayList;
import java.util.List;

import com.sun.javadoc.FieldDoc;

public class JsDocEnum extends JsDocClass
{
  private List<FieldDoc> enumConstants = new ArrayList<FieldDoc>();

  JsDocEnum(String className, int modifiers)
  {
    super(className, modifiers);
  }

  public JsDocEnumConstant createEnumConstant(String name)
  {
    JsDocEnumConstant e = new JsDocEnumConstant(name, JsDocType.NUMERIC);
    e.setContainingClass(this);
    this.enumConstants.add(e);
    return e;
  }

  @Override
  public FieldDoc[] enumConstants()
  {
    return this.enumConstants.toArray(new FieldDoc[this.enumConstants.size()]);
  }

  @Override
  public boolean isEnum()
  {
    return true;
  }

  @Override
  public boolean isOrdinaryClass()
  {
    return false;
  }
}
