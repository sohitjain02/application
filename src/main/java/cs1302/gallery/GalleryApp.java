package cs1302.gallery;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/** 
 * Represents an iTunes GalleryApp!
 */
public class GalleryApp extends Application {

	private static List<String> imagesList = new ArrayList<>();
	
	/** {@inheritDoc} 
	 * @throws Exception */
    @Override
    public void start(Stage primaryStage) throws Exception {
		
    	BorderPane root = new BorderPane(); // root node of the scene graph
		
		Scene scene = new Scene(root, 200, 300);
		
		/* Border pane has 3 regions 
		 * 1. Menu Bar at the top
		 * 2. Vbox in centre having main content area(Play/Pause button, Search Text and Update Image button and Images Grid)
		 * 3. ProgressBar and Courtesy text at the bottom
		 * 
		 */
		MenuBar menubar = new MenuBar();
		VBox vBox = new VBox();
		HBox progressBar = new HBox();
		
		root.setTop(menubar);
        root.setCenter(vBox);
        root.setBottom(progressBar);
        
		/* 
		 * Menu Bar Items Starts 
		 */
        /* 1. File Menu */
        Menu fileMenu = new Menu(GalleryAppConstants.FILE_MENU_TITLE);    
        MenuItem exit = new MenuItem(GalleryAppConstants.EXIT_MENU_ITEM);
        exit.setId(GalleryAppConstants.EXIT_MENU_ITEM_ID);
        fileMenu.getItems().addAll(exit); 
        
        /* 2. Theme Menu */
        Menu theme = new Menu(GalleryAppConstants.THEME_MENU_TITLE);
        ToggleGroup themeToggle = new ToggleGroup();
        RadioMenuItem defaultTheme = new RadioMenuItem(GalleryAppConstants.THEME_MENU_ITEM_DEFAULT);
        defaultTheme.setId(GalleryAppConstants.THEME_MENU_ITEM_DEFAULT_ID);
        defaultTheme.setToggleGroup(themeToggle);
        defaultTheme.setSelected(true);
        RadioMenuItem theme1 = new RadioMenuItem(GalleryAppConstants.THEME_MENU_ITEM1);
        theme1.setId(GalleryAppConstants.THEME_MENU_ITEM1_ID);
        theme1.setToggleGroup(themeToggle);
        RadioMenuItem theme2 = new RadioMenuItem(GalleryAppConstants.THEME_MENU_ITEM2);
        theme2.setId(GalleryAppConstants.THEME_MENU_ITEM2_ID);
        theme2.setToggleGroup(themeToggle);
        theme.getItems().addAll(defaultTheme, theme1, theme2);
        
        /* 3. Theme Menu */
        Menu help = new Menu(GalleryAppConstants.HELP_MENU_TITLE);
        MenuItem about = new MenuItem(GalleryAppConstants.ABOUT_MENU_ITEM);
        about.setId(GalleryAppConstants.ABOUT_MENU_ITEM_ID);
        help.getItems().addAll(about);
        
        menubar.getMenus().addAll(fileMenu, theme, help);
        
        exit.setOnAction(setMenuItemsEvent(scene)); //action for File->exit menu
        defaultTheme.setOnAction(setMenuItemsEvent(scene)); //action for Theme -> Default menu
        theme1.setOnAction(setMenuItemsEvent(scene)); //action for Theme -> Theme1 menu
        theme2.setOnAction(setMenuItemsEvent(scene)); //action for Theme -> Theme2 menu
        about.setOnAction(setMenuItemsEvent(scene)); //action for Help -> About menu
        /* Menu Bar Items Ends */
        
        /* Progress Bar Start */
        ProgressBar progress = new ProgressBar(0); 
        
        Text courtesy = new Text();  
        courtesy.setText(GalleryAppConstants.COURTESY_TEXT);
        courtesy.setId(GalleryAppConstants.COURTESY_TEXT_ID);
        
        progressBar.getChildren().addAll(progress, courtesy);
        progressBar.setSpacing(15);
        /* Progress Bar Ends */
        
        /* Buttons and Search Bar Start*/
        HBox searchHandler = new HBox();
        Button play = new Button(GalleryAppConstants.PLAY);
        play.setDisable(true);
        play.setId(GalleryAppConstants.PLAY_BUTTON_ID);
        
        Separator sepVert = new Separator();
        sepVert.setOrientation(Orientation.VERTICAL);
        sepVert.setValignment(VPos.CENTER);
        
        Text searchText = new Text();  
        searchText.setText(GalleryAppConstants.SEARCH_LABEL_TEXT);
        searchText.setId(GalleryAppConstants.SEARCH_LABEL_TEXT_ID);
        
        TextField searchField = new TextField();
        searchField.setPromptText(GalleryAppConstants.DEFAULT_QUERY);
        searchField.setDisable(true);
        
        Button updateImages = new Button(GalleryAppConstants.UPDATE_IMAGES_BUTTON_TEXT);
        updateImages.setDisable(true);
        updateImages.setId(GalleryAppConstants.UPDATE_IMAGES_BUTTON_ID);
        
        searchHandler.getChildren().addAll(play, sepVert, searchText, searchField, updateImages);
        searchHandler.setSpacing(15);
        searchHandler.setPadding(new Insets(10, 10, 10, 10));
        /* Buttons and Search Bar End*/
        
        /* Images Grid Start*/
        GridPane imagesGrid = new GridPane();
        imagesGrid.setPadding(new Insets(10, 10, 10, 10));
        /* Images Grid End */
        
        vBox.getChildren().addAll(searchHandler, imagesGrid);
        
        /* Load Initial Images as per default query (Start)*/
        LoadImagesTask task = new LoadImagesTask(GalleryAppConstants.DEFAULT_QUERY);
    	progress.setProgress(0);
        progress.progressProperty().unbind();
        progress.progressProperty().bind(task.progressProperty());
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                List<String> imageUrls = task.getValue();
                GalleryApp.imagesList.addAll(imageUrls);
                play.setDisable(false);
				updateImages.setDisable(false);
				searchField.setDisable(false);
				searchField.setPromptText(GalleryAppConstants.DEFAULT_QUERY);
                if (GalleryApp.imagesList.size() >= 21) {
                	imagesGrid.getChildren().clear();
					updateImageGridPane(imagesGrid);
				}
            	task.cancel(true);
	            progress.progressProperty().unbind();
            }
        });
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
				Alert alert = new Alert(AlertType.ERROR);
			    alert.setTitle(GalleryAppConstants.ALERT_TITLE);
			    alert.setContentText(GalleryAppConstants.IMAGE_LOAD_FAILURE_MESSAGE);
			    Optional<ButtonType> result = alert.showAndWait();
			    if (result.get() == ButtonType.OK) {
			    	task.cancel(true);
			    	Platform.exit();
			    	System.exit(0);
			    }
            }
        });
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
        /* Load Initial Images as per default query (End)*/
        
        /* Refresh Image Thread using Timeline (Start) */
        EventHandler<ActionEvent> handler = createImageRefreshEvent(imagesGrid);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(keyFrame);
		/* Refresh Image Thread using Timeline (End) */
		
        play.setOnAction(setActionEventForPlay(timeline)); //play Button Action
        
        updateImages.setOnAction(setActionEventForUpdateImages(searchField, imagesGrid, progress, play, timeline)); //updateImages action
        
        primaryStage.setMaxWidth(1280);
        primaryStage.setMaxHeight(720);
        primaryStage.setMinWidth(580);
        primaryStage.setMinHeight(580);
        primaryStage.setWidth(580);
        primaryStage.setHeight(580);
        primaryStage.setTitle(GalleryAppConstants.APPLICATION_TITLE);
        primaryStage.getIcons().add(new Image(GalleryAppConstants.APPLICATION_ICON_PATH));
        primaryStage.setScene(scene);  
        primaryStage.show();
    } // start
    
    /**
     * This method is responsible for defining the action event for all menu items.
     * These include:
     * 1. File -> Exit (Close the Application)
     * 2. Theme -> Default (Sets default theme for application)
     * 3. Theme -> Theme1 (Sets theme1.css for application)
     * 4. Theme -> Theme2 (Sets theme2.css for application)
     * 5. Help -> About (Displays About me Information)
     * @param scene Menus present in this scene
     * @return EventHandler<ActionEvent> Represents Event handler for all menus and menu items
     */
    private EventHandler<ActionEvent> setMenuItemsEvent(Scene scene) {
    	EventHandler<ActionEvent> menuItemEvent = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
            	MenuItem menuItem = (MenuItem)e.getSource();
                if (menuItem.getId().equalsIgnoreCase(GalleryAppConstants.EXIT_MENU_ITEM_ID)) { //exit menu
                	Platform.exit();
                    System.exit(0);
                } else if (menuItem.getId().equalsIgnoreCase(GalleryAppConstants.THEME_MENU_ITEM_DEFAULT_ID)) { //default theme menu
                	scene.getStylesheets().remove(0, scene.getStylesheets().size());
                } else if (menuItem.getId().equalsIgnoreCase(GalleryAppConstants.THEME_MENU_ITEM1_ID)) { //theme1 menu
                	scene.getStylesheets().remove(0, scene.getStylesheets().size());
                	scene.getStylesheets().add("theme1.css");
                } else if (menuItem.getId().equalsIgnoreCase(GalleryAppConstants.THEME_MENU_ITEM2_ID)) { //theme2 menu
                	scene.getStylesheets().remove(0, scene.getStylesheets().size());
                	scene.getStylesheets().add("theme2.css");
                } else if (menuItem.getId().equalsIgnoreCase(GalleryAppConstants.ABOUT_MENU_ITEM_ID)) { //about menu
                	createAboutMeStage();
                }
            } 
        };
        return menuItemEvent;
    }
    
    /**
     * This method is responsible for defining the action event for the Play/Pause button.
     * When enabled and instruction is Play, then it will start the imageRefresh event which will refresh the 
     * image in the image grid at random position in every 2 seconds.
     * When enabled and instruction is Pause, it will stop the imageRefresh event.
     * 
     * @param timeline Timeline object created in start method which has event for imageRefresh associated with it.
     * @return EventHandler<ActionEvent> Represents action event for Play/Pause button
     */
    private EventHandler<ActionEvent> setActionEventForPlay(Timeline timeline) {
    	EventHandler<ActionEvent> playButtonEvent = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
            	Button buttonNode = (Button) e.getSource();
                if (buttonNode.getId().equalsIgnoreCase(GalleryAppConstants.PLAY_BUTTON_ID)) {
                	if (buttonNode.getText().equalsIgnoreCase(GalleryAppConstants.PLAY)) {
                		buttonNode.setText(GalleryAppConstants.PAUSE);
                		timeline.play();
                	} else {
                		buttonNode.setText(GalleryAppConstants.PLAY);
                		timeline.stop();
                	}
                }
            } 
        };
        return playButtonEvent;
    }
    
    /**
     * 
     * This method is responsible for updating the images in the grid based on the query entered by user. 
     * This event will initiate a background call to Task that is {@Code LoadImagesTask} which will query the iTunes search API
     * for searchText and update the images in the imagesGrid. If the imageRefresh event is in play mode it will stop that event.
     * 
     * 
     * @param searchText Represent text entered by user in searchQuery text field in application.
     * @param imagesGrid Images Grid present scene in main content area of application.
     * @param progress Progress Bar to show progress of image update in grid when query is made
     * @param play Play button present in scene
     * @param timeline Timeline thread for image refresh associated with play button
     * @return EventHandler<ActionEvent> Represents action event for update Images button
     */
    private EventHandler<ActionEvent> setActionEventForUpdateImages(TextField searchText, GridPane imagesGrid, 
    		ProgressBar progress, Button play, Timeline timeline) {
    	EventHandler<ActionEvent> playButtonEvent = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
            	Button buttonNode = (Button) e.getSource();
            	if (null != searchText.getText() && searchText.getText().length() > 0) {
	                if (buttonNode.getId().equalsIgnoreCase(GalleryAppConstants.UPDATE_IMAGES_BUTTON_ID)) {
	                	play.setDisable(true); //disable play button
	                	buttonNode.setDisable(true); //disable updateImages button
	                	if (Status.RUNNING.equals(timeline.getStatus())) { //stop the tieline for image refresh if it is running
	                		timeline.stop();
	                	}
	                	LoadImagesTask task = new LoadImagesTask(searchText.getText());
	                	progress.setProgress(0);
	                    progress.progressProperty().unbind();
	                    progress.progressProperty().bind(task.progressProperty());
	                    task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent t) {
                                List<String> imageUrls = task.getValue();
					            progress.progressProperty().unbind();
                                play.setDisable(false);
                                play.setText(GalleryAppConstants.PLAY);
        	                	buttonNode.setDisable(false);
                                if (imageUrls.size() >= 21) {
                                	GalleryApp.imagesList.clear();
                                	GalleryApp.imagesList.addAll(imageUrls);
                                	imagesGrid.getChildren().clear();
    								updateImageGridPane(imagesGrid);
    								task.cancel(true);
    							} else {
    								Alert alert = new Alert(AlertType.ERROR);
    							    alert.setTitle(GalleryAppConstants.ALERT_TITLE);
    							    alert.setContentText(GalleryAppConstants.ALERT_MESSAGE);
    							    alert.showAndWait();
    							}
                                task.cancel(true);
                            }
                        });
	                    task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent t) {
        						Alert alert = new Alert(AlertType.ERROR);
        					    alert.setTitle(GalleryAppConstants.ALERT_TITLE);
        					    alert.setContentText(GalleryAppConstants.IMAGE_LOAD_FAILURE_MESSAGE);
        					    Optional<ButtonType> result = alert.showAndWait();
        					    if (result.get() == ButtonType.OK) {
        					    	task.cancel(true);
        					    	Platform.exit();
        					    	System.exit(0);
        					    }
                            }
	                    });
	                    Thread t = new Thread(task);
	                    t.setDaemon(true);
	                    t.start();
	                }
            	}
            } 
        };
        return playButtonEvent;
    }
    
    /**
     * This method is responsible for updating the imagesGrid (passed as parameter)
     * by selecting 20 random images from the list of images returned by {@code LoadImagesTask}
     * 
     * @param imagesGrid
     */
    private static void updateImageGridPane(GridPane imagesGrid) {
    	int count = 1; //counter for total images
    	int i = 0; //counter for columns
        int j = 0; //counter for rows
        while (count <= 20) {
        	Random rand = new Random();
        	String imageURL = GalleryApp.imagesList.get(rand.nextInt(GalleryApp.imagesList.size()));
			try {
				URL url = new URL(imageURL);
				Image image = new Image(url.openStream()); //load image from image URL and create Image object from that
				ImageView imageView = new ImageView(image);
	            imagesGrid.add(imageView, i, j); //add ImageView to grid at column 'i' and row 'j'
	            i++;
	            if (i == 5) { //4*5 grid of images
	            	i = 0;
	            	j++;
	            }
	            count++;
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    /**
     * This method is defining event for KeyFrame class object.
     * This will select the image randomly from {@code GalleryApp} imagesList
     * and update in the images grid at random position.
     * 
     * @param imagesGrid GridPane used in Scene of our application
     * @return EventHandler<ActionEvent> Event Handler for KeyFrame
     * 
     */
    private EventHandler<ActionEvent> createImageRefreshEvent(GridPane imagesGrid) {
    	EventHandler<ActionEvent> imageRefreshEvent = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
            	Random rand = new Random();
            	int i = rand.nextInt(5); //randomly select column number in grid of image to update
            	int j = rand.nextInt(4); //randomly select row number in grid of image to update
            	String imageURL = GalleryApp.imagesList.get(rand.nextInt(GalleryApp.imagesList.size())); //random selection of image from all image URL's
    			try {
    				URL url = new URL(imageURL);
    				Image image = new Image(url.openStream());
    				ImageView imageView = new ImageView(image);
    				for(Node imageNode : imagesGrid.getChildren()) { // Loop to traverse the to the node to be updated
    				    if(imageNode instanceof ImageView && GridPane.getRowIndex(imageNode) == j && GridPane.getColumnIndex(imageNode) == i) {
    				    	imagesGrid.getChildren().remove(imageNode); // remove node at i'th column and j'th row 
    				        break;
    				    }
    				}
    	            imagesGrid.add(imageView, i, j); //update new image in grid
    			} catch (IOException ex) {
    				ex.printStackTrace();
    			}
            }
        };
        return imageRefreshEvent;
    }
    
    /**
     * Method to render About Me stage on clicking the Help -> About Menu
     */
    private void createAboutMeStage() {
    	Stage aboutSatge = new Stage();
    	aboutSatge.initModality(Modality.APPLICATION_MODAL);
    	VBox modalroot = new VBox();
    	Image myImage = new Image(GalleryAppConstants.MY_IMAGE_PATH);
    	ImageView myImageNode = new ImageView(myImage);
    	GridPane grid = new GridPane(); 
    	Text name = new Text();
    	name.setText(GalleryAppConstants.NAME);
    	Text myName = new Text();
    	myName.setText(GalleryAppConstants.MY_NAME);
    	Text email = new Text();
    	email.setText(GalleryAppConstants.EMAIL);
    	Text myEmail = new Text();
    	myEmail.setText(GalleryAppConstants.MY_EMAIL);
    	Text version = new Text();
    	version.setText(GalleryAppConstants.APPLICATION_VERSION);
    	Text myAppVersion = new Text();
    	myAppVersion.setText(GalleryAppConstants.MY_APPLICATION_VERSION);
    	grid.addRow(0, name, myName);
    	grid.addRow(1, email, myEmail);
    	grid.addRow(2, version, myAppVersion);
    	grid.setAlignment(Pos.CENTER);
    	modalroot.getChildren().addAll(myImageNode, grid);
    	modalroot.setAlignment(Pos.CENTER);
        Scene aboutScene = new Scene(modalroot, 400, 300);
        aboutSatge.setTitle(GalleryAppConstants.ABOUT_MODAL_TITLE+GalleryAppConstants.MY_NAME);
        aboutSatge.setScene(aboutScene);
        aboutSatge.getIcons().add(new Image(GalleryAppConstants.MY_IMAGE_PATH));
        aboutSatge.setMaxWidth(600);
        aboutSatge.setMaxHeight(400);
        aboutSatge.show();
    }
} // GalleryApp
