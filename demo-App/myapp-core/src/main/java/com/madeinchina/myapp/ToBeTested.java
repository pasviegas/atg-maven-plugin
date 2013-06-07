package com.madeinchina.myapp;

import atg.nucleus.GenericService;
import atg.nucleus.Nucleus;
import atg.nucleus.ServiceException;
import atg.nucleus.logging.ApplicationLogging;

public class ToBeTested extends GenericService {

	boolean mCleanStart = false;

	// -------------------------
	/**
	 * Called when this component is started.
	 */
	public void doStartService() throws ServiceException {
		prepare();
	}

	// -------------------------
	/**
	 * Called when this component is shut down.
	 */
	public void doStopService() throws ServiceException {
		mCleanStart = false;
	}

	// -------------------------
	/**
	 * Checks that Nucleus is running, if its not, then throw a
	 * ServiceException. If Nucleus is running, the log an info message.
	 * 
	 * @throws ServiceException
	 */
	public void prepare() throws ServiceException {
		Nucleus n = Nucleus.getGlobalNucleus();
		if (n == null)
			throw new ServiceException("Nucleus is not running.");
		else
			((ApplicationLogging) n).logInfo("Prepared.");
		mCleanStart = true;
	}
}
