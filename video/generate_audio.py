import math
import struct
import wave
import os

SAMPLE_RATE = 44100
DURATION = 42.0
TOTAL_SAMPLES = int(SAMPLE_RATE * DURATION)

def generate_track():
    print(f"Generating {DURATION}s stereo ambient soundtrack at {SAMPLE_RATE}Hz...")
    
    # Pre-allocate buffer for left and right
    left = [0.0] * TOTAL_SAMPLES
    right = [0.0] * TOTAL_SAMPLES
    
    # Chord progression map (start_time, end_time, [root, notes...])
    # Frequencies for notes
    # D3: 146.83, F3: 174.61, A3: 220.0, C4: 261.63, D4: 293.66, E4: 329.63, F4: 349.23, G4: 392.0, A4: 440.0
    # Bb2: 116.54, F3: 174.61, Bb3: 233.08, D4: 293.66
    # F2: 87.31, C3: 130.81, A3: 220.0, C4: 261.63
    # G2: 98.00, D3: 146.83, Bb3: 233.08, D4: 293.66
    # A2: 110.00, E3: 164.81, C4: 261.63, E4: 329.63
    
    chords = [
        # (start, end, bass_freq, chord_freqs)
        (0.0, 8.0, 73.42, [146.83, 220.00, 261.63, 329.63, 349.23]),      # Dm9
        (8.0, 16.0, 58.27, [116.54, 174.61, 233.08, 293.66, 349.23]),     # Bbmaj7
        (16.0, 25.0, 87.31, [130.81, 174.61, 220.00, 261.63, 329.63]),    # Fadd9 / C
        (25.0, 34.0, 98.00, [146.83, 196.00, 233.08, 293.66, 349.23]),    # Gm7 / Am
        (34.0, 42.0, 73.42, [146.83, 220.00, 293.66, 370.00, 440.00]),    # Dmaj (triumphant)
    ]
    
    # 1. Synthesize Warm Pad Layers
    for (start_t, end_t, bass_f, chord_fs) in chords:
        start_samp = int(start_t * SAMPLE_RATE)
        end_samp = int(min(DURATION, end_t + 1.5) * SAMPLE_RATE) # slight overlap
        chord_len = end_samp - start_samp
        
        for i in range(chord_len):
            idx = start_samp + i
            if idx >= TOTAL_SAMPLES:
                break
            t = i / SAMPLE_RATE
            
            # Chord envelope: smooth attack (1.2s), smooth release (1.5s)
            dur = (end_t - start_t)
            if t < 1.2:
                env = 0.5 * (1.0 - math.cos(math.pi * t / 1.2))
            elif t > dur:
                rel_t = (t - dur) / 1.5
                env = max(0.0, 0.5 * (1.0 + math.cos(math.pi * min(1.0, rel_t))))
            else:
                env = 1.0
                
            # Bass sub-oscillator (sine + soft 2nd harmonic)
            bass_sig = 0.28 * math.sin(2 * math.pi * bass_f * t) + 0.12 * math.sin(2 * math.pi * bass_f * 2 * t)
            
            # Polyphonic pad voices with slight detuning and chorus stereo spread
            pad_sig_l = 0.0
            pad_sig_r = 0.0
            for note_idx, freq in enumerate(chord_fs):
                detune1 = 1.002
                detune2 = 0.998
                # Sine + gentle triangle harmonic
                v1 = math.sin(2 * math.pi * freq * detune1 * t) + 0.25 * math.sin(2 * math.pi * freq * 2 * t)
                v2 = math.sin(2 * math.pi * freq * detune2 * t + 0.5) + 0.25 * math.sin(2 * math.pi * freq * 2 * t + 0.5)
                # Pan notes across stereo field
                pan = 0.3 + 0.4 * (note_idx / max(1, len(chord_fs) - 1))
                pad_sig_l += v1 * (1.0 - pan) * 0.065
                pad_sig_r += v2 * pan * 0.065
                
            left[idx] += (bass_sig * 0.5 + pad_sig_l) * env
            right[idx] += (bass_sig * 0.5 + pad_sig_r) * env

    # 2. Add Ambient Tech Arpeggios / Plucks (repeating 16th/8th pattern)
    # Notes in D minor pentatonic: D4, F4, G4, A4, C5, D5
    arp_notes = [293.66, 349.23, 392.00, 440.00, 523.25, 440.00, 392.00, 349.23,
                 293.66, 392.00, 440.00, 523.25, 587.33, 523.25, 440.00, 392.00]
    arp_interval = 0.25 # 120 bpm eighth notes
    num_arps = int((40.0 - 2.0) / arp_interval)
    
    for n in range(num_arps):
        note_time = 2.0 + n * arp_interval
        freq = arp_notes[n % len(arp_notes)]
        start_samp = int(note_time * SAMPLE_RATE)
        note_len = int(0.7 * SAMPLE_RATE) # ring out
        
        # Stereo ping-pong alternating
        pan = 0.25 if (n % 2 == 0) else 0.75
        
        for i in range(note_len):
            idx = start_samp + i
            if idx >= TOTAL_SAMPLES:
                break
            t = i / SAMPLE_RATE
            # Pluck envelope: zero attack, exponential decay
            env = math.exp(-6.5 * t)
            # Sine wave + octave harmonic for bell/crystal tone
            sample = (math.sin(2 * math.pi * freq * t) + 0.3 * math.sin(4 * math.pi * freq * t)) * env * 0.09
            
            # Subtle delay echo after 0.25s
            delay_idx = idx + int(0.25 * SAMPLE_RATE)
            left[idx] += sample * (1.0 - pan)
            right[idx] += sample * pan
            if delay_idx < TOTAL_SAMPLES:
                left[delay_idx] += sample * pan * 0.35
                right[delay_idx] += sample * (1.0 - pan) * 0.35

    # 3. Add Gentle Modern Tech Pulse / Heartbeat (from 8.0s to 39.0s)
    # Subtle deep kick every 1.0s (60 bpm)
    for beat in range(8, 38):
        kick_start = int(beat * SAMPLE_RATE)
        kick_len = int(0.35 * SAMPLE_RATE)
        for i in range(kick_len):
            idx = kick_start + i
            if idx >= TOTAL_SAMPLES:
                break
            t = i / SAMPLE_RATE
            env = math.exp(-12.0 * t)
            # Pitch drop from 95Hz down to 45Hz
            pitch = 45.0 + 50.0 * math.exp(-25.0 * t)
            kick_val = math.sin(2 * math.pi * pitch * t) * env * 0.22
            left[idx] += kick_val
            right[idx] += kick_val

    # 4. Master Volume, Fade In / Out, Limiter & Clipping Prevention
    fade_in_len = int(1.5 * SAMPLE_RATE)
    fade_out_start = int(39.0 * SAMPLE_RATE)
    fade_out_len = TOTAL_SAMPLES - fade_out_start

    max_peak = 0.0
    for idx in range(TOTAL_SAMPLES):
        # Fade in
        if idx < fade_in_len:
            left[idx] *= (idx / fade_in_len)
            right[idx] *= (idx / fade_in_len)
        # Fade out
        elif idx >= fade_out_start:
            fade_pos = (idx - fade_out_start) / fade_out_len
            fade_vol = 0.5 * (1.0 + math.cos(math.pi * fade_pos))
            left[idx] *= fade_vol
            right[idx] *= fade_vol

        # Soft limiter curve (tanh-like)
        left[idx] = math.tanh(left[idx] * 1.1) * 0.85
        right[idx] = math.tanh(right[idx] * 1.1) * 0.85
        
        max_peak = max(max_peak, abs(left[idx]), abs(right[idx]))

    print(f"Mastering complete. Peak level: {max_peak:.2f}")

    # Write WAV file
    out_wav = "/home/juancito/Documents/Orca/java/video/soundtrack.wav"
    with wave.open(out_wav, "wb") as wf:
        wf.setnchannels(2)
        wf.setsampwidth(2)
        wf.setframerate(SAMPLE_RATE)
        
        raw_frames = bytearray()
        for i in range(TOTAL_SAMPLES):
            # Clamp to 16-bit PCM (-32767 to 32767)
            val_l = int(max(-32767, min(32767, left[i] * 32767)))
            val_r = int(max(-32767, min(32767, right[i] * 32767)))
            raw_frames.extend(struct.pack("<hh", val_l, val_r))
        wf.writeframes(raw_frames)

    print(f"Generated soundtrack saved to: {out_wav} ({os.path.getsize(out_wav)} bytes)")

if __name__ == "__main__":
    generate_track()
