# WikiFont

WikiFont is an Android application created as part of the **Desarrollo Avanzado de Software** course at UPV/EHU. Its goal is to catalogue drinking fountains around different towns. The app lets you:

- Browse towns and see their fountains.
- Add, edit or delete fountain information.
- View locations on a map and save a fountain as a notification.
- Persist data locally using Room with an initial import from a CSV file.

## Building

Download the .apk generated from **Android Studio** to test the app.

Alternatively, open the project in **Android Studio** and let it import the Gradle settings automatically. From there you can run or debug the app on an emulator or device.

## Documentation

The full project report is available in Spanish at `Documentación/memoria.tex`.

## API

The `php` folder contains simple backend scripts used for authentication and
profile management. They are designed to run on the provided MySQL server and
expect to be deployed at `http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/odiez016/WEB/`.

My user is `Xxodiez016` and the password is `1pUQN3Vut`.

## License

This project is licensed under the terms of the [MIT License](LICENSE)
