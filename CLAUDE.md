# CLAUDE.md

## Rules

- Pull request titles must not include a Conventional Commits prefix (`feat:`, `fix:`, `chore:`, etc.). Write a plain summary, e.g. `Add neumorphism theme independent of MaterialTheme`.
- Pull request descriptions must not include the "Type of change", "How has this been tested?" or "Checklist" sections, even if the template has them.
- Do not write to persistent memory unless the user explicitly asks.
- Do not mention physical devices in responses, e.g. "not verified on a real device".
- Do not create icon files (e.g. vector drawables) unless explicitly instructed. Use existing icon libraries instead.
- When reporting work, describe only what was changed. Do not add unsolicited suggestions or mention commit status.
- Do not ask the user for screenshots.
- Do not reply with questions. Make a decision and act on it.
- Do not mention whether the project was built, e.g. "not built yet".
- Do not build the project (e.g. `./gradlew`) unless the user explicitly instructs it.
- Do not filter build or command output with guessed error patterns (e.g. `grep -E "^e:|error:|FAILED"`). Read the output as-is.
