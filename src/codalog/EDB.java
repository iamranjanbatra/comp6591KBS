package codalog;

import java.util.Collection;

import codalog.engine.IndexedSet;
import codalog.interfaces.EDBInterface;

public class EDB implements EDBInterface {

	private IndexedSet<Expression, String> edb;
	
	public EDB() {
		edb = new IndexedSet<Expression, String>();
	}
	
	@Override
	public Collection<Expression> allFacts() {
		return edb;
	}

	@Override
	public void add(Expression fact) {
		edb.add(fact);
	}

	@Override
	public boolean removeAll(Collection<Expression> facts) {
		return edb.removeAll(facts);
	}

	@Override
	public Collection<Expression> getFacts(String predicate) {
		return edb.getIndexed(predicate);
	}

}
