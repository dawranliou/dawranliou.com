---
title: Twitter scraper tutorial with Python: Requests, BeautifulSoup, and Selenium - Part 2
slug: twitter-scraper-2
tags: [python]
---

This is the part 2 of my Twitter scraper tutorial. If you haven’t checkout part 
1, the link is right here. In the last part, I left the tutorial with an unsolved
problem — how to scrape the web page that uses infinite scrolling design? Two 
solutions came into my mind: one more sophisticated, the other more naive:

<!-- more -->

# The more sophisticated approach

In this approach, you need to understand how the infinite scrolling works. In 
the case of this tutorial, when the tweet search page loaded, none of the tweets
contents were loaded at the time. It is the frontend javascript code that sends 
the HTTP request to get tweets from the server asynchronously, and manipulates 
the HTML file to render those tweets. This technology is called AJAX. You may 
find more information online if you are interested.

Anyways, do you spot the flaw in the approach of part 1? The original HTTP 
request wasn’t the right request to get the tweets! It merely loads a page with 
right the javascript code, which load the tweets on the fly. So listen carefully:
If you are able to track down the right HTTP request object, which gets the 
tweets result, the only thing left is to replace the original request with this 
one. Voila! This is my so-called more sophisticated approach. However, I didn’t 
go with this approach simply I didn’t spend the time to :P

# The more naive approach

Okay! This is the approach I want to show you. Think about the problem this way:
no matter how sophisticated the website is designed, the end result is still a 
list of tweets loaded on your browser. So my so-called more naive approach is to
focus on the end result only. if we could manipulate the browser to load those 
tweets for me, just as what we see normally, we could use the same the knowledge
to parse the HTML file and get the tweets. To automate the browser for us, I’ll 
show you how I used Selenium.

“Selenium automates browsers.” That’s what the official website says. Selenium 
Python bindings will help us to use Selenium using Python. Follow the 
installation page to install it. The code below tells Selenium to use Chrome to 
open up the Twitter search page and then move down the page for 5 times. Since 
the browser object provides the handy API to locate the tweets, we don’t need to
use the BeautifulSoup again to parse the HTML file. You may run the script now:

```python
import time

from selenium import webdriver
from selenium.webdriver.common.keys import Keys

browser = webdriver.Chrome()
base_url = u'https://twitter.com/search?q='
query = u'%40dawranliou'
url = base_url + query

browser.get(url)
time.sleep(1)

body = browser.find_element_by_tag_name('body')

for _ in range(5):
    body.send_keys(Keys.PAGE_DOWN)
    time.sleep(0.2)

tweets = browser.find_elements_by_class_name('tweet-text')

for tweet in tweets:
    print(tweet.text)
```

If you see the following error:

`selenium.common.exceptions.WebDriverException: Message: 'chromedriver'
executable needs to be in PATH. Please see
https://sites.google.com/a/chromium.org/chromedriver/home`

Don’t panic. Read the error message (don’t just google it blindly) and what’s 
wrong is that you are missing the ‘chromedriver’ executable file. It also 
suggest you to go to the website. How nice it is! Download the executable from 
the website given and put it under one of your PATH loacation. For me, I put it 
under my /usr/local/bin/ folder. You should be fine to run the script by now.

This is the end of this tutorial! Hope you enjoy working with these amazing PyPI
packages. Feel free to comment or contact me if you want to learn more. Happy 
learning! Cheers!
