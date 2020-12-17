package js.tools.script.doc;

import java.util.ArrayList;
import java.util.List;

import js.tools.commons.ast.Log;
import js.tools.script.doc.doclet.JsDocRoot;

public final class Context
{
  Log log;
  JsDocFacade facade;
  List<Scope> scopes;

  public Context(Log log, JsDocFacade facade)
  {
    this.log = log;
    this.facade = facade;
    this.scopes = new ArrayList<Scope>();
  }

  public JsDocRoot getJsDocRoot()
  {
    return this.facade.getJsDocRoot();
  }
}
