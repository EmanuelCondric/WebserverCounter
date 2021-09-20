import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class WebserverLogClass {

	public static void main(String[] args) throws IOException{
		String rangList = "";
		String uniqueList = "";
		File logInput = new File("C:\\Users\\emanu\\OneDrive\\Desktop\\Posao\\Pinko\\webserver.log");
		rangList = getRankListOfSites(logInput);
		uniqueList = getUniqueListOfSites(logInput);
		System.out.println("Stranice po posjecenosti: \r\n" + rangList);
		System.out.println("Broj unikatnih url-ova prema domenama: \r\n" + uniqueList);
	}
	
	
	public static String getRankListOfSites(File logInput) {
		String rangList = "";
		List<String> distinctWebpages = new ArrayList<String>();
		List<String> allWebpages = new ArrayList<String>();
		Hashtable<String, Integer> finalResultDict = new Hashtable<String, Integer>();
// Rade se dvije Liste. Unutar jedne nalaze se sve webstranice (bez ekstenzije sa 'IP-om') koje su zapisane u Logu (svaki log ima svoj zapis u listi što znači da ukoliko postoji 500 zapisa u logu, lista će sadržavati 500 elemenata), dok se unutar 
// druge liste nalaze distinct webstranice (bez ekstenzije sa 'IP-om'). 
		try {
			Scanner logScanner = new Scanner(logInput);
			while(logScanner.hasNextLine()){
				String logItem = logScanner.nextLine();
				String webpage = logItem.substring(0, logItem.indexOf(" "));
				allWebpages.add(webpage);
				if(distinctWebpages.size() <= 0){
					distinctWebpages.add(webpage);
				}else{
					if(distinctWebpages.contains(webpage)){
						continue;
					}else{
						distinctWebpages.add(webpage);
					}
				}
			}
			
			logScanner.close();
			
// Iterira se kroz listu distinct vrijednosti te se za svaki element liste traži koliko se puta ponavlja u listi svih vrijednosti.
// Na taj način će se dobiti točan broj posjeta određene webstranice.
			
			for (int i = 0; i < distinctWebpages.size(); i++){
				Integer nbrOfSamples = Collections.frequency(allWebpages, distinctWebpages.get(i));
				finalResultDict.put(distinctWebpages.get(i), nbrOfSamples);
			}
			
//Uzimaju se vrijednosti iz Hashtable-a i zapisuju se u Array
			List<Map.Entry<String, Integer>> listbyHashtable = new ArrayList<Entry<String, Integer>>(finalResultDict.entrySet());
			
// Vrši se sortiranje uz pomoć komparatora(Desc- što znači od najveće vrijednosti prema najmanjoj)
			Collections.sort(listbyHashtable, new Comparator<Map.Entry<String, Integer>>(){
			 
			  public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2){
			      return entry2.getValue().compareTo( entry1.getValue());
			  }	  
			});
// 'Slaže' se završni string kojeg funkcija vraća - distinct lista webstranica bez IP-ova sa pripadajućim brojem posjeta
			
			for(Map.Entry<String, Integer> entry : listbyHashtable) {
				rangList += entry.getKey() + " " + entry.getValue() + " visits" + "\r\n";
			}
		}catch (FileNotFoundException e){
		}
		return rangList;
	}
	
	public static String getUniqueListOfSites(File logInput){
		String uniqueList = "";
		List<String> distinctWebpages = new ArrayList<String>();
		List<String> allUrls = new ArrayList<String>();
		List<String> distinctUrls = new ArrayList<String>();
		Hashtable<String, Integer> finalResultDict = new Hashtable<String, Integer>();
// Rade se tri Liste. Unutar liste distinctWebpages pohranjuju se distinct webstranice bez ekstenzije sa 'IP-om'. Unutar liste allUrls pohranjuju se sve web stranice koje se nalaze u logu zajedno sa IP ekstenzijom (podatkom koji je user posjetio dotičnu stranicu).
// U listi distinctUrls pohranjuju se distinct vrijednosti iz liste allUrls.
		try{
			Scanner logScanner = new Scanner(logInput);
			while(logScanner.hasNextLine()){
				String logItem = logScanner.nextLine();
				String webpage = logItem.substring(0, logItem.indexOf(" "));
				allUrls.add(logItem);
				if(distinctWebpages.size() <= 0){
					distinctWebpages.add(webpage);
				}else{
					if(distinctWebpages.contains(webpage)){
						continue;
					}else{
						distinctWebpages.add(webpage);
					}
				}
			}
			
			logScanner.close();
// For petlja u for petlji. U vanjskoj petlji iterira se kroz listu distinct websiteova bez IP-ova, dok se u drugoj petlji iterira kroz listu svih posjećenih webstranica zajedno sa podatkom koji je user posjetio dotičnu stranicu (informacija o tome sadržana u IP-u).
// Uspoređuje se web domena iz distinctWebpages liste sa web domenom iz liste allUrls. Ukoliko je ona jednaka, uz pomoć funkcije contains kreira se lista koji sadrži informacije koji su različiti korisnici posjetili određenu stranicu (distinctUrls). 
// Drugim riječima lista distinctUrls sadrži popis jedinstvenih IP-ova s kojih je posjećena neka stranica. To je temporary lista, što znači da se brišu svi zapisi iz nje prilikom svake nove iteracije kroz distinctWebpages.
// Veličina (size) te liste zapravo predstavlja broj unique view-ova za neku stranicu.
// Podaci o broju unique view-a neke web stranice pohranjuje se u Hashtable finalResultDict.
			for(int i = 0; i < distinctWebpages.size(); i++){
				distinctUrls.clear();
				for(int j = 0; j < allUrls.size(); j++){
					if(distinctWebpages.get(i).equals(allUrls.get(j).substring(0, allUrls.get(j).indexOf(" ")))){
						if(distinctUrls.size() <= 0){
							distinctUrls.add(allUrls.get(j));
						}else{
							if(distinctUrls.contains(allUrls.get(j))){
								continue;
							}else{
								distinctUrls.add(allUrls.get(j));
							}
						}
					}
				}
				finalResultDict.put(distinctWebpages.get(i), distinctUrls.size());
			}
			
//Uzimaju se vrijednosti iz Hashtable-a i zapisuju se u Array
			List<Map.Entry<String, Integer>> listbyHashtable = new ArrayList<Entry<String, Integer>>(finalResultDict.entrySet());
			
// Vrši se sortiranje uz pomoć komparatora(Desc- što znači od najveće vrijednosti prema najmanjoj)
			Collections.sort(listbyHashtable, new Comparator<Map.Entry<String, Integer>>(){		 
				public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2){
					return entry2.getValue().compareTo( entry1.getValue() );
				}		  
			});
//'Slaže' se završni string kojeg funkcija vraća - distinct lista webstranica bez IP-ova sa pripadajućim brojem unique view-ova.
			for(Map.Entry<String, Integer> entry : listbyHashtable){
				uniqueList += entry.getKey() + " " + entry.getValue() + " unique views" + "\r\n";
			}	
		}catch (FileNotFoundException e){
		}
		return uniqueList;
	}
}
