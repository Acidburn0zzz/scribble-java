/*
 * Copyright 2009-10 www.scribble.org
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
package org.scribble.protocol.parser;

import org.scribble.common.logging.Journal;
import org.scribble.protocol.ProtocolContext;
import org.scribble.protocol.model.ProtocolModel;

/**
 * This interface defines the protocol parser manager, responsible
 * for parsing a specified input stream against a parser appropriate
 * for the information being parsed.
 *
 */
public interface ProtocolParserManager {
	
	/**
	 * This method adds a protocol parser to the manager.
	 * 
	 * @param parser The protocol parser
	 */
	public void addParser(ProtocolParser parser);

	/**
	 * This method removes a protocol parser to the manager.
	 * 
	 * @param parser The protocol parser
	 */
	public void removeParser(ProtocolParser parser);

	/**
	 * This method parses the supplied input stream to create a protocol
	 * model. Any issues are reported to the supplied journal. The protocol
	 * context is optionally used by the parser to locate additional artifacts
	 * required to construct the protocol model.
	 * 
	 * @param sourceType The type of the content to be parsed
	 * @param is The input stream containing the information to be parsed
	 * @param journal The journal for reporting issues
	 * @param context The protocol context
	 * @return The protocol model
	 * @throws IOException Failed to retrieve content to be parsed
	 */
	public ProtocolModel parse(String sourceType, java.io.InputStream is, Journal journal,
				ProtocolContext context) throws java.io.IOException;
	
	/**
	 * This method returns the list of protocol parsers.
	 * 
	 * @return The list of protocol parsers
	 */
	public java.util.List<ProtocolParser> getParsers();
	
}
