package net.glowstone.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.glowstone.GlowChunk;
import net.glowstone.util.Position;

import net.glowstone.msg.DestroyEntityMessage;
import net.glowstone.msg.LoadChunkMessage;
import net.glowstone.msg.Message;
import net.glowstone.msg.PingMessage;
import net.glowstone.msg.PositionRotationMessage;
import net.glowstone.msg.SpawnPlayerMessage;
import net.glowstone.net.Session;

/**
 * Represents an in-game player.
 * @author Graham Edgecombe
 */
public final class Player extends Mob {

    /**
     * The normal height of a player's eyes above their feet.
     */
	public static final double EYE_HEIGHT = 1.62D;

    /**
     * The name of this player.
     */
	private final String name;

    /**
     * This player's session.
     */
	private final Session session;

    /**
     * The entities that the client knows about.
     */
	private Set<Entity> knownEntities = new HashSet<Entity>();

    /**
     * The chunks that the client knows about.
     */
	private Set<GlowChunk.Key> knownChunks = new HashSet<GlowChunk.Key>();

    /**
     * Creates a new player and adds it to the world.
     * @param session The player's session.
     * @param name The player's name.
     */
	public Player(Session session, String name) {
		super(session.getServer().getWorld());
		this.name = name;
		this.session = session;

		// stream the initial set of blocks and teleport us
		this.streamBlocks();
		this.location = world.getSpawnLocation();
		this.session.send(new PositionRotationMessage(location.getX(), location.getY(), location.getZ(), location.getY() + EYE_HEIGHT, (float) location.getYaw(), (float) location.getPitch(), true));
	}

    /**
     * Gets the name of this player.
     * @return The name of this player.
     */
	public String getName() {
		return name;
	}

	@Override
	public void pulse() {
		super.pulse();
        session.send(new PingMessage());

		streamBlocks();

		for (Iterator<Entity> it = knownEntities.iterator(); it.hasNext(); ) {
			Entity entity = it.next();
			boolean withinDistance = entity.isActive() && isWithinDistance(entity);

			if (withinDistance) {
				Message msg = entity.createUpdateMessage();
				if (msg != null)
					session.send(msg);
			} else {
				session.send(new DestroyEntityMessage(entity.getId()));
				it.remove();
			}
		}

		for (Entity entity : world.getEntityManager()) {
			if (entity == this)
				continue;
			boolean withinDistance = entity.isActive() && isWithinDistance(entity);

			if (withinDistance && !knownEntities.contains(entity)) {
				knownEntities.add(entity);
				session.send(entity.createSpawnMessage());
			}
		}
	}

    /**
     * Streams chunks to the player's client.
     */
	private void streamBlocks() {
        Set<GlowChunk.Key> previousChunks = new HashSet<GlowChunk.Key>(knownChunks);

		int centralX = ((int) location.getX()) / GlowChunk.WIDTH;
		int centralZ = ((int) location.getZ()) / GlowChunk.HEIGHT;

		for (int x = (centralX - GlowChunk.VISIBLE_RADIUS); x <= (centralX + GlowChunk.VISIBLE_RADIUS); x++) {
			for (int z = (centralZ - GlowChunk.VISIBLE_RADIUS); z <= (centralZ + GlowChunk.VISIBLE_RADIUS); z++) {
				GlowChunk.Key key = new GlowChunk.Key(x, z);
				if (!knownChunks.contains(key)) {
					knownChunks.add(key);
					session.send(new LoadChunkMessage(x, z, true));
					session.send(world.getChunkManager().getChunk(x, z).toMessage());
				}
				previousChunks.remove(key);
			}
		}

		for (GlowChunk.Key key : previousChunks) {
			session.send(new LoadChunkMessage(key.getX(), key.getZ(), false));
			knownChunks.remove(key);
		}

		previousChunks.clear();
	}

    /**
     * Gets the session.
     * @return The session.
     */
	public Session getSession() {
		return session;
	}

	@Override
	public Message createSpawnMessage() {
		int x = Position.getIntX(location);
		int y = Position.getIntY(location);
		int z = Position.getIntZ(location);
		int yaw = Position.getIntYaw(location);
		int pitch = Position.getIntPitch(location);
		return new SpawnPlayerMessage(id, name, x, y, z, yaw, pitch, 0);
	}

}
