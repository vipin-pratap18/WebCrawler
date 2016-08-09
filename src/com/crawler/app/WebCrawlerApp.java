package com.crawler.app;

import com.crawler.service.Crawler;

/**
 * This class starts the application and crawl service by 
 * passing in the first URL to begin with
 * @author VipinK
 *
 */
public class WebCrawlerApp {

	public static void main(String[] args) {

		/* Receiving the parent url to start crawling from command line arguments */
		String url = null;
		if(args != null && args.length == 1){
			url = args[0];
			//String url = "https://www.google.co.in/";
			/* Starting the crawling services */
			new Crawler().startCrawling(url);
		}else{
			System.out.println("WebCrawlerApp require at least one parameter to start the crawling");
		}

	}

}
