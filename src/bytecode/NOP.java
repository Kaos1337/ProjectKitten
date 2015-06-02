package bytecode;

import javaBytecodeGenerator.GeneralClassGenerator;

import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;

/**
 * A bytecode that does not do anything.
 * <br><br>
 * ... -&gt; ...
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

public class NOP extends NonCallingSequentialBytecode {

	/**
	 * Constructs a bytecode that does not do anything.
	 */

	public NOP() {}

	/**
	 * Generates the Java bytecode corresponding to this Kitten bytecode.
	 *
	 * @param classGen the Java class generator to be used for this generation
	 * @return the Java {@code nop} bytecode
	 */

	public InstructionList generateJavaBytecode(GeneralClassGenerator classGen) {
		return new InstructionList(InstructionFactory.NOP);
	}
}