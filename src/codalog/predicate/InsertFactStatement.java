package codalog.predicate;

import java.util.Collection;
import java.util.Map;

import codalog.CodalogException;
import codalog.Expression;
import codalog.DatalogInterpreter;
import codalog.interfaces.PredicateInterface;


public class InsertFactStatement implements PredicateInterface {

	private final Expression fact;
	
	InsertFactStatement(Expression fact) {
		this.fact = fact;
	}

	@Override
	public Collection<Map<String, String>> execute(DatalogInterpreter datalog, Map<String, String> bindings) throws CodalogException {
		Expression newFact;
		if(bindings != null) {
			newFact = fact.substitute(bindings);
		} else {
			newFact = fact;
		}
		datalog.fact(newFact);
		return null;
	}

}
