package at.ac.tuwien.kr.alpha.commons.programs.reification;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import at.ac.tuwien.kr.alpha.api.ComparisonOperator;
import at.ac.tuwien.kr.alpha.api.programs.ASPCore2Program;
import at.ac.tuwien.kr.alpha.api.programs.InlineDirectives;
import at.ac.tuwien.kr.alpha.api.programs.Predicate;
import at.ac.tuwien.kr.alpha.api.programs.atoms.AggregateAtom;
import at.ac.tuwien.kr.alpha.api.programs.atoms.AggregateAtom.AggregateElement;
import at.ac.tuwien.kr.alpha.api.programs.atoms.AggregateAtom.AggregateFunctionSymbol;
import at.ac.tuwien.kr.alpha.api.programs.atoms.Atom;
import at.ac.tuwien.kr.alpha.api.programs.atoms.BasicAtom;
import at.ac.tuwien.kr.alpha.api.programs.atoms.ComparisonAtom;
import at.ac.tuwien.kr.alpha.api.programs.atoms.ExternalAtom;
import at.ac.tuwien.kr.alpha.api.programs.literals.Literal;
import at.ac.tuwien.kr.alpha.api.programs.rules.Rule;
import at.ac.tuwien.kr.alpha.api.programs.rules.heads.ChoiceHead;
import at.ac.tuwien.kr.alpha.api.programs.rules.heads.ChoiceHead.ChoiceElement;
import at.ac.tuwien.kr.alpha.api.programs.rules.heads.Head;
import at.ac.tuwien.kr.alpha.api.programs.rules.heads.NormalHead;
import at.ac.tuwien.kr.alpha.api.programs.terms.ArithmeticTerm;
import at.ac.tuwien.kr.alpha.api.programs.terms.ConstantTerm;
import at.ac.tuwien.kr.alpha.api.programs.terms.FunctionTerm;
import at.ac.tuwien.kr.alpha.api.programs.terms.IntervalTerm;
import at.ac.tuwien.kr.alpha.api.programs.terms.Term;
import at.ac.tuwien.kr.alpha.api.programs.terms.VariableTerm;
import at.ac.tuwien.kr.alpha.commons.Predicates;
import at.ac.tuwien.kr.alpha.commons.comparisons.ComparisonOperators;
import at.ac.tuwien.kr.alpha.commons.programs.atoms.Atoms;
import at.ac.tuwien.kr.alpha.commons.programs.terms.Terms;
import at.ac.tuwien.kr.alpha.commons.util.IdGenerator;
import at.ac.tuwien.kr.alpha.commons.util.Util;

public class Reifier {

	// Predicates describing rules.
	static final Predicate RULE = Predicates.getPredicate("rule", 1);
	static final Predicate CONSTRAINT = Predicates.getPredicate("constraint", 1);
	static final Predicate RULE_HEAD = Predicates.getPredicate("rule_head", 2);
	static final Predicate RULE_NUM_BODY_LITERALS = Predicates.getPredicate("rule_numBodyLiterals", 2);
	static final Predicate RULE_BODY_LITERAL = Predicates.getPredicate("rule_bodyLiteral", 2);

	// Predicates describing facts.
	static final Predicate FACT = Predicates.getPredicate("fact", 1);

	// Predicates describing heads.
	static final Predicate HEAD_TYPE = Predicates.getPredicate("head_type", 2);

	// Predicates describing normal heads.
	static final Predicate NORMAL_HEAD_ATOM = Predicates.getPredicate("normalHead_atom", 2);

	// Predicates describing choice heads.
	static final Predicate CHOICE_HEAD_LOWER_BOUND = Predicates.getPredicate("choiceHead_lowerBound", 2);
	static final Predicate CHOICE_HEAD_UPPER_BOUND = Predicates.getPredicate("choiceHead_upperBound", 2);
	static final Predicate CHOICE_HEAD_NUM_ELEMENTS = Predicates.getPredicate("choiceHead_numElements", 2);
	static final Predicate CHOICE_HEAD_ELEMENT = Predicates.getPredicate("choiceHead_element", 2);
	static final Predicate CHOICE_ELEMENT_ATOM = Predicates.getPredicate("choiceElement_atom", 2);
	static final Predicate CHOICE_ELEMENT_NUM_CONDITION_LITERALS = Predicates.getPredicate("choiceElement_numConditionLiterals", 2);
	static final Predicate CHOICE_ELEMENT_CONDITION_LITERAL = Predicates.getPredicate("choiceElement_conditionLiteral", 2);

