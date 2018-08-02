/**
 * Copyright (c) 2016-2018, the Alpha Team.
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
package at.ac.tuwien.kr.alpha.common;

import at.ac.tuwien.kr.alpha.solver.ThriceTruth;

import java.util.Iterator;
import java.util.Set;

import static at.ac.tuwien.kr.alpha.common.Literals.*;

public interface Assignment {
	Entry get(int atom);

	/**
	 * Returns the truth value assigned to an atom.
	 * @param atom the id of the atom.
	 * @return the truth value; null if atomId is not assigned.
	 */
	default ThriceTruth getTruth(int atom) {
		final Entry entry = get(atom);
		return entry == null ? null : entry.getTruth();
	}

	/**
	 * Returns the weak decision level of the atom if it is assigned.
	 * @param atom the atom.
	 * @return the weak decision level of the atom if it is assigned; otherwise any value may be returned.
	 */
	int getWeakDecisionLevel(int atom);

	/**
	 * Returns the strong decision level of the atom.
	 * @param atom the atom.
	 * @return the strong decision level of the atom or -1.
	 */
	int getStrongDecisionLevel(int atom);

	default boolean isAssigned(int atom) {
		return get(atom) != null;
	}

	/**
	 * Determines if the given {@code noGood} is undefined in the current assignment.
	 *
	 * @param noGood
	 * @return {@code true} iff at least one literal in {@code noGood} is unassigned.
	 */
	default boolean isUndefined(NoGood noGood) {
		for (Integer literal : noGood) {
			if (!isAssigned(atomOf(literal))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an iterator over all new assignments. New assignments are only returned once.
	 * @return an iterator over all new assignments to TRUE or MBT.
	 */
	Iterator<Integer> getNewPositiveAssignmentsIterator();

	Pollable getAssignmentsToProcess();

	/**
	 * Returns the weak decision level of the atom considering also out-of-order assignments.
	 * @param atom the atom.
	 * @return the weakDecisionLevel of the atom if it is not an out-of-order assignment, otherwise the lowest
	 * decision level at which it will be re-assigned.
	 */
	int getRealWeakDecisionLevel(int atom);

	interface Pollable {
		int peek();
		int remove();
		boolean isEmpty();
	}

	interface Entry {
		ThriceTruth getTruth();
		int getAtom();
		int getDecisionLevel();
		NoGood getImpliedBy();
		int getPropagationLevel();

		boolean hasPreviousMBT();
		int getMBTDecisionLevel();
		int getMBTPropagationLevel();
		NoGood getMBTImpliedBy();

		default int getPropagationLevelRespectingLowerMBT() {
			return hasPreviousMBT() ? getMBTPropagationLevel() : getPropagationLevel();
		}

		default NoGood getImpliedByRespectingLowerMBT() {
			if (hasPreviousMBT()) {
				return getMBTImpliedBy();
			}
			return getImpliedBy();
		}

		/**
		 * Returns the literal corresponding to this assignment
		 * @return atomId if this entry is TRUE/MBT and -atomId if entry is FALSE.
		 */
		default int getLiteral() {
			return atomToLiteral(getAtom(), getTruth().toBoolean());
		}

		/**
		 * Returns the weakly assigned decision level.
		 * @return the decision level of a previous MBT if it exists, otherwise the decision level of this entry.
		 */
		default int getWeakDecisionLevel() {
			return hasPreviousMBT() ? getMBTDecisionLevel() : getDecisionLevel();
		}

	}

	int getDecisionLevel();

	/**
	 * TODO: rename to isSatisfied? (cf. issue #121)
	 * @param literal
	 * @return {@code true} iff {@code literal} is assigned,
	 * 	and either it is positive and its value is {@link ThriceTruth#TRUE} or {@link ThriceTruth#MBT}
	 * 	or it is negative and its value is {@link ThriceTruth#FALSE}.
	 */
	default boolean isViolated(int literal) {
		final int atom = atomOf(literal);
		final ThriceTruth truth = getTruth(atom);

		// For unassigned atoms, any literal is not violated.
		return truth != null && isNegated(literal) != truth.toBoolean();

	}

	/**
	 * 
	 * @param literal
	 * @return {@code true} iff {@code literal} is assigned,
	 * 	and either it is positive and its value is {@link ThriceTruth#FALSE}
	 * 	or it is negative and its value is {@link ThriceTruth#TRUE} or {@link ThriceTruth#MBT}.
	 */
	default boolean isUnsatisfied(int literal) {
		final int atom = atomOf(literal);
		final ThriceTruth truth = getTruth(atom);
		return truth != null && isNegated(literal) == truth.toBoolean();
	}
	
	/**
	 * Checks if the given literal is satisfied under the current partial assignment
	 * @param literal
	 * @return {@code true} iff ({@code literal} is positive and
	 * assigned either {@link ThriceTruth#TRUE} or {@link ThriceTruth#MBT})
	 * or ({@code literal} is negative and either unassigned or {@link ThriceTruth#FALSE}). 
	 */
	default boolean isSatisfied(int literal) {
		final int atom = atomOf(literal);
		final ThriceTruth truth = getTruth(atom);
		final boolean assignedTrue = truth != null && truth.toBoolean();
		return (!isNegated(literal) && assignedTrue) || (isNegated(literal) && !assignedTrue);
	}

	default boolean violates(NoGood noGood) {
		// Check each NoGood, if it is violated
		for (Integer noGoodLiteral : noGood) {
			if (!isAssigned(atomOf(noGoodLiteral)) || !isViolated(noGoodLiteral)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns all atomIds that are assigned TRUE in the current assignment.
	 * @return a list of all true assigned atoms.
	 */
	Set<Integer> getTrueAssignments();

	/**
	 * Reports how many atoms are assigned to must-be-true currently. If this method returns
	 * zero, the assignment is guaranteed to be free of must-be-true values (i.e. it only
	 * contains assignments to either true or false).
	 * @return the count of must-be-true values in the asignment.
	 */
	int getMBTCount();

	void backtrack();

	void growForMaxAtomId(int maxAtomId);
}
