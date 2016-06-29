package com.sasisa.chat.sasisachat;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.FileDescriptor;

/**
 * Created by cherry on 09.09.2015.
 */
public class GlobalMediaPlayer {
    private static volatile GlobalMediaPlayer instance;
    private MediaPlayer mPlayer;

    private GlobalMediaPlayer() {
        mPlayer = null;
    }

    public static GlobalMediaPlayer getInstance() {
        GlobalMediaPlayer localInstance = instance;
        if (localInstance == null) {
            synchronized (GlobalMediaPlayer.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new GlobalMediaPlayer();
                }
            }
        }
        return localInstance;
    }

    public void releaseMP() {
        if (mPlayer != null) {
            try {
                mPlayer.release();
                mPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playAudio(AssetFileDescriptor fd) {
        try {
            mPlayer = new MediaPlayer();
            long start = fd.getStartOffset();
            long end = fd.getLength();
            mPlayer.setDataSource(fd.getFileDescriptor(), start, end);
            //mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.prepare();
            mPlayer.setVolume(1.0f, 1.0f);
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
