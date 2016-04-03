package com.khayal.shero.services;

import com.khayal.shero.BrandLuceneIndexer;
import com.khayal.shero.SHSurveyUser;
import com.sdl.dxa.modules.generic.model.BrandSearch;
import com.sdl.dxa.modules.generic.model.BrandSearchList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.SuggestMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;

/**
 * Created by DELL on 11/2/2015.
 */
public class SHSearchService {
	private static final Logger LOG = LoggerFactory.getLogger(SHSearchService.class);
	public static List<BrandSearch> searchBrand(HttpServletRequest request, String searchText, String sector){
		String searchQuery = null, strItemCatId = null;
		int aMaxCount = 7, bMaxCount = 10, cMaxCount = 10;
		searchQuery = searchText;
		if(sector == null){
			strItemCatId = "0";
		} else {
			strItemCatId = sector;
		}
		//searchQuery = "pizz";
		int allCount = 0;
		try {request.setCharacterEncoding("UTF-8");
			int docId, aCount = 0, bCount = 0, count = 0, cCount=0, dCount=0;
			boolean loopSet=false;
			Document doc;
			StringBuffer objSBxml = new StringBuffer();
			TreeMap<String,String> objTermMapFirst=new TreeMap<String,String>();
			TreeMap<String,String> objSortedTermMap=new TreeMap<String,String>();
			TreeMap<String,String> objTermMap=new TreeMap<String,String>();
			TreeMap<String,String> objTMAlphaSort=new TreeMap<String,String>();
			long startTime = System.currentTimeMillis();
			IndexSearcher searcher = null;
		    Analyzer temp = new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_41);
		    Analyzer analyzer = new ShingleAnalyzerWrapper(temp, 4);
		    Directory directory = null;

			String userCountry = (String) request.getSession().getAttribute("userCountry");
		    String userLanguage = (String) request.getSession().getAttribute("userLanguage");
		    final File dirFile = new File(request.getServletContext().getRealPath("/") + File.separator + BrandLuceneIndexer.DEFAULT_WEB_ROOT + File.separator + BrandLuceneIndexer.DEFAULT_LUCENE_ROOT + File.separator + userCountry + "-" + userLanguage);
		    
	    	int minimumVotes = 10;
		    if(userCountry.equals("ae"))
		    	minimumVotes = -1;
		    else
		    	minimumVotes = 10;
		    
		    String brandTitles = "";
		    	
		    String tempTerm = "", titlevalue = "", strTMKey = "";
		    directory = FSDirectory.open(dirFile);
		    DirectoryReader dRdr = DirectoryReader.open(directory);
		    searcher = new IndexSearcher(dRdr);
		    //QueryParser qp = new QueryParser(Version.LUCENE_41, "titlevalue", analyzer);
			//Query query = qp.parse(searchQuery);
		    Query query;
		    QueryParser qp;
		    //request.getSession().setAttribute("searchBrand", searchText);
		    /*if(searchText.indexOf(",") == -1) { 
			    qp = new QueryParser(Version.LUCENE_41, BrandLuceneIndexer.INDEXER_SERACH_FIELD_TITLE_SEARCH, analyzer);
				query = qp.parse(searchText);
		    } else {
	    		StringTokenizer objSt = new StringTokenizer(searchText, ",");
	    		int icount = objSt.countTokens();
	    		String[] fieldsValue = new String[icount];
	    		String[] fieldsName = new String[icount];
	    		for(int i = 0; i < icount; i++) {
	    			fieldsValue[i] = objSt.nextToken();
	    			fieldsName[i] = BrandLuceneIndexer.INDEXER_SERACH_FIELD_TITLE_SEARCH;
	    		} 
	    		query = MultiFieldQueryParser.parse(Version.LUCENE_41, fieldsValue, fieldsName, analyzer);
		    }*/		    
		    
			Map<String, String> reversedMap = objTermMap.descendingMap();
			int voteCount = 0, maxVoteCount = 0;
			boolean hasVotes = false;
			TopDocs hits;
			
			qp = new QueryParser(Version.LUCENE_41, BrandLuceneIndexer.INDEXER_SERACH_FIELD_TITLE, analyzer);
			qp.setAllowLeadingWildcard(true);
		    query = qp.parse(searchQuery);
		    hits = searcher.search(query, 500);
		    objTermMap = new TreeMap<String,String>();
			for (int i = 0; i < hits.scoreDocs.length; i++) {
				docId = hits.scoreDocs[i].doc;
				doc = searcher.doc(docId);
				if(doc.get("active").toLowerCase().equals("y")) {	
					objTermMap.put(doc.get("weightage") + "~" + doc.get("titlevalue"), "" + i);
				}
			}
			reversedMap = objTermMap.descendingMap();
			for (Map.Entry<String, String> entry : reversedMap.entrySet()) {
				docId = hits.scoreDocs[Integer.parseInt(entry.getValue())].doc;
				doc = searcher.doc(docId);
				try {
					voteCount = Integer.parseInt(doc.get("weightage")) % 10000000;
				} catch(Exception ex) {
					voteCount = 0;
				}
				if(maxVoteCount < voteCount)
					maxVoteCount = voteCount;
				if(brandTitles.indexOf(doc.get("titlevalue")) == -1) {
					if(voteCount > minimumVotes && allCount < 18 && (minimumVotes > -1 || (minimumVotes == -1 && doc.get("priority").equals("y")))) {
						strTMKey = doc.get("titlevalue") +"~" + doc.get("countryid") + "~" + doc.get("sectorid") + "~" + doc.get("sectortitlevalue")+ "~" + doc.get("sectorcaption")+ "~" + doc.get("sectorsvgimg")+ "~" + doc.get("sectornormalimg")+ "~" + doc.get("parentsectorid")+ "~" + doc.get("parentsectortitlevalue")+ "~" + doc.get("parentsectorcaption")+ "~" + doc.get("parentsectorsvgimg")+ "~" + doc.get("parentsectornormalimg")  + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("brandid") + "~" + doc.get("weightage") + "~" + doc.get("priority") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X") ;
						objSortedTermMap.put("0~" + strTMKey, doc.get("titlevalue"));
						allCount++;
					} else {
						objTMAlphaSort.put(doc.get("titlevalue"), doc.get("countryid") + "~" + doc.get("parentsectorid") + "~" + doc.get("sectorid") + "~" + doc.get("brandid") + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("titlevalue") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X"));
					}
					brandTitles += doc.get("titlevalue") + ",";
				}

			}
			
			qp = new QueryParser(Version.LUCENE_41, BrandLuceneIndexer.INDEXER_SERACH_FIELD_TITLE_SEARCH, analyzer);
			qp.setAllowLeadingWildcard(true);
		    tempTerm = "*" + searchQuery + "*";
		    query = qp.parse(tempTerm);
		    hits = searcher.search(query, 500);
			objTermMap = new TreeMap<String,String>();
			for (int i = 0; i < hits.scoreDocs.length; i++) {
				docId = hits.scoreDocs[i].doc;
				doc = searcher.doc(docId);
				if(doc.get("active").toLowerCase().equals("y")) {	
					objTermMap.put(doc.get("weightage") + "~" + doc.get("titlevalue"), "" + i);
				}
			}
			reversedMap = objTermMap.descendingMap();
			for (Map.Entry<String, String> entry : reversedMap.entrySet()) {
				docId = hits.scoreDocs[Integer.parseInt(entry.getValue())].doc;
				doc = searcher.doc(docId);
				try {
					voteCount = Integer.parseInt(doc.get("weightage")) % 10000000;
				} catch(Exception ex) {
					voteCount = 0;
				}
				if(maxVoteCount < voteCount)
					maxVoteCount = voteCount;
				if(brandTitles.indexOf(doc.get("titlevalue")) == -1) {
					if(voteCount > minimumVotes && allCount < 18 && (minimumVotes > -1 || (minimumVotes == -1 && doc.get("priority").equals("y")))) {
						strTMKey = doc.get("titlevalue") +"~" + doc.get("countryid") + "~" + doc.get("sectorid") + "~" + doc.get("sectortitlevalue")+ "~" + doc.get("sectorcaption")+ "~" + doc.get("sectorsvgimg")+ "~" + doc.get("sectornormalimg")+ "~" + doc.get("parentsectorid")+ "~" + doc.get("parentsectortitlevalue")+ "~" + doc.get("parentsectorcaption")+ "~" + doc.get("parentsectorsvgimg")+ "~" + doc.get("parentsectornormalimg")  + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("brandid")+ "~" + doc.get("weightage") + "~" + doc.get("priority") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X") ;
						objSortedTermMap.put("A~" + strTMKey, doc.get("titlevalue"));
						allCount++;
					} else {
						objTMAlphaSort.put(doc.get("titlevalue"), doc.get("countryid") + "~" + doc.get("parentsectorid") + "~" + doc.get("sectorid") + "~" + doc.get("brandid") + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("sectortitlevalue") + "~" + doc.get("titlevalue") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X"));
					}
					brandTitles += doc.get("titlevalue") + ",";
				}
			}
			
			
			qp = new QueryParser(Version.LUCENE_41, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SECTOR_TITLE_SEARCH, analyzer);
			qp.setAllowLeadingWildcard(true);
		    tempTerm = "*" + searchQuery + "*";
		    query = qp.parse(tempTerm);
		    hits = searcher.search(query, 500);
			objTermMap = new TreeMap<String,String>();
			for (int i = 0; i < hits.scoreDocs.length; i++) {
				docId = hits.scoreDocs[i].doc;
				doc = searcher.doc(docId);
				strTMKey = doc.get("titlevalue") + "~" + doc.get("countryid") + "~" + doc.get("sectorid") + "~" + doc.get("sectortitlevalue")+ "~" + doc.get("sectorcaption")+ "~" + doc.get("sectorsvgimg")+ "~" + doc.get("sectornormalimg")+ "~" + doc.get("parentsectorid")+ "~" + doc.get("parentsectortitlevalue")+ "~" + doc.get("parentsectorcaption")+ "~" + doc.get("parentsectorsvgimg")+ "~" + doc.get("parentsectornormalimg")  + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("brandid")+ "~" + doc.get("weightage") + "~" + doc.get("priority") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X");
				if(objSortedTermMap.get("A~" + strTMKey) == null) {
					if(doc.get("active").toLowerCase().equals("y")) {	
						objTermMap.put(doc.get("weightage") + "~" + doc.get("titlevalue"), "" + i);
					}
				}
			}
			reversedMap = objTermMap.descendingMap();
			for (Map.Entry<String, String> entry : reversedMap.entrySet()) {
				docId = hits.scoreDocs[Integer.parseInt(entry.getValue())].doc;
				doc = searcher.doc(docId);
				try {
					voteCount = Integer.parseInt(doc.get("weightage")) % 10000000;
				} catch(Exception ex) {
					voteCount = 0;
				}
				if(maxVoteCount < voteCount)
					maxVoteCount = voteCount;
				if(brandTitles.indexOf(doc.get("titlevalue")) == -1) {
					if(voteCount > minimumVotes && allCount < 18 && (minimumVotes > -1 || (minimumVotes == -1 && doc.get("priority").equals("y")))) {
						strTMKey = doc.get("titlevalue") +"~" + doc.get("countryid") + "~" + doc.get("sectorid") + "~" + doc.get("sectortitlevalue")+ "~" + doc.get("sectorcaption")+ "~" + doc.get("sectorsvgimg")+ "~" + doc.get("sectornormalimg")+ "~" + doc.get("parentsectorid")+ "~" + doc.get("parentsectortitlevalue")+ "~" + doc.get("parentsectorcaption")+ "~" + doc.get("parentsectorsvgimg")+ "~" + doc.get("parentsectornormalimg")  + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("brandid")+ "~" + doc.get("weightage") + "~" + doc.get("priority") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X");
						objSortedTermMap.put("B~" + strTMKey, doc.get("titlevalue"));
						allCount++;
					} else {
						objTMAlphaSort.put(doc.get("titlevalue"), doc.get("countryid") + "~" + doc.get("parentsectorid") + "~" + doc.get("sectorid") + "~" + doc.get("brandid") + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("sectortitlevalue") + "~" + doc.get("titlevalue") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X"));
					}
					brandTitles += doc.get("titlevalue") + ",";
				}
			}

			qp = new QueryParser(Version.LUCENE_41, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SHORT_NAME, analyzer);
			qp.setAllowLeadingWildcard(true);
		    tempTerm = "*" + searchQuery + "*";
		    query = qp.parse(tempTerm);
		    hits = searcher.search(query, 500);
			objTermMap = new TreeMap<String,String>();
			for (int i = 0; i < hits.scoreDocs.length; i++) {
				docId = hits.scoreDocs[i].doc;
				doc = searcher.doc(docId);
				strTMKey = doc.get("titlevalue") +"~" + doc.get("countryid") + "~" + doc.get("sectorid") + "~" + doc.get("sectortitlevalue")+ "~" + doc.get("sectorcaption")+ "~" + doc.get("sectorsvgimg")+ "~" + doc.get("sectornormalimg")+ "~" + doc.get("parentsectorid")+ "~" + doc.get("parentsectortitlevalue")+ "~" + doc.get("parentsectorcaption")+ "~" + doc.get("parentsectorsvgimg")+ "~" + doc.get("parentsectornormalimg")  + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("brandid")+ "~" + doc.get("weightage") + "~" + doc.get("priority") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X");
				if(objSortedTermMap.get("A~" + strTMKey) == null) {
					if(objSortedTermMap.get("B~" + strTMKey) == null) {
						if(doc.get("active").toLowerCase().equals("y")) {	
							objTermMap.put(doc.get("weightage") + "~" + doc.get("titlevalue"), "" + i);
						}
					}

				}
			}
			reversedMap = objTermMap.descendingMap();
			for (Map.Entry<String, String> entry : reversedMap.entrySet()) {
				docId = hits.scoreDocs[Integer.parseInt(entry.getValue())].doc;
				doc = searcher.doc(docId);
				try {
					voteCount = Integer.parseInt(doc.get("weightage")) % 10000000;
				} catch(Exception ex) {
					voteCount = 0;
				}
				if(maxVoteCount < voteCount)
					maxVoteCount = voteCount;
				if(brandTitles.indexOf(doc.get("titlevalue")) == -1) {
					if(voteCount > minimumVotes && allCount < 18 && (minimumVotes > -1 || (minimumVotes == -1 && doc.get("priority").equals("y")))) {
						strTMKey = doc.get("titlevalue") +"~" + doc.get("countryid") + "~" + doc.get("sectorid") + "~" + doc.get("sectortitlevalue")+ "~" + doc.get("sectorcaption")+ "~" + doc.get("sectorsvgimg")+ "~" + doc.get("sectornormalimg")+ "~" + doc.get("parentsectorid")+ "~" + doc.get("parentsectortitlevalue")+ "~" + doc.get("parentsectorcaption")+ "~" + doc.get("parentsectorsvgimg")+ "~" + doc.get("parentsectornormalimg")  + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("brandid")+ "~" + doc.get("weightage") + "~" + doc.get("priority") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X");
						objSortedTermMap.put("C~" + strTMKey, doc.get("titlevalue"));
						allCount++;
					} else {
						objTMAlphaSort.put(doc.get("titlevalue"), doc.get("countryid") + "~" + doc.get("parentsectorid") + "~" + doc.get("sectorid") + "~" + doc.get("brandid") + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("sectortitlevalue") + "~" + doc.get("titlevalue") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X"));
					}
					brandTitles += doc.get("titlevalue") + ",";
				}
			}
	
			IndexWriterConfig conf1 = new IndexWriterConfig(Version.LUCENE_41, new ShingleAnalyzerWrapper(new StandardAnalyzer(Version.LUCENE_41), 4));
			Directory directory1 = FSDirectory.open(dirFile);
			IndexReader indexReader = IndexReader.open(directory1);
			SpellChecker spellChecker = new SpellChecker(directory1);
			spellChecker.indexDictionary(new LuceneDictionary(indexReader, "titlevalue"), conf1, true);
			String wordForSuggestions = searchQuery;
			int suggestionsNumber =5;
			String[] suggestions = spellChecker.suggestSimilar(wordForSuggestions, suggestionsNumber,indexReader,"titlevalue", SuggestMode.SUGGEST_ALWAYS,0.8f);
			if (suggestions != null && suggestions.length > 0) {
				for (String word : suggestions) {
					if(cCount < cMaxCount) {
						query = qp.parse(word);
						hits = searcher.search(query, 500);
						objTermMap = new TreeMap<String,String>();
						for (int i = 0; i < hits.scoreDocs.length; i++) {
							docId = hits.scoreDocs[i].doc;
							doc = searcher.doc(docId);
							titlevalue = doc.get("titlevalue");
							strTMKey = doc.get("titlevalue") +"~" + doc.get("countryid") + "~" + doc.get("sectorid") + "~" + doc.get("sectortitlevalue")+ "~" + doc.get("sectorcaption")+ "~" + doc.get("sectorsvgimg")+ "~" + doc.get("sectornormalimg")+ "~" + doc.get("parentsectorid")+ "~" + doc.get("parentsectortitlevalue")+ "~" + doc.get("parentsectorcaption")+ "~" + doc.get("parentsectorsvgimg")+ "~" + doc.get("parentsectornormalimg")  + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("brandid")+ "~" + doc.get("weightage") + "~" + doc.get("priority") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X");
							if(objSortedTermMap.get("A~" + strTMKey) == null) {
								if(objSortedTermMap.get("B~" + strTMKey)==null) {
									if(objSortedTermMap.get("C~" + strTMKey)==null) {
										if(doc.get("active").toLowerCase().equals("y")) {	
											if(doc.get("active").toLowerCase().equals("y")) {	
												objTermMap.put(doc.get("weightage") + "~" + doc.get("titlevalue"), "" + i);
											}
										}
									}
								}
							}
						}
						reversedMap = objTermMap.descendingMap();
						for (Map.Entry<String, String> entry : reversedMap.entrySet()) {
							docId = hits.scoreDocs[Integer.parseInt(entry.getValue())].doc;
							doc = searcher.doc(docId);
							try {
								voteCount = Integer.parseInt(doc.get("weightage")) % 10000000;
							} catch(Exception ex) {
								voteCount = 0;
							}
							if(maxVoteCount < voteCount)
								maxVoteCount = voteCount;
							if(brandTitles.indexOf(doc.get("titlevalue")) == -1) {
								if(voteCount > minimumVotes && allCount < 18 && (minimumVotes > -1 || (minimumVotes == -1 && doc.get("priority").equals("y")))) {
									strTMKey = doc.get("titlevalue") +"~" + doc.get("countryid") + "~" + doc.get("sectorid") + "~" + doc.get("sectortitlevalue")+ "~" + doc.get("sectorcaption")+ "~" + doc.get("sectorsvgimg")+ "~" + doc.get("sectornormalimg")+ "~" + doc.get("parentsectorid")+ "~" + doc.get("parentsectortitlevalue")+ "~" + doc.get("parentsectorcaption")+ "~" + doc.get("parentsectorsvgimg")+ "~" + doc.get("parentsectornormalimg")  + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("brandid")+ "~" + doc.get("weightage") + "~" + doc.get("priority") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X");
									objSortedTermMap.put("D~" + strTMKey, doc.get("titlevalue"));
									allCount++;
								} else {
									objTMAlphaSort.put(doc.get("titlevalue"), doc.get("countryid") + "~" + doc.get("parentsectorid") + "~" + doc.get("sectorid") + "~" + doc.get("brandid") + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("sectortitlevalue") + "~" + doc.get("titlevalue") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X"));
								}
								brandTitles += doc.get("titlevalue") + ",";
							}
						}
						
						qp.setAllowLeadingWildcard(true);
						word="*" + word + "*";
						query = qp.parse(word);
						hits = searcher.search(query, 500);
						objTermMap = new TreeMap<String,String>();
						for (int i = 0; i < hits.scoreDocs.length; i++) {
							docId = hits.scoreDocs[i].doc;
							doc = searcher.doc(docId);
							strTMKey = doc.get("titlevalue") +"~" + doc.get("countryid") + "~" + doc.get("sectorid") + "~" + doc.get("sectortitlevalue")+ "~" + doc.get("sectorcaption")+ "~" + doc.get("sectorsvgimg")+ "~" + doc.get("sectornormalimg")+ "~" + doc.get("parentsectorid")+ "~" + doc.get("parentsectortitlevalue")+ "~" + doc.get("parentsectorcaption")+ "~" + doc.get("parentsectorsvgimg")+ "~" + doc.get("parentsectornormalimg")  + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("brandid")+ "~" + doc.get("weightage") + "~" + doc.get("priority") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X");
							if(objSortedTermMap.get("A~" + strTMKey)==null) {
								if(objSortedTermMap.get("B~" + strTMKey)==null) {
									if(objSortedTermMap.get("C~" + strTMKey)==null) {
										if(objSortedTermMap.get("D~" + strTMKey)==null) {
											if(doc.get("active").toLowerCase().equals("y")) {	
												objTermMap.put(doc.get("weightage") + "~" + doc.get("titlevalue"), "" + i);
											}
										}
									}
								}
							}
						}
						reversedMap = objTermMap.descendingMap();
						for (Map.Entry<String, String> entry : reversedMap.entrySet()) {
							docId = hits.scoreDocs[Integer.parseInt(entry.getValue())].doc;
							doc = searcher.doc(docId);
							try {
								voteCount = Integer.parseInt(doc.get("weightage")) % 10000000;
							} catch(Exception ex) {
								voteCount = 0;
							}
							if(maxVoteCount < voteCount)
								maxVoteCount = voteCount;
							if(brandTitles.indexOf(doc.get("titlevalue")) == -1) {
								if(voteCount > minimumVotes && allCount < 18 && (minimumVotes > -1 || (minimumVotes == -1 && doc.get("priority").equals("y")))) {
									strTMKey = doc.get("titlevalue") +"~" + doc.get("countryid") + "~" + doc.get("sectorid") + "~" + doc.get("sectortitlevalue")+ "~" + doc.get("sectorcaption")+ "~" + doc.get("sectorsvgimg")+ "~" + doc.get("sectornormalimg")+ "~" + doc.get("parentsectorid")+ "~" + doc.get("parentsectortitlevalue")+ "~" + doc.get("parentsectorcaption")+ "~" + doc.get("parentsectorsvgimg")+ "~" + doc.get("parentsectornormalimg")  + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("brandid")+ "~" + doc.get("weightage") + "~" + doc.get("priority") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X");
									objSortedTermMap.put("E~" + strTMKey, doc.get("titlevalue"));
									allCount++;
								} else {
									objTMAlphaSort.put(doc.get("titlevalue"), doc.get("countryid") + "~" + doc.get("parentsectorid") + "~" + doc.get("sectorid") + "~" + doc.get("brandid") + "~" + doc.get("dealerName") + "~" + doc.get("relatedbrands") + "~" + doc.get("sectortitlevalue") + "~" + doc.get("titlevalue") + "~" + doc.get("logo1X")+ "~" + doc.get("logo2X"));
								}
								brandTitles += doc.get("titlevalue") + ",";
							}
						}
					}
				}
			}
			indexReader.close();
			Iterator entries = objSortedTermMap.entrySet().iterator();
			StringTokenizer objSt;
			String countryId, parentSectorId, sectorId, brandid, sectorName, sectorImage, priority, cityName, relatedBrands;
			String imagePath = null, imagePath2x = null;
			HttpSession oSession = request.getSession(true);
			SHSurveyUser objSHUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
			String strVotedComp = "",     strUserLanguageNew= userLanguage;
			//if(objSHUser != null )
					//strVotedComp = objSHUser.getVotedCompany(request, countryId, objSHUser.getId());

			if(strUserLanguageNew.contains("/")){
				strUserLanguageNew = strUserLanguageNew.replace("/","");
			}
			BrandSearchList   objBrandList = new BrandSearchList();
			List<BrandSearch> brandList = new ArrayList<BrandSearch>();
			String dealerNames = "", dealerName = "";
			StringTokenizer objSt1;
			String strListAbr = "";
			String noOfVotes = "";
			
			String switchingCountryPossible = (String) request.getSession().getAttribute("switchingCountryPossible");
			if(switchingCountryPossible == null || switchingCountryPossible.length() == 0)
				switchingCountryPossible = "No";
				
			String addedBrands = "";
			String strTemp = "", strTemp2 = "";
			
			while (entries.hasNext()) {

				Map.Entry thisEntry = (Map.Entry) entries.next();
				Object key = thisEntry.getKey();
				Object value = thisEntry.getValue();
				objSt = new java.util.StringTokenizer((String)key, "~");
				strListAbr = objSt.nextToken();      //A, B, C
				objSt.nextToken();     //brandTitle
				countryId = objSt.nextToken();   //countryid
				sectorId = objSt.nextToken();    //sectorid

				sectorName = objSt.nextToken();        //sectortitlevalue
				objSt.nextToken();        //sectorcaption
				objSt.nextToken();        //sectorsvgimg
				sectorImage = objSt.nextToken();        //sectornormalimg

				parentSectorId = objSt.nextToken();     //parentsectorid
				objSt.nextToken();       //parentsectortitlevalue
				objSt.nextToken();       //parentsectorcaption
				objSt.nextToken();       //parentsectorsvgimg
				objSt.nextToken();       //parentsectornormalimg
				dealerNames = objSt.nextToken();       //dealerName
				relatedBrands = objSt.nextToken();       //relatedbrands

				brandid = objSt.nextToken();     //brandid
				noOfVotes = objSt.nextToken();     //weightage
				priority =  objSt.nextToken();     //priority

				imagePath = objSt.nextToken();   //logo1X

				if (objSt.hasMoreTokens())
					imagePath2x =  objSt.nextToken();  //logo2X
				

				objSt1 = new StringTokenizer(dealerNames, "^");
				while(objSt1.hasMoreTokens())
				{
					strTemp = objSt1.nextToken();
					if(addedBrands.indexOf(brandid + strTemp + ",")  == -1) {
						addedBrands += brandid + strTemp + ",";
						if(dealerName.indexOf("|") > -1 && !switchingCountryPossible.equals("No")) {
							if(dealerNames.indexOf("^") > -1) 
								dealerName = strTemp + "^";
							else 
								dealerName = strTemp;
						} else 
							dealerName = "111| |111|0";
							
						BrandSearch bs = new BrandSearch();
						bs.setBrandCode(brandid);
						bs.setBrandName(((String) value));
						bs.setBrandLogo1x(imagePath);
						bs.setBrandLogo2x(imagePath2x);
						bs.setSectorCode(sectorId);
						bs.setSectorName(sectorName);
						bs.setSectorImage(sectorImage);
						bs.setCountryCode(countryId);
						bs.setParentSectorCode(parentSectorId);
						bs.setDealerName(dealerName);
						bs.setRelatedBrands(relatedBrands);
						
						if(minimumVotes == -1) {
							count++;
							bs.setPreferedBrand(priority.toUpperCase());
						} else { 
							count++;
							if(count < 19)
								bs.setPreferedBrand("Y");
							else 
								bs.setPreferedBrand("M");
						}
						brandList.add(bs);
					}
				}

			}

			int ic = 0;
			if(count < 18) {
				Iterator objIt = objTMAlphaSort.keySet().iterator();
				while(objIt.hasNext() && count < 18) {
					strTemp = (String) objIt.next();
					strTemp2 = objTMAlphaSort.get(strTemp);
					objSt = new StringTokenizer(strTemp2, "~");
					dealerName = "111|xyz|111";

					BrandSearch bs = new BrandSearch();
					bs.setBrandName(strTemp);
					bs.setCountryCode(objSt.nextToken());
					bs.setParentSectorCode(objSt.nextToken());
					bs.setSectorCode(objSt.nextToken());
					bs.setBrandCode(objSt.nextToken());
					objSt.nextToken();
					bs.setDealerName(dealerName);
					bs.setRelatedBrands(objSt.nextToken());
					bs.setSectorName(objSt.nextToken());
					objSt.nextToken();
					bs.setBrandLogo1x(objSt.nextToken());
					bs.setBrandLogo2x(objSt.nextToken());
					bs.setSectorImage("");
					bs.setPreferedBrand("Y");
					count++;
					ic++;
					brandList.add(bs);
					//objTMAlphaSort.remove(strTemp);
				}
				
			}
			if(ic < objTMAlphaSort.size())
				request.getSession().setAttribute("alphaSortList", objTMAlphaSort);
			request.getSession().setAttribute("normalSearch", "true");
			//objBrandList.setBrandDtl(brandList);
			//LOG.error(" 7 " + searchQuery);
			return brandList;
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	public static void searchSite(HttpServletRequest request, String searchText, String sector){

	}
	
	public static HashMap searchBrands(HttpServletRequest request, String searchField, String searchText) {
		return searchBrands(request, searchField, searchText, "titlevalue");
	}
	
	public static HashMap searchBrands(HttpServletRequest request, String searchField, String searchText, String sorter) {
	    String userCountry = (String) request.getSession().getAttribute("userCountry");
		return searchBrands(request, searchField, searchText, sorter, userCountry);
	}

	public static HashMap searchBrands(HttpServletRequest request, String searchField, String searchText, String sorter, String userCountry) {
		return searchBrands(request, searchField, searchText, sorter, userCountry, false);
	}

	public static HashMap searchBrands(HttpServletRequest request, String searchField, String searchText, String sorter, String userCountry, boolean includeInactive) {
		String userLanguage = (String) request.getSession().getAttribute("userLanguage");
		return searchBrands(request, searchField, searchText, sorter, userCountry, userLanguage, false);
	}
	
	public static HashMap searchBrands(HttpServletRequest request, String searchField, String searchText, String sorter, String userCountry, String userLanguage, boolean includeInactive) {
		HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
		try {
			int docId; 
			Document doc;
		    Directory directory = null;
			IndexSearcher searcher = null;
			HashMap<String,String> objHMSort = new HashMap<String,String>();
			HashMap<String,String> objHMIdTitle = new HashMap<String,String>();
			HashMap<String,String> objHMIdShortTitle = new HashMap<String,String>();
			HashMap<String,String> objHMIdCountry = new HashMap<String,String>();
			HashMap<String,String> objHMIdLogo1X = new HashMap<String,String>();
			HashMap<String,String> objHMIdLogo2X = new HashMap<String,String>();
			HashMap<String,String> objHMIdLogoAltText = new HashMap<String,String>();
			HashMap<String,String> objHMIdShortcutName = new HashMap<String,String>();
			
			HashMap<String,String> objHMIdParentSectorId = new HashMap<String,String>();
			HashMap<String,String> objHMIdParentSectorTitle = new HashMap<String,String>();
			HashMap<String,String> objHMIdParentSectorImgSvg = new HashMap<String,String>();
			HashMap<String,String> objHMIdParentSectorImgPng = new HashMap<String,String>();
			HashMap<String,String> objHMIdSectorId = new HashMap<String,String>();
			HashMap<String,String> objHMIdSectorTitle = new HashMap<String,String>();
			HashMap<String,String> objHMIdSectorImgSvg = new HashMap<String,String>();
			HashMap<String,String> objHMIdSectorImgPng = new HashMap<String,String>();
			
			HashMap<String,String> objHMIdPriority = new HashMap<String,String>();
			HashMap<String,String> objHMIdDealers = new HashMap<String,String>();
			HashMap<String,String> objHMIdDealersCity = new HashMap<String,String>();
			HashMap<String,String> objHMIdRelatedBrands = new HashMap<String,String>();
			HashMap<String,String> objHMIdAwards = new HashMap<String,String>();
			
			HashMap<String,String> objHMIdVotes = new HashMap<String,String>();
			HashMap<String,String> objHMIdComments = new HashMap<String,String>();
			HashMap<String,String> objHMIdNominations = new HashMap<String,String>();
			HashMap<String,String> objHMIdWins = new HashMap<String,String>();
			HashMap<String,String> objHMIdTrends = new HashMap<String,String>();
			HashMap<String,String> objHMIdStarRating = new HashMap<String,String>();
			HashMap<String,String> objHMIdAvgBrand = new HashMap<String,String>();
			
			Analyzer temp = new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_41);
		    Analyzer analyzer = new ShingleAnalyzerWrapper(temp, 4);
		    //String userLanguage = (String) request.getSession().getAttribute("userLanguage");

		    final File dirFile = new File(request.getServletContext().getRealPath("/") + File.separator + BrandLuceneIndexer.DEFAULT_WEB_ROOT + File.separator + BrandLuceneIndexer.DEFAULT_LUCENE_ROOT + File.separator + userCountry + "-" + userLanguage);
		    directory = FSDirectory.open(dirFile);
		    DirectoryReader dRdr = DirectoryReader.open(directory);
		    searcher = new IndexSearcher(dRdr);
		    int resultsCount = 0;
		    try {
			    if(sorter.indexOf("~") > -1) {
			    	resultsCount = Integer.parseInt(sorter.substring(sorter.indexOf("~") + 1));
			    	sorter = sorter.substring(0, sorter.indexOf("~"));
			    } else 
			    	resultsCount = 500;
		    } catch(Exception ex) {
		    	resultsCount = 500;
		    }

		    Query query;
		    QueryParser qp;
		    if(searchText.indexOf(",") == -1) { 
			    qp = new QueryParser(Version.LUCENE_41, searchField, analyzer);
				query = qp.parse(searchText);
		    } else {
		    	int offset = 0;
	    		StringTokenizer objSt = new StringTokenizer(searchText, ",");
	    		int count = objSt.countTokens();
	    		if(searchField.equals(BrandLuceneIndexer.INDEXER_SERACH_FIELD_SHORT_NAME))
	    			offset = 1;
	    		String[] fieldsValue = new String[count + offset];
	    		String[] fieldsName = new String[count + offset];
	    		BooleanClause.Occur[] flags = new BooleanClause.Occur[count + offset];
	    		for(int i = 0; i < count; i++) {
	    			fieldsValue[i] = objSt.nextToken();
	    			fieldsName[i] = searchField;
	    			flags[i] = BooleanClause.Occur.SHOULD;
	    		} 
	    		if(searchField.equals(BrandLuceneIndexer.INDEXER_SERACH_FIELD_SHORT_NAME)) {
	    			fieldsValue[count] = (String) request.getSession().getAttribute("surveySector");
	    			fieldsName[count] = BrandLuceneIndexer.INDEXER_SERACH_FIELD_SECTOR;
	    			flags[count] = BooleanClause.Occur.MUST;
	    		}
	    		query = MultiFieldQueryParser.parse(Version.LUCENE_41, fieldsValue, fieldsName, flags, analyzer);
		    }
		    //Sort sort = new Sort(new SortField[] { SortField.FIELD_SCORE, new SortField("titlevalue", SortField.Type.STRING), new SortField(sorter, SortField.Type.STRING)});
		    
		    int tempSort = 0;
			String strTemp1 = "";
			strTemp1 = (String)request.getSession().getAttribute("paginationSort");
			if(strTemp1 == null || strTemp1.length() == 0)
				strTemp1 = "asc";
    		if(searchField.equals(BrandLuceneIndexer.INDEXER_SERACH_FIELD_SHORT_NAME))
				strTemp1 = "desc";
			
		    TopDocs hits = searcher.search(query, 500);
		    for (int i = 0; i < hits.scoreDocs.length; i++) {
				if(i < resultsCount) {
					docId = hits.scoreDocs[i].doc;
					doc = searcher.doc(docId);
					if(doc.get("active").toLowerCase().equals("y") || includeInactive) {	
						if(strTemp1.indexOf("asc") > -1)
							tempSort = java.lang.Math.abs((int)doc.get("titlevalue").toUpperCase().charAt(0));
						else
							tempSort = java.lang.Math.abs(101 - (int)doc.get("titlevalue").toUpperCase().charAt(0));
							
						if(sorter.equals("starrating")) {
							//strTemp1 = ((Float.parseFloat(doc.get(sorter)) + 5) * 1000) + "" + tempSort + "" + doc.get("votes") + "" + doc.get("titlevalue");
							strTemp1 = ((Float.parseFloat(doc.get(sorter)) + 5) * 1000) + "" + tempSort + "" + doc.get("titlevalue");
							objHMSort.put(strTemp1, doc.get("brandid"));
						} else  {
							//objHMSort.put(doc.get(sorter) + "" + tempSort + "" + doc.get("votes") + "" + doc.get("titlevalue"),  doc.get("brandid"));
							objHMSort.put(doc.get(sorter) + "" + tempSort + "" + doc.get("titlevalue"),  doc.get("brandid"));
						}
						LOG.debug(sorter + " : " + doc.get(sorter) + "" + doc.get("titlevalue"));
						objHMIdTitle.put(doc.get("brandid"), doc.get("titlevalue"));
						objHMIdShortTitle.put(doc.get("brandid"), doc.get("trimtitlevalue"));
						objHMIdCountry.put(doc.get("brandid"), doc.get("countryid"));
						objHMIdLogo1X.put(doc.get("brandid"), doc.get("logo1X"));
						objHMIdLogo2X.put(doc.get("brandid"), doc.get("logo2X"));
						objHMIdLogoAltText.put(doc.get("brandid"), doc.get("imgAltText"));
						objHMIdShortcutName.put(doc.get("brandid"), doc.get("shortcutName"));
		
						objHMIdParentSectorId.put(doc.get("brandid"), doc.get("parentsectorid"));
						objHMIdParentSectorTitle.put(doc.get("brandid"), doc.get("parentsectortitlevalue"));
						objHMIdParentSectorImgSvg.put(doc.get("brandid"), doc.get("parentsectorsvgimg"));
						objHMIdParentSectorImgPng.put(doc.get("brandid"), doc.get("parentsectornormalimg"));
						objHMIdSectorId.put(doc.get("brandid"), doc.get("sectorid"));
						objHMIdSectorTitle.put(doc.get("brandid"), doc.get("sectortitlevalue"));
						objHMIdSectorImgSvg.put(doc.get("brandid"), doc.get("sectorsvgimg"));
						objHMIdSectorImgPng.put(doc.get("brandid"), doc.get("sectornormalimg"));
		
						objHMIdPriority.put(doc.get("brandid"), doc.get("priority"));
						objHMIdDealers.put(doc.get("brandid"), doc.get("dealerName"));
						objHMIdDealersCity.put(doc.get("brandid"), doc.get("dealercitymap"));
						objHMIdRelatedBrands.put(doc.get("brandid"), doc.get("relatedbrands"));
						objHMIdAwards.put(doc.get("brandid"), doc.get("awards"));
						
						objHMIdVotes.put(doc.get("brandid"), doc.get("votes"));
						objHMIdComments.put(doc.get("brandid"), doc.get("commentscount"));
						objHMIdNominations.put(doc.get("brandid"), doc.get("nominationscount"));
						objHMIdWins.put(doc.get("brandid"), doc.get("winscount"));
						objHMIdTrends.put(doc.get("brandid"), doc.get("trends"));
						objHMIdStarRating.put(doc.get("brandid"), doc.get("starrating"));
						objHMIdAvgBrand.put(doc.get("brandid"), doc.get("avgbrand"));
					}
				} else 
					break;
			}
			
			objHM.put("sorter", objHMSort);
			objHM.put("title", objHMIdTitle);
			objHM.put("shortTitle", objHMIdShortTitle);
			objHM.put("countryCode", objHMIdCountry);
			objHM.put("logo1X", objHMIdLogo1X);
			objHM.put("logo2X", objHMIdLogo2X);
			objHM.put("logoAltText", objHMIdLogoAltText);
			objHM.put("shortcutName", objHMIdShortcutName);

			objHM.put("parentSectorId", objHMIdParentSectorId);
			objHM.put("parentSectorTitle", objHMIdParentSectorTitle);
			objHM.put("parentSectorImgSvg", objHMIdParentSectorImgSvg);
			objHM.put("parentSectorImgPng", objHMIdParentSectorImgPng);
			objHM.put("sectorId", objHMIdSectorId);
			objHM.put("sectorTitle", objHMIdSectorTitle);
			objHM.put("sectorImgSvg", objHMIdSectorImgSvg);
			objHM.put("sectorImgPng", objHMIdSectorImgPng);

			objHM.put("priority", objHMIdPriority);
			objHM.put("dealers", objHMIdDealers);
			objHM.put("dealersCity", objHMIdDealersCity);
			objHM.put("relatedBrands", objHMIdRelatedBrands);
			objHM.put("awards", objHMIdAwards);
			
			objHM.put("votes", objHMIdVotes);
			objHM.put("commentsCount", objHMIdComments);
			objHM.put("nominationsCount", objHMIdNominations);
			objHM.put("winsCount", objHMIdWins);
			objHM.put("trends", objHMIdTrends);
			objHM.put("starRating", objHMIdStarRating);
			objHM.put("avgBrand", objHMIdAvgBrand);
			
			request.getSession().setAttribute("normalSearch", "false");

		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return objHM;
	}

	/* generic function to get the titles for a given code */
	/*HttpServletRequest request, String searchField, String searchText, String sorter, String userCountry,*/
	public static String getTitleBrands(HttpServletRequest request, String searchCode, String srchField, String countrycode){
		String strTitle = "", strTempValue;
		HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
		HashMap<String,String> objHMSort = new HashMap<String,String>();
        HashMap<String,String> objHMIdTitle = new HashMap<String,String>();
		HashMap<String,String> objHMIdSectorTitle = new HashMap<String,String>();
		HashMap<String,String> objHMIdDealers = new HashMap<String,String>();
		objHM = searchBrands(request, srchField, searchCode, "titlevalue", countrycode);
		objHMSort = (HashMap)objHM.get("sorter");
		objHMIdTitle = (HashMap) objHM.get("title");
		objHMIdSectorTitle = (HashMap)objHM.get("sectorTitle");
		objHMIdDealers = (HashMap) objHM.get("dealers");


		List<String> sorterList = new ArrayList<String>(objHMSort.keySet());
		if (srchField.equals(BrandLuceneIndexer.INDEXER_SERACH_FIELD_SECTOR)) {
			Map.Entry<String,String> entry = objHMIdSectorTitle.entrySet().iterator().next()  ;
			strTitle = entry.getValue();
		}else if(srchField.equals(BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE)) {
			strTitle = objHMIdTitle.get(searchCode);
		}

		return strTitle;
	}
	
}
