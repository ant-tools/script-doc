package js.tools.script.doc.doclet;

import com.sun.javadoc.AnnotatedType;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

/**
 * Represents a type. A type can be a class or interface, an invocation of a generic class or interface, a type
 * variable, a wildcard type ("?"), or a primitive data type (like char).
 * 
 * @author Iulian Rotaru
 */
class JsDocType implements Type
{
  static final Type UNDEFINED = new JsDocType("undefined", true);
  static final Type NUMERIC = new JsDocType("Number", true);
  static final Type VOID = new JsDocType("void", true);

  private boolean isPrimitive;
  private ClassDoc classDoc;
  private String qualifiedName;
  private String simpleName;
  private String dimension;

  /**
   * Construct a js-doc type from given full type name. It tries to initialize both qualified and a simple names and
   * dimension.
   * <p>
   * A full name has all components: package, class and dimension. So that an array of strings is represented as
   * <em>java.lang.String[]</em>. If dimension is missing just ignore it. If package is missing, i.e. the name is not
   * qualified uses JsDocContext#GLOBAL_SCOPE instead.
   * 
   * @param typeName qualified type name
   */
  public JsDocType(String typeName)
  {
    if(typeName == null) throw new IllegalArgumentException();
    this.isPrimitive = false;

    int dimensionIndex = typeName.indexOf('[');
    if(dimensionIndex != -1) {
      this.qualifiedName = typeName.substring(0, dimensionIndex);
      this.dimension = typeName.substring(dimensionIndex);
    }
    else if(typeName.endsWith(CT.ELLIPSIS)) {
      this.qualifiedName = typeName.substring(0, typeName.length() - 3);
      this.dimension = CT.ELLIPSIS;
    }
    else {
      this.qualifiedName = typeName;
      this.dimension = CT.EMPTY;
    }
    this.simpleName = this.qualifiedName.substring(this.qualifiedName.lastIndexOf('.') + 1);
  }

  /**
   * Private primitive types constructor.
   * 
   * @param typeName primitive type name,
   * @param isPrimitive mark flag for primitive values, always true.
   */
  private JsDocType(String typeName, boolean isPrimitive)
  {
    if(typeName == null) throw new IllegalArgumentException();
    if(!isPrimitive) throw new IllegalArgumentException();

    this.isPrimitive = true;
    this.qualifiedName = typeName;
    this.simpleName = typeName;
    this.dimension = CT.EMPTY;
  }

  @Override
  public AnnotationTypeDoc asAnnotationTypeDoc()
  {
    return null;
  }

  @Override
  public ClassDoc asClassDoc()
  {
    if(this.classDoc == null && !this.isPrimitive) {
      this.classDoc = JsDocRoot.getInstance().getLazyClass(JsDocClass.class, this.qualifiedName);
    }
    return this.classDoc;
  }

  @Override
  public ParameterizedType asParameterizedType()
  {
    return null;
  }

  @Override
  public TypeVariable asTypeVariable()
  {
    return null;
  }

  @Override
  public WildcardType asWildcardType()
  {
    return null;
  }

  @Override
  public String dimension()
  {
    return this.dimension;
  }

  @Override
  public boolean isPrimitive()
  {
    return this.isPrimitive;
  }

  public String fullName()
  {
    return qualifiedTypeName() + this.dimension;
  }

  @Override
  public String qualifiedTypeName()
  {
    return this.qualifiedName;
  }

  @Override
  public String simpleTypeName()
  {
    return this.simpleName;
  }

  @Override
  public String typeName()
  {
    return this.simpleName;
  }

  @Override
  public String toString()
  {
    return fullName();
  }

  @Override
  public AnnotatedType asAnnotatedType()
  {
    return null;
  }

  @Override
  public Type getElementType()
  {
    return null;
  }
}
