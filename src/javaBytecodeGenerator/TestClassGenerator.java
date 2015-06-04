package javaBytecodeGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import bytecode.ARRAYLOAD;
import bytecode.Bytecode;
import bytecode.NEW;
import bytecode.RETURN;
import bytecode.VIRTUALCALL;
import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;
import types.ConstructorSignature;
import types.FixtureSignature;
import types.MethodSignature;
import types.TestSignature;
import types.VoidType;

/**
 * A Java bytecode generator. It transforms the Kitten intermediate language
 * into Java bytecode that can be dumped to Java class files and run.
 * It uses the BCEL library to represent Java classes and dump them on the file-system.
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

@SuppressWarnings("serial")
public class TestClassGenerator extends GeneralClassGenerator{

	/**
	 * The class being tested
	 */
	private ClassType clazztest;
	
	private ArrayList<MethodSignature> fixt = new ArrayList<MethodSignature>();
	private ArrayList<MethodSignature> test = new ArrayList<MethodSignature>();

	/**
	 * Builds a class generator for the given class type.
	 *
	 * @param clazz the class type
	 * @param sigs a set of class member signatures. These are those that must be
	 *             translated. If this is {@code null}, all class members are translated
	 */

	public TestClassGenerator(ClassType clazz, Set<ClassMemberSignature> sigs) {
		super(clazz.getName()  + "Test", // name of the class
			// the superclass of the Kitten Object class is set to be the Java java.lang.Object class
			clazz.getSuperclass() != null ? clazz.getSuperclass().getName() : "java.lang.Object",
			clazz.getName() + ".kit"); // empty constant pool, at the beginning

		clazztest = clazz;
		
		// we add the tests
		for (TestSignature t : clazz.testLookup())
			if (sigs == null || sigs.contains(t))
				t.createTest(this);
		
		// we add the fixtures
		for (FixtureSignature t : clazz.fixtureLookup())
			if (sigs == null || sigs.contains(t))
				t.createFixture(this, fixt);
		
		createMain();
	}
	
	private void createMain(){
		MethodGen mainGen;
		
		mainGen = new MethodGen(Constants.ACC_PUBLIC | Constants.ACC_STATIC, // private and static
				org.apache.bcel.generic.Type.VOID, // return type
				new org.apache.bcel.generic.Type[] // parameters
						{ new org.apache.bcel.generic.ArrayType("java.lang.String", 1) },
					null, // parameters names: we do not care
					"main", // method's name
					this.getClassName(), // defining class
					this.generateJavaBytecode(getMainCode()), // bytecode of the method
					this.getConstantPool()); // constant pool

		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		mainGen.setMaxStack();
		mainGen.setMaxLocals();

		// we add a method to the class that we are generating
		this.addMethod(mainGen.getMethod());
	}
	
	private Block getMainCode(){
		Block body = new Block(new RETURN(VoidType.INSTANCE));
		ArrayList<Bytecode> code = new ArrayList<Bytecode>();
		code.add(new NEW(clazztest));
		
		this.getJavaClass()
		
		for(MethodSignature m : )
			code.add(new VIRTUALCALL((ClassType) this.getJavaClass(), (MethodSignature) "aa"));
		
		return body;
		
		
	}

}