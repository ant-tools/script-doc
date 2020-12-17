package js.tools.script.doc;

import java.util.ArrayList;
import java.util.List;

import js.tools.commons.ast.Names;
import js.tools.commons.ast.SemanticException;
import js.tools.commons.util.Strings;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.InfixExpression;

/**
 * j(s)-doc construct name. Recognized constructs:
 * 
 * <pre>
 *      js.net.Connection
 *      js.net._Connection
 *      js.net.Connection.address
 *      js.net.Connection._address
 *      js.net.Connection.TIMEOUT
 *      js.net.Connection._TIMEOUT
 *      js.net.Connection.State
 *      js.net.Connection._State
 *      js.net.Connection.prototype
 *      js.net.Connection.prototype.address
 *      js.net.Connection.prototype._address
 *      js.net.Connection.prototype.TIMEOUT
 *      js.net.Connection.prototype._TIMEOUT
 *      js.net.TIMEOUT ???
 * </pre>
 * 
 * @author Iulian Rotaru
 */
public final class LeftName
{
  private static final char PSEUDO_OPERATOR_PREFIX = '$';
  private static final String PROTOTYPE = "prototype";

  private String value;
  private List<String> names;
  private String packageName;
  private String outerClassName;
  private String className;
  private boolean isPseudoOperator;
  private boolean isPrototypeBody;
  private boolean isPrototypeProperty;
  private boolean isClassName;

  public LeftName(InfixExpression infixExpression)
  {
    this.value = Names.getName(infixExpression.getLeft());
    if(this.value.charAt(0) == PSEUDO_OPERATOR_PREFIX) {
      this.isPseudoOperator = true;
      return;
    }
    this.names = Strings.split(this.value, '.');
    parse(this.value, infixExpression.getRight().getType());
  }

  public boolean isValid()
  {
    return this.isPseudoOperator || this.isPrototypeBody || this.isPrototypeProperty || this.isClassName;
  }

  public boolean isPseudoOperator()
  {
    return this.isPseudoOperator;
  }

  public boolean isPrototypeBody()
  {
    return this.isPrototypeBody;
  }

  public boolean isPrototypeProperty()
  {
    return this.isPrototypeProperty;
  }

  public String asPackage()
  {
    return this.packageName;
  }

  public String asOuterClass()
  {
    if(this.outerClassName == null) return null;
    return this.packageName != null ? this.packageName + '.' + this.outerClassName : this.outerClassName;
  }

  public String asClass()
  {
    if(this.outerClassName != null) return this.className;
    return this.packageName != null ? this.packageName + '.' + this.className : this.className;
  }

  public String asMember()
  {
    return this.names != null ? this.names.get(this.names.size() - 1) : null;
  }

  public String value()
  {
    return this.value;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if(obj == null) return false;
    if(getClass() != obj.getClass()) return false;
    LeftName other = (LeftName)obj;
    if(this.value == null) {
      if(other.value != null) return false;
    }
    else if(!this.value.equals(other.value)) return false;
    return true;
  }

