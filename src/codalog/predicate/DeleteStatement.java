package codalog.predicate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import codalog.Expression;
import codalog.CodalogException;
import codalog.Expression;
import codalog.DatalogInterpreter;
import codalog.interfaces.PredicateInterface;

class DeleteStatement implements PredicateInterface {

	private List<Expression> goals;
	
	DeleteStatement(List<Expression> goals) {
		this.goals = goals;
	}

	@Override
	public Collection<Map<String, String>> execute(DatalogInterpreter datalog, Map<String, String> bindings) throws CodalogException {
		datalog.delete(goals, bindings);
		return null;
	}

}
