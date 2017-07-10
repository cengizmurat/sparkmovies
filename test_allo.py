from bs4 import BeautifulSoup
import urllib


url = "http://www.allocine.fr/film/fichefilm-207060/critiques/spectateurs/"

html = urllib.request.urlopen(url).read()
soup = BeautifulSoup(html, 'html.parser')

max = 0
for p in soup.find_all("nav", {"class": "pagination cf"}):
    for c in p.find_all("div", {"class": "pagination-item-holder"}):
        max = int(p.find_all("span")[-1].text)


com = []

for i in range(max):
    print("Parsing page...", i + 1)
    url = "http://www.allocine.fr/film/fichefilm-207060/critiques/spectateurs/?page=" + str(i + 1)
    html = urllib.request.urlopen(url).read()
    soup = BeautifulSoup(html, 'html.parser')
    for d in soup.find_all("div", {"class": "row item hred"}):
        c = d.find("div", {"class": "col-xs-12 col-sm-9"})
        com.append(c.find("p", {"itemprop": "description"}).text)

print(len(com))
