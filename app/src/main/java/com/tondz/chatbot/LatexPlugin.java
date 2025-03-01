package com.tondz.chatbot;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.scilab.forge.jlatexmath.TeXConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.jlatexmath.awt.Graphics;

public class LatexPlugin extends AbstractMarkwonPlugin {

    @Override
    public void afterSetText(TextView textView) {
        CharSequence text = textView.getText();
        SpannableStringBuilder builder = new SpannableStringBuilder(text);

        // Regex tìm công thức dạng $E=mc^2$
        String regex = "\\$(.*?)\\$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String latex = matcher.group(1);
            Log.e("TAG", "afterSetText: " + latex);
            try {
                Drawable drawable = renderLatexToDrawable(textView, latex);
                int start = matcher.start();
                int end = matcher.end();
                builder.setSpan(new ImageSpan(drawable), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        textView.setText(builder);
    }

    private Drawable renderLatexToDrawable(TextView textView, String latex) {
        try {
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, (int) textView.getTextSize());

            // Tạo bitmap có nền trong suốt
            Bitmap bitmap = Bitmap.createBitmap(icon.getIconWidth(), icon.getIconHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // Vẽ nền trắng (nếu cần)
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);

            return new BitmapDrawable(textView.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