	// Predicates describing literals.
	static final Predicate LITERAL_POLARITY = Predicates.getPredicate("literal_polarity", 2);
	static final Predicate LITERAL_ATOM = Predicates.getPredicate("literal_atom", 2);

	// Predicates describing predicates.
	static final Predicate PREDICATE = Predicates.getPredicate("predicate", 3);

	// Predicates describing atoms.
	static final Predicate ATOM_TYPE = Predicates.getPredicate("atom_type", 2);

	// Predicates describing basic atoms.
	static final Predicate BASIC_ATOM_PREDICATE = Predicates.getPredicate("basicAtom_predicate", 2);
	static final Predicate BASIC_ATOM_NUM_TERMS = Predicates.getPredicate("basicAtom_numTerms", 2);
	static final Predicate BASIC_ATOM_TERM = Predicates.getPredicate("basicAtom_term", 3);

	// Predicates describing comparison atoms.
	static final Predicate COMPARISON_ATOM_LEFT = Predicates.getPredicate("comparisonAtom_leftTerm", 2);
	static final Predicate COMPARISON_ATOM_RIGHT = Predicates.getPredicate("comparisonAtom_rightTerm", 2);
	static final Predicate COMPARISON_ATOM_OPERATOR = Predicates.getPredicate("comparisonAtom_operator", 2);

	// Predicates describing external atoms.
	static final Predicate EXTERNAL_ATOM_NAME = Predicates.getPredicate("externalAtom_name", 2);
	static final Predicate EXTERNAL_ATOM_NUM_INPUT_TERMS = Predicates.getPredicate("externalAtom_numInputTerms", 2);
	static final Predicate EXTERNAL_ATOM_INPUT_TERM = Predicates.getPredicate("externalAtom_inputTerm", 3);
	static final Predicate EXTERNAL_ATOM_NUM_OUTPUT_TERMS = Predicates.getPredicate("externalAtom_numOutputTerms", 2);
	static final Predicate EXTERNAL_ATOM_OUTPUT_TERM = Predicates.getPredicate("externalAtom_outputTerm", 3);

	// Predicates describing aggregate atoms.
	static final Predicate AGGREGATE_ATOM_LEFT_TERM = Predicates.getPredicate("aggregateAtom_leftHandTerm", 2);
	static final Predicate AGGREGATE_ATOM_LEFT_OPERATOR = Predicates.getPredicate("aggregateAtom_leftHandOperator", 2);
	static final Predicate AGGREGATE_ATOM_RIGHT_TERM = Predicates.getPredicate("aggregateAtom_rightHandTerm", 2);
	static final Predicate AGGREGATE_ATOM_RIGHT_OPERATOR = Predicates.getPredicate("aggregateAtom_rightHandOperator", 2);
	static final Predicate AGGREGATE_ATOM_AGGREGATE_FUNCTION = Predicates.getPredicate("aggregateAtom_aggregateFunction", 2);
	static final Predicate AGGREGATE_ATOM_NUM_AGGREGATE_ELEMENTS = Predicates.getPredicate("aggregateAtom_numAggregateElements", 2);
	static final Predicate AGGREGATE_ATOM_AGGREGATE_ELEMENT = Predicates.getPredicate("aggregateAtom_aggregateElement", 2);

