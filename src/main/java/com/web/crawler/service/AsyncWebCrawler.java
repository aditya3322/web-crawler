package com.web.crawler.service;


import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.stream.Collectors.toSet;
import static org.jsoup.Jsoup.connect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.nodes.Document;

import com.web.crawler.constants.Status;
import com.web.crawler.response.vo.ResponseVo;
public class AsyncWebCrawler {
	private final ConcurrentLinkedQueue<String> visited = new ConcurrentLinkedQueue<>();
	private static Status status = Status.IDLE;
	private static AsyncWebCrawler instance;
	private AsyncWebCrawler() {
		commonPool().awaitQuiescence(1000, TimeUnit.MINUTES);
	}
	
	public static AsyncWebCrawler instance(){
		if(instance == null) {
			synchronized (AsyncWebCrawler.class) {
				instance = new AsyncWebCrawler();
			}
		}
		return instance;
	}

	public void crawl(final String startingUrl, final int depth, final int breath, String domain) {
		status = Status.CRAWLING;
		if (isMaximumDepthReached(depth)) { // Prevent too deep recursion.
			status =  Status.COMPLETED;
			return;
		} else if (isAlreadyVisited(startingUrl)) { // Prevent cycles; not
			return;
		}
		visited.add(startingUrl);
		supplyAsync(content(startingUrl))
				.thenApply(fetchUrls(domain, breath))
				.thenApply(doForEach(depth, breath, domain))
				.thenApply(futures -> futures.toArray(CompletableFuture[]::new))
				.thenAccept(CompletableFuture::allOf).join();
	}
	
	public boolean isCompleted() {
		return (status == Status.COMPLETED) ? true : false;
	}
	
	private boolean isMaximumDepthReached(final int depth) {
		return depth <= 0;
	    }

	private boolean isAlreadyVisited(final String url) {
		return visited.contains(url);
	}

	private Supplier<Document> content(final String url) {
		return () -> {
			try {
				return connect(url).get();
			} catch (IOException e) {
				System.err.println("Something went wrong when fetching the contents of the URL: " + url +  " :: " +e.getMessage());
				visited.remove(url);
			}
			return null;
		};
	}

	private Function<Document, Set<String>> fetchUrls(String domain, int breath) {
		return doc -> {
			return doc != null ? doc.select("a[href]").stream()
					.map(link -> link.attr("abs:href"))
					.filter(url -> url != null && url.contains(domain))
					.filter(url -> !(url.contains(".pdf") || url.contains("video") || url.contains("mail")))
					.limit(breath)
					//.peek(System.out::println)
					.collect(toSet()) : new HashSet<>();
		};
	}

	private Function<Set<String>, Stream<CompletableFuture<Void>>> doForEach(final int depth, final int breath, final String domain) {
		return urls -> urls.stream().map(url -> {
			crawl(url, depth - 1, breath, domain);
			return completedFuture(null);
		});
	}
	
	private void reset() {
		visited.clear();
		status = Status.IDLE;
	}
	public ResponseVo respone(String domain) throws Exception {
		if(status == Status.CRAWLING) {
			reset();
			commonPool().shutdown();
			throw new Exception("crawler is in invalid state");
		}
		try{
			return new ResponseVo(domain,  visited.stream().collect(Collectors.toList()));
		} finally {
			reset();
		}
	}
}
