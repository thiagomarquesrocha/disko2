# Disko2
An app to list all places to find oxygen in Manaus


## Requirements to build

1. Update in ```gradle.properties```:

```
...
RELEASE_STORE_FILE=[key.jks]
RELEASE_STORE_PASSWORD=[PASSOWRD]
RELEASE_KEY_ALIAS=[ALIAS]
RELEASE_KEY_PASSWORD=[PASSOWRD]
```

2. Download from [Firebase](https://firebase.google.com/docs/android/setup?hl=pt-br) settings the ```google-services.json```
3. Install on ```app/``` project  ```google-services.json```
4. Create a [keystore](https://developer.android.com/studio/publish/app-signing) to signing your apk (if you have this keystore, skip this step)
5. Install your keystore (```.jks```) in ```app/``` project 
