package types;

import javaBytecodeGenerator.GeneralClassGenerator;
import javaBytecodeGenerator.TestClassGenerator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import translation.Block;
import absyn.FixtureDeclaration;

/**
 * The signature of a constructor of a Kitten class.
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

public class FixtureSignature extends CodeSignature {

	private final int pos;
	private static int nfixture = 0;

	/**
	 * Constructs a signature for a constructor, given its parameters types and the class it belongs to.
	 *
	 * @param clazz
	 *            the class this constructor belongs to
	 * @param parameters
	 *            the types of the parameters of the constructor
	 * @param abstractSyntax
	 *            the abstract syntax of the declaration of this constructor
	 */
	public FixtureSignature(ClassType clazz, FixtureDeclaration abstractSyntax) {
		// a constructor always returns void and its name is by default init
		super(clazz, VoidType.INSTANCE, TypeList.EMPTY, "fixture" + nfixture, abstractSyntax);
		this.pos = nfixture++;
	}

	@Override
	public String toString() {
		return getDefiningClass() + "fixture" + pos;
	}

	/**
	 * Generates an {@code invokespecial} Java bytecode that calls this constructor. The Java {@code invokespecial} bytecode calls a method by using a hard-wired class name to look up for the method's implementation and has a receiver.
	 *
	 * @param classGen
	 *            the class generator to be used to generate the {@code invokespecial} Java bytecode
	 * @return an {@code invokespecial} Java bytecode that calls this constructor
	 */

	public INVOKESTATIC createINVOKESTATIC(GeneralClassGenerator classGen) {
		return (INVOKESTATIC) createInvokeInstruction(classGen, Constants.INVOKESTATIC);
	}

	/**
	 * Adds a prefix to the Kitten bytecode generated for this method.
	 * For fixtures we don't do anything
	 * @param code
	 *            the code already compiled for this constructor
	 * @return {@code code} code itself
	 */

	@Override
	protected Block addPrefixToCode(Block code) {
		return code;
	}

	public void createFixture(TestClassGenerator classGen) {
		MethodGen fixtureGen;
		// http://www.tutorialspoint.com/java/java_basic_operators.htm
		fixtureGen = new MethodGen(Constants.ACC_PRIVATE | Constants.ACC_STATIC, // private and static
				org.apache.bcel.generic.Type.VOID, // return type
				new Type[] { this.getDefiningClass().toBCEL() }, // TODO
				// getParameters().toBCEL(), // parameters types, if any
				null, // parameters names we do not care too
				getName(), // method's name
				classGen.getClassName(), // defining class
				classGen.generateJavaBytecode(getCode()), // bytecode of the
															// method
				classGen.getConstantPool()); // constant pool

		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		fixtureGen.setMaxStack();
		fixtureGen.setMaxLocals();

		// we add a method to the class that we are generating
		classGen.addMethod(fixtureGen.getMethod());

	}
}