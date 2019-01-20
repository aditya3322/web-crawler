package com.prudential.assignment.crawlers;


import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.stream.Collectors.toSet;
import static org.jsoup.Jsoup.connect;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.*;

import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.prudential.assignment.constants.Status;
import com.prudential.assignment.processors.ResponseVo;
public class AyncWebCrawler {
	private final ConcurrentLinkedQueue<String> visited = new ConcurrentLinkedQueue<>();
	private static Status status = Status.IDLE;
	private static AyncWebCrawler instance;
	private String domain;
	private AyncWebCrawler(String domain) {
		this.domain = domain;
		commonPool().awaitQuiescence(10, TimeUnit.SECONDS);
	}
	
	public static AyncWebCrawler instance(String domain){
		if(instance == null) {
			synchronized (AyncWebCrawler.class) {
				instance = new AyncWebCrawler(domain);
			}
		}
		return instance;
	}

	public void crawl(final String startingUrl, final int depth) {
		status = Status.CRAWLING;
		if (isMaximumDepthReached(depth)) { // Prevent too deep recursion.
			status =  Status.COMPLETED;
			return;
		} else if (isAlreadyVisited(startingUrl)) { // Prevent cycles; not
			return;
		}
		visited.add(startingUrl);
		System.out.println(visited);
		supplyAsync(content(startingUrl))
				.thenApply(fetchUrls(domain))
				.thenApply(doForEach(depth))
				.thenApply(futures -> futures.toArray(CompletableFuture[]::new))
				.thenAccept(CompletableFuture::allOf).join();
	}
	
	public boolean isIdle() {
		return (status == Status.IDLE || status == Status.COMPLETED) ? true : false;
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
				System.err.println("Something went wrong when fetching the contents of the URL: " + url);
			}
			return null;
		};
	}

	private Function<Document, Set<String>> fetchUrls(String domain) {
		return doc -> {
			return doc != null ? doc.select("a[href]").stream()
					.map(link -> link.attr("abs:href"))
					.filter(url -> url != null && url.contains(domain))
					.peek(System.out::println)
					.collect(toSet()) : new HashSet<>();
		};
	}

	private Function<Set<String>, Stream<CompletableFuture<Void>>> doForEach(final int depth) {
		return urls -> urls.stream().map(url -> {
			crawl(url, depth - 1);
			return completedFuture(null);
		});
	}
	
	public String respone() {
		
		if(status == Status.CRAWLING) 
			return "Crawling is not completed";
		ResponseVo reponse= new ResponseVo(domain,  visited.stream().collect(Collectors.toList()));
		GsonBuilder builder = new GsonBuilder();
		return builder.create().toJson(reponse);
	}
}
