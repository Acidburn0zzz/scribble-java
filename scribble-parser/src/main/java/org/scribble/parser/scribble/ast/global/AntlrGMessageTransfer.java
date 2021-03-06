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
package org.scribble.parser.scribble.ast.global;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.MessageNode;
import org.scribble.ast.MessageSigNode;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.parser.scribble.AntlrToScribParser;
import org.scribble.parser.scribble.AntlrToScribParserUtil;
import org.scribble.parser.scribble.ScribbleAntlrConstants.AntlrNodeType;
import org.scribble.parser.scribble.ast.name.AntlrAmbigName;
import org.scribble.parser.scribble.ast.name.AntlrQualifiedName;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;
import org.scribble.util.ScribParserException;

public class AntlrGMessageTransfer
{
	public static final int MESSAGE_CHILD_INDEX = 0;
	public static final int SOURCE_CHILD_INDEX = 1;
	public static final int DESTINATION_CHILDREN_START_INDEX = 2;

	public static GMessageTransfer parseGMessageTransfer(AntlrToScribParser parser, CommonTree ct, AstFactory af) throws ScribParserException
	{
		RoleNode src = AntlrSimpleName.toRoleNode(getSourceChild(ct), af);
		MessageNode msg = parseMessage(parser, getMessageChild(ct), af);
		List<RoleNode> dests = 
			getDestChildren(ct).stream().map(d -> AntlrSimpleName.toRoleNode(d, af)).collect(Collectors.toList());
		return af.GMessageTransfer(ct, src, msg, dests);
	}

	public static MessageNode parseMessage(AntlrToScribParser parser, CommonTree ct, AstFactory af) throws ScribParserException
	{
		AntlrNodeType type = AntlrToScribParserUtil.getAntlrNodeType(ct);
		if (type == AntlrNodeType.MESSAGESIGNATURE)
		{
			return (MessageSigNode) parser.parse(ct, af);
		}
		else //if (type.equals(AntlrConstants.AMBIGUOUSNAME_NODE_TYPE))
		{
			return (ct.getChildCount() == 1)
				? AntlrAmbigName.toAmbigNameNode(ct, af)  // parametername or simple messagesignaturename
				: AntlrQualifiedName.toMessageSigNameNode(ct, af);
		}
	}

	public static CommonTree getMessageChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(MESSAGE_CHILD_INDEX);
	}

	public static CommonTree getSourceChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(SOURCE_CHILD_INDEX);
	}

	public static List<CommonTree> getDestChildren(CommonTree ct)
	{
		return AntlrToScribParserUtil.toCommonTreeList(ct.getChildren().subList(DESTINATION_CHILDREN_START_INDEX, ct.getChildCount()));
	}
}
