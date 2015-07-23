package javaBytecodeGenerator;

import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import types.ClassMemberSignature;
import types.ClassType;
import types.FixtureSignature;
import types.TestSignature;
import types.TypeList;
import bytecode.IF_TRUE;
import bytecode.NEWSTRING;

/**
 * A Java bytecode generator. It transforms the Kitten intermediate language into Java bytecode that can be dumped to Java class files and run. It uses the BCEL library to represent Java classes and dump them on the file-system.
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

@SuppressWarnings("serial")
public class TestClassGenerator extends GeneralClassGenerator {

	/**
	 * Builds a class generator for the given class type.
	 *
	 * @param clazz
	 *            the class type
	 * @param sigs
	 *            a set of class member signatures. These are those that must be translated. If this is {@code null}, all class members are translated
	 */

	public TestClassGenerator(ClassType clazz, Set<ClassMemberSignature> sigs) {
		super(clazz.getName() + "Test", // name of the class
				"java.lang.Object", // the superclass of a Test Class is always Object
				clazz.getName() + ".kit"); // empty constant pool, at the beginning

		// we add fields for support the report
		createFields();

		// we add the tests
		for (TestSignature t : clazz.testLookup())
			if (sigs == null || sigs.contains(t))
				t.createTest(this);

		// we add the fixtures
		for (FixtureSignature t : clazz.fixtureLookup())
			if (sigs == null || sigs.contains(t))
				t.createFixture(this);

		// we add the main
		createMain(clazz, sigs);
	}

	private void createFields() {
		FieldGen posAsserts;

		posAsserts = new FieldGen(Constants.ACC_PRIVATE | Constants.ACC_STATIC, Type.getType(runTime.String.class), "posAsserts", this.getConstantPool()); // constant pool

		this.addField(posAsserts.getField());

	}

	private void createMain(ClassType clazztest, Set<ClassMemberSignature> sigs) {
		MethodGen mainGen;

		mainGen = new MethodGen(Constants.ACC_PUBLIC | Constants.ACC_STATIC, // private and static
				org.apache.bcel.generic.Type.VOID, // return type
				new org.apache.bcel.generic.Type[] // parameters
				{ new org.apache.bcel.generic.ArrayType("java.lang.String", 1) }, null, // parameters names: we do not care
				"main", // method's name
				this.getClassName(), // defining class
				this.getMainCode(clazztest, sigs), this.getConstantPool()); // constant pool

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

		System.out.println("Generata TestClass per " + clazztest.toBCEL().toString());
		// inizio ciclo dei test
		for (TestSignature t : clazztest.testLookup())
			if (sigs == null || sigs.contains(t)) {

				il.append(new NEWSTRING("").generateJavaBytecode(this));

				il.append(getFactory().createPutStatic(this.getClassName(), "posAsserts", Type.getType(runTime.String.class)));

				// creo una nuova istanza dell'oggetto sul quale eseguire i test
				il.append(getFactory().createNew(clazztest.toBCEL().toString()));

				il.append(InstructionFactory.DUP);

				il.append(clazztest.constructorLookup(TypeList.EMPTY).createINVOKESPECIAL(this));

				// il.append(getFactory().createInvoke(clazztest.getName(), Constants.CONSTRUCTOR_NAME, Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
				// ciclo per i metodi delle fixture da eseguire sulla classe di test
				for (FixtureSignature f : clazztest.fixtureLookup()) {
					if (sigs == null || sigs.contains(f)) {

						il.append(InstructionFactory.DUP);

						il.append(this.getFactory().createInvoke(clazztest.getName() + "Test", // name of the class
								f.getName(), // name of the method
								org.apache.bcel.generic.Type.VOID, // return type
								new org.apache.bcel.generic.Type[] { clazztest.toBCEL() }, // parameters types
								Constants.INVOKESTATIC)); // the type of invocation (static, special, ecc.)

					}
				}

				// il.append(InstructionFactory.DUP);
				// il.append(getFactory().createConstant(""));

				// il.append(InstructionFactory.SWAP);

				il.append(this.getFactory().createInvoke(clazztest.getName() + "Test", // name of the class
						t.getName(), // name of the method
						org.apache.bcel.generic.Type.VOID, // return type
						new org.apache.bcel.generic.Type[] { clazztest.toBCEL() }, // parameters types
						Constants.INVOKESTATIC)); // the type of invocation

				il.append(createReport(t.getName()));
			}
		il.append(InstructionFactory.RETURN);
		
		return il;

	}

	private InstructionList createReport(String nometest) {
		InstructionList il = new InstructionList();
		InstructionList ramoVero = new InstructionList();
		InstructionList ramoFalso = new InstructionList();
		InstructionList fine = new InstructionList();

		il.append(getFactory().createGetStatic(this.getClassName(), "posAsserts", Type.getType(runTime.String.class)));
		il.append(new NEWSTRING("").generateJavaBytecode(this));
		il.append(getFactory().createInvoke(runTime.String.class.getName(), "equals", Type.BOOLEAN, new Type[] { Type.getType(runTime.String.class) }, Constants.INVOKEVIRTUAL));

		/*
		 * IF TRUE goto vero -carico test fallito con posizioni goto fine vero: -carico test successo fine: -stampa return
		 */
		// stampa
		fine.append(getFactory().createInvoke(runTime.String.class.getName(), "output", Type.VOID, Type.NO_ARGS, Constants.INVOKEVIRTUAL));

		// se e' falso carichiamo la stringa "test fallito [posAsserts]"
		ramoFalso.append(new NEWSTRING("\n- test " + nometest + ": fallito").generateJavaBytecode(this));

		//ramoFalso.append(getFactory().createConstant("\n- test " + nometest + ": fallito"));
		ramoFalso.append(getFactory().createGetStatic(this.getClassName(), "posAsserts", Type.getType(runTime.String.class)));
		ramoFalso.append(getFactory().createInvoke(runTime.String.class.getName(), "concat", Type.getType(runTime.String.class), new Type[] { Type.getType(runTime.String.class) }, Constants.INVOKEVIRTUAL));
		ramoFalso.append(new org.apache.bcel.generic.GOTO(fine.getStart()));

		// se e' vero carichiamo la stringa "test superato"
		ramoVero.append(new NEWSTRING("\n- test " + nometest + ": superato").generateJavaBytecode(this));

		// creo l'if
		il.append((new IF_TRUE()).generateJavaBytecode(this, ramoVero.getStart(), ramoFalso.getStart()));

		// aggancio i rami
		il.append(ramoFalso);
		il.append(ramoVero);
		il.append(fine);

		return il;
	}

}