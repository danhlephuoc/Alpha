/**
 * Copyright (c) 2019, the Alpha Team.
 * All rights reserved.
 *
 * Additional changes made by Siemens.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package at.ac.tuwien.kr.alpha.api.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import at.ac.tuwien.kr.alpha.api.Alpha;

/**
 * Config structure for {@link Alpha} instances.
 * 
 * Copyright (c) 2021, the Alpha Team.
 */
public class SystemConfig {
	// Note: Defining constants for default values here rather than just
	// initializing from those values in order to have the values accessible in
	// contexts where no AlphaConfig instance exists (e.g. argument parsing from
	// command line)
	public static final String DEFAULT_GROUNDER_NAME = "naive";
	public static final String DEFAULT_SOLVER_NAME = "default";
	public static final String DEFAULT_NOGOOD_STORE_NAME = "alphaRoaming";
	public static final Heuristic DEFAULT_BRANCHING_HEURISTIC = Heuristic.VSIDS;
	public static final BinaryNoGoodPropagationEstimationStrategy DEFAULT_MOMS_STRATEGY = BinaryNoGoodPropagationEstimationStrategy.CountBinaryWatches;
	public static final long DEFAULT_SEED = System.nanoTime();
	public static final boolean DEFAULT_DETERMINISTIC = false;
	public static final boolean DEFAULT_PRINT_STATS = false;
	public static final boolean DEFAULT_QUIET = false;
	public static final boolean DEFAULT_DISABLE_JUSTIFICATION_SEARCH = false;
	public static final boolean DEFAULT_DEBUG_INTERNAL_CHECKS = false;
	public static final boolean DEFAULT_SORT_ANSWER_SETS = false;
	public static final List<Integer> DEFAULT_REPLAY_CHOICES = Collections.emptyList();
	public static final boolean DEFAULT_STRATIFIED_EVALUATION = true;
	public static final boolean DEFAULT_DISABLE_NOGOOD_DELETION = false;
	public static final String DEFAULT_GROUNDER_TOLERANCE_CONSTRAINTS = GrounderHeuristicsConfiguration.STRICT_STRING;
	public static final String DEFAULT_GROUNDER_TOLERANCE_RULES = GrounderHeuristicsConfiguration.STRICT_STRING;
	public static final boolean DEFAULT_GROUNDER_ACCUMULATOR_ENABLED = false;
	public static final String DEFAULT_ATOM_SEPARATOR = ", ";
	public static final AggregateRewritingConfig DEFAULT_AGGREGATE_REWRITING_CONFIG = new AggregateRewritingConfig();

	private String grounderName = DEFAULT_GROUNDER_NAME;
	private String solverName = DEFAULT_SOLVER_NAME;
	private String nogoodStoreName = DEFAULT_NOGOOD_STORE_NAME;
	private boolean deterministic = DEFAULT_DETERMINISTIC;
	private long seed = DEFAULT_SEED;
	private boolean debugInternalChecks = DEFAULT_DEBUG_INTERNAL_CHECKS;
	private Heuristic branchingHeuristic = DEFAULT_BRANCHING_HEURISTIC;
	private BinaryNoGoodPropagationEstimationStrategy momsStrategy = DEFAULT_MOMS_STRATEGY;
	private boolean quiet = DEFAULT_QUIET;
	private boolean printStats = DEFAULT_PRINT_STATS;
	private boolean disableJustificationSearch = DEFAULT_DISABLE_JUSTIFICATION_SEARCH;
	private boolean sortAnswerSets = DEFAULT_SORT_ANSWER_SETS;
	private List<Integer> replayChoices = DEFAULT_REPLAY_CHOICES;
	private boolean evaluateStratifiedPart = DEFAULT_STRATIFIED_EVALUATION;
	private boolean disableNoGoodDeletion = DEFAULT_DISABLE_NOGOOD_DELETION;
	private String grounderToleranceConstraints = DEFAULT_GROUNDER_TOLERANCE_CONSTRAINTS;
	private String grounderToleranceRules = DEFAULT_GROUNDER_TOLERANCE_RULES;
	private boolean grounderAccumulatorEnabled = DEFAULT_GROUNDER_ACCUMULATOR_ENABLED;
	private String atomSeparator = DEFAULT_ATOM_SEPARATOR;
	private AggregateRewritingConfig aggregateRewritingConfig = DEFAULT_AGGREGATE_REWRITING_CONFIG;

	public String getGrounderName() {
		return this.grounderName;
	}

	/**
	 * Sets the name of the grounder implementation to use.
	 */
	public void setGrounderName(String grounderName) {
		this.grounderName = grounderName;
	}

	public String getSolverName() {
		return this.solverName;
	}

	/**
	 * Sets the name of the solver implementation to use.
	 */
	public void setSolverName(String solverName) {
		this.solverName = solverName;
	}

