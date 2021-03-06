package codalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import codalog.engine.Engine;


/**
 * Class that represents a Datalog rule.
 * A rule has a head that is an expression and a body that is a list of expressions.
 * It takes the form {@code foo(X, Y) :- bar(X, Y), baz(X), fred(Y)}
 * @see Expression
 */
public class Rules {

	private Expression head;
	private List<Expression> body;

	/**
	 * Constructor that takes an expression as the head of the rule and a list of expressions as the body.
	 * The expressions in the body may be reordered to be able to evaluate rules correctly.
	 * @param head The head of the rule (left hand side)
	 * @param body The list of expressions that make up the body of the rule (right hand side)
	 */
	public Rules(Expression head, List<Expression> body) {
		this.setHead(head);

		this.setBody(Engine.reorderQuery(body));
	}

	/**
	 * Constructor for the fluent API that allows a variable number of expressions in the body.
	 * The expressions in the body may be reordered to be able to evaluate rules correctly.
	 * @param head The head of the rule (left hand side)
	 * @param body The list of expressions that make up the body of the rule (right hand side)
	 */
	public Rules(Expression head, Expression... body) {
		this(head, Arrays.asList(body));
	}

	/**
	 * Checks whether a rule is valid.
	 * There are a variety of reasons why a rule may not be valid:
	 * <ul>
	 * <li> Each variable in the head of the rule <i>must</i> appear in the body.
	 * <li> Each variable in the body of a rule should appear at least once in a positive (that is non-negated) expression.
	 * <li> Variables that are used in built-in predicates must appear at least once in a positive expression.
	 * </ul>
	 * @throws CodalogException if the rule is not valid, with the reason in the message.
	 */
	public void validate() throws CodalogException {

		// Check for /safety/: each variable in the body of a rule should appear at least once in a positive expression,
		// to prevent infinite results. E.g. p(X) :- not q(X, Y) is unsafe because there are an infinite number of values
		// for Y that satisfies `not q`. This is a requirement for negation - [gree] contains a nice description.
		// We also leave out variables from the built-in predicates because variables must be bound to be able to compare
		// them, i.e. a rule like `s(A, B) :- r(A,B), A > X` is invalid ('=' is an exception because it can bind variables)
		// You won't be able to tell if the variables have been bound to _numeric_ values until you actually evaluate the
		// expression, though.
		Set<String> bodyVariables = new HashSet<String>();
		for(Expression clause : getBody()) {
			if (clause.isBuiltIn()) {
				if (clause.getTerms().size() != 2)
					throw new CodalogException("Operator " + clause.getPredicate() + " must have only two operands");
				String a = clause.getTerms().get(0);
				String b = clause.getTerms().get(1);
				if (clause.getPredicate().equals("=")) {
					if (DatalogInterpreter.isVariable(a) && DatalogInterpreter.isVariable(b) && !bodyVariables.contains(a)
							&& !bodyVariables.contains(b)) {
						throw new CodalogException("Both variables of '=' are unbound in clause " + a + " = " + b);
					}
				} else {
					if (DatalogInterpreter.isVariable(a) && !bodyVariables.contains(a)) {
						throw new CodalogException("Unbound variable " + a + " in " + clause);
					}
					if (DatalogInterpreter.isVariable(b) && !bodyVariables.contains(b)) {
						throw new CodalogException("Unbound variable " + b + " in " + clause);
					}
				}
			} 
			if(clause.isNegated()) {
				for (String term : clause.getTerms()) {
					if (DatalogInterpreter.isVariable(term) && !bodyVariables.contains(term)) {
						throw new CodalogException("Variable " + term + " of rule " + toString() + " must appear in at least one positive expression");
					}
				}
			} else {
				for (String term : clause.getTerms()) {
					if (DatalogInterpreter.isVariable(term)) {
						bodyVariables.add(term);
					}
				}
			}
		}
		
		// Enforce the rule that variables in the head must appear in the body
		for (String term : getHead().getTerms()) {
			if (!DatalogInterpreter.isVariable(term)) {
				throw new CodalogException("Constant " + term + " in head of rule " + toString());
			}
			if (!bodyVariables.contains(term)) {
				throw new CodalogException(
						"Variables " + term + " from the head of rule " + toString() + " must appear in the body");
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getHead());
		sb.append(" :- ");
		for(int i = 0; i < getBody().size(); i++) {
			sb.append(getBody().get(i));
			if(i < getBody().size() - 1)
				sb.append(", ");
		}
		return sb.toString();
	}

	/**
	 * Creates a new Rule with all variables from bindings substituted.
	 * eg. a Rule {@code p(X,Y) :- q(X),q(Y),r(X,Y)} with bindings {X:aa}
	 * will result in a new Rule {@code p(aa,Y) :- q(aa),q(Y),r(aa,Y)}
	 * @param bindings The bindings to substitute.
	 * @return the Rule with the substituted bindings. 
	 */
	public Rules substitute(Map<String, String> bindings) {
		List<Expression> subsBody = new ArrayList<>();
		for(Expression e : getBody()) {
			subsBody.add(e.substitute(bindings));
		}
		return new Rules(this.getHead().substitute(bindings), subsBody);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Rules)) {
			return false;
		}
		Rules that = ((Rules) other);
		if (!this.getHead().equals(that.getHead())) {
			return false;
		}
		if (this.getBody().size() != that.getBody().size()) {
			return false;
		}
		for (Expression e : this.getBody()) {
			if (!that.getBody().contains(e)) {
				return false;
			}
		}
		return true;
	}

	public Expression getHead() {
		return head;
	}

	public void setHead(Expression head) {
		this.head = head;
	}

	public List<Expression> getBody() {
		return body;
	}

	public void setBody(List<Expression> body) {
		this.body = body;
	}
}