package com.example.ana.exampleapp;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.util.AttributeSet;


/**
 * Custom view for a rating bar made of surrounded {@link TextView}s. They can be selected or not
 * and there is a special one whose border is pink. The way in which the {@link TextView}s are
 * selected and the value of the answer depends on the specific implementation in the inheriting
 * classes.
 *
 * @author Ana María Martínez Gómez
 */
public abstract class RatingStars extends LinearLayout implements OnClickListener {

    protected int pink = 10; //not selected = 10
    protected int answer = 10; //not selected = 10
    protected int[] color_numbers = new int[]{};
    protected int[] non_color_numbers; // Initialized in the inheriting classes

    /**
     * Class constructor.
     */
    public RatingStars(Context context) {
        super(context, null);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * Class constructor specifying attributes
     */
    public RatingStars(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * Update the color of the rating bar circles as if you have called {@link #setPink(int)} or
     * {@link #setAnswer(int)}), the color would be out of date.
     *
     * @see #setPink(int)
     * @see #setAnswer(int)
     */
    public void updateColor() {
        TextView v;
        for (int i = 0; i < color_numbers.length; i++) {
            v = (TextView) findViewById(color_numbers[i]);
            v.setTextColor(Color.WHITE);
            if (pink == color_numbers[i])
                v.setBackgroundResource(R.drawable.star_yesterday_selected);
            else
                v.setBackgroundResource(R.drawable.star_selected);
        }

        for (int i = 0; i < non_color_numbers.length; i++) {
            v = (TextView) findViewById(non_color_numbers[i]);
            v.setTextColor(Color.GRAY);
            if (pink == non_color_numbers[i])
                v.setBackgroundResource(R.drawable.star_yesterday);
            else
                v.setBackgroundResource(R.drawable.star);
        }
    }

    /**
     * Return the value of the answer. The range of possible values depends on the implementation
     * of the inheriting classes.
     *
     * @return the value of the answer
     */
    public int getAnswer() {
        return answer;
    }

    /**
     * Set the special {@link TextView} whose border is wanted to be pink. {@link #updateColor()}
     * needs to be called to see the color changes.
     *
     * @param pink_answer The value of the special {@link TextView} whose border is wanted to be
     *                    pink. The range of possible values depends on the inheriting class.
     *                    If the value is not a possible one this function has no effect.
     * @see #updateColor()
     */
    public abstract void setPink(int pink_answer);

    /**
     * Set the answer value. {@link #updateColor()} needs to be called to see the color changes.
     *
     * @param answer The value of the answer. The range of possible values depends on the
     *               inheriting class. If the value is not a possible one this function only
     *               change the answer value but not the way it is colored.
     * @see #updateColor()
     */
    public abstract void setAnswer(int answer);

    @Override
    public abstract void onClick(View view);

}