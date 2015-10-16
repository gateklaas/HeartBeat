package com.heart_beat.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class IdMapper
{
	private File mappingFile;
	private HashMap<Integer, String> labelMap;
	private HashMap<String, Integer> idMap;
	private int id;

	public IdMapper(File mappingFile) throws IOException, ClassNotFoundException
	{
		this.mappingFile = mappingFile;
		labelMap = new HashMap<Integer, String>();
		idMap = new HashMap<String, Integer>();

		if (mappingFile.exists())
		{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(mappingFile));
			labelMap.putAll((HashMap<Integer, String>) in.readObject());
			idMap.putAll((HashMap<String, Integer>) in.readObject());
			in.close();

			id = labelMap.size();
		}
		else
		{
			id = 0;
		}
	}

	public boolean exists(String label)
	{
		return idMap.containsKey(label);
	}

	public int get(String label)
	{
		Integer id = idMap.get(label);
		if (id == null)
		{
			id = this.id++;
			labelMap.put(id, label);
			idMap.put(label, id);
			save();
		}
		return id;
	}

	public String get(int id)
	{
		return labelMap.get(id);
	}

	public int size() { return labelMap.size(); }

	private void save()
	{
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(mappingFile));
			out.writeObject(labelMap);
			out.writeObject(idMap);
			out.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
