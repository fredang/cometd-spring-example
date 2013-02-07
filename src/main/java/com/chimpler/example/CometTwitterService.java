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

import java.util.Map;

import javax.inject.Inject;

import org.cometd.annotation.Configure;
import org.cometd.annotation.Service;
import org.cometd.annotation.Session;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.authorizer.GrantAuthorizer;

/**
 * Comet Twitter Service
 * @author Frederic Dang Ngoc
 */

@javax.inject.Named
@javax.inject.Singleton
@Service("twitter")
public class CometTwitterService {
	
	@Inject
	private BayeuxServer bayeuxServer;
	@Session
	private ServerSession serverSession;
	
	@Configure ({"/twitter/samples"})
	protected void configureTwitterSamples(ConfigurableServerChannel channel) {
		channel.addAuthorizer(GrantAuthorizer.GRANT_SUBSCRIBE);
	}
	
	public void publishMessage(Map<String, Object> msg, String id) {
		ServerChannel channel = bayeuxServer.getChannel("/twitter/samples");
		channel.publish(serverSession, msg, id);
	}
}