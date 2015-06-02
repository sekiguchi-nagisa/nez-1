package nez.lang;

import nez.ast.SourcePosition;
import nez.util.StringUtils;
import nez.util.UList;
import nez.vm.Instruction;
import nez.vm.NezCompiler;

public class ByteChar extends Terminal {
	boolean binary;
	public final boolean isBinary() {
		return this.binary;
	}
	public int byteChar;
	ByteChar(SourcePosition s, boolean binary, int ch) {
		super(s);
		this.byteChar = ch;
		this.binary = binary;
	}
	@Override
	public final boolean equalsExpression(Expression o) {
		if(o instanceof ByteChar) {
			return this.byteChar == ((ByteChar)o).byteChar && this.binary == ((ByteChar)o).isBinary();
		}
		return false;
	}
	@Override
	protected final void format(StringBuilder sb) {
		sb.append(StringUtils.stringfyCharacter(this.byteChar));
	}
	@Override
	public String getPredicate() {
		return "byte " + byteChar;
	}
	@Override
	public String key() { 
		return binary ? "b'" + byteChar : "'" + byteChar;
	}	
	@Override
	public Expression reshape(GrammarReshaper m) {
		return m.reshapeByteChar(this);
	}
	@Override
	public boolean isConsumed(Stacker stacker) {
		return true;
	}
	@Override
	public boolean checkAlwaysConsumed(GrammarChecker checker, String startNonTerminal, UList<String> stack) {
		return true;
	}
	@Override
	public short acceptByte(int ch, int option) {
		return (byteChar == ch) ? Acceptance.Accept : Acceptance.Reject;
	}
	@Override
	public Instruction encode(NezCompiler bc, Instruction next, Instruction failjump) {
		return bc.encodeByteChar(this, next, failjump);
	}	
	@Override
	protected int pattern(GEP gep) {
		return this.size();
	}
	@Override
	protected void examplfy(GEP gep, StringBuilder sb, int p) {
		sb.append((char)this.byteChar);
	}
}
