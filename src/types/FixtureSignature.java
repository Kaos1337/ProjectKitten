package types;

import java.util.ArrayList;

import javaBytecodeGenerator.GeneralClassGenerator;
import javaBytecodeGenerator.TestClassGenerator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.INVOKESPECIAL;/*
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;*/

import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import translation.Block;
import absyn.FixtureDeclaration;
import bytecode.CONSTRUCTORCALL;
import bytecode.LOAD;

/**
 * The signature of a constructor of a Kitten class.
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

public class FixtureSignature extends CodeSignature {

	private final int pos;

	/**
	 * Constructs a signature for a constructor, given its parameters types and
	 * the class it belongs to.
	 *
	 * @param clazz
	 *            the class this constructor belongs to
	 * @param parameters
	 *            the types of the parameters of the constructor
	 * @param abstractSyntax
	 *            the abstract syntax of the declaration of this constructor
	 * @param pos the position in the code
	 */
	public FixtureSignature(ClassType clazz, FixtureDeclaration abstractSyntax, int pos) {
		// a constructor always returns void and its name is by default init
		super(clazz, VoidType.INSTANCE, TypeList.EMPTY, "fixture", abstractSyntax);
		this.pos = pos;
	}


	@Override
	public String toString() {
		return getDefiningClass() + "fixture" + pos;
	}

	/**
	 * Generates an {@code invokespecial} Java bytecode that calls this
	 * constructor. The Java {@code invokespecial} bytecode calls a method by
	 * using a hard-wired class name to look up for the method's implementation
	 * and has a receiver.
	 *
	 * @param classGen
	 *            the class generator to be used to generate the
	 *            {@code invokespecial} Java bytecode
	 * @return an {@code invokespecial} Java bytecode that calls this
	 *         constructor
	 */

	public INVOKESPECIAL createINVOKESPECIAL(GeneralClassGenerator classGen) {
		return (INVOKESPECIAL) createInvokeInstruction(classGen, Constants.INVOKESPECIAL);
	}

	/**
	 * Adds a prefix to the Kitten bytecode generated for this constructor. That
	 * is a call to the empty constructor of the superclass (if any)
	 *
	 * @param code
	 *            the code already compiled for this constructor
	 * @return {@code code} prefixed with a call to the empty constructor of the
	 *         superclass
	 */

	@Override
	protected Block addPrefixToCode(Block code) {
		// we prefix a piece of code that calls the constructor of
		// the superclass (if any)
		if (!getDefiningClass().getName().equals("Object")) {
			ClassType superclass = getDefiningClass().getSuperclass();

			code = new LOAD(0, getDefiningClass()).followedBy(new CONSTRUCTORCALL(superclass
					.constructorLookup(TypeList.EMPTY)).followedBy(code));
		}

		return code;
	}


	public void createFixture(TestClassGenerator classGen, ArrayList<MethodSignature> fixt) {
		MethodGen fixtureGen;
		// http://www.tutorialspoint.com/java/java_basic_operators.htm
		fixtureGen = new MethodGen(Constants.ACC_PRIVATE | Constants.ACC_STATIC, // private and static
				org.apache.bcel.generic.Type.VOID, // return type
				new Type[] { org.apache.bcel.generic.Type.CLASS }, // TODO
				// getParameters().toBCEL(), // parameters types, if any
				null, // parameters names: yo man, we do not give a fuck too.
						// Peace.
				getName().toString() + pos, // method's name
				classGen.getClassName(), // defining class
				classGen.generateJavaBytecode(getCode()), // bytecode of the
															// method
				classGen.getConstantPool()); // constant pool

		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		fixtureGen.setMaxStack();
		fixtureGen.setMaxLocals();
		
		fixt.add(fixtureGen);
		// we add a method to the class that we are generating
		classGen.addMethod(fixtureGen.getMethod());
		
		
	}
}