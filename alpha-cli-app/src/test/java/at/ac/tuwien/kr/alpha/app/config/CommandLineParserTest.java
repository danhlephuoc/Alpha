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
package at.ac.tuwien.kr.alpha.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import at.ac.tuwien.kr.alpha.api.config.AlphaConfig;

public class CommandLineParserTest {

	private static final String DEFAULT_COMMAND_LINE = "java -jar Alpha-bundled.jar";
	private static final Consumer<String> DEFAULT_ABORT_ACTION = (msg) -> { };

	/**
	 * Tests that a help message is written to the consumer configured in the
	 * parser.
	 * 
	 * @throws ParseException shouldn't happen
	 */
	@Test
	public void help() throws ParseException {
		StringBuilder bld = new StringBuilder();
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, (msg) -> bld.append(msg));
		parser.parseCommandLine(new String[] {"-h"});
		assertTrue(!(bld.toString().isEmpty()));
	}

	@Test
	public void basicUsageWithFile() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig ctx = parser.parseCommandLine(new String[] {"-i", "someFile.asp", "-i", "someOtherFile.asp"});
		assertEquals(Arrays.asList(new String[] {"someFile.asp", "someOtherFile.asp"}), ctx.getInputConfig().getFiles());
	}

	@Test
	public void basicUsageWithString() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig ctx = parser.parseCommandLine(new String[] {"-str", "b :- a.", "-str", "c :- a, b."});
		assertEquals(Arrays.asList(new String[] {"b :- a.", "c :- a, b."}), ctx.getInputConfig().getAspStrings());
	}

	@Test
	public void invalidUsageNoInput() {
		assertThrows(ParseException.class, () -> {
			CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
			parser.parseCommandLine(new String[] {});
		});
	}

	@Test
	public void moreThanOneInputSource() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		parser.parseCommandLine(new String[] {"-i", "a.b", "-i", "b.c", "-str", "aString."});
	}

	@Test
	public void invalidUsageMissingInputFlag() {
		assertThrows(ParseException.class, () -> {
			CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
			parser.parseCommandLine(new String[] {"-i", "a.b", "b.c"});
		});
	}

	@Test
	public void numAnswerSets() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig ctx = parser.parseCommandLine(new String[] {"-str", "aString.", "-n", "00435"});
		assertEquals(435, ctx.getInputConfig().getNumAnswerSets());
	}
	
	@Test
	public void noInputGiven() {
		assertThrows(ParseException.class, () -> {
			CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
			parser.parseCommandLine(new String[] {});
		});
	}

	@Test
	public void replay() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig alphaConfig = parser.parseCommandLine(new String[]{"-str", "aString.", "-rc", "\"1,2, 3\""});
		assertEquals(Arrays.asList(1, 2, 3), alphaConfig.getSystemConfig().getReplayChoices());
	}

	@Test
	public void replayWithNonNumericLiteral() {
		assertThrows(ParseException.class, () -> {
			CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
			parser.parseCommandLine(new String[]{"-str", "aString.", "-rc", "\"1, 2, x\""});
		});
	}

	@Test
	public void grounderToleranceConstraints_numeric() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig alphaConfig = parser.parseCommandLine(new String[]{"-str", "aString.", "-gtc", "-1"});
		assertEquals("-1", alphaConfig.getSystemConfig().getGrounderToleranceConstraints());
	}

	@Test
	public void grounderToleranceConstraints_string() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig alphaConfig = parser.parseCommandLine(new String[]{"-str", "aString.", "-gtc", "strict"});
		assertEquals("strict", alphaConfig.getSystemConfig().getGrounderToleranceConstraints());
	}

	@Test
	public void grounderToleranceRules_numeric() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig alphaConfig = parser.parseCommandLine(new String[]{"-str", "aString.", "-gtr", "1"});
		assertEquals("1", alphaConfig.getSystemConfig().getGrounderToleranceRules());
	}

	@Test
	public void grounderToleranceRules_string() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig alphaConfig = parser.parseCommandLine(new String[]{"-str", "aString.", "-gtr", "permissive"});
		assertEquals("permissive", alphaConfig.getSystemConfig().getGrounderToleranceRules());
	}

	@Test
	public void noInstanceRemoval() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig alphaConfig = parser.parseCommandLine(new String[]{"-str", "aString.", "-acc"});
		assertTrue(alphaConfig.getSystemConfig().isGrounderAccumulatorEnabled());
	}

	@Test
	public void disableStratifiedEval() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig ctx = parser.parseCommandLine(new String[]{"-i", "someFile.asp", "-i", "someOtherFile.asp", "-dse"});
		assertFalse(ctx.getSystemConfig().isEvaluateStratifiedPart());
	}
	
	@Test
	public void disableStratifiedEvalLongOpt() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig ctx = parser.parseCommandLine(new String[]{"-i", "someFile.asp", "-i", "someOtherFile.asp", "--disableStratifiedEvaluation"});
		assertFalse(ctx.getSystemConfig().isEvaluateStratifiedPart());
	}

	@Test
	public void atomSeparator() throws ParseException {
		CommandLineParser parser = new CommandLineParser(DEFAULT_COMMAND_LINE, DEFAULT_ABORT_ACTION);
		AlphaConfig cfg = parser.parseCommandLine(new String[]{"-str", "aString.", "-sep", "some-string"});
		assertEquals("some-string", cfg.getSystemConfig().getAtomSeparator());
	}

}
