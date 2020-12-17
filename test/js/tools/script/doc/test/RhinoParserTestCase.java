package js.tools.script.doc.test;

import junit.framework.TestCase;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WithStatement;

public class RhinoParserTestCase extends TestCase
{
  public void testJSDocAttachment1()
  {
    AstRoot root = parse("/** @type number */var a;");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    assertEquals("/** @type number */", root.getComments().first().getValue());
    assertNotNull(root.getFirstChild().getJsDoc());
  }

  public void testJSDocAttachment2()
  {
    AstRoot root = parse("/** @type number */a.b;");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    assertEquals("/** @type number */", root.getComments().first().getValue());
    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    assertNotNull(st.getExpression().getJsDoc());
  }

  public void testJSDocAttachment3()
  {
    AstRoot root = parse("var a = /** @type number */(x);");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    assertEquals("/** @type number */", root.getComments().first().getValue());
    VariableDeclaration vd = (VariableDeclaration)root.getFirstChild();
    VariableInitializer vi = vd.getVariables().get(0);
    assertNotNull(vi.getInitializer().getJsDoc());
  }

  public void testJSDocAttachment4()
  {
    AstRoot root = parse("(function() {/** should not be attached */})()");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    FunctionCall fc = (FunctionCall)st.getExpression();
    ParenthesizedExpression pe = (ParenthesizedExpression)fc.getTarget();
    assertNull(pe.getJsDoc());
  }

  public void testJSDocAttachment5()
  {
    AstRoot root = parse("({/** attach me */ 1: 2});");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    ParenthesizedExpression pt = (ParenthesizedExpression)st.getExpression();
    ObjectLiteral lit = (ObjectLiteral)pt.getExpression();
    NumberLiteral number = (NumberLiteral)lit.getElements().get(0).getLeft();
    assertNotNull(number.getJsDoc());
  }

  public void testJSDocAttachment6()
  {
    AstRoot root = parse("({1: /** don't attach me */ 2, 3: 4});");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    ParenthesizedExpression pt = (ParenthesizedExpression)st.getExpression();
    ObjectLiteral lit = (ObjectLiteral)pt.getExpression();
    for(ObjectProperty el : lit.getElements()) {
      assertNull(el.getLeft().getJsDoc());
      assertNull(el.getRight().getJsDoc());
    }
  }

  public void testJSDocAttachment7()
  {
    AstRoot root = parse("({/** attach me */ '1': 2});");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    ParenthesizedExpression pt = (ParenthesizedExpression)st.getExpression();
    ObjectLiteral lit = (ObjectLiteral)pt.getExpression();
    StringLiteral stringLit = (StringLiteral)lit.getElements().get(0).getLeft();
    assertNotNull(stringLit.getJsDoc());
  }

  public void testJSDocAttachment8()
  {
    AstRoot root = parse("({'1': /** attach me */ (foo())});");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    ParenthesizedExpression pt = (ParenthesizedExpression)st.getExpression();
    ObjectLiteral lit = (ObjectLiteral)pt.getExpression();
    ParenthesizedExpression parens = (ParenthesizedExpression)lit.getElements().get(0).getRight();
    assertNotNull(parens.getJsDoc());
  }

  public void testJSDocAttachment9()
  {
    AstRoot root = parse("({/** attach me */ foo: 2});");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    ParenthesizedExpression pt = (ParenthesizedExpression)st.getExpression();
    ObjectLiteral lit = (ObjectLiteral)pt.getExpression();
    Name objLitKey = (Name)lit.getElements().get(0).getLeft();
    assertNotNull(objLitKey.getJsDoc());
  }

  public void testJSDocAttachment10()
  {
    AstRoot root = parse("({foo: /** attach me */ (bar)});");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    ParenthesizedExpression pt = (ParenthesizedExpression)st.getExpression();
    ObjectLiteral lit = (ObjectLiteral)pt.getExpression();
    ParenthesizedExpression parens = (ParenthesizedExpression)lit.getElements().get(0).getRight();
    assertNotNull(parens.getJsDoc());
  }

