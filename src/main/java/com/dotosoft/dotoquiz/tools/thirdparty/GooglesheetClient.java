/*
	Copyright 2015 Denis Prasetio
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.dotosoft.dotoquiz.tools.thirdparty;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;

import com.dotosoft.dotoquiz.common.QuizConstant;
import com.dotosoft.dotoquiz.tools.common.QuizParserConstant;
import com.dotosoft.dotoquiz.tools.common.QuizParserConstant.DATA_TYPE;
import com.dotosoft.dotoquiz.tools.config.Settings;
import com.dotosoft.dotoquiz.tools.util.DotoQuizStructure;
import com.dotosoft.dotoquiz.utils.StringUtils;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;


public class GooglesheetClient {
	
	private static final Logger log = LogManager.getLogger(GooglesheetClient.class.getName());
	
	private final SpreadsheetService service = new SpreadsheetService(QuizParserConstant.SYNC_CLIENT_NAME);

	// Define the URL to request.  This should never change.
    private final URL SPREADSHEET_FEED_URL;
    private String spreadsheetQuery;
    private SpreadsheetEntry spreadsheetEntry;
    
    public GooglesheetClient( GoogleCredential credential, String spreadsheetName) throws MalformedURLException, IOException, ServiceException {
    	this((Credential) credential, spreadsheetName);
    }
	
	public GooglesheetClient( Credential credential, String spreadsheetName) throws MalformedURLException, IOException, ServiceException {
		SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
		this.spreadsheetQuery = spreadsheetName;
		
		service.setProtocolVersion(SpreadsheetService.Versions.V3);
		service.setOAuth2Credentials( credential );
        service.setConnectTimeout( 1000 * QuizParserConstant.CONNECTION_TIMEOUT_SECS );
        service.setReadTimeout(1000 * QuizParserConstant.CONNECTION_TIMEOUT_SECS);
        
        // Get Spreadsheet Entry
        // Make a request to the API and get all spreadsheets.
	    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
		List<SpreadsheetEntry> spreadsheets = feed.getEntries();
		
	    // Iterate through all of the spreadsheets returned
	    for (SpreadsheetEntry spreadsheet : spreadsheets) {
	    	if(spreadsheetQuery.equals(spreadsheet.getTitle().getPlainText())) {
	    		spreadsheetEntry = spreadsheet;
	    	}
	    }
	}
	
	public WorksheetEntry getWorksheet(int index) throws Exception {
		// Make a request to the API to fetch information about all worksheets in the spreadsheet.
	    List<WorksheetEntry> worksheets = spreadsheetEntry.getWorksheets();

	    return worksheets.get(index);
	}
	
	public List<WorksheetEntry> getWorksheets() throws Exception {
		List<WorksheetEntry> worksheetEntries = new ArrayList<WorksheetEntry>();
		
		// Make a request to the API to fetch information about all worksheets in the spreadsheet.
	    List<WorksheetEntry> worksheets = spreadsheetEntry.getWorksheets();

	    // Iterate through each worksheet in the spreadsheet.
	    for (WorksheetEntry worksheet : worksheets) {
	    	worksheetEntries.add(worksheet);
	    }
		
		return worksheetEntries;
	}
	
	public WorksheetEntry createNewSheet(String title, int col, int row) throws Exception {
		// Create a local representation of the new worksheet.
	    WorksheetEntry worksheet = new WorksheetEntry();
	    worksheet.setTitle(new PlainTextConstruct("New Worksheet"));
	    worksheet.setColCount(col);
	    worksheet.setRowCount(row);

	    return service.insert(SPREADSHEET_FEED_URL, worksheet);
	}
	
	public WorksheetEntry updateSheet(WorksheetEntry worksheetEntry) throws Exception {
	    return worksheetEntry.update();
	}
	
	public void deleteSheet(WorksheetEntry worksheetEntry) throws Exception {
		worksheetEntry.delete();
	}
	
	public List getListRows(Object worksheetEntryObject) throws Exception {
		WorksheetEntry worksheet = (WorksheetEntry) worksheetEntryObject; 
		// Fetch the list feed of the worksheet.
	    URL listFeedUrl = worksheet.getListFeedUrl();
	    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
	    return listFeed.getEntries();
	}
	
	public List<ListEntry> getSortedListRows(WorksheetEntry worksheet, boolean isReverse, String columnSorted) throws IOException, ServiceException, URISyntaxException {
		// Fetch the sorted list feed of the worksheet
	    URL listFeedUrl = new URI(worksheet.getListFeedUrl().toString() + "?reverse=" + (isReverse ? "true" : "false") + "?orderby=column:" + columnSorted).toURL();
	    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
	    return listFeed.getEntries();
	}
	
	public List<ListEntry> getListRowsByQuery(WorksheetEntry worksheet, String query) throws IOException, ServiceException, URISyntaxException {
		// Fetch the sorted list feed of the worksheet
	    URL listFeedUrl = new URI(worksheet.getListFeedUrl().toString() + "?sq=" + query).toURL();
	    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
	    return listFeed.getEntries();
	}
	
	public ListEntry insertRow(WorksheetEntry worksheet, ListEntry rowEntry) throws IOException, ServiceException, URISyntaxException {
		// Fetch the list feed of the worksheet.
	    URL listFeedUrl = worksheet.getListFeedUrl();

	    // Send the new row to the API for insertion.
	    return service.insert(listFeedUrl, rowEntry);
	}
	
	public ListEntry updateRow(WorksheetEntry worksheet, ListEntry rowEntry) throws IOException, ServiceException, URISyntaxException {
		// Fetch the list feed of the worksheet.
	    URL listFeedUrl = worksheet.getListFeedUrl();

	    // Send the new row to the API for insertion.
	    return service.update(listFeedUrl, rowEntry);
	}
	
	public void deleteRow(ListEntry rowEntry) throws IOException, ServiceException, URISyntaxException {
	    // Send the new row to the API for insertion.
	    rowEntry.delete();
	}

	public void showColumnHeader(ListEntry listEntry, Boolean useLog) {
		int index = 0;
		for (String tag : listEntry.getCustomElements().getTags()) {
			if(useLog) {
				log.info("** Column" + (index++) + ": " + tag);
			} else {
				System.out.println("** Column" + (index++) + ": " + tag);
			}
		}
		System.out.print("\n");
	}
	
	public static void updateSyncPicasa(Settings settings, String parseType, Object data, String picasaId, String imagePicasaURL, String isProcessed) throws IOException, ServiceException {
		String paramPIcasaId = DotoQuizStructure.getStructureKey(parseType, settings, "iAlbumIdPicasa");
		String paramImageURLPicasa = DotoQuizStructure.getStructureKey(parseType, settings, "iImageURLPicasa");
		String paramIsProcessed = DotoQuizStructure.getStructureKey(parseType, settings, "iIsProcessed");
		
		if(DATA_TYPE.EXCEL.toString().equals(settings.getDataType())) {
			Row rowData = (Row) data;
			
			if(StringUtils.hasValue(picasaId)) rowData.getCell(Integer.parseInt(paramPIcasaId)).setCellValue(picasaId);
			if(StringUtils.hasValue(imagePicasaURL)) rowData.getCell(Integer.parseInt(paramImageURLPicasa)).setCellValue(imagePicasaURL);
			rowData.getCell(Integer.parseInt(paramIsProcessed)).setCellValue(isProcessed);
		} else if(DATA_TYPE.GOOGLESHEET.toString().equals(settings.getDataType())) {
			ListEntry listEntry = (ListEntry) data;
			if(StringUtils.hasValue(picasaId)) listEntry.getCustomElements().setValueLocal(paramPIcasaId, picasaId);
			if(StringUtils.hasValue(imagePicasaURL)) listEntry.getCustomElements().setValueLocal(paramImageURLPicasa, imagePicasaURL);
			listEntry.getCustomElements().setValueLocal(paramIsProcessed, isProcessed);
			listEntry.update();
		}
	}
    
    public static void updateClearPicasa(Settings settings, String parseType, Object data) throws IOException, ServiceException {
    	String paramPIcasaId = DotoQuizStructure.getStructureKey(parseType, settings, "iAlbumIdPicasa");
		String paramImageURLPicasa = DotoQuizStructure.getStructureKey(parseType, settings, "iImageURLPicasa");
		String paramIsProcessed = DotoQuizStructure.getStructureKey(parseType, settings, "iIsProcessed");
		
		String picasaId = "";
		if(DATA_TYPE.EXCEL.toString().equals(settings.getDataType())) {
			Row rowData = (Row) data;
			picasaId = rowData.getCell(Integer.parseInt(paramPIcasaId)).getStringCellValue();
			rowData.getCell(Integer.parseInt(paramPIcasaId)).setCellValue(QuizConstant.EMPTY_STRING);
			rowData.getCell(Integer.parseInt(paramImageURLPicasa)).setCellValue(QuizConstant.EMPTY_STRING);
			rowData.getCell(Integer.parseInt(paramIsProcessed)).setCellValue(QuizConstant.EMPTY_STRING);
		} else if(DATA_TYPE.GOOGLESHEET.toString().equals(settings.getDataType())) {
			ListEntry listEntry = (ListEntry) data;
			picasaId = listEntry.getCustomElements().getValue(paramPIcasaId);
			listEntry.getCustomElements().setValueLocal(paramPIcasaId, QuizConstant.EMPTY_STRING);
			listEntry.getCustomElements().setValueLocal(paramImageURLPicasa, QuizConstant.EMPTY_STRING);
			listEntry.getCustomElements().setValueLocal(paramIsProcessed, QuizConstant.EMPTY_STRING);
			listEntry.update();
		}
		
		log.info("Clear Data " + parseType + ": " + picasaId);
    }
}
