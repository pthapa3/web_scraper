""" Web Scraper
  	python version 2.7
	Author: Prakash Thapa
	This module scrapes event name, artist(s), event city and venue, and ticket prices from the website http://www.wegotickets.co.uk/.
	Writes the ouptut to csv file 
"""


import requests
from bs4 import BeautifulSoup
import time
from time import sleep
import random
import pandas as pd



# scrape data from each event link and return the scraped data in tuple
def scrape_data(event_url):
	evnt_url_resp = requests.get(event_url, headers=headers)
	event_page = BeautifulSoup(evnt_url_resp.content, "lxml")

	event_name = event_page.find('div', {'class': 'left full-width-mobile event-information event-width'})
	event_city_venue = event_page.find('div', {'class': 'venue-details'}).h2
	event_date = event_page.find('div', {'class': 'venue-details'}).h4

	# Ticket information and price
	ticket_info = event_page.find_all('div',  {'class': 'BuyBox block'})
	ticket_type_price = ''
	for indx in range(len(ticket_info)):
		ticket_type = ticket_info[indx].h2.text
		price =  ticket_info[indx].find_all('td', {'class': 'half text-top text-right' })
		ticket_price = price[0].p.text
		ticket_type_price += ticket_type + ': '  + ticket_price + '\n'

	print  'Event name and artists: ', event_name.h1.text, ' ', event_name.h4.text
	event_name_artists = event_name.h1.text + ' ' + event_name.h4.text
	event_city = event_city_venue.text.split(':')[0]
	print 'Event city: ' , event_city
	event_venue = event_city_venue.text.split(':')[1]
	event_venue = event_venue.lstrip(' ')
	print 'Event venue: ', event_venue
	print 'Event date: ', event_date.text
	print 'Ticket type and price: ', ticket_type_price
	
	return event_name_artists, event_city, event_venue, event_date.text, ticket_type_price


# Get all event links from a current page
def parse_event_links(curr_page_event_links):
	links_list = []
	
	for indx in range(len(curr_page_event_links)):
		 links_list.append(curr_page_event_links[indx]['href'])
	
	return links_list

# Spoof User Agent
headers = requests.utils.default_headers()
headers.update({'User-Agent': 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/52.0'})


# Send get requests to the website and parse html content
url = "https://www.wegottickets.com/searchresults/all"
url_resp = requests.get(url, headers=headers)
url_resp_content = BeautifulSoup (url_resp.content,"lxml")

# Get total number of pages
num_pages = url_resp_content.findAll('a', {'class' : 'pagination_link'})
last_page = num_pages[len(num_pages)-1]
total_page = int(last_page.text)


# Navigate through each page, scrape and append returned data in a list
# Monitor time it takes to scrape
data = []
scrape_start_time = time.time()

for page in range(total_page - 1):
	page_num = str(page+1)
	print 'Scraping data from page number', page_num
	url_page = "https://www.wegottickets.com/searchresults/page/"+page_num+"/all#paginate"
	req_page = requests.get(url_page, headers=headers)
	req_page_content = BeautifulSoup (req_page.content,"lxml")

	curr_page_event_links = req_page_content.findAll("a", {'class': "event_link"})
	events = parse_event_links(curr_page_event_links)

	# sleep on each loop so that not to overwhelm the server
	# scrape data from each event links
	for evs in events:
		sleep(random.randint(3,5))
		data.append(scrape_data(evs))


print '\nTime taken to scrape: ', time.time() - scrape_start_time

# output scraped data to csv file
df = pd.DataFrame(data, columns =['Event_Name and Artists', 'City', 'Venue', 'Event_Date' , 'Ticket_Information'])
print df.head()
df.to_csv('scraped_data.csv', index=False, encoding='utf-8')




 