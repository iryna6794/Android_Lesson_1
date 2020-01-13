package ru.javaops.android.tamagotchi.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.SparseBooleanArray;

import java.util.EnumMap;
import java.util.Map;

import ru.javaops.android.tamagotchi.R;
import ru.javaops.android.tamagotchi.enums.PetsType;

public class SoundHelper {
    private static final int MAX_STREAMS_SIZE = 1;

    private static SoundPool soundPool;
    private static final Map<PetsType, Integer> soundMap = new EnumMap<>(PetsType.class);
    private static final SparseBooleanArray soundCheckMap = new SparseBooleanArray();

    private SoundHelper() {
    }

    public synchronized static void initialSoundPool(final Context context) {
        if (soundPool == null) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_STREAMS_SIZE)
                    .setAudioAttributes(attributes)
                    .build();
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool sp, int sampleId, int status) {
                    soundCheckMap.put(sampleId, status == 0);
                }
            });
            soundMap.put(PetsType.CAT, soundPool.load(context, R.raw.cat, 0));
            soundMap.put(PetsType.DOG, soundPool.load(context, R.raw.dog, 0));
            soundMap.put(PetsType.CTHULHU, soundPool.load(context, R.raw.cthulhu, 0));
        }
    }

    public static void play(PetsType petsType) {
        Integer sampleId = soundMap.get(petsType);
        if (soundPool != null && sampleId != null && soundCheckMap.get(sampleId)) {
            soundPool.play(sampleId, 1, 1, 2, 0, 1);
        }
    }
}
