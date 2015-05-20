package absyn;

import java.io.FileWriter;

import semantical.TypeChecker;
import translation.Block;
import types.BooleanType;
import types.CodeSignature;
import types.Type;
import bytecode.Bytecode;
import bytecode.NEWSTRING;
import bytecode.RETURN;
import bytecode.VIRTUALCALL;

/**
 * A node of abstract syntax representing a {@code assert} command.
 *
 * @author Comencini Marco, Marretta Francesco, Zuliani Davide
 */

public class Assert extends Command {

	/**
	 * The abstract syntax of the expression whose value is returned. It might be {@code null}.
	 */

	private Expression condition;

	/**
	 * Constructs the abstract syntax of a {@code assert} command.
	 *
	 * @param pos the position in the source file where it starts
	 *            the concrete syntax represented by this abstract syntax
	 * @param expression l'espressione booleana da valutare
	 */

	public Assert(int pos, Expression condition) {
		super(pos);

		this.condition = condition;
	}

	/**
	 * Adds abstract syntax class-specific information in the dot file
	 * representing the abstract syntax of the {@code assert} command.
	 * This amounts to adding an arc from the node for the {@code assert}
	 * command to the abstract syntax for {@link #returned}.
	 *
	 * @param where the file where the dot representation must be written
	 */

	@Override
	protected void toDotAux(FileWriter where) throws java.io.IOException {
			linkToNode("condition", condition.toDot(where), where);
	}

	/**
	 * Performs the type-checking of the {@code return} command
	 * by using a given type-checker. It type-checks the expression whose
	 * value is returned, if it is not {@code null}, and checks that
	 * its static type can be assigned to the type expected by the
	 * type-checker for the {@code return} instructions.
	 * If no returned expression is present, then the it checks that
	 * the type-checker expects {@code void} as a
	 * return type. It returns the same type-checker passed as a parameter.
	 *
	 * @param checker the type-checker to be used for type-checking
	 * @return the type-checker {@code checker} itself
	 */
	@Override
	protected TypeChecker typeCheckAux(TypeChecker checker) {
		
		if( ! ( checker.isAssertAllowed() ) )
			error("Assert not allowed here");
		
		if (condition == null)
			error("Assert: boolean expression expected");
		else{
			Type t = condition.typeCheck(checker);
			
			if( t != BooleanType.INSTANCE)
				error("Assert must contains a boolean expression");
		}
		return checker;
	}

	/**
	 * Checks that this {@code assert} command does not contain <i>dead-code</i>, that is,
	 * commands that can never be executed. This is always true for {@code assert} commands.
	 *
	 * @return true, since this command doesn't contain commands. 
	 */
	@Override
	public boolean checkForDeadcode() {
		return true;
	}


	/**
	 * Translates this command into intermediate Kitten bytecode. Namely,
	 * it returns a code which starts with the evaluation of {@link #returned},
	 * if any, and continues with a {@code return} bytecode for the type returned
	 * by the current method.
	 *
	 * @param where the method or constructor where this expression occurs
	 * @param continuation the continuation to be executed after this command
	 * @return the code executing this command and then the {@code continuation}
	 */

	@Override
	public Block translate(CodeSignature where, Block continuation) {
		// we get the type which must be returned by this the current method
		Type returnType = getTypeChecker().getReturnType();

		// we get a code which is made of a block containing the bytecode return
		continuation = new Block(new RETURN(returnType));
		Bytecode falso = new NEWSTRING("test fallito @file.kit:"+where.getName());//TODO
		Bytecode j = new VIRTUALCALL(String.,null);
		// if there is an initialising expression, we translate it
		if (condition != null)
			//continuation = condition.translateAs(where, returnType, continuation);
			
			continuation = condition.translateAsTest(where, continuation, continuation.prefixedBy(falso) );
		return continuation;
	}
	
	
}