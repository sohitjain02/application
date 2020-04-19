1. On import Eclipse may give warning for Restriction on required library. To solve this follow below steps.

Eclipse, by default, disallows access to classes in jar files in the jre/lib/ext directory, as these are not guaranteed to be present on all Java platforms. If you are writing a JavaFX 8 application, you are assuming you are on a platform where jfxrt.jar is available in the lib/ext location.
So the minimal** fix for this is to allow access to the classes in this jar file (and only in this jar file). To do this, right-click on the project and bring up the project properties dialog. Select "Build Path" in the left pane, and select the "Libraries" tab. You will see a "JRE System Library" entry. Expand that entry, and you will see an "Access Rules" subentry:
Select the "Access Rules" entry and click "Edit". Click "Add".
Under "Resolution", choose "Accessible", and under "Rule Pattern", enter javafx/**:
Click OK to exit all the dialogs.
This setting will allow access to all the classes in any packages beginning javafx., but will preserve the rule on the ext folder for all other classes.

2. How to change name, email, version number and title for About Me page?
Under Help->About in the applcation, there is configurable name and email set. To set it to your name, follow below steps:
1. In project, Go to /cs1302-gallery/src/main/java/cs1302/gallery/GalleryAppConstants.java file.
2. Update your name, email and version under below constants:
	public static final String MY_NAME = "Sohit Jain";
	public static final String MY_EMAIL = "sohitjain02@gmail.com";
	public static final String MY_APPLICATION_VERSION = "1.0";