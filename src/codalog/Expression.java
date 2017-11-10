package codalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import codalog.engine.Indexable;

public class Expression implements Indexable<String> {

    private String predicate;
    private List<String> terms;

    protected boolean negated = false;

    public Expression(String predicate, List<String> terms) {
        this.predicate = predicate;
        // I've seen both versions of the symbol for not equals being used, so I allow
        // both, but we convert to "<>" internally to simplify matters later.
        if(this.predicate.equals("!=")) {
            this.predicate = "<>";
        }
        this.terms = terms;
    }

   
    public Expression(String predicate, String... terms) {
        this(predicate, Arrays.asList(terms));
    }

    
    public int arity() {
        return terms.size();
    }

    public boolean isGround() {
        for(String term : terms) {
            if(DatalogInterpreter.isVariable(term))
                return false;
        }
        return true;
    }

    public boolean isNegated() {
        return negated;
    }

    public boolean isBuiltIn() {
        char op = predicate.charAt(0);
        return !Character.isLetterOrDigit(op) && op != '\"';
    }

    /**
     * Unifies {@code this} expression with another expression.
     * @param that The expression to unify with
     * @param bindings The bindings of variables to values after unification
     * @return true if the expressions unify.
     */
    public boolean unify(Expression that, Map<String, String> bindings) {
        if(!this.predicate.equals(that.predicate) || this.arity() != that.arity()) {
            return false;
        }
        for(int i = 0; i < this.arity(); i++) {
            String term1 = this.terms.get(i);
            String term2 = that.terms.get(i);
            if(DatalogInterpreter.isVariable(term1)) {
                if(!term1.equals(term2)) {
                    if(!bindings.containsKey(term1)) {
                        bindings.put(term1, term2);
                    } else if (!bindings.get(term1).equals(term2)) {
                        return false;
                    }
                }
            } else if(DatalogInterpreter.isVariable(term2)) {
                if(!bindings.containsKey(term2)) {
                    bindings.put(term2, term1);
                } else if (!bindings.get(term2).equals(term1)) {
                    return false;
                }
            } else if (!term1.equals(term2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Substitutes the variables in this expression with bindings from a unification.
     * @param bindings The bindings to substitute.
     * @return A new expression with the variables replaced with the values in bindings.
     */
    public Expression substitute(Map<String, String> bindings) {
        // that.terms.add() below doesn't work without the new ArrayList()
        Expression that = new Expression(this.predicate, new ArrayList<>());
        that.negated = negated;
        for(String term : this.terms) {
            String value;
            if(DatalogInterpreter.isVariable(term)) {
                value = bindings.get(term);
                if(value == null) {
                    value = term;
                }
            } else {
                value = term;
            }
            that.terms.add(value);
        }
        return that;
    }

    /**
     * Evaluates a built-in predicate. 
     * @param bindings A map of variable bindings 
     * @return true if the operator matched.
     */
    public boolean evalBuiltIn(Map<String, String> bindings) {
    	// This method may throw a RuntimeException for a variety of possible reasons, but 
    	// these conditions are supposed to have been caught earlier in the chain by 
    	// methods such as Rule#validate().
    	// The RuntimeException is a requirement of using the Streams API.
    	String term1 = terms.get(0);
        if(DatalogInterpreter.isVariable(term1) && bindings.containsKey(term1))
            term1 = bindings.get(term1);
        String term2 = terms.get(1);
        if(DatalogInterpreter.isVariable(term2) && bindings.containsKey(term2))
            term2 = bindings.get(term2);
        if(predicate.equals("=")) {
            // '=' is special
            if(DatalogInterpreter.isVariable(term1)) {
                if(DatalogInterpreter.isVariable(term2)) {
                	// Rule#validate() was supposed to catch this condition
                    throw new RuntimeException("Both operands of '=' are unbound (" + term1 + ", " + term2 + ") in evaluation of " + this);
                }
                bindings.put(term1, term2);
                return true;
            } else if(DatalogInterpreter.isVariable(term2)) {
                bindings.put(term2, term1);
                return true;
            } else {
				if (Parser.tryParseDouble(term1) && Parser.tryParseDouble(term2)) {
					double d1 = Double.parseDouble(term1);
					double d2 = Double.parseDouble(term2);
					return d1 == d2;
				} else {
					return term1.equals(term2);
				}
            }
        } else {
            try {
            	
            	// These errors can be detected in the validate method:
                if(DatalogInterpreter.isVariable(term1) || DatalogInterpreter.isVariable(term2)) {
                	// Rule#validate() was supposed to catch this condition
                	throw new RuntimeException("Unbound variable in evaluation of " + this);
                }
                
                if(predicate.equals("<>")) {
                    // '<>' is also a bit special
                    if(Parser.tryParseDouble(term1) && Parser.tryParseDouble(term2)) {
                            double d1 = Double.parseDouble(term1);
                            double d2 = Double.parseDouble(term2);
                            return d1 != d2;
                    } else {
                        return !term1.equals(term2);
                    }
                } else {
                    // Ordinary comparison operator
                	// If the term doesn't parse to a double it gets treated as 0.0.
                	double d1 = 0.0, d2 = 0.0;
                    if(Parser.tryParseDouble(term1)) {
                    	d1 = Double.parseDouble(term1);
                    }
                    if(Parser.tryParseDouble(term2)) {
                    	d2 = Double.parseDouble(term2);
                    }
                    switch(predicate) {
                        case "<": return d1 < d2;
                        case "<=": return d1 <= d2;
                        case ">": return d1 > d2;
                        case ">=": return d1 >= d2;
                    }
                }
            } catch (NumberFormatException e) {
                // You found a way to write a double in a way that the regex in tryParseDouble() doesn't understand.
                throw new RuntimeException("tryParseDouble() experienced a false positive!?", e);
            }
        }
        throw new RuntimeException("Unimplemented built-in predicate " + predicate);
    }
    
    public String getPredicate() {
		return predicate;
	}
    
    public List<String> getTerms() {
		return terms;
	}

    @Override
    public boolean equals(Object other) {
        if(other == null || !(other instanceof Expression)) {
            return false;
        }
        Expression that = ((Expression) other);
        if(!this.predicate.equals(that.predicate)) {
            return false;
        }
        if(arity() != that.arity() || negated != that.negated) {
            return false;
        }
        for(int i = 0; i < terms.size(); i++) {
            if(!terms.get(i).equals(that.terms.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = predicate.hashCode();
        for(String term : terms) {
            hash += term.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(isNegated()) {
            sb.append("not ");
        }
        if(isBuiltIn()) {
            termToString(sb, terms.get(0));
            sb.append(" ").append(predicate).append(" ");
            termToString(sb, terms.get(1));
        } else {
            sb.append(predicate).append('(');
            for(int i = 0; i < terms.size(); i++) {
                String term = terms.get(i);
                termToString(sb, term);
                if(i < terms.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(')');
        }
        return sb.toString();
    }

    /* Converts a term to a string. If it started as a quoted string it is now enclosed in quotes,
     * and other quotes escaped.
     * caveat: You're going to have trouble if you have other special characters in your strings */
    private static StringBuilder termToString(StringBuilder sb, String term) {
        if(term.startsWith("\""))
            sb.append('"').append(term.substring(1).replaceAll("\"", "\\\\\"")).append('"');
        else
            sb.append(term);
        return sb;
    }

	/**
	 * Helper method for creating a new expression.
	 * This method is part of the fluent API intended for {@code import static}
	 * @param predicate The predicate of the expression.
	 * @param terms The terms of the expression.
	 * @return the new expression
	 */
	public static Expression expr(String predicate, String... terms) {
		return new Expression(predicate, terms);
	}

    /**
     * Static method for constructing negated expressions in the fluent API.
     * Negated expressions are of the form {@code not predicate(term1, term2,...)}.
     * @param predicate The predicate of the expression
     * @param terms The terms of the expression
     * @return The negated expression
     */
    public static Expression not(String predicate, String... terms) {
        Expression e = new Expression(predicate, terms);
        e.negated = true;
        return e;
    }
    
    /**
     * Static helper method for constructing an expression {@code a = b} in the fluent API.
     * @param a the left hand side of the operator
     * @param b the right hand side of the operator
     * @return the expression
     */
    public static Expression eq(String a, String b) {
        return new Expression("=", a, b);
    }
    
    /**
     * Static helper method for constructing an expression {@code a <> b} in the fluent API.
     * @param a the left hand side of the operator
     * @param b the right hand side of the operator
     * @return the expression
     */
    public static Expression ne(String a, String b) {
        return new Expression("<>", a, b);
    }
    
    /**
     * Static helper method for constructing an expression {@code a < b} in the fluent API.
     * @param a the left hand side of the operator
     * @param b the right hand side of the operator
     * @return the expression
     */
    public static Expression lt(String a, String b) {
        return new Expression("<", a, b);
    }
    
    /**
     * Static helper method for constructing an expression {@code a <= b} in the fluent API.
     * @param a the left hand side of the operator
     * @param b the right hand side of the operator
     * @return the expression
     */
    public static Expression le(String a, String b) {
        return new Expression("<=", a, b);
    }
    
    /**
     * Static helper method for constructing an expression {@code a > b} in the fluent API.
     * @param a the left hand side of the operator
     * @param b the right hand side of the operator
     * @return the expression
     */
    public static Expression gt(String a, String b) {
        return new Expression(">", a, b);
    }
    
    /**
     * Static helper method for constructing an expression {@code a >= b} in the fluent API.
     * @param a the left hand side of the operator
     * @param b the right hand side of the operator
     * @return the expression
     */
    public static Expression ge(String a, String b) {
        return new Expression(">=", a, b);
    }

	@Override
	public String index() {		
		return predicate;
	}

	/**
	 * Validates a fact in the IDB.
	 * Valid facts must be ground and cannot be negative.
	 * @throws CodalogException if the fact is invalid.
	 */
	public void validFact() throws CodalogException {
		if(!isGround()) {
            throw new CodalogException("Fact " + this + " is not ground");
        } else if(isNegated()) {
            throw new CodalogException("Fact " + this + " is negated");
        }
	}
}