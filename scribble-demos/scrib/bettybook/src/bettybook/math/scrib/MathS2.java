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
package bettybook.math.scrib;

import static bettybook.math.scrib.Math.MathService.MathService.C;
import static bettybook.math.scrib.Math.MathService.MathService.Prod;
import static bettybook.math.scrib.Math.MathService.MathService.S;
import static bettybook.math.scrib.Math.MathService.MathService.Sum;

import java.io.IOException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.runtime.net.Buf;
import org.scribble.runtime.net.ObjectStreamFormatter;
import org.scribble.runtime.net.scribsock.ScribServerSocket;
import org.scribble.runtime.net.scribsock.SocketChannelServer;
import org.scribble.runtime.net.session.MPSTEndpoint;

import bettybook.math.scrib.Math.MathService.MathService;
import bettybook.math.scrib.Math.MathService.channels.S.EndSocket;
import bettybook.math.scrib.Math.MathService.channels.S.MathService_S_1;
import bettybook.math.scrib.Math.MathService.channels.S.MathService_S_1_Handler;
import bettybook.math.scrib.Math.MathService.channels.S.MathService_S_2;
import bettybook.math.scrib.Math.MathService.channels.S.MathService_S_2_Handler;
import bettybook.math.scrib.Math.MathService.channels.S.MathService_S_3;
import bettybook.math.scrib.Math.MathService.channels.S.MathService_S_4;
import bettybook.math.scrib.Math.MathService.ops.Add;
import bettybook.math.scrib.Math.MathService.ops.Bye;
import bettybook.math.scrib.Math.MathService.ops.Mult;
import bettybook.math.scrib.Math.MathService.ops.Val;
import bettybook.math.scrib.Math.MathService.roles.S;

public class MathS2
{
	public static void main(String[] args) throws Exception
	{
		try (ScribServerSocket ss = new SocketChannelServer(8888))
		{
			while (true)
			{
				MathService sess = new MathService();
				try (MPSTEndpoint<MathService, S> se = new MPSTEndpoint<>(sess, S, new ObjectStreamFormatter()))
				{
					se.accept(ss, C);

					MathService_S_1 s1 = new MathService_S_1(se);
					s1.branch(C, new MathSHandler());
				}
			}
		}
	}
}

// FIXME: add generic session-state object to handler methods
class MathSHandler implements MathService_S_1_Handler, MathService_S_2_Handler
{
	private Buf<Integer> b1;

	@Override
	public void receive(MathService_S_2 s2, Val op, Buf<Integer> b1)
			throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		this.b1 = b1;
		s2.branch(C, this);
	}

	@Override
	public void receive(EndSocket end, Bye op) throws ScribbleRuntimeException,
			IOException, ClassNotFoundException
	{
		
	}

	@Override
	public void receive(MathService_S_3 s3, Add op, Buf<Integer> b2)
			throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		s3.send(C, Sum, this.b1.val + b2.val).branch(C, this);
	}

	@Override
	public void receive(MathService_S_4 s4, Mult op, Buf<Integer> b2)
			throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		s4.send(C, Prod, this.b1.val * b2.val).branch(C, this);
	}
}
