package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Filter;

public class MainActivity extends AppCompatActivity{

    String songName, songUrl;
    ListView listView;
    ArrayList<String> arrayListSongsName = new ArrayList<>();
    ArrayList<String> arrayListSongsUrl = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    MediaPlayer mediaPlayer;
    Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.mylst);
        EditText srch = (EditText) findViewById(R.id.srch);
        ImageButton btn = (ImageButton) findViewById(R.id.play);
        Button btn2 = (Button) findViewById(R.id.next);
        TextView txt = (TextView) findViewById(R.id.musictxt);
        mediaPlayer = new MediaPlayer();

        retrieveSongs();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                listView.getItemAtPosition(i);
                songName = arrayListSongsName.get(i);
                songUrl = arrayListSongsUrl.get(i);
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                        bottomSheetDialog.setContentView(R.layout.bottom_sheet);
                        bottomSheetDialog.setCanceledOnTouchOutside(false);
                        SeekBar seekBar = (SeekBar) bottomSheetDialog.findViewById(R.id.skbar);
                        TextView txtSongName = (TextView) bottomSheetDialog.findViewById(R.id.txtmusic);
                        ImageButton pause = (ImageButton) bottomSheetDialog.findViewById(R.id.pause);
                        if(mediaPlayer.isPlaying()){
                            pause.setBackground(getDrawable(R.drawable.pause));
                        }
                        else{
                            pause.setBackground(getDrawable(R.drawable.play));
                        }
                        txtSongName.setText(songName);

                        pause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(mediaPlayer.isPlaying()){
                                    mediaPlayer.pause();
                                    pause.setBackground(getDrawable(R.drawable.play));
                                    btn.setBackground(getDrawable(R.drawable.play));
                                }
                                else{
                                    mediaPlayer.start();
                                    pause.setBackground(getDrawable(R.drawable.pause));
                                    btn.setBackground(getDrawable(R.drawable.pause));
                                }
                            }
                        });

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mediaPlayer != null){
                                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                    seekBar.setProgress(mCurrentPosition);
                                }
                                mHandler.postDelayed(this, 1000);
                            }
                        });
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                seekBar.setMax(mediaPlayer.getDuration() / 1000);

                                if(b)
                                {
                                    mediaPlayer.seekTo(i*1000);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                        bottomSheetDialog.show();
                    }
                });
                txt.setText(songName);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                btn.setBackground(getDrawable(R.drawable.pause));
                try{
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(arrayListSongsUrl.get(i));
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!mediaPlayer.isPlaying()){
                                mediaPlayer.start();
                                btn.setBackground(getDrawable(R.drawable.pause));
                            }
                            else{
                                mediaPlayer.pause();
                                btn.setBackground(getDrawable(R.drawable.play));
                            }
                        }
                    });
                }
                catch (IOException e){

                }
            }
        });

        srch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                arrayAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }



    private void retrieveSongs() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Song songObj = ds.getValue(Song.class);
                    arrayListSongsName.add(songObj.getSongName());
                    arrayListSongsUrl.add(songObj.getSongUrl());
                }
                arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, arrayListSongsName){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position,convertView,parent);

                        TextView textView = (TextView)view.findViewById(android.R.id.text1);
                        textView.setSingleLine(true);
                        textView.setMaxLines(1);
                        textView.setTextColor(Color.WHITE);
                        return view;
                    }
                };
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}