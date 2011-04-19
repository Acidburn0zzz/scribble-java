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
package org.scribble.common.resource;

import javax.xml.namespace.QName;

/**
 * This class represents file content to be used by the Scribble tools.
 *
 */
public class FileContent implements Content {

	private java.io.File m_file=null;
	
	/**
	 * This construct is initialized with the file.
	 * 
	 * @param file The file
	 */
	public FileContent(java.io.File file) {
		m_file = file;
	}
	
	/**
	 * This method returns the content name if available.
	 * 
	 * @return The optional content name
	 */
	public String getName() {
		return(m_file.getName());
	}
	
	/**
	 * This method returns the input stream for accessing the content.
	 * 
	 * @return The input stream
	 * @throws IOException Failed to get input stream
	 */
	public java.io.InputStream getInputStream() throws java.io.IOException {
		return(new java.io.FileInputStream(m_file));
	}
	
	/**
	 * This method determines whether the content has an extension
	 * of the specified type.
	 * 
	 * @param ext The extension
	 * @return Whether the content has the specified extension
	 */
	public boolean hasExtension(String ext) {
		int index=m_file.getName().lastIndexOf('.');
		String sourceType=null;
		
		if (index != -1) {
			sourceType = m_file.getName().substring(index+1);
		}

		return(sourceType.equals(ext));
	}
	
	/**
	 * This method determines whether the content is an XSD type of
	 * the value specified.
	 * 
	 * @param xsdType The XSD type name
	 * @return Whether the content is an XSD type of the specified name
	 */
	public boolean isXSDType(QName xsdType) {
		// TODO: Implement method
		throw new UnsupportedOperationException();
	}

	/**
	 * This method determines whether the content is an XSD element of
	 * the value specified.
	 * 
	 * @param xsdElem The XSD element name
	 * @return Whether the content is an XSD element of the specified name
	 */
	public boolean isXSDElement(QName xsdElem) {
		// TODO: Implement method
		throw new UnsupportedOperationException();
	}

}