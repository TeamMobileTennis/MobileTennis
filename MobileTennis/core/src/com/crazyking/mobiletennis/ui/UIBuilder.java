package com.crazyking.mobiletennis.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;



public class UIBuilder {

    private UIBuilder(){}

    public static Label createLabel(String text, Skin skin, String type, Label.LabelStyle style, float width, float height, float posYProc){
        Label label = new Label(text, skin, type);
        label.setAlignment(Align.center);
        label.setStyle(style);
        label.setSize(width, height);
        label.setPosition(Gdx.graphics.getWidth() / 2 - width / 2, Gdx.graphics.getHeight() * posYProc);

        return label;
    }


    public static TextButton createButton(String text, Skin skin, String type, Label.LabelStyle style, float width, float height, float posYProc){
        TextButton button = new TextButton(text, skin, type);
        button.getLabel().setStyle(style);
        button.setSize(width, height);
        button.setPosition(Gdx.graphics.getWidth() / 2 - width / 2, Gdx.graphics.getHeight() * posYProc);

        return button;
    }
}
