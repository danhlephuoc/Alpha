package at.ac.tuwien.kr.alpha.core.common;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import at.ac.tuwien.kr.alpha.api.programs.ASPCore2Program;
import at.ac.tuwien.kr.alpha.api.programs.rules.Rule;
import at.ac.tuwien.kr.alpha.api.programs.rules.heads.Head;
import at.ac.tuwien.kr.alpha.commons.programs.rules.Rules;
import at.ac.tuwien.kr.alpha.core.parser.ProgramParserImpl;
import at.ac.tuwien.kr.alpha.core.programs.rules.CompiledRule;
import at.ac.tuwien.kr.alpha.core.programs.rules.InternalRule;

/**
 * Copyright (c) 2018, the Alpha Team.
 */
public class RuleTest {

	private final ProgramParserImpl parser = new ProgramParserImpl();

	@Test
	public void renameVariables() {
		String originalRule = "p(X,Y) :- a, f(Z) = 1, q(X,g(Y),Z), dom(A).";
		Rule<Head> rule = parser.parse(originalRule).getRules().get(0);
		CompiledRule normalRule = InternalRule.fromNormalRule(Rules.toNormalRule(rule));
		CompiledRule renamedRule = normalRule.renameVariables("_13");
		Rule<Head> expectedRenamedRule = parser.parse("p(X_13, Y_13) :- a, f(Z_13) = 1, q(X_13, g(Y_13), Z_13), dom(A_13).").getRules().get(0);
		CompiledRule expectedRenamedNormalRule = InternalRule.fromNormalRule(Rules.toNormalRule(expectedRenamedRule));
		assertEquals(expectedRenamedNormalRule.toString(), renamedRule.toString());
	}

	@Test
	public void testRulesEqual() {
		ASPCore2Program p1 = parser.parse("p(X, Y) :- bla(X), blub(Y), foo(X, Y), not bar(X).");
		Rule<Head> r1 = p1.getRules().get(0);
		ASPCore2Program p2 = parser.parse("p(X, Y) :- bla(X), blub(Y), foo(X, Y), not bar(X).");
		Rule<Head> r2 = p2.getRules().get(0);
		ASPCore2Program p3 = parser.parse("p(X, Y) :- bla(X), blub(X), foo(X, X), not bar(X).");
		Rule<Head> r3 = p3.getRules().get(0);
		assertTrue(r1.equals(r2));
		assertTrue(r2.equals(r1));
		assertTrue(r1.hashCode() == r2.hashCode());
		assertFalse(r1.equals(r3));
		assertFalse(r3.equals(r1));
		assertTrue(r1.hashCode() != r3.hashCode());
		assertFalse(r2.equals(r3));
		assertFalse(r3.equals(r2));
		assertTrue(r2.hashCode() != r3.hashCode());
	}

}