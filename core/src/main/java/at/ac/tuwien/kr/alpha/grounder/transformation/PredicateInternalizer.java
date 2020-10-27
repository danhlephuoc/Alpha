package at.ac.tuwien.kr.alpha.grounder.transformation;

import at.ac.tuwien.kr.alpha.common.atoms.Literal;
import at.ac.tuwien.kr.alpha.common.PredicateImpl;
import at.ac.tuwien.kr.alpha.common.atoms.*;
import at.ac.tuwien.kr.alpha.common.program.InputProgram;
import at.ac.tuwien.kr.alpha.common.rule.BasicRule;
import at.ac.tuwien.kr.alpha.common.rule.head.Head;
import at.ac.tuwien.kr.alpha.common.rule.head.NormalHead;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Rewrites all predicates of a given Program such that they are internal and hence hidden from answer sets.
 *
 * Copyright (c) 2018-2020, the Alpha Team.
 */
public class PredicateInternalizer {

	static InputProgram makePredicatesInternal(InputProgram program) {
		InputProgram.Builder prgBuilder = InputProgram.builder();
		for (AtomImpl atom : program.getFacts()) {
			prgBuilder.addFact(PredicateInternalizer.makePredicateInternal(atom));
		}
		for (BasicRule rule : program.getRules()) {
			prgBuilder.addRule(PredicateInternalizer.makePredicateInternal(rule));
		}
		prgBuilder.addInlineDirectives(program.getInlineDirectives());
		return prgBuilder.build();
	}

	private static BasicRule makePredicateInternal(BasicRule rule) {
		Head newHead = null;
		if (rule.getHead() != null) {
			if (!(rule.getHead() instanceof NormalHead)) {
				throw new UnsupportedOperationException("Cannot make predicates in rules internal whose head is not normal.");
			}
			newHead = new NormalHead(makePredicateInternal(((NormalHead) rule.getHead()).getAtom()));
		}
		List<LiteralImpl> newBody = new ArrayList<>();
		for (LiteralImpl bodyElement : rule.getBody()) {
			// Only rewrite BasicAtoms.
			if (bodyElement instanceof BasicLiteral) {
				newBody.add(makePredicateInternal(bodyElement.getAtom()).toLiteral());
			} else {
				// Keep other body element as is.
				newBody.add(bodyElement);
			}
		}
		return new BasicRule(newHead, newBody);
	}

	private static AtomImpl makePredicateInternal(AtomImpl atom) {
		PredicateImpl newInternalPredicate = PredicateImpl.getInstance(atom.getPredicate().getName(), atom.getPredicate().getArity(), true);
		return new BasicAtom(newInternalPredicate, atom.getTerms());
	}
}
