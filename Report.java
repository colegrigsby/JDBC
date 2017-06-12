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

import org.omg.IOP.TAG_CODE_SETS;

public class Report {
	
	private ArrayList<Tag> tags;
	private String ticker;
	
	public Report(String ticker){
		this.ticker=ticker;
		tags = new ArrayList<>();
	}

	public static void main(String[] args) {
		
		//TODO store general info Tag and just add it into each ind report that is run 
		
		//writeFile();
		
		Report r = new Report("test");
		//r.info("1",10000,10000000);
		r.writeFile();

	}
	
	public void writeFile(){
		
		File f;
		FileWriter fw;
		try {
			f = new File(ticker + ".html");
			fw = new FileWriter(f);
			fw.write(
					html(
						head(title(ticker), link().withRel("stylesheet").withHref("css/main.css"))
							.with(h1("General Info!"))	
						,body().with(tags)
						).render());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*private String getBody(){
		String s = "";
		for (Tag t: tags){
			s += t.;
		}
		return s;
	}*/
	
	// GENERAL INFO METHODS -------------
	

	public void info(String totalBeg, String totalEnd, String totalP, String totalD){
		tags.add(div(numInfo("Total Securities traded at the beggining of 2016", totalBeg),
				numInfo("Total Securities traded at the end of 2016", totalEnd),
				numInfo("Total Securities with a price increase between end of 2015 and end of 2016", totalP),
				numInfo("Total Securities with a price decrease between end of 2015 and end of 2016", totalD)
				));
	}
	
	public static Tag numInfo(String s, String i){
		return div(h4(s + ": " + i));
	}
	
	public void topTable(String name, List<String> head, ArrayList<ArrayList<String>> labels){
		tags.add(top(name,head,labels));
	}
	
	public static Tag top(String name, List<String> head, ArrayList<ArrayList<String>> labels){
		
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
	
	public void topFivePerYear(List<String> years, HashMap<String, ArrayList<ArrayList<String>>> tickers){
		List<String> head = Arrays.asList("Absolute Top Symbol", "Relative Top Symbol");

		tags.add(div(h2("Top five stocks per year"), p("For each year with data, the following tables give "
				+ "the top 5 ticker symbols for the highest performing stocks based on their absolute and "
				+ "relative price increases."),
				each(years, y -> 
							top("Top Five for " + y, head, tickers.get(y))
						)
				
				));
	}
	
	
	// ---------------------------------
	
	// TICKER SPECIFIC OUTPUT 
	
	public void tickerInfo(String sym, String name){
		tags.add(div( h1("Individual Data"), h2(sym + " : " + name)
				));
	}
	
	public void dateInfo(String start, String end){
		tags.add(div(p("Data is available from " + start + " until " + end)
				));
	}
	
	public void byYearInfo(ArrayList<ArrayList<String>> data){
		List<String> head = Arrays.asList("Increase/Decrease from Previous Year", 
				"Volume of Trading", "Average Closing Price", "Average Trade Volume per Day");

		
		tags.add(div(
				top("", head, data)
				));
	

	}
	
	public void lastYearInfo(String y, ArrayList<ArrayList<String>> data){
		List<String> head = Arrays.asList(
				"Month", "Average Closing Price", "Highest Price", "Lowest Price",
				"Average Trade Volume per Month");

		tags.add(div(h3("Info for " + y),
				top(y, head, data)
				));
				
	}
	
	public void byYearBestMonth(ArrayList<ArrayList<String>> data){

		//TODO explanation of criteria
		
		tags.add(div(
				p("To choose the best month of each year, a score was calculated based on "
						+ "the standardized volume by month, the standardized average closing"
						+ " price by month and the ratio of days in which the close was greater"
						+ " than the open to the close being less than the open. The months with the "
						+ " highest score in each year was chosen as the best performance month."),
				top("Best Months per Year", Arrays.asList("Year", "Month"),data)
				));
	
	}
	
	public void predictions(ArrayList<ArrayList<String>> data1, ArrayList<ArrayList<String>> data2, ArrayList<ArrayList<String>> data3,
			ArrayList<ArrayList<String>> data4, ArrayList<ArrayList<String>> data5, ArrayList<ArrayList<String>> data6){
		
		
		ArrayList<ArrayList<String>> pred = new ArrayList<>(); 
		
		
		//TODO 
		
		
		List<String> head = Arrays.asList("Date", "Position"); 
		tags.add(div(
				p("Predictions for each date are based on the relative"
				+ " price of the stock, how volatile it is based on the days up versus down and the overall recent "
				+ " increases/decreases over the previous two months"),
				top("Predictions", head, pred)
				));
	}
	
	public void outcomes(ArrayList<ArrayList<String>> data){
		List<String> head = Arrays.asList("Date", "Position"); 
		tags.add(div(
				p("Outcomes are based upon the same criteria as above, but with an emphasis on "
						+ "the increase in price."),
					top("Results", head, data)
				));
	}
	
	public void compareTop(String year, ArrayList<ArrayList<String>> dataThis, ArrayList<ArrayList<String>> dataTop){
		List<String> head = Arrays.asList("Year", "Month", "Ticker Symbol", "Increase", "Volume"); 
		tags.add(div(
				p("Top performing Stocks here are based upon the highest percentage"
						+ " increase in value"),
				top(year + " " +ticker + " data", head, dataThis), 
				top(year + " Top Performing Stock data", head, dataTop)
				));
	}
	
	public void compareNearby(String year, String tick2, ArrayList<ArrayList<String>> data1,ArrayList<ArrayList<String>> data2){
		List<String> head = Arrays.asList("TODO"); 
		tags.add(div(
				p("Compare "+ticker+" to " + tick2),
				top("month by month for " + year + ": " + ticker, head, data1),
				top("month by month for " + year + ": " + tick2, head, data2),
				p("And the better performing stock is! : TODOAODS")
				));
	}
	

}
