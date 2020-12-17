package js.tools.script.doc.doclet;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

/**
 * Parameter information. This includes a parameter type and parameter name.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
final class JsDocParameter implements Parameter
{
  /**
   * Empty annotation description array.
   */
  private static final AnnotationDesc[] ANNOTATION_DESC = new AnnotationDesc[0];

  /**
   * Parameter name.
   */
  private String name;

  /**
   * Parameter type.
   */
  private Type type;

  /**
   * Construct parameter using given type and name.
   * 
   * @param typeName qualified type name,
   * @param name parameter name.
   */
  public JsDocParameter(String typeName, String name)
  {
    this.type = new JsDocType(typeName);
    this.name = name;
  }

  /**
   * Construct parameter of undefined type.
   * 
   * @param name parameter name.
   */
  public JsDocParameter(String name)
  {
    this.type = JsDocType.UNDEFINED;
    this.name = name;
  }

  @Override
  public AnnotationDesc[] annotations()
  {
    return JsDocParameter.ANNOTATION_DESC;
  }

  /**
   * Get this parameter name.
   * 
   * @return this parameter name.
   */
  @Override
  public String name()
  {
    return this.name;
  }

  /**
   * Get this parameter type.
   * 
   * @return this parameter type.
   */
  @Override
  public Type type()
  {
    return this.type;
  }

  /**
   * Lazily initialized full type name. It has value only after first call of {@link #typeName} method.
   */
  private String typeName;

  /**
   * Get type name of this parameter. For example if parameter is the short 'index', returns "short". This method returns a
   * complete string representation of the type, including the dimensions of arrays and the type arguments of parameterized
   * types. Names are qualified.
   * 
   * @return full type name.
   */
  @Override
  public String typeName()
  {
    if(this.typeName == null) {
      this.typeName = this.type.qualifiedTypeName() + this.type.dimension();
    }
    return this.typeName;
  }

  /**
   * Flat type name, that is, simple type name and dimension. Used by {@link JsDocExecutableMember#flatSignature()}.
   * 
   * @return flat type name.
   */
  public String flatTypeName()
  {
    return this.type.simpleTypeName() + this.type.dimension();
  }
}
