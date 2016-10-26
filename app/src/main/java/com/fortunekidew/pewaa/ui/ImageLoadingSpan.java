package com.fortunekidew.pewaa.ui;

/**
 * Created by mwakima on 10/26/16.
 */

import android.text.TextPaint;
import android.text.style.CharacterStyle;

/**
 * A simple text span used to mark text that will be replaced by an image once it has been
 * downloaded.
 */
public class ImageLoadingSpan extends CharacterStyle {
    @Override
    public void updateDrawState(TextPaint textPaint) {
        // no-op
    }
}
