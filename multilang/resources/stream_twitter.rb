# -*- coding: utf-8 -*-
require "./storm"
require "twitter"


$words = ["nathan", "mike", "jackson", "golda", "bertels人"]
def random_word
  $words[rand($words.length)]
end

class StreamTwitter < Storm::Spout
  attr_accessor :uid, :pending
  def open(conf, context)
    emit ['spout initializing']
    self.pending = {}
    self.uid = 0

  end
  def nextTuple
    #sleep 0.5
    word = random_word
    id = self.uid += 1
    self.pending[id] = word
    emit [word], :id => id

    client = Twitter::Streaming::Client.new do |config|
      config.consumer_key       = 'ziSzNkrNzeQ2pjdGUZYmw'
      config.consumer_secret    = 'JApCmbjr5Hjx0LTTMfcmIM20g6ID54o4Vub6TcfB4'
      config.oauth_token        = '1012280850-Pb7EA6urmlDWVkKOuuLB9IAvGqyw4JiJVYTMEx8'
      config.oauth_token_secret = 'QAh47SHFRTElih0sSPFTbrFyE6QpVEDA4XarIzM4WA'
      config.proxy = 'http://194.140.11.77:80'
    end

    topics = ["mundial", "brasil"]
    client.filter(:track => topics.join(",")) do |object|
      case object
  when Twitter::Tweet
    puts "It's a tweet!"
  when Twitter::DirectMessage
    puts "It's a direct message!"
  when Twitter::Streaming::StallWarning
    warn "Falling behind!"
  end
      emit [object]
      puts object.text if object.is_a?(Twitter::Tweet)
      emit [status.text]
    end


  end
  def ack(id)
    #self.pending.delete(id)
  end
  def fail(id)
    word = self.pending[id]
    log "emitting " + word + " on fail"
    emit [word], :id => id
  end
end

StreamTwitter.new.run
