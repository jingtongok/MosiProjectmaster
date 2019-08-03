/*
 * Copyright (c) 2011-2012 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */

package com.jttj.sheet;

import android.app.*;
import android.net.Uri;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.content.res.*;

import com.jttj.midtv.StrUtils;

import java.util.zip.CRC32;

/**
 * @class SheetMusicActivity
 * <p>
 * The SheetMusicActivity is the main activity. The main components are:
 * - MidiPlayer : The buttons and speed bar at the top.
 * - Piano : For highlighting the piano notes during playback.
 * - SheetMusic : For highlighting the sheet music notes during playback.
 */
public class SheetMusicActivity extends Activity {

    public static final int settingsRequestCode = 1;

//    private MidiPlayer player;   /* The play/stop/rewind toolbar */
//    private Piano piano;         /* The piano at the top */
    private SheetMusic sheet;    /* The sheet music */
    private LinearLayout layout; /* THe layout */
    private MidiFile midifile;   /* The midi file to play */
    private MidiOptions options; /* The options for sheet music and sound */
    private long midiCRC;      /* CRC of the midi bytes */

    /**
     * Create this SheetMusicActivity.
     * The Intent should have two parameters:
     * - data: The uri of the midi file to open.
     * - MidiTitleID: The title of the song (String)
     */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        ClefSymbol.LoadImages(this);
        TimeSigSymbol.LoadImages(this);
        MidiPlayer.LoadImages(this);

        // Parse the MidiFile from the raw bytes
        Uri uri = this.getIntent().getData();
        String title = this.getIntent().getStringExtra(StrUtils.MidiTitleID);
        if (title == null) {
            title = uri.getLastPathSegment();
        }
        FileUri file = new FileUri(uri, title);
        this.setTitle(title);
        byte[] data;
        try {
            data = file.getData(this);
            midifile = new MidiFile(data, title);
        } catch (MidiFileException e) {
            this.finish();
            return;
        }

        // Initialize the settings (MidiOptions).
        // If previous settings have been saved, used those
        options = new MidiOptions(midifile);
        CRC32 crc = new CRC32();
        crc.update(data);
        midiCRC = crc.getValue();
        SharedPreferences settings = getPreferences(0);
        options.scrollVert = settings.getBoolean("scrollVert", false);
        options.shade1Color = settings.getInt("shade1Color", options.shade1Color);
        options.shade2Color = settings.getInt("shade2Color", options.shade2Color);
        options.showPiano = settings.getBoolean("showPiano", true);
        String json = settings.getString("" + midiCRC, null);
        MidiOptions savedOptions = MidiOptions.fromJson(json);
        if (savedOptions != null) {
            options.merge(savedOptions);
        }
        createView();
        createSheetMusic(options);
    }

    /* Create the MidiPlayer and Piano views */
    void createView() {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
//        player = new MidiPlayer(this);
//        piano = new Piano(this);
//        layout.addView(player);
//        layout.addView(piano);
//        setContentView(layout);
//        player.SetPiano(piano);
        layout.requestLayout();
    }

    /**
     * Create the SheetMusic view with the given options
     */
    private void
    createSheetMusic(MidiOptions options) {
        if (sheet != null) {
            layout.removeView(sheet);
        }
//        if (!options.showPiano) {
//            piano.setVisibility(View.GONE);
//        } else {
//            piano.setVisibility(View.VISIBLE);
//        }
        sheet = new SheetMusic(this);
        sheet.init(midifile, options);
//        sheet.setPlayer(player);
        layout.addView(sheet);
//        piano.SetMidiFile(midifile, options, player);
//        piano.SetShadeColors(options.shade1Color, options.shade2Color);
//        player.SetMidiFile(midifile, options, sheet);
        layout.requestLayout();
        sheet.callOnDraw();
    }


    /**
     * Always display this activity in landscape mode.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * When the menu button is pressed, initialize the menus.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (player != null) {
//            player.Pause();
//        }
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.sheet_menu, menu);
        return true;
    }

    /**
     * When this activity resumes, redraw all the views
     */
    @Override
    protected void onResume() {
        super.onResume();
        layout.requestLayout();
//        player.invalidate();
//        piano.invalidate();
        if (sheet != null) {
            sheet.invalidate();
        }
        layout.requestLayout();
    }

    /**
     * When this activity pauses, stop the music
     */
    @Override
    protected void onPause() {
//        if (player != null) {
//            player.Pause();
//        }
        super.onPause();
    }
}

