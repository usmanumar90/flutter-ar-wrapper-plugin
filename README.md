# YEAR BOOK

Year Book is based on ARcore and flutter. It is using augmented Image technology and plays video based on image recognition.

# Getting Started

  - Setup your Flutter environment as per documents https://flutter.dev/docs/get-started/install
  - Download or clone repo and open with Android Studio
  - Run gradle sync from Android Studio to get dependancy updates
  - Run Command 'flutter packages get'
  - Add your images in "flutter-ar-wrapper-plugin-kotlin\android\src\main\images" folder
  - Add your videos in "flutter-ar-wrapper-plugin-kotlin\android\src\main\videos" folder
  - put your references in file "flutter-ar-wrapper-plugin-kotlin\android\src\main\kotlin\com\smartminds\Constants.kt" as below sample:

```sh
 val data_map = hashMapOf(
            "images/test_image_1.jpg" to "videos/test_video_1.mp4",
            "images/test_image_2.jpg" to "videos/test_video_2.mp4",
            "images/test_image_3.jpg" to "videos/test_video_3.mp4"
    )
```

Connect your AR Supported device and run from Android Studio.