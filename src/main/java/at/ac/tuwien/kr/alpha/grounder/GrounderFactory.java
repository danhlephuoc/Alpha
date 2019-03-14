package at.ac.tuwien.kr.alpha.grounder;

import at.ac.tuwien.kr.alpha.common.AtomStore;
import at.ac.tuwien.kr.alpha.common.Predicate;
import at.ac.tuwien.kr.alpha.common.program.Program;
import at.ac.tuwien.kr.alpha.grounder.bridges.Bridge;

public final class GrounderFactory {
	public static Grounder getInstance(String name, Program program, AtomStore atomStore, java.util.function.Predicate<Predicate> filter, boolean useCountingGridNormalization, Bridge... bridges) {
		switch (name.toLowerCase()) {
			case "naive":
				return new NaiveGrounder(program, atomStore, filter, useCountingGridNormalization, bridges);
		}
		throw new IllegalArgumentException("Unknown grounder requested.");
	}

	public static Grounder getInstance(String name, Program program, AtomStore atomStore) {
		return getInstance(name, program, atomStore, p -> true, false);
	}
}
