package codalog.interfaces;

import java.util.Collection;
import java.util.Map;

import codalog.CodalogException;
import codalog.DatalogInterpreter;
import codalog.predicate.StatementFactory;


/**
 * Represents a statement that can be executed against a Jatalog database.
 * <p>
 * There are several types of statements: to insert facts, to insert rules,
 * to retract facts and to query the database.
 * </p><p>
 * Instances of Statement are created by {@link StatementFactory}.
 * </p><p>
 * Strings can be parsed to Statements through {@link DatalogInterpreter#prepareStatement(String)}
 * </p>
 * @see StatementFactory
 * @see DatalogInterpreter#prepareStatement(String)
 */
public interface PredicateInterface {
	
	/**
	 * Executes a statement against a Jatalog database.
	 * @param datalog The database against which to execute the statement.
	 * @param bindings an optional (nullable) mapping of variables to values.
	 * <p>
	 * A statement like "a(B,C)?" with bindings {@code <B = "foo", C = "bar">}
	 * is equivalent to the statement "a(foo,bar)?"
	 * </p> 
	 * @return The result of the statement.
     * <ul>
	 * <li> If null, the statement was an insert or delete that didn't produce query results.
	 * <li> If empty the query's answer is "No."
	 * <li> If a list of empty maps, then answer is "Yes."
	 * <li> Otherwise it is a list of all bindings that satisfy the query.
	 * </ul>
	 * Jatalog provides a {@link OutputUtils#answersToString(Collection)} method that can convert answers to 
	 * Strings
	 * @throws CodalogException if an error occurs in processing the statement
	 * @see OutputUtils#answersToString(Collection)
	 */
	public Collection<Map<String, String>> execute(DatalogInterpreter datalog, Map<String, String> bindings) throws CodalogException;
	
	/**
	 * Shorthand for {@code statement.execute(jatalog, null)}.
	 * @param datalog The database against which to execute the statement.
	 * @return The result of the statement
	 * @throws CodalogException if an error occurs in processing the statement
	 * @see #execute(DatalogInterpreter, Map)
	 */
	default public Collection<Map<String, String>> execute(DatalogInterpreter datalog) throws CodalogException {
		return execute(datalog, null);
	}
	
	
}
