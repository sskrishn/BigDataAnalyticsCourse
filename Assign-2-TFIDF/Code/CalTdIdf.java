import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class CalTdIdf {

	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
		Comparator<K> valueComparator = new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = map.get(k2).compareTo(map.get(k1));
				if (compare == 0)
					return 1;
				else
					return compare;
			}
		};

		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}

	public static void sortbykey(Map<String, TreeMap<String, Double>> map) {
		TreeMap<String, TreeMap<String, Double>> sorted = new TreeMap<>();
		sorted.putAll(map);
		for (Map.Entry<String, TreeMap<String, Double>> entry : sorted.entrySet())
			for (Map.Entry<String, Double> child : entry.getValue().entrySet())
				System.out.println(entry.getKey() + " " + child.getKey() + " " + child.getValue());
	}

	public static void getTopFifteenSorted(Map<String, Map<String, Double>> map) {
		TreeMap<String, Map<String, Double>> sorted = new TreeMap<>();
		sorted.putAll(map);
		for (Map.Entry<String, Map<String, Double>> entry : sorted.entrySet()) {
			int counter = 0;
			System.out.println("The top Fifteen words with Highest TF*IDF value in document: " + entry.getKey());
			for (Map.Entry<String, Double> child : entry.getValue().entrySet()) {
				counter = counter + 1;
				if (counter <= 15) {
					System.out.println(child.getKey() + "  " + child.getValue());
				}
			}
		}
	}

	public static void interChangeDocNameTerm(Map<String, TreeMap<String, Double>> map) {
		Map<String, TreeMap<String, Double>> fb = new HashMap<>();
		for (Map.Entry<String, TreeMap<String, Double>> entry : map.entrySet()) {
			for (Map.Entry<String, Double> child : entry.getValue().entrySet()) {

				TreeMap<String, Double> temp;
				if (fb.containsKey(child.getKey())) {
					temp = fb.get(child.getKey());
					temp.put(entry.getKey(), child.getValue());
				} else {
					temp = new TreeMap<>();
					temp.put(entry.getKey(), child.getValue());
					fb.put(child.getKey(), temp);
				}
			}
		}

		Map<String, Map<String, Double>> fbb = new HashMap<>();
		for (Map.Entry<String, TreeMap<String, Double>> entry : fb.entrySet()) {
			Map<String, Double> child = sortByValues(entry.getValue());
			fbb.put(entry.getKey(), child);
		}
		getTopFifteenSorted(fbb);
	}

	public static void main(String[] args) {
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		BufferedReader br3 = null;

		Map<String, TreeMap<String, Double>> termtfIdf = new HashMap<>();
		Map<String, Integer> docNameTotalTerms = new HashMap<>();
		Map<String, Double> termIdf = new HashMap<>();
		try {
			br1 = new BufferedReader(new FileReader("./Phase1Ques2OP/part-r-00000"));
			br2 = new BufferedReader(new FileReader("./Phase2Ques2OP/part-r-00000"));
			br3 = new BufferedReader(new FileReader("./Phase3Ques2OP/part-r-00000"));
			String line1;
			String line2;
			String line3;

			StringTokenizer word_list;
			int totalDocs = 0;

			while ((line2 = br2.readLine()) != null) {
				word_list = new StringTokenizer(line2);
				String docName = word_list.nextToken();
				int totalTermInDoc = Integer.parseInt(word_list.nextToken());
				docNameTotalTerms.put(docName, totalTermInDoc);
				totalDocs += 1;
			}

			while ((line3 = br3.readLine()) != null) {
				word_list = new StringTokenizer(line3);
				String term = word_list.nextToken();
				int docWithTerm = Integer.parseInt(word_list.nextToken());
				double tm = (double) totalDocs / docWithTerm;
				double idf = Math.log(tm);
				termIdf.put(term, idf);
			}

			while ((line1 = br1.readLine()) != null) {
				word_list = new StringTokenizer(line1);
				String keyPhase1 = word_list.nextToken();
				String[] termDocName = keyPhase1.split(",");
				String term = termDocName[0];
				String docName = termDocName[1];
				int tFrequency = Integer.parseInt(word_list.nextToken());

				TreeMap<String, Double> temp;
				if (termtfIdf.containsKey(term)) {
					temp = termtfIdf.get(term);
					int totalTerms = docNameTotalTerms.get(docName);
					double tf = (double) tFrequency / totalTerms;
					double tfIdf = (double) tf * termIdf.get(term);
					temp.put(docName, tfIdf);
				} else {
					temp = new TreeMap<>();
					int totalTerms = docNameTotalTerms.get(docName);
					double tf = (double) tFrequency / totalTerms;
					double tfIdf = (double) tf * termIdf.get(term);
					temp.put(docName, tfIdf);
					termtfIdf.put(term, temp);
				}

			}

			System.out.println("All words TF*IDF values sorted by terms of all documents");
			sortbykey(termtfIdf);
			System.out.println("Top Fifteen words TF*IDF values sorted by tf*idf values in each document");

			interChangeDocNameTerm(termtfIdf);
			br1.close();
			br2.close();
			br3.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
