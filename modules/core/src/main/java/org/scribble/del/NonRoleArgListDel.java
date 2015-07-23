package org.scribble.del;

import java.util.Iterator;

import org.scribble.ast.Do;
import org.scribble.ast.NonRoleArg;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.NonRoleParamDecl;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.ScribNode;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.DataTypeKind;
import org.scribble.sesstype.kind.NonRoleParamKind;
import org.scribble.sesstype.kind.SigKind;
import org.scribble.visit.NameDisambiguator;

public class NonRoleArgListDel extends DoArgListDel
{
	public NonRoleArgListDel()
	{

	}

	// Doing in leave allows the arguments to be individually checked first
	@Override
	public NonRoleArgList leaveDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb, ScribNode visited) throws ScribbleException
	{
		NonRoleArgList nral = (NonRoleArgList) super.leaveDisambiguation(parent, child, disamb, visited);
				// Checks matching arity

		ProtocolDecl<?> pd = getTargetProtocolDecl((Do<?>) parent, disamb);
		Iterator<NonRoleArg> args = nral.getDoArgs().iterator();
		for (NonRoleParamDecl<?> param : pd.header.paramdecls.getDecls())
		{
			NonRoleParamKind kind = param.kind;
			NonRoleArg arg = args.next();
			if (kind.equals(SigKind.KIND))
			{
				if (!arg.val.isMessageSigNameNode() && !arg.val.isMessageSigNameNode())
				{
					throw new RuntimeException("Bad arg " + arg + " for param kind: " + kind);
				}
			}
			else if (kind.equals(DataTypeKind.KIND))
			{
				if (!arg.val.isDataTypeNameNode())
				{
					throw new RuntimeException("Bad arg " + arg + " for param kind: " + kind);
				}
			}
			else
			{
				throw new RuntimeException("Shouldn't get in here: " + kind);
			}
		}

		return nral;
	}

	@Override
	protected NonRoleParamDeclList getParamDeclList(ProtocolDecl<?> pd)
	{
		return pd.header.paramdecls;
	}
}
