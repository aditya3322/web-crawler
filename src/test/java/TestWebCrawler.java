import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.web.crawler.response.vo.ResponseVo;
import com.web.crawler.service.*;

public class TestWebCrawler {
	
	private AsyncWebCrawler crawler;
	private String url = "http://www.prudential.co.uk/";
	private String domain = "prudential";
	
	@Before
	public void init() {
		crawler = AsyncWebCrawler.instance();
	}

	@Test
	public void test_crawled_count_for_depth_one() {
		crawler.crawl(url, 1, 10, domain);
		ResponseVo responseVo = crawl();
		assertNotNull("crawler is in invalid state", responseVo);
		assertNotEquals("test_crawled_count_for_depth_one", 0, responseVo.getCount());
	}
	
	@Test
	public void test_crawled_list_non_empty_for_depth_one() {
		crawler.crawl(url, 1, 10, domain);
		ResponseVo responseVo = crawl();
		assertNotNull("crawler is in invalid state", responseVo);
		assertTrue("test_crawled_list_non_empty_for_depth_one", !responseVo.getUrls().isEmpty());
	}
	
	@Test
	public void test_crawled_list_non_empty_for_depth_two() {
		crawler.crawl(url, 2, 10, domain);
		ResponseVo responseVo = crawl();
		assertNotNull("crawler is in invalid state", responseVo);
		assertTrue("test_crawled_list_non_empty_for_depth_two", !responseVo.getUrls().isEmpty());
	}
	
	@Test
	public void test_crawled_list_count_for_depth_two() {
		crawler.crawl(url, 2, 10, domain);
		ResponseVo responseVo = crawl();
		assertNotNull("crawler is in invalid state", responseVo);
		assertTrue("test_crawled_list_count_for_depth_two", !responseVo.getUrls().isEmpty());
	}
	
	@Test
	public void test_crawled_list_count_for_depth_two_and_breath_twenty() {
		crawler.crawl(url, 2, 20, domain);
		ResponseVo responseVo = crawl();
		System.out.println(responseVo);
		assertNotNull("crawler is in invalid state", responseVo);
		assertTrue("test_crawled_list_count_for_depth_two_and_breath_twenty", !responseVo.getUrls().isEmpty());
	}
	
	private ResponseVo crawl() {
		ResponseVo responseVo = null;
		try {
			responseVo = crawler.respone(domain);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return responseVo;
	}

}
