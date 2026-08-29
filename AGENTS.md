# Air TV design app

- This repository is an offline Android TV fixture and interaction host. Canonical presentation models, screens, focus policy, theme, and player controls live in `../app/shared`; never copy or fork them back into this repository. Keep design source limited to fixture construction, host activities, and test adapters/resources.
- Do not add networking, databases, dependency injection, or production data loading. The player adapter may use the real Air backend only against the committed copyright-free local corpus asset so video/overlay/focus behavior is testable without provider data.
- Use `JetStreamCompose` and `TvMaterialCatalog` as the component reference, and Google's JetFit Figma/case study as the sidebar and Challenge-details composition reference.
- Do not use image generation or generic UI-design skills. Make UI decisions directly in Compose and verify them on `air-tv-api36`.
- Media artwork remains deterministic color/gradient geometry until real application data integration begins.
- Mock records and playback state must instantiate the public models from the sibling `air`, `stremio-addon-client`, `iptv`, and `video` KMP builds. Household profiles/settings use Air core contracts; do not recreate protocol/domain/player/settings models inside this repository.
- Every URL in design fixtures must use the reserved `.invalid` domain. Never put provider credentials, addon URLs, tokens, or copied production payloads in this repository.
- `air-player-test.mkv` is generated test media, not application content. Keep playback offline; never replace it with a provider or addon URL.
- The canonical UI is consumed as `com.getair:air-app-shared` through an explicit `includeBuild("../app")` substitution. The protocol/core/player sibling builds retain coordinates `com.getair:air`, `com.getair:stremio-addon-client`, `com.getair:iptv`, and `com.getair:video` so the fixture host can switch to published artifacts without source changes.
- Preserve D-pad focus, Back behavior, 3dp focus outlines, fixed focus scale, 5% safe margins, and Material 3 for TV components.
- Player option panels use the exact Air audio/subtitle/video track contracts and restore focus to the trigger on selection or Back. Labels must follow confirmed native selection events; never flash an optimistic track. Plain live shows EPG/up-next information and never renders seek controls, while DVR may use the shared seekable-live timeline.
- Profiles, Settings, Home, IPTV Live/Series/Movies, and the polymorphic Info screen must remain reachable in the static app.
- Run `./gradlew :app:assembleDebug` after changes and install on the Android TV emulator for visual work.
