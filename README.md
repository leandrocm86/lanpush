# LANPUSH
### Share texts through LAN


LANPUSH is a utility to exchange text messages between devices in the same local network.<br>
No internet required! No login! Just send a message and all configured devices in the same LAN can be notified with it.

#### Use cases:
- Share a link between PC and phone:
Tired of copying/pasting (and then copying/pasting again) online notes to share links or texts between your devices?
LANPUSH allows you to quickly move texts from one device to another.
Once a message is sent, all PCs and Android phones on the same network will be able to show a notification with the new message, along with the options to copy it or browse it directly.

- Server automation:
With LANPUSH your server can easily send you warnings even when no internet is available.
You can also go the other way around, sending commands for the server to read and start your tasks.
Tired of going through SSH everytime just to start trivial jobs? Make your server act upon the text it receives by redirecting lanpush messages to a predefined file and consume it in any way you wish.

#### Installation:
- <ins>Linux:</ins>&nbsp;&nbsp;LANPUSH's GUI client can easily be installed via snap (command bellow). The snap already has a built-in Java JRE and is ready to go by simply calling "lanpush-gui" as a command or by launching the shurtcut installed with it.
```
sudo snap install lanpush-gui
```

- <ins>Windows:</ins>&nbsp;&nbsp;Although the LANPUSH's GUI client is not available at the Windows Store, there's a msi installer file for it. You can download it on the [latest release page in github](https://github.com/leandrocm86/lanpush/releases/latest). This installation has a built-in Java JRE and makes a shortcut to launch the app.

- <ins>JAR file (any system):</ins>&nbsp;&nbsp;If you prefer to save a few megabytes and run LANPUSH's GUI client with your own JRE (Java 17 required), simply download the JAR file from the [latest release](https://github.com/leandrocm86/lanpush/releases/latest) and run it with
```
java -jar lanpush-gui.x.x.x.jar
```

#### LANPUSH is also available for CLI servers and phones with Android.
For more information about the CLI client, [check its repository](https://github.com/leandrocm86/lanpush-cli/)<br>
Android client: Download  the [app on GooglePlay](https://play.google.com/store/apps/details?id=lcm.lanpush), or check the [project on github](https://github.com/leandrocm86/lanpush-android)<br>
All projects are free, open sourced and open to suggestions.
