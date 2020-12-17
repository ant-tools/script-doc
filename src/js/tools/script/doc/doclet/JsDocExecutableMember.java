package js.tools.script.doc.doclet;

import java.util.ArrayList;
import java.util.List;

import js.tools.commons.util.Strings;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

/**
 * Represents a method or constructor of a j(s) script class. This class is abstract; it is the base class for
 * {@link JsDocConstructor} and {@link JsDocMethod}.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
public abstract class JsDocExecutableMember extends JsDocMember implements ExecutableMemberDoc
{
  private List<String> thrownExceptionNames = new ArrayList<String>();
  private Type[] thrownExceptions;
  private List<TypeVariable> typeParameters = new ArrayList<TypeVariable>();
  private List<JsDocParameter> parameters = new ArrayList<JsDocParameter>();

  /**
   * Add parameter with specified type.
   * 
   * @param typeName qualified type name,
   * @param name parameter name.
   */
  public void addParameter(String typeName, String name)
  {
    this.parameters.add(new JsDocParameter(typeName, name));
  }

  /**
   * Add parameters of undefined type.
   * 
   * @param name parameter name.
   */
  public void addParameter(String name)
  {
    this.parameters.add(new JsDocParameter(name));
  }

  /**
   * Add class name for executable throws clause.
   * 
   * @param className the qualified class name of throws clause.
   */
  public void addThrownClassName(String className)
  {
    this.thrownExceptionNames.add(className);
  }

  /**
   * Return true if this method is native. A j(s) script class can't be native and this method always returns false.
   * 
   * @return always return false.
   */
  @Override
  public boolean isNative()
  {
    return false;
  }

  /**
   * Return true if this method is synchronized. A j(s) script class can't be synchronized and this method always
   * returns false.
   * 
   * @return always return false.
   */
  @Override
  public boolean isSynchronized()
  {
    return false;
  }

  @Override
  public boolean isVarArgs()
  {
    return false;
  }

  @Override
  public ParamTag[] paramTags()
  {
    return this.jsDocComment.paramTags();
  }

  @Override
  public Parameter[] parameters()
  {
    return this.parameters.toArray(new Parameter[this.parameters.size()]);
  }

  public boolean hasParameterTypes(String[] searchedTypes)
  {
    if(searchedTypes == null) return true;
    List<JsDocType> formalTypes = formalTypes();
    if(searchedTypes.length != formalTypes.size()) return false;

    for(int i = 0; i < formalTypes.size(); i++) {
      if(!equals(formalTypes.get(i), searchedTypes[i])) return false;
    }
    return true;
  }

  private List<JsDocType> formalTypes()
  {
    List<JsDocType> types = new ArrayList<JsDocType>();
    for(Parameter p : this.parameters) {
      types.add((JsDocType)p.type());
    }
    return types;
  }

  private boolean equals(JsDocType type, String typeName)
  {
    if(type.fullName().equals(typeName)) return true;
    if(type.simpleTypeName().equals(typeName)) return true;
    return false;
  }

  @Override
  public String flatSignature()
  {
    List<String> types = new ArrayList<String>();
    for(JsDocParameter p : this.parameters) {
      types.add(p.flatTypeName());
    }
    return '(' + Strings.join(types, ", ") + ')';
  }

  /**
   * Although interface require to return qualified name this method refused to obey and returns unqualified type names.
   * Also type dimension is appended.
   */
  @Override
  public String signature()
  {
    List<String> types = new ArrayList<String>();
    for(JsDocParameter p : this.parameters) {
      types.add(p.flatTypeName());
    }
    return '(' + Strings.join(types, ", ") + ')';
  }

  @Override
  public Type[] thrownExceptionTypes()
  {
    if(this.thrownExceptions == null) {
      this.thrownExceptions = new Type[this.thrownExceptionNames.size()];
      for(int i = 0; i < this.thrownExceptionNames.size(); ++i) {
        this.thrownExceptions[i] = JsDocRoot.getInstance().getLazyClass(JsDocError.class, this.thrownExceptionNames.get(i));
      }
    }
    return this.thrownExceptions;
  }

  @Override
  public ClassDoc[] thrownExceptions()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public ThrowsTag[] throwsTags()
  {
    return this.jsDocComment.throwsTags();
  }

  @Override
  public ParamTag[] typeParamTags()
  {
    return this.jsDocComment.typeParamTags();
  }

  @Override
  public TypeVariable[] typeParameters()
  {
    return this.typeParameters.toArray(new TypeVariable[this.typeParameters.size()]);
  }
}
