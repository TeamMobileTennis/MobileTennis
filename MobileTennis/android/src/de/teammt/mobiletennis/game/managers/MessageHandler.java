package de.teammt.mobiletennis.game.managers;

import android.app.Activity;
import android.util.Log;

import de.teammt.mobiletennis.game.MobileTennis;
import de.teammt.mobiletennis.game.screens.AbstractScreen;

public class MessageHandler {

    Activity activity;
    MobileTennis mt;

    AbstractScreen activeScreen;

    public MessageHandler(MobileTennis mt, Activity activity){
        this.activity = activity;
        this.mt = mt;

        // we are the current message handler
        // and say this to the activity
        mt.setMessageHandler(this);
    }

    public void HandleReceivedMessage(final String message){
        Log.d("Message Empfangen", "Empfangen");
        Log.d("String", message);

        activeScreen.getMessage(message);

    }

    public void SetActiveScreen(AbstractScreen screen){
        this.activeScreen = screen;
    }

}
