# Web Crawler

## Prerequistes

* Java 8

## Library Used
* Jsoup-1.11.3
* Gson-2.1.1

## Overview 

Web crawler utility provide an async web crawling over a given URL. There are below input parameter required:
* URL : URL on which web crawling going to done
* domain : As page can have multiple other source urls so just serach to the given domain
* depth : number of recursion with url

## How to run utility

* command : java -jar webcrawler-utility url domian depth
* Reponse : JSON response 
'''
{
  "domain":"domain":"prudential.co.uk",
  "urls":
          [ "http://www.prudential.co.uk/",
            "https://www.prudential.co.uk/contacts/investor-relations",
            "https://www.prudential.co.uk/about-us/our-history/prudence-the-face-of-our-business",
            "https://www.prudential.co.uk/insights/spending-habits-in-uk-retirement"
          ]
}
'''

      
