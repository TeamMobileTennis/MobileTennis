package com.crazyking.mobiletennis.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;


public class StyleBuilder {

    static FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Regular.ttf"));

    private StyleBuilder(){}

    public static Label.LabelStyle createStyle(int widthPro, Color color){
        Label.LabelStyle style;

        FreeTypeFontGenerator.FreeTypeFontParameter paramter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramter.size = Gdx.graphics.getWidth() / widthPro;
        paramter.borderWidth = 2;
        BitmapFont font = generator.generateFont(paramter);
        style = new Label.LabelStyle(font, color);

        return style;
    }

}
