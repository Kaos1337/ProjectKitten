package javaBytecodeGenerator;

import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import types.ClassMemberSignature;
import types.ClassType;
import types.FixtureSignature;
import types.TestSignature;

/**
 * A Java bytecode generator. It transforms the Kitten intermediate language
 * into Java bytecode that can be dumped to Java class files and run. It uses
 * the BCEL library to represent Java classes and dump them on the file-system.
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

@SuppressWarnings("serial")
public class TestClassGenerator extends GeneralClassGenerator {

	/**
	 * Builds a class generator for the given class type.
	 *
	 * @param clazz the class type
	 * @param sigs a set of class member signatures. These are those that must
	 *            be translated. If this is {@code null}, all class members are
	 *            translated
	 */

	public TestClassGenerator(ClassType clazz, Set<ClassMemberSignature> sigs) {
		super(clazz.getName() + "Test", // name of the class
				// the superclass of the Kitten Object class is set to be the
				// Java java.lang.Object class
				clazz.getSuperclass() != null ? clazz.getSuperclass().getName() : "java.lang.Object", clazz.getName() + ".kit"); // empty constant pool, at the
																																	// beginning

		// we add the tests
		for (TestSignature t : clazz.testLookup())
			if (sigs == null || sigs.contains(t))
				t.createTest(this);

		// we add the fixtures
		for (FixtureSignature t : clazz.fixtureLookup())
			if (sigs == null || sigs.contains(t))
				t.createFixture(this);

		createMain(clazz, sigs);
	}

	private void createMain(ClassType clazztest, Set<ClassMemberSignature> sigs) {
		MethodGen mainGen;

		mainGen = new MethodGen(Constants.ACC_PUBLIC | Constants.ACC_STATIC, // private and static
				org.apache.bcel.generic.Type.VOID, // return type
				new org.apache.bcel.generic.Type[] // parameters
				{ new org.apache.bcel.generic.ArrayType("java.lang.String", 1) }, null, // parameters names: we do not care
				"main", // method's name
				this.getClassName(), // defining class
				this.getMainCode(clazztest, sigs),
				// this.generateJavaBytecode(getMainCode()), // bytecode of the
				// method
				this.getConstantPool()); // constant pool

		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		mainGen.setMaxStack();
		mainGen.setMaxLocals();

		// we add a method to the class that we are generating
		this.addMethod(mainGen.getMethod());
	}

	private InstructionList getMainCode(ClassType clazztest, Set<ClassMemberSignature> sigs) {
		InstructionList il = new InstructionList();
		// getStaticTarget()).createINVOKEVIRTUAL(classGen));
		il.insert(getFactory().createNew(clazztest.toBCEL().toString()));

		// inizio ciclo dei test
		for (TestSignature t : clazztest.testLookup())
			if (sigs == null || sigs.contains(t)) {

				il.append(InstructionFactory.DUP);

				il.append(getFactory()
						.createInvoke(clazztest.getName(), Constants.CONSTRUCTOR_NAME, Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
				// /
				// ciclo per i metodi delle fixture da eseguire sulla classe di test
				for (FixtureSignature f : clazztest.fixtureLookup()) {
					if (sigs == null || sigs.contains(f)) {

						il.append(InstructionFactory.DUP);

						il.append(this.getFactory().createInvoke(clazztest.getName() + "Test", // name of the class
								f.getName(), // name of the method
								org.apache.bcel.generic.Type.VOID, // return type
								new org.apache.bcel.generic.Type[] { org.apache.bcel.generic.Type.OBJECT }, // parameters types
								Constants.INVOKESTATIC)); // the type of invocation (static, special,
															// ecc.)

					}
				}

				il.append(InstructionFactory.DUP);

				il.append(this.getFactory().createInvoke(clazztest.getName() + "Test", // name of the class
						t.getName(), // name of the method
						org.apache.bcel.generic.Type.VOID, // return type
						new org.apache.bcel.generic.Type[] { org.apache.bcel.generic.Type.OBJECT }, // parameters types
						Constants.INVOKESTATIC)); // the type of invocation (static, special,
													// ecc.)

			}
		il.append(InstructionFactory.RETURN);
		return il;

	}
}