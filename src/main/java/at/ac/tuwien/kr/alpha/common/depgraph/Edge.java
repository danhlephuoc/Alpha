/**
 * Copyright (c) 2019, the Alpha Team.
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
package at.ac.tuwien.kr.alpha.common.depgraph;

/**
 * An edge in a dependency graph.
 * 
 * Copyright (c) 2019, the Alpha Team.
 */
public class Edge {

	private final Node target;
	private final boolean sign;

	/**
	 * Creates a new edge of a dependency graph. Read as "target depends on source" Sign indicates if the dependency is positive or negative (target node
	 * depends on default negated atom). NOTE: Working assumption is to treat strong negation as a positive dependency
	 * 
	 * @param target
	 * @param sign
	 */
	public Edge(Node target, boolean sign) {
		this.target = target;
		this.sign = sign;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Edge)) {
			return false;
		}
		Edge other = (Edge) o;
		return this.target.equals(other.target) && this.sign == other.sign;
	}

	@Override
	public int hashCode() {
		return ("" + target.getPredicate().toString() + sign).hashCode();
	}

	@Override
	public String toString() {
		return "(" + (sign ? "+" : "-") + ") ---> " + target.toString();
	}
	
	public Node getTarget() {
		return target;
	}

	public boolean getSign() {
		return sign;
	}

}
