/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.component.impl.AnimationComponent;
import org.spout.api.component.impl.InteractComponent;
import org.spout.api.component.impl.ModelComponent;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Vector3;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.plugin.Platform;
import org.spout.api.plugin.Plugin;

import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutEngine;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.util.thread.AsyncExecutorUtils;
import org.spout.engine.world.SpoutRegion;

public class TestCommands {
	private final SpoutEngine engine;

	public TestCommands(SpoutEngine engine) {
		this.engine = engine;
	}

	@Command(aliases = "break", desc = "Debug command to break a block")
	public void debugBreak(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getPlatform() != Platform.CLIENT) {
			throw new CommandException("You must be a client to perform this command.");
		}
		Player player = ((Client) Spout.getEngine()).getActivePlayer();
		Block block = player.get(InteractComponent.class).getTargetBlock();

		if (block == null || block.getMaterial().equals(BlockMaterial.AIR)) {
			source.sendMessage("No blocks in range.");
		} else {
			source.sendMessage("Block to break: ", block.toString());
			block.setMaterial(BlockMaterial.AIR);
		}
	}

	@Command(aliases = {"dbg"}, desc = "Debug Output")
	public void debugOutput(CommandContext args, CommandSource source) {
		World world = engine.getDefaultWorld();
		source.sendMessage("World Entity count: ", world.getAll().size());
	}

	@Command(aliases = "dumpthreads", desc = "Dumps a listing of all thread stacks to the console")
	public void dumpThreads(CommandContext args, CommandSource source) throws CommandException {
		AsyncExecutorUtils.dumpAllStacks();
	}

	@Command(aliases = "testmsg", desc = "Test extracting chat styles from a message and printing them")
	public void testMsg(CommandContext args, CommandSource source) throws CommandException {
		source.sendMessage(args.getJoinedString(0));
	}

	@Command(aliases = "plugins-tofile", usage = "[filename]", desc = "Creates a file containing all loaded plugins and their version", min = 0, max = 1)
	@CommandPermissions("spout.command.pluginstofile")
	public void getPluginDetails(CommandContext args, CommandSource source) throws CommandException {

		// File and filename
		String filename = "";
		String standpath = "pluginreports";
		File file = null;

		// Getting date
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String parse = dateFormat.format(date);

		// Create file with passed filename or current date and time as name
		if (args.length() == 1) {
			filename = args.getString(0);
			file = new File(standpath.concat("/" + replaceInvalidCharsWin(filename)));
		} else {
			file = new File(standpath.concat("/" + replaceInvalidCharsWin(parse)).concat(".txt"));
		}

		// Delete the file if existent
		if (file.exists()) {
			file.delete();
		}

		String linesep = System.getProperty("line.separator");

		// Create a new file
		try {
			new File("pluginreports").mkdirs();
			file.createNewFile();
		} catch (IOException e) {
			throw new CommandException("Couldn't create report-file!" + linesep + "Please make sure to only use valid chars in the filename.");
		}

		// Content Builder
		StringBuilder sbuild = new StringBuilder();
		sbuild.append("# This file was created on the " + dateFormat.format(date).concat(linesep));
		sbuild.append("# Plugin Name | Version | Authors".concat(linesep));

		// Plugins to write down
		List<Plugin> plugins = Spout.getEngine().getPluginManager().getPlugins();

		// Getting plugin informations
		for (Plugin plugin : plugins) {

			// Name and Version
			sbuild.append(plugin.getName().concat(" | "));
			sbuild.append(plugin.getDescription().getVersion());

			// Authors
			List<String> authors = plugin.getDescription().getAuthors();
			StringBuilder authbuilder = new StringBuilder();
			if (authors != null && authors.size() > 0) {
				int size = authors.size();
				int count = 0;
				for (String s : authors) {
					count++;
					if (count != size) {
						authbuilder.append(s + ", ");
					} else {
						authbuilder.append(s);
					}
				}
				sbuild.append(" | ".concat(authbuilder.toString()).concat(linesep));
			} else {
				sbuild.append(linesep);
			}
		}

		BufferedWriter writer = null;

		// Write to file
		if (file != null) {
			try {
				writer = new BufferedWriter(new FileWriter(file));
				writer.write(sbuild.toString());
			} catch (IOException e) {
				throw new CommandException("Couldn't write to report-file!");
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		source.sendMessage("Plugins-report successfully created! " + linesep + "Stored in: " + standpath);
	}

	@Command(aliases = {"move"}, desc = "Move a entity with his Id", min = 4, max = 4)
	public void moveEntity(CommandContext args, CommandSource source) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (Spout.getPlatform() == Platform.CLIENT) {
				player = ((SpoutClient) engine).getActivePlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);
		float x = args.getFloat(1);
		float y = args.getFloat(2);
		float z = args.getFloat(3);

		Entity e = player.getWorld().getEntity(id);

		if(e == null)
			return;

		e.getScene().setPosition(new Point(e.getWorld(), x, y, z));

		Spout.log("Entity " + id + " move to " + x + " " + y + " " +z);
	}

	@Command(aliases = {"rotate"}, desc = "Rotate a entity with his Id", min = 4, max = 4)
	public void rotateEntity(CommandContext args, CommandSource source) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (Spout.getPlatform() == Platform.CLIENT) {
				player = ((SpoutClient) engine).getActivePlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);
		float pitch = args.getFloat(1);
		float yaw = args.getFloat(2);
		float roll = args.getFloat(3);

		Entity e = player.getWorld().getEntity(id);

		if(e == null)
			return;
		e.getTransform().setPitch(pitch);
		e.getTransform().setYaw(yaw);
		e.getTransform().setRoll(roll);

		Spout.log("Entity " + id + " rotate to " + pitch + " " + yaw + " " +roll);
	}


	@Command(aliases = {"scale"}, desc = "Scale a entity with his Id", min = 4, max = 4)
	public void scaleEntity(CommandContext args, CommandSource source) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (Spout.getPlatform() == Platform.CLIENT) {
				player = ((SpoutClient) engine).getActivePlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);
		float x = args.getFloat(1);
		float y = args.getFloat(2);
		float z = args.getFloat(3);

		Entity e = player.getWorld().getEntity(id);

		if(e == null)
			return;

		e.getScene().getTransform().scale(new Vector3(x, y, z));

		Spout.log("Entity " + id + " scale to " + x + " " + y + " " +z);
	}

	@Command(aliases = {"animstart"}, desc = "Launch a animation his Id", min = 2, max = 3)
	public void playAnimation(CommandContext args, CommandSource source) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (Spout.getPlatform() == Platform.CLIENT) {
				player = ((SpoutClient) engine).getActivePlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);

		Entity e = player.getWorld().getEntity(id);
		
		if(e == null){
			Spout.log("Entity not found");
			return;
		}
		
		ModelComponent model = e.get(ModelComponent.class);
		
		if(model == null){
			Spout.log("No model on this entity");
			return;
		}

		Skeleton skeleton = model.getModel().getSkeleton();

		if(skeleton == null){
			Spout.log("No skeleton on this entity");
			return;
		}

		Animation animation = model.getModel().getAnimations().get(args.getString(1));
		
		if(animation == null){
			Spout.log("No animation with " + args.getString(1) + ", see the list :");
			for(String a : model.getModel().getAnimations().keySet()){
				Spout.log(a);
			}
			return;
		}
		
		AnimationComponent ac = e.get(AnimationComponent.class);

		ac.playAnimation(animation, args.length() > 2 ? args.getString(2).equalsIgnoreCase("on") : false);
		
		Spout.log("Entity " + id + " play " + animation.getName());
	}
	
	@Command(aliases = {"animstop"}, desc = "Stop all animation on a entity", min = 1, max = 1)
	public void stopAnimation(CommandContext args, CommandSource source) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (Spout.getPlatform() == Platform.CLIENT) {
				player = ((SpoutClient) engine).getActivePlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);

		Entity e = player.getWorld().getEntity(id);
		
		if(e == null){
			Spout.log("Entity not found");
			return;
		}
		
		AnimationComponent ac = e.get(AnimationComponent.class);
		
		if(ac == null){
			Spout.log("No AnimationComponent on this entity");
			return;
		}

		ac.stopAnimations();
		
		Spout.log("Entity " + id + " animation stopped ");
	}
	
	/**
	 * Replaces chars which are not allowed in filenames on windows with "-".
	 */
	private String replaceInvalidCharsWin(String s) {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			return s.replaceAll("[\\/:*?\"<>|]", "-");
		} else {
			return s;
		}
	}
}
