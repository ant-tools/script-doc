package js.tools.script.doc;

import java.util.List;
import java.util.SortedSet;

import js.tools.commons.ast.Names;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;

public class RootHandler extends AbstractAstHandler
{
  private static final String PACKAGE_INFO = "package-info.js";

  protected RootHandler(Context context)
  {
    super(context);
  }

  @Override
  public void handle(Node node)
  {
    AstRoot root = (AstRoot)node;
    if(!root.getSourceName().endsWith(PACKAGE_INFO)) return;

    SortedSet<Comment> comments = root.getComments();
    if(comments == null) return;

    ExpressionStatement statement = (ExpressionStatement)root.getFirstChild();
    FunctionCall functionCall = (FunctionCall)statement.getExpression();
    List<AstNode> arguments = functionCall.getArguments();
    if(arguments.isEmpty()) return;

    String packageName = Names.getName(arguments.get(0));
    String packageRawComment = comments.first().getValue();
    this.context.facade.setPackageComment(packageName, packageRawComment);
  }
}
