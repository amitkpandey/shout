
package org.whispercomm.shout.id;

import org.whispercomm.shout.ShoutImage;
import org.whispercomm.shout.HashReference;
import org.whispercomm.shout.Me;
import org.whispercomm.shout.crypto.ECKeyPair;
import org.whispercomm.shout.crypto.ECPrivateKey;
import org.whispercomm.shout.crypto.ECPublicKey;

public class MeImpl implements Me {

	private ECPublicKey publicKey;
	private ECPrivateKey privateKey;

	private String username;

	private HashReference<ShoutImage> avatar;

	public MeImpl(String username, ECPublicKey publicKey, ECPrivateKey privateKey,
			HashReference<ShoutImage> avatar) {
		this.username = username;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		this.avatar = avatar;
	}

	public MeImpl(String username, ECKeyPair keyPair, HashReference<ShoutImage> avatar) {
		this(username, keyPair.getPublicKey(), keyPair.getPrivateKey(), avatar);
	}

	@Override
	public ECPublicKey getPublicKey() {
		return this.publicKey;
	}

	@Override
	public ECPrivateKey getPrivateKey() {
		return this.privateKey;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public HashReference<ShoutImage> getAvatar() {
		return this.avatar;
	}

}