  public void testJSDocAttachment11()
  {
    AstRoot root = parse("({/** attach me */ get foo() {}});");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    ParenthesizedExpression pt = (ParenthesizedExpression)st.getExpression();
    ObjectLiteral lit = (ObjectLiteral)pt.getExpression();
    Name objLitKey = (Name)lit.getElements().get(0).getLeft();
    assertNotNull(objLitKey.getJsDoc());
  }

  public void testJSDocAttachment12()
  {
    AstRoot root = parse("({/** attach me */ get 1() {}});");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    ParenthesizedExpression pt = (ParenthesizedExpression)st.getExpression();
    ObjectLiteral lit = (ObjectLiteral)pt.getExpression();
    NumberLiteral number = (NumberLiteral)lit.getElements().get(0).getLeft();
    assertNotNull(number.getJsDoc());
  }

  public void testJSDocAttachment13()
  {
    AstRoot root = parse("({/** attach me */ get 'foo'() {}});");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    ParenthesizedExpression pt = (ParenthesizedExpression)st.getExpression();
    ObjectLiteral lit = (ObjectLiteral)pt.getExpression();
    StringLiteral stringLit = (StringLiteral)lit.getElements().get(0).getLeft();
    assertNotNull(stringLit.getJsDoc());
  }

  public void testJSDocAttachment14()
  {
    AstRoot root = parse("var a = (/** @type {!Foo} */ {});");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());
    assertEquals("/** @type {!Foo} */", root.getComments().first().getValue());
    VariableDeclaration vd = (VariableDeclaration)root.getFirstChild();
    VariableInitializer vi = vd.getVariables().get(0);
    assertNotNull(((ParenthesizedExpression)vi.getInitializer()).getExpression().getJsDoc());
  }

  public void testJSDocAttachment15()
  {
    AstRoot root = parse("/** @private */ x(); function f() {}");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());

    ExpressionStatement st = (ExpressionStatement)root.getFirstChild();
    assertNotNull(st.getExpression().getJsDoc());
  }

  public void testJSDocAttachment16()
  {
    AstRoot root = parse("/** @suppress {with} */ with (context) {\n" + "  eval('[' + expr + ']');\n" + "}\n");
    assertNotNull(root.getComments());
    assertEquals(1, root.getComments().size());

    WithStatement st = (WithStatement)root.getFirstChild();
    assertNotNull(st.getJsDoc());
  }

  private AstRoot parse(String string)
  {
    return parse(string, true);
  }

  private AstRoot parse(String string, boolean jsdoc)
  {
    return parse(string, null, null, jsdoc);
  }

  private AstRoot parse(String string, final String[] errors, final String[] warnings, boolean jsdoc)
  {
    CompilerEnvirons environment = new CompilerEnvirons();
    environment.setReservedKeywordAsIdentifier(true);
    environment.setStrictMode(false);

    RhinoErrorReporter testErrorReporter = new RhinoErrorReporter(errors, warnings)
    {
      @Override
      public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset)
      {
        if(errors == null) {
          throw new UnsupportedOperationException();
        }
        return new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
      }
    };
    environment.setErrorReporter(testErrorReporter);

    environment.setRecordingComments(true);
    environment.setRecordingLocalJsDocComments(jsdoc);

    Parser p = new Parser(environment, testErrorReporter);
    AstRoot script = null;
    try {
      script = p.parse(string, null, 0);
    }
    catch(EvaluatorException e) {
      if(errors == null) {
        // EvaluationExceptions should not occur when we aren't expecting
        // errors.
        throw e;
      }
    }

    assertTrue(testErrorReporter.hasEncounteredAllErrors());
    assertTrue(testErrorReporter.hasEncounteredAllWarnings());

    return script;
  }
}
