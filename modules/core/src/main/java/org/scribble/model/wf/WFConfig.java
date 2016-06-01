package org.scribble.model.wf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.model.local.Accept;
import org.scribble.model.local.Connect;
import org.scribble.model.local.Disconnect;
import org.scribble.model.local.EndpointState;
import org.scribble.model.local.EndpointState.Kind;
import org.scribble.model.local.IOAction;
import org.scribble.model.local.Receive;
import org.scribble.model.local.Send;
import org.scribble.sesstype.name.Role;

public class WFConfig
{
	public final Map<Role, EndpointState> states;
	//public final Map<Role, Map<Role, Send>> buffs;  // dest -> src -> msg
	public final WFBuffers buffs;
	
	//public WFConfig(Map<Role, EndpointState> state, Map<Role, Map<Role, Send>> buff)
	public WFConfig(Map<Role, EndpointState> state, WFBuffers buffs)
	{
		this.states = Collections.unmodifiableMap(state);
		//this.buffs = Collections.unmodifiableMap(buff.keySet().stream() .collect(Collectors.toMap((k) -> k, (k) -> Collections.unmodifiableMap(buff.get(k)))));
		//this.buffs = Collections.unmodifiableMap(buff);
		this.buffs = buffs;
	}

	// FIXME: rename: not just termination, could be unconnected/uninitiated
	//public boolean isEnd()
	public boolean isSafeTermination()
	{
		//return this.states.values().stream().allMatch((s) -> s.isTerminal()) && this.buffs.isEmpty();
		for (Role r : this.states.keySet())
		{
			if (!canSafelyTerminate(r))
			{
				return false;
			}
		}
		return true;
	}

	public boolean canSafelyTerminate(Role r)
	{
		EndpointState s = this.states.get(r);
		return
				!((s.isTerminal() && !this.buffs.isEmpty(r))
					||
					(!s.isTerminal() &&
						//(!(s.getStateKind().equals(Kind.UNARYINPUT) && s.getTakeable().iterator().next().isAccept())  // Accept state now distinguished
						(!s.getStateKind().equals(Kind.ACCEPT)
									// FIXME: could be blocked on unary accept part way through the protocol -- but can't happen?
						|| this.states.keySet().stream().anyMatch((rr) -> !r.equals(rr) && this.buffs.isConnected(r, rr))))
									// FIXME: isConnected is not symmetric, and could disconnect all part way through protocol -- but can't happen?
					// Above assumes initial is not terminal (holds for EFSMs), and doesn't check buffer is empty (i.e. for orphan messages)
				);
	}
	
	public List<WFConfig> accept(Role r, IOAction a)
	{
		List<WFConfig> res = new LinkedList<>();
		
		List<EndpointState> succs = this.states.get(r).takeAll(a);
		for (EndpointState succ : succs)
		{
			Map<Role, EndpointState> tmp1 = new HashMap<>(this.states);
			//Map<Role, Map<Role, Send>> tmp2 = new HashMap<>(this.buffs);
		
			tmp1.put(r, succ);

			/*Map<Role, Send> tmp3 = new HashMap<>(tmp2.get(a.peer));
			tmp2.put(a.peer, tmp3);* /
			Map<Role, Send> tmp3 = tmp2.get(a.peer);
			if (a.isSend())
			{
				tmp3.put(r, (Send) a);
			}
			else
			{
				tmp3.put(r, null);
			}*/
			WFBuffers tmp2 = 
					a.isSend()       ? this.buffs.send(r, (Send) a)
				: a.isReceive()    ? this.buffs.receive(r, (Receive) a)
				: a.isDisconnect() ? this.buffs.disconnect(r, (Disconnect) a)
				: null;
			if (tmp2 == null)
			{
				throw new RuntimeException("Shouldn't get in here: " + a);
			}
			res.add(new WFConfig(tmp1, tmp2));
		}

		return res;
	}

	public List<WFConfig> sync(Role r1, IOAction a1, Role r2, IOAction a2)
	{
		List<WFConfig> res = new LinkedList<>();
		
		List<EndpointState> succs1 = this.states.get(r1).takeAll(a1);
		List<EndpointState> succs2 = this.states.get(r2).takeAll(a2);
		for (EndpointState succ1 : succs1)
		{
			for (EndpointState succ2 : succs2)
			{
				Map<Role, EndpointState> tmp1 = new HashMap<>(this.states);
				tmp1.put(r1, succ1);
				tmp1.put(r2, succ2);
				WFBuffers tmp2;
				if (((a1.isConnect() && a2.isAccept()) || (a1.isAccept() && a2.isConnect())))
						//&& this.buffs.canConnect(r1, r2))
				{
					tmp2 = this.buffs.connect(r1, r2);
				}
				else
				{
					throw new RuntimeException("Shouldn't get in here: " + a1 + ", " + a2);
				}
				res.add(new WFConfig(tmp1, tmp2));
			}
		}

		return res;
	}

