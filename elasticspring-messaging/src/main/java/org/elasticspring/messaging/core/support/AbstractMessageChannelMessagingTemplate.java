/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasticspring.messaging.core.support;

import org.elasticspring.messaging.support.destination.CachingDestinationResolver;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.core.AbstractMessagingTemplate;
import org.springframework.messaging.core.DestinationResolver;

/**
 * @author Agim Emruli
 * @since 1.0
 */
//TODO: Use composition to make sure sns does not have to receive operations which are not implemented
public abstract class AbstractMessageChannelMessagingTemplate extends AbstractMessagingTemplate<String> {

	private final DestinationResolver<String> destinationResolver;

	protected AbstractMessageChannelMessagingTemplate(DestinationResolver<String> destinationResolver) {
		this.destinationResolver = new CachingDestinationResolver<String>(destinationResolver);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <P> Message<P> doReceive(String destination) {
		return (Message<P>) resolveMessageChannelByLogicalName(destination).receive();
	}

	@Override
	protected <S, R> Message<R> doSendAndReceive(String destination, Message<S> requestMessage) {
		throw new UnsupportedOperationException("not supported yet");
	}

	@Override
	protected void doSend(String destination, Message<?> message) {
		resolveMessageChannelByLogicalName(destination).send(message);
	}

	private PollableChannel resolveMessageChannelByLogicalName(String destination) {
		String physicalResourceId = this.destinationResolver.resolveDestination(destination);
		return resolveMessageChannel(physicalResourceId);
	}

	protected abstract PollableChannel resolveMessageChannel(String physicalResourceIdentifier);
}