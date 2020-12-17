package js.tools.script.doc.doclet;

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.SerialFieldTag;
import com.sun.javadoc.Type;

public class JsDocField extends JsDocMember implements FieldDoc
{
  protected Type type;

  /**
   * Construct a js-doc field of unknown type.
   * 
   * @param name unqualified field name
   * @param modifiers field modifiers as defined by {@linkplain java.lang.reflect.Modifier}
   */
  JsDocField(String name, int modifiers)
  {
    this(name, JsDocType.UNDEFINED, modifiers);
  }

  /**
   * Construct a js-doc field of given type.
   * 
   * @param name unqualified field name
   * @param type requested field type
   * @param modifiers field modifiers as defined by {@linkplain java.lang.reflect.Modifier}
   */
  JsDocField(String name, Type type, int modifiers)
  {
    this.nodeName = name;
    this.type = type;
    this.modifiers = modifiers;
  }

  public void setType(String typeName)
  {
    this.type = new JsDocType(typeName);
  }

  public void setType(Type type)
  {
    this.type = type;
  }

  @Override
  public Object constantValue()
  {
    return null;
  }

  @Override
  public String constantValueExpression()
  {
    return null;
  }

  @Override
  public boolean isTransient()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isVolatile()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SerialFieldTag[] serialFieldTags()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Type type()
  {
    return this.type;
  }

  public boolean isField()
  {
    return true;
  }
}
