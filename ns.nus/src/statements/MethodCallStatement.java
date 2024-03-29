/*
 * Copyright (C) 2018-2019  Dinu Blanovschi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package statements;

import lang.METHOD;
import tokens.Token;
import tokens.Value;
import tokens.ValueEvaluator;
import variables.Variable;

import java.util.Map;

public class MethodCallStatement extends Statement {
	private final Token[][] argTokens;
	private final METHOD method;

	public MethodCallStatement(METHOD method, Token[][] v) {
		super(Statement_TYPE.METHOD_CALL);
		this.method = method;
		this.argTokens = v;
	}

	public MethodCallStatement(METHOD method) {
		super(Statement_TYPE.METHOD_CALL);
		this.method = method;
		argTokens = null;
	}

	@Override
	public void run(Map<String, Variable> variables) {
		if (argTokens != null) {
			Value[] val = new Value[argTokens.length];
			for (int i = 0; i < val.length; i++)
				val[i] = ValueEvaluator.evaluate(argTokens[i], variables);
			method.call(val);
		} else method.call(new Value[0]);
	}

	public String assembly() {
		return method.assembly(argTokens);
	}
}
