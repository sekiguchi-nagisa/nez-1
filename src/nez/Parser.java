package nez;

import nez.ast.CommonTree;
import nez.ast.CommonTreeTransducer;
import nez.ast.TreeTransducer;
import nez.io.SourceContext;
import nez.main.NezProfier;
import nez.main.Verbose;
import nez.parser.GenerativeGrammar;
import nez.parser.Instruction;
import nez.parser.MemoTable;
import nez.parser.NezCode;
import nez.parser.NezCompiler;
import nez.parser.ParsingMachine;

public class Parser {
	private GenerativeGrammar gg;
	protected Strategy option;
	protected NezCode compiledCode = null;

	public Parser(GenerativeGrammar gg, Strategy option) {
		this.gg = gg;
		this.option = option;
	}

	public final GenerativeGrammar getGrammar() {
		return gg;
	}

	public final NezCode getCompiledCode() {
		return compiledCode;
	}

	protected NezProfier prof = null;

	public void setProfiler(NezProfier prof) {
		this.prof = prof;
		if (prof != null) {
			this.compile();
			// prof.setFile("G.File", this.start.getGrammarFile().getURN());
			prof.setCount("G.Production", this.gg.size());
			prof.setCount("G.Instruction", this.compiledCode.getInstructionSize());
			prof.setCount("G.MemoPoint", this.compiledCode.getMemoPointSize());
		}
	}

	public NezProfier getProfiler() {
		return this.prof;
	}

	public void logProfiler() {
		if (prof != null) {
			prof.log();
		}
	}

	/* -------------------------------------------------------------------- */

	public Instruction compile() {
		if (compiledCode == null) {
			NezCompiler bc = NezCompiler.newCompiler(this.option);
			compiledCode = bc.compile(gg);
		}
		return compiledCode.getStartPoint();
	}

	public final boolean perform(ParsingMachine machine, SourceContext s, TreeTransducer treeTransducer) {
		Instruction pc = this.compile();
		s.init(newMemoTable(s), treeTransducer);
		if (prof != null) {
			s.startProfiling(prof);
			boolean matched = machine.run(pc, s);
			s.doneProfiling(prof);
			if (Verbose.PackratParsing) {
				this.compiledCode.dumpMemoPoints();
			}
			return matched;
		}
		return machine.run(pc, s);
	}

	protected ParsingMachine newParsingMachine() {
		// return new TraceMachine(); // debug
		return new ParsingMachine();
	}

	protected final MemoTable newMemoTable(SourceContext sc) {
		return MemoTable.newTable(option, sc.length(), 32, this.compiledCode.getMemoPointSize());
	}

	/* --------------------------------------------------------------------- */

	public final boolean match(SourceContext s) {
		return perform(newParsingMachine(), s, null);
	}

	public final boolean match(String str) {
		SourceContext sc = SourceContext.newStringContext(str);
		if (perform(newParsingMachine(), sc, null)) {
			return (!sc.hasUnconsumed());
		}
		return false;
	}

	public Object parse(SourceContext sc, TreeTransducer treeTransducer) {
		long startPosition = sc.getPosition();
		if (!this.perform(newParsingMachine(), sc, treeTransducer)) {
			return null;
		}
		return sc.getParseResult(startPosition, sc.getPosition());
	}

	public final CommonTree parseCommonTree(SourceContext sc) {
		return (CommonTree) this.parse(sc, new CommonTreeTransducer());
	}

	public final CommonTree parseCommonTree(String str) {
		SourceContext sc = SourceContext.newStringContext(str);
		return (CommonTree) this.parse(sc, new CommonTreeTransducer());
	}

}
