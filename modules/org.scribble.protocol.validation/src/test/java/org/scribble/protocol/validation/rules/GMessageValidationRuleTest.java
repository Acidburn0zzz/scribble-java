/*
 * Copyright 2009-11 www.scribble.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.protocol.validation.rules;

import static org.junit.Assert.*;

import java.text.MessageFormat;

import org.scribble.protocol.model.FullyQualifiedName;
import org.scribble.protocol.model.Message;
import org.scribble.protocol.model.MessageSignature;
import org.scribble.protocol.model.Module;
import org.scribble.protocol.model.ParameterDecl;
import org.scribble.protocol.model.PayloadType;
import org.scribble.protocol.model.PayloadTypeDecl;
import org.scribble.protocol.model.global.GBlock;
import org.scribble.protocol.model.global.GMessage;
import org.scribble.protocol.model.global.GProtocolDefinition;
import org.scribble.protocol.validation.TestValidationLogger;
import org.scribble.protocol.validation.ValidationMessages;

public class GMessageValidationRuleTest {

    private static final String KNOWN_PAYLOAD_TYPE = "KnownPayloadType";

	@org.junit.Test
    public void testPayloadTypeFound() {
    	GMessageValidationRule rule=new GMessageValidationRule();
    	TestValidationLogger logger=new TestValidationLogger();
    	
    	Module module=new Module();
    	module.setFullyQualifiedName(new FullyQualifiedName("test"));
    	
    	PayloadTypeDecl pltd=new PayloadTypeDecl();
    	module.getTypeDeclarations().add(pltd);
    	
    	pltd.setAlias(KNOWN_PAYLOAD_TYPE);
    	
    	GProtocolDefinition gpd=new GProtocolDefinition();
    	module.getProtocols().add(gpd);
    	
    	GBlock block=new GBlock();
    	gpd.setBlock(block);
    	
    	GMessage gm=new GMessage();
    	block.add(gm);
    	
    	Message message=new Message();    	
    	gm.setMessage(message);
    	
    	MessageSignature ms=new MessageSignature();
    	message.setMessageSignature(ms);
    	
    	PayloadType plt=new PayloadType();
    	ms.getTypes().add(plt);
    	
    	plt.setName(KNOWN_PAYLOAD_TYPE);
    	
    	rule.validate(null, gm, logger);
    	
    	if (logger.isErrorsOrWarnings()) {
    		fail("Errors detected");
    	}
    }

	@org.junit.Test
    public void testProtocolParameterNameFound() {
    	GMessageValidationRule rule=new GMessageValidationRule();
    	TestValidationLogger logger=new TestValidationLogger();
    	
    	Module module=new Module();
    	module.setFullyQualifiedName(new FullyQualifiedName("test"));
    	
    	GProtocolDefinition gpd=new GProtocolDefinition();
    	module.getProtocols().add(gpd);
    	
    	ParameterDecl pd=new ParameterDecl();
    	gpd.getParameterDeclarations().add(pd);
    	
    	pd.setName(KNOWN_PAYLOAD_TYPE);
    	
    	GBlock block=new GBlock();
    	gpd.setBlock(block);
    	
    	GMessage gm=new GMessage();
    	block.add(gm);
    	
    	Message message=new Message();    	
    	gm.setMessage(message);
    	
    	MessageSignature ms=new MessageSignature();
    	message.setMessageSignature(ms);
    	
    	PayloadType plt=new PayloadType();
    	ms.getTypes().add(plt);
    	
    	plt.setName(KNOWN_PAYLOAD_TYPE);
    	
    	rule.validate(null, gm, logger);
    	
    	if (logger.isErrorsOrWarnings()) {
    		fail("Errors detected");
    	}
    }

	@org.junit.Test
    public void testProtocolParameterAliasFound() {
    	GMessageValidationRule rule=new GMessageValidationRule();
    	TestValidationLogger logger=new TestValidationLogger();
    	
    	Module module=new Module();
    	module.setFullyQualifiedName(new FullyQualifiedName("test"));
    	
    	GProtocolDefinition gpd=new GProtocolDefinition();
    	module.getProtocols().add(gpd);
    	
    	ParameterDecl pd=new ParameterDecl();
    	gpd.getParameterDeclarations().add(pd);
    	
    	pd.setName("A_Name");
    	pd.setAlias(KNOWN_PAYLOAD_TYPE);
    	
    	GBlock block=new GBlock();
    	gpd.setBlock(block);
    	
    	GMessage gm=new GMessage();
    	block.add(gm);
    	
    	Message message=new Message();    	
    	gm.setMessage(message);
    	
    	MessageSignature ms=new MessageSignature();
    	message.setMessageSignature(ms);
    	
    	PayloadType plt=new PayloadType();
    	ms.getTypes().add(plt);
    	
    	plt.setName(KNOWN_PAYLOAD_TYPE);
    	
    	rule.validate(null, gm, logger);
    	
    	if (logger.isErrorsOrWarnings()) {
    		fail("Errors detected");
    	}
    }

	@org.junit.Test
    public void testPayloadNameUnknown() {
    	GMessageValidationRule rule=new GMessageValidationRule();
    	TestValidationLogger logger=new TestValidationLogger();
    	
    	Module module=new Module();
    	module.setFullyQualifiedName(new FullyQualifiedName("test"));
    	
    	GProtocolDefinition gpd=new GProtocolDefinition();
    	module.getProtocols().add(gpd);
    	
    	GBlock block=new GBlock();
    	gpd.setBlock(block);
    	
    	GMessage gm=new GMessage();
    	block.add(gm);
    	
    	Message message=new Message();    	
    	gm.setMessage(message);
    	
    	MessageSignature ms=new MessageSignature();
    	message.setMessageSignature(ms);
    	
    	PayloadType plt=new PayloadType();
    	ms.getTypes().add(plt);
    	
    	plt.setName(KNOWN_PAYLOAD_TYPE);
    	
    	rule.validate(null, gm, logger);
    	
    	if (!logger.isErrorsOrWarnings()) {
    		fail("Errors not detected");
    	}
    	
    	if (!logger.getErrors().contains(MessageFormat.format(ValidationMessages.getMessage("UNKNOWN_PAYLOAD_ELEMENT"), KNOWN_PAYLOAD_TYPE))) {
    		fail("Error UNKNOWN_PAYLOAD_ELEMENT not detected");
    	}
    }
}