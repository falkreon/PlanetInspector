# Planet Inspector

This program edits "Planets Enigma Worlds". If that doesn't mean anything to you, please move along.


It opens existing mp_world files, and lets you delete, import, and rearrange rooms, change items at item locations, and build maps that the randomizer would never create.


## Getting Java

Planet Inspector requires Java. You can check this with the command-line command:

```
java --version
```

If it says 21 or higher, such as "openjdk 21.0.7", you don't need to install anything new. Often this will be the case if you play Minecraft.


### Java on Windows

If you *don't* have a compatible java, you can grab one from Adoptium - (https://adoptium.net/). Click the Download Temurin link (java 25 at the time of this writing).

The .msi distribution will do everything you need.


### Java on Linux

If you're on Ubuntu, Neon, Mint, or another Canonical distribution, Canonical ships an appropriate JDK/JRE through apt.

```
sudo apt install openjdk-25-jdk
```

verify with another `java --version`, because you may need to reboot the machine or switch the active java version to a different one.

(https://www.baeldung.com/linux/java-choose-default-version)


## Getting the Editor

Click on Releases and find the most recent release. You'll need the ".jar" file. Put in any folder you like, make sure it has executable access, and double-click the jar.
