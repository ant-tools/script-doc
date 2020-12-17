package js.tools.script.doc.test;

import js.tools.script.doc.LeftName;
import junit.framework.TestCase;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.StringLiteral;

public class LeftNameUnitTests extends TestCase
{
  public void testPseudoOperators()
  {
    for(String pseudoOperator : new String[]
    {
        "$static", "$preload", "$package", "$declare", "$include", "$extends", "$augments", "$args", "$format", "$legacy", "$log"
    }) {
      LeftName name = new LeftName(new InfixExpressionMock(pseudoOperator, Token.OBJECTLIT));
      assertTrue(name.isValid());
      assertTrue(name.isPseudoOperator());
      assertFalse(name.isPrototypeBody());
      assertFalse(name.isPrototypeProperty());
      assertNull(name.asPackage());
      assertNull(name.asClass());
      assertNull(name.asOuterClass());
      assertNull(name.asMember());
    }
  }

  public void testMember()
  {
  }

  public void testPrototypeBody()
  {
    LeftName name = new LeftName(new InfixExpressionMock("js.net.Connection.State.prototype", Token.OBJECTLIT));
    assertTrue(name.isValid());
    assertEquals("js.net.Connection.State.prototype", name.value());
    assertEquals("js.net", name.asPackage());
    assertTrue(name.isPrototypeBody());
    assertFalse(name.isPrototypeProperty());
    assertEquals("js.net.Connection", name.asOuterClass());
    assertEquals("State", name.asClass());
  }

  public void testPrototypeBodyOnPrivateClass()
  {
    LeftName name = new LeftName(new InfixExpressionMock("js.net.Connection._State.prototype", Token.OBJECTLIT));
    assertTrue(name.isValid());
    assertEquals("js.net.Connection._State.prototype", name.value());
    assertEquals("js.net", name.asPackage());
    assertTrue(name.isPrototypeBody());
    assertFalse(name.isPrototypeProperty());
    assertEquals("js.net.Connection", name.asOuterClass());
    assertEquals("_State", name.asClass());
  }

  public void testPrototypeProperty()
  {
    for(int token : new int[]
    {
        Token.FUNCTION, Token.OBJECTLIT, Token.ARRAYLIT, Token.NUMBER, Token.STRING, Token.FALSE, Token.TRUE, Token.REGEXP, Token.NULL
    }) {
      LeftName name = new LeftName(new InfixExpressionMock("js.net.Connection.State.prototype.address", token));
      assertTrue(name.isValid());
      assertEquals("js.net.Connection.State.prototype.address", name.value());
      assertEquals("js.net", name.asPackage());
      assertFalse(name.isPrototypeBody());
      assertTrue(name.isPrototypeProperty());
      assertEquals("js.net.Connection", name.asOuterClass());
      assertEquals("State", name.asClass());
      assertEquals("address", name.asMember());
    }
  }

  public void testPrototypePropertyWithoutPackageName()
  {
    for(int token : new int[]
    {
        Token.FUNCTION, Token.OBJECTLIT, Token.ARRAYLIT, Token.NUMBER, Token.STRING, Token.FALSE, Token.TRUE, Token.REGEXP, Token.NULL
    }) {
      LeftName name = new LeftName(new InfixExpressionMock("Connection.prototype.address", token));
      assertTrue(name.isValid());
      assertEquals("Connection.prototype.address", name.value());
      assertNull(name.asPackage());
      assertFalse(name.isPrototypeBody());
      assertTrue(name.isPrototypeProperty());
      assertNull(name.asOuterClass());
      assertEquals("Connection", name.asClass());
      assertEquals("address", name.asMember());
    }
  }

  public void testUnqualifiedClassName()
  {
    LeftName name = new LeftName(new InfixExpressionMock("String", Token.FUNCTION));
    assertTrue(name.isValid());
    assertEquals("String", name.value());
    assertNull(name.asPackage());
    assertFalse(name.isPrototypeBody());
    assertFalse(name.isPrototypeProperty());
    assertNull(name.asOuterClass());
    assertEquals("String", name.asClass());
  }

