package gedi.solutions.geode.commas.data.catalog;

import java.io.Serializable;

/**
 * 
 * 
 * 
 * <pre>
 * 
 * 
 *  FunctionAttribute is a value object representation of a entity FunctionAttribute with
 *  name and a value
 * 
 * 
 * 
 * 
 * </pre>
 * 
 * @author Gregory Green
 * 
 * @version 1.0
 * 
 */

public class FunctionAttribute
implements Serializable
{
	public FunctionAttribute()
	{

		name = null;

		value = null;

	}// --------------------------------------------

	/**
	 * 
	 * Constructor for FunctionAttribute initializes internal data settings.
	 * 
	 * @param aName
	 *            the property name
	 * @param aValue
	 *            the property value
	 */
	public FunctionAttribute(String aName, Serializable aValue)
	{
		this.setName(aName);
		this.setValue(aValue);

	}// --------------------------------------------
	public int compareTo(Object aOther)
	{
		FunctionAttribute other = (FunctionAttribute) aOther;

		// compare names
		return other.getName().compareTo(this.getName());
	}// --------------------------------------------

	/**
	 * 
	 * @return the property name
	 */
	public String getName()
	{
		return name;

	}// --------------------------------------------

	/**
	 * Set property name
	 * 
	 * @param name
	 *            name to set
	 */
	public void setName(String name)
	{

		if (name == null)
		{
			name = "";
		}

		this.name = name.trim();

	}// --------------------------------------------

	/**
	 * 
	 * @return the value of the property
	 */
	public Object getValue()
	{
		return value;
	}// --------------------------------------------

	/**
	 * 
	 * @param value
	 *            the property value to set
	 */
	public void setValue(Serializable value)
	{
		this.value = value;
	}// --------------------------------------------

	/**
	 * 
	 * @return name of the property
	 * 
	 */
	public Object getKey()
	{

		return name;
	}// --------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionAttribute other = (FunctionAttribute) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (value == null)
		{
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer text = new StringBuffer("[")
		.append(getClass().getName()).append("]")
		.append(" name: ").append(name)
		.append(" value: ").append(value);

		return text.toString();
	}// ----------------------------------------

	/**
	 * 
	 * @param aValue
	 *            the property value
	 * @return true if string version of the property value equals (ignore case)
	 *         aValue
	 */
	public boolean equalsValueIgnoreCase(Object aValue)
	{
		return String.valueOf(value).equalsIgnoreCase(
		String.valueOf(aValue));
	}// --------------------------------------------

	/**
	 * Set name to key
	 * 
	 * @param key the key to set
	 */
	public void setKey(Object key)
	{
		if (key == null)
			throw new IllegalArgumentException("key required in FunctionAttribute.setKey");

		this.name = key.toString();
	}// --------------------------------------------

	/**
	 * 
	 * @param text
	 *            the text value
	 */
	public void setTextValue(String text)
	{
		this.setValue(text);
	}// --------------------------------------------

	/**
	 * 
	 * @return (String)getValue()
	 */
	public String getTextValue()
	{
		return (String) getValue();
	}// --------------------------------------------

	private String name = "";
	private Serializable value = "";
	static final long serialVersionUID = FunctionAttribute.class.getName().hashCode();

}
