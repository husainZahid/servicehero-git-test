package com.sdl.dxa.modules.generic.utilclasses;

public class HTMLCheckBoxInput extends HTMLInput
{

	// Default Constructor
	boolean checked = false;

	public HTMLCheckBoxInput()
	{
		setType("CHECKBOX");
	}

	// Set checked to true/false
	public void setChecked(boolean value)
	{
		checked = value;
	}

	// Check to see if RadioButton is CHECKED
	public boolean isChecked()
	{
		return checked;
	}

	// Return String containing the HTML formatted Radio Button
	public String toHTML()
	{
		if (checked)
			setAttributes(" CHECKED");
		return super.toHTML();
	}
}