  public void testUnqualifiedMemberName()
  {
    LeftName name = new LeftName(new InfixExpressionMock("send", Token.FUNCTION));
    assertFalse(name.isValid());
    assertEquals("send", name.value());
    assertNull(name.asPackage());
    assertFalse(name.isPrototypeBody());
    assertFalse(name.isPrototypeProperty());
    assertNull(name.asOuterClass());
    assertNull(name.asClass());
    assertEquals("send", name.asMember());
  }

  public void testClassDeclaration()
  {
    for(int token : new int[]
    {
        Token.FUNCTION, Token.OBJECTLIT
    }) {
      LeftName name = new LeftName(new InfixExpressionMock("js.net.Connection", token));
      assertTrue(name.isValid());
      assertEquals("js.net.Connection", name.value());
      assertEquals("js.net", name.asPackage());
      assertFalse(name.isPrototypeBody());
      assertFalse(name.isPrototypeProperty());
      assertNull(name.asOuterClass());
      assertEquals("js.net.Connection", name.asClass());
    }
  }

  public void testInnerClassDeclaration()
  {
    LeftName name = new LeftName(new InfixExpressionMock("js.net.Connection.State", Token.FUNCTION));
    assertTrue(name.isValid());
    assertEquals("js.net.Connection.State", name.value());
    assertEquals("js.net", name.asPackage());
    assertFalse(name.isPrototypeBody());
    assertFalse(name.isPrototypeProperty());
    assertEquals("js.net.Connection", name.asOuterClass());
    assertEquals("State", name.asClass());
  }

  public void testUpperCaseInnerClassDeclaration()
  {
    for(int token : new int[]
    {
        Token.FUNCTION, Token.OBJECTLIT
    }) {
      LeftName name = new LeftName(new InfixExpressionMock("js.net.Connection.TIMEOUT", token));
      assertTrue(name.isValid());
      assertEquals("js.net.Connection.TIMEOUT", name.value());
      assertEquals("js.net", name.asPackage());
      assertFalse(name.isPrototypeBody());
      assertFalse(name.isPrototypeProperty());
      assertEquals("js.net.Connection", name.asOuterClass());
      assertEquals("TIMEOUT", name.asClass());
    }
  }

  public void testUpperCaseClassAndInnerClassDeclaration()
  {
    for(int token : new int[]
    {
        Token.FUNCTION, Token.OBJECTLIT
    }) {
      LeftName name = new LeftName(new InfixExpressionMock("js.net.RPC.TIMEOUT", token));
      assertTrue(name.isValid());
      assertEquals("js.net.RPC.TIMEOUT", name.value());
      assertEquals("js.net", name.asPackage());
      assertFalse(name.isPrototypeBody());
      assertFalse(name.isPrototypeProperty());
      assertEquals("js.net.RPC", name.asOuterClass());
      assertEquals("TIMEOUT", name.asClass());
    }
  }

  public void testStaticField()
  {
    for(int token : new int[]
    {
        Token.ARRAYLIT, Token.NUMBER, Token.STRING, Token.FALSE, Token.TRUE, Token.REGEXP, Token.NULL
    }) {
      LeftName name = new LeftName(new InfixExpressionMock("js.net.Connection.TIMEOUT", token));
      assertTrue(name.isValid());
      assertEquals("js.net.Connection.TIMEOUT", name.value());
      assertEquals("js.net", name.asPackage());
      assertFalse(name.isPrototypeBody());
      assertFalse(name.isPrototypeProperty());
      assertNull(name.asOuterClass());
      assertEquals("js.net.Connection", name.asClass());
      assertEquals("TIMEOUT", name.asMember());
    }
  }

