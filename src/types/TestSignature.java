package types;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import absyn.TestDeclaration;
import translation.Block;

public class TestSignature extends CodeSignature {

	/**
	 * Constructs the signature of a method with the given name, return type
	 * and parameters types.
	 *
	 * @param clazz the class where this test is defined
	 * @param name the name of the test
	 * @param abstractSyntax the abstract syntax of the declaration of this test
	 */

	public TestSignature(ClassType clazz, String name, TestDeclaration abstractSyntax) {

		//super(clazz,returnType,parameters,name,abstractSyntax);
		super(clazz,VoidType.INSTANCE,TypeList.EMPTY,name,abstractSyntax);
	}

	@Override
	public String toString() {
		return getDefiningClass() + "test " + getName();
	}
	
	/**
	 * Generates an {@code invokevirtual} Java bytecode that calls this
	 * method. The Java {@code invokevirtual} bytecode calls a method by using
	 * the run-time class of the receiver to look up for the method's implementation.
	 *
	 * @param classGen the class generator to be used to generate
	 *                 the {@code invokevirtual} Java bytecode
	 * @return an {@code invokevirtual} Java bytecode that calls this method
	 */

	public INVOKEVIRTUAL createINVOKEVIRTUAL(JavaClassGenerator classGen) {
		return (INVOKEVIRTUAL) createInvokeInstruction(classGen,Constants.INVOKEVIRTUAL);
	}


	/**
	 * Adds a prefix to the Kitten bytecode generated for this method.
	 *
	 * @param code the code already compiled for this method
	 * @return {@code code} itself
	 */

	@Override
	protected Block addPrefixToCode(Block code) {
		return code;
	}
}