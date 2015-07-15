package org.scribble.visit.env;

import java.util.List;

// Immutable
// Generic parameter inhibits further subclassing of concrete Env classes
public abstract class Env<E extends Env<?>>
{
	protected Env()
	{

	}
	
	protected abstract E copy();  // Shallow copy

	// Default push for entering a compound interaction context (e.g. used in CompoundInteractionDelegate)
	public abstract E enterContext();

	// Mostly for merging a compound interaction node context into the parent block context
	// Usually used in the base compound interaction node del when leaving and restoring the parent env in the visitor env stack (e.g. CompoundInteractionNodeDel for WF-choice)
  // By default: merge just discards the argument(s) -- not all EnvVisitors need to merge (e.g. projection)
	protected Env<E> mergeContext(E env)
	{
		//return mergeContexts(Arrays.asList(env));
		return this;
	}

	// Mostly for merging child blocks contexts into the parent compound interaction node context
	// Usually used in the parent compound interaction node del to update the parent context after visting each child block before leaving (e.g. GChoiceDel for WF-choice)
	protected Env<E> mergeContexts(List<E> envs)
	{
		return this;
	}
}
