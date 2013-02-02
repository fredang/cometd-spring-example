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

import javax.inject.Inject;

import org.cometd.annotation.Configure;
import org.cometd.annotation.Service;
import org.cometd.annotation.Session;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.authorizer.GrantAuthorizer;

import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Twitter Service
 * @author Frederic Dang Ngoc
 */

@javax.inject.Named
@javax.inject.Singleton
@Service("generator")
public class TwitterService {
	private final static Logger logger = Logger.getLogger(TwitterService.class.getName());
	
	@Inject
	private BayeuxServer bayeuxServer;
	@Session
	private ServerSession serverSession;
	
	private TwitterStream twitterStream;

	@Configure ({"/twitter/samples"})
	protected void configureTwitterSamples(ConfigurableServerChannel channel) {
		logger.log(Level.INFO, "Configuring /twitter/samples");
		channel.addAuthorizer(GrantAuthorizer.GRANT_SUBSCRIBE);
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
				ServerChannel channel = bayeuxServer.getChannel("/twitter/samples");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("status", "OK");
				map.put("createdAt", status.getCreatedAt().toString());
				map.put("username", status.getUser().getName());
				map.put("profileImageUrl", status.getUser().getMiniProfileImageURL());
				map.put("text", status.getText());
				channel.publish(serverSession, map, "" + status.getId());
			}

			@Override
			public void onException(Exception ex) {
				ServerChannel channel = bayeuxServer.getChannel("/twitter/samples");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("status", "ERR");
				map.put("text", ex.getMessage());
				channel.publish(serverSession, map, "-1");
				stopSample();
			}
        });
		logger.log(Level.INFO, "Listening to twitter sample");
        twitterStream.sample();
	}
	
	public synchronized void stopSample() {
		if (twitterStream == null) {
			return;
		}
		try {
			twitterStream.shutdown();
		} catch (Exception e) {}
		twitterStream = null;
	}
}