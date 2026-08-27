# Air TV Design

A static Android TV design application used to inspect Air's real Compose UI on an emulator or television. It deliberately contains no networking, persistence, playback, image loading, or production business logic.

## Routes

- **Profiles** — household profile selection using TV Material cards.
- **Home** — JetFit Challenge-style featured carousel plus Continue Watching, Stremio movie, and Stremio series rows.
- **IPTV / Live** — channel list, now-playing programme, and XMLTV-style EPG schedule.
- **IPTV / Series** — five-column provider series grid.
- **IPTV / Movies** — five-column provider movie grid.
- **Settings** — JetStream-style two-column settings with Playback, Subtitles & Audio, Appearance, Sources, Advanced, and About sections.
- **Info** — one composition with independent branches for Stremio metadata, IPTV live + EPG, and IPTV VOD/series data.
- **Player** — backend-neutral TV controls for VOD and live playback; VOD has progress/seek treatment while live playback intentionally has no seek bar.

The primary shell uses a fixed, opaque JetFit-derived icon rail with Profiles at the top and Home, IPTV, and Settings centered. IPTV alone keeps a Training-style top tab row for Live, Series, and Movies. Home heroes and Info use the JetFit Challenge-details hierarchy: right-anchored artwork, directional scrims, source/type kicker, three compact facts, and left-aligned actions. Landscape shelves fit four complete cards at 1080p, and Appearance includes a true-black OLED mode.

All media artwork is represented by deterministic color gradients so layout, scrims, focus, scale, and contrast can be evaluated without pretending that generated imagery is production content.

The displayed records instantiate the real normalized contracts from the sibling
Kotlin Multiplatform libraries through Gradle composite substitution. Every
record is fictional, every URL uses `mock.invalid`, and this repository contains
no provider credentials or addon configuration.

## Run

```bash
./gradlew :app:installDebug
adb shell am start -n com.getair.design/.MainActivity
```

Use the canonical `air-tv-api36` AVD documented in the workspace `AGENTS.md`.

## Source basis

The UI structure, theme, typography, focus border, carousel, cards, tab row, lists, switches, dialog conventions, and TV layout behavior are adapted directly from Google's Apache-2.0 licensed Android TV samples:

- [JetStreamCompose](https://github.com/android/tv-samples/tree/main/JetStreamCompose)
- [TvMaterialCatalog](https://github.com/android/tv-samples/tree/main/TvMaterialCatalog)

The sidebar and Challenge-details composition reference Google's CC BY 4.0 JetFit design:

- [JetFit Figma community file](https://www.figma.com/community/file/1237433831695839696/jetfit-fitness-app)
- [JetFit Android TV case study](https://developer.android.com/design/ui/tv/samples/jet-fit)

Copied Inter/Lexend font files and upstream notices are retained in `THIRD_PARTY_LICENSES.md`.
