package types;

import javaBytecodeGenerator.GeneralClassGenerator;
import javaBytecodeGenerator.TestClassGenerator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.MethodGen;

import translation.Block;
import absyn.TestDeclaration;

public class TestSignature extends CodeSignature {

	/**
	 * Constructs the signature of a method with the given name, return type and parameters types.
	 *
	 * @param clazz
	 *            the class where this test is defined
	 * @param name
	 *            the name of the test
	 * @param abstractSyntax
	 *            the abstract syntax of the declaration of this test
	 */

	public TestSignature(ClassType clazz, String name, TestDeclaration abstractSyntax) {

		// super(clazz,returnType,parameters,name,abstractSyntax);
		super(clazz, VoidType.INSTANCE, TypeList.EMPTY, name, abstractSyntax);
	}

	@Override
	public String toString() {
		return getDefiningClass() + "test " + getName();
	}

	/**
	 * Generates an {@code invokevirtual} Java bytecode that calls this method. The Java {@code invokevirtual} bytecode calls a method by using the run-time class of the receiver to look up for the method's implementation.
	 *
	 * @param classGen
	 *            the class generator to be used to generate the {@code invokevirtual} Java bytecode
	 * @return an {@code invokevirtual} Java bytecode that calls this method
	 */

	public INVOKEVIRTUAL createINVOKEVIRTUAL(GeneralClassGenerator classGen) {
		return (INVOKEVIRTUAL) createInvokeInstruction(classGen, Constants.INVOKEVIRTUAL);
	}

	public void createTest(TestClassGenerator classGen) {

		MethodGen testGen;
		System.out.println("-----------------------------------------"+getCode());
		// http://www.tutorialspoint.com/java/java_basic_operators.htm
		testGen = new MethodGen(Constants.ACC_PRIVATE | Constants.ACC_STATIC, // private and static
				org.apache.bcel.generic.Type.VOID, // return type
				new org.apache.bcel.generic.Type[] { org.apache.bcel.generic.Type.CLASS }, // TODO
				// getParameters().toBCEL(), // parameters types, if any
				null, // parameters names: yo man, we do not give a fuck too.
						// Peace.
				getName(), // method's name
				classGen.getClassName(), // defining class
				classGen.generateJavaBytecode(getCode()), // bytecode of the method
				classGen.getConstantPool()); // constant pool

		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		testGen.setMaxStack();
		testGen.setMaxLocals();

		// we add a method to the class that we are generating
		classGen.addMethod(testGen.getMethod());

	}

	/**
	 * Adds a prefix to the Kitten bytecode generated for this method.
	 *
	 * @param code
	 *            the code already compiled for this method
	 * @return {@code code} itself
	 */

	@Override
	protected Block addPrefixToCode(Block code) {
		return code;
	}
}