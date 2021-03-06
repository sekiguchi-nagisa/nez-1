package nez.lang.expr;

import nez.ast.SourcePosition;
import nez.lang.Expression;
import nez.lang.GrammarTransducer;
import nez.lang.PossibleAcceptance;
import nez.lang.Typestate;
import nez.lang.Visa;
import nez.parser.AbstractGenerator;
import nez.parser.Instruction;
import nez.util.StringUtils;

public class Treplace extends Term {
	public String value;

	Treplace(SourcePosition s, String value) {
		super(s);
		this.value = value;
	}

	@Override
	public final boolean equalsExpression(Expression o) {
		if (o instanceof Treplace) {
			return this.value.equals(((Treplace) o).value);
		}
		return false;
	}

	@Override
	public final void format(StringBuilder sb) {
		sb.append(StringUtils.quoteString('`', this.value, '`'));
	}

	@Override
	public boolean isConsumed() {
		return false;
	}

	@Override
	public int inferTypestate(Visa v) {
		return Typestate.OperationType;
	}

	@Override
	public short acceptByte(int ch) {
		return PossibleAcceptance.Unconsumed;
	}

	@Override
	public Expression reshape(GrammarTransducer m) {
		return m.reshapeTreplace(this);
	}

	@Override
	public Instruction encode(AbstractGenerator bc, Instruction next, Instruction failjump) {
		return bc.encodeTreplace(this, next);
	}
}