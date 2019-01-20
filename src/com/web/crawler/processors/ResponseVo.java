package com.web.crawler.processors;

import java.util.List;

public class ResponseVo {
	
	private Domain domain;
	private List<String> urls;
	
	public ResponseVo(String domain, List<String> urls) {
		this.domain = new Domain(domain);
		this.urls =  urls;
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	class Domain {
		private String domain;

		public Domain(String domain) {
			this.domain = domain;
		}
		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}
		
		
	}
}
