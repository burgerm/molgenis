package org.molgenis.charts.highcharts;

public class Credits
{
	boolean enabled = true;
	String href = "http://www.molgenis.org";
	String text = "Molgneis";
	
	/**
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	/**
	 * @return the href
	 */
	public String getHref()
	{
		return href;
	}
	/**
	 * @param href the href to set
	 */
	public void setHref(String href)
	{
		this.href = href;
	}
	/**
	 * @return the text
	 */
	public String getText()
	{
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text)
	{
		this.text = text;
	}
	
	
}
