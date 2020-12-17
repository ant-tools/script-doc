package js.tools.script.doc.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ScriptDocTestSuite extends TestCase
{
  public static TestSuite suite()
  {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(RhinoParserTestCase.class);
    suite.addTestSuite(AstHandlersUnitTests.class);
    suite.addTestSuite(HelpersUnitTests.class);
    suite.addTestSuite(LeftNameUnitTests.class);
    suite.addTestSuite(JsDocFacadeUnitTests.class);
    suite.addTestSuite(JsDocDocletUnitTests.class);
    suite.addTestSuite(JsDocIntegrationTests.class);
    suite.addTestSuite(JavaStandardGeneratorIntegrationTests.class);
    return suite;
  }
}
