//Cole Grigsby + Robert Weber 

import static j2html.TagCreator.*;
import j2html.tags.Tag;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Report {

	public static void main(String[] args) {
		
		//TODO store general info Tag and just add it into each ind report that is run 
		
		writeFile("test");

	}
	
	public static void writeFile(String ticker){
		
		File f;
		FileWriter fw;
		try {
			f = new File(ticker + ".html");
			fw = new FileWriter(f);
			fw.write(
					html(
						head(title(ticker), link().withRel("stylesheet").withHref("css/main.css"))
							.with(h1("General Info!"))	
						,body(generalInfo(),
								tickerInfo("GOOG", "Google"))	
						).render());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	// GENERAL INFO METHODS -------------
	
	public static Tag generalInfo() {
		ArrayList<ArrayList<String>> labels = new ArrayList<>();
		ArrayList<String> s = new ArrayList<>();
		
		s.add("cole");
		s.add("yes");
		
		ArrayList<String> t = new ArrayList<>();
		
		t.add("robert");
		t.add("no");
		
		labels.add(s);
		labels.add(t);
		
		List<String> head1 = Arrays.asList("Symbol", "Full Name");
		List<String> head2 = Arrays.asList("2014", "2015", "2016");

		
		HashMap<String, ArrayList<ArrayList<String>>> hm = new HashMap<>();
		
		hm.put("2014", labels);
		hm.put("2015", labels);
		hm.put("2016", labels);
		
		return div(info(),
				topTable("Top Ten Most Heavily Traded in 2016", head1, labels),
				topFivePerYear(head2, hm),
				topTable("Ten Stocks to Watch in 2017", head1, labels),
				p("The above stocks are 10 that we determined to be on the lookout for in 2017. "
						+ "We based these decisions ONASDFASDFASD F")
				//TODO assessment 		
						);
	}

	public static Tag info(){
		return div(numInfo("Total Securities traded at the beggining of 2016", 10000),
				numInfo("Total Securities traded at the end of 2016", 10000),
				numInfo("Total Securities with a price increase between end of 2015 and end of 2016", 10000)
				);
	}
	
	public static Tag numInfo(String s, int i){
		return div(h4(s + ": " + Integer.toString(i)));
	}
	
	public static Tag topTable(String name, List<String> head, ArrayList<ArrayList<String>> labels){
		List<Integer> numbers = Arrays.asList(1, 2);
		
		return div(h2(name),
				table(
					thead(each(head, h -> th(h))),
						tbody(
							each(labels, i -> tr( 
								each(i, w -> td(w))
			            ))
			        )
			    ));
	}
	
	public static Tag topFivePerYear(List<String> years, HashMap<String, ArrayList<ArrayList<String>>> tickers){
		List<String> head = Arrays.asList("Absolute Top Symbol", "Relative Top Symbol");

		return div(h2("Top five stocks per year"), p("For each year with data, the following tables give "
				+ "the top 5 ticker symbols for the highest performing stocks based on their absolute and "
				+ "relative price increases."),
				each(years, y -> 
							topTable("Top Five for " + y, head, tickers.get(y))
						)
				
				);
	}
	
	
	// ---------------------------------
	
	// TICKER SPECIFIC OUTPUT 
	
	public static Tag tickerInfo(String sym, String name){
		
		return div( h1("Individual Data"), h2(sym + " : " + name)
				//dates, performance by year 
				// 
				
				);
	}
	
	public static Tag dateInfo(String start, String end){
		return div(p("Data is available from " + start + " until " + end)
				);
	}
	
	public static Tag byYearInfo(List<String> years, ArrayList<ArrayList<String>> data){
		List<String> head = Arrays.asList("Increase/Decrease from Previous Year", 
				"Volume of Trading", "Average Closing Price", "Average Trade Volume per Day");

		
		return div(
				each(years, y -> 
				topTable(y, head, data)
						)
				);
	

	}
	
	public static Tag lastYearInfo(String y, ArrayList<ArrayList<String>> data){
		List<String> head = Arrays.asList(
				"Average Closing Price", "Highest Price", "Lowest Price",
				"Average Trade Volume per Month");

		return div(h3("Info for " + y),
				topTable(y, head, data)
				);
				
	}
	
	public static Tag byYearBestMonth(List<String> years, ArrayList<String> data){

		//TODO explanation of criteria
		
		return div(
				each(years, y -> 
					div(h2(y), p("Best Month")))
				);
	
	}
	
	public static Tag predictions(){
		return div();
	}
	
	public static Tag outcomes(){
		return div();
	}
	
	public static Tag compareTop(){
		return div();
	}
	
	public static Tag compareNearby(){
		return div();
	}
	

}
