package org.scribble2.model;

import java.util.LinkedList;
import java.util.List;

import org.scribble2.model.name.simple.RoleNode;

// Maybe factor out a base class with MessageTransfer (the list of dest roles has a different meaning though)
// Can't use abstract/reconstruct pattern, LocalInterrupt has different arguments -- add dummy dests field here to make reconstruct work?
public class Interrupt extends ModelNodeBase//AbstractSimpleInteractionNode
{
	public final RoleNode src;
	public final List<MessageNode> msgs;

	// Role, not RoleNode like MessageTransfer -- wrap inside generic "type info" container?
	//public final List<Role> dests;  // Empty until set by WellFormednessChecker for GlobalInterrupt (directly parsed for LocalInterrupt)

	/*protected Interrupt(CommonTree ct, RoleNode src, List<MessageNode> msgs)
	{
		this(ct, src, msgs, null, null);
	}

	//public Interrupt(CommonTree ct, RoleNode src, List<MessageNode> msgs)//, List<Role> dests)
	protected Interrupt(CommonTree ct, RoleNode src, List<MessageNode> msgs, GlobalInterruptContext ncontext)
	{
		this(ct, src, msgs, ncontext, null);
	}*/

	protected Interrupt(RoleNode src, List<MessageNode> msgs)//, GlobalInterruptContext gicontext, Env env)
	{
		this.src = src;
		this.msgs = new LinkedList<>(msgs);
	}

	@Override
	protected ModelNodeBase copy()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	//protected abstract Interrupt reconstruct(CommonTree ct, RoleNode src, List<MessageNode> msgs, GlobalInterruptContext ncontext, Env env);

	/*@Override
	public Interrupt leaveWFChoiceCheck(WellFormedChoiceChecker checker) throws ScribbleException
	{
		Role src = this.src.toName();
		WellFormedChoiceEnv env = checker.getEnv();
		for (Message msg : this.msgs.stream().map((mn) -> mn.toMessage()).collect(Collectors.toList()))
		{
			for (Role dest : ((GlobalInterruptContext) getContext()).getDestinations())
			{
				// Recorded in the interruptible env (not the block or the parent)
				//checker.setEnv(checker.getEnv().addInterrupt(src, dest, msg.toScopedMessage(checker.getScope())));  // No: src must be enabled, and if receiver may be enabled by this interrupt then it must also be receiving normally inside the interruptible block
				//checker.getEnv().addMessage(src, dest, msg);
				env = env.addInterrupt(src, dest, msg.toScopedMessage(checker.getScope()));
			}
		}
		checker.setEnv(env);
		return this;
	}

	/*@Override
	public Interrupt leave(EnvVisitor nv) throws ScribbleException
	{
		Interrupt gi = (Interrupt) super.leave(nv);
		Env env = nv.getEnv();
		Role src = gi.src.toName();
		List<Role> tmp = new LinkedList<>(gi.dests);  // For GlobalInterrupt, won't work till after well-formedness checked
		tmp.remove(src);
		for (MessageNode msg : this.msgs)
		{
			for (Role dest : tmp)
			{
				env.ops.addOperator(src, dest, msg.getOperator());
			}
		}
		return gi;
	}
	
	@Override
	public Interrupt substitute(Substitutor subs) throws ScribbleException
	{
		// Follows GlobalMessageTransfer
		RoleNode src = subs.substituteRole(this.src.toName());
		List<MessageNode> msgs = new LinkedList<>();
		for (MessageNode msg : this.msgs)
		{
			if (msg.isParameterNode())
			{
				msgs.add((MessageNode) subs.substituteParameter(((ParameterNode) msg).toName()));
			}
			else
			{
				msgs.add((MessageNode) subs.visit((AbstractNode) msg));
			}
		}
		List<Role> dests = new LinkedList<>();
		for (Role dest : this.dests)
		{
			dests.add(subs.substituteRole(dest).toName());
		}
		return new Interrupt(this.ct, src, msgs, dests);
	}* /
	
	@Override
	public Interrupt visitChildren(NodeVisitor nv) throws ScribbleException
	{
		RoleNode src = (RoleNode) visitChild(this.src, nv);
		List<MessageNode> msgs = new LinkedList<>();
		for (MessageNode msg : this.msgs)
		{
			//msgs.add(MessageTransfer.visitMessageNode(nv, msg));
			msgs.add((MessageNode) visitChild(msg, nv));
		}
		return new Interrupt(this.ct, src, msgs, getContext(), getEnv());//, this.dests);
		//return reconstruct(this.ct, src, msgs, getContext(), getEnv());
	}
	
	@Override
	public GlobalInterruptContext getContext()
	{
		return (GlobalInterruptContext) super.getContext();
	}*/
	
	/*public List<Operator> getOperators(Env env)
	{
		List<Operator> ops = new LinkedList<>();
		for (MessageNode msg : this.msgs)
		{
			// FIXME: factor out scope prefix manipulation
			ops.add(new Operator(env.scopes.getCurrentPrefix() + msg.getOperator().text));
		}
		return ops;
	}*/
}
