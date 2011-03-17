//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-146 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.01.30 at 04:43:27 PM GMT 
//


package org.scribble.protocol.monitor.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.scribble.protocol.monitor.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Description_QNAME = new QName("http://www.scribble.org/monitor", "description");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.scribble.protocol.monitor.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Description }
     * 
     */
    public Description createDescription() {
        return new Description();
    }

    /**
     * Create an instance of {@link MessageType }
     * 
     */
    public MessageType createMessageType() {
        return new MessageType();
    }

    /**
     * Create an instance of {@link ParallelNode }
     * 
     */
    public ParallelNode createParallelNode() {
        return new ParallelNode();
    }

    /**
     * Create an instance of {@link ReceiveMessage }
     * 
     */
    public ReceiveMessage createReceiveMessage() {
        return new ReceiveMessage();
    }

    /**
     * Create an instance of {@link ReceiveChoice }
     * 
     */
    public ReceiveChoice createReceiveChoice() {
        return new ReceiveChoice();
    }

    /**
     * Create an instance of {@link Annotation }
     * 
     */
    public Annotation createAnnotation() {
        return new Annotation();
    }

    /**
     * Create an instance of {@link Scope }
     * 
     */
    public Scope createScope() {
        return new Scope();
    }

    /**
     * Create an instance of {@link SendMessage }
     * 
     */
    public SendMessage createSendMessage() {
        return new SendMessage();
    }

    /**
     * Create an instance of {@link DecisionNode }
     * 
     */
    public DecisionNode createDecisionNode() {
        return new DecisionNode();
    }

    /**
     * Create an instance of {@link ReceiveDecision }
     * 
     */
    public ReceiveDecision createReceiveDecision() {
        return new ReceiveDecision();
    }

    /**
     * Create an instance of {@link SendChoice }
     * 
     */
    public SendChoice createSendChoice() {
        return new SendChoice();
    }

    /**
     * Create an instance of {@link ChoiceNode }
     * 
     */
    public ChoiceNode createChoiceNode() {
        return new ChoiceNode();
    }

    /**
     * Create an instance of {@link Path }
     * 
     */
    public Path createPath() {
        return new Path();
    }

    /**
     * Create an instance of {@link MessageNode }
     * 
     */
    public MessageNode createMessageNode() {
        return new MessageNode();
    }

    /**
     * Create an instance of {@link TryNode }
     * 
     */
    public TryNode createTryNode() {
        return new TryNode();
    }

    /**
     * Create an instance of {@link SendDecision }
     * 
     */
    public SendDecision createSendDecision() {
        return new SendDecision();
    }

    /**
     * Create an instance of {@link Call }
     * 
     */
    public Call createCall() {
        return new Call();
    }

    /**
     * Create an instance of {@link Node }
     * 
     */
    public Node createNode() {
        return new Node();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Description }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.scribble.org/monitor", name = "description")
    public JAXBElement<Description> createDescription(Description value) {
        return new JAXBElement<Description>(_Description_QNAME, Description.class, null, value);
    }

}