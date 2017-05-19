package com.crazyking.mobiletennis.game.managers;

import android.app.Activity;
import android.util.Log;

import com.crazyking.mobiletennis.AndroidLauncher;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.screens.AbstractScreen;
import com.crazyking.mobiletennis.game.screens.JoinLobbyScreen;

import static com.crazyking.mobiletennis.connection.Constants.CMD.CLOSE;
import static com.crazyking.mobiletennis.connection.Constants.CMD.CONN;
import static com.crazyking.mobiletennis.connection.Constants.CMD.END;
import static com.crazyking.mobiletennis.connection.Constants.CMD.PAUSE;
import static com.crazyking.mobiletennis.connection.Constants.CMD.RESP;
import static com.crazyking.mobiletennis.connection.Constants.CMD.START;
import static com.crazyking.mobiletennis.connection.Constants.CODE;
import static com.crazyking.mobiletennis.connection.Constants.NAME;

public class MessageHandler {

    Activity activity;
    MobileTennis mt;

    AbstractScreen activeScreen;

    public MessageHandler(MobileTennis mt, Activity activity){
        this.activity = activity;
        this.mt = mt;

        // we are the current message handler
        // and say this to the activity
        ((AndroidLauncher)activity).SetMessagehandler(this);
    }

    public void HandleReceivedMessage(final String message){
        Log.d("Message Empfangen", "Empfangen");

        activeScreen.GetMessage(message);

    }

    public void SetActiveScreen(AbstractScreen screen){
        this.activeScreen = screen;
    }

}
