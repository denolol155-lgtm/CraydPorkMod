# Johnabipork (Fabric, Minecraft 1.21.4)

## Installation (fertige .jar)
1. Fabric Loader + Fabric API fuer 1.21.4 muessen installiert sein (wie bei jeder Fabric-Mod).
2. `johnabipork-1.0.0.jar` in den Ordner `.minecraft/mods` ziehen.
3. Minecraft starten, in eine Welt gehen, **Rechte Shift-Taste** druecken -> Menue.

## .jar bauen
**Ohne Installation:** Ordner in ein GitHub-Repo hochladen -> Reiter "Actions" -> "build" -> Artifact `johnabipork-jar` herunterladen.
**Lokal:** JDK 21 + Gradle 8.12 installieren, im Ordner `gradle build` ausfuehren -> `build/libs/johnabipork-1.0.0.jar`.

## Neue Funktion hinzufuegen
1. Klasse in `module/` anlegen, die `Module` erweitert (`render(...)` implementieren).
2. In `ModuleManager.init()` mit `register(new MeinModul());` eintragen.
Das GUI zeigt es automatisch mit eigenem Schalter an.

## Andere Minecraft-Version
Werte in `gradle.properties` aendern (siehe fabricmc.net/develop). Bei anderen Versionen koennen kleine Code-Anpassungen noetig sein.
