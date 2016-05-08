package com.example.ana.exampleapp;

import android.content.Context;
import android.view.View;
import android.view.LayoutInflater;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * Custom view for a rating bar made of 7 surrounded {@link TextView}s. They can be selected or not
 * and there is a special one whose border is pink. The possible answer values are: -3, -2, -1, 0,
 * 1, 2 and 3. 0 is the center value and it is coloured accordingly.
 *
 * @author Ana María Martínez Gómez
 */
public class RatingStars7 extends RatingStars {

    /**
     * Class constructor.
     */
    public RatingStars7(Context context) {
        super(context, null);
        init(context);
    }

    /**
     * Class constructor specifying attributes
     */
    public RatingStars7(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Inflate rating_stars_7.xml and add click listener to its 7 {@link TextView}s.
     *
     * @param context Its context
     * @see LayoutInflater
     * @see View#setOnClickListener(OnClickListener)
     */
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.rating_stars_7, this, true);
        findViewById(R.id.star1).setOnClickListener(this);
        findViewById(R.id.star2).setOnClickListener(this);
        findViewById(R.id.star3).setOnClickListener(this);
        findViewById(R.id.star4).setOnClickListener(this);
        findViewById(R.id.star5).setOnClickListener(this);
        findViewById(R.id.star6).setOnClickListener(this);
        findViewById(R.id.star7).setOnClickListener(this);

        non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5, R.id.star6, R.id.star7};
    }

    /**
     * Set the special {@link TextView} whose border is wanted to be pink. {@link #updateColor()}
     * needs to be called to see the color changes.
     *
     * @param pink_answer The value of the special {@link TextView} whose border is wanted to be
     *                    pink. The possible values are: 1, 2, 3, 4, 5, 6 and 7. If the value is
     *                    not a possible one this function has no effect.
     * @see #updateColor()
     */
    public void setPink(int pink_answer) {
        switch (pink_answer) {
            case -3:
                pink = R.id.star1;
                break;
            case -2:
                pink = R.id.star2;
                break;
            case -1:
                pink = R.id.star3;
                break;
            case 0:
                pink = R.id.star4;
                break;
            case 1:
                pink = R.id.star5;
                break;
            case 2:
                pink = R.id.star6;
                break;
            case 3:
                pink = R.id.star7;
                break;
        }
    }

    /**
     * Set the answer value. {@link #updateColor()} needs to be called to see the color changes.
     *
     * @param answer The value of the answer. The possible values are: 1, 2, 3, 4, 5, 6 and 7. If
     *               the value is not a possible one this function only change the answer value
     *               but not the way it is colored.
     * @see #updateColor()
     */
    public void setAnswer(int answer) {
        this.answer = answer;
        switch (answer) {
            case -3:
                color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star4};
                non_color_numbers = new int[]{R.id.star5, R.id.star6, R.id.star7};
                break;
            case -2:
                color_numbers = new int[]{R.id.star2, R.id.star3, R.id.star4};
                non_color_numbers = new int[]{R.id.star1, R.id.star5, R.id.star6, R.id.star7};
                break;
            case -1:
                color_numbers = new int[]{R.id.star3, R.id.star4};
                non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star5, R.id.star6, R.id.star7};
                break;
            case 0:
                color_numbers = new int[]{R.id.star4};
                non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star5, R.id.star6, R.id.star7};
                break;
            case 1:
                color_numbers = new int[]{R.id.star5, R.id.star4};
                non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star6, R.id.star7};
                break;
            case 2:
                color_numbers = new int[]{R.id.star4, R.id.star5, R.id.star6};
                non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star7};
                break;
            case 3:
                color_numbers = new int[]{R.id.star4, R.id.star5, R.id.star6, R.id.star7};
                non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3};
                break;
        }
    }

    /**
     * Change the answer value and color the rating bar appropriately.
     *
     * @param view the clicked {@link View}. Expected to be star1, star2, star3, star4, star5,
     *             star6 or star7.
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.star1:
                answer = -3;
                color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star4};
                non_color_numbers = new int[]{R.id.star5, R.id.star6, R.id.star7};
                break;
            case R.id.star2:
                answer = -2;
                color_numbers = new int[]{R.id.star2, R.id.star3, R.id.star4};
                non_color_numbers = new int[]{R.id.star1, R.id.star5, R.id.star6, R.id.star7};
                break;
            case R.id.star3:
                answer = -1;
                color_numbers = new int[]{R.id.star3, R.id.star4};
                non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star5, R.id.star6, R.id.star7};
                break;
            case R.id.star4:
                answer = 0;
                color_numbers = new int[]{R.id.star4};
                non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star5, R.id.star6, R.id.star7};
                break;
            case R.id.star5:
                answer = 1;
                color_numbers = new int[]{R.id.star5, R.id.star4};
                non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star6, R.id.star7};
                break;
            case R.id.star6:
                answer = 2;
                color_numbers = new int[]{R.id.star4, R.id.star5, R.id.star6};
                non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star7};
                break;
            case R.id.star7:
                answer = 3;
                color_numbers = new int[]{R.id.star4, R.id.star5, R.id.star6, R.id.star7};
                non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3};
                break;
        }
        updateColor();
    }

}
