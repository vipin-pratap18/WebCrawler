package com.crawler.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class starts the page crawling, stores all the extracted URLs 
 * And maintains a repository for URL links to visit and already visited links
 * @author VipinK
 *
 */
public class CrawlerService {

	/* Declare the limit up to which crawler would crawl the pages by maintaining this limit on visited link repository */
	private final int limitToCrawl = 10;
	
	/* Stores the Set of links which are already visited, no duplicates allowed here */
	private Set<String> linksVisited = new HashSet<>();
	
	/* Stores the Queue of links which are extracted and parsed from web page */
	private Queue<String> linksToVisit = new ConcurrentLinkedQueue<String>();
	
	/* ShutDown flag, true for shut downing the crawling service */
	private volatile boolean shutDown = false;
	
	/* The parent URL from where crawling will starts and will be initialized by constructor or setter */
	private String parentUrl;
	
	/* The regex pattern to extract only urls from <a> tag and containing only http or https */
	private String regex = "<a\\s[^>]*href=\"([http][\'\"]?[^\'\" >]+)\"";
	
	/* Compiling the regex pattern */
	private Pattern pattern = Pattern.compile(regex);
	

	/* 
	 * Constructor to initialize 
	 * the parent URL 
	 */
	public CrawlerService(String parentUrl){
		this.parentUrl = parentUrl;
	}
	
	
	/* 
	 * Crawling will start from here by first crawling the parent URL 
	 * and adding all the extracted URLs from that page into the links to visit repository and then crawl every link from that repository
	 * till the shutdown flag become true or the limit of visited page exceeded 
	 */
	public void crawlPage(){
		try{
			if(parentUrl == null)
				return;
			if(linksToVisit.size() == 0)
			{
				String webData = crawl(parentUrl);
				extractUrls(webData);
				linksVisited.add(parentUrl);
			}

			for(String link : linksToVisit){
				if(!shutDown){
					if(linksVisited.contains(link)){
						System.out.println("This link : \"" + link + "\", is already crawled, so skiping it");
						continue;
					}

					String webData = crawl(link);
					extractUrls(webData);

					if(linksVisited.size() <= limitToCrawl){
						linksVisited.add(link);
						linksToVisit.remove(link);
					}else{
						System.out.println("Visited link limit is: "+limitToCrawl+", which is exceeded, so Stoping crawling service");
						System.exit(0);
					}
				}else{
					System.out.println("Shutdown flag is enabled by pressing Enter key, so Stoping crawling service");
					System.exit(0);
				}

			}

		}catch(Exception ex){
			ex.printStackTrace();
		}

	}

	
	/* 
	 * Extracting links containing only <a> tag and http or https from website text data, no resource link will be extracted
	 * and adding all the extracted URLs into the links to visit repository
	 */
	private void extractUrls(String webData){
		
		Matcher matcher = pattern.matcher(webData);

		while(matcher.find()) {
			System.out.println("URL Link found: " + matcher.group(1));
			linksToVisit.add(matcher.group(1));
		}
		/* Code to save the crawled urls from a link can be saved in database, and you can put the DAO layer call here. */
		System.out.println("URL Queue Size : " + linksToVisit.size());
	}

	
	/* 
	 * Crawling the web page and reading the text data with all the links containing inside the text
	 */
	private String crawl(String url){
		System.out.println("Started Crawling : " + url);
		StringBuffer webData = new StringBuffer();
		try{
			URL oracle = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null){
				webData.append(inputLine);
			}
			in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return webData.toString();
	}

	/* 
	 * Setting the shutDown flag true
	 */
	public void shutDown(){
		shutDown = true;
	}

}
