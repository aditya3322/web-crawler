package com.web.crawler.response.vo;

import java.util.List;

public class ResponseVo {
	
	private String domain;
	private int count;
	private List<String> urls;
	
	public ResponseVo(String domain, List<String> urls) {
		this.domain = domain;
		this.urls =  urls;
		this.setCount(urls.size());
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	
}