  private void parse(String value, int rightType)
  {
    if(value.isEmpty()) throw new SemanticException("Empty name value.");

    int charIndex = 0, length = value.length();

    boolean allUppercase = true;
    // search for first period; if not found value should be a member name
    for(;; ++charIndex) {
      if(charIndex == length) {
        if(!allUppercase && (Character.isUpperCase(value.charAt(0)) || (value.charAt(0) == '_' && Character.isUpperCase(value.charAt(1))))) {
          this.isClassName = true;
          this.className = value;
        }
        return;
      }
      char c = value.charAt(charIndex);
      if(c != '_' && !Character.isUpperCase(c)) allUppercase = false;
      if(c == '.') break;
    }

    // TODO refactor all left name parsing using tokens
    if((Character.isUpperCase(value.charAt(0)) || (value.charAt(0) == '_' && Character.isUpperCase(value.charAt(1)))) && value.indexOf("prototype") != -1) {
      this.className = value.substring(0, charIndex);
      this.isClassName = true;
      int i = charIndex + 11;
      if(i < length) {
        this.isPrototypeProperty = true;
      }
      else {
        this.isPrototypeBody = true;
      }
      return;
    }

    // search for next class name beginning (underscore or capital letter) after just found period
    for(; charIndex < length; ++charIndex) {
      char c = value.charAt(charIndex);
      if(c == '_' || Character.isUpperCase(c)) break;
    }

    if(charIndex < length) {
      this.packageName = value.substring(0, charIndex - 1);
    }
    else {
      // if package name is missing we reach value's end without actually found class beginning
      // so reset char index and start searching for class name again
      charIndex = 0;
    }

    List<String> names = new ArrayList<String>();
    boolean isPrototype = true;

    int namesIndex = charIndex;
    for(int protoIndex = 0; namesIndex < length; ++namesIndex, ++protoIndex) {
      if(value.charAt(namesIndex) == '.') {
        if(isPrototype) break;
        addNameToList(names, value, charIndex, namesIndex);
        charIndex = ++namesIndex;
        protoIndex = 0;
        isPrototype = true;
      }
      if(isPrototype && (value.charAt(namesIndex) != PROTOTYPE.charAt(protoIndex))) {
        isPrototype = false;
      }
      if(!isPrototype && namesIndex + 1 == length) {
        addNameToList(names, value, charIndex, namesIndex + 1);
      }
    }

    if(isPrototype) {
      this.isClassName = false;
      if(namesIndex == length) {
        this.isPrototypeBody = true;
      }
      else {
        this.isPrototypeProperty = true;
      }
      initClassesAndMemberNames(names, 1);
    }
    else {
      if(names.isEmpty()) {
        throw new SemanticException("Invalid name usage: missing class name.");
      }
      String lastName = names.get(names.size() - 1);
      if(isMemberLikeName(lastName)) {
        initClassesAndMemberNames(names, 2);
        return;
      }
      if(isConstantLikeName(lastName) && (rightType != Token.FUNCTION && rightType != Token.OBJECTLIT && rightType != Token.NEW)) {
        initClassesAndMemberNames(names, 2);
        return;
      }
      initClassesAndMemberNames(names, 1);
    }
  }

  private void initClassesAndMemberNames(List<String> names, int lastExcludedCount)
  {
    if(names.size() < lastExcludedCount) return;
    if(names.size() > lastExcludedCount) {
      this.outerClassName = Strings.join(names.subList(0, names.size() - lastExcludedCount), '.');
    }
    this.className = names.get(names.size() - lastExcludedCount);
    this.isClassName = true;
  }

  private void addNameToList(List<String> names, String value, int beginIndex, int endIndex)
  {
    String name = value.substring(beginIndex, endIndex);
    if(name.length() == 1 && name.charAt(0) == '-') {
      throw new SemanticException("Invalid name. Only underscore.");
    }
    names.add(name);
  }

  /**
   * Is member like name. Test if given name is like member name, i.e. it starts with lower case, optionally prefixed with
   * underscore.
   * 
   * @param name name to be tested
   * @return true if given name is like member.
   */
  private boolean isMemberLikeName(String name)
  {
    char prefix = name.charAt(0);
    // allow pseudo-operators as member; a pseudo-operator starts with dollar
    int i = (prefix == '_' || prefix == '$') ? 1 : 0;
    return Character.isLowerCase(name.charAt(i));
  }

  /**
   * Is constant like name. Test if given name is like a constant, i.e. all upper case, optionally prefixed with underscore.
   * 
   * @param name name to be tested
   * @return true if given name resemble a constant.
   */
  private boolean isConstantLikeName(String name)
  {
    for(int i = 0, l = name.length(); i < l; ++i) {
      char c = name.charAt(i);
      if(!(c == '_' || Character.isUpperCase(c))) return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return this.value;
  }
}
