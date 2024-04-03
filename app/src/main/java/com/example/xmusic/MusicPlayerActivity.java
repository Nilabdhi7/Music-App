package com.example.xmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {
    TextView tittleTextView ,currentTimeTextView,totalTimeTextView;
    SeekBar seekBar;
    ImageView bigIcon, previousButton,pausePlayButton,nextButton,backButton;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x=0;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        tittleTextView = findViewById(R.id.song_title);
        currentTimeTextView = findViewById(R.id.current_time);
        totalTimeTextView = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seekbar);
        bigIcon = findViewById(R.id.music_icon_big);
        previousButton = findViewById(R.id.previous);
        pausePlayButton = findViewById(R.id.pause_play);
        nextButton = findViewById(R.id.next);
        backButton = findViewById(R.id.back_icon);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicPlayerActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });



        tittleTextView.setSelected(true);


        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();


        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTextView.setText(convertToMS(mediaPlayer.getCurrentPosition()+""));

                    if (mediaPlayer.isPlaying()){
                        pausePlayButton.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                        bigIcon.setRotation(x++);
                    }else{
                        pausePlayButton.setImageResource(R.drawable.baseline_play_circle_outline_24);
                        bigIcon.setRotation(0);

                    }

                }
                new Handler().postDelayed(this,180);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    void setResourcesWithMusic(){
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        tittleTextView.setText(currentSong.getTitle());
        totalTimeTextView.setText(convertToMS(currentSong.getDuration()));

        pausePlayButton.setOnClickListener(v-> pausePlay());
        nextButton.setOnClickListener(v-> playNext());
        previousButton.setOnClickListener(v-> playPrevious());
        playMusic();

    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void playNext(){

        if (MyMediaPlayer.currentIndex==songsList.size()-1)
            return;
        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPrevious(){

        if (MyMediaPlayer.currentIndex==0)
            return;
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else{
            mediaPlayer.start();
        }
    }


    public static String convertToMS(String duration){

        long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) %TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) %TimeUnit.MINUTES.toSeconds(1));

    }

}

