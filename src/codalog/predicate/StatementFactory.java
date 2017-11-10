package codalog.predicate;

import java.util.List;

import codalog.Expression;
import codalog.Rules;
import codalog.interfaces.PredicateInterface;

/**
 * Provides factory methods for building Statement instances for
 * use with the fluent API.
 * <p>
 * {@link DatalogInterpreter#prepareStatement(String)} can be used to parse
 * Strings to statement object.
 * </p>
 * @see PredicateInterface
 * @see PredicateInterface#execute(DatalogInterpreter, java.util.Map)
 * @see DatalogInterpreter#prepareStatement(String)
 */
public class StatementFactory {
	
	/**
	 * Creates a statement to query the database.
	 * @param goals The goals of the query
	 * @return A statement that will query the database for the given goals.
	 */
	public static PredicateInterface query(List<Expression> goals) {
		return new QueryStatement(goals);
	}
	
	/**
	 * Creates a statement that will insert a fact into the EDB.
	 * @param fact The fact to insert
	 * @return A statement that will insert the given fact into the database.
	 */
	public static PredicateInterface insertFact(Expression fact) {
		return new InsertFactStatement(fact);
	}
	
	/**
	 * Creates a statement that will insert a rule into the IDB.
	 * @param rule The rule to insert
	 * @return A statement that will insert the given rule into the database.
	 */
	public static PredicateInterface insertRule(Rules rule) {
		return new InsertRuleStatement(rule);
	}
	
	/**
	 * Creates a statement that will delete facts from the database.
	 * @param goals The goals of the facts to delete
	 * @return A statement that will delete facts matching the goals from the database.
	 */
	public static PredicateInterface deleteFacts(List<Expression> goals) {
		return new DeleteStatement(goals);		
	}
}
