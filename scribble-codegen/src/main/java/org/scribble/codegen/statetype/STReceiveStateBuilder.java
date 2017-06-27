/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.codegen.statetype;

import java.util.List;

import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;

public abstract class STReceiveStateBuilder extends STStateChanBuilder
{
	protected final STReceiveActionBuilder rb;
	
	public STReceiveStateBuilder(STReceiveActionBuilder rb)
	{
		this.rb = rb;
	}
	
	@Override
	public String build(STStateChanAPIBuilder api, EState s)
	{
		String out = getPreamble(api, s);
		
		List<EAction> as = s.getActions();
		if (as.size() > 1)
		{
			throw new RuntimeException("Shouldn't get in here: " + as);
		}
		out += "\n\n";
		out += this.rb.build(api, s, as.get(0));

		return out;
	}
}
