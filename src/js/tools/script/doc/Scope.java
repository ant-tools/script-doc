package js.tools.script.doc;

import org.mozilla.javascript.ast.AstNode;

final class Scope
{
  enum Type
  {
    NONE, FUNCTION, OBJECT, PROTOTYPE
  }

  Type type;
  AstNode node;
  String name;

  public Scope(Type type, AstNode node, String name)
  {
    this.type = type;
    this.node = node;
    this.name = name;
  }
}
