# Air TV design app

- This repository is a static Android TV UI laboratory. Do not add networking, databases, dependency injection, or production data loading. A player route may host a backend-neutral fake player for control-surface testing.
- Use `JetStreamCompose` and `TvMaterialCatalog` as the component reference, and Google's JetFit Figma/case study as the sidebar and Challenge-details composition reference.
- Do not use image generation or generic UI-design skills. Make UI decisions directly in Compose and verify them on `air-tv-api36`.
- Media artwork remains deterministic color/gradient geometry until real application data integration begins.
- Mock records and playback state must instantiate the public models from the sibling `stremio-addon-client`, `iptv`, and `video` KMP builds. Do not recreate protocol/domain/player models inside this repository.
- Every URL in design fixtures must use the reserved `.invalid` domain. Never put provider credentials, addon URLs, tokens, or copied production payloads in this repository.
- The sibling builds are included through Gradle composite substitution; Maven coordinates remain `com.getair:stremio-addon-client`, `com.getair:iptv`, and `com.getair:video` so the app can switch to published artifacts without source changes.
- Preserve D-pad focus, Back behavior, 3dp focus outlines, fixed focus scale, 5% safe margins, and Material 3 for TV components.
- Profiles, Settings, Home, IPTV Live/Series/Movies, and the polymorphic Info screen must remain reachable in the static app.
- Run `./gradlew :app:assembleDebug` after changes and install on the Android TV emulator for visual work.
