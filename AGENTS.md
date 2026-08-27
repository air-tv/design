# Air TV design app

- This repository is a static Android TV UI laboratory. Do not add networking, databases, playback engines, dependency injection, or production domain behavior.
- Use `JetStreamCompose` and `TvMaterialCatalog` as the component reference, and Google's JetFit Figma/case study as the sidebar and Challenge-details composition reference.
- Do not use image generation or generic UI-design skills. Make UI decisions directly in Compose and verify them on `air-tv-api36`.
- Media artwork remains deterministic color/gradient geometry until real application data integration begins.
- Preserve D-pad focus, Back behavior, 3dp focus outlines, fixed focus scale, 5% safe margins, and Material 3 for TV components.
- Profiles, Settings, Home, IPTV Live/Series/Movies, and the polymorphic Info screen must remain reachable in the static app.
- Run `./gradlew :app:assembleDebug` after changes and install on the Android TV emulator for visual work.
