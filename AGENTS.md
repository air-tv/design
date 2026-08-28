# Air TV design app

- This repository is an offline Android TV UI laboratory. Do not add networking, databases, dependency injection, or production data loading. The player route may use the real Air backend only against the committed copyright-free local corpus asset so video/overlay/focus behavior is testable without provider data.
- Use `JetStreamCompose` and `TvMaterialCatalog` as the component reference, and Google's JetFit Figma/case study as the sidebar and Challenge-details composition reference.
- Do not use image generation or generic UI-design skills. Make UI decisions directly in Compose and verify them on `air-tv-api36`.
- Media artwork remains deterministic color/gradient geometry until real application data integration begins.
- Mock records and playback state must instantiate the public models from the sibling `stremio-addon-client`, `iptv`, and `video` KMP builds. Do not recreate protocol/domain/player models inside this repository.
- Every URL in design fixtures must use the reserved `.invalid` domain. Never put provider credentials, addon URLs, tokens, or copied production payloads in this repository.
- `air-player-test.mkv` is generated test media, not application content. Keep playback offline; never replace it with a provider or addon URL.
- The sibling builds are included through Gradle composite substitution; Maven coordinates remain `com.getair:stremio-addon-client`, `com.getair:iptv`, and `com.getair:video` so the app can switch to published artifacts without source changes.
- Preserve D-pad focus, Back behavior, 3dp focus outlines, fixed focus scale, 5% safe margins, and Material 3 for TV components.
- Profiles, Settings, Home, IPTV Live/Series/Movies, and the polymorphic Info screen must remain reachable in the static app.
- Run `./gradlew :app:assembleDebug` after changes and install on the Android TV emulator for visual work.
