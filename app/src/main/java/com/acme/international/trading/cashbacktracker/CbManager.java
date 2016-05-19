package com.acme.international.trading.cashbacktracker;

import com.acme.international.trading.cashbacktracker.database.CashbackDba;

public class CbManager {

	private static CbManager mManager;
	private CashbackDba mDba;
	
	public static CbManager getManager() {
		if(mManager == null)
			mManager = new CbManager();
		return mManager;
	}
	
	public CbManager(){
		mDba = new CashbackDba();
	}
	
	public CashbackDba getDB() {
		return mDba;
	}
}
