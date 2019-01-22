from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
import requests
from bs4 import BeautifulSoup
import os
import time

# chrome_options11 = Options()  
# chrome_options11.add_argument("--headless")

browser = webdriver.Chrome(executable_path=os.path.abspath("chromedriver")) # chrome_options = chrome_options11)
browser.implicitly_wait(30)
browser.get('https://www.wegottickets.com/searchresults/all')


# Get total number of pages
url_content = BeautifulSoup (browser.page_source,"lxml-xml")
num_pages = url_content.findAll('a', {'class' : 'pagination_link'})
last_page = num_pages[len(num_pages)-1]
total_page = int(last_page.text)



start_time = time.time()
for indx in range(3):

	curr_page_event_links = browser.find_elements_by_xpath('.//*[@class="event_link"]')
	curr_page_total_events = len(curr_page_event_links)
	print 'Ready to CLICK @@@@@@@@@@@$$$$$$$$$$$$$$$'
	
	for i in range(3):

		events = browser.find_elements_by_xpath('.//*[@class="event_link"]')[i]
		events.send_keys('\n')
		
		event_page = BeautifulSoup(browser.page_source,"lxml-xml")
		event_name = event_page.find('div', {'class': 'left full-width-mobile event-information event-width'})
		event_city_venue = event_page.find('div', {'class': 'venue-details'}).h2
		event_date = event_page.find('div', {'class': 'venue-details'}).h4

		# Ticket information and price
		ticket_info = event_page.find_all('div',  {'class': 'BuyBox block'})
		ticket_type_price = ''
		for idx in range(len(ticket_info)):
			ticket_type = ticket_info[idx].h2.text
			price =  ticket_info[idx].find_all('td', {'class': 'half text-top text-right' })
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

		browser.back() 
		
	indx = indx + 2
	indx_str = str(indx)
	print 'Next Page #################', indx_str

	browser.get('https://www.wegottickets.com/searchresults/page/' +indx_str+'/all#paginate')



print "Time Elapsed: ", time.time() - start_time





