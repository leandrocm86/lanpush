# LANPUSH
### Share texts through LAN


LANPUSH is a utility to exchange text messages between devices in the same local network.<br>
**No internet required! No login!** Just send a message and all configured devices in the same LAN will read it.

#### Use cases:
- Share a link between PC and phone:
Tired of copying/pasting (and copying/pasting again) online notes to share links or texts between your devices?
LANPUSH allows you to quickly move from one device to another.
Once a message is sent, all PCs and Android phones on the same wifi will be able to show a notification with the new message, along with the option to copy it or browse it.

- Server automation:
With LANPUSH your server can easily send you warnings even when no internet is available.
You can also go the other way around, sending commands for the server to read and start your tasks.
Tired of going through SSH everytime just to start trivial jobs? Make your server act upon the messages it receives. You can configure lanpush to log messages on a predefined file, and also choose IPs and ports to listen to.

#### LANPUSH is available for PCs with Java (Windows/Linux/Mac) and phones with Android.
For the Android app, check the project on https://github.com/leandrocm86/lanpush-android<br>
Both projects are free, open sourced, have no ads, and are open to suggestions.

#### Usage:

The downloadable ZIP file contains a JAR file and an INI file.
The INI file is a text file that you can edit to configure LANPUSH as you like. All the options and their defaults are described below.
The JAR file is the executable to start the program (Java JRE is necessary). There are two ways to use it:
 - Via Graphical Interface: Just double click the JAR file and the LANPUSH window will open. There will be shown any message that is sent in the same network (IPs and ports are configurable in the INI file). There is also an input text field for sending texts.
 - Via CLI: Execute the JAR file with a "--listen" parameter to start listening the network (IPs and ports are configurable in the INI file). Any different parameter will be treated as a message to be sent. Example: ```java -jar lanpush.jar 'Hello World!'```

##### Options:
There are currently a total of 14 options to customize LANPUSH on the lanpush-cfg.ini file. The file must me kept on the same folder with the JAR. The options are:

- *connection.send_to_ips*: IPs to which notifications will be sent, separated by colons. It can be left empty for "receive-only" usage. By default, it will try to broadcast to 192.168.0.255, but you will probably want to change this.
- *connection.port*: UDP port from which the device should listen for messages. By default, it listens on 1050.
- *connection.exit_on_receive*: Makes the program exits when receiving the first message. It's off by default.
- *log.file.enabled*: Indicates if logs should be saved in a file. May be useful to detect problems or to have a message history. It's on by default.
- *log.file.path*: Path to log file (if above configuration is on). If empty, it will be saved in the same folder as the application.
- *log.output_to_console*: By default, when using LANPUSH on terminal, only received messages are printed in standard output. If this option is on, log messages will also be printed on console.

The configurations below only affect Graphical Interface usage:

- *gui.window.width*: Width of the program window.
- *gui.window.height*: Height of the program window.
- *gui.font.size*: Font size for messages and buttons (may be useful to change on high-dpi displays).
- *gui.start_in_tray*: Indicates if the program should appear in system tray. It's on by default.
- *gui.max_message_length_display*: Maximum number of characters to be displayed for each message. The default is 50. If messages are longer than this, they'll be shown truncated with a "(...)" at the end. Only affects presentation, not the content to be copied or browsed.
- *gui.auto_message*: Indicates whether the application should notify even messagens sent by itself. It's off by default.
- *gui.onreceive.restore*: Restores the application window when a message is received. It's off by default.
- *gui.onreceive.notification*: Shows a small fading notification with received messages. It's on by default.
