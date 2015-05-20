package absyn;

import java.io.FileWriter;

import semantical.TypeChecker;
import types.ClassType;
import types.FixtureSignature;
import types.TypeList;
import types.VoidType;

/**
 * A node of abstract syntax representing the declaration of a fixture
 * of a Kitten class.
 *
 * @author Comencini Marco, Marretta Francesco, Zuliani Davide
 */

public class FixtureDeclaration extends CodeDeclaration {

	/**
	 * Constructs the abstract syntax of a fixture declaration.
	 *
	 * @param pos the starting position in the source file of
	 *            the concrete syntax represented by this abstract syntax
	 * @param body the abstract syntax of the body of the constructor
	 * @param next the abstract syntax of the declaration of the
	 *             subsequent class member, if any
	 */

	public FixtureDeclaration(int pos, Command body, ClassMemberDeclaration next) {
		super(pos, null, body, next);
	}

	/**
	 * Yields the signature of this fixture declaration.
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
	 * and those representing the body of the constructor.
	 *
	 * @param where the file where the dot representation must be written
	 */

	protected void toDotAux(FileWriter where) throws java.io.IOException {

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
		FixtureSignature fs = new FixtureSignature(clazz, this);
		clazz.addFixture(fs);
		setSignature(fs);
	}
	
	/**
	 * Type-checks this fixture declaration. Namely, it builds a type-checker
	 * whose only variable in scope is {@code this} of the defining class of the
	 * fixture.
	 * It then type-checks the body of the fixture in that type-checker
	 * and checks that it does not contain any dead-code.
	 *
	 * @param clazz the semantical type of the class where this fixture occurs.
	 */

	@Override
	protected void typeCheckAux(ClassType clazz) {

		TypeChecker checker = new TypeChecker(VoidType.INSTANCE, clazz.getErrorMsg());
		checker = checker.putVar("this", clazz);

		getBody().typeCheck(checker);

		/* Secondo me non serve - Davide
		 * if (! getBody().checkForDeadcode())
			error(checker, "fixture deadcode");*/
		
	}
}