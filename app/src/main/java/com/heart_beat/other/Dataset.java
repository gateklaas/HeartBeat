package com.heart_beat.other;

import java.util.List;

public class Dataset
{
	private String[] header;
	private List<double[]> data;

	public Dataset(String[] header, List<double[]> data)
	{
		this.header = header;
		this.data = data;
	}

	public double[] get(String columnName)
	{
		double[] array = new double[data.size()];
		for (int i = 0; i < header.length; i++)
		{
			if (columnName.equals(header[i]))
			{
				for (int j = 0; j < data.size(); j++) { array[j] = data.get(j)[i]; }
				break;
			}
		}
		return array;
	}

	public double[][] get(String... columnNames)
	{
		double[][] array = new double[data.size()][columnNames.length];
		for (int i = 0; i < columnNames.length; i++)
		{
			for (int j = 0; j < header.length; j++)
			{
				if (columnNames[i].equals(header[j]))
				{
					for (int k = 0; k < data.size(); k++) { array[k][i] = data.get(k)[j]; }
					break;
				}
			}
		}
		return array;
	}

	public void add(double[][] records)
	{
		for (int i = 0; i < records.length; i++) { data.add(records[i]); }
	}

	public int[] getInt(String columnName) { return Utils.toInt(get(columnName)); }

	public int[][] getInt(String... columnNames) { return Utils.toInt(get(columnNames)); }

	public void add(double... record) { add(new double[][]{record}); }

	public void remove(int rowIndex) { data.remove(rowIndex); }

	public String[] getHeader() { return header; }

	public List<double[]> getData() { return data; }

	public String toString()
	{
		String string = "{\n";

		string += "{";
		for (int i = 0; i < header.length; i++)
		{ string += header[i] + ","; }
		string += "}\n";

		for (int i = 0; i < data.size(); i++)
		{
			string += "{";
			for (int j = 0; j < header.length; j++)
			{ string += (int) data.get(i)[j] + ","; }
			string += "}\n";
		}

		return string + "}";
	}
}
