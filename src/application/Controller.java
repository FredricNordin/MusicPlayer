package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;


public class Controller implements Initializable{
	
	@FXML
	private Pane pane;
	@FXML
	private Label songLabel;
	@FXML
	private Button playButton, pauseButton, resetButton, previousButton, nextButton;
	@FXML
	private ComboBox<String> speedBox;
	@FXML
	private Slider volumeSlider;
	@FXML
	private ProgressBar songProgressBar;
	
	private Media media;
	private MediaPlayer mediaPlayer;
	private File directory;
	private File[] files;
	private ArrayList<File> songs;
	
	private int songNumber;
	private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};
	
	private Timer timer;
	private TimerTask task;
	private boolean running;

	// Fill array with songs in "music" folder.
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		songs = new ArrayList<File>();
		
		directory = new File("music");
		
		files = directory.listFiles();
		
		if(files != null) {
			
			for(File file : files) {
				
				songs.add(file);
				System.out.println(file);
			}
		}
		
		media = new Media(songs.get(songNumber).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		songLabel.setText(songs.get(songNumber).getName());
		
		
		for(int i = 0; i < speeds.length; i++) {
			
			speedBox.getItems().add(Integer.toString(speeds[i]));
		}
		
		speedBox.setOnAction(this::changeSpeed);
		
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
				
			}
		});
	}
	
	// When song starts playing.
	public void playMedia() {
		beginTimer();
		changeSpeed(null);
		mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
		mediaPlayer.play();
	}
	
	// When song is paused.
	public void pauseMedia() {
		cancelTimer();
		mediaPlayer.pause();
	}

	// When reset btn is pressed.
	public void resetMedia() {
		songProgressBar.setProgress(0);
		mediaPlayer.seek(Duration.seconds(0));
	}

	// Go back one song.
	public void previousMedia() {
	
		if(songNumber > 0) {
			songNumber--;
			mediaPlayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			playMedia();
			
		} else {
			songNumber = songs.size() -1;
			mediaPlayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			playMedia();
		}
	}

	// Next song.
	public void nextMedia() {
	
		if(songNumber < songs.size() -1) {
			songNumber++;
			mediaPlayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			playMedia();
			
		} else {
			songNumber = 0;
			mediaPlayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			playMedia();
		}
	}

	// Change song playback speeds.
	public void changeSpeed(ActionEvent event) {
		if(speedBox.getValue() == null) {
			mediaPlayer.setRate(1);
		} else {
			mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
		}
	}

	// Timer for progressBar to display.
	public void beginTimer() {
		timer = new Timer();
		task = new TimerTask() {
			
			public void run() {
				running = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				songProgressBar.setProgress(current/end);
				
				if(current/end == 1) {
					cancelTimer();
				}
			}
		};
		timer.scheduleAtFixedRate(task, 0, 1000);
	}

	// Pause when stopped.
	public void cancelTimer() {
		running = false;
		timer.cancel();
	}

}
