package types;

import javaBytecodeGenerator.GeneralClassGenerator;
import javaBytecodeGenerator.TestClassGenerator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import bytecode.NEWSTRING;
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

	public INVOKESTATIC createINVOKESTATIC(GeneralClassGenerator classGen) {
		return (INVOKESTATIC) createInvokeInstruction(classGen, Constants.INVOKESTATIC);
	}

	public void createTest(TestClassGenerator classGen) {

		MethodGen testGen;
		// http://www.tutorialspoint.com/java/java_basic_operators.htm
		
		InstructionList testbody = new InstructionList();
		testbody = classGen.generateJavaBytecode(getCode());
		testbody.insert(new NEWSTRING("").generateJavaBytecode(classGen));
		
		/*for(int i = 0; i <= testbody.getInstructionPositions().length; i++){
			Instruction in = testbody.findHandle(testbody.getInstructionPositions()[i]).getInstruction();
			System.out.println(Instruction.getComparator().equals(in, InstructionFactory.RETURN));
			if (Instruction.getComparator().equals(in, InstructionFactory.RETURN))
			in = InstructionFactory.ARETURN;
		}*/
		InstructionList testfinal = new InstructionList();
		for(Instruction i : testbody.getInstructions()){
			if(Instruction.getComparator().equals(i, InstructionFactory.RETURN))
				testfinal.append(InstructionFactory.ARETURN);
			else
				testfinal.append(i);
		}
		System.out.println("=======================");
		
		for(Instruction i : testbody.getInstructions()){
			System.out.println(Instruction.getComparator().equals(i, InstructionFactory.RETURN));
			if (Instruction.getComparator().equals(i, InstructionFactory.RETURN));
		}
		
		System.out.println("++++++++++++++++++++++++");
		
		//ClassType.mk(runTime.String.class.getSimpleName()).toBCEL(),
		testGen = new MethodGen(Constants.ACC_PRIVATE | Constants.ACC_STATIC, // private and static
				//Type.getReturnType("String"), // return type
				ClassType.mk(runTime.String.class.getSimpleName()).toBCEL(),
				new Type[] { this.getDefiningClass().toBCEL() }, // parameters types, if any
				null, // parameters names: we do not care
				getName(), // method's name
				classGen.getClassName(), // defining class
				testbody, // bytecode of the method
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