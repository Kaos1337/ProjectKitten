package absyn;

import java.io.FileWriter;

import semantical.TypeChecker;
import types.ClassType;
import types.ConstructorSignature;
import types.TypeList;
import types.VoidType;

/**
 * A node of abstract syntax representing the declaration of a test
 * of a Kitten class.
 *
 * @author Comencini Marco, Marretta Francesco, Zuliani Davide
 */


public class TestDeclaration extends CodeDeclaration {

	private final String name;
	
	/**
	 * Constructs the abstract syntax of a constructor declaration.
	 *
	 * @param pos the starting position in the source file of
	 *            the concrete syntax represented by this abstract syntax
	 * @param id the name of the test
	 * @param body the abstract syntax of the body of the constructor
	 * @param next the abstract syntax of the declaration of the
	 *             subsequent class member, if any
	 */
	
	public TestDeclaration(int pos, String id, Command body, ClassMemberDeclaration next) {
		super(pos, null, body, next);
		this.name = id;
	}

	/**
	 * Yields the signature of this constructor declaration.
	 *
	 * @return the signature of this constructor declaration.
	 *         Yields {@code null} if type-checking has not been performed yet
	 */

	/*@Override
	public ConstructorSignature getSignature() {
		return (ConstructorSignature) super.getSignature();
	}*/

	/**
	 * Adds arcs between the dot node for this piece of abstract syntax
	 * and those representing the name and body of the test.
	 *
	 * @param where the file where the dot representation must be written
	 */

	protected void toDotAux(FileWriter where) throws java.io.IOException {
		
		linkToNode("name", toDot(name, where), where);

		linkToNode("body", getBody().toDot(where), where);
	}

	/**
	 * Adds the signature of this constructor declaration to the given class.
	 *
	 * @param clazz the class where the signature of this constructor
	 *              declaration must be added
	 */

	@Override
	protected void addTo(ClassType clazz) {
		
	}

	//TODO fdfdfd
	/**
	 * Type-checks this constructor declaration. Namely, it builds a type-checker
	 * whose only variable in scope is {@code this} of the defining class of the
	 * constructor, and where only return instructions of type {@code void} are allowed.
	 * It then type-checks the body of the constructor in that type-checker
	 * and checks that it does not contain any dead-code.
	 *
	 * @param clazz the semantical type of the class where this constructor occurs.
	 */

	@Override
	protected void typeCheckAux(ClassType clazz) {
		FormalParameters formals = getFormals();

		TypeChecker checker = new TypeChecker(VoidType.INSTANCE, clazz.getErrorMsg());
		checker = checker.putVar("this", clazz);
		// we enrich the type-checker with the formal parameters
		if (formals != null)
			checker = formals.typeCheck(checker);

		// we type-check the body of the constructor in the resulting type-checker
		getBody().typeCheck(checker);

		// we check that there is no dead-code in the body of the constructor
		getBody().checkForDeadcode();

		// if our superclass exists, it must contain an empty constructor,
		// that will be chained to this constructor
		if (clazz.getSuperclass() != null && clazz.getSuperclass().constructorLookup(TypeList.EMPTY) == null)
			error(checker, clazz.getSuperclass() + " has no empty constructor");

		// constructors return nothing, so that we do not check whether
		// a return statement is always present at the end of every
		// syntactical execution path in the body of a constructor
	}
}