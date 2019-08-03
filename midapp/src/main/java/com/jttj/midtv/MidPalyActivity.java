package com.jttj.midtv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;

import org.litepal.crud.DataSupport;

import java.io.File;

import yin.source.com.midimusicbook.midi.baseBean.MidiFile;
import yin.source.com.midimusicbook.midi.baseBean.MidiOptions;
import yin.source.com.midimusicbook.midi.musicBook.MidiPlayer;
import yin.source.com.midimusicbook.midi.musicBook.MusicBook;
import yin.source.com.midimusicbook.midi.musicBook.Piano;

import static android.media.AudioManager.STREAM_VOICE_CALL;
import static yin.source.com.midimusicbook.midi.musicBook.MidiPlayer.PlayState.PAUSED;
import static yin.source.com.midimusicbook.midi.musicBook.MidiPlayer.PlayState.PLAYING;
import static yin.source.com.midimusicbook.midi.musicBook.MidiPlayer.PlayState.STOPPED;

/**
 * @content: Author: gjt66888
 * Description:
 * Time: 2019/6/12
 */
public class MidPalyActivity extends AppCompatActivity {

    private MidiPlayer player; /* The play/stop/rewind toolbar */
    private Piano piano; /* The piano at the top */ // 顶部的钢琴
    private MusicBook sheet; /* The sheet music */ // 乐谱
    private MidiFile midifile; /* The midi file to play */ // 需要播放的midi文件
    private MidiOptions options; /* The options for sheet music and sound */ // 乐谱和声音的选项

    private Toolbar toolbar;

    private Button rewindButton;
    /**
     * The rewind button // 倒带按钮
     */
    private Button playButton;
    /**
     * The stop button // 停止按钮
     */
    private Button fastFwdButton;

    private Button btnRestart;

    private Button btn_volume_mute;

    boolean isPaly = true;
    boolean isVolume = true;

    private AudioManager myAudioManager = null;

    int ringerMode;
    File fileFromAssets;

    private SoundDialog mSoundDialog = null;

    private String title = "";

    private boolean isThea = false;

    /**
     * Create this SheetMusicActivity. The Intent should have two parameters: -
     * data: The uri of the midi file to open. - MidiTitleID: The title of the
     * song (String)
     * 创建这个乐谱Activity.传过来的intent必须有两个参数：
     * data:将要打开的midi文件的uri
     * MidiTitleID:歌曲的标题
     */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_mid_play);
        Log.i(StrUtils.TAG, "onCreate" + System.currentTimeMillis());

        title = this.getIntent().getStringExtra(StrUtils.MidiTitleID);
        String namess = this.getIntent().getStringExtra(StrUtils.MidiTitleName);
        fileFromAssets = new File(title);

        player = new MidiPlayer(getApplicationContext());

        toolbar = findViewById(R.id.mid_paly_toolbar);

        piano = findViewById(R.id.piano);

        sheet = findViewById(R.id.sheet);

        // 主标题
        toolbar.setTitle(namess);

        toolbar.setNavigationIcon(R.drawable.ic_return);
        //点击左边返回按钮监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//返回
            }
        });
    }

    /**
     * When this activity resumes, redraw all the views
     */
    @Override
    protected void onResume() {
        super.onResume();

        createPlayerButton();

//        new Thread(new MidRunnable()).start();

        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        midifile = new MidiFile(fileFromAssets, fileFromAssets.getName());

        options = new MidiOptions(midifile);
        options.scrollVert = true;
        options.showMeasures = true;
        //显示音名
        options.showNoteLetters = 0;

        createSheetMusic(options);

    }

    private void createPlayerButton() {

        rewindButton = (Button) findViewById(R.id.btn_rewind);
        playButton = (Button) findViewById(R.id.btn_play);
        fastFwdButton = (Button) findViewById(R.id.btn_fast_forward);

        btnRestart = findViewById(R.id.btn_restart);
        btn_volume_mute = findViewById(R.id.btn_volume_mute);

        rewindButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                player.Rewind();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // playState == PlayState.INIT_STOP || playState == PlayState.INIT_PAUSE || playState == PlayState.PLAYING
                //STOPPED, PLAYING, PAUSED, INIT_STOP, INIT_PAUSE
                if (player.getPlayState() == STOPPED || player.getPlayState() == PAUSED) {
                    isPaly = false;
                    StrUtils.showToast("正在缓冲中...");
                    playButton.setBackground(MidPalyActivity.this.getResources().getDrawable(R.drawable.svg_pause));
                    player.play();

                } else if (player.getPlayState() == PLAYING) {
                    isPaly = true;
                    playButton.setBackground(MidPalyActivity.this.getResources().getDrawable(R.drawable.play));
//                    StrUtils.showToast("暂停");
                    player.pause();
                }
            }
        });

        btn_volume_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                player.pause();

                if (mSoundDialog == null) {

                    mSoundDialog = new SoundDialog(MidPalyActivity.this).builder()
                            .setSound01Button(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    player.setSound(0.5f);
                                }
                            }).setSound02Button(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    player.setSound(0.8f);
                                }
                            }).setSound03Button(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    player.setSound(1.0f);
                                }
                            }).setSound04Button(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    player.setSound(1.5f);
                                }
                            });

                }
                mSoundDialog.show();
            }
        });

        fastFwdButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                player.FastForward();

            }
        });

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                player.stop();
//                player.play();
                normalDialog(title);
            }
        });
    }


    /**
     * Create the SheetMusic view with the given options
     */
    private void createSheetMusic(final MidiOptions options) {
        if (!options.showPiano) {
            piano.setVisibility(View.GONE);
        } else {
            piano.setVisibility(View.VISIBLE);
        }
        sheet.init(midifile, options);
        sheet.setPlayer(player);

        piano.setMidiFile(midifile, options, player);
        piano.setShadeColors(options.colorLeftHandShade, options.colorRightHandShade);
        player.setMidiFile(midifile, options);

        sheet.callOnDraw();
        sheet.setBackground(new WaterMarkBg("默  思"));
    }

    /**
     * This is the callback when the SettingsActivity is finished. Get the
     * modified MidiOptions (passed as a parameter in the Intent). Save the
     * MidiOptions. The key is the CRC checksum of the midi data, and the value
     * is a JSON dump of the MidiOptions. Finally, re-create the SheetMusic View
     * with the new options.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Recreate the sheet music with the new options
        createSheetMusic(options);
    }

    /**
     * When this activity pauses, stop the music
     */
    @Override
    protected void onPause() {
        if (player != null) {
            player.pause();
        }
        super.onPause();
    }

    //一般的Dialog
    public void normalDialog(final String mMidBean) {
        AlertDialog.Builder bulider = new AlertDialog.Builder(MidPalyActivity.this);
        bulider.setIcon(R.drawable.ic_launcher);//在title的左边显示一个图片
        bulider.setTitle("提示");
        bulider.setMessage("确定删除吗？");
        bulider.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {

                detDate(title);
                dialog.dismiss();

            }
        });
        bulider.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        bulider.create().show();

    }

    /**
     * 删除数据库中数据
     */
    private void detDate(String getMidAddress) {

        int a = DataSupport.deleteAll(MidBean.class, "midAddress=?", getMidAddress);

        if (a == 1) {
            // 开启异步加载数据
            isThea = true;
            StrUtils.showToast("删除成功！");
            player.stop();
            setResult(520);
            finish();
        } else {
            StrUtils.showToast("删除失败！");
            isThea = false;
        }
    }
}
