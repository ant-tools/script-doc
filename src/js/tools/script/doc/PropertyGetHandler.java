package js.tools.script.doc;

import js.tools.commons.ast.Names;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.PropertyGet;

final class PropertyGetHandler extends AbstractAstHandler
{
  private JsDocFacade facade;

  public PropertyGetHandler(Context context)
  {
    super(context);
    this.facade = context.facade;
  }

  @Override
  public void handle(Node node)
  {
    PropertyGet propertyGet = (PropertyGet)node;
    if(propertyGet.getParent().getType() != Token.ASSIGN) {
      // this property getter is not part of assignment statement
      return;
    }
    Assignment assignment = (Assignment)propertyGet.getParent();
    if(assignment.getRight() == propertyGet) {
      // this property getter is not left hand side of assignment statement
      return;
    }
    if(propertyGet.getTarget().getType() != Token.THIS) {
      // this property getter target is not this keyword
      return;
    }

    RightValue rightValue = new RightValue(assignment);
    String rawComment = propertyGet.getParent().getJsDoc();

    FunctionNode functionNode = propertyGet.getEnclosingFunction();
    if(functionNode == null) {
      // TODO warning
      return;
    }

    Scope scope = getScope(propertyGet);
    if(scope == null) {
      // missing scope due probably to anonymous function
      return;
    }

    // TODO consider both declaration and assignment
    // declaration is only first assignment

    String className = scope.name;
    String memberName = Names.getName(propertyGet);

    if(scope.type == Scope.Type.FUNCTION || scope.type == Scope.Type.PROTOTYPE) {
      if(rightValue.isFunctionNode()) {
        if(rightValue.isAbstractMethod()) {
          this.facade.addAbstractMethod(className, memberName, rightValue.formalParameters(), rawComment);
        }
        else {
          this.facade.addMethod(className, memberName, rightValue.formalParameters(), rawComment);
        }
      }
      else {
        if(isConstantName(memberName)) {
          this.facade.addConstant(className, memberName, rightValue.type(), rightValue.value(), rawComment);
        }
        else {
          this.facade.addField(className, memberName, rightValue.type(), rawComment);
        }
      }
      return;
    }

    if(scope.type == Scope.Type.OBJECT) {
      if(rightValue.isFunctionNode()) {
        if(rightValue.isAbstractMethod()) {
          this.facade.addAbstractMethod(className, memberName, rightValue.formalParameters(), rawComment);
        }
        else {
          this.facade.addStaticMethod(className, memberName, rightValue.formalParameters(), rawComment);
        }
      }
      else {
        if(isConstantName(memberName)) {
          this.facade.addStaticConstant(className, memberName, rightValue.type(), rightValue.value(), rawComment);
        }
        else {
          this.facade.addStaticField(className, memberName, rightValue.type(), rawComment);
        }
      }
      return;
    }
  }

  private Scope getScope(AstNode node)
  {
    for(; node != null; node = node.getParent()) {
      if(node.getType() != Token.FUNCTION && node.getType() != Token.OBJECTLIT) continue;
      for(Scope scope : this.context.scopes) {
        if(scope.node == node) return scope;
      }
    }
    return null;
  }
}