	public String getNogoodStoreName() {
		return this.nogoodStoreName;
	}

	/**
	 * Sets the name of the nogood store implementation to use.
	 */
	public void setNogoodStoreName(String nogoodStoreName) {
		this.nogoodStoreName = nogoodStoreName;
	}

	public boolean isDeterministic() {
		return this.deterministic;
	}

	/**
	 * If set, 0 will be used as random seed for solver-internal branching heuristics, resulting in answer sets of the same program being found
	 * in a fixed sequence.
	 */
	public void setDeterministic(boolean deterministic) {
		this.deterministic = deterministic;
	}

	public long getSeed() {
		return this.seed;
	}

	/**
	 * Sets a fixed seed for solver-internal branching heuristics.
	 */
	public void setSeed(long seed) {
		this.seed = seed;
	}

	public boolean isDebugInternalChecks() {
		return this.debugInternalChecks;
	}

	/**
	 * Enables additional (time-consuming) debug options in the core solving component.
	 */
	public void setDebugInternalChecks(boolean debugInternalChecks) {
		this.debugInternalChecks = debugInternalChecks;
	}

	public Heuristic getBranchingHeuristic() {
		return this.branchingHeuristic;
	}

	/**
	 * Use the given branching heuristic.
	 */
	public void setBranchingHeuristic(Heuristic branchingHeuristic) {
		this.branchingHeuristic = branchingHeuristic;
	}

	public void setBranchingHeuristicName(String branchingHeuristicName) {
		this.branchingHeuristic = Heuristic.valueOf(branchingHeuristicName.replace("-", "_").toUpperCase());
	}

	public BinaryNoGoodPropagationEstimationStrategy getMomsStrategy() {
		return momsStrategy;
	}

	public void setMomsStrategy(BinaryNoGoodPropagationEstimationStrategy momsStrategy) {
		this.momsStrategy = momsStrategy;
	}

	public void setMomsStrategyName(String momsStrategyName) {
		this.momsStrategy = BinaryNoGoodPropagationEstimationStrategy.valueOf(momsStrategyName);
	}

	public boolean isQuiet() {
		return this.quiet;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	public boolean isPrintStats() {
		return this.printStats;
	}

	public void setPrintStats(boolean printStats) {
		this.printStats = printStats;
	}

	public boolean isDisableJustificationSearch() {
		return this.disableJustificationSearch;
	}

	public void setDisableJustificationSearch(boolean disableJustificationSearch) {
		this.disableJustificationSearch = disableJustificationSearch;
	}

	public boolean isSortAnswerSets() {
		return this.sortAnswerSets;
	}

	public void setSortAnswerSets(boolean sortAnswerSets) {
		this.sortAnswerSets = sortAnswerSets;
	}

	public List<Integer> getReplayChoices() {
		return replayChoices;
	}

	public void setReplayChoices(List<Integer> replayChoices) {
		this.replayChoices = replayChoices;
	}

	public void setReplayChoices(String replayChoices) {
		this.replayChoices = Arrays.stream(replayChoices.split(",")).map(String::trim).map(Integer::valueOf).collect(Collectors.toList());
	}

	public boolean isEvaluateStratifiedPart() {
		return this.evaluateStratifiedPart;
	}

	public void setEvaluateStratifiedPart(boolean evaluateStratifiedPart) {
		this.evaluateStratifiedPart = evaluateStratifiedPart;
	}

	public boolean isDisableNoGoodDeletion() {
		return this.disableNoGoodDeletion;
	}

	public void setDisableNoGoodDeletion(boolean disableNoGoodDeletion) {
		this.disableNoGoodDeletion = disableNoGoodDeletion;
	}

	public String getGrounderToleranceConstraints() {
		return grounderToleranceConstraints;
	}

	public void setGrounderToleranceConstraints(String grounderToleranceConstraints) {
		this.grounderToleranceConstraints = grounderToleranceConstraints;
	}

	public String getGrounderToleranceRules() {
		return grounderToleranceRules;
	}

	public void setGrounderToleranceRules(String grounderToleranceRules) {
		this.grounderToleranceRules = grounderToleranceRules;
	}

	public boolean isGrounderAccumulatorEnabled() {
		return grounderAccumulatorEnabled;
	}

	public void setGrounderAccumulatorEnabled(boolean grounderAccumulatorEnabled) {
		this.grounderAccumulatorEnabled = grounderAccumulatorEnabled;
	}

	public String getAtomSeparator() {
		return this.atomSeparator;
	}

	public void setAtomSeparator(String atomSeparator) {
		this.atomSeparator = atomSeparator;
	}

	public AggregateRewritingConfig getAggregateRewritingConfig() {
		return this.aggregateRewritingConfig;
	}

	public void setAggregateRewritingConfig(AggregateRewritingConfig aggregateRewritingConfig) {
		this.aggregateRewritingConfig = aggregateRewritingConfig;
	}

}
