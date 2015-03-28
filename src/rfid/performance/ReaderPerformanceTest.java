package rfid.performance;

import java.util.HashMap;
import java.util.stream.DoubleStream;

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
		System.out.println("Log: Conectado ao Leitor");
	}

	public String performanceToString(HashMap<String, Double[]> properties, String propertyName, boolean percentage) {
		String result = "";
		int repetitions = properties.values().iterator().next().length;
		result += performanceToStringIndividualRepeated(properties, propertyName, percentage, repetitions);		
		result += performanceToStringAverage(properties, propertyName, percentage, repetitions);
		result += "----------------------------\n\n\n";
		return result;
	}
	
	private String performanceToStringIndividualRepeated(HashMap<String, Double[]> properties, String propertyName, boolean percentage, int repetitions) {
		String result = "AVALIAÇÃO DE " + propertyName.toUpperCase() + "\n\n";
		for (int i = 0; i < repetitions; i++) {
			result += "--- Experimento #" + (i + 1);
			result += "\n\n";
			result += "Etiqueta\t\t\t" + propertyName + "\n";
			for (String tagID : properties.keySet()) {
				result += "" + tagID + "\t";
				if (percentage)
					result += properties.get(tagID)[i] * 100 + "%\n";
				else
					result += properties.get(tagID)[i] + "\n";
			}
			result += "\n";
		}
		return result;
	}
	
	private String performanceToStringAverage(HashMap<String, Double[]> properties, String propertyName, boolean percentage, int repetitions) {
		String result = "";
		HashMap<String, Double> tagAverage = new HashMap<String, Double>();
		double allAvg = 0.0;
		for (String tagID : properties.keySet()) {
			double sum = 0.0;
			for (Double measure : properties.get(tagID)) sum += measure;
			double average = (double) sum / repetitions;
			tagAverage.put(tagID, average);
			allAvg += average;
		}
		allAvg = allAvg / tagAverage.size();
		if (repetitions > 1) {
			result += "--- Média por Etiqueta";
			result += "\n\n";
			result += "Etiqueta\t\t\t" + propertyName + "\n";
			for (String tagID : tagAverage.keySet()) {
				result += "" + tagID + "\t";
				if (percentage)
					result += tagAverage.get(tagID) * 100 + "%\n";
				else
					result += tagAverage.get(tagID) + "\n";
			}
			result += "\n";
		}
		if (percentage)
			result += propertyName + " Média = " + String.format("%.2f", allAvg * 100) + "%\n";
		else
			result += propertyName + " Média = " + String.format("%.2f", allAvg) + "\n";
		return result;
	}

	public HashMap<String, Double[]> getIndividualSuccessRate(int trials, int repetitions) throws AlienReaderException {
		HashMap<String, Double[]> tagSuccessRate = new HashMap<String, Double[]>();
		for (int i = 0; i < repetitions; i++) {
			reader.clearTagList();
			Tag[] tagList = null;
			for (int j = 0; j < trials; j++) {
				tagList = reader.getTagList();
			}
			for (Tag tag : tagList) {
				if (!tagSuccessRate.containsKey(tag.getTagID()))
					tagSuccessRate.put(tag.getTagID(), new Double[repetitions]);
				tagSuccessRate.get(tag.getTagID())[i] = Double.valueOf((double) tag.getRenewCount() / trials);
				// se alguma repetição tiver zero leituras, o valor será zero, que é igual à taxa de sucesso
			}
		}
		return tagSuccessRate;
	}

	public HashMap<String, Double[]> getIndividualReadRate(long time, int repetitions) throws AlienReaderException {
		HashMap<String, Double[]> tagReadRate = new HashMap<String, Double[]>();
		for (int i = 0; i < repetitions; i++) {
			reader.clearTagList();
			Tag[] tagList = null;
			long start = System.currentTimeMillis();
			long end = start + (time * 1000);
			while (System.currentTimeMillis() < end)
				tagList = reader.getTagList();
			for (Tag tag : tagList) {
				if (!tagReadRate.containsKey(tag.getTagID()))
					tagReadRate.put(tag.getTagID(), new Double[repetitions]);
				tagReadRate.get(tag.getTagID())[i] = Double.valueOf((double) tag.getRenewCount() / time);
			}
		}
		return tagReadRate;
	}

	public static final void main(String args[]){
		try {
			ReaderPerformanceTest perf = new ReaderPerformanceTest();

			HashMap<String, Double[]> tagSuccessRate = perf.getIndividualSuccessRate(10, 1);
			System.out.println(perf.performanceToString(tagSuccessRate, "Taxa de Sucesso", true));
			
			HashMap<String, Double[]> tagReadRate = perf.getIndividualReadRate(10, 1);
			System.out.println(perf.performanceToString(tagReadRate, "Taxa de Leitura", false));

			perf.reader.close();

		} catch(AlienReaderException e) {
			System.out.println("Error: " + e.toString());
		}
	}

}
