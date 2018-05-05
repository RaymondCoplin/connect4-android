package com.rcsoft.connect4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Explode;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Rotate;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener {

    private Player player1;
    private Player player2;
    private Board board;

    private ViewGroup activityContent;
    private RelativeLayout boardParent;
    private GridLayout boardContent;

    private static final int COLS_MAX = 7;
    private static final int ROWS_MAX = 7;

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleCurrentAccount;
    private AchievementsClient achievementsClient;

    //Views
    TextView player1Counter;
    TextView player2Counter;
    Button btnAchievements;
    ImageView accountPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        getSupportActionBar().hide();

        activityContent = findViewById(R.id.activityContent);
        boardParent = findViewById(R.id.board_parent);
        boardContent = findViewById(R.id.board_content);
        player1Counter = findViewById(R.id.player1CounterText);
        player2Counter = findViewById(R.id.player2CounterText);
        btnAchievements = findViewById(R.id.btnAchievements);
        accountPhoto = findViewById(R.id.accountPhotoProfile);

        //Initialize
        Initialize();

        //Rendering Cells
        RenderBoard();
    }

    @Override
    protected void onStart() {
        super.onStart();

            Task<GoogleSignInAccount> task = googleSignInClient.silentSignIn();
            if(task.isSuccessful()){
                googleCurrentAccount = task.getResult();
                achievementsClient = Games.getAchievementsClient(this, googleCurrentAccount);
                UpdateAccount(googleCurrentAccount);
            } else {
                task.addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        try {
                            googleCurrentAccount = task.getResult(ApiException.class);
                            achievementsClient = Games.getAchievementsClient(getApplicationContext(), googleCurrentAccount);
                            UpdateAccount(googleCurrentAccount);
                        } catch(ApiException ex) {
                            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }

    private void Initialize(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Initialing Players
        player1 = new Player(1, "Player1");
        player2 = new Player(2, "Player2");

        //Initializing Board
        board = new Board
                .Builder()
                .setRows(7)
                .setCols(7)
                .setCurrentPlayer(player1)
                .build();

        player1Counter.setText("0");
        player2Counter.setText("0");
        btnAchievements.setOnClickListener(this);
        accountPhoto.setClickable(true);
        accountPhoto.setOnClickListener(this);
    }

    private void UpdateAccount(GoogleSignInAccount account){
        ImageView iv = findViewById(R.id.accountPhotoProfile);
        Picasso.get().load(account.getPhotoUrl()).into(iv);
        ((TextView)findViewById(R.id.accountDisplayName)).setText(account.getDisplayName());
    }

    private void RenderBoard(){

        board.newGame(7, 7);
        AnimateCurrentPlayer();

        for (int i = 0; i < 7; i++) {

            for (int j = 0; j < 7; j++) {

                if(i == 0) {
                    final ImageView iv = new ImageView(this);
                    iv.setTag(j);
                    iv.setImageResource(R.drawable.flecha);
                    iv.setClickable(true);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TransitionManager.beginDelayedTransition(activityContent, new Rotate());
                            v.setRotation(v.getRotation() == 360 ? 0 : 360);
                            FillCell((int)v.getTag(), iv);
                        }
                    });
                    boardContent.addView(iv,
                            new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j)));
                    continue;
                }

                // Load a bitmap from the drawable folder
                Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.cell);

                // Resize the bitmap to 140x140 (width x height)
                Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 140, 140, true);

                ImageView iv = new ImageView(this);

                iv.setTag(new Cell(true, i, j, null));
                iv.setImageBitmap(bMapScaled);

                GridLayout.Spec row = GridLayout.spec(i);
                GridLayout.Spec col = GridLayout.spec(j);
                boardContent.addView(iv, new GridLayout.LayoutParams(row, col));
            }
        }
    }

    private void FillCell(int col, View v){

        Pair<Boolean, Integer> result = verifyAvailability(col);
        if(!result.first) {
            Toast.makeText(this, "Todas las columnas están ocupadas", Toast.LENGTH_SHORT).show();
            return;
        }

        int row = result.second;
        int index = getIndex(result.second, col);

        // Load a bitmap from the drawable folder
        Bitmap bMap = BitmapFactory.decodeResource(getResources(), (board.getCurrentPlayer().getId() == 1 ? R.drawable.cell_green : R.drawable.cell_red));

        // Resize the bitmap to 140x140 (width x height)
        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 140, 140, true);

        ImageView iv = new ImageView(this);
        iv.setImageBitmap(bMapScaled);
        Cell cell = new Cell(false, row, col, board.getCurrentPlayer());

        iv.setTag(cell);
        board.fillCell(cell);

        TransitionManager.beginDelayedTransition(boardContent, new Fade().setDuration(1000));

        boardContent.removeViewAt(index);
        boardContent.addView(iv, index);

        //Checking if there are four connected
        if(VerifyBoard(row, col)){
            AnimateNewGame(v);
            return;
        }

        board.setCurrentPlayer(board.getCurrentPlayer().getId() == 1 ? player2 : player1);
        AnimateCurrentPlayer();
    }

    private boolean VerifyBoard(int row, int col){
        boolean isConnectedFour = board.verifyBoard(row, col);
        if(isConnectedFour){
            Toast.makeText(this, "El jugador: " + board.getCurrentPlayer().getName() + " ha ganado la partida.", Toast.LENGTH_LONG).show();
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(2000);
            if(board.getCurrentPlayer().getId() == 1) {
                int count = Integer.parseInt(player1Counter.getText().toString());
                count += 1;
                player1Counter.setText(String.valueOf(count));
                if(count == 1) {
                    achievementsClient.unlock(getResources().getString(R.string.achievement_logro_1));
                }
                if(count == 4) {
                    achievementsClient.unlock(getResources().getString(R.string.achievement_logro_2));
                }
            } else {
                String count = String.valueOf(Integer.parseInt(player2Counter.getText().toString())+ 1);
                player2Counter.setText(count);
            }
            return true;
        }
        return false;
    }

    private int getIndex(int rowIndex, int columnIndex){
        return (COLS_MAX * rowIndex) + columnIndex;
    }

    private Pair<Boolean, Integer> verifyAvailability(int col){
        for (int row = ROWS_MAX-1; row > 0; row--) {
            View v = boardContent.getChildAt(getIndex(row, col));
            if (((Cell)v.getTag()).isFree()){
                return new Pair(true, row);
            }
        }
        return new Pair(false, null);
    }

    //Animations
    private void AnimateCurrentPlayer(){
        //Animate Current Player
        ViewGroup counterLayout = findViewById(R.id.counterLayout);
        ImageView player1 = findViewById(R.id.player1);
        ImageView player2 = findViewById(R.id.player2);

        TransitionManager.beginDelayedTransition(counterLayout, new ChangeBounds());

        if(board.getCurrentPlayer().getId() == 1) {
            ViewGroup.LayoutParams paramsPlayer1 = player1.getLayoutParams();
            ViewGroup.LayoutParams paramsPlayer2 = player2.getLayoutParams();

            paramsPlayer1.width = 200;
            paramsPlayer1.height = 200;
            paramsPlayer2.width = 140;
            paramsPlayer2.height = 140;
            player1.setLayoutParams(paramsPlayer1);
            player2.setLayoutParams(paramsPlayer2);
        } else {
            ViewGroup.LayoutParams paramsPlayer1 = player1.getLayoutParams();
            ViewGroup.LayoutParams paramsPlayer2 = player2.getLayoutParams();
            paramsPlayer1.width = 140;
            paramsPlayer1.height = 140;
            paramsPlayer2.width = 200;
            paramsPlayer2.height = 200;
            player1.setLayoutParams(paramsPlayer1);
            player2.setLayoutParams(paramsPlayer2);
        }
    }

    private void AnimateNewGame(View v){

        final Rect viewRect = new Rect();
        v.getGlobalVisibleRect(viewRect);

        Transition explode = new Explode().setEpicenterCallback(new Transition.EpicenterCallback() {
            @Override
            public Rect onGetEpicenter(Transition transition) {
                return viewRect;
            }
        });
        explode.setDuration(1000);
        TransitionManager.beginDelayedTransition(boardParent, explode);
        boardContent.removeAllViewsInLayout();
        RenderBoard();
    }

    //Google Play Games Services
    private void SeeAchievements() {
        achievementsClient
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, 2);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnAchievements){
            SeeAchievements();
        } else if(v.getId() == R.id.accountPhotoProfile) {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 9001);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 9001){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult();
            googleCurrentAccount = account;
            UpdateAccount(account);
        }
    }
}