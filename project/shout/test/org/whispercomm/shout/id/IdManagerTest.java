package org.whispercomm.shout.id;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.whispercomm.shout.Me;
import org.whispercomm.shout.test.ShoutTestRunner;

import android.app.Activity;
import android.content.Context;

@RunWith(ShoutTestRunner.class)
public class IdManagerTest {

	private static final String USERNAME = "catherine";
	private Context context;
	private IdManager idManager;
	
	@Before
	public void setUp() {
		this.context = new Activity();
		idManager = new IdManager(context);
	}
	
	@After
	public void takeDown() {
		this.context = null;
		this.idManager = null;
	}
	
	@Test
	public void testResetUserFirstTime() {
		idManager.resetUser(USERNAME);
		Me me = idManager.getMe();
		assertNotNull(me);
		assertNotNull(me.getKeyPair());
		assertNotNull(me.getPublicKey());
		assertEquals(me.getPublicKey(), me.getKeyPair().getPublic());
		assertEquals(USERNAME, me.getUsername());
		assertTrue(me.getDatabaseId() > 0);
	}
	
	@Test
	public void testResetUserOverwritesOldUser() {
		String newUsername = "dadrian";
		idManager.resetUser(USERNAME);
		Me first = idManager.getMe();
		idManager.resetUser(newUsername);
		Me second = idManager.getMe();
		assertThat(first.getUsername(), is(not(second.getUsername())));
		assertThat(first.getDatabaseId(), is(not(second.getDatabaseId())));
		assertThat(first.getKeyPair(), is(not(second.getKeyPair())));
		assertThat(first.getPublicKey(), is(not(second.getPublicKey())));
	}
}