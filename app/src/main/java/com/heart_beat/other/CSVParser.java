package com.heart_beat.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser
{
	private File file;
	private boolean hasHeader;
	private String delimiter;

	public CSVParser(File file, boolean hasHeader, String delimiter)
	{
		this.file = file;
		this.hasHeader = hasHeader;
		this.delimiter = delimiter;
	}

	public Dataset read() throws IOException
	{
		BufferedReader fileReader = new BufferedReader(new FileReader(file));
		String[] header = null;

		String line;
		if (hasHeader)
		{
			line = fileReader.readLine();
			header = line.split(delimiter);
		}
		line = fileReader.readLine();

		List<double[]> data = new ArrayList<double[]>();
		while (line != null)
		{
			data.add(Utils.toDouble(line.split(delimiter)));
			line = fileReader.readLine();
		}

		fileReader.close();

		return new Dataset(header, data);
	}

	public void write(Dataset dataset) throws IOException
	{
		String[] header = dataset.getHeader();
		List<double[]> data = dataset.getData();

		FileWriter fileWriter = new FileWriter(file, false);
		for (int i = 0; i < header.length; i++)
		{
			fileWriter.write(header[i]);
			if (i == header.length - 1)
			{ fileWriter.write("\r\n"); }
			else
			{ fileWriter.write(delimiter); }
		}

		for (double[] record: data)
		{
			for (int j = 0; j < header.length; j++)
			{
				fileWriter.write(Double.toString(record[j]));
				if (j == header.length - 1)
				{ fileWriter.write("\r\n"); }
				else
				{ fileWriter.write(delimiter); }
			}
		}
		fileWriter.close();
	}

	public void append(double... record) throws IOException
	{
		append(new double[][]{record});
	}

	public void append(double[][] record) throws IOException
	{
		FileWriter fileWriter = new FileWriter(file, true);
		for (int i = 0; i < record.length; i++)
		{
			for (int j = 0; j < record[i].length; j++)
			{
				fileWriter.append(Double.toString(record[i][j]));
				if (j == record[i].length - 1)
				{ fileWriter.append("\r\n"); }
				else
				{ fileWriter.append(delimiter); }
			}
		}
		fileWriter.close();
	}

	public Dataset writeHeader(String... header) throws IOException
	{
		FileWriter fileWriter = new FileWriter(file, false);
		for (int i = 0; i < header.length; i++)
		{
			fileWriter.write(header[i]);

			if (i == header.length - 1)
			{ fileWriter.append("\r\n"); }
			else
			{ fileWriter.append(delimiter); }
		}
		fileWriter.close();
		return new Dataset(header, new ArrayList<double[]>());
	}

	public void delete()
	{
		file.delete();
	}
}