	// Deadlock from non handleable messages (reception errors)
	public Map<Role, Receive> getStuckMessages()
	{
		Map<Role, Receive> res = new HashMap<>();
		for (Role r : this.states.keySet())
		{
			EndpointState s = this.states.get(r);
			Kind k = s.getStateKind();
			if (k == Kind.UNARY_INPUT || k == Kind.POLY_INPUT)
			{
				/*Set<IOAction> duals = this.buffs.get(r).entrySet().stream()
						.filter((e) -> e.getValue() != null)
						.map((e) -> e.getValue().toDual(e.getKey()))
						.collect(Collectors.toSet());
				if (duals.stream().anyMatch((a) -> s.isAcceptable(a)))
				{
					break;
				}*/
				Role peer = s.getAllTakeable().iterator().next().peer;
				Send send = this.buffs.get(r).get(peer);
				if (send != null)
				{
					Receive recv = send.toDual(peer);
					if (!s.isTakeable(recv))
					//res.put(r, new IOError(peer));
					res.put(r, recv);
				}
			}
		}
		return res;
	}
	
	
	// Doesn't include locally terminated (single term state does not induce a deadlock cycle) -- i.e. only "bad" deadlocks
	public Set<Set<Role>> getInputCycles()
	{
		Set<Set<Role>> res = new HashSet<>();
		List<Role> todo = new LinkedList<>(this.states.keySet());
		/*while (!todo.isEmpty())
		{
			Role r = todo.get(0);
			todo.remove(r);
			Set<Role> seen = new HashSet<>();
			while (true)
			{
				if (seen.contains(r))
				{
					res.add(seen);
					break;
				}
				seen.add(r);
				Role rr = isInputBlocked(r);
				if (rr == null)
				{
					break;
				}
				todo.remove(rr);
				if (this.states.get(rr).isTerminal())
				{
					seen.add(rr);
					res.add(seen);
					break;
				}
				r = rr;
			}
		}*/
		while (!todo.isEmpty())  // FIXME: maybe better to do directly on states, rather than via roles
		{
			Role r = todo.remove(0);
			//Set<Role> cycle = isCycle(new HashSet<>(), new HashSet<>(Arrays.asList(r)));
			if (!this.states.get(r).isTerminal())
			{
				Set<Role> cycle = isWaitForCycle(r);
				//if (!cycle.isEmpty())
				if (cycle != null)
				{
					todo.removeAll(cycle);
					res.add(cycle);
				}
			}
		}
		return res;
	}
	
	// Includes dependencies from input-blocking, termination and connect-blocking
	// FIXME: should also include connect?
	// NB: if this.states.get(orig).isTerminal() then orig is returned as "singleton deadlock"
	//public Set<Role> isCycle(Set<Role> candidate, Set<Role> todo)
	public Set<Role> isWaitForCycle(Role orig)
	{
		/*if (todo.isEmpty())
		{
			return candidate;
		}*/
		/*Set<Role> tmp = new HashSet<Role>(todo);
		Role r = tmp.iterator().next();
		tmp.remove(r);
		candidate.add(r);*/
		Set<Role> candidate = new HashSet<>();
		Set<Role> todo = new HashSet<>(Arrays.asList(orig));
		while (!todo.isEmpty())
		{
			Role r = todo.iterator().next();
			todo.remove(r);
			candidate.add(r);
			
			EndpointState s = this.states.get(r);
			if (s.getStateKind() == Kind.OUTPUT && !s.isConnectOnly())  // FIXME: includes connect, could still be deadlock? -- no: doesn't include connect any more
			{
				return null;
			}
			if (s.isTerminal())
			{
				if (todo.isEmpty())
				{
					return candidate;
				}
				continue;
			}
			Set<Role> blocked = isInputBlocked(r);
			//if (blocked.isEmpty())
			if (blocked == null)
			{
				return null;
			}
			if (todo.isEmpty() && candidate.containsAll(blocked))
			{
				return candidate;
			}
			blocked.forEach((x) ->
			{
				if (!candidate.contains(x))
				{
					//candidate.add(x);
					todo.add(x);
				}
			});
		}
		return null;
	}
	
