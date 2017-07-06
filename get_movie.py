#!/usr/bin/python2.7
# -*-coding:Latin-1 -*

from bs4 import BeautifulSoup
import urllib

def getSoupFromUrl(url) :
  html = urllib.urlopen(url).read()
  soup = BeautifulSoup(html, 'html.parser')
  return soup

def findMaxPages(soup):
  max = 0
  for p in soup.find_all("nav", {"class": "pagination cf"}):
    for c in p.find_all("div", {"class": "pagination-item-holder"}):
      max = int(p.find_all("span")[-1].text)
  return max

def getReviewsJson(movieUrl) :
  soup = getSoupFromUrl(movieUrl)
  max = findMaxPages(soup)

  json = "\"reviews\":["
  for i in range(max):
    print("Parsing page...", i + 1)
    url_page = movieUrl + "?page=" + str(i + 1)
    soup_page = getSoupFromUrl(url_page)
    for d in soup_page.find_all("div", {"class": "row item hred"}):
        c = d.find("div", {"class": "col-xs-12 col-sm-9"})
        json += "\"" + c.find("p", {"itemprop": "description"}).text.strip() + "\","
  json = json[:-1] + "]"
  return json

def getMovieJson(movieId):
  url_movie = "http://www.allocine.fr/film/fichefilm_gen_cfilm=" + movieId + ".html"
  soup = getSoupFromUrl(url_movie)
  json = "{"
  for d in soup.find_all("section", {"id" : "content-start"}):
    for e in d.find_all("div", {"class", "row row-2-cols row-col-padded cf section"}):
      for f in e.find_all("div", {"class", "col-left"}):
        for g in f.find_all("div", {"class", "card card-entity card-movie-overview row row-col-padded-10 cf"}):
          for h in g.find_all("div", {"class", "meta col-xs-12 col-md-8"}):
            for i in h.find_all("div", {"class", "meta-body"}):
              for j in i.find_all("div", {"class", "meta-body-item"}):
                key = j.find("span", {"class": "light"}).text.encode("utf-8")
                if key == "Date de sortie":
                  json += "\"release\":\"" + j.find_all("span")[-1].text + "\","
                elif key == "De" :
                  json += "\"author\":\""
                  json += j.find_all("span")[-1].text
                  json += "\","
                elif key == "Avec" :
                  json += "\"actors\":["
                  for v in j.find_all("span") :
                    actor = v.text
                    if actor != " plus " and actor != "Avec":
                      json += "\"" + v.text + "\","
                  json = json[:-1]
                  json += "],"
                elif key == "Genres" :
                  json += "\"genres\":["
                  for v in j.find_all("span", {"itemprop": "genre"}):
                    json += "\"" + v.text + "\","
                  json = json[:-1]
                  json += "],"
                elif key == "Nationalité" :
                  json += "\"country\":\"" + j.find_all("span")[-1].text.strip() + "\","

  url_review = "http://www.allocine.fr/film/fichefilm-" + movieId + "/critiques/spectateurs/"
  json = json[:-1] + getReviewsJson(url_review) + "}"
  #json = json[:-1] + "}" # without reviews
  return json

json = getMovieJson("207060")
print(json)
