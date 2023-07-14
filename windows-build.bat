set version=2.0
REM It's necessary to install wix311.exe first: https://github.com/wixtoolset/wix3/releases
jpackage --input target/ --name LANPUSH --app-version %version% --vendor leandrocm86 --main-jar lanpush-jar-with-dependencies.jar --main-class lcm.lanpush.Lanpush --icon src/main/resources/lanpush.ico --win-shortcut --win-menu --type msi