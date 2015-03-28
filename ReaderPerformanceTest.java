package rfid.performance;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.tags.Tag;

public class ReaderPerformanceTest {

	private AlienClass1Reader reader;
	
	public ReaderPerformanceTest() throws AlienReaderException {
	
	  reader = new AlienClass1Reader();
	  reader.setConnection("150.164.9.35", 23);
	  reader.setUsername("alien");
	  reader.setPassword("password");
	  reader.open();

	  HashMap<String, Double> tagSuccessRate = getIndividualSuccessRate(100);
	  HashMap<String, Double> tagReadRate = getIndividualReadRate(60);
	  
	  for (String tagID : tagSuccessRate.keySet()) {
		  System.out.println("ID:" + tagID +
					         ", Success Rate:" + tagSuccessRate.get(tagID) * 100 + "%" +
					         ", Read Rate:" + tagReadRate.get(tagID)
				  			 );
	  }
	  reader.close();
	}
	
	public HashMap<String, Double> getIndividualSuccessRate(int trials) throws AlienReaderException {
		reader.clearTagList();
		HashMap<String, Double> tagSuccessRate = new HashMap<String, Double>();
		Tag[] tagList = null;
		for (int i = 0; i < trials; i++)
			tagList = reader.getTagList();
		for (Tag tag : tagList)
			tagSuccessRate.put(tag.getTagID(), ((double) tag.getRenewCount()) / trials);
		return tagSuccessRate;
	}
	
	public HashMap<String, Double> getIndividualReadRate(long time) throws AlienReaderException {
		reader.clearTagList();
		HashMap<String, Double> tagReadRate = new HashMap<String, Double>();
		Tag[] tagList = null;
		long start = System.currentTimeMillis();
		long end = start + (time * 1000);
		while (System.currentTimeMillis() < end) // Problema: pode estar limitado ao tempo do loop?
			tagList = reader.getTagList();
		for (Tag tag : tagList)
			tagReadRate.put(tag.getTagID(), ((double) tag.getRenewCount()) / time);
		return tagReadRate;
	}
	
	public static final void main(String args[]){
	  try {
	    new ReaderPerformanceTest();
	  } catch(AlienReaderException e) {
	    System.out.println("Error: " + e.toString());
	  }
	}

}