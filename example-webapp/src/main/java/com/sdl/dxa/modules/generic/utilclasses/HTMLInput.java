package com.sdl.dxa.modules.generic.utilclasses;

public class HTMLInput extends HTMLObject
{

	protected int size = 0;
	protected String type = null;
	protected String input_value = null;

	// Default Constructor
	public HTMLInput()
	{
		setType("text");
	}

	// Set the Input Size
	public void setSize(int value)
	{
		if (value > 0)
			size = value;
	}

	// Get the Input Size
	public int getSize()
	{
		return size;
	}

	// Set the Input Type
	public void setType(String value)
	{
		if (value != null)
			type = value;
	}

	// Get the Input Type
	public String getType()
	{
		return type;
	}

	// Set the Input Name
	// Set the value of the Input Object
	public void setValue(String value)
	{
		if (value != null)
			input_value = value;
	}

	// Get the value of the Input Object
	public String getValue()
	{
		return input_value;
	}

	// Return String containing the HTML formatted Input
	public String toHTML()
	{
		StringBuffer html = new StringBuffer("<INPUT");
        if (name != null)
            html.append(" NAME=\"" + name + "\"");
        if (id != null)
            html.append(" ID=\"" + id + "\"");
		if (type != null)
            html.append(" TYPE=\"" + type + "\"");
		if (size > 0)
			html.append(" SIZE=\"" + size + "\"");
		// Add the VALUE
		if (input_value != null)
			html.append(" VALUE=\"" + input_value + "\"");
		// If there is any additional attributes
		if (attributes != null)
			html.append(" " + attributes);
		if (isDisabled)
			html.append(" DISABLED=true");
		// Ending Character
		if (event != null)
			html.append(" " + event + "=");
		if (eventAction != null)
			html.append("\"" + eventAction + "\"");
        if (style != null)
            html.append(" STYLE=\"" + style + "\"");
        if (cssClassName != null)
            html.append(" CLASS=\"" + cssClassName + "\"");
		html.append(">");
		return html.toString();
	}
}
