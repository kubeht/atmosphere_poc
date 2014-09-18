package com.me.amps.apps.filter;

import org.atmosphere.config.service.BroadcasterFilterService;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.BroadcastFilterAdapter;

import com.me.amps.core.MessageExchangeService;
import com.me.amps.core.MessageExchangeService.ProcessAction;


@BroadcasterFilterService
public class BroadcastFilterImpl extends BroadcastFilterAdapter {
	private MessageExchangeService subscriptionService = new MessageExchangeService();

    @Override
    public BroadcastAction filter(String broadcasterId, AtmosphereResource r, Object originalMessage, Object message) {
    	ProcessAction pa = subscriptionService.interceptBroadcast(broadcasterId, r, originalMessage, message);
    	if (pa ==  ProcessAction.GO) {
    		return new BroadcastAction(message);
    	} else {
            return new BroadcastAction(BroadcastAction.ACTION.ABORT ,message);
    	}
    }

}
