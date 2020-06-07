# SmartPCSys

The android application developed, is a simple and concise depiction of mobile ad hoc cloud using UDP Communication. The architecture which is followed in the application design and development is in the following figure;

![Android Application Architecture](https://github.com/hassammughal/SmartPCSys/blob/master/image.png)

To run this application, there are two ways;
1.	In this method, we need to download the android project from the github: https://github.com/hassammughal/SmartPCSys or use the clone option in the android studio and choose the clone link provided at github. After the project is built (automatically), just press the run button in android studio.
2.	If the application is already built, just create the app-debug.apk from Build menu as shown in the following image;

![How to build the apk](https://github.com/hassammughal/SmartPCSys/blob/master/img1.jpg)

After the app is built, you can simply transfer that apk file to the device and install it.

As the application starts, the main activity consisting the main fragment will appear as in the image below; One can touch on the select application button to select the application/task file from the device, and to select the data files one has to press the select data files button. The file name and size are shown in the text views. On touching the submit button, the task is then submitted to the scheduler and scheduler schedules it and finds the suitable device among the devices available. 

![Main Screen](https://github.com/hassammughal/SmartPCSys/blob/master/img2.jpg)

To view the devices, you need to swipe your finger on the screen in left direction. Or just touch the devices tab as shown. The devices is a fragment and it screen looks as the picture below;

 ![Devices List Screen](https://github.com/hassammughal/SmartPCSys/blob/master/img3.jpg)

This screen provides a list of devices that are the part of the mobile ad hoc network. The first list item is the device itself, whereas the next items are the other devices. The time is updated every 2 to 3 seconds. Any device that becomes out of range or closes the application is removed only after the 15 seconds of no packet arrival from that device. The list is scrollable if the number of devices increases.

The next tab is About us which is a simple screen that displays a brief description about this application.

![About Us Screen](https://github.com/hassammughal/SmartPCSys/blob/master/img4.jpg)


The screens that show how on pressing the select application and data files button look are as follows. User can select single or multiple files at a time.
On first time, the permissions will be required, which user has to accept before going in to the file picker. Once the permissions are given, the user will be directed to the file picker on the second touch.
  
![File Attachment](https://github.com/hassammughal/SmartPCSys/blob/master/img5.jpg)
![File Attachment](https://github.com/hassammughal/SmartPCSys/blob/master/img6.jpg)
	Overall app’s functionality is very simple at the moment. However, further changes will be done to make it more interactive and be more user attractive.

