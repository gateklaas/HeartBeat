package com.heart_beat.machine_learner;

import android.content.Context;
import android.util.Log;

import com.heart_beat.communication.FitbitCommunication;
import com.heart_beat.communication.OAuth2Helper;
import com.heart_beat.music.Music;
import com.heart_beat.music.MusicHelper;
import com.heart_beat.other.CSVParser;
import com.heart_beat.other.Constants;
import com.heart_beat.other.Dataset;
import com.heart_beat.other.IdMapper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import smile.classification.KNN;
import smile.math.kernel.LinearKernel;
import smile.regression.SVR;

public class MlHelper
{
	private static final String DELIMITER = ",";

	private CSVParser datasetParser;
	private CSVParser unprocessedDataParser;
	private Dataset dataset;
	private Dataset unprocessedData;

	private IdMapper artistMap;
	private IdMapper genreMap;
	private IdMapper musicMap;

	private SVR.Trainer<double[]> tempoTrainer;
	private SVR<double[]> tempoSVR;
	private KNN<double[]> artistKNN;
	private KNN<double[]> genreKNN;
	private KNN<double[]> musicKNN;

	public MlHelper(Context context, final double hrStart)
	{
		// create files
		File directory = context.getExternalFilesDir(null);
		File datasetFile = new File(directory, "/dataset.csv");
		File unprocessedDataFile = new File(directory, "/unprocessed_entries.csv");

		try
		{
			artistMap = new IdMapper(new File(directory, "artist_map"));
			genreMap = new IdMapper(new File(directory, "genre_map"));
			musicMap = new IdMapper(new File(directory, "music_map"));

			datasetParser = new CSVParser(datasetFile, true, DELIMITER);
			unprocessedDataParser = new CSVParser(unprocessedDataFile, true, DELIMITER);

			// create or open datasetParser
			if (!datasetFile.exists())
			{
				datasetFile.createNewFile();
				unprocessedDataFile.createNewFile();
				dataset = datasetParser.writeHeader("HRstart", "HRend", "HRtarget", "thumbs", "tempo", "artist", "genre", "music");
				unprocessedData = unprocessedDataParser.writeHeader("startTime", "endTime", "HRtarget", "thumbs", "tempo", "artist", "genre", "music");
			}
			else
			{
				dataset = datasetParser.read();
				unprocessedData = unprocessedDataParser.read();
			}
		} catch (IOException | ClassNotFoundException e) {throw new RuntimeException(e);}

		for (Music music : MusicHelper.MUSIC_LIST)
		{
			music.setOnLoadedListener(
					new Music.OnLoadedListener()
					{
						@Override
						public void onLoaded(Music music)
						{
							if (!musicMap.exists(music.PATH) && music.tempo != 0)
							{
								double hrEnd = music.tempo;
								double hrTarget = music.tempo;
								double thumbs = 0;
								double tempo = music.tempo;
								int artistId = artistMap.get(music.artist);
								int genreId = genreMap.get(music.genre);
								int musicId = musicMap.get(music.PATH);
								dataset.add(hrStart, hrEnd, hrTarget, thumbs, tempo, artistId, genreId, musicId);
							}
						}
					}
			);
		}

		MusicHelper.setOnLoadedListener(new MusicHelper.OnLoadedListener()
		{
			@Override
			public void onLoaded()
			{
				try
				{
					datasetParser.write(dataset);
				} catch (IOException e) {e.printStackTrace();}

				tempoTrainer = new SVR.Trainer<double[]>(new LinearKernel(), 0.1, 1.0);
				tempoSVR = tempoTrainer.train(dataset.get("HRstart", "HRend", "HRtarget"), dataset.get("tempo"));
				artistKNN = KNN.learn(dataset.get("thumbs", "tempo"), dataset.getInt("artist"), 2);
				genreKNN = KNN.learn(dataset.get("thumbs", "tempo"), dataset.getInt("genre"), 2);
				musicKNN = KNN.learn(dataset.get("tempo", "artist", "genre"), dataset.getInt("music"), 2);
			}
		});
	}