	// Generalised to include connect-blocked roles
	//private Role isInputBlocked(Role r)
	private Set<Role> isInputBlocked(Role r)
	{
		EndpointState s = this.states.get(r);
		Kind k = s.getStateKind();
		if (k == Kind.UNARY_INPUT || k == Kind.POLY_INPUT)
		{
			List<IOAction> all = s.getAllTakeable();
			IOAction a = all.get(0);  // FIXME: assumes single choice subject (OK for current syntax, but should generalise)
			/*if (a.isAccept())  // Sound?
			{
				return null;
			}*/
			/*Role peer = a.peer;
			if (a.isReceive() && this.buffs.get(r).get(peer) == null)
			{
				//return peer;
			}*/
			if (a.isReceive())
			{
				Set<Role> peers = all.stream().map((x) -> x.peer).collect(Collectors.toSet());
				if (peers.stream().noneMatch((p) -> this.buffs.get(r).get(p) != null))
				{
					return peers;
				}
			}
		}
		/*else if (k == Kind.ACCEPT)
		{
			// FIXME TODO: if analysing ACCEPTs, check if s is initial (not "deadlock blocked" if initial) -- no: instead, analysing connects
			 
			List<IOAction> all = s.getAllTakeable();
			return all.stream().map((x) -> x.peer).collect(Collectors.toSet());
		}*/
		//else if (k == Kind.CONNECTION)
		else if (k == Kind.OUTPUT //|| k == Kind.ACCEPT  ..// FIXME: filter out connects if no available sends
				)
		{
			//List<IOAction> all = s.getAllAcceptable();
			if (s.isConnectOnly())
			{
				List<IOAction> all = s.getAllTakeable();
				return all.stream().map((x) -> x.peer).collect(Collectors.toSet());
			}
		}
		return null;
		//return Collections.emptySet();
	}

	// Generalised to include "unconnected" messages
	public Map<Role, Set<Send>> getOrphanMessages()
	{
		Map<Role, Set<Send>> res = new HashMap<>();
		for (Role r : this.states.keySet())
		{
			EndpointState s = this.states.get(r);
			if (s.isTerminal())
			{
				Set<Send> orphs = this.buffs.get(r).values().stream().filter((v) -> v != null).collect(Collectors.toSet());
				if (!orphs.isEmpty())
				{
					Set<Send> tmp = res.get(r);
					if (tmp == null)
					{
						tmp = new HashSet<>();
						res.put(r, tmp);
					}
					tmp.addAll(orphs);
				}
			}
			else
			{
				this.states.keySet().forEach((rr) ->
				{
					if (!rr.equals(r))
					{
						// Connection direction doesn't matter? -- wrong: matters because of async. disconnect
						if (!this.buffs.isConnected(r, rr))
						{
							Send send = this.buffs.get(r).get(rr);
							if (send != null)
							{
								Set<Send> tmp = res.get(r);
								if (tmp == null)
								{
									tmp = new HashSet<>();
									res.put(r, tmp);
								}
								tmp.add(send);
							}
						}
					}
				}); 
			}
		}
		return res;
	}

