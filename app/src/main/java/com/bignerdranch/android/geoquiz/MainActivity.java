package com.bignerdranch.android.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String CHEAT_INDEX = "cheat";
    private static final String CHEAT_ARRAY = "array";
    private static final int REQUEST_CODE_CHEAT = 0;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private int tanswer=0,fanswer=0;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia,true),
            new Question(R.string.question_oceans,true),
            new Question(R.string.question_mideast,false),
            new Question(R.string.question_africa,false),
            new Question(R.string.question_americas,true),
            new Question(R.string.question_asia,true)
    };
    private ArrayList<Integer> mCheatArray = new ArrayList<Integer>();
    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called");
        setContentView(R.layout.activity_main);
        for(int i=0;i<6;i++)
        {
            mCheatArray.add(i,0);
        }
        if(savedInstanceState != null)
        {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            int ischeat = savedInstanceState.getInt(CHEAT_INDEX,0);
            ArrayList<Integer> ifcheat = savedInstanceState.getIntegerArrayList(CHEAT_ARRAY);
            for(int i=0;i<ifcheat.size();i++)
            {
                mCheatArray.add(i,ifcheat.get(i));
            }
            if(ischeat == 1)
            {
                mIsCheater = true;

            }
            else
            {
                mIsCheater = false;
            }
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex =(mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
                releasebutton();

            }
        });


        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
                banButton(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
                banButton(false);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex =(mCurrentIndex + 1) % mQuestionBank.length;
                if(mCheatArray.get(mCurrentIndex)==1)mIsCheater=true;
                else mIsCheater=false;
                updateQuestion();
                releasebutton();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + mQuestionBank.length - 1) % mQuestionBank.length;
                updateQuestion();
                releasebutton();
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerisTrue=mQuestionBank[mCurrentIndex].isAnswerTure();
                Intent intent=CheatActivity.newIntent(MainActivity.this,answerisTrue);
                startActivityForResult(intent,REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
    }



    @Override
    protected void onActivityResult(int requesCode,int resultCode,Intent data)
    {
        super.onActivityResult(requesCode,resultCode,data);
        if(resultCode!= Activity.RESULT_OK)
        {
            return;
        }
        if(requesCode==REQUEST_CODE_CHEAT)
        {
            if(data==null)
            {
                return;

            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            if(mIsCheater)Toast.makeText(this,R.string.judgment_toast,Toast.LENGTH_SHORT).show();
            if(mIsCheater)mCheatArray.add(mCurrentIndex,1);
            else mCheatArray.add(mCurrentIndex,0);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG,"onStart() called");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG,"onResume() called");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG,"onPause() called");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG,"onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);
        savedInstanceState.putIntegerArrayList(CHEAT_ARRAY,mCheatArray);
        if(mIsCheater)
        {
            savedInstanceState.putInt(CHEAT_INDEX,1);
        }
        else
        {
            savedInstanceState.putInt(CHEAT_INDEX,0);
        }

    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG,"onStop() called");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG,"onDestroy() called");
    }

    private void updateQuestion()
    {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue)
    {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTure();

        int messageResId = 0;

        if(mIsCheater)
        {
            messageResId = R.string.judgment_toast;
        }
        else if(userPressedTrue == answerIsTrue)
        {
            messageResId = R.string.correct_toast;
            tanswer++;
        }
        else
        {
            messageResId = R.string.incorrect_toast;
            fanswer++;
        }

        Toast.makeText(this,messageResId,Toast.LENGTH_SHORT).show();

        if(mCurrentIndex == mQuestionBank.length-1)//全部问题回答完后，显示分数
        {
            //全答对多加4分
            Toast.makeText(this,""+(tanswer*16+(tanswer/mQuestionBank.length)*4),Toast.LENGTH_SHORT).show();
        }
    }

    private void banButton(boolean userchoice)//锁定按钮不能点击
    {
        if(userchoice)
        {
            mFalseButton.setEnabled(false);
        }
        else
        {
            mTrueButton.setEnabled(false);
        }
    }

    private void releasebutton()//解除锁定
    {
        mTrueButton.setEnabled(true);
        mFalseButton.setEnabled(true);
    }
}
