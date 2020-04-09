package com.heaven7.android.video;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

import static android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;

//AudioManager mutex
public final class AudioManageCompat {

    public interface IMediaPlayer{
        MediaPlayer getMediaPlayer();

        void setStreamType(int type);

        void setAudioAttributes(AudioAttributes audioAttrs);

        void pause();

        void stop();

        void resumeIfNeed();
    }

    public static Delegate create(AudioManager am, IMediaPlayer mp){
        if(Build.VERSION.SDK_INT >= 26){
            return new Above26(am, mp);
        }else if(Build.VERSION.SDK_INT >= 21){
            return new Above21(am, mp);
        }
        return new Below21(am , mp);
    }

    public static abstract class Delegate implements AudioManager.OnAudioFocusChangeListener {

        final AudioManager am;
        final IMediaPlayer mp;

        public Delegate(AudioManager am, IMediaPlayer mp) {
            this.am = am;
            this.mp = mp;
        }
        public abstract void setStreamType(int type);

        public abstract void lossAudioFocus();

        public abstract void requestAudioFocus();

        @Override
        public void onAudioFocusChange(int focusChange) {
            MediaPlayer player = mp.getMediaPlayer();
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // 获得音频焦点
                    if (player != null) {
                        player.setVolume(1.0f, 1.0f);
                    }
                    mp.resumeIfNeed();
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    // 长久的失去音频焦点，释放MediaPlayer
                    mp.stop();
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // 展示失去音频焦点，暂停播放等待重新获得音频焦点
                    mp.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // 失去音频焦点，无需停止播放，降低声音即可
                    if (player != null && player.isPlaying()) {
                        player.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        }

    }

    private static class Below21 extends Delegate {

        public Below21(AudioManager am, IMediaPlayer mp) {
            super(am, mp);
        }

        @Override
        public void setStreamType(int type) {
            mp.setStreamType(type);
        }

        @Override
        public void lossAudioFocus() {
            am.abandonAudioFocus(this);
        }
        @Override
        public void requestAudioFocus() {
            am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }
    @TargetApi(21)
    private static class Above21 extends Delegate{

        AudioAttributes audioAttrs;

        public Above21(AudioManager am, IMediaPlayer mp) {
            super(am, mp);
        }

        @Override
        public void setStreamType(int type) {
            audioAttrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setLegacyStreamType(type)
                    .build();
            mp.setAudioAttributes(audioAttrs);
        }

        @Override
        public void lossAudioFocus() {
            am.abandonAudioFocus(this);
        }

        @Override
        public void requestAudioFocus() {
            am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }
    @TargetApi(26)
    private static class Above26 extends Above21{

        private AudioFocusRequest mRequest;

        public Above26(AudioManager am, IMediaPlayer mp) {
            super(am, mp);
        }
        @Override
        public void requestAudioFocus() {
            if(audioAttrs == null){
                setStreamType(AudioManager.STREAM_MUSIC);
            }
            AudioFocusRequest request = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(audioAttrs)
                    .setOnAudioFocusChangeListener(this)
                    .build();
            mRequest = request;
            am.requestAudioFocus(request);
        }

        @Override
        public void lossAudioFocus() {
            if(mRequest != null){
                am.abandonAudioFocusRequest(mRequest);
            }
        }
    }
}
