from textblob import TextBlob
from textblob_fr import PatternTagger, PatternAnalyzer
import json
import operator

def getSentimentForReview(review):
  #print(review)
  blob = TextBlob(review, pos_tagger=PatternTagger(), analyzer=PatternAnalyzer())
  sentiment = blob.sentiment
  #print(sentiment)
  #print()
  return sentiment

def getGlobalAnalyzisForMovie(jsonStr):
  movie = json.loads(jsonStr)
  movieId = movie['id']
  print(movieId)
  sentiments = (0.0, 0.0)
  reviews_number = 0
  for review in movie['reviews']:
    sentiments = map(operator.add, sentiments, getSentimentForReview(review))
    reviews_number += 1
  reviews_number += 0.0
  sentiments = map(lambda x : x / reviews_number, sentiments)
  output = {}
  output['id'] = movieId
  output['positivity'] = sentiments[0]
  output['subjectivity'] = sentiments[1]
  return json.dumps(output)
