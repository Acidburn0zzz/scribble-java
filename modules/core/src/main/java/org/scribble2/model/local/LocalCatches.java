package org.scribble2.model.local;

import java.util.List;

import org.scribble2.model.MessageNode;
import org.scribble2.model.name.simple.RoleNode;


public class LocalCatches extends LocalInterrupt
{
	protected LocalCatches(RoleNode src, List<MessageNode> msgs)
	{
		super(src, msgs);
		// TODO Auto-generated constructor stub
	}

	/*public LocalCatches(CommonTree ct, RoleNode src, List<MessageNode> msgs, List<RoleNode> dests)
	{
		this(ct, src, msgs, dests, null, null);
	}

	protected LocalCatches(CommonTree ct, RoleNode src, List<MessageNode> msgs, List<RoleNode> dests, GlobalInterruptContext gicontext, Env env)
	{
		super(ct, src, msgs, dests, gicontext, env);
	}

	@Override
	protected LocalThrows reconstruct(CommonTree ct, RoleNode src, List<MessageNode> msgs, List<RoleNode> dests, GlobalInterruptContext icontext, Env env)
	{
		return new LocalThrows(ct, src, msgs, dests, icontext, env);
	}
	
	/*@Override
	public LocalCatches visitChildren(NodeVisitor nv) throws ScribbleException
	{
		LocalInterrupt interr = super.visitChildren(nv);
		return new LocalCatches(interr.ct, interr.src, interr.msgs, interr.dests, (GlobalInterruptContext) interr.getContext());
	}* /
	
	@Override
	public String toString()
	{
		String s = AntlrConstants.CATCHES_KW + " " + this.msgs.get(0).toString();
		for (MessageNode msg : this.msgs.subList(1, this.msgs.size()))
		{
			s += ", " + msg;
		}
		return s + " " + AntlrConstants.FROM_KW + " " + this.src.toName() + ";";
	}

	/*@Override
	public void toGraph(GraphBuilder gb)
	{
		throw new RuntimeException("Shouldn't get in here.");
	}*/
}
