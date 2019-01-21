# Web Crawler

## Prerequistes

* Java 8
* Maven 

## Library Used
* Jsoup-1.11.3
* Gson-2.1.1

## Overview 

Web crawler provide an async web crawling over a given URL. There are below input parameter required:
* URL : URL on which web crawling going to done
* domain : As page can have multiple other source urls so just serach to the given domain
* depth : number of recursion with url
* breath :  limit on number of Urls on the page

## How to build & test

* mvn clean compile build : used to build apis
* mvn test : used to run Juint testcases over the AyncWebCrawler


## Code Walkthrough

  * AsyncWebCrawler is the singleton class responsible to crawl 
  ```
  public static AyncWebCrawler instance(String domain){
		if(instance == null) {
			synchronized (AyncWebCrawler.class) {
				instance = new AyncWebCrawler(domain);
			}
		}
		return instance;
	}
  ```
  * crawl method is a recursive function which uses Completable Future and common fork join pool 
  ```
  supplyAsync(content(startingUrl))
				.thenApply(fetchUrls(domain))
				.thenApply(doForEach(depth))
				.thenApply(futures -> futures.toArray(CompletableFuture[]::new))
				.thenAccept(CompletableFuture::allOf).join();
   ```
  * it uses visited queue to track url which are visited on that depth
  * fetch url uses Jsoup library to get content and urls from the page
  ```
  private Function<Document, Set<String>> fetchUrls(String domain) {
		return doc -> {
			return doc != null ? doc.select("a[href]").stream()
					.map(link -> link.attr("abs:href"))
					.filter(url -> url != null && url.contains(domain))
					.peek(System.out::println)
					.collect(toSet()) : new HashSet<>();
		};
	}
  ```
  * code snippet to crawl and get response for the given url 
  ```
  ResponseVo responseVo = crawler.respone(domain);
  ```
  * ResponseVo :
  ```
  public class ResponseVo {
	
	private String domain;
	private int count;
	private List<String> urls;
}
```
 
  
      
