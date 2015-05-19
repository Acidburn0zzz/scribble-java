package org.scribble2.parser.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble2.model.NonRoleArgument;
import org.scribble2.model.ArgNode;
import org.scribble2.model.ModelFactoryImpl;
import org.scribble2.parser.ScribbleParser;
import org.scribble2.parser.ast.name.AntlrAmbiguousName;
import org.scribble2.parser.ast.name.AntlrQualifiedName;
import org.scribble2.parser.util.Util;

public class AntlrArgumentInstantiation
{
	public static final int ARG_CHILD_INDEX = 0;

	public static NonRoleArgument parseArgumentInstantiation(ScribbleParser parser, CommonTree ct)
	{
		ArgNode arg = parseArgument(parser, getArgChild(ct));
		//return new ArgumentInstantiation(ct, arg);
		return ModelFactoryImpl.FACTORY.ArgumentInstantiation(arg);
	}

	// Similar to AntlrGlobalMessageTransfer.parseMessage
	protected static ArgNode parseArgument(ScribbleParser parser, CommonTree ct)
	{
		switch (Util.getAntlrNodeType(ct))
		{
			case MESSAGESIGNATURE:
			{
				return AntlrMessageSignature.parseMessageSignature(parser, ct);
			}
			case QUALIFIEDNAME:
			{
				return AntlrQualifiedName.toDataTypeNameNode(ct);
			}
			default: // As for PayloadElement: cannot syntactically distinguish between SimplePayloadTypeNode and ParameterNode
			{
				return AntlrAmbiguousName.toAmbiguousNameNode(ct);
			}
		}
	}

	public static CommonTree getArgChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(ARG_CHILD_INDEX);
	}
}
