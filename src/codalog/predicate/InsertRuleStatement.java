package codalog.predicate;

import java.util.Collection;
import java.util.Map;

import codalog.Rules;
import codalog.interfaces.PredicateInterface;
import codalog.CodalogException;
import codalog.DatalogInterpreter;

public class InsertRuleStatement implements PredicateInterface {
	
	private final Rules rule;
	
	InsertRuleStatement(Rules rule) {
		this.rule = rule;
	}

	@Override
	public Collection<Map<String, String>> execute(DatalogInterpreter datalog, Map<String, String> bindings) throws CodalogException {
		Rules newRule;
		if(bindings != null) {
			newRule = rule.substitute(bindings);
		} else {
			newRule = rule;
		}
		datalog.rule(newRule);
		return null;
	}

}