	public void learnLater(long startTime, long endTime, double hrTarget, double thumbs, Music music)
	{
		double tempo = music.tempo;
		int artistId = artistMap.get(music.artist);
		int genreId = genreMap.get(music.genre);
		int musicId = musicMap.get(music.PATH);

		try
		{
			unprocessedData.add(startTime, endTime, hrTarget, thumbs, tempo, artistId, genreId, musicId);
			unprocessedDataParser.append(startTime, endTime, hrTarget, thumbs, tempo, artistId, genreId, musicId);
		} catch (IOException e) {e.printStackTrace();}
	}

	public void learnNow(final OAuth2Helper oAuth2Helper)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Iterator<double[]> iterator = unprocessedData.getData().iterator();
				while (iterator.hasNext())
				{
					double[] record = iterator.next();
					long startTime = (long) record[0];
					long endTime = (long) record[1];
					double hrTarget = record[2];
					double thumbs = record[3];
					double tempo = record[4];
					int artistId = (int) record[5];
					int genreId = (int) record[6];
					int musicId = (int) record[7];
					try
					{
						int hrStart = FitbitCommunication.retrieveHR(oAuth2Helper, startTime);
						int hrEnd = FitbitCommunication.retrieveHR(oAuth2Helper, endTime);
						iterator.remove();

						Log.i(Constants.TAG, "hrStart: " + hrStart + " hrEnd: " + hrEnd);
						if (hrStart == 0 || hrEnd == 0)
						{ break; }

						learn(hrStart, hrEnd, hrTarget, thumbs, tempo, artistId, genreId, musicId);
					} catch (Exception e) {e.printStackTrace();}
				}
				try
				{
					unprocessedDataParser.write(unprocessedData);
				} catch (Exception e) {e.printStackTrace();}
			}
		}).start();
	}

	public void learn(double hrStart, double hrEnd, double hrTarget, double thumbs, Music music)
	{
		double tempo = music.tempo;
		int artistId = artistMap.get(music.artist);
		int genreId = genreMap.get(music.genre);
		int musicId = musicMap.get(music.PATH);
		learn(hrStart, hrEnd, hrTarget, thumbs, tempo, artistId, genreId, musicId);
	}

	private void learn(double hrStart, double hrEnd, double hrTarget, double thumbs, double tempo, int artistId, int genreId, int musicId)
	{
		Music.loader.shutdown();
		while (!Music.loader.isTerminated()) {Thread.yield();}

		try
		{
			dataset.add(hrStart, hrEnd, hrTarget, thumbs, tempo, artistId, genreId, musicId);
			datasetParser.append(hrStart, hrEnd, hrTarget, thumbs, tempo, artistId, genreId, musicId);
		} catch (IOException e) {e.printStackTrace();}

		tempoSVR = tempoTrainer.train(dataset.get("HRstart", "HRend", "HRtarget"), dataset.get("tempo"));
		artistKNN = KNN.learn(dataset.get("thumbs", "tempo"), dataset.getInt("artist"), 2);
		genreKNN = KNN.learn(dataset.get("thumbs", "tempo"), dataset.getInt("genre"), 2);
		musicKNN = KNN.learn(dataset.get("tempo", "artist", "genre"), dataset.getInt("music"), 2);
	}

	public Music predict(double hrStart, double hrTarget)
	{
		Music.loader.shutdown();
		while (!Music.loader.isTerminated()) {Thread.yield();}

		double hrEnd = hrTarget;
		double thumbs = 1;
		double tempo = tempoSVR.predict(new double[]{hrStart, hrEnd, hrTarget});
		int artistId = artistKNN.predict(new double[]{thumbs, tempo});
		int genreId = genreKNN.predict(new double[]{thumbs, tempo});
		int musicId = musicKNN.predict(new double[]{tempo, artistId, genreId});

		Log.i(Constants.TAG, "HRstart: " + hrStart + " HRend: " + hrEnd + " HRtarget: " + hrTarget + " thumbs: " + thumbs + " tempo: " + (int) tempo + " artist: " + artistId + " genre: " + genreId + " song: " + musicId);
		return MusicHelper.find(musicMap.get(musicId));
	}
}
