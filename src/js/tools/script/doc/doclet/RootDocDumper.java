package js.tools.script.doc.doclet;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import js.tools.commons.BugError;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;

public class RootDocDumper
{
  private PrintWriter writer;
  private int indentLevel = 0;
  private Set<Object> processed = new HashSet<Object>();

  public RootDocDumper()
  {
    this(writer());
  }

  private static Writer writer()
  {
    try {
      return new OutputStreamWriter(System.out, "UTF-8");
    }
    catch(UnsupportedEncodingException e) {
      throw new BugError("JVM with mnissing support for UTF-8.");
    }
  }

  public RootDocDumper(Writer writer)
  {
    this.writer = new PrintWriter(writer);
  }

  public void dump(RootDoc rootDoc)
  {
    for(ClassDoc classDoc : rootDoc.classes()) {
      printObject(classDoc.getClass().getName(), classDoc);
    }
    this.writer.flush();
  }

  private void dump(PackageDoc packageDoc)
  {
    if(!this.processed.add(packageDoc)) return;
    printValue("name", packageDoc.name());

    // package contains classes that have reference to package, circular dependency
    // so don't dump package content
  }

  @SuppressWarnings("deprecation")
  private void dump(ClassDoc classDoc)
  {
    if(!this.processed.add(classDoc)) return;
    // printArray("constructors", classDoc.constructors());
    // printValue("definesSerializableFields", classDoc.definesSerializableFields());
    // printArray("enumConstants", classDoc.enumConstants());
    // printArray("fields", classDoc.fields());
    printArray("importedClasses", classDoc.importedClasses());
    printArray("importedPackages", classDoc.importedPackages());
    printArray("innerClasses", classDoc.innerClasses());
    // printArray("interfaces", classDoc.interfaces());
    // printArray("interfaceTypes", classDoc.interfaceTypes());
    printValue("isAbstract", classDoc.isAbstract());
    printValue("isExternalizable", classDoc.isExternalizable());
    printValue("isSerializable", classDoc.isSerializable());
    if(!classDoc.qualifiedName().equals("java.lang.String")) {
      printArray("methods", classDoc.methods());
    }
    // printArray("serializableFields", classDoc.serializableFields());
    // printArray("serializationMethods", classDoc.serializationMethods());
    // printObject("superclass", classDoc.superclass());
    // printObject("superclassType", classDoc.superclassType());
    // printArray("typeParameters", classDoc.typeParameters());
    // printArray("typeParamTags", classDoc.typeParamTags());

    printValue("qualifiedTypeName", classDoc.qualifiedTypeName());

    dump((ProgramElementDoc)classDoc);
  }

  private void dump(MethodDoc methodDoc)
  {
    if(!this.processed.add(methodDoc)) return;
    printValue("isAbstract", methodDoc.isAbstract());
    // printObject("overriddenClass", methodDoc.overriddenClass());
    // printObject("overriddenMethod", methodDoc.overriddenMethod());
    // printObject("overriddenType", methodDoc.overriddenType());
    printObject("returnType", methodDoc.returnType());

    dump((ProgramElementDoc)methodDoc);
  }

  private void dump(Type typeDoc)
  {
    if(!this.processed.add(typeDoc)) return;
    printObject("asAnnotationTypeDoc", typeDoc.asAnnotationTypeDoc());
    printObject("asClassDoc", typeDoc.asClassDoc());
    printObject("asParameterizedType", typeDoc.asParameterizedType());
    printObject("asTypeVariable", typeDoc.asTypeVariable());
    printObject("asWildcardType", typeDoc.asWildcardType());
    printValue("dimension", typeDoc.dimension());
    printValue("isPrimitive", typeDoc.isPrimitive());
    printValue("qualifiedTypeName", typeDoc.qualifiedTypeName());
    printValue("toString", typeDoc.toString());
    printValue("typeName", typeDoc.typeName());
    printValue("simpleTypeName", typeDoc.simpleTypeName());
  }

  private void dump(ProgramElementDoc programElementDoc)
  {
    printObject("containingPackage", programElementDoc.containingPackage());
    printValue("qualifiedName", programElementDoc.qualifiedName());
    printValue("modifiers", programElementDoc.modifiers());
    printValue("isFinal", programElementDoc.isFinal());
    printValue("isPackagePrivate", programElementDoc.isPackagePrivate());
    printValue("isPrivate", programElementDoc.isPrivate());
    printValue("isProtected", programElementDoc.isProtected());
    printValue("isPublic", programElementDoc.isPublic());
    printValue("isStatic", programElementDoc.isStatic());

    dump((Doc)programElementDoc);
  }

  private void dump(Doc doc)
  {
    printValue("name", doc.name());
    printValue("isAnnotationType", doc.isAnnotationType());
    printValue("isAnnotationTypeElement", doc.isAnnotationTypeElement());
    printValue("isClass", doc.isClass());
    printValue("isConstructor", doc.isConstructor());
    printValue("isEnum", doc.isEnum());
    printValue("isEnumConstant", doc.isEnumConstant());
    printValue("isError", doc.isError());
    printValue("isException", doc.isException());
    printValue("isField", doc.isField());
    printValue("isIncluded", doc.isIncluded());
    printValue("isInterface", doc.isInterface());
    printValue("isMethod", doc.isMethod());
    printValue("isOrdinaryClass", doc.isOrdinaryClass());
  }

  private void printValue(String label, Object text)
  {
    printIndentation();
    this.writer.println(label + ": " + text);
  }

  private void printObject(String label, Object object)
  {
    printIndentation();
    this.writer.print(label + ": ");
    if(object == null) {
      this.writer.println("null");
    }
    else {
      indent();
      this.writer.println();
      if(object instanceof PackageDoc) {
        dump((PackageDoc)object);
      }
      else if(object instanceof ClassDoc) {
        dump((ClassDoc)object);
      }
      else if(object instanceof MethodDoc) {
        dump((MethodDoc)object);
      }
      else if(object instanceof Type) {
        dump((Type)object);
      }
      else {
        this.writer.println("Object class not implemented: " + object.getClass());
      }
      unindent();
    }
  }

  private void printArray(String label, Object[] array)
  {
    printIndentation();
    this.writer.print(label + ": ");
    if(array.length == 0) {
      this.writer.println("[]");
    }
    else {
      indent();
      this.writer.println();
      for(Object object : array) {
        if(object instanceof Doc) {
          printObject(((Doc)object).name(), object);
        }
        else {
          printObject(object.getClass().getSimpleName(), object);
        }
      }
      unindent();
    }
  }

  private void indent()
  {
    this.indentLevel += 2;
  }

  private void unindent()
  {
    this.indentLevel -= 2;
  }

  private void printIndentation()
  {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < this.indentLevel; i++) {
      builder.append(' ');
    }
    this.writer.print(builder.toString());
  }
}