	// Predicates describing aggregate elements.
	static final Predicate AGGREGATE_ELEMENT_NUM_TERMS = Predicates.getPredicate("aggregateElement_numTerms", 2);
	static final Predicate AGGREGATE_ELEMENT_TERM = Predicates.getPredicate("aggregateElement_term", 3);
	static final Predicate AGGREGATE_ELEMENT_NUM_LITERALS = Predicates.getPredicate("aggregateElement_numLiterals", 2);
	static final Predicate AGGREGATE_ELEMENT_LITERAL = Predicates.getPredicate("aggregateElement_literal", 2);

	// Predicates describing terms.
	static final Predicate TERM_TYPE = Predicates.getPredicate("term_type", 2);

	// Predicates describing constant terms.
	static final Predicate CONSTANT_TERM_TYPE = Predicates.getPredicate("constantTerm_type", 2);
	static final Predicate CONSTANT_TERM_VALUE = Predicates.getPredicate("constantTerm_value", 2);

	// Predicates describing variable terms.
	static final Predicate VARIABLE_TERM_SYMBOL = Predicates.getPredicate("variableTerm_symbol", 2);

	// Predicates describing arithmetic terms.
	static final Predicate ARITHMETIC_TERM_LEFT = Predicates.getPredicate("arithmeticTerm_leftTerm", 2);
	static final Predicate ARITHMETIC_TERM_RIGHT = Predicates.getPredicate("arithmeticTerm_rightTerm", 2);
	static final Predicate ARITHMETIC_TERM_OPERATOR = Predicates.getPredicate("arithmeticTerm_operator", 2);

	// Predicates describing function terms.
	static final Predicate FUNCTION_TERM_SYMBOL = Predicates.getPredicate("functionTerm_symbol", 2);
	static final Predicate FUNCTION_TERM_NUM_ARGUMENTS = Predicates.getPredicate("functionTerm_numArguments", 2);
	static final Predicate FUNCTION_TERM_ARGUMENT = Predicates.getPredicate("functionTerm_argumentTerm", 3);

	// Predicates describing interval terms
	static final Predicate INTERVAL_TERM_LOWER = Predicates.getPredicate("intervalTerm_lower", 2);
	static final Predicate INTERVAL_TERM_UPPER = Predicates.getPredicate("intervalTerm_Upper", 2);

	// Predicates describing InlineDirectives
	static final Predicate INLINE_DIRECTIVE = Predicates.getPredicate("inlineDirective", 2);

	// Constants describing head types.
	static final ConstantTerm<String> HEAD_TYPE_NORMAL = Terms.newSymbolicConstant("normal");
	static final ConstantTerm<String> HEAD_TYPE_CHOICE = Terms.newSymbolicConstant("choice");

	// Constants describing literal polaritites.
	static final ConstantTerm<String> LITERAL_POLARITY_POSITIVE = Terms.newSymbolicConstant("pos");
	static final ConstantTerm<String> LITERAL_POLARITY_NEGATIVE = Terms.newSymbolicConstant("neg");

	// Constants describing atom types.
	static final ConstantTerm<String> ATOM_TYPE_BASIC = Terms.newSymbolicConstant("basic");
	static final ConstantTerm<String> ATOM_TYPE_COMPARISON = Terms.newSymbolicConstant("comparison");
	static final ConstantTerm<String> ATOM_TYPE_EXTERNAL = Terms.newSymbolicConstant("external");
	static final ConstantTerm<String> ATOM_TYPE_AGGREGATE = Terms.newSymbolicConstant("aggregate");

	// Constants describing comparison operators.
	static final ConstantTerm<String> CMP_OP_EQ = Terms.newSymbolicConstant("eq");
	static final ConstantTerm<String> CMP_OP_NE = Terms.newSymbolicConstant("ne");
	static final ConstantTerm<String> CMP_OP_LE = Terms.newSymbolicConstant("le");
	static final ConstantTerm<String> CMP_OP_LT = Terms.newSymbolicConstant("lt");
	static final ConstantTerm<String> CMP_OP_GE = Terms.newSymbolicConstant("ge");
	static final ConstantTerm<String> CMP_OP_GT = Terms.newSymbolicConstant("gt");

