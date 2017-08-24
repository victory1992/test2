package com.dingapp.core.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;

import java.io.File;

public class MediaPlayTools {

	private static final String TAG = "MediaPlayTools";

	private static final int STATUS_ERROR = -1;
	private static final int STATUS_STOP = 0;
	private static final int STATUS_PLAYING = 1;
	private static final int STATUS_PAUSE = 2;

	private MediaPlayer mediaPlayer = null;
	private OnVoicePlayCompletionListener mListener;

	private int status = 0;
	private String urlPath = "";

	private MediaPlayTools() {
		mediaPlayer = new MediaPlayer();
		setOnCompletionListener();
		setOnErrorListener();
	}

	private static class SLocker {
		private static MediaPlayTools sTools = new MediaPlayTools();
	}

	synchronized public static MediaPlayTools getInstance() {
		return SLocker.sTools;
	}

	/**
	 * <p>
	 * Title: play
	 * </p>
	 * <p>
	 * Description: Speech interface, you can set the start position to play,
	 * and to select the output stream (Earpiece or Speaker)
	 * </p>
	 * 
	 * @param isEarpiece
	 * @param seek
	 */
	private void play(boolean isEarpiece, int seek) {
		if (TextUtils.isEmpty(urlPath) || !new File(urlPath).exists()) {
			return;
		}

		int streamType = AudioManager.STREAM_MUSIC;
		if (isEarpiece) {
			streamType = AudioManager.STREAM_VOICE_CALL;
		}

		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}
		try {
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(streamType);
			mediaPlayer.setDataSource(urlPath);
			mediaPlayer.prepare();
			if (seek > 0) {
				mediaPlayer.seekTo(seek);
			}
			mediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean play(String urlPath, boolean isEarpiece, int seek) {
		if (status != STATUS_STOP) {
			LoggerUtil
					.e(TAG, "[MediaPlayTools - play ] startPlay error status:"
							+ status);
			return false;
		}

		this.urlPath = urlPath;
		boolean result = false;
		try {
			play(isEarpiece, seek);
			this.status = STATUS_PLAYING;
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				play(true, seek);
				result = true;
			} catch (Exception e1) {
				e1.printStackTrace();
				result = false;
			}
		}

		return result;
	}

	public boolean playVoice(String urlPath, boolean isEarpiece) {
		return play(urlPath, isEarpiece, 0);
	}

	public boolean resume() {
		if (this.status != STATUS_PAUSE) {
			LoggerUtil.e(TAG,
					"[MediaPlayTools - resume ] resume not STATUS_PAUSE error status:"
							+ this.status);
			return false;
		}

		boolean result = false;
		try {
			mediaPlayer.start();
			this.status = STATUS_PLAYING;
			result = true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			this.status = STATUS_ERROR;
			result = false;
		}

		return result;
	}

	public boolean stop() {
		if (status != STATUS_PLAYING && status != STATUS_PAUSE) {
			LoggerUtil.e(TAG,
					"[MediaPlayTools - stop] stop not STATUS_PLAYING or STATUS_PAUSE error status:"
							+ this.status);
			return false;
		}

		boolean result = false;
		try {
			if (mediaPlayer != null) {
				this.mediaPlayer.stop();
				this.mediaPlayer.release();
				this.mediaPlayer = null;
			}
			this.status = STATUS_STOP;
			result = true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			this.status = STATUS_ERROR;
			result = false;
		}

		return result;
	}

	private boolean calling = false;

	public void setSpeakerOn(boolean speakerOn) {

		LoggerUtil.v(TAG, "[MediaPlayTools - setSpeakerOn] setSpeakerOn="
				+ speakerOn);

		if (calling) {
			// 这里需要判断当前的状态是否是正在系统电话振铃或者接听中

		} else {
			int currentPosition = mediaPlayer.getCurrentPosition();
			stop();
			setOnCompletionListener();
			setOnErrorListener();
			play(urlPath, !speakerOn, currentPosition);
		}
	}

	public boolean pause() {
		if (this.status != STATUS_PLAYING) {
			LoggerUtil.e(TAG,
					"[MediaPlayTools - pause]pause not STATUS_PLAYING error status:"
							+ this.status);
			return false;
		}

		boolean result = false;
		try {
			mediaPlayer.pause();
			this.status = STATUS_PAUSE;
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
			status = STATUS_ERROR;
		}

		return result;
	}

	public int getStatus() {
		return status;
	}

	public boolean isPlaying() {
		if (this.status == STATUS_PLAYING) {
			return true;
		}
		return false;
	}

	private void setOnCompletionListener() {
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				LoggerUtil.d(TAG,
						"[MediaPlayTools - setOnCompletionListener] Play file["
								+ urlPath + "] com");
				status = STATUS_STOP;

				if (mListener != null) {
					mListener.OnVoicePlayCompletion();
				}
			}
		});
	}

	private void setOnErrorListener() {
		mediaPlayer.setOnErrorListener(null);
	}

	public void setOnVoicePlayCompletionListener(OnVoicePlayCompletionListener l) {
		mListener = l;
	}

	public interface OnVoicePlayCompletionListener {
		void OnVoicePlayCompletion();
	}
}
