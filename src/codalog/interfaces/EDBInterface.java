package codalog.interfaces;

import java.util.Collection;

import codalog.EDB;
import codalog.Expression;
import codalog.DatalogInterpreter;

/**
 * The EdbProvider allows the EDB from Jatalog's perspective to be abstracted away from the actual
 * storage mechanism.
 * <p>
 * The purpose is to allow different sources for the EDB data, such as CSV or XML files or even a SQL 
 * or NoSQL database.
 * </p><p>
 * Jatalog uses a {@link EDB} by default, which simply stores facts in memory, but it 
 * can be changed through the {@link DatalogInterpreter#setEdbProvider(EdbProvider)} method. 
 * </p> 
 * @see EDB
 */
public interface EDBInterface {

	/**
	 * Retrieves a {@code Collection} of all the facts in the database.
	 * @return All the facts in the EDB
	 */
	public Collection<Expression> allFacts();
	
	/**
	 * Adds a fact to the EDB database.
	 * @param fact The fact to add
	 */
	public void add(Expression fact);
	
	/**
	 * Removes facts from the database
	 * @param facts the facts to remove
	 * @return true if facts were removed
	 */
	public boolean removeAll(Collection<Expression> facts);

	/**
	 * Retrieves all the facts in the database that match specific predicate.
	 * @param predicate The predicate of the facts to be retrieved.
	 * @return A collection of facts matching the {@code predicate}
	 */
	public Collection<Expression> getFacts(String predicate);
}