  public void testStaticFieldInUpperCaseClass()
  {
    for(int token : new int[]
    {
        Token.ARRAYLIT, Token.NUMBER, Token.STRING, Token.FALSE, Token.TRUE, Token.REGEXP, Token.NULL
    }) {
      LeftName name = new LeftName(new InfixExpressionMock("js.net.RPC.TIMEOUT", token));
      assertTrue(name.isValid());
      assertEquals("js.net.RPC.TIMEOUT", name.value());
      assertEquals("js.net", name.asPackage());
      assertFalse(name.isPrototypeBody());
      assertFalse(name.isPrototypeProperty());
      assertNull(name.asOuterClass());
      assertEquals("js.net.RPC", name.asClass());
      assertEquals("TIMEOUT", name.asMember());
    }
  }

  public void testPrivateStaticFieldInUpperCaseClass()
  {
    for(int token : new int[]
    {
        Token.ARRAYLIT, Token.NUMBER, Token.STRING, Token.FALSE, Token.TRUE, Token.REGEXP, Token.NULL
    }) {
      LeftName name = new LeftName(new InfixExpressionMock("js.net.RPC._TIMEOUT", token));
      assertTrue(name.isValid());
      assertEquals("js.net.RPC._TIMEOUT", name.value());
      assertEquals("js.net", name.asPackage());
      assertFalse(name.isPrototypeBody());
      assertFalse(name.isPrototypeProperty());
      assertNull(name.asOuterClass());
      assertEquals("js.net.RPC", name.asClass());
      assertEquals("_TIMEOUT", name.asMember());
    }
  }

  public void testStaticMethod()
  {
    LeftName name = new LeftName(new InfixExpressionMock("js.net.Connection.send", Token.FUNCTION));
    assertTrue(name.isValid());
    assertEquals("js.net.Connection.send", name.value());
    assertEquals("js.net", name.asPackage());
    assertFalse(name.isPrototypeBody());
    assertFalse(name.isPrototypeProperty());
    assertNull(name.asOuterClass());
    assertEquals("js.net.Connection", name.asClass());
    assertEquals("send", name.asMember());
  }

  public void testStaticMethodWithTitleCase()
  {
    LeftName name = new LeftName(new InfixExpressionMock("js.dom.template.Template.getInstance", Token.FUNCTION));
    assertTrue(name.isValid());
    assertEquals("js.dom.template.Template.getInstance", name.value());
    assertEquals("js.dom.template", name.asPackage());
    assertFalse(name.isPrototypeBody());
    assertFalse(name.isPrototypeProperty());
    assertNull(name.asOuterClass());
    assertEquals("js.dom.template.Template", name.asClass());
    assertEquals("getInstance", name.asMember());
  }

  public void testStaticPseudoOperator()
  {
    LeftName name = new LeftName(new InfixExpressionMock("js.ua.Page.$extends", Token.FUNCTION));
    assertTrue(name.isValid());
    assertEquals("js.ua.Page.$extends", name.value());
    assertEquals("js.ua", name.asPackage());
    assertFalse(name.isPrototypeBody());
    assertFalse(name.isPrototypeProperty());
    assertNull(name.asOuterClass());
    assertEquals("js.ua.Page", name.asClass());
    assertEquals("$extends", name.asMember());
  }

  private static class InfixExpressionMock extends InfixExpression
  {
    private AstNode left;
    private AstNode right;

    public InfixExpressionMock(String value, int type)
    {
      this.left = new AstNodeMock(value);
      this.right = new AstNodeMock(type);
    }

    @Override
    public AstNode getLeft()
    {
      return this.left;
    }

    @Override
    public AstNode getRight()
    {
      return this.right;
    }
  }

  private static class AstNodeMock extends StringLiteral
  {
    private String value;
    private int type;

    public AstNodeMock(String value)
    {
      this.value = value;
      this.type = Token.STRING;
    }

    public AstNodeMock(int type)
    {
      this.type = type;
    }

    @Override
    public int getType()
    {
      return this.type;
    }

    @Override
    public String getValue()
    {
      return this.value;
    }

    @Override
    public String toSource(int level)
    {
      return null;
    }

    @Override
    public void visit(NodeVisitor visitor)
    {
    }
  }
}
