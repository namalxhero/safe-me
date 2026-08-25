# Safe Me

Private chat app. Phase 3 build: phone login + real-time text chat via Firebase.
Encryption (Phase 4), groups (Phase 5), and images via Cloudinary (Phase 6) come next.

## One-time setup after cloning this repo

1. Copy the `google-services.json` file you downloaded from Firebase console
   into the `app/` folder, right next to `app/build.gradle.kts`.
   Final path must be: `app/google-services.json`

2. Push to GitHub (from Termux):
   ```
   git add .
   git commit -m "Phase 3: basic chat"
   git push origin main
   ```

3. Go to the repo's **Actions** tab on GitHub (or GitHub mobile app) —
   the "Build APK" workflow runs automatically and produces a downloadable
   debug APK under the workflow run's **Artifacts** section.

## Project structure

- `app/src/main/java/com/nipuna/safeme/data/` — Firebase Auth + Firestore helpers, data models
- `app/src/main/java/com/nipuna/safeme/ui/` — Compose screens (Login, ChatList, Chat)
- `app/src/main/java/com/nipuna/safeme/MainActivity.kt` — navigation between screens
- `.github/workflows/build.yml` — CI that builds the APK on every push

## Firestore structure

```
users/{uid}          -> name, phone, publicKey
chats/{chatId}        -> title, members[], lastMessage, lastTimestamp
chats/{chatId}/messages/{msgId} -> senderId, text, timestamp
```
