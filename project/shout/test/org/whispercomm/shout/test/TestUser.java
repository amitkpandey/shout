
package org.whispercomm.shout.test;

import java.security.interfaces.ECPublicKey;

import org.whispercomm.shout.User;

public class TestUser implements User {

	public String username;
	public ECPublicKey ecPubKey;

	public TestUser() {

	}

	public TestUser(String username, ECPublicKey ecPubKey) {
		this.username = username;
		this.ecPubKey = ecPubKey;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public ECPublicKey getPublicKey() {
		return ecPubKey;
	}

}
