package com.example.tarot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import android.content.Intent;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    ImageView cardImageView;
    ImageView cardLabels; // Added reference to cardLabels ImageView
    ImageButton startButton;
    ImageButton backButton;

    ImageView instructionImageView;

    MediaPlayer chain;
    MediaPlayer spin;
    MediaPlayer zoom;
    boolean isCardFlipped = false;
    boolean isInstructionsVisible = true; // Track if instructions are visible

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardImageView = findViewById(R.id.cardImageView);
        cardLabels = findViewById(R.id.cardlabels); // Initialize cardLabels ImageView
        startButton = findViewById(R.id.STARTButton);
        backButton = findViewById(R.id.BackButton);
        instructionImageView = findViewById(R.id.tarotinstructions);
        chain = MediaPlayer.create(this, R.raw.chainsound);
        zoom = MediaPlayer.create(this, R.raw.zoomin);
        spin = MediaPlayer.create(this, R.raw.spinningsound);


        // Initially hide the cardImageView and cardLabels
        cardImageView.setVisibility(View.INVISIBLE);
        cardLabels.setVisibility(View.INVISIBLE);

        // Set OnClickListener for cardImageView to reset screen when clicked
        cardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetScreen();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInstructionsVisible) { // Check if instructions are not visible
                    // Stop and release the zoom MediaPlayer instance if it's currently playing
                    if (zoom.isPlaying()) {
                        zoom.stop();
                        zoom.release();
                    }

                    // Create a new MediaPlayer instance for zoom sound each time startButton is clicked
                    zoom = MediaPlayer.create(MainActivity.this, R.raw.zoomin);
                    zoom.start();

                    if (!isCardFlipped) {
                        // Zoom in animation for back card image
                        zoomInAnimation();
                        // Show the cardImageView
                        cardImageView.setVisibility(View.VISIBLE);
                        // Change the image of the startButton to flipbtn
                        startButton.setImageResource(R.drawable.flipbtn);
                        isCardFlipped = true;
                    } else {
                        // Flip the card
                        flipCard();
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to reset the screen
                Toast.makeText(MainActivity.this, "You have pressed the back button", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void flipCard() {
        if (!isCardFlipped && startButton.isEnabled()) {
            // Flip the card to show fourcard image
            flipImage(R.drawable.fourcard);
            // Disable further clicks on the button
            startButton.setEnabled(false);
        } else if (isCardFlipped && startButton.isEnabled()) {
            // Flip the card to show backcard image
            flipImage(R.drawable.backcard);
            // Disable further clicks on the button
            startButton.setEnabled(false);
        }
    }

    private void resetScreen() {
        // Reset any state variables or UI elements to their initial state
        isCardFlipped = false;
        cardImageView.setVisibility(View.INVISIBLE);
        cardLabels.setVisibility(View.INVISIBLE); // Hide card labels when resetting screen
        startButton.setImageResource(R.drawable.startbtn);
        startButton.setEnabled(true);
        cardImageView.setRotationY(0f);
        cardImageView.setImageResource(R.drawable.backcard); // Set the card to backcard
        // You may need to reset other UI elements or variables depending on your app's requirements
    }

    private void animateCardLabels() {
        // Set initial translationY value to make the labels start from below the view
        cardLabels.setTranslationY(cardLabels.getHeight());

        // Animate translationY to 0 to move the labels upward
        ObjectAnimator animator = ObjectAnimator.ofFloat(cardLabels, "translationY", 0);
        animator.setDuration(2000); // Adjust duration as needed
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    private void flipImage(final int resourceId) {
        // Create a new MediaPlayer instance for spin sound each time flipImage is called
        final MediaPlayer spinSound = MediaPlayer.create(this, R.raw.spinningsound);
        zoom.stop();
        spinSound.start();

        // Stop the spin sound after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (spinSound != null && spinSound.isPlaying()) {
                    spinSound.stop();
                    spinSound.release(); // Release the MediaPlayer to free up resources
                }
            }
        }, 900); // Stops after 0.9 second

        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cardImageView, "rotationY", 0f, 360f);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cardImageView, "rotationY", 0f, 360f);
        final ObjectAnimator oa3 = ObjectAnimator.ofFloat(cardImageView, "rotationY", 0f, 360f);
        final ObjectAnimator oa4 = ObjectAnimator.ofFloat(cardImageView, "rotationY", 0f, 360f);

        oa1.setInterpolator(new AccelerateInterpolator());
        oa2.setInterpolator(new AccelerateInterpolator());
        oa3.setInterpolator(new AccelerateInterpolator());
        oa4.setInterpolator(new AccelerateInterpolator());

        oa1.setDuration(100); // Adjust the duration as needed
        oa2.setDuration(100); // Adjust the duration as needed
        oa3.setDuration(100); // Adjust the duration as needed
        oa4.setDuration(100); // Adjust the duration as needed

        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                oa2.start();
            }
        });

        oa2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                oa3.start();
            }
        });

        oa3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                oa4.start();
            }
        });

        oa4.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Update isCardFlipped after flip animation
                isCardFlipped = !isCardFlipped;
                if (isCardFlipped) {
                    // Set the new image after flip animation completes
                    cardImageView.setImageResource(R.drawable.backcard);
                    cardLabels.setVisibility(View.VISIBLE); // Show card labels after the card is flipped
                    cardImageView.setVisibility(View.VISIBLE); // Ensure the ImageView is visible after setting the image
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateCardLabels(); // Call the method to animate cardLabels only when the card is flipped and after a delay
                        }
                    }, 3000); // 3000 milliseconds delay
                } else {
                    // Randomly select one of the seven cards
                    int[] cardDrawables = {
                            R.drawable.fourcard,
                            R.drawable.friendcard,
                            R.drawable.kingcard,
                            R.drawable.knifecard,
                            R.drawable.lovecard,
                            R.drawable.moneycard,
                            R.drawable.targetcard
                    };
                    Random random = new Random();
                    int index = random.nextInt(cardDrawables.length);
                    int selectedCardDrawable = cardDrawables[index];

                    // Set the randomly selected card image
                    cardImageView.setImageResource(selectedCardDrawable);
                    cardLabels.setVisibility(View.INVISIBLE); // Hide card labels when the card is face down
                    cardImageView.setVisibility(View.VISIBLE); // Ensure the ImageView is visible after setting the image
                }
            }
        });

        oa1.start();
    }



    public void InstructionClicked(View view) {
        // Create an ObjectAnimator to animate the translationY property
        ObjectAnimator animator = ObjectAnimator.ofFloat(instructionImageView, "translationY", 0, -instructionImageView.getHeight());
        animator.setDuration(1400); // Set duration for the animation in milliseconds
        animator.start(); // Start the animation
        chain.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (chain != null && chain.isPlaying()) {
                    chain.pause();
                }
            }
        }, 1500);

        // Disable click listener to prevent further clicks
        instructionImageView.setOnClickListener(null);
        isInstructionsVisible = false; // Update instructions visibility status
    }

    private void zoomInAnimation() {
        cardImageView.setScaleX(0f);
        cardImageView.setScaleY(0f);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(cardImageView, "scaleX", 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(cardImageView, "scaleY", 1f);
        scaleXAnimator.setDuration(2000); // 2000 milliseconds duration for zoom in
        scaleYAnimator.setDuration(2000); // 2000 milliseconds duration for zoom in
        scaleXAnimator.setInterpolator(new DecelerateInterpolator());
        scaleYAnimator.setInterpolator(new DecelerateInterpolator());

        // Delay setting the image until after the zoom animation completes
        scaleXAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        scaleXAnimator.start();
        scaleYAnimator.start();
    }
}