	// Constants describing aggregate functions.
	static final ConstantTerm<String> AGGREGATE_FUNCTION_COUNT = Terms.newSymbolicConstant("count");
	static final ConstantTerm<String> AGGREGATE_FUNCTION_SUM = Terms.newSymbolicConstant("sum");
	static final ConstantTerm<String> AGGREGATE_FUNCTION_MIN = Terms.newSymbolicConstant("min");
	static final ConstantTerm<String> AGGREGATE_FUNCTION_MAX = Terms.newSymbolicConstant("max");

	// Constants describing term types.
	static final ConstantTerm<String> TERM_TYPE_CONSTANT = Terms.newSymbolicConstant("constant");
	static final ConstantTerm<String> TERM_TYPE_VARIABLE = Terms.newSymbolicConstant("variable");
	static final ConstantTerm<String> TERM_TYPE_ARITHMETIC = Terms.newSymbolicConstant("arithmetic");
	static final ConstantTerm<String> TERM_TYPE_FUNCTION = Terms.newSymbolicConstant("function");
	static final ConstantTerm<String> TERM_TYPE_INTERVAL = Terms.newSymbolicConstant("interval");

	private static final Map<ComparisonOperator, ConstantTerm<String>> CMP_OPS = new HashMap<>();
	private static final Map<AggregateFunctionSymbol, ConstantTerm<String>> AGG_FUNCS = new HashMap<>();
	private static final Map<Class<?>, String> TERM_TYPES = new HashMap<>();

	static {
		CMP_OPS.put(ComparisonOperators.EQ, CMP_OP_EQ);
		CMP_OPS.put(ComparisonOperators.NE, CMP_OP_NE);
		CMP_OPS.put(ComparisonOperators.LE, CMP_OP_LE);
		CMP_OPS.put(ComparisonOperators.LT, CMP_OP_LT);
		CMP_OPS.put(ComparisonOperators.GE, CMP_OP_GE);
		CMP_OPS.put(ComparisonOperators.GT, CMP_OP_GT);

		AGG_FUNCS.put(AggregateFunctionSymbol.COUNT, AGGREGATE_FUNCTION_COUNT);
		AGG_FUNCS.put(AggregateFunctionSymbol.SUM, AGGREGATE_FUNCTION_SUM);
		AGG_FUNCS.put(AggregateFunctionSymbol.MIN, AGGREGATE_FUNCTION_MIN);
		AGG_FUNCS.put(AggregateFunctionSymbol.MAX, AGGREGATE_FUNCTION_MAX);

		TERM_TYPES.put(String.class, "string");
		TERM_TYPES.put(Integer.class, "integer");
	}

	private final Supplier<IdGenerator<ConstantTerm<?>>> idGeneratorProvider;

	public Reifier(Supplier<IdGenerator<ConstantTerm<?>>> idGeneratorProvider) {
		this.idGeneratorProvider = idGeneratorProvider;
	}

