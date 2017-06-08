package com.crazyking.mobiletennis.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;


public class StyleBuilder {

    static FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Regular.ttf"));

    private StyleBuilder(){}

    /**
     * Creates a "Textfont" and returns it
         * @param   size    The size of the font
     * @param   color       The color of the style
     * @return  style       The "Textfont"
     */
    public static Label.LabelStyle CreateStyle(int size, Color color, int borderSize, Color borderColor){
        Label.LabelStyle style;

        // set the parameters of the font
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.borderWidth = borderSize;
        parameter.borderColor = borderColor;

        // create the font/style from the parameters and starting font
        BitmapFont font = generator.generateFont(parameter);
        style = new Label.LabelStyle(font, color);

        return style;
    }


}
