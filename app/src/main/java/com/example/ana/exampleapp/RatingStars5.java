package com.example.ana.exampleapp;

import android.content.Context;
import android.view.View;
import android.view.LayoutInflater;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * Custom view for a rating bar made of 5 surrounded {@link TextView}s. They can be selected or not
 * and there is a special one whose border is pink. The possible answer values are: 1,2,3,4 and 5.
 *
 * @author Ana María Martínez Gómez
 */
public class RatingStars5 extends RatingStars {

    /**
     * Class constructor.
     */
    public RatingStars5(Context context) {
        super(context, null);
        init(context);
    }

    /**
     * Class constructor specifying attributes
     */
    public RatingStars5(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Inflate rating_stars_5.xml and add click listener to its 5 {@link TextView}s.
     *
     * @param context Its context
     * @see LayoutInflater
     * @see View#setOnClickListener(OnClickListener)
     */
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.rating_stars_5, this, true);
        findViewById(R.id.star1).setOnClickListener(this);
        findViewById(R.id.star2).setOnClickListener(this);
        findViewById(R.id.star3).setOnClickListener(this);
        findViewById(R.id.star4).setOnClickListener(this);
        findViewById(R.id.star5).setOnClickListener(this);

        non_color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5};
    }

    /**
     * Set the special {@link TextView} whose border is wanted to be pink. {@link #updateColor()}
     * needs to be called to see the color changes.
     *
     * @param pink_answer The value of the special {@link TextView} whose border is wanted to be
     *                    pink. The possible values are: 1, 2, 3, 4 and 5. If the value is not a
     *                    possible one this function has no effect.
     * @see #updateColor()
     */
    public void setPink(int pink_answer) {
        switch (pink_answer) {
            case 1:
                pink = R.id.star1;
                break;
            case 2:
                pink = R.id.star2;
                break;
            case 3:
                pink = R.id.star3;
                break;
            case 4:
                pink = R.id.star4;
                break;
            case 5:
                pink = R.id.star5;
                break;
        }
    }

    /**
     * Set the answer value. {@link #updateColor()} needs to be called to see the color changes.
     *
     * @param answer The value of the answer. The possible values are: 1, 2, 3, 4 and 5. If the
     *               value is not a possible one this function only change the answer value but
     *               not the way it is colored.
     * @see #updateColor()
     */
    public void setAnswer(int answer) {
        this.answer = answer;
        switch (answer) {
            case 1:
                color_numbers = new int[]{R.id.star1};
                non_color_numbers = new int[]{R.id.star2, R.id.star3, R.id.star4, R.id.star5};
                break;
            case 2:
                color_numbers = new int[]{R.id.star1, R.id.star2};
                non_color_numbers = new int[]{R.id.star3, R.id.star4, R.id.star5};
                break;
            case 3:
                color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3};
                non_color_numbers = new int[]{R.id.star4, R.id.star5};
                break;
            case 4:
                color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star4};
                non_color_numbers = new int[]{R.id.star5};
                break;
            case 5:
                color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5};
                non_color_numbers = new int[]{};
                break;
        }
    }

    /**
     * Change the answer value and color the rating bar appropriately.
     *
     * @param view the clicked {@link View}. Expected to be star1, star2, star3, star4 or star5.
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.star1:
                answer = 1;
                color_numbers = new int[]{R.id.star1};
                non_color_numbers = new int[]{R.id.star2, R.id.star3, R.id.star4, R.id.star5};
                break;
            case R.id.star2:
                answer = 2;
                color_numbers = new int[]{R.id.star1, R.id.star2};
                non_color_numbers = new int[]{R.id.star3, R.id.star4, R.id.star5};
                break;
            case R.id.star3:
                answer = 3;
                color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3};
                non_color_numbers = new int[]{R.id.star4, R.id.star5};
                break;
            case R.id.star4:
                answer = 4;
                color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star4};
                non_color_numbers = new int[]{R.id.star5};
                break;
            case R.id.star5:
                answer = 5;
                color_numbers = new int[]{R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5};
                non_color_numbers = new int[]{};
                break;
        }
        updateColor();
    }

}
