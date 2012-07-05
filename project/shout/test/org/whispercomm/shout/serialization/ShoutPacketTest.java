
package org.whispercomm.shout.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.whispercomm.shout.Shout;
import org.whispercomm.shout.serialization.ShoutPacket.PacketBuilder;
import org.whispercomm.shout.test.ShoutTestRunner;
import org.whispercomm.shout.test.util.TestFactory;
import org.whispercomm.shout.test.util.TestShout;
import org.whispercomm.shout.test.util.TestUser;
import org.whispercomm.shout.test.util.TestUtility;

@RunWith(ShoutTestRunner.class)
public class ShoutPacketTest {

	private static final String PACKET_EXCEPTION_FAIL = "ShoutPacketException thrown";
	private static final int SIGNATURE_SIZE = 71;
	private TestShout shout;
	private TestShout recomment;
	private TestShout comment;

	private TestUser shouter;
	private TestUser reshouter;
	private TestUser commenter;

	@Before
	public void setUp() {
		shouter = new TestUser("dadrian");
		reshouter = new TestUser("duiu");
		commenter = new TestUser("DRBild");
		shout = new TestShout(shouter, null, "I shout cool things!", DateTime.now(),
				TestFactory.genByteArray(SIGNATURE_SIZE), null);
		comment = new TestShout(commenter, shout, "No you don't", DateTime.now(),
				TestFactory.genByteArray(SIGNATURE_SIZE), null);
		recomment = new TestShout(reshouter, comment, null, DateTime.now(),
				TestFactory.genByteArray(SIGNATURE_SIZE), null);
		shout.hash = SerializeUtility.generateHash(shout);
		comment.hash = SerializeUtility.generateHash(comment);
		recomment.hash = SerializeUtility.generateHash(recomment);
	}

	@After
	public void takeDown() {
		this.shout = null;
		this.recomment = null;
		this.comment = null;
		this.shouter = null;
		this.reshouter = null;
		this.commenter = null;
	}

	@Test
	public void testBuildRecomment() {
		PacketBuilder builder = new ShoutPacket.PacketBuilder();
		try {
			builder.addShout(recomment);
		} catch (ShoutChainTooLongException e) {
			fail("Shout chain is only 3!");
		}
		ShoutPacket packet = builder.build();
		assertNotNull(packet);
		int count = packet.getShoutCount();
		assertEquals(3, count);
		byte[] body = packet.getBodyBytes();
		assertNotNull(body);
		Shout fromBytes;
		try {
			fromBytes = packet.decodeShout();
		} catch (BadShoutVersionException e) {
			fail("BadShoutVersionException thrown");
			return;
		} catch (ShoutPacketException e) {
			fail(PACKET_EXCEPTION_FAIL);
			return;
		}
		assertNotNull(fromBytes);
		TestUtility.testEqualShoutFields(recomment, fromBytes);
	}

	@Test
	public void testBuildComment() {
		PacketBuilder builder = new ShoutPacket.PacketBuilder();
		try {
			builder.addShout(comment);
		} catch (ShoutChainTooLongException e) {
			fail("Shout chain is only 2!");
		}
		ShoutPacket packet = builder.build();
		assertNotNull(packet);
		int count = packet.getShoutCount();
		assertEquals(2, count);
		byte[] body = packet.getBodyBytes();
		assertNotNull(body);
		Shout fromBytes;
		try {
			fromBytes = packet.decodeShout();
		} catch (BadShoutVersionException e) {
			fail("BadShoutVersionException thrown");
			return;
		} catch (ShoutPacketException e) {
			fail(PACKET_EXCEPTION_FAIL);
			return;
		}
		TestUtility.testEqualShoutFields(comment, fromBytes);
	}
	
	@Test
	public void testWrapShout() {
		try {
			PacketBuilder builder = new ShoutPacket.PacketBuilder();
			builder.addShout(shout);
			ShoutPacket packet = builder.build();
			assertNotNull(packet);
			byte[] packetBytes = packet.getPacketBytes();
			assertNotNull(packetBytes);
			ShoutPacket wrapper = ShoutPacket.wrap(packetBytes);
			assertEquals(1, wrapper.getShoutCount());
			Shout decoded = wrapper.decodeShout();
			assertNotNull(decoded);
			TestUtility.testEqualShoutFields(shout, decoded);
		} catch (ShoutChainTooLongException e) {
			fail("Shout chain was not too long");
		} catch (BadShoutVersionException e) {
			fail("Shout version is not bad");
		} catch (ShoutPacketException e) {
			fail(PACKET_EXCEPTION_FAIL);
		}
	}
	
	@Test
	public void testWrapComment() {
		try {
			PacketBuilder builder = new ShoutPacket.PacketBuilder();
			builder.addShout(comment);
			ShoutPacket packet = builder.build();
			assertNotNull(packet);
			byte[] packetBytes = packet.getPacketBytes();
			assertNotNull(packetBytes);
			ShoutPacket wrapper = ShoutPacket.wrap(packetBytes);
			assertEquals(2, wrapper.getShoutCount());
			Shout decoded = wrapper.decodeShout();
			assertNotNull(decoded);
			TestUtility.testEqualShoutFields(comment, decoded);
		} catch (BadShoutVersionException e) {
			e.printStackTrace();
			fail("Shout version is not bad");
		} catch (ShoutChainTooLongException e) {
			e.printStackTrace();
			fail("Shout chain is not too long (2)");
		} catch (ShoutPacketException e) {
			e.printStackTrace();
			fail(PACKET_EXCEPTION_FAIL);
		}
	}
}
