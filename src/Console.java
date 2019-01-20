import com.web.crawlers.AyncWebCrawler;


public class Console {
	
	public static void main(String[] args) {
		if(args.length != 3) {
			System.err.println("Usage: java -jar webcrawler-utility url domain depth");
            System.exit(-1);
		}
		
		String url = args[0];
		String domain = args[1];
		int depth = Integer.parseInt(args[2]);
 		//start crawling 
		AyncWebCrawler crawler = AyncWebCrawler.instance(domain);
		crawler.crawl(url, depth);
		// Reponse in json form
		System.out.println("-----------------------------------------------------------------------");
		System.out.println(crawler.respone());
		System.out.println("-----------------------------------------------------------------------");
	}

}
