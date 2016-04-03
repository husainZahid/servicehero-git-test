package com.sdl.dxa.modules.generic.utilclasses;

import java.util.Vector;

public abstract class HTMLObject
{

	// Vector used to hold other HTMLObjects
	protected Vector htmlObjects = null;
    protected String name = null;
    protected String id = null;

	// Static Alignment values
	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;
	public static final int TOP = 3;
	public static final int MIDDLE = 4;
	public static final int BOTTOM = 5;

	// Set the objects initial alignment to LEFT
	private int alignment = LEFT;

	// horizontal and vertical alignments
	private int horizontal = -1 ;
	private int vertical = MIDDLE;
	public String event = null;
	public String eventAction = null;
    public String style = null;
    public String cssClassName = null;
    protected String attributes = null;
    protected boolean isDisabled = false;


	// This abstract method forces all derived classes to
	// impliment the toHTML() method.  The toHTML() method
	// should return the HTML String necessary to
	// display this object in its current state.
	public abstract String toHTML();

	private String backgroundColor = new String("");

	// Constructor
	public HTMLObject()
	{
		// Default size of utilClassesV4.HTMLObject vector
		htmlObjects = new Vector(5);
	}

    public String getCssClassName() {
        return cssClassName;
    }

    public void setCssClassName(String cssClassName) {
        this.cssClassName = cssClassName;
    }

	// Add a utilClassesV4.HTMLObject
	public void addObject(HTMLObject value)
	{
		if (value != null)
			htmlObjects.addElement(value);
	}

	// Set the cell's horizontal alignment
	public void setHorizontalAlign(int value)
	{
		horizontal = value;
	}

	// Get the cell's horizontal alignment
	public int getHorizontalAlign()
	{
		return horizontal;
	}

	// Get the object's style
	public String getStyle()
	{
		return style;
	}

	// Set the object's style
	public void setStyle(String strValue)
	{
		this.style = strValue;
	}

    public void setName(String value)
    {
        if (value != null)
        {
            name = value;
            id = value;
        }
    }

    // Get the Input Name
    public String getName()
    {
        return name;
    }

    // Set the Input Id
    public void setId(String value)
    {
        if (value != null)
        {
            id = value;
            name = value;
        }
    }

    // Get the Input Id
    public String getId()
    {

        return id;
    }

	// Set the cell's vertical alignment
	public void setVerticalAlign(int value)
	{

		if (value >= TOP && value <= BOTTOM)
		{

			vertical = value;
		}
	}

	// Get the cell's vertical alignment
	public int getVerticalAlign()
	{

		return vertical;
	}

	// Remove a utilClassesV4.HTMLObject, if the element is removed
	// successfully, returns true
	public boolean removeObject(HTMLObject value)
	{

		if (value != null)
		{

			return htmlObjects.removeElement(value);
		}
		return false;
	}

	// Set the Script Event
	public void setEvent(String value)
	{

		if (value != null)
		{

			event = value;
		}
	}

	// Get the Script Event
	public String getEvent()
	{

		return event;
	}

	// Set the Script Action
	public void setEventAction(String value)
	{

		if (value != null)
		{

			eventAction = value;
		}
	}

	// Get the Script Action
	public String getEventAction()
	{

		return eventAction;
	}

	// Set the Objects Background Color
	public void setBackgroundColor(String value)
	{
		if (value != null)
			backgroundColor = value;
	}

	// Get the Objects Background Color
	public String getBackgroundColor()
	{
		return backgroundColor;
	}

	// Set the object's Alignment
	public void setAlignment(int value)
	{

		if (value >= LEFT && value <= RIGHT)
		{

			alignment = value;
		}
	}

	// Get the object's Alignment
	public int getAlignment()
	{
		return alignment;
	}

    // Set the additional attributes string
    // This will be used to specialize an input type
    public void setAttributes(String value)
    {
        if (value != null)
            attributes = value;
    }

    // Get the additional attributes string
    public String getAttributes()
    {
        return attributes;
    }

    // Set the disabled flag to true/false
    public void setDisabled(boolean bValue)
    {
        this.isDisabled = bValue;
    }

    
}