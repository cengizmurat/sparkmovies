from kafka import KafkaConsumer
from kafka import KafkaProducer
from kafka import TopicPartition
from get_movie import *
from review_analyzis import *

class KafkaManager:

  def __init__(self, ip):
    self.movieTopic = 'movie4'
    self.feelsTopic = 'feel4'
    self.producer = KafkaProducer(bootstrap_servers=ip + ':9092')
    self.consumerMovie = KafkaConsumer(self.movieTopic, bootstrap_servers=ip + ':9092', group_id='test-consumer-group')
    self.consumerFeels = KafkaConsumer(self.feelsTopic, bootstrap_servers=ip + ':9092', group_id='test-consumer-group')
    #self.consumer.subscribe([self.movieTopic, self.feelsTopic])

  def getMovies(self, n, restart):
    #return self.getMessages(self.movieTopic, n, restart)
    messages = []
    if n == 0:
      return messages
    self.consumerMovie.poll(timeout_ms=100)
    partition = TopicPartition(self.movieTopic, 0)
    pos = self.consumerMovie.position(partition)
    if n < 0:
      n = pos
    elif n > pos:
      n = pos
    #print("Fetched movies : " + pos)

    if (restart):
      self.consumerMovie.seek_to_beginning()
    count = 0
    for record in self.consumerMovie:
      message = record.value
      #print(message)
      messages.append(message)
      count += 1
      if count >= n:
        break
      elif count >= pos:
        break
    return messages

  def getFeels(self, n, restart):
    #return self.getMessages(self.feelsTopic, n, restart)
    messages = []
    if n == 0:
      return messages
    self.consumerFeels.poll(timeout_ms=100)
    partition = TopicPartition(self.feelsTopic, 0)
    pos = self.consumerFeels.position(partition)
    if n < 0:
      n = pos
    elif n > pos:
      n = pos
    #print("Fetched movies : " + pos)

    if (restart):
      self.consumerFeels.seek_to_beginning()
    count = 0
    for record in self.consumerFeels:
      message = record.value
      #print(message)
      messages.append(message)
      count += 1
      if count >= n:
        break
      elif count >= pos:
        break
    return messages

  def getMessages(self, topic, n, restart):
    messages = []
    if n == 0:
      return messages
    self.consumer.poll(timeout_ms=100)
    partition = TopicPartition(topic, 0)
    pos = self.consumer.position(partition)
    if n < 0:
      n = pos
    elif n > pos:
      n = pos
    #print("Fetched movies : " + pos)

    if (restart):
      self.consumer.seek_to_beginning()
    count = 0
    for record in self.consumer:
      message = record.value
      #print(message)
      messages.append(message)
      count += 1
      if count >= n:
        break
      elif count >= pos:
        break
    return messages

  def writeMovie(self, msg):
    self.producer.send(self.movieTopic, msg)

  def writeFeels(self, msg):
    self.producer.send(self.feelsTopic, msg)

  def getDataFile(self):
    file = open("data/movies.txt", "r")
    count = 0
    for json in file.readlines():
      self.writeMovie(json)
      count += 1
    return count

  def getDataLive(self, movieId):
    json = getMovieJson(movieId)
    self.writeMovie(json)

  def computeFeels(self, n, restart):
    movies = self.getMovies(n, restart)
    file = open("data/feels.txt", "w")
    for movie in movies:
      json = getGlobalAnalyzisForMovie(movie)
      self.writeFeels(json)
      file.write(json + '\n')
    return len(movies)
