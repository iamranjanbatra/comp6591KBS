package codalog.output;

import java.util.Collection;
import java.util.Map;

import codalog.interfaces.QueryInterface;
import codalog.interfaces.PredicateInterface;

public class DefaultQueryOutput implements QueryInterface {

    @Override
    public void writeResult(PredicateInterface statement, Collection<Map<String, String>> answers) {
		System.out.println(statement.toString());
		if (!answers.isEmpty()) {
			if (answers.iterator().next().isEmpty()) {
				System.out.println("  Yes.");
			} else {
				for (Map<String, String> answer : answers) {
					System.out.println("  " + OutputUtils.bindingsToString(answer));
				}
			}
		} else {
			System.out.println("  No.");
		}
	}

}