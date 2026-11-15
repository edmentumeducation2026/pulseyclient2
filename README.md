# UtilClient (Fabric, Minecraft 1.21.1)
Entity ESP, entity info overlay, reach adjustment, flight, freecam, ClickGUI.

Keys (rebind in Options > Controls > UtilClient):
Right Shift = ClickGUI | G = flight | H = freecam | ] / [ = reach +/- 0.5
ClickGUI: drag headers to move, right-click header to collapse, click toggles, drag sliders.
Config: config/utilclient.json

## Build the jar on GitHub
Push this folder to a repo -> Actions tab -> "build" -> download the `UtilClient-jar` artifact
(use the jar without the `-sources` suffix). Put it in mods/ with Fabric Loader + Fabric API.
Local build: JDK 21 + Gradle 8.11+, run `gradle build` -> build/libs/.

## Multiplayer test server notes
- Freecam: client-only, no packets sent, works on any server.
- ESP / info overlay: client-only rendering, works anywhere.
- Flight: set `allow-flight=true` in server.properties or you'll be kicked for flying.
- Reach: the server validates interaction range. On your own test server you can raise it
  with: /attribute <you> minecraft:player.entity_interaction_range base set 6
         /attribute <you> minecraft:player.block_interaction_range base set 6
