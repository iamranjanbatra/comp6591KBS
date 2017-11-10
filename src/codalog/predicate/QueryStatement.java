package codalog.predicate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import codalog.CodalogException;
import codalog.Expression;
import codalog.DatalogInterpreter;
import codalog.interfaces.PredicateInterface;

class QueryStatement implements PredicateInterface {

	private List<Expression> goals;
	
	QueryStatement(List<Expression> goals) {
		this.goals = goals;
	}

	@Override
	public Collection<Map<String, String>> execute(DatalogInterpreter datalog, Map<String, String> bindings) throws CodalogException {
		return datalog.query(goals, bindings);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < goals.size(); i++) {
			sb.append(goals.get(i).toString());
			if (i < goals.size() - 1)
				sb.append(", ");
		}
		sb.append("?");
		return sb.toString();
	}
}
