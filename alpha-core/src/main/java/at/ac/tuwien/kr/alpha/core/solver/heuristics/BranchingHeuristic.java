/**
 * Copyright (c) 2016, 2018-2019 Siemens AG
 * All rights reserved.
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
package at.ac.tuwien.kr.alpha.core.solver.heuristics;

import at.ac.tuwien.kr.alpha.core.common.NoGood;
import at.ac.tuwien.kr.alpha.core.solver.learning.GroundConflictNoGoodLearner;

import static at.ac.tuwien.kr.alpha.core.programs.atoms.Literals.atomToLiteral;

import java.util.Collection;

/**
 * A heuristic that selects an atom to choose on.
 * 
 * Copyright (c) 2016, 2018 Siemens AG
 *
 */
public interface BranchingHeuristic {
	int DEFAULT_CHOICE_ATOM = 0;
	int DEFAULT_CHOICE_LITERAL = atomToLiteral(DEFAULT_CHOICE_ATOM);

	/**
	 * Stores a newly violated {@link NoGood} and updates associated activity and sign counters.
	 * 
	 * @param violatedNoGood
	 */
	void violatedNoGood(NoGood violatedNoGood);

	/**
	 * Processes the result of a conflict analysis, i.e. counts literals in
	 * {@link NoGood}s responsible for the conflict and stores a newly learned
	 * {@link NoGood}.
	 * 
	 * @param analysisResult
	 */
	void analyzedConflict(GroundConflictNoGoodLearner.ConflictAnalysisResult analysisResult);

	/**
	 * Stores a newly grounded {@link NoGood} and updates associated activity counters.
	 * 
	 * @param newNoGood
	 */
	void newNoGood(NoGood newNoGood);
	
	/**
	 * @see #newNoGood(NoGood)
	 */
	default void newNoGoods(Collection<NoGood> newNoGoods) {
		newNoGoods.forEach(this::newNoGood);
	}
	
	/**
	 * Determines a literal (= atom + sign) to choose.
	 *
	 * @return the literal to choose, or {@link BranchingHeuristic#DEFAULT_CHOICE_LITERAL} if no such atom can be determined.
	 */
	int chooseLiteral();

	default void growForMaxAtomId(int maxAtomId) {
	}
}
