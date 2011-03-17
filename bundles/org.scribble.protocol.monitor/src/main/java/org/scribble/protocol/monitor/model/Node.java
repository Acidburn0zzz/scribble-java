//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-146 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.01.30 at 04:43:27 PM GMT 
//


package org.scribble.protocol.monitor.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Node complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Node">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="annotation" type="{http://www.scribble.org/monitor}Annotation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nextIndex" type="{http://www.w3.org/2001/XMLSchema}int" default="-1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Node", propOrder = {
    "annotation"
})
@XmlSeeAlso({
    Call.class,
    MessageNode.class,
    DecisionNode.class,
    ChoiceNode.class,
    ParallelNode.class,
    Scope.class
})
public class Node {

    protected List<Annotation> annotation;
    @XmlAttribute(name = "nextIndex")
    protected Integer nextIndex;

    /**
     * Gets the value of the annotation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annotation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnotation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Annotation }
     * 
     * 
     */
    public List<Annotation> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<Annotation>();
        }
        return this.annotation;
    }

    /**
     * Gets the value of the nextIndex property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getNextIndex() {
        if (nextIndex == null) {
            return -1;
        } else {
            return nextIndex;
        }
    }

    /**
     * Sets the value of the nextIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNextIndex(Integer value) {
        this.nextIndex = value;
    }

}