	public Map<Role, List<IOAction>> getTakeable()
	{
		Map<Role, List<IOAction>> res = new HashMap<>();
		for (Role r : this.states.keySet())
		{
			EndpointState s = this.states.get(r);
			switch (s.getStateKind())  // Choice subject enabling needed for non-mixed states (mixed states would be needed for async. permutations though)
			{
				case OUTPUT:
				{
					List<IOAction> as = s.getAllTakeable();
					for (IOAction a : as)
					{
						if (a.isSend())
						{
							if (this.buffs.canSend(r, (Send) a))
							{
								List<IOAction> tmp = res.get(r);  // FIXME: factor out
								if (tmp == null)
								{
									tmp = new LinkedList<>();
									res.put(r, tmp);
								}
								tmp.add(a);
							}
						}
						else if (a.isConnect())
						{
							// FIXME: factor out
							Connect c = (Connect) a;
							EndpointState speer = this.states.get(c.peer);
							//if (speer.getStateKind() == Kind.UNARY_INPUT)
							{
								List<IOAction> peeras = speer.getAllTakeable();
								for (IOAction peera : peeras)
								{
									if (peera.equals(c.toDual(r)) && this.buffs.canConnect(r, c))
									{
										List<IOAction> tmp = res.get(r);
										if (tmp == null)
										{
											tmp = new LinkedList<>();
											res.put(r, tmp);
										}
										tmp.add(a);
									}
								}
							}
						}
						else if (a.isDisconnect())
						{
							// Duplicated from Send
							if (this.buffs.canDisconnect(r, (Disconnect) a))
							{
								List<IOAction> tmp = res.get(r);  // FIXME: factor out
								if (tmp == null)
								{
									tmp = new LinkedList<>();
									res.put(r, tmp);
								}
								tmp.add(a);
							}
						}
						else
						{
							throw new RuntimeException("Shouldn't get in here: " + a);
						}
					}
					break;
				}
				case UNARY_INPUT:
				case POLY_INPUT:
				{
					for (IOAction a : this.buffs.inputable(r))
					{
						if (a.isReceive())
						{
							if (s.isTakeable(a))
							{
								List<IOAction> tmp = res.get(r);
								if (tmp == null)
								{
									tmp = new LinkedList<>();
									res.put(r, tmp);
								}
								tmp.add(a);
							}
						}
						/*else if (a.isAccept())
						{
							// FIXME: factor out
							Accept c = (Accept) a;
							EndpointState speer = this.states.get(c.peer);
							//if (speer.getStateKind() == Kind.OUTPUT)
							{
								List<IOAction> peeras = speer.getAllAcceptable();
								for (IOAction peera : peeras)
								{
									if (peera.equals(c.toDual(r)) && this.buffs.canAccept(r, c))
									{
										List<IOAction> tmp = res.get(r);
										if (tmp == null)
										{
											tmp = new LinkedList<>();
											res.put(r, tmp);
										}
										tmp.add(a);
										//break;  // Add all of them
									}
								}
							}
						}*/
						else
						{
							throw new RuntimeException("Shouldn't get in here: " + a);
						}
					}
					break;
				}
				case TERMINAL:
				{
					break;
				}
				/*case CONNECT:
				{
					List<IOAction> as = s.getAllTakeable();
					for (IOAction a : as)
					{
						if (a.isConnect())  ..// FIXME: could be send actions
						{
							// FIXME: factor out
							Connect c = (Connect) a;
							EndpointState speer = this.states.get(c.peer);
							//if (speer.getStateKind() == Kind.UNARY_INPUT)
							{
								List<IOAction> peeras = speer.getAllTakeable();
								for (IOAction peera : peeras)
								{
									if (peera.equals(c.toDual(r)) && this.buffs.canConnect(r, c))
									{
										List<IOAction> tmp = res.get(r);
										if (tmp == null)
										{
											tmp = new LinkedList<>();
											res.put(r, tmp);
										}
										tmp.add(a);
									}
								}
							}
						}
						else
						{
							throw new RuntimeException("Shouldn't get in here: " + s);
						}	
					}
					break;
				}*/
				case ACCEPT:
				{
					for (IOAction a : this.buffs.acceptable(r))
					{
						if (a.isAccept())
						{
							// FIXME: factor out
							Accept c = (Accept) a;
							EndpointState speer = this.states.get(c.peer);
							//if (speer.getStateKind() == Kind.OUTPUT)
							{
								List<IOAction> peeras = speer.getAllTakeable();
								for (IOAction peera : peeras)
								{
									if (peera.equals(c.toDual(r)) && this.buffs.canAccept(r, c))
									{
										List<IOAction> tmp = res.get(r);
										if (tmp == null)
										{
											tmp = new LinkedList<>();
											res.put(r, tmp);
										}
										tmp.add(a);
										//break;  // Add all of them
									}
								}
							}
						}
						else
						{
							throw new RuntimeException("Shouldn't get in here: " + a);
						}
					}
					break;
				}
				default:
				{
					throw new RuntimeException("Shouldn't get in here: " + s);
				}
			}
		}
		return res;
	}

	@Override
	public final int hashCode()
	{
		int hash = 71;
		hash = 31 * hash + this.states.hashCode();
		hash = 31 * hash + this.buffs.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof WFConfig))
		{
			return false;
		}
		WFConfig c = (WFConfig) o;
		return this.states.equals(c.states) && this.buffs.equals(c.buffs);
	}
	
	@Override
	public String toString()
	{
		return "(" + this.states + ", " + this.buffs + ")";
	}
}
