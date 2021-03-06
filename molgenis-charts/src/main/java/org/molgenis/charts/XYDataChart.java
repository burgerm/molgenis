package org.molgenis.charts;

import java.util.List;

import org.molgenis.charts.data.XYDataSerie;

/**
 * A chart that uses xy data points like a LineChart or BarChart.
 */
public class XYDataChart extends AbstractChart
{
	private String xAxisLabel;
	private String yAxisLabel;
	private MolgenisAxisType xAxisType;
	private MolgenisAxisType yAxisType;
	private final List<XYDataSerie> data;

	public XYDataChart(
			ChartType type,
			List<XYDataSerie> data,
			MolgenisAxisType xAxisType, 
			MolgenisAxisType yAxisType)
	{
		super(type);
		if (data == null) throw new IllegalArgumentException("data is null");
		if (xAxisType == null) throw new IllegalArgumentException("xAxisType is null");
		if (yAxisType == null) throw new IllegalArgumentException("yAxisType is null");
		this.data = data;
		this.xAxisType = xAxisType;
		this.yAxisType = yAxisType;
	}

	/**
	 * @return the xAxisLabel
	 */
	public String getxAxisLabel()
	{
		return xAxisLabel;
	}

	/**
	 * @param xAxisLabel the xAxisLabel to set
	 */
	public void setxAxisLabel(String xAxisLabel)
	{
		this.xAxisLabel = xAxisLabel;
	}

	/**
	 * @return the yAxisLabel
	 */
	public String getyAxisLabel()
	{
		return yAxisLabel;
	}

	/**
	 * @param yAxisLabel the yAxisLabel to set
	 */
	public void setyAxisLabel(String yAxisLabel)
	{
		this.yAxisLabel = yAxisLabel;
	}

	/**
	 * @return the xAxisType
	 */
	public MolgenisAxisType getxAxisType()
	{
		return xAxisType;
	}

	/**
	 * @param xAxisType the xAxisType to set
	 */
	public void setxAxisType(MolgenisAxisType xAxisType)
	{
		this.xAxisType = xAxisType;
	}

	/**
	 * @return the yAxisType
	 */
	public MolgenisAxisType getyAxisType()
	{
		return yAxisType;
	}

	/**
	 * @param yAxisType the yAxisType to set
	 */
	public void setyAxisType(MolgenisAxisType yAxisType)
	{
		this.yAxisType = yAxisType;
	}

	/**
	 * @return the data
	 */
	public List<XYDataSerie> getData()
	{
		return data;
	}
	
}
