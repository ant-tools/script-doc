package js.tools.script.doc;

import java.util.ArrayList;
import java.util.List;

import js.tools.commons.ast.Names;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.RegExpLiteral;
import org.mozilla.javascript.ast.StringLiteral;

public class RightValue
{
  private static final String STRING = "String";
  private static final String NUMBER = "Number";
  private static final String BOOLEAN = "Boolean";
  private static final String NULL = "NULL";
  private static final String FUNCTION = "Function";
  private static final String OBJECT = "Object";
  private static final String ARRAY = "Array";
  private static final String REGEXP = "RegExp";
  private static final String UNKNOWN = "Unknown";

  private AstNode node;
  private String type;
  private Object value;

  public RightValue(InfixExpression infixExpression)
  {
    this.node = infixExpression.getRight();
    switch(this.node.getType()) {
    case Token.STRING:
      this.type = STRING;
      this.value = ((StringLiteral)this.node).getValue(true);
      break;

    case Token.NUMBER:
      this.type = NUMBER;
      this.value = ((NumberLiteral)this.node).getValue();
      break;

    case Token.FALSE:
      this.type = BOOLEAN;
      this.value = false;
      break;

    case Token.TRUE:
      this.type = BOOLEAN;
      this.value = true;
      break;

    case Token.NULL:
      this.type = NULL;
      break;

    case Token.FUNCTION:
      this.type = FUNCTION;
      break;

    case Token.OBJECTLIT:
      this.type = OBJECT;
      break;

    case Token.ARRAYLIT:
      this.type = ARRAY;
      break;

    case Token.REGEXP:
      this.type = REGEXP;
      this.value = ((RegExpLiteral)this.node).getValue();
      break;

    default:
      this.type = UNKNOWN;
    }
  }

  public boolean isObjectNode()
  {
    return this.node instanceof ObjectLiteral;
  }

  public ObjectLiteral asObjectLiteral()
  {
    return (ObjectLiteral)this.node;
  }

  public boolean isFunctionNode()
  {
    return this.node instanceof FunctionNode;
  }

  public boolean isAbstractMethod()
  {
    return !((FunctionNode)this.node).getBody().hasChildren();
  }

  public FunctionNode asFunctionNode()
  {
    return (FunctionNode)this.node;
  }

  public List<String> formalParameters()
  {
    List<String> formalParameters = new ArrayList<String>();
    for(AstNode param : ((FunctionNode)this.node).getParams()) {
      formalParameters.add(Names.getName(param));
    }
    return formalParameters;
  }

  public boolean isValueNode()
  {
    return !isFunctionNode() && !isObjectNode();
  }

  public String type()
  {
    return this.type;
  }

  public Object value()
  {
    return this.value;
  }
}
