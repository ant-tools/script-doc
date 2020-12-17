package js.tools.script.doc;

import java.util.List;

import js.tools.commons.ast.Names;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;

/**
 * Function call AST handler. Process j(s)-script semantic extensions based on function call. Constraining rules
 * consider statement scope, function call formal parameters and naming convention. For those recognized at the time
 * these are written scope must be global. There are three rules based on name matching and following function
 * signature, as listed:
 * <ul>
 * <li>$package(packageName) - package declaration</li>
 * <li>$extends(subClass, superClass) - class extension statement</li>
 * <li>$implements(className, interfaceName) - interface implementation statement</li>
 * </ul>
 * 
 * @author Iulian Rotaru
 */
final class FunctionCallHandler extends AbstractAstHandler
{
  public FunctionCallHandler(Context context)
  {
    super(context);
  }

  @Override
  public void handle(Node node)
  {
    FunctionCall functionCall = (FunctionCall)node;
    if(functionCall.getEnclosingFunction() != null) {
      // j(s)-script semantic extensions based on function call are allowed only in global scope
      return;
    }

    AstNode target = functionCall.getTarget();
    String functionName = Names.getName(target);
    List<AstNode> arguments = functionCall.getArguments();

    if("$package".equals(functionName)) {
      if(arguments.size() < 1) {
        this.log.warn("Missing argument from package declaration.");
        return;
      }
      String packageName = Names.getName(arguments.get(0));
      this.context.facade.declarePackage(packageName);
      return;
    }

    if("$interface".equals(functionName)) {
      if(arguments.size() < 2) {
        this.log.warn("Missing argument(s) from interface declaration.");
        return;
      }
      String interfaceName = Names.getName(arguments.get(0));
      String rawComment = functionCall.getJsDoc();
      System.out.println(rawComment);
      rawComment = target.getJsDoc();
      System.out.println(rawComment);

      this.context.facade.declareInterface(interfaceName, rawComment);

      ObjectLiteral declaration = (ObjectLiteral)functionCall.getArguments().get(1);
      for(ObjectProperty objectProperty : declaration.getElements()) {
        String leftName = Names.getName(objectProperty);
        RightValue rightValue = new RightValue(objectProperty);
        if(!rightValue.isFunctionNode()) {
          this.log.warn("Interface declaration for %s does not support type %s for member '%s'.", interfaceName, rightValue.type(), leftName);
          continue;
        }
        rawComment = objectProperty.getLeft().getJsDoc();
        this.context.facade.addAbstractMethod(interfaceName, leftName, rightValue.formalParameters(), rawComment);
      }
    }

    if("$extends".equals(functionName)) {
      if(arguments.size() < 2) {
        this.log.warn("Missing argument(s) from class extension statement.");
        return;
      }
      String subClassName = Names.getName(arguments.get(0));
      String superClassName = Names.getName(arguments.get(1));
      this.context.facade.extendsClass(subClassName, superClassName);
      return;
    }

    if("$mixin".equals(functionName)) {
      if(arguments.size() < 2) {
        this.log.warn("Missing argument(s) from mixin statement.");
        return;
      }
      String className = Names.getName(arguments.get(0));
      String mixinName = Names.getName(arguments.get(1));
      this.context.facade.mixinClass(className, mixinName);
      return;
    }

    if("$implements".equals(functionName)) {
      if(arguments.size() < 2) {
        this.log.warn("Missing argument(s) from interface implementation statement.");
        return;
      }
      String className = Names.getName(arguments.get(0));
      String interfaceName = Names.getName(arguments.get(1));
      this.context.facade.implementsInterface(className, interfaceName);
    }
  }
}
