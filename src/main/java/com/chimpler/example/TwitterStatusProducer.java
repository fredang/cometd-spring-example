/*
 * Copyright (c) 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chimpler.example;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Comet Twitter Status Producer
 * @author Frederic Dang Ngoc
 */

public class TwitterStatusProducer {
	private final static Logger logger = Logger.getLogger(TwitterStatusProducer.class.getName());

	private TwitterStream twitterStream;
	private CometTwitterService cometTwitterService;
	
	public void setCometTwitterService(CometTwitterService cometTwitterService) {
		this.cometTwitterService = cometTwitterService;
	}
	
	public synchronized void startSample(String username, String password) {
		if (twitterStream != null) {
			return;
		}
        TwitterStreamFactory factory = new TwitterStreamFactory(
        		new ConfigurationBuilder().setUser(username).setPassword(password)
        		.build());
        twitterStream = factory.getInstance();
        twitterStream.addListener(new StatusAdapter() {
			public void onStatus(Status status) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("status", "OK");
				map.put("createdAt", status.getCreatedAt().toString());
				map.put("username", status.getUser().getName());
				map.put("profileImageUrl", status.getUser().getMiniProfileImageURL());
				map.put("text", status.getText());
				cometTwitterService.publishMessage(map, Long.toString(status.getId()));
			}

			@Override
			public void onException(Exception ex) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("status", "ERR");
				map.put("text", ex.getMessage());
				cometTwitterService.publishMessage(map, "-1");
				stopSample();
			}
        });
		logger.log(Level.INFO, "Starting listening to twitter sample");
        twitterStream.sample();
	}
	
	public synchronized void stopSample() {
		if (twitterStream == null) {
			return;
		}

		logger.log(Level.INFO, "Stopping listening to twitter sample");
		try {
			twitterStream.shutdown();
		} catch (Exception e) {}
		twitterStream = null;
	}
}
