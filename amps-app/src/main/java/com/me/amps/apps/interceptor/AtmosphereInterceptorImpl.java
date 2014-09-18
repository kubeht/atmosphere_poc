package com.me.amps.apps.interceptor;

import org.atmosphere.config.service.AtmosphereInterceptorService;
import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereInterceptorAdapter;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.interceptor.InvokationOrder;

import com.me.amps.core.MessageExchangeService;
import com.me.amps.core.MessageExchangeService.ProcessAction;

/**
 * 
 * @author ian.li
 *
 */
@AtmosphereInterceptorService
public class AtmosphereInterceptorImpl extends AtmosphereInterceptorAdapter {
	
	private MessageExchangeService mes = new MessageExchangeService();
	
	public AtmosphereInterceptorImpl () {
		super();
	}
	
	@Override
	public Action inspect(AtmosphereResource r) {
		ProcessAction pa = mes.interceptRequest(r);
		if (ProcessAction.GO == pa) {
			return Action.CONTINUE;
		} else {
			return Action.SKIP_ATMOSPHEREHANDLER;
		} 
	}

    @Override
    public PRIORITY priority() {
        return InvokationOrder.AFTER_DEFAULT;
    }

}
