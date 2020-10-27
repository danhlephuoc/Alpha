package at.ac.tuwien.kr.alpha.common.terms;

import at.ac.tuwien.kr.alpha.common.Interner;
import at.ac.tuwien.kr.alpha.grounder.Substitution;

import java.util.*;

import static at.ac.tuwien.kr.alpha.Util.join;

/**
 * Copyright (c) 2016-2017, the Alpha Team.
 */
public class FunctionTerm extends TermImpl {
	private static final Interner<FunctionTerm> INTERNER = new Interner<>();

	private final String symbol;
	private final List<? extends TermImpl> terms;
	private final boolean ground;

	private FunctionTerm(String symbol, List<? extends TermImpl> terms) {
		if (symbol == null) {
			throw new IllegalArgumentException();
		}

		this.symbol = symbol;
		this.terms = Collections.unmodifiableList(terms);

		boolean ground = true;
		for (Term term : terms) {
			if (!term.isGround()) {
				ground = false;
				break;
			}
		}
		this.ground = ground;
	}

	public static FunctionTerm getInstance(String functionSymbol, List<? extends TermImpl> termList) {
		return INTERNER.intern(new FunctionTerm(functionSymbol, termList));
	}

	public static FunctionTerm getInstance(String functionSymbol, TermImpl... terms) {
		return getInstance(functionSymbol, Arrays.asList(terms));
	}

	public List<? extends TermImpl> getTerms() {
		return terms;
	}

	public String getSymbol() {
		return symbol;
	}

	@Override
	public boolean isGround() {
		return ground;
	}

	@Override
	public List<VariableTerm> getOccurringVariables() {
		LinkedList<VariableTerm> vars = new LinkedList<>();
		for (TermImpl term : terms) {
			vars.addAll(term.getOccurringVariables());
		}
		return vars;
	}

	@Override
	public FunctionTerm substitute(Substitution substitution) {
		List<TermImpl> groundTermList = new ArrayList<>(terms.size());
		for (TermImpl term : terms) {
			groundTermList.add(term.substitute(substitution));
		}
		return FunctionTerm.getInstance(symbol, groundTermList);
	}

	@Override
	public String toString() {
		if (terms.isEmpty()) {
			return symbol;
		}

		return join(symbol + "(", terms, ")");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FunctionTerm that = (FunctionTerm) o;

		if (!symbol.equals(that.symbol)) {
			return false;
		}
		return terms.equals(that.terms);
	}

	@Override
	public int hashCode() {
		return 31 * symbol.hashCode() + terms.hashCode();
	}

	@Override
	public int compareTo(Term o) {
		if (this == o) {
			return 0;
		}

		if (!(o instanceof FunctionTerm)) {
			return super.compareTo(o);
		}

		FunctionTerm other = (FunctionTerm)o;

		if (terms.size() != other.terms.size()) {
			return terms.size() - other.terms.size();
		}

		int result = symbol.compareTo(other.symbol);

		if (result != 0) {
			return result;
		}

		for (int i = 0; i < terms.size(); i++) {
			result = terms.get(i).compareTo(other.terms.get(i));
			if (result != 0) {
				return result;
			}
		}

		return 0;
	}

	@Override
	public TermImpl renameVariables(String renamePrefix) {
		List<TermImpl> renamedTerms = new ArrayList<>(terms.size());
		for (TermImpl term : terms) {
			renamedTerms.add(term.renameVariables(renamePrefix));
		}
		return FunctionTerm.getInstance(symbol, renamedTerms);
	}

	@Override
	public TermImpl normalizeVariables(String renamePrefix, RenameCounter counter) {
		List<TermImpl> normalizedTerms = new ArrayList<>(terms.size());
		for (TermImpl term : terms) {
			normalizedTerms.add(term.normalizeVariables(renamePrefix, counter));
		}
		return FunctionTerm.getInstance(symbol, normalizedTerms);
	}
}
