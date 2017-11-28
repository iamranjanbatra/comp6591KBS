package codalog;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import codalog.engine.BasicEngine;
import codalog.engine.Engine;
import codalog.interfaces.EDBInterface;
import codalog.interfaces.QueryInterface;
import codalog.interfaces.PredicateInterface;
public class DatalogInterpreter {

	private EDBInterface edbProvider;   // Facts
    private Collection<Rules> idb;      // Rules
    
    private Engine engine = new BasicEngine();
	private boolean isNaive = false;
    
    public DatalogInterpreter() {
        this.edbProvider = new EDB();
        this.idb = new ArrayList<>();
    }

     static boolean isVariable(String term) {
        return Character.isUpperCase(term.charAt(0));
    }
    
	private static StreamTokenizer getTokenizer(Reader reader) throws IOException {
		StreamTokenizer scan = new StreamTokenizer(reader);
		scan.ordinaryChar('.');
		scan.commentChar('%'); 
		scan.quoteChar('"');
		scan.quoteChar('\'');
		return scan;
	}
    
    private Collection<Map<String, String>> executeSingleStatement(StreamTokenizer scan, Reader reader, QueryInterface output) throws CodalogException {
    	PredicateInterface statement = Parser.parseStmt(scan);
		try {
			Collection<Map<String, String>> answers = statement.execute(this);
			if (answers != null && output != null) {
				output.writeResult(statement, answers);
			}
			return answers;
		} catch (CodalogException e) {
			throw new CodalogException("[line " + scan.lineno() + "] Error executing statement", e);
		}
    }


    public Collection<Map<String, String>> executeAll(Reader reader, QueryInterface output) throws CodalogException {
        try {
            StreamTokenizer scan = getTokenizer(reader);
            
            // Tracks the last query's answers
            Collection<Map<String, String>> answers = null;
            scan.nextToken();
            while(scan.ttype != StreamTokenizer.TT_EOF) {
                scan.pushBack();
                answers = executeSingleStatement(scan, reader, output);
                scan.nextToken();
            }            
            return answers;
        } catch (IOException e) {
            throw new CodalogException(e);
        }
    }

    public Collection<Map<String, String>> executeAll(String statements) throws CodalogException {
        StringReader reader = new StringReader(statements);
        return executeAll(reader, null);
    }
    
	public Collection<Map<String, String>> query(List<Expression> goals, Map<String, String> bindings)
			throws CodalogException {
		return engine.query(this, goals, bindings);
	}

	public Collection<Map<String, String>> query(List<Expression> goals) throws CodalogException {
		return query(goals, null);
	}
	public Collection<Map<String, String>> query(Expression... goals) throws CodalogException {
		return query(Arrays.asList(goals), null);
	}
    public void validate() throws CodalogException {
        for(Rules rule : idb) {
            rule.validate();
        }
        Engine.computeStratification(idb);
        for (Expression fact : edbProvider.allFacts()) {
			fact.validFact();
		}
    }

    public DatalogInterpreter rule(Expression head, Expression... body) throws CodalogException {
        Rules newRule = new Rules(head, body);
        return rule(newRule);
    }
    
    public DatalogInterpreter rule(Rules newRule) throws CodalogException {
        newRule.validate();
        idb.add(newRule);
        return this;
    }

   public DatalogInterpreter fact(String predicate, String... terms) throws CodalogException {
        return fact(new Expression(predicate, terms));
    }
   
   public DatalogInterpreter fact(Expression newFact) throws CodalogException {
        if(!newFact.isGround()) {
            throw new CodalogException("Facts must be ground: " + newFact);
        }
        if(newFact.isNegated()) {
            throw new CodalogException("Facts cannot be negated: " + newFact);
        }
        edbProvider.add(newFact);
        return this;
    }

   public boolean delete(Expression... goals) throws CodalogException {
        return delete(Arrays.asList(goals), null);
    }

   public boolean delete(List<Expression> goals, Map<String, String> bindings) throws CodalogException {
        Collection<Map<String, String>> answers = query(goals, bindings);
        List<Expression> facts = answers.stream()
            // and substitute the answer on each goal
            .flatMap(answer -> goals.stream().map(goal -> goal.substitute(answer)))
            .collect(Collectors.toList());
        return edbProvider.removeAll(facts);
    }
   
   public boolean delete(List<Expression> goals) throws CodalogException {
    	return delete(goals, null);
    }
    
   public static PredicateInterface prepareStatement(String statement) throws CodalogException {
		try {
        	StringReader reader = new StringReader(statement);
            StreamTokenizer scan = getTokenizer(reader);
            return Parser.parseStmt(scan);
        } catch (IOException e) {
            throw new CodalogException(e);
        }
    }

   public static Map<String, String> makeBindings(Object... kvPairs) throws CodalogException {
		Map<String, String> mapping = new HashMap<String, String>();
		if (kvPairs.length % 2 != 0) {
			throw new CodalogException("kvPairs must be even");
		}
		for (int i = 0; i < kvPairs.length / 2; i++) {
			String k = kvPairs[i * 2].toString();
			String v = kvPairs[i * 2 + 1].toString();
			mapping.put(k, v);
		}
		return mapping;
	}
        
    @Override
	public String toString() {
    	
    	StringBuilder sb = new StringBuilder("% Facts:\n");
        for(Expression fact : edbProvider.allFacts()) {
            sb.append(fact).append(".\n");
        }
        sb.append("\n% Rules:\n");
        for(Rules rule : idb) {
            sb.append(rule).append(".\n");
        }
        return sb.toString();
    }
    
    @Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof DatalogInterpreter)) {
			return false;
		}
		DatalogInterpreter that = ((DatalogInterpreter) obj);
		if(this.idb.size() != that.idb.size()) {
			return false;
		}
		for(Rules rule : idb) {
			if(!that.idb.contains(rule))
				return false;
		}

		Collection<Expression> theseFacts = this.edbProvider.allFacts();
		Collection<Expression> thoseFacts = that.edbProvider.allFacts();
		
		if(theseFacts.size() != thoseFacts.size()) {
			return false;
		}
		for(Expression fact : theseFacts) {
			if(!thoseFacts.contains(fact))
				return false;
		}
		
		return true;
    }

    public EDBInterface getEdbProvider() {
		return edbProvider;
	}
	
	public void setEdbProvider(EDBInterface edbProvider) {
		this.edbProvider = edbProvider;
	}

	public Collection<Rules> getIdb() {
		return idb;
	}
	
	public boolean getIsNaive() {
		return isNaive;
	}

	public void setIsNaive(boolean naive) {
		this.isNaive  = naive;
	}
}
