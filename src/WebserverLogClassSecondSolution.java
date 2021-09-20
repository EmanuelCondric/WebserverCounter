import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFileChooser;

import java.util.Map.Entry;

public class WebserverLogClassSecondSolution {
	
	public static void main(String[] args) throws IOException{
		String rangList = "";
		String uniqueList = "";
		JFileChooser j = new JFileChooser();
		j.showSaveDialog(null);
		File logInput = new File(j.getSelectedFile().getPath());
		rangList = getRankListOfSites(logInput);
		uniqueList = getUniqueListOfSites(logInput);
		System.out.println("Stranice po posjecenosti: \r\n" + rangList);
		System.out.println("Broj unikatnih url-ova prema domenama: \r\n" + uniqueList);
	}
	
	public static String getRankListOfSites(File logInput){
		String rangList = "";
// Kreira se Hashtable htResult. Prednost Hashtable-a jest što pohranjuje key (unikatnu vrijednost) te pripadajući value što znači da 
// će postojati jedinstveni ključ samo jednom zapisan bez obzira koliko puta se isti ključ 'put-a' unutar hashtable-a. Na taj način će se odmah napraviti distinct
// webstranica koje su posjećene. 	
		Hashtable<String, Integer> htResult = new Hashtable<String, Integer>();
		try {
			Scanner logScanner = new Scanner(logInput);
			while(logScanner.hasNextLine()){
// Cita se redak po redak log file-a. Izdavaja se webstranica bez podatka o IP-u (bez podatka o tome koji je korisnik posjetio stranicu).
// S funkcijom containsKey ispituje se dali u Hashtable-u postoji zapis s unikatnim key-em - webstranicom (recimo da li postoji key '/index').
// Ukoliko postoji, u varijablu counter sprema se value za dotični key (u ovom slučaju value je broj posjeta određenoj stranici). Nakon toga, za 
// dotični key setira se novi value koji je za 1 uvećan u odnosu na prethodni value.
// Ukoliko promatrani key još ne postoji unutar hashtable-a, za value tog key-a insertira se vrijednost 1 (varijabla counter je inicijalno postavljena na vrijednost 0, 
// sto znaci da je 0+1 = 1)
				Integer counter = 0;
				String logItem = logScanner.nextLine();
				String webpage = logItem.substring(0, logItem.indexOf(" "));
				if(htResult.containsKey(webpage)) {
					counter = htResult.get(webpage);
				}
				htResult.put(webpage, counter + 1);
			}
			logScanner.close();
			
// Na ovaj način u jednoj iteraciji kroz log file dobiju se sve distinct vrijednosti webstranica te broj posjeta istoj.
			
// Vrši se sortiranje od najvećeg value-a prema najmanjem
			
			List<Map.Entry<String, Integer>> listbyHashtable = new ArrayList<Entry<String, Integer>>(htResult.entrySet());
			Collections.sort(listbyHashtable, new Comparator<Map.Entry<String, Integer>>(){
				public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2){
					return entry2.getValue().compareTo( entry1.getValue());
				}	  
			});	
			
// 'Slaže' se završni String rangList u formatu u kakvom treba biti.
			for(Map.Entry<String, Integer> entry : listbyHashtable) {
				rangList += entry.getKey() + " " + entry.getValue() + " visits" + "\r\n";
			}
		}catch (FileNotFoundException e){
		}
		return rangList;
	}
	
	public static String getUniqueListOfSites(File logInput){
		String uniqueList = "";
// U ovom se slučaju za pohranu podataka koristio Hashtable koji za value prima tip podatka Set<String>.
// Kako se u ovom konkretnom slučaju tražio broj unikatnih view-a, koristio se Set zbog toga što Set ne dopušta dupliranje 
// vrijednosti unutar istog. Na taj se način u prvom koraku iteracije odmah dobije lista unikatnih IP-ova (user-a) koji su posjetili određenu stranicu.
// 
		Hashtable<String, Set<String>> htResultSet = new Hashtable<String, Set<String>>();
		Hashtable<String, Integer> htResultCount = new Hashtable<String, Integer>();
		try{
			Set<String> setVar;
			Scanner logScanner = new Scanner(logInput);
			while(logScanner.hasNextLine()){
				String logItem = logScanner.nextLine();
				String webpage = logItem.substring(0, logItem.indexOf(" "));
				if(htResultSet.containsKey(webpage)) {
					setVar = htResultSet.get(webpage);
				}else{
					setVar = new HashSet<String>();
				}
				setVar.add(logItem);
				htResultSet.put(webpage, setVar);
			}	
// Broj unikatnih view-ova dobije se kao dužina (size) Set-a unutar hashtable-a htResultSet. Website kao key te broj unikatnih view-a kao value pohranjuju se u hashtable htResultCount 
			List<Map.Entry<String, Set<String>>> listbyHashtable = new ArrayList<Entry<String, Set<String>>>(htResultSet.entrySet());
			for(Map.Entry<String, Set<String>> entry : listbyHashtable) {
				htResultCount.put(entry.getKey(), entry.getValue().size());
			}
// Vrši se soritranje hashtable-a htResultCount od najveceg value-a ka najmanjem.
			List<Map.Entry<String, Integer>> listbyHashtableCount = new ArrayList<Entry<String, Integer>>(htResultCount.entrySet());
			Collections.sort(listbyHashtableCount, new Comparator<Map.Entry<String, Integer>>(){
				public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2){
					return entry2.getValue().compareTo( entry1.getValue());
				}	  
			});
// 'Slaže' se završni String uniqueList u formatu u kakvom treba biti.
			for(Map.Entry<String, Integer> entry : listbyHashtableCount){
				uniqueList += entry.getKey() + " " + entry.getValue() + " unique views" + "\r\n";
			}	
		}catch (FileNotFoundException e){
		}
		return uniqueList;
	}

}
