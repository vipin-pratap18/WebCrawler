package com.crawler.service;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Defines the starting of crawler service and creates thread and manage inter thread communication 
 * @author VipinK
 *
 */
public class Crawler {

	
	/* 
	 * Starting the crawling service by creating two thread, 
	 * one for crawler service and one for managing the shutdown flag
	 */
	public void startCrawling(String parentUrl){
		
		/* Validating the parentUrl */
		if(validateUrl(parentUrl)){

			/* Creates a CrawlerService final object and initialize it with parent URL and starts the crawling inside a thread */
			final CrawlerService crawlService = new CrawlerService(parentUrl);

			Thread service = new Thread(new Runnable(){

				@Override
				public void run() {
					crawlService.crawlPage();
				}

			});
			service.start();


			/* Listens for a keyboard input (Enter key in below case) to stop the crawling thread by changing the shutDown flag */
			Thread keyPress = new Thread(new Runnable(){

				@Override
				public void run() {
					Scanner scanner = new Scanner(System.in);
					System.out.println("Press Enter to exit from the Crawling Service : ");
					scanner.nextLine();
					crawlService.shutDown();
				}

			});
			keyPress.start();

			try {
				service.join();
				keyPress.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else
			return;
	}


	/* 
	 * Validating parent url by matching it through a regex, 
	 * it will allow only those urls which starts with http or https 
	 */
	private boolean validateUrl(String url){
		/* Regex for validating url, which contains http ot https */
		String regex = "([http][\'\"]?[^\'\" >]+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);
		return matcher.matches();
	}

}
