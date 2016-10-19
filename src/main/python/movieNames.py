import requests
from bs4 import BeautifulSoup
import sys

page_source = #requests.get("https://en.wikiquote.org/wiki/List_of_films_(D–F)")
soup = BeautifulSoup(page_source.text, 'html.parser')

links = soup.find_all('a')

if len(sys.argv) < 2:
    print("need to name the output file")
    sys.exit()

movies = open(sys.argv[1], 'w')

def isMovieLink(link):
    if(link.get("title") == None):
        return False
    for phrase in link.get("title").split(" "):
        if phrase not in link.get("href"):
            return False
    return True

def getMovieNames(links):
    print("Stub")

for link in links:
    if isMovieLink(link):
        movies.write(link.get("title") + "\t" + link.get("href") + "\n")