	public Set<BasicAtom> reifyProgram(ASPCore2Program program) {
		ReificationContext ctx = new ReificationContext(idGeneratorProvider.get());
		reifyDirectives(ctx, program.getInlineDirectives());
		for (Atom fact : program.getFacts()) {
			ConstantTerm<?> factId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(FACT, factId));
			reifyAtom(ctx, factId, fact);
		}
		for (Rule<? extends Head> rule : program.getRules()) {
			reifyRule(ctx, rule);
		}
		return ctx.computeReifiedProgram();
	}

	/**
	 * Generates atoms of form inlineDirective("<DIRECTIVE_NAME>", "<DIRECTIVE_VALUE>").
	 */
	void reifyDirectives(ReificationContext ctx, InlineDirectives directives) {
		for (Map.Entry<InlineDirectives.DIRECTIVE, String> entry : directives.getDirectives().entrySet()) {
			ctx.addAtom(Atoms.newBasicAtom(INLINE_DIRECTIVE, Terms.newConstant(entry.getKey().name()), Terms.newConstant(entry.getValue())));
		}
	}

	void reifyRule(ReificationContext ctx, Rule<? extends Head> rule) {
		ConstantTerm<?> ruleId = ctx.getNextId();
		if (rule.isConstraint()) {
			ctx.addAtom(Atoms.newBasicAtom(CONSTRAINT, ruleId));
		} else {
			ctx.addAtom(Atoms.newBasicAtom(RULE, ruleId));
			reifyHead(ctx, ruleId, rule.getHead());
		}
		ctx.addAtom(Atoms.newBasicAtom(RULE_NUM_BODY_LITERALS, ruleId, Terms.newConstant(rule.getBody().size())));
		for (Literal lit : rule.getBody()) {
			ConstantTerm<?> literalId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(RULE_BODY_LITERAL, ruleId, literalId));
			reifyLiteral(ctx, literalId, lit);
		}
	}

	void reifyHead(ReificationContext ctx, ConstantTerm<?> ruleId, Head head) {
		ConstantTerm<?> headId = ctx.getNextId();
		ctx.addAtom(Atoms.newBasicAtom(RULE_HEAD, ruleId, headId));
		if (head instanceof NormalHead) {
			reifyNormalHead(ctx, headId, (NormalHead) head);
		} else if (head instanceof ChoiceHead) {
			reifyChoiceHead(ctx, headId, (ChoiceHead) head);
		} else {
			throw Util.oops("Head type " + head.getClass().getSimpleName() + " cannot be reified!");
		}
	}

	void reifyNormalHead(ReificationContext ctx, ConstantTerm<?> headId, NormalHead head) {
		ctx.addAtom(Atoms.newBasicAtom(HEAD_TYPE, headId, HEAD_TYPE_NORMAL));
		ConstantTerm<?> atomId = ctx.getNextId();
		ctx.addAtom(Atoms.newBasicAtom(NORMAL_HEAD_ATOM, headId, atomId));
		reifyAtom(ctx, atomId, head.getAtom());
	}

	void reifyChoiceHead(ReificationContext ctx, ConstantTerm<?> headId, ChoiceHead head) {
		ctx.addAtom(Atoms.newBasicAtom(HEAD_TYPE, headId, HEAD_TYPE_CHOICE));
		if (head.getLowerBound() != null) {
			ctx.addAtom(Atoms.newBasicAtom(CHOICE_HEAD_LOWER_BOUND, headId, head.getLowerBound()));
		}
		if (head.getUpperBound() != null) {
			ctx.addAtom(Atoms.newBasicAtom(CHOICE_HEAD_UPPER_BOUND, headId, head.getUpperBound()));
		}
		ctx.addAtom(Atoms.newBasicAtom(CHOICE_HEAD_NUM_ELEMENTS, headId, Terms.newConstant(head.getChoiceElements().size())));
		for (ChoiceElement element : head.getChoiceElements()) {
			reifyChoiceElement(ctx, headId, element);
		}
	}

	void reifyChoiceElement(ReificationContext ctx, ConstantTerm<?> headId, ChoiceElement element) {
		ConstantTerm<?> elementId = ctx.getNextId();
		ctx.addAtom(Atoms.newBasicAtom(CHOICE_HEAD_ELEMENT, headId, elementId));
		ConstantTerm<?> atomId = ctx.getNextId();
		ctx.addAtom(Atoms.newBasicAtom(CHOICE_ELEMENT_ATOM, elementId, atomId));
		reifyAtom(ctx, atomId, element.getChoiceAtom());
		ctx.addAtom(Atoms.newBasicAtom(CHOICE_ELEMENT_NUM_CONDITION_LITERALS, elementId, Terms.newConstant(element.getConditionLiterals().size())));
		for (Literal lit : element.getConditionLiterals()) {
			ConstantTerm<?> literalId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(CHOICE_ELEMENT_CONDITION_LITERAL, elementId, literalId));
			reifyLiteral(ctx, literalId, lit);
		}
	}

	void reifyLiteral(ReificationContext ctx, ConstantTerm<?> literalId, Literal lit) {
		ctx.addAtom(Atoms.newBasicAtom(LITERAL_POLARITY, literalId, lit.isNegated() ? LITERAL_POLARITY_NEGATIVE : LITERAL_POLARITY_POSITIVE));
		ConstantTerm<?> atomId = ctx.getNextId();
		ctx.addAtom(Atoms.newBasicAtom(LITERAL_ATOM, literalId, atomId));
		reifyAtom(ctx, atomId, lit.getAtom());
	}

	void reifyAtom(ReificationContext ctx, ConstantTerm<?> atomId, Atom atom) {
		if (atom instanceof BasicAtom) {
			reifyBasicAtom(ctx, atomId, (BasicAtom) atom);
		} else if (atom instanceof ComparisonAtom) {
			reifyComparisonAtom(ctx, atomId, (ComparisonAtom) atom);
		} else if (atom instanceof ExternalAtom) {
			reifyExternalAtom(ctx, atomId, (ExternalAtom) atom);
		} else if (atom instanceof AggregateAtom) {
			reifyAggregateAtom(ctx, atomId, (AggregateAtom) atom);
		} else {
			throw Util.oops("Atom type " + atom.getClass().getSimpleName() + " cannot be reified!");
		}
	}

	void reifyBasicAtom(ReificationContext ctx, ConstantTerm<?> atomId, BasicAtom atom) {
		ctx.addAtom(Atoms.newBasicAtom(ATOM_TYPE, atomId, ATOM_TYPE_BASIC));
		ConstantTerm<?> predicateId = ctx.computePredicateId(atom.getPredicate());
		ctx.addAtom(Atoms.newBasicAtom(BASIC_ATOM_PREDICATE, atomId, predicateId));
		ctx.addAtom(Atoms.newBasicAtom(BASIC_ATOM_NUM_TERMS, atomId, Terms.newConstant(atom.getTerms().size())));
		for (int i = 0; i < atom.getTerms().size(); i++) {
			ConstantTerm<?> termId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(BASIC_ATOM_TERM, atomId, Terms.newConstant(i), termId));
			reifyTerm(ctx, termId, atom.getTerms().get(i));
		}
	}

	void reifyComparisonAtom(ReificationContext ctx, ConstantTerm<?> atomId, ComparisonAtom atom) {
		ctx.addAtom(Atoms.newBasicAtom(ATOM_TYPE, atomId, ATOM_TYPE_COMPARISON));
		ConstantTerm<?> leftTermId = ctx.getNextId();
		ConstantTerm<?> rightTermId = ctx.getNextId();
		ctx.addAtom(Atoms.newBasicAtom(COMPARISON_ATOM_LEFT, atomId, leftTermId));
		ctx.addAtom(Atoms.newBasicAtom(COMPARISON_ATOM_RIGHT, atomId, rightTermId));
		if (!CMP_OPS.containsKey(atom.getOperator())) {
			throw Util.oops("Cannot reifiy comparison operator " + atom.getOperator());
		}
		ctx.addAtom(Atoms.newBasicAtom(COMPARISON_ATOM_OPERATOR, atomId, CMP_OPS.get(atom.getOperator())));
		reifyTerm(ctx, leftTermId, atom.getTerms().get(0));
		reifyTerm(ctx, rightTermId, atom.getTerms().get(1));
	}

	void reifyExternalAtom(ReificationContext ctx, ConstantTerm<?> atomId, ExternalAtom atom) {
		ctx.addAtom(Atoms.newBasicAtom(ATOM_TYPE, atomId, ATOM_TYPE_EXTERNAL));
		ctx.addAtom(Atoms.newBasicAtom(EXTERNAL_ATOM_NAME, atomId, Terms.newConstant(atom.getPredicate().getName())));
		ctx.addAtom(Atoms.newBasicAtom(EXTERNAL_ATOM_NUM_INPUT_TERMS, atomId, Terms.newConstant(atom.getInput().size())));
		for (int i = 0; i < atom.getInput().size(); i++) {
			ConstantTerm<?> inTermId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(EXTERNAL_ATOM_INPUT_TERM, atomId, Terms.newConstant(i), inTermId));
			reifyTerm(ctx, inTermId, atom.getInput().get(i));
		}
		ctx.addAtom(Atoms.newBasicAtom(EXTERNAL_ATOM_NUM_OUTPUT_TERMS, atomId, Terms.newConstant(atom.getOutput().size())));
		for (int i = 0; i < atom.getOutput().size(); i++) {
			ConstantTerm<?> outTermId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(EXTERNAL_ATOM_OUTPUT_TERM, atomId, Terms.newConstant(i), outTermId));
			reifyTerm(ctx, outTermId, atom.getOutput().get(i));
		}
	}

	void reifyAggregateAtom(ReificationContext ctx, ConstantTerm<?> atomId, AggregateAtom atom) {
		ctx.addAtom(Atoms.newBasicAtom(ATOM_TYPE, atomId, ATOM_TYPE_AGGREGATE));
		ctx.addAtom(Atoms.newBasicAtom(AGGREGATE_ATOM_AGGREGATE_FUNCTION, atomId, AGG_FUNCS.get(atom.getAggregateFunction())));
		if (atom.getLowerBoundOperator() != null) {
			ctx.addAtom(Atoms.newBasicAtom(AGGREGATE_ATOM_LEFT_OPERATOR, atomId, CMP_OPS.get(atom.getLowerBoundOperator())));
			ConstantTerm<?> leftTermId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(AGGREGATE_ATOM_LEFT_TERM, atomId, leftTermId));
		}
		if (atom.getUpperBoundOperator() != null) {
			ctx.addAtom(Atoms.newBasicAtom(AGGREGATE_ATOM_RIGHT_OPERATOR, atomId, CMP_OPS.get(atom.getUpperBoundOperator())));
			ConstantTerm<?> rightTermId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(AGGREGATE_ATOM_RIGHT_TERM, atomId, rightTermId));
		}
		ctx.addAtom(Atoms.newBasicAtom(AGGREGATE_ATOM_NUM_AGGREGATE_ELEMENTS, atomId, Terms.newConstant(atom.getAggregateElements().size())));
		for (AggregateElement element : atom.getAggregateElements()) {
			ConstantTerm<?> elementId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(AGGREGATE_ATOM_AGGREGATE_ELEMENT, atomId, elementId));
			reifyAggregateElement(ctx, elementId, element);
		}
	}

	void reifyAggregateElement(ReificationContext ctx, ConstantTerm<?> elementId, AggregateElement element) {
		ctx.addAtom(Atoms.newBasicAtom(AGGREGATE_ELEMENT_NUM_TERMS, elementId, Terms.newConstant(element.getElementTerms().size())));
		for (int i = 0; i < element.getElementTerms().size(); i++) {
			ConstantTerm<?> termId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(AGGREGATE_ELEMENT_TERM, elementId, Terms.newConstant(i), termId));
			reifyTerm(ctx, termId, element.getElementTerms().get(i));
		}
		ctx.addAtom(Atoms.newBasicAtom(AGGREGATE_ELEMENT_NUM_LITERALS, elementId, Terms.newConstant(element.getElementLiterals().size())));
		for (Literal lit : element.getElementLiterals()) {
			ConstantTerm<?> literalId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(AGGREGATE_ELEMENT_LITERAL, elementId, literalId));
			reifyLiteral(ctx, literalId, lit);
		}
	}

	void reifyTerm(ReificationContext ctx, ConstantTerm<?> termId, Term term) {
		if (term instanceof ConstantTerm) {
			reifyConstantTerm(ctx, termId, (ConstantTerm<?>) term);
		} else if (term instanceof VariableTerm) {
			reifyVariableTerm(ctx, termId, (VariableTerm) term);
		} else if (term instanceof ArithmeticTerm) {
			reifyArithmeticTerm(ctx, termId, (ArithmeticTerm) term);
		} else if (term instanceof FunctionTerm) {
			reifyFunctionTerm(ctx, termId, (FunctionTerm) term);
		} else if (term instanceof IntervalTerm) {
			reifyIntervalTerm(ctx, termId, (IntervalTerm) term);
		} else {
			throw Util.oops("Cannot reify term of type " + term.getClass().getSimpleName());
		}
	}

	void reifyConstantTerm(ReificationContext ctx, ConstantTerm<?> termId, ConstantTerm<?> term) {
		String termType;
		if (term.isSymbolic()) {
			termType = "symbol";
		} else {
			termType = TERM_TYPES.getOrDefault(term.getObject().getClass(), "object(" + term.getObject().getClass().getName() + ")");
		}
		ctx.addAtom(Atoms.newBasicAtom(TERM_TYPE, termId, TERM_TYPE_CONSTANT));
		ctx.addAtom(Atoms.newBasicAtom(CONSTANT_TERM_TYPE, termId, Terms.newConstant(termType)));
		ctx.addAtom(Atoms.newBasicAtom(CONSTANT_TERM_VALUE, termId, Terms.newConstant(term.getObject().toString().replace("\"", "\\\""))));
	}

	void reifyVariableTerm(ReificationContext ctx, ConstantTerm<?> termId, VariableTerm term) {
		ctx.addAtom(Atoms.newBasicAtom(TERM_TYPE, termId, TERM_TYPE_VARIABLE));
		ctx.addAtom(Atoms.newBasicAtom(VARIABLE_TERM_SYMBOL, termId, Terms.newConstant(term.toString())));
	}

	void reifyArithmeticTerm(ReificationContext ctx, ConstantTerm<?> termId, ArithmeticTerm term) {
		ctx.addAtom(Atoms.newBasicAtom(TERM_TYPE, termId, TERM_TYPE_ARITHMETIC));
		ConstantTerm<?> leftTermId = ctx.getNextId();
		ctx.addAtom(Atoms.newBasicAtom(ARITHMETIC_TERM_LEFT, termId, leftTermId));
		reifyTerm(ctx, leftTermId, term.getLeftOperand());
		ConstantTerm<?> rightTermId = ctx.getNextId();
		ctx.addAtom(Atoms.newBasicAtom(ARITHMETIC_TERM_RIGHT, termId, rightTermId));
		reifyTerm(ctx, rightTermId, term.getRightOperand());
		ctx.addAtom(Atoms.newBasicAtom(ARITHMETIC_TERM_OPERATOR, termId, Terms.newConstant(term.getOperator().toString())));
	}

	void reifyFunctionTerm(ReificationContext ctx, ConstantTerm<?> termId, FunctionTerm term) {
		ctx.addAtom(Atoms.newBasicAtom(TERM_TYPE, termId, TERM_TYPE_FUNCTION));
		ctx.addAtom(Atoms.newBasicAtom(FUNCTION_TERM_SYMBOL, termId, Terms.newConstant(term.getSymbol())));
		ctx.addAtom(Atoms.newBasicAtom(FUNCTION_TERM_NUM_ARGUMENTS, termId, Terms.newConstant(term.getTerms().size())));
		for (int i = 0; i < term.getTerms().size(); i++) {
			ConstantTerm<?> argTermId = ctx.getNextId();
			ctx.addAtom(Atoms.newBasicAtom(FUNCTION_TERM_ARGUMENT, termId, Terms.newConstant(i), argTermId));
			reifyTerm(ctx, argTermId, term.getTerms().get(i));
		}
	}

	void reifyIntervalTerm(ReificationContext ctx, ConstantTerm<?> termId, IntervalTerm term) {
		ctx.addAtom(Atoms.newBasicAtom(TERM_TYPE, termId, TERM_TYPE_INTERVAL));
		ConstantTerm<?> lowerTermId = ctx.getNextId();
		ctx.addAtom(Atoms.newBasicAtom(INTERVAL_TERM_LOWER, termId, lowerTermId));
		reifyTerm(ctx, lowerTermId, term.getLowerBound());
		ConstantTerm<?> upperTermId = ctx.getNextId();
		ctx.addAtom(Atoms.newBasicAtom(INTERVAL_TERM_UPPER, termId, upperTermId));
		reifyTerm(ctx, upperTermId, term.getUpperBound());
	}

}
