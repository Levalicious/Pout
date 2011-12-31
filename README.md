Spout
==========
Introduction
------------
Spout is an open-source implementation of the [Minecraft](http://minecraft.net) 
server software written in Java, originally forked from Tad Hardesty's Glowstone
project, which was originally forked from Graham Edgecombe's now-defunct
[Lightstone](https://github.com/grahamedgecombe/lightstone) project.

The official server software has some shortcomings such as the use of threaded,
synchronous I/O along with high CPU and RAM usage. Spout aims to be a
lightweight and high-performance alternative.

Spout's main aim as a project independent from Lightstone is to offer a
higher-performance server while implementing the SpoutAPI platform for Minecraft mods.


Building
--------
Spout can be built with the
[Java Development Kit](http://oracle.com/technetwork/java/javase/downloads) and
[Apache Maven](http://maven.apache.org). Maven is also used for dependency
management.

You may download and compile [SpoutAPI](https://github.com/SpoutDev/SpoutAPI)
yourself if you desire and install it using `mvn install`, but it and other
dependencies will be automatically downloaded by Maven if they are not found.

The command `mvn package` will build Spout, and `mvn install` will copy it
to your local Maven repository. Official builds of Spout may be found on
[Jenkins](http://ci.getspout.org/job/Spout).

Running
-------
Running Spout is simple because all dependencies, including SpoutAPI, are
shaded into the output jar at compile time thanks to a nifty Maven plugin.
Simply execute `java -jar spout-dev-SNAPSHOT.jar` along with whatever
memory-related options to Java you desire, and the server should start.

By default, configuration is stored in the `config/` subdirectory and logs
are stored in the `logs/` subdirectory. The main configuration file is
`config/spout.yml`. 

Spout uses a [JLine](http://jline.sf.net)-based server console for command
input. On non-Windows systems, console output can also be colored. 

Documentation
-------------
Javadocs can be generated by using the `mvn javadoc:javadoc` command in the
terminal. This utilizes Maven's javadoc plugin and may need to download
dependencies the first time it is run.

For documentation on the Spout API, see the
[SpoutAPI Javadocs](http://jd.getspout.org/).

Credits
-------
 * [The Minecraft Coalition](http://wiki.vg/wiki) - protocol and file formats
   research.
 * [Trustin Lee](http://gleamynode.net) - author of the
   [Netty](http://jboss.org/netty) library.
 * Graham Edgecombe - author of the original
   [Lightstone](https://github.com/grahamedgecombe/lightstone) - and everyone
   else who has contributed to Lightstone.
 * All the people behind [Maven](http://maven.apache.org) and
   [Java](http://java.oracle.com).
 * [Notch](http://mojang.com/notch) and all the other people at
   [Mojang](http://mojang.com) - for making such an awesome game in the first
   place!

Copyright
---------
Spout is open-source software released under the LGPLv3 license, but with a 
provision that files are released under the MIT license 180 days after they
are published. Please see the `LICENSE.txt` file for details.
