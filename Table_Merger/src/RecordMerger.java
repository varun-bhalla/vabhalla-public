import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class RecordMerger {

	
	//public static final String DIRECTORY_PATH = "C:\\Users\\vabhalla.ORADEV\\workspace\\Veeva_Systems_Table_Merger\\data";
	/*
	 * DIRECTORY_PATH variable is added, if you have a fixed directory from where files should be read and written.
	 * It's set to blank right now. Input files should be passed with full path and output file will be written to execution directory.
	 */
	public static final String DIRECTORY_PATH = "";
	public static final String FILENAME_COMBINED = "combined.csv";
	private static Table<String,String,String> guavaTable;

	//Assuming ID is an integer. Using rowIncreasingOrder Comparator to sort in increasing order.
	public RecordMerger() {
		guavaTable = TreeBasedTable.create(rowIncreasingOrder, Comparator.naturalOrder());
	}

	/**
	 * Entry point of this test.
	 *
	 * @param args command line arguments: first.html and second.csv.
	 * @throws Exception bad things had happened.
	 */
	public static void main(final String[] fileNames) throws Exception {

		if (fileNames.length == 0) {
			System.err.println("Usage: java RecordMerger file1 [ file2 [...] ]");
			System.exit(1);
		}

		// your code starts here.
		RecordMerger rm = new RecordMerger();
		rm.mergeTableDataFromInputFiles(fileNames);
		if(guavaTable == null) {
			System.err.println("Something went wrong. Output file not generated");
			System.exit(1);
		}
		else {
			rm.exportTableToCSV(guavaTable);
		}
	}


	private void mergeTableDataFromInputFiles(String[] fileNames) throws IOException {
		for(String fileName:fileNames) {
			//Assuming files are passed with complete path address.
			String fileLocation = DIRECTORY_PATH+"\\"+fileName;
			String extension=FilenameUtils.getExtension(fileName);
			switch(extension) {
			case "html":convertHtmlToTable(fileLocation);
			break;
			case "csv":convertCsvToTable(fileLocation);
			break;
			/*
			 * Define methods to support unsupported file extensions and add a case to handle that extension.
			 */
			default: 
				System.err.println("File format for one of the supplied files is not supported");
				System.exit(1);
			}
		}
	}

	private void convertHtmlToTable(String fileName) throws IOException
	{
		String htmlFileLocation = DIRECTORY_PATH+"\\"+fileName;
		File htmlFile = new File(htmlFileLocation); 
		Document document = Jsoup.parse(htmlFile,"UTF-8");
        //Assuming table in html file is always under table tag with id=directory.
		Elements table = document.select("table#directory");
		//If html table with id=directory not found, Throw error and exit. 
		if(table.size()==0) {
			System.err.println("Table with directory id Not Found. Exiting.");
			System.exit(1);
		}
		Elements rows = table.get(0).select("tr"); //Parsing rows from table in html file
		int idColIndex = -1; 
		Map<Integer,String> headingMap = new HashMap<>();
        
		Elements columns = rows.get(0).select("th"); //Parsing header row
		//Exits if Header row is missing in Html File.
		if(columns.size() == 0) {
			System.err.println("Invalid Data.Header row is missing from Html File. Exiting");
			System.exit(1);
		}
		for (int i=0;i<columns.size();i++) {
			//Getting column index for ID column. Assuming ID column always exists and column headings are case-insensitive.
			if(columns.get(i).text().toUpperCase().equals("ID")) {
				idColIndex = i;
			}
			headingMap.put(i,columns.get(i).text().toUpperCase());				
		}

		for (Element row1:rows) {
			Elements dataColumns = row1.select("td");	
			for (int j =0;j<dataColumns.size();j++) {
				guavaTable.put(dataColumns.get(idColIndex).text(),headingMap.get(j),dataColumns.get(j).text());
			}	
		}
	}


	private void convertCsvToTable(String fileName) throws IOException {
		String csvFileLocation = DIRECTORY_PATH+"\\"+fileName;
		/*
	    CSVReader(Reader reader, char separator, char quotechar, char escape, int line, boolean strictQuotes) 
	    reader - The reader to an underlying CSV source.
	    separator - The delimiter to use for separating entries
	    quotechar - The character to use for quoted elements
	    skipLines - How many lines to skip before starting to parse csv
		 */
		/*
		 * Assuming delimeter in csv is ","
		 * Column values are enclosed in double-quotes(")
		 * Header row is the first line in CSV. If not, change skipLines parameter in CSVReader definition  
		 */
		 
		CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(csvFileLocation), "UTF-8"), ',','"',0);
		String[] nextLine;
		int idColIndex = -1; 
		int colNum = 0;
		Map<Integer,String> headingMap = new HashMap<>();
		//Reading first line in csv and inserting Column Headings in a HashMap
		if((nextLine = reader.readNext()) != null) {
			colNum=nextLine.length;
			for(int i=0;i<colNum;i++) {
				if (nextLine[i].toUpperCase().equals("ID")) {
					idColIndex = i;
				}
				headingMap.put(i,nextLine[i].toUpperCase());
			}
		}
		//Reading Table Data
		while ((nextLine = reader.readNext()) != null) {
			//Checks if each line in csv contains same number of columns as Header row.  If no, Program exits after throwing error
			if(nextLine.length != colNum) {
				System.err.println("Inconsistent Data Found.Exiting.");
				System.exit(1);
			}
			else {
				for(int j=0;j<colNum;j++) {
					String value= guavaTable.row(nextLine[idColIndex]).get(headingMap.get(j));
					if(value == null) {
						guavaTable.put(nextLine[idColIndex],headingMap.get(j),nextLine[j]);
					}
					//Checks, If same column for a specific id in file being exported to table, contains different value for a column than already being inserted in Guava table. If yes, Program exits after throwing error
					else if (!value.equals(nextLine[j])){
						System.err.println("Conflict found in supplied data. Exiting.");
						System.exit(1);
					}   		 
				}
			}
		}	
	}
	public void exportTableToCSV(Table<String, String, String> table) throws IOException{
		String outputFileLocation = DIRECTORY_PATH+"combined.csv";
		CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(outputFileLocation), "UTF-8"),',','"');
		Set<String> cols = table.columnKeySet();

		// write headers to output file
		writer.writeNext(cols.toArray(new String[cols.size()]));

		// write row data to output file
		for(String id : table.rowKeySet()){
			List<String> rowData = new ArrayList<>();
			for(String col : table.columnKeySet()){
				if(table.get(id, col)== null) {
					//add empty quotes if col data is null
					rowData.add("");
				}
				else {
					rowData.add(table.get(id, col));
				}
			}
			writer.writeNext(rowData.toArray(new String[rowData.size()]));
		}
		//close the writer
		writer.close();
	}

	private Comparator<String> rowIncreasingOrder = new Comparator<String>() {
		/*
		 *ID is handled as String but converted to Integer in overridden compare method to avoid lexicological order. 
		 */
		@Override
		public int compare(String s1, String s2) {
			return Integer.parseInt(s1)-Integer.parseInt(s2);
		}
	};

